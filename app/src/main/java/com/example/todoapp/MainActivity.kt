package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var todos: ArrayList<Todo>
    private var filteredTodos: ArrayList<Todo> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var todoAdapter: TodoRecyclerViewAdapter
    private lateinit var progressSpinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val fab: FloatingActionButton = findViewById(R.id.add_todo)
        fab.setOnClickListener { addTodo() }

        progressSpinner = findViewById(R.id.progress_spinner)
        recyclerView = findViewById(R.id.todo_rv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        if (TodoSingleton.todos == null) {
            loadTodos()
        } else {
            initAdapter()
        }
    }

    private fun initAdapter() {
        todos = TodoSingleton.todos!!

        todoAdapter = TodoRecyclerViewAdapter(
            todos,
            { todo -> finishTodo(todo) },
            { todo -> editTodo(todo) }
        )
        recyclerView.adapter = todoAdapter
    }

    private fun loadTodos() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = Firebase.firestore

        progressSpinner.visibility = View.VISIBLE
        db.collection("todos").whereEqualTo("user_id", user.uid).get()
            .addOnSuccessListener { querySnapshot ->
                TodoSingleton.todos =
                    ArrayList(querySnapshot.documents.map { it.toObject<Todo>()!! })

                initAdapter()
                progressSpinner.visibility = View.GONE
            }
            .addOnFailureListener {
                Log.e(TAG, "Error getting todos: ", it)
            }
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