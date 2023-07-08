package com.example.todoapp.data.di

import android.content.Context
import androidx.room.Room
import com.example.todoapp.data.database.AppDatabase
import com.example.todoapp.data.database.TodoItemDao
import com.example.todoapp.di.AppScope
import dagger.Module
import dagger.Provides

/**
 * Модуль с зависимостями для работы с локальной базой данных
 */
@Module
interface DatabaseModule {

    companion object {
        @Provides
        @AppScope
        fun provideAppDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "todolist_database"
            ).build()
        }

        @Provides
        @AppScope
        fun provideTodoItemDao(appDatabase: AppDatabase): TodoItemDao {
            return appDatabase.todoDao()
        }
    }
}
