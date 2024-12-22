package com.example.ichef.di.modules

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.ConcatAdapter
import com.example.ichef.R
import com.example.ichef.adapters.FooterAdapterImpl
import com.example.ichef.adapters.SharedData
import com.example.ichef.adapters.StoreCheckBoxAdapterImpl
import com.example.ichef.adapters.interfaces.FooterAdapter
import com.example.ichef.adapters.interfaces.StoreCheckboxAdapter
import com.example.ichef.fragments.HomeFragment
import com.example.ichef.fragments.SearchFragment
import com.example.ichef.fragments.ShoppingFragmentImpl
import com.example.ichef.fragments.interfaces.ShoppingFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
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
    fun providesShoppingFragment(checkBoxAdapterImpl: StoreCheckBoxAdapterImpl, footerAdapterImpl: FooterAdapterImpl, concatAdapter: ConcatAdapter, sharedData: SharedData, app: Application) : ShoppingFragment {
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
    fun providesCheckBoxesAdapter(footerAdapter: FooterAdapter, sharedData: SharedData) : StoreCheckboxAdapter {
        return StoreCheckBoxAdapterImpl(footerAdapter, sharedData)
    }

    @Provides
    @Singleton
    fun providesSharedData(app: Application) : SharedData {
        return SharedData(app)
    }

    @Provides
    @Singleton
    fun providesFooterAdapter(sharedData: SharedData, app: Application) : FooterAdapter {
        return FooterAdapterImpl(sharedData, app)
    }

    @Provides
    @Singleton
    fun providesConcatAdapter(checkBoxAdapter: StoreCheckBoxAdapterImpl, footerAdapter: FooterAdapterImpl) : ConcatAdapter {
        return ConcatAdapter(checkBoxAdapter, footerAdapter)
    }

    @Provides
    @ApplicationContext
    fun provideApplicationContext(app: Application): Context = app.applicationContext
}