package com.example.ichef.di.binds.adapters

import com.example.ichef.adapters.StoreCheckBoxAdapterImpl
import com.example.ichef.adapters.interfaces.StoreCheckboxAdapter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StoreCheckboxAdapterBindModule {

    @Binds
    abstract fun bindStoreCheckboxAdapter(
        storeCheckBoxAdapterImpl: StoreCheckBoxAdapterImpl
    ) : StoreCheckboxAdapter
}