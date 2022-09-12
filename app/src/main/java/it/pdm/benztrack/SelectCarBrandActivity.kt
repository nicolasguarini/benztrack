package it.pdm.benztrack

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL
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
        service.execute {
            getBrands()
            handler.post { showListView() }
        }

        findViewById<Button>(R.id.btnProducerNext).setOnClickListener {
            if (selectedBrand != "") {
                val intent = Intent(this, SelectCarModelActivity::class.java)
                intent.putExtra("selectedBrand", selectedBrand)
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.no_item_selected), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getBrands(){
        val key = BuildConfig.RAPIDAPI_KEY
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("X-RapidAPI-Host", "car-data.p.rapidapi.com")
                .addHeader("X-RapidAPI-Key", key)
                .build()

            val body = client.newCall(request).execute().body?.string()
            brands = GsonBuilder().create().fromJson(body, Array<String>::class.java)
            brands.sort()
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


