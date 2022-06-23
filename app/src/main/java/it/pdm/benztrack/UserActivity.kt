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
        val expenses: List<Expense> = listOf(
            Expense(0, "Filtro aria", "MAINTENANCE", "03/05/2022", 60.0, null, null, null, selectedCarId),
            Expense(0, "Rifornimento", "REFUEL", "05/05/2022", 20.0, null, 2.0, 1000, selectedCarId),
            Expense(0, "Rifornimento", "REFUEL", "13/05/2022", 20.0, null, 2.0, 1150, selectedCarId),
            Expense(0, "Bollo 2022", "TAX", "20/05/2022", 50.0, "Rata mensile", null, null, selectedCarId),
            Expense(0, "Pastiglie freni", "MAINTENANCE", "25/05/2022", 73.0, "43€ pastiglie + 30€ manodopera", null, null, selectedCarId),
            Expense(0, "Rifornimento", "REFUEL", "29/05/2022", 50.0, null, 2.0, 1290, selectedCarId),
            Expense(0, "Cambio olio", "MAINTENANCE", "02/06/2022", 25.0, "Cambio olio presso Norauto Varese", null, null, selectedCarId),
            Expense(0, "Rifornimento", "REFUEL", "05/06/2022", 30.0, null, 2.0, 1640, selectedCarId),
            Expense(0, "Rifornimento", "REFUEL", "14/06/2022", 20.0, null, 2.0, 1820, selectedCarId),
            Expense(0, "Assicurazione 2022", "INSURANCE", "18/06/2022", 130.0, "Seconda rata semestrale", null, null, selectedCarId),
            Expense(0, "Bollo 2022", "TAX", "20/06/2022", 50.0, "Rata mensile", null, null, selectedCarId),
            Expense(0, "Rifornimento", "REFUEL", "21/06/2022", 20.0, null, 2.0, 2050, selectedCarId)
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