package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AtelierDashboardView
import com.example.ui.components.CreateWorkshopClientAccountModal
import com.example.ui.components.MekanikPrimaryButton
import com.example.ui.components.MekanikSecondaryButton
import com.example.ui.components.OfflineCacheHubTab
import com.example.ui.components.RepairOrderChecklistModal
import com.example.ui.components.VoiceDictationModal
import com.example.ui.components.WorkshopClientPortalModal
import com.example.ui.components.WorkshopOfflineCacheStatusBanner
import com.example.ui.components.WorkshopQuoteGeneratorModal
import com.example.ui.components.WorkshopRechartsView
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

enum class WorkshopTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
  DASHBOARD("Tableau de Bord", Icons.Default.Dashboard),
  HISTORIQUE_PANNES_CACHE("Pannes, Historique & Cache", Icons.Default.Storage),
  RENDEZ_VOUS("Planning & RDV", Icons.Default.EventAvailable),
  ORDRES_DEVIS("Ordres & Devis", Icons.Default.ReceiptLong),
  COMPTES_CLIENTS("Comptes Clients & QR", Icons.Default.QrCodeScanner),
  SOUS_COMPTES_ULTRA_PRO("Équipes Ultra Pro", Icons.Default.CorporateFare),
  STATISTIQUES("Stats Recharts", Icons.Default.BarChart)
}

