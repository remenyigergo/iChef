package com.example.ichef

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.ichef.constants.Constants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        applySavedTheme() // Apply the saved theme globally when the app starts
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences(Constants.SHAREDPREFERENCES_NAME, MODE_PRIVATE)
        val themeMode = prefs.getInt(Constants.THEME_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }
}