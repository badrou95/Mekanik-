package com.example.data.model

// ============================================================================
// 1. PLANNING VISUEL DES PONTS & CHARGE ATELIER (KANBAN / GANTT TEMPS RÉEL)
// ============================================================================

enum class WorkshopBayStatus(val label: String, val badgeColorHex: Long) {
  EN_COURS("En cours sur pont", 0xFF00E676),
  EN_ATTENTE_PIECE("Attente Pièce", 0xFFFF7A00),
  CONTROLE_QUALITE("Essai & Contrôle", 0xFF00E5FF),
  LIBRE("Pont Disponible", 0xFF9AA5B1),
  MAINTENANCE_PONT("Maintenance Pont", 0xFFE53935)
}

enum class WorkshopBayType(val label: String, val maxWeightTons: String) {
  PONT_2_COLONNES("Pont 2 Colonnes Rapide", "4.0 Tonnes"),
  PONT_4_COLONNES("Pont 4 Colonnes Lourd", "5.0 Tonnes"),
  BAIE_GEOMETRIE_3D("Baie Géométrie 3D & Pneus", "4.5 Tonnes"),
  BAIE_DIAG_ELECTRIQUE("Baie Diagnostic & EV/Hybride", "3.5 Tonnes")
}

data class WorkshopBayItem(
  val id: String,
  val bayNumber: Int,
  val bayName: String,
  val bayType: WorkshopBayType,
  val status: WorkshopBayStatus,
  val assignedMechanic: String,
  val mechanicSpecialty: String,
  val currentVehicleModel: String? = null,
  val currentLicensePlate: String? = null,
  val repairOrderNumber: String? = null,
  val operationTitle: String? = null,
  val elapsedMinutes: Int = 0,
  val estimatedMinutes: Int = 90,
  val isTimerRunning: Boolean = false
) {
  val progressRatio: Float
    get() = if (estimatedMinutes <= 0 || currentVehicleModel == null) 0f
    else (elapsedMinutes.toFloat() / estimatedMinutes.toFloat()).coerceIn(0f, 1f)
}

data class WaitingQueueVehicle(
  val id: String,
  val repairOrderNumber: String,
  val vehicleModel: String,
  val licensePlate: String,
  val clientName: String,
  val operationTitle: String,
  val estimatedMinutes: Int,
  val priorityLevel: String, // "URGENT", "NORMAL", "FLOTTE"
  val recommendedBayType: WorkshopBayType
)

// ============================================================================
// 2. SUIVI DES GARANTIES PIÈCES & GESTION DES RETOURS SAV / RMA
// ============================================================================

enum class WarrantyStatus(val label: String, val colorHex: Long) {
  ACTIVE("Garantie Active", 0xFF00E676),
  EXPIRE_BIENTOT("Expire < 30 j", 0xFFFFB300),
  RMA_EN_COURS("Dossier RMA Ouvert", 0xFFFF7A00),
  REMPLACEE_SAV("Échangée sous Garantie", 0xFF00E5FF),
  EXPIREE("Expirée", 0xFF9AA5B1)
}

data class PartWarrantyRecord(
  val id: String,
  val warrantyNumber: String,
  val partName: String,
  val oemReference: String,
  val brand: String,
  val supplierName: String,
  val vehicleModel: String,
  val licensePlate: String,
  val clientName: String,
  val installationDate: String,
  val expirationDate: String,
  val durationMonths: Int,
  val daysRemaining: Int,
  val mileageAtInstallKm: Int,
  val maxWarrantyMileageKm: Int,
  val purchasePriceDzd: Int,
  val status: WarrantyStatus,
  val rmaClaimReason: String? = null,
  val rmaTrackingCode: String? = null
)

// ============================================================================
// 3. PORTAIL CLIENT WEB / QR & VALIDATION DEVIS PAR SIGNATURE À DISTANCE
// ============================================================================