@Composable
fun WorkshopScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableStateOf(WorkshopTab.DASHBOARD) }
  var appointmentStatusFilter by remember { mutableStateOf<AppointmentStatus?>(null) }
  var selectedOrderForDetails by remember { mutableStateOf<RepairOrder?>(null) }
  var selectedOrderForChecklist by remember { mutableStateOf<RepairOrder?>(null) }
  var showSignatureDialogForOrder by remember { mutableStateOf<RepairOrder?>(null) }
  var selectedAptForDetails by remember { mutableStateOf<WorkshopAppointment?>(null) }

  val networkCondition by viewModel.workshopNetworkCondition.collectAsStateWithLifecycle()
  val cacheSummary by viewModel.localCacheSummary.collectAsStateWithLifecycle()
  val clientFaultHistory by viewModel.clientFaultHistory.collectAsStateWithLifecycle()
  val savedDiagnostics by viewModel.savedDiagnostics.collectAsStateWithLifecycle()
  val cachedSchematics by viewModel.cachedSchematics.collectAsStateWithLifecycle()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("workshop_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Handyman,
              contentDescription = null,
              tint = PerformanceRed,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "ESPACE ATELIER PRO",
              style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite,
                fontSize = 20.sp
              )
            )
          }
          Text(
            text = "Gestion des rendez-vous en ligne, planning et ordres de réparation",
            style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
          )
        }

        IconButton(
          onClick = {
            if (selectedTab == WorkshopTab.RENDEZ_VOUS) {
              viewModel.toggleBookAppointmentDialog(true)
            } else {
              viewModel.toggleNewQuoteDialog(true)
            }
          },
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(PerformanceRed)
            .testTag("btn_add_action_header")
        ) {
          Icon(
            Icons.Default.Add,
            contentDescription = "Ajouter",
            tint = PureWhite
          )
        }
      }
    }

    // Bannière Stratégie de Mise en Cache Locale (Schémas 0ms, Diagnostics Enregistrés & Pannes Clients)
    item {
      WorkshopOfflineCacheStatusBanner(
        networkCondition = networkCondition,
        cacheSummary = cacheSummary,
        onOpenCacheHub = { tab -> viewModel.openOfflineCacheHubModal(tab) },
        onCycleNetworkCondition = { viewModel.cycleWorkshopNetworkCondition() }
      )
    }

    // Suite Métier DMS WinMotor Next & Passerelle Peugeot Service Box V14 Banner
    item {
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = adaptiveBannerSurface(0xFF141E2B, 0xFFEFF6FF),
        border = BorderStroke(1.2.dp, TechCyan),
        onClick = { viewModel.openWinMotorServiceBoxModal() },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("workshop_winmotor_dms_banner")
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(TechCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.PrecisionManufacturing,
                contentDescription = null,
                tint = TechCyan,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "DMS MÉCANICAUTO+™ • GESTION AUTOMOBILE INTÉGRÉE",
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.5.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = MechanicalOrange.copy(alpha = 0.2f),
                  border = BorderStroke(1.dp, MechanicalOrange)
                ) {
                  Text(
                    text = "T1/T2/T3 • CTH",
                    color = MechanicalOrange,
                    fontWeight = FontWeight.Black,
                    fontSize = 8.sp,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                  )
                }
              }
              Text(
                text = "Notre DMS propriétaire : Paniers OEM • Pointage compagnons • Facturation multi-payeurs • Reliquats VOR",
                color = AluminumLight,
                fontSize = 10.sp
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = TechCyan
          ) {
            Text(
              text = "OUVRIR DMS",
              color = GraphiteBlack,
              fontWeight = FontWeight.Black,
              fontSize = 10.sp,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }
    }

    // Quick-Access Row: Recharts Analytics + Pointage Compagnon & 6 Boucliers de Défense RBAC
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = adaptiveBannerSurface(0xFF1E1914, 0xFFFFF7ED),
          border = BorderStroke(1.2.dp, MechanicalOrange),
          onClick = { selectedTab = WorkshopTab.STATISTIQUES },
          modifier = Modifier
            .weight(1f)
            .testTag("banner_workshop_recharts_pointage")
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.PunchClock, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "RECHARTS & POINTAGE",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 10.5.sp
              )
              Text(
                text = "Productivité • Garantie/Cession",
                color = MechanicalOrange,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = adaptiveBannerSurface(0xFF12231B, 0xFFF0FDF4),
          border = BorderStroke(1.2.dp, ElectricGreen),
          onClick = { viewModel.openSecurityShieldModal() },
          modifier = Modifier
            .weight(1f)
            .testTag("banner_workshop_security_shields")
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Shield, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "6 BOUCLIERS & RBAC",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 10.5.sp
              )
              Text(
                text = "Rôle : ${uiState.currentFirebaseRole.id}",
                color = ElectricGreen,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    // Workshop Performance Banner
    item {
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
            Column {
              Text(
                text = "Garage Auto Performance Alger",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = PureWhite
                )
              )
              Text(
                text = "3 Ponts Élévateurs • Taux M.O : 2 000 DA/h",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumLight,
                  fontSize = 11.5.sp
                )
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = GraphiteBlack,
              border = BorderStroke(0.5.dp, ElectricGreen)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
                  text = "Atelier Ouvert",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = ElectricGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                  )
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Quick Action Switcher Buttons
          LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(WorkshopTab.entries) { tab ->
              val isSelected = tab == selectedTab
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) PerformanceRed else GraphiteBlack,
                border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
                onClick = { selectedTab = tab },
                modifier = Modifier.testTag("tab_workshop_${tab.name.lowercase()}")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
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
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = if (isSelected) PureWhite else AluminumLight,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                      fontSize = 11.5.sp
                    )
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Fast QR Scanner Action for Reception of Vehicles
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = GraphiteBlack,
            border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.6f)),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.toggleUniversalQrScanner(true) }
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
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = TechCyan, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "SCANNER QR VÉHICULE EN RÉCEPTION",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = PureWhite,
                    fontWeight = FontWeight.Bold
                  )
                )
                Text(
                  text = "Accéder immédiatement aux Ordres de Réparation, Devis et Pièces demandées",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 10.5.sp)
                )
              }
              Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TechCyan, modifier = Modifier.size(14.dp))
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Active Manufacturer Mode & OEM Software Hub Shortcut
          val activeGroup = uiState.workshopSoftwareConfig.activeModeGroup
          val isPackUnlocked = uiState.workshopSoftwareConfig.hasFullSoftwarePackageUnlocked
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = adaptiveBannerSurface(0xFF131D27, 0xFFEFF6FF),
            border = BorderStroke(1.dp, Color(activeGroup.themeColorHex).copy(alpha = 0.7f)),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.openOemSoftwareLauncher() }
              .testTag("banner_oem_software_workshop")
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                  modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(activeGroup.themeColorHex).copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(text = activeGroup.iconEmoji, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "MODE ATELIER : ${activeGroup.shortCode}",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(activeGroup.themeColorHex),
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                      )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                      shape = RoundedCornerShape(3.dp),
                      color = if (isPackUnlocked) ElectricGreenDark else PerformanceRedDark
                    ) {
                      Text(
                        text = if (isPackUnlocked) "PACK INTÉGRAL" else "+10 000 DA",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = if (isPackUnlocked) ElectricGreen else PerformanceRedGlow,
                          fontSize = 8.5.sp,
                          fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                      )
                    }
                  }
                  Text(
                    text = "Catalogues EPC (ETKA, ServiceBox, Dialogys, Microcat, TecDoc...)",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = AluminumLight,
                      fontSize = 10.5.sp
                    )
                  )
                }
              }

              Icon(Icons.Default.Launch, contentDescription = null, tint = Color(activeGroup.themeColorHex), modifier = Modifier.size(18.dp))
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Multi-Device (Max 3) & 2FA Google Authenticator + Store Link Quick Actions Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // 2FA Multi-Device Card
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = adaptiveBannerSurface(0xFF132228, 0xFFF0F9FF),
              border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.6f)),
              modifier = Modifier
                .weight(1f)
                .clickable { viewModel.openWorkshop2FAModal() }
                .testTag("banner_2fa_multi_device_workshop")
            ) {
              Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(TechCyan.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Devices, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "${uiState.workshop2FAConfig.activeDevicesCount}/3 APPAREILS",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = TechCyan,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                      )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ElectricGreen))
                  }
                  Text(
                    text = "2FA Google Auth",
                    style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 9.5.sp)
                  )
                }
              }
            }

            // Store Link Card (+5 000 DA)
            val isLinked = uiState.workshopStoreLinkConfig.isStoreLinked
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isLinked) adaptiveBannerSurface(0xFF261D15, 0xFFFFF7ED) else GraphiteBlack,
              border = BorderStroke(1.dp, if (isLinked) MechanicalOrange else MetalBorder),
              modifier = Modifier
                .weight(1f)
                .clickable { viewModel.openWorkshopStoreLinkModal() }
                .testTag("banner_store_link_workshop")
            ) {
              Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MechanicalOrange.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Storefront, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                  Text(
                    text = if (isLinked) "MAGASIN RELIÉ" else "+5 000 DA MAGASIN",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = MechanicalOrange,
                      fontWeight = FontWeight.Black,
                      fontSize = 10.sp
                    )
                  )
                  Text(
                    text = if (isLinked) "Compte Unique" else "Lier mon magasin",
                    style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 9.5.sp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // 3 Nouveaux Modules : Inspection 360°, Barème Temps M.O & Messagerie WhatsApp
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // 1. Inspection 360 Card
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = adaptiveBannerSurface(0xFF26181A, 0xFFFFF1F2),
              border = BorderStroke(1.dp, PerformanceRedGlow),
              modifier = Modifier
                .weight(1f)
                .clickable { viewModel.openInspectionModal() }
                .testTag("banner_inspection_workshop")
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("INSPECTION 360°", color = PerformanceRedGlow, fontWeight = FontWeight.Black, fontSize = 9.5.sp)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text("État des lieux carrosserie", color = AluminumLight, fontSize = 9.sp)
              }
            }

            // 2. Barème OEM Card
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = adaptiveBannerSurface(0xFF13222B, 0xFFF0F9FF),
              border = BorderStroke(1.dp, TechCyan),
              modifier = Modifier
                .weight(1f)
                .clickable { viewModel.openLaborStandardModal() }
                .testTag("banner_labor_standards_workshop")
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Timer, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("BARÈME OEM", color = TechCyan, fontWeight = FontWeight.Black, fontSize = 9.5.sp)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text("Temps & Chiffrage M.O", color = AluminumLight, fontSize = 9.sp)
              }
            }

            // 3. WhatsApp SMS Card
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = adaptiveBannerSurface(0xFF12281E, 0xFFF0FDF4),
              border = BorderStroke(1.dp, ElectricGreen),
              modifier = Modifier
                .weight(1f)
                .clickable { viewModel.openClientMessagingModal() }
                .testTag("banner_whatsapp_sms_workshop")
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Send, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("WHATSAPP 📱", color = ElectricGreen, fontWeight = FontWeight.Black, fontSize = 9.5.sp)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text("Alerte devis & prêt", color = AluminumLight, fontSize = 9.sp)
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // 4 Nouveaux Modules Suite Pro : Ponts Temps Réel, Devis à Distance, Comparateur B2B & Garanties RMA
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = GraphiteBlack,
              border = BorderStroke(1.dp, MechanicalOrange),
              modifier = Modifier
                .weight(1f)
                .clickable { viewModel.openBayPlannerModal() }
                .testTag("banner_bay_planner_workshop")
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.ViewKanban, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(15.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("PONTS & CHRONO", color = MechanicalOrange, fontWeight = FontWeight.Black, fontSize = 9.sp)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text("Kanban 4 baies", color = AluminumLight, fontSize = 8.5.sp)
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = GraphiteBlack,
              border = BorderStroke(1.dp, TechCyan),
              modifier = Modifier
                .weight(1f)
                .clickable { viewModel.openRemoteQuotePortalModal() }
                .testTag("banner_remote_quote_workshop")
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Draw, contentDescription = null, tint = TechCyan, modifier = Modifier.size(15.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("DEVIS SIGNÉ", color = TechCyan, fontWeight = FontWeight.Black, fontSize = 9.sp)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text("Accord à distance", color = AluminumLight, fontSize = 8.5.sp)
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = GraphiteBlack,
              border = BorderStroke(1.dp, WarningYellow),
              modifier = Modifier
                .weight(1f)
                .clickable { viewModel.openWarrantyRmaModal() }
                .testTag("banner_warranty_rma_workshop")
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(15.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("SAV & RMA", color = WarningYellow, fontWeight = FontWeight.Black, fontSize = 9.sp)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text("Garanties pièces", color = AluminumLight, fontSize = 8.5.sp)
              }
            }
          }
        }
      }
    }

    // Dynamic Tab Views
    when (selectedTab) {
      WorkshopTab.DASHBOARD -> {
        item {
          AtelierDashboardView(
            uiState = uiState,
            viewModel = viewModel,
            onOpenOrderChecklist = { order -> selectedOrderForChecklist = order },
            onOpenOrderQuote = { order -> selectedOrderForDetails = order },
            onNavigateToTab = { targetTab -> selectedTab = targetTab }
          )
        }
      }
      WorkshopTab.RENDEZ_VOUS -> {
        // Appointments Section
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "PLANNING DES RÉSERVATIONS CLIENTS (${uiState.appointments.size})",
              style = MaterialTheme.typography.labelMedium.copy(
                color = AluminumMuted,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
              )
            )

            TextButton(
              onClick = { viewModel.toggleBookAppointmentDialog(true) },
              contentPadding = PaddingValues(0.dp)
            ) {
              Text(
                text = "+ Prendre RDV",
                color = MechanicalOrange,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
        }

        // Appointment Filter Chips
        item {
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            item {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (appointmentStatusFilter == null) PerformanceRed else MetalSurface,
                border = BorderStroke(1.dp, if (appointmentStatusFilter == null) PerformanceRed else MetalBorder),
                onClick = { appointmentStatusFilter = null }
              ) {
                Text(
                  text = "Tous (${uiState.appointments.size})",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  ),
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
              }
            }

            items(AppointmentStatus.entries) { status ->
              val isSelected = appointmentStatusFilter == status
              val count = uiState.appointments.count { it.status == status }
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(status.colorHex) else MetalSurface,
                border = BorderStroke(1.dp, if (isSelected) Color(status.colorHex) else MetalBorder),
                onClick = { appointmentStatusFilter = if (isSelected) null else status }
              ) {
                Text(
                  text = "${status.label} ($count)",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) GraphiteBlack else AluminumLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  ),
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
              }
            }
          }
        }

        val filteredApts = if (appointmentStatusFilter == null) {
          uiState.appointments
        } else {
          uiState.appointments.filter { it.status == appointmentStatusFilter }
        }

        items(filteredApts) { apt ->
          AppointmentCard(
            appointment = apt,
            onConfirm = { viewModel.updateAppointmentStatus(apt.id, AppointmentStatus.CONFIRME) },
            onConvertToOrder = { viewModel.convertAppointmentToRepairOrder(apt) },
            onViewDetails = { selectedAptForDetails = apt }
          )
        }
      }

      WorkshopTab.ORDRES_DEVIS -> {
        // Orders List Header
        // 1. Hero Action Banner: Professional Quote Generator & PDF Preview
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.toggleNewQuoteDialog(true) }
              .testTag("card_open_quote_generator"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MetalCard),
            border = BorderStroke(1.dp, PerformanceRedDark)
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(PerformanceRedDark),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.ReceiptLong,
                  contentDescription = null,
                  tint = PerformanceRedGlow,
                  modifier = Modifier.size(24.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "Générateur de Devis Atelier",
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontWeight = FontWeight.Black,
                      color = PureWhite
                    )
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = TechnicalBlue.copy(alpha = 0.3f),
                    border = BorderStroke(0.5.dp, TechCyan)
                  ) {
                    Text(
                      text = "PDF PRO",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = TechCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                      ),
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = "Extraction pièces catalogue, main-d'œuvre, calcul TTC en DZD et aperçu PDF normalisé",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = AluminumGray,
                    fontSize = 11.sp
                  )
                )
              }

              Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = PerformanceRedGlow
              )
            }
          }
        }

        // 1-B. Banner: Checklist d'Ordre de Réparation (Contrôle des 22 Ampoules + État Intérieur/Extérieur Propre/Sale)
        item {
          val targetOr = uiState.repairOrders.firstOrNull()
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                if (targetOr != null) {
                  selectedOrderForChecklist = targetOr
                } else {
                  viewModel.toggleNewQuoteDialog(true)
                }
              }
              .testTag("card_open_or_bulbs_and_cleanliness_checklist"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = adaptiveBannerSurface(0xFF231A13, 0xFFFFF7ED)),
            border = BorderStroke(1.2.dp, MechanicalOrange)
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(MechanicalOrange.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.Mic,
                  contentDescription = null,
                  tint = MechanicalOrange,
                  modifier = Modifier.size(24.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "Ordre de Réparation : Photo IA, Voix 5 Langues & Checklist",
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontWeight = FontWeight.Black,
                      color = PureWhite,
                      fontSize = 13.sp
                    )
                  )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "Photo auto (Immat, VIN, Carburant, Km, Carte Grise, Voyants allumés) • Voix IA (FR/AR/EN/ES/ZH) • Pneus • 23 Ampoules • Signature électronique",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = AluminumLight,
                    fontSize = 10.5.sp
                  )
                )
              }

              Spacer(modifier = Modifier.width(8.dp))

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = MechanicalOrange
              ) {
                Text(
                  text = "OUVRIR",
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 9.5.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
              }
            }
          }
        }

        // Section Title
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "ORDRES DE RÉPARATION & DEVIS EN COURS (${uiState.repairOrders.size})",
              style = MaterialTheme.typography.labelMedium.copy(
                color = AluminumMuted,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
              )
            )

            TextButton(
              onClick = { viewModel.toggleNewQuoteDialog(true) },
              contentPadding = PaddingValues(0.dp)
            ) {
              Text(
                text = "+ Nouveau Devis",
                color = PerformanceRedGlow,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
        }

        // List of Repair Orders
        items(uiState.repairOrders) { order ->
          RepairOrderCard(
            order = order,
            onViewDetails = { selectedOrderForDetails = order },
            onOpenChecklistAndBulbs = { selectedOrderForChecklist = order },
            onSignOrder = { showSignatureDialogForOrder = order }
          )
        }
      }

      WorkshopTab.COMPTES_CLIENTS -> {
        // --- NOUVEAU SYSTÈME : COMPTES CLIENTS ATELIER GRATUITS & ACCÈS QR CODE ---
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MetalCard),
            border = BorderStroke(1.5.dp, TechCyan.copy(alpha = 0.5f))
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = TechCyan, modifier = Modifier.size(22.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = "ESPACE CLIENTS ATELIER",
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = PureWhite)
                    )
                    Text(
                      text = "Accès gratuit & sécurisé (Matricule + Mot de passe / QR)",
                      style = MaterialTheme.typography.bodySmall.copy(color = ElectricGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    )
                  }
                }

                Button(
                  onClick = { viewModel.openCreateClientAccountDialog() },
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed, contentColor = PureWhite),
                  modifier = Modifier.testTag("btn_create_workshop_client_account")
                ) {
                  Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(text = "CRÉER COMPTE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
              }

              Text(
                text = "Permet à vos clients de suivre en direct l'avancement de leur réparation, consulter et imprimer leurs factures et devis officiels en scannant leur QR Code à l'arrivée.",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
              )
            }
          }
        }

        // Section Title & Counter
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "COMPTES CLIENTS ENREGISTRÉS (${uiState.workshopClientAccounts.size})",
              style = MaterialTheme.typography.labelMedium.copy(
                color = AluminumMuted,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
              )
            )

            TextButton(
              onClick = { viewModel.toggleUniversalQrScanner(true) }
            ) {
              Icon(Icons.Default.QrCode, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Scanner QR Arrivée",
                color = TechCyan,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
        }

        // List of Client Accounts
        items(uiState.workshopClientAccounts) { clientAcc ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MetalCard),
            border = BorderStroke(1.dp, MetalBorder)
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                      .background(GraphiteBlack)
                      .border(1.dp, TechCyan, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(text = clientAcc.clientName, style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold))
                    Text(text = "${clientAcc.vehicleModel} • ${clientAcc.clientPhone}", style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp))
                  }
                }

                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, PerformanceRedGlow)
                ) {
                  Text(
                    text = clientAcc.plateNumber,
                    style = MaterialTheme.typography.labelMedium.copy(
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }

              Divider(color = MetalBorder)

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Lock, contentDescription = null, tint = AluminumMuted, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Mot de passe : ${clientAcc.passwordPin}",
                    style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                  )
                }

                clientAcc.activeRepairOrder?.let { activeOrder ->
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(activeOrder.status.colorHex).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, Color(activeOrder.status.colorHex))
                  ) {
                    Text(
                      text = activeOrder.status.label,
                      style = MaterialTheme.typography.labelSmall.copy(color = Color(activeOrder.status.colorHex), fontWeight = FontWeight.Bold),
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                MekanikSecondaryButton(
                  text = "ESPACE CLIENT (VUE)",
                  onClick = { viewModel.openClientPortal(clientAcc) },
                  icon = Icons.Default.Visibility,
                  modifier = Modifier.weight(1.3f).testTag("btn_open_portal_${clientAcc.plateNumber}")
                )

                OutlinedButton(
                  onClick = {
                    viewModel.openClientPortal(clientAcc)
                  },
                  shape = RoundedCornerShape(10.dp),
                  border = BorderStroke(1.dp, TechCyan),
                  modifier = Modifier.weight(0.9f)
                ) {
                  Icon(Icons.Default.QrCode, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("QR CODE", color = TechCyan, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
              }
            }
          }
        }
      }

      WorkshopTab.SOUS_COMPTES_ULTRA_PRO -> {
        // En-tête Organisation Ultra Pro & Gestion Équipes
        item {
          Card(
            modifier = Modifier.fillMaxWidth().testTag("ultra_pro_workshop_header"),
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
                      .size(38.dp)
                      .clip(CircleShape)
                      .background(PerformanceRed),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.CorporateFare, contentDescription = null, tint = PureWhite, modifier = Modifier.size(22.dp))
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = "COMPTE ULTRA PRO MULTI-ÉQUIPES",
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = PureWhite)
                    )
                    Text(
                      text = "38 000 DA / an • Multi-Comptes Atelier, Magasin, Vente, Compta & Workflow Pièces",
                      style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = PerformanceRedDark,
                  border = BorderStroke(1.dp, PerformanceRed)
                ) {
                  Text(
                    text = "${uiState.ultraProSubAccounts.size} / ${uiState.ultraProOrganization.maxSubAccountsAllowed} ACTIFS",
                    style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }
              }

              Text(
                text = "Structure d'entreprise intégrée : Gérant (Admin Maître), Chef Conseiller Client (Team Leader) & Conseillers, Chef d'Atelier & 5 Mécaniciens d'atelier (recherche pièces), Chef Magasinier & Magasiniers (stock/sortie directe sur facture & OR), Comptables (analyse factures) et Auditeur qualité.",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.5.sp)
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Button(
                  onClick = { viewModel.openUltraProManager() },
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                  modifier = Modifier.weight(1.2f).testTag("btn_open_ultra_pro_manager")
                ) {
                  Icon(Icons.Default.ManageAccounts, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("GÉRER LES FLUX & COMPTES", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }

                OutlinedButton(
                  onClick = { viewModel.openPurchaseExtensionDialog() },
                  shape = RoundedCornerShape(10.dp),
                  border = BorderStroke(1.dp, TechCyan),
                  modifier = Modifier.weight(1f).testTag("btn_buy_extension_workshop")
                ) {
                  Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = TechCyan, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("+2 COMPTES (2 500 DA)", color = TechCyan, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp))
                }
              }
            }
          }
        }

        // Sub-Accounts List
        items(uiState.ultraProSubAccounts) { subAcc ->
          val isSimulated = uiState.activeImpersonatedSubAccount?.id == subAcc.id
          Card(
            modifier = Modifier.fillMaxWidth().testTag("workshop_sub_account_${subAcc.id}"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isSimulated) Color(0xFF1F241C) else MetalCard
            ),
            border = BorderStroke(
              if (isSimulated) 1.5.dp else 1.dp,
              if (isSimulated) Color(subAcc.role.badgeColorHex) else MetalBorder
            )
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                      .background(Color(subAcc.role.badgeColorHex).copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = subAcc.avatarInitials,
                      style = MaterialTheme.typography.labelMedium.copy(
                        color = Color(subAcc.role.badgeColorHex),
                        fontWeight = FontWeight.Black
                      )
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = subAcc.fullName,
                        style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold)
                      )
                      if (isSimulated) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(4.dp), color = ElectricGreenDark) {
                          Text(
                            text = "ACTIF",
                            style = MaterialTheme.typography.labelSmall.copy(color = ElectricGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                          )
                        }
                      }
                    }
                    Text(
                      text = "${subAcc.role.label} • PIN : ${subAcc.accessPin}",
                      style = MaterialTheme.typography.bodySmall.copy(color = Color(subAcc.role.badgeColorHex), fontSize = 11.sp)
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(0.5.dp, Color(subAcc.role.badgeColorHex))
                ) {
                  Text(
                    text = subAcc.role.shortLabel,
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(subAcc.role.badgeColorHex), fontSize = 9.5.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                  )
                }
              }

              Text(
                text = subAcc.role.description,
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
              )

              Divider(color = MetalBorder, thickness = 0.5.dp)

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Dernière action : ${subAcc.lastActive}",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 10.5.sp)
                )

                if (subAcc.role != UltraProSubAccountRole.GERANT_PRINCIPAL) {
                  OutlinedButton(
                    onClick = {
                      if (isSimulated) viewModel.switchActiveSubAccount(null) else viewModel.switchActiveSubAccount(subAcc)
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(subAcc.role.badgeColorHex)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                  ) {
                    Icon(
                      if (isSimulated) Icons.Default.Restore else Icons.Default.PlayArrow,
                      contentDescription = null,
                      tint = Color(subAcc.role.badgeColorHex),
                      modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = if (isSimulated) "QUITTER" else "SE CONNECTER",
                      color = Color(subAcc.role.badgeColorHex),
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      WorkshopTab.HISTORIQUE_PANNES_CACHE -> {
        item {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MekanikCardSurface,
            border = BorderStroke(1.dp, MetalBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Text(
                text = "HISTORIQUE CLIENT, PANNES DES CLIENTS & CACHE LOCAL (0ms)",
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "Consultez l'historique complet des pannes par véhicule/client, les diagnostics OBD-II/IA enregistrés et les schémas techniques mis en cache local SQLite Room pour une continuité totale même sans réseau en atelier.",
                color = AluminumGray,
                fontSize = 12.sp
              )
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                MekanikPrimaryButton(
                  text = "Ouvrir l'Espace Complet (${clientFaultHistory.size} Pannes • ${savedDiagnostics.size} Diags)",
                  icon = Icons.Default.Storage,
                  onClick = { viewModel.openOfflineCacheHubModal(OfflineCacheHubTab.CLIENT_FAULTS_HISTORY) },
                  modifier = Modifier.fillMaxWidth().testTag("btn_open_full_offline_cache_hub_from_workshop")
                )
              }
            }
          }
        }

        // Aperçu direct des Pannes Clients récentes
        items(clientFaultHistory) { fault ->
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MekanikCardSurface,
            border = BorderStroke(1.dp, MetalBorder),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.openOfflineCacheHubModal(OfflineCacheHubTab.CLIENT_FAULTS_HISTORY) }
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${fault.clientName} • ${fault.licensePlate}",
                  color = PureWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.5.sp
                )
                Text(
                  text = fault.linkedDtcCodes,
                  color = MekanikBrandOrange,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.5.sp
                )
              }
              Text(
                text = "Panne : ${fault.faultTitle} (${fault.vehicleModel})",
                color = OffWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
              )
              Text(
                text = "Diagnostic : ${fault.diagnosticConclusion}",
                color = AluminumGray,
                fontSize = 11.5.sp
              )
              Text(
                text = "Pièces : ${fault.partsReplaced} • ${fault.totalCostDzd} DA",
                color = MekanikValidationGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp
              )
            }
          }
        }
      }

      WorkshopTab.STATISTIQUES -> {
        // Full Interactive Recharts Analytics (Mechanic Productivity, Garantie/Cession/Client & Companion Time-Clock)
        item {
          WorkshopRechartsView(
            companionClocks = uiState.companionClockEntries,
            multiPayerOrders = uiState.multiPayerSplitOrders,
            onUpdateCompanionClockStatus = { id, status, deltaCth ->
              viewModel.updateCompanionClockStatus(id, status, deltaCth)
            },
            onAddCompanionClockEntry = { name, mat, spec, orNum, veh, opTitle, tier, baremeCth, bay ->
              viewModel.addCompanionClockEntry(name, mat, spec, orNum, veh, opTitle, tier, baremeCth, bay)
            },
            onUpdateMultiPayerSplit = { id, cPct, wPct, iPct ->
              viewModel.updateMultiPayerSplit(id, cPct, wPct, iPct)
            },
            onOpenNewQuote = { viewModel.toggleNewQuoteDialog(true) },
            onExportReport = {
              viewModel.showToastNotification("Rapport analytique Recharts (Productivité & Garantie/Cession/Client) généré avec succès.")
            }
          )
        }
      }
    }
  }

  // Dialog to Book New Online Appointment
  if (uiState.showBookAppointmentDialog) {
    BookAppointmentDialog(
      activeVehicle = uiState.activeVehicle,
      onDismiss = { viewModel.toggleBookAppointmentDialog(false) },
      onConfirm = { clientName, clientPhone, vehicleModel, plate, serviceType, date, timeSlot, notes ->
        viewModel.bookAppointment(
          clientName,
          clientPhone,
          vehicleModel,
          plate,
          serviceType,
          date,
          timeSlot,
          notes
        )
      }
    )
  }

  // Workshop Quote Generator Component with Extraction, Manual Labor, DZD Calculations & PDF Preview
  if (uiState.showNewQuoteDialog) {
    WorkshopQuoteGeneratorModal(
      activeVehicle = uiState.activeVehicle,
      allAvailableParts = uiState.partsForSelectedCategory,
      preSelectedPart = uiState.selectedPart,
      initialClientName = uiState.prefilledClientName,
      initialClientPhone = uiState.prefilledClientPhone,
      initialPlateNumber = uiState.prefilledPlateNumber,
      initialMileage = uiState.prefilledMileage,
      initialNotes = uiState.prefilledNotes,
      onOpenVoiceDictation = { viewModel.openVoiceDictation() },
      onDismiss = { viewModel.toggleNewQuoteDialog(false) },
      onConfirmQuote = { clientName, clientPhone, plate, laborHours, items, notes ->
        viewModel.createRepairOrder(clientName, clientPhone, plate, laborHours, items)
      },
      onDownloadPdf = { quoteNumber, cName, cPhone, cAddr, plate, vModel, vMileage, qNotes, items, lHours, lRate, diagCost, suppCost, tva, stamp ->
        viewModel.generateAndDownloadWorkshopQuotePdf(
          context = context,
          quoteNumber = quoteNumber,
          clientName = cName,
          clientPhone = cPhone,
          clientAddress = cAddr,
          plateNumber = plate,
          vehicleModel = vModel,
          vehicleMileage = vMileage,
          quoteNotes = qNotes,
          items = items,
          laborHours = lHours,
          laborRatePerHour = lRate,
          diagnosticCostDzd = diagCost,
          suppliesCostDzd = suppCost,
          applyTva19 = tva,
          fiscalStampDzd = stamp
        )
      }
    )
  }

  // Modal Création Compte Client Atelier
  if (uiState.showCreateClientAccountModal) {
    CreateWorkshopClientAccountModal(
      initialPlateNumber = uiState.prefilledPlateNumber,
      initialClientName = uiState.prefilledClientName,
      initialClientPhone = uiState.prefilledClientPhone,
      initialVehicleModel = uiState.prefilledVehicleModel,
      initialVin = uiState.prefilledVin,
      onCreateAccount = { newAcc ->
        viewModel.addNewClientAccount(newAcc)
      },
      onDismiss = { viewModel.closeCreateClientAccountDialog() }
    )
  }

  // Modal Espace Client Atelier (Portail Gratuit en Lecture & Impression)
  if (uiState.showWorkshopClientPortalModal && uiState.activeClientPortalAccount != null) {
    WorkshopClientPortalModal(
      account = uiState.activeClientPortalAccount!!,
      onDismiss = { viewModel.closeClientPortal() }
    )
  }

  // Modal Dictée Vocale des Pannes
  if (uiState.showVoiceDictationModal) {
    VoiceDictationModal(
      initialText = uiState.prefilledNotes,
      onApplyDictation = { text ->
        viewModel.applyVoiceDictation(text)
      },
      onDismiss = { viewModel.closeVoiceDictation() }
    )
  }

  // Dialog for Digital Signature
  if (showSignatureDialogForOrder != null) {
    val order = showSignatureDialogForOrder!!
    SignatureDialog(
      orderNumber = order.orderNumber,
      clientName = order.clientName,
      totalDzd = order.netTotalDzd,
      onDismiss = { showSignatureDialogForOrder = null },
      onSign = { signName ->
        viewModel.signRepairOrder(order.id, signName)
        showSignatureDialogForOrder = null
      }
    )
  }

  // Detail Modal for Order
  if (selectedOrderForDetails != null) {
    val order = selectedOrderForDetails!!
    OrderDetailModal(
      order = order,
      onDismiss = { selectedOrderForDetails = null },
      onOpenChecklistAndBulbs = {
        selectedOrderForChecklist = order
        selectedOrderForDetails = null
      },
      onSign = {
        showSignatureDialogForOrder = order
        selectedOrderForDetails = null
      },
      onDownloadPdf = {
        viewModel.generateAndDownloadRepairOrderPdf(context, order)
      }
    )
  }

  // Checklist OR & 22 Ampoules + État Intérieur/Extérieur Modal
  if (selectedOrderForChecklist != null) {
    val order = selectedOrderForChecklist!!
    RepairOrderChecklistModal(
      order = order,
      onSaveUpdatedOrder = { updated ->
        viewModel.updateRepairOrder(updated)
        selectedOrderForChecklist = null
      },
      onDismiss = { selectedOrderForChecklist = null }
    )
  }

  // Detail Modal for Appointment
  if (selectedAptForDetails != null) {
    val apt = selectedAptForDetails!!
    AppointmentDetailModal(
      appointment = apt,
      onDismiss = { selectedAptForDetails = null },
      onConfirm = {
        viewModel.updateAppointmentStatus(apt.id, AppointmentStatus.CONFIRME)
        selectedAptForDetails = null
      },
      onConvertToOrder = {
        viewModel.convertAppointmentToRepairOrder(apt)
        selectedAptForDetails = null
      }
    )
  }
}

