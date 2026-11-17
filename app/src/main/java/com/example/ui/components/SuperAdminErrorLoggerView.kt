package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun SuperAdminErrorLoggerView(
  errorLoggerState: ErrorLoggerUiState,
  onSelectCategoryFilter: (SystemErrorCategory?) -> Unit,
  onSelectSeverityFilter: (SystemErrorSeverity?) -> Unit,
  onSelectStatusFilter: (ErrorResolutionStatus?) -> Unit,
  onSearchQueryChange: (String) -> Unit,
  onSimulateCrashLog: () -> Unit,
  onSimulateApiFailureLog: () -> Unit,
  onSimulatePersistenceFailureLog: () -> Unit,
  onSimulateClientBugReport: () -> Unit = {},
  onToggleAiAutoRepair: (Boolean) -> Unit = {},
  onRepairErrorWithAi: (errorId: String) -> Unit = {},
  onRepairAllOpenErrorsWithAi: () -> Unit = {},
  onUpdateErrorStatusAndNote: (errorId: String, status: ErrorResolutionStatus, note: String) -> Unit,
  onDeleteErrorLog: (errorId: String) -> Unit,
  onPurgeResolvedLogs: () -> Unit
) {
  val filteredLogs = errorLoggerState.filteredLogs

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(14.dp)
      .testTag("super_admin_error_logger_view"),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // 1. BANNIÈRE D'ÉTAT SYSTÈME & SYNCHRONISATION FIREBASE FIRESTORE
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.5.dp, PerformanceRed.copy(alpha = 0.85f))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF280B0E), GraphiteDark, Color(0xFF141D26))
              )
            )
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(PerformanceRed.copy(alpha = 0.22f))
                  .border(1.dp, PerformanceRed, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.BugReport,
                  contentDescription = null,
                  tint = PerformanceRedGlow,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "ERROR LOGGER & AUTO-RÉPARATION IA • FIREBASE",
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 13.sp
                )
                Text(
                  text = errorLoggerState.lastSyncedTimestamp,
                  color = TechCyan,
                  fontSize = 10.5.sp,
                  fontFamily = FontFamily.Monospace
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = ElectricGreen.copy(alpha = 0.18f),
              border = BorderStroke(1.dp, ElectricGreen)
            ) {
              Text(
                text = if (errorLoggerState.isUncaughtHandlerInstalled) "CRASH + IA 24/7 ACTIF" else "INACTIF",
                color = ElectricGreen,
                fontWeight = FontWeight.Black,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
              )
            }
          }

          // 5 Compteurs KPI incluant Auto-Réparations IA & Bugs Clients
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            ErrorKpiTile(
              title = "Total Logs",
              count = errorLoggerState.logs.size,
              subtitle = "${errorLoggerState.unresolvedCount} ouverts",
              accentColor = OffWhite,
              modifier = Modifier.weight(1f)
            )
            ErrorKpiTile(
              title = "🤖 Réparés IA",
              count = errorLoggerState.totalAiAutoRepairsCount,
              subtitle = "Self-Healing",
              accentColor = ElectricGreen,
              modifier = Modifier.weight(1f)
            )
            ErrorKpiTile(
              title = "💥 Plantages",
              count = errorLoggerState.crashCount,
              subtitle = "JVM/Compose",
              accentColor = PerformanceRedGlow,
              modifier = Modifier.weight(1f)
            )
            ErrorKpiTile(
              title = "🌐 API / Sync",
              count = errorLoggerState.apiFailureCount + errorLoggerState.localPersistenceFailureCount,
              subtitle = "Cloud & Room",
              accentColor = MechanicalOrange,
              modifier = Modifier.weight(1f)
            )
            ErrorKpiTile(
              title = "📨 Clients",
              count = errorLoggerState.clientBugReportsCount,
              subtitle = "Bugs signalés",
              accentColor = Color(0xFFCE93D8),
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    // 1B. PANNEAU DE PILOTAGE MEKANIK-AI GUARDIAN (SYSTÈME D'AUTO-RÉPARATION AUTOMATIQUE 24/7)
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("ai_auto_repair_guardian_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1B24)),
        border = BorderStroke(
          1.5.dp,
          if (errorLoggerState.isAiAutoRepairEnabled) TechCyan else MetalBorder
        )
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Icon(
                Icons.Default.AutoFixHigh,
                contentDescription = null,
                tint = if (errorLoggerState.isAiAutoRepairEnabled) ElectricGreen else AluminumLight,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "🤖 MEKANIK-AI GUARDIAN • RÉPARATION AUTOMATIQUE 24/7",
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp
                )
                Text(
                  text = "En cas de plantage, bug ou rapport client, l'IA diagnostique et applique le Hot-Patch instantanément",
                  color = AluminumLight,
                  fontSize = 10.sp
                )
              }
            }

            Switch(
              checked = errorLoggerState.isAiAutoRepairEnabled,
              onCheckedChange = onToggleAiAutoRepair,
              colors = SwitchDefaults.colors(
                checkedThumbColor = GraphiteDark,
                checkedTrackColor = ElectricGreen
              ),
              modifier = Modifier.testTag("switch_ai_auto_repair_24_7")
            )
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = GraphiteBlack,
            border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(10.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = errorLoggerState.activeAiRepairStepText,
                  color = if (errorLoggerState.isAiRepairInProgress) MechanicalOrange else ElectricGreen,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.5.sp,
                  fontFamily = FontFamily.Monospace,
                  modifier = Modifier.weight(1f)
                )
              }
              if (errorLoggerState.isAiRepairInProgress) {
                LinearProgressIndicator(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(4.dp)),
                  color = TechCyan,
                  trackColor = MetalCard
                )
              }
            }
          }

          if (errorLoggerState.unresolvedCount > 0) {
            Button(
              onClick = onRepairAllOpenErrorsWithAi,
              colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_ai_repair_all_open_logs")
            ) {
              Icon(
                Icons.Default.AutoFixHigh,
                contentDescription = null,
                tint = GraphiteDark,
                modifier = Modifier.size(17.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "🤖 RÉPARER AUTOMATIQUEMENT PAR L'IA LES ${errorLoggerState.unresolvedCount} INCIDENT(S) OUVERTS",
                color = GraphiteDark,
                fontWeight = FontWeight.Black,
                fontSize = 10.5.sp
              )
            }
          }
        }
      }
    }

    // 2. CONSOLE D'INJECTION & TEST DE CAPTURE TEMPS RÉEL (CHAOS TESTING + RAPPORT CLIENT)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Column(
          modifier = Modifier.padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "⚡ BANC DE TEST AUTO-RÉPARATION IA (CRASH / API / PERSISTANCE / CLIENT)",
              color = MechanicalOrange,
              fontWeight = FontWeight.Black,
              fontSize = 10.5.sp
            )
            if (errorLoggerState.logs.any {
                it.parsedStatus == ErrorResolutionStatus.RESOLVED ||
                  it.parsedStatus == ErrorResolutionStatus.AUTO_REPAIRED_BY_AI ||
                  it.parsedStatus == ErrorResolutionStatus.IGNORED
              }
            ) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MetalCard,
                border = BorderStroke(1.dp, AluminumGray),
                modifier = Modifier
                  .clickable { onPurgeResolvedLogs() }
                  .testTag("btn_purge_resolved_error_logs")
              ) {
                Text(
                  text = "🧹 Purger Résolus",
                  color = OffWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.5.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Button(
              onClick = onSimulateCrashLog,
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
              shape = RoundedCornerShape(10.dp),
              contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("btn_simulate_crash_error_log")
            ) {
              Text(
                text = "💥 + Crash",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp
              )
            }

            Button(
              onClick = onSimulateApiFailureLog,
              colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
              shape = RoundedCornerShape(10.dp),
              contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("btn_simulate_api_error_log")
            ) {
              Text(
                text = "🌐 + Panne API",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp
              )
            }

            Button(
              onClick = onSimulatePersistenceFailureLog,
              colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
              shape = RoundedCornerShape(10.dp),
              contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("btn_simulate_persistence_error_log")
            ) {
              Text(
                text = "💾 + Sync",
                color = GraphiteDark,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp
              )
            }

            Button(
              onClick = onSimulateClientBugReport,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E24AA)),
              shape = RoundedCornerShape(10.dp),
              contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
              modifier = Modifier
                .weight(1.15f)
                .testTag("btn_simulate_client_bug_report")
            ) {
              Text(
                text = "📨 + Bug Client",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp
              )
            }
          }
        }
      }
    }

    // 3. RECHERCHE & FILTRES DYNAMIQUES MULTI-CRITÈRES
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = errorLoggerState.searchQuery,
          onValueChange = onSearchQueryChange,
          placeholder = {
            Text(
              "Rechercher par code erreur, Hotfix IA, module, stacktrace, email client...",
              fontSize = 11.sp,
              color = AluminumMuted
            )
          },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = TechCyan)
          },
          trailingIcon = {
            if (errorLoggerState.searchQuery.isNotEmpty()) {
              IconButton(onClick = { onSearchQueryChange("") }) {
                Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = AluminumLight)
              }
            }
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_error_logs_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TechCyan,
            unfocusedBorderColor = MetalBorder,
            focusedTextColor = PureWhite,
            unfocusedTextColor = PureWhite,
            focusedContainerColor = MetalCard,
            unfocusedContainerColor = MetalCard
          )
        )

        // Filtres par Catégorie
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          item {
            FilterChip(
              selected = errorLoggerState.selectedCategoryFilter == null,
              onClick = { onSelectCategoryFilter(null) },
              label = { Text("Toutes Catégories (${errorLoggerState.logs.size})", fontSize = 10.5.sp) }
            )
          }
          items(SystemErrorCategory.entries) { cat ->
            val count = errorLoggerState.logs.count { it.parsedCategory == cat }
            FilterChip(
              selected = errorLoggerState.selectedCategoryFilter == cat,
              onClick = {
                onSelectCategoryFilter(
                  if (errorLoggerState.selectedCategoryFilter == cat) null else cat
                )
              },
              label = { Text("${cat.shortTabLabel} ($count)", fontSize = 10.5.sp) },
              modifier = Modifier.testTag("filter_error_cat_${cat.id.lowercase()}")
            )
          }
        }

        // Filtres par Sévérité & Statut
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          item {
            FilterChip(
              selected = errorLoggerState.selectedStatusFilter == null && errorLoggerState.selectedSeverityFilter == null,
              onClick = {
                onSelectStatusFilter(null)
                onSelectSeverityFilter(null)
              },
              label = { Text("Tous Statuts & Sévérités", fontSize = 10.sp) }
            )
          }
          items(ErrorResolutionStatus.entries) { st ->
            FilterChip(
              selected = errorLoggerState.selectedStatusFilter == st,
              onClick = {
                onSelectStatusFilter(
                  if (errorLoggerState.selectedStatusFilter == st) null else st
                )
              },
              label = { Text(st.badgeText, fontSize = 10.sp) }
            )
          }
          items(listOf(SystemErrorSeverity.CRITICAL, SystemErrorSeverity.HIGH)) { sev ->
            FilterChip(
              selected = errorLoggerState.selectedSeverityFilter == sev,
              onClick = {
                onSelectSeverityFilter(
                  if (errorLoggerState.selectedSeverityFilter == sev) null else sev
                )
              },
              label = { Text(sev.badgeText, fontSize = 10.sp) }
            )
          }
        }
      }
    }

    // 4. LISTE DES ENTRÉES DU JOURNAL D'ERREURS
    if (filteredLogs.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              Icons.Default.CheckCircle,
              contentDescription = null,
              tint = ElectricGreen,
              modifier = Modifier.size(36.dp)
            )
            Text(
              text = "Aucune erreur ne correspond aux filtres sélectionnés",
              color = PureWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
            Text(
              text = "MekanikAI Guardian veille en permanence sur les plantages, les APIs et les rapports clients.",
              color = AluminumLight,
              fontSize = 11.sp
            )
          }
        }
      }
    } else {
      items(filteredLogs, key = { it.id }) { log ->
        SuperAdminErrorLogCard(
          log = log,
          onRepairWithAi = { onRepairErrorWithAi(log.id) },
          onUpdateStatusAndNote = { status, note ->
            onUpdateErrorStatusAndNote(log.id, status, note)
          },
          onDelete = { onDeleteErrorLog(log.id) }
        )
      }
    }
  }
}

