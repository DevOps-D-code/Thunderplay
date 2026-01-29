package com.thunderplay.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ThunderplayColorScheme = darkColorScheme(
    primary = Violet600,
    onPrimary = TextPrimary,
    primaryContainer = Violet800,
    onPrimaryContainer = Violet100,
    
    secondary = Cyan500,
    onSecondary = DeepSpace,
    secondaryContainer = Cyan700,
    onSecondaryContainer = Cyan100,
    
    tertiary = Pink400,
    onTertiary = DeepSpace,
    tertiaryContainer = Pink500,
    onTertiaryContainer = Pink300,
    
    background = DeepSpace,
    onBackground = TextPrimary,
    
    surface = SpaceGray100,
    onSurface = TextPrimary,
    surfaceVariant = SpaceGray200,
    onSurfaceVariant = TextSecondary,
    
    outline = GlassBorder,
    outlineVariant = GlassHighlight,
    
    error = Color(0xFFEF4444),
    onError = TextPrimary
)

@Composable
fun ThunderplayTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = ThunderplayColorScheme,
        typography = Typography,
        content = content
    )
}

// Extension for Glassmorphism colors
object GlassColors {
    val surface = GlassWhite
    val border = GlassBorder
    val highlight = GlassHighlight
}
