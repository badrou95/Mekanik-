package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

// ============================================================================
// MEKANIK+ SHIELD : FIREBASE AUTH RBAC (SuperAdmin / Gestionnaire / Technicien)
// + LES 6 BOUCLIERS DE DÉFENSE CYBER-SÉCURITÉ & PROTECTION DMS
// ============================================================================

enum class FirebaseRbacRole(
  val id: String,
  val title: String,
  val shortBadge: String,
  val description: String,
  val colorHex: Long,
  // Permissions DMS & Données Sensibles
  val canAccessSuperAdminConsole: Boolean,
  val canApproveSubscriptionsAndKeys: Boolean,
  val canRemoteWipeCacheAndDevices: Boolean,
  val canViewDmsMarginsAndAccounting: Boolean,
  val canModifyTariffRatesT1T2T3: Boolean,
  val canDeleteOrDiscountInvoices: Boolean,
  val canCreateAndUpdateRepairOrders: Boolean,
  val canUse11ApiCascadeAndObd: Boolean
) {
  SUPER_ADMIN(
    id = "SUPER_ADMIN",
    title = "SuperAdmin (Propriétaire Plateforme)",
    shortBadge = "SUPER-ADMIN • ACCÈS TOTAL",
    description = "Contrôle absolu : Console Super-Admin (badroumc@gmail.com), validation des clés d'activation, révocation à distance AES-256 (Remote Wipe), audit HMAC-SHA256, DMS complet & tarifs T1/T2/T3.",
    colorHex = 0xFFE53935,
    canAccessSuperAdminConsole = true,
    canApproveSubscriptionsAndKeys = true,
    canRemoteWipeCacheAndDevices = true,
    canViewDmsMarginsAndAccounting = true,
    canModifyTariffRatesT1T2T3 = true,
    canDeleteOrDiscountInvoices = true,
    canCreateAndUpdateRepairOrders = true,
    canUse11ApiCascadeAndObd = true
  ),
  GESTIONNAIRE(
    id = "GESTIONNAIRE",
    title = "Gestionnaire (Chef d'Atelier / Gérant DMS)",
    shortBadge = "GESTIONNAIRE • DMS & FINANCES",
    description = "Accès complet au DMS WinMotor, marges nettes, facturation DZD, modification des taux horaires T1/T2/T3, remises clients et gestion des 3 appareils de l'atelier (sans console SuperAdmin globale).",
    colorHex = 0xFFFF7A00,
    canAccessSuperAdminConsole = false,
    canApproveSubscriptionsAndKeys = false,
    canRemoteWipeCacheAndDevices = false,
    canViewDmsMarginsAndAccounting = true,
    canModifyTariffRatesT1T2T3 = true,
    canDeleteOrDiscountInvoices = true,
    canCreateAndUpdateRepairOrders = true,
    canUse11ApiCascadeAndObd = true
  ),
  TECHNICIEN(
    id = "TECHNICIEN",
    title = "Technicien / Mécanicien Compagnon",
    shortBadge = "TECHNICIEN • OPÉRATIONS & DIAGNOSTIC",
    description = "Accès restreint aux opérations techniques : Décodage VIN (Cascade 11 API), pointage compagnon sur OR, couples de serrage, fluides DCT et diagnostic OBD-II. Accès BLOQUÉ aux marges nettes, CA global, suppression de factures et modification des tarifs T1/T2/T3.",
    colorHex = 0xFF00E5FF,
    canAccessSuperAdminConsole = false,
    canApproveSubscriptionsAndKeys = false,
    canRemoteWipeCacheAndDevices = false,
    canViewDmsMarginsAndAccounting = false,
    canModifyTariffRatesT1T2T3 = false,
    canDeleteOrDiscountInvoices = false,
    canCreateAndUpdateRepairOrders = true,
    canUse11ApiCascadeAndObd = true
  );

  companion object {
    fun fromId(raw: String?): FirebaseRbacRole {
      return entries.firstOrNull { it.id.equals(raw, ignoreCase = true) } ?: TECHNICIEN
    }
  }
}

