package com.example.todoapp.data

import com.example.todoapp.domain.TodoItem
import com.example.todoapp.domain.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.Instant
import java.util.Date
import java.util.UUID
import kotlin.random.Random

object StubTodoRepository : TodoRepository {

    private val random = Random(System.currentTimeMillis())
    private val todos = MutableList(30) { getRandomTodo() }
    private val flow = MutableSharedFlow<List<TodoItem>>(replay = 1)
    private var counter = 0

    init {
        flow.tryEmit(todos)
    }

    override fun addTodo(item: TodoItem) {
        val itemWithID = item.copy(itemID = UUID.randomUUID().toString())
        todos.add(itemWithID)
        flow.tryEmit(todos.toList())
    }

    override fun deleteTodo(id: String) {
        todos.removeIf { it.itemID == id }
        flow.tryEmit(todos.toList())
    }

    override fun updateTodo(item: TodoItem) {
        val index = todos.indexOfFirst { it.itemID == item.itemID }
        if (index == -1) {
            return
        }
        todos[index] = item
        flow.tryEmit(todos.toList())
    }

    override fun getTodo(id: String): TodoItem {
        val index = todos.indexOfFirst { it.itemID == id }
        if (index == -1) {
            TODO()
        }
        flow.tryEmit(todos.toList())
        return todos[index]
    }

    override fun observeTodos(): Flow<List<TodoItem>> {
        return flow
    }

    override fun getAllTodos(): List<TodoItem> {
        return todos.toList()
    }

    private fun getRandomTodo(): TodoItem {
        val texts = listOf(
            "Купить хлеб",
            "Выкинуть мусор, выучить английский, написать книгу",
            "Заселить Марс, посадить лес, построить руками город, собрать машину, проплыть океан, 10 раз отжаться, позвонить в сбербанк - предложить кредитную карту, научить сальто",
        )
        val changeDate = if (random.nextBoolean()) randomDate(+500_000) else null
        val deadline = if (random.nextBoolean()) randomDate(+500_000_000) else null
        return TodoItem(
            itemID = UUID.randomUUID().toString(),
            itemText = "${texts.random(random)} ${counter++}",
            itemPriority = TodoItem.Priority.values().random(random),
            deadline = deadline,
            doneFlag = random.nextBoolean(),
            dateOfCreation = randomDate(-1_000_000),
            dateOfChanges = changeDate,
        )
    }

    private fun randomDate(seconds: Long): Date {
        return if (seconds >= 0) {
            Date.from(Instant.now().plusSeconds(random.nextLong(0, seconds)))
        } else {
            Date.from(Instant.now().minusSeconds(random.nextLong(0, -seconds)))
        }
    }

}