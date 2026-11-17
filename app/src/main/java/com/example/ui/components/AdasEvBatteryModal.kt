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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AdasCalibrationRecord
import com.example.data.model.EvBatteryHealthReport
import com.example.ui.theme.*

@Composable
fun AdasEvBatteryModal(
  adasCalibrations: List<AdasCalibrationRecord>,
  evBatteryReports: List<EvBatteryHealthReport>,
  onRunAdasCalibration: (String) -> Unit,
  onToggleEvHighVoltageLockout: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Calibrage ADAS, 1 = Santé Batterie EV/Hybride (SoH)

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .fillMaxHeight(0.90f),
      shape = RoundedCornerShape(18.dp),
      color = GraphiteDark,
      border = BorderStroke(1.dp, EvBlue.copy(alpha = 0.7f))
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(MetalCard)
            .padding(horizontal = 16.dp, vertical = 14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(EvBlue.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.ElectricCar, contentDescription = null, tint = EvBlue, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "CALIBRAGE ADAS & BILAN SANTÉ BATTERIE EV / HYBRIDE",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
              )
              Text(
                text = "Cibles optiques caméras/radars & Diagnostic Haute Tension SoH (400V/800V)",
                color = AluminumGray,
                fontSize = 10.5.sp
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight)
          }
        }

        // Tabs
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (selectedTab == 0) EvBlue.copy(alpha = 0.22f) else MetalSurface,
            border = BorderStroke(1.dp, if (selectedTab == 0) EvBlue else MetalBorder),
            modifier = Modifier
              .weight(1f)
              .clickable { selectedTab = 0 }
          ) {
            Row(
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.CenterFocusStrong, contentDescription = null, tint = if (selectedTab == 0) EvBlue else AluminumGray, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "CALIBRAGE ADAS (${adasCalibrations.size})",
                color = if (selectedTab == 0) PureWhite else AluminumGray,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (selectedTab == 1) ElectricGreen.copy(alpha = 0.22f) else MetalSurface,
            border = BorderStroke(1.dp, if (selectedTab == 1) ElectricGreen else MetalBorder),
            modifier = Modifier
              .weight(1f)
              .clickable { selectedTab = 1 }
          ) {
            Row(
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = if (selectedTab == 1) ElectricGreen else AluminumGray, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "SANTÉ BATTERIE SoH (${evBatteryReports.size})",
                color = if (selectedTab == 1) PureWhite else AluminumGray,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }
        }

        if (selectedTab == 0) {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(adasCalibrations, key = { it.id }) { adas ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, if (adas.isCalibrated) ElectricGreen.copy(alpha = 0.6f) else WarningYellow)
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = adas.vehicleModel,
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 13.sp
                    )
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = if (adas.isCalibrated) ElectricGreen.copy(alpha = 0.2f) else WarningYellow.copy(alpha = 0.2f)
                    ) {
                      Text(
                        text = if (adas.isCalibrated) "CALIBRÉ CONFORME OEM" else "À RECALIBRER",
                        color = if (adas.isCalibrated) ElectricGreen else WarningYellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                      )
                    }
                  }

                  Text(
                    text = adas.systemType.label,
                    color = EvBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                  )

                  Text(
                    text = "Motif : ${adas.triggerReason}",
                    color = AluminumLight,
                    fontSize = 11.sp
                  )

                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, MetalBorder),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                      Text("• Mode : ${adas.calibrationMode}", color = PureWhite, fontSize = 10.5.sp)
                      Text("• Cible & Mesures : ${adas.targetBoardCode}", color = TechCyan, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace)
                      Text(
                        text = "• Écart angulaire : Avant ${adas.angleDeviationBeforeDeg}° → Après ${adas.angleDeviationAfterDeg}°",
                        color = if (adas.isCalibrated) ElectricGreen else WarningYellow,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }

                  if (!adas.isCalibrated) {
                    Button(
                      onClick = { onRunAdasCalibration(adas.id) },
                      colors = ButtonDefaults.buttonColors(containerColor = EvBlue),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Icon(Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("LANCER L'ÉTALONNAGE LASER & VALIDER CERTIFICAT ADAS", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                  }
                }
              }
            }
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            items(evBatteryReports, key = { it.id }) { report ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.6f))
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text(report.vehicleModel, color = PureWhite, fontWeight = FontWeight.Black, fontSize = 13.sp)
                      Text("VIN : ${report.vin} • ${report.batteryChemistry}", color = AluminumGray, fontSize = 10.5.sp)
                    }
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = ElectricGreen.copy(alpha = 0.2f),
                      border = BorderStroke(1.dp, ElectricGreen)
                    ) {
                      Text(
                        text = "SoH ${report.stateOfHealthPercent}%",
                        color = ElectricGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                      )
                    }
                  }

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = GraphiteBlack,
                      modifier = Modifier.weight(1f)
                    ) {
                      Column(modifier = Modifier.padding(8.dp)) {
                        Text("ÉQUILIBRAGE CELLULES", color = AluminumGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                          text = "Δ ${report.cellDeltaMv} mV",
                          color = if (report.cellDeltaMv <= 25) ElectricGreen else WarningYellow,
                          fontWeight = FontWeight.Black,
                          fontSize = 13.sp
                        )
                        Text("${report.cellMinVoltageMv} - ${report.cellMaxVoltageMv} mV", color = AluminumLight, fontSize = 9.5.sp)
                      }
                    }

                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = GraphiteBlack,
                      modifier = Modifier.weight(1f)
                    ) {
                      Column(modifier = Modifier.padding(8.dp)) {
                        Text("AUTONOMIE RÉELLE", color = AluminumGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                          text = "${report.estimatedRealRangeKm} km",
                          color = TechCyan,
                          fontWeight = FontWeight.Black,
                          fontSize = 13.sp
                        )
                        Text("Origine WLTP : ${report.wltpOriginalRangeKm} km", color = AluminumLight, fontSize = 9.5.sp)
                      }
                    }
                  }

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                        if (report.highVoltageLockoutActive) Icons.Default.Lock else Icons.Default.Bolt,
                        contentDescription = null,
                        tint = if (report.highVoltageLockoutActive) ElectricGreen else WarningYellow,
                        modifier = Modifier.size(18.dp)
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = if (report.highVoltageLockoutActive)
                          "CONSIGNATION HAUTE TENSION ACTIVE (0V SÉCURISÉ)"
                        else
                          "CIRCUIT HAUTE TENSION SOUS TENSION (DANGER 400V)",
                        color = if (report.highVoltageLockoutActive) ElectricGreen else WarningYellow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }

                    OutlinedButton(
                      onClick = { onToggleEvHighVoltageLockout(report.id) },
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                      border = BorderStroke(1.dp, if (report.highVoltageLockoutActive) WarningYellow else ElectricGreen)
                    ) {
                      Text(
                        text = if (report.highVoltageLockoutActive) "Déconsigner" else "Consigner HT",
                        color = PureWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
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
}
