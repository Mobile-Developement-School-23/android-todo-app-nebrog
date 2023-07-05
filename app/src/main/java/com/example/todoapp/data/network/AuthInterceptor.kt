package com.example.todoapp.data.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    private val TOKEN = "Bearer inductions"
    private val AUTHORIZATION = "Authorization"

    override fun intercept(chain: Interceptor.Chain): Response {
        val currentRequest = chain.request().newBuilder()
        currentRequest.addHeader(AUTHORIZATION, TOKEN)

        val newRequest = currentRequest.build()
        return chain.proceed(newRequest)
    }
}