package com.example.data.model

/**
 * Modèles DMS inspirés de WinMotor (WinMotor2 / WinMotor Next — Solware Auto)
 * et de la passerelle officielle Peugeot Service Box V14 / Crescendo+ (Stellantis).
 */

enum class WinMotorLaborTier(
  val code: String,
  val title: String,
  val description: String,
  val defaultRateDzdPerHour: Int
) {
  T1_COURANT(
    code = "T1",
    title = "T1 — Entretien Courant & Révision Rapide",
    description = "Vidange, filtration, plaquettes/disques, pneumatiques, batterie, balais d'essuie-glace",
    defaultRateDzdPerHour = 2500
  ),
  T2_MECANIQUE_COMPLEXE(
    code = "T2",
    title = "T2 — Mécanique Lourde & Organes",
    description = "Distribution, kit chaîne AAC DV5 8mm, embrayage bi-masse, turbo, boîte de vitesses, culasse",
    defaultRateDzdPerHour = 3800
  ),
  T3_HAUTE_TECHNICITE(
    code = "T3",
    title = "T3 — Haute Technicité, Diag BSI & VE/Hybride",
    description = "Diagnostic DiagBox/PP2000, télécodage calculateur BSI/ECU, calibrage ADAS, haute tension 400V",
    defaultRateDzdPerHour = 5500
  )
}

data class WinMotorWorkshopRatesConfig(
  val rateT1Dzd: Int = 2500,
  val rateT2Dzd: Int = 3800,
  val rateT3Dzd: Int = 5500,
  val smallSuppliesPercentage: Int = 4, // Petites fournitures / consommables d'atelier (%)
  val wasteRecyclingFeeDzd: Int = 500 // Participation recyclage fluides & déchets
) {
  fun getRateForTier(tier: WinMotorLaborTier): Int = when (tier) {
    WinMotorLaborTier.T1_COURANT -> rateT1Dzd
    WinMotorLaborTier.T2_MECANIQUE_COMPLEXE -> rateT2Dzd
    WinMotorLaborTier.T3_HAUTE_TECHNICITE -> rateT3Dzd
  }
}

/**
 * Opération barèmée constructeur chiffrée en centièmes d'heure (standard WinMotor / Service Box : 1h00 = 100 cth).
 */
data class WinMotorFlatRateOperation(
  val id: String,
  val oemOperationCode: String, // ex: "0129-A4" ou "1638-B2"
  val label: String,
  val vehicleApplication: String,
  val centiemesHeure: Int, // ex: 340 cth = 3.40 h = 3h24min
  val laborTier: WinMotorLaborTier,
  val includesDiagnosticPass: Boolean = false,
  val isSelectedForOrder: Boolean = false
) {
  val decimalHours: Double
    get() = centiemesHeure / 100.0

  val formattedDurationReadable: String
    get() {
      val hours = centiemesHeure / 100
      val remCentiemes = centiemesHeure % 100
      val minutes = (remCentiemes * 60) / 100
      return "${hours}h${minutes.toString().padStart(2, '0')} (${centiemesHeure} cth)"
    }

  fun computeLaborCostDzd(rates: WinMotorWorkshopRatesConfig): Int {
    val hourlyRate = rates.getRateForTier(laborTier)
    return ((centiemesHeure * hourlyRate) / 100.0).toInt()
  }
}

data class ServiceBoxPartLine(
  val oemRef: String,
  val designation: String,
  val quantity: Int,
  val unitPriceHtDzd: Int
)

/**
 * Panier exporté depuis Peugeot Service Box V14 / CatCar / 7Zap vers WinMotor DMS (Passerelle DMS).
 */
data class ServiceBoxBasketImport(
  val id: String,
  val basketReference: String, // ex: "SBX-V14-2026-884"
  val sourceCatalog: String, // ex: "Peugeot Service Box V14 GAR • Crescendo+"
  val vin: String,
  val oprDamCode: String,
  val vehicleModel: String,
  val partsList: List<ServiceBoxPartLine>,
  val associatedOperations: List<WinMotorFlatRateOperation>,
  val isImportedIntoActiveOrder: Boolean = false
) {
  val totalPartsHtDzd: Int
    get() = partsList.sumOf { it.quantity * it.unitPriceHtDzd }

  val totalCentiemesHeure: Int
    get() = associatedOperations.sumOf { it.centiemesHeure }
}

