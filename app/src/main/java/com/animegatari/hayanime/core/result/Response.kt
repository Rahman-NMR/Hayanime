package com.animegatari.hayanime.core.result

sealed class Response<out T> {
    object Loading : Response<Nothing>()
    data class Success<T>(val data: T? = null) : Response<T>()
    data class Error(val message: String? = null) : Response<Nothing>()
}