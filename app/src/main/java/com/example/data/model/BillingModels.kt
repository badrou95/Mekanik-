package com.example.data.model

enum class InvoiceType(val label: String) {
  FACTURE_PROFORMA("Facture Proforma"),
  FACTURE_DEFINITIVE("Facture Fiscale Définitive"),
  BON_DE_LIVRAISON("Bon de Livraison Atelier")
}

enum class PaymentMethod(val label: String) {
  CIB_EDAHABIA("Carte CIB / Edahabia"),
  VIREMENT_BANCAIRE("Virement Bancaire (RIB)"),
  ESPECES("Espèces / Au Comptant"),
  CHEQUE("Chèque Bancaire")
}

data class InvoiceLineItem(
  val reference: String,
  val description: String,
  val quantity: Int,
  val unitPriceDzd: Double,
  val isLabor: Boolean = false
) {
  val totalLineDzd: Double get() = quantity * unitPriceDzd
}

data class FiscalInvoice(
  val invoiceNumber: String,
  val invoiceType: InvoiceType,
  val date: String,
  val garageName: String = "MEKANIK+ PRO SERVICES",
  val garageNif: String = "002016098745632",
  val garageNis: String = "198745632001",
  val garageRc: String = "16/00-0987654B20",
  val garageAddress: String = "Zone Industrielle Oued Smar, Alger",
  val clientName: String,
  val clientPhone: String,
  val vehicleVin: String,
  val vehicleModel: String,
  val plateNumber: String,
  val items: List<InvoiceLineItem>,
  val tvaPercent: Double = 19.0,
  val stampTaxDzd: Double = 100.0,
  val isPaid: Boolean = false,
  val paymentMethod: PaymentMethod? = null,
  val qrFiscalHash: String = "DZ-MEKANIK-2026-FCT-98421"
) {
  val subTotalDzd: Double get() = items.sumOf { it.totalLineDzd }
  val tvaAmountDzd: Double get() = subTotalDzd * (tvaPercent / 100.0)
  val totalTtcDzd: Double get() = subTotalDzd + tvaAmountDzd + stampTaxDzd
}
