package com.example.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

enum class RentalFleetTab(val label: String, val icon: ImageVector) {
  PARC_VEHICULES("Parc & Véhicules", Icons.Default.DirectionsCar),
  CONTRATS_LOCATION("Contrats de Location", Icons.Default.Assignment),
  MAINTENANCE_ALERTES("Maintenance & Alertes", Icons.Default.Build)
}

@Composable
fun CarRentalFleetScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  onBack: () -> Unit,
  onNavigateToWorkshop: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableStateOf(RentalFleetTab.PARC_VEHICULES) }
  val summary = uiState.rentalFleetSummary

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("car_rental_fleet_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Header with back button & Agency Name
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
              text = "AGENCE LOCATION DE VOITURES",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite
              )
            )
            Text(
              text = "${summary.agencyName} • ${summary.wilaya}",
              style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.sp)
            )
          }
        }

        IconButton(
          onClick = { viewModel.toggleNewRentalContractModal(true) },
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(PerformanceRed)
        ) {
          Icon(Icons.Default.Add, contentDescription = "Nouveau Contrat", tint = PureWhite)
        }
      }
    }

    // 2. Rental Fleet KPI Summary Cards
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        RentalKpiCard(
          title = "Flotte Totale",
          value = "${summary.totalCars}",
          subtitle = "${summary.rentedCars} loués • ${summary.availableCars} dispo",
          accentColor = ElectricGreen,
          modifier = Modifier.weight(1f)
        )
        RentalKpiCard(
          title = "Occupation",
          value = "${summary.occupancyRatePct.toInt()}%",
          subtitle = "${summary.inWorkshopCars} en atelier",
          accentColor = TechCyan,
          modifier = Modifier.weight(1f)
        )
        RentalKpiCard(
          title = "Revenus / Mois",
          value = "${(summary.monthlyRentalRevenueDzd / 1000).toInt()}k DA",
          subtitle = "Charges: ${(summary.monthlyMaintenanceExpenseDzd / 1000).toInt()}k",
          accentColor = MechanicalOrange,
          modifier = Modifier.weight(1.1f)
        )
      }
    }

    // QR Code Fleet & Restitution Quick Bar
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MetalCard,
        border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.7f)),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { viewModel.toggleUniversalQrScanner(true) }
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(MechanicalOrangeDark),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = PureWhite, modifier = Modifier.size(20.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "SCANNER QR VÉHICULE EN RESTITUTION",
              style = MaterialTheme.typography.labelSmall.copy(
                color = PureWhite,
                fontWeight = FontWeight.Bold
              )
            )
            Text(
              text = "Accès direct au contrat, contrôle km retour, libération caution ou envoi atelier",
              style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 10.5.sp)
            )
          }
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MechanicalOrange
          ) {
            Text(
              text = "SCANNER",
              style = MaterialTheme.typography.labelSmall.copy(
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              ),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }
    }

    // 3. Tab Chips Selector
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        RentalFleetTab.values().forEach { tab ->
          val isSelected = selectedTab == tab
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) PerformanceRed else MetalCard,
            border = BorderStroke(
              1.dp,
              if (isSelected) PerformanceRedGlow else MetalBorder
            ),
            modifier = Modifier
              .weight(1f)
              .clickable { selectedTab = tab }
          ) {
            Row(
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                tab.icon,
                contentDescription = null,
                tint = if (isSelected) PureWhite else AluminumLight,
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = tab.label,
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (isSelected) PureWhite else AluminumLight,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.5.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }
      }
    }

    // 4. Tab Views
    when (selectedTab) {
      RentalFleetTab.PARC_VEHICULES -> {
        // Status & Category Filter Chips
        item {
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            item {
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (uiState.selectedRentalStatusFilter == null) PerformanceRed else MetalCard,
                border = BorderStroke(
                  1.dp,
                  if (uiState.selectedRentalStatusFilter == null) PerformanceRedGlow else MetalBorder
                ),
                modifier = Modifier.clickable { viewModel.filterRentalStatus(null) }
              ) {
                Text(
                  text = "Tous (${uiState.rentalVehicles.size})",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (uiState.selectedRentalStatusFilter == null) PureWhite else AluminumLight,
                    fontWeight = FontWeight.Bold
                  ),
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
              }
            }

            items(RentalStatus.values()) { status ->
              val isSelected = uiState.selectedRentalStatusFilter == status
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) PerformanceRed else MetalCard,
                border = BorderStroke(
                  1.dp,
                  if (isSelected) PerformanceRedGlow else MetalBorder
                ),
                modifier = Modifier.clickable { viewModel.filterRentalStatus(status) }
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

        val filteredVehicles = if (uiState.selectedRentalStatusFilter == null) {
          uiState.rentalVehicles
        } else {
          uiState.rentalVehicles.filter { it.rentalStatus == uiState.selectedRentalStatusFilter }
        }

        items(filteredVehicles) { vehicle ->
          RentalVehicleCard(
            vehicle = vehicle,
            onOpenDetails = { viewModel.openRentalVehicleDetails(vehicle) },
            onSendToWorkshop = {
              viewModel.sendRentalVehicleToWorkshop(vehicle.id, "Révision périodique demandée par l'agence")
            },
            onCreateContract = {
              viewModel.toggleNewRentalContractModal(true)
            }
          )
        }
      }

      RentalFleetTab.CONTRATS_LOCATION -> {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "CONTRATS DE LOCATION EN COURS & ARCHIVES",
              style = MaterialTheme.typography.labelMedium.copy(
                color = AluminumMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            )
            Button(
              onClick = { viewModel.toggleNewRentalContractModal(true) },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.height(30.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, tint = PureWhite, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Nouveau Contrat", style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold))
            }
          }
        }

        items(uiState.rentalContracts) { contract ->
          RentalContractCard(
            contract = contract,
            onOpenDetails = { viewModel.openRentalContractDetails(contract) },
            onDownloadPdf = { viewModel.generateAndDownloadRentalContractPdf(context, contract) },
            onReturnCar = {
              val currentCar = uiState.rentalVehicles.find { it.id == contract.vehicleId }
              val targetKm = (currentCar?.currentMileageKm ?: contract.initialKm) + 450
              viewModel.returnRentalCar(contract.contractId, targetKm)
            }
          )
        }
      }

      RentalFleetTab.MAINTENANCE_ALERTES -> {
        item {
          Text(
            text = "ÉCHÉANCIER PRÉVENTIF & ALERTES ATELIER",
            style = MaterialTheme.typography.labelMedium.copy(
              color = AluminumMuted,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          )
        }

        // Overdue Alert Banner
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = PerformanceRedDark.copy(alpha = 0.3f)),
            border = BorderStroke(1.dp, PerformanceRed)
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "2 Véhicules nécessitent un passage immédiat à l'atelier",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                )
                Text(
                  text = "Dacia Duster (+2100 km de dépassement) et Renault Symbol (Contrôle technique requis).",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.5.sp)
                )
              }
              Button(
                onClick = onNavigateToWorkshop,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(30.dp)
              ) {
                Text("Atelier", style = MaterialTheme.typography.labelSmall.copy(color = PureWhite))
              }
            }
          }
        }

        items(uiState.rentalVehicles) { vehicle ->
          RentalMaintenanceScheduleCard(
            vehicle = vehicle,
            onOpenDetails = { viewModel.openRentalVehicleDetails(vehicle) }
          )
        }
      }
    }
  }

  // --- 5. MODAL NOUVEAU CONTRAT DE LOCATION ---
  if (uiState.showNewRentalContractModal) {
    NewRentalContractDialog(
      availableVehicles = uiState.rentalVehicles.filter { it.rentalStatus == RentalStatus.DISPONIBLE },
      onDismiss = { viewModel.toggleNewRentalContractModal(false) },
      onConfirmContract = { vehicleId, clientName, clientPhone, clientNid, clientPermis, durationDays, depositDzd ->
        viewModel.createRentalContract(
          vehicleId = vehicleId,
          clientName = clientName,
          clientPhone = clientPhone,
          clientNid = clientNid,
          clientPermis = clientPermis,
          durationDays = durationDays,
          depositDzd = depositDzd
        )
      }
    )
  }

  // --- 6. MODAL FICHE TECHNIQUE & HISTORIQUE VÉHICULE ---
  if (uiState.showRentalVehicleModal && uiState.selectedRentalVehicle != null) {
    RentalVehicleDetailDialog(
      vehicle = uiState.selectedRentalVehicle!!,
      onDismiss = { viewModel.closeRentalVehicleModal() },
      onSendToWorkshop = {
        viewModel.sendRentalVehicleToWorkshop(uiState.selectedRentalVehicle!!.id, "Maintenance programmée")
      }
    )
  }
}

