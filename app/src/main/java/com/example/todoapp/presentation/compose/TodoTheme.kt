package com.example.todoapp.presentation.compose

import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.google.accompanist.themeadapter.material3.Mdc3Theme

@Composable
fun TodoTheme(content: @Composable () -> Unit) {
    Mdc3Theme {
        CompositionLocalProvider(
            LocalRippleTheme provides RippleTodoTheme,
            content = content
        )
    }
}

object RippleTodoTheme : RippleTheme {
    @Composable
    override fun defaultColor() = MaterialTheme.colorScheme.primary

    @Composable
    override fun rippleAlpha() = RippleAlpha(
        pressedAlpha = 0.32f,
        focusedAlpha = 0.24f,
        draggedAlpha = 0.16f,
        hoveredAlpha = 0.24f,
    )
}