package com.example.callblocker.view

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.example.callblocker.R
import com.example.callblocker.utils.isValidNumber
import com.google.android.material.snackbar.Snackbar

class NumberInputDialog {
    companion object {
        /**
         * Show input dialog to get phone number input Only valide phone numbers will be accepted
         */
        fun show(context: Context, root: View, onSuccess: (String) -> Unit) {
            val inputBox = AppCompatEditText(context)
            inputBox.inputType = InputType.TYPE_CLASS_PHONE
            inputBox.filters = arrayOf(InputFilter.LengthFilter(13))

            androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Enter number")
                .setMessage("Please enter the number to be blocked")
                .setView(inputBox)
                .setCancelable(true)
                .setPositiveButton("Block"
                ) { dialog, whichButton ->
                    val value = inputBox.text.toString()
                    if (value.isValidNumber()) {
                        onSuccess(value)
                    } else {
                        Snackbar.make(
                            root,
                            context.getString(R.string.msg_invalid_number),
                            Snackbar.LENGTH_LONG
                        )
                            .show()
                    }
                }.setNegativeButton("Cancel"
                ) { dialog, whichButton ->
                    // Do nothing. Auto dismiss
                }.show()
        }
    }
}