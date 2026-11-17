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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.RentalDepositStatus
import com.example.data.model.ShortTermRentalContract
import com.example.ui.theme.*

@Composable
fun ShortTermRentalCheckModal(
  contracts: List<ShortTermRentalContract>,
  onUpdateRentalReturnMetrics: (String, Int, Int) -> Unit,
  onCloseContractAndReleaseDeposit: (String) -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .fillMaxHeight(0.90f),
      shape = RoundedCornerShape(18.dp),
      color = GraphiteDark,
      border = BorderStroke(1.dp, EvBlue.copy(alpha = 0.7f))
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(MetalCard)
            .padding(horizontal = 16.dp, vertical = 14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(EvBlue.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.CarRental, contentDescription = null, tint = EvBlue, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "CONTRATS LOCATION, CAUTION & CHECK-OUT / IN",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 13.5.sp
              )
              Text(
                text = "Comparateur départ/retour, jauge carburant 8èmes & calcul dépassement km",
                color = AluminumGray,
                fontSize = 10.5.sp
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight)
          }
        }

        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(contracts, key = { it.id }) { contract ->
            val depositBadgeColor = when (contract.depositStatus) {
              RentalDepositStatus.CAUTION_BLOQUEE -> TechCyan
              RentalDepositStatus.CAUTION_LIBEREE -> ElectricGreen
              RentalDepositStatus.RETENUE_PARTIELLE -> MechanicalOrange
            }

            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, depositBadgeColor.copy(alpha = 0.7f))
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(
                      text = "${contract.contractNumber} • ${contract.vehicleModel}",
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 13.sp
                    )
                    Text(
                      text = "Locataire : ${contract.renterFullName} (${contract.renterPhone}) • Immat : ${contract.licensePlate}",
                      color = AluminumLight,
                      fontSize = 10.5.sp
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = depositBadgeColor.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, depositBadgeColor)
                  ) {
                    Text(
                      text = contract.depositStatus.label,
                      color = depositBadgeColor,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }

                // Grid of Departure vs Return metrics
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, MetalBorder),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(
                        text = "Kilométrage : ${contract.departureKm} km → ${contract.returnKm} km (${contract.drivenKm} km parcourus)",
                        color = PureWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                      )
                      Text(
                        text = "Forfait : ${contract.allowedTotalKm} km",
                        color = TechCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                      )
                    }

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(
                        text = "Niveau Carburant : Départ ${contract.departureFuelEighths}/8 → Retour ${contract.returnFuelEighths}/8",
                        color = if (contract.missingFuelEighths == 0) ElectricGreen else WarningYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                      )
                      Text(
                        text = "Photos Check : ${contract.checkOutPhotosCount} AV / ${contract.checkInPhotosCount} AP",
                        color = AluminumGray,
                        fontSize = 10.5.sp
                      )
                    }

                    Divider(color = MetalBorder, modifier = Modifier.padding(vertical = 2.dp))

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Column {
                        Text(
                          text = "Location (${contract.rentalDays}j × ${contract.dailyRateDzd} DA) : ${contract.baseRentalTotalDzd} DA",
                          color = AluminumLight,
                          fontSize = 10.5.sp
                        )
                        Text(
                          text = "Supplément Km (+${contract.extraKm} km) : +${contract.extraKmFeeDzd} DA | Carburant manquant : +${contract.missingFuelFeeDzd} DA",
                          color = if (contract.extraKmFeeDzd + contract.missingFuelFeeDzd > 0) MechanicalOrange else ElectricGreen,
                          fontSize = 10.5.sp,
                          fontWeight = FontWeight.Bold
                        )
                      }
                    }

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = "Caution déposée : ${contract.depositAmountDzd} DA",
                        color = TechCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                      )
                      Text(
                        text = "TOTAL À FACTURER : ${contract.totalFinalInvoiceDzd} DA",
                        color = ElectricGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                      )
                    }
                  }
                }

                if (!contract.isClosedAndSigned) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    OutlinedButton(
                      onClick = {
                        onUpdateRentalReturnMetrics(
                          contract.id,
                          contract.returnKm,
                          8 // Remettre le plein 8/8
                        )
                      },
                      border = BorderStroke(1.dp, TechCyan),
                      modifier = Modifier.weight(1f)
                    ) {
                      Text("Plein fait (8/8)", color = TechCyan, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                      onClick = { onCloseContractAndReleaseDeposit(contract.id) },
                      colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
                      modifier = Modifier.weight(1.4f)
                    ) {
                      Icon(Icons.Default.Verified, contentDescription = null, tint = GraphiteBlack, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "CLÔTURER & LIBÉRER CAUTION",
                        color = GraphiteBlack,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.5.sp
                      )
                    }
                  }
                } else {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ElectricGreen.copy(alpha = 0.16f),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(8.dp),
                      horizontalArrangement = Arrangement.Center,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "Contrat clôturé • Quitus de retour signé & Caution soldée",
                        color = ElectricGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
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
