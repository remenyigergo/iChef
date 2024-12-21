package com.example.ichef.fragments

import com.example.ichef.models.StoreCheckBox

interface ShoppingFragment {
    fun getStores() : MutableList<StoreCheckBox>
    fun getTickedCount() : Int
    fun incrementTick()
    fun decreaseTick()
    fun isAllChecked() : Boolean
    fun setAllChecked(value: Boolean)
    fun SetEmptyPageVisibilty()
    fun checkIngredient(storePosition: Int, ingredientPosition: Int, value: Boolean)
}