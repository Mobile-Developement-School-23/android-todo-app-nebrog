package com.example.todoapp.presentation.editTodo

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.todoapp.R
import com.example.todoapp.presentation.SoloTodoFragment

class EditTodoFragment : SoloTodoFragment() {

    private val viewModel: EditTodoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI(view)
    }

    override fun setUpUI(view: View) {
        super.setUpUI(view)
        val todoID = requireArguments().getString(KEY) as String
        binding.delImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.color_dark_red))
        binding.delButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_dark_red))
        binding.delButton.isEnabled = true
        binding.delButton.setOnClickListener {
            viewModel.onDeleteTodo(todoID, this)
        }
        val todo = viewModel.getTodo(todoID)
        binding.todoEdit.setText(todo.itemText)
        priority = todo.itemPriority
        deadline = todo.deadline

        binding.saveButton.setOnClickListener {
            viewModel.onChangeTodo(todo.itemID, binding.todoEdit.text.toString(), priority, deadline, todo.doneFlag, todo.dateOfCreation, this)
        }
    }

    companion object {
        private const val KEY = "TodoID"

        fun createNewInstance(id: String): EditTodoFragment {
            val bundle = Bundle()
            val fragmentEdit = EditTodoFragment()
            bundle.putString(KEY, id)
            fragmentEdit.arguments = bundle
            return fragmentEdit
        }
    }
}