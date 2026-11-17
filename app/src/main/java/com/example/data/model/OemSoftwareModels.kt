package com.example.data.model

enum class OemManufacturerGroup(
  val label: String,
  val shortCode: String,
  val coveredBrands: List<String>,
  val description: String,
  val iconEmoji: String,
  val themeColorHex: Long
) {
  VAG_GROUP(
    label = "Groupe Volkswagen (VAG)",
    shortCode = "VAG",
    coveredBrands = listOf("Volkswagen", "Audi", "Seat", "Skoda", "Cupra", "Porsche", "VW Véhicules Utilitaires"),
    description = "Catalogues électroniques ETKA, manuels de réparation ELSAWin, schémas de câblage et diagnostic ODIS.",
    iconEmoji = "🇩🇪",
    themeColorHex = 0xFF00E5FF
  ),
  STELLANTIS_GROUP(
    label = "Groupe Stellantis (PSA & FCA)",
    shortCode = "STELLANTIS",
    coveredBrands = listOf("Peugeot", "Citroën", "Fiat", "Opel", "DS Automobiles", "Alfa Romeo", "Jeep", "Abarth"),
    description = "ServiceBox EPC, schémas DocBackup, barèmes de temps, diagnostic DiagBox & portail TechHub.",
    iconEmoji = "🇫🇷",
    themeColorHex = 0xFFFF7A00
  ),
  RENAULT_GROUP(
    label = "Groupe Renault & Dacia",
    shortCode = "RENAULT",
    coveredBrands = listOf("Renault", "Dacia", "Alpine", "Renault Trucks (Master/Trafic)"),
    description = "Logiciel officiel Dialogys (éclatés pièces & références OEM), Clip Diagnostic et manuels Repman.",
    iconEmoji = "🟡",
    themeColorHex = 0xFFFFD600
  ),
  GERMAN_PREMIUM(
    label = "BMW Group & Mercedes-Benz",
    shortCode = "PREMIUM_DE",
    coveredBrands = listOf("BMW", "Mini", "Mercedes-Benz", "Smart"),
    description = "BMW ETK / ISTA-D / TIS, Mercedes-Benz EPC / WIS / ASRA (Atelier, manuels et éclatés pièces).",
    iconEmoji = "⭐",
    themeColorHex = 0xFF40C4FF
  ),
  ASIAN_BRANDS(
    label = "Constructeurs Asiatiques (Toyota, Hyundai, Kia, Nissan)",
    shortCode = "ASIAN",
    coveredBrands = listOf("Toyota", "Hyundai", "Kia", "Nissan", "Suzuki", "Chery", "Geely"),
    description = "Portail Microcat EPC, Hyundai/Kia GDS, Toyota TechDoc & Nissan Fast.",
    iconEmoji = "🇯🇵",
    themeColorHex = 0xFF00E676
  ),
  MULTI_BRAND_UNIVERSAL(
    label = "Suites Universelles Multimarques (TecDoc, HaynesPro, Autodata)",
    shortCode = "UNIVERSAL",
    coveredBrands = listOf("Toutes marques mondiales (Européennes, Asiatiques, Américaines)"),
    description = "Base globale TecDoc, HaynesPro WorkshopData, Autodata Connect et barèmes universels.",
    iconEmoji = "🌐",
    themeColorHex = 0xFFE53935
  )
}

enum class SoftwareToolType(val label: String, val badgeColorHex: Long) {
  CATALOGUE_EPC("Catalogue Pièces (EPC)", 0xFF00E5FF),
  MANUEL_REPARATION("Manuel & Procédures", 0xFFFF7A00),
  SCHEMA_ELECTRIQUE("Schémas Électriques", 0xFFFFD600),
  DIAGNOSTIC_ODIS("Diagnostic & Calculateurs", 0xFFE53935),
  BAREME_TEMPS("Barèmes de Temps & Devis", 0xFF00E676)
}

data class OemSoftwareTool(
  val id: String,
  val name: String,
  val group: OemManufacturerGroup,
  val toolType: SoftwareToolType,
  val officialVersion: String,
  val description: String,
  val targetAudience: String, // "Concessionnaires & Ateliers Spécialisés"
  val keyFeatures: List<String>,
  val accessStatus: String = "Accès Immédiat Sécurisé",
  val directLaunchUrl: String = "https://epc.mekanikplus.dz/launch",
  val isIncludedInStandardPlan: Boolean = false,
  val requiresFullSupplementPack: Boolean = true
)

data class FullSoftwarePackageSupplement(
  val packageId: String = "pack_full_oem_software_10k",
  val title: String = "Pack Intégral Tous Logiciels & Manuels Constructeurs",
  val priceDzd: Int = 10000, // 10 000 DA
  val billingPeriod: String = "Supplément Annuel / Entreprise",
  val description: String = "Débloquez l'accès illimité à TOUS les logiciels officiels de recherche de pièces (ETKA, ServiceBox, Dialogys, Microcat, TecDoc Pro) et l'intégralité des manuels de réparation constructeurs.",
  val advantages: List<String> = listOf(
    "Accès 360° à 100% des catalogues électroniques de pièces (EPC)",
    "Manuels de démontage, couples de serrage et schémas officiels VAG, Stellantis, Renault, BMW, Asian",
    "Module de recherche de pièces par VIN instantané dans tous les catalogues",
    "Passage libre d'un mode atelier à l'autre (Mode Volkswagen, Stellantis, Renault, etc.)",
    "Mises à jour mensuelles des références constructeurs et prix tarifaires",
    "Assistance technique prioritaire & intégration directe dans les devis Mekanik+"
  )
)

data class WorkshopSoftwareConfiguration(
  val activeModeGroup: OemManufacturerGroup = OemManufacturerGroup.MULTI_BRAND_UNIVERSAL,
  val hasFullSoftwarePackageUnlocked: Boolean = true, // Unlocked by default in Ultra Pro or via 10,000 DA supplement
  val allowedGroups: List<OemManufacturerGroup> = OemManufacturerGroup.entries,
  val selectedBrandsFilter: List<String> = emptyList(),
  val lastSearchVin: String = "WVWZZZ5NZMW123456"
)
