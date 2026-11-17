package com.example.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.data.local.CachedTechnicalSchematicEntity
import com.example.data.local.ClientFaultHistoryEntity
import com.example.data.local.MekanikDatabase
import com.example.data.local.SavedDiagnosticRecordEntity
import com.example.data.local.VehicleRepairLogDao
import com.example.data.local.VehicleRepairLogEntity
import com.example.data.local.WorkshopLocalCacheDao
import com.example.data.model.AiDiagnosticResult
import com.example.data.model.OBDFault
import com.example.data.model.RepairManualProcedure
import com.example.data.model.WiringDiagramDoc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class WorkshopNetworkCondition(
  val label: String,
  val shortBadge: String,
  val latencyLabel: String,
  val description: String
) {
  OPTIMAL_ONLINE(
    label = "Réseau 4G/Wi-Fi Optimal + Cache Actif",
    shortBadge = "CONNECTÉ + CACHE 0ms",
    latencyLabel = "Lecture SQLite 0 ms • Sync Cloud Active",
    description = "Les schémas et diagnostics consultés sont automatiquement mis en cache local SQLite Room."
  ),
  WEAK_WORKSHOP_SIGNAL(
    label = "Faible Connexion Atelier (Priorité Cache Local)",
    shortBadge = "CONNEXION FAIBLE • CACHE PRIORITAIRE",
    latencyLabel = "Lecture Locale < 2 ms • Économie Bande Passante",
    description = "Bascule intelligente sur le cache Room SQLite pour les schémas électriques, couples et pannes clients sans attente réseau."
  ),
  OFFLINE_BASEMENT(
    label = "Hors-Ligne Intégral (Sous-Sol / Zone Blanche)",
    shortBadge = "100% HORS-LIGNE • ROOM SQLITE",
    latencyLabel = "Autonomie Locale Totale (0 ms)",
    description = "Consultation et enregistrement 100% hors-ligne des schémas, diagnostics et pannes clients avec file de synchronisation différée."
  )
}

data class LocalCacheStorageSummary(
  val cachedSchematicsCount: Int = 0,
  val pinnedSchematicsCount: Int = 0,
  val savedDiagnosticsCount: Int = 0,
  val unsyncedDiagnosticsCount: Int = 0,
  val clientFaultsCount: Int = 0,
  val resolvedFaultsCount: Int = 0,
  val totalStorageSizeKb: Int = 0,
  val lastCacheSyncFormatted: String = "Aujourd'hui • SQLite Room v2"
)

/**
 * Repository centralisant la stratégie de mise en cache locale robuste (Room SQLite)
 * pour les schémas techniques, les données de diagnostic enregistrées, l'historique client
 * et le registre des pannes des clients.
 */
object WorkshopOfflineCacheRepository {

  private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  @Volatile
  private var cacheDao: WorkshopLocalCacheDao? = null

  @Volatile
  private var repairLogDao: VehicleRepairLogDao? = null

  @Volatile
  private var isRoomInitialized = false

  private val _networkCondition = MutableStateFlow(WorkshopNetworkCondition.WEAK_WORKSHOP_SIGNAL)
  val networkCondition: StateFlow<WorkshopNetworkCondition> = _networkCondition.asStateFlow()

  private val _cachedSchematics = MutableStateFlow(defaultInitialSchematics())
  val cachedSchematics: StateFlow<List<CachedTechnicalSchematicEntity>> = _cachedSchematics.asStateFlow()

  private val _savedDiagnostics = MutableStateFlow(defaultInitialSavedDiagnostics())
  val savedDiagnostics: StateFlow<List<SavedDiagnosticRecordEntity>> = _savedDiagnostics.asStateFlow()

  private val _clientFaultHistory = MutableStateFlow(defaultInitialClientFaultHistory())
  val clientFaultHistory: StateFlow<List<ClientFaultHistoryEntity>> = _clientFaultHistory.asStateFlow()

  private val _vehicleRepairLogs = MutableStateFlow(defaultInitialRepairLogs())
  val vehicleRepairLogs: StateFlow<List<VehicleRepairLogEntity>> = _vehicleRepairLogs.asStateFlow()

  private val _cacheSummary = MutableStateFlow(computeSummary(
    schematics = _cachedSchematics.value,
    diagnostics = _savedDiagnostics.value,
    faults = _clientFaultHistory.value
  ))
  val cacheSummary: StateFlow<LocalCacheStorageSummary> = _cacheSummary.asStateFlow()

  /**
   * Initialise la base de données Room SQLite avec le Context Android,
   * pré-remplit le cache local lors du premier lancement et observe les Flows Room.
   */
  fun initializeWithContext(context: Context) {
    if (isRoomInitialized) return
    synchronized(this) {
      if (isRoomInitialized) return
      try {
        val db = MekanikDatabase.getDatabase(context.applicationContext)
        val localDao = db.workshopLocalCacheDao()
        val logDao = db.vehicleRepairLogDao()
        cacheDao = localDao
        repairLogDao = logDao
        isRoomInitialized = true

        // Détection initiale de l'état réseau réel du téléphone
        detectDeviceNetworkCondition(context)

        repositoryScope.launch {
          // Pré-remplissage initial si la base locale est vide
          if (localDao.getCachedSchematicsCount() == 0) {
            localDao.upsertAllCachedSchematics(defaultInitialSchematics())
          }
          if (localDao.getSavedDiagnosticsCount() == 0) {
            localDao.upsertAllSavedDiagnostics(defaultInitialSavedDiagnostics())
          }
          if (localDao.getClientFaultHistoryCount() == 0) {
            localDao.upsertAllClientFaultRecords(defaultInitialClientFaultHistory())
          }
          logDao.insertAllRepairLogs(defaultInitialRepairLogs())
        }

        // Observation réactive des Flows Room -> StateFlow
        repositoryScope.launch {
          localDao.getAllCachedSchematics().collect { list ->
            if (list.isNotEmpty()) {
              _cachedSchematics.value = list
              refreshSummary()
            }
          }
        }
        repositoryScope.launch {
          localDao.getAllSavedDiagnostics().collect { list ->
            if (list.isNotEmpty()) {
              _savedDiagnostics.value = list
              refreshSummary()
            }
          }
        }
        repositoryScope.launch {
          localDao.getAllClientFaultHistory().collect { list ->
            if (list.isNotEmpty()) {
              _clientFaultHistory.value = list
              refreshSummary()
            }
          }
        }
        repositoryScope.launch {
          logDao.getAllRepairLogs().collect { list ->
            if (list.isNotEmpty()) {
              _vehicleRepairLogs.value = list
            }
          }
        }
      } catch (_: Exception) {
        // Fallback sécurisé en mémoire (ex: tests JVM unitaires sans contexte SQLite natif)
      }
    }
  }

