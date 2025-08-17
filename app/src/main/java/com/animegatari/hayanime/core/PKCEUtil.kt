package com.animegatari.hayanime.core

import java.security.SecureRandom

object PKCEUtil {
    fun generateCodeVerifier(length: Int = 64): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = SecureRandom()
        val sb = StringBuilder(length)

        repeat(length) {
            val index = random.nextInt(charset.length)
            sb.append(charset[index])
        }
        return sb.toString()
    }
}