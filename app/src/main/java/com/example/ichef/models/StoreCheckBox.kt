package com.example.ichef.models

data class StoreCheckBox(
    val storeName: String,
    val ingredients: MutableList<IngredientCheckbox>
)