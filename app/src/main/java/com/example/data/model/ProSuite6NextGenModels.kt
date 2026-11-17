package com.example.data.model

// ============================================================================
// 1. SCHÉMAS ÉLECTRIQUES INTERACTIFS, FUSIBLES & PINOUT ECU (HaynesPro / Autodata)
// ============================================================================

enum class FuseLocationZone(val label: String) {
  COMPARTIMENT_MOTEUR("Boîtier BSM Compartiment Moteur"),
  HABITACLE_BSI("Boîtier BSI Habitacle (Sous Volant)"),
  COFFRE_BATTERIE("Platine Fusibles Puissance & Coffre")
}

data class FuseBoxItem(
  val id: String,
  val fuseCode: String, // ex: "F15", "F32", "R04 (Relais)"
  val amperage: String, // ex: "15A (Bleu)", "30A (Vert)", "Relais 40A"
  val zone: FuseLocationZone,
  val protectedCircuit: String,
  val symptomsIfBlown: String,
  val nominalVoltageV: Double = 12.4,
  val isIntact: Boolean = true
)

data class EcuPinoutSignal(
  val id: String,
  val ecuName: String, // ex: "Bosch EDC17C60 (PSA 1.6 BlueHDi)"
  val connectorPin: String, // ex: "Connecteur B - Voie 42"
  val wireColorCode: String, // ex: "Jaune / Vert (0.75 mm²)"
  val signalName: String, // ex: "Signal Capteur Pression Rail (FRP)"
  val referenceIdleValue: String, // ex: "1.15 V au ralenti (300 bars)"
  val referenceLoadValue: String, // ex: "3.85 V pleine charge (1600 bars)"
  val oscilloscopePattern: String // ex: "Signal analogique linéaire 0.5V - 4.5V"
)

// ============================================================================
// 2. CALIBRAGE ADAS & DIAGNOSTIC HAUTE TENSION EV / HYBRIDE (SoH Batterie)
// ============================================================================

enum class AdasSystemType(val label: String) {
  CAMERA_PARE_BRISE("Caméra Multifonction Pare-brise (LDW / LKA)"),
  RADAR_FRONTAL_ACC("Radar Anticollision Frontal (AEB / ACC)"),
  CAPTEURS_ANGLES_MORTS("Radars Angles Morts Arrière (BSD)"),
  VISION_360_PARKING("Caméras Vision 360° & Park Assist")
}

data class AdasCalibrationRecord(
  val id: String,
  val vehicleModel: String,
  val systemType: AdasSystemType,
  val calibrationMode: String, // "Statique sur cible CSC" ou "Dynamique sur route"
  val targetBoardCode: String, // ex: "CSC-VW-01 (Distance 1500 mm, Hauteur 1280 mm)"
  val triggerReason: String, // ex: "Remplacement pare-brise acoustique"
  val angleDeviationBeforeDeg: Double,
  val angleDeviationAfterDeg: Double,
  val isCalibrated: Boolean
)

data class EvBatteryHealthReport(
  val id: String,
  val vehicleModel: String,
  val vin: String,
  val batteryChemistry: String, // ex: "Lithium-Ion NMC 400V (50 kWh)"
  val stateOfHealthPercent: Int, // ex: 93%
  val stateOfChargePercent: Int, // ex: 78%
  val cellMinVoltageMv: Int, // ex: 3812 mV
  val cellMaxVoltageMv: Int, // ex: 3826 mV
  val wltpOriginalRangeKm: Int,
  val estimatedRealRangeKm: Int,
  val fastDcChargesCount: Int,
  val acStandardChargesCount: Int,
  val highVoltageLockoutActive: Boolean = false // Consignation Haute Tension (Sécurité Atelier)
) {
  val cellDeltaMv: Int get() = cellMaxVoltageMv - cellMinVoltageMv
}

// ============================================================================
// 3. BULLETINS DE SERVICE TECHNIQUE (TSB), RAPPELS & PANNES CONNUES (Identifix)
// ============================================================================

enum class TsbSeverity(val label: String) {
  RAPPEL_SECURITE("Rappel Officiel Constructeur"),
  MALADIE_CHRONIQUE("Panne Connue Série / Maladie Moteur"),
  NOTE_TECHNIQUE_OEM("Bulletin Technique Atelier (TSB)")
}

data class TechnicalServiceBulletin(
  val id: String,
  val bulletinNumber: String, // ex: "TSB-PSA-B1AW012Q0"
  val brandAndModels: String,
  val engineCodes: String, // ex: "DV5RC (1.5 BlueHDi 130) / EB2ADTS (1.2 PureTech)"
  val severity: TsbSeverity,
  val title: String,
  val relatedDtcCodes: String, // ex: "P0016, P0011, P0299"
  val symptomDescription: String,
  val verifiedRootCause: String,
  val officialOemFix: String,
  val modifiedPartReference: String, // ex: "Kit Chaîne 8mm réf. 16 381 598 80"
  val verifiedSuccessRatePercent: Int, // ex: 96%
  val reportedCasesCount: Int // ex: 418 ateliers Mekanik+
)

// ============================================================================
// 4. BOURSE PIÈCES D'OCCASION CERTIFIÉES (CASSES AUTO) & CONSIGNES ÉCHANGE STANDARD
// ============================================================================

