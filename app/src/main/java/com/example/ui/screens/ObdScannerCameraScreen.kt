package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.MekanikPrimaryButton
import com.example.ui.components.MekanikSecondaryButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

@Composable
fun ObdScannerCameraScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  onBack: () -> Unit,
  onNavigateToCatalog: (DiagnosticRecommendedPart) -> Unit,
  onNavigateToQuote: (AiDiagnosticResult, DiagnosticRecommendedPart?) -> Unit,
  onNavigateToVendors: () -> Unit,
  modifier: Modifier = Modifier
) {
  val selectedPreset = uiState.selectedObdPreset
  var activeTab by remember { mutableStateOf(0) } // 0 = Scanner Viewfinder, 1 = Rapport IA & Pièces

  // Laser sweep animation
  val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
  val laserPosition by infiniteTransition.animateFloat(
    initialValue = 0.05f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "laser_sweep"
  )

  // OCR Reticle Pulsing
  val reticlePulse by infiniteTransition.animateFloat(
    initialValue = 0.92f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "reticle_pulse"
  )

  // AI Glow
  val aiGlow by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0.9f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "ai_glow"
  )

  LaunchedEffect(selectedPreset) {
    if (uiState.obdScannerAiResult == null && !uiState.isObdAiScanning) {
      viewModel.triggerGeminiAiAnalysisForObd(selectedPreset.primaryDtc, selectedPreset)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF070A0D))
      .testTag("obd_scanner_camera_screen")
  ) {
    // 1. High-Tech Top HUD Bar
    Surface(
      color = GraphiteBlack.copy(alpha = 0.95f),
      border = BorderStroke(0.5.dp, MetalBorder)
    ) {
      Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = onBack,
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MetalSurface)
                .testTag("btn_obd_scanner_back")
            ) {
              Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Retour",
                tint = PureWhite
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "SCANNER OBD-II",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = PureWhite
                  )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = TechCyan.copy(alpha = 0.2f),
                  border = BorderStroke(0.8.dp, TechCyan)
                ) {
                  Text(
                    text = "CAMÉRA OCR + IA",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = TechCyan,
                      fontWeight = FontWeight.Bold,
                      fontSize = 8.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                  )
                }
              }
              Text(
                text = "Reconnaissance d'écrans valises & tablettes diagnostic",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumMuted,
                  fontSize = 11.sp
                )
              )
            }
          }

          // Top Action Controls: Torch & Freeze-Frame Indicator
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            IconButton(
              onClick = { viewModel.toggleObdTorch() },
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (uiState.isObdTorchActive) WarningAmber.copy(alpha = 0.3f) else MetalSurface)
                .border(
                  0.8.dp,
                  if (uiState.isObdTorchActive) WarningAmber else MetalBorder,
                  CircleShape
                )
                .testTag("btn_obd_torch")
            ) {
              Icon(
                if (uiState.isObdTorchActive) Icons.Default.FlashOn else Icons.Default.FlashOff,
                contentDescription = "Torche",
                tint = if (uiState.isObdTorchActive) WarningAmber else AluminumGray,
                modifier = Modifier.size(18.dp)
              )
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (uiState.isObdCameraFreezeFrame) PerformanceRed.copy(alpha = 0.25f) else ElectricGreen.copy(alpha = 0.15f),
              border = BorderStroke(
                0.8.dp,
                if (uiState.isObdCameraFreezeFrame) PerformanceRedGlow else ElectricGreen
              )
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(if (uiState.isObdCameraFreezeFrame) PerformanceRedGlow else ElectricGreen)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                  text = if (uiState.isObdCameraFreezeFrame) "FIGÉ" else "LIVE OCR",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (uiState.isObdCameraFreezeFrame) PerformanceRedGlow else ElectricGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  )
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Physical Diagnostic Tool Presets Horizontal Selector
        Text(
          text = "CHOISIR L'ÉCRAN DE LA VALISE / APPAREIL À SCANNER :",
          style = MaterialTheme.typography.labelSmall.copy(
            color = AluminumMuted,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(ObdScreenPresetsProvider.presets) { preset ->
            val isSelected = preset.id == selectedPreset.id
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) PerformanceRed.copy(alpha = 0.2f) else MetalCard,
              border = BorderStroke(
                if (isSelected) 1.2.dp else 0.5.dp,
                if (isSelected) PerformanceRedGlow else MetalBorder
              ),
              modifier = Modifier
                .clickable { viewModel.selectObdPreset(preset) }
                .testTag("preset_${preset.id}")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) PerformanceRedGlow else AluminumMuted)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                  Text(
                    text = preset.toolBrand,
                    style = MaterialTheme.typography.labelMedium.copy(
                      color = if (isSelected) PureWhite else AluminumLight,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                      fontSize = 11.sp
                    )
                  )
                  Text(
                    text = "Code ${preset.primaryDtc}",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = if (isSelected) PerformanceRedGlow else AluminumMuted,
                      fontSize = 9.5.sp,
                      fontWeight = FontWeight.Bold
                    )
                  )
                }
              }
            }
          }
        }
      }
    }

    // View Switcher Tabs (0: Caméra & Viseur OCR | 1: Rapport IA & Pièces Recommandées)
    TabRow(
      selectedTabIndex = activeTab,
      containerColor = GraphiteBlack,
      contentColor = PureWhite,
      divider = { HorizontalDivider(color = MetalBorder, thickness = 0.8.dp) }
    ) {
      Tab(
        selected = activeTab == 0,
        onClick = { activeTab = 0 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Viseur Caméra OCR", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        },
        selectedContentColor = TechCyan,
        unselectedContentColor = AluminumGray
      )
      Tab(
        selected = activeTab == 1,
        onClick = { activeTab = 1 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Diagnostic Gemini IA & Pièces", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        },
        selectedContentColor = PerformanceRedGlow,
        unselectedContentColor = AluminumGray
      )
    }

    // Main Content
    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
      if (activeTab == 0) {
        ObdCameraViewfinderContent(
          preset = selectedPreset,
          uiState = uiState,
          viewModel = viewModel,
          laserPosition = laserPosition,
          reticlePulse = reticlePulse,
          onTriggerAi = {
            viewModel.triggerGeminiAiAnalysisForObd(selectedPreset.primaryDtc, selectedPreset)
            activeTab = 1
          },
          onOpenAiReport = { activeTab = 1 }
        )
      } else {
        ObdAiDiagnosticResultsContent(
          uiState = uiState,
          preset = selectedPreset,
          viewModel = viewModel,
          aiGlow = aiGlow,
          onNavigateToCatalog = onNavigateToCatalog,
          onNavigateToQuote = onNavigateToQuote,
          onNavigateToVendors = onNavigateToVendors,
          onSwitchToCamera = { activeTab = 0 }
        )
      }
    }
  }
}

