package com.example.ichef.di.modules

import com.example.ichef.adapters.FooterViewModel
import com.example.ichef.adapters.SharedData
import com.example.ichef.adapters.StoreCheckBoxAdapterImpl
import com.example.ichef.adapters.interfaces.FooterAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

//@Module
//@InstallIn(FragmentComponent::class) // Use FragmentComponent since FooterViewModel is fragment-scoped
//object StoreAdapterModule {
//
//    @Provides
//    fun provideStoreCheckBoxAdapter(
//        sharedData: SharedData
//    ): StoreCheckBoxAdapterImpl {
//        return StoreCheckBoxAdapterImpl(sharedData)
//    }
//}
