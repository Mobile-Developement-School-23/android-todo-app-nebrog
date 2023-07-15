package com.example.todoapp.presentation.compose

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapp.R

@Composable
fun TodoDeleteButton(onDeleteClick: (() -> Unit)?) {
    Button(
        enabled = onDeleteClick != null,
        contentPadding = PaddingValues(0.dp),
        onClick = { onDeleteClick?.invoke() },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
    ) {
        val deleteColor = if (onDeleteClick != null) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
        }
        Icon(
            Icons.Filled.Delete,
            contentDescription = "Delete",
            modifier = Modifier.size(ButtonDefaults.IconSize),
            tint = deleteColor
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(
            stringResource(id = R.string.delete),
            color = deleteColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewTodoDeleteButtonLight() {
    TodoTheme {
        TodoDeleteButton(
            onDeleteClick = null,
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTodoDeleteButtonDark() {
    TodoTheme {
        TodoDeleteButton(
            onDeleteClick = {},
        )
    }
}
