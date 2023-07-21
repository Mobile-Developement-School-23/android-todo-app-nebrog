package com.example.todoapp.di

import javax.inject.Scope

/**
 * Файлы с кастомными скоупами
 */

@Scope
annotation class AppScope

@Scope
annotation class MyWorkerScope

@Scope
annotation class RepositoryScope

@Scope
annotation class TodoListFragmentScope

@Scope
annotation class AddTodoFragmentScope

@Scope
annotation class EditTodoFragmentScope

@Scope
annotation class MainActivityScope

@Scope
annotation class AlarmServiceScope
