@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todoapp.presentation.compose

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.R

@Composable
fun TodoTopBar(
    onSaveClick: () -> Unit,
    onCloseClick: () -> Unit,
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onCloseClick) {
                Icon(
                    Icons.Filled.Close,
                    "backIcon",
                    tint = colorResource(id = R.color.label_primary)
                )
            }
        },
        actions = {
            Button(
                contentPadding = PaddingValues(16.dp),
                onClick = onSaveClick,
                colors = ButtonDefaults.buttonColors(Color.Transparent),
            ) {
                Text(
                    text = stringResource(R.string.save),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.back_primary),
        ),
    )
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_NO)
fun PreviewLightTodoTopBar() {
    TodoTheme {
        TodoTopBar(onSaveClick = {}, onCloseClick = {})
    }
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
fun PreviewDarkTodoTopBar() {
    TodoTheme {
        TodoTopBar(onSaveClick = {}, onCloseClick = {})
    }
}
