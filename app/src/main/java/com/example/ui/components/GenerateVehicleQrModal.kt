package com.example.ui.components

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AccountRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikViewModel

@Composable
fun GenerateVehicleQrModal(
  viewModel: MekanikViewModel,
  onDismiss: () -> Unit
) {
  var vinInput by remember { mutableStateOf("WVWZZZ5NZMW998877") }
  var plateInput by remember { mutableStateOf("01928-122-16") }
  var brandInput by remember { mutableStateOf("Volkswagen") }
  var modelInput by remember { mutableStateOf("Golf 8 1.5 eTSI 150ch") }
  var engineCodeInput by remember { mutableStateOf("EA211 evo") }
  var mileageInput by remember { mutableStateOf("45000") }
  var selectedRole by remember { mutableStateOf(AccountRole.GARAGE_PRO) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .background(GraphiteDark)
        .testTag("generate_vehicle_qr_modal"),
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
                .size(40.dp)
                .clip(CircleShape)
                .background(PerformanceRedDark),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.QrCode2, contentDescription = null, tint = PureWhite, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "GÉNÉRATEUR DE QR PASSEPORT",
                style = MaterialTheme.typography.titleMedium.copy(
                  color = PureWhite,
                  fontWeight = FontWeight.Black
                )
              )
              Text(
                text = "Inscrire un véhicule et regrouper toutes ses données",
                style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.sp)
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

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
              text = "PROFIL D'INSCRIPTION DU VÉHICULE",
              style = MaterialTheme.typography.labelSmall.copy(
                color = AluminumMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              AccountRole.values().forEach { role ->
                val isSelected = selectedRole == role
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (isSelected) PerformanceRed else MetalSurface,
                  border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
                  modifier = Modifier
                    .weight(1f)
                    .clickable { selectedRole = role }
                ) {
                  Text(
                    text = role.shortTitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = if (isSelected) PureWhite else AluminumLight,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                    maxLines = 1
                  )
                }
              }
            }

            // Input Fields
            QrInputField(label = "Numéro de Châssis (VIN)", value = vinInput, onValueChange = { vinInput = it })
            QrInputField(label = "Plaque d'Immatriculation (DZ)", value = plateInput, onValueChange = { plateInput = it })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Box(modifier = Modifier.weight(1f)) {
                QrInputField(label = "Marque", value = brandInput, onValueChange = { brandInput = it })
              }
              Box(modifier = Modifier.weight(1.5f)) {
                QrInputField(label = "Modèle & Version", value = modelInput, onValueChange = { modelInput = it })
              }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Box(modifier = Modifier.weight(1f)) {
                QrInputField(label = "Code Moteur", value = engineCodeInput, onValueChange = { engineCodeInput = it })
              }
              Box(modifier = Modifier.weight(1f)) {
                QrInputField(label = "Kilométrage Actuel", value = mileageInput, onValueChange = { mileageInput = it })
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info Banner
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = TechCyan.copy(alpha = 0.1f),
          border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = TechCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Ce QR regroupera automatiquement les devis, pièces commandées au comptoir et ordres de réparation atelier.",
              style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontSize = 11.sp)
            )
          }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
          onClick = {
            val mileage = mileageInput.toIntOrNull() ?: 50000
            viewModel.registerNewVehicleQrPassport(
              vin = vinInput,
              plate = plateInput,
              brand = brandInput,
              model = modelInput,
              engineCode = engineCodeInput,
              mileage = mileage,
              role = selectedRole
            )
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
        ) {
          Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Générer le QR Code & Ouvrir le Passeport", fontWeight = FontWeight.Black, fontSize = 13.sp)
        }
      }
    }
  }
}

@Composable
private fun QrInputField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit
) {
  Column {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 10.5.sp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(8.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MetalSurface,
        unfocusedContainerColor = MetalSurface,
        focusedBorderColor = PerformanceRed,
        unfocusedBorderColor = MetalBorder,
        focusedTextColor = PureWhite,
        unfocusedTextColor = PureWhite
      )
    )
  }
}
