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
  val signatureName: String = "",
  val checklist: RepairOrderInspectionChecklist = RepairOrderInspectionChecklist.defaultSample()
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

enum class MaintenanceTaskPriority(val label: String, val colorHex: Long) {
  URGENT("Haute Priorité", 0xFFE53935),
  NORMAL("Priorité Normale", 0xFFFF7A00),
  PROGRAMME("Entretien Périodique", 0xFF00E5FF),
  FLOTTE("Contrat Flotte", 0xFF00E676)
}

enum class MaintenanceTaskStatus(val label: String, val colorHex: Long) {
  EN_ATTENTE("En Attente", 0xFFFF7A00),
  EN_COURS("En Cours", 0xFF2979FF),
  TERMINE("Validé / Terminé", 0xFF00E676),
  REPORTE("Reporté", 0xFF9AA5B1)
}

data class DailyMaintenanceTask(
  val id: String,
  val taskTitle: String,
  val category: String, // "Vidange & Filtres", "Freinage & Sécurité", "Climatisation", "Diagnostic & ADAS", "Distribution", "Pré-Contrôle"
  val vehicleModel: String,
  val licensePlate: String,
  val clientName: String,
  val clientPhone: String = "0550 12 34 56",
  val scheduledTime: String, // e.g. "09:30"
  val estimatedMinutes: Int = 60,
  val priority: MaintenanceTaskPriority = MaintenanceTaskPriority.NORMAL,
  val status: MaintenanceTaskStatus = MaintenanceTaskStatus.EN_ATTENTE,
  val assignedMechanic: String = "Karim B.",
  val bayNumber: Int = 1,
  val requiredPartsSummary: String = "Filtres Purflux + Huile Synthèse",
  val partsAvailable: Boolean = true,
  val notes: String = ""
)
