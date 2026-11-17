package com.example.data.model

enum class RepairStepStatus {
  COMPLETED,
  IN_PROGRESS,
  PENDING
}

data class RepairTimelineStep(
  val stepNumber: Int,
  val title: String,
  val description: String,
  val timestamp: String,
  val status: RepairStepStatus,
  val technicianName: String? = null,
  val inspectionNotes: String? = null,
  val photoProofUrl: String? = null
)

data class LiveRepairTrackingSession(
  val trackingId: String,
  val vehicleVin: String,
  val vehicleName: String,
  val plateNumber: String,
  val clientName: String,
  val clientPhone: String,
  val workshopName: String,
  val currentStageName: String,
  val progressPercent: Int,
  val estimatedReadyTime: String,
  val totalEstimatedPriceDzd: Double,
  val steps: List<RepairTimelineStep>
)

data class DigitalServiceRecord(
  val recordId: String,
  val date: String,
  val mileageKm: Int,
  val operationTitle: String,
  val performedTasks: List<String>,
  val partsReplaced: List<String>,
  val totalCostDzd: Double,
  val garageName: String,
  val certHash: String,
  val isVerified: Boolean = true
)

data class CertifiedServicePassport(
  val vehicleVin: String,
  val vehicleModel: String,
  val firstRegistrationYear: Int,
  val currentMileageKm: Int,
  val ownerName: String,
  val qrValidationCode: String,
  val records: List<DigitalServiceRecord>
)
