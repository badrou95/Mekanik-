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
import com.example.data.model.BulkOilDrumTank
import com.example.data.model.RepairOrderProfitabilityItem
import com.example.ui.theme.*

@Composable
fun WorkshopProfitabilityEfficiencyModal(
  repairOrders: List<RepairOrderProfitabilityItem>,
  bulkOilDrums: List<BulkOilDrumTank>,
  onDispenseOilFromDrum: (drumId: String, liters: Double) -> Unit,
  onRefillOilDrum: (drumId: String) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Rentabilité OR & Temps Pointés, 1 = Fûts d'Huile Vrac 208L

  val totalNetProfit = remember(repairOrders) { repairOrders.sumOf { it.netProfitMarginDzd } }
  val avgEfficiency = remember(repairOrders) {
    if (repairOrders.isNotEmpty()) repairOrders.map { it.efficiencyPercent }.average().toInt() else 100
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)
        .testTag("modal_workshop_profitability_efficiency"),
      shape = RoundedCornerShape(16.dp),
      color = GraphiteBlack,
      border = BorderStroke(1.dp, ElectricGreen)
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
                .background(ElectricGreen.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.TrendingUp, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "RENTABILITÉ OR, PRODUCTIVITÉ & FÛTS D'HUILE 208L",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
              )
              Text(
                text = "Marge nette Pièces/MO, Temps Pointés vs Barème & Jauges Huile Vrac",
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
            color = if (selectedTab == 0) Color(0xFF1B5E20) else GraphiteBlack,
            border = BorderStroke(1.dp, if (selectedTab == 0) ElectricGreen else MetalBorder),
            modifier = Modifier
              .weight(1f)
              .clickable { selectedTab = 0 }
          ) {
            Row(
              modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Analytics, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("MARGE OR & TEMPS POINTÉS", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
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
              Icon(Icons.Default.OilBarrel, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("FÛTS D'HUILE VRAC 208L (${bulkOilDrums.size})", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
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
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Card(
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.dp, ElectricGreen)
                ) {
                  Column(modifier = Modifier.padding(10.dp)) {
                    Text("MARGE NETTE CUMULÉE", color = AluminumLight, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    Text("$totalNetProfit DA", color = ElectricGreen, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    Text("Pièces + Main d'œuvre HT", color = OffWhite, fontSize = 9.5.sp)
                  }
                }
                Card(
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.dp, TechCyan)
                ) {
                  Column(modifier = Modifier.padding(10.dp)) {
                    Text("EFFICACITÉ HORAIRE MOY.", color = AluminumLight, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    Text("$avgEfficiency %", color = TechCyan, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    Text("Heures Barème / Pointées", color = OffWhite, fontSize = 9.5.sp)
                  }
                }
              }
            }

            items(repairOrders, key = { it.id }) { orItem ->
              val isHighEff = orItem.efficiencyPercent >= 100
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, if (isHighEff) ElectricGreen else WarningYellow)
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text("${orItem.repairOrderNumber} • ${orItem.vehicleAndClient}", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = if (isHighEff) Color(0xFF1B5E20) else MechanicalOrangeDark
                    ) {
                      Text(
                        text = "Productivité : ${orItem.efficiencyPercent}%",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                      )
                    }
                  }

                  Text("👨‍🔧 Mécanicien : ${orItem.mechanicAssigned}", color = TechCyan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("⏱️ Barème facturé : ${orItem.billedHoursBarème} h", color = OffWhite, fontSize = 10.5.sp)
                    Text("⏱️ Pointé réel : ${orItem.clockedHoursReal} h", color = WarningYellow, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                  }

                  HorizontalDivider(color = MetalBorder)

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text("Achat Pièces HT : ${orItem.partsBuyCostHtDzd} DA", color = AluminumLight, fontSize = 10.sp)
                      Text("Vente Pièces + MO : ${orItem.totalRevenueHtDzd} DA", color = PureWhite, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                      Text(
                        text = "+${orItem.netProfitMarginDzd} DA NET",
                        color = ElectricGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                      )
                      Text("Taux de marge : ${orItem.grossMarginPercent}%", color = AluminumLight, fontSize = 9.5.sp)
                    }
                  }
                }
              }
            }
          }
        } else {
          // Tab 1: Bulk Oil Drums 208L
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
          ) {
            items(bulkOilDrums, key = { it.id }) { drum ->
              val barColor = if (drum.isLowLevelAlert) PerformanceRedGlow else ElectricGreen
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, barColor)
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(drum.fluidName, color = PureWhite, fontWeight = FontWeight.Black, fontSize = 12.sp)
                      Text(drum.viscosityAndNorm, color = TechCyan, fontSize = 10.sp)
                    }
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = if (drum.isLowLevelAlert) PerformanceRedDark else Color(0xFF1B5E20)
                    ) {
                      Text(
                        text = if (drum.isLowLevelAlert) "⚠️ SEUIL CRITIQUE (${drum.fillPercentage}%)" else "${drum.fillPercentage}% DISPO",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                      )
                    }
                  }

                  LinearProgressIndicator(
                    progress = { (drum.fillPercentage / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(10.dp)
                      .clip(RoundedCornerShape(5.dp)),
                    color = barColor,
                    trackColor = GraphiteBlack
                  )

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = "Reste : ${drum.currentRemainingLiters} L / ${drum.drumCapacityLiters.toInt()} L",
                      color = PureWhite,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                    Text(
                      text = "Marge : +${drum.sellPricePerLiterDzd - drum.costPerLiterDzd} DA / Litre",
                      color = ElectricGreen,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                  }

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    OutlinedButton(
                      onClick = { onDispenseOilFromDrum(drum.id, 5.0) },
                      border = BorderStroke(1.dp, TechCyan),
                      modifier = Modifier.weight(1f)
                    ) {
                      Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, tint = TechCyan, modifier = Modifier.size(15.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Prélever 5L (Vidange)", color = TechCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                      onClick = { onRefillOilDrum(drum.id) },
                      colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrangeDark),
                      modifier = Modifier.weight(1f)
                    ) {
                      Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(15.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Fût Neuf Plein", fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