enum class UsedPartGrade(val code: String, val description: String) {
  GRADE_A("Grade A+", "État proche du neuf • Faible kilométrage • Testé sur banc"),
  GRADE_B("Grade B", "Bon état fonctionnel • Usure normale • Garantie 6 mois"),
  RECONDITIONNE("Échange Standard", "Reconditionné à neuf en usine • Garantie 12 mois")
}

data class CertifiedUsedPartOffer(
  val id: String,
  val partTitle: String,
  val oemReference: String,
  val donorVehicleInfo: String, // ex: "VW Golf 7 2.0 TDI (2019 • 64 000 km)"
  val donorVinMasked: String, // ex: "WVWZZZAUZKW******"
  val paintColorCode: String?, // ex: "LA7W (Gris Reflet d'Argent)" ou null pour mécanique
  val grade: UsedPartGrade,
  val recyclerName: String, // ex: "Casse Agréée AutoRecycl' Alger-Est"
  val warrantyMonths: Int,
  val priceDzd: Int,
  val newOemPriceDzd: Int, // Pour afficher l'économie réalisée
  val isReserved: Boolean = false
) {
  val savingsPercent: Int
    get() = if (newOemPriceDzd > 0) (((newOemPriceDzd - priceDzd).toDouble() / newOemPriceDzd) * 100).toInt() else 0
}

enum class CoreReturnStatus(val label: String) {
  A_RESTITUER_FOURNISSEUR("Vieille matière à renvoyer"),
  EXPEDIE_EN_CONTROLE("En cours d'expertise usine"),
  CONSIGNE_REMBOURSEE("Caution remboursée")
}

data class CoreReturnConsigneItem(
  val id: String,
  val partName: String, // ex: "Turbo Garrett Échange Standard 1.5 dCi"
  val coreReference: String,
  val supplierName: String,
  val depositAmountDzd: Int, // Montant de la consigne (ex: 18 000 DA)
  val deadlineDate: String,
  val status: CoreReturnStatus
)

// ============================================================================
// 5. REGISTRE CLIMATISATION A/C (GAZ R134a / R1234yf) & TRAÇABILITÉ FLUIDES
// ============================================================================

enum class RefrigerantGasType(val code: String, val label: String) {
  R134A("R134a", "Gaz Standard HFC R134a"),
  R1234YF("R1234yf", "Gaz Écologique HFO R1234yf (Post-2017)")
}

data class AcVehicleSpecification(
  val id: String,
  val vehicleModel: String,
  val productionYears: String,
  val gasType: RefrigerantGasType,
  val targetGasWeightGrams: Int, // ex: 450 g (±15g)
  val compressorOilType: String, // ex: "PAG 46 / SP-A2"
  val totalOilCapacityMl: Int, // ex: 120 ml
  val lowPressureNominalBar: String, // ex: "1.8 - 2.3 bar"
  val highPressureNominalBar: String // ex: "11.5 - 15.0 bar"
)

data class AcServiceChargeRecord(
  val id: String,
  val dateFormatted: String,
  val vehicleModel: String,
  val licensePlate: String,
  val gasType: RefrigerantGasType,
  val recoveredGrams: Int,
  val injectedGrams: Int,
  val oilInjectedMl: Int,
  val uvTracerAdded: Boolean,
  val vacuumTestPassed: Boolean,
  val ventOutputTempCelsius: Double // ex: 5.4 °C
)

// ============================================================================
// 6. ÉTAT DES LIEUX PHOTO LOCATION, CAUTION & CONTRATS COURT TERME (Turo/Getaround)
// ============================================================================

enum class RentalDepositStatus(val label: String) {
  CAUTION_BLOQUEE("Caution bloquée"),
  CAUTION_LIBEREE("Caution restituée intégralement"),
  RETENUE_PARTIELLE("Retenue frais / dépassement")
}

data class ShortTermRentalContract(
  val id: String,
  val contractNumber: String,
  val vehicleModel: String,
  val licensePlate: String,
  val renterFullName: String,
  val renterPhone: String,
  val renterLicenseNumber: String,
  val checkOutDate: String,
  val checkInDate: String,
  val dailyRateDzd: Int,
  val rentalDays: Int,
  val includedKmPerDay: Int,
  val departureKm: Int,
  val returnKm: Int,
  val departureFuelEighths: Int, // sur 8 (ex: 8/8 = Plein)
  val returnFuelEighths: Int, // sur 8 (ex: 6/8)
  val depositAmountDzd: Int, // ex: 50 000 DA
  val depositStatus: RentalDepositStatus,
  val checkOutPhotosCount: Int = 8,
  val checkInPhotosCount: Int = 8,
  val newScratchesDetected: Boolean = false,
  val isClosedAndSigned: Boolean = false
) {
  val allowedTotalKm: Int get() = rentalDays * includedKmPerDay
  val drivenKm: Int get() = (returnKm - departureKm).coerceAtLeast(0)
  val extraKm: Int get() = (drivenKm - allowedTotalKm).coerceAtLeast(0)
  val extraKmFeeDzd: Int get() = extraKm * 25 // 25 DA / km supplémentaire
  val missingFuelEighths: Int get() = (departureFuelEighths - returnFuelEighths).coerceAtLeast(0)
  val missingFuelFeeDzd: Int get() = missingFuelEighths * 800 // 800 DA par 1/8ème manquant + service
  val baseRentalTotalDzd: Int get() = dailyRateDzd * rentalDays
  val totalFinalInvoiceDzd: Int get() = baseRentalTotalDzd + extraKmFeeDzd + missingFuelFeeDzd
}
