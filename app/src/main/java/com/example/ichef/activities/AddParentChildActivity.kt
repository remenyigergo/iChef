package com.example.ichef

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddParentChildViewModel : ViewModel() {

    // Holds the list of child ingredients
    private val _childItems = MutableLiveData<MutableList<String>>(mutableListOf())
    val childItems: LiveData<MutableList<String>> get() = _childItems

    // Add a child item
    fun addChildItem(item: String) {
        _childItems.value?.apply {
            add(item)
            _childItems.value = this // Triggers observers
        }
    }

    // Update a child item at a specific index
    fun updateChildItem(index: Int, item: String) {
        _childItems.value?.apply {
            this[index] = item
            _childItems.value = this // Triggers observers
        }
    }
}

class AddParentChildActivity : AppCompatActivity() {

    private val viewModel: AddParentChildViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_parent_child)

        val parentTitleInput: EditText = findViewById(R.id.etParentTitle)
        val addChildButton: Button = findViewById(R.id.btnAddChild)
        val childContainer: LinearLayout = findViewById(R.id.childContainer)
        val saveButton: Button = findViewById(R.id.btnSaveParent)

        val ingredients = intent.getStringArrayListExtra("ingredients_list")
        val stores = intent.getStringArrayListExtra("stores")

        // Set up autocomplete for stores
        SetAutoCompleteStoresField(stores)

        // Observe LiveData for child items and update the UI
        viewModel.childItems.observe(this) { items ->
            // Update existing views or add new ones if necessary
            items.forEachIndexed { index, item ->
                val existingView = if (index < childContainer.childCount) {
                    childContainer.getChildAt(index) as? AutoCompleteTextView
                } else null

                if (existingView == null) {
                    // Add a new view if it doesn't already exist
                    val childInput = createChildInput(item, index, ingredients)
                    childContainer.addView(childInput)
                } else if (existingView.text.toString() != item) {
                    // Update existing view if the text differs
                    existingView.setText(item)
                }
            }

            // Remove extra views if the LiveData list is shorter
            while (childContainer.childCount > items.size) {
                childContainer.removeViewAt(childContainer.childCount - 1)
            }
        }



        addChildButton.setOnClickListener {
            // Add a new child item (empty string for now)
            viewModel.addChildItem("")

            // Get the newly created child input view (the last one in the container)
            val newChildInput = childContainer.getChildAt(childContainer.childCount - 1) as AutoCompleteTextView

            // Request focus on the newly created input
            newChildInput.requestFocus()

            // Optionally, open the keyboard automatically when the input field gains focus
            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showSoftInput(newChildInput, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
        }

        saveButton.setOnClickListener {
            updateFocusedChildInput(childContainer) // Update the current focused input before saving

            val parentTitle = parentTitleInput.text.toString()

            if (parentTitle.isBlank()) {
                Toast.makeText(this, getString(R.string.store_cannot_be_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val childItems = mutableListOf<String>()
            for (i in 0 until childContainer.childCount) {
                val childInput = childContainer.getChildAt(i) as AutoCompleteTextView
                val childText = childInput.text.toString()
                if (childText.isNotBlank()) {
                    childItems.add(childText)
                }
            }

            if (childItems.isEmpty()) {
                Toast.makeText(this, getString(R.string.add_at_least_one_child_item), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            childItems.forEach { ingredient ->
                if (ingredients != null && !ingredients.contains(ingredient)) {
                    Toast.makeText(this, "$ingredient is not a valid ingredient.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }

            val resultIntent = Intent().apply {
                putExtra("parentTitle", parentTitle)
                putStringArrayListExtra("childItems", ArrayList(childItems))
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    // Create a child input dynamically
    private fun createChildInput(item: String, index: Int, ingredients: ArrayList<String>?): AutoCompleteTextView {
        return AutoCompleteTextView(this).apply {
            inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
            hint = applicationContext.getString(R.string.ingredient_name)
            setText(item)
            SetAutoCompleteIngredientField(this, ingredients)

            // Use TextWatcher to update LiveData in real-time
            addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    viewModel.updateChildItem(index, s.toString())
                }

                override fun afterTextChanged(s: android.text.Editable?) {}
            })
        }
    }



    private fun SetAutoCompleteStoresField(storesToShow: ArrayList<String>?) {
        if (storesToShow != null) {
            val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.etParentTitle)

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                storesToShow
            )
            autoCompleteTextView.setAdapter(adapter)
            autoCompleteTextView.threshold = 1
        }
    }

    private fun SetAutoCompleteIngredientField(textView: AutoCompleteTextView, ingredientsToShow: ArrayList<String>?) {
        if (ingredientsToShow != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                ingredientsToShow
            )
            textView.setAdapter(adapter)
            textView.threshold = 1
            textView.dropDownVerticalOffset = 0
        }
    }

    // Update the currently focused child input
    private fun updateFocusedChildInput(childContainer : LinearLayout) {
        val focusedView = currentFocus
        if (focusedView is AutoCompleteTextView) {
            val index = (childContainer.indexOfChild(focusedView))
            if (index != -1) {
                viewModel.updateChildItem(index, focusedView.text.toString())
            }
        }
    }
}
