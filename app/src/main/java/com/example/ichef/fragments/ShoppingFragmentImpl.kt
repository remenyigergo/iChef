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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.AddParentChildActivity
import com.example.ichef.R
import com.example.ichef.models.IngredientCheckbox
import com.example.ichef.adapters.FooterAdapter
import com.example.ichef.adapters.StoreCheckBoxAdapter
import com.example.ichef.database.ShoppingDataManager
import com.example.ichef.models.StoreCheckBox
import com.example.ichef.models.IngredientsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ShoppingFragmentImpl : Fragment(), ShoppingFragment {

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.to_bottom_anim) }

    private val storeDatabase by lazy { ShoppingDataManager(requireContext()) }

    private var addButtonClicked: Boolean = false
    private var allChecked: Boolean = false
    private var tickedCount = 0

    private lateinit var fab: FloatingActionButton
    private lateinit var tickAllButton: FloatingActionButton
    private lateinit var newStoreCheckBoxButton: FloatingActionButton

    //by viewModels means config change is not voiding out the property
    private val viewModel: IngredientsViewModel by viewModels()

    private var checkBoxesAdapter: StoreCheckBoxAdapter? = null

    @Inject
    lateinit var footerAdapter: FooterAdapter
    private lateinit var emptyPageView: ConstraintLayout

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("addButtonClicked", addButtonClicked)
        outState.putBoolean("allChecked", allChecked)
        outState.putInt("tickedCount", tickedCount)
        HandleStates(outState)
    }

    private fun HandleStates(outState: Bundle) {
        for (store in stores) {
            for (ingredient in store.ingredients) {
                outState.putBoolean("${store.storeName}_${ingredient.title}", ingredient.isChecked)
            }
        }
    }

    /*
    * This is only to make a mock GET from DB, should be empty from start or GET API should load up these
    * */
    private var stores : MutableList<StoreCheckBox> = mutableListOf()
//    private var stores = mutableListOf(
//        StoreCheckBox("Aldi", mutableListOf(IngredientCheckbox("Kenyér", false), IngredientCheckbox("Római kömény", false))),
//        StoreCheckBox("Lidl", mutableListOf(IngredientCheckbox("Paradicsom", false), IngredientCheckbox("Paprika", false), IngredientCheckbox("Olaj", false), IngredientCheckbox("Narancs", false), IngredientCheckbox("Citrom", false), IngredientCheckbox("Fahéj", false))),
//        StoreCheckBox("Spar", mutableListOf(IngredientCheckbox("Mogyoróvaj", false), IngredientCheckbox("Fűszerpaprika", false), IngredientCheckbox("Alma", false)))
//    )

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
                checkBoxesAdapter?.notifyItemInserted(stores.size-1)
                saveStoreToDatabase(store)
            }

            if (::footerAdapter.isInitialized) {
                footerAdapter.showFooter(true) //use interface
                SetEmptyPageVisibilty()
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
        restoreStates(savedInstanceState)
        loadStoresFromDatabase() // todo temporary db while backend not ready
        
        // Get empty layout to make it visible if nothing in shopping list for the first time
        emptyPageView = rootView.findViewById(R.id.empty_shopping_list_page)
        SetEmptyPageVisibilty()

        checkBoxesAdapter = StoreCheckBoxAdapter(footerAdapter, this)


        /*
            TODO make http GET call to backend and upload list to stores variable
        */
        if (stores.size > 0) {
            footerAdapter.showFooter(true)
        }

        // Combine adapters using ConcatAdapter
        val concatAdapter = ConcatAdapter(checkBoxesAdapter, footerAdapter)

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
                    tickedCount = 0 //reset to 0 selection, when everything was ticked off with the button : FIX for button showing everything was purchased even though nothing was ticked
                    Toast.makeText(context, getString(R.string.check_all_unpressed), Toast.LENGTH_SHORT).show()
                } else {
                    allChecked = true
                    tickedCount = 0 //reset to 0 selection, when everything was ticked off with the button

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
        addButtonClicked = savedInstanceState?.getBoolean("addButtonClicked") ?: false
        allChecked = savedInstanceState?.getBoolean("allChecked") ?: false
        tickedCount = savedInstanceState?.getInt("tickedCount") ?: 0

        // Restore the state of the checkboxes if available
        savedInstanceState?.let {
            for (store in stores) {
                for (ingredient in store.ingredients) {
                    ingredient.isChecked =
                        it.getBoolean("${store.storeName}_${ingredient.title}", false)
                }
            }
        }
    }

    private fun GetStoresNames(): ArrayList<String>? {
        var storesNames = ArrayList<String>()
        stores.forEach({checkboxElement-> storesNames.add(checkboxElement.storeName)})
        return storesNames
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

    override fun SetEmptyPageVisibilty() {
        if (stores.size == 0) {
            emptyPageView.visibility = View.VISIBLE
        } else {
            emptyPageView.visibility = View.INVISIBLE
        }
    }

    override fun checkIngredient(storePosition: Int, ingredientPosition: Int, value: Boolean) {
        stores[storePosition].ingredients[ingredientPosition].isChecked = value
        footerAdapter.reloadShoppingFragment(this)
    }

    override fun getStores(): MutableList<StoreCheckBox> {
        return stores
    }

    override fun getTickedCount(): Int {
        return tickedCount
    }

    override fun incrementTick() {
        tickedCount++
    }

    override fun decreaseTick() {
        tickedCount--
    }

    override fun isAllChecked(): Boolean {
        return allChecked
    }

    override fun setAllChecked(value: Boolean) {
        allChecked = value
    }

    private fun saveStoresToDatabase() {
        stores.forEach { store ->
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
        stores = storeDatabase.getStores().toMutableList()
        checkBoxesAdapter?.notifyDataSetChanged()
    }
}
