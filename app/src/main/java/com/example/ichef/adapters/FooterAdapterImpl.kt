package com.example.ichef.adapters

import android.app.Application
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.R
import com.example.ichef.adapters.interfaces.FooterAdapter
import javax.inject.Inject

class FooterAdapterImpl @Inject constructor(
    private var sharedData: SharedData,
    private val context: Application
) :
    FooterAdapter,
    RecyclerView.Adapter<FooterAdapterImpl.FooterViewHolder>() {

    private var isFooterVisible: Boolean = false

    inner class FooterViewHolder(val purchasedButton: Button) :
        RecyclerView.ViewHolder(purchasedButton)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FooterViewHolder {
        val button = Button(parent.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1f
                bottomMargin = 200
            }
            text = context.getString(R.string.purchased)
        }

        return FooterViewHolder(button)
    }

    override fun onBindViewHolder(holder: FooterViewHolder, position: Int) {
        holder.purchasedButton.setOnClickListener {
            if (sharedData.tickedCount == 0 && !sharedData.isAllChecked()) {
                Toast.makeText(
                    context,
                    context.getString(R.string.nothing_is_ticked), Toast.LENGTH_SHORT
                ).show()
            } else {
                // clicked button
                Toast.makeText(context.applicationContext, context.getString(R.string.purchased_button_pressed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun showFooter(show: Boolean) {
        isFooterVisible = show
        notifyDataSetChanged()
    }

    // Only one footer item, when at least one parent checkbox is existing
    override fun getItemCount(): Int {
        return if (isFooterVisible) 1 else 0
    }
}