data class OptionalQuoteLineItem(
  val id: String,
  val title: String,
  val recommendationReason: String,
  val priceDzd: Int,
  val isSelectedByClient: Boolean
)

data class RemoteClientQuoteSession(
  val id: String,
  val quoteNumber: String,
  val shareableUrl: String,
  val clientName: String,
  val clientPhone: String,
  val vehicleModel: String,
  val licensePlate: String,
  val currentStepIndex: Int, // 0: Réception, 1: Diagnostic, 2: Accord Devis, 3: Sur Pont, 4: Prêt
  val mandatoryTotalDzd: Int,
  val optionalItems: List<OptionalQuoteLineItem>,
  val isSignedRemotely: Boolean,
  val signatureTimestamp: String? = null,
  val mechanicVideoNoteTitle: String = "Vidéo explicative du mécanicien (0:42) - État disques & plaquettes"
) {
  val finalApprovedTotalDzd: Int
    get() = mandatoryTotalDzd + optionalItems.filter { it.isSelectedByClient }.sumOf { it.priceDzd }
}

// ============================================================================
// 4. MODE HORS-LIGNE INTELLIGENT (CACHE LOCAL SOUS-SOL & SYNCHRO AUTO)
// ============================================================================

enum class SyncItemType(val label: String) {
  INSPECTION_360("Inspection 360° & Photos"),
  DEVIS_OR("Ordre de Réparation / Devis"),
  MOUVEMENT_STOCK("Scan Code-Barres Stock"),
  DIAGNOSTIC_OBD("Rapport Diagnostic OBD/IA")
}

data class PendingSyncAction(
  val id: String,
  val type: SyncItemType,
  val title: String,
  val timestamp: String,
  val payloadSizeKb: Int,
  val isSynced: Boolean = false
)

data class OfflineCachedPack(
  val id: String,
  val title: String,
  val category: String,
  val itemCount: Int,
  val storageSizeMb: Double,
  val lastUpdated: String,
  val isDownloaded: Boolean = true
)

// ============================================================================
// 5. COMPARATEUR MULTI-FOURNISSEURS & COMMANDES GROUPÉES B2B + LIVREUR
// ============================================================================

enum class B2bDeliveryStatus(val label: String, val stepIndex: Int) {
  PREPARATION_DEPOT("Préparation au dépôt grossiste", 1),
  COURSIER_EN_ROUTE("Coursier Moto Express en route", 2),
  LIVRE_ATELIER("Livré & Réceptionné à l'atelier", 3)
}

data class SupplierComparisonOffer(
  val id: String,
  val partName: String,
  val oemReference: String,
  val brand: String,
  val supplierName: String,
  val supplierCity: String,
  val wholesalePriceHtDzd: Int,
  val recommendedRetailPriceDzd: Int,
  val availableStock: Int,
  val deliverySlaLabel: String, // "Express 45 min", "Fourgon 2h", "J+1 Matin"
  val isBestPrice: Boolean = false,
  val isFastestDelivery: Boolean = false,
  val supplierRating: Double = 4.9
) {
  val workshopMarginDzd: Int get() = recommendedRetailPriceDzd - wholesalePriceHtDzd
}

data class ActiveB2bOrderTracking(
  val orderId: String,
  val supplierName: String,
  val courierName: String,
  val courierPhone: String,
  val itemsCount: Int,
  val totalHtDzd: Int,
  val etaMinutes: Int,
  val status: B2bDeliveryStatus
)

// ============================================================================
// 6. CALCULATEUR DE COÛT DE REVIENT KILOMÉTRIQUE (TCO) & CARBURANT
// ============================================================================

enum class VehicleExpenseCategory(val label: String, val colorHex: Long) {
  CARBURANT("Carburant / Recharge", 0xFFFF7A00),
  ENTRETIEN_PIECES("Entretien & Réparations", 0xFFE53935),
  ASSURANCE_TAXES("Assurance, CT & Vignette", 0xFF2979FF),
  PNEUMATIQUES("Pneumatiques & Géométrie", 0xFF00E5FF),
  AMORTISSEMENT("Décote & Financement", 0xFF9AA5B1)
}

