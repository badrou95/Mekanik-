package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class UltraProSubAccountRole(
  val label: String,
  val shortLabel: String,
  val department: String,
  val description: String,
  val maxLimit: Int,
  val badgeColorHex: Long
) {
  GERANT_PRINCIPAL(
    label = "Administrateur Général / Gérant",
    shortLabel = "Gérant Principal",
    department = "Direction Générale",
    description = "Accès maître absolu, gouvernance 360°, gestion des droits de tous les sous-comptes, validation finale et traçabilité.",
    maxLimit = 1,
    badgeColorHex = 0xFFE53935 // Performance Red
  ),
  CHEF_CONSEILLER_CLIENT(
    label = "Chef Conseiller Client / Team Leader Réception",
    shortLabel = "Team Leader Réception",
    department = "Accueil & Réception",
    description = "Supervision de l'accueil, coordination des conseillers clients, affectation des créneaux prioritaires et validation des devis complexes.",
    maxLimit = 1,
    badgeColorHex = 0xFFFF5252 // Coral Red
  ),
  CONSEILLER_CLIENT(
    label = "Conseiller Client / Accueil Atelier",
    shortLabel = "Conseiller Client",
    department = "Accueil & Réception",
    description = "Accueil physique et téléphonique des clients, prise de rendez-vous, ouverture d'Ordres de Réparation (OR) et devis.",
    maxLimit = 2,
    badgeColorHex = 0xFFFF7A00 // Mechanical Orange
  ),
  CHEF_ATELIER(
    label = "Chef d'Atelier & Responsable Technique",
    shortLabel = "Chef d'Atelier",
    department = "Atelier Mécanique",
    description = "Supervise les 5 mécaniciens, valide et approuve techniquement les demandes de pièces émises par les techniciens selon le châssis.",
    maxLimit = 1,
    badgeColorHex = 0xFFFFAB40 // Amber Glow
  ),
  MECANICIEN_ATELIER(
    label = "Mécanicien / Technicien d'Atelier",
    shortLabel = "Mécanicien",
    department = "Atelier Mécanique",
    description = "Exécute les réparations, effectue le diagnostic et formule les demandes de pièces de rechange requises selon le VIN/Châssis.",
    maxLimit = 5,
    badgeColorHex = 0xFF40C4FF // Vivid Cyan
  ),
  ADMIN_MAGASIN(
    label = "Chef Département Magasin & Gestion Stock",
    shortLabel = "Chef Magasinier",
    department = "Magasin & Pièces",
    description = "Responsable en chef du magasin : confirme la sortie définitive des pièces du stock et leur report direct sur facture/OR client.",
    maxLimit = 1,
    badgeColorHex = 0xFF00E5FF // Tech Cyan
  ),
  VENDEUR_MAGASIN(
    label = "Magasinier / Vendeur Comptoir Pièces",
    shortLabel = "Magasinier Comptoir",
    department = "Magasin & Pièces",
    description = "Reçoit les demandes de pièces validées par le chef d'atelier, prépare les pièces en rayon selon le véhicule et sert au comptoir.",
    maxLimit = 2,
    badgeColorHex = 0xFF2979FF // Electric Blue
  ),
  COMPTABLE_PRINCIPAL(
    label = "Comptabilité Générale & Clôture Fiscale",
    shortLabel = "Comptable",
    department = "Finance & Comptabilité",
    description = "Gestion de la trésorerie, suivi de la TVA (19%), timbres fiscaux, encaissements CIB/Edahabia et bilans fiscaux.",
    maxLimit = 1,
    badgeColorHex = 0xFF00E676 // Electric Green
  ),
  ANALYSTE_FACTURES(
    label = "Analyste Factures & Suivi Règlements",
    shortLabel = "Analyste Factures",
    department = "Finance & Comptabilité",
    description = "Analyse et vérification des factures clients/fournisseurs, rapprochement des devis/bons de livraison et suivi des impayés.",
    maxLimit = 2,
    badgeColorHex = 0xFFFFD600 // Warning Yellow
  ),
  AUDITEUR_QUALITE(
    label = "Auditeur Qualité & Contrôle Interne",
    shortLabel = "Auditeur Qualité",
    department = "Audit & Conformité",
    description = "Contrôle qualité des interventions, vérification du respect des normes constructeurs, audit des mouvements de stock et marges.",
    maxLimit = 1,
    badgeColorHex = 0xFFD500F9 // Purple Neon
  )
}

data class UltraProSubAccount(
  val id: String,
  val fullName: String,
  val email: String,
  val phone: String,
  val role: UltraProSubAccountRole,
  val accessPin: String,
  val isActive: Boolean = true,
  val permissions: List<String>,
  val avatarInitials: String,
  val lastActive: String,
  val actionsCount: Int = 0
) {
  val department: String get() = role.department
}

