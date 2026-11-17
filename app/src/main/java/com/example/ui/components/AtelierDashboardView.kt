package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.screens.WorkshopTab
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

enum class DashboardSectionFilter(val label: String, val icon: ImageVector) {
  TOUT("Vue Complète", Icons.Default.Dashboard),
  PRODUCTIVITE_RECHARTS_D3("Productivité Recharts/D3", Icons.Default.Insights),
  ORDRES_ACTIFS("Ordres Actifs", Icons.Default.ReceiptLong),
  OCCUPATION_MECANICIENS("Occupation Baies", Icons.Default.Engineering),
  MAINTENANCE_JOUR("Maintenance Jour", Icons.Default.BuildCircle)
}

@Composable
fun AtelierDashboardView(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  onOpenOrderChecklist: (RepairOrder) -> Unit,
  onOpenOrderQuote: (RepairOrder) -> Unit,
  onNavigateToTab: (WorkshopTab) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var activeSectionFilter by remember { mutableStateOf(DashboardSectionFilter.TOUT) }
  var orderStatusFilter by remember { mutableStateOf<RepairStatus?>(null) }
  var maintenanceStatusFilter by remember { mutableStateOf<MaintenanceTaskStatus?>(null) }

  // Dialog states
  var showAddTaskDialog by remember { mutableStateOf(false) }
  var orderToChangeStatus by remember { mutableStateOf<RepairOrder?>(null) }
  var taskToReassign by remember { mutableStateOf<DailyMaintenanceTask?>(null) }
  var bayToAssignVehicle by remember { mutableStateOf<WorkshopBayItem?>(null) }

  // Derived metrics
  val activeOrders = remember(uiState.repairOrders) {
    uiState.repairOrders.filter { it.status != RepairStatus.FACTURE }
  }
  val inProgressOrdersCount = activeOrders.count { it.status == RepairStatus.EN_COURS }
  val waitingPartsOrdersCount = activeOrders.count { it.status == RepairStatus.PIECES_COMMANDEES }
  val diagnosticOrdersCount = activeOrders.count { it.status == RepairStatus.DIAGNOSTIC }
  val completedOrdersCount = activeOrders.count { it.status == RepairStatus.TERMINE }
  val activeRevenueTotal = activeOrders.sumOf { it.netTotalDzd }

  val bays = uiState.workshopBays
  val occupiedBaysCount = bays.count { it.status != WorkshopBayStatus.LIBRE }
  val occupancyPercent = if (bays.isNotEmpty()) (occupiedBaysCount * 100) / bays.size else 0
  val activeTimersCount = bays.count { it.isTimerRunning }

  val maintenanceTasks = uiState.dailyMaintenanceTasks
  val pendingTasksCount = maintenanceTasks.count { it.status == MaintenanceTaskStatus.EN_ATTENTE }
  val inProgressTasksCount = maintenanceTasks.count { it.status == MaintenanceTaskStatus.EN_COURS }
  val doneTasksCount = maintenanceTasks.count { it.status == MaintenanceTaskStatus.TERMINE }
  val urgentTasksCount = maintenanceTasks.count { it.priority == MaintenanceTaskPriority.URGENT && it.status != MaintenanceTaskStatus.TERMINE }

  val weeklyProductivityPct = remember(uiState.companionWorkTimeAllocations) {
    val sold = uiState.companionWorkTimeAllocations.sumOf { it.soldBaremeHours.toDouble() }.toFloat()
    val clocked = uiState.companionWorkTimeAllocations.sumOf { it.totalClockedHours.toDouble() }.toFloat().coerceAtLeast(1f)
    ((sold / clocked) * 100f).toInt()
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("atelier_dashboard_view"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // -------------------------------------------------------------------------
    // 1. EXECUTIVE LIVE STATUS BAR
    // -------------------------------------------------------------------------
    Surface(
      shape = RoundedCornerShape(14.dp),
      color = adaptiveBannerSurface(0xFF141C24, 0xFFF1F5F9),
      border = BorderStroke(1.2.dp, PerformanceRedGlow),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("banner_atelier_executive_live_status")
    ) {
      Row(
        modifier = Modifier.padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(PerformanceRed.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Dashboard, contentDescription = null, tint = PerformanceRed, modifier = Modifier.size(22.dp))
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(ElectricGreen)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "TABLEAU DE BORD EN DIRECT",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = ElectricGreen,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp
                )
              )
            }
            Text(
              text = "Supervision centrale de l'activité atelier • Alger",
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.5.sp
              )
            )
          }
        }

        // Live clock / shifts pill
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = adaptiveBannerSurface(0xFF1A232F, 0xFFE2E8F0),
          border = BorderStroke(0.5.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.AccessTime, contentDescription = null, tint = TechCyan, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "08:00 - 18:30",
              style = MaterialTheme.typography.labelSmall.copy(
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            )
          }
        }
      }
    }

    // -------------------------------------------------------------------------
    // 1.5. MODULAR 2x2 QUICK-ACCESS GRID DASHBOARD (#1C2833 Cards & #F07836 Actions)
    // -------------------------------------------------------------------------
    ModularQuickAccessGridDashboard(
      activeVehicle = uiState.activeVehicle,
      currentVinQuery = uiState.searchQuery,
      dailyVinCount = uiState.dailyVinCount,
      dailyVinLimit = uiState.dailyVinLimit,
      repairOrders = uiState.repairOrders,
      dailyMaintenanceTasks = uiState.dailyMaintenanceTasks,
      invoicesList = uiState.invoicesList,
      onVinSearchAction = { viewModel.setNavigationTab(com.example.ui.viewmodel.MainNavigationTab.RECHERCHE) },
      onScanVinOcrAction = { viewModel.openCarteGriseScanner() },
      onPendingRepairsAction = { activeSectionFilter = DashboardSectionFilter.ORDRES_ACTIFS },
      onActiveQuotesAction = { onNavigateToTab(WorkshopTab.ORDRES_DEVIS) },
      onCreateNewQuoteAction = { viewModel.toggleNewQuoteDialog(true) },
      onDailySalesKpiAction = { onNavigateToTab(WorkshopTab.STATISTIQUES) },
      modifier = Modifier.padding(horizontal = 0.dp)
    )

    // -------------------------------------------------------------------------
    // 2. 3 INTERACTIVE KPI SUMMARY CARDS
    // -------------------------------------------------------------------------
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // KPI 1: Active Repair Orders
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = adaptiveBannerSurface(0xFF1B1816, 0xFFFFF7ED),
        border = BorderStroke(1.2.dp, MechanicalOrange),
        modifier = Modifier
          .weight(1f)
          .clickable { activeSectionFilter = DashboardSectionFilter.ORDRES_ACTIFS }
          .testTag("kpi_card_active_orders")
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(18.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = MechanicalOrange.copy(alpha = 0.2f)
            ) {
              Text(
                text = "${activeOrders.size} OR",
                color = MechanicalOrange,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "${activeOrders.size}",
            style = MaterialTheme.typography.headlineMedium.copy(
              color = PureWhite,
              fontWeight = FontWeight.Black,
              fontSize = 22.sp
            )
          )
          Text(
            text = "Ordres Actifs",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MechanicalOrange,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${activeRevenueTotal} DA chiffrés",
            style = MaterialTheme.typography.bodySmall.copy(
              color = AluminumLight,
              fontSize = 9.5.sp
            )
          )
        }
      }

      // KPI 2: Mechanic Occupancy
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = adaptiveBannerSurface(0xFF132228, 0xFFF0F9FF),
        border = BorderStroke(1.2.dp, TechCyan),
        modifier = Modifier
          .weight(1f)
          .clickable { activeSectionFilter = DashboardSectionFilter.OCCUPATION_MECANICIENS }
          .testTag("kpi_card_mechanic_occupancy")
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Engineering, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = TechCyan.copy(alpha = 0.2f)
            ) {
              Text(
                text = "$activeTimersCount ⏱️",
                color = TechCyan,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "$occupancyPercent%",
            style = MaterialTheme.typography.headlineMedium.copy(
              color = PureWhite,
              fontWeight = FontWeight.Black,
              fontSize = 22.sp
            )
          )
          Text(
            text = "Occupation Baies",
            style = MaterialTheme.typography.labelSmall.copy(
              color = TechCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "$occupiedBaysCount/${bays.size} ponts occupés",
            style = MaterialTheme.typography.bodySmall.copy(
              color = AluminumLight,
              fontSize = 9.5.sp
            )
          )
        }
      }

      // KPI 3: Daily Maintenance Tasks
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = adaptiveBannerSurface(0xFF12231B, 0xFFF0FDF4),
        border = BorderStroke(1.2.dp, ElectricGreen),
        modifier = Modifier
          .weight(1f)
          .clickable { activeSectionFilter = DashboardSectionFilter.MAINTENANCE_JOUR }
          .testTag("kpi_card_daily_maintenance")
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.BuildCircle, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(18.dp))
            if (urgentTasksCount > 0) {
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = PerformanceRed.copy(alpha = 0.2f)
              ) {
                Text(
                  text = "! $urgentTasksCount urg",
                  color = PerformanceRedGlow,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
              }
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "$pendingTasksCount",
            style = MaterialTheme.typography.headlineMedium.copy(
              color = PureWhite,
              fontWeight = FontWeight.Black,
              fontSize = 22.sp
            )
          )
          Text(
            text = "Maintenance Jour",
            style = MaterialTheme.typography.labelSmall.copy(
              color = ElectricGreen,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "$doneTasksCount terminées",
            style = MaterialTheme.typography.bodySmall.copy(
              color = AluminumLight,
              fontSize = 9.5.sp
            )
          )
        }
      }
    }

    // -------------------------------------------------------------------------
    // 3. SECTION SWITCHER PILLS (TOUT, PRODUCTIVITÉ D3, ORDRES, OCCUPATION, MAINTENANCE)
    // -------------------------------------------------------------------------
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("dashboard_section_filters_row")
    ) {
      items(DashboardSectionFilter.entries) { filter ->
        val isSelected = filter == activeSectionFilter
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = if (isSelected) PerformanceRed else adaptiveBannerSurface(0xFF1E2631, 0xFFE2E8F0),
          border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
          onClick = { activeSectionFilter = filter },
          modifier = Modifier.testTag("filter_tab_${filter.name.lowercase()}")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              filter.icon,
              contentDescription = null,
              tint = if (isSelected) PureWhite else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = when (filter) {
                DashboardSectionFilter.TOUT -> "Vue Complète"
                DashboardSectionFilter.PRODUCTIVITE_RECHARTS_D3 -> "Recharts/D3 ($weeklyProductivityPct%)"
                DashboardSectionFilter.ORDRES_ACTIFS -> "Ordres Actifs (${activeOrders.size})"
                DashboardSectionFilter.OCCUPATION_MECANICIENS -> "Occupation Baies ($occupiedBaysCount/${bays.size})"
                DashboardSectionFilter.MAINTENANCE_JOUR -> "Maintenance ($pendingTasksCount)"
              },
              style = MaterialTheme.typography.labelSmall.copy(
                color = PureWhite,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.5.sp
              )
            )
          }
        }
      }
    }

    // -------------------------------------------------------------------------
    // 3.B RECHARTS / D3.JS DATA VISUALIZATION WIDGET:
    //     COMPANION WORK TIME DISTRIBUTION & WEEKLY PRODUCTIVITY RATE
    // -------------------------------------------------------------------------
    if (
      activeSectionFilter == DashboardSectionFilter.TOUT ||
      activeSectionFilter == DashboardSectionFilter.PRODUCTIVITE_RECHARTS_D3 ||
      activeSectionFilter == DashboardSectionFilter.OCCUPATION_MECANICIENS
    ) {
      AtelierProductivityD3Widget(
        companionAllocations = uiState.companionWorkTimeAllocations,
        weeklyPoints = uiState.weeklyProductivityPoints,
        onAdjustCompanionHours = { companionId, prodDelta, diagDelta, waitDelta, soldDelta ->
          viewModel.adjustCompanionWorkHours(
            companionId = companionId,
            productiveDeltaHours = prodDelta,
            diagnosticDeltaHours = diagDelta,
            waitingDeltaHours = waitDelta,
            soldBaremeDeltaHours = soldDelta
          )
        },
        onOpenFullAnalytics = { onNavigateToTab(WorkshopTab.STATISTIQUES) }
      )
    }

    // -------------------------------------------------------------------------
    // 4. SECTION 1: ACTIVE REPAIR ORDERS (ORDRES DE RÉPARATION ACTIFS)
    // -------------------------------------------------------------------------
    if (activeSectionFilter == DashboardSectionFilter.TOUT || activeSectionFilter == DashboardSectionFilter.ORDRES_ACTIFS) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("section_active_repair_orders"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          // Section Title & Quick Create
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "ORDRES DE RÉPARATION ACTIFS",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    fontSize = 14.sp
                  )
                )
                Text(
                  text = "${activeOrders.size} véhicules en atelier actuellement",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 10.5.sp)
                )
              }
            }

            TextButton(
              onClick = { viewModel.toggleNewQuoteDialog(true) },
              modifier = Modifier.testTag("btn_dashboard_new_repair_order")
            ) {
              Icon(Icons.Default.Add, contentDescription = null, tint = PerformanceRed, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Nouveau Devis / OR",
                color = PerformanceRed,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp
              )
            }
          }

          // Sub-filters for Repair Status
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            item {
              FilterChip(
                selected = orderStatusFilter == null,
                onClick = { orderStatusFilter = null },
                label = { Text("Tous (${activeOrders.size})", fontSize = 10.5.sp) }
              )
            }
            items(listOf(RepairStatus.EN_COURS, RepairStatus.DIAGNOSTIC, RepairStatus.PIECES_COMMANDEES, RepairStatus.TERMINE)) { status ->
              val count = activeOrders.count { it.status == status }
              FilterChip(
                selected = orderStatusFilter == status,
                onClick = { orderStatusFilter = if (orderStatusFilter == status) null else status },
                label = { Text("${status.label} ($count)", fontSize = 10.5.sp) }
              )
            }
          }

          // Active Orders List
          val filteredOrders = if (orderStatusFilter == null) activeOrders else activeOrders.filter { it.status == orderStatusFilter }

          if (filteredOrders.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "Aucun ordre de réparation actif dans cette catégorie",
                color = AluminumGray,
                style = MaterialTheme.typography.bodySmall
              )
            }
          } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              filteredOrders.forEach { order ->
                ActiveRepairOrderCard(
                  order = order,
                  onOpenChecklist = { onOpenOrderChecklist(order) },
                  onOpenQuote = { onOpenOrderQuote(order) },
                  onChangeStatus = { orderToChangeStatus = order },
                  onDownloadPdf = {
                    viewModel.generateAndDownloadRepairOrderPdf(
                      context = context,
                      order = order
                    )
                  }
                )
              }
            }
          }

          // View all OR link
          TextButton(
            onClick = { onNavigateToTab(WorkshopTab.ORDRES_DEVIS) },
            modifier = Modifier.align(Alignment.End)
          ) {
            Text(
              text = "Accéder à l'historique complet des Ordres & Devis →",
              color = TechCyan,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // -------------------------------------------------------------------------
    // 5. SECTION 2: CURRENT MECHANIC OCCUPANCY & BAYS
    // -------------------------------------------------------------------------
    if (activeSectionFilter == DashboardSectionFilter.TOUT || activeSectionFilter == DashboardSectionFilter.OCCUPATION_MECANICIENS) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("section_mechanic_occupancy"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          // Header with Planning Modal shortcut
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Engineering, contentDescription = null, tint = TechCyan, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "OCCUPATION DES MÉCANICIENS & BAIES",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    fontSize = 14.sp
                  )
                )
                Text(
                  text = "$occupiedBaysCount/${bays.size} techniciens en poste sur véhicules",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 10.5.sp)
                )
              }
            }

            TextButton(
              onClick = { viewModel.openBayPlannerModal() },
              modifier = Modifier.testTag("btn_dashboard_open_bay_planner")
            ) {
              Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Planning Global",
                color = TechCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp
              )
            }
          }

          // Global Occupancy Bar
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = adaptiveBannerSurface(0xFF141C24, 0xFFF1F5F9),
            border = BorderStroke(1.dp, MetalBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Taux de Charge Atelier",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = AluminumLight,
                    fontWeight = FontWeight.Bold
                  )
                )
                Text(
                  text = "$occupancyPercent% ($occupiedBaysCount/${bays.size} Baies)",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (occupancyPercent >= 75) ElectricGreen else MechanicalOrange,
                    fontWeight = FontWeight.Black
                  )
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              LinearProgressIndicator(
                progress = { occupancyPercent / 100f },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(8.dp)
                  .clip(RoundedCornerShape(4.dp)),
                color = if (occupancyPercent >= 75) ElectricGreen else MechanicalOrange,
                trackColor = GraphiteBlack
              )
            }
          }

          // Bays & Mechanics Grid / Cards
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            bays.forEach { bay ->
              MechanicBayOccupancyCard(
                bay = bay,
                onToggleTimer = { viewModel.toggleBayTimer(bay.id) },
                onCompleteOperation = { viewModel.completeBayOperation(bay.id) },
                onAssignVehicle = { bayToAssignVehicle = bay }
              )
            }
          }
        }
      }
    }

    // -------------------------------------------------------------------------
    // 6. SECTION 3: PENDING MAINTENANCE TASKS FOR THE DAY
    // -------------------------------------------------------------------------
    if (activeSectionFilter == DashboardSectionFilter.TOUT || activeSectionFilter == DashboardSectionFilter.MAINTENANCE_JOUR) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("section_daily_maintenance_tasks"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          // Header with Add Task button
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.BuildCircle, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "MAINTENANCE EN ATTENTE DU JOUR",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    fontSize = 14.sp
                  )
                )
                Text(
                  text = "$pendingTasksCount en attente • $inProgressTasksCount en cours • $doneTasksCount terminées",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 10.5.sp)
                )
              }
            }

            IconButton(
              onClick = { showAddTaskDialog = true },
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ElectricGreen.copy(alpha = 0.2f))
                .testTag("btn_dashboard_add_maintenance_task")
            ) {
              Icon(Icons.Default.Add, contentDescription = "Ajouter tâche", tint = ElectricGreen, modifier = Modifier.size(20.dp))
            }
          }

          // Sub-filters
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            item {
              FilterChip(
                selected = maintenanceStatusFilter == null,
                onClick = { maintenanceStatusFilter = null },
                label = { Text("Toutes (${maintenanceTasks.size})", fontSize = 10.5.sp) }
              )
            }
            items(MaintenanceTaskStatus.entries) { status ->
              val count = maintenanceTasks.count { it.status == status }
              FilterChip(
                selected = maintenanceStatusFilter == status,
                onClick = { maintenanceStatusFilter = if (maintenanceStatusFilter == status) null else status },
                label = { Text("${status.label} ($count)", fontSize = 10.5.sp) }
              )
            }
          }

          // Tasks List
          val filteredTasks = if (maintenanceStatusFilter == null) {
            maintenanceTasks
          } else {
            maintenanceTasks.filter { it.status == maintenanceStatusFilter }
          }

          if (filteredTasks.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "Aucune tâche de maintenance correspondante",
                color = AluminumGray,
                style = MaterialTheme.typography.bodySmall
              )
            }
          } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              filteredTasks.forEach { task ->
                DailyMaintenanceTaskCard(
                  task = task,
                  onStartTask = {
                    viewModel.updateMaintenanceTaskStatus(task.id, MaintenanceTaskStatus.EN_COURS)
                  },
                  onCompleteTask = {
                    viewModel.updateMaintenanceTaskStatus(task.id, MaintenanceTaskStatus.TERMINE)
                  },
                  onConvertToRepairOrder = {
                    viewModel.convertMaintenanceTaskToRepairOrder(task)
                  },
                  onReassign = { taskToReassign = task }
                )
              }
            }
          }
        }
      }
    }
  }

  // ---------------------------------------------------------------------------
  // DIALOGS & MODALS
  // ---------------------------------------------------------------------------

  // 1. Add Daily Maintenance Task Dialog
  if (showAddTaskDialog) {
    AddDailyMaintenanceTaskDialog(
      onDismiss = { showAddTaskDialog = false },
      onConfirm = { newTask ->
        viewModel.addNewMaintenanceTask(newTask)
        showAddTaskDialog = false
      }
    )
  }

  // 2. Change Repair Order Status Dialog
  orderToChangeStatus?.let { order ->
    ChangeRepairOrderStatusDialog(
      order = order,
      onDismiss = { orderToChangeStatus = null },
      onSelectStatus = { newStatus ->
        viewModel.updateRepairOrderStatus(order.id, newStatus)
        orderToChangeStatus = null
      }
    )
  }

  // 3. Reassign Maintenance Task Dialog
  taskToReassign?.let { task ->
    ReassignMaintenanceTaskDialog(
      task = task,
      bays = uiState.workshopBays,
      onDismiss = { taskToReassign = null },
      onConfirm = { mechanicName, bayNumber ->
        viewModel.assignMaintenanceTaskMechanic(task.id, mechanicName, bayNumber)
        taskToReassign = null
      }
    )
  }

  // 4. Assign Vehicle to Free Bay Dialog
  bayToAssignVehicle?.let { bay ->
    AssignVehicleToBayDialog(
      bay = bay,
      waitingVehicles = uiState.waitingQueueVehicles,
      onDismiss = { bayToAssignVehicle = null },
      onSelectVehicle = { waitingVehicleId ->
        viewModel.assignVehicleToBay(bay.id, waitingVehicleId)
        bayToAssignVehicle = null
      }
    )
  }
}

