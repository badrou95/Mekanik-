package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AccountRole
import com.example.data.model.VehiclePassportQrData
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikViewModel

@Composable
fun UniversalQrScannerModal(
  registeredPassports: List<VehiclePassportQrData>,
  currentRole: AccountRole,
  viewModel: MekanikViewModel,
  onDismiss: () -> Unit
) {
  var manualTokenInput by remember { mutableStateOf("") }
  var isFlashOn by remember { mutableStateOf(false) }

  // Animated laser scan effect
  val infiniteTransition = rememberInfiniteTransition(label = "LaserScan")
  val laserOffset by infiniteTransition.animateFloat(
    initialValue = 0.1f,
    targetValue = 0.9f,
    animationSpec = infiniteRepeatable(
      animation = tween(1600, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "LaserPosition"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .background(GraphiteDark)
        .testTag("universal_qr_scanner_modal"),
      color = GraphiteDark
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Top Header
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
              Icon(
                Icons.Default.QrCodeScanner,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "SCANNER QR VÉHICULE UNIVERSEL",
                style = MaterialTheme.typography.titleMedium.copy(
                  color = PureWhite,
                  fontWeight = FontWeight.Black
                )
              )
              Text(
                text = "Atelier • Vendeur Pièces • Location • Particulier",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = TechCyan,
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

        Spacer(modifier = Modifier.height(14.dp))

        // Simulated Optical Camera Viewfinder with Laser Scanner
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .height(210.dp),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
          border = BorderStroke(1.5.dp, PerformanceRed.copy(alpha = 0.8f))
        ) {
          Box(modifier = Modifier.fillMaxSize()) {
            // Viewfinder Grid & Corner Targets
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
              verticalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Box(modifier = Modifier.size(24.dp).border(3.dp, PerformanceRed, RoundedCornerShape(topStart = 6.dp)))
                Box(modifier = Modifier.size(24.dp).border(3.dp, PerformanceRed, RoundedCornerShape(topEnd = 6.dp)))
              }

              // Center Targeting Area
              Box(
                modifier = Modifier
                  .align(Alignment.CenterHorizontally)
                  .size(110.dp)
                  .border(1.dp, TechCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.QrCode2,
                  contentDescription = null,
                  tint = PureWhite.copy(alpha = 0.25f),
                  modifier = Modifier.size(80.dp)
                )

                // Laser scan line
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .offset(y = ((laserOffset - 0.5f) * 90).dp)
                    .background(PerformanceRedGlow)
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Box(modifier = Modifier.size(24.dp).border(3.dp, PerformanceRed, RoundedCornerShape(bottomStart = 6.dp)))
                Box(modifier = Modifier.size(24.dp).border(3.dp, PerformanceRed, RoundedCornerShape(bottomEnd = 6.dp)))
              }
            }

            // Controls over Camera (Flash & Status)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.TopCenter),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = GraphiteBlack.copy(alpha = 0.8f),
                border = BorderStroke(1.dp, TechCyan)
              ) {
                Text(
                  text = "VISÉE OPTIQUE QR ACTIVE",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = TechCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                  ),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }

              IconButton(
                onClick = { isFlashOn = !isFlashOn },
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(if (isFlashOn) WarningAmber else MetalCard)
              ) {
                Icon(
                  imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                  contentDescription = "Flash",
                  tint = if (isFlashOn) GraphiteBlack else PureWhite,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Manual Token / VIN Input
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = manualTokenInput,
            onValueChange = { manualTokenInput = it },
            placeholder = { Text("Code QR, VIN ou Plaque...", color = AluminumMuted, fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MetalCard,
              unfocusedContainerColor = MetalCard,
              focusedBorderColor = PerformanceRed,
              unfocusedBorderColor = MetalBorder,
              focusedTextColor = PureWhite,
              unfocusedTextColor = PureWhite
            )
          )

          Button(
            onClick = {
              if (manualTokenInput.isNotBlank()) {
                viewModel.scanUniversalQrToken(manualTokenInput)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
          ) {
            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Valider", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Instant Scan Quick Presets
        Text(
          text = "VÉHICULES ENREGISTRÉS DISPONIBLES AU SCAN RAPIDE",
          style = MaterialTheme.typography.labelSmall.copy(
            color = AluminumMuted,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(registeredPassports) { passport ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  viewModel.selectQrPassport(passport)
                },
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PureWhite)
                    .padding(4.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.QrCode2,
                    contentDescription = null,
                    tint = GraphiteDark,
                    modifier = Modifier.size(34.dp)
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "${passport.brand} ${passport.model}",
                      style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                      )
                    )
                  }
                  Text(
                    text = "Plaque : ${passport.vehiclePlate} • ${passport.engineCode}",
                    style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp)
                  )
                  Text(
                    text = "Token : ${passport.qrToken}",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = PerformanceRedGlow,
                      fontFamily = FontFamily.Monospace,
                      fontSize = 10.sp
                    )
                  )
                }

                Column(horizontalAlignment = Alignment.End) {
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (passport.registeredByRole) {
                      AccountRole.GARAGE_PRO -> PerformanceRedDark
                      AccountRole.VENDEUR_PIECES -> MechanicalOrangeDark
                      AccountRole.ATELIER_MAGASIN_MIXTE -> MechanicalOrangeDark
                      AccountRole.ATELIER_CARROSSERIE -> PerformanceRedDark
                      AccountRole.CONDUCTEUR -> EvBlue.copy(alpha = 0.3f)
                      AccountRole.LOCATION_VOITURES -> MechanicalOrangeDark
                    },
                    border = BorderStroke(
                      1.dp,
                      when (passport.registeredByRole) {
                        AccountRole.GARAGE_PRO -> PerformanceRed
                        AccountRole.VENDEUR_PIECES -> MechanicalOrange
                        AccountRole.ATELIER_MAGASIN_MIXTE -> MechanicalOrange
                        AccountRole.ATELIER_CARROSSERIE -> PerformanceRed
                        AccountRole.CONDUCTEUR -> EvBlue
                        AccountRole.LOCATION_VOITURES -> MechanicalOrange
                      }
                    )
                  ) {
                    Text(
                      text = passport.registeredByRole.shortTitle,
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = PureWhite,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                      ),
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "${passport.orderedPartsForRetailer.size} pièces • ${passport.activeRepairOrdersForWorkshop.size} OR",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = AluminumMuted,
                      fontSize = 9.5.sp
                    )
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Create New QR Passport Action
        Button(
          onClick = {
            viewModel.toggleGenerateVehicleQrModal(true)
            onDismiss()
          },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, PerformanceRedGlow),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Enregistrer un Nouveau Véhicule & Générer son QR",
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }
    }
  }
}