@Composable
fun RentalKpiCard(
  title: String,
  value: String,
  subtitle: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 9.5.sp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Black,
          color = accentColor,
          fontSize = 15.sp
        )
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 9.5.sp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
fun RentalVehicleCard(
  vehicle: RentalVehicle,
  onOpenDetails: () -> Unit,
  onSendToWorkshop: () -> Unit,
  onCreateContract: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = vehicle.brandModel,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PureWhite)
            )
          }
          Text(
            text = "Immat : ${vehicle.plateNumber} • ${vehicle.category.label}",
            style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
          )
        }

        // Status Badge
        val (bgColor, textColor) = when (vehicle.rentalStatus) {
          RentalStatus.DISPONIBLE -> Pair(ElectricGreen.copy(alpha = 0.15f), ElectricGreen)
          RentalStatus.EN_LOCATION -> Pair(EvBlue.copy(alpha = 0.15f), EvBlue)
          RentalStatus.EN_REVISION_ATELIER -> Pair(PerformanceRedDark.copy(alpha = 0.3f), PerformanceRedGlow)
          RentalStatus.RESERVE -> Pair(WarningAmber.copy(alpha = 0.2f), WarningAmber)
          RentalStatus.NETTOYAGE_INSPECTION -> Pair(TechCyan.copy(alpha = 0.15f), TechCyan)
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = bgColor,
          border = BorderStroke(1.dp, textColor)
        ) {
          Text(
            text = vehicle.rentalStatus.label,
            style = MaterialTheme.typography.labelSmall.copy(color = textColor, fontWeight = FontWeight.Bold, fontSize = 9.5.sp),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Current Renter info if rented
      if (vehicle.currentRenterName != null) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MetalSurface,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = EvBlue, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Locataire : ${vehicle.currentRenterName}",
              style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontSize = 11.5.sp)
            )
          }
        }
        Spacer(modifier = Modifier.height(8.dp))
      }

      // Stats row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text("Kilométrage", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 9.5.sp))
          Text("${vehicle.currentMileageKm} km", style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp))
        }
        Column {
          Text("Carburant", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 9.5.sp))
          Text("${vehicle.fuelLevelPct}% (${vehicle.fuelType})", style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 11.5.sp))
        }
        Column {
          Text("Tarif / Jour", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 9.5.sp))
          Text("${vehicle.dailyRateDzd} DA", style = MaterialTheme.typography.bodySmall.copy(color = MechanicalOrange, fontWeight = FontWeight.Bold, fontSize = 11.5.sp))
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Divider(color = MetalBorder)
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = onOpenDetails,
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, MetalBorder),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          modifier = Modifier.height(32.dp)
        ) {
          Icon(Icons.Default.Info, contentDescription = null, tint = PureWhite, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Fiche & Historique", style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontSize = 10.5.sp))
        }

        if (vehicle.rentalStatus == RentalStatus.DISPONIBLE) {
          Button(
            onClick = onCreateContract,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(32.dp)
          ) {
            Icon(Icons.Default.Key, contentDescription = null, tint = PureWhite, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Louer ce Véhicule", style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp))
          }
        } else if (vehicle.rentalStatus != RentalStatus.EN_REVISION_ATELIER) {
          OutlinedButton(
            onClick = onSendToWorkshop,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MechanicalOrange),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.height(32.dp)
          ) {
            Icon(Icons.Default.Build, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Révision Atelier", style = MaterialTheme.typography.labelSmall.copy(color = MechanicalOrange, fontSize = 10.5.sp))
          }
        }
      }
    }
  }
}

