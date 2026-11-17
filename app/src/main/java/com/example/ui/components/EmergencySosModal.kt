package com.example.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BreakdownType
import com.example.data.model.SosEmergencyRequest
import com.example.data.model.TowTruckService
import com.example.ui.theme.*

@Composable
fun EmergencySosModal(
  activeRequest: SosEmergencyRequest?,
  towTrucks: List<TowTruckService>,
  selectedType: BreakdownType,
  onTypeSelected: (BreakdownType) -> Unit,
  onTriggerSos: (BreakdownType) -> Unit,
  onSelectTowTruck: (TowTruckService) -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .background(GraphiteDark)
        .testTag("sos_emergency_dialog"),
      color = GraphiteDark
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PerformanceRedDark),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Emergency, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "SOS DÉPANNAGE & REMORQUAGE",
                style = MaterialTheme.typography.titleMedium.copy(
                  color = PureWhite,
                  fontWeight = FontWeight.Black
                )
              )
              Text(
                text = "Assistance 24/7 & Dépanneuses à Proximité",
                style = MaterialTheme.typography.bodySmall.copy(color = PerformanceRedGlow, fontSize = 11.sp)
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(MetalCard)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // GPS Location & Vehicle Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = PerformanceRed, modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Position GPS Détectée",
                style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted)
              )
              Text(
                text = activeRequest?.gpsLocationString ?: "Autoroute de l'Est A1, km 42 (Alger Ouest)",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite)
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = ElectricGreen.copy(alpha = 0.2f)
            ) {
              Text(
                text = "GPS Fixé",
                style = MaterialTheme.typography.labelSmall.copy(color = ElectricGreen, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Breakdown Type Selector Chips
        Text(
          text = "TYPE DE PANNE",
          style = MaterialTheme.typography.labelMedium.copy(color = AluminumMuted, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          BreakdownType.values().forEach { type ->
            val isSelected = type == selectedType
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) PerformanceRed else MetalCard,
              border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
              modifier = Modifier.clickable { onTypeSelected(type) }
            ) {
              Text(
                text = type.label,
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (isSelected) PureWhite else AluminumLight,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SOS Big Button
        Button(
          onClick = { onTriggerSos(selectedType) },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("btn_trigger_sos"),
          colors = ButtonDefaults.buttonColors(
            containerColor = PerformanceRed,
            contentColor = PureWhite
          ),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "LANCER L'APPEL D'URGENCE SOS",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "DÉPANNEUSES DISPONIBLES À PROXIMITÉ",
          style = MaterialTheme.typography.labelMedium.copy(color = AluminumMuted, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Tow trucks list
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(towTrucks) { truck ->
            val isSelected = activeRequest?.selectedTowTruck?.id == truck.id
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectTowTruck(truck) },
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, if (isSelected) PerformanceRed else MetalBorder)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(
                      text = truck.companyName,
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                    )
                    Text(
                      text = "${truck.wilayaLocation} • ${truck.truckType}",
                      style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 11.sp)
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PerformanceRedDark.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, PerformanceRed)
                  ) {
                    Text(
                      text = "${truck.baseRateDzd.toInt()} DA",
                      style = MaterialTheme.typography.labelMedium.copy(
                        color = PureWhite,
                        fontWeight = FontWeight.Black
                      ),
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "Arrivée ~${truck.estimatedArrivalMinutes} min (${truck.distanceKm} km)",
                      style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontWeight = FontWeight.Bold)
                    )
                  }

                  Button(
                    onClick = { onSelectTowTruck(truck) },
                    colors = ButtonDefaults.buttonColors(
                      containerColor = if (isSelected) ElectricGreen else MetalSurface,
                      contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                  ) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (isSelected) "En Route" else "Appeler",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
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
