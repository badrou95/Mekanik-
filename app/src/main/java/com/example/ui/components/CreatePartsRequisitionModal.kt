package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.RequisitionPartItem
import com.example.data.model.UltraProSubAccount
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePartsRequisitionModal(
  activeSubAccount: UltraProSubAccount?,
  onSubmitRequisition: (vehicleModel: String, vin: String, plate: String, parts: List<RequisitionPartItem>, notes: String) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  var vehicleModel by remember { mutableStateOf("VW Golf 7 2.0 TDI EA288") }
  var vin by remember { mutableStateOf("WVWZZZAUZHP998877") }
  var plateNumber by remember { mutableStateOf("05612-118-16") }
  var notes by remember { mutableStateOf("Diagnostic OBD défaut P0401 : Vanne EGR colmatée. Remplacement nécessaire avant remontage.") }

  // Quick parts list
  var partRef by remember { mutableStateOf("") }
  var partName by remember { mutableStateOf("") }
  var partBrand by remember { mutableStateOf("") }
  var partQty by remember { mutableStateOf("1") }
  var partPrice by remember { mutableStateOf("") }

  val partsList = remember {
    mutableStateListOf(
      RequisitionPartItem("p_01", "04L 131 512 BH", "Vanne EGR avec Refroidisseur Gaz", "Wahler OEM", 1, 48000, 1),
      RequisitionPartItem("p_02", "G12EVO-5L", "Liquide de Refroidissement G12evo 5L", "VAG OEM", 1, 4500, 7)
    )
  }

  var errorMessage by remember { mutableStateOf<String?>(null) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.90f)
        .testTag("modal_create_parts_requisition"),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, TechCyan)
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
                .background(TechCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Build, contentDescription = null, tint = TechCyan, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "DEMANDE DE PIÈCES (MÉCANICIEN)",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = PureWhite
                )
              )
              Text(
                text = "Étape 1/4 : Recherche par Châssis/VIN ➔ Approbation Chef d'Atelier",
                style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight)
          }
        }

        HorizontalDivider(color = MetalBorder, modifier = Modifier.padding(vertical = 10.dp))

        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Vehicle Details
          Text(
            text = "1. IDENTIFICATION DU VÉHICULE & CHÂSSIS :",
            style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = vehicleModel,
              onValueChange = { vehicleModel = it },
              label = { Text("Modèle Véhicule", color = AluminumGray) },
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = PureWhite,
                unfocusedTextColor = OffWhite,
                focusedBorderColor = TechCyan,
                unfocusedBorderColor = MetalBorder,
                focusedContainerColor = MetalCard,
                unfocusedContainerColor = MetalCard
              ),
              modifier = Modifier.weight(1.2f)
            )

            OutlinedTextField(
              value = plateNumber,
              onValueChange = { plateNumber = it },
              label = { Text("Matricule", color = AluminumGray) },
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = PureWhite,
                unfocusedTextColor = OffWhite,
                focusedBorderColor = TechCyan,
                unfocusedBorderColor = MetalBorder,
                focusedContainerColor = MetalCard,
                unfocusedContainerColor = MetalCard
              ),
              modifier = Modifier.weight(0.8f)
            )
          }

          OutlinedTextField(
            value = vin,
            onValueChange = { vin = it.uppercase() },
            label = { Text("Numéro Châssis / VIN (17 caractères)", color = AluminumGray) },
            leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null, tint = TechCyan) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = PureWhite,
              unfocusedTextColor = OffWhite,
              focusedBorderColor = TechCyan,
              unfocusedBorderColor = MetalBorder,
              focusedContainerColor = MetalCard,
              unfocusedContainerColor = MetalCard
            ),
            modifier = Modifier.fillMaxWidth()
          )

          // Parts list
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "2. PIÈCES SÉLECTIONNÉES (${partsList.size}) :",
              style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            )
            Text(
              text = "Total : ${partsList.sumOf { it.unitPriceDzd * it.quantity }} DA",
              style = MaterialTheme.typography.labelSmall.copy(color = PerformanceRedGlow, fontWeight = FontWeight.Black)
            )
          }

          // Part Items Cards
          partsList.forEachIndexed { index, part ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = part.name,
                    style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                  )
                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "Réf : ${part.oemReference}",
                      style = MaterialTheme.typography.bodySmall.copy(color = PerformanceRedGlow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    )
                    Text(
                      text = "• ${part.brand}",
                      style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
                    )
                    Text(
                      text = "• Qté : ${part.quantity}",
                      style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    )
                  }
                  Text(
                    text = "${part.unitPriceDzd * part.quantity} DA",
                    style = MaterialTheme.typography.labelSmall.copy(color = ElectricGreen, fontWeight = FontWeight.Bold)
                  )
                }

                IconButton(onClick = { partsList.removeAt(index) }) {
                  Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = PerformanceRed)
                }
              }
            }
          }

          // Add quick part form
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MetalSurface,
            border = BorderStroke(1.dp, MetalBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                text = "+ AJOUTER UNE RÉFÉRENCE PIÈCE :",
                style = MaterialTheme.typography.labelSmall.copy(color = TechCyan, fontWeight = FontWeight.Bold)
              )

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(
                  value = partRef,
                  onValueChange = { partRef = it },
                  label = { Text("Réf. OEM", fontSize = 11.sp) },
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = OffWhite,
                    focusedBorderColor = TechCyan,
                    unfocusedBorderColor = MetalBorder,
                    focusedContainerColor = MetalCard,
                    unfocusedContainerColor = MetalCard
                  ),
                  modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                  value = partBrand,
                  onValueChange = { partBrand = it },
                  label = { Text("Marque", fontSize = 11.sp) },
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = OffWhite,
                    focusedBorderColor = TechCyan,
                    unfocusedBorderColor = MetalBorder,
                    focusedContainerColor = MetalCard,
                    unfocusedContainerColor = MetalCard
                  ),
                  modifier = Modifier.weight(0.8f)
                )
              }

              OutlinedTextField(
                value = partName,
                onValueChange = { partName = it },
                label = { Text("Désignation de la pièce", fontSize = 11.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                  focusedTextColor = PureWhite,
                  unfocusedTextColor = OffWhite,
                  focusedBorderColor = TechCyan,
                  unfocusedBorderColor = MetalBorder,
                  focusedContainerColor = MetalCard,
                  unfocusedContainerColor = MetalCard
                ),
                modifier = Modifier.fillMaxWidth()
              )

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                  value = partQty,
                  onValueChange = { partQty = it },
                  label = { Text("Qté", fontSize = 11.sp) },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = OffWhite,
                    focusedBorderColor = TechCyan,
                    unfocusedBorderColor = MetalBorder,
                    focusedContainerColor = MetalCard,
                    unfocusedContainerColor = MetalCard
                  ),
                  modifier = Modifier.weight(0.6f)
                )

                OutlinedTextField(
                  value = partPrice,
                  onValueChange = { partPrice = it },
                  label = { Text("Prix Estimé DA", fontSize = 11.sp) },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = OffWhite,
                    focusedBorderColor = TechCyan,
                    unfocusedBorderColor = MetalBorder,
                    focusedContainerColor = MetalCard,
                    unfocusedContainerColor = MetalCard
                  ),
                  modifier = Modifier.weight(1.4f)
                )

                Button(
                  onClick = {
                    if (partName.isNotBlank() && partRef.isNotBlank()) {
                      val qty = partQty.toIntOrNull() ?: 1
                      val price = partPrice.toIntOrNull() ?: 3500
                      partsList.add(
                        RequisitionPartItem(
                          partId = "p_${System.currentTimeMillis()}",
                          oemReference = partRef.trim().uppercase(),
                          name = partName.trim(),
                          brand = if (partBrand.isNotBlank()) partBrand.trim() else "OEM",
                          quantity = qty,
                          unitPriceDzd = price,
                          stockAvailable = 5
                        )
                      )
                      partRef = ""
                      partName = ""
                      partBrand = ""
                      partPrice = ""
                    }
                  },
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                  modifier = Modifier.padding(top = 8.dp)
                ) {
                  Icon(Icons.Default.Add, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(18.dp))
                }
              }
            }
          }

          // Notes
          OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes techniques / Justification mécanicien", color = AluminumGray) },
            minLines = 2,
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = PureWhite,
              unfocusedTextColor = OffWhite,
              focusedBorderColor = TechCyan,
              unfocusedBorderColor = MetalBorder,
              focusedContainerColor = MetalCard,
              unfocusedContainerColor = MetalCard
            ),
            modifier = Modifier.fillMaxWidth()
          )

          if (errorMessage != null) {
            Text(
              text = errorMessage ?: "",
              color = PerformanceRed,
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
            )
          }
        }

        HorizontalDivider(color = MetalBorder, modifier = Modifier.padding(vertical = 10.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, MetalBorder)
          ) {
            Text("Annuler", color = AluminumLight)
          }

          Button(
            onClick = {
              if (vehicleModel.isBlank() || plateNumber.isBlank()) {
                errorMessage = "Veuillez renseigner le modèle et le matricule du véhicule."
                return@Button
              }
              if (partsList.isEmpty()) {
                errorMessage = "Veuillez ajouter au moins une pièce à la demande."
                return@Button
              }
              onSubmitRequisition(vehicleModel, vin, plateNumber, partsList.toList(), notes)
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            modifier = Modifier.testTag("btn_submit_mechanic_requisition")
          ) {
            Icon(Icons.Default.Send, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "ENVOYER AU CHEF D'ATELIER",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black, color = GraphiteDark)
            )
          }
        }
      }
    }
  }
}
