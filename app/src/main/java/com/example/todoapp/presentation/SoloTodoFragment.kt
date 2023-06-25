package com.example.todoapp.presentation

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
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

    protected var deadline: Date? = null
        set(value) {
            internalSetDeadline(value)
            field = value
        }

    protected var priority = TodoItem.Priority.NORMAL
        set(value) {
            internalSetPriority(value)
            field = value
        }

    protected open fun setUpUI(view: View, savedInstanceState: Bundle?) {

        binding.cancelButton.setOnClickListener {
            parentFragmentManager.popBackStackImmediate()
        }
        binding.switchButton.tag = false
        binding.switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (binding.switchButton.tag != true) {
                if (isChecked) {
                    datePicker()
                } else {
                    deadline = null
                }
            } else {
                binding.switchButton.tag = false
            }

        }
        binding.deadlineDate.setOnClickListener {
            datePicker()
        }

        binding.changePriority.setOnClickListener {
            val popupMenu = PopupMenu(it.context, binding.changePriority)
            popupMenu.menuInflater.inflate(R.menu.priority_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                priority = when (item.itemId) {
                    R.id.action_low -> TodoItem.Priority.LOW
                    R.id.action_normal -> TodoItem.Priority.NORMAL
                    R.id.action_high -> TodoItem.Priority.HIGH
                    else -> error("Unexpected ID")
                }
                true
            }
            popupMenu.show()
        }
    }

    private fun datePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, mYear, mMonth, mDay ->
                deadline = Date(mYear - 1900, mMonth, mDay)
            }, year, month, day
        )

        datePickerDialog.setButton(
            DialogInterface.BUTTON_NEGATIVE,
            getString(R.string.cancel)
        ) { _, _ ->
            binding.switchButton.isChecked = false
        }

        datePickerDialog.show()
    }

    private fun internalSetDeadline(deadline: Date?) {
        if (deadline == null) {
            binding.deadlineDate.text = ""
            binding.switchButton.isChecked = false
        } else {
            val formatter = SimpleDateFormat("d MMMM yyyy", Locale.forLanguageTag("RU"))
            val mDate = formatter.format(deadline)
            binding.deadlineDate.text = mDate
            binding.switchButton.tag = true
            binding.switchButton.isChecked = true
        }

    }


    private fun internalSetPriority(priority: TodoItem.Priority) {
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