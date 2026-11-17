package com.example.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**
 * MEKANIK+ — AUTOMOTIVE INTELLIGENCE PLATFORM
 * Identité visuelle officielle :
 * - Arrière-plan principal (Fond) : #101820
 * - Cartes et surfaces (Cartes)   : #1C2833
 * - Couleur principale (Accent)   : #F07836 (Orange Mécanique signature)
 * - Texte principal (Texte)       : #E8EDF2
 * - Texte secondaire              : #94A3B8
 * - Vert de validation            : #22C55E
 * - Rouge d'erreur                : #EF4444
 */
enum class AppDisplayThemeMode(
  val id: String,
  val label: String,
  val shortLabel: String,
  val subtitle: String,
  val badgeEmoji: String
) {
  LIGHT_MODE(
    id = "LIGHT_MODE",
    label = "Mode Normal (Clair • Écriture Noire)",
    shortLabel = "☀️ Mode Normal",
    subtitle = "Fond clair (#F8FAFC) et écriture noire (#0F172A) automatique",
    badgeEmoji = "☀️"
  ),
  DARK_MODE(
    id = "DARK_MODE",
    label = "Mode Sombre (#101820 • Écriture Claire)",
    shortLabel = "🌙 Mode Sombre",
    subtitle = "Fond sombre profond (#101820), surfaces (#1C2833) et texte (#E8EDF2)",
    badgeEmoji = "🌙"
  );

  companion object {
    val SYSTEM_AUTO: AppDisplayThemeMode get() = DARK_MODE
    val DAY_MODE: AppDisplayThemeMode get() = LIGHT_MODE

    fun fromId(raw: String?): AppDisplayThemeMode {
      return when (raw?.uppercase()) {
        "LIGHT_MODE", "DAY_MODE", "NORMAL" -> LIGHT_MODE
        "DARK_MODE" -> DARK_MODE
        else -> DARK_MODE
      }
    }
  }

  val emoji: String
    get() = badgeEmoji
}

data class MekanikDynamicPalette(
  val isLightAppearance: Boolean,
  val graphiteDark: Color,
  val graphiteBlack: Color,
  val metalGray: Color,
  val metalSurface: Color,
  val metalCard: Color,
  val metalBorder: Color,
  val aluminumGray: Color,
  val aluminumLight: Color,
  val aluminumMuted: Color,
  val offWhite: Color,
  val pureWhite: Color,
  val textMuted: Color,
  val performanceRedDark: Color,
  val mechanicalOrangeDark: Color,
  val techCyan: Color,
  val techCyanDark: Color,
  val techCyanGlow: Color,
  val electricGreen: Color,
  val electricGreenDark: Color
)

object MekanikThemeController {
  private const val PREFS_NAME = "mekanik_display_theme_prefs"
  private const val KEY_THEME_MODE = "selected_display_theme_mode"
  private const val KEY_USER_MANUAL_CHOICE = "user_manual_theme_choice"

  // Par défaut : Mode Sombre Premium #101820 / #1C2833 / #F07836
  var selectedThemeMode by mutableStateOf(AppDisplayThemeMode.DARK_MODE)
    private set

  var isSystemDark by mutableStateOf(true)
    private set

  private var lastSystemDarkObserved: Boolean? = null
  private var hasManualOverrideInSession: Boolean = false

  val selectedMode: AppDisplayThemeMode
    get() = selectedThemeMode

  val systemIsDark: Boolean
    get() = isSystemDark

  // Palette Sombre Officielle MEKANIK+ (#101820 / #1C2833 / #F07836 / #E8EDF2 / #94A3B8 / #22C55E / #EF4444)
  private val DarkGraphitePalette = MekanikDynamicPalette(
    isLightAppearance = false,
    graphiteDark = Color(0xFF101820),          // Arrière-plan principal officiel #101820
    graphiteBlack = Color(0xFF0C131A),         // Surface en retrait discrète
    metalGray = Color(0xFF243342),             // Surface secondaire sobre
    metalSurface = Color(0xFF16212B),          // Surface de champ de saisie
    metalCard = Color(0xFF1C2833),             // Cartes et surfaces officielles #1C2833
    metalBorder = Color(0xFF2B3B4B),           // Bordure fine et sobre (sans néon)
    aluminumGray = Color(0xFF94A3B8),          // Texte secondaire officiel #94A3B8
    aluminumLight = Color(0xFFCBD5E1),         // Sous-titres clairs
    aluminumMuted = Color(0xFF94A3B8),         // Labels secondaires #94A3B8
    offWhite = Color(0xFFE8EDF2),              // Texte principal officiel #E8EDF2
    pureWhite = Color(0xFFE8EDF2),             // Titres principaux #E8EDF2
    textMuted = Color(0xFF94A3B8),             // Texte secondaire #94A3B8
    performanceRedDark = Color(0xFF3B181E),    // Fond discret d'alerte/erreur
    mechanicalOrangeDark = Color(0xFF3D2417),  // Fond discret accent orange
    techCyan = Color(0xFFF07836),              // Harmonisé sur l'accent signature sobre ou ardoise claire
    techCyanDark = Color(0xFF243342),
    techCyanGlow = Color(0xFFF07836),
    electricGreen = Color(0xFF22C55E),         // Vert de validation officiel #22C55E
    electricGreenDark = Color(0xFF132E22)
  )

