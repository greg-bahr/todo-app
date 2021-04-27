package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
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
    private lateinit var recyclerView: RecyclerView
    private lateinit var todoAdapter: TodoRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        TodoSingleton.todos = arrayListOf(
            Todo("this is a test todo", null),
            Todo("this is another test todo", Date())
        )
        todos = TodoSingleton.todos!!

        val fab: FloatingActionButton = findViewById(R.id.add_todo)
        fab.setOnClickListener {
            Toast.makeText(this@MainActivity, "Add todo", Toast.LENGTH_SHORT).show()
        }

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

    private fun editTodo(todo: Todo) {
        val intent = Intent(this, AddOrChangeTodoActivity::class.java)
        intent.putExtra("text", todo.text)
        todo.date?.let { intent.putExtra("date", it.time) }
    }

    private fun finishTodo(todo: Todo) {
        TodoSingleton.deleteTodo(todo)
        todoAdapter.notifyDataSetChanged()
        Snackbar.make(this, recyclerView, "Completed todo.", Snackbar.LENGTH_LONG)
            .setAction("Undo") { undoFinishedTodo(todo) }
            .show()
    }

    private fun undoFinishedTodo(todo: Todo) {
        TodoSingleton.addTodo(todo)
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
                val filteredTodos =
                    todos.filter { it.text.contains(searchText, true) } as ArrayList<Todo>
                todoAdapter.updateTodos(filteredTodos)
                return true
            }
        })

        searchView.setOnCloseListener {
            todoAdapter.updateTodos(todos)
            true
        }

        return super.onCreateOptionsMenu(menu)
    }
}