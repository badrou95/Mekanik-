package com.example.ui.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.*
import com.example.data.repository.AdvancedModulesRepository
import com.example.data.repository.MekanikRepository
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

enum class RechartsAnalyticsDashboardTab(
  val title: String,
  val subtitle: String,
  val badge: String
) {
  PRODUCTIVITE_ET_POINTAGE(
    title = "Productivité & Pointage",
    subtitle = "Taux de productivité des mécaniciens (Barème CTH vs Pointé Réel) & Chronomètre Compagnon",
    badge = "RECHARTS + POINTAGE"
  ),
  GARANTIE_CESSION_CLIENT(
    title = "Garantie • Cession • Client",
    subtitle = "Répartition analytique des interventions et du CA HT entre Garantie, Cession Interne et Client",
    badge = "MULTI-PAYEURS"
  ),
  MENSUEL_SYSTEMES(
    title = "Activité Mensuelle",
    subtitle = "Évolution annuelle des réparations, heures facturées et répartition par système",
    badge = "12 MOIS"
  )
}

@Composable
fun WorkshopRechartsView(
  modifier: Modifier = Modifier,
  companionClocks: List<CompanionTimeClockEntry> = AdvancedModulesRepository.sampleCompanionClockEntries,
  multiPayerOrders: List<MultiPayerSplitOrder> = AdvancedModulesRepository.sampleMultiPayerSplitOrders,
  onUpdateCompanionClockStatus: (String, CompanionClockStatus, Int) -> Unit = { _, _, _ -> },
  onAddCompanionClockEntry: (
    companionName: String,
    matricule: String,
    specialty: String,
    repairOrderNumber: String,
    vehicleAndPlate: String,
    operationTitle: String,
    laborTier: WinMotorLaborTier,
    soldCentiemes: Int,
    bayName: String
  ) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
  onUpdateMultiPayerSplit: (String, Int, Int, Int) -> Unit = { _, _, _, _ -> },
  onOpenNewQuote: () -> Unit = {},
  onExportReport: () -> Unit = {}
) {
  var activeAnalyticsTab by remember { mutableStateOf(RechartsAnalyticsDashboardTab.PRODUCTIVITE_ET_POINTAGE) }
  var selectedMetric by remember { mutableStateOf(ChartMetricType.REPAIRS_COUNT) }
  var selectedPeriod by remember { mutableStateOf(ChartViewPeriod.FULL_YEAR) }
  var isWebViewMode by remember { mutableStateOf(true) }
  var selectedMonthIndex by remember { mutableIntStateOf(7) }
  var clockStatusFilter by remember { mutableStateOf<CompanionClockStatus?>(null) }
  var showAddClockForm by remember { mutableStateOf(false) }

  // New Clock Entry Form State
  var newMechanicName by remember { mutableStateOf("Karim Benali") }
  var newMechanicMatricule by remember { mutableStateOf("COMP-DZ-01") }
  var newMechanicSpecialty by remember { mutableStateOf("Motoriste & Distribution") }
  var newOrNumber by remember { mutableStateOf("OR-2026-8904") }
  var newVehicleLabel by remember { mutableStateOf("VW Golf 8 2.0 TDI • 09841-123-16") }
  var newOperationLabel by remember { mutableStateOf("Kit Distribution + Pompe à Eau Haute Pression") }
  var newSelectedTier by remember { mutableStateOf(WinMotorLaborTier.T2_MECANIQUE_COMPLEXE) }
  var newBaremeCth by remember { mutableIntStateOf(350) }
  var newBayLabel by remember { mutableStateOf("Pont Élévateur N°1") }

  val allStats = MekanikRepository.monthlyRepairStats
  val filteredStats = remember(selectedPeriod) {
    when (selectedPeriod) {
      ChartViewPeriod.FULL_YEAR -> allStats
      ChartViewPeriod.LAST_6_MONTHS -> allStats.takeLast(6)
      ChartViewPeriod.CURRENT_QUARTER -> allStats.subList(6, 9.coerceAtMost(allStats.size))
    }
  }

  val kpiSummary = remember { MekanikRepository.getWorkshopKpiSummary() }
  val systemBreakdowns = remember { MekanikRepository.getSystemBreakdownList() }
  val selectedMonthData = filteredStats.getOrNull(selectedMonthIndex.coerceIn(0, filteredStats.size - 1)) ?: filteredStats.last()

  // Computed Analytics for Mechanic Productivity
  val totalSoldCth = companionClocks.sumOf { it.soldCentiemesBarème }.coerceAtLeast(1)
  val totalClockedCth = companionClocks.sumOf { it.clockedCentiemesReal }.coerceAtLeast(1)
  val globalProductivityPct = ((totalSoldCth.toDouble() / totalClockedCth.toDouble()) * 100.0).toInt()
  val totalSavedCth = companionClocks.sumOf { it.timeSavedOrLostCentiemes }
  val activeCompanionsCount = companionClocks.count { it.status == CompanionClockStatus.EN_COURS }

  // Computed Analytics for Multi-Payer Split (Garantie / Cession / Client)
  val totalSplitOrdersDzd = multiPayerOrders.sumOf { it.totalOrderAmountHtDzd.toLong() }.coerceAtLeast(1L)
  val totalClientDzd = multiPayerOrders.sumOf { it.clientAmountDzd.toLong() }
  val totalWarrantyDzd = multiPayerOrders.sumOf { it.warrantyAmountDzd.toLong() }
  val totalCessionDzd = multiPayerOrders.sumOf { it.internalCessionAmountDzd.toLong() }
  val clientGlobalPct = ((totalClientDzd * 100.0) / totalSplitOrdersDzd).toInt()
  val warrantyGlobalPct = ((totalWarrantyDzd * 100.0) / totalSplitOrdersDzd).toInt()
  val cessionGlobalPct = (100 - clientGlobalPct - warrantyGlobalPct).coerceAtLeast(0)

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("workshop_recharts_view"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header & Recharts Dashboard Mode Selector
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MetalCard),
      border = BorderStroke(1.dp, PerformanceRed.copy(alpha = 0.6f))
    ) {
      Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                .background(PerformanceRed.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Analytics,
                contentDescription = null,
                tint = PerformanceRedGlow,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "TABLEAU DE BORD ANALYTIQUE RECHARTS & POINTAGE",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = PureWhite,
                  letterSpacing = 0.8.sp
                )
              )
              Text(
                text = activeAnalyticsTab.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumLight,
                  fontSize = 10.5.sp
                )
              )
            }
          }

          // Toggle Mode: Recharts WebView / Native Compose
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = GraphiteBlack,
            border = BorderStroke(1.dp, if (isWebViewMode) TechCyan.copy(alpha = 0.6f) else MetalBorder),
            modifier = Modifier
              .clickable { isWebViewMode = !isWebViewMode }
              .testTag("toggle_recharts_engine_mode")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                if (isWebViewMode) Icons.Default.Web else Icons.Default.Smartphone,
                contentDescription = null,
                tint = if (isWebViewMode) TechCyan else AluminumLight,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isWebViewMode) "Recharts.js" else "Canvas Natif",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isWebViewMode) TechCyan else AluminumLight
                )
              )
            }
          }
        }

        // 3 Analytical Tabs Selector
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          RechartsAnalyticsDashboardTab.entries.forEach { tab ->
            val isSelected = tab == activeAnalyticsTab
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) PerformanceRed else GraphiteBlack,
              border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
              onClick = { activeAnalyticsTab = tab },
              modifier = Modifier
                .weight(1f)
                .testTag("recharts_tab_${tab.name.lowercase()}")
            ) {
              Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = tab.badge,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) PureWhite.copy(alpha = 0.9f) else TechCyan
                  )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = tab.title,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.5.sp,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                    color = if (isSelected) PureWhite else AluminumLight,
                    textAlign = TextAlign.Center
                  ),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }
        }
      }
    }

    // =========================================================================
    // TAB 1 : TAUX DE PRODUCTIVITÉ DES MÉCANICIENS & MODULE DE POINTAGE
    // =========================================================================
    if (activeAnalyticsTab == RechartsAnalyticsDashboardTab.PRODUCTIVITE_ET_POINTAGE) {
      // KPI Row for Mechanic Productivity
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        KpiMetricCard(
          title = "Productivité Globale",
          value = "$globalProductivityPct%",
          subText = if (globalProductivityPct >= 100) "Objectif 100% dépassé (+${globalProductivityPct - 100}%)" else "Sous objectif 100%",
          icon = Icons.Default.Speed,
          accentColor = if (globalProductivityPct >= 100) ElectricGreen else MechanicalOrange,
          modifier = Modifier.weight(1f)
        )
        KpiMetricCard(
          title = "Vendu vs Pointé",
          value = "${totalSoldCth} / ${totalClockedCth} cth",
          subText = "Gain net : ${if (totalSavedCth >= 0) "+$totalSavedCth" else "$totalSavedCth"} cth",
          icon = Icons.Default.Timer,
          accentColor = TechCyan,
          modifier = Modifier.weight(1f)
        )
        KpiMetricCard(
          title = "Compagnons Actifs",
          value = "$activeCompanionsCount / ${companionClocks.size}",
          subText = "Pointage en direct sur ponts",
          icon = Icons.Default.Engineering,
          accentColor = PerformanceRed,
          modifier = Modifier.weight(1f)
        )
      }

      // Recharts Productivity Chart Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("recharts_productivity_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "GRAPHIQUE RECHARTS : PRODUCTIVITÉ DES MÉCANICIENS",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = TechCyan,
                  letterSpacing = 0.8.sp
                )
              )
              Text(
                text = "Temps Barème Vendu (CTH) vs Temps Pointé Réel (CTH) & Courbe d'Efficacité (%)",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 10.5.sp)
              )
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = ElectricGreenDark,
              border = BorderStroke(1.dp, ElectricGreen)
            ) {
              Text(
                text = "CIBLE 100%",
                color = ElectricGreen,
                fontWeight = FontWeight.Black,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }

          // Legend
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            LegendBadge(color = TechCyan, label = "Barème Vendu (CTH)")
            LegendBadge(color = MechanicalOrange, label = "Pointé Réel (CTH)")
            LegendBadge(color = ElectricGreen, label = "Taux Productivité (%)")
            LegendBadge(color = PerformanceRedGlow, label = "Seuil Cible 100%")
          }

          if (isWebViewMode) {
            RechartsProductivityWebView(
              entries = companionClocks,
              modifier = Modifier
                .fillMaxWidth()
                .height(275.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GraphiteDark)
            )
          } else {
            NativeMechanicProductivityChart(
              entries = companionClocks,
              modifier = Modifier
                .fillMaxWidth()
                .height(275.dp)
            )
          }
        }
      }

      // =========================================================================
      // MODULE DE POINTAGE COMPAGNON (CHRONOMÈTRE & BADGEUSE ATELIER)
      // =========================================================================
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("companion_time_clock_module_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.2.dp, MechanicalOrange.copy(alpha = 0.65f))
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(MechanicalOrange.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.PunchClock, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "MODULE DE POINTAGE COMPAGNONS (TEMPS RÉEL)",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    fontSize = 13.sp
                  )
                )
                Text(
                  text = "Badgeuse atelier en centièmes d'heure (1h00 = 100 cth) • Synchronisé avec Recharts",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 10.5.sp)
                )
              }
            }

            Button(
              onClick = { showAddClockForm = !showAddClockForm },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (showAddClockForm) MetalSurface else PerformanceRed
              ),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
              modifier = Modifier.testTag("btn_toggle_add_clock_entry")
            ) {
              Icon(
                if (showAddClockForm) Icons.Default.Close else Icons.Default.AddAlarm,
                contentDescription = null,
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (showAddClockForm) "FERMER" else "+ POINTAGE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
              )
            }
          }

          // Quick Add Companion Time Clock Form
          AnimatedVisibility(visible = showAddClockForm) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = GraphiteBlack,
              border = BorderStroke(1.dp, TechCyan),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("form_add_companion_clock")
            ) {
              Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                  text = "NOUVEL ORDRE DE POINTAGE COMPAGNON",
                  color = TechCyan,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp
                )

                // Preset Mechanics Selector
                Text(
                  text = "Sélection rapide du compagnon :",
                  color = AluminumMuted,
                  fontSize = 10.sp
                )
                val presetMechanics = listOf(
                  Triple("Karim Benali", "COMP-DZ-01", "Motoriste & Distribution"),
                  Triple("Yacine Mansouri", "COMP-DZ-02", "Diagnostic Électronique & ADAS"),
                  Triple("Sofiane Hadj", "COMP-DZ-03", "Freinage, Train Roulant & Embrayage"),
                  Triple("Mourad Touati", "COMP-DZ-04", "Service Rapide & Climatisation A/C")
                )
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  presetMechanics.forEach { (name, mat, spec) ->
                    val selected = newMechanicName == name
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = if (selected) TechCyanDark else MetalCard,
                      border = BorderStroke(1.dp, if (selected) TechCyan else MetalBorder),
                      onClick = {
                        newMechanicName = name
                        newMechanicMatricule = mat
                        newMechanicSpecialty = spec
                      }
                    ) {
                      Text(
                        text = "$name ($mat)",
                        color = if (selected) PureWhite else AluminumLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                      )
                    }
                  }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                  OutlinedTextField(
                    value = newOrNumber,
                    onValueChange = { newOrNumber = it },
                    label = { Text("N° OR", fontSize = 10.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(0.9f)
                  )
                  OutlinedTextField(
                    value = newBayLabel,
                    onValueChange = { newBayLabel = it },
                    label = { Text("Pont / Baie", fontSize = 10.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1.1f)
                  )
                }

                OutlinedTextField(
                  value = newVehicleLabel,
                  onValueChange = { newVehicleLabel = it },
                  label = { Text("Véhicule & Immatriculation", fontSize = 10.sp) },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                  value = newOperationLabel,
                  onValueChange = { newOperationLabel = it },
                  label = { Text("Opération Barèmée", fontSize = 10.sp) },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth()
                )

                // Tier & Barème CTH selector
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    WinMotorLaborTier.entries.forEach { tier ->
                      val isTierSelected = tier == newSelectedTier
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isTierSelected) MechanicalOrange.copy(alpha = 0.25f) else MetalCard,
                        border = BorderStroke(1.dp, if (isTierSelected) MechanicalOrange else MetalBorder),
                        onClick = { newSelectedTier = tier }
                      ) {
                        Text(
                          text = tier.code,
                          color = if (isTierSelected) MechanicalOrange else AluminumLight,
                          fontWeight = FontWeight.Black,
                          fontSize = 10.sp,
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                      }
                    }
                  }

                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Barème :", color = AluminumLight, fontSize = 10.sp)
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = MetalCard,
                      border = BorderStroke(1.dp, MetalBorder),
                      onClick = { newBaremeCth = (newBaremeCth - 50).coerceAtLeast(50) }
                    ) {
                      Text("-50", color = PureWhite, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
                    }
                    Text(
                      text = "$newBaremeCth cth",
                      color = TechCyan,
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.sp
                    )
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = MetalCard,
                      border = BorderStroke(1.dp, MetalBorder),
                      onClick = { newBaremeCth += 50 }
                    ) {
                      Text("+50", color = PureWhite, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
                    }
                  }
                }

                Button(
                  onClick = {
                    onAddCompanionClockEntry(
                      newMechanicName,
                      newMechanicMatricule,
                      newMechanicSpecialty,
                      newOrNumber,
                      newVehicleLabel,
                      newOperationLabel,
                      newSelectedTier,
                      newBaremeCth,
                      newBayLabel
                    )
                    showAddClockForm = false
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = ElectricGreenDark),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_confirm_new_clock_entry")
                ) {
                  Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "LANCER LE POINTAGE SUR ${newBayLabel.uppercase()}",
                    color = ElectricGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                  )
                }
              }
            }
          }

          // Filter Pills for Clock Status
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            val allSelected = clockStatusFilter == null
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (allSelected) PerformanceRedDark else GraphiteBlack,
              border = BorderStroke(1.dp, if (allSelected) PerformanceRed else MetalBorder),
              onClick = { clockStatusFilter = null }
            ) {
              Text(
                text = "Tous (${companionClocks.size})",
                color = if (allSelected) PureWhite else AluminumLight,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              )
            }
            CompanionClockStatus.entries.forEach { status ->
              val count = companionClocks.count { it.status == status }
              val isSelected = clockStatusFilter == status
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) PerformanceRedDark else GraphiteBlack,
                border = BorderStroke(1.dp, if (isSelected) PerformanceRed else MetalBorder),
                onClick = { clockStatusFilter = status }
              ) {
                Text(
                  text = "${status.badgeText} ($count)",
                  color = if (isSelected) PureWhite else AluminumLight,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
              }
            }
          }

          // Companion Time Clock Cards List
          val displayedClocks = companionClocks.filter { clockStatusFilter == null || it.status == clockStatusFilter }
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            displayedClocks.forEach { entry ->
              CompanionTimeClockCard(
                entry = entry,
                onUpdateStatus = { newStatus, deltaCth ->
                  onUpdateCompanionClockStatus(entry.id, newStatus, deltaCth)
                }
              )
            }
          }
        }
      }
    }

    // =========================================================================
    // TAB 2 : RÉPARTITION DES INTERVENTIONS (GARANTIE • CESSION • CLIENT)
    // =========================================================================
    if (activeAnalyticsTab == RechartsAnalyticsDashboardTab.GARANTIE_CESSION_CLIENT) {
      // Summary KPI Cards for Garantie / Cession / Client
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        KpiMetricCard(
          title = "Client Direct ($clientGlobalPct%)",
          value = formatDzd(totalClientDzd),
          subText = "Facturation Comptoir / CIB",
          icon = Icons.Default.Person,
          accentColor = TechCyan,
          modifier = Modifier.weight(1f)
        )
        KpiMetricCard(
          title = "Garantie OEM ($warrantyGlobalPct%)",
          value = formatDzd(totalWarrantyDzd),
          subText = "Prise en charge Constructeur",
          icon = Icons.Default.VerifiedUser,
          accentColor = MechanicalOrange,
          modifier = Modifier.weight(1f)
        )
        KpiMetricCard(
          title = "Cession Interne ($cessionGlobalPct%)",
          value = formatDzd(totalCessionDzd),
          subText = "Atelier / Préparation VO",
          icon = Icons.Default.Domain,
          accentColor = PerformanceRed,
          modifier = Modifier.weight(1f)
        )
      }

      // Recharts Multi-Payer Breakdown Chart Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("recharts_multipayer_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "GRAPHIQUE RECHARTS : GARANTIE • CESSION • CLIENT",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = MechanicalOrange,
                  letterSpacing = 0.8.sp
                )
              )
              Text(
                text = "Répartition globale (Donut) & Ventilation par Ordre de Réparation (Barres Empilées)",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 10.5.sp)
              )
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = GraphiteBlack,
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Text(
                text = "${multiPayerOrders.size} OR VENTILÉS",
                color = PureWhite,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 9.5.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            LegendBadge(color = TechCyan, label = "Client Direct ($clientGlobalPct%)")
            LegendBadge(color = MechanicalOrange, label = "Garantie Constructeur ($warrantyGlobalPct%)")
            LegendBadge(color = PerformanceRed, label = "Cession Interne ($cessionGlobalPct%)")
          }

          if (isWebViewMode) {
            RechartsMultiPayerWebView(
              orders = multiPayerOrders,
              clientPct = clientGlobalPct,
              warrantyPct = warrantyGlobalPct,
              cessionPct = cessionGlobalPct,
              totalClientDzd = totalClientDzd,
              totalWarrantyDzd = totalWarrantyDzd,
              totalCessionDzd = totalCessionDzd,
              modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GraphiteDark)
            )
          } else {
            NativeMultiPayerBreakdownChart(
              orders = multiPayerOrders,
              clientPct = clientGlobalPct,
              warrantyPct = warrantyGlobalPct,
              cessionPct = cessionGlobalPct,
              modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
            )
          }
        }
      }

      // Interactive Multi-Payer Orders Adjustment List
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "VENTILATION MULTI-PAYEURS PAR ORDRE DE RÉPARATION",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Black,
              color = PureWhite,
              letterSpacing = 0.8.sp
            )
          )
          Text(
            text = "Ajustez la prise en charge (Client / Garantie Constructeur / Cession) pour mettre à jour les graphiques Recharts en direct :",
            style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 10.5.sp)
          )

          multiPayerOrders.forEach { order ->
            MultiPayerOrderInteractiveCard(
              order = order,
              onApplySplit = { cPct, wPct, iPct ->
                onUpdateMultiPayerSplit(order.id, cPct, wPct, iPct)
              }
            )
          }
        }
      }
    }

    // =========================================================================
    // TAB 3 : STATISTIQUES MENSUELLES DES RÉPARATIONS PAR SYSTÈME
    // =========================================================================
    if (activeAnalyticsTab == RechartsAnalyticsDashboardTab.MENSUEL_SYSTEMES) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          // Metric Selector Buttons Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            ChartMetricType.values().forEach { metric ->
              val isSelected = metric == selectedMetric
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) PerformanceRedDark else GraphiteBlack,
                border = BorderStroke(1.dp, if (isSelected) PerformanceRed else MetalBorder),
                modifier = Modifier
                  .weight(1f)
                  .clickable { selectedMetric = metric }
              ) {
                Column(
                  modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = metric.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 10.sp,
                      fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                      color = if (isSelected) PureWhite else AluminumLight,
                      textAlign = TextAlign.Center
                    ),
                    maxLines = 1
                  )
                }
              }
            }
          }

          // Time Period Filter
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Période :",
              style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 11.sp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              ChartViewPeriod.values().forEach { period ->
                val isPeriodSelected = period == selectedPeriod
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = if (isPeriodSelected) MetalSurface else GraphiteDark,
                  border = BorderStroke(1.dp, if (isPeriodSelected) MechanicalOrange else MetalBorder),
                  modifier = Modifier.clickable { selectedPeriod = period }
                ) {
                  Text(
                    text = period.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 9.5.sp,
                      color = if (isPeriodSelected) MechanicalOrange else AluminumGray,
                      fontWeight = if (isPeriodSelected) FontWeight.Bold else FontWeight.Normal
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }
        }
      }

      // Chart Display Container (Recharts WebView or Compose Canvas)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("recharts_chart_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, PerformanceRed.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "STATISTIQUES MENSUELLES DES RÉPARATIONS",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = PerformanceRedGlow,
                  letterSpacing = 1.sp
                )
              )
              Text(
                text = "Atelier Garage Auto Performance Alger • Année 2026",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumGray,
                  fontSize = 11.sp
                )
              )
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = GraphiteBlack,
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Text(
                text = "${filteredStats.size} MOIS",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  color = PureWhite,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            LegendBadge(color = Color(0xFFE53935), label = "Freinage")
            LegendBadge(color = Color(0xFFFF7A00), label = "Moteur/Turbo")
            LegendBadge(color = Color(0xFF00E5FF), label = "Distribution")
            LegendBadge(color = Color(0xFF2979FF), label = "Clim/Thermique")
            LegendBadge(color = Color(0xFF7C4DFF), label = "Électricité")
            LegendBadge(color = Color(0xFF00E676), label = "Vidange")
          }

          Spacer(modifier = Modifier.height(14.dp))

          if (isWebViewMode) {
            RechartsWebViewContainer(
              stats = filteredStats,
              metric = selectedMetric,
              modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GraphiteDark)
            )
          } else {
            NativeAnimatedStatsChart(
              stats = filteredStats,
              metric = selectedMetric,
              selectedMonthIndex = selectedMonthIndex,
              onSelectMonth = { selectedMonthIndex = it },
              modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = GraphiteBlack,
            border = BorderStroke(1.dp, MetalBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = selectedMonthData.monthFull,
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = PureWhite
                    )
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = TechCyanDark
                  ) {
                    Text(
                      text = "${selectedMonthData.totalRepairs} réparations",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        color = TechCyanGlow,
                        fontWeight = FontWeight.Bold
                      ),
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                  }
                }
                Text(
                  text = "CA : ${formatDzd(selectedMonthData.revenueDzd)} • MO : ${selectedMonthData.laborHoursWorked}h (${formatDzd(selectedMonthData.laborRevenueDzd)})",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = AluminumLight,
                    fontSize = 11.5.sp
                  )
                )
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "Panier Moyen",
                  style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 9.5.sp)
                )
                Text(
                  text = formatDzd(selectedMonthData.avgTicketDzd),
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MechanicalOrange,
                    fontFamily = FontFamily.Monospace
                  )
                )
              }
            }
          }
        }
      }

      // KPI Summary Metrics Cards (4-Grid)
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "INDICATEURS CLÉS DE PERFORMANCE ATELIER (2026)",
          style = MaterialTheme.typography.labelSmall.copy(
            color = AluminumMuted,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
          )
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          KpiMetricCard(
            title = "Total Réparations",
            value = "${kpiSummary.totalRepairsYear}",
            subText = "+${kpiSummary.monthlyGrowthPct}% vs 2025",
            icon = Icons.Default.Build,
            accentColor = PerformanceRed,
            modifier = Modifier.weight(1f)
          )
          KpiMetricCard(
            title = "Chiffre d'Affaires",
            value = formatDzd(kpiSummary.totalRevenueYearDzd),
            subText = "Pièces + Main d'œuvre",
            icon = Icons.Default.AccountBalanceWallet,
            accentColor = MechanicalOrange,
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          KpiMetricCard(
            title = "Heures Facturées",
            value = "${kpiSummary.totalLaborHoursYear.toInt()} h",
            subText = "Moyenne 2.4h / véhicule",
            icon = Icons.Default.Timer,
            accentColor = TechCyan,
            modifier = Modifier.weight(1f)
          )
          KpiMetricCard(
            title = "Satisfaction Client",
            value = "${kpiSummary.satisfactionRatePct}%",
            subText = "4 techniciens qualifiés",
            icon = Icons.Default.ThumbUp,
            accentColor = ElectricGreen,
            modifier = Modifier.weight(1f)
          )
        }
      }

      // System Category Breakdown List
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.PieChart,
                contentDescription = null,
                tint = MechanicalOrange,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "RÉPARTITION DES RÉPARATIONS PAR SYSTÈME",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = PureWhite,
                  letterSpacing = 1.sp
                )
              )
            }

            Text(
              text = "Part Volume",
              style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 10.sp)
            )
          }

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            systemBreakdowns.forEach { breakdown ->
              SystemBreakdownRow(breakdown = breakdown)
            }
          }
        }
      }
    }

    // Action Buttons: [ Exporter Rapport ] [ Nouveau Devis ]
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      MekanikPrimaryButton(
        text = "NOUVEL ORDRE / DEVIS",
        onClick = onOpenNewQuote,
        icon = Icons.Default.Add,
        modifier = Modifier.weight(1.2f),
        testTag = "btn_create_quote_from_stats"
      )
      MekanikSecondaryButton(
        text = "RAPPORT PDF",
        onClick = onExportReport,
        icon = Icons.Default.ReceiptLong,
        modifier = Modifier.weight(0.8f),
        testTag = "btn_export_stats_pdf"
      )
    }
  }
}

