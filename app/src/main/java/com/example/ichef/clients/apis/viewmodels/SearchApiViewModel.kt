package com.example.ichef.clients.apis.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ichef.clients.apis.ApiState
import com.example.ichef.clients.apis.SearchApi
import com.example.ichef.clients.models.Search.SearchResult
import com.example.ichef.constants.Constants
import com.example.ichef.di.modules.MockApi
import com.example.ichef.mappers.SearchResultDtoBlaMapper
import com.example.ichef.models.activities.search.SearchRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchApiViewModel @Inject constructor() : ViewModel() {

    @Inject
    @MockApi
    lateinit var client: SearchApi

    private val _searchApiState = MutableStateFlow<ApiState<ArrayList<SearchRecipe>>>(ApiState.Loading)
    val apiState: StateFlow<ApiState<ArrayList<SearchRecipe>>> = _searchApiState

    private var currentPage = 1
    private var isLoading = false
    private val pageSize = 3
    private val allRecipes = ArrayList<SearchRecipe>()

    fun searchRecipes(title: String, page: Int = 1) {
        if (isLoading) return

        /*
        * HACK FOR IMITATING THERE IS ONLY 4 PAGES
        * */
        if (page == 3) {
            return
        }

        isLoading = true
        _searchApiState.value = ApiState.Loading

        viewModelScope.launch {
            try {
                val response = client.search(title, page, pageSize)
                kotlinx.coroutines.delay(Constants.recipeApiDelay)

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    Log.i("SearchApiViewModel", "searchRecipes Page $page result: $data")

                    val mapped = data.map { recipeDto ->
                        SearchResultDtoBlaMapper.ToBla(recipeDto)
                    }

                    if (page == 1) {
                        allRecipes.clear() // Clear only on first load
                    }

                    allRecipes.addAll(mapped)

                    _searchApiState.value = ApiState.Success(ArrayList(allRecipes))

                    currentPage = page
                } else {
                    _searchApiState.value = ApiState.Error("Failed to load data. Error code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("SearchApiViewModel", "searchRecipes: $e")
                _searchApiState.value = ApiState.Error("Failed to load data. Please try again.")
            } finally {
                isLoading = false
            }
        }
    }

    fun loadNextPage(title: String, page : Int) {
        searchRecipes(title, page)
    }
}
