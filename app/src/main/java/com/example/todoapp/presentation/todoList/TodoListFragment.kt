package com.example.todoapp.presentation.todoList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmetTodolistBinding
import com.example.todoapp.presentation.addTodo.AddTodoFragment
import com.example.todoapp.presentation.connectivity.NetworkChangeListener
import com.example.todoapp.presentation.editTodo.EditTodoFragment
import com.example.todoapp.presentation.todoList.TodoListViewModel.Actions
import com.example.todoapp.presentation.todoList.TodoListViewModel.State
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class TodoListFragment : Fragment(), Callback {

    private var _binding: FragmetTodolistBinding? = null
    private val binding get() = _binding!!
    private val networkListener = NetworkChangeListener { isConnected ->
        viewModel.onOnlineChanged(isConnected)
    }

    private val viewModel: TodoListViewModel by viewModels()
    private val todoAdapter = TodoAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmetTodolistBinding.inflate(inflater, container, false)
        NetworkChangeListener.register(requireContext(), networkListener)
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
        NetworkChangeListener.unregister(requireContext(), networkListener)
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
                        binding.errorTodolist.root.isVisible = state is State.NoNetwork
                        binding.todolistLayout.root.isVisible = state is State.Success
                        binding.progressTodolist.root.isVisible = state is State.Loading
                        when (state) {
                            is State.Success -> showSuccessState(state)
                            is State.Loading -> showLoadingState()
                            State.NoNetwork -> showNoNetworkState()
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

    private fun showErrorAction(state: Actions.Error, view: View) {
        Snackbar.make(
            view, state.messageID,
            Snackbar.LENGTH_LONG
        ).setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.color_light_blue)).show()
    }

    private fun showLoadingState() {
        //No action required
    }

    private fun showNoNetworkState() {
        //No action required
    }

    private fun showSuccessState(state: State.Success) {
        binding.todolistLayout.addButton.visibility = View.VISIBLE
        todoAdapter.setListTodos(state.items)
        if (state.isHidden) {
            binding.todolistLayout.toolbarIcon.setImageResource(R.drawable.visible)
        } else {
            binding.todolistLayout.toolbarIcon.setImageResource(R.drawable.visible_off)
        }
        binding.todolistLayout.toolbarSubtitle.text = getString(R.string.toolbar_subtitle, state.doneCount)

    }

    private fun setUpUI() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.todolistLayout.recyclerView.adapter = todoAdapter
        binding.todolistLayout.recyclerView.layoutManager = layoutManager

        binding.todolistLayout.toolbarIcon.setOnClickListener {
            viewModel.onHideClick()
        }
        binding.errorTodolist.retryButton.setOnClickListener {
            viewModel.onRetryClick()
        }
        binding.todolistLayout.addButton.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container_view, AddTodoFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}