// Modèle Firestore pour `/users/{userId}`
@IgnoreExtraProperties
data class FirestoreUserRbacProfile(
  @DocumentId val id: String = "",
  val userId: String = "",
  val email: String = "",
  val displayName: String = "",
  val rbacRole: String = FirebaseRbacRole.TECHNICIEN.id,
  val boundHardwareDeviceId: String = "",
  val wilaya: String = "16 - Alger",
  val operatorPinHash: String? = null,
  val isRemoteWiped: Boolean? = false,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

// Modèle Firestore pour `/user_account_selections/{userId}` (Composant AccountSelector)
@IgnoreExtraProperties
data class FirestoreUserAccountSelection(
  @DocumentId val id: String = "",
  val userId: String = "",
  val email: String = "",
  val displayName: String = "",
  val accountRole: String = AccountRole.GARAGE_PRO.name,
  val organizationName: String = "",
  val wilaya: String = "16 - Alger",
  val subAccountsCount: Int = 2,
  val subAccountsSummary: String = "",
  val subscriptionPriceDzd: Int = 18000,
  val mecaVerifFeePerVinDzd: Int = 250,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
) {
  val parsedAccountRole: AccountRole
    get() = AccountRole.fromId(accountRole)
}

// Modèle Firestore pour `/security_audit_logs/{logId}`
@IgnoreExtraProperties
data class FirestoreSecurityAuditEvent(
  @DocumentId val id: String = "",
  val userId: String = "",
  val operatorName: String = "",
  val rbacRole: String = FirebaseRbacRole.TECHNICIEN.id,
  val shieldCategory: String = "RBAC_AUDIT",
  val actionDescription: String = "",
  val hmacSha256Seal: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null
)

// ============================================================================
// LES 6 BOUCLIERS DE DÉFENSE « MEKANIK+ SHIELD »
// ============================================================================

// Bouclier 1 : Hardware Binding & Anti-Partage de Compte (Max 3 appareils / Geo-Velocity Check)
data class HardwareBoundDeviceRecord(
  val deviceSlotNumber: Int, // 1, 2, 3 (ou 4 = Intrus bloqué)
  val deviceName: String,
  val hardwareKeystoreId: String,
  val boundWilaya: String,
  val ipAddressMasked: String,
  val lastActiveStatus: String,
  val isAuthorized: Boolean,
  val isGeoVelocityAnomaly: Boolean = false
)

// Bouclier 2 : Anti-Scraping / Anti-Aspiration des 11 APIs & Tatouage Invisible (Watermarking)
data class AntiScrapingShieldStatus(
  val currentVinVelocityPer2Min: Int = 4,
  val maxAllowedVelocityPer2Min: Int = 15,
  val isCircuitBreakerTripped: Boolean = false,
  val blockedBotAttemptsCount: Int = 18,
  val activeWatermarkToken: String = "WM-MK1-DZ16-9F4A2C8E",
  val lastBlockedBotSignature: String = "Python-Requests/2.31 (IP 197.204.xx.xx • 84 req/min bloquée)"
)

// Bouclier 3 : Signature Cryptographique Anti-Falsification HMAC-SHA256 (OR, Factures DZD & Passeport QR)
data class SignedDocumentSealRecord(
  val docId: String,
  val docTypeBadge: String, // "FACTURE FISCALE DZD", "ORDRE DE RÉPARATION (OR)", "PASSEPORT QR VÉHICULE"
  val docTitle: String,
  val vin: String,
  val certifiedMileageKm: Int,
  val certifiedAmountDzd: Int,
  val hmacSha256Signature: String,
  val isTamperedDetected: Boolean = false,
  val tamperAlertDetail: String? = null
)

// Bouclier 4 : Coffre-Fort Local Chiffré AES-256-GCM & Auto-Destruction à Distance (Remote Wipe)
data class Aes256LocalVaultStatus(
  val cipherAlgorithm: String = "AES-256-GCM (Android Keystore Hardware-Backed + SQLCipher Room)",
  val encryptedRoomEntriesCount: Int = 3,
  val apiKeysZeroPlaintextInApk: Boolean = true,
  val isVaultLockedOrWiped: Boolean = false,
  val lastKeyRotationTimestamp: String = "Rotation auto clé maître il y a 2h"
)

// Bouclier 5 : Détection d'Environnement Compromis (Anti-Root, Anti-Patch APK, Anti-Time-Rollback)
data class PlayIntegrityEnvironmentCheck(
  val apkSignatureVerified: Boolean = true,
  val apkCertSha256Preview: String = "FE:06:1D:07...FF:21:A0:A5",
  val rootOrFridaDetected: Boolean = false,
  val ntpClockDriftSeconds: Int = 0,
  val isTimeRollbackAttackBlocked: Boolean = false,
  val securityScorePercent: Int = 100
)

// Bouclier 6 : Verrouillage Kiosque PIN Mécanicien & Matrice de Permissions DMS (RBAC)
data class DmsProtectedOperationCheck(
  val operationId: String,
  val operationTitle: String,
  val sensitiveCategory: String,
  val minimumRoleRequired: FirebaseRbacRole,
  val requiresManagerPinInKiosk: Boolean = true
)

data class DefenseShieldItem(
  val shieldNumber: Int,
  val title: String,
  val subtitle: String,
  val technicalSpec: String,
  val statusLabel: String,
  val isEnabled: Boolean = true,
  val minimumRoleToConfigure: FirebaseRbacRole = FirebaseRbacRole.SUPER_ADMIN
)

// ============================================================================
// ESPACE VENDEUR : GESTION DE STOCK TEMPS RÉEL (FIRESTORE + SYNC CATALOGUE)
// ============================================================================

enum class VendorStockAvailabilityStatus(
  val id: String,
  val label: String,
  val colorHex: Long
) {
  EN_STOCK("EN_STOCK", "En Stock Immédiat", 0xFF00E676),
  STOCK_FAIBLE("STOCK_FAIBLE", "Stock Faible (< 5)", 0xFFFFB300),
  SUR_COMMANDE("SUR_COMMANDE", "Sur Commande (24-48h)", 0xFF00E5FF),
  RUPTURE("RUPTURE", "Rupture de Stock", 0xFFE53935);

  companion object {
    fun fromId(raw: String?): VendorStockAvailabilityStatus {
      return entries.firstOrNull { it.id.equals(raw, ignoreCase = true) } ?: EN_STOCK
    }
  }
}

@IgnoreExtraProperties
data class FirestoreVendorStockItem(
  @DocumentId val id: String = "",
  val userId: String = "",
  val vendorName: String = "Sarl Auto Pièces Alger",
  val partName: String = "",
  val oemReference: String = "",
  val iamReference: String = "",
  val quantityInStock: Int = 0,
  val priceDzd: Int = 0,
  val availabilityStatus: String = VendorStockAvailabilityStatus.EN_STOCK.id,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
) {
  val parsedAvailability: VendorStockAvailabilityStatus
    get() = VendorStockAvailabilityStatus.fromId(availabilityStatus)
}

