package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var todos: ArrayList<Todo>
    private lateinit var filteredTodos: ArrayList<Todo>
    private lateinit var recyclerView: RecyclerView
    private lateinit var todoAdapter: TodoRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        todos = TodoSingleton.todos!!

        val fab: FloatingActionButton = findViewById(R.id.add_todo)
        fab.setOnClickListener { addTodo() }

        recyclerView = findViewById(R.id.todo_rv)
        todoAdapter = TodoRecyclerViewAdapter(
                todos,
                { todo -> finishTodo(todo) },
                { todo -> editTodo(todo) }
        )
        recyclerView.adapter = todoAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun addTodo() {
        val intent = Intent(this, AddOrChangeTodoActivity::class.java)
        startActivity(intent)
    }

    private fun editTodo(todo: Todo) {
        val intent = Intent(this, AddOrChangeTodoActivity::class.java)
        intent.putExtra("index", TodoSingleton.todos?.indexOf(todo))
        startActivity(intent)
    }

    private fun finishTodo(todo: Todo) {
        val inFilteredTodos = filteredTodos.contains(todo)
        TodoSingleton.deleteTodo(todo)
        filteredTodos.remove(todo)

        todoAdapter.notifyDataSetChanged()
        Snackbar.make(this, recyclerView, "Completed todo.", Snackbar.LENGTH_LONG)
            .setAction("Undo") { undoFinishedTodo(todo, inFilteredTodos) }
            .show()
    }

    private fun undoFinishedTodo(todo: Todo, inFilteredTodos: Boolean) {
        TodoSingleton.addTodo(todo)
        if (inFilteredTodos) {
            filteredTodos.add(todo)
        }
        todoAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.todo_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val searchText = newText ?: ""
                filteredTodos =
                    todos.filter { it.text.contains(searchText, true) } as ArrayList<Todo>
                todoAdapter.updateTodos(filteredTodos)
                return true
            }
        })

        searchView.setOnCloseListener {
            filteredTodos = todos
            todoAdapter.updateTodos(todos)
            true
        }

        return super.onCreateOptionsMenu(menu)
    }
}