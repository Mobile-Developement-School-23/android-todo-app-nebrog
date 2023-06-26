package com.example.todoapp.data

import com.example.todoapp.domain.TodoItem
import com.example.todoapp.domain.TodoRepository
import kotlinx.coroutines.flow.Flow

class NetworkRepository: TodoRepository {


    override suspend fun addTodo(item: TodoItem): TodoRepository.Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTodo(id: String): TodoRepository.Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateTodo(item: TodoItem): TodoRepository.Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getTodo(id: String): TodoRepository.Result<TodoItem> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllTodos(): TodoRepository.Result<List<TodoItem>> {
        TODO("Not yet implemented")
    }

    override fun observeTodos(): Flow<List<TodoItem>> {
        TODO("Not yet implemented")
    }

}