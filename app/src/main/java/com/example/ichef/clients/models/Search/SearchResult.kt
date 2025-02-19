package com.example.ichef.clients.models.Search

import com.google.gson.annotations.SerializedName

data class SearchResult(
    @SerializedName("results")
    val results: ArrayList<SearchResultItem>,
    @SerializedName("page")
    val page: Int,
    @SerializedName("totalPage")
    val totalPage: Int
) {
}