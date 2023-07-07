package com.example.todoapp.di

import android.content.Context
import com.example.todoapp.App
import com.example.todoapp.data.di.DataModule
import com.example.todoapp.data.di.DatabaseModule
import com.example.todoapp.data.di.NetworkModule
import com.example.todoapp.presentation.addTodo.di.AddTodoFragmentComponent
import com.example.todoapp.presentation.editTodo.di.EditTodoFragmentComponent
import com.example.todoapp.presentation.todoList.di.TodoFragmentComponent
import com.example.todoapp.presentation.viewmodel.ViewModelModule
import com.example.todoapp.presentation.viewmodel.ViewModelsMap
import dagger.BindsInstance
import dagger.Component

@AppScope
@MyWorkerScope
@RepositoryScope
@Component(modules = [
    DataModule::class,
    DatabaseModule::class,
    NetworkModule::class,
    ViewModelModule::class,
])
interface AppComponent {

    fun inject(app: App)

    fun getViewModelsMap(): ViewModelsMap

    fun getTodoFragmentComponentFactory(): TodoFragmentComponent.Factory

    fun getEditTodoFragmentComponentFactory(): EditTodoFragmentComponent.Factory

    fun getAddTodoFragmentComponentFactory(): AddTodoFragmentComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}
