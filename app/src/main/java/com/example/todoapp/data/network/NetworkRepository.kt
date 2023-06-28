package com.example.todoapp.data.network

import android.provider.Settings
import com.example.todoapp.App
import com.example.todoapp.domain.TodoItem
import com.example.todoapp.domain.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.Date


object NetworkRepository : TodoRepository {

    private val service = RetrofitService.getInstance()
    private var revision = 0
    private val androidID = Settings.Secure.getString(App.application.contentResolver, Settings.Secure.ANDROID_ID)


    override suspend fun addTodo(item: TodoItem): TodoRepository.Result<Unit> {
        return networkCall(action = {
            service.addTodo(revision, TodoItemRequest(converterRequest(item)))
        })
    }

    override suspend fun deleteTodo(id: String, item: TodoItem): TodoRepository.Result<Unit> {
        return networkCall(action = {
            service.deleteTodo(revision, id, TodoItemRequest(converterRequest(item)))
        })

    }

    override suspend fun updateTodo(item: TodoItem): TodoRepository.Result<Unit> {
        return networkCall(action = {
            service.updateTodo(revision, item.itemID, TodoItemRequest(converterRequest(item)))
        })
    }


    override suspend fun getTodo(id: String): TodoRepository.Result<TodoItem> {
        return networkCall(action = {
            val response = service.getTodo(id)
            val todo = converterResponse(response.element)
            return@networkCall todo
        })
    }

    override suspend fun getAllTodos(): TodoRepository.Result<List<TodoItem>> {
        return networkCall(action = {
            val response = service.getTodoList()
            val listTodoItem = response.list.map { pojo: TodoItemPOJO ->
                converterResponse(pojo)
            }
            revision = response.revision
            return@networkCall listTodoItem
        })
    }

    override fun observeTodos(): Flow<List<TodoItem>> {
        return flowOf()
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

    private suspend fun <R> networkCall(action: suspend () -> R): TodoRepository.Result<R> {
        try {
            val result: R = withContext(Dispatchers.IO) { action() }
            return TodoRepository.Result.Success(result)
        } catch (e: HttpException) {
            return TodoRepository.Result.Failure(e.message())
        } catch (e: IOException) {
            return TodoRepository.Result.Failure(e.message.toString())
        }

    }

}



