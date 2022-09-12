package it.pdm.benztrack.data

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class Utilities {
    companion object{
        fun getTotalSpent(expenses: List<Expense>): Double{
            var spentThisMonth = 0.0

            for(i in expenses){
                spentThisMonth += i.spent
            }

            return spentThisMonth
        }

        fun getEmitted(expenses: List<Expense>, fuelType: String, euroCategory: String): Double {
            val refuels = expenses.filter { it.type == "REFUEL" }

            if (refuels.isEmpty()){
                return 0.0
            }

            val percurred = refuels[refuels.size-1].totalKm?.minus(refuels[0].totalKm!!)

            val emitted: Double
            when(fuelType){ //SOURCE: WIKIPEDIA
                "petrol" ->{
                    emitted =  when(euroCategory){
                        "Euro 6" -> 1.0 * percurred!!
                        "Euro 5" -> 1.0 * percurred!!
                        "Euro 4" -> 1.0 * percurred!!
                        "Euro 3" -> 2.3 * percurred!!
                        "Euro 2" -> 2.2 * percurred!!
                        else -> 2.7 * percurred!!
                    }
                }
                "diesel" -> {
                    emitted =  when(euroCategory){
                        "Euro 6" -> 0.5 * percurred!!
                        "Euro 5" -> 0.5 * percurred!!
                        "Euro 4" -> 0.5 * percurred!!
                        "Euro 3" -> 0.6 * percurred!!
                        "Euro 2" -> 1.0 * percurred!!
                        else -> 2.7 * percurred!!
                    }
                }
                else -> { //hybrid
                    emitted =  when(euroCategory){
                        "Euro 6" -> 0.7 * percurred!!
                        else -> 0.9 * percurred!!
                    }
                }
            }

            return ((emitted / 10.0).roundToInt() * 10.0)
        }

        fun getAvgConsumption(expenses: List<Expense>): Double{
            val refuels = expenses.filter { it.type == "REFUEL" }

            if(refuels.isEmpty()){
                return 0.0
            }

            val percurred = refuels[refuels.size-1].totalKm?.minus(refuels[0].totalKm!!)!!

            var litersUsed = 0.0
            for(r in refuels){
                litersUsed += r.spent / r.pricePerLiter!!
            }

            return ((percurred / litersUsed) * 10.0).roundToInt() / 10.0
        }

        fun getThisMonthExpenses(expenses: List<Expense>): List<Expense>{
            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()).toString().split('/')
            return expenses.filter { e -> e.date.split('/')[1] == currentDate[1] && e.date.split('/')[2] == currentDate[2] }
        }

        fun getPrevMonthExpenses(expenses: List<Expense>): List<Expense>{
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, -1)
            val datePrevMonth = format.format(cal.time).split('/')

            return expenses.filter { e -> e.date.split('/')[1] ==  datePrevMonth[1] && e.date.split('/')[2] == datePrevMonth[2]}
        }

        fun getThisMonthYear(): String{
            val cal: Calendar = Calendar.getInstance()

            val monthDate = SimpleDateFormat("MMMM", Locale.getDefault())
            val monthYear = SimpleDateFormat("yyyy", Locale.getDefault())

            val monthName: String = monthDate.format(cal.time).replaceFirstChar { it.uppercase() }
            val yearName: String = monthYear.format(cal.time)

            return "$monthName $yearName"
        }
    }
}