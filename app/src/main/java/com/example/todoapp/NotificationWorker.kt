package com.example.todoapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

private const val CHANNEL_ID = "TODO_APP_NOTIFICATIONS"

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val task = getUnfinishedTodoCount() ?: return Result.failure()
        Tasks.await(task)

        return Result.success()
    }

    private fun sendNotification(unfinishedTodos: Int) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val text = if (unfinishedTodos == 0) {
            "You've finished all your todos today!"
        } else {
            "You have $unfinishedTodos todos to complete today!"
        }

        createNotificationChannel()
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.outline_assignment_24)
            .setContentTitle("Todo App")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(123, builder.build())
        }
    }

    private fun getUnfinishedTodoCount(): Task<QuerySnapshot>? {
        val db = Firebase.firestore
        val user = FirebaseAuth.getInstance().currentUser ?: return null

        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        val task = db.collection("todos").whereEqualTo("user_id", user.uid)
            .whereEqualTo("date", today.time).get()
        task.addOnSuccessListener { sendNotification(it.documents.size) }

        return task
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Todo App Notifications"
            val descriptionText = "Notifications for Todo App"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager? =
                getSystemService(applicationContext, NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}