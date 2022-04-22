package it.pdm.benztrack

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("it.pdm.benztrack", Context.MODE_PRIVATE)

        if(sharedPref.getBoolean("firstStart", true)){
            println("First Start")
            //TODO: Lanciare l'activity di benvenuto
        }else{
            println("Normal Launch")
            //TODO: Lanciare l'activity della dashboard
        }
    }
}