package com.example.ichef.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.R
import com.example.ichef.adapters.interfaces.StoreCheckboxAdapter
import javax.inject.Inject

class StoreCheckBoxAdapterImpl @Inject constructor(
    private var sharedData: SharedData,
    private var footerViewModel: FooterViewModel,
    private var lifecycleOwner: LifecycleOwner
) : StoreCheckboxAdapter, RecyclerView.Adapter<StoreCheckBoxAdapterImpl.ParentViewHolder>() {

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
        parentViewHolder.title.text = sharedData.getStoreByIndex(position)?.storeName

        // Set up child RecyclerView
        parentViewHolder.recyclerView.layoutManager = LinearLayoutManager(parentViewHolder.itemView.context)

        parentViewHolder.recyclerView.adapter =
            IngredientCheckBoxAdapterImpl(sharedData, position, { child, isChecked ->
                child.isChecked = isChecked
            }, parentViewHolder, lifecycleOwner)

        // Handle parent deletion
        parentViewHolder.deleteButton.setOnClickListener {
            Log.d("ParentAdapter", "Delete button clicked for position $position")

            sharedData.removeStore(position)
            notifyItemRemoved(position)

            val storesSize = sharedData.getStoresSize()
            notifyItemRangeChanged(position, storesSize)

            if (storesSize == 0) {
                footerViewModel.showFooter(false)
                sharedData.setAllChecked(false)
                sharedData.setTickedCount(0)
                sharedData.updateEmptyPageVisibility()
            } else {
                //decrement tickedCount because of deletion
                val store = sharedData.getStoreByIndex(position-1)
                var newTickedCount = 0
                if (sharedData.tickedCount.value!! - store?.ingredients?.size!! < 0) {
                    newTickedCount = 0
                } else {
                    newTickedCount = sharedData.tickedCount.value!! - store?.ingredients?.size!!
                }
                sharedData.setTickedCount(newTickedCount)
            }
        }
    }

    override fun getItemCount() = sharedData.getStoresSize()
}


