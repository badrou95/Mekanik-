package com.example.data.model

data class RepairOrder(
  val id: String,
  val orderNumber: String, // e.g. "OR-2026-0842"
  val clientName: String,
  val clientPhone: String,
  val vehicleModel: String,
  val vin: String,
  val plateNumber: String, // e.g. "04291-120-16"
  val mileageKm: Int,
  val date: String,
  val status: RepairStatus,
  val items: List<RepairItem>,
  val laborHours: Float,
  val laborRatePerHourDzd: Int = 2000,
  val discountDzd: Int = 0,
  val notes: String = "",
  val isSigned: Boolean = false,
  val signatureName: String = ""
) {
  val partsTotalDzd: Int get() = items.sumOf { it.priceDzd * it.quantity }
  val laborTotalDzd: Int get() = (laborHours * laborRatePerHourDzd).toInt()
  val totalGrossDzd: Int get() = partsTotalDzd + laborTotalDzd
  val netTotalDzd: Int get() = (totalGrossDzd - discountDzd).coerceAtLeast(0)
}

enum class RepairStatus(val label: String, val colorHex: Long) {
  DIAGNOSTIC("En Diagnostic", 0xFF00E5FF),
  PIECES_COMMANDEES("Pièces Commandées", 0xFFFF7A00),
  EN_COURS("En Cours de Réparation", 0xFF2979FF),
  TERMINE("Réparation Terminée", 0xFF00E676),
  FACTURE("Devis / Facturé", 0xFFE53935)
}

data class RepairItem(
  val name: String,
  val reference: String,
  val quantity: Int,
  val priceDzd: Int
)

data class MaintenanceAlert(
  val id: String,
  val title: String,
  val vehicle: String,
  val severity: AlertSeverity,
  val dueInfo: String,
  val recommendedAction: String
)

enum class AlertSeverity {
  URGENT,
  PREVENTIF,
  CONSEILLE
}
