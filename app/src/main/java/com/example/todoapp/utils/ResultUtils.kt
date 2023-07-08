package com.example.todoapp.utils

import com.example.todoapp.domain.TodoRepository

/**
 * Утильные методы для работы с [TodoRepository.Result]
 */
inline fun <R> TodoRepository.Result<R>.onSuccess(action: (R) -> Unit): TodoRepository.Result<R> {
    if (this is TodoRepository.Result.Success) {
        action(value)
    }
    return this
}

inline fun <R> TodoRepository.Result<R>.onFailure(action: () -> Unit): TodoRepository.Result<R> {
    if (this is TodoRepository.Result.Failure) {
        action()
    }
    return this
}

inline fun <R> TodoRepository.Result<R>.getOr(fallback: (TodoRepository.Result.Failure) -> R): R {
    return when (this) {
        is TodoRepository.Result.Failure -> fallback(this)
        is TodoRepository.Result.Success -> value
    }
}
