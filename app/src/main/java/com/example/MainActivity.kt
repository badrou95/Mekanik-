package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.*
import com.example.data.repository.MekanikErrorLogger
import com.example.data.repository.WorkshopOfflineCacheRepository
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainNavigationTab
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory
import kotlinx.coroutines.launch

private const val TAG = "AppCheckInit"

private const val DEBUG_TOKEN_EXTRA = "FIREBASE_APPCHECK_DEBUG_TOKEN"
private const val DEBUG_STORE_PREFIX = "com.google.firebase.appcheck.debug.store."
private const val DEBUG_SECRET_KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"
private const val RECAPTCHA_SITE_KEY_RES = "recaptcha_site_key"

fun configureAppCheck(context: Context, intent: Intent?) {
  try {
    // 1. Ensure FirebaseApp is initialized
    if (FirebaseApp.getApps(context).isEmpty()) {
      FirebaseApp.initializeApp(context)
    }

    if (BuildConfig.DEBUG) {
      // 2. Debug: Read debug token from Intent extra
      val debugToken = intent?.getStringExtra(DEBUG_TOKEN_EXTRA)

      if (!debugToken.isNullOrEmpty()) {
        val persistenceKey = FirebaseApp.getInstance().persistenceKey
        val prefs = context.getSharedPreferences(
          "$DEBUG_STORE_PREFIX$persistenceKey",
          Context.MODE_PRIVATE
        )
        // CRITICAL: MUST use commit() instead of apply() so the secret is written
        // synchronously before DebugAppCheckProviderFactory reads it.
        prefs.edit()
          .putString(DEBUG_SECRET_KEY, debugToken)
          .commit()
        Log.d(TAG, "App Check debug secret configured successfully.")
      } else {
        Log.w(TAG, "No debug token found in Intent extra.")
      }

      FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
        DebugAppCheckProviderFactory.getInstance()
      )
      Log.d(TAG, "DebugAppCheckProviderFactory installed.")
    } else {
      // 3. Release: Look up recaptcha_site_key safely without throwing Resources.NotFoundException
      val resId = context.resources.getIdentifier(RECAPTCHA_SITE_KEY_RES, "string", context.packageName)
      if (resId != 0) {
        val siteKey = context.getString(resId)
        if (siteKey.isNotBlank()) {
          FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            RecaptchaAppCheckProviderFactory.getInstance(siteKey)
          )
          Log.d(TAG, "RecaptchaAppCheckProviderFactory installed.")
        } else {
          Log.e(TAG, "recaptcha_site_key resource string is blank.")
        }
      } else {
        Log.e(TAG, "recaptcha_site_key string resource not found in package.")
      }
    }
  } catch (e: Exception) {
    Log.e(TAG, "Failed to initialize Firebase App Check", e)
  }
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    configureAppCheck(this, intent)
    MekanikErrorLogger.installGlobalUncaughtExceptionHandler(this)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MekanikApp()
      }
    }
  }
}

enum class SecondarySubScreen {
  NONE,
  VENDORS_DIRECTORY,
  SUBSCRIPTION_PLANS,
  TECHNICAL_DOCS,
  FLEET_MANAGEMENT,
  LIVE_TRACKING,
  OBD_TELEMETRY,
  DRIVER_SPACE,
  CAR_RENTAL_FLEET
}