  // Palette Mode Normal Clair (Fond Clair & Écriture Noire Automatique)
  private val NormalLightPalette = MekanikDynamicPalette(
    isLightAppearance = true,
    graphiteDark = Color(0xFFF4F6F9),          // Fond principal clair
    graphiteBlack = Color(0xFFE8EEF5),         // Fond secondaire clair
    metalGray = Color(0xFFD8E2EE),             // Conteneur secondaire clair
    metalSurface = Color(0xFFFFFFFF),          // Surface claire
    metalCard = Color(0xFFFFFFFF),             // Carte blanche nette
    metalBorder = Color(0xFFCBD5E1),           // Bordure subtile et propre
    aluminumGray = Color(0xFF475569),          // Texte secondaire ardoise
    aluminumLight = Color(0xFF1E293B),         // Sous-titres foncés
    aluminumMuted = Color(0xFF64748B),         // Labels secondaires
    offWhite = Color(0xFF101820),              // Écriture principale noire/anthracite
    pureWhite = Color(0xFF101820),             // Titres noirs/anthracite
    textMuted = Color(0xFF475569),             // Texte secondaire
    performanceRedDark = Color(0xFFFEE2E2),    // Fond badge erreur clair
    mechanicalOrangeDark = Color(0xFFFFEDD5),  // Fond badge orange clair
    techCyan = Color(0xFFF07836),              // Accent signature
    techCyanDark = Color(0xFFFFEDD5),
    techCyanGlow = Color(0xFFD96526),
    electricGreen = Color(0xFF16A34A),         // Vert validation sur fond clair
    electricGreenDark = Color(0xFFDCFCE7)
  )

  val currentPalette: MekanikDynamicPalette
    get() = when (selectedThemeMode) {
      AppDisplayThemeMode.LIGHT_MODE -> NormalLightPalette
      AppDisplayThemeMode.DARK_MODE -> DarkGraphitePalette
    }

  val isLightModeActive: Boolean
    get() = currentPalette.isLightAppearance

  val resolvedModeLabel: String
    get() = selectedThemeMode.label

  fun updateSystemDarkState(systemDark: Boolean) {
    val previous = lastSystemDarkObserved
    isSystemDark = systemDark
    lastSystemDarkObserved = systemDark
    if (previous != null && previous != systemDark && !hasManualOverrideInSession) {
      selectedThemeMode = if (systemDark) AppDisplayThemeMode.DARK_MODE else AppDisplayThemeMode.LIGHT_MODE
    }
  }

  fun loadSavedThemeMode(context: Context) {
    try {
      val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
      val hasManual = prefs.getBoolean(KEY_USER_MANUAL_CHOICE, false)
      val saved = prefs.getString(KEY_THEME_MODE, null)
      if (hasManual && !saved.isNullOrBlank()) {
        hasManualOverrideInSession = true
        selectedThemeMode = AppDisplayThemeMode.fromId(saved)
      }
    } catch (_: Exception) {
    }
  }

  fun setDisplayThemeMode(mode: AppDisplayThemeMode, context: Context? = null) {
    hasManualOverrideInSession = true
    selectedThemeMode = mode
    if (context != null) {
      try {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
          .edit()
          .putString(KEY_THEME_MODE, mode.id)
          .putBoolean(KEY_USER_MANUAL_CHOICE, true)
          .apply()
      } catch (_: Exception) {
      }
    }
  }

  fun toggleNormalOrDarkMode(context: Context? = null) {
    val next = if (selectedThemeMode == AppDisplayThemeMode.LIGHT_MODE) {
      AppDisplayThemeMode.DARK_MODE
    } else {
      AppDisplayThemeMode.LIGHT_MODE
    }
    setDisplayThemeMode(next, context)
  }
}

