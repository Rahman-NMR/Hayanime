package com.animegatari.hayanime.data.remote.api

import com.animegatari.hayanime.data.remote.response.AnimeListResponse
import retrofit2.http.GET
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
}