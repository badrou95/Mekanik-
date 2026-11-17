package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.model.WaitingQueueVehicle
import com.example.data.model.WorkshopBayItem
import com.example.data.model.WorkshopBayStatus
import com.example.ui.theme.*

@Composable
fun WorkshopBayPlannerModal(
  bays: List<WorkshopBayItem>,
  waitingVehicles: List<WaitingQueueVehicle>,
  onToggleBayTimer: (String) -> Unit,
  onCompleteBayOperation: (String) -> Unit,
  onAssignVehicleToBay: (String, String) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedWaitingVehicle by remember { mutableStateOf<WaitingQueueVehicle?>(waitingVehicles.firstOrNull()) }

  val occupiedCount = bays.count { it.status != WorkshopBayStatus.LIBRE }
  val occupancyPercent = if (bays.isNotEmpty()) (occupiedCount * 100) / bays.size else 0

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)
        .testTag("workshop_bay_planner_modal"),
      shape = RoundedCornerShape(18.dp),
      color = GraphiteDark,
      border = BorderStroke(1.dp, PerformanceRedGlow)
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
                .background(PerformanceRedDark),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.ViewKanban, contentDescription = null, tint = PureWhite)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "PLANNING VISUEL DES PONTS & CHARGE",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
              )
              Text(
                text = "Kanban temps réel • Pointage chrono mécaniciens",
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
          // Live Load Summary Bar
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text("TAUX D'OCCUPATION ATELIER", color = AluminumGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  Text(
                    text = "$occupiedCount / ${bays.size} Ponts Actifs ($occupancyPercent%)",
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                  )
                  Text(
                    text = "${waitingVehicles.size} véhicule(s) en file d'attente réception",
                    color = MechanicalOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                }
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (occupancyPercent >= 75) MechanicalOrange.copy(alpha = 0.2f) else ElectricGreen.copy(alpha = 0.2f),
                  border = BorderStroke(1.dp, if (occupancyPercent >= 75) MechanicalOrange else ElectricGreen)
                ) {
                  Text(
                    text = if (occupancyPercent >= 75) "CHARGE ÉLEVÉE" else "FLUX OPTIMAL",
                    color = if (occupancyPercent >= 75) MechanicalOrange else ElectricGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                  )
                }
              }
            }
          }

          // Waiting Vehicles Queue to Assign
          if (waitingVehicles.isNotEmpty()) {
            item {
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
                border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.7f))
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "⏳ FILE D'ATTENTE À AFFECTER SUR UN PONT LIBRE",
                      color = MechanicalOrange,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.5.sp
                    )
                    Text(
                      text = "Sélectionnez puis cliquez 'Affecter'",
                      color = AluminumGray,
                      fontSize = 10.sp
                    )
                  }
                  waitingVehicles.forEach { waitVeh ->
                    val isSelected = selectedWaitingVehicle?.id == waitVeh.id
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = if (isSelected) MetalCard else GraphiteDark,
                      border = BorderStroke(1.dp, if (isSelected) MechanicalOrange else MetalBorder),
                      modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedWaitingVehicle = waitVeh }
                    ) {
                      Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Column(modifier = Modifier.weight(1f)) {
                          Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                              shape = RoundedCornerShape(4.dp),
                              color = if (waitVeh.priorityLevel == "URGENT") PerformanceRedDark else TechCyanDark
                            ) {
                              Text(
                                text = waitVeh.priorityLevel,
                                color = PureWhite,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                              )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                              text = "${waitVeh.vehicleModel} (${waitVeh.licensePlate})",
                              color = PureWhite,
                              fontWeight = FontWeight.Bold,
                              fontSize = 12.sp
                            )
                          }
                          Text(
                            text = "${waitVeh.repairOrderNumber} • ${waitVeh.operationTitle} (${waitVeh.estimatedMinutes} min)",
                            color = AluminumLight,
                            fontSize = 10.5.sp
                          )
                        }
                        RadioButton(
                          selected = isSelected,
                          onClick = { selectedWaitingVehicle = waitVeh },
                          colors = RadioButtonDefaults.colors(selectedColor = MechanicalOrange)
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          // Bays Grid / List
          items(bays, key = { it.id }) { bay ->
            val statusColor = Color(bay.status.badgeColorHex)
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, statusColor.copy(alpha = 0.7f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.2f)),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = "#${bay.bayNumber}",
                        color = statusColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                      )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text(bay.bayName, color = PureWhite, fontWeight = FontWeight.Black, fontSize = 13.5.sp)
                      Text(
                        text = "${bay.bayType.label} (${bay.bayType.maxWeightTons}) • ${bay.assignedMechanic}",
                        color = AluminumGray,
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
                      text = bay.status.label.uppercase(),
                      color = statusColor,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.sp,
                      modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                  }
                }

                if (bay.currentVehicleModel != null) {
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, MetalBorder),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Text(
                          text = "🚗 ${bay.currentVehicleModel}",
                          color = PureWhite,
                          fontWeight = FontWeight.Bold,
                          fontSize = 12.sp
                        )
                        Text(
                          text = "${bay.repairOrderNumber ?: ""} • ${bay.currentLicensePlate ?: ""}",
                          color = TechCyan,
                          fontWeight = FontWeight.Bold,
                          fontSize = 11.sp
                        )
                      }
                      Text(
                        text = "🔧 ${bay.operationTitle}",
                        color = AluminumLight,
                        fontSize = 11.sp
                      )

                      // Progress Bar & Timer
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = "Chrono pointé : ${bay.elapsedMinutes} min / ${bay.estimatedMinutes} min prévues",
                          color = if (bay.isTimerRunning) ElectricGreen else AluminumGray,
                          fontSize = 10.5.sp,
                          fontWeight = FontWeight.SemiBold
                        )
                        Text(
                          text = "${(bay.progressRatio * 100).toInt()}%",
                          color = PureWhite,
                          fontWeight = FontWeight.Bold,
                          fontSize = 11.sp
                        )
                      }
                      LinearProgressIndicator(
                        progress = { bay.progressRatio },
                        modifier = Modifier
                          .fillMaxWidth()
                          .height(6.dp)
                          .clip(RoundedCornerShape(3.dp)),
                        color = statusColor,
                        trackColor = MetalBorder
                      )
                    }
                  }

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    OutlinedButton(
                      onClick = { onToggleBayTimer(bay.id) },
                      modifier = Modifier.weight(1f),
                      border = BorderStroke(1.dp, if (bay.isTimerRunning) MechanicalOrange else ElectricGreen),
                      colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite)
                    ) {
                      Icon(
                        if (bay.isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = if (bay.isTimerRunning) MechanicalOrange else ElectricGreen,
                        modifier = Modifier.size(16.dp)
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = if (bay.isTimerRunning) "Pause Chrono" else "Reprendre (+15m)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                    Button(
                      onClick = { onCompleteBayOperation(bay.id) },
                      modifier = Modifier.weight(1f),
                      colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen, contentColor = GraphiteBlack)
                    ) {
                      Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Libérer le Pont", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                  }
                } else {
                  // Pont Libre -> Affecter le véhicule sélectionné en file d'attente
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, MetalBorder),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(12.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text("Pont élévateur libre et prêt", color = ElectricGreen, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        val targetVeh = selectedWaitingVehicle ?: waitingVehicles.firstOrNull()
                        Text(
                          text = if (targetVeh != null) "Prêt à recevoir : ${targetVeh.vehicleModel}" else "Aucun véhicule en attente",
                          color = AluminumLight,
                          fontSize = 10.5.sp
                        )
                      }
                      val targetVeh = selectedWaitingVehicle ?: waitingVehicles.firstOrNull()
                      if (targetVeh != null) {
                        Button(
                          onClick = { onAssignVehicleToBay(bay.id, targetVeh.id) },
                          colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed, contentColor = PureWhite),
                          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                          Icon(Icons.Default.AddTask, contentDescription = null, modifier = Modifier.size(15.dp))
                          Spacer(modifier = Modifier.width(6.dp))
                          Text("Affecter sur ce pont", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
    }
  }
}
