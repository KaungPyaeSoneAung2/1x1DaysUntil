<img src="https://github.com/user-attachments/assets/f0eb1960-67ef-4419-9aef-02c8d72db647" alt="sample app image" width="600">

# Simple 1x1 Widget that Counts Remaining Days

This guide will help you create a minimal 1x1 Android widget that displays the number of days remaining until a specific date. The widget will have a semi-transparent background with rounded corners for a sleek, modern look.

## 1. Project Setup
1. Create a new Android project in Android Studio with an empty activity.
2. Name your project as desired.

## 2. Create the Widget Layout

1. In `res/layout`, create a new XML file named `day_counter.xml`.
2. Add the following code to define the layout for the widget:

    ```xml
    <LinearLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:gravity="center"
        android:padding="4dp"
        android:background="@drawable/widget_background"> <!-- Apply custom background -->

        <TextView
            android:id="@+id/tvWidgetDaysRemaining"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="0"
            android:textSize="16sp"
            android:textColor="@android:color/black"
            android:gravity="center"/>
    </LinearLayout>
    ```

## 3. Create the Widget Background

1. In `res/drawable`, create a new XML file named `widget_background.xml`.
2. Add the following code to define a semi-transparent background with rounded corners:

    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <shape xmlns:android="http://schemas.android.com/apk/res/android">
        <solid android:color="#80FFFFFF"/> <!-- Semi-transparent white -->
        <corners android:radius="10dp"/> <!-- Rounded corners -->
    </shape>
    ```

## 4. Create the Widget Provider

1. Create a new class `DayCounter` that extends `AppWidgetProvider`:

    ```kotlin
    import android.app.PendingIntent
    import android.appwidget.AppWidgetManager
    import android.appwidget.AppWidgetProvider
    import android.content.Context
    import android.content.Intent
    import android.widget.RemoteViews
    import java.util.*

    class DayCounter : AppWidgetProvider() {

        override fun onUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
        ) {
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }

        companion object {
            internal fun updateAppWidget(
                context: Context,
                appWidgetManager: AppWidgetManager,
                appWidgetId: Int
            ) {
                val sharedPreferences = context.getSharedPreferences("CountdownApp", Context.MODE_PRIVATE)
                val savedDateInMillis = sharedPreferences.getLong("selectedDate", 0L)

                val selectedDate = Calendar.getInstance()
                selectedDate.timeInMillis = savedDateInMillis

                val today = Calendar.getInstance()
                val diffInMillis = selectedDate.timeInMillis - today.timeInMillis
                val daysRemaining = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()

                val views = RemoteViews(context.packageName, R.layout.day_counter)
                views.setTextViewText(R.id.tvWidgetDaysRemaining, "Days: $daysRemaining")

                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                views.setOnClickPendingIntent(R.id.tvWidgetDaysRemaining, pendingIntent)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
    ```

## 5. Define the Widget in the Manifest

1. In `AndroidManifest.xml`, declare the widget:

    ```xml
    <receiver android:name=".DayCounter">
        <intent-filter>
            <action android:name="android.appwidget.action.APPWIDGET_UPDATE"/>
        </intent-filter>

        <meta-data
            android:name="android.appwidget.provider"
            android:resource="@xml/countdown_widget_info"/>
    </receiver>
    ```

## 6. Configure the Widget Provider

1. Create an XML file `countdown_widget_info.xml` in `res/xml`:

    ```xml
    <appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
        android:minWidth="40dp"
        android:minHeight="40dp"
        android:updatePeriodMillis="86400000"  <!-- Updates every 24 hours -->
        android:initialLayout="@layout/day_counter"
        android:resizeMode="none"
        android:widgetCategory="home_screen" />
    ```

## 7. Update the Widget When Date is Set

1. In your `MainActivity`, save the selected date to `SharedPreferences` and update the widget:

    ```kotlin
    import android.appwidget.AppWidgetManager
    import android.content.ComponentName
    import android.content.Context
    import android.content.Intent
    import android.content.SharedPreferences
    import android.os.Bundle
    import android.widget.Button
    import android.widget.DatePicker
    import android.widget.TextView
    import androidx.appcompat.app.AppCompatActivity
    import java.util.*

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

                val today = Calendar.getInstance()
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
    ```

## 8. Run and Test

- Build and run your app.
- Add the widget to your home screen.
- Set a date in the app, and the widget will show the number of days remaining.

---

This minimal widget is now ready, and it will display the remaining days until the set date on your home screen in a 1x1 space.
