package com.animegatari.hayanime.domain.repository

import com.animegatari.hayanime.data.model.UserInfo
import com.animegatari.hayanime.domain.utils.Response
import kotlinx.coroutines.flow.Flow

interface UserInfoRepository {
    fun getProfileImage(): Flow<Response<UserInfo>>
    fun getProfileInfo(): Flow<Response<UserInfo>>
}