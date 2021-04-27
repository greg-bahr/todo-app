package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.*

class TodoRecyclerViewAdapter(
    private var todos: ArrayList<Todo>,
    private val onItemClick: (todo: Todo) -> Unit,
    private val onItemLongClick: (todo: Todo) -> Unit
) : RecyclerView.Adapter<TodoRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.todo_checkbox)
        val todoText: TextView = itemView.findViewById(R.id.todo_text)
        val dateText: TextView = itemView.findViewById(R.id.date_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.todo_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo = todos[position]

        holder.todoText.text = todo.text
        if (todo.date != null) {
            holder.dateText.text =
                DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US).format(todo.date)
            holder.dateText.visibility = View.VISIBLE
        } else {
            holder.dateText.visibility = View.GONE
        }

        holder.checkBox.setOnClickListener { onItemClick(todo) }
        holder.checkBox.isChecked = false
        holder.itemView.setOnLongClickListener { onItemLongClick(todo); true }
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    fun updateTodos(todos: ArrayList<Todo>) {
        this.todos = todos
        this.notifyDataSetChanged()
    }
}