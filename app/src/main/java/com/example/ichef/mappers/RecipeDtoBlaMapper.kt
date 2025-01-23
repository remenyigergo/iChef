package com.example.ichef.mappers

import com.example.ichef.clients.models.MyRecipes.MyRecipesResultItem
import com.example.ichef.models.activities.more.Recipe

object RecipeDtoBlaMapper {
    fun ToBla(myRecipe :MyRecipesResultItem) : Recipe {
        if (myRecipe == null)
            return Recipe("","","")

        return Recipe(myRecipe.title,myRecipe.description,myRecipe.imageResId)
    }

    fun ToDto(myRecipe: Recipe) : MyRecipesResultItem {
        return MyRecipesResultItem(myRecipe.title,myRecipe.description,myRecipe.imageResId)
    }
}