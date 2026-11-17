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
import com.example.data.model.EcuPinoutSignal
import com.example.data.model.FuseBoxItem
import com.example.data.model.FuseLocationZone
import com.example.ui.theme.*

@Composable
fun WiringFuseEcuModal(
  fuses: List<FuseBoxItem>,
  pinouts: List<EcuPinoutSignal>,
  onToggleFuseStatus: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Boîtes à Fusibles & Relais, 1 = Pinout ECU & CAN-Bus
  var selectedZoneFilter by remember { mutableStateOf<FuseLocationZone?>(null) }

  val filteredFuses = remember(fuses, selectedZoneFilter) {
    if (selectedZoneFilter == null) fuses else fuses.filter { it.zone == selectedZoneFilter }
  }
  val blownCount = fuses.count { !it.isIntact }

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
      border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.6f))
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
                .background(TechCyan.copy(alpha = 0.18f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.ElectricalServices, contentDescription = null, tint = TechCyan, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "SCHÉMAS ÉLECTRIQUES, FUSIBLES & PINOUT ECU",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 13.5.sp
              )
              Text(
                text = "Base HaynesPro / Autodata • Affectation BSM/BSI & Tensions Calculateurs",
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
            color = if (selectedTab == 0) TechCyan.copy(alpha = 0.22f) else MetalSurface,
            border = BorderStroke(1.dp, if (selectedTab == 0) TechCyan else MetalBorder),
            modifier = Modifier
              .weight(1f)
              .clickable { selectedTab = 0 }
          ) {
            Row(
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.PowerInput, contentDescription = null, tint = if (selectedTab == 0) TechCyan else AluminumGray, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "FUSIBLES & RELAIS (${fuses.size})",
                color = if (selectedTab == 0) PureWhite else AluminumGray,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
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
              Icon(Icons.Default.Memory, contentDescription = null, tint = if (selectedTab == 1) MechanicalOrange else AluminumGray, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "PINOUT ECU & CAN-BUS (${pinouts.size})",
                color = if (selectedTab == 1) PureWhite else AluminumGray,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }
        }

        if (selectedTab == 0) {
          // Zone Filter Row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            FilterChip(
              selected = selectedZoneFilter == null,
              onClick = { selectedZoneFilter = null },
              label = { Text("Toutes zones", fontSize = 10.sp) }
            )
            FuseLocationZone.entries.forEach { zone ->
              FilterChip(
                selected = selectedZoneFilter == zone,
                onClick = { selectedZoneFilter = if (selectedZoneFilter == zone) null else zone },
                label = { Text(zone.name.replace("_", " "), fontSize = 9.5.sp) }
              )
            }
          }

          if (blownCount > 0) {
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
              shape = RoundedCornerShape(10.dp),
              color = PerformanceRed.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, PerformanceRed)
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.WarningAmber, contentDescription = null, tint = PerformanceRed, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "$blownCount fusible(s) coupé(s) détecté(s) (Continuité 0.0 V). Cliquez sur 'Remplacer' après test multimètre.",
                  color = PureWhite,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }

          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(filteredFuses, key = { it.id }) { fuse ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(
                  1.dp,
                  if (fuse.isIntact) ElectricGreen.copy(alpha = 0.5f) else PerformanceRed
                )
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = TechCyan.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, TechCyan)
                      ) {
                        Text(
                          text = fuse.fuseCode,
                          color = PureWhite,
                          fontWeight = FontWeight.Black,
                          fontFamily = FontFamily.Monospace,
                          fontSize = 11.5.sp,
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                      }
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MechanicalOrange.copy(alpha = 0.2f)
                      ) {
                        Text(
                          text = fuse.amperage,
                          color = MechanicalOrange,
                          fontWeight = FontWeight.Bold,
                          fontSize = 10.5.sp,
                          modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                      }
                    }

                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = if (fuse.isIntact) ElectricGreen.copy(alpha = 0.18f) else PerformanceRed.copy(alpha = 0.22f)
                    ) {
                      Text(
                        text = if (fuse.isIntact) "CONTINUITÉ OK (${fuse.nominalVoltageV}V)" else "GRILLÉ / COUPÉ (0.0V)",
                        color = if (fuse.isIntact) ElectricGreen else PerformanceRedGlow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                      )
                    }
                  }

                  Text(
                    text = fuse.zone.label,
                    color = TechCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                  )

                  Text(
                    text = "Circuit protégé : ${fuse.protectedCircuit}",
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                  )

                  Text(
                    text = "Symptôme si coupé : ${fuse.symptomsIfBlown}",
                    color = AluminumLight,
                    fontSize = 11.sp
                  )

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                  ) {
                    OutlinedButton(
                      onClick = { onToggleFuseStatus(fuse.id) },
                      border = BorderStroke(1.dp, if (fuse.isIntact) AluminumMuted else ElectricGreen),
                      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                      Icon(
                        if (fuse.isIntact) Icons.Default.Build else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (fuse.isIntact) AluminumLight else ElectricGreen,
                        modifier = Modifier.size(15.dp)
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = if (fuse.isIntact) "Simuler coupure" else "Remplacer fusible & Valider 12.6V",
                        color = if (fuse.isIntact) AluminumLight else ElectricGreen,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
              }
            }
          }
        } else {
          // Tab 1: Pinout ECU & Signaux Oscilloscope
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(pinouts, key = { it.id }) { pin ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.6f))
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = pin.ecuName,
                      color = MechanicalOrange,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.5.sp
                    )
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = TechCyan.copy(alpha = 0.2f)
                    ) {
                      Text(
                        text = pin.connectorPin,
                        color = TechCyan,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                      )
                    }
                  }

                  Text(
                    text = pin.signalName,
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp
                  )

                  Text(
                    text = "Couleur faisceau & section : ${pin.wireColorCode}",
                    color = AluminumLight,
                    fontSize = 11.sp
                  )

                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, MetalBorder),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                      Text(
                        text = "• Valeur réf. Ralenti : ${pin.referenceIdleValue}",
                        color = ElectricGreen,
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace
                      )
                      Text(
                        text = "• Valeur réf. Charge : ${pin.referenceLoadValue}",
                        color = TechCyan,
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace
                      )
                      Text(
                        text = "• Profil Oscilloscope : ${pin.oscilloscopePattern}",
                        color = WarningYellow,
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
