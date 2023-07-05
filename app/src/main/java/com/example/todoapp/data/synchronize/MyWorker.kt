package com.example.todoapp.data.synchronize

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todoapp.data.database.OfflineRepository
import com.example.todoapp.data.network.NetworkRepository
import com.example.todoapp.domain.TodoRepository

class MyWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    private val networkRepository = NetworkRepository
    private val offlineRepository = OfflineRepository

    override suspend fun doWork(): Result {
        val result = networkRepository.getAllTodos()
        return when (result) {
            is TodoRepository.Result.Failure -> Result.retry()
            is TodoRepository.Result.Success -> {
                val dbResult = offlineRepository.updateAllTodos(result.value)
                when (dbResult) {
                    is TodoRepository.Result.Failure -> Result.retry()
                    is TodoRepository.Result.Success -> Result.success()
                }
            }
        }
    }
}