@Composable
private fun AppointmentCard(
  appointment: WorkshopAppointment,
  onConfirm: () -> Unit,
  onConvertToOrder: () -> Unit,
  onViewDetails: () -> Unit
) {
  val statusColor = Color(appointment.status.colorHex)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onViewDetails)
      .testTag("appointment_${appointment.id}"),
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
          Icon(
            Icons.Default.CalendarMonth,
            contentDescription = null,
            tint = PerformanceRed,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${appointment.appointmentDate} • ${appointment.timeSlot}",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = PureWhite,
              fontSize = 13.5.sp
            )
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = statusColor.copy(alpha = 0.2f),
          border = BorderStroke(0.5.dp, statusColor)
        ) {
          Text(
            text = appointment.status.label,
            style = MaterialTheme.typography.labelSmall.copy(
              color = statusColor,
              fontWeight = FontWeight.Bold,
              fontSize = 9.5.sp
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = appointment.serviceType.label,
        style = MaterialTheme.typography.bodyLarge.copy(
          fontWeight = FontWeight.Black,
          color = OffWhite,
          fontSize = 14.5.sp
        )
      )

      Text(
        text = "${appointment.vehicleModel} • Immat : ${appointment.plateNumber}",
        style = MaterialTheme.typography.bodyMedium.copy(
          color = AluminumLight,
          fontSize = 12.sp
        )
      )

      Text(
        text = "Client : ${appointment.clientName} (${appointment.clientPhone}) • Tech : ${appointment.assignedMechanic}",
        style = MaterialTheme.typography.bodySmall.copy(
          color = AluminumGray,
          fontSize = 11.sp
        )
      )

      if (appointment.clientNotes.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Note : « ${appointment.clientNotes} »",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MechanicalOrange,
            fontSize = 11.sp
          )
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Divider(color = MetalBorder, thickness = 0.5.dp)

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Est. ${appointment.estimatedCostDzd} DA (${appointment.estimatedDurationHours}h)",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = TechCyan,
            fontSize = 13.5.sp
          )
        )

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          if (appointment.status == AppointmentStatus.EN_ATTENTE) {
            Button(
              onClick = onConfirm,
              colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.height(32.dp)
            ) {
              Text(
                text = "Confirmer RDV",
                color = GraphiteBlack,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
              )
            }
          }

          if (appointment.status == AppointmentStatus.CONFIRME) {
            Button(
              onClick = onConvertToOrder,
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.height(32.dp)
            ) {
              Text(
                text = "Créer Devis/OR",
                color = PureWhite,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun BookAppointmentDialog(
  activeVehicle: Vehicle,
  onDismiss: () -> Unit,
  onConfirm: (String, String, String, String, ServiceType, String, String, String) -> Unit
) {
  var clientName by remember { mutableStateOf("") }
  var clientPhone by remember { mutableStateOf("") }
  var plateNumber by remember { mutableStateOf("04291-121-16") }
  var selectedService by remember { mutableStateOf(ServiceType.VIDANGE_FILTRES) }
  var selectedDate by remember { mutableStateOf("19 Août 2026") }
  var selectedTimeSlot by remember { mutableStateOf("10:00 - 11:30") }
  var notes by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "RÉSERVATION DE RENDEZ-VOUS EN LIGNE",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Black,
          color = PureWhite
        )
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 440.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "Véhicule : ${activeVehicle.brand} ${activeVehicle.model} (${activeVehicle.displacement})",
          style = MaterialTheme.typography.bodySmall.copy(color = PerformanceRedGlow, fontWeight = FontWeight.Bold)
        )

        OutlinedTextField(
          value = clientName,
          onValueChange = { clientName = it },
          label = { Text("Nom et Prénom du client") },
          placeholder = { Text("Ex: Karim Meziane") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = clientPhone,
          onValueChange = { clientPhone = it },
          label = { Text("Numéro de Téléphone") },
          placeholder = { Text("Ex: 0550 12 34 56") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = plateNumber,
          onValueChange = { plateNumber = it },
          label = { Text("Matricule / Immatriculation") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Text(
          text = "PRESTATION SOUHAITÉE :",
          style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold)
        )

        ServiceType.entries.forEach { service ->
          val isSelected = service == selectedService
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) GraphiteBlack else MetalSurface,
            border = BorderStroke(1.dp, if (isSelected) PerformanceRed else MetalBorder),
            onClick = { selectedService = service },
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = service.label,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = if (isSelected) PureWhite else OffWhite,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 11.5.sp
                ),
                modifier = Modifier.weight(1f)
              )
              Text(
                text = "${service.defaultPriceDzd} DA",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = TechCyan,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.5.sp
                )
              )
            }
          }
        }

        OutlinedTextField(
          value = selectedDate,
          onValueChange = { selectedDate = it },
          label = { Text("Date du rendez-vous") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = selectedTimeSlot,
          onValueChange = { selectedTimeSlot = it },
          label = { Text("Créneau horaire") },
          placeholder = { Text("Ex: 09:00 - 10:30, 14:00 - 15:30") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Observations particulières") },
          placeholder = { Text("Ex: voyant clignote, bruit suspect...") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      MekanikPrimaryButton(
        text = "ENREGISTRER LA RÉSERVATION",
        onClick = {
          onConfirm(
            clientName,
            clientPhone,
            "${activeVehicle.brand} ${activeVehicle.model}",
            plateNumber,
            selectedService,
            selectedDate,
            selectedTimeSlot,
            notes
          )
        }
      )
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Annuler", color = AluminumGray)
      }
    },
    containerColor = MetalCard
  )
}

