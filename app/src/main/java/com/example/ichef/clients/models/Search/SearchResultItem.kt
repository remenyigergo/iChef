package com.example.ichef.clients.models.Search

import com.google.gson.annotations.SerializedName

data class SearchResultItem (
    @SerializedName("recipe_name")
    val title: String,
    @SerializedName("recipe_description")
    val description: String,
    @SerializedName("recipe_image")
    val imageResId: String
)