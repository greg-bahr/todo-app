package com.example.todoapp

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

object TodoSingleton {
    var todos: ArrayList<Todo>? = null
        set(value) {
            field = value; sortTodos()
        }

    fun deleteTodo(todo: Todo) {
        val db = Firebase.firestore
        db.collection("todos").document(todo.id).delete()

        todos?.remove(todo)
        sortTodos()
    }

    fun addTodo(todo: Todo) {
        val db = Firebase.firestore
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val todoHashMap = hashMapOf(
            "user_id" to user.uid,
            "text" to todo.text,
            "date" to todo.date
        )
        db.collection("todos").add(todoHashMap)
            .addOnSuccessListener {
                todo.id = it.id
            }
            .addOnFailureListener { e -> Log.e("TodoSingleton", "Failed to add: ", e) }

        todos?.add(todo)
        sortTodos()
    }

    fun updateTodo(todoIndex: Int, text: String, dueDate: Date?) {
        val todo = todos?.get(todoIndex)
        todo?.text = text
        todo?.date = dueDate

        val db = Firebase.firestore
        todo?.id?.let { db.collection("todos").document(it).update("text", text, "date", dueDate) }
        sortTodos()
    }

    private fun sortTodos() {
        todos?.sortWith(kotlin.Comparator { first, second ->
            when {
                first.date == null -> {
                    return@Comparator -1
                }
                second.date == null -> {
                    return@Comparator 1
                }
                else -> {
                    return@Comparator first.date!!.compareTo(second.date)
                }
            }
        })
    }
}