@Composable
fun RentalContractCard(
  contract: RentalContract,
  onOpenDetails: () -> Unit,
  onDownloadPdf: (() -> Unit)? = null,
  onReturnCar: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = contract.contractId, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PureWhite))
          Text(
            text = "${contract.vehicleName} (${contract.plateNumber})",
            style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.5.sp)
          )
        }

        val isOverdue = contract.status == ContractStatus.RETARD_RESTITUTION
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (isOverdue) PerformanceRedDark.copy(alpha = 0.3f) else ElectricGreen.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, if (isOverdue) PerformanceRed else ElectricGreen)
        ) {
          Text(
            text = contract.status.label,
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (isOverdue) PerformanceRedGlow else ElectricGreen,
              fontWeight = FontWeight.Bold,
              fontSize = 9.5.sp
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Surface(
        shape = RoundedCornerShape(8.dp),
        color = MetalSurface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(text = "Client : ${contract.clientFullName} • Tél : ${contract.clientPhone}", style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontSize = 11.5.sp))
          Text(text = "Permis de conduire : ${contract.clientDrivingLicense}", style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp))
          Text(text = "Période : Du ${contract.startDate} au ${contract.endDate} (${contract.durationDays}j)", style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp))
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Divider(color = MetalBorder)
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Caution : ${contract.depositAmountDzd} DA", style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 10.sp))
          Text("Total : ${contract.totalRentDzd} DA", style = MaterialTheme.typography.titleSmall.copy(color = MechanicalOrange, fontWeight = FontWeight.Black))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          if (onDownloadPdf != null) {
            IconButton(
              onClick = onDownloadPdf,
              modifier = Modifier.size(32.dp)
            ) {
              Icon(Icons.Default.Download, contentDescription = "Télécharger PDF", tint = TechCyan, modifier = Modifier.size(18.dp))
            }
          }

          if (contract.status == ContractStatus.EN_COURS || contract.status == ContractStatus.RETARD_RESTITUTION) {
            Button(
              onClick = onReturnCar,
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen.copy(alpha = 0.85f)),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.height(32.dp)
            ) {
              Icon(Icons.Default.Check, contentDescription = null, tint = PureWhite, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Restitution", style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold))
            }
          }
        }
      }
    }
  }
}

