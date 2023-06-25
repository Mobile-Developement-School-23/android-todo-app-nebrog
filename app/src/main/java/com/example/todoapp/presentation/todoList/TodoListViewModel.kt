package com.example.todoapp.presentation.todoList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.StubTodoRepository
import com.example.todoapp.domain.TodoItem
import com.example.todoapp.domain.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class TodoListViewModel : ViewModel() {

    private val repository = StubTodoRepository
    private val mutableStates: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    private val isHidden: MutableStateFlow<Boolean> = MutableStateFlow(true)

    val states: StateFlow<State> = mutableStates


    init {
        viewModelScope.launch {
            repository.observeTodos().combine(isHidden) { a, b -> a to b }.collect { (todos, isHidden) ->
                val doneCount = todos.count { it.doneFlag }
                println("KEK $doneCount")
                val items = if (isHidden) {
                    todos.filter { !it.doneFlag }
                } else {
                    todos
                }
                val success = State.Success(items, isHidden, doneCount)
                mutableStates.value = success
            }
        }
    }

    fun onDoneClick(id: String, isDone: Boolean) {
        viewModelScope.launch {
            val loading = State.Loading
            mutableStates.value = loading
            val resultTodo = repository.getTodo(id)
            val todo = when (resultTodo) {
                is TodoRepository.Result.Failure -> {
                    mutableStates.value = State.Error
                    return@launch
                }

                is TodoRepository.Result.Success -> resultTodo.value
            }
            val todoDone = todo.copy(doneFlag = isDone)
            val resultUpdate = repository.updateTodo(todoDone)
            if (resultUpdate is TodoRepository.Result.Failure) {
                mutableStates.value = State.Error
                return@launch
            }
            println("KEKOnDone $id $isDone")
        }

    }

    fun onHideClick() {
        isHidden.value = !isHidden.value
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