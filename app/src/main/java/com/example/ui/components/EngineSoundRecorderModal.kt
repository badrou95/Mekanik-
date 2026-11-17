package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.data.model.*
import com.example.data.repository.AcousticSoundRepository
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

enum class AcousticModalTab(val label: String, val icon: ImageVector) {
  ENREGISTREUR("Micro & Diagnostic IA", Icons.Default.Mic),
  BIBLIOTHEQUE_SONS("Sons Témoins", Icons.Default.Audiotrack),
  HISTORIQUE("Historique Enregistré", Icons.Default.History)
}

@Composable
fun EngineSoundRecorderModal(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val df = DecimalFormat("#,###", DecimalFormatSymbols(Locale.FRENCH))
  var selectedTab by remember { mutableStateOf(AcousticModalTab.ENREGISTREUR) }

  // Audio Recording permission launcher
  var hasAudioPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
      ) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasAudioPermission = isGranted
    if (isGranted) {
      viewModel.startEngineSoundRecording()
    }
  }

  // Pulsing animation when recording
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_recording")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.18f,
    animationSpec = infiniteRepeatable(
      animation = tween(650, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.6f,
    targetValue = 0.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(650, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  Dialog(
    onDismissRequest = {
      if (!uiState.isRecordingEngineSound) onDismiss()
    },
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .fillMaxHeight(0.92f)
        .testTag("engine_sound_recorder_modal"),
      shape = RoundedCornerShape(22.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, MetalBorder)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {

        // --- 1. MODAL HEADER (Automotive Red & Graphite) ---
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = MetalCard,
          border = BorderStroke(0.5.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(PerformanceRedDark.copy(alpha = 0.4f))
                  .border(1.dp, PerformanceRed, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.GraphicEq,
                  contentDescription = null,
                  tint = PerformanceRedGlow,
                  modifier = Modifier.size(22.dp)
                )
              }

              Column {
                Text(
                  text = "MEKANIK+ SONAR IA MOTEUR",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    letterSpacing = 0.5.sp
                  )
                )
                Text(
                  text = "Analyse Acoustique & Détection des Bruits Anormaux",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = TechCyan,
                    fontSize = 11.sp
                  )
                )
              }
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier.size(34.dp)
            ) {
              Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite)
            }
          }
        }

        // --- 2. TAB SELECTOR ---
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = GraphiteBlack,
          border = BorderStroke(0.5.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            AcousticModalTab.values().forEach { tab ->
              val isSelected = selectedTab == tab
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) PerformanceRed else MetalCard,
                border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
                modifier = Modifier
                  .weight(1f)
                  .clickable { selectedTab = tab }
              ) {
                Row(
                  modifier = Modifier.padding(vertical = 8.dp),
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    tab.icon,
                    contentDescription = null,
                    tint = if (isSelected) PureWhite else AluminumMuted,
                    modifier = Modifier.size(15.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = tab.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      color = if (isSelected) PureWhite else AluminumLight,
                      fontSize = 10.5.sp
                    )
                  )
                }
              }
            }
          }
        }

        // --- 3. TAB CONTENT ---
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          when (selectedTab) {

            // ==================== TAB 1: ENREGISTREUR MICRO & DIAGNOSTIC IA ====================
            AcousticModalTab.ENREGISTREUR -> {

              // Vehicle Context Card
              item {
                val v = uiState.activeVehicle
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = MetalCard,
                  border = BorderStroke(1.dp, MetalBorder),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text(
                        text = "VÉHICULE ANALYSÉ :",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = AluminumMuted,
                          fontSize = 9.sp,
                          letterSpacing = 0.5.sp
                        )
                      )
                      Text(
                        text = "${v?.brand ?: "Volkswagen"} ${v?.model ?: "Golf 7"} (${v?.engineCode ?: "2.0 TDI CR"})",
                        style = MaterialTheme.typography.bodyMedium.copy(
                          fontWeight = FontWeight.Bold,
                          color = PureWhite
                        )
                      )
                      Text(
                        text = "VIN : ${v.vin} • Cylindrée : ${v.displacement}",
                        style = MaterialTheme.typography.bodySmall.copy(
                          color = AluminumLight,
                          fontSize = 11.sp
                        )
                      )
                    }

                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = PerformanceRedDark.copy(alpha = 0.4f),
                      border = BorderStroke(0.5.dp, PerformanceRed)
                    ) {
                      Text(
                        text = "Moteur Thermique",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = PureWhite,
                          fontWeight = FontWeight.Bold,
                          fontSize = 9.5.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                      )
                    }
                  }
                }
              }

              // Recording Oscilloscope & Spectrum Visualizer Box
              item {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
                  border = BorderStroke(1.5.dp, if (uiState.isRecordingEngineSound) PerformanceRed else MetalBorder)
                ) {
                  Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    // Header Telemetry Stats
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                          modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (uiState.isRecordingEngineSound) PerformanceRed else ElectricGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                          text = if (uiState.isRecordingEngineSound) "ENREGISTREMENT EN COURS..." else "CAPTEUR PRÊT",
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = if (uiState.isRecordingEngineSound) PerformanceRedGlow else ElectricGreen,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.5.sp
                          )
                        )
                      }

                      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                          shape = RoundedCornerShape(4.dp),
                          color = MetalCard,
                          border = BorderStroke(0.5.dp, MetalBorder)
                        ) {
                          Text(
                            text = "${uiState.recordedDecibelLevel} dB SPL",
                            style = MaterialTheme.typography.labelSmall.copy(
                              color = if (uiState.recordedDecibelLevel > 80) PerformanceRedGlow else TechCyan,
                              fontFamily = FontFamily.Monospace,
                              fontWeight = FontWeight.Bold,
                              fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                          )
                        }

                        Surface(
                          shape = RoundedCornerShape(4.dp),
                          color = MetalCard,
                          border = BorderStroke(0.5.dp, MetalBorder)
                        ) {
                          Text(
                            text = "Pic : ${uiState.recordedPeakHz} Hz",
                            style = MaterialTheme.typography.labelSmall.copy(
                              color = MechanicalOrange,
                              fontFamily = FontFamily.Monospace,
                              fontWeight = FontWeight.Bold,
                              fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                          )
                        }
                      }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Audio Waveform Equalizer Display
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .background(MetalCard.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.Bottom
                    ) {
                      val amplitudes = if (uiState.isRecordingEngineSound) {
                        uiState.engineSoundLiveAmplitudes
                      } else {
                        uiState.acousticDiagnosisResult?.waveformAmplitudes
                          ?: listOf(0.2f, 0.35f, 0.5f, 0.8f, 0.45f, 0.6f, 0.95f, 0.4f, 0.7f, 0.5f, 0.85f, 0.3f, 0.6f, 0.4f)
                      }

                      amplitudes.forEachIndexed { idx, amp ->
                        val barHeightFraction = amp.coerceIn(0.12f, 1.0f)
                        val isCriticalBar = amp > 0.8f

                        Column(
                          horizontalAlignment = Alignment.CenterHorizontally,
                          verticalArrangement = Arrangement.Bottom,
                          modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                        ) {
                          Box(
                            modifier = Modifier
                              .fillMaxWidth()
                              .fillMaxHeight(barHeightFraction)
                              .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                              .background(
                                Brush.verticalGradient(
                                  colors = if (isCriticalBar) listOf(PerformanceRedGlow, PerformanceRedDark)
                                  else if (uiState.isRecordingEngineSound) listOf(MechanicalOrange, PerformanceRed)
                                  else listOf(TechCyan, Color(0xFF005577))
                                )
                              )
                          )
                        }
                      }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Center Microphone Record Button
                    Box(
                      contentAlignment = Alignment.Center,
                      modifier = Modifier.size(90.dp)
                    ) {
                      if (uiState.isRecordingEngineSound) {
                        Box(
                          modifier = Modifier
                            .size(80.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(PerformanceRed.copy(alpha = pulseAlpha))
                        )
                      }

                      FloatingActionButton(
                        onClick = {
                          if (uiState.isRecordingEngineSound) {
                            viewModel.stopAndAnalyzeEngineSound()
                          } else {
                            if (hasAudioPermission) {
                              viewModel.startEngineSoundRecording()
                            } else {
                              permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                          }
                        },
                        containerColor = if (uiState.isRecordingEngineSound) PerformanceRed else GraphiteBlack,
                        contentColor = PureWhite,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
                        modifier = Modifier
                          .size(68.dp)
                          .border(2.dp, if (uiState.isRecordingEngineSound) PerformanceRedGlow else TechCyan, CircleShape)
                          .testTag("btn_record_engine_sound")
                      ) {
                        if (uiState.isRecordingEngineSound) {
                          Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Stop, contentDescription = "Arrêter", modifier = Modifier.size(28.dp), tint = PureWhite)
                            Text(
                              text = "${uiState.engineSoundRecordingDurationSec}s",
                              style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                            )
                          }
                        } else {
                          Icon(Icons.Default.Mic, contentDescription = "Enregistrer", modifier = Modifier.size(32.dp), tint = TechCyan)
                        }
                      }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                      text = if (uiState.isRecordingEngineSound) "Enregistrement en cours... Tenez le smartphone près du compartiment moteur (50 cm)."
                      else "Appuyez sur le micro pour lancer l'enregistrement acoustique de 5 secondes.",
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = AluminumLight,
                        fontSize = 11.5.sp,
                        textAlign = TextAlign.Center
                      ),
                      modifier = Modifier.padding(horizontal = 12.dp)
                    )
                  }
                }
              }

              // Sound Presets Selector Shortcut Bar
              item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "OU TESTER AVEC UN SON TÉMOIN CONNU :",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = AluminumMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        fontSize = 10.sp
                      )
                    )
                    Text(
                      text = "7 Anomalies",
                      style = MaterialTheme.typography.labelSmall.copy(color = TechCyan, fontSize = 10.sp)
                    )
                  }

                  LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    items(AcousticSoundRepository.PRESETS) { preset ->
                      val isSelected = uiState.selectedSoundPresetId == preset.id
                      Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) PerformanceRedDark.copy(alpha = 0.4f) else MetalCard,
                        border = BorderStroke(1.dp, if (isSelected) PerformanceRed else MetalBorder),
                        modifier = Modifier
                          .clickable {
                            viewModel.selectEngineSoundPreset(preset.id)
                            viewModel.stopAndAnalyzeEngineSound()
                          }
                      ) {
                        Row(
                          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                          Box(
                            modifier = Modifier
                              .size(8.dp)
                              .clip(CircleShape)
                              .background(Color(preset.severity.colorHex))
                          )
                          Text(
                            text = preset.title.take(24) + "...",
                            style = MaterialTheme.typography.labelSmall.copy(
                              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                              color = if (isSelected) PureWhite else AluminumLight,
                              fontSize = 10.5.sp
                            )
                          )
                        }
                      }
                    }
                  }
                }
              }

              // --- ACOUSTIC DIAGNOSIS RESULT SECTION ---
              if (uiState.acousticDiagnosisResult != null) {
                val diag = uiState.acousticDiagnosisResult!!
                val preset = diag.identifiedPreset

                item {
                  Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MetalCard),
                    border = BorderStroke(1.5.dp, Color(preset.severity.colorHex))
                  ) {
                    Column(
                      modifier = Modifier.padding(16.dp),
                      verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                      // Top Severity Banner
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = Color(preset.severity.colorHex).copy(alpha = 0.2f),
                          border = BorderStroke(1.dp, Color(preset.severity.colorHex))
                        ) {
                          Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                          ) {
                            Icon(
                              Icons.Default.Warning,
                              contentDescription = null,
                              tint = Color(preset.severity.colorHex),
                              modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                              text = preset.severity.label,
                              style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(preset.severity.colorHex),
                                fontWeight = FontWeight.Black,
                                fontSize = 10.5.sp
                              )
                            )
                          }
                        }

                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = ElectricGreen.copy(alpha = 0.15f),
                          border = BorderStroke(0.5.dp, ElectricGreen)
                        ) {
                          Text(
                            text = "Confiance IA : ${diag.confidenceScorePercent}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                              color = ElectricGreen,
                              fontWeight = FontWeight.Bold,
                              fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                          )
                        }
                      }

                      // Diagnostic Title & Subtitle
                      Column {
                        Text(
                          text = preset.title,
                          style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = PureWhite,
                            fontSize = 15.sp
                          )
                        )
                        Text(
                          text = preset.frenchSubtitle,
                          style = MaterialTheme.typography.bodySmall.copy(
                            color = TechCyan,
                            fontSize = 11.5.sp
                          )
                        )
                      }

                      // Frequency & Rhythm details
                      Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MetalSurface,
                        modifier = Modifier.fillMaxWidth()
                      ) {
                        Column(
                          modifier = Modifier.padding(10.dp),
                          verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                          Text(
                            text = "Bande spectrale : ${preset.frequencyBandDescription}",
                            style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp)
                          )
                          Text(
                            text = "Cadence rythmique : ${preset.rhythmicPattern}",
                            style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp)
                          )
                        }
                      }

                      // Plain Language Explanation
                      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                          text = "EXPLICATION CLAIRE DE LA PANNE :",
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = MechanicalOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                          )
                        )
                        Text(
                          text = diag.plainExplanation,
                          style = MaterialTheme.typography.bodyMedium.copy(
                            color = PureWhite,
                            lineHeight = 18.sp,
                            fontSize = 12.sp
                          )
                        )
                      }

                      // Causes Probables
                      Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                          text = "CAUSES PROBABLES :",
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = AluminumMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                          )
                        )
                        preset.probableCauses.forEach { cause ->
                          Row(verticalAlignment = Alignment.Top) {
                            Text("• ", color = PerformanceRed, fontWeight = FontWeight.Bold)
                            Text(
                              text = cause,
                              style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.5.sp)
                            )
                          }
                        }
                      }

                      // Actions Immédiates
                      Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                          text = "ACTIONS RECOMMANDÉES :",
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = ElectricGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                          )
                        )
                        preset.immediateActions.forEach { action ->
                          Row(verticalAlignment = Alignment.Top) {
                            Text("✓ ", color = ElectricGreen, fontWeight = FontWeight.Bold)
                            Text(
                              text = action,
                              style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontSize = 11.5.sp)
                            )
                          }
                        }
                      }

                      // Recommended Parts & Prices in DZD
                      if (preset.recommendedParts.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                          Text(
                            text = "PIÈCES DE RECHANGE RECOMMANDÉES :",
                            style = MaterialTheme.typography.labelSmall.copy(
                              color = TechCyan,
                              fontWeight = FontWeight.Bold,
                              fontSize = 10.sp
                            )
                          )

                          preset.recommendedParts.forEach { part ->
                            Surface(
                              shape = RoundedCornerShape(8.dp),
                              color = MetalSurface,
                              border = BorderStroke(0.5.dp, MetalBorder),
                              modifier = Modifier.fillMaxWidth()
                            ) {
                              Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                              ) {
                                Column(modifier = Modifier.weight(1f)) {
                                  Text(
                                    text = part.name,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                      fontWeight = FontWeight.Bold,
                                      color = PureWhite,
                                      fontSize = 11.5.sp
                                    )
                                  )
                                  Text(
                                    text = "${part.brandName} • OEM: ${part.oemReference}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                      color = AluminumGray,
                                      fontSize = 9.5.sp
                                    )
                                  )
                                }
                                Text(
                                  text = "${df.format(part.priceEstimatedDzd)} DA",
                                  style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = MechanicalOrange,
                                    fontSize = 12.sp
                                  )
                                )
                              }
                            }
                          }
                        }
                      }

                      Divider(color = MetalBorder)

                      // Cost Estimate and Actions
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Column {
                          Text(
                            text = "Estimation Réparation :",
                            style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 9.5.sp)
                          )
                          Text(
                            text = if (preset.estimatedRepairCostDzd > 0) "${df.format(preset.estimatedRepairCostDzd)} DA (${preset.estimatedLaborHours}h MO)" else "0 DA (Entretien normal)",
                            style = MaterialTheme.typography.titleSmall.copy(
                              fontWeight = FontWeight.Black,
                              color = MechanicalOrange
                            )
                          )
                        }

                        Button(
                          onClick = {
                            viewModel.shareAcousticDiagnosisWithWorkshop()
                          },
                          shape = RoundedCornerShape(8.dp),
                          colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                          Icon(Icons.Default.Send, contentDescription = null, tint = PureWhite, modifier = Modifier.size(14.dp))
                          Spacer(modifier = Modifier.width(4.dp))
                          Text(
                            text = "Transmettre Atelier",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                          )
                        }
                      }
                    }
                  }
                }
              }
            }

            // ==================== TAB 2: BIBLIOTHÈQUE DE SONS TÉMOINS ====================
            AcousticModalTab.BIBLIOTHEQUE_SONS -> {
              item {
                Text(
                  text = "ENCYCLOPÉDIE ACOUSTIQUE AUTOMOBILE (7 ANOMALIES COURANTES)",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = AluminumMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                  )
                )
              }

              items(AcousticSoundRepository.PRESETS) { preset ->
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                      viewModel.selectEngineSoundPreset(preset.id)
                      viewModel.stopAndAnalyzeEngineSound()
                      selectedTab = AcousticModalTab.ENREGISTREUR
                    },
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.dp, MetalBorder)
                ) {
                  Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(preset.severity.colorHex).copy(alpha = 0.2f),
                        border = BorderStroke(0.5.dp, Color(preset.severity.colorHex))
                      ) {
                        Text(
                          text = preset.category.label,
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(preset.severity.colorHex),
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp
                          ),
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                      }

                      Text(
                        text = "${preset.frequencyPeakHz} Hz • ${preset.decibelTypical} dB",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = TechCyan,
                          fontFamily = FontFamily.Monospace,
                          fontSize = 10.sp
                        )
                      )
                    }

                    Text(
                      text = preset.title,
                      style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                      )
                    )

                    Text(
                      text = preset.description,
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = AluminumLight,
                        fontSize = 11.5.sp
                      )
                    )

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = "Coût estimé : ${df.format(preset.estimatedRepairCostDzd)} DA",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = MechanicalOrange,
                          fontWeight = FontWeight.Bold
                        )
                      )

                      Button(
                        onClick = {
                          viewModel.selectEngineSoundPreset(preset.id)
                          viewModel.stopAndAnalyzeEngineSound()
                          selectedTab = AcousticModalTab.ENREGISTREUR
                        },
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GraphiteBlack),
                        border = BorderStroke(1.dp, TechCyan),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                      ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TechCyan, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tester l'analyse", color = TechCyan, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                      }
                    }
                  }
                }
              }
            }

            // ==================== TAB 3: HISTORIQUE ENREGISTRÉ ====================
            AcousticModalTab.HISTORIQUE -> {
              item {
                Text(
                  text = "ENREGISTREMENTS ACOUSTIQUES SAUVEGARDÉS",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = AluminumMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                  )
                )
              }

              if (uiState.soundRecordingHistory.isEmpty()) {
                item {
                  Text(
                    text = "Aucun enregistrement sonore dans l'historique.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = AluminumLight)
                  )
                }
              } else {
                items(uiState.soundRecordingHistory) { item ->
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MetalCard,
                    border = BorderStroke(1.dp, MetalBorder),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(
                      modifier = Modifier.padding(12.dp),
                      verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = item.title,
                          style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                          )
                        )
                        Text(
                          text = item.date,
                          style = MaterialTheme.typography.labelSmall.copy(color = AluminumGray, fontSize = 10.sp)
                        )
                      }

                      Text(
                        text = "Véhicule : ${item.vehicleModel} (${item.plateNumber})",
                        style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.sp)
                      )

                      Text(
                        text = "Panne détectée : ${item.diagnosedProblem}",
                        style = MaterialTheme.typography.bodySmall.copy(
                          color = Color(item.severity.colorHex),
                          fontWeight = FontWeight.Bold,
                          fontSize = 11.5.sp
                        )
                      )

                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = "Score : ${item.confidenceScorePercent}% • Durée : ${item.durationSeconds}s",
                          style = MaterialTheme.typography.labelSmall.copy(color = AluminumLight, fontSize = 10.sp)
                        )

                        if (item.isSharedWithWorkshop) {
                          Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ElectricGreen.copy(alpha = 0.15f),
                            border = BorderStroke(0.5.dp, ElectricGreen)
                          ) {
                            Text(
                              text = "Transmis à l'Atelier",
                              style = MaterialTheme.typography.labelSmall.copy(
                                color = ElectricGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                              ),
                              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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

        // --- 4. BOTTOM ACTION BAR ---
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = MetalCard,
          border = BorderStroke(0.5.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = onDismiss,
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, MetalBorder),
              modifier = Modifier.weight(1f)
            ) {
              Text("Fermer", color = AluminumLight)
            }

            Button(
              onClick = {
                if (hasAudioPermission) {
                  viewModel.startEngineSoundRecording()
                  selectedTab = AcousticModalTab.ENREGISTREUR
                } else {
                  permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
              modifier = Modifier.weight(1.4f)
            ) {
              Icon(Icons.Default.Mic, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Nouvel Enregistrement", color = PureWhite, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
          }
        }
      }
    }
  }
}
