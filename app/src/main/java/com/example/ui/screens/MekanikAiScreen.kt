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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DiagnosticRecommendedPart
import com.example.data.model.DiagnosticTechDocType
import com.example.data.model.DiagnosticTechnicalDoc
import com.example.data.model.SystemCategory
import com.example.ui.components.MekanikPrimaryButton
import com.example.ui.components.MekanikSecondaryButton
import com.example.ui.components.OfflineCacheHubTab
import com.example.ui.components.WorkshopOfflineCacheStatusBanner
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainNavigationTab
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

enum class ObdCategoryFilter(val label: String, val prefix: String) {
  ALL("Tous les codes", ""),
  POWERTRAIN("P • Moteur / Turbo / Échappement", "P"),
  CHASSIS("C • Châssis / Freinage / ABS", "C"),
  BODY("B • Carrosserie / Habitacle / Airbag", "B"),
  NETWORK("U • Réseau CAN-Bus / Calculateurs", "U")
}

@Composable
fun MekanikAiScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  onNavigateToObdScanner: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val focusManager = LocalFocusManager.current
  var selectedCategoryFilter by remember { mutableStateOf(ObdCategoryFilter.ALL) }
  val checkedDiagnosticSteps = remember { mutableStateMapOf<Int, Boolean>() }
  val networkCondition by viewModel.workshopNetworkCondition.collectAsStateWithLifecycle()
  val cacheSummary by viewModel.localCacheSummary.collectAsStateWithLifecycle()

  // Reset checklist when diagnostic result changes
  LaunchedEffect(uiState.aiDiagnosticResult?.query) {
    checkedDiagnosticSteps.clear()
  }

  //Progressive staggered reveal states for the diagnostic result sections
  var revealStage by remember { mutableIntStateOf(0) }
  LaunchedEffect(uiState.isAiLoading, uiState.aiDiagnosticResult) {
    if (!uiState.isAiLoading && uiState.aiDiagnosticResult != null) {
      revealStage = 1
      kotlinx.coroutines.delay(90)
      revealStage = 2
      kotlinx.coroutines.delay(90)
      revealStage = 3
      kotlinx.coroutines.delay(90)
      revealStage = 4
      kotlinx.coroutines.delay(90)
      revealStage = 5
    } else {
      revealStage = 0
    }
  }

  val infiniteTransition = rememberInfiniteTransition(label = "ai_pulse")
  val haloAlpha by infiniteTransition.animateFloat(
    initialValue = 0.25f,
    targetValue = 0.78f,
    animationSpec = infiniteRepeatable(
      animation = tween(1600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "halo_alpha"
  )
  val gearRotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(14000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "gear_rotation"
  )
  val particlePhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 6.283185f,
    animationSpec = infiniteRepeatable(
      animation = tween(4200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "particle_phase"
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("mekanik_ai_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 0. Local Cache Status Banner (Diagnostics Enregistrés, Pannes Clients & Schémas 0ms)
    item {
      WorkshopOfflineCacheStatusBanner(
        networkCondition = networkCondition,
        cacheSummary = cacheSummary,
        onOpenCacheHub = { tab -> viewModel.openOfflineCacheHubModal(tab) },
        onCycleNetworkCondition = { viewModel.cycleWorkshopNetworkCondition() }
      )
    }

    // 1. Glowing AI Hero Header: MekanikAI (Brain + Gear + Subtle Particle Halo) + Vehicle Context
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Box(modifier = Modifier.fillMaxWidth()) {
          // Subtle animated halo Canvas background
          androidx.compose.foundation.Canvas(
            modifier = Modifier
              .matchParentSize()
              .heightIn(min = 210.dp)
          ) {
            val cx = size.width / 2f
            val cy = 58.dp.toPx()
            drawCircle(
              brush = Brush.radialGradient(
                colors = listOf(
                  MekanikBrandOrange.copy(alpha = haloAlpha * 0.18f),
                  Color.Transparent
                ),
                center = androidx.compose.ui.geometry.Offset(cx, cy),
                radius = size.minDimension * 0.65f
              ),
              center = androidx.compose.ui.geometry.Offset(cx, cy),
              radius = size.minDimension * 0.65f
            )

            // Subtle floating neural particles around the MekanikAI emblem
            val particleCount = 10
            for (i in 0 until particleCount) {
              val angle = (i * (6.283185f / particleCount)) + particlePhase * (if (i % 2 == 0) 0.5f else -0.35f)
              val dist = (44.dp.toPx()) + (i % 3) * 18.dp.toPx()
              val px = cx + kotlin.math.cos(angle) * dist * 1.45f
              val py = cy + kotlin.math.sin(angle) * dist * 0.65f
              drawCircle(
                color = if (i % 3 == 0) Color(0xFFFF7A00).copy(alpha = 0.55f) else Color(0xFF00E5FF).copy(alpha = 0.6f),
                radius = if (i % 2 == 0) 2.6.dp.toPx() else 1.8.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(px, py)
              )
            }
          }

          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // AI Glowing Emblem (Brain + Rotating Mechanical Gear Ring + Radial Halo)
            Box(
              modifier = Modifier.size(84.dp),
              contentAlignment = Alignment.Center
            ) {
              androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val r = size.minDimension / 2f
                val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
                drawCircle(
                  color = Color(0xFF00E5FF).copy(alpha = haloAlpha * 0.45f),
                  radius = r,
                  style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )
                // Gear teeth around the Brain emblem
                val teeth = 12
                for (t in 0 until teeth) {
                  val deg = gearRotation + t * (360f / teeth)
                  val rad = Math.toRadians(deg.toDouble())
                  val innerR = r * 0.74f
                  val outerR = r * 0.90f
                  val start = androidx.compose.ui.geometry.Offset(
                    center.x + (kotlin.math.cos(rad) * innerR).toFloat(),
                    center.y + (kotlin.math.sin(rad) * innerR).toFloat()
                  )
                  val end = androidx.compose.ui.geometry.Offset(
                    center.x + (kotlin.math.cos(rad) * outerR).toFloat(),
                    center.y + (kotlin.math.sin(rad) * outerR).toFloat()
                  )
                  drawLine(
                    color = if (t % 2 == 0) Color(0xFF00E5FF) else Color(0xFFFF7A00),
                    start = start,
                    end = end,
                    strokeWidth = 3.5.dp.toPx()
                  )
                }
              }

              Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = MetalCard,
                border = BorderStroke(1.5.dp, TechCyan)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    Icons.Default.Psychology,
                    contentDescription = "MekanikAI Brain + Gear",
                    tint = PureWhite,
                    modifier = Modifier.size(30.dp)
                  )
                  Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    tint = MechanicalOrange,
                    modifier = Modifier
                      .size(16.dp)
                      .align(Alignment.BottomEnd)
                      .padding(2.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "MEKANIK",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp,
                  color = PureWhite
                )
              )
              Text(
                text = "AI",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp,
                  color = TechCyan
                )
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = PerformanceRed.copy(alpha = 0.2f),
                border = BorderStroke(0.8.dp, PerformanceRed)
              ) {
                Text(
                  text = "ANALYSEUR OBD-II & DTC",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = PerformanceRedGlow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                  ),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Text(
              text = "Décodage intelligent des codes défauts DTC (P, C, B, U), causes probables, étapes de contrôle & pièces OEM",
              style = MaterialTheme.typography.bodySmall.copy(
                color = AluminumGray,
                fontSize = 11.5.sp
              )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Current Vehicle Context Badge
            val v = uiState.activeVehicle
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MetalSurface,
              border = BorderStroke(0.5.dp, MetalBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = TechCyan,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = "VÉHICULE CONNECTÉ AU CALCULATEUR",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = AluminumMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                      )
                    )
                    Text(
                      text = "${v.brand} ${v.model} (${v.exactYear}) • ${v.engineCode}",
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = PureWhite,
                        fontWeight = FontWeight.Bold
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
                    text = "OBD-II Prêt",
                    color = ElectricGreen,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // 2. OBD-II DTC Input Card, Live DTC Structure Breakdown & Standard Code Families (Put FIRST so DTC analysis is immediate)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.5.dp, TechCyan.copy(alpha = 0.75f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Memory,
                contentDescription = null,
                tint = TechCyan,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "ANALYSEUR DE CODE DÉFAUT OBD-II (DTC)",
                style = MaterialTheme.typography.labelMedium.copy(
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.8.sp
                )
              )
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = TechCyan.copy(alpha = 0.16f),
              border = BorderStroke(0.8.dp, TechCyan)
            ) {
              Text(
                text = "ISO 15031-6 / SAE J2012",
                color = TechCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = uiState.aiQuery,
            onValueChange = { viewModel.onAiQueryChanged(it) },
            placeholder = {
              Text(
                "Saisissez un code DTC (ex: P0299, P0401, C0035, B0001, U0100) ou un symptôme...",
                color = AluminumMuted,
                fontSize = 12.5.sp
              )
            },
            leadingIcon = {
              Icon(
                Icons.Default.QrCodeScanner,
                contentDescription = null,
                tint = TechCyan
              )
            },
            trailingIcon = {
              if (uiState.aiQuery.isNotEmpty()) {
                IconButton(onClick = { viewModel.onAiQueryChanged("") }) {
                  Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = AluminumGray)
                }
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("ai_query_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MetalSurface,
              unfocusedContainerColor = MetalSurface,
              focusedBorderColor = TechCyan,
              unfocusedBorderColor = MetalBorder,
              focusedTextColor = PureWhite,
              unfocusedTextColor = OffWhite
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
              onSend = {
                focusManager.clearFocus()
                viewModel.askMekanikAi()
              }
            )
          )

          // Live Interactive DTC 5-Character Breakdown (when input or current result contains a P/C/B/U code)
          val activeDtcCode = remember(uiState.aiQuery, uiState.aiDiagnosticResult?.faultCode) {
            val fromInput = Regex("\\b([PCBU]\\d{4})\\b").find(uiState.aiQuery.uppercase())?.groupValues?.getOrNull(1)
            fromInput ?: uiState.aiDiagnosticResult?.faultCode ?: "P0299"
          }

          Spacer(modifier = Modifier.height(10.dp))

          ObdDtcStructureDecoderBanner(dtcCode = activeDtcCode)

          Spacer(modifier = Modifier.height(12.dp))

          // Filter by OBD family tabs
          Text(
            text = "FILTRER PAR FAMILLE DE CALCULATEUR OBD-II :",
            style = MaterialTheme.typography.labelSmall.copy(
              color = AluminumMuted,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          )
          Spacer(modifier = Modifier.height(6.dp))

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(ObdCategoryFilter.entries) { cat ->
              val isSelected = selectedCategoryFilter == cat
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) TechCyan else MetalSurface,
                border = BorderStroke(1.dp, if (isSelected) TechCyan else MetalBorder),
                onClick = { selectedCategoryFilter = cat }
              ) {
                Text(
                  text = cat.label,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) GraphiteBlack else AluminumLight,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 10.5.sp
                  ),
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Suggested OBD Codes list based on filter
          Text(
            text = "CODES DÉFAUTS DTC FRÉQUENTS (CLIQUEZ POUR ANALYSER) :",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MechanicalOrange,
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold
            )
          )
          Spacer(modifier = Modifier.height(6.dp))

          val chipsList = remember(selectedCategoryFilter) {
            when (selectedCategoryFilter) {
              ObdCategoryFilter.ALL -> listOf(
                Pair("P0299 • Turbo Basse Pression", "P0299"),
                Pair("P0401 • Débit EGR Insuffisant", "P0401"),
                Pair("P0300 • Ratés Allumage Cylindres", "P0300"),
                Pair("P2002 • Colmatage FAP / DPF", "P2002"),
                Pair("P0087 • Pression Rampe Basse", "P0087"),
                Pair("P0420 • Efficacité Catalyseur", "P0420"),
                Pair("P0101 • Signal Débitmètre MAF", "P0101"),
                Pair("P0171 • Mélange Trop Pauvre", "P0171"),
                Pair("P0340 • Capteur Arbre à Cames", "P0340"),
                Pair("C0035 • Capteur ABS Roue AV G", "C0035"),
                Pair("B0001 • Airbag Conducteur SRS", "B0001"),
                Pair("U0100 • Perte Comm. Calculateur ECM", "U0100")
              )
              ObdCategoryFilter.POWERTRAIN -> listOf(
                Pair("P0299 • Turbo Basse Pression", "P0299"),
                Pair("P0401 • Débit EGR Insuffisant", "P0401"),
                Pair("P0300 • Ratés Allumage Cylindres", "P0300"),
                Pair("P2002 • Colmatage FAP / DPF", "P2002"),
                Pair("P0087 • Pression Rail Basse", "P0087"),
                Pair("P0420 • Catalyseur Seuil Bas", "P0420"),
                Pair("P0101 • Débitmètre MAF", "P0101"),
                Pair("P0171 • Mélange Pauvre", "P0171"),
                Pair("P0340 • Capteur AAC", "P0340")
              )
              ObdCategoryFilter.CHASSIS -> listOf(
                Pair("C0035 • Capteur Vitesse Roue AV G", "C0035"),
                Pair("C0040 • Capteur Vitesse Roue AV D", "C0040"),
                Pair("C1288 • Capteur Pression Maître Cylindre", "C1288"),
                Pair("Vibration freinage", "Bruit au freinage")
              )
              ObdCategoryFilter.BODY -> listOf(
                Pair("B0001 • Détonateur Airbag Conducteur", "B0001"),
                Pair("B1000 • Calculateur Airbag Défaut", "B1000"),
                Pair("B1325 • Tension Batterie BSI Basse", "B1325")
              )
              ObdCategoryFilter.NETWORK -> listOf(
                Pair("U0100 • Perte Communication Calculateur Moteur (ECM)", "U0100"),
                Pair("U0121 • Perte Communication ABS/ESP", "U0121"),
                Pair("U0001 • Bus Haute Vitesse CAN-Bus Défaut", "U0001")
              )
            }
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            chipsList.forEach { (label, code) ->
              ObdQuickChip(
                label = label,
                code = code,
                isSelected = uiState.aiQuery.equals(code, ignoreCase = true) || uiState.aiDiagnosticResult?.faultCode.equals(code, ignoreCase = true)
              ) {
                focusManager.clearFocus()
                viewModel.askMekanikAi(it)
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          MekanikPrimaryButton(
            text = if (uiState.isAiLoading) "ANALYSE DU PROBLÈME EN COURS..." else "ANALYSER LE CODE OBD-II AVEC MEKANIK AI",
            onClick = {
              focusManager.clearFocus()
              viewModel.askMekanikAi()
            },
            icon = Icons.Default.AutoFixHigh,
            enabled = !uiState.isAiLoading,
            modifier = Modifier.fillMaxWidth(),
            testTag = "btn_submit_ai"
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Sonar IA Acoustic Diagnostic trigger
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = GraphiteBlack,
            border = BorderStroke(1.dp, PerformanceRedGlow),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.openEngineSoundRecorder() }
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PerformanceRedDark)
                    .border(1.dp, PerformanceRedGlow, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = PerformanceRedGlow,
                    modifier = Modifier.size(20.dp)
                  )
                }

                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "DIAGNOSTIC ACOUSTIQUE MOTEUR",
                      style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = PureWhite
                      )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = PerformanceRedDark.copy(alpha = 0.5f),
                      border = BorderStroke(0.5.dp, PerformanceRed)
                    ) {
                      Text(
                        text = "Microphone IA",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = PerformanceRedGlow,
                          fontWeight = FontWeight.Bold,
                          fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                      )
                    }
                  }
                  Text(
                    text = "Enregistrer le bruit suspect pour identifier coussinets, chaîne, turbo ou injecteurs",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = AluminumLight,
                      fontSize = 10.5.sp
                    )
                  )
                }
              }

              Icon(Icons.Default.ChevronRight, contentDescription = null, tint = PerformanceRedGlow)
            }
          }
        }
      }
    }

    // 3. Scanning Animation (Automotive Radar Pulse + Brain & Gear)
    if (uiState.isAiLoading) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
          border = BorderStroke(1.5.dp, TechCyan)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier.size(64.dp),
              contentAlignment = Alignment.Center
            ) {
              CircularProgressIndicator(
                color = TechCyan,
                modifier = Modifier.size(60.dp),
                strokeWidth = 3.dp
              )
              Icon(
                Icons.Default.Psychology,
                contentDescription = null,
                tint = MechanicalOrange,
                modifier = Modifier.size(28.dp)
              )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "« Analyse du problème en cours… »",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = TechCyanGlow
              )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Interrogation du calculateur via MekanikAI (Gemini 3.8 Flash) • Décodage de la trame OBD-II, extraction des causes probables, procédures multimètre et références OEM...",
              style = MaterialTheme.typography.bodySmall.copy(
                color = AluminumLight,
                fontSize = 11.5.sp
              )
            )
          }
        }
      }
    }

    // 4. Complete AI Diagnostic Result (Progressive Staggered Reveal)
    if (!uiState.isAiLoading && uiState.aiDiagnosticResult != null) {
      val result = uiState.aiDiagnosticResult!!

      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_result_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            // Header Result Badge & Severity
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = TechCyan.copy(alpha = 0.2f),
                  border = BorderStroke(1.dp, TechCyan)
                ) {
                  Text(
                    text = if (result.isObdCode) "RAPPORT DTC OBD-II" else "ANALYSE SYMPTÔME IA",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = TechCyan,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = result.query.uppercase(),
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = PureWhite
                  )
                )
              }

              // Severity Badge
              val severity = result.severityLevel
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = when {
                  severity.contains("Critique", true) || severity.contains("Haute", true) || severity.contains("Élevée", true) ->
                    PerformanceRed.copy(alpha = 0.2f)
                  severity.contains("Moyenne", true) -> MechanicalOrange.copy(alpha = 0.2f)
                  else -> ElectricGreen.copy(alpha = 0.2f)
                },
                border = BorderStroke(
                  1.dp,
                  when {
                    severity.contains("Critique", true) || severity.contains("Haute", true) || severity.contains("Élevée", true) ->
                      PerformanceRed
                    severity.contains("Moyenne", true) -> MechanicalOrange
                    else -> ElectricGreen
                  }
                )
              ) {
                Text(
                  text = "Sévérité : $severity",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = when {
                      severity.contains("Critique", true) || severity.contains("Haute", true) || severity.contains("Élevée", true) ->
                        PerformanceRedGlow
                      severity.contains("Moyenne", true) -> MechanicalOrange
                      else -> ElectricGreen
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp
                  ),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Freeze-Frame ECU Context Pills (RPM, Temp, Voltage, Estimated Labor)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              FreezeFrameMetricPill(label = "Régime ECU", value = "2 140 tr/min", accent = TechCyan)
              FreezeFrameMetricPill(label = "T° Liquide", value = "89 °C", accent = ElectricGreen)
              FreezeFrameMetricPill(label = "Tension Réseau", value = "14.1 V", accent = MechanicalOrange)
              FreezeFrameMetricPill(label = "Barème MO", value = "${result.estimatedLaborHours} h", accent = PerformanceRedGlow)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Official Technical Summary
            Text(
              text = result.analysisSummary,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PureWhite,
                fontSize = 13.5.sp,
                lineHeight = 19.sp
              )
            )

            // Safety Warning if applicable
            if (result.safetyWarning != null) {
              Spacer(modifier = Modifier.height(10.dp))
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = adaptiveBannerSurface(0xFF2E1315, 0xFFFFF1F2),
                border = BorderStroke(1.dp, PerformanceRed),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = PerformanceRed,
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = result.safetyWarning,
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = PerformanceRedGlow,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.5.sp
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION 1: EXPLICATION VULGARISÉE & CLAIRE DU CODE DÉFAUT
            AnimatedVisibility(
              visible = revealStage >= 1,
              enter = fadeIn(tween(220)) + expandVertically(tween(220))
            ) {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
                border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.6f))
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      Icons.Default.Lightbulb,
                      contentDescription = null,
                      tint = WarningAmber,
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "1. EXPLICATION CLAIRE DU CODE DÉFAUT",
                      style = MaterialTheme.typography.labelMedium.copy(
                        color = WarningAmber,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                      )
                    )
                  }
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = result.plainLanguageExplanation,
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = OffWhite,
                      fontSize = 12.sp,
                      lineHeight = 18.sp
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION 2: CAUSES PROBABLES AVEC PROBABILITÉ STATISTIQUE ATELIER
            AnimatedVisibility(
              visible = revealStage >= 2,
              enter = fadeIn(tween(220)) + expandVertically(tween(220))
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    Icons.Default.FactCheck,
                    contentDescription = null,
                    tint = MechanicalOrange,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "2. CAUSES PROBABLES & FRÉQUENCE ATELIER",
                    style = MaterialTheme.typography.labelMedium.copy(
                      color = MechanicalOrange,
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 0.8.sp
                    )
                  )
                }
                Spacer(modifier = Modifier.height(6.dp))

                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MetalSurface)
                    .padding(12.dp),
                  verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  result.rootCauses.forEachIndexed { index, cause ->
                    val likelihoodPercent = when (index) {
                      0 -> 52
                      1 -> 28
                      2 -> 14
                      else -> 6
                    }
                    Column {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Row(
                          verticalAlignment = Alignment.Top,
                          modifier = Modifier.weight(1f)
                        ) {
                          Surface(
                            shape = CircleShape,
                            color = MechanicalOrangeDark.copy(alpha = 0.35f),
                            modifier = Modifier.size(18.dp)
                          ) {
                            Box(contentAlignment = Alignment.Center) {
                              Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                  color = MechanicalOrange,
                                  fontWeight = FontWeight.Black,
                                  fontSize = 9.5.sp
                                )
                              )
                            }
                          }
                          Spacer(modifier = Modifier.width(8.dp))
                          Text(
                            text = cause,
                            style = MaterialTheme.typography.bodySmall.copy(
                              color = OffWhite,
                              fontWeight = FontWeight.SemiBold,
                              fontSize = 12.sp,
                              lineHeight = 16.sp
                            )
                          )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = GraphiteBlack,
                          border = BorderStroke(0.5.dp, MechanicalOrange)
                        ) {
                          Text(
                            text = "~$likelihoodPercent%",
                            color = MechanicalOrange,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.5.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                          )
                        }
                      }
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION 3: PROCÉDURES DE DIAGNOSTIC INTERACTIVES & VALEURS MULTIMÈTRE
            AnimatedVisibility(
              visible = revealStage >= 3,
              enter = fadeIn(tween(220)) + expandVertically(tween(220))
            ) {
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      Icons.Default.Engineering,
                      contentDescription = null,
                      tint = TechCyan,
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "3. ÉTAPES DE DIAGNOSTIC & CONTRÔLES",
                      style = MaterialTheme.typography.labelMedium.copy(
                        color = TechCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                      )
                    )
                  }
                  Text(
                    text = "${checkedDiagnosticSteps.count { it.value }}/${result.stepByStepInspection.size} vérifiées",
                    color = ElectricGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  )
                }
                Spacer(modifier = Modifier.height(6.dp))

                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GraphiteBlack)
                    .padding(10.dp),
                  verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  result.stepByStepInspection.forEachIndexed { idx, step ->
                    val isDone = checkedDiagnosticSteps[idx] == true
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = if (isDone) ElectricGreen.copy(alpha = 0.12f) else MetalSurface,
                      border = BorderStroke(
                        0.8.dp,
                        if (isDone) ElectricGreen else MetalBorder
                      ),
                      modifier = Modifier
                        .fillMaxWidth()
                        .clickable { checkedDiagnosticSteps[idx] = !isDone }
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Icon(
                          imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                          contentDescription = null,
                          tint = if (isDone) ElectricGreen else TechCyan,
                          modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                          text = step,
                          style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isDone) ElectricGreen else AluminumLight,
                            fontWeight = if (isDone) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                          )
                        )
                      }
                    }
                  }
                }

                // Multimeter & Tolerance Specs
                if (result.multimeterSpecs != null) {
                  Spacer(modifier = Modifier.height(10.dp))
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MetalSurface,
                    border = BorderStroke(1.dp, WarningAmber.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(
                        Icons.Default.ElectricMeter,
                        contentDescription = null,
                        tint = WarningAmber,
                        modifier = Modifier.size(20.dp)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Column {
                        Text(
                          text = "VALEURS DE CONSIGNE MULTIMÈTRE / OSCILLOSCOPE :",
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = WarningAmber,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black
                          )
                        )
                        Text(
                          text = result.multimeterSpecs,
                          style = MaterialTheme.typography.bodySmall.copy(
                            color = PureWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                          )
                        )
                      }
                    }
                  }
                }

                // Guided Actuator / UDS Bi-directional Test
                if (!result.actuatorTestRecommendation.isNullOrBlank()) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.65f)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(
                        Icons.Default.DeveloperBoard,
                        contentDescription = null,
                        tint = TechCyan,
                        modifier = Modifier.size(20.dp)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = "TEST D'ACTIONNEUR & ROUTINE VALISE UDS RECOMMANDÉE :",
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = TechCyan,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black
                          )
                        )
                        Text(
                          text = result.actuatorTestRecommendation,
                          style = MaterialTheme.typography.bodySmall.copy(
                            color = OffWhite,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                          )
                        )
                      }
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 4: PIÈCES RECOMMANDÉES À LA RÉPARATION AVEC ACCÈS DIRECT AU CATALOGUE
            AnimatedVisibility(
              visible = revealStage >= 4,
              enter = fadeIn(tween(220)) + expandVertically(tween(220))
            ) {
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      Icons.Default.ShoppingCart,
                      contentDescription = null,
                      tint = ElectricGreen,
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "4. PIÈCES RECOMMANDÉES & RÉFÉRENCES OEM",
                      style = MaterialTheme.typography.labelMedium.copy(
                        color = ElectricGreen,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                      )
                    )
                  }
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ElectricGreen.copy(alpha = 0.15f),
                    border = BorderStroke(0.5.dp, ElectricGreen)
                  ) {
                    Text(
                      text = "${result.richRecommendedParts.size} réf. compatibles",
                      color = ElectricGreen,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (result.richRecommendedParts.isNotEmpty()) {
                  result.richRecommendedParts.forEach { part ->
                    DiagnosticPartPurchaseCard(
                      part = part,
                      onViewInCatalog = { viewModel.navigateToCatalogForPart(part) },
                      onCreateQuote = { viewModel.openQuoteForDiagnosticResult(result, part) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                  }
                } else {
                  result.recommendedParts.forEach { partName ->
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = MetalSurface,
                      border = BorderStroke(0.5.dp, MetalBorder),
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                        .clickable { viewModel.setNavigationTab(MainNavigationTab.PIECES) }
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Column {
                          Text(
                            text = partName,
                            style = MaterialTheme.typography.bodySmall.copy(
                              fontWeight = FontWeight.Bold,
                              color = PureWhite,
                              fontSize = 12.sp
                            )
                          )
                          Text(
                            text = "Qualité première monte certifiée",
                            style = MaterialTheme.typography.labelSmall.copy(color = AluminumGray, fontSize = 10.sp)
                          )
                        }
                        Text(
                          text = "Catalogue →",
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = MechanicalOrange,
                            fontWeight = FontWeight.Bold
                          )
                        )
                      }
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 5: DOCUMENTATION TECHNIQUE, SCHÉMAS ÉLECTRIQUES & BULLETINS TSB RECOMMANDÉS
            AnimatedVisibility(
              visible = revealStage >= 5,
              enter = fadeIn(tween(220)) + expandVertically(tween(220))
            ) {
              Column(modifier = Modifier.testTag("ai_recommended_tech_docs_section")) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      Icons.Default.MenuBook,
                      contentDescription = null,
                      tint = MechanicalOrange,
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "5. DOCUMENTATION TECHNIQUE & SCHÉMAS OEM",
                      style = MaterialTheme.typography.labelMedium.copy(
                        color = MechanicalOrange,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                      )
                    )
                  }
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MechanicalOrange.copy(alpha = 0.15f),
                    border = BorderStroke(0.5.dp, MechanicalOrange)
                  ) {
                    Text(
                      text = "${result.recommendedTechnicalDocs.size} dossiers OEM",
                      color = MechanicalOrange,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                result.recommendedTechnicalDocs.forEach { doc ->
                  DiagnosticTechnicalDocCard(
                    doc = doc,
                    onOpenDocAction = {
                      when (doc.docType) {
                        DiagnosticTechDocType.WIRING_PINOUT -> viewModel.openWiringFuseEcuModal()
                        DiagnosticTechDocType.EXPLODED_VIEW_TORQUE -> viewModel.openWebOemCatalogsModal()
                        DiagnosticTechDocType.TECHNICAL_BULLETIN_TSB -> viewModel.openTechnicalBulletinsModal()
                        DiagnosticTechDocType.REPAIR_MANUAL_UDS -> viewModel.openOfflineCacheHubModal(OfflineCacheHubTab.CACHED_SCHEMATICS)
                      }
                    },
                    onOpenOfflineSchematic = {
                      viewModel.openOfflineCacheHubModal(OfflineCacheHubTab.CACHED_SCHEMATICS)
                    }
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                MekanikPrimaryButton(
                  text = "ENREGISTRER CE DIAGNOSTIC EN CACHE LOCAL (0ms)",
                  onClick = { viewModel.saveCurrentAiDiagnosticToLocalCache(result) },
                  icon = Icons.Default.Save,
                  modifier = Modifier.fillMaxWidth(),
                  testTag = "btn_save_ai_diagnostic_local_cache"
                )

                Spacer(modifier = Modifier.height(8.dp))

                MekanikSecondaryButton(
                  text = "CRÉER UN DEVIS ATELIER POUR CE DIAGNOSTIC",
                  onClick = { viewModel.openQuoteForDiagnosticResult(result) },
                  icon = Icons.Default.ReceiptLong,
                  modifier = Modifier.fillMaxWidth()
                )
              }
            }
          }
        }
      }
    }

    // 5. GEMINI CHATBOT CONVERSATIONNEL MULTI-TOURS (Gemini 3.8 Flash & Gemini 4 Argon)
    item {
      com.example.ui.components.GeminiChatbotSection(
        messages = uiState.geminiChatMessages,
        chatInput = uiState.geminiChatInput,
        isTyping = uiState.isGeminiChatTyping,
        selectedModel = uiState.selectedGeminiModel,
        activeVehicle = uiState.activeVehicle,
        onInputChanged = { viewModel.onGeminiChatInputChanged(it) },
        onSendMessage = { preset -> viewModel.sendGeminiChatMessage(preset) },
        onSelectModel = { viewModel.setGeminiModelOption(it) },
        onClearHistory = { viewModel.clearGeminiChatHistory() },
        onOpenPartInCatalog = { part -> viewModel.navigateToCatalogForPart(part) }
      )
    }

    // 6. MekanikOne Unified Auto-API : Function Calling Gemini + Car2DB Breadcrumb + Scénarios JSON
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("card_unified_auto_api_ai_screen"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
        border = BorderStroke(1.2.dp, TechCyan)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Icon(
                Icons.Default.Hub,
                contentDescription = null,
                tint = TechCyan,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "API UNIQUE MEKANIK-ONE & FUNCTION CALLING IA",
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.5.sp
                )
                Text(
                  text = "SinoAuto360 • Dongchedi Auto-API • Car2DB LLM • 142 Marques • ChineseCar.parts",
                  color = MechanicalOrange,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.5.sp
                )
              }
            }

            OutlinedButton(
              onClick = { viewModel.openUnifiedAutoApiHubModal() },
              border = BorderStroke(1.dp, TechCyan),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = "HUB API 6-EN-1",
                color = TechCyan,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Scénarios Démo (Arborescence JSON + Fil d'Ariane Car2DB + Réf OEM) :",
            color = AluminumLight,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold
          )

          Spacer(modifier = Modifier.height(6.dp))

          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.reverseEngineeredScenarios) { scenario ->
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MetalSurface,
                border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.7f)),
                modifier = Modifier
                  .clickable { viewModel.launchReverseEngineeredScenarioInAi(scenario) }
                  .testTag("ai_scenario_${scenario.id}")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = MechanicalOrange,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Text(
                      text = "${scenario.brand} ${scenario.model.substringBefore(" ")} — Plaquettes & Freins",
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 10.5.sp
                    )
                    Text(
                      text = "VIN ${scenario.vinExample.take(8)}... • Réf OEM ${scenario.partsList.firstOrNull()?.oemPartNumber ?: ""}",
                      color = TechCyan,
                      fontFamily = FontFamily.Monospace,
                      fontSize = 9.sp
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

@Composable
private fun ObdDtcStructureDecoderBanner(dtcCode: String) {
  val clean = dtcCode.trim().uppercase().take(5).padEnd(5, '0')
  val sysChar = clean.getOrElse(0) { 'P' }
  val normChar = clean.getOrElse(1) { '0' }
  val subChar = clean.getOrElse(2) { '2' }
  val faultNum = clean.substring(3, 5)

  val systemDesc = when (sysChar) {
    'P' -> "Powertrain (Moteur / Boîte / Dépollution)"
    'C' -> "Chassis (ABS / ESP / Suspension / Direction)"
    'B' -> "Body (Airbag SRS / Habitacle / BSI / Clim)"
    'U' -> "Network (Multiplexage CAN-Bus / LIN)"
    else -> "Système Embarqué"
  }
  val normDesc = when (normChar) {
    '0' -> "Standard Universel ISO/SAE"
    '1' -> "Spécifique Constructeur (OEM)"
    '2' -> "Standard ISO / Spécifique Constructeur"
    '3' -> "Réservé SAE / Mixte"
    else -> "Norme OBD-II"
  }
  val subSystemDesc = when (subChar) {
    '0', '1' -> "Dosage Air / Carburant & Débitmètre"
    '2' -> "Circuit Injection / Suralimentation Turbo"
    '3' -> "Allumage / Ratés de Combustion / Capteur AAC-PMH"
    '4' -> "Contrôle Émissions Auxiliaires (EGR / FAP / Catalyseur)"
    '5' -> "Vitesse Véhicule & Régulation Ralenti"
    '6' -> "Calculateur ECM / Alimentations & Sorties"
    '7', '8' -> "Transmission & Boîte de Vitesses"
    else -> "Sous-système Électronique"
  }

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = GraphiteBlack,
    border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.45f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "DÉCODAGE STRUCTUREL DU CODE DTC : $clean",
          style = MaterialTheme.typography.labelSmall.copy(
            color = TechCyanGlow,
            fontWeight = FontWeight.Black,
            fontSize = 9.5.sp
          )
        )
        Text(
          text = "Index #$faultNum",
          style = MaterialTheme.typography.labelSmall.copy(
            color = MechanicalOrange,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 9.5.sp
          )
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        DtcSegmentBox(codePart = sysChar.toString(), title = "Famille", detail = systemDesc, accent = TechCyan, modifier = Modifier.weight(1f))
        DtcSegmentBox(codePart = normChar.toString(), title = "Norme", detail = normDesc, accent = MechanicalOrange, modifier = Modifier.weight(1f))
        DtcSegmentBox(codePart = subChar.toString(), title = "Organe", detail = subSystemDesc, accent = ElectricGreen, modifier = Modifier.weight(1.2f))
      }
    }
  }
}

