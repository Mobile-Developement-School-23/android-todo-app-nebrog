package com.example.todoapp.presentation.compose

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.R

@Composable
fun TodoTextField(text: TextFieldValue, onTextChanged: (TextFieldValue) -> Unit) {
    OutlinedTextField(
        value = text,
        modifier = Modifier
            .wrapContentHeight()
            .defaultMinSize(minHeight = 120.dp)
            .fillMaxWidth(),
        onValueChange = onTextChanged,
        textStyle = TextStyle(fontSize = 16.sp),
        shape = MaterialTheme.shapes.small,
        placeholder = {
            Text(
                text = stringResource(id = R.string.new_todo),
            )
        }
    )
}


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
fun PreviewLightTodoTextFieldLong() {
    TodoTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) {
            TodoTextField(
                text = TextFieldValue("lorem ispum ololo ".repeat(100)),
                onTextChanged = {},
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun PreviewDarkTodoTextFieldShort() {
    TodoTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) {
            TodoTextField(text = TextFieldValue("lorem ispum ololo"), onTextChanged = {})
        }
    }
}
