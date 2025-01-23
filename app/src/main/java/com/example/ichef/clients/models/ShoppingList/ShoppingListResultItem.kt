package com.example.ichef.clients.models.ShoppingList

data class ShoppingListResultItem(
    val ingredients: List<Ingredient>,
    val storeName: String
)