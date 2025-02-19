package com.example.ichef.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ichef.R
import com.example.ichef.adapters.SearchAdapter
import com.example.ichef.clients.apis.ApiState
import com.example.ichef.clients.apis.viewmodels.SearchApiViewModel
import com.example.ichef.models.activities.search.SearchRecipe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchResultActivity : AppCompatActivity() {

    private val searchApi: SearchApiViewModel by viewModels()
    lateinit var searchAdapter: SearchAdapter
    private var isLoading = false
    private var currentPage = 1
    private var isLastPage = false
    private val pageSize = 6
    private val recipeList: ArrayList<SearchRecipe> = arrayListOf()
    private var isFirstLoad = true  // Track if it's the first API call
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_result)

        // Find views
        val recyclerView: RecyclerView = findViewById(R.id.searchResultRecyclerView)
//        swipeRefreshLayout = findViewById(R.id.search_pull_to_refresh)

        // Initialize adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchAdapter = SearchAdapter(this, recipeList)
        recyclerView.adapter = searchAdapter

        val view: CoordinatorLayout = findViewById(R.id.search_result_layout)

        // Initial API call
        fetchRecipes(view)

        // Handle Pull-to-Refresh
//        swipeRefreshLayout.setOnRefreshListener {
//            refreshRecipes(view)
//        }

        // Add ScrollListener for infinite scroll
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                        firstVisibleItemPosition >= 0 &&
                        totalItemCount >= pageSize
                    ) {
                        // Check if there's actually more data to load before showing the loading footer
                        if (searchApi.hasMorePages()) {
                            searchAdapter.addLoadingFooter()
                            loadMoreRecipes()

                        } else {
                            isLastPage = true // Mark as the last page to prevent unnecessary calls
                            Log.w("SearchResultActivity", "No more pages to load")
                        }
                    }
                } else {
                    Log.w("SearchResultActivity", "End of page")
                }
            }
        })

        val backButton = findViewById<ImageView>(R.id.back_button_search_result)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun fetchRecipes(view: View) {
        HandleSearchApiCall(view, "RECIPE TITLE HERE", currentPage) { result ->
            recipeList.clear()
            recipeList.addAll(result as ArrayList<SearchRecipe>)
            searchAdapter.notifyDataSetChanged()
        }
    }

    private fun HandleSearchApiCall(
        rootView: View,
        title: String,
        page: Int,
        onSuccess: (data: Any) -> Unit
    ) {
        val loadingView = rootView.findViewById<ProgressBar>(R.id.loadingView)
        val errorView = rootView.findViewById<LinearLayout>(R.id.errorView)
        val retryButton = rootView.findViewById<Button>(R.id.retryButton)

        lifecycleScope.launch {
            searchApi.apiState.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        if (isFirstLoad) {
                            // Show full-screen loading only for the first load
                            loadingView?.visibility = View.VISIBLE
                            errorView?.visibility = View.GONE
                        } else if(currentPage == 3) {  //TODO delete the currentPage check later. It is just mock to stop the loading as we reach the mocked last page (3)
                            Log.w("SearchResultActivity", "Page 3 is reached. Removing loading icon.")
                            searchAdapter.removeLoadingFooter()
                        } else {
                            searchAdapter.addLoadingFooter()
                        }
                    }

                    is ApiState.Success -> {
                        isLoading = false
                        loadingView?.visibility = View.GONE
                        errorView?.visibility = View.GONE

                        val loadedContent = state.data
                        if (loadedContent.size < pageSize) {
                            isLastPage = true
                        }

                        searchAdapter.removeLoadingFooter()
                        onSuccess(loadedContent)

                        isFirstLoad = false // First load is complete
                    }

                    is ApiState.Error -> {
                        isLoading = false
                        loadingView?.visibility = View.GONE
                        errorView?.visibility = View.VISIBLE
                        searchAdapter.removeLoadingFooter()
                    }
                }
            }
        }

        retryButton?.setOnClickListener {
            searchApi.searchRecipes(title, page)
        }

        // Fetch data initially
        searchApi.searchRecipes(title, page)
    }

    private fun refreshRecipes(view: View) {
        swipeRefreshLayout.isRefreshing = true
        currentPage = 1
        isLastPage = false
        isFirstLoad = true

        searchApi.reload()
        recipeList.clear()

        HandleSearchApiCall(view, "RECIPE TITLE HERE", currentPage) { result ->
            recipeList.addAll(result as ArrayList<SearchRecipe>)
            searchAdapter.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun loadMoreRecipes() {
        isLoading = true
        currentPage += 1
        Log.w("SearchResultActivity", "Loading page $currentPage")
        searchApi.loadNextPage("RECIPE TITLE HERE", currentPage)
    }
}