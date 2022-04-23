package it.pdm.benztrack

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        findViewById<Button>(R.id.btnWelcomeNext).setOnClickListener {
            val userName = findViewById<EditText>(R.id.editTextUserName).text.toString()
            if(userName != ""){
                getSharedPreferences("it.pdm.benztrack", Context.MODE_PRIVATE)
                    .edit()
                    .putString("userName", userName)
                    .apply()

                val intent = Intent(this, SelectCarBrandActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Enter a valid name!", Toast.LENGTH_SHORT).show()
            }
        }

    }
}