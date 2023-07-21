package com.example.todoapp.utils

import androidx.fragment.app.FragmentTransaction
import com.example.todoapp.R

/**
 * Добавляет кастомную анимацию переходов между экранами.
 */
fun FragmentTransaction.addTodoAnimation(): FragmentTransaction {
    return setCustomAnimations(
        /* enter = */ R.anim.enter_from_right,
        /* exit = */ R.anim.exit_to_left,
        /* popEnter = */ R.anim.enter_from_left,
        /* popExit = */ R.anim.exit_to_right
    )
}