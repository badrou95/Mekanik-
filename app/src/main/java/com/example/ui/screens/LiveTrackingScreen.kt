package com.example.ui.screens

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
import com.example.data.model.RepairStepStatus
import com.example.data.model.RepairTimelineStep
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

@Composable
fun LiveTrackingScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val session = uiState.liveRepairSession

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("live_tracking_screen"),
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
              text = "SUIVI ATELIER EN DIRECT",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite
              )
            )
            Text(
              text = "N° Suivi : ${session.trackingId}",
              style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.sp)
            )
          }
        }

        Button(
          onClick = { viewModel.togglePassportModal(true) },
          colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed, contentColor = PureWhite),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Passeport QR", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
        }
      }
    }

    // Live Progress Summary Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = session.vehicleName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = PureWhite)
              )
              Text(
                text = "Client : ${session.clientName} • Matricule : ${session.plateNumber}",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = PerformanceRedDark.copy(alpha = 0.3f),
              border = BorderStroke(1.dp, PerformanceRed)
            ) {
              Text(
                text = "${session.progressPercent}% Terminé",
                style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Progress Bar
          LinearProgressIndicator(
            progress = { session.progressPercent / 100f },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = PerformanceRed,
            trackColor = MetalSurface
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Étape : ${session.currentStageName}",
              style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontWeight = FontWeight.Bold)
            )
            Text(
              text = "Prêt : ${session.estimatedReadyTime}",
              style = MaterialTheme.typography.bodySmall.copy(color = ElectricGreen, fontWeight = FontWeight.Bold)
            )
          }
        }
      }
    }

    // Step by step timeline
    item {
      Text(
        text = "TIMELINE D'AVANCEMENT ATELIER",
        style = MaterialTheme.typography.labelMedium.copy(color = AluminumMuted, fontWeight = FontWeight.Bold)
      )
    }

    items(session.steps) { step ->
      TimelineStepCard(
        step = step,
        onClick = { viewModel.updateLiveRepairStep(step.stepNumber) }
      )
    }
  }
}

@Composable
private fun TimelineStepCard(
  step: RepairTimelineStep,
  onClick: () -> Unit
) {
  val (statusColor, statusIcon) = when (step.status) {
    RepairStepStatus.COMPLETED -> Pair(ElectricGreen, Icons.Default.CheckCircle)
    RepairStepStatus.IN_PROGRESS -> Pair(PerformanceRed, Icons.Default.Handyman)
    RepairStepStatus.PENDING -> Pair(AluminumMuted, Icons.Default.RadioButtonUnchecked)
  }

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(14.dp),
    color = MetalCard,
    border = BorderStroke(1.dp, if (step.status == RepairStepStatus.IN_PROGRESS) PerformanceRed else MetalBorder)
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(statusColor.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(18.dp))
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Étape ${step.stepNumber} : ${step.title}",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = if (step.status == RepairStepStatus.PENDING) AluminumMuted else PureWhite
            )
          )
          Text(
            text = step.timestamp,
            style = MaterialTheme.typography.labelSmall.copy(color = statusColor, fontWeight = FontWeight.Bold)
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = step.description,
          style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.5.sp)
        )

        step.technicianName?.let { tech ->
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Intervenant : $tech",
            style = MaterialTheme.typography.labelSmall.copy(color = TechCyan, fontSize = 10.5.sp)
          )
        }
      }
    }
  }
}
