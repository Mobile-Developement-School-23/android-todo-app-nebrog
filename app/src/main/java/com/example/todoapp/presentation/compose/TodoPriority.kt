@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todoapp.presentation.compose

import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.R
import com.example.todoapp.domain.TodoItem
import kotlinx.coroutines.launch

@Composable
fun TodoPriority(
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewTodoPriorityLight() {
    TodoTheme {
        TodoPriority(TodoItem.Priority.HIGH,{})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTodoPriorityDark() {
    TodoTheme {
        TodoPriority(TodoItem.Priority.HIGH,{})
    }
}

@Composable
private fun PriorityDropDown(
    priority: TodoItem.Priority,
    onPriorityChanged: (TodoItem.Priority) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    val isAnimated = remember { mutableStateOf(false) }
    val currentFontSizePx = with(LocalDensity.current) {
        MaterialTheme.typography.labelLarge.fontSize.toPx()
    }
    val currentFontSizeDoublePx = currentFontSizePx * 2
    val offset = animateFloatAsState(
        targetValue = if (isAnimated.value) currentFontSizeDoublePx else 0f,
        animationSpec = tween(2_500, easing = LinearEasing),
        finishedListener = { isAnimated.value = false }
    )
    val animatedBrush = Brush.linearGradient(
        0.0f to MaterialTheme.colorScheme.error,
        0.6f to MaterialTheme.colorScheme.onSurface,
        1.0f to MaterialTheme.colorScheme.error,
        start = Offset(offset.value, offset.value),
        end = Offset(offset.value + currentFontSizePx, offset.value + currentFontSizePx),
        tileMode = TileMode.Mirror
    )

    val priorityBrush = when {
        priority == TodoItem.Priority.HIGH && isAnimated.value -> animatedBrush
        priority == TodoItem.Priority.HIGH && !isAnimated.value -> SolidColor(MaterialTheme.colorScheme.error)
        else -> null
    }

    Text(
        text = priority.asText(),
        style = MaterialTheme.typography.titleMedium.copy(brush = priorityBrush),
        modifier = Modifier
            .padding(top = 12.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { expanded.value = true }
            )
    )

    TodoPriorityBottomSheet(expanded) { newPriority ->
        onPriorityChanged(newPriority)
        if (newPriority == TodoItem.Priority.HIGH) {
            isAnimated.value = true
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewPriorityDropDownLight() {
    TodoTheme {
        PriorityDropDown(TodoItem.Priority.LOW,{})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewPriorityDropDownDark() {
    TodoTheme {
        PriorityDropDown(TodoItem.Priority.NORMAL,{})
    }
}

@Composable
private fun TodoPriorityBottomSheet(
    showBottomSheet: MutableState<Boolean>,
    onPriorityChanged: (TodoItem.Priority) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet.value = false },
            sheetState = sheetState
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.priority),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )
            Column(modifier = Modifier.padding(24.dp)) {
                val allPriorities = TodoItem.Priority.values()
                for ((index, itemPriority) in allPriorities.withIndex()) {
                    val shape = when (index) {
                        0 -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        allPriorities.lastIndex -> RoundedCornerShape(
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                        else -> RectangleShape
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) showBottomSheet.value = false
                            }
                            onPriorityChanged(itemPriority)
                        },
                        shape = shape,
                        content = {
                            Text(
                                text = itemPriority.asText(),
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                            )
                        },
                    )
                    if (index != allPriorities.lastIndex) {
                        Divider(color = MaterialTheme.colorScheme.surface)
                    }
                }
            }
        }
    }
}
