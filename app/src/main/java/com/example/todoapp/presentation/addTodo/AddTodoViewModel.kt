package com.example.todoapp.presentation.addTodo

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.todoapp.data.StubTodoRepository
import com.example.todoapp.domain.TodoItem
import java.time.Instant
import java.util.Date

class AddTodoViewModel : ViewModel() {

    private val repository = StubTodoRepository

    fun onTodoSave(text: String, priority: TodoItem.Priority, deadline: Date?, fragment: Fragment) {
        val todo = TodoItem(
            itemID = "",
            itemText = text,
            itemPriority = priority,
            deadline = deadline,
            doneFlag = false,
            dateOfCreation = Date.from(Instant.now()),
            dateOfChanges = null
        )
        repository.addTodo(todo)
        fragment
            .parentFragmentManager
            .popBackStackImmediate()
    }
}