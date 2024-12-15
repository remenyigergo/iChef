package com.example.ichef.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.AddParentChildActivity
import com.example.ichef.R
import com.example.ichef.adapters.IngredientCheckbox
import com.example.ichef.adapters.FooterAdapter
import com.example.ichef.adapters.StoreCheckBoxAdapter
import com.example.ichef.adapters.StoreCheckBox
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

    private lateinit var footerAdapter: FooterAdapter

    var adapter: StoreCheckBoxAdapter? = null
    val stores: MutableList<StoreCheckBox> = arrayListOf()

    private val addNewStore = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult
            val storeName = data.getStringExtra("parentTitle") ?: return@registerForActivityResult
            val ingredients = data.getStringArrayListExtra("childItems") ?: return@registerForActivityResult

            val store = StoreCheckBox(
                storeName,
                ingredients.map { IngredientCheckbox(it, false) }.toMutableList()
            )

            // Add the new parent item to the list and update RecyclerView
            stores.add(store)
            adapter?.notifyItemInserted(stores.size-1)


            if (::footerAdapter.isInitialized) {
                // Log an error or ensure proper initialization
                footerAdapter.showFooter(true)
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val rootView = inflater.inflate(R.layout.shopping_fragment, container, false)

        footerAdapter = FooterAdapter(onButtonClick = {
            Toast.makeText(context, getString(R.string.purchased_button_pressed), Toast.LENGTH_SHORT).show()
        })

        adapter = StoreCheckBoxAdapter(stores, footerAdapter)

        // Combine adapters using ConcatAdapter
        val concatAdapter = ConcatAdapter(adapter, footerAdapter)

        val recyclerView: RecyclerView = rootView.findViewById(R.id.rvParent)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = concatAdapter

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
            stores.forEach { parent ->
                parent.ingredients.forEach { child ->
                    child.isChecked = allChecked
                }
            }

            adapter?.notifyDataSetChanged() // Notify the adapter to update the UI
            Toast.makeText(context, getString(R.string.check_all_pressed), Toast.LENGTH_SHORT).show()
            onAddButtonClicked()
        }

        fabOpt2 = rootView.findViewById(R.id.fab_opt2)
        fabOpt2.setOnClickListener {
            val intent = Intent(context, AddParentChildActivity::class.java)
            addNewStore.launch(intent)
            Toast.makeText(context, getString(R.string.add_new_pressed), Toast.LENGTH_SHORT).show()
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
            fab.startAnimation(rotateOpen)
        } else {
            fabOpt1.startAnimation(toBottom)
            fabOpt2.startAnimation(toBottom)
            fab.startAnimation(rotateClose)
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
}
