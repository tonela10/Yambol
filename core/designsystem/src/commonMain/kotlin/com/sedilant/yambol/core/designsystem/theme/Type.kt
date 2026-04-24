package com.sedilant.yambol.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font
import com.sedilant.yambol.core.designsystem.Res
import com.sedilant.yambol.core.designsystem.adlam_display_regular
import com.sedilant.yambol.core.designsystem.anuphan_regular

@Composable
fun AnuphanFontFamily(): FontFamily = FontFamily(Font(Res.font.anuphan_regular))

@Composable
fun AdlamDisplayFontFamily(): FontFamily = FontFamily(Font(Res.font.adlam_display_regular))

// Default Material 3 typography values
val baseline = Typography()

@Composable
fun YambolTypography(): Typography {
    val displayFontFamily = AdlamDisplayFontFamily()
    val bodyFontFamily = AnuphanFontFamily()

    return Typography(
        displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
        displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
        displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
        titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
        titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
        titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
        bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
        bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
        labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
        labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
        labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
    )
}