@Composable
private fun DtcSegmentBox(
  codePart: String,
  title: String,
  detail: String,
  accent: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = MetalSurface,
    border = BorderStroke(0.8.dp, accent.copy(alpha = 0.55f)),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(horizontal = 7.dp, vertical = 6.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = accent.copy(alpha = 0.2f)
        ) {
          Text(
            text = codePart,
            color = accent,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
          )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = title,
          color = PureWhite,
          fontWeight = FontWeight.Bold,
          fontSize = 9.5.sp
        )
      }
      Spacer(modifier = Modifier.height(3.dp))
      Text(
        text = detail,
        color = AluminumLight,
        fontSize = 8.5.sp,
        lineHeight = 11.sp,
        maxLines = 2
      )
    }
  }
}

@Composable
private fun FreezeFrameMetricPill(
  label: String,
  value: String,
  accent: Color
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = GraphiteBlack,
    border = BorderStroke(0.8.dp, accent.copy(alpha = 0.55f))
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Text(
        text = "$label :",
        color = AluminumMuted,
        fontSize = 9.5.sp,
        fontWeight = FontWeight.SemiBold
      )
      Text(
        text = value,
        color = accent,
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        fontWeight = FontWeight.Black
      )
    }
  }
}

@Composable
private fun DiagnosticPartPurchaseCard(
  part: DiagnosticRecommendedPart,
  onViewInCatalog: () -> Unit,
  onCreateQuote: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MetalSurface),
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = part.name,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.Bold,
              color = PureWhite,
              fontSize = 13.sp
            )
          )
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "RÉF OEM : ",
              style = MaterialTheme.typography.labelSmall.copy(
                color = AluminumMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            )
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = GraphiteBlack,
              border = BorderStroke(0.5.dp, MechanicalOrange)
            ) {
              Text(
                text = part.oemReference,
                style = MaterialTheme.typography.labelSmall.copy(
                  color = MechanicalOrange,
                  fontWeight = FontWeight.Black,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.5.sp
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "${part.priceEstimatedDzd.toInt()} DZD",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Black,
              color = PerformanceRedGlow,
              fontSize = 13.5.sp
            )
          )
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = ElectricGreen.copy(alpha = 0.15f)
          ) {
            Text(
              text = part.availabilityBadge,
              style = MaterialTheme.typography.labelSmall.copy(
                color = ElectricGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 8.5.sp
              ),
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Brands & Urgency
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Marques : ${part.equivalentBrands.joinToString(", ")}",
          style = MaterialTheme.typography.bodySmall.copy(
            color = AluminumLight,
            fontSize = 10.5.sp
          )
        )
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = MetalCard,
          border = BorderStroke(0.5.dp, MetalBorder)
        ) {
          Text(
            text = part.urgencyLevel,
            style = MaterialTheme.typography.labelSmall.copy(
              color = OffWhite,
              fontWeight = FontWeight.Medium,
              fontSize = 9.sp
            ),
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Divider(color = MetalBorder, thickness = 0.5.dp)

      Spacer(modifier = Modifier.height(8.dp))

      // Direct Catalog Purchase & Quote Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onViewInCatalog,
          modifier = Modifier
            .weight(1.3f)
            .height(34.dp),
          colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Icon(
            Icons.Default.ShoppingCart,
            contentDescription = null,
            tint = PureWhite,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Voir Catalogue & Acheter",
            color = PureWhite,
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp
            )
          )
        }

        OutlinedButton(
          onClick = onCreateQuote,
          modifier = Modifier
            .weight(1f)
            .height(34.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = AluminumLight),
          border = BorderStroke(1.dp, MetalBorder),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
        ) {
          Icon(
            Icons.Default.ReceiptLong,
            contentDescription = null,
            tint = TechCyan,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Ajouter au Devis",
            color = OffWhite,
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          )
        }
      }
    }
  }
}

