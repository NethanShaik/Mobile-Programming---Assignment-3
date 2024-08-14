package com.example.assignment_3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager


class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_ADD_TASK = 1
    }

    private lateinit var taskList: MutableList<Task>
    private lateinit var customAdapter: customAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.task_list)
        val add_new_task: Button = findViewById(R.id.add_task)

        taskList = mutableListOf()
        // Initialize the adapter with the task list
        customAdapter = customAdapter(taskList)
        // Set up the RecyclerView
        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        add_new_task.setOnClickListener{
            val new_task = Intent(this, AddTask::class.java)
            startActivityForResult(new_task, REQUEST_CODE_ADD_TASK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_ADD_TASK && resultCode == RESULT_OK) {
            data?.let {
                val taskName = it.getStringExtra("TASK") ?: ""
                val taskDeadline = it.getStringExtra("DEADLINE") ?: ""
                val newTask = Task(taskName, taskDeadline)
                taskList.add(newTask)
                customAdapter.notifyDataSetChanged()
            }
        }
    }


}