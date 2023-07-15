package com.example.todoapp.presentation.addTodo

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.R
import com.example.todoapp.presentation.addTodo.AddTodoViewModel.Actions
import com.example.todoapp.presentation.addTodo.AddTodoViewModel.State.Loading
import com.example.todoapp.presentation.addTodo.AddTodoViewModel.State.Success
import com.example.todoapp.presentation.compose.DetailTodoItem
import com.example.todoapp.presentation.compose.LoadingState
import com.example.todoapp.presentation.compose.TodoTheme
import com.example.todoapp.presentation.viewmodel.vladViewModels
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar
import java.util.Date

/**
 * UI класс, который отвечает за добавление нового элемента в список.
 */
class AddTodoFragment : Fragment() {

    private val viewModel by vladViewModels<AddTodoViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                TodoTheme {
                    val state: State<AddTodoViewModel.State> = viewModel.states.collectAsStateWithLifecycle()
                    val value = state.value
                    when (value) {
                        Loading -> LoadingState()
                        is Success -> SuccessState(value)
                    }
                    LaunchedEffect(Unit) {
                        viewModel.actions.collectLatest { action ->
                            when (action) {
                                Actions.Exit -> parentFragmentManager.popBackStackImmediate()
                                Actions.CalendarPicker -> showCalendarPicker(context)
                                is Actions.Error -> showErrorState(action, this@apply)
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
            onSaveClick = { text -> viewModel.onTodoSave(text) },
            onCloseClick = parentFragmentManager::popBackStackImmediate,
            onDeleteClick = null,
            onPriorityChanged = viewModel::onPriorityChanged,
            onCheckChanged = viewModel::onCheckedChanged,

            )
    }

    private fun showCalendarPicker(context: Context) {
        val c = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _, mYear, mMonth, mDay ->
                viewModel.onDeadlineChanged(Date(mYear - DATE_YEAR_OFFSET, mMonth, mDay))
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.setButton(
            DialogInterface.BUTTON_NEGATIVE,
            getString(R.string.cancel)
        ) { _, _ ->
            viewModel.onCalendarCancel()
        }
        datePickerDialog.setOnCancelListener {
            viewModel.onCalendarCancel()
        }
        datePickerDialog.show()
    }

    private fun showErrorState(state: Actions.Error, view: View) {
        Snackbar.make(view, state.messageID, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        const val DATE_YEAR_OFFSET = 1900
    }
}
