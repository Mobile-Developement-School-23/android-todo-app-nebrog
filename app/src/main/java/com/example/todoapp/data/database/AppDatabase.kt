package com.example.todoapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [EntityTodoItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoItemDao

    companion object {
        fun create(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "todolist_database"
        ).build()
    }
}