package com.example.todoapp.data.network

import android.provider.Settings
import com.example.todoapp.App
import com.example.todoapp.domain.TodoItem
import com.example.todoapp.domain.TodoRepository
import com.example.todoapp.domain.TodoRepository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.Date


object NetworkRepository : TodoRepository {

    private val service = RetrofitService.getInstance()
    private val androidID = Settings.Secure.getString(App.application.contentResolver, Settings.Secure.ANDROID_ID)
    private val revision = MutableStateFlow(0)


    override suspend fun addTodo(item: TodoItem): Result<Unit> {
        return networkCall(action = {
            val result = service.addTodo(revision.value, TodoItemRequest(converterRequest(item)))
            revision.value = result.revision
        })
    }

    override suspend fun deleteTodo(id: String, item: TodoItem): Result<Unit> {
        return networkCall(action = {
            val result = service.deleteTodo(revision.value, id, TodoItemRequest(converterRequest(item)))
            revision.value = result.revision
        })

    }

    override suspend fun updateTodo(item: TodoItem): Result<Unit> {
        return networkCall(action = {
            val result = service.updateTodo(revision.value, item.itemID, TodoItemRequest(converterRequest(item)))
            revision.value = result.revision
        })
    }


    override suspend fun getTodo(id: String): Result<TodoItem> {
        return networkCall(action = {
            val response = service.getTodo(id)
            val todo = converterResponse(response.element)
            revision.value = response.revision
            return@networkCall todo
        })
    }

    override suspend fun getAllTodos(): Result<List<TodoItem>> {
        return networkCall(action = {
            val response = service.getTodoList()
            val listTodoItem = response.list.map { pojo: TodoItemPOJO ->
                converterResponse(pojo)
            }
            revision.value = response.revision
            return@networkCall listTodoItem
        })
    }

    override suspend fun updateAllTodos(updateList: List<TodoItem>): Result<List<TodoItem>> {
        return networkCall {
            val listUpdateTodoItem = updateList.map { todo -> converterRequest(todo) }
            val response = service.updateTodosOnServer(revision.value, TodoItemListRequest(listUpdateTodoItem))
            val listTodoItem = response.list.map { pojo: TodoItemPOJO ->
                converterResponse(pojo)
            }
            revision.value = response.revision
            return@networkCall listTodoItem
        }
    }

    override fun observeTodos(): Flow<List<TodoItem>> {
        return revision.map {
            val result = getAllTodos()
            val list = when (result) {
                is Result.Failure -> throw IOException()
                is Result.Success -> result.value
            }
            return@map list
        }
    }

    private fun converterResponse(pojo: TodoItemPOJO): TodoItem {

        val priority = when (pojo.itemPriority) {
            TodoItemPOJO.Priority.LOW -> TodoItem.Priority.LOW
            TodoItemPOJO.Priority.NORMAL -> TodoItem.Priority.NORMAL
            TodoItemPOJO.Priority.HIGH -> TodoItem.Priority.HIGH
        }

        return TodoItem(
            pojo.itemID,
            pojo.itemText,
            priority,
            pojo.deadline?.let { Date(it) },
            pojo.doneFlag,
            Date(pojo.dateOfCreation),
            pojo.dateOfChanges?.let { Date(it) }
        )
    }

    private fun converterRequest(todoItem: TodoItem): TodoItemPOJO {

        val priority = when (todoItem.itemPriority) {
            TodoItem.Priority.LOW -> TodoItemPOJO.Priority.LOW
            TodoItem.Priority.NORMAL -> TodoItemPOJO.Priority.NORMAL
            TodoItem.Priority.HIGH -> TodoItemPOJO.Priority.HIGH

        }

        return TodoItemPOJO(
            todoItem.itemID,
            todoItem.itemText,
            priority,
            todoItem.deadline?.time,
            todoItem.doneFlag,
            todoItem.dateOfCreation.time,
            todoItem.dateOfChanges?.time,
            androidID
        )
    }

    private suspend fun <R> networkCall(action: suspend () -> R): Result<R> {
        try {
            val result: R = withContext(Dispatchers.IO) { action() }
            return Result.Success(result)
        } catch (e: HttpException) {
            return Result.Failure(e.message())
        } catch (e: IOException) {
            return Result.Failure(e.message.toString())
        }

    }

}



