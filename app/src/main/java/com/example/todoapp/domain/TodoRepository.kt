package com.example.todoapp.domain

import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun addTodo(item: TodoItem)
    fun deleteTodo(id: String)
    fun updateTodo(item: TodoItem)
    fun getTodo(id: String): TodoItem
    fun getAllTodos(): List<TodoItem>
    fun observeTodos(): Flow<List<TodoItem>>
}