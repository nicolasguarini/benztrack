package it.pdm.benztrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView

class DashboardActivity : AppCompatActivity() {
    private val rotateOpen: Animation by lazy{ AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim)}
    private val rotateClose: Animation by lazy{AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim)}
    private val fromBottom: Animation by lazy{AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim)}
    private val toBottom: Animation by lazy{AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim)}
    private lateinit var fabAddMaintenance: FloatingActionButton
    private lateinit var fabAddExpense: FloatingActionButton
    private lateinit var fabAddRefuel: FloatingActionButton
    private lateinit var fabAddTax: FloatingActionButton
    private lateinit var fabAddInsurance: FloatingActionButton
    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        loadFragment(HomeFragment())
        fabAddMaintenance = findViewById(R.id.fabAddMaintenance)
        fabAddExpense = findViewById(R.id.fabAddExpense)
        fabAddRefuel = findViewById(R.id.fabAddRefuel)
        fabAddInsurance = findViewById(R.id.fabAddInsurance)
        fabAddTax = findViewById(R.id.fabAddTax)

        findViewById<NavigationBarView>(R.id.bottomNav).setOnItemSelectedListener {
            when(it.itemId){
                R.id.homeNavItem -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.expenseListNavItem -> {
                    loadFragment(ExpenseListFragment())
                    true
                }
                else -> false
            }
        }

        fabAddExpense.setOnClickListener{
            onAddButtonClick()
        }

        fabAddMaintenance.setOnClickListener{
            startActivity(Intent(this, AddMaintenanceActivity::class.java))
        }

        fabAddRefuel.setOnClickListener {
            startActivity(Intent(this, AddRefuelActivity::class.java))
        }

        fabAddTax.setOnClickListener{
            startActivity(Intent(this, AddTaxActivity::class.java))
        }

        fabAddInsurance.setOnClickListener {
            startActivity(Intent(this, AddInsuranceActivity::class.java))
        }
    }

    private fun onAddButtonClick() {
        setVisibility()
        setAnimation()
        setClickable()
        clicked = !clicked
    }

    private fun setVisibility(){
        if(!clicked){
            fabAddMaintenance.visibility = View.VISIBLE
            fabAddRefuel.visibility = View.VISIBLE
            fabAddInsurance.visibility = View.VISIBLE
            fabAddTax.visibility = View.VISIBLE
        }else{
            fabAddMaintenance.visibility = View.INVISIBLE
            fabAddRefuel.visibility = View.INVISIBLE
            fabAddInsurance.visibility = View.INVISIBLE
            fabAddTax.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(){
        if(!clicked){
            fabAddRefuel.startAnimation(fromBottom)
            fabAddMaintenance.startAnimation(fromBottom)
            fabAddInsurance.startAnimation(fromBottom)
            fabAddTax.startAnimation(fromBottom)
            fabAddExpense.startAnimation(rotateOpen)
        }else{
            fabAddRefuel.startAnimation(toBottom)
            fabAddMaintenance.startAnimation(toBottom)
            fabAddInsurance.startAnimation(toBottom)
            fabAddTax.startAnimation(toBottom)
            fabAddExpense.startAnimation(rotateClose)
        }
    }

    private fun setClickable(){
        if(!clicked){
            fabAddRefuel.isClickable = true
            fabAddMaintenance.isClickable = true
            fabAddInsurance.isClickable = true
            fabAddTax.isClickable = true
        }else{
            fabAddRefuel.isClickable = false
            fabAddMaintenance.isClickable = false
            fabAddInsurance.isClickable = false
            fabAddTax.isClickable = false
        }
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.dashboardContainer,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}