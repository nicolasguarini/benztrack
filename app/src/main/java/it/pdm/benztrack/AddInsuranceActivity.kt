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
import java.time.Year
import java.util.*

class AddInsuranceActivity : AppCompatActivity() {
    private lateinit var etDate: EditText
    private lateinit var etYear: EditText
    private lateinit var etTotalSpent: EditText
    private lateinit var etOptionalDesc: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_insurance)

        etDate = findViewById(R.id.etInsDate)
        etYear = findViewById(R.id.etInsYear)
        etTotalSpent = findViewById(R.id.etInsTotalSpent)
        etOptionalDesc = findViewById(R.id.etInsOptionalDescription)

        etDate.transformIntoDatePicker(this, "dd/MM/yyyy", Date())
        findViewById<MaterialButton>(R.id.btnInsRegister).setOnClickListener{
            registerInsurance()
        }
    }

    private fun registerInsurance() {
        val year: Int = try {
            Integer.valueOf(etYear.text.toString())
        }catch (e: NumberFormatException){
            -1
        }

        var date = etDate.text.toString()

        if(date == ""){
            date = SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN).format(Date()).toString()
        }

        val totalSpent: Double = try {
            etTotalSpent.text.toString().toDouble()
        }catch(e: java.lang.NumberFormatException){
            -1.0
        }

        val optionalDescription = etOptionalDesc.text.toString()

        if (year < 1900 || totalSpent < 0){
            Toast.makeText(this, "Errore", Toast.LENGTH_SHORT).show()
        }else{
            Log.d("RegIns", year.toString())
            Log.d("RegIns", date)
            Log.d("RegIns", totalSpent.toString())
            Log.d("RegIns", optionalDescription)
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