@Composable
private fun ObdCameraViewfinderContent(
  preset: ObdPhysicalScreenPreset,
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  laserPosition: Float,
  reticlePulse: Float,
  onTriggerAi: () -> Unit,
  onOpenAiReport: () -> Unit
) {
  Box(modifier = Modifier.fillMaxSize().background(Color(0xFF040608))) {
    // 1. Simulated Physical Camera View with Realistic Physical Diagnostic Screen
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Vehicle Context Banner
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = MetalCard.copy(alpha = 0.85f),
        border = BorderStroke(0.6.dp, MetalBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "${uiState.activeVehicle.brand} ${uiState.activeVehicle.model} • ${uiState.activeVehicle.engineCode}",
              style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp)
            )
          }
          Text(
            text = "VIN: ${uiState.activeVehicle.vin.take(8)}...",
            style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // The Optical HUD Camera Reticle & Target Area
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF0B1015))
          .border(1.dp, MetalBorder, RoundedCornerShape(16.dp))
          .testTag("camera_viewfinder_viewport")
      ) {
        // Render Physical Screen Simulation (Autel, VCDS, Launch, Bosch, etc.)
        PhysicalScreenRenderer(preset = preset, zoomLevel = uiState.obdZoomLevel)

        // Torch Light Glow Simulation
        if (uiState.isObdTorchActive) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                brush = Brush.radialGradient(
                  colors = listOf(WarningAmber.copy(alpha = 0.22f), Color.Transparent),
                  radius = 700f
                )
              )
          )
        }

        // Animated Optical Scanning Laser Line
        Canvas(modifier = Modifier.fillMaxSize()) {
          val y = size.height * laserPosition
          drawLine(
            brush = Brush.horizontalGradient(
              colors = listOf(
                Color.Transparent,
                TechCyan.copy(alpha = 0.85f),
                PureWhite,
                TechCyan.copy(alpha = 0.85f),
                Color.Transparent
              )
            ),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 3.dp.toPx()
          )

          // Laser glow halo
          drawRect(
            brush = Brush.verticalGradient(
              colors = listOf(
                TechCyan.copy(alpha = 0.15f),
                TechCyan.copy(alpha = 0.02f),
                Color.Transparent
              ),
              startY = y - 30.dp.toPx(),
              endY = y + 30.dp.toPx()
            ),
            topLeft = Offset(0f, y - 30.dp.toPx()),
            size = Size(size.width, 60.dp.toPx())
          )

          // 4 HUD Corner Brackets
          val bracketLen = 28.dp.toPx()
          val stroke = 3.2.dp.toPx()
          val inset = 12.dp.toPx()

          // Top Left
          drawLine(TechCyan, Offset(inset, inset), Offset(inset + bracketLen, inset), stroke)
          drawLine(TechCyan, Offset(inset, inset), Offset(inset, inset + bracketLen), stroke)

          // Top Right
          drawLine(TechCyan, Offset(size.width - inset, inset), Offset(size.width - inset - bracketLen, inset), stroke)
          drawLine(TechCyan, Offset(size.width - inset, inset), Offset(size.width - inset, inset + bracketLen), stroke)

          // Bottom Left
          drawLine(TechCyan, Offset(inset, size.height - inset), Offset(inset + bracketLen, size.height - inset), stroke)
          drawLine(TechCyan, Offset(inset, size.height - inset), Offset(inset, size.height - inset - bracketLen), stroke)

          // Bottom Right
          drawLine(TechCyan, Offset(size.width - inset, size.height - inset), Offset(size.width - inset - bracketLen, size.height - inset), stroke)
          drawLine(TechCyan, Offset(size.width - inset, size.height - inset), Offset(size.width - inset, size.height - inset - bracketLen), stroke)

          // Center crosshair
          val centerX = size.width / 2
          val centerY = size.height / 2
          val crossLen = 10.dp.toPx()
          drawLine(AluminumMuted.copy(alpha = 0.5f), Offset(centerX - crossLen, centerY), Offset(centerX + crossLen, centerY), 1.5.dp.toPx())
          drawLine(AluminumMuted.copy(alpha = 0.5f), Offset(centerX, centerY - crossLen), Offset(centerX, centerY + crossLen), 1.5.dp.toPx())
        }

        // Dynamic Real-Time OCR Bounding Box over the primary DTC
        Box(
          modifier = Modifier
            .align(Alignment.Center)
            .offset(y = (-30).dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.65f))
            .border(1.dp, MekanikBrandOrange, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(PerformanceRedGlow)
            )
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "CODE OBD DÉTECTÉ :",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = AluminumGray,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold
                  )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(3.dp),
                  color = ElectricGreen.copy(alpha = 0.25f)
                ) {
                  Text(
                    text = "OCR ${uiState.obdOcrConfidence}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = ElectricGreen,
                      fontWeight = FontWeight.Black,
                      fontSize = 8.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }
              Text(
                text = preset.primaryDtc,
                style = MaterialTheme.typography.titleLarge.copy(
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontFamily = FontFamily.Monospace,
                  letterSpacing = 2.sp
                )
              )
            }
          }
        }

        // Live Optical Telemetry Overlay
        Row(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .background(
              Brush.verticalGradient(
                listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
              )
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "HUD: 4K UHD • 60 FPS • RAW OCR",
            style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
          )
          Text(
            text = "CALCULATEUR: ${preset.ecuModule.take(20)}...",
            style = MaterialTheme.typography.labelSmall.copy(color = TechCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Bottom Camera & AI Controls
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Zoom Controls
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          listOf(1.0f to "1x", 1.5f to "1.5x", 2.0f to "2x", 3.0f to "3x").forEach { (zoom, label) ->
            val isSelected = uiState.obdZoomLevel == zoom
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (isSelected) TechCyan.copy(alpha = 0.25f) else MetalSurface,
              border = BorderStroke(0.8.dp, if (isSelected) TechCyan else MetalBorder),
              modifier = Modifier.clickable { viewModel.setObdZoomLevel(zoom) }
            ) {
              Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (isSelected) TechCyan else AluminumGray,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
              )
            }
          }
        }

        // Shutter / Freeze-Frame Capture Button
        IconButton(
          onClick = { viewModel.toggleObdFreezeFrame() },
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(PerformanceRed, PerformanceRedGlow, MetalSurface)))
            .border(2.dp, PureWhite, CircleShape)
            .testTag("btn_obd_shutter")
        ) {
          Icon(
            if (uiState.isObdCameraFreezeFrame) Icons.Default.PlayArrow else Icons.Default.Camera,
            contentDescription = "Capture Freeze Frame",
            tint = PureWhite,
            modifier = Modifier.size(26.dp)
          )
        }

        // Switch to AI Report Button
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = PerformanceRed.copy(alpha = 0.2f),
          border = BorderStroke(1.dp, PerformanceRedGlow),
          modifier = Modifier.clickable { onTriggerAi() }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text("ANALYSER IA", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 11.sp)
              Text("Gemini 3.5 Flash", color = MechanicalOrange, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun PhysicalScreenRenderer(
  preset: ObdPhysicalScreenPreset,
  zoomLevel: Float
) {
  val bgColor = when (preset.toolTheme) {
    DiagnosticToolTheme.AUTEL_MAXISYS -> Color(0xFF0F1E36)
    DiagnosticToolTheme.VCDS_VAGCOM -> Color(0xFFD4D0C8)
    DiagnosticToolTheme.LAUNCH_X431 -> Color(0xFF1E1114)
    DiagnosticToolTheme.BOSCH_KTS -> Color(0xFF0A2218)
    DiagnosticToolTheme.DELPHI_DS150 -> Color(0xFF161E28)
    DiagnosticToolTheme.DASHBOARD_CLUSTER -> Color(0xFF151008)
  }

  val textColor = when (preset.toolTheme) {
    DiagnosticToolTheme.VCDS_VAGCOM -> Color(0xFF000000)
    else -> Color(0xFFE6EDF5)
  }

  val accentColor = when (preset.toolTheme) {
    DiagnosticToolTheme.AUTEL_MAXISYS -> Color(0xFF3897F0)
    DiagnosticToolTheme.VCDS_VAGCOM -> Color(0xFF000080)
    DiagnosticToolTheme.LAUNCH_X431 -> Color(0xFFE53935)
    DiagnosticToolTheme.BOSCH_KTS -> Color(0xFF00C853)
    DiagnosticToolTheme.DELPHI_DS150 -> Color(0xFFFF7A00)
    DiagnosticToolTheme.DASHBOARD_CLUSTER -> Color(0xFFFFB300)
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(bgColor)
      .padding(12.dp)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Diagnostic Tool Header Title Bar
      Surface(
        color = accentColor.copy(alpha = if (preset.toolTheme == DiagnosticToolTheme.VCDS_VAGCOM) 1f else 0.35f),
        shape = RoundedCornerShape(4.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = preset.toolBrand,
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (preset.toolTheme == DiagnosticToolTheme.VCDS_VAGCOM) PureWhite else accentColor,
              fontWeight = FontWeight.Black,
              fontSize = 10.sp
            )
          )
          Text(
            text = preset.toolSoftware,
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (preset.toolTheme == DiagnosticToolTheme.VCDS_VAGCOM) PureWhite.copy(alpha = 0.8f) else AluminumMuted,
              fontSize = 8.5.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = preset.screenSubtitle,
        style = MaterialTheme.typography.labelSmall.copy(
          color = textColor.copy(alpha = 0.7f),
          fontSize = 9.5.sp,
          fontWeight = FontWeight.Bold
        )
      )

      Spacer(modifier = Modifier.height(8.dp))

      // DTC Fault Code Highlight Card inside the tool
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (preset.toolTheme == DiagnosticToolTheme.VCDS_VAGCOM) Color.White else Color.Black.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, accentColor)
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "DTC: ${preset.primaryDtc}",
              style = MaterialTheme.typography.titleMedium.copy(
                color = if (preset.toolTheme == DiagnosticToolTheme.VCDS_VAGCOM) Color(0xFFCC0000) else PerformanceRedGlow,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp
              )
            )
            Surface(
              shape = RoundedCornerShape(3.dp),
              color = PerformanceRed.copy(alpha = 0.2f)
            ) {
              Text(
                text = preset.statusBadge,
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (preset.toolTheme == DiagnosticToolTheme.VCDS_VAGCOM) Color(0xFF990000) else PerformanceRedGlow,
                  fontWeight = FontWeight.Bold,
                  fontSize = 8.5.sp
                ),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
              )
            }
          }

          Text(
            text = preset.dtcStandardTitle,
            style = MaterialTheme.typography.bodySmall.copy(
              color = textColor,
              fontWeight = FontWeight.Bold,
              fontSize = 11.5.sp
            )
          )

          Text(
            text = preset.dtcFullDescription,
            style = MaterialTheme.typography.bodySmall.copy(
              color = textColor.copy(alpha = 0.8f),
              fontSize = 10.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Freeze-Frame Sensor Parameters Table
      Text(
        text = "FREEZE FRAME (PARAMÈTRES CAPTEURS AU MOMENT DU DÉFAUT) :",
        style = MaterialTheme.typography.labelSmall.copy(
          color = textColor.copy(alpha = 0.7f),
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        )
      )
      Spacer(modifier = Modifier.height(4.dp))

      preset.freezeFrameData.entries.take(4).forEach { (param, value) ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "• $param",
            style = MaterialTheme.typography.bodySmall.copy(
              color = textColor.copy(alpha = 0.85f),
              fontSize = 10.sp
            )
          )
          Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
              color = if (preset.toolTheme == DiagnosticToolTheme.VCDS_VAGCOM) Color(0xFF000080) else TechCyan,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              fontSize = 10.5.sp
            )
          )
        }
      }
    }
  }
}

