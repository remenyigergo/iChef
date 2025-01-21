package com.example.ichef.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.ichef.R
import com.example.ichef.components.ShoppingFragmentBottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var bottomSheetDialogController: ShoppingFragmentBottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("HomeFragment", "In onCreateView")

        val rootView = inflater.inflate(R.layout.home_fragment, container, false)

        return rootView
    }

    override fun onResume() {
        super.onResume()

        // Handle dialog show when the fragment is resumed after configuration change (like theme change)

    }

}