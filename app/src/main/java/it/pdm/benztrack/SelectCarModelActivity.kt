package it.pdm.benztrack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class SelectCarModelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_car_model)

        val selectedBrand = intent.getStringExtra("selectedBrand")

        //TODO: Implement SelectCarModelActivity logic
    }
}