  fun detectDeviceNetworkCondition(context: Context) {
    try {
      val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
      val activeNet = cm?.activeNetwork
      val caps = if (activeNet != null) cm.getNetworkCapabilities(activeNet) else null
      if (caps == null || !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
        _networkCondition.value = WorkshopNetworkCondition.OFFLINE_BASEMENT
      } else if (caps.linkDownstreamBandwidthKbps in 1..1500) {
        _networkCondition.value = WorkshopNetworkCondition.WEAK_WORKSHOP_SIGNAL
      }
    } catch (_: Exception) {
      // Ignore security or service exceptions
    }
  }

  fun setNetworkCondition(condition: WorkshopNetworkCondition) {
    _networkCondition.value = condition
    refreshSummary()
  }

  private fun refreshSummary() {
    _cacheSummary.value = computeSummary(
      schematics = _cachedSchematics.value,
      diagnostics = _savedDiagnostics.value,
      faults = _clientFaultHistory.value
    )
  }

  private fun computeSummary(
    schematics: List<CachedTechnicalSchematicEntity>,
    diagnostics: List<SavedDiagnosticRecordEntity>,
    faults: List<ClientFaultHistoryEntity>
  ): LocalCacheStorageSummary {
    val totalKb = schematics.sumOf { it.payloadSizeKb } + (diagnostics.size * 38) + (faults.size * 24)
    val nowFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(Date())
    return LocalCacheStorageSummary(
      cachedSchematicsCount = schematics.size,
      pinnedSchematicsCount = schematics.count { it.isPinnedOffline },
      savedDiagnosticsCount = diagnostics.size,
      unsyncedDiagnosticsCount = diagnostics.count { !it.isSyncedToCloud },
      clientFaultsCount = faults.size,
      resolvedFaultsCount = faults.count { it.status == "PANNE_RESOLUE" },
      totalStorageSizeKb = totalKb,
      lastCacheSyncFormatted = "Mis à jour $nowFormatted • SQLite Room"
    )
  }

  // ==========================================================================
  // ACTIONS : MISE EN CACHE DES SCHÉMAS TECHNIQUES & MANUELS
  // ==========================================================================
  fun isSchematicCached(id: String): Boolean {
    return _cachedSchematics.value.any { it.id == id }
  }

  fun cacheWiringDiagramLocally(diagram: WiringDiagramDoc, vehicleBrand: String, vehicleModel: String, engineCode: String) {
    val entity = CachedTechnicalSchematicEntity(
      id = diagram.id,
      title = diagram.title,
      category = "SCHEMA_ELECTRIQUE",
      vehicleBrand = vehicleBrand,
      vehicleModel = vehicleModel,
      engineCode = engineCode,
      systemName = diagram.circuitName,
      schemaReferenceCode = diagram.schemaDiagramRef,
      ecuPinoutOrSpecs = diagram.ecuPins,
      fuseBoxAndRelays = "${diagram.fuseBoxLocation} • ${diagram.relayCodes}",
      wireColorsOrSteps = diagram.wireColorCodes.joinToString("\n"),
      torqueSpecsSummary = "Serrage bornier masse : 9 Nm",
      requiredToolsSummary = "Multimètre numérique, Schéma brochage OEM, Sonde oscilloscope",
      warningNotes = "Débrancher la borne négative batterie 12V avant toute mesure de continuité sur calculateur.",
      payloadSizeKb = 210,
      isPinnedOffline = true,
      cachedAtFormatted = "Mis en cache local (0 ms)"
    )
    upsertSchematicInternal(entity)
  }

  fun cacheProcedureLocally(proc: RepairManualProcedure, vehicleBrand: String, vehicleModel: String, engineCode: String) {
    val entity = CachedTechnicalSchematicEntity(
      id = proc.id,
      title = proc.title,
      category = "PROCEDURE_MANUEL",
      vehicleBrand = vehicleBrand,
      vehicleModel = vehicleModel,
      engineCode = engineCode,
      systemName = proc.systemCategory.label,
      schemaReferenceCode = "OEM-PROC-${proc.id.uppercase()}",
      ecuPinoutOrSpecs = "Temps barème : ${proc.estimatedTimeHours}h • Niveau : ${proc.difficultyLevel}",
      fuseBoxAndRelays = "Compatibilité : ${proc.vehicleCompatibility}",
      wireColorsOrSteps = proc.stepByStepInstructions.joinToString("\n"),
      torqueSpecsSummary = proc.torqueSpecs.joinToString(" | ").ifBlank { "Couples standards constructeur" },
      requiredToolsSummary = proc.requiredTools.joinToString(", "),
      warningNotes = proc.warningNote ?: "",
      payloadSizeKb = 340,
      isPinnedOffline = true,
      cachedAtFormatted = "Mis en cache local (0 ms)"
    )
    upsertSchematicInternal(entity)
  }

  fun upsertSchematicInternal(entity: CachedTechnicalSchematicEntity) {
    val updated = listOf(entity) + _cachedSchematics.value.filterNot { it.id == entity.id }
    _cachedSchematics.value = updated
    refreshSummary()
    repositoryScope.launch {
      cacheDao?.upsertCachedSchematic(entity)
    }
  }

  fun togglePinSchematic(id: String) {
    val current = _cachedSchematics.value.find { it.id == id } ?: return
    val newPinned = !current.isPinnedOffline
    val updated = _cachedSchematics.value.map {
      if (it.id == id) it.copy(isPinnedOffline = newPinned, lastAccessedTimestamp = System.currentTimeMillis()) else it
    }
    _cachedSchematics.value = updated
    refreshSummary()
    repositoryScope.launch {
      cacheDao?.updateSchematicPinStatus(id, newPinned)
    }
  }

  fun removeSchematicFromCache(id: String) {
    _cachedSchematics.value = _cachedSchematics.value.filterNot { it.id == id }
    refreshSummary()
    repositoryScope.launch {
      cacheDao?.deleteCachedSchematicById(id)
    }
  }

