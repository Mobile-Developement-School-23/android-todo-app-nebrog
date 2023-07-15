package com.example.todoapp.presentation.compose

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodoDeadline(
    deadline: Date?,
    onCheckChanged: (Boolean) -> Unit,
) {
    val formatter = remember {
        SimpleDateFormat("d MMMM yyyy", Locale.forLanguageTag("RU"))
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
            onCheckedChange = onCheckChanged,
        )
    }
    Text(
        text = if (deadline == null) "" else formatter.format(deadline),
        color = MaterialTheme.colorScheme.primary,
        fontSize = 16.sp,
    )
    Divider(modifier = Modifier.padding(top = 16.dp))
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewTodoDeadlineLight() {
    TodoTheme {
        TodoDeadline(null, {})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTodoDeadlineDark() {
    TodoTheme {
        TodoDeadline(null, {})
    }
}
