package com.example.ichef.di.modules

import android.app.Application
import com.example.ichef.adapters.FooterAdapterImpl
import com.example.ichef.adapters.FooterViewModel
import com.example.ichef.adapters.SharedData
import com.example.ichef.adapters.interfaces.FooterAdapter
import com.example.ichef.fragments.ShoppingFragmentImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

//@Module
//@InstallIn(FragmentComponent::class) // Use FragmentComponent as the adapter uses LifecycleOwner
//object FooterAdapterModule {
//
//    @Provides
//    fun provideFooterAdapter(
//        sharedData: SharedData,
//        app: Application,
//        fragment: ShoppingFragmentImpl,
//        footerViewModel: FooterViewModel
//    ): FooterAdapter {
//        return FooterAdapterImpl(sharedData, app, fragment.viewLifecycleOwner, footerViewModel)
//    }
//}