data class VehicleExpenseEntry(
  val id: String,
  val vehicleId: String,
  val vehicleLabel: String,
  val category: VehicleExpenseCategory,
  val title: String,
  val date: String,
  val mileageKm: Int,
  val amountDzd: Int,
  val litersOrUnits: Double? = null
)

data class VehicleTcoSummary(
  val vehicleId: String,
  val vehicleName: String,
  val licensePlate: String,
  val annualDistanceKm: Int,
  val averageFuelLitersPer100Km: Double,
  val totalFuelCostDzd: Int,
  val totalMaintenanceCostDzd: Int,
  val totalInsuranceCostDzd: Int,
  val totalTiresCostDzd: Int,
  val dailyRentalRateDzd: Int = 6500, // Pour les loueurs
  val rentedDaysPerMonth: Int = 22
) {
  val totalAnnualCostDzd: Int
    get() = totalFuelCostDzd + totalMaintenanceCostDzd + totalInsuranceCostDzd + totalTiresCostDzd

  val costPerKilometerDzd: Double
    get() = if (annualDistanceKm > 0) totalAnnualCostDzd.toDouble() / annualDistanceKm else 0.0

  val monthlyAverageCostDzd: Int
    get() = totalAnnualCostDzd / 12

  val estimatedMonthlyRentalRevenueDzd: Int
    get() = dailyRentalRateDzd * rentedDaysPerMonth

  val netMonthlyRentalProfitDzd: Int
    get() = estimatedMonthlyRentalRevenueDzd - monthlyAverageCostDzd
}

// ============================================================================
// 7. CONSTAT AMIABLE DIGITAL & DÉCLARATION SINISTRE GUIDÉE
// ============================================================================

enum class AccidentCircumstance(val code: Int, val label: String) {
  EN_STATIONNEMENT(1, "En stationnement / À l'arrêt"),
  QUITTAIT_STATIONNEMENT(2, "Quittait un stationnement / Ouvrait une portière"),
  PRENAIT_STATIONNEMENT(3, "Prenait un stationnement"),
  SORTAIT_PARKING_LIEU_PRIVE(4, "Sortait d'un parking, d'un lieu privé, d'un chemin de terre"),
  HEURTAIT_ARRIERE(8, "Heurtait à l'arrière, en roulant dans le même sens et sur une même file"),
  CHANGEAIT_DE_FILE(10, "Changeait de file"),
  DOUBLAIT(11, "Doublait"),
  VIRAIT_A_DROITE(12, "Virait à droite"),
  VIRAIT_A_GAUCHE(13, "Virait à gauche"),
  RECULAIT(14, "Reculait"),
  EMPIETAIT_SENS_INVERSE(15, "Empiétait sur une voie réservée à la circulation en sens inverse"),
  VENAIT_DE_DROITE_REFUS_PRIORITE(16, "Venait de droite (dans un carrefour) / Refus de priorité")
}

data class DigitalAccidentReport(
  val id: String,
  val reportNumber: String,
  val dateTimeFormatted: String,
  val gpsLocation: String,
  val driverAVehicle: String,
  val driverAPlate: String,
  val driverAInsurance: String,
  val driverAPolicyNumber: String,
  val driverBName: String,
  val driverBPhone: String,
  val driverBVehicle: String,
  val driverBPlate: String,
  val driverBInsurance: String,
  val driverBPolicyNumber: String,
  val selectedCircumstancesA: List<AccidentCircumstance>,
  val selectedCircumstancesB: List<AccidentCircumstance>,
  val impactZoneDescription: String,
  val attachedPhotosCount: Int,
  val status: String, // "TRANSMIS_ASSURANCE_ET_ATELIER", "EN_EXPERTISE"
  val isSignedByBothParties: Boolean
)
