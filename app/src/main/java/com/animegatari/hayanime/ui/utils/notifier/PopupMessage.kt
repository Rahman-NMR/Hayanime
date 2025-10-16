package com.animegatari.hayanime.ui.utils.notifier

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

object PopupMessage {
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    fun showSnackbar(
        view: View,
        message: String,
        anchorView: View? = null,
        duration: Int = Snackbar.LENGTH_SHORT,
        actionName: String? = null,
        action: View.OnClickListener? = null,
    ) {
        val snackbar = Snackbar.make(view, message, duration)
            .setAnchorView(anchorView)

        if (actionName.isNullOrBlank().not()) {
            val clickListener = action ?: View.OnClickListener { }
            snackbar.setAction(actionName, clickListener)
        }
        snackbar.show()
    }
}