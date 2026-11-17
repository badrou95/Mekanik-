package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AccountRole

// ============================================================================
// PALETTE IDENTITÉ VISUELLE SAAS MEKANIK+ (DMS MÉCANICAUTO+)
// ============================================================================
private val SaasBgPrimary = Color(0xFF101820)       // Fond : #101820
private val SaasSurfaceCard = Color(0xFF1C2833)     // Surfaces : #1C2833
private val SaasSurfaceElevated = Color(0xFF253342) // Surface surélevée / Table Header
private val SaasOrange = Color(0xFFF07836)          // Orange : #F07836
private val SaasPerformanceRed = Color(0xFFE53935)  // Rouge mécanique / Réf OEM : #E53935
private val SaasTextPrimary = Color(0xFFE8EDF2)     // Texte principal : #E8EDF2
private val SaasTextSecondary = Color(0xFF94A3B8)   // Texte secondaire : #94A3B8
private val SaasBorder = Color(0xFF2E3D4F)          // Bordure métallique subtile
private val SaasGreen = Color(0xFF22C55E)           // Validation / En stock
private val SaasCyan = Color(0xFF38BDF8)            // Technologie / IA / Sync

// ============================================================================
// LES 6 ESPACES DE LA PLATEFORME WEB SAAS MEKANIK+
// ============================================================================
enum class SaasSpace(
    val code: String,
    val title: String,
    val shortLabel: String,
    val subtitle: String,
    val icon: ImageVector
) {
    SITE_PUBLIC(
        code = "A",
        title = "A. Site Public & Abonnements",
        shortLabel = "Site Public",
        subtitle = "Accueil, recherche VIN, fonctionnalités, tarifs, inscription & contact",
        icon = Icons.Default.Public
    ),
    ESPACE_UTILISATEUR(
        code = "B",
        title = "B. Espace Utilisateur",
        shortLabel = "Utilisateur",
        subtitle = "Tableau de bord personnalisé, profil, historique, MécaVerif+ & abonnement",
        icon = Icons.Default.Person
    ),
    ESPACE_ATELIER(
        code = "C",
        title = "C. Espace Atelier",
        shortLabel = "Atelier",
        subtitle = "Identification VIN, pièces, compatibilité, clients, devis/OR & interventions",
        icon = Icons.Default.Build
    ),
    ESPACE_MAGASIN(
        code = "D",
        title = "D. Espace Magasin",
        shortLabel = "Magasin",
        subtitle = "Catalogue pièces, gestion des stocks, références OEM, compatibilité & commandes",
        icon = Icons.Default.Storefront
    ),
    ESPACE_FOURNISSEUR(
        code = "E",
        title = "E. Espace Fournisseur B2B",
        shortLabel = "Fournisseur",
        subtitle = "Catalogue grossiste, gestion des prix, disponibilité dépôts & demandes B2B",
        icon = Icons.Default.LocalShipping
    ),
    ADMINISTRATION_GENERALE(
        code = "F",
        title = "F. Administration Générale",
        shortLabel = "Admin SaaS",
        subtitle = "Utilisateurs, ateliers, magasins, fournisseurs, abonnements, stats & permissions 2FA",
        icon = Icons.Default.AdminPanelSettings
    )
}

// ============================================================================
// MODÈLES DE DONNÉES SAAS MULTI-ORGANISATIONS (ATELIER, MAGASIN, FOURNISSEUR, ADMIN)
// ============================================================================

data class SaasOrganizationTenant(
    val organizationId: String,
    val name: String,
    val orgType: String, // "ATELIER", "MAGASIN", "MIXTE_ATELIER_MAGASIN", "CARROSSERIE", "FOURNISSEUR", "PARTICULIER"
    val wilayaOrCity: String,
    val managerName: String,
    val email: String,
    val phone: String,
    val subscriptionPlan: String,
    val monthlyFeeDzd: Int,
    val status: String = "ACTIF", // "ACTIF", "ESSAI_DEMO_3J", "SUSPENDU"
    val subAccountsCount: Int = 1,
    val is2FaEnabled: Boolean = true
)

data class SaasSupplierCatalogProduct(
    val id: String,
    val partName: String,
    val systemCategory: String,
    val oemPrimaryRef: String,
    val equivalentRefs: List<String>,
    val brand: String,
    val compatibleVehicles: List<String>,
    val compatibleVinPrefixes: List<String>,
    val publicPriceDzd: Int,
    val b2bProPriceDzd: Int,
    val discountPercent: Int,
    val stockQuantity: Int,
    val warehouseLocation: String,
    val availabilityStatus: String, // "EN STOCK IMMÉDIAT", "STOCK LIMITÉ", "SUR COMMANDE 24H"
    val supplierOrgId: String = "ORG-FOURN-01",
    val supplierName: String = " Maghreb Auto Parts Distribution (Grossiste Officiel)"
)

data class SaasB2bSupplyRequestOrder(
    val id: String,
    val createdAt: String,
    val requesterOrgId: String,
    val requesterName: String,
    val requesterType: String, // "ATELIER" ou "MAGASIN"
    val targetVehicleVin: String,
    val partOemRef: String,
    val partName: String,
    val quantity: Int,
    val unitPriceB2bDzd: Int,
    val status: String // "NOUVELLE DEMANDE", "EN PRÉPARATION", "EXPÉDIÉE", "LIVRÉE"
)

data class SaasWorkshopClientRecord(
    val id: String,
    val organizationId: String,
    val fullName: String,
    val phone: String,
    val vehicleLabel: String,
    val vin: String,
    val engineCode: String,
    val mileageKm: Int,
    val lastVisitDate: String,
    val totalSpentDzd: Int
)

data class SaasWorkshopInterventionQuote(
    val id: String,
    val organizationId: String,
    val clientName: String,
    val vehicleLabel: String,
    val vin: String,
    val description: String,
    val oemPartsIncluded: List<String>,
    val totalAmountDzd: Int,
    val status: String, // "DEVIS EN ATTENTE", "VALIDÉ • EN COURS", "TERMINÉ & FACTURÉ"
    val date: String,
    val hasMandatoryBeforeAfterPhotos: Boolean = true
)

// ============================================================================
// REGISTRE CENTRALISÉ TEMPS RÉEL DE LA PLATEFORME SAAS MEKANIK+
// ============================================================================
object MekanikSaasPlatformRegistry {
    var activeOrganizationId by mutableStateOf("ORG-MIXTE-ALGER-01")
    var activeSaasSpace by mutableStateOf(SaasSpace.SITE_PUBLIC)
    var forceDesktopSidebarLayout by mutableStateOf(true)

    val organizations = mutableStateListOf(
        SaasOrganizationTenant(
            organizationId = "ORG-MIXTE-ALGER-01",
            name = "Garage & Pièces El-Djazair Auto+ (Compte Mixte)",
            orgType = "MIXTE_ATELIER_MAGASIN",
            wilayaOrCity = "Alger (Bab Ezzouar)",
            managerName = "Karim Benali",
            email = "contact@eldjazair-auto.dz",
            phone = "+213 550 12 34 56",
            subscriptionPlan = "Formule Mixte Atelier + Magasin (1 Principal + 4 Sous-Comptes)",
            monthlyFeeDzd = 14900,
            status = "ACTIF",
            subAccountsCount = 4,
            is2FaEnabled = true
        ),
        SaasOrganizationTenant(
            organizationId = "ORG-ATELIER-ORAN-02",
            name = "Atelier Mécanique & Diagnostic Oran Pro",
            orgType = "ATELIER",
            wilayaOrCity = "Oran (Es Senia)",
            managerName = "Amine Khelil",
            email = "atelier.oran@mecanicauto.dz",
            phone = "+213 551 98 76 54",
            subscriptionPlan = "Formule Atelier Mécanique Pro",
            monthlyFeeDzd = 9900,
            status = "ACTIF",
            subAccountsCount = 3,
            is2FaEnabled = true
        ),
        SaasOrganizationTenant(
            organizationId = "ORG-CARROSSERIE-BLIDA-03",
            name = "Carrosserie & Tôlerie Laser Blida (Photos Avant/Après)",
            orgType = "CARROSSERIE",
            wilayaOrCity = "Blida (Boufarik)",
            managerName = "Djamel Belkacem",
            email = "tolerie.blida@mecanicauto.dz",
            phone = "+213 552 44 55 66",
            subscriptionPlan = "Formule Spéciale Carrosserie & Tôlerie VIN",
            monthlyFeeDzd = 8900,
            status = "ACTIF",
            subAccountsCount = 3,
            is2FaEnabled = true
        ),
        SaasOrganizationTenant(
            organizationId = "ORG-MAGASIN-SETIF-04",
            name = "Comptoir Pièces OEM Hauts-Plateaux",
            orgType = "MAGASIN",
            wilayaOrCity = "Sétif (El Eulma)",
            managerName = "Mourad Ziani",
            email = "pieces.setif@mecanicauto.dz",
            phone = "+213 553 77 88 99",
            subscriptionPlan = "Formule Magasin Pièces de Rechange",
            monthlyFeeDzd = 9500,
            status = "ACTIF",
            subAccountsCount = 3,
            is2FaEnabled = true
        ),
        SaasOrganizationTenant(
            organizationId = "ORG-FOURN-01",
            name = "Maghreb Auto Parts Distribution (Importateur & Grossiste)",
            orgType = "FOURNISSEUR",
            wilayaOrCity = "Alger / Oran / Constantine",
            managerName = "Yacine Brahimi",
            email = "b2b@maghreb-autoparts.dz",
            phone = "+213 560 00 11 22",
            subscriptionPlan = "Formule Fournisseur & Distributeur B2B",
            monthlyFeeDzd = 24900,
            status = "ACTIF",
            subAccountsCount = 8,
            is2FaEnabled = true
        ),
        SaasOrganizationTenant(
            organizationId = "ORG-PARTICULIER-05",
            name = "Riad Mansouri (Compte Particulier & MécaVerif+)",
            orgType = "PARTICULIER",
            wilayaOrCity = "Constantine",
            managerName = "Riad Mansouri",
            email = "riad.mansouri@gmail.com",
            phone = "+213 661 22 33 44",
            subscriptionPlan = "Compte Particulier Gratuit + MécaVerif+ à l'acte (250/300 DA)",
            monthlyFeeDzd = 0,
            status = "ACTIF",
            subAccountsCount = 0,
            is2FaEnabled = true
        )
    )

