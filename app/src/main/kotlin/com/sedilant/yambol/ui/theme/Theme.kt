package com.sedilant.yambol.ui.theme

// Backward-compatibility re-export. Will be removed when all screens migrate.
import androidx.compose.runtime.Composable
import com.sedilant.yambol.core.designsystem.theme.YambolTheme as DesignSystemYambolTheme

@Composable
fun YambolTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    DesignSystemYambolTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        content = content
    )
}
