package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioNoiseAnalysis
import com.example.data.model.ObdConnectionStatus
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

@Composable
fun ObdTelemetryScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val telemetry = uiState.liveTelemetry

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("obd_telemetry_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onBack,
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(MetalCard)
          ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = PureWhite)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "OBD-II & TÉLÉMÉTRIE LIVE",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite
              )
            )
            Text(
              text = "Flux ECU & Analyseur Acoustique IA",
              style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.sp)
            )
          }
        }

        // Bluetooth Toggle Button
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = when (uiState.obdStatus) {
            ObdConnectionStatus.CONNECTED -> ElectricGreen.copy(alpha = 0.2f)
            ObdConnectionStatus.CONNECTING, ObdConnectionStatus.SCANNING -> WarningAmber.copy(alpha = 0.2f)
            ObdConnectionStatus.DISCONNECTED -> PerformanceRedDark.copy(alpha = 0.3f)
          },
          border = BorderStroke(
            1.dp,
            when (uiState.obdStatus) {
              ObdConnectionStatus.CONNECTED -> ElectricGreen
              ObdConnectionStatus.CONNECTING, ObdConnectionStatus.SCANNING -> WarningAmber
              ObdConnectionStatus.DISCONNECTED -> PerformanceRed
            }
          ),
          modifier = Modifier.clickable { viewModel.toggleObdConnection() }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.Bluetooth,
              contentDescription = null,
              tint = if (uiState.obdStatus == ObdConnectionStatus.CONNECTED) ElectricGreen else AluminumLight,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (uiState.obdStatus == ObdConnectionStatus.CONNECTED) "OBD Connecté" else "Connecter",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PureWhite,
                fontSize = 11.sp
              )
            )
          }
        }
      }
    }

    // Live Sensor Virtual Gauges Grid
    item {
      Text(
        text = "DONNÉES EN DIRECT DU CALCULATEUR (ECU)",
        style = MaterialTheme.typography.labelMedium.copy(color = AluminumMuted, fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ObdLiveGaugeCard(
          title = "Pression Turbo",
          value = "${telemetry.turboBoostBar}",
          unit = "Bar (Boost)",
          accentColor = PerformanceRedGlow,
          modifier = Modifier.weight(1f)
        )
        ObdLiveGaugeCard(
          title = "Rampe Commune",
          value = "${telemetry.railPressureBar}",
          unit = "Bar (Common Rail)",
          accentColor = TechCyan,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ObdLiveGaugeCard(
          title = "Température LDR",
          value = "${telemetry.coolantTempC}",
          unit = "°C (Moteur)",
          accentColor = if (telemetry.coolantTempC > 105) PerformanceRed else ElectricGreen,
          modifier = Modifier.weight(1f)
        )
        ObdLiveGaugeCard(
          title = "Suie FAP / DPF",
          value = "${telemetry.dpfSootLoadPercent}",
          unit = "% (Charge FAP)",
          accentColor = if (telemetry.dpfSootLoadPercent > 70) WarningAmber else TechCyan,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Active DTC Codes & Clear DTCs Button
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "CODES DÉFAUTS DTC CALCULATEUR",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = AluminumMuted)
            )

            if (telemetry.activeDtcCodes.isNotEmpty()) {
              Button(
                onClick = { viewModel.clearDtcCodes() },
                colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed, contentColor = PureWhite),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Effacer DTCs", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          if (telemetry.activeDtcCodes.isEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Aucun code défaut actif dans les calculateurs.",
                style = MaterialTheme.typography.bodyMedium.copy(color = ElectricGreen, fontWeight = FontWeight.Bold)
              )
            }
          } else {
            telemetry.activeDtcCodes.forEach { dtc ->
              Surface(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                color = MetalSurface,
                border = BorderStroke(1.dp, PerformanceRed.copy(alpha = 0.4f))
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = dtc,
                    style = MaterialTheme.typography.titleMedium.copy(color = PerformanceRedGlow, fontWeight = FontWeight.Black)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = if (dtc == "P0299") "Pression de suralimentation insuffisante (Turbo)" else "Recyclage des gaz d'échappement EGR (Débit insuffisant)",
                    style = MaterialTheme.typography.bodySmall.copy(color = PureWhite)
                  )
                }
              }
            }
          }
        }
      }
    }

    // Acoustic Audio Noise Analyzer (IA MekanikAI)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.4f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.GraphicEq, contentDescription = null, tint = TechCyan, modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "ANALYSEUR AUDIO DES BRUITS MOTEUR",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                )
                Text(
                  text = "Détection acoustique par IA (Poussoirs, Injecteurs, Turbo)",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Record audio button
          Button(
            onClick = { viewModel.recordAudioSampleAndDiagnose() },
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (uiState.isAudioRecording) PerformanceRed else TechCyan,
              contentColor = PureWhite
            )
          ) {
            Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (uiState.isAudioRecording) "Écoute du bruit moteur en cours (Spectrogramme IA)..." else "Enregistrer un bruit suspect (5 sec)",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
          }

          uiState.selectedAudioAnalysis?.let { analysis ->
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MetalSurface,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = analysis.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                  )
                  Text(
                    text = "${analysis.confidenceRate}% Confiance",
                    style = MaterialTheme.typography.labelSmall.copy(color = ElectricGreen, fontWeight = FontWeight.Bold)
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "Source suspectée : ${analysis.probableSource}",
                  style = MaterialTheme.typography.bodySmall.copy(color = MechanicalOrange, fontWeight = FontWeight.SemiBold)
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = analysis.acousticDescription,
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.5.sp)
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "Action conseillée : ${analysis.recommendedAction}",
                  style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.sp)
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ObdLiveGaugeCard(
  title: String,
  value: String,
  unit: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    color = MetalCard,
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 10.sp))
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = value, style = MaterialTheme.typography.titleLarge.copy(color = accentColor, fontWeight = FontWeight.Black, fontSize = 20.sp))
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = unit, style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 10.sp))
    }
  }
}
