package it.pdm.benztrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import org.w3c.dom.Text

class SelectCarFuelTypeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_car_fuel_type)

        val selectedBrand = intent.getStringExtra("selectedBrand").toString()
        val selectedModel = intent.getStringExtra("selectedModel").toString()
        val toggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.toggleButtonGroup)
        var selectedFuel = ""

        if(selectedModel != "" && selectedModel != ""){
            val tvSelectFuelType = findViewById<TextView>(R.id.textViewSelectFuelType)
            tvSelectFuelType.text = tvSelectFuelType.text.toString().replace("auto", "$selectedBrand $selectedModel")
        }

        toggleGroup.addOnButtonCheckedListener{_, checkedId, isChecked ->
            if(isChecked) {
                when(checkedId) {
                    R.id.diesel -> selectedFuel = "diesel"
                    R.id.petrol -> selectedFuel = "petrol"
                    R.id.hybrid -> selectedFuel = "hybrid"
                }
            }else {
                selectedFuel = ""
            }
        }

        findViewById<Button>(R.id.btnFuelNext).setOnClickListener {
            if(selectedFuel != ""){
                if(selectedFuel != ""){
                    val intent = Intent(this, SelectCarEuroCategoryActivity::class.java)
                    intent.putExtra("selectedBrand", selectedBrand)
                    intent.putExtra("selectedModel", selectedModel)
                    intent.putExtra("selectedFuel", selectedFuel)
                    startActivity(intent)
                }else{
                    Toast.makeText(this, "No item selected!", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "No item selected!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}