@Composable
private fun CompanionTimeClockCard(
  entry: CompanionTimeClockEntry,
  onUpdateStatus: (CompanionClockStatus, Int) -> Unit
) {
  val prodPct = entry.productivityPercent
  val prodColor = when {
    prodPct >= 105 -> ElectricGreen
    prodPct >= 95 -> TechCyan
    else -> MechanicalOrange
  }
  val statusColor = when (entry.status) {
    CompanionClockStatus.EN_COURS -> ElectricGreen
    CompanionClockStatus.EN_PAUSE_PIECE -> MechanicalOrange
    CompanionClockStatus.TERMINE -> TechCyan
  }
  val progressRatio = (entry.clockedCentiemesReal.toFloat() / entry.soldCentiemesBarème.coerceAtLeast(1).toFloat()).coerceIn(0.05f, 1.0f)

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = GraphiteBlack,
    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f)),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("clock_card_${entry.id}")
  ) {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(prodColor.copy(alpha = 0.2f))
              .border(1.dp, prodColor, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "${prodPct}%",
              color = prodColor,
              fontWeight = FontWeight.Black,
              fontSize = 9.5.sp,
              fontFamily = FontFamily.Monospace
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = entry.companionName,
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "• ${entry.companionMatricule}",
                color = AluminumMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
              )
            }
            Text(
              text = "${entry.specialty} • ${entry.bayName}",
              color = AluminumLight,
              fontSize = 10.5.sp
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = statusColor.copy(alpha = 0.18f),
          border = BorderStroke(1.dp, statusColor)
        ) {
          Text(
            text = entry.status.badgeText,
            color = statusColor,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }
      }

      // Vehicle & Operation Info
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = MetalCard,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "${entry.repairOrderNumber} • ${entry.vehicleAndPlate}",
              color = TechCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp
            )
            Text(
              text = entry.laborTier.code,
              color = MechanicalOrange,
              fontWeight = FontWeight.Black,
              fontSize = 10.sp
            )
          }
          Text(
            text = entry.operationTitle,
            color = OffWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Barème CTH vs Pointé CTH Progress Bar
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Barème Vendu : ${entry.soldCentiemesBarème} cth (${String.format(Locale.US, "%.2f", entry.soldCentiemesBarème / 100.0)}h)",
            color = TechCyan,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
          )
          val diff = entry.timeSavedOrLostCentiemes
          Text(
            text = "Pointé Réel : ${entry.clockedCentiemesReal} cth (${if (diff >= 0) "Gain +$diff cth" else "Dépassement $diff cth"})",
            color = prodColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(7.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MetalSurface)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth(progressRatio)
              .fillMaxHeight()
              .background(
                Brush.horizontalGradient(listOf(TechCyan, prodColor))
              )
          )
        }
      }

      // Action Buttons for Clocking & Time Adjustment
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Time adjustment buttons
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MetalCard,
            border = BorderStroke(1.dp, MetalBorder),
            onClick = { onUpdateStatus(entry.status, -25) },
            modifier = Modifier.testTag("btn_clock_minus_${entry.id}")
          ) {
            Text(
              text = "-25 cth",
              color = AluminumLight,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp)
            )
          }
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MetalCard,
            border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.6f)),
            onClick = { onUpdateStatus(CompanionClockStatus.EN_COURS, 25) },
            modifier = Modifier.testTag("btn_clock_plus25_${entry.id}")
          ) {
            Text(
              text = "+25 cth",
              color = TechCyan,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp)
            )
          }
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MetalCard,
            border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.6f)),
            onClick = { onUpdateStatus(CompanionClockStatus.EN_COURS, 50) },
            modifier = Modifier.testTag("btn_clock_plus50_${entry.id}")
          ) {
            Text(
              text = "+50 cth",
              color = MechanicalOrange,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp)
            )
          }
        }

        // Status transitions: Démarrer / Pause Pièce / Terminer
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          if (entry.status != CompanionClockStatus.EN_COURS) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = ElectricGreenDark,
              border = BorderStroke(1.dp, ElectricGreen),
              onClick = { onUpdateStatus(CompanionClockStatus.EN_COURS, 0) }
            ) {
              Text(
                text = "▶ REPRENDRE",
                color = ElectricGreen,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
              )
            }
          }
          if (entry.status == CompanionClockStatus.EN_COURS) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFF2B2114),
              border = BorderStroke(1.dp, MechanicalOrange),
              onClick = { onUpdateStatus(CompanionClockStatus.EN_PAUSE_PIECE, 0) }
            ) {
              Text(
                text = "⏸ PAUSE PIÈCE",
                color = MechanicalOrange,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
              )
            }
          }
          if (entry.status != CompanionClockStatus.TERMINE) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = TechCyanDark,
              border = BorderStroke(1.dp, TechCyan),
              onClick = { onUpdateStatus(CompanionClockStatus.TERMINE, 0) }
            ) {
              Text(
                text = "✓ CLÔTURER",
                color = TechCyan,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun MultiPayerOrderInteractiveCard(
  order: MultiPayerSplitOrder,
  onApplySplit: (Int, Int, Int) -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = GraphiteBlack,
    border = BorderStroke(1.dp, MetalBorder),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("multipayer_order_${order.id}")
  ) {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "${order.repairOrderNumber} • ${order.vehicleModel}",
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
          Text(
            text = "Client : ${order.clientName} • Dossier : ${order.warrantyClaimReference}",
            color = AluminumGray,
            fontSize = 10.sp
          )
        }
        Text(
          text = formatDzd(order.totalOrderAmountHtDzd.toLong()),
          color = PureWhite,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Black,
          fontSize = 12.5.sp
        )
      }

      // Stacked visual bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(10.dp)
          .clip(RoundedCornerShape(5.dp))
          .background(MetalSurface)
      ) {
        if (order.clientSharePercent > 0) {
          Box(
            modifier = Modifier
              .weight(order.clientSharePercent.toFloat().coerceAtLeast(1f))
              .fillMaxHeight()
              .background(TechCyan)
          )
        }
        if (order.manufacturerWarrantyPercent > 0) {
          Box(
            modifier = Modifier
              .weight(order.manufacturerWarrantyPercent.toFloat().coerceAtLeast(1f))
              .fillMaxHeight()
              .background(MechanicalOrange)
          )
        }
        if (order.internalCessionPercent > 0) {
          Box(
            modifier = Modifier
              .weight(order.internalCessionPercent.toFloat().coerceAtLeast(1f))
              .fillMaxHeight()
              .background(PerformanceRed)
          )
        }
      }

      // Breakdown labels
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Client: ${order.clientSharePercent}% (${formatDzd(order.clientAmountDzd.toLong())})",
          color = TechCyan,
          fontSize = 9.5.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Garantie: ${order.manufacturerWarrantyPercent}% (${formatDzd(order.warrantyAmountDzd.toLong())})",
          color = MechanicalOrange,
          fontSize = 9.5.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Cession: ${order.internalCessionPercent}% (${formatDzd(order.internalCessionAmountDzd.toLong())})",
          color = PerformanceRedGlow,
          fontSize = 9.5.sp,
          fontWeight = FontWeight.Bold
        )
      }

      // Preset split buttons to test live Recharts update
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        val presets = listOf(
          Triple(100, 0, 0) to "100% Client",
          Triple(30, 50, 20) to "30% Cli / 50% Gar / 20% Ces",
          Triple(20, 80, 0) to "80% Garantie OEM",
          Triple(0, 0, 100) to "100% Cession Interne",
          Triple(50, 25, 25) to "50% Cli / 25% Gar / 25% Ces"
        )
        presets.forEach { (split, label) ->
          val isCurrent = order.clientSharePercent == split.first &&
            order.manufacturerWarrantyPercent == split.second &&
            order.internalCessionPercent == split.third
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isCurrent) PerformanceRedDark else MetalCard,
            border = BorderStroke(1.dp, if (isCurrent) PerformanceRed else MetalBorder),
            onClick = { onApplySplit(split.first, split.second, split.third) }
          ) {
            Text(
              text = label,
              color = if (isCurrent) PureWhite else AluminumLight,
              fontSize = 9.sp,
              fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }
    }
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun RechartsProductivityWebView(
  entries: List<CompanionTimeClockEntry>,
  modifier: Modifier = Modifier
) {
  val htmlContent = remember(entries) {
    generateProductivityRechartsHtml(entries)
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

private fun generateProductivityRechartsHtml(entries: List<CompanionTimeClockEntry>): String {
  val jsonBuilder = StringBuilder("[")
  entries.forEachIndexed { idx, item ->
    val shortName = item.companionName.split(" ").firstOrNull() ?: item.companionName
    jsonBuilder.append(
      """
      {
        "name": "$shortName",
        "fullName": "${item.companionName}",
        "orNum": "${item.repairOrderNumber}",
        "soldCth": ${item.soldCentiemesBarème},
        "clockedCth": ${item.clockedCentiemesReal},
        "productivity": ${item.productivityPercent},
        "bay": "${item.bayName}"
      }${if (idx < entries.size - 1) "," else ""}
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
      body { background-color: #101418; color: #F5F7F9; overflow: hidden; padding: 8px 10px; }
      .chart-container { position: relative; width: 100%; height: 255px; }
      svg { width: 100%; height: 100%; }
      .grid-line { stroke: #252B31; stroke-dasharray: 3 3; }
      .target-line { stroke: #E53935; stroke-width: 1.5; stroke-dasharray: 5 3; }
      .axis-text { fill: #AEB6BF; font-size: 10px; font-weight: 600; }
      .bar-rect { cursor: pointer; transition: all 0.25s ease; }
      .bar-rect:hover { filter: brightness(1.25); }
      .tooltip {
        position: absolute; display: none; background: #181F26; border: 1px solid #00E5FF;
        border-radius: 8px; padding: 7px 10px; font-size: 11px; color: #fff;
        pointer-events: none; box-shadow: 0 4px 14px rgba(0,0,0,0.65); z-index: 100; white-space: nowrap;
      }
      .tt-title { font-weight: bold; color: #00E5FF; margin-bottom: 2px; }
      .tt-prod { color: #00E676; font-family: monospace; font-size: 12px; font-weight: bold; }
      .tt-sub { font-size: 9.5px; color: #AEB6BF; margin-top: 2px; }
    </style>
  </head>
  <body>
    <div class="chart-container" id="container">
      <div class="tooltip" id="tooltip"></div>
      <svg id="chartSvg" viewBox="0 0 500 245" preserveAspectRatio="none">
        <defs>
          <linearGradient id="soldGrad" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stop-color="#00E5FF" stop-opacity="0.95" />
            <stop offset="100%" stop-color="#007799" stop-opacity="0.5" />
          </linearGradient>
          <linearGradient id="clockedGrad" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stop-color="#FF7A00" stop-opacity="0.95" />
            <stop offset="100%" stop-color="#994400" stop-opacity="0.5" />
          </linearGradient>
        </defs>
        <line x1="38" y1="30" x2="475" y2="30" class="grid-line" />
        <line x1="38" y1="80" x2="475" y2="80" class="grid-line" />
        <line x1="38" y1="130" x2="475" y2="130" class="grid-line" />
        <line x1="38" y1="175" x2="475" y2="175" class="grid-line" />
        <line x1="38" y1="205" x2="475" y2="205" stroke="#374151" stroke-width="1.5" />
        <!-- Target 100% Productivity Line -->
        <line x1="38" y1="95" x2="475" y2="95" class="target-line" />
        <text x="472" y="90" text-anchor="end" fill="#E53935" font-size="9" font-weight="bold">CIBLE 100%</text>
        <g id="barsGroup"></g>
        <path id="prodLine" fill="none" stroke="#00E676" stroke-width="2.5" />
        <g id="pointsGroup"></g>
        <g id="labelsGroup"></g>
      </svg>
    </div>
    <script>
      const data = $jsonBuilder;
      const barsGroup = document.getElementById('barsGroup');
      const pointsGroup = document.getElementById('pointsGroup');
      const labelsGroup = document.getElementById('labelsGroup');
      const prodLine = document.getElementById('prodLine');
      const tooltip = document.getElementById('tooltip');
      const container = document.getElementById('container');

      const maxCth = Math.max(...data.map(d => Math.max(d.soldCth, d.clockedCth)), 400) * 1.18;
      const startX = 48;
      const startY = 205;
      const chartW = 420;
      const chartH = 165;
      const stepX = chartW / Math.max(data.length, 1);
      const singleBarW = Math.min(24, Math.max(10, stepX * 0.28));

      let prodLinePoints = [];

      data.forEach((d, i) => {
        const centerX = startX + (i * stepX) + (stepX / 2);
        const soldH = (d.soldCth / maxCth) * chartH;
        const clockedH = (d.clockedCth / maxCth) * chartH;
        const soldY = startY - soldH;
        const clockedY = startY - clockedH;

        // Sold Bar (Barème)
        const rectSold = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
        rectSold.setAttribute('x', centerX - singleBarW - 2);
        rectSold.setAttribute('y', soldY);
        rectSold.setAttribute('width', singleBarW);
        rectSold.setAttribute('height', soldH);
        rectSold.setAttribute('rx', '3');
        rectSold.setAttribute('fill', 'url(#soldGrad)');
        rectSold.setAttribute('class', 'bar-rect');

        // Clocked Bar (Pointé)
        const rectClocked = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
        rectClocked.setAttribute('x', centerX + 2);
        rectClocked.setAttribute('y', clockedY);
        rectClocked.setAttribute('width', singleBarW);
        rectClocked.setAttribute('height', clockedH);
        rectClocked.setAttribute('rx', '3');
        rectClocked.setAttribute('fill', 'url(#clockedGrad)');
        rectClocked.setAttribute('class', 'bar-rect');

        const showTip = (evt) => {
          tooltip.style.display = 'block';
          tooltip.innerHTML = `
            <div class="tt-title">${'$'}{d.fullName} (${'$'}{d.orNum})</div>
            <div class="tt-prod">⚡ Productivité : ${'$'}{d.productivity}%</div>
            <div class="tt-sub">📘 Barème Vendu : ${'$'}{d.soldCth} cth | ⏱️ Pointé : ${'$'}{d.clockedCth} cth</div>
            <div class="tt-sub">🛠️ Poste : ${'$'}{d.bay}</div>
          `;
          const cRect = container.getBoundingClientRect();
          tooltip.style.left = Math.min(Math.max(10, centerX * (cRect.width / 500) - 70), cRect.width - 195) + 'px';
          tooltip.style.top = '12px';
        };

        rectSold.addEventListener('click', showTip);
        rectSold.addEventListener('touchstart', showTip);
        rectClocked.addEventListener('click', showTip);
        rectClocked.addEventListener('touchstart', showTip);
        barsGroup.appendChild(rectSold);
        barsGroup.appendChild(rectClocked);

        // Productivity curve point (100% mapped around y=95)
        const prodY = Math.max(22, Math.min(190, 205 - ((d.productivity / 150.0) * 165)));
        prodLinePoints.push(`${'$'}{centerX},${'$'}{prodY}`);

        const circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        circle.setAttribute('cx', centerX);
        circle.setAttribute('cy', prodY);
        circle.setAttribute('r', '4.5');
        circle.setAttribute('fill', '#00E676');
        circle.setAttribute('stroke', '#101418');
        circle.setAttribute('stroke-width', '1.5');
        pointsGroup.appendChild(circle);

        // Productivity Badge Text above point
        const pText = document.createElementNS('http://www.w3.org/2000/svg', 'text');
        pText.setAttribute('x', centerX);
        pText.setAttribute('y', prodY - 8);
        pText.setAttribute('text-anchor', 'middle');
        pText.setAttribute('fill', '#00E676');
        pText.setAttribute('font-size', '9.5');
        pText.setAttribute('font-weight', 'bold');
        pText.textContent = d.productivity + '%';
        pointsGroup.appendChild(pText);

        // X Axis Label
        const label = document.createElementNS('http://www.w3.org/2000/svg', 'text');
        label.setAttribute('x', centerX);
        label.setAttribute('y', startY + 16);
        label.setAttribute('text-anchor', 'middle');
        label.setAttribute('class', 'axis-text');
        label.textContent = d.name;
        labelsGroup.appendChild(label);
      });

      if (prodLinePoints.length > 1) {
        prodLine.setAttribute('d', 'M ' + prodLinePoints.join(' L '));
      }
    </script>
  </body>
  </html>
  """.trimIndent()
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun RechartsMultiPayerWebView(
  orders: List<MultiPayerSplitOrder>,
  clientPct: Int,
  warrantyPct: Int,
  cessionPct: Int,
  totalClientDzd: Long,
  totalWarrantyDzd: Long,
  totalCessionDzd: Long,
  modifier: Modifier = Modifier
) {
  val htmlContent = remember(orders, clientPct, warrantyPct, cessionPct) {
    generateMultiPayerRechartsHtml(
      orders = orders,
      clientPct = clientPct,
      warrantyPct = warrantyPct,
      cessionPct = cessionPct,
      totalClientDzd = totalClientDzd,
      totalWarrantyDzd = totalWarrantyDzd,
      totalCessionDzd = totalCessionDzd
    )
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

private fun generateMultiPayerRechartsHtml(
  orders: List<MultiPayerSplitOrder>,
  clientPct: Int,
  warrantyPct: Int,
  cessionPct: Int,
  totalClientDzd: Long,
  totalWarrantyDzd: Long,
  totalCessionDzd: Long
): String {
  val ordersJson = StringBuilder("[")
  orders.forEachIndexed { idx, o ->
    ordersJson.append(
      """
      {
        "orNum": "${o.repairOrderNumber}",
        "clientPct": ${o.clientSharePercent},
        "warrantyPct": ${o.manufacturerWarrantyPercent},
        "cessionPct": ${o.internalCessionPercent},
        "totalDzd": ${o.totalOrderAmountHtDzd}
      }${if (idx < orders.size - 1) "," else ""}
      """.trimIndent()
    )
  }
  ordersJson.append("]")

  return """
  <!DOCTYPE html>
  <html>
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
    <style>
      * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }
      body { background-color: #101418; color: #F5F7F9; overflow: hidden; padding: 8px; }
      .wrapper { display: flex; flex-direction: row; align-items: center; justify-content: space-between; width: 100%; height: 260px; }
      .donut-pane { width: 44%; height: 100%; position: relative; display: flex; align-items: center; justify-content: center; }
      .bars-pane { width: 56%; height: 100%; }
      svg { width: 100%; height: 100%; }
      .axis-label { fill: #AEB6BF; font-size: 9.5px; font-weight: 600; }
    </style>
  </head>
  <body>
    <div class="wrapper">
      <div class="donut-pane">
        <svg viewBox="0 0 160 160">
          <!-- Recharts Donut Ring using stroke-dasharray on r=54 (circumference = 339.29) -->
          <circle cx="80" cy="80" r="54" fill="none" stroke="#252B31" stroke-width="20" />
          <circle id="sliceClient" cx="80" cy="80" r="54" fill="none" stroke="#00E5FF" stroke-width="20" transform="rotate(-90 80 80)" />
          <circle id="sliceWarranty" cx="80" cy="80" r="54" fill="none" stroke="#FF7A00" stroke-width="20" transform="rotate(-90 80 80)" />
          <circle id="sliceCession" cx="80" cy="80" r="54" fill="none" stroke="#E53935" stroke-width="20" transform="rotate(-90 80 80)" />
          <text x="80" y="75" text-anchor="middle" fill="#F5F7F9" font-size="11" font-weight="bold">RÉPARTITION</text>
          <text x="80" y="91" text-anchor="middle" fill="#00E5FF" font-size="10" font-family="monospace">${clientPct}% / ${warrantyPct}% / ${cessionPct}%</text>
        </svg>
      </div>
      <div class="bars-pane">
        <svg id="stackedSvg" viewBox="0 0 280 240" preserveAspectRatio="none">
          <line x1="25" y1="200" x2="265" y2="200" stroke="#374151" stroke-width="1.5" />
          <g id="stackedBars"></g>
        </svg>
      </div>
    </div>
    <script>
      const cPct = $clientPct;
      const wPct = $warrantyPct;
      const iPct = $cessionPct;
      const circ = 2 * Math.PI * 54;

      const cLen = (cPct / 100.0) * circ;
      const wLen = (wPct / 100.0) * circ;
      const iLen = (iPct / 100.0) * circ;

      const sliceClient = document.getElementById('sliceClient');
      const sliceWarranty = document.getElementById('sliceWarranty');
      const sliceCession = document.getElementById('sliceCession');

      sliceClient.setAttribute('stroke-dasharray', `${'$'}{cLen} ${'$'}{circ}`);
      sliceClient.setAttribute('stroke-dashoffset', '0');

      sliceWarranty.setAttribute('stroke-dasharray', `${'$'}{wLen} ${'$'}{circ}`);
      sliceWarranty.setAttribute('stroke-dashoffset', -cLen);

      sliceCession.setAttribute('stroke-dasharray', `${'$'}{iLen} ${'$'}{circ}`);
      sliceCession.setAttribute('stroke-dashoffset', -(cLen + wLen));

      const orders = $ordersJson;
      const stackedBars = document.getElementById('stackedBars');
      const startX = 32;
      const startY = 200;
      const chartH = 165;
      const stepX = 225 / Math.max(orders.length, 1);
      const barW = Math.min(34, stepX * 0.55);

      orders.forEach((o, idx) => {
        const x = startX + (idx * stepX) + (stepX / 2) - (barW / 2);
        const hClient = (o.clientPct / 100.0) * chartH;
        const hWarranty = (o.warrantyPct / 100.0) * chartH;
        const hCession = (o.cessionPct / 100.0) * chartH;

        let curY = startY;

        if (hClient > 0) {
          curY -= hClient;
          const r1 = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
          r1.setAttribute('x', x); r1.setAttribute('y', curY);
          r1.setAttribute('width', barW); r1.setAttribute('height', hClient);
          r1.setAttribute('fill', '#00E5FF');
          stackedBars.appendChild(r1);
        }
        if (hWarranty > 0) {
          curY -= hWarranty;
          const r2 = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
          r2.setAttribute('x', x); r2.setAttribute('y', curY);
          r2.setAttribute('width', barW); r2.setAttribute('height', hWarranty);
          r2.setAttribute('fill', '#FF7A00');
          stackedBars.appendChild(r2);
        }
        if (hCession > 0) {
          curY -= hCession;
          const r3 = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
          r3.setAttribute('x', x); r3.setAttribute('y', curY);
          r3.setAttribute('width', barW); r3.setAttribute('height', hCession);
          r3.setAttribute('fill', '#E53935');
          stackedBars.appendChild(r3);
        }

        const lbl = document.createElementNS('http://www.w3.org/2000/svg', 'text');
        lbl.setAttribute('x', x + barW / 2);
        lbl.setAttribute('y', startY + 15);
        lbl.setAttribute('text-anchor', 'middle');
        lbl.setAttribute('class', 'axis-label');
        lbl.textContent = o.orNum.replace('OR-2026-', 'OR-');
        stackedBars.appendChild(lbl);
      });
    </script>
  </body>
  </html>
  """.trimIndent()
}

@Composable
private fun NativeMechanicProductivityChart(
  entries: List<CompanionTimeClockEntry>,
  modifier: Modifier = Modifier
) {
  val maxCth = remember(entries) {
    (entries.maxOfOrNull { maxOf(it.soldCentiemesBarème, it.clockedCentiemesReal) }?.toFloat() ?: 400f) * 1.15f
  }

  Column(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(GraphiteDark)
      .padding(12.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.Bottom
    ) {
      entries.forEach { entry ->
        val soldFrac = (entry.soldCentiemesBarème / maxCth).coerceIn(0.1f, 1f)
        val clockedFrac = (entry.clockedCentiemesReal / maxCth).coerceIn(0.1f, 1f)
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Bottom,
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
        ) {
          Text(
            text = "${entry.productivityPercent}%",
            color = if (entry.productivityPercent >= 100) ElectricGreen else MechanicalOrange,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .width(14.dp)
                .fillMaxHeight(soldFrac)
                .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                .background(TechCyan)
            )
            Box(
              modifier = Modifier
                .width(14.dp)
                .fillMaxHeight(clockedFrac)
                .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                .background(MechanicalOrange)
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = entry.companionName.split(" ").firstOrNull() ?: entry.companionName,
            color = PureWhite,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
          )
        }
      }
    }
  }
}

@Composable
private fun NativeMultiPayerBreakdownChart(
  orders: List<MultiPayerSplitOrder>,
  clientPct: Int,
  warrantyPct: Int,
  cessionPct: Int,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(GraphiteDark)
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Box(
      modifier = Modifier
        .size(130.dp),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 22.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
        val arcSize = Size(diameter, diameter)

        val cSweep = (clientPct / 100f) * 360f
        val wSweep = (warrantyPct / 100f) * 360f
        val iSweep = 360f - cSweep - wSweep

        drawArc(
          color = TechCyan,
          startAngle = -90f,
          sweepAngle = cSweep,
          useCenter = false,
          topLeft = topLeft,
          size = arcSize,
          style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
        drawArc(
          color = MechanicalOrange,
          startAngle = -90f + cSweep,
          sweepAngle = wSweep,
          useCenter = false,
          topLeft = topLeft,
          size = arcSize,
          style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
        drawArc(
          color = PerformanceRed,
          startAngle = -90f + cSweep + wSweep,
          sweepAngle = iSweep,
          useCenter = false,
          topLeft = topLeft,
          size = arcSize,
          style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
        )
      }
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("MULTI-PAYEURS", color = AluminumLight, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        Text("$clientPct% / $warrantyPct% / $cessionPct%", color = PureWhite, fontSize = 9.5.sp, fontWeight = FontWeight.Black)
      }
    }

    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      orders.forEach { o ->
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(
            text = "${o.repairOrderNumber} (${o.clientSharePercent}% / ${o.manufacturerWarrantyPercent}% / ${o.internalCessionPercent}%)",
            color = OffWhite,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(MetalSurface)
          ) {
            if (o.clientSharePercent > 0) {
              Box(modifier = Modifier.weight(o.clientSharePercent.toFloat()).fillMaxHeight().background(TechCyan))
            }
            if (o.manufacturerWarrantyPercent > 0) {
              Box(modifier = Modifier.weight(o.manufacturerWarrantyPercent.toFloat()).fillMaxHeight().background(MechanicalOrange))
            }
            if (o.internalCessionPercent > 0) {
              Box(modifier = Modifier.weight(o.internalCessionPercent.toFloat()).fillMaxHeight().background(PerformanceRed))
            }
          }
        }
      }
    }
  }
}

@Composable
private fun LegendBadge(color: Color, label: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.sp,
        color = AluminumLight
      )
    )
  }
}

@Composable
private fun KpiMetricCard(
  title: String,
  value: String,
  subText: String,
  icon: ImageVector,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MetalCard,
    border = BorderStroke(1.dp, MetalBorder),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelSmall.copy(
            color = AluminumMuted,
            fontSize = 10.sp
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Icon(
          icon,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(15.dp)
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Black,
          color = PureWhite,
          fontFamily = FontFamily.Monospace,
          fontSize = 14.sp
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subText,
        style = MaterialTheme.typography.bodySmall.copy(
          color = accentColor,
          fontSize = 9.5.sp,
          fontWeight = FontWeight.Medium
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
private fun SystemBreakdownRow(breakdown: SystemRepairBreakdown) {
  val barColor = Color(android.graphics.Color.parseColor(breakdown.colorHex))

  Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = breakdown.systemName,
        style = MaterialTheme.typography.bodySmall.copy(
          color = PureWhite,
          fontWeight = FontWeight.Medium,
          fontSize = 11.5.sp
        )
      )
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "${breakdown.repairCount} répas",
          style = MaterialTheme.typography.labelSmall.copy(
            color = AluminumLight,
            fontSize = 10.sp
          )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = barColor.copy(alpha = 0.2f),
          border = BorderStroke(0.5.dp, barColor)
        ) {
          Text(
            text = "${breakdown.percentage}%",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = barColor,
              fontSize = 9.5.sp
            ),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
          )
        }
      }
    }

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp))
        .background(GraphiteBlack)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(breakdown.percentage / 100f)
          .fillMaxHeight()
          .clip(RoundedCornerShape(3.dp))
          .background(
            Brush.horizontalGradient(
              listOf(barColor.copy(alpha = 0.7f), barColor)
            )
          )
      )
    }
  }
}

