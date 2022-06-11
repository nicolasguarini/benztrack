package it.pdm.benztrack

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import it.pdm.benztrack.data.AppDatabase
import it.pdm.benztrack.data.ExpenseDao
import it.pdm.benztrack.data.ExpenseView
import kotlin.collections.ArrayList

class ExpenseListFragment : Fragment() {
    private lateinit var requiredView: View
    private lateinit var listView: ListView
    private var arrayList: ArrayList<ExpenseView> = ArrayList()
    private var adapter: CustomAdapter? = null
    private lateinit var db: AppDatabase
    private lateinit var expenseDao: ExpenseDao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_expense_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requiredView = requireView()
        listView = requiredView.findViewById(R.id.lvExpenseList)
        db = AppDatabase.getDatabase(this.requireContext())
        expenseDao = db.expenseDao()
        setupListView()
    }

    private fun setupListView(){
        Thread{
            val expensesList = expenseDao.getAll()
            for(i in expensesList){
                Log.d("DB RESULT", i.toString())

                val iconId = when(i.type){
                    "REFUEL" -> R.drawable.ic_green_local_gas_station_for_list
                    "MAINTENANCE" -> R.drawable.ic_tabler_engine
                    "TAX" -> R.drawable.ic_baseline_article_24
                    else -> R.drawable.ic_bi_shield_check
                }

                arrayList.add(ExpenseView(i.expenseId, iconId, i.title, i.spent))
            }

            adapter = CustomAdapter(requireContext() ,arrayList)
            listView.adapter = adapter

            listView.setOnItemClickListener { parent, view, position, id ->
                val item = parent.getItemAtPosition(position)
                Log.d("ITEM CLICKED", item.toString())
            }
        }.start()

    }
}