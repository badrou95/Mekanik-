package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Mekanik+ High-End Graphite & Metal Automotive Theme
private val MekanikColorScheme = darkColorScheme(
  primary = PerformanceRed,
  onPrimary = PureWhite,
  primaryContainer = PerformanceRedDark,
  onPrimaryContainer = PureWhite,
  secondary = MechanicalOrange,
  onSecondary = GraphiteDark,
  secondaryContainer = MetalGray,
  onSecondaryContainer = OffWhite,
  tertiary = AluminumGray,
  onTertiary = GraphiteBlack,
  background = GraphiteDark,
  onBackground = OffWhite,
  surface = MetalSurface,
  onSurface = OffWhite,
  surfaceVariant = MetalCard,
  onSurfaceVariant = AluminumLight,
  outline = MetalBorder,
  outlineVariant = AluminumMuted
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Mekanik+ is a sleek automotive dark dashboard platform by default
  content: @Composable () -> Unit
) {
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = GraphiteDark.toArgb()
        window.navigationBarColor = GraphiteDark.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = MekanikColorScheme,
    typography = Typography,
    content = content
  )
}
