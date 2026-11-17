package com.example.ui.components

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.FiscalInvoice
import com.example.data.model.PaymentMethod
import com.example.ui.theme.*
import com.example.util.PdfGeneratorUtil

@Composable
fun FiscalInvoiceModal(
  invoice: FiscalInvoice,
  onOpenPayment: () -> Unit,
  onDownloadPdf: (() -> Unit)? = null,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .background(GraphiteDark)
        .testTag("fiscal_invoice_dialog"),
      color = GraphiteDark
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
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
                .size(38.dp)
                .clip(CircleShape)
                .background(MechanicalOrange.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Receipt, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = invoice.invoiceType.label,
                style = MaterialTheme.typography.titleMedium.copy(
                  color = PureWhite,
                  fontWeight = FontWeight.Black
                )
              )
              Text(
                text = "N° ${invoice.invoiceNumber} • Date : ${invoice.date}",
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

        Spacer(modifier = Modifier.height(14.dp))

        // Printable Fiscal Document Sheet
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // Garage legal header & NIF/RC
            item {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(MetalSurface, RoundedCornerShape(10.dp))
                  .padding(12.dp)
              ) {
                Text(
                  text = invoice.garageName,
                  style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Black)
                )
                Text(
                  text = invoice.garageAddress,
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(text = "NIF : ${invoice.garageNif}", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted))
                  Text(text = "RC : ${invoice.garageRc}", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted))
                }
              }
            }

            // Client & Vehicle Info
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(text = "CLIENT", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold))
                  Text(text = invoice.clientName, style = MaterialTheme.typography.bodyMedium.copy(color = PureWhite, fontWeight = FontWeight.Bold))
                  Text(text = invoice.clientPhone, style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray))
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                  Text(text = "VÉHICULE & CHÂSSIS", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold))
                  Text(text = invoice.vehicleModel, style = MaterialTheme.typography.bodyMedium.copy(color = PureWhite, fontWeight = FontWeight.Bold))
                  Text(text = "Matricule : ${invoice.plateNumber}", style = MaterialTheme.typography.bodySmall.copy(color = TechCyan))
                }
              }
            }

            // Table of items
            item {
              Text(
                text = "DÉTAIL DES PRESTATIONS & PIÈCES DÉTACHÉES",
                style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold)
              )
            }

            items(invoice.items) { item ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = MetalSurface,
                border = BorderStroke(1.dp, MetalBorder)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = item.description,
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite, fontSize = 12.5.sp)
                    )
                    Text(
                      text = "Réf : ${item.reference} • Qté : ${item.quantity} x ${item.unitPriceDzd.toInt()} DA",
                      style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 11.sp)
                    )
                  }
                  Text(
                    text = "${item.totalLineDzd.toInt()} DA",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, color = OffWhite)
                  )
                }
              }
            }

            // Calculations Box
            item {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(MetalSurface, RoundedCornerShape(10.dp))
                  .padding(12.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(text = "Total HT :", style = MaterialTheme.typography.bodyMedium.copy(color = AluminumGray))
                  Text(text = "${invoice.subTotalDzd.toInt()} DA", style = MaterialTheme.typography.bodyMedium.copy(color = PureWhite, fontWeight = FontWeight.Bold))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(text = "TVA (${invoice.tvaPercent.toInt()}%) :", style = MaterialTheme.typography.bodyMedium.copy(color = AluminumGray))
                  Text(text = "${invoice.tvaAmountDzd.toInt()} DA", style = MaterialTheme.typography.bodyMedium.copy(color = PureWhite, fontWeight = FontWeight.Bold))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(text = "Droit de Timbre :", style = MaterialTheme.typography.bodyMedium.copy(color = AluminumGray))
                  Text(text = "${invoice.stampTaxDzd.toInt()} DA", style = MaterialTheme.typography.bodyMedium.copy(color = PureWhite, fontWeight = FontWeight.Bold))
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = MetalBorder)
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "TOTAL NET À PAYER (TTC) :",
                    style = MaterialTheme.typography.titleMedium.copy(color = PerformanceRedGlow, fontWeight = FontWeight.Black)
                  )
                  Text(
                    text = "${invoice.totalTtcDzd.toInt()} DZD",
                    style = MaterialTheme.typography.titleLarge.copy(color = PureWhite, fontWeight = FontWeight.Black)
                  )
                }
              }
            }

            // Payment status & QR Hash
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (invoice.isPaid) ElectricGreen.copy(alpha = 0.2f) else WarningAmber.copy(alpha = 0.2f),
                  border = BorderStroke(1.dp, if (invoice.isPaid) ElectricGreen else WarningAmber)
                ) {
                  Text(
                    text = if (invoice.isPaid) "PAYÉ (${invoice.paymentMethod?.label ?: "CIB"})" else "EN ATTENTE DE RÈGLEMENT",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = if (invoice.isPaid) ElectricGreen else WarningAmber,
                      fontWeight = FontWeight.Black
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                  )
                }

                Text(
                  text = "Certifié DZ-MEKANIK",
                  style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons: Pay via CIB / Edahabia and Download PDF
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = {
              if (onDownloadPdf != null) {
                onDownloadPdf()
              } else {
                try {
                  val result = PdfGeneratorUtil.generateFiscalInvoicePdf(context, invoice)
                  PdfGeneratorUtil.openPdfFile(context, result.file)
                } catch (_: Exception) {}
              }
            },
            modifier = Modifier
              .weight(1f)
              .testTag("btn_download_fiscal_invoice_pdf"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed, contentColor = PureWhite)
          ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Télécharger PDF", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
          }

          if (!invoice.isPaid) {
            Button(
              onClick = onOpenPayment,
              modifier = Modifier
                .weight(1f)
                .testTag("btn_pay_fiscal_invoice"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen, contentColor = PureWhite)
            ) {
              Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "Régler CIB", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
          }
        }
      }
    }
  }
}
