package com.example.ichef.clients.apis.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TooltipViewModel : ViewModel() {
    private val _tooltipEnabled = MutableLiveData<Boolean>()
    val tooltipEnabled: LiveData<Boolean> get() = _tooltipEnabled

    fun setTooltipEnabled(isEnabled: Boolean) {
        _tooltipEnabled.value = isEnabled
    }
}
