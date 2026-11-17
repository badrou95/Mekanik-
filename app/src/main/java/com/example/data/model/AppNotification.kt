package com.example.data.model

data class AppNotification(
  val id: String,
  val title: String,
  val message: String,
  val category: NotificationCategory,
  val timestamp: String, // e.g. "Il y a 10 min", "Aujourd'hui à 08:30"
  val isRead: Boolean = false,
  val vehicleVin: String? = null,
  val actionTag: String? = null,
  val priority: NotificationPriority = NotificationPriority.NORMAL
)

enum class NotificationCategory(val label: String, val colorHex: Long) {
  ENTRETIEN_RAPPEL("Rappel Entretien", 0xFFFF7A00),
  RDV_ATELIER("Rendez-vous Atelier", 0xFF00E676),
  PIECE_DISPONIBLE("Disponibilité Pièces", 0xFF2979FF),
  MEKANIK_AI_ALERTE("Alerte MekanikAI", 0xFF00E5FF),
  OFFRE_VENDEUR("Offres & Promotions", 0xFFAB47BC)
}

enum class NotificationPriority {
  NORMAL,
  HIGH,
  URGENT
}
