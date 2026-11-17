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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AccidentCircumstance
import com.example.data.model.DigitalAccidentReport
import com.example.ui.theme.*

@Composable
fun DigitalAccidentReportModal(
  reports: List<DigitalAccidentReport>,
  onSubmitNewReport: (String, String, String, List<AccidentCircumstance>, String) -> Unit,
  onDismiss: () -> Unit
) {
  var showWizard by remember { mutableStateOf(true) }
  var driverBName by remember { mutableStateOf("Karim Ziani") }
  var driverBVehicle by remember { mutableStateOf("Renault Symbol 1.5 dCi (04129-119-16)") }
  var driverBInsurance by remember { mutableStateOf("CAAR Assurance - Police N° 884120") }
  var impactZone by remember { mutableStateOf("Pare-chocs avant droit + Phare optique AVD") }
  var selectedCircumstances by remember {
    mutableStateOf(setOf(AccidentCircumstance.VENAIT_DE_DROITE_REFUS_PRIORITE))
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)
        .testTag("digital_accident_report_modal"),
      shape = RoundedCornerShape(18.dp),
      color = GraphiteDark,
      border = BorderStroke(1.dp, PerformanceRedGlow)
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
                .background(PerformanceRedDark),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.GppGood, contentDescription = null, tint = PureWhite)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "CONSTAT AMIABLE DIGITAL & SINISTRE",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
              )
              Text(
                text = "Assistant pas-à-pas • Photos, circonstances & envoi assurance/garage",
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
          // Mode Switch
          item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = { showWizard = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (showWizard) PerformanceRed else MetalCard,
                  contentColor = PureWhite
                )
              ) {
                Text("Nouveau Constat Guidé", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
              }
              Button(
                onClick = { showWizard = false },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (!showWizard) TechCyanDark else MetalCard,
                  contentColor = PureWhite
                )
              ) {
                Text("Dossiers Transmis (${reports.size})", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
              }
            }
          }

          if (showWizard) {
            // Step 1: Parties A & B
            item {
              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, MetalBorder)
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  Text("1. IDENTIFICATION DU TIERS (VÉHICULE B)", color = TechCyan, fontWeight = FontWeight.Black, fontSize = 12.sp)
                  OutlinedTextField(
                    value = driverBName,
                    onValueChange = { driverBName = it },
                    label = { Text("Nom & Prénom du conducteur B") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                  )
                  OutlinedTextField(
                    value = driverBVehicle,
                    onValueChange = { driverBVehicle = it },
                    label = { Text("Véhicule B & Immatriculation") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                  )
                  OutlinedTextField(
                    value = driverBInsurance,
                    onValueChange = { driverBInsurance = it },
                    label = { Text("Compagnie d'Assurance & N° Police B") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                  )
                }
              }
            }

            // Step 2: Official Circumstances Checkboxes
            item {
              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.7f))
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text(
                    text = "2. CIRCONSTANCES DE L'ACCROCHAGE (RUBRIQUE 12 OFFICIELLE)",
                    color = MechanicalOrange,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                  )
                  AccidentCircumstance.entries.take(7).forEach { circ ->
                    val isChecked = selectedCircumstances.contains(circ)
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = if (isChecked) MechanicalOrange.copy(alpha = 0.15f) else GraphiteBlack,
                      border = BorderStroke(1.dp, if (isChecked) MechanicalOrange else MetalBorder),
                      modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                          selectedCircumstances = if (isChecked) selectedCircumstances - circ else selectedCircumstances + circ
                        }
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Checkbox(
                          checked = isChecked,
                          onCheckedChange = {
                            selectedCircumstances = if (isChecked) selectedCircumstances - circ else selectedCircumstances + circ
                          },
                          colors = CheckboxDefaults.colors(checkedColor = MechanicalOrange)
                        )
                        Text(
                          text = "[Case ${circ.code}] ${circ.label}",
                          color = PureWhite,
                          fontSize = 11.sp,
                          fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal
                        )
                      }
                    }
                  }
                }
              }
            }

            // Step 3: Impact Zone & Submit
            item {
              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
                border = BorderStroke(1.dp, ElectricGreen)
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  Text("3. POINT DE CHOC INITIAL & PHOTOS RÉGLEMENTAIRES", color = ElectricGreen, fontWeight = FontWeight.Black, fontSize = 12.sp)
                  OutlinedTextField(
                    value = impactZone,
                    onValueChange = { impactZone = it },
                    label = { Text("Dégâts apparents & Point de choc initial") },
                    modifier = Modifier.fillMaxWidth()
                  )
                  Text(
                    text = "📸 4 Photos certifiées jointes (Vue générale avant/arrière, plaques, point d'impact) + Coordonnées GPS verrouillées.",
                    color = AluminumLight,
                    fontSize = 10.5.sp
                  )
                  Button(
                    onClick = {
                      onSubmitNewReport(
                        driverBName,
                        driverBVehicle,
                        driverBInsurance,
                        selectedCircumstances.toList(),
                        impactZone
                      )
                      showWizard = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen, contentColor = GraphiteBlack)
                  ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Signer & Transmettre le Constat (Assurance + Atelier)", fontWeight = FontWeight.Black)
                  }
                }
              }
            }
          } else {
            items(reports, key = { it.id }) { rep ->
              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, ElectricGreen),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(rep.reportNumber, color = ElectricGreen, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    Text(rep.dateTimeFormatted, color = AluminumLight, fontSize = 10.5.sp)
                  }
                  Text("📍 Lieu GPS : ${rep.gpsLocation}", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                  Text("🚗 Véhicule A : ${rep.driverAVehicle} (${rep.driverAPlate})", color = AluminumLight, fontSize = 11.sp)
                  Text("🚙 Véhicule B : ${rep.driverBVehicle} • Conducteur : ${rep.driverBName}", color = AluminumLight, fontSize = 11.sp)
                  Text("💥 Dégâts : ${rep.impactZoneDescription}", color = MechanicalOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                  Text("📸 ${rep.attachedPhotosCount} photos jointes • Signé par les 2 conducteurs", color = TechCyan, fontSize = 10.5.sp)
                }
              }
            }
          }
        }
      }
    }
  }
}
