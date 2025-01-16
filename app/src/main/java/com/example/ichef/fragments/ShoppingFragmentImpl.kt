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
import androidx.lifecycle.LifecycleOwner
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
    * Observer properties
    * */
    private var allChecked: Boolean = false


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("ShoppingFragment", "onSaveInstanceState called")
        Log.d("ShoppingFragment", "addButtonClicked: ${sharedDataViewModel.addButtonClicked}")

        outState.putBoolean("addButtonClicked", sharedDataViewModel.addButtonClicked)

        HandleStates(outState)
    }

    private fun HandleStates(outState: Bundle) {
        for (store in sharedDataViewModel.stores) {
            for (ingredient in store.ingredients) {
                val key = "${store.storeName}_${ingredient.title}"
                outState.putBoolean("${store.storeName}_${ingredient.title}", ingredient.isChecked)
                Log.d("ShoppingFragment", "Saving ingredient state - $key: ${ingredient.isChecked}")
            }
        }
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
                ingredients.map { IngredientCheckbox(it, false) }.toMutableList()
            )

            // Add the new parent item to the list and update RecyclerView
            var storeIndex = getStoreIndex(storeName)
            if (storeIndex != -1) {
                store.ingredients.forEach({ ingredient ->
                    if (sharedDataViewModel.stores[storeIndex].ingredients.find{it.title == ingredient.title} == null) { //ingredient is not in sharedData's corresponding store so we can add this ingredient to the store
                        sharedDataViewModel.stores[storeIndex].ingredients.add(ingredient)
                        storeDatabase.storeNewIngredientsInStore(store.storeName, ingredients)
                    } else {
                        Toast.makeText(app.applicationContext, "${ingredient.title} is already added to ${store.storeName}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                sharedDataViewModel.stores.add(store)
                checkBoxesAdapter?.notifyItemInserted(sharedDataViewModel.stores.size-1)
                saveStoreToDatabase(store)
            }

            footerViewModel.showFooter(true) //use interface
            sharedDataViewModel.SetEmptyPageVisibilty()
        }
    }

    private fun getStoreIndex(storeName: String): Int {
        var positionOfStore = -1
        var iteration = 0
        sharedDataViewModel.stores.forEach({ store ->
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
        SetupObservers()

        // Inflate the layout
        val rootView = inflater.inflate(R.layout.shopping_fragment, container, false)
        checkBoxesAdapter = StoreCheckBoxAdapterImpl(sharedDataViewModel, footerViewModel, viewLifecycleOwner)

        restoreStates(savedInstanceState)

        sharedDataViewModel.emptyPageView = rootView.findViewById(R.id.empty_shopping_list_page)

        if (sharedDataViewModel.getStoresSize() == 0) { // this way config change doesnt re-call the API. uses the stored state
            HandleShoppingListApiCall(rootView) { onSuccess ->
                sharedDataViewModel.stores = storeDatabase.getStores().toMutableList() // todo temporary db while backend not ready - put this inside of the API call to actually load some data for now.
                checkBoxesAdapter?.notifyDataSetChanged()
                sharedDataViewModel.SetEmptyPageVisibilty()

                // Set empty layout to make it visible if needed
                if (sharedDataViewModel.stores.size > 0) {
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

        HandleOpenButton(rootView)
        HandleTickButton(rootView)
        HandleNewButton(rootView)

        return rootView
    }

    private fun SetupObservers() {
        sharedDataViewModel.allChecked.observe(viewLifecycleOwner) { _allChecked ->
            allChecked = _allChecked
        }
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
            if (sharedDataViewModel.stores.size > 0) {
                if (allChecked) {
                    sharedDataViewModel.setAllChecked(false)
                    sharedDataViewModel.setTickedCount(0) //reset to 0 selection, when everything was ticked off with the button : FIX for button showing everything was purchased even though nothing was ticked
                    Toast.makeText(
                        context,
                        getString(R.string.check_all_unpressed),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    sharedDataViewModel.setAllChecked(true)
                    sharedDataViewModel.setTickedCount(0) //reset to 0 selection, when everything was ticked off with the button

                    Toast.makeText(
                        context,
                        getString(R.string.check_all_pressed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.nothing_to_tick_here),
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Select All functionality
            sharedDataViewModel.stores.forEach { parent ->
                parent.ingredients.forEach { child ->
                    child.isChecked = allChecked
                }
            }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun HandleShoppingListApiCall(rootView: View,  onSuccess: (data: Any) -> Unit) { // THIS IS OVERWRITING THE RESTORESTATES ! MAKE SURE THIS CAN WORK TOGETHER WITH STATE RESTORING LATER ON
//        shoppingListApiViewModel = ViewModelProvider(this).get(ShoppingListApiViewModel::class.java)

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
                    }
                    is ApiState.Success -> {
                        Log.d("ShoppingFragment", "State: Success")
                        loadingView?.visibility = View.GONE
                        //successView?.visibility = View.VISIBLE
                        errorView?.visibility = View.GONE
                        // Update your UI with data
                        onSuccess(state.data)
                    }
                    is ApiState.Error -> {
                        Log.d("ShoppingFragment", "State: Error")
                        loadingView?.visibility = View.GONE
                        successView?.visibility = View.GONE
                        errorView?.visibility = View.VISIBLE
                        footerViewModel.showFooter(false)
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

    private fun restoreStates(savedInstanceState: Bundle?) {
        Log.d("ShoppingFragmentImpl", "Restoring state")
        //restoring variables if config change happens (light-dark-mode switch)
        sharedDataViewModel.addButtonClicked = savedInstanceState?.getBoolean("addButtonClicked") ?: false
        //sharedData.allChecked = savedInstanceState?.getBoolean("allChecked") ?: false
        //sharedData.tickedCount = savedInstanceState?.getInt("tickedCount") ?: 0
        Log.d("ShoppingFragmentImpl", "TickedCount: ${sharedDataViewModel.tickedCount.value}")

        // Restore the state of the checkboxes if available
        savedInstanceState?.let {
            for (store in sharedDataViewModel.stores) {
                for (ingredient in store.ingredients) {
                    Log.d("ShoppingFragmentImpl", "Restoring ${ingredient}")
                    ingredient.isChecked =
                        it.getBoolean("${store.storeName}_${ingredient.title}", false)
                }
            }
        }
    }

    private fun GetStoresNames(): ArrayList<String>? {
        var storesNames = ArrayList<String>()
        sharedDataViewModel.stores.forEach({ checkboxElement-> storesNames.add(checkboxElement.storeName)})
        return storesNames
    }

    private fun onAddButtonClicked() {
        setVisibility(sharedDataViewModel.addButtonClicked)
        setAnimation(sharedDataViewModel.addButtonClicked)
        setClickable(sharedDataViewModel.addButtonClicked)
        sharedDataViewModel.addButtonClicked = !sharedDataViewModel.addButtonClicked
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
        sharedDataViewModel.stores.forEach { store ->
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