@Composable
private fun AppointmentDetailModal(
  appointment: WorkshopAppointment,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
  onConvertToOrder: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "DÉTAILS DU RENDEZ-VOUS CLIENT",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Black,
          color = PureWhite
        )
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = "Client : ${appointment.clientName} (${appointment.clientPhone})",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite)
        )
        Text(
          text = "Véhicule : ${appointment.vehicleModel} • Matricule : ${appointment.plateNumber}",
          style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight)
        )
        Text(
          text = "Date & Heure : ${appointment.appointmentDate} à ${appointment.timeSlot}",
          style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Prestation : ${appointment.serviceType.label}",
          style = MaterialTheme.typography.bodySmall.copy(color = MechanicalOrange, fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Tarif estimé : ${appointment.estimatedCostDzd} DA (Durée : ${appointment.estimatedDurationHours}h)",
          style = MaterialTheme.typography.bodySmall.copy(color = PureWhite)
        )
        if (appointment.clientNotes.isNotBlank()) {
          Text(
            text = "Remarques : « ${appointment.clientNotes} »",
            style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
          )
        }
      }
    },
    confirmButton = {
      if (appointment.status == AppointmentStatus.EN_ATTENTE) {
        MekanikPrimaryButton(
          text = "CONFIRMER CE RENDEZ-VOUS",
          onClick = onConfirm
        )
      } else {
        MekanikPrimaryButton(
          text = "CONVERTIR EN ORDRE DE RÉPARATION",
          onClick = onConvertToOrder
        )
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Fermer", color = AluminumGray)
      }
    },
    containerColor = MetalCard
  )
}

