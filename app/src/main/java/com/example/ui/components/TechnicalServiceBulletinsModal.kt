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
import com.example.data.model.TechnicalServiceBulletin
import com.example.data.model.TsbSeverity
import com.example.ui.theme.*

@Composable
fun TechnicalServiceBulletinsModal(
  bulletins: List<TechnicalServiceBulletin>,
  onOrderModifiedPart: (TechnicalServiceBulletin) -> Unit,
  onDismiss: () -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }

  val filteredBulletins = remember(bulletins, searchQuery) {
    val q = searchQuery.trim().lowercase()
    if (q.isEmpty()) bulletins
    else bulletins.filter {
      it.title.lowercase().contains(q) ||
        it.engineCodes.lowercase().contains(q) ||
        it.brandAndModels.lowercase().contains(q) ||
        it.relatedDtcCodes.lowercase().contains(q) ||
        it.bulletinNumber.lowercase().contains(q)
    }
  }

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
      border = BorderStroke(1.dp, PerformanceRed.copy(alpha = 0.7f))
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
                .background(PerformanceRed.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.LibraryBooks, contentDescription = null, tint = PerformanceRed, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "BULLETINS TSB, RAPPELS & PANNES CONNUES",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 13.5.sp
              )
              Text(
                text = "Base Identifix Direct-Hit • Maladies moteurs & Solutions OEM vérifiées",
                color = AluminumGray,
                fontSize = 10.5.sp
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight)
          }
        }

        // Search input
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = {
            Text(
              "Rechercher par code moteur (DV5, EB2, K9K, TDI), code défaut (P0016) ou modèle...",
              color = AluminumMuted,
              fontSize = 11.5.sp
            )
          },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = MechanicalOrange)
          },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MechanicalOrange,
            unfocusedBorderColor = MetalBorder,
            focusedTextColor = PureWhite,
            unfocusedTextColor = PureWhite
          ),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
        )

        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 4.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(filteredBulletins, key = { it.id }) { tsb ->
            val badgeColor = when (tsb.severity) {
              TsbSeverity.RAPPEL_SECURITE -> PerformanceRed
              TsbSeverity.MALADIE_CHRONIQUE -> MechanicalOrange
              TsbSeverity.NOTE_TECHNIQUE_OEM -> TechCyan
            }

            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.65f))
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, badgeColor)
                  ) {
                    Text(
                      text = "${tsb.severity.label} • ${tsb.bulletinNumber}",
                      color = badgeColor,
                      fontWeight = FontWeight.Black,
                      fontSize = 10.sp,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ElectricGreen.copy(alpha = 0.18f)
                  ) {
                    Text(
                      text = "${tsb.verifiedSuccessRatePercent}% Réussite (${tsb.reportedCasesCount} cas)",
                      color = ElectricGreen,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                  }
                }

                Text(
                  text = tsb.title,
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 13.sp
                )

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Text(
                    text = "Moteurs : ${tsb.engineCodes}",
                    color = MechanicalOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.weight(1f)
                  )
                  Text(
                    text = "DTC : ${tsb.relatedDtcCodes}",
                    color = PerformanceRedGlow,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                }

                Text(
                  text = "Modèles : ${tsb.brandAndModels}",
                  color = AluminumGray,
                  fontSize = 10.5.sp
                )

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, MetalBorder),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("• Symptôme : ${tsb.symptomDescription}", color = AluminumLight, fontSize = 10.5.sp)
                    Text("• Cause racine : ${tsb.verifiedRootCause}", color = WarningYellow, fontSize = 10.5.sp)
                    Text("• Solution officielle OEM : ${tsb.officialOemFix}", color = ElectricGreen, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                      text = "• Référence Pièce Modifiée : ${tsb.modifiedPartReference}",
                      color = TechCyan,
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.5.sp
                    )
                  }
                }

                Button(
                  onClick = { onOrderModifiedPart(tsb) },
                  colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("CHERCHER LE KIT MODIFIÉ OEM EN STOCK B2B", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
              }
            }
          }
        }
      }
    }
  }
}
