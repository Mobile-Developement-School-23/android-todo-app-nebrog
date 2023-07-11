package com.example.todoapp.presentation.todoList.di

import com.example.todoapp.di.TodoListFragmentScope
import com.example.todoapp.presentation.todoList.Callback
import com.example.todoapp.presentation.todoList.TodoListFragment
import dagger.BindsInstance
import dagger.Subcomponent

/**
 * Скоуп для экрана со списком элементов
 */
@Subcomponent
@TodoListFragmentScope
interface TodoFragmentComponent {

    fun inject(fragment: TodoListFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance callback:Callback): TodoFragmentComponent
    }
}
