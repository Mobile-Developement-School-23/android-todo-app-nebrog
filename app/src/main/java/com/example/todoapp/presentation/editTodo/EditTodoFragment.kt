package com.example.todoapp.presentation.editTodo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.presentation.compose.DetailTodoItem
import com.example.todoapp.presentation.compose.LoadingState
import com.example.todoapp.presentation.compose.TodoTheme
import com.example.todoapp.presentation.editTodo.EditTodoViewModel.Actions.Error
import com.example.todoapp.presentation.editTodo.EditTodoViewModel.Actions.Exit
import com.example.todoapp.presentation.editTodo.EditTodoViewModel.State.Loading
import com.example.todoapp.presentation.editTodo.EditTodoViewModel.State.Success
import com.example.todoapp.presentation.viewmodel.vladViewModels
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest

/**
 * UI класс, который отвечает за редактирование элемента в списке.
 */
class EditTodoFragment : Fragment() {

    private val viewModel by vladViewModels<EditTodoViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            val todoID = requireArguments().getString(ARGUMENT_KEY) as String
            viewModel.init(todoID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                TodoTheme {
                    val state = viewModel.states.collectAsStateWithLifecycle()
                    val value = state.value
                    when (value) {
                        Loading -> LoadingState()
                        is Success -> SuccessState(value)
                    }
                    LaunchedEffect(Unit) {
                        viewModel.actions.collectLatest { action ->
                            when (action) {
                                Exit -> parentFragmentManager.popBackStackImmediate()
                                is Error -> showErrorAction(action, this@apply)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun SuccessState(state: Success) {
        DetailTodoItem(
            todo = state.item,
            onSaveClick = { text -> viewModel.onSaveClick(text) },
            onCloseClick = parentFragmentManager::popBackStackImmediate,
            onDeleteClick = { viewModel.onDeleteTodo() },
            onPriorityChanged = { priority -> viewModel.onPriorityChanged(priority) },
            onDeadlineChanged = viewModel::onDeadlineChanged
        )
    }

    private fun showErrorAction(state: Error, view: View) {
        Snackbar.make(view, state.messageID, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        private const val ARGUMENT_KEY = "TodoID"

        fun createNewInstance(id: String): EditTodoFragment {
            val bundle = Bundle()
            val fragmentEdit = EditTodoFragment()
            bundle.putString(ARGUMENT_KEY, id)
            fragmentEdit.arguments = bundle
            return fragmentEdit
        }
    }
}
