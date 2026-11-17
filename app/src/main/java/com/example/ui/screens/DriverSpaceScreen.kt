package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.components.MekanikPrimaryButton
import com.example.ui.components.MekanikSecondaryButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

enum class DriverTab(val label: String, val icon: ImageVector) {
  CARNET_HISTORIQUE("Carnet & Historique", Icons.Default.MenuBook),
  FACTURES_BONS("Bons & Factures", Icons.Default.ReceiptLong),
  METHODES_DIY("Guides & Méthodes DIY", Icons.Default.BuildCircle)
}

@Composable
fun DriverSpaceScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  onBack: () -> Unit,
  onNavigateToParts: () -> Unit,
  onNavigateToAi: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableStateOf(DriverTab.CARNET_HISTORIQUE) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("driver_space_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Header with back button & Driver identity
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
              text = "ESPACE CONDUCTEUR",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite
              )
            )
            Text(
              text = "Carnet d'entretien digital & Guides pratiques",
              style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.sp)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = PerformanceRedDark.copy(alpha = 0.4f),
          border = BorderStroke(1.dp, PerformanceRed)
        ) {
          Text(
            text = "PARTICULIER",
            style = MaterialTheme.typography.labelSmall.copy(
              color = PureWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }

    // 2. Driver Active Vehicle Health & Summary Card
    item {
      val vehicle = uiState.driverVehicle
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
            verticalAlignment = Alignment.Top
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = vehicle.brandModel,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = PureWhite
                )
              )
              Text(
                text = "Immat : ${vehicle.plateNumber} • ${vehicle.engine}",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.5.sp)
              )
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = ElectricGreen.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, ElectricGreen)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  Icons.Default.HealthAndSafety,
                  contentDescription = null,
                  tint = ElectricGreen,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Santé : ${vehicle.healthScorePct}%",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = ElectricGreen,
                    fontWeight = FontWeight.Bold
                  )
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          Divider(color = MetalBorder)
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            DriverVehicleStatMini(
              label = "Kilométrage",
              value = "${vehicle.currentMileageKm} km",
              color = TechCyan
            )
            DriverVehicleStatMini(
              label = "Prochaine Vidange",
              value = "${vehicle.nextServiceDueKm} km",
              color = WarningAmber
            )
            DriverVehicleStatMini(
              label = "Contrôle Tech.",
              value = vehicle.technicalInspectionDue,
              color = AluminumLight
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Driver QR Code Passport Quick Share Button
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = GraphiteBlack,
            border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.5f)),
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                val found = uiState.universalQrPassports.firstOrNull { it.registeredByRole == AccountRole.CONDUCTEUR }
                  ?: uiState.universalQrPassports.first()
                viewModel.selectQrPassport(found, 0)
              }
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(34.dp)
                  .clip(RoundedCornerShape(6.dp))
                  .background(TechCyan.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.QrCode2, contentDescription = null, tint = TechCyan, modifier = Modifier.size(22.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "MON PASSEPORT QR VÉHICULE",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = PureWhite,
                    fontWeight = FontWeight.Bold
                  )
                )
                Text(
                  text = "Présenter au mécanicien pour devis ou au vendeur pour achat de pièces",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 10.5.sp)
                )
              }
              Icon(Icons.Default.OpenInNew, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Driver Engine Sound Sonar AI Recording Button
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = GraphiteBlack,
            border = BorderStroke(1.dp, PerformanceRedGlow.copy(alpha = 0.6f)),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.openEngineSoundRecorder() }
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(34.dp)
                  .clip(RoundedCornerShape(6.dp))
                  .background(PerformanceRedDark.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "ENREGISTRER UN BRUIT SUSPECT DU MOTEUR",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = PureWhite,
                    fontWeight = FontWeight.Bold
                  )
                )
                Text(
                  text = "L'IA analyse le son de votre moteur pour détecter une panne",
                  style = MaterialTheme.typography.bodySmall.copy(color = PerformanceRedGlow, fontSize = 10.5.sp)
                )
              }
              Icon(Icons.Default.Mic, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(18.dp))
            }
          }
        }
      }
    }

    // 3. Tab Navigation Chips
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        DriverTab.values().forEach { tab ->
          val isSelected = selectedTab == tab
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) PerformanceRed else MetalCard,
            border = BorderStroke(
              1.dp,
              if (isSelected) PerformanceRedGlow else MetalBorder
            ),
            modifier = Modifier
              .weight(1f)
              .clickable { selectedTab = tab }
          ) {
            Row(
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                tab.icon,
                contentDescription = null,
                tint = if (isSelected) PureWhite else AluminumLight,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = tab.label,
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (isSelected) PureWhite else AluminumLight,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }
      }
    }

    // 4. Tab Contents
    when (selectedTab) {
      DriverTab.CARNET_HISTORIQUE -> {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "HISTORIQUE DES RÉPARATIONS & ENTRETIENS",
              style = MaterialTheme.typography.labelMedium.copy(
                color = AluminumMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            )
            Text(
              text = "${uiState.driverRepairHistory.size} Interventions",
              style = MaterialTheme.typography.labelSmall.copy(
                color = TechCyan,
                fontWeight = FontWeight.Bold
              )
            )
          }
        }

        items(uiState.driverRepairHistory) { record ->
          DriverRepairHistoryCard(
            record = record,
            onViewInvoice = {
              val inv = uiState.driverInvoices.find { it.invoiceNumber == record.invoiceNumber }
              if (inv != null) {
                viewModel.openDriverInvoiceDetails(inv)
              } else {
                viewModel.showToastNotification("Facture ${record.invoiceNumber} disponible.")
              }
            }
          )
        }
      }

      DriverTab.FACTURES_BONS -> {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "BONS DE TRAVAUX & FACTURES FISCALES",
              style = MaterialTheme.typography.labelMedium.copy(
                color = AluminumMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            )
            Text(
              text = "${uiState.driverInvoices.size} Documents",
              style = MaterialTheme.typography.labelSmall.copy(
                color = TechCyan,
                fontWeight = FontWeight.Bold
              )
            )
          }
        }

        items(uiState.driverInvoices) { invoice ->
          DriverInvoiceCard(
            invoice = invoice,
            onOpenDetails = { viewModel.openDriverInvoiceDetails(invoice) },
            onDownloadPdf = { viewModel.generateAndDownloadDriverInvoicePdf(context, invoice) }
          )
        }
      }

      DriverTab.METHODES_DIY -> {
        item {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = "MÉTHODES & GUIDES D'AUTO-ENTRETIEN",
              style = MaterialTheme.typography.labelMedium.copy(
                color = AluminumMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            )
            Text(
              text = "Tutoriels pas-à-pas recommandés pour entretenir votre véhicule en toute sécurité et économiser sur les petites interventions.",
              style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.5.sp)
            )
          }
        }

        // Category Filter Chips for Guides
        item {
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            item {
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (uiState.selectedDriverGuideCategory == null) PerformanceRed else MetalCard,
                border = BorderStroke(
                  1.dp,
                  if (uiState.selectedDriverGuideCategory == null) PerformanceRedGlow else MetalBorder
                ),
                modifier = Modifier.clickable { viewModel.filterDriverGuideCategory(null) }
              ) {
                Text(
                  text = "Tous les Guides (${uiState.driverMaintenanceMethods.size})",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (uiState.selectedDriverGuideCategory == null) PureWhite else AluminumLight,
                    fontWeight = FontWeight.Bold
                  ),
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
              }
            }

            items(DriverGuideCategory.values()) { cat ->
              val isSelected = uiState.selectedDriverGuideCategory == cat
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) PerformanceRed else MetalCard,
                border = BorderStroke(
                  1.dp,
                  if (isSelected) PerformanceRedGlow else MetalBorder
                ),
                modifier = Modifier.clickable { viewModel.filterDriverGuideCategory(cat) }
              ) {
                Text(
                  text = cat.label,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) PureWhite else AluminumLight,
                    fontWeight = FontWeight.Bold
                  ),
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
              }
            }
          }
        }

        val filteredMethods = if (uiState.selectedDriverGuideCategory == null) {
          uiState.driverMaintenanceMethods
        } else {
          uiState.driverMaintenanceMethods.filter { it.category == uiState.selectedDriverGuideCategory }
        }

        items(filteredMethods) { method ->
          DriverMethodCard(
            method = method,
            onOpenGuide = { viewModel.openDriverMethodDetails(method) },
            onFindParts = {
              viewModel.onSearchQueryChanged(method.relatedPartKeyword)
              onNavigateToParts()
            }
          )
        }
      }
    }
  }

  // --- 5. MODAL DÉTAILS DE GUIDE DIY ---
  if (uiState.showDriverMethodModal && uiState.selectedDriverMethod != null) {
    DriverMethodDetailDialog(
      method = uiState.selectedDriverMethod!!,
      onDismiss = { viewModel.closeDriverMethodModal() },
      onFindCompatibleParts = {
        viewModel.closeDriverMethodModal()
        viewModel.onSearchQueryChanged(uiState.selectedDriverMethod!!.relatedPartKeyword)
        onNavigateToParts()
      },
      onAskMekanikAi = {
        viewModel.closeDriverMethodModal()
        viewModel.onAiQueryChanged("Comment faire : ${uiState.selectedDriverMethod!!.title}")
        onNavigateToAi()
      }
    )
  }

  // --- 6. MODAL DÉTAILS FACTURE CONDUCTEUR ---
  if (uiState.showDriverInvoiceModal && uiState.selectedDriverInvoice != null) {
    DriverInvoiceDetailDialog(
      invoice = uiState.selectedDriverInvoice!!,
      onDismiss = { viewModel.closeDriverInvoiceModal() },
      onDownloadPdf = {
        viewModel.generateAndDownloadDriverInvoicePdf(context, uiState.selectedDriverInvoice!!)
      }
    )
  }
}

