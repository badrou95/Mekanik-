package com.example.data.model

// ============================================================================
// LES 6 OPTIONS INDISPENSABLES « MEKANIK+ INDUSTRIAL PRO SUITE »
// 1. Pont « Photo Plaque Châssis / Carte Grise (OCR Hors-Ligne) → Cascade 11 API »
// 2. Pont « Équivalence Croisée Chinoise ↔ Européenne (OEM → TecDoc IAM) »
// 3. Synchronisation Intelligente du Cache Local (« Auto-Apprentissage Room SQLite »)
// 4. Génération Automatique du « Devis / Ordre de Réparation (OR) en 1 Clic » (DZD)
// 5. Module « Fluides, Lubrifiants & Procédures de Reset Service » (Spécial Boîtes DCT Chinoises & Europe)
// 6. Pont Direct avec le « Passeport QR Universel du Véhicule »
// ============================================================================

// --- OPTION 1 : PONT OCR HORS-LIGNE (WHATSAPP / PARE-BRISE / CARTE GRISE DZ) → CASCADE 11 API ---
data class OcrChassisCascadePreset(
  val id: String,
  val sourceTypeBadge: String, // "PHOTO WHATSAPP PARE-BRISE", "CARTE GRISE ALGÉRIENNE", "PLAQUE RIVETÉE MONTANT B", "ÉTIQUETTE MOTEUR"
  val title: String,
  val rawNoisyOcrText: String, // Ex: "L V V D B 2 I B 9 P D O 4 8 9 I 2" avec erreurs OCR classiques (O->0, I->1)
  val autoCleanedIsoVin: String, // Ex: "LVVDB21B9PD048912"
  val ocrCorrectionsApplied: List<String>,
  val confidencePercent: Int,
  val extractionTimeMs: Int,
  val detectedBrandModelPreview: String,
  val wilayaPlateDetected: String
)

// --- OPTION 2 : PONT ÉQUIVALENCE CROISÉE CHINOISE ↔ EUROPÉENNE (OEM CHINE → TECDOC IAM) ---
data class CrossRefIamSupplierMatch(
  val iamBrand: String, // Bosch, Brembo, TRW, Mann-Filter, Valeo, Sachs, NGK, Continental
  val iamPartNumber: String,
  val tecDocKType: String,
  val compatibilityCertaintyPercent: Int,
  val sharedPlatformOrigin: String, // Ex: "Plateforme CMA Volvo XC40", "Étriers Continental/ATE", "Injection Bosch MED17"
  val localVendorNameWilaya: String,
  val priceDzd: Int,
  val oemDealerPriceDzd: Int,
  val savingsPercent: Int,
  val stockStatus: String
)

data class SinoEuroCrossReferenceBridge(
  val id: String,
  val chineseVehicleLabel: String,
  val vinExample: String,
  val partCategory: String,
  val partName: String,
  val chineseOemPartNumber: String, // Ex: "J60-3501080" (Chery/Jetour) ou "4048001500" (Geely)
  val chineseOemAvailabilityAlert: String, // Ex: "Rupture maison mère / Délai 21j"
  val technicalDimensionsSpec: String, // Ex: "133.0 x 50.6 x 16.0 mm • Système Continental"
  val europeanPlatformSibling: String, // Ex: "Volvo XC40 (CMA) / VAG MQB / Bosch System"
  val iamEquivalents: List<CrossRefIamSupplierMatch>
)

// --- OPTION 3 : SYNCHRONISATION INTELLIGENTE DU CACHE LOCAL (AUTO-APPRENTISSAGE ROOM SQLITE 0ms) ---
data class AutoLearnedVinCacheEntry(
  val vin: String,
  val vehicleLabel: String,
  val engineAndTrans: String,
  val initialSourceProvider: UnifiedApiConnectorProvider,
  val initialNetworkLatencyMs: Int,
  val cachedOfflineLatencyMs: Int = 0, // 0ms en hors-ligne !
  val learnedTimestamp: String,
  val oemPartsCountIndexed: Int,
  val crossRefIamCountIndexed: Int,
  val fluidsAndTorqueIndexed: Boolean,
  val localRoomSizeKb: Int,
  val hitCountInWorkshop: Int
)

