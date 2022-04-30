package it.pdm.benztrack

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SelectCarBrandActivity : AppCompatActivity() {
    private var selectedBrand = ""
    private val url = URL("https://car-data.p.rapidapi.com/cars/makes")
    private var brands : Array<String> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_car_brand)

        val service = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        service.execute(Runnable {
            getBrands()
            handler.post(Runnable { showListView() })
        })

        findViewById<Button>(R.id.btnProducerNext).setOnClickListener {
            if (selectedBrand != "") {
                val intent = Intent(this, SelectCarModelActivity::class.java)
                intent.putExtra("selectedBrand", selectedBrand)
                startActivity(intent)
            } else {
                Toast.makeText(this, "No item selected!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getBrands(){
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("X-RapidAPI-Host", "car-data.p.rapidapi.com")
                .addHeader("X-RapidAPI-Key", "09b1cc4c05msh7ee26c674f810e5p1df3e2jsn2e0a7d20d96b")
                .build()

            val body = client.newCall(request).execute().body?.string()
            this.brands = GsonBuilder().create().fromJson(body, Array<String>::class.java)
            this.brands.sort()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun showListView(){
        findViewById<ProgressBar>(R.id.progressBarCarBrands).visibility = View.GONE

        val listView = findViewById<ListView>(R.id.listViewCarBrands)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, brands)
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        listView.visibility = View.VISIBLE

        val textFilter = findViewById<EditText>(R.id.producerFilter)
        textFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                adapter.filter.filter(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        listView.setOnItemClickListener { adapterView, _, position, _ ->
            selectedBrand = adapterView.getItemAtPosition(position).toString()
        }
    }
}


