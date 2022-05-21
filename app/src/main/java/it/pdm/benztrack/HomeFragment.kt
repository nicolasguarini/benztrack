package it.pdm.benztrack

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.Utils

class HomeFragment : Fragment() {
    private lateinit var pieChart: PieChart
    private lateinit var lineChart: LineChart

    private lateinit var requiredView: View
    private var selectedChart = "PIE"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requiredView = requireView()
        pieChart = requiredView.findViewById(R.id.homePieChart)
        lineChart = requiredView.findViewById(R.id.homeLineChart)

        setupPieChart()
        setupLineChart()

        requiredView.findViewById<Button>(R.id.btnDistribution).setOnClickListener {
            if(selectedChart != "PIE"){
                pieChart.visibility = View.VISIBLE
                lineChart.visibility = View.GONE
                selectedChart = "PIE"
            }
        }

        requireView().findViewById<Button>(R.id.btnTrend).setOnClickListener {
            if(selectedChart != "LINE"){
                pieChart.visibility = View.GONE
                lineChart.visibility = View.VISIBLE
                selectedChart = "LINE"
            }
        }
    }

    private fun setupPieChart(){
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.holeRadius = 58f
        pieChart.centerText = "€52,43\nUltimi 7 giorni"
        pieChart.setDrawCenterText(true)
        pieChart.setEntryLabelColor(R.color.black)
        pieChart.isHighlightPerTapEnabled = true
        pieChart.setEntryLabelColor(R.color.black)
        pieChart.setEntryLabelTextSize(12f)

        /*
        val l: Legend = pieChart.legend
        l.textColor = R.color.black
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f
         */

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

        val set = PieDataSet(entries, "Spese Maggio 2022")
        set.colors = colors
        val data = PieData(set)
        data.setValueTextSize(15f)
        data.setValueFormatter(PercentFormatter(pieChart))
        pieChart.data = data
        pieChart.invalidate()
    }

    private fun setupLineChart(){
        lineChart.setBackgroundColor(Color.WHITE)
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)

        val values = ArrayList<Entry>()
        values.add(Entry(1f, 50f))
        values.add(Entry(2f, 75f))
        values.add(Entry(3f, 75f))
        values.add(Entry(4f, 90f))
        values.add(Entry(5f, 90f))
        values.add(Entry(6f, 115f))
        values.add(Entry(7f, 115f))
        values.add(Entry(8f, 140f))
        values.add(Entry(9f, 155f))
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
            set1.enableDashedLine(10f, 5f, 0f)
            set1.enableDashedHighlightLine(10f, 5f, 0f)
            set1.color = Color.DKGRAY
            set1.setCircleColor(Color.DKGRAY)
            set1.lineWidth = 1f
            set1.circleRadius = 3f
            set1.setDrawCircleHole(false)
            set1.valueTextSize = 9f
            set1.setDrawFilled(true)
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f
            if (Utils.getSDKInt() >= 18) {
                //val drawable = ContextCompat.getDrawable(this, R.drawable.fade_blue)
                //set1.fillDrawable = drawable
                set1.fillColor = Color.BLACK
            } else {
                set1.fillColor = Color.BLACK
            }
            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set1)
            val data = LineData(dataSets)
            lineChart.data = data
        }

    }
}