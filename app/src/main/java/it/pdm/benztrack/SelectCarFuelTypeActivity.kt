package it.pdm.benztrack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SelectCarFuelTypeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_car_fuel_type)

        val selectedBrand = intent.getStringExtra("selectedBrand").toString()
        val selectedModel = intent.getStringExtra("selectedModel").toString()

        //TODO: Implement logic
    }
}