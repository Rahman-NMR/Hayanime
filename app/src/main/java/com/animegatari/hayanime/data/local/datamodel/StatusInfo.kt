package com.animegatari.hayanime.data.local.datamodel

import com.animegatari.hayanime.data.types.WatchingStatus
import com.animegatari.hayanime.databinding.IncludeLegendStatusWatchingBinding

data class StatusInfo(
    val status: WatchingStatus,
    val value: Int?,
    val legendBinding: IncludeLegendStatusWatchingBinding,
)