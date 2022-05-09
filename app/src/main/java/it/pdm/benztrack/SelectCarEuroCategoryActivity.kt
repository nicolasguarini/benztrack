package it.pdm.benztrack

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.android.material.button.MaterialButtonToggleGroup

class SelectCarEuroCategoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_car_euro_category)

        val selectedBrand = intent.getStringExtra("selectedBrand").toString()
        val selectedModel = intent.getStringExtra("selectedModel").toString()
        val selectedFuel = intent.getStringExtra("selectedFuel").toString()
        var selectedEuro = ""
        val euroCategory = arrayOf(
            "Euro 6",
            "Euro 5",
            "Euro 4",
            "Euro 3",
            "Euro 2",
            "Euro 1",
            "Euro 0"
        )

        if(selectedModel != "" && selectedModel != "" && selectedFuel != ""){
            val tvSelectEuro = findViewById<TextView>(R.id.textViewSelectEuroCategory)
            tvSelectEuro.text = tvSelectEuro.text.toString().replace("auto", "$selectedBrand $selectedModel")
        }

        val listView = findViewById<ListView>(R.id.listViewEuroCategory)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, euroCategory)
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE

        listView.setOnItemClickListener{ adapterVIew, _, position, _ ->
            selectedEuro = adapterVIew.getItemAtPosition(position).toString()
        }

        findViewById<Button>(R.id.btnEuroNext).setOnClickListener {
            val sharedPref = getSharedPreferences("it.pdm.benztrack", Context.MODE_PRIVATE)
            if(selectedEuro != ""){
                //TODO: Register car to database
                sharedPref.edit()
                    .putBoolean("firstStart", false)
                    .putString("carBrand", selectedBrand)
                    .putString("carModel", selectedModel)
                    .putString("carFuel", selectedFuel)
                    .putString("carEuro", selectedEuro)
                    .apply()

                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this, "No item selected!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}