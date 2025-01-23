package com.example.ichef.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ichef.AddParentChildActivity
import com.example.ichef.R
import com.example.ichef.activities.more.OptionsActivity
import com.example.ichef.activities.more.ProfileActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MoreFragment : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.window?.setDimAmount(0f)

        return dialog
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("MoreFragment", "In onCreateView")

        val rootView = inflater.inflate(R.layout.bottom_sheet_dialog, container, false)


        if (context is Context) {
            // Set listeners for buttons
            val button1 = rootView.findViewById<Button>(R.id.button1)
            val button2 = rootView.findViewById<Button>(R.id.button2)
            val button3 = rootView.findViewById<Button>(R.id.button3)

            button1.setOnClickListener {
                // Handle Button 1 click
                Toast.makeText(context, "${resources.getString(R.string.profile)} pressed", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, ProfileActivity::class.java)
//                intent.putStringArrayListExtra("ingredients_list", ingredients)
//                intent.putStringArrayListExtra("stores", GetStoresNames())
                profileIntent.launch(intent)
            }

            button2.setOnClickListener {
                // Handle Button 2 click
                Toast.makeText(context, "Fridge pressed", Toast.LENGTH_SHORT).show()
            }

            button3.setOnClickListener {
                // Handle Button 3 click
                Toast.makeText(context, "${resources.getString(R.string.options)} pressed", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, OptionsActivity::class.java)
//                intent.putStringArrayListExtra("ingredients_list", ingredients)
//                intent.putStringArrayListExtra("stores", GetStoresNames())
                optionsIntent.launch(intent)
            }
        }

        return rootView
    }

    private val profileIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

    }

    private val optionsIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

    }
}