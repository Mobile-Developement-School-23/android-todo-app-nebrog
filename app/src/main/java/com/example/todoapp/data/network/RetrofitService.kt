package com.example.todoapp.data.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RetrofitService {

    companion object {

        var retrofitService: RetrofitService? = null

        fun getInstance(): RetrofitService {
            val mHttpLoggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)

            val gson = GsonBuilder()
                .setLenient()
                .create()

            val mOkHttpClient = OkHttpClient
                .Builder()
                .addInterceptor(mHttpLoggingInterceptor)
                .build()

            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://beta.mrdekk.ru/todobackend/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(mOkHttpClient)
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!

        }
    }


    @GET("list")
    suspend fun getTodoList(@Header("Authorization") token: String): TodoItemListResponse

    @PATCH("list")
    suspend fun updateTodosOnServer(
        @Header("Authorization") token: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Body todoItemList: TodoItemListRequest
    ): TodoItemListResponse

    @GET("list/{id}")
    suspend fun getTodo(@Header("Authorization") token: String, @Path("id") id: String): TodoItemResponse

    @POST("list")
    suspend fun addTodo(
        @Header("Authorization") token: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Body todoItem: TodoItemRequest
    ): TodoItemResponse

    @PUT("list/{id}")
    suspend fun updateTodo(
        @Header("Authorization") token: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String,
        @Body todoItem: TodoItemRequest
    ): TodoItemResponse

    @HTTP(method = "DELETE", path = "list/{id}", hasBody = true)
    suspend fun deleteTodo(
        @Header("Authorization") token: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String,
        @Body todoItem: TodoItemRequest
    ): TodoItemResponse
}