@Composable
fun DriverVehicleStatMini(
  label: String,
  value: String,
  color: Color
) {
  Column {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 10.sp)
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.Bold,
        color = color,
        fontSize = 12.sp
      )
    )
  }
}

@Composable
fun DriverRepairHistoryCard(
  record: DriverRepairRecord,
  onViewInvoice: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = record.interventionType,
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = PureWhite
            )
          )
          Text(
            text = "${record.date} • ${record.garageName} (${record.garageWilaya})",
            style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.5.sp)
          )
        }

        if (record.isCertified) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = ElectricGreen.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, ElectricGreen)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Verified, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "Certifié",
                style = MaterialTheme.typography.labelSmall.copy(color = ElectricGreen, fontWeight = FontWeight.Bold, fontSize = 9.5.sp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = record.description,
        style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 12.sp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Replaced Parts Chips
      if (record.replacedParts.isNotEmpty()) {
        Text(
          text = "PIÈCES REMPLACÉES :",
          style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        record.replacedParts.take(3).forEach { part ->
          Row(
            modifier = Modifier.padding(vertical = 1.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TechCyan, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = part,
              style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 11.sp),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      Divider(color = MetalBorder)
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Speed, contentDescription = null, tint = AluminumMuted, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${record.odometerKm} km",
            style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${record.totalAmountDzd} DA",
            style = MaterialTheme.typography.titleSmall.copy(
              color = MechanicalOrange,
              fontWeight = FontWeight.Black,
              fontSize = 13.5.sp
            )
          )
          Spacer(modifier = Modifier.width(8.dp))
          OutlinedButton(
            onClick = onViewInvoice,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MetalBorder),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.height(30.dp)
          ) {
            Icon(Icons.Default.Receipt, contentDescription = null, tint = PureWhite, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Facture", style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontSize = 10.sp))
          }
        }
      }
    }
  }
}