@Composable
fun MekanikApp(
  mekanikViewModel: MekanikViewModel = viewModel()
) {
  val uiState by mekanikViewModel.uiState.collectAsStateWithLifecycle()
  val errorLoggerState by mekanikViewModel.errorLoggerState.collectAsStateWithLifecycle()
  val superAdminInitialTab by mekanikViewModel.superAdminInitialTab.collectAsStateWithLifecycle()
  val isClientBugAiRepairModalOpen by mekanikViewModel.isClientBugAiRepairModalOpen.collectAsStateWithLifecycle()
  val isSmartCartFreeAccessMapModalOpen by mekanikViewModel.isSmartCartFreeAccessMapModalOpen.collectAsStateWithLifecycle()
  val smartCartModalInitialTab by mekanikViewModel.smartCartModalInitialTab.collectAsStateWithLifecycle()
  val smartCartItems by mekanikViewModel.smartCartItems.collectAsStateWithLifecycle()
  val particulierAccountState by mekanikViewModel.particulierAccountState.collectAsStateWithLifecycle()
  val isOfflineCacheHubModalOpen by mekanikViewModel.isOfflineCacheHubModalOpen.collectAsStateWithLifecycle()
  val offlineCacheHubInitialTab by mekanikViewModel.offlineCacheHubInitialTab.collectAsStateWithLifecycle()
  val workshopNetworkCondition by mekanikViewModel.workshopNetworkCondition.collectAsStateWithLifecycle()
  val localCacheSummary by mekanikViewModel.localCacheSummary.collectAsStateWithLifecycle()
  val cachedSchematics by mekanikViewModel.cachedSchematics.collectAsStateWithLifecycle()
  val savedDiagnostics by mekanikViewModel.savedDiagnostics.collectAsStateWithLifecycle()
  val clientFaultHistory by mekanikViewModel.clientFaultHistory.collectAsStateWithLifecycle()
  val vehicleRepairLogs by mekanikViewModel.vehicleRepairLogs.collectAsStateWithLifecycle()
  val isPartLabelVisionScannerOpen by mekanikViewModel.isPartLabelVisionScannerOpen.collectAsStateWithLifecycle()
  var currentSubScreen by remember { mutableStateOf(SecondarySubScreen.NONE) }
  val snackbarHostState = remember { SnackbarHostState() }
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val coroutineScope = rememberCoroutineScope()

  val appContext = LocalContext.current
  LaunchedEffect(appContext) {
    WorkshopOfflineCacheRepository.initializeWithContext(appContext)
    AccountSelectorPrefs.getSavedAccountRole(appContext)?.let { savedRole ->
      if (savedRole != uiState.currentAccountRole) {
        mekanikViewModel.switchAccountRole(savedRole)
      }
    }
    try {
      com.example.data.repository.SecurityShieldFirestoreRepository(appContext)
        .observeCurrentUserAccountSelection()
        .collect { firestoreSelection ->
          if (firestoreSelection != null) {
            val cloudRole = AccountRole.fromId(firestoreSelection.accountRole)
            if (cloudRole != mekanikViewModel.uiState.value.currentAccountRole) {
              mekanikViewModel.switchAccountRole(cloudRole)
            }
          }
        }
    } catch (_: Exception) {
    }
  }
  // Nouveaux espaces modaux simplifiés : Store/Ville/Pièce, Vendeur Photo IA, Forum par Groupe, Réseau Social Pro B2B, Archive ZIP & Recherche Vocale Mains-Libres
  var showStoreAndCitySearchModal by remember { mutableStateOf(false) }
  var storeSearchInitialTab by remember { mutableStateOf(StoreSearchTab.IN_STORE) }
  var showVendorAddPartPhotoAiModal by remember { mutableStateOf(false) }
  var showAutoGroupForumModal by remember { mutableStateOf(false) }
  var showProB2bSocialNetworkModal by remember { mutableStateOf(false) }
  var showMonthlySalesZipArchiveModal by remember { mutableStateOf(false) }
  var showHandsFreeVoiceSearchModal by remember { mutableStateOf(false) }
  var showAccountSelectorModal by remember {
    mutableStateOf(AccountSelectorPrefs.shouldShowAccountSelectorOnFirstLogin(appContext))
  }
  var showAccountAdmin2FaModal by remember { mutableStateOf(false) }
  var showSaasWebPlatformModal by remember { mutableStateOf(false) }
  var initialSaasSpace by remember { mutableStateOf(SaasSpace.SITE_PUBLIC) }

  // Tutoriel de Démarrage Rapide Interactif (Onboarding avec superposition semi-transparente lors de la 1ère connexion)
  var showInteractiveOnboardingOverlay by remember {
    mutableStateOf(false)
  }
  var onboardingStepIndex by remember { mutableIntStateOf(0) }

  // Système de Notifications en Temps Réel (Live Stream + Bannière Flottante + Notification Android)
  var isRealtimeStreamEnabled by remember { mutableStateOf(true) }
  var liveBannerNotification by remember { mutableStateOf<AppNotification?>(null) }
  var realtimeSequenceCounter by remember { mutableIntStateOf(0) }

  val triggerRealtimeNotification: (NotificationCategory) -> Unit = { category ->
    val notif = buildRealtimeAppNotification(category, realtimeSequenceCounter)
    realtimeSequenceCounter += 1
    mekanikViewModel.sendSimulatedNotification(notif.title, notif.message, notif.category)
    liveBannerNotification = notif
    MekanikOnboardingAndRealtimePrefs.postSystemNotificationIfPermitted(appContext, notif)
  }

  // Diffusion automatique en temps réel lorsque le flux Live est actif
  LaunchedEffect(isRealtimeStreamEnabled, showInteractiveOnboardingOverlay) {
    if (isRealtimeStreamEnabled && !showInteractiveOnboardingOverlay) {
      val categoriesCycle = listOf(
        NotificationCategory.MEKANIK_AI_ALERTE,
        NotificationCategory.PIECE_DISPONIBLE,
        NotificationCategory.RDV_ATELIER,
        NotificationCategory.OFFRE_VENDEUR,
        NotificationCategory.ENTRETIEN_RAPPEL
      )
      kotlinx.coroutines.delay(14000L)
      while (isRealtimeStreamEnabled && !showInteractiveOnboardingOverlay) {
        val nextCat = categoriesCycle[realtimeSequenceCounter % categoriesCycle.size]
        val autoNotif = buildRealtimeAppNotification(nextCat, realtimeSequenceCounter)
        realtimeSequenceCounter += 1
        mekanikViewModel.sendSimulatedNotification(autoNotif.title, autoNotif.message, autoNotif.category)
        liveBannerNotification = autoNotif
        MekanikOnboardingAndRealtimePrefs.postSystemNotificationIfPermitted(appContext, autoNotif)
        kotlinx.coroutines.delay(38000L)
      }
    }
  }

  // Fermeture automatique de la bannière flottante temps réel après 6,5 secondes
  LaunchedEffect(liveBannerNotification?.id) {
    if (liveBannerNotification != null) {
      kotlinx.coroutines.delay(6500L)
      liveBannerNotification = null
    }
  }

  LaunchedEffect(uiState.toastMessage) {
    uiState.toastMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      mekanikViewModel.dismissModals()
    }
  }

  // Lors du passage d'un compte à un autre, suppression immédiate de tous les sous-écrans et modales ouverts du compte précédent
  LaunchedEffect(uiState.currentAccountRole) {
    currentSubScreen = SecondarySubScreen.NONE
    showStoreAndCitySearchModal = false
    showVendorAddPartPhotoAiModal = false
    showAutoGroupForumModal = false
    showProB2bSocialNetworkModal = false
    showMonthlySalesZipArchiveModal = false
    showHandsFreeVoiceSearchModal = false
  }

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      MekanikLeftDrawerContent(
        currentRole = uiState.currentAccountRole,
        onSelectRole = { mekanikViewModel.switchAccountRole(it) },
        onNavigateHome = {
          currentSubScreen = SecondarySubScreen.NONE
          mekanikViewModel.setNavigationTab(MainNavigationTab.ACCUEIL)
        },
        onOpenSearchByCity = {
          storeSearchInitialTab = StoreSearchTab.BY_CITY
          showStoreAndCitySearchModal = true
        },
        onOpenSearchInStore = {
          storeSearchInitialTab = StoreSearchTab.IN_STORE
          showStoreAndCitySearchModal = true
        },
        onOpenSearchPart = {
          storeSearchInitialTab = StoreSearchTab.BY_PART
          showStoreAndCitySearchModal = true
        },
        onOpenRepairOrder = {
          currentSubScreen = SecondarySubScreen.NONE
          mekanikViewModel.setNavigationTab(MainNavigationTab.ATELIER)
        },
        onOpenTechnicalDiagrams = {
          mekanikViewModel.openWebOemCatalogsModal()
        },
        onOpenSparePartsCatalog = {
          currentSubScreen = SecondarySubScreen.NONE
          mekanikViewModel.setNavigationTab(MainNavigationTab.PIECES)
        },
        onOpenPartVendors = {
          currentSubScreen = SecondarySubScreen.VENDORS_DIRECTORY
        },
        onOpenVendorAddPhotoAi = {
          showVendorAddPartPhotoAiModal = true
        },
        onOpenAutoForum = {
          showAutoGroupForumModal = true
        },
        onOpenProSocialNetwork = {
          showProB2bSocialNetworkModal = true
        },
        onOpenMonthlyZipArchive = {
          showMonthlySalesZipArchiveModal = true
        },
        onOpenFleetAndRental = {
          currentSubScreen = SecondarySubScreen.CAR_RENTAL_FLEET
        },
        onOpenHandsFreeVoiceSearch = {
          showHandsFreeVoiceSearchModal = true
        },
        onOpenPartLabelVisionScanner = {
          mekanikViewModel.openPartLabelVisionScanner()
        },
        onOpenMecaVerifHistory = {
          currentSubScreen = SecondarySubScreen.NONE
          mekanikViewModel.setNavigationTab(MainNavigationTab.MECA_VERIF)
        },
        onOpenMekanikAiObd = { dtcCode ->
          currentSubScreen = SecondarySubScreen.NONE
          mekanikViewModel.setNavigationTab(MainNavigationTab.AI_DIAGNOSTIC)
          if (!dtcCode.isNullOrBlank()) {
            mekanikViewModel.askMekanikAi(dtcCode)
          }
        },
        onOpenNotificationsCenter = {
          mekanikViewModel.toggleNotificationModal(true)
        },
        onStartOnboardingGuide = {
          onboardingStepIndex = 0
          showInteractiveOnboardingOverlay = true
        },
        onOpenAccountSelector = {
          showAccountSelectorModal = true
        },
        onOpenAccountAdmin2Fa = {
          showAccountAdmin2FaModal = true
        },
        onOpenSuperAdminPortal = {
          mekanikViewModel.openSuperAdminModal(7)
        },
        onCloseDrawer = {
          coroutineScope.launch { drawerState.close() }
        }
      )
    }
  ) {
  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(GraphiteDark),
    containerColor = GraphiteDark,
    contentWindowInsets = WindowInsets.systemBars,
    topBar = {
      MekanikTopThreeBarsHeader(
        currentRole = uiState.currentAccountRole,
        onOpenDrawer = {
          coroutineScope.launch { drawerState.open() }
        },
        onOpenStoreSearch = { tab ->
          storeSearchInitialTab = tab
          showStoreAndCitySearchModal = true
        },
        onOpenVendorPhotoAi = { showVendorAddPartPhotoAiModal = true },
        onOpenAutoForum = { showAutoGroupForumModal = true },
        onOpenProSocialNetwork = { showProB2bSocialNetworkModal = true },
        onOpenMonthlyZipArchive = { showMonthlySalesZipArchiveModal = true },
        onOpenHandsFreeVoiceSearch = { showHandsFreeVoiceSearchModal = true },
        onOpenPartLabelVisionScanner = { mekanikViewModel.openPartLabelVisionScanner() },
        onOpenMecaVerifHistory = {
          currentSubScreen = SecondarySubScreen.NONE
          mekanikViewModel.setNavigationTab(MainNavigationTab.MECA_VERIF)
        },
        onOpenMekanikAiObd = { dtcCode ->
          currentSubScreen = SecondarySubScreen.NONE
          mekanikViewModel.setNavigationTab(MainNavigationTab.AI_DIAGNOSTIC)
          if (!dtcCode.isNullOrBlank()) {
            mekanikViewModel.askMekanikAi(dtcCode)
          }
        },
        unreadNotificationsCount = uiState.unreadNotificationsCount,
        isRealtimeStreamEnabled = isRealtimeStreamEnabled,
        onOpenNotificationsCenter = {
          mekanikViewModel.toggleNotificationModal(true)
        },
        onStartOnboardingGuide = {
          onboardingStepIndex = 0
          showInteractiveOnboardingOverlay = true
        },
        onOpenSaasWebPlatform = {
          MekanikSaasPlatformRegistry.activeSaasSpace = SaasSpace.SITE_PUBLIC
          initialSaasSpace = SaasSpace.SITE_PUBLIC
          showSaasWebPlatformModal = true
        }
      )
    },
    snackbarHost = {
      SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(bottom = 80.dp)
      ) { data ->
        Snackbar(
          containerColor = MetalCard,
          contentColor = PureWhite,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.padding(16.dp)
        ) {
          Text(
            text = data.visuals.message,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
          )
        }
      }
    },
    bottomBar = {
      if (currentSubScreen == SecondarySubScreen.NONE) {
        MekanikBottomNavigationBar(
          currentTab = uiState.currentNavTab,
          currentRole = uiState.currentAccountRole,
          onTabSelected = { tab ->
            mekanikViewModel.setNavigationTab(tab)
          }
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      AnimatedContent(
        targetState = Pair(uiState.currentNavTab, currentSubScreen),
        transitionSpec = {
          fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
        },
        label = "screen_navigation"
      ) { (navTab, subScreen) ->
        when (subScreen) {
          SecondarySubScreen.TECHNICAL_DOCS -> {
            TechnicalDocsScreen(
              uiState = uiState,
              viewModel = mekanikViewModel,
              onBack = { currentSubScreen = SecondarySubScreen.NONE }
            )
          }
          SecondarySubScreen.VENDORS_DIRECTORY -> {
            VendorsScreen(
              uiState = uiState,
              viewModel = mekanikViewModel,
              onBack = { currentSubScreen = SecondarySubScreen.NONE }
            )
          }
          SecondarySubScreen.SUBSCRIPTION_PLANS -> {
            SubscriptionsScreen(
              uiState = uiState,
              viewModel = mekanikViewModel,
              onBack = { currentSubScreen = SecondarySubScreen.NONE }
            )
          }
          SecondarySubScreen.FLEET_MANAGEMENT -> {
            FleetManagementScreen(
              uiState = uiState,
              viewModel = mekanikViewModel,
              onBack = { currentSubScreen = SecondarySubScreen.NONE }
            )
          }
          SecondarySubScreen.LIVE_TRACKING -> {
            LiveTrackingScreen(
              uiState = uiState,
              viewModel = mekanikViewModel,
              onBack = { currentSubScreen = SecondarySubScreen.NONE }
            )
          }
          SecondarySubScreen.OBD_TELEMETRY -> {
            ObdTelemetryScreen(
              uiState = uiState,
              viewModel = mekanikViewModel,
              onBack = { currentSubScreen = SecondarySubScreen.NONE }
            )
          }
          SecondarySubScreen.DRIVER_SPACE -> {
            DriverSpaceScreen(
              uiState = uiState,
              viewModel = mekanikViewModel,
              onBack = { currentSubScreen = SecondarySubScreen.NONE },
              onNavigateToParts = {
                currentSubScreen = SecondarySubScreen.NONE
                mekanikViewModel.setNavigationTab(MainNavigationTab.PIECES)
              },
              onNavigateToAi = {
                currentSubScreen = SecondarySubScreen.NONE
                mekanikViewModel.setNavigationTab(MainNavigationTab.AI_DIAGNOSTIC)
              }
            )
          }
          SecondarySubScreen.CAR_RENTAL_FLEET -> {
            CarRentalFleetScreen(
              uiState = uiState,
              viewModel = mekanikViewModel,
              onBack = { currentSubScreen = SecondarySubScreen.NONE },
              onNavigateToWorkshop = {
                currentSubScreen = SecondarySubScreen.NONE
                mekanikViewModel.setNavigationTab(MainNavigationTab.ATELIER)
              }
            )
          }
          SecondarySubScreen.NONE -> {
            when (navTab) {
              MainNavigationTab.ACCUEIL -> {
                HomeScreen(
                  uiState = uiState,
                  viewModel = mekanikViewModel,
                  onNavigateToVendors = { currentSubScreen = SecondarySubScreen.VENDORS_DIRECTORY },
                  onNavigateToSubscriptions = { currentSubScreen = SecondarySubScreen.SUBSCRIPTION_PLANS },
                  onNavigateToDocs = { currentSubScreen = SecondarySubScreen.TECHNICAL_DOCS },
                  onNavigateToFleet = { currentSubScreen = SecondarySubScreen.FLEET_MANAGEMENT },
                  onNavigateToLiveTracking = { currentSubScreen = SecondarySubScreen.LIVE_TRACKING },
                  onNavigateToObd = { currentSubScreen = SecondarySubScreen.OBD_TELEMETRY },
                  onNavigateToDriverSpace = { currentSubScreen = SecondarySubScreen.DRIVER_SPACE },
                  onNavigateToCarRental = { currentSubScreen = SecondarySubScreen.CAR_RENTAL_FLEET },
                  onOpenDrawerMenu = { coroutineScope.launch { drawerState.open() } },
                  onOpenSearchByCity = {
                    storeSearchInitialTab = StoreSearchTab.BY_CITY
                    showStoreAndCitySearchModal = true
                  },
                  onOpenSearchInStore = {
                    storeSearchInitialTab = StoreSearchTab.IN_STORE
                    showStoreAndCitySearchModal = true
                  },
                  onOpenSearchPart = {
                    storeSearchInitialTab = StoreSearchTab.BY_PART
                    showStoreAndCitySearchModal = true
                  },
                  onOpenVendorAddPhotoAi = { showVendorAddPartPhotoAiModal = true },
                  onOpenAutoForum = { showAutoGroupForumModal = true },
                  onOpenProSocialNetwork = { showProB2bSocialNetworkModal = true },
                  onOpenMonthlyZipArchive = { showMonthlySalesZipArchiveModal = true },
                  onOpenHandsFreeVoiceSearch = { showHandsFreeVoiceSearchModal = true },
                  onOpenMekanikAiObd = { dtcCode ->
                    currentSubScreen = SecondarySubScreen.NONE
                    mekanikViewModel.setNavigationTab(MainNavigationTab.AI_DIAGNOSTIC)
                    if (!dtcCode.isNullOrBlank()) {
                      mekanikViewModel.askMekanikAi(dtcCode)
                    }
                  },
                  isRealtimeStreamEnabled = isRealtimeStreamEnabled,
                  onToggleRealtimeStream = { isRealtimeStreamEnabled = it },
                  onTriggerInstantRealtimeEvent = { category ->
                    triggerRealtimeNotification(category)
                  },
                  onOpenNotificationsCenter = {
                    mekanikViewModel.toggleNotificationModal(true)
                  },
                  onStartOnboardingGuide = {
                    onboardingStepIndex = 0
                    showInteractiveOnboardingOverlay = true
                  },
                  onOpenAccountSelector = {
                    showAccountSelectorModal = true
                  },
                  onOpenAccountAdmin2Fa = {
                    showAccountAdmin2FaModal = true
                  },
                  onOpenSaasPlatformSpace = { space ->
                    MekanikSaasPlatformRegistry.activeSaasSpace = space
                    initialSaasSpace = space
                    showSaasWebPlatformModal = true
                  }
                )
              }
              MainNavigationTab.RECHERCHE -> {
                VinSearchScreen(
                  uiState = uiState,
                  viewModel = mekanikViewModel
                )
              }
              MainNavigationTab.PIECES -> {
                PartsCatalogScreen(
                  uiState = uiState,
                  viewModel = mekanikViewModel,
                  onNavigateToVendors = { currentSubScreen = SecondarySubScreen.VENDORS_DIRECTORY }
                )
              }
              MainNavigationTab.MECA_VERIF -> {
                MecaVerifHistoryScreen(
                  initialVin = uiState.activeVehicle.vin,
                  isBodyshopAccount = uiState.currentAccountRole.isBodyshopAccount,
                  isProAccount = !uiState.currentAccountRole.isIndividualAccount,
                  onOpenScannerForVin = { mekanikViewModel.openCarteGriseScanner() }
                )
              }
              MainNavigationTab.ATELIER -> {
                WorkshopScreen(
                  uiState = uiState,
                  viewModel = mekanikViewModel
                )
              }
              MainNavigationTab.PROFIL -> {
                ProfileScreen(
                  uiState = uiState,
                  viewModel = mekanikViewModel,
                  onNavigateToFleet = { currentSubScreen = SecondarySubScreen.FLEET_MANAGEMENT },
                  onNavigateToLiveTracking = { currentSubScreen = SecondarySubScreen.LIVE_TRACKING },
                  onNavigateToObd = { currentSubScreen = SecondarySubScreen.OBD_TELEMETRY },
                  onNavigateToSubscriptions = { currentSubScreen = SecondarySubScreen.SUBSCRIPTION_PLANS },
                  onNavigateToDocs = { currentSubScreen = SecondarySubScreen.TECHNICAL_DOCS },
                  onNavigateToVendors = { currentSubScreen = SecondarySubScreen.VENDORS_DIRECTORY },
                  onNavigateToDriverSpace = { currentSubScreen = SecondarySubScreen.DRIVER_SPACE },
                  onNavigateToCarRental = { currentSubScreen = SecondarySubScreen.CAR_RENTAL_FLEET }
                )
              }
              MainNavigationTab.AI_DIAGNOSTIC -> {
                MekanikAiScreen(
                  uiState = uiState,
                  viewModel = mekanikViewModel
                )
              }
            }
          }
        }
      }

      // 0. Scanner Vision Étiquette Pièce (Code-Barres / QR / Compatibilité Châssis & Méthode d'Installation)
      if (isPartLabelVisionScannerOpen) {
        PartLabelVisionScannerModal(
          activeVehicle = uiState.activeVehicle,
          onOpenPartInCatalog = { part, category ->
            mekanikViewModel.openScannedPartInCatalog(part, category)
          },
          onAddPartToQuoteOrCart = { part ->
            mekanikViewModel.closePartLabelVisionScanner()
            mekanikViewModel.openQuoteGeneratorWithPart(part)
          },
          onDismiss = { mekanikViewModel.closePartLabelVisionScanner() }
        )
      }

      // 1. Camera & OCR Scanner Modal
      if (uiState.showCameraScanModal) {
        CameraScannerModal(
          targetType = uiState.scanTargetType,
          isScanning = uiState.isScanningActive,
          detectionResult = uiState.detectedScanResult,
          onTargetTypeChanged = { mekanikViewModel.setScanTargetType(it) },
          onApplyResult = { mekanikViewModel.applyScannedVin() },
          onDismiss = { mekanikViewModel.closeCameraScanner() }
        )
      }

      // 1b. Dedicated Carte Grise VIN Scanner Modal (avec Auto-Remplissage Ordre de Réparation)
      if (uiState.showCarteGriseScannerModal) {
        CarteGriseVinScannerModal(
          onVinDetected = { vin ->
            mekanikViewModel.applyScannedCarteGriseVin(vin)
          },
          onCreateRepairOrder = { scanPreset ->
            mekanikViewModel.autoFillRepairOrderFromScan(
              clientName = "Karim Meziane (${scanPreset.brandModel.take(15)})",
              clientPhone = "0550 42 19 88",
              plateNumber = scanPreset.plateNumber,
              vehicleModel = scanPreset.brandModel,
              vin = scanPreset.vin,
              mileage = "88500",
              notes = "Véhicule scanné via Carte Grise OCR (${scanPreset.engineCode}). Contrôle général & diagnostic."
            )
            mekanikViewModel.setNavigationTab(MainNavigationTab.ATELIER)
          },
          onDismiss = { mekanikViewModel.closeCarteGriseScanner() }
        )
      }

      // 2. Printable VIN certified report modal
      if (uiState.showVinReportModal && uiState.activeVinReport != null) {
        VinReportModal(
          report = uiState.activeVinReport!!,
          onDismiss = { mekanikViewModel.dismissModals() }
        )
      }

      // 3. Push & Real-Time Notifications Center Modal
      if (uiState.showNotificationModal) {
        NotificationCenterModal(
          notifications = uiState.notifications,
          onDismiss = { mekanikViewModel.toggleNotificationModal(false) },
          onMarkAsRead = { id -> mekanikViewModel.markNotificationAsRead(id) },
          onMarkAllAsRead = { mekanikViewModel.markAllNotificationsAsRead() },
          onSendSimulatedNotification = { title, msg, cat ->
            mekanikViewModel.sendSimulatedNotification(title, msg, cat)
          },
          isRealtimeStreamEnabled = isRealtimeStreamEnabled,
          onToggleRealtimeStream = { isRealtimeStreamEnabled = it },
          onTriggerInstantRealtimeCategory = { cat ->
            triggerRealtimeNotification(cat)
          }
        )
      }

      // 4. Digital Passport Modal
      if (uiState.showPassportModal) {
        DigitalPassportModal(
          passport = uiState.certifiedPassport,
          onDismiss = { mekanikViewModel.togglePassportModal(false) }
        )
      }

      // 5. Emergency SOS Modal
      if (uiState.showSosModal) {
        EmergencySosModal(
          activeRequest = uiState.activeSosRequest,
          towTrucks = uiState.towTrucksList,
          selectedType = uiState.selectedBreakdownType,
          onTypeSelected = { type ->
            // Update breakdown type
          },
          onTriggerSos = { type ->
            mekanikViewModel.triggerSosEmergency(type)
          },
          onSelectTowTruck = { truck ->
            mekanikViewModel.selectTowTruck(truck)
          },
          onDismiss = { mekanikViewModel.toggleSosModal(false) }
        )
      }

      // 6. Fiscal Invoice Modal
      if (uiState.showInvoiceModal && uiState.activeInvoice != null) {
        FiscalInvoiceModal(
          invoice = uiState.activeInvoice!!,
          onOpenPayment = { mekanikViewModel.togglePaymentModal(true) },
          onDismiss = { mekanikViewModel.toggleInvoiceModal(false) }
        )
      }

      // 7. Payment Gateway (CIB / Edahabia) Modal
      if (uiState.showPaymentModal && uiState.activeInvoice != null) {
        PaymentGatewayModal(
          invoice = uiState.activeInvoice!!,
          onConfirmPayment = { method ->
            mekanikViewModel.processPayment(method)
          },
          onDismiss = { mekanikViewModel.togglePaymentModal(false) }
        )
      }

      // 8. B2B Marketplace & Group Quotes Modal
      if (uiState.showGroupQuoteModal) {
        MarketplaceQuoteModal(
          offers = uiState.marketplaceOffers,
          onSubmitGroupQuote = { parts ->
            mekanikViewModel.submitGroupQuoteRequest(parts)
          },
          onDismiss = { mekanikViewModel.toggleGroupQuoteModal(false) }
        )
      }

      // 9. Universal Vehicle QR Passport Multi-Role Hub Modal
      if (uiState.showUniversalQrPassportDetailModal) {
        UniversalVehicleQrPassportModal(
          passport = uiState.activeScannedQrPassport,
          selectedTab = uiState.selectedQrPassportRoleTab,
          onTabSelected = { mekanikViewModel.setQrPassportRoleTab(it) },
          viewModel = mekanikViewModel,
          onDismiss = { mekanikViewModel.toggleUniversalQrPassportDetail(false) }
        )
      }

      // 10. Universal QR Scanner Modal
      if (uiState.showUniversalQrScannerModal) {
        UniversalQrScannerModal(
          registeredPassports = uiState.universalQrPassports,
          currentRole = uiState.currentAccountRole,
          viewModel = mekanikViewModel,
          onDismiss = { mekanikViewModel.toggleUniversalQrScanner(false) }
        )
      }

      // 11. Generate / Register Vehicle QR Modal
      if (uiState.showGenerateVehicleQrModal) {
        GenerateVehicleQrModal(
          viewModel = mekanikViewModel,
          onDismiss = { mekanikViewModel.toggleGenerateVehicleQrModal(false) }
        )
      }

      // 12. Fiscal Invoices & Rental Contracts PDF Download Modal
      if (uiState.showPdfDownloadModal && uiState.generatedPdfResult != null) {
        PdfDownloadSuccessModal(
          result = uiState.generatedPdfResult!!,
          onDismiss = { mekanikViewModel.dismissPdfModal() }
        )
      }

      // 13. Engine Sound Recording & AI Acoustic Fault Detection Modal
      if (uiState.showEngineSoundRecorderModal) {
        EngineSoundRecorderModal(
          uiState = uiState,
          viewModel = mekanikViewModel,
          onDismiss = { mekanikViewModel.closeEngineSoundRecorder() }
        )
      }

      // 14. Compte Entreprise Ultra Pro & Gestion Hiérarchique Multi-Comptes Modal
      if (uiState.showUltraProManagerModal) {
        UltraProAccountsManagerModal(
          organization = uiState.ultraProOrganization,
          subAccounts = uiState.ultraProSubAccounts,
          activeImpersonation = uiState.activeImpersonatedSubAccount,
          auditLogs = uiState.subAccountAuditLogs,
          partsRequisitions = uiState.partsRequisitions,
          onOpenCreatePartsRequisition = { mekanikViewModel.openCreatePartsRequisitionDialog() },
          onOpenPurchaseExtensionModal = { mekanikViewModel.openPurchaseExtensionDialog() },
          onOpenOemSoftwareHub = { mekanikViewModel.openOemSoftwareLauncher() },
          onOpenPurchaseSoftwareSupplementModal = { mekanikViewModel.openPurchaseFullSoftwareSupplementModal() },
          onApproveByWorkshopManager = { id -> mekanikViewModel.approvePartsRequisitionByWorkshopManager(id) },
          onPrepareByStorekeeper = { id -> mekanikViewModel.preparePartsRequisitionByStorekeeper(id) },
          onConfirmStockDispatchByHeadStorekeeper = { id -> mekanikViewModel.confirmStockDispatchByHeadStorekeeper(id) },
          onRejectPartsRequisition = { id, reason -> mekanikViewModel.rejectPartsRequisition(id, reason) },
          onAddSubAccountClick = { mekanikViewModel.openCreateSubAccountDialog() },
          onEditSubAccount = { mekanikViewModel.openCreateSubAccountDialog(it) },
          onDeleteSubAccount = { mekanikViewModel.deleteSubAccount(it) },
          onToggleActive = { id, active -> mekanikViewModel.toggleSubAccountActive(id, active) },
          onSwitchAccount = { mekanikViewModel.switchActiveSubAccount(it) },
          onDismiss = { mekanikViewModel.closeUltraProManager() }
        )
      }

      // 15. Create / Edit Ultra Pro Sub-Account Modal
      if (uiState.showCreateSubAccountModal) {
        CreateUltraProSubAccountModal(
          initialAccount = uiState.selectedSubAccountForEdit,
          currentAccounts = uiState.ultraProSubAccounts,
          onSaveAccount = { mekanikViewModel.addNewSubAccount(it) },
          onDismiss = { mekanikViewModel.closeCreateSubAccountDialog() }
        )
      }

      // 16. Create Workshop Parts Requisition Modal (Mécanicien ➔ Chef d'Atelier)
      if (uiState.showCreatePartsRequisitionModal) {
        CreatePartsRequisitionModal(
          activeSubAccount = uiState.activeImpersonatedSubAccount,
          onSubmitRequisition = { vehicleModel, vin, plate, parts, notes ->
            mekanikViewModel.submitMechanicPartsRequisition(vehicleModel, vin, plate, parts, notes)
          },
          onDismiss = { mekanikViewModel.closeCreatePartsRequisitionDialog() }
        )
      }

      // 17. Purchase Sub-Account Extension Pack Modal (+2 Sous-Comptes / 2 500 DA)
      if (uiState.showPurchaseExtensionModal) {
        PurchaseExtensionPackModal(
          currentAllowedAccounts = uiState.ultraProOrganization.maxSubAccountsAllowed,
          purchasedPacks = uiState.ultraProOrganization.purchasedExtensionPacksCount,
          onPurchaseConfirmed = { count ->
            mekanikViewModel.purchaseSubAccountExtensionPack(count)
          },
          onDismiss = { mekanikViewModel.closePurchaseExtensionDialog() }
        )
      }

      // 18. OEM Software Hub Launcher Modal (ETKA, ServiceBox, Dialogys, Microcat, TecDoc...)
      if (uiState.showOemSoftwareLauncherModal) {
        OemSoftwareLauncherModal(
          tools = uiState.oemSoftwareTools,
          config = uiState.workshopSoftwareConfig,
          supplement = uiState.fullSoftwareSupplement,
          selectedTool = uiState.selectedOemToolForLaunch,
          selectedGroupFilter = uiState.selectedOemToolGroupFilter,
          onSelectGroupMode = { mekanikViewModel.setWorkshopActiveOemMode(it) },
          onLaunchTool = { mekanikViewModel.launchOemSoftwareTool(it) },
          onOpenPurchaseSupplementModal = { mekanikViewModel.openPurchaseFullSoftwareSupplementModal() },
          onDismiss = { mekanikViewModel.closeOemSoftwareLauncher() }
        )
      }

      // 19. Purchase Full Software Supplement Package Modal (+10 000 DA)
      if (uiState.showPurchaseFullSoftwareSupplementModal) {
        PurchaseFullSoftwarePackageModal(
          supplement = uiState.fullSoftwareSupplement,
          onConfirmPurchase = { method ->
            mekanikViewModel.purchaseFullSoftwarePackage(method)
          },
          onDismiss = { mekanikViewModel.closePurchaseFullSoftwareSupplementModal() }
        )
      }

      // 20. Dev Sandbox, Multi-Sources API Gateway & Local Cache Hub Modal (Zéro Frais)
      if (uiState.showDevApiManagerModal) {
        DevSandboxApiManagerModal(
          currentMode = uiState.apiDataSourceMode,
          metrics = uiState.apiTelemetryMetrics,
          endpoints = uiState.apiEndpointsList,
          licensePlateMatches = uiState.licensePlateMatches,
          onSelectMode = { mekanikViewModel.setApiDataSourceMode(it) },
          onClearCache = { mekanikViewModel.clearLocalApiCache() },
          onTestPing = { mekanikViewModel.pingApiEndpoint(it) },
          onSelectPlateMatch = { mekanikViewModel.selectLicensePlateMatch(it) },
          onDismiss = { mekanikViewModel.closeDevApiManagerModal() }
        )
      }

      // 21. Multi-Appareils Atelier (Max 3) & Sécurité 2FA Google Authenticator Modal
      if (uiState.showWorkshop2FAModal) {
        WorkshopMultiDevice2FAModal(
          securityConfig = uiState.workshop2FAConfig,
          onToggle2Fa = { mekanikViewModel.toggleWorkshop2FA(it) },
          onRevokeDevice = { mekanikViewModel.revokeWorkshopDevice(it) },
          onPairNewDevice = { name, model, type, code ->
            mekanikViewModel.pairNewWorkshopDevice(name, model, type, code)
          },
          onDismiss = { mekanikViewModel.closeWorkshop2FAModal() }
        )
      }

      // 22. Passerelle Magasin de Pièces + Atelier (Compte Unique - +5 000 DA) Modal
      if (uiState.showWorkshopStoreLinkModal) {
        WorkshopStoreLinkModal(
          storeConfig = uiState.workshopStoreLinkConfig,
          onToggleStoreLinked = { mekanikViewModel.toggleWorkshopStoreLinked(it) },
          onSwitchActiveView = { mekanikViewModel.switchWorkshopActiveView(it) },
          onUpdateStoreDetails = { name, rc, wilaya, phone ->
            mekanikViewModel.updateWorkshopStoreDetails(name, rc, wilaya, phone)
          },
          onDismiss = { mekanikViewModel.closeWorkshopStoreLinkModal() }
        )
      }

      // 23. Inspection & État des Lieux Numérique 360° Modal
      if (uiState.showInspectionModal) {
        VehicleInspectionModal(
          inspections = uiState.inspections,
          onSaveInspection = { inspection ->
            mekanikViewModel.saveVehicleInspection(inspection)
          },
          onDismiss = { mekanikViewModel.closeInspectionModal() }
        )
      }

      // 24. Messagerie Client WhatsApp & SMS Modal
      if (uiState.showMessageModal) {
        val clientDispatch = uiState.prefilledMessageClient
        ClientMessagingModal(
          initialClientName = clientDispatch?.clientName ?: "Sofiane Benali",
          initialClientPhone = clientDispatch?.clientPhone ?: "0550 12 34 56",
          initialVehicleModel = clientDispatch?.vehicleModel ?: "Peugeot 308 (1.6 BlueHDi)",
          initialPlateNumber = clientDispatch?.licensePlate ?: "01429-122-16",
          initialAmountDzd = clientDispatch?.quoteOrInvoiceAmountDzd ?: "18 500",
          templates = uiState.messagingTemplates,
          onDismiss = { mekanikViewModel.closeClientMessagingModal() }
        )
      }

      // 25. Logiciel Complet de Gestion de Stock & ERP Pièces de Rechange (StockPro WMS)
      if (uiState.showBarcodeScannerModal) {
        BarcodeInventoryScannerModal(
          inventory = uiState.barcodeInventory,
          movementLogs = uiState.stockMovementLogs,
          supplierOrders = uiState.supplierPurchaseOrders,
          scannedInvoices = uiState.scannedPurchaseInvoices,
          printedLabels = uiState.printedThermalLabels,
          onUpdateStock = { id, delta ->
            mekanikViewModel.updateBarcodeStock(id, delta)
          },
          onRecordMovement = { itemId, type, qty, docRef, operator ->
            mekanikViewModel.recordStockMovement(itemId, type, qty, docRef, operator)
          },
          onCreateNewItem = { partName, oem, bc, brand, cat, initQty, minT, buyCost, sellPrice, shelf, supp, rot ->
            mekanikViewModel.createNewInventoryItem(partName, oem, bc, brand, cat, initQty, minT, buyCost, sellPrice, shelf, supp, rot)
          },
          onUpdateItemPricing = { itemId, buyCost, sellPrice, minT, shelf ->
            mekanikViewModel.updateInventoryItemPricingAndThresholds(itemId, buyCost, sellPrice, minT, shelf)
          },
          onGenerateAutoRestockOrder = {
            mekanikViewModel.generateAutoRestockPurchaseOrder()
          },
          onReceiveSupplierOrder = { orderId ->
            mekanikViewModel.receiveSupplierPurchaseOrder(orderId)
          },
          onUpdateInvoiceLinePriceChoice = { invoiceId, lineId, choice, customSalePrice ->
            mekanikViewModel.updateScannedInvoiceLinePriceChoice(invoiceId, lineId, choice, customSalePrice)
          },
          onUpdateAllInvoiceLinesPriceStrategy = { invoiceId, keepSame ->
            mekanikViewModel.updateAllScannedInvoiceLinesPriceStrategy(invoiceId, keepSame)
          },
          onUpdateInvoiceLinePlacementAndQty = { invoiceId, lineId, rayon, etagere, bac, qty ->
            mekanikViewModel.updateScannedInvoiceLinePlacementAndQty(invoiceId, lineId, rayon, etagere, bac, qty)
          },
          onAddLineToInvoice = { invoiceId, partName, oem, brand, cat, qty, buyPrice, salePrice, rayon, etagere, bac ->
            mekanikViewModel.addLineToScannedPurchaseInvoice(invoiceId, partName, oem, brand, cat, qty, buyPrice, salePrice, rayon, etagere, bac)
          },
          onIntegrateInvoiceAndGenerateLabels = { invoiceId ->
            mekanikViewModel.integrateScannedInvoiceAndGenerateLabels(invoiceId)
          },
          onPrintSingleLabel = { labelId ->
            mekanikViewModel.printSingleThermalLabel(labelId)
          },
          onPrintAllLabelsForInvoice = { invNum ->
            mekanikViewModel.printAllThermalLabelsForInvoice(invNum)
          },
          onAddToQuote = { item ->
            mekanikViewModel.addBarcodeItemToCurrentQuote(item)
          },
          onDismiss = { mekanikViewModel.closeBarcodeScannerModal() }
        )
      }

      // 26. Barème des Temps Constructeur OEM & Chiffrage M.O Modal
      if (uiState.showLaborStandardModal) {
        OemLaborStandardModal(
          operations = uiState.laborStandardOperations,
          currentHourlyRateDzd = uiState.workshopHourlyRateDzd,
          onApplyOperationsToQuote = { ops, totalCost ->
            mekanikViewModel.applyLaborStandardOperationsToQuote(ops, totalCost)
          },
          onDismiss = { mekanikViewModel.closeLaborStandardModal() }
        )
      }

      // 27. Planning Visuel des Ponts & Charge Atelier Modal
      if (uiState.showBayPlannerModal) {
        WorkshopBayPlannerModal(
          bays = uiState.workshopBays,
          waitingVehicles = uiState.waitingQueueVehicles,
          onToggleBayTimer = { mekanikViewModel.toggleBayTimer(it) },
          onCompleteBayOperation = { mekanikViewModel.completeBayOperation(it) },
          onAssignVehicleToBay = { bayId, waitId ->
            mekanikViewModel.assignVehicleToBay(bayId, waitId)
          },
          onDismiss = { mekanikViewModel.closeBayPlannerModal() }
        )
      }

      // 28. Suivi des Garanties Pièces & Retours SAV / RMA Modal
      if (uiState.showWarrantyRmaModal) {
        WarrantyRmaModal(
          warranties = uiState.partWarranties,
          onCreateRmaClaim = { warId, reason ->
            mekanikViewModel.createRmaClaim(warId, reason)
          },
          onRegisterNewWarranty = { name, ref, brand, plate ->
            mekanikViewModel.registerNewPartWarranty(name, ref, brand, plate)
          },
          onDismiss = { mekanikViewModel.closeWarrantyRmaModal() }
        )
      }

      // 29. Portail Client Partageable & Validation Devis par Signature à Distance Modal
      if (uiState.showRemoteQuotePortalModal) {
        ClientRemoteQuotePortalModal(
          session = uiState.remoteClientQuoteSession,
          onToggleOptionalItem = { mekanikViewModel.toggleRemoteOptionalQuoteItem(it) },
          onApproveAndSignRemotely = { mekanikViewModel.approveAndSignRemoteQuote() },
          onAdvanceLiveStep = { mekanikViewModel.advanceRemoteQuoteLiveStep() },
          onSendWhatsAppLink = {
            mekanikViewModel.closeRemoteQuotePortalModal()
            mekanikViewModel.openClientMessagingModal(
              clientName = uiState.remoteClientQuoteSession.clientName,
              clientPhone = uiState.remoteClientQuoteSession.clientPhone,
              vehicleModel = uiState.remoteClientQuoteSession.vehicleModel,
              plateNumber = uiState.remoteClientQuoteSession.licensePlate,
              amount = uiState.remoteClientQuoteSession.finalApprovedTotalDzd.toString()
            )
          },
          onDismiss = { mekanikViewModel.closeRemoteQuotePortalModal() }
        )
      }

      // 30. Mode Hors-Ligne Intelligent & Synchronisation Auto Modal
      if (uiState.showOfflineSyncModal) {
        OfflineSyncCenterModal(
          isOfflineModeEnabled = uiState.isOfflineModeEnabled,
          pendingSyncActions = uiState.pendingSyncActions,
          cachedPacks = uiState.offlineCachedPacks,
          onToggleOfflineMode = { mekanikViewModel.toggleOfflineMode(it) },
          onSyncAllPendingNow = { mekanikViewModel.syncAllPendingOfflineActions() },
          onDownloadOfflinePack = { mekanikViewModel.downloadOfflinePack(it) },
          onDismiss = { mekanikViewModel.closeOfflineSyncModal() }
        )
      }

      // 30b. Stratégie de Mise en Cache Locale Robuste (Room SQLite) : Schémas Techniques (0ms), Diagnostics Enregistrés & Historique Pannes Clients
      if (isOfflineCacheHubModalOpen) {
        WorkshopOfflineCacheAndClientHistoryModal(
          initialTab = offlineCacheHubInitialTab,
          networkCondition = workshopNetworkCondition,
          cacheSummary = localCacheSummary,
          cachedSchematics = cachedSchematics,
          savedDiagnostics = savedDiagnostics,
          clientFaultHistory = clientFaultHistory,
          vehicleRepairLogs = vehicleRepairLogs,
          activeVehicleModel = "${uiState.activeVehicle.brand} ${uiState.activeVehicle.model} (${uiState.activeVehicle.engineCode})",
          activeVin = uiState.activeVehicle.vin,
          onSelectNetworkCondition = { mekanikViewModel.setWorkshopNetworkCondition(it) },
          onPreloadAllSchematics = { mekanikViewModel.preloadAllSchematicsToLocalCache() },
          onTogglePinSchematic = { mekanikViewModel.togglePinSchematic(it) },
          onRemoveSchematic = { mekanikViewModel.removeSchematicFromCache(it) },
          onSyncPendingDiagnostics = { mekanikViewModel.syncPendingDiagnostics() },
          onDeleteSavedDiagnostic = { mekanikViewModel.deleteSavedDiagnostic(it) },
          onAddClientFaultRecord = { cName, cPhone, vModel, vin, plate, km, fTitle, symp, sys, dtc, diagConc, action, parts, cost, tech, status ->
            mekanikViewModel.addClientFaultHistoryRecord(
              clientName = cName,
              clientPhone = cPhone,
              vehicleModel = vModel,
              vin = vin,
              licensePlate = plate,
              mileageKm = km,
              faultTitle = fTitle,
              symptoms = symp,
              affectedSystem = sys,
              dtcCodes = dtc,
              diagnosticConclusion = diagConc,
              repairAction = action,
              partsReplaced = parts,
              costDzd = cost,
              technicianName = tech,
              status = status
            )
          },
          onUpdateClientFaultStatus = { id, newStatus -> mekanikViewModel.updateClientFaultStatus(id, newStatus) },
          onDeleteClientFaultRecord = { id -> mekanikViewModel.deleteClientFaultRecord(id) },
          onDismiss = { mekanikViewModel.closeOfflineCacheHubModal() }
        )
      }

      // 31. Comparateur Multi-Fournisseurs B2B & Suivi Livreur Modal
      if (uiState.showB2bComparatorModal) {
        B2bSupplierComparatorModal(
          offers = uiState.supplierComparisonOffers,
          activeOrders = uiState.activeB2bOrders,
          onOrderFromSupplier = { mekanikViewModel.orderPartFromSupplierB2b(it) },
          onAdvanceOrderDelivery = { mekanikViewModel.advanceB2bOrderDelivery(it) },
          onDismiss = { mekanikViewModel.closeB2bComparatorModal() }
        )
      }

      // 32. Calculateur de Coût de Revient Kilométrique (TCO) & Carburant Modal
      if (uiState.showTcoCalculatorModal) {
        VehicleTcoCalculatorModal(
          summaries = uiState.vehicleTcoSummaries,
          expenses = uiState.vehicleExpenses,
          onAddExpense = { cat, title, amt, km ->
            mekanikViewModel.addVehicleExpense(cat, title, amt, km)
          },
          onDismiss = { mekanikViewModel.closeTcoCalculatorModal() }
        )
      }

      // 33. Constat Amiable Digital & Déclaration Sinistre Guidée Modal
      if (uiState.showAccidentReportModal) {
        DigitalAccidentReportModal(
          reports = uiState.accidentReports,
          onSubmitNewReport = { nameB, vehB, insB, circs, impact ->
            mekanikViewModel.submitDigitalAccidentReport(nameB, vehB, insB, circs, impact)
          },
          onDismiss = { mekanikViewModel.closeAccidentReportModal() }
        )
      }

      // 34. Schémas Électriques Interactifs, Fusibles & Pinout ECU Modal
      if (uiState.showWiringFuseEcuModal) {
        WiringFuseEcuModal(
          fuses = uiState.fuseBoxItems,
          pinouts = uiState.ecuPinouts,
          onToggleFuseStatus = { mekanikViewModel.toggleFuseIntactStatus(it) },
          onDismiss = { mekanikViewModel.closeWiringFuseEcuModal() }
        )
      }

      // 35. Calibrage ADAS & Bilan Santé Batterie EV / Hybride (SoH) Modal
      if (uiState.showAdasEvBatteryModal) {
        AdasEvBatteryModal(
          adasCalibrations = uiState.adasCalibrations,
          evBatteryReports = uiState.evBatteryReports,
          onRunAdasCalibration = { mekanikViewModel.runAdasCalibration(it) },
          onToggleEvHighVoltageLockout = { mekanikViewModel.toggleEvHighVoltageLockout(it) },
          onDismiss = { mekanikViewModel.closeAdasEvBatteryModal() }
        )
      }

      // 36. Bulletins de Service Technique (TSB), Rappels & Pannes Connues Modal
      if (uiState.showTechnicalBulletinsModal) {
        TechnicalServiceBulletinsModal(
          bulletins = uiState.technicalBulletins,
          onOrderModifiedPart = { mekanikViewModel.orderModifiedPartFromTsb(it) },
          onDismiss = { mekanikViewModel.closeTechnicalBulletinsModal() }
        )
      }

      // 37. Bourse Pièces d'Occasion Certifiées (Casses Auto) & Consignes Échange Standard Modal
      if (uiState.showCertifiedUsedPartsModal) {
        CertifiedUsedPartsExchangeModal(
          usedParts = uiState.certifiedUsedParts,
          coreConsignes = uiState.coreReturnConsignes,
          onReserveUsedPart = { mekanikViewModel.reserveCertifiedUsedPart(it) },
          onAdvanceCoreReturnStatus = { mekanikViewModel.advanceCoreReturnStatus(it) },
          onDismiss = { mekanikViewModel.closeCertifiedUsedPartsModal() }
        )
      }

      // 38. Registre Climatisation A/C (Gaz R134a / R1234yf) & Traçabilité Fluides Modal
      if (uiState.showAcRefrigerantModal) {
        AcRefrigerantLogModal(
          vehicleSpecs = uiState.acVehicleSpecs,
          serviceLogs = uiState.acServiceLogs,
          onLogAcChargeFromSpec = { spec, plate, rec, temp ->
            mekanikViewModel.logAcChargeFromSpec(spec, plate, rec, temp)
          },
          onDismiss = { mekanikViewModel.closeAcRefrigerantModal() }
        )
      }

      // 39. Contrats Location Court Terme, Caution & Comparateur Départ/Retour Modal
      if (uiState.showShortTermRentalModal) {
        ShortTermRentalCheckModal(
          contracts = uiState.shortTermRentalContracts,
          onUpdateRentalReturnMetrics = { id, km, fuel ->
            mekanikViewModel.updateRentalReturnMetrics(id, km, fuel)
          },
          onCloseContractAndReleaseDeposit = { mekanikViewModel.closeRentalContractAndReleaseDeposit(it) },
          onDismiss = { mekanikViewModel.closeShortTermRentalModal() }
        )
      }

      // 40. Catalogues Web OEM Gratuits & Vues Éclatées (CatCar.info, 7Zap, PartsSouq, NemigaParts, IFInterface, WebAutoCats) Modal
      if (uiState.showWebOemCatalogsModal) {
        WebOemExplodedCatalogsModal(
          platforms = uiState.freeWebOemPlatforms,
          explodedDiagrams = uiState.oemExplodedDiagrams,
          onSelectCalloutForQuote = { callout, sheet ->
            mekanikViewModel.selectExplodedCalloutForQuote(callout, sheet)
          },
          onAddCalloutToSmartCart = { callout, sheet ->
            mekanikViewModel.addCalloutFromWebExplodedToSmartCart(callout, sheet)
          },
          onDismiss = { mekanikViewModel.closeWebOemCatalogsModal() }
        )
      }

      // 41. Vérificateur Anti-Erreur Montage (Codes PR VAG, N° OPR/DAM PSA, Indices Boîte/Moteur Renault) Modal
      if (uiState.showPartCompatibilityModal) {
        PartCompatibilityPrOprModal(
          rules = uiState.fitmentVerificationRules,
          onAddNewRuleCheck = { code, vehicle ->
            mekanikViewModel.addCustomFitmentVerification(code, vehicle)
          },
          onDismiss = { mekanikViewModel.closePartCompatibilityModal() }
        )
      }

      // 42. Rentabilité par OR, Productivité Mécaniciens & Fûts d'Huile Vrac 208L Modal
      if (uiState.showWorkshopProfitabilityModal) {
        WorkshopProfitabilityEfficiencyModal(
          repairOrders = uiState.repairOrderProfitability,
          bulkOilDrums = uiState.bulkOilDrums,
          onDispenseOilFromDrum = { drumId, liters ->
            mekanikViewModel.dispenseOilFromBulkDrum(drumId, liters)
          },
          onRefillOilDrum = { mekanikViewModel.refillBulkOilDrum(it) },
          onDismiss = { mekanikViewModel.closeWorkshopProfitabilityModal() }
        )
      }

      // 43. Scanner Pré-Contrôle Technique (Zéro Contre-Visite) & Véhicules de Courtoisie Modal
      if (uiState.showPreCtCourtesyModal) {
        PreTechnicalInspectionCourtesyModal(
          preCtPoints = uiState.preCtInspectionPoints,
          courtesyVehicles = uiState.courtesyLoanerVehicles,
          onFixPreCtPoint = { mekanikViewModel.fixPreCtInspectionPoint(it) },
          onToggleCourtesyVehicleLoan = { mekanikViewModel.toggleCourtesyVehicleLoan(it) },
          onDismiss = { mekanikViewModel.closePreCtCourtesyModal() }
        )
      }

      // 44. Portail Super-Administrateur : Connexion, Employés Admin, Approbation des Inscriptions/Forfaits & Error Logger Firebase
      if (uiState.showSuperAdminModal) {
        val context = LocalContext.current
        SuperAdminPortalModal(
          adminSession = uiState.superAdminSession,
          staffMembers = uiState.adminStaffMembers,
          registrationRequests = uiState.registrationRequests,
          planSubscriptionRequests = uiState.planSubscriptionRequests,
          auditLogs = uiState.adminAuditLogs,
          onLoginAdmin = { email, pwd -> mekanikViewModel.loginSuperAdmin(email, pwd) },
          onLogoutAdmin = { mekanikViewModel.logoutSuperAdmin() },
          onApproveRegistration = { mekanikViewModel.approveRegistrationRequest(it) },
          onRejectRegistration = { mekanikViewModel.rejectRegistrationRequest(it) },
          onToggleSuspendRegistration = { mekanikViewModel.toggleSuspendRegistrationRequest(it) },
          onGenerateActivationKeyForRegistration = { mekanikViewModel.generateActivationKeyForRegistration(it) },
          onApproveAllPendingRegistrations = { mekanikViewModel.approveAllPendingRegistrations() },
          onCreateTestRegistration = { company, manager, email, wilaya, role, rc, plan ->
            mekanikViewModel.createTestRegistrationRequest(company, manager, email, wilaya, role, rc, plan)
          },
          onApprovePlanSubscription = { mekanikViewModel.approvePlanSubscriptionRequest(it) },
          onRejectPlanSubscription = { mekanikViewModel.rejectPlanSubscriptionRequest(it) },
          onExtendPlanSubscription = { mekanikViewModel.extendPlanSubscriptionRequest(it) },
          onApproveAllPendingSubscriptions = { mekanikViewModel.approveAllPendingSubscriptions() },
          onCreateTestPlanSubscription = { accountName, planTitle, amountDzd, method, proof, role ->
            mekanikViewModel.createTestPlanSubscriptionRequest(accountName, planTitle, amountDzd, method, proof, role)
          },
          onCreateAdminStaffMember = { fullName, email, pinCode, role, wilayas, canReg, canKey, canPay, canSusp ->
            mekanikViewModel.createAdminStaffMember(
              fullName,
              email,
              pinCode,
              role,
              wilayas,
              canReg,
              canKey,
              canPay,
              canSusp
            )
          },
          onToggleStaffActive = { mekanikViewModel.toggleAdminStaffActive(it) },
          onSwitchAdminOperator = { mekanikViewModel.switchActiveAdminOperator(it) },
          onSwitchToRolePreview = { role ->
            mekanikViewModel.switchAccountRole(role)
            mekanikViewModel.closeSuperAdminModal()
          },
          onDismiss = { mekanikViewModel.closeSuperAdminModal() },
          initialSelectedTab = superAdminInitialTab,
          errorLoggerState = errorLoggerState,
          onSelectErrorCategoryFilter = { mekanikViewModel.setErrorLoggerCategoryFilter(it) },
          onSelectErrorSeverityFilter = { mekanikViewModel.setErrorLoggerSeverityFilter(it) },
          onSelectErrorStatusFilter = { mekanikViewModel.setErrorLoggerStatusFilter(it) },
          onSearchErrorLogs = { mekanikViewModel.setErrorLoggerSearchQuery(it) },
          onSimulateCrashLog = { mekanikViewModel.simulateCrashErrorLog(context) },
          onSimulateApiFailureLog = { mekanikViewModel.simulateApiConnectionFailureLog(context) },
          onSimulatePersistenceFailureLog = { mekanikViewModel.simulateLocalPersistenceFailureLog(context) },
          onSimulateClientBugReport = { mekanikViewModel.simulateClientBugReport(context) },
          onToggleAiAutoRepair = { mekanikViewModel.toggleAiAutoRepair(it) },
          onRepairErrorWithAi = { errorId -> mekanikViewModel.repairSingleErrorWithAi(errorId, context) },
          onRepairAllOpenErrorsWithAi = { mekanikViewModel.repairAllOpenErrorsWithAi(context) },
          onUpdateErrorStatusAndNote = { errorId, status, note ->
            mekanikViewModel.updateErrorLogStatusAndNote(errorId, status, note, context)
          },
          onDeleteErrorLog = { errorId ->
            mekanikViewModel.deleteErrorLogEntry(errorId, context)
          },
          onPurgeResolvedErrorLogs = {
            mekanikViewModel.purgeResolvedErrorLogs(context)
          }
        )
      }

      // 44B. MekanikAI Guardian 24/7 : Système d'Auto-Réparation Automatique & Signalement de Bug Client
      if (isClientBugAiRepairModalOpen) {
        val context = LocalContext.current
        ClientBugReportAndAiRepairModal(
          errorLoggerState = errorLoggerState,
          currentUserEmail = uiState.superAdminSession.currentOperatorEmail,
          onSubmitBugAndAutoRepair = { moduleSource, bugTitle, bugDesc, severity ->
            mekanikViewModel.submitClientBugReportAndAutoRepair(
              moduleSource = moduleSource,
              bugTitle = bugTitle,
              bugDescription = bugDesc,
              severity = severity,
              context = context
            )
          },
          onRepairAllOpenIncidents = { mekanikViewModel.repairAllOpenErrorsWithAi(context) },
          onOpenSuperAdminErrorLogger = {
            mekanikViewModel.closeClientBugAiRepairModal()
            mekanikViewModel.openSuperAdminErrorLogger()
          },
          onDismiss = { mekanikViewModel.closeClientBugAiRepairModal() }
        )
      }

      // 44C. Accès Libre Sans Compte + Panier Schéma Éclaté + Carte Google Maps Exacte + Compte Particulier (3 500 DA/an)
      if (isSmartCartFreeAccessMapModalOpen) {
        SmartCartFreeAccessAndMapModal(
          initialTab = smartCartModalInitialTab,
          smartCartItems = smartCartItems,
          particulierAccountState = particulierAccountState,
          onUpdateCartQuantity = { id, delta -> mekanikViewModel.updateSmartCartQuantity(id, delta) },
          onRemoveCartItem = { id -> mekanikViewModel.removeSmartCartItem(id) },
          onAddExtraVinToParticulier = { vin, label, engine, year ->
            mekanikViewModel.addExtraVinToParticulier(vin, label, engine, year)
          },
          onRemoveExtraVinFromParticulier = { vin ->
            mekanikViewModel.removeExtraVinFromParticulier(vin)
          },
          onSelectParticulierTier = { tier ->
            mekanikViewModel.selectParticulierPlanTier(tier)
          },
          onDismiss = { mekanikViewModel.closeSmartCartFreeAccessMapModal() }
        )
      }

      // 45. Portail Compte Démo 3 Jours Automatique & Déblocage par Clé d'Activation
      if (uiState.showDemoTrialActivationModal) {
        DemoTrialActivationModal(
          demoStatus = uiState.demoTrialStatus,
          onSimulatePhaseChange = { mekanikViewModel.simulateDemoTrialPhase(it) },
          onActivateWithKey = { mekanikViewModel.activateAccountWithLicenseKey(it) },
          onRequestActivationKeyFromAdmin = { method, ref ->
            mekanikViewModel.requestActivationKeyFromAdmin(method, ref)
          },
          onSimulateNewSignupWithAutoDemo = { company, email, role ->
            mekanikViewModel.simulateNewSignupWithAutoDemo(company, email, role)
          },
          onConsumeDemoQuotaSample = { mekanikViewModel.consumeDemoQuotaSample() },
          onOpenSuperAdminConsole = {
            mekanikViewModel.closeDemoTrialActivationModal()
            mekanikViewModel.openSuperAdminModal()
          },
          onDismiss = { mekanikViewModel.closeDemoTrialActivationModal() }
        )
      }

      // 46. Suite DMS « WinMotor Next & Peugeot Service Box V14 » (T1/T2/T3 • Pointage Compagnon • Garantie/VOR)
      if (uiState.showWinMotorServiceBoxModal) {
        WinMotorServiceBoxModal(
          ratesConfig = uiState.winMotorRatesConfig,
          operations = uiState.winMotorOperations,
          serviceBoxBaskets = uiState.serviceBoxBaskets,
          companionClocks = uiState.companionClockEntries,
          multiPayerOrders = uiState.multiPayerSplitOrders,
          backorderVorItems = uiState.backorderVorItems,
          onUpdateLaborRates = { t1, t2, t3 ->
            mekanikViewModel.updateWinMotorLaborRates(t1, t2, t3)
          },
          onToggleOperationSelection = { mekanikViewModel.toggleWinMotorOperationSelection(it) },
          onImportServiceBoxBasketToOrder = { mekanikViewModel.importServiceBoxBasketToOrder(it) },
          onCreateRepairOrderFromWinMotorSelection = {
            mekanikViewModel.createRepairOrderFromWinMotorSelection()
          },
          onUpdateCompanionClockStatus = { id, status, addCth ->
            mekanikViewModel.updateCompanionClockStatus(id, status, addCth)
          },
          onUpdateMultiPayerSplit = { id, clientPct, warrantyPct, internalPct ->
            mekanikViewModel.updateMultiPayerSplit(id, clientPct, warrantyPct, internalPct)
          },
          onReceiveAndAllocateVorPart = { mekanikViewModel.receiveAndAllocateVorPart(it) },
          onDismiss = { mekanikViewModel.closeWinMotorServiceBoxModal() }
        )
      }

      // 47. MekanikOne Unified Auto-API Hub (11 APIs + Cascade + Les 6 Options Indispensables)
      if (uiState.showUnifiedAutoApiHubModal) {
        val context = LocalContext.current
        UnifiedAutoApiHubModal(
          connectors = uiState.unifiedApiConnectors,
          fallbackChainConfigs = uiState.fallbackChainConfigs,
          sinoRecords = uiState.sinoChineseVinRecords,
          demoScenarios = uiState.reverseEngineeredScenarios,
          lifeOfCapoBrands = uiState.lifeOfCapoBrands,
          activeTrace = uiState.activeUnifiedApiTrace,
          initialTabIndex = uiState.unifiedHubInitialTabIndex,
          ocrPresets = uiState.ocrChassisCascadePresets,
          crossRefBridges = uiState.sinoEuroCrossRefBridges,
          autoLearnedCacheEntries = uiState.autoLearnedCacheEntries,
          oneClickQuote = uiState.oneClickAutoQuoteBundle,
          selectedQuoteTierIndex = uiState.selectedQuoteTierIndex,
          fluidSpecs = uiState.fluidAndServiceResetSpecs,
          qrPassportSync = uiState.qrPassportCascadeSyncRecord,
          onExecuteUnifiedQuery = { query, scenarioId ->
            mekanikViewModel.executeUnifiedAutoApiQuery(query, scenarioId)
          },
          onUpdateProviderHealth = { provider, state ->
            mekanikViewModel.setApiProviderHealthState(provider, state)
          },
          onMoveProviderPriorityUp = { provider ->
            mekanikViewModel.moveApiProviderPriorityUp(provider)
          },
          onSimulateOutageAndFallback = {
            mekanikViewModel.simulateApiOutageAndFallback()
          },
          onSimulateLegacyApisFallback = {
            mekanikViewModel.simulateLegacyApisFallback()
          },
          onResetFallbackChainOnline = {
            mekanikViewModel.resetFallbackChainOnline()
          },
          onRunOcrToCascade = { rawOcr ->
            mekanikViewModel.runOcrChassisToCascade(rawOcr)
          },
          onSelectCrossRefAndAddToQuote = { bridge, iam ->
            mekanikViewModel.selectCrossRefAndAddToOneClickQuote(bridge, iam)
          },
          onTriggerAutoLearnCache = {
            mekanikViewModel.triggerAutoLearnActiveTraceToRoom()
          },
          onRecallFromOfflineRoomZeroMs = { vin ->
            mekanikViewModel.recallVehicleFromOfflineRoomZeroMs(vin)
          },
          onSelectQuoteTier = { tierIdx ->
            mekanikViewModel.selectOneClickQuoteTier(tierIdx)
          },
          onCreateWorkshopOrderFromOneClickQuote = {
            mekanikViewModel.createWorkshopOrderFromOneClickQuote()
          },
          onExportOneClickQuotePdf = {
            mekanikViewModel.createWorkshopOrderFromOneClickQuote()
            mekanikViewModel.uiState.value.activeRepairOrder?.let { order ->
              mekanikViewModel.generateAndDownloadRepairOrderPdf(context, order)
            }
          },
          onExecuteObdResetRoutine = { spec ->
            mekanikViewModel.executeObdServiceResetRoutine(spec)
          },
          onSyncAndOpenUniversalQrPassport = {
            mekanikViewModel.syncAndOpenUniversalQrPassportFromCascade()
          },
          onApplyVehicleToApp = { record ->
            mekanikViewModel.applySinoVehicleFromUnifiedApi(record)
          },
          onLaunchScenarioInAiAndWorkshop = { scenario ->
            mekanikViewModel.launchReverseEngineeredScenarioInAi(scenario)
          },
          onDismiss = { mekanikViewModel.closeUnifiedAutoApiHubModal() }
        )
      }

      // 48. Les 6 Boucliers de Défense & Contrôle d'Accès RBAC Firebase Auth (SuperAdmin / Gestionnaire / Technicien)
      if (uiState.showSecurityShieldModal) {
        SecurityShieldCenterModal(
          currentRole = uiState.currentFirebaseRole,
          shields = uiState.defenseShields,
          dmsMatrix = uiState.dmsAccessMatrix,
          auditLogs = uiState.securityAuditEvents,
          onSelectRole = { mekanikViewModel.setFirebaseRbacRole(it) },
          onToggleShield = { num, enabled -> mekanikViewModel.toggleDefenseShield(num, enabled) },
          onTriggerTestAuditLog = { desc -> mekanikViewModel.triggerSecurityAuditLog(desc) },
          onDismiss = { mekanikViewModel.closeSecurityShieldModal() }
        )
      }

      // 49. Gemini Chatbot Modal (accessible depuis n'importe quel écran)
      if (uiState.showGeminiChatbotModal) {
        com.example.ui.components.GeminiChatbotDialogModal(
          messages = uiState.geminiChatMessages,
          chatInput = uiState.geminiChatInput,
          isTyping = uiState.isGeminiChatTyping,
          selectedModel = uiState.selectedGeminiModel,
          activeVehicle = uiState.activeVehicle,
          onInputChanged = { mekanikViewModel.onGeminiChatInputChanged(it) },
          onSendMessage = { preset -> mekanikViewModel.sendGeminiChatMessage(preset) },
          onSelectModel = { mekanikViewModel.setGeminiModelOption(it) },
          onClearHistory = { mekanikViewModel.clearGeminiChatHistory() },
          onOpenPartInCatalog = { part -> mekanikViewModel.navigateToCatalogForPart(part) },
          onDismiss = { mekanikViewModel.closeGeminiChatbotModal() }
        )
      }

      // 50. Espace Store & Recherche Simplifiée (Par ville / Dans store / Recherche pièce)
      if (showStoreAndCitySearchModal) {
        SimplifiedStoreAndCitySearchModal(
          initialTab = storeSearchInitialTab,
          onOpenVendorAddPartAi = {
            showStoreAndCitySearchModal = false
            showVendorAddPartPhotoAiModal = true
          },
          onOpenHandsFreeVoiceSearch = {
            showStoreAndCitySearchModal = false
            showHandsFreeVoiceSearchModal = true
          },
          onDismiss = { showStoreAndCitySearchModal = false }
        )
      }

      // 51. Espace Vendeur : Ajouter sa pièce par Photo + Compilation IA (Réf OEM, 4 Photos générées, Voitures & Réfs compatibles)
      if (showVendorAddPartPhotoAiModal) {
        VendorAddPartPhotoAiModal(
          onDismiss = { showVendorAddPartPhotoAiModal = false }
        )
      }

      // 52. Forum Automobile par Groupe Constructeur (VW, Stellantis, Ford, Renault...) + Recherche par voiture
      if (showAutoGroupForumModal) {
        AutoGroupForumModal(
          onOpenProSocialNetwork = {
            showAutoGroupForumModal = false
            showProB2bSocialNetworkModal = true
          },
          onDismiss = { showAutoGroupForumModal = false }
        )
      }

      // 53. Réseau Social Professionnel B2B (Vendeurs & Ateliers : Fil pièces, Commentaires + Chat Privé Écrit & Vocal)
      if (showProB2bSocialNetworkModal) {
        ProB2bSocialNetworkModal(
          onOpenForum = {
            showProB2bSocialNetworkModal = false
            showAutoGroupForumModal = true
          },
          onOpenVendorAddPhotoAi = {
            showProB2bSocialNetworkModal = false
            showVendorAddPartPhotoAiModal = true
          },
          onDismiss = { showProB2bSocialNetworkModal = false }
        )
      }

      // 54. Système d'Archive Mensuelle Automatique en Fichier .ZIP (Ventes, Bons & Factures du mois)
      if (showMonthlySalesZipArchiveModal) {
        MonthlySalesZipArchiveModal(
          onDismiss = { showMonthlySalesZipArchiveModal = false }
        )
      }

      // 55. Recherche Vocale Mains-Libres pour Mécaniciens (Références de Pièces & Schémas Techniques sans utiliser les mains + Synthèse Vocale TTS & Gemini 3.8 Flash)
      if (showHandsFreeVoiceSearchModal) {
        HandsFreeMechanicVoiceSearchModal(
          onApplyQueryToStoreSearch = { refQuery ->
            showHandsFreeVoiceSearchModal = false
            storeSearchInitialTab = StoreSearchTab.BY_PART
            showStoreAndCitySearchModal = true
          },
          onOpenFullOemCatalogs = {
            showHandsFreeVoiceSearchModal = false
            mekanikViewModel.openWebOemCatalogsModal()
          },
          onDismiss = { showHandsFreeVoiceSearchModal = false }
        )
      }

      // Floating Quick-Access Gemini Chatbot Pill (visible across screens)
      if (currentSubScreen == SecondarySubScreen.NONE && uiState.currentNavTab != MainNavigationTab.AI_DIAGNOSTIC) {
        Surface(
          shape = RoundedCornerShape(24.dp),
          color = GraphiteBlack,
          border = androidx.compose.foundation.BorderStroke(1.5.dp, TechCyan),
          shadowElevation = 8.dp,
          onClick = { mekanikViewModel.openGeminiChatbotModal() },
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 16.dp, bottom = 18.dp)
            .testTag("fab_open_gemini_chatbot")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.AutoAwesome,
              contentDescription = "Gemini Chatbot",
              tint = TechCyanGlow,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Gemini AI",
              style = MaterialTheme.typography.labelMedium.copy(
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 11.5.sp
              )
            )
          }
        }
      }

      // 56. Bannière Flottante de Notification en Temps Réel (Live Real-Time Banner) en haut de l'écran
      Box(
        modifier = Modifier
          .align(Alignment.TopCenter)
          .fillMaxWidth()
      ) {
        LiveRealTimeNotificationBanner(
          notification = liveBannerNotification,
          isRealtimeStreamActive = isRealtimeStreamEnabled,
          onOpenNotificationCenter = {
            mekanikViewModel.toggleNotificationModal(true)
          },
          onActionClick = { notif ->
            mekanikViewModel.markNotificationAsRead(notif.id)
            when (notif.actionTag) {
              "OPEN_OBD_AI" -> {
                currentSubScreen = SecondarySubScreen.NONE
                mekanikViewModel.setNavigationTab(MainNavigationTab.AI_DIAGNOSTIC)
                mekanikViewModel.askMekanikAi("P0299")
              }
              "OPEN_STORE_SEARCH" -> {
                storeSearchInitialTab = StoreSearchTab.IN_STORE
                showStoreAndCitySearchModal = true
              }
              "OPEN_ATELIER" -> {
                currentSubScreen = SecondarySubScreen.NONE
                mekanikViewModel.setNavigationTab(MainNavigationTab.ATELIER)
              }
              "OPEN_PRO_SOCIAL" -> {
                showProB2bSocialNetworkModal = true
              }
              else -> {
                mekanikViewModel.toggleNotificationModal(true)
              }
            }
          },
          onDismissBanner = {
            liveBannerNotification = null
          }
        )
      }

      // 57. Superposition Semi-Transparente du Tutoriel de Démarrage Rapide Interactif (Onboarding Première Connexion)
      if (showInteractiveOnboardingOverlay) {
        InteractiveQuickStartOnboardingOverlay(
          currentStepIndex = onboardingStepIndex,
          onStepChange = { onboardingStepIndex = it },
          onExecuteStepAction = { actionKey ->
            when (actionKey) {
              "TOGGLE_THEME" -> {
                MekanikThemeController.toggleNormalOrDarkMode(appContext)
              }
              "OPEN_STORE_SEARCH" -> {
                MekanikOnboardingAndRealtimePrefs.markOnboardingCompleted(appContext)
                showInteractiveOnboardingOverlay = false
                storeSearchInitialTab = StoreSearchTab.IN_STORE
                showStoreAndCitySearchModal = true
              }
              "OPEN_VOICE_SEARCH" -> {
                MekanikOnboardingAndRealtimePrefs.markOnboardingCompleted(appContext)
                showInteractiveOnboardingOverlay = false
                showHandsFreeVoiceSearchModal = true
              }
              "OPEN_OBD_AI" -> {
                MekanikOnboardingAndRealtimePrefs.markOnboardingCompleted(appContext)
                showInteractiveOnboardingOverlay = false
                currentSubScreen = SecondarySubScreen.NONE
                mekanikViewModel.setNavigationTab(MainNavigationTab.AI_DIAGNOSTIC)
                mekanikViewModel.askMekanikAi("P0299")
              }
              "OPEN_VENDOR_PHOTO_AI" -> {
                MekanikOnboardingAndRealtimePrefs.markOnboardingCompleted(appContext)
                showInteractiveOnboardingOverlay = false
                showVendorAddPartPhotoAiModal = true
              }
              "OPEN_PRO_SOCIAL" -> {
                MekanikOnboardingAndRealtimePrefs.markOnboardingCompleted(appContext)
                showInteractiveOnboardingOverlay = false
                showProB2bSocialNetworkModal = true
              }
              "TRIGGER_LIVE_NOTIF" -> {
                MekanikOnboardingAndRealtimePrefs.markOnboardingCompleted(appContext)
                showInteractiveOnboardingOverlay = false
                triggerRealtimeNotification(NotificationCategory.MEKANIK_AI_ALERTE)
              }
            }
          },
          onFinishOnboarding = {
            MekanikOnboardingAndRealtimePrefs.markOnboardingCompleted(appContext)
            showInteractiveOnboardingOverlay = false
          }
        )
      }

      // 58. Composant AccountSelector (Première Connexion & Changement de Formule synchronisé dans Firestore)
      if (showAccountSelectorModal) {
        androidx.compose.ui.window.Dialog(
          onDismissRequest = { showAccountSelectorModal = false },
          properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
          AccountSelector(
            currentRole = uiState.currentAccountRole,
            isFirstLoginModal = AccountSelectorPrefs.shouldShowAccountSelectorOnFirstLogin(appContext),
            onRoleConfirmed = { chosenRole, _, _ ->
              mekanikViewModel.switchAccountRole(chosenRole)
              showAccountSelectorModal = false
            },
            onDismiss = {
              showAccountSelectorModal = false
            }
          )
        }
      }

      // 59. Authentification 2FA Google Authenticator pour l'Administrateur du Compte (Ajouter/Supprimer Sous-Comptes & Anti-Clonage)
      if (showAccountAdmin2FaModal) {
        AccountAdmin2FaSubAccountsModal(
          accountRole = uiState.currentAccountRole,
          onDismiss = { showAccountAdmin2FaModal = false }
        )
      }

      // 60. Plateforme Web SaaS MEKANIK+ Multi-Supports (PC Barre Latérale, Tablette & Mobile - 6 Espaces A à F)
      if (showSaasWebPlatformModal) {
        MekanikSaasWebPlatformModal(
          currentAccountRole = uiState.currentAccountRole,
          onSwitchAccountRole = { role ->
            mekanikViewModel.switchAccountRole(role)
          },
          onOpenSuperAdminConsole = { tabIdx ->
            showSaasWebPlatformModal = false
            mekanikViewModel.openSuperAdminModal(tabIdx)
          },
          onOpenMecaVerifScreen = {
            showSaasWebPlatformModal = false
            currentSubScreen = SecondarySubScreen.NONE
            mekanikViewModel.setNavigationTab(MainNavigationTab.MECA_VERIF)
          },
          onOpenMekanikAiScreen = { dtcCode ->
            showSaasWebPlatformModal = false
            currentSubScreen = SecondarySubScreen.NONE
            mekanikViewModel.setNavigationTab(MainNavigationTab.AI_DIAGNOSTIC)
            if (!dtcCode.isNullOrBlank()) {
              mekanikViewModel.askMekanikAi(dtcCode)
            }
          },
          onOpenPartVisionScanner = {
            showSaasWebPlatformModal = false
            mekanikViewModel.openPartLabelVisionScanner()
          },
          onDismiss = { showSaasWebPlatformModal = false }
        )
      }
    }
  }
  } // end ModalNavigationDrawer
}

