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
import com.example.data.model.VehicleExpenseCategory
import com.example.data.model.VehicleExpenseEntry
import com.example.data.model.VehicleTcoSummary
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun VehicleTcoCalculatorModal(
  summaries: List<VehicleTcoSummary>,
  expenses: List<VehicleExpenseEntry>,
  onAddExpense: (VehicleExpenseCategory, String, Int, Int) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedVehicleIdx by remember { mutableIntStateOf(0) }
  var showAddExpenseForm by remember { mutableStateOf(false) }
  var selectedCategory by remember { mutableStateOf(VehicleExpenseCategory.CARBURANT) }
  var expenseTitle by remember { mutableStateOf("") }
  var expenseAmountStr by remember { mutableStateOf("2000") }
  var expenseMileageStr by remember { mutableStateOf("89500") }

  val currentSummary = summaries.getOrNull(selectedVehicleIdx) ?: summaries.first()
  val formattedCostPerKm = String.format(Locale.US, "%.2f", currentSummary.costPerKilometerDzd)

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)
        .testTag("vehicle_tco_calculator_modal"),
      shape = RoundedCornerShape(18.dp),
      color = GraphiteDark,
      border = BorderStroke(1.dp, EvBlue)
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
                .background(EvBlue.copy(alpha = 0.25f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.QueryStats, contentDescription = null, tint = EvBlue)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "COÛT KILOMÉTRIQUE (TCO) & CARBURANT",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
              )
              Text(
                text = "Suivi budget réel DA/km, carburant & rentabilité flotte",
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
          // Vehicle Selector Tabs
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              summaries.forEachIndexed { idx, summary ->
                val isSelected = idx == selectedVehicleIdx
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (isSelected) EvBlue else MetalCard,
                  border = BorderStroke(1.dp, if (isSelected) TechCyan else MetalBorder),
                  modifier = Modifier
                    .weight(1f)
                    .clickable { selectedVehicleIdx = idx }
                ) {
                  Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Text(
                      text = summary.vehicleName,
                      color = PureWhite,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp,
                      maxLines = 1
                    )
                    Text(
                      text = summary.licensePlate,
                      color = if (isSelected) PureWhite else AluminumLight,
                      fontSize = 10.sp
                    )
                  }
                }
              }
            }
          }

          // Hero Cost per Km & Annual Budget Card
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, EvBlue)
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text("COÛT DE REVIENT KILOMÉTRIQUE RÉEL", color = AluminumGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(
                      text = "$formattedCostPerKm DA / km",
                      color = TechCyan,
                      fontWeight = FontWeight.Black,
                      fontSize = 22.sp
                    )
                    Text(
                      text = "Basé sur ${currentSummary.annualDistanceKm} km/an • Conso moy: ${currentSummary.averageFuelLitersPer100Km} L/100km",
                      color = AluminumLight,
                      fontSize = 10.5.sp
                    )
                  }
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, ElectricGreen)
                  ) {
                    Column(
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                      horizontalAlignment = Alignment.End
                    ) {
                      Text("BUDGET MENSUEL", color = AluminumGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                      Text(
                        text = "${currentSummary.monthlyAverageCostDzd} DA",
                        color = ElectricGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                      )
                    }
                  }
                }

                HorizontalDivider(color = MetalBorder)

                // Breakdown Grid
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Column {
                    Text("⛽ Carburant annuel", color = AluminumGray, fontSize = 10.sp)
                    Text("${currentSummary.totalFuelCostDzd} DA", color = MechanicalOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  }
                  Column {
                    Text("🔧 Entretien & Pièces", color = AluminumGray, fontSize = 10.sp)
                    Text("${currentSummary.totalMaintenanceCostDzd} DA", color = PerformanceRedGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  }
                  Column {
                    Text("🛡️ Assurance & Pneus", color = AluminumGray, fontSize = 10.sp)
                    Text("${currentSummary.totalInsuranceCostDzd + currentSummary.totalTiresCostDzd} DA", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  }
                }

                // Rental Fleet Profitability Bar
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.5f)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text("INDICATEUR LOUEUR / FLOTTE (${currentSummary.rentedDaysPerMonth} j/mois loués)", color = ElectricGreen, fontWeight = FontWeight.Black, fontSize = 10.sp)
                      Text("CA mensuel : ${currentSummary.estimatedMonthlyRentalRevenueDzd} DA (${currentSummary.dailyRentalRateDzd} DA/j)", color = AluminumLight, fontSize = 10.5.sp)
                    }
                    Text(
                      text = "Marge : +${currentSummary.netMonthlyRentalProfitDzd} DA/mois",
                      color = ElectricGreen,
                      fontWeight = FontWeight.Black,
                      fontSize = 12.sp
                    )
                  }
                }
              }
            }
          }

          // Add Expense Button
          item {
            Button(
              onClick = { showAddExpenseForm = !showAddExpenseForm },
              modifier = Modifier.fillMaxWidth(),
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed, contentColor = PureWhite)
            ) {
              Icon(Icons.Default.LocalGasStation, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (showAddExpenseForm) "Fermer la saisie" else "Ajouter un Plein Carburant ou une Dépense Véhicule",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }

          if (showAddExpenseForm) {
            item {
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
                border = BorderStroke(1.dp, TechCyan)
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  Text("NOUVELLE DÉPENSE VÉHICULE", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 12.sp)
                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                      VehicleExpenseCategory.CARBURANT,
                      VehicleExpenseCategory.ENTRETIEN_PIECES,
                      VehicleExpenseCategory.PNEUMATIQUES
                    ).forEach { cat ->
                      val isSel = selectedCategory == cat
                      Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) TechCyan.copy(alpha = 0.2f) else MetalCard,
                        border = BorderStroke(1.dp, if (isSel) TechCyan else MetalBorder),
                        modifier = Modifier
                          .weight(1f)
                          .clickable { selectedCategory = cat }
                      ) {
                        Text(
                          text = cat.label,
                          color = if (isSel) TechCyan else PureWhite,
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          modifier = Modifier.padding(8.dp)
                        )
                      }
                    }
                  }
                  OutlinedTextField(
                    value = expenseTitle,
                    onValueChange = { expenseTitle = it },
                    label = { Text("Libellé (ex: Plein Gasoil Naftal, Vidange...)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                  )
                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                      value = expenseAmountStr,
                      onValueChange = { expenseAmountStr = it },
                      label = { Text("Montant (DZD)") },
                      singleLine = true,
                      modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                      value = expenseMileageStr,
                      onValueChange = { expenseMileageStr = it },
                      label = { Text("Kilométrage (km)") },
                      singleLine = true,
                      modifier = Modifier.weight(1f)
                    )
                  }
                  Button(
                    onClick = {
                      val amt = expenseAmountStr.toIntOrNull() ?: 2000
                      val km = expenseMileageStr.toIntOrNull() ?: 89500
                      onAddExpense(
                        selectedCategory,
                        expenseTitle.ifBlank { selectedCategory.label },
                        amt,
                        km
                      )
                      expenseTitle = ""
                      showAddExpenseForm = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen, contentColor = GraphiteBlack)
                  ) {
                    Text("Enregistrer & Recalculer le TCO", fontWeight = FontWeight.Black)
                  }
                }
              }
            }
          }

          // Expense History
          item {
            Text("HISTORIQUE DES DÉPENSES & PLEINS :", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 12.sp)
          }

          items(expenses, key = { it.id }) { exp ->
            val catColor = Color(exp.category.colorHex)
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, MetalBorder),
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
                  Text(exp.title, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  Text(
                    text = "${exp.category.label} • ${exp.date} • Compteur : ${exp.mileageKm} km",
                    color = AluminumLight,
                    fontSize = 10.5.sp
                  )
                }
                Text(
                  text = "${exp.amountDzd} DA",
                  color = catColor,
                  fontWeight = FontWeight.Black,
                  fontSize = 13.5.sp
                )
              }
            }
          }
        }
      }
    }
  }
}
