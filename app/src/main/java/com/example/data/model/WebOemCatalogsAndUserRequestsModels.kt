package com.example.data.model

// ============================================================================
// 1. CATALOGUES WEB OEM GRATUITS & VUES ÉCLATÉES (CatCar, 7Zap, PartsSouq, Nemiga, IFInterface, WebAutoCats)
// ============================================================================

enum class OemBrandGroupFilter(val label: String) {
  TOUS("Toutes Marques"),
  CHINE_SINO_AUTO("Chine (Geely / Jetour / BYD / Chery)"),
  RENAULT_DACIA("Renault / Dacia"),
  STELLANTIS("Stellantis (Peugeot/Citroën/Fiat)"),
  HYUNDAI_KIA("Hyundai & Kia"),
  VAG("Groupe VAG (VW/Audi/Seat/Skoda)")
}

enum class OemTreeBranchCategory(val label: String, val codePrefix: String) {
  MOTEUR("Moteur & Injection", "GRP-10"),
  BOITE_TRANSMISSION("Boîte & Embrayage", "GRP-21"),
  CHASSIS_FREINAGE("Châssis, Trains & Freinage", "GRP-31"),
  ELECTRICITE("Électricité & Éclairage", "GRP-80")
}

data class FreeWebOemCatalogPlatform(
  val id: String,
  val name: String,
  val baseUrl: String,
  val specialtyBadge: String,
  val targetBrandGroups: List<OemBrandGroupFilter>,
  val supportedBrandsText: String,
  val architectureHighlights: String,
  val testScenarioExample: String,
  val supportsVinLookup: Boolean,
  val supportsGearboxEngineSort: Boolean,
  val requiresAccount: Boolean = false
)

data class ExplodedCalloutItem(
  val calloutNumber: Int, // Repère sur la vue éclatée [1], [2], [3]...
  val partName: String,
  val oemReference: String,
  val replacementChainRef: String?,
  val technicalConditionNote: String, // ex: "Boîte TL4-045 / Code PR 1ZE / OPR > 14820"
  val quantityRequired: Int,
  val estimatedOemPriceDzd: Int
)

data class OemExplodedDiagramSheet(
  val id: String,
  val platformSource: String, // ex: "CatCar.info", "7Zap", "PartsSouq", "IFInterface VAG"
  val brandGroup: OemBrandGroupFilter,
  val vehicleTitle: String,
  val engineAndGearboxCode: String, // ex: "1.5 dCi K9K-872 • Boîte TL4-082 (Code Oval B98)"
  val category: OemTreeBranchCategory,
  val diagramCode: String, // ex: "Planche 19.04 - Suralimentation & Échangeur"
  val diagramTitle: String,
  val externalDirectUrl: String,
  val callouts: List<ExplodedCalloutItem>
)

// ============================================================================
// 2. DÉTECTEUR ANTI-ERREUR MONTAGE (Codes PR VAG, N° OPR/DAM Stellantis, Indices Renault)
// ============================================================================

enum class FitmentCodeSystemType(val label: String, val badgeColorHex: Long) {
  WMI_USINE_CHINE("VIN Chine & Plateforme (SinoAuto360 : Jetour / Geely / BYD)", 0xFF00E5FF),
  PR_CODE_VAG("Code PR (VAG : VW / Audi / Seat / Skoda)", 0xFF29B6F6),
  OPR_DAM_PSA("Numéro OPR / DAM (Stellantis : Peugeot / Citroën / Fiat)", 0xFFFF7A00),
  INDICE_RENAULT("Indice Moteur & Code Boîte (Renault / Dacia)", 0xFF00E676),
  MARCHE_MOBIS("Code Marché & Châssis (Hyundai / Kia Mobis)", 0xFFE53935)
}

