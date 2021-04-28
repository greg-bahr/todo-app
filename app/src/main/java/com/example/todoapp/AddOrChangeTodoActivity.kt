package com.example.todoapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.text.SimpleDateFormat
import java.util.*

class AddOrChangeTodoActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var dueDate: Date
    private lateinit var todoDate: EditText
    private lateinit var todoText: EditText
    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_or_change_todo)

        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        todoText = findViewById(R.id.edit_todo_text)
        todoDate = findViewById(R.id.edit_todo_date)
        todoDate.setOnClickListener {
            DatePickerFragment().show(supportFragmentManager, "datePicker")
        }

        val todoIndex = intent.getIntExtra("index", -1)
        if (todoIndex > -1) {
            isEditing = true
            val todo = TodoSingleton.todos?.get(todoIndex)

            todoText.setText(todo?.text)
            todo?.date?.let {
                todoDate.setText(SimpleDateFormat("MM/dd/yyyy", Locale.US).format(it))
            }

            toolbar.title = "Edit Todo"
        }

        val submitButton: Button = findViewById(R.id.submit_todo)
        submitButton.setOnClickListener {
            if (isEditing) {
                updateTodo()
            } else {
                submit()
            }
        }
    }

    private fun updateTodo() {
        if (todoText.length() > 0) {
            val todoIndex = intent.getIntExtra("index", -1)
            TodoSingleton.updateTodo(todoIndex, todoText.text.toString(), dueDate)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun submit() {
        if (todoText.length() > 0) {
            val todo = Todo(todoText.text.toString(), dueDate)
            TodoSingleton.addTodo(todo)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }

        dueDate = calendar.time
        todoDate.setText(SimpleDateFormat("MM/dd/yyyy", Locale.US).format(dueDate))
    }
}