  /**
   * Pré-charge en un clic tous les schémas électriques et manuels du catalogue MekanikRepository
   * dans la base locale Room SQLite pour une disponibilité 100% hors-ligne.
   */
  fun preloadAllRepositorySchematicsToLocalCache(
    activeBrand: String,
    activeModel: String,
    activeEngineCode: String
  ): Int {
    val wiringEntities = MekanikRepository.wiringDiagrams.map { diag ->
      CachedTechnicalSchematicEntity(
        id = diag.id,
        title = diag.title,
        category = "SCHEMA_ELECTRIQUE",
        vehicleBrand = activeBrand,
        vehicleModel = activeModel,
        engineCode = activeEngineCode,
        systemName = diag.circuitName,
        schemaReferenceCode = diag.schemaDiagramRef,
        ecuPinoutOrSpecs = diag.ecuPins,
        fuseBoxAndRelays = "${diag.fuseBoxLocation} • ${diag.relayCodes}",
        wireColorsOrSteps = diag.wireColorCodes.joinToString("\n"),
        torqueSpecsSummary = "Serrage bornier masse : 9 Nm",
        requiredToolsSummary = "Multimètre numérique, Schéma brochage OEM",
        warningNotes = "Couper le contact avant dépose connecteur calculateur.",
        payloadSizeKb = 215,
        isPinnedOffline = true,
        cachedAtFormatted = "Pack Atelier Pré-chargé"
      )
    }
    val procEntities = MekanikRepository.technicalProcedures.map { proc ->
      CachedTechnicalSchematicEntity(
        id = proc.id,
        title = proc.title,
        category = "PROCEDURE_MANUEL",
        vehicleBrand = activeBrand,
        vehicleModel = activeModel,
        engineCode = activeEngineCode,
        systemName = proc.systemCategory.label,
        schemaReferenceCode = "OEM-PROC-${proc.id.uppercase()}",
        ecuPinoutOrSpecs = "Durée : ${proc.estimatedTimeHours}h • ${proc.difficultyLevel}",
        fuseBoxAndRelays = proc.vehicleCompatibility,
        wireColorsOrSteps = proc.stepByStepInstructions.joinToString("\n"),
        torqueSpecsSummary = proc.torqueSpecs.joinToString(" | "),
        requiredToolsSummary = proc.requiredTools.joinToString(", "),
        warningNotes = proc.warningNote ?: "",
        payloadSizeKb = 320,
        isPinnedOffline = true,
        cachedAtFormatted = "Pack Atelier Pré-chargé"
      )
    }

    val mergedMap = LinkedHashMap<String, CachedTechnicalSchematicEntity>()
    (wiringEntities + procEntities + _cachedSchematics.value).forEach { item ->
      mergedMap[item.id] = item
    }
    val mergedList = mergedMap.values.toList()
    _cachedSchematics.value = mergedList
    refreshSummary()

    repositoryScope.launch {
      cacheDao?.upsertAllCachedSchematics(mergedList)
    }
    return mergedList.size
  }

  // ==========================================================================
  // ACTIONS : DIAGNOSTICS ENREGISTRÉS (OBD-II & MEKANIKAI)
  // ==========================================================================
  fun saveAiDiagnosticToLocalCache(
    result: AiDiagnosticResult,
    vehicleModel: String,
    vin: String,
    licensePlate: String,
    clientName: String = "Client Atelier"
  ): SavedDiagnosticRecordEntity {
    val nowDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(Date())
    val code = result.faultCode ?: "DIAG-IA"
    val entity = SavedDiagnosticRecordEntity(
      id = "DIAG-${code}-${System.currentTimeMillis().toString().takeLast(5)}",
      dtcCode = code,
      diagnosticTitle = result.query.ifBlank { "Diagnostic $code - $vehicleModel" },
      queryOrSymptom = result.query,
      vehicleModel = vehicleModel,
      vin = vin,
      licensePlate = licensePlate,
      clientName = clientName,
      severityLevel = result.severityLevel.uppercase(),
      plainExplanation = result.plainLanguageExplanation,
      technicalSummary = result.analysisSummary,
      rootCausesJoined = result.rootCauses.joinToString("\n"),
      inspectionStepsJoined = result.stepByStepInspection.joinToString("\n"),
      multimeterSpecs = result.multimeterSpecs ?: "Contrôle tension batterie 12.6V / CAN-Bus 60 Ω",
      recommendedPartsJoined = if (result.richRecommendedParts.isNotEmpty()) {
        result.richRecommendedParts.joinToString("\n") { "${it.name} (Réf OEM : ${it.oemReference}) • ${it.priceEstimatedDzd} DA" }
      } else {
        result.recommendedParts.joinToString("\n")
      },
      estimatedLaborHours = result.estimatedLaborHours,
      isSyncedToCloud = _networkCondition.value == WorkshopNetworkCondition.OPTIMAL_ONLINE,
      formattedDate = nowDate,
      createdAtTimestamp = System.currentTimeMillis()
    )
    upsertSavedDiagnosticInternal(entity)
    return entity
  }

  fun saveObdFaultToLocalCache(
    fault: OBDFault,
    vehicleModel: String,
    vin: String,
    licensePlate: String,
    clientName: String = "Client Atelier"
  ): SavedDiagnosticRecordEntity {
    val nowDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(Date())
    val entity = SavedDiagnosticRecordEntity(
      id = "DIAG-${fault.code}-${System.currentTimeMillis().toString().takeLast(5)}",
      dtcCode = fault.code,
      diagnosticTitle = "${fault.code} — ${fault.frenchDescription}",
      queryOrSymptom = fault.symptoms.firstOrNull() ?: fault.standardTitle,
      vehicleModel = vehicleModel,
      vin = vin,
      licensePlate = licensePlate,
      clientName = clientName,
      severityLevel = fault.estimatedSeverity.uppercase(),
      plainExplanation = fault.plainLanguageExplanation,
      technicalSummary = "${fault.standardTitle} (${fault.concernedPartCategory.label})",
      rootCausesJoined = fault.probableCauses.joinToString("\n"),
      inspectionStepsJoined = fault.diagnosticSteps.joinToString("\n"),
      multimeterSpecs = fault.multimeterSpecs ?: "Continuité faisceau < 0.5 Ω • Masse franche",
      recommendedPartsJoined = if (fault.detailedParts.isNotEmpty()) {
        fault.detailedParts.joinToString("\n") { "${it.name} (OEM : ${it.oemReference}) • ${it.priceEstimatedDzd} DA" }
      } else {
        fault.relatedParts.joinToString("\n")
      },
      estimatedLaborHours = fault.estimatedRepairTimeHours,
      isSyncedToCloud = _networkCondition.value == WorkshopNetworkCondition.OPTIMAL_ONLINE,
      formattedDate = nowDate,
      createdAtTimestamp = System.currentTimeMillis()
    )
    upsertSavedDiagnosticInternal(entity)
    return entity
  }

