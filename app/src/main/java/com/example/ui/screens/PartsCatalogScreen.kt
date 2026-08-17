package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.repository.MekanikRepository
import com.example.ui.components.ExplodedSchemaCanvas
import com.example.ui.components.MekanikPrimaryButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainNavigationTab
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

@Composable
fun PartsCatalogScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  onNavigateToVendors: () -> Unit,
  modifier: Modifier = Modifier
) {
  val activeVehicle = uiState.activeVehicle
  val selectedPart = uiState.selectedPart

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("parts_catalog_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Active Vehicle Context Banner
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MetalCard,
        border = BorderStroke(1.dp, MetalBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PerformanceRedDark),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.DirectionsCar,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "${activeVehicle.brand} ${activeVehicle.model} (${activeVehicle.exactYear})",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = PureWhite,
                  fontSize = 14.sp
                )
              )
              Text(
                text = "Moteur : ${activeVehicle.engineCode} • ${activeVehicle.displacement} • VIN : ${activeVehicle.vin.take(8)}...",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumGray,
                  fontSize = 11.sp
                )
              )
            }
          }

          TextButton(onClick = { viewModel.setNavigationTab(MainNavigationTab.RECHERCHE) }) {
            Text(
              text = "Changer",
              color = MechanicalOrange,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      }
    }

    // Category System Selector (Horizontal Scrolling Chips)
    item {
      Column {
        Text(
          text = "SYSTÈMES & ORGANES VÉHICULE",
          style = MaterialTheme.typography.labelMedium.copy(
            color = AluminumMuted,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
          )
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(SystemCategory.values()) { category ->
            val isSelected = uiState.selectedSystemCategory == category
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) PerformanceRed else MetalSurface,
              border = BorderStroke(
                1.dp,
                if (isSelected) PerformanceRedGlow else MetalBorder
              ),
              onClick = { viewModel.updateCategory(category) }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = category.label,
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                    color = if (isSelected) PureWhite else AluminumLight,
                    fontSize = 12.sp
                  )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = CircleShape,
                  color = if (isSelected) GraphiteDark else MetalCard
                ) {
                  Text(
                    text = "${category.count}",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 9.sp,
                      color = if (isSelected) PerformanceRedGlow else AluminumMuted,
                      fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // Exploded Technical Schematic (ETKA / Partslink24 style)
    item {
      ExplodedSchemaCanvas(
        schema = uiState.activeSchema,
        selectedCalloutNumber = uiState.selectedHotspotNumber,
        onHotspotClicked = { hotspot ->
          viewModel.selectHotspot(hotspot)
        }
      )
    }

    // Selected Part Card (RÉFÉRENCE CONSTRUCTEUR & ÉQUIVALENCES)
    if (selectedPart != null) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("part_detail_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.5.dp, PerformanceRed.copy(alpha = 0.8f))
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            // Part Title & Callout Badge
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                Surface(
                  shape = CircleShape,
                  color = PerformanceRed,
                  modifier = Modifier.size(26.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(
                      text = "${selectedPart.schemaCalloutNumber}",
                      style = MaterialTheme.typography.labelMedium.copy(
                        color = PureWhite,
                        fontWeight = FontWeight.Black
                      )
                    )
                  }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = selectedPart.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                      fontWeight = FontWeight.Black,
                      color = PureWhite,
                      fontSize = 16.sp
                    )
                  )
                  Text(
                    text = "Position : ${selectedPart.position}",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = AluminumGray,
                      fontSize = 11.5.sp
                    )
                  )
                }
              }

              Text(
                text = "~ ${selectedPart.priceEstimatedDzd} DA",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = MechanicalOrangeGlow,
                  fontSize = 15.sp
                )
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Référence Constructeur OEM (High Visual Prominence)
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFF2C1518),
              border = BorderStroke(1.dp, PerformanceRed),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "RÉFÉRENCE CONSTRUCTEUR ORIGINE (OEM)",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = PerformanceRedGlow,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                  )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = selectedPart.oemReference,
                    style = MaterialTheme.typography.headlineMedium.copy(
                      fontWeight = FontWeight.Black,
                      color = PureWhite,
                      fontFamily = FontFamily.Monospace,
                      letterSpacing = 1.2.sp
                    )
                  )
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PerformanceRed
                  ) {
                    Text(
                      text = "OE DIRECT",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                      ),
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                if (selectedPart.oemAlternatives.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "Réf. Antérieures / Remplacées : ${selectedPart.oemAlternatives.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = AluminumLight,
                      fontSize = 10.5.sp,
                      fontFamily = FontFamily.Monospace
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Compatibility & Year Range Badges
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MetalSurface,
                border = BorderStroke(0.5.dp, ElectricGreen),
                modifier = Modifier.weight(1f)
              ) {
                Row(
                  modifier = Modifier.padding(6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = ElectricGreen,
                    modifier = Modifier.size(13.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = selectedPart.compatibilityBadge,
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = ElectricGreen,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MetalSurface,
                border = BorderStroke(0.5.dp, MetalBorder),
                modifier = Modifier.weight(1f)
              ) {
                Text(
                  text = selectedPart.yearCompatibility,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = AluminumLight,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Medium
                  ),
                  modifier = Modifier.padding(6.dp),
                  maxLines = 1
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Références Équivalentes (Cross-References: Brembo, Bosch, Ferodo, TRW, ATE, Valeo)
            Text(
              text = "RÉFÉRENCES ÉQUIVALENTES (CROSS-REFERENCES)",
              style = MaterialTheme.typography.labelMedium.copy(
                color = AluminumMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MetalSurface)
                .padding(10.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              selectedPart.crossReferences.forEach { crossRef ->
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = GraphiteBlack,
                      border = BorderStroke(0.5.dp, MetalBorder)
                    ) {
                      Text(
                        text = crossRef.brand,
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = MechanicalOrange,
                          fontWeight = FontWeight.Bold,
                          fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = crossRef.reference,
                      style = MaterialTheme.typography.bodyMedium.copy(
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                      )
                    )
                  }

                  Text(
                    text = crossRef.qualityGrade,
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = AluminumMuted,
                      fontSize = 10.sp
                    )
                  )
                }
              }
            }

            // Technical Specs (Dimensions mm)
            if (selectedPart.technicalSpecs.isNotEmpty()) {
              Spacer(modifier = Modifier.height(12.dp))
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(GraphiteBlack)
                  .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                selectedPart.technicalSpecs.forEach { (key, value) ->
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = key,
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = AluminumMuted,
                        fontSize = 11.sp
                      )
                    )
                    Text(
                      text = value,
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = AluminumLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                      )
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Available Vendors Section
            Text(
              text = "VENDEURS DISPONIBLES EN ALGÉRIE",
              style = MaterialTheme.typography.labelMedium.copy(
                color = AluminumMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            )
            Spacer(modifier = Modifier.height(8.dp))

            val associatedStock = MekanikRepository.vendorStockList.filter { it.partId == selectedPart.id }
            if (associatedStock.isNotEmpty()) {
              associatedStock.forEach { stock ->
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = MetalSurface,
                  border = BorderStroke(1.dp, MetalBorder),
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text(
                        text = stock.vendorName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                          fontWeight = FontWeight.Bold,
                          color = PureWhite
                        )
                      )
                      Text(
                        text = "${stock.wilaya} • Marque : ${stock.brand} • Stock : ${stock.stockQuantity} unités",
                        style = MaterialTheme.typography.bodySmall.copy(
                          color = AluminumGray,
                          fontSize = 11.sp
                        )
                      )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                      Text(
                        text = "${stock.priceDzd} DA",
                        style = MaterialTheme.typography.titleMedium.copy(
                          fontWeight = FontWeight.Black,
                          color = MechanicalOrange
                        )
                      )
                      Text(
                        text = stock.phone,
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = AluminumMuted,
                          fontSize = 10.sp
                        )
                      )
                    }
                  }
                }
              }
            } else {
              Text(
                text = "Contactez le réseau de grossistes Mekanik+ pour réserver cette référence.",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
              )
            }
          }
        }
      }
    }

    // Other Parts in this System Category
    item {
      Text(
        text = "TOUTES LES PIÈCES DU SYSTÈME (${uiState.partsForSelectedCategory.size})",
        style = MaterialTheme.typography.labelMedium.copy(
          color = AluminumMuted,
          letterSpacing = 1.2.sp,
          fontWeight = FontWeight.Bold
        )
      )
    }

    items(uiState.partsForSelectedCategory) { part ->
      PartListItem(
        part = part,
        isSelected = part.id == selectedPart?.id,
        onSelect = { viewModel.selectPartItem(part) }
      )
    }
  }
}

@Composable
private fun PartListItem(
  part: PartItem,
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
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Surface(
          shape = CircleShape,
          color = if (isSelected) PerformanceRed else MetalBorder,
          modifier = Modifier.size(24.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = "${part.schemaCalloutNumber}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PureWhite,
                fontSize = 11.sp
              )
            )
          }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = part.name,
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = PureWhite
            )
          )
          Text(
            text = "OEM : ${part.oemReference} • ${part.crossReferences.take(2).joinToString { "${it.brand} ${it.reference}" }}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = AluminumGray,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          )
        }
      }

      Text(
        text = "${part.priceEstimatedDzd} DA",
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.Bold,
          color = MechanicalOrange,
          fontSize = 12.sp
        )
      )
    }
  }
}
