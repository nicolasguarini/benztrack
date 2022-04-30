package it.pdm.benztrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.wait
import java.net.URL
import java.util.concurrent.Executors

class SelectCarModelActivity : AppCompatActivity() {
    private var selectedModel = ""
    private var url = "https://car-data.p.rapidapi.com/cars?limit=50&make="
    private var models : Array<String> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_car_model)

        val selectedBrand = intent.getStringExtra("selectedBrand").toString()
        if(selectedBrand != ""){
            val tvSelectCarModel = findViewById<TextView>(R.id.textViewSelectCarModel)
            this.url += "$selectedBrand&page=0"
            tvSelectCarModel.text = tvSelectCarModel.text.toString().replace("auto", selectedBrand)
        }

        val service = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        service.execute(Runnable {
            getModels()
            handler.post(Runnable {showListView()})
        })

        /*val models = arrayOf( //TODO: Get data from API
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
        )*/

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

    private fun getModels()
    {
        var responseResult = true
        var pageModels = mutableListOf<String>()
        var pageIndex = 0//pagina della richiesta
        try {
            while(responseResult) {
                Log.d("PROVA", pageIndex.toString())
                val client = OkHttpClient()
                url = url.substring(0, url.length-1) + pageIndex.toString()
                Log.d("PROVA", url)
                pageIndex++

                val request = Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("X-RapidAPI-Host", "car-data.p.rapidapi.com")
                    .addHeader(
                        "X-RapidAPI-Key",
                        "09b1cc4c05msh7ee26c674f810e5p1df3e2jsn2e0a7d20d96b"
                    )
                    .build()

                Log.d("PROVA", "provo la richiesta " + (pageIndex-1).toString())
                val body = client.newCall(request).execute().body?.string()
                Log.d("PROVA", "la richiesta funziona " + (pageIndex-1).toString())

                if (body != "[]") {//se la richiesta dà risultato vuoto
                    Log.d("PROVA", "Siamo nell'if " + (pageIndex-1).toString())
                    var parsedData : Array<CarInfo> = GsonBuilder().create().fromJson(body, Array<CarInfo>::class.java)
                    Log.d("PROVA", "Ha parsato " + (pageIndex-1).toString())

                    for (element in parsedData) {//salva i dati dell'array nella lista
                        pageModels.add(element.model)
                        Log.d("AGGIUNTO", element.model)
                    }
                } else {
                    Log.d("PROVA", "è vuoto " + pageIndex.toString())
                    responseResult = false
                }
                Log.d("PROVA", "fineee " + (pageIndex-1).toString())
            }
            clearArray(pageModels)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    data class CarInfo(//classe che serve a memorizzare il dato del modello essendo un campo, l'unico modo che ho trovato
        @SerializedName("model")  var model:String
    )

    private fun clearArray(list : MutableList<String>)//elimina doppioni e passa all'array principale tutto pulito
    {
        this.models = list.distinct().toTypedArray()
    }

    private fun showListView()
    {
        Log.d("PROVA", "Nel showListView")
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
    }
}
