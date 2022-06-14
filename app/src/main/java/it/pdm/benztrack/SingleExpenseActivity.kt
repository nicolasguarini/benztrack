package it.pdm.benztrack

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import it.pdm.benztrack.data.AppDatabase
import it.pdm.benztrack.data.Expense
import it.pdm.benztrack.data.ExpenseDao
import it.pdm.benztrack.data.ExpenseView
import java.util.concurrent.Executors
import kotlin.math.exp

class SingleExpenseActivity : AppCompatActivity() {
    private lateinit var llPricePerLiter: LinearLayout
    private lateinit var llKm: LinearLayout
    private lateinit var llYear: LinearLayout
    private lateinit var llDescription: LinearLayout

    private lateinit var tvTitle: TextView
    private lateinit var tvSpent: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvPricePerLiter: TextView
    private lateinit var tvYear: TextView
    private lateinit var tvKm: TextView
    private lateinit var tvDescription: TextView

    private lateinit var db: AppDatabase
    private lateinit var expenseDao: ExpenseDao
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var expense: Expense

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_expense)

        llPricePerLiter = findViewById(R.id.containerSingleExpensePricePerLiter)
        llKm = findViewById(R.id.containerSingleExpenseKm)
        llYear = findViewById(R.id.containerSingleExpenseYear)
        llDescription = findViewById(R.id.containerSingleExpenseDescription)

        tvTitle = findViewById(R.id.tvSingleExpenseTitle)
        tvDate = findViewById(R.id.tvSingleExpenseDate)
        tvPricePerLiter = findViewById(R.id.tvSingleExpensePricePerLiter)
        tvYear = findViewById(R.id.tvSingleExpenseYear)
        tvKm = findViewById(R.id.tvSingleExpenseKm)
        tvDescription = findViewById(R.id.tvSingleExpenseDescription)
        tvSpent = findViewById(R.id.tvSingleExpenseTotalSpent)

        sharedPreferences = applicationContext.getSharedPreferences("it.pdm.benztrack", Context.MODE_PRIVATE)
        db = AppDatabase.getDatabase(applicationContext)
        expenseDao = db.expenseDao()
        getExpense()
    }

    private fun getExpense() {
        val expenseDao = db.expenseDao()
        val selectedCarId = sharedPreferences.getLong("selectedCarId", -1L)
        val expenseId = intent.getLongExtra("expenseId", -1L)

        if(selectedCarId != -1L && expenseId != -1L){
            val service = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            service.execute {
                expense = expenseDao.getExpenseFromId(selectedCarId, expenseId)
                handler.post {
                    setupViews()
                    setupData()
                }
            }
        }else{
            Toast.makeText(applicationContext, "DB ERROR: cancella i dati dell'app e registra l'auto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupData() {
        tvTitle.text = expense.title
        tvDate.text = expense.date
        tvSpent.text = expense.spent.toString()

        if(expense.type == "REFUEL"){
            tvPricePerLiter.text = expense.pricePerLiter.toString()
            tvKm.text = expense.totalKm.toString()
        }else if(expense.type == "INSURANCE"){
            tvYear.text = expense.title.split(' ')[1]
        }

        tvDescription.text = expense.description
    }

    private fun setupViews() {
        if(expense.type != "REFUEL"){
            llPricePerLiter.visibility = View.GONE
            llKm.visibility = View.GONE
        }else{
            llDescription.visibility = View.GONE
        }

        if(expense.type != "INSURANCE"){
            llYear.visibility = View.GONE
        }
    }
}