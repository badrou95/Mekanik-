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

    // Scanner Vision Étiquette Pièce (Code-Barres / QR / Compatibilité Châssis & Méthode de Montage)
    item {
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = MetalCard,
        border = BorderStroke(1.2.dp, MekanikBrandOrange),
        onClick = { viewModel.openPartLabelVisionScanner() },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("banner_part_label_vision_scanner_catalog")
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
          ) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MekanikBrandOrange.copy(alpha = 0.18f),
              border = BorderStroke(1.dp, MekanikBrandOrange),
              modifier = Modifier.size(44.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.QrCodeScanner,
                  contentDescription = null,
                  tint = MekanikBrandOrange,
                  modifier = Modifier.size(24.dp)
                )
              }
            }
            Column {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = "SCANNER ÉTIQUETTE PIÈCE (CODE-BARRES / QR)",
                  color = OffWhite,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 12.5.sp
                )
                Surface(
                  shape = RoundedCornerShape(5.dp),
                  color = MekanikValidationGreen.copy(alpha = 0.16f)
                ) {
                  Text(
                    text = "VISION IA",
                    color = MekanikValidationGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
              Text(
                text = "Vérifie la compatibilité avec le châssis ${activeVehicle.vin.take(11)}..., les autres références montables et la méthode d'installation.",
                color = AluminumGray,
                fontSize = 11.sp
              )
            }
          }
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MekanikBrandOrange
          ) {
            Text(
              text = "SCANNER",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
            )
          }
        }
      }
    }

    // Free Web OEM Exploded Catalogs Hub (CatCar.info, 7Zap, PartsSouq, NemigaParts, IFInterface, WebAutoCats) + MekanikOne Unified API
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = GraphiteBlack,
          border = BorderStroke(1.5.dp, MechanicalOrange),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.openUnifiedAutoApiHubModal() }
            .testTag("banner_unified_auto_api_parts_screen")
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(PerformanceRedDark),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Hub, contentDescription = null, tint = PureWhite, modifier = Modifier.size(22.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "API UNIQUE MEKANIK-ONE (6-EN-1) & MARQUES CHINOISES",
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.5.sp
                )
                Text(
                  text = "SinoAuto360 • Auto-API (Dongchedi) • Car2DB LLM • lifeofcapo (142 Marques) • ChineseCar.parts",
                  color = MechanicalOrange,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
                Text(
                  text = "Décodage VIN Chine & Arborescences OEM : Geely Coolray, Jetour Dashing, BYD, Chery, Changan, FAW-VAG",
                  color = AluminumLight,
                  fontSize = 9.5.sp
                )
              }
            }
            Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(15.dp))
          }
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MetalCard,
          border = BorderStroke(1.5.dp, TechCyan),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.openWebOemCatalogsModal() }
            .testTag("banner_web_oem_catalogs_parts_screen")
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(TechCyanDark),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.AccountTree, contentDescription = null, tint = PureWhite, modifier = Modifier.size(22.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "CATALOGUES WEB OEM GRATUITS & VUES ÉCLATÉES",
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.5.sp
                )
                Text(
                  text = "ChineseCar.parts • CatCar.info • 7Zap • PartsSouq • NemigaParts • IFInterface • WebAutoCats",
                  color = TechCyan,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
                Text(
                  text = "Chine (Geely/Jetour/BYD), Renault, Stellantis, Hyundai/Kia & VAG (Moteur, Châssis, Élec)",
                  color = AluminumLight,
                  fontSize = 9.5.sp
                )
              }
            }
            Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TechCyan, modifier = Modifier.size(15.dp))
          }
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MetalCard,
          border = BorderStroke(1.dp, WarningYellow),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.openPartCompatibilityModal() }
            .testTag("banner_pr_opr_compatibility_parts_screen")
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Icon(Icons.Default.Verified, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "VÉRIFICATEUR ANTI-ERREUR : CODES PR (VAG) • N° OPR (PSA) • BOÎTES TL4/JR5",
                  color = PureWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.5.sp
                )
                Text(
                  text = "Évitez les erreurs de diamètre disques (1ZE vs 1ZA), chaîne 7/8mm & embrayage",
                  color = AluminumLight,
                  fontSize = 9.5.sp
                )
              }
            }
            Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(14.dp))
          }
        }
      }
    }

    // NOUVEAU : Accès Libre Sans Compte + Panier Schéma Éclaté + Carte Google Maps Exacte
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("banner_smart_cart_free_access_parts"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = adaptiveBannerSurface(0xFF0E212E, 0xFFF0F9FF)),
        border = BorderStroke(1.5.dp, TechCyan)
      ) {
        Column(
          modifier = Modifier.padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "🛒 PANIER SCHÉMA ÉCLATÉ • 🔓 ACCÈS LIBRE • 🗺️ MAPS",
              color = PureWhite,
              fontWeight = FontWeight.Black,
              fontSize = 11.5.sp
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = ElectricGreen.copy(alpha = 0.2f),
              border = BorderStroke(1.dp, ElectricGreen)
            ) {
              Text(
                text = "SANS COMPTE DISPO",
                color = ElectricGreen,
                fontWeight = FontWeight.Black,
                fontSize = 8.5.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Text(
            text = "Recherchez sans compte par mot-clé (ex: « pompe à eau 206+ »), ajoutez des pièces/câblages du schéma éclaté au panier pour voir les références compatibles et les vendeurs/ateliers en stock, ou ouvrez Google Maps.",
            color = AluminumLight,
            fontSize = 10.sp
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Button(
              onClick = { viewModel.openSmartCartFreeAccessMapModal(0) },
              colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("btn_open_free_access_from_parts")
            ) {
              Text("🔓 Accès Libre", color = GraphiteDark, fontWeight = FontWeight.Black, fontSize = 9.5.sp)
            }
            Button(
              onClick = { viewModel.openSmartCartFreeAccessMapModal(1) },
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("btn_open_smart_cart_from_parts")
            ) {
              Text("🛒 Panier Éclaté", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 9.5.sp)
            }
            Button(
              onClick = { viewModel.openSmartCartFreeAccessMapModal(2) },
              colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("btn_open_google_maps_from_parts")
            ) {
              Text("🗺️ Google Maps", color = GraphiteDark, fontWeight = FontWeight.Black, fontSize = 9.5.sp)
            }
          }
        }
      }
    }

    // Barcode & Store Inventory Direct Scanner Banner
    item {
      val lowStockCount = uiState.barcodeInventory.count { it.isLowStock }
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MetalCard,
        border = BorderStroke(1.dp, MechanicalOrange),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { viewModel.openBarcodeScannerModal() }
          .testTag("banner_barcode_scanner_parts_screen")
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MechanicalOrangeDark),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = PureWhite, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "LOGICIEL STOCKPRO ERP • GESTION PIÈCES DE RECHANGE",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.5.sp
                  )
                )
                if (lowStockCount > 0) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(shape = RoundedCornerShape(4.dp), color = PerformanceRedDark) {
                    Text(
                      text = "$lowStockCount ALERTES",
                      color = PureWhite,
                      fontSize = 8.5.sp,
                      fontWeight = FontWeight.Black,
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                  }
                }
              }
              Text(
                text = "Valorisation HT/TTC (${uiState.barcodeInventory.sumOf { it.totalSaleValueDzd }} DA) • Rayons • Mouvements E/S (${uiState.stockMovementLogs.size}) • Bons de Commande (${uiState.supplierPurchaseOrders.size})",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 10.sp)
              )
            }
          }
          Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(14.dp))
        }
      }
    }

    // QR Code Parts Reception Banner (For Retailers & Workshops)
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MetalCard,
        border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.6f)),
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
              text = "COMMERÇANT / VENDEUR : SCANNER QR PIÈCES",
              style = MaterialTheme.typography.labelSmall.copy(
                color = PureWhite,
                fontWeight = FontWeight.Bold
              )
            )
            Text(
              text = "Charger instantanément le panier de pièces commandées par le client ou le loueur",
              style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 10.5.sp)
            )
          }
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MechanicalOrange,
            modifier = Modifier.padding(start = 6.dp)
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
              color = adaptiveBannerSurface(0xFF2C1518, 0xFFFFF1F2),
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

            // Visual Pipeline Breadcrumb (ETKA / Partslink24 Flow)
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = GraphiteBlack,
              border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.45f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "🚗 ${uiState.activeVehicle.brand} ${uiState.activeVehicle.model} ➔ ⚙️ ${uiState.selectedSystemCategory.label} ➔ #${selectedPart.schemaCalloutNumber} ➔ 🔴 ${selectedPart.oemReference} ➔ 🏪 Stock Live",
                  color = TechCyan,
                  fontSize = 9.5.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Available Vendors Section (Connected to Real-Time Seller Inventory)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(ElectricGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "VENDEURS DISPONIBLES (TEMPS RÉEL FIRESTORE)",
                  style = MaterialTheme.typography.labelMedium.copy(
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp
                  )
                )
              }
              TextButton(
                onClick = onNavigateToVendors,
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                modifier = Modifier.testTag("btn_open_realtime_vendor_space")
              ) {
                Icon(Icons.Default.Inventory2, contentDescription = null, tint = TechCyan, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Espace Vendeur",
                  color = TechCyan,
                  fontSize = 10.5.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
            Spacer(modifier = Modifier.height(6.dp))

            val realtimeMatches = uiState.vendorStockItems.filter {
              it.oemReference.equals(selectedPart.oemReference, ignoreCase = true) ||
                selectedPart.oemAlternatives.any { alt -> alt.equals(it.oemReference, ignoreCase = true) } ||
                it.partName.contains(selectedPart.name.take(10), ignoreCase = true)
            }.ifEmpty { uiState.vendorStockItems.take(3) }

            realtimeMatches.forEach { liveStock ->
              val st = liveStock.parsedAvailability
              val stColor = Color(st.colorHex)
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MetalSurface,
                border = BorderStroke(1.dp, stColor.copy(alpha = 0.55f)),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 6.dp)
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = liveStock.vendorName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                          fontWeight = FontWeight.Bold,
                          color = PureWhite
                        )
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = stColor.copy(alpha = 0.2f),
                        border = BorderStroke(0.5.dp, stColor)
                      ) {
                        Text(
                          text = st.label,
                          color = stColor,
                          fontSize = 8.5.sp,
                          fontWeight = FontWeight.Black,
                          modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                      }
                    }
                    Text(
                      text = "OEM : ${liveStock.oemReference} • Équiv : ${liveStock.iamReference} • Stock : ${liveStock.quantityInStock} u.",
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = AluminumGray,
                        fontSize = 10.5.sp
                      )
                    )
                  }

                  Column(horizontalAlignment = Alignment.End) {
                    Text(
                      text = "${liveStock.priceDzd} DA",
                      style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = MechanicalOrange,
                        fontFamily = FontFamily.Monospace
                      )
                    )
                    Text(
                      text = "Sync Temps Réel",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = ElectricGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                      )
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action 1: Ajouter au Panier Intelligent Schéma Éclaté (Compatibilités OEM/IAM, Véhicules & Vendeurs/Ateliers en Stock)
            Button(
              onClick = { viewModel.addPartFromExplodedSchemaToSmartCart(selectedPart) },
              colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_add_exploded_part_to_smart_cart")
            ) {
              Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = GraphiteDark)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "🛒 AJOUTER AU PANIER SCHÉMA ÉCLATÉ (COMPATIBILITÉS & STOCK)",
                color = GraphiteDark,
                fontWeight = FontWeight.Black,
                fontSize = 10.5.sp
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action 2: Add this part directly to an Atelier Quote
            MekanikPrimaryButton(
              text = "GÉNÉRER UN DEVIS ATELIER AVEC CETTE PIÈCE",
              onClick = { viewModel.openQuoteGeneratorWithPart(selectedPart) },
              icon = Icons.Default.ReceiptLong,
              testTag = "btn_quote_with_this_part"
            )
          }
        }
      }
    }

    // Dynamic Real-Time Parts Filtering Panel
    item {
      DynamicPartFilterPanel(
        filter = uiState.partDynamicFilter,
        activeSystem = uiState.selectedSystemCategory,
        matchingCount = uiState.partsForSelectedCategory.size,
        onUpdateFilter = { transform -> viewModel.updatePartDynamicFilter(transform) },
        onResetFilters = { viewModel.resetPartDynamicFilters() }
      )
    }

    // Filtered Parts Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (uiState.partDynamicFilter.searchAllSystems) {
            "RÉSULTATS GLOBAUX TOUS SYSTÈMES (${uiState.partsForSelectedCategory.size})"
          } else {
            "PIÈCES FILTRÉES • ${uiState.selectedSystemCategory.label.uppercase()} (${uiState.partsForSelectedCategory.size})"
          },
          style = MaterialTheme.typography.labelMedium.copy(
            color = AluminumMuted,
            letterSpacing = 1.1.sp,
            fontWeight = FontWeight.Bold
          )
        )
        if (uiState.partDynamicFilter.activeFilterCount > 0) {
          Text(
            text = "${uiState.partDynamicFilter.activeFilterCount} filtre(s) actif(s)",
            style = MaterialTheme.typography.labelSmall.copy(
              color = TechCyanGlow,
              fontWeight = FontWeight.Bold
            )
          )
        }
      }
    }

    if (uiState.partsForSelectedCategory.isEmpty()) {
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
              .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              Icons.Default.FilterAltOff,
              contentDescription = null,
              tint = MechanicalOrange,
              modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Aucune pièce ne correspond aux filtres sélectionnés",
              style = MaterialTheme.typography.titleSmall.copy(
                color = PureWhite,
                fontWeight = FontWeight.Bold
              )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Activez « Tous les systèmes » ou réinitialisez les filtres de marque/prix.",
              style = MaterialTheme.typography.bodySmall.copy(
                color = AluminumGray,
                fontSize = 11.5.sp
              )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              if (!uiState.partDynamicFilter.searchAllSystems) {
                Button(
                  onClick = { viewModel.updatePartDynamicFilter { it.copy(searchAllSystems = true) } },
                  colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("Chercher dans les 10 systèmes", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
              OutlinedButton(
                onClick = { viewModel.resetPartDynamicFilters() },
                border = BorderStroke(1.dp, PerformanceRed),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("Réinitialiser filtres", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    } else {
      items(uiState.partsForSelectedCategory) { part ->
        PartListItem(
          part = part,
          isSelected = part.id == selectedPart?.id,
          onSelect = { viewModel.selectPartItem(part) }
        )
      }
    }
  }
}

@Composable
private fun DynamicPartFilterPanel(
  filter: PartDynamicFilterState,
  activeSystem: SystemCategory,
  matchingCount: Int,
  onUpdateFilter: ((PartDynamicFilterState) -> PartDynamicFilterState) -> Unit,
  onResetFilters: () -> Unit
) {
  val basePool = remember(activeSystem, filter.searchAllSystems) {
    if (filter.searchAllSystems) {
      MekanikRepository.sampleParts
    } else {
      MekanikRepository.sampleParts.filter { it.system == activeSystem }
    }
  }

  val availableBrands = remember(basePool) {
    listOf("TOUTES") + basePool
      .flatMap { part -> part.crossReferences.map { it.brand } }
      .distinct()
      .sorted()
  }

  val positionOptions = listOf("TOUTES", "Avant", "Arrière", "Moteur / Interne")

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("dynamic_part_filter_panel"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.2.dp, TechCyan.copy(alpha = 0.6f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Header + Live Matching Counter + Reset
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            Icons.Default.FilterList,
            contentDescription = null,
            tint = TechCyan,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "FILTRAGE DYNAMIQUE PIÈCES & RÉFÉRENCES OEM",
              style = MaterialTheme.typography.labelMedium.copy(
                color = PureWhite,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.6.sp
              )
            )
            Text(
              text = "$matchingCount pièce(s) sur ${basePool.size} dans ${if (filter.searchAllSystems) "les 10 systèmes" else activeSystem.label}",
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
              .testTag("reset_part_filters_btn")
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
                text = "Reset (${filter.activeFilterCount})",
                color = PureWhite,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // Real-time search box (Nom, Réf OEM, Réf IAM, Position)
      OutlinedTextField(
        value = filter.searchQuery,
        onValueChange = { q -> onUpdateFilter { it.copy(searchQuery = q) } },
        placeholder = {
          Text(
            text = "Filtrer en direct : Réf OEM (ex: 5Q0698151), Marque (Brembo, Bosch), Nom pièce...",
            color = AluminumMuted,
            fontSize = 11.5.sp
          )
        },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
        },
        trailingIcon = {
          if (filter.searchQuery.isNotEmpty()) {
            IconButton(onClick = { onUpdateFilter { it.copy(searchQuery = "") } }) {
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
          .testTag("part_dynamic_search_input")
      )

      // Scope & Quick Toggles Row: [Système Actif vs Tous Systèmes] [Stock Live] [Compatible 100%]
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (filter.searchAllSystems) TechCyanDark else GraphiteBlack,
          border = BorderStroke(1.dp, if (filter.searchAllSystems) TechCyan else MetalBorder),
          modifier = Modifier.clickable {
            onUpdateFilter { it.copy(searchAllSystems = !it.searchAllSystems) }
          }
        ) {
          Text(
            text = if (filter.searchAllSystems) "🌐 Tous les Systèmes (Actif)" else "🎯 Système : ${activeSystem.label}",
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (filter.onlyInLiveStock) ElectricGreen.copy(alpha = 0.22f) else GraphiteBlack,
          border = BorderStroke(1.dp, if (filter.onlyInLiveStock) ElectricGreen else MetalBorder),
          modifier = Modifier.clickable {
            onUpdateFilter { it.copy(onlyInLiveStock = !it.onlyInLiveStock) }
          }
        ) {
          Text(
            text = "📦 En Stock Live",
            color = if (filter.onlyInLiveStock) ElectricGreen else AluminumLight,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (filter.only100PercentCompatible) MechanicalOrangeDark else GraphiteBlack,
          border = BorderStroke(1.dp, if (filter.only100PercentCompatible) MechanicalOrange else MetalBorder),
          modifier = Modifier.clickable {
            onUpdateFilter { it.copy(only100PercentCompatible = !it.only100PercentCompatible) }
          }
        ) {
          Text(
            text = "✅ 100% Compatible VIN",
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
          )
        }
      }

      // Equipment Brand Facets with Live Counts
      Text(
        text = "ÉQUIPEMENTIER PREMIÈRE MONTE (IAM / OEM) :",
        style = MaterialTheme.typography.labelSmall.copy(
          color = AluminumMuted,
          fontWeight = FontWeight.Bold,
          fontSize = 9.5.sp
        )
      )
      LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(availableBrands) { brand ->
          val isSelected = filter.selectedBrand.equals(brand, ignoreCase = true)
          val countForBrand = remember(basePool, filter.searchQuery, filter.priceBracket, brand) {
            basePool.count { p ->
              (brand == "TOUTES" || p.crossReferences.any { it.brand.equals(brand, ignoreCase = true) }) &&
                p.priceEstimatedDzd >= filter.priceBracket.minDzd &&
                p.priceEstimatedDzd <= filter.priceBracket.maxDzd
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
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (brand == "TOUTES") "Toutes Marques" else brand,
                color = if (isSelected) PureWhite else AluminumLight,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                fontSize = 10.sp
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "($countForBrand)",
                color = if (isSelected) PureWhite else TechCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              )
            }
          }
        }
      }

      // Position + Price Brackets + Sort Order Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        positionOptions.forEach { pos ->
          val isSelected = filter.selectedPosition == pos
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) TechCyanDark else GraphiteBlack,
            border = BorderStroke(1.dp, if (isSelected) TechCyan else MetalBorder),
            modifier = Modifier.clickable {
              onUpdateFilter { it.copy(selectedPosition = pos) }
            }
          ) {
            Text(
              text = if (pos == "TOUTES") "📍 Toutes Positions" else "📍 $pos",
              color = if (isSelected) PureWhite else AluminumLight,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 10.sp,
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
            )
          }
        }

        PartPriceBracket.entries.drop(1).forEach { bracket ->
          val isSelected = filter.priceBracket == bracket
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) MechanicalOrangeDark else GraphiteBlack,
            border = BorderStroke(1.dp, if (isSelected) MechanicalOrange else MetalBorder),
            modifier = Modifier.clickable {
              onUpdateFilter {
                it.copy(priceBracket = if (isSelected) PartPriceBracket.ALL else bracket)
              }
            }
          ) {
            Text(
              text = "💰 ${bracket.label}",
              color = if (isSelected) PureWhite else AluminumLight,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 10.sp,
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
            )
          }
        }

        PartSortOption.entries.forEach { sortOpt ->
          val isSelected = filter.sortOption == sortOpt
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) PerformanceRedDark else GraphiteBlack,
            border = BorderStroke(1.dp, if (isSelected) PerformanceRed else MetalBorder),
            modifier = Modifier.clickable {
              onUpdateFilter { it.copy(sortOption = sortOpt) }
            }
          ) {
            Text(
              text = "⇅ ${sortOpt.label}",
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
