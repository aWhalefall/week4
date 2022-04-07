package com.example.week4.data

/**
 * author：yangweichao@reworldgame.com
 * data: 2022/4/1 10:36
 * 保存值或异常的泛型类
 */
sealed class Response<out R> {
    data class Success<out T>(val data: T) : Response<T>()
    data class Error(val exception: Exception) : Response<Nothing>()
}

fun <T> Response<T>.successOr(fallback: T): T {
    return ((this as? Response.Success<T>)?.data ?: fallback)
}