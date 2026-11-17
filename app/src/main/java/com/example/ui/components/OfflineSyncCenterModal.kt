package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.OfflineCachedPack
import com.example.data.model.PendingSyncAction
import com.example.ui.theme.*

@Composable
fun OfflineSyncCenterModal(
  isOfflineModeEnabled: Boolean,
  pendingSyncActions: List<PendingSyncAction>,
  cachedPacks: List<OfflineCachedPack>,
  onToggleOfflineMode: (Boolean) -> Unit,
  onSyncAllPendingNow: () -> Unit,
  onDownloadOfflinePack: (String) -> Unit,
  onDismiss: () -> Unit
) {
  val unsyncedCount = pendingSyncActions.count { !it.isSynced }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)
        .testTag("offline_sync_center_modal"),
      shape = RoundedCornerShape(18.dp),
      color = GraphiteDark,
      border = BorderStroke(1.dp, if (isOfflineModeEnabled) MechanicalOrange else ElectricGreen)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(MetalCard)
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isOfflineModeEnabled) MechanicalOrange.copy(alpha = 0.2f) else ElectricGreen.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                if (isOfflineModeEnabled) Icons.Default.CloudOff else Icons.Default.CloudDone,
                contentDescription = null,
                tint = if (isOfflineModeEnabled) MechanicalOrange else ElectricGreen
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "MODE HORS-LIGNE & SYNCHRONISATION AUTO",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
              )
              Text(
                text = "Continuité atelier sous-sol / zone blanche & cache Room SQLite",
                color = AluminumLight,
                fontSize = 11.sp
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite)
          }
        }

        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 14.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          contentPadding = PaddingValues(vertical = 14.dp)
        ) {
          // Toggle Switch Card
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, if (isOfflineModeEnabled) MechanicalOrange else ElectricGreen)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = if (isOfflineModeEnabled) "MODE HORS-LIGNE ACTIF (SOUS-SOL / SANS RÉSEAU)" else "CONNECTÉ AU CLOUD MEKANIK+ (SYNCHRO AUTO)",
                    color = if (isOfflineModeEnabled) MechanicalOrange else ElectricGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.5.sp
                  )
                  Text(
                    text = "Toutes les inspections 360°, devis et scans de stock sont enregistrés localement sans perte.",
                    color = AluminumLight,
                    fontSize = 10.5.sp
                  )
                }
                Switch(
                  checked = isOfflineModeEnabled,
                  onCheckedChange = onToggleOfflineMode,
                  colors = SwitchDefaults.colors(
                    checkedThumbColor = PureWhite,
                    checkedTrackColor = MechanicalOrange
                  )
                )
              }
            }
          }

          // Pending Queue Section
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "🔄 FILE D'ATTENTE DE SYNCHRONISATION ($unsyncedCount)",
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                  )
                  if (unsyncedCount > 0) {
                    Button(
                      onClick = onSyncAllPendingNow,
                      colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen, contentColor = GraphiteBlack),
                      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                      Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Synchroniser Tout", fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }
                  }
                }

                pendingSyncActions.forEach { action ->
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MetalCard,
                    border = BorderStroke(1.dp, if (action.isSynced) ElectricGreen.copy(alpha = 0.5f) else MechanicalOrange.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(action.title, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text(
                          text = "${action.type.label} • ${action.timestamp} (${action.payloadSizeKb} Ko)",
                          color = AluminumGray,
                          fontSize = 10.sp
                        )
                      }
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (action.isSynced) ElectricGreen.copy(alpha = 0.2f) else MechanicalOrange.copy(alpha = 0.2f)
                      ) {
                        Text(
                          text = if (action.isSynced) "SYNCHRONISÉ" else "EN ATTENTE",
                          color = if (action.isSynced) ElectricGreen else MechanicalOrange,
                          fontWeight = FontWeight.Black,
                          fontSize = 9.sp,
                          modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          // Local Cached Technical Packs
          item {
            Text(
              text = "📦 BASES TECHNIQUES TÉLÉCHARGÉES SUR LE TÉLÉPHONE :",
              color = TechCyan,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp
            )
          }

          items(cachedPacks, key = { it.id }) { pack ->
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, if (pack.isDownloaded) TechCyan.copy(alpha = 0.5f) else MetalBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(pack.title, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  Text(
                    text = "${pack.category} • ${pack.itemCount} fiches (${pack.storageSizeMb} Mo) • Maj : ${pack.lastUpdated}",
                    color = AluminumLight,
                    fontSize = 10.sp
                  )
                }
                if (pack.isDownloaded) {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ElectricGreen.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, ElectricGreen)
                  ) {
                    Text(
                      text = "PRÊT HORS-LIGNE",
                      color = ElectricGreen,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.sp,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                } else {
                  Button(
                    onClick = { onDownloadOfflinePack(pack.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = TechCyan, contentColor = GraphiteBlack),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Télécharger", fontWeight = FontWeight.Black, fontSize = 10.5.sp)
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
