package com.example.data.model

enum class FleetVehicleStatus(val label: String) {
  EN_SERVICE("En Service"),
  EN_MAINTENANCE("En Maintenance"),
  CONTROLE_REQUIS("Contrôle Requis"),
  IMMOBILISE("Immobilisé")
}

data class FleetVehicle(
  val id: String,
  val plateNumber: String,
  val brandModel: String,
  val vin: String,
  val assignedDriver: String,
  val driverPhone: String,
  val currentMileageKm: Int,
  val nextServiceKm: Int,
  val insuranceExpiryDate: String,
  val technicalControlDate: String,
  val vignetteTaxYear: Int,
  val fuelConsumptionAvg: Double,
  val status: FleetVehicleStatus,
  val monthlyMaintenanceCostDzd: Double
)

data class FleetOverviewSummary(
  val totalVehicles: Int,
  val activeInService: Int,
  val inMaintenance: Int,
  val pendingAlertsCount: Int,
  val totalMonthlyBudgetDzd: Double,
  val fleetCategory: String = "Flotte Transport & Logistique (Alger - Oran)"
)
