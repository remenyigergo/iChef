package com.example.ichef.di.modules

import co.infinum.retromock.Retromock
import com.example.ichef.clients.apis.ShoppingListApi
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitInstance {

    private const val BACKEND_URL = "https://localhost:5000"

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit {
        val gson = GsonBuilder()
            .setLenient() // Optional, for permissive parsing
            .create()

        return Retrofit.Builder()
            .baseUrl(BACKEND_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @RealApi
    @Provides
    @Singleton
    fun providesShoppingListApi(retrofit: Retrofit): ShoppingListApi {
        return retrofit.create(ShoppingListApi::class.java)
    }

    @MockApi
    @Provides
    @Singleton
    fun providesMockShoppingListApi(retrofit: Retrofit): ShoppingListApi {
        val retromock = Retromock.Builder()
            .retrofit(retrofit)
            .build()
        return retromock.create(ShoppingListApi::class.java)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RealApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MockApi