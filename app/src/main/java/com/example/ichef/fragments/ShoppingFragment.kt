package com.example.ichef.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.ichef.R

class ShoppingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val rootView = inflater.inflate(R.layout.shopping_fragment, container, false)

        val shoppingList: LinearLayout = rootView.findViewById(R.id.shopping_list)
        AddIngredientsToCheckboxList(shoppingList)

        return rootView
    }

    private fun AddIngredientsToCheckboxList(shoppingList: LinearLayout) {
        val ingredients = listOf(
            "Tomato", "Bread", "Hummus", "Mayonnaise", "Mustard", "Ketchup", "Pork",
            "Sausage", "Paprika", "Garlic", "Butter", "Sugar", "Sugar", "Sugar", "Sugar", "Sugar", "Sugar", "Sugar", "Sugar", "Sugar", "Sugar", "Sugar", "Sugar", "Sugar", "Sugar", "Sugar"
        )
        ingredients.forEach { ingredient ->
            // Create a new CheckBox
            val newCheckBox = CheckBox(context).apply {
                text = ingredient
                textSize = 20f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            // Add the CheckBox to the LinearLayout
            shoppingList.addView(newCheckBox)
        }
    }
}
