package com.example.todoapp.data.network

import com.google.gson.annotations.SerializedName

/**
 * Класс, представляющий собой модель данных
 * для работы с сетевыми ответами.
 */
data class TodoItemListResponse(
    @SerializedName("revision")
    val revision: Int,
    @SerializedName("list")
    val list: List<TodoItemPOJO>,
    @SerializedName("status")
    val status: String
)
