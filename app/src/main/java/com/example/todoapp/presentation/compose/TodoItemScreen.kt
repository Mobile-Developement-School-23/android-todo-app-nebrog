@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todoapp.presentation.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.R
import com.example.todoapp.domain.TodoItem
import com.example.todoapp.utils.getRandomTodo
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DetailTodoItem(
    todo: TodoItem,
    onSaveClick: (String) -> Unit,
    onCloseClick: () -> Unit,
    onDeleteClick: (() -> Unit)?,
    onPriorityChanged: (TodoItem.Priority) -> Unit,
    onCheckChanged: (Boolean) -> Unit,
) {
    val text = rememberSaveable(saver = textFieldValueSaver()) {
        mutableStateOf(TextFieldValue(todo.itemText))
    }
    val formatter = remember { SimpleDateFormat("d MMMM yyyy", Locale.forLanguageTag("RU")) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onCloseClick) {
                        Icon(Icons.Filled.Close, "backIcon")
                    }
                },
                actions = {
                    Button(
                        contentPadding = PaddingValues(16.dp),
                        onClick = { onSaveClick(text.value.text) },
                        colors = ButtonDefaults.buttonColors(Color.Transparent),
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                            color = colorResource(R.color.color_light_blue),
                            fontSize = 14.sp,
                        )
                    }
                },
                colors = topAppBarColors(
                    containerColor = colorResource(R.color.back_light_primary),
                ),
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .background(colorResource(R.color.back_light_primary))
                    .padding(it)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                OutlinedTextField(
                    value = text.value,
                    modifier = Modifier
                        .wrapContentHeight()
                        .defaultMinSize(minHeight = 120.dp)
                        .fillMaxWidth(),
                    onValueChange = { text.value = it },
                    textStyle = TextStyle(fontSize = 16.sp),
                    shape = MaterialTheme.shapes.small,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.new_todo),
                            color = Color.Gray
                        )
                    }
                )
                Text(
                    text = stringResource(id = R.string.priority),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 40.dp)
                )
                PriorityDropDown(todo.itemPriority, onPriorityChanged)
                Divider(
                    modifier = Modifier.padding(top = 16.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.deadline),
                        color = Color.Black,
                        fontSize = 16.sp,
                    )
                    Switch(
                        checked = todo.deadline != null,
                        onCheckedChange = onCheckChanged,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colorResource(id = R.color.white),
                            checkedTrackColor = colorResource(id = R.color.color_light_blue),
                            uncheckedThumbColor = colorResource(id = R.color.color_light_blue),
                            uncheckedTrackColor = colorResource(id = R.color.back_light_primary),
                        ),
                    )
                }
                Text(
                    text = if (todo.deadline == null) "" else formatter.format(todo.deadline),
                    color = colorResource(R.color.color_light_blue),
                    fontSize = 16.sp,
                )
                Divider(modifier = Modifier.padding(top = 16.dp))
                Button(
                    contentPadding = PaddingValues(0.dp),
                    onClick = { onDeleteClick?.invoke() },
                    colors = ButtonDefaults.buttonColors(Color.Transparent),
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        tint = if (onDeleteClick != null) Color.Red else Color.Gray
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(stringResource(id = R.string.delete), color = if (onDeleteClick != null) Color.Red else Color.Gray, fontSize = 16.sp)
                }
            }
        }
    )
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun PriorityDropDown(priority: TodoItem.Priority, onPriorityChanged: (TodoItem.Priority) -> Unit) {
    val expanded = remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = !expanded.value },
    ) {
        OutlinedTextField(
            value = priority.asText(),
            onValueChange = {},
            textStyle = TextStyle(fontSize = 14.sp),
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
            modifier = Modifier
                .padding(top = 6.dp)
                .menuAnchor()
                .width(140.dp)

        )

        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            val allPriorities = TodoItem.Priority.values()
            for (itemPriority in allPriorities) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = itemPriority.asText(),
                            fontWeight = if (itemPriority == priority) FontWeight.Bold else null,
                            color = if (itemPriority == TodoItem.Priority.HIGH) Color.Red else Color.Black
                        )
                    },
                    onClick = {
                        onPriorityChanged(itemPriority)
                        expanded.value = false
                    }
                )
            }
        }
    }
}

@ReadOnlyComposable
@Composable
fun TodoItem.Priority.asText(): String {
    return when (this) {
        TodoItem.Priority.LOW -> stringResource(id = R.string.low_priority)
        TodoItem.Priority.NORMAL -> stringResource(id = R.string.normal_priority)
        TodoItem.Priority.HIGH -> stringResource(id = R.string.high_priority)
    }
}

@Preview
@Composable
fun PreviewDetailTodoItem() {
    DetailTodoItem(
        todo = getRandomTodo(),
        onSaveClick = {},
        onCloseClick = {},
        onDeleteClick = null,
        onPriorityChanged = {},
        onCheckChanged = {}
    )
}

private fun textFieldValueSaver() = Saver<MutableState<TextFieldValue>, String>(
    save = { it.value.text },
    restore = { mutableStateOf(TextFieldValue(it)) }
)
