package com.example.ichef.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.ichef.R
import com.example.ichef.fragments.HomeFragment
import com.example.ichef.fragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge() to have a full top bar to edge. in future this can be enabled and top bar needs deletion.
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // If savedInstanceState is null, it means the Activity is starting for the first time
        val homeFragment = HomeFragment()
        val searchFragment = SearchFragment()
        if (savedInstanceState == null) {
            loadFragment(homeFragment)
        }

        // Get the bottom navbar and set each button to load the corresponding fragment
        val bottomNav : BottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener {
            try {
                when (it.itemId) {
                    R.id.home_button -> {
                        loadFragment(homeFragment)
                        true
                    }
                    R.id.search_button -> {
                        loadFragment(searchFragment)
                        true
                    }
                    else -> {
                        false
                    }
                }
            } catch (e : Exception) {
                throw e;
            }

        }

    }

    // Loads up a given fragment
    private fun loadFragment(fragment : Fragment) {

        if(fragment!=null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainerView,fragment)
            transaction.setReorderingAllowed(true)
            transaction.commit()
        } else{
            throw Exception("Fragment was null. Cannot load.");
        }

    }

}