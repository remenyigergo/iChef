package com.example.ichef.adapters

import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.ichef.R
import com.example.ichef.database.ShoppingDataManager
import com.example.ichef.models.StoreCheckBox
import javax.inject.Inject

class SharedData @Inject constructor(
    private var app: Application,
    private var storeDb : ShoppingDataManager
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
        storeDb.deleteStoreWithIngredientsByName(storeName)
        Toast.makeText(app.applicationContext,
            app.applicationContext.getString(R.string.store_deleted_successfully), Toast.LENGTH_SHORT).show()
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
}