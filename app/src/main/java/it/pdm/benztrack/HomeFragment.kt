package it.pdm.benztrack

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.button.MaterialButton
import it.pdm.benztrack.data.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    private lateinit var tvWelcomeName: TextView
    private lateinit var tvCarName: TextView
    private lateinit var tvSpentThisMonth: TextView
    private lateinit var tvEmittedThisMonth: TextView
    private lateinit var tvAvgConsumptionThisMonth: TextView
    private lateinit var imgUser: ImageView

    private lateinit var pieChart: PieChart
    private lateinit var lineChart: LineChart
    private lateinit var listView: ListView
    private var arrayList: ArrayList<ExpenseView> = ArrayList()
    private var adapter: CustomAdapter? = null
    private lateinit var btnPieChart: MaterialButton
    private lateinit var btnLineChart: MaterialButton
    private lateinit var requiredView: View
    private var selectedChart = "PIE"

    private lateinit var db: AppDatabase
    private lateinit var expenseDao: ExpenseDao
    private lateinit var expensesList: List<Expense>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requiredView = requireView()

        tvWelcomeName = requiredView.findViewById(R.id.tvWelcomeName)
        tvCarName = requiredView.findViewById(R.id.tvCarName)
        tvSpentThisMonth = requiredView.findViewById(R.id.tvSpentThisMonth)
        tvEmittedThisMonth = requiredView.findViewById(R.id.tvEmittedThisMonth)
        tvAvgConsumptionThisMonth = requiredView.findViewById(R.id.tvAvgConsumptionThisMonth)
        imgUser = requiredView.findViewById(R.id.imgUser)
        imgUser.setOnClickListener { startActivity(Intent(this.requireContext(), UserActivity::class.java)) }

        pieChart = requiredView.findViewById(R.id.homePieChart)
        lineChart = requiredView.findViewById(R.id.homeLineChart)
        listView = requiredView.findViewById(R.id.lvLastExpenses)
        btnPieChart = requiredView.findViewById(R.id.btnDistribution)
        btnLineChart = requiredView.findViewById(R.id.btnTrend)
        db = AppDatabase.getDatabase(this.requireContext())
        expenseDao = db.expenseDao()
        sharedPreferences = this.requireContext().getSharedPreferences("it.pdm.benztrack", Context.MODE_PRIVATE)

        setupListView()

        btnPieChart.setOnClickListener {
            if(selectedChart != "PIE"){
                pieChart.visibility = View.VISIBLE
                lineChart.visibility = View.GONE

                btnPieChart.setBackgroundColor(getColor(requireContext(), R.color.gray_bg))
                btnPieChart.setTextColor(getColor(requireContext(), R.color.black))
                btnPieChart.setIconTintResource(R.color.black)

                btnLineChart.setBackgroundColor(getColor(requireContext(), R.color.white))
                btnLineChart.setTextColor(getColor(requireContext(), R.color.lightgray))
                btnLineChart.setIconTintResource(R.color.lightgray)

                selectedChart = "PIE"
            }
        }

        btnLineChart.setOnClickListener {
            if(selectedChart != "LINE" && expensesList.isNotEmpty()){
                pieChart.visibility = View.GONE
                lineChart.visibility = View.VISIBLE

                btnLineChart.setBackgroundColor(getColor(requireContext(), R.color.gray_bg))
                btnLineChart.setTextColor(getColor(requireContext(), R.color.black))
                btnLineChart.setIconTintResource(R.color.black)

                btnPieChart.setBackgroundColor(getColor(requireContext(), R.color.white))
                btnPieChart.setTextColor(getColor(requireContext(), R.color.lightgray))
                btnPieChart.setIconTintResource(R.color.lightgray)

                selectedChart = "LINE"
            }
        }
    }

    private fun setupListView(){
        val selectedCarId = sharedPreferences.getLong("selectedCarId", -1L)

        if(selectedCarId != -1L){
            val service = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            service.execute {
                expensesList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    expenseDao.getExpensesFromCarId(selectedCarId).sortedBy { LocalDate.parse(it.date, dateTimeFormatter) }
                } else {
                    expenseDao.getExpensesFromCarId(selectedCarId).sortedBy { it.date }
                }

                for (i in expensesList) {
                    val iconId = when (i.type) {
                        "REFUEL" -> R.drawable.ic_green_local_gas_station_for_list
                        "MAINTENANCE" -> R.drawable.ic_tabler_engine
                        "TAX" -> R.drawable.ic_baseline_article_24
                        else -> R.drawable.ic_bi_shield_check
                    }

                    arrayList.add(ExpenseView(i.expenseId, iconId, i.title, i.spent))
                }

                handler.post {
                    arrayList.reverse()
                    setupUIData()
                    setupPieChart()
                    setupLineChart()

                    adapter = CustomAdapter(requireContext(), arrayList)
                    listView.adapter = adapter
                    listView.setOnItemClickListener { _, _, position, _ ->
                        val expenseId = arrayList[position].expenseId
                        startActivity(Intent(this.requireContext(), SingleExpenseActivity::class.java).putExtra("expenseId", expenseId))
                    }
                }
            }
        }else{
            Toast.makeText(this.requireContext(), "DB ERROR: cancella i dati dell'app o reinstallala", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUIData() {
        val userName = sharedPreferences.getString("userName", "")
        val carBrand = sharedPreferences.getString("selectedCarBrand", "")
        val carModel = sharedPreferences.getString("selectedCarModel", "")
        val fuelType = sharedPreferences.getString("selectedFuelType", "")!!
        val euroCategory = sharedPreferences.getString("selectedEuroCategory", "")!!

        tvWelcomeName.text = getString(R.string.welcome_user, userName)
        tvCarName.text = getString(R.string.car_label, carBrand, carModel)

        val thisMonthExpenses = Utilities.getThisMonthExpenses(expensesList)
        val spent = Utilities.getTotalSpent(thisMonthExpenses)
        val emitted = Utilities.getEmitted(thisMonthExpenses, fuelType, euroCategory)
        val consumption = Utilities.getAvgConsumption(thisMonthExpenses)

        tvSpentThisMonth.text = spent.toString()
        tvEmittedThisMonth.text = emitted.toString()
        tvAvgConsumptionThisMonth.text = consumption.toString()

        sharedPreferences.edit()
            .putFloat("spentThisMonth", spent.toFloat())
            .putFloat("emittedThisMonth", emitted.toFloat())
            .putFloat("consumptionThisMonth", consumption.toFloat())
            .apply()

        val prevMonthExpenses = Utilities.getPrevMonthExpenses(expensesList)
        if(prevMonthExpenses.any { it.type == "REFUEL" }){
            val consumptionPrevMonth = Utilities.getAvgConsumption(prevMonthExpenses)
            sharedPreferences.edit()
                .putFloat("consumptionPrevMonth", consumptionPrevMonth.toFloat())
                .apply()
        }
    }

    private fun setupPieChart(){
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.holeRadius = 48f
        pieChart.centerText = if (expensesList.isEmpty()) "Nessuna spesa registrata!" else Utilities.getThisMonthYear()
        pieChart.setCenterTextSize(16f)
        pieChart.setDrawCenterText(true)
        pieChart.setEntryLabelColor(R.color.black)
        pieChart.isHighlightPerTapEnabled = true
        pieChart.setEntryLabelColor(R.color.black)
        pieChart.setEntryLabelTextSize(14f)
        pieChart.legend.isEnabled = false

        val entries = getPieEntries()

        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor("#A7FB9C"))
        colors.add(Color.parseColor("#A36BFF"))
        colors.add(Color.parseColor("#FF885C"))
        colors.add(ColorTemplate.getHoloBlue())

        val set = PieDataSet(entries, "")
        set.colors = colors
        val data = PieData(set)
        data.setValueTextSize(15f)
        data.setValueFormatter(PercentFormatter(pieChart))
        pieChart.data = data
        pieChart.invalidate()

        pieChart.animateY(1400, Easing.EaseInOutQuad)
    }

    private fun getPieEntries(): ArrayList<PieEntry> {
        val entries = ArrayList<PieEntry>()
        val thisMonthExpenses = Utilities.getThisMonthExpenses(expensesList)
        var spentRefuel = 0.0
        var spentInsurance = 0.0
        var spentTax = 0.0
        var spentMaintenance = 0.0

        for(e in thisMonthExpenses){
            when(e.type){
                "REFUEL" -> spentRefuel += e.spent
                "MAINTENANCE" -> spentMaintenance += e.spent
                "TAX" -> spentTax += e.spent
                else -> spentInsurance += e.spent
            }
        }

        val total = spentRefuel + spentMaintenance + spentInsurance + spentTax
        if(spentRefuel > 0.0) entries.add(PieEntry(((spentRefuel/total)*100).toFloat(), "Rifornimento"))
        if(spentMaintenance > 0.0) entries.add(PieEntry(((spentMaintenance/total)*100).toFloat(), "Manutenzione"))
        if(spentInsurance > 0.0) entries.add(PieEntry(((spentInsurance/total)*100).toFloat(), "Assicurazione"))
        if(spentTax > 0.0) entries.add(PieEntry(((spentTax/total)*100).toFloat(), "Tasse"))

        return entries
    }

    private fun setupLineChart(){
        lineChart.setBackgroundColor(Color.WHITE)
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)

        val values = getLineEntries()

        val set1: LineDataSet
        if (lineChart.data != null &&
            lineChart.data.dataSetCount > 0
        ) {
            set1 = lineChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            lineChart.data.notifyDataChanged()
            lineChart.notifyDataSetChanged()
        } else {
            set1 = LineDataSet(values, "Andamento spese ${Utilities.getThisMonthYear()}")
            set1.setDrawIcons(false)
            set1.color = Color.DKGRAY
            set1.setCircleColor(Color.DKGRAY)
            set1.lineWidth = 3f
            set1.mode = LineDataSet.Mode.CUBIC_BEZIER
            set1.circleRadius = 3f
            set1.setDrawCircleHole(false)
            set1.valueTextSize = 9f
            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set1)
            val data = LineData(dataSets)
            lineChart.data = data
            lineChart.data.notifyDataChanged()
            lineChart.notifyDataSetChanged()
            lineChart.invalidate()
        }
    }

    private fun getLineEntries(): List<Entry>{
        val entries = ArrayList<Entry>()
        var spent = 0.0
        val thisMonthExpenses = Utilities.getThisMonthExpenses(expensesList)

        for (e in thisMonthExpenses){
            val day = e.date.split('/')[0]
            spent += e.spent
            entries.add(Entry(day.toFloat(), spent.toFloat()))
        }

        return entries
    }
}