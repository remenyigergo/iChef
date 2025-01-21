package com.example.ichef

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ichef.database.AddParentChildDataManager
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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

    fun getLastChildItem() : String? {
        if (_childItems.value != null && _childItems.value!!.size > 0) {
            val lastChild = _childItems.value!!.lastOrNull()
            if (lastChild == null || lastChild == "") return null
        }

        return ""
    }
}

@AndroidEntryPoint
class AddParentChildActivity : AppCompatActivity() {

    @Inject
    lateinit var db : AddParentChildDataManager

    lateinit var recipeHintArray : ArrayList<String>
    lateinit var ingredientHintArray : ArrayList<String>

    private val viewModel: AddParentChildViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_parent_child)

        ingredientHintArray = arrayListOf(getString(R.string.tomato),
            getString(R.string.potato),
            getString(R.string.kiwi),
            getString(R.string.apple), getString(R.string.broccoli), getString(R.string.avocado))

        recipeHintArray = arrayListOf(getString(R.string.pad_thai),
            getString(R.string.aldi),
            getString(R.string.tesco), getString(R.string.french_toast),
            getString(R.string.french_onion_soup), getString(R.string.kaufland))

        val linearLayoutForTopParents = findViewById<LinearLayout>(R.id.addParentLayout_layout_below_parent)
        val parentTitleInput: EditText = findViewById(R.id.etParentTitle)

        val saveButton: Button = findViewById(R.id.btnSaveParent)

        val ingredients = intent.getStringArrayListExtra("ingredients_list")
        val stores = intent.getStringArrayListExtra("stores")

        // Set up autocomplete for stores
        SetAutoCompleteStoresField(stores)

        // Add top 3 parent buttons
        CreateTopParentButtons(linearLayoutForTopParents, parentTitleInput, viewModel)

        // Add the dynamically expanding ingredient list layout
        val childContainer = CreateIngredientsDynamicLayout(linearLayoutForTopParents)

        // Add the new ingredient button
        var addChildButton = AddNewIngredientButton(linearLayoutForTopParents)

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
            if (viewModel.getLastChildItem() == null) {
                Toast.makeText(this, "Last item is empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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

            // Increase top 3 counters accordingly
            db.insertOrUpdateParent(parentTitle, 1)

            val top3 = db.getTop3FavoriteParents()
            Log.i("AddParentChildActivity", "Top 3: ${top3}")

            val resultIntent = Intent().apply {
                putExtra("parentTitle", parentTitle)
                putStringArrayListExtra("childItems", ArrayList(childItems))
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun CreateIngredientsDynamicLayout(linearLayoutForTopParents: LinearLayout?) : LinearLayout {
        val ingredientsListLayout = LinearLayout(this).apply {
            id = View.generateViewId() // Generate a unique ID for the LinearLayout
            orientation = LinearLayout.VERTICAL // Set orientation to vertical
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16 // todo convert 16 dp to pixels in dimen resource
            }
        }

        linearLayoutForTopParents?.addView(ingredientsListLayout)

        return ingredientsListLayout
    }

    private fun AddNewIngredientButton(linearLayoutForTopParents: LinearLayout?) : MaterialButton {
        val addIngredientButton: MaterialButton = MaterialButton(this).apply {
            id = View.generateViewId()
            text = resources.getString(R.string.new_ingredient)
            setBackgroundColor(resources.getColor(R.color.button_color, theme))

            // Set layout parameters with wrap content
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                // todo Convert 16dp to pixels and set the top margin
                topMargin = 16
            }
            layoutParams = params
        }
        linearLayoutForTopParents?.addView(addIngredientButton) // add button to the linearLayout for showing
        return addIngredientButton
    }

    private fun CreateTopParentButtons(rootView : LinearLayout, parentTitle: EditText, viewModel: AddParentChildViewModel) {
        // Create the ConstraintLayout
        val constraintLayout = ConstraintLayout(this).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                // Set constraints relative to the parent layout
                topToBottom = R.id.etParentTitle // Top constraint to be below ParentTitle
                startToStart = ConstraintSet.PARENT_ID // Align start to parent's start
                endToEnd = ConstraintSet.PARENT_ID // Align end to parent's end
            }
            id = View.generateViewId()
        }

        // Create Top1 Button if theres at least one parent saved
        val top3 = db.getTop3FavoriteParents()

        var button1 : MaterialButton? = null
        var button2 : MaterialButton? = null
        var button3 : MaterialButton? = null
        if (top3.size >= 1) {
            button1 = MaterialButton(this).apply {
                id = View.generateViewId()
                text = top3[0]
                setBackgroundColor(resources.getColor(R.color.gray, theme))
                layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                ).apply {
                    //rightMargin = 10
                }
            }
            button1.setOnClickListener({
                parentTitle.setText(button1.text)
            })

            constraintLayout.addView(button1)
        }


        if (top3.size >= 2) {
            button2 = MaterialButton(this).apply {
                id = View.generateViewId()
                text = top3[1]
                setBackgroundColor(resources.getColor(R.color.gray, theme))
                layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                ).apply {
                    //rightMargin = 10
                }
            }
            button2.setOnClickListener({
                parentTitle.setText(button2.text)
            })

            constraintLayout.addView(button2)
        }

        if (top3.size >= 3) {
            button3 = MaterialButton(this).apply {
                id = View.generateViewId()
                text = top3[2]
                setBackgroundColor(resources.getColor(R.color.gray, theme))
            }
            button3.setOnClickListener({
                parentTitle.setText(button3.text)
            })

            constraintLayout.addView(button3)
        }

        // Apply constraints programmatically
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        val marginGap = 10 // 10-pixel gap between buttons

        // Set constraints for button1
        button1?.let {
            constraintSet.constrainWidth(it.id, 0) // Width set to 0dp for percentage width
            constraintSet.constrainHeight(it.id, ConstraintSet.WRAP_CONTENT)
            constraintSet.connect(it.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, marginGap)
            constraintSet.connect(it.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            constraintSet.connect(it.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            if (button2 != null) {
                constraintSet.connect(it.id, ConstraintSet.END, button2.id, ConstraintSet.START, marginGap)
            } else {
                constraintSet.connect(it.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, marginGap)
            }
        }

        // Set constraints for button2
        button2?.let {
            constraintSet.constrainWidth(it.id, 0) // Width set to 0dp for percentage width
            constraintSet.constrainHeight(it.id, ConstraintSet.WRAP_CONTENT)
            button1?.let { btn1 ->
                constraintSet.connect(it.id, ConstraintSet.START, btn1.id, ConstraintSet.END, marginGap)
            } ?: constraintSet.connect(it.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, marginGap)

            button3?.let { btn3 ->
                constraintSet.connect(it.id, ConstraintSet.END, btn3.id, ConstraintSet.START, marginGap)
            } ?: constraintSet.connect(it.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, marginGap)

            constraintSet.connect(it.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            constraintSet.connect(it.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        }

        // Set constraints for button3
        button3?.let {
            constraintSet.constrainWidth(it.id, 0) // Width set to 0dp for percentage width
            constraintSet.constrainHeight(it.id, ConstraintSet.WRAP_CONTENT)
            button2?.let { btn2 ->
                constraintSet.connect(it.id, ConstraintSet.START, btn2.id, ConstraintSet.END, marginGap)
            } ?: constraintSet.connect(it.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, marginGap)

            constraintSet.connect(it.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, marginGap)
            constraintSet.connect(it.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            constraintSet.connect(it.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        }

        // Apply the constraints
        constraintSet.applyTo(constraintLayout)

        rootView.addView(constraintLayout)
    }

    // Create a child input dynamically
    private fun createChildInput(item: String, index: Int, ingredients: ArrayList<String>?): AutoCompleteTextView {
        return AutoCompleteTextView(this).apply {
            inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
            hint = "e.g.: ${ingredientHintArray.random()}"
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
            autoCompleteTextView.hint = "e.g.: ${recipeHintArray.random()}"

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
