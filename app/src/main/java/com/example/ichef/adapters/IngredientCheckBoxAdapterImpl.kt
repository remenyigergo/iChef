package com.example.ichef.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.R
import com.example.ichef.adapters.interfaces.IngredientCheckboxAdapter
import com.example.ichef.models.IngredientCheckbox
import javax.inject.Inject

class IngredientCheckBoxAdapterImpl @Inject constructor(
    private var sharedData: SharedData,
    private var storeIndex: Int,
    private val onChildCheckedChange: (IngredientCheckbox, Boolean) -> Unit,
    private val parentViewHolder: StoreCheckBoxAdapterImpl.ParentViewHolder,
    private val lifecycleOwner: LifecycleOwner // To observe LiveData
) : IngredientCheckboxAdapter,
    RecyclerView.Adapter<IngredientCheckBoxAdapterImpl.ChildViewHolder>() {

    private var currentTickedCount: Int = 0 // Cache ticked count for the click listener

    inner class ChildViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ingredientLayout: LinearLayout = view.findViewById(R.id.ingredient_layout)
        val checkBox: CheckBox = view.findViewById(R.id.cbChild)
        val deleteButton: ImageButton = view.findViewById(R.id.btnDeleteChild)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_child, parent, false)

        SetupObserver()

        return ChildViewHolder(view)
    }

    private fun SetupObserver() {
        // Observe tickedCount changes here
        sharedData.tickedCount.observe(lifecycleOwner, Observer { tickedCount ->
            currentTickedCount = tickedCount ?: 0
        })
    }

    override fun onBindViewHolder(holder: ChildViewHolder, ingredientIndex: Int) {
        storeIndex = getParentPosition()
        val child = sharedData.getIngredientCheckboxByIndex(storeIndex, ingredientIndex)
        holder.checkBox.text = child?.title
        holder.checkBox.isChecked = child?.isChecked ?: false

        // Handle child checkbox change
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sharedData.incrementTick()
                sharedData.checkIngredient(
                    storeIndex,
                    ingredientIndex,
                    true
                ) //change the list of ingredients check state
            } else {
                if (currentTickedCount > 0) {
                    sharedData.decreaseTick()
                    sharedData.checkIngredient(
                        storeIndex,
                        ingredientIndex,
                        false
                    ) //change the list of ingredients check state
                }
            }
        }

        // Handle child checkbox when area clicked
        holder.ingredientLayout.setOnClickListener {
            holder.checkBox.isChecked = !holder.checkBox.isChecked
            if (child != null)
                onChildCheckedChange(child, holder.checkBox.isChecked)
        }

        // Handle child deletion
        holder.deleteButton.setOnClickListener {
            Log.i("ChildAdapter", "Deleting child in pos: $ingredientIndex")
            sharedData.removeIngredient(sharedData.getStoreByIndex(storeIndex)?.storeName, sharedData.getIngredientCheckboxByIndex(storeIndex,ingredientIndex)?.title) //todo inject DB here and directly delete not through shared

            notifyItemRemoved(ingredientIndex)
            notifyItemRangeChanged(
                ingredientIndex,
                sharedData.getStoreByIndex(storeIndex)?.ingredients?.size ?: 0
            ) // Adjust the subsequent items' positions

            if (currentTickedCount > 0) {
                sharedData.decreaseTick()
            }

            if (sharedData.getStoreByIndex(storeIndex)?.ingredients?.size == 0) {
                parentViewHolder.deleteButton.performClick()
            }

            //debug toast
            Log.d("IngredientCheckBoxAdapter", "tickedCount: ${sharedData.tickedCount}")
            //Toast.makeText(shoppingFragment.context, "${shoppingFragment.tickedCount}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        val position = getParentPosition()
        return sharedData.getStoreByIndex(position)?.ingredients?.size ?: 0
    }

    //when deleting the childs, make sure we set the parentPosition to the actual value, so we dont get out of bounds later
    private fun getParentPosition(): Int {
        if (storeIndex <= sharedData.getStoresSize() - 1) {
            return storeIndex
        }

        return storeIndex - 1
    }
}


