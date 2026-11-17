package com.example.data.model

data class Vendor(
  val id: String,
  val name: String,
  val wilaya: String,
  val wilayaCode: Int,
  val commune: String,
  val address: String,
  val phone: String,
  val specialty: String, // "VAG & Allemandes", "Multimarque", "Françaises & Dacia", "Asiatiques", "Pneumatiques & Freins"
  val isVerified: Boolean = true,
  val isPremium: Boolean = false,
  val registerCommerceNumber: String, // RC: 16/00-0984214B20
  val rating: Float = 4.8f,
  val reviewCount: Int = 86,
  val deliveryAvailable: Boolean = true,
  val inStockItemsCount: Int = 1420
)

data class VendorStockItem(
  val partId: String,
  val vendorId: String,
  val vendorName: String,
  val wilaya: String,
  val priceDzd: Int,
  val stockQuantity: Int,
  val brand: String, // "OEM VW", "Brembo", "Bosch"
  val condition: String = "Neuf sous emballage",
  val phone: String
)