@Composable
fun RentalMaintenanceScheduleCard(
  vehicle: RentalVehicle,
  onOpenDetails: () -> Unit
) {
  val kmRemaining = vehicle.nextScheduledServiceKm - vehicle.currentMileageKm
  val isOverdue = kmRemaining <= 0

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, if (isOverdue) PerformanceRed else MetalBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = vehicle.brandModel, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PureWhite))
          Text(text = "Immat : ${vehicle.plateNumber}", style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp))
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (isOverdue) PerformanceRedDark.copy(alpha = 0.3f) else TechCyan.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, if (isOverdue) PerformanceRed else TechCyan)
        ) {
          Text(
            text = if (isOverdue) "DÉPASSÉ (+${-kmRemaining} km)" else "Dans $kmRemaining km",
            style = MaterialTheme.typography.labelSmall.copy(
              color = if (isOverdue) PerformanceRedGlow else TechCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 9.5.sp
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text("Contrôle Tech.", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 9.5.sp))
          Text(vehicle.technicalControlExpiry, style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontSize = 11.5.sp))
        }
        Column {
          Text("Assurance Flotte", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 9.5.sp))
          Text(vehicle.insuranceExpiry, style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontSize = 11.5.sp))
        }
        Column {
          Text("Dépenses Maintenance", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 9.5.sp))
          Text("${vehicle.totalMaintenanceSpentDzd} DA", style = MaterialTheme.typography.bodySmall.copy(color = MechanicalOrange, fontWeight = FontWeight.Bold, fontSize = 11.5.sp))
        }
      }
    }
  }
}

