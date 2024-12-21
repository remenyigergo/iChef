package com.example.ichef.models

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IngredientsViewModel @Inject constructor(
    var ingredients: ArrayList<String>
) : ViewModel() {
}