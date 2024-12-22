package com.example.ichef.fragments

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.AddParentChildActivity
import com.example.ichef.R
import com.example.ichef.adapters.SharedData
import com.example.ichef.models.IngredientCheckbox
import com.example.ichef.adapters.interfaces.FooterAdapter
import com.example.ichef.adapters.interfaces.StoreCheckboxAdapter
import com.example.ichef.database.ShoppingDataManager
import com.example.ichef.fragments.interfaces.ShoppingFragment
import com.example.ichef.models.StoreCheckBox
import com.example.ichef.models.IngredientsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ShoppingFragmentImpl @Inject constructor() : Fragment(), ShoppingFragment {

    @Inject
    lateinit var checkBoxesAdapter: StoreCheckboxAdapter
    @Inject
    lateinit var footerAdapter: FooterAdapter
    //var concatAdapter: ConcatAdapter,
    @Inject
    lateinit var sharedData: SharedData
    @Inject
    lateinit var app: Application

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.to_bottom_anim) }

    private val storeDatabase by lazy { ShoppingDataManager(app.applicationContext) }

    private lateinit var fab: FloatingActionButton
    private lateinit var tickAllButton: FloatingActionButton
    private lateinit var newStoreCheckBoxButton: FloatingActionButton

    //by viewModels means config change is not voiding out the property
    private val viewModel: IngredientsViewModel by viewModels()

    init {
        //loadStoresFromDatabase()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        Log.d("ShoppingFragment", "onSaveInstanceState called")
        Log.d("ShoppingFragment", "addButtonClicked: ${sharedData.addButtonClicked}")
        Log.d("ShoppingFragment", "allChecked: $sharedData.allChecked")
        Log.d("ShoppingFragment", "tickedCount: $sharedData.tickedCount")

        outState.putBoolean("addButtonClicked", sharedData.addButtonClicked)
        outState.putBoolean("allChecked", sharedData.allChecked)
        outState.putInt("tickedCount", sharedData.tickedCount)
        HandleStates(outState)
    }

    private fun HandleStates(outState: Bundle) {
        for (store in sharedData.stores) {
            for (ingredient in store.ingredients) {
                val key = "${store.storeName}_${ingredient.title}"
                outState.putBoolean("${store.storeName}_${ingredient.title}", ingredient.isChecked)
                Log.d("ShoppingFragment", "Saving ingredient state - $key: ${ingredient.isChecked}")
            }
        }
    }

    /*
    * This is only to make a mock GET from DB, should be empty from start or GET API should load up these
    * */


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
                    sharedData.stores[storeIndex].ingredients.add(ingredient)
                })
            } else {
                sharedData.stores.add(store)
                checkBoxesAdapter?.notifyItemInserted(sharedData.stores.size-1)
                saveStoreToDatabase(store)
            }

            // todo ajajj not init exception
            footerAdapter.showFooter(true) //use interface

        }
    }

    private fun getStoreIndex(storeName: String): Int {
        var positionOfStore = -1
        var iteration = 0
        sharedData.stores.forEach({ store ->
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
        loadStoresFromDatabase() // todo temporary db while backend not ready
        //restoreStates(savedInstanceState)
        
        // Get empty layout to make it visible if nothing in shopping list for the first time
        sharedData.emptyPageView = rootView.findViewById(R.id.empty_shopping_list_page)
        sharedData.SetEmptyPageVisibilty()

        /*
            TODO make http GET call to backend and upload list to stores variable
        */
        if (sharedData.stores.size > 0) {
            footerAdapter.showFooter(true)
        }

        val concatAdapter = ConcatAdapter(checkBoxesAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>, footerAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)

        val recyclerView: RecyclerView = rootView.findViewById(R.id.rvParent)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = concatAdapter

        fab = rootView.findViewById(R.id.fab)
        fab.setOnClickListener {
            onAddButtonClicked()
        }

        tickAllButton = rootView.findViewById(R.id.fab_opt1)
        tickAllButton.setOnClickListener {
            if (sharedData.stores.size > 0) {
                if (sharedData.allChecked) {
                    sharedData.allChecked = false
                    sharedData.tickedCount = 0 //reset to 0 selection, when everything was ticked off with the button : FIX for button showing everything was purchased even though nothing was ticked
                    Toast.makeText(context, getString(R.string.check_all_unpressed), Toast.LENGTH_SHORT).show()
                } else {
                    sharedData.allChecked = true
                    sharedData.tickedCount = 0 //reset to 0 selection, when everything was ticked off with the button

                    Toast.makeText(context, getString(R.string.check_all_pressed), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, getString(R.string.nothing_to_tick_here), Toast.LENGTH_SHORT).show()
            }

            // Select All functionality
            sharedData.stores.forEach { parent ->
                parent.ingredients.forEach { child ->
                    child.isChecked = sharedData.allChecked
                }
            }

            checkBoxesAdapter?.notifyDataSetChanged() // Notify the adapter to update the UI

            onAddButtonClicked()
        }

        newStoreCheckBoxButton = rootView.findViewById(R.id.fab_opt2)
        newStoreCheckBoxButton.setOnClickListener {
            val ingredients = viewModel.ingredients
            val intent = Intent(context, AddParentChildActivity::class.java)
            intent.putStringArrayListExtra("ingredients_list",ingredients)
            intent.putStringArrayListExtra("stores",GetStoresNames())
            addNewStore.launch(intent)
            Toast.makeText(context, getString(R.string.add_new_pressed), Toast.LENGTH_SHORT).show()
            onAddButtonClicked()
        }

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveStoresToDatabase()
    }

    private fun restoreStates(savedInstanceState: Bundle?) {
        //restoring variables if config change happens (light-dark-mode switch)
        sharedData.addButtonClicked = savedInstanceState?.getBoolean("addButtonClicked") ?: false
        sharedData.allChecked = savedInstanceState?.getBoolean("allChecked") ?: false
        sharedData.tickedCount = savedInstanceState?.getInt("tickedCount") ?: 0

        // Restore the state of the checkboxes if available
        savedInstanceState?.let {
            for (store in sharedData.stores) {
                for (ingredient in store.ingredients) {
                    ingredient.isChecked =
                        it.getBoolean("${store.storeName}_${ingredient.title}", false)
                }
            }
        }

    }

    private fun GetStoresNames(): ArrayList<String>? {
        var storesNames = ArrayList<String>()
        sharedData.stores.forEach({checkboxElement-> storesNames.add(checkboxElement.storeName)})
        return storesNames
    }

    private fun onAddButtonClicked() {
        setVisibility(sharedData.addButtonClicked)
        setAnimation(sharedData.addButtonClicked)
        setClickable(sharedData.addButtonClicked)
        sharedData.addButtonClicked = !sharedData.addButtonClicked
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

    private fun saveStoresToDatabase() {
        sharedData.stores.forEach { store ->
            val storeId = storeDatabase.insertStore(store.storeName)
            store.ingredients.forEach { ingredient ->
                storeDatabase.insertIngredient(storeId, ingredient.title, ingredient.isChecked)
            }
        }
    }

    private fun saveStoreToDatabase(childParent: StoreCheckBox) {
        val storeId = storeDatabase.insertStore(childParent.storeName)
        childParent.ingredients.forEach { ingredient ->
            storeDatabase.insertIngredient(storeId, ingredient.title, ingredient.isChecked)
        }
    }

    private fun loadStoresFromDatabase() {
        sharedData.stores = storeDatabase.getStores().toMutableList()
        checkBoxesAdapter?.notifyDataSetChanged()
    }
}