@Composable
private fun RepairOrderCard(
  order: RepairOrder,
  onViewDetails: () -> Unit,
  onOpenChecklistAndBulbs: () -> Unit,
  onSignOrder: () -> Unit
) {
  val statusColor = Color(order.status.colorHex)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onViewDetails)
      .testTag("repair_order_${order.id}"),
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
          Text(
            text = order.orderNumber,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Black,
              color = PureWhite,
              fontFamily = FontFamily.Monospace
            )
          )
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = statusColor.copy(alpha = 0.2f),
            border = BorderStroke(0.5.dp, statusColor)
          ) {
            Text(
              text = order.status.label,
              style = MaterialTheme.typography.labelSmall.copy(
                color = statusColor,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              ),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Text(
          text = "${order.netTotalDzd} DA",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Black,
            color = PerformanceRedGlow,
            fontSize = 16.sp
          )
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "${order.vehicleModel} • Immat : ${order.plateNumber}",
        style = MaterialTheme.typography.bodyMedium.copy(
          fontWeight = FontWeight.SemiBold,
          color = OffWhite
        )
      )

      Text(
        text = "Client : ${order.clientName} (${order.clientPhone}) • Date : ${order.date}",
        style = MaterialTheme.typography.bodySmall.copy(
          color = AluminumGray,
          fontSize = 11.5.sp
        )
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Résumé Checklist OR : État Intérieur/Extérieur (Propre/Sale) + Contrôle Ampoules
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = GraphiteBlack,
        border = BorderStroke(0.8.dp, MechanicalOrange.copy(alpha = 0.6f)),
        onClick = onOpenChecklistAndBulbs,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("btn_card_checklist_${order.id}")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "💡 Ampoules : ${order.checklist.okBulbsCount}/${order.checklist.bulbs.size} OK • ${order.checklist.replacedBulbs.size} remplacée(s) • ${order.checklist.burntNotReplacedBulbs.size} grillée(s)",
              style = MaterialTheme.typography.labelSmall.copy(
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            )
            Text(
              text = "🚗 Extérieur : ${order.checklist.exteriorCleanliness.shortLabel} • Intérieur : ${order.checklist.interiorCleanliness.shortLabel}",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MechanicalOrange,
                fontSize = 9.5.sp
              )
            )
          }
          Text(
            text = "CHECKLIST ›",
            style = MaterialTheme.typography.labelSmall.copy(
              color = TechCyan,
              fontWeight = FontWeight.Black,
              fontSize = 9.5.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            if (order.isSigned) Icons.Default.CheckCircle else Icons.Default.Draw,
            contentDescription = null,
            tint = if (order.isSigned) ElectricGreen else AluminumMuted,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (order.isSigned) "Signé (${order.signatureName})" else "En attente signature client",
            style = MaterialTheme.typography.bodySmall.copy(
              color = if (order.isSigned) ElectricGreen else AluminumMuted,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          )
        }

        TextButton(
          onClick = onViewDetails,
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Text(
            text = "Détails & Impression",
            color = MechanicalOrange,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    }
  }
}

