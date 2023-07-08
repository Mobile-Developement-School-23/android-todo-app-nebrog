package com.example.todoapp.data.synchronize

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todoapp.domain.TodoRepository

/**
 * Класс подтягивает свежие изменения раз в 8 часов.
 */
class MyWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val repository: TodoRepository,
) : CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result {
        val result = repository.getAllTodos()
        return when (result) {
            is TodoRepository.Result.Failure -> Result.retry()
            is TodoRepository.Result.Success -> Result.success()
        }
    }
}
