package it.pdm.benztrack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class SelectCarBrandActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_car_brand)

        val users = arrayOf(
            "BMW",
            "Audi",
            "Toyota",
            "Honda",
            "Hyundai",
            "Mazda",
            "Maserati",
            "FIAT",
            "Opel",
            "Renault",
            "Ferrari",
            "Ford",
            "Skoda",
            "Bentley",
            "Jaguar",
            "Mercedes",
            "McLaren",
            "Subaru",
            "Nissan",
            "Dodge"
        )

        val listView = findViewById<ListView>(R.id.listViewCarBrands)
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, users)
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
    }
}