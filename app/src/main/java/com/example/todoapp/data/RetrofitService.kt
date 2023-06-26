package com.example.todoapp.data

import com.example.todoapp.domain.TodoItem
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path

interface RetrofitService {

    companion object {

        var retrofitService: RetrofitService? = null

        fun getInstance(): RetrofitService {
            val mHttpLoggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)

            val mOkHttpClient = OkHttpClient
                .Builder()
                .addInterceptor(mHttpLoggingInterceptor)
                .build()

            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://beta.mrdekk.ru/todobackend/list/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(mOkHttpClient)
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!

        }
    }


    @GET()
    suspend fun getTodoList(): Call<List<TodoItem>>

    @PATCH()
    suspend fun updateTodosOnServer()

    @GET()
    suspend fun getTodo(@Path("id") id: String): Call<TodoItem>

    @PUT()
    suspend fun addTodo(@Path("id") id: String)

    @DELETE()
    suspend fun deleteTodo(@Path("id") id: String)
}