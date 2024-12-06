package com.example.ichef.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
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

        // Access the LinearLayout after inflating the layout
        val shoppingListLayout = rootView.findViewById<LinearLayout>(R.id.shopping_list)

        // Create a new CheckBox
        val newCheckBox = CheckBox(context).apply {
            text = "Tomato"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Add the CheckBox to the LinearLayout
        shoppingListLayout.addView(newCheckBox)

        Log.d("ShoppingFragment", "Added new CheckBox to shopping list")

        return rootView // Return the inflated layout as the fragment's view
    }
}