package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object SmartCartAndFreeAccessRepository {

  val exactMapLocations: List<ExactMapLocationPin> = listOf(
    // 1. Magasins & Vendeurs de Pièces de Rechange
    ExactMapLocationPin(
      id = "pin_store_alger_kouba",
      name = "AutoPièces Pro Kouba & Câblages OEM",
      category = MapPinCategory.PARTS_STORE,
      wilaya = "16 - Alger",
      exactAddress = "14 Avenue des Frères Bouadou, Bir Mourad Raïs / Kouba, Alger",
      latitude = 36.7325,
      longitude = 3.0682,
      phone = "+213 550 18 42 90",
      rating = 4.9,
      openingHours = "Sam - Jeu : 08h00 - 18h30",
      highlightBadge = "Stock Certifié PSA / VAG / Renault",
      availableStockOrFleetSummary = "Pompes à eau 206+, Kits distribution, Faisceaux moteurs & Capteurs OEM en stock immédiat",
      canvasXPercent = 0.48f,
      canvasYPercent = 0.24f
    ),
    ExactMapLocationPin(
      id = "pin_store_oran_senia",
      name = "Maghreb Pièces & Faisceaux Es-Sénia",
      category = MapPinCategory.PARTS_STORE,
      wilaya = "31 - Oran",
      exactAddress = "Zone Industrielle Es-Sénia Ilot 12, Route Nationale N4, Oran",
      latitude = 35.6541,
      longitude = -0.6218,
      phone = "+213 552 84 11 33",
      rating = 4.8,
      openingHours = "Sam - Jeu : 08h30 - 18h00",
      highlightBadge = "Grossiste & Détail OEM/IAM",
      availableStockOrFleetSummary = "Plus de 14 000 références : Freinage Brembo, Embrayages LuK, Pompes Valeo & Câblages ECU",
      canvasXPercent = 0.19f,
      canvasYPercent = 0.36f
    ),
    ExactMapLocationPin(
      id = "pin_store_setif_eulma",
      name = "Hub Pièces Dubaï El Eulma (VAG / Chery / PSA)",
      category = MapPinCategory.PARTS_STORE,
      wilaya = "19 - Sétif",
      exactAddress = "Quartier Commercial Dubaï, Rue des Frères Messaoudi, El Eulma, Sétif",
      latitude = 36.1528,
      longitude = 5.6901,
      phone = "+213 661 49 77 10",
      rating = 4.9,
      openingHours = "Sam - Jeu : 08h00 - 19h00",
      highlightBadge = "Importateur Direct OEM & Électrique",
      availableStockOrFleetSummary = "Spécialiste Faisceaux électriques, Calculateurs, Turbocompresseurs & Pièces Chery / Geely / VW",
      canvasXPercent = 0.67f,
      canvasYPercent = 0.31f
    ),

    // 2. Ateliers & Garagistes (Montage + Stock Tampon)
    ExactMapLocationPin(
      id = "pin_garage_alger_babez",
      name = "Atelier Mekanik+ Performance Bab Ezzouar",
      category = MapPinCategory.WORKSHOP_GARAGE,
      wilaya = "16 - Alger",
      exactAddress = "Zone d'Activité Bab Ezzouar, Lot 44 (Près USTHB), Alger",
      latitude = 36.7168,
      longitude = 3.1845,
      phone = "+213 560 91 20 45",
      rating = 4.9,
      openingHours = "Sam - Jeu : 08h00 - 17h30",
      highlightBadge = "Montage Garanti 12 Mois + Stock Atelier",
      availableStockOrFleetSummary = "Stock tampon sur place (Pompes à eau, Kits distribution, Plaquettes, Faisceaux) + Diagnostic OBD",
      mountingLaborPriceRangeDzd = "Montage Pompe à eau : 4 500 DA • Freinage : 2 000 DA • Diag Câblage : 3 000 DA",
      canvasXPercent = 0.53f,
      canvasYPercent = 0.28f
    ),
    ExactMapLocationPin(
      id = "pin_garage_blida_zi",
      name = "TechnoGarage & Électricité Auto Blida",
      category = MapPinCategory.WORKSHOP_GARAGE,
      wilaya = "09 - Blida",
      exactAddress = "Zone Industrielle Ben Boulaid, Rue N°8, Blida",
      latitude = 36.4812,
      longitude = 2.8340,
      phone = "+213 558 32 19 80",
      rating = 4.7,
      openingHours = "Sam - Jeu : 08h00 - 18h00",
      highlightBadge = "Spécialiste Mécanique PSA/Renault & Faisceaux",
      availableStockOrFleetSummary = "Réparation faisceaux électriques, montage distribution/pompe à eau 206+/208/Clio 4",
      mountingLaborPriceRangeDzd = "Montage Pompe à eau 206+ : 4 000 DA • Réparation Faisceau : 3 500 DA",
      canvasXPercent = 0.44f,
      canvasYPercent = 0.35f
    ),
    ExactMapLocationPin(
      id = "pin_garage_constantine_khroub",
      name = "EuroDiag & Montage Mécanique El Khroub",
      category = MapPinCategory.WORKSHOP_GARAGE,
      wilaya = "25 - Constantine",
      exactAddress = "Zone d'Activité El Khroub, Route Nationale 3, Constantine",
      latitude = 36.2635,
      longitude = 6.6932,
      phone = "+213 770 64 55 12",
      rating = 4.8,
      openingHours = "Sam - Jeu : 08h00 - 17h30",
      highlightBadge = "Atelier Agréé Devis & Montage",
      availableStockOrFleetSummary = "Montage express pièces commandées en Accès Libre avec garantie main-d'œuvre",
      mountingLaborPriceRangeDzd = "Montage Pompe à eau : 4 500 DA • Kit Embrayage : 11 000 DA",
      canvasXPercent = 0.78f,
      canvasYPercent = 0.29f
    ),

    // 3. Showrooms de Location de Voitures
    ExactMapLocationPin(
      id = "pin_rental_alger_hydra",
      name = "Showroom Luxury & Pro Car Rental Hydra",
      category = MapPinCategory.CAR_RENTAL_SHOWROOM,
      wilaya = "16 - Alger",
      exactAddress = "28 Chemin Sidi Yahia, Hydra, Alger",
      latitude = 36.7458,
      longitude = 3.0419,
      phone = "+213 561 77 88 00",
      rating = 4.9,
      openingHours = "7j/7 : 08h00 - 21h00",
      highlightBadge = "Showroom GPS Certifié • 24 Véhicules Dispo",
      availableStockOrFleetSummary = "VW Golf 8 R-Line (14 500 DA/j), Peugeot 208 GT (7 500 DA/j), Chery Tiggo 8 Pro (12 000 DA/j)",
      canvasXPercent = 0.46f,
      canvasYPercent = 0.21f
    ),
    ExactMapLocationPin(
      id = "pin_rental_oran_aeroport",
      name = "Oran Airport Mobility Showroom",
      category = MapPinCategory.CAR_RENTAL_SHOWROOM,
      wilaya = "31 - Oran",
      exactAddress = "Boulevard de l'Aéroport International Ahmed Ben Bella, Es-Sénia, Oran",
      latitude = 35.6302,
      longitude = -0.6085,
      phone = "+213 550 99 31 31",
      rating = 4.8,
      openingHours = "7j/7 : 07h00 - 23h00",
      highlightBadge = "Livraison Terminal Aéroport & Showroom",
      availableStockOrFleetSummary = "Renault Clio 5 Alpine (7 000 DA/j), Dacia Duster 4x4 (8 500 DA/j), Skoda Octavia DSG (11 000 DA/j)",
      canvasXPercent = 0.23f,
      canvasYPercent = 0.42f
    ),
    ExactMapLocationPin(
      id = "pin_rental_constantine_centre",
      name = "Cirta Prestige Auto Location Showroom",
      category = MapPinCategory.CAR_RENTAL_SHOWROOM,
      wilaya = "25 - Constantine",
      exactAddress = "Cité Boussouf, Avenue Novembre 1954, Constantine",
      latitude = 36.3570,
      longitude = 6.5890,
      phone = "+213 662 25 25 90",
      rating = 4.8,
      openingHours = "7j/7 : 08h00 - 20h00",
      highlightBadge = "Flotte Suivie par Télémétrie Mekanik+",
      availableStockOrFleetSummary = "Hyundai Tucson (11 500 DA/j), Fiat Tipo Berline (6 500 DA/j), Seat Ibiza FR (7 500 DA/j)",
      canvasXPercent = 0.82f,
      canvasYPercent = 0.37f
    )
  )

  val freeAccessCatalogItems: List<FreeAccessSearchItem> = listOf(
    FreeAccessSearchItem(
      id = "free_pompe_eau_206_plus",
      keywordTags = listOf("pompe à eau 206+", "pompe a eau 206", "206+", "peugeot 206", "1201g9", "1201.g9", "tu3jp", "kfw", "1.4i"),
      partName = "Pompe à Eau Renforcée avec Joint (Moteur 1.4i / 1.1i TU3JP)",
      vehicleApplication = "Peugeot 206+ (T3E) / 206 / 207 / Partner • Citroën C3 / Berlingo (1.4i 75ch KFW/TU3A)",
      isElectricalWiring = false,
      primaryOemRef = "1201.G9",
      crossReferences = listOf(
        "OEM PSA : 1201.G9 / 1201.E4 / 1609402180",
        "VALEO : 506539 (Qualité Première Monte)",
        "DOLZ : C-119",
        "SKF : VKPC 83205",
        "BOSCH : 1 987 949 710"
      ),
      compatibleVehicles = listOf(
        "Peugeot 206+ (2009 - 2013) • 1.1i 60ch & 1.4i 75ch (TU1JP / TU3JP)",
        "Peugeot 206 Berline / SW (1998 - 2009) • 1.4i 75ch",
        "Peugeot 207 (2006 - 2012) • 1.4 75ch",
        "Peugeot Partner / Citroën Berlingo 1.4i",
        "Citroën C2 / C3 I & II 1.1i / 1.4i"
      ),
      bestPartPriceDzd = 5800,
      averageMountingPriceDzd = 4200,
      mountingTimeMinutes = 110,
      diyDifficultyLabel = "Niveau Moyen (2h • Nécessite dépose courroie distribution + purge LDR)",
      diyRepairStepsPreview = listOf(
        "Étape 1 : Débrancher la cosse négative de la batterie et vidanger le circuit de refroidissement par le robinet bas du radiateur.",
        "Étape 2 : Déposer la roue avant droite, le pare-boue, la courroie d'accessoires et les carters de distribution sur le bloc TU3JP.",
        "Étape 3 : Piger le volant moteur (foret Ø 6 mm) et la poulie d'arbre à cames (vis M10), puis détendre le galet tendeur.",
        "Étape 4 : Déposer les 5 vis de la pompe à eau (1201.G9), nettoyer le plan de joint, poser la nouvelle pompe (couple : 14 Nm) et purger via les 2 vis de purge (boîtier thermostat + durite chauffage)."
      ),
      fullRepairManualLockedNote = "Schéma d'alignement des repères de courroie 108 dents, procédure de tension angulaire SEEM et schéma électrique du motoventilateur complets disponibles avec le Pass Accès Libre (10 000 DA/an).",
      stockVendors = listOf(
        CartStockHolderMatch(
          holderId = "pin_store_alger_kouba",
          holderName = "AutoPièces Pro Kouba (Magasin)",
          isWorkshop = false,
          wilaya = "16 - Alger",
          exactAddress = "14 Av. des Frères Bouadou, Kouba, Alger",
          latitude = 36.7325,
          longitude = 3.0682,
          matchedReference = "VALEO 506539 (Équiv. 1201.G9)",
          quantityAvailable = 14,
          partPriceDzd = 5800,
          mountingPriceDzd = null,
          phone = "+213 550 18 42 90"
        ),
        CartStockHolderMatch(
          holderId = "pin_store_oran_senia",
          holderName = "Maghreb Pièces Es-Sénia (Magasin)",
          isWorkshop = false,
          wilaya = "31 - Oran",
          exactAddress = "Zone Industrielle Es-Sénia Ilot 12, Oran",
          latitude = 35.6541,
          longitude = -0.6218,
          matchedReference = "SKF VKPC 83205 (Équiv. 1201.G9)",
          quantityAvailable = 9,
          partPriceDzd = 6100,
          mountingPriceDzd = null,
          phone = "+213 552 84 11 33"
        )
      ),
      partnerGarages = listOf(
        WorkshopMountingPriceEstimate(
          workshopName = "TechnoGarage & Mécanique PSA Blida",
          wilaya = "09 - Blida",
          distanceKm = 4.2,
          mountingLaborDzd = 4000,
          estimatedDurationMinutes = 110,
          includesWarrantyAndDiag = "Inclut montage pompe + purge liquide refroidissement G12 + contrôle étanchéité",
          phone = "+213 558 32 19 80",
          hasPartInWorkshopStock = true
        ),
        WorkshopMountingPriceEstimate(
          workshopName = "Atelier Mekanik+ Performance Bab Ezzouar",
          wilaya = "16 - Alger",
          distanceKm = 6.8,
          mountingLaborDzd = 4500,
          estimatedDurationMinutes = 95,
          includesWarrantyAndDiag = "Pièce disponible directement à l'atelier + Garantie montage 12 mois",
          phone = "+213 560 91 20 45",
          hasPartInWorkshopStock = true
        )
      )
    ),
    FreeAccessSearchItem(
      id = "free_faisceau_injecteur_golf7",
      keywordTags = listOf("faisceau", "câblage", "cablage", "électrique", "electrique", "golf 7", "tdi", "04l971600", "connecteur", "injecteur"),
      partName = "Faisceau de Câblage Électrique Moteur & Rampe Injecteurs / Bougies",
      vehicleApplication = "VW Golf 7 / Tiguan / Passat B8 • Audi A3 8V • Seat Leon 3 • Skoda Octavia (2.0 TDI CRLB / DFGA)",
      isElectricalWiring = true,
      primaryOemRef = "04L 971 600 L",
      crossReferences = listOf(
        "OEM VAG : 04L 971 600 L / 04L 972 623 C",
        "LEONI OEM : L-971600-VAG",
        "METZGER : 2324109",
        "HERTH+BUSS ELPARTS : 51277340",
        "DELPHI : WH-2048-TDI"
      ),
      compatibleVehicles = listOf(
        "Volkswagen Golf VII (2013 - 2020) • 2.0 TDI 150ch (CRLB, DFGA)",
        "Volkswagen Tiguan AD1 (2016 - 2023) • 2.0 TDI 150/190ch",
        "Audi A3 8V Sportback / Berline • 2.0 TDI",
        "Seat Leon III (5F) & Skoda Octavia III (5E) • 2.0 TDI"
      ),
      bestPartPriceDzd = 18500,
      averageMountingPriceDzd = 3500,
      mountingTimeMinutes = 65,
      diyDifficultyLabel = "Niveau Intermédiaire (1h • Repérage connecteurs étanches ECU T94)",
      diyRepairStepsPreview = listOf(
        "Étape 1 : Attendre l'endormissement du réseau CAN-Bus (10 min après coupure contact) puis débrancher la batterie 12V.",
        "Étape 2 : Déverrouiller les clips secondaires jaunes des 4 connecteurs piézo/solénoïdes d'injecteurs et du capteur de pression rail G247.",
        "Étape 3 : Déclipser la goulotte thermorésistante le long du couvre-culasse et brancher le nouveau faisceau 04L 971 600 L.",
        "Étape 4 : Effacer les codes défauts P0201 à P0204 à la valise OBD-II et vérifier la continuité (< 0.3 Ω)."
      ),
      fullRepairManualLockedNote = "Brochage complet du calculateur Bosch EDC17C64 / MD1CS004 (94 voies) avec code couleur des fils disponible dans le Pass Accès Libre (10 000 DA/an).",
      stockVendors = listOf(
        CartStockHolderMatch(
          holderId = "pin_store_setif_eulma",
          holderName = "Hub Pièces & Câblages Dubaï El Eulma",
          isWorkshop = false,
          wilaya = "19 - Sétif",
          exactAddress = "Quartier Commercial Dubaï, El Eulma, Sétif",
          latitude = 36.1528,
          longitude = 5.6901,
          matchedReference = "OEM VAG 04L 971 600 L (Neuf Origine)",
          quantityAvailable = 6,
          partPriceDzd = 18500,
          mountingPriceDzd = null,
          phone = "+213 661 49 77 10"
        ),
        CartStockHolderMatch(
          holderId = "pin_store_alger_kouba",
          holderName = "AutoPièces Pro Kouba & Câblages OEM",
          isWorkshop = false,
          wilaya = "16 - Alger",
          exactAddress = "14 Av. des Frères Bouadou, Kouba, Alger",
          latitude = 36.7325,
          longitude = 3.0682,
          matchedReference = "METZGER 2324109 (Équiv. 04L 971 600 L)",
          quantityAvailable = 4,
          partPriceDzd = 16900,
          mountingPriceDzd = null,
          phone = "+213 550 18 42 90"
        )
      ),
      partnerGarages = listOf(
        WorkshopMountingPriceEstimate(
          workshopName = "Atelier Mekanik+ Performance Bab Ezzouar",
          wilaya = "16 - Alger",
          distanceKm = 5.1,
          mountingLaborDzd = 3500,
          estimatedDurationMinutes = 60,
          includesWarrantyAndDiag = "Inclut pose faisceau + effacement défauts OBD + test faisceau à l'oscilloscope",
          phone = "+213 560 91 20 45",
          hasPartInWorkshopStock = true
        )
      )
    ),
    FreeAccessSearchItem(
      id = "free_embrayage_clio4",
      keywordTags = listOf("embrayage", "clio 4", "duster", "1.5 dci", "k9k", "302052307r", "butée", "luk", "valeo"),
      partName = "Kit d'Embrayage Complet 3 Pièces (Mécanisme + Disque + Butée Hydraulique)",
      vehicleApplication = "Renault Clio 4 / Captur / Kangoo 2 • Dacia Duster / Logan 2 / Sandero Stepway (1.5 dCi K9K)",
      isElectricalWiring = false,
      primaryOemRef = "302052307R",
      crossReferences = listOf(
        "OEM RENAULT : 302052307R / 302051526R",
        "VALEO : 828012 (Origine Constructeur)",
        "LuK : 622 3333 33",
        "SACHS : 3000 990 318"
      ),
      compatibleVehicles = listOf(
        "Renault Clio IV (2012 - 2020) • 1.5 dCi 75/90ch (K9K 608 / K9K 628)",
        "Dacia Sandero II & Stepway (2013 - 2021) • 1.5 dCi",
        "Dacia Logan II MCV & Berline • 1.5 dCi",
        "Renault Captur I & Kangoo II • 1.5 dCi"
      ),
      bestPartPriceDzd = 24500,
      averageMountingPriceDzd = 11000,
      mountingTimeMinutes = 240,
      diyDifficultyLabel = "Niveau Avancé (4h • Dépose berceau / boîte de vitesses JR5 / TL4)",
      diyRepairStepsPreview = listOf(
        "Étape 1 : Vidanger la boîte de vitesses JR5 (huile 75W80 API GL-4) et déposer les transmissions gauche et droite.",
        "Étape 2 : Soutenir le moteur K9K, déposer le support boîte et reculer la boîte de vitesses sur chandelles.",
        "Étape 3 : Centrer le nouveau disque Valeo 828012 avec un mandrin de centrage et serrer le mécanisme en étoile à 20 Nm.",
        "Étape 4 : Purger la butée hydraulique concentrique avec du liquide DOT 4 neuf sans actionner la pédale à sec."
      ),
      fullRepairManualLockedNote = "Couples complets du berceau avant, des rotules de suspension et procédure de purge sous pression débloqués dans le Pass Accès Libre (10 000 DA/an).",
      stockVendors = listOf(
        CartStockHolderMatch(
          holderId = "pin_store_oran_senia",
          holderName = "Maghreb Pièces Es-Sénia",
          isWorkshop = false,
          wilaya = "31 - Oran",
          exactAddress = "ZI Es-Sénia Ilot 12, Oran",
          latitude = 35.6541,
          longitude = -0.6218,
          matchedReference = "VALEO 828012 (Équiv. 302052307R)",
          quantityAvailable = 11,
          partPriceDzd = 24500,
          mountingPriceDzd = null,
          phone = "+213 552 84 11 33"
        )
      ),
      partnerGarages = listOf(
        WorkshopMountingPriceEstimate(
          workshopName = "EuroDiag & Montage Mécanique El Khroub",
          wilaya = "25 - Constantine",
          distanceKm = 3.8,
          mountingLaborDzd = 11000,
          estimatedDurationMinutes = 240,
          includesWarrantyAndDiag = "Dépose/pose boîte JR5 + centrage laser + purge récepteur hydraulique",
          phone = "+213 770 64 55 12",
          hasPartInWorkshopStock = true
        )
      )
    )
  )

  private val _smartCartItems = MutableStateFlow(
    listOf(
      SmartExplodedCartItem(
        id = "cart_item_waterpump_206",
        partOrHarnessName = "Pompe à Eau & Joint d'Étanchéité (Schéma Refroidissement Repère #1)",
        categoryLabel = "Refroidissement & Distribution",
        isElectricalHarnessOrWiring = false,
        calloutNumber = 1,
        sourceVin = "VF32HNFUF42891034",
        sourceVehicleName = "Peugeot 206+ 1.4i 75ch (TU3JP)",
        primaryOemReference = "1201.G9",
        compatibleReferences = listOf(
          "OEM PSA : 1201.G9 / 1201.E4 / 1609402180",
          "VALEO : 506539",
          "SKF : VKPC 83205",
          "DOLZ : C-119",
          "BOSCH : 1 987 949 710"
        ),
        compatibleVehicles = listOf(
          "Peugeot 206+ 1.1i & 1.4i (2009-2013)",
          "Peugeot 206 / 207 / Partner 1.4i (TU3JP / KFW)",
          "Citroën C3 I & II / Berlingo 1.4i 75ch"
        ),
        unitPriceEstimatedDzd = 5800,
        quantity = 1,
        matchingVendorsInStock = freeAccessCatalogItems[0].stockVendors,
        matchingWorkshopsInStock = listOf(
          CartStockHolderMatch(
            holderId = "pin_garage_alger_babez",
            holderName = "Atelier Mekanik+ Performance Bab Ezzouar (Stock Atelier)",
            isWorkshop = true,
            wilaya = "16 - Alger",
            exactAddress = "Zone d'Activité Bab Ezzouar, Lot 44, Alger",
            latitude = 36.7168,
            longitude = 3.1845,
            matchedReference = "VALEO 506539 / 1201.G9",
            quantityAvailable = 4,
            partPriceDzd = 5900,
            mountingPriceDzd = 4500,
            phone = "+213 560 91 20 45"
          ),
          CartStockHolderMatch(
            holderId = "pin_garage_blida_zi",
            holderName = "TechnoGarage & Mécanique PSA Blida (Stock Atelier)",
            isWorkshop = true,
            wilaya = "09 - Blida",
            exactAddress = "ZI Ben Boulaid, Rue N°8, Blida",
            latitude = 36.4812,
            longitude = 2.8340,
            matchedReference = "SKF VKPC 83205 / 1201.G9",
            quantityAvailable = 3,
            partPriceDzd = 6000,
            mountingPriceDzd = 4000,
            phone = "+213 558 32 19 80"
          )
        ),
        mountingEstimates = freeAccessCatalogItems[0].partnerGarages
      ),
      SmartExplodedCartItem(
        id = "cart_item_harness_golf7",
        partOrHarnessName = "Faisceau de Câblage Électrique Rampe Injecteurs & Capteur Rail (Repère #4)",
        categoryLabel = "Câblages Électriques & Faisceaux ECU",
        isElectricalHarnessOrWiring = true,
        calloutNumber = 4,
        sourceVin = "WVGZZZ5NZJW849201",
        sourceVehicleName = "Volkswagen Tiguan / Golf 7 2.0 TDI 150ch (DFGA)",
        primaryOemReference = "04L 971 600 L",
        compatibleReferences = listOf(
          "OEM VAG : 04L 971 600 L / 04L 972 623 C",
          "LEONI OEM : L-971600-VAG",
          "METZGER : 2324109",
          "HERTH+BUSS ELPARTS : 51277340"
        ),
        compatibleVehicles = listOf(
          "VW Golf 7 / Tiguan AD1 / Passat B8 2.0 TDI (CRLB, DFGA)",
          "Audi A3 8V / Q3 2.0 TDI",
          "Seat Leon 3 (5F) & Skoda Octavia 3 (5E) 2.0 TDI"
        ),
        unitPriceEstimatedDzd = 18500,
        quantity = 1,
        matchingVendorsInStock = freeAccessCatalogItems[1].stockVendors,
        matchingWorkshopsInStock = listOf(
          CartStockHolderMatch(
            holderId = "pin_garage_alger_babez",
            holderName = "Atelier Mekanik+ Performance Bab Ezzouar (Stock Atelier)",
            isWorkshop = true,
            wilaya = "16 - Alger",
            exactAddress = "Zone d'Activité Bab Ezzouar, Lot 44, Alger",
            latitude = 36.7168,
            longitude = 3.1845,
            matchedReference = "04L 971 600 L (Origine VAG)",
            quantityAvailable = 2,
            partPriceDzd = 18900,
            mountingPriceDzd = 3500,
            phone = "+213 560 91 20 45"
          )
        ),
        mountingEstimates = freeAccessCatalogItems[1].partnerGarages
      )
    )
  )
  val smartCartItems: StateFlow<List<SmartExplodedCartItem>> = _smartCartItems.asStateFlow()

  private val _particulierAccountState = MutableStateFlow(ParticulierAccountState())
  val particulierAccountState: StateFlow<ParticulierAccountState> = _particulierAccountState.asStateFlow()

  fun addPartFromCatalogExplodedSchema(part: PartItem, activeVehicle: Vehicle) {
    val existing = _smartCartItems.value.firstOrNull { it.primaryOemReference.equals(part.oemReference, ignoreCase = true) }
    if (existing != null) {
      _smartCartItems.update { list ->
        list.map {
          if (it.id == existing.id) it.copy(quantity = it.quantity + 1) else it
        }
      }
      return
    }

    val isWiring = part.system == SystemCategory.ELECTRICITE ||
      part.system == SystemCategory.VEHICULE_ELECTRIQUE ||
      part.name.contains("câbl", ignoreCase = true) ||
      part.name.contains("faisceau", ignoreCase = true) ||
      part.name.contains("capteur", ignoreCase = true)

    val crossRefs = listOf("OEM Origine : ${part.oemReference}") +
      part.oemAlternatives.map { "OEM Remplacée : $it" } +
      part.crossReferences.map { "${it.brand} : ${it.reference} (${it.qualityGrade})" }

    val compatVehicles = listOf(
      "${activeVehicle.brand} ${activeVehicle.model} ${activeVehicle.generation} (${activeVehicle.engineCode} • ${activeVehicle.exactYear})",
      "${activeVehicle.brand} Plateforme ${activeVehicle.platform} (${part.yearCompatibility})",
      "Modèles partageant le bloc ${activeVehicle.engineCode} (${part.compatibilityBadge})"
    )

    val newItem = SmartExplodedCartItem(
      id = "cart_${part.id}_${System.currentTimeMillis() % 10000}",
      partOrHarnessName = part.name,
      categoryLabel = part.system.label,
      isElectricalHarnessOrWiring = isWiring,
      calloutNumber = part.schemaCalloutNumber,
      sourceVin = activeVehicle.vin,
      sourceVehicleName = "${activeVehicle.brand} ${activeVehicle.model} (${activeVehicle.engineCode})",
      primaryOemReference = part.oemReference,
      compatibleReferences = crossRefs,
      compatibleVehicles = compatVehicles,
      unitPriceEstimatedDzd = part.priceEstimatedDzd,
      quantity = 1,
      matchingVendorsInStock = listOf(
        CartStockHolderMatch(
          holderId = "pin_store_alger_kouba",
          holderName = "AutoPièces Pro Kouba (Magasin)",
          isWorkshop = false,
          wilaya = "16 - Alger",
          exactAddress = "14 Av. des Frères Bouadou, Kouba, Alger",
          latitude = 36.7325,
          longitude = 3.0682,
          matchedReference = "${part.crossReferences.firstOrNull()?.brand ?: "OEM"} ${part.crossReferences.firstOrNull()?.reference ?: part.oemReference}",
          quantityAvailable = 8,
          partPriceDzd = part.priceEstimatedDzd,
          mountingPriceDzd = null,
          phone = "+213 550 18 42 90"
        ),
        CartStockHolderMatch(
          holderId = "pin_store_setif_eulma",
          holderName = "Hub Pièces Dubaï El Eulma (Magasin)",
          isWorkshop = false,
          wilaya = "19 - Sétif",
          exactAddress = "Quartier Commercial Dubaï, El Eulma, Sétif",
          latitude = 36.1528,
          longitude = 5.6901,
          matchedReference = "OEM ${part.oemReference}",
          quantityAvailable = 12,
          partPriceDzd = (part.priceEstimatedDzd * 0.96).toInt(),
          mountingPriceDzd = null,
          phone = "+213 661 49 77 10"
        )
      ),
      matchingWorkshopsInStock = listOf(
        CartStockHolderMatch(
          holderId = "pin_garage_alger_babez",
          holderName = "Atelier Mekanik+ Performance Bab Ezzouar (Stock Atelier)",
          isWorkshop = true,
          wilaya = "16 - Alger",
          exactAddress = "Zone d'Activité Bab Ezzouar, Lot 44, Alger",
          latitude = 36.7168,
          longitude = 3.1845,
          matchedReference = part.oemReference,
          quantityAvailable = 3,
          partPriceDzd = part.priceEstimatedDzd,
          mountingPriceDzd = 4000,
          phone = "+213 560 91 20 45"
        )
      ),
      mountingEstimates = listOf(
        WorkshopMountingPriceEstimate(
          workshopName = "Atelier Mekanik+ Performance Bab Ezzouar",
          wilaya = "16 - Alger",
          distanceKm = 5.2,
          mountingLaborDzd = 4000,
          estimatedDurationMinutes = 90,
          includesWarrantyAndDiag = "Pièce disponible dans le stock de l'atelier + Garantie pose 12 mois",
          phone = "+213 560 91 20 45",
          hasPartInWorkshopStock = true
        )
      )
    )

    _smartCartItems.update { listOf(newItem) + it }
  }

  fun addCalloutFromWebExplodedDiagram(
    callout: ExplodedCalloutItem,
    sheet: OemExplodedDiagramSheet
  ) {
    val existing = _smartCartItems.value.firstOrNull { it.primaryOemReference.equals(callout.oemReference, ignoreCase = true) }
    if (existing != null) {
      _smartCartItems.update { list ->
        list.map {
          if (it.id == existing.id) it.copy(quantity = it.quantity + 1) else it
        }
      }
      return
    }

    val isWiring = callout.partName.contains("câbl", ignoreCase = true) ||
      callout.partName.contains("faisceau", ignoreCase = true) ||
      callout.partName.contains("capteur", ignoreCase = true) ||
      callout.partName.contains("électr", ignoreCase = true)

    val newItem = SmartExplodedCartItem(
      id = "cart_web_${callout.calloutNumber}_${System.currentTimeMillis() % 10000}",
      partOrHarnessName = "${callout.partName} (Planche ${sheet.diagramCode})",
      categoryLabel = sheet.diagramTitle,
      isElectricalHarnessOrWiring = isWiring,
      calloutNumber = callout.calloutNumber,
      sourceVin = "VIN-${sheet.brandGroup.name}-2025",
      sourceVehicleName = "${sheet.vehicleTitle} (${sheet.engineAndGearboxCode})",
      primaryOemReference = callout.oemReference,
      compatibleReferences = listOfNotNull(
        "OEM Constructeur : ${callout.oemReference}",
        callout.replacementChainRef?.let { "Chaîne / Équivalence : $it" }
      ),
      compatibleVehicles = listOf(
        "${sheet.vehicleTitle} • ${sheet.engineAndGearboxCode}",
        "Gamme ${sheet.brandGroup.label} partageant la planche ${sheet.diagramCode}"
      ),
      unitPriceEstimatedDzd = callout.estimatedOemPriceDzd,
      quantity = 1,
      matchingVendorsInStock = listOf(
        CartStockHolderMatch(
          holderId = "pin_store_alger_kouba",
          holderName = "AutoPièces Pro Kouba (Magasin)",
          isWorkshop = false,
          wilaya = "16 - Alger",
          exactAddress = "14 Av. des Frères Bouadou, Kouba, Alger",
          latitude = 36.7325,
          longitude = 3.0682,
          matchedReference = "${callout.oemReference} ${callout.replacementChainRef ?: ""}".trim(),
          quantityAvailable = 6,
          partPriceDzd = callout.estimatedOemPriceDzd,
          mountingPriceDzd = null,
          phone = "+213 550 18 42 90"
        )
      ),
      matchingWorkshopsInStock = listOf(
        CartStockHolderMatch(
          holderId = "pin_garage_alger_babez",
          holderName = "Atelier Mekanik+ Performance Bab Ezzouar (Stock Atelier)",
          isWorkshop = true,
          wilaya = "16 - Alger",
          exactAddress = "Zone d'Activité Bab Ezzouar, Lot 44, Alger",
          latitude = 36.7168,
          longitude = 3.1845,
          matchedReference = callout.oemReference,
          quantityAvailable = 2,
          partPriceDzd = callout.estimatedOemPriceDzd,
          mountingPriceDzd = 4500,
          phone = "+213 560 91 20 45"
        )
      ),
      mountingEstimates = listOf(
        WorkshopMountingPriceEstimate(
          workshopName = "Atelier Mekanik+ Performance Bab Ezzouar",
          wilaya = "16 - Alger",
          distanceKm = 5.2,
          mountingLaborDzd = 4500,
          estimatedDurationMinutes = 95,
          includesWarrantyAndDiag = "Pièce en stock atelier + pose constructeur (${callout.technicalConditionNote})",
          phone = "+213 560 91 20 45",
          hasPartInWorkshopStock = true
        )
      )
    )

    _smartCartItems.update { listOf(newItem) + it }
  }

  fun removeSmartCartItem(itemId: String) {
    _smartCartItems.update { list -> list.filterNot { it.id == itemId } }
  }

  fun updateSmartCartQuantity(itemId: String, delta: Int) {
    _smartCartItems.update { list ->
      list.mapNotNull { item ->
        if (item.id == itemId) {
          val nextQty = item.quantity + delta
          if (nextQty <= 0) null else item.copy(quantity = nextQty)
        } else item
      }
    }
  }

  fun addExtraVinToParticulierAccount(vin: String, vehicleLabel: String, engineCode: String, year: Int) {
    val cleanVin = vin.trim().uppercase().ifBlank { "VF1RJA00568129401" }
    if (_particulierAccountState.value.registeredVins.any { it.vin.equals(cleanVin, ignoreCase = true) }) {
      return
    }
    _particulierAccountState.update { state ->
      val isFirst = state.registeredVins.isEmpty()
      val fee = if (isFirst) 0 else state.extraVinUnitFeeDzd
      val updatedList = state.registeredVins + RegisteredParticulierVin(
        vin = cleanVin,
        vehicleLabel = vehicleLabel.ifBlank { "Véhicule Particulier ($cleanVin)" },
        engineCode = engineCode.ifBlank { "OEM-STD" },
        year = year,
        isPrimaryIncludedVin = isFirst,
        extraFeePaidDzd = fee
      )
      state.copy(
        registeredVins = updatedList,
        statusMessage = "Châssis $cleanVin ajouté avec succès (+$fee DA/an) • Total : ${updatedList.size} véhicule(s) actif(s)"
      )
    }
  }

  fun removeExtraVinFromParticulierAccount(vin: String) {
    _particulierAccountState.update { state ->
      if (state.registeredVins.size <= 1) return@update state
      val updated = state.registeredVins.filterNot { it.vin == vin }
      state.copy(
        registeredVins = updated,
        statusMessage = "Châssis $vin retiré • Total : ${updated.size} véhicule(s)"
      )
    }
  }

  fun selectParticulierPlanTier(tier: ParticulierPlanTier) {
    _particulierAccountState.update { state ->
      state.copy(
        currentTier = tier,
        statusMessage = if (tier == ParticulierPlanTier.FULL_LIBRE_10000) {
          "Pass Accès Libre Intégral (10 000 DA/an) activé : Tous les schémas électriques, ordres de réparation et manuels complets sont débloqués !"
        } else {
          "Formule Particulier Basique (3 500 DA/an) active : 1 châssis inclus + guides de réparation essentiels (+1 200 DA par châssis supplémentaire)."
        }
      )
    }
  }
}
