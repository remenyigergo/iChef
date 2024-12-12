package com.example.ichef.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.R

class ChildAdapter(
    private val children: List<ChildItem>,
    private val onCheckBoxChanged: (ChildItem, Boolean) -> Unit
) : RecyclerView.Adapter<ChildAdapter.ChildViewHolder>() {

    inner class ChildViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.cbChild)
        val title: TextView = view.findViewById(R.id.tvChildTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_child, parent, false)
        return ChildViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val child = children[position]
        holder.title.text = child.title
        holder.checkBox.isChecked = child.isChecked
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckBoxChanged(child, isChecked)
        }
    }

    override fun getItemCount() = children.size
}
