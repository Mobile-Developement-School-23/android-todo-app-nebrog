package com.example.todoapp.domain

import com.google.gson.annotations.SerializedName
import java.util.Date

data class TodoItem(
    @SerializedName("id")
    val itemID: String,
    @SerializedName("text")
    val itemText: String,
    @SerializedName("importance")
    val itemPriority: Priority,
    @SerializedName("deadline")
    val deadline: Date?,
    @SerializedName("done")
    val doneFlag: Boolean,
    @SerializedName("created_at")
    val dateOfCreation: Date,
    @SerializedName("changed_at")
    val dateOfChanges: Date?
) {
    enum class Priority {
        LOW,
        NORMAL,
        HIGH
    }
}