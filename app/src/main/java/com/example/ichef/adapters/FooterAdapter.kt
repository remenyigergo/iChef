package com.example.ichef.adapters
import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.R
import com.example.ichef.activities.shoppingFragment

class FooterAdapter(private val onButtonClick: () -> Unit) :
    RecyclerView.Adapter<FooterAdapter.FooterViewHolder>() {

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
            if (shoppingFragment.tickedCount == 0) {
               Toast.makeText(shoppingFragment.context,
                   shoppingFragment.getString(R.string.nothing_is_ticked), Toast.LENGTH_SHORT).show()
            } else {
                onButtonClick()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun showFooter(show: Boolean){
        isFooterVisible = show
        notifyDataSetChanged()
    }

    // Only one footer item, when at least one parent checkbox is existing
    override fun getItemCount(): Int {
        return if (isFooterVisible) 1 else 0
    }
}
