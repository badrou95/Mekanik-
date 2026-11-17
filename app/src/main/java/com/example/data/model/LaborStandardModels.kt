package com.example.data.model

import java.util.UUID

enum class LaborCategory(val label: String, val iconEmoji: String) {
  DISTRIBUTION("Distribution & Courroies", "⚙️"),
  FREINAGE("Freinage & ABS", "🛑"),
  EMBRAYAGE_TRANSMISSION("Embrayage & Boîte", "🔄"),
  SUSPENSION_DIRECTION("Suspension & Direction", "🛞"),
  MOTEUR_CULASSE("Moteur & Culasse", "🔧"),
  ECHAPPEMENT_FAP("Échappement & FAP/EGR", "💨"),
  CLIMATISATION("Climatisation & Chauffage", "❄️"),
  ELECTRICITE_DIAGNOSTIC("Diagnostic & Électricité", "⚡")
}

data class LaborStandardOperation(
  val id: String = UUID.randomUUID().toString(),
  val category: LaborCategory,
  val oemCode: String,
  val title: String,
  val standardHours: Double, // Ex: 3.50 h
  val difficultyStars: Int = 3, // 1 to 5
  val engineCompatibility: String = "Multi-Motorisations (HDi, TDI, dCi, Essence)",
  val standardToolsRequired: List<String> = emptyList(),
  val technicalNotes: String = ""
) {
  val formattedTime: String
    get() {
      val hours = standardHours.toInt()
      val minutes = Math.round((standardHours - hours) * 60).toInt()
      return if (minutes == 0) "${hours}h00" else "${hours}h${minutes.toString().padStart(2, '0')}"
    }

  fun calculateCost(hourlyRateDzd: Int): Int {
    return Math.round(standardHours * hourlyRateDzd).toInt()
  }
}
