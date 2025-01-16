package com.moez.QKSMS.common.widget

import android.app.Activity
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import dev.octoshrimpy.quik.R
import dev.octoshrimpy.quik.databinding.FieldDialogBinding

class FieldDialog(context: Activity, hint: String, listener: (String) -> Unit) :
    AlertDialog(context) {

    private val binding = FieldDialogBinding.inflate(context.layoutInflater)

    init {
        binding.field.hint = hint

        setView(binding.root)
        setButton(
            DialogInterface.BUTTON_NEUTRAL,
            context.getString(R.string.button_cancel)
        ) { _, _ -> }
        setButton(
            DialogInterface.BUTTON_NEGATIVE,
            context.getString(R.string.button_delete)
        ) { _, _ -> listener("") }
        setButton(
            DialogInterface.BUTTON_POSITIVE,
            context.getString(R.string.button_save)
        ) { _, _ ->
            listener(binding.field.text.toString())
        }
    }

    fun setText(text: String): FieldDialog {
        binding.field.setText(text)
        return this
    }

}
