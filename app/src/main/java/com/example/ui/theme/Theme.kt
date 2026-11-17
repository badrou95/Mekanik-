package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val context = LocalContext.current
  val systemDark = isSystemInDarkTheme()

  LaunchedEffect(Unit) {
    MekanikThemeController.loadSavedThemeMode(context)
  }
  MekanikThemeController.updateSystemDarkState(systemDark)

  val selectedMode = MekanikThemeController.selectedThemeMode
  val isSysDark = MekanikThemeController.isSystemDark
  val palette = MekanikThemeController.currentPalette

  val dynamicColorScheme = if (palette.isLightAppearance) {
    lightColorScheme(
      primary = MechanicalOrange,
      onPrimary = Color.White,
      primaryContainer = MechanicalOrangeDark,
      onPrimaryContainer = palette.pureWhite,
      secondary = MechanicalOrange,
      onSecondary = Color.White,
      secondaryContainer = palette.metalGray,
      onSecondaryContainer = palette.offWhite,
      tertiary = palette.aluminumGray,
      onTertiary = Color.White,
      background = palette.graphiteDark,
      onBackground = palette.offWhite,
      surface = palette.metalCard,
      onSurface = palette.offWhite,
      surfaceVariant = palette.metalSurface,
      onSurfaceVariant = palette.aluminumGray,
      outline = palette.metalBorder,
      outlineVariant = palette.aluminumMuted,
      error = ErrorRed,
      onError = Color.White
    )
  } else {
    darkColorScheme(
      primary = MechanicalOrange,
      onPrimary = Color.White,
      primaryContainer = MechanicalOrangeDark,
      onPrimaryContainer = Color(0xFFE8EDF2),
      secondary = MechanicalOrange,
      onSecondary = Color.White,
      secondaryContainer = palette.metalCard,
      onSecondaryContainer = palette.offWhite,
      tertiary = palette.aluminumGray,
      onTertiary = palette.graphiteDark,
      background = palette.graphiteDark,
      onBackground = palette.offWhite,
      surface = palette.metalCard,
      onSurface = palette.offWhite,
      surfaceVariant = palette.metalSurface,
      onSurfaceVariant = palette.aluminumGray,
      outline = palette.metalBorder,
      outlineVariant = palette.aluminumMuted,
      error = ErrorRed,
      onError = Color.White
    )
  }

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = palette.graphiteDark.toArgb()
        window.navigationBarColor = palette.graphiteDark.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = palette.isLightAppearance
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = palette.isLightAppearance
      }
    }
  }

  key(selectedMode, isSysDark) {
    MaterialTheme(
      colorScheme = dynamicColorScheme,
      typography = Typography
    ) {
      CompositionLocalProvider(
        LocalContentColor provides palette.offWhite
      ) {
        content()
      }
    }
  }
}
