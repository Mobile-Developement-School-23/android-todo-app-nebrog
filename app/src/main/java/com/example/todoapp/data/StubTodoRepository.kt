package com.example.todoapp.data

import com.example.todoapp.domain.TodoItem
import com.example.todoapp.domain.TodoRepository
import com.example.todoapp.domain.TodoRepository.Result
import com.example.todoapp.utils.getRandomTodo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.UUID

/**
 * Класс без реальных запросов, предоставляющий уже готовые данные
 */
object StubTodoRepository : TodoRepository {

    private val todos = MutableList(30) { getRandomTodo() }
    private val flow = MutableSharedFlow<List<TodoItem>>(replay = 1)

    init {
        flow.tryEmit(todos)
    }

    override suspend fun addTodo(item: TodoItem): Result<Unit> {
        val itemWithID = item.copy(itemID = UUID.randomUUID().toString())
        todos.add(itemWithID)
        flow.emit(todos.toList())
        return Result.Success(Unit)
    }

    override suspend fun deleteTodo(id: String): Result<Unit> {
        todos.removeIf { it.itemID == id }
        flow.emit(todos.toList())
        return Result.Success(Unit)
    }

    override suspend fun updateTodo(item: TodoItem): Result<Unit> {
        val index = todos.indexOfFirst { it.itemID == item.itemID }
        if (index == -1) {
            return Result.Failure("No item with such ID")
        }
        todos[index] = item
        flow.emit(todos.toList())
        return Result.Success(Unit)
    }

    override suspend fun getTodo(id: String): Result<TodoItem> {
        val index = todos.indexOfFirst { it.itemID == id }
        if (index == -1) {
            return Result.Failure("No item with such ID")
        }
        flow.emit(todos.toList())
        return Result.Success(todos[index])
    }

    override fun observeTodos(): Flow<List<TodoItem>> {
        return flow
    }

    override suspend fun getAllTodos(): Result<List<TodoItem>> {
        return Result.Success(todos.toList())
    }

    override suspend fun updateAllTodos(updateList: List<TodoItem>): Result<List<TodoItem>> {
        TODO("Not yet implemented")
    }
}
