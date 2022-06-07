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

class AddMaintenanceActivity : AppCompatActivity() {
    private lateinit var etTitle: EditText
    private lateinit var etDate: EditText
    private lateinit var etTotalSpent: EditText
    private lateinit var etOptionalDesc: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_maintenance)

        etTitle = findViewById(R.id.etMaintTitle)
        etDate = findViewById(R.id.etMaintDate)
        etTotalSpent = findViewById(R.id.etMaintTotalSpent)
        etOptionalDesc = findViewById(R.id.etMaintOptionalDescription)

        etDate.transformIntoDatePicker(this, "dd/MM/yyyy", Date())
        findViewById<MaterialButton>(R.id.btnMaintRegister).setOnClickListener { registerMaintenance() }
    }

    private fun registerMaintenance() {
        val title = etTitle.text.toString()

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

        if (totalSpent < 0 || title == ""){
            Toast.makeText(this, "Errore", Toast.LENGTH_SHORT).show()
        }else{
            Log.d("Reg", title)
            Log.d("Reg", date)
            Log.d("Reg", totalSpent.toString())
            Log.d("Reg", optionalDescription)
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