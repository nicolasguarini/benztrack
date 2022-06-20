package it.pdm.benztrack

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import it.pdm.benztrack.data.AppDatabase
import it.pdm.benztrack.data.ExpenseDao
import java.util.*
import java.util.concurrent.Executors

class UserActivity : AppCompatActivity() {
    private lateinit var etUserName: EditText
    private lateinit var btnSaveChanges: Button
    private lateinit var btnSendNotification: Button
    private lateinit var btnClearExpenses: Button
    private lateinit var userName: String

    private lateinit var db: AppDatabase
    private lateinit var expenseDao: ExpenseDao
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        db = AppDatabase.getDatabase(applicationContext)
        expenseDao = db.expenseDao()
        sharedPreferences = applicationContext.getSharedPreferences("it.pdm.benztrack", Context.MODE_PRIVATE)
        userName = sharedPreferences.getString("userName", "").toString()

        etUserName = findViewById(R.id.etUserName)
        etUserName.hint = userName

        btnSaveChanges = findViewById(R.id.btnSaveChanges)
        btnSaveChanges.setOnClickListener { updateUsername() }

        btnSendNotification = findViewById(R.id.btnSendNotification)
        btnSendNotification.setOnClickListener { sendNotification() }

        btnClearExpenses = findViewById(R.id.btnClearExpenses)
        btnClearExpenses.setOnClickListener { clearExpenses() }
    }

    private fun clearExpenses() {
        val selectedCarId = sharedPreferences.getLong("selectedCarId", -1L)
        val service = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        service.execute {
            if(selectedCarId != -1L) expenseDao.deleteCarExpenses(selectedCarId)
            handler.post{
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }
        }
    }

    private fun sendNotification() {
        val calendar = Calendar.getInstance()
        val intent = Intent(applicationContext, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            100,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private fun updateUsername() {
        val text = etUserName.text.toString()
        if(text != userName && text != ""){
            sharedPreferences.edit().putString("userName", text).apply()
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }
}