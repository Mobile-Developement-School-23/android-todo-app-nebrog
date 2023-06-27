package com.example.todoapp.presentation.editTodo

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.todoapp.R
import com.example.todoapp.domain.TodoItem
import com.example.todoapp.presentation.SoloTodoFragment
import kotlinx.coroutines.launch
import java.util.Date

class EditTodoFragment : SoloTodoFragment() {

    private val viewModel: EditTodoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI(view, savedInstanceState)
    }

    override fun setUpUI(view: View, savedInstanceState: Bundle?) {
        super.setUpUI(view, savedInstanceState)
        val todoID = requireArguments().getString(ARGUMENT_KEY) as String
        binding.delImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.color_dark_red))
        binding.delButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_dark_red))
        binding.delButton.isEnabled = true


        lifecycleScope.launch{
            val todo = viewModel.getTodo(todoID)
            binding.delButton.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.onDeleteTodo(todoID, todo)
                    parentFragmentManager
                        .popBackStackImmediate()
                }
            }
            binding.todoEdit.setText(todo.itemText)
            if (savedInstanceState != null) {
                val enumName = savedInstanceState.getString(BUNDLE_KEY_PRIORITY)
                priority = TodoItem.Priority.valueOf(enumName!!)
                val dateLong = savedInstanceState.getLong(BUNDLE_KEY_DEADLINE, -1)
                if (dateLong == -1L) {
                    deadline = null
                } else {
                    deadline = Date(dateLong)
                }
            } else {
                priority = todo.itemPriority
                deadline = todo.deadline
            }

            binding.saveButton.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.onChangeTodo(
                        todo.itemID,
                        binding.todoEdit.text.toString(),
                        priority,
                        deadline,
                        todo.doneFlag,
                        todo.dateOfCreation
                    )
                    parentFragmentManager
                        .popBackStackImmediate()

                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(BUNDLE_KEY_PRIORITY, priority.name)
        val dateLong = deadline?.time
        if (dateLong != null) {
            outState.putLong(BUNDLE_KEY_DEADLINE, dateLong)
        }
        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val ARGUMENT_KEY = "TodoID"
        private const val BUNDLE_KEY_DEADLINE = "deadline"
        private const val BUNDLE_KEY_PRIORITY = "priority"

        fun createNewInstance(id: String): EditTodoFragment {
            val bundle = Bundle()
            val fragmentEdit = EditTodoFragment()
            bundle.putString(ARGUMENT_KEY, id)
            fragmentEdit.arguments = bundle
            return fragmentEdit
        }
    }
}