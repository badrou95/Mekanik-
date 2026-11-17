package com.example.data.model

enum class AccountRole(
  val label: String,
  val shortTitle: String,
  val formulaName: String,
  val subscriptionPriceLabel: String,
  val subscriptionPriceDzd: Int,
  val billingPeriodLabel: String,
  val mecaVerifExternalRateDzd: Int,
  val defaultSubAccountsCount: Int,
  val maxSubAccountsAllowed: Int,
  val description: String,
  val badgeLabel: String,
  val iconEmoji: String = "🚗",
  val allowedFeaturesSummary: List<String>,
  val excludedFeaturesSummary: String
) {
  CONDUCTEUR(
    label = "Compte Particulier",
    shortTitle = "Particulier",
    formulaName = "Formule Particulier & Acheteur Auto (3 500 DA/an)",
    subscriptionPriceLabel = "3 500 DA / an",
    subscriptionPriceDzd = 3500,
    billingPeriodLabel = "par an (ou 350 DA/mois)",
    mecaVerifExternalRateDzd = 300,
    defaultSubAccountsCount = 0,
    maxSubAccountsAllowed = 0,
    description = "Mon véhicule par VIN, recherche de pièces compatibles, trouver un atelier/magasin par ville & suivi réparation. Historique MécaVerif+™ externe à 300 DA / VIN.",
    badgeLabel = "COMPTE PARTICULIER",
    iconEmoji = "👤",
    allowedFeaturesSummary = listOf(
      "Identification de Mon Véhicule par Numéro de Châssis (VIN)",
      "MécaVerif+™ (Externe hors abonnement : 300 DA par VIN consulté)",
      "Trouver une Pièce Compatible & Rechercher un Atelier / Magasin par Ville",
      "Carnet d'Entretien Personnel, Suivi de Réparation en Direct & SOS Dépannage"
    ),
    excludedFeaturesSummary = "Toutes les options Atelier Pro, Vendeur de Pièces, Carrosserie et Entreprise sont supprimées."
  ),
  GARAGE_PRO(
    label = "Compte Atelier & Mécanicien Pro",
    shortTitle = "Atelier Pro",
    formulaName = "Formule DMS Mécanicauto+ Atelier Mécanique (18 000 DA/an)",
    subscriptionPriceLabel = "18 000 DA / an",
    subscriptionPriceDzd = 18000,
    billingPeriodLabel = "par an (ou 1 800 DA/mois)",
    mecaVerifExternalRateDzd = 250,
    defaultSubAccountsCount = 2,
    maxSubAccountsAllowed = 3,
    description = "Ordres de réparation, devis, DMS T1/T2/T3, cache pannes clients, schémas techniques, diagnostic OBD-II, vocale mains-libres & scanner pièce.",
    badgeLabel = "COMPTE ATELIER",
    iconEmoji = "🛠️",
    allowedFeaturesSummary = listOf(
      "Ordres de Réparation (OR), Devis & Planning RDV Atelier",
      "Suite DMS Mécanicauto+ (Barèmes T1/T2/T3, Compagnons, VOR)",
      "Cache Local Atelier (Schémas 0 ms, Pannes Clients & Diagnostics)",
      "Scanner Étiquette Pièce (Compatibilité Châssis & Méthode de Montage)",
      "Diagnostic OBD-II MekanikAI & Recherche Vocale Mains-Libres",
      "MécaVerif+™ (Externe hors abonnement : 250 DA par VIN)"
    ),
    excludedFeaturesSummary = "Options Vendeur (Ajout pièce Store IA), Tôlerie Carrosserie et Contrats Location masquées."
  ),
  VENDEUR_PIECES(
    label = "Compte Magasin de Pièces de Rechange",
    shortTitle = "Magasin Pièces",
    formulaName = "Formule DMS Mécanicauto+ Magasin Pièces (20 000 DA/an)",
    subscriptionPriceLabel = "20 000 DA / an",
    subscriptionPriceDzd = 20000,
    billingPeriodLabel = "par an (ou 1 700 DA/mois)",
    mecaVerifExternalRateDzd = 250,
    defaultSubAccountsCount = 2,
    maxSubAccountsAllowed = 3,
    description = "Catalogue pièces OEM & équivalences, ajout de pièces par Photo + IA, scanner code-barres/QR, gestion de stock magasin, réseau B2B & archives ventes.",
    badgeLabel = "COMPTE MAGASIN",
    iconEmoji = "🏪",
    allowedFeaturesSummary = listOf(
      "Catalogue Pièces OEM, Équivalences & Recherche Comptoir par VIN",
      "Ajouter une Pièce au Store par Photo + IA (Réf OEM & Vues Studio)",
      "Scanner Étiquette Pièce (Code-Barres EAN-13 / QR & Compatibilité VIN)",
      "Gestion du Store, Stocks Magasin & Réseau Social Pro B2B",
      "Archivage Mensuel des Ventes & Factures (.ZIP)",
      "MécaVerif+™ (Externe hors abonnement : 250 DA par VIN)"
    ),
    excludedFeaturesSummary = "Options Atelier (OR, Ponts, Pannes Clients, DMS T1/T2/T3), Carrosserie et Flotte supprimées."
  ),
  ATELIER_MAGASIN_MIXTE(
    label = "Compte Mixte : Atelier + Magasin de Pièces",
    shortTitle = "Mixte Atelier+Magasin",
    formulaName = "Formule Mixte Atelier Montage & Magasin Pièces (23 000 DA/an)",
    subscriptionPriceLabel = "23 000 DA / an",
    subscriptionPriceDzd = 23000,
    billingPeriodLabel = "par an (ou 2 200 DA/mois • 1 Principal + 4 Sous-Comptes)",
    mecaVerifExternalRateDzd = 250,
    defaultSubAccountsCount = 4,
    maxSubAccountsAllowed = 4,
    description = "Idéal pour les magasins de pièces disposant d'un atelier mécanique de montage : réunit 100% des options Atelier + 100% des options Magasin avec 1 Compte Principal + 3 à 4 Sous-Comptes.",
    badgeLabel = "MIXTE ATELIER + MAGASIN",
    iconEmoji = "⚙️",
    allowedFeaturesSummary = listOf(
      "1 Compte Principal Gérant + 3 ou 4 Sous-Comptes inclus (Chef Atelier, Mécanicien Monteur, Vendeur Comptoir, Magasinier)",
      "100% des Options ATELIER : Ordres de Réparation (OR), Devis Montage, DMS T1/T2/T3, Cache Pannes, OBD-II & Vocale",
      "100% des Options MAGASIN : Catalogue OEM, Ajout Pièce Store Photo IA, Scanner Étiquette QR/EAN & Gestion Stock",
      "Imputation directe d'une pièce vendue au comptoir vers le pont de montage mécanique",
      "MécaVerif+™ (Service externe hors abonnement : 250 DA par numéro de châssis)"
    ),
    excludedFeaturesSummary = "Cumule intégralement Atelier Mécanique + Magasin de Pièces (seules les options Location Flotte sont masquées)."
  ),
  ATELIER_CARROSSERIE(
    label = "Compte Spécial Carrosserie & Tôlerie",
    shortTitle = "Carrosserie & Tôlerie",
    formulaName = "Formule Spéciale Atelier Carrosserie, Tôlerie & Peinture (19 500 DA/an)",
    subscriptionPriceLabel = "19 500 DA / an",
    subscriptionPriceDzd = 19500,
    billingPeriodLabel = "par an (ou 1 800 DA/mois)",
    mecaVerifExternalRateDzd = 250,
    defaultSubAccountsCount = 2,
    maxSubAccountsAllowed = 3,
    description = "Dédié aux tôliers, carrossiers et peintres : accès par N° de Châssis (VIN) aux pièces de tôlerie, plastique, optiques et agrafes + obligation de photos AVANT et APRÈS réparation.",
    badgeLabel = "COMPTE CARROSSERIE",
    iconEmoji = "🚘",
    allowedFeaturesSummary = listOf(
      "Accès par N° de Châssis (VIN) au Catalogue Tôlerie, Plastique, Pare-Chocs, Ailes, Capots, Traverses, Optiques & Agrafes",
      "Obligation Certifiée de Photos AVANT Réparation (tôle endommagée) et APRÈS Réparation (tôlerie redressée/peinte)",
      "Synchronisation automatique du dossier photos Avant/Après avec l'historique national MécaVerif+™",
      "Devis Carrosserie, Chiffrage Peinture Étuve, Micromètre (µm) & Géométrie Marbre Laser",
      "MécaVerif+™ (Service externe hors abonnement : 250 DA par numéro de châssis)"
    ),
    excludedFeaturesSummary = "Options Vendeur Comptoir généraliste et Location Flotte masquées."
  ),
  LOCATION_VOITURES(
    label = "Compte Entreprise & Flotte Auto",
    shortTitle = "Entreprise",
    formulaName = "Formule DMS Mécanicauto+ Entreprise & Parc (38 000 DA/an)",
    subscriptionPriceLabel = "38 000 DA / an",
    subscriptionPriceDzd = 38000,
    billingPeriodLabel = "par an (ou 3 500 DA/mois • Jusqu'à 10 Sous-Comptes)",
    mecaVerifExternalRateDzd = 250,
    defaultSubAccountsCount = 6,
    maxSubAccountsAllowed = 10,
    description = "Gestion de parc entreprise & flotte location, contrats départ/retour, suivi GPS, coût de revient TCO, sous-comptes équipes & audit externe MécaVerif+™.",
    badgeLabel = "COMPTE ENTREPRISE",
    iconEmoji = "🏢",
    allowedFeaturesSummary = listOf(
      "Gestion de Parc Automobile Entreprise, Contrats & Cautions",
      "Suivi GPS Temps Réel de la Flotte & Calculateur TCO Kilométrique",
      "Gestion Hiérarchique Multi-Comptes Entreprise (Ultra Pro)",
      "Archivage Mensuel Comptable (.ZIP)",
      "MécaVerif+™ (Service externe hors abonnement : 250 DA par VIN)"
    ),
    excludedFeaturesSummary = "Options Comptoir Magasin de Pièces et Outils Mécanicien Mains-Libres supprimées."
  );

  // Le compte mixte (ATELIER_MAGASIN_MIXTE) active à la fois isWorkshopAccount et isPartsStoreAccount !
  val isWorkshopAccount: Boolean
    get() = this == GARAGE_PRO || this == ATELIER_MAGASIN_MIXTE

  val isPartsStoreAccount: Boolean
    get() = this == VENDEUR_PIECES || this == ATELIER_MAGASIN_MIXTE

  val isHybridWorkshopStoreAccount: Boolean
    get() = this == ATELIER_MAGASIN_MIXTE

  val isBodyshopAccount: Boolean
    get() = this == ATELIER_CARROSSERIE

  val isIndividualAccount: Boolean
    get() = this == CONDUCTEUR

  val isEnterpriseAccount: Boolean
    get() = this == LOCATION_VOITURES

  companion object {
    fun fromId(raw: String?): AccountRole {
      return entries.firstOrNull { it.name.equals(raw?.trim(), ignoreCase = true) } ?: GARAGE_PRO
    }
  }
}