    val supplierCatalog = mutableStateListOf(
        SaasSupplierCatalogProduct(
            id = "SUP-PART-01",
            partName = "Kit de Distribution Complet + Pompe à Eau Haut Débit",
            systemCategory = "Distribution & Moteur",
            oemPrimaryRef = "04L 198 119 A",
            equivalentRefs = listOf("CT1168WP1 (ContiTech)", "KP15678XS (Gates)", "VKMC 01278 (SKF)", "530 0650 30 (INA)"),
            brand = "Continental / INA OEM",
            compatibleVehicles = listOf("VW Golf 7 / 8 2.0 TDI", "Audi A3 / A4 2.0 TDI", "Seat Leon 2.0 TDI", "Škoda Octavia 2.0 TDI"),
            compatibleVinPrefixes = listOf("WVWZZZAUZ", "WAUZZZ8V", "VSSZZZ5F"),
            publicPriceDzd = 34500,
            b2bProPriceDzd = 26800,
            discountPercent = 22,
            stockQuantity = 48,
            warehouseLocation = "Dépôt Central Alger (Rouiba)",
            availabilityStatus = "EN STOCK IMMÉDIAT"
        ),
        SaasSupplierCatalogProduct(
            id = "SUP-PART-02",
            partName = "Jeu de 4 Plaquettes de Frein Avant Haute Performance",
            systemCategory = "Freinage & Hydraulique",
            oemPrimaryRef = "5Q0 698 151 BR",
            equivalentRefs = listOf("P 85 126 (Brembo)", "GDB1956 (TRW)", "0 986 494 660 (Bosch)", "FDB4433 (Ferodo)"),
            brand = "Brembo Première Monte",
            compatibleVehicles = listOf("VW Golf 7 / Tiguan", "Peugeot 308 II / 3008 (Équiv. RP)", "Audi A3 8V"),
            compatibleVinPrefixes = listOf("WVWZZZ", "VF3L", "WAUZZZ"),
            publicPriceDzd = 9200,
            b2bProPriceDzd = 6900,
            discountPercent = 25,
            stockQuantity = 115,
            warehouseLocation = "Dépôt Alger & Oran",
            availabilityStatus = "EN STOCK IMMÉDIAT"
        ),
        SaasSupplierCatalogProduct(
            id = "SUP-PART-03",
            partName = "Vanne EGR Électropneumatique + Refroidisseur Gaz (Stellantis)",
            systemCategory = "Dépollution & Injection",
            oemPrimaryRef = "98 075 930 80",
            equivalentRefs = listOf("VALEO 700444", "PIERBURG 7.09720.00.0", "DELPHI EG10512-12B1"),
            brand = "Valeo / Groupe Stellantis ServiceBox",
            compatibleVehicles = listOf("Peugeot 208 / 308 / 3008 1.5 & 1.6 BlueHDi", "Citroën C4 / Berlingo", "Opel Corsa / Grandland"),
            compatibleVinPrefixes = listOf("VF3", "VR3", "VF7"),
            publicPriceDzd = 42000,
            b2bProPriceDzd = 33500,
            discountPercent = 20,
            stockQuantity = 19,
            warehouseLocation = "Dépôt Alger (Bab Ezzouar)",
            availabilityStatus = "EN STOCK IMMÉDIAT"
        ),
        SaasSupplierCatalogProduct(
            id = "SUP-PART-04",
            partName = "Optique Phare Avant Full LED Matrix + Bouclier Tôlerie Avant",
            systemCategory = "Carrosserie, Tôlerie & Éclairage",
            oemPrimaryRef = "5G1 941 081 F",
            equivalentRefs = listOf("HELLA 1EX 013 924-111", "VALEO 046891", "MAGNETI MARELLI 711451000120"),
            brand = "Hella / Valeo OEM Carrosserie",
            compatibleVehicles = listOf("VW Golf 7.5 / 8", "Seat Leon FR", "Renault Clio 5 (Réf 260604129R)"),
            compatibleVinPrefixes = listOf("WVWZZZAUZ", "VF1RJA"),
            publicPriceDzd = 78000,
            b2bProPriceDzd = 61000,
            discountPercent = 22,
            stockQuantity = 7,
            warehouseLocation = "Dépôt Carrosserie Blida / Alger",
            availabilityStatus = "STOCK LIMITÉ"
        ),
        SaasSupplierCatalogProduct(
            id = "SUP-PART-05",
            partName = "Turbocompresseur Géométrie Variable (Actuateur Électronique)",
            systemCategory = "Suralimentation & Moteur",
            oemPrimaryRef = "04L 253 010 B",
            equivalentRefs = listOf("GARRETT 813860-5003S", "BORGWARNER 53039880205", "MAHLE 030 TC 11001 000"),
            brand = "Garrett Motion OEM",
            compatibleVehicles = listOf("VW Golf 7 2.0 TDI 150ch (CRLB)", "Audi A3 / Q3 2.0 TDI", "Skoda Superb"),
            compatibleVinPrefixes = listOf("WVWZZZAUZHW", "WAUZZZ"),
            publicPriceDzd = 118000,
            b2bProPriceDzd = 92000,
            discountPercent = 22,
            stockQuantity = 11,
            warehouseLocation = "Dépôt Central Alger",
            availabilityStatus = "EN STOCK IMMÉDIAT"
        )
    )

    val b2bSupplyOrders = mutableStateListOf(
        SaasB2bSupplyRequestOrder(
            id = "CMD-B2B-2026-801",
            createdAt = "Aujourd'hui • 09:42",
            requesterOrgId = "ORG-MIXTE-ALGER-01",
            requesterName = "Garage & Pièces El-Djazair Auto+",
            requesterType = "MIXTE ATELIER+MAGASIN",
            targetVehicleVin = "WVWZZZAUZHW129834",
            partOemRef = "04L 198 119 A",
            partName = "Kit de Distribution Complet + Pompe à Eau",
            quantity = 3,
            unitPriceB2bDzd = 26800,
            status = "NOUVELLE DEMANDE"
        ),
        SaasB2bSupplyRequestOrder(
            id = "CMD-B2B-2026-802",
            createdAt = "Aujourd'hui • 10:15",
            requesterOrgId = "ORG-CARROSSERIE-BLIDA-03",
            requesterName = "Carrosserie & Tôlerie Laser Blida",
            requesterType = "ATELIER CARROSSERIE",
            targetVehicleVin = "WVWZZZAUZHW129834",
            partOemRef = "5G1 941 081 F",
            partName = "Optique Phare Avant Full LED Matrix + Bouclier",
            quantity = 1,
            unitPriceB2bDzd = 61000,
            status = "EN PRÉPARATION"
        ),
        SaasB2bSupplyRequestOrder(
            id = "CMD-B2B-2026-803",
            createdAt = "Hier • 16:30",
            requesterOrgId = "ORG-MAGASIN-SETIF-04",
            requesterName = "Comptoir Pièces OEM Hauts-Plateaux",
            requesterType = "MAGASIN PIÈCES",
            targetVehicleVin = "VF3LCBHZHJS102938",
            partOemRef = "98 075 930 80",
            partName = "Vanne EGR Électropneumatique Stellantis",
            quantity = 5,
            unitPriceB2bDzd = 33500,
            status = "EXPÉDIÉE"
        )
    )

    val workshopClients = mutableStateListOf(
        SaasWorkshopClientRecord(
            id = "CLI-01",
            organizationId = "ORG-MIXTE-ALGER-01",
            fullName = "Karim Boudiaf",
            phone = "+213 550 88 11 22",
            vehicleLabel = "Volkswagen Golf 7 2.0 TDI 150ch (2019)",
            vin = "WVWZZZAUZHW129834",
            engineCode = "CRLB / DFGA",
            mileageKm = 142500,
            lastVisitDate = "14/11/2026",
            totalSpentDzd = 64500
        ),
        SaasWorkshopClientRecord(
            id = "CLI-02",
            organizationId = "ORG-MIXTE-ALGER-01",
            fullName = "Nabil Benkhedda",
            phone = "+213 661 45 90 12",
            vehicleLabel = "Peugeot 3008 GT Line 1.6 BlueHDi (2021)",
            vin = "VF3MCBHZHKS449102",
            engineCode = "DV6FC (BHZ)",
            mileageKm = 98400,
            lastVisitDate = "12/11/2026",
            totalSpentDzd = 48000
        ),
        SaasWorkshopClientRecord(
            id = "CLI-03",
            organizationId = "ORG-MIXTE-ALGER-01",
            fullName = "Sofiane Merabet",
            phone = "+213 770 33 21 09",
            vehicleLabel = "Renault Clio 5 RS Line 1.3 TCe (2022)",
            vin = "VF1RJA00568192011",
            engineCode = "H5Ht 470",
            mileageKm = 61200,
            lastVisitDate = "10/11/2026",
            totalSpentDzd = 22500
        )
    )