@Composable
private fun SignatureDialog(
  orderNumber: String,
  clientName: String,
  totalDzd: Int,
  onDismiss: () -> Unit,
  onSign: (String) -> Unit
) {
  var signerName by remember { mutableStateOf(clientName) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "SIGNATURE NUMÉRIQUE DU CLIENT",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Black,
          color = PureWhite
        )
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "Devis $orderNumber — Montant Net TTC : $totalDzd DZD",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = PerformanceRedGlow,
            fontWeight = FontWeight.Bold
          )
        )
        Text(
          text = "Le client certifie accepter l'ordre de réparation et les tarifs pièces/main d'œuvre spécifiés.",
          style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
        )

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = GraphiteBlack,
          border = BorderStroke(1.dp, MetalBorder),
          modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = "Zone de signature tactile / tablette",
              style = MaterialTheme.typography.bodySmall.copy(
                color = AluminumMuted,
                fontFamily = FontFamily.Cursive,
                fontSize = 16.sp
              )
            )
          }
        }

        OutlinedTextField(
          value = signerName,
          onValueChange = { signerName = it },
          label = { Text("Nom du signataire") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      MekanikPrimaryButton(
        text = "VALIDER LA SIGNATURE",
        onClick = { onSign(signerName) }
      )
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Annuler", color = AluminumGray)
      }
    },
    containerColor = MetalCard
  )
}

