package com.example.ui.components

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
import com.example.data.model.ApiDataSourceMode
import com.example.ui.theme.*

enum class SearchTabMode {
  VIN,
  PLAQUE,
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
  onScanCarteGriseVin: (() -> Unit)? = null,
  apiDataSourceMode: ApiDataSourceMode? = null,
  onOpenDevApiManager: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val focusManager = LocalFocusManager.current

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("vin_search_card"),
    shape = RoundedCornerShape(14.dp),
    color = MetalCard,
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Top Header & API Status Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Identification Véhicule & Recherche Rapide",
            color = OffWhite,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
          )
          Text(
            text = "Recherche par VIN 17 caractères, code moteur, plaque ou modèle",
            color = AluminumGray,
            fontSize = 12.sp
          )
        }

        if (apiDataSourceMode != null) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = GraphiteDark,
            border = BorderStroke(1.dp, MetalBorder),
            onClick = { onOpenDevApiManager?.invoke() }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(RoundedCornerShape(4.dp))
                  .background(MekanikValidationGreen)
              )
              Text(
                text = apiDataSourceMode.badgeText,
                color = OffWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
              )
            }
          }
        }
      }

      // 4 Segmented Mode Switchers: [ VIN ] [ PLAQUE ] [ MOTEUR ] [ VÉHICULE ]
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(GraphiteDark)
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
          title = "Plaque",
          icon = Icons.Default.CreditCard,
          isSelected = selectedTab == SearchTabMode.PLAQUE,
          onClick = { onTabSelected(SearchTabMode.PLAQUE) },
          isLocked = true,
          modifier = Modifier.weight(1f)
        )
        SearchTabButton(
          title = "Moteur",
          icon = Icons.Default.Settings,
          isSelected = selectedTab == SearchTabMode.CODE_MOTEUR,
          onClick = { onTabSelected(SearchTabMode.CODE_MOTEUR) },
          modifier = Modifier.weight(1f)
        )
        SearchTabButton(
          title = "Véhicule",
          icon = Icons.Default.Search,
          isSelected = selectedTab == SearchTabMode.MANUELLE,
          onClick = { onTabSelected(SearchTabMode.MANUELLE) },
          modifier = Modifier.weight(1.1f)
        )
      }

      // Dynamic Search Input Box
      when (selectedTab) {
        SearchTabMode.VIN -> {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = currentQuery,
              onValueChange = onQueryChange,
              modifier = Modifier
                .weight(1f)
                .testTag("vin_input_field"),
              placeholder = {
                Text(
                  "Saisir un VIN 17 caractères (ex: WVWZZZ5NZMW123456)",
                  color = AluminumGray,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 13.sp
                )
              },
              leadingIcon = {
                Icon(
                  Icons.Default.QrCodeScanner,
                  contentDescription = "Scanner VIN",
                  tint = MekanikBrandOrange
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
                focusedContainerColor = GraphiteDark,
                unfocusedContainerColor = GraphiteDark,
                focusedBorderColor = MekanikBrandOrange,
                unfocusedBorderColor = MetalBorder,
                focusedTextColor = OffWhite,
                unfocusedTextColor = OffWhite
              ),
              shape = RoundedCornerShape(12.dp),
              singleLine = true,
              textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.5.sp
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

            if (onScanCarteGriseVin != null) {
              MekanikIconButton(
                icon = Icons.Default.CameraAlt,
                contentDescription = "Scanner la Carte Grise",
                onClick = onScanCarteGriseVin,
                isHighlighted = true,
                modifier = Modifier.size(52.dp),
                testTag = "btn_scan_carte_grise_direct"
              )
            }
          }

          // Quick VIN examples chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            QuickFilterChip("VW Tiguan II 2.0 TDI", "WVWZZZ5NZMW123456", onQuickChipClicked)
            QuickFilterChip("Peugeot 308 III", "VF3FPHNSSMY001234", onQuickChipClicked)
            QuickFilterChip("Renault Clio V 1.5 dCi", "VF1RJA00067890123", onQuickChipClicked)
            QuickFilterChip("Jetour Dashing 1.6T", "LVVDB21B9PD048912", onQuickChipClicked)
            QuickFilterChip("Geely Coolray 1.5TD", "L6T7824S8ND109432", onQuickChipClicked)
            QuickFilterChip("Toyota Hilux 2022", "MR0FA3CD000445566", onQuickChipClicked)
          }
        }

        SearchTabMode.PLAQUE -> {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = GraphiteDark,
            border = BorderStroke(1.dp, MetalBorder),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("license_plate_disabled_banner")
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    Icons.Default.Lock,
                    contentDescription = "Verrouillé",
                    tint = MekanikBrandOrange,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Recherche par immatriculation soumise à habilitation",
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                  )
                }
              }

              Text(
                text = "Utilisez le numéro de châssis VIN (17 caractères) ou le scanner photo de Carte Grise pour identifier instantanément le véhicule.",
                color = AluminumGray,
                fontSize = 12.sp,
                lineHeight = 17.sp
              )
            }
          }

          OutlinedTextField(
            value = "",
            onValueChange = {},
            enabled = false,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("license_plate_input"),
            placeholder = {
              Text(
                "Accès fichier national immatriculation en attente d'agrément",
                color = AluminumGray,
                fontSize = 12.5.sp
              )
            },
            leadingIcon = {
              Icon(
                Icons.Default.Lock,
                contentDescription = "Plaque verrouillée",
                tint = AluminumGray
              )
            },
            colors = OutlinedTextFieldDefaults.colors(
              disabledContainerColor = GraphiteDark,
              disabledBorderColor = MetalBorder,
              disabledTextColor = AluminumGray,
              disabledPlaceholderColor = AluminumGray
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
          )
        }

        SearchTabMode.CODE_MOTEUR -> {
          OutlinedTextField(
            value = currentQuery,
            onValueChange = onQueryChange,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("engine_code_input"),
            placeholder = {
              Text(
                "Code moteur (ex: DFGA, DV5RC, K9K 608, 1GD-FTV)",
                color = AluminumGray,
                fontSize = 13.sp
              )
            },
            leadingIcon = {
              Icon(
                Icons.Default.Build,
                contentDescription = "Code Moteur",
                tint = MekanikBrandOrange
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
              focusedContainerColor = GraphiteDark,
              unfocusedContainerColor = GraphiteDark,
              focusedBorderColor = MekanikBrandOrange,
              unfocusedBorderColor = MetalBorder,
              focusedTextColor = OffWhite,
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

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            QuickFilterChip("DFGA (2.0 TDI)", "DFGA", onQuickChipClicked)
            QuickFilterChip("DV5RC (1.5 BlueHDi)", "DV5RC", onQuickChipClicked)
            QuickFilterChip("K9K (1.5 dCi)", "K9K", onQuickChipClicked)
            QuickFilterChip("EA888 (2.0 TSI)", "EA888", onQuickChipClicked)
            QuickFilterChip("1GD-FTV (2.8 D-4D)", "1GD-FTV", onQuickChipClicked)
          }
        }

        SearchTabMode.MANUELLE -> {
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
          }
        }
      }

      // Main Action Search Button (#F07836, 50.dp height, 13.dp radius)
      MekanikPrimaryButton(
        text = when (selectedTab) {
          SearchTabMode.VIN -> "Rechercher & Décoder le VIN"
          SearchTabMode.PLAQUE -> "Passer à la recherche par VIN"
          SearchTabMode.CODE_MOTEUR -> "Identifier ce code moteur"
          SearchTabMode.MANUELLE -> "Ouvrir le catalogue véhicule"
        },
        onClick = {
          focusManager.clearFocus()
          if (selectedTab == SearchTabMode.PLAQUE) {
            onTabSelected(SearchTabMode.VIN)
          } else {
            onPerformSearch()
          }
        },
        icon = Icons.Default.Search,
        modifier = Modifier.fillMaxWidth(),
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
  isLocked: Boolean = false,
  modifier: Modifier = Modifier
) {
  val bgColor = if (isSelected) MekanikBrandOrange else Color.Transparent
  val textColor = if (isSelected) Color.White else AluminumGray

  Box(
    modifier = modifier
      .heightIn(min = 40.dp)
      .clip(RoundedCornerShape(9.dp))
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
        tint = textColor,
        modifier = Modifier.size(15.dp)
      )
      Spacer(modifier = Modifier.width(5.dp))
      Text(
        text = title,
        color = textColor,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
        fontSize = 12.sp
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
    shape = RoundedCornerShape(10.dp),
    color = GraphiteDark,
    border = BorderStroke(1.dp, MetalBorder),
    onClick = { onClick(value) },
    modifier = Modifier.height(34.dp)
  ) {
    Box(
      modifier = Modifier.padding(horizontal = 12.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = label,
        color = OffWhite,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}
