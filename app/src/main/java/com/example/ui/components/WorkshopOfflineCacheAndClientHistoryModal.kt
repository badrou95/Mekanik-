package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.CachedTechnicalSchematicEntity
import com.example.data.local.ClientFaultHistoryEntity
import com.example.data.local.SavedDiagnosticRecordEntity
import com.example.data.local.VehicleRepairLogEntity
import com.example.data.repository.LocalCacheStorageSummary
import com.example.data.repository.WorkshopNetworkCondition
import com.example.ui.theme.*

enum class OfflineCacheHubTab(val label: String, val shortLabel: String) {
  CLIENT_FAULTS_HISTORY("Historique & Pannes Clients", "Pannes Clients"),
  SAVED_DIAGNOSTICS("Diagnostics Enregistrés", "Diagnostics"),
  CACHED_SCHEMATICS("Schémas Techniques (Cache 0ms)", "Schémas 0ms")
}

/**
 * Bannière compacte affichant l'état de la stratégie de mise en cache locale Room SQLite
 * (Schémas techniques, Pannes clients et Diagnostics enregistrés) pour faible connexion atelier.
 */
@Composable
fun WorkshopOfflineCacheStatusBanner(
  networkCondition: WorkshopNetworkCondition,
  cacheSummary: LocalCacheStorageSummary,
  onOpenCacheHub: (OfflineCacheHubTab) -> Unit,
  onCycleNetworkCondition: () -> Unit,
  modifier: Modifier = Modifier
) {
  val statusColor = when (networkCondition) {
    WorkshopNetworkCondition.OPTIMAL_ONLINE -> MekanikValidationGreen
    WorkshopNetworkCondition.WEAK_WORKSHOP_SIGNAL -> MekanikBrandOrange
    WorkshopNetworkCondition.OFFLINE_BASEMENT -> MekanikErrorRed
  }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("workshop_offline_cache_status_banner"),
    shape = RoundedCornerShape(14.dp),
    color = MekanikCardSurface,
    border = BorderStroke(1.dp, MetalBorder)
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
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(statusColor.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = when (networkCondition) {
                WorkshopNetworkCondition.OPTIMAL_ONLINE -> Icons.Default.CloudDone
                WorkshopNetworkCondition.WEAK_WORKSHOP_SIGNAL -> Icons.Default.SignalCellularAlt2Bar
                WorkshopNetworkCondition.OFFLINE_BASEMENT -> Icons.Default.OfflineBolt
              },
              contentDescription = null,
              tint = statusColor,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "CACHE LOCAL ATELIER & HISTORIQUE PANNES",
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp
              )
            }
            Text(
              text = "${networkCondition.shortBadge} • ${networkCondition.latencyLabel}",
              color = statusColor,
              fontWeight = FontWeight.SemiBold,
              fontSize = 11.sp
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MekanikBgDark,
          border = BorderStroke(1.dp, statusColor.copy(alpha = 0.6f)),
          modifier = Modifier
            .clickable { onCycleNetworkCondition() }
            .testTag("btn_cycle_workshop_network_mode")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.NetworkCheck,
              contentDescription = "Mode réseau",
              tint = statusColor,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Mode Réseau",
              color = PureWhite,
              fontWeight = FontWeight.SemiBold,
              fontSize = 10.5.sp
            )
          }
        }
      }

      // 3 Quick Pills : Pannes Clients | Diagnostics Enregistrés | Schémas en Cache
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        CacheMetricPill(
          icon = Icons.Default.CarCrash,
          label = "Pannes Clients",
          count = cacheSummary.clientFaultsCount,
          onClick = { onOpenCacheHub(OfflineCacheHubTab.CLIENT_FAULTS_HISTORY) },
          modifier = Modifier.weight(1f).testTag("pill_open_client_faults")
        )
        CacheMetricPill(
          icon = Icons.Default.Troubleshoot,
          label = "Diagnostics",
          count = cacheSummary.savedDiagnosticsCount,
          onClick = { onOpenCacheHub(OfflineCacheHubTab.SAVED_DIAGNOSTICS) },
          modifier = Modifier.weight(1f).testTag("pill_open_saved_diagnostics")
        )
        CacheMetricPill(
          icon = Icons.Default.ElectricalServices,
          label = "Schémas 0ms",
          count = cacheSummary.cachedSchematicsCount,
          onClick = { onOpenCacheHub(OfflineCacheHubTab.CACHED_SCHEMATICS) },
          modifier = Modifier.weight(1f).testTag("pill_open_cached_schematics")
        )
      }
    }
  }
}

@Composable
private fun CacheMetricPill(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  count: Int,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = MekanikBgDark,
    border = BorderStroke(1.dp, MetalBorder),
    modifier = modifier.clickable { onClick() }
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MekanikBrandOrange,
          modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = label,
          color = AluminumGray,
          fontSize = 11.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = count.toString(),
        color = PureWhite,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
      )
    }
  }
}

