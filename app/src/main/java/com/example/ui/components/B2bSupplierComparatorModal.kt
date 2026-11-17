package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ActiveB2bOrderTracking
import com.example.data.model.SupplierComparisonOffer
import com.example.ui.theme.*

@Composable
fun B2bSupplierComparatorModal(
  offers: List<SupplierComparisonOffer>,
  activeOrders: List<ActiveB2bOrderTracking>,
  onOrderFromSupplier: (SupplierComparisonOffer) -> Unit,
  onAdvanceOrderDelivery: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var sortByBestPrice by remember { mutableStateOf(true) }

  val sortedOffers = remember(offers, sortByBestPrice) {
    if (sortByBestPrice) offers.sortedBy { it.wholesalePriceHtDzd }
    else offers.sortedByDescending { it.isFastestDelivery }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)
        .testTag("b2b_supplier_comparator_modal"),
      shape = RoundedCornerShape(18.dp),
      color = GraphiteDark,
      border = BorderStroke(1.dp, MechanicalOrange)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(MetalCard)
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MechanicalOrange.copy(alpha = 0.25f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MechanicalOrange)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "COMPARATEUR GROSSISTES B2B & LIVREUR",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
              )
              Text(
                text = "Prix d'achat HT atelier, calcul de marge & suivi coursier express",
                color = AluminumLight,
                fontSize = 11.sp
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite)
          }
        }

        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 14.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          contentPadding = PaddingValues(vertical = 14.dp)
        ) {
          // Active B2B Courier Tracking Section
          if (activeOrders.isNotEmpty()) {
            item {
              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
                border = BorderStroke(1.dp, ElectricGreen)
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text(
                    text = "🛵 SUIVI EN DIRECT DES LIVRAISONS GROSSISTES B2B (${activeOrders.size})",
                    color = ElectricGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                  )
                  activeOrders.forEach { order ->
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = MetalCard,
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Column(modifier = Modifier.weight(1f)) {
                          Text(
                            text = "${order.orderId} • ${order.supplierName}",
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                          )
                          Text(
                            text = "Livreur : ${order.courierName} (${order.courierPhone})",
                            color = AluminumLight,
                            fontSize = 10.5.sp
                          )
                          Text(
                            text = "Statut : ${order.status.label} • Arrivée estimée : ${order.etaMinutes} min",
                            color = TechCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                          )
                        }
                        OutlinedButton(
                          onClick = { onAdvanceOrderDelivery(order.orderId) },
                          border = BorderStroke(1.dp, ElectricGreen),
                          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                          Text("Réceptionner", color = ElectricGreen, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                        }
                      }
                    }
                  }
                }
              }
            }
          }

          // Filter / Sort Row
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = { sortByBestPrice = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (sortByBestPrice) PerformanceRed else MetalCard,
                  contentColor = PureWhite
                )
              ) {
                Text("Trier par Meilleur Prix HT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
              Button(
                onClick = { sortByBestPrice = false },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (!sortByBestPrice) MechanicalOrange else MetalCard,
                  contentColor = PureWhite
                )
              ) {
                Text("Trier par Livraison Express", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          // Supplier Offers List
          items(sortedOffers, key = { it.id }) { offer ->
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(
                1.dp,
                when {
                  offer.isBestPrice -> ElectricGreen
                  offer.isFastestDelivery -> MechanicalOrange
                  else -> MetalBorder
                }
              ),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(offer.brand, color = MechanicalOrange, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    Text(offer.partName, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    Text("Réf : ${offer.oemReference}", color = AluminumGray, fontSize = 10.5.sp)
                  }
                  if (offer.isBestPrice) {
                    Surface(shape = RoundedCornerShape(6.dp), color = ElectricGreen.copy(alpha = 0.2f), border = BorderStroke(1.dp, ElectricGreen)) {
                      Text("MEILLEUR PRIX HT", color = ElectricGreen, fontWeight = FontWeight.Black, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp))
                    }
                  } else if (offer.isFastestDelivery) {
                    Surface(shape = RoundedCornerShape(6.dp), color = MechanicalOrange.copy(alpha = 0.2f), border = BorderStroke(1.dp, MechanicalOrange)) {
                      Text("LIVRAISON 45 MIN", color = MechanicalOrange, fontWeight = FontWeight.Black, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp))
                    }
                  }
                }

                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text("Prix Achat Atelier HT", color = AluminumGray, fontSize = 10.sp)
                      Text("${offer.wholesalePriceHtDzd} DZD", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }
                    Column {
                      Text("Prix Public Conseillé", color = AluminumGray, fontSize = 10.sp)
                      Text("${offer.recommendedRetailPriceDzd} DZD", color = AluminumLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                      Text("Marge Atelier Net", color = AluminumGray, fontSize = 10.sp)
                      Text("+${offer.workshopMarginDzd} DA", color = ElectricGreen, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                  }
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text("🏪 ${offer.supplierName}", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text("📍 ${offer.supplierCity} • 🚚 ${offer.deliverySlaLabel} • Stock: ${offer.availableStock} pcs", color = TechCyan, fontSize = 10.5.sp)
                  }
                  Button(
                    onClick = { onOrderFromSupplier(offer) },
                    colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed, contentColor = PureWhite),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                  ) {
                    Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Commander Express", fontWeight = FontWeight.Bold, fontSize = 11.sp)
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
