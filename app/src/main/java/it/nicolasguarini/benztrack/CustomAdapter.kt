package it.nicolasguarini.benztrack

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import it.nicolasguarini.benztrack.data.ExpenseView

class CustomAdapter(private val context: Context, private val arrayList: java.util.ArrayList<ExpenseView>) : BaseAdapter(){
    private lateinit var icon: ImageView
    private lateinit var title: TextView
    private lateinit var price: TextView

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val retView: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.row_custom_el, parent, false)

        icon = retView.findViewById(R.id.el_icon)
        title = retView.findViewById(R.id.el_title)
        price = retView.findViewById(R.id.el_price)

        icon.setImageResource(arrayList[position].iconId)
        title.text = arrayList[position].title
        price.text = arrayList[position].spent.toString()

        return retView
    }
}