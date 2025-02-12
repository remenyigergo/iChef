package com.example.ichef.activities.more

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ichef.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.profile)

        val backButton = findViewById<ImageView>(R.id.back_button_profile)
        backButton.setOnClickListener {
            // Go back to the previous page
            onBackPressedDispatcher.onBackPressed()
        }

        val myRecipesButton = findViewById<Button>(R.id.my_recipes_button)
        myRecipesButton.setOnClickListener {
            val intent = Intent(this, RecipeActivity::class.java)
//                intent.putStringArrayListExtra("ingredients_list", ingredients)
//                intent.putStringArrayListExtra("stores", GetStoresNames())
            myRecipesIntent.launch(intent)
        }
    }

    private val myRecipesIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

    }
}