    val workshopInterventions = mutableStateListOf(
        SaasWorkshopInterventionQuote(
            id = "OR-2026-410",
            organizationId = "ORG-MIXTE-ALGER-01",
            clientName = "Karim Boudiaf",
            vehicleLabel = "VW Golf 7 2.0 TDI (CRLB)",
            vin = "WVWZZZAUZHW129834",
            description = "Remplacement Kit Distribution + Pompe à eau G12evo + Diagnostic P0299",
            oemPartsIncluded = listOf("04L 198 119 A", "04L 121 011 N"),
            totalAmountDzd = 44500,
            status = "VALIDÉ • EN COURS",
            date = "16/11/2026",
            hasMandatoryBeforeAfterPhotos = true
        ),
        SaasWorkshopInterventionQuote(
            id = "OR-2026-411",
            organizationId = "ORG-MIXTE-ALGER-01",
            clientName = "Nabil Benkhedda",
            vehicleLabel = "Peugeot 3008 1.6 BlueHDi",
            vin = "VF3MCBHZHKS449102",
            description = "Remplacement Vanne EGR Stellantis + Régénération FAP UDS + Plaquettes AV",
            oemPartsIncluded = listOf("98 075 930 80", "5Q0 698 151 BR"),
            totalAmountDzd = 51000,
            status = "DEVIS EN ATTENTE",
            date = "16/11/2026",
            hasMandatoryBeforeAfterPhotos = true
        ),
        SaasWorkshopInterventionQuote(
            id = "OR-2026-409",
            organizationId = "ORG-MIXTE-ALGER-01",
            clientName = "Sofiane Merabet",
            vehicleLabel = "Renault Clio 5 1.3 TCe",
            vin = "VF1RJA00568192011",
            description = "Réparation Tôlerie Aile Avant Droite + Peinture Étuve (Photos Avant/Après certifiées)",
            oemPartsIncluded = listOf("5G1 941 081 F"),
            totalAmountDzd = 38000,
            status = "TERMINÉ & FACTURÉ",
            date = "11/11/2026",
            hasMandatoryBeforeAfterPhotos = true
        )
    )

    fun placeB2bOrderFromStoreOrWorkshop(
        product: SaasSupplierCatalogProduct,
        quantity: Int,
        targetVin: String,
        requesterType: String
    ) {
        val currentOrg = organizations.firstOrNull { it.organizationId == activeOrganizationId }
        val orderId = "CMD-B2B-2026-${(810..999).random()}"
        b2bSupplyOrders.add(
            0,
            SaasB2bSupplyRequestOrder(
                id = orderId,
                createdAt = "À l'instant • Sync Cloud Temps Réel",
                requesterOrgId = activeOrganizationId,
                requesterName = currentOrg?.name ?: "Atelier / Magasin Partenaire",
                requesterType = requesterType,
                targetVehicleVin = targetVin.ifBlank { "WVWZZZAUZHW129834" },
                partOemRef = product.oemPrimaryRef,
                partName = product.partName,
                quantity = quantity.coerceAtLeast(1),
                unitPriceB2bDzd = product.b2bProPriceDzd,
                status = "NOUVELLE DEMANDE"
            )
        )
    }

    fun advanceB2bOrderStatus(orderId: String) {
        val idx = b2bSupplyOrders.indexOfFirst { it.id == orderId }
        if (idx >= 0) {
            val current = b2bSupplyOrders[idx]
            val nextStatus = when (current.status) {
                "NOUVELLE DEMANDE" -> "EN PRÉPARATION"
                "EN PRÉPARATION" -> "EXPÉDIÉE"
                else -> "LIVRÉE"
            }
            b2bSupplyOrders[idx] = current.copy(status = nextStatus)
        }
    }

    fun updateSupplierProductPriceAndStock(
        productId: String,
        newB2bPrice: Int,
        newStockQty: Int,
        newAvailability: String
    ) {
        val idx = supplierCatalog.indexOfFirst { it.id == productId }
        if (idx >= 0) {
            val current = supplierCatalog[idx]
            supplierCatalog[idx] = current.copy(
                b2bProPriceDzd = newB2bPrice.coerceAtLeast(500),
                stockQuantity = newStockQty.coerceAtLeast(0),
                availabilityStatus = newAvailability
            )
        }
    }

    fun addSupplierProduct(
        partName: String,
        systemCat: String,
        oemRef: String,
        equivRef: String,
        brand: String,
        compatVehicle: String,
        publicPrice: Int,
        b2bPrice: Int,
        stockQty: Int,
        warehouse: String
    ) {
        val discount = if (publicPrice > 0) (((publicPrice - b2bPrice) * 100) / publicPrice).coerceIn(5, 60) else 20
        supplierCatalog.add(
            0,
            SaasSupplierCatalogProduct(
                id = "SUP-PART-${(10..99).random()}",
                partName = partName.trim(),
                systemCategory = systemCat.trim().ifBlank { "Moteur & Pièces OEM" },
                oemPrimaryRef = oemRef.trim().uppercase(),
                equivalentRefs = equivRef.split(",").map { it.trim() }.filter { it.isNotEmpty() }.ifEmpty { listOf("BOSCH OEM", "VALEO EQ") },
                brand = brand.trim().ifBlank { "Équipementier Certifié" },
                compatibleVehicles = listOf(compatVehicle.trim().ifBlank { "Multimarque VAG / Stellantis / Renault" }),
                compatibleVinPrefixes = listOf("WVW", "VF3", "VF1"),
                publicPriceDzd = publicPrice,
                b2bProPriceDzd = b2bPrice,
                discountPercent = discount,
                stockQuantity = stockQty,
                warehouseLocation = warehouse.trim().ifBlank { "Dépôt Central Alger" },
                availabilityStatus = if (stockQty > 0) "EN STOCK IMMÉDIAT" else "SUR COMMANDE 24H"
            )
        )
    }

    fun registerNewSaasOrganization(
        name: String,
        orgType: String,
        wilaya: String,
        manager: String,
        email: String,
        phone: String,
        planName: String,
        monthlyFee: Int
    ): SaasOrganizationTenant {
        val newOrg = SaasOrganizationTenant(
            organizationId = "ORG-${orgType.take(4)}-${(100..999).random()}",
            name = name.trim(),
            orgType = orgType,
            wilayaOrCity = wilaya.trim().ifBlank { "Alger" },
            managerName = manager.trim().ifBlank { "Responsable" },
            email = email.trim().ifBlank { "contact@mecanicauto.dz" },
            phone = phone.trim().ifBlank { "+213 550 00 00 00" },
            subscriptionPlan = planName,
            monthlyFeeDzd = monthlyFee,
            status = "ACTIF",
            subAccountsCount = if (orgType.contains("MIXTE")) 4 else 2,
            is2FaEnabled = true
        )
        organizations.add(0, newOrg)
        activeOrganizationId = newOrg.organizationId
        return newOrg
    }
}

// ============================================================================
// CARTE D'ACCÈS RAPIDE SUR L'ACCUEIL : PLATEFORME WEB SAAS MEKANIK+ (6 ESPACES)
// ============================================================================

