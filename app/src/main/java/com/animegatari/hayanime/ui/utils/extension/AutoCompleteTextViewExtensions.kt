package com.animegatari.hayanime.ui.utils.extension

import android.R.layout.simple_dropdown_item_1line
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

object AutoCompleteTextViewExtensions {
    inline fun <reified E> AutoCompleteTextView.setupDropdownWithEnum(
        items: List<E>,
        defaultItem: E? = null,
        noinline onItemSelected: (selectedValue: E) -> Unit,
    ) where E : Enum<E>, E : DisplayableEnum {
        val displayStrings = items.map { it.getDisplayString(context) }

        val optionAdapter = ArrayAdapter(context, simple_dropdown_item_1line, displayStrings)
        setAdapter(optionAdapter)

        val initialItem = defaultItem ?: items.first()
        setText(initialItem.getDisplayString(context), false)

        setOnItemClickListener { parent, _, position, _ ->
            val selectedDisplayString = parent.getItemAtPosition(position) as String
            val selectedEnum = items.find { it.getDisplayString(context) == selectedDisplayString }

            selectedEnum?.let(onItemSelected)
        }
    }

    fun AutoCompleteTextView.setupSimpleDropdown(
        items: List<String>,
        defaultDisplayValue: String? = null,
        onItemSelected: (selectedDisplay: String) -> Unit,
    ) {
        val adapter = ArrayAdapter(context, simple_dropdown_item_1line, items)
        setAdapter(adapter)

        defaultDisplayValue?.let {
            setText(it, false)
        }

        setOnItemClickListener { parent, _, position, _ ->
            val selectedDisplay = parent.getItemAtPosition(position) as String
            onItemSelected(selectedDisplay)
        }
    }
}