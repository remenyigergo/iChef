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

class ParentAdapter(public val parents: MutableList<ParentItem>) : RecyclerView.Adapter<ParentAdapter.ParentViewHolder>() {

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
        parentViewHolder.title.text = parent.title

        // Get current item's layout parameters
        val layoutParams = parentViewHolder.itemView.layoutParams as ViewGroup.MarginLayoutParams

        // Apply bottom margin only to the last item
        //SetLastElementMargin(position, layoutParams)

        // Set up child RecyclerView
        parentViewHolder.recyclerView.layoutManager = LinearLayoutManager(parentViewHolder.itemView.context)
        parentViewHolder.recyclerView.adapter =
            ChildAdapter(parent.children.toMutableList(), { child, isChecked ->
                child.isChecked = isChecked
            }) { childPosition ->
                parent.children.removeAt(childPosition)
                parentViewHolder.recyclerView.adapter?.notifyItemRemoved(childPosition)
            }

        // Handle parent deletion
        parentViewHolder.deleteButton.setOnClickListener {
            Log.d("ParentAdapter", "Delete button clicked for position $position")
            parents.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, parents.size)
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


