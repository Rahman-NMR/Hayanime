package com.animegatari.hayanime.data.remote.api

import com.animegatari.hayanime.data.model.AnimeDetail
import com.animegatari.hayanime.data.model.AnimeMinimum
import com.animegatari.hayanime.data.remote.response.AnimeListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimeApiService {
    @GET("anime")
    suspend fun getAnimeList(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("nsfw") nsfw: Boolean? = null,
        @Query("fields") fields: String? = null,
    ): AnimeListResponse

    @GET("anime/suggestions")
    suspend fun getSuggestedAnime(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("nsfw") nsfw: Boolean? = null,
        @Query("fields") fields: String? = null,
    ): AnimeListResponse

    @GET("anime/season/{year}/{season}")
    suspend fun getAnimeSeason(
        @Path("year") year: Int,
        @Path("season") season: String,
        @Query("sort") sort: String? = null,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("nsfw") nsfw: Boolean? = null,
        @Query("fields") fields: String? = null,
    ): AnimeListResponse

    @GET("anime/{id}")
    suspend fun getAnimeDetail(
        @Path("id") id: Int,
        @Query("fields") fields: String? = null,
    ): AnimeDetail

    @GET("anime/{id}")
    suspend fun getShortAnimeDetail(
        @Path("id") id: Int,
        @Query("fields") fields: String? = null,
    ): AnimeMinimum
}