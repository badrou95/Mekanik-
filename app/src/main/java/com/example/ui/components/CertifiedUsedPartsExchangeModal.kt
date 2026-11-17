package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.model.CertifiedUsedPartOffer
import com.example.data.model.CoreReturnConsigneItem
import com.example.data.model.CoreReturnStatus
import com.example.ui.theme.*

@Composable
fun CertifiedUsedPartsExchangeModal(
  usedParts: List<CertifiedUsedPartOffer>,
  coreConsignes: List<CoreReturnConsigneItem>,
  onReserveUsedPart: (String) -> Unit,
  onAdvanceCoreReturnStatus: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Pièces Occasion & Échange Standard, 1 = Suivi Consignes (Vieille Matière)

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
      border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.7f))
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
                .background(ElectricGreen.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Autorenew, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "BOURSE PIÈCES D'OCCASION CERTIFIÉES & CONSIGNES",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
              )
              Text(
                text = "Réseau Casses Agréées (Grade A/B) • Échange Standard & Cautions Vieille Matière",
                color = AluminumGray,
                fontSize = 10.5.sp
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight)
          }
        }

        // Tabs
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (selectedTab == 0) ElectricGreen.copy(alpha = 0.22f) else MetalSurface,
            border = BorderStroke(1.dp, if (selectedTab == 0) ElectricGreen else MetalBorder),
            modifier = Modifier
              .weight(1f)
              .clickable { selectedTab = 0 }
          ) {
            Row(
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Verified, contentDescription = null, tint = if (selectedTab == 0) ElectricGreen else AluminumGray, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "OCCASION & RECONDITIONNÉ (${usedParts.size})",
                color = if (selectedTab == 0) PureWhite else AluminumGray,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (selectedTab == 1) MechanicalOrange.copy(alpha = 0.22f) else MetalSurface,
            border = BorderStroke(1.dp, if (selectedTab == 1) MechanicalOrange else MetalBorder),
            modifier = Modifier
              .weight(1f)
              .clickable { selectedTab = 1 }
          ) {
            Row(
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.AssignmentReturn, contentDescription = null, tint = if (selectedTab == 1) MechanicalOrange else AluminumGray, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "CONSIGNES VIEILLE MATIÈRE (${coreConsignes.size})",
                color = if (selectedTab == 1) PureWhite else AluminumGray,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp
              )
            }
          }
        }

        if (selectedTab == 0) {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(usedParts, key = { it.id }) { part ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, if (part.isReserved) TechCyan else ElectricGreen.copy(alpha = 0.6f))
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = ElectricGreen.copy(alpha = 0.2f),
                      border = BorderStroke(1.dp, ElectricGreen)
                    ) {
                      Text(
                        text = "${part.grade.code} • Garantie ${part.warrantyMonths} mois",
                        color = ElectricGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                      )
                    }

                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = MechanicalOrange.copy(alpha = 0.2f)
                    ) {
                      Text(
                        text = "-${part.savingsPercent}% vs Neuf OEM",
                        color = MechanicalOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                      )
                    }
                  }

                  Text(
                    text = part.partTitle,
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                  )

                  Text(
                    text = "Réf OEM : ${part.oemReference} • VIN Donneur : ${part.donorVinMasked}",
                    color = TechCyan,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                  )

                  Text(
                    text = "Origine : ${part.donorVehicleInfo}",
                    color = AluminumLight,
                    fontSize = 11.sp
                  )

                  if (part.paintColorCode != null) {
                    Text(
                      text = "Code Peinture Carrosserie : ${part.paintColorCode}",
                      color = WarningYellow,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                  }

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text(
                        text = "${part.priceDzd} DA",
                        color = ElectricGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                      )
                      Text(
                        text = "Neuf : ${part.newOemPriceDzd} DA • ${part.recyclerName}",
                        color = AluminumGray,
                        fontSize = 10.sp
                      )
                    }

                    Button(
                      onClick = { onReserveUsedPart(part.id) },
                      enabled = !part.isReserved,
                      colors = ButtonDefaults.buttonColors(
                        containerColor = if (part.isReserved) MetalSurface else ElectricGreen
                      )
                    ) {
                      Text(
                        text = if (part.isReserved) "RÉSERVÉE" else "RÉSERVER PIÈCE",
                        color = if (part.isReserved) TechCyan else GraphiteBlack,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                      )
                    }
                  }
                }
              }
            }
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(coreConsignes, key = { it.id }) { core ->
              val statusColor = when (core.status) {
                CoreReturnStatus.A_RESTITUER_FOURNISSEUR -> WarningYellow
                CoreReturnStatus.EXPEDIE_EN_CONTROLE -> TechCyan
                CoreReturnStatus.CONSIGNE_REMBOURSEE -> ElectricGreen
              }
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, statusColor.copy(alpha = 0.7f))
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = core.coreReference,
                      color = TechCyan,
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = statusColor.copy(alpha = 0.2f)
                    ) {
                      Text(
                        text = core.status.label,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                      )
                    }
                  }

                  Text(core.partName, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                  Text("Fournisseur : ${core.supplierName} • Échéance retour : ${core.deadlineDate}", color = AluminumLight, fontSize = 11.sp)

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "Caution Consigne : ${core.depositAmountDzd} DA",
                      color = MechanicalOrange,
                      fontWeight = FontWeight.Black,
                      fontSize = 13.5.sp
                    )

                    if (core.status != CoreReturnStatus.CONSIGNE_REMBOURSEE) {
                      OutlinedButton(
                        onClick = { onAdvanceCoreReturnStatus(core.id) },
                        border = BorderStroke(1.dp, ElectricGreen)
                      ) {
                        Text(
                          text = if (core.status == CoreReturnStatus.A_RESTITUER_FOURNISSEUR)
                            "Expédier Vieille Matière"
                          else
                            "Valider Avoir Caution",
                          color = ElectricGreen,
                          fontWeight = FontWeight.Bold,
                          fontSize = 10.5.sp
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