@Composable
private fun DiagnosticTechnicalDocCard(
  doc: DiagnosticTechnicalDoc,
  onOpenDocAction: () -> Unit,
  onOpenOfflineSchematic: () -> Unit
) {
  val accentColor = when (doc.docType) {
    DiagnosticTechDocType.WIRING_PINOUT -> TechCyan
    DiagnosticTechDocType.EXPLODED_VIEW_TORQUE -> MechanicalOrange
    DiagnosticTechDocType.TECHNICAL_BULLETIN_TSB -> WarningAmber
    DiagnosticTechDocType.REPAIR_MANUAL_UDS -> ElectricGreen
  }
  val docIcon = when (doc.docType) {
    DiagnosticTechDocType.WIRING_PINOUT -> Icons.Default.ElectricalServices
    DiagnosticTechDocType.EXPLODED_VIEW_TORQUE -> Icons.Default.AccountTree
    DiagnosticTechDocType.TECHNICAL_BULLETIN_TSB -> Icons.Default.Campaign
    DiagnosticTechDocType.REPAIR_MANUAL_UDS -> Icons.Default.MenuBook
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("tech_doc_card_${doc.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MetalSurface),
    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.55f))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = docIcon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            shape = RoundedCornerShape(5.dp),
            color = accentColor.copy(alpha = 0.16f),
            border = BorderStroke(0.6.dp, accentColor)
          ) {
            Text(
              text = doc.docType.badgeLabel,
              color = accentColor,
              fontWeight = FontWeight.Black,
              fontSize = 9.sp,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(5.dp),
          color = GraphiteBlack,
          border = BorderStroke(0.5.dp, MetalBorder)
        ) {
          Text(
            text = doc.docCode,
            color = PureWhite,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 9.5.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = doc.title,
        color = PureWhite,
        fontWeight = FontWeight.Bold,
        fontSize = 12.5.sp
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = doc.summary,
        color = AluminumLight,
        fontSize = 11.sp,
        lineHeight = 15.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      Surface(
        shape = RoundedCornerShape(8.dp),
        color = GraphiteBlack,
        border = BorderStroke(0.6.dp, accentColor.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
          Text(
            text = "SPÉCIFICATIONS & COUPLES DE SERRAGE :",
            color = accentColor,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp
          )
          Text(
            text = doc.keySpecsOrTorque,
            color = OffWhite,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 10.5.sp
          )
          if (!doc.ecuPinoutOrRoutine.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = doc.ecuPinoutOrRoutine,
              color = AluminumLight,
              fontSize = 10.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onOpenDocAction,
          modifier = Modifier
            .weight(1.3f)
            .height(34.dp),
          colors = ButtonDefaults.buttonColors(containerColor = accentColor),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Icon(
            imageVector = docIcon,
            contentDescription = null,
            tint = GraphiteBlack,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = doc.docType.actionButtonLabel,
            color = GraphiteBlack,
            fontWeight = FontWeight.Black,
            fontSize = 10.sp
          )
        }

        OutlinedButton(
          onClick = onOpenOfflineSchematic,
          modifier = Modifier
            .weight(1f)
            .height(34.dp),
          border = BorderStroke(1.dp, MetalBorder),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.OfflineBolt,
            contentDescription = null,
            tint = TechCyan,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Cache 0ms",
            color = OffWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        }
      }
    }
  }
}

@Composable
private fun ObdQuickChip(
  label: String,
  code: String,
  isSelected: Boolean = false,
  onClick: (String) -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = if (isSelected) TechCyan.copy(alpha = 0.22f) else MetalSurface,
    border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) TechCyan else MetalBorder),
    onClick = { onClick(code) },
    modifier = Modifier.height(30.dp)
  ) {
    Box(
      modifier = Modifier.padding(horizontal = 10.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          color = if (isSelected) PureWhite else AluminumLight,
          fontSize = 11.sp,
          fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
        )
      )
    }
  }
}


