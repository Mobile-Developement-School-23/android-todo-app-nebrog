package com.example.todoapp.presentation.todoList

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.R
import com.example.todoapp.data.network.NetworkRepository
import com.example.todoapp.domain.TodoItem
import com.example.todoapp.domain.TodoRepository.Result.Failure
import com.example.todoapp.domain.TodoRepository.Result.Success
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class TodoListViewModel : ViewModel() {

    private val repository = NetworkRepository
    private val mutableStates: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    private val mutableActions = MutableSharedFlow<Actions>(replay = 0)
    private val isHidden: MutableStateFlow<Boolean> = MutableStateFlow(true)

    val actions: MutableSharedFlow<Actions> = mutableActions
    val states: StateFlow<State> = mutableStates


    init {
        viewModelScope.launch {
            repository.observeTodos().combine(isHidden) { a, b -> a to b }.collect { (todos, isHidden) ->
                val doneCount = todos.count { it.doneFlag }
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
            val resultTodo = repository.getTodo(id)
            val todo = when (resultTodo) {
                is Failure -> {
                    mutableActions.emit(Actions.Error(R.string.update_error))
                    return@launch
                }

                is Success -> resultTodo.value
            }
            val todoDone = todo.copy(doneFlag = isDone)
            val resultUpdate = repository.updateTodo(todoDone)
            if (resultUpdate is Failure) {
                mutableActions.emit(Actions.Error(R.string.update_error))
                return@launch
            }
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

        object Loading : State
    }


    sealed interface Actions {

        class Error(@StringRes val messageID: Int) : Actions
    }
}