@Composable
private fun ErrorKpiTile(
  title: String,
  count: Int,
  subtitle: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = GraphiteBlack.copy(alpha = 0.85f),
    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 7.dp, vertical = 7.dp),
      verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      Text(
        text = title,
        color = AluminumLight,
        fontSize = 9.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Text(
        text = count.toString(),
        color = accentColor,
        fontWeight = FontWeight.Black,
        fontSize = 15.5.sp
      )
      Text(
        text = subtitle,
        color = AluminumMuted,
        fontSize = 8.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
private fun SuperAdminErrorLogCard(
  log: FirestoreSystemErrorLog,
  onRepairWithAi: () -> Unit,
  onUpdateStatusAndNote: (ErrorResolutionStatus, String) -> Unit,
  onDelete: () -> Unit
) {
  val category = log.parsedCategory
  val severity = log.parsedSeverity
  val status = log.parsedStatus
  val catColor = Color(category.colorHex)
  val sevColor = Color(severity.colorHex)
  val statusColor = Color(status.colorHex)

  var showStackTrace by remember { mutableStateOf(false) }
  var showNoteEditor by remember { mutableStateOf(false) }
  var noteInput by remember(log.superAdminNote) { mutableStateOf(log.superAdminNote) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("error_log_card_${log.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(
      width = if (severity == SystemErrorSeverity.CRITICAL && status == ErrorResolutionStatus.OPEN) 1.5.dp else 1.dp,
      color = if (status == ErrorResolutionStatus.RESOLVED || status == ErrorResolutionStatus.AUTO_REPAIRED_BY_AI) {
        ElectricGreen.copy(alpha = 0.65f)
      } else {
        catColor.copy(alpha = 0.75f)
      }
    )
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Top badges row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = catColor.copy(alpha = 0.18f),
            border = BorderStroke(1.dp, catColor)
          ) {
            Text(
              text = category.badgeText,
              color = catColor,
              fontWeight = FontWeight.Black,
              fontSize = 8.5.sp,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = sevColor.copy(alpha = 0.18f),
            border = BorderStroke(1.dp, sevColor)
          ) {
            Text(
              text = severity.badgeText,
              color = sevColor,
              fontWeight = FontWeight.Black,
              fontSize = 8.5.sp,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = statusColor.copy(alpha = 0.18f),
            border = BorderStroke(1.dp, statusColor)
          ) {
            Text(
              text = status.badgeText,
              color = statusColor,
              fontWeight = FontWeight.Black,
              fontSize = 8.5.sp,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Text(
          text = log.formattedDate,
          color = AluminumMuted,
          fontSize = 9.5.sp,
          fontFamily = FontFamily.Monospace
        )
      }

      // Error code & title
      Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = log.errorCode,
            color = TechCyan,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
          Text(
            text = "• ${log.moduleSource}",
            color = MechanicalOrange,
            fontWeight = FontWeight.Bold,
            fontSize = 10.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Text(
          text = log.errorTitle,
          color = PureWhite,
          fontWeight = FontWeight.Bold,
          fontSize = 12.5.sp
        )

        Text(
          text = log.technicalMessage,
          color = AluminumLight,
          fontSize = 11.sp
        )
      }

      // Metadata strip (User, Role, Device Hardware ID)
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = GraphiteBlack,
        border = BorderStroke(1.dp, MetalBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(8.dp),
          verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "👤 Opérateur/Client : ${log.userEmail} [${log.rbacRole}]",
              color = OffWhite,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = "☁️ ID: ${log.id.take(14)}",
              color = TechCyan,
              fontSize = 9.5.sp,
              fontFamily = FontFamily.Monospace
            )
          }
          Text(
            text = "📱 Appareil : ${log.deviceHardwareInfo}",
            color = AluminumMuted,
            fontSize = 9.5.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      // Rapport structuré d'Auto-Réparation IA si présent
      log.aiAutoRepairReport?.let { aiReport ->
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFF0B1C19),
          border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.7f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.AutoFixHigh,
                  contentDescription = null,
                  tint = ElectricGreen,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "RAPPORT AUTO-RÉPARATION IA • ${aiReport.hotfixId}",
                  color = ElectricGreen,
                  fontWeight = FontWeight.Black,
                  fontSize = 10.5.sp
                )
              }
              Text(
                text = "Fiabilité ${aiReport.confidenceScorePercent}% • ${aiReport.repairedAtFormatted}",
                color = TechCyan,
                fontSize = 9.5.sp,
                fontFamily = FontFamily.Monospace
              )
            }

            Text(
              text = "Cause racine : ${aiReport.rootCauseAnalysis}",
              color = OffWhite,
              fontSize = 10.5.sp
            )

            aiReport.executedActions.forEach { step ->
              Text(
                text = "✔ $step",
                color = ElectricGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
              )
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFF060A0D),
              border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.35f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = aiReport.generatedPatchSnippet,
                color = TechCyan,
                fontSize = 9.5.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(7.dp)
              )
            }
          }
        }
      } ?: run {
        // Note SuperAdmin ou note IA persistée si aiAutoRepairReport n'est pas en mémoire
        if (log.superAdminNote.isNotBlank()) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = ElectricGreen.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                Icons.Default.VerifiedUser,
                contentDescription = null,
                tint = ElectricGreen,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = log.superAdminNote,
                color = ElectricGreen,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.5.sp
              )
            }
          }
        }
      }

      // StackTrace expandable terminal
      AnimatedVisibility(visible = showStackTrace) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFF0A0D10),
          border = BorderStroke(1.dp, PerformanceRed.copy(alpha = 0.6f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "TERMINAL STACKTRACE & DIAGNOSTIC PAYLOAD FIREBASE",
                color = PerformanceRedGlow,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = "UTF-8 • JSON/JVM",
                color = TechCyan,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
              )
            }
            HorizontalDivider(color = MetalBorder)
            Text(
              text = log.stackTracePreview,
              color = Color(0xFFE2E8F0),
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              lineHeight = 14.sp
            )
          }
        }
      }

      // Editor for SuperAdmin Note
      AnimatedVisibility(visible = showNoteEditor) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = noteInput,
            onValueChange = { noteInput = it },
            label = { Text("Note de résolution SuperAdmin", fontSize = 10.sp) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ElectricGreen,
              unfocusedBorderColor = MetalBorder,
              focusedTextColor = PureWhite,
              unfocusedTextColor = PureWhite
            )
          )
          Button(
            onClick = {
              onUpdateStatusAndNote(status, noteInput)
              showNoteEditor = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Enregistrer", color = GraphiteDark, fontWeight = FontWeight.Black, fontSize = 10.5.sp)
          }
        }
      }

      // Action bar for SuperAdmin & AI Auto-Repair
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = { showStackTrace = !showStackTrace },
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, TechCyan),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          modifier = Modifier.weight(1f)
        ) {
          Icon(
            if (showStackTrace) Icons.Default.ExpandLess else Icons.Default.Terminal,
            contentDescription = null,
            tint = TechCyan,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (showStackTrace) "Masquer" else "Trace",
            color = TechCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        }

        Button(
          onClick = onRepairWithAi,
          colors = ButtonDefaults.buttonColors(
            containerColor = if (log.isRepairedByAi) Color(0xFF1A3A34) else TechCyan
          ),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          modifier = Modifier
            .weight(1.25f)
            .testTag("btn_ai_repair_error_${log.id}")
        ) {
          Icon(
            Icons.Default.AutoFixHigh,
            contentDescription = null,
            tint = if (log.isRepairedByAi) ElectricGreen else GraphiteDark,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (log.isRepairedByAi) "🤖 Re-Vérifier IA" else "🤖 Réparer par IA",
            color = if (log.isRepairedByAi) ElectricGreen else GraphiteDark,
            fontWeight = FontWeight.Black,
            fontSize = 9.5.sp
          )
        }

        if (status != ErrorResolutionStatus.RESOLVED && status != ErrorResolutionStatus.AUTO_REPAIRED_BY_AI) {
          Button(
            onClick = {
              onUpdateStatusAndNote(
                ErrorResolutionStatus.RESOLVED,
                noteInput.ifBlank { "Corrigé et validé par SuperAdmin" }
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier
              .weight(0.95f)
              .testTag("btn_resolve_error_${log.id}")
          ) {
            Text(
              text = "✅ Résoudre",
              color = GraphiteDark,
              fontWeight = FontWeight.Black,
              fontSize = 9.5.sp
            )
          }
        }

        IconButton(
          onClick = { showNoteEditor = !showNoteEditor },
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(GraphiteBlack)
        ) {
          Icon(
            Icons.Default.EditNote,
            contentDescription = "Note SuperAdmin",
            tint = OffWhite,
            modifier = Modifier.size(17.dp)
          )
        }

        IconButton(
          onClick = onDelete,
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(GraphiteBlack)
            .testTag("btn_delete_error_${log.id}")
        ) {
          Icon(
            Icons.Default.DeleteOutline,
            contentDescription = "Supprimer",
            tint = PerformanceRedGlow,
            modifier = Modifier.size(17.dp)
          )
        }
      }
    }
  }
}
