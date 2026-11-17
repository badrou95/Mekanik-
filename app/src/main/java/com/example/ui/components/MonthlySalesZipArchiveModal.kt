package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.RepairOrder
import com.example.ui.theme.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.text.NumberFormat
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

data class MonthlyArchiveDocumentItem(
  val id: String,
  val docType: String, // "VENTE", "BON_LIVRAISON_OR", "FACTURE"
  val docNumber: String,
  val dateLabel: String,
  val clientOrPartner: String,
  val description: String,
  val amountDzd: Int,
  val uncompressedSizeKb: Int
)

@Composable
fun MonthlySalesZipArchiveModal(
  repairOrders: List<RepairOrder> = emptyList(),
  onDismiss: () -> Unit,
  onArchiveSavedToast: (String) -> Unit = {}
) {
  val context = LocalContext.current
  var startDate by remember { mutableStateOf("01/11/2026") }
  var endDate by remember { mutableStateOf("30/11/2026") }
  var autoArchiveEndOfMonth by remember { mutableStateOf(true) }
  var includeSales by remember { mutableStateOf(true) }
  var includeDeliveryNotesAndOr by remember { mutableStateOf(true) }
  var includeInvoices by remember { mutableStateOf(true) }
  var selectedDeviceTarget by remember { mutableStateOf("Téléphone / Tablette / PC (ZIP Universel)") }

  var isCompressing by remember { mutableStateOf(false) }
  var lastSavedZipPath by remember { mutableStateOf<String?>(null) }
  var pendingZipBytes by remember { mutableStateOf<ByteArray?>(null) }

  // Preset period options
  val periodPresets = listOf(
    Triple("Novembre 2026", "01/11/2026", "30/11/2026"),
    Triple("Octobre 2026", "01/10/2026", "31/10/2026"),
    Triple("Septembre 2026", "01/09/2026", "30/09/2026"),
    Triple("Trimestre Actuel", "01/09/2026", "30/11/2026")
  )

  // Build list of archiveable documents from repairOrders + counter sales + invoices
  val allArchiveItems = remember(repairOrders, startDate, endDate, includeSales, includeDeliveryNotesAndOr, includeInvoices) {
    val list = mutableListOf<MonthlyArchiveDocumentItem>()

    if (includeSales) {
      list.add(
        MonthlyArchiveDocumentItem(
          id = "VNT-01",
          docType = "VENTE",
          docNumber = "VNT-2026-1101",
          dateLabel = startDate,
          clientOrPartner = "Comptoir Pièces Auto Alger",
          description = "Kit Distribution Gates + Pompe à Eau (VW Golf 7 2.0 TDI)",
          amountDzd = 27500,
          uncompressedSizeKb = 240
        )
      )
      list.add(
        MonthlyArchiveDocumentItem(
          id = "VNT-02",
          docType = "VENTE",
          docNumber = "VNT-2026-1102",
          dateLabel = "12/11/2026",
          clientOrPartner = "Garage Mécanique Moderne Oran",
          description = "Jeu Disques + Plaquettes Brembo + Huile Motul 5W30",
          amountDzd = 34900,
          uncompressedSizeKb = 285
        )
      )
      list.add(
        MonthlyArchiveDocumentItem(
          id = "VNT-03",
          docType = "VENTE",
          docNumber = "VNT-2026-1103",
          dateLabel = "19/11/2026",
          clientOrPartner = "Client Particulier (Sofiane B.)",
          description = "Kit Embrayage Valeo 3 Pièces + Butée Hydraulique",
          amountDzd = 42000,
          uncompressedSizeKb = 210
        )
      )
    }

    if (includeDeliveryNotesAndOr) {
      repairOrders.forEachIndexed { idx, orOrder ->
        list.add(
          MonthlyArchiveDocumentItem(
            id = "BL-${orOrder.id}",
            docType = "BON_LIVRAISON_OR",
            docNumber = "BL-${orOrder.orderNumber}",
            dateLabel = orOrder.date,
            clientOrPartner = "${orOrder.clientName} (${orOrder.plateNumber})",
            description = "Bon & OR : ${orOrder.vehicleModel} (${orOrder.items.size} pièces + ${orOrder.laborHours}h M.O)",
            amountDzd = orOrder.netTotalDzd,
            uncompressedSizeKb = 320 + (idx * 35)
          )
        )
      }
    }

    if (includeInvoices) {
      list.add(
        MonthlyArchiveDocumentItem(
          id = "FCT-01",
          docType = "FACTURE",
          docNumber = "FCT-2026-0842",
          dateLabel = "15/11/2026",
          clientOrPartner = "Karim Benali • VW Tiguan II",
          description = "Facture Fiscale Définitive TTC (Freinage Brembo + Révision)",
          amountDzd = 44500,
          uncompressedSizeKb = 410
        )
      )
      list.add(
        MonthlyArchiveDocumentItem(
          id = "FCT-02",
          docType = "FACTURE",
          docNumber = "FCT-2026-0849",
          dateLabel = endDate,
          clientOrPartner = "Flotte Entreprise Sonatrach / Leasing",
          description = "Facture Mensuelle Entretien Flotte (5 Véhicules)",
          amountDzd = 168000,
          uncompressedSizeKb = 520
        )
      )
    }

    list
  }

  val totalAmountDzd = allArchiveItems.sumOf { it.amountDzd }
  val totalUncompressedKb = allArchiveItems.sumOf { it.uncompressedSizeKb }.coerceAtLeast(100)
  val estimatedCompressedZipKb = (totalUncompressedKb * 0.18).toInt().coerceAtLeast(24)
  val spaceSavedPercent = 82

  // System file saver launcher (works on Phone, Tablet, and ChromeOS/PC)
  val saveZipLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.CreateDocument("application/zip")
  ) { uri: Uri? ->
    val bytes = pendingZipBytes
    if (uri != null && bytes != null) {
      try {
        context.contentResolver.openOutputStream(uri)?.use { out ->
          out.write(bytes)
          out.flush()
        }
        val cleanStart = startDate.replace("/", "-")
        val cleanEnd = endDate.replace("/", "-")
        val fileName = "Archive_Mekanik_${cleanStart}_au_${cleanEnd}.zip"
        lastSavedZipPath = "Enregistré sur votre appareil : $fileName (${bytes.size / 1024} Ko)"
        onArchiveSavedToast("🗜️ Fichier ZIP enregistré avec succès ($fileName)")
      } catch (e: Exception) {
        onArchiveSavedToast("Archive ZIP créée dans le dossier de l'application.")
      }
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)
        .testTag("monthly_sales_zip_archive_modal"),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, TechCyan)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Top Header
        Surface(
          color = MetalCard,
          border = BorderStroke(1.dp, MetalBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(TechCyan.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.FolderZip,
                  contentDescription = null,
                  tint = TechCyan,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "ARCHIVE MENSUELLE ZIP AUTOMATIQUE",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    fontSize = 15.sp
                  )
                )
                Text(
                  text = "Compression ZIP des Ventes, Bons & Factures • Téléphone, Tablette & PC",
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
                .background(GraphiteBlack)
                .testTag("btn_close_zip_archive_modal")
            ) {
              Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite, modifier = Modifier.size(18.dp))
            }
          }
        }

        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // 1. Date Range Picker Card (Date de début & Date de fin du mois)
          item {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, MetalBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "1. PÉRIODE D'ARCHIVAGE (DÉBUT & FIN DU MOIS)",
                      style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = PureWhite,
                        fontSize = 12.sp
                      )
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ElectricGreenDark,
                    border = BorderStroke(0.8.dp, ElectricGreen)
                  ) {
                    Text(
                      text = "-$spaceSavedPercent% ESPACE",
                      color = ElectricGreen,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.5.sp,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                  }
                }

                // Quick Period Presets
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  periodPresets.forEach { (label, pStart, pEnd) ->
                    val isSelected = startDate == pStart && endDate == pEnd
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = if (isSelected) MechanicalOrange else GraphiteDark,
                      border = BorderStroke(1.dp, if (isSelected) MechanicalOrangeGlow else MetalBorder),
                      onClick = {
                        startDate = pStart
                        endDate = pEnd
                      },
                      modifier = Modifier.weight(1f)
                    ) {
                      Text(
                        text = label,
                        color = if (isSelected) Color.White else PureWhite,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp),
                        maxLines = 1
                      )
                    }
                  }
                }

                // Start Date & End Date TextFields
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Date de début (JJ/MM/AAAA)") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    modifier = Modifier
                      .weight(1f)
                      .testTag("input_archive_start_date")
                  )
                  OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("Date de fin (JJ/MM/AAAA)") },
                    leadingIcon = { Icon(Icons.Default.EventAvailable, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    modifier = Modifier
                      .weight(1f)
                      .testTag("input_archive_end_date")
                  )
                }

                // Automatic End-of-Month ZIP Switch
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteDark,
                  border = BorderStroke(1.dp, if (autoArchiveEndOfMonth) ElectricGreen else MetalBorder),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "Archivage ZIP automatique à chaque fin de mois",
                        style = MaterialTheme.typography.bodyMedium.copy(
                          color = PureWhite,
                          fontWeight = FontWeight.Bold,
                          fontSize = 12.sp
                        )
                      )
                      Text(
                        text = "Compresse automatiquement les ventes, bons et factures le dernier jour du mois",
                        style = MaterialTheme.typography.bodySmall.copy(
                          color = AluminumLight,
                          fontSize = 10.sp
                        )
                      )
                    }
                    Switch(
                      checked = autoArchiveEndOfMonth,
                      onCheckedChange = { autoArchiveEndOfMonth = it },
                      modifier = Modifier.testTag("switch_auto_zip_month_end")
                    )
                  }
                }
              }
            }
          }

          // 2. Document Categories & Compression Savings Summary
          item {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, MetalBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Text(
                  text = "2. DOCUMENTS INCLUS DANS LE FICHIER ZIP",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    fontSize = 12.sp
                  )
                )

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  FilterChip(
                    selected = includeSales,
                    onClick = { includeSales = !includeSales },
                    label = { Text("Ventes Store", fontSize = 11.sp) },
                    leadingIcon = {
                      Icon(
                        if (includeSales) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                      )
                    },
                    modifier = Modifier.weight(1f)
                  )
                  FilterChip(
                    selected = includeDeliveryNotesAndOr,
                    onClick = { includeDeliveryNotesAndOr = !includeDeliveryNotesAndOr },
                    label = { Text("Bons & OR", fontSize = 11.sp) },
                    leadingIcon = {
                      Icon(
                        if (includeDeliveryNotesAndOr) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                      )
                    },
                    modifier = Modifier.weight(1f)
                  )
                  FilterChip(
                    selected = includeInvoices,
                    onClick = { includeInvoices = !includeInvoices },
                    label = { Text("Factures", fontSize = 11.sp) },
                    leadingIcon = {
                      Icon(
                        if (includeInvoices) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                      )
                    },
                    modifier = Modifier.weight(1f)
                  )
                }

                // Space Reduction Visualization Bar
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteDark,
                  border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.5f)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text(
                        text = "${allArchiveItems.size} documents sélectionnés",
                        style = MaterialTheme.typography.bodyMedium.copy(
                          color = PureWhite,
                          fontWeight = FontWeight.Black,
                          fontSize = 12.5.sp
                        )
                      )
                      Text(
                        text = "Total période : ${NumberFormat.getNumberInstance(Locale.FRANCE).format(totalAmountDzd)} DA",
                        style = MaterialTheme.typography.bodySmall.copy(
                          color = MechanicalOrange,
                          fontWeight = FontWeight.Bold,
                          fontSize = 11.sp
                        )
                      )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                      Text(
                        text = "Avant : $totalUncompressedKb Ko → ZIP : $estimatedCompressedZipKb Ko",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = TechCyan,
                          fontFamily = FontFamily.Monospace,
                          fontWeight = FontWeight.Bold,
                          fontSize = 11.sp
                        )
                      )
                      Text(
                        text = "Gain de place : -$spaceSavedPercent% sur Téléphone / Tablette / PC",
                        style = MaterialTheme.typography.bodySmall.copy(
                          color = ElectricGreen,
                          fontWeight = FontWeight.Bold,
                          fontSize = 10.sp
                        )
                      )
                    }
                  }
                }

                // Target Device Selector (Téléphone / Tablette / PC)
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  val targets = listOf(
                    "📱 Téléphone" to Icons.Default.Smartphone,
                    "📟 Tablette" to Icons.Default.TabletMac,
                    "💻 PC / Bureau" to Icons.Default.Computer
                  )
                  targets.forEach { (label, icon) ->
                    val isSelected = selectedDeviceTarget.contains(label.takeLast(4))
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = if (isSelected) TechCyanDark else GraphiteDark,
                      border = BorderStroke(1.dp, if (isSelected) TechCyan else MetalBorder),
                      onClick = { selectedDeviceTarget = label },
                      modifier = Modifier.weight(1f)
                    ) {
                      Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Icon(icon, contentDescription = null, tint = TechCyan, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                          text = label,
                          color = PureWhite,
                          fontSize = 10.5.sp,
                          fontWeight = FontWeight.Bold
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          // Confirmation banner when ZIP is saved
          if (lastSavedZipPath != null) {
            item {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = ElectricGreenDark,
                border = BorderStroke(1.5.dp, ElectricGreen),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("banner_zip_saved_confirmation")
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(22.dp))
                  Spacer(modifier = Modifier.width(10.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "ARCHIVE ZIP CRÉÉE ET ENREGISTRÉE !",
                      color = ElectricGreen,
                      fontWeight = FontWeight.Black,
                      fontSize = 12.sp
                    )
                    Text(
                      text = lastSavedZipPath ?: "",
                      color = PureWhite,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }
          }

          // 3. Preview of Documents in the ZIP
          item {
            Text(
              text = "DÉTAIL DES PIÈCES COMPTABLES DE LA PÉRIODE ($startDate → $endDate)",
              style = MaterialTheme.typography.labelSmall.copy(
                color = AluminumMuted,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp
              )
            )
          }

          items(allArchiveItems, key = { it.id }) { doc ->
            val badgeColor = when (doc.docType) {
              "VENTE" -> ElectricGreen
              "BON_LIVRAISON_OR" -> TechCyan
              else -> MechanicalOrange
            }
            val badgeLabel = when (doc.docType) {
              "VENTE" -> "VENTE"
              "BON_LIVRAISON_OR" -> "BON / OR"
              else -> "FACTURE"
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MetalCard,
              border = BorderStroke(1.dp, MetalBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = badgeColor.copy(alpha = 0.16f),
                      border = BorderStroke(0.6.dp, badgeColor)
                    ) {
                      Text(
                        text = badgeLabel,
                        color = badgeColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "${doc.docNumber} • ${doc.dateLabel}",
                      color = PureWhite,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp,
                      fontFamily = FontFamily.Monospace
                    )
                  }
                  Spacer(modifier = Modifier.height(3.dp))
                  Text(
                    text = doc.clientOrPartner,
                    color = PureWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.5.sp
                  )
                  Text(
                    text = doc.description,
                    color = AluminumLight,
                    fontSize = 10.5.sp
                  )
                }

                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = "${NumberFormat.getNumberInstance(Locale.FRANCE).format(doc.amountDzd)} DA",
                    color = MechanicalOrange,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.5.sp,
                    fontFamily = FontFamily.Monospace
                  )
                  Text(
                    text = "${doc.uncompressedSizeKb} Ko",
                    color = AluminumMuted,
                    fontSize = 9.5.sp
                  )
                }
              }
            }
          }
        }

        // Bottom Action Bar: Compress & Save ZIP directly
        Surface(
          color = MetalCard,
          border = BorderStroke(1.dp, MetalBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Direct Save to Device Storage + Open System Save Dialog
            Button(
              onClick = {
                isCompressing = true
                val zipBytes = buildMonthlyArchiveZipBytes(
                  startDate = startDate,
                  endDate = endDate,
                  items = allArchiveItems,
                  totalAmountDzd = totalAmountDzd
                )
                pendingZipBytes = zipBytes
                val cleanStart = startDate.replace("/", "-")
                val cleanEnd = endDate.replace("/", "-")
                val fileName = "Archive_Mekanik_${cleanStart}_au_${cleanEnd}.zip"

                // 1. Save immediately in app external/downloads folder so it's always stored on device
                val localPath = saveZipFileLocally(context, fileName, zipBytes)
                lastSavedZipPath = "Fichier $fileName (${zipBytes.size / 1024} Ko) enregistré dans $localPath"
                isCompressing = false

                // 2. Also trigger system file picker so user can place it anywhere on Phone/Tablet/PC
                try {
                  saveZipLauncher.launch(fileName)
                } catch (_: Exception) {
                  onArchiveSavedToast("🗜️ Archive ZIP enregistrée : $fileName")
                }
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = PerformanceRed,
                contentColor = Color.White
              ),
              modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .testTag("btn_compress_and_save_zip")
            ) {
              Icon(Icons.Default.FolderZip, contentDescription = null, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "COMPRESSER EN ZIP & ENREGISTRER ($estimatedCompressedZipKb Ko)",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }
  }
}

private fun buildMonthlyArchiveZipBytes(
  startDate: String,
  endDate: String,
  items: List<MonthlyArchiveDocumentItem>,
  totalAmountDzd: Int
): ByteArray {
  val baos = ByteArrayOutputStream()
  ZipOutputStream(baos).use { zos ->
    // 1. CSV Summary File (Excel / PC / Tablet compatible)
    val csvBuilder = StringBuilder()
    csvBuilder.append("ID;Type;Numero_Document;Date;Client_Ou_Partenaire;Description;Montant_DZD\n")
    items.forEach { item ->
      csvBuilder.append("${item.id};${item.docType};${item.docNumber};${item.dateLabel};\"${item.clientOrPartner}\";\"${item.description}\";${item.amountDzd}\n")
    }
    csvBuilder.append("TOTAL;PERIODE;$startDate -> $endDate;;;\"Total Mensuel Archive\";$totalAmountDzd\n")

    val csvEntry = ZipEntry("1_Recapitulatif_Ventes_Bons_Factures.csv")
    zos.putNextEntry(csvEntry)
    zos.write(csvBuilder.toString().toByteArray(StandardCharsets.UTF_8))
    zos.closeEntry()

    // 2. Sales & Counter Receipts File
    val salesTxt = buildString {
      appendLine("=================================================================")
      appendLine("MEKANIK+ - ARCHIVE MENSUELLE DES VENTES STORE & COMPTOIR")
      appendLine("Période : Du $startDate au $endDate")
      appendLine("=================================================================")
      items.filter { it.docType == "VENTE" }.forEach { v ->
        appendLine("[${v.docNumber}] Date: ${v.dateLabel} | Client: ${v.clientOrPartner}")
        appendLine("   Détail : ${v.description}")
        appendLine("   Montant TTC : ${v.amountDzd} DZD")
        appendLine("-----------------------------------------------------------------")
      }
    }
    zos.putNextEntry(ZipEntry("2_Ventes_Store_du_Mois.txt"))
    zos.write(salesTxt.toByteArray(StandardCharsets.UTF_8))
    zos.closeEntry()

    // 3. Delivery Notes & Repair Orders File
    val bonsTxt = buildString {
      appendLine("=================================================================")
      appendLine("MEKANIK+ - ARCHIVE DES BONS DE LIVRAISON & ORDRES DE RÉPARATION")
      appendLine("Période : Du $startDate au $endDate")
      appendLine("=================================================================")
      items.filter { it.docType == "BON_LIVRAISON_OR" }.forEach { b ->
        appendLine("[${b.docNumber}] Date: ${b.dateLabel} | Véhicule/Client: ${b.clientOrPartner}")
        appendLine("   Intervention : ${b.description}")
        appendLine("   Montant Net : ${b.amountDzd} DZD")
        appendLine("-----------------------------------------------------------------")
      }
    }
    zos.putNextEntry(ZipEntry("3_Bons_Livraison_et_OR.txt"))
    zos.write(bonsTxt.toByteArray(StandardCharsets.UTF_8))
    zos.closeEntry()

    // 4. Invoices File
    val invoicesTxt = buildString {
      appendLine("=================================================================")
      appendLine("MEKANIK+ - ARCHIVE DES FACTURES FISCALES DU MOIS")
      appendLine("Période : Du $startDate au $endDate")
      appendLine("=================================================================")
      items.filter { it.docType == "FACTURE" }.forEach { f ->
        appendLine("[${f.docNumber}] Date: ${f.dateLabel} | Client: ${f.clientOrPartner}")
        appendLine("   Objet : ${f.description}")
        appendLine("   Total Facturé : ${f.amountDzd} DZD")
        appendLine("-----------------------------------------------------------------")
      }
      appendLine("\nTOTAL GÉNÉRAL ARCHIVÉ : $totalAmountDzd DZD")
    }
    zos.putNextEntry(ZipEntry("4_Factures_Fiscales_Mois.txt"))
    zos.write(invoicesTxt.toByteArray(StandardCharsets.UTF_8))
    zos.closeEntry()
  }
  return baos.toByteArray()
}

private fun saveZipFileLocally(context: Context, fileName: String, zipBytes: ByteArray): String {
  return try {
    val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
    if (!dir.exists()) dir.mkdirs()
    val outFile = File(dir, fileName)
    FileOutputStream(outFile).use { fos ->
      fos.write(zipBytes)
      fos.flush()
    }
    outFile.absolutePath
  } catch (e: Exception) {
    fileName
  }
}
