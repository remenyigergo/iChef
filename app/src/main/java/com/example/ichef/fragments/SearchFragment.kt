package com.example.ichef.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ichef.R
import com.example.ichef.models.IngredientsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    /*
        ViewModels
    */
    private val ingredientsViewModel: IngredientsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.search_fragment, container, false)

        HandleInclude(view)
        HandleExclude(view)

        return view
    }

    private fun HandleExclude(view: View) {
        val excludeFiltersToggleButton: Button = view.findViewById(R.id.btn_toggle_exclude_filters)
        val excludeFiltersSection: LinearLayout = view.findViewById(R.id.exclude_filters_section)
        HandleExcludeIncludeFilterToggles(
            excludeFiltersToggleButton,
            excludeFiltersSection,
            resources.getString(R.string.exclude_filters_expand),
            resources.getString(R.string.exclude_filters_collapse)
        )

        val ingredientExcludeEditText: AutoCompleteTextView =
            view.findViewById(R.id.ingredient_exclude)

        val adapter =
            SetAutoCompleteIngredients(ingredientExcludeEditText, ingredientsViewModel.ingredients)

        val excludeIngredientLayout: GridLayout = view.findViewById(R.id.exclude_ingredient_layout)

        // Handle item selection event
        ingredientExcludeEditText.setOnItemClickListener { _, _, position, _ ->
            val selectedIngredient = adapter?.getItem(position) ?: return@setOnItemClickListener
            createNewButton(selectedIngredient, excludeIngredientLayout)
            ingredientExcludeEditText.setText("") // Clear text after selection
        }
    }

    private fun HandleInclude(view: View) {
        val includeFiltersToggleButton: Button = view.findViewById(R.id.btn_toggle_include_filters)
        val includeFiltersSection: LinearLayout = view.findViewById(R.id.include_filters_section)

        HandleExcludeIncludeFilterToggles(
            includeFiltersToggleButton,
            includeFiltersSection,
            resources.getString(R.string.include_filters_expand),
            resources.getString(R.string.include_filters_collapse)
        )

        val ingredientIncludeEditText: AutoCompleteTextView =
            view.findViewById(R.id.ingredient_include)

        val adapter =
            SetAutoCompleteIngredients(ingredientIncludeEditText, ingredientsViewModel.ingredients)

        val includeIngredientLayout: GridLayout = view.findViewById(R.id.include_ingredient_layout)

        // Handle item selection event
        ingredientIncludeEditText.setOnItemClickListener { _, _, position, _ ->
            val selectedIngredient = adapter?.getItem(position) ?: return@setOnItemClickListener
            createNewButton(selectedIngredient, includeIngredientLayout)
            ingredientIncludeEditText.setText("") // Clear text after selection
        }
    }

    private fun createNewButton(text: String, container: GridLayout) {
        // Create the button
        val newButton = Button(requireContext()).apply {
            this.text = text
            this.layoutParams = GridLayout.LayoutParams().apply {
                width = GridLayout.LayoutParams.WRAP_CONTENT
                height = GridLayout.LayoutParams.WRAP_CONTENT
                width = 0
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(5, 5, 5, 5) // Add some space around the button
            }
        }

        newButton.setOnClickListener {
            container.removeView(newButton)
            Toast.makeText(requireContext(), "$text clicked!", Toast.LENGTH_SHORT).show()
            true
        }

        // Add the button to the GridLayout
        container.addView(newButton)
    }

    private fun getScreenWidth(): Int {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    private fun HandleExcludeIncludeFilterToggles(
        includeFiltersToggleButton: Button,
        includeFiltersSection: LinearLayout,
        includeString: String,
        excludeString: String
    ) {
        includeFiltersToggleButton.setOnClickListener {
            if (includeFiltersSection.visibility == View.VISIBLE) {
                includeFiltersSection.visibility = View.GONE
                includeFiltersToggleButton.text = includeString
            } else {
                includeFiltersSection.visibility = View.VISIBLE
                includeFiltersToggleButton.text = excludeString
            }
        }
    }

    private fun SetAutoCompleteIngredients(ingredientsTextView: AutoCompleteTextView, ingredients: List<String>?) : ArrayAdapter<String>? {
        if (ingredients != null) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                ingredients
            )
            ingredientsTextView.setAdapter(adapter)
            ingredientsTextView.threshold = 1

            return adapter
        }

        return null
    }

}