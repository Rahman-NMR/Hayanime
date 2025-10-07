package com.animegatari.hayanime.data.remote.api

import com.animegatari.hayanime.data.remote.response.AnimeListResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface UserAnimeListApiService {
    @GET("users/@me/animelist")
    suspend fun getUserAnimeList(
        @Query("status") status: String? = null,
        @Query("sort") sort: String? = null,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("nsfw") nsfw: Boolean? = true,
        @Query("fields") fields: String? = null,
    ): AnimeListResponse

    @FormUrlEncoded
    @PATCH("anime/{anime_id}/my_list_status")
    suspend fun updateAnimeListStatus(
        @Path("anime_id") animeId: Int,
        @Field("status") status: String? = null,
        @Field("is_rewatching") isRewatching: Boolean? = null,
        @Field("score") score: Int? = null,
        @Field("num_watched_episodes") numWatchedEpisodes: Int? = null,
        @Field("priority") priority: Int? = null,
        @Field("num_times_rewatched") numTimesRewatched: Int? = null,
        @Field("rewatch_value") rewatchValue: Int? = null,
        @Field("tags") tags: String? = null,
        @Field("comments") comments: String? = null,
        @Field("start_date") startDate: String? = null,
        @Field("finish_date") finishDate: String? = null,
    ): Response<ResponseBody>

    @DELETE("anime/{anime_id}/my_list_status")
    suspend fun deleteAnimeFromList(
        @Path("anime_id") animeId: Int,
    ): Response<ResponseBody>

    @FormUrlEncoded
    @PATCH("anime/{anime_id}/my_list_status")
    suspend fun updateProgressWatching(
        @Path("anime_id") animeId: Int,
        @Field("num_watched_episodes") numWatchedEpisodes: Int? = null,
        @Field("status") status: String? = null,
        @Field("finish_date") finishDate: String? = null,
    ): Response<ResponseBody>
}