package com.example.todoapp.presentation.addTodo

import androidx.lifecycle.ViewModel
import com.example.todoapp.data.network.NetworkRepository
import com.example.todoapp.domain.TodoItem
import java.util.Calendar
import java.util.Date

class AddTodoViewModel : ViewModel() {

    private val repository = NetworkRepository

    suspend fun onTodoSave(id: String, text: String, priority: TodoItem.Priority, deadline: Date?) {
        val todo = TodoItem(
            itemID = id,
            itemText = text,
            itemPriority = priority,
            deadline = deadline,
            doneFlag = false,
            dateOfCreation = Calendar.getInstance().time,
            dateOfChanges = Calendar.getInstance().time
        )
        repository.addTodo(todo)

    }
}