@Composable
private fun ObdAiDiagnosticResultsContent(
  uiState: MekanikUiState,
  preset: ObdPhysicalScreenPreset,
  viewModel: MekanikViewModel,
  aiGlow: Float,
  onNavigateToCatalog: (DiagnosticRecommendedPart) -> Unit,
  onNavigateToQuote: (AiDiagnosticResult, DiagnosticRecommendedPart?) -> Unit,
  onNavigateToVendors: () -> Unit,
  onSwitchToCamera: () -> Unit
) {
  val result = uiState.obdScannerAiResult
  val isLoading = uiState.isObdAiScanning

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("obd_ai_results_content"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. AI Analysis Header Banner
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(TechCyan.copy(alpha = aiGlow * 0.4f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TechCyan, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "EXPERTISE MEKANIK AI",
                  style = MaterialTheme.typography.titleMedium.copy(
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                  )
                )
                Text(
                  text = "Moteur Gemini 3.5 Flash • Analyse immédiate",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 11.sp)
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = PerformanceRed.copy(alpha = 0.2f),
              border = BorderStroke(0.8.dp, PerformanceRedGlow)
            ) {
              Text(
                text = "CODE ${uiState.obdDetectedDtc}",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = PerformanceRedGlow,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Véhicule analysé : ${uiState.activeVehicle.brand} ${uiState.activeVehicle.model} ${uiState.activeVehicle.displacement} (${uiState.activeVehicle.engineCode})",
            style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 12.sp)
          )
        }
      }
    }

    if (isLoading) {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(0.8.dp, TechCyan.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            CircularProgressIndicator(color = TechCyan, strokeWidth = 3.dp)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "Analyse en cours par Gemini 3.5 Flash...",
              style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Extraction des données atelier & corrélation des causes de pannes...",
              style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 11.sp)
            )
          }
        }
      }
    } else if (result != null) {
      // 2. Plain Language Explanation
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(0.8.dp, MetalBorder)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Info, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "EXPLICATION VULGARISÉE POUR LE CLIENT",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = TechCyan,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                )
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = result.plainLanguageExplanation,
              style = MaterialTheme.typography.bodyMedium.copy(
                color = OffWhite,
                lineHeight = 20.sp,
                fontSize = 13.sp
              )
            )
          }
        }
      }

      // 3. Technical Summary & Root Causes
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(0.8.dp, MetalBorder)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Troubleshoot, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "CAUSES PROBABLES & ORIGINES DE LA DÉFAILLANCE",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = PerformanceRedGlow,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                )
              )
            }
            Spacer(modifier = Modifier.height(10.dp))

            result.rootCauses.forEachIndexed { index, cause ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
              ) {
                Surface(
                  shape = CircleShape,
                  color = PerformanceRed.copy(alpha = 0.25f),
                  modifier = Modifier.size(20.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(
                      text = "${index + 1}",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = PerformanceRedGlow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                      )
                    )
                  }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = cause,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    color = OffWhite,
                    fontSize = 12.5.sp
                  )
                )
              }
            }
          }
        }
      }

      // 4. Workshop Inspection & Tolerances
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(0.8.dp, MetalBorder)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Build, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "CONTRÔLES ATELIER & VALEURS MULTIMÈTRE / PRESSION",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = MechanicalOrange,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                )
              )
            }
            Spacer(modifier = Modifier.height(10.dp))

            result.stepByStepInspection.forEach { step ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 3.dp),
                verticalAlignment = Alignment.Top
              ) {
                Icon(
                  Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = ElectricGreen,
                  modifier = Modifier.size(15.dp).offset(y = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = step,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = AluminumLight,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                  )
                )
              }
            }

            result.multimeterSpecs?.let { specs ->
              Spacer(modifier = Modifier.height(8.dp))
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = GraphiteBlack,
                border = BorderStroke(0.6.dp, TechCyan.copy(alpha = 0.5f))
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.Speed, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Valeurs cibles : $specs",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = TechCyan,
                      fontSize = 11.5.sp,
                      fontWeight = FontWeight.SemiBold
                    )
                  )
                }
              }
            }
          }
        }
      }

      // 5. Recommended Replacement Parts with Direct Catalog & Quote CTA
      item {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "PIÈCES DE RECHANGE RECOMMANDÉES :",
              style = MaterialTheme.typography.labelMedium.copy(
                color = AluminumMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            )
            Text(
              text = "${result.richRecommendedParts.size} pièces OEM",
              style = MaterialTheme.typography.labelSmall.copy(color = PerformanceRedGlow, fontWeight = FontWeight.Bold)
            )
          }
          Spacer(modifier = Modifier.height(8.dp))

          result.richRecommendedParts.forEach { part ->
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(0.8.dp, MetalBorder),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = part.name,
                      style = MaterialTheme.typography.titleSmall.copy(
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                      )
                    )
                    Text(
                      text = "Réf OEM : ${part.oemReference} • ${part.brandName}",
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = TechCyan,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                      )
                    )
                  }
                  Text(
                    text = "${part.priceEstimatedDzd} DZD",
                    style = MaterialTheme.typography.titleMedium.copy(
                      color = PerformanceRedGlow,
                      fontWeight = FontWeight.Black,
                      fontSize = 14.sp
                    )
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons for this Part
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Button(
                    onClick = { onNavigateToCatalog(part) },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MetalSurface),
                    border = BorderStroke(0.8.dp, MetalBorder),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = AluminumLight, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Catalogue", color = AluminumLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }

                  Button(
                    onClick = { onNavigateToQuote(result, part) },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, tint = PureWhite, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Devis Atelier", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
      }

      // Bottom Global Action Buttons
      item {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          MekanikSecondaryButton(
            text = "📷 Scanner un autre écran",
            onClick = onSwitchToCamera,
            modifier = Modifier.weight(1f)
          )
          MekanikPrimaryButton(
            text = "🏪 Trouver Vendeurs",
            onClick = onNavigateToVendors,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }
  }
}
