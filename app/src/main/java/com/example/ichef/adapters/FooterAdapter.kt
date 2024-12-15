package com.example.ichef.adapters

import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.R

class FooterAdapter(private val onButtonClick: () -> Unit, checkboxesCount: Int?) : RecyclerView.Adapter<FooterAdapter.FooterViewHolder>() {

    val checkboxesCount = checkboxesCount
    inner class FooterViewHolder(val purchasedButton: Button) : RecyclerView.ViewHolder(purchasedButton)

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
    }

    // Only one footer item, when at least one parent checkbox is existing
    override fun getItemCount() : Int {
//        if (checkboxesCount != null && checkboxesCount >= 1) {
//            return 1
//        }
//        return 0

        return 1;
    }
}
