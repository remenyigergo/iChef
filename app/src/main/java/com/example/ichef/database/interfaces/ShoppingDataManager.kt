package com.example.ichef.database.interfaces

import com.example.ichef.models.StoreCheckBox

interface ShoppingDataManager {
    fun insertStore(storeName: String): Long
    fun insertIngredient(storeId: Long, ingredientName: String, isChecked: Boolean)
    fun storeNewIngredientsInStore(storeName: String, ingredients: List<String>)
    fun getStores(): List<StoreCheckBox>
    fun deleteStoreWithIngredientsByName(storeName: String)
    fun deleteIngredientFromStore(storeName: String, ingredientName: String)
}