// =============================================================================
// SUB-COMPONENTS
// =============================================================================

@Composable
private fun ActiveRepairOrderCard(
  order: RepairOrder,
  onOpenChecklist: () -> Unit,
  onOpenQuote: () -> Unit,
  onChangeStatus: () -> Unit,
  onDownloadPdf: () -> Unit
) {
  val context = LocalContext.current
  val statusColor = Color(order.status.colorHex)

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = adaptiveBannerSurface(0xFF161F28, 0xFFFFFFFF),
    border = BorderStroke(1.dp, MetalBorder),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("active_order_card_${order.id}")
  ) {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      // Header: Order Number, Plate, Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = order.orderNumber,
            style = MaterialTheme.typography.titleSmall.copy(
              color = PureWhite,
              fontWeight = FontWeight.Black,
              fontFamily = FontFamily.Monospace,
              fontSize = 13.sp
            )
          )
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = adaptiveBannerSurface(0xFF1E2836, 0xFFF1F5F9),
            border = BorderStroke(0.5.dp, MetalBorder)
          ) {
            Text(
              text = order.plateNumber,
              style = MaterialTheme.typography.labelSmall.copy(
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp
              ),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        // Status pill (clickable to change)
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = statusColor.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, statusColor.copy(alpha = 0.8f)),
          onClick = onChangeStatus,
          modifier = Modifier.testTag("btn_change_order_status_${order.id}")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(statusColor))
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = order.status.label,
              style = MaterialTheme.typography.labelSmall.copy(
                color = statusColor,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp
              )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = statusColor, modifier = Modifier.size(14.dp))
          }
        }
      }

      // Vehicle & Client Info
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = order.vehicleModel,
            style = MaterialTheme.typography.bodyMedium.copy(
              color = PureWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 12.5.sp
            )
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Client : ${order.clientName}",
              style = MaterialTheme.typography.bodySmall.copy(
                color = AluminumLight,
                fontSize = 11.sp
              )
            )
            if (order.clientPhone.isNotBlank()) {
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "• ${order.clientPhone}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = TechCyan,
                  fontSize = 10.5.sp,
                  fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.clickable {
                  val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.clientPhone}"))
                  context.startActivity(intent)
                }
              )
            }
          }
        }

        // Total Net
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "${order.netTotalDzd} DA",
            style = MaterialTheme.typography.titleMedium.copy(
              color = MechanicalOrange,
              fontWeight = FontWeight.Black,
              fontSize = 14.sp
            )
          )
          Text(
            text = "${order.laborHours}h M.O • ${order.items.size} pièces",
            style = MaterialTheme.typography.labelSmall.copy(
              color = AluminumGray,
              fontSize = 9.5.sp
            )
          )
        }
      }

      // Checklist & Signature Indicator row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          if (order.isSigned) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = ElectricGreen.copy(alpha = 0.15f)
            ) {
              Text(
                text = "✍️ Signé (${order.signatureName})",
                color = ElectricGreen,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }
          } else {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = MechanicalOrange.copy(alpha = 0.15f)
            ) {
              Text(
                text = "⏳ Signature en attente",
                color = MechanicalOrange,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }
          }

          val burntBulbsCount = order.checklist.burntNotReplacedBulbs.size
          if (burntBulbsCount > 0) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = PerformanceRed.copy(alpha = 0.15f)
            ) {
              Text(
                text = "💡 $burntBulbsCount ampoule(s) grillée(s)",
                color = PerformanceRedGlow,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }
          }
        }

        // Action Buttons Row
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          IconButton(
            onClick = onOpenChecklist,
            modifier = Modifier.size(32.dp).testTag("btn_checklist_order_${order.id}")
          ) {
            Icon(Icons.Default.FactCheck, contentDescription = "Checklist & Ampoules", tint = PureWhite, modifier = Modifier.size(16.dp))
          }
          IconButton(
            onClick = onOpenQuote,
            modifier = Modifier.size(32.dp).testTag("btn_quote_order_${order.id}")
          ) {
            Icon(Icons.Default.Receipt, contentDescription = "Devis & Facture", tint = MechanicalOrange, modifier = Modifier.size(16.dp))
          }
          IconButton(
            onClick = onDownloadPdf,
            modifier = Modifier.size(32.dp).testTag("btn_pdf_order_${order.id}")
          ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = "Télécharger PDF", tint = TechCyan, modifier = Modifier.size(16.dp))
          }
        }
      }
    }
  }
}

