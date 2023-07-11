package com.example.todoapp.data

import com.example.todoapp.domain.TodoItem
import com.example.todoapp.domain.TodoRepository
import com.example.todoapp.domain.TodoRepository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.Calendar
import java.util.UUID
import kotlin.random.Random

/**
 * Класс без реальных запросов, предоставляющий уже готовые данные
 */
object StubTodoRepository : TodoRepository {

    private val random = Random(System.currentTimeMillis())
    private val todos = MutableList(30) { getRandomTodo() }
    private val flow = MutableSharedFlow<List<TodoItem>>(replay = 1)
    private var counter = 0

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

    private fun getRandomTodo(): TodoItem {
        val texts = listOf(
            "Купить хлеб",
            "Выкинуть мусор, выучить английский, написать книгу",
            "Заселить Марс, посадить лес, построить руками город, собрать машину, проплыть океан, 10 раз отжаться, позвонить в сбербанк - предложить кредитную карту, научить сальто",
        )
        val changeDate = if (random.nextBoolean()) Calendar.getInstance().time else null
        val deadline = if (random.nextBoolean()) Calendar.getInstance().time else null
        return TodoItem(
            itemID = UUID.randomUUID().toString(),
            itemText = "${texts.random(random)} ${counter++}",
            itemPriority = TodoItem.Priority.values().random(random),
            deadline = deadline,
            doneFlag = random.nextBoolean(),
            dateOfCreation = Calendar.getInstance().time,
            dateOfChanges = changeDate,
        )
    }
}
