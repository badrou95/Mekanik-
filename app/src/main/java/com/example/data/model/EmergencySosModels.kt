package com.example.data.model

enum class BreakdownType(val label: String) {
  MOTEUR_SURCHAUFFE("Surchauffe Moteur / Voyant Rouge"),
  CREVAISON_ROUE("Crevaison / Pneu Éclaté"),
  BATTERIE_VIDE("Batterie Déchargée / Ne démarre pas"),
  ACCIDENT_CHOC("Accident / Choc Véhicule"),
  TRANSMISSION_BOITE("Boîte de Vitesse / Embrayage HS"),
  AUTRE_PANNE("Autre panne mécanique")
}

data class TowTruckService(
  val id: String,
  val companyName: String,
  val driverName: String,
  val phone: String,
  val distanceKm: Double,
  val estimatedArrivalMinutes: Int,
  val baseRateDzd: Double,
  val wilayaLocation: String,
  val rating: Float,
  val isAvailable24_7: Boolean = true,
  val truckType: String = "Plateau Hydraulique Lourd"
)

data class SosEmergencyRequest(
  val requestId: String,
  val breakdownType: BreakdownType,
  val gpsLocationString: String,
  val userVehicle: String,
  val userPhone: String,
  val selectedTowTruck: TowTruckService? = null,
  val status: String = "Recherche dépanneuse à proximité..."
)
