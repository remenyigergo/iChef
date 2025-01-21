package com.example.ichef.adapters

import android.app.Application
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.ichef.R
import com.example.ichef.adapters.interfaces.FooterAdapter
import com.google.android.material.button.MaterialButton
import javax.inject.Inject

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class FooterViewModel @Inject constructor() : ViewModel() {

    // Store adapter in ViewModel
    private val _footerAdapter = MutableLiveData<FooterAdapter>()
    val footerAdapter: LiveData<FooterAdapter> get() = _footerAdapter

    private val _isFooterVisible = MutableLiveData<Boolean>(false)
    val isFooterVisible: LiveData<Boolean> get() = _isFooterVisible

    fun showFooter(visible: Boolean) {
        _isFooterVisible.value = visible
    }

    fun setFooterAdapter(adapter: FooterAdapter) {
        _footerAdapter.value = adapter
    }
}


class FooterAdapterImpl @Inject constructor(
    private val sharedData: SharedData,
    private val context: Application,
    private val lifecycleOwner: LifecycleOwner,
    private val footerViewModel: FooterViewModel // Add FooterViewModel here
) : FooterAdapter, RecyclerView.Adapter<FooterAdapterImpl.FooterViewHolder>() {

    private var isFooterVisible: Boolean = false
    private var currentTickedCount: Int = 0 // Cache ticked count for the click listener

    init {
        // Observe ViewModel for isFooterVisible changes
        footerViewModel.isFooterVisible.observe(lifecycleOwner) { visible ->
            isFooterVisible = visible
            notifyDataSetChanged()
        }

        // Observe tickedCount changes
        sharedData.tickedCount.observe(lifecycleOwner) { tickedCount ->
            currentTickedCount = tickedCount ?: 0
        }
    }

    inner class FooterViewHolder(val purchasedButton: Button) :
        RecyclerView.ViewHolder(purchasedButton)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FooterViewHolder {
        val button = MaterialButton(parent.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1f
                bottomMargin = 200
                leftMargin = 30
                rightMargin = 30
            }
            text = context.getString(R.string.purchased)
            setBackgroundColor(resources.getColor(R.color.button_color))
        }
        footerViewModel.setFooterAdapter(this)

        return FooterViewHolder(button)
    }

    override fun onBindViewHolder(holder: FooterViewHolder, position: Int) {
        holder.purchasedButton.setOnClickListener {
            if (currentTickedCount == 0 && !sharedData.isAllChecked()) {
                Toast.makeText(
                    context,
                    context.getString(R.string.nothing_is_ticked), Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(context.applicationContext, context.getString(R.string.purchased_button_pressed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (isFooterVisible) 1 else 0
    }
}

