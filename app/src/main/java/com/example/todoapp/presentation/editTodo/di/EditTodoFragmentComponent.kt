package com.example.todoapp.presentation.editTodo.di

import com.example.todoapp.di.EditTodoFragmentScope
import dagger.Subcomponent

/**
 * Скоуп для экрана изменения элемента
 */
@Subcomponent
@EditTodoFragmentScope
interface EditTodoFragmentComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): EditTodoFragmentComponent
    }
}