/**
 * Fenêtre modale plein écran regroupant :
 * 1. L'historique client et les pannes des clients
 * 2. Les diagnostics enregistrés (OBD-II & MekanikAI)
 * 3. Les schémas techniques et procédures en cache local (Room SQLite) pour faible connexion atelier.
 */
@Composable
fun WorkshopOfflineCacheAndClientHistoryModal(
  initialTab: OfflineCacheHubTab = OfflineCacheHubTab.CLIENT_FAULTS_HISTORY,
  networkCondition: WorkshopNetworkCondition,
  cacheSummary: LocalCacheStorageSummary,
  cachedSchematics: List<CachedTechnicalSchematicEntity>,
  savedDiagnostics: List<SavedDiagnosticRecordEntity>,
  clientFaultHistory: List<ClientFaultHistoryEntity>,
  vehicleRepairLogs: List<VehicleRepairLogEntity>,
  activeVehicleModel: String,
  activeVin: String,
  onSelectNetworkCondition: (WorkshopNetworkCondition) -> Unit,
  onPreloadAllSchematics: () -> Unit,
  onTogglePinSchematic: (String) -> Unit,
  onRemoveSchematic: (String) -> Unit,
  onSyncPendingDiagnostics: () -> Unit,
  onDeleteSavedDiagnostic: (String) -> Unit,
  onAddClientFaultRecord: (
    clientName: String,
    clientPhone: String,
    vehicleModel: String,
    vin: String,
    licensePlate: String,
    mileageKm: Int,
    faultTitle: String,
    symptoms: String,
    affectedSystem: String,
    dtcCodes: String,
    diagnosticConclusion: String,
    repairAction: String,
    partsReplaced: String,
    costDzd: Long,
    technicianName: String,
    status: String
  ) -> Unit,
  onUpdateClientFaultStatus: (String, String) -> Unit,
  onDeleteClientFaultRecord: (String) -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)
        .testTag("workshop_offline_cache_client_history_modal"),
      shape = RoundedCornerShape(16.dp),
      color = MekanikBgDark,
      border = BorderStroke(1.dp, MetalBorder)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Top Header Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(MekanikCardSurface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MekanikBrandOrange.copy(alpha = 0.18f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Storage,
                contentDescription = null,
                tint = MekanikBrandOrange,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "CACHE LOCAL ATELIER • PANNES CLIENTS & DIAGNOSTICS",
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "${cacheSummary.lastCacheSyncFormatted} • ${cacheSummary.totalStorageSizeKb} Ko en base locale",
                color = AluminumGray,
                fontSize = 11.sp
              )
            }
          }

          MekanikIconButton(
            icon = Icons.Default.Close,
            contentDescription = "Fermer",
            onClick = onDismiss,
            modifier = Modifier.testTag("btn_close_offline_cache_hub_modal")
          )
        }

        HorizontalDivider(color = MetalBorder, thickness = 1.dp)

        WorkshopOfflineCacheAndClientHistoryContent(
          initialTab = initialTab,
          networkCondition = networkCondition,
          cacheSummary = cacheSummary,
          cachedSchematics = cachedSchematics,
          savedDiagnostics = savedDiagnostics,
          clientFaultHistory = clientFaultHistory,
          vehicleRepairLogs = vehicleRepairLogs,
          activeVehicleModel = activeVehicleModel,
          activeVin = activeVin,
          onSelectNetworkCondition = onSelectNetworkCondition,
          onPreloadAllSchematics = onPreloadAllSchematics,
          onTogglePinSchematic = onTogglePinSchematic,
          onRemoveSchematic = onRemoveSchematic,
          onSyncPendingDiagnostics = onSyncPendingDiagnostics,
          onDeleteSavedDiagnostic = onDeleteSavedDiagnostic,
          onAddClientFaultRecord = onAddClientFaultRecord,
          onUpdateClientFaultStatus = onUpdateClientFaultStatus,
          onDeleteClientFaultRecord = onDeleteClientFaultRecord,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

/**
 * Contenu principal réutilisable dans l'onglet Espace Atelier ET dans la modale dédiée.
 */
@Composable
fun WorkshopOfflineCacheAndClientHistoryContent(
  initialTab: OfflineCacheHubTab = OfflineCacheHubTab.CLIENT_FAULTS_HISTORY,
  networkCondition: WorkshopNetworkCondition,
  cacheSummary: LocalCacheStorageSummary,
  cachedSchematics: List<CachedTechnicalSchematicEntity>,
  savedDiagnostics: List<SavedDiagnosticRecordEntity>,
  clientFaultHistory: List<ClientFaultHistoryEntity>,
  vehicleRepairLogs: List<VehicleRepairLogEntity>,
  activeVehicleModel: String,
  activeVin: String,
  onSelectNetworkCondition: (WorkshopNetworkCondition) -> Unit,
  onPreloadAllSchematics: () -> Unit,
  onTogglePinSchematic: (String) -> Unit,
  onRemoveSchematic: (String) -> Unit,
  onSyncPendingDiagnostics: () -> Unit,
  onDeleteSavedDiagnostic: (String) -> Unit,
  onAddClientFaultRecord: (
    clientName: String,
    clientPhone: String,
    vehicleModel: String,
    vin: String,
    licensePlate: String,
    mileageKm: Int,
    faultTitle: String,
    symptoms: String,
    affectedSystem: String,
    dtcCodes: String,
    diagnosticConclusion: String,
    repairAction: String,
    partsReplaced: String,
    costDzd: Long,
    technicianName: String,
    status: String
  ) -> Unit,
  onUpdateClientFaultStatus: (String, String) -> Unit,
  onDeleteClientFaultRecord: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember(initialTab) { mutableStateOf(initialTab) }
  var searchQuery by remember { mutableStateOf("") }
  var selectedSchematicCategory by remember { mutableStateOf("TOUS") }
  var showAddClientFaultForm by remember { mutableStateOf(false) }

  // Form states for adding a new client breakdown / fault + diagnostic
  var formClientName by remember { mutableStateOf("") }
  var formClientPhone by remember { mutableStateOf("0550 ") }
  var formPlateNumber by remember { mutableStateOf("04291-120-16") }
  var formVehicleModel by remember(activeVehicleModel) { mutableStateOf(activeVehicleModel) }
  var formVin by remember(activeVin) { mutableStateOf(activeVin) }
  var formMileage by remember { mutableStateOf("135000") }
  var formFaultTitle by remember { mutableStateOf("") }
  var formSymptoms by remember { mutableStateOf("") }
  var formSystem by remember { mutableStateOf("Injection & Suralimentation") }
  var formDtcCodes by remember { mutableStateOf("P0299") }
  var formDiagConclusion by remember { mutableStateOf("") }
  var formRepairAction by remember { mutableStateOf("") }
  var formPartsReplaced by remember { mutableStateOf("") }
  var formCostDzd by remember { mutableStateOf("18500") }
  var formTechnician by remember { mutableStateOf("Mourad (Chef d'Atelier)") }
  var formStatus by remember { mutableStateOf("EN_DIAGNOSTIC") }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MekanikBgDark)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(vertical = 14.dp)
  ) {
    // 1. Carte Stratégie Réseau Atelier & Synchronisation SQLite Room
    item {
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = MekanikCardSurface,
        border = BorderStroke(1.dp, MetalBorder),
        modifier = Modifier.fillMaxWidth()
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
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "STRATÉGIE DE CACHE LOCAL ATELIER (ROOM SQLITE)",
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Text(
                text = networkCondition.description,
                color = AluminumGray,
                fontSize = 11.5.sp
              )
            }
          }

          // Sélecteur de condition réseau (Normal / Faible connexion atelier / Hors-ligne sous-sol)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            WorkshopNetworkCondition.entries.forEach { mode ->
              val isSelected = networkCondition == mode
              val modeAccent = when (mode) {
                WorkshopNetworkCondition.OPTIMAL_ONLINE -> MekanikValidationGreen
                WorkshopNetworkCondition.WEAK_WORKSHOP_SIGNAL -> MekanikBrandOrange
                WorkshopNetworkCondition.OFFLINE_BASEMENT -> MekanikErrorRed
              }
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) modeAccent.copy(alpha = 0.18f) else MekanikBgDark,
                border = BorderStroke(1.dp, if (isSelected) modeAccent else MetalBorder),
                modifier = Modifier
                  .weight(1f)
                  .clickable { onSelectNetworkCondition(mode) }
                  .testTag("network_mode_chip_${mode.name.lowercase()}")
              ) {
                Column(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = when (mode) {
                      WorkshopNetworkCondition.OPTIMAL_ONLINE -> "4G / Wi-Fi"
                      WorkshopNetworkCondition.WEAK_WORKSHOP_SIGNAL -> "Faible Réseau"
                      WorkshopNetworkCondition.OFFLINE_BASEMENT -> "Sous-Sol 0 Réseau"
                    },
                    color = if (isSelected) PureWhite else AluminumGray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 11.sp
                  )
                  Text(
                    text = if (isSelected) "ACTIF (0ms)" else "Simuler",
                    color = if (isSelected) modeAccent else AluminumGray,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }
          }

          // Barre d'actions rapides de mise en cache
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            MekanikPrimaryButton(
              text = "Mettre tous les schémas en cache (${cacheSummary.cachedSchematicsCount})",
              onClick = onPreloadAllSchematics,
              icon = Icons.Default.DownloadForOffline,
              modifier = Modifier
                .weight(1f)
                .testTag("btn_preload_all_schematics_cache")
            )
            if (cacheSummary.unsyncedDiagnosticsCount > 0) {
              MekanikSecondaryButton(
                text = "Sync (${cacheSummary.unsyncedDiagnosticsCount})",
                onClick = onSyncPendingDiagnostics,
                icon = Icons.Default.Sync,
                modifier = Modifier.testTag("btn_sync_pending_diagnostics")
              )
            }
          }
        }
      }
    }

    // 2. Onglets : Pannes & Historique Clients | Diagnostics Enregistrés | Schémas Techniques (0ms)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OfflineCacheHubTab.entries.forEach { tab ->
          val isSelected = selectedTab == tab
          val countBadge = when (tab) {
            OfflineCacheHubTab.CLIENT_FAULTS_HISTORY -> clientFaultHistory.size
            OfflineCacheHubTab.SAVED_DIAGNOSTICS -> savedDiagnostics.size
            OfflineCacheHubTab.CACHED_SCHEMATICS -> cachedSchematics.size
          }
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) MekanikBrandOrange else MekanikCardSurface,
            border = BorderStroke(1.dp, if (isSelected) MekanikBrandOrange else MetalBorder),
            modifier = Modifier
              .weight(1f)
              .heightIn(min = 48.dp)
              .clickable { selectedTab = tab }
              .testTag("tab_offline_hub_${tab.name.lowercase()}")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Text(
                text = "${tab.shortLabel} ($countBadge)",
                color = if (isSelected) PureWhite else OffWhite,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }
      }
    }

    // 3. Barre de recherche instantanée dans le cache local (0 ms)
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = {
          Text(
            text = when (selectedTab) {
              OfflineCacheHubTab.CLIENT_FAULTS_HISTORY -> "Rechercher client, matricule, VIN, panne, code défaut..."
              OfflineCacheHubTab.SAVED_DIAGNOSTICS -> "Rechercher code OBD (ex: P0299, P0087), véhicule, client..."
              OfflineCacheHubTab.CACHED_SCHEMATICS -> "Rechercher schéma électrique, brochage ECU, moteur (DFGA, K9K)..."
            },
            color = AluminumGray,
            fontSize = 12.5.sp
          )
        },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = null, tint = MekanikBrandOrange)
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = AluminumGray)
            }
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("offline_cache_search_input"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = MekanikCardSurface,
          unfocusedContainerColor = MekanikCardSurface,
          focusedBorderColor = MekanikBrandOrange,
          unfocusedBorderColor = MetalBorder,
          focusedTextColor = PureWhite,
          unfocusedTextColor = PureWhite
        ),
        singleLine = true
      )
    }

    // ========================================================================
    // ONGLET 1 : HISTORIQUE CLIENT & PANNES DES CLIENTS
    // ========================================================================
    when (selectedTab) {
      OfflineCacheHubTab.CLIENT_FAULTS_HISTORY -> {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "HISTORIQUE CLIENT & REGISTRE DES PANNES",
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp
              )
              Text(
                text = "Consultable hors-ligne • Lié aux codes défauts OBD et interventions",
                color = AluminumGray,
                fontSize = 11.sp
              )
            }
            MekanikPrimaryButton(
              text = if (showAddClientFaultForm) "Fermer" else "+ Nouvelle Panne",
              onClick = { showAddClientFaultForm = !showAddClientFaultForm },
              icon = if (showAddClientFaultForm) Icons.Default.ExpandLess else Icons.Default.Add,
              modifier = Modifier.testTag("btn_toggle_add_client_fault_form")
            )
          }
        }

        // Formulaire d'ajout d'une panne client + diagnostic enregistré
        if (showAddClientFaultForm) {
          item {
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = MekanikCardSurface,
              border = BorderStroke(1.dp, MekanikBrandOrange),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("add_client_fault_form_card")
            ) {
              Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Text(
                  text = "ENREGISTRER UNE PANNE CLIENT & SON DIAGNOSTIC (CACHE LOCAL + DOSSIER)",
                  color = PureWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  OutlinedTextField(
                    value = formClientName,
                    onValueChange = { formClientName = it },
                    label = { Text("Nom du Client") },
                    modifier = Modifier.weight(1f).testTag("input_fault_client_name"),
                    singleLine = true
                  )
                  OutlinedTextField(
                    value = formClientPhone,
                    onValueChange = { formClientPhone = it },
                    label = { Text("Téléphone") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                  )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  OutlinedTextField(
                    value = formPlateNumber,
                    onValueChange = { formPlateNumber = it },
                    label = { Text("Matricule") },
                    modifier = Modifier.weight(1f).testTag("input_fault_plate"),
                    singleLine = true
                  )
                  OutlinedTextField(
                    value = formMileage,
                    onValueChange = { formMileage = it },
                    label = { Text("Kilométrage (km)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                  )
                }

                OutlinedTextField(
                  value = formVehicleModel,
                  onValueChange = { formVehicleModel = it },
                  label = { Text("Véhicule & Motorisation") },
                  modifier = Modifier.fillMaxWidth(),
                  singleLine = true
                )

                OutlinedTextField(
                  value = formFaultTitle,
                  onValueChange = { formFaultTitle = it },
                  label = { Text("Intitulé de la panne (ex: Coupure injection en côte)") },
                  modifier = Modifier.fillMaxWidth().testTag("input_fault_title"),
                  singleLine = true
                )

                OutlinedTextField(
                  value = formSymptoms,
                  onValueChange = { formSymptoms = it },
                  label = { Text("Symptômes constatés par le client") },
                  modifier = Modifier.fillMaxWidth(),
                  minLines = 2
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  OutlinedTextField(
                    value = formDtcCodes,
                    onValueChange = { formDtcCodes = it },
                    label = { Text("Codes OBD (ex: P0299)") },
                    modifier = Modifier.weight(1f).testTag("input_fault_dtc"),
                    singleLine = true
                  )
                  OutlinedTextField(
                    value = formCostDzd,
                    onValueChange = { formCostDzd = it },
                    label = { Text("Coût estimé (DA)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                  )
                }

                OutlinedTextField(
                  value = formDiagConclusion,
                  onValueChange = { formDiagConclusion = it },
                  label = { Text("Diagnostic technique & cause identifiée") },
                  modifier = Modifier.fillMaxWidth(),
                  minLines = 2
                )

                OutlinedTextField(
                  value = formRepairAction,
                  onValueChange = { formRepairAction = it },
                  label = { Text("Intervention réalisée / Préconisation") },
                  modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                  value = formPartsReplaced,
                  onValueChange = { formPartsReplaced = it },
                  label = { Text("Pièces remplacées ou commandées (Réf OEM)") },
                  modifier = Modifier.fillMaxWidth()
                )

                MekanikPrimaryButton(
                  text = "Enregistrer dans l'Historique Client & Cache Local",
                  icon = Icons.Default.Save,
                  onClick = {
                    val safeClient = formClientName.ifBlank { "Client Atelier (${formPlateNumber})" }
                    val safeTitle = formFaultTitle.ifBlank { "Panne signalée ($formDtcCodes)" }
                    onAddClientFaultRecord(
                      safeClient,
                      formClientPhone,
                      formVehicleModel,
                      formVin,
                      formPlateNumber,
                      formMileage.toIntOrNull() ?: 120000,
                      safeTitle,
                      formSymptoms.ifBlank { "Symptôme enregistré en atelier" },
                      formSystem,
                      formDtcCodes,
                      formDiagConclusion.ifBlank { "Diagnostic enregistré en cache local SQLite" },
                      formRepairAction.ifBlank { "Contrôle et intervention atelier" },
                      formPartsReplaced.ifBlank { "Pièces OEM selon diagnostic" },
                      formCostDzd.toLongOrNull() ?: 15000L,
                      formTechnician,
                      formStatus
                    )
                    formClientName = ""
                    formFaultTitle = ""
                    formSymptoms = ""
                    formDiagConclusion = ""
                    showAddClientFaultForm = false
                  },
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_submit_new_client_fault")
                )
              }
            }
          }
        }

        val filteredFaults = clientFaultHistory.filter { item ->
          searchQuery.isBlank() ||
            item.clientName.contains(searchQuery, ignoreCase = true) ||
            item.licensePlate.contains(searchQuery, ignoreCase = true) ||
            item.vehicleModel.contains(searchQuery, ignoreCase = true) ||
            item.faultTitle.contains(searchQuery, ignoreCase = true) ||
            item.linkedDtcCodes.contains(searchQuery, ignoreCase = true) ||
            item.customerReportedSymptoms.contains(searchQuery, ignoreCase = true)
        }

        items(filteredFaults, key = { it.id }) { fault ->
          ClientFaultHistoryCard(
            fault = fault,
            onUpdateStatus = { newStatus -> onUpdateClientFaultStatus(fault.id, newStatus) },
            onDelete = { onDeleteClientFaultRecord(fault.id) }
          )
        }

        // Section repliable : Journal SQLite des Réparations Véhicules (VehicleRepairLogEntity)
        item {
          MekanikCollapsibleSection(
            title = "Journal SQLite des Interventions Véhicules (${vehicleRepairLogs.size})",
            subtitle = "Table Room vehicle_repair_logs synchronisée localement",
            icon = Icons.Default.History,
            initiallyExpanded = false
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              vehicleRepairLogs.forEach { log ->
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = MekanikBgDark,
                  border = BorderStroke(1.dp, MetalBorder),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "${log.vehicleModel} • ${log.licensePlate}",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                      )
                      Text(
                        text = log.interventionType,
                        color = MekanikBrandOrange,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.5.sp
                      )
                      Text(
                        text = "${log.description} • ${log.mileageKm} km • ${log.technicianName}",
                        color = AluminumGray,
                        fontSize = 10.5.sp
                      )
                    }
                    Text(
                      text = "${log.costDzd} DA",
                      color = MekanikValidationGreen,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp
                    )
                  }
                }
              }
            }
          }
        }
      }

      // ======================================================================
      // ONGLET 2 : DIAGNOSTICS ENREGISTRÉS (OBD-II & MEKANIKAI HORS-LIGNE)
      // ======================================================================
      OfflineCacheHubTab.SAVED_DIAGNOSTICS -> {
        val filteredDiags = savedDiagnostics.filter { diag ->
          searchQuery.isBlank() ||
            diag.dtcCode.contains(searchQuery, ignoreCase = true) ||
            diag.diagnosticTitle.contains(searchQuery, ignoreCase = true) ||
            diag.vehicleModel.contains(searchQuery, ignoreCase = true) ||
            diag.clientName.contains(searchQuery, ignoreCase = true) ||
            diag.licensePlate.contains(searchQuery, ignoreCase = true)
        }

        item {
          Text(
            text = "DIAGNOSTICS ENREGISTRÉS EN BASE LOCALE (${filteredDiags.size})",
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp
          )
        }

        items(filteredDiags, key = { it.id }) { diag ->
          SavedDiagnosticRecordCard(
            diag = diag,
            onDelete = { onDeleteSavedDiagnostic(diag.id) }
          )
        }
      }

      // ======================================================================
      // ONGLET 3 : SCHÉMAS TECHNIQUES EN CACHE LOCAL (0 ms)
      // ======================================================================
      OfflineCacheHubTab.CACHED_SCHEMATICS -> {
        item {
          val categories = listOf(
            "TOUS" to "Tous (${cachedSchematics.size})",
            "SCHEMA_ELECTRIQUE" to "Schémas Électriques",
            "PROCEDURE_MANUEL" to "Procédures OEM",
            "COUPLES_FLUIDES" to "Couples & Fluides",
            "PINNED" to "Épinglés Hors-Ligne"
          )
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { (code, label) ->
              val isSelected = selectedSchematicCategory == code
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) MekanikBrandOrange else MekanikCardSurface,
                border = BorderStroke(1.dp, if (isSelected) MekanikBrandOrange else MetalBorder),
                modifier = Modifier.clickable { selectedSchematicCategory = code }
              ) {
                Text(
                  text = label,
                  color = PureWhite,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 11.5.sp,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
              }
            }
          }
        }

        val filteredSchematics = cachedSchematics.filter { schema ->
          val matchesCategory = when (selectedSchematicCategory) {
            "TOUS" -> true
            "PINNED" -> schema.isPinnedOffline
            else -> schema.category == selectedSchematicCategory
          }
          val matchesQuery = searchQuery.isBlank() ||
            schema.title.contains(searchQuery, ignoreCase = true) ||
            schema.vehicleBrand.contains(searchQuery, ignoreCase = true) ||
            schema.vehicleModel.contains(searchQuery, ignoreCase = true) ||
            schema.engineCode.contains(searchQuery, ignoreCase = true) ||
            schema.schemaReferenceCode.contains(searchQuery, ignoreCase = true) ||
            schema.ecuPinoutOrSpecs.contains(searchQuery, ignoreCase = true)
          matchesCategory && matchesQuery
        }

        items(filteredSchematics, key = { it.id }) { schema ->
          CachedTechnicalSchematicCard(
            schema = schema,
            onTogglePin = { onTogglePinSchematic(schema.id) },
            onRemove = { onRemoveSchematic(schema.id) }
          )
        }
      }
    }
  }
}

