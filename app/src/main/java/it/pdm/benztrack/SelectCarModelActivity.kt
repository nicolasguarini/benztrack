package it.pdm.benztrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*

class SelectCarModelActivity : AppCompatActivity() {
    private var selectedModel = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_car_model)

        val selectedBrand = intent.getStringExtra("selectedBrand").toString()
        if(selectedBrand != ""){
            val tvSelectCarModel = findViewById<TextView>(R.id.textViewSelectCarModel)
            tvSelectCarModel.text = tvSelectCarModel.text.toString().replace("auto", selectedBrand)
        }

        val models = arrayOf( //TODO: Get data from API
            "Classe A",
            "Classe A Sedan",
            "CLA",
            "Classe S",
            "Classe E",
            "Classe G",
            "GLC",
            "GLA",
            "GLE",
            "AMG GT",
            "Classe B",
            "GLS",
            "GLB",
            "Classe V",
            "EQC",
            "EQA",
            "EQV"
        )

        val listView = findViewById<ListView>(R.id.listViewCarModels)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, models)
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE

        val textFilter = findViewById<EditText>(R.id.modelFilter)
        textFilter.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                adapter.filter.filter(charSequence.toString())
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        listView.setOnItemClickListener{ adapterVIew, _, position, _ ->
            selectedModel = adapterVIew.getItemAtPosition(position).toString()
        }

        findViewById<Button>(R.id.btnModelNext).setOnClickListener {
            if(selectedModel != ""){
                val intent = Intent(this, SelectCarFuelTypeActivity::class.java)
                intent.putExtra("selectedBrand", selectedBrand)
                intent.putExtra("selectedModel", selectedModel)
                startActivity(intent)
            }else{
                Toast.makeText(this, "No item selected!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}