@Composable
private fun MechanicBayOccupancyCard(
  bay: WorkshopBayItem,
  onToggleTimer: () -> Unit,
  onCompleteOperation: () -> Unit,
  onAssignVehicle: () -> Unit
) {
  val isOccupied = bay.status != WorkshopBayStatus.LIBRE
  val statusColor = Color(bay.status.badgeColorHex)

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = adaptiveBannerSurface(0xFF161F28, 0xFFFFFFFF),
    border = BorderStroke(1.dp, if (isOccupied) statusColor.copy(alpha = 0.6f) else MetalBorder),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("mechanic_bay_card_${bay.id}")
  ) {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      // Top row: Bay info & Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(if (isOccupied) statusColor.copy(alpha = 0.2f) else GraphiteBlack),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.Engineering,
              contentDescription = null,
              tint = if (isOccupied) statusColor else AluminumGray,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = bay.bayName,
              style = MaterialTheme.typography.titleSmall.copy(
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp
              )
            )
            Text(
              text = "${bay.assignedMechanic} • ${bay.mechanicSpecialty}",
              style = MaterialTheme.typography.bodySmall.copy(
                color = AluminumLight,
                fontSize = 10.5.sp
              )
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = statusColor.copy(alpha = 0.15f),
          border = BorderStroke(0.8.dp, statusColor.copy(alpha = 0.7f))
        ) {
          Text(
            text = bay.status.label,
            style = MaterialTheme.typography.labelSmall.copy(
              color = statusColor,
              fontWeight = FontWeight.Bold,
              fontSize = 9.5.sp
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }
      }

      // Middle: If occupied, vehicle and task info
      if (isOccupied && bay.currentVehicleModel != null) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = adaptiveBannerSurface(0xFF131A22, 0xFFF8FAFC),
          border = BorderStroke(0.5.dp, MetalBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = bay.currentVehicleModel ?: "",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                  )
                )
                Text(
                  text = "Matricule : ${bay.currentLicensePlate ?: "N/A"}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = TechCyan,
                    fontSize = 10.sp
                  )
                )
              }

              bay.repairOrderNumber?.let { orNum ->
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = MechanicalOrange.copy(alpha = 0.2f)
                ) {
                  Text(
                    text = orNum,
                    color = MechanicalOrange,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                  )
                }
              }
            }

            Text(
              text = bay.operationTitle ?: "",
              style = MaterialTheme.typography.bodySmall.copy(
                color = AluminumLight,
                fontSize = 11.sp
              )
            )

            // Timer & Progress
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  if (bay.isTimerRunning) Icons.Default.Timer else Icons.Default.TimerOff,
                  contentDescription = null,
                  tint = if (bay.isTimerRunning) ElectricGreen else AluminumGray,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "${bay.elapsedMinutes} min / ${bay.estimatedMinutes} min",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (bay.isTimerRunning) ElectricGreen else AluminumLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                  )
                )
              }

              Text(
                text = "${(bay.progressRatio * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = statusColor,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.5.sp
                )
              )
            }

            LinearProgressIndicator(
              progress = { bay.progressRatio },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = statusColor,
              trackColor = GraphiteBlack
            )
          }
        }

        // Actions Row for occupied bay
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Play / Pause timer
          TextButton(
            onClick = onToggleTimer,
            modifier = Modifier.testTag("btn_toggle_timer_${bay.id}")
          ) {
            Icon(
              if (bay.isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = null,
              tint = if (bay.isTimerRunning) MechanicalOrange else ElectricGreen,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (bay.isTimerRunning) "Pause Chrono" else "Reprendre Chrono",
              color = if (bay.isTimerRunning) MechanicalOrange else ElectricGreen,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Mark complete & free bay
          FilledTonalButton(
            onClick = onCompleteOperation,
            colors = ButtonDefaults.filledTonalButtonColors(
              containerColor = ElectricGreen.copy(alpha = 0.2f),
              contentColor = ElectricGreen
            ),
            modifier = Modifier.testTag("btn_complete_bay_${bay.id}")
          ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Terminer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      } else {
        // Free Bay State
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "🟢 Pont disponible • Prêt pour nouvelle prise en charge",
            style = MaterialTheme.typography.bodySmall.copy(
              color = ElectricGreen,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          )

          Button(
            onClick = onAssignVehicle,
            colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            modifier = Modifier.testTag("btn_assign_bay_${bay.id}")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Affecter Véhicule", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun DailyMaintenanceTaskCard(
  task: DailyMaintenanceTask,
  onStartTask: () -> Unit,
  onCompleteTask: () -> Unit,
  onConvertToRepairOrder: () -> Unit,
  onReassign: () -> Unit
) {
  val priorityColor = Color(task.priority.colorHex)
  val statusColor = Color(task.status.colorHex)

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = adaptiveBannerSurface(0xFF161F28, 0xFFFFFFFF),
    border = BorderStroke(1.dp, MetalBorder),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("maintenance_task_card_${task.id}")
  ) {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      // Top row: Time badge, Priority, Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = TechCyan.copy(alpha = 0.15f),
            border = BorderStroke(0.8.dp, TechCyan.copy(alpha = 0.6f))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.AccessTime, contentDescription = null, tint = TechCyan, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = task.scheduledTime,
                color = TechCyan,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
              )
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          Surface(
            shape = RoundedCornerShape(4.dp),
            color = priorityColor.copy(alpha = 0.15f)
          ) {
            Text(
              text = task.priority.label,
              color = priorityColor,
              fontWeight = FontWeight.Bold,
              fontSize = 9.5.sp,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = statusColor.copy(alpha = 0.15f),
          border = BorderStroke(0.8.dp, statusColor.copy(alpha = 0.6f))
        ) {
          Text(
            text = task.status.label,
            color = statusColor,
            fontWeight = FontWeight.Bold,
            fontSize = 9.5.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      // Title & Category
      Text(
        text = task.taskTitle,
        style = MaterialTheme.typography.titleSmall.copy(
          color = PureWhite,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
      )

      // Vehicle & Client info
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "${task.vehicleModel} (${task.licensePlate})",
            style = MaterialTheme.typography.bodySmall.copy(
              color = TechCyan,
              fontWeight = FontWeight.Medium,
              fontSize = 11.sp
            )
          )
          Text(
            text = "Client : ${task.clientName} • ${task.clientPhone}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = AluminumLight,
              fontSize = 10.sp
            )
          )
        }

        // Assigned Mechanic & Bay
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = adaptiveBannerSurface(0xFF131D27, 0xFFEFF6FF),
          border = BorderStroke(0.5.dp, MetalBorder)
        ) {
          Text(
            text = "${task.assignedMechanic} • Pont ${task.bayNumber}",
            style = MaterialTheme.typography.labelSmall.copy(
              color = PureWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }
      }

      // Required Parts summary & availability
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Icon(
            if (task.partsAvailable) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
            contentDescription = null,
            tint = if (task.partsAvailable) ElectricGreen else MechanicalOrange,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (task.partsAvailable) "Pièces en stock : ${task.requiredPartsSummary}" else "Pièces en commande : ${task.requiredPartsSummary}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = if (task.partsAvailable) ElectricGreen else MechanicalOrange,
              fontSize = 9.5.sp
            ),
            maxLines = 1
          )
        }

        Text(
          text = "${task.estimatedMinutes} min estimées",
          style = MaterialTheme.typography.labelSmall.copy(
            color = AluminumGray,
            fontSize = 9.5.sp
          )
        )
      }

      // Action Buttons Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        TextButton(
          onClick = onReassign,
          modifier = Modifier.testTag("btn_reassign_task_${task.id}")
        ) {
          Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = AluminumLight, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(3.dp))
          Text("Réassigner", color = AluminumLight, fontSize = 10.5.sp)
        }

        Spacer(modifier = Modifier.width(4.dp))

        TextButton(
          onClick = onConvertToRepairOrder,
          modifier = Modifier.testTag("btn_convert_to_or_${task.id}")
        ) {
          Icon(Icons.Default.PostAdd, contentDescription = null, tint = TechCyan, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(3.dp))
          Text("Créer OR", color = TechCyan, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(4.dp))

        if (task.status == MaintenanceTaskStatus.EN_ATTENTE) {
          Button(
            onClick = onStartTask,
            colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.testTag("btn_start_task_${task.id}")
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("Démarrer", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
          }
        } else if (task.status == MaintenanceTaskStatus.EN_COURS) {
          Button(
            onClick = onCompleteTask,
            colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.testTag("btn_complete_task_${task.id}")
          ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("Valider", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

// =============================================================================
// MODALS
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDailyMaintenanceTaskDialog(
  onDismiss: () -> Unit,
  onConfirm: (DailyMaintenanceTask) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Vidange & Filtres") }
  var vehicleModel by remember { mutableStateOf("") }
  var licensePlate by remember { mutableStateOf("") }
  var clientName by remember { mutableStateOf("") }
  var clientPhone by remember { mutableStateOf("") }
  var scheduledTime by remember { mutableStateOf("10:00") }
  var estimatedMinutes by remember { mutableStateOf("60") }
  var priority by remember { mutableStateOf(MaintenanceTaskPriority.NORMAL) }
  var assignedMechanic by remember { mutableStateOf("Karim B.") }
  var bayNumber by remember { mutableStateOf(1) }
  var partsSummary by remember { mutableStateOf("Filtres & Huile moteur") }
  var notes by remember { mutableStateOf("") }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .padding(16.dp)
        .testTag("dialog_add_maintenance_task"),
      shape = RoundedCornerShape(18.dp),
      color = MetalCard,
      border = BorderStroke(1.dp, PerformanceRedGlow)
    ) {
      Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.BuildCircle, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "NOUVELLE TÂCHE MAINTENANCE",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite,
                fontSize = 14.sp
              )
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumGray)
          }
        }

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Titre de l'intervention (ex: Vidange 10 000 km)") },
          modifier = Modifier.fillMaxWidth().testTag("input_task_title"),
          singleLine = true
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = vehicleModel,
            onValueChange = { vehicleModel = it },
            label = { Text("Véhicule (ex: Golf 7)") },
            modifier = Modifier.weight(1.2f).testTag("input_task_vehicle"),
            singleLine = true
          )
          OutlinedTextField(
            value = licensePlate,
            onValueChange = { licensePlate = it },
            label = { Text("Matricule") },
            modifier = Modifier.weight(1f).testTag("input_task_plate"),
            singleLine = true
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = clientName,
            onValueChange = { clientName = it },
            label = { Text("Client") },
            modifier = Modifier.weight(1.2f),
            singleLine = true
          )
          OutlinedTextField(
            value = scheduledTime,
            onValueChange = { scheduledTime = it },
            label = { Text("Heure (HH:MM)") },
            modifier = Modifier.weight(0.8f),
            singleLine = true
          )
        }

        OutlinedTextField(
          value = partsSummary,
          onValueChange = { partsSummary = it },
          label = { Text("Pièces requises (ex: Filtre Purflux + Huile 5W30)") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        // Priority Selector
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          MaintenanceTaskPriority.entries.forEach { p ->
            val isSelected = p == priority
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) Color(p.colorHex) else GraphiteBlack,
              border = BorderStroke(1.dp, Color(p.colorHex)),
              onClick = { priority = p },
              modifier = Modifier.weight(1f)
            ) {
              Text(
                text = p.label.take(8),
                color = if (isSelected) PureWhite else Color(p.colorHex),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = onDismiss) {
            Text("Annuler", color = AluminumGray)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (title.isNotBlank()) {
                val newTask = DailyMaintenanceTask(
                  id = "TASK-${System.currentTimeMillis().toString().takeLast(4)}",
                  taskTitle = title,
                  category = category,
                  vehicleModel = vehicleModel.ifBlank { "Véhicule Atelier" },
                  licensePlate = licensePlate.ifBlank { "00000-000-16" },
                  clientName = clientName.ifBlank { "Client Passage" },
                  clientPhone = clientPhone.ifBlank { "0550 00 00 00" },
                  scheduledTime = scheduledTime.ifBlank { "11:00" },
                  estimatedMinutes = estimatedMinutes.toIntOrNull() ?: 60,
                  priority = priority,
                  status = MaintenanceTaskStatus.EN_ATTENTE,
                  assignedMechanic = assignedMechanic,
                  bayNumber = bayNumber,
                  requiredPartsSummary = partsSummary.ifBlank { "Pièces en stock" },
                  partsAvailable = true,
                  notes = notes
                )
                onConfirm(newTask)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
            modifier = Modifier.testTag("btn_confirm_add_task")
          ) {
            Text("Ajouter au Planning", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun ChangeRepairOrderStatusDialog(
  order: RepairOrder,
  onDismiss: () -> Unit,
  onSelectStatus: (RepairStatus) -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = MetalCard,
      border = BorderStroke(1.dp, PerformanceRedGlow),
      modifier = Modifier.padding(16.dp).fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "CHANGER LE STATUT : ${order.orderNumber}",
          style = MaterialTheme.typography.titleMedium.copy(
            color = PureWhite,
            fontWeight = FontWeight.Black,
            fontSize = 13.5.sp
          )
        )
        Text(
          text = "${order.vehicleModel} • ${order.clientName}",
          style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp)
        )

        Divider(color = MetalBorder)

        RepairStatus.entries.forEach { status ->
          val isCurrent = order.status == status
          val color = Color(status.colorHex)
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isCurrent) color.copy(alpha = 0.2f) else GraphiteBlack,
            border = BorderStroke(1.dp, if (isCurrent) color else MetalBorder),
            onClick = { onSelectStatus(status) },
            modifier = Modifier.fillMaxWidth().testTag("select_status_${status.name.lowercase()}")
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = status.label,
                  color = PureWhite,
                  fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Medium,
                  fontSize = 12.sp
                )
              }
              if (isCurrent) {
                Icon(Icons.Default.Check, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
              }
            }
          }
        }

        TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
          Text("Fermer", color = AluminumGray)
        }
      }
    }
  }
}