@Composable
private fun NativeAnimatedStatsChart(
  stats: List<MonthlyRepairStats>,
  metric: ChartMetricType,
  selectedMonthIndex: Int,
  onSelectMonth: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val maxValue = remember(stats, metric) {
    val rawMax = when (metric) {
      ChartMetricType.REPAIRS_COUNT -> stats.maxOfOrNull { it.totalRepairs }?.toFloat() ?: 100f
      ChartMetricType.REVENUE_DZD -> stats.maxOfOrNull { it.revenueDzd }?.toFloat() ?: 4000000f
      ChartMetricType.LABOR_HOURS -> stats.maxOfOrNull { it.laborHoursWorked } ?: 500f
      ChartMetricType.AVG_TICKET -> stats.maxOfOrNull { it.avgTicketDzd }?.toFloat() ?: 40000f
    }
    (rawMax * 1.15f).coerceAtLeast(1f)
  }

  Column(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(GraphiteDark)
      .padding(horizontal = 8.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Max: ${formatMetricValue(maxValue.toLong(), metric)}",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          color = AluminumMuted,
          fontSize = 9.sp
        )
      )
      Text(
        text = "Échelle : ${metric.unit}",
        style = MaterialTheme.typography.labelSmall.copy(
          color = PerformanceRedGlow,
          fontSize = 9.sp
        )
      )
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(top = 10.dp, bottom = 6.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.Bottom
    ) {
      stats.forEachIndexed { index, item ->
        val itemValue = when (metric) {
          ChartMetricType.REPAIRS_COUNT -> item.totalRepairs.toFloat()
          ChartMetricType.REVENUE_DZD -> item.revenueDzd.toFloat()
          ChartMetricType.LABOR_HOURS -> item.laborHoursWorked
          ChartMetricType.AVG_TICKET -> item.avgTicketDzd.toFloat()
        }

        val heightFraction = (itemValue / maxValue).coerceIn(0.08f, 1.0f)
        val isSelected = index == selectedMonthIndex

        val animatedHeight by animateFloatAsState(
          targetValue = heightFraction,
          animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
          label = "bar_height_$index"
        )

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable { onSelectMonth(index) },
          verticalArrangement = Arrangement.Bottom
        ) {
          if (isSelected) {
            Surface(
              shape = RoundedCornerShape(3.dp),
              color = PerformanceRedDark,
              border = BorderStroke(0.5.dp, PerformanceRed)
            ) {
              Text(
                text = formatMetricShort(itemValue.toLong(), metric),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  color = PureWhite
                ),
                modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp)
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
          }

          Box(
            modifier = Modifier
              .fillMaxWidth(0.65f)
              .fillMaxHeight(animatedHeight)
              .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
              .background(
                if (isSelected) {
                  Brush.verticalGradient(
                    listOf(PerformanceRed, PerformanceRedDark)
                  )
                } else {
                  Brush.verticalGradient(
                    listOf(MechanicalOrange, MetalSurface)
                  )
                }
              )
              .border(
                1.dp,
                if (isSelected) PerformanceRed else MetalBorder,
                RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
              )
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = item.monthShort,
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 9.sp,
              fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
              color = if (isSelected) PureWhite else AluminumGray
            ),
            maxLines = 1
          )
        }
      }
    }
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun RechartsWebViewContainer(
  stats: List<MonthlyRepairStats>,
  metric: ChartMetricType,
  modifier: Modifier = Modifier
) {
  val htmlContent = remember(stats, metric) {
    generateRechartsHtml(stats, metric)
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

private fun generateRechartsHtml(stats: List<MonthlyRepairStats>, metric: ChartMetricType): String {
  val jsonArrayBuilder = StringBuilder("[")
  stats.forEachIndexed { idx, item ->
    val value = when (metric) {
      ChartMetricType.REPAIRS_COUNT -> item.totalRepairs
      ChartMetricType.REVENUE_DZD -> item.revenueDzd
      ChartMetricType.LABOR_HOURS -> item.laborHoursWorked
      ChartMetricType.AVG_TICKET -> item.avgTicketDzd
    }
    jsonArrayBuilder.append("""
      {
        "month": "${item.monthShort}",
        "full": "${item.monthFull}",
        "value": $value,
        "freinage": ${item.freinageRepairs},
        "moteur": ${item.moteurTurboRepairs},
        "distribution": ${item.distributionRepairs},
        "clim": ${item.climatisationRepairs},
        "electricite": ${item.electriciteRepairs},
        "vidange": ${item.vidangeRevisionRepairs},
        "revenue": ${item.revenueDzd},
        "hours": ${item.laborHoursWorked},
        "repairs": ${item.totalRepairs}
      }${if (idx < stats.size - 1) "," else ""}
    """)
  }
  jsonArrayBuilder.append("]")

  val unitLabel = metric.unit

  return """
  <!DOCTYPE html>
  <html>
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
    <style>
      * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }
      body { background-color: #101418; color: #F5F7F9; overflow: hidden; padding: 8px 12px; }
      .chart-container { position: relative; width: 100%; height: 260px; }
      svg { width: 100%; height: 100%; }
      .grid-line { stroke: #252B31; stroke-dasharray: 3 3; }
      .axis-text { fill: #AEB6BF; font-size: 10px; font-weight: 500; }
      .bar-rect { cursor: pointer; transition: all 0.3s ease; }
      .bar-rect:hover { filter: brightness(1.25); }
      .tooltip {
        position: absolute;
        display: none;
        background: #181F26;
        border: 1px solid #E53935;
        border-radius: 8px;
        padding: 6px 10px;
        font-size: 11px;
        color: #fff;
        pointer-events: none;
        box-shadow: 0 4px 14px rgba(0,0,0,0.6);
        z-index: 100;
        white-space: nowrap;
      }
      .tooltip-title { font-weight: bold; color: #FF7A00; margin-bottom: 2px; }
      .tooltip-val { color: #00E5FF; font-family: monospace; font-size: 12px; font-weight: bold; }
      .tooltip-sub { font-size: 9.5px; color: #AEB6BF; margin-top: 2px; }
    </style>
  </head>
  <body>
    <div class="chart-container" id="container">
      <div class="tooltip" id="tooltip"></div>
      <svg id="chartSvg" viewBox="0 0 500 250" preserveAspectRatio="none">
        <defs>
          <linearGradient id="barGrad" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stop-color="#E53935" stop-opacity="0.95" />
            <stop offset="100%" stop-color="#FF7A00" stop-opacity="0.4" />
          </linearGradient>
          <linearGradient id="areaGrad" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stop-color="#00E5FF" stop-opacity="0.35" />
            <stop offset="100%" stop-color="#00E5FF" stop-opacity="0.0" />
          </linearGradient>
          <linearGradient id="lineGrad" x1="0" y1="0" x2="1" y2="0">
            <stop offset="0%" stop-color="#FF7A00" />
            <stop offset="100%" stop-color="#00E5FF" />
          </linearGradient>
        </defs>
        
        <line x1="40" y1="30" x2="480" y2="30" class="grid-line" />
        <line x1="40" y1="80" x2="480" y2="80" class="grid-line" />
        <line x1="40" y1="130" x2="480" y2="130" class="grid-line" />
        <line x1="40" y1="180" x2="480" y2="180" class="grid-line" />
        <line x1="40" y1="210" x2="480" y2="210" stroke="#374151" stroke-width="1.5" />

        <g id="barsGroup"></g>
        <path id="trendArea" fill="url(#areaGrad)" />
        <path id="trendLine" fill="none" stroke="url(#lineGrad)" stroke-width="2.5" />
        <g id="pointsGroup"></g>
        <g id="labelsGroup"></g>
      </svg>
    </div>

    <script>
      const rawData = $jsonArrayBuilder;
      const svg = document.getElementById('chartSvg');
      const barsGroup = document.getElementById('barsGroup');
      const pointsGroup = document.getElementById('pointsGroup');
      const labelsGroup = document.getElementById('labelsGroup');
      const trendLine = document.getElementById('trendLine');
      const trendArea = document.getElementById('trendArea');
      const tooltip = document.getElementById('tooltip');
      const container = document.getElementById('container');

      const maxVal = Math.max(...rawData.map(d => d.value)) * 1.15 || 100;
      const chartW = 440;
      const chartH = 180;
      const startX = 45;
      const startY = 210;
      const stepX = chartW / rawData.length;
      const barW = Math.max(12, stepX * 0.45);

      let linePoints = [];
      let areaPoints = [`${'$'}{startX},${'$'}{startY}`];

      rawData.forEach((d, i) => {
        const x = startX + (i * stepX) + (stepX / 2);
        const barH = (d.value / maxVal) * chartH;
        const y = startY - barH;

        linePoints.push(`${'$'}{x},${'$'}{y}`);
        areaPoints.push(`${'$'}{x},${'$'}{y}`);

        const rect = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
        rect.setAttribute('x', x - (barW / 2));
        rect.setAttribute('y', y);
        rect.setAttribute('width', barW);
        rect.setAttribute('height', barH);
        rect.setAttribute('rx', '4');
        rect.setAttribute('fill', 'url(#barGrad)');
        rect.setAttribute('class', 'bar-rect');
        
        const showTip = (evt) => {
          tooltip.style.display = 'block';
          tooltip.innerHTML = `
            <div class="tooltip-title">${'$'}{d.full}</div>
            <div class="tooltip-val">${'$'}{d.value.toLocaleString()} $unitLabel</div>
            <div class="tooltip-sub">🚗 ${'$'}{d.repairs} réparations • 🔧 ${'$'}{d.hours}h M.O</div>
            <div class="tooltip-sub">💰 CA: ${'$'}{(d.revenue).toLocaleString()} DA</div>
          `;
          const cRect = container.getBoundingClientRect();
          let pageX = evt.clientX ? (evt.clientX - cRect.left) : (x * (cRect.width / 500));
          let pageY = evt.clientY ? (evt.clientY - cRect.top) : (y * (cRect.height / 250));
          tooltip.style.left = Math.min(pageX + 10, cRect.width - 160) + 'px';
          tooltip.style.top = Math.max(10, pageY - 50) + 'px';
        };

        rect.addEventListener('mousemove', showTip);
        rect.addEventListener('touchstart', showTip);
        barsGroup.appendChild(rect);

        const circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        circle.setAttribute('cx', x);
        circle.setAttribute('cy', y);
        circle.setAttribute('r', '3.5');
        circle.setAttribute('fill', '#00E5FF');
        circle.setAttribute('stroke', '#101418');
        circle.setAttribute('stroke-width', '1.5');
        pointsGroup.appendChild(circle);

        const text = document.createElementNS('http://www.w3.org/2000/svg', 'text');
        text.setAttribute('x', x);
        text.setAttribute('y', startY + 16);
        text.setAttribute('text-anchor', 'middle');
        text.setAttribute('class', 'axis-text');
        text.textContent = d.month;
        labelsGroup.appendChild(text);
      });

      areaPoints.push(`${'$'}{startX + (rawData.length * stepX)},${'$'}{startY}`);
      trendLine.setAttribute('d', 'M ' + linePoints.join(' L '));
      trendArea.setAttribute('d', 'M ' + areaPoints.join(' L ') + ' Z');

      document.body.addEventListener('click', (e) => {
        if (!e.target.classList.contains('bar-rect')) {
          tooltip.style.display = 'none';
        }
      });
    </script>
  </body>
  </html>
  """.trimIndent()
}

private fun formatDzd(amount: Long): String {
  val formatter = NumberFormat.getNumberInstance(Locale.FRENCH)
  return "${formatter.format(amount)} DA"
}

private fun formatMetricValue(value: Long, metric: ChartMetricType): String {
  return when (metric) {
    ChartMetricType.REVENUE_DZD, ChartMetricType.AVG_TICKET -> formatDzd(value)
    ChartMetricType.LABOR_HOURS -> "$value h"
    ChartMetricType.REPAIRS_COUNT -> "$value répas"
  }
}

private fun formatMetricShort(value: Long, metric: ChartMetricType): String {
  return when (metric) {
    ChartMetricType.REVENUE_DZD, ChartMetricType.AVG_TICKET -> "${value / 1000}k"
    ChartMetricType.LABOR_HOURS -> "${value}h"
    ChartMetricType.REPAIRS_COUNT -> "$value"
  }
}
