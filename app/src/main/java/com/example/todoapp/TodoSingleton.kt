package com.example.todoapp

object TodoSingleton {
    var todos: ArrayList<Todo>? = null

    fun deleteTodo(todo: Todo) {
        todos?.remove(todo)
    }

    fun addTodo(todo: Todo) {
        todos?.add(todo)
    }
}