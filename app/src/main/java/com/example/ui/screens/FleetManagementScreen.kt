package com.example.ui.screens

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
import com.example.data.model.FleetVehicle
import com.example.data.model.FleetVehicleStatus
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

@Composable
fun FleetManagementScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val filteredVehicles = if (uiState.fleetStatusFilter == null) {
    uiState.fleetVehicles
  } else {
    uiState.fleetVehicles.filter { it.status == uiState.fleetStatusFilter }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("fleet_management_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onBack,
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(MetalCard)
          ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = PureWhite)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "GESTIONNAIRE DE FLOTTE",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite
              )
            )
            Text(
              text = uiState.fleetSummary.fleetCategory,
              style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.sp)
            )
          }
        }
      }
    }

    // KPI Summary Row
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FleetKpiBox(
          title = "Véhicules",
          value = "${uiState.fleetSummary.totalVehicles}",
          subtitle = "${uiState.fleetSummary.activeInService} en service",
          accentColor = ElectricGreen,
          modifier = Modifier.weight(1f)
        )
        FleetKpiBox(
          title = "Maintenance",
          value = "${uiState.fleetSummary.inMaintenance}",
          subtitle = "${uiState.fleetSummary.pendingAlertsCount} alertes urgentes",
          accentColor = PerformanceRed,
          modifier = Modifier.weight(1f)
        )
        FleetKpiBox(
          title = "Budget Mensuel",
          value = "${(uiState.fleetSummary.totalMonthlyBudgetDzd / 1000).toInt()}k DA",
          subtitle = "Consommables & MO",
          accentColor = MechanicalOrange,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Status Filter Chips
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = if (uiState.fleetStatusFilter == null) PerformanceRed else MetalCard,
          border = BorderStroke(1.dp, if (uiState.fleetStatusFilter == null) PerformanceRedGlow else MetalBorder),
          modifier = Modifier.clickable { viewModel.setFleetFilter(null) }
        ) {
          Text(
            text = "Tous (${uiState.fleetVehicles.size})",
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (uiState.fleetStatusFilter == null) PureWhite else AluminumLight,
              fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }

        FleetVehicleStatus.values().forEach { status ->
          val isSelected = uiState.fleetStatusFilter == status
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) PerformanceRed else MetalCard,
            border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
            modifier = Modifier.clickable { viewModel.setFleetFilter(status) }
          ) {
            Text(
              text = status.label,
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (isSelected) PureWhite else AluminumLight,
                fontWeight = FontWeight.Bold
              ),
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
          }
        }
      }
    }

    // Fleet Vehicles List
    items(filteredVehicles) { vehicle ->
      FleetVehicleCard(
        vehicle = vehicle,
        onInspect = {
          viewModel.onSearchQueryChanged(vehicle.vin)
          viewModel.performVehicleSearch()
        }
      )
    }
  }
}

@Composable
private fun FleetKpiBox(
  title: String,
  value: String,
  subtitle: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = MetalCard,
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 10.sp))
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, style = MaterialTheme.typography.titleMedium.copy(color = accentColor, fontWeight = FontWeight.Black))
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 9.sp))
    }
  }
}

@Composable
private fun FleetVehicleCard(
  vehicle: FleetVehicle,
  onInspect: () -> Unit
) {
  val statusColor = when (vehicle.status) {
    FleetVehicleStatus.EN_SERVICE -> ElectricGreen
    FleetVehicleStatus.CONTROLE_REQUIS -> WarningAmber
    FleetVehicleStatus.EN_MAINTENANCE -> PerformanceRed
    FleetVehicleStatus.IMMOBILISE -> Color(0xFF9E9E9E)
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.35f))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = vehicle.brandModel,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite)
          )
          Text(
            text = "Matricule : ${vehicle.plateNumber} • Châssis : ${vehicle.vin}",
            style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.sp)
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = statusColor.copy(alpha = 0.2f),
          border = BorderStroke(1.dp, statusColor)
        ) {
          Text(
            text = vehicle.status.label,
            style = MaterialTheme.typography.labelSmall.copy(color = statusColor, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(text = "Chauffeur assigné", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted))
          Text(text = "${vehicle.assignedDriver} (${vehicle.driverPhone})", style = MaterialTheme.typography.bodySmall.copy(color = OffWhite))
        }
        Column(horizontalAlignment = Alignment.End) {
          Text(text = "Kilométrage", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted))
          Text(text = "${vehicle.currentMileageKm} km (Prochaine : ${vehicle.nextServiceKm} km)", style = MaterialTheme.typography.bodySmall.copy(color = ElectricGreen, fontWeight = FontWeight.Bold))
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Assurance : ${vehicle.insuranceExpiryDate}",
          style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 10.5.sp)
        )

        Button(
          onClick = onInspect,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed, contentColor = PureWhite),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(text = "Fiche & Pièces", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
        }
      }
    }
  }
}
