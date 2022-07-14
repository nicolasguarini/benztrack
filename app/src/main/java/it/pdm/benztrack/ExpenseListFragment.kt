package it.pdm.benztrack

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import it.pdm.benztrack.data.AppDatabase
import it.pdm.benztrack.data.Expense
import it.pdm.benztrack.data.ExpenseDao
import it.pdm.benztrack.data.ExpenseView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class ExpenseListFragment : Fragment() {
    private lateinit var requiredView: View
    private lateinit var listView: ListView
    private var arrayList: ArrayList<ExpenseView> = ArrayList()
    private var adapter: CustomAdapter? = null
    private lateinit var db: AppDatabase
    private lateinit var expenseDao: ExpenseDao
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var tvWelcomeName: TextView
    private lateinit var tvCarName: TextView
    private lateinit var ivUser: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_expense_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requiredView = requireView()
        listView = requiredView.findViewById(R.id.lvExpenseList)
        sharedPreferences = this.requireContext().getSharedPreferences("it.pdm.benztrack", Context.MODE_PRIVATE)

        tvWelcomeName = requiredView.findViewById(R.id.tvExpensesListWelcomeUser)
        tvCarName = requiredView.findViewById(R.id.tvCarNameExpensesList)
        ivUser = requiredView.findViewById(R.id.imgUserExpensesList)
        ivUser.setOnClickListener { startActivity(Intent(this.requireContext(), UserActivity::class.java)) }

        db = AppDatabase.getDatabase(this.requireContext())
        expenseDao = db.expenseDao()
        setupUIData()
        setupListView()
    }

    private fun setupUIData(){
        val userName = sharedPreferences.getString("userName", "")
        val carBrand = sharedPreferences.getString("selectedCarBrand", "")
        val carModel = sharedPreferences.getString("selectedCarModel", "")

        tvWelcomeName.text = getString(R.string.welcome_user, userName)
        tvCarName.text = getString(R.string.car_label, carBrand, carModel)
    }

    private fun setupListView(){
        val selectedCarId = sharedPreferences.getLong("selectedCarId", -1L)

        if(selectedCarId != -1L){
            val service = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            service.execute {
                val expensesList: List<Expense> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    expenseDao.getExpensesFromCarId(selectedCarId).sortedBy { LocalDate.parse(it.date, dateTimeFormatter) }
                } else {
                    expenseDao.getExpensesFromCarId(selectedCarId).sortedBy { it.date }
                }

                for(i in expensesList){
                    val iconId = when(i.type){
                        "REFUEL" -> R.drawable.ic_green_local_gas_station_for_list
                        "MAINTENANCE" -> R.drawable.ic_tabler_engine
                        "TAX" -> R.drawable.ic_baseline_article_24
                        else -> R.drawable.ic_bi_shield_check
                    }

                    arrayList.add(ExpenseView(i.expenseId, iconId, i.title, i.spent))
                }

                handler.post {
                    arrayList.reverse()
                    adapter = CustomAdapter(requireContext(), arrayList)
                    listView.adapter = adapter

                    listView.setOnItemClickListener { _, _, position, _ ->
                        val expenseId = arrayList[position].expenseId
                        startActivity(Intent(this.requireContext(), SingleExpenseActivity::class.java).putExtra("expenseId", expenseId))
                    }
                }
            }
        }else{
            Toast.makeText(this.requireContext(), "DB ERROR: cancella i dati dell'app e registra l'auto", Toast.LENGTH_SHORT).show()
        }
    }
}