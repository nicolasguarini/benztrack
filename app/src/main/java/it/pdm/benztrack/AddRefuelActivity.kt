package it.pdm.benztrack

import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class AddRefuelActivity : AppCompatActivity() {
    private lateinit var etDate: EditText
    private lateinit var etTotalSpent: EditText
    private lateinit var etPricePerLiter: EditText
    private lateinit var etTotalKm: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_refuel)

        etDate = findViewById(R.id.etRefDate)
        etTotalSpent = findViewById(R.id.etRefTotalSpent)
        etPricePerLiter = findViewById(R.id.etRefPricePerLiter)
        etTotalKm = findViewById(R.id.etRefTotalKm)

        etDate.transformIntoDatePicker(this, "dd/MM/yyyy", Date())
        findViewById<MaterialButton>(R.id.btnRefRegister).setOnClickListener { registerRefuel() }
    }

    private fun registerRefuel() {
        var date = etDate.text.toString()
        if(date == ""){
            date = SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN).format(Date()).toString()
        }

        val pricePerLiter: Double = try{
            etPricePerLiter.text.toString().toDouble()
        }catch(e: NumberFormatException){
            -1.0
        }

        val totalKm: Int = try{
            Integer.valueOf(etTotalKm.text.toString())
        }catch(e: java.lang.NumberFormatException){
            -1
        }

        val totalSpent: Double = try {
            etTotalSpent.text.toString().toDouble()
        }catch(e: java.lang.NumberFormatException){
            -1.0
        }

        if (totalSpent < 0 || totalKm < 0 || pricePerLiter <= 0){
            Toast.makeText(this, "Errore", Toast.LENGTH_SHORT).show()
        }else{
            Log.d("Reg", date)
            Log.d("Reg", totalSpent.toString())
            Log.d("Reg", totalKm.toString())
            Log.d("Reg", pricePerLiter.toString())
        }
    }

    private fun EditText.transformIntoDatePicker(context: Context, format: String, maxDate: Date? = null) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(format, Locale.ITALIAN)
                setText(sdf.format(myCalendar.time))
            }

        setOnClickListener {
            DatePickerDialog(
                context, datePickerOnDataSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
                maxDate?.time?.also { datePicker.maxDate = it }
                show()
            }
        }
    }
}