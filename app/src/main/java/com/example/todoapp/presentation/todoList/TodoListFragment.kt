package com.example.todoapp.presentation.todoList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmetTodolistBinding
import com.example.todoapp.presentation.addTodo.AddTodoFragment
import com.example.todoapp.presentation.editTodo.EditTodoFragment
import com.example.todoapp.presentation.todoList.TodoListViewModel.Actions
import com.example.todoapp.presentation.todoList.TodoListViewModel.State
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class TodoListFragment : Fragment(), Callback {

    private var _binding: FragmetTodolistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TodoListViewModel by viewModels()
    private val todoAdapter = TodoAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmetTodolistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
        setUpCollects(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickCheckBox(id: String, isDone: Boolean) {
        viewModel.onDoneClick(id, isDone)
    }

    override fun onClickText(id: String) {
        val editTodo = EditTodoFragment.createNewInstance(id)
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_view, editTodo)
            .addToBackStack(null)
            .commit()
    }

    private fun setUpCollects(view: View) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.states.collect { state ->
                        when (state) {
                            is State.Success -> showSuccessState(state)
                            is State.Loading -> showLoadingState()
                        }
                    }
                }
                launch {
                    viewModel.actions.collect { action ->
                        when (action) {
                            is Actions.Error -> showErrorAction(action, view)
                        }
                    }
                }
            }
        }
    }

    private fun showLoadingState() {
        binding.progressTodolist.visibility = View.VISIBLE
    }

    private fun showErrorAction(state: Actions.Error, view: View) {
        Snackbar.make(
            view, state.messageID,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun showSuccessState(state: State.Success) {
        binding.progressTodolist.visibility = View.GONE
        binding.addButton.visibility = View.VISIBLE
        todoAdapter.setListTodos(state.items)
        if (state.isHidden) {
            binding.toolbarIcon.setImageResource(R.drawable.visible)
        } else {
            binding.toolbarIcon.setImageResource(R.drawable.visible_off)
        }
        binding.toolbarSubtitle.text = getString(R.string.toolbar_subtitle, state.doneCount)

    }

    private fun setUpUI() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.toolbarIcon.setOnClickListener {
            viewModel.onHideClick()
        }
        binding.recyclerView.adapter = todoAdapter
        binding.recyclerView.layoutManager = layoutManager

        binding.addButton.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_view, AddTodoFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}