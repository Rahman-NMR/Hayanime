package com.animegatari.hayanime.presentation.feature.profile.userStats

import com.animegatari.hayanime.databinding.IncludeLegendStatusWatchingBinding
import com.animegatari.hayanime.presentation.common.types.WatchingStatus

data class StatusInfo(
    val status: WatchingStatus,
    val value: Int?,
    val legendBinding: IncludeLegendStatusWatchingBinding,
)