package com.example.todoapp.presentation.addTodo

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.R
import com.example.todoapp.domain.TodoItem
import com.example.todoapp.domain.TodoRepository
import com.example.todoapp.domain.TodoRepository.Result.Failure
import com.example.todoapp.domain.TodoRepository.Result.Success
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel UI класса AddTodoFragment. Связывает слои Presentation и Domain.
 */
class AddTodoViewModel @Inject constructor(private val repository: TodoRepository) : ViewModel() {

    private val mutableStates: MutableStateFlow<State> = MutableStateFlow(State.Success(generateEmptyTodo()))
    private val mutableActions = MutableSharedFlow<Actions>(replay = 0)

    val states: StateFlow<State> = mutableStates
    val actions: SharedFlow<Actions> = mutableActions

    fun onTodoSave(text: String) {
        val state: State.Success = getSuccessState() ?: return
        val todo = state.item.copy(itemText = text)
        viewModelScope.launch {
            mutableStates.value = State.Loading
            val result = repository.addTodo(todo)
            when (result) {
                is Failure -> {
                    mutableStates.value = State.Success(todo)
                    mutableActions.emit(Actions.Error(R.string.save_error))
                }

                is Success -> mutableActions.emit(Actions.Exit)
            }
        }
    }

    fun onDeadlineChanged(date: Date?) {
        val state: State.Success = getSuccessState() ?: return
        val updatedItem = state.item.copy(deadline = date)
        mutableStates.value = State.Success(updatedItem)
    }

    fun onPriorityChanged(priority: TodoItem.Priority) {
        val state: State.Success = getSuccessState() ?: return
        val updatedItem = state.item.copy(itemPriority = priority)
        mutableStates.value = State.Success(updatedItem)
    }

    private fun getSuccessState(): State.Success? {
        return states.value as? State.Success
    }

    private fun generateEmptyTodo(): TodoItem {
        return TodoItem(
            itemID = UUID.randomUUID().toString(),
            itemText = "",
            itemPriority = TodoItem.Priority.NORMAL,
            deadline = null,
            doneFlag = false,
            dateOfCreation = Calendar.getInstance().time,
            dateOfChanges = Calendar.getInstance().time,
        )
    }

    sealed interface State {
        data class Success(val item: TodoItem) : State

        object Loading : State
    }

    sealed interface Actions {
        object Exit : Actions

        class Error(@StringRes val messageID: Int) : Actions
    }
}
