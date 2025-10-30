package com.animegatari.hayanime.ui.adapter.generic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

class CleanAdapter<T : Any, VB : ViewBinding>(
    private val inflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
    private val binder: (VB, T) -> Unit,
    diffCallback: DiffUtil.ItemCallback<T>,
) : ListAdapter<T, GenericViewHolder<VB>>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<VB> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = inflater(layoutInflater, parent, false)
        return GenericViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder<VB>, position: Int) {
        val item = getItem(position)
        binder(holder.binding, item)
    }
}