package com.example.todoapp.data.synchronize

import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.todoapp.di.MyWorkerScope
import com.example.todoapp.domain.TodoRepository
import javax.inject.Inject
import javax.inject.Provider

/**
 * Фабрика для предоставления кастомных зависимостей
 */
@MyWorkerScope
class MyWorkerFactory @Inject constructor(
    private val repository: Provider<TodoRepository>,
) : WorkerFactory() {
    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): MyWorker {
        return MyWorker(appContext, workerParameters, repository.get())
    }
}
