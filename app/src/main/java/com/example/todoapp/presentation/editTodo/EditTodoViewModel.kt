package com.example.todoapp.presentation.editTodo

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.todoapp.data.StubTodoRepository
import com.example.todoapp.domain.TodoItem
import java.time.Instant
import java.util.Date

class EditTodoViewModel : ViewModel() {

    private val repository = StubTodoRepository

    fun onChangeTodo(id: String, text: String, priority: TodoItem.Priority, deadline: Date?, doneFlag: Boolean, dateOfCreation: Date, fragment: Fragment) {
        val todo = TodoItem(
            itemID = id,
            itemText = text,
            itemPriority = priority,
            deadline = deadline,
            doneFlag = doneFlag,
            dateOfCreation = dateOfCreation,
            dateOfChanges = Date.from(Instant.now())
        )
        repository.updateTodo(todo)
        backTodoList(fragment)
    }

    fun onDeleteTodo(id: String, fragment: Fragment) {
        repository.deleteTodo(id)
        backTodoList(fragment)
    }

    fun getTodo(id: String): TodoItem {
        return repository.getTodo(id)
    }

    private fun backTodoList(fragment: Fragment) {
        fragment
            .parentFragmentManager
            .popBackStackImmediate()
    }
}