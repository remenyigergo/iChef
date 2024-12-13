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

class ParentAdapter(
    private val parents: MutableList<ParentItem>,
    private val onDeleteParent: (Int) -> Unit
) : RecyclerView.Adapter<ParentAdapter.ParentViewHolder>() {

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

    private var previousLastPosition: Int = -1 // Tracks the previous last position

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val parent = parents[position]
        holder.title.text = parent.title

        // Disable scrolling for the child RecyclerView to let the parent RecyclerView handle scrolling
        //holder.recyclerView.setNestedScrollingEnabled(false)

        // Get current item's layout parameters
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams

        // Apply bottom margin only to the last item
        if (position == parents.size - 1) {
            // Convert 20dp to pixels
            layoutParams.bottomMargin = 320
        } else {
            // Reset margin for non-last items
            layoutParams.bottomMargin = 0
        }

        // Set up child RecyclerView
        holder.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.recyclerView.adapter =
            ChildAdapter(parent.children.toMutableList(), { child, isChecked ->
                child.isChecked = isChecked
            }) { childPosition ->
                parent.children.removeAt(childPosition)
                holder.recyclerView.adapter?.notifyItemRemoved(childPosition)
            }

        // Handle parent deletion
        holder.deleteButton.setOnClickListener {
            Log.d("ParentAdapter", "Delete button clicked for position $position")
            parents.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, parents.size)
        }
    }

    override fun getItemCount() = parents.size
}


