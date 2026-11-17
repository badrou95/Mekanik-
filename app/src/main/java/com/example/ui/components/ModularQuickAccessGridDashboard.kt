package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyMaintenanceTask
import com.example.data.model.FiscalInvoice
import com.example.data.model.MaintenanceTaskStatus
import com.example.data.model.RepairOrder
import com.example.data.model.RepairStatus
import com.example.data.model.Vehicle
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

// Palette stricte demandée : #1C2833 pour les cartes et #F07836 pour les boutons d'action
private val ModularCardSurfaceColor = Color(0xFF1C2833)
private val ModularActionOrangeColor = Color(0xFFF07836)
private val ModularBgDarkColor = Color(0xFF101820)
private val ModularPrimaryTextColor = Color(0xFFE8EDF2)
private val ModularSecondaryTextColor = Color(0xFF94A3B8)
private val ModularValidationGreenColor = Color(0xFF22C55E)
private val ModularBorderColor = Color(0xFF2B3B4B)

enum class ModularDashboardCardType(
  val title: String,
  val frenchSubtitle: String
) {
  VIN_SEARCH("VIN Search", "Recherche VIN & Décodage OEM"),
  PENDING_REPAIRS("Pending Repairs", "Réparations en Attente & Ponts"),
  ACTIVE_QUOTES("Active Quotes", "Devis Actifs & Signature Client"),
  DAILY_SALES_KPI("Daily Sales KPI", "Ventes du Jour & Facturation")
}

/**
 * Tableau de bord modulaire en grille (2x2 adaptative) offrant un accès rapide aux 4 piliers :
 * 1. 'VIN Search'
 * 2. 'Pending Repairs'
 * 3. 'Active Quotes'
 * 4. 'Daily Sales KPI'
 *
 * Utilise strictement la couleur #1C2833 pour les cartes et #F07836 pour les boutons d'action.
 */
