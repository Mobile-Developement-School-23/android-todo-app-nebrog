package com.example.todoapp.di

import android.content.Context
import com.example.todoapp.App
import com.example.todoapp.data.alarm.di.AlarmServiceComponent
import com.example.todoapp.data.di.DataModule
import com.example.todoapp.data.di.DatabaseModule
import com.example.todoapp.data.di.NetworkModule
import com.example.todoapp.presentation.di.MainActivityComponent
import com.example.todoapp.presentation.todoList.di.TodoFragmentComponent
import com.example.todoapp.presentation.viewmodel.ViewModelModule
import com.example.todoapp.presentation.viewmodel.ViewModelsMap
import dagger.BindsInstance
import dagger.Component

/**
 * Главный скоуп приложения
 */
@AppScope
@MyWorkerScope
@RepositoryScope
@Component(modules = [DataModule::class, DatabaseModule::class, NetworkModule::class, ViewModelModule::class])
interface AppComponent {

    fun inject(app: App)

    fun getViewModelsMap(): ViewModelsMap

    fun getTodoFragmentComponentFactory(): TodoFragmentComponent.Factory

    fun getMainActivityComponentFactory(): MainActivityComponent.Factory

    fun getAlarmServiceComponentFactory(): AlarmServiceComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}
