package com.example.ichef.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.R

class StoreCheckBoxAdapter(private val parents: MutableList<StoreCheckBox>, private var footerAdapter: FooterAdapter) : RecyclerView.Adapter<StoreCheckBoxAdapter.ParentViewHolder>() {

    inner class ParentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvParentTitle)
        val recyclerView: RecyclerView = view.findViewById(R.id.rvChildren)
        val deleteButton: ImageButton = view.findViewById(R.id.btnDeleteParent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parent, parent, false)
        return ParentViewHolder(view)
    }

    override fun onBindViewHolder(parentViewHolder: ParentViewHolder, position: Int) {
        val parent = parents[position]
        parentViewHolder.title.text = parent.storeName

        // Set up child RecyclerView
        parentViewHolder.recyclerView.layoutManager = LinearLayoutManager(parentViewHolder.itemView.context)
        parentViewHolder.recyclerView.adapter =
            IngredientCheckBoxAdapter(parent.ingredients.toMutableList(), { child, isChecked ->
                child.isChecked = isChecked
            }) { childPosition ->
                parent.ingredients.removeAt(childPosition)
                parentViewHolder.recyclerView.adapter?.notifyItemRemoved(childPosition)
            }

        // Handle parent deletion
        parentViewHolder.deleteButton.setOnClickListener {
            Log.d("ParentAdapter", "Delete button clicked for position $position")
            parents.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, parents.size)

            //handle footer
            if (parents.size == 0) {
                footerAdapter.showFooter(false)
            }
        }
    }

    private fun SetLastElementMargin(
        position: Int,
        layoutParams: ViewGroup.MarginLayoutParams
    ) {
        if (position == parents.size - 1) {
            // Convert 20dp to pixels
            layoutParams.bottomMargin = 320
        } else {
            // Reset margin for non-last items
            layoutParams.bottomMargin = 0
        }
    }

    override fun getItemCount() = parents.size
}


