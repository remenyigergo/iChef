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
        val child = shoppingFragment.stores[parentPosition].ingredients[position]
        holder.checkBox.text = child.title
        holder.checkBox.isChecked = child.isChecked

        // Handle child checkbox change
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                shoppingFragment.tickedCount++
            } else {
                if(shoppingFragment.tickedCount > 0) {
                    shoppingFragment.tickedCount--
                }
            }
        }

        // Handle child checkbox when area clicked
        holder.ingredientLayout.setOnClickListener {
            if (holder.checkBox.isChecked) {
                shoppingFragment.tickedCount--
            } else {
                shoppingFragment.tickedCount++
            }

            holder.checkBox.isChecked = !holder.checkBox.isChecked
            onChildCheckedChange(child, holder.checkBox.isChecked)
        }

        // Handle child deletion
        holder.deleteButton.setOnClickListener {
            Log.i("ChildAdapter","Deleting child in pos: $position")
            shoppingFragment.stores[parentPosition].ingredients.removeAt(position)

            notifyItemRemoved(position)
            notifyItemRangeChanged(position, shoppingFragment.stores[parentPosition].ingredients.size) // Adjust the subsequent items' positions

            if (shoppingFragment.tickedCount > 0) {
                shoppingFragment.tickedCount--
            }

            if (shoppingFragment.stores[parentPosition].ingredients.size == 0) {
                parentViewHolder.deleteButton.performClick()
            }

            //debug toast
            Log.d("IngredientCheckBoxAdapter","tickedCount: ${shoppingFragment.tickedCount}")
            //Toast.makeText(shoppingFragment.context, "${shoppingFragment.tickedCount}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        val position = getParentPosition()
        return shoppingFragment.stores[position].ingredients.size
    }

    //when deleting the childs, make sure we set the parentPosition to the actual value, so we dont get out of bounds later
    private fun getParentPosition(): Int {
        if (parentPosition <= shoppingFragment.stores.size - 1) {
            return parentPosition
        }

        return parentPosition-1
    }
}


