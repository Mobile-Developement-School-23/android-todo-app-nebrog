package com.example.todoapp.presentation.compose

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import com.example.todoapp.domain.TodoItem
import com.example.todoapp.utils.getRandomTodo
import java.util.Date

@Composable
fun DetailTodoItem(
    todo: TodoItem,
    onSaveClick: (String) -> Unit,
    onCloseClick: () -> Unit,
    onDeleteClick: (() -> Unit)?,
    onPriorityChanged: (TodoItem.Priority) -> Unit,
    onDeadlineChanged: (Date?) -> Unit,
) {
    val text = rememberSaveable(saver = textFieldValueSaver()) {
        mutableStateOf(TextFieldValue(todo.itemText))
    }
    Scaffold(
        topBar = {
            TodoTopBar(
                onSaveClick = { onSaveClick(text.value.text) },
                onCloseClick = onCloseClick,
            )
        },
        content = {
            TodoContent(
                todo = todo,
                text = text,
                padding = it,
                onDeleteClick = onDeleteClick,
                onPriorityChanged = onPriorityChanged,
                onDeadlineChanged = onDeadlineChanged,
            )
        }
    )
}

@Composable
private fun TodoContent(
    todo: TodoItem,
    text: MutableState<TextFieldValue>,
    padding: PaddingValues,
    onDeleteClick: (() -> Unit)?,
    onPriorityChanged: (TodoItem.Priority) -> Unit,
    onDeadlineChanged: (Date?) -> Unit,
) {
    Column(
        modifier = Modifier
            .background(colorResource(R.color.back_primary))
            .padding(padding)
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        TodoTextField(text.value, onTextChanged = { text.value = it })
        TodoPriority(todo.itemPriority, onPriorityChanged)
        TodoDeadline(todo.deadline, onDeadlineChanged)
        TodoDeleteButton(onDeleteClick)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewDetailTodoItemLight() {
    TodoTheme {
        DetailTodoItem(
            todo = getRandomTodo(),
            onSaveClick = {},
            onCloseClick = {},
            onDeleteClick = null,
            onPriorityChanged = {},
            onDeadlineChanged = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewDetailTodoItemDark() {
    TodoTheme {
        DetailTodoItem(
            todo = getRandomTodo(),
            onSaveClick = {},
            onCloseClick = {},
            onDeleteClick = {},
            onPriorityChanged = {},
            onDeadlineChanged = {}
        )
    }
}
