package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val HouseColorScheme = darkColorScheme(
    primary = Gold,
    secondary = SecondaryText,
    background = BackgroundColor,
    surface = CardColor,
    surfaceVariant = DarkBorder,
    onPrimary = BackgroundColor,
    onBackground = TextColor,
    onSurface = TextColor,
    onSurfaceVariant = TextColor
)

@Composable
fun TheHouseTheme(
    primaryColor: Color = Gold,
    backgroundColor: Color = BackgroundColor,
    surfaceColor: Color = CardColor,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val customColorScheme = darkColorScheme(
        primary = primaryColor,
        secondary = SecondaryText,
        background = backgroundColor,
        surface = surfaceColor,
        surfaceVariant = DarkBorder,
        onPrimary = backgroundColor,
        onBackground = TextColor,
        onSurface = TextColor,
        onSurfaceVariant = TextColor
    )

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = backgroundColor.toArgb()
            window.navigationBarColor = backgroundColor.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = customColorScheme,
        typography = Typography,
        content = content
    )
}
