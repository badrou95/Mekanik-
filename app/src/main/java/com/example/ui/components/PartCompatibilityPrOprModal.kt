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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.FitmentVerificationRule
import com.example.ui.theme.*

@Composable
fun PartCompatibilityPrOprModal(
  rules: List<FitmentVerificationRule>,
  onAddNewRuleCheck: (code: String, vehicle: String) -> Unit,
  onDismiss: () -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }
  var customCodeInput by remember { mutableStateOf("") }
  var customVehicleInput by remember { mutableStateOf("") }

  val filteredRules = remember(rules, searchQuery) {
    if (searchQuery.isBlank()) rules
    else rules.filter {
      it.codeEntered.contains(searchQuery, ignoreCase = true) ||
        it.vehicleModel.contains(searchQuery, ignoreCase = true) ||
        it.verifiedOemRef.contains(searchQuery, ignoreCase = true)
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)
        .testTag("modal_part_compatibility_pr_opr"),
      shape = RoundedCornerShape(16.dp),
      color = GraphiteBlack,
      border = BorderStroke(1.dp, WarningYellow)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(MetalCard)
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(WarningYellow.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Verified, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "ANTI-ERREUR MONTAGE : CODES PR / OPR / BOÎTES",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
              )
              Text(
                text = "Vérificateur Codes PR (VAG), N° OPR/DAM (PSA), Indices K9K/TL4 (Renault) & Mobis",
                color = AluminumLight,
                fontSize = 10.sp
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite)
          }
        }

        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(bottom = 24.dp)
        ) {
          // Instant Decoder Box
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, TechCyan)
            ) {
              Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                  text = "🔍 DÉCODEUR INSTANTANÉ DE MONTAGE USINE (ZÉRO RETOUR PIÈCE)",
                  color = TechCyan,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.5.sp
                )
                OutlinedTextField(
                  value = searchQuery,
                  onValueChange = { searchQuery = it },
                  label = { Text("Filtrer par Code PR (1ZE, 1ZA), OPR (15482), Boîte (TL4) ou Modèle...", fontSize = 11.sp) },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth()
                )
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  OutlinedTextField(
                    value = customCodeInput,
                    onValueChange = { customCodeInput = it },
                    label = { Text("Nouveau Code (ex: PR 1ZD / OPR 16200)", fontSize = 10.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                  )
                  OutlinedTextField(
                    value = customVehicleInput,
                    onValueChange = { customVehicleInput = it },
                    label = { Text("Véhicule (ex: Octavia 2.0 TDI)", fontSize = 10.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                  )
                }
                Button(
                  onClick = {
                    if (customCodeInput.isNotBlank()) {
                      onAddNewRuleCheck(customCodeInput, customVehicleInput.ifBlank { "Véhicule Client Atelier" })
                      customCodeInput = ""
                      customVehicleInput = ""
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("VÉRIFIER LA COMPATIBILITÉ EXACTE & VERROUILLER LA RÉF OEM", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
              }
            }
          }

          items(filteredRules, key = { it.id }) { rule ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, Color(rule.systemType.badgeColorHex))
            ) {
              Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(rule.systemType.badgeColorHex).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, Color(rule.systemType.badgeColorHex))
                  ) {
                    Text(
                      text = "CODE : ${rule.codeEntered}",
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontFamily = FontFamily.Monospace,
                      fontSize = 11.sp,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                  Text(
                    text = rule.systemType.label,
                    color = AluminumLight,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                }

                Text(
                  text = "${rule.vehicleModel} — ${rule.partCategory}",
                  color = PureWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.5.sp
                )

                Text(
                  text = "📍 Où lire ce code : ${rule.stickerLocationHint}",
                  color = AluminumLight,
                  fontSize = 10.5.sp
                )

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Color(0xFF0E2A1C),
                  border = BorderStroke(1.dp, ElectricGreen),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(8.dp)) {
                    Text("✅ MONTAGE EXACT CERTIFIÉ :", color = ElectricGreen, fontWeight = FontWeight.Black, fontSize = 10.sp)
                    Text(rule.exactMountedSpec, color = PureWhite, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    Text(
                      text = "Réf OEM verrouillée : ${rule.verifiedOemRef}",
                      color = TechCyan,
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Color(0xFF2D1414),
                  border = BorderStroke(1.dp, PerformanceRedGlow),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text(
                    text = rule.incompatibleTrapSpec,
                    color = WarningYellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp,
                    modifier = Modifier.padding(8.dp)
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
