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
import androidx.compose.runtime.Composable
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
import com.example.data.model.CertifiedServicePassport
import com.example.ui.theme.*

@Composable
fun DigitalPassportModal(
  passport: CertifiedServicePassport,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .background(GraphiteDark)
        .testTag("digital_passport_dialog"),
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
                .background(TechCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = TechCyan, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "PASSEPORT D'ENTRETIEN CERTIFIÉ",
                style = MaterialTheme.typography.titleMedium.copy(
                  color = PureWhite,
                  fontWeight = FontWeight.Black
                )
              )
              Text(
                text = "Historique infalsifiable & QR Code de Revente",
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

        // Passport Summary & QR Validation Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = passport.vehicleModel,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = PureWhite)
              )
              Text(
                text = "Châssis : ${passport.vehicleVin}",
                style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.sp)
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Kilométrage certifié : ${passport.currentMileageKm} km",
                style = MaterialTheme.typography.bodyMedium.copy(color = ElectricGreen, fontWeight = FontWeight.Bold)
              )
              Text(
                text = "Propriétaire : ${passport.ownerName}",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
              )
            }

            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PureWhite),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.QrCode2, contentDescription = "QR Code", tint = GraphiteBlack, modifier = Modifier.size(62.dp))
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "HISTORIQUE DES RÉVISIONS & FACTURES VALIDÉES",
          style = MaterialTheme.typography.labelMedium.copy(color = AluminumMuted, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Records List
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(passport.records) { rec ->
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
                  Text(
                    text = "${rec.mileageKm} km • ${rec.date}",
                    style = MaterialTheme.typography.labelMedium.copy(color = TechCyan, fontWeight = FontWeight.Bold)
                  )
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ElectricGreen.copy(alpha = 0.2f)
                  ) {
                    Text(
                      text = "Certifié",
                      style = MaterialTheme.typography.labelSmall.copy(color = ElectricGreen, fontWeight = FontWeight.Bold),
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = rec.operationTitle,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                )

                Text(
                  text = "Atelier : ${rec.garageName} • Montant : ${rec.totalCostDzd.toInt()} DA",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                rec.performedTasks.forEach { task ->
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = task, style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.5.sp))
                  }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = rec.certHash,
                  style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 9.sp)
                )
              }
            }
          }
        }
      }
    }
  }
}
