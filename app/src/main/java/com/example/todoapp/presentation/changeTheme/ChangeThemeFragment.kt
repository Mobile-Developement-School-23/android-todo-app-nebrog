package com.example.todoapp.presentation.changeTheme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.fragment.app.Fragment
import com.example.todoapp.databinding.ChangeThemeFragmentBinding

class ChangeThemeFragment : Fragment(){

    private var _binding: ChangeThemeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ChangeThemeFragmentBinding.inflate(inflater, container, false)
        binding.darkThemeButton.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode( MODE_NIGHT_YES)
        }
        binding.lightThemeButton.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode( MODE_NIGHT_NO)
        }
        binding.systemThemeButton.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode( MODE_NIGHT_FOLLOW_SYSTEM)
        }
        return binding.root
    }
}
