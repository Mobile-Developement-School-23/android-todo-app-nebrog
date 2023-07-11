package com.example.todoapp.presentation.editTodo

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.todoapp.App
import com.example.todoapp.R
import com.example.todoapp.domain.TodoItem
import com.example.todoapp.presentation.SoloTodoFragment
import com.example.todoapp.presentation.editTodo.EditTodoViewModel.Actions
import com.example.todoapp.presentation.editTodo.EditTodoViewModel.State.Loading
import com.example.todoapp.presentation.editTodo.EditTodoViewModel.State.Success
import com.example.todoapp.presentation.viewmodel.vladViewModels
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Date

/**
 * UI класс, который отвечает за редактирование элемента в списке.
 */
class EditTodoFragment : SoloTodoFragment() {

    private val viewModel by vladViewModels<EditTodoViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appComponent = (requireContext().applicationContext as App).appComponent
        appComponent.getEditTodoFragmentComponentFactory().create()
        if (savedInstanceState == null) {
            val todoID = requireArguments().getString(ARGUMENT_KEY) as String
            viewModel.init(todoID)
        }
        setUpCollects(view.context, view)
        setUpUI()
    }

    private fun setUpCollects(context: Context, view: View) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.states.collect { state ->
                        when (state) {
                            is Success -> showSuccessState(state)
                            is Loading -> showLoadingState()
                        }
                    }
                }
                launch {
                    viewModel.actions.collect { action ->
                        when (action) {
                            Actions.Exit -> parentFragmentManager.popBackStackImmediate()
                            is Actions.Error -> showErrorAction(action, view)
                            Actions.CalendarPicker -> showCalendarPicker(context)
                            is Actions.SetText -> setInitialText(action.text)
                        }
                    }
                }
            }
        }
    }

    private fun setUpUI() {
        binding.delImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.color_dark_red))
        binding.delButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_dark_red))
        binding.delButton.isEnabled = true

        binding.delButton.setOnClickListener {
            viewModel.onDeleteTodo()
        }
        binding.saveButton.setOnClickListener {
            viewModel.onSaveClick(binding.todoEdit.text.toString())
        }
        binding.cancelButton.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }
        binding.switchButton.setOnCheckedChangeListener { view, isChecked ->
            if (view.isPressed) viewModel.onCheckedChanged(isChecked)
        }
        binding.deadlineDate.setOnClickListener {
            viewModel.onDeadlineClick()
        }
        binding.changePriority.setOnClickListener {
            val popupMenu = PopupMenu(it.context, binding.changePriority)
            popupMenu.menuInflater.inflate(R.menu.priority_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                val priority = when (item.itemId) {
                    R.id.action_low -> TodoItem.Priority.LOW
                    R.id.action_normal -> TodoItem.Priority.NORMAL
                    R.id.action_high -> TodoItem.Priority.HIGH
                    else -> error("Unexpected ID")
                }
                viewModel.onPriorityChanged(priority)
                true
            }
            popupMenu.show()
        }
    }

    private fun showLoadingState() {
        binding.progressAddEdit.visibility = View.VISIBLE
    }

    private fun showErrorAction(state: Actions.Error, view: View) {
        Snackbar.make(view, state.messageID, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.color_light_blue)).show()
    }

    private fun showSuccessState(state: Success) {
        binding.progressAddEdit.visibility = View.GONE

        internalSetDeadline(state.item.deadline)
        internalSetPriority(state.item.itemPriority)
    }

    private fun setInitialText(text: String) {
        binding.todoEdit.setText(text)
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
        datePickerDialog.show()
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