@Composable
private fun OrderDetailModal(
  order: RepairOrder,
  onDismiss: () -> Unit,
  onOpenChecklistAndBulbs: () -> Unit,
  onSign: () -> Unit,
  onDownloadPdf: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "FICHE DEVIS / FACTURE PRO",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Black,
            color = PureWhite
          )
        )
        Text(
          text = order.orderNumber,
          style = MaterialTheme.typography.bodySmall.copy(
            color = PerformanceRed,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
          )
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = "Client : ${order.clientName} (${order.clientPhone})",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = OffWhite)
        )
        Text(
          text = "Véhicule : ${order.vehicleModel} • VIN : ${order.vin}",
          style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
        )

        Divider(color = MetalBorder, thickness = 1.dp)

        // Résumé Checklist OR & Ampoules dans la fiche détail
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = GraphiteBlack,
          border = BorderStroke(1.dp, MechanicalOrange),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "CHECKLIST D'ORDRE DE RÉPARATION & AMPOULES",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MechanicalOrange,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp
              )
            )
            Text(
              text = "• État Extérieur : ${order.checklist.exteriorCleanliness.shortLabel} | Intérieur : ${order.checklist.interiorCleanliness.shortLabel}",
              style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 10.5.sp)
            )
            Text(
              text = "• Contrôle des ${order.checklist.bulbs.size} ampoules : ${order.checklist.okBulbsCount} OK, ${order.checklist.replacedBulbs.size} remplacée(s), ${order.checklist.burntNotReplacedBulbs.size} grillée(s) à prévenir",
              style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 10.5.sp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Button(
              onClick = onOpenChecklistAndBulbs,
              colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
              shape = RoundedCornerShape(6.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(Icons.Default.Lightbulb, contentDescription = null, tint = PureWhite, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "OUVRIR CHECKLIST AMPOULES & ÉTAT INT./EXT.",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp
              )
            }
          }
        }

        Text(
          text = "DÉTAIL DES PIÈCES & MAIN D'ŒUVRE",
          style = MaterialTheme.typography.labelSmall.copy(
            color = AluminumMuted,
            fontWeight = FontWeight.Bold
          )
        )

        order.items.forEach { item ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "${item.quantity}x ${item.name} (${item.reference})",
              style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 11.sp),
              modifier = Modifier.weight(1f)
            )
            Text(
              text = "${item.priceDzd * item.quantity} DA",
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = AluminumLight,
                fontSize = 11.sp
              )
            )
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Main d'œuvre atelier (${order.laborHours}h à ${order.laborRatePerHourDzd} DA/h)",
            style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 11.sp)
          )
          Text(
            text = "${order.laborTotalDzd} DA",
            style = MaterialTheme.typography.bodySmall.copy(
              fontWeight = FontWeight.Bold,
              color = AluminumLight,
              fontSize = 11.sp
            )
          )
        }

        Divider(color = MetalBorder, thickness = 1.dp)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "TOTAL NET TTC :",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Black,
              color = PureWhite
            )
          )
          Text(
            text = "${order.netTotalDzd} DA",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              color = PerformanceRedGlow
            )
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
          onClick = onDownloadPdf,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("btn_download_order_pdf_in_modal"),
          colors = ButtonDefaults.buttonColors(containerColor = GraphiteBlack),
          border = BorderStroke(1.dp, TechCyan),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("TÉLÉCHARGER LE DEVIS EN PDF", color = TechCyan, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
        }
      }
    },
    confirmButton = {
      if (!order.isSigned) {
        MekanikPrimaryButton(
          text = "SIGNER CE DEVIS",
          onClick = onSign
        )
      } else {
        MekanikSecondaryButton(
          text = "FERMER",
          onClick = onDismiss
        )
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Fermer", color = AluminumGray)
      }
    },
    containerColor = MetalCard
  )
}
