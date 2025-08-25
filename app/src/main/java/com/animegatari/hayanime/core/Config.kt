package com.animegatari.hayanime.core

object Config {
    const val DEFAULT_PAGE_LIMIT = 30
    const val ANIME_USER_LIST_FIELDS = "my_list_status{status}"
    const val ANIME_MEDIA_FIELDS = "media_type,rating,status,source,studios,genres,nsfw"
    const val ANIME_EPISODE_FIELDS = "start_season,num_episodes,average_episode_duration"
    const val ANIME_SCORING_FIELDS = "mean,num_scoring_users"
    const val COMMON_ANIME_FIELDS = "$ANIME_EPISODE_FIELDS,$ANIME_MEDIA_FIELDS,$ANIME_SCORING_FIELDS,$ANIME_USER_LIST_FIELDS"
}