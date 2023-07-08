package com.example.todoapp.data.network

import com.google.gson.annotations.SerializedName

/**
 * Класс, представляющий собой модель данных
 * для работы с сетевыми ответами.
 */
data class TodoItemResponse(
    @SerializedName("revision")
    val revision: Int,
    @SerializedName("element")
    val element: TodoItemPOJO,
    @SerializedName("status")
    val status: String
)
