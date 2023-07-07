package com.example.todoapp.presentation.addTodo.di

import com.example.todoapp.di.AddTodoFragmentScope
import dagger.Subcomponent

@Subcomponent
@AddTodoFragmentScope
interface AddTodoFragmentComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): AddTodoFragmentComponent
    }
}
