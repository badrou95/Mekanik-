package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

data class QrOrderedPart(
  val partId: String,
  val name: String,
  val oemReference: String,
  val brandRequested: String, // e.g. "Brembo", "Bosch", "Gates", "Valeo"
  val quantity: Int,
  val unitPriceDzd: Int,
  val status: QrPartOrderStatus,
  val systemCategory: String = "Freinage"
) {
  val totalPriceDzd: Int get() = unitPriceDzd * quantity
}

enum class QrPartOrderStatus(val label: String) {
  DEMANDEE_CLIENT("Demandée par client"),
  RESERVEE_MAGASIN("Réservée en stock"),
  PRETE_COMPTOIR("Prête au comptoir"),
  LIVREE("Livrée / Montée")
}

data class QrRepairOrderInfo(
  val orderNumber: String,
  val dateCreated: String,
  val interventionType: String,
  val reportedSymptoms: String,
  val diagnosticSummary: String,
  val assignedTechnician: String,
  val estimatedLaborHours: Float,
  val estimatedCostDzd: Int,
  val status: RepairStatus
)

data class QrRentalContractInfo(
  val contractNumber: String,
  val agencyName: String,
  val agencyPhone: String,
  val renterFullName: String,
  val renterPhone: String,
  val driverLicenseNumber: String,
  val startDate: String,
  val expectedEndDate: String,
  val initialMileageKm: Int,
  val fuelLevelPct: Int,
  val dailyRateDzd: Int,
  val depositAmountDzd: Int,
  val returnNotes: String = ""
)

data class VehiclePassportQrData(
  val qrToken: String, // e.g. "MK-QR-2026-VAG-04829"
  val registeredByRole: AccountRole,
  val registeredByName: String,
  val registrationDate: String,
  val vehicleVin: String,
  val vehiclePlate: String,
  val brand: String,
  val model: String,
  val exactYear: Int,
  val engineCode: String,
  val displacement: String,
  val powerCh: Int,
  val fuelType: String,
  val gearbox: String,
  val currentMileageKm: Int,
  val healthScorePct: Int,
  val lastServiceDate: String,
  val nextServiceKm: Int,
  val qrVerificationHash: String,
  // Multi-Role Data Packs
  val orderedPartsForRetailer: List<QrOrderedPart> = emptyList(),
  val activeRepairOrdersForWorkshop: List<QrRepairOrderInfo> = emptyList(),
  val rentalContractInfo: QrRentalContractInfo? = null,
  val recentCertifiedRecords: List<DigitalServiceRecord> = emptyList()
) {
  val partsTotalValueDzd: Int get() = orderedPartsForRetailer.sumOf { it.totalPriceDzd }
}
