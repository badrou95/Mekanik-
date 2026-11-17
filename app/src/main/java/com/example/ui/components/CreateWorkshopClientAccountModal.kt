package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.RepairOrder
import com.example.data.model.WorkshopClientAccount
import com.example.ui.theme.*

@Composable
fun CreateWorkshopClientAccountModal(
  initialPlateNumber: String = "",
  initialClientName: String = "",
  initialClientPhone: String = "",
  initialVehicleModel: String = "",
  initialVin: String = "",
  associatedRepairOrder: RepairOrder? = null,
  onCreateAccount: (WorkshopClientAccount) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  var plateNumber by remember { mutableStateOf(initialPlateNumber.ifBlank { "05123-122-16" }) }
  var passwordPin by remember { mutableStateOf("1234") }
  var clientName by remember { mutableStateOf(initialClientName.ifBlank { "Client Atelier" }) }
  var clientPhone by remember { mutableStateOf(initialClientPhone.ifBlank { "0550 00 00 00" }) }
  var vehicleModel by remember { mutableStateOf(initialVehicleModel.ifBlank { "Véhicule Client" }) }
  var vin by remember { mutableStateOf(initialVin.ifBlank { "VF3XXXXXXXXXXXXXX" }) }

  var generatedAccount by remember { mutableStateOf<WorkshopClientAccount?>(null) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .wrapContentHeight()
        .clip(RoundedCornerShape(20.dp))
        .border(1.5.dp, PerformanceRedGlow, RoundedCornerShape(20.dp))
        .testTag("create_client_account_dialog"),
      color = GraphiteDark,
      tonalElevation = 8.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        if (generatedAccount == null) {
          // Formulaire de création simplifié (Uniquement Matricule + Mot de passe requis)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(PerformanceRed.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "CRÉER UN COMPTE CLIENT",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = PureWhite, letterSpacing = 1.sp)
                )
                Text(
                  text = "Accès gratuit client (Matricule + Mot de passe + QR)",
                  style = MaterialTheme.typography.bodySmall.copy(color = ElectricGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                )
              }
            }

            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MetalCard),
            border = BorderStroke(1.dp, MetalBorder)
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = "IDENTIFIANTS D'ACCÈS DU CLIENT (OBLIGATOIRES)",
                style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Black)
              )

              OutlinedTextField(
                value = plateNumber,
                onValueChange = { plateNumber = it },
                label = { Text("1. Matricule (Plaque d'immatriculation)") },
                placeholder = { Text("Ex: 04291-121-16") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_client_matricule"),
                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = PerformanceRed) }
              )

              OutlinedTextField(
                value = passwordPin,
                onValueChange = { passwordPin = it },
                label = { Text("2. Mot de passe / Code PIN d'accès") },
                placeholder = { Text("Ex: 1234") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_client_password"),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TechCyan) }
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Optional Client & Vehicle Details
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MetalCard),
            border = BorderStroke(1.dp, MetalBorder)
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                text = "INFORMATIONS COMPLÉMENTAIRES (FACULTATIF)",
                style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold)
              )

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                  value = clientName,
                  onValueChange = { clientName = it },
                  label = { Text("Nom Client") },
                  singleLine = true,
                  modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                  value = clientPhone,
                  onValueChange = { clientPhone = it },
                  label = { Text("Téléphone") },
                  singleLine = true,
                  modifier = Modifier.weight(1f)
                )
              }

              OutlinedTextField(
                value = vehicleModel,
                onValueChange = { vehicleModel = it },
                label = { Text("Modèle Véhicule") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MekanikSecondaryButton(
              text = "ANNULER",
              onClick = onDismiss,
              modifier = Modifier.weight(1f)
            )

            MekanikPrimaryButton(
              text = "CRÉER LE COMPTE & QR",
              onClick = {
                val newAcc = WorkshopClientAccount(
                  id = "WCA-${System.currentTimeMillis().toString().takeLast(4)}",
                  plateNumber = plateNumber.trim(),
                  passwordPin = passwordPin.trim(),
                  clientName = clientName.trim(),
                  clientPhone = clientPhone.trim(),
                  vehicleModel = vehicleModel.trim(),
                  vin = vin.trim(),
                  qrAccessCode = "MEKANIK-CLIENT-${plateNumber.trim()}",
                  createdAt = "Aujourd'hui",
                  activeRepairOrder = associatedRepairOrder,
                  repairHistory = associatedRepairOrder?.let { listOf(it) } ?: emptyList(),
                  invoices = emptyList(),
                  isFreeAccount = true
                )
                generatedAccount = newAcc
                onCreateAccount(newAcc)
              },
              icon = Icons.Default.QrCode,
              modifier = Modifier.weight(1.4f).testTag("btn_confirm_create_client_acc")
            )
          }
        } else {
          // Success Screen displaying the Generated QR Access Badge for the Client
          val acc = generatedAccount!!
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(ElectricGreen.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(30.dp))
            }

            Text(
              text = "COMPTE CLIENT CRÉÉ AVEC SUCCÈS !",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = PureWhite, textAlign = TextAlign.Center)
            )

            Text(
              text = "Le client peut maintenant accéder gratuitement à ses réparations et factures en scannant ce QR Code ou avec son Matricule & Mot de passe.",
              style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, textAlign = TextAlign.Center, fontSize = 11.5.sp)
            )

            // High Fidelity QR Card
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = PureWhite,
              border = BorderStroke(2.dp, PerformanceRed),
              modifier = Modifier.padding(8.dp)
            ) {
              Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(
                  text = "ESPACE CLIENT ATELIER",
                  style = MaterialTheme.typography.labelSmall.copy(color = PerformanceRed, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                )

                Box(
                  modifier = Modifier
                    .size(160.dp)
                    .background(Color.White)
                    .padding(8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                      Box(modifier = Modifier.size(32.dp).background(Color.Black))
                      Box(modifier = Modifier.size(32.dp).background(Color.Black))
                    }
                    Text(
                      text = acc.plateNumber,
                      style = MaterialTheme.typography.labelSmall.copy(color = Color.Black, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                      Box(modifier = Modifier.size(32.dp).background(Color.Black))
                      Box(modifier = Modifier.size(24.dp).background(Color(0xFFE53935)))
                    }
                  }
                }

                Text(
                  text = "MATRICULE : ${acc.plateNumber}\nMOT DE PASSE : ${acc.passwordPin}",
                  textAlign = TextAlign.Center,
                  style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                )
              }
            }

            MekanikPrimaryButton(
              text = "TERMINER & ENREGISTRER",
              onClick = onDismiss,
              icon = Icons.Default.Done,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }
    }
  }
}
