package com.animegatari.hayanime.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.remote.response.AnimeList
import com.animegatari.hayanime.data.remote.response.AnimeNode
import com.animegatari.hayanime.data.types.AiringStatus
import com.animegatari.hayanime.data.types.MediaType
import com.animegatari.hayanime.data.types.NsfwMedia
import com.animegatari.hayanime.data.types.RatingCategory
import com.animegatari.hayanime.data.types.WatchingStatus
import com.animegatari.hayanime.databinding.LayoutAnimeGridBinding
import com.animegatari.hayanime.utils.TimeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class AnimeGridAdapter(
    private val onEditMyListClicked: (AnimeNode) -> Unit,
    private val onItemClicked: (AnimeNode) -> Unit,
) : PagingDataAdapter<AnimeList, AnimeGridAdapter.AnimeViewHolder>(AnimeDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = LayoutAnimeGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = getItem(position)
        anime?.let { holder.bind(it, onEditMyListClicked, onItemClicked) }
    }

    class AnimeViewHolder(private val binding: LayoutAnimeGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(animeList: AnimeList, onEditMyListClicked: (AnimeNode) -> Unit, onItemClicked: (AnimeNode) -> Unit) {
            val viewContext = binding.root.context
            val anime = animeList.node ?: return

            with(binding) {
                Glide.with(viewContext)
                    .load(anime.mainPicture?.medium)
                    .placeholder(R.drawable.placeholder) //TODO: change image placeholder & error
                    .error(R.drawable.placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade(333))
                    .into(mainPicture)

                mediaType.text = viewContext.getString(MediaType.fromApiValue(anime.mediaType).stringResId)

                score.text = anime.mean?.toString()
                    ?: viewContext.getString(R.string.not_available)

                numScoringUser.text = anime.numScoringUsers
                    ?.takeIf { it != 0 }
                    ?.toString()
                    ?: viewContext.getString(R.string.not_available)

                val season = anime.startSeason?.season?.takeIf { it.isNotEmpty() }
                val year = anime.startSeason?.year?.takeIf { it != 0 }
                startSeason.text = when {
                    season != null && year != null -> "${season.replaceFirstChar { it.titlecase() }} $year"
                    season != null -> season.replaceFirstChar { it.titlecase() }
                    year != null -> year.toString()
                    else -> viewContext.getString(R.string.label_unknown)
                }

                val animeStatus = AiringStatus.fromApiValue(anime.status)
                status.apply {
                    text = viewContext.getString(AiringStatus.fromApiValue(anime.status).stringResId)
                    isVisible = animeStatus != AiringStatus.FINISHED_AIRING
                }

                title.text = anime.title
                    ?.takeIf { it.isNotEmpty() }
                    ?: viewContext.getString(R.string.label_unknown)

                val nsfwMedia = NsfwMedia.fromApiValue(anime.nsfw)
                nsfw.isVisible = nsfwMedia != NsfwMedia.WHITE
                if (nsfwMedia == NsfwMedia.BLACK) {
                    val nsfwBlack = viewContext.getString(R.string.label_nsfw) + "+"
                    nsfw.text = nsfwBlack
                }

                rating.text = viewContext.getString(RatingCategory.fromApiValue(anime.rating).stringResId)

                genres.text = anime.genres
                    ?.mapNotNull { it?.name }
                    ?.filter { it.isNotBlank() }
                    ?.joinToString(", ")
                    ?.takeIf { it.isNotEmpty() }
                    ?: viewContext.getString(R.string.label_unknown)

                numEpisodes.text = anime.numEpisodes
                    ?.takeIf { it != 0 }
                    ?.toString()
                    ?: viewContext.getString(R.string.unknown_symbol)

                val durationText = anime.averageEpisodeDuration
                    ?.takeIf { it != 0 }
                    ?.let { TimeUtils.durationString(it) }
                    ?: viewContext.getString(R.string.unknown_symbol)
                avgEpsPerDuration.text = viewContext.getString(R.string.label_episode_duration, durationText)

                studio.text = anime.studios
                    ?.mapNotNull { it?.name }
                    ?.filter { it.isNotBlank() }
                    ?.joinToString(", ")
                    ?.takeIf { it.isNotEmpty() }
                    ?: viewContext.getString(R.string.label_unknown)

                val statusWatching = WatchingStatus.fromApiValue(anime.myListStatus?.status)
                watchingStatus.apply {
                    isVisible = anime.myListStatus != null
                    text = if (statusWatching != WatchingStatus.COMPLETED) {
                        viewContext.getString(statusWatching.stringResId)
                    } else "${viewContext.getString(statusWatching.stringResId)} on ${anime.myListStatus?.finishDate}"
                } //TODO: sdf for finishDate

                btnEditMylist.setOnClickListener { onEditMyListClicked(anime) }

                root.setOnClickListener { onItemClicked(anime) }
            }
        }
    }

    class AnimeDiffCallback : DiffUtil.ItemCallback<AnimeList>() {
        override fun areItemsTheSame(oldItem: AnimeList, newItem: AnimeList): Boolean = oldItem.node?.id == newItem.node?.id
        override fun areContentsTheSame(oldItem: AnimeList, newItem: AnimeList): Boolean = oldItem == newItem
    }
}