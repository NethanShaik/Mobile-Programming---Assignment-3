package com.example.assignment_3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_ADD_TASK = 1
        private const val CHANNEL_ID = "task_notifications_channel"
        private const val NOTIFICATION_REQUEST_CODE = 1
    }

    private lateinit var taskList: MutableList<Task>
    private lateinit var customAdapter: customAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create Notification Channel
        createNotificationChannel()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val recyclerView: RecyclerView = findViewById(R.id.task_list)
        val addNewTask: Button = findViewById(R.id.add_task)

        taskList = mutableListOf()
        // Initialize the adapter with the task list
        customAdapter = customAdapter(taskList)
        // Set up the RecyclerView
        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        addNewTask.setOnClickListener {
            val newTaskIntent = Intent(this, AddTask::class.java)
            startActivityForResult(newTaskIntent, REQUEST_CODE_ADD_TASK)
        }

        // Check for notification permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_option -> {
                clearTaskList()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clearTaskList() {
        taskList.clear()
        Toast.makeText(this, "Task list is empty", Toast.LENGTH_SHORT).show()
        customAdapter.notifyDataSetChanged()
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

                // Show notification if permission granted
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    showNotification(taskName, taskDeadline)
                } else {
                    Toast.makeText(this, "Notification permission is not granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task Notifications"
            val descriptionText = "Notifications for new tasks"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(taskName: String, taskDeadline: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Check notification permission before creating a notification
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Notification permission is not granted", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Replace with your notification icon
                .setContentTitle("New Task Added")
                .setContentText("Task: $taskName\nDeadline: $taskDeadline")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(this)) {
                notify(NOTIFICATION_REQUEST_CODE, builder.build())
            }
        } catch (e: SecurityException) {
            // Handle potential SecurityException
            Toast.makeText(this, "Error showing notification: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can show notifications
            } else {
                Toast.makeText(this, "Notification permission is required to show notifications", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
