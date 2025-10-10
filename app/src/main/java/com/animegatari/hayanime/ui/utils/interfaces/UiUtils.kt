package com.animegatari.hayanime.ui.utils.interfaces

import android.content.Context
import android.text.Editable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.animegatari.hayanime.R
import com.animegatari.hayanime.utils.FormatterUtils.formatDecimal
import com.google.android.material.textfield.TextInputEditText

object UiUtils {
    fun scoreStringMap(context: Context): Map<Int, String> = mapOf(
        0 to context.getString(R.string.my_score_not_yet_scored),
        10 to context.getString(R.string.my_score_masterpiece),
        9 to context.getString(R.string.my_score_great),
        8 to context.getString(R.string.my_score_very_good),
        7 to context.getString(R.string.my_score_good),
        6 to context.getString(R.string.my_score_fine),
        5 to context.getString(R.string.my_score_average),
        4 to context.getString(R.string.my_score_bad),
        3 to context.getString(R.string.my_score_very_bad),
        2 to context.getString(R.string.my_score_horrible),
        1 to context.getString(R.string.my_score_appalling)
    ).toSortedMap(compareByDescending { it })

    fun handleTextChange(editable: Editable?, action: (String) -> Unit) {
        action(editable?.toString()?.trim() ?: "")
    }

    fun shouldUpdateInputText(textInputEditText: TextInputEditText, newValue: String?): Boolean {
        return !textInputEditText.isFocused && textInputEditText.text.toString() != newValue
    }

    fun hideKeyboardAndClearFocus(view: View?) {
        view?.let {
            it.clearFocus()

            val imm = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun createStatSpannable(context: Context, value: Number, label: String): SpannableString {
        val valueStr = if (value is Float) formatDecimal(value) else value.toString()
        val text = "$valueStr\n$label"
        val spannable = SpannableString(text)
        val textSizeLarge = context.resources.getDimensionPixelSize(R.dimen.pie_chart_center_text_large)
        val textSizeSmall = context.resources.getDimensionPixelSize(R.dimen.pie_chart_center_text_small)

        spannable.setSpan(AbsoluteSizeSpan(textSizeLarge), 0, valueStr.length, 0)
        spannable.setSpan(AbsoluteSizeSpan(textSizeSmall), valueStr.length, text.length, 0)
        return spannable
    }
}