package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.todoapp.presentation.addTodo.AddTodoViewModel
import com.example.todoapp.presentation.editTodo.EditTodoViewModel
import com.example.todoapp.presentation.todoList.TodoListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Интерфейс, который собирает Viewmodel экранов в общуюю мапу.
 * Живет в AppScope
 */
@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AddTodoViewModel::class)
    fun bindAddTodoViewModel(viewModel: AddTodoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TodoListViewModel::class)
    fun bindTodoListViewModel(viewModel: TodoListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditTodoViewModel::class)
    fun bindEditTodoViewModel(viewModel: EditTodoViewModel): ViewModel
}
