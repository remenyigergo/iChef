package com.example.ichef.di.modules

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import com.example.ichef.components.ShoppingFragmentBottomSheetDialog
import com.example.ichef.components.ShoppingFragmentBottomSheetDialogImpl
import com.example.ichef.fragments.HomeFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped


@Module
@InstallIn(ActivityComponent::class)
object BottomSheetDialogModule {

    @Provides
    @ActivityScoped
    fun provideShoppingFragmentBottomSheetDialog(
        homeFragment: HomeFragment
    ): ShoppingFragmentBottomSheetDialog {
        return ShoppingFragmentBottomSheetDialogImpl(homeFragment.context, homeFragment.layoutInflater)
    }

    @Provides
    @ActivityScoped
    //@Named("shoppingFragmentBottomSheetDialog")
    fun providesBottomSheetDialog(application: Application) : BottomSheetDialog {
        return BottomSheetDialog(application.applicationContext)
    }
}