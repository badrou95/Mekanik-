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
import com.example.data.model.*
import com.example.data.repository.MekanikRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainNavigationTab
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  onNavigateToVendors: () -> Unit,
  onNavigateToSubscriptions: () -> Unit,
  onNavigateToDocs: () -> Unit,
  onNavigateToFleet: () -> Unit,
  onNavigateToLiveTracking: () -> Unit,
  onNavigateToObd: () -> Unit,
  onNavigateToDriverSpace: () -> Unit = {},
  onNavigateToCarRental: () -> Unit = {},
  onNavigateToObdScanner: () -> Unit = {},
  onOpenDrawerMenu: () -> Unit = {},
  onOpenSearchByCity: () -> Unit = {},
  onOpenSearchInStore: () -> Unit = {},
  onOpenSearchPart: () -> Unit = {},
  onOpenVendorAddPhotoAi: () -> Unit = {},
  onOpenAutoForum: () -> Unit = {},
  onOpenProSocialNetwork: () -> Unit = {},
  onOpenMonthlyZipArchive: () -> Unit = {},
  onOpenHandsFreeVoiceSearch: () -> Unit = {},
  onOpenMekanikAiObd: (String?) -> Unit = {},
  isRealtimeStreamEnabled: Boolean = true,
  onToggleRealtimeStream: (Boolean) -> Unit = {},
  onTriggerInstantRealtimeEvent: (NotificationCategory) -> Unit = {},
  onOpenNotificationsCenter: () -> Unit = {},
  onStartOnboardingGuide: () -> Unit = {},
  onOpenAccountSelector: () -> Unit = {},
  onOpenAccountAdmin2Fa: () -> Unit = {},
  onOpenSaasPlatformSpace: (SaasSpace) -> Unit = {},
  modifier: Modifier = Modifier
) {
  var showAllAdvancedModules by remember { mutableStateOf(false) }
  var showSingleTapVinEngineModal by remember { mutableStateOf(false) }
  val networkCondition by viewModel.workshopNetworkCondition.collectAsStateWithLifecycle()
  val cacheSummary by viewModel.localCacheSummary.collectAsStateWithLifecycle()

  // Réinitialisation complète de l'état local de l'accueil lors d'un changement de formule de compte
  LaunchedEffect(uiState.currentAccountRole) {
    showAllAdvancedModules = false
    showSingleTapVinEngineModal = false
  }

  val executeSingleTapSearch: (String, Boolean) -> Unit = { queryValue, isEngineCode ->
    viewModel.setSearchTabMode(
      if (isEngineCode) SearchTabMode.CODE_MOTEUR else SearchTabMode.VIN
    )
    viewModel.onSearchQueryChanged(queryValue)
    viewModel.performVehicleSearch()
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 0. BARRE DE RECHERCHE PERSISTANTE (STICKY TOP BAR) VIN & CODE MOTEUR 1-TAP
      PersistentWorkshopVinEngineBar(
        currentQuery = uiState.searchQuery,
        selectedTabMode = uiState.searchTabMode,
        onQueryChange = { viewModel.onSearchQueryChanged(it) },
        onSelectTabMode = { viewModel.setSearchTabMode(it) },
        onTriggerInstantSearch = { viewModel.performVehicleSearch() },
        onTriggerSingleTapPreset = { presetCode, isEngine ->
          executeSingleTapSearch(presetCode, isEngine)
        },
        onOpenCameraOcr = { viewModel.openCarteGriseScanner() },
        onOpenVoiceSearch = onOpenHandsFreeVoiceSearch
      )

      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .background(GraphiteDark)
          .testTag("home_screen_content"),
        contentPadding = PaddingValues(bottom = 104.dp)
      ) {
    // 0.5. BANNIÈRE PLATEFORME WEB SAAS MEKANIK+ (6 ESPACES A-F : PUBLIC, UTILISATEUR, ATELIER, MAGASIN, FOURNISSEUR, ADMIN)
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 2.dp)
      ) {
        MekanikSaasPlatformHomeBanner(
          onOpenSaasPlatform = { space -> onOpenSaasPlatformSpace(space) }
        )
      }
    }

    // 1. BARRE DE RECHERCHE RAPIDE VIN / MOTEUR / VÉHICULE EN HAUT D'ÉCRAN
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 2.dp)
      ) {
        VinSearchBar(
          currentQuery = uiState.searchQuery,
          onQueryChange = { viewModel.onSearchQueryChanged(it) },
          onPerformSearch = { viewModel.performVehicleSearch() },
          selectedTab = uiState.searchTabMode,
          onTabSelected = { viewModel.setSearchTabMode(it) },
          onQuickChipClicked = { chipValue ->
            viewModel.onSearchQueryChanged(chipValue)
            viewModel.performVehicleSearch()
          },
          onScanCarteGriseVin = { viewModel.openCarteGriseScanner() },
          apiDataSourceMode = uiState.apiDataSourceMode,
          onOpenDevApiManager = { viewModel.openDevApiManagerModal() }
        )
      }
    }

    // 1.2. TABLEAU DE BORD MODULAIRE EN GRILLE 2x2 (Réservé exclusivement au Compte Atelier Pro)
    if (uiState.currentAccountRole.isWorkshopAccount) {
      item {
        ModularQuickAccessGridDashboard(
          activeVehicle = uiState.activeVehicle,
          currentVinQuery = uiState.searchQuery,
          dailyVinCount = uiState.dailyVinCount,
          dailyVinLimit = uiState.dailyVinLimit,
          repairOrders = uiState.repairOrders,
          dailyMaintenanceTasks = uiState.dailyMaintenanceTasks,
          invoicesList = uiState.invoicesList,
          onVinSearchAction = { viewModel.performVehicleSearch() },
          onScanVinOcrAction = { viewModel.openCarteGriseScanner() },
          onPendingRepairsAction = { viewModel.setNavigationTab(MainNavigationTab.ATELIER) },
          onActiveQuotesAction = { viewModel.setNavigationTab(MainNavigationTab.ATELIER) },
          onCreateNewQuoteAction = { viewModel.toggleNewQuoteDialog(true) },
          onDailySalesKpiAction = { onOpenMonthlyZipArchive() }
        )
      }

      // 1.5. STRATÉGIE DE CACHE LOCAL ATELIER (SCHÉMAS 0ms, PANNES CLIENTS & DIAGNOSTICS ENREGISTRÉS - Réservé Atelier)
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
          WorkshopOfflineCacheStatusBanner(
            networkCondition = networkCondition,
            cacheSummary = cacheSummary,
            onOpenCacheHub = { tab -> viewModel.openOfflineCacheHubModal(tab) },
            onCycleNetworkCondition = { viewModel.cycleWorkshopNetworkCondition() }
          )
        }
      }
    }

    // 1.6. OUTILS DMS MÉCANICAUTO+ FILTRÉS PAR FORMULE DE COMPTE
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Card 1: Scanner Vision Étiquette Pièce (Uniquement pour Compte Atelier Pro & Compte Magasin de Pièces)
        if (uiState.currentAccountRole.isWorkshopAccount || uiState.currentAccountRole.isPartsStoreAccount) {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MetalCard,
            border = BorderStroke(1.2.dp, MekanikBrandOrange),
            onClick = { viewModel.openPartLabelVisionScanner() },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("home_card_part_label_vision_scanner")
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
              ) {
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = MekanikBrandOrange.copy(alpha = 0.18f),
                  border = BorderStroke(1.dp, MekanikBrandOrange),
                  modifier = Modifier.size(44.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Default.QrCodeScanner,
                      contentDescription = null,
                      tint = MekanikBrandOrange,
                      modifier = Modifier.size(24.dp)
                    )
                  }
                }
                Column {
                  Text(
                    text = "SCANNER ÉTIQUETTE PIÈCE (CODE-BARRES / QR)",
                    color = OffWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.5.sp
                  )
                  Text(
                    text = "Réf. catalogue • Compatibilité N° Châssis • Réfs montables & Méthodes d'installation",
                    color = AluminumGray,
                    fontSize = 11.sp
                  )
                }
              }
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MekanikBrandOrange
              ) {
                Text(
                  text = "SCANNER",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                )
              }
            }
          }
        }

        // Card 2: MécaVerif+™ (Audit Historique Véhicule, Carrosserie, Mécanicien & Kilométrage Réel)
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MetalCard,
          border = BorderStroke(1.2.dp, MekanikValidationGreen.copy(alpha = 0.7f)),
          onClick = { viewModel.setNavigationTab(MainNavigationTab.MECA_VERIF) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("home_card_mecaverif_history")
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              modifier = Modifier.weight(1f)
            ) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MekanikValidationGreen.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, MekanikValidationGreen),
                modifier = Modifier.size(44.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = MekanikValidationGreen,
                    modifier = Modifier.size(24.dp)
                  )
                }
              }
              Column {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = "MÉCAVERIF+™ • HISTORIQUE VÉHICULE PAR VIN",
                    color = OffWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.5.sp
                  )
                  Surface(
                    shape = RoundedCornerShape(5.dp),
                    color = MekanikBrandOrange.copy(alpha = 0.2f)
                  ) {
                    Text(
                      text = "EXTERNE • ${uiState.currentAccountRole.mecaVerifExternalRateDzd} DA / VIN",
                      color = MekanikBrandOrange,
                      fontWeight = FontWeight.Bold,
                      fontSize = 8.5.sp,
                      modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = "Hors abonnement (${uiState.currentAccountRole.mecaVerifExternalRateDzd} DA/châssis) : Graphique kilométrique, photos tôle Avant/Après & passages ateliers",
                  color = AluminumGray,
                  fontSize = 11.sp
                )
              }
            }
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MekanikValidationGreen
            ) {
              Text(
                text = "VÉRIFIER",
                color = Color(0xFF101820),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
              )
            }
          }
        }
      }
    }

    // 2. TABLEAU DE BORD PRINCIPAL MODERNE & ÉPURÉ
    item {
      SimplifiedModernHomeDashboard(
        currentRole = uiState.currentAccountRole,
        onSelectRole = { viewModel.switchAccountRole(it) },
        onOpenDrawerMenu = onOpenDrawerMenu,
        onOpenSearchByCity = onOpenSearchByCity,
        onOpenSearchInStore = onOpenSearchInStore,
        onOpenSearchPart = onOpenSearchPart,
        onOpenVendorAddPhotoAi = onOpenVendorAddPhotoAi,
        onOpenAutoForum = onOpenAutoForum,
        onOpenProSocialNetwork = onOpenProSocialNetwork,
        onOpenMonthlyZipArchive = onOpenMonthlyZipArchive,
        onOpenRepairOrder = { viewModel.setNavigationTab(MainNavigationTab.ATELIER) },
        onOpenTechnicalDiagrams = { viewModel.openWebOemCatalogsModal() },
        onOpenSparePartsCatalog = { viewModel.setNavigationTab(MainNavigationTab.PIECES) },
        onOpenPartVendors = onNavigateToVendors,
        onOpenFleetAndRental = onNavigateToCarRental,
        onOpenHandsFreeVoiceSearch = onOpenHandsFreeVoiceSearch,
        onOpenMekanikAiObd = onOpenMekanikAiObd,
        isRealtimeStreamEnabled = isRealtimeStreamEnabled,
        unreadNotificationsCount = uiState.unreadNotificationsCount,
        onToggleRealtimeStream = onToggleRealtimeStream,
        onTriggerInstantRealtimeEvent = onTriggerInstantRealtimeEvent,
        onOpenNotificationsCenter = onOpenNotificationsCenter,
        onStartOnboardingGuide = onStartOnboardingGuide,
        onOpenAccountSelector = onOpenAccountSelector,
        onOpenAccountAdmin2Fa = onOpenAccountAdmin2Fa,
        onOpenSuperAdminPortal = { viewModel.openSuperAdminModal(7) },
        onOpenMecaVerifHistory = { viewModel.setNavigationTab(MainNavigationTab.MECA_VERIF) }
      )
    }

    // 3. SECTION REPLIABLE DES MODULES AVANCÉS ATELIER PRO (Uniquement pour Compte Atelier Pro)
    if (uiState.currentAccountRole.isWorkshopAccount) {
      item {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { showAllAdvancedModules = !showAllAdvancedModules }
            .testTag("toggle_advanced_home_modules_btn"),
          shape = RoundedCornerShape(14.dp),
          color = MetalCard,
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(GraphiteDark)
                  .border(1.dp, MetalBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Tune,
                  contentDescription = null,
                  tint = MekanikBrandOrange,
                  modifier = Modifier.size(20.dp)
                )
              }
              Column {
                Text(
                  text = if (showAllAdvancedModules) "Replier les modules avancés Atelier Pro & Admin"
                  else "Modules Avancés Atelier Pro, Stock WMS, Flotte & Admin",
                  color = OffWhite,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 14.sp
                )
                Text(
                  text = "DMS Mécanicauto+, StockPro ERP, Comparateur B2B, TCO, ADAS/EV, Console Admin",
                  color = AluminumGray,
                  fontSize = 12.sp
                )
              }
            }
            Icon(
              imageVector = if (showAllAdvancedModules) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = null,
              tint = AluminumGray
            )
          }
        }
      }
    }

    if (showAllAdvancedModules && uiState.currentAccountRole.isWorkshopAccount) {
    // Header Bar with Notification Bell, Super Admin Console & Plan
    item {
      HomeTopHeader(
        userPlan = uiState.userPlanName,
        dailyVinCount = uiState.dailyVinCount,
        dailyVinLimit = uiState.dailyVinLimit,
        unreadNotificationsCount = uiState.unreadNotificationsCount,
        pendingAdminApprovalsCount = uiState.pendingAdminApprovalsCount,
        onOpenAdminConsole = { viewModel.openSuperAdminModal() },
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
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            MekanikLogo(isSmall = true, showTagline = false, animateOnEntry = true)
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

          // Camera Scanner Quick Trigger
          Button(
            onClick = { viewModel.openCameraScanner() },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed, contentColor = PureWhite),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
            modifier = Modifier.testTag("btn_quick_camera_scan")
          ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Scan OCR", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
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

        Spacer(modifier = Modifier.height(10.dp))

        // Role Switcher Selector Bar (Atelier Pro, Magasin Pièces, Conducteur, Agence Flotte)
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "PROFIL & ESPACE ACTIF :",
              style = MaterialTheme.typography.labelSmall.copy(
                color = AluminumMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                fontSize = 10.sp
              )
            )
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = PerformanceRedDark
            ) {
              Text(
                text = uiState.currentAccountRole.badgeLabel,
                color = PureWhite,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            AccountRole.entries.forEach { role ->
              val isSelected = role == uiState.currentAccountRole
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) PerformanceRed else MetalCard,
                border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
                onClick = { viewModel.switchAccountRole(role) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("role_btn_${role.name.lowercase()}")
              ) {
                Column(
                  modifier = Modifier.padding(vertical = 7.dp, horizontal = 2.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(role.iconEmoji, fontSize = 13.sp)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = role.shortTitle,
                    color = if (isSelected) PureWhite else AluminumLight,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 9.5.sp,
                    maxLines = 1
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Sélecteur de Mode d'Affichage (Automatique Téléphone, Sombre, Jour, Clair)
          val context = androidx.compose.ui.platform.LocalContext.current
          val currentThemeMode = MekanikThemeController.selectedMode
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MetalCard.copy(alpha = 0.85f),
            border = BorderStroke(1.dp, MetalBorder),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("home_display_theme_selector_card")
          ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "🎨 MODE D'AFFICHAGE",
                  color = AluminumMuted,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.8.sp
                )
                Text(
                  text = if (currentThemeMode == AppDisplayThemeMode.SYSTEM_AUTO) {
                    "Auto Téléphone (${if (MekanikThemeController.systemIsDark) "Sombre : Écriture Blanche" else "Clair : Écriture Noire"})"
                  } else if (MekanikThemeController.isLightModeActive) {
                    "${currentThemeMode.shortLabel} Actif • Fond Clair & Écriture Noire Auto"
                  } else {
                    "${currentThemeMode.shortLabel} Actif • Fond Sombre & Écriture Blanche Auto"
                  },
                  color = MechanicalOrange,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
              ) {
                AppDisplayThemeMode.entries.forEach { mode ->
                  val isModeSelected = currentThemeMode == mode
                  Surface(
                    shape = RoundedCornerShape(7.dp),
                    color = if (isModeSelected) MechanicalOrange else GraphiteDark,
                    border = BorderStroke(1.dp, if (isModeSelected) MechanicalOrangeGlow else MetalBorder),
                    onClick = { MekanikThemeController.setDisplayThemeMode(mode, context) },
                    modifier = Modifier
                      .weight(1f)
                      .testTag("home_theme_mode_${mode.name.lowercase()}")
                  ) {
                    Row(
                      modifier = Modifier.padding(vertical = 5.dp, horizontal = 4.dp),
                      horizontalArrangement = Arrangement.Center,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(mode.emoji, fontSize = 11.sp)
                      Spacer(modifier = Modifier.width(3.dp))
                      Text(
                        text = mode.shortLabel,
                        color = if (isModeSelected) PureWhite else OffWhite,
                        fontWeight = if (isModeSelected) FontWeight.Black else FontWeight.SemiBold,
                        fontSize = 9.5.sp,
                        maxLines = 1
                      )
                    }
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Super-Admin Control Bar (badroumc@gmail.com)
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = adaptiveBannerSurface(0xFF281014, 0xFFFFF1F2),
            border = BorderStroke(1.2.dp, PerformanceRedGlow),
            onClick = { viewModel.openSuperAdminModal() },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("home_super_admin_banner")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                  modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PerformanceRed.copy(alpha = 0.25f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = PerformanceRedGlow,
                    modifier = Modifier.size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "COMPTE SUPER-ADMINISTRATEUR",
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = ElectricGreen.copy(alpha = 0.2f),
                      border = BorderStroke(1.dp, ElectricGreen)
                    ) {
                      Text(
                        text = if (uiState.superAdminSession.isAuthenticated) "CONNECTÉ" else "CONNEXION",
                        color = ElectricGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 8.sp,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                      )
                    }
                  }
                  Text(
                    text = "${uiState.superAdminSession.currentOperatorName} (${uiState.superAdminSession.currentOperatorBadge}) • ${uiState.pendingAdminApprovalsCount} demandes à traiter",
                    color = AluminumLight,
                    fontSize = 10.sp
                  )
                }
              }

              Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, TechCyan),
                  onClick = { viewModel.openSuperAdminErrorLogger() },
                  modifier = Modifier.testTag("home_open_error_logger_btn")
                ) {
                  Text(
                    text = "🚨 LOGS",
                    color = TechCyan,
                    fontWeight = FontWeight.Black,
                    fontSize = 9.5.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                  )
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = PerformanceRed
                ) {
                  Text(
                    text = "CONSOLE",
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // 3-Day Automatic Demo Trial & Activation Key Banner
          DemoTrialStatusBanner(
            demoStatus = uiState.demoTrialStatus,
            onOpenActivationPortal = { viewModel.openDemoTrialActivationModal() },
            onSimulatePhaseChange = { viewModel.simulateDemoTrialPhase(it) },
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))

          // NOUVEAU : Accès Libre Sans Compte + Panier Schéma Éclaté + Carte Google Maps + Compte Particulier 3 500 DA + Auto-Réparation IA
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = adaptiveBannerSurface(0xFF0E212E, 0xFFF0F9FF),
            border = BorderStroke(1.2.dp, TechCyan),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("home_free_access_smart_cart_map_banner")
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "🔓 ACCÈS LIBRE SANS COMPTE • 🛒 PANIER ÉCLATÉ • 🗺️ GOOGLE MAPS • 🤖 IA 24/7",
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 10.5.sp
                )
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = ElectricGreen.copy(alpha = 0.2f),
                  border = BorderStroke(1.dp, ElectricGreen),
                  onClick = { viewModel.openClientBugAiRepairModal() },
                  modifier = Modifier.testTag("home_open_ai_auto_repair_btn")
                ) {
                  Text(
                    text = "🤖 RÉPARATION IA / BUG",
                    color = ElectricGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 8.5.sp,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                  )
                }
              }

              Text(
                text = "Recherchez une pièce sans compte (ex: « pompe à eau 206+ ») avec prix de montage garagiste, consultez le Panier Schéma Éclaté (réf. compatibles & stock vendeurs/ateliers), la Carte Google Maps ou le Compte Particulier (3 500 DA/an).",
                color = AluminumLight,
                fontSize = 10.sp
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Button(
                  onClick = { viewModel.openSmartCartFreeAccessMapModal(0) },
                  colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                  modifier = Modifier
                    .weight(1.1f)
                    .testTag("home_btn_free_access_no_account")
                ) {
                  Text("🔓 Accès Libre", color = GraphiteDark, fontWeight = FontWeight.Black, fontSize = 9.5.sp)
                }

                Button(
                  onClick = { viewModel.openSmartCartFreeAccessMapModal(1) },
                  colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                  modifier = Modifier
                    .weight(1f)
                    .testTag("home_btn_smart_exploded_cart")
                ) {
                  Text("🛒 Panier Éclaté", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 9.5.sp)
                }

                Button(
                  onClick = { viewModel.openSmartCartFreeAccessMapModal(2) },
                  colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                  modifier = Modifier
                    .weight(1f)
                    .testTag("home_btn_exact_google_maps")
                ) {
                  Text("🗺️ Google Maps", color = GraphiteDark, fontWeight = FontWeight.Black, fontSize = 9.5.sp)
                }

                Button(
                  onClick = { viewModel.openSmartCartFreeAccessMapModal(3) },
                  colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                  modifier = Modifier
                    .weight(1f)
                    .testTag("home_btn_particulier_3500")
                ) {
                  Text("👤 3 500 DA/an", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 9.5.sp)
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
          },
          onScanCarteGriseVin = { viewModel.openCarteGriseScanner() },
          apiDataSourceMode = uiState.apiDataSourceMode,
          onOpenDevApiManager = { viewModel.openDevApiManagerModal() }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // MekanikOne Unified Auto-API Hub (11 APIs + Cascade + Les 6 Options Indispensables)
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MetalCard,
          border = BorderStroke(1.dp, MetalBorder),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.openUnifiedAutoApiHubModal(0) }
            .testTag("card_unified_auto_api_home")
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                  modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GraphiteDark)
                    .border(1.dp, MetalBorder, RoundedCornerShape(10.dp)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.Hub,
                    contentDescription = null,
                    tint = MekanikBrandOrange,
                    modifier = Modifier.size(22.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "MEKANIK-ONE • API UNIQUE & FALLBACK AUTO",
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                      shape = RoundedCornerShape(5.dp),
                      color = TechCyan.copy(alpha = 0.2f),
                      border = BorderStroke(0.8.dp, TechCyan)
                    ) {
                      Text(
                        text = "11 API + 6 OPTIONS PRO",
                        color = TechCyan,
                        fontWeight = FontWeight.Black,
                        fontSize = 8.5.sp,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                      )
                    }
                  }
                  Text(
                    text = "SinoAuto360 → Dongchedi → Car2DB → ChineseCar.parts → ServiceBox/Dialogys/ETKA → TecDoc → HaynesPro → 7Zap → NHTSA → lifeofcapo → Room 0ms",
                    color = MechanicalOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp
                  )
                  Text(
                    text = uiState.activeUnifiedApiTrace.fallbackCascade.cascadeSummaryBanner,
                    color = if (uiState.activeUnifiedApiTrace.fallbackCascade.fallbackHopsCount > 0) MechanicalOrange else ElectricGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 9.5.sp
                  )
                }
              }

              Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TechCyan,
                modifier = Modifier.size(20.dp)
              )
            }

            // Quick bar to open the 6 Essential Options directly
            Surface(
              shape = RoundedCornerShape(9.dp),
              color = MetalCard,
              border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.7f)),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.openUnifiedAutoApiHubModal(2) }
                .testTag("btn_open_essential_six_options_home")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                  Icon(
                    Icons.Default.Verified,
                    contentDescription = null,
                    tint = ElectricGreen,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "6 OPTIONS PRO : 1. OCR→Cascade • 2. Chine↔Europe IAM • 3. Room 0ms • 4. Devis OR 1-Clic • 5. Huiles DCT • 6. Passeport QR",
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp
                  )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "OUVRIR →",
                  color = ElectricGreen,
                  fontWeight = FontWeight.Black,
                  fontSize = 9.5.sp
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // StockPro ERP — Complete Spare Parts Stock Management Software Banner
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MetalCard,
          border = BorderStroke(1.5.dp, MechanicalOrange),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.openBarcodeScannerModal() }
            .testTag("card_stockpro_erp_home")
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(MechanicalOrangeDark),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Inventory, contentDescription = null, tint = PureWhite, modifier = Modifier.size(22.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "STOCKPRO ERP • LOGICIEL GESTION DE STOCK PIÈCES",
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.5.sp
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  val lowCount = uiState.barcodeInventory.count { it.isLowStock }
                  if (lowCount > 0) {
                    Surface(shape = RoundedCornerShape(4.dp), color = PerformanceRedDark) {
                      Text(
                        text = "$lowCount ALERTES",
                        color = PureWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 8.5.sp,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                      )
                    }
                  }
                }
                Text(
                  text = "Valorisation (${uiState.barcodeInventory.sumOf { it.totalSaleValueDzd }} DA) • Rayonnage • Mouvements E/S • Commandes BC • Scanner Laser",
                  color = AluminumLight,
                  fontSize = 10.sp
                )
              }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(20.dp))
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Universal Vehicle QR Passport Multi-Role Quick Access Bar
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MetalCard,
          border = BorderStroke(1.dp, PerformanceRedGlow),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.toggleUniversalQrScanner(true) }
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PerformanceRedDark)
                .border(1.dp, PerformanceRedGlow, RoundedCornerShape(10.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = PureWhite, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "PASSEPORT QR UNIVERSEL DU VÉHICULE",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    fontSize = 12.sp
                  )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = TechCyan.copy(alpha = 0.2f),
                  border = BorderStroke(1.dp, TechCyan)
                ) {
                  Text(
                    text = "NOUVEAU",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = TechCyan,
                      fontSize = 8.5.sp,
                      fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }
              Text(
                text = "Scan express pour Ateliers (OR/Devis), Commerçants (Pièces) et Loueurs",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 10.5.sp)
              )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = PerformanceRedGlow)
          }
        }
      }
    }

    // Dedicated Role-Based Interface Section
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        when (uiState.currentAccountRole) {
          AccountRole.VENDEUR_PIECES -> {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, MechanicalOrange)
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
                        .clip(RoundedCornerShape(8.dp))
                        .background(MechanicalOrange.copy(alpha = 0.2f)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.Storefront, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text("TABLEAU DE BORD VENDEUR PIÈCES", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 13.sp)
                      Text("Gestion de stock, scanner EAN/QR & ventes comptoir", color = AluminumGray, fontSize = 10.5.sp)
                    }
                  }
                  Surface(shape = RoundedCornerShape(4.dp), color = MechanicalOrangeDark) {
                    Text("MAGASIN COMPTOIR", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                  }
                }

                // Quick Action 1: Barcode Scanner
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.6f)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openBarcodeScannerModal() }
                    .testTag("btn_vendor_open_barcode_scanner")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(22.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("SCANNER CODE-BARRES / QR DE STOCK", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Entrée/Sortie express avec laser caméra", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(14.dp))
                  }
                }

                // Quick Action 2: Low Stock Alerts
                val lowStockCount = uiState.barcodeInventory.count { it.isLowStock }
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (lowStockCount > 0) PerformanceRedDark.copy(alpha = 0.4f) else GraphiteBlack,
                  border = BorderStroke(1.dp, if (lowStockCount > 0) PerformanceRed else MetalBorder),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openBarcodeScannerModal() }
                    .testTag("btn_vendor_open_stock_alerts")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Warning, contentDescription = null, tint = if (lowStockCount > 0) PerformanceRedGlow else ElectricGreen, modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("ALERTES RÉAPPROVISIONNEMENT ($lowStockCount RUPTURES)", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Articles sous le seuil critique de sécurité", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = AluminumLight, modifier = Modifier.size(14.dp))
                  }
                }

                // Quick Action 3: Store to Workshop Link (+5k)
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.5f)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openWorkshopStoreLinkModal() }
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Link, contentDescription = null, tint = TechCyan, modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("PASSERELLE ATELIER PARTENAIRE (+5 000 DA)", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Compte unifié Atelier + Magasin avec déduction directe", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TechCyan, modifier = Modifier.size(14.dp))
                  }
                }

                // Quick Action 4: Comparateur B2B & Coursier Express
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.6f)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openB2bComparatorModal() }
                    .testTag("btn_vendor_open_b2b_comparator")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.LocalShipping, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("COMMANDES B2B & SUIVI COURSIER EXPRESS", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Comparateur tarifs grossistes HT & livraison moto 45 min", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(14.dp))
                  }
                }

                // Quick Action 5: Garanties Pièces & Retours SAV / RMA
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, WarningYellow.copy(alpha = 0.6f)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openWarrantyRmaModal() }
                    .testTag("btn_vendor_open_warranty_rma")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("GARANTIES PIÈCES & DOSSIERS RETOUR SAV (RMA)", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Certificats 12/24 mois & bons d'échange standard", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(14.dp))
                  }
                }

                // Quick Action 6: Bourse Pièces d'Occasion Certifiées & Consignes Échange Standard
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, ElectricGreen),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openCertifiedUsedPartsModal() }
                    .testTag("btn_vendor_open_used_parts")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Autorenew, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("BOURSE PIÈCES OCCASION CERTIFIÉES & CONSIGNES", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Pièces Grade A/B, Échange Standard & cautions vieille matière", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(14.dp))
                  }
                }

                // Vendor New: Catalogues Web OEM Gratuits (CatCar.info, 7Zap, PartsSouq, NemigaParts, IFInterface, WebAutoCats)
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, TechCyan),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openWebOemCatalogsModal() }
                    .testTag("btn_vendor_open_web_oem_catalogs")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                      Icon(Icons.Default.AccountTree, contentDescription = null, tint = TechCyan, modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("CATALOGUES WEB OEM GRATUITS & VUES ÉCLATÉES", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("CatCar.info, 7Zap, PartsSouq, NemigaParts, IFInterface & WebAutoCats", color = AluminumLight, fontSize = 9.5.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TechCyan, modifier = Modifier.size(14.dp))
                  }
                }

                // Vendor New: Vérificateur Anti-Erreur Montage (PR VAG / OPR PSA / Boîtes Renault)
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, WarningYellow),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openPartCompatibilityModal() }
                    .testTag("btn_vendor_open_part_compatibility")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                      Icon(Icons.Default.Verified, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("ANTI-ERREUR MONTAGE : CODES PR / OPR / BOÎTES", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("Zéro retour client : filtrage strict PR VAG, DAM/OPR PSA & K9K/TL4", color = AluminumLight, fontSize = 9.5.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(14.dp))
                  }
                }
              }
            }
          }

          AccountRole.GARAGE_PRO -> {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, PerformanceRed)
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
                        .clip(RoundedCornerShape(8.dp))
                        .background(PerformanceRed.copy(alpha = 0.2f)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.Handyman, contentDescription = null, tint = PerformanceRed, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text("MODULES AVANCÉS ATELIER PRO", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 13.sp)
                      Text("Ponts temps réel, Inspection 360°, Barème OEM & Devis à distance", color = AluminumGray, fontSize = 10.5.sp)
                    }
                  }
                  Surface(shape = RoundedCornerShape(4.dp), color = PerformanceRedDark) {
                    Text("ATELIER PRO", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                  }
                }

                // New Feature 1: Planning Visuel des Ponts & Charge Atelier
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, MechanicalOrange),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openBayPlannerModal() }
                    .testTag("btn_home_open_bay_planner")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.ViewKanban, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(22.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("PLANNING VISUEL DES PONTS & CHARGE ATELIER", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Occupation des 4 ponts en direct, chrono mécanicien & file d'attente", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(14.dp))
                  }
                }

                // Module 1: Inspection 360 & Carrosserie
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, PerformanceRedGlow),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openInspectionModal() }
                    .testTag("btn_home_open_inspection")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(22.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("INSPECTION 360° & ÉTAT DES LIEUX NUMÉRIQUE", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Schéma carrosserie tactile multi-angles, photos & signature", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(14.dp))
                  }
                }

                // Module 2: Barème Temps Constructeur OEM
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.6f)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openLaborStandardModal() }
                    .testTag("btn_home_open_labor_standards")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Timer, contentDescription = null, tint = TechCyan, modifier = Modifier.size(22.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("BARÈME DES TEMPS CONSTRUCTEUR OEM", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Chiffrage temps officiel + calcul M.O instantané", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TechCyan, modifier = Modifier.size(14.dp))
                  }
                }

                // New Feature 3: Portail Client Partageable & Signature Devis à Distance
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, TechCyan),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openRemoteQuotePortalModal() }
                    .testTag("btn_home_open_remote_quote_portal")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Draw, contentDescription = null, tint = TechCyan, modifier = Modifier.size(22.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("PORTAIL DEVIS À DISTANCE & SIGNATURE CLIENT", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Lien web/QR partageable, choix d'options & accord signé", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TechCyan, modifier = Modifier.size(14.dp))
                  }
                }

                // New Feature 5: Comparateur Grossistes B2B & Livreur Express
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.7f)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openB2bComparatorModal() }
                    .testTag("btn_home_open_b2b_comparator")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.LocalShipping, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(22.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("COMPARATEUR GROSSISTES B2B & COURSIER 45 MIN", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Meilleurs prix HT, calcul marge atelier & suivi livreur", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(14.dp))
                  }
                }

                // New Feature 2 & 4 Compact Row: Garanties RMA + Mode Hors-Ligne
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, WarningYellow.copy(alpha = 0.7f)),
                    modifier = Modifier
                      .weight(1f)
                      .clickable { viewModel.openWarrantyRmaModal() }
                      .testTag("btn_home_open_warranty_rma")
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Column {
                        Text("GARANTIES & RMA", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                        Text("SAV Pièces 12/24m", color = AluminumLight, fontSize = 9.sp)
                      }
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, if (uiState.isOfflineModeEnabled) MechanicalOrange else ElectricGreen),
                    modifier = Modifier
                      .weight(1f)
                      .clickable { viewModel.openOfflineSyncModal() }
                      .testTag("btn_home_open_offline_sync")
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(
                        if (uiState.isOfflineModeEnabled) Icons.Default.CloudOff else Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = if (uiState.isOfflineModeEnabled) MechanicalOrange else ElectricGreen,
                        modifier = Modifier.size(18.dp)
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Column {
                        Text("MODE HORS-LIGNE", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                        Text("Cache Sous-Sol & Sync", color = AluminumLight, fontSize = 9.sp)
                      }
                    }
                  }
                }

                // Module 3: Notifications WhatsApp & SMS
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.6f)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openClientMessagingModal() }
                    .testTag("btn_home_open_messaging")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Send, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(22.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("NOTIFICATIONS WHATSAPP & SMS CLIENT", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Devis prêt, Véhicule réparé & rappels en 1 clic", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(14.dp))
                  }
                }

                // Next-Gen Row 1: Schémas Électriques / Pinout ECU + Bulletins TSB & Pannes Connues
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, TechCyan),
                    modifier = Modifier
                      .weight(1f)
                      .clickable { viewModel.openWiringFuseEcuModal() }
                      .testTag("btn_home_open_wiring_ecu")
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(Icons.Default.ElectricalServices, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Column {
                        Text("FUSIBLES & PINOUT", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text("Schémas BSM/BSI & ECU", color = AluminumLight, fontSize = 8.8.sp)
                      }
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, PerformanceRedGlow),
                    modifier = Modifier
                      .weight(1f)
                      .clickable { viewModel.openTechnicalBulletinsModal() }
                      .testTag("btn_home_open_tsb_bulletins")
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(Icons.Default.LibraryBooks, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Column {
                        Text("BULLETINS TSB OEM", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text("Pannes connues & Rappels", color = AluminumLight, fontSize = 8.8.sp)
                      }
                    }
                  }
                }

                // Next-Gen Row 2: Calibrage ADAS / Batterie EV + Registre Climatisation A/C
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, EvBlue),
                    modifier = Modifier
                      .weight(1f)
                      .clickable { viewModel.openAdasEvBatteryModal() }
                      .testTag("btn_home_open_adas_ev")
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(Icons.Default.ElectricCar, contentDescription = null, tint = EvBlue, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Column {
                        Text("ADAS & BATTERIE EV", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text("Cibles CSC & SoH 400V", color = AluminumLight, fontSize = 8.8.sp)
                      }
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.7f)),
                    modifier = Modifier
                      .weight(1f)
                      .clickable { viewModel.openAcRefrigerantModal() }
                      .testTag("btn_home_open_ac_refrigerant")
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(Icons.Default.AcUnit, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Column {
                        Text("CLIM A/C & FLUIDES", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text("Charges R134a / R1234yf", color = AluminumLight, fontSize = 8.8.sp)
                      }
                    }
                  }
                }

                // Next-Gen Row 3: Pièces d'Occasion Certifiées & Consignes Échange Standard
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.8f)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openCertifiedUsedPartsModal() }
                    .testTag("btn_home_open_used_parts_exchange")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Autorenew, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("PIÈCES OCCASION CERTIFIÉES & CONSIGNES ÉCHANGE STANDARD", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("Casses VHU agréées (Grade A/B) & suivi cautions vieille matière", color = AluminumLight, fontSize = 9.5.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(14.dp))
                  }
                }

                // Garage New Row A: Catalogues Web OEM Gratuits (CatCar, 7Zap, PartsSouq...) + Anti-Erreur PR/OPR
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, TechCyan),
                    modifier = Modifier
                      .weight(1f)
                      .clickable { viewModel.openWebOemCatalogsModal() }
                      .testTag("btn_home_open_web_oem_catalogs")
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(Icons.Default.AccountTree, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Column {
                        Text("CATALOGUES WEB OEM", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 9.8.sp)
                        Text("CatCar • 7Zap • PartsSouq", color = AluminumLight, fontSize = 8.5.sp)
                      }
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, WarningYellow),
                    modifier = Modifier
                      .weight(1f)
                      .clickable { viewModel.openPartCompatibilityModal() }
                      .testTag("btn_home_open_part_compatibility")
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(Icons.Default.Verified, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Column {
                        Text("CODES PR / OPR / TL4", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 9.8.sp)
                        Text("Anti-erreur montage OEM", color = AluminumLight, fontSize = 8.5.sp)
                      }
                    }
                  }
                }

                // Garage New Row B: Rentabilité OR & Fûts d'Huile 208L + Pré-Contrôle Technique & Véhicules de Courtoisie
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, ElectricGreen),
                    modifier = Modifier
                      .weight(1f)
                      .clickable { viewModel.openWorkshopProfitabilityModal() }
                      .testTag("btn_home_open_profitability_drums")
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(Icons.Default.OilBarrel, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Column {
                        Text("MARGE OR & FÛTS 208L", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 9.8.sp)
                        Text("Temps pointés & Huile vrac", color = AluminumLight, fontSize = 8.5.sp)
                      }
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, MechanicalOrange),
                    modifier = Modifier
                      .weight(1f)
                      .clickable { viewModel.openPreCtCourtesyModal() }
                      .testTag("btn_home_open_pre_ct_courtesy")
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(Icons.Default.FactCheck, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Column {
                        Text("PRÉ-CONTRÔLE & PRÊT", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 9.8.sp)
                        Text("0 Contre-visite & Courtoisie", color = AluminumLight, fontSize = 8.5.sp)
                      }
                    }
                  }
                }

                // Row 3: Suite DMS WinMotor Next & Passerelle Peugeot Service Box V14
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = adaptiveBannerSurface(0xFF141E2B, 0xFFEFF6FF),
                  border = BorderStroke(1.2.dp, TechCyan),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openWinMotorServiceBoxModal() }
                    .testTag("btn_home_open_winmotor_dms")
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                      Icon(Icons.Default.PrecisionManufacturing, contentDescription = null, tint = TechCyan, modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Text(
                            text = "DMS MÉCANICAUTO+™ • GESTION INTÉGRÉE",
                            color = PureWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.5.sp
                          )
                          Spacer(modifier = Modifier.width(6.dp))
                          Surface(shape = RoundedCornerShape(4.dp), color = TechCyan.copy(alpha = 0.2f)) {
                            Text(
                              text = "DMS PRO",
                              color = TechCyan,
                              fontWeight = FontWeight.Black,
                              fontSize = 8.sp,
                              modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                          }
                        }
                        Text(
                          text = "Chiffrage T1/T2/T3 (cth) • Import Paniers OEM • Pointage Compagnons • Reliquats VOR",
                          color = AluminumLight,
                          fontSize = 9.sp
                        )
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TechCyan, modifier = Modifier.size(14.dp))
                  }
                }

                // Standby / Disabled Modules Row (Kept aside like License Plate search for future activation)
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack.copy(alpha = 0.55f),
                  border = BorderStroke(1.dp, MetalBorder),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.notifyStandbyModuleReserved("Gardiennage Saisonnier Pneus & Simulateur Reprise") }
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.LockClock, contentDescription = null, tint = AluminumMuted, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "En réserve : Gardiennage Pneus & Simulation Reprise",
                        color = AluminumMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                      )
                    }
                    Surface(shape = RoundedCornerShape(4.dp), color = MetalSurface) {
                      Text(
                        text = "DÉSACTIVÉ",
                        color = WarningYellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.5.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }
              }
            }
          }

          AccountRole.CONDUCTEUR -> {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, TechCyan)
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
                        .clip(RoundedCornerShape(8.dp))
                        .background(TechCyan.copy(alpha = 0.2f)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = TechCyan, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text("MON ESPACE VÉHICULE PERSONNEL", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 13.sp)
                      Text("Budget TCO/km, Constat digital, Devis à distance & SOS", color = AluminumGray, fontSize = 10.5.sp)
                    }
                  }
                  Surface(shape = RoundedCornerShape(4.dp), color = TechCyanDark) {
                    Text("PARTICULIER", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                  }
                }

                // Feature 6: Calculateur TCO & Carburant
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, TechCyan),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openTcoCalculatorModal() }
                    .testTag("btn_driver_open_tco_calculator")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.LocalGasStation, contentDescription = null, tint = TechCyan, modifier = Modifier.size(22.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("CALCULATEUR COÛT KILOMÉTRIQUE (TCO) & CARBURANT", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Suivi réel DA/km, pleins carburant, entretien & assurance", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TechCyan, modifier = Modifier.size(14.dp))
                  }
                }

                // Feature 7: Constat Amiable Digital & Déclaration Sinistre
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, MechanicalOrange),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openAccidentReportModal() }
                    .testTag("btn_driver_open_accident_report")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.GppGood, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(22.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("CONSTAT AMIABLE DIGITAL & DÉCLARATION SINISTRE", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Assistant pas-à-pas, circonstances officielles & photos", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(14.dp))
                  }
                }

                // Feature 3: Suivi Travaux & Signature Devis à Distance
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, ElectricGreen),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openRemoteQuotePortalModal() }
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Draw, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(22.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("VALIDER MON DEVIS ATELIER À DISTANCE", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Suivi en direct sur pont, vidéo mécanicien & signature", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(14.dp))
                  }
                }

                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, PerformanceRedGlow),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleSosModal(true) }
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Emergency, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(22.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("SOS PANNE & DÉPANNAGE REMORQUAGE 24/7", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Trouver un dépanneur agréé en Algérie", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(14.dp))
                  }
                }

                // Driver Next-Gen Shortcuts: Rappels TSB + Bilan Batterie EV + Pièces Occasion
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, WarningYellow.copy(alpha = 0.7f)),
                    modifier = Modifier
                      .weight(1f)
                      .clickable { viewModel.openTechnicalBulletinsModal() }
                  ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.ReportProblem, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Column {
                        Text("RAPPELS & PANNES", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text("Bulletins TSB Moteur", color = AluminumLight, fontSize = 8.8.sp)
                      }
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.7f)),
                    modifier = Modifier
                      .weight(1f)
                      .clickable { viewModel.openCertifiedUsedPartsModal() }
                  ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Autorenew, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Column {
                        Text("PIÈCES OCCASION", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text("Casses certifiées -60%", color = AluminumLight, fontSize = 8.8.sp)
                      }
                    }
                  }
                }

                // Driver Row 2: Pré-Contrôle Technique (Zéro Contre-Visite) + Vues Éclatées Web OEM
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, TechCyan),
                    modifier = Modifier
                      .weight(1f)
                      .clickable { viewModel.openPreCtCourtesyModal() }
                  ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.FactCheck, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Column {
                        Text("PRÉ-CONTRÔLE TECH.", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text("Éviter contre-visite", color = AluminumLight, fontSize = 8.8.sp)
                      }
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, MechanicalOrange),
                    modifier = Modifier
                      .weight(1f)
                      .clickable { viewModel.openWebOemCatalogsModal() }
                  ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.AccountTree, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Column {
                        Text("VUES ÉCLATÉES OEM", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text("CatCar • 7Zap • PartsSouq", color = AluminumLight, fontSize = 8.8.sp)
                      }
                    }
                  }
                }

                // Standby Card for Simulateur de Reprise (Kept aside for later activation)
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack.copy(alpha = 0.55f),
                  border = BorderStroke(1.dp, MetalBorder),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.notifyStandbyModuleReserved("Simulateur de Reprise & Cote Argus Locale") }
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.LockClock, contentDescription = null, tint = AluminumMuted, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "En réserve : Simulateur de Reprise & Cote Argus",
                        color = AluminumMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                      )
                    }
                    Surface(shape = RoundedCornerShape(4.dp), color = MetalSurface) {
                      Text(
                        text = "DÉSACTIVÉ",
                        color = WarningYellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.5.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }
              }
            }
          }

          AccountRole.LOCATION_VOITURES -> {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, EvBlue)
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
                        .clip(RoundedCornerShape(8.dp))
                        .background(EvBlue.copy(alpha = 0.2f)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.AirportShuttle, contentDescription = null, tint = EvBlue, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text("ESPACE AGENCE DE LOCATION & FLOTTE", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 13.sp)
                      Text("États des lieux 360°, Rentabilité TCO/km & Constat Sinistre", color = AluminumGray, fontSize = 10.5.sp)
                    }
                  }
                  Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF0D47A1)) {
                    Text("AGENCE FLOTTE", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                  }
                }

                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, EvBlue.copy(alpha = 0.6f)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openInspectionModal() }
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Assignment, contentDescription = null, tint = EvBlue, modifier = Modifier.size(22.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("ÉTAT DES LIEUX DÉPART & RETOUR (INSPECTION 360°)", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Relever rayures, carburant et signature locataire", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = EvBlue, modifier = Modifier.size(14.dp))
                  }
                }

                // Feature 6 for Rental: Rentabilité Flotte & TCO Kilométrique
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.7f)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openTcoCalculatorModal() }
                    .testTag("btn_rental_open_tco_calculator")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.QueryStats, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(22.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("RENTABILITÉ VÉHICULE & COÛT AU KILOMÈTRE (TCO)", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Marge nette mensuelle, coût DA/km & suivi entretiens", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(14.dp))
                  }
                }

                // Feature 7 for Rental: Constat Amiable Digital & Déclaration Sinistre Locataire
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.8f)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openAccidentReportModal() }
                    .testTag("btn_rental_open_accident_report")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.GppGood, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(22.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("DÉCLARATION SINISTRE & CONSTAT AMIABLE LOCATAIRE", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        Text("Dossier complet avec photos, circonstances & assurance", color = AluminumLight, fontSize = 10.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(14.dp))
                  }
                }

                // New Feature for Rental: Contrats Court Terme, Caution & Comparateur Départ/Retour
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, TechCyan),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openShortTermRentalModal() }
                    .testTag("btn_rental_open_short_term_contracts")
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.CarRental, contentDescription = null, tint = TechCyan, modifier = Modifier.size(22.dp))
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text("CONTRATS LOCATION, CAUTION & COMPARATEUR KM/CARBURANT", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("Jauge 8èmes, calcul automatique km sup. & libération caution", color = AluminumLight, fontSize = 9.5.sp)
                      }
                    }
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, tint = TechCyan, modifier = Modifier.size(14.dp))
                  }
                }

                // Standby Card for Gardiennage Pneus Flotte (Kept aside for later activation)
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack.copy(alpha = 0.55f),
                  border = BorderStroke(1.dp, MetalBorder),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.notifyStandbyModuleReserved("Gardiennage Saisonnier Pneus & Suivi Usure Flotte") }
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.LockClock, contentDescription = null, tint = AluminumMuted, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "En réserve : Gardiennage Pneus & Simulation Reprise",
                        color = AluminumMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                      )
                    }
                    Surface(shape = RoundedCornerShape(4.dp), color = MetalSurface) {
                      Text(
                        text = "DÉSACTIVÉ",
                        color = WarningYellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.5.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }
              }
            }
          }
          AccountRole.ATELIER_MAGASIN_MIXTE,
          AccountRole.ATELIER_CARROSSERIE -> {
            // Déjà affiché en haut dans SimplifiedModernHomeDashboard
          }
        }
      }
    }

    // Pro Modules Shortcuts Row (Scrollable):
    // 📷 Scanner OCR | 📡 OBD Télémétrie | ⏱️ Suivi Atelier | 🚚 Flotte | 🚨 SOS 24/7 | 📦 B2B Devis | 🧾 Facturation
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        Text(
          text = "MODULES & SERVICES AUTOMOBILES AVANCÉS",
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
            title = "Inspection 360°",
            subtitle = "Carrosserie & Défauts",
            icon = Icons.Default.DirectionsCar,
            accentColor = PerformanceRedGlow,
            onClick = { viewModel.openInspectionModal() },
            testTag = "shortcut_inspection_360"
          )
          ShortcutCard(
            title = "Scanner Stock",
            subtitle = "Code-Barres & EAN",
            icon = Icons.Default.QrCodeScanner,
            accentColor = MechanicalOrange,
            onClick = { viewModel.openBarcodeScannerModal() },
            testTag = "shortcut_barcode_stock"
          )
          ShortcutCard(
            title = "Barème OEM",
            subtitle = "Temps & Chiffrage M.O",
            icon = Icons.Default.Timer,
            accentColor = TechCyan,
            onClick = { viewModel.openLaborStandardModal() },
            testTag = "shortcut_labor_standards"
          )
          ShortcutCard(
            title = "Alerte Client",
            subtitle = "WhatsApp & SMS",
            icon = Icons.Default.Send,
            accentColor = ElectricGreen,
            onClick = { viewModel.openClientMessagingModal() },
            testTag = "shortcut_messaging_whatsapp"
          )
          ShortcutCard(
            title = "Sonar IA Acoustique",
            subtitle = "Micro & Bruits Moteur",
            icon = Icons.Default.GraphicEq,
            accentColor = PerformanceRedGlow,
            onClick = { viewModel.openEngineSoundRecorder() },
            testTag = "shortcut_sonar_acoustic"
          )
          ShortcutCard(
            title = "QR Passeport Hub",
            subtitle = "Scan OR, Pièces & Loueur",
            icon = Icons.Default.QrCodeScanner,
            accentColor = PerformanceRedGlow,
            onClick = { viewModel.toggleUniversalQrScanner(true) },
            testTag = "shortcut_universal_qr"
          )
          ShortcutCard(
            title = "Atelier Dashboard",
            subtitle = "Ordres, Mécanos & Tâches",
            icon = Icons.Default.Dashboard,
            accentColor = PerformanceRed,
            onClick = { viewModel.setNavigationTab(MainNavigationTab.ATELIER) },
            testTag = "shortcut_atelier_dashboard"
          )
          ShortcutCard(
            title = "Stats & Recharts",
            subtitle = "Bilan Réparations Atelier",
            icon = Icons.Default.BarChart,
            accentColor = PerformanceRed,
            onClick = { viewModel.setNavigationTab(MainNavigationTab.ATELIER) },
            testTag = "shortcut_recharts_stats"
          )
          ShortcutCard(
            title = "Scanner Valise OBD",
            subtitle = "Caméra OCR & Gemini IA",
            icon = Icons.Default.DocumentScanner,
            accentColor = TechCyan,
            onClick = onNavigateToObdScanner,
            testTag = "shortcut_obd_scanner"
          )
          ShortcutCard(
            title = "Espace Conducteur",
            subtitle = "Carnet, Factures & DIY",
            icon = Icons.Default.DirectionsCar,
            accentColor = EvBlue,
            onClick = onNavigateToDriverSpace,
            testTag = "shortcut_driver"
          )
          ShortcutCard(
            title = "Location Voitures",
            subtitle = "Flotte & Contrats",
            icon = Icons.Default.CarRental,
            accentColor = MechanicalOrange,
            onClick = onNavigateToCarRental,
            testTag = "shortcut_rental"
          )
          ShortcutCard(
            title = "Scanner OCR",
            subtitle = "Carte grise & VIN",
            icon = Icons.Default.CameraAlt,
            accentColor = PerformanceRedGlow,
            onClick = { viewModel.openCameraScanner() },
            testTag = "shortcut_ocr"
          )
          ShortcutCard(
            title = "OBD Télémétrie",
            subtitle = "Données Live & Audio",
            icon = Icons.Default.Speed,
            accentColor = TechCyan,
            onClick = onNavigateToObd,
            testTag = "shortcut_obd"
          )
          ShortcutCard(
            title = "Suivi Réparation",
            subtitle = "Avancement & QR",
            icon = Icons.Default.AvTimer,
            accentColor = ElectricGreen,
            onClick = onNavigateToLiveTracking,
            testTag = "shortcut_tracking"
          )
          ShortcutCard(
            title = "Gestion Flotte",
            subtitle = "Parc multi-véhicules",
            icon = Icons.Default.LocalShipping,
            accentColor = MechanicalOrange,
            onClick = onNavigateToFleet,
            testTag = "shortcut_fleet"
          )
          ShortcutCard(
            title = "SOS Dépannage",
            subtitle = "Remorquage 24/7",
            icon = Icons.Default.Emergency,
            accentColor = PerformanceRed,
            onClick = { viewModel.toggleSosModal(true) },
            testTag = "shortcut_sos"
          )
          ShortcutCard(
            title = "B2B Marketplace",
            subtitle = "Comparateur 58 Wilayas",
            icon = Icons.Default.Storefront,
            accentColor = TechnicalBlue,
            onClick = { viewModel.toggleGroupQuoteModal(true) },
            testTag = "shortcut_b2b"
          )
          ShortcutCard(
            title = "Facturation Pro",
            subtitle = "Factures & CIB DZ",
            icon = Icons.Default.ReceiptLong,
            accentColor = WarningAmber,
            onClick = { viewModel.toggleInvoiceModal(true) },
            testTag = "shortcut_invoices"
          )
          ShortcutCard(
            title = "Documentation",
            subtitle = "Manuels & Schémas",
            icon = Icons.Default.MenuBook,
            accentColor = TechnicalBlue,
            onClick = onNavigateToDocs,
            testTag = "shortcut_docs"
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
    } // end if (showAllAdvancedModules)
      } // end LazyColumn
    } // end Column

    // BOUTON D'ACTION FLOTTANT (FAB) 1-TAP VIN / CODE MOTEUR POUR ATELIER
    QuickVinEngineSearchFab(
      onSingleTapOpenQuickSearch = { showSingleTapVinEngineModal = true },
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 16.dp, bottom = 20.dp)
    )

    if (showSingleTapVinEngineModal) {
      SingleTapVinEngineLauncherModal(
        currentQuery = uiState.searchQuery,
        onExecuteSingleTapSearch = { queryValue, isEngine ->
          showSingleTapVinEngineModal = false
          executeSingleTapSearch(queryValue, isEngine)
        },
        onOpenCameraOcr = { viewModel.openCarteGriseScanner() },
        onOpenVoiceSearch = onOpenHandsFreeVoiceSearch,
        onDismiss = { showSingleTapVinEngineModal = false }
      )
    }
  } // end Box
}

@Composable
private fun HomeTopHeader(
  userPlan: String,
  dailyVinCount: Int,
  dailyVinLimit: Int,
  unreadNotificationsCount: Int,
  pendingAdminApprovalsCount: Int,
  onOpenAdminConsole: () -> Unit,
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
      // Super-Admin Pill Button
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = adaptiveBannerSurface(0xFF2B0E12, 0xFFFFF1F2),
        border = BorderStroke(1.dp, PerformanceRedGlow),
        onClick = onOpenAdminConsole,
        modifier = Modifier.testTag("btn_header_super_admin")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            Icons.Default.AdminPanelSettings,
            contentDescription = "Admin",
            tint = PerformanceRedGlow,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "ADMIN ($pendingAdminApprovalsCount)",
            style = MaterialTheme.typography.labelSmall.copy(
              color = PureWhite,
              fontWeight = FontWeight.Black,
              fontSize = 10.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.width(6.dp))

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
