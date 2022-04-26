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

        toggleGroup.addOnButtonCheckedListener{toggleGroup, checkedId, isChecked ->
            if(isChecked) {
                when(checkedId) {
                    R.id.diesel -> selectedFuel = findViewById<MaterialButton>(R.id.diesel).text.toString()
                    R.id.gasoil -> selectedFuel = findViewById<MaterialButton>(R.id.gasoil).text.toString()
                    R.id.hybrid -> selectedFuel = findViewById<MaterialButton>(R.id.hybrid).text.toString()
                }
            }
            else {
                selectedFuel = ""
            }
        }

        findViewById<Button>(R.id.btnFuelNext).setOnClickListener {
            if(selectedFuel != ""){

            }else{
                Toast.makeText(this, "No item selected!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}