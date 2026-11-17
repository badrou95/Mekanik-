package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun PurchaseFullSoftwarePackageModal(
  supplement: FullSoftwarePackageSupplement,
  onConfirmPurchase: (PaymentMethod) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CIB_EDAHABIA) }
  var isProcessing by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .testTag("modal_purchase_full_software_supplement"),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, PerformanceRedGlow)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(18.dp)
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
                .size(44.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(PerformanceRed, MechanicalOrange))),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Apps, contentDescription = null, tint = PureWhite, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "PACK INTÉGRAL LOGICIELS OEM",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = PureWhite
                )
              )
              Text(
                text = "Tous les catalogues EPC & manuels constructeurs (+10 000 DA)",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.5.sp)
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Hero Offer Card
          item {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(PerformanceRed, MechanicalOrange)))
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PerformanceRedDark,
                    border = BorderStroke(1.dp, PerformanceRed)
                  ) {
                    Text(
                      text = "COMPLÉMENT ENTREPRISE",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                      ),
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                  }

                  Text(
                    text = "${supplement.priceDzd} DA / an",
                    style = MaterialTheme.typography.titleLarge.copy(
                      color = PerformanceRedGlow,
                      fontWeight = FontWeight.Black,
                      fontSize = 20.sp
                    )
                  )
                }

                Text(
                  text = supplement.title,
                  style = MaterialTheme.typography.titleMedium.copy(
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                  )
                )

                Text(
                  text = supplement.description,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    color = AluminumLight,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp
                  )
                )

                HorizontalDivider(color = MetalBorder, thickness = 0.5.dp)

                Text(
                  text = "LOGICIELS OFFICIELS & CATALOGUES INCLUS DANS LE PACK :",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = TechCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                  )
                )

                // Software badges
                val softwareBadges = listOf(
                  "ETKA 8.3 (VAG)" to 0xFF00E5FF,
                  "ELSAWin Pro (VAG)" to 0xFF00E5FF,
                  "Stellantis ServiceBox" to 0xFFFF7A00,
                  "DocBackup & SEDRE" to 0xFFFF7A00,
                  "Renault Dialogys 4.90" to 0xFFFFD600,
                  "Renault Can-CLIP" to 0xFFFFD600,
                  "BMW ISTA-D & ETK" to 0xFF40C4FF,
                  "Mercedes WIS / EPC" to 0xFF40C4FF,
                  "Microcat Asiatiques" to 0xFF00E676,
                  "TecDoc Professional" to 0xFFE53935,
                  "HaynesPro WorkshopData" to 0xFFE53935
                )

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  softwareBadges.take(3).forEach { (name, colorHex) ->
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = GraphiteBlack,
                      border = BorderStroke(0.5.dp, Color(colorHex))
                    ) {
                      Text(
                        text = name,
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = Color(colorHex),
                          fontSize = 9.5.sp,
                          fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                      )
                    }
                  }
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  softwareBadges.drop(3).take(4).forEach { (name, colorHex) ->
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = GraphiteBlack,
                      border = BorderStroke(0.5.dp, Color(colorHex))
                    ) {
                      Text(
                        text = name,
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = Color(colorHex),
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                      )
                    }
                  }
                }
              }
            }
          }

          // Key Advantages Checklist
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                  text = "AVANTAGES CONCESSIONNAIRE & ATELIER ULTRA PRO :",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = AluminumMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  )
                )

                supplement.advantages.forEach { adv ->
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = ElectricGreen,
                      modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = adv,
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = AluminumLight,
                        fontSize = 11.5.sp
                      )
                    )
                  }
                }
              }
            }
          }

          // Payment Methods
          item {
            Text(
              text = "MODE DE RÈGLEMENT (FACTURATION FASCICULE ATELIER) :",
              style = MaterialTheme.typography.labelSmall.copy(
                color = AluminumMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              PaymentMethod.entries.forEach { method ->
                val isSelected = selectedPaymentMethod == method
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (isSelected) Color(0xFF1E272E) else MetalCard,
                  border = BorderStroke(
                    if (isSelected) 1.5.dp else 1.dp,
                    if (isSelected) PerformanceRed else MetalBorder
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedPaymentMethod = method }
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                        when (method) {
                          PaymentMethod.CIB_EDAHABIA -> Icons.Default.CreditCard
                          PaymentMethod.VIREMENT_BANCAIRE -> Icons.Default.AccountBalance
                          PaymentMethod.ESPECES -> Icons.Default.Payments
                          PaymentMethod.CHEQUE -> Icons.Default.ReceiptLong
                        },
                        contentDescription = null,
                        tint = if (isSelected) PerformanceRedGlow else AluminumLight,
                        modifier = Modifier.size(18.dp)
                      )
                      Spacer(modifier = Modifier.width(10.dp))
                      Text(
                        text = method.label,
                        style = MaterialTheme.typography.bodySmall.copy(
                          color = PureWhite,
                          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                          fontSize = 12.sp
                        )
                      )
                    }

                    RadioButton(
                      selected = isSelected,
                      onClick = { selectedPaymentMethod = method },
                      colors = RadioButtonDefaults.colors(
                        selectedColor = PerformanceRed,
                        unselectedColor = AluminumMuted
                      )
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text("ANNULER", color = AluminumLight)
          }

          Button(
            onClick = {
              isProcessing = true
              onConfirmPurchase(selectedPaymentMethod)
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
            modifier = Modifier
              .weight(1.5f)
              .testTag("btn_confirm_purchase_full_software_supplement")
          ) {
            Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "ACTIVER (+10 000 DA)",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite
              )
            )
          }
        }
      }
    }
  }
}
