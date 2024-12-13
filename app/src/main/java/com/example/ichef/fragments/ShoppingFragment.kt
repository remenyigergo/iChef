package com.example.ichef.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.marginLeft
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.AddParentChildActivity
import com.example.ichef.R
import com.example.ichef.adapters.ChildItem
import com.example.ichef.adapters.ParentAdapter
import com.example.ichef.adapters.ParentItem
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ShoppingFragment : Fragment() {

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.to_bottom_anim) }

    private var clicked: Boolean = false
    private var allChecked: Boolean = false

    lateinit var fab: FloatingActionButton
    lateinit var fabOpt1: FloatingActionButton
    lateinit var fabOpt2: FloatingActionButton

    var adapter: ParentAdapter? = null

    val parentItems = mutableListOf(
        ParentItem("Parent 1", mutableListOf(ChildItem("Child 1", false), ChildItem("Child 2", false))),
        ParentItem("Parent 2", mutableListOf(ChildItem("Child 3", false), ChildItem("Child 4", false), ChildItem("Child 5", false), ChildItem("Child 6", false), ChildItem("Child 7", false), ChildItem("Child 8", false), ChildItem("Child 9", false), ChildItem("Child 10", false), ChildItem("Child 11", false), ChildItem("Child 12", false), ChildItem("Child 13", false))),
        ParentItem("Parent 3", mutableListOf(ChildItem("Child 6", false), ChildItem("Child 2", false), ChildItem("Child 8", false)))
    )

    private val addParentResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult
            val parentTitle = data.getStringExtra("parentTitle") ?: return@registerForActivityResult
            val childItems = data.getStringArrayListExtra("childItems") ?: return@registerForActivityResult

            val newParent = ParentItem(
                parentTitle,
                childItems.map { ChildItem(it, false) }.toMutableList()
            )

            // Add the new parent item to the list and update RecyclerView
            parentItems.add(newParent)
            adapter?.notifyItemInserted(parentItems.size-1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val rootView = inflater.inflate(R.layout.shopping_fragment, container, false)

        //val shoppingList: LinearLayout = rootView.findViewById(R.id.shopping_list)
        //AddIngredientsToCheckboxList(shoppingList)

        val recyclerView: RecyclerView = rootView.findViewById(R.id.rvParent)

        adapter = ParentAdapter(parentItems) { parentPosition ->
            parentItems.removeAt(parentPosition) // Remove parent item
            adapter?.notifyItemRemoved(parentPosition) // Notify RecyclerView
            adapter?.notifyItemRangeChanged(parentPosition, parentItems.size) // Adjust the subsequent items' positions
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        fab = rootView.findViewById(R.id.fab)
        fab.setOnClickListener {
            onAddButtonClicked()
        }

        fabOpt1 = rootView.findViewById(R.id.fab_opt1)
        fabOpt1.setOnClickListener {
            if (allChecked) {
                allChecked = false
            } else {
                allChecked = true
            }

            // Select All functionality
            parentItems.forEach { parent ->
                parent.children.forEach { child ->
                    child.isChecked = allChecked
                }
            }

            adapter?.notifyDataSetChanged() // Notify the adapter to update the UI
            Toast.makeText(context,"Opt 1 pressed", Toast.LENGTH_SHORT).show()
            onAddButtonClicked()
        }

        fabOpt2 = rootView.findViewById(R.id.fab_opt2)
        fabOpt2.setOnClickListener {
            val intent = Intent(context, AddParentChildActivity::class.java)
            addParentResultLauncher.launch(intent)
            Toast.makeText(context,"Opt 2 pressed", Toast.LENGTH_SHORT).show()
            onAddButtonClicked()
        }
        return rootView
    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            fabOpt1.startAnimation(fromBottom)
            fabOpt2.startAnimation(fromBottom)
            //fab.startAnimation(rotateOpen)
        } else {
            fabOpt1.startAnimation(toBottom)
            fabOpt2.startAnimation(toBottom)
            //fab.startAnimation(rotateClose)
        }
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            fabOpt1.visibility = View.VISIBLE
            fabOpt2.visibility = View.VISIBLE
        } else {
            fabOpt1.visibility = View.INVISIBLE
            fabOpt2.visibility = View.INVISIBLE
        }
    }

    private fun setClickable(clicked: Boolean) {
        if (!clicked) {
            fabOpt1.isClickable = true
            fabOpt2.isClickable = true
        } else {
            fabOpt1.isClickable = false
            fabOpt2.isClickable = false
        }
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
                ).apply {
                    leftMargin = 55
                }
                setPadding(0,0,0,30)
            }
            // Add the CheckBox to the LinearLayout
            shoppingList.addView(newCheckBox)
        }

        val purchasedButton = Button(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1f
                bottomMargin = 200
            }

            text = "Purchased All"
        }
        shoppingList.addView(purchasedButton)


    }
}
