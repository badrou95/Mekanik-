package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class SearchTabMode {
  VIN,
  CODE_MOTEUR,
  MANUELLE
}

@Composable
fun VinSearchBar(
  currentQuery: String,
  onQueryChange: (String) -> Unit,
  onPerformSearch: () -> Unit,
  selectedTab: SearchTabMode,
  onTabSelected: (SearchTabMode) -> Unit,
  onQuickChipClicked: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val focusManager = LocalFocusManager.current

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("vin_search_card"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, MetalBorder),
    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // 3 Mode Switchers: [ VIN ] [ CODE MOTEUR ] [ RECHERCHE VÉHICULE ]
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(GraphiteBlack)
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        SearchTabButton(
          title = "VIN",
          icon = Icons.Default.DirectionsCar,
          isSelected = selectedTab == SearchTabMode.VIN,
          onClick = { onTabSelected(SearchTabMode.VIN) },
          modifier = Modifier.weight(1f)
        )
        SearchTabButton(
          title = "CODE MOTEUR",
          icon = Icons.Default.Settings,
          isSelected = selectedTab == SearchTabMode.CODE_MOTEUR,
          onClick = { onTabSelected(SearchTabMode.CODE_MOTEUR) },
          modifier = Modifier.weight(1.3f)
        )
        SearchTabButton(
          title = "VÉHICULE",
          icon = Icons.Default.Search,
          isSelected = selectedTab == SearchTabMode.MANUELLE,
          onClick = { onTabSelected(SearchTabMode.MANUELLE) },
          modifier = Modifier.weight(1.1f)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Dynamic Search Input Box
      when (selectedTab) {
        SearchTabMode.VIN -> {
          Text(
            text = "NUMÉRO DE CHÂSSIS (17 CARACTÈRES)",
            style = MaterialTheme.typography.labelMedium.copy(
              color = AluminumGray,
              letterSpacing = 1.sp,
              fontWeight = FontWeight.Bold
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = currentQuery,
            onValueChange = onQueryChange,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("vin_input_field"),
            placeholder = {
              Text(
                "Ex: WVWZZZ5NZMW123456",
                color = AluminumMuted,
                fontFamily = FontFamily.Monospace
              )
            },
            leadingIcon = {
              Icon(
                Icons.Default.QrCodeScanner,
                contentDescription = "Scanner VIN",
                tint = PerformanceRed
              )
            },
            trailingIcon = {
              if (currentQuery.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                  Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = AluminumGray)
                }
              }
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MetalSurface,
              unfocusedContainerColor = MetalSurface,
              focusedBorderColor = PerformanceRed,
              unfocusedBorderColor = MetalBorder,
              focusedTextColor = PureWhite,
              unfocusedTextColor = OffWhite
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            ),
            keyboardOptions = KeyboardOptions(
              capitalization = KeyboardCapitalization.Characters,
              imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
              onSearch = {
                focusManager.clearFocus()
                onPerformSearch()
              }
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Quick VIN examples chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            QuickFilterChip("VW Tiguan II 2021", "WVWZZZ5NZMW123456", onQuickChipClicked)
            QuickFilterChip("Peugeot 308 III", "VF3FPHNSSMY001234", onQuickChipClicked)
            QuickFilterChip("Clio V 1.5 dCi", "VF1RJA00067890123", onQuickChipClicked)
            QuickFilterChip("Toyota Hilux 2022", "MR0FA3CD000445566", onQuickChipClicked)
          }
        }

        SearchTabMode.CODE_MOTEUR -> {
          Text(
            text = "CODE MOTEUR OU CYLINDRÉE",
            style = MaterialTheme.typography.labelMedium.copy(
              color = AluminumGray,
              letterSpacing = 1.sp,
              fontWeight = FontWeight.Bold
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = currentQuery,
            onValueChange = onQueryChange,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("engine_code_input"),
            placeholder = {
              Text(
                "Ex: DFGA, DV5RC, K9K 608, 1GD-FTV...",
                color = AluminumMuted
              )
            },
            leadingIcon = {
              Icon(
                Icons.Default.Build,
                contentDescription = "Code Moteur",
                tint = MechanicalOrange
              )
            },
            trailingIcon = {
              if (currentQuery.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                  Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = AluminumGray)
                }
              }
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MetalSurface,
              unfocusedContainerColor = MetalSurface,
              focusedBorderColor = MechanicalOrange,
              unfocusedBorderColor = MetalBorder,
              focusedTextColor = PureWhite,
              unfocusedTextColor = OffWhite
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
              capitalization = KeyboardCapitalization.Characters,
              imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
              onSearch = {
                focusManager.clearFocus()
                onPerformSearch()
              }
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            QuickFilterChip("DFGA (2.0 TDI)", "DFGA", onQuickChipClicked)
            QuickFilterChip("DV5RC (1.5 HDi)", "DV5RC", onQuickChipClicked)
            QuickFilterChip("K9K (1.5 dCi)", "K9K", onQuickChipClicked)
            QuickFilterChip("EA888 (2.0 TSI)", "EA888", onQuickChipClicked)
            QuickFilterChip("1GD-FTV (2.8 D-4D)", "1GD-FTV", onQuickChipClicked)
          }
        }

        SearchTabMode.MANUELLE -> {
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "SÉLECTION RAPIDE DE VÉHICULE (JUSQU'À 2025)",
              style = MaterialTheme.typography.labelMedium.copy(
                color = AluminumGray,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
              )
            )
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              QuickFilterChip("Volkswagen", "Volkswagen", onQuickChipClicked)
              QuickFilterChip("Peugeot", "Peugeot", onQuickChipClicked)
              QuickFilterChip("Renault", "Renault", onQuickChipClicked)
              QuickFilterChip("Audi", "Audi", onQuickChipClicked)
              QuickFilterChip("Toyota", "Toyota", onQuickChipClicked)
              QuickFilterChip("Hyundai / Kia", "Hyundai", onQuickChipClicked)
              QuickFilterChip("Tesla / EV", "Tesla", onQuickChipClicked)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Main Action Search Button
      MekanikPrimaryButton(
        text = when (selectedTab) {
          SearchTabMode.VIN -> "DÉCODER CE VIN"
          SearchTabMode.CODE_MOTEUR -> "RECHERCHER CE MOTEUR"
          SearchTabMode.MANUELLE -> "AFFICHER LE CATALOGUE"
        },
        onClick = {
          focusManager.clearFocus()
          onPerformSearch()
        },
        icon = Icons.Default.Search,
        modifier = Modifier.fillMaxWidth(),
        isOrange = selectedTab == SearchTabMode.CODE_MOTEUR,
        testTag = "submit_search_button"
      )
    }
  }
}

@Composable
private fun SearchTabButton(
  title: String,
  icon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val bgColor = if (isSelected) PerformanceRedDark else Color.Transparent
  val textColor = if (isSelected) PureWhite else AluminumGray

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bgColor)
      .clickable(onClick = onClick)
      .padding(vertical = 8.dp, horizontal = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) PureWhite else AluminumMuted,
        modifier = Modifier.size(14.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
          fontSize = 10.5.sp,
          color = textColor
        )
      )
    }
  }
}

@Composable
private fun QuickFilterChip(
  label: String,
  value: String,
  onClick: (String) -> Unit
) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = MetalSurface,
    border = BorderStroke(1.dp, MetalBorder),
    onClick = { onClick(value) },
    modifier = Modifier.height(28.dp)
  ) {
    Box(
      modifier = Modifier.padding(horizontal = 10.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          color = AluminumLight,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium
        )
      )
    }
  }
}
