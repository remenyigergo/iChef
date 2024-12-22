package com.example.ichef.di.binds.adapters

import com.example.ichef.adapters.IngredientCheckBoxAdapterImpl
import com.example.ichef.adapters.interfaces.IngredientCheckboxAdapter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IngredientCheckboxAdapterBindModule {

    @Binds
    @Singleton
    abstract fun bindIngredientCheckBoxAdapter(
        ingredientCheckBoxAdapterImpl: IngredientCheckBoxAdapterImpl
    ) : IngredientCheckboxAdapter
}