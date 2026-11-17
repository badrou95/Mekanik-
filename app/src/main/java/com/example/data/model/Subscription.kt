package com.example.data.model

data class PricingPlan(
  val id: String,
  val title: String,
  val subtitle: String,
  val priceDzd: String,
  val billingPeriod: String, // "par an", "par mois", "à l'acte"
  val targetUser: String, // "Particulier", "Atelier & Mécanicien", "Vendeur de Pièces"
  val features: List<String>,
  val isPopular: Boolean = false,
  val badgeText: String? = null
)

data class VinReport(
  val vin: String,
  val brandModel: String,
  val engineNumber: String,
  val gearboxCode: String,
  val firstRegistrationDate: String,
  val technicalControlStatus: String,
  val mileageHistory: List<MileageRecord>,
  val recallCampaigns: List<String>,
  val reportToken: String
)

data class MileageRecord(
  val date: String,
  val km: Int,
  val source: String
)
