package com.example.todoapp.data.network

import com.google.gson.annotations.SerializedName

data class TodoItemRequest(
    @SerializedName("element")
    val element: TodoItemPOJO,
)

