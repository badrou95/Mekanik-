package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.example.data.api.vin.VinDecoderEngine
import com.example.data.local.CachedTechnicalSchematicEntity
import com.example.data.local.ClientFaultHistoryEntity
import com.example.data.local.SavedDiagnosticRecordEntity
import com.example.data.local.VehicleRepairLogEntity
import com.example.data.model.*
import com.example.data.repository.AcousticSoundRepository
import com.example.data.repository.AdvancedModulesRepository
import com.example.data.repository.AiDiagnosticService
import com.example.data.repository.LocalCacheStorageSummary
import com.example.data.repository.MekanikErrorLogger
import com.example.data.repository.MekanikRepository
import com.example.data.repository.SecurityShieldFirestoreRepository
import com.example.data.repository.SmartCartAndFreeAccessRepository
import com.example.data.repository.UnifiedAutoApiRepository
import com.example.data.repository.WorkshopNetworkCondition
import com.example.data.repository.WorkshopOfflineCacheRepository
import com.example.ui.components.OfflineCacheHubTab
import com.example.ui.components.SearchTabMode
import com.example.util.GeneratedPdfResult
import com.example.util.PdfGeneratorUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MainNavigationTab(val label: String, val iconTag: String) {
  ACCUEIL("Accueil", "home"),
  RECHERCHE("Recherche", "search"),
  PIECES("Pièces", "parts"),
  MECA_VERIF("MécaVerif+", "verified_car"),
  ATELIER("Atelier", "workshop"),
  PROFIL("Profil", "profile"),
  AI_DIAGNOSTIC("MekanikAI", "ai")
}

data class MekanikUiState(
  val currentNavTab: MainNavigationTab = MainNavigationTab.ACCUEIL,
  val searchTabMode: SearchTabMode = SearchTabMode.VIN,
  val searchQuery: String = "WVWZZZ5NZMW123456",
  val activeVehicle: Vehicle = MekanikRepository.sampleVehicles.first(),
  val searchResults: List<Vehicle> = MekanikRepository.sampleVehicles,
  val recentSearches: List<String> = listOf("WVWZZZ5NZMW123456", "DFGA", "VF3FPHNSSMY001234", "K9K 608"),
  
  // Catalog & Exploded Diagram
  val selectedSystemCategory: SystemCategory = SystemCategory.FREINAGE,
  val activeSchema: ExplodedSchema = MekanikRepository.tiguanBrakeSchema,
  val selectedHotspotNumber: Int? = 1,
  val selectedPart: PartItem? = MekanikRepository.sampleParts.first(),
  val partsForSelectedCategory: List<PartItem> = MekanikRepository.sampleParts.filter { it.system == SystemCategory.FREINAGE },

  // Workshop & Repair Orders (Devis)
  val repairOrders: List<RepairOrder> = MekanikRepository.sampleRepairOrders,
  val activeRepairOrder: RepairOrder? = null,
  val showNewQuoteDialog: Boolean = false,
  val isOrderSigned: Boolean = false,
  val dailyMaintenanceTasks: List<DailyMaintenanceTask> = MekanikRepository.sampleDailyMaintenanceTasks,
  val companionWorkTimeAllocations: List<CompanionWorkTimeAllocation> = MekanikRepository.sampleCompanionWorkTimeAllocations,
  val weeklyProductivityPoints: List<WeeklyProductivityDayPoint> = MekanikRepository.sampleWeeklyProductivityPoints,

  // Appointments Management (Système de Gestion des Rendez-Vous)
  val appointments: List<WorkshopAppointment> = MekanikRepository.sampleAppointments,
  val showBookAppointmentDialog: Boolean = false,

  // Push Notifications Center
  val notifications: List<AppNotification> = MekanikRepository.sampleNotifications,
  val showNotificationModal: Boolean = false,

  // MekanikAI State
  val aiQuery: String = "",
  val isAiLoading: Boolean = false,
  val aiDiagnosticResult: AiDiagnosticResult? = null,

  // Vendors & Stores
  val selectedWilayaFilter: String = "Toutes les Wilayas",
  val vendorsList: List<Vendor> = MekanikRepository.sampleVendors,
  val vendorSearchQuery: String = "",

  // User & Subscriptions
  val dailyVinCount: Int = 3,
  val dailyVinLimit: Int = 15,
  val userPlanName: String = "DMS Mécanicauto+ Atelier Pro (Alger)",
  val showSubscriptionModal: Boolean = false,
  val showVinReportModal: Boolean = false,
  val activeVinReport: VinReport? = null,
  val toastMessage: String? = null,

  // 1. Camera & OCR Scanner
  val scanTargetType: ScanTargetType = ScanTargetType.CARTE_GRISE,
  val isScanningActive: Boolean = false,
  val detectedScanResult: ScanDetectionResult? = null,
  val showCameraScanModal: Boolean = false,
  val showCarteGriseScannerModal: Boolean = false,

  // 2. OBD-II Live Telemetry & Audio Acoustic Diagnostics
  val obdStatus: ObdConnectionStatus = ObdConnectionStatus.CONNECTED,
  val liveTelemetry: ObdLiveTelemetry = ObdLiveTelemetry(),
  val isAudioRecording: Boolean = false,
  val selectedAudioAnalysis: AudioNoiseAnalysis? = MekanikRepository.sampleAudioAnalyses.first(),

  // 3. Live Repair Tracking & Certified Passport
  val liveRepairSession: LiveRepairTrackingSession = MekanikRepository.sampleLiveRepairSession,
  val showPassportModal: Boolean = false,

  // 4. Fleet Management
  val fleetVehicles: List<FleetVehicle> = MekanikRepository.sampleFleetVehicles,
  val fleetStatusFilter: FleetVehicleStatus? = null,

  // 5. SOS Emergency Breakdown & Tow Trucks
  val selectedBreakdownType: BreakdownType = BreakdownType.MOTEUR_SURCHAUFFE,
  val activeSosRequest: SosEmergencyRequest? = null,
  val showSosModal: Boolean = false,

  // 6. Billing, Invoices & CIB/Edahabia
  val invoicesList: List<FiscalInvoice> = MekanikRepository.sampleInvoices,
  val activeInvoice: FiscalInvoice? = MekanikRepository.sampleInvoices.first(),
  val showInvoiceModal: Boolean = false,
  val showPaymentModal: Boolean = false,

  // 7. B2B Marketplace & Group Quote Comparison
  val showGroupQuoteModal: Boolean = false,

  // 8. Dedicated OBD-II Physical Screen Camera Scanner
  val selectedObdPreset: ObdPhysicalScreenPreset = ObdScreenPresetsProvider.presets.first(),
  val isObdCameraFreezeFrame: Boolean = false,
  val isObdTorchActive: Boolean = false,
  val obdZoomLevel: Float = 1.0f,
  val isObdAutoAiTrigger: Boolean = true,
  val isObdAiScanning: Boolean = false,
  val obdScannerAiResult: AiDiagnosticResult? = null,
  val obdDetectedDtc: String = "P0299",
  val obdOcrConfidence: Int = 99,

  // 9. VIN Decoder Module State (NHTSA Public API & ISO-3779)
  val isVinDecoding: Boolean = false,
  val decodedVinSpecs: DecodedVehicleSpecs? = null,
  val vinDecodeError: String? = null,
  val vinSearchHistory: List<DecodedVehicleSpecs> = emptyList(),

  // 10. Multi-Account Roles & Profiles (Atelier Pro / Conducteur / Location de Voiture)
  val currentAccountRole: AccountRole = AccountRole.GARAGE_PRO,

  // --- Espace Conducteur ---
  val selectedDriverGuideCategory: DriverGuideCategory? = null,
  val selectedDriverMethod: DriverMaintenanceMethod? = null,
  val selectedDriverInvoice: DriverInvoiceItem? = null,
  val showDriverMethodModal: Boolean = false,
  val showDriverInvoiceModal: Boolean = false,

  // --- Espace Agence Location de Voitures ---
  val rentalVehicles: List<RentalVehicle> = MekanikRepository.sampleRentalVehicles,
  val rentalContracts: List<RentalContract> = MekanikRepository.sampleRentalContracts,
  val selectedRentalStatusFilter: RentalStatus? = null,
  val selectedRentalCategoryFilter: RentalCategory? = null,
  val selectedRentalVehicle: RentalVehicle? = null,
  val selectedRentalContract: RentalContract? = null,
  val showRentalVehicleModal: Boolean = false,
  val showRentalContractModal: Boolean = false,
  val showNewRentalContractModal: Boolean = false,
  val showAddRentalCarModal: Boolean = false,

  // --- 11. Passeport Véhicule QR Universel (Atelier / Vendeur Pièces / Location / Conducteur) ---
  val universalQrPassports: List<VehiclePassportQrData> = MekanikRepository.sampleVehicleQrPassports,
  val activeScannedQrPassport: VehiclePassportQrData = MekanikRepository.sampleVehicleQrPassports.first(),
  val showUniversalQrScannerModal: Boolean = false,
  val showUniversalQrPassportDetailModal: Boolean = false,
  val showGenerateVehicleQrModal: Boolean = false,
  val selectedQrPassportRoleTab: Int = 0, // 0: Fiche Tech, 1: Atelier & OR, 2: Vendeur / Pièces, 3: Location

  // --- 12. Génération & Téléchargement PDF Devis et Factures ---
  val generatedPdfResult: GeneratedPdfResult? = null,
  val showPdfDownloadModal: Boolean = false,

  // --- 13. Diagnostic Sonore Acoustique Moteur IA (Enregistrement Audio) ---
  val isRecordingEngineSound: Boolean = false,
  val engineSoundRecordingDurationSec: Int = 0,
  val recordedDecibelLevel: Int = 74,
  val recordedPeakHz: Int = 850,
  val selectedSoundPresetId: String = "SOUND_ROD_KNOCK",
  val acousticDiagnosisResult: AcousticDiagnosisResult? = null,
  val showEngineSoundRecorderModal: Boolean = false,
  val engineSoundLiveAmplitudes: List<Float> = listOf(0.3f, 0.4f, 0.6f, 0.8f, 0.5f, 0.7f, 0.9f, 0.4f, 0.65f, 0.55f),
  val soundRecordingHistory: List<SoundRecordingHistoryItem> = listOf(
    SoundRecordingHistoryItem(
      id = "REC-001",
      title = "Enregistrement Moteur 2.0 TDI (Ralenti à froid)",
      date = "18 Août 2026",
      durationSeconds = 8,
      vehicleModel = "Volkswagen Golf 7 (2.0 TDI CR)",
      plateNumber = "04291-120-16",
      diagnosedProblem = "Claquement Coussinets de Bielle",
      severity = SoundSeverity.CRITIQUE,
      confidenceScorePercent = 94,
      isSharedWithWorkshop = true
    ),
    SoundRecordingHistoryItem(
      id = "REC-002",
      title = "Enregistrement Accélération 2500 RPM",
      date = "15 Août 2026",
      durationSeconds = 6,
      vehicleModel = "Renault Clio 4 (1.5 dCi 90)",
      plateNumber = "12984-114-16",
      diagnosedProblem = "Sifflement Aigu Turbo & Durite",
      severity = SoundSeverity.ELEVE,
      confidenceScorePercent = 91,
      isSharedWithWorkshop = false
    )
  ),

  // --- 14. Workshop Free Client Accounts & Portal (Comptes Clients Ateliers & QR) ---
  val workshopClientAccounts: List<WorkshopClientAccount> = MekanikRepository.sampleWorkshopClientAccounts,
  val activeClientPortalAccount: WorkshopClientAccount? = MekanikRepository.sampleWorkshopClientAccounts.firstOrNull(),
  val showWorkshopClientPortalModal: Boolean = false,
  val showCreateClientAccountModal: Boolean = false,
  val showVoiceDictationModal: Boolean = false,
  val voiceDictationTargetField: String = "repair_order_notes",
  val prefilledClientName: String = "",
  val prefilledClientPhone: String = "",
  val prefilledPlateNumber: String = "",
  val prefilledVehicleModel: String = "",
  val prefilledVin: String = "",
  val prefilledMileage: String = "",
  val prefilledNotes: String = "",

  // --- 15. Compte Entreprise Ultra Pro & Gestion Hiérarchique Multi-Comptes ---
  val ultraProOrganization: UltraProOrganizationSummary = MekanikRepository.sampleUltraProOrganization,
  val ultraProSubAccounts: List<UltraProSubAccount> = MekanikRepository.sampleUltraProSubAccounts,
  val activeImpersonatedSubAccount: UltraProSubAccount? = null, // Null = Gérant Principal
  val subAccountAuditLogs: List<SubAccountAuditLog> = MekanikRepository.sampleSubAccountAuditLogs,
  val showUltraProManagerModal: Boolean = false,
  val showCreateSubAccountModal: Boolean = false,
  val selectedSubAccountForEdit: UltraProSubAccount? = null,
  val partsRequisitions: List<WorkshopPartsRequisition> = MekanikRepository.samplePartsRequisitions,
  val showCreatePartsRequisitionModal: Boolean = false,
  val showPurchaseExtensionModal: Boolean = false,

  // --- 16. Intrasystème Logiciels Constructeurs OEM & Supplément Pack Intégral 10k ---
  val workshopSoftwareConfig: WorkshopSoftwareConfiguration = WorkshopSoftwareConfiguration(),
  val fullSoftwareSupplement: FullSoftwarePackageSupplement = MekanikRepository.sampleFullSoftwareSupplement,
  val showOemSoftwareLauncherModal: Boolean = false,
  val showPurchaseFullSoftwareSupplementModal: Boolean = false,
  val selectedOemToolForLaunch: OemSoftwareTool? = MekanikRepository.sampleOemSoftwareTools.firstOrNull(),
  val selectedOemToolGroupFilter: OemManufacturerGroup? = null,

  // --- 17. Architecture API Gateway, Sandbox Dev (Zéro Frais) & Recherche par Plaque Immatriculation ---
  val apiDataSourceMode: ApiDataSourceMode = ApiDataSourceMode.MEKANIK_ONE_UNIFIED,
  val apiTelemetryMetrics: ApiTelemetryMetrics = ApiTelemetryMetrics(),
  val apiEndpointsList: List<ApiServiceEndpoint> = MekanikRepository.sampleApiEndpoints,
  val showDevApiManagerModal: Boolean = false,
  val selectedLicensePlateMatch: LicensePlateMatch? = null,
  val licensePlateQuery: String = "12345-121-16",
  val isSearchingPlate: Boolean = false,

  // --- 18. Multi-Appareils Atelier (Max 3), Sécurité 2FA Google Authenticator & Compte Unifié Magasin (+5k) ---
  val workshop2FAConfig: Workshop2FASecurityConfig = MekanikRepository.sampleWorkshop2FAConfig,
  val showWorkshop2FAModal: Boolean = false,
  val workshopStoreLinkConfig: WorkshopStoreLinkConfig = MekanikRepository.sampleWorkshopStoreLinkConfig,
  val showWorkshopStoreLinkModal: Boolean = false,

  // --- 19. Nouveaux Modules : Inspection 360°, Alertes WhatsApp/SMS, Scanner Stock & Barème M.O ---
  val inspections: List<VehicleInspection> = AdvancedModulesRepository.sampleInspections,
  val showInspectionModal: Boolean = false,
  val showMessageModal: Boolean = false,
  val prefilledMessageClient: ClientMessageDispatch? = null,
  val barcodeInventory: List<BarcodeInventoryItem> = AdvancedModulesRepository.sampleBarcodeInventory,
  val stockMovementLogs: List<StockMovementLog> = AdvancedModulesRepository.sampleStockMovementLogs,
  val supplierPurchaseOrders: List<SupplierPurchaseOrder> = AdvancedModulesRepository.sampleSupplierPurchaseOrders,
  val scannedPurchaseInvoices: List<ScannedPurchaseInvoice> = AdvancedModulesRepository.sampleScannedPurchaseInvoices,
  val printedThermalLabels: List<PrintedThermalLabel> = AdvancedModulesRepository.samplePrintedThermalLabels,
  val showBarcodeScannerModal: Boolean = false,
  val showLaborStandardModal: Boolean = false,
  val workshopHourlyRateDzd: Int = 2500,

  // --- 20. Suite des 7 Nouveaux Modules Pro (Ponts, RMA, Portail Devis, Hors-Ligne, B2B, TCO, Constat) ---
  val workshopBays: List<WorkshopBayItem> = AdvancedModulesRepository.sampleWorkshopBays,
  val waitingQueueVehicles: List<WaitingQueueVehicle> = AdvancedModulesRepository.sampleWaitingVehicles,
  val showBayPlannerModal: Boolean = false,

  val partWarranties: List<PartWarrantyRecord> = AdvancedModulesRepository.samplePartWarranties,
  val showWarrantyRmaModal: Boolean = false,

  val remoteClientQuoteSession: RemoteClientQuoteSession = AdvancedModulesRepository.sampleRemoteClientQuote,
  val showRemoteQuotePortalModal: Boolean = false,

  val isOfflineModeEnabled: Boolean = false,
  val pendingSyncActions: List<PendingSyncAction> = AdvancedModulesRepository.samplePendingSyncActions,
  val offlineCachedPacks: List<OfflineCachedPack> = AdvancedModulesRepository.sampleOfflineCachedPacks,
  val showOfflineSyncModal: Boolean = false,

  val activeB2bOrders: List<ActiveB2bOrderTracking> = AdvancedModulesRepository.sampleActiveB2bOrders,
  val showB2bComparatorModal: Boolean = false,

  val vehicleTcoSummaries: List<VehicleTcoSummary> = AdvancedModulesRepository.sampleVehicleTcoSummaries,
  val vehicleExpenses: List<VehicleExpenseEntry> = AdvancedModulesRepository.sampleVehicleExpenses,
  val showTcoCalculatorModal: Boolean = false,

  val accidentReports: List<DigitalAccidentReport> = AdvancedModulesRepository.sampleAccidentReports,
  val showAccidentReportModal: Boolean = false,

  // --- 21. Suite des 6 Nouveaux Modules Next-Gen (Schémas/Pinout, ADAS/EV, TSB, Occasion/Consignes, Clim A/C, Location Contrats) ---
  val fuseBoxItems: List<FuseBoxItem> = AdvancedModulesRepository.sampleFuseBoxItems,
  val showWiringFuseEcuModal: Boolean = false,

  val adasCalibrations: List<AdasCalibrationRecord> = AdvancedModulesRepository.sampleAdasCalibrations,
  val evBatteryReports: List<EvBatteryHealthReport> = AdvancedModulesRepository.sampleEvBatteryReports,
  val showAdasEvBatteryModal: Boolean = false,

  val showTechnicalBulletinsModal: Boolean = false,

  val certifiedUsedParts: List<CertifiedUsedPartOffer> = AdvancedModulesRepository.sampleCertifiedUsedParts,
  val coreReturnConsignes: List<CoreReturnConsigneItem> = AdvancedModulesRepository.sampleCoreReturnConsignes,
  val showCertifiedUsedPartsModal: Boolean = false,

  val acServiceLogs: List<AcServiceChargeRecord> = AdvancedModulesRepository.sampleAcServiceLogs,
  val showAcRefrigerantModal: Boolean = false,

  val shortTermRentalContracts: List<ShortTermRentalContract> = AdvancedModulesRepository.sampleShortTermRentalContracts,
  val showShortTermRentalModal: Boolean = false,

  // --- 22. Catalogues Web OEM Gratuits (CatCar.info, 7Zap, PartsSouq, NemigaParts, IFInterface, WebAutoCats) & Modules Demandés ---
  val showWebOemCatalogsModal: Boolean = false,

  val fitmentVerificationRules: List<FitmentVerificationRule> = AdvancedModulesRepository.sampleFitmentVerificationRules,
  val showPartCompatibilityModal: Boolean = false,

  val repairOrderProfitability: List<RepairOrderProfitabilityItem> = AdvancedModulesRepository.sampleRepairOrderProfitability,
  val bulkOilDrums: List<BulkOilDrumTank> = AdvancedModulesRepository.sampleBulkOilDrums,
  val showWorkshopProfitabilityModal: Boolean = false,

  val preCtInspectionPoints: List<PreCtInspectionPoint> = AdvancedModulesRepository.samplePreCtInspectionPoints,
  val courtesyLoanerVehicles: List<CourtesyLoanerVehicle> = AdvancedModulesRepository.sampleCourtesyLoanerVehicles,
  val showPreCtCourtesyModal: Boolean = false,

  // --- 23. Compte Super-Administrateur, Équipe Employés, Essai Démo 3 Jours & Clés d'Activation ---
  val superAdminSession: SuperAdminAccountSession = SuperAdminAccountSession(),
  val adminStaffMembers: List<AdminStaffMember> = AdvancedModulesRepository.sampleAdminStaffMembers,
  val registrationRequests: List<UserRegistrationRequest> = AdvancedModulesRepository.sampleRegistrationRequests,
  val planSubscriptionRequests: List<PlanSubscriptionRequest> = AdvancedModulesRepository.samplePlanSubscriptionRequests,
  val adminAuditLogs: List<AdminAuditLogEntry> = AdvancedModulesRepository.sampleAdminAuditLogs,
  val showSuperAdminModal: Boolean = false,

  val demoTrialStatus: UserDemoTrialStatus = UserDemoTrialStatus(),
  val showDemoTrialActivationModal: Boolean = false,

  // --- 24. Suite DMS « WinMotor Next & Peugeot Service Box V14 » ---
  val winMotorRatesConfig: WinMotorWorkshopRatesConfig = WinMotorWorkshopRatesConfig(),
  val winMotorOperations: List<WinMotorFlatRateOperation> = AdvancedModulesRepository.sampleWinMotorOperations,
  val serviceBoxBaskets: List<ServiceBoxBasketImport> = AdvancedModulesRepository.sampleServiceBoxBaskets,
  val companionClockEntries: List<CompanionTimeClockEntry> = AdvancedModulesRepository.sampleCompanionClockEntries,
  val multiPayerSplitOrders: List<MultiPayerSplitOrder> = AdvancedModulesRepository.sampleMultiPayerSplitOrders,
  val backorderVorItems: List<BackorderVorItem> = AdvancedModulesRepository.sampleBackorderVorItems,
  val showWinMotorServiceBoxModal: Boolean = false,

  // --- 25. MekanikOne Unified Auto-API (SinoAuto360 + Dongchedi Auto-API + Car2DB LLM + lifeofcapo 142 Marques + ChineseCar.parts) ---
  val fallbackChainConfigs: List<FallbackSourceConfig> = UnifiedAutoApiRepository.currentFallbackChainConfigs,
  val activeUnifiedApiTrace: UnifiedSingleApiExecutionTrace = UnifiedAutoApiRepository.executeUnifiedApiQuery(
    queryInput = "LVVDB21B9PD048912",
    scenarioId = "demo-jetour-dashing-brakes"
  ),
  val showUnifiedAutoApiHubModal: Boolean = false,
  val unifiedHubInitialTabIndex: Int = 0,

  // --- 26. Suite des 6 Options Indispensables (OCR→Cascade, Pont Chine↔Europe IAM, Auto-Apprentissage Room 0ms, OR 1-Clic DZD, Fluides DCT, Passeport QR) ---
  val autoLearnedCacheEntries: List<AutoLearnedVinCacheEntry> = UnifiedAutoApiRepository.autoLearnedCacheEntries,
  val selectedQuoteTierIndex: Int = 1,
  val oneClickAutoQuoteBundle: OneClickAutoQuoteBundle = UnifiedAutoApiRepository.buildOneClickQuoteForTrace(
    trace = UnifiedAutoApiRepository.executeUnifiedApiQuery(
      queryInput = "LVVDB21B9PD048912",
      scenarioId = "demo-jetour-dashing-brakes"
    ),
    tierIndex = 1
  ),
  val qrPassportCascadeSyncRecord: QrPassportCascadeSyncRecord = UnifiedAutoApiRepository.buildQrPassportSyncRecord(
    trace = UnifiedAutoApiRepository.executeUnifiedApiQuery(
      queryInput = "LVVDB21B9PD048912",
      scenarioId = "demo-jetour-dashing-brakes"
    ),
    fluidSpec = UnifiedAutoApiRepository.fluidAndServiceResetSpecs.first(),
    quoteBundle = UnifiedAutoApiRepository.buildOneClickQuoteForTrace(
      trace = UnifiedAutoApiRepository.executeUnifiedApiQuery(
        queryInput = "LVVDB21B9PD048912",
        scenarioId = "demo-jetour-dashing-brakes"
      ),
      tierIndex = 1
    )
  ),

  // --- 27. Les 6 Boucliers de Défense, Rôles RBAC Firebase Auth & Stock Temps Réel Espace Vendeur ---
  val currentFirebaseRole: FirebaseRbacRole = FirebaseRbacRole.SUPER_ADMIN,
  val defenseShields: List<DefenseShieldItem> = SecurityShieldFirestoreRepository.defaultSixShields,
  val securityAuditEvents: List<FirestoreSecurityAuditEvent> = SecurityShieldFirestoreRepository.defaultAuditEvents,
  val showSecurityShieldModal: Boolean = false,
  val vendorStockItems: List<FirestoreVendorStockItem> = SecurityShieldFirestoreRepository.defaultRealtimeVendorInventory,

  // --- 28. Filtrage Dynamique Temps Réel (Véhicules & Pièces) + Gemini Chatbot ---
  val vehicleDynamicFilter: VehicleDynamicFilterState = VehicleDynamicFilterState(),
  val partDynamicFilter: PartDynamicFilterState = PartDynamicFilterState(),
  val selectedGeminiModel: GeminiModelOption = GeminiModelOption.FLASH_3_5,
  val geminiChatInput: String = "",
  val isGeminiChatTyping: Boolean = false,
  val showGeminiChatbotModal: Boolean = false,
  val geminiChatMessages: List<GeminiChatMessage> = listOf(
    GeminiChatMessage(
      id = "msg_welcome_gemini",
      isUser = false,
      text = "👋 Bonjour ! Je suis **MekanikAI Chatbot**, propulsé par **Google Gemini 3.5 Flash**.\n\nJe connais le véhicule actuellement sélectionné (**Volkswagen Tiguan II • 2.0 TDI DFGA**), vos références OEM (ETKA / Partslink24), les codes défauts OBD-II et le stock vendeur en temps réel en Algérie.\n\nPosez-moi une question technique (ex: *« Couple de serrage kit distribution »*, *« Défaut P0299 turbo »*, ou *« Référence plaquettes avant »*) !",
      timestamp = "En ligne • Gemini AI",
      modelUsed = GeminiModelOption.FLASH_3_5.modelId,
      vehicleBadge = "Volkswagen Tiguan • DFGA",
      suggestedParts = emptyList(),
      quickFollowUps = listOf(
        "Diagnostiquer le code défaut P0299",
        "Couple de serrage distribution & pompe à eau",
        "Quelles plaquettes de frein pour mon véhicule ?",
        "Plan d'entretien & capacité huile 5W-30"
      ),
      isLiveCloudApi = false
    )
  )
) {
  // Static & catalog reference collections (kept in class body to keep data class constructor/copy under 255 JVM argument limit)
  val obdHistory: List<String> = listOf("P0299", "P0401", "P0300", "P2002")
  val audioAnalysesList: List<AudioNoiseAnalysis> = MekanikRepository.sampleAudioAnalyses
  val certifiedPassport: CertifiedServicePassport = MekanikRepository.sampleCertifiedPassport
  val fleetSummary: FleetOverviewSummary = MekanikRepository.sampleFleetSummary
  val towTrucksList: List<TowTruckService> = MekanikRepository.sampleTowTrucks
  val marketplaceOffers: List<VendorPartOffer> = MekanikRepository.sampleMarketplaceOffers
  val driverVehicle: DriverVehicleSummary = MekanikRepository.sampleDriverVehicleSummary
  val driverRepairHistory: List<DriverRepairRecord> = MekanikRepository.sampleDriverRepairRecords
  val driverInvoices: List<DriverInvoiceItem> = MekanikRepository.sampleDriverInvoices
  val driverMaintenanceMethods: List<DriverMaintenanceMethod> = MekanikRepository.sampleDriverMaintenanceMethods
  val rentalFleetSummary: RentalFleetSummary = MekanikRepository.sampleRentalFleetSummary
  val selectedRequisitionForDetails: WorkshopPartsRequisition? = null
  val oemSoftwareTools: List<OemSoftwareTool> = MekanikRepository.sampleOemSoftwareTools
  val licensePlateMatches: List<LicensePlateMatch> = MekanikRepository.sampleLicensePlateMatches
  val messagingTemplates: List<MessageNotificationTemplate> = AdvancedModulesRepository.sampleNotificationTemplates
  val laborStandardOperations: List<LaborStandardOperation> = AdvancedModulesRepository.sampleLaborOperations
  val supplierComparisonOffers: List<SupplierComparisonOffer> = AdvancedModulesRepository.sampleSupplierComparisons
  val ecuPinouts: List<EcuPinoutSignal> = AdvancedModulesRepository.sampleEcuPinouts
  val technicalBulletins: List<TechnicalServiceBulletin> = AdvancedModulesRepository.sampleTechnicalBulletins
  val acVehicleSpecs: List<AcVehicleSpecification> = AdvancedModulesRepository.sampleAcVehicleSpecs
  val freeWebOemPlatforms: List<FreeWebOemCatalogPlatform> = AdvancedModulesRepository.sampleFreeWebOemPlatforms
  val oemExplodedDiagrams: List<OemExplodedDiagramSheet> = AdvancedModulesRepository.sampleOemExplodedDiagrams
  val unifiedApiConnectors: List<UnifiedApiConnectorProvider> = UnifiedApiConnectorProvider.entries
  val sinoChineseVinRecords: List<SinoChineseVinRecord> = UnifiedAutoApiRepository.sinoChineseVinRecords
  val reverseEngineeredScenarios: List<ReverseEngineeredDemoScenario> = UnifiedAutoApiRepository.reverseEngineeredDemoScenarios
  val lifeOfCapoBrands: List<LifeOfCapoBrandCatalogEntry> = UnifiedAutoApiRepository.lifeOfCapoEmbeddedBrands
  val ocrChassisCascadePresets: List<OcrChassisCascadePreset> = UnifiedAutoApiRepository.ocrChassisCascadePresets
  val sinoEuroCrossRefBridges: List<SinoEuroCrossReferenceBridge> = UnifiedAutoApiRepository.sinoEuroCrossRefBridges
  val fluidAndServiceResetSpecs: List<FluidAndServiceResetSpec> = UnifiedAutoApiRepository.fluidAndServiceResetSpecs
  val dmsAccessMatrix: List<DmsProtectedOperationCheck> = SecurityShieldFirestoreRepository.dmsProtectedOperations

  val unreadNotificationsCount: Int get() = notifications.count { !it.isRead }
  val pendingAdminApprovalsCount: Int
    get() = registrationRequests.count {
      it.status == AdminApprovalStatus.EN_ATTENTE ||
        it.status == AdminApprovalStatus.DEMO_3_JOURS ||
        it.status == AdminApprovalStatus.CLE_REQUISE
    } + planSubscriptionRequests.count { it.status == AdminApprovalStatus.EN_ATTENTE }
}

