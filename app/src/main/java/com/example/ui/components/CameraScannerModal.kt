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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ScanDetectionResult
import com.example.data.model.ScanTargetType
import com.example.ui.theme.*

@Composable
fun CameraScannerModal(
  targetType: ScanTargetType,
  isScanning: Boolean,
  detectionResult: ScanDetectionResult?,
  onTargetTypeChanged: (ScanTargetType) -> Unit,
  onApplyResult: () -> Unit,
  onDismiss: () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "laser_sweep")
  val laserY by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1800, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "laser_anim"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF080B0E))
        .testTag("camera_scanner_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
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
                .size(36.dp)
                .clip(CircleShape)
                .background(PerformanceRed.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.CameraAlt, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "SCANNER INTELLIGENT OCR",
                style = MaterialTheme.typography.titleMedium.copy(
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp
                )
              )
              Text(
                text = "Reconnaissance optique haute précision",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 11.sp)
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

        // Mode Selector Chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ScanTargetType.values().forEach { mode ->
            val isSelected = mode == targetType
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = if (isSelected) PerformanceRed else MetalCard,
              border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
              modifier = Modifier.clickable { onTargetTypeChanged(mode) }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                if (isSelected) {
                  Icon(Icons.Default.Check, contentDescription = null, tint = PureWhite, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                  text = mode.label,
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

        // Camera Viewfinder & Laser Animation Area
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF141920))
            .border(2.dp, if (detectionResult != null) ElectricGreen else TechCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
          contentAlignment = Alignment.Center
        ) {
          // Grid background lines
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                brush = Brush.radialGradient(
                  colors = listOf(TechCyan.copy(alpha = 0.08f), Color.Transparent)
                )
              )
          )

          // Laser scan line
          if (isScanning) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .align(Alignment.TopCenter)
                .offset(y = (laserY * 260).dp)
                .background(
                  brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, PerformanceRedGlow, TechCyan, Color.Transparent)
                  )
                )
            )
          }

          // Target reticle
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = if (detectionResult != null) Icons.Default.CheckCircle else Icons.Default.QrCodeScanner,
              contentDescription = null,
              tint = if (detectionResult != null) ElectricGreen else TechCyan,
              modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = if (isScanning) "Analyse optique par IA en cours..." else "Cible détectée avec succès !",
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (detectionResult != null) ElectricGreen else OffWhite
              )
            )
            Text(
              text = targetType.description,
              style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
            )
          }
        }

        // Detection Result Panel
        if (detectionResult != null) {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MetalCard),
            border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.5f))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = ElectricGreen.copy(alpha = 0.2f),
                  border = BorderStroke(1.dp, ElectricGreen)
                ) {
                  Text(
                    text = "CONFIANCE ${detectionResult.confidencePercent}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = ElectricGreen,
                      fontWeight = FontWeight.Black,
                      fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }

                Text(
                  text = "Algérie DZ",
                  style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted)
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = detectionResult.extractedVin ?: detectionResult.extractedPlate ?: detectionResult.extractedOem ?: "Texte détecté",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.Black,
                  color = PureWhite,
                  letterSpacing = 1.2.sp
                )
              )

              detectionResult.vehiclePreview?.let { preview ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = preview,
                  style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontWeight = FontWeight.SemiBold)
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              Button(
                onClick = onApplyResult,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("btn_apply_scan"),
                colors = ButtonDefaults.buttonColors(
                  containerColor = PerformanceRed,
                  contentColor = PureWhite
                ),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Injecter & Décoder le Véhicule",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
              }
            }
          }
        }

        // Footer helper
        Text(
          text = "Placez le document ou la pièce bien en face du capteur photo dans un endroit éclairé.",
          style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 10.5.sp),
          modifier = Modifier.align(Alignment.CenterHorizontally)
        )
      }
    }
  }
}
