package com.sedilant.yambol.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun platformDynamicColorScheme(darkTheme: Boolean): ColorScheme? {
    // iOS does not support dynamic color schemes
    return null
}
