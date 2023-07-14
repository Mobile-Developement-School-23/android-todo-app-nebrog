package com.example.todoapp.domain

interface DeadlineManager {
    fun setAlarm(todoItem: TodoItem)
    fun cancelAlarm(itemId: String)
    fun getRequiredPermissions(): List<String>
}