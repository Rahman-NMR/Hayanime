package com.animegatari.hayanime.data.repository

import com.animegatari.hayanime.core.Config
import com.animegatari.hayanime.data.model.UserInfo
import com.animegatari.hayanime.data.remote.api.UserInfoApiService
import com.animegatari.hayanime.domain.repository.UserInfoRepository
import com.animegatari.hayanime.domain.utils.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserInfoRepositoryImpl @Inject constructor(
    private val userInfoApiService: UserInfoApiService,
) : UserInfoRepository {
    override suspend fun getProfileImage(): Response<UserInfo> {
        return try {
            val response = userInfoApiService.getUserInfo("id,picture")

            if (response.isSuccessful) {
                Response.Success(response.body())
            } else {
                Response.Error()
            }
        } catch (e: Exception) {
            Response.Error(e.localizedMessage)
        }
    }

    override suspend fun getProfileInfo(): Response<UserInfo> {
        return try {
            val response = userInfoApiService.getUserInfo("${Config.USER_INFO_FIELDS},${Config.USER_INFO_MORE_FIELDS}")

            if (response.isSuccessful) {
                Response.Success(response.body())
            } else {
                Response.Error()
            }
        } catch (e: Exception) {
            Response.Error(e.localizedMessage)
        }
    }
}