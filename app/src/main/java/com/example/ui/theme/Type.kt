package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typographie moderne, internationale et hautement lisible pour MEKANIK+
 * Textes confortables, hiérarchie nette (#E8EDF2 principal / #94A3B8 secondaire).
 */
val Typography: Typography
  get() = Typography(
    displayLarge = TextStyle(
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Bold,
      fontSize = 30.sp,
      lineHeight = 36.sp,
      letterSpacing = (-0.4).sp,
      color = PureWhite
    ),
    displayMedium = TextStyle(
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Bold,
      fontSize = 24.sp,
      lineHeight = 30.sp,
      letterSpacing = (-0.2).sp,
      color = PureWhite
    ),
    displaySmall = TextStyle(
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.SemiBold,
      fontSize = 20.sp,
      lineHeight = 26.sp,
      color = PureWhite
    ),
    headlineLarge = TextStyle(
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Bold,
      fontSize = 20.sp,
      lineHeight = 26.sp,
      color = PureWhite
    ),
    headlineMedium = TextStyle(
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.SemiBold,
      fontSize = 18.sp,
      lineHeight = 24.sp,
      color = PureWhite
    ),
    headlineSmall = TextStyle(
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.SemiBold,
      fontSize = 16.sp,
      lineHeight = 22.sp,
      color = OffWhite
    ),
    titleLarge = TextStyle(
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.SemiBold,
      fontSize = 16.sp,
      lineHeight = 22.sp,
      color = OffWhite
    ),
    titleMedium = TextStyle(
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.SemiBold,
      fontSize = 14.5.sp,
      lineHeight = 20.sp,
      letterSpacing = 0.1.sp,
      color = OffWhite
    ),
    titleSmall = TextStyle(
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.SemiBold,
      fontSize = 13.sp,
      lineHeight = 18.sp,
      color = OffWhite
    ),
    bodyLarge = TextStyle(
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Normal,
      fontSize = 14.5.sp,
      lineHeight = 21.sp,
      letterSpacing = 0.15.sp,
      color = OffWhite
    ),
    bodyMedium = TextStyle(
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Normal,
      fontSize = 13.sp,
      lineHeight = 18.sp,
      letterSpacing = 0.15.sp,
      color = AluminumGray
    ),
    bodySmall = TextStyle(
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Normal,
      fontSize = 12.sp,
      lineHeight = 16.sp,
      color = AluminumGray
    ),
    labelLarge = TextStyle(
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.SemiBold,
      fontSize = 13.5.sp,
      lineHeight = 18.sp,
      letterSpacing = 0.3.sp,
      color = OffWhite
    ),
    labelMedium = TextStyle(
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Medium,
      fontSize = 12.sp,
      lineHeight = 16.sp,
      letterSpacing = 0.3.sp,
      color = AluminumGray
    ),
    labelSmall = TextStyle(
      fontFamily = FontFamily.SansSerif,
      fontWeight = FontWeight.Medium,
      fontSize = 11.sp,
      lineHeight = 15.sp,
      letterSpacing = 0.25.sp,
      color = AluminumGray
    )
  )
