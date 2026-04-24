package com.sedilant.yambol.feature.createTeam

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // No-op on iOS — system back gesture not applicable
}
