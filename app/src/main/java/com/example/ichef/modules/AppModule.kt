package com.example.ichef.modules

import android.app.Application
import android.widget.Toast
import com.example.ichef.R
import com.example.ichef.adapters.FooterAdapter
import com.example.ichef.fragments.HomeFragment
import com.example.ichef.fragments.SearchFragment
import com.example.ichef.fragments.ShoppingFragment
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
    fun providesShoppingFragment() : ShoppingFragmentImpl {
        var shoppingFragment = ShoppingFragmentImpl()
        return shoppingFragment
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

    @Provides
    @Singleton
    fun providesFooterAdapter(shoppingFragment: ShoppingFragmentImpl, app: Application) : FooterAdapter {
        return FooterAdapter(onButtonClick = {
            Toast.makeText(app.applicationContext, app.getString(R.string.purchased_button_pressed), Toast.LENGTH_SHORT).show()
        }, shoppingFragment, app.applicationContext)
    }
}