@Composable
fun DriverInvoiceCard(
  invoice: DriverInvoiceItem,
  onOpenDetails: () -> Unit,
  onDownloadPdf: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
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
              .background(PerformanceRedDark.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(18.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = invoice.invoiceNumber,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PureWhite)
            )
            Text(
              text = "${invoice.date} • ${invoice.garageName}",
              style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = ElectricGreen.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, ElectricGreen)
        ) {
          Text(
            text = "Payée",
            style = MaterialTheme.typography.labelSmall.copy(color = ElectricGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Divider(color = MetalBorder)
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = "Règlement : ${invoice.paymentMethod}", style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp))
          Text(
            text = "Total : ${invoice.totalAmountDzd} DA",
            style = MaterialTheme.typography.titleMedium.copy(color = MechanicalOrange, fontWeight = FontWeight.Black)
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          IconButton(
            onClick = onDownloadPdf,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(MetalSurface)
          ) {
            Icon(Icons.Default.Download, contentDescription = "Télécharger PDF", tint = TechCyan, modifier = Modifier.size(16.dp))
          }

          Button(
            onClick = onOpenDetails,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Text("Détail", style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold))
          }
        }
      }
    }
  }
}

@Composable
fun DriverMethodCard(
  method: DriverMaintenanceMethod,
  onOpenGuide: () -> Unit,
  onFindParts: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, if (method.isRecommendedNow) PerformanceRed.copy(alpha = 0.6f) else MetalBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = TechCyan.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, TechCyan)
            ) {
              Text(
                text = method.category.label,
                style = MaterialTheme.typography.labelSmall.copy(color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 9.5.sp),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            if (method.isRecommendedNow) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = WarningAmber.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, WarningAmber)
              ) {
                Text(
                  text = "Recommandé maintenant",
                  style = MaterialTheme.typography.labelSmall.copy(color = WarningAmber, fontWeight = FontWeight.Bold, fontSize = 9.5.sp),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = method.title,
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = PureWhite
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Timer, contentDescription = null, tint = AluminumMuted, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "${method.durationMinutes} min", style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp))
          Spacer(modifier = Modifier.width(10.dp))
          Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = AluminumMuted, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = method.difficulty, style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp))
        }

        Text(
          text = "Économie : ~${method.estimatedSavingsDzd} DA",
          style = MaterialTheme.typography.bodySmall.copy(color = ElectricGreen, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))
      Divider(color = MetalBorder)
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${method.steps.size} Étapes pas-à-pas",
          style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 11.sp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(
            onClick = onFindParts,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, TechCyan),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.height(32.dp)
          ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = TechCyan, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Pièces", style = MaterialTheme.typography.labelSmall.copy(color = TechCyan, fontSize = 10.sp))
          }

          Button(
            onClick = onOpenGuide,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(32.dp)
          ) {
            Text("Voir le Guide", style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp))
          }
        }
      }
    }
  }
}

