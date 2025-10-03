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
import com.animegatari.hayanime.data.types.SourceOfRefference
import com.animegatari.hayanime.data.types.WatchingStatus
import com.animegatari.hayanime.databinding.LayoutAnimeListBinding
import com.bumptech.glide.Glide
import kotlin.math.roundToInt

class MyListAdapter(
    private val onItemClicked: (Anime) -> Unit,
    private val onEditMyListClicked: (Anime) -> Unit,
    private val onAddProgressEpisode: (Anime) -> Unit,
) : PagingDataAdapter<AnimeList, MyListAdapter.AnimeViewHolder>(AnimeDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = LayoutAnimeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = getItem(position)
        anime?.let { holder.bind(it, onItemClicked, onEditMyListClicked, onAddProgressEpisode) }
    }

    class AnimeViewHolder(private val binding: LayoutAnimeListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            animeList: AnimeList,
            onItemClicked: (Anime) -> Unit,
            onEditMyListClicked: (Anime) -> Unit,
            onAddProgressEpisode: (Anime) -> Unit,
        ) {
            val viewContext = binding.root.context
            val anime = animeList.node ?: return
            val unknownString = viewContext.getString(R.string.label_unknown)
            val notAvailableString = viewContext.getString(R.string.not_available)

            with(binding) {
                val pictureUrl = anime.mainPicture
                Glide.with(viewContext)
                    .load(pictureUrl?.medium ?: pictureUrl?.large)
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_error)
                    .into(mainPicture)

                val myScore = anime.myListStatus?.score
                score.text = if (myScore != null) {
                    score.isVisible = myScore > 0
                    myScore.toString()
                } else {
                    notAvailableString
                }

                val animeStatus = AiringStatus.Companion.fromApiValue(anime.status)
                status.apply {
                    text = viewContext.getString(AiringStatus.Companion.fromApiValue(anime.status).stringResId)
                    isVisible = animeStatus != AiringStatus.FINISHED_AIRING
                }

                val nsfwMedia = NsfwMedia.Companion.fromApiValue(anime.nsfw)
                nsfw.isVisible = nsfwMedia != NsfwMedia.WHITE
                if (nsfwMedia == NsfwMedia.BLACK) {
                    val nsfwBlack = viewContext.getString(R.string.label_nsfw) + "+"
                    nsfw.text = nsfwBlack
                }

                val mediaType = viewContext.getString(MediaType.Companion.fromApiValue(anime.mediaType).stringResId)
                val season = anime.startSeason?.season?.takeIf { it.isNotEmpty() }
                val year = anime.startSeason?.year?.takeIf { it != 0 }
                val strSeason = viewContext.getString(SeasonStart.Companion.fromApiValue(season).stringResId)
                mediaTypeNStartSeason.text = when {
                    season != null && year != null -> "$mediaType • $strSeason $year"
                    season != null -> "$mediaType • $strSeason"
                    year != null -> "$mediaType • $year"
                    else -> mediaType
                }

                val watchingStatusColorResId = WatchingStatus.fromApiValue(anime.myListStatus?.status).colorResId
                title.apply {
                    text = anime.title
                        ?.takeIf { it.isNotEmpty() }
                        ?: unknownString
                    setTextColor(viewContext.getColor(watchingStatusColorResId))
                }

                rating.text = viewContext.getString(RatingCategory.Companion.fromApiValue(anime.rating).stringResId)

                genres.text = anime.genres
                    ?.mapNotNull { it?.name }
                    ?.filter { it.isNotBlank() }
                    ?.joinToString(", ")
                    ?.takeIf { it.isNotEmpty() }
                    ?: unknownString

                val source = viewContext.getString(SourceOfRefference.Companion.fromApiValue(anime.source).stringResId)
                val studios = anime.studios
                    ?.mapNotNull { it?.name }
                    ?.filter { it.isNotBlank() }
                    ?.joinToString(", ")
                    ?.takeIf { it.isNotEmpty() }
                    ?: unknownString
                sourceNStudio.text = when {
                    source.isNotBlank() && studios.isNotBlank() -> viewContext.getString(R.string.label_source_studio, source, studios)
                    source.isNotBlank() -> viewContext.getString(R.string.label_source, source)
                    studios.isNotBlank() -> viewContext.getString(R.string.label_source, studios)
                    else -> unknownString
                }

                val episodeWatched = anime.myListStatus?.numEpisodesWatched ?: 0
                val numOfMaxEpisode = anime.numEpisodes
                progressEpisode.apply {
                    min = 0
                    max = numOfMaxEpisode.takeIf { it != 0 } ?: (episodeWatched * 2.0).roundToInt()
                    progress = episodeWatched
                    setIndicatorColor(viewContext.getColor(watchingStatusColorResId))
                }

                val numTotalEpisodes = numOfMaxEpisode
                    ?.takeIf { it != 0 }
                    ?.toString()
                    ?: viewContext.getString(R.string.unknown_symbol)
                val strProgressEpisode = "$episodeWatched/$numTotalEpisodes"
                numEpisodes.text = strProgressEpisode

                val percentageWatched = when {
                    progressEpisode.max > 0 -> (progressEpisode.progress.toDouble() / progressEpisode.max) * 100
                    else -> 0.0
                }
                val strProgressEpisodeDone = "${percentageWatched.roundToInt()}%"
                percentageEpisodeDone.text = strProgressEpisodeDone

                btnEditMylist.setOnClickListener { onEditMyListClicked(anime) }

                btnPlusOneEpisode.apply {
                    isEnabled = episodeWatched < (numOfMaxEpisode ?: 0)
                    setOnClickListener { onAddProgressEpisode(anime) }
                }

                root.setOnClickListener { onItemClicked(anime) }
            }
        }
    }

    class AnimeDiffCallback : DiffUtil.ItemCallback<AnimeList>() {
        override fun areItemsTheSame(oldItem: AnimeList, newItem: AnimeList): Boolean = oldItem.node?.id == newItem.node?.id
        override fun areContentsTheSame(oldItem: AnimeList, newItem: AnimeList): Boolean = oldItem == newItem
    }
}