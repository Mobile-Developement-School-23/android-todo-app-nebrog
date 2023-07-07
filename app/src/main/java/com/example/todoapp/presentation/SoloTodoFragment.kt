package com.example.todoapp.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmetAddTodoBinding
import com.example.todoapp.domain.TodoItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

abstract class SoloTodoFragment : Fragment(R.layout.fragmet_add_todo) {

    private var _binding: FragmetAddTodoBinding? = null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmetAddTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun internalSetDeadline(deadline: Date?) {
        if (deadline == null) {
            binding.deadlineDate.text = ""
            binding.switchButton.isChecked = false
        } else {
            val formatter = SimpleDateFormat("d MMMM yyyy", Locale.forLanguageTag("RU"))
            val mDate = formatter.format(deadline)
            binding.deadlineDate.text = mDate
            binding.switchButton.isChecked = true
        }

    }

    protected fun internalSetPriority(priority: TodoItem.Priority) {
        when (priority) {
            TodoItem.Priority.LOW -> {
                binding.changePriority.setText(R.string.low_priority)
                binding.changePriority.setTextColor(ContextCompat.getColor(requireContext(), R.color.label_light_tertiary))
            }

            TodoItem.Priority.NORMAL -> {
                binding.changePriority.setText(R.string.normal_priority)
                binding.changePriority.setTextColor(ContextCompat.getColor(requireContext(), R.color.label_light_tertiary))
            }

            TodoItem.Priority.HIGH -> {
                binding.changePriority.setText(R.string.high_priority)
                binding.changePriority.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_dark_red))
            }
        }
    }
}
