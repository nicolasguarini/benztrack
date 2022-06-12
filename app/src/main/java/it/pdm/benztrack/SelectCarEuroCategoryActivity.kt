package it.pdm.benztrack

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import it.pdm.benztrack.data.AppDatabase
import it.pdm.benztrack.data.Car
import it.pdm.benztrack.data.CarDao
import java.util.concurrent.Executors

class SelectCarEuroCategoryActivity : AppCompatActivity() {
    private lateinit var selectedBrand: String
    private lateinit var selectedModel: String
    private lateinit var selectedFuel: String
    private lateinit var selectedEuro: String

    private lateinit var db: AppDatabase
    private lateinit var carDao: CarDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_car_euro_category)

        selectedBrand = intent.getStringExtra("selectedBrand").toString()
        selectedModel = intent.getStringExtra("selectedModel").toString()
        selectedFuel = intent.getStringExtra("selectedFuel").toString()
        selectedEuro = ""

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

        db = AppDatabase.getDatabase(applicationContext)
        carDao = db.carDao()

        findViewById<Button>(R.id.btnEuroNext).setOnClickListener { registerCar() }
    }

    private fun registerCar(){
        val sharedPref = getSharedPreferences("it.pdm.benztrack", Context.MODE_PRIVATE)
        if(selectedEuro != ""){
            val newCar = Car(0, selectedBrand, selectedModel, selectedFuel, selectedEuro)

            val service = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            service.execute {
                val carId = carDao.insertCar(newCar)

                handler.post {
                    sharedPref.edit()
                        .putBoolean("firstStart", false)
                        .putString("selectedCarBrand", selectedBrand)
                        .putString("selectedCarModel", selectedModel)
                        .putString("selectedCarFuel", selectedFuel)
                        .putString("selectedCarEuro", selectedEuro)
                        .putLong("selectedCarId", carId)
                        .apply()

                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }
            }
        }else{
            Toast.makeText(this, "No item selected!", Toast.LENGTH_SHORT).show()
        }
    }
}