@Composable
fun MekanikSaasPlatformHomeBanner(
    onOpenSaasPlatform: (SaasSpace) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("banner_open_saas_web_platform"),
        shape = RoundedCornerShape(16.dp),
        color = SaasSurfaceCard,
        border = BorderStroke(1.5.dp, SaasOrange)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(SaasBgPrimary, SaasSurfaceCard, Color(0xFF231815))
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SaasOrange.copy(alpha = 0.2f))
                            .border(1.dp, SaasOrange, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DesktopWindows,
                            contentDescription = null,
                            tint = SaasOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "PLATEFORME WEB SAAS MEKANIK+",
                                color = SaasTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SaasGreen.copy(alpha = 0.18f),
                                border = BorderStroke(0.8.dp, SaasGreen)
                            ) {
                                Text(
                                    text = "PC • TABLETTE • MOBILE",
                                    color = SaasGreen,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 8.5.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Accès direct aux 6 espaces SaaS : Site Public, Utilisateur, Atelier, Magasin, Fournisseur B2B & Admin Générale",
                            color = SaasTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Button(
                    onClick = { onOpenSaasPlatform(MekanikSaasPlatformRegistry.activeSaasSpace) },
                    colors = ButtonDefaults.buttonColors(containerColor = SaasOrange),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("btn_launch_saas_platform_modal")
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ouvrir SaaS",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.5.sp
                    )
                }
            }

            // Accès direct à chacun des 6 Espaces SaaS (A à F)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SaasSpace.entries.forEach { space ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SaasBgPrimary,
                        border = BorderStroke(1.dp, SaasBorder),
                        modifier = Modifier
                            .clickable { onOpenSaasPlatform(space) }
                            .testTag("chip_open_saas_space_${space.code}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = space.icon,
                                contentDescription = null,
                                tint = SaasOrange,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "${space.code}. ${space.shortLabel}",
                                color = SaasTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// PLATEFORME SAAS COMPLÈTE (SIDEBAR DESKTOP + ADAPTATIF MOBILE + 6 ESPACES A-F)
// ============================================================================

@Composable
fun MekanikSaasWebPlatformModal(
    currentAccountRole: AccountRole,
    onSwitchAccountRole: (AccountRole) -> Unit,
    onOpenSuperAdminConsole: (Int) -> Unit,
    onOpenMecaVerifScreen: () -> Unit,
    onOpenMekanikAiScreen: (String?) -> Unit,
    onOpenPartVisionScanner: () -> Unit,
    onDismiss: () -> Unit
) {
    var activeSpace by remember { mutableStateOf(MekanikSaasPlatformRegistry.activeSaasSpace) }
    var desktopMode by remember { mutableStateOf(MekanikSaasPlatformRegistry.forceDesktopSidebarLayout) }
    var universalVinQuery by remember { mutableStateOf("WVWZZZAUZHW129834") }
    var vinDecodedBanner by remember {
        mutableStateOf<String?>(
            "VIN WVWZZZAUZHW129834 identifié : Volkswagen Golf 7 2.0 TDI 150ch (Moteur CRLB • Boîte DSG6 • Année 2019 • 48 pièces OEM en stock Fournisseur)"
        )
    }
    var feedbackToast by remember { mutableStateOf<String?>(null) }

    val activeOrg = MekanikSaasPlatformRegistry.organizations.firstOrNull {
        it.organizationId == MekanikSaasPlatformRegistry.activeOrganizationId
    } ?: MekanikSaasPlatformRegistry.organizations.first()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(SaasBgPrimary)
                .testTag("mekanik_saas_web_platform_modal")
        ) {
            val isWideScreen = maxWidth >= 680.dp
            val showSidebar = isWideScreen || desktopMode

            Column(modifier = Modifier.fillMaxSize()) {
                // ============================================================
                // 1. TOP BAR SAAS PROFESSIONNELLE ( RECHERCHE VIN + ORGANISATION + MODE PC/MOBILE )
                // ============================================================
                Surface(
                    color = SaasSurfaceCard,
                    border = BorderStroke(1.dp, SaasBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Brand Logo & SaaS Badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = SaasOrange,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.PrecisionManufacturing,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "MEKANIK",
                                            color = SaasTextPrimary,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "+ SAAS WEB",
                                            color = SaasOrange,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(5.dp),
                                            color = SaasBgPrimary,
                                            border = BorderStroke(1.dp, SaasCyan)
                                        ) {
                                            Text(
                                                text = "ORG: ${activeOrg.organizationId}",
                                                color = SaasCyan,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${activeOrg.name} • Isolation Multi-Tenant & 2FA Actifs",
                                        color = SaasTextSecondary,
                                        fontSize = 10.5.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Commutateur Mode Desktop (Barre latérale) / Mode Mobile + Bouton Fermer
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        desktopMode = !desktopMode
                                        MekanikSaasPlatformRegistry.forceDesktopSidebarLayout = desktopMode
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, if (showSidebar) SaasOrange else SaasBorder),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("btn_toggle_saas_desktop_sidebar")
                                ) {
                                    Icon(
                                        imageVector = if (showSidebar) Icons.Default.DesktopMac else Icons.Default.Smartphone,
                                        contentDescription = null,
                                        tint = SaasOrange,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (showSidebar) "Vue PC (Sidebar)" else "Vue Smartphone",
                                        color = SaasTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    )
                                }

                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SaasBgPrimary)
                                        .testTag("btn_close_saas_platform")
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Fermer", tint = SaasTextPrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        // Notification Toast interne si action exécutée
                        feedbackToast?.let { msg ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SaasGreen.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, SaasGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = msg,
                                        color = SaasTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "Fermer",
                                        color = SaasGreen,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.5.sp,
                                        modifier = Modifier.clickable { feedbackToast = null }
                                    )
                                }
                            }
                        }
                    }
                }

                // ============================================================
                // 2. CORPS DE LA PLATEFORME : SIDEBAR DESKTOP OU NAVIGATION MOBILE
                // ============================================================
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    if (showSidebar) {
                        // BARRE LATÉRALE GAUCHE DE TYPE LOGICIEL SAAS PROFESSIONNEL (PC / TABLETTE)
                        Surface(
                            color = SaasSurfaceCard,
                            border = BorderStroke(1.dp, SaasBorder),
                            modifier = Modifier
                                .width(215.dp)
                                .fillMaxHeight()
                                .testTag("saas_desktop_sidebar")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "NAVIGATION SAAS (6 ESPACES)",
                                        color = SaasTextSecondary,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.5.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    )

                                    SaasSpace.entries.forEach { space ->
                                        val selected = activeSpace == space
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (selected) SaasOrange else Color.Transparent,
                                            border = if (selected) null else BorderStroke(1.dp, SaasBorder.copy(alpha = 0.5f)),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    activeSpace = space
                                                    MekanikSaasPlatformRegistry.activeSaasSpace = space
                                                }
                                                .testTag("sidebar_nav_space_${space.code}")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = space.icon,
                                                    contentDescription = null,
                                                    tint = if (selected) Color.White else SaasOrange,
                                                    modifier = Modifier.size(17.dp)
                                                )
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = space.title,
                                                        color = if (selected) Color.White else SaasTextPrimary,
                                                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                        fontSize = 11.sp,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    HorizontalDivider(color = SaasBorder)
                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "OUTILS INTÉGRÉS MEKANIK+",
                                        color = SaasTextSecondary,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.5.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )

                                    SidebarQuickToolButton(
                                        label = "Scanner Étiquette QR/Code-Barres",
                                        icon = Icons.Default.QrCodeScanner,
                                        accent = SaasCyan,
                                        onClick = onOpenPartVisionScanner
                                    )
                                    SidebarQuickToolButton(
                                        label = "Assistant Diagnostic MekanikAI",
                                        icon = Icons.Default.Psychology,
                                        accent = SaasOrange,
                                        onClick = { onOpenMekanikAiScreen("P0299") }
                                    )
                                    SidebarQuickToolButton(
                                        label = "MécaVerif+™ (250/300 DA / VIN)",
                                        icon = Icons.Default.Verified,
                                        accent = SaasGreen,
                                        onClick = onOpenMecaVerifScreen
                                    )
                                    SidebarQuickToolButton(
                                        label = "Clés API TecDoc / ETKA / Stellantis",
                                        icon = Icons.Default.VpnKey,
                                        accent = SaasPerformanceRed,
                                        onClick = { onOpenSuperAdminConsole(7) }
                                    )
                                }

                                // Pied de barre latérale : Sécurité Multi-Tenant & 2FA
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = SaasBgPrimary,
                                    border = BorderStroke(1.dp, SaasGreen.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "🛡️ CLOUD FIRESTORE + 2FA",
                                            color = SaasGreen,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 9.5.sp
                                        )
                                        Text(
                                            text = "Données isolées par Organisation • React/TS & Android synchronisés",
                                            color = SaasTextSecondary,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ZONE DE CONTENU PRINCIPALE DES 6 ESPACES SAAS
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(SaasBgPrimary)
                    ) {
                        when (activeSpace) {
                            SaasSpace.SITE_PUBLIC -> SaasPublicSiteView(
                                universalVinQuery = universalVinQuery,
                                onVinQueryChange = { universalVinQuery = it },
                                vinDecodedBanner = vinDecodedBanner,
                                onDecodeVin = { mode ->
                                    vinDecodedBanner = "[$mode] Analyse instantanée de '$universalVinQuery' : Volkswagen Golf 7 2.0 TDI 150ch (Code Moteur CRLB • Année 2019 • 48 réf. OEM compatibles en stock)"
                                    feedbackToast = "✅ Recherche $mode exécutée avec succès sur la base centrale MEKANIK+"
                                },
                                onNavigateToSpace = { target ->
                                    activeSpace = target
                                    MekanikSaasPlatformRegistry.activeSaasSpace = target
                                },
                                onRegisterOrganization = { org ->
                                    feedbackToast = "🎉 Organisation '${org.name}' (${org.organizationId}) créée et isolée avec succès !"
                                    activeSpace = when (org.orgType) {
                                        "FOURNISSEUR" -> SaasSpace.ESPACE_FOURNISSEUR
                                        "MAGASIN" -> SaasSpace.ESPACE_MAGASIN
                                        "PARTICULIER" -> SaasSpace.ESPACE_UTILISATEUR
                                        else -> SaasSpace.ESPACE_ATELIER
                                    }
                                }
                            )

                            SaasSpace.ESPACE_UTILISATEUR -> SaasUserSpaceView(
                                activeOrg = activeOrg,
                                onOpenMecaVerif = onOpenMecaVerifScreen,
                                onNavigateToSpace = { activeSpace = it },
                                onShowToast = { feedbackToast = it }
                            )

                            SaasSpace.ESPACE_ATELIER -> SaasWorkshopSpaceView(
                                activeOrg = activeOrg,
                                universalVinQuery = universalVinQuery,
                                onVinQueryChange = { universalVinQuery = it },
                                onOpenMekanikAi = onOpenMekanikAiScreen,
                                onOpenPartVisionScanner = onOpenPartVisionScanner,
                                onShowToast = { feedbackToast = it }
                            )

                            SaasSpace.ESPACE_MAGASIN -> SaasStoreSpaceView(
                                activeOrg = activeOrg,
                                universalVinQuery = universalVinQuery,
                                onOpenPartVisionScanner = onOpenPartVisionScanner,
                                onShowToast = { feedbackToast = it }
                            )

                            SaasSpace.ESPACE_FOURNISSEUR -> SaasSupplierSpaceView(
                                onShowToast = { feedbackToast = it }
                            )

                            SaasSpace.ADMINISTRATION_GENERALE -> SaasGeneralAdminSpaceView(
                                onOpenSuperAdminConsole = onOpenSuperAdminConsole,
                                onSwitchOrganization = { orgId ->
                                    MekanikSaasPlatformRegistry.activeOrganizationId = orgId
                                    feedbackToast = "🔄 Organisation active basculée sur $orgId (Données isolées chargées)"
                                },
                                onShowToast = { feedbackToast = it }
                            )
                        }
                    }
                }

                // ============================================================
                // 3. BARRE DE NAVIGATION INFÉRIEURE SUR SMARTPHONE (SI SIDEBAR MASQUÉE)
                // ============================================================
                if (!showSidebar) {
                    Surface(
                        color = SaasSurfaceCard,
                        border = BorderStroke(1.dp, SaasBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SaasSpace.entries.forEach { space ->
                                val selected = activeSpace == space
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) SaasOrange else SaasBgPrimary,
                                    border = BorderStroke(1.dp, if (selected) SaasOrange else SaasBorder),
                                    modifier = Modifier.clickable {
                                        activeSpace = space
                                        MekanikSaasPlatformRegistry.activeSaasSpace = space
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Icon(
                                            imageVector = space.icon,
                                            contentDescription = null,
                                            tint = if (selected) Color.White else SaasOrange,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = space.shortLabel,
                                            color = if (selected) Color.White else SaasTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SidebarQuickToolButton(
    label: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = SaasBgPrimary,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.45f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(15.dp))
            Text(
                text = label,
                color = SaasTextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ============================================================================
// ESPACE A : SITE PUBLIC (ACCUEIL, FONCTIONNALITÉS, ABONNEMENTS, INSCRIPTION, CONTACT)
// ============================================================================
@Composable
private fun SaasPublicSiteView(
    universalVinQuery: String,
    onVinQueryChange: (String) -> Unit,
    vinDecodedBanner: String?,
    onDecodeVin: (String) -> Unit,
    onNavigateToSpace: (SaasSpace) -> Unit,
    onRegisterOrganization: (SaasOrganizationTenant) -> Unit
) {
    var selectedSearchMode by remember { mutableStateOf("VIN") }
    var orgNameInput by remember { mutableStateOf("") }
    var orgTypeInput by remember { mutableStateOf("MIXTE_ATELIER_MAGASIN") }
    var orgWilayaInput by remember { mutableStateOf("Alger") }
    var orgManagerInput by remember { mutableStateOf("") }
    var orgEmailInput by remember { mutableStateOf("") }
    var orgPhoneInput by remember { mutableStateOf("+213 ") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("saas_public_site_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. HERO ZONE : MEKANIK+ « La plateforme automobile nouvelle génération »
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = SaasSurfaceCard,
                border = BorderStroke(1.5.dp, SaasOrange)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1F2A38), SaasSurfaceCard, SaasBgPrimary)
                            )
                        )
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SaasOrange.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, SaasOrange)
                    ) {
                        Text(
                            text = "PLATEFORME WEB SAAS AUTOMOBILE & DMS MÉCANICAUTO+",
                            color = SaasOrange,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.5.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "MEKANIK",
                            color = SaasTextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 30.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "+",
                            color = SaasPerformanceRed,
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp
                        )
                    }

                    Text(
                        text = "« La plateforme automobile nouvelle génération »",
                        color = SaasTextSecondary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )

                    // Les 3 modes de recherche : [ VIN ] [ CODE MOTEUR ] [ RECHERCHE VÉHICULE ]
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("VIN", "CODE MOTEUR", "RECHERCHE VÉHICULE").forEach { mode ->
                            val isSel = selectedSearchMode == mode
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) SaasOrange else SaasBgPrimary,
                                border = BorderStroke(1.dp, if (isSel) SaasOrange else SaasBorder),
                                modifier = Modifier.clickable { selectedSearchMode = mode }
                            ) {
                                Text(
                                    text = "[ $mode ]",
                                    color = if (isSel) Color.White else SaasTextPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }

                    // Champ central de recherche VIN très visible
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = universalVinQuery,
                            onValueChange = onVinQueryChange,
                            label = { Text("Saisir un numéro de châssis VIN (17 car.), code moteur (ex: CRLB) ou véhicule") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SaasOrange) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("public_site_vin_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SaasOrange,
                                unfocusedBorderColor = SaasBorder,
                                focusedTextColor = SaasTextPrimary,
                                unfocusedTextColor = SaasTextPrimary
                            )
                        )

                        Button(
                            onClick = { onDecodeVin(selectedSearchMode) },
                            colors = ButtonDefaults.buttonColors(containerColor = SaasPerformanceRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("btn_public_site_search_vin")
                        ) {
                            Icon(Icons.Default.ManageSearch, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "IDENTIFIER",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                    }

                    vinDecodedBanner?.let { decoded ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SaasBgPrimary,
                            border = BorderStroke(1.dp, SaasGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = SaasGreen, modifier = Modifier.size(18.dp))
                                Text(
                                    text = decoded,
                                    color = SaasTextPrimary,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Raccourcis sous forme de cartes sous le moteur de recherche
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PublicShortcutCard("🔧 Trouver une pièce", "Catalogue OEM & Équivalences") {
                            onNavigateToSpace(SaasSpace.ESPACE_MAGASIN)
                        }
                        PublicShortcutCard("🚗 Identifier mon véhicule", "Décodage VIN 17 caractères") {
                            onNavigateToSpace(SaasSpace.ESPACE_ATELIER)
                        }
                        PublicShortcutCard("🛠️ Trouver un atelier", "Devis, OR & Carrosserie") {
                            onNavigateToSpace(SaasSpace.ESPACE_ATELIER)
                        }
                        PublicShortcutCard("🏪 Espace Fournisseur B2B", "Grossistes & Prix Pro") {
                            onNavigateToSpace(SaasSpace.ESPACE_FOURNISSEUR)
                        }
                        PublicShortcutCard("🧠 MekanikAI", "Diagnostic OBD-II & Schémas") {
                            onNavigateToSpace(SaasSpace.ESPACE_ATELIER)
                        }
                    }
                }
            }
        }

        // 2. PRÉSENTATION DES ABONNEMENTS & FORMULES DE COMPTES SÉPARÉES
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SaasSurfaceCard,
                border = BorderStroke(1.dp, SaasBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "GRILLE DES ABONNEMENTS SAAS PAR TYPE DE COMPTE",
                        color = SaasOrange,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.5.sp
                    )
                    Text(
                        text = "Chaque compte affiche exclusivement les outils adaptés à son métier. Le service MécaVerif+™ (250 ou 300 DA par numéro de châssis) est externe et facturé à l'acte.",
                        color = SaasTextSecondary,
                        fontSize = 11.5.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SaasPlanCard(
                            title = "Compte Particulier",
                            price = "0 DA / mois",
                            sub = "Carnet d'entretien, alertes & accès MécaVerif+ (250/300 DA par châssis)",
                            targetSpace = SaasSpace.ESPACE_UTILISATEUR,
                            onSelect = onNavigateToSpace
                        )
                        SaasPlanCard(
                            title = "Atelier Mécanique Pro",
                            price = "9 900 DA / mois",
                            sub = "DMS complet, VIN, Devis/OR, MekanikAI OBD, Schémas & 3 sous-comptes",
                            targetSpace = SaasSpace.ESPACE_ATELIER,
                            onSelect = onNavigateToSpace
                        )
                        SaasPlanCard(
                            title = "Magasin Pièces Détachées",
                            price = "9 500 DA / mois",
                            sub = "Catalogue OEM TecDoc/ETKA/Stellantis, Stock, Scanner QR & Comptoir",
                            targetSpace = SaasSpace.ESPACE_MAGASIN,
                            onSelect = onNavigateToSpace
                        )
                        SaasPlanCard(
                            title = "Compte Mixte (Atelier + Magasin)",
                            price = "14 900 DA / mois",
                            sub = "1 Compte Principal + 4 Sous-Comptes • Toutes les options Atelier + Magasin",
                            targetSpace = SaasSpace.ESPACE_ATELIER,
                            isHighlighted = true,
                            onSelect = onNavigateToSpace
                        )
                        SaasPlanCard(
                            title = "Spécial Carrosserie & Tôlerie",
                            price = "8 900 DA / mois",
                            sub = "Pièces tôlerie/plastique par VIN + Photos Avant/Après obligatoires",
                            targetSpace = SaasSpace.ESPACE_ATELIER,
                            onSelect = onNavigateToSpace
                        )
                        SaasPlanCard(
                            title = "Espace Fournisseur / Grossiste B2B",
                            price = "24 900 DA / mois",
                            sub = "Catalogue B2B, gestion des prix/remises, stocks dépôts & commandes garages",
                            targetSpace = SaasSpace.ESPACE_FOURNISSEUR,
                            onSelect = onNavigateToSpace
                        )
                    }
                }
            }
        }

        // 3. INSCRIPTION D'UNE NOUVELLE ORGANISATION & CONTACT SUPPORT
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SaasSurfaceCard,
                border = BorderStroke(1.dp, SaasBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "INSCRIPTION ORGANISATION & CRÉATION D'ESPACE DÉDIÉ (ESSAI DÉMO 3 JOURS INCLUS)",
                        color = SaasTextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = orgNameInput,
                            onValueChange = { orgNameInput = it },
                            label = { Text("Nom de l'Atelier / Magasin / Fournisseur") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_saas_register_org_name")
                        )
                        OutlinedTextField(
                            value = orgWilayaInput,
                            onValueChange = { orgWilayaInput = it },
                            label = { Text("Wilaya / Ville") },
                            singleLine = true,
                            modifier = Modifier.weight(0.7f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = orgManagerInput,
                            onValueChange = { orgManagerInput = it },
                            label = { Text("Nom du Gérant") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = orgEmailInput,
                            onValueChange = { orgEmailInput = it },
                            label = { Text("Email professionnel") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = orgPhoneInput,
                            onValueChange = { orgPhoneInput = it },
                            label = { Text("Téléphone (2FA SMS)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "MIXTE_ATELIER_MAGASIN" to "Compte Mixte (1+4)",
                            "ATELIER" to "Atelier Mécanique",
                            "CARROSSERIE" to "Atelier Carrosserie",
                            "MAGASIN" to "Magasin Pièces",
                            "FOURNISSEUR" to "Fournisseur B2B",
                            "PARTICULIER" to "Particulier"
                        ).forEach { (typeKey, label) ->
                            val selected = orgTypeInput == typeKey
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selected) SaasOrange else SaasBgPrimary,
                                border = BorderStroke(1.dp, if (selected) SaasOrange else SaasBorder),
                                modifier = Modifier.clickable { orgTypeInput = typeKey }
                            ) {
                                Text(
                                    text = label,
                                    color = if (selected) Color.White else SaasTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (orgNameInput.isNotBlank()) {
                                val created = MekanikSaasPlatformRegistry.registerNewSaasOrganization(
                                    name = orgNameInput,
                                    orgType = orgTypeInput,
                                    wilaya = orgWilayaInput,
                                    manager = orgManagerInput,
                                    email = orgEmailInput,
                                    phone = orgPhoneInput,
                                    planName = "Formule SaaS $orgTypeInput (2FA + Isolation Cloud)",
                                    monthlyFee = if (orgTypeInput == "FOURNISSEUR") 24900 else 14900
                                )
                                orgNameInput = ""
                                onRegisterOrganization(created)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SaasOrange),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_submit_saas_org_registration")
                    ) {
                        Icon(Icons.Default.DomainAdd, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CRÉER MON ORGANISATION SAAS & OUVRIR L'ESPACE PROFESSIONNEL",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }

                    HorizontalDivider(color = SaasBorder)

                    // Contact & Informations Légales
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📞 Support SaaS DMS Mécanicauto+ : support@mecanicautoplus.dz • +213 550 94 82 10 (Alger / Oran / Constantine)",
                            color = SaasTextSecondary,
                            fontSize = 10.5.sp
                        )
                        Text(
                            text = "Conformité RGPD & Loi 18-07 Protection des Données",
                            color = SaasGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PublicShortcutCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SaasBgPrimary,
        border = BorderStroke(1.dp, SaasBorder),
        modifier = Modifier
            .width(175.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                color = SaasTextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp
            )
            Text(
                text = subtitle,
                color = SaasTextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun SaasPlanCard(
    title: String,
    price: String,
    sub: String,
    targetSpace: SaasSpace,
    isHighlighted: Boolean = false,
    onSelect: (SaasSpace) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SaasBgPrimary,
        border = BorderStroke(if (isHighlighted) 1.5.dp else 1.dp, if (isHighlighted) SaasOrange else SaasBorder),
        modifier = Modifier.width(220.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                color = if (isHighlighted) SaasOrange else SaasTextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.5.sp
            )
            Text(
                text = price,
                color = SaasTextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
            Text(
                text = sub,
                color = SaasTextSecondary,
                fontSize = 10.5.sp,
                minLines = 3
            )
            Button(
                onClick = { onSelect(targetSpace) },
                colors = ButtonDefaults.buttonColors(containerColor = if (isHighlighted) SaasOrange else SaasSurfaceElevated),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Accéder à l'espace",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

// ============================================================================
// ESPACE B : ESPACE UTILISATEUR (TABLEAU DE BORD, PROFIL, HISTORIQUE, ABONNEMENT)
// ============================================================================
@Composable
private fun SaasUserSpaceView(
    activeOrg: SaasOrganizationTenant,
    onOpenMecaVerif: () -> Unit,
    onNavigateToSpace: (SaasSpace) -> Unit,
    onShowToast: (String) -> Unit
) {
    var showInlineMecaVerifCheckout by remember { mutableStateOf(false) }
    var showInlineReceiptsLedger by remember { mutableStateOf(false) }
    var selectedCheckoutChannel by remember { mutableStateOf(MecaVerifGatewayChannel.SATIM_CIB) }
    var unlockedSaasVin by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("saas_user_space_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SaasSurfaceCard,
                border = BorderStroke(1.dp, SaasOrange)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "B. ESPACE UTILISATEUR & GESTION D'ABONNEMENT",
                        color = SaasOrange,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Titulaire : ${activeOrg.managerName} (${activeOrg.email}) • Organisation : ${activeOrg.name}",
                        color = SaasTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Formule active : ${activeOrg.subscriptionPlan} • Tarif : ${activeOrg.monthlyFeeDzd} DA / mois • Statut 2FA : Actif (Google Authenticator + SMS)",
                        color = SaasGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showInlineMecaVerifCheckout = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SaasOrange),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Acheter Rapport MécaVerif+ (250/300 DA)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { onNavigateToSpace(SaasSpace.SITE_PUBLIC) },
                            border = BorderStroke(1.dp, SaasBorder),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Changer d'abonnement", color = SaasTextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Passerelle de Paiement Sécurisé MécaVerif+™ intégrée dans l'Espace Utilisateur SaaS
        item {
            MecaVerifSecurePaymentSummaryBanner(
                currentVin = "WVWZZZAUZHW129834",
                isCurrentVinUnlocked = unlockedSaasVin,
                defaultPriceDzd = if (activeOrg.orgType == "PARTICULIER") 300 else 250,
                unlockedCount = MecaVerifPaymentLedger.transactions.size,
                onOpenSecureCheckout = { channel ->
                    if (channel != null) {
                        selectedCheckoutChannel = channel
                    }
                    showInlineMecaVerifCheckout = true
                },
                onOpenReceiptsHistory = {
                    showInlineReceiptsLedger = true
                }
            )
        }

        // Véhicules récents, Alertes & Entretiens
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SaasSurfaceCard,
                border = BorderStroke(1.dp, SaasBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "VÉHICULES ENREGISTRÉS, ALERTES & HISTORIQUE D'ENTRETIEN",
                        color = SaasTextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                    MekanikSaasPlatformRegistry.workshopInterventions.forEach { interv ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SaasBgPrimary,
                            border = BorderStroke(1.dp, SaasBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${interv.vehicleLabel} • VIN : ${interv.vin}",
                                        color = SaasTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "${interv.date} — ${interv.description}",
                                        color = SaasTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = "${interv.totalAmountDzd} DA",
                                    color = SaasOrange,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.5.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showInlineMecaVerifCheckout) {
        MecaVerifSecureCheckoutModal(
            vin = "WVWZZZAUZHW129834",
            vehicleTitle = "Volkswagen Golf 7 2.0 TDI 150ch (2019)",
            isProAccount = activeOrg.orgType != "PARTICULIER",
            initialChannel = selectedCheckoutChannel,
            onPaymentCompleted = { receipt ->
                unlockedSaasVin = true
                showInlineMecaVerifCheckout = false
                onShowToast("Reçu ${receipt.receiptId} validé (${receipt.amountDzd} DA) • Ouverture du rapport MécaVerif+...")
                onOpenMecaVerif()
            },
            onDismiss = { showInlineMecaVerifCheckout = false }
        )
    }

    if (showInlineReceiptsLedger) {
        MecaVerifReceiptsLedgerModal(
            onSelectUnlockedVin = {
                showInlineReceiptsLedger = false
                onOpenMecaVerif()
            },
            onDismiss = { showInlineReceiptsLedger = false }
        )
    }
}

// ============================================================================
// ESPACE C : ESPACE ATELIER (VIN, PIÈCES, COMPATIBILITÉ, CLIENTS, DEVIS, HISTORIQUE)
// ============================================================================
@Composable
private fun SaasWorkshopSpaceView(
    activeOrg: SaasOrganizationTenant,
    universalVinQuery: String,
    onVinQueryChange: (String) -> Unit,
    onOpenMekanikAi: (String?) -> Unit,
    onOpenPartVisionScanner: () -> Unit,
    onShowToast: (String) -> Unit
) {
    var newClientName by remember { mutableStateOf("") }
    var newClientPhone by remember { mutableStateOf("+213 ") }
    var newClientVehicle by remember { mutableStateOf("") }
    var newClientVin by remember { mutableStateOf(universalVinQuery) }
    var showAddClientRow by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("saas_workshop_space_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Identification VIN & Actions Rapides Atelier
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SaasSurfaceCard,
                border = BorderStroke(1.5.dp, SaasOrange)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "C. ESPACE ATELIER PROFESSIONNEL (${activeOrg.name})",
                                color = SaasOrange,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.5.sp
                            )
                            Text(
                                text = "Identification VIN, compatibilité automobile, gestion clients, devis/OR & commandes B2B",
                                color = SaasTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = onOpenPartVisionScanner,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, SaasCyan)
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = SaasCyan, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Scanner Pièce", color = SaasTextPrimary, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { onOpenMekanikAi("P0299") },
                                colors = ButtonDefaults.buttonColors(containerColor = SaasOrange),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Psychology, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("MekanikAI OBD", color = Color.White, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = universalVinQuery,
                            onValueChange = onVinQueryChange,
                            label = { Text("Identification VIN Atelier (17 caractères)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                onShowToast("🔍 Châssis $universalVinQuery décodé : VW Golf 7 2.0 TDI (CRLB) • Codes PR 1ZA/G0K • 100% compatible")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaasPerformanceRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Text("Décoder VIN & Pièces", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 2. Tableau Professionnel des Clients de l'Atelier (Isolés par organizationId)
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SaasSurfaceCard,
                border = BorderStroke(1.dp, SaasBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GESTION DES CLIENTS & PARC VÉHICULES (${MekanikSaasPlatformRegistry.workshopClients.size})",
                            color = SaasTextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                        OutlinedButton(
                            onClick = { showAddClientRow = !showAddClientRow },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, SaasOrange)
                        ) {
                            Text(
                                text = if (showAddClientRow) "Fermer" else "+ Nouveau Client Atelier",
                                color = SaasOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (showAddClientRow) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newClientName,
                                onValueChange = { newClientName = it },
                                label = { Text("Nom Client") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = newClientVehicle,
                                onValueChange = { newClientVehicle = it },
                                label = { Text("Véhicule") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = newClientVin,
                                onValueChange = { newClientVin = it },
                                label = { Text("VIN") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    if (newClientName.isNotBlank()) {
                                        MekanikSaasPlatformRegistry.workshopClients.add(
                                            0,
                                            SaasWorkshopClientRecord(
                                                id = "CLI-${(10..99).random()}",
                                                organizationId = activeOrg.organizationId,
                                                fullName = newClientName,
                                                phone = newClientPhone,
                                                vehicleLabel = newClientVehicle.ifBlank { "Véhicule Client" },
                                                vin = newClientVin.ifBlank { universalVinQuery },
                                                engineCode = "OEM-STD",
                                                mileageKm = 85000,
                                                lastVisitDate = "Aujourd'hui",
                                                totalSpentDzd = 0
                                            )
                                        )
                                        newClientName = ""
                                        showAddClientRow = false
                                        onShowToast("✅ Client ajouté dans la base isolée de l'atelier ${activeOrg.organizationId}")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SaasGreen)
                            ) {
                                Text("Enregistrer", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // En-tête de tableau professionnel
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SaasSurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("CLIENT & TÉLÉPHONE", color = SaasTextSecondary, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, modifier = Modifier.weight(1.2f))
                            Text("VÉHICULE & MOTEUR", color = SaasTextSecondary, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, modifier = Modifier.weight(1.4f))
                            Text("CHÂSSIS VIN", color = SaasTextSecondary, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, modifier = Modifier.weight(1.3f))
                            Text("KILOMÉTRAGE & CA", color = SaasTextSecondary, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, modifier = Modifier.weight(1f))
                        }
                    }

                    MekanikSaasPlatformRegistry.workshopClients.forEach { cli ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SaasBgPrimary,
                            border = BorderStroke(1.dp, SaasBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1.2f)) {
                                    Text(cli.fullName, color = SaasTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(cli.phone, color = SaasTextSecondary, fontSize = 10.5.sp)
                                }
                                Column(modifier = Modifier.weight(1.4f)) {
                                    Text(cli.vehicleLabel, color = SaasTextPrimary, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Moteur : ${cli.engineCode}", color = SaasOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    text = cli.vin,
                                    color = SaasCyan,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp,
                                    modifier = Modifier.weight(1.3f)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${cli.mileageKm} km", color = SaasTextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("${cli.totalSpentDzd} DA", color = SaasGreen, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Devis, Commandes Pièces & Historique des Interventions
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SaasSurfaceCard,
                border = BorderStroke(1.dp, SaasBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "DEVIS, ORDRES DE RÉPARATION (OR) & HISTORIQUE DES INTERVENTIONS",
                        color = SaasTextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )

                    MekanikSaasPlatformRegistry.workshopInterventions.forEach { orItem ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SaasBgPrimary,
                            border = BorderStroke(1.dp, SaasBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${orItem.id} • ${orItem.clientName} (${orItem.vehicleLabel})",
                                        color = SaasTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SaasOrange.copy(alpha = 0.18f),
                                        border = BorderStroke(0.8.dp, SaasOrange)
                                    ) {
                                        Text(
                                            text = orItem.status,
                                            color = SaasOrange,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 9.5.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = orItem.description,
                                    color = SaasTextSecondary,
                                    fontSize = 11.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Réf OEM : ${orItem.oemPartsIncluded.joinToString(", ")} • Photos Avant/Après : Certifiées",
                                        color = SaasPerformanceRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "${orItem.totalAmountDzd} DA",
                                            color = SaasGreen,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp
                                        )
                                        OutlinedButton(
                                            onClick = {
                                                val firstProduct = MekanikSaasPlatformRegistry.supplierCatalog.first()
                                                MekanikSaasPlatformRegistry.placeB2bOrderFromStoreOrWorkshop(
                                                    product = firstProduct,
                                                    quantity = 1,
                                                    targetVin = orItem.vin,
                                                    requesterType = "ATELIER (${activeOrg.organizationId})"
                                                )
                                                onShowToast("📦 Commande B2B envoyée au Fournisseur pour l'intervention ${orItem.id} !")
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, SaasOrange),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text("Commander Pièces Fournisseur", color = SaasOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// ESPACE D : ESPACE MAGASIN (CATALOGUE, STOCKS, RÉF. CONSTRUCTEURS, COMPATIBILITÉ, COMMANDES)
// ============================================================================
@Composable
private fun SaasStoreSpaceView(
    activeOrg: SaasOrganizationTenant,
    universalVinQuery: String,
    onOpenPartVisionScanner: () -> Unit,
    onShowToast: (String) -> Unit
) {
    var searchFilter by remember { mutableStateOf("") }
    val catalog = MekanikSaasPlatformRegistry.supplierCatalog.filter {
        searchFilter.isBlank() ||
            it.partName.contains(searchFilter, ignoreCase = true) ||
            it.oemPrimaryRef.contains(searchFilter, ignoreCase = true) ||
            it.equivalentRefs.any { eq -> eq.contains(searchFilter, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("saas_store_space_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Fil d'Ariane Visuel : Véhicule sélectionné ↓ Système ↓ Pièce ↓ Référence constructeur ↓ Équivalences ↓ Fournisseurs disponibles
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SaasSurfaceCard,
                border = BorderStroke(1.5.dp, SaasOrange)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "D. ESPACE MAGASIN DE PIÈCES DÉTACHÉES & GESTION DES STOCKS",
                                color = SaasOrange,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.5.sp
                            )
                            Text(
                                text = "Véhicule sélectionné ($universalVinQuery) ↓ Système ↓ Pièce ↓ Référence Constructeur (Rouge/Orange) ↓ Équivalences ↓ Fournisseurs",
                                color = SaasTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        OutlinedButton(
                            onClick = onOpenPartVisionScanner,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, SaasOrange)
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = SaasOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scanner Étiquette Stock", color = SaasTextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    OutlinedTextField(
                        value = searchFilter,
                        onValueChange = { searchFilter = it },
                        label = { Text("Rechercher par Référence Constructeur OEM (ex: 04L 198 119 A), Équivalence ou Désignation...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SaasOrange) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("store_space_search_input")
                    )
                }
            }
        }

        // 2. Cartes Catalogue avec Référence Principale en Rouge/Orange, Badges de Compatibilité & Gestion Stock/Commande
        items(catalog, key = { it.id }) { item ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SaasSurfaceCard,
                border = BorderStroke(1.dp, SaasBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "${item.systemCategory} • ${item.brand}",
                                color = SaasTextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            )
                            Text(
                                text = item.partName,
                                color = SaasTextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )

                            // Référence constructeur principale mise en évidence en Rouge/Orange
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SaasPerformanceRed.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, SaasPerformanceRed)
                                ) {
                                    Text(
                                        text = "RÉF CONSTRUCTEUR OEM : ${item.oemPrimaryRef}",
                                        color = SaasOrange,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.5.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SaasGreen.copy(alpha = 0.16f),
                                    border = BorderStroke(0.8.dp, SaasGreen)
                                ) {
                                    Text(
                                        text = "✓ COMPATIBLE VIN $universalVinQuery",
                                        color = SaasGreen,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.5.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Prix Comptoir : ${item.publicPriceDzd} DA",
                                color = SaasTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.5.sp
                            )
                            Text(
                                text = "Prix Achat B2B : ${item.b2bProPriceDzd} DA (-${item.discountPercent}%)",
                                color = SaasOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Stock Magasin/Dépôt : ${item.stockQuantity} u.",
                                color = SaasGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Équivalences équipementiers
                    Text(
                        text = "Équivalences croisées : ${item.equivalentRefs.joinToString(" • ")}",
                        color = SaasTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    // Compatibilités sous forme de badges
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item.compatibleVehicles.forEach { compat ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SaasBgPrimary,
                                border = BorderStroke(1.dp, SaasBorder)
                            ) {
                                Text(
                                    text = "🚗 $compat",
                                    color = SaasTextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = SaasBorder)

                    // Actions Magasin : Ajuster stock & Commander chez le Fournisseur B2B
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🏪 Fournisseur : ${item.supplierName} (${item.warehouseLocation})",
                            color = SaasTextSecondary,
                            fontSize = 10.5.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    MekanikSaasPlatformRegistry.updateSupplierProductPriceAndStock(
                                        productId = item.id,
                                        newB2bPrice = item.b2bProPriceDzd,
                                        newStockQty = item.stockQuantity + 5,
                                        newAvailability = "EN STOCK IMMÉDIAT"
                                    )
                                    onShowToast("📦 Stock de '${item.oemPrimaryRef}' mis à jour (+5 unités réceptionnées)")
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, SaasGreen),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("+5 Stock", color = SaasGreen, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                            }
                            Button(
                                onClick = {
                                    MekanikSaasPlatformRegistry.placeB2bOrderFromStoreOrWorkshop(
                                        product = item,
                                        quantity = 5,
                                        targetVin = universalVinQuery,
                                        requesterType = "MAGASIN (${activeOrg.organizationId})"
                                    )
                                    onShowToast("🚚 Demande de réapprovisionnement B2B (5x ${item.oemPrimaryRef}) transmise à l'Espace Fournisseur !")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SaasOrange),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("Commander au Fournisseur B2B", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// ESPACE E : ESPACE FOURNISSEUR (CATALOGUE, GESTION DES PRIX, DISPONIBILITÉ, RÉCEPTION DEMANDES)
// ============================================================================
@Composable
private fun SaasSupplierSpaceView(
    onShowToast: (String) -> Unit
) {
    var showAddProductForm by remember { mutableStateOf(false) }
    var newPartName by remember { mutableStateOf("") }
    var newSystemCat by remember { mutableStateOf("Moteur & Injection") }
    var newOemRef by remember { mutableStateOf("") }
    var newEquivRef by remember { mutableStateOf("BOSCH, VALEO, CONTINENTAL") }
    var newBrand by remember { mutableStateOf("Bosch / OEM") }
    var newCompat by remember { mutableStateOf("VW Golf 7 / Peugeot 308 / Renault Clio 5") }
    var newPublicPrice by remember { mutableStateOf("18500") }
    var newB2bPrice by remember { mutableStateOf("14200") }
    var newStockQty by remember { mutableStateOf("30") }
    var newWarehouse by remember { mutableStateOf("Dépôt Central Alger") }

    val catalog = MekanikSaasPlatformRegistry.supplierCatalog
    val incomingOrders = MekanikSaasPlatformRegistry.b2bSupplyOrders

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("saas_supplier_space_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. En-tête Espace Fournisseur & KPIs B2B
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SaasSurfaceCard,
                border = BorderStroke(1.5.dp, SaasOrange)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "E. ESPACE FOURNISSEUR & DISTRIBUTEUR B2B",
                                color = SaasOrange,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Gestion du catalogue grossiste, tarifs B2B, disponibilité dépôts et réception en direct des demandes Ateliers & Magasins",
                                color = SaasTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Button(
                            onClick = { showAddProductForm = !showAddProductForm },
                            colors = ButtonDefaults.buttonColors(containerColor = SaasOrange),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_supplier_add_catalog_item")
                        ) {
                            Icon(Icons.Default.AddBusiness, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (showAddProductForm) "Fermer" else "+ Ajouter Réf. au Catalogue",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (showAddProductForm) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SaasBgPrimary,
                            border = BorderStroke(1.dp, SaasOrange),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "NOUVELLE RÉFÉRENCE AU CATALOGUE FOURNISSEUR B2B",
                                    color = SaasOrange,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.5.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = newPartName,
                                        onValueChange = { newPartName = it },
                                        label = { Text("Désignation de la pièce") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1.2f).testTag("input_supplier_new_part_name")
                                    )
                                    OutlinedTextField(
                                        value = newOemRef,
                                        onValueChange = { newOemRef = it },
                                        label = { Text("Référence OEM Constructeur") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("input_supplier_new_oem_ref")
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = newBrand,
                                        onValueChange = { newBrand = it },
                                        label = { Text("Marque / Équipementier") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = newPublicPrice,
                                        onValueChange = { newPublicPrice = it.filter { c -> c.isDigit() } },
                                        label = { Text("Prix Public (DA)") },
                                        singleLine = true,
                                        modifier = Modifier.weight(0.8f)
                                    )
                                    OutlinedTextField(
                                        value = newB2bPrice,
                                        onValueChange = { newB2bPrice = it.filter { c -> c.isDigit() } },
                                        label = { Text("Prix Pro B2B (DA)") },
                                        singleLine = true,
                                        modifier = Modifier.weight(0.8f)
                                    )
                                    OutlinedTextField(
                                        value = newStockQty,
                                        onValueChange = { newStockQty = it.filter { c -> c.isDigit() } },
                                        label = { Text("Stock") },
                                        singleLine = true,
                                        modifier = Modifier.weight(0.6f)
                                    )
                                }
                                Button(
                                    onClick = {
                                        if (newPartName.isNotBlank() && newOemRef.isNotBlank()) {
                                            MekanikSaasPlatformRegistry.addSupplierProduct(
                                                partName = newPartName,
                                                systemCat = newSystemCat,
                                                oemRef = newOemRef,
                                                equivRef = newEquivRef,
                                                brand = newBrand,
                                                compatVehicle = newCompat,
                                                publicPrice = newPublicPrice.toIntOrNull() ?: 18500,
                                                b2bPrice = newB2bPrice.toIntOrNull() ?: 14200,
                                                stockQty = newStockQty.toIntOrNull() ?: 25,
                                                warehouse = newWarehouse
                                            )
                                            newPartName = ""
                                            newOemRef = ""
                                            showAddProductForm = false
                                            onShowToast("✅ Référence ajoutée au catalogue Fournisseur et publiée vers les Magasins & Ateliers !")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SaasGreen),
                                    modifier = Modifier.fillMaxWidth().testTag("btn_save_supplier_product")
                                ) {
                                    Text("PUBLIER DANS LE CATALOGUE B2B TEMPS RÉEL", color = Color.White, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. RÉCEPTION DES DEMANDES & COMMANDES B2B (ATELIERS & MAGASINS)
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SaasSurfaceCard,
                border = BorderStroke(1.dp, SaasCyan.copy(alpha = 0.7f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "RÉCEPTION DES DEMANDES & COMMANDES B2B (${incomingOrders.size} EN DIRECT)",
                        color = SaasCyan,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )

                    incomingOrders.forEach { order ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SaasBgPrimary,
                            border = BorderStroke(1.dp, SaasBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "${order.id} • ${order.requesterName}",
                                            color = SaasTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(5.dp),
                                            color = SaasOrange.copy(alpha = 0.18f)
                                        ) {
                                            Text(
                                                text = order.requesterType,
                                                color = SaasOrange,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${order.quantity}x ${order.partName} (Réf OEM : ${order.partOemRef}) • VIN : ${order.targetVehicleVin}",
                                        color = SaasTextSecondary,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "Total B2B : ${order.quantity * order.unitPriceB2bDzd} DA • Statut : ${order.status}",
                                        color = SaasGreen,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        MekanikSaasPlatformRegistry.advanceB2bOrderStatus(order.id)
                                        onShowToast("🚚 Statut de la demande ${order.id} mis à jour et notifié au client B2B !")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SaasOrange),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = when (order.status) {
                                            "NOUVELLE DEMANDE" -> "Valider & Préparer"
                                            "EN PRÉPARATION" -> "Expédier"
                                            else -> "Livrée ✓"
                                        },
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. GESTION DU CATALOGUE FOURNISSEUR, DES PRIX B2B ET DE LA DISPONIBILITÉ
        item {
            Text(
                text = "GESTION DU CATALOGUE FOURNISSEUR, DES PRIX PRO & DE LA DISPONIBILITÉ (${catalog.size} RÉFÉRENCES)",
                color = SaasTextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
        }

        items(catalog, key = { "sup_${it.id}" }) { prod ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SaasSurfaceCard,
                border = BorderStroke(1.dp, SaasBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = prod.partName,
                                color = SaasTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Réf OEM : ${prod.oemPrimaryRef} • ${prod.brand} • ${prod.warehouseLocation}",
                                color = SaasOrange,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SaasGreen.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, SaasGreen)
                        ) {
                            Text(
                                text = "${prod.availabilityStatus} (${prod.stockQuantity} u.)",
                                color = SaasGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Prix Public : ${prod.publicPriceDzd} DA  |  Prix Pro B2B : ${prod.b2bProPriceDzd} DA (-${prod.discountPercent}%)",
                            color = SaasTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = {
                                    MekanikSaasPlatformRegistry.updateSupplierProductPriceAndStock(
                                        productId = prod.id,
                                        newB2bPrice = (prod.b2bProPriceDzd - 500).coerceAtLeast(1000),
                                        newStockQty = prod.stockQuantity,
                                        newAvailability = prod.availabilityStatus
                                    )
                                    onShowToast("💰 Tarif B2B de ${prod.oemPrimaryRef} ajusté (-500 DA Promo Garage)")
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, SaasOrange),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Promo -500 DA", color = SaasOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = {
                                    val nextAvail = if (prod.availabilityStatus == "EN STOCK IMMÉDIAT") "SUR COMMANDE 24H" else "EN STOCK IMMÉDIAT"
                                    MekanikSaasPlatformRegistry.updateSupplierProductPriceAndStock(
                                        productId = prod.id,
                                        newB2bPrice = prod.b2bProPriceDzd,
                                        newStockQty = prod.stockQuantity + 10,
                                        newAvailability = nextAvail
                                    )
                                    onShowToast("📊 Disponibilité de ${prod.oemPrimaryRef} mise à jour : $nextAvail")
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, SaasCyan),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Basculer Dispo / +10 Stock", color = SaasCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// ESPACE F : ADMINISTRATION GÉNÉRALE (UTILISATEURS, ATELIERS, MAGASINS, FOURNISSEURS, ABONNEMENTS, STATS, PERMISSIONS)
// ============================================================================
@Composable
private fun SaasGeneralAdminSpaceView(
    onOpenSuperAdminConsole: (Int) -> Unit,
    onSwitchOrganization: (String) -> Unit,
    onShowToast: (String) -> Unit
) {
    val orgs = MekanikSaasPlatformRegistry.organizations
    val totalMonthlyRecurringDzd = orgs.sumOf { it.monthlyFeeDzd }
    val totalB2bVolumeDzd = MekanikSaasPlatformRegistry.b2bSupplyOrders.sumOf { it.quantity * it.unitPriceB2bDzd }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("saas_general_admin_space_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Statistiques Générales SaaS & Accès Console Super-Admin 2FA + SMS
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SaasSurfaceCard,
                border = BorderStroke(1.5.dp, SaasPerformanceRed)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "F. ADMINISTRATION GÉNÉRALE SAAS & GOUVERNANCE MULTI-ORGANISATIONS",
                        color = SaasTextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdminStatBox("Organisations Actives", "${orgs.size} Établissements", "Ateliers, Magasins, Fournisseurs", SaasOrange, Modifier.weight(1f))
                        AdminStatBox("Revenu Mensuel SaaS", "$totalMonthlyRecurringDzd DA / mois", "+ MécaVerif+ 250/300 DA", SaasGreen, Modifier.weight(1f))
                        AdminStatBox("Flux Commandes B2B", "$totalB2bVolumeDzd DA", "${MekanikSaasPlatformRegistry.b2bSupplyOrders.size} commandes B2B", SaasCyan, Modifier.weight(1f))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onOpenSuperAdminConsole(7) },
                            colors = ButtonDefaults.buttonColors(containerColor = SaasOrange),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Clés API TecDoc / ETKA / Stellantis & Droits (2FA+SMS)", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { onOpenSuperAdminConsole(6) },
                            border = BorderStroke(1.dp, SaasGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = SaasGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Activer / Désactiver Toutes les Options (ON/OFF)", color = SaasTextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 2. Gestion des Organisations (Ateliers, Magasins, Fournisseurs, Particuliers) & Isolation des données
        item {
            Text(
                text = "GESTION DES UTILISATEURS, ATELIERS, MAGASINS, FOURNISSEURS & ABONNEMENTS (${orgs.size})",
                color = SaasTextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
        }

        items(orgs, key = { it.organizationId }) { org ->
            val isCurrent = org.organizationId == MekanikSaasPlatformRegistry.activeOrganizationId
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SaasSurfaceCard,
                border = BorderStroke(if (isCurrent) 1.5.dp else 1.dp, if (isCurrent) SaasOrange else SaasBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = SaasBgPrimary,
                                border = BorderStroke(1.dp, SaasOrange)
                            ) {
                                Text(
                                    text = org.orgType,
                                    color = SaasOrange,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "${org.name} (${org.wilayaOrCity})",
                                color = SaasTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            text = "Gérant : ${org.managerName} • ${org.email} • ${org.phone} • Sous-comptes : ${org.subAccountsCount}",
                            color = SaasTextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Abonnement : ${org.subscriptionPlan} (${org.monthlyFeeDzd} DA/mois) • ID Tenant : ${org.organizationId}",
                            color = SaasGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    OutlinedButton(
                        onClick = { onSwitchOrganization(org.organizationId) },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (isCurrent) SaasGreen else SaasOrange)
                    ) {
                        Text(
                            text = if (isCurrent) "Organisation Active ✓" else "Inspecter Tenant",
                            color = if (isCurrent) SaasGreen else SaasOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStatBox(
    title: String,
    value: String,
    sub: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SaasBgPrimary,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.55f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, color = SaasTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            Text(value, color = accent, fontSize = 14.sp, fontWeight = FontWeight.Black)
            Text(sub, color = SaasTextPrimary, fontSize = 9.5.sp)
        }
    }
}