@Composable
private fun ReassignMaintenanceTaskDialog(
  task: DailyMaintenanceTask,
  bays: List<WorkshopBayItem>,
  onDismiss: () -> Unit,
  onConfirm: (mechanicName: String, bayNumber: Int) -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = MetalCard,
      border = BorderStroke(1.dp, TechCyan),
      modifier = Modifier.padding(16.dp).fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "RÉASSIGNER LA TÂCHE",
          style = MaterialTheme.typography.titleMedium.copy(
            color = PureWhite,
            fontWeight = FontWeight.Black,
            fontSize = 13.5.sp
          )
        )
        Text(
          text = task.taskTitle,
          style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
        )

        Divider(color = MetalBorder)

        bays.forEach { bay ->
          val isCurrent = task.bayNumber == bay.bayNumber
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isCurrent) TechCyan.copy(alpha = 0.2f) else GraphiteBlack,
            border = BorderStroke(1.dp, if (isCurrent) TechCyan else MetalBorder),
            onClick = { onConfirm(bay.assignedMechanic, bay.bayNumber) },
            modifier = Modifier.fillMaxWidth().testTag("reassign_to_bay_${bay.bayNumber}")
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(
                  text = bay.bayName,
                  color = PureWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
                Text(
                  text = "${bay.assignedMechanic} (${bay.mechanicSpecialty})",
                  color = AluminumLight,
                  fontSize = 10.5.sp
                )
              }
              if (isCurrent) {
                Text("Actuel", color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
            }
          }
        }

        TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
          Text("Annuler", color = AluminumGray)
        }
      }
    }
  }
}

