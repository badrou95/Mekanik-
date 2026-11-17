package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.vin.VinDecoderEngine
import com.example.data.model.DecodedVehicleSpecs
import com.example.data.model.PresetTestVin
import com.example.data.model.Vehicle
import com.example.ui.components.MekanikPrimaryButton
import com.example.ui.components.MekanikSecondaryButton
import com.example.ui.components.SearchTabMode
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
  val decodedSpecs = uiState.decodedVinSpecs
  val (isVinValid, validationMsg) = remember(uiState.searchQuery) {
    if (uiState.searchTabMode == SearchTabMode.VIN && uiState.searchQuery.isNotEmpty()) {
      VinDecoderEngine.validateVinString(uiState.searchQuery)
    } else {
      Pair(true, "")
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("vin_search_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Title Section & Public API Online Status Badge
    item {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.DirectionsCar,
              contentDescription = null,
              tint = PerformanceRed,
              modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "DÉCODEUR VIN PUBLIC",
              style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                color = PureWhite
              )
            )
          }

          // API Status Chip
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = TechCyanDark.copy(alpha = 0.3f),
            border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.6f))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(TechCyanGlow)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "API NHTSA vPIC",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = TechCyan
                )
              )
            }
          }
        }
        Text(
          text = "Interrogez les spécifications techniques officielles par numéro de châssis 17 caractères",
          style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
        )
      }
    }

    // 1-Tap Preset Test VINs Bar
    item {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "EXEMPLES DE VIN HOMOLOGUÉS (1-CLIC)",
            style = MaterialTheme.typography.labelSmall.copy(
              color = AluminumMuted,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          )
          Text(
            text = "Base 2016-2025",
            style = MaterialTheme.typography.labelSmall.copy(color = PerformanceRedGlow)
          )
        }

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(vertical = 4.dp)
        ) {
          items(VinDecoderEngine.presetTestVins) { preset ->
            PresetVinChip(
              preset = preset,
              isSelected = uiState.searchQuery.equals(preset.vin, ignoreCase = true),
              onClick = { viewModel.loadPresetVin(preset.vin) }
            )
          }
        }
      }
    }

    // Interactive Search & Input Bar
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
        },
        apiDataSourceMode = uiState.apiDataSourceMode,
        onOpenDevApiManager = { viewModel.openDevApiManagerModal() }
      )
    }

    // Real-Time ISO-3779 Character Counter & Validity Feedback
    if (uiState.searchTabMode == SearchTabMode.VIN && uiState.searchQuery.isNotEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(
            1.dp,
            if (isVinValid) TechCyan.copy(alpha = 0.5f) else PerformanceRed.copy(alpha = 0.5f)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Icon(
                if (isVinValid) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isVinValid) TechCyanGlow else PerformanceRed,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = if (isVinValid) "Format VIN ISO 3779 Conforme" else "Contrôle Format VIN",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isVinValid) PureWhite else PerformanceRed
                  )
                )
                Text(
                  text = validationMsg,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = AluminumGray,
                    fontSize = 11.sp
                  )
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (uiState.searchQuery.length == 17) TechCyanDark else GraphiteBlack,
              border = BorderStroke(
                1.dp,
                if (uiState.searchQuery.length == 17) TechCyan else MetalBorder
              )
            ) {
              Text(
                text = "${uiState.searchQuery.length} / 17",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = if (uiState.searchQuery.length == 17) TechCyanGlow else AluminumLight
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }

    // Decoding Loading Radar Animation
    if (uiState.isVinDecoding) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.7f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            val infiniteTransition = rememberInfiniteTransition(label = "decoding_spin")
            val rotation by infiniteTransition.animateFloat(
              initialValue = 0f,
              targetValue = 360f,
              animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
              ),
              label = "radar_spin"
            )

            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier.size(64.dp)
            ) {
              CircularProgressIndicator(
                color = TechCyan,
                trackColor = MetalBorder,
                strokeWidth = 3.dp,
                modifier = Modifier.fillMaxSize()
              )
              Icon(
                Icons.Default.CloudSync,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(28.dp)
              )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "INTERROGATION DE L'API AUTOMOBILE PUBLIQUE",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TechCyanGlow,
                letterSpacing = 0.8.sp
              )
            )
            Text(
              text = "Récupération des données NHTSA vPIC & Décodage ISO-3779...",
              style = MaterialTheme.typography.bodySmall.copy(
                color = AluminumLight,
                fontSize = 12.sp
              )
            )
          }
        }
      }
    }

    // Error Banner (if any)
    if (uiState.vinDecodeError != null) {
      item {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = PerformanceRedDark.copy(alpha = 0.4f),
          border = BorderStroke(1.dp, PerformanceRed),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.ErrorOutline,
              contentDescription = null,
              tint = PerformanceRed,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Information Décodage",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = PureWhite
                )
              )
              Text(
                text = uiState.vinDecodeError,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumLight,
                  fontSize = 12.sp
                )
              )
            }
          }
        }
      }
    }

    // 17-Character Interactive VIN Anatomy Breakdown Card
    if (decodedSpecs != null) {
      item {
        VinAnatomyBreakdownCard(specs = decodedSpecs)
      }
    }

    // Comprehensive Decoded Technical Specifications Card
    if (decodedSpecs != null) {
      item {
        DecodedTechnicalSpecsFullCard(
          specs = decodedSpecs,
          onApplyAsActive = { viewModel.applyDecodedVehicleAsActive(decodedSpecs) },
          onOpenParts = { viewModel.setNavigationTab(MainNavigationTab.PIECES) },
          onOpenAi = { viewModel.setNavigationTab(MainNavigationTab.AI_DIAGNOSTIC) },
          onGenerateReport = { viewModel.generateVinReport(decodedSpecs.vin) }
        )
      }
    } else {
      // Default Vehicle Identification Sheet (Active Vehicle Fallback)
      item {
        DefaultActiveVehicleCard(
          activeVehicle = activeVehicle,
          viewModel = viewModel
        )
      }
    }

    // Recent VIN Decodes & History
    if (uiState.vinSearchHistory.isNotEmpty()) {
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "HISTORIQUE RÉCENT DES DÉCODAGES (${uiState.vinSearchHistory.size})",
            style = MaterialTheme.typography.labelMedium.copy(
              color = AluminumMuted,
              letterSpacing = 1.2.sp,
              fontWeight = FontWeight.Bold
            )
          )

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            uiState.vinSearchHistory.forEach { histItem ->
              RecentDecodeHistoryItem(
                specs = histItem,
                isSelected = histItem.vin == (decodedSpecs?.vin ?: activeVehicle.vin),
                onClick = { viewModel.loadPresetVin(histItem.vin) }
              )
            }
          }
        }
      }
    }

    // Dynamic Real-Time Vehicle Filtering Panel
    item {
      DynamicVehicleFilterPanel(
        filter = uiState.vehicleDynamicFilter,
        matchingCount = uiState.searchResults.size,
        totalCount = com.example.data.repository.MekanikRepository.sampleVehicles.size,
        onUpdateFilter = { transform -> viewModel.updateVehicleDynamicFilter(transform) },
        onResetFilters = { viewModel.resetVehicleDynamicFilters() }
      )
    }

    // Filtered Models Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "MODÈLES CATALOGUE FILTRÉS EN TEMPS RÉEL (${uiState.searchResults.size})",
          style = MaterialTheme.typography.labelMedium.copy(
            color = AluminumMuted,
            letterSpacing = 1.1.sp,
            fontWeight = FontWeight.Bold
          )
        )
        if (uiState.vehicleDynamicFilter.activeFilterCount > 0) {
          Text(
            text = "${uiState.vehicleDynamicFilter.activeFilterCount} filtre(s) actif(s)",
            style = MaterialTheme.typography.labelSmall.copy(
              color = TechCyanGlow,
              fontWeight = FontWeight.Bold
            )
          )
        }
      }
    }

    if (uiState.searchResults.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.5f))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              Icons.Default.FilterAltOff,
              contentDescription = null,
              tint = MechanicalOrange,
              modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Aucun véhicule ne correspond à cette combinaison de filtres",
              style = MaterialTheme.typography.titleSmall.copy(
                color = PureWhite,
                fontWeight = FontWeight.Bold
              )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Élargissez la marque, l'énergie ou la boîte de vitesses pour afficher les modèles compatibles.",
              style = MaterialTheme.typography.bodySmall.copy(
                color = AluminumGray,
                fontSize = 11.5.sp
              )
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
              onClick = { viewModel.resetVehicleDynamicFilters() },
              border = BorderStroke(1.dp, TechCyan),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Refresh, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Réinitialiser tous les filtres", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
            }
          }
        }
      }
    } else {
      items(uiState.searchResults) { vehicle ->
        VehicleMiniListItem(
          vehicle = vehicle,
          isSelected = vehicle.id == activeVehicle.id,
          onSelect = { viewModel.selectVehicle(vehicle) }
        )
      }
    }
  }
}

