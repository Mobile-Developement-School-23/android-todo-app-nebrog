package com.example.todoapp.presentation.todoList

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.R
import com.example.todoapp.domain.TodoItem
import com.example.todoapp.domain.TodoRepository
import com.example.todoapp.domain.TodoRepository.Result.Failure
import com.example.todoapp.domain.TodoRepository.Result.Success
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel UI класса TodoListFragment. Связывает слои Presentation и Domain.
 */
class TodoListViewModel @Inject constructor(private val repository: TodoRepository) : ViewModel() {

    private val mutableStates: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    private val mutableActions = MutableSharedFlow<Actions>(replay = 0)
    private val isHidden: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private val isOnline = MutableStateFlow<Boolean>(true)
    private var collectJob: Job? = null

    val actions: MutableSharedFlow<Actions> = mutableActions
    val states: StateFlow<State> = mutableStates

    init {
        loadContent()
        observeForOnline()
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

    fun onRetryClick() {
        mutableStates.value = State.Loading
        loadContent()
    }

    fun onOnlineChanged(isConnected: Boolean) {
        isOnline.value = isConnected
    }

    private fun loadContent() {
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            val result = repository.getAllTodos()
            when (result) {
                is Failure -> mutableStates.value = State.Error
                is Success -> emitSuccessState(result.value, isHidden.value)
            }

            repository.observeTodos().combine(isHidden) { todos, isHidden ->
                emitSuccessState(todos, isHidden)
            }.catch {
                mutableStates.value = State.Error
            }.collect()
        }
    }

    private fun emitSuccessState(todos: List<TodoItem>, isHidden: Boolean) {
        val doneCount = todos.count { it.doneFlag }
        val items = if (isHidden) {
            todos.filter { !it.doneFlag }
        } else {
            todos
        }
        val success = State.Success(items, isHidden, doneCount, isOnline.value)
        mutableStates.value = success
    }

    private fun observeForOnline() {
        viewModelScope.launch {
            // .drop - пропустить первое значение, так как инициализирующее значение MutableStateFlow<Boolean>(true), при пересоздании фрагмента
            // вьюмодель получает состояние isConnected еще раз (MutableSharedFlow не фильтрует одиковые значений), поэтому использую MutableStateFlow
            // .debounce - нужен при переключении с Wi-Fi на 4G, потому что в этот момент connectivity быстро меняет свое значение несколько раз
            isOnline.drop(1).debounce(ONLINE_DEBOUNCE_TIME).collect { isOnline ->
                if (isOnline) {
                    onRetryClick()
                } else {
                    val lastSuccessState = mutableStates.value as? State.Success
                    if (lastSuccessState != null) {
                        mutableStates.value = lastSuccessState.copy(isOnline = false)
                    }
                }
            }
        }
    }

    sealed interface State {
        data class Success(
            val items: List<TodoItem>,
            val isHidden: Boolean,
            val doneCount: Int,
            val isOnline: Boolean,
        ) : State

        object Loading : State

        object Error : State
    }

    sealed interface Actions {

        class Error(@StringRes val messageID: Int) : Actions
    }

    companion object {
        private const val ONLINE_DEBOUNCE_TIME = 300L
    }
}
