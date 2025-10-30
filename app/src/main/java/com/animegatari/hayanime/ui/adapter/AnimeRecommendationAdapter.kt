package com.animegatari.hayanime.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.model.Recommendation
import com.animegatari.hayanime.databinding.LayoutAnimeRecommendationBinding
import com.bumptech.glide.Glide

class AnimeRecommendationAdapter(
    private val onRecommendationClick: (Recommendation) -> Unit,
) : ListAdapter<Recommendation, AnimeRecommendationAdapter.RecommendationViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val binding = LayoutAnimeRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecommendationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
        holder.bind(getItem(position), onRecommendationClick)
    }

    class RecommendationViewHolder(private val binding: LayoutAnimeRecommendationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(anime: Recommendation, onRecommendationClick: (Recommendation) -> Unit) {
            val context = binding.root.context

            val numRecommend = anime.numRecommendations ?: 0
            val numRecommendStr = context.resources.getQuantityString(
                R.plurals.num_user,
                numRecommend,
                numRecommend.toString()
            )
            binding.numRecom.text = numRecommendStr
            Glide.with(context)
                .load(anime.node?.mainPicture?.large ?: anime.node?.mainPicture?.medium)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_error)
                .into(binding.mainPicture)
            binding.title.text = anime.node?.title?.takeIf { it.isNotBlank() } ?: context.getString(R.string.label_unknown)

            binding.root.setOnClickListener { onRecommendationClick(anime) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Recommendation>() {
        override fun areItemsTheSame(oldItem: Recommendation, newItem: Recommendation): Boolean = oldItem.node?.id == newItem.node?.id
        override fun areContentsTheSame(oldItem: Recommendation, newItem: Recommendation): Boolean = oldItem == newItem
    }
}