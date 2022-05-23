package it.pdm.benztrack

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*
import kotlin.collections.ArrayList

class ExpenseListFragment : Fragment() {
    private lateinit var requiredView: View
    private lateinit var listView: ListView
    private var arrayList: ArrayList<Expense> = ArrayList()
    private var adapter: CustomAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_expense_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requiredView = requireView()
        listView = requiredView.findViewById(R.id.lvExpenseList)

        //TODO: fetch data from db
        arrayList.add(Expense(R.drawable.ic_tabler_engine, "Cambiio olio", 45.00))
        arrayList.add(Expense(R.drawable.ic_green_local_gas_station_for_list, "Rifornimento", 98.00))
        arrayList.add(Expense(R.drawable.ic_bi_shield_check, "Assicurazione RCA 2022", 490.00))
        arrayList.add(Expense(R.drawable.ic_green_local_gas_station_for_list, "Rifornimento", 20.00))
        arrayList.add(Expense(R.drawable.ic_green_local_gas_station_for_list, "Rifornimento", 15.00))
        arrayList.add(Expense(R.drawable.ic_tabler_engine, "Pastiglie freni", 29.50))
        arrayList.add(Expense(R.drawable.ic_green_local_gas_station_for_list, "Rifornimento", 20.00))
        arrayList.add(Expense(R.drawable.ic_tabler_engine, "Cambio olio", 45.00))
        arrayList.add(Expense(R.drawable.ic_green_local_gas_station_for_list, "Rifornimento", 98.00))
        arrayList.add(Expense(R.drawable.ic_tabler_engine, "Cambiio olio", 45.00))
        arrayList.add(Expense(R.drawable.ic_green_local_gas_station_for_list, "Rifornimento", 98.00))
        arrayList.add(Expense(R.drawable.ic_bi_shield_check, "Assicurazione RCA 2022", 490.00))
        arrayList.add(Expense(R.drawable.ic_green_local_gas_station_for_list, "Rifornimento", 20.00))
        arrayList.add(Expense(R.drawable.ic_green_local_gas_station_for_list, "Rifornimento", 15.00))
        arrayList.add(Expense(R.drawable.ic_tabler_engine, "Pastiglie freni", 29.50))

        adapter = CustomAdapter(requireContext() ,arrayList)
        listView.adapter = adapter
        
        listView.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position)
            Log.d("ITEM CLICKED", item.toString())
        }
    }
}

class Expense(val iconId: Int, val title: String, val price: Double)