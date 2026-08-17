package com.example.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Vehicle
import com.example.ui.components.MekanikPrimaryButton
import com.example.ui.components.MekanikSecondaryButton
import com.example.ui.components.VinSearchBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainNavigationTab
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

@Composable
fun VinSearchScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  modifier: Modifier = Modifier
) {
  val activeVehicle = uiState.activeVehicle

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("vin_search_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Title Section
    item {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            Icons.Default.DirectionsCar,
            contentDescription = null,
            tint = PerformanceRed,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "IDENTIFICATION VÉHICULE",
            style = MaterialTheme.typography.headlineLarge.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp,
              color = PureWhite
            )
          )
        }
        Text(
          text = "Recherche par numéro de châssis (VIN), code moteur ou marque jusqu'en 2025",
          style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
        )
      }
    }

    // Interactive Search Bar
    item {
      VinSearchBar(
        currentQuery = uiState.searchQuery,
        onQueryChange = { viewModel.onSearchQueryChanged(it) },
        onPerformSearch = { viewModel.performVehicleSearch() },
        selectedTab = uiState.searchTabMode,
        onTabSelected = { viewModel.setSearchTabMode(it) },
        onQuickChipClicked = { chipVal ->
          viewModel.onSearchQueryChanged(chipVal)
          viewModel.performVehicleSearch()
        }
      )
    }

    // Vehicle Identification Sheet (Fiche Complète du Véhicule)
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("vehicle_specs_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // Brand & Model Header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = activeVehicle.brand.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                  color = MechanicalOrange,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                )
              )
              Text(
                text = "${activeVehicle.model} (${activeVehicle.exactYear})",
                style = MaterialTheme.typography.headlineMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = PureWhite
                )
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = PerformanceRedDark,
              border = BorderStroke(1.dp, PerformanceRed)
            ) {
              Text(
                text = activeVehicle.engineCode,
                style = MaterialTheme.typography.titleMedium.copy(
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontFamily = FontFamily.Monospace
                ),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Key Specifications Matrix
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(MetalSurface)
              .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            SpecRow("Numéro VIN :", activeVehicle.vin, isMono = true, highlight = true)
            SpecRow("Motorisation / Cylindrée :", activeVehicle.displacement)
            SpecRow("Puissance Fiscale / Réelle :", "${activeVehicle.powerCh} ch (${activeVehicle.powerKw} kW)")
            SpecRow("Boîte de Vitesses :", activeVehicle.gearbox)
            SpecRow("Carburant / Énergie :", activeVehicle.fuelType)
            SpecRow("Plateforme Châssis :", activeVehicle.platform)
            SpecRow("Usine d'Assemblage :", activeVehicle.assemblyPlant)
            SpecRow("Période de Production :", activeVehicle.yearRange)
          }

          // Generation Specific Technical Notes (e.g. Tiguan Facelift / BlueHDi 8mm chain)
          if (activeVehicle.generationNotes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = GraphiteBlack,
              border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.5f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  Icons.Default.Engineering,
                  contentDescription = null,
                  tint = MechanicalOrange,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "NOTE TECHNIQUE CONSTRUCTEUR",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = MechanicalOrange,
                      letterSpacing = 0.8.sp
                    )
                  )
                  Text(
                    text = activeVehicle.generationNotes,
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = AluminumLight,
                      fontSize = 11.5.sp,
                      lineHeight = 15.sp
                    )
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Primary Navigation Buttons: [ Voir Pièces & Schémas ] [ Rapport VIN ]
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            MekanikPrimaryButton(
              text = "VOIR CATALOGUE PIÈCES",
              onClick = { viewModel.setNavigationTab(MainNavigationTab.PIECES) },
              icon = Icons.Default.Build,
              modifier = Modifier.weight(1f),
              testTag = "btn_open_catalog_from_vin"
            )
            MekanikSecondaryButton(
              text = "RAPPORT VIN",
              onClick = { viewModel.generateVinReport(activeVehicle.vin) },
              icon = Icons.Default.ReceiptLong,
              modifier = Modifier.weight(0.7f),
              testTag = "btn_open_report_from_vin"
            )
          }
        }
      }
    }

    // Other Available Models in Database (Couverture Complète)
    item {
      Text(
        text = "AUTRES VÉHICULES DISPONIBLES (${uiState.searchResults.size})",
        style = MaterialTheme.typography.labelMedium.copy(
          color = AluminumMuted,
          letterSpacing = 1.2.sp,
          fontWeight = FontWeight.Bold
        )
      )
    }

    items(uiState.searchResults) { vehicle ->
      VehicleMiniListItem(
        vehicle = vehicle,
        isSelected = vehicle.id == activeVehicle.id,
        onSelect = { viewModel.selectVehicle(vehicle) }
      )
    }
  }
}

@Composable
private fun SpecRow(
  label: String,
  value: String,
  isMono: Boolean = false,
  highlight: Boolean = false
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall.copy(
        color = AluminumMuted,
        fontSize = 12.sp
      )
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall.copy(
        color = if (highlight) PerformanceRedGlow else OffWhite,
        fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
        fontFamily = if (isMono) FontFamily.Monospace else FontFamily.SansSerif,
        fontSize = 12.sp
      )
    )
  }
}

@Composable
private fun VehicleMiniListItem(
  vehicle: Vehicle,
  isSelected: Boolean,
  onSelect: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = if (isSelected) MetalSurface else MetalCard,
    border = BorderStroke(
      1.dp,
      if (isSelected) PerformanceRed else MetalBorder
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onSelect)
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${vehicle.brand} ${vehicle.model}",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = PureWhite
            )
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = MetalBorder
          ) {
            Text(
              text = "${vehicle.exactYear}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.5.sp,
                color = AluminumLight
              ),
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
          }
        }
        Text(
          text = "${vehicle.displacement} • Code Moteur: ${vehicle.engineCode} • ${vehicle.powerCh} ch",
          style = MaterialTheme.typography.bodySmall.copy(
            color = AluminumGray,
            fontSize = 11.sp
          )
        )
      }

      Icon(
        Icons.Default.ChevronRight,
        contentDescription = "Sélectionner",
        tint = if (isSelected) PerformanceRed else AluminumMuted,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}
