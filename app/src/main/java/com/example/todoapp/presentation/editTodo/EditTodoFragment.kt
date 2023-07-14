package com.example.todoapp.presentation.editTodo

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.R
import com.example.todoapp.presentation.compose.DetailTodoItem
import com.example.todoapp.presentation.compose.TodoTheme
import com.example.todoapp.presentation.editTodo.EditTodoViewModel.Actions.CalendarPicker
import com.example.todoapp.presentation.editTodo.EditTodoViewModel.Actions.Error
import com.example.todoapp.presentation.editTodo.EditTodoViewModel.Actions.Exit
import com.example.todoapp.presentation.editTodo.EditTodoViewModel.Actions.SetText
import com.example.todoapp.presentation.editTodo.EditTodoViewModel.State.Loading
import com.example.todoapp.presentation.editTodo.EditTodoViewModel.State.Success
import com.example.todoapp.presentation.viewmodel.vladViewModels
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar
import java.util.Date

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
                                CalendarPicker -> showCalendarPicker(context)
                                is Error -> showErrorAction(action, this@apply)
                                is SetText -> {}
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
            onCheckChanged = viewModel::onCheckedChanged
        )
    }

    @Composable
    private fun LoadingState() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .height(120.dp)
                    .width(120.dp),
                color = colorResource(id = R.color.color_blue),
                strokeWidth = 4.dp
            )
        }
    }

    private fun showErrorAction(state: Error, view: View) {
        Snackbar.make(view, state.messageID, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.color_blue)).show()
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

    companion object {
        private const val ARGUMENT_KEY = "TodoID"
        const val DATE_YEAR_OFFSET = 1900

        fun createNewInstance(id: String): EditTodoFragment {
            val bundle = Bundle()
            val fragmentEdit = EditTodoFragment()
            bundle.putString(ARGUMENT_KEY, id)
            fragmentEdit.arguments = bundle
            return fragmentEdit
        }
    }

}
