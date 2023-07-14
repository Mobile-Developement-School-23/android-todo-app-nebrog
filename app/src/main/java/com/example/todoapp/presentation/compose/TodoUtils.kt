package com.example.todoapp.presentation.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.example.todoapp.R
import com.example.todoapp.domain.TodoItem

@Composable
@ReadOnlyComposable
fun TodoItem.Priority.asText(): String {
    return when (this) {
        TodoItem.Priority.LOW -> stringResource(id = R.string.low_priority)
        TodoItem.Priority.NORMAL -> stringResource(id = R.string.normal_priority)
        TodoItem.Priority.HIGH -> stringResource(id = R.string.high_priority)
    }
}

fun textFieldValueSaver() = Saver<MutableState<TextFieldValue>, String>(
    save = { it.value.text },
    restore = { mutableStateOf(TextFieldValue(it)) }
)