@Composable
private fun DynamicVehicleFilterPanel(
  filter: com.example.data.model.VehicleDynamicFilterState,
  matchingCount: Int,
  totalCount: Int,
  onUpdateFilter: ((com.example.data.model.VehicleDynamicFilterState) -> com.example.data.model.VehicleDynamicFilterState) -> Unit,
  onResetFilters: () -> Unit
) {
  val allVehicles = remember { com.example.data.repository.MekanikRepository.sampleVehicles }
  val availableBrands = remember {
    listOf("TOUTES") + allVehicles.map { it.brand }.distinct().sorted()
  }
  val fuelOptions = listOf("TOUTES", "Diesel", "Essence", "Hybride / EV")
  val gearboxOptions = listOf("TOUTES", "Auto / DSG / EAT", "Manuelle (BVM)")
  val yearOptions = listOf("TOUTES", "2022+", "2019-2021", "≤ 2018")

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("dynamic_vehicle_filter_panel"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.55f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Header + Live Counter + Reset
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            Icons.Default.Tune,
            contentDescription = null,
            tint = TechCyan,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "FILTRAGE DYNAMIQUE VÉHICULES (TEMPS RÉEL)",
              style = MaterialTheme.typography.labelMedium.copy(
                color = PureWhite,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.6.sp
              )
            )
            Text(
              text = "$matchingCount sur $totalCount véhicules correspondent aux critères",
              style = MaterialTheme.typography.labelSmall.copy(
                color = TechCyanGlow,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            )
          }
        }

        if (filter.activeFilterCount > 0) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = PerformanceRedDark,
            border = BorderStroke(1.dp, PerformanceRed),
            modifier = Modifier
              .clickable { onResetFilters() }
              .testTag("reset_vehicle_filters_btn")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                Icons.Default.ClearAll,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Réinitialiser (${filter.activeFilterCount})",
                color = PureWhite,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // Instant Text Filter (Modèle, Code Moteur, Plateforme, VIN)
      OutlinedTextField(
        value = filter.textQuery,
        onValueChange = { q -> onUpdateFilter { it.copy(textQuery = q) } },
        placeholder = {
          Text(
            text = "Filtrer en direct : Marque, Modèle, Code Moteur (DFGA, K9K, DV5RC), Plateforme (MQB)...",
            color = AluminumMuted,
            fontSize = 11.5.sp
          )
        },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
        },
        trailingIcon = {
          if (filter.textQuery.isNotEmpty()) {
            IconButton(onClick = { onUpdateFilter { it.copy(textQuery = "") } }) {
              Icon(Icons.Default.Close, contentDescription = "Effacer", tint = AluminumLight, modifier = Modifier.size(16.dp))
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = GraphiteBlack,
          unfocusedContainerColor = GraphiteBlack,
          focusedBorderColor = TechCyan,
          unfocusedBorderColor = MetalBorder,
          focusedTextColor = PureWhite,
          unfocusedTextColor = OffWhite
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("vehicle_dynamic_search_input")
      )

      // 1. Brand Facet Row with Dynamic Real-Time Counts
      Text(
        text = "CONSTRUCTEUR :",
        style = MaterialTheme.typography.labelSmall.copy(
          color = AluminumMuted,
          fontWeight = FontWeight.Bold,
          fontSize = 9.5.sp
        )
      )
      LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(availableBrands) { brand ->
          val isSelected = filter.selectedBrand.equals(brand, ignoreCase = true)
          val countForBrand = remember(filter, brand) {
            allVehicles.count { v ->
              (brand == "TOUTES" || v.brand.equals(brand, ignoreCase = true)) &&
                (filter.selectedFuelType == "TOUTES" ||
                  (filter.selectedFuelType == "Diesel" && (v.fuelType.contains("Diesel", true) || v.displacement.contains("TDI", true) || v.displacement.contains("HDi", true) || v.displacement.contains("dCi", true))) ||
                  (filter.selectedFuelType == "Essence" && (v.fuelType.contains("Essence", true) || v.displacement.contains("TSI", true) || v.displacement.contains("PureTech", true) || v.displacement.contains("TCe", true))) ||
                  (filter.selectedFuelType == "Hybride / EV" && (v.isEv || v.fuelType.contains("Électrique", true) || v.fuelType.contains("Hybride", true))))
            }
          }
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) PerformanceRed else GraphiteBlack,
            border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
            modifier = Modifier.clickable {
              onUpdateFilter { it.copy(selectedBrand = brand) }
            }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (brand == "TOUTES") "Toutes Marques" else brand,
                color = if (isSelected) PureWhite else AluminumLight,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                fontSize = 10.5.sp
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = "($countForBrand)",
                color = if (isSelected) PureWhite else TechCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 9.5.sp
              )
            }
          }
        }
      }

      // 2. Fuel / Energy + Gearbox + Year Facets
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        fuelOptions.forEach { fuel ->
          val isSelected = filter.selectedFuelType == fuel
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) TechCyanDark else GraphiteBlack,
            border = BorderStroke(1.dp, if (isSelected) TechCyan else MetalBorder),
            modifier = Modifier.clickable {
              onUpdateFilter { it.copy(selectedFuelType = fuel) }
            }
          ) {
            Text(
              text = if (fuel == "TOUTES") "⚡ Toutes Énergies" else "⛽ $fuel",
              color = if (isSelected) PureWhite else AluminumLight,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 10.sp,
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
            )
          }
        }

        gearboxOptions.drop(1).forEach { gb ->
          val isSelected = filter.selectedGearboxType == gb
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) MechanicalOrangeDark else GraphiteBlack,
            border = BorderStroke(1.dp, if (isSelected) MechanicalOrange else MetalBorder),
            modifier = Modifier.clickable {
              onUpdateFilter {
                it.copy(selectedGearboxType = if (isSelected) "TOUTES" else gb)
              }
            }
          ) {
            Text(
              text = "⚙️ $gb",
              color = if (isSelected) PureWhite else AluminumLight,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 10.sp,
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
            )
          }
        }

        yearOptions.drop(1).forEach { yr ->
          val isSelected = filter.selectedYearBracket == yr
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) PerformanceRedDark else GraphiteBlack,
            border = BorderStroke(1.dp, if (isSelected) PerformanceRed else MetalBorder),
            modifier = Modifier.clickable {
              onUpdateFilter {
                it.copy(selectedYearBracket = if (isSelected) "TOUTES" else yr)
              }
            }
          ) {
            Text(
              text = "📅 $yr",
              color = if (isSelected) PureWhite else AluminumLight,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 10.sp,
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun PresetVinChip(
  preset: PresetTestVin,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = if (isSelected) PerformanceRedDark else MetalCard,
    border = BorderStroke(
      1.dp,
      if (isSelected) PerformanceRed else MetalBorder
    ),
    modifier = Modifier.clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(text = preset.flagEmoji, fontSize = 14.sp)
      Spacer(modifier = Modifier.width(6.dp))
      Column {
        Text(
          text = preset.label,
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = if (isSelected) PureWhite else OffWhite,
            fontSize = 11.sp
          )
        )
        Text(
          text = preset.vin,
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            color = if (isSelected) MechanicalOrange else AluminumMuted,
            fontSize = 9.5.sp
          )
        )
      }
    }
  }
}