@Composable
private fun ClientFaultHistoryCard(
  fault: ClientFaultHistoryEntity,
  onUpdateStatus: (String) -> Unit,
  onDelete: () -> Unit
) {
  var isDetailsExpanded by remember { mutableStateOf(false) }

  val statusColor = when (fault.status) {
    "PANNE_RESOLUE" -> MekanikValidationGreen
    "EN_DIAGNOSTIC" -> MekanikBrandOrange
    else -> MekanikErrorRed
  }
  val statusLabel = when (fault.status) {
    "PANNE_RESOLUE" -> "PANNE RÉSOLUE"
    "EN_DIAGNOSTIC" -> "EN DIAGNOSTIC"
    else -> "À SURVEILLER"
  }

  Surface(
    shape = RoundedCornerShape(14.dp),
    color = MekanikCardSurface,
    border = BorderStroke(1.dp, MetalBorder),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("client_fault_card_${fault.id}")
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Header : Client + Matricule + Statut
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(MekanikBgDark),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.Person,
              contentDescription = null,
              tint = MekanikBrandOrange,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "${fault.clientName} • ${fault.licensePlate}",
              color = PureWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 13.5.sp
            )
            Text(
              text = "${fault.vehicleModel} • ${fault.mileageKm} km • ${fault.recordedDate}",
              color = AluminumGray,
              fontSize = 11.sp
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = statusColor.copy(alpha = 0.16f),
          border = BorderStroke(1.dp, statusColor),
          modifier = Modifier.clickable {
            val nextStatus = when (fault.status) {
              "EN_DIAGNOSTIC" -> "PANNE_RESOLUE"
              "PANNE_RESOLUE" -> "A_SURVEILLER"
              else -> "EN_DIAGNOSTIC"
            }
            onUpdateStatus(nextStatus)
          }
        ) {
          Text(
            text = statusLabel,
            color = statusColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      // Titre de la panne + Codes DTC
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MekanikBgDark,
        border = BorderStroke(1.dp, MetalBorder),
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
              text = "Panne : ${fault.faultTitle}",
              color = PureWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 12.5.sp,
              modifier = Modifier.weight(1f)
            )
            if (fault.linkedDtcCodes.isNotBlank()) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MekanikBrandOrange.copy(alpha = 0.18f),
                border = BorderStroke(1.dp, MekanikBrandOrange)
              ) {
                Text(
                  text = fault.linkedDtcCodes,
                  color = MekanikBrandOrange,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.5.sp,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
          Text(
            text = fault.customerReportedSymptoms,
            color = OffWhite,
            fontSize = 11.5.sp,
            lineHeight = 16.sp
          )
        }
      }

      // Barre inférieure : Coût + Bouton Détails du Diagnostic & Réparation
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Intervention : ${fault.totalCostDzd} DA • ${fault.technicianName}",
          color = MekanikValidationGreen,
          fontWeight = FontWeight.SemiBold,
          fontSize = 11.5.sp
        )

        TextButton(
          onClick = { isDetailsExpanded = !isDetailsExpanded }
        ) {
          Text(
            text = if (isDetailsExpanded) "Masquer le diagnostic" else "Voir diagnostic & pièces",
            color = MekanikBrandOrange,
            fontWeight = FontWeight.Bold,
            fontSize = 11.5.sp
          )
          Icon(
            imageVector = if (isDetailsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null,
            tint = MekanikBrandOrange,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      AnimatedVisibility(visible = isDetailsExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          HorizontalDivider(color = MetalBorder)
          Text(
            text = "Diagnostic retenu : ${fault.diagnosticConclusion}",
            color = PureWhite,
            fontSize = 11.5.sp
          )
          Text(
            text = "Travaux effectués : ${fault.repairActionTaken}",
            color = OffWhite,
            fontSize = 11.5.sp
          )
          Text(
            text = "Pièces remplacées : ${fault.partsReplaced}",
            color = MekanikBrandOrange,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.5.sp
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Tél client : ${fault.clientPhone} • VIN : ${fault.vin}",
              color = AluminumGray,
              fontSize = 10.5.sp
            )
            TextButton(onClick = onDelete) {
              Text("Supprimer", color = MekanikErrorRed, fontSize = 11.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SavedDiagnosticRecordCard(
  diag: SavedDiagnosticRecordEntity,
  onDelete: () -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }
  val severityColor = when {
    diag.severityLevel.contains("CRITIQUE", ignoreCase = true) -> MekanikErrorRed
    diag.severityLevel.contains("RÉSOLU", ignoreCase = true) -> MekanikValidationGreen
    else -> MekanikBrandOrange
  }

  Surface(
    shape = RoundedCornerShape(14.dp),
    color = MekanikCardSurface,
    border = BorderStroke(1.dp, MetalBorder),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("saved_diagnostic_card_${diag.id}")
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MekanikBrandOrange.copy(alpha = 0.18f),
            border = BorderStroke(1.dp, MekanikBrandOrange)
          ) {
            Text(
              text = diag.dtcCode,
              color = MekanikBrandOrange,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              fontSize = 12.5.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = diag.diagnosticTitle,
              color = PureWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "${diag.vehicleModel} • ${diag.clientName} (${diag.licensePlate})",
              color = AluminumGray,
              fontSize = 11.sp
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = severityColor.copy(alpha = 0.16f),
          border = BorderStroke(1.dp, severityColor)
        ) {
          Text(
            text = diag.severityLevel,
            color = severityColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
          )
        }
      }

      Text(
        text = diag.plainExplanation,
        color = OffWhite,
        fontSize = 12.sp,
        lineHeight = 16.sp
      )

      // Valeurs Multimètre mises en évidence
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = MekanikBgDark,
        border = BorderStroke(1.dp, MetalBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            Icons.Default.ElectricMeter,
            contentDescription = null,
            tint = MekanikValidationGreen,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = diag.multimeterSpecs,
            color = PureWhite,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${diag.formattedDate} • ${if (diag.isSyncedToCloud) "Synchronisé Cloud + Cache 0ms" else "Cache Local SQLite (En attente Sync)"}",
          color = if (diag.isSyncedToCloud) MekanikValidationGreen else MekanikBrandOrange,
          fontSize = 10.5.sp,
          fontWeight = FontWeight.SemiBold
        )

        TextButton(onClick = { isExpanded = !isExpanded }) {
          Text(
            text = if (isExpanded) "Réduire" else "Étapes & Pièces OEM",
            color = MekanikBrandOrange,
            fontWeight = FontWeight.Bold,
            fontSize = 11.5.sp
          )
          Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null,
            tint = MekanikBrandOrange,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          HorizontalDivider(color = MetalBorder)
          Text(
            text = "CAUSES PROBABLES :",
            color = MekanikBrandOrange,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
          diag.rootCausesJoined.split("\n").filter { it.isNotBlank() }.forEach { cause ->
            Text(text = "• $cause", color = OffWhite, fontSize = 11.5.sp)
          }

          Text(
            text = "PROCÉDURE DE CONTRÔLE PAS À PAS :",
            color = MekanikValidationGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
          diag.inspectionStepsJoined.split("\n").filter { it.isNotBlank() }.forEach { step ->
            Text(text = step, color = OffWhite, fontSize = 11.5.sp)
          }

          if (diag.recommendedPartsJoined.isNotBlank()) {
            Text(
              text = "PIÈCES OEM RECOMMANDÉES :",
              color = PureWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
            diag.recommendedPartsJoined.split("\n").filter { it.isNotBlank() }.forEach { part ->
              Text(text = "• $part", color = MekanikBrandOrange, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            TextButton(onClick = onDelete) {
              Text("Supprimer du cache", color = MekanikErrorRed, fontSize = 11.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CachedTechnicalSchematicCard(
  schema: CachedTechnicalSchematicEntity,
  onTogglePin: () -> Unit,
  onRemove: () -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }

  Surface(
    shape = RoundedCornerShape(14.dp),
    color = MekanikCardSurface,
    border = BorderStroke(1.dp, if (schema.isPinnedOffline) MekanikValidationGreen.copy(alpha = 0.6f) else MetalBorder),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("cached_schematic_card_${schema.id}")
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MekanikValidationGreen.copy(alpha = 0.16f),
              border = BorderStroke(1.dp, MekanikValidationGreen)
            ) {
              Text(
                text = "CACHE 0ms • ${schema.payloadSizeKb} Ko",
                color = MekanikValidationGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 9.5.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = schema.schemaReferenceCode,
              color = AluminumGray,
              fontFamily = FontFamily.Monospace,
              fontSize = 10.5.sp
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = schema.title,
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp
          )
          Text(
            text = "${schema.vehicleBrand} ${schema.vehicleModel} • Moteur ${schema.engineCode}",
            color = MekanikBrandOrange,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.5.sp
          )
        }

        IconButton(
          onClick = onTogglePin,
          modifier = Modifier.testTag("btn_pin_schematic_${schema.id}")
        ) {
          Icon(
            imageVector = if (schema.isPinnedOffline) Icons.Default.PushPin else Icons.Default.OfflinePin,
            contentDescription = "Épingler hors-ligne",
            tint = if (schema.isPinnedOffline) MekanikValidationGreen else AluminumGray
          )
        }
      }

      // Résumé Brochage ECU / Spécifications
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MekanikBgDark,
        border = BorderStroke(1.dp, MetalBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = schema.ecuPinoutOrSpecs,
          color = PureWhite,
          fontFamily = FontFamily.Monospace,
          fontSize = 11.sp,
          lineHeight = 16.sp,
          modifier = Modifier.padding(10.dp)
        )
      }

      Text(
        text = "📍 ${schema.fuseBoxAndRelays}",
        color = AluminumGray,
        fontSize = 11.sp
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Couples : ${schema.torqueSpecsSummary}",
          color = OffWhite,
          fontSize = 11.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )

        TextButton(onClick = { isExpanded = !isExpanded }) {
          Text(
            text = if (isExpanded) "Masquer détails" else "Ouvrir Schéma Hors-Ligne",
            color = MekanikBrandOrange,
            fontWeight = FontWeight.Bold,
            fontSize = 11.5.sp
          )
          Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null,
            tint = MekanikBrandOrange,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          HorizontalDivider(color = MetalBorder)
          Text(
            text = "FAISCEAU / ÉTAPES TECHNIQUES COMPLÈTES :",
            color = MekanikBrandOrange,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
          schema.wireColorsOrSteps.split("\n").filter { it.isNotBlank() }.forEach { line ->
            Text(
              text = line,
              color = PureWhite,
              fontSize = 11.5.sp,
              lineHeight = 16.sp
            )
          }

          Text(
            text = "Outillage requis : ${schema.requiredToolsSummary}",
            color = OffWhite,
            fontSize = 11.sp
          )

          if (schema.warningNotes.isNotBlank()) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MekanikErrorRed.copy(alpha = 0.14f),
              border = BorderStroke(1.dp, MekanikErrorRed),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "⚠️ ${schema.warningNotes}",
                color = PureWhite,
                fontSize = 11.sp,
                modifier = Modifier.padding(8.dp)
              )
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            TextButton(onClick = onRemove) {
              Text("Retirer du cache", color = MekanikErrorRed, fontSize = 11.sp)
            }
          }
        }
      }
    }
  }
}
