package com.example.todoapp.data.network

import com.google.gson.annotations.SerializedName

/**
 * Класс, представляющий собой модель данных
 * для работы с сетевыми запросами.
 */
data class TodoItemListRequest(
    @SerializedName("list")
    val list: List<TodoItemPOJO>,
)
