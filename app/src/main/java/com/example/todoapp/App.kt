package com.example.todoapp

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todoapp.data.synchronize.MyWorker
import java.util.concurrent.TimeUnit


class App : Application() {

    companion object {
        lateinit var application: Application
        private const val WORKNAME = "MyWorker"

    }

    override fun onCreate() {
        super.onCreate()
        application = this
        runPeriodicWorkRequest()
    }

    private fun runPeriodicWorkRequest() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()
        val workRequest = PeriodicWorkRequestBuilder<MyWorker>(
            repeatInterval = 8, TimeUnit.HOURS
        ).setConstraints(constraints).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WORKNAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}

