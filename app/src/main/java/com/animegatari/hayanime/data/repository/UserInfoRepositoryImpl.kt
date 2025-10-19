package com.animegatari.hayanime.data.repository

import com.animegatari.hayanime.core.Config
import com.animegatari.hayanime.data.model.UserInfo
import com.animegatari.hayanime.data.remote.api.UserInfoApiService
import com.animegatari.hayanime.domain.repository.UserInfoRepository
import com.animegatari.hayanime.domain.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserInfoRepositoryImpl @Inject constructor(
    private val userInfoApiService: UserInfoApiService,
) : UserInfoRepository {
    override fun getProfileImage(): Flow<Response<UserInfo>> = flow {
        emit(Response.Loading)
        try {
            val response = userInfoApiService.getUserInfo("id,picture")

            if (response.isSuccessful) {
                emit(Response.Success(response.body()))
            } else {
                emit(Response.Error())
            }
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage))
        }
    }

    override fun getProfileInfo(): Flow<Response<UserInfo>> = flow {
        emit(Response.Loading)
        try {
            val response = userInfoApiService.getUserInfo(
                "${Config.USER_INFO_FIELDS},${Config.USER_INFO_MORE_FIELDS},{${Config.USER_INFO_ANIME_STATS_FIELDS}}"
            )

            if (response.isSuccessful) {
                emit(Response.Success(response.body()))
            } else {
                emit(Response.Error())
            }
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage))
        }
    }
}