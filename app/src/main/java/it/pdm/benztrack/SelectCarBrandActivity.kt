package it.pdm.benztrack

import android.content.Intent
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SelectCarBrandActivity : AppCompatActivity() {
    private var selectedBrand = ""
    val url = URL("https://car-data.p.rapidapi.com/cars/makes")
    var brands : Array<String> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_car_brand)
        Thread(getBrands()).start()

        //getBrands()
        //TODO: Get data from API
        /*val users = arrayOf(
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
        )*/

        val listView = findViewById<ListView>(R.id.listViewCarBrands)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, brands)
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

    inner class getBrands() :  Runnable {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://car-data.p.rapidapi.com/cars/makes")
            .get()
            .addHeader("X-RapidAPI-Host", "car-data.p.rapidapi.com")
            .addHeader("X-RapidAPI-Key", "86d027cd9fmshbff559a3e923fe4p19337bjsn207fd2c9a6b5")
            .build()
        var brandsList  = mutableListOf<String>()

        override fun run() {


            /*client.newCall(request).execute().use { response ->
                //if (!response.isSuccessful) throw IOException("Unexpected code $response")

                for (element in response.body()?.string().toString()) {
                    Log.d("RISPOSTA1", element.toString())
                }

                reply = arrayOf(response.body()?.string().toString())
                println(reply.toString())
                Log.d("RISPOSTA2", reply.toString())
                for (element in reply) {
                    println(element)
                    Log.d("RISPOSTA3", element)
                }
            }*/
            Log.d("RISPOSTA", "DENTRO GET")
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Log.d("RISPOSTA", "DENTRO ON FAILURE")
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")
                        var jsonArr : JSONArray = JSONObject(response.body()?.string()).getJSONArray("makes")

                        Log.d("RISPOSTA", "DENTRO ON RESPONSE")
                        for (element in 0 until jsonArr.length()) {
                            brandsList.add(element.toString())
                            Log.d("RISPOSTA", element.toString())
                        }
                        /*
                            println(reply.toString())
                            Log.d("RISPOSTA2", reply.toString())
                            for (element in reply) {
                                println(element)
                                Log.d("RISPOSTA3", element)
                            }*/
                        brands = brandsList.toTypedArray()
                    }
                }
            })
        }

    }
}





