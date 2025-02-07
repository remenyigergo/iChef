package com.example.ichef.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.ichef.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.search_fragment, container, false)

        val toggleButton: Button = view.findViewById(R.id.btn_toggle_filters)
        val filtersSection: LinearLayout = view.findViewById(R.id.filters_section)

        toggleButton.setOnClickListener {
            if (filtersSection.visibility == View.VISIBLE) {
                filtersSection.visibility = View.GONE
                toggleButton.text = "Show Filters ▼"
            } else {
                filtersSection.visibility = View.VISIBLE
                toggleButton.text = "Hide Filters ▲"
            }
        }

        return view
    }

}