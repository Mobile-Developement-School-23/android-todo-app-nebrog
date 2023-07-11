package com.example.todoapp

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todoapp.data.synchronize.MyWorker
import com.example.todoapp.data.synchronize.MyWorkerFactory
import com.example.todoapp.di.AppComponent
import com.example.todoapp.di.DaggerAppComponent
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Приложение TodoApp by Vladislav Gorbenko =)
 */
class App : Application(), Configuration.Provider {

    lateinit var appComponent: AppComponent
        private set

    @Inject
    lateinit var workerFactory: MyWorkerFactory

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)
        appComponent.inject(this)
        runPeriodicWorkRequest()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setWorkerFactory(workerFactory).build()
    }

    private fun runPeriodicWorkRequest() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()
        val workRequest = PeriodicWorkRequestBuilder<MyWorker>(DURATION, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setInitialDelay(DURATION, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WORKNAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    companion object {
        private const val WORKNAME = "MyWorker"
        private const val DURATION = 8L
    }
}