@Composable
fun DriverMethodDetailDialog(
  method: DriverMaintenanceMethod,
  onDismiss: () -> Unit,
  onFindCompatibleParts: () -> Unit,
  onAskMekanikAi: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.88f),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, MetalBorder)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Dialog Header
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = MetalCard,
          border = BorderStroke(0.5.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = method.category.label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(color = TechCyan, fontWeight = FontWeight.Bold)
              )
              Text(
                text = method.title,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = PureWhite
                )
              )
            }
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite)
            }
          }
        }

        // Scrollable Guide Body
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Info summary strip
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MetalSurface),
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("Durée", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted))
                  Text("${method.durationMinutes} min", style = MaterialTheme.typography.bodyMedium.copy(color = PureWhite, fontWeight = FontWeight.Bold))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("Difficulté", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted))
                  Text(method.difficulty, style = MaterialTheme.typography.bodyMedium.copy(color = TechCyan, fontWeight = FontWeight.Bold))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("Économie DA", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted))
                  Text("~${method.estimatedSavingsDzd} DA", style = MaterialTheme.typography.bodyMedium.copy(color = ElectricGreen, fontWeight = FontWeight.Bold))
                }
              }
            }
          }

          // Safety Alert Box
          if (method.safetyTips.isNotEmpty()) {
            item {
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PerformanceRedDark.copy(alpha = 0.25f)),
                border = BorderStroke(1.dp, PerformanceRed)
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "CONSIGNES DE SÉCURITÉ OBLIGATOIRES",
                      style = MaterialTheme.typography.labelSmall.copy(color = PerformanceRedGlow, fontWeight = FontWeight.Bold)
                    )
                  }
                  Spacer(modifier = Modifier.height(6.dp))
                  method.safetyTips.forEach { tip ->
                    Text(
                      text = "• $tip",
                      style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontSize = 11.5.sp)
                    )
                  }
                }
              }
            }
          }

          // Tools Required
          item {
            Text(
              text = "OUTILS & MATÉRIEL REQUIS",
              style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            method.toolsNeeded.forEach { tool ->
              Row(
                modifier = Modifier.padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Build, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = tool, style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 12.sp))
              }
            }
          }

          // Step-by-Step Instructions
          item {
            Text(
              text = "ÉTAPES D'INTERVENTION PAS-À-PAS",
              style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            )
          }

          items(method.steps) { step ->
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(24.dp)
                      .clip(CircleShape)
                      .background(PerformanceRed),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = "${step.stepNumber}",
                      style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold)
                    )
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = step.title,
                    style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold)
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = step.instruction,
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 12.sp)
                )

                if (step.cautionTip != null) {
                  Spacer(modifier = Modifier.height(6.dp))
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = WarningAmber.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, WarningAmber)
                  ) {
                    Row(
                      modifier = Modifier.padding(8.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(Icons.Default.Info, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = step.cautionTip,
                        style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontSize = 11.sp)
                      )
                    }
                  }
                }
              }
            }
          }
        }

        // Bottom Actions
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = MetalCard,
          border = BorderStroke(0.5.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = onAskMekanikAi,
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, TechCyan),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.Psychology, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Aide IA", color = TechCyan, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }

            Button(
              onClick = onFindCompatibleParts,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
              modifier = Modifier.weight(1.3f)
            ) {
              Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Trouver Pièces", color = PureWhite, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
          }
        }
      }
    }
  }
}

