package com.example.data.model

import java.util.UUID

enum class NotificationTemplateType(
  val label: String,
  val iconEmoji: String,
  val defaultSubject: String
) {
  DEVIS_PRET("Devis Atelier Prêt", "📋", "Votre devis de réparation est disponible"),
  VEHICULE_REPARE("Véhicule Réparé & Prêt", "✅", "Votre véhicule est prêt à être récupéré"),
  PIECES_EN_STOCK("Pièces Reçues en Stock", "📦", "Vos pièces détachées commandées sont arrivées"),
  RAPPEL_ENTRETIEN("Rappel Entretien / Contrôle", "⏱️", "Rappel : Entretien périodique recommandé"),
  FACTURE_DISPONIBLE("Facture & Paiement", "🧾", "Facture d'atelier disponible"),
  DIAGNOSTIC_AI_TERMINE("Rapport Diagnostic Électronique", "🧠", "Rapport de diagnostic moteur finalisé")
}

data class MessageNotificationTemplate(
  val id: String = UUID.randomUUID().toString(),
  val type: NotificationTemplateType,
  val title: String,
  val templateText: String
)

data class ClientMessageDispatch(
  val id: String = UUID.randomUUID().toString(),
  val clientName: String,
  val clientPhone: String,
  val vehicleModel: String,
  val licensePlate: String,
  val quoteOrInvoiceAmountDzd: String,
  val garageName: String = "Garage Auto Performance Alger",
  val garagePhone: String = "021 68 40 12",
  val templateType: NotificationTemplateType = NotificationTemplateType.VEHICULE_REPARE,
  val customText: String = "",
  val sentViaWhatsApp: Boolean = false,
  val sentViaSms: Boolean = false,
  val lastSentTimestamp: String = ""
)
