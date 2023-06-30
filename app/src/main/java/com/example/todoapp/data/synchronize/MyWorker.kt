package com.example.todoapp.data.synchronize

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todoapp.data.network.NetworkRepository
import com.example.todoapp.domain.TodoRepository

class MyWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    private val repository = NetworkRepository

    override suspend fun doWork(): Result {
        val result = repository.getAllTodos()
        return when (result) {
            is TodoRepository.Result.Failure -> Result.failure()
            is TodoRepository.Result.Success -> Result.success()
        }
    }

}