  fun upsertSavedDiagnosticInternal(entity: SavedDiagnosticRecordEntity) {
    _savedDiagnostics.value = listOf(entity) + _savedDiagnostics.value.filterNot { it.id == entity.id }
    refreshSummary()
    repositoryScope.launch {
      cacheDao?.upsertSavedDiagnostic(entity)
    }
  }

  fun deleteSavedDiagnostic(id: String) {
    _savedDiagnostics.value = _savedDiagnostics.value.filterNot { it.id == id }
    refreshSummary()
    repositoryScope.launch {
      cacheDao?.deleteSavedDiagnosticById(id)
    }
  }

  fun syncAllPendingDiagnostics() {
    _savedDiagnostics.value = _savedDiagnostics.value.map { it.copy(isSyncedToCloud = true) }
    refreshSummary()
    repositoryScope.launch {
      cacheDao?.markAllDiagnosticsSynced()
    }
  }

  // ==========================================================================
  // ACTIONS : HISTORIQUE CLIENT & PANNES DES CLIENTS
  // ==========================================================================
  fun addClientFaultHistoryRecord(
    clientName: String,
    clientPhone: String,
    vehicleModel: String,
    vin: String,
    licensePlate: String,
    mileageKm: Int,
    faultTitle: String,
    customerReportedSymptoms: String,
    affectedSystem: String,
    linkedDtcCodes: String,
    diagnosticConclusion: String,
    repairActionTaken: String,
    partsReplaced: String,
    totalCostDzd: Long,
    technicianName: String,
    status: String = "EN_DIAGNOSTIC"
  ): ClientFaultHistoryEntity {
    val nowDate = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date())
    val record = ClientFaultHistoryEntity(
      id = "PANNE-${System.currentTimeMillis().toString().takeLast(6)}",
      clientId = "CLI-${licensePlate.take(5)}",
      clientName = clientName,
      clientPhone = clientPhone,
      vehicleModel = vehicleModel,
      vin = vin,
      licensePlate = licensePlate,
      mileageKm = mileageKm,
      faultTitle = faultTitle,
      customerReportedSymptoms = customerReportedSymptoms,
      affectedSystem = affectedSystem,
      linkedDtcCodes = linkedDtcCodes,
      diagnosticConclusion = diagnosticConclusion,
      repairActionTaken = repairActionTaken,
      partsReplaced = partsReplaced,
      totalCostDzd = totalCostDzd,
      technicianName = technicianName,
      status = status,
      recordedDate = nowDate,
      isAvailableOffline = true,
      createdAtTimestamp = System.currentTimeMillis()
    )
    _clientFaultHistory.value = listOf(record) + _clientFaultHistory.value

    // Synchronise également une entrée dans VehicleRepairLogEntity (Room)
    val repairLog = VehicleRepairLogEntity(
      vehicleId = vin,
      vin = vin,
      licensePlate = licensePlate,
      vehicleModel = vehicleModel,
      interventionType = "$faultTitle ($linkedDtcCodes)",
      description = "Client: $clientName • Symptôme: $customerReportedSymptoms • Action: $repairActionTaken",
      technicianName = technicianName,
      costDzd = totalCostDzd,
      mileageKm = mileageKm,
      status = if (status == "PANNE_RESOLUE") "COMPLETED" else "IN_PROGRESS",
      createdAtTimestamp = System.currentTimeMillis()
    )
    _vehicleRepairLogs.value = listOf(repairLog) + _vehicleRepairLogs.value
    refreshSummary()

    repositoryScope.launch {
      cacheDao?.upsertClientFaultRecord(record)
      repairLogDao?.insertRepairLog(repairLog)
    }

    // Si un code DTC ou diagnostic est renseigné, l'enregistre aussi dans les diagnostics enregistrés
    if (linkedDtcCodes.isNotBlank() || diagnosticConclusion.isNotBlank()) {
      val autoSavedDiag = SavedDiagnosticRecordEntity(
        id = "DIAG-CLI-${record.id.takeLast(5)}",
        dtcCode = linkedDtcCodes.split(",").firstOrNull()?.trim()?.ifBlank { "ATELIER" } ?: "ATELIER",
        diagnosticTitle = "$faultTitle — $clientName",
        queryOrSymptom = customerReportedSymptoms,
        vehicleModel = vehicleModel,
        vin = vin,
        licensePlate = licensePlate,
        clientName = clientName,
        severityLevel = if (status == "PANNE_RESOLUE") "RÉSOLU" else "ÉLEVÉE",
        plainExplanation = customerReportedSymptoms,
        technicalSummary = diagnosticConclusion,
        rootCausesJoined = "$affectedSystem\n$diagnosticConclusion",
        inspectionStepsJoined = repairActionTaken,
        multimeterSpecs = "Vérifié par $technicianName (${mileageKm} km)",
        recommendedPartsJoined = partsReplaced,
        estimatedLaborHours = 2.0f,
        isSyncedToCloud = _networkCondition.value == WorkshopNetworkCondition.OPTIMAL_ONLINE,
        formattedDate = nowDate,
        createdAtTimestamp = System.currentTimeMillis()
      )
      upsertSavedDiagnosticInternal(autoSavedDiag)
    }

