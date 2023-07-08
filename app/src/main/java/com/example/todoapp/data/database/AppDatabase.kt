package com.example.todoapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Класс локальной базы данных
 */
@Database(entities = [EntityTodoItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoItemDao
}
