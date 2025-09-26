package com.animegatari.hayanime.ui.utils.notifier

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

object PopupMessage {
    fun toastShort(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun snackBarShort(view: View, message: String, anchorView: View? = null) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setAnchorView(anchorView).show()
    }
}