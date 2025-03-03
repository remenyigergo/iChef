package com.example.ichef.activities.more

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.ichef.R
import com.example.ichef.constants.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySavedTheme()
        setContentView(R.layout.options)

        val backButton = findViewById<ImageView>(R.id.back_button_options)
        backButton.setOnClickListener {
            // Go back to the previous page
            onBackPressedDispatcher.onBackPressed()
        }

        val themeGroup: RadioGroup = findViewById(R.id.radio_group_theme)
        val radioLight: RadioButton = findViewById(R.id.radio_light)
        val radioDark: RadioButton = findViewById(R.id.radio_dark)
        val radioAuto: RadioButton = findViewById(R.id.radio_auto)
        val tooltipText: TextView = findViewById(R.id.tips_text)
        val tooltipSwitch: Switch = findViewById(R.id.switch_enable_tips)

        when (getSavedTheme()) {
            AppCompatDelegate.MODE_NIGHT_YES -> radioDark.isChecked = true
            AppCompatDelegate.MODE_NIGHT_NO -> radioLight.isChecked = true
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> radioAuto.isChecked = true
        }

        when (getTooltipsEnabled()) {
            true -> tooltipSwitch.isChecked = true
            false -> tooltipSwitch.isChecked = false
        }

        themeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_light -> setThemeMode(AppCompatDelegate.MODE_NIGHT_NO)
                R.id.radio_dark -> setThemeMode(AppCompatDelegate.MODE_NIGHT_YES)
                R.id.radio_auto -> setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }

        tooltipText.setOnClickListener {
            Log.i("OptionsActivity", "TooltipLayout tapped")
            tooltipSwitch.performClick()
        }

        tooltipSwitch.setOnCheckedChangeListener { tooltip, checked ->
            if (!checked) {
                tooltip.isChecked = false
                Log.e("OptionsActivity","Setting tooltip to disabled. Value is ${checked}")
            } else {
                tooltip.isChecked = true
                Log.e("OptionsActivity","Setting tooltip to enabled. Value is ${checked}")
            }

            saveTooltipChange(tooltip.isChecked)
        }
    }

    private fun setThemeMode(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
        saveTheme(mode)
        recreate()
    }

    private fun saveTheme(mode: Int) {
        val prefs: SharedPreferences = getSharedPreferences(Constants.SHAREDPREFERENCES_NAME, MODE_PRIVATE)
        with(prefs.edit()) {
            putInt(Constants.THEME_KEY, mode)
            apply()
        }
    }

    private fun saveTooltipChange(checked: Boolean) {
        val sharedPreferences: SharedPreferences = getSharedPreferences(Constants.SHAREDPREFERENCES_NAME, MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(Constants.TOOLTIPS_ENABLED, checked).commit()
        Log.e("OptionsActivity", "Saved tooltip as enabled: $checked")
    }

    private fun getSavedTheme(): Int {
        val prefs: SharedPreferences = getSharedPreferences(Constants.SHAREDPREFERENCES_NAME, MODE_PRIVATE)
        return prefs.getInt(Constants.THEME_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    private fun getTooltipsEnabled(): Boolean {
        val prefs: SharedPreferences = getSharedPreferences(Constants.SHAREDPREFERENCES_NAME, MODE_PRIVATE)
        return prefs.getBoolean(Constants.TOOLTIPS_ENABLED, true)
    }

    private fun applySavedTheme() {
        AppCompatDelegate.setDefaultNightMode(getSavedTheme())
    }
}
