package com.animegatari.hayanime.domain.repository

import com.animegatari.hayanime.data.model.UserInfo
import com.animegatari.hayanime.domain.utils.Response

interface UserInfoRepository {
    suspend fun getProfileImage(): Response<UserInfo>
}