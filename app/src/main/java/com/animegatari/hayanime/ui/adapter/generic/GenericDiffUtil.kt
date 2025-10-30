package com.animegatari.hayanime.ui.adapter.generic

import androidx.recyclerview.widget.DiffUtil

class GenericDiffUtil<T : Any>(
    private val onAreItemsTheSame: (T, T) -> Boolean,
    private val onAreContentsTheSame: (T, T) -> Boolean,
) : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return onAreItemsTheSame(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return onAreContentsTheSame(oldItem, newItem)
    }
}