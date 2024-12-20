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
import com.example.ichef.fragments.ShoppingFragment

class StoreCheckBoxAdapter(private var footerAdapter: FooterAdapter, private var shoppingFragment: ShoppingFragment) : RecyclerView.Adapter<StoreCheckBoxAdapter.ParentViewHolder>() {

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
        var stores = shoppingFragment.getStores()
        parentViewHolder.title.text = stores[position].storeName

        // Set up child RecyclerView
        parentViewHolder.recyclerView.layoutManager = LinearLayoutManager(parentViewHolder.itemView.context)
        parentViewHolder.recyclerView.adapter =
            IngredientCheckBoxAdapter(shoppingFragment, position, { child, isChecked ->
                child.isChecked = isChecked
            }, parentViewHolder)

        // Handle parent deletion
        parentViewHolder.deleteButton.setOnClickListener {
            Log.d("ParentAdapter", "Delete button clicked for position $position")
            stores = shoppingFragment.getStores()
            stores.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, stores.size)

            //handle footer
            if (stores.size == 0) {
                footerAdapter.showFooter(false)
                //set allClicked to false because no element is presented
                shoppingFragment.setAllChecked(false)
                shoppingFragment.SetEmptyPageVisibilty()
            }
        }
    }

    override fun getItemCount() = shoppingFragment.getStores().size
}


