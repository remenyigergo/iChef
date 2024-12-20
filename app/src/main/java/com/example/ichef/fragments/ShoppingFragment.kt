package com.example.ichef.fragments

import com.example.ichef.adapters.StoreCheckBox

interface ShoppingFragment {
    fun getStores() : MutableList<StoreCheckBox>
    fun getTickedCount() : Int
    fun incrementTick()
    fun decreaseTick()
    fun isAllChecked() : Boolean
    fun setAllChecked(value: Boolean)
    fun SetEmptyPageVisibilty()
}