@Composable
fun NewRentalContractDialog(
  availableVehicles: List<RentalVehicle>,
  onDismiss: () -> Unit,
  onConfirmContract: (vehicleId: String, clientName: String, clientPhone: String, clientNid: String, clientPermis: String, durationDays: Int, depositDzd: Long) -> Unit
) {
  var selectedVehicleId by remember { mutableStateOf(availableVehicles.firstOrNull()?.id ?: "") }
  var clientName by remember { mutableStateOf("") }
  var clientPhone by remember { mutableStateOf("") }
  var clientNid by remember { mutableStateOf("") }
  var clientPermis by remember { mutableStateOf("") }
  var durationDays by remember { mutableStateOf("7") }
  var depositDzd by remember { mutableStateOf("50000") }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.88f),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, MetalBorder)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = MetalCard,
          border = BorderStroke(0.5.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = "NOUVEAU CONTRAT DE LOCATION", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = PureWhite))
              Text(text = "Création & Enregistrement Caution CIB", style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.5.sp))
            }
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite)
            }
          }
        }

        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            Text("1. SÉLECTIONNER UN VÉHICULE DU PARC", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
            Spacer(modifier = Modifier.height(6.dp))
            if (availableVehicles.isEmpty()) {
              Text("Aucun véhicule actuellement disponible au parc.", style = MaterialTheme.typography.bodySmall.copy(color = WarningAmber))
            } else {
              availableVehicles.forEach { car ->
                val isSelected = selectedVehicleId == car.id
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (isSelected) PerformanceRedDark.copy(alpha = 0.4f) else MetalCard,
                  border = BorderStroke(1.dp, if (isSelected) PerformanceRed else MetalBorder),
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clickable { selectedVehicleId = car.id }
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text(car.brandModel, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite))
                      Text("${car.plateNumber} • ${car.dailyRateDzd} DA / jour", style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.sp))
                    }
                    if (isSelected) {
                      Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PerformanceRedGlow)
                    }
                  }
                }
              }
            }
          }

          item {
            Text("2. INFORMATIONS DU LOCATAIRE", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
              value = clientName,
              onValueChange = { clientName = it },
              label = { Text("Nom & Prénom du client", color = AluminumLight) },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = PureWhite,
                unfocusedTextColor = PureWhite,
                focusedBorderColor = PerformanceRed,
                unfocusedBorderColor = MetalBorder
              )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
              value = clientPhone,
              onValueChange = { clientPhone = it },
              label = { Text("Numéro de Téléphone (05xx / 06xx / 07xx)", color = AluminumLight) },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = PureWhite,
                unfocusedTextColor = PureWhite,
                focusedBorderColor = PerformanceRed,
                unfocusedBorderColor = MetalBorder
              )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
              value = clientPermis,
              onValueChange = { clientPermis = it },
              label = { Text("N° Permis de Conduire", color = AluminumLight) },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = PureWhite,
                unfocusedTextColor = PureWhite,
                focusedBorderColor = PerformanceRed,
                unfocusedBorderColor = MetalBorder
              )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedTextField(
                value = durationDays,
                onValueChange = { durationDays = it },
                label = { Text("Durée (Jours)", color = AluminumLight) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedTextColor = PureWhite,
                  unfocusedTextColor = PureWhite,
                  focusedBorderColor = PerformanceRed,
                  unfocusedBorderColor = MetalBorder
                )
              )
              OutlinedTextField(
                value = depositDzd,
                onValueChange = { depositDzd = it },
                label = { Text("Caution (DA)", color = AluminumLight) },
                modifier = Modifier.weight(1.2f),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedTextColor = PureWhite,
                  unfocusedTextColor = PureWhite,
                  focusedBorderColor = PerformanceRed,
                  unfocusedBorderColor = MetalBorder
                )
              )
            }
          }
        }

        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = MetalCard,
          border = BorderStroke(0.5.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = onDismiss,
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, MetalBorder),
              modifier = Modifier.weight(1f)
            ) {
              Text("Annuler", color = AluminumLight)
            }

            Button(
              onClick = {
                val dur = durationDays.toIntOrNull() ?: 7
                val dep = depositDzd.toLongOrNull() ?: 50000L
                val validClient = if (clientName.isBlank()) "Client Agence" else clientName
                val validPhone = if (clientPhone.isBlank()) "0550 00 11 22" else clientPhone
                val validPermis = if (clientPermis.isBlank()) "P-16/094820" else clientPermis
                onConfirmContract(selectedVehicleId, validClient, validPhone, "109823471890", validPermis, dur, dep)
              },
              enabled = selectedVehicleId.isNotBlank(),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
              modifier = Modifier.weight(1.5f)
            ) {
              Text("Valider Contrat", color = PureWhite, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
          }
        }
      }
    }
  }
}

