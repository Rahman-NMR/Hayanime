package com.animegatari.hayanime.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.model.Picture
import com.animegatari.hayanime.databinding.LayoutCarouselBinding
import com.bumptech.glide.Glide

class PictureCarouselAdapter : ListAdapter<Picture, PictureCarouselAdapter.PictureCarouselViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureCarouselViewHolder {
        val binding = LayoutCarouselBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PictureCarouselViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PictureCarouselViewHolder, position: Int) {
        val picture = getItem(position)
        picture?.let { holder.bind(it) }
    }

    class PictureCarouselViewHolder(private val binding: LayoutCarouselBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(picture: Picture) {
            val context = binding.root.context

            Glide.with(context)
                .load(picture.large ?: picture.medium)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_error)
                .into(binding.carouselImage)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Picture>() {
        override fun areItemsTheSame(oldItem: Picture, newItem: Picture): Boolean = oldItem.medium == newItem.medium
        override fun areContentsTheSame(oldItem: Picture, newItem: Picture): Boolean = oldItem == newItem
    }
}