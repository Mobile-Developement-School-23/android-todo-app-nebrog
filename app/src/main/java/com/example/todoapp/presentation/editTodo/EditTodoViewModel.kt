package com.example.todoapp.presentation.editTodo

import androidx.lifecycle.ViewModel
import com.example.todoapp.data.network.NetworkRepository
import com.example.todoapp.domain.TodoItem
import com.example.todoapp.domain.TodoRepository
import java.util.Calendar
import java.util.Date

class EditTodoViewModel : ViewModel() {

    private val repository = NetworkRepository

    suspend fun onChangeTodo(id: String, text: String, priority: TodoItem.Priority, deadline: Date?, doneFlag: Boolean, dateOfCreation: Date) {
        val todo = TodoItem(
            itemID = id,
            itemText = text,
            itemPriority = priority,
            deadline = deadline,
            doneFlag = doneFlag,
            dateOfCreation = dateOfCreation,
            dateOfChanges = Calendar.getInstance().time
        )
        repository.updateTodo(todo)
    }

    suspend fun onDeleteTodo(id: String, item: TodoItem) {
        repository.deleteTodo(id, item)
    }

    suspend fun getTodo(id: String): TodoItem {
        val result = repository.getTodo(id)
        return when (result) {
            is TodoRepository.Result.Failure -> TODO()
            is TodoRepository.Result.Success -> result.value
        }
    }

    sealed interface State {
        data class Success(
            val items: List<TodoItem>,
            val isHidden: Boolean,
            val doneCount: Int
        ) : State

        object Error : State

        object Loading : State
    }
}