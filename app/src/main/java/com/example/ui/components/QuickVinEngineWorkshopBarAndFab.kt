package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

private val WorkshopBarSurface = Color(0xFF1C2833)
private val WorkshopBarInputBg = Color(0xFF101820)
private val WorkshopBarAccentOrange = Color(0xFFF07836)
private val WorkshopBarPrimaryText = Color(0xFFE8EDF2)
private val WorkshopBarSecondaryText = Color(0xFF94A3B8)
private val WorkshopBarValidationGreen = Color(0xFF22C55E)
private val WorkshopBarBorder = Color(0xFF2B3B4B)

data class SingleTapWorkshopPreset(
  val code: String,
  val label: String,
  val isEngineCode: Boolean
)

val defaultSingleTapWorkshopPresets = listOf(
  SingleTapWorkshopPreset("WVWZZZ5NZMW123456", "VW Tiguan 2.0 TDI", isEngineCode = false),
  SingleTapWorkshopPreset("DFGA", "Moteur 2.0 TDI VAG", isEngineCode = true),
  SingleTapWorkshopPreset("K9K 608", "Moteur 1.5 dCi Renault", isEngineCode = true),
  SingleTapWorkshopPreset("VF3FPHNSSMY001234", "Peugeot 308 1.6 HDi", isEngineCode = false),
  SingleTapWorkshopPreset("DV6C", "Moteur 1.6 HDi PSA", isEngineCode = true),
  SingleTapWorkshopPreset("LVVDB21B9PD048912", "Jetour Dashing 1.6T", isEngineCode = false)
)

/**
 * Barre de recherche persistante (Sticky Top Bar) optimisée pour la rapidité en atelier.
 * Permet de basculer et lancer une recherche VIN ou Code Moteur en un seul geste (1-Tap)
 * à tout moment lors du défilement de l'écran d'accueil.
 */
