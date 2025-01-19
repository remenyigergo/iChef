package com.example.ichef.adapters

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ichef.R
import com.example.ichef.database.ShoppingDataManager
import com.example.ichef.models.IngredientCheckbox
import com.example.ichef.models.StoreCheckBox
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedData @Inject constructor(
    private val app: Application,
    private val storeDb: ShoppingDataManager
) : ViewModel() {

    private val _stores = MutableLiveData<MutableList<StoreCheckBox>>(mutableListOf())

    lateinit var emptyPageView: ConstraintLayout

    private val _allChecked = MutableLiveData(false)

    private val _tickedCount = MutableLiveData(0)
    val tickedCount: LiveData<Int> get() = _tickedCount

    fun updateStores(stores: MutableList<StoreCheckBox>) {
        _stores.value = stores
        updateEmptyPageVisibility()
    }

    fun updateEmptyPageVisibility() {
        if (::emptyPageView.isInitialized) {
            emptyPageView.visibility = if (_stores.value.isNullOrEmpty()) View.VISIBLE else View.INVISIBLE
        }
    }

    /*
    * SETTERS
    * */
    fun setTickedCount(value: Int) {
        _tickedCount.value = value
    }

    fun setAllChecked(value: Boolean) {
        _allChecked.value = value
    }

    fun setAllIngredientChecked() {
        Log.d("SharedData","TickedCount value ${_tickedCount.value}.")
        Log.d("SharedData","GetAllIngredientsCount value ${getAllIngredientsCount()}.")

        val ingredientsCount = getAllIngredientsCount()

        // between 0 and max
        if (_tickedCount.value!! > 0 && _tickedCount.value!! < ingredientsCount) {
            Log.d("SharedData","Turning all checkbox to ticked.")
            _allChecked.value = true
            setTickedCount(ingredientsCount)
        } else
        // all ticked
        if(_tickedCount.value == getAllIngredientsCount() || isAllChecked()) {
            Log.d("SharedData","All checked. Turning all checkbox to unticked.")
            _allChecked.value = false
            setTickedCount(0)
        // none ticked
        } else if (_tickedCount.value == 0) {
            _allChecked.value = true
            setTickedCount(ingredientsCount)
        }

        _stores.value?.forEach { store ->
            store.ingredients.forEach { ingredient -> ingredient.isChecked = _allChecked.value!! }
        }
        updateStores(_stores.value ?: mutableListOf())
    }

    fun addIngredientToStore(storeIndex: Int, ingredient: IngredientCheckbox) {
        _stores.value?.getOrNull(storeIndex)?.ingredients?.add(ingredient)
        updateStores(_stores.value ?: mutableListOf())
    }

    fun setStores(newStores: MutableList<StoreCheckBox>) {
        updateStores(newStores)
    }

    /*
    * GETTERS
    * */

    fun getAllStores(): List<StoreCheckBox> {
        return _stores.value ?: emptyList()
    }

    fun getStoresNames(): ArrayList<String> {
        return ArrayList(_stores.value?.map { it.storeName } ?: emptyList())
    }

    fun getStoresSize(): Int {
        return _stores.value?.size ?: 0
    }

    fun getAllIngredientsCount(): Int {
        return _stores.value?.sumOf { it.ingredients.size } ?: 0
    }

    fun isAllChecked(): Boolean {
        return _allChecked.value ?: false
    }

    fun getStoreByIndex(storeIndex: Int): StoreCheckBox? {
        return _stores.value?.getOrNull(storeIndex)
    }

    fun getIngredientCheckboxByIndex(storeIndex: Int, ingredientIndex: Int): IngredientCheckbox? {
        return _stores.value?.getOrNull(storeIndex)?.ingredients?.getOrNull(ingredientIndex)
    }

    fun findStoreIndex(predicate: (StoreCheckBox) -> Boolean): Int? {
        return _stores.value?.indexOfFirst(predicate).takeIf { it != -1 }
    }

    fun doesStoreContainIngredient(storeIndex: Int, ingredient: String): Boolean {
        return _stores.value?.getOrNull(storeIndex)?.ingredients?.any { it.title == ingredient } ?: false
    }

    /*
    * TickedCount handlers
    * */
    fun incrementTick() {
        _tickedCount.value = (_tickedCount.value ?: 0) + 1
        Log.d("SharedData","Increment tick. TickedCount now : ${_tickedCount.value}")
    }

    fun decreaseTick() {
        _tickedCount.value = (_tickedCount.value ?: 0) - 1
        Log.d("SharedData","Decrement tick. TickedCount now : ${_tickedCount.value}")
    }

    /*
    * Add a store to the list and notify observers
    */
    fun addStore(store: StoreCheckBox) {
        val currentList = _stores.value ?: mutableListOf()
        currentList.add(store)
        updateStores(currentList)
    }

    /*
    * Remove a store by index and notify observers
    */
    fun removeStore(storeIndex: Int) {
        _stores.value?.let { currentList ->
            if (storeIndex in currentList.indices) {
                val storeName = currentList[storeIndex].storeName
                storeDb.deleteStoreWithIngredientsByName(storeName)
                currentList.removeAt(storeIndex)
                updateStores(currentList)
                showToast(app.getString(R.string.store_deleted_successfully))
            }
        }
    }

    /*
    * Remove an ingredient from a store and notify observers
    */
    fun removeIngredient(storeName: String?, ingredient: String?) {
        if (storeName == null || ingredient == null) {
            Log.d("ShareData", "StoreName or Ingredient is null to delete.")
            return
        }

        _stores.value?.let { currentList ->
            val store = currentList.find { it.storeName == storeName }
            store?.ingredients?.removeIf { it.title == ingredient }
            updateStores(currentList)
            storeDb.deleteIngredientFromStore(storeName, ingredient)
        }
    }

    /*
    * Update the checked status of an ingredient and notify observers
    */
    fun checkIngredient(storePosition: Int, ingredientPosition: Int, value: Boolean) {
        _stores.value?.let { currentList ->
            if (storePosition in currentList.indices) {
                val store = currentList[storePosition]
                if (ingredientPosition in store.ingredients.indices) {
                    store.ingredients[ingredientPosition].isChecked = value
                    updateStores(currentList)
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(app.applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