private data class NavItemConfig(
  val tab: MainNavigationTab,
  val label: String,
  val icon: ImageVector,
  val isAi: Boolean = false
)

@Composable
private fun MekanikBottomNavigationBar(
  currentTab: MainNavigationTab,
  currentRole: AccountRole,
  onTabSelected: (MainNavigationTab) -> Unit
) {
  val navItems = when (currentRole) {
    AccountRole.GARAGE_PRO -> listOf(
      NavItemConfig(MainNavigationTab.ACCUEIL, "Atelier", Icons.Default.Dashboard),
      NavItemConfig(MainNavigationTab.PIECES, "Catalogue", Icons.Default.Build),
      NavItemConfig(MainNavigationTab.MECA_VERIF, "MécaVerif+", Icons.Default.VerifiedUser),
      NavItemConfig(MainNavigationTab.ATELIER, "DMS Pro", Icons.Default.Handyman),
      NavItemConfig(MainNavigationTab.PROFIL, "Compte", Icons.Default.Person)
    )
    AccountRole.VENDEUR_PIECES -> listOf(
      NavItemConfig(MainNavigationTab.ACCUEIL, "Magasin", Icons.Default.Storefront),
      NavItemConfig(MainNavigationTab.PIECES, "Catalogue", Icons.Default.Inventory2),
      NavItemConfig(MainNavigationTab.RECHERCHE, "Recherche", Icons.Default.Search),
      NavItemConfig(MainNavigationTab.MECA_VERIF, "MécaVerif+", Icons.Default.VerifiedUser),
      NavItemConfig(MainNavigationTab.PROFIL, "Compte", Icons.Default.Person)
    )
    AccountRole.ATELIER_MAGASIN_MIXTE -> listOf(
      NavItemConfig(MainNavigationTab.ACCUEIL, "Mixte 1+4", Icons.Default.PrecisionManufacturing),
      NavItemConfig(MainNavigationTab.PIECES, "Magasin", Icons.Default.Storefront),
      NavItemConfig(MainNavigationTab.ATELIER, "Atelier", Icons.Default.Handyman),
      NavItemConfig(MainNavigationTab.MECA_VERIF, "MécaVerif+", Icons.Default.VerifiedUser),
      NavItemConfig(MainNavigationTab.PROFIL, "Compte", Icons.Default.Person)
    )
    AccountRole.ATELIER_CARROSSERIE -> listOf(
      NavItemConfig(MainNavigationTab.ACCUEIL, "Carrosserie", Icons.Default.CarRepair),
      NavItemConfig(MainNavigationTab.MECA_VERIF, "Tôlerie & Photos", Icons.Default.PhotoCamera),
      NavItemConfig(MainNavigationTab.PIECES, "Pièces VIN", Icons.Default.Build),
      NavItemConfig(MainNavigationTab.ATELIER, "Devis", Icons.Default.ReceiptLong),
      NavItemConfig(MainNavigationTab.PROFIL, "Compte", Icons.Default.Person)
    )
    AccountRole.CONDUCTEUR -> listOf(
      NavItemConfig(MainNavigationTab.ACCUEIL, "Mon Auto", Icons.Default.DirectionsCar),
      NavItemConfig(MainNavigationTab.RECHERCHE, "Recherche", Icons.Default.Search),
      NavItemConfig(MainNavigationTab.MECA_VERIF, "MécaVerif+", Icons.Default.VerifiedUser),
      NavItemConfig(MainNavigationTab.PIECES, "Pièces", Icons.Default.Build),
      NavItemConfig(MainNavigationTab.PROFIL, "Compte", Icons.Default.Person)
    )
    AccountRole.LOCATION_VOITURES -> listOf(
      NavItemConfig(MainNavigationTab.ACCUEIL, "Flotte", Icons.Default.AirportShuttle),
      NavItemConfig(MainNavigationTab.RECHERCHE, "Recherche", Icons.Default.Search),
      NavItemConfig(MainNavigationTab.MECA_VERIF, "MécaVerif+", Icons.Default.VerifiedUser),
      NavItemConfig(MainNavigationTab.ATELIER, "Parc Auto", Icons.Default.BuildCircle),
      NavItemConfig(MainNavigationTab.PROFIL, "Compte", Icons.Default.Person)
    )
  }

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("bottom_nav_bar"),
    color = MetalCard,
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 6.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      navItems.forEach { item ->
        MekanikNavItem(
          title = item.label,
          icon = item.icon,
          isSelected = currentTab == item.tab,
          onClick = { onTabSelected(item.tab) },
          isAi = item.isAi,
          modifier = Modifier.weight(1f),
          testTag = "nav_${item.tab.name.lowercase()}"
        )
      }
    }
  }
}

@Composable
private fun MekanikNavItem(
  title: String,
  icon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  isAi: Boolean = false,
  modifier: Modifier = Modifier,
  testTag: String = "nav_item"
) {
  val itemColor = if (isSelected) MekanikBrandOrange else AluminumGray

  Column(
    modifier = modifier
      .heightIn(min = 52.dp)
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 6.dp, vertical = 4.dp)
      .testTag(testTag),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .height(30.dp)
        .width(48.dp)
        .clip(RoundedCornerShape(15.dp))
        .background(
          if (isSelected) MekanikBrandOrange.copy(alpha = 0.16f) else Color.Transparent
        ),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = itemColor,
        modifier = Modifier.size(20.dp)
      )
    }

    Spacer(modifier = Modifier.height(3.dp))

    Text(
      text = title,
      color = if (isSelected) OffWhite else AluminumGray,
      fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
      fontSize = 11.sp,
      maxLines = 1
    )
  }
}