fun adaptiveBannerSurface(darkHex: Long = 0xFF1C2833, lightHex: Long = 0xFFFFFFFF): Color {
  return if (MekanikThemeController.isLightModeActive) Color(lightHex) else Color(0xFF1C2833)
}

// ============================================================================
// JETONS DE COULEURS OFFICIELS MEKANIK+ (#101820 / #1C2833 / #F07836 / #E8EDF2)
// ============================================================================
val MekanikBgDark = Color(0xFF101820)
val MekanikCardSurface = Color(0xFF1C2833)
val MekanikBrandOrange = Color(0xFFF07836)
val MekanikPrimaryText = Color(0xFFE8EDF2)
val MekanikSecondaryText = Color(0xFF94A3B8)
val MekanikValidationGreen = Color(0xFF22C55E)
val MekanikErrorRed = Color(0xFFEF4444)

// Dynamic Getters
val GraphiteDark: Color get() = MekanikThemeController.currentPalette.graphiteDark
val GraphiteBlack: Color get() = MekanikThemeController.currentPalette.graphiteBlack
val MetalGray: Color get() = MekanikThemeController.currentPalette.metalGray
val MetalSurface: Color get() = MekanikThemeController.currentPalette.metalSurface
val MetalCard: Color get() = MekanikThemeController.currentPalette.metalCard
val MetalBorder: Color get() = MekanikThemeController.currentPalette.metalBorder
val AluminumGray: Color get() = MekanikThemeController.currentPalette.aluminumGray
val AluminumLight: Color get() = MekanikThemeController.currentPalette.aluminumLight
val AluminumMuted: Color get() = MekanikThemeController.currentPalette.aluminumMuted

// Couleur Principale Signature : Orange Mécanique #F07836 & Rouge d'Erreur #EF4444
val MechanicalOrange = Color(0xFFF07836)
val MechanicalOrangeDark: Color get() = MekanikThemeController.currentPalette.mechanicalOrangeDark
val MechanicalOrangeGlow: Color get() = Color(0xFFF07836)

// Pour garantir que l'orange #F07836 est la signature principale partout tout en gardant #EF4444 pour les erreurs
val ErrorRed = Color(0xFFEF4444)
val PerformanceRed = Color(0xFFF07836)
val PerformanceRedDark: Color get() = MekanikThemeController.currentPalette.mechanicalOrangeDark
val PerformanceRedGlow: Color get() = Color(0xFFF07836)

// Texte Principal (#E8EDF2 en Mode Sombre / #101820 en Mode Normal) & Texte Secondaire (#94A3B8)
val OffWhite: Color get() = MekanikThemeController.currentPalette.offWhite
val PureWhite: Color get() = MekanikThemeController.currentPalette.pureWhite
val TextMuted: Color get() = MekanikThemeController.currentPalette.textMuted

// Couleurs Fonctionnelles Sobres (Validation #22C55E, Erreur #EF4444, Info Ardoise)
val ElectricGreen: Color get() = MekanikThemeController.currentPalette.electricGreen
val ElectricGreenDark: Color get() = MekanikThemeController.currentPalette.electricGreenDark
val TechCyan: Color get() = MekanikThemeController.currentPalette.techCyan
val TechCyanDark: Color get() = MekanikThemeController.currentPalette.techCyanDark
val TechCyanGlow: Color get() = MekanikThemeController.currentPalette.techCyanGlow
val WarningAmber: Color
  get() = if (MekanikThemeController.isLightModeActive) Color(0xFFD97706) else Color(0xFFF59E0B)
val WarningYellow: Color
  get() = if (MekanikThemeController.isLightModeActive) Color(0xFFD97706) else Color(0xFFF59E0B)
val EvBlue = Color(0xFF64748B)
val TechnicalBlue = Color(0xFF64748B)

// Aliases unifiés sans néon
val GraphiteMetal: Color get() = MetalCard
val SteelBorder: Color get() = MetalBorder
val AluminumGrey: Color get() = AluminumGray
val MechanicRed: Color get() = MechanicalOrange
val MechanicOrange: Color get() = MechanicalOrange
val ElectricBlue: Color get() = AluminumLight
val StatusGreen: Color get() = ElectricGreen
val AiCyan: Color get() = MechanicalOrange
val AiPurple: Color get() = AluminumLight
