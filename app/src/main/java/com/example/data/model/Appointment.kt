package com.example.data.model

data class WorkshopAppointment(
  val id: String,
  val clientName: String,
  val clientPhone: String,
  val vehicleModel: String,
  val plateNumber: String,
  val serviceType: ServiceType,
  val appointmentDate: String, // e.g. "20 Août 2026"
  val timeSlot: String, // e.g. "09:30 - 10:30"
  val workshopName: String = "Garage Auto Performance Alger",
  val status: AppointmentStatus = AppointmentStatus.EN_ATTENTE,
  val estimatedDurationHours: Float = 1.5f,
  val estimatedCostDzd: Int = 6500,
  val assignedMechanic: String = "Mohamed K.",
  val clientNotes: String = "",
  val createdAt: String = "17 Août 2026, 15:42"
)

enum class AppointmentStatus(val label: String, val colorHex: Long) {
  EN_ATTENTE("En Attente de Confirmation", 0xFFFF7A00),
  CONFIRME("Confirmé par l'Atelier", 0xFF00E676),
  EN_COURS("Véhicule sur le Pont", 0xFF00E5FF),
  TERMINE("Prestation Terminée", 0xFF2979FF),
  ANNULE("Annulé", 0xFFE53935)
}

enum class ServiceType(val label: String, val defaultPriceDzd: Int, val durationHours: Float, val iconTag: String) {
  VIDANGE_FILTRES("Vidange & Remplacement Filtres", 4500, 1.0f, "oil"),
  DIAGNOSTIC_OBD("Diagnostic Électronique & Valise OBD", 3500, 0.75f, "scan"),
  SYSTEME_FREINAGE("Freinage (Disques & Plaquettes)", 6500, 1.5f, "brake"),
  COURROIE_DISTRIBUTION("Kit Distribution & Pompe à Eau", 22000, 4.0f, "belt"),
  CLIMATISATION("Recharge Climatisation & Gaz R134a/1234yf", 5500, 1.0f, "ac"),
  SUSPENSION_GEOMETRIE("Suspension & Géométrie des Trains", 7000, 2.0f, "alignment"),
  REVISION_COMPLETE("Révision Complète 60 000 / 120 000 km", 14500, 3.0f, "checkup")
}
