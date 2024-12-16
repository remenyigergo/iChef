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
import androidx.constraintlayout.widget.ConstraintLayout
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
    var allChecked: Boolean = false //this is changed in storeCheckBoxAdapter. reference to this could be given only.

    lateinit var fab: FloatingActionButton
    lateinit var tickAllButton: FloatingActionButton
    lateinit var newStoreCheckBoxButton: FloatingActionButton

    private lateinit var footerAdapter: FooterAdapter
    lateinit var emptyPageView: ConstraintLayout

    var adapter: StoreCheckBoxAdapter? = null
    //val stores: MutableList<StoreCheckBox> = arrayListOf()


    /*
    * Mock to GET ingredients from backend
    * */
    val ingredients = arrayListOf("Só","Bors", "Sonka", "Mustár","Gesztenye","Gomba","Cékla","Leveles tészta","Vöröshagyma","Fokhagyma","Gyömbér","Szalonna","Paprika","Római Kömény", "Fűszerkömény","Fokhagymapor", "Kenyér","Paradicsom")

    /*
    * This is only to make a mock GET from DB
    * */
    val stores = mutableListOf(
        StoreCheckBox("Aldi", mutableListOf(IngredientCheckbox("Kenyér", false), IngredientCheckbox("Római kömény", false))),
        StoreCheckBox("Lidl", mutableListOf(IngredientCheckbox("Paradicsom", false), IngredientCheckbox("Paprika", false), IngredientCheckbox("Olaj", false), IngredientCheckbox("Narancs", false), IngredientCheckbox("Citrom", false), IngredientCheckbox("Fahéj", false))),
        StoreCheckBox("Spar", mutableListOf(IngredientCheckbox("Mogyoróvaj", false), IngredientCheckbox("Fűszerpaprika", false), IngredientCheckbox("Alma", false)))
    )

    var tickedCount = 0

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
            var storeIndex = getStoreIndex(storeName)
            if (storeIndex != -1) {
                store.ingredients.forEach({ ingredient ->
                    stores[storeIndex].ingredients.add(ingredient)
                })
            } else {
                stores.add(store)
                adapter?.notifyItemInserted(stores.size-1)
            }

            if (::footerAdapter.isInitialized) {
                // Log an error or ensure proper initialization
                footerAdapter.showFooter(true)
                SetEmptyPageVisibilty(emptyPageView)
            }
        }
    }

    private fun getStoreIndex(storeName: String): Int {
        var positionOfStore = -1
        var iteration = 0
        stores.forEach({ store ->
            if (store.storeName == storeName) {
                positionOfStore = iteration
            }
            iteration++
        })

        return positionOfStore
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val rootView = inflater.inflate(R.layout.shopping_fragment, container, false)

        // Get empty layout to make it visible if nothing in shopping list for the first time
        emptyPageView = rootView.findViewById<ConstraintLayout>(R.id.empty_shopping_list_page)
        SetEmptyPageVisibilty(emptyPageView)

        // Set the Purchased bottom to be on the bottom of the list
        footerAdapter = FooterAdapter(onButtonClick = {
            Toast.makeText(context, getString(R.string.purchased_button_pressed), Toast.LENGTH_SHORT).show()
        })

        adapter = StoreCheckBoxAdapter(footerAdapter, this)

        /*
            TODO make http GET call to backend and upload list to stores variable
        */
        if (stores.size > 0) {
            footerAdapter.showFooter(true)
        }


        // Combine adapters using ConcatAdapter
        val concatAdapter = ConcatAdapter(adapter, footerAdapter)

        val recyclerView: RecyclerView = rootView.findViewById(R.id.rvParent)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = concatAdapter

        fab = rootView.findViewById(R.id.fab)
        fab.setOnClickListener {
            onAddButtonClicked()
        }

        tickAllButton = rootView.findViewById(R.id.fab_opt1)
        tickAllButton.setOnClickListener {
            if (stores.size > 0) {
                if (allChecked) {
                    allChecked = false
                    Toast.makeText(context, getString(R.string.check_all_unpressed), Toast.LENGTH_SHORT).show()
                } else {
                    allChecked = true
                    Toast.makeText(context, getString(R.string.check_all_pressed), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, getString(R.string.nothing_to_tick_here), Toast.LENGTH_SHORT).show()
            }

            // Select All functionality
            stores.forEach { parent ->
                parent.ingredients.forEach { child ->
                    child.isChecked = allChecked
                }
            }

            adapter?.notifyDataSetChanged() // Notify the adapter to update the UI

            onAddButtonClicked()
        }

        newStoreCheckBoxButton = rootView.findViewById(R.id.fab_opt2)
        newStoreCheckBoxButton.setOnClickListener {
            val intent = Intent(context, AddParentChildActivity::class.java)
            intent.putStringArrayListExtra("ingredients_list",ingredients)
            intent.putStringArrayListExtra("stores",GetStoresNames())
            addNewStore.launch(intent)
            Toast.makeText(context, getString(R.string.add_new_pressed), Toast.LENGTH_SHORT).show()
            onAddButtonClicked()
        }

        return rootView
    }

    private fun GetStoresNames(): ArrayList<String>? {
        var storesNames = ArrayList<String>()
        stores.forEach({checkboxElement-> storesNames.add(checkboxElement.storeName)})
        return storesNames
    }

    fun SetEmptyPageVisibilty(emptyPageView: ConstraintLayout) {
        if (stores.size == 0) {
            emptyPageView.visibility = View.VISIBLE
        } else {
            emptyPageView.visibility = View.INVISIBLE
        }
    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            tickAllButton.startAnimation(fromBottom)
            newStoreCheckBoxButton.startAnimation(fromBottom)
            fab.startAnimation(rotateOpen)
        } else {
            tickAllButton.startAnimation(toBottom)
            newStoreCheckBoxButton.startAnimation(toBottom)
            fab.startAnimation(rotateClose)
        }
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            tickAllButton.visibility = View.VISIBLE
            newStoreCheckBoxButton.visibility = View.VISIBLE
        } else {
            tickAllButton.visibility = View.INVISIBLE
            newStoreCheckBoxButton.visibility = View.INVISIBLE
        }
    }

    private fun setClickable(clicked: Boolean) {
        if (!clicked) {
            tickAllButton.isClickable = true
            newStoreCheckBoxButton.isClickable = true
        } else {
            tickAllButton.isClickable = false
            newStoreCheckBoxButton.isClickable = false
        }
    }
}
