package com.example.todoapp.presentation.todoList

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.R
import com.example.todoapp.data.StubTodoRepository
import com.example.todoapp.domain.TodoItem
import com.example.todoapp.presentation.addTodo.AddTodoFragment
import com.example.todoapp.presentation.editTodo.EditTodoFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TodoListViewModel : ViewModel() {

    private val repository = StubTodoRepository
    private val mutableStates: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    private var isHidden = true

    val states: StateFlow<State> = mutableStates

    init {
        viewModelScope.launch {
            repository.observeTodos().collect { todos ->
                createState(todos)
            }
        }
    }

    fun onAddClick(fragment: Fragment) {
        fragment
            .parentFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_view, AddTodoFragment())
            .addToBackStack(null)
            .commit()
    }

    fun onTodoClick(id: String, fragment: Fragment) {
        val editTodo = EditTodoFragment.createNewInstance(id)
        fragment
            .parentFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_view, editTodo)
            .addToBackStack(null)
            .commit()
    }

    fun onDoneClick(id: String, isDone: Boolean) {
        val todo = repository.getTodo(id)
        val todoDone = todo.copy(doneFlag = isDone)
        repository.updateTodo(todoDone)
    }

    fun onHideClick() {
        isHidden = !isHidden
        createState(repository.getAllTodos())
    }

    private fun createState(todos: List<TodoItem>) {
        val doneCount = todos.count { it.doneFlag }
        val items = if (isHidden) {
            todos.filter { !it.doneFlag }
        } else {
            todos
        }
        val success = State.Success(items, isHidden, doneCount)
        mutableStates.value = success
    }

    sealed interface State {
        data class Success(
            val items: List<TodoItem>,
            val isHidden: Boolean,
            val doneCount: Int
        ) : State

        object Loading : State
    }
}