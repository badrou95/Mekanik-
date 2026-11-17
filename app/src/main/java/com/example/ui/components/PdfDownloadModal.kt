package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import com.example.util.GeneratedPdfResult
import com.example.util.PdfGeneratorUtil
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun PdfDownloadSuccessModal(
  result: GeneratedPdfResult,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val df = DecimalFormat("#,###", DecimalFormatSymbols(Locale.FRENCH))

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .wrapContentHeight()
        .testTag("pdf_download_modal"),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, MetalBorder)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Icon Checkmark with glowing halo
        Box(
          modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(ElectricGreen.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            Icons.Default.FileDownloadDone,
            contentDescription = null,
            tint = ElectricGreen,
            modifier = Modifier.size(32.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "DOCUMENT PDF GÉNÉRÉ & TÉLÉCHARGÉ",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Black,
            color = PureWhite,
            letterSpacing = 0.5.sp,
            fontSize = 15.sp
          )
        )

        Text(
          text = "Le document a été créé avec succès et enregistré sur votre appareil",
          style = MaterialTheme.typography.bodySmall.copy(
            color = AluminumLight,
            fontSize = 11.5.sp
          ),
          modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Document Details Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = PerformanceRedDark.copy(alpha = 0.4f),
                border = BorderStroke(0.5.dp, PerformanceRed)
              ) {
                Text(
                  text = result.documentType,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  ),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }

              Text(
                text = "${df.format(result.totalAmountDzd)} DA TTC",
                style = MaterialTheme.typography.labelMedium.copy(
                  color = MechanicalOrange,
                  fontWeight = FontWeight.Black,
                  fontSize = 13.sp
                )
              )
            }

            Divider(color = MetalBorder.copy(alpha = 0.5f))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Numéro :",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
              )
              Text(
                text = result.documentNumber,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = PureWhite,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                )
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Client :",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
              )
              Text(
                text = result.clientName,
                style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontWeight = FontWeight.SemiBold)
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Fichier :",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
              )
              Text(
                text = "${result.fileName} (${result.formattedSize})",
                style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontWeight = FontWeight.Bold)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Action Buttons: Open PDF & Share
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = {
              PdfGeneratorUtil.sharePdfFile(context, result.file, "${result.documentType} ${result.documentNumber}")
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite),
            border = BorderStroke(1.dp, TechCyan),
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("btn_share_pdf")
          ) {
            Icon(Icons.Default.Share, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Partager", color = TechCyan, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
          }

          Button(
            onClick = {
              PdfGeneratorUtil.openPdfFile(context, result.file)
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
            modifier = Modifier
              .weight(1.3f)
              .height(44.dp)
              .testTag("btn_open_pdf")
          ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Ouvrir PDF", color = PureWhite, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Fermer", color = AluminumMuted, style = MaterialTheme.typography.labelMedium)
        }
      }
    }
  }
}
