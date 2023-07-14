@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todoapp.presentation.compose

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.R
import com.example.todoapp.domain.TodoItem

@Composable
fun ColumnScope.TodoPriority(
    priority: TodoItem.Priority,
    onPriorityChanged: (TodoItem.Priority) -> Unit,
) {
    Text(
        text = stringResource(id = R.string.priority),
        fontSize = 16.sp,
        modifier = Modifier.padding(top = 40.dp)
    )
    PriorityDropDown(priority, onPriorityChanged)
    Divider(
        modifier = Modifier.padding(top = 16.dp)
    )
}

@Composable
private fun PriorityDropDown(priority: TodoItem.Priority, onPriorityChanged: (TodoItem.Priority) -> Unit) {
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