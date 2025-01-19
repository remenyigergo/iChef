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
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.AddParentChildActivity
import com.example.ichef.R
import com.example.ichef.adapters.FooterAdapterImpl
import com.example.ichef.adapters.FooterViewModel
import com.example.ichef.adapters.SharedData
import com.example.ichef.adapters.StoreCheckBoxAdapterImpl
import com.example.ichef.models.IngredientCheckbox
import com.example.ichef.adapters.interfaces.StoreCheckboxAdapter
import com.example.ichef.clients.apis.ApiState
import com.example.ichef.clients.apis.viewmodels.ShoppingListApiViewModel
import com.example.ichef.database.ShoppingDataManager
import com.example.ichef.fragments.interfaces.ShoppingFragment
import com.example.ichef.models.StoreCheckBox
import com.example.ichef.models.IngredientsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ShoppingFragmentImpl @Inject constructor() : Fragment(), ShoppingFragment {

    @Inject
    lateinit var app: Application
    @Inject
    lateinit var storeDatabase: ShoppingDataManager

    /*
    * ViewModels
    * */
    private val footerViewModel: FooterViewModel by viewModels()
    private val sharedDataViewModel: SharedData by viewModels()
    private val shoppingListApiViewModel: ShoppingListApiViewModel by viewModels()
    private val ingredientsViewModel: IngredientsViewModel by viewModels()

    /*
    * Animations
    * */
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.to_bottom_anim) }

    /*
    * Buttons
    * */
    private lateinit var fab: FloatingActionButton
    private lateinit var tickAllButton: FloatingActionButton
    private lateinit var newStoreCheckBoxButton: FloatingActionButton

    /*
    * Adapters
    * */
    private lateinit var checkBoxesAdapter: StoreCheckboxAdapter

    /*
    * Private properties
    * */
    private var addButtonClicked: Boolean = false

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("ShoppingFragment", "onSaveInstanceState called")
    }

    private val addNewStore = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult
            val storeName = data.getStringExtra("parentTitle") ?: return@registerForActivityResult
            val ingredients = data.getStringArrayListExtra("childItems") ?: return@registerForActivityResult

            val store = StoreCheckBox(
                storeName,
                ingredients.map { IngredientCheckbox(it, false) }
                    .distinct()
                    .toMutableList())

            // Add the new parent item to the list and update RecyclerView
            var storeIndex = sharedDataViewModel.findStoreIndex { store -> store.storeName == storeName }
            Log.d("ShoppingFragmentImpl", "Storeindex ${storeIndex }")

            if (storeIndex != -1 && storeIndex != null) {
                store.ingredients.forEach({ ingredient ->
                    if (!sharedDataViewModel.doesStoreContainIngredient(storeIndex, ingredient.title)) { //ingredient is not in sharedData's corresponding store so we can add this ingredient to the store
                        Log.d("ShoppingFragmentImpl", "Store ${store.storeName} doesnt contain ${ingredient.title}")
                        sharedDataViewModel.addIngredientToStore(storeIndex, ingredient)
                        storeDatabase.storeNewIngredientsInStore(store.storeName, ingredients)
                    } else {
                        Toast.makeText(app.applicationContext, "${ingredient.title} is already added to ${store.storeName}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                sharedDataViewModel.addStore(store)
                checkBoxesAdapter?.notifyItemInserted(sharedDataViewModel.getStoresSize()-1)
                saveStoreToDatabase(store)
            }

            footerViewModel.showFooter(true) //use interface
            sharedDataViewModel.updateEmptyPageVisibility()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val rootView = inflater.inflate(R.layout.shopping_fragment, container, false)

        HandleOpenButton(rootView) // these need to be before api call, so FAB button is initialized to make it invisible while API call is happening
        HandleTickButton(rootView)
        HandleNewButton(rootView)

        checkBoxesAdapter = StoreCheckBoxAdapterImpl(sharedDataViewModel, footerViewModel, viewLifecycleOwner)

        sharedDataViewModel.emptyPageView = rootView.findViewById(R.id.empty_shopping_list_page)

        if (sharedDataViewModel.getStoresSize() == 0) { // this way config change doesnt re-call the API. uses the stored state
            HandleShoppingListApiCall(rootView) { onSuccess ->
                sharedDataViewModel.setStores(storeDatabase.getStores().toMutableList())  // todo temporary db while backend not ready - put this inside of the API call to actually load some data for now.
                checkBoxesAdapter?.notifyDataSetChanged()
                sharedDataViewModel.updateEmptyPageVisibility()
                fab.visibility = View.VISIBLE

                if (sharedDataViewModel.getStoresSize() > 0) {
                    footerViewModel.showFooter(true)
                }
            }
        }

        val footerAdapter = FooterAdapterImpl(sharedDataViewModel, app, viewLifecycleOwner, footerViewModel)
        footerViewModel.setFooterAdapter(footerAdapter)

        val concatAdapter = ConcatAdapter(checkBoxesAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>, footerAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)

        val parentCheckBoxView: RecyclerView = rootView.findViewById(R.id.rvParent)
        parentCheckBoxView.layoutManager = LinearLayoutManager(context)
        parentCheckBoxView.adapter = concatAdapter

        // Observe the adapter (if needed)
        footerViewModel.footerAdapter.observe(viewLifecycleOwner, Observer { footerAdapter ->
        })

        return rootView
    }


    private fun HandleNewButton(rootView: View) {
        newStoreCheckBoxButton = rootView.findViewById(R.id.fab_opt2)
        newStoreCheckBoxButton.setOnClickListener {
            val ingredients = ingredientsViewModel.ingredients
            val intent = Intent(context, AddParentChildActivity::class.java)
            intent.putStringArrayListExtra("ingredients_list", ingredients)
            intent.putStringArrayListExtra("stores", GetStoresNames())
            addNewStore.launch(intent)
            Toast.makeText(context, getString(R.string.add_new_pressed), Toast.LENGTH_SHORT).show()
            onAddButtonClicked()
        }
    }

    private fun HandleTickButton(rootView: View) {
        tickAllButton = rootView.findViewById(R.id.fab_opt1)
        tickAllButton.setOnClickListener {
            // Select All functionality
            sharedDataViewModel.setAllIngredientChecked()
            checkBoxesAdapter?.notifyDataSetChanged() // Notify the adapter to update the UI
            onAddButtonClicked()
        }
    }

    private fun HandleOpenButton(rootView: View) {
        fab = rootView.findViewById(R.id.fab)
        fab.setOnClickListener {
            onAddButtonClicked()
        }
    }

    private fun HandleShoppingListApiCall(rootView: View,  onSuccess: (data: Any) -> Unit) { // THIS IS OVERWRITING THE RESTORESTATES ! MAKE SURE THIS CAN WORK TOGETHER WITH STATE RESTORING LATER ON
        val loadingView = rootView.findViewById<ProgressBar>(R.id.loadingView)
        val successView = rootView.findViewById<TextView>(R.id.successView)
        val errorView = rootView.findViewById<LinearLayout>(R.id.errorView)
        val retryButton = rootView.findViewById<Button>(R.id.retryButton)

        lifecycleScope.launch {
            shoppingListApiViewModel.apiState.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        Log.d("ShoppingFragment", "State: Loading")
                        loadingView?.visibility = View.VISIBLE
                        successView?.visibility = View.GONE
                        errorView?.visibility = View.GONE
                        footerViewModel.showFooter(false)

                        if (fab != null)
                            fab.visibility = View.GONE
                    }
                    is ApiState.Success -> {
                        Log.d("ShoppingFragment", "State: Success")
                        loadingView?.visibility = View.GONE
                        errorView?.visibility = View.GONE

                        onSuccess(state.data)
                    }
                    is ApiState.Error -> {
                        Log.d("ShoppingFragment", "State: Error")
                        loadingView?.visibility = View.GONE
                        successView?.visibility = View.GONE
                        errorView?.visibility = View.VISIBLE
                        footerViewModel.showFooter(false)
                        if (fab != null)
                            fab.visibility = View.GONE
                    }
                }
            }
        }

        retryButton?.setOnClickListener {
            shoppingListApiViewModel.fetchApiData()
        }

        // Fetch data initially
        shoppingListApiViewModel.fetchApiData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveStoresToDatabase()
    }

    private fun GetStoresNames(): ArrayList<String> {
        return sharedDataViewModel.getStoresNames()
    }

    private fun onAddButtonClicked() {
        setVisibility(addButtonClicked)
        setAnimation(addButtonClicked)
        setClickable(addButtonClicked)
        addButtonClicked = !addButtonClicked
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
        sharedDataViewModel.getAllStores().forEach { store ->
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
}