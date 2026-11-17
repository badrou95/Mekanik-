package com.example.data.model

data class VendorPartOffer(
  val vendorId: String,
  val vendorName: String,
  val wilaya: String,
  val brandName: String, // e.g. "Brembo", "Bosch", "Febi Bilstein", "OEM Original"
  val priceDzd: Double,
  val inStockQuantity: Int,
  val deliveryCarrier: String, // "Yalidine Express (24h)", "ZR Express (48h)", "Kazi Tour"
  val deliveryCostDzd: Double,
  val vendorRating: Float,
  val isCertifiedOem: Boolean = true
)

data class GroupQuoteRequest(
  val quoteId: String,
  val workshopName: String,
  val vehicleVin: String,
  val vehicleModel: String,
  val requestedParts: List<String>,
  val targetWilaya: String,
  val status: String,
  val offersReceived: List<VendorPartOffer>
)
