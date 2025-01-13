package com.example.ichef.di.binds.fragments

import com.example.ichef.fragments.interfaces.ShoppingFragment
import com.example.ichef.fragments.ShoppingFragmentImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ShoppingFragmentBindModule {

    @Binds
    abstract fun bindShoppingFragment(
        shoppingFragmentImpl: ShoppingFragmentImpl
    ) : ShoppingFragment
}