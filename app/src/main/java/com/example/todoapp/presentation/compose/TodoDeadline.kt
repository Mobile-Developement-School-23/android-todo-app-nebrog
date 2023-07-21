@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todoapp.presentation.compose

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.R
import com.example.todoapp.utils.noRippleClickable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ColumnScope.TodoDeadline(
    deadline: Date?,
    onDeadlineChanged: (Date?) -> Unit,
) {
    val datePickerState = rememberDatePickerState(deadline)
    val timePickerState = rememberTimePickerState(deadline)
    val isDateShown = remember { mutableStateOf(false) }
    val isTimeShown = remember { mutableStateOf(false) }
    val formatter = remember {
        SimpleDateFormat("d MMMM yyyy, HH:mm", Locale.forLanguageTag("RU"))
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.deadline),
            style = MaterialTheme.typography.bodyLarge,
        )
        Switch(
            checked = deadline != null,
            onCheckedChange = {
                if (deadline == null) {
                    isDateShown.value = true
                } else {
                    onDeadlineChanged(null)
                }
            },
        )
    }
    Text(
        text = if (deadline == null) "" else formatter.format(deadline),
        color = MaterialTheme.colorScheme.primary,
        fontSize = 16.sp,
        modifier = Modifier.noRippleClickable {
            if (deadline != null) {
                isDateShown.value = true
            }
        }
    )
    Divider(modifier = Modifier.padding(top = 16.dp))

    if (isDateShown.value) {
        DatePickerDialog(
            state = datePickerState,
            onSuccess = {
                isDateShown.value = false
                isTimeShown.value = true
            },
            onCancel = { isDateShown.value = false }
        )
    }

    if (isTimeShown.value) {
        TimePickerDialog(
            state = timePickerState,
            onSuccess = {
                isTimeShown.value = false
                val newDeadline = calculateDeadline(datePickerState, timePickerState)
                onDeadlineChanged(newDeadline)
            },
            onCancel = { isTimeShown.value = false }
        )
    }
}

@Composable
private fun DatePickerDialog(
    state: DatePickerState,
    onSuccess: () -> Unit,
    onCancel: () -> Unit,
) {
    DatePickerDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(
                onClick = onSuccess,
                content = { Text(text = stringResource(R.string.ok)) },
            )
        },
        dismissButton = {
            TextButton(
                onClick = onCancel,
                content = { Text(text = stringResource(R.string.cancel)) },
            )
        },
        content = { DatePicker(state = state, showModeToggle = false) },
    )
}

@Composable
private fun TimePickerDialog(
    state: TimePickerState,
    onSuccess: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        content = {
            Surface(
                shape = RoundedCornerShape(36.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    TimePicker(
                        state = state,
                        colors = TimePickerDefaults.colors(),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(1f),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(
                            onClick = onCancel,
                            content = { Text(text = stringResource(R.string.cancel)) },
                        )

                        TextButton(
                            onClick = onSuccess,
                            content = { Text(text = stringResource(R.string.ok)) },
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun rememberDatePickerState(date: Date?): DatePickerState {
    val calendar = Calendar.getInstance()
    if (date != null) {
        calendar.timeInMillis = date.time
    }
    return rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)
}

@Composable
fun rememberTimePickerState(date: Date?): TimePickerState {
    val calendar = Calendar.getInstance()
    if (date != null) {
        calendar.timeInMillis = date.time
    } else {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
    }
    val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
    val initialMinute = calendar.get(Calendar.MINUTE)
    return rememberTimePickerState(initialHour, initialMinute)
}

private fun calculateDeadline(date: DatePickerState, time: TimePickerState): Date? {
    val timestamp = date.selectedDateMillis ?: return null
    val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
    calendar.timeInMillis = timestamp
    calendar.set(Calendar.HOUR_OF_DAY, time.hour)
    calendar.set(Calendar.MINUTE, time.minute)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewDatePickerLight() {
    TodoTheme {
        DatePickerDialog(rememberDatePickerState(), {}, {})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewDatePickerDark() {
    TodoTheme {
        DatePickerDialog(rememberDatePickerState(), {}, {})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewTimePickerLight() {
    TodoTheme {
        TimePickerDialog(rememberTimePickerState(), {}, {})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTimePickerDark() {
    TodoTheme {
        TimePickerDialog(rememberTimePickerState(), {}, {})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewTodoDeadlineLight() {
    TodoTheme {
        Column {
            TodoDeadline(Calendar.getInstance().time, {})
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTodoDeadlineDark() {
    TodoTheme {
        Column {
            TodoDeadline(null, {})
        }
    }
}