// --- WORKFLOW FLUX PIÈCES ATELIER (4 ÉTAPES) ---
enum class PartsRequisitionStatus(
  val label: String,
  val stepIndex: Int,
  val colorHex: Long,
  val badgeText: String,
  val stepDescription: String
) {
  DEMANDE_MECANICIEN(
    label = "1. Demande Mécanicien émise",
    stepIndex = 1,
    colorHex = 0xFF2979FF,
    badgeText = "ÉTAPE 1 / 4",
    stepDescription = "Le mécanicien a identifié et demandé les pièces nécessaires selon le véhicule/châssis."
  ),
  VALIDEE_CHEF_ATELIER(
    label = "2. Validée par le Chef d'Atelier",
    stepIndex = 2,
    colorHex = 0xFFFF7A00,
    badgeText = "ÉTAPE 2 / 4",
    stepDescription = "Le Chef d'Atelier a validé techniquement la conformité des pièces demandées."
  ),
  EN_PREPARATION_MAGASIN(
    label = "3. Reçue & Préparée par le Magasinier",
    stepIndex = 3,
    colorHex = 0xFF00E5FF,
    badgeText = "ÉTAPE 3 / 4",
    stepDescription = "Le magasinier a reçu la demande avec les données châssis et préparé les pièces dans les casiers."
  ),
  CONFIRMEE_SORTIE_STOCK(
    label = "4. Sortie Validée par Chef Magasin (Sur Facture & OR)",
    stepIndex = 4,
    colorHex = 0xFF00E676,
    badgeText = "ÉTAPE 4 / 4 : FINALISÉ",
    stepDescription = "Le Chef Magasin a confirmé la sortie de stock. Les pièces sont automatiquement imputées sur l'OR et la Facture client."
  ),
  REJETEE(
    label = "Rejetée / Non Conforme",
    stepIndex = 0,
    colorHex = 0xFFE53935,
    badgeText = "REJETÉE",
    stepDescription = "La demande a été refusée pour non-conformité technique ou indisponibilité."
  )
}

data class RequisitionPartItem(
  val partId: String,
  val oemReference: String,
  val name: String,
  val brand: String,
  val quantity: Int,
  val unitPriceDzd: Int,
  val stockAvailable: Int = 5
) {
  val totalPriceDzd: Int get() = quantity * unitPriceDzd
}

data class WorkshopPartsRequisition(
  val id: String,
  val orderNumber: String,
  val linkedRepairOrderId: String,
  val vehicleModel: String,
  val vehicleVin: String,
  val plateNumber: String,
  val requestedByTechnicianId: String,
  val requestedByTechnicianName: String,
  val parts: List<RequisitionPartItem>,
  val status: PartsRequisitionStatus,
  val createdAt: String,
  val approvedByWorkshopManagerAt: String? = null,
  val workshopManagerName: String? = null,
  val receivedByStorekeeperAt: String? = null,
  val storekeeperName: String? = null,
  val confirmedByHeadStorekeeperAt: String? = null,
  val headStorekeeperName: String? = null,
  val notes: String = ""
) {
  val totalRequisitionDzd: Int get() = parts.sumOf { it.totalPriceDzd }
}

data class SubAccountExtensionPack(
  val packId: String = "ext_pack_2_accounts",
  val title: String = "Pack Extension +2 Sous-Comptes",
  val additionalAccounts: Int = 2,
  val priceDzd: Int = 2500, // 2 000 à 3 000 DA
  val description: String = "Ajoutez 2 sous-comptes supplémentaires à votre entreprise Ultra Pro (Mécaniciens, Conseillers, etc.)."
)

data class SubAccountAuditLog(
  val id: String,
  val authorName: String,
  val authorRole: String,
  val actionTitle: String,
  val details: String,
  val timestamp: String,
  val department: String,
  val severity: String = "NORMAL" // NORMAL, IMPORTANT, CRITIQUE
)

data class UltraProOrganizationSummary(
  val companyName: String = "Groupe Auto Mekanik Alger Ultra Pro",
  val subscriptionPlan: String = "Ultra Pro Multi-Équipes",
  val priceDzdAnnual: Int = 38000,
  val renewalDate: String = "19 Août 2027",
  val activeSubAccountsCount: Int = 12,
  val maxSubAccountsAllowed: Int = 16,
  val totalSalesTodayDzd: Int = 425000,
  val totalStockItemsManaged: Int = 14200,
  val pendingInvoicesToAnalyze: Int = 14,
  val purchasedExtensionPacksCount: Int = 3
)
