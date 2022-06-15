package it.pdm.benztrack.data

import java.text.SimpleDateFormat
import java.util.*

class Utilities {
    companion object{
        fun getTotalSpentThisMonth(expenses: List<Expense>): Double{
            var spentThisMonth = 0.0
            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN).format(Date()).toString().split('/')
            val currentMonthExpenses = expenses.filter { e -> e.date.split('/')[1] == currentDate[1] && e.date.split('/')[2] == currentDate[2] }

            for(i in currentMonthExpenses){
                spentThisMonth += i.spent
            }

            return spentThisMonth
        }
    }
}