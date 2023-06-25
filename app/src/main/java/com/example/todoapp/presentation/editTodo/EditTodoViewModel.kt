package com.example.todoapp.presentation.editTodo

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.todoapp.data.StubTodoRepository
import com.example.todoapp.domain.TodoItem
import com.example.todoapp.domain.TodoRepository
import java.util.Calendar
import java.util.Date

class EditTodoViewModel : ViewModel() {

    private val repository = StubTodoRepository

    suspend fun onChangeTodo(id: String, text: String, priority: TodoItem.Priority, deadline: Date?, doneFlag: Boolean, dateOfCreation: Date, fragment: Fragment) {
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
        backTodoList(fragment)
    }

    suspend fun onDeleteTodo(id: String, fragment: Fragment) {
        repository.deleteTodo(id)
        backTodoList(fragment)
    }

    suspend fun getTodo(id: String): TodoItem {
        val result = repository.getTodo(id)
        return when(result){
            is TodoRepository.Result.Failure -> TODO()
            is TodoRepository.Result.Success -> result.value
        }
    }

    private fun backTodoList(fragment: Fragment) {
        fragment
            .parentFragmentManager
            .popBackStackImmediate()
    }
}