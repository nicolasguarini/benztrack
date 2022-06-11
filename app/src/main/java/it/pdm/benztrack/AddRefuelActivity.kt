package it.pdm.benztrack

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import it.pdm.benztrack.data.AppDatabase
import it.pdm.benztrack.data.Expense
import it.pdm.benztrack.data.ExpenseDao
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class AddRefuelActivity : AppCompatActivity() {
    private lateinit var etDate: EditText
    private lateinit var etTotalSpent: EditText
    private lateinit var etPricePerLiter: EditText
    private lateinit var etTotalKm: EditText
    private lateinit var db: AppDatabase
    private lateinit var expenseDao: ExpenseDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_refuel)

        etDate = findViewById(R.id.etRefDate)
        etTotalSpent = findViewById(R.id.etRefTotalSpent)
        etPricePerLiter = findViewById(R.id.etRefPricePerLiter)
        etTotalKm = findViewById(R.id.etRefTotalKm)

        etDate.transformIntoDatePicker(this, "dd/MM/yyyy", Date())
        findViewById<MaterialButton>(R.id.btnRefRegister).setOnClickListener { registerRefuel() }

        db = AppDatabase.getDatabase(applicationContext)
        expenseDao = db.expenseDao()
    }

    private fun registerRefuel() {
        var dateString = etDate.text.toString()
        if(dateString == ""){
            dateString = SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN).format(Date()).toString()
        }

        val pricePerLiter: Double = try{
            etPricePerLiter.text.toString().toDouble()
        }catch(e: NumberFormatException){
            -1.0
        }

        val totalKm: Int = try{
            Integer.valueOf(etTotalKm.text.toString())
        }catch(e: java.lang.NumberFormatException){
            -1
        }

        val totalSpent: Double = try {
            etTotalSpent.text.toString().toDouble()
        }catch(e: java.lang.NumberFormatException){
            -1.0
        }

        if (totalSpent < 0 || totalKm < 0 || pricePerLiter <= 0){
            Toast.makeText(this, "Errore", Toast.LENGTH_SHORT).show()
        }else{
            val service = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            service.execute {
                val newExpense = Expense(
                    0,
                    "Rifornimento",
                    "REFUEL",
                    dateString,
                    totalSpent,
                    null,
                    pricePerLiter
                )
                expenseDao.insertExpense(newExpense)
                handler.post {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun EditText.transformIntoDatePicker(context: Context, format: String, maxDate: Date? = null) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(format, Locale.ITALIAN)
                setText(sdf.format(myCalendar.time))
            }

        setOnClickListener {
            DatePickerDialog(
                context, datePickerOnDataSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
                maxDate?.time?.also { datePicker.maxDate = it }
                show()
            }
        }
    }
}