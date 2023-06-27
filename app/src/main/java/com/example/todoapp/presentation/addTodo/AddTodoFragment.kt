package com.example.todoapp.presentation.addTodo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.todoapp.domain.TodoItem
import com.example.todoapp.presentation.SoloTodoFragment
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class AddTodoFragment : SoloTodoFragment() {

    private val viewModel: AddTodoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI(view, savedInstanceState)
    }

    override fun setUpUI(view: View, savedInstanceState: Bundle?) {
        super.setUpUI(view, savedInstanceState)
        val id = UUID.randomUUID().toString()
        if (savedInstanceState != null) {
            val enumName = savedInstanceState.getString(BUNDLE_KEY_PRIORITY)
            priority = TodoItem.Priority.valueOf(enumName!!)
            val dateLong = savedInstanceState.getLong(BUNDLE_KEY_DEADLINE, -1)
            if (dateLong == -1L) {
                deadline = null
            } else {
                deadline = Date(dateLong)
            }
        }
        binding.saveButton.setOnClickListener {
            lifecycleScope.launch {
                viewModel.onTodoSave(
                    id.toString(), binding.todoEdit.text.toString(), priority, deadline
                )
            }
            parentFragmentManager.popBackStackImmediate()
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
        private const val BUNDLE_KEY_DEADLINE = "deadline"
        private const val BUNDLE_KEY_PRIORITY = "priority"

    }
}