package com.animegatari.hayanime.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.model.AnimeDetail
import com.animegatari.hayanime.data.model.Genre
import com.animegatari.hayanime.data.model.Studio
import com.animegatari.hayanime.data.types.AiringStatus
import com.animegatari.hayanime.data.types.MediaType
import com.animegatari.hayanime.data.types.NsfwMedia
import com.animegatari.hayanime.data.types.RatingCategory
import com.animegatari.hayanime.data.types.SeasonStart
import com.animegatari.hayanime.data.types.SourceOfRefference
import com.animegatari.hayanime.databinding.FragmentAnimeDetailBinding
import com.animegatari.hayanime.databinding.LayoutChipBinding
import com.animegatari.hayanime.databinding.LayoutRateDetailsBinding
import com.animegatari.hayanime.domain.model.RateDetail
import com.animegatari.hayanime.domain.utils.onError
import com.animegatari.hayanime.domain.utils.onLoading
import com.animegatari.hayanime.domain.utils.onSuccess
import com.animegatari.hayanime.ui.adapter.AnimeRecommendationAdapter
import com.animegatari.hayanime.ui.adapter.AnimeRelatedAdapter
import com.animegatari.hayanime.ui.adapter.PictureCarouselAdapter
import com.animegatari.hayanime.ui.adapter.generic.CleanAdapter
import com.animegatari.hayanime.ui.adapter.generic.GenericDiffUtil
import com.animegatari.hayanime.ui.utils.interfaces.UiUtils.listRateDetail
import com.animegatari.hayanime.ui.utils.notifier.PopupMessage.showToast
import com.animegatari.hayanime.ui.utils.recyclerview.RecyclerViewUtils.carouselRecyclerView
import com.animegatari.hayanime.ui.utils.recyclerview.RecyclerViewUtils.flexChipRecyclerView
import com.animegatari.hayanime.ui.utils.recyclerview.RecyclerViewUtils.horizontalSpacingRecyclerView
import com.animegatari.hayanime.utils.FormatterUtils.dayLocaleFormatter
import com.animegatari.hayanime.utils.FormatterUtils.formatApiDate
import com.animegatari.hayanime.utils.FormatterUtils.formatTimeIntoLocale
import com.animegatari.hayanime.utils.TimeUtils
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AnimeDetailFragment : Fragment() {
    private var _binding: FragmentAnimeDetailBinding? = null
    private val binding get() = _binding!!

    private val animeDetailViewModel: AnimeDetailViewModel by viewModels()

    private val pictureCarouselAdapter by lazy { PictureCarouselAdapter() }
    private val genreAdapter by lazy { genreAdapter() }
    private val rateDetailAdapter by lazy { rateDetailAdapter() }
    private val studioAdapter by lazy { studioAdapter() }
    private val relatedAnimeAdapter by lazy { relatedAnimeAdapter() }
    private val animeRecAdapter by lazy { animeRecAdapter() }

    private var isSynopsisExpanded = false
    private var isBackgroundCollapsed = false
    private val collapsedMaxLines = 4

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAnimeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        initializeStaticView()
        setupInteractionListeners()
        observeViewModelStates()
    }

    private fun initializeStaticView() = with(binding) {
        synopsis.maxLines = collapsedMaxLines
        background.maxLines = collapsedMaxLines
    }

    private fun setupInteractionListeners() = with(binding) {
        toolBar.setNavigationOnClickListener { dismiss() }
        toolBar.setOnMenuItemClickListener { menuItem ->
            handleMenuItemClick(menuItem)
        }

        synopsis.setOnClickListener { toggleSynopsisExpansion() }
        btnMoreSyn.setOnClickListener { toggleSynopsisExpansion() }
        background.setOnClickListener { toggleBackgroundExpansion() }
        btnMoreBg.setOnClickListener { toggleBackgroundExpansion() }

        fabEditMylist.setOnClickListener {
            val action = AnimeDetailFragmentDirections.actionNavigationToNavigationEditAnime(
                animeId = animeDetailViewModel.animeId,
                requestKey = EditOwnListFragment.DETAIL_REQUEST_KEY
            )
            findNavController().navigate(action)
        }
    }

    private fun toggleSynopsisExpansion() {
        isSynopsisExpanded = !isSynopsisExpanded
        toggleExpansion(binding.synopsis, binding.btnMoreSyn, isSynopsisExpanded)
    }

    private fun toggleBackgroundExpansion() {
        isBackgroundCollapsed = !isBackgroundCollapsed
        toggleExpansion(binding.background, binding.btnMoreBg, isBackgroundCollapsed)
    }

    private fun toggleExpansion(textView: TextView, button: MaterialButton, isExpanded: Boolean) {
        TransitionManager.beginDelayedTransition(binding.content)
        textView.maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLines
        val textRes = if (isExpanded) R.string.label_show_less else R.string.label_read_more
        button.setText(textRes)
    }

    private fun openTranslator(text: String) {
        try {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
                setPackage("com.google.android.apps.translate")
            }
            startActivity(intent)
        } catch (_: Exception) {
            showToast(requireContext(), getString(R.string.message_error_app_not_found))
        }
    }

    private fun handleMenuItemClick(menuItem: MenuItem?): Boolean = when (menuItem?.itemId) {
        R.id.menu_item_share -> {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, animeDetailViewModel.getAnimeUrl())
            }
            startActivity(Intent.createChooser(shareIntent, null))
            true
        }

        R.id.menu_item_open_browser -> {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = animeDetailViewModel.getAnimeUrl().toUri()
            }
            startActivity(intent)
            true
        }

        else -> false
    }

    private fun setupRecyclerViews() {
        val spacing = resources.getDimensionPixelSize(R.dimen.small_dp)
        val mediumSpacing = resources.getDimensionPixelSize(R.dimen.item_rv_scrolling_width)
        val divider = ContextCompat.getDrawable(requireContext(), R.drawable.divider)

        carouselRecyclerView(binding.carouselRecyclerView, pictureCarouselAdapter)
        requireContext().flexChipRecyclerView(binding.genreRecyclerView, genreAdapter)
        requireContext().flexChipRecyclerView(binding.moreInfo.studioRecyclerView, studioAdapter)
        requireContext().horizontalSpacingRecyclerView(binding.recommendationRecyclerView, animeRecAdapter, spacing)
        requireContext().horizontalSpacingRecyclerView(binding.relationRecyclerView, relatedAnimeAdapter, spacing)
        requireContext().horizontalSpacingRecyclerView(
            binding.rateRecyclerView,
            rateDetailAdapter,
            mediumSpacing,
            hasDivider = true,
            divider = divider
        )
    }

    private fun genreAdapter(): CleanAdapter<Genre, LayoutChipBinding> {
        val genreDiffUtil = GenericDiffUtil<Genre>(
            onAreItemsTheSame = { old, new -> old.id == new.id },
            onAreContentsTheSame = { old, new -> old == new }
        )
        return CleanAdapter(
            inflater = { layoutInflater, parent, attach ->
                LayoutChipBinding.inflate(layoutInflater, parent, attach)
            }, binder = { vBinding, item ->
                vBinding.textView.text = item.name
            }, diffCallback = genreDiffUtil
        )
    }

    private fun rateDetailAdapter(): CleanAdapter<RateDetail, LayoutRateDetailsBinding> {
        val rateDetailDiffUtil = GenericDiffUtil<RateDetail>(
            onAreItemsTheSame = { old, new -> old == new },
            onAreContentsTheSame = { old, new -> old == new }
        )
        return CleanAdapter(
            inflater = { layoutInflater, parent, attach ->
                LayoutRateDetailsBinding.inflate(layoutInflater, parent, attach)
            }, binder = { vBinding, item ->
                vBinding.name.text = item.name
                vBinding.value.text = item.value
            }, diffCallback = rateDetailDiffUtil
        )
    }

    private fun studioAdapter(): CleanAdapter<Studio, LayoutChipBinding> {
        val studioDiffUtil = GenericDiffUtil<Studio>(
            onAreItemsTheSame = { old, new -> old.id == new.id },
            onAreContentsTheSame = { old, new -> old == new }
        )
        return CleanAdapter(
            inflater = { layoutInflater, parent, attach ->
                LayoutChipBinding.inflate(layoutInflater, parent, attach)
            }, binder = { vBinding, item ->
                vBinding.textView.text = item.name ?: getString(R.string.nothing)
            }, diffCallback = studioDiffUtil
        )
    }

    private fun relatedAnimeAdapter(): AnimeRelatedAdapter = AnimeRelatedAdapter(
        onRecommendationClick = { anime ->
            anime.node?.id?.let { animeId ->
                val action = AnimeDetailFragmentDirections.actionNavigationToNavigationAnimeDetail(animeId)
                findNavController().navigate(action)
            } ?: run {
                showToast(requireContext(), getString(R.string.message_error_missing_anime_id))
            }
        }
    )

    private fun animeRecAdapter(): AnimeRecommendationAdapter = AnimeRecommendationAdapter(
        onRecommendationClick = { anime ->
            anime.node?.id?.let { animeId ->
                val action = AnimeDetailFragmentDirections.actionNavigationToNavigationAnimeDetail(animeId)
                findNavController().navigate(action)
            } ?: run {
                showToast(requireContext(), getString(R.string.message_error_missing_anime_id))
            }
        }
    )

    private fun observeUIState(anime: AnimeDetail?) = with(binding) {
        val mergedPictures = buildList {
            anime?.mainPicture?.let(::add)
            anime?.pictures?.let(::addAll)
        }.ifEmpty { listOf(null) }
        pictureCarouselAdapter.submitList(mergedPictures)

        val nsfwCategory = NsfwMedia.fromApiValue(anime?.nsfw)
        if (nsfwCategory == NsfwMedia.BLACK) {
            val nsfwBlack = getString(R.string.label_nsfw) + "+"
            nsfw.layout.text = nsfwBlack
        }
        nsfw.layout.isVisible = nsfwCategory != NsfwMedia.WHITE && nsfwCategory != NsfwMedia.UNKNOWN
        rating.text = getString(RatingCategory.fromApiValue(anime?.rating).stringResId)

        title.text = anime?.title ?: getString(R.string.label_unknown)

        val mediaTypeStringResId = MediaType.fromApiValue(anime?.mediaType).stringResId
        mediaType.textView.text = getString(mediaTypeStringResId)
        mediaType.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_movie_24px_rounded, 0, 0, 0)

        val durationText = anime?.averageEpisodeDuration
            ?.takeIf { it != 0 }
            ?.let { TimeUtils.durationString(it) }
            ?: getString(R.string.unknown_symbol)
        val numEpisodes = anime?.numEpisodes
            ?.takeIf { it > 0 }
            ?.toString()
            ?: getString(R.string.unknown_symbol)
        val episodeStr = "${getString(R.string.num_episodes, numEpisodes)} (${getString(R.string.episode_duration, durationText)})"
        numEpisode.textView.text = episodeStr
        numEpisode.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_live_tv_24px_rounded, 0, 0, 0)

        val seasonStringResId = SeasonStart.fromApiValue(anime?.startSeason?.season).stringResId
        val seasonIconResId = SeasonStart.fromApiValue(anime?.startSeason?.season).iconResId
        val seasonStart = "${getString(seasonStringResId)} ${anime?.startSeason?.year}"
        season.textView.text = seasonStart
        season.textView.setCompoundDrawablesWithIntrinsicBounds(seasonIconResId, 0, 0, 0)

        val airingStatus = AiringStatus.fromApiValue(anime?.status).stringResId
        airing.textView.text = getString(airingStatus)
        airing.textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_broadcast_on_personal_24px_rounded, 0, 0, 0)

        genreAdapter.submitList(anime?.genres)

        val hasSynopsis = anime?.synopsis.isNullOrBlank().not()
        synopsis.isVisible = hasSynopsis
        if (hasSynopsis) {
            synopsis.text = anime.synopsis
            synopsis.post {
                btnMoreSyn.isVisible = synopsis.lineCount > collapsedMaxLines
                btnTlSyn.isVisible = synopsis.lineCount > collapsedMaxLines
                btnTlSyn.setOnClickListener { openTranslator(anime.synopsis) }
            }
        }

        rateDetailAdapter.submitList(requireContext().listRateDetail(anime))

        setupMoreInfoSection(anime)

        val hasBackground = anime?.background.isNullOrBlank().not()
        labelBackground.isVisible = hasBackground
        background.isVisible = hasBackground
        if (hasBackground) {
            background.text = anime.background
            background.post {
                btnMoreBg.isVisible = background.lineCount > collapsedMaxLines
                btnTlBg.isVisible = background.lineCount > collapsedMaxLines
                btnTlBg.setOnClickListener { openTranslator(anime.background) }
            }
        }

        val hasRelatedAnime = anime?.relatedAnime.isNullOrEmpty().not()
        relationRecyclerView.isVisible = hasRelatedAnime
        relatedAnimeAdapter.submitList(anime?.relatedAnime)

        val hasRecommendations = anime?.recommendations.isNullOrEmpty().not()
        labelRecommendation.isVisible = hasRecommendations
        recommendationRecyclerView.isVisible = hasRecommendations
        animeRecAdapter.submitList(anime?.recommendations)
    }

    private fun setupMoreInfoSection(anime: AnimeDetail?) = with(binding.moreInfo) {
        val defaultText = getString(R.string.nothing)
        val unknownText = getString(R.string.unknown_symbol)

        dataStartDate.text = formatApiDate(anime?.startDate).takeIf { !it.isNullOrBlank() } ?: defaultText
        dataFinishDate.text = formatApiDate(anime?.endDate).takeIf { !it.isNullOrBlank() } ?: defaultText

        val day = dayLocaleFormatter(anime?.broadcast?.dayOfTheWeek)
        val time = formatTimeIntoLocale(anime?.broadcast?.startTime)
        dataBroadcast.text = when {
            !day.isNullOrBlank() && !time.isNullOrBlank() -> getString(R.string.hyphen_string, day, time)
            !day.isNullOrBlank() -> getString(R.string.hyphen_string, day, unknownText)
            !time.isNullOrBlank() -> getString(R.string.hyphen_string, unknownText, time)
            else -> defaultText
        }

        dataSource.text = getString(SourceOfRefference.fromApiValue(anime?.source).stringResId)

        val studioList = anime?.studios.takeUnless { it.isNullOrEmpty() } ?: listOf(
            Studio(name = unknownText)
        )
        studioAdapter.submitList(studioList)

        synonymsTitle.text = anime?.alternativeTitles?.synonyms
            ?.joinToString(",\n")
            ?.takeIf { it.isNotBlank() }
            ?: defaultText

        engTitle.text = anime?.alternativeTitles?.en?.takeIf { it.isNotBlank() }
            ?: defaultText

        jpnTitle.text = anime?.alternativeTitles?.ja?.takeIf { it.isNotBlank() }
            ?: defaultText
    }

    private fun observeViewModelStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                animeDetailViewModel.animeDetail.collect { response ->
                    response.onSuccess { anime ->
                        loadingVisibilityView(indicator = false, layout = true)
                        observeUIState(anime)
                    }.onError {
                        loadingVisibilityView(indicator = false, layout = false)
                        showToast(requireContext(), getString(R.string.message_error_missing_anime_id))
                        dismiss()
                    }.onLoading { loadingVisibilityView(indicator = true, layout = false) }
                }
            }
        }
    }

    private fun loadingVisibilityView(indicator: Boolean, layout: Boolean) {
        binding.progressBar.isVisible = indicator
        binding.appBar.isVisible = layout
        binding.content.isVisible = layout
    }

    private fun dismiss() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}