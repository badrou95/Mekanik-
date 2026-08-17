package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertSeverity
import com.example.data.model.MaintenanceAlert
import com.example.data.model.Vehicle
import com.example.data.repository.MekanikRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainNavigationTab
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

@Composable
fun HomeScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  onNavigateToVendors: () -> Unit,
  onNavigateToSubscriptions: () -> Unit,
  onNavigateToDocs: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("home_screen_content"),
    contentPadding = PaddingValues(bottom = 96.dp)
  ) {
    // Header Bar with Notification Bell & Plan
    item {
      HomeTopHeader(
        userPlan = uiState.userPlanName,
        dailyVinCount = uiState.dailyVinCount,
        dailyVinLimit = uiState.dailyVinLimit,
        unreadNotificationsCount = uiState.unreadNotificationsCount,
        onOpenPlans = onNavigateToSubscriptions,
        onOpenNotifications = { viewModel.toggleNotificationModal(true) }
      )
    }

    // Hero Search Area: Mekanik+ « La plateforme automobile nouvelle génération »
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "MEKANIK+",
            style = MaterialTheme.typography.displayLarge.copy(
              fontWeight = FontWeight.Black,
              fontSize = 24.sp,
              color = OffWhite,
              letterSpacing = 1.sp
            )
          )
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = PerformanceRedDark,
            border = BorderStroke(1.dp, PerformanceRed)
          ) {
            Text(
              text = "PRO 2026",
              style = MaterialTheme.typography.labelSmall.copy(
                color = PureWhite,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              ),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Text(
          text = "« La plateforme automobile nouvelle génération »",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = AluminumLight,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Central VIN / Engine / Vehicle Search Bar
        VinSearchBar(
          currentQuery = uiState.searchQuery,
          onQueryChange = { viewModel.onSearchQueryChanged(it) },
          onPerformSearch = { viewModel.performVehicleSearch() },
          selectedTab = uiState.searchTabMode,
          onTabSelected = { viewModel.setSearchTabMode(it) },
          onQuickChipClicked = { chipValue ->
            viewModel.onSearchQueryChanged(chipValue)
            viewModel.performVehicleSearch()
          }
        )
      }
    }

    // 6 Shortcut Cards Section:
    // 🔧 Pièces | 🚗 Identifier | 📘 Documentation | 🛠️ Atelier & RDV | 🧠 MekanikAI | 🏪 Vendeurs
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        Text(
          text = "SERVICES AUTOMOBILES RAPIDES",
          style = MaterialTheme.typography.labelMedium.copy(
            color = AluminumMuted,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
          )
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          ShortcutCard(
            title = "Trouver une pièce",
            subtitle = "OEM & Équivalences",
            icon = Icons.Default.Build,
            accentColor = PerformanceRed,
            onClick = { viewModel.setNavigationTab(MainNavigationTab.PIECES) },
            testTag = "shortcut_parts"
          )
          ShortcutCard(
            title = "Identifier véhicule",
            subtitle = "Décodeur VIN Algérie",
            icon = Icons.Default.DirectionsCar,
            accentColor = TechCyan,
            onClick = { viewModel.setNavigationTab(MainNavigationTab.RECHERCHE) },
            testTag = "shortcut_identify"
          )
          ShortcutCard(
            title = "Documentation",
            subtitle = "Manuels & Schémas",
            icon = Icons.Default.MenuBook,
            accentColor = TechnicalBlue,
            onClick = onNavigateToDocs,
            testTag = "shortcut_docs"
          )
          ShortcutCard(
            title = "MekanikAI",
            subtitle = "Diagnostic & OBD",
            icon = Icons.Default.Psychology,
            accentColor = MechanicalOrange,
            onClick = { viewModel.setNavigationTab(MainNavigationTab.AI_DIAGNOSTIC) },
            testTag = "shortcut_ai"
          )
          ShortcutCard(
            title = "Atelier & RDV",
            subtitle = "Planning & Devis",
            icon = Icons.Default.Handyman,
            accentColor = ElectricGreen,
            onClick = { viewModel.setNavigationTab(MainNavigationTab.ATELIER) },
            testTag = "shortcut_workshop"
          )
          ShortcutCard(
            title = "Trouver un vendeur",
            subtitle = "Magasins 58 Wilayas",
            icon = Icons.Default.Storefront,
            accentColor = AluminumLight,
            onClick = onNavigateToVendors,
            testTag = "shortcut_vendors"
          )
        }
      }
    }

    // Active Vehicle Card
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "VÉHICULE EN COURS",
            style = MaterialTheme.typography.labelMedium.copy(
              color = AluminumMuted,
              letterSpacing = 1.2.sp,
              fontWeight = FontWeight.Bold
            )
          )
          TextButton(onClick = { viewModel.setNavigationTab(MainNavigationTab.RECHERCHE) }) {
            Text(
              text = "Changer",
              color = MechanicalOrange,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
        }

        VehicleHeroCard(
          vehicle = uiState.activeVehicle,
          onOpenCatalog = { viewModel.setNavigationTab(MainNavigationTab.PIECES) }
        )
      }
    }

    // Professional Dashboard (Tableau de Bord)
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        Text(
          text = "TABLEAU DE BORD ATELIER",
          style = MaterialTheme.typography.labelMedium.copy(
            color = AluminumMuted,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
          )
        )
        Spacer(modifier = Modifier.height(10.dp))

        // 3 Key Stats
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          DashboardStatBox(
            title = "VINs Restants",
            value = "${uiState.dailyVinLimit - uiState.dailyVinCount} / ${uiState.dailyVinLimit}",
            subtitle = "Quota journalier",
            accentColor = PerformanceRed,
            modifier = Modifier.weight(1f)
          )
          DashboardStatBox(
            title = "Rendez-Vous",
            value = "${uiState.appointments.size}",
            subtitle = "Planning actif",
            accentColor = ElectricGreen,
            modifier = Modifier.weight(1f)
          )
          DashboardStatBox(
            title = "Ordres en cours",
            value = "${uiState.repairOrders.size}",
            subtitle = "Devis atelier",
            accentColor = MechanicalOrange,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // Proactive Maintenance & Self-Check Cards for Drivers
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Text(
          text = "GUIDES PRATIQUES & CONTRÔLES RAPIDES",
          style = MaterialTheme.typography.labelMedium.copy(
            color = AluminumMuted,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
          )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MetalCard,
          border = BorderStroke(1.dp, MetalBorder),
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              viewModel.onAiQueryChanged("Comment vérifier le niveau d'huile moteur ?")
              viewModel.setNavigationTab(MainNavigationTab.AI_DIAGNOSTIC)
              viewModel.askMekanikAi("Comment vérifier le niveau d'huile moteur ?")
            }
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MechanicalOrange.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.WaterDrop, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Vérifier le niveau d'huile moteur",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite)
              )
              Text(
                text = "Procédure jauge à froid & spécification huile 5W-30",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
              )
            }
            Text(
              text = "Guide IA →",
              style = MaterialTheme.typography.labelSmall.copy(color = MechanicalOrange, fontWeight = FontWeight.Bold)
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MetalCard,
          border = BorderStroke(1.dp, MetalBorder),
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              viewModel.onAiQueryChanged("Vérification et niveau liquide de refroidissement LDR")
              viewModel.setNavigationTab(MainNavigationTab.AI_DIAGNOSTIC)
              viewModel.askMekanikAi("Vérification et niveau liquide de refroidissement LDR")
            }
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(TechCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Thermostat, contentDescription = null, tint = TechCyan, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Contrôler le liquide de refroidissement (LDR)",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite)
              )
              Text(
                text = "Éviter la surchauffe • Vase d'expansion & norme G12evo",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
              )
            }
            Text(
              text = "Guide IA →",
              style = MaterialTheme.typography.labelSmall.copy(color = TechCyan, fontWeight = FontWeight.Bold)
            )
          }
        }
      }
    }

    // Maintenance Alerts
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Text(
          text = "ALERTES & ENTRETIENS PROGRAMMÉS",
          style = MaterialTheme.typography.labelMedium.copy(
            color = AluminumMuted,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
          )
        )
        Spacer(modifier = Modifier.height(10.dp))

        MekanikRepository.sampleAlerts.forEach { alert ->
          MaintenanceAlertCard(
            alert = alert,
            onAction = { viewModel.setNavigationTab(MainNavigationTab.PIECES) },
            modifier = Modifier.padding(bottom = 8.dp)
          )
        }
      }
    }

    // Printable VIN History Report Card (Monetisation 300 DA / 600 DA)
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp)
          .testTag("vin_report_promo_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(PerformanceRedDark.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.ReceiptLong,
              contentDescription = null,
              tint = PerformanceRedGlow,
              modifier = Modifier.size(26.dp)
            )
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Rapport Historique VIN Officiel",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PureWhite
              )
            )
            Text(
              text = "Fiche certifiée, kilométrage et campagnes de rappel (300 DA)",
              style = MaterialTheme.typography.bodySmall.copy(
                color = AluminumGray,
                fontSize = 11.5.sp
              )
            )
          }

          Button(
            onClick = { viewModel.generateVinReport(uiState.activeVehicle.vin) },
            colors = ButtonDefaults.buttonColors(
              containerColor = PerformanceRed,
              contentColor = PureWhite
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = "Générer",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            )
          }
        }
      }
    }
  }
}