@Composable
private fun AssignVehicleToBayDialog(
  bay: WorkshopBayItem,
  waitingVehicles: List<WaitingQueueVehicle>,
  onDismiss: () -> Unit,
  onSelectVehicle: (waitingVehicleId: String) -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = MetalCard,
      border = BorderStroke(1.dp, PerformanceRedGlow),
      modifier = Modifier.padding(16.dp).fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "AFFECTER UN VÉHICULE : ${bay.bayName.uppercase()}",
          style = MaterialTheme.typography.titleMedium.copy(
            color = PureWhite,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
          )
        )
        Text(
          text = "Mécanicien : ${bay.assignedMechanic} (${bay.mechanicSpecialty})",
          style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp)
        )

        Divider(color = MetalBorder)

        if (waitingVehicles.isEmpty()) {
          Text(
            text = "Aucun véhicule en file d'attente actuellement",
            color = AluminumGray,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(vertical = 12.dp)
          )
        } else {
          waitingVehicles.forEach { veh ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = GraphiteBlack,
              border = BorderStroke(1.dp, MetalBorder),
              onClick = { onSelectVehicle(veh.id) },
              modifier = Modifier.fillMaxWidth().testTag("assign_vehicle_${veh.id}")
            ) {
              Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = veh.vehicleModel,
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                  )
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (veh.priorityLevel == "URGENT") PerformanceRed.copy(alpha = 0.2f) else TechCyan.copy(alpha = 0.2f)
                  ) {
                    Text(
                      text = veh.priorityLevel,
                      color = if (veh.priorityLevel == "URGENT") PerformanceRedGlow else TechCyan,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = "${veh.operationTitle} • ${veh.estimatedMinutes} min",
                  color = AluminumLight,
                  fontSize = 10.5.sp
                )
                Text(
                  text = "Matricule : ${veh.licensePlate} • Client : ${veh.clientName}",
                  color = AluminumGray,
                  fontSize = 9.5.sp
                )
              }
            }
          }
        }

        TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
          Text("Fermer", color = AluminumGray)
        }
      }
    }
  }
}
