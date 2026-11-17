package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.data.repository.SmartCartAndFreeAccessRepository
import com.example.ui.theme.*

@Composable
fun SmartCartFreeAccessAndMapModal(
  initialTab: Int = 0,
  smartCartItems: List<SmartExplodedCartItem>,
  particulierAccountState: ParticulierAccountState,
  onUpdateCartQuantity: (itemId: String, delta: Int) -> Unit,
  onRemoveCartItem: (itemId: String) -> Unit,
  onAddExtraVinToParticulier: (vin: String, vehicleLabel: String, engineCode: String, year: Int) -> Unit,
  onRemoveExtraVinFromParticulier: (vin: String) -> Unit,
  onSelectParticulierTier: (ParticulierPlanTier) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab) }
  var freeSearchQuery by remember { mutableStateOf("pompe à eau 206+") }
  var selectedMapCategory by remember { mutableStateOf<MapPinCategory?>(null) }
  var selectedMapPinId by remember {
    mutableStateOf(SmartCartAndFreeAccessRepository.exactMapLocations.first().id)
  }
  var orderConfirmationToast by remember { mutableStateOf<String?>(null) }

  // Form states for Particulier Extra VIN
  var newVinInput by remember { mutableStateOf("") }
  var newVehicleModelInput by remember { mutableStateOf("") }
  var newEngineInput by remember { mutableStateOf("") }

  val uriHandler = LocalUriHandler.current

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .fillMaxHeight(0.93f)
        .testTag("smart_cart_free_access_map_modal"),
      shape = RoundedCornerShape(22.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, MechanicalOrange)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF281208), GraphiteBlack, Color(0xFF0C222E))
              )
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MechanicalOrange.copy(alpha = 0.22f))
                .border(1.dp, MechanicalOrange, RoundedCornerShape(12.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.ShoppingCartCheckout,
                contentDescription = null,
                tint = MechanicalOrange,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "ACCÈS LIBRE • PANIER SCHÉMA ÉCLATÉ • MAPS & PARTICULIER",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 12.5.sp
              )
              Text(
                text = "Recherche sans compte (ex: pompe à eau 206+) • Compatibilités Stock • Google Maps • Compte DIY 3 500 DA",
                color = TechCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("btn_close_smart_cart_modal")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite)
          }
        }

        // 4 Navigation Tabs
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(MetalSurface)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 10.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val tabs = listOf(
            Triple(0, "🔓 Accès Libre (Sans Compte)", "tab_free_access_no_account"),
            Triple(1, "🛒 Panier Schéma Éclaté (${smartCartItems.sumOf { it.quantity }})", "tab_smart_exploded_cart"),
            Triple(2, "🗺️ Google Maps Exacte (${SmartCartAndFreeAccessRepository.exactMapLocations.size})", "tab_exact_google_maps"),
            Triple(3, "👤 Compte Particulier (3 500 DA)", "tab_particulier_diy_account")
          )

          tabs.forEach { (idx, title, tag) ->
            val isSelected = selectedTab == idx
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) PerformanceRed else MetalCard,
              border = BorderStroke(
                1.dp,
                if (isSelected) PerformanceRedGlow else MetalBorder
              ),
              modifier = Modifier
                .clickable { selectedTab = idx }
                .testTag(tag)
            ) {
              Text(
                text = title,
                color = PureWhite,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
              )
            }
          }
        }

        // Confirmation Banner if user placed a quick order or reservation
        AnimatedVisibility(visible = orderConfirmationToast != null) {
          orderConfirmationToast?.let { msg ->
            Surface(
              color = ElectricGreen.copy(alpha = 0.16f),
              border = BorderStroke(1.dp, ElectricGreen),
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
              shape = RoundedCornerShape(10.dp)
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = msg,
                  color = ElectricGreen,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { orderConfirmationToast = null }, modifier = Modifier.size(24.dp)) {
                  Icon(Icons.Default.Close, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(16.dp))
                }
              }
            }
          }
        }

        // Content by Tab
        Box(modifier = Modifier.weight(1f)) {
          when (selectedTab) {
            // =========================================================================
            // TAB 0: ACCÈS LIBRE SANS COMPTE (RECHERCHE MOT-CLÉ EX: "pompe à eau 206+" OU RÉFÉRENCE)
            // =========================================================================
            0 -> {
              val allItems = SmartCartAndFreeAccessRepository.freeAccessCatalogItems
              val q = freeSearchQuery.trim().lowercase()
              val matchedItems = remember(q, allItems) {
                if (q.isBlank()) allItems
                else allItems.filter { item ->
                  item.partName.lowercase().contains(q) ||
                    item.vehicleApplication.lowercase().contains(q) ||
                    item.primaryOemRef.lowercase().contains(q) ||
                    item.crossReferences.any { it.lowercase().contains(q) } ||
                    item.keywordTags.any { tag -> q.contains(tag) || tag.contains(q) }
                }.ifEmpty { allItems }
              }

              LazyColumn(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                item {
                  Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MetalCard),
                    border = BorderStroke(1.dp, ElectricGreen)
                  ) {
                    Column(
                      modifier = Modifier.padding(14.dp),
                      verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = "🔓 ACCÈS LIBRE GRATUIT • ZÉRO COMPTE REQUIS",
                          color = ElectricGreen,
                          fontWeight = FontWeight.Black,
                          fontSize = 12.sp
                        )
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = ElectricGreen.copy(alpha = 0.2f)
                        ) {
                          Text(
                            text = "ACCÈS PUBLIC INSTANTANÉ",
                            color = ElectricGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                          )
                        }
                      }

                      Text(
                        text = "Vous avez déjà une référence ou vous cherchez une pièce par mot-clé (ex: « pompe à eau 206+ », « faisceau électrique Golf 7 », « embrayage Clio 4 ») ? Trouvez immédiatement les vendeurs en stock et les prix de montage chez les garagistes sans créer de compte.",
                        color = AluminumLight,
                        fontSize = 11.sp
                      )

                      OutlinedTextField(
                        value = freeSearchQuery,
                        onValueChange = { freeSearchQuery = it },
                        label = {
                          Text("Mot-clé (ex: pompe à eau 206+) ou Référence (ex: 1201.G9, 04L971600L)", fontSize = 10.5.sp)
                        },
                        leadingIcon = {
                          Icon(Icons.Default.Search, contentDescription = null, tint = MechanicalOrange)
                        },
                        trailingIcon = {
                          if (freeSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { freeSearchQuery = "" }) {
                              Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = AluminumLight)
                            }
                          }
                        },
                        singleLine = true,
                        modifier = Modifier
                          .fillMaxWidth()
                          .testTag("input_free_access_keyword_search"),
                        colors = OutlinedTextFieldDefaults.colors(
                          focusedBorderColor = MechanicalOrange,
                          unfocusedBorderColor = MetalBorder,
                          focusedTextColor = PureWhite,
                          unfocusedTextColor = PureWhite,
                          focusedContainerColor = GraphiteBlack,
                          unfocusedContainerColor = GraphiteBlack
                        )
                      )

                      // Quick keyword chips
                      LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val quickKeywords = listOf(
                          "pompe à eau 206+",
                          "1201.G9",
                          "faisceau électrique Golf 7",
                          "04L 971 600 L",
                          "embrayage Clio 4",
                          "302052307R"
                        )
                        items(quickKeywords) { kw ->
                          FilterChip(
                            selected = freeSearchQuery.equals(kw, ignoreCase = true),
                            onClick = { freeSearchQuery = kw },
                            label = { Text(kw, fontSize = 10.sp) }
                          )
                        }
                      }
                    }
                  }
                }

                items(matchedItems, key = { it.id }) { item ->
                  Card(
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("free_access_result_${item.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MetalCard),
                    border = BorderStroke(
                      1.2.dp,
                      if (item.isElectricalWiring) TechCyan else MechanicalOrange
                    )
                  ) {
                    Column(
                      modifier = Modifier.padding(14.dp),
                      verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = if (item.isElectricalWiring) TechCyan.copy(alpha = 0.2f) else MechanicalOrange.copy(alpha = 0.2f),
                          border = BorderStroke(1.dp, if (item.isElectricalWiring) TechCyan else MechanicalOrange)
                        ) {
                          Text(
                            text = if (item.isElectricalWiring) "⚡ CÂBLAGE / FAISCEAU ÉLECTRIQUE" else "⚙️ PIÈCE DE RECHANGE CERTIFIÉE",
                            color = if (item.isElectricalWiring) TechCyan else MechanicalOrange,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                          )
                        }

                        Text(
                          text = "Réf OEM : ${item.primaryOemRef}",
                          color = PerformanceRedGlow,
                          fontWeight = FontWeight.Black,
                          fontSize = 11.5.sp,
                          fontFamily = FontFamily.Monospace
                        )
                      }

                      Text(
                        text = item.partName,
                        color = PureWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                      )

                      Text(
                        text = "🚗 Véhicules compatibles : ${item.vehicleApplication}",
                        color = AluminumLight,
                        fontSize = 11.sp
                      )

                      // Prix Pièce + Prix Montage côte à côte
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = GraphiteBlack,
                          border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.6f)),
                          modifier = Modifier.weight(1f)
                        ) {
                          Column(modifier = Modifier.padding(10.dp)) {
                            Text("PRIX PIÈCE CHEZ VENDEURS", color = AluminumMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(
                              text = "dès ${item.bestPartPriceDzd} DA",
                              color = MechanicalOrange,
                              fontWeight = FontWeight.Black,
                              fontSize = 15.sp,
                              fontFamily = FontFamily.Monospace
                            )
                            Text("${item.stockVendors.size} magasin(s) en stock", color = ElectricGreen, fontSize = 9.5.sp)
                          }
                        }

                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = GraphiteBlack,
                          border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.6f)),
                          modifier = Modifier.weight(1f)
                        ) {
                          Column(modifier = Modifier.padding(10.dp)) {
                            Text("PRIX MONTAGE GARAGISTE", color = AluminumMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(
                              text = "~ ${item.averageMountingPriceDzd} DA",
                              color = TechCyan,
                              fontWeight = FontWeight.Black,
                              fontSize = 15.sp,
                              fontFamily = FontFamily.Monospace
                            )
                            Text("Durée ~${item.mountingTimeMinutes} min (M.O)", color = OffWhite, fontSize = 9.5.sp)
                          }
                        }
                      }

                      // Références compatibles
                      Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = GraphiteBlack,
                        border = BorderStroke(1.dp, MetalBorder),
                        modifier = Modifier.fillMaxWidth()
                      ) {
                        Column(
                          modifier = Modifier.padding(10.dp),
                          verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                          Text(
                            text = "🔄 RÉFÉRENCES COMPATIBLES (OEM & ÉQUIVALENCES IAM) :",
                            color = TechCyan,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp
                          )
                          item.crossReferences.forEach { ref ->
                            Text(
                              text = "• $ref",
                              color = OffWhite,
                              fontSize = 10.5.sp,
                              fontFamily = FontFamily.Monospace
                            )
                          }
                        }
                      }

                      // Vendeurs disponibles
                      Text(
                        text = "🏪 CONTACTER UN VENDEUR POUR COMMANDER (SANS COMPTE) :",
                        color = MechanicalOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.5.sp
                      )
                      item.stockVendors.forEach { vendor ->
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = MetalSurface,
                          border = BorderStroke(1.dp, MetalBorder),
                          modifier = Modifier.fillMaxWidth()
                        ) {
                          Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                          ) {
                            Column(modifier = Modifier.weight(1f)) {
                              Text(vendor.holderName, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                              Text(
                                text = "📍 ${vendor.wilaya} • ${vendor.exactAddress}",
                                color = AluminumLight,
                                fontSize = 10.sp
                              )
                              Text(
                                text = "Réf : ${vendor.matchedReference} • En stock : ${vendor.quantityAvailable} u.",
                                color = ElectricGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                              )
                            }
                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                              Text(
                                text = "${vendor.partPriceDzd} DA",
                                color = MechanicalOrange,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                              )
                              Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(
                                  onClick = {
                                    orderConfirmationToast = "📞 Demande de commande envoyée à ${vendor.holderName} (${vendor.phone}) pour ${item.partName} (${vendor.partPriceDzd} DA)."
                                  },
                                  colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
                                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                  shape = RoundedCornerShape(8.dp)
                                ) {
                                  Text("Commander", color = GraphiteDark, fontWeight = FontWeight.Black, fontSize = 9.5.sp)
                                }
                                OutlinedButton(
                                  onClick = {
                                    selectedMapPinId = vendor.holderId
                                    selectedTab = 2
                                  },
                                  border = BorderStroke(1.dp, TechCyan),
                                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                  shape = RoundedCornerShape(8.dp)
                                ) {
                                  Text("🗺️ Maps", color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 9.5.sp)
                                }
                              }
                            }
                          }
                        }
                      }

                      // Garagistes & Prix de montage
                      Text(
                        text = "🛠️ PRIX DE MONTAGE CHEZ LES GARAGISTES / ATELIERS PARTENAIRES :",
                        color = TechCyan,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.5.sp
                      )
                      item.partnerGarages.forEach { garage ->
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = MetalSurface,
                          border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.45f)),
                          modifier = Modifier.fillMaxWidth()
                        ) {
                          Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                          ) {
                            Column(modifier = Modifier.weight(1f)) {
                              Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(garage.workshopName, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                if (garage.hasPartInWorkshopStock) {
                                  Spacer(modifier = Modifier.width(6.dp))
                                  Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = ElectricGreen.copy(alpha = 0.2f)
                                  ) {
                                    Text(
                                      text = "PIÈCE AUSSI EN STOCK ATELIER",
                                      color = ElectricGreen,
                                      fontWeight = FontWeight.Black,
                                      fontSize = 8.sp,
                                      modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                  }
                                }
                              }
                              Text(
                                text = "📍 ${garage.wilaya} • ${garage.includesWarrantyAndDiag}",
                                color = AluminumLight,
                                fontSize = 10.sp
                              )
                            }
                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                              Text(
                                text = "Pose : ${garage.mountingLaborDzd} DA",
                                color = TechCyan,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.5.sp,
                                fontFamily = FontFamily.Monospace
                              )
                              Button(
                                onClick = {
                                  orderConfirmationToast = "🛠️ Demande de RDV Montage envoyée à ${garage.workshopName} (${garage.phone}) • Pose estimée : ${garage.mountingLaborDzd} DA."
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                              ) {
                                Text("RDV Montage", color = GraphiteDark, fontWeight = FontWeight.Black, fontSize = 9.5.sp)
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }

            // =========================================================================
            // TAB 1: PANIER SCHÉMA ÉCLATÉ (PIÈCES & CÂBLAGES + COMPATIBILITÉS + VENDEURS/ATELIERS EN STOCK)
            // =========================================================================
            1 -> {
              LazyColumn(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                item {
                  Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MetalCard),
                    border = BorderStroke(1.2.dp, PerformanceRed)
                  ) {
                    Column(
                      modifier = Modifier.padding(14.dp),
                      verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = "🛒 PANIER INTELLIGENT • SCHÉMA ÉCLATÉ & CÂBLAGES",
                          color = PureWhite,
                          fontWeight = FontWeight.Black,
                          fontSize = 12.5.sp
                        )
                        Text(
                          text = "Total estimé : ${smartCartItems.sumOf { it.unitPriceEstimatedDzd * it.quantity }} DA",
                          color = MechanicalOrange,
                          fontWeight = FontWeight.Black,
                          fontSize = 13.sp,
                          fontFamily = FontFamily.Monospace
                        )
                      }
                      Text(
                        text = "Pour chaque pièce ou faisceau électrique sélectionné depuis le schéma éclaté (Atelier, Magasin, Client ou Société), visualisez toutes les références compatibles, les véhicules concernés, ainsi que les vendeurs ET ateliers qui l'ont disponible dans leur stock.",
                        color = AluminumLight,
                        fontSize = 10.5.sp
                      )
                    }
                  }
                }

                if (smartCartItems.isEmpty()) {
                  item {
                    Card(
                      modifier = Modifier.fillMaxWidth(),
                      shape = RoundedCornerShape(14.dp),
                      colors = CardDefaults.cardColors(containerColor = MetalCard)
                    ) {
                      Column(
                        modifier = Modifier
                          .fillMaxWidth()
                          .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = AluminumGray, modifier = Modifier.size(36.dp))
                        Text("Votre panier Schéma Éclaté est vide", color = PureWhite, fontWeight = FontWeight.Bold)
                        Text(
                          "Cliquez sur une pièce ou un câblage dans le Schéma Éclaté du Catalogue Pièces pour l'ajouter au panier.",
                          color = AluminumLight,
                          fontSize = 11.sp
                        )
                      }
                    }
                  }
                } else {
                  items(smartCartItems, key = { it.id }) { cartItem ->
                    Card(
                      modifier = Modifier
                        .fillMaxWidth()
                        .testTag("smart_cart_item_${cartItem.id}"),
                      shape = RoundedCornerShape(16.dp),
                      colors = CardDefaults.cardColors(containerColor = MetalCard),
                      border = BorderStroke(
                        1.2.dp,
                        if (cartItem.isElectricalHarnessOrWiring) TechCyan else PerformanceRed.copy(alpha = 0.8f)
                      )
                    ) {
                      Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                      ) {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                          ) {
                            Surface(
                              shape = CircleShape,
                              color = if (cartItem.isElectricalHarnessOrWiring) TechCyan else PerformanceRed
                            ) {
                              Text(
                                text = "#${cartItem.calloutNumber}",
                                color = if (cartItem.isElectricalHarnessOrWiring) GraphiteDark else PureWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                              )
                            }
                            Column {
                              Text(
                                text = cartItem.partOrHarnessName,
                                color = PureWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                              )
                              Text(
                                text = "Châssis source : ${cartItem.sourceVin} • ${cartItem.sourceVehicleName}",
                                color = TechCyan,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                              )
                            }
                          }

                          Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                              onClick = { onUpdateCartQuantity(cartItem.id, -1) },
                              modifier = Modifier.size(28.dp)
                            ) {
                              Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Moins", tint = OffWhite, modifier = Modifier.size(18.dp))
                            }
                            Text(
                              text = "${cartItem.quantity}",
                              color = PureWhite,
                              fontWeight = FontWeight.Black,
                              fontSize = 13.sp,
                              modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            IconButton(
                              onClick = { onUpdateCartQuantity(cartItem.id, 1) },
                              modifier = Modifier.size(28.dp)
                            ) {
                              Icon(Icons.Default.AddCircleOutline, contentDescription = "Plus", tint = ElectricGreen, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                              onClick = { onRemoveCartItem(cartItem.id) },
                              modifier = Modifier.size(28.dp)
                            ) {
                              Icon(Icons.Default.DeleteOutline, contentDescription = "Retirer", tint = PerformanceRedGlow, modifier = Modifier.size(18.dp))
                            }
                          }
                        }

                        // 1. Références Compatibles
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = GraphiteBlack,
                          border = BorderStroke(1.dp, PerformanceRed.copy(alpha = 0.6f)),
                          modifier = Modifier.fillMaxWidth()
                        ) {
                          Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                          ) {
                            Text(
                              text = "1️⃣ RÉFÉRENCE PRINCIPALE (${cartItem.primaryOemReference}) & RÉFÉRENCES COMPATIBLES :",
                              color = PerformanceRedGlow,
                              fontWeight = FontWeight.Black,
                              fontSize = 10.sp
                            )
                            cartItem.compatibleReferences.forEach { ref ->
                              Text(
                                text = "✔ $ref",
                                color = PureWhite,
                                fontSize = 10.5.sp,
                                fontFamily = FontFamily.Monospace
                              )
                            }
                          }
                        }

                        // 2. Véhicules concernés par cette pièce
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = GraphiteBlack,
                          border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.5f)),
                          modifier = Modifier.fillMaxWidth()
                        ) {
                          Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                          ) {
                            Text(
                              text = "2️⃣ VÉHICULES COMPATIBLES CONCERNÉS PAR CETTE PIÈCE :",
                              color = TechCyan,
                              fontWeight = FontWeight.Black,
                              fontSize = 10.sp
                            )
                            cartItem.compatibleVehicles.forEach { veh ->
                              Text(
                                text = "🚗 $veh",
                                color = OffWhite,
                                fontSize = 10.5.sp
                              )
                            }
                          }
                        }

                        // 3. Vendeurs ET Ateliers ayant cette pièce disponible dans leur stock
                        Text(
                          text = "3️⃣ VENDEURS & ATELIERS AYANT CETTE PIÈCE DISPONIBLE EN STOCK :",
                          color = ElectricGreen,
                          fontWeight = FontWeight.Black,
                          fontSize = 10.5.sp
                        )

                        val allHolders = cartItem.matchingVendorsInStock + cartItem.matchingWorkshopsInStock
                        allHolders.forEach { holder ->
                          Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MetalSurface,
                            border = BorderStroke(
                              1.dp,
                              if (holder.isWorkshop) TechCyan else MechanicalOrange
                            ),
                            modifier = Modifier.fillMaxWidth()
                          ) {
                            Row(
                              modifier = Modifier.padding(10.dp),
                              horizontalArrangement = Arrangement.SpaceBetween,
                              verticalAlignment = Alignment.CenterVertically
                            ) {
                              Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                  Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (holder.isWorkshop) TechCyan.copy(alpha = 0.2f) else MechanicalOrange.copy(alpha = 0.2f)
                                  ) {
                                    Text(
                                      text = if (holder.isWorkshop) "🛠️ ATELIER AVEC STOCK" else "🏪 VENDEUR PIÈCES",
                                      color = if (holder.isWorkshop) TechCyan else MechanicalOrange,
                                      fontWeight = FontWeight.Black,
                                      fontSize = 8.5.sp,
                                      modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                  }
                                  Spacer(modifier = Modifier.width(6.dp))
                                  Text(
                                    text = holder.holderName,
                                    color = PureWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                  )
                                }
                                Text(
                                  text = "📍 ${holder.wilaya} • Réf dispo : ${holder.matchedReference} • Stock : ${holder.quantityAvailable} u.",
                                  color = AluminumLight,
                                  fontSize = 10.sp
                                )
                                holder.mountingPriceDzd?.let { poseDzd ->
                                  Text(
                                    text = "🔧 Tarif montage sur place dans cet atelier : +$poseDzd DA",
                                    color = ElectricGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                  )
                                }
                              }

                              Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                  text = "${holder.partPriceDzd} DA",
                                  color = MechanicalOrange,
                                  fontWeight = FontWeight.Black,
                                  fontSize = 13.sp,
                                  fontFamily = FontFamily.Monospace
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                  Button(
                                    onClick = {
                                      orderConfirmationToast = "✅ Réservation stock confirmée auprès de ${holder.holderName} (${holder.phone}) pour ${cartItem.primaryOemReference}."
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                  ) {
                                    Text("Réserver", color = GraphiteDark, fontWeight = FontWeight.Black, fontSize = 9.5.sp)
                                  }
                                  OutlinedButton(
                                    onClick = {
                                      selectedMapPinId = holder.holderId
                                      selectedTab = 2
                                    },
                                    border = BorderStroke(1.dp, TechCyan),
                                    contentPadding = PaddingValues(horizontal = 7.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                  ) {
                                    Text("🗺️", fontSize = 10.sp)
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }

            // =========================================================================
            // TAB 2: CARTE GOOGLE MAPS EXACTE (VENDEURS, MAGASINS, ATELIERS & SHOWROOMS LOCATION)
            // =========================================================================
            2 -> {
              val allPins = SmartCartAndFreeAccessRepository.exactMapLocations
              val filteredPins = remember(selectedMapCategory, allPins) {
                if (selectedMapCategory == null) allPins
                else allPins.filter { it.category == selectedMapCategory }
              }
              val selectedPin = filteredPins.find { it.id == selectedMapPinId } ?: filteredPins.first()

              LazyColumn(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                // Filter chips for Map Categories
                item {
                  LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                      FilterChip(
                        selected = selectedMapCategory == null,
                        onClick = { selectedMapCategory = null },
                        label = { Text("Toutes Positions (${allPins.size})", fontSize = 10.5.sp) }
                      )
                    }
                    items(MapPinCategory.entries) { cat ->
                      val count = allPins.count { it.category == cat }
                      FilterChip(
                        selected = selectedMapCategory == cat,
                        onClick = {
                          selectedMapCategory = if (selectedMapCategory == cat) null else cat
                        },
                        label = { Text("${cat.badgeText} ($count)", fontSize = 10.5.sp) }
                      )
                    }
                  }
                }

                // Interactive Google Maps GPS Canvas
                item {
                  Card(
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("interactive_google_maps_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1926)),
                    border = BorderStroke(1.5.dp, TechCyan)
                  ) {
                    Column {
                      Row(
                        modifier = Modifier
                          .fillMaxWidth()
                          .background(GraphiteBlack)
                          .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Icon(Icons.Default.Map, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
                          Spacer(modifier = Modifier.width(6.dp))
                          Text(
                            text = "GOOGLE MAPS ENGINE • POSITIONS GPS EXACTES ALGÉRIE",
                            color = PureWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                          )
                        }
                        Text(
                          text = "GPS: ${selectedPin.latitude}, ${selectedPin.longitude}",
                          color = ElectricGreen,
                          fontSize = 9.5.sp,
                          fontFamily = FontFamily.Monospace
                        )
                      }

                      Box(
                        modifier = Modifier
                          .fillMaxWidth()
                          .height(220.dp)
                          .background(Color(0xFF0B1924))
                      ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                          val step = 30.dp.toPx()
                          var x = 0f
                          while (x < size.width) {
                            drawLine(
                              color = Color(0xFF153046),
                              start = Offset(x, 0f),
                              end = Offset(x, size.height),
                              strokeWidth = 1f
                            )
                            x += step
                          }
                          var y = 0f
                          while (y < size.height) {
                            drawLine(
                              color = Color(0xFF153046),
                              start = Offset(0f, y),
                              end = Offset(size.width, y),
                              strokeWidth = 1f
                            )
                            y += step
                          }

                          // Autoroute Est-Ouest A1 / RN4 vector path
                          drawLine(
                            color = Color(0xFF295F8A),
                            start = Offset(size.width * 0.08f, size.height * 0.42f),
                            end = Offset(size.width * 0.92f, size.height * 0.30f),
                            strokeWidth = 4f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 8f), 0f)
                          )

                          filteredPins.forEach { pin ->
                            val px = size.width * pin.canvasXPercent
                            val py = size.height * pin.canvasYPercent
                            val pinColor = Color(pin.category.colorHex)
                            val isSelected = pin.id == selectedPin.id

                            if (isSelected) {
                              drawCircle(
                                color = pinColor.copy(alpha = 0.28f),
                                radius = 26.dp.toPx(),
                                center = Offset(px, py)
                              )
                            }
                            drawCircle(
                              color = pinColor,
                              radius = if (isSelected) 10.dp.toPx() else 7.dp.toPx(),
                              center = Offset(px, py)
                            )
                          }
                        }

                        // Overlay clickable pills for each pin
                        filteredPins.forEach { pin ->
                          val isSelected = pin.id == selectedPin.id
                          val pinColor = Color(pin.category.colorHex)
                          Box(
                            modifier = Modifier
                              .fillMaxSize()
                              .padding(
                                start = (pin.canvasXPercent * 260).dp.coerceIn(8.dp, 240.dp),
                                top = (pin.canvasYPercent * 170).dp.coerceIn(8.dp, 165.dp)
                              )
                          ) {
                            Surface(
                              shape = RoundedCornerShape(8.dp),
                              color = if (isSelected) pinColor else GraphiteBlack.copy(alpha = 0.9f),
                              border = BorderStroke(1.dp, pinColor),
                              modifier = Modifier.clickable { selectedMapPinId = pin.id }
                            ) {
                              Text(
                                text = "📍 ${pin.wilaya.take(2)} • ${pin.name.take(16)}..",
                                color = if (isSelected) GraphiteDark else PureWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 8.5.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                              )
                            }
                          }
                        }
                      }

                      // Selected Pin Details Bar
                      Column(
                        modifier = Modifier
                          .fillMaxWidth()
                          .background(MetalCard)
                          .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                      ) {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(selectedPin.category.colorHex).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(selectedPin.category.colorHex))
                          ) {
                            Text(
                              text = selectedPin.category.badgeText,
                              color = Color(selectedPin.category.colorHex),
                              fontWeight = FontWeight.Black,
                              fontSize = 9.sp,
                              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                          }
                          Text(
                            text = "⭐ ${selectedPin.rating}/5 • ${selectedPin.openingHours}",
                            color = WarningYellow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                          )
                        }

                        Text(
                          text = selectedPin.name,
                          color = PureWhite,
                          fontWeight = FontWeight.Black,
                          fontSize = 14.sp
                        )

                        Text(
                          text = "📍 Adresse exacte : ${selectedPin.exactAddress} (${selectedPin.wilaya})",
                          color = OffWhite,
                          fontSize = 11.sp
                        )

                        Text(
                          text = "🧭 Coordonnées GPS : Lat ${selectedPin.latitude} • Lng ${selectedPin.longitude}",
                          color = TechCyan,
                          fontSize = 10.sp,
                          fontFamily = FontFamily.Monospace
                        )

                        Text(
                          text = "📦 Disponibilité : ${selectedPin.availableStockOrFleetSummary}",
                          color = AluminumLight,
                          fontSize = 10.5.sp
                        )

                        if (selectedPin.mountingLaborPriceRangeDzd.isNotBlank()) {
                          Text(
                            text = "🔧 Barème Montage : ${selectedPin.mountingLaborPriceRangeDzd}",
                            color = ElectricGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                          )
                        }

                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                          Button(
                            onClick = {
                              val geoUrl = "https://www.google.com/maps/search/?api=1&query=${selectedPin.latitude},${selectedPin.longitude}"
                              runCatching { uriHandler.openUri(geoUrl) }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                              .weight(1f)
                              .testTag("btn_open_external_google_maps")
                          ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                              text = "ITINÉRAIRE GOOGLE MAPS EXACT",
                              color = GraphiteDark,
                              fontWeight = FontWeight.Black,
                              fontSize = 10.sp
                            )
                          }

                          OutlinedButton(
                            onClick = {
                              orderConfirmationToast = "📞 Appel / Contact direct vers ${selectedPin.name} : ${selectedPin.phone}"
                            },
                            border = BorderStroke(1.dp, ElectricGreen),
                            shape = RoundedCornerShape(10.dp)
                          ) {
                            Text(
                              text = "📞 ${selectedPin.phone}",
                              color = ElectricGreen,
                              fontWeight = FontWeight.Bold,
                              fontSize = 10.sp
                            )
                          }
                        }
                      }
                    }
                  }
                }

                // All pins list below the map
                items(filteredPins, key = { it.id }) { pin ->
                  val pinColor = Color(pin.category.colorHex)
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (pin.id == selectedPin.id) MetalSurface else MetalCard,
                    border = BorderStroke(
                      if (pin.id == selectedPin.id) 1.5.dp else 1.dp,
                      if (pin.id == selectedPin.id) pinColor else MetalBorder
                    ),
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable { selectedMapPinId = pin.id }
                  ) {
                    Row(
                      modifier = Modifier.padding(12.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = "${pin.category.badgeText} • ${pin.wilaya}",
                          color = pinColor,
                          fontWeight = FontWeight.Black,
                          fontSize = 9.5.sp
                        )
                        Text(pin.name, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(pin.exactAddress, color = AluminumLight, fontSize = 10.sp)
                      }
                      Text(
                        text = "${pin.latitude}\n${pin.longitude}",
                        color = TechCyan,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                      )
                    }
                  }
                }
              }
            }

            // =========================================================================
            // TAB 3: COMPTE PARTICULIER (AUTO-ENTRETIEN DIY : 3 500 DA/AN • 1 VIN + 1 200 DA/VIN SUPP. OU 10 000 DA ACCÈS LIBRE)
            // =========================================================================
            3 -> {
              LazyColumn(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                // Choix entre Formule Basique 3 500 DA/an et Accès Libre Intégral 10 000 DA/an
                item {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                  ) {
                    ParticulierPlanTier.entries.forEach { tier ->
                      val isSelected = particulierAccountState.currentTier == tier
                      Card(
                        modifier = Modifier
                          .weight(1f)
                          .clickable { onSelectParticulierTier(tier) }
                          .testTag("card_particulier_tier_${tier.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                          containerColor = if (isSelected) Color(0xFF1A2936) else MetalCard
                        ),
                        border = BorderStroke(
                          if (isSelected) 1.5.dp else 1.dp,
                          if (isSelected) TechCyan else MetalBorder
                        )
                      ) {
                        Column(
                          modifier = Modifier.padding(12.dp),
                          verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                          Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) TechCyan.copy(alpha = 0.2f) else GraphiteBlack
                          ) {
                            Text(
                              text = tier.badgeText,
                              color = if (isSelected) TechCyan else AluminumLight,
                              fontWeight = FontWeight.Black,
                              fontSize = 8.5.sp,
                              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                          }
                          Text(
                            text = tier.title,
                            color = PureWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                          )
                          Text(
                            text = "${tier.annualPriceDzd} DA / an",
                            color = MechanicalOrange,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace
                          )
                          Text(
                            text = if (tier == ParticulierPlanTier.BASIC_DIY_3500) {
                              "• 1 numéro de châssis inclus\n• +1 200 DA par châssis supp.\n• Pièces, stocks & schémas DIY essentiels"
                            } else {
                              "• Accès 100% libre sans restriction\n• Schémas électriques & OR complets\n• Jusqu'à 5 châssis inclus"
                            },
                            color = AluminumLight,
                            fontSize = 10.sp
                          )
                        }
                      }
                    }
                  }
                }

                // Résumé Financier & Châssis (VIN) enregistrés dans le Compte Particulier
                item {
                  Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MetalCard),
                    border = BorderStroke(1.dp, ElectricGreen)
                  ) {
                    Column(
                      modifier = Modifier.padding(14.dp),
                      verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = "🚗 VOS NUMÉROS DE CHÂSSIS (VIN) ENREGISTRÉS (${particulierAccountState.registeredVins.size})",
                          color = PureWhite,
                          fontWeight = FontWeight.Black,
                          fontSize = 12.sp
                        )
                        Text(
                          text = "Total : ${particulierAccountState.totalAnnualCostDzd} DA / an",
                          color = ElectricGreen,
                          fontWeight = FontWeight.Black,
                          fontSize = 13.sp,
                          fontFamily = FontFamily.Monospace
                        )
                      }

                      Text(
                        text = particulierAccountState.statusMessage,
                        color = TechCyan,
                        fontSize = 10.5.sp
                      )

                      particulierAccountState.registeredVins.forEach { regVin ->
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = GraphiteBlack,
                          border = BorderStroke(1.dp, MetalBorder),
                          modifier = Modifier.fillMaxWidth()
                        ) {
                          Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                          ) {
                            Column(modifier = Modifier.weight(1f)) {
                              Text(
                                text = "${regVin.vehicleLabel} (${regVin.year})",
                                color = PureWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                              )
                              Text(
                                text = "VIN : ${regVin.vin} • Moteur : ${regVin.engineCode}",
                                color = TechCyan,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                              )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                              Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (regVin.isPrimaryIncludedVin) ElectricGreen.copy(alpha = 0.18f) else MechanicalOrange.copy(alpha = 0.2f)
                              ) {
                                Text(
                                  text = if (regVin.isPrimaryIncludedVin) "1er VIN INCLUS" else "+${regVin.extraFeePaidDzd} DA/AN",
                                  color = if (regVin.isPrimaryIncludedVin) ElectricGreen else MechanicalOrange,
                                  fontWeight = FontWeight.Black,
                                  fontSize = 9.sp,
                                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                              }
                              if (!regVin.isPrimaryIncludedVin) {
                                IconButton(
                                  onClick = { onRemoveExtraVinFromParticulier(regVin.vin) },
                                  modifier = Modifier.size(28.dp)
                                ) {
                                  Icon(Icons.Default.DeleteOutline, contentDescription = "Supprimer", tint = PerformanceRedGlow, modifier = Modifier.size(16.dp))
                                }
                              }
                            }
                          }
                        }
                      }

                      HorizontalDivider(color = MetalBorder)

                      Text(
                        text = "➕ AJOUTER UN AUTRE NUMÉRO DE CHÂSSIS (+${particulierAccountState.extraVinUnitFeeDzd} DA / CHÂSSIS)",
                        color = MechanicalOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                      )

                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        OutlinedTextField(
                          value = newVinInput,
                          onValueChange = { newVinInput = it.uppercase().take(17) },
                          label = { Text("Nouveau VIN (17 caract.)", fontSize = 10.sp) },
                          placeholder = { Text("Ex: VF1RJA00568129401", fontSize = 10.sp) },
                          singleLine = true,
                          modifier = Modifier
                            .weight(1f)
                            .testTag("input_particulier_extra_vin"),
                          colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TechCyan,
                            unfocusedBorderColor = MetalBorder,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                          )
                        )
                        OutlinedTextField(
                          value = newVehicleModelInput,
                          onValueChange = { newVehicleModelInput = it },
                          label = { Text("Modèle & Moteur", fontSize = 10.sp) },
                          placeholder = { Text("Ex: Renault Clio 4 1.5 dCi", fontSize = 10.sp) },
                          singleLine = true,
                          modifier = Modifier.weight(1f),
                          colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TechCyan,
                            unfocusedBorderColor = MetalBorder,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                          )
                        )
                      }

                      Button(
                        onClick = {
                          onAddExtraVinToParticulier(
                            newVinInput.ifBlank { "VF1RJA00568129401" },
                            newVehicleModelInput.ifBlank { "Renault Clio 4 1.5 dCi 90ch" },
                            newEngineInput.ifBlank { "K9K 608" },
                            2018
                          )
                          newVinInput = ""
                          newVehicleModelInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                          .fillMaxWidth()
                          .testTag("btn_add_extra_vin_particulier")
                      ) {
                        Icon(Icons.Default.AddCircle, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                          text = "AJOUTER CE CHÂSSIS (+${particulierAccountState.extraVinUnitFeeDzd} DA / AN)",
                          color = PureWhite,
                          fontWeight = FontWeight.Black,
                          fontSize = 10.5.sp
                        )
                      }
                    }
                  }
                }

                // Aperçu des Schémas de Réparation & Ordres de Réparation DIY (Partiel à 3 500 DA vs Complet à 10 000 DA)
                item {
                  val sampleDiy = SmartCartAndFreeAccessRepository.freeAccessCatalogItems.first()
                  Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MetalCard),
                    border = BorderStroke(1.dp, TechCyan)
                  ) {
                    Column(
                      modifier = Modifier.padding(14.dp),
                      verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text(
                          text = "🛠️ SCHÉMAS & MÉTHODES DE RÉPARATION DIY (VOTRE VÉHICULE)",
                          color = PureWhite,
                          fontWeight = FontWeight.Black,
                          fontSize = 11.5.sp
                        )
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = if (particulierAccountState.hasFullUnrestrictedRepairManuals) ElectricGreen.copy(alpha = 0.2f) else MechanicalOrange.copy(alpha = 0.2f)
                        ) {
                          Text(
                            text = if (particulierAccountState.hasFullUnrestrictedRepairManuals) "ACCÈS LIBRE 100% DÉBLOQUÉ" else "ACCÈS STANDARD PARTICULIER",
                            color = if (particulierAccountState.hasFullUnrestrictedRepairManuals) ElectricGreen else MechanicalOrange,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                          )
                        }
                      }

                      Text(
                        text = "Opération : ${sampleDiy.partName} • ${sampleDiy.diyDifficultyLabel}",
                        color = TechCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                      )

                      sampleDiy.diyRepairStepsPreview.forEach { step ->
                        Text(
                          text = "✔ $step",
                          color = OffWhite,
                          fontSize = 10.5.sp
                        )
                      }

                      if (!particulierAccountState.hasFullUnrestrictedRepairManuals) {
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = Color(0xFF28160B),
                          border = BorderStroke(1.dp, MechanicalOrange),
                          modifier = Modifier.fillMaxWidth()
                        ) {
                          Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                          ) {
                            Text(
                              text = "🔒 ${sampleDiy.fullRepairManualLockedNote}",
                              color = MechanicalOrange,
                              fontWeight = FontWeight.SemiBold,
                              fontSize = 10.5.sp
                            )
                            Button(
                              onClick = { onSelectParticulierTier(ParticulierPlanTier.FULL_LIBRE_10000) },
                              colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
                              shape = RoundedCornerShape(8.dp),
                              modifier = Modifier.fillMaxWidth()
                            ) {
                              Text(
                                text = "🔓 DÉBLOQUER L'ACCÈS LIBRE INTÉGRAL (10 000 DA / AN)",
                                color = PureWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                              )
                            }
                          }
                        }
                      } else {
                        Surface(
                          shape = RoundedCornerShape(10.dp),
                          color = Color(0xFF0C241D),
                          border = BorderStroke(1.dp, ElectricGreen),
                          modifier = Modifier.fillMaxWidth()
                        ) {
                          Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                          ) {
                            Text(
                              text = "✅ MANUEL CONSTRUCTEUR & SCHÉMA ÉLECTRIQUE INTÉGRAL DÉBLOQUÉS (PASS 10 000 DA)",
                              color = ElectricGreen,
                              fontWeight = FontWeight.Black,
                              fontSize = 10.5.sp
                            )
                            Text(
                              text = "• Pigeage volant moteur Ø 6mm + Arbre à cames M10 • Courroie 108 dents • Couple pompe à eau : 14 Nm • Galet tendeur : 21 Nm • Poulie vilebrequin : 40 Nm + 45° • Brochage motoventilateur Relais R1/R2 voie 14 ECU.",
                              color = OffWhite,
                              fontSize = 10.sp,
                              fontFamily = FontFamily.Monospace
                            )
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
