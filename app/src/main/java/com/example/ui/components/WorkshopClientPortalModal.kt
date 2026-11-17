package com.example.ui.components

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.util.PdfGeneratorUtil
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

enum class ClientPortalSection(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
  REPARATION_EN_COURS("Réparation en Direct", Icons.Default.DirectionsCar),
  HISTORIQUE_REPARATIONS("Historique Atelier", Icons.Default.History),
  FACTURES_DOCUMENTS("Factures & Reçus", Icons.Default.ReceiptLong)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkshopClientPortalModal(
  account: WorkshopClientAccount,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedSection by remember { mutableStateOf(ClientPortalSection.REPARATION_EN_COURS) }
  var showPrintPreviewInvoice by remember { mutableStateOf<FiscalInvoice?>(null) }
  var showQrCodeModal by remember { mutableStateOf(false) }
  var downloadToastMessage by remember { mutableStateOf<String?>(null) }

  val df = remember { DecimalFormat("#,###", DecimalFormatSymbols(Locale.FRENCH)) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Scaffold(
      modifier = Modifier
        .fillMaxSize()
        .background(GraphiteDark)
        .testTag("workshop_client_portal_modal"),
      topBar = {
        TopAppBar(
          title = {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "ESPACE CLIENT ATELIER",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    letterSpacing = 1.sp
                  )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = ElectricGreen.copy(alpha = 0.2f),
                  border = BorderStroke(1.dp, ElectricGreen)
                ) {
                  Text(
                    text = "GRATUIT",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = ElectricGreen,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
              Text(
                text = "Accès sécurisé réservé au propriétaire • Consultation & Impression",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
              )
            }
          },
          actions = {
            IconButton(
              onClick = { showQrCodeModal = true },
              modifier = Modifier.testTag("btn_show_my_qr_code")
            ) {
              Icon(Icons.Default.QrCode, contentDescription = "Mon QR Code", tint = TechCyan)
            }
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite)
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(containerColor = GraphiteBlack)
        )
      },
      bottomBar = {
        Surface(
          color = GraphiteBlack,
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            ClientPortalSection.values().forEach { section ->
              val isSelected = selectedSection == section
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) PerformanceRed else Color.Transparent,
                modifier = Modifier
                  .clickable { selectedSection = section }
                  .padding(4.dp)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    section.icon,
                    contentDescription = null,
                    tint = if (isSelected) PureWhite else AluminumMuted,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = section.title,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                      color = if (isSelected) PureWhite else AluminumGray,
                      fontSize = 11.sp
                    )
                  )
                }
              }
            }
          }
        }
      }
    ) { paddingValues ->
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .background(GraphiteDark)
          .padding(paddingValues)
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Vehicle & Workshop ID Header Card
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
                    text = account.clientName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                  )
                  Text(
                    text = "Véhicule: ${account.vehicleModel}",
                    style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight)
                  )
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, PerformanceRedGlow)
                ) {
                  Text(
                    text = account.plateNumber,
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black,
                      color = PureWhite
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))
              Divider(color = MetalBorder)
              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Storefront, contentDescription = null, tint = TechnicalBlue, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = account.workshopName,
                    style = MaterialTheme.typography.labelSmall.copy(color = AluminumGray, fontSize = 11.sp)
                  )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Phone, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = account.workshopPhone.split("/").first().trim(),
                    style = MaterialTheme.typography.labelSmall.copy(color = ElectricGreen, fontWeight = FontWeight.Bold)
                  )
                }
              }
            }
          }
        }

        // Section Content Switcher
        when (selectedSection) {
          ClientPortalSection.REPARATION_EN_COURS -> {
            item {
              val activeOrder = account.activeRepairOrder
              if (activeOrder != null) {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.5.dp, PerformanceRedGlow)
                ) {
                  Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                          modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(activeOrder.status.colorHex))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                          text = "STATUT ACTUEL : ${activeOrder.status.label.uppercase()}",
                          style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(activeOrder.status.colorHex),
                            fontWeight = FontWeight.Black
                          )
                        )
                      }

                      Text(
                        text = activeOrder.orderNumber,
                        style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontFamily = FontFamily.Monospace)
                      )
                    }

                    // Progress Stepper
                    RepairProgressStepper(currentStatus = activeOrder.status)

                    Divider(color = MetalBorder)

                    Text(
                      text = "Travaux programmés / Pièces en cours :",
                      style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold)
                    )

                    activeOrder.items.forEach { item ->
                      Row(
                        modifier = Modifier
                          .fillMaxWidth()
                          .background(GraphiteBlack, RoundedCornerShape(8.dp))
                          .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Column {
                          Text(text = item.name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = PureWhite))
                          Text(text = "Réf: ${item.reference} • Qte: ${item.quantity}", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 10.sp))
                        }
                        Text(
                          text = "${df.format(item.priceDzd * item.quantity)} DA",
                          style = MaterialTheme.typography.bodySmall.copy(color = MechanicalOrange, fontWeight = FontWeight.Bold)
                        )
                      }
                    }

                    if (activeOrder.notes.isNotBlank()) {
                      Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GraphiteBlack,
                        border = BorderStroke(1.dp, MetalBorder)
                      ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                          Icon(Icons.Default.Notes, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
                          Spacer(modifier = Modifier.width(8.dp))
                          Text(text = activeOrder.notes, style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.5.sp))
                        }
                      }
                    }
                  }
                }
              } else {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.dp, MetalBorder)
                ) {
                  Column(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Aucune réparation en cours", style = MaterialTheme.typography.titleMedium.copy(color = PureWhite, fontWeight = FontWeight.Bold))
                    Text(text = "Votre véhicule est actuellement prêt ou en circulation normale.", style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray))
                  }
                }
              }
            }
          }

          ClientPortalSection.HISTORIQUE_REPARATIONS -> {
            if (account.repairHistory.isEmpty()) {
              item {
                Text(text = "Aucun historique disponible pour le moment.", style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted))
              }
            } else {
              items(account.repairHistory) { order ->
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.dp, MetalBorder)
                ) {
                  Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text(text = "Intervention ${order.orderNumber}", style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold))
                        Text(text = "Date : ${order.date} • ${order.mileageKm} km", style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp))
                      }

                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(order.status.colorHex).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(order.status.colorHex))
                      ) {
                        Text(
                          text = order.status.label,
                          style = MaterialTheme.typography.labelSmall.copy(color = Color(order.status.colorHex), fontWeight = FontWeight.Bold),
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                      }
                    }

                    Divider(color = MetalBorder)

                    order.items.forEach { item ->
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Text(text = "• ${item.name} (${item.quantity}x)", style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight))
                        Text(text = "${df.format(item.priceDzd * item.quantity)} DA", style = MaterialTheme.typography.bodySmall.copy(color = PureWhite))
                      }
                    }

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(text = "Main d'œuvre (${order.laborHours}h)", style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight))
                      Text(text = "${df.format(order.laborTotalDzd)} DA", style = MaterialTheme.typography.bodySmall.copy(color = PureWhite))
                    }

                    Divider(color = MetalBorder)

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(text = "TOTAL INTERVENTION", style = MaterialTheme.typography.labelMedium.copy(color = AluminumMuted, fontWeight = FontWeight.Bold))
                      Text(text = "${df.format(order.netTotalDzd)} DA", style = MaterialTheme.typography.titleSmall.copy(color = MechanicalOrange, fontWeight = FontWeight.Black))
                    }
                  }
                }
              }
            }
          }

          ClientPortalSection.FACTURES_DOCUMENTS -> {
            if (account.invoices.isEmpty()) {
              item {
                Text(text = "Aucune facture émise pour le moment.", style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted))
              }
            } else {
              items(account.invoices) { invoice ->
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.4f))
                ) {
                  Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = TechCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                          Text(text = invoice.invoiceNumber, style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Black))
                          Text(text = "${invoice.invoiceType.label} • ${invoice.date}", style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 10.5.sp))
                        }
                      }

                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (invoice.isPaid) ElectricGreen.copy(alpha = 0.2f) else PerformanceRedDark.copy(alpha = 0.3f),
                        border = BorderStroke(1.dp, if (invoice.isPaid) ElectricGreen else PerformanceRed)
                      ) {
                        Text(
                          text = if (invoice.isPaid) "PAYÉE" else "À RÉGLER",
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = if (invoice.isPaid) ElectricGreen else PerformanceRedGlow,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.5.sp
                          ),
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                      }
                    }

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(text = "Montant TTC :", style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray))
                      Text(text = "${df.format(invoice.totalTtcDzd.toInt())} DA", style = MaterialTheme.typography.titleMedium.copy(color = MechanicalOrange, fontWeight = FontWeight.Black))
                    }

                    Divider(color = MetalBorder)

                    // PRINT & DOWNLOAD ACTION BUTTONS
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Button(
                        onClick = { showPrintPreviewInvoice = invoice },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed, contentColor = PureWhite),
                        modifier = Modifier
                          .weight(1f)
                          .testTag("btn_print_invoice_${invoice.invoiceNumber}")
                      ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "IMPRIMER REÇU", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                      }

                      OutlinedButton(
                        onClick = {
                          downloadToastMessage = "Facture ${invoice.invoiceNumber}.pdf téléchargée avec succès !"
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, TechCyan),
                        modifier = Modifier.weight(1f)
                      ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "PDF", color = TechCyan, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
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

    // Modal QR Code d'accès client
    if (showQrCodeModal) {
      Dialog(onDismissRequest = { showQrCodeModal = false }) {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = GraphiteDark),
          border = BorderStroke(2.dp, PerformanceRedGlow),
          modifier = Modifier.fillMaxWidth(0.92f)
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Text(text = "BADGE QR CODE ACCÈS CLIENT", style = MaterialTheme.typography.titleMedium.copy(color = PureWhite, fontWeight = FontWeight.Black))
            Text(text = "À scanner à la réception de l'atelier pour charger votre dossier", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray))

            // Simulated High Fidelity QR Code Box
            Box(
              modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PureWhite)
                .padding(14.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Box(modifier = Modifier.size(36.dp).background(GraphiteBlack))
                  Box(modifier = Modifier.size(36.dp).background(GraphiteBlack))
                }
                Text(
                  text = account.plateNumber,
                  style = MaterialTheme.typography.labelSmall.copy(color = GraphiteBlack, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Box(modifier = Modifier.size(36.dp).background(GraphiteBlack))
                  Box(modifier = Modifier.size(24.dp).background(PerformanceRed))
                }
              }
            }

            Text(
              text = "Matricule : ${account.plateNumber}\nCode PIN : ${account.passwordPin}",
              textAlign = TextAlign.Center,
              style = MaterialTheme.typography.bodyMedium.copy(color = TechCyan, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            )

            MekanikPrimaryButton(
              text = "FERMER LE QR CODE",
              onClick = { showQrCodeModal = false },
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }
    }

    // Modal d'impression de Facture / Reçu Fiscal
    showPrintPreviewInvoice?.let { invoice ->
      Dialog(
        onDismissRequest = { showPrintPreviewInvoice = null },
        properties = DialogProperties(usePlatformDefaultWidth = false)
      ) {
        Surface(
          modifier = Modifier
            .fillMaxSize()
            .background(GraphiteBlack)
            .padding(16.dp),
          shape = RoundedCornerShape(16.dp),
          color = PureWhite
        ) {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .verticalScroll(rememberScrollState())
              .padding(18.dp)
          ) {
            // Header Fiscal
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Column {
                Text(text = invoice.garageName, style = MaterialTheme.typography.titleMedium.copy(color = GraphiteBlack, fontWeight = FontWeight.Black))
                Text(text = invoice.garageAddress, style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray, fontSize = 10.sp))
                Text(text = "NIF: ${invoice.garageNif} • NIS: ${invoice.garageNis}", style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray, fontSize = 9.sp))
              }
              Surface(shape = RoundedCornerShape(4.dp), color = PerformanceRed) {
                Text(text = "REÇU OFFICIEL", color = PureWhite, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = Color.LightGray)
            Spacer(modifier = Modifier.height(14.dp))

            // Client & Vehicle Info
            Text(text = "CLIENT : ${invoice.clientName} (${invoice.clientPhone})", style = MaterialTheme.typography.bodyMedium.copy(color = GraphiteBlack, fontWeight = FontWeight.Bold))
            Text(text = "VÉHICULE : ${invoice.vehicleModel} • MATRICULE : ${invoice.plateNumber}", style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray))
            Text(text = "FACTURE N° : ${invoice.invoiceNumber} • DATE : ${invoice.date}", style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray))

            Spacer(modifier = Modifier.height(14.dp))

            // Table of items
            Text(text = "DÉSIGNATION DES PRESTATIONS & PIÈCES", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(6.dp))

            invoice.items.forEach { item ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(text = item.description, style = MaterialTheme.typography.bodySmall.copy(color = GraphiteBlack, fontWeight = FontWeight.Bold))
                  Text(text = "Réf: ${item.reference} • Qte: ${item.quantity}", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 10.sp))
                }
                Text(text = "${df.format(item.totalLineDzd.toInt())} DA", style = MaterialTheme.typography.bodySmall.copy(color = GraphiteBlack, fontWeight = FontWeight.Bold))
              }
              Divider(color = Color(0xFFEEEEEE))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Totals
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text(text = "Total HT :", style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray))
              Text(text = "${df.format(invoice.subTotalDzd.toInt())} DA", style = MaterialTheme.typography.bodySmall.copy(color = GraphiteBlack, fontWeight = FontWeight.Bold))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text(text = "TVA (19%) :", style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray))
              Text(text = "${df.format(invoice.tvaAmountDzd.toInt())} DA", style = MaterialTheme.typography.bodySmall.copy(color = GraphiteBlack))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text(text = "Timbre Fiscal :", style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray))
              Text(text = "${df.format(invoice.stampTaxDzd.toInt())} DA", style = MaterialTheme.typography.bodySmall.copy(color = GraphiteBlack))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text(text = "NET À PAYER TTC :", style = MaterialTheme.typography.titleMedium.copy(color = PerformanceRed, fontWeight = FontWeight.Black))
              Text(text = "${df.format(invoice.totalTtcDzd.toInt())} DA", style = MaterialTheme.typography.titleMedium.copy(color = PerformanceRed, fontWeight = FontWeight.Black))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Print action button
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              Button(
                onClick = {
                  downloadToastMessage = "Impression du reçu fiscal envoyée à l'imprimante !"
                  showPrintPreviewInvoice = null
                },
                colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
              ) {
                Icon(Icons.Default.Print, contentDescription = null, tint = PureWhite, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("LANCER L'IMPRESSION", color = PureWhite, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
              }

              OutlinedButton(
                onClick = { showPrintPreviewInvoice = null },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(0.7f)
              ) {
                Text("FERMER", color = Color.DarkGray)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun RepairProgressStepper(currentStatus: RepairStatus) {
  val steps = listOf(
    RepairStatus.DIAGNOSTIC,
    RepairStatus.PIECES_COMMANDEES,
    RepairStatus.EN_COURS,
    RepairStatus.TERMINE
  )
  val currentIndex = steps.indexOf(currentStatus).coerceAtLeast(0)

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    steps.forEachIndexed { index, status ->
      val isReached = index <= currentIndex
      val isCurrent = index == currentIndex

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(if (isReached) PerformanceRed else MetalCard)
            .border(1.dp, if (isCurrent) PerformanceRedGlow else MetalBorder, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          if (isReached) {
            Icon(Icons.Default.Check, contentDescription = null, tint = PureWhite, modifier = Modifier.size(14.dp))
          } else {
            Text(text = "${index + 1}", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 10.sp))
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = status.label.take(9),
          style = MaterialTheme.typography.labelSmall.copy(
            color = if (isReached) PureWhite else AluminumMuted,
            fontSize = 9.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
          ),
          textAlign = TextAlign.Center
        )
      }
    }
  }
}
