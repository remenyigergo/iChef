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
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchApiViewModel @Inject constructor() : ViewModel() {

    @Inject
    @MockApi
    lateinit var client: SearchApi

    private val _searchApiState = MutableStateFlow<ApiState<ArrayList<SearchRecipe>>>(ApiState.Loading)
    val apiState: StateFlow<ApiState<ArrayList<SearchRecipe>>> = _searchApiState

    fun searchRecipes(title: String) : Response<ArrayList<SearchResult>> {
        viewModelScope.launch {
            _searchApiState.value = ApiState.Loading
            try {
                // Simulate API Call
                val response = client.search(title)
                // Simulate a delay
                kotlinx.coroutines.delay(Constants.recipeApiDelay)

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    Log.i("SearchApiViewModel", "searchRecipes result: $data")

                    val mapped = data.map { recipeDto ->
                        SearchResultDtoBlaMapper.ToBla(recipeDto)
                    }.toCollection(ArrayList())

                    _searchApiState.value = ApiState.Success(mapped)

                    Response.success(200, mapped)
                } else {
                    _searchApiState.value =
                        ApiState.Error("Failed to load data. Error code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("SearchApiViewModel", "searchRecipes: $e", )
                _searchApiState.value = ApiState.Error("Failed to load data. Please try again.")
            }
        }

        return Response.error(50000, ResponseBody.create(null, "Generic Error"))
    }
}