package com.example.ichef.di.binds.database

import com.example.ichef.database.interfaces.ShoppingDataManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ShoppingDataManagerBindModule {

    @Binds
    abstract fun bindShoppingDataManager(
        shoppingDataManager: ShoppingDataManager
    ) : ShoppingDataManager
}