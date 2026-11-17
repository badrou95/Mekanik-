package com.example.data.model

enum class DriverGuideCategory(val label: String, val iconTag: String) {
  NIVEAUX_FLUIDES("Niveaux & Fluides", "water_drop"),
  FREINAGE_PNEUS("Freins & Pneumatiques", "disc_full"),
  FILTRES_MOTEUR("Filtres & Admission", "air"),
  VISIBILITE_ECLAIRAGE("Éclairage & Visibilité", "light_mode"),
  ELECTRICITE_BATTERIE("Batterie & Fusibles", "bolt"),
  VOYANTS_DIAGNOSTIC("Voyants & Diagnostic", "warning")
}

data class MaintenanceStep(
  val stepNumber: Int,
  val title: String,
  val instruction: String,
  val cautionTip: String? = null
)

data class DriverMaintenanceMethod(
  val id: String,
  val title: String,
  val category: DriverGuideCategory,
  val difficulty: String, // "Facile (Débutant)", "Moyen (Intermédiaire)", "Avancé"
  val durationMinutes: Int,
  val estimatedSavingsDzd: Long,
  val toolsNeeded: List<String>,
  val safetyTips: List<String>,
  val steps: List<MaintenanceStep>,
  val relatedPartKeyword: String,
  val isRecommendedNow: Boolean = false,
  val intervalKmOrMonths: String = "Tous les 10 000 km / 1 an"
)

data class DriverRepairRecord(
  val id: String,
  val date: String,
  val vehiclePlate: String,
  val vehicleModel: String,
  val garageName: String,
  val garageWilaya: String,
  val interventionType: String,
  val description: String,
  val replacedParts: List<String>,
  val laborHours: Float,
  val partsCostDzd: Long,
  val laborCostDzd: Long,
  val totalAmountDzd: Long,
  val invoiceNumber: String,
  val isCertified: Boolean,
  val odometerKm: Int
)

data class DriverInvoiceItem(
  val id: String,
  val invoiceNumber: String,
  val date: String,
  val garageName: String,
  val garagePhone: String,
  val vehicleInfo: String,
  val mileageKm: Int,
  val partsAmountDzd: Long,
  val laborAmountDzd: Long,
  val totalAmountDzd: Long,
  val paymentMethod: String,
  val status: String,
  val itemsDetail: List<String>
)

data class DriverVehicleSummary(
  val plateNumber: String,
  val brandModel: String,
  val exactYear: Int,
  val engine: String,
  val vin: String,
  val currentMileageKm: Int,
  val lastServiceDate: String,
  val nextServiceDueKm: Int,
  val technicalInspectionDue: String,
  val insuranceExpiry: String,
  val healthScorePct: Int = 94
)