@Composable
private fun HomeTopHeader(
  userPlan: String,
  dailyVinCount: Int,
  dailyVinLimit: Int,
  unreadNotificationsCount: Int,
  onOpenPlans: () -> Unit,
  onOpenNotifications: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(GraphiteBlack)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    MekanikLogo(isSmall = false, showTagline = true)

    Row(verticalAlignment = Alignment.CenterVertically) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = MetalSurface,
        border = BorderStroke(1.dp, MetalBorder),
        onClick = onOpenPlans
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(ElectricGreen)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "PRO",
            style = MaterialTheme.typography.labelSmall.copy(
              color = PureWhite,
              fontWeight = FontWeight.Black,
              fontSize = 11.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Notification Bell with Badge
      Box {
        IconButton(
          onClick = onOpenNotifications,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MetalCard)
            .testTag("btn_header_notifications")
        ) {
          Icon(
            Icons.Default.Notifications,
            contentDescription = "Notifications",
            tint = if (unreadNotificationsCount > 0) PerformanceRedGlow else AluminumLight,
            modifier = Modifier.size(18.dp)
          )
        }

        if (unreadNotificationsCount > 0) {
          Box(
            modifier = Modifier
              .size(14.dp)
              .align(Alignment.TopEnd)
              .clip(CircleShape)
              .background(PerformanceRed),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "$unreadNotificationsCount",
              color = PureWhite,
              fontSize = 8.5.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ShortcutCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  accentColor: Color,
  onClick: () -> Unit,
  testTag: String = "shortcut_card"
) {
  Card(
    modifier = Modifier
      .width(135.dp)
      .height(115.dp)
      .clickable(onClick = onClick)
      .testTag(testTag),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(accentColor.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(20.dp)
        )
      }

      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = PureWhite,
            fontSize = 12.sp,
            lineHeight = 15.sp
          )
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall.copy(
            color = AluminumMuted,
            fontSize = 10.sp
          ),
          maxLines = 1
        )
      }
    }
  }
}

