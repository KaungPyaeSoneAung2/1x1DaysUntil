package com.example.daysuntil

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("CountdownApp", Context.MODE_PRIVATE)

        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val btnSetDate = findViewById<Button>(R.id.btnSetDate)
        val tvDaysRemaining = findViewById<TextView>(R.id.tvDaysRemaining)

        btnSetDate.setOnClickListener {
            val selectedDate = Calendar.getInstance()
            selectedDate.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            selectedDate.set(Calendar.HOUR_OF_DAY, 0)
            selectedDate.set(Calendar.MINUTE, 0)
            selectedDate.set(Calendar.SECOND, 0)
            selectedDate.set(Calendar.MILLISECOND, 0)

            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)

            val diffInMillis = selectedDate.timeInMillis - today.timeInMillis
            val daysRemaining = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()

            tvDaysRemaining.text = "Days Remaining: $daysRemaining"

            sharedPreferences.edit().putLong("selectedDate", selectedDate.timeInMillis).apply()

            updateWidget()
        }
    }

    private fun updateWidget() {
        val intent = Intent(this, DayCounter::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(
            ComponentName(application, DayCounter::class.java)
        )
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }
}