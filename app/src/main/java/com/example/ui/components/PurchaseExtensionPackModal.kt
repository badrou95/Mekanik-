package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ui.theme.*

@Composable
fun PurchaseExtensionPackModal(
  currentAllowedAccounts: Int,
  purchasedPacks: Int,
  onPurchaseConfirmed: (Int) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedPackCount by remember { mutableStateOf(1) } // 1 pack = 2 comptes (2500 DA)
  var selectedPaymentMethod by remember { mutableStateOf("CIB_EDAHABIA") }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.92f)
        .fillMaxHeight(0.85f)
        .testTag("modal_purchase_extension_pack"),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, PerformanceRed)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp)
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
                .size(40.dp)
                .clip(CircleShape)
                .background(PerformanceRed),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.GroupAdd, contentDescription = null, tint = PureWhite, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "PACK EXTENSION SOUS-COMPTES",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = PureWhite
                )
              )
              Text(
                text = "+2 Sous-Comptes pour 2 500 DA",
                style = MaterialTheme.typography.bodySmall.copy(color = PerformanceRedGlow, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight)
          }
        }

        HorizontalDivider(color = MetalBorder, modifier = Modifier.padding(vertical = 12.dp))

        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Summary Banner
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MetalCard),
            border = BorderStroke(1.dp, MetalBorder)
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "CAPACITÉ ACTUELLE DE L'ENTREPRISE",
                style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold)
              )
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "$currentAllowedAccounts Sous-comptes autorisés",
                  style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold)
                )
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = PerformanceRedDark.copy(alpha = 0.4f),
                  border = BorderStroke(1.dp, PerformanceRed)
                ) {
                  Text(
                    text = "$purchasedPacks PACK(S) ACHETÉ(S)",
                    style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontSize = 9.5.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
              Text(
                text = "Étendez votre équipe avec des mécaniciens supplémentaires, des conseillers ou des magasiniers selon vos besoins d'atelier.",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
              )
            }
          }

          // Choose Pack Count
          Text(
            text = "SÉLECTIONNER LE NOMBRE DE PACKS :",
            style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
          )

          val packOptions = listOf(
            Triple(1, "+2 Sous-comptes", "2 500 DA"),
            Triple(2, "+4 Sous-comptes", "5 000 DA"),
            Triple(3, "+6 Sous-comptes", "7 500 DA"),
            Triple(5, "+10 Sous-comptes (Grand Atelier)", "12 000 DA (-500 DA)")
          )

          packOptions.forEach { (count, label, priceStr) ->
            val isSelected = selectedPackCount == count
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) PerformanceRedDark.copy(alpha = 0.25f) else MetalCard,
              border = BorderStroke(
                if (isSelected) 1.5.dp else 1.dp,
                if (isSelected) PerformanceRed else MetalBorder
              ),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedPackCount = count }
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  RadioButton(
                    selected = isSelected,
                    onClick = { selectedPackCount = count },
                    colors = RadioButtonDefaults.colors(selectedColor = PerformanceRed)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Text(
                      text = label,
                      style = MaterialTheme.typography.titleSmall.copy(
                        color = if (isSelected) PureWhite else OffWhite,
                        fontWeight = FontWeight.Bold
                      )
                    )
                    Text(
                      text = "${count * 2} utilisateurs supplémentaires instantanés",
                      style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 10.5.sp)
                    )
                  }
                }

                Text(
                  text = priceStr,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) PerformanceRedGlow else PureWhite
                  )
                )
              }
            }
          }

          // Payment Method Selector
          Text(
            text = "MOYEN DE PAIEMENT SÉCURISÉ (ALGÉRIE) :",
            style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (selectedPaymentMethod == "CIB_EDAHABIA") TechCyan.copy(alpha = 0.15f) else MetalCard,
              border = BorderStroke(
                if (selectedPaymentMethod == "CIB_EDAHABIA") 1.5.dp else 1.dp,
                if (selectedPaymentMethod == "CIB_EDAHABIA") TechCyan else MetalBorder
              ),
              modifier = Modifier
                .weight(1f)
                .clickable { selectedPaymentMethod = "CIB_EDAHABIA" }
            ) {
              Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(Icons.Default.CreditCard, contentDescription = null, tint = TechCyan, modifier = Modifier.size(22.dp))
                Text(
                  text = "CIB / Edahabia",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                )
                Text(
                  text = "Validation instantanée",
                  style = MaterialTheme.typography.bodySmall.copy(color = ElectricGreen, fontSize = 9.sp)
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (selectedPaymentMethod == "ESPECES") WarningAmber.copy(alpha = 0.15f) else MetalCard,
              border = BorderStroke(
                if (selectedPaymentMethod == "ESPECES") 1.5.dp else 1.dp,
                if (selectedPaymentMethod == "ESPECES") WarningAmber else MetalBorder
              ),
              modifier = Modifier
                .weight(1f)
                .clickable { selectedPaymentMethod = "ESPECES" }
            ) {
              Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(Icons.Default.Payments, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(22.dp))
                Text(
                  text = "Espèces Atelier",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                )
                Text(
                  text = "Facture reçue",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 9.sp)
                )
              }
            }
          }
        }

        HorizontalDivider(color = MetalBorder, modifier = Modifier.padding(vertical = 12.dp))

        // Total & Action Button
        val totalDzd = when (selectedPackCount) {
          1 -> 2500
          2 -> 5000
          3 -> 7500
          5 -> 12000
          else -> selectedPackCount * 2500
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(text = "TOTAL À PAYER :", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted))
            Text(
              text = "$totalDzd DA",
              style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
                color = PerformanceRedGlow
              )
            )
          }

          Button(
            onClick = { onPurchaseConfirmed(selectedPackCount) },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            modifier = Modifier.testTag("btn_confirm_purchase_extension")
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "ACTIVER +${selectedPackCount * 2} COMPTES",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
            )
          }
        }
      }
    }
  }
}
