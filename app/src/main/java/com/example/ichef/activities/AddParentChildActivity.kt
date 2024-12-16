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
import androidx.appcompat.app.AppCompatActivity

class AddParentChildActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_parent_child)

        val parentTitleInput: EditText = findViewById(R.id.etParentTitle)
        val addChildButton: Button = findViewById(R.id.btnAddChild)
        val childContainer: LinearLayout = findViewById(R.id.childContainer)
        val saveButton: Button = findViewById(R.id.btnSaveParent)

        val childItems = mutableListOf<String>()

        val ingredients = intent.getStringArrayListExtra("ingredients_list")
        val stores = intent.getStringArrayListExtra("stores")
        SetAutoCompleteStoresField(stores)


        addChildButton.setOnClickListener {
            val newChildInput = AutoCompleteTextView(this).apply {
                inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
            }
            SetAutoCompleteIngredientField(newChildInput,ingredients)
            newChildInput.hint = applicationContext.getString(R.string.ingredient_name)
            childContainer.addView(newChildInput)
        }

        saveButton.setOnClickListener {
            val parentTitle = parentTitleInput.text.toString()

            if (parentTitle.isBlank()) {
                Toast.makeText(this, getString(R.string.store_cannot_be_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            childItems.clear()
            for (i in 0 until childContainer.childCount) {
                val childInput = childContainer.getChildAt(i) as EditText
                val childText = childInput.text.toString()
                if (childText.isNotBlank()) {
                    childItems.add(childText)
                }
            }

            if (childItems.isEmpty()) {
                Toast.makeText(this,
                    getString(R.string.add_at_least_one_child_item), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultIntent = Intent().apply {
                putExtra("parentTitle", parentTitle)
                putStringArrayListExtra("childItems", ArrayList(childItems))
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun SetAutoCompleteStoresField(storesToShow: ArrayList<String>?) {
        if (storesToShow != null) {
            val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.etParentTitle)

            // Create an ArrayAdapter
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                storesToShow
            )

            // Set the adapter to the AutoCompleteTextView
            autoCompleteTextView.setAdapter(adapter)

            // Optionally set the threshold for showing suggestions (e.g., 1 character)
            autoCompleteTextView.threshold = 1
        }
    }

    private fun SetAutoCompleteIngredientField(textView: AutoCompleteTextView,ingredientsToShow: ArrayList<String>?) {
        if (ingredientsToShow != null) {
            // Create an ArrayAdapter
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                ingredientsToShow
            )

            // Set the adapter to the AutoCompleteTextView
            textView.setAdapter(adapter)

            // Optionally set the threshold for showing suggestions (e.g., 1 character)
            textView.threshold = 1
        }
    }
}
