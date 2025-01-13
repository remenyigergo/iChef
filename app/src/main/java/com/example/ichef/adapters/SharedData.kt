package com.example.ichef.adapters

import android.app.Application
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.ichef.R
import com.example.ichef.database.interfaces.ShoppingDataManager
import com.example.ichef.models.StoreCheckBox
import javax.inject.Inject
import dagger.Lazy


class SharedData @Inject constructor(
    private val context: Context,
    private val storeDb: Lazy<ShoppingDataManager>
) {
    var stores : MutableList<StoreCheckBox> = mutableListOf()
    lateinit var emptyPageView: ConstraintLayout

    var addButtonClicked: Boolean = false
    var allChecked: Boolean = false
    var tickedCount = 0

    fun SetEmptyPageVisibilty() {
        if (stores.size == 0) {
            emptyPageView.visibility = View.VISIBLE
        } else {
            emptyPageView.visibility = View.INVISIBLE
        }
    }

    fun getStoresSize(): Int {
        return stores.size
    }

    fun checkIngredient(storePosition: Int, ingredientPosition: Int, value: Boolean) {
        stores[storePosition].ingredients[ingredientPosition].isChecked = value
    }

    fun removeStore(storeIndex: Int) {
        val storeName = stores[storeIndex].storeName
        storeDb.get().deleteStoreWithIngredientsByName(storeName)
        Toast.makeText(context,
            context.getString(R.string.store_deleted_successfully), Toast.LENGTH_SHORT).show()
    }

    fun removeIngredient(storeName: String, ingredient: String) {
        storeDb.get().deleteIngredientFromStore(storeName, ingredient)
    }

    fun incrementTick() {
        tickedCount++
    }

    fun decreaseTick() {
        tickedCount--
    }

    fun isAllChecked(): Boolean {
        return allChecked
    }

    fun loadStoresFromDatabase() {
        stores = storeDb.get().getStores().toMutableList()
    }
}