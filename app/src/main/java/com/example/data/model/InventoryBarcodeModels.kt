package com.example.data.model

import java.util.UUID

enum class StockMovementType(val label: String, val signMultiplier: Int, val badgeColorHex: Long) {
  ENTREE_ACHAT("Entrée Fournisseur (+)", 1, 0xFF00E676),
  SORTIE_ATELIER("Sortie Réparation Atelier (-)", -1, 0xFFFF7A00),
  SORTIE_VENTE_COMPTOIR("Vente Magasin Comptoir (-)", -1, 0xFF00E5FF),
  AJUSTEMENT_INVENTAIRE("Ajustement Inventaire (±)", 0, 0xFFFFC107),
  RETOUR_FOURNISSEUR("Retour / Avoir Garantie", -1, 0xFFE53935)
}

enum class AbcRotationClass(val code: String, val label: String, val badgeColorHex: Long) {
  CLASS_A("A", "Classe A • Rotation Rapide", 0xFF00E676),
  CLASS_B("B", "Classe B • Rotation Régulière", 0xFF00E5FF),
  CLASS_C("C", "Classe C • Rotation Spécifique", 0xFFFF7A00)
}

data class BarcodeInventoryItem(
  val id: String = UUID.randomUUID().toString(),
  val barcode: String, // EAN-13, QR Code ou code-barres interne
  val oemReference: String,
  val partName: String,
  val brand: String,
  val category: String,
  val currentStock: Int,
  val minAlertThreshold: Int = 3,
  val maxTargetStock: Int = 15,
  val unitPurchaseCostDzd: Int,
  val unitSalePriceDzd: Int,
  val shelfLocation: String = "Rayon A2 • Étagère E3 • Bac B04",
  val supplierName: String = "Sarl Auto Pièces Alger",
  val compatibilityVehicles: List<String> = emptyList(),
  val lastRestockDate: String = "12/10/2026",
  val rotationClass: AbcRotationClass = AbcRotationClass.CLASS_A
) {
  val isLowStock: Boolean get() = currentStock <= minAlertThreshold
  val isOutOfStock: Boolean get() = currentStock <= 0
  val unitMarginDzd: Int get() = (unitSalePriceDzd - unitPurchaseCostDzd).coerceAtLeast(0)
  val marginPercent: Int
    get() = if (unitSalePriceDzd > 0) ((unitMarginDzd.toDouble() / unitSalePriceDzd.toDouble()) * 100).toInt() else 0
  val totalPurchaseValueDzd: Long get() = currentStock.toLong() * unitPurchaseCostDzd.toLong()
  val totalSaleValueDzd: Long get() = currentStock.toLong() * unitSalePriceDzd.toLong()
  val recommendedOrderQty: Int get() = (maxTargetStock - currentStock).coerceAtLeast(minAlertThreshold + 2)
}

data class StockMovementLog(
  val id: String = UUID.randomUUID().toString(),
  val itemId: String,
  val partName: String,
  val oemReference: String = "",
  val barcode: String,
  val type: StockMovementType,
  val quantity: Int,
  val previousStock: Int = 0,
  val newStock: Int = 0,
  val unitValueDzd: Int = 0,
  val dateFormatted: String,
  val operatorName: String,
  val destinationRef: String // Numéro d'Ordre de Réparation, BL Fournisseur ou Facture Comptoir
)

enum class SupplierOrderStatus(val label: String, val colorHex: Long) {
  A_COMMANDER("À Commander (Urgent)", 0xFFE53935),
  ENVOYEE("BC Envoyé Fournisseur", 0xFFFF7A00),
  EN_TRANSIT("En Livraison", 0xFF00E5FF),
  RECEPTIONNEE("Réceptionné en Stock", 0xFF00E676)
}

data class SupplierOrderLine(
  val itemId: String,
  val oemReference: String,
  val partName: String,
  val brand: String,
  val quantityOrdered: Int,
  val unitPurchaseCostDzd: Int
) {
  val lineTotalDzd: Int get() = quantityOrdered * unitPurchaseCostDzd
}

data class SupplierPurchaseOrder(
  val id: String = UUID.randomUUID().toString(),
  val orderNumber: String,
  val supplierName: String,
  val createdAt: String,
  val expectedDeliveryDate: String,
  val status: SupplierOrderStatus,
  val lines: List<SupplierOrderLine>,
  val notes: String = ""
) {
  val totalOrderCostDzd: Int get() = lines.sumOf { it.lineTotalDzd }
  val totalUnitsCount: Int get() = lines.sumOf { it.quantityOrdered }
}

// --- SCAN BON D'ACHAT FOURNISSEUR (OCR IA) + DISPATCH RAYON/ÉTAGÈRE/BAC + VALIDATION PRIX + IMPRESSION ÉTIQUETTES ---

enum class SalePriceValidationChoice(val label: String) {
  KEEP_SAME_PRICE("Garder le même prix de vente"),
  CHANGE_SALE_PRICE("Modifier le prix de vente")
}

data class ScannedPurchaseInvoiceLine(
  val id: String = UUID.randomUUID().toString(),
  val matchedInventoryItemId: String?,
  val oemReference: String,
  val barcode: String,
  val partName: String,
  val brand: String,
  val category: String,
  val quantityReceived: Int,
  val previousStock: Int,
  val previousPurchaseCostDzd: Int,
  val scannedPurchaseCostDzd: Int,
  val previousSalePriceDzd: Int,
  val priceChoice: SalePriceValidationChoice = SalePriceValidationChoice.KEEP_SAME_PRICE,
  val customSalePriceDzd: Int = previousSalePriceDzd,
  val assignedRayon: String,
  val assignedEtagere: String,
  val assignedBac: String
) {
  val finalValidatedSalePriceDzd: Int
    get() = if (priceChoice == SalePriceValidationChoice.KEEP_SAME_PRICE) previousSalePriceDzd else customSalePriceDzd

  val formattedFullLocation: String
    get() = "$assignedRayon • $assignedEtagere • $assignedBac"

  val lineTotalPurchaseDzd: Int
    get() = quantityReceived * scannedPurchaseCostDzd

  val newMarginDzd: Int
    get() = (finalValidatedSalePriceDzd - scannedPurchaseCostDzd).coerceAtLeast(0)

  val newMarginPercent: Int
    get() = if (finalValidatedSalePriceDzd > 0) {
      ((newMarginDzd.toDouble() / finalValidatedSalePriceDzd.toDouble()) * 100).toInt()
    } else 0

  val purchasePriceDeltaDzd: Int
    get() = scannedPurchaseCostDzd - previousPurchaseCostDzd
}

data class ScannedPurchaseInvoice(
  val id: String = UUID.randomUUID().toString(),
  val invoiceNumber: String,
  val supplierName: String,
  val invoiceDate: String,
  val lines: List<ScannedPurchaseInvoiceLine>,
  val isIntegrated: Boolean = false
) {
  val totalUnitsCount: Int get() = lines.sumOf { it.quantityReceived }
  val totalInvoicePurchaseDzd: Int get() = lines.sumOf { it.lineTotalPurchaseDzd }
}

data class PrintedThermalLabel(
  val id: String = UUID.randomUUID().toString(),
  val invoiceNumber: String,
  val oemReference: String,
  val barcode: String,
  val partName: String,
  val brand: String,
  val category: String,
  val rayon: String,
  val etagere: String,
  val bac: String,
  val validatedSalePriceDzd: Int,
  val copiesToPrint: Int,
  val isPrinted: Boolean = false,
  val printedTimestamp: String = ""
) {
  val fullShelfCode: String get() = "$rayon • $etagere • $bac"
}
