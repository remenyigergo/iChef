package com.example.ichef.clients.models.MyRecipes

import com.google.gson.annotations.SerializedName

data class MyRecipesResultItem(
    @SerializedName("recipe_name")
    val title: String,
    @SerializedName("recipe_description")
    val description: String,
    @SerializedName("recipe_image")
    val imageResId: String
)