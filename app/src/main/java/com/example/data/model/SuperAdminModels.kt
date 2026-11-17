package com.example.data.model

enum class AdminApprovalStatus(
  val label: String,
  val badgeText: String
) {
  EN_ATTENTE("En attente de validation", "EN ATTENTE"),
  DEMO_3_JOURS("Compte Démo 3 Jours Actif", "DÉMO 3J (72H)"),
  CLE_REQUISE("Essai 3J Expiré • Clé Requise", "CLÉ REQUISE"),
  APPROUVE("Approuvé & Actif Pro", "APPROUVÉ PRO"),
  REFUSE("Refusé", "REFUSÉ"),
  SUSPENDU("Compte Suspendu", "SUSPENDU")
}

enum class PaymentMethodAdminType(
  val label: String,
  val shortTag: String
) {
  BARIDIMOB_CCP("BaridiMob / Virement CCP", "CCP / BaridiMob"),
  CIB_EDAHABIA("Carte CIB / Edahabia (SATIM)", "CIB / Edahabia"),
  VIREMENT_BANCAIRE("Virement Bancaire Entreprise", "Banque RIB"),
  ESPECES_AGENCE("Espèces / Reçu Agence", "Espèces")
}

enum class DemoTrialPhase(
  val label: String,
  val badgeText: String,
  val defaultRemainingHours: Int
) {
  DEMO_ACTIVE_3_DAYS("Compte Démo 3 Jours Actif (Accès Limité)", "ESSAI DÉMO • 72H", 68),
  DEMO_LAST_DAY_WARNING("Dernier Jour d'Essai Démo (Urgence Clé)", "FIN DÉMO • 5H RESTANTES", 5),
  DEMO_EXPIRED_KEY_REQUIRED("Période Démo 3 Jours Expirée — Clé d'Activation Requise", "VERROUILLÉ • CLÉ REQUISE", 0),
  PRO_ACTIVATED_UNLOCKED("Compte Pro Activé par Clé Officielle", "LICENCE PRO ACTIVE", 8760)
}

data class UserDemoTrialStatus(
  val accountHolderName: String = "Garage Auto Performance • Alger",
  val registeredEmail: String = "atelier.performance@mekanik.dz",
  val phase: DemoTrialPhase = DemoTrialPhase.DEMO_ACTIVE_3_DAYS,
  val totalTrialHours: Int = 72,
  val remainingHours: Int = 68,
  val vinUsedToday: Int = 1,
  val vinDailyDemoLimit: Int = 3,
  val aiDiagUsedToday: Int = 1,
  val aiDiagDailyDemoLimit: Int = 2,
  val oemCatalogViewsToday: Int = 2,
  val oemCatalogDailyDemoLimit: Int = 5,
  val assignedDemoCode: String = "DEMO-3J-2026-ALGER",
  val expectedActivationKey: String = "MKP-ACT-2026-9941",
  val enteredActivationKey: String? = null,
  val activatedByAdminOrStaff: String? = null,
  val isKeyRequestedFromAdmin: Boolean = false
) {
  val isLockedByExpiration: Boolean
    get() = phase == DemoTrialPhase.DEMO_EXPIRED_KEY_REQUIRED

  val isProUnlocked: Boolean
    get() = phase == DemoTrialPhase.PRO_ACTIVATED_UNLOCKED
}

enum class AdminStaffRole(
  val label: String,
  val badgeLabel: String,
  val description: String
) {
  AGENT_INSCRIPTIONS(
    "Agent Validation Inscriptions",
    "INSCRIPTIONS & RC",
    "Vérifie les registres de commerce (RC/NIF), approuve les nouveaux inscrits et active les essais Démo 3 jours."
  ),
  GESTIONNAIRE_LICENCES_DZD(
    "Gestionnaire Licences & Paiements DZD",
    "CLÉS & PAIEMENTS",
    "Vérifie les reçus BaridiMob/CIB/Virement, génère et envoie les Clés d'Activation Pro aux clients."
  ),
  SUPERVISEUR_REGIONAL(
    "Superviseur Régional Wilayas",
    "SUPERVISEUR",
    "Supervise les ateliers et magasins partenaires par région et traite les demandes prioritaires."
  ),
  SUPPORT_TECHNIQUE(
    "Agent Support & Assistance Pro",
    "SUPPORT & DÉMO",
    "Assiste les garages pendant leur période Démo de 3 jours et prépare les dossiers d'activation."
  )
}

data class AdminStaffMember(
  val id: String,
  val fullName: String,
  val email: String,
  val pinCode: String,
  val role: AdminStaffRole,
  val assignedWilayas: String,
  val canApproveRegistrations: Boolean = true,
  val canGenerateActivationKeys: Boolean = true,
  val canValidatePaymentsDzd: Boolean = true,
  val canSuspendAccounts: Boolean = false,
  val processedRequestsCount: Int = 0,
  val generatedKeysCount: Int = 0,
  val isActive: Boolean = true,
  val createdByEmail: String = "badroumc@gmail.com",
  val lastActiveLabel: String = "En ligne aujourd'hui"
)

data class UserRegistrationRequest(
  val id: String,
  val companyOrFullName: String,
  val managerName: String,
  val email: String,
  val phone: String,
  val wilaya: String,
  val requestedRole: AccountRole,
  val rcNumber: String, // Registre de commerce ou N° Permis/CIN pour particulier
  val nifNumber: String, // Identifiant fiscal
  val requestedInitialPlanName: String,
  val submittedDate: String,
  val status: AdminApprovalStatus,
  val generatedLicenseKey: String? = null,
  val adminNote: String = "",
  val demoTrialRemainingHours: Int = 72,
  val isDemoTrialGrantedOnSignup: Boolean = true,
  val processedByOperator: String? = null
)

data class PlanSubscriptionRequest(
  val id: String,
  val accountName: String,
  val userEmail: String,
  val accountRole: AccountRole,
  val wilaya: String,
  val planId: String,
  val planTitle: String,
  val billingPeriod: String,
  val amountDzd: Int,
  val paymentMethod: PaymentMethodAdminType,
  val paymentProofReference: String,
  val submittedDate: String,
  val status: AdminApprovalStatus,
  val includesStoreLinkAddon: Boolean = false,
  val extraSubAccountsCount: Int = 0,
  val validUntilDate: String = "17 Oct 2027",
  val generatedActivationKey: String? = null,
  val processedByOperator: String? = null
)

data class AdminAuditLogEntry(
  val id: String,
  val timestamp: String,
  val adminEmail: String,
  val actionTitle: String,
  val targetEntity: String,
  val impactSummary: String,
  val operatorBadge: String = "SUPER-ADMIN ROOT"
)

data class SuperAdminAccountSession(
  val adminEmail: String = "badroumc@gmail.com",
  val adminFullName: String = "Badreddine — Super Administrateur",
  val roleTitle: String = "Fondateur & Administrateur Général Mekanik+",
  val isAuthenticated: Boolean = true,
  val lastLoginTime: String = "Aujourd'hui à 09:14",
  val securityClearance: String = "NIVEAU ROOT • CONTRÔLE INSCRIPTIONS, CLÉS & EMPLOYÉS",
  val activeStaffOperator: AdminStaffMember? = null
) {
  val currentOperatorEmail: String
    get() = activeStaffOperator?.email ?: adminEmail

  val currentOperatorName: String
    get() = activeStaffOperator?.fullName ?: adminFullName

  val currentOperatorBadge: String
    get() = activeStaffOperator?.let { "EMPLOYÉ • ${it.role.badgeLabel}" } ?: "SUPER-ADMIN ROOT"
}
