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
import com.example.data.model.AcServiceChargeRecord
import com.example.data.model.AcVehicleSpecification
import com.example.data.model.RefrigerantGasType
import com.example.ui.theme.*

@Composable
fun AcRefrigerantLogModal(
  vehicleSpecs: List<AcVehicleSpecification>,
  serviceLogs: List<AcServiceChargeRecord>,
  onLogAcChargeFromSpec: (AcVehicleSpecification, String, Int, Double) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Base de Données Charges A/C, 1 = Registre Traçabilité Gaz
  var plateInput by remember { mutableStateOf("09412-123-16") }
  var recoveredGramsInput by remember { mutableStateOf("240") }
  var ventTempInput by remember { mutableStateOf("5.1") }

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
      border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.7f))
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
                .background(TechCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.AcUnit, contentDescription = null, tint = TechCyan, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "REGISTRE CLIMATISATION A/C (R134a / R1234yf)",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 13.5.sp
              )
              Text(
                text = "Quantités exactes gaz/huile PAG & Traçabilité récupération/charge",
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
            color = if (selectedTab == 0) TechCyan.copy(alpha = 0.22f) else MetalSurface,
            border = BorderStroke(1.dp, if (selectedTab == 0) TechCyan else MetalBorder),
            modifier = Modifier
              .weight(1f)
              .clickable { selectedTab = 0 }
          ) {
            Row(
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.MenuBook, contentDescription = null, tint = if (selectedTab == 0) TechCyan else AluminumGray, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "TABLES DE CHARGE (${vehicleSpecs.size})",
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
              Icon(Icons.Default.FactCheck, contentDescription = null, tint = if (selectedTab == 1) ElectricGreen else AluminumGray, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "REGISTRE INTERVENTIONS (${serviceLogs.size})",
                color = if (selectedTab == 1) PureWhite else AluminumGray,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }
        }

        if (selectedTab == 0) {
          // Quick parameters for logging a charge
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = plateInput,
              onValueChange = { plateInput = it },
              label = { Text("Immat. Client", fontSize = 10.sp) },
              singleLine = true,
              modifier = Modifier.weight(1.2f)
            )
            OutlinedTextField(
              value = recoveredGramsInput,
              onValueChange = { recoveredGramsInput = it },
              label = { Text("Gaz récupéré (g)", fontSize = 10.sp) },
              singleLine = true,
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = ventTempInput,
              onValueChange = { ventTempInput = it },
              label = { Text("T° Soufflage °C", fontSize = 10.sp) },
              singleLine = true,
              modifier = Modifier.weight(1f)
            )
          }

          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(vehicleSpecs, key = { it.id }) { spec ->
              val isR1234yf = spec.gasType == RefrigerantGasType.R1234YF
              val gasBadgeColor = if (isR1234yf) ElectricGreen else TechCyan

              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, gasBadgeColor.copy(alpha = 0.6f))
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(spec.vehicleModel, color = PureWhite, fontWeight = FontWeight.Black, fontSize = 13.sp)
                      Text("Années : ${spec.productionYears}", color = AluminumGray, fontSize = 10.5.sp)
                    }
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = gasBadgeColor.copy(alpha = 0.2f),
                      border = BorderStroke(1.dp, gasBadgeColor)
                    ) {
                      Text(
                        text = "${spec.gasType.code} • ${spec.targetGasWeightGrams} g",
                        color = gasBadgeColor,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                      )
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, MetalBorder),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                      Text(
                        text = "• Huile Compresseur : ${spec.compressorOilType} (Capacité totale : ${spec.totalOilCapacityMl} ml)",
                        color = PureWhite,
                        fontSize = 10.5.sp
                      )
                      Text(
                        text = "• Pression BP nominale : ${spec.lowPressureNominalBar} | HP nominale : ${spec.highPressureNominalBar}",
                        color = TechCyan,
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace
                      )
                    }
                  }

                  Button(
                    onClick = {
                      val rec = recoveredGramsInput.toIntOrNull() ?: 220
                      val temp = ventTempInput.toDoubleOrNull() ?: 5.2
                      onLogAcChargeFromSpec(spec, plateInput, rec, temp)
                      selectedTab = 1
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = GraphiteBlack, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "ENREGISTRER CYCLE CHARGE (${spec.targetGasWeightGrams}g ${spec.gasType.code})",
                      color = GraphiteBlack,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.sp
                    )
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(serviceLogs, key = { it.id }) { log ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.6f))
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "${log.vehicleModel} (${log.licensePlate})",
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 12.5.sp
                    )
                    Text(log.dateFormatted, color = AluminumGray, fontSize = 10.sp)
                  }

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Surface(shape = RoundedCornerShape(6.dp), color = GraphiteBlack, modifier = Modifier.weight(1f)) {
                      Column(modifier = Modifier.padding(8.dp)) {
                        Text("GAZ RÉCUPÉRÉ / INJECTÉ", color = AluminumGray, fontSize = 9.sp)
                        Text(
                          text = "${log.recoveredGrams}g → ${log.injectedGrams}g (${log.gasType.code})",
                          color = TechCyan,
                          fontWeight = FontWeight.Bold,
                          fontSize = 11.sp
                        )
                      }
                    }

                    Surface(shape = RoundedCornerShape(6.dp), color = GraphiteBlack, modifier = Modifier.weight(1f)) {
                      Column(modifier = Modifier.padding(8.dp)) {
                        Text("SOUFFLAGE & VIDE", color = AluminumGray, fontSize = 9.sp)
                        Text(
                          text = "${log.ventOutputTempCelsius} °C • Vide OK",
                          color = ElectricGreen,
                          fontWeight = FontWeight.Bold,
                          fontSize = 11.sp
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
}
