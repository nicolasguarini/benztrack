package it.pdm.benztrack

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate


class HomeFragment : Fragment() {
    private lateinit var pieChart: PieChart

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPieChart()
        setPieChartData()
    }

    private fun setupPieChart(){
        pieChart = requireView().findViewById(R.id.homePieChart)
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.holeRadius = 58f
        pieChart.centerText = "€52,43\nUltimi 7 giorni"
        pieChart.setDrawCenterText(true)
        pieChart.setEntryLabelColor(R.color.black)
        pieChart.isHighlightPerTapEnabled = true;
        pieChart.setEntryLabelColor(R.color.black)
        pieChart.setEntryLabelTextSize(12f)

        val l: Legend = pieChart.legend
        l.textColor = R.color.black
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f
    }

    private fun setPieChartData(){
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
}