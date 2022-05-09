package it.pdm.benztrack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import it.pdm.benztrack.databinding.ActivityMainBinding

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        loadFragment(HomeFragment())

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

        findViewById<FloatingActionButton>(R.id.fabAddExpense).setOnClickListener {
            Toast.makeText(this, "FAB Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.dashboardContainer,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}