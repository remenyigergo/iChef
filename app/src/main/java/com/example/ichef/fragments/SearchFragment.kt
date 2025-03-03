package com.example.ichef.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ichef.R
import com.example.ichef.activities.SearchResultActivity
import com.example.ichef.constants.Constants
import com.example.ichef.models.IngredientsViewModel
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    /*
        ViewModels
    */
    private val ingredientsViewModel: IngredientsViewModel by viewModels()
    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences(Constants.SHAREDPREFERENCES_NAME, MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.search_fragment, container, false)

        HandleInclude(view)
        HandleExclude(view)

        LoadThresholdSpinner(view)

        val searchButton: Button = view.findViewById(R.id.btn_search)
        searchButton.setOnClickListener({
            val intent = Intent(context, SearchResultActivity::class.java)
            searchIntent.launch(intent)
        })

        return view
    }

    private fun LoadThresholdSpinner(view: View) {
        val spinner: Spinner = view.findViewById(R.id.spinner_threshold)

        // Create a list of numbers from 1 to 10
        val numbers = (0..10).map { it.toString() }

        // Create an ArrayAdapter using the number list and a default spinner layout
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, numbers)

        // Set the adapter to the spinner
        spinner.adapter = adapter

        val screenWidth = resources.displayMetrics.widthPixels
        spinner.dropDownWidth = (screenWidth * 0.5).toInt()

        // Center the dropdown
        val spinnerWidth = spinner.width
        spinner.dropDownHorizontalOffset = (spinnerWidth / 2) - (spinner.dropDownWidth / 2)
    }

    override fun onResume() {
        super.onResume()
        val isEnabled = sharedPreferences.getBoolean(Constants.TOOLTIPS_ENABLED, false)
        Log.e("SearchFragment", "🟡 Manually fetched tooltip_enabled onResume(): $isEnabled")
        HandleTooltips(requireView(), isEnabled)
    }

    private fun HandleTooltips(view: View, enabled: Boolean) {
        Log.e("SearchFragment", "HandleTooltips")
        val searchTitleInfo = view.findViewById<ImageView>(R.id.title_search_info)
        val includeInfo = view.findViewById<ImageView>(R.id.include_info)
        val excludeInfo = view.findViewById<ImageView>(R.id.exclude_info)
        val switchInfo = view.findViewById<ImageView>(R.id.info_icon)
        val thresholdInfo = view.findViewById<ImageView>(R.id.treshold_tip_icon)

        if (enabled) {
            searchTitleInfo.visibility = View.VISIBLE
            includeInfo.visibility = View.VISIBLE
            excludeInfo.visibility = View.VISIBLE
            switchInfo.visibility = View.VISIBLE
            thresholdInfo.visibility = View.VISIBLE

            searchTitleInfo?.setOnClickListener {
                // Display a tooltip, toast, or dialog with information
                showInfoDialog(view, "Title search tip","Basic recipe search by title matching. This is only doing search by containing these words in the recipe title.", searchTitleInfo.drawable)
            }

            includeInfo?.setOnClickListener {
                // Display a tooltip, toast, or dialog with information
                showInfoDialog(view, "Include tip","Includes the ingredients in the search results even if other search filters are selected. To delete an ingredient, just tap the ingredient you added.", includeInfo.drawable)
            }

            excludeInfo?.setOnClickListener {
                // Display a tooltip, toast, or dialog with information
                showInfoDialog(view, "Exclude tip","Excludes the ingredients from the search results even if other search filters are selected. To delete an ingredient, just tap the ingredient you added.", excludeInfo.drawable)
            }

            switchInfo?.setOnClickListener {
                // Display a tooltip, toast, or dialog with information
                showInfoDialog(view, "Only cookable tip","This going to show you only recipes, that you can cook based on your pantry ingredients. This is exact matching the ingredients (as per you have that ingredient or not) to recipes.", switchInfo.drawable)
            }

            thresholdInfo?.setOnClickListener {
                // Display a tooltip, toast, or dialog with information
                showInfoDialog(view, "Threshold tip","You can set your ingredients threshold limit. This means that a maximum of this number of ingredients will be missing in your search result.", switchInfo.drawable)
            }
        } else {
            searchTitleInfo.visibility = View.GONE
            includeInfo.visibility = View.GONE
            excludeInfo.visibility = View.GONE
            switchInfo.visibility = View.GONE
            thresholdInfo.visibility = View.GONE
        }

    }
    private fun showInfoDialog(view: View, hint: String, msg: String, icon: Drawable) {
        MaterialAlertDialogBuilder(requireContext(), R.style.FancyDialogStyle)
            .setTitle(hint)
            .setMessage(msg)
            .setIcon(icon)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private val searchIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

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

        val excludeIngredientLayout: FlexboxLayout = view.findViewById(R.id.exclude_ingredient_layout)

        // Handle item selection event
        ingredientExcludeEditText.setOnItemClickListener { _, _, position, _ ->
            val selectedIngredient = adapter?.getItem(position) ?: return@setOnItemClickListener
            createNewButton(selectedIngredient, excludeIngredientLayout, R.drawable.exclude_ingredient_button_style)
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

        val includeIngredientLayout: FlexboxLayout = view.findViewById(R.id.include_ingredient_layout)

        // Handle item selection event
        ingredientIncludeEditText.setOnItemClickListener { _, _, position, _ ->
            val selectedIngredient = adapter?.getItem(position) ?: return@setOnItemClickListener
            createNewButton(selectedIngredient, includeIngredientLayout, R.drawable.include_ingredient_button_style)
            ingredientIncludeEditText.setText("") // Clear text after selection
        }
    }

    private fun createNewButton(text: String, container: FlexboxLayout, drawable: Int) {
        // Create the button
        val newButton = Button(requireContext()).apply {
            this.text = text
            this.textSize = 10f
            this.layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                120
            ).apply {
                flexGrow = 1f  // Makes the button grow and take available space
                marginStart = 5
                marginEnd = 5
                topMargin = 5
                bottomMargin = 5
            }

            this.background = ContextCompat.getDrawable(requireContext(), drawable)
        }


        newButton.setOnClickListener {
            container.removeView(newButton)
            Toast.makeText(requireContext(), "$text clicked!", Toast.LENGTH_SHORT).show()
            true
        }

        // Add the button to the GridLayout
        container.addView(newButton)
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