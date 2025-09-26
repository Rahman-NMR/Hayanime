package com.animegatari.hayanime.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.model.Anime
import com.animegatari.hayanime.data.remote.response.AnimeList
import com.animegatari.hayanime.data.types.AiringStatus
import com.animegatari.hayanime.data.types.MediaType
import com.animegatari.hayanime.data.types.NsfwMedia
import com.animegatari.hayanime.data.types.RatingCategory
import com.animegatari.hayanime.data.types.SeasonStart
import com.animegatari.hayanime.data.types.WatchingStatus
import com.animegatari.hayanime.databinding.LayoutAnimeGridBinding
import com.animegatari.hayanime.utils.FormatterUtils.digitNumberFormatter
import com.animegatari.hayanime.utils.FormatterUtils.formatApiDate
import com.animegatari.hayanime.utils.TimeUtils
import com.bumptech.glide.Glide

class AnimeGridAdapter(
    private val onEditMyListClicked: (Anime) -> Unit,
    private val onItemClicked: (Anime) -> Unit,
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
        fun bind(animeList: AnimeList, onEditMyListClicked: (Anime) -> Unit, onItemClicked: (Anime) -> Unit) {
            val viewContext = binding.root.context
            val anime = animeList.node ?: return

            with(binding) {
                val pictureUrl = anime.mainPicture
                Glide.with(viewContext)
                    .load(pictureUrl?.medium ?: pictureUrl?.large)
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_error)
                    .into(mainPicture)

                mediaType.text = viewContext.getString(MediaType.fromApiValue(anime.mediaType).stringResId)

                score.text = anime.mean?.toString()
                    ?: viewContext.getString(R.string.not_available)

                numScoringUser.text = anime.numScoringUsers
                    ?.takeIf { it != 0 }
                    ?.let { digitNumberFormatter(it) }
                    ?: viewContext.getString(R.string.not_available)

                val season = anime.startSeason?.season?.takeIf { it.isNotEmpty() }
                val year = anime.startSeason?.year?.takeIf { it != 0 }
                val strSeason = viewContext.getString(SeasonStart.fromApiValue(season).stringResId)
                startSeason.text = when {
                    season != null && year != null -> "$strSeason $year"
                    season != null -> strSeason
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
                val strWatchingStatus = viewContext.getString(statusWatching.stringResId)
                watchingStatus.apply {
                    isVisible = anime.myListStatus != null
                    text = when (statusWatching) {
                        WatchingStatus.COMPLETED -> {
                            val formattedFinishDate = formatApiDate(anime.myListStatus?.finishDate)
                            formattedFinishDate?.let { "$strWatchingStatus on $it" } ?: strWatchingStatus
                        }

                        else -> strWatchingStatus
                    }
                }

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