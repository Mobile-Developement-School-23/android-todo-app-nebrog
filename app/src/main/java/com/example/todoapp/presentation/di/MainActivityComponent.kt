package com.example.todoapp.presentation.di

import com.example.todoapp.di.MainActivityScope
import com.example.todoapp.presentation.MainActivity
import dagger.Subcomponent

@Subcomponent
@MainActivityScope
interface MainActivityComponent {

    fun inject(activity: MainActivity)

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainActivityComponent
    }
}