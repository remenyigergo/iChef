package com.example.ichef.activities.more

import RecipeAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.R
import com.example.ichef.clients.apis.ApiState
import com.example.ichef.clients.apis.viewmodels.MyRecipesApiViewModel
import com.example.ichef.models.activities.more.Recipe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipeActivity : AppCompatActivity() {

    private val myRecipesApi: MyRecipesApiViewModel by viewModels()

    lateinit var recipeAdapter: RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myrecipes)

        val recyclerView: RecyclerView = findViewById(R.id.recipeRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize empty list and adapter
        val recipeList: ArrayList<Recipe> = arrayListOf()
        recipeAdapter = RecipeAdapter(this, recipeList)
        recyclerView.adapter = recipeAdapter

        // Fetch data and handle the API response
        val view: CoordinatorLayout = findViewById(R.id.myrecipes)
        HandleMyRecipesApiCall(view, 1) { result ->
            // Update the list and notify the adapter
            recipeList.clear()
            recipeList.addAll(result as ArrayList<Recipe>)
            recipeAdapter.notifyDataSetChanged()

            Log.d("RecipeActivity", "Updated recipeList: $recipeList")
        }

        val backButton = findViewById<ImageView>(R.id.back_button_myrecipes)
        backButton.setOnClickListener {
            // Go back to the previous page
            onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun HandleMyRecipesApiCall(
        rootView: View,
        userId: Long,
        onSuccess: (data: Any) -> Unit
    ) {
        val loadingView = rootView.findViewById<ProgressBar>(R.id.loadingView)
        val successView = rootView.findViewById<TextView>(R.id.successView)
        val errorView = rootView.findViewById<LinearLayout>(R.id.errorView)
        val retryButton = rootView.findViewById<Button>(R.id.retryButton)

        lifecycleScope.launch {
            myRecipesApi.apiState.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        Log.d("RecipeActivity", "getUserRecipes State: Loading")
                        loadingView?.visibility = View.VISIBLE
                        successView?.visibility = View.GONE
                        errorView?.visibility = View.GONE
                    }

                    is ApiState.Success -> {
                        Log.d("RecipeActivity", "getUserRecipes State: Success")
                        loadingView?.visibility = View.GONE
                        errorView?.visibility = View.GONE

                        onSuccess(state.data)
                    }

                    is ApiState.Error -> {
                        Log.d("RecipeActivity", "getUserRecipes State: Error")
                        loadingView?.visibility = View.GONE
                        successView?.visibility = View.GONE
                        errorView?.visibility = View.VISIBLE
                    }
                }
            }
        }

        retryButton?.setOnClickListener {
            myRecipesApi.getUserRecipes(userId)
        }

        // Fetch data initially
        myRecipesApi.getUserRecipes(userId)
    }
}