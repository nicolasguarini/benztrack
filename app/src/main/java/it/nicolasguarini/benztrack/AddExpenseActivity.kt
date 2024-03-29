package it.nicolasguarini.benztrack

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import it.nicolasguarini.benztrack.data.AppDatabase
import it.nicolasguarini.benztrack.data.Expense
import it.nicolasguarini.benztrack.data.ExpenseDao
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class AddExpenseActivity : AppCompatActivity() {
    private lateinit var expenseType: String
    private var expenseId = 0L
    private lateinit var tvTitleLabel: TextView
    private lateinit var llDescription: LinearLayout
    private lateinit var llPricePerLiter: LinearLayout
    private lateinit var llTotalKm: LinearLayout
    private lateinit var llTaxTitle: LinearLayout
    private lateinit var llMaintenanceTitle: LinearLayout
    private lateinit var llYear: LinearLayout

    private lateinit var etDate: EditText
    private lateinit var etTotalSpent: EditText
    private lateinit var etPricePerLiter: EditText
    private lateinit var etTotalKm: EditText
    private lateinit var etMaintenanceTitle: EditText
    private lateinit var etTaxTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var etYear: EditText
    private lateinit var btnRegister: Button

    private lateinit var db: AppDatabase
    private lateinit var expenseDao: ExpenseDao
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)
        expenseType = intent.getStringExtra("expenseType").toString()
        sharedPreferences = getSharedPreferences("it.nicolasguarini.benztrack", Context.MODE_PRIVATE)

        tvTitleLabel = findViewById(R.id.addExpenseTitleLabel)

        llPricePerLiter = findViewById(R.id.etPricePerLiterContainer)
        llTotalKm = findViewById(R.id.etTotalKmContainer)
        llTaxTitle = findViewById(R.id.etTaxTitleContainer)
        llDescription = findViewById(R.id.etOptionalDescriptionContainer)
        llMaintenanceTitle = findViewById(R.id.etMaintenanceTitleContainer)
        llYear = findViewById(R.id.etYearContainer)

        etDate = findViewById(R.id.etRefDate)
        etTotalSpent = findViewById(R.id.etRefTotalSpent)
        etPricePerLiter = findViewById(R.id.etRefPricePerLiter)
        etTotalKm = findViewById(R.id.etRefTotalKm)
        etMaintenanceTitle = findViewById(R.id.etMaintenanceTitle)
        etTaxTitle = findViewById(R.id.etTaxTitle)
        etDescription = findViewById(R.id.etOptionalDescription)
        etYear = findViewById(R.id.etYear)
        etDate.transformIntoDatePicker(this, "dd/MM/yyyy", Date())
        etDate.hint = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()).toString()
        btnRegister = findViewById(R.id.btnRegister)
        btnRegister.setOnClickListener { registerExpense() }

        setupViews()

        db = AppDatabase.getDatabase(applicationContext)
        expenseDao = db.expenseDao()
    }

    private fun setupViews() {
        if(expenseType != "REFUEL"){
            llPricePerLiter.visibility = View.GONE
            llTotalKm.visibility = View.GONE
            llDescription.visibility = View.VISIBLE
        }

        when (expenseType) {
            "REFUEL" -> {}
            "TAX" -> llTaxTitle.visibility = View.VISIBLE
            "MAINTENANCE" -> llMaintenanceTitle.visibility = View.VISIBLE
            else -> llYear.visibility = View.VISIBLE
        }

        val expenseIdExtra = intent.getLongExtra("expenseId", -1L)
        val selectedCarId = sharedPreferences.getLong("selectedCarId", -1L)
        if(expenseIdExtra != -1L){ //We are modifying an existing expense
            expenseId = expenseIdExtra
            tvTitleLabel.text = when(expenseType){
                "REFUEL" -> getString(R.string.update_refuel)
                "TAX" -> getString(R.string.update_tax)
                "MAINTENANCE" -> getString(R.string.update_maintenance)
                else -> getString(R.string.update_insurance)
            }
            
            val service = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            service.execute {
                val expense = expenseDao.getExpenseFromId(selectedCarId, expenseIdExtra)
                handler.post {
                    etDate.setText(expense.date)
                    etTotalSpent.setText(expense.spent.toString())
                    etDescription.setText(expense.description)

                    when(expense.type){
                        "REFUEL" -> {
                            etPricePerLiter.setText(expense.pricePerLiter.toString())
                            etTotalKm.setText(expense.totalKm.toString())
                        }
                        "TAX" -> {
                            etTaxTitle.setText(expense.title)
                        }
                        "MAINTENANCE" -> {
                            etMaintenanceTitle.setText(expense.title)
                        }
                        else -> {
                            etYear.setText(expense.title.split(' ')[1])
                        }
                    }
                }
            }
        }else{
            tvTitleLabel.text = when(expenseType){
                "REFUEL" -> getString(R.string.label_add_refuel)
                "TAX" -> getString(R.string.label_add_tax)
                "MAINTENANCE" -> getString(R.string.label_add_maintenance)
                else -> getString(R.string.label_add_insurance)
            }
        }
    }

    private fun registerExpense() {
        lateinit var newExpense: Expense
        var errorFlag = false
        val selectedCarId = sharedPreferences.getLong("selectedCarId", -1L)

        var dateString = etDate.text.toString()
        if(dateString == ""){
            dateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()).toString()
        }

        val totalSpent: Double = try {
            etTotalSpent.text.toString().toDouble()
        }catch(e: java.lang.NumberFormatException){
            -1.0
        }

        val description = etDescription.text.toString()

        when(expenseType){
            "REFUEL" -> {
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

                if (totalSpent < 0 || totalKm < 0 || pricePerLiter <= 0){
                    Toast.makeText(this, getString(R.string.insert_all_data_warning), Toast.LENGTH_SHORT).show()
                    errorFlag = true
                }else {
                    newExpense = Expense(
                        expenseId,
                        getString(R.string.refuel_title),
                        expenseType,
                        dateString,
                        totalSpent,
                        null,
                        pricePerLiter,
                        totalKm,
                        selectedCarId
                    )
                }
            }
            "MAINTENANCE" -> {
                val title = etMaintenanceTitle.text.toString()

                if (totalSpent < 0 || title == ""){
                    Toast.makeText(this, getString(R.string.insert_all_data_warning), Toast.LENGTH_SHORT).show()
                    errorFlag = true
                }else{
                    newExpense = Expense(
                        expenseId,
                        title,
                        expenseType,
                        dateString,
                        totalSpent,
                        description,
                        null,
                        null,
                        selectedCarId
                    )
                }
            }
            "TAX" -> {
                val title = etTaxTitle.text.toString()

                if (totalSpent < 0 || title == ""){
                    Toast.makeText(this, getString(R.string.insert_all_data_warning), Toast.LENGTH_SHORT).show()
                    errorFlag = true
                }else{
                    newExpense = Expense(
                        expenseId,
                        title,
                        expenseType,
                        dateString,
                        totalSpent,
                        description,
                        null,
                        null,
                        selectedCarId
                    )
                }
            }
            else -> {
                val year: Int = try {
                    Integer.valueOf(etYear.text.toString())
                }catch (e: NumberFormatException){
                    -1
                }

                if (year < 1900 || totalSpent < 0){
                    Toast.makeText(this, getString(R.string.insert_all_data_warning), Toast.LENGTH_SHORT).show()
                    errorFlag = true
                }else{
                    val title = getString(R.string.insurance_title) + year
                    newExpense = Expense(
                        expenseId,
                        title,
                        expenseType,
                        dateString,
                        totalSpent,
                        description,
                        null,
                        null,
                        selectedCarId
                    )
                }
            }
        }

        if(!errorFlag){
            val service = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            service.execute {
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
                val sdf = SimpleDateFormat(format, Locale.getDefault())
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