data class FitmentVerificationRule(
  val id: String,
  val systemType: FitmentCodeSystemType,
  val vehicleModel: String,
  val codeEntered: String, // ex: "1ZE", "OPR 15118", "K9K 872 / TL4"
  val stickerLocationHint: String,
  val partCategory: String,
  val exactMountedSpec: String, // ex: "Disques Avant Ventilés Ø 288 mm × 25 mm (Étrier ATE)"
  val incompatibleTrapSpec: String, // ex: "NE PAS MONTER Ø 280 mm (PR 1ZF) ni Ø 312 mm (PR 1ZA)"
  val verifiedOemRef: String,
  val isVerifiedMatch: Boolean = true
)

// ============================================================================
// 3. RENTABILITÉ OR, PRODUCTIVITÉ MÉCANICIENS & FÛTS D'HUILE VRAC 208L (Tekmetric / Shopmonkey)
// ============================================================================

data class RepairOrderProfitabilityItem(
  val id: String,
  val repairOrderNumber: String,
  val vehicleAndClient: String,
  val mechanicAssigned: String,
  val billedHoursBarème: Double, // Heures facturées selon barème OEM
  val clockedHoursReal: Double, // Heures réellement pointées au chrono
  val partsBuyCostHtDzd: Int,
  val partsSellPriceHtDzd: Int,
  val laborSellPriceHtDzd: Int
) {
  val efficiencyPercent: Int
    get() = if (clockedHoursReal > 0.0) ((billedHoursBarème / clockedHoursReal) * 100.0).toInt() else 100
  val totalRevenueHtDzd: Int
    get() = partsSellPriceHtDzd + laborSellPriceHtDzd
  val netProfitMarginDzd: Int
    get() = (partsSellPriceHtDzd - partsBuyCostHtDzd) + laborSellPriceHtDzd
  val grossMarginPercent: Int
    get() = if (totalRevenueHtDzd > 0) ((netProfitMarginDzd.toDouble() / totalRevenueHtDzd) * 100.0).toInt() else 0
}

data class BulkOilDrumTank(
  val id: String,
  val fluidName: String,
  val viscosityAndNorm: String, // ex: "5W-30 Synthèse • Norme PSA B71 2290 / VW 504.00"
  val drumCapacityLiters: Double = 208.0,
  val currentRemainingLiters: Double,
  val costPerLiterDzd: Int,
  val sellPricePerLiterDzd: Int,
  val supplierName: String
) {
  val fillPercentage: Int
    get() = ((currentRemainingLiters / drumCapacityLiters) * 100.0).toInt().coerceIn(0, 100)
  val isLowLevelAlert: Boolean
    get() = fillPercentage <= 18
}

// ============================================================================
// 4. SCANNER PRÉ-CONTRÔLE TECHNIQUE (ZÉRO CONTRE-VISITE) & VÉHICULES DE COURTOISIE
// ============================================================================

enum class PreCtCheckStatus(val label: String) {
  CONFORME("Conforme"),
  DEFAUT_MINEUR("Défaut Mineur (Sans Contre-Visite)"),
  CONTRE_VISITE_MAJEURE("Défaut Majeur (Contre-Visite Obligatoire)")
}

data class PreCtInspectionPoint(
  val id: String,
  val categoryTitle: String, // ex: "Freinage", "Pollution / Opacité", "Trains Roulants", "Éclairage"
  val checkPointName: String,
  val measuredValueText: String, // ex: "Déséquilibre Av: 8% (Seuil max < 30%)"
  val officialThresholdText: String,
  val status: PreCtCheckStatus,
  val correctiveActionDzd: Int = 0
)

data class CourtesyLoanerVehicle(
  val id: String,
  val vehicleModel: String,
  val licensePlate: String,
  val fuelLevelEighths: Int,
  val dailyPenaltyIfLateDzd: Int,
  val isCurrentlyLoaned: Boolean,
  val assignedClientName: String?,
  val linkedRepairOrderNumber: String?,
  val expectedReturnDate: String?
)
