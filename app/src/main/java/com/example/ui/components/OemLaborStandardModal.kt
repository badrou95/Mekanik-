package com.example.ui.components

import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.LaborCategory
import com.example.data.model.LaborStandardOperation
import com.example.ui.theme.*

@Composable
fun OemLaborStandardModal(
  operations: List<LaborStandardOperation>,
  currentHourlyRateDzd: Int = 2500,
  onApplyOperationsToQuote: (selectedOps: List<LaborStandardOperation>, totalCostDzd: Int) -> Unit,
  onDismiss: () -> Unit
) {
  var hourlyRateText by remember { mutableStateOf(currentHourlyRateDzd.toString()) }
  val hourlyRate = hourlyRateText.toIntOrNull() ?: currentHourlyRateDzd
  var selectedCategory by remember { mutableStateOf<LaborCategory?>(null) }
  var searchQuery by remember { mutableStateOf("") }
  val selectedOperationIds = remember { mutableStateListOf<String>() }

  val filteredOperations = operations.filter { op ->
    val matchesCat = selectedCategory == null || op.category == selectedCategory
    val matchesQuery = searchQuery.isBlank() ||
      op.title.contains(searchQuery, ignoreCase = true) ||
      op.oemCode.contains(searchQuery, ignoreCase = true) ||
      op.engineCompatibility.contains(searchQuery, ignoreCase = true)
    matchesCat && matchesQuery
  }

  val selectedOpsList = operations.filter { it.id in selectedOperationIds }
  val totalHours = selectedOpsList.sumOf { it.standardHours }
  val totalCostDzd = Math.round(totalHours * hourlyRate).toInt()

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = 28.dp, bottom = 14.dp, start = 10.dp, end = 10.dp)
        .testTag("modal_labor_standards"),
      shape = RoundedCornerShape(16.dp),
      color = GraphiteDark,
      border = BorderStroke(1.dp, MetalBorder)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(TechCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Timer, contentDescription = null, tint = TechCyan, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "BARÈME DES TEMPS CONSTRUCTEUR OEM",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = PureWhite,
                  fontSize = 15.sp
                )
              )
              Text(
                text = "Temps officiels barémés & chiffrage main d'œuvre certifié",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(MetalCard)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight, modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hourly Rate Config Card
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("TAUX HORAIRE ATELIER (DZD/H)", color = AluminumGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              Text("Tarif de base appliqué aux barèmes", color = AluminumMuted, fontSize = 10.sp)
            }

            OutlinedTextField(
              value = hourlyRateText,
              onValueChange = { hourlyRateText = it },
              modifier = Modifier.width(130.dp),
              suffix = { Text("DA/h", color = TechCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = PureWhite,
                unfocusedTextColor = PureWhite,
                focusedBorderColor = TechCyan,
                unfocusedBorderColor = MetalBorder
              ),
              singleLine = true
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Rechercher opération (distribution, embrayage, disques...)") },
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = PureWhite,
            unfocusedTextColor = PureWhite,
            focusedBorderColor = TechCyan,
            unfocusedBorderColor = MetalBorder
          ),
          singleLine = true,
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AluminumLight) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          item {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (selectedCategory == null) PerformanceRed else MetalCard,
              border = BorderStroke(1.dp, if (selectedCategory == null) PerformanceRedGlow else MetalBorder),
              onClick = { selectedCategory = null }
            ) {
              Text("Toutes (${operations.size})", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp))
            }
          }
          items(LaborCategory.entries) { cat ->
            val isSel = selectedCategory == cat
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (isSel) PerformanceRed else MetalCard,
              border = BorderStroke(1.dp, if (isSel) PerformanceRedGlow else MetalBorder),
              onClick = { selectedCategory = cat }
            ) {
              Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(cat.iconEmoji, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(cat.label, color = if (isSel) PureWhite else AluminumLight, fontSize = 10.5.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Operations List
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(filteredOperations) { op ->
            val isChecked = op.id in selectedOperationIds
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = if (isChecked) Color(0xFF162530) else MetalCard),
              border = BorderStroke(1.dp, if (isChecked) TechCyan else MetalBorder),
              modifier = Modifier.clickable {
                if (isChecked) selectedOperationIds.remove(op.id) else selectedOperationIds.add(op.id)
              }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Checkbox(
                  checked = isChecked,
                  onCheckedChange = { checked ->
                    if (checked) selectedOperationIds.add(op.id) else selectedOperationIds.remove(op.id)
                  },
                  colors = CheckboxDefaults.colors(
                    checkedColor = TechCyan,
                    uncheckedColor = AluminumGray,
                    checkmarkColor = GraphiteDark
                  )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(4.dp), color = GraphiteBlack) {
                      Text(op.oemCode, color = TechCyan, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(op.category.label, color = AluminumMuted, fontSize = 10.sp)
                  }
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(op.title, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  Text("Compatibilité : ${op.engineCompatibility}", color = AluminumGray, fontSize = 10.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                  Surface(shape = RoundedCornerShape(6.dp), color = PerformanceRedDark) {
                    Text(
                      text = op.formattedTime,
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.sp,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "${op.calculateCost(hourlyRate)} DA",
                    color = TechCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Total Selection Summary & Validation
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
          border = BorderStroke(1.dp, if (selectedOperationIds.isNotEmpty()) TechCyan else MetalBorder)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("${selectedOperationIds.size} opération(s) sélectionnée(s)", color = AluminumLight, fontSize = 11.sp)
              Text("Total Temps : ${String.format("%.2f", totalHours)} h", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Column(horizontalAlignment = Alignment.End) {
              Text("Total Main-d'œuvre :", color = AluminumMuted, fontSize = 10.sp)
              Text("$totalCostDzd DZD", color = TechCyan, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
          onClick = {
            onApplyOperationsToQuote(selectedOpsList, totalCostDzd)
          },
          enabled = selectedOperationIds.isNotEmpty(),
          colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .testTag("btn_apply_labor_standards")
        ) {
          Icon(Icons.Default.PlaylistAddCheck, contentDescription = null, tint = PureWhite)
          Spacer(modifier = Modifier.width(8.dp))
          Text("INSÉRER AU DEVIS ATELIER ($totalCostDzd DA)", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
        }
      }
    }
  }
}
