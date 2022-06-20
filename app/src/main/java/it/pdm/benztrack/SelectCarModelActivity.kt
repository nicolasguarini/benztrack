package it.pdm.benztrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.Executors

class SelectCarModelActivity : AppCompatActivity() {
    private var selectedModel = ""
    private var models : Array<String> = emptyArray()
    private var url = "https://car-data.p.rapidapi.com/cars?limit=50&make="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_car_model)

        val selectedBrand = intent.getStringExtra("selectedBrand").toString()
        if(selectedBrand != ""){
            val tvSelectCarModel = findViewById<TextView>(R.id.textViewSelectCarModel)
            url += "$selectedBrand&page=0"
            tvSelectCarModel.text = tvSelectCarModel.text.toString().replace("auto", selectedBrand)
        }

        val service = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        service.execute {
            getModels()
            handler.post { showListView() }
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

    private fun getModels() {
        var responseResult = true
        val pageModels = mutableListOf<String>()
        var pageIndex = 0
        val key = BuildConfig.RAPIDAPI_KEY

        try {
            while(responseResult) {
                val client = OkHttpClient()
                url = url.substring(0, url.length-1) + pageIndex.toString()
                val request = Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("X-RapidAPI-Host", "car-data.p.rapidapi.com")
                    .addHeader("X-RapidAPI-Key", key)
                    .build()
                val body = client.newCall(request).execute().body?.string()

                if (body != "[]") {
                    var parsedData: Array<CarInfo>
                    try{
                        parsedData = GsonBuilder().create().fromJson(body, Array<CarInfo>::class.java)
                    }catch(ex: Exception){
                        ex.printStackTrace()
                        break
                    }

                    for (element in parsedData) {
                        pageModels.add(element.model)
                    }
                } else {
                    responseResult = false
                }

                pageIndex++
                Thread.sleep(700) //API requests per seconds restrictions on free plan
            }

            this.models = pageModels.distinct().toTypedArray() //clears duplicates
            this.models.sort()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    data class CarInfo(@SerializedName("model")  var model:String)

    private fun showListView(){
        findViewById<ProgressBar>(R.id.progressBarCarModels).visibility = View.GONE
        val listView = findViewById<ListView>(R.id.listViewCarModels)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, models)
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        listView.visibility = View.VISIBLE

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
