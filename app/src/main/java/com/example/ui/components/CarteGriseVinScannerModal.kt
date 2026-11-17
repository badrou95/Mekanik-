package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CarteGrisePresetsProvider
import com.example.data.model.CarteGriseScanPreset
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun CarteGriseVinScannerModal(
  onVinDetected: (String) -> Unit,
  onCreateRepairOrder: ((CarteGriseScanPreset) -> Unit)? = null,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val presets = remember { CarteGrisePresetsProvider.presets }
  var selectedPresetIndex by remember { mutableIntStateOf(0) }
  val activePreset = presets[selectedPresetIndex]

  var isScanning by remember { mutableStateOf(true) }
  var isFlashOn by remember { mutableStateOf(false) }
  var scanSuccess by remember { mutableStateOf(false) }

  // Animated laser sweep effect
  val infiniteTransition = rememberInfiniteTransition(label = "cg_laser_sweep")
  val laserY by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1600, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "cg_laser_anim"
  )

  // Trigger OCR extraction whenever preset changes
  LaunchedEffect(selectedPresetIndex) {
    isScanning = true
    scanSuccess = false
    delay(900)
    isScanning = false
    scanSuccess = true
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .background(GraphiteDark)
        .testTag("carte_grise_vin_scanner_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {

        // ==================== 1. TOP BAR ====================
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
                .background(PerformanceRedDark)
                .border(1.2.dp, PerformanceRedGlow, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.DocumentScanner,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "SCAN CARTE GRISE",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = PureWhite
                  )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = PerformanceRed,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "VIN UNIQUE",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = PureWhite,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.sp
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }
              Text(
                text = "Reconnaissance ciblée de la section (E) • Numéro de châssis 17 car.",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumLight,
                  fontSize = 11.sp
                )
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

        // ==================== 2. PRESET SELECTION CHIPS ====================
        Column {
          Text(
            text = "MODÈLES & DOCUMENTS CARTE GRISE ENREGISTRÉS :",
            style = MaterialTheme.typography.labelSmall.copy(
              color = AluminumMuted,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              letterSpacing = 0.8.sp
            )
          )
          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            presets.forEachIndexed { index, item ->
              val isSelected = index == selectedPresetIndex
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) PerformanceRed else MetalCard,
                border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
                modifier = Modifier.clickable { selectedPresetIndex = index }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = if (isSelected) PureWhite else TechCyan,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = item.title,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                      color = if (isSelected) PureWhite else AluminumLight,
                      fontSize = 11.sp
                    )
                  )
                }
              }
            }
          }
        }

        // ==================== 3. HUD CAMERA VIEWFINDER (CARTE GRISE RECOGNITION) ====================
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF0D1117))
            .border(
              2.dp,
              if (scanSuccess) ElectricGreen else PerformanceRedGlow,
              RoundedCornerShape(18.dp)
            ),
          contentAlignment = Alignment.Center
        ) {
          // Document Simulation Card (Photographie Carte Grise)
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            // Document Header
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = activePreset.countryDoc,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = AluminumGray,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  )
                )
                Text(
                  text = "CERTIFICAT D'IMMATRICULATION",
                  style = MaterialTheme.typography.labelMedium.copy(
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                )
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MetalSurface,
                border = BorderStroke(0.5.dp, MetalBorder)
              ) {
                Text(
                  text = "Immat : ${activePreset.plateNumber}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = TechCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  ),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            // Central Targeted Bounding Box: SECTION (E) VIN TARGET
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
              border = BorderStroke(
                1.5.dp,
                if (scanSuccess) ElectricGreen else PerformanceRedGlow
              )
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      Icons.Default.FilterCenterFocus,
                      contentDescription = null,
                      tint = if (scanSuccess) ElectricGreen else PerformanceRedGlow,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = activePreset.sectionELabel,
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = if (scanSuccess) ElectricGreen else PerformanceRedGlow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                      )
                    )
                  }

                  if (scanSuccess) {
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = ElectricGreen.copy(alpha = 0.2f),
                      border = BorderStroke(0.5.dp, ElectricGreen)
                    ) {
                      Text(
                        text = "VIN 17/17 OK",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = ElectricGreen,
                          fontWeight = FontWeight.Black,
                          fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // High Prominence Isolated Scanned VIN
                Text(
                  text = activePreset.vin,
                  style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    color = if (scanSuccess) PureWhite else AluminumMuted,
                    letterSpacing = 2.sp,
                    fontSize = 18.sp
                  )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                  text = "Véhicule : ${activePreset.brandModel} • Moteur : ${activePreset.engineCode}",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = AluminumLight,
                    fontSize = 10.5.sp
                  )
                )
              }
            }

            // Bottom Document Metadata
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "1ère MEC : ${activePreset.firstRegistration}",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 10.sp)
              )
              Text(
                text = "Cylindrée : ${activePreset.displacement}",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 10.sp)
              )
            }
          }

          // Laser Scan Animation
          if (isScanning) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .align(Alignment.TopCenter)
                .offset(y = (laserY * 200).dp)
                .background(
                  brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, PerformanceRedGlow, MechanicalOrange, Color.Transparent)
                  )
                )
            )
          }

          // Flash Light Button Overlay
          IconButton(
            onClick = { isFlashOn = !isFlashOn },
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(8.dp)
              .size(34.dp)
              .clip(CircleShape)
              .background(if (isFlashOn) MechanicalOrange else GraphiteBlack)
          ) {
            Icon(
              if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
              contentDescription = "Flash",
              tint = PureWhite,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        // ==================== 4. CONFIRMATION & DIRECT VIN INJECTION ====================
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, if (scanSuccess) ElectricGreen.copy(alpha = 0.6f) else MetalBorder)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = ElectricGreen,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "VIN RECONNU DEPUIS LA CARTE GRISE",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = ElectricGreen,
                    letterSpacing = 0.8.sp
                  )
                )
              }

              Text(
                text = "Score IA : 99.8%",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = TechCyan,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // The exact clean VIN to be injected
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = GraphiteBlack,
              border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.4f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = activePreset.vin,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    letterSpacing = 1.2.sp
                  )
                )

                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = PerformanceRedDark,
                  border = BorderStroke(0.5.dp, PerformanceRed)
                ) {
                  Text(
                    text = activePreset.brandModel.take(18),
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = PureWhite,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: 1. Search VIN | 2. Pre-fill Repair Order (OR)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              MekanikPrimaryButton(
                text = "INJECTER LE VIN & RECHERCHER",
                onClick = {
                  onVinDetected(activePreset.vin)
                },
                icon = Icons.Default.Search,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("btn_inject_carte_grise_vin")
              )

              if (onCreateRepairOrder != null) {
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = TechnicalBlue,
                  border = BorderStroke(1.dp, TechCyan),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                      onCreateRepairOrder(activePreset)
                    }
                    .testTag("btn_fill_repair_order_from_cg")
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 10.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "REMPLIR L'ORDRE DE RÉPARATION (ATELIER)",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = PureWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                      )
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
