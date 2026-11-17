package com.example.data.model

enum class RentalStatus(val label: String, val colorHex: String) {
  DISPONIBLE("Disponible au Parc", "#00E676"),
  EN_LOCATION("En Contrat Location", "#2979FF"),
  EN_REVISION_ATELIER("En Atelier / Révision", "#E53935"),
  RESERVE("Réservation Confirmée", "#FF7A00"),
  NETTOYAGE_INSPECTION("Inspection & Lavage", "#00E5FF")
}

enum class RentalCategory(val label: String) {
  CITADINE("Citadine Économique"),
  BERLINE_PREMIUM("Berline Routière"),
  SUV_4X4("SUV & Baroudeur 4x4"),
  UTILITAIRE_FOURGON("Utilitaire & Fourgon"),
  LUXE_SPORT("Prestige & Sport")
}

enum class ContractStatus(val label: String) {
  EN_COURS("Location en Cours"),
  CLOTURE("Contrat Clôturé"),
  RESERVATION("Réservation"),
  RETARD_RESTITUTION("Alerte Retard Restitution")
}

data class RentalMaintenanceEntry(
  val id: String,
  val date: String,
  val mileageAtServiceKm: Int,
  val interventionType: String,
  val garageName: String,
  val partsReplaced: List<String>,
  val costDzd: Long,
  val invoiceRef: String,
  val nextDueKm: Int
)

data class RentalVehicle(
  val id: String,
  val plateNumber: String,
  val brandModel: String,
  val category: RentalCategory,
  val year: Int,
  val vin: String,
  val fuelType: String,
  val transmission: String,
  val currentMileageKm: Int,
  val fuelLevelPct: Int,
  val dailyRateDzd: Long,
  val rentalStatus: RentalStatus,
  val currentRenterName: String?,
  val currentContractId: String?,
  val nextScheduledServiceKm: Int,
  val technicalControlExpiry: String,
  val insuranceExpiry: String,
  val totalMaintenanceSpentDzd: Long,
  val maintenanceHistory: List<RentalMaintenanceEntry>
)

data class RentalContract(
  val contractId: String,
  val vehicleId: String,
  val vehicleName: String,
  val plateNumber: String,
  val clientFullName: String,
  val clientPhone: String,
  val clientNationalId: String,
  val clientDrivingLicense: String,
  val startDate: String,
  val endDate: String,
  val durationDays: Int,
  val initialKm: Int,
  val returnKm: Int?,
  val depositAmountDzd: Long,
  val dailyRateDzd: Long,
  val totalRentDzd: Long,
  val status: ContractStatus,
  val isDepositReturned: Boolean = false,
  val notes: String = ""
)

data class RentalFleetSummary(
  val agencyName: String,
  val wilaya: String,
  val totalCars: Int,
  val rentedCars: Int,
  val availableCars: Int,
  val inWorkshopCars: Int,
  val occupancyRatePct: Float,
  val monthlyRentalRevenueDzd: Long,
  val monthlyMaintenanceExpenseDzd: Long,
  val overdueMaintenanceAlertsCount: Int
)
