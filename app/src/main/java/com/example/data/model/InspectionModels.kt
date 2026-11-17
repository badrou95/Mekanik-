package com.example.data.model

import java.util.UUID

enum class InspectionViewAngle(val label: String, val iconDescription: String) {
  AVANT("Face Avant", "Capot, calandre, pare-brise avant, phares"),
  ARRIERE("Face Arrière", "Coffre, pare-chocs arrière, feux, lunette"),
  PROFIL_GAUCHE("Profil Gauche", "Portières avant/arrière gauche, ailes, rétroviseur G"),
  PROFIL_DROIT("Profil Droit", "Portières avant/arrière droit, ailes, rétroviseur D"),
  TOIT("Toit & Vitrage", "Pavillon de toit, toit panoramique, montants"),
  HABITACLE("Habitacle & TDB", "Tableau de bord, sellerie, volant, commandes")
}

enum class DamageSeverity(val label: String, val colorHex: Long) {
  LEGER("Léger / Mineur", 0xFF00E676),      // Vert/Cyan
  MOYEN("Moyen / Visible", 0xFFFF7A00),     // Orange mécanique
  CRITIQUE("Critique / À réparer", 0xFFE53935) // Rouge performance
}

enum class DamageType(val label: String, val iconEmoji: String) {
  RAYURE("Rayure carrosserie", "⚡"),
  BOSSE("Bosse / Enfoncement", "🔨"),
  IMPACT_VITRAGE("Impact pare-brise / vitrage", "💎"),
  FISSURE("Fissure pare-chocs", "⚠️"),
  PIECE_MANQUANTE("Élément manquant", "🧩"),
  VOYANT_TABLEAU("Voyant anomalie allumé", "🚨"),
  PNEU_USE("Usure pneumatique / Jante", "⚙️"),
  AUTRE("Autre anomalie notée", "📝")
}

data class DamageMarker(
  val id: String = UUID.randomUUID().toString(),
  val viewAngle: InspectionViewAngle,
  val xRatio: Float, // 0.0f .. 1.0f relative to canvas
  val yRatio: Float, // 0.0f .. 1.0f relative to canvas
  val damageType: DamageType = DamageType.RAYURE,
  val severity: DamageSeverity = DamageSeverity.MOYEN,
  val description: String = "",
  val photoLabel: String = "Photo_01.jpg",
  val hasPhotoAttached: Boolean = true,
  val timestampFormatted: String = "16:45"
)

data class VehicleInspection(
  val id: String = UUID.randomUUID().toString(),
  val inspectionNumber: String = "INSP-2026-0042",
  val vin: String = "VF77J9HP4EJ123456",
  val licensePlate: String = "01429-122-16",
  val vehicleModel: String = "Peugeot 308 (1.6 BlueHDi 120)",
  val clientName: String = "Sofiane Benali",
  val clientPhone: String = "0550 12 34 56",
  val inspectorName: String = "Mourad (Chef d'Atelier)",
  val inspectionType: String = "Entrée Réparation / Prise en charge",
  val dateFormatted: String = "17 Octobre 2026",
  val mileageKm: String = "142 850",
  val fuelPercent: Int = 65, // 0 to 100%
  val exteriorCleanliness: String = "Correcte",
  val interiorCleanliness: String = "Propre",
  val markers: List<DamageMarker> = emptyList(),
  val clientSignatureAccepted: Boolean = true,
  val clientSignatureDate: String = "17/10/2026 16:50",
  val generalNotes: String = "Véhicule déposé pour révision distribution et diagnostic bruit avant droit. Carrosserie propre sauf rayure aile avant gauche signalée."
)
