package com.example.todoapp.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoapp.domain.TodoItem.Priority

@Entity(tableName = "todos")
class EntityTodoItem(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "item_id") val itemID: String,
    @ColumnInfo(name = "item_text") val itemText: String,
    @ColumnInfo(name = "item_priority") val itemPriority: Priority,
    @ColumnInfo(name = "deadline") val deadline: Long?,
    @ColumnInfo(name = "done_flag") val doneFlag: Boolean,
    @ColumnInfo(name = "date_of_creation") val dateOfCreation: Long,
    @ColumnInfo(name = "date_of_changes") val dateOfChanges: Long?,
)
