package com.animegatari.hayanime.domain.repository

import com.animegatari.hayanime.data.remote.dto.UserInfo
import com.animegatari.hayanime.core.result.Response
import kotlinx.coroutines.flow.Flow

interface UserInfoRepository {
    fun getProfileImage(): Flow<Response<UserInfo>>
    fun getProfileInfo(): Flow<Response<UserInfo>>
}