data class HybridSubAccountMember(
  val slotNumber: Int,
  val fullName: String,
  val roleTitle: String,
  val departmentBadge: String, // "ATELIER MONTAGE" ou "MAGASIN PIÈCES"
  val pinCode: String,
  val isActive: Boolean = true,
  val canSearchVinAndOem: Boolean = true,
  val canManageWorkshopOrQuotes: Boolean = true,
  val canManageStoreStockAndSales: Boolean = true,
  val canUploadBodyshopPhotos: Boolean = true,
  val canUseMekanikAiObd: Boolean = true,
  val canViewFinancialKpis: Boolean = false
)

val defaultHybridWorkshopStoreSubAccounts = listOf(
  HybridSubAccountMember(
    slotNumber = 1,
    fullName = "Karim Benali",
    roleTitle = "Chef d'Atelier Montage & Diagnostic",
    departmentBadge = "ATELIER MONTAGE",
    pinCode = "1122",
    canSearchVinAndOem = true,
    canManageWorkshopOrQuotes = true,
    canManageStoreStockAndSales = false,
    canUploadBodyshopPhotos = true,
    canUseMekanikAiObd = true,
    canViewFinancialKpis = true
  ),
  HybridSubAccountMember(
    slotNumber = 2,
    fullName = "Yacine Mansouri",
    roleTitle = "Mécanicien Monteur Pièces & Entretien",
    departmentBadge = "ATELIER MONTAGE",
    pinCode = "2233",
    canSearchVinAndOem = true,
    canManageWorkshopOrQuotes = true,
    canManageStoreStockAndSales = false,
    canUploadBodyshopPhotos = true,
    canUseMekanikAiObd = true,
    canViewFinancialKpis = false
  ),
  HybridSubAccountMember(
    slotNumber = 3,
    fullName = "Sofiane Touati",
    roleTitle = "Vendeur Comptoir Pièces de Rechange",
    departmentBadge = "MAGASIN PIÈCES",
    pinCode = "3344",
    canSearchVinAndOem = true,
    canManageWorkshopOrQuotes = true,
    canManageStoreStockAndSales = true,
    canUploadBodyshopPhotos = false,
    canUseMekanikAiObd = false,
    canViewFinancialKpis = true
  ),
  HybridSubAccountMember(
    slotNumber = 4,
    fullName = "Mehdi Khelifi",
    roleTitle = "Magasinier Stock, Réception & Étiquettes QR",
    departmentBadge = "MAGASIN PIÈCES",
    pinCode = "4455",
    canSearchVinAndOem = true,
    canManageWorkshopOrQuotes = false,
    canManageStoreStockAndSales = true,
    canUploadBodyshopPhotos = false,
    canUseMekanikAiObd = false,
    canViewFinancialKpis = false
  )
)
