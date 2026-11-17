package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FirestoreVendorStockItem
import com.example.data.model.VendorStockAvailabilityStatus
import com.example.data.repository.MekanikRepository
import com.example.ui.components.MekanikSecondaryButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

@Composable
fun VendorsScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  var activeVendorTab by remember { mutableIntStateOf(0) } // 0: Espace Vendeur Stock Temps Réel, 1: Annuaire 58 Wilayas
  var showAddStockForm by remember { mutableStateOf(false) }
  var stockFilterQuery by remember { mutableStateOf("") }

  // New Stock Item Form
  var newPartName by remember { mutableStateOf("Kit Embrayage + Volant Moteur Bimasse") }
  var newOemRef by remember { mutableStateOf("04L 105 266 B") }
  var newIamRef by remember { mutableStateOf("LuK 600 0371 00 / Sachs") }
  var newQty by remember { mutableIntStateOf(6) }
  var newPriceDzd by remember { mutableIntStateOf(68500) }
  var newVendorName by remember { mutableStateOf("AutoPièces Bab Ezzouar (Alger 16)") }

  val filteredStock = uiState.vendorStockItems.filter {
    stockFilterQuery.isBlank() ||
      it.partName.contains(stockFilterQuery, ignoreCase = true) ||
      it.oemReference.contains(stockFilterQuery, ignoreCase = true) ||
      it.iamReference.contains(stockFilterQuery, ignoreCase = true) ||
      it.vendorName.contains(stockFilterQuery, ignoreCase = true)
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("vendors_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          IconButton(
            onClick = onBack,
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(MetalCard)
              .testTag("btn_back_from_vendors")
          ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = PureWhite)
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "ESPACE VENDEUR & STOCK TEMPS RÉEL",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite,
                letterSpacing = 0.8.sp
              )
            )
            Text(
              text = "Synchronisation Firestore en direct avec le Catalogue Pièces (ETKA / Partslink24)",
              style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 10.5.sp)
            )
          }
        }
      }
    }

    // Tab Switcher: Espace Vendeur Stock Temps Réel vs Annuaire 58 Wilayas
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = if (activeVendorTab == 0) PerformanceRed else MetalCard,
          border = BorderStroke(1.dp, if (activeVendorTab == 0) PerformanceRedGlow else MetalBorder),
          onClick = { activeVendorTab = 0 },
          modifier = Modifier
            .weight(1f)
            .testTag("tab_vendor_realtime_inventory")
        ) {
          Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.Inventory2,
              contentDescription = null,
              tint = if (activeVendorTab == 0) PureWhite else TechCyan,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "STOCK TEMPS RÉEL (${uiState.vendorStockItems.size})",
              color = if (activeVendorTab == 0) PureWhite else AluminumLight,
              fontWeight = FontWeight.Black,
              fontSize = 10.5.sp
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = if (activeVendorTab == 1) PerformanceRed else MetalCard,
          border = BorderStroke(1.dp, if (activeVendorTab == 1) PerformanceRedGlow else MetalBorder),
          onClick = { activeVendorTab = 1 },
          modifier = Modifier
            .weight(1f)
            .testTag("tab_vendor_directory_58_wilayas")
        ) {
          Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.Storefront,
              contentDescription = null,
              tint = if (activeVendorTab == 1) PureWhite else MechanicalOrange,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "ANNUAIRE 58 WILAYAS (${uiState.vendorsList.size})",
              color = if (activeVendorTab == 1) PureWhite else AluminumLight,
              fontWeight = FontWeight.Black,
              fontSize = 10.5.sp
            )
          }
        }
      }
    }

    if (activeVendorTab == 0) {
      // Real-Time Inventory KPI Summary & Add Button
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.6f))
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(CircleShape)
                      .background(ElectricGreen)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "FLUX FIRESTORE /vendor_stock EN DIRECT",
                    color = ElectricGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.5.sp
                  )
                }
                Text(
                  text = "Modifiez les quantités, prix (DZD) et disponibilités : vos changements sont immédiatement répercutés lors des recherches de pièces par les ateliers et clients.",
                  color = AluminumLight,
                  fontSize = 10.5.sp
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Button(
                onClick = { showAddStockForm = !showAddStockForm },
                colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("btn_toggle_add_vendor_stock")
              ) {
                Icon(if (showAddStockForm) Icons.Default.Close else Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (showAddStockForm) "FERMER" else "+ RÉFÉRENCE", fontSize = 10.sp, fontWeight = FontWeight.Black)
              }
            }

            AnimatedVisibility(visible = showAddStockForm) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = GraphiteBlack,
                border = BorderStroke(1.dp, TechCyan),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text(
                    text = "AJOUTER UNE PIÈCE AU STOCK TEMPS RÉEL",
                    color = TechCyan,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                  )
                  OutlinedTextField(
                    value = newPartName,
                    onValueChange = { newPartName = it },
                    label = { Text("Désignation Pièce", fontSize = 10.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                  )
                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                      value = newOemRef,
                      onValueChange = { newOemRef = it },
                      label = { Text("Réf. OEM Constructeur", fontSize = 10.sp) },
                      singleLine = true,
                      modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                      value = newIamRef,
                      onValueChange = { newIamRef = it },
                      label = { Text("Équivalence IAM", fontSize = 10.sp) },
                      singleLine = true,
                      modifier = Modifier.weight(1f)
                    )
                  }
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      Text("Qté :", color = AluminumLight, fontSize = 10.5.sp)
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MetalCard,
                        border = BorderStroke(1.dp, MetalBorder),
                        onClick = { newQty = (newQty - 1).coerceAtLeast(0) }
                      ) {
                        Text("-", color = PureWhite, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                      }
                      Text("$newQty", color = ElectricGreen, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MetalCard,
                        border = BorderStroke(1.dp, MetalBorder),
                        onClick = { newQty += 1 }
                      ) {
                        Text("+", color = PureWhite, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                      }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      Text("Prix :", color = AluminumLight, fontSize = 10.5.sp)
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MetalCard,
                        border = BorderStroke(1.dp, MetalBorder),
                        onClick = { newPriceDzd = (newPriceDzd - 1000).coerceAtLeast(500) }
                      ) {
                        Text("-1k", color = PureWhite, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
                      }
                      Text("$newPriceDzd DA", color = MechanicalOrange, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MetalCard,
                        border = BorderStroke(1.dp, MetalBorder),
                        onClick = { newPriceDzd += 1000 }
                      ) {
                        Text("+1k", color = PureWhite, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
                      }
                    }
                  }

                  Button(
                    onClick = {
                      viewModel.addRealtimeVendorStockItem(
                        vendorName = newVendorName,
                        partName = newPartName,
                        oemRef = newOemRef,
                        iamRef = newIamRef,
                        qty = newQty,
                        priceDzd = newPriceDzd,
                        status = if (newQty > 4) VendorStockAvailabilityStatus.EN_STOCK else VendorStockAvailabilityStatus.STOCK_FAIBLE
                      )
                      showAddStockForm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricGreenDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Text("PUBLIER EN TEMPS RÉEL DANS LE CATALOGUE", color = ElectricGreen, fontWeight = FontWeight.Black, fontSize = 10.5.sp)
                  }
                }
              }
            }

            // Search bar inside Seller Inventory
            OutlinedTextField(
              value = stockFilterQuery,
              onValueChange = { stockFilterQuery = it },
              placeholder = { Text("Filtrer par réf OEM (ex: 5Q0 698 151 BR), pièce ou marque IAM...", fontSize = 11.sp) },
              leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp)) },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_filter_vendor_stock")
            )
          }
        }
      }

      // Real-Time Stock Items List
      items(filteredStock, key = { it.id }) { stockItem ->
        VendorRealtimeStockCard(
          item = stockItem,
          onUpdateStock = { qty, price, status ->
            viewModel.updateVendorStockQuantityPriceStatus(stockItem.id, qty, price, status)
          }
        )
      }
    } else {
      // Wilaya Filter Chips
      item {
        Text(
          text = "FILTRER PAR WILAYA :",
          style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(MekanikRepository.wilayasList) { wilaya ->
            val isSelected = wilaya == uiState.selectedWilayaFilter
            FilterChip(
              selected = isSelected,
              onClick = { viewModel.onWilayaSelected(wilaya) },
              label = {
                Text(
                  text = wilaya,
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = PerformanceRedDark,
                selectedLabelColor = PureWhite,
                containerColor = MetalCard,
                labelColor = AluminumLight
              )
            )
          }
        }
      }

      // Vendors Cards
      items(uiState.vendorsList) { vendor ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("vendor_directory_card_${vendor.id}"),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, if (vendor.isVerified) TechCyan.copy(alpha = 0.4f) else MetalBorder)
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Top
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = vendor.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                  )
                  if (vendor.isVerified) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.Verified, contentDescription = "Certifié", tint = TechCyan, modifier = Modifier.size(16.dp))
                  }
                }
                Text(
                  text = "${vendor.wilaya} • ${vendor.address}",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight)
                )
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = GraphiteBlack
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.Star, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "${vendor.rating}",
                    style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold)
                  )
                }
              }
            }

            Text(
              text = "Spécialités : ${vendor.specialty}",
              style = MaterialTheme.typography.bodySmall.copy(color = MechanicalOrange, fontSize = 11.5.sp)
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = if (vendor.deliveryAvailable) "🚚 Livraison inter-wilayas disponible" else "📍 Retrait en magasin uniquement",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (vendor.deliveryAvailable) ElectricGreen else AluminumMuted
                )
              )

              MekanikSecondaryButton(
                text = vendor.phone,
                onClick = { viewModel.showToastNotification("Appel vers ${vendor.name} (${vendor.phone})...") },
                icon = Icons.Default.Phone,
                testTag = "call_vendor_${vendor.id}"
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun VendorRealtimeStockCard(
  item: FirestoreVendorStockItem,
  onUpdateStock: (Int, Int, VendorStockAvailabilityStatus) -> Unit
) {
  val status = item.parsedAvailability
  val statusColor = Color(status.colorHex)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("realtime_stock_card_${item.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.55f))
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = item.partName,
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = PerformanceRedDark,
              border = BorderStroke(0.5.dp, PerformanceRed)
            ) {
              Text(
                text = "OEM : ${item.oemReference}",
                color = PureWhite,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = item.iamReference,
              color = TechCyan,
              fontSize = 10.5.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Magasin : ${item.vendorName}",
            color = AluminumGray,
            fontSize = 10.sp
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = statusColor.copy(alpha = 0.18f),
          border = BorderStroke(1.dp, statusColor)
        ) {
          Text(
            text = status.label,
            color = statusColor,
            fontWeight = FontWeight.Black,
            fontSize = 9.5.sp,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
          )
        }
      }

      Divider(color = MetalBorder)

      // Interactive Quantity & Price Controls
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Stock Quantity Stepper
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "Stock :",
            color = AluminumMuted,
            fontSize = 10.5.sp
          )
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = GraphiteBlack,
            border = BorderStroke(1.dp, MetalBorder),
            onClick = {
              val nextQty = (item.quantityInStock - 1).coerceAtLeast(0)
              onUpdateStock(nextQty, item.priceDzd, status)
            },
            modifier = Modifier.testTag("btn_stock_dec_${item.id}")
          ) {
            Text(
              text = "−",
              color = PureWhite,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp,
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
            )
          }
          Text(
            text = "${item.quantityInStock} u.",
            color = statusColor,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp
          )
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = GraphiteBlack,
            border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.6f)),
            onClick = {
              val nextQty = item.quantityInStock + 1
              val nextStatus = if (nextQty >= 5 && status == VendorStockAvailabilityStatus.RUPTURE) {
                VendorStockAvailabilityStatus.EN_STOCK
              } else status
              onUpdateStock(nextQty, item.priceDzd, nextStatus)
            },
            modifier = Modifier.testTag("btn_stock_inc_${item.id}")
          ) {
            Text(
              text = "+",
              color = ElectricGreen,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp,
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
            )
          }
        }

        // Price DZD Stepper
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = GraphiteBlack,
            border = BorderStroke(1.dp, MetalBorder),
            onClick = {
              onUpdateStock(item.quantityInStock, (item.priceDzd - 500).coerceAtLeast(500), status)
            },
            modifier = Modifier.testTag("btn_price_dec_${item.id}")
          ) {
            Text(
              text = "-500",
              color = AluminumLight,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
            )
          }
          Text(
            text = "${item.priceDzd} DA",
            color = MechanicalOrange,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp
          )
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = GraphiteBlack,
            border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.6f)),
            onClick = {
              onUpdateStock(item.quantityInStock, item.priceDzd + 500, status)
            },
            modifier = Modifier.testTag("btn_price_inc_${item.id}")
          ) {
            Text(
              text = "+500",
              color = MechanicalOrange,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
            )
          }
        }
      }

      // Availability Status Quick Selector
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        VendorStockAvailabilityStatus.entries.forEach { st ->
          val isCurrent = st == status
          val stColor = Color(st.colorHex)
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isCurrent) stColor.copy(alpha = 0.22f) else GraphiteBlack,
            border = BorderStroke(1.dp, if (isCurrent) stColor else MetalBorder),
            onClick = {
              val adjustedQty = if (st == VendorStockAvailabilityStatus.RUPTURE) 0 else item.quantityInStock.coerceAtLeast(1)
              onUpdateStock(adjustedQty, item.priceDzd, st)
            }
          ) {
            Text(
              text = st.label,
              color = if (isCurrent) stColor else AluminumLight,
              fontSize = 9.5.sp,
              fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Medium,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }
    }
  }
}
