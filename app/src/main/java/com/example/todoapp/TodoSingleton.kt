package com.example.todoapp

import java.util.*

object TodoSingleton {
    var todos: ArrayList<Todo>? = null

    init {
        todos = arrayListOf(
                Todo("this is a test todo", null),
                Todo("this is another test todo", Date())
        )
    }

    fun deleteTodo(todo: Todo) {
        todos?.remove(todo)
    }

    fun addTodo(todo: Todo) {
        todos?.add(todo)
    }

    fun updateTodo(todoIndex: Int, text: String, dueDate: Date?) {
        val todo = todos?.get(todoIndex)
        todo?.text = text
        todo?.date = dueDate
    }
}