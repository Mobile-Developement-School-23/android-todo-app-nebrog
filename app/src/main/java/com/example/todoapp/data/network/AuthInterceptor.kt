package com.example.todoapp.data.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Добавляет хедер для авторизации
 */
class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val currentRequest = chain.request().newBuilder()
        currentRequest.addHeader(AUTHORIZATION, TOKEN)

        val newRequest = currentRequest.build()
        return chain.proceed(newRequest)
    }

    companion object {
        private const val TOKEN = "Bearer inductions"
        private const val AUTHORIZATION = "Authorization"
    }
}
