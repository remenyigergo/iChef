package com.example.ichef.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.ichef.R
import com.example.ichef.fragments.HomeFragment
import com.example.ichef.fragments.SearchFragment
import com.example.ichef.fragments.ShoppingFragmentImpl
import com.example.ichef.notifications.scheduler.AlarmScheduler
import com.example.ichef.notifications.channels.ShoppingListReminderNotificationManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

lateinit var fragments: List<Fragment>

@AndroidEntryPoint
class MainActivity @Inject constructor(
) : AppCompatActivity() {

    @Inject lateinit var ingredients: ArrayList<String>
    @Inject lateinit var shoppingFragment: ShoppingFragmentImpl
    @Inject lateinit var homeFragment: HomeFragment
    @Inject lateinit var searchFragment: SearchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ShoppingListReminderNotificationManager.createNotificationChannel(this)
        AlarmScheduler.scheduleDailyNotification(this, 20, 8, 30)

        fragments = listOf(homeFragment, searchFragment, shoppingFragment)

        // If savedInstanceState is null, it means the Activity is starting for the first time
        if (savedInstanceState == null) {
            loadFragment(homeFragment, R.id.fragmentContainerView)
        }

        // Get the bottom navbar and set each button to load the corresponding fragment
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener {
            try {
                when (it.itemId) {
                    R.id.home_button -> {
                        loadFragment(homeFragment, R.id.fragmentContainerView)
                        true
                    }

                    R.id.search_button -> {
                        loadFragment(searchFragment, R.id.fragmentContainerView)
                        true
                    }

                    R.id.list_button -> {
                        loadFragment(shoppingFragment, R.id.shoppingFragmentContainerView)
                        true
                    }

                    else -> {
                        false
                    }
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    // Loads up a given fragment
    private fun loadFragment(fragment: Fragment, containerId: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setReorderingAllowed(true)

        // Check if the fragment is already in the FragmentManager
        val existingFragment = supportFragmentManager.findFragmentByTag(fragment::class.java.name)

        if (existingFragment != null) {
            // If the fragment exists, show it
            transaction.show(existingFragment)
        } else {
            // If the fragment doesn't exist, add it to the FragmentManager with a tag
            transaction.add(containerId, fragment, fragment::class.java.name)
        }

        // Hide all other fragments
        supportFragmentManager.fragments.forEach { frg ->
            if (frg != existingFragment && frg != fragment) {
                transaction.hide(frg)
            }
        }

        // Commit the transaction
        transaction.commit()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle intent if needed
        intent?.let {
            // Check if the activity was launched via a notification
            val fragmentToOpen = intent.getStringExtra("fragment_to_open")
            if (fragmentToOpen != null && fragmentToOpen == "shoppingFragment") {
                loadFragment(shoppingFragment, R.id.shoppingFragmentContainerView)
            }
        }
    }
}