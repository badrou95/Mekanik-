package com.example.ui.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.CompanionWorkTimeAllocation
import com.example.data.model.WeeklyProductivityDayPoint
import com.example.ui.theme.*
import java.util.Locale

enum class AtelierD3VisualizationMode(val label: String, val badge: String) {
  COMBINED("Vue Combinée", "D3 + RECHARTS"),
  WORK_TIME_SPLIT("Temps / Compagnon", "RÉPARTITION H"),
  WEEKLY_PRODUCTIVITY("Productivité Hebdo", "COURBE 7 JOURS")
}

@Composable
fun AtelierProductivityD3Widget(
  companionAllocations: List<CompanionWorkTimeAllocation>,
  weeklyPoints: List<WeeklyProductivityDayPoint>,
  onAdjustCompanionHours: (
    companionId: String,
    productiveDelta: Float,
    diagnosticDelta: Float,
    waitingDelta: Float,
    soldBaremeDelta: Float
  ) -> Unit,
  onOpenFullAnalytics: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedMode by remember { mutableStateOf(AtelierD3VisualizationMode.COMBINED) }
  var isInteractiveD3WebView by remember { mutableStateOf(true) }
  var selectedCompanionId by remember { mutableStateOf<String?>(null) }
  var selectedDayIndex by remember { mutableIntStateOf(5) } // Default to Jeudi (peak day)
  var selectedCompanionCurveIndex by remember { mutableIntStateOf(0) } // 0 = Atelier Global, 1..4 = Companions

  // Global Aggregated Metrics
  val totalClockedWeekHours = remember(companionAllocations) {
    companionAllocations.sumOf { it.totalClockedHours.toDouble() }.toFloat().coerceAtLeast(1f)
  }
  val totalSoldWeekHours = remember(companionAllocations) {
    companionAllocations.sumOf { it.soldBaremeHours.toDouble() }.toFloat()
  }
  val totalProductiveHours = remember(companionAllocations) {
    companionAllocations.sumOf { it.productiveRepairHours.toDouble() }.toFloat()
  }
  val totalDiagnosticHours = remember(companionAllocations) {
    companionAllocations.sumOf { it.diagnosticAndTestHours.toDouble() }.toFloat()
  }
  val totalWaitingHours = remember(companionAllocations) {
    companionAllocations.sumOf { it.waitingPartsHours.toDouble() }.toFloat()
  }
  val globalWeeklyProductivityPct = remember(totalSoldWeekHours, totalClockedWeekHours) {
    ((totalSoldWeekHours / totalClockedWeekHours) * 100f).toInt()
  }
  val billableRatioPct = remember(totalProductiveHours, totalDiagnosticHours, totalClockedWeekHours) {
    (((totalProductiveHours + totalDiagnosticHours) / totalClockedWeekHours) * 100f).toInt()
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("atelier_productivity_d3_widget"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.2.dp, TechCyan.copy(alpha = 0.65f))
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // -----------------------------------------------------------------------
      // 1. WIDGET HEADER & ENGINE TOGGLE (RECHARTS/D3 SVG vs CANVAS NATIF)
      // -----------------------------------------------------------------------
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(TechCyan.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.Insights,
              contentDescription = null,
              tint = TechCyan,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "VISUALISATION RECHARTS / D3.JS",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Black,
                  color = PureWhite,
                  fontSize = 13.5.sp,
                  letterSpacing = 0.5.sp
                )
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = ElectricGreen.copy(alpha = 0.2f),
                border = BorderStroke(0.5.dp, ElectricGreen)
              ) {
                Text(
                  text = "$globalWeeklyProductivityPct% HEBDO",
                  color = ElectricGreen,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Black,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = "Répartition du temps de travail par compagnon & Taux de productivité hebdomadaire",
              style = MaterialTheme.typography.bodySmall.copy(
                color = AluminumLight,
                fontSize = 10.5.sp
              )
            )
          }
        }

        // Toggle D3/Recharts WebView vs Compose Canvas
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = adaptiveBannerSurface(0xFF10161D, 0xFFF1F5F9),
          border = BorderStroke(1.dp, if (isInteractiveD3WebView) TechCyan else MetalBorder),
          onClick = { isInteractiveD3WebView = !isInteractiveD3WebView },
          modifier = Modifier.testTag("btn_toggle_d3_recharts_engine")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              if (isInteractiveD3WebView) Icons.Default.AutoGraph else Icons.Default.Speed,
              contentDescription = null,
              tint = if (isInteractiveD3WebView) TechCyan else MechanicalOrange,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isInteractiveD3WebView) "Recharts/D3" else "Canvas 60fps",
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (isInteractiveD3WebView) TechCyan else MechanicalOrange,
                fontWeight = FontWeight.Bold,
                fontSize = 9.5.sp
              )
            )
          }
        }
      }

      // -----------------------------------------------------------------------
      // 2. SUMMARY KPI STRIP FOR WORK TIME & WEEKLY PRODUCTIVITY
      // -----------------------------------------------------------------------
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MiniAnalyticsKpiBox(
          title = "Productivité 7J",
          value = "$globalWeeklyProductivityPct%",
          subtitle = "Cible atelier : 100%",
          accentColor = if (globalWeeklyProductivityPct >= 100) ElectricGreen else MechanicalOrange,
          modifier = Modifier.weight(1f)
        )
        MiniAnalyticsKpiBox(
          title = "Vendu vs Pointé",
          value = "${String.format(Locale.US, "%.1f", totalSoldWeekHours)}h / ${String.format(Locale.US, "%.1f", totalClockedWeekHours)}h",
          subtitle = "Gain : +${String.format(Locale.US, "%.1f", (totalSoldWeekHours - totalClockedWeekHours))}h barème",
          accentColor = TechCyan,
          modifier = Modifier.weight(1f)
        )
        MiniAnalyticsKpiBox(
          title = "Temps Facturable",
          value = "$billableRatioPct%",
          subtitle = "Attente pièces : ${String.format(Locale.US, "%.1f", totalWaitingHours)}h",
          accentColor = MechanicalOrange,
          modifier = Modifier.weight(1f)
        )
      }

      // -----------------------------------------------------------------------
      // 3. MODE SELECTOR TABS (COMBINÉE / TEMPS COMPAGNON / PRODUCTIVITÉ HEBDO)
      // -----------------------------------------------------------------------
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        AtelierD3VisualizationMode.entries.forEach { mode ->
          val isSelected = selectedMode == mode
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) PerformanceRed else adaptiveBannerSurface(0xFF141C24, 0xFFF1F5F9),
            border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
            onClick = { selectedMode = mode },
            modifier = Modifier
              .weight(1f)
              .testTag("d3_mode_tab_${mode.name.lowercase()}")
          ) {
            Column(
              modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = mode.badge,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Black,
                  color = if (isSelected) PureWhite.copy(alpha = 0.85f) else TechCyan
                )
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = mode.label,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.5.sp,
                  fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                  color = PureWhite,
                  textAlign = TextAlign.Center
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }
      }

      // -----------------------------------------------------------------------
      // 4. CHART SECTION A: RÉPARTITION DU TEMPS DE TRAVAIL PAR COMPAGNON
      // -----------------------------------------------------------------------
      if (selectedMode == AtelierD3VisualizationMode.COMBINED || selectedMode == AtelierD3VisualizationMode.WORK_TIME_SPLIT) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = adaptiveBannerSurface(0xFF121820, 0xFFF8FAFC),
          border = BorderStroke(1.dp, MetalBorder),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("chart_container_companion_work_time")
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "1. RÉPARTITION DU TEMPS DE TRAVAIL PAR COMPAGNON",
                  style = MaterialTheme.typography.labelMedium.copy(
                    color = TechCyan,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.5.sp
                  )
                )
                Text(
                  text = "Heures hebdomadaires ventilées : Réparation • Diagnostic OBD • Attente Pièces • Interne",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 10.sp)
                )
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = TechCyan.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "${companionAllocations.size} Compagnons",
                  color = TechCyan,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.5.sp,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
              }
            }

            // Color Legend for Time Allocation
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              ChartLegendDot(color = ElectricGreen, label = "Réparation Facturée")
              ChartLegendDot(color = TechCyan, label = "Diag OBD & Essais")
              ChartLegendDot(color = MechanicalOrange, label = "Attente Pièces / VOR")
              ChartLegendDot(color = PerformanceRed, label = "Maintenance Interne")
            }

            // Render either Recharts/D3 SVG WebView or Native Compose Canvas
            if (isInteractiveD3WebView) {
              RechartsD3CompanionTimeWebView(
                allocations = companionAllocations,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(235.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFF101418))
                  .testTag("webview_d3_companion_time")
              )
            } else {
              NativeCompanionWorkTimeCanvasChart(
                allocations = companionAllocations,
                selectedCompanionId = selectedCompanionId,
                onSelectCompanion = { id ->
                  selectedCompanionId = if (selectedCompanionId == id) null else id
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(235.dp)
                  .testTag("canvas_native_companion_time")
              )
            }

            // Interactive Companion Breakdown Pills & Quick Simulation Controls
            Text(
              text = "Toucher un compagnon pour ajuster ou simuler le pointage en direct :",
              style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 10.sp)
            )

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              companionAllocations.forEach { comp ->
                val isSelected = selectedCompanionId == comp.id
                val prodColor = if (comp.productivityRatePct >= 105) ElectricGreen else if (comp.productivityRatePct >= 98) TechCyan else MechanicalOrange
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (isSelected) TechCyan.copy(alpha = 0.2f) else adaptiveBannerSurface(0xFF18222D, 0xFFFFFFFF),
                  border = BorderStroke(1.dp, if (isSelected) TechCyan else MetalBorder),
                  onClick = {
                    selectedCompanionId = if (isSelected) null else comp.id
                  },
                  modifier = Modifier.testTag("pill_companion_alloc_${comp.id}")
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(prodColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                      Text(
                        text = "${comp.shortName} (${comp.productivityRatePct}%)",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = PureWhite,
                          fontWeight = FontWeight.Bold,
                          fontSize = 10.5.sp
                        )
                      )
                      Text(
                        text = "Prod: ${comp.productiveRepairHours}h • Diag: ${comp.diagnosticAndTestHours}h • Att: ${comp.waitingPartsHours}h",
                        style = MaterialTheme.typography.bodySmall.copy(
                          color = AluminumLight,
                          fontSize = 9.sp
                        )
                      )
                    }
                  }
                }
              }
            }

            // Selected Companion Quick Adjustment Bar
            val activeCompanion = companionAllocations.find { it.id == selectedCompanionId } ?: companionAllocations.firstOrNull()
            AnimatedVisibility(visible = activeCompanion != null) {
              activeCompanion?.let { comp ->
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = adaptiveBannerSurface(0xFF16202B, 0xFFEFF6FF),
                  border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.5f)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text(
                          text = "${comp.companionName} • ${comp.bayName}",
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = PureWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                          )
                        )
                        Text(
                          text = "Répartition : ${comp.productiveSharePct}% Réparation | ${comp.diagnosticSharePct}% Diag | ${comp.waitingPartsSharePct}% Attente Pièce | ${comp.internalSharePct}% Interne",
                          style = MaterialTheme.typography.bodySmall.copy(
                            color = TechCyan,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold
                          )
                        )
                      }
                      Text(
                        text = "${comp.soldBaremeHours}h vendues / ${comp.totalClockedHours}h",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = ElectricGreen,
                          fontFamily = FontFamily.Monospace,
                          fontWeight = FontWeight.Bold,
                          fontSize = 10.sp
                        )
                      )
                    }

                    // Horizontal stacked progress bar for this companion
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(GraphiteBlack)
                    ) {
                      if (comp.productiveRepairHours > 0f) {
                        Box(
                          modifier = Modifier
                            .weight(comp.productiveRepairHours.coerceAtLeast(0.1f))
                            .fillMaxHeight()
                            .background(ElectricGreen)
                        )
                      }
                      if (comp.diagnosticAndTestHours > 0f) {
                        Box(
                          modifier = Modifier
                            .weight(comp.diagnosticAndTestHours.coerceAtLeast(0.1f))
                            .fillMaxHeight()
                            .background(TechCyan)
                        )
                      }
                      if (comp.waitingPartsHours > 0f) {
                        Box(
                          modifier = Modifier
                            .weight(comp.waitingPartsHours.coerceAtLeast(0.1f))
                            .fillMaxHeight()
                            .background(MechanicalOrange)
                        )
                      }
                      if (comp.internalMaintenanceHours > 0f) {
                        Box(
                          modifier = Modifier
                            .weight(comp.internalMaintenanceHours.coerceAtLeast(0.1f))
                            .fillMaxHeight()
                            .background(PerformanceRed)
                        )
                      }
                    }

                    // Quick live simulation buttons
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ElectricGreen.copy(alpha = 0.18f),
                        border = BorderStroke(0.8.dp, ElectricGreen),
                        onClick = {
                          onAdjustCompanionHours(comp.id, 1.0f, 0f, 0f, 1.5f)
                        },
                        modifier = Modifier
                          .weight(1f)
                          .testTag("btn_sim_add_productive_${comp.id}")
                      ) {
                        Text(
                          text = "+1h Réparation (+1.5h Barème)",
                          color = ElectricGreen,
                          fontWeight = FontWeight.Bold,
                          fontSize = 9.sp,
                          textAlign = TextAlign.Center,
                          modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                        )
                      }

                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = TechCyan.copy(alpha = 0.18f),
                        border = BorderStroke(0.8.dp, TechCyan),
                        onClick = {
                          onAdjustCompanionHours(comp.id, 0f, 0.5f, 0f, 0.8f)
                        },
                        modifier = Modifier
                          .weight(1f)
                          .testTag("btn_sim_add_diag_${comp.id}")
                      ) {
                        Text(
                          text = "+0.5h Diag OBD",
                          color = TechCyan,
                          fontWeight = FontWeight.Bold,
                          fontSize = 9.sp,
                          textAlign = TextAlign.Center,
                          modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                        )
                      }

                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MechanicalOrange.copy(alpha = 0.18f),
                        border = BorderStroke(0.8.dp, MechanicalOrange),
                        onClick = {
                          onAdjustCompanionHours(comp.id, 0f, 0f, 0.5f, 0f)
                        },
                        modifier = Modifier
                          .weight(1f)
                          .testTag("btn_sim_add_wait_${comp.id}")
                      ) {
                        Text(
                          text = "+0.5h Attente Pièce",
                          color = MechanicalOrange,
                          fontWeight = FontWeight.Bold,
                          fontSize = 9.sp,
                          textAlign = TextAlign.Center,
                          modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
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

      // -----------------------------------------------------------------------
      // 5. CHART SECTION B: TAUX DE PRODUCTIVITÉ HEBDOMADAIRE (7 JOURS)
      // -----------------------------------------------------------------------
      if (selectedMode == AtelierD3VisualizationMode.COMBINED || selectedMode == AtelierD3VisualizationMode.WEEKLY_PRODUCTIVITY) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = adaptiveBannerSurface(0xFF121820, 0xFFF8FAFC),
          border = BorderStroke(1.dp, MetalBorder),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("chart_container_weekly_productivity")
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "2. TAUX DE PRODUCTIVITÉ HEBDOMADAIRE (SAM → VEN)",
                  style = MaterialTheme.typography.labelMedium.copy(
                    color = ElectricGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.5.sp
                  )
                )
                Text(
                  text = "Courbe d'efficacité Recharts/D3 (%) vs Cible 100% & Heures Vendues/Pointées",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 10.sp)
                )
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = PerformanceRed.copy(alpha = 0.18f),
                border = BorderStroke(0.8.dp, PerformanceRed)
              ) {
                Text(
                  text = "CIBLE 100%",
                  color = PerformanceRedGlow,
                  fontWeight = FontWeight.Black,
                  fontSize = 9.sp,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
              }
            }

            // Companion Curve Filter (Global Atelier vs Individual Companion Curve)
            val curveLabels = listOf("Atelier Global", "Karim B.", "Yacine M.", "Sofiane R.", "Amine K.")
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              curveLabels.forEachIndexed { idx, label ->
                val isSelected = selectedCompanionCurveIndex == idx
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (isSelected) ElectricGreen.copy(alpha = 0.2f) else adaptiveBannerSurface(0xFF18222D, 0xFFFFFFFF),
                  border = BorderStroke(1.dp, if (isSelected) ElectricGreen else MetalBorder),
                  onClick = { selectedCompanionCurveIndex = idx },
                  modifier = Modifier.testTag("weekly_curve_filter_$idx")
                ) {
                  Text(
                    text = label,
                    color = if (isSelected) ElectricGreen else PureWhite,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                  )
                }
              }
            }

            // Render Recharts/D3 Weekly Productivity WebView or Native Compose Canvas
            if (isInteractiveD3WebView) {
              RechartsD3WeeklyProductivityWebView(
                weeklyPoints = weeklyPoints,
                selectedCurveIndex = selectedCompanionCurveIndex,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(235.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFF101418))
                  .testTag("webview_d3_weekly_productivity")
              )
            } else {
              NativeWeeklyProductivityCanvasChart(
                weeklyPoints = weeklyPoints,
                selectedCurveIndex = selectedCompanionCurveIndex,
                selectedDayIndex = selectedDayIndex,
                onSelectDay = { selectedDayIndex = it },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(235.dp)
                  .testTag("canvas_native_weekly_productivity")
              )
            }

            // Interactive Day Selector Pills & Daily Detail Summary
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              weeklyPoints.forEachIndexed { index, pt ->
                val isSelected = index == selectedDayIndex
                val dayPct = when (selectedCompanionCurveIndex) {
                  1 -> pt.karimPct
                  2 -> pt.yacinePct
                  3 -> pt.sofianePct
                  4 -> pt.aminePct
                  else -> pt.productivityPct
                }
                val badgeColor = if (dayPct >= 100) ElectricGreen else MechanicalOrange
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (isSelected) badgeColor.copy(alpha = 0.22f) else adaptiveBannerSurface(0xFF18222D, 0xFFFFFFFF),
                  border = BorderStroke(1.dp, if (isSelected) badgeColor else MetalBorder),
                  onClick = { selectedDayIndex = index },
                  modifier = Modifier
                    .weight(1f)
                    .testTag("day_pill_${pt.dayCode.lowercase()}")
                ) {
                  Column(
                    modifier = Modifier.padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Text(
                      text = pt.dayCode,
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                      )
                    )
                    Text(
                      text = "$dayPct%",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = badgeColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.5.sp,
                        fontFamily = FontFamily.Monospace
                      )
                    )
                  }
                }
              }
            }

            // Selected Day Breakdown Banner
            val activeDay = weeklyPoints.getOrNull(selectedDayIndex.coerceIn(0, weeklyPoints.lastIndex))
            activeDay?.let { day ->
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = adaptiveBannerSurface(0xFF16202B, 0xFFF0FDF4),
                border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "${day.dayFullLabel} • ${day.vehiclesCompleted} véhicules livrés",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = PureWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                      )
                    )
                    Text(
                      text = "Karim: ${day.karimPct}% • Yacine: ${day.yacinePct}% • Sofiane: ${day.sofianePct}% • Amine: ${day.aminePct}%",
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = AluminumLight,
                        fontSize = 9.5.sp
                      )
                    )
                  }
                  Column(horizontalAlignment = Alignment.End) {
                    Text(
                      text = "${day.soldBaremeHours}h vendues / ${day.clockedRealHours}h",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = TechCyan,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.5.sp
                      )
                    )
                    Text(
                      text = "Productivité : ${day.productivityPct}%",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = if (day.productivityPct >= 100) ElectricGreen else MechanicalOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.5.sp
                      )
                    )
                  }
                }
              }
            }
          }
        }
      }

      // -----------------------------------------------------------------------
      // 6. FOOTER LINK TO FULL RECHARTS ANALYTICS & POINTAGE TAB
      // -----------------------------------------------------------------------
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Verified, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "Synchronisé en direct avec le pointage CTH & les baies atelier",
            style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 10.sp)
          )
        }

        TextButton(
          onClick = onOpenFullAnalytics,
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
          modifier = Modifier.testTag("btn_open_full_recharts_tab")
        ) {
          Text(
            text = "Console Recharts Complète →",
            color = TechCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

// =============================================================================
// SUB-COMPONENTS & RECHARTS / D3.JS WEBVIEW / CANVAS IMPLEMENTATIONS
// =============================================================================

@Composable
private fun MiniAnalyticsKpiBox(
  title: String,
  value: String,
  subtitle: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = adaptiveBannerSurface(0xFF151D26, 0xFFF8FAFC),
    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.55f)),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
          color = AluminumLight,
          fontSize = 9.5.sp,
          fontWeight = FontWeight.SemiBold
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(3.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(
          color = PureWhite,
          fontWeight = FontWeight.Black,
          fontSize = 14.sp,
          fontFamily = FontFamily.Monospace
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(
          color = accentColor,
          fontSize = 8.5.sp,
          fontWeight = FontWeight.Bold
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
private fun ChartLegendDot(color: Color, label: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(5.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        color = AluminumLight,
        fontSize = 9.5.sp,
        fontWeight = FontWeight.Medium
      )
    )
  }
}

/**
 * Interactive Recharts / D3.js SVG WebView for Companion Work Time Allocation
 * Displays a Horizontal/Vertical Stacked Bar Chart + Donut Breakdown with interactive tooltips.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun RechartsD3CompanionTimeWebView(
  allocations: List<CompanionWorkTimeAllocation>,
  modifier: Modifier = Modifier
) {
  val htmlContent = remember(allocations) {
    buildCompanionWorkTimeD3Html(allocations)
  }

  AndroidView(
    factory = { ctx ->
      WebView(ctx).apply {
        layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
        )
        setBackgroundColor(android.graphics.Color.parseColor("#101418"))
        settings.apply {
          javaScriptEnabled = true
          domStorageEnabled = true
          loadWithOverviewMode = true
          useWideViewPort = true
          cacheMode = WebSettings.LOAD_NO_CACHE
        }
        webChromeClient = WebChromeClient()
        webViewClient = WebViewClient()
        loadDataWithBaseURL("https://mekanik.app/", htmlContent, "text/html", "UTF-8", null)
      }
    },
    update = { webView ->
      webView.loadDataWithBaseURL("https://mekanik.app/", htmlContent, "text/html", "UTF-8", null)
    },
    modifier = modifier
  )
}

private fun buildCompanionWorkTimeD3Html(allocations: List<CompanionWorkTimeAllocation>): String {
  val jsonBuilder = StringBuilder("[")
  allocations.forEachIndexed { idx, item ->
    jsonBuilder.append(
      """
      {
        "name": "${item.shortName}",
        "fullName": "${item.companionName}",
        "bay": "${item.bayName}",
        "productive": ${item.productiveRepairHours},
        "diag": ${item.diagnosticAndTestHours},
        "waiting": ${item.waitingPartsHours},
        "internal": ${item.internalMaintenanceHours},
        "sold": ${item.soldBaremeHours},
        "total": ${item.totalClockedHours},
        "prodPct": ${item.productivityRatePct}
      }${if (idx < allocations.size - 1) "," else ""}
      """.trimIndent()
    )
  }
  jsonBuilder.append("]")

  val totProd = allocations.sumOf { it.productiveRepairHours.toDouble() }.toFloat()
  val totDiag = allocations.sumOf { it.diagnosticAndTestHours.toDouble() }.toFloat()
  val totWait = allocations.sumOf { it.waitingPartsHours.toDouble() }.toFloat()
  val totInt = allocations.sumOf { it.internalMaintenanceHours.toDouble() }.toFloat()
  val grandTot = (totProd + totDiag + totWait + totInt).coerceAtLeast(1f)

  val pPct = ((totProd / grandTot) * 100f).toInt()
  val dPct = ((totDiag / grandTot) * 100f).toInt()
  val wPct = ((totWait / grandTot) * 100f).toInt()
  val iPct = (100 - pPct - dPct - wPct).coerceAtLeast(0)

  return """
  <!DOCTYPE html>
  <html>
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
    <style>
      * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }
      body { background-color: #101418; color: #F5F7F9; overflow: hidden; padding: 6px 8px; }
      .layout { display: flex; flex-direction: row; align-items: center; width: 100%; height: 220px; position: relative; }
      .bars-area { width: 64%; height: 100%; }
      .donut-area { width: 36%; height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; }
      svg { width: 100%; height: 100%; }
      .grid-line { stroke: #252B31; stroke-dasharray: 3 3; }
      .axis-label { fill: #AEB6BF; font-size: 10px; font-weight: 600; }
      .seg-rect { cursor: pointer; transition: opacity 0.2s ease; }
      .seg-rect:hover { opacity: 0.85; }
      .tooltip {
        position: absolute; display: none; background: rgba(18, 24, 32, 0.96); border: 1px solid #00E5FF;
        border-radius: 8px; padding: 7px 10px; font-size: 10.5px; color: #F5F7F9;
        pointer-events: none; box-shadow: 0 4px 14px rgba(0,0,0,0.7); z-index: 99; white-space: nowrap;
      }
      .tt-head { color: #00E5FF; font-weight: 800; margin-bottom: 3px; }
      .tt-row { margin-top: 2px; font-size: 9.5px; color: #D5DCE2; }
    </style>
  </head>
  <body>
    <div class="layout" id="wrap">
      <div class="tooltip" id="tip"></div>
      <div class="bars-area">
        <svg viewBox="0 0 320 215" preserveAspectRatio="none">
          <line x1="30" y1="25" x2="308" y2="25" class="grid-line" />
          <line x1="30" y1="70" x2="308" y2="70" class="grid-line" />
          <line x1="30" y1="115" x2="308" y2="115" class="grid-line" />
          <line x1="30" y1="160" x2="308" y2="160" class="grid-line" />
          <line x1="30" y1="185" x2="308" y2="185" stroke="#374151" stroke-width="1.5" />
          <text x="26" y="28" text-anchor="end" class="axis-label">45h</text>
          <text x="26" y="73" text-anchor="end" class="axis-label">30h</text>
          <text x="26" y="118" text-anchor="end" class="axis-label">20h</text>
          <text x="26" y="163" text-anchor="end" class="axis-label">10h</text>
          <g id="barsLayer"></g>
        </svg>
      </div>
      <div class="donut-area">
        <svg viewBox="0 0 140 165">
          <circle cx="70" cy="72" r="46" fill="none" stroke="#252B31" stroke-width="16" />
          <circle id="dProd" cx="70" cy="72" r="46" fill="none" stroke="#00E676" stroke-width="16" transform="rotate(-90 70 72)" />
          <circle id="dDiag" cx="70" cy="72" r="46" fill="none" stroke="#00E5FF" stroke-width="16" transform="rotate(-90 70 72)" />
          <circle id="dWait" cx="70" cy="72" r="46" fill="none" stroke="#FF7A00" stroke-width="16" transform="rotate(-90 70 72)" />
          <circle id="dInt" cx="70" cy="72" r="46" fill="none" stroke="#E53935" stroke-width="16" transform="rotate(-90 70 72)" />
          <text x="70" y="69" text-anchor="middle" fill="#FFFFFF" font-size="12" font-weight="900">${pPct + dPct}%</text>
          <text x="70" y="82" text-anchor="middle" fill="#00E676" font-size="8.5" font-weight="bold">FACTURABLE</text>
          <text x="70" y="142" text-anchor="middle" fill="#AEB6BF" font-size="9" font-weight="bold">GLOBAL ÉQUIPE</text>
          <text x="70" y="155" text-anchor="middle" fill="#00E5FF" font-size="8.5" font-family="monospace">${String.format(Locale.US, "%.1f", grandTot)}h pointées</text>
        </svg>
      </div>
    </div>
    <script>
      const data = $jsonBuilder;
      const barsLayer = document.getElementById('barsLayer');
      const tip = document.getElementById('tip');
      const wrap = document.getElementById('wrap');

      // Donut arcs
      const circ = 2 * Math.PI * 46;
      const pLen = ($pPct / 100.0) * circ;
      const dLen = ($dPct / 100.0) * circ;
      const wLen = ($wPct / 100.0) * circ;
      const iLen = ($iPct / 100.0) * circ;

      const setArc = (id, len, offset) => {
        const el = document.getElementById(id);
        el.setAttribute('stroke-dasharray', `${'$'}{len} ${'$'}{circ}`);
        el.setAttribute('stroke-dashoffset', -offset);
      };
      setArc('dProd', pLen, 0);
      setArc('dDiag', dLen, pLen);
      setArc('dWait', wLen, pLen + dLen);
      setArc('dInt', iLen, pLen + dLen + wLen);

      // Stacked Bars per Companion + Sold Barème Marker
      const maxH = 48.0;
      const startX = 36;
      const baseY = 185;
      const chartH = 155;
      const stepX = 268 / Math.max(data.length, 1);
      const barW = Math.min(34, stepX * 0.56);

      data.forEach((d, idx) => {
        const centerX = startX + idx * stepX + stepX / 2;
        const x = centerX - barW / 2;
        let curY = baseY;

        const segments = [
          { val: d.productive, color: '#00E676' },
          { val: d.diag, color: '#00E5FF' },
          { val: d.waiting, color: '#FF7A00' },
          { val: d.internal, color: '#E53935' }
        ];

        const showTooltip = () => {
          tip.style.display = 'block';
          tip.innerHTML = `
            <div class="tt-head">${'$'}{d.fullName} • Productivité ${'$'}{d.prodPct}%</div>
            <div class="tt-row">🟢 Réparation : <b>${'$'}{d.productive}h</b> | 🔵 Diag OBD : <b>${'$'}{d.diag}h</b></div>
            <div class="tt-row">🟠 Attente Pièces : <b>${'$'}{d.waiting}h</b> | 🔴 Interne : <b>${'$'}{d.internal}h</b></div>
            <div class="tt-row">⚡ Barème Vendu : <b>${'$'}{d.sold}h</b> sur ${'$'}{d.total}h pointées</div>
          `;
          tip.style.left = '12px';
          tip.style.top = '8px';
        };

        segments.forEach(seg => {
          if (seg.val > 0) {
            const h = (seg.val / maxH) * chartH;
            curY -= h;
            const r = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
            r.setAttribute('x', x);
            r.setAttribute('y', curY);
            r.setAttribute('width', barW);
            r.setAttribute('height', Math.max(2, h));
            r.setAttribute('fill', seg.color);
            r.setAttribute('class', 'seg-rect');
            r.addEventListener('click', showTooltip);
            r.addEventListener('touchstart', showTooltip);
            barsLayer.appendChild(r);
          }
        });

        // Total hours & productivity badge on top of stacked bar
        const topText = document.createElementNS('http://www.w3.org/2000/svg', 'text');
        topText.setAttribute('x', centerX);
        topText.setAttribute('y', Math.max(14, curY - 5));
        topText.setAttribute('text-anchor', 'middle');
        topText.setAttribute('fill', '#00E676');
        topText.setAttribute('font-size', '9');
        topText.setAttribute('font-weight', 'bold');
        topText.textContent = d.prodPct + '%';
        barsLayer.appendChild(topText);

        // X Axis Companion Short Name
        const lbl = document.createElementNS('http://www.w3.org/2000/svg', 'text');
        lbl.setAttribute('x', centerX);
        lbl.setAttribute('y', baseY + 15);
        lbl.setAttribute('text-anchor', 'middle');
        lbl.setAttribute('class', 'axis-label');
        lbl.textContent = d.name;
        barsLayer.appendChild(lbl);
      });
    </script>
  </body>
  </html>
  """.trimIndent()
}

/**
 * Interactive Recharts / D3.js SVG WebView for Weekly Productivity Rate (7 Days)
 * Displays an Area + Line Chart for Weekly Productivity (%) + Grouped Bars for Sold vs Clocked Hours + 100% Target Line.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun RechartsD3WeeklyProductivityWebView(
  weeklyPoints: List<WeeklyProductivityDayPoint>,
  selectedCurveIndex: Int,
  modifier: Modifier = Modifier
) {
  val htmlContent = remember(weeklyPoints, selectedCurveIndex) {
    buildWeeklyProductivityD3Html(weeklyPoints, selectedCurveIndex)
  }

  AndroidView(
    factory = { ctx ->
      WebView(ctx).apply {
        layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
        )
        setBackgroundColor(android.graphics.Color.parseColor("#101418"))
        settings.apply {
          javaScriptEnabled = true
          domStorageEnabled = true
          loadWithOverviewMode = true
          useWideViewPort = true
          cacheMode = WebSettings.LOAD_NO_CACHE
        }
        webChromeClient = WebChromeClient()
        webViewClient = WebViewClient()
        loadDataWithBaseURL("https://mekanik.app/", htmlContent, "text/html", "UTF-8", null)
      }
    },
    update = { webView ->
      webView.loadDataWithBaseURL("https://mekanik.app/", htmlContent, "text/html", "UTF-8", null)
    },
    modifier = modifier
  )
}

private fun buildWeeklyProductivityD3Html(
  weeklyPoints: List<WeeklyProductivityDayPoint>,
  selectedCurveIndex: Int
): String {
  val curveName = when (selectedCurveIndex) {
    1 -> "Karim B. (Distribution)"
    2 -> "Yacine M. (Boîte/Embrayage)"
    3 -> "Sofiane R. (Freinage/Géométrie)"
    4 -> "Amine K. (Diag/ADAS)"
    else -> "Atelier Global (4 Compagnons)"
  }

  val jsonBuilder = StringBuilder("[")
  weeklyPoints.forEachIndexed { idx, pt ->
    val activePct = when (selectedCurveIndex) {
      1 -> pt.karimPct
      2 -> pt.yacinePct
      3 -> pt.sofianePct
      4 -> pt.aminePct
      else -> pt.productivityPct
    }
    jsonBuilder.append(
      """
      {
        "day": "${pt.dayCode}",
        "dayFull": "${pt.dayFullLabel}",
        "sold": ${pt.soldBaremeHours},
        "clocked": ${pt.clockedRealHours},
        "pct": $activePct,
        "vehicles": ${pt.vehiclesCompleted}
      }${if (idx < weeklyPoints.size - 1) "," else ""}
      """.trimIndent()
    )
  }
  jsonBuilder.append("]")

  return """
  <!DOCTYPE html>
  <html>
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
    <style>
      * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }
      body { background-color: #101418; color: #F5F7F9; overflow: hidden; padding: 6px 10px; }
      .chart-wrap { position: relative; width: 100%; height: 220px; }
      svg { width: 100%; height: 100%; }
      .grid-line { stroke: #252B31; stroke-dasharray: 3 3; }
      .target-line { stroke: #E53935; stroke-width: 1.5; stroke-dasharray: 5 3; }
      .axis-label { fill: #AEB6BF; font-size: 10px; font-weight: 600; }
      .tooltip {
        position: absolute; display: none; background: rgba(18, 24, 32, 0.96); border: 1px solid #00E676;
        border-radius: 8px; padding: 7px 10px; font-size: 10.5px; color: #F5F7F9;
        pointer-events: none; box-shadow: 0 4px 14px rgba(0,0,0,0.7); z-index: 99; white-space: nowrap;
      }
    </style>
  </head>
  <body>
    <div class="chart-wrap" id="wrap">
      <div class="tooltip" id="tip"></div>
      <svg viewBox="0 0 480 215" preserveAspectRatio="none">
        <defs>
          <linearGradient id="areaGrad" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stop-color="#00E676" stop-opacity="0.38" />
            <stop offset="100%" stop-color="#00E676" stop-opacity="0.02" />
          </linearGradient>
          <linearGradient id="soldBarGrad" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stop-color="#00E5FF" stop-opacity="0.55" />
            <stop offset="100%" stop-color="#00E5FF" stop-opacity="0.15" />
          </linearGradient>
        </defs>
        <line x1="36" y1="25" x2="462" y2="25" class="grid-line" />
        <line x1="36" y1="68" x2="462" y2="68" class="grid-line" />
        <line x1="36" y1="110" x2="462" y2="110" class="grid-line" />
        <line x1="36" y1="150" x2="462" y2="150" class="grid-line" />
        <line x1="36" y1="185" x2="462" y2="185" stroke="#374151" stroke-width="1.5" />

        <!-- Target 100% Productivity Line -->
        <line x1="36" y1="92" x2="462" y2="92" class="target-line" />
        <text x="458" y="87" text-anchor="end" fill="#E53935" font-size="8.5" font-weight="bold">OBJECTIF 100%</text>

        <g id="barsGroup"></g>
        <path id="areaPath" fill="url(#areaGrad)" />
        <path id="linePath" fill="none" stroke="#00E676" stroke-width="2.8" stroke-linecap="round" />
        <g id="nodesGroup"></g>
      </svg>
    </div>
    <script>
      const pts = $jsonBuilder;
      const curveTitle = "$curveName";
      const barsGroup = document.getElementById('barsGroup');
      const nodesGroup = document.getElementById('nodesGroup');
      const areaPath = document.getElementById('areaPath');
      const linePath = document.getElementById('linePath');
      const tip = document.getElementById('tip');

      const startX = 46;
      const baseY = 185;
      const chartW = 408;
      const chartH = 152;
      const stepX = chartW / Math.max(pts.length, 1);

      let coords = [];

      pts.forEach((d, i) => {
        const cx = startX + i * stepX + stepX / 2;

        // Background bars for Sold Bareme Hours vs Clocked Hours
        const barH = (d.sold / 42.0) * (chartH * 0.75);
        const barRect = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
        barRect.setAttribute('x', cx - 12);
        barRect.setAttribute('y', baseY - barH);
        barRect.setAttribute('width', 24);
        barRect.setAttribute('height', barH);
        barRect.setAttribute('rx', '3');
        barRect.setAttribute('fill', 'url(#soldBarGrad)');
        barsGroup.appendChild(barRect);

        // Map 70%..130% productivity onto y
        const clampedPct = Math.max(70, Math.min(132, d.pct));
        const y = baseY - ((clampedPct - 65) / 70.0) * chartH;
        coords.push({ x: cx, y: y, d: d });

        // X Axis Day Label
        const lbl = document.createElementNS('http://www.w3.org/2000/svg', 'text');
        lbl.setAttribute('x', cx);
        lbl.setAttribute('y', baseY + 16);
        lbl.setAttribute('text-anchor', 'middle');
        lbl.setAttribute('class', 'axis-label');
        lbl.textContent = d.day;
        nodesGroup.appendChild(lbl);
      });

      if (coords.length > 1) {
        const lineStr = 'M ' + coords.map(c => `${'$'}{c.x},${'$'}{c.y}`).join(' L ');
        linePath.setAttribute('d', lineStr);
        const areaStr = lineStr + ` L ${'$'}{coords[coords.length - 1].x},${'$'}{baseY} L ${'$'}{coords[0].x},${'$'}{baseY} Z`;
        areaPath.setAttribute('d', areaStr);
      }

      coords.forEach(c => {
        const showTip = () => {
          tip.style.display = 'block';
          tip.innerHTML = `
            <div style="color:#00E676;font-weight:800;">${'$'}{c.d.dayFull} • ${'$'}{curveTitle}</div>
            <div style="margin-top:2px;">⚡ Taux Productivité : <b>${'$'}{c.d.pct}%</b> (Cible 100%)</div>
            <div style="font-size:9.5px;color:#AEB6BF;">📘 Barème : ${'$'}{c.d.sold}h | ⏱️ Pointé : ${'$'}{c.d.clocked}h | 🚗 ${'$'}{c.d.vehicles} véhicules</div>
          `;
          tip.style.left = '14px';
          tip.style.top = '8px';
        };

        const dot = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        dot.setAttribute('cx', c.x);
        dot.setAttribute('cy', c.y);
        dot.setAttribute('r', '5');
        dot.setAttribute('fill', c.d.pct >= 100 ? '#00E676' : '#FF7A00');
        dot.setAttribute('stroke', '#101418');
        dot.setAttribute('stroke-width', '2');
        dot.style.cursor = 'pointer';
        dot.addEventListener('click', showTip);
        dot.addEventListener('touchstart', showTip);
        nodesGroup.appendChild(dot);

        const valTxt = document.createElementNS('http://www.w3.org/2000/svg', 'text');
        valTxt.setAttribute('x', c.x);
        valTxt.setAttribute('y', c.y - 9);
        valTxt.setAttribute('text-anchor', 'middle');
        valTxt.setAttribute('fill', c.d.pct >= 100 ? '#00E676' : '#FF7A00');
        valTxt.setAttribute('font-size', '9.5');
        valTxt.setAttribute('font-weight', 'bold');
        valTxt.textContent = c.d.pct + '%';
        nodesGroup.appendChild(valTxt);
      });
    </script>
  </body>
  </html>
  """.trimIndent()
}

/**
 * Native Compose Canvas fallback/high-FPS chart for Companion Work Time Allocation
 */
@Composable
private fun NativeCompanionWorkTimeCanvasChart(
  allocations: List<CompanionWorkTimeAllocation>,
  selectedCompanionId: String?,
  onSelectCompanion: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(GraphiteDark)
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Stacked vertical bars per companion
    Row(
      modifier = Modifier
        .weight(0.65f)
        .fillMaxHeight(),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.Bottom
    ) {
      allocations.forEach { item ->
        val isSelected = selectedCompanionId == item.id
        val maxHours = 45f
        val totalFrac = (item.totalClockedHours / maxHours).coerceIn(0.25f, 1f)

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Bottom,
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable { onSelectCompanion(item.id) }
        ) {
          Text(
            text = "${item.productivityRatePct}%",
            color = if (item.productivityRatePct >= 100) ElectricGreen else MechanicalOrange,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace
          )
          Spacer(modifier = Modifier.height(4.dp))
          Column(
            modifier = Modifier
              .width(if (isSelected) 30.dp else 24.dp)
              .fillMaxHeight(totalFrac)
              .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
          ) {
            if (item.internalMaintenanceHours > 0f) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .weight(item.internalMaintenanceHours.coerceAtLeast(0.2f))
                  .background(PerformanceRed)
              )
            }
            if (item.waitingPartsHours > 0f) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .weight(item.waitingPartsHours.coerceAtLeast(0.2f))
                  .background(MechanicalOrange)
              )
            }
            if (item.diagnosticAndTestHours > 0f) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .weight(item.diagnosticAndTestHours.coerceAtLeast(0.2f))
                  .background(TechCyan)
              )
            }
            if (item.productiveRepairHours > 0f) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .weight(item.productiveRepairHours.coerceAtLeast(0.2f))
                  .background(ElectricGreen)
              )
            }
          }
          Spacer(modifier = Modifier.height(5.dp))
          Text(
            text = item.shortName,
            color = if (isSelected) TechCyan else PureWhite,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
          )
        }
      }
    }

    // Native Donut Chart on the right
    val totProd = allocations.sumOf { it.productiveRepairHours.toDouble() }.toFloat()
    val totDiag = allocations.sumOf { it.diagnosticAndTestHours.toDouble() }.toFloat()
    val totWait = allocations.sumOf { it.waitingPartsHours.toDouble() }.toFloat()
    val totInt = allocations.sumOf { it.internalMaintenanceHours.toDouble() }.toFloat()
    val grandTotal = (totProd + totDiag + totWait + totInt).coerceAtLeast(1f)
    val billablePct = (((totProd + totDiag) / grandTotal) * 100f).toInt()

    Box(
      modifier = Modifier
        .weight(0.35f)
        .fillMaxHeight(),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.size(110.dp)) {
        val strokeWidth = 18.dp.toPx()
        val slices = listOf(
          totProd to ElectricGreen,
          totDiag to TechCyan,
          totWait to MechanicalOrange,
          totInt to PerformanceRed
        )
        var startAngle = -90f
        slices.forEach { (value, color) ->
          val sweep = (value / grandTotal) * 360f
          drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweep,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
          )
          startAngle += sweep
        }
      }
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "$billablePct%",
          color = PureWhite,
          fontWeight = FontWeight.Black,
          fontSize = 14.sp,
          fontFamily = FontFamily.Monospace
        )
        Text(
          text = "Facturable",
          color = ElectricGreen,
          fontSize = 8.5.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

/**
 * Native Compose Canvas fallback/high-FPS chart for Weekly Productivity Rate (7 Days)
 */
@Composable
private fun NativeWeeklyProductivityCanvasChart(
  weeklyPoints: List<WeeklyProductivityDayPoint>,
  selectedCurveIndex: Int,
  selectedDayIndex: Int,
  onSelectDay: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(GraphiteDark)
      .padding(12.dp)
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height - 24.dp.toPx()
      if (weeklyPoints.isEmpty() || w <= 0f || h <= 0f) return@Canvas

      val stepX = w / weeklyPoints.size
      // Target 100% dashed line
      val targetY = h - ((100f - 65f) / 70f) * h
      drawLine(
        color = PerformanceRed,
        start = Offset(0f, targetY),
        end = Offset(w, targetY),
        strokeWidth = 1.5.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
      )

      val points = weeklyPoints.mapIndexed { idx, pt ->
        val pct = when (selectedCurveIndex) {
          1 -> pt.karimPct
          2 -> pt.yacinePct
          3 -> pt.sofianePct
          4 -> pt.aminePct
          else -> pt.productivityPct
        }.coerceIn(70, 132)
        val cx = idx * stepX + stepX / 2f
        val cy = h - ((pct - 65f) / 70f) * h

        // Draw subtle sold hours bar
        val barH = (pt.soldBaremeHours / 42f) * (h * 0.7f)
        drawRoundRect(
          color = TechCyan.copy(alpha = if (idx == selectedDayIndex) 0.38f else 0.18f),
          topLeft = Offset(cx - 10.dp.toPx(), h - barH),
          size = Size(20.dp.toPx(), barH),
          cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
        Offset(cx, cy)
      }

      if (points.size > 1) {
        val linePath = Path().apply {
          moveTo(points.first().x, points.first().y)
          points.drop(1).forEach { lineTo(it.x, it.y) }
        }
        val areaPath = Path().apply {
          addPath(linePath)
          lineTo(points.last().x, h)
          lineTo(points.first().x, h)
          close()
        }
        drawPath(
          path = areaPath,
          brush = Brush.verticalGradient(
            colors = listOf(ElectricGreen.copy(alpha = 0.35f), Color.Transparent),
            startY = 0f,
            endY = h
          )
        )
        drawPath(
          path = linePath,
          color = ElectricGreen,
          style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )
      }

      points.forEachIndexed { idx, offset ->
        drawCircle(
          color = if (idx == selectedDayIndex) TechCyan else ElectricGreen,
          radius = if (idx == selectedDayIndex) 6.dp.toPx() else 4.dp.toPx(),
          center = offset
        )
      }
    }
  }
}
