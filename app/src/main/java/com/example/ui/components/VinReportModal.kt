package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VinReport
import com.example.ui.theme.*

@Composable
fun VinReportModal(
  report: VinReport,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            Icons.Default.Verified,
            contentDescription = null,
            tint = PerformanceRed,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "RAPPORT HISTORIQUE VÉHICULE",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Black,
              color = PureWhite
            )
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumGray)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Certified Header Tag
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = GraphiteBlack,
          border = BorderStroke(1.dp, MetalBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "ID RAPPORT : ${report.reportToken}",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = PerformanceRedGlow,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                )
              )
              Text(
                text = "Validé par le Réseau Mekanik+ Algérie",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumGray,
                  fontSize = 10.5.sp
                )
              )
            }
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = ElectricGreen.copy(alpha = 0.2f),
              border = BorderStroke(0.5.dp, ElectricGreen)
            ) {
              Text(
                text = "CERTIFIÉ",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = ElectricGreen,
                  fontWeight = FontWeight.Black,
                  fontSize = 9.sp
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }

        // Vehicle Summary Matrix
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MetalSurface)
            .padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          ReportRow("Véhicule :", report.brandModel)
          ReportRow("Numéro VIN :", report.vin, isMono = true)
          ReportRow("Numéro Moteur :", report.engineNumber, isMono = true)
          ReportRow("Boîte de Vitesses :", report.gearboxCode)
          ReportRow("1ère Mise en Circulation :", report.firstRegistrationDate)
          ReportRow("Contrôle Technique :", report.technicalControlStatus)
        }

        // Mileage History
        Text(
          text = "HISTORIQUE KILOMÉTRIQUE ENREGISTRÉ",
          style = MaterialTheme.typography.labelSmall.copy(
            color = AluminumMuted,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
          )
        )

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(GraphiteBlack)
            .padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          report.mileageHistory.forEach { record ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = record.date,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = AluminumLight,
                    fontWeight = FontWeight.SemiBold
                  )
                )
                Text(
                  text = record.source,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = AluminumMuted,
                    fontSize = 10.sp
                  )
                )
              }
              Text(
                text = "${record.km} km",
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = PureWhite,
                  fontFamily = FontFamily.Monospace
                )
              )
            }
          }
        }

        // Recall Campaigns
        Text(
          text = "CAMPAGNES DE RAPPEL CONSTRUCTEUR",
          style = MaterialTheme.typography.labelSmall.copy(
            color = AluminumMuted,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
          )
        )

        report.recallCampaigns.forEach { campaign ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.CheckCircle,
              contentDescription = null,
              tint = TechCyan,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = campaign,
              style = MaterialTheme.typography.bodySmall.copy(
                color = OffWhite,
                fontSize = 11.sp
              )
            )
          }
        }
      }
    },
    confirmButton = {
      MekanikPrimaryButton(
        text = "IMPRIMER / PDF (300 DA)",
        onClick = onDismiss,
        icon = Icons.Default.Print,
        modifier = Modifier.fillMaxWidth(),
        testTag = "btn_print_vin_report"
      )
    },
    containerColor = MetalCard,
    modifier = modifier.testTag("vin_report_modal")
  )
}

@Composable
private fun ReportRow(label: String, value: String, isMono: Boolean = false) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall.copy(
        color = AluminumMuted,
        fontSize = 11.sp
      )
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall.copy(
        color = OffWhite,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        fontFamily = if (isMono) FontFamily.Monospace else FontFamily.SansSerif
      )
    )
  }
}
