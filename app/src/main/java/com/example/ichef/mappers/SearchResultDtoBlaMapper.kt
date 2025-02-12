package com.example.ichef.mappers

import com.example.ichef.clients.models.Search.SearchResultItem
import com.example.ichef.models.activities.search.SearchRecipe

object SearchResultDtoBlaMapper {
    fun ToBla(search : SearchResultItem) : SearchRecipe {
        if (search == null)
            return SearchRecipe("","","")

        return SearchRecipe(search.title,search.description,search.imageResId)
    }
}