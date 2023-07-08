package com.example.todoapp.data.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RetrofitService {

    @GET("list")
    suspend fun getTodoList(): TodoItemListResponse

    @PATCH("list")
    suspend fun updateTodosOnServer(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body todoItemList: TodoItemListRequest
    ): TodoItemListResponse

    @GET("list/{id}")
    suspend fun getTodo(@Path("id") id: String): TodoItemResponse

    @POST("list")
    suspend fun addTodo(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body todoItem: TodoItemRequest
    ): TodoItemResponse

    @PUT("list/{id}")
    suspend fun updateTodo(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String,
        @Body todoItem: TodoItemRequest
    ): TodoItemResponse

    @DELETE("list/{id}")
    suspend fun deleteTodo(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") id: String
    ): TodoItemResponse
}
