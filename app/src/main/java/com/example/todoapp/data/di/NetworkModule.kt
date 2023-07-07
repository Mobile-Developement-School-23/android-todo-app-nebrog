package com.example.todoapp.data.di

import com.example.todoapp.data.network.AuthInterceptor
import com.example.todoapp.data.network.RetrofitService
import com.example.todoapp.di.AppScope
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
interface NetworkModule {

    companion object {
        @Provides
        fun provideAuthInterceptor(): AuthInterceptor {
            return AuthInterceptor()
        }

        @Provides
        @AppScope
        fun provideOkhttp(authInterceptor: AuthInterceptor): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)

            return OkHttpClient
                .Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()
        }

        @Provides
        @AppScope
        fun provideRetrofit(okHttpClient: OkHttpClient): RetrofitService {
            val gson = GsonBuilder()
                .setLenient()
                .create()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://beta.mrdekk.ru/todobackend/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()
            return retrofit.create(RetrofitService::class.java)
        }
    }
}
