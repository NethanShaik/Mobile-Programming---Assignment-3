package com.example.assignment_3

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class AddTask : AppCompatActivity() {

    private val calendar = Calendar.getInstance()
    private lateinit var deadline: EditText
    private lateinit var new_task: EditText

    private fun showDatePickerLog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener{ view, year, month, date ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DATE, date)
            updateDateInView()
        }
        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateInView() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        deadline.setText(sdf.format(calendar.time))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_task)

        new_task = findViewById(R.id.add_new_task)
        deadline = findViewById(R.id.deadline)
        var create_task: Button = findViewById(R.id.create_task)

        deadline.setOnClickListener{
            showDatePickerLog()
        }

        create_task.setOnClickListener{
            val taskName = new_task.text.toString()
            val taskDeadline = deadline.text.toString()

            if(taskName.isNotEmpty() && taskDeadline.isNotEmpty()){
                val resultIntent = Intent().apply{
                    putExtra("TASK", taskName)
                    putExtra("DEADLINE", taskDeadline)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
            else
            {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

}