@Composable
fun PersistentWorkshopVinEngineBar(
  currentQuery: String,
  selectedTabMode: SearchTabMode,
  onQueryChange: (String) -> Unit,
  onSelectTabMode: (SearchTabMode) -> Unit,
  onTriggerInstantSearch: () -> Unit,
  onTriggerSingleTapPreset: (String, Boolean) -> Unit,
  onOpenCameraOcr: () -> Unit,
  onOpenVoiceSearch: () -> Unit,
  modifier: Modifier = Modifier
) {
  val focusManager = LocalFocusManager.current
  val isEngineMode = selectedTabMode == SearchTabMode.CODE_MOTEUR

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("persistent_workshop_vin_engine_bar"),
    color = WorkshopBarSurface, // #1C2833
    border = BorderStroke(1.dp, WorkshopBarBorder),
    shadowElevation = 6.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Ligne principale : Sélecteur 1-Tap [VIN | MOTEUR] + Champ rapide + Bouton Action #F07836 1-Tap
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Bascule 1-Tap VIN <-> CODE MOTEUR
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = WorkshopBarInputBg,
          border = BorderStroke(1.dp, WorkshopBarAccentOrange.copy(alpha = 0.7f)),
          modifier = Modifier
            .height(44.dp)
            .clickable {
              val nextMode =
                if (isEngineMode) SearchTabMode.VIN else SearchTabMode.CODE_MOTEUR
              onSelectTabMode(nextMode)
            }
            .testTag("persistent_bar_toggle_vin_engine")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = if (isEngineMode) Icons.Default.Engineering else Icons.Default.Tag,
              contentDescription = null,
              tint = WorkshopBarAccentOrange,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = if (isEngineMode) "MOTEUR" else "VIN 17",
              color = WorkshopBarPrimaryText,
              fontWeight = FontWeight.Bold,
              fontSize = 11.5.sp
            )
            Icon(
              imageVector = Icons.Default.SwapHoriz,
              contentDescription = "Basculer VIN / Code Moteur",
              tint = WorkshopBarSecondaryText,
              modifier = Modifier.size(14.dp)
            )
          }
        }

        // Champ de saisie compact haute lisibilité
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = WorkshopBarInputBg,
          border = BorderStroke(1.dp, WorkshopBarBorder),
          modifier = Modifier
            .weight(1f)
            .height(44.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            BasicTextField(
              value = currentQuery,
              onValueChange = { onQueryChange(it.uppercase()) },
              singleLine = true,
              textStyle = TextStyle(
                color = WorkshopBarPrimaryText,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              ),
              cursorBrush = SolidColor(WorkshopBarAccentOrange),
              keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Search
              ),
              keyboardActions = KeyboardActions(
                onSearch = {
                  focusManager.clearFocus()
                  onTriggerInstantSearch()
                }
              ),
              modifier = Modifier
                .weight(1f)
                .testTag("persistent_bar_search_input"),
              decorationBox = { innerTextField ->
                if (currentQuery.isEmpty()) {
                  Text(
                    text = if (isEngineMode) "Code Moteur (DFGA, K9K...)" else "VIN 17 car. ou Moteur...",
                    color = WorkshopBarSecondaryText,
                    fontSize = 11.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
                innerTextField()
              }
            )

            if (currentQuery.isNotEmpty()) {
              IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(26.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = "Effacer",
                  tint = WorkshopBarSecondaryText,
                  modifier = Modifier.size(15.dp)
                )
              }
            }
          }
        }

        // Bouton Scan OCR Caméra 1-Tap
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = WorkshopBarInputBg,
          border = BorderStroke(1.dp, WorkshopBarBorder),
          onClick = onOpenCameraOcr,
          modifier = Modifier
            .size(44.dp)
            .testTag("persistent_bar_btn_ocr")
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = "Scan OCR VIN / Moteur",
              tint = WorkshopBarPrimaryText,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        // Bouton Recherche 1-Tap (#F07836)
        Button(
          onClick = {
            focusManager.clearFocus()
            onTriggerInstantSearch()
          },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = WorkshopBarAccentOrange, // #F07836
            contentColor = Color.White
          ),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
          modifier = Modifier
            .height(44.dp)
            .testTag("persistent_bar_btn_one_tap_go")
        ) {
          Icon(
            imageVector = Icons.Default.FlashOn,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "1-Tap",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }

      // Ligne de puces 1-Tap : Déclenchement instantané d'une recherche VIN ou Code Moteur en 1 seul clic
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          text = "1-TAP ATELIER :",
          color = WorkshopBarAccentOrange,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp
        )
        defaultSingleTapWorkshopPresets.forEach { preset ->
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = WorkshopBarInputBg,
            border = BorderStroke(
              1.dp,
              if (currentQuery.equals(preset.code, ignoreCase = true))
                WorkshopBarAccentOrange
              else WorkshopBarBorder
            ),
            modifier = Modifier
              .clickable {
                focusManager.clearFocus()
                onTriggerSingleTapPreset(preset.code, preset.isEngineCode)
              }
              .testTag("persistent_chip_1tap_${preset.code.replace(" ", "_")}")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = if (preset.isEngineCode) "⚙️ ${preset.code}" else "🚗 ${preset.code.take(8)}…",
                color = WorkshopBarPrimaryText,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp
              )
              Text(
                text = "• ${preset.label}",
                color = WorkshopBarSecondaryText,
                fontSize = 10.sp
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Bouton d'action flottant (Floating Action Button) sur l'écran d'accueil
 * permettant aux mécaniciens de déclencher une recherche VIN ou Code Moteur en un seul tap.
 */
@Composable
fun QuickVinEngineSearchFab(
  onSingleTapOpenQuickSearch: () -> Unit,
  modifier: Modifier = Modifier
) {
  ExtendedFloatingActionButton(
    onClick = onSingleTapOpenQuickSearch,
    containerColor = WorkshopBarAccentOrange, // #F07836
    contentColor = Color.White,
    shape = RoundedCornerShape(14.dp),
    modifier = modifier
      .height(52.dp)
      .shadow(8.dp, RoundedCornerShape(14.dp))
      .testTag("fab_quick_vin_engine_search"),
    icon = {
      Icon(
        imageVector = Icons.Default.QrCodeScanner,
        contentDescription = "Recherche Rapide VIN ou Code Moteur",
        tint = Color.White,
        modifier = Modifier.size(22.dp)
      )
    },
    text = {
      Column {
        Text(
          text = "VIN / Moteur 1-Tap",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 12.5.sp
        )
        Text(
          text = "Recherche Express Atelier",
          color = Color.White.copy(alpha = 0.88f),
          fontSize = 9.5.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }
  )
}

/**
 * Fenêtre d'action rapide 1-Tap ouverte par le FAB pour lancer en une fraction de seconde
 * une recherche par VIN (17 caractères), par Code Moteur, par Scan Caméra OCR ou Voix Mains-Libres.
 */
@Composable
fun SingleTapVinEngineLauncherModal(
  currentQuery: String,
  onExecuteSingleTapSearch: (query: String, isEngineCode: Boolean) -> Unit,
  onOpenCameraOcr: () -> Unit,
  onOpenVoiceSearch: () -> Unit,
  onDismiss: () -> Unit
) {
  var customInput by remember(currentQuery) { mutableStateOf(currentQuery) }
  var isEngineSelected by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 24.dp)
        .testTag("single_tap_vin_engine_modal"),
      shape = RoundedCornerShape(16.dp),
      color = WorkshopBarSurface, // #1C2833
      border = BorderStroke(1.dp, WorkshopBarAccentOrange)
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(WorkshopBarAccentOrange.copy(alpha = 0.18f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.FlashOn,
                contentDescription = null,
                tint = WorkshopBarAccentOrange,
                modifier = Modifier.size(24.dp)
              )
            }
            Column {
              Text(
                text = "RECHERCHE EXPRESS 1-TAP (VIN & CODE MOTEUR)",
                color = WorkshopBarPrimaryText,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "Conçu pour les environnements d'atelier à cadence élevée",
                color = WorkshopBarSecondaryText,
                fontSize = 11.sp
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Fermer",
              tint = WorkshopBarPrimaryText
            )
          }
        }

        // Sélecteur Mode : VIN (17 car.) vs Code Moteur
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (!isEngineSelected) WorkshopBarAccentOrange else WorkshopBarInputBg,
            border = BorderStroke(1.dp, if (!isEngineSelected) WorkshopBarAccentOrange else WorkshopBarBorder),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .clickable { isEngineSelected = false }
              .testTag("modal_tab_1tap_vin")
          ) {
            Row(
              modifier = Modifier.fillMaxSize(),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Recherche VIN", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
            }
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isEngineSelected) WorkshopBarAccentOrange else WorkshopBarInputBg,
            border = BorderStroke(1.dp, if (isEngineSelected) WorkshopBarAccentOrange else WorkshopBarBorder),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .clickable { isEngineSelected = true }
              .testTag("modal_tab_1tap_engine")
          ) {
            Row(
              modifier = Modifier.fillMaxSize(),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Engineering, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Code Moteur", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
            }
          }
        }

        // Saisie directe + Bouton Lancer #F07836
        OutlinedTextField(
          value = customInput,
          onValueChange = { customInput = it.uppercase() },
          label = {
            Text(
              if (isEngineSelected) "Code Moteur (ex: DFGA, K9K 608, DV6C)"
              else "Numéro de châssis VIN (17 caractères)"
            )
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("modal_1tap_input_field"),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = WorkshopBarInputBg,
            unfocusedContainerColor = WorkshopBarInputBg,
            focusedBorderColor = WorkshopBarAccentOrange,
            unfocusedBorderColor = WorkshopBarBorder,
            focusedTextColor = WorkshopBarPrimaryText,
            unfocusedTextColor = WorkshopBarPrimaryText
          )
        )

        Button(
          onClick = {
            if (customInput.isNotBlank()) {
              onExecuteSingleTapSearch(customInput.trim(), isEngineSelected)
            }
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = WorkshopBarAccentOrange, // #F07836
            contentColor = Color.White
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("modal_btn_execute_1tap_search")
        ) {
          Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isEngineSelected) "Lancer Recherche Code Moteur" else "Lancer Décodage VIN Instantané",
            fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp
          )
        }

        // Déclencheurs 1-Tap Sans Saisie Clavier (Caméra OCR & Dictée Vocale Mains-Sales)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          MekanikSecondaryButton(
            text = "Scan Caméra OCR",
            icon = Icons.Default.CameraAlt,
            onClick = {
              onDismiss()
              onOpenCameraOcr()
            },
            modifier = Modifier.weight(1f)
          )
          MekanikSecondaryButton(
            text = "Voix Mains-Libres",
            icon = Icons.Default.Mic,
            onClick = {
              onDismiss()
              onOpenVoiceSearch()
            },
            modifier = Modifier.weight(1f)
          )
        }

        HorizontalDivider(color = WorkshopBarBorder)

        // Sélection 1-Tap Atelier : Châssis & Codes Moteurs Récents
        Text(
          text = "DÉCLENCHEMENT INSTANTANÉ EN 1 SEUL TAP :",
          color = WorkshopBarSecondaryText,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          defaultSingleTapWorkshopPresets.chunked(2).forEach { rowPresets ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              rowPresets.forEach { preset ->
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = WorkshopBarInputBg,
                  border = BorderStroke(1.dp, WorkshopBarBorder),
                  modifier = Modifier
                    .weight(1f)
                    .clickable {
                      onExecuteSingleTapSearch(preset.code, preset.isEngineCode)
                    }
                    .testTag("modal_preset_1tap_${preset.code.replace(" ", "_")}")
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = preset.code,
                        color = WorkshopBarPrimaryText,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                      Text(
                        text = preset.label,
                        color = WorkshopBarSecondaryText,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                    }
                    Icon(
                      imageVector = Icons.Default.PlayArrow,
                      contentDescription = null,
                      tint = WorkshopBarAccentOrange,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
