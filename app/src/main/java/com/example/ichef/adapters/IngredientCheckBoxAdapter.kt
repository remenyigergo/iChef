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
import com.example.ichef.fragments.ShoppingFragment
import com.example.ichef.models.IngredientCheckbox

class IngredientCheckBoxAdapter(
    private var shoppingFragment: ShoppingFragment,
    private var parentPosition: Int,
    private val onChildCheckedChange: (IngredientCheckbox, Boolean) -> Unit,
    private val parentViewHolder: StoreCheckBoxAdapter.ParentViewHolder
) : RecyclerView.Adapter<IngredientCheckBoxAdapter.ChildViewHolder>() {

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
        var stores = shoppingFragment.getStores()
        val child = stores[parentPosition].ingredients[position]
        holder.checkBox.text = child.title
        holder.checkBox.isChecked = child.isChecked

        // Handle child checkbox change
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            val tickedCount = shoppingFragment.getTickedCount()
            if (isChecked) {
                shoppingFragment.incrementTick()
                shoppingFragment.checkIngredient(parentPosition,position,true) //change the list of ingredients check state
            } else {
                if(tickedCount > 0) {
                    shoppingFragment.decreaseTick()
                    shoppingFragment.checkIngredient(parentPosition,position,false) //change the list of ingredients check state
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
            Log.i("ChildAdapter","Deleting child in pos: $position")
            var stores = shoppingFragment.getStores()
            var tickedCount = shoppingFragment.getTickedCount()
            stores[parentPosition].ingredients.removeAt(position)

            notifyItemRemoved(position)
            notifyItemRangeChanged(position, stores[parentPosition].ingredients.size) // Adjust the subsequent items' positions

            if (tickedCount > 0) {
                shoppingFragment.decreaseTick()
            }

            if (stores[parentPosition].ingredients.size == 0) {
                parentViewHolder.deleteButton.performClick()
            }

            //debug toast
            Log.d("IngredientCheckBoxAdapter","tickedCount: ${tickedCount}")
            //Toast.makeText(shoppingFragment.context, "${shoppingFragment.tickedCount}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        val position = getParentPosition()
        var stores = shoppingFragment.getStores()
        return stores[position].ingredients.size
    }

    //when deleting the childs, make sure we set the parentPosition to the actual value, so we dont get out of bounds later
    private fun getParentPosition(): Int {
        var stores = shoppingFragment.getStores()
        if (parentPosition <= stores.size - 1) {
            return parentPosition
        }

        return parentPosition-1
    }
}


