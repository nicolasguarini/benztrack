package it.pdm.benztrack

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("it.pdm.benztrack", Context.MODE_PRIVATE)
        if(sharedPref.getBoolean("firstStart", true)){
            setupAlarmManager()
            startActivity(Intent(this, WelcomeActivity::class.java))
        }else{
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        finish()
    }

    private fun setupAlarmManager(){
        val calendar = Calendar.getInstance()

        val intent = Intent(applicationContext, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            100,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 15, //send notification every 15 days
            pendingIntent
        )
    }
}