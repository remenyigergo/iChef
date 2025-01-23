package com.example.ichef.clients.apis.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ichef.clients.apis.ApiState
import com.example.ichef.clients.apis.MyRecipesApi
import com.example.ichef.clients.models.MyRecipes.MyRecipesResultItem
import com.example.ichef.constants.Constants
import com.example.ichef.di.modules.MockApi
import com.example.ichef.models.activities.more.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MyRecipesApiViewModel @Inject constructor() : ViewModel() {

    @Inject
    @MockApi
    lateinit var client: MyRecipesApi

    private val _myRecipesApiState = MutableStateFlow<ApiState<ArrayList<Recipe>>>(ApiState.Loading)
    val apiState: StateFlow<ApiState<ArrayList<Recipe>>> = _myRecipesApiState

    fun getUserRecipes(userId: Long) : Response<ArrayList<Recipe>> {
        viewModelScope.launch {
            _myRecipesApiState.value = ApiState.Loading
            try {
                // Simulate API Call
                val response = client.getMyRecipes(userId)
                // Simulate a delay
                kotlinx.coroutines.delay(Constants.recipeApiDelay)

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    Log.i("MyRecipesApiViewModel", "getUserRecipes result: $data")
                    _myRecipesApiState.value = ApiState.Success(data)
                } else {
                    _myRecipesApiState.value =
                        ApiState.Error("Failed to load data. Error code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MyRecipesApiViewModel", "getUserRecipes: $e", )
                _myRecipesApiState.value = ApiState.Error("Failed to load data. Please try again.")
            }
        }

        return Response.error(50000, ResponseBody.create(null, "Generic Error"))
    }
}