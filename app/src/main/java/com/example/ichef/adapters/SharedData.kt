package com.example.ichef.adapters

import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ichef.R
import com.example.ichef.database.ShoppingDataManager
import com.example.ichef.models.StoreCheckBox
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class SharedData @Inject constructor(
    private var app: Application,
    private var storeDb : ShoppingDataManager
) : ViewModel() {
    var stores : MutableList<StoreCheckBox> = mutableListOf()
    lateinit var emptyPageView: ConstraintLayout

    var addButtonClicked: Boolean = false

    private val _allChecked = MutableLiveData(false)
    val allChecked: LiveData<Boolean> get() = _allChecked

    private val _tickedCount = MutableLiveData(0)
    val tickedCount: LiveData<Int> get() = _tickedCount

    /*
    * SETTERS
    * */
    fun setTickedCount(value: Int) {
        _tickedCount.value = value
    }

    fun setAllChecked(value: Boolean) {
        _allChecked.value = value
    }

    /*
    * GETTERS
    * */
    fun getStoresSize(): Int {
        return stores.size
    }

    fun isAllChecked(): Boolean {
        return _allChecked.value ?: false
    }

    /*
    * TickedCount handlers
    * */
    fun incrementTick() {
        _tickedCount.value = (_tickedCount.value ?: 0) + 1
    }

    fun decreaseTick() {
        _tickedCount.value = (_tickedCount.value ?: 0) - 1
    }

    fun SetEmptyPageVisibilty() {
        if (stores.size == 0) {
            emptyPageView.visibility = View.VISIBLE
        } else {
            emptyPageView.visibility = View.INVISIBLE
        }
    }

    fun checkIngredient(storePosition: Int, ingredientPosition: Int, value: Boolean) {
        stores[storePosition].ingredients[ingredientPosition].isChecked = value
    }

    fun removeStore(storeIndex: Int) {
        val storeName = stores[storeIndex].storeName
        storeDb.deleteStoreWithIngredientsByName(storeName)
        Toast.makeText(app.applicationContext,
            app.applicationContext.getString(R.string.store_deleted_successfully), Toast.LENGTH_SHORT).show()
    }

    fun removeIngredient(storeName: String, ingredient: String) {
        storeDb.deleteIngredientFromStore(storeName, ingredient)
    }
}