// --- OPTION 4 : DEVIS / ORDRE DE RÉPARATION (OR) AUTOMATIQUE EN 1 CLIC (BARÈME DZD + PDF/WHATSAPP) ---
data class OneClickAutoQuoteLineItem(
  val refCode: String,
  val description: String,
  val sourceApiBadge: String,
  val quantity: Int,
  val unitPriceDzd: Int,
  val isLaborLine: Boolean = false,
  val laborHours: Float = 0f
)

data class OneClickAutoQuoteBundle(
  val id: String,
  val orderReferenceNumber: String,
  val vehicleTitle: String,
  val vin: String,
  val clientName: String,
  val clientPhone: String,
  val wilaya: String,
  val tariffTierLabel: String, // "T1 Mécanique Courante (2 500 DA/h)", "T2 Diagnostic & Électronique (3 500 DA/h)", "T3 Haute Technicité DCT/EV (4 500 DA/h)"
  val hourlyRateDzd: Int,
  val flatRateHours: Float,
  val partsLines: List<OneClickAutoQuoteLineItem>,
  val consumablesDzd: Int,
  val tvaPercent: Int = 19,
  val warrantyNotice: String,
  val generatedFromCascadeSource: String
) {
  val totalPartsHtDzd: Int get() = partsLines.filter { !it.isLaborLine }.sumOf { it.unitPriceDzd * it.quantity }
  val totalLaborHtDzd: Int get() = (hourlyRateDzd * flatRateHours).toInt()
  val totalHtDzd: Int get() = totalPartsHtDzd + totalLaborHtDzd + consumablesDzd
  val tvaAmountDzd: Int get() = (totalHtDzd * tvaPercent) / 100
  val totalTtcDzd: Int get() = totalHtDzd + tvaAmountDzd
}

// --- OPTION 5 : FLUIDES, LUBRIFIANTS & PROCÉDURES DE RESET SERVICE (DCT CHINOISES & EUROPE) ---
data class FluidAndServiceResetSpec(
  val id: String,
  val vehicleTitle: String,
  val vinSample: String,
  val engineCode: String,
  val transmissionCode: String,
  val engineOilViscosity: String, // Ex: "0W-20 Synthèse Totale"
  val engineOilNormOem: String, // Ex: "API SP / ILSAC GF-6A / VCC RBS0-2AE (Norme Geely-Volvo)"
  val engineOilCapacityLiters: String, // Ex: "4.5 L (avec filtre) — 5.6 L pour le 1.5TD 3-Cylindres"
  val dctGearboxFluidType: String, // Ex: "Huile DCT Humide 7DCT300 (Shell Spirax S5 DCT 11 / Castrol Transmax Dual)"
  val dctGearboxCapacityLiters: String, // Ex: "4.2 L (Vidange atelier) / 4.8 L (Remplissage à sec)"
  val dctChangeIntervalKm: String, // Ex: "Tous les 60 000 km ou 3 ans (Usage sévère Algérie : 45 000 km)"
  val coolantSpecAndCapacity: String, // Ex: "G12++ / LEC-II Rose Organique (-35°C) • 7.2 L"
  val brakeFluidSpec: String, // Ex: "DOT 4 LV (Basse Viscosité ESP/EPB) • 0.85 L"
  val manualClusterResetSteps: List<String>, // Manipulation au combiné sans valise
  val obdUdsResetRoutine: String // Routine UDS OBD-II (ex: Service $31 01 F0 02)
)

// --- OPTION 6 : PONT DIRECT AVEC LE « PASSEPORT QR UNIVERSEL DU VÉHICULE » ---
data class QrPassportCascadeSyncRecord(
  val passportQrId: String,
  val vin: String,
  val vehicleTitle: String,
  val licensePlateDz: String,
  val winningApiProviderName: String,
  val syncedOemPartsSummary: List<String>,
  val certifiedEngineOilGrade: String,
  val nextDctGearboxServiceKm: String,
  val lastSyncedWorkshopOrderRef: String,
  val lastSyncedTimestamp: String,
  val isStickerReadyForDoorJamb: Boolean = true,
  val isSyncedWithDriverSpace: Boolean = true,
  val isSyncedWithVendorCounter: Boolean = true
)
