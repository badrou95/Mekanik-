package com.example.data.model

enum class MapPinCategory(
  val id: String,
  val label: String,
  val badgeText: String,
  val colorHex: Long
) {
  PARTS_STORE("PARTS_STORE", "Magasins & Vendeurs de Pièces", "🏪 VENDEUR PIÈCES", 0xFFFF7A00),
  WORKSHOP_GARAGE("WORKSHOP_GARAGE", "Ateliers & Garagistes (Montage)", "🛠️ ATELIER MONTAGE", 0xFF00E5FF),
  CAR_RENTAL_SHOWROOM("CAR_RENTAL_SHOWROOM", "Showrooms Location de Voitures", "🚘 SHOWROOM LOCATION", 0xFF00E676)
}

data class ExactMapLocationPin(
  val id: String,
  val name: String,
  val category: MapPinCategory,
  val wilaya: String,
  val exactAddress: String,
  val latitude: Double,
  val longitude: Double,
  val phone: String,
  val rating: Double,
  val openingHours: String,
  val highlightBadge: String,
  val availableStockOrFleetSummary: String,
  val mountingLaborPriceRangeDzd: String = "",
  val canvasXPercent: Float,
  val canvasYPercent: Float
)

data class WorkshopMountingPriceEstimate(
  val workshopName: String,
  val wilaya: String,
  val distanceKm: Double,
  val mountingLaborDzd: Int,
  val estimatedDurationMinutes: Int,
  val includesWarrantyAndDiag: String,
  val phone: String,
  val hasPartInWorkshopStock: Boolean
)

data class SmartExplodedCartItem(
  val id: String,
  val partOrHarnessName: String,
  val categoryLabel: String,
  val isElectricalHarnessOrWiring: Boolean,
  val calloutNumber: Int,
  val sourceVin: String,
  val sourceVehicleName: String,
  val primaryOemReference: String,
  val compatibleReferences: List<String>,
  val compatibleVehicles: List<String>,
  val unitPriceEstimatedDzd: Int,
  val quantity: Int = 1,
  val matchingVendorsInStock: List<CartStockHolderMatch>,
  val matchingWorkshopsInStock: List<CartStockHolderMatch>,
  val mountingEstimates: List<WorkshopMountingPriceEstimate>
)

data class CartStockHolderMatch(
  val holderId: String,
  val holderName: String,
  val isWorkshop: Boolean,
  val wilaya: String,
  val exactAddress: String,
  val latitude: Double,
  val longitude: Double,
  val matchedReference: String,
  val quantityAvailable: Int,
  val partPriceDzd: Int,
  val mountingPriceDzd: Int?,
  val phone: String
)

data class FreeAccessSearchItem(
  val id: String,
  val keywordTags: List<String>,
  val partName: String,
  val vehicleApplication: String,
  val isElectricalWiring: Boolean,
  val primaryOemRef: String,
  val crossReferences: List<String>,
  val compatibleVehicles: List<String>,
  val bestPartPriceDzd: Int,
  val averageMountingPriceDzd: Int,
  val mountingTimeMinutes: Int,
  val diyDifficultyLabel: String,
  val diyRepairStepsPreview: List<String>,
  val fullRepairManualLockedNote: String,
  val stockVendors: List<CartStockHolderMatch>,
  val partnerGarages: List<WorkshopMountingPriceEstimate>
)

enum class ParticulierPlanTier(
  val id: String,
  val title: String,
  val annualPriceDzd: Int,
  val includedVinsCount: Int,
  val extraVinPriceDzd: Int,
  val badgeText: String
) {
  BASIC_DIY_3500(
    id = "BASIC_DIY_3500",
    title = "Compte Particulier Basique (Auto-Entretien DIY)",
    annualPriceDzd = 3500,
    includedVinsCount = 1,
    extraVinPriceDzd = 1200,
    badgeText = "3 500 DA / AN (1 CHÂSSIS INCLUS)"
  ),
  FULL_LIBRE_10000(
    id = "FULL_LIBRE_10000",
    title = "Compte Accès Libre Intégral (Expert Sans Limite)",
    annualPriceDzd = 10000,
    includedVinsCount = 5,
    extraVinPriceDzd = 1000,
    badgeText = "10 000 DA / AN (ACCÈS TOTAL DÉBLOQUÉ)"
  )
}

data class RegisteredParticulierVin(
  val vin: String,
  val vehicleLabel: String,
  val engineCode: String,
  val year: Int,
  val isPrimaryIncludedVin: Boolean,
  val extraFeePaidDzd: Int
)

data class ParticulierAccountState(
  val currentTier: ParticulierPlanTier = ParticulierPlanTier.BASIC_DIY_3500,
  val registeredVins: List<RegisteredParticulierVin> = listOf(
    RegisteredParticulierVin(
      vin = "VF32HNFUF42891034",
      vehicleLabel = "Peugeot 206+ 1.4i 75ch (TU3JP)",
      engineCode = "KFW / TU3JP",
      year = 2012,
      isPrimaryIncludedVin = true,
      extraFeePaidDzd = 0
    )
  ),
  val extraVinUnitFeeDzd: Int = 1200,
  val statusMessage: String = "Compte Particulier Actif (3 500 DA/an) • 1 châssis inclus • +1 200 DA par châssis supplémentaire"
) {
  val extraVinsCount: Int
    get() = (registeredVins.size - 1).coerceAtLeast(0)

  val totalAnnualCostDzd: Int
    get() = if (currentTier == ParticulierPlanTier.FULL_LIBRE_10000) {
      10000 + ((registeredVins.size - 5).coerceAtLeast(0) * 1000)
    } else {
      currentTier.annualPriceDzd + (extraVinsCount * extraVinUnitFeeDzd)
    }

  val hasFullUnrestrictedRepairManuals: Boolean
    get() = currentTier == ParticulierPlanTier.FULL_LIBRE_10000
}