@Composable
fun DriverInvoiceDetailDialog(
  invoice: DriverInvoiceItem,
  onDismiss: () -> Unit,
  onDownloadPdf: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.82f),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, MetalBorder)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = MetalCard,
          border = BorderStroke(0.5.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "FACTURE D'ENTRETIEN OFFICIELLE",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = PureWhite)
              )
              Text(
                text = "N° ${invoice.invoiceNumber} • Reçu Client",
                style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.5.sp)
              )
            }
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite)
            }
          }
        }

        // Invoice Body
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MetalSurface),
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "Émetteur : ${invoice.garageName}", style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontWeight = FontWeight.Bold))
                Text(text = "Téléphone Atelier : ${invoice.garagePhone}", style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight))
                Text(text = "Véhicule : ${invoice.vehicleInfo}", style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight))
                Text(text = "Kilométrage relevé : ${invoice.mileageKm} km", style = MaterialTheme.typography.bodySmall.copy(color = TechCyan))
                Text(text = "Date d'émission : ${invoice.date}", style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight))
              }
            }
          }

          item {
            Text(
              text = "LIGNES DE FACTURATION DÉTAILLÉES",
              style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            )
          }

          items(invoice.itemsDetail) { item ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MetalCard,
              border = BorderStroke(1.dp, MetalBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = item,
                  style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontSize = 11.5.sp)
                )
              }
            }
          }

          // Total Calculation Card
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MetalSurface),
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Total Pièces HT :", style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight))
                  Text("${invoice.partsAmountDzd} DA", style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontWeight = FontWeight.Bold))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Main d'œuvre & Calage :", style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight))
                  Text("${invoice.laborAmountDzd} DA", style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontWeight = FontWeight.Bold))
                }
                Divider(color = MetalBorder)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("TOTAL NET PAYÉ (TTC) :", style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Black))
                  Text("${invoice.totalAmountDzd} DA", style = MaterialTheme.typography.titleMedium.copy(color = MechanicalOrange, fontWeight = FontWeight.Black))
                }
              }
            }
          }
        }

        // Bottom Actions
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = MetalCard,
          border = BorderStroke(0.5.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = onDismiss,
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, MetalBorder),
              modifier = Modifier.weight(1f)
            ) {
              Text("Fermer", color = AluminumLight)
            }

            Button(
              onClick = onDownloadPdf,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
              modifier = Modifier.weight(1.5f)
            ) {
              Icon(Icons.Default.Download, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Télécharger PDF", color = PureWhite, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
          }
        }
      }
    }
  }
}