@Composable
private fun DashboardStatBox(
  title: String,
  value: String,
  subtitle: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = MetalCard,
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
          color = AluminumMuted,
          fontSize = 10.sp
        )
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(
          color = accentColor,
          fontWeight = FontWeight.Black,
          fontSize = 14.sp
        )
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(
          color = AluminumGray,
          fontSize = 9.5.sp
        )
      )
    }
  }
}

@Composable
private fun MaintenanceAlertCard(
  alert: MaintenanceAlert,
  onAction: () -> Unit,
  modifier: Modifier = Modifier
) {
  val severityColor = when (alert.severity) {
    AlertSeverity.URGENT -> PerformanceRed
    AlertSeverity.PREVENTIF -> MechanicalOrange
    AlertSeverity.CONSEILLE -> TechCyan
  }

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    color = MetalCard,
    border = BorderStroke(1.dp, severityColor.copy(alpha = 0.4f))
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(10.dp)
          .clip(CircleShape)
          .background(severityColor)
      )
      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = alert.title,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.Bold,
              color = OffWhite,
              fontSize = 12.5.sp
            )
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "• ${alert.vehicle}",
            style = MaterialTheme.typography.labelSmall.copy(
              color = AluminumMuted,
              fontSize = 10.sp
            )
          )
        }
        Text(
          text = alert.dueInfo,
          style = MaterialTheme.typography.bodySmall.copy(
            color = AluminumGray,
            fontSize = 11.sp
          )
        )
      }

      IconButton(onClick = onAction) {
        Icon(
          Icons.Default.ArrowForwardIos,
          contentDescription = "Voir pièces",
          tint = AluminumLight,
          modifier = Modifier.size(14.dp)
        )
      }
    }
  }
}
