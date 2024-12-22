package com.example.ichef.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.R
import com.example.ichef.adapters.interfaces.IngredientCheckboxAdapter
import com.example.ichef.fragments.ShoppingFragmentImpl
import com.example.ichef.fragments.interfaces.ShoppingFragment
import com.example.ichef.models.IngredientCheckbox
import javax.inject.Inject

class IngredientCheckBoxAdapterImpl @Inject constructor(
    //private var shoppingFragment: ShoppingFragment,
    private var sharedData: SharedData,
    private var parentPosition: Int,
    private val onChildCheckedChange: (IngredientCheckbox, Boolean) -> Unit,
    private val parentViewHolder: StoreCheckBoxAdapterImpl.ParentViewHolder
) : IngredientCheckboxAdapter,
    RecyclerView.Adapter<IngredientCheckBoxAdapterImpl.ChildViewHolder>() {

    inner class ChildViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ingredientLayout: LinearLayout = view.findViewById(R.id.ingredient_layout)
        val checkBox: CheckBox = view.findViewById(R.id.cbChild)
        val deleteButton: ImageButton = view.findViewById(R.id.btnDeleteChild)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_child, parent, false)
        return ChildViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        parentPosition = getParentPosition()
        var stores = sharedData.stores
        val child = stores[parentPosition].ingredients[position]
        holder.checkBox.text = child.title
        holder.checkBox.isChecked = child.isChecked

        // Handle child checkbox change
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            val tickedCount = sharedData.tickedCount
            if (isChecked) {
                sharedData.incrementTick()
                sharedData.checkIngredient(
                    parentPosition,
                    position,
                    true
                ) //change the list of ingredients check state
            } else {
                if (tickedCount > 0) {
                    sharedData.decreaseTick()
                    sharedData.checkIngredient(
                        parentPosition,
                        position,
                        false
                    ) //change the list of ingredients check state
                }
            }
        }

        // Handle child checkbox when area clicked
        holder.ingredientLayout.setOnClickListener {
            holder.checkBox.isChecked = !holder.checkBox.isChecked
            onChildCheckedChange(child, holder.checkBox.isChecked)
        }

        // Handle child deletion
        holder.deleteButton.setOnClickListener {
            Log.i("ChildAdapter", "Deleting child in pos: $position")
            var stores = sharedData.stores
            var tickedCount = sharedData.tickedCount
            stores[parentPosition].ingredients.removeAt(position)

            notifyItemRemoved(position)
            notifyItemRangeChanged(
                position,
                stores[parentPosition].ingredients.size
            ) // Adjust the subsequent items' positions

            if (tickedCount > 0) {
                sharedData.decreaseTick()
            }

            if (stores[parentPosition].ingredients.size == 0) {
                parentViewHolder.deleteButton.performClick()
            }

            //debug toast
            Log.d("IngredientCheckBoxAdapter", "tickedCount: ${tickedCount}")
            //Toast.makeText(shoppingFragment.context, "${shoppingFragment.tickedCount}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        val position = getParentPosition()
        var stores = sharedData.stores
        return stores[position].ingredients.size
    }

    //when deleting the childs, make sure we set the parentPosition to the actual value, so we dont get out of bounds later
    private fun getParentPosition(): Int {
        var stores = sharedData.stores
        if (parentPosition <= stores.size - 1) {
            return parentPosition
        }

        return parentPosition - 1
    }
}


