package com.example.ichef.mappers

import com.example.ichef.clients.models.MyRecipes.MyRecipesResultItem
import com.example.ichef.models.activities.more.MyRecipe

object MyRecipeDtoBlaMapper {
    fun ToBla(myRecipe :MyRecipesResultItem) : MyRecipe {
        if (myRecipe == null)
            return MyRecipe("","","")

        return MyRecipe(myRecipe.title,myRecipe.description,myRecipe.imageResId)
    }

    fun ToDto(myRecipe: MyRecipe) : MyRecipesResultItem {
        return MyRecipesResultItem(myRecipe.title,myRecipe.description,myRecipe.imageResId)
    }
}