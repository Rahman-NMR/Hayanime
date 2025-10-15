package com.animegatari.hayanime.ui.utils.interfaces

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object AlertDialog {
    fun confirmationDialog(
        context: Context,
        title: String,
        message: String,
        positiveButton: String,
        negativeButton: String,
        positiveAction: () -> Unit,
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { _, _ ->
                positiveAction()
            }
            .setNegativeButton(negativeButton, null)
            .show()
    }
}