@Composable
private fun VinAnatomyBreakdownCard(specs: DecodedVehicleSpecs) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("vin_anatomy_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            Icons.Default.Pin,
            contentDescription = null,
            tint = TechCyan,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "ANATOMIE DU NUMÉRO DE CHÂSSIS (ISO 3779)",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = PureWhite,
              letterSpacing = 0.8.sp
            )
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = TechCyanDark.copy(alpha = 0.4f),
          border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.5f))
        ) {
          Text(
            text = "17 CARACTÈRES",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = TechCyanGlow
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 17-Char Block Segments
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(GraphiteBlack)
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
      ) {
        VinCharBlock(
          chars = specs.wmi,
          label = "WMI",
          bgColor = TechCyanDark,
          borderColor = TechCyan,
          modifier = Modifier.weight(3f)
        )
        VinCharBlock(
          chars = specs.vds,
          label = "VDS",
          bgColor = MechanicalOrangeDark,
          borderColor = MechanicalOrange,
          modifier = Modifier.weight(5f)
        )
        VinCharBlock(
          chars = specs.checkDigit,
          label = "CLÉ",
          bgColor = PerformanceRedDark,
          borderColor = PerformanceRed,
          modifier = Modifier.weight(1f)
        )
        VinCharBlock(
          chars = specs.modelYearChar,
          label = "AN",
          bgColor = TechCyanDark,
          borderColor = TechCyan,
          modifier = Modifier.weight(1f)
        )
        VinCharBlock(
          chars = specs.plantCode,
          label = "USN",
          bgColor = MetalSurface,
          borderColor = MetalBorder,
          modifier = Modifier.weight(1f)
        )
        VinCharBlock(
          chars = specs.sequentialNumber,
          label = "N° SÉRIE",
          bgColor = MetalCard,
          borderColor = MetalBorder,
          modifier = Modifier.weight(6f)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Explanatory breakdown details
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(MetalSurface)
          .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        BreakdownDetailRow(
          title = "WMI (Pos 1-3) :",
          value = "${specs.wmi} • ${specs.wmiManufacturerName} (${specs.wmiOriginCountry})",
          tagColor = TechCyan
        )
        BreakdownDetailRow(
          title = "VDS (Pos 4-8) :",
          value = "${specs.vds} • ${specs.vdsDescription}",
          tagColor = MechanicalOrange
        )
        BreakdownDetailRow(
          title = "Contrôle & Année (Pos 9-10) :",
          value = "Clé '${specs.checkDigit}' (Conforme) • Millésime ${specs.modelYear} (Code '${specs.modelYearChar}')",
          tagColor = PerformanceRedGlow
        )
        BreakdownDetailRow(
          title = "Usine & Série (Pos 11-17) :",
          value = "Site '${specs.plantCode}' (${specs.plantLocation}) • Séquence ${specs.sequentialNumber}",
          tagColor = AluminumLight
        )
      }
    }
  }
}

