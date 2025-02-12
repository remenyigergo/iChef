package com.example.ichef.clients.apis.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ichef.clients.apis.ApiState
import com.example.ichef.clients.apis.MyRecipesApi
import com.example.ichef.clients.models.MyRecipes.MyRecipeResult
import com.example.ichef.constants.Constants
import com.example.ichef.di.modules.MockApi
import com.example.ichef.mappers.MyRecipeDtoBlaMapper
import com.example.ichef.models.activities.more.MyRecipe
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

    private val _myRecipesApiState = MutableStateFlow<ApiState<ArrayList<MyRecipe>>>(ApiState.Loading)
    val apiState: StateFlow<ApiState<ArrayList<MyRecipe>>> = _myRecipesApiState

    fun getUserRecipes(userId: Long) : Response<ArrayList<MyRecipeResult>> {
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

                    val mapped = data.map { recipeDto ->
                        MyRecipeDtoBlaMapper.ToBla(recipeDto)
                    }.toCollection(ArrayList())

                    _myRecipesApiState.value = ApiState.Success(mapped)

                    Response.success(200, mapped)
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