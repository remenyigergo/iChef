package com.example.ichef.clients.apis.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ichef.adapters.SharedData
import com.example.ichef.adapters.interfaces.StoreCheckboxAdapter
import com.example.ichef.clients.apis.ApiState
import com.example.ichef.clients.apis.ShoppingListApi
import com.example.ichef.clients.models.ShoppingListResultItem
import com.example.ichef.constants.Constants
import com.example.ichef.database.ShoppingDataManagerImpl
import com.example.ichef.database.interfaces.ShoppingDataManager
import com.example.ichef.di.modules.MockApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ShoppingListApiViewModel @Inject constructor(
    @MockApi private val shoppingListApi: ShoppingListApi,
    private val checkBoxesAdapter: StoreCheckboxAdapter,
    private val sharedData: SharedData,
) : ViewModel() {

    private val _shoppingListApiState = MutableStateFlow<ApiState<ArrayList<ShoppingListResultItem>>>(ApiState.Loading)
    val apiState: StateFlow<ApiState<ArrayList<ShoppingListResultItem>>> = _shoppingListApiState

    fun fetchApiData() : Response<ArrayList<ShoppingListResultItem>> {
        viewModelScope.launch {
            _shoppingListApiState.value = ApiState.Loading
            try {
                // Simulate API Call
                val response = shoppingListApi.getShoppingList(1)
                // Simulate a delay
                kotlinx.coroutines.delay(Constants.shoppingApiDelay)
                sharedData.loadStoresFromDatabase()
                checkBoxesAdapter.notifyDataSetChanged()

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    Log.i("ShoppingListApiViewModel", "ShoppingListApi result: $data")
                    _shoppingListApiState.value = ApiState.Success(data)
                } else {
                    _shoppingListApiState.value =
                        ApiState.Error("Failed to load data. Error code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ShoppingListApiViewModel", "fetchApiData: $e", )
                _shoppingListApiState.value = ApiState.Error("Failed to load data. Please try again.")
            }
        }

        return Response.error(50000, ResponseBody.create(null, "Generic Error"))
    }


}