    return record
  }

  fun updateClientFaultStatus(id: String, newStatus: String) {
    _clientFaultHistory.value = _clientFaultHistory.value.map {
      if (it.id == id) it.copy(status = newStatus) else it
    }
    refreshSummary()
    repositoryScope.launch {
      cacheDao?.updateClientFaultStatus(id, newStatus)
    }
  }

  fun deleteClientFaultRecord(id: String) {
    _clientFaultHistory.value = _clientFaultHistory.value.filterNot { it.id == id }
    refreshSummary()
    repositoryScope.launch {
      cacheDao?.deleteClientFaultRecordById(id)
    }
  }

  // ==========================================================================
  // JEUX DE DONNÉES INITIAUX PRÉ-CHARGÉS EN CACHE ROOM SQLITE
  // ==========================================================================
  private fun defaultInitialSchematics(): List<CachedTechnicalSchematicEntity> = listOf(
    CachedTechnicalSchematicEntity(
      id = "CACHE-WIR-001",
      title = "Schéma Faisceau Régulation Turbo N75 & Capteur G31 (2.0 TDI DFGA)",
      category = "SCHEMA_ELECTRIQUE",
      vehicleBrand = "Volkswagen",
      vehicleModel = "Tiguan II / Golf 7 (2.0 TDI 150)",
      engineCode = "DFGA / CRLB",
      systemName = "Suralimentation & Gestion Moteur EDC17C74",
      schemaReferenceCode = "VW-ELSA-DFGA-042",
      ecuPinoutOrSpecs = "Calculateur J623 (Connecteur T91) :\n• Pin 18 -> Électrovanne N75 (Commande PWM 12V)\n• Pin 44 -> Capteur Pression G31 (Signal 0.5V - 4.5V)\n• Pin 62 -> Masse capteur G31 (0V franc)\n• Pin 79 -> Recopie position actuateur G581",
      fuseBoxAndRelays = "Boîte fusibles compartiment moteur BSM-SC : Fusible F15 (15A) • Relais principal J271",
      wireColorsOrSteps = "Pin 18 (N75) : Brun / Jaune (0.75 mm²)\nPin 44 (Signal G31) : Jaune / Bleu (0.50 mm²)\nPin 62 (Masse capteur) : Marron / Blanc (0.50 mm²)\nAlimentation +5V réf : Rouge / Vert (0.50 mm²)",
      torqueSpecsSummary = "Actuateur géométrie variable : 10 Nm • Colliers durite suralimentation : 5.5 Nm",
      requiredToolsSummary = "Multimètre Fluke, Pompe à vide manuelle avec manomètre, Valise OBD-II",
      warningNotes = "Vérifier l'absence d'oxydation sur le connecteur 3 voies du capteur MAP G31 sous le tablier.",
      payloadSizeKb = 248,
      isPinnedOffline = true,
      cachedAtFormatted = "Épinglé Hors-Ligne • 0 ms",
      cachedAtTimestamp = System.currentTimeMillis() - 3_600_000L
    ),
    CachedTechnicalSchematicEntity(
      id = "CACHE-PROC-001",
      title = "Procédure Calage Distribution & Pompe Haute Pression (2.0 TDI Common Rail)",
      category = "PROCEDURE_MANUEL",
      vehicleBrand = "Volkswagen",
      vehicleModel = "Tiguan / Passat / Octavia 2.0 TDI",
      engineCode = "DFGA / CFFB",
      systemName = "Distribution & Synchronisation Arbres",
      schemaReferenceCode = "OEM-VAG-T10490",
      ecuPinoutOrSpecs = "Durée barème : 4.5h • Niveau : Expert Atelier • Pige vilebrequin T10490",
      fuseBoxAndRelays = "Remplacement impératif : Courroie 145 dents + Galet tendeur + Pompe à eau débrayable",
      wireColorsOrSteps = "1. Déposer le cache supérieur de distribution et caler le moteur au PMH cylindre N°1.\n2. Insérer l'outil de blocage vilebrequin T10490 sur le pignon inférieur.\n3. Piger la poulie d'arbre à cames avec la broche T10492 et la pompe HP avec T10050.\n4. Desserrer le galet tendeur, poser la courroie neuve en respectant le sens de rotation.\n5. Serrer l'écrou du galet tendeur à 20 Nm + 45° et effectuer 2 tours moteur manuels.",
      torqueSpecsSummary = "Galet tendeur : 20 Nm + 45° | Poulie AAC : 100 Nm | Galet enrouleur : 50 Nm | Pompe à eau : 15 Nm",
      requiredToolsSummary = "Coffret calage VAG T10490 / T10492, Clé dynamométrique 10-200 Nm, Clé angulaire",
      warningNotes = "Ne jamais tourner le vilebrequin dans le sens anti-horaire. Vis de galet tendeur à remplacer obligatoirement.",
      payloadSizeKb = 385,
      isPinnedOffline = true,
      cachedAtFormatted = "Épinglé Hors-Ligne • 0 ms",
      cachedAtTimestamp = System.currentTimeMillis() - 7_200_000L
    ),
    CachedTechnicalSchematicEntity(
      id = "CACHE-WIR-002",
      title = "Schéma Réseau CAN-Bus Moteur / ABS-ESP & Boîtier BSM (1.6 HDi DV6)",
      category = "SCHEMA_ELECTRIQUE",
      vehicleBrand = "Peugeot / Citroën",
      vehicleModel = "308 / Partner / Berlingo 1.6 HDi",
      engineCode = "DV6ATED4 / 9HX",
      systemName = "Multiplexage CAN High-Speed & Alimentation BSM",
      schemaReferenceCode = "PSA-SEDRE-CAN-019",
      ecuPinoutOrSpecs = "Calculateur Bosch EDC17C10 (Connecteur 53V NR) :\n• Pin A3 -> CAN High (9010) — Résistance terminale 60 Ω\n• Pin A4 -> CAN Low (9011) — Résistance terminale 60 Ω\n• BSM Connecteur 28V MR Pin 5 -> Relais R1 Alimentation Injecteurs 12V",
      fuseBoxAndRelays = "BSM (Boîtier de Servitude Moteur) : Fusible F1 (20A Calculateurs) • F10 (30A Actionneurs Moteur)",
      wireColorsOrSteps = "CAN High (9010) : Blanc torsadé (2.6V au repos)\nCAN Low (9011) : Bleu torsadé (2.4V au repos)\nAlimentation Relais Principal : Rouge 2.5 mm²\nMasse châssis MC10 : Vert / Jaune sur longeron gauche",
      torqueSpecsSummary = "Écrou borne positive BSM : 8 Nm • Masse longeron MC10 : 12 Nm",
      requiredToolsSummary = "Multimètre, Boîtier de dérivation 53 voies, Lexia / DiagBox",
      warningNotes = "Attendre 3 minutes après coupure contact (endormissement réseau BSI) avant de mesurer la résistance de 60 Ω.",
      payloadSizeKb = 275,
      isPinnedOffline = true,
      cachedAtFormatted = "Épinglé Hors-Ligne • 0 ms",
      cachedAtTimestamp = System.currentTimeMillis() - 10_800_000L
    ),
    CachedTechnicalSchematicEntity(
      id = "CACHE-PROC-002",
      title = "Schéma & Couples Embrayage Double DCT 7DCI (Jetour Dashing / Chery 1.6 TGDI)",
      category = "COUPLES_FLUIDES",
      vehicleBrand = "Jetour / Chery",
      vehicleModel = "Dashing / Tiggo 8 Pro 1.6 TGDI",
      engineCode = "SQRF4J16",
      systemName = "Transmission DCT7 Humide Getrag 7DCT300",
      schemaReferenceCode = "SINO-DCT300-08",
      ecuPinoutOrSpecs = "Capacité huile boîte : 4.2 Litres DCTF-7 • Apprentissage point de léchage obligatoire",
      fuseBoxAndRelays = "Calculateur TCU intégré mécatronique • Fusible F04 (40A Pompe hydraulique TCU)",
      wireColorsOrSteps = "1. Vidanger l'huile DCT à une température comprise entre 35°C et 45°C.\n2. Remplacer la cartouche filtrante haute pression externe sur le carter latéral.\n3. Remplir avec 4.2L d'huile norme Getrag DCTF-7.\n4. Lancer la procédure d'adaptation des fourchettes K1/K2 via l'outil OBD Mekanik+.",
      torqueSpecsSummary = "Bouchon vidange DCT : 35 Nm | Cloche carter filtre : 22 Nm | Support boîte : 65 Nm",
      requiredToolsSummary = "Seringue remplissage ATF/DCT, Thermomètre infrarouge ou lecture PID TCU",
      warningNotes = "Utiliser exclusivement de l'huile DCT pour double embrayage humide (ne jamais mettre d'huile ATF classique).",
      payloadSizeKb = 195,
      isPinnedOffline = true,
      cachedAtFormatted = "Épinglé Hors-Ligne • 0 ms",
      cachedAtTimestamp = System.currentTimeMillis() - 14_400_000L
    ),
    CachedTechnicalSchematicEntity(
      id = "CACHE-WIR-003",
      title = "Brochage Injection Delphi / Continental & Régulateur IMV (1.5 dCi K9K)",
      category = "SCHEMA_ELECTRIQUE",
      vehicleBrand = "Renault / Dacia",
      vehicleModel = "Clio 4 / Duster / Symbol 1.5 dCi",
      engineCode = "K9K 608 / K9K 836",
      systemName = "Injection Common Rail & Régulation Pression Rail",
      schemaReferenceCode = "REN-VISU-K9K-112",
      ecuPinoutOrSpecs = "Calculateur Injection Connecteur Marron 48V :\n• Pin M1 / M2 -> Actuateur débit pompe HP (IMV) — Résistance 5.3 Ω à 20°C\n• Pin E4 -> Capteur pression rampe (0.5V ralenti ~260 bars)\n• Pins A1..B4 -> Pilotage piézo injecteurs Cyl 1 à 4 (Ordre 1-3-4-2)",
      fuseBoxAndRelays = "Boîtier UPC compartiment moteur : Fusible F5 (25A Injection) • Relais R02 Verrouillage",
      wireColorsOrSteps = "Commande IMV (+) : Saumon / Blanc\nCommande IMV (-) : Violet / Noir\nSignal Pression Rail : Vert / Or\nMasse blindage capteur : Noir",
      torqueSpecsSummary = "Bride injecteur K9K : 28 Nm | Raccord tuyau haute pression : 25 Nm | Capteur rampe : 35 Nm",
      requiredToolsSummary = "Fioles de retour d'injecteurs, Multimètre, Clé à tuyauter 17 mm",
      warningNotes = "Coder les 16 caractères IMA de chaque nouvel injecteur dans le calculateur après remplacement.",
      payloadSizeKb = 230,
      isPinnedOffline = false,
      cachedAtFormatted = "En cache local • 0 ms",
      cachedAtTimestamp = System.currentTimeMillis() - 20_000_000L
    )
  )

  private fun defaultInitialSavedDiagnostics(): List<SavedDiagnosticRecordEntity> = listOf(
    SavedDiagnosticRecordEntity(
      id = "DIAG-P0299-001",
      dtcCode = "P0299",
      diagnosticTitle = "P0299 — Pression de suralimentation turbocompresseur trop faible (Underboost)",
      queryOrSymptom = "Perte de puissance en côte à 2400 tr/min + voyant préchauffage clignotant",
      vehicleModel = "Volkswagen Tiguan II 2.0 TDI 150 (DFGA)",
      vin = "WVWZZZ5NZMW123456",
      licensePlate = "04291-120-16",
      clientName = "Karim Benali",
      severityLevel = "ÉLEVÉE",
      plainExplanation = "Le turbocompresseur ne parvient pas à fournir la pression d'air demandée par le calculateur moteur (écart > 350 mbar pendant plus de 5 secondes).",
      technicalSummary = "Écart négatif de régulation de pression de suralimentation détecté par le capteur MAP G31. Suspicion fuite sur circuit d'air haute pression ou grippage géométrie variable.",
      rootCausesJoined = "Durite d'échangeur d'air (intercooler) fissurée ou collier desserré (48%)\nÉlectrovanne de régulation N75 ou capsule à dépression percée (27%)\nGéométrie variable du turbo (ailettes VNT) encrassée par la calamine (18%)\nCapteur de pression de suralimentation MAP G31 encrassé (7%)",
      inspectionStepsJoined = "1. Mettre le circuit d'admission sous pression (1.5 bar) avec la machine à fumée pour localiser toute fuite sur les durites.\n2. Tester l'étanchéité de la capsule du turbo à la pompe à vide (doit tenir -0.6 bar sans chuter).\n3. Contrôler la résistance de l'électrovanne N75 et l'alimentation 12V sur le connecteur.\n4. Comparer en lecture OBD temps réel la consigne pression turbo vs pression mesurée à 2500 tr/min en 3ème.",
      multimeterSpecs = "Électrovanne N75 : 14 Ω à 20 Ω à 20°C • Capteur G31 (Pin 44) : 1.1V au ralenti -> 3.8V sous pleine charge",
      recommendedPartsJoined = "Électrovanne de Pression Turbo Pierburg N75 (OEM : 1K0 906 627 A) • 8 500 DA\nDurite Suralimentation Renforcée Silicone/Kevlar (OEM : 5N0 145 832 S) • 12 400 DA\nCapteur Pression Suralimentation Bosch MAP G31 (OEM : 03G 906 051 E) • 6 900 DA",
      estimatedLaborHours = 1.5f,
      isSyncedToCloud = true,
      formattedDate = "12/11/2026 16:40",
      createdAtTimestamp = System.currentTimeMillis() - 86_400_000L
    ),
    SavedDiagnosticRecordEntity(
      id = "DIAG-P0401-002",
      dtcCode = "P0401",
      diagnosticTitle = "P0401 — Système de recirculation des gaz d'échappement (EGR) : Débit insuffisant",
      queryOrSymptom = "À-coups à bas régime (1500-2000 tr/min) et fumée noire à l'accélération",
      vehicleModel = "Peugeot 308 1.6 HDi 115 (DV6C)",
      vin = "VF3FPHNSSMY001234",
      licensePlate = "11842-117-31",
      clientName = "Sofiane Mansouri",
      severityLevel = "MOYENNE",
      plainExplanation = "La vanne EGR est obstruée par la suie ou son moteur électrique ne s'ouvre plus assez pour recycler les gaz d'échappement.",
      technicalSummary = "Le débitmètre massique (MAF) mesure un débit d'air frais trop élevé lorsque la vanne EGR est commandée ouverte par le calculateur EDC17C10.",
      rootCausesJoined = "Soupape de vanne EGR bloquée fermée par la calamine (55%)\nRefroidisseur EGR et conduit d'admission colmatés (25%)\nPotentiomètre de recopie de position EGR défaillant (15%)\nDébitmètre d'air (MAF) dérivant (5%)",
      inspectionStepsJoined = "1. Effectuer un test des actionneurs à la valise pour écouter le claquement d'ouverture/fermeture de la vanne EGR.\n2. Contrôler le pourcentage de recopie EGR vs consigne (écart max toléré : ±5%).\n3. Déposer la vanne EGR et inspecter l'encrassement du boisseau et du conduit collecteur.\n4. Lancer l'apprentissage des butées haute et basse EGR après nettoyage ou remplacement.",
      multimeterSpecs = "Alimentation capteur recopie EGR : 5.0V ± 0.1V • Signal position fermée : ~0.75V • Position ouverte : ~4.1V",
      recommendedPartsJoined = "Vanne EGR Électrique Complète Valeo Origine (OEM : 1618.NR / 9672880080) • 24 500 DA\nJoint Métallique Bride EGR Victor Reinz (OEM : 1618.AW) • 1 200 DA",
      estimatedLaborHours = 2.5f,
      isSyncedToCloud = true,
      formattedDate = "11/11/2026 11:15",
      createdAtTimestamp = System.currentTimeMillis() - 172_800_000L
    ),
    SavedDiagnosticRecordEntity(
      id = "DIAG-P0087-003",
      dtcCode = "P0087",
      diagnosticTitle = "P0087 — Pression dans la rampe commune (Common Rail) trop basse",
      queryOrSymptom = "Démarrage très long à chaud et coupure moteur en pleine accélération",
      vehicleModel = "Renault Clio 4 / Symbol 1.5 dCi 90 (K9K 608)",
      vin = "VF15RRL0A52910482",
      licensePlate = "09812-119-16",
      clientName = "Yacine Haddad",
      severityLevel = "CRITIQUE",
      plainExplanation = "La pression du gasoil dans la rampe d'injection chute sous le seuil minimal (moins de 180 bars au démarrage), empêchant l'ouverture des injecteurs.",
      technicalSummary = "Défaut critique de régulation haute pression carburant. Fuite interne sur le retour d'un ou plusieurs injecteurs ou régulateur de débit IMV grippé.",
      rootCausesJoined = "Fuite interne excessive sur retour d'un ou plusieurs injecteurs (52%)\nRégulateur de pression / débit IMV sur pompe HP défectueux (28%)\nFiltre à gasoil colmaté ou prise d'air sur poire d'amorçage (14%)\nLimiteur de pression de rampe fuyard (6%)",
      inspectionStepsJoined = "1. Brancher les 4 fioles de test de retour d'injecteurs pendant 2 minutes au ralenti (débit max < 25 ml, écart < 30%).\n2. Inspecter le fond du bocal de filtre à carburant à l'aimant pour vérifier l'absence de limaille métallique.\n3. Mesurer la résistance de l'actuateur IMV sur la pompe haute pression.\n4. Remplacer le filtre à gasoil et purger l'air avant tout remplacement d'organe coûteux.",
      multimeterSpecs = "Actuateur IMV Pompe HP : 5.1 Ω à 5.6 Ω à 20°C • Capteur Pression Rail (Pin E4) : 0.5V (0 bar) -> 1.15V (260 bars ralenti)",
      recommendedPartsJoined = "Régulateur de Débit Pompe HP Delphi IMV (OEM : 9109-903 / 7701206905) • 14 800 DA\nInjecteur Common Rail Continental/Delphi Neuf (OEM : 166009445R) • 38 000 DA\nFiltre à Gasoil Complet Purflux (OEM : 164004350R) • 4 200 DA",
      estimatedLaborHours = 2.0f,
      isSyncedToCloud = false,
      formattedDate = "Aujourd'hui 09:20",
      createdAtTimestamp = System.currentTimeMillis() - 7_200_000L
    )
  )

  private fun defaultInitialClientFaultHistory(): List<ClientFaultHistoryEntity> = listOf(
    ClientFaultHistoryEntity(
      id = "PANNE-CLI-101",
      clientId = "CLI-04291",
      clientName = "Karim Benali",
      clientPhone = "0550 42 18 90",
      vehicleModel = "Volkswagen Tiguan II 2.0 TDI 150 (DFGA)",
      vin = "WVWZZZ5NZMW123456",
      licensePlate = "04291-120-16",
      mileageKm = 128450,
      faultTitle = "Perte de puissance en côte + Sifflement Turbo",
      customerReportedSymptoms = "Le client signale que le véhicule passe en mode dégradé (bridé à 3000 tr/min) dès qu'il emprunte une montée sur autoroute avec un sifflement d'air sous le capot.",
      affectedSystem = "Suralimentation & Circuit Air",
      linkedDtcCodes = "P0299, P0101",
      diagnosticConclusion = "Fissure de 3 cm détectée au test de fumée sur la durite inférieure d'intercooler + électrovanne N75 fatiguée.",
      repairActionTaken = "Remplacement durite inférieure suralimentation + remplacement électrovanne N75 + effacement défauts et essai routier validé (pression 2.35 bars abs).",
      partsReplaced = "Durite Suralimentation (5N0 145 832 S), Électrovanne Pierburg N75 (1K0 906 627 A)",
      totalCostDzd = 26500L,
      technicianName = "Mourad (Chef d'Atelier)",
      status = "PANNE_RESOLUE",
      recordedDate = "12/11/2026",
      isAvailableOffline = true,
      createdAtTimestamp = System.currentTimeMillis() - 86_400_000L
    ),
    ClientFaultHistoryEntity(
      id = "PANNE-CLI-102",
      clientId = "CLI-09812",
      clientName = "Yacine Haddad",
      clientPhone = "0661 88 34 12",
      vehicleModel = "Renault Clio 4 1.5 dCi 90 (K9K 608)",
      vin = "VF15RRL0A52910482",
      licensePlate = "09812-119-16",
      mileageKm = 164200,
      faultTitle = "Démarrage difficile à chaud & Voyant Injection à contrôler",
      customerReportedSymptoms = "Le moteur met 6 à 8 secondes à démarrer lorsqu'il est chaud. Coupure nette lors d'un dépassement en 4ème.",
      affectedSystem = "Injection Haute Pression Common Rail",
      linkedDtcCodes = "P0087, DF053",
      diagnosticConclusion = "Test des retours d'injecteurs : l'injecteur N°3 remplit la fiole (65 ml en 2 min contre 14 ml sur les cylindres 1, 2 et 4) + filtre gasoil saturé.",
      repairActionTaken = "Remplacement injecteur Cylindre N°3 + codage IMA 16 caractères + remplacement filtre à gasoil et purge circuit.",
      partsReplaced = "Injecteur 1.5 dCi (166009445R), Rondelle pare-feu cuivre, Filtre à gasoil Purflux",
      totalCostDzd = 46500L,
      technicianName = "Amine (Électromécanicien)",
      status = "EN_DIAGNOSTIC",
      recordedDate = "13/11/2026",
      isAvailableOffline = true,
      createdAtTimestamp = System.currentTimeMillis() - 7_200_000L
    ),
    ClientFaultHistoryEntity(
      id = "PANNE-CLI-103",
      clientId = "CLI-11842",
      clientName = "Sofiane Mansouri",
      clientPhone = "0770 19 65 44",
      vehicleModel = "Peugeot 308 1.6 HDi 115 (DV6C)",
      vin = "VF3FPHNSSMY001234",
      licensePlate = "11842-117-31",
      mileageKm = 142900,
      faultTitle = "À-coups moteur à 1800 tr/min & Voyant Antipollution",
      customerReportedSymptoms = "Hésitations du moteur à vitesse stabilisée en ville et fumée noire à la réaccélération.",
      affectedSystem = "Dépollution EGR & Admission",
      linkedDtcCodes = "P0401, P0409",
      diagnosticConclusion = "Vanne EGR grippée à 18% d'ouverture avec encrassement important du conduit répartiteur d'admission.",
      repairActionTaken = "Décalaminage complet conduit d'admission + remplacement vanne EGR Valeo neuve + apprentissage butées au DiagBox.",
      partsReplaced = "Vanne EGR Valeo (1618.NR), Joint bride EGR, Nettoyant admission Pro",
      totalCostDzd = 31000L,
      technicianName = "Mourad (Chef d'Atelier)",
      status = "PANNE_RESOLUE",
      recordedDate = "10/11/2026",
      isAvailableOffline = true,
      createdAtTimestamp = System.currentTimeMillis() - 259_200_000L
    ),
    ClientFaultHistoryEntity(
      id = "PANNE-CLI-104",
      clientId = "CLI-24510",
      clientName = "Nassim Bouzid",
      clientPhone = "0552 91 04 77",
      vehicleModel = "Jetour Dashing 1.6 TGDI DCT7",
      vin = "LVVDB21B9PD048912",
      licensePlate = "24510-123-16",
      mileageKm = 61200,
      faultTitle = "Vibration au freinage & Broutement embrayage 1ère/2ème",
      customerReportedSymptoms = "Secousses dans la pédale de frein à 100 km/h et léger patinage à froid lors du passage 1ère -> 2ème vitesse.",
      affectedSystem = "Freinage Avant & Boîte DCT7",
      linkedDtcCodes = "C1201, P1778",
      diagnosticConclusion = "Voile des disques avant mesuré à 0.14 mm (tolérance max 0.05 mm) + huile DCT7 à vidanger (60 000 km atteints) avec recalibrage des points de léchage.",
      repairActionTaken = "Remplacement disques et plaquettes céramiques avant + vidange 4.2L huile DCTF-7 et adaptation mécatronique.",
      partsReplaced = "Kit Disques Ventilés 300mm + Plaquettes Céramiques, 5L Huile Getrag DCTF-7",
      totalCostDzd = 39800L,
      technicianName = "Kamel (Spécialiste Boîtes Auto)",
      status = "A_SURVEILLER",
      recordedDate = "08/11/2026",
      isAvailableOffline = true,
      createdAtTimestamp = System.currentTimeMillis() - 432_000_000L
    )
  )

  private fun defaultInitialRepairLogs(): List<VehicleRepairLogEntity> = listOf(
    VehicleRepairLogEntity(
      id = 1L,
      vehicleId = "WVWZZZ5NZMW123456",
      vin = "WVWZZZ5NZMW123456",
      licensePlate = "04291-120-16",
      vehicleModel = "Volkswagen Tiguan II 2.0 TDI 150",
      interventionType = "Remplacement Durite Turbo & Électrovanne N75 (P0299)",
      description = "Client: Karim Benali • Fuite circuit suralimentation résolue • Pression turbo validée",
      technicianName = "Mourad (Chef d'Atelier)",
      costDzd = 26500L,
      mileageKm = 128450,
      status = "COMPLETED",
      createdAtTimestamp = System.currentTimeMillis() - 86_400_000L
    ),
    VehicleRepairLogEntity(
      id = 2L,
      vehicleId = "VF15RRL0A52910482",
      vin = "VF15RRL0A52910482",
      licensePlate = "09812-119-16",
      vehicleModel = "Renault Clio 4 1.5 dCi 90",
      interventionType = "Diagnostic Pression Rail & Injecteur Cyl N°3 (P0087)",
      description = "Client: Yacine Haddad • Test fioles retour injecteurs + remplacement injecteur 3",
      technicianName = "Amine (Électromécanicien)",
      costDzd = 46500L,
      mileageKm = 164200,
      status = "IN_PROGRESS",
      createdAtTimestamp = System.currentTimeMillis() - 7_200_000L
    ),
    VehicleRepairLogEntity(
      id = 3L,
      vehicleId = "VF3FPHNSSMY001234",
      vin = "VF3FPHNSSMY001234",
      licensePlate = "11842-117-31",
      vehicleModel = "Peugeot 308 1.6 HDi 115",
      interventionType = "Remplacement Vanne EGR & Décalaminage (P0401)",
      description = "Client: Sofiane Mansouri • Vanne EGR Valeo neuve + apprentissage DiagBox",
      technicianName = "Mourad (Chef d'Atelier)",
      costDzd = 31000L,
      mileageKm = 142900,
      status = "COMPLETED",
      createdAtTimestamp = System.currentTimeMillis() - 259_200_000L
    )
  )
}