enum class CompanionClockStatus(
  val label: String,
  val badgeText: String
) {
  EN_COURS("Pointage Actif sur Pont", "EN COURS"),
  EN_PAUSE_PIECE("Pause (Attente Pièce / Magasin)", "PAUSE PIÈCE"),
  TERMINE("Travaux Pointés & Clôturés", "TERMINÉ")
}

/**
 * Pointage Compagnon (Chronométrage Mécanicien par Ordre de Réparation) dans WinMotor.
 */
data class CompanionTimeClockEntry(
  val id: String,
  val companionName: String,
  val companionMatricule: String,
  val specialty: String,
  val repairOrderNumber: String,
  val vehicleAndPlate: String,
  val operationTitle: String,
  val laborTier: WinMotorLaborTier,
  val soldCentiemesBarème: Int, // Temps vendu selon barème constructeur (ex: 340 cth)
  val clockedCentiemesReal: Int, // Temps réellement pointé par le compagnon (ex: 290 cth)
  val status: CompanionClockStatus,
  val bayName: String
) {
  val productivityPercent: Int
    get() = if (clockedCentiemesReal <= 0) 100 else ((soldCentiemesBarème.toDouble() / clockedCentiemesReal.toDouble()) * 100).toInt()

  val timeSavedOrLostCentiemes: Int
    get() = soldCentiemesBarème - clockedCentiemesReal
}

/**
 * Ventilation Multi-Payeurs sur un même Ordre de Réparation (Client / Garantie Constructeur / Cession Interne / Assurance).
 */
data class MultiPayerSplitOrder(
  val id: String,
  val repairOrderNumber: String,
  val vehicleModel: String,
  val vin: String,
  val clientName: String,
  val totalOrderAmountHtDzd: Int,
  val clientSharePercent: Int, // ex: 30%
  val manufacturerWarrantyPercent: Int, // ex: 50% (Garantie / Campagne Rappel Constructeur Peugeot)
  val internalCessionPercent: Int, // ex: 20% (Cession Interne Atelier / Préparation VO)
  val insuranceSharePercent: Int = 0,
  val warrantyClaimReference: String, // ex: "GAR-PSA-DV5-2026-912"
  val isValidatedForInvoicing: Boolean = false
) {
  val clientAmountDzd: Int
    get() = (totalOrderAmountHtDzd * clientSharePercent) / 100

  val warrantyAmountDzd: Int
    get() = (totalOrderAmountHtDzd * manufacturerWarrantyPercent) / 100

  val internalCessionAmountDzd: Int
    get() = (totalOrderAmountHtDzd * internalCessionPercent) / 100

  val insuranceAmountDzd: Int
    get() = (totalOrderAmountHtDzd * insuranceSharePercent) / 100
}

enum class BackorderVorPriority(
  val label: String,
  val badgeText: String
) {
  VOR_PONT_BLOQUE("URGENCE VOR • Véhicule Immobilisé sur Pont", "URGENCE VOR"),
  COMMANDE_FERME_CLIENT("Commande Ferme Client (Véhicule Roulant)", "COMMANDE CLIENT"),
  REAPPRO_STOCK_MAGASIN("Réapprovisionnement Stock Magasin", "STOCK MAGASIN")
}

/**
 * Gestion des Reliquats Magasin & Commandes VOR (Vehicle Off Road) reliées aux Ordres de Réparation.
 */
data class BackorderVorItem(
  val id: String,
  val oemReference: String,
  val partDesignation: String,
  val linkedRepairOrderNumber: String,
  val clientName: String,
  val vehicleModel: String,
  val blockedBayLabel: String?, // ex: "Pont N°2 Immobilisé"
  val supplierName: String,
  val priority: BackorderVorPriority,
  val expectedArrivalTime: String,
  val priceDzd: Int,
  val isReceivedAndAllocated: Boolean = false
)