@Composable
fun ModularQuickAccessGridDashboard(
  activeVehicle: Vehicle,
  currentVinQuery: String,
  dailyVinCount: Int,
  dailyVinLimit: Int,
  repairOrders: List<RepairOrder>,
  dailyMaintenanceTasks: List<DailyMaintenanceTask>,
  invoicesList: List<FiscalInvoice>,
  onVinSearchAction: () -> Unit,
  onScanVinOcrAction: () -> Unit,
  onPendingRepairsAction: () -> Unit,
  onActiveQuotesAction: () -> Unit,
  onCreateNewQuoteAction: () -> Unit,
  onDailySalesKpiAction: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedPreviewModule by remember { mutableStateOf<ModularDashboardCardType?>(null) }

  val pendingRepairsList = remember(repairOrders) {
    repairOrders.filter {
      it.status == RepairStatus.EN_COURS ||
        it.status == RepairStatus.DIAGNOSTIC ||
        it.status == RepairStatus.PIECES_COMMANDEES
    }
  }
  val pendingTasksCount = remember(dailyMaintenanceTasks) {
    dailyMaintenanceTasks.count { it.status != MaintenanceTaskStatus.TERMINE }
  }
  val totalPendingRepairsCount = pendingRepairsList.size + pendingTasksCount

  val activeQuotesList = remember(repairOrders) {
    repairOrders.filter { it.status != RepairStatus.FACTURE }
  }
  val activeQuotesTotalDzd: Long = remember(activeQuotesList) {
    activeQuotesList.sumOf { it.netTotalDzd.toLong() }
  }

  val dailySalesTotalDzd: Long = remember(invoicesList, activeQuotesTotalDzd) {
    val invoicesSum = invoicesList.sumOf { it.totalTtcDzd.toLong() }
    if (invoicesSum > 0L) invoicesSum else activeQuotesTotalDzd
  }

  val numberFormatter = remember { NumberFormat.getInstance(Locale.FRANCE) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("modular_quick_access_grid_dashboard"),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // En-tête de section
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(ModularActionOrangeColor)
        )
        Column {
          Text(
            text = "MODULAR QUICK-ACCESS DASHBOARD",
            color = ModularPrimaryTextColor,
            fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp,
            letterSpacing = 0.6.sp
          )
          Text(
            text = "Accès rapide : VIN Search • Pending Repairs • Active Quotes • Daily Sales KPI",
            color = ModularSecondaryTextColor,
            fontSize = 11.sp
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(8.dp),
        color = ModularCardSurfaceColor,
        border = BorderStroke(1.dp, ModularBorderColor)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(ModularValidationGreenColor)
          )
          Text(
            text = "LIVE KPI",
            color = ModularValidationGreenColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        }
      }
    }

    // Grille Modulaire 2x2 (Ligne 1 : VIN Search & Pending Repairs)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // CARTE 1 : VIN Search
      ModularDashboardGridCard(
        cardType = ModularDashboardCardType.VIN_SEARCH,
        icon = Icons.Default.QrCodeScanner,
        badgeText = "$dailyVinCount/$dailyVinLimit VIN",
        badgeColor = ModularValidationGreenColor,
        primaryValue = currentVinQuery.take(11) + "…",
        secondaryMetric = "${activeVehicle.brand} ${activeVehicle.model} (${activeVehicle.engineCode})",
        actionButtonLabel = "VIN Search",
        isPreviewExpanded = selectedPreviewModule == ModularDashboardCardType.VIN_SEARCH,
        onTogglePreview = {
          selectedPreviewModule =
            if (selectedPreviewModule == ModularDashboardCardType.VIN_SEARCH) null
            else ModularDashboardCardType.VIN_SEARCH
        },
        onActionClick = onVinSearchAction,
        cardTestTag = "modular_card_vin_search",
        buttonTestTag = "btn_modular_vin_search",
        modifier = Modifier.weight(1f)
      )

      // CARTE 2 : Pending Repairs
      ModularDashboardGridCard(
        cardType = ModularDashboardCardType.PENDING_REPAIRS,
        icon = Icons.Default.BuildCircle,
        badgeText = "${pendingRepairsList.size} OR actifs",
        badgeColor = ModularActionOrangeColor,
        primaryValue = "$totalPendingRepairsCount",
        secondaryMetric = "${pendingRepairsList.size} véhicules sur pont • $pendingTasksCount tâches",
        actionButtonLabel = "Pending Repairs",
        isPreviewExpanded = selectedPreviewModule == ModularDashboardCardType.PENDING_REPAIRS,
        onTogglePreview = {
          selectedPreviewModule =
            if (selectedPreviewModule == ModularDashboardCardType.PENDING_REPAIRS) null
            else ModularDashboardCardType.PENDING_REPAIRS
        },
        onActionClick = onPendingRepairsAction,
        cardTestTag = "modular_card_pending_repairs",
        buttonTestTag = "btn_modular_pending_repairs",
        modifier = Modifier.weight(1f)
      )
    }

    // Grille Modulaire 2x2 (Ligne 2 : Active Quotes & Daily Sales KPI)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // CARTE 3 : Active Quotes
      ModularDashboardGridCard(
        cardType = ModularDashboardCardType.ACTIVE_QUOTES,
        icon = Icons.Default.ReceiptLong,
        badgeText = "${activeQuotesList.size} Devis",
        badgeColor = ModularValidationGreenColor,
        primaryValue = "${numberFormatter.format(activeQuotesTotalDzd)} DA",
        secondaryMetric = "${activeQuotesList.size} devis ouverts • Signature QR",
        actionButtonLabel = "Active Quotes",
        isPreviewExpanded = selectedPreviewModule == ModularDashboardCardType.ACTIVE_QUOTES,
        onTogglePreview = {
          selectedPreviewModule =
            if (selectedPreviewModule == ModularDashboardCardType.ACTIVE_QUOTES) null
            else ModularDashboardCardType.ACTIVE_QUOTES
        },
        onActionClick = onActiveQuotesAction,
        cardTestTag = "modular_card_active_quotes",
        buttonTestTag = "btn_modular_active_quotes",
        modifier = Modifier.weight(1f)
      )

      // CARTE 4 : Daily Sales KPI
      ModularDashboardGridCard(
        cardType = ModularDashboardCardType.DAILY_SALES_KPI,
        icon = Icons.Default.TrendingUp,
        badgeText = "+14.8%",
        badgeColor = ModularValidationGreenColor,
        primaryValue = "${numberFormatter.format(dailySalesTotalDzd)} DA",
        secondaryMetric = "${invoicesList.size} factures • CIB / Edahabia",
        actionButtonLabel = "Daily Sales KPI",
        isPreviewExpanded = selectedPreviewModule == ModularDashboardCardType.DAILY_SALES_KPI,
        onTogglePreview = {
          selectedPreviewModule =
            if (selectedPreviewModule == ModularDashboardCardType.DAILY_SALES_KPI) null
            else ModularDashboardCardType.DAILY_SALES_KPI
        },
        onActionClick = onDailySalesKpiAction,
        cardTestTag = "modular_card_daily_sales_kpi",
        buttonTestTag = "btn_modular_daily_sales_kpi",
        modifier = Modifier.weight(1f)
      )
    }

    // Panneau de prévisualisation rapide repliable lorsqu'une carte de la grille est sélectionnée
    AnimatedVisibility(
      visible = selectedPreviewModule != null,
      enter = fadeIn(tween(180)) + expandVertically(tween(180)),
      exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
    ) {
      val currentModule = selectedPreviewModule
      if (currentModule != null) {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = ModularCardSurfaceColor,
          border = BorderStroke(1.dp, ModularActionOrangeColor),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("modular_dashboard_expanded_preview")
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
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "${currentModule.title.uppercase()} — APERÇU DIRECT",
                  color = ModularPrimaryTextColor,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
                Text(
                  text = currentModule.frenchSubtitle,
                  color = ModularSecondaryTextColor,
                  fontSize = 11.sp
                )
              }
              IconButton(
                onClick = { selectedPreviewModule = null },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(
                  Icons.Default.Close,
                  contentDescription = "Fermer l'aperçu",
                  tint = ModularSecondaryTextColor,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            HorizontalDivider(color = ModularBorderColor)

            when (currentModule) {
              ModularDashboardCardType.VIN_SEARCH -> {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "Véhicule actif : ${activeVehicle.brand} ${activeVehicle.model}",
                      color = ModularPrimaryTextColor,
                      fontWeight = FontWeight.SemiBold,
                      fontSize = 12.5.sp
                    )
                    Text(
                      text = "VIN : ${activeVehicle.vin} • Moteur : ${activeVehicle.engineCode}",
                      color = ModularSecondaryTextColor,
                      fontFamily = FontFamily.Monospace,
                      fontSize = 11.sp
                    )
                  }
                  Button(
                    onClick = onScanVinOcrAction,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                      containerColor = ModularActionOrangeColor,
                      contentColor = Color.White
                    ),
                    modifier = Modifier.height(44.dp)
                  ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Scan OCR", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                  }
                }
              }

              ModularDashboardCardType.PENDING_REPAIRS -> {
                pendingRepairsList.take(3).forEach { order ->
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ModularBgDarkColor,
                    border = BorderStroke(1.dp, ModularBorderColor),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = "${order.vehicleModel} • ${order.clientName}",
                          color = ModularPrimaryTextColor,
                          fontWeight = FontWeight.SemiBold,
                          fontSize = 12.sp
                        )
                        Text(
                          text = "OR ${order.orderNumber} • ${order.status.label}",
                          color = ModularSecondaryTextColor,
                          fontSize = 11.sp
                        )
                      }
                      Text(
                        text = "${numberFormatter.format(order.netTotalDzd)} DA",
                        color = ModularActionOrangeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                      )
                    }
                  }
                }
              }

              ModularDashboardCardType.ACTIVE_QUOTES -> {
                activeQuotesList.take(3).forEach { quote ->
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ModularBgDarkColor,
                    border = BorderStroke(1.dp, ModularBorderColor),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = "Devis ${quote.orderNumber} — ${quote.clientName}",
                          color = ModularPrimaryTextColor,
                          fontWeight = FontWeight.SemiBold,
                          fontSize = 12.sp
                        )
                        Text(
                          text = "${quote.vehicleModel} • ${quote.items.size} lignes pièces",
                          color = ModularSecondaryTextColor,
                          fontSize = 11.sp
                        )
                      }
                      Text(
                        text = "${numberFormatter.format(quote.netTotalDzd)} DA",
                        color = ModularValidationGreenColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                      )
                    }
                  }
                }
                Button(
                  onClick = onCreateNewQuoteAction,
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = ModularActionOrangeColor,
                    contentColor = Color.White
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                ) {
                  Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Créer un Nouveau Devis Atelier", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
              }

              ModularDashboardCardType.DAILY_SALES_KPI -> {
                val avgCartDzd = dailySalesTotalDzd.toLong() / invoicesList.size.coerceAtLeast(1).toLong()
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  KpiMiniStatBox(
                    label = "CA TTC Jour",
                    value = "${numberFormatter.format(dailySalesTotalDzd)} DA",
                    accentColor = ModularValidationGreenColor,
                    modifier = Modifier.weight(1f)
                  )
                  KpiMiniStatBox(
                    label = "Panier Moyen",
                    value = "${numberFormatter.format(avgCartDzd)} DA",
                    accentColor = ModularActionOrangeColor,
                    modifier = Modifier.weight(1f)
                  )
                  KpiMiniStatBox(
                    label = "Taux Transformation",
                    value = "84%",
                    accentColor = ModularValidationGreenColor,
                    modifier = Modifier.weight(1f)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ModularDashboardGridCard(
  cardType: ModularDashboardCardType,
  icon: ImageVector,
  badgeText: String,
  badgeColor: Color,
  primaryValue: String,
  secondaryMetric: String,
  actionButtonLabel: String,
  isPreviewExpanded: Boolean,
  onTogglePreview: () -> Unit,
  onActionClick: () -> Unit,
  cardTestTag: String,
  buttonTestTag: String,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .clickable { onTogglePreview() }
      .testTag(cardTestTag),
    shape = RoundedCornerShape(14.dp),
    color = ModularCardSurfaceColor, // #1C2833
    border = BorderStroke(
      width = 1.dp,
      color = if (isPreviewExpanded) ModularActionOrangeColor else ModularBorderColor
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Ligne 1 : Icône dans conteneur #101820 + Badge KPI
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(ModularBgDarkColor),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = cardType.title,
            tint = ModularActionOrangeColor,
            modifier = Modifier.size(20.dp)
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = badgeColor.copy(alpha = 0.16f),
          border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.7f))
        ) {
          Text(
            text = badgeText,
            color = badgeColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
          )
        }
      }

      // Ligne 2 : Titre du module + Valeur principale + Sous-titre
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = cardType.title,
          color = ModularSecondaryTextColor,
          fontWeight = FontWeight.SemiBold,
          fontSize = 11.5.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = primaryValue,
          color = ModularPrimaryTextColor,
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = secondaryMetric,
          color = ModularSecondaryTextColor,
          fontSize = 10.5.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      // Ligne 3 : Bouton d'action principal en #F07836 (hauteur confortable 48.dp, coins 12.dp)
      Button(
        onClick = onActionClick,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag(buttonTestTag),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = ModularActionOrangeColor, // #F07836
          contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
      ) {
        Text(
          text = actionButtonLabel,
          color = Color.White,
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
          imageVector = Icons.Default.ArrowForward,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(15.dp)
        )
      }
    }
  }
}

@Composable
private fun KpiMiniStatBox(
  label: String,
  value: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = ModularBgDarkColor,
    border = BorderStroke(1.dp, ModularBorderColor),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Text(
        text = label,
        color = ModularSecondaryTextColor,
        fontSize = 10.5.sp
      )
      Text(
        text = value,
        color = accentColor,
        fontWeight = FontWeight.Bold,
        fontSize = 12.5.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
