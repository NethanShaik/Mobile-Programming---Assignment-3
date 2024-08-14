package com.example.assignment_3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import org.w3c.dom.Text

data class Task(
    val name: String,
    val deadline: String
)


class customAdapter(private val taskList: List<Task>) :
    RecyclerView.Adapter<customAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.name)
        val deadline: TextView = itemView.findViewById(R.id.deadline)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.task_row_item, viewGroup, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskName.text = task.name
        holder.deadline.text = task.deadline
    }

    override fun getItemCount() = taskList.size
}

