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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.AddParentChildActivity
import com.example.ichef.R
import com.example.ichef.adapters.SharedData
import com.example.ichef.models.IngredientCheckbox
import com.example.ichef.adapters.interfaces.FooterAdapter
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
    lateinit var checkBoxesAdapter: StoreCheckboxAdapter
    @Inject
    lateinit var footerAdapter: FooterAdapter
    @Inject
    lateinit var sharedData: SharedData
    @Inject
    lateinit var app: Application
    @Inject
    lateinit var storeDatabase: ShoppingDataManager

    private lateinit var shoppingListApiViewModel: ShoppingListApiViewModel

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.to_bottom_anim) }

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
                    if (sharedData.stores[storeIndex].ingredients.find{it.title == ingredient.title} == null) { //ingredient is not in sharedData's corresponding store so we can add this ingredient to the store
                        sharedData.stores[storeIndex].ingredients.add(ingredient)
                        storeDatabase.storeNewIngredientsInStore(store.storeName, ingredients)
                    } else {
                        Toast.makeText(app.applicationContext, "${ingredient.title} is already added to ${store.storeName}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                sharedData.stores.add(store)
                checkBoxesAdapter?.notifyItemInserted(sharedData.stores.size-1)
                saveStoreToDatabase(store)
            }

            footerAdapter.showFooter(true) //use interface
            sharedData.SetEmptyPageVisibilty()
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

        restoreStates(savedInstanceState)

        HandleShoppingListApiCall(rootView) { data ->
            //loadStoresFromDatabase() // todo temporary db while backend not ready - put this inside of the API call to actually load some data for now

            // Get empty layout to make it visible if nothing in shopping list for the first time
            sharedData.emptyPageView = rootView.findViewById(R.id.empty_shopping_list_page)
            sharedData.SetEmptyPageVisibilty()
            if (sharedData.stores.size > 0) {
                footerAdapter.showFooter(true)
            }
        }

        val concatAdapter = ConcatAdapter(checkBoxesAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>, footerAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>)

        val parentCheckBoxView: RecyclerView = rootView.findViewById(R.id.rvParent)
        parentCheckBoxView.layoutManager = LinearLayoutManager(context)
        parentCheckBoxView.adapter = concatAdapter

        HandleOpenButton(rootView)
        HandleTickButton(rootView)
        HandleNewButton(rootView)

        return rootView
    }

    private fun HandleNewButton(rootView: View) {
        newStoreCheckBoxButton = rootView.findViewById(R.id.fab_opt2)
        newStoreCheckBoxButton.setOnClickListener {
            val ingredients = viewModel.ingredients
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
            if (sharedData.stores.size > 0) {
                if (sharedData.allChecked) {
                    sharedData.allChecked = false
                    sharedData.tickedCount =
                        0 //reset to 0 selection, when everything was ticked off with the button : FIX for button showing everything was purchased even though nothing was ticked
                    Toast.makeText(
                        context,
                        getString(R.string.check_all_unpressed),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    sharedData.allChecked = true
                    sharedData.tickedCount =
                        0 //reset to 0 selection, when everything was ticked off with the button

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
            sharedData.stores.forEach { parent ->
                parent.ingredients.forEach { child ->
                    child.isChecked = sharedData.allChecked
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

    private fun HandleShoppingListApiCall(rootView: View,  onSuccess: (data: Any) -> Unit) {
        shoppingListApiViewModel = ViewModelProvider(this).get(ShoppingListApiViewModel::class.java)

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


}
