package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CourtesyLoanerVehicle
import com.example.data.model.PreCtCheckStatus
import com.example.data.model.PreCtInspectionPoint
import com.example.ui.theme.*

@Composable
fun PreTechnicalInspectionCourtesyModal(
  preCtPoints: List<PreCtInspectionPoint>,
  courtesyVehicles: List<CourtesyLoanerVehicle>,
  onFixPreCtPoint: (pointId: String) -> Unit,
  onToggleCourtesyVehicleLoan: (vehicleId: String) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Pré-Contrôle Technique, 1 = Véhicules de Courtoisie

  val majorDefectsCount = remember(preCtPoints) {
    preCtPoints.count { it.status == PreCtCheckStatus.CONTRE_VISITE_MAJEURE }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)
        .testTag("modal_pre_technical_inspection_courtesy"),
      shape = RoundedCornerShape(16.dp),
      color = GraphiteBlack,
      border = BorderStroke(1.dp, TechCyan)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(MetalCard)
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(TechCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.FactCheck, contentDescription = null, tint = TechCyan, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "PRÉ-CONTRÔLE TECHNIQUE & VÉHICULES DE COURTOISIE",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
              )
              Text(
                text = "Grille Zéro Contre-Visite (Pollution/Freins/Phares) & Prêt Véhicules Atelier",
                color = AluminumLight,
                fontSize = 10.sp
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite)
          }
        }

        // Tabs
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(MetalSurface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (selectedTab == 0) TechCyanDark else GraphiteBlack,
            border = BorderStroke(1.dp, if (selectedTab == 0) TechCyan else MetalBorder),
            modifier = Modifier
              .weight(1f)
              .clickable { selectedTab = 0 }
          ) {
            Row(
              modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.ChecklistRtl, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("PRÉ-CONTRÔLE TECHNIQUE", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
            }
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (selectedTab == 1) MechanicalOrangeDark else GraphiteBlack,
            border = BorderStroke(1.dp, if (selectedTab == 1) MechanicalOrange else MetalBorder),
            modifier = Modifier
              .weight(1f)
              .clickable { selectedTab = 1 }
          ) {
            Row(
              modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.CarRental, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("PRÊT COURTOISIE (${courtesyVehicles.size})", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
            }
          }
        }

        if (selectedTab == 0) {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
          ) {
            item {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (majorDefectsCount == 0) Color(0xFF0E2A1C) else Color(0xFF2D1414),
                border = BorderStroke(1.dp, if (majorDefectsCount == 0) ElectricGreen else PerformanceRedGlow),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = if (majorDefectsCount == 0) "✅ PRÊT POUR LE CONTRÔLE TECHNIQUE OFFICIEL (0 CONTRE-VISITE)"
                      else "⚠️ $majorDefectsCount DÉFAUT(S) ÉLIMINATOIRE(S) DÉTECTÉ(S) (CONTRE-VISITE)",
                      color = if (majorDefectsCount == 0) ElectricGreen else WarningYellow,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.5.sp
                    )
                    Text(
                      text = "Corrigez les points rouges à l'atelier avant de présenter le véhicule au centre agréé.",
                      color = OffWhite,
                      fontSize = 10.sp
                    )
                  }
                }
              }
            }

            items(preCtPoints, key = { it.id }) { pt ->
              val statusColor = when (pt.status) {
                PreCtCheckStatus.CONFORME -> ElectricGreen
                PreCtCheckStatus.DEFAUT_MINEUR -> WarningYellow
                PreCtCheckStatus.CONTRE_VISITE_MAJEURE -> PerformanceRedGlow
              }
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, statusColor)
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(pt.categoryTitle, color = TechCyan, fontWeight = FontWeight.Black, fontSize = 10.5.sp)
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = statusColor.copy(alpha = 0.2f),
                      border = BorderStroke(1.dp, statusColor)
                    ) {
                      Text(
                        text = pt.status.label,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                      )
                    }
                  }

                  Text(pt.checkPointName, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  Text("Mesure relevée : ${pt.measuredValueText}", color = OffWhite, fontSize = 11.sp)
                  Text("Norme officielle : ${pt.officialThresholdText}", color = AluminumLight, fontSize = 10.sp)

                  if (pt.status != PreCtCheckStatus.CONFORME) {
                    Button(
                      onClick = { onFixPreCtPoint(pt.id) },
                      colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
                      modifier = Modifier.fillMaxWidth(),
                      contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                      Icon(Icons.Default.BuildCircle, contentDescription = null, tint = GraphiteBlack, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "CORRIGER EN ATELIER (${pt.correctiveActionDzd} DA) & VALIDER CONFORME",
                        color = GraphiteBlack,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.5.sp
                      )
                    }
                  }
                }
              }
            }
          }
        } else {
          // Tab 1: Courtesy Loaner Vehicles
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
          ) {
            items(courtesyVehicles, key = { it.id }) { car ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, if (car.isCurrentlyLoaned) MechanicalOrange else ElectricGreen)
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(car.vehicleModel, color = PureWhite, fontWeight = FontWeight.Black, fontSize = 12.5.sp)
                      Text("Immatriculation : ${car.licensePlate} • Carburant : ${car.fuelLevelEighths}/8", color = AluminumLight, fontSize = 10.5.sp)
                    }
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = if (car.isCurrentlyLoaned) MechanicalOrangeDark else Color(0xFF1B5E20)
                    ) {
                      Text(
                        text = if (car.isCurrentlyLoaned) "EN PRÊT CLIENT" else "DISPONIBLE ATELIER",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                      )
                    }
                  }

                  if (car.isCurrentlyLoaned) {
                    Text("👤 Client : ${car.assignedClientName} (${car.linkedRepairOrderNumber})", color = WarningYellow, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text("📅 Retour prévu : ${car.expectedReturnDate}", color = OffWhite, fontSize = 10.5.sp)
                  }

                  Button(
                    onClick = { onToggleCourtesyVehicleLoan(car.id) },
                    colors = ButtonDefaults.buttonColors(
                      containerColor = if (car.isCurrentlyLoaned) TechCyanDark else MechanicalOrange
                    ),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Icon(
                      if (car.isCurrentlyLoaned) Icons.Default.AssignmentReturn else Icons.Default.Key,
                      contentDescription = null,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (car.isCurrentlyLoaned) "ENREGISTRER RETOUR DU VÉHICULE DE COURTOISIE"
                      else "ATTRIBUER EN PRÊT CLIENT (DOSSIER OR EN COURS)",
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.5.sp
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
