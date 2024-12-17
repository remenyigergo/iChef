package com.example.ichef.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.ichef.R
import com.example.ichef.fragments.HomeFragment
import com.example.ichef.fragments.SearchFragment
import com.example.ichef.fragments.ShoppingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.BufferedReader
import java.io.InputStreamReader

val homeFragment = HomeFragment()
val searchFragment = SearchFragment()
lateinit var shoppingFragment: ShoppingFragment
lateinit var fragments: List<Fragment>

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

        //Read all ingredients for a cold-start update
        val ingredients = ArrayList<String>() // Create an ArrayList to hold the lines
        try {
            // Open file from assets
            val inputStream = assets.open("ingredients.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))

            // Read file line by line and add each line to the ArrayList
            reader.forEachLine { line ->
                ingredients.add(line)
            }
            shoppingFragment = ShoppingFragment(ingredients)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        fragments = listOf(homeFragment, searchFragment, shoppingFragment)

        // If savedInstanceState is null, it means the Activity is starting for the first time

        if (savedInstanceState == null) {
            loadFragment(homeFragment,R.id.fragmentContainerView)
        }

        // Get the bottom navbar and set each button to load the corresponding fragment
        val bottomNav : BottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener {
            try {
                when (it.itemId) {
                    R.id.home_button -> {
                        loadFragment(homeFragment,R.id.fragmentContainerView)
                        true
                    }
                    R.id.search_button -> {
                        loadFragment(searchFragment,R.id.fragmentContainerView)
                        true
                    }
                    R.id.list_button -> {
                        loadFragment(shoppingFragment,R.id.shoppingFragmentContainerView)
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
    private fun loadFragment(fragment : Fragment, containerId: Int) {
        if(fragment!=null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(containerId,fragment)
            transaction.setReorderingAllowed(true)

            fragments.forEach { frg ->
                if (frg == fragment) transaction.show(frg) else transaction.hide(frg)
            }

            transaction.commit()
        } else{
            throw Exception("Fragment was null. Cannot load.");
        }
    }

}