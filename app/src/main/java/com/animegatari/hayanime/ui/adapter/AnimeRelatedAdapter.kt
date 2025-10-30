package com.animegatari.hayanime.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.model.RelatedAnime
import com.animegatari.hayanime.databinding.LayoutAnimeRelatedBinding
import com.bumptech.glide.Glide

class AnimeRelatedAdapter(
    private val onRecommendationClick: (RelatedAnime) -> Unit,
) : ListAdapter<RelatedAnime, AnimeRelatedAdapter.RecommendationViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val binding = LayoutAnimeRelatedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecommendationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
        holder.bind(getItem(position), onRecommendationClick)
    }

    class RecommendationViewHolder(private val binding: LayoutAnimeRelatedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(anime: RelatedAnime, onRecommendationClick: (RelatedAnime) -> Unit) {
            val context = binding.root.context

            binding.relationName.text = anime.relationTypeFormatted
            Glide.with(context)
                .load(anime.node?.mainPicture?.large ?: anime.node?.mainPicture?.medium)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_error)
                .into(binding.mainPicture)
            binding.title.text = anime.node?.title?.takeIf { it.isNotBlank() } ?: context.getString(R.string.label_unknown)

            binding.root.setOnClickListener { onRecommendationClick(anime) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<RelatedAnime>() {
        override fun areItemsTheSame(oldItem: RelatedAnime, newItem: RelatedAnime): Boolean = oldItem.node?.id == newItem.node?.id
        override fun areContentsTheSame(oldItem: RelatedAnime, newItem: RelatedAnime): Boolean = oldItem == newItem
    }
}