package com.example.ichef.activities.more

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.ichef.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OptionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.options)

        val backButton = findViewById<ImageView>(R.id.back_button_options)
        backButton.setOnClickListener {
            // Go back to the previous page
            onBackPressedDispatcher.onBackPressed()
        }
    }
}