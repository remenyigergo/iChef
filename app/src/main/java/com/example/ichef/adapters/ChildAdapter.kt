package com.example.ichef.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.R

class ChildAdapter(
    private val children: MutableList<ChildItem>,
    private val onChildCheckedChange: (ChildItem, Boolean) -> Unit,
    private val onDeleteChild: (Int) -> Unit
) : RecyclerView.Adapter<ChildAdapter.ChildViewHolder>() {

    inner class ChildViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.cbChild)
        val deleteButton: ImageButton = view.findViewById(R.id.btnDeleteChild)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_child, parent, false)
        return ChildViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {

        val child = children[position]
        holder.checkBox.text = child.title
        holder.checkBox.isChecked = child.isChecked

        // Handle checkbox change
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onChildCheckedChange(child, isChecked)
        }

        // Handle child deletion
        holder.deleteButton.setOnClickListener {
            Log.i("ChildAdapter","Deleting child in pos: "+position)
            children.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, children.size) // Adjust the subsequent items' positions
        }
    }

    override fun getItemCount() = children.size
}


