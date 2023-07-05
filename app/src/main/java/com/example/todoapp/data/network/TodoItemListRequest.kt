package com.example.todoapp.data.network

import com.google.gson.annotations.SerializedName

data class TodoItemListRequest(
    @SerializedName("list")
    val list: List<TodoItemPOJO>,
)