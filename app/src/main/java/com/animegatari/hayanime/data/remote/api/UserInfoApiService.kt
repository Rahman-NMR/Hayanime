package com.animegatari.hayanime.data.remote.api

import com.animegatari.hayanime.data.model.UserInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserInfoApiService {
    @GET("users/@me")
    suspend fun getUserInfo(
        @Query("fields") fields: String,
    ): Response<UserInfo>
}