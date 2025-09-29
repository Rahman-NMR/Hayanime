package com.animegatari.hayanime.domain.utils

sealed class Response<out T> {
    data class Success<T>(val data: T? = null) : Response<T>()
    data class Error(val message: String? = null) : Response<Nothing>()
}