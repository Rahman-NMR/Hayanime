package com.animegatari.hayanime.core

object Config {
    const val DEFAULT_PAGE_LIMIT = 30

    // User Anime List Fields
    private const val MY_LIST_STATUS_BASIC_FIELDS = "status,score,num_episodes_watched"
    const val OWN_ANIME_LIST_MAIN_FIELDS = "${MY_LIST_STATUS_BASIC_FIELDS},is_rewatching,start_date,finish_date"
    const val OWN_ANIME_LIST_EXTENDED_FIELDS = "priority,num_times_rewatched,rewatch_value,tags,comments"

    // Anime List Fields
    const val SHORT_ANIME_FIELDS = "status,num_episodes,my_list_status"
    private const val BROADCAST_FIELDS = "media_type,rating,status,source,studios,genres,nsfw,start_season"
    private const val AVG_FIELDS = "mean,num_scoring_users,average_episode_duration"
    const val ANIME_LIST_FIELDS = "$BROADCAST_FIELDS,$AVG_FIELDS,$SHORT_ANIME_FIELDS{status,finish_date}"
    const val MYLIST_ANIME_FIELDS = "$BROADCAST_FIELDS,$SHORT_ANIME_FIELDS{$MY_LIST_STATUS_BASIC_FIELDS}"

    // User Info Fields
    private const val NI = "num_items"
    private const val ND = "num_days"
    const val USER_ANIME_STATS_ITEM_COUNT = "${NI}_watching,${NI}_completed,${NI}_on_hold,${NI}_dropped,${NI}_plan_to_watch,$NI"
    const val USER_ANIME_STATS_DAYS = "${ND}_watched,${ND}_watching,${ND}_completed,${ND}_on_hold,${ND}_dropped,$ND"
    const val USER_ANIME_STATS_OTHERS = "num_episodes,num_times_rewatched,mean_score"
    const val USER_INFO_ANIME_STATS_FIELDS = "$USER_ANIME_STATS_ITEM_COUNT,$USER_ANIME_STATS_DAYS,$USER_ANIME_STATS_OTHERS"
    const val USER_INFO_MORE_FIELDS = "picture,time_zone,is_supporter,anime_statistics"
    const val USER_INFO_FIELDS = "id,name,gender,birthday,location,joined_at"
}