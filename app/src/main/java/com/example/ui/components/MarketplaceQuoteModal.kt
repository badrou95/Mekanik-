package com.example.ui.components

import androidx.compose.foundation.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.VendorPartOffer
import com.example.ui.theme.*

@Composable
fun MarketplaceQuoteModal(
  offers: List<VendorPartOffer>,
  onSubmitGroupQuote: (List<String>) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedWilaya by remember { mutableStateOf("Toutes les Wilayas (58)") }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .background(GraphiteDark)
        .testTag("marketplace_quote_dialog"),
      color = GraphiteDark
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(PerformanceRed.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Storefront, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "B2B MARKETPLACE & DEVIS",
                style = MaterialTheme.typography.titleMedium.copy(
                  color = PureWhite,
                  fontWeight = FontWeight.Black
                )
              )
              Text(
                text = "Comparateur de prix & Disponibilité 58 Wilayas",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 11.sp)
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(MetalCard)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Group Quote Trigger Banner
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, PerformanceRed.copy(alpha = 0.4f))
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Appel d'Offres Groupé en 1 Clic",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PureWhite)
              )
              Text(
                text = "Interrogez instantanément les grossistes d'Alger, Oran et Sétif",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
              )
            }
            Button(
              onClick = { onSubmitGroupQuote(listOf("5Q0 615 301 F", "5Q0 698 151 BR")) },
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed, contentColor = PureWhite),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(text = "Lancer", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "OFFRES DISPONIBLES EN STOCK (EXPÉDITION EXPRESS)",
          style = MaterialTheme.typography.labelMedium.copy(color = AluminumMuted, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(offers) { offer ->
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MetalCard,
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(
                      text = offer.vendorName,
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                    )
                    Text(
                      text = "${offer.wilaya} • Marque : ${offer.brandName}",
                      style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.sp)
                    )
                  }

                  Text(
                    text = "${offer.priceDzd.toInt()} DA",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = PerformanceRedGlow)
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Inventory2, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "${offer.inStockQuantity} en stock",
                      style = MaterialTheme.typography.labelSmall.copy(color = ElectricGreen, fontWeight = FontWeight.Bold)
                    )
                  }

                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = AluminumLight, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "${offer.deliveryCarrier} (+${offer.deliveryCostDzd.toInt()} DA)",
                      style = MaterialTheme.typography.labelSmall.copy(color = AluminumLight)
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
