package com.example.todoapp.presentation.addTodo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.todoapp.presentation.SoloTodoFragment

class AddTodoFragment : SoloTodoFragment() {

    private val viewModel: AddTodoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI(view)
    }

    override fun setUpUI(view: View) {
        super.setUpUI(view)
        binding.saveButton.setOnClickListener {
            viewModel.onTodoSave(binding.todoEdit.text.toString(), priority, deadline, this)
        }
    }
}