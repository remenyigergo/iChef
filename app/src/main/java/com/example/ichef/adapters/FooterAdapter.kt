package com.example.ichef.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.R

class FooterAdapter(private val onButtonClick: () -> Unit) :
    RecyclerView.Adapter<FooterAdapter.FooterViewHolder>() {

    private var isFooterVisible: Boolean = false

    inner class FooterViewHolder(val purchasedButton: Button) :
        RecyclerView.ViewHolder(purchasedButton) {
            fun bind(isVisible: Boolean) {
                itemView.visibility = if (isVisible) View.VISIBLE else View.GONE
            }
        }


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
            onButtonClick()
        }

        if (holder is FooterViewHolder) {
            holder.bind(isFooterVisible)
        }
    }


    fun showFooter(show: Boolean){
        isFooterVisible = show
        notifyDataSetChanged()
    }

    // Only one footer item, when at least one parent checkbox is existing
    override fun getItemCount(): Int {
        return if (isFooterVisible) 1 else 0
    }
}
