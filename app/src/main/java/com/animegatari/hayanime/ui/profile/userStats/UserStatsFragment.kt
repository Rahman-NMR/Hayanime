package com.animegatari.hayanime.ui.profile.userStats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.animegatari.hayanime.R
import com.animegatari.hayanime.data.local.datamodel.StatusInfo
import com.animegatari.hayanime.data.model.UserAnimeStatistics
import com.animegatari.hayanime.data.model.UserInfo
import com.animegatari.hayanime.data.types.Gender
import com.animegatari.hayanime.data.types.WatchingStatus
import com.animegatari.hayanime.databinding.FragmentUserStatsBinding
import com.animegatari.hayanime.databinding.IncludeLegendStatusWatchingBinding
import com.animegatari.hayanime.ui.utils.interfaces.UiUtils.createStatSpannable
import com.animegatari.hayanime.utils.FormatterUtils.formatApiDate
import com.animegatari.hayanime.utils.FormatterUtils.formattedDateTimeZone
import com.bumptech.glide.Glide
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserStatsFragment : Fragment() {
    private var _binding: FragmentUserStatsBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserStatsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModelStates()
    }

    private fun setupUserProfileHeader(info: UserInfo?) = with(binding) {
        Glide.with(requireContext())
            .load(info?.picture)
            .placeholder(R.drawable.img_placeholder)
            .error(R.drawable.img_error)
            .into(userPicture)
        userName.text = info?.name?.takeIf { it.isNotBlank() } ?: getString(R.string.label_unknown)
        userLocation.isVisible = info?.location.isNullOrBlank().not()
        userLocation.text = info?.location
        userGender.setImageResource(Gender.fromApiValue(info?.gender).iconResId)
    }

    private fun setupProfileInfo(info: UserInfo?) = with(binding) {
        birthDate.text = formatApiDate(info?.birthday) ?: getString(R.string.nothing)
        joinDate.text = formattedDateTimeZone(
            dateStr = info?.joinedAt,
            timeZoneStr = info?.timeZone
        ) ?: getString(R.string.nothing)
        timezone.text = info?.timeZone?.takeIf { it.isNotBlank() } ?: getString(R.string.nothing)
        supporterStatus.text = when (info?.isSupporter) {
            true -> getString(R.string.label_supporter)
            false -> getString(R.string.label_not_supporter)
            null -> getString(R.string.nothing)
        }
    }

    private fun setupMyAnimeListStats(stats: UserAnimeStatistics?) = with(binding) {
        updateSummaryStats(stats)
        updatePieChart(stats)
    }

    private fun updateSummaryStats(stats: UserAnimeStatistics?) = with(binding) {
        sumEpisodes.summaryValue.text = createStatSpannable(requireContext(), stats?.numEpisodes ?: 0, getString(R.string.label_episodes))
        sumRewatched.summaryValue.text = createStatSpannable(requireContext(), stats?.numTimesRewatched ?: 0, getString(R.string.label_rewatched))
        sumMean.summaryValue.text = createStatSpannable(requireContext(), stats?.meanScore ?: 0.0, getString(R.string.label_mean_score))
    }

    private fun updatePieChart(stats: UserAnimeStatistics?) = with(binding) {
        val statusInfoList = listOf(
            StatusInfo(WatchingStatus.WATCHING, stats?.numItemsWatching, legendWatching),
            StatusInfo(WatchingStatus.COMPLETED, stats?.numItemsCompleted, legendComplete),
            StatusInfo(WatchingStatus.ON_HOLD, stats?.numItemsOnHold, legendOnHold),
            StatusInfo(WatchingStatus.DROPPED, stats?.numItemsDropped, legendDropped),
            StatusInfo(WatchingStatus.PLAN_TO_WATCH, stats?.numItemsPlanToWatch, legendPlanToWatch)
        )

        val entries = statusInfoList.map { PieEntry(it.value?.toFloat() ?: 0f, getString(it.status.stringResId)) }
        val colors = statusInfoList.map { requireContext().getColor(it.status.colorResId) }
        val dataSet = PieDataSet(entries, "").apply {
            this.colors = colors
            valueTextSize = 0f
        }

        val totalAnime = stats?.numItems ?: 0
        setupPieChart(PieData(dataSet), totalAnime)
        statusInfoList.forEach { info ->
            updateLegendItem(info.legendBinding, info.status, info.value, totalAnime)
        }
    }

    private fun setupPieChart(pieData: PieData, totalAnime: Int) = with(binding.pieChart) {
        data = pieData

        isDrawHoleEnabled = true
        holeRadius = 64f
        setHoleColor(ContextCompat.getColor(requireContext(), R.color.md_theme_surface))
        transparentCircleRadius = 16f

        centerText = createStatSpannable(requireContext(), totalAnime, getString(R.string.label_anime))
        setCenterTextColor(ContextCompat.getColor(requireContext(), R.color.text_color_secondary))
        setCenterTextTypeface(ResourcesCompat.getFont(requireContext(), R.font.roboto_medium))

        description.isEnabled = false
        legend.isEnabled = false
        setDrawEntryLabels(false)
        animateY(1000)
        invalidate()
    }

    private fun updateLegendItem(
        legendBinding: IncludeLegendStatusWatchingBinding,
        status: WatchingStatus,
        value: Int?,
        totalAnime: Int,
    ) {
        val context = requireContext()

        legendBinding.bullet.backgroundTintList = ContextCompat.getColorStateList(context, status.colorResId)
        legendBinding.title.text = getString(status.stringResId)
        legendBinding.value.text = (value ?: 0).toString()
        legendBinding.value.setTextColor(context.getColor(status.colorResId))
        val totalAnimeSuffix = "/$totalAnime"

        legendBinding.numValue.text = totalAnimeSuffix
    }

    private fun updateUi(userInfo: UserInfo?) = with(binding) {
        setupUserProfileHeader(userInfo)
        setupProfileInfo(userInfo)
        setupMyAnimeListStats(userInfo?.userAnimeStatistics)
    }

    private fun observeViewModelStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.isLoading
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest { isLoading ->
                    binding.loadingIndicator.isVisible = isLoading
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.userInfo
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest(::updateUi)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}