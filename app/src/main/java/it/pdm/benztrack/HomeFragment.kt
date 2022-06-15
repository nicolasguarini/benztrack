package it.pdm.benztrack

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.github.mikephil.charting.utils.Utils
import com.google.android.material.button.MaterialButton
import it.pdm.benztrack.data.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    private lateinit var tvWelcomeName: TextView
    private lateinit var tvCarName: TextView
    private lateinit var tvSpentThisMonth: TextView

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

        pieChart = requiredView.findViewById(R.id.homePieChart)
        lineChart = requiredView.findViewById(R.id.homeLineChart)
        listView = requiredView.findViewById(R.id.lvLastExpenses)
        btnPieChart = requiredView.findViewById(R.id.btnDistribution)
        btnLineChart = requiredView.findViewById(R.id.btnTrend)
        db = AppDatabase.getDatabase(this.requireContext())
        expenseDao = db.expenseDao()
        sharedPreferences = this.requireContext().getSharedPreferences("it.pdm.benztrack", Context.MODE_PRIVATE)

        setupListView()
        setupPieChart()
        setupLineChart()

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
            if(selectedChart != "LINE"){
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
                expensesList = expenseDao.getExpensesFromCarId(selectedCarId)
                for (i in expensesList) {
                    Log.d("DB RESULT", i.toString())
                    Log.d("SELECTED CAR", selectedCarId.toString())

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
                    adapter = CustomAdapter(requireContext(), arrayList)
                    listView.adapter = adapter

                    listView.setOnItemClickListener { parent, view, position, id ->
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
        tvWelcomeName.text = getString(R.string.welcome_user, userName)
        tvCarName.text = getString(R.string.car_label, carBrand, carModel)

        tvSpentThisMonth.text = Utilities.getTotalSpentThisMonth(expensesList).toString()
    }

    private fun setupPieChart(){
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.holeRadius = 48f
        pieChart.centerText = "Maggio 2022"
        pieChart.setCenterTextSize(16f)
        pieChart.setDrawCenterText(true)
        pieChart.setEntryLabelColor(R.color.black)
        pieChart.isHighlightPerTapEnabled = true
        pieChart.setEntryLabelColor(R.color.black)
        pieChart.setEntryLabelTextSize(14f)
        pieChart.legend.isEnabled = false

        //TODO: fetch data from db
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(32f, "Manutenzione"))
        entries.add(PieEntry(18f, "Assicurazione"))
        entries.add(PieEntry(12f, "Bollo"))
        entries.add(PieEntry(38f, "Carburante"))

        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor("#FF885C"))
        colors.add(Color.parseColor("#A7FB9C"))
        colors.add(Color.parseColor("#A36BFF"))
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

    private fun setupLineChart(){
        lineChart.setBackgroundColor(Color.WHITE)
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)

        val values = ArrayList<Entry>()
        values.add(Entry(10f, 155f))
        values.add(Entry(11f, 155f))
        values.add(Entry(12f, 155f))
        values.add(Entry(13f, 180f))
        values.add(Entry(14f, 180f))
        values.add(Entry(15f, 200f))

        val set1: LineDataSet
        if (lineChart.data != null &&
            lineChart.data.dataSetCount > 0
        ) {
            set1 = lineChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            lineChart.data.notifyDataChanged()
            lineChart.notifyDataSetChanged()
        } else {
            set1 = LineDataSet(values, "Andamento spese Maggio 2022")
            set1.setDrawIcons(false)
            set1.color = Color.DKGRAY
            set1.setCircleColor(Color.DKGRAY)
            set1.lineWidth = 3f
            set1.mode = LineDataSet.Mode.CUBIC_BEZIER
            set1.circleRadius = 3f
            set1.setDrawCircleHole(false)
            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set1)
            val data = LineData(dataSets)
            lineChart.data = data
        }

    }
}