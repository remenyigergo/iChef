package com.example.ichef.activities.more

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.ichef.R
import com.example.ichef.clients.apis.viewmodels.TooltipViewModel
import com.example.ichef.constants.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OptionsActivity : AppCompatActivity() {

    private val sharedViewModel: TooltipViewModel by viewModels()

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
            handleLayoutTap(tooltipSwitch)
            saveTooltipChange(tooltipSwitch)
        }

//        tooltipSwitch.setOnCheckedChangeListener { _, checked ->
//            Log.i("OptionsActivity", "TooltipSwitch tapped")
//            handleLayoutTap(tooltipSwitch)
//            saveTooltipChange(tooltipSwitch)
//        }
    }

    private fun handleLayoutTap(tooltipSwitch: Switch) {
        if (tooltipSwitch.isChecked) {
            tooltipSwitch.isChecked = false
            sharedViewModel.setTooltipEnabled(false)
            Log.e("OptionsActivity","Setting tooltip to disabled. Value is ${sharedViewModel.tooltipEnabled.value}")
        } else {
            tooltipSwitch.isChecked = true
            sharedViewModel.setTooltipEnabled(true)
            Log.e("OptionsActivity","Setting tooltip to enabled. Value is ${sharedViewModel.tooltipEnabled.value}")
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

    private fun saveTooltipChange(tooltipSwitch: Switch) {
        val sharedPreferences: SharedPreferences = getSharedPreferences(Constants.SHAREDPREFERENCES_NAME, MODE_PRIVATE)
        val isSwitchChecked = tooltipSwitch.isChecked
        sharedPreferences.edit().putBoolean(Constants.TOOLTIPS_ENABLED, isSwitchChecked).commit()
        Log.e("OptionsActivity", "Saved tooltip as enabled: $isSwitchChecked")
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