class MekanikViewModel(
  private val aiDiagnosticService: AiDiagnosticService = AiDiagnosticService()
) : ViewModel() {

  private val _uiState = MutableStateFlow(MekanikUiState())
  val uiState: StateFlow<MekanikUiState> = _uiState.asStateFlow()

  private val _superAdminInitialTab = MutableStateFlow(0)
  val superAdminInitialTab: StateFlow<Int> = _superAdminInitialTab.asStateFlow()

  val errorLoggerState: StateFlow<ErrorLoggerUiState> = MekanikErrorLogger.uiState

  private val _isClientBugAiRepairModalOpen = MutableStateFlow(false)
  val isClientBugAiRepairModalOpen: StateFlow<Boolean> = _isClientBugAiRepairModalOpen.asStateFlow()

  private val _isSmartCartFreeAccessMapModalOpen = MutableStateFlow(false)
  val isSmartCartFreeAccessMapModalOpen: StateFlow<Boolean> = _isSmartCartFreeAccessMapModalOpen.asStateFlow()

  private val _smartCartModalInitialTab = MutableStateFlow(0)
  val smartCartModalInitialTab: StateFlow<Int> = _smartCartModalInitialTab.asStateFlow()

  val smartCartItems: StateFlow<List<SmartExplodedCartItem>> = SmartCartAndFreeAccessRepository.smartCartItems
  val particulierAccountState: StateFlow<ParticulierAccountState> = SmartCartAndFreeAccessRepository.particulierAccountState

  // --- Stratégie de Mise en Cache Locale Robuste (Room SQLite) : Schémas, Diagnostics & Historique Pannes Clients ---
  val workshopNetworkCondition: StateFlow<WorkshopNetworkCondition> = WorkshopOfflineCacheRepository.networkCondition
  val cachedSchematics: StateFlow<List<CachedTechnicalSchematicEntity>> = WorkshopOfflineCacheRepository.cachedSchematics
  val savedDiagnostics: StateFlow<List<SavedDiagnosticRecordEntity>> = WorkshopOfflineCacheRepository.savedDiagnostics
  val clientFaultHistory: StateFlow<List<ClientFaultHistoryEntity>> = WorkshopOfflineCacheRepository.clientFaultHistory
  val vehicleRepairLogs: StateFlow<List<VehicleRepairLogEntity>> = WorkshopOfflineCacheRepository.vehicleRepairLogs
  val localCacheSummary: StateFlow<LocalCacheStorageSummary> = WorkshopOfflineCacheRepository.cacheSummary

  private val _isOfflineCacheHubModalOpen = MutableStateFlow(false)
  val isOfflineCacheHubModalOpen: StateFlow<Boolean> = _isOfflineCacheHubModalOpen.asStateFlow()

  private val _offlineCacheHubInitialTab = MutableStateFlow(OfflineCacheHubTab.CLIENT_FAULTS_HISTORY)
  val offlineCacheHubInitialTab: StateFlow<OfflineCacheHubTab> = _offlineCacheHubInitialTab.asStateFlow()

  init {
    updateCategory(SystemCategory.FREINAGE)
  }

  fun setNavigationTab(tab: MainNavigationTab) {
    _uiState.update { it.copy(currentNavTab = tab) }
  }

  fun setSearchTabMode(mode: SearchTabMode) {
    _uiState.update { it.copy(searchTabMode = mode) }
  }

  fun onSearchQueryChanged(query: String) {
    _uiState.update { state ->
      val newFilter = state.vehicleDynamicFilter.copy(textQuery = query)
      val filteredVehicles = applyVehicleDynamicFiltersInternal(newFilter)
      state.copy(
        searchQuery = query,
        vehicleDynamicFilter = newFilter,
        searchResults = filteredVehicles
      )
    }
  }

  fun performVehicleSearch() {
    val query = _uiState.value.searchQuery.trim()
    val mode = _uiState.value.searchTabMode

    when (mode) {
      SearchTabMode.VIN -> {
        if (query.length == 17 || query.isNotEmpty()) {
          decodeVinOnline(query)
        } else {
          val found = MekanikRepository.findVehicleByVin(query)
          if (found != null) {
            _uiState.update {
              it.copy(
                activeVehicle = found,
                searchResults = listOf(found),
                dailyVinCount = (it.dailyVinCount + 1).coerceAtMost(it.dailyVinLimit),
                currentNavTab = MainNavigationTab.RECHERCHE
              )
            }
          }
        }
      }
      SearchTabMode.PLAQUE -> {
        _uiState.update {
          it.copy(
            searchTabMode = SearchTabMode.VIN,
            toastMessage = "🔒 Recherche par immatriculation désactivée (en attente de certificat / autorisation officielle). Veuillez utiliser le code VIN."
          )
        }
      }
      SearchTabMode.CODE_MOTEUR -> {
        val results = MekanikRepository.findVehicleByEngineCode(query)
        if (results.isNotEmpty()) {
          _uiState.update {
            it.copy(
              activeVehicle = results.first(),
              searchResults = results,
              currentNavTab = MainNavigationTab.RECHERCHE
            )
          }
        }
      }
      SearchTabMode.MANUELLE -> {
        val results = MekanikRepository.sampleVehicles.filter {
          it.brand.contains(query, ignoreCase = true) || it.model.contains(query, ignoreCase = true)
        }.ifEmpty { MekanikRepository.sampleVehicles }
        _uiState.update {
          it.copy(
            activeVehicle = results.first(),
            searchResults = results,
            currentNavTab = MainNavigationTab.RECHERCHE
          )
        }
      }
    }
  }

  // --- Public Automotive VIN Decoder Online Method ---
  fun decodeVinOnline(rawVin: String) {
    val cleanVin = rawVin.trim().uppercase()
    if (cleanVin.isEmpty()) {
      _uiState.update { it.copy(vinDecodeError = "Veuillez saisir un numéro VIN") }
      return
    }

    _uiState.update {
      it.copy(
        isVinDecoding = true,
        vinDecodeError = null,
        searchQuery = cleanVin
      )
    }

    viewModelScope.launch {
      try {
        val unifiedTrace = UnifiedAutoApiRepository.executeUnifiedApiQuery(cleanVin)
        val decoded = VinDecoderEngine.decodeVin(cleanVin)
        val vehicleEntity = VinDecoderEngine.toVehicleEntity(decoded)

        _uiState.update { current ->
          val updatedHistory = (listOf(decoded) + current.vinSearchHistory.filterNot { it.vin == decoded.vin }).take(10)
          val updatedRecent = (listOf(decoded.vin) + current.recentSearches.filterNot { it == decoded.vin }).take(8)
          val currentResults = current.searchResults.filterNot { it.vin == vehicleEntity.vin }
          val cascade = unifiedTrace.fallbackCascade
          val toastMsg = if (cascade.fallbackHopsCount > 0) {
            "🔄 Fallback Auto (${cascade.fallbackHopsCount} saut(s)) → Résolu par #${cascade.winningStepOrder} ${cascade.winningProvider.displayName} : ${decoded.make} ${decoded.model}"
          } else {
            "✅ VIN Décodé (#1 ${cascade.winningProvider.displayName}) : ${decoded.make} ${decoded.model} (${decoded.modelYear})"
          }

          current.copy(
            isVinDecoding = false,
            decodedVinSpecs = decoded,
            activeVehicle = vehicleEntity,
            activeUnifiedApiTrace = unifiedTrace,
            searchResults = listOf(vehicleEntity) + currentResults,
            vinSearchHistory = updatedHistory,
            recentSearches = updatedRecent,
            dailyVinCount = (current.dailyVinCount + 1).coerceAtMost(current.dailyVinLimit),
            currentNavTab = MainNavigationTab.RECHERCHE,
            toastMessage = toastMsg
          )
        }
      } catch (e: Exception) {
        _uiState.update {
          it.copy(
            isVinDecoding = false,
            vinDecodeError = "Erreur lors du décodage : ${e.localizedMessage ?: "Problème réseau"}"
          )
        }
      }
    }
  }

  fun loadPresetVin(presetVin: String) {
    _uiState.update {
      it.copy(
        searchQuery = presetVin,
        searchTabMode = SearchTabMode.VIN
      )
    }
    decodeVinOnline(presetVin)
  }

  fun applyDecodedVehicleAsActive(decoded: DecodedVehicleSpecs) {
    val vehicle = VinDecoderEngine.toVehicleEntity(decoded)
    _uiState.update {
      it.copy(
        activeVehicle = vehicle,
        searchQuery = vehicle.vin,
        currentNavTab = MainNavigationTab.PIECES,
        toastMessage = "Véhicule actif sélectionné : ${vehicle.brand} ${vehicle.model}"
      )
    }
    updateCategory(SystemCategory.FREINAGE)
  }

  fun clearDecodedVin() {
    _uiState.update {
      it.copy(
        decodedVinSpecs = null,
        vinDecodeError = null
      )
    }
  }

  fun selectVehicle(vehicle: Vehicle) {
    _uiState.update {
      it.copy(
        activeVehicle = vehicle,
        searchQuery = vehicle.vin,
        currentNavTab = MainNavigationTab.PIECES
      )
    }
    updateCategory(SystemCategory.FREINAGE)
  }

  fun updateCategory(category: SystemCategory) {
    val schema = MekanikRepository.getSchemaForCategory(category)
    _uiState.update { state ->
      val filtered = applyPartDynamicFiltersInternal(
        category = category,
        filter = state.partDynamicFilter,
        liveStock = state.vendorStockItems
      )
      state.copy(
        selectedSystemCategory = category,
        activeSchema = schema,
        partsForSelectedCategory = filtered,
        selectedPart = filtered.firstOrNull() ?: MekanikRepository.sampleParts.firstOrNull { it.system == category },
        selectedHotspotNumber = filtered.firstOrNull()?.schemaCalloutNumber ?: 1
      )
    }
  }

  fun selectHotspot(hotspot: ExplodedHotspot) {
    val currentCategory = _uiState.value.selectedSystemCategory
    val matchingPart = MekanikRepository.sampleParts.find {
      it.id == hotspot.partId || (it.system == currentCategory && it.schemaCalloutNumber == hotspot.calloutNumber)
    } ?: MekanikRepository.sampleParts.find { it.schemaCalloutNumber == hotspot.calloutNumber }
    _uiState.update {
      it.copy(
        selectedHotspotNumber = hotspot.calloutNumber,
        selectedPart = matchingPart ?: it.selectedPart
      )
    }
  }

  fun selectPartItem(part: PartItem) {
    _uiState.update {
      it.copy(
        selectedPart = part,
        selectedHotspotNumber = part.schemaCalloutNumber
      )
    }
  }

  // --- MekanikAI Methods ---
  fun onAiQueryChanged(query: String) {
    _uiState.update { it.copy(aiQuery = query) }
  }

  fun askMekanikAi(predefinedQuery: String? = null) {
    val query = predefinedQuery ?: _uiState.value.aiQuery
    if (query.isBlank()) return

    _uiState.update {
      it.copy(
        isAiLoading = true,
        aiQuery = query
      )
    }

    viewModelScope.launch {
      val result = aiDiagnosticService.diagnoseProblem(query, _uiState.value.activeVehicle)
      _uiState.update {
        it.copy(
          isAiLoading = false,
          aiDiagnosticResult = result
        )
      }
    }
  }

  fun navigateToCatalogForPart(part: com.example.data.model.DiagnosticRecommendedPart) {
    updateCategory(part.systemCategory)
    val matchingPart = MekanikRepository.sampleParts.find {
      it.oemReference.equals(part.oemReference, ignoreCase = true) ||
      it.name.contains(part.name.take(6), ignoreCase = true) ||
      it.system == part.systemCategory
    }
    if (matchingPart != null) {
      selectPartItem(matchingPart)
    }
    _uiState.update {
      it.copy(
        currentNavTab = MainNavigationTab.PIECES,
        toastMessage = "Catalogue : ${part.name} [${part.oemReference}]"
      )
    }
  }

  fun openQuoteForDiagnosticResult(result: com.example.data.model.AiDiagnosticResult, specificPart: com.example.data.model.DiagnosticRecommendedPart? = null) {
    val vehicle = _uiState.value.activeVehicle
    val partName = specificPart?.name ?: result.recommendedParts.firstOrNull() ?: "Kit Réparation OBD ${result.faultCode ?: ""}"
    val partRef = specificPart?.oemReference ?: "OEM-DIAG-${result.faultCode ?: "OBD"}"
    val partPrice = (specificPart?.priceEstimatedDzd ?: 8500).toDouble()

    val quoteItems = listOf(
      com.example.data.model.InvoiceLineItem(
        reference = partRef,
        description = partName,
        quantity = 1,
        unitPriceDzd = partPrice,
        isLabor = false
      ),
      com.example.data.model.InvoiceLineItem(
        reference = "MO-DIAG-OBD",
        description = "Main d'œuvre : Remplacement pièce & effacement défaut ${result.faultCode ?: "valise"}",
        quantity = (result.estimatedLaborHours ?: 1.5f).toInt().coerceAtLeast(1),
        unitPriceDzd = 2500.0,
        isLabor = true
      )
    )

    _uiState.update {
      it.copy(
        currentNavTab = MainNavigationTab.ATELIER,
        showNewQuoteDialog = true,
        toastMessage = "Devis atelier pré-configuré pour l'anomalie ${result.faultCode ?: "signalée"}"
      )
    }
  }

  // --- Appointments System Actions ---
  fun bookAppointment(
    clientName: String,
    clientPhone: String,
    vehicleModel: String,
    plateNumber: String,
    serviceType: ServiceType,
    date: String,
    timeSlot: String,
    notes: String
  ) {
    val newApt = WorkshopAppointment(
      id = "apt_${System.currentTimeMillis()}",
      clientName = clientName.ifBlank { "Client Particulier" },
      clientPhone = clientPhone.ifBlank { "0550 00 00 00" },
      vehicleModel = vehicleModel.ifBlank { "${_uiState.value.activeVehicle.brand} ${_uiState.value.activeVehicle.model}" },
      plateNumber = plateNumber.ifBlank { "01234-121-16" },
      serviceType = serviceType,
      appointmentDate = date,
      timeSlot = timeSlot,
      workshopName = "Garage Auto Performance Alger",
      status = AppointmentStatus.EN_ATTENTE,
      estimatedDurationHours = serviceType.durationHours,
      estimatedCostDzd = serviceType.defaultPriceDzd,
      assignedMechanic = "Équipe Atelier",
      clientNotes = notes,
      createdAt = "Aujourd'hui"
    )

    val confirmationNotification = AppNotification(
      id = "notif_${System.currentTimeMillis()}",
      title = "📅 Demande de Rendez-vous Enregistrée",
      message = "Votre demande pour '${serviceType.label}' le $date ($timeSlot) a été envoyée à l'atelier. Vous recevrez une confirmation sous peu.",
      category = NotificationCategory.RDV_ATELIER,
      timestamp = "À l'instant",
      isRead = false,
      priority = NotificationPriority.HIGH
    )

    _uiState.update {
      it.copy(
        appointments = listOf(newApt) + it.appointments,
        notifications = listOf(confirmationNotification) + it.notifications,
        showBookAppointmentDialog = false,
        toastMessage = "Rendez-vous réservé avec succès pour le $date à $timeSlot !"
      )
    }
  }

  fun updateAppointmentStatus(appointmentId: String, newStatus: AppointmentStatus) {
    _uiState.update { state ->
      val updated = state.appointments.map { apt ->
        if (apt.id == appointmentId) {
          apt.copy(status = newStatus)
        } else apt
      }

      val aptItem = state.appointments.find { it.id == appointmentId }
      val statusNotif = if (newStatus == AppointmentStatus.CONFIRME && aptItem != null) {
        AppNotification(
          id = "notif_${System.currentTimeMillis()}",
          title = "✅ Rendez-vous Atelier Confirmé !",
          message = "Le Garage Auto Performance a confirmé votre prestation '${aptItem.serviceType.label}' pour le ${aptItem.appointmentDate} à ${aptItem.timeSlot}.",
          category = NotificationCategory.RDV_ATELIER,
          timestamp = "À l'instant",
          isRead = false,
          priority = NotificationPriority.HIGH
        )
      } else null

      state.copy(
        appointments = updated,
        notifications = if (statusNotif != null) listOf(statusNotif) + state.notifications else state.notifications,
        toastMessage = "Statut du rendez-vous mis à jour : ${newStatus.label}"
      )
    }
  }

  fun convertAppointmentToRepairOrder(apt: WorkshopAppointment) {
    val newOrder = RepairOrder(
      id = "or_${System.currentTimeMillis()}",
      orderNumber = "OR-2026-${(1000..9999).random()}",
      clientName = apt.clientName,
      clientPhone = apt.clientPhone,
      vehicleModel = apt.vehicleModel,
      vin = _uiState.value.activeVehicle.vin,
      plateNumber = apt.plateNumber,
      mileageKm = 88000,
      date = apt.appointmentDate,
      status = RepairStatus.EN_COURS,
      items = listOf(
        RepairItem(apt.serviceType.label, "PRESTATION-ATELIER", 1, apt.estimatedCostDzd)
      ),
      laborHours = apt.estimatedDurationHours,
      laborRatePerHourDzd = 2000,
      notes = "Généré automatiquement depuis le rendez-vous en ligne. Notes client : ${apt.clientNotes}"
    )

    updateAppointmentStatus(apt.id, AppointmentStatus.EN_COURS)

    _uiState.update {
      it.copy(
        repairOrders = listOf(newOrder) + it.repairOrders,
        toastMessage = "Rendez-vous converti en Ordre de Réparation ${newOrder.orderNumber} !"
      )
    }
  }

  // --- Push Notifications Center Actions ---
  fun markNotificationAsRead(notificationId: String) {
    _uiState.update { state ->
      val updated = state.notifications.map {
        if (it.id == notificationId) it.copy(isRead = true) else it
      }
      state.copy(notifications = updated)
    }
  }

  fun markAllNotificationsAsRead() {
    _uiState.update { state ->
      val updated = state.notifications.map { it.copy(isRead = true) }
      state.copy(notifications = updated, toastMessage = "Toutes les notifications sont marquées comme lues.")
    }
  }

  fun sendSimulatedNotification(title: String, message: String, category: NotificationCategory) {
    val newNotif = AppNotification(
      id = "notif_${System.currentTimeMillis()}",
      title = title,
      message = message,
      category = category,
      timestamp = "À l'instant",
      isRead = false,
      priority = NotificationPriority.HIGH
    )
    _uiState.update {
      it.copy(
        notifications = listOf(newNotif) + it.notifications,
        toastMessage = "Alerte push envoyée : $title"
      )
    }
  }

  // --- Workshop & Repair Order Actions ---
  fun createRepairOrder(
    clientName: String,
    clientPhone: String,
    plate: String,
    laborHours: Float,
    items: List<RepairItem>
  ) {
    val vehicle = _uiState.value.activeVehicle
    val newOrder = RepairOrder(
      id = "or_${System.currentTimeMillis()}",
      orderNumber = "OR-2026-${(1000..9999).random()}",
      clientName = clientName.ifBlank { "Client Particulier" },
      clientPhone = clientPhone.ifBlank { "0550 00 00 00" },
      vehicleModel = "${vehicle.brand} ${vehicle.model} (${vehicle.exactYear})",
      vin = vehicle.vin,
      plateNumber = plate.ifBlank { "01234-121-16" },
      mileageKm = 85000,
      date = "17 Août 2026",
      status = RepairStatus.EN_COURS,
      items = items.ifEmpty {
        listOf(
          RepairItem("Plaquettes de frein avant Brembo", "5Q0 698 151 BR", 1, 8500),
          RepairItem("Disques avant 312mm High Carbon", "1K0 615 301 AA", 1, 18500)
        )
      },
      laborHours = laborHours,
      laborRatePerHourDzd = 2000,
      notes = "Contrôle technique et essai routier effectués."
    )

    _uiState.update {
      it.copy(
        repairOrders = listOf(newOrder) + it.repairOrders,
        showNewQuoteDialog = false,
        toastMessage = "Devis ${newOrder.orderNumber} créé avec succès !"
      )
    }
  }

  fun signRepairOrder(orderId: String, signatureName: String) {
    _uiState.update { state ->
      val updated = state.repairOrders.map { order ->
        if (order.id == orderId) {
          order.copy(isSigned = true, signatureName = signatureName)
        } else order
      }
      state.copy(
        repairOrders = updated,
        toastMessage = "Ordre de réparation validé et signé numériquement."
      )
    }
  }

  fun updateRepairOrder(updatedOrder: RepairOrder) {
    _uiState.update { state ->
      val updated = state.repairOrders.map { order ->
        if (order.id == updatedOrder.id) updatedOrder else order
      }
      state.copy(
        repairOrders = updated,
        toastMessage = "✅ Checklist OR & Ampoules mise à jour sur ${updatedOrder.orderNumber}"
      )
    }
  }

  fun updateRepairOrderStatus(orderId: String, newStatus: RepairStatus) {
    _uiState.update { state ->
      val updated = state.repairOrders.map { order ->
        if (order.id == orderId) order.copy(status = newStatus) else order
      }
      state.copy(
        repairOrders = updated,
        toastMessage = "🛠️ Statut OR mis à jour : ${newStatus.label}"
      )
    }
  }

  fun updateMaintenanceTaskStatus(taskId: String, newStatus: MaintenanceTaskStatus) {
    _uiState.update { state ->
      val updated = state.dailyMaintenanceTasks.map { task ->
        if (task.id == taskId) task.copy(status = newStatus) else task
      }
      state.copy(
        dailyMaintenanceTasks = updated,
        toastMessage = "📋 Statut tâche maintenance : ${newStatus.label}"
      )
    }
  }

  fun assignMaintenanceTaskMechanic(taskId: String, mechanicName: String, bayNumber: Int) {
    _uiState.update { state ->
      val updated = state.dailyMaintenanceTasks.map { task ->
        if (task.id == taskId) task.copy(assignedMechanic = mechanicName, bayNumber = bayNumber) else task
      }
      state.copy(
        dailyMaintenanceTasks = updated,
        toastMessage = "🔧 Tâche affectée à $mechanicName sur le Pont N°$bayNumber"
      )
    }
  }

  fun addNewMaintenanceTask(task: DailyMaintenanceTask) {
    _uiState.update { state ->
      state.copy(
        dailyMaintenanceTasks = listOf(task) + state.dailyMaintenanceTasks,
        toastMessage = "✅ Tâche de maintenance ajoutée au planning du jour !"
      )
    }
  }

  fun convertMaintenanceTaskToRepairOrder(task: DailyMaintenanceTask) {
    val newOrderNum = "OR-2026-0${(100..999).random()}"
    val newOrder = RepairOrder(
      id = "or_${System.currentTimeMillis()}",
      orderNumber = newOrderNum,
      clientName = task.clientName,
      clientPhone = task.clientPhone,
      vehicleModel = task.vehicleModel,
      vin = "VF3${task.licensePlate.replace("-", "")}2026",
      plateNumber = task.licensePlate,
      mileageKm = 85000,
      date = "Aujourd'hui, ${task.scheduledTime}",
      status = RepairStatus.EN_COURS,
      items = listOf(
        RepairItem(
          name = task.requiredPartsSummary,
          reference = "OEM-${task.category.take(4).uppercase()}-2026",
          quantity = 1,
          priceDzd = 14500
        )
      ),
      laborHours = (task.estimatedMinutes / 60f).coerceAtLeast(1.0f),
      laborRatePerHourDzd = 2000,
      discountDzd = 0,
      notes = "Généré depuis Tâche Maintenance : ${task.taskTitle}. ${task.notes}"
    )

    _uiState.update { state ->
      val updatedTasks = state.dailyMaintenanceTasks.map {
        if (it.id == task.id) it.copy(status = MaintenanceTaskStatus.EN_COURS) else it
      }
      state.copy(
        repairOrders = listOf(newOrder) + state.repairOrders,
        dailyMaintenanceTasks = updatedTasks,
        toastMessage = "📑 Ordre de Réparation $newOrderNum créé avec succès !"
      )
    }
  }

  fun adjustCompanionWorkHours(
    companionId: String,
    productiveDeltaHours: Float = 0f,
    diagnosticDeltaHours: Float = 0f,
    waitingDeltaHours: Float = 0f,
    soldBaremeDeltaHours: Float = 0f
  ) {
    _uiState.update { state ->
      val updatedAllocations = state.companionWorkTimeAllocations.map { item ->
        if (item.id == companionId) {
          item.copy(
            productiveRepairHours = (item.productiveRepairHours + productiveDeltaHours).coerceAtLeast(1f),
            diagnosticAndTestHours = (item.diagnosticAndTestHours + diagnosticDeltaHours).coerceAtLeast(0.5f),
            waitingPartsHours = (item.waitingPartsHours + waitingDeltaHours).coerceAtLeast(0f),
            soldBaremeHours = (item.soldBaremeHours + soldBaremeDeltaHours).coerceAtLeast(2f)
          )
        } else item
      }
      val totalSold = updatedAllocations.sumOf { it.soldBaremeHours.toDouble() }.toFloat()
      val totalClocked = updatedAllocations.sumOf { it.totalClockedHours.toDouble() }.toFloat().coerceAtLeast(1f)
      val newAvgPct = ((totalSold / totalClocked) * 100f).toInt()

      val updatedWeekly = state.weeklyProductivityPoints.mapIndexed { index, pt ->
        if (index == 5) { // Jeudi (current active peak day)
          pt.copy(
            soldBaremeHours = (pt.soldBaremeHours + soldBaremeDeltaHours).coerceAtLeast(5f),
            clockedRealHours = (pt.clockedRealHours + productiveDeltaHours + diagnosticDeltaHours + waitingDeltaHours).coerceAtLeast(5f),
            productivityPct = newAvgPct
          )
        } else pt
      }

      state.copy(
        companionWorkTimeAllocations = updatedAllocations,
        weeklyProductivityPoints = updatedWeekly,
        toastMessage = "📊 Graphiques Recharts/D3 mis à jour (Productivité : $newAvgPct%)"
      )
    }
  }

  // --- Vendors Filter ---
  fun onWilayaSelected(wilaya: String) {
    val filtered = if (wilaya == "Toutes les Wilayas") {
      MekanikRepository.sampleVendors
    } else {
      MekanikRepository.sampleVendors.filter { it.wilaya.contains(wilaya.take(2)) }
    }
    _uiState.update {
      it.copy(
        selectedWilayaFilter = wilaya,
        vendorsList = filtered.ifEmpty { MekanikRepository.sampleVendors }
      )
    }
  }

  fun generateVinReport(vin: String) {
    val vehicle = MekanikRepository.findVehicleByVin(vin) ?: _uiState.value.activeVehicle
    val report = VinReport(
      vin = vehicle.vin,
      brandModel = "${vehicle.brand} ${vehicle.model} ${vehicle.displacement}",
      engineNumber = "${vehicle.engineCode}*194821",
      gearboxCode = vehicle.gearbox,
      firstRegistrationDate = "14/03/${vehicle.exactYear}",
      technicalControlStatus = "Conforme (Validé jusqu'en 03/2027)",
      mileageHistory = listOf(
        MileageRecord("12/03/2022", 15400, "Concessionnaire Agréé"),
        MileageRecord("20/07/2024", 52300, "Contrôle Technique Wilaya 16"),
        MileageRecord("10/08/2026", 78400, "Atelier Partenaire Mekanik+")
      ),
      recallCampaigns = listOf(
        "Rappel Constructeur 23Z4 (Mise à jour logicielle calculateur moteur - Effectué)",
        "Contrôle étanchéité raccord haute pression (Conforme)"
      ),
      reportToken = "MEK-REP-${(10000..99999).random()}-DZ"
    )
    _uiState.update {
      it.copy(
        activeVinReport = report,
        showVinReportModal = true
      )
    }
  }

  fun dismissModals() {
    _uiState.update {
      it.copy(
        showSubscriptionModal = false,
        showVinReportModal = false,
        showNewQuoteDialog = false,
        showBookAppointmentDialog = false,
        showNotificationModal = false,
        toastMessage = null
      )
    }
  }

  fun toggleSubscriptionModal(show: Boolean) {
    _uiState.update { it.copy(showSubscriptionModal = show) }
  }

  fun toggleNewQuoteDialog(show: Boolean) {
    _uiState.update { it.copy(showNewQuoteDialog = show) }
  }

  fun openQuoteGeneratorWithPart(part: PartItem) {
    _uiState.update {
      it.copy(
        selectedPart = part,
        showNewQuoteDialog = true,
        currentNavTab = MainNavigationTab.ATELIER
      )
    }
  }

  fun toggleBookAppointmentDialog(show: Boolean) {
    _uiState.update { it.copy(showBookAppointmentDialog = show) }
  }

  fun toggleNotificationModal(show: Boolean) {
    _uiState.update { it.copy(showNotificationModal = show) }
  }

  fun showToastNotification(msg: String) {
    _uiState.update { it.copy(toastMessage = msg) }
  }

  // --- 1. Camera & OCR Scanner ---
  fun openCameraScanner(targetType: ScanTargetType = ScanTargetType.CARTE_GRISE) {
    _uiState.update {
      it.copy(
        showCameraScanModal = true,
        scanTargetType = targetType,
        isScanningActive = true,
        detectedScanResult = null
      )
    }
    simulateOcrScan(targetType)
  }

  fun closeCameraScanner() {
    _uiState.update { it.copy(showCameraScanModal = false, isScanningActive = false) }
  }

  fun openCarteGriseScanner() {
    _uiState.update { it.copy(showCarteGriseScannerModal = true) }
  }

  fun closeCarteGriseScanner() {
    _uiState.update { it.copy(showCarteGriseScannerModal = false) }
  }

  fun applyScannedCarteGriseVin(vin: String) {
    _uiState.update {
      it.copy(
        searchQuery = vin,
        searchTabMode = SearchTabMode.VIN,
        showCarteGriseScannerModal = false,
        toastMessage = "VIN $vin extrait de la Carte Grise avec succès !"
      )
    }
    performVehicleSearch()
  }

  fun setScanTargetType(type: ScanTargetType) {
    _uiState.update { it.copy(scanTargetType = type, isScanningActive = true, detectedScanResult = null) }
    simulateOcrScan(type)
  }

  fun simulateOcrScan(targetType: ScanTargetType) {
    viewModelScope.launch {
      kotlinx.coroutines.delay(1200)
      val result = when (targetType) {
        ScanTargetType.CARTE_GRISE -> ScanDetectionResult(
          detectedText = "CHASSIS / VIN : WVWZZZ5NZMW123456\nIMMATRICULATION : 08745 121 16\nMARQUE : VOLKSWAGEN TIGUAN 2.0 TDI",
          targetType = targetType,
          confidencePercent = 98,
          extractedVin = "WVWZZZ5NZMW123456",
          extractedPlate = "08745-121-16",
          vehiclePreview = "VW Tiguan II Facelift (2021) 2.0 TDI 150ch"
        )
        ScanTargetType.VIN_BARCODE -> ScanDetectionResult(
          detectedText = "WVWZZZ5NZMW123456",
          targetType = targetType,
          confidencePercent = 100,
          extractedVin = "WVWZZZ5NZMW123456",
          vehiclePreview = "VW Tiguan II Facelift (2021) 2.0 TDI"
        )
        ScanTargetType.NUM_IMMATRICULATION -> ScanDetectionResult(
          detectedText = "08745 121 16",
          targetType = targetType,
          confidencePercent = 95,
          extractedPlate = "08745-121-16",
          extractedVin = "WVWZZZ5NZMW123456",
          vehiclePreview = "Immatriculation Alger (16) - Véhicule Enregistré"
        )
        ScanTargetType.PIECE_OEM -> ScanDetectionResult(
          detectedText = "5Q0 615 301 F / 5Q0 698 151 BR - BREMBO 312x25",
          targetType = targetType,
          confidencePercent = 96,
          extractedOem = "5Q0 615 301 F",
          vehiclePreview = "Jeu de disques avant ventilés Ø312mm (Compatibilité VW/Audi/Seat/Skoda)"
        )
        ScanTargetType.ECRAN_VALISE_OBD -> ScanDetectionResult(
          detectedText = "DTC: P0299 - Turbocharger Underboost Condition\nECU: 01 - Engine Control Module",
          targetType = targetType,
          confidencePercent = 99,
          extractedVin = "WVWZZZ5NZMW123456",
          vehiclePreview = "Défaut détecté sur écran scanner : P0299"
        )
      }
      _uiState.update { it.copy(isScanningActive = false, detectedScanResult = result) }
    }
  }

  fun applyScannedVin() {
    val result = _uiState.value.detectedScanResult
    if (result?.extractedVin != null) {
      _uiState.update {
        it.copy(
          searchQuery = result.extractedVin,
          showCameraScanModal = false
        )
      }
      performVehicleSearch()
    } else if (result?.extractedOem != null) {
      _uiState.update {
        it.copy(
          searchQuery = result.extractedOem,
          showCameraScanModal = false,
          currentNavTab = MainNavigationTab.PIECES
        )
      }
    } else {
      _uiState.update { it.copy(showCameraScanModal = false) }
    }
  }

  // --- 2. OBD-II Live Telemetry & Audio Acoustic Diagnostics ---
  fun toggleObdConnection() {
    val current = _uiState.value.obdStatus
    if (current == ObdConnectionStatus.CONNECTED) {
      _uiState.update { it.copy(obdStatus = ObdConnectionStatus.DISCONNECTED) }
    } else {
      viewModelScope.launch {
        _uiState.update { it.copy(obdStatus = ObdConnectionStatus.CONNECTING) }
        kotlinx.coroutines.delay(1000)
        _uiState.update {
          it.copy(
            obdStatus = ObdConnectionStatus.CONNECTED,
            toastMessage = "Connecté à l'interface OBD-II Bluetooth (Protocole ISO 15765-4 CAN 500k)."
          )
        }
      }
    }
  }

  fun clearDtcCodes() {
    viewModelScope.launch {
      kotlinx.coroutines.delay(800)
      _uiState.update {
        it.copy(
          liveTelemetry = it.liveTelemetry.copy(activeDtcCodes = emptyList()),
          toastMessage = "Codes défauts DTC effacés avec succès. Calculateur moteur réinitialisé."
        )
      }
    }
  }

  fun recordAudioSampleAndDiagnose() {
    viewModelScope.launch {
      _uiState.update { it.copy(isAudioRecording = true) }
      kotlinx.coroutines.delay(2200)
      val analysis = MekanikRepository.sampleAudioAnalyses.first()
      _uiState.update {
        it.copy(
          isAudioRecording = false,
          selectedAudioAnalysis = analysis,
          toastMessage = "Analyse acoustique terminée : ${analysis.probableSource} (${analysis.confidenceRate}%)"
        )
      }
    }
  }

  fun selectAudioAnalysis(analysis: AudioNoiseAnalysis) {
    _uiState.update { it.copy(selectedAudioAnalysis = analysis) }
  }

  // --- 3. Live Repair Tracking & Digital Passport ---
  fun updateLiveRepairStep(stepNumber: Int) {
    val session = _uiState.value.liveRepairSession
    val updatedSteps = session.steps.map { step ->
      if (step.stepNumber < stepNumber) {
        step.copy(status = RepairStepStatus.COMPLETED)
      } else if (step.stepNumber == stepNumber) {
        step.copy(status = RepairStepStatus.IN_PROGRESS)
      } else {
        step.copy(status = RepairStepStatus.PENDING)
      }
    }
    val currentStage = updatedSteps.find { it.stepNumber == stepNumber }?.title ?: session.currentStageName
    val progress = (stepNumber * 100) / session.steps.size

    _uiState.update {
      it.copy(
        liveRepairSession = session.copy(
          steps = updatedSteps,
          currentStageName = currentStage,
          progressPercent = progress
        ),
        toastMessage = "Étape atelier mise à jour : $currentStage"
      )
    }
  }

  fun togglePassportModal(show: Boolean) {
    _uiState.update { it.copy(showPassportModal = show) }
  }

  // --- 4. Fleet Management ---
  fun setFleetFilter(status: FleetVehicleStatus?) {
    _uiState.update { it.copy(fleetStatusFilter = status) }
  }

  // --- 5. SOS Emergency Breakdown & Tow Trucks ---
  fun triggerSosEmergency(breakdownType: BreakdownType, location: String = "Autoroute de l'Est A1, km 42 (Alger)") {
    val req = SosEmergencyRequest(
      requestId = "SOS-${(1000..9999).random()}",
      breakdownType = breakdownType,
      gpsLocationString = location,
      userVehicle = "${_uiState.value.activeVehicle.brand} ${_uiState.value.activeVehicle.model}",
      userPhone = "+213 550 12 34 56",
      selectedTowTruck = MekanikRepository.sampleTowTrucks.first(),
      status = "Dépanneuse en route - Arrivée estimée 15 min"
    )
    _uiState.update {
      it.copy(
        activeSosRequest = req,
        showSosModal = true,
        toastMessage = "🚨 Signal SOS transmis ! Dépanneuse la plus proche en route."
      )
    }
  }

  fun toggleSosModal(show: Boolean) {
    _uiState.update { it.copy(showSosModal = show) }
  }

  fun selectTowTruck(truck: TowTruckService) {
    val req = _uiState.value.activeSosRequest?.copy(
      selectedTowTruck = truck,
      status = "Dépanneuse assignée : ${truck.companyName} (${truck.phone})"
    )
    _uiState.update {
      it.copy(
        activeSosRequest = req,
        toastMessage = "Dépanneuse sélectionnée : ${truck.companyName} (${truck.estimatedArrivalMinutes} min)"
      )
    }
  }

  // --- 6. Billing & Invoices & CIB ---
  fun selectInvoice(invoice: FiscalInvoice) {
    _uiState.update { it.copy(activeInvoice = invoice, showInvoiceModal = true) }
  }

  fun toggleInvoiceModal(show: Boolean) {
    _uiState.update { it.copy(showInvoiceModal = show) }
  }

  fun togglePaymentModal(show: Boolean) {
    _uiState.update { it.copy(showPaymentModal = show) }
  }

  fun processPayment(method: PaymentMethod) {
    val currentInvoice = _uiState.value.activeInvoice ?: return
    val updated = currentInvoice.copy(isPaid = true, paymentMethod = method)
    val updatedList = _uiState.value.invoicesList.map {
      if (it.invoiceNumber == currentInvoice.invoiceNumber) updated else it
    }
    _uiState.update {
      it.copy(
        activeInvoice = updated,
        invoicesList = updatedList,
        showPaymentModal = false,
        toastMessage = "Règlement de ${updated.totalTtcDzd.toInt()} DZD validé par ${method.label}."
      )
    }
  }

  // --- 7. B2B Marketplace & Group Quotes ---
  fun toggleGroupQuoteModal(show: Boolean) {
    _uiState.update { it.copy(showGroupQuoteModal = show) }
  }

  fun submitGroupQuoteRequest(selectedParts: List<String>, wilaya: String = "Alger (16)") {
    viewModelScope.launch {
      kotlinx.coroutines.delay(1000)
      _uiState.update {
        it.copy(
          showGroupQuoteModal = false,
          toastMessage = "Appel d'offres envoyé à 14 magasins et grossistes certifiés (${wilaya})."
        )
      }
    }
  }

  // --- 8. Dedicated OBD-II Camera Scanner Methods ---
  fun selectObdPreset(preset: ObdPhysicalScreenPreset) {
    _uiState.update {
      it.copy(
        selectedObdPreset = preset,
        obdDetectedDtc = preset.primaryDtc,
        obdOcrConfidence = (96..99).random(),
        isObdCameraFreezeFrame = false,
        obdScannerAiResult = null
      )
    }
    if (_uiState.value.isObdAutoAiTrigger) {
      triggerGeminiAiAnalysisForObd(preset.primaryDtc, preset)
    }
  }

  fun toggleObdTorch() {
    _uiState.update { it.copy(isObdTorchActive = !it.isObdTorchActive) }
  }

  fun setObdZoomLevel(zoom: Float) {
    _uiState.update { it.copy(obdZoomLevel = zoom) }
  }

  fun toggleObdFreezeFrame() {
    val newFreeze = !_uiState.value.isObdCameraFreezeFrame
    _uiState.update {
      it.copy(
        isObdCameraFreezeFrame = newFreeze,
        toastMessage = if (newFreeze) "📸 Freeze-Frame capturé ! Code ${_uiState.value.obdDetectedDtc} verrouillé." else "Reprise du flux caméra OCR direct."
      )
    }
    if (newFreeze && _uiState.value.obdScannerAiResult == null) {
      triggerGeminiAiAnalysisForObd()
    }
  }

  fun toggleObdAutoAiTrigger(enabled: Boolean) {
    _uiState.update { it.copy(isObdAutoAiTrigger = enabled) }
  }

  fun triggerGeminiAiAnalysisForObd(
    customCode: String? = null,
    preset: ObdPhysicalScreenPreset? = null
  ) {
    val targetPreset = preset ?: _uiState.value.selectedObdPreset
    val code = (customCode ?: targetPreset.primaryDtc).trim().uppercase()
    val vehicle = _uiState.value.activeVehicle

    _uiState.update {
      it.copy(
        isObdAiScanning = true,
        obdDetectedDtc = code
      )
    }

    viewModelScope.launch {
      val result = aiDiagnosticService.diagnoseProblem(code, vehicle)
      _uiState.update {
        it.copy(
          isObdAiScanning = false,
          obdScannerAiResult = result,
          toastMessage = "✨ Analyse Gemini AI générée pour le code défaut $code !"
        )
      }
    }
  }

  fun resetObdScanner() {
    _uiState.update {
      it.copy(
        isObdCameraFreezeFrame = false,
        obdScannerAiResult = null,
        isObdAiScanning = false
      )
    }
  }

  // ==========================================
  // --- 10. GESTION DES RÔLES & COMPTES MULTI-UTILISATEURS ---
  // ==========================================

  fun setAccountRole(role: AccountRole) {
    _uiState.update {
      it.copy(
        currentAccountRole = role,
        toastMessage = "Basculé en mode : ${role.label}"
      )
    }
  }

  // --- Espace Conducteur ---
  fun filterDriverGuideCategory(category: DriverGuideCategory?) {
    _uiState.update { it.copy(selectedDriverGuideCategory = category) }
  }

  fun openDriverMethodDetails(method: DriverMaintenanceMethod) {
    _uiState.update {
      it.copy(
        selectedDriverMethod = method,
        showDriverMethodModal = true
      )
    }
  }

  fun closeDriverMethodModal() {
    _uiState.update { it.copy(showDriverMethodModal = false) }
  }

  fun openDriverInvoiceDetails(invoice: DriverInvoiceItem) {
    _uiState.update {
      it.copy(
        selectedDriverInvoice = invoice,
        showDriverInvoiceModal = true
      )
    }
  }

  fun closeDriverInvoiceModal() {
    _uiState.update { it.copy(showDriverInvoiceModal = false) }
  }

  fun downloadDriverInvoicePdf(invoiceNumber: String) {
    _uiState.update {
      it.copy(toastMessage = "📄 Facture $invoiceNumber téléchargée au format PDF certifié.")
    }
  }

  // --- Espace Agence Location de Voitures ---
  fun filterRentalStatus(status: RentalStatus?) {
    _uiState.update { it.copy(selectedRentalStatusFilter = status) }
  }

  fun filterRentalCategory(category: RentalCategory?) {
    _uiState.update { it.copy(selectedRentalCategoryFilter = category) }
  }

  fun openRentalVehicleDetails(vehicle: RentalVehicle) {
    _uiState.update {
      it.copy(
        selectedRentalVehicle = vehicle,
        showRentalVehicleModal = true
      )
    }
  }

  fun closeRentalVehicleModal() {
    _uiState.update { it.copy(showRentalVehicleModal = false) }
  }

  fun openRentalContractDetails(contract: RentalContract) {
    _uiState.update {
      it.copy(
        selectedRentalContract = contract,
        showRentalContractModal = true
      )
    }
  }

  fun closeRentalContractModal() {
    _uiState.update { it.copy(showRentalContractModal = false) }
  }

  fun toggleNewRentalContractModal(show: Boolean) {
    _uiState.update { it.copy(showNewRentalContractModal = show) }
  }

  fun toggleAddRentalCarModal(show: Boolean) {
    _uiState.update { it.copy(showAddRentalCarModal = show) }
  }

  fun createRentalContract(
    vehicleId: String,
    clientName: String,
    clientPhone: String,
    clientNid: String,
    clientPermis: String,
    durationDays: Int,
    depositDzd: Long
  ) {
    val vehicle = _uiState.value.rentalVehicles.find { it.id == vehicleId } ?: return
    val totalRent = vehicle.dailyRateDzd * durationDays
    val newContract = RentalContract(
      contractId = "CTR-2026-${(1000..9999).random()}",
      vehicleId = vehicle.id,
      vehicleName = vehicle.brandModel,
      plateNumber = vehicle.plateNumber,
      clientFullName = clientName,
      clientPhone = clientPhone,
      clientNationalId = clientNid,
      clientDrivingLicense = clientPermis,
      startDate = "18 Août 2026",
      endDate = "28 Août 2026",
      durationDays = durationDays,
      initialKm = vehicle.currentMileageKm,
      returnKm = null,
      depositAmountDzd = depositDzd,
      dailyRateDzd = vehicle.dailyRateDzd,
      totalRentDzd = totalRent,
      status = ContractStatus.EN_COURS,
      notes = "Contrat créé avec succès. Caution CIB enregistrée."
    )

    val updatedVehicles = _uiState.value.rentalVehicles.map {
      if (it.id == vehicleId) {
        it.copy(
          rentalStatus = RentalStatus.EN_LOCATION,
          currentRenterName = clientName,
          currentContractId = newContract.contractId
        )
      } else it
    }

    _uiState.update {
      it.copy(
        rentalContracts = listOf(newContract) + it.rentalContracts,
        rentalVehicles = updatedVehicles,
        showNewRentalContractModal = false,
        toastMessage = "✅ Nouveau contrat ${newContract.contractId} pour $clientName validé !"
      )
    }
  }

  fun returnRentalCar(contractId: String, returnKm: Int) {
    val contract = _uiState.value.rentalContracts.find { it.contractId == contractId } ?: return
    val updatedContracts = _uiState.value.rentalContracts.map {
      if (it.contractId == contractId) {
        it.copy(status = ContractStatus.CLOTURE, returnKm = returnKm, isDepositReturned = true)
      } else it
    }

    val updatedVehicles = _uiState.value.rentalVehicles.map {
      if (it.id == contract.vehicleId) {
        it.copy(
          rentalStatus = RentalStatus.NETTOYAGE_INSPECTION,
          currentRenterName = null,
          currentContractId = null,
          currentMileageKm = returnKm
        )
      } else it
    }

    _uiState.update {
      it.copy(
        rentalContracts = updatedContracts,
        rentalVehicles = updatedVehicles,
        showRentalContractModal = false,
        toastMessage = "🏁 Véhicule restitué à $returnKm km. Caution débloquée."
      )
    }
  }

  fun sendRentalVehicleToWorkshop(vehicleId: String, reason: String) {
    val updatedVehicles = _uiState.value.rentalVehicles.map {
      if (it.id == vehicleId) {
        it.copy(
          rentalStatus = RentalStatus.EN_REVISION_ATELIER,
          currentRenterName = null
        )
      } else it
    }
    _uiState.update {
      it.copy(
        rentalVehicles = updatedVehicles,
        showRentalVehicleModal = false,
        toastMessage = "🛠️ Véhicule envoyé en révision atelier : $reason"
      )
    }
  }

  // --------------------------------------------------------------------------
  // UNIVERSAL VEHICLE QR CODE PASSPORT & MULTI-ROLE SCANNER ACTIONS
  // --------------------------------------------------------------------------
  fun toggleUniversalQrScanner(show: Boolean) {
    _uiState.update { it.copy(showUniversalQrScannerModal = show) }
  }

  fun toggleUniversalQrPassportDetail(show: Boolean) {
    _uiState.update { it.copy(showUniversalQrPassportDetailModal = show) }
  }

  fun toggleGenerateVehicleQrModal(show: Boolean) {
    _uiState.update { it.copy(showGenerateVehicleQrModal = show) }
  }

  fun setQrPassportRoleTab(tabIndex: Int) {
    _uiState.update { it.copy(selectedQrPassportRoleTab = tabIndex) }
  }

  fun selectQrPassport(passport: VehiclePassportQrData, initialTab: Int? = null) {
    _uiState.update {
      it.copy(
        activeScannedQrPassport = passport,
        selectedQrPassportRoleTab = initialTab ?: it.selectedQrPassportRoleTab,
        showUniversalQrPassportDetailModal = true,
        showUniversalQrScannerModal = false
      )
    }
  }

  fun scanUniversalQrToken(token: String) {
    val found = MekanikRepository.findQrPassportByToken(token) ?: _uiState.value.universalQrPassports.firstOrNull {
      it.qrToken.contains(token, ignoreCase = true) ||
      it.vehicleVin.contains(token, ignoreCase = true) ||
      it.vehiclePlate.contains(token, ignoreCase = true)
    }

    if (found != null) {
      val defaultTab = when (_uiState.value.currentAccountRole) {
        AccountRole.GARAGE_PRO -> 1 // Atelier & OR
        AccountRole.VENDEUR_PIECES -> 2 // Pièces
        AccountRole.ATELIER_MAGASIN_MIXTE -> 1 // Atelier & Magasin
        AccountRole.ATELIER_CARROSSERIE -> 1 // Carrosserie & OR
        AccountRole.CONDUCTEUR -> 0 // Fiche Tech
        AccountRole.LOCATION_VOITURES -> 3 // Location
      }
      _uiState.update {
        it.copy(
          activeScannedQrPassport = found,
          selectedQrPassportRoleTab = defaultTab,
          showUniversalQrScannerModal = false,
          showUniversalQrPassportDetailModal = true,
          toastMessage = "✅ QR Code scanné avec succès : ${found.brand} ${found.model} (${found.vehiclePlate})"
        )
      }
    } else {
      _uiState.update {
        it.copy(
          toastMessage = "⚠️ QR Code non reconnu. Vérifiez le format du token."
        )
      }
    }
  }

  fun updateQrPartStatus(partId: String, newStatus: QrPartOrderStatus) {
    val currentPassport = _uiState.value.activeScannedQrPassport
    val updatedParts = currentPassport.orderedPartsForRetailer.map {
      if (it.partId == partId) it.copy(status = newStatus) else it
    }
    val updatedPassport = currentPassport.copy(orderedPartsForRetailer = updatedParts)
    val updatedList = _uiState.value.universalQrPassports.map {
      if (it.qrToken == currentPassport.qrToken) updatedPassport else it
    }

    _uiState.update {
      it.copy(
        activeScannedQrPassport = updatedPassport,
        universalQrPassports = updatedList,
        toastMessage = "📦 Statut pièce mis à jour : ${newStatus.label}"
      )
    }
  }

  fun updateQrRepairOrderStatus(orderNumber: String, newStatus: RepairStatus) {
    val currentPassport = _uiState.value.activeScannedQrPassport
    val updatedOrders = currentPassport.activeRepairOrdersForWorkshop.map {
      if (it.orderNumber == orderNumber) it.copy(status = newStatus) else it
    }
    val updatedPassport = currentPassport.copy(activeRepairOrdersForWorkshop = updatedOrders)
    val updatedList = _uiState.value.universalQrPassports.map {
      if (it.qrToken == currentPassport.qrToken) updatedPassport else it
    }

    _uiState.update {
      it.copy(
        activeScannedQrPassport = updatedPassport,
        universalQrPassports = updatedList,
        toastMessage = "🛠️ Statut Ordre de Réparation mis à jour : ${newStatus.label}"
      )
    }
  }

  fun registerNewVehicleQrPassport(
    vin: String,
    plate: String,
    brand: String,
    model: String,
    engineCode: String,
    mileage: Int,
    role: AccountRole
  ) {
    val newToken = "MK-QR-2026-${brand.take(3).uppercase()}-${plate.takeLast(5).replace("-", "")}"
    val newPassport = VehiclePassportQrData(
      qrToken = newToken,
      registeredByRole = role,
      registeredByName = when (role) {
        AccountRole.GARAGE_PRO -> "Garage Auto Performance"
        AccountRole.VENDEUR_PIECES -> "Sarl Auto Pièces Alger"
        AccountRole.ATELIER_MAGASIN_MIXTE -> "Auto Pièces & Montage Express"
        AccountRole.ATELIER_CARROSSERIE -> "Carrosserie & Tôlerie Pro Alger"
        AccountRole.CONDUCTEUR -> "Conducteur Particulier"
        AccountRole.LOCATION_VOITURES -> "Agence Location de Voitures"
      },
      registrationDate = "18 Août 2026",
      vehicleVin = vin.ifBlank { "WVWZZZ5NZMW998877" },
      vehiclePlate = plate.ifBlank { "09911-122-16" },
      brand = brand.ifBlank { "Volkswagen" },
      model = model.ifBlank { "Golf 8 1.5 eTSI" },
      exactYear = 2023,
      engineCode = engineCode.ifBlank { "EA211" },
      displacement = "1.5 TSI / Turbo",
      powerCh = 150,
      fuelType = "Essence / Hybride",
      gearbox = "DSG7",
      currentMileageKm = mileage.coerceAtLeast(1000),
      healthScorePct = 95,
      lastServiceDate = "18 Août 2026",
      nextServiceKm = mileage + 15000,
      qrVerificationHash = "SHA256:${System.currentTimeMillis()}MK_UNIVERSAL",
      orderedPartsForRetailer = emptyList(),
      activeRepairOrdersForWorkshop = emptyList(),
      rentalContractInfo = null,
      recentCertifiedRecords = emptyList()
    )

    _uiState.update {
      it.copy(
        universalQrPassports = listOf(newPassport) + it.universalQrPassports,
        activeScannedQrPassport = newPassport,
        showGenerateVehicleQrModal = false,
        showUniversalQrPassportDetailModal = true,
        toastMessage = "🌟 Nouveau QR Passeport Véhicule généré : $newToken"
      )
    }
  }

  // --- PDF GENERATION & AUTOMATIC DOWNLOAD HANDLERS ---
  fun dismissPdfModal() {
    _uiState.update { it.copy(showPdfDownloadModal = false, generatedPdfResult = null) }
  }

  fun generateAndDownloadWorkshopQuotePdf(
    context: Context,
    quoteNumber: String,
    clientName: String,
    clientPhone: String,
    clientAddress: String,
    plateNumber: String,
    vehicleModel: String,
    vehicleMileage: String,
    quoteNotes: String,
    items: List<RepairItem>,
    laborHours: Float,
    laborRatePerHour: Int,
    diagnosticCostDzd: Int,
    suppliesCostDzd: Int,
    applyTva19: Boolean,
    fiscalStampDzd: Int
  ) {
    viewModelScope.launch {
      try {
        val result = PdfGeneratorUtil.generateWorkshopQuotePdf(
          context = context,
          quoteNumber = quoteNumber,
          clientName = clientName,
          clientPhone = clientPhone,
          clientAddress = clientAddress,
          plateNumber = plateNumber,
          vehicleModel = vehicleModel,
          vehicleMileage = vehicleMileage,
          quoteNotes = quoteNotes,
          items = items,
          laborHours = laborHours,
          laborRatePerHour = laborRatePerHour,
          diagnosticCostDzd = diagnosticCostDzd,
          suppliesCostDzd = suppliesCostDzd,
          applyTva19 = applyTva19,
          fiscalStampDzd = fiscalStampDzd
        )
        _uiState.update {
          it.copy(
            generatedPdfResult = result,
            showPdfDownloadModal = true,
            toastMessage = "✅ Devis PDF ${result.fileName} généré et téléchargé avec succès !"
          )
        }
      } catch (e: Exception) {
        _uiState.update {
          it.copy(toastMessage = "Erreur de génération PDF: ${e.message}")
        }
      }
    }
  }

  fun generateAndDownloadFiscalInvoicePdf(
    context: Context,
    invoice: FiscalInvoice
  ) {
    viewModelScope.launch {
      try {
        val result = PdfGeneratorUtil.generateFiscalInvoicePdf(context, invoice)
        _uiState.update {
          it.copy(
            generatedPdfResult = result,
            showPdfDownloadModal = true,
            toastMessage = "✅ Facture PDF ${result.fileName} générée et téléchargée !"
          )
        }
      } catch (e: Exception) {
        _uiState.update {
          it.copy(toastMessage = "Erreur de génération PDF: ${e.message}")
        }
      }
    }
  }

  fun generateAndDownloadDriverInvoicePdf(
    context: Context,
    invoice: DriverInvoiceItem
  ) {
    viewModelScope.launch {
      try {
        val result = PdfGeneratorUtil.generateDriverInvoicePdf(context, invoice)
        _uiState.update {
          it.copy(
            generatedPdfResult = result,
            showPdfDownloadModal = true,
            toastMessage = "✅ Facture PDF ${result.fileName} téléchargée sur l'appareil !"
          )
        }
      } catch (e: Exception) {
        _uiState.update {
          it.copy(toastMessage = "Erreur de génération PDF: ${e.message}")
        }
      }
    }
  }

  fun generateAndDownloadRepairOrderPdf(
    context: Context,
    order: RepairOrder
  ) {
    viewModelScope.launch {
      try {
        val items = order.items.map {
          InvoiceLineItem(
            reference = it.reference,
            description = it.name,
            quantity = it.quantity,
            unitPriceDzd = it.priceDzd.toDouble(),
            isLabor = false
          )
        } + listOf(
          InvoiceLineItem(
            reference = "MO-ATELIER",
            description = "Main d'œuvre mécanique (${order.laborHours}h à ${order.laborRatePerHourDzd} DA/h)",
            quantity = 1,
            unitPriceDzd = order.laborTotalDzd.toDouble(),
            isLabor = true
          ),
          InvoiceLineItem(
            reference = "CHECKLIST-OR",
            description = "État Ext:${order.checklist.exteriorCleanliness.label}/Int:${order.checklist.interiorCleanliness.label} • Ampoules:${order.checklist.okBulbsCount}/${order.checklist.bulbs.size} OK (${order.checklist.burntOrReplacedBulbs.size} HS)",
            quantity = 1,
            unitPriceDzd = 0.0,
            isLabor = true
          )
        )

        val fiscalInvoice = FiscalInvoice(
          invoiceNumber = order.orderNumber,
          invoiceType = InvoiceType.FACTURE_DEFINITIVE,
          date = order.date,
          garageName = "Mekanik+ Services Pro Alger",
          clientName = order.clientName,
          clientPhone = order.clientPhone,
          vehicleVin = order.vin,
          vehicleModel = order.vehicleModel,
          plateNumber = order.plateNumber,
          items = items,
          isPaid = order.isSigned,
          paymentMethod = PaymentMethod.CIB_EDAHABIA,
          qrFiscalHash = "DZ-OR-${order.orderNumber}"
        )

        val result = PdfGeneratorUtil.generateFiscalInvoicePdf(context, fiscalInvoice)
        _uiState.update {
          it.copy(
            generatedPdfResult = result,
            showPdfDownloadModal = true,
            toastMessage = "✅ Facture/Devis ${result.fileName} généré et téléchargé !"
          )
        }
      } catch (e: Exception) {
        _uiState.update {
          it.copy(toastMessage = "Erreur de génération PDF: ${e.message}")
        }
      }
    }
  }

  fun generateAndDownloadRentalContractPdf(
    context: Context,
    contract: RentalContract
  ) {
    viewModelScope.launch {
      try {
        val result = PdfGeneratorUtil.generateRentalContractPdf(context, contract)
        _uiState.update {
          it.copy(
            generatedPdfResult = result,
            showPdfDownloadModal = true,
            toastMessage = "✅ Contrat de location ${result.fileName} généré et téléchargé !"
          )
        }
      } catch (e: Exception) {
        _uiState.update {
          it.copy(toastMessage = "Erreur de génération PDF: ${e.message}")
        }
      }
    }
  }

  // --- ACOUSTIC SOUND RECORDING & AI ENGINE FAULT DIAGNOSIS HANDLERS ---
  fun openEngineSoundRecorder(presetId: String? = null) {
    _uiState.update {
      it.copy(
        showEngineSoundRecorderModal = true,
        selectedSoundPresetId = presetId ?: it.selectedSoundPresetId,
        acousticDiagnosisResult = null,
        isRecordingEngineSound = false,
        engineSoundRecordingDurationSec = 0
      )
    }
  }

  fun closeEngineSoundRecorder() {
    _uiState.update {
      it.copy(
        showEngineSoundRecorderModal = false,
        isRecordingEngineSound = false
      )
    }
  }

  fun selectEngineSoundPreset(presetId: String) {
    _uiState.update {
      it.copy(
        selectedSoundPresetId = presetId,
        acousticDiagnosisResult = null
      )
    }
  }

  fun startEngineSoundRecording() {
    _uiState.update {
      it.copy(
        isRecordingEngineSound = true,
        engineSoundRecordingDurationSec = 0,
        acousticDiagnosisResult = null
      )
    }

    viewModelScope.launch {
      // Simulate 5 seconds audio recording with live waveform fluctuations
      for (sec in 1..5) {
        delay(1000)
        val randomAmps = List(14) { (0.25f + (Math.random() * 0.7f).toFloat()) }
        val currentDb = (72..88).random()
        val currentPeakHz = (200..4500).random()
        _uiState.update {
          it.copy(
            engineSoundRecordingDurationSec = sec,
            engineSoundLiveAmplitudes = randomAmps,
            recordedDecibelLevel = currentDb,
            recordedPeakHz = currentPeakHz
          )
        }
      }

      // Auto-finish recording and run AI Acoustic Analysis
      stopAndAnalyzeEngineSound()
    }
  }

  fun stopAndAnalyzeEngineSound() {
    _uiState.update { it.copy(isRecordingEngineSound = false) }

    viewModelScope.launch {
      delay(800) // Small calculation delay for AI spectrum processing
      val currentVehicle = _uiState.value.activeVehicle
      val currentPresetId = _uiState.value.selectedSoundPresetId
      val currentDb = _uiState.value.recordedDecibelLevel
      val currentPeakHz = _uiState.value.recordedPeakHz

      val diagnosis = AcousticSoundRepository.analyzeAcousticRecording(
        detectedDominantHz = currentPeakHz,
        decibelLevel = currentDb,
        selectedPresetId = currentPresetId,
        vehicle = currentVehicle
      )

      val newHistoryItem = SoundRecordingHistoryItem(
        id = "REC-AUDIO-${(1000..9999).random()}",
        title = "Enregistrement Moteur (${currentVehicle?.brand ?: "Véhicule"} ${currentVehicle?.model ?: ""})",
        date = "Aujourd'hui à ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.FRENCH).format(java.util.Date())}",
        durationSeconds = 5,
        vehicleModel = "${currentVehicle.brand} ${currentVehicle.model}",
        plateNumber = _uiState.value.driverVehicle.plateNumber,
        diagnosedProblem = diagnosis.identifiedPreset.title,
        severity = diagnosis.identifiedPreset.severity,
        confidenceScorePercent = diagnosis.confidenceScorePercent,
        isSharedWithWorkshop = false
      )

      _uiState.update {
        it.copy(
          acousticDiagnosisResult = diagnosis,
          soundRecordingHistory = listOf(newHistoryItem) + it.soundRecordingHistory,
          toastMessage = "✅ Analyse acoustique terminée : ${diagnosis.identifiedPreset.title}"
        )
      }
    }
  }

  fun shareAcousticDiagnosisWithWorkshop() {
    _uiState.update {
      it.copy(
        toastMessage = "✅ Enregistrement audio et diagnostic acoustique transmis à l'atelier sélectionné !"
      )
    }
  }

  // --- Workshop Free Client Accounts & Portal Methods ---
  fun openClientPortal(account: WorkshopClientAccount) {
    _uiState.update {
      it.copy(
        activeClientPortalAccount = account,
        showWorkshopClientPortalModal = true
      )
    }
  }

  fun closeClientPortal() {
    _uiState.update {
      it.copy(showWorkshopClientPortalModal = false)
    }
  }

  fun openCreateClientAccountDialog(
    initialPlate: String = "",
    initialName: String = "",
    initialPhone: String = "",
    initialModel: String = "",
    initialVin: String = ""
  ) {
    _uiState.update {
      it.copy(
        prefilledPlateNumber = initialPlate,
        prefilledClientName = initialName,
        prefilledClientPhone = initialPhone,
        prefilledVehicleModel = initialModel,
        prefilledVin = initialVin,
        showCreateClientAccountModal = true
      )
    }
  }

  fun closeCreateClientAccountDialog() {
    _uiState.update {
      it.copy(showCreateClientAccountModal = false)
    }
  }

  fun addNewClientAccount(account: WorkshopClientAccount) {
    _uiState.update {
      it.copy(
        workshopClientAccounts = listOf(account) + it.workshopClientAccounts.filter { acc -> acc.plateNumber != account.plateNumber },
        toastMessage = "✅ Compte client gratuit créé pour ${account.plateNumber} !"
      )
    }
  }

  fun loginClientWithCredentials(plate: String, pin: String): Boolean {
    val cleanPlate = plate.trim().replace("-", "").replace(" ", "").uppercase()
    val cleanPin = pin.trim()
    val account = _uiState.value.workshopClientAccounts.firstOrNull {
      val accPlate = it.plateNumber.replace("-", "").replace(" ", "").uppercase()
      (accPlate == cleanPlate || it.vin.contains(cleanPlate, ignoreCase = true)) && it.passwordPin == cleanPin
    }
    return if (account != null) {
      _uiState.update {
        it.copy(
          activeClientPortalAccount = account,
          showWorkshopClientPortalModal = true,
          toastMessage = "✅ Connexion réussie à l'Espace Client (${account.clientName})"
        )
      }
      true
    } else {
      _uiState.update {
        it.copy(toastMessage = "❌ Matricule ou mot de passe incorrect. Vérifiez vos identifiants.")
      }
      false
    }
  }

  // --- Voice-to-Text Dictation Methods ---
  fun openVoiceDictation(targetField: String = "repair_order_notes", initialText: String = "") {
    _uiState.update {
      it.copy(
        voiceDictationTargetField = targetField,
        prefilledNotes = initialText,
        showVoiceDictationModal = true
      )
    }
  }

  fun closeVoiceDictation() {
    _uiState.update {
      it.copy(showVoiceDictationModal = false)
    }
  }

  fun applyVoiceDictation(text: String) {
    _uiState.update {
      it.copy(
        prefilledNotes = text,
        showVoiceDictationModal = false,
        toastMessage = "🎙️ Dictée vocale insérée avec succès !"
      )
    }
  }

  // --- OCR Auto-Fill Repair Order Integration ---
  fun autoFillRepairOrderFromScan(
    clientName: String,
    clientPhone: String,
    plateNumber: String,
    vehicleModel: String,
    vin: String,
    mileage: String,
    notes: String
  ) {
    _uiState.update {
      it.copy(
        prefilledClientName = clientName,
        prefilledClientPhone = clientPhone,
        prefilledPlateNumber = plateNumber,
        prefilledVehicleModel = vehicleModel,
        prefilledVin = vin,
        prefilledMileage = mileage,
        prefilledNotes = notes,
        showCarteGriseScannerModal = false,
        showCameraScanModal = false,
        showNewQuoteDialog = true,
        toastMessage = "📋 Ordre de Réparation pré-rempli depuis la Carte Grise !"
      )
    }
  }

  // --- 15. Compte Entreprise Ultra Pro & Gestion Hiérarchique Multi-Comptes ---
  fun openUltraProManager() {
    _uiState.update {
      it.copy(showUltraProManagerModal = true)
    }
  }

  fun closeUltraProManager() {
    _uiState.update {
      it.copy(showUltraProManagerModal = false)
    }
  }

  fun openCreateSubAccountDialog(accountToEdit: UltraProSubAccount? = null) {
    _uiState.update {
      it.copy(
        selectedSubAccountForEdit = accountToEdit,
        showCreateSubAccountModal = true
      )
    }
  }

  fun closeCreateSubAccountDialog() {
    _uiState.update {
      it.copy(
        selectedSubAccountForEdit = null,
        showCreateSubAccountModal = false
      )
    }
  }

  fun addNewSubAccount(account: UltraProSubAccount) {
    val existing = _uiState.value.ultraProSubAccounts.find { it.id == account.id }
    val updatedList = if (existing != null) {
      _uiState.value.ultraProSubAccounts.map { if (it.id == account.id) account else it }
    } else {
      _uiState.value.ultraProSubAccounts + account
    }

    val newLog = SubAccountAuditLog(
      id = "LOG-${(1000..9999).random()}",
      authorName = _uiState.value.activeImpersonatedSubAccount?.fullName ?: "M. Reda Mansouri (Gérant)",
      authorRole = _uiState.value.activeImpersonatedSubAccount?.role?.shortLabel ?: "Gérant Principal",
      actionTitle = if (existing != null) "Modification sous-compte : ${account.fullName}" else "Création sous-compte : ${account.fullName}",
      details = "Rôle attribué : ${account.role.label} (${account.department}). Permissions : ${account.permissions.size} droits configurés.",
      timestamp = "Aujourd'hui à ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.FRENCH).format(java.util.Date())}",
      department = account.department,
      severity = "IMPORTANT"
    )

    _uiState.update {
      it.copy(
        ultraProSubAccounts = updatedList,
        subAccountAuditLogs = listOf(newLog) + it.subAccountAuditLogs,
        showCreateSubAccountModal = false,
        toastMessage = "✅ Sous-compte ${account.fullName} (${account.role.shortLabel}) enregistré avec succès !"
      )
    }
  }

  fun deleteSubAccount(id: String) {
    val target = _uiState.value.ultraProSubAccounts.find { it.id == id }
    if (target?.role == UltraProSubAccountRole.GERANT_PRINCIPAL) {
      _uiState.update {
        it.copy(toastMessage = "❌ Impossible de supprimer le compte Administrateur Général (Gérant) !")
      }
      return
    }

    val newLog = SubAccountAuditLog(
      id = "LOG-${(1000..9999).random()}",
      authorName = _uiState.value.activeImpersonatedSubAccount?.fullName ?: "M. Reda Mansouri (Gérant)",
      authorRole = _uiState.value.activeImpersonatedSubAccount?.role?.shortLabel ?: "Gérant Principal",
      actionTitle = "Suppression sous-compte : ${target?.fullName}",
      details = "Compte révoqué par la direction. Identifiant : $id",
      timestamp = "Aujourd'hui à ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.FRENCH).format(java.util.Date())}",
      department = target?.department ?: "Direction",
      severity = "CRITIQUE"
    )

    _uiState.update {
      it.copy(
        ultraProSubAccounts = it.ultraProSubAccounts.filter { acc -> acc.id != id },
        subAccountAuditLogs = listOf(newLog) + it.subAccountAuditLogs,
        toastMessage = "🗑️ Sous-compte ${target?.fullName} supprimé."
      )
    }
  }

  fun toggleSubAccountActive(id: String, isActive: Boolean) {
    val updated = _uiState.value.ultraProSubAccounts.map {
      if (it.id == id) it.copy(isActive = isActive) else it
    }
    _uiState.update {
      it.copy(
        ultraProSubAccounts = updated,
        toastMessage = if (isActive) "✅ Accès sous-compte réactivé" else "⏸️ Accès sous-compte suspendu temporairement"
      )
    }
  }

  fun switchActiveSubAccount(account: UltraProSubAccount?) {
    _uiState.update {
      it.copy(
        activeImpersonatedSubAccount = account,
        toastMessage = if (account != null) {
          "🔄 Mode simulation actif : ${account.fullName} [${account.role.shortLabel}]"
        } else {
          "👑 Retour au profil Administrateur Général (Gérant)"
        }
      )
    }
  }

  // --- WORKFLOW FLUX PIÈCES ATELIER (4 ÉTAPES) ---

  fun openCreatePartsRequisitionDialog() {
    _uiState.update { it.copy(showCreatePartsRequisitionModal = true) }
  }

  fun closeCreatePartsRequisitionDialog() {
    _uiState.update { it.copy(showCreatePartsRequisitionModal = false) }
  }

  fun openPurchaseExtensionDialog() {
    _uiState.update { it.copy(showPurchaseExtensionModal = true) }
  }

  fun closePurchaseExtensionDialog() {
    _uiState.update { it.copy(showPurchaseExtensionModal = false) }
  }

  fun purchaseSubAccountExtensionPack(packsCount: Int = 1) {
    val extraAccounts = packsCount * 2
    val costDzd = packsCount * 2500
    val updatedOrg = _uiState.value.ultraProOrganization.copy(
      maxSubAccountsAllowed = _uiState.value.ultraProOrganization.maxSubAccountsAllowed + extraAccounts,
      purchasedExtensionPacksCount = _uiState.value.ultraProOrganization.purchasedExtensionPacksCount + packsCount
    )

    val newLog = SubAccountAuditLog(
      id = "LOG-${(1000..9999).random()}",
      authorName = _uiState.value.activeImpersonatedSubAccount?.fullName ?: "M. Reda Mansouri (Gérant)",
      authorRole = _uiState.value.activeImpersonatedSubAccount?.role?.shortLabel ?: "Gérant Principal",
      actionTitle = "Achat Pack Extension +$extraAccounts Sous-Comptes",
      details = "Paiement de $costDzd DA effectué. Quota d'entreprise augmenté à ${updatedOrg.maxSubAccountsAllowed} sous-comptes.",
      timestamp = "Aujourd'hui à ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.FRENCH).format(java.util.Date())}",
      department = "Direction Générale",
      severity = "IMPORTANT"
    )

    _uiState.update {
      it.copy(
        ultraProOrganization = updatedOrg,
        subAccountAuditLogs = listOf(newLog) + it.subAccountAuditLogs,
        showPurchaseExtensionModal = false,
        toastMessage = "🎉 Pack Extension +$extraAccounts Sous-Comptes activé avec succès ($costDzd DA) !"
      )
    }
  }

  // Étape 1 : Le mécanicien émet la demande de pièces après recherche par châssis/VIN
  fun submitMechanicPartsRequisition(
    vehicleModel: String,
    vin: String,
    plate: String,
    parts: List<RequisitionPartItem>,
    notes: String
  ) {
    val reqNumber = "DMD-${(1000..9999).random()}"
    val technicianName = _uiState.value.activeImpersonatedSubAccount?.fullName ?: "Karim Meziane (Mécanicien)"
    val technicianId = _uiState.value.activeImpersonatedSubAccount?.id ?: "SUB-006"

    val newRequisition = WorkshopPartsRequisition(
      id = "REQ-2026-${(100..999).random()}",
      orderNumber = reqNumber,
      linkedRepairOrderId = "or_001",
      vehicleModel = vehicleModel,
      vehicleVin = vin,
      plateNumber = plate,
      requestedByTechnicianId = technicianId,
      requestedByTechnicianName = "$technicianName [Mécanicien]",
      parts = parts,
      status = PartsRequisitionStatus.DEMANDE_MECANICIEN,
      createdAt = "Aujourd'hui à ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.FRENCH).format(java.util.Date())}",
      notes = notes
    )

    val newLog = SubAccountAuditLog(
      id = "LOG-${(1000..9999).random()}",
      authorName = technicianName,
      authorRole = "Mécanicien",
      actionTitle = "Création Demande Pièces $reqNumber",
      details = "Véhicule : $vehicleModel ($plate). ${parts.size} référence(s) demandée(s) d'une valeur de ${newRequisition.totalRequisitionDzd} DA.",
      timestamp = "Aujourd'hui à ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.FRENCH).format(java.util.Date())}",
      department = "Atelier Mécanique",
      severity = "NORMAL"
    )

    _uiState.update {
      it.copy(
        partsRequisitions = listOf(newRequisition) + it.partsRequisitions,
        subAccountAuditLogs = listOf(newLog) + it.subAccountAuditLogs,
        showCreatePartsRequisitionModal = false,
        toastMessage = "🔧 Demande $reqNumber créée par le mécanicien. En attente de validation par le Chef d'Atelier."
      )
    }
  }

  // Étape 2 : Le Chef d'Atelier valide et approuve techniquement les pièces
  fun approvePartsRequisitionByWorkshopManager(requisitionId: String) {
    val target = _uiState.value.partsRequisitions.find { it.id == requisitionId } ?: return
    val managerName = _uiState.value.activeImpersonatedSubAccount?.fullName ?: "Mourad Guendouz (Chef d'Atelier)"

    val updatedRequisition = target.copy(
      status = PartsRequisitionStatus.VALIDEE_CHEF_ATELIER,
      approvedByWorkshopManagerAt = "Aujourd'hui à ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.FRENCH).format(java.util.Date())}",
      workshopManagerName = managerName
    )

    val newLog = SubAccountAuditLog(
      id = "LOG-${(1000..9999).random()}",
      authorName = managerName,
      authorRole = "Chef d'Atelier",
      actionTitle = "Approbation Technique Pièces ${target.orderNumber}",
      details = "Demande ${target.orderNumber} approuvée par le Chef d'Atelier pour ${target.vehicleModel} (${target.plateNumber}). Transmise au magasin.",
      timestamp = "Aujourd'hui à ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.FRENCH).format(java.util.Date())}",
      department = "Atelier Mécanique",
      severity = "NORMAL"
    )

    _uiState.update {
      it.copy(
        partsRequisitions = it.partsRequisitions.map { req -> if (req.id == requisitionId) updatedRequisition else req },
        subAccountAuditLogs = listOf(newLog) + it.subAccountAuditLogs,
        toastMessage = "✅ Demande ${target.orderNumber} validée par le Chef d'Atelier ! Transmise au Magasinier."
      )
    }
  }

  // Étape 3 : Le Magasinier reçoit la demande et prépare les pièces selon le châssis
  fun preparePartsRequisitionByStorekeeper(requisitionId: String) {
    val target = _uiState.value.partsRequisitions.find { it.id == requisitionId } ?: return
    val storekeeperName = _uiState.value.activeImpersonatedSubAccount?.fullName ?: "Yacine Cherif (Magasinier)"

    val updatedRequisition = target.copy(
      status = PartsRequisitionStatus.EN_PREPARATION_MAGASIN,
      receivedByStorekeeperAt = "Aujourd'hui à ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.FRENCH).format(java.util.Date())}",
      storekeeperName = storekeeperName,
      notes = target.notes + " [Pièces préparées en rayon par $storekeeperName]"
    )

    val newLog = SubAccountAuditLog(
      id = "LOG-${(1000..9999).random()}",
      authorName = storekeeperName,
      authorRole = "Magasinier Comptoir",
      actionTitle = "Préparation Pièces Magasin ${target.orderNumber}",
      details = "Pièces préparées au casier atelier selon données VIN/Châssis. En attente de sortie finale par le Chef Magasin.",
      timestamp = "Aujourd'hui à ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.FRENCH).format(java.util.Date())}",
      department = "Magasin & Pièces",
      severity = "NORMAL"
    )

    _uiState.update {
      it.copy(
        partsRequisitions = it.partsRequisitions.map { req -> if (req.id == requisitionId) updatedRequisition else req },
        subAccountAuditLogs = listOf(newLog) + it.subAccountAuditLogs,
        toastMessage = "📦 Demande ${target.orderNumber} reçue & préparée par le magasinier. En attente de confirmation par le Chef Magasin."
      )
    }
  }

  // Étape 4 : Le Chef du Département Magasin confirme la sortie de stock -> Imputation directe sur Facture et OR client !
  fun confirmStockDispatchByHeadStorekeeper(requisitionId: String) {
    val target = _uiState.value.partsRequisitions.find { it.id == requisitionId } ?: return
    val headStoreName = _uiState.value.activeImpersonatedSubAccount?.fullName ?: "Kamel Boudiaf (Chef Magasinier)"

    val updatedRequisition = target.copy(
      status = PartsRequisitionStatus.CONFIRMEE_SORTIE_STOCK,
      confirmedByHeadStorekeeperAt = "Aujourd'hui à ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.FRENCH).format(java.util.Date())}",
      headStorekeeperName = headStoreName,
      notes = target.notes + " [Sortie de stock confirmée définitivement par le Chef Magasin. Imputée sur Facture & OR client.]"
    )

    // 1. Ajouter automatiquement les articles dans l'Ordre de Réparation (OR)
    val convertedRepairItems = target.parts.map { part ->
      RepairItem(
        name = "${part.name} (${part.brand})",
        reference = part.oemReference,
        quantity = part.quantity,
        priceDzd = part.unitPriceDzd
      )
    }

    val updatedOrders = _uiState.value.repairOrders.map { order ->
      if (order.id == target.linkedRepairOrderId || order.plateNumber == target.plateNumber) {
        order.copy(items = order.items + convertedRepairItems)
      } else {
        order
      }
    }

    // 2. Ajouter automatiquement les lignes de facturation dans les Factures Fiscales
    val invoiceLineItems = target.parts.map { part ->
      InvoiceLineItem(
        reference = part.oemReference,
        description = "${part.name} [Sortie Magasin ${target.orderNumber}]",
        quantity = part.quantity,
        unitPriceDzd = part.unitPriceDzd.toDouble()
      )
    }

    val updatedClientAccounts = _uiState.value.workshopClientAccounts.map { acc ->
      if (acc.plateNumber == target.plateNumber) {
        val updatedInvoices = acc.invoices.map { inv ->
          inv.copy(items = inv.items + invoiceLineItems)
        }
        acc.copy(invoices = updatedInvoices)
      } else {
        acc
      }
    }

    val newLog = SubAccountAuditLog(
      id = "LOG-${(1000..9999).random()}",
      authorName = headStoreName,
      authorRole = "Chef Magasinier",
      actionTitle = "Sortie de stock confirmée & Facturation auto (${target.orderNumber})",
      details = "Sortie définitive confirmée pour ${target.parts.size} référence(s) (${target.totalRequisitionDzd} DA). Pièces automatiquement ajoutées sur l'Ordre de Réparation et sur la Facture de ${target.vehicleModel} (${target.plateNumber}).",
      timestamp = "Aujourd'hui à ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.FRENCH).format(java.util.Date())}",
      department = "Magasin & Pièces",
      severity = "IMPORTANT"
    )

    _uiState.update {
      it.copy(
        partsRequisitions = it.partsRequisitions.map { req -> if (req.id == requisitionId) updatedRequisition else req },
        repairOrders = updatedOrders,
        workshopClientAccounts = updatedClientAccounts,
        subAccountAuditLogs = listOf(newLog) + it.subAccountAuditLogs,
        toastMessage = "✨ Sortie confirmée par le Chef Magasin ! Pièces automatiquement imputées sur l'OR et la Facture du client (${target.plateNumber})."
      )
    }
  }

  fun rejectPartsRequisition(requisitionId: String, reason: String) {
    val target = _uiState.value.partsRequisitions.find { it.id == requisitionId } ?: return
    val authorName = _uiState.value.activeImpersonatedSubAccount?.fullName ?: "Mourad Guendouz (Chef d'Atelier)"

    val updatedRequisition = target.copy(
      status = PartsRequisitionStatus.REJETEE,
      notes = "Rejeté : $reason"
    )

    val newLog = SubAccountAuditLog(
      id = "LOG-${(1000..9999).random()}",
      authorName = authorName,
      authorRole = _uiState.value.activeImpersonatedSubAccount?.role?.shortLabel ?: "Responsable",
      actionTitle = "Rejet Demande Pièces ${target.orderNumber}",
      details = "Motif de refus : $reason. Véhicule : ${target.vehicleModel}.",
      timestamp = "Aujourd'hui à ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.FRENCH).format(java.util.Date())}",
      department = "Atelier & Magasin",
      severity = "IMPORTANT"
    )

    _uiState.update {
      it.copy(
        partsRequisitions = it.partsRequisitions.map { req -> if (req.id == requisitionId) updatedRequisition else req },
        subAccountAuditLogs = listOf(newLog) + it.subAccountAuditLogs,
        toastMessage = "❌ Demande ${target.orderNumber} refusée."
      )
    }
  }

  // --- OEM SOFTWARE PLATFORMS & FULL SUPPLEMENT ACTIONS ---
  fun openOemSoftwareLauncher(tool: OemSoftwareTool? = null) {
    _uiState.update {
      it.copy(
        selectedOemToolForLaunch = tool ?: it.oemSoftwareTools.firstOrNull(),
        showOemSoftwareLauncherModal = true
      )
    }
  }

  fun closeOemSoftwareLauncher() {
    _uiState.update { it.copy(showOemSoftwareLauncherModal = false) }
  }

  fun openPurchaseFullSoftwareSupplementModal() {
    _uiState.update { it.copy(showPurchaseFullSoftwareSupplementModal = true) }
  }

  fun closePurchaseFullSoftwareSupplementModal() {
    _uiState.update { it.copy(showPurchaseFullSoftwareSupplementModal = false) }
  }

  fun setWorkshopActiveOemMode(group: OemManufacturerGroup) {
    _uiState.update {
      it.copy(
        workshopSoftwareConfig = it.workshopSoftwareConfig.copy(activeModeGroup = group),
        selectedOemToolGroupFilter = group,
        toastMessage = "🔧 Mode Constructeur activé : ${group.label} (${group.iconEmoji})"
      )
    }
  }

  fun setSelectedOemToolGroupFilter(group: OemManufacturerGroup?) {
    _uiState.update { it.copy(selectedOemToolGroupFilter = group) }
  }

  fun purchaseFullSoftwarePackage(paymentMethod: PaymentMethod = PaymentMethod.CIB_EDAHABIA) {
    val currentOrg = _uiState.value.ultraProOrganization
    val updatedOrg = currentOrg.copy(
      priceDzdAnnual = currentOrg.priceDzdAnnual + 10000
    )

    val newLog = SubAccountAuditLog(
      id = "LOG-${(1000..9999).random()}",
      authorName = _uiState.value.activeImpersonatedSubAccount?.fullName ?: "M. Reda Mansouri",
      authorRole = _uiState.value.activeImpersonatedSubAccount?.role?.shortLabel ?: "Gérant Principal",
      actionTitle = "Souscription Pack Intégral Tous Logiciels Constructeurs",
      details = "Activation de l'accès illimité à tous les catalogues EPC et manuels d'atelier constructeurs (+10 000 DA). Règlement : ${paymentMethod.label}.",
      timestamp = "Aujourd'hui à ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.FRENCH).format(java.util.Date())}",
      department = "Direction Générale & Technique",
      severity = "CRITIQUE"
    )

    _uiState.update {
      it.copy(
        ultraProOrganization = updatedOrg,
        workshopSoftwareConfig = it.workshopSoftwareConfig.copy(hasFullSoftwarePackageUnlocked = true),
        showPurchaseFullSoftwareSupplementModal = false,
        subAccountAuditLogs = listOf(newLog) + it.subAccountAuditLogs,
        toastMessage = "🚀 Pack Intégral Tous Logiciels Constructeurs activé (+10 000 DA) ! Tous les manuels & catalogues EPC sont débloqués."
      )
    }
  }

  fun launchOemSoftwareTool(tool: OemSoftwareTool) {
    val newLog = SubAccountAuditLog(
      id = "LOG-${(1000..9999).random()}",
      authorName = _uiState.value.activeImpersonatedSubAccount?.fullName ?: "Utilisateur Atelier",
      authorRole = _uiState.value.activeImpersonatedSubAccount?.role?.shortLabel ?: "Technicien",
      actionTitle = "Session Intrasystème : ${tool.name}",
      details = "Connexion sécurisée au portail officiel ${tool.name} (${tool.officialVersion}). Recherche pièces / manuels en cours.",
      timestamp = "Aujourd'hui à ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.FRENCH).format(java.util.Date())}",
      department = tool.group.label,
      severity = "NORMAL"
    )

    _uiState.update {
      it.copy(
        subAccountAuditLogs = listOf(newLog) + it.subAccountAuditLogs,
        toastMessage = "🌐 Lancement du logiciel constructeur : ${tool.name}..."
      )
    }
  }

  // --- API Gateway, Sandbox & Zero-Frais Caching Manager ---
  fun openDevApiManagerModal() {
    _uiState.update { it.copy(showDevApiManagerModal = true) }
  }

  fun closeDevApiManagerModal() {
    _uiState.update { it.copy(showDevApiManagerModal = false) }
  }

  fun setApiDataSourceMode(mode: ApiDataSourceMode) {
    val previousMode = _uiState.value.apiDataSourceMode
    val isModeChange = previousMode != mode

    val newMetrics = when (mode) {
      ApiDataSourceMode.MEKANIK_ONE_UNIFIED -> _uiState.value.apiTelemetryMetrics.copy(
        avgResponseLatencyMs = 48,
        cacheHitRatePercent = 98,
        cachedRequests = _uiState.value.apiTelemetryMetrics.cachedRequests + 6,
        totalRequests = _uiState.value.apiTelemetryMetrics.totalRequests + 6
      )
      ApiDataSourceMode.OFFLINE_JSON -> _uiState.value.apiTelemetryMetrics.copy(
        avgResponseLatencyMs = 2,
        cacheHitRatePercent = 100,
        cachedRequests = _uiState.value.apiTelemetryMetrics.cachedRequests + 15,
        totalRequests = _uiState.value.apiTelemetryMetrics.totalRequests + 15,
        estimatedDinarSaved = _uiState.value.apiTelemetryMetrics.estimatedDinarSaved + 450.0
      )
      ApiDataSourceMode.TECDOC_SANDBOX -> _uiState.value.apiTelemetryMetrics.copy(
        avgResponseLatencyMs = 38,
        cacheHitRatePercent = 92
      )
      ApiDataSourceMode.CAR_API_OPENSOURCE -> _uiState.value.apiTelemetryMetrics.copy(
        avgResponseLatencyMs = 28,
        cacheHitRatePercent = 95
      )
      ApiDataSourceMode.PRODUCTION_HYBRID -> _uiState.value.apiTelemetryMetrics.copy(
        avgResponseLatencyMs = 15,
        cacheHitRatePercent = 88
      )
    }

    _uiState.update {
      it.copy(
        apiDataSourceMode = mode,
        apiTelemetryMetrics = newMetrics,
        toastMessage = if (isModeChange) "⚡ Source API basculée : ${mode.label} (${mode.badgeText})" else null
      )
    }
  }

  fun clearApiHttpCache() {
    _uiState.update {
      it.copy(
        apiTelemetryMetrics = it.apiTelemetryMetrics.copy(
          cachedRequests = 0,
          totalRequests = 1,
          cacheHitRatePercent = 0,
          estimatedDinarSaved = 0.0
        ),
        toastMessage = "🧹 Cache HTTP OkHttp vidé avec succès. Nouvelles requêtes en direct."
      )
    }
  }

  fun testEndpointConnectivity(endpointId: String) {
    viewModelScope.launch {
      val endpoint = _uiState.value.apiEndpointsList.find { it.id == endpointId }
      val endpointName = endpoint?.name ?: "Endpoint"

      _uiState.update {
        it.copy(toastMessage = "🔄 Test de ping vers $endpointName...")
      }
      delay(400)

      val updatedList = _uiState.value.apiEndpointsList.map { ep ->
        if (ep.id == endpointId) {
          ep.copy(
            isConnected = true,
            latencyMs = (12..45).random(),
            cachedItemsCount = ep.cachedItemsCount + (5..20).random()
          )
        } else ep
      }

      val updatedMetrics = _uiState.value.apiTelemetryMetrics.copy(
        totalRequests = _uiState.value.apiTelemetryMetrics.totalRequests + 1,
        cachedRequests = _uiState.value.apiTelemetryMetrics.cachedRequests + 1,
        estimatedDinarSaved = _uiState.value.apiTelemetryMetrics.estimatedDinarSaved + 30.0
      )

      _uiState.update {
        it.copy(
          apiEndpointsList = updatedList,
          apiTelemetryMetrics = updatedMetrics,
          toastMessage = "✅ Ping réussi ($endpointName) : 200 OK (${(12..45).random()} ms) - Zéro Frais (Cache/Sandbox)"
        )
      }
    }
  }

  // --- Recherche par Plaque d'Immatriculation (DZ & International) ---
  fun updateLicensePlateQuery(query: String) {
    _uiState.update { it.copy(licensePlateQuery = query) }
  }

  fun performLicensePlateSearch(rawQuery: String = _uiState.value.licensePlateQuery) {
    _uiState.update {
      it.copy(
        isSearchingPlate = false,
        toastMessage = "🔒 Recherche par immatriculation temporairement désactivée (en attente de certificat / autorisation officielle)."
      )
    }
  }

  fun selectLicensePlateMatch(match: LicensePlateMatch) {
    _uiState.update {
      it.copy(
        toastMessage = "🔒 Recherche par immatriculation temporairement désactivée (en attente de certificat / autorisation officielle)."
      )
    }
  }

  fun clearLocalApiCache() {
    clearApiHttpCache()
  }

  fun pingApiEndpoint(endpointId: String) {
    testEndpointConnectivity(endpointId)
  }

  fun lookupLicensePlate(plate: String) {
    performLicensePlateSearch(plate)
  }

  // --- 18. Multi-Appareils Atelier (Max 3 Appareils) & 2FA Google Authenticator ---
  fun openWorkshop2FAModal() {
    _uiState.update { it.copy(showWorkshop2FAModal = true) }
  }

  fun closeWorkshop2FAModal() {
    _uiState.update { it.copy(showWorkshop2FAModal = false) }
  }

  fun toggleWorkshop2FA(enabled: Boolean) {
    _uiState.update {
      val updatedConfig = it.workshop2FAConfig.copy(is2faEnabled = enabled)
      it.copy(
        workshop2FAConfig = updatedConfig,
        toastMessage = if (enabled) "🔒 Authentification 2FA Google Authenticator activée" else "⚠️ 2FA désactivée"
      )
    }
  }

  fun revokeWorkshopDevice(deviceId: String) {
    _uiState.update {
      val deviceToRevoke = it.workshop2FAConfig.connectedDevices.find { d -> d.id == deviceId }
      val updatedList = it.workshop2FAConfig.connectedDevices.filter { d -> d.id != deviceId }
      val updatedConfig = it.workshop2FAConfig.copy(connectedDevices = updatedList)
      it.copy(
        workshop2FAConfig = updatedConfig,
        toastMessage = "🗑️ Appareil ${deviceToRevoke?.deviceName ?: ""} révoqué et déconnecté de l'atelier"
      )
    }
  }

  fun pairNewWorkshopDevice(name: String, model: String, type: WorkshopDeviceType, totpCode: String) {
    val currentConfig = _uiState.value.workshop2FAConfig
    if (!currentConfig.canAddMoreDevices) {
      _uiState.update {
        it.copy(toastMessage = "❌ Limite de 3 appareils atteinte ! Veuillez révoquer un appareil existant.")
      }
      return
    }

    val newDevice = WorkshopConnectedDevice(
      id = "dev_${System.currentTimeMillis().toString().takeLast(4)}",
      deviceName = name,
      deviceModel = model.ifBlank { "Terminal Atelier Android" },
      deviceType = type,
      osVersion = "Android 14",
      ipLocation = "105.101.44.12 • Wi-Fi Garage",
      linkedDate = "Aujourd'hui",
      lastActiveTime = "En ligne actuellement",
      isCurrentDevice = false,
      status = ConnectedDeviceStatus.ACTIF,
      assignedTechnicianName = name
    )

    val updatedList = currentConfig.connectedDevices + newDevice
    val updatedConfig = currentConfig.copy(connectedDevices = updatedList)

    _uiState.update {
      it.copy(
        workshop2FAConfig = updatedConfig,
        toastMessage = "✅ Téléphone \"$name\" authentifié via 2FA Google et connecté à l'atelier (${updatedList.size}/3)"
      )
    }
  }

  // --- 19. Passerelle Magasin de Pièces & Atelier (Compte Unique - +5 000 DA) ---
  fun openWorkshopStoreLinkModal() {
    _uiState.update { it.copy(showWorkshopStoreLinkModal = true) }
  }

  fun closeWorkshopStoreLinkModal() {
    _uiState.update { it.copy(showWorkshopStoreLinkModal = false) }
  }

  fun toggleWorkshopStoreLinked(linked: Boolean) {
    _uiState.update {
      val updatedConfig = it.workshopStoreLinkConfig.copy(
        isStoreLinked = linked,
        isActivatedOnSubscription = linked
      )
      it.copy(
        workshopStoreLinkConfig = updatedConfig,
        toastMessage = if (linked) "🏪 Passerelle Magasin activée ! Compte unique Atelier + Magasin synchronisé (+5 000 DA)" else "Passerelle Magasin désactivée"
      )
    }
  }

  fun switchWorkshopActiveView(viewMode: WorkshopActiveView) {
    _uiState.update {
      val updatedConfig = it.workshopStoreLinkConfig.copy(activeView = viewMode)
      it.copy(
        workshopStoreLinkConfig = updatedConfig,
        toastMessage = "Bascule vers : ${viewMode.label}"
      )
    }
  }

  fun updateWorkshopStoreDetails(name: String, rc: String, wilaya: String, phone: String) {
    _uiState.update {
      val updatedConfig = it.workshopStoreLinkConfig.copy(
        storeName = name,
        storeRegisterNumber = rc,
        storeWilaya = wilaya,
        storePhone = phone
      )
      it.copy(
        workshopStoreLinkConfig = updatedConfig,
        toastMessage = "Informations du magasin de pièces mises à jour avec succès"
      )
    }
  }

  // --- 20. Module Inspection 360° & État des Lieux Numérique ---
  fun openInspectionModal() {
    _uiState.update { it.copy(showInspectionModal = true) }
  }

  fun closeInspectionModal() {
    _uiState.update { it.copy(showInspectionModal = false) }
  }

  fun saveVehicleInspection(inspection: VehicleInspection) {
    _uiState.update {
      it.copy(
        inspections = listOf(inspection) + it.inspections,
        showInspectionModal = false,
        toastMessage = "✅ État des lieux 360° enregistré (${inspection.inspectionNumber}) !"
      )
    }
  }

  // --- 21. Module Messagerie Client WhatsApp & SMS ---
  fun openClientMessagingModal(
    clientName: String = "Sofiane Benali",
    clientPhone: String = "0550 12 34 56",
    vehicleModel: String = "Peugeot 308 (1.6 BlueHDi)",
    plateNumber: String = "01429-122-16",
    amount: String = "18 500"
  ) {
    _uiState.update {
      it.copy(
        showMessageModal = true,
        prefilledMessageClient = ClientMessageDispatch(
          clientName = clientName,
          clientPhone = clientPhone,
          vehicleModel = vehicleModel,
          licensePlate = plateNumber,
          quoteOrInvoiceAmountDzd = amount
        )
      )
    }
  }

  fun closeClientMessagingModal() {
    _uiState.update { it.copy(showMessageModal = false) }
  }

  // --- 22. Module Scanner Code-Barres & Stock Magasin ---
  fun openBarcodeScannerModal() {
    _uiState.update { it.copy(showBarcodeScannerModal = true) }
  }

  fun closeBarcodeScannerModal() {
    _uiState.update { it.copy(showBarcodeScannerModal = false) }
  }

  fun updateBarcodeStock(itemId: String, delta: Int) {
    val movType = if (delta >= 0) StockMovementType.ENTREE_ACHAT else StockMovementType.SORTIE_ATELIER
    recordStockMovement(
      itemId = itemId,
      type = movType,
      quantity = kotlin.math.abs(delta).coerceAtLeast(1),
      destinationRef = if (delta >= 0) "BL-RECEP-RAPIDE" else "OR-ATELIER-DIRECT",
      operatorName = "Kamel M. (Chef Magasinier)"
    )
  }

  fun recordStockMovement(
    itemId: String,
    type: StockMovementType,
    quantity: Int,
    destinationRef: String,
    operatorName: String
  ) {
    _uiState.update { state ->
      val target = state.barcodeInventory.find { it.id == itemId } ?: return@update state
      val cleanQty = quantity.coerceAtLeast(1)
      val nextStock = when (type) {
        StockMovementType.ENTREE_ACHAT -> target.currentStock + cleanQty
        StockMovementType.SORTIE_ATELIER,
        StockMovementType.SORTIE_VENTE_COMPTOIR,
        StockMovementType.RETOUR_FOURNISSEUR -> (target.currentStock - cleanQty).coerceAtLeast(0)
        StockMovementType.AJUSTEMENT_INVENTAIRE -> cleanQty
      }

      val newLog = StockMovementLog(
        id = "MVT-${System.currentTimeMillis()}",
        itemId = target.id,
        partName = target.partName,
        oemReference = target.oemReference,
        barcode = target.barcode,
        type = type,
        quantity = cleanQty,
        previousStock = target.currentStock,
        newStock = nextStock,
        unitValueDzd = if (type == StockMovementType.ENTREE_ACHAT) target.unitPurchaseCostDzd else target.unitSalePriceDzd,
        dateFormatted = "À l'instant",
        operatorName = operatorName.ifBlank { "Kamel M. (Magasinier)" },
        destinationRef = destinationRef.ifBlank { "MVT-MANUEL-ERP" }
      )

      val updatedInventory = state.barcodeInventory.map { item ->
        if (item.id == itemId) {
          item.copy(
            currentStock = nextStock,
            lastRestockDate = if (type == StockMovementType.ENTREE_ACHAT) "Aujourd'hui" else item.lastRestockDate
          )
        } else item
      }

      state.copy(
        barcodeInventory = updatedInventory,
        stockMovementLogs = listOf(newLog) + state.stockMovementLogs,
        toastMessage = "📦 Mouvement ERP enregistré : ${type.label} (${target.oemReference} • Stock : ${target.currentStock} ➔ $nextStock)"
      )
    }
  }

  fun createNewInventoryItem(
    partName: String,
    oemRef: String,
    barcode: String,
    brand: String,
    category: String,
    initialStock: Int,
    minThreshold: Int,
    purchaseCostDzd: Int,
    salePriceDzd: Int,
    shelfLocation: String,
    supplierName: String,
    rotationClass: AbcRotationClass
  ) {
    val cleanPartName = partName.ifBlank { "Pièce de Rechange Certifiée" }
    val cleanOem = oemRef.ifBlank { "OEM-${(10000..99999).random()}" }
    val cleanBarcode = barcode.ifBlank { "613000${(1000000..9999999).random()}" }
    val newItem = BarcodeInventoryItem(
      id = "BC-${System.currentTimeMillis()}",
      barcode = cleanBarcode,
      oemReference = cleanOem,
      partName = cleanPartName,
      brand = brand.ifBlank { "BOSCH" }.uppercase(),
      category = category.ifBlank { "Freinage" },
      currentStock = initialStock.coerceAtLeast(0),
      minAlertThreshold = minThreshold.coerceAtLeast(1),
      maxTargetStock = (minThreshold * 3).coerceAtLeast(10),
      unitPurchaseCostDzd = purchaseCostDzd.coerceAtLeast(100),
      unitSalePriceDzd = salePriceDzd.coerceAtLeast(purchaseCostDzd),
      shelfLocation = shelfLocation.ifBlank { "Rayon B2 - Casier 05" },
      supplierName = supplierName.ifBlank { "Sarl Auto Pièces Alger" },
      compatibilityVehicles = listOf("${_uiState.value.activeVehicle.brand} ${_uiState.value.activeVehicle.model}"),
      lastRestockDate = "Aujourd'hui",
      rotationClass = rotationClass
    )

    val initLog = StockMovementLog(
      id = "MVT-INIT-${System.currentTimeMillis()}",
      itemId = newItem.id,
      partName = newItem.partName,
      oemReference = newItem.oemReference,
      barcode = newItem.barcode,
      type = StockMovementType.ENTREE_ACHAT,
      quantity = newItem.currentStock,
      previousStock = 0,
      newStock = newItem.currentStock,
      unitValueDzd = newItem.unitPurchaseCostDzd,
      dateFormatted = "À l'instant",
      operatorName = "Kamel M. (Chef Magasinier)",
      destinationRef = "CRÉATION FICHE ARTICLE ERP"
    )

    _uiState.update { state ->
      state.copy(
        barcodeInventory = listOf(newItem) + state.barcodeInventory,
        stockMovementLogs = listOf(initLog) + state.stockMovementLogs,
        toastMessage = "✅ Nouvelle fiche stock créée : ${newItem.partName} [${newItem.oemReference}] (${newItem.currentStock} u. en stock)"
      )
    }
  }

  fun updateInventoryItemPricingAndThresholds(
    itemId: String,
    newPurchaseCostDzd: Int,
    newSalePriceDzd: Int,
    newMinThreshold: Int,
    newShelfLocation: String
  ) {
    _uiState.update { state ->
      val updated = state.barcodeInventory.map { item ->
        if (item.id == itemId) {
          item.copy(
            unitPurchaseCostDzd = newPurchaseCostDzd.coerceAtLeast(100),
            unitSalePriceDzd = newSalePriceDzd.coerceAtLeast(100),
            minAlertThreshold = newMinThreshold.coerceAtLeast(1),
            shelfLocation = newShelfLocation.ifBlank { item.shelfLocation }
          )
        } else item
      }
      state.copy(
        barcodeInventory = updated,
        toastMessage = "⚙️ Paramètres article (Prix HT/TTC, Seuil Min & Emplacement) mis à jour !"
      )
    }
  }

  fun generateAutoRestockPurchaseOrder() {
    _uiState.update { state ->
      val lowStockItems = state.barcodeInventory.filter { it.isLowStock }
      if (lowStockItems.isEmpty()) {
        return@update state.copy(
          toastMessage = "✅ Aucun article en rupture ou sous le seuil d'alerte !"
        )
      }
      val orderLines = lowStockItems.map { item ->
        SupplierOrderLine(
          itemId = item.id,
          oemReference = item.oemReference,
          partName = item.partName,
          brand = item.brand,
          quantityOrdered = item.recommendedOrderQty,
          unitPurchaseCostDzd = item.unitPurchaseCostDzd
        )
      }
      val newOrder = SupplierPurchaseOrder(
        id = "PO-${System.currentTimeMillis()}",
        orderNumber = "BC-AUTO-2026-${(200..999).random()}",
        supplierName = lowStockItems.first().supplierName,
        createdAt = "Aujourd'hui",
        expectedDeliveryDate = "Demain 10h00 (Express 24h)",
        status = SupplierOrderStatus.ENVOYEE,
        lines = orderLines,
        notes = "Réapprovisionnement automatique des ${orderLines.size} références sous seuil critique"
      )
      state.copy(
        supplierPurchaseOrders = listOf(newOrder) + state.supplierPurchaseOrders,
        toastMessage = "🚚 Bon de commande ${newOrder.orderNumber} généré pour ${orderLines.size} réf. en alerte (${newOrder.totalOrderCostDzd} DA) !"
      )
    }
  }

  fun receiveSupplierPurchaseOrder(orderId: String) {
    _uiState.update { state ->
      val targetOrder = state.supplierPurchaseOrders.find { it.id == orderId } ?: return@update state
      if (targetOrder.status == SupplierOrderStatus.RECEPTIONNEE) {
        return@update state.copy(toastMessage = "ℹ️ Ce bon de commande a déjà été réceptionné en stock.")
      }

      val lineByItemId = targetOrder.lines.associateBy { it.itemId }
      val newLogs = mutableListOf<StockMovementLog>()

      val updatedInventory = state.barcodeInventory.map { item ->
        val matchingLine = lineByItemId[item.id]
        if (matchingLine != null) {
          val nextStock = item.currentStock + matchingLine.quantityOrdered
          newLogs.add(
            StockMovementLog(
              id = "MVT-RECEP-${System.currentTimeMillis()}-${item.id}",
              itemId = item.id,
              partName = item.partName,
              oemReference = item.oemReference,
              barcode = item.barcode,
              type = StockMovementType.ENTREE_ACHAT,
              quantity = matchingLine.quantityOrdered,
              previousStock = item.currentStock,
              newStock = nextStock,
              unitValueDzd = item.unitPurchaseCostDzd,
              dateFormatted = "À l'instant",
              operatorName = "Kamel M. (Réception Magasin)",
              destinationRef = "${targetOrder.orderNumber} • ${targetOrder.supplierName}"
            )
          )
          item.copy(
            currentStock = nextStock,
            lastRestockDate = "Aujourd'hui"
          )
        } else item
      }

      val updatedOrders = state.supplierPurchaseOrders.map { po ->
        if (po.id == orderId) po.copy(status = SupplierOrderStatus.RECEPTIONNEE) else po
      }

      state.copy(
        barcodeInventory = updatedInventory,
        stockMovementLogs = newLogs + state.stockMovementLogs,
        supplierPurchaseOrders = updatedOrders,
        toastMessage = "✅ Commande ${targetOrder.orderNumber} réceptionnée : +${targetOrder.totalUnitsCount} pièces ajoutées au stock !"
      )
    }
  }

  fun addBarcodeItemToCurrentQuote(item: BarcodeInventoryItem) {
    _uiState.update { state ->
      state.copy(
        toastMessage = "✅ Réf ${item.oemReference} (${item.brand}) ajoutée au devis !"
      )
    }
  }

  // --- SCAN BON D'ACHAT FOURNISSEUR (OCR IA) + DISPATCH RAYON/ÉTAGÈRE/BAC + VALIDATION PRIX + IMPRESSION ÉTIQUETTES ---

  fun updateScannedInvoiceLinePriceChoice(
    invoiceId: String,
    lineId: String,
    choice: SalePriceValidationChoice,
    customSalePriceDzd: Int
  ) {
    _uiState.update { state ->
      val updatedInvoices = state.scannedPurchaseInvoices.map { invoice ->
        if (invoice.id == invoiceId) {
          invoice.copy(
            lines = invoice.lines.map { line ->
              if (line.id == lineId) {
                line.copy(
                  priceChoice = choice,
                  customSalePriceDzd = customSalePriceDzd.coerceAtLeast(100)
                )
              } else line
            }
          )
        } else invoice
      }
      state.copy(scannedPurchaseInvoices = updatedInvoices)
    }
  }

  fun updateAllScannedInvoiceLinesPriceStrategy(
    invoiceId: String,
    keepSamePriceForAll: Boolean
  ) {
    _uiState.update { state ->
      val updatedInvoices = state.scannedPurchaseInvoices.map { invoice ->
        if (invoice.id == invoiceId) {
          invoice.copy(
            lines = invoice.lines.map { line ->
              if (keepSamePriceForAll) {
                line.copy(
                  priceChoice = SalePriceValidationChoice.KEEP_SAME_PRICE,
                  customSalePriceDzd = line.previousSalePriceDzd
                )
              } else {
                // Ajuste automatiquement avec +38% sur le coût d'achat scanné si changement choisi
                val suggestedPrice = ((line.scannedPurchaseCostDzd * 1.38) / 50.0).toInt() * 50
                line.copy(
                  priceChoice = SalePriceValidationChoice.CHANGE_SALE_PRICE,
                  customSalePriceDzd = suggestedPrice.coerceAtLeast(line.previousSalePriceDzd)
                )
              }
            }
          )
        } else invoice
      }
      state.copy(
        scannedPurchaseInvoices = updatedInvoices,
        toastMessage = if (keepSamePriceForAll) {
          "✅ Toutes les lignes configurées pour GARDER le même prix de vente."
        } else {
          "✏️ Prix de vente ajustés automatiquement selon le nouveau prix d'achat HT !"
        }
      )
    }
  }

  fun updateScannedInvoiceLinePlacementAndQty(
    invoiceId: String,
    lineId: String,
    rayon: String,
    etagere: String,
    bac: String,
    quantityReceived: Int
  ) {
    _uiState.update { state ->
      val updatedInvoices = state.scannedPurchaseInvoices.map { invoice ->
        if (invoice.id == invoiceId) {
          invoice.copy(
            lines = invoice.lines.map { line ->
              if (line.id == lineId) {
                line.copy(
                  assignedRayon = rayon.ifBlank { line.assignedRayon },
                  assignedEtagere = etagere.ifBlank { line.assignedEtagere },
                  assignedBac = bac.ifBlank { line.assignedBac },
                  quantityReceived = quantityReceived.coerceAtLeast(1)
                )
              } else line
            }
          )
        } else invoice
      }
      state.copy(
        scannedPurchaseInvoices = updatedInvoices,
        toastMessage = "📍 Affectation Rayon / Étagère / Bac et quantité mises à jour !"
      )
    }
  }

  fun addLineToScannedPurchaseInvoice(
    invoiceId: String,
    partName: String,
    oemRef: String,
    brand: String,
    category: String,
    quantity: Int,
    purchaseCostDzd: Int,
    salePriceDzd: Int,
    rayon: String,
    etagere: String,
    bac: String
  ) {
    _uiState.update { state ->
      val cleanOem = oemRef.ifBlank { "OEM-${(1000..9999).random()}" }
      val matchedExisting = state.barcodeInventory.find {
        it.oemReference.equals(cleanOem, ignoreCase = true)
      }
      val newLine = ScannedPurchaseInvoiceLine(
        id = "LINE-${System.currentTimeMillis()}",
        matchedInventoryItemId = matchedExisting?.id,
        oemReference = cleanOem,
        barcode = matchedExisting?.barcode ?: "613000${(1000000..9999999).random()}",
        partName = partName.ifBlank { matchedExisting?.partName ?: "Pièce Détachée Réceptionnée" },
        brand = brand.ifBlank { matchedExisting?.brand ?: "BOSCH" }.uppercase(),
        category = category.ifBlank { matchedExisting?.category ?: "Freinage" },
        quantityReceived = quantity.coerceAtLeast(1),
        previousStock = matchedExisting?.currentStock ?: 0,
        previousPurchaseCostDzd = matchedExisting?.unitPurchaseCostDzd ?: purchaseCostDzd.coerceAtLeast(100),
        scannedPurchaseCostDzd = purchaseCostDzd.coerceAtLeast(100),
        previousSalePriceDzd = matchedExisting?.unitSalePriceDzd ?: salePriceDzd.coerceAtLeast(purchaseCostDzd),
        priceChoice = SalePriceValidationChoice.KEEP_SAME_PRICE,
        customSalePriceDzd = salePriceDzd.coerceAtLeast(purchaseCostDzd),
        assignedRayon = rayon.ifBlank { "Rayon A2" },
        assignedEtagere = etagere.ifBlank { "Étagère E2" },
        assignedBac = bac.ifBlank { "Bac B05" }
      )
      val updatedInvoices = state.scannedPurchaseInvoices.map { invoice ->
        if (invoice.id == invoiceId) {
          invoice.copy(
            isIntegrated = false,
            lines = invoice.lines + newLine
          )
        } else invoice
      }
      state.copy(
        scannedPurchaseInvoices = updatedInvoices,
        toastMessage = "➕ Référence ${newLine.oemReference} ajoutée au Bon d'Achat (${newLine.formattedFullLocation}) !"
      )
    }
  }

  fun integrateScannedInvoiceAndGenerateLabels(invoiceId: String) {
    _uiState.update { state ->
      val targetInvoice = state.scannedPurchaseInvoices.find { it.id == invoiceId } ?: return@update state
      val currentInventory = state.barcodeInventory.toMutableList()
      val newMovements = mutableListOf<StockMovementLog>()
      val generatedLabels = mutableListOf<PrintedThermalLabel>()

      targetInvoice.lines.forEach { line ->
        val finalSalePrice = line.finalValidatedSalePriceDzd
        val formattedShelf = line.formattedFullLocation
        val existingIndex = currentInventory.indexOfFirst {
          it.id == line.matchedInventoryItemId || it.oemReference.equals(line.oemReference, ignoreCase = true)
        }

        if (existingIndex >= 0) {
          val existing = currentInventory[existingIndex]
          val updatedStock = existing.currentStock + line.quantityReceived
          currentInventory[existingIndex] = existing.copy(
            currentStock = updatedStock,
            unitPurchaseCostDzd = line.scannedPurchaseCostDzd,
            unitSalePriceDzd = finalSalePrice,
            shelfLocation = formattedShelf,
            lastRestockDate = "Aujourd'hui (${targetInvoice.invoiceNumber})"
          )
          newMovements.add(
            StockMovementLog(
              id = "MVT-SCAN-${System.currentTimeMillis()}-${line.id}",
              itemId = existing.id,
              partName = existing.partName,
              oemReference = existing.oemReference,
              barcode = existing.barcode,
              type = StockMovementType.ENTREE_ACHAT,
              quantity = line.quantityReceived,
              previousStock = existing.currentStock,
              newStock = updatedStock,
              unitValueDzd = line.scannedPurchaseCostDzd,
              dateFormatted = "À l'instant (Scan Bon d'Achat)",
              operatorName = "Kamel M. (Scan OCR Magasin)",
              destinationRef = "${targetInvoice.invoiceNumber} • ${formattedShelf}"
            )
          )
        } else {
          val createdItem = BarcodeInventoryItem(
            id = "BC-SCAN-${System.currentTimeMillis()}-${line.id.takeLast(4)}",
            barcode = line.barcode,
            oemReference = line.oemReference,
            partName = line.partName,
            brand = line.brand,
            category = line.category,
            currentStock = line.quantityReceived,
            minAlertThreshold = 3,
            maxTargetStock = (line.quantityReceived * 2).coerceAtLeast(10),
            unitPurchaseCostDzd = line.scannedPurchaseCostDzd,
            unitSalePriceDzd = finalSalePrice,
            shelfLocation = formattedShelf,
            supplierName = targetInvoice.supplierName,
            compatibilityVehicles = listOf("${state.activeVehicle.brand} ${state.activeVehicle.model}"),
            lastRestockDate = "Aujourd'hui (${targetInvoice.invoiceNumber})",
            rotationClass = AbcRotationClass.CLASS_A
          )
          currentInventory.add(0, createdItem)
          newMovements.add(
            StockMovementLog(
              id = "MVT-SCAN-NEW-${System.currentTimeMillis()}-${line.id}",
              itemId = createdItem.id,
              partName = createdItem.partName,
              oemReference = createdItem.oemReference,
              barcode = createdItem.barcode,
              type = StockMovementType.ENTREE_ACHAT,
              quantity = line.quantityReceived,
              previousStock = 0,
              newStock = line.quantityReceived,
              unitValueDzd = line.scannedPurchaseCostDzd,
              dateFormatted = "À l'instant (Scan Bon d'Achat)",
              operatorName = "Kamel M. (Scan OCR Magasin)",
              destinationRef = "${targetInvoice.invoiceNumber} • ${formattedShelf}"
            )
          )
        }

        generatedLabels.add(
          PrintedThermalLabel(
            id = "LBL-${targetInvoice.invoiceNumber}-${line.id}",
            invoiceNumber = targetInvoice.invoiceNumber,
            oemReference = line.oemReference,
            barcode = line.barcode,
            partName = line.partName,
            brand = line.brand,
            category = line.category,
            rayon = line.assignedRayon,
            etagere = line.assignedEtagere,
            bac = line.assignedBac,
            validatedSalePriceDzd = finalSalePrice,
            copiesToPrint = line.quantityReceived,
            isPrinted = false,
            printedTimestamp = "En attente d'impression thermique"
          )
        )
      }

      val updatedInvoices = state.scannedPurchaseInvoices.map { inv ->
        if (inv.id == invoiceId) inv.copy(isIntegrated = true) else inv
      }

      val remainingLabels = state.printedThermalLabels.filterNot { existing ->
        generatedLabels.any { it.id == existing.id }
      }

      state.copy(
        barcodeInventory = currentInventory,
        stockMovementLogs = newMovements + state.stockMovementLogs,
        scannedPurchaseInvoices = updatedInvoices,
        printedThermalLabels = generatedLabels + remainingLabels,
        toastMessage = "✅ Bon ${targetInvoice.invoiceNumber} intégré : +${targetInvoice.totalUnitsCount} pièces rangées dans leurs Rayons/Étagères/Bacs & ${generatedLabels.size} séries d'étiquettes générées !"
      )
    }
  }

  fun printSingleThermalLabel(labelId: String) {
    _uiState.update { state ->
      val target = state.printedThermalLabels.find { it.id == labelId } ?: return@update state
      val updatedLabels = state.printedThermalLabels.map { lbl ->
        if (lbl.id == labelId) {
          lbl.copy(
            isPrinted = true,
            printedTimestamp = "Imprimé à l'instant • ${lbl.copiesToPrint} étiquettes (Zebra ZD421)"
          )
        } else lbl
      }
      state.copy(
        printedThermalLabels = updatedLabels,
        toastMessage = "🖨️ Impression thermique lancée : ${target.copiesToPrint} étiquette(s) [${target.oemReference} • ${target.fullShelfCode} • ${target.validatedSalePriceDzd} DA] !"
      )
    }
  }

  fun printAllThermalLabelsForInvoice(invoiceNumber: String) {
    _uiState.update { state ->
      var totalCopiesPrinted = 0
      val updatedLabels = state.printedThermalLabels.map { lbl ->
        if (lbl.invoiceNumber == invoiceNumber || invoiceNumber == "ALL") {
          totalCopiesPrinted += lbl.copiesToPrint
          lbl.copy(
            isPrinted = true,
            printedTimestamp = "Imprimé à l'instant • ${lbl.copiesToPrint} étiquettes (Zebra ZD421)"
          )
        } else lbl
      }
      state.copy(
        printedThermalLabels = updatedLabels,
        toastMessage = "🖨️ $totalCopiesPrinted étiquettes thermiques (Code-barres + Rayon/Étagère/Bac + Prix TTC) imprimées avec succès !"
      )
    }
  }

  // --- 23. Module Barème Temps Constructeur OEM ---
  fun openLaborStandardModal() {
    _uiState.update { it.copy(showLaborStandardModal = true) }
  }

  fun closeLaborStandardModal() {
    _uiState.update { it.copy(showLaborStandardModal = false) }
  }

  fun applyLaborStandardOperationsToQuote(ops: List<LaborStandardOperation>, totalCostDzd: Int) {
    _uiState.update { state ->
      val hoursFormatted = String.format("%.2f", ops.sumOf { it.standardHours })
      state.copy(
        showLaborStandardModal = false,
        toastMessage = "✅ ${ops.size} opération(s) constructeur insérée(s) : $hoursFormatted h • $totalCostDzd DZD"
      )
    }
  }

  // --- 24. Bascule de Rôle Compte & Séparation Stricte des Options par Formule ---
  fun switchAccountRole(role: AccountRole) {
    // Purge all standalone StateFlow modals from previous account
    _isPartLabelVisionScannerOpen.value = false
    _isOfflineCacheHubModalOpen.value = false
    _isSmartCartFreeAccessMapModalOpen.value = false
    _isClientBugAiRepairModalOpen.value = false

    _uiState.update { state ->
      state.copy(
        currentAccountRole = role,
        currentNavTab = MainNavigationTab.ACCUEIL,
        userPlanName = role.formulaName,
        // Purge all modals and active dialogs from the previous account
        showNewQuoteDialog = false,
        showBookAppointmentDialog = false,
        showNotificationModal = false,
        showSubscriptionModal = false,
        showVinReportModal = false,
        showCameraScanModal = false,
        showCarteGriseScannerModal = false,
        showPassportModal = false,
        showSosModal = false,
        showInvoiceModal = false,
        showPaymentModal = false,
        showGroupQuoteModal = false,
        showDriverMethodModal = false,
        showDriverInvoiceModal = false,
        showRentalVehicleModal = false,
        showRentalContractModal = false,
        showNewRentalContractModal = false,
        showAddRentalCarModal = false,
        showUniversalQrScannerModal = false,
        showUniversalQrPassportDetailModal = false,
        showGenerateVehicleQrModal = false,
        showPdfDownloadModal = false,
        showEngineSoundRecorderModal = false,
        showWorkshopClientPortalModal = false,
        showCreateClientAccountModal = false,
        showVoiceDictationModal = false,
        showUltraProManagerModal = false,
        showCreateSubAccountModal = false,
        showWinMotorServiceBoxModal = false,
        showUnifiedAutoApiHubModal = false,
        showSecurityShieldModal = false,
        showGeminiChatbotModal = false,
        showBayPlannerModal = false,
        showBarcodeScannerModal = false,
        showTcoCalculatorModal = false,
        showAccidentReportModal = false,
        showWiringFuseEcuModal = false,
        showAdasEvBatteryModal = false,
        showTechnicalBulletinsModal = false,
        showCertifiedUsedPartsModal = false,
        showAcRefrigerantModal = false,
        showShortTermRentalModal = false,
        showWebOemCatalogsModal = false,
        showPartCompatibilityModal = false,
        showWorkshopProfitabilityModal = false,
        showPreCtCourtesyModal = false,
        activeRepairOrder = null,
        toastMessage = "Compte basculé : ${role.label} • Options précédentes supprimées, seules les options de votre formule sont affichées."
      )
    }
  }

  // --- 25. Module Planning Visuel des Ponts & Charge Atelier ---
  fun openBayPlannerModal() {
    _uiState.update { it.copy(showBayPlannerModal = true) }
  }

  fun closeBayPlannerModal() {
    _uiState.update { it.copy(showBayPlannerModal = false) }
  }

  fun toggleBayTimer(bayId: String) {
    _uiState.update { state ->
      val updated = state.workshopBays.map { bay ->
        if (bay.id == bayId) {
          val running = !bay.isTimerRunning
          bay.copy(
            isTimerRunning = running,
            elapsedMinutes = if (running) bay.elapsedMinutes + 15 else bay.elapsedMinutes,
            status = if (running) WorkshopBayStatus.EN_COURS else bay.status
          )
        } else bay
      }
      state.copy(
        workshopBays = updated,
        toastMessage = "⏱️ Pointage chrono mis à jour sur le pont"
      )
    }
  }

  fun completeBayOperation(bayId: String) {
    _uiState.update { state ->
      val updated = state.workshopBays.map { bay ->
        if (bay.id == bayId) {
          bay.copy(
            status = WorkshopBayStatus.LIBRE,
            currentVehicleModel = null,
            currentLicensePlate = null,
            repairOrderNumber = null,
            operationTitle = null,
            elapsedMinutes = 0,
            isTimerRunning = false
          )
        } else bay
      }
      state.copy(
        workshopBays = updated,
        toastMessage = "✅ Intervention terminée ! Le pont élévateur est maintenant libre."
      )
    }
  }

  fun assignVehicleToBay(bayId: String, waitingVehicleId: String) {
    _uiState.update { state ->
      val waitVeh = state.waitingQueueVehicles.find { it.id == waitingVehicleId } ?: return@update state
      val updatedBays = state.workshopBays.map { bay ->
        if (bay.id == bayId) {
          bay.copy(
            status = WorkshopBayStatus.EN_COURS,
            currentVehicleModel = waitVeh.vehicleModel,
            currentLicensePlate = waitVeh.licensePlate,
            repairOrderNumber = waitVeh.repairOrderNumber,
            operationTitle = waitVeh.operationTitle,
            elapsedMinutes = 5,
            estimatedMinutes = waitVeh.estimatedMinutes,
            isTimerRunning = true
          )
        } else bay
      }
      state.copy(
        workshopBays = updatedBays,
        waitingQueueVehicles = state.waitingQueueVehicles.filterNot { it.id == waitingVehicleId },
        toastMessage = "🔧 ${waitVeh.vehicleModel} affecté au pont avec démarrage du chrono !"
      )
    }
  }

  // --- 26. Module Suivi des Garanties Pièces & Retours SAV (RMA) ---
  fun openWarrantyRmaModal() {
    _uiState.update { it.copy(showWarrantyRmaModal = true) }
  }

  fun closeWarrantyRmaModal() {
    _uiState.update { it.copy(showWarrantyRmaModal = false) }
  }

  fun createRmaClaim(warrantyId: String, reason: String) {
    _uiState.update { state ->
      val rmaCode = "RMA-SAV-2026-${(100..999).random()}"
      val updated = state.partWarranties.map { w ->
        if (w.id == warrantyId) {
          w.copy(
            status = WarrantyStatus.RMA_EN_COURS,
            rmaClaimReason = reason,
            rmaTrackingCode = rmaCode
          )
        } else w
      }
      state.copy(
        partWarranties = updated,
        toastMessage = "📦 Bon de retour garantie $rmaCode transmis au fournisseur !"
      )
    }
  }

  fun registerNewPartWarranty(partName: String, oemRef: String, brand: String, vehiclePlate: String) {
    _uiState.update { state ->
      val newWar = PartWarrantyRecord(
        id = "WAR-${System.currentTimeMillis()}",
        warrantyNumber = "GAR-2026-${(1000..9999).random()}",
        partName = partName,
        oemReference = oemRef,
        brand = brand.uppercase(),
        supplierName = "Sarl Auto Pièces Alger",
        vehicleModel = "Véhicule Client ($vehiclePlate)",
        licensePlate = vehiclePlate,
        clientName = "Client Atelier Certifié",
        installationDate = "17/10/2026",
        expirationDate = "17/10/2027",
        durationMonths = 12,
        daysRemaining = 365,
        mileageAtInstallKm = 90000,
        maxWarrantyMileageKm = 130000,
        purchasePriceDzd = 19500,
        status = WarrantyStatus.ACTIVE
      )
      state.copy(
        partWarranties = listOf(newWar) + state.partWarranties,
        toastMessage = "🛡️ Certificat de garantie 12 mois (${newWar.warrantyNumber}) activé !"
      )
    }
  }

  // --- 27. Module Portail Client Partageable & Signature Devis à Distance ---
  fun openRemoteQuotePortalModal() {
    _uiState.update { it.copy(showRemoteQuotePortalModal = true) }
  }

  fun closeRemoteQuotePortalModal() {
    _uiState.update { it.copy(showRemoteQuotePortalModal = false) }
  }

  fun toggleRemoteOptionalQuoteItem(itemId: String) {
    _uiState.update { state ->
      val updatedItems = state.remoteClientQuoteSession.optionalItems.map { item ->
        if (item.id == itemId) item.copy(isSelectedByClient = !item.isSelectedByClient) else item
      }
      state.copy(
        remoteClientQuoteSession = state.remoteClientQuoteSession.copy(optionalItems = updatedItems)
      )
    }
  }

  fun approveAndSignRemoteQuote() {
    _uiState.update { state ->
      val updatedSession = state.remoteClientQuoteSession.copy(
        isSignedRemotely = true,
        signatureTimestamp = "17/10/2026 à 11:45 (IP & OTP Vérifiés)",
        currentStepIndex = 3
      )
      state.copy(
        remoteClientQuoteSession = updatedSession,
        toastMessage = "✍️ Devis approuvé et signé à distance (${updatedSession.finalApprovedTotalDzd} DZD) !"
      )
    }
  }

  fun advanceRemoteQuoteLiveStep() {
    _uiState.update { state ->
      val nextStep = (state.remoteClientQuoteSession.currentStepIndex + 1) % 5
      state.copy(
        remoteClientQuoteSession = state.remoteClientQuoteSession.copy(currentStepIndex = nextStep),
        toastMessage = "📲 Statut de suivi client mis à jour en temps réel"
      )
    }
  }

  // --- 28. Module Mode Hors-Ligne Intelligent & Synchronisation Auto ---
  fun openOfflineSyncModal() {
    _uiState.update { it.copy(showOfflineSyncModal = true) }
  }

  fun closeOfflineSyncModal() {
    _uiState.update { it.copy(showOfflineSyncModal = false) }
  }

  fun toggleOfflineMode(enabled: Boolean) {
    _uiState.update { state ->
      state.copy(
        isOfflineModeEnabled = enabled,
        toastMessage = if (enabled) "📴 Mode Hors-Ligne Sous-Sol activé : stockage local sécurisé" else "☁️ Connexion Cloud rétablie : synchronisation active"
      )
    }
  }

  fun syncAllPendingOfflineActions() {
    _uiState.update { state ->
      val syncedList = state.pendingSyncActions.map { it.copy(isSynced = true) }
      state.copy(
        pendingSyncActions = syncedList,
        isOfflineModeEnabled = false,
        toastMessage = "✅ Toutes les opérations hors-ligne ont été synchronisées avec le Cloud !"
      )
    }
  }

  fun downloadOfflinePack(packId: String) {
    _uiState.update { state ->
      val updatedPacks = state.offlineCachedPacks.map { pack ->
        if (pack.id == packId) pack.copy(isDownloaded = true, lastUpdated = "À l'instant") else pack
      }
      state.copy(
        offlineCachedPacks = updatedPacks,
        toastMessage = "📥 Pack technique téléchargé pour consultation sans réseau !"
      )
    }
  }

  // --- 29. Module Comparateur Multi-Fournisseurs B2B & Suivi Livreur ---
  fun openB2bComparatorModal() {
    _uiState.update { it.copy(showB2bComparatorModal = true) }
  }

  fun closeB2bComparatorModal() {
    _uiState.update { it.copy(showB2bComparatorModal = false) }
  }

  fun orderPartFromSupplierB2b(offer: SupplierComparisonOffer) {
    _uiState.update { state ->
      val newOrder = ActiveB2bOrderTracking(
        orderId = "CMD-B2B-${(800..999).random()}",
        supplierName = offer.supplierName,
        courierName = "Mourad (Coursier Express Moto)",
        courierPhone = "0558 12 49 00",
        itemsCount = 1,
        totalHtDzd = offer.wholesalePriceHtDzd,
        etaMinutes = 35,
        status = B2bDeliveryStatus.COURSIER_EN_ROUTE
      )
      state.copy(
        activeB2bOrders = listOf(newOrder) + state.activeB2bOrders,
        toastMessage = "🛵 Commande B2B lancée chez ${offer.supplierName} (${offer.wholesalePriceHtDzd} DA HT) !"
      )
    }
  }

  fun advanceB2bOrderDelivery(orderId: String) {
    _uiState.update { state ->
      val updated = state.activeB2bOrders.map { ord ->
        if (ord.idOrOrderMatch(orderId)) {
          ord.copy(status = B2bDeliveryStatus.LIVRE_ATELIER, etaMinutes = 0)
        } else ord
      }
      state.copy(
        activeB2bOrders = updated,
        toastMessage = "📦 Livraison B2B réceptionnée et entrée en stock atelier !"
      )
    }
  }

  private fun ActiveB2bOrderTracking.idOrOrderMatch(id: String): Boolean = this.orderId == id

  // --- 30. Module Calculateur Coût de Revient Kilométrique (TCO) & Carburant ---
  fun openTcoCalculatorModal() {
    _uiState.update { it.copy(showTcoCalculatorModal = true) }
  }

  fun closeTcoCalculatorModal() {
    _uiState.update { it.copy(showTcoCalculatorModal = false) }
  }

  fun addVehicleExpense(category: VehicleExpenseCategory, title: String, amountDzd: Int, mileageKm: Int) {
    _uiState.update { state ->
      val newExp = VehicleExpenseEntry(
        id = "EXP-${System.currentTimeMillis()}",
        vehicleId = "TCO-01",
        vehicleLabel = "VW Golf 7 2.0 TDI",
        category = category,
        title = title,
        date = "17 Oct 2026",
        mileageKm = mileageKm,
        amountDzd = amountDzd
      )
      val updatedSummaries = state.vehicleTcoSummaries.mapIndexed { index, summary ->
        if (index == 0) {
          when (category) {
            VehicleExpenseCategory.CARBURANT -> summary.copy(totalFuelCostDzd = summary.totalFuelCostDzd + amountDzd)
            VehicleExpenseCategory.ENTRETIEN_PIECES -> summary.copy(totalMaintenanceCostDzd = summary.totalMaintenanceCostDzd + amountDzd)
            VehicleExpenseCategory.PNEUMATIQUES -> summary.copy(totalTiresCostDzd = summary.totalTiresCostDzd + amountDzd)
            else -> summary.copy(totalInsuranceCostDzd = summary.totalInsuranceCostDzd + amountDzd)
          }
        } else summary
      }
      state.copy(
        vehicleExpenses = listOf(newExp) + state.vehicleExpenses,
        vehicleTcoSummaries = updatedSummaries,
        toastMessage = "⛽ Dépense de $amountDzd DA ajoutée • TCO kilométrique recalculé !"
      )
    }
  }

  // --- 31. Module Constat Amiable Digital & Déclaration Sinistre Guidée ---
  fun openAccidentReportModal() {
    _uiState.update { it.copy(showAccidentReportModal = true) }
  }

  fun closeAccidentReportModal() {
    _uiState.update { it.copy(showAccidentReportModal = false) }
  }

  fun submitDigitalAccidentReport(
    driverBName: String,
    driverBVehicle: String,
    driverBInsurance: String,
    circumstances: List<AccidentCircumstance>,
    impactZone: String
  ) {
    _uiState.update { state ->
      val newReport = DigitalAccidentReport(
        id = "SIN-${System.currentTimeMillis()}",
        reportNumber = "CONSTAT-2026-${(1000..9999).random()}",
        dateTimeFormatted = "17 Oct 2026 à 11:50",
        gpsLocation = "Autoroute Est-Ouest - Sortie Dar El Beïda, Alger",
        driverAVehicle = state.activeVehicle.brand + " " + state.activeVehicle.model,
        driverAPlate = "01429-122-16",
        driverAInsurance = "SAA Assurance Algérie",
        driverAPolicyNumber = "POL-SAA-9981204",
        driverBName = driverBName,
        driverBPhone = "0550 88 77 66",
        driverBVehicle = driverBVehicle,
        driverBPlate = "04129-119-16",
        driverBInsurance = driverBInsurance,
        driverBPolicyNumber = "CAAR-884120",
        selectedCircumstancesA = emptyList(),
        selectedCircumstancesB = circumstances,
        impactZoneDescription = impactZone,
        attachedPhotosCount = 4,
        status = "TRANSMIS_ASSURANCE_ET_ATELIER",
        isSignedByBothParties = true
      )
      state.copy(
        accidentReports = listOf(newReport) + state.accidentReports,
        toastMessage = "🛡️ Dossier Constat ${newReport.reportNumber} transmis à l'assurance et à l'atelier !"
      )
    }
  }

  // --- 32. Module Schémas Électriques, Fusibles & Pinout ECU ---
  fun openWiringFuseEcuModal() {
    _uiState.update { it.copy(showWiringFuseEcuModal = true) }
  }

  fun closeWiringFuseEcuModal() {
    _uiState.update { it.copy(showWiringFuseEcuModal = false) }
  }

  fun toggleFuseIntactStatus(fuseId: String) {
    _uiState.update { state ->
      val updated = state.fuseBoxItems.map { fuse ->
        if (fuse.id == fuseId) {
          val nowIntact = !fuse.isIntact
          fuse.copy(isIntact = nowIntact, nominalVoltageV = if (nowIntact) 12.6 else 0.0)
        } else fuse
      }
      state.copy(
        fuseBoxItems = updated,
        toastMessage = "⚡ État du fusible mis à jour (Test multimètre 12.6V vérifié)"
      )
    }
  }

  // --- 33. Module Calibrage ADAS & Santé Batterie EV / Hybride (SoH) ---
  fun openAdasEvBatteryModal() {
    _uiState.update { it.copy(showAdasEvBatteryModal = true) }
  }

  fun closeAdasEvBatteryModal() {
    _uiState.update { it.copy(showAdasEvBatteryModal = false) }
  }

  fun runAdasCalibration(adasId: String) {
    _uiState.update { state ->
      val updated = state.adasCalibrations.map { item ->
        if (item.id == adasId) {
          item.copy(isCalibrated = true, angleDeviationAfterDeg = 0.02)
        } else item
      }
      state.copy(
        adasCalibrations = updated,
        toastMessage = "🎯 Étalonnage optique/laser ADAS terminé avec succès (Écart 0.02° conforme OEM) !"
      )
    }
  }

  fun toggleEvHighVoltageLockout(reportId: String) {
    _uiState.update { state ->
      val updated = state.evBatteryReports.map { rep ->
        if (rep.id == reportId) {
          rep.copy(highVoltageLockoutActive = !rep.highVoltageLockoutActive)
        } else rep
      }
      state.copy(
        evBatteryReports = updated,
        toastMessage = "⚡ Statut de consignation Haute Tension (Service Plug 400V) mis à jour !"
      )
    }
  }

  // --- 34. Module Bulletins de Service Technique (TSB) & Pannes Connues ---
  fun openTechnicalBulletinsModal() {
    _uiState.update { it.copy(showTechnicalBulletinsModal = true) }
  }

  fun closeTechnicalBulletinsModal() {
    _uiState.update { it.copy(showTechnicalBulletinsModal = false) }
  }

  fun orderModifiedPartFromTsb(tsb: TechnicalServiceBulletin) {
    _uiState.update { state ->
      state.copy(
        showTechnicalBulletinsModal = false,
        showB2bComparatorModal = true,
        toastMessage = "🔍 Recherche B2B ouverte pour la référence modifiée : ${tsb.modifiedPartReference}"
      )
    }
  }

  // --- 35. Module Bourse Pièces d'Occasion Certifiées & Consignes Échange Standard ---
  fun openCertifiedUsedPartsModal() {
    _uiState.update { it.copy(showCertifiedUsedPartsModal = true) }
  }

  fun closeCertifiedUsedPartsModal() {
    _uiState.update { it.copy(showCertifiedUsedPartsModal = false) }
  }

  fun reserveCertifiedUsedPart(partId: String) {
    _uiState.update { state ->
      val updated = state.certifiedUsedParts.map { p ->
        if (p.id == partId) p.copy(isReserved = true) else p
      }
      state.copy(
        certifiedUsedParts = updated,
        toastMessage = "♻️ Pièce d'occasion certifiée réservée auprès du centre VHU agréé !"
      )
    }
  }

  fun advanceCoreReturnStatus(coreId: String) {
    _uiState.update { state ->
      val updated = state.coreReturnConsignes.map { c ->
        if (c.id == coreId) {
          val nextStatus = when (c.status) {
            CoreReturnStatus.A_RESTITUER_FOURNISSEUR -> CoreReturnStatus.EXPEDIE_EN_CONTROLE
            CoreReturnStatus.EXPEDIE_EN_CONTROLE -> CoreReturnStatus.CONSIGNE_REMBOURSEE
            CoreReturnStatus.CONSIGNE_REMBOURSEE -> CoreReturnStatus.CONSIGNE_REMBOURSEE
          }
          c.copy(status = nextStatus)
        } else c
      }
      state.copy(
        coreReturnConsignes = updated,
        toastMessage = "📦 Statut de la consigne vieille matière mis à jour !"
      )
    }
  }

  // --- 36. Module Registre Climatisation A/C (R134a / R1234yf) ---
  fun openAcRefrigerantModal() {
    _uiState.update { it.copy(showAcRefrigerantModal = true) }
  }

  fun closeAcRefrigerantModal() {
    _uiState.update { it.copy(showAcRefrigerantModal = false) }
  }

  fun logAcChargeFromSpec(spec: AcVehicleSpecification, plate: String, recoveredGrams: Int, ventTempC: Double) {
    _uiState.update { state ->
      val newLog = AcServiceChargeRecord(
        id = "AC-LOG-${System.currentTimeMillis()}",
        dateFormatted = "17 Oct 2026 • À l'instant",
        vehicleModel = spec.vehicleModel,
        licensePlate = plate.ifBlank { "01928-123-16" },
        gasType = spec.gasType,
        recoveredGrams = recoveredGrams,
        injectedGrams = spec.targetGasWeightGrams,
        oilInjectedMl = 15,
        uvTracerAdded = true,
        vacuumTestPassed = true,
        ventOutputTempCelsius = ventTempC
      )
      state.copy(
        acServiceLogs = listOf(newLog) + state.acServiceLogs,
        toastMessage = "❄️ Recharge A/C (${spec.targetGasWeightGrams}g ${spec.gasType.code}) enregistrée dans le registre !"
      )
    }
  }

  // --- 37. Module État des Lieux Photo Location, Caution & Contrats Court Terme ---
  fun openShortTermRentalModal() {
    _uiState.update { it.copy(showShortTermRentalModal = true) }
  }

  fun closeShortTermRentalModal() {
    _uiState.update { it.copy(showShortTermRentalModal = false) }
  }

  fun updateRentalReturnMetrics(contractId: String, returnKm: Int, returnFuelEighths: Int) {
    _uiState.update { state ->
      val updated = state.shortTermRentalContracts.map { c ->
        if (c.id == contractId) {
          c.copy(returnKm = returnKm, returnFuelEighths = returnFuelEighths)
        } else c
      }
      state.copy(
        shortTermRentalContracts = updated,
        toastMessage = "⛽ Relevé de retour location mis à jour (Jauge carburant 8/8) !"
      )
    }
  }

  fun closeRentalContractAndReleaseDeposit(contractId: String) {
    _uiState.update { state ->
      val updated = state.shortTermRentalContracts.map { c ->
        if (c.id == contractId) {
          c.copy(isClosedAndSigned = true, depositStatus = RentalDepositStatus.CAUTION_LIBEREE)
        } else c
      }
      state.copy(
        shortTermRentalContracts = updated,
        toastMessage = "✅ Contrat clôturé et caution locataire libérée !"
      )
    }
  }

  // --- Modules mis de côté pour activation ultérieure (Gardiennage Pneus & Simulation Reprise) ---
  fun notifyStandbyModuleReserved(moduleName: String) {
    _uiState.update {
      it.copy(
        toastMessage = "🔒 Module « $moduleName » en réserve (désactivé temporairement comme l'immatriculation — réactivable à tout moment)."
      )
    }
  }

  // --- 38. Catalogues Web OEM Gratuits & Vues Éclatées (CatCar.info, 7Zap, PartsSouq, NemigaParts, IFInterface, WebAutoCats) ---
  fun openWebOemCatalogsModal() {
    _uiState.update { it.copy(showWebOemCatalogsModal = true) }
  }

  fun closeWebOemCatalogsModal() {
    _uiState.update { it.copy(showWebOemCatalogsModal = false) }
  }

  fun selectExplodedCalloutForQuote(callout: ExplodedCalloutItem, sheet: OemExplodedDiagramSheet) {
    _uiState.update { state ->
      state.copy(
        showWebOemCatalogsModal = false,
        showB2bComparatorModal = true,
        toastMessage = "📐 Repère [${callout.calloutNumber}] (${callout.oemReference}) extrait de ${sheet.platformSource} vers le comparateur B2B !"
      )
    }
  }

  // --- 39. Vérificateur Anti-Erreur Montage (Codes PR VAG, N° OPR PSA, Indices Boîte/Moteur Renault) ---
  fun openPartCompatibilityModal() {
    _uiState.update { it.copy(showPartCompatibilityModal = true) }
  }

  fun closePartCompatibilityModal() {
    _uiState.update { it.copy(showPartCompatibilityModal = false) }
  }

  fun addCustomFitmentVerification(code: String, vehicle: String) {
    _uiState.update { state ->
      val newRule = FitmentVerificationRule(
        id = "FIT-CUST-${System.currentTimeMillis()}",
        systemType = FitmentCodeSystemType.PR_CODE_VAG,
        vehicleModel = vehicle,
        codeEntered = code.uppercase(),
        stickerLocationHint = "Vérifié via décodeur VIN / étiquette constructeur",
        partCategory = "Montage Certifié Sans Erreur",
        exactMountedSpec = "Spécification OEM verrouillée pour le code ${code.uppercase()} (100% compatible)",
        incompatibleTrapSpec = "⚠️ Variantes alternatives exclues automatiquement par filtre PR/OPR/Indice",
        verifiedOemRef = "OEM-${code.uppercase().replace(" ", "")}-CERT"
      )
      state.copy(
        fitmentVerificationRules = listOf(newRule) + state.fitmentVerificationRules,
        toastMessage = "✅ Code montage « ${code.uppercase()} » vérifié et verrouillé sans risque d'erreur !"
      )
    }
  }

  // --- 40. Rentabilité par OR, Productivité Mécaniciens & Fûts d'Huile Vrac 208L ---
  fun openWorkshopProfitabilityModal() {
    _uiState.update { it.copy(showWorkshopProfitabilityModal = true) }
  }

  fun closeWorkshopProfitabilityModal() {
    _uiState.update { it.copy(showWorkshopProfitabilityModal = false) }
  }

  fun dispenseOilFromBulkDrum(drumId: String, liters: Double) {
    _uiState.update { state ->
      val updated = state.bulkOilDrums.map { drum ->
        if (drum.id == drumId) {
          drum.copy(currentRemainingLiters = (drum.currentRemainingLiters - liters).coerceAtLeast(0.0))
        } else drum
      }
      state.copy(
        bulkOilDrums = updated,
        toastMessage = "🛢️ $liters L prélevés du fût d'atelier et imputés à l'Ordre de Réparation !"
      )
    }
  }

  fun refillBulkOilDrum(drumId: String) {
    _uiState.update { state ->
      val updated = state.bulkOilDrums.map { drum ->
        if (drum.id == drumId) {
          drum.copy(currentRemainingLiters = drum.drumCapacityLiters)
        } else drum
      }
      state.copy(
        bulkOilDrums = updated,
        toastMessage = "✅ Fût neuf réceptionné et jauge réinitialisée à 100% !"
      )
    }
  }

  // --- 41. Scanner Pré-Contrôle Technique & Véhicules de Courtoisie ---
  fun openPreCtCourtesyModal() {
    _uiState.update { it.copy(showPreCtCourtesyModal = true) }
  }

  fun closePreCtCourtesyModal() {
    _uiState.update { it.copy(showPreCtCourtesyModal = false) }
  }

  fun fixPreCtInspectionPoint(pointId: String) {
    _uiState.update { state ->
      val updated = state.preCtInspectionPoints.map { pt ->
        if (pt.id == pointId) {
          pt.copy(
            status = PreCtCheckStatus.CONFORME,
            measuredValueText = "${pt.measuredValueText} → [CORRIGÉ & CONFORME]"
          )
        } else pt
      }
      state.copy(
        preCtInspectionPoints = updated,
        toastMessage = "✅ Défaut corrigé en atelier : Point validé CONFORME pour le Contrôle Technique !"
      )
    }
  }

  fun toggleCourtesyVehicleLoan(vehicleId: String) {
    _uiState.update { state ->
      val updated = state.courtesyLoanerVehicles.map { car ->
        if (car.id == vehicleId) {
          val nowLoaned = !car.isCurrentlyLoaned
          car.copy(
            isCurrentlyLoaned = nowLoaned,
            assignedClientName = if (nowLoaned) "Client Atelier (Immobilisation Pont)" else null,
            linkedRepairOrderNumber = if (nowLoaned) "OR-2026-1098" else null,
            expectedReturnDate = if (nowLoaned) "20 Oct 2026 (17:30)" else null
          )
        } else car
      }
      state.copy(
        courtesyLoanerVehicles = updated,
        toastMessage = "🚗 Statut du véhicule de courtoisie mis à jour !"
      )
    }
  }

  // --- 42. Compte Super-Administrateur, Équipe Employés, Essai Démo 3 Jours & Clés d'Activation ---
  fun openSuperAdminModal(initialTab: Int = 0) {
    _superAdminInitialTab.value = initialTab
    _uiState.update { it.copy(showSuperAdminModal = true) }
  }

  fun openSuperAdminErrorLogger() {
    _superAdminInitialTab.value = 5
    _uiState.update { it.copy(showSuperAdminModal = true) }
  }

  fun closeSuperAdminModal() {
    _uiState.update { it.copy(showSuperAdminModal = false) }
  }

  fun loginSuperAdmin(email: String, passwordOrPin: String) {
    val cleanEmail = email.trim().ifBlank { "badroumc@gmail.com" }
    if (passwordOrPin.isBlank()) {
      _uiState.update { it.copy(toastMessage = "⚠️ Veuillez saisir le mot de passe ou le code PIN Employé.") }
      return
    }
    _uiState.update { state ->
      val matchedStaff = state.adminStaffMembers.find {
        it.email.equals(cleanEmail, ignoreCase = true) || it.pinCode == passwordOrPin.trim()
      }
      val log = AdminAuditLogEntry(
        id = "LOG-${System.currentTimeMillis()}",
        timestamp = "À l'instant",
        adminEmail = matchedStaff?.email ?: cleanEmail,
        actionTitle = if (matchedStaff != null) {
          "Connexion Employé Admin (${matchedStaff.fullName})"
        } else {
          "Connexion Super-Administrateur Root réussie"
        },
        targetEntity = "Portail Central Mekanik+",
        impactSummary = if (matchedStaff != null) {
          "Session employé active : ${matchedStaff.role.label}"
        } else {
          "Session Root active pour l'approbation des inscriptions, clés et employés"
        },
        operatorBadge = matchedStaff?.let { "EMPLOYÉ • ${it.role.badgeLabel}" } ?: "SUPER-ADMIN ROOT"
      )
      state.copy(
        superAdminSession = state.superAdminSession.copy(
          adminEmail = "badroumc@gmail.com",
          isAuthenticated = true,
          lastLoginTime = "Connecté à l'instant",
          activeStaffOperator = matchedStaff
        ),
        adminAuditLogs = listOf(log) + state.adminAuditLogs,
        toastMessage = if (matchedStaff != null) {
          "👤 Connecté en tant qu'employé admin : ${matchedStaff.fullName} !"
        } else {
          "🛡️ Connecté en tant que Super-Administrateur ($cleanEmail) !"
        }
      )
    }
  }

  fun logoutSuperAdmin() {
    _uiState.update { state ->
      state.copy(
        superAdminSession = state.superAdminSession.copy(
          isAuthenticated = false,
          activeStaffOperator = null
        ),
        toastMessage = "🔒 Console Super-Administrateur verrouillée."
      )
    }
  }

  fun createAdminStaffMember(
    fullName: String,
    email: String,
    pinCode: String,
    role: AdminStaffRole,
    wilayas: String,
    canApproveReg: Boolean,
    canGenerateKey: Boolean,
    canValidatePay: Boolean,
    canSuspend: Boolean
  ) {
    _uiState.update { state ->
      val newStaff = AdminStaffMember(
        id = "STAFF-${(10..99).random()}",
        fullName = fullName,
        email = email,
        pinCode = pinCode,
        role = role,
        assignedWilayas = wilayas,
        canApproveRegistrations = canApproveReg,
        canGenerateActivationKeys = canGenerateKey,
        canValidatePaymentsDzd = canValidatePay,
        canSuspendAccounts = canSuspend,
        processedRequestsCount = 0,
        generatedKeysCount = 0,
        isActive = true,
        lastActiveLabel = "Créé à l'instant par ${state.superAdminSession.adminEmail}"
      )
      val log = AdminAuditLogEntry(
        id = "LOG-${System.currentTimeMillis()}",
        timestamp = "À l'instant",
        adminEmail = state.superAdminSession.currentOperatorEmail,
        actionTitle = "Création d'un Compte Employé Admin",
        targetEntity = "$fullName ($email)",
        impactSummary = "Poste : ${role.label} • PIN : $pinCode • Wilayas : $wilayas",
        operatorBadge = state.superAdminSession.currentOperatorBadge
      )
      state.copy(
        adminStaffMembers = listOf(newStaff) + state.adminStaffMembers,
        adminAuditLogs = listOf(log) + state.adminAuditLogs,
        toastMessage = "✅ Compte employé créé pour $fullName (PIN : $pinCode) !"
      )
    }
  }

  fun toggleAdminStaffActive(staffId: String) {
    _uiState.update { state ->
      val updated = state.adminStaffMembers.map { staff ->
        if (staff.id == staffId) staff.copy(isActive = !staff.isActive) else staff
      }
      state.copy(
        adminStaffMembers = updated,
        toastMessage = "🔄 Statut du compte employé mis à jour !"
      )
    }
  }

  fun switchActiveAdminOperator(staff: AdminStaffMember?) {
    _uiState.update { state ->
      state.copy(
        superAdminSession = state.superAdminSession.copy(activeStaffOperator = staff),
        toastMessage = if (staff != null) {
          "👤 Session active basculée sur l'employé : ${staff.fullName} (${staff.role.badgeLabel})"
        } else {
          "🛡️ Retour à la session Super-Administrateur Root (badroumc@gmail.com)"
        }
      )
    }
  }

  fun approveRegistrationRequest(requestId: String) {
    _uiState.update { state ->
      val target = state.registrationRequests.find { it.id == requestId }
      val generatedKey = "MKP-ACT-2026-${(1000..9999).random()}"
      val operatorName = state.superAdminSession.currentOperatorName
      val activeStaffId = state.superAdminSession.activeStaffOperator?.id

      val updated = state.registrationRequests.map { req ->
        if (req.id == requestId) {
          req.copy(
            status = AdminApprovalStatus.APPROUVE,
            generatedLicenseKey = req.generatedLicenseKey ?: generatedKey,
            processedByOperator = operatorName
          )
        } else req
      }
      val updatedStaff = state.adminStaffMembers.map { s ->
        if (s.id == activeStaffId) {
          s.copy(
            processedRequestsCount = s.processedRequestsCount + 1,
            generatedKeysCount = s.generatedKeysCount + 1
          )
        } else s
      }
      val newLog = AdminAuditLogEntry(
        id = "LOG-${System.currentTimeMillis()}",
        timestamp = "À l'instant",
        adminEmail = state.superAdminSession.currentOperatorEmail,
        actionTitle = "Inscription Approuvée & Clé d'Activation Générée",
        targetEntity = target?.companyOrFullName ?: requestId,
        impactSummary = "Clé $generatedKey générée par $operatorName (${target?.requestedRole?.label ?: "Compte Pro"})",
        operatorBadge = state.superAdminSession.currentOperatorBadge
      )
      state.copy(
        registrationRequests = updated,
        adminStaffMembers = updatedStaff,
        demoTrialStatus = state.demoTrialStatus.copy(
          expectedActivationKey = generatedKey,
          activatedByAdminOrStaff = operatorName
        ),
        adminAuditLogs = listOf(newLog) + state.adminAuditLogs,
        toastMessage = "✅ Inscription de « ${target?.companyOrFullName ?: requestId} » activée avec la clé $generatedKey !"
      )
    }
  }

  fun generateActivationKeyForRegistration(requestId: String) {
    _uiState.update { state ->
      val target = state.registrationRequests.find { it.id == requestId }
      val newKey = "MKP-ACT-2026-${(1000..9999).random()}"
      val operatorName = state.superAdminSession.currentOperatorName
      val activeStaffId = state.superAdminSession.activeStaffOperator?.id

      val updated = state.registrationRequests.map { req ->
        if (req.id == requestId) {
          req.copy(
            status = AdminApprovalStatus.APPROUVE,
            generatedLicenseKey = newKey,
            processedByOperator = operatorName
          )
        } else req
      }
      val updatedStaff = state.adminStaffMembers.map { s ->
        if (s.id == activeStaffId) {
          s.copy(generatedKeysCount = s.generatedKeysCount + 1)
        } else s
      }
      val newLog = AdminAuditLogEntry(
        id = "LOG-${System.currentTimeMillis()}",
        timestamp = "À l'instant",
        adminEmail = state.superAdminSession.currentOperatorEmail,
        actionTitle = "Nouvelle Clé d'Activation Émise",
        targetEntity = target?.companyOrFullName ?: requestId,
        impactSummary = "Clé $newKey envoyée au client pour débloquer la fin de Démo 3 jours",
        operatorBadge = state.superAdminSession.currentOperatorBadge
      )
      state.copy(
        registrationRequests = updated,
        adminStaffMembers = updatedStaff,
        demoTrialStatus = state.demoTrialStatus.copy(expectedActivationKey = newKey),
        adminAuditLogs = listOf(newLog) + state.adminAuditLogs,
        toastMessage = "🔑 Nouvelle Clé d'Activation $newKey générée pour ${target?.companyOrFullName ?: ""} !"
      )
    }
  }

  fun rejectRegistrationRequest(requestId: String) {
    _uiState.update { state ->
      val target = state.registrationRequests.find { it.id == requestId }
      val updated = state.registrationRequests.map { req ->
        if (req.id == requestId) {
          req.copy(
            status = AdminApprovalStatus.REFUSE,
            processedByOperator = state.superAdminSession.currentOperatorName
          )
        } else req
      }
      val newLog = AdminAuditLogEntry(
        id = "LOG-${System.currentTimeMillis()}",
        timestamp = "À l'instant",
        adminEmail = state.superAdminSession.currentOperatorEmail,
        actionTitle = "Demande d'Inscription Refusée",
        targetEntity = target?.companyOrFullName ?: requestId,
        impactSummary = "Dossier refusé par ${state.superAdminSession.currentOperatorName}",
        operatorBadge = state.superAdminSession.currentOperatorBadge
      )
      state.copy(
        registrationRequests = updated,
        adminAuditLogs = listOf(newLog) + state.adminAuditLogs,
        toastMessage = "❌ Inscription de « ${target?.companyOrFullName ?: requestId} » refusée."
      )
    }
  }

  fun toggleSuspendRegistrationRequest(requestId: String) {
    _uiState.update { state ->
      val target = state.registrationRequests.find { it.id == requestId }
      val updated = state.registrationRequests.map { req ->
        if (req.id == requestId) {
          val nextStatus = if (req.status == AdminApprovalStatus.SUSPENDU) {
            AdminApprovalStatus.APPROUVE
          } else {
            AdminApprovalStatus.SUSPENDU
          }
          req.copy(status = nextStatus)
        } else req
      }
      state.copy(
        registrationRequests = updated,
        toastMessage = "🔄 Statut d'accès de « ${target?.companyOrFullName ?: requestId} » mis à jour !"
      )
    }
  }

  fun approveAllPendingRegistrations() {
    _uiState.update { state ->
      val operatorName = state.superAdminSession.currentOperatorName
      val count = state.registrationRequests.count {
        it.status == AdminApprovalStatus.EN_ATTENTE ||
          it.status == AdminApprovalStatus.DEMO_3_JOURS ||
          it.status == AdminApprovalStatus.CLE_REQUISE
      }
      val updated = state.registrationRequests.map { req ->
        if (
          req.status == AdminApprovalStatus.EN_ATTENTE ||
          req.status == AdminApprovalStatus.DEMO_3_JOURS ||
          req.status == AdminApprovalStatus.CLE_REQUISE
        ) {
          req.copy(
            status = AdminApprovalStatus.APPROUVE,
            generatedLicenseKey = req.generatedLicenseKey ?: "MKP-ACT-2026-${(1000..9999).random()}",
            processedByOperator = operatorName
          )
        } else req
      }
      val newLog = AdminAuditLogEntry(
        id = "LOG-${System.currentTimeMillis()}",
        timestamp = "À l'instant",
        adminEmail = state.superAdminSession.currentOperatorEmail,
        actionTitle = "Approbation Groupée des Inscriptions ($count dossiers)",
        targetEntity = "File d'attente Inscriptions Mekanik+",
        impactSummary = "$count comptes activés avec clés d'activation par $operatorName",
        operatorBadge = state.superAdminSession.currentOperatorBadge
      )
      state.copy(
        registrationRequests = updated,
        adminAuditLogs = listOf(newLog) + state.adminAuditLogs,
        toastMessage = "✅ Les $count demandes d'inscription ont été activées avec leurs clés Pro !"
      )
    }
  }

  fun createTestRegistrationRequest(
    companyName: String,
    managerName: String,
    email: String,
    wilaya: String,
    role: AccountRole,
    rcNumber: String,
    planName: String
  ) {
    _uiState.update { state ->
      val newReq = UserRegistrationRequest(
        id = "REG-2026-${(810..999).random()}",
        companyOrFullName = companyName,
        managerName = managerName,
        email = email,
        phone = "0550 ${(10..99).random()} ${(10..99).random()} ${(10..99).random()}",
        wilaya = wilaya,
        requestedRole = role,
        rcNumber = rcNumber,
        nifNumber = "NIF 002516${(1000000..9999999).random()}",
        requestedInitialPlanName = planName,
        submittedDate = "À l'instant",
        status = AdminApprovalStatus.DEMO_3_JOURS,
        demoTrialRemainingHours = 72,
        isDemoTrialGrantedOnSignup = true,
        adminNote = "Compte Démo 3 jours (72h) attribué automatiquement à l'inscription — Clé d'activation requise après 3j."
      )
      state.copy(
        registrationRequests = listOf(newReq) + state.registrationRequests,
        demoTrialStatus = state.demoTrialStatus.copy(
          accountHolderName = companyName,
          registeredEmail = email,
          phase = DemoTrialPhase.DEMO_ACTIVE_3_DAYS,
          remainingHours = 72
        ),
        toastMessage = "🎁 Inscription enregistrée : Compte Démo 3 Jours (72h) activé automatiquement !"
      )
    }
  }

  fun approvePlanSubscriptionRequest(subscriptionId: String) {
    _uiState.update { state ->
      val target = state.planSubscriptionRequests.find { it.id == subscriptionId }
      val generatedKey = "MKP-ACT-2026-${(1000..9999).random()}"
      val operatorName = state.superAdminSession.currentOperatorName
      val activeStaffId = state.superAdminSession.activeStaffOperator?.id

      val updated = state.planSubscriptionRequests.map { sub ->
        if (sub.id == subscriptionId) {
          sub.copy(
            status = AdminApprovalStatus.APPROUVE,
            generatedActivationKey = sub.generatedActivationKey ?: generatedKey,
            processedByOperator = operatorName
          )
        } else sub
      }
      val updatedStaff = state.adminStaffMembers.map { s ->
        if (s.id == activeStaffId) {
          s.copy(
            processedRequestsCount = s.processedRequestsCount + 1,
            generatedKeysCount = s.generatedKeysCount + 1
          )
        } else s
      }
      val newLog = AdminAuditLogEntry(
        id = "LOG-${System.currentTimeMillis()}",
        timestamp = "À l'instant",
        adminEmail = state.superAdminSession.currentOperatorEmail,
        actionTitle = "Validation Forfait & Émission Clé (${target?.amountDzd ?: 0} DA)",
        targetEntity = target?.accountName ?: subscriptionId,
        impactSummary = "Clé $generatedKey émise par $operatorName • Actif jusqu'au ${target?.validUntilDate ?: "2027"}",
        operatorBadge = state.superAdminSession.currentOperatorBadge
      )
      state.copy(
        planSubscriptionRequests = updated,
        adminStaffMembers = updatedStaff,
        userPlanName = target?.planTitle?.take(28) ?: state.userPlanName,
        demoTrialStatus = state.demoTrialStatus.copy(
          expectedActivationKey = generatedKey,
          activatedByAdminOrStaff = operatorName
        ),
        adminAuditLogs = listOf(newLog) + state.adminAuditLogs,
        toastMessage = "✅ Forfait validé et Clé d'Activation $generatedKey envoyée à ${target?.accountName ?: ""} !"
      )
    }
  }

  fun rejectPlanSubscriptionRequest(subscriptionId: String) {
    _uiState.update { state ->
      val target = state.planSubscriptionRequests.find { it.id == subscriptionId }
      val updated = state.planSubscriptionRequests.map { sub ->
        if (sub.id == subscriptionId) sub.copy(status = AdminApprovalStatus.REFUSE) else sub
      }
      val newLog = AdminAuditLogEntry(
        id = "LOG-${System.currentTimeMillis()}",
        timestamp = "À l'instant",
        adminEmail = state.superAdminSession.currentOperatorEmail,
        actionTitle = "Rejet Demande de Forfait",
        targetEntity = target?.accountName ?: subscriptionId,
        impactSummary = "Justificatif de paiement non validé",
        operatorBadge = state.superAdminSession.currentOperatorBadge
      )
      state.copy(
        planSubscriptionRequests = updated,
        adminAuditLogs = listOf(newLog) + state.adminAuditLogs,
        toastMessage = "❌ Souscription forfait de « ${target?.accountName ?: ""} » rejetée."
      )
    }
  }

  fun extendPlanSubscriptionRequest(subscriptionId: String) {
    _uiState.update { state ->
      val updated = state.planSubscriptionRequests.map { sub ->
        if (sub.id == subscriptionId) {
          sub.copy(validUntilDate = "17 Oct 2028 (+1 An Prolongé)")
        } else sub
      }
      state.copy(
        planSubscriptionRequests = updated,
        toastMessage = "📅 Licence prolongée d'une année supplémentaire (+12 mois) !"
      )
    }
  }

  fun approveAllPendingSubscriptions() {
    _uiState.update { state ->
      val operatorName = state.superAdminSession.currentOperatorName
      val pending = state.planSubscriptionRequests.filter { it.status == AdminApprovalStatus.EN_ATTENTE }
      val totalAmount = pending.sumOf { it.amountDzd }
      val updated = state.planSubscriptionRequests.map { sub ->
        if (sub.status == AdminApprovalStatus.EN_ATTENTE) {
          sub.copy(
            status = AdminApprovalStatus.APPROUVE,
            generatedActivationKey = sub.generatedActivationKey ?: "MKP-ACT-2026-${(1000..9999).random()}",
            processedByOperator = operatorName
          )
        } else sub
      }
      val newLog = AdminAuditLogEntry(
        id = "LOG-${System.currentTimeMillis()}",
        timestamp = "À l'instant",
        adminEmail = state.superAdminSession.currentOperatorEmail,
        actionTitle = "Validation Groupée des Forfaits (${pending.size} forfaits)",
        targetEntity = "Trésorerie & Licences Mekanik+",
        impactSummary = "Total validé : $totalAmount DA par $operatorName • Clés d'activation envoyées",
        operatorBadge = state.superAdminSession.currentOperatorBadge
      )
      state.copy(
        planSubscriptionRequests = updated,
        adminAuditLogs = listOf(newLog) + state.adminAuditLogs,
        toastMessage = "✅ ${pending.size} forfaits validés ($totalAmount DA) et clés d'activation générées !"
      )
    }
  }

  fun createTestPlanSubscriptionRequest(
    accountName: String,
    planTitle: String,
    amountDzd: Int,
    paymentMethod: PaymentMethodAdminType,
    proofRef: String,
    role: AccountRole
  ) {
    _uiState.update { state ->
      val newSub = PlanSubscriptionRequest(
        id = "SUB-2026-${(910..999).random()}",
        accountName = accountName,
        userEmail = "client.pro@mekanik.dz",
        accountRole = role,
        wilaya = "16 - Alger",
        planId = "plan_custom_${System.currentTimeMillis()}",
        planTitle = planTitle,
        billingPeriod = "Annuel (12 mois)",
        amountDzd = amountDzd,
        paymentMethod = paymentMethod,
        paymentProofReference = proofRef,
        submittedDate = "À l'instant",
        status = AdminApprovalStatus.EN_ATTENTE,
        includesStoreLinkAddon = planTitle.contains("Magasin", ignoreCase = true)
      )
      state.copy(
        planSubscriptionRequests = listOf(newSub) + state.planSubscriptionRequests,
        toastMessage = "💳 Demande de forfait envoyée dans la console Super-Administrateur !"
      )
    }
  }

  fun submitUserPlanSelectionForAdminApproval(plan: PricingPlan) {
    val parsedAmount = plan.priceDzd.filter { it.isDigit() }.toIntOrNull() ?: 18000
    _uiState.update { state ->
      val newSub = PlanSubscriptionRequest(
        id = "SUB-2026-${(910..999).random()}",
        accountName = when (state.currentAccountRole) {
          AccountRole.GARAGE_PRO -> "Garage Auto Performance • Alger"
          AccountRole.VENDEUR_PIECES -> "SARL Auto Pièces Alger"
          AccountRole.ATELIER_MAGASIN_MIXTE -> "Auto Pièces & Montage Express • Alger"
          AccountRole.ATELIER_CARROSSERIE -> "Carrosserie & Tôlerie Pro • Alger"
          AccountRole.CONDUCTEUR -> "Karim Mansouri (Conducteur)"
          AccountRole.LOCATION_VOITURES -> "Auto Rent Algiers Prestige"
        },
        userEmail = state.superAdminSession.adminEmail,
        accountRole = state.currentAccountRole,
        wilaya = "16 - Alger",
        planId = plan.id,
        planTitle = "${plan.title} (${plan.priceDzd})",
        billingPeriod = plan.billingPeriod,
        amountDzd = parsedAmount,
        paymentMethod = PaymentMethodAdminType.CIB_EDAHABIA,
        paymentProofReference = "SATIM CIB #AUTH-${(100000..999999).random()}",
        submittedDate = "À l'instant",
        status = AdminApprovalStatus.EN_ATTENTE,
        includesStoreLinkAddon = plan.title.contains("Magasin", ignoreCase = true)
      )
      state.copy(
        planSubscriptionRequests = listOf(newSub) + state.planSubscriptionRequests,
        showSuperAdminModal = true,
        toastMessage = "📥 Demande de souscription « ${plan.title} » transmise au Super-Administrateur pour approbation !"
      )
    }
  }

  // --- 43. Compte Démo 3 Jours Automatique & Déblocage par Clé d'Activation ---
  fun openDemoTrialActivationModal() {
    _uiState.update { it.copy(showDemoTrialActivationModal = true) }
  }

  fun closeDemoTrialActivationModal() {
    _uiState.update { it.copy(showDemoTrialActivationModal = false) }
  }

  fun simulateDemoTrialPhase(phase: DemoTrialPhase) {
    _uiState.update { state ->
      state.copy(
        demoTrialStatus = state.demoTrialStatus.copy(
          phase = phase,
          remainingHours = phase.defaultRemainingHours
        ),
        toastMessage = when (phase) {
          DemoTrialPhase.DEMO_ACTIVE_3_DAYS -> "⏳ Simulation : Compte Démo 3 Jours actif (68h restantes, accès limité)."
          DemoTrialPhase.DEMO_LAST_DAY_WARNING -> "⚠️ Simulation : Dernier jour d'essai Démo (5h restantes avant blocage)."
          DemoTrialPhase.DEMO_EXPIRED_KEY_REQUIRED -> "🔒 Simulation : Essai Démo 3 jours expiré — Clé d'Activation demandée !"
          DemoTrialPhase.PRO_ACTIVATED_UNLOCKED -> "✅ Simulation : Compte Pro débloqué par Clé d'Activation !"
        }
      )
    }
  }

  fun activateAccountWithLicenseKey(enteredKey: String) {
    val cleanKey = enteredKey.trim().uppercase()
    if (cleanKey.length < 6) {
      _uiState.update { it.copy(toastMessage = "⚠️ Veuillez saisir une Clé d'Activation valide (ex: MKP-ACT-2026-9941).") }
      return
    }
    _uiState.update { state ->
      val log = AdminAuditLogEntry(
        id = "LOG-${System.currentTimeMillis()}",
        timestamp = "À l'instant",
        adminEmail = state.demoTrialStatus.registeredEmail,
        actionTitle = "Déblocage Compte après Démo 3 Jours par Clé",
        targetEntity = state.demoTrialStatus.accountHolderName,
        impactSummary = "Clé $cleanKey validée • Passage de Démo 3J à Licence Pro Illimitée",
        operatorBadge = "ACTIVATION CLIENT"
      )
      state.copy(
        demoTrialStatus = state.demoTrialStatus.copy(
          phase = DemoTrialPhase.PRO_ACTIVATED_UNLOCKED,
          remainingHours = 8760,
          enteredActivationKey = cleanKey,
          activatedByAdminOrStaff = state.superAdminSession.currentOperatorName
        ),
        dailyVinLimit = 999,
        adminAuditLogs = listOf(log) + state.adminAuditLogs,
        toastMessage = "🔓 Clé d'Activation « $cleanKey » validée ! Votre compte Pro est débloqué sans limite."
      )
    }
  }

  fun requestActivationKeyFromAdmin(paymentMethod: PaymentMethodAdminType, reference: String) {
    _uiState.update { state ->
      val newSub = PlanSubscriptionRequest(
        id = "SUB-2026-${(920..999).random()}",
        accountName = state.demoTrialStatus.accountHolderName,
        userEmail = state.demoTrialStatus.registeredEmail,
        accountRole = state.currentAccountRole,
        wilaya = "16 - Alger",
        planId = "plan_unlock_after_3d_demo",
        planTitle = "Déblocage Clé d'Activation après Essai Démo 3 Jours (18 000 DA)",
        billingPeriod = "Annuel (12 mois)",
        amountDzd = 18000,
        paymentMethod = paymentMethod,
        paymentProofReference = reference.ifBlank { "Reçu DZD #ACT-2026" },
        submittedDate = "À l'instant (Fin Démo 3J)",
        status = AdminApprovalStatus.EN_ATTENTE
      )
      state.copy(
        planSubscriptionRequests = listOf(newSub) + state.planSubscriptionRequests,
        demoTrialStatus = state.demoTrialStatus.copy(isKeyRequestedFromAdmin = true),
        toastMessage = "📨 Demande de Clé d'Activation transmise au Super-Admin et aux employés !"
      )
    }
  }

  fun simulateNewSignupWithAutoDemo(companyName: String, email: String, role: AccountRole) {
    createTestRegistrationRequest(
      companyName = companyName,
      managerName = "Nouveau Gérant Inscrit",
      email = email,
      wilaya = "16 - Alger",
      role = role,
      rcNumber = "RC 16/00-${(100000..999999).random()}B26",
      planName = "Essai Démo 3 Jours Automatique (72h) puis Clé Pro"
    )
  }

  fun consumeDemoQuotaSample() {
    _uiState.update { state ->
      val cur = state.demoTrialStatus
      val nextVin = (cur.vinUsedToday + 1).coerceAtMost(cur.vinDailyDemoLimit)
      val nextAi = (cur.aiDiagUsedToday + 1).coerceAtMost(cur.aiDiagDailyDemoLimit)
      val nextOem = (cur.oemCatalogViewsToday + 1).coerceAtMost(cur.oemCatalogDailyDemoLimit)
      val reachedMax = nextVin >= cur.vinDailyDemoLimit && nextAi >= cur.aiDiagDailyDemoLimit
      state.copy(
        demoTrialStatus = cur.copy(
          vinUsedToday = nextVin,
          aiDiagUsedToday = nextAi,
          oemCatalogViewsToday = nextOem,
          phase = if (reachedMax) DemoTrialPhase.DEMO_EXPIRED_KEY_REQUIRED else cur.phase,
          remainingHours = if (reachedMax) 0 else (cur.remainingHours - 12).coerceAtLeast(1)
        ),
        toastMessage = if (reachedMax) {
          "🔒 Quotas Démo 3 jours atteints ! Veuillez saisir votre Clé d'Activation pour continuer."
        } else {
          "📊 Quota Démo utilisé : VIN ($nextVin/${cur.vinDailyDemoLimit}) • IA ($nextAi/${cur.aiDiagDailyDemoLimit})"
        }
      )
    }
  }

  // --- 44. Suite DMS « WinMotor Next & Peugeot Service Box V14 » ---
  fun openWinMotorServiceBoxModal() {
    _uiState.update { it.copy(showWinMotorServiceBoxModal = true) }
  }

  fun closeWinMotorServiceBoxModal() {
    _uiState.update { it.copy(showWinMotorServiceBoxModal = false) }
  }

  fun updateWinMotorLaborRates(t1: Int, t2: Int, t3: Int) {
    _uiState.update { state ->
      state.copy(
        winMotorRatesConfig = state.winMotorRatesConfig.copy(
          rateT1Dzd = t1,
          rateT2Dzd = t2,
          rateT3Dzd = t3
        ),
        workshopHourlyRateDzd = t1,
        toastMessage = "⚙️ Grille horaire WinMotor mise à jour : T1=$t1 DA/h • T2=$t2 DA/h • T3=$t3 DA/h"
      )
    }
  }

  fun toggleWinMotorOperationSelection(opId: String) {
    _uiState.update { state ->
      val updated = state.winMotorOperations.map { op ->
        if (op.id == opId) op.copy(isSelectedForOrder = !op.isSelectedForOrder) else op
      }
      state.copy(winMotorOperations = updated)
    }
  }

  fun importServiceBoxBasketToOrder(basketId: String) {
    _uiState.update { state ->
      val basket = state.serviceBoxBaskets.find { it.id == basketId } ?: return@update state
      val updatedBaskets = state.serviceBoxBaskets.map {
        if (it.id == basketId) it.copy(isImportedIntoActiveOrder = true) else it
      }
      val totalCentiemes = basket.totalCentiemesHeure
      val laborHoursFloat = (totalCentiemes / 100f).coerceAtLeast(1f)
      val avgRate = state.winMotorRatesConfig.rateT2Dzd

      val newOrder = RepairOrder(
        id = "or_sbx_${System.currentTimeMillis()}",
        orderNumber = "OR-SBX-${(2000..9999).random()}",
        clientName = "Client Service Box (${basket.vehicleModel.take(18)})",
        clientPhone = "0550 88 41 20",
        vehicleModel = basket.vehicleModel,
        vin = basket.vin,
        plateNumber = "18204-122-16",
        mileageKm = 94500,
        date = "Aujourd'hui (Import Service Box V14)",
        status = RepairStatus.EN_COURS,
        items = basket.partsList.map { p ->
          RepairItem(p.designation, p.oemRef, p.quantity, p.unitPriceHtDzd)
        },
        laborHours = laborHoursFloat,
        laborRatePerHourDzd = avgRate,
        notes = "Importé via Passerelle WinMotor / ${basket.sourceCatalog} (${basket.basketReference} • ${basket.oprDamCode} • $totalCentiemes centièmes d'heure)."
      )

      state.copy(
        serviceBoxBaskets = updatedBaskets,
        repairOrders = listOf(newOrder) + state.repairOrders,
        toastMessage = "🛒 Panier ${basket.basketReference} importé dans l'Ordre de Réparation ${newOrder.orderNumber} !"
      )
    }
  }

  fun createRepairOrderFromWinMotorSelection() {
    _uiState.update { state ->
      val selectedOps = state.winMotorOperations.filter { it.isSelectedForOrder }
      if (selectedOps.isEmpty()) {
        return@update state.copy(toastMessage = "⚠️ Sélectionnez au moins une opération barèmée (T1/T2/T3).")
      }
      val totalCth = selectedOps.sumOf { it.centiemesHeure }
      val totalLaborDzd = selectedOps.sumOf { it.computeLaborCostDzd(state.winMotorRatesConfig) }
      val hoursFloat = (totalCth / 100f).coerceAtLeast(0.5f)
      val vehicle = state.activeVehicle

      val newOrder = RepairOrder(
        id = "or_wm_${System.currentTimeMillis()}",
        orderNumber = "OR-WM-${(1000..9999).random()}",
        clientName = "Client Atelier WinMotor",
        clientPhone = "0550 44 12 90",
        vehicleModel = "${vehicle.brand} ${vehicle.model}",
        vin = vehicle.vin,
        plateNumber = "08745-121-16",
        mileageKm = 82000,
        date = "Aujourd'hui (Chiffrage T1/T2/T3)",
        status = RepairStatus.EN_COURS,
        items = selectedOps.map { op ->
          RepairItem(
            name = "[${op.laborTier.code} • ${op.oemOperationCode}] ${op.label} (${op.centiemesHeure} cth)",
            reference = op.oemOperationCode,
            quantity = 1,
            priceDzd = op.computeLaborCostDzd(state.winMotorRatesConfig)
          )
        },
        laborHours = hoursFloat,
        laborRatePerHourDzd = state.winMotorRatesConfig.rateT2Dzd,
        notes = "Chiffrage WinMotor Multi-Technicité : $totalCth centièmes d'heure ($totalLaborDzd DA)."
      )

      state.copy(
        repairOrders = listOf(newOrder) + state.repairOrders,
        toastMessage = "🧾 Ordre de Réparation ${newOrder.orderNumber} généré ($totalCth cth • $totalLaborDzd DA) !"
      )
    }
  }

  fun updateCompanionClockStatus(
    entryId: String,
    newStatus: CompanionClockStatus,
    addCentiemes: Int
  ) {
    _uiState.update { state ->
      val updated = state.companionClockEntries.map { entry ->
        if (entry.id == entryId) {
          entry.copy(
            status = newStatus,
            clockedCentiemesReal = (entry.clockedCentiemesReal + addCentiemes).coerceAtLeast(10)
          )
        } else entry
      }
      state.copy(
        companionClockEntries = updated,
        toastMessage = "⏱️ Pointage compagnon mis à jour (${newStatus.label}) !"
      )
    }
  }

  fun updateMultiPayerSplit(
    orderId: String,
    clientPct: Int,
    warrantyPct: Int,
    internalPct: Int
  ) {
    _uiState.update { state ->
      val updated = state.multiPayerSplitOrders.map { order ->
        if (order.id == orderId) {
          order.copy(
            clientSharePercent = clientPct,
            manufacturerWarrantyPercent = warrantyPct,
            internalCessionPercent = internalPct,
            isValidatedForInvoicing = true
          )
        } else order
      }
      state.copy(
        multiPayerSplitOrders = updated,
        toastMessage = "🛡️ Ventilation Multi-Payeurs appliquée : Client $clientPct% • Garantie $warrantyPct% • Cession $internalPct% !"
      )
    }
  }

  fun receiveAndAllocateVorPart(vorId: String) {
    _uiState.update { state ->
      val target = state.backorderVorItems.find { it.id == vorId }
      val updated = state.backorderVorItems.map { item ->
        if (item.id == vorId) {
          item.copy(
            isReceivedAndAllocated = true,
            expectedArrivalTime = "Réceptionnée & affectée à ${item.linkedRepairOrderNumber}"
          )
        } else item
      }
      state.copy(
        backorderVorItems = updated,
        toastMessage = "📦 Pièce VOR [${target?.oemReference ?: ""}] réceptionnée et affectée en priorité à ${target?.linkedRepairOrderNumber ?: ""} !"
      )
    }
  }

  // ============================================================================
  // 25. MEKANIK-ONE UNIFIED AUTO-API (SinoAuto360 + Dongchedi + Car2DB LLM + 142 Marques + ChineseCar.parts)
  // ============================================================================

  fun openUnifiedAutoApiHubModal(initialTabIndex: Int = 0) {
    _uiState.update {
      it.copy(
        showUnifiedAutoApiHubModal = true,
        unifiedHubInitialTabIndex = initialTabIndex
      )
    }
  }

  fun closeUnifiedAutoApiHubModal() {
    _uiState.update { it.copy(showUnifiedAutoApiHubModal = false) }
  }

  fun executeUnifiedAutoApiQuery(query: String, scenarioId: String? = null) {
    val trace = UnifiedAutoApiRepository.executeUnifiedApiQuery(
      queryInput = query,
      scenarioId = scenarioId,
      sourceConfigs = _uiState.value.fallbackChainConfigs
    )
    val cascade = trace.fallbackCascade
    val learnedList = UnifiedAutoApiRepository.autoLearnActiveTraceToRoomCache(trace)
    val updatedQuote = UnifiedAutoApiRepository.buildOneClickQuoteForTrace(trace, _uiState.value.selectedQuoteTierIndex)
    val matchedFluid = _uiState.value.fluidAndServiceResetSpecs.firstOrNull {
      it.vinSample.take(3).equals(trace.matchedVinRecord.vin.take(3), ignoreCase = true)
    } ?: _uiState.value.fluidAndServiceResetSpecs.first()
    val updatedQrSync = UnifiedAutoApiRepository.buildQrPassportSyncRecord(trace, matchedFluid, updatedQuote)

    _uiState.update { state ->
      state.copy(
        activeUnifiedApiTrace = trace,
        autoLearnedCacheEntries = learnedList,
        oneClickAutoQuoteBundle = updatedQuote,
        qrPassportCascadeSyncRecord = updatedQrSync,
        apiTelemetryMetrics = state.apiTelemetryMetrics.copy(
          totalRequests = state.apiTelemetryMetrics.totalRequests + 1,
          cachedRequests = state.apiTelemetryMetrics.cachedRequests + 1,
          apiCallsSaved = state.apiTelemetryMetrics.apiCallsSaved + (11 - cascade.winningStepOrder).coerceAtLeast(1)
        ),
        toastMessage = cascade.cascadeSummaryBanner
      )
    }
  }

  // --- Handlers des 6 Options Indispensables ---

  // Option 1 : Pont Photo Plaque Châssis / Carte Grise (OCR Hors-Ligne) → Cascade 11 API
  fun runOcrChassisToCascade(rawNoisyOcrText: String) {
    val (cleanedVin, corrections) = UnifiedAutoApiRepository.cleanNoisyOcrVinString(rawNoisyOcrText)
    val trace = UnifiedAutoApiRepository.executeUnifiedApiQuery(
      queryInput = cleanedVin,
      scenarioId = null,
      sourceConfigs = _uiState.value.fallbackChainConfigs
    )
    val learnedList = UnifiedAutoApiRepository.autoLearnActiveTraceToRoomCache(trace)
    val updatedQuote = UnifiedAutoApiRepository.buildOneClickQuoteForTrace(trace, _uiState.value.selectedQuoteTierIndex)
    val matchedFluid = _uiState.value.fluidAndServiceResetSpecs.firstOrNull {
      it.vinSample.take(3).equals(cleanedVin.take(3), ignoreCase = true)
    } ?: _uiState.value.fluidAndServiceResetSpecs.first()
    val updatedQrSync = UnifiedAutoApiRepository.buildQrPassportSyncRecord(trace, matchedFluid, updatedQuote)

    _uiState.update { state ->
      state.copy(
        searchQuery = cleanedVin,
        activeUnifiedApiTrace = trace,
        autoLearnedCacheEntries = learnedList,
        oneClickAutoQuoteBundle = updatedQuote,
        qrPassportCascadeSyncRecord = updatedQrSync,
        toastMessage = "📷 OCR Nettoyé [${cleanedVin}] (${corrections.firstOrNull() ?: "ISO-3779 OK"}) → ${trace.fallbackCascade.cascadeSummaryBanner}"
      )
    }
  }

  // Option 2 : Pont Équivalence Croisée Chinoise ↔ Européenne (OEM → TecDoc IAM)
  fun selectCrossRefAndAddToOneClickQuote(
    bridge: SinoEuroCrossReferenceBridge,
    iamMatch: CrossRefIamSupplierMatch
  ) {
    _uiState.update { state ->
      val currentQuote = state.oneClickAutoQuoteBundle
      val newIamLine = OneClickAutoQuoteLineItem(
        refCode = "${iamMatch.iamPartNumber} (Équiv. OEM ${bridge.chineseOemPartNumber})",
        description = "${bridge.partName} — ${iamMatch.iamBrand}",
        sourceApiBadge = "Pont Chine↔Europe #6 TecDoc (${iamMatch.tecDocKType}) • ${iamMatch.localVendorNameWilaya}",
        quantity = 1,
        unitPriceDzd = iamMatch.priceDzd,
        isLaborLine = false
      )
      val updatedLines = listOf(newIamLine) + currentQuote.partsLines.filterNot {
        it.refCode.contains(iamMatch.iamPartNumber)
      }
      val updatedQuote = currentQuote.copy(partsLines = updatedLines)
      state.copy(
        oneClickAutoQuoteBundle = updatedQuote,
        toastMessage = "🔄 Équivalence Européenne ${iamMatch.iamBrand} (${iamMatch.iamPartNumber} à ${iamMatch.priceDzd} DA, -${iamMatch.savingsPercent}%) ajoutée au Devis 1-Clic !"
      )
    }
  }

  // Option 3 : Auto-Apprentissage du Cache Local Room SQLite (0 ms)
  fun triggerAutoLearnActiveTraceToRoom() {
    val trace = _uiState.value.activeUnifiedApiTrace
    val learnedList = UnifiedAutoApiRepository.autoLearnActiveTraceToRoomCache(trace)
    _uiState.update { state ->
      state.copy(
        autoLearnedCacheEntries = learnedList,
        toastMessage = "💾 VIN ${trace.matchedVinRecord.vin} (${trace.matchedVinRecord.brand} ${trace.matchedVinRecord.model}) indexé dans Room SQLite #11 (Réponse 0 ms hors-ligne garantie) !"
      )
    }
  }

  fun recallVehicleFromOfflineRoomZeroMs(vin: String) {
    val trace = UnifiedAutoApiRepository.executeUnifiedApiQuery(
      queryInput = vin,
      scenarioId = null,
      sourceConfigs = _uiState.value.fallbackChainConfigs
    )
    val learnedList = UnifiedAutoApiRepository.autoLearnActiveTraceToRoomCache(trace)
    val updatedQuote = UnifiedAutoApiRepository.buildOneClickQuoteForTrace(trace, _uiState.value.selectedQuoteTierIndex)
    _uiState.update { state ->
      state.copy(
        activeUnifiedApiTrace = trace,
        autoLearnedCacheEntries = learnedList,
        oneClickAutoQuoteBundle = updatedQuote,
        toastMessage = "⚡ Chargé en 0 ms depuis le Cache Local Room SQLite (#11) : ${trace.matchedVinRecord.brand} ${trace.matchedVinRecord.model} (${vin}) !"
      )
    }
  }

  // Option 4 : Devis / Ordre de Réparation (OR) en 1 Clic (DZD)
  fun selectOneClickQuoteTier(tierIndex: Int) {
    _uiState.update { state ->
      val updatedQuote = UnifiedAutoApiRepository.buildOneClickQuoteForTrace(state.activeUnifiedApiTrace, tierIndex)
      state.copy(
        selectedQuoteTierIndex = tierIndex,
        oneClickAutoQuoteBundle = updatedQuote,
        toastMessage = "🧾 Barème appliqué : ${updatedQuote.tariffTierLabel} → Total : ${updatedQuote.totalTtcDzd} DZD TTC"
      )
    }
  }

  fun createWorkshopOrderFromOneClickQuote() {
    _uiState.update { state ->
      val q = state.oneClickAutoQuoteBundle
      val newOrder = RepairOrder(
        id = q.id,
        orderNumber = q.orderReferenceNumber,
        clientName = q.clientName,
        clientPhone = q.clientPhone,
        vehicleModel = q.vehicleTitle,
        vin = q.vin,
        plateNumber = "09412-124-16",
        mileageKm = 28500,
        date = "Aujourd'hui • Généré en 1-Clic via Cascade 11 API",
        status = RepairStatus.EN_COURS,
        items = q.partsLines.map { line ->
          RepairItem(
            name = line.description,
            reference = line.refCode,
            quantity = line.quantity,
            priceDzd = line.unitPriceDzd
          )
        },
        laborHours = q.flatRateHours,
        laborRatePerHourDzd = q.hourlyRateDzd,
        notes = "${q.generatedFromCascadeSource} • ${q.warrantyNotice}"
      )
      val updatedOrders = listOf(newOrder) + state.repairOrders.filterNot { it.orderNumber == newOrder.orderNumber }
      state.copy(
        repairOrders = updatedOrders,
        activeRepairOrder = newOrder,
        showUnifiedAutoApiHubModal = false,
        currentNavTab = MainNavigationTab.ATELIER,
        toastMessage = "✅ Ordre de Réparation ${newOrder.orderNumber} (${q.totalTtcDzd} DZD TTC) créé et ouvert dans l'Atelier !"
      )
    }
  }

  // Option 5 : Fluides, Boîtes DCT & Procédures de Reset Service
  fun executeObdServiceResetRoutine(spec: FluidAndServiceResetSpec) {
    _uiState.update { state ->
      state.copy(
        toastMessage = "💻 Routine OBD-II [${spec.obdUdsResetRoutine}] exécutée avec succès sur ${spec.vehicleTitle} : Compteur vidange & échéance DCT remis à 100% !"
      )
    }
  }

  // Option 6 : Pont Direct avec le Passeport QR Universel du Véhicule
  fun syncAndOpenUniversalQrPassportFromCascade() {
    _uiState.update { state ->
      val qrSync = state.qrPassportCascadeSyncRecord
      val rec = state.activeUnifiedApiTrace.matchedVinRecord
      val existingFirst = state.universalQrPassports.first()
      val syncedPassport = existingFirst.copy(
        qrToken = qrSync.passportQrId,
        vehicleVin = qrSync.vin,
        vehiclePlate = qrSync.licensePlateDz,
        brand = rec.brand,
        model = rec.model,
        exactYear = rec.year,
        engineCode = rec.engineCode,
        gearbox = rec.transmissionCode,
        lastServiceDate = "Synchronisé via ${qrSync.winningApiProviderName} • Huile : ${qrSync.certifiedEngineOilGrade}"
      )
      state.copy(
        activeScannedQrPassport = syncedPassport,
        showUnifiedAutoApiHubModal = false,
        showUniversalQrPassportDetailModal = true,
        toastMessage = "📲 Passeport QR Universel [${qrSync.passportQrId}] synchronisé avec la Cascade 11 API (${qrSync.vehicleTitle}) !"
      )
    }
  }

  fun setApiProviderHealthState(
    provider: UnifiedApiConnectorProvider,
    healthState: ApiSourceHealthState
  ) {
    val updatedConfigs = UnifiedAutoApiRepository.updateProviderHealthState(provider, healthState)
    val currentQuery = _uiState.value.activeUnifiedApiTrace.queryInput
    val trace = UnifiedAutoApiRepository.executeUnifiedApiQuery(currentQuery, null, updatedConfigs)
    _uiState.update { state ->
      state.copy(
        fallbackChainConfigs = updatedConfigs,
        activeUnifiedApiTrace = trace,
        toastMessage = "⚙️ ${provider.displayName} → ${healthState.shortBadge} • ${trace.fallbackCascade.cascadeSummaryBanner}"
      )
    }
  }

  fun moveApiProviderPriorityUp(provider: UnifiedApiConnectorProvider) {
    val updatedConfigs = UnifiedAutoApiRepository.moveProviderPriorityUp(provider)
    val currentQuery = _uiState.value.activeUnifiedApiTrace.queryInput
    val trace = UnifiedAutoApiRepository.executeUnifiedApiQuery(currentQuery, null, updatedConfigs)
    _uiState.update { state ->
      state.copy(
        fallbackChainConfigs = updatedConfigs,
        activeUnifiedApiTrace = trace,
        toastMessage = "🔼 Priorité de ${provider.displayName} remontée dans la chaîne de Fallback !"
      )
    }
  }

  fun simulateApiOutageAndFallback() {
    val updatedConfigs = UnifiedAutoApiRepository.applyChaosOutageSimulationPreset()
    val currentQuery = _uiState.value.activeUnifiedApiTrace.queryInput
    val trace = UnifiedAutoApiRepository.executeUnifiedApiQuery(currentQuery, null, updatedConfigs)
    _uiState.update { state ->
      state.copy(
        fallbackChainConfigs = updatedConfigs,
        activeUnifiedApiTrace = trace,
        toastMessage = "🚨 Simulation Panne : #1 SinoAuto360 (503) & #2 Dongchedi (Timeout 504) → Bascule auto réussie sur #3 ${trace.fallbackCascade.winningProvider.displayName} !"
      )
    }
  }

  fun simulateLegacyApisFallback() {
    val updatedConfigs = UnifiedAutoApiRepository.applyLegacyApisFallbackSimulationPreset()
    val targetQuery = "VF3FPHNSSMY001234" // Peugeot 308 III -> test de bascule vers les APIs historiques (#5 ServiceBox/Dialogys/ETKA, #6 TecDoc, #7 HaynesPro...)
    val trace = UnifiedAutoApiRepository.executeUnifiedApiQuery(targetQuery, null, updatedConfigs)
    _uiState.update { state ->
      state.copy(
        fallbackChainConfigs = updatedConfigs,
        activeUnifiedApiTrace = trace,
        toastMessage = "🔄 Bascule vers APIs d'Avant : Nouvelles APIs (#1-#4) en panne → Résolu par #${trace.fallbackCascade.winningStepOrder} ${trace.fallbackCascade.winningProvider.displayName} !"
      )
    }
  }

  fun resetFallbackChainOnline() {
    val updatedConfigs = UnifiedAutoApiRepository.resetFallbackChainToAllOnline()
    val currentQuery = _uiState.value.activeUnifiedApiTrace.queryInput
    val trace = UnifiedAutoApiRepository.executeUnifiedApiQuery(currentQuery, null, updatedConfigs)
    _uiState.update { state ->
      state.copy(
        fallbackChainConfigs = updatedConfigs,
        activeUnifiedApiTrace = trace,
        toastMessage = "✅ Toutes les 11 sources API (5 nouvelles + 6 historiques) ont été rétablies en mode 200 ONLINE !"
      )
    }
  }

  fun applySinoVehicleFromUnifiedApi(record: SinoChineseVinRecord) {
    val vehicle = UnifiedAutoApiRepository.toMekanikVehicle(record)
    val sinoParts = UnifiedAutoApiRepository.toMekanikPartItems(record)
    _uiState.update { state ->
      state.copy(
        activeVehicle = vehicle,
        searchQuery = record.vin,
        selectedSystemCategory = SystemCategory.FREINAGE,
        partsForSelectedCategory = sinoParts.ifEmpty { state.partsForSelectedCategory },
        selectedPart = sinoParts.firstOrNull() ?: state.selectedPart,
        showUnifiedAutoApiHubModal = false,
        currentNavTab = MainNavigationTab.PIECES,
        toastMessage = "🚗 ${record.brand} ${record.model} (${record.vin}) activé avec ses ${sinoParts.size} pièces OEM (SinoAuto360 + Dongchedi) !"
      )
    }
  }

  fun launchReverseEngineeredScenarioInAi(scenario: ReverseEngineeredDemoScenario) {
    val record = UnifiedAutoApiRepository.findSinoRecordByQuery(scenario.vinExample)
    val vehicle = UnifiedAutoApiRepository.toMekanikVehicle(record)
    val sinoParts = UnifiedAutoApiRepository.toMekanikPartItems(record)
    val recommendedParts = scenario.partsList.map { part ->
      DiagnosticRecommendedPart(
        partId = part.partId,
        name = part.partName,
        oemReference = part.oemPartNumber,
        brandName = "${scenario.brand} OEM / ${part.sourceProvider.displayName}",
        equivalentBrands = part.crossReferences,
        systemCategory = SystemCategory.FREINAGE,
        priceEstimatedDzd = part.estimatedPriceDzd,
        availabilityBadge = part.stockAvailability,
        urgencyLevel = "Référence Exacte Arborescence JSON (${part.calloutRef})"
      )
    }

    val aiResult = AiDiagnosticResult(
      query = "${scenario.title} (VIN: ${scenario.vinExample})",
      isObdCode = false,
      faultCode = "JSON-OEM-${scenario.brand.uppercase()}",
      vehicleContext = "${scenario.brand} ${scenario.model} • Moteur ${scenario.engineCode} • VIN ${scenario.vinExample}",
      plainLanguageExplanation = scenario.car2DbLlmDigest,
      analysisSummary = "Résolution via l'API Unique MekanikOne (${scenario.sourcePlatformNames}). Fil d'Ariane Car2DB : ${scenario.car2DbBreadcrumb}. Appel Gemini Function Calling : ${scenario.geminiFunctionCallName}() exécuté avec succès.",
      rootCauses = listOf(
        "Identification VIN Usine Chine (SinoAuto360) : ${record.wmiPrefix}",
        "Spécifications Dongchedi Auto-API : ${record.displacementAndPower} • Boîte ${record.transmissionCode}",
        "Arborescence JSON ChineseCar.parts : Réf OEM principale ${scenario.partsList.firstOrNull()?.oemPartNumber ?: ""}"
      ),
      stepByStepInspection = scenario.stepByStepMechanicalProcedure,
      multimeterSpecs = "Routine UDS OBD-II : 0x3101 (Mode Maintenance Servomoteur EPB Arrière) • Couple colonnettes : 32-35 Nm • Support étrier : 105-115 Nm",
      actuatorTestRecommendation = "Routine UDS 0x3101 — Ouverture servomoteur EPB arrière avant dépose des plaquettes & purge assistée du bloc ESP.",
      recommendedParts = scenario.partsList.map { "${it.partName} (OEM ${it.oemPartNumber})" },
      richRecommendedParts = recommendedParts,
      recommendedTechnicalDocs = listOf(
        DiagnosticTechnicalDoc(
          id = "doc_sino_${scenario.id}_exploded",
          docCode = "SINO360-ASM-${scenario.brand.uppercase()}",
          title = "Vue Éclatée Usine & Couples de Serrage — ${scenario.brand} ${scenario.model}",
          docType = DiagnosticTechDocType.EXPLODED_VIEW_TORQUE,
          summary = "Arborescence JSON ChineseCar.parts (${scenario.car2DbBreadcrumb}) avec repères d'assemblage et couples dynamométriques.",
          keySpecsOrTorque = "Colonnettes d'étrier : 32–35 Nm • Support d'étrier : 110 Nm • Vis de roue : 120 Nm",
          ecuPinoutOrRoutine = "Réf OEM principale : ${scenario.partsList.firstOrNull()?.oemPartNumber ?: "OEM"}"
        ),
        DiagnosticTechnicalDoc(
          id = "doc_sino_${scenario.id}_wiring",
          docCode = "WD-EPB-${scenario.brand.uppercase()}",
          title = "Schéma Électrique Servomoteur EPB & Capteurs ABS/WSS",
          docType = DiagnosticTechDocType.WIRING_PINOUT,
          summary = "Brochage du calculateur ESP/EPB et contrôle de continuité du faisceau de frein de stationnement électrique.",
          keySpecsOrTorque = "Tension servomoteur EPB : 12.0V–14.2V • Courant d'accostage : 8.5A nominal",
          ecuPinoutOrRoutine = "Fusible F22 (30A) Boîtier compartiment moteur • Routine UDS 0x3101"
        )
      ),
      safetyWarning = scenario.specialToolOrObdResetNote,
      severityLevel = "Scénario Démo Certifié OEM",
      estimatedLaborHours = 1.2f
    )

    _uiState.update { state ->
      state.copy(
        activeVehicle = vehicle,
        selectedSystemCategory = SystemCategory.FREINAGE,
        partsForSelectedCategory = sinoParts.ifEmpty { state.partsForSelectedCategory },
        selectedPart = sinoParts.firstOrNull() ?: state.selectedPart,
        aiQuery = "${scenario.title} — VIN ${scenario.vinExample}",
        aiDiagnosticResult = aiResult,
        isAiLoading = false,
        showUnifiedAutoApiHubModal = false,
        currentNavTab = MainNavigationTab.AI_DIAGNOSTIC,
        toastMessage = "🧠 Scénario JSON ${scenario.brand} ${scenario.model} injecté dans MekanikAI avec Function Calling & Réf OEM !"
      )
    }
  }

  // ============================================================================
  // 26. MODULE DE POINTAGE COMPAGNON TEMPS RÉEL (RECHARTS ANALYTICS)
  // ============================================================================
  fun addCompanionClockEntry(
    companionName: String,
    matricule: String,
    specialty: String,
    repairOrderNumber: String,
    vehicleAndPlate: String,
    operationTitle: String,
    laborTier: WinMotorLaborTier,
    soldCentiemes: Int,
    bayName: String
  ) {
    val newEntry = CompanionTimeClockEntry(
      id = "clk_${System.currentTimeMillis()}",
      companionName = companionName.ifBlank { "Karim Benali" },
      companionMatricule = matricule.ifBlank { "COMP-DZ-01" },
      specialty = specialty.ifBlank { "Mécanique Générale" },
      repairOrderNumber = repairOrderNumber.ifBlank { "OR-2026-8990" },
      vehicleAndPlate = vehicleAndPlate.ifBlank { "${_uiState.value.activeVehicle.brand} ${_uiState.value.activeVehicle.model}" },
      operationTitle = operationTitle.ifBlank { "Intervention Mécanique Barèmée" },
      laborTier = laborTier,
      soldCentiemesBarème = soldCentiemes.coerceAtLeast(50),
      clockedCentiemesReal = (soldCentiemes * 0.85).toInt().coerceAtLeast(25),
      status = CompanionClockStatus.EN_COURS,
      bayName = bayName.ifBlank { "Pont Élévateur N°1" }
    )
    _uiState.update { state ->
      state.copy(
        companionClockEntries = listOf(newEntry) + state.companionClockEntries,
        toastMessage = "⏱️ Nouveau pointage compagnon actif : ${newEntry.companionName} sur ${newEntry.bayName} (${newEntry.soldCentiemesBarème} cth) !"
      )
    }
  }

  // ============================================================================
  // 27. ESPACE VENDEUR : GESTION DE STOCK TEMPS RÉEL (FIRESTORE + CATALOGUE)
  // ============================================================================
  fun updateVendorStockQuantityPriceStatus(
    stockId: String,
    newQty: Int,
    newPriceDzd: Int,
    newStatus: VendorStockAvailabilityStatus
  ) {
    _uiState.update { state ->
      val updatedStock = state.vendorStockItems.map { item ->
        if (item.id == stockId) {
          val resolvedStatus = when {
            newQty <= 0 -> VendorStockAvailabilityStatus.RUPTURE
            newQty < 5 && newStatus == VendorStockAvailabilityStatus.EN_STOCK -> VendorStockAvailabilityStatus.STOCK_FAIBLE
            else -> newStatus
          }
          item.copy(
            quantityInStock = newQty.coerceAtLeast(0),
            priceDzd = newPriceDzd.coerceAtLeast(100),
            availabilityStatus = resolvedStatus.id
          )
        } else item
      }
      val target = updatedStock.find { it.id == stockId }
      state.copy(
        vendorStockItems = updatedStock,
        toastMessage = "📦 Stock Temps Réel mis à jour : ${target?.partName ?: ""} → Qté: ${target?.quantityInStock ?: 0} • ${target?.priceDzd ?: 0} DA (${target?.parsedAvailability?.label ?: ""})"
      )
    }
  }

  fun addRealtimeVendorStockItem(
    vendorName: String,
    partName: String,
    oemRef: String,
    iamRef: String,
    qty: Int,
    priceDzd: Int,
    status: VendorStockAvailabilityStatus
  ) {
    val newItem = FirestoreVendorStockItem(
      id = "stk_${System.currentTimeMillis()}",
      userId = "vendor_local_session",
      vendorName = vendorName.ifBlank { "AutoPièces Bab Ezzouar (Alger 16)" },
      partName = partName.ifBlank { "Pièce Détachée Certifiée" },
      oemReference = oemRef.ifBlank { "5Q0 698 151 BR" },
      iamReference = iamRef.ifBlank { "Brembo / Bosch" },
      quantityInStock = qty.coerceAtLeast(0),
      priceDzd = priceDzd.coerceAtLeast(500),
      availabilityStatus = status.id
    )
    _uiState.update { state ->
      state.copy(
        vendorStockItems = listOf(newItem) + state.vendorStockItems,
        toastMessage = "✅ Nouvelle référence [${newItem.oemReference}] publiée en temps réel dans le catalogue !"
      )
    }
  }

  // ============================================================================
  // 28. LES 6 BOUCLIERS DE DÉFENSE & CONTRÔLE D'ACCÈS RBAC FIREBASE AUTH
  // ============================================================================
  fun openSecurityShieldModal() {
    _uiState.update { it.copy(showSecurityShieldModal = true) }
  }

  fun closeSecurityShieldModal() {
    _uiState.update { it.copy(showSecurityShieldModal = false) }
  }

  fun setFirebaseRbacRole(role: FirebaseRbacRole) {
    _uiState.update { state ->
      val log = FirestoreSecurityAuditEvent(
        id = "audit_${System.currentTimeMillis()}",
        userId = "auth_session_uid",
        operatorName = "Opérateur (${role.id})",
        rbacRole = role.id,
        shieldCategory = "BOUCLIER_6_RBAC",
        actionDescription = "Bascule du rôle Firebase Auth vers ${role.title}",
        hmacSha256Seal = SecurityShieldFirestoreRepository.computeHmacSha256Seal("${role.id}|${System.currentTimeMillis()}")
      )
      state.copy(
        currentFirebaseRole = role,
        securityAuditEvents = listOf(log) + state.securityAuditEvents,
        toastMessage = "🔐 Rôle Firebase Auth actif : ${role.title}"
      )
    }
  }

  fun toggleDefenseShield(shieldNumber: Int, enabled: Boolean) {
    _uiState.update { state ->
      val updated = state.defenseShields.map { s ->
        if (s.shieldNumber == shieldNumber) {
          s.copy(
            isEnabled = enabled,
            statusLabel = if (enabled) "BOUCLIER #$shieldNumber ACTIF & VÉRIFIÉ" else "EN PAUSE MAINTENANCE"
          )
        } else s
      }
      state.copy(
        defenseShields = updated,
        toastMessage = "🛡️ Bouclier #$shieldNumber ${if (enabled) "activé" else "mis en pause"} !"
      )
    }
  }

  fun triggerSecurityAuditLog(actionDescription: String) {
    _uiState.update { state ->
      val role = state.currentFirebaseRole
      val log = FirestoreSecurityAuditEvent(
        id = "audit_${System.currentTimeMillis()}",
        userId = "auth_session_uid",
        operatorName = "Opérateur (${role.id})",
        rbacRole = role.id,
        shieldCategory = "BOUCLIER_3_HMAC",
        actionDescription = actionDescription,
        hmacSha256Seal = SecurityShieldFirestoreRepository.computeHmacSha256Seal("$actionDescription|${role.id}|${System.currentTimeMillis()}")
      )
      state.copy(
        securityAuditEvents = listOf(log) + state.securityAuditEvents,
        toastMessage = "📜 Entrée journal scellée HMAC-SHA256 ajoutée avec succès !"
      )
    }
  }

  // ============================================================================
  // 29. FILTRAGE DYNAMIQUE TEMPS RÉEL (VÉHICULES & PIÈCES DÉTACHÉES)
  // ============================================================================
  private fun applyVehicleDynamicFiltersInternal(filter: VehicleDynamicFilterState): List<Vehicle> {
    val q = filter.textQuery.trim().lowercase()
    return MekanikRepository.sampleVehicles.filter { v ->
      val matchesText = q.isEmpty() ||
        v.brand.lowercase().contains(q) ||
        v.model.lowercase().contains(q) ||
        v.engineCode.lowercase().contains(q) ||
        v.vin.lowercase().contains(q) ||
        v.displacement.lowercase().contains(q) ||
        v.platform.lowercase().contains(q)

      val matchesBrand = filter.selectedBrand == "TOUTES" ||
        v.brand.equals(filter.selectedBrand, ignoreCase = true)

      val matchesFuel = when (filter.selectedFuelType) {
        "TOUTES" -> true
        "Diesel" -> v.fuelType.contains("Diesel", ignoreCase = true) || v.displacement.contains("TDI", ignoreCase = true) || v.displacement.contains("HDi", ignoreCase = true) || v.displacement.contains("dCi", ignoreCase = true)
        "Essence" -> v.fuelType.contains("Essence", ignoreCase = true) || v.displacement.contains("TSI", ignoreCase = true) || v.displacement.contains("PureTech", ignoreCase = true) || v.displacement.contains("TCe", ignoreCase = true)
        "Hybride / EV" -> v.isEv || v.fuelType.contains("Électrique", ignoreCase = true) || v.fuelType.contains("Hybride", ignoreCase = true)
        else -> v.fuelType.contains(filter.selectedFuelType, ignoreCase = true)
      }

      val matchesGearbox = when (filter.selectedGearboxType) {
        "TOUTES" -> true
        "Auto / DSG / EAT" -> v.gearbox.contains("DSG", ignoreCase = true) || v.gearbox.contains("EAT", ignoreCase = true) || v.gearbox.contains("Auto", ignoreCase = true) || v.gearbox.contains("DCT", ignoreCase = true) || v.gearbox.contains("EDC", ignoreCase = true)
        "Manuelle (BVM)" -> v.gearbox.contains("BVM", ignoreCase = true) || v.gearbox.contains("Manuelle", ignoreCase = true)
        else -> v.gearbox.contains(filter.selectedGearboxType, ignoreCase = true)
      }

      val matchesYear = when (filter.selectedYearBracket) {
        "TOUTES" -> true
        "2022+" -> v.exactYear >= 2022
        "2019-2021" -> v.exactYear in 2019..2021
        "≤ 2018" -> v.exactYear <= 2018
        else -> true
      }

      val matchesPlatform = filter.selectedPlatform == "TOUTES" ||
        v.platform.equals(filter.selectedPlatform, ignoreCase = true)

      matchesText && matchesBrand && matchesFuel && matchesGearbox && matchesYear && matchesPlatform
    }
  }

  fun updateVehicleDynamicFilter(transform: (VehicleDynamicFilterState) -> VehicleDynamicFilterState) {
    _uiState.update { state ->
      val updatedFilter = transform(state.vehicleDynamicFilter)
      val filteredVehicles = applyVehicleDynamicFiltersInternal(updatedFilter)
      state.copy(
        vehicleDynamicFilter = updatedFilter,
        searchResults = filteredVehicles
      )
    }
  }

  fun resetVehicleDynamicFilters() {
    _uiState.update { state ->
      val cleanFilter = VehicleDynamicFilterState()
      state.copy(
        vehicleDynamicFilter = cleanFilter,
        searchQuery = "",
        searchResults = MekanikRepository.sampleVehicles,
        toastMessage = "🔄 Filtres véhicules réinitialisés (${MekanikRepository.sampleVehicles.size} modèles affichés)"
      )
    }
  }

  private fun applyPartDynamicFiltersInternal(
    category: SystemCategory,
    filter: PartDynamicFilterState,
    liveStock: List<FirestoreVendorStockItem>
  ): List<PartItem> {
    val q = filter.searchQuery.trim().lowercase()
    val basePool = if (filter.searchAllSystems) {
      MekanikRepository.sampleParts
    } else {
      MekanikRepository.sampleParts.filter { it.system == category }
    }

    val filtered = basePool.filter { part ->
      val matchesQuery = q.isEmpty() ||
        part.name.lowercase().contains(q) ||
        part.oemReference.lowercase().contains(q) ||
        part.oemAlternatives.any { it.lowercase().contains(q) } ||
        part.position.lowercase().contains(q) ||
        part.description.lowercase().contains(q) ||
        part.crossReferences.any { it.brand.lowercase().contains(q) || it.reference.lowercase().contains(q) }

      val matchesBrand = filter.selectedBrand == "TOUTES" ||
        part.crossReferences.any { it.brand.equals(filter.selectedBrand, ignoreCase = true) }

      val matchesPosition = when (filter.selectedPosition) {
        "TOUTES" -> true
        "Avant" -> part.position.contains("Avant", ignoreCase = true)
        "Arrière" -> part.position.contains("Arrière", ignoreCase = true)
        "Moteur / Interne" -> part.position.contains("Moteur", ignoreCase = true) ||
          part.position.contains("Courroie", ignoreCase = true) ||
          part.position.contains("Culasse", ignoreCase = true) ||
          part.position.contains("Carter", ignoreCase = true) ||
          part.position.contains("Boîte", ignoreCase = true)
        else -> part.position.contains(filter.selectedPosition, ignoreCase = true)
      }

      val matchesPrice = part.priceEstimatedDzd >= filter.priceBracket.minDzd &&
        part.priceEstimatedDzd <= filter.priceBracket.maxDzd

      val matchesCompatibility = !filter.only100PercentCompatible ||
        part.compatibilityBadge.contains("100%", ignoreCase = true)

      val matchesLiveStock = !filter.onlyInLiveStock || liveStock.any { stock ->
        stock.quantityInStock > 0 && (
          stock.oemReference.equals(part.oemReference, ignoreCase = true) ||
            part.oemAlternatives.any { alt -> alt.equals(stock.oemReference, ignoreCase = true) } ||
            stock.partName.contains(part.name.take(8), ignoreCase = true)
          )
      }

      matchesQuery && matchesBrand && matchesPosition && matchesPrice && matchesCompatibility && matchesLiveStock
    }

    return when (filter.sortOption) {
      PartSortOption.CALLOUT -> filtered.sortedBy { it.schemaCalloutNumber }
      PartSortOption.PRICE_ASC -> filtered.sortedBy { it.priceEstimatedDzd }
      PartSortOption.PRICE_DESC -> filtered.sortedByDescending { it.priceEstimatedDzd }
      PartSortOption.NAME_ASC -> filtered.sortedBy { it.name }
    }
  }

  fun updatePartDynamicFilter(transform: (PartDynamicFilterState) -> PartDynamicFilterState) {
    _uiState.update { state ->
      val updatedFilter = transform(state.partDynamicFilter)
      val filteredParts = applyPartDynamicFiltersInternal(
        category = state.selectedSystemCategory,
        filter = updatedFilter,
        liveStock = state.vendorStockItems
      )
      val nextSelectedPart = if (state.selectedPart != null && filteredParts.any { it.id == state.selectedPart.id }) {
        state.selectedPart
      } else {
        filteredParts.firstOrNull() ?: state.selectedPart
      }
      state.copy(
        partDynamicFilter = updatedFilter,
        partsForSelectedCategory = filteredParts,
        selectedPart = nextSelectedPart,
        selectedHotspotNumber = nextSelectedPart?.schemaCalloutNumber ?: state.selectedHotspotNumber
      )
    }
  }

  fun resetPartDynamicFilters() {
    _uiState.update { state ->
      val cleanFilter = PartDynamicFilterState()
      val defaultParts = MekanikRepository.sampleParts.filter { it.system == state.selectedSystemCategory }
      state.copy(
        partDynamicFilter = cleanFilter,
        partsForSelectedCategory = defaultParts,
        selectedPart = defaultParts.firstOrNull(),
        selectedHotspotNumber = defaultParts.firstOrNull()?.schemaCalloutNumber ?: 1,
        toastMessage = "🔄 Filtres catalogue pièces réinitialisés"
      )
    }
  }

  // ============================================================================
  // 30. GEMINI CHATBOT MULTI-TOURS (GEMINI 3.5 FLASH & GEMINI 3.1 PRO PREVIEW)
  // ============================================================================
  fun openGeminiChatbotModal() {
    _uiState.update { it.copy(showGeminiChatbotModal = true) }
  }

  fun closeGeminiChatbotModal() {
    _uiState.update { it.copy(showGeminiChatbotModal = false) }
  }

  fun onGeminiChatInputChanged(input: String) {
    _uiState.update { it.copy(geminiChatInput = input) }
  }

  fun setGeminiModelOption(option: GeminiModelOption) {
    _uiState.update {
      it.copy(
        selectedGeminiModel = option,
        toastMessage = "🤖 Modèle Gemini actif : ${option.displayName} (${option.badge})"
      )
    }
  }

  fun clearGeminiChatHistory() {
    val v = _uiState.value.activeVehicle
    _uiState.update {
      it.copy(
        geminiChatMessages = listOf(
          GeminiChatMessage(
            id = "msg_reset_${System.currentTimeMillis()}",
            isUser = false,
            text = "🔄 Conversation réinitialisée pour **${v.brand} ${v.model} (${v.exactYear} • ${v.engineCode})**. Quelle opération mécanique ou référence de pièce souhaitez-vous vérifier ?",
            timestamp = "À l'instant",
            modelUsed = it.selectedGeminiModel.modelId,
            vehicleBadge = "${v.brand} ${v.model} • ${v.engineCode}",
            quickFollowUps = listOf(
              "Couples de serrage moteur ${v.engineCode}",
              "Diagnostic code défaut P0299 / P0401",
              "Références freinage avant & arrière"
            )
          )
        ),
        toastMessage = "Conversation Gemini Chatbot réinitialisée"
      )
    }
  }

  fun sendGeminiChatMessage(presetPrompt: String? = null) {
    val promptToSend = (presetPrompt ?: _uiState.value.geminiChatInput).trim()
    if (promptToSend.isEmpty() || _uiState.value.isGeminiChatTyping) return

    val currentVehicle = _uiState.value.activeVehicle
    val currentModel = _uiState.value.selectedGeminiModel
    val userMsg = GeminiChatMessage(
      id = "msg_usr_${System.currentTimeMillis()}",
      isUser = true,
      text = promptToSend,
      timestamp = "À l'instant",
      modelUsed = currentModel.modelId,
      vehicleBadge = "${currentVehicle.brand} ${currentVehicle.model}"
    )

    val historyBeforeCall = _uiState.value.geminiChatMessages
    _uiState.update {
      it.copy(
        geminiChatInput = if (presetPrompt == null) "" else it.geminiChatInput,
        isGeminiChatTyping = true,
        geminiChatMessages = it.geminiChatMessages + userMsg
      )
    }

    viewModelScope.launch {
      val aiReply = aiDiagnosticService.sendChatMessage(
        userPrompt = promptToSend,
        conversationHistory = historyBeforeCall,
        activeVehicle = currentVehicle,
        modelOption = currentModel
      )
      _uiState.update {
        it.copy(
          isGeminiChatTyping = false,
          geminiChatMessages = it.geminiChatMessages + aiReply
        )
      }
    }
  }

  // ============================================================================
  // 31. JOURNAL D'ERREURS SYSTÈME (ERROR LOGGER : CRASHES, CONNEXION API, PERSISTANCE LOCALE & FIREBASE)
  // ============================================================================
  fun setErrorLoggerCategoryFilter(category: SystemErrorCategory?) {
    MekanikErrorLogger.setCategoryFilter(category)
  }

  fun setErrorLoggerSeverityFilter(severity: SystemErrorSeverity?) {
    MekanikErrorLogger.setSeverityFilter(severity)
  }

  fun setErrorLoggerStatusFilter(status: ErrorResolutionStatus?) {
    MekanikErrorLogger.setStatusFilter(status)
  }

  fun setErrorLoggerSearchQuery(query: String) {
    MekanikErrorLogger.setSearchQuery(query)
  }

  fun simulateCrashErrorLog(context: Context? = null) {
    val state = _uiState.value
    val simulatedCrash = IllegalStateException(
      "NullPointerException / State Sync Failure dans ExplodedSchemaCanvas lors du rendu vectoriel haute densité (${state.activeVehicle.brand} ${state.activeVehicle.model})"
    )
    val logEntry = MekanikErrorLogger.logCrash(
      throwable = simulatedCrash,
      moduleSource = "ExplodedSchemaCanvas / Compose Render Thread",
      userEmail = state.superAdminSession.currentOperatorEmail,
      rbacRole = state.currentFirebaseRole,
      context = context
    )
    _uiState.update {
      it.copy(
        toastMessage = "💥 Crash intercepté et enregistré dans Firebase Error Logger (${logEntry.errorCode}) !"
      )
    }
  }

  fun simulateApiConnectionFailureLog(context: Context? = null) {
    val state = _uiState.value
    val logEntry = MekanikErrorLogger.logApiConnectionError(
      apiName = "Cascade 11 APIs • PartsTech (#7) & Nexpart (#8)",
      endpoint = "https://api.partstech.com/v2/catalog/vin/${state.activeVehicle.vin}",
      httpCodeOrError = "HTTP_503_SERVICE_UNAVAILABLE",
      technicalDetail = "Délai d'attente dépassé (SocketTimeout 4000ms) sur le noeud B2B PartsTech pour ${state.activeVehicle.brand} ${state.activeVehicle.model}. Bascule immédiate sur NHTSA vPIC + Cache Room AES-256.",
      severity = SystemErrorSeverity.HIGH,
      userEmail = state.superAdminSession.currentOperatorEmail,
      rbacRole = state.currentFirebaseRole,
      context = context
    )
    _uiState.update {
      it.copy(
        toastMessage = "🌐 Problème de connexion API journalisé dans Firebase (${logEntry.errorCode}) !"
      )
    }
  }

  fun simulateLocalPersistenceFailureLog(context: Context? = null) {
    val state = _uiState.value
    val logEntry = MekanikErrorLogger.logLocalPersistenceOrFirebaseError(
      storageEngine = "Room SQLCipher AES-256 & Firestore Offline Queue",
      operation = "WRITE_TRANSACTION",
      pathOrTable = "system_error_logs / vendor_stock",
      exceptionMessage = "Échec de validation d'écriture locale ou conflit de verrouillage SQLite WAL lors de la synchronisation hors-ligne.",
      stackTraceOrJson = "{\n  \"storage\": \"Room SQLCipher AES-256-GCM + Firestore Enterprise\",\n  \"operation\": \"BATCH_COMMIT\",\n  \"vehicleVin\": \"${state.activeVehicle.vin}\",\n  \"status\": \"QUEUED_FOR_RETRY\"\n}",
      severity = SystemErrorSeverity.HIGH,
      userEmail = state.superAdminSession.currentOperatorEmail,
      rbacRole = state.currentFirebaseRole,
      context = context
    )
    _uiState.update {
      it.copy(
        toastMessage = "💾 Échec de persistance locale/Firebase capturé dans le journal SuperAdmin (${logEntry.id}) !"
      )
    }
  }

  fun updateErrorLogStatusAndNote(
    errorId: String,
    status: ErrorResolutionStatus,
    note: String,
    context: Context? = null
  ) {
    MekanikErrorLogger.updateErrorStatusAndNote(
      errorId = errorId,
      newStatus = status,
      superAdminNote = note,
      context = context
    )
    _uiState.update {
      it.copy(
        toastMessage = "✅ Statut du log [$errorId] mis à jour : ${status.badgeText}"
      )
    }
  }

  fun deleteErrorLogEntry(errorId: String, context: Context? = null) {
    MekanikErrorLogger.deleteErrorLog(errorId, context)
    _uiState.update {
      it.copy(
        toastMessage = "🗑️ Entrée $errorId supprimée du journal SuperAdmin"
      )
    }
  }

  fun purgeResolvedErrorLogs(context: Context? = null) {
    MekanikErrorLogger.clearResolvedLogs(context)
    _uiState.update {
      it.copy(
        toastMessage = "🧹 Toutes les erreurs résolues/ignorées ont été purgées !"
      )
    }
  }

  fun openClientBugAiRepairModal() {
    _isClientBugAiRepairModalOpen.value = true
  }

  fun closeClientBugAiRepairModal() {
    _isClientBugAiRepairModalOpen.value = false
  }

  fun toggleAiAutoRepair(enabled: Boolean) {
    MekanikErrorLogger.setAiAutoRepairEnabled(enabled)
    _uiState.update {
      it.copy(
        toastMessage = if (enabled) {
          "🤖 MekanikAI Guardian 24/7 activé : Réparation automatique immédiate des plantages & bugs clients !"
        } else {
          "⏸️ Auto-Réparation IA passée en mode manuel SuperAdmin."
        }
      )
    }
  }

  fun repairSingleErrorWithAi(errorId: String, context: Context? = null) {
    MekanikErrorLogger.repairSingleErrorWithAi(errorId, context)
    _uiState.update {
      it.copy(
        toastMessage = "🤖 MekanikAI Guardian a lancé le diagnostic et le Hot-Patch sur [$errorId]..."
      )
    }
  }

  fun repairAllOpenErrorsWithAi(context: Context? = null) {
    MekanikErrorLogger.repairAllOpenErrorsWithAi(context)
    _uiState.update {
      it.copy(
        toastMessage = "⚡ MekanikAI Guardian répare automatiquement tous les incidents ouverts..."
      )
    }
  }

  fun submitClientBugReportAndAutoRepair(
    moduleSource: String,
    bugTitle: String,
    bugDescription: String,
    severity: SystemErrorSeverity,
    context: Context? = null
  ) {
    val state = _uiState.value
    val logEntry = MekanikErrorLogger.submitClientBugReport(
      clientEmail = state.superAdminSession.currentOperatorEmail,
      rbacRole = state.currentFirebaseRole,
      moduleSource = moduleSource,
      bugTitle = bugTitle,
      bugDescription = bugDescription,
      severity = severity,
      forceImmediateAiRepair = true,
      context = context
    )
    _uiState.update {
      it.copy(
        toastMessage = "🤖 Bug client reçu (${logEntry.errorCode}) : MekanikAI Guardian applique le correctif automatiquement !"
      )
    }
  }

  fun simulateClientBugReport(context: Context? = null) {
    val preset = MekanikErrorLogger.clientBugPresets.random()
    submitClientBugReportAndAutoRepair(
      moduleSource = preset.moduleSource,
      bugTitle = preset.title,
      bugDescription = preset.defaultDescription,
      severity = preset.suggestedSeverity,
      context = context
    )
  }

  // ============================================================================
  // 32. PANIER INTELLIGENT SCHÉMA ÉCLATÉ, ACCÈS LIBRE SANS COMPTE, GOOGLE MAPS & COMPTE PARTICULIER (3 500 DA/AN)
  // ============================================================================
  fun openSmartCartFreeAccessMapModal(initialTab: Int = 0) {
    _smartCartModalInitialTab.value = initialTab
    _isSmartCartFreeAccessMapModalOpen.value = true
  }

  fun closeSmartCartFreeAccessMapModal() {
    _isSmartCartFreeAccessMapModalOpen.value = false
  }

  fun addPartFromExplodedSchemaToSmartCart(part: PartItem) {
    val activeVehicle = _uiState.value.activeVehicle
    SmartCartAndFreeAccessRepository.addPartFromCatalogExplodedSchema(part, activeVehicle)
    _smartCartModalInitialTab.value = 1
    _isSmartCartFreeAccessMapModalOpen.value = true
    _uiState.update {
      it.copy(
        toastMessage = "🛒 « ${part.name} » (${part.oemReference}) ajouté au Panier Schéma Éclaté avec ses compatibilités et stocks !"
      )
    }
  }

  fun addCalloutFromWebExplodedToSmartCart(
    callout: ExplodedCalloutItem,
    sheet: OemExplodedDiagramSheet
  ) {
    SmartCartAndFreeAccessRepository.addCalloutFromWebExplodedDiagram(callout, sheet)
    _smartCartModalInitialTab.value = 1
    _isSmartCartFreeAccessMapModalOpen.value = true
    _uiState.update {
      it.copy(
        toastMessage = "🛒 Repère #${callout.calloutNumber} (${callout.oemReference}) ajouté au Panier de Compatibilités & Stock !"
      )
    }
  }

  fun updateSmartCartQuantity(itemId: String, delta: Int) {
    SmartCartAndFreeAccessRepository.updateSmartCartQuantity(itemId, delta)
  }

  fun removeSmartCartItem(itemId: String) {
    SmartCartAndFreeAccessRepository.removeSmartCartItem(itemId)
  }

  fun addExtraVinToParticulier(vin: String, vehicleLabel: String, engineCode: String, year: Int) {
    SmartCartAndFreeAccessRepository.addExtraVinToParticulierAccount(vin, vehicleLabel, engineCode, year)
    _uiState.update {
      it.copy(
        toastMessage = "🚗 Nouveau châssis ($vin) ajouté au Compte Particulier (+1 200 DA/an) !"
      )
    }
  }

  fun removeExtraVinFromParticulier(vin: String) {
    SmartCartAndFreeAccessRepository.removeExtraVinFromParticulierAccount(vin)
  }

  fun selectParticulierPlanTier(tier: ParticulierPlanTier) {
    SmartCartAndFreeAccessRepository.selectParticulierPlanTier(tier)
    _uiState.update {
      it.copy(
        toastMessage = "✅ Formule ${tier.title} (${tier.annualPriceDzd} DA/an) sélectionnée !"
      )
    }
  }

  // ============================================================================
  // 33. STRATÉGIE DE MISE EN CACHE LOCALE ROBUSTE (ROOM SQLITE) :
  //     SCHÉMAS TECHNIQUES, DIAGNOSTICS ENREGISTRÉS & HISTORIQUE PANNES CLIENTS
  // ============================================================================
  fun openOfflineCacheHubModal(initialTab: OfflineCacheHubTab = OfflineCacheHubTab.CLIENT_FAULTS_HISTORY) {
    _offlineCacheHubInitialTab.value = initialTab
    _isOfflineCacheHubModalOpen.value = true
  }

  fun closeOfflineCacheHubModal() {
    _isOfflineCacheHubModalOpen.value = false
  }

  fun setWorkshopNetworkCondition(condition: WorkshopNetworkCondition) {
    WorkshopOfflineCacheRepository.setNetworkCondition(condition)
    _uiState.update {
      it.copy(
        isOfflineModeEnabled = condition != WorkshopNetworkCondition.OPTIMAL_ONLINE,
        toastMessage = "📡 Mode Réseau Atelier : ${condition.label}"
      )
    }
  }

  fun cycleWorkshopNetworkCondition() {
    val entries = WorkshopNetworkCondition.entries
    val nextIndex = (entries.indexOf(workshopNetworkCondition.value) + 1) % entries.size
    setWorkshopNetworkCondition(entries[nextIndex])
  }

  fun cacheWiringDiagramLocally(diagram: WiringDiagramDoc) {
    val vehicle = _uiState.value.activeVehicle
    WorkshopOfflineCacheRepository.cacheWiringDiagramLocally(
      diagram = diagram,
      vehicleBrand = vehicle.brand,
      vehicleModel = vehicle.model,
      engineCode = vehicle.engineCode
    )
    _uiState.update {
      it.copy(
        toastMessage = "⚡ Schéma « ${diagram.title} » enregistré en cache local Room SQLite (0 ms) !"
      )
    }
  }

  fun cacheProcedureLocally(proc: RepairManualProcedure) {
    val vehicle = _uiState.value.activeVehicle
    WorkshopOfflineCacheRepository.cacheProcedureLocally(
      proc = proc,
      vehicleBrand = vehicle.brand,
      vehicleModel = vehicle.model,
      engineCode = vehicle.engineCode
    )
    _uiState.update {
      it.copy(
        toastMessage = "📘 Manuel « ${proc.title} » enregistré en cache local Room SQLite (0 ms) !"
      )
    }
  }

  fun preloadAllSchematicsToLocalCache() {
    val vehicle = _uiState.value.activeVehicle
    val total = WorkshopOfflineCacheRepository.preloadAllRepositorySchematicsToLocalCache(
      activeBrand = vehicle.brand,
      activeModel = vehicle.model,
      activeEngineCode = vehicle.engineCode
    )
    _uiState.update {
      it.copy(
        toastMessage = "✅ $total schémas électriques et manuels OEM mis en cache local SQLite (lecture 0 ms hors-ligne) !"
      )
    }
  }

  fun togglePinSchematic(id: String) {
    WorkshopOfflineCacheRepository.togglePinSchematic(id)
  }

  fun removeSchematicFromCache(id: String) {
    WorkshopOfflineCacheRepository.removeSchematicFromCache(id)
    _uiState.update {
      it.copy(toastMessage = "🗑️ Schéma retiré du cache local.")
    }
  }

  fun saveCurrentAiDiagnosticToLocalCache(result: AiDiagnosticResult) {
    val vehicle = _uiState.value.activeVehicle
    val activeClient = _uiState.value.activeClientPortalAccount
    val saved = WorkshopOfflineCacheRepository.saveAiDiagnosticToLocalCache(
      result = result,
      vehicleModel = "${vehicle.brand} ${vehicle.model} (${vehicle.engineCode})",
      vin = vehicle.vin,
      licensePlate = activeClient?.plateNumber ?: "04291-120-16",
      clientName = activeClient?.clientName ?: "Client Atelier (${vehicle.brand})"
    )
    _uiState.update {
      it.copy(
        toastMessage = "💾 Diagnostic ${saved.dtcCode} enregistré dans le cache local Room SQLite & dossier client !"
      )
    }
  }

  fun saveObdFaultToLocalCache(fault: OBDFault) {
    val vehicle = _uiState.value.activeVehicle
    val activeClient = _uiState.value.activeClientPortalAccount
    val saved = WorkshopOfflineCacheRepository.saveObdFaultToLocalCache(
      fault = fault,
      vehicleModel = "${vehicle.brand} ${vehicle.model} (${vehicle.engineCode})",
      vin = vehicle.vin,
      licensePlate = activeClient?.plateNumber ?: "04291-120-16",
      clientName = activeClient?.clientName ?: "Client Atelier (${vehicle.brand})"
    )
    _uiState.update {
      it.copy(
        toastMessage = "💾 Fiche diagnostic OBD ${saved.dtcCode} enregistrée en cache local hors-ligne (0 ms) !"
      )
    }
  }

  fun syncPendingDiagnostics() {
    WorkshopOfflineCacheRepository.syncAllPendingDiagnostics()
    _uiState.update {
      it.copy(toastMessage = "☁️ Tous les diagnostics enregistrés en local ont été synchronisés avec le Cloud !")
    }
  }

  fun deleteSavedDiagnostic(id: String) {
    WorkshopOfflineCacheRepository.deleteSavedDiagnostic(id)
    _uiState.update {
      it.copy(toastMessage = "🗑️ Diagnostic supprimé du cache local.")
    }
  }

  fun addClientFaultHistoryRecord(
    clientName: String,
    clientPhone: String,
    vehicleModel: String,
    vin: String,
    licensePlate: String,
    mileageKm: Int,
    faultTitle: String,
    symptoms: String,
    affectedSystem: String,
    dtcCodes: String,
    diagnosticConclusion: String,
    repairAction: String,
    partsReplaced: String,
    costDzd: Long,
    technicianName: String,
    status: String
  ) {
    val record = WorkshopOfflineCacheRepository.addClientFaultHistoryRecord(
      clientName = clientName,
      clientPhone = clientPhone,
      vehicleModel = vehicleModel,
      vin = vin,
      licensePlate = licensePlate,
      mileageKm = mileageKm,
      faultTitle = faultTitle,
      customerReportedSymptoms = symptoms,
      affectedSystem = affectedSystem,
      linkedDtcCodes = dtcCodes,
      diagnosticConclusion = diagnosticConclusion,
      repairActionTaken = repairAction,
      partsReplaced = partsReplaced,
      totalCostDzd = costDzd,
      technicianName = technicianName,
      status = status
    )
    _uiState.update {
      it.copy(
        toastMessage = "✅ Panne client « ${record.faultTitle} » (${record.clientName}) enregistrée en base locale Room !"
      )
    }
  }

  fun updateClientFaultStatus(id: String, newStatus: String) {
    WorkshopOfflineCacheRepository.updateClientFaultStatus(id, newStatus)
  }

  fun deleteClientFaultRecord(id: String) {
    WorkshopOfflineCacheRepository.deleteClientFaultRecord(id)
    _uiState.update {
      it.copy(toastMessage = "🗑️ Entrée historique panne supprimée.")
    }
  }

  // --- Scanner Vision Étiquette Pièce (Code-Barres / QR / Compatibilité Châssis & Montage) ---
  private val _isPartLabelVisionScannerOpen = MutableStateFlow(false)
  val isPartLabelVisionScannerOpen: StateFlow<Boolean> = _isPartLabelVisionScannerOpen.asStateFlow()

  fun openPartLabelVisionScanner() {
    _isPartLabelVisionScannerOpen.value = true
  }

  fun closePartLabelVisionScanner() {
    _isPartLabelVisionScannerOpen.value = false
  }

  fun openScannedPartInCatalog(part: PartItem, category: SystemCategory) {
    _isPartLabelVisionScannerOpen.value = false
    updateCategory(category)
    selectPartItem(part)
    setNavigationTab(MainNavigationTab.PIECES)
    _uiState.update {
      it.copy(
        toastMessage = "📦 Pièce ${part.oemReference} ouverte dans le catalogue DMS Mécanicauto+ !"
      )
    }
  }
}



