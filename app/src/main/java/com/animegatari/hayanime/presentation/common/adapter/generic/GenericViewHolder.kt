package com.animegatari.hayanime.presentation.common.adapter.generic

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class GenericViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)