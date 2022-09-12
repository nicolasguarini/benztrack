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
import it.pdm.benztrack.data.Expense
import it.pdm.benztrack.data.ExpenseDao
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class UserActivity : AppCompatActivity() {
    private lateinit var etUserName: EditText
    private lateinit var btnSaveChanges: Button
    private lateinit var btnSendNotification: Button
    private lateinit var btnClearExpenses: Button
    private lateinit var btnGenerateExpenses: Button
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

        btnGenerateExpenses = findViewById(R.id.btnGenerateExpenses)
        btnGenerateExpenses.setOnClickListener { generateExpenses() }

        btnClearExpenses = findViewById(R.id.btnClearExpenses)
        btnClearExpenses.setOnClickListener { clearExpenses() }
    }

    private fun generateExpenses() {
        val selectedCarId = sharedPreferences.getLong("selectedCarId", -1L)
        val calendar = Calendar.getInstance()
        val currentMonth = SimpleDateFormat("MM", Locale.getDefault()).format(calendar.time)
        val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(calendar.time)
        calendar.add(Calendar.MONTH, -1)
        val previousMonth = SimpleDateFormat("MM", Locale.getDefault()).format(calendar.time)
        val previousYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(calendar.time)

        val expenses: List<Expense> = listOf(
            Expense(0, getString(R.string.air_filter), "MAINTENANCE", "03/$previousMonth/$previousYear", 60.0, null, null, null, selectedCarId),
            Expense(0, getString(R.string.refuel_title), "REFUEL", "05/$previousMonth/$previousYear", 20.0, null, 2.0, 1000, selectedCarId),
            Expense(0, getString(R.string.refuel_title), "REFUEL", "13/$previousMonth/$previousYear", 20.0, null, 2.0, 1150, selectedCarId),
            Expense(0, getString(R.string.tax_title)+ " $previousYear", "TAX", "20/$previousMonth/$previousYear", 50.0, getString(R.string.example_tax_description1), null, null, selectedCarId),
            Expense(0, getString(R.string.brake_pads), "MAINTENANCE", "25/$previousMonth/$previousYear", 73.0, getString(R.string.example_maint_description1), null, null, selectedCarId),
            Expense(0, getString(R.string.refuel_title), "REFUEL", "29/$previousMonth/$previousYear", 50.0, null, 2.0, 1290, selectedCarId),
            Expense(0, getString(R.string.oil_change), "MAINTENANCE", "02/$currentMonth/$currentYear", 25.0, getString(R.string.example_maint_description2), null, null, selectedCarId),
            Expense(0, getString(R.string.refuel_title), "REFUEL", "05/$currentMonth/$currentYear", 30.0, null, 2.0, 1640, selectedCarId),
            Expense(0, getString(R.string.refuel_title), "REFUEL", "14/$currentMonth/$currentYear", 20.0, null, 2.0, 1820, selectedCarId),
            Expense(0, getString(R.string.insurance_title)+ " $currentYear", "INSURANCE", "18/$currentMonth/$currentYear", 130.0, getString(R.string.example_ins_description1), null, null, selectedCarId),
            Expense(0, getString(R.string.tax_title)+ " $currentYear", "TAX", "20/$currentMonth/$currentYear", 50.0, getString(R.string.example_tax_description1), null, null, selectedCarId),
            Expense(0, getString(R.string.refuel_title), "REFUEL", "21/$currentMonth/$currentYear", 20.0, null, 2.0, 2050, selectedCarId)
        )

        val service = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        service.execute {
            expenseDao.deleteCarExpenses(selectedCarId)
            expenseDao.insertExpenses(expenses)
            handler.post {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }
        }
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
            PendingIntent.FLAG_IMMUTABLE
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