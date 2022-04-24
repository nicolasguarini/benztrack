package it.pdm.benztrack

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SelectCarBrandActivity : AppCompatActivity() {
    private var selectedBrand = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_car_brand)

        val users = arrayOf( //TODO: Get data from API
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
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, users)
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE

        val textFilter = findViewById<EditText>(R.id.producerFilter)
        textFilter.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                adapter.filter.filter(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        listView.setOnItemClickListener { adapterView, _, position, _ ->
            selectedBrand = adapterView.getItemAtPosition(position).toString()
        }

        findViewById<Button>(R.id.btnProducerNext).setOnClickListener {
            if(selectedBrand != ""){
                val intent = Intent(this, SelectCarModelActivity::class.java)
                intent.putExtra("selectedBrand", selectedBrand)
                startActivity(intent)
            }else{
                Toast.makeText(this, "No item selected!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

