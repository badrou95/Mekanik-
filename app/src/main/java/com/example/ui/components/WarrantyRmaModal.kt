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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PartWarrantyRecord
import com.example.data.model.WarrantyStatus
import com.example.ui.theme.*

@Composable
fun WarrantyRmaModal(
  warranties: List<PartWarrantyRecord>,
  onCreateRmaClaim: (String, String) -> Unit,
  onRegisterNewWarranty: (String, String, String, String) -> Unit,
  onDismiss: () -> Unit
) {
  var showNewRmaForId by remember { mutableStateOf<String?>(null) }
  var selectedRmaReason by remember { mutableStateOf("Défaut prématuré / Bruit mécanique anormal") }
  var showAddWarrantyForm by remember { mutableStateOf(false) }

  var newPartName by remember { mutableStateOf("") }
  var newOemRef by remember { mutableStateOf("") }
  var newBrand by remember { mutableStateOf("BOSCH") }
  var newVehiclePlate by remember { mutableStateOf("01429-122-16") }

  val rmaReasons = listOf(
    "Défaut prématuré / Bruit mécanique anormal",
    "Fuite / Défaut d'étanchéité constaté sur pont",
    "Défaillance électronique / Code défaut OBD",
    "Non-conformité référence / Erreur boîte fournisseur"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)
        .testTag("warranty_rma_modal"),
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
              Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MechanicalOrange)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "GARANTIES PIÈCES & RETOURS SAV (RMA)",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
              )
              Text(
                text = "Suivi certificats 12/24 mois • Échange standard grossiste",
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
          // KPI Row
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              val activeCount = warranties.count { it.status == WarrantyStatus.ACTIVE }
              val expiringCount = warranties.count { it.status == WarrantyStatus.EXPIRE_BIENTOT }
              val rmaCount = warranties.count { it.status == WarrantyStatus.RMA_EN_COURS }

              Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.5f))
              ) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("$activeCount", color = ElectricGreen, fontWeight = FontWeight.Black, fontSize = 18.sp)
                  Text("Garanties Actives", color = AluminumLight, fontSize = 10.sp)
                }
              }
              Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, WarningYellow.copy(alpha = 0.5f))
              ) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("$expiringCount", color = WarningYellow, fontWeight = FontWeight.Black, fontSize = 18.sp)
                  Text("Expirent < 30j", color = AluminumLight, fontSize = 10.sp)
                }
              }
              Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.5f))
              ) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("$rmaCount", color = MechanicalOrange, fontWeight = FontWeight.Black, fontSize = 18.sp)
                  Text("Dossiers RMA", color = AluminumLight, fontSize = 10.sp)
                }
              }
            }
          }

          // Action Button to Register a New Part Warranty Certificate
          item {
            Button(
              onClick = { showAddWarrantyForm = !showAddWarrantyForm },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed, contentColor = PureWhite)
            ) {
              Icon(Icons.Default.AddModerator, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (showAddWarrantyForm) "Fermer le formulaire" else "Enregistrer un Nouveau Certificat Garantie Pièce",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }

          if (showAddWarrantyForm) {
            item {
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
                border = BorderStroke(1.dp, PerformanceRedGlow)
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  Text("NOUVEAU CERTIFICAT DE GARANTIE (12 MOIS / 40 000 KM)", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 12.sp)
                  OutlinedTextField(
                    value = newPartName,
                    onValueChange = { newPartName = it },
                    label = { Text("Désignation de la pièce (ex: Turbo, Amortisseurs)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                  )
                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                      value = newOemRef,
                      onValueChange = { newOemRef = it },
                      label = { Text("Référence OEM") },
                      singleLine = true,
                      modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                      value = newBrand,
                      onValueChange = { newBrand = it },
                      label = { Text("Marque") },
                      singleLine = true,
                      modifier = Modifier.weight(1f)
                    )
                  }
                  OutlinedTextField(
                    value = newVehiclePlate,
                    onValueChange = { newVehiclePlate = it },
                    label = { Text("Véhicule / Matricule Client") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                  )
                  Button(
                    onClick = {
                      if (newPartName.isNotBlank()) {
                        onRegisterNewWarranty(newPartName, newOemRef.ifBlank { "OEM-STD-2026" }, newBrand, newVehiclePlate)
                        newPartName = ""
                        newOemRef = ""
                        showAddWarrantyForm = false
                      }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen, contentColor = GraphiteBlack)
                  ) {
                    Text("Activer le Certificat de Garantie", fontWeight = FontWeight.Black)
                  }
                }
              }
            }
          }

          // Warranties List
          items(warranties, key = { it.id }) { item ->
            val badgeColor = Color(item.status.colorHex)
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.65f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "${item.partName} (${item.brand})",
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 13.5.sp
                    )
                    Text(
                      text = "Réf : ${item.oemReference} • Certificat N° ${item.warrantyNumber}",
                      color = MechanicalOrange,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                  }
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, badgeColor)
                  ) {
                    Text(
                      text = item.status.label.uppercase(),
                      color = badgeColor,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.sp,
                      modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = GraphiteBlack,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                      text = "🚗 Véhicule : ${item.vehicleModel} (${item.licensePlate}) • Client : ${item.clientName}",
                      color = PureWhite,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold
                    )
                    Text(
                      text = "🏪 Fournisseur : ${item.supplierName} • Achat : ${item.purchasePriceDzd} DZD",
                      color = AluminumLight,
                      fontSize = 10.5.sp
                    )
                    Text(
                      text = "📅 Pose : ${item.installationDate} ➔ Fin : ${item.expirationDate} (${item.daysRemaining} j restants) • Plafond : ${item.maxWarrantyMileageKm} km",
                      color = TechCyan,
                      fontSize = 10.5.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }

                if (item.rmaClaimReason != null) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MechanicalOrange.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, MechanicalOrange),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                      Text(
                        text = "📦 DOSSIER RETOUR SAV : ${item.rmaTrackingCode ?: "RMA-2026"}",
                        color = MechanicalOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                      )
                      Text(
                        text = "Motif déclaré : ${item.rmaClaimReason}",
                        color = PureWhite,
                        fontSize = 10.5.sp
                      )
                    }
                  }
                } else {
                  if (showNewRmaForId == item.id) {
                    Column(
                      modifier = Modifier
                        .fillMaxWidth()
                        .background(GraphiteBlack, RoundedCornerShape(10.dp))
                        .padding(10.dp),
                      verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Text("MOTIF DE LA DEMANDE DE PRISE EN CHARGE GARANTIE (RMA) :", color = MechanicalOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                      rmaReasons.forEach { reason ->
                        Row(
                          verticalAlignment = Alignment.CenterVertically,
                          modifier = Modifier.fillMaxWidth()
                        ) {
                          RadioButton(
                            selected = selectedRmaReason == reason,
                            onClick = { selectedRmaReason = reason },
                            colors = RadioButtonDefaults.colors(selectedColor = MechanicalOrange)
                          )
                          Text(reason, color = PureWhite, fontSize = 11.sp)
                        }
                      }
                      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                          onClick = { showNewRmaForId = null },
                          modifier = Modifier.weight(1f)
                        ) {
                          Text("Annuler", color = AluminumLight, fontSize = 11.sp)
                        }
                        Button(
                          onClick = {
                            onCreateRmaClaim(item.id, selectedRmaReason)
                            showNewRmaForId = null
                          },
                          modifier = Modifier.weight(1f),
                          colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange, contentColor = PureWhite)
                        ) {
                          Text("Transmettre Bon RMA", fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                      }
                    }
                  } else {
                    OutlinedButton(
                      onClick = { showNewRmaForId = item.id },
                      modifier = Modifier.fillMaxWidth(),
                      border = BorderStroke(1.dp, MechanicalOrange),
                      colors = ButtonDefaults.outlinedButtonColors(contentColor = MechanicalOrange)
                    ) {
                      Icon(Icons.Default.AssignmentReturn, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Ouvrir un Dossier Retour Garantie (SAV / RMA)", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
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
