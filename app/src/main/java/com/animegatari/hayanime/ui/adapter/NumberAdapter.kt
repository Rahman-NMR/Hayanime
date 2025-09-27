package com.animegatari.hayanime.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.animegatari.hayanime.R
import com.animegatari.hayanime.databinding.ItemNumberPickerBinding

class NumberAdapter : ListAdapter<Int, NumberAdapter.NumberViewHolder>(DiffCallback) {
    class NumberViewHolder(private val binding: ItemNumberPickerBinding) : ViewHolder(binding.root) {
        fun bind(itemValue: Int) {
            binding.tvNumber.text =
                if (itemValue == 0) binding.root.context.getString(R.string.nothing)
                else itemValue.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
        val binding = ItemNumberPickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NumberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        val itemValue = getItem(position)
        holder.bind(itemValue)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean = oldItem == newItem
    }
}