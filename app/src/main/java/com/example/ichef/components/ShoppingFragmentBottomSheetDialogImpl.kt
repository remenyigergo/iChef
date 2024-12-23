package com.example.ichef.components


import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import com.example.ichef.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class ShoppingFragmentBottomSheetDialogImpl @Inject constructor(
    @ActivityContext private val context: Context?,
    private var layoutInflater: LayoutInflater
): ShoppingFragmentBottomSheetDialog {

    override fun show() {

        if (context is Context) {

            val bottomSheetDialog = BottomSheetDialog(context)

            // Inflate the layout
            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            bottomSheetDialog.setContentView(view)

            // Set listeners for buttons
            val button1 = view.findViewById<Button>(R.id.button1)
            val button2 = view.findViewById<Button>(R.id.button2)
            val button3 = view.findViewById<Button>(R.id.button3)

            button1.setOnClickListener {
                // Handle Button 1 click
                bottomSheetDialog.dismiss()
            }

            button2.setOnClickListener {
                // Handle Button 2 click
                bottomSheetDialog.dismiss()
            }

            button3.setOnClickListener {
                // Handle Button 3 click
                bottomSheetDialog.dismiss()
            }

            // Show the dialog
            bottomSheetDialog.show()
        }

    }

}