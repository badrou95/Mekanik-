package com.example.data.model

data class Vehicle(
  val id: String,
  val vin: String,
  val brand: String,
  val model: String,
  val generation: String, // e.g. "Tiguan II (AD1)", "308 II Phase 2", "Clio V"
  val yearRange: String, // e.g. "2016 - 2024"
  val exactYear: Int,
  val engineCode: String, // e.g. "DFGA", "EA888 Gen3", "DV5RC", "K9K 608"
  val displacement: String, // e.g. "2.0 TDI", "1.5 BlueHDi", "1.5 dCi", "EV 60kWh"
  val powerCh: Int, // e.g. 150
  val powerKw: Int, // e.g. 110
  val fuelType: String, // "Diesel", "Essence", "Hybride", "Électrique (EV)"
  val gearbox: String, // "DSG7 (DQ381)", "BVM6", "EAT8", "Réducteur EV"
  val platform: String, // "MQB", "EMP2", "CMF-B", "TNGA"
  val assemblyPlant: String = "Wolfsburg, Allemagne",
  val isEv: Boolean = false,
  val generationNotes: String = ""
)

enum class SystemCategory(val label: String, val iconName: String, val count: Int) {
  FREINAGE("Freinage", "brake", 24),
  FILTRATION("Filtres", "filter", 18),
  MOTEUR("Moteur & Turbo", "engine", 32),
  DISTRIBUTION("Distribution & Courroie", "belt", 12),
  EMBRAYAGE("Embrayage & Boîte", "clutch", 16),
  SUSPENSION("Suspension & Direction", "suspension", 28),
  REFROIDISSEMENT("Refroidissement", "cooling", 14),
  ELECTRICITE("Électricité & Électronique", "electric", 20),
  VEHICULE_ELECTRIQUE("Système EV & Batterie", "ev_battery", 9),
  CARROSSERIE("Carrosserie & Éclairage", "body", 15)
}

data class PartItem(
  val id: String,
  val system: SystemCategory,
  val name: String,
  val position: String, // "Avant Gauche/Droit", "Essieu Avant", "Courroie Principale"
  val oemReference: String, // High visual prominence e.g. "5Q0 698 151 AP"
  val oemAlternatives: List<String> = emptyList(),
  val crossReferences: List<CrossReference>,
  val schemaCalloutNumber: Int, // Number shown on exploded schema diagram e.g. 1, 2, 3
  val yearCompatibility: String, // "2016-2020 (Réf AP) / 2021-2025 (Réf BR)"
  val compatibilityBadge: String, // "100% Compatible", "Vérifier châssis après Facelift"
  val priceEstimatedDzd: Int, // e.g. 8500
  val description: String,
  val technicalSpecs: Map<String, String> = emptyMap()
)

data class CrossReference(
  val brand: String, // "Brembo", "Bosch", "Ferodo", "TRW", "ATE", "Valeo", "Continental", "Gates", "LuK"
  val reference: String, // "P 85 126", "0 986 494 660"
  val qualityGrade: String = "Qualité Première Monte (OEM EQ)"
)

data class ExplodedHotspot(
  val calloutNumber: Int,
  val label: String,
  val xPercent: Float, // 0f to 100f
  val yPercent: Float,
  val partId: String
)

data class ExplodedSchema(
  val system: SystemCategory,
  val title: String,
  val subTitle: String,
  val diagramType: String, // "etka_brake_front", "timing_belt_exploded", "suspension_exploded"
  val hotspots: List<ExplodedHotspot>
)

data class VehicleDynamicFilterState(
  val textQuery: String = "",
  val selectedBrand: String = "TOUTES",
  val selectedFuelType: String = "TOUTES",
  val selectedGearboxType: String = "TOUTES",
  val selectedYearBracket: String = "TOUTES",
  val selectedPlatform: String = "TOUTES"
) {
  val activeFilterCount: Int
    get() = listOf(
      textQuery.isNotBlank(),
      selectedBrand != "TOUTES",
      selectedFuelType != "TOUTES",
      selectedGearboxType != "TOUTES",
      selectedYearBracket != "TOUTES",
      selectedPlatform != "TOUTES"
    ).count { it }
}

enum class PartPriceBracket(val label: String, val minDzd: Int, val maxDzd: Int) {
  ALL("Tous Prix", 0, Int.MAX_VALUE),
  UNDER_10K("< 10 000 DA", 0, 10000),
  FROM_10K_TO_25K("10k – 25k DA", 10000, 25000),
  ABOVE_25K("> 25 000 DA", 25000, Int.MAX_VALUE)
}

enum class PartSortOption(val label: String) {
  CALLOUT("Repère Schéma (#)"),
  PRICE_ASC("Prix Croissant"),
  PRICE_DESC("Prix Décroissant"),
  NAME_ASC("Nom (A-Z)")
}

data class PartDynamicFilterState(
  val searchQuery: String = "",
  val searchAllSystems: Boolean = false,
  val selectedBrand: String = "TOUTES",
  val selectedPosition: String = "TOUTES",
  val onlyInLiveStock: Boolean = false,
  val only100PercentCompatible: Boolean = false,
  val priceBracket: PartPriceBracket = PartPriceBracket.ALL,
  val sortOption: PartSortOption = PartSortOption.CALLOUT
) {
  val activeFilterCount: Int
    get() = listOf(
      searchQuery.isNotBlank(),
      searchAllSystems,
      selectedBrand != "TOUTES",
      selectedPosition != "TOUTES",
      onlyInLiveStock,
      only100PercentCompatible,
      priceBracket != PartPriceBracket.ALL,
      sortOption != PartSortOption.CALLOUT
    ).count { it }
}