@Composable
fun RentalVehicleDetailDialog(
  vehicle: RentalVehicle,
  onDismiss: () -> Unit,
  onSendToWorkshop: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.85f),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, MetalBorder)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = MetalCard,
          border = BorderStroke(0.5.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = vehicle.brandModel, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = PureWhite))
              Text(text = "Plaque : ${vehicle.plateNumber} • VIN : ${vehicle.vin}", style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.5.sp))
            }
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite)
            }
          }
        }

        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MetalSurface),
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "Catégorie : ${vehicle.category.label}", style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontWeight = FontWeight.Bold))
                Text(text = "Carburant : ${vehicle.fuelType} • Boîte : ${vehicle.transmission}", style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight))
                Text(text = "Kilométrage Actuel : ${vehicle.currentMileageKm} km", style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontWeight = FontWeight.Bold))
                Text(text = "Dernière Révision : À ${vehicle.maintenanceHistory.firstOrNull()?.mileageAtServiceKm ?: 0} km", style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight))
                Text(text = "Total Dépenses Entretien : ${vehicle.totalMaintenanceSpentDzd} DA", style = MaterialTheme.typography.bodySmall.copy(color = MechanicalOrange, fontWeight = FontWeight.Bold))
              }
            }
          }

          item {
            Text("HISTORIQUE DES RÉPARATIONS DU VÉHICULE", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
          }

          if (vehicle.maintenanceHistory.isEmpty()) {
            item {
              Text("Aucune réparation lourde enregistrée pour ce véhicule récent.", style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight))
            }
          } else {
            items(vehicle.maintenanceHistory) { entry ->
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MetalCard,
                border = BorderStroke(1.dp, MetalBorder),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(entry.interventionType, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite))
                    Text("${entry.costDzd} DA", style = MaterialTheme.typography.bodySmall.copy(color = MechanicalOrange, fontWeight = FontWeight.Bold))
                  }
                  Text("${entry.date} à ${entry.mileageAtServiceKm} km • ${entry.garageName}", style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp))
                }
              }
            }
          }
        }

        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = MetalCard,
          border = BorderStroke(0.5.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = onDismiss,
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, MetalBorder),
              modifier = Modifier.weight(1f)
            ) {
              Text("Fermer", color = AluminumLight)
            }

            Button(
              onClick = onSendToWorkshop,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
              modifier = Modifier.weight(1.3f)
            ) {
              Icon(Icons.Default.Build, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Envoyer Atelier", color = PureWhite, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
          }
        }
      }
    }
  }
}