@Composable
private fun VinCharBlock(
  chars: String,
  label: String,
  bgColor: Color,
  borderColor: Color,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(bgColor)
      .border(1.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
      .padding(vertical = 4.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = chars,
      style = MaterialTheme.typography.bodyMedium.copy(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Black,
        color = PureWhite,
        fontSize = 11.sp
      ),
      maxLines = 1
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 8.sp,
        color = AluminumMuted,
        fontWeight = FontWeight.Bold
      )
    )
  }
}

@Composable
private fun BreakdownDetailRow(
  title: String,
  value: String,
  tagColor: Color
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.Top
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.bodySmall.copy(
        fontWeight = FontWeight.Bold,
        color = tagColor,
        fontSize = 11.5.sp
      ),
      modifier = Modifier.width(130.dp)
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall.copy(
        color = PureWhite,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Medium
      ),
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun DecodedTechnicalSpecsFullCard(
  specs: DecodedVehicleSpecs,
  onApplyAsActive: () -> Unit,
  onOpenParts: () -> Unit,
  onOpenAi: () -> Unit,
  onGenerateReport: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("decoded_specs_card"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, PerformanceRed.copy(alpha = 0.6f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Brand, Model, Year, Source Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = specs.make.uppercase(),
              style = MaterialTheme.typography.labelMedium.copy(
                color = PerformanceRedGlow,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
              )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = GraphiteBlack,
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Text(
                text = "${specs.modelYear}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = PureWhite,
                  fontSize = 10.sp
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Text(
            text = "${specs.model} ${specs.trimLevel}",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Black,
              color = PureWhite
            )
          )
          Text(
            text = "${specs.bodyClass} • ${specs.wmiOriginCountry}",
            style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = PerformanceRedDark,
          border = BorderStroke(1.dp, PerformanceRed)
        ) {
          Text(
            text = specs.engineCode,
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

      // Data Source Stamp
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = GraphiteBlack,
        border = BorderStroke(1.dp, if (specs.isPublicApiSuccess) TechCyan.copy(alpha = 0.4f) else MetalBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              if (specs.isPublicApiSuccess) Icons.Default.Public else Icons.Default.Storage,
              contentDescription = null,
              tint = if (specs.isPublicApiSuccess) TechCyan else MechanicalOrange,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = specs.dataSource,
              style = MaterialTheme.typography.labelSmall.copy(
                color = PureWhite,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium
              )
            )
          }
          Text(
            text = specs.decodeTimestamp,
            style = MaterialTheme.typography.labelSmall.copy(
              color = AluminumMuted,
              fontSize = 9.5.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Categorized Specs Grid
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(MetalSurface)
          .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Powertrain Section
        SectionHeader("GROUPE MOTOPROPULSEUR", Icons.Default.Engineering, MechanicalOrange)
        SpecRow("Cylindrée & Type :", specs.displacementL)
        SpecRow("Architecture Moteur :", specs.engineCylinders)
        SpecRow("Puissance Homologuée :", "${specs.enginePowerHp} (${specs.enginePowerKw})", highlight = true)
        SpecRow("Type d'Énergie :", specs.fuelTypePrimary)
        SpecRow("Suralimentation / Turbo :", if (specs.isTurboCharged) "Turbocompresseur Haute Pression" else "Atmosphérique")
        SpecRow("Niveau Électrification :", specs.electrificationLevel)

        Divider(color = MetalBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

        // Drivetrain Section
        SectionHeader("TRANSMISSION & CHÂSSIS", Icons.Default.Speed, TechCyan)
        SpecRow("Transmission / Motricité :", specs.driveType)
        SpecRow("Boîte de Vitesses :", specs.transmissionStyle)
        SpecRow("Système de Freinage :", specs.brakeSystemType)
        SpecRow("Direction Assistée :", specs.steeringType)
        SpecRow("Classe de Poids (GVWR) :", specs.grossVehicleWeightRating)

        Divider(color = MetalBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

        // Assembly & Manufacturing Section
        SectionHeader("PRODUCTION & HOMOLOGATION", Icons.Default.Factory, PerformanceRedGlow)
        SpecRow("Numéro VIN :", specs.vin, isMono = true, highlight = true)
        SpecRow("Site d'Assemblage :", specs.plantLocation)
        SpecRow("Pays Fabricant :", specs.plantCountry)
        SpecRow("Constructeur Officiel :", specs.plantCompanyName)
      }

      // Expanded VIN Section 1: Trim Levels & PR/OPR Equipment Codes
      if (specs.prOprEquipmentCodes.isNotEmpty()) {
        Spacer(modifier = Modifier.height(14.dp))
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GraphiteBlack)
            .border(1.dp, TechCyan.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          SectionHeader("FINITION EXACTE & CODES ÉQUIPEMENTS USINE (PR / OPR)", Icons.Default.Verified, TechCyan)
          Text(
            text = "Sous-modèle décodé : ${specs.trimSubModelCode}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = PureWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            specs.prOprEquipmentCodes.forEach { code ->
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = TechCyanDark.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, TechCyan)
              ) {
                Text(
                  text = code,
                  color = TechCyanGlow,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  fontSize = 10.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }

      // Expanded VIN Section 2: Factory Optional Packages
      if (specs.optionalPackages.isNotEmpty()) {
        Spacer(modifier = Modifier.height(12.dp))
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MetalSurface)
            .border(1.dp, MechanicalOrange.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          SectionHeader("PACKS D'OPTIONS USINE & IMPACT PIÈCES DÉTACHÉES (${specs.optionalPackages.size})", Icons.Default.Tune, MechanicalOrange)
          specs.optionalPackages.forEach { pkg ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = GraphiteBlack,
              border = BorderStroke(1.dp, MetalBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = pkg.packageName,
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    modifier = Modifier.weight(1f)
                  )
                  Surface(shape = RoundedCornerShape(4.dp), color = MechanicalOrangeDark) {
                    Text(
                      text = pkg.optionCode,
                      color = PureWhite,
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.sp,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = "Équipements : ${pkg.includedEquipment}",
                  color = AluminumLight,
                  fontSize = 10.5.sp
                )
                Text(
                  text = "Impact Pièces OEM : ${pkg.mechanicalImpactNote}",
                  color = MechanicalOrangeGlow,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.5.sp
                )
              }
            }
          }
        }
      }

      // Expanded VIN Section 3: Manufacturer Recalls & Technical Service Bulletins (TSB)
      if (specs.manufacturerRecalls.isNotEmpty() || specs.technicalServiceBulletins.isNotEmpty()) {
        Spacer(modifier = Modifier.height(12.dp))
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF221114))
            .border(1.dp, PerformanceRed, RoundedCornerShape(12.dp))
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          SectionHeader(
            "RAPPELS CONSTRUCTEUR (${specs.manufacturerRecalls.size}) & BULLETINS TSB (${specs.technicalServiceBulletins.size})",
            Icons.Default.WarningAmber,
            PerformanceRedGlow
          )

          specs.manufacturerRecalls.forEach { recall ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = GraphiteBlack,
              border = BorderStroke(1.dp, PerformanceRed.copy(alpha = 0.7f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(shape = RoundedCornerShape(4.dp), color = PerformanceRed) {
                    Text(
                      text = "RAPPEL • ${recall.campaignCode}",
                      color = PureWhite,
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.5.sp,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                  Text(
                    text = recall.statusBadge,
                    color = WarningYellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp
                  )
                }
                Text(
                  text = recall.title,
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp
                )
                Text(
                  text = "Risque : ${recall.riskDescription}",
                  color = AluminumLight,
                  fontSize = 10.5.sp
                )
                Text(
                  text = "Action Atelier : ${recall.correctiveAction}",
                  color = ElectricGreen,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.5.sp
                )
                Text(
                  text = "Réf. OEM Concernée : ${recall.concernedOemPartNumber}",
                  color = MechanicalOrange,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.5.sp
                )
              }
            }
          }

          specs.technicalServiceBulletins.forEach { tsb ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MetalCard,
              border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.5f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(shape = RoundedCornerShape(4.dp), color = TechCyanDark) {
                    Text(
                      text = "BULLETIN TSB • ${tsb.tsbNumber}",
                      color = TechCyanGlow,
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.5.sp,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                  Text(
                    text = tsb.systemCategory,
                    color = AluminumLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  )
                }
                Text(
                  text = "Symptôme : ${tsb.symptom}",
                  color = PureWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
                Text(
                  text = "Cause : ${tsb.rootCause}",
                  color = AluminumGray,
                  fontSize = 10.5.sp
                )
                Text(
                  text = "Solution OEM : ${tsb.workshopSolution} (Réf : ${tsb.recommendedOemPartNumber})",
                  color = TechCyan,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.5.sp
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Primary Navigation Buttons: [ Voir Pièces ] [ Diagnostic IA ] [ Rapport ]
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MekanikPrimaryButton(
          text = "CATALOGUE PIÈCES",
          onClick = {
            onApplyAsActive()
            onOpenParts()
          },
          icon = Icons.Default.Build,
          modifier = Modifier.weight(1.2f),
          testTag = "btn_open_catalog_from_decoded_vin"
        )
        MekanikSecondaryButton(
          text = "DIAG IA",
          onClick = {
            onApplyAsActive()
            onOpenAi()
          },
          icon = Icons.Default.Psychology,
          modifier = Modifier.weight(0.8f),
          testTag = "btn_open_ai_from_decoded_vin"
        )
        IconButton(
          onClick = onGenerateReport,
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MetalSurface)
            .border(1.dp, MetalBorder, RoundedCornerShape(10.dp))
        ) {
          Icon(
            Icons.Default.ReceiptLong,
            contentDescription = "Rapport Certifié",
            tint = PerformanceRedGlow
          )
        }
      }
    }
  }
}

@Composable
private fun SectionHeader(title: String, icon: ImageVector, color: Color) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
  ) {
    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Bold,
        color = color,
        letterSpacing = 0.8.sp,
        fontSize = 10.5.sp
      )
    )
  }
}

@Composable
private fun RecentDecodeHistoryItem(
  specs: DecodedVehicleSpecs,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = if (isSelected) MetalSurface else MetalCard,
    border = BorderStroke(1.dp, if (isSelected) TechCyan else MetalBorder),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${specs.make} ${specs.model}",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = PureWhite
            )
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = GraphiteBlack
          ) {
            Text(
              text = "${specs.modelYear}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.5.sp,
                color = TechCyanGlow
              ),
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
          }
        }
        Text(
          text = "${specs.vin} • ${specs.displacementL} • ${specs.enginePowerHp}",
          style = MaterialTheme.typography.bodySmall.copy(
            fontFamily = FontFamily.Monospace,
            color = AluminumGray,
            fontSize = 10.5.sp
          )
        )
      }

      Icon(
        Icons.Default.ChevronRight,
        contentDescription = "Sélectionner",
        tint = if (isSelected) TechCyan else AluminumMuted,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}

@Composable
private fun DefaultActiveVehicleCard(
  activeVehicle: Vehicle,
  viewModel: MekanikViewModel
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("vehicle_specs_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
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
