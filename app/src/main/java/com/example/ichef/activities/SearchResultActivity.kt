package com.example.ichef.activities

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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.R
import com.example.ichef.adapters.SearchAdapter
import com.example.ichef.clients.apis.ApiState
import com.example.ichef.clients.apis.viewmodels.SearchApiViewModel
import com.example.ichef.models.activities.more.MyRecipe
import com.example.ichef.models.activities.search.SearchRecipe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchResultActivity : AppCompatActivity() {

    private val searchApi: SearchApiViewModel by viewModels()
    lateinit var searchAdapter: RecyclerView.Adapter<SearchAdapter.SearchResultViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_result)

        val recyclerView: RecyclerView = findViewById(R.id.searchResultRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize empty list and adapter
        val recipeList: ArrayList<SearchRecipe> = arrayListOf()
        searchAdapter = SearchAdapter(this, recipeList)
        recyclerView.adapter = searchAdapter

        // Fetch data and handle the API response
        val view: CoordinatorLayout = findViewById(R.id.search_result_layout)
        HandleSearchApiCall(view, "RECIPE TITLE HERE") { result ->
            // Update the list and notify the adapter
            recipeList.clear()
            recipeList.addAll(result as ArrayList<SearchRecipe>)
            searchAdapter.notifyDataSetChanged()

            Log.d("RecipeActivity", "Updated recipeList: $recipeList")
        }

        val backButton = findViewById<ImageView>(R.id.back_button_search_result)
        backButton.setOnClickListener {
            // Go back to the previous page
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun HandleSearchApiCall(
        rootView: View,
        title: String,
        onSuccess: (data: Any) -> Unit
    ) {
        val loadingView = rootView.findViewById<ProgressBar>(R.id.loadingView)
        val successView = rootView.findViewById<TextView>(R.id.successView)
        val errorView = rootView.findViewById<LinearLayout>(R.id.errorView)
        val retryButton = rootView.findViewById<Button>(R.id.retryButton)

        lifecycleScope.launch {
            searchApi.apiState.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        Log.d("SearchResultActivity", "searchResult State: Loading")
                        loadingView?.visibility = View.VISIBLE
                        successView?.visibility = View.GONE
                        errorView?.visibility = View.GONE
                    }

                    is ApiState.Success -> {
                        Log.d("SearchResultActivity", "searchResult State: Success")
                        loadingView?.visibility = View.GONE
                        errorView?.visibility = View.GONE

                        onSuccess(state.data)
                    }

                    is ApiState.Error -> {
                        Log.d("SearchResultActivity", "searchResult State: Error")
                        loadingView?.visibility = View.GONE
                        successView?.visibility = View.GONE
                        errorView?.visibility = View.VISIBLE
                    }
                }
            }
        }

        retryButton?.setOnClickListener {
            searchApi.searchRecipes(title)
        }

        // Fetch data initially
        searchApi.searchRecipes(title)
    }
}