package com.example.ichef.modules

import android.app.Application
import com.example.ichef.fragments.HomeFragment
import com.example.ichef.fragments.SearchFragment
import com.example.ichef.fragments.ShoppingFragmentImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesIngredients(app: Application) : ArrayList<String> {
        var ingredients = ArrayList<String>()
        try {
            val inputStream = app.assets.open("ingredients.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))

            // Read file line by line and add each line to the ArrayList
            reader.forEachLine { line ->
                ingredients.add(line)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ingredients
    }

    @Provides
    @Singleton
    fun providesShoppingFragment(ingredients: ArrayList<String>) : ShoppingFragmentImpl {
        return ShoppingFragmentImpl(ingredients)
    }

    @Provides
    @Singleton
    fun providesHomeFragment() : HomeFragment {
        return HomeFragment()
    }

    @Provides
    @Singleton
    fun providesSearchFragment() : SearchFragment {
        return SearchFragment()
    }
}