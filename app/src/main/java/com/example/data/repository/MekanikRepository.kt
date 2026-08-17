package com.example.data.repository

import com.example.data.model.*

object MekanikRepository {

  // Comprehensive Vehicle Database (covering up to 2025)
  val sampleVehicles: List<Vehicle> = listOf(
    Vehicle(
      id = "vw_tiguan_2021",
      vin = "WVWZZZ5NZMW123456",
      brand = "Volkswagen",
      model = "Tiguan II Facelift",
      generation = "Phase 2 (AD1 Facelift)",
      yearRange = "2021 - 2025",
      exactYear = 2021,
      engineCode = "DFGA",
      displacement = "2.0 TDI SCR 4Motion",
      powerCh = 150,
      powerKw = 110,
      fuelType = "Diesel (AdBlue)",
      gearbox = "DSG7 (DQ381)",
      platform = "MQB Evo",
      assemblyPlant = "Wolfsburg, Allemagne",
      generationNotes = "Facelift 2021+ : Nouveaux étriers et capteurs d'usure de plaquettes. Réf avant spécifique 5Q0 698 151 BR."
    ),
    Vehicle(
      id = "vw_tiguan_2017",
      vin = "WVWZZZ5NZHW098765",
      brand = "Volkswagen",
      model = "Tiguan II",
      generation = "Phase 1 (AD1)",
      yearRange = "2016 - 2020",
      exactYear = 2017,
      engineCode = "DFGA",
      displacement = "2.0 TDI",
      powerCh = 150,
      powerKw = 110,
      fuelType = "Diesel",
      gearbox = "DSG7 (DQ500)",
      platform = "MQB",
      assemblyPlant = "Wolfsburg, Allemagne",
      generationNotes = "Génération pré-facelift : Réf plaquettes avant standard 5Q0 698 151 AP."
    ),
    Vehicle(
      id = "peugeot_308_2022",
      vin = "VF3FPHNSSMY001234",
      brand = "Peugeot",
      model = "308 III (P51)",
      generation = "Génération III",
      yearRange = "2021 - 2025",
      exactYear = 2022,
      engineCode = "DV5RC",
      displacement = "1.5 BlueHDi 130",
      powerCh = 130,
      powerKw = 96,
      fuelType = "Diesel",
      gearbox = "EAT8 (Automatique)",
      platform = "EMP2 V3",
      assemblyPlant = "Mulhouse, France",
      generationNotes = "Moteur 1.5 BlueHDi : Kit chaîne d'arbres à cames renforcé 8mm OEM 16 381 573 80."
    ),
    Vehicle(
      id = "renault_clio5_2023",
      vin = "VF1RJA00067890123",
      brand = "Renault",
      model = "Clio V (BJA)",
      generation = "Phase 1 / 2",
      yearRange = "2019 - 2025",
      exactYear = 2023,
      engineCode = "K9K 872",
      displacement = "1.5 Blue dCi",
      powerCh = 100,
      powerKw = 74,
      fuelType = "Diesel",
      gearbox = "BVM6",
      platform = "CMF-B",
      assemblyPlant = "Bursa, Turquie",
      generationNotes = "Injecteurs piézo-électriques nouvelle génération Bosch."
    ),
    Vehicle(
      id = "vw_golf8_2024",
      vin = "WVWZZZCDZPW554433",
      brand = "Volkswagen",
      model = "Golf 8 (CD1)",
      generation = "Génération VIII",
      yearRange = "2020 - 2025",
      exactYear = 2024,
      engineCode = "EA211 evo",
      displacement = "1.5 eTSI Mild-Hybrid",
      powerCh = 150,
      powerKw = 110,
      fuelType = "Hybride Essence 48V",
      gearbox = "DSG7 (DQ200)",
      platform = "MQB Evo",
      assemblyPlant = "Wolfsburg, Allemagne",
      generationNotes = "Système hybride 48V avec alterno-démarreur entraîné par courroie."
    ),
    Vehicle(
      id = "audi_a3_2023",
      vin = "WAUZZZGY4NA112233",
      brand = "Audi",
      model = "A3 Sportback (8Y)",
      generation = "Génération IV",
      yearRange = "2020 - 2025",
      exactYear = 2023,
      engineCode = "DTSA",
      displacement = "2.0 TDI 35 TDI",
      powerCh = 150,
      powerKw = 110,
      fuelType = "Diesel",
      gearbox = "S-Tronic 7",
      platform = "MQB Evo",
      assemblyPlant = "Ingolstadt, Allemagne",
      generationNotes = "Système freinage électromécanique avec disques 312mm avant."
    ),
    Vehicle(
      id = "toyota_hilux_2022",
      vin = "MR0FA3CD000445566",
      brand = "Toyota",
      model = "Hilux Revo",
      generation = "Génération VIII Facelift",
      yearRange = "2015 - 2025",
      exactYear = 2022,
      engineCode = "1GD-FTV",
      displacement = "2.8 D-4D 4x4",
      powerCh = 204,
      powerKw = 150,
      fuelType = "Diesel",
      gearbox = "Automatique 6 rapports",
      platform = "Toyota IMV",
      assemblyPlant = "Durban, Afrique du Sud",
      generationNotes = "Filtre FAP & DPNR spécifique avec 5ème injecteur de régénération."
    ),
    Vehicle(
      id = "tesla_model3_2023",
      vin = "5YJ3E1EB7NF998877",
      brand = "Tesla",
      model = "Model 3 Highland",
      generation = "Refresh 2023-2025",
      yearRange = "2023 - 2025",
      exactYear = 2023,
      engineCode = "3D6 / 3D7",
      displacement = "Dual Motor AWD (EV)",
      powerCh = 440,
      powerKw = 324,
      fuelType = "Électrique (EV)",
      gearbox = "Transmission directe 1 rapport",
      platform = "Tesla EV Architecture",
      assemblyPlant = "Shanghai, Chine",
      isEv = true,
      generationNotes = "Pompe à chaleur octovalve, plaquettes de frein à faible frottement régénératif."
    )
  )

  // Exploded Schemas and Parts Database (e.g. Brake System for Tiguan / MQB)
  val tiguanBrakeSchema = ExplodedSchema(
    system = SystemCategory.FREINAGE,
    title = "Freinage Avant - Système TRW/Lucas 312x25mm",
    subTitle = "Schéma Technique Éclaté (Inspiré ETKA / Partslink24)",
    diagramType = "etka_brake_front",
    hotspots = listOf(
      ExplodedHotspot(1, "Jeu de 4 plaquettes de frein avant avec témoin", 48f, 32f, "part_pad_front"),
      ExplodedHotspot(2, "Disque de frein ventilé 312x25mm (5 trous)", 22f, 45f, "part_disc_front"),
      ExplodedHotspot(3, "Étrier de frein flottant gauche (Rouge/Alu)", 75f, 28f, "part_caliper_left"),
      ExplodedHotspot(4, "Support d'étrier de frein (Chape)", 65f, 60f, "part_caliper_carrier"),
      ExplodedHotspot(5, "Flexible de frein haute pression renforcé", 82f, 15f, "part_brake_hose"),
      ExplodedHotspot(6, "Kit accessoires & ressorts antibruit", 38f, 72f, "part_hardware_kit")
    )
  )

  val sampleParts: List<PartItem> = listOf(
    PartItem(
      id = "part_pad_front",
      system = SystemCategory.FREINAGE,
      name = "Jeu de plaquettes de frein avant",
      position = "Essieu Avant (Gauche & Droit)",
      oemReference = "5Q0 698 151 BR",
      oemAlternatives = listOf("5Q0 698 151 AP", "3AA 615 301 A", "8V0 698 151 C"),
      crossReferences = listOf(
        CrossReference("Brembo", "P 85 126", "Gamme Prime Performance"),
        CrossReference("Bosch", "0 986 494 660", "Qualité Première Monte OEM"),
        CrossReference("Ferodo", "FDB4454", "Eco-Friction Sans Cuivre"),
        CrossReference("ATE", "13.0460-7294.2", "Ceramic Faible Poussière"),
        CrossReference("TRW", "GDB1956", "Cotec Friction Coating"),
        CrossReference("Valeo", "601332", "First Range")
      ),
      schemaCalloutNumber = 1,
      yearCompatibility = "Tiguan 2016-2020 (AP) / Facelift 2021-2025 (BR)",
      compatibilityBadge = "100% Vérifié Châssis MQB Evo",
      priceEstimatedDzd = 8500,
      description = "Plaquettes de frein avant haute endurance adaptées au système TRW avec capteur de témoin d'usure électronique intégré. Conforme norme ECE R90.",
      technicalSpecs = mapOf(
        "Épaisseur" to "20.3 mm",
        "Largeur" to "160.2 mm",
        "Hauteur" to "64.5 mm",
        "Système de freinage" to "Lucas / TRW",
        "Témoin d'usure" to "Inclus (Longueur câble 165mm)"
      )
    ),
    PartItem(
      id = "part_disc_front",
      system = SystemCategory.FREINAGE,
      name = "Paire de disques de frein ventilés 312mm",
      position = "Essieu Avant",
      oemReference = "1K0 615 301 AA",
      oemAlternatives = listOf("5Q0 615 301 F", "3C0 615 301 C"),
      crossReferences = listOf(
        CrossReference("Brembo", "09.9772.11", "Peint anti-corrosion UV"),
        CrossReference("Bosch", "0 986 479 932", "High Carbon ventilé"),
        CrossReference("ATE", "24.0125-0158.1", "PowerDisc rainuré"),
        CrossReference("Zimmermann", "600.3233.20", "Sport Perforé Coat Z")
      ),
      schemaCalloutNumber = 2,
      yearCompatibility = "Tous modèles Tiguan II & Golf 7/8 2.0 TDI",
      compatibilityBadge = "Montage Direct Origine",
      priceEstimatedDzd = 18500,
      description = "Disques ventilés haute teneur en carbone pour dissipation thermique optimale sous contraintes lourdes.",
      technicalSpecs = mapOf(
        "Diamètre" to "312.0 mm",
        "Épaisseur" to "25.0 mm (Min 22.0 mm)",
        "Nombre de trous" to "5 / 112",
        "Traitement" to "Haute teneur en carbone verni"
      )
    ),
    PartItem(
      id = "part_caliper_left",
      system = SystemCategory.FREINAGE,
      name = "Étrier de frein avant gauche monopiston",
      position = "Avant Gauche",
      oemReference = "5Q0 615 123 D",
      oemAlternatives = listOf("8V0 615 123"),
      crossReferences = listOf(
        CrossReference("TRW", "BHW1071E", "Échange standard neuf"),
        CrossReference("Brembo", "F 85 330", "Garantie 2 ans"),
        CrossReference("Budweg", "344848", "Piston 57mm chromé")
      ),
      schemaCalloutNumber = 3,
      yearCompatibility = "2016 - 2025",
      compatibilityBadge = "Système Lucas 57mm",
      priceEstimatedDzd = 24000,
      description = "Étrier de frein en fonte d'aluminium anodisée pour disques épaisseur 25mm.",
      technicalSpecs = mapOf("Diamètre piston" to "57 mm", "Matière" to "Aluminium forgé")
    ),
    PartItem(
      id = "part_timing_chain",
      system = SystemCategory.DISTRIBUTION,
      name = "Kit chaîne de distribution renforcée 8mm",
      position = "Moteur - Arbres à cames",
      oemReference = "16 381 573 80",
      oemAlternatives = listOf("98 239 548 80", "16 832 764 80"),
      crossReferences = listOf(
        CrossReference("Gates", "KP15678XS", "Kit complet avec pompe à eau"),
        CrossReference("INA", "530 0698 30", "Galets tendeurs renforcés"),
        CrossReference("Dayco", "KTB884", "Gamme OEM High Temp"),
        CrossReference("Continental", "CT1233K1", "Courroie + Chaîne 8mm")
      ),
      schemaCalloutNumber = 1,
      yearCompatibility = "Peugeot/Citroën 1.5 BlueHDi (DV5RC) 2018-2025",
      compatibilityBadge = "Mise à jour essentielle 7mm -> 8mm",
      priceEstimatedDzd = 32000,
      description = "Kit complet d'arbres à cames et chaîne modifiée 8mm prévenant la rupture prématurée sur les moteurs DV5.",
      technicalSpecs = mapOf(
        "Largeur maillon" to "8.0 mm renforcé",
        "Comprend" to "Chaîne, Arbre d'admission, Tendeur hydraulique, Joints"
      )
    ),
    PartItem(
      id = "part_filter_oil",
      system = SystemCategory.FILTRATION,
      name = "Filtre à huile moteur haute filtration",
      position = "Carter Filtre Moteur",
      oemReference = "03N 115 562 B",
      oemAlternatives = listOf("03N 115 466"),
      crossReferences = listOf(
        CrossReference("Mann-Filter", "HU 7020 z", "Qualité Origine OE"),
        CrossReference("Purflux", "L980", "Plissage chevrons"),
        CrossReference("Mahle", "OX 787 D", "EcoFilter"),
        CrossReference("Bosch", "F 026 407 157", "Filtration micro-particules")
      ),
      schemaCalloutNumber = 1,
      yearCompatibility = "Moteurs 2.0 TDI EA288 (2014-2025)",
      compatibilityBadge = "100% Origine VAG",
      priceEstimatedDzd = 1900,
      description = "Cartouche filtrante avec joint torique en élastomère résistant aux huiles de synthèse 0W-20 et 5W-30.",
      technicalSpecs = mapOf("Hauteur" to "100 mm", "Diamètre extérieur" to "65 mm")
    ),
    PartItem(
      id = "part_ev_inverter_pump",
      system = SystemCategory.VEHICULE_ELECTRIQUE,
      name = "Pompe à eau auxiliaire 12V refroidissement Inverter EV",
      position = "Boucle Basse Température Batterie",
      oemReference = "5QE 965 561 B",
      oemAlternatives = listOf("1K0 965 561 J"),
      crossReferences = listOf(
        CrossReference("Pierburg", "7.04071.71.0", "Moteur Brushless 12V"),
        CrossReference("Bosch", "0 392 023 004", "Débit régulé CAN-bus")
      ),
      schemaCalloutNumber = 1,
      yearCompatibility = "Véhicules Hybrides & Électriques VAG / MEB (2020-2025)",
      compatibilityBadge = "Véhicule Électrique / Hybride",
      priceEstimatedDzd = 28500,
      description = "Pompe de circulation électrique sans balais pour le refroidissement du pack batterie haute tension et de l'électronique de puissance.",
      technicalSpecs = mapOf("Tension" to "12V", "Consommation" to "1.5A", "Débit" to "1200 L/h")
    )
  )

  // Algerian Auto Parts Stores Directory (Across 58 Wilayas)
  val sampleVendors: List<Vendor> = listOf(
    Vendor(
      id = "vendor_bab_ezzouar",
      name = "AutoPièces Bab Ezzouar",
      wilaya = "Alger (16)",
      wilayaCode = 16,
      commune = "Bab Ezzouar",
      address = "Cité 5 Juillet, Rue 12 N° 45, Bab Ezzouar",
      phone = "0550 12 34 56 / 023 88 90 12",
      specialty = "Spécialiste VAG (VW, Audi, Seat, Skoda) & Allemandes",
      isVerified = true,
      isPremium = true,
      registerCommerceNumber = "RC: 16/00-0876421B21",
      rating = 4.9f,
      reviewCount = 142,
      deliveryAvailable = true,
      inStockItemsCount = 3850
    ),
    Vendor(
      id = "vendor_oran_pieces",
      name = "Sarl Maghreb Pièces Détachées",
      wilaya = "Oran (31)",
      wilayaCode = 31,
      commune = "Bir El Djir",
      address = "Boulevard Millenium 2, Bâtiment El Bahia",
      phone = "0560 99 88 77 / 041 55 44 33",
      specialty = "Multimarque & Pièces d'Origine Françaises (Peugeot, Renault)",
      isVerified = true,
      isPremium = true,
      registerCommerceNumber = "RC: 31/00-0043198A19",
      rating = 4.8f,
      reviewCount = 98,
      deliveryAvailable = true,
      inStockItemsCount = 4200
    ),
    Vendor(
      id = "vendor_setif_turbo",
      name = "Sétif Auto Performance & Freinage",
      wilaya = "Sétif (19)",
      wilayaCode = 19,
      commune = "Sétif Centre",
      address = "Zone Industrielle Ouest, Lot N° 18",
      phone = "0661 33 22 11",
      specialty = "Freinage Brembo, Filtration Mann & Kits Distribution",
      isVerified = true,
      isPremium = false,
      registerCommerceNumber = "RC: 19/00-0112984B22",
      rating = 4.7f,
      reviewCount = 74,
      deliveryAvailable = true,
      inStockItemsCount = 2100
    ),
    Vendor(
      id = "vendor_blida_moteur",
      name = "Comptoir Central Pièces Blida",
      wilaya = "Blida (09)",
      wilayaCode = 9,
      commune = "Ouled Yaïch",
      address = "Route Nationale 1, En face Stade",
      phone = "0770 45 67 89",
      specialty = "Organes Moteurs, Embrayages LuK/Sachs & Électrique",
      isVerified = true,
      isPremium = false,
      registerCommerceNumber = "RC: 09/00-0993817B20",
      rating = 4.6f,
      reviewCount = 61,
      deliveryAvailable = true,
      inStockItemsCount = 1890
    ),
    Vendor(
      id = "vendor_constantine_auto",
      name = "Constantine Auto Express",
      wilaya = "Constantine (25)",
      wilayaCode = 25,
      commune = "Ali Mendjeli",
      address = "UV 13, Boulevard de l'ALN",
      phone = "0555 67 89 01",
      specialty = "Asiatiques (Toyota, Hyundai, Kia) & Allemandes",
      isVerified = true,
      isPremium = true,
      registerCommerceNumber = "RC: 25/00-0554198A23",
      rating = 4.9f,
      reviewCount = 115,
      deliveryAvailable = true,
      inStockItemsCount = 3100
    )
  )

  // Real-time Vendor Stock Association
  val vendorStockList: List<VendorStockItem> = listOf(
    VendorStockItem(
      partId = "part_pad_front",
      vendorId = "vendor_bab_ezzouar",
      vendorName = "AutoPièces Bab Ezzouar",
      wilaya = "Alger (16)",
      priceDzd = 8500,
      stockQuantity = 18,
      brand = "OEM VW / Ferodo",
      phone = "0550 12 34 56"
    ),
    VendorStockItem(
      partId = "part_pad_front",
      vendorId = "vendor_oran_pieces",
      vendorName = "Sarl Maghreb Pièces Détachées",
      wilaya = "Oran (31)",
      priceDzd = 7900,
      stockQuantity = 12,
      brand = "Bosch Prime",
      phone = "0560 99 88 77"
    ),
    VendorStockItem(
      partId = "part_pad_front",
      vendorId = "vendor_setif_turbo",
      vendorName = "Sétif Auto Performance",
      wilaya = "Sétif (19)",
      priceDzd = 9200,
      stockQuantity = 6,
      brand = "Brembo Prime",
      phone = "0661 33 22 11"
    ),
    VendorStockItem(
      partId = "part_disc_front",
      vendorId = "vendor_bab_ezzouar",
      vendorName = "AutoPièces Bab Ezzouar",
      wilaya = "Alger (16)",
      priceDzd = 18500,
      stockQuantity = 8,
      brand = "Brembo Coated",
      phone = "0550 12 34 56"
    ),
    VendorStockItem(
      partId = "part_timing_chain",
      vendorId = "vendor_oran_pieces",
      vendorName = "Sarl Maghreb Pièces Détachées",
      wilaya = "Oran (31)",
      priceDzd = 32000,
      stockQuantity = 4,
      brand = "OEM Stellantis 8mm",
      phone = "0560 99 88 77"
    ),
    VendorStockItem(
      partId = "part_filter_oil",
      vendorId = "vendor_blida_moteur",
      vendorName = "Comptoir Central Blida",
      wilaya = "Blida (09)",
      priceDzd = 1900,
      stockQuantity = 45,
      brand = "Mann-Filter HU 7020 z",
      phone = "0770 45 67 89"
    )
  )

  // OBD Codes Dictionary for MekanikAI
  val obdFaults: List<OBDFault> = listOf(
    OBDFault(
      code = "P0299",
      standardTitle = "Pression de suralimentation du turbocompresseur - Trop faible",
      frenchDescription = "Le calculateur moteur a détecté une pression de turbo inférieure à la consigne demandée pendant plus de 5 secondes.",
      probableCauses = listOf(
        "Fuite d'air sur le circuit de suralimentation (durite percée ou échangeur fissuré)",
        "Électrovanne de régulation de pression N75 défaillante",
        "Géométrie variable du turbo grippée ou capsule à dépression (Wastegate) percée",
        "Capteur de pression absolue (MAP sensor) encrassé ou défectueux"
      ),
      symptoms = listOf(
        "Perte de puissance flagrante à l'accélération (mode dégradé / limp mode)",
        "Sifflement d'air anormal sous le capot",
        "Voyant moteur 'Check Engine' allumé au tableau de bord"
      ),
      diagnosticSteps = listOf(
        "1. Effectuer un test d'étanchéité sous pression du circuit d'air (fumigène ou 1.5 bar d'air comprimé).",
        "2. Contrôler la dépression à l'entrée de la capsule turbo (minimum -600 mbar au ralenti).",
        "3. Mesurer la résistance de l'électrovanne N75 au multimètre (valeur nominale : 15 à 20 Ohms).",
        "4. Enregistrer les paramètres en temps réel avec la valise : Pression de consigne vs Pression mesurée."
      ),
      concernedPartCategory = SystemCategory.MOTEUR,
      relatedParts = listOf("Électrovanne N75", "Durite de suralimentation", "Capteur MAP", "Actuateur Turbo Wastegate")
    ),
    OBDFault(
      code = "P0401",
      standardTitle = "Système EGR - Débit de recirculation des gaz insuffisant",
      frenchDescription = "Le flux de gaz d'échappement recyclés réinjecté dans l'admission est insuffisant par rapport à la cartographie moteur.",
      probableCauses = listOf(
        "Vanne EGR colmatée par la calamine",
        "Refroidisseur EGR (EGR Cooler) bouché",
        "Conduits d'admission encrassés",
        "Capteur de débitmètre massique (MAF) fournissant des valeurs erronées"
      ),
      symptoms = listOf(
        "À-coups moteur entre 1500 et 2200 tr/min",
        "Fumée noire à l'accélération",
        "Augmentation légère de la consommation de carburant"
      ),
      diagnosticSteps = listOf(
        "1. Démonter la vanne EGR et inspecter le niveau d'encrassement du clapet.",
        "2. Tester l'actionneur électrique/pneumatique de la vanne avec la fonction d'apprentissage valise.",
        "3. Contrôler la tension du capteur de position EGR (0.5V fermé à 4.5V pleine ouverture)."
      ),
      concernedPartCategory = SystemCategory.MOTEUR,
      relatedParts = listOf("Vanne EGR Électrique", "Joints d'étanchéité EGR", "Débitmètre d'air MAF")
    ),
    OBDFault(
      code = "P0300",
      standardTitle = "Ratés d'allumage détectés sur cylindres multiples / aléatoires",
      frenchDescription = "Le capteur PMH vilebrequin a mesuré des variations d'accélération angulaire correspondant à des combustions incomplètes.",
      probableCauses = listOf(
        "Bougies d'allumage ou de préchauffage usées",
        "Bobines d'allumage défaillantes (court-circuit interne)",
        "Injecteur encrassé ou débit irrégulier",
        "Prise d'air sur le collecteur d'admission",
        "Pression de rampe commune de carburant trop basse"
      ),
      symptoms = listOf(
        "Moteur qui tremble et tourne sur 3 cylindres au ralenti",
        "Odeur d'essence ou de gasoil imbrûlé à l'échappement",
        "Voyant moteur clignotant (signe de danger pour le pot catalytique)"
      ),
      diagnosticSteps = listOf(
        "1. Vérifier les valeurs de correction de débit des injecteurs (doivent être comprises entre -1.5 et +1.5 mg/coup).",
        "2. Tester les bobines d'allumage par inversion de cylindre pour observer si le code d'erreur suit la bobine.",
        "3. Contrôler les compressions des cylindres à chaud."
      ),
      concernedPartCategory = SystemCategory.MOTEUR,
      relatedParts = listOf("Bougies NGK / Bosch", "Bobines d'allumage", "Injecteur de carburant", "Filtre à carburant")
    ),
    OBDFault(
      code = "P2002",
      standardTitle = "Filtre à particules diesel (FAP) - Efficacité sous le seuil (Banque 1)",
      frenchDescription = "Le capteur de pression différentielle mesure un delta de pression anormal indiquant une saturation ou une dégradation du monolithe FAP.",
      probableCauses = listOf(
        "FAP saturé en suies suite à des trajets urbains répétés",
        "Tuyaux en silicone du capteur de pression différentielle percés ou fondus",
        "Niveau d'additif Cérine / AdBlue bas",
        "Bougie de préchauffage défectueuse empêchant les régénérations automatiques"
      ),
      symptoms = listOf(
        "Voyant FAP + Voyant Préchauffage clignotant",
        "Ventilateur moteur tournant à fond après coupure du contact",
        "Mode dégradé avec limitation à 3000 tr/min"
      ),
      diagnosticSteps = listOf(
        "1. Mesurer la masse de suies et la masse de cendres (Oil Ash Mass) dans le calculateur.",
        "2. Inspecter les tuyaux du capteur de pression différentielle G450.",
        "3. Lancer une régénération forcée stationnaire si la masse de suies est inférieure à 45g."
      ),
      concernedPartCategory = SystemCategory.MOTEUR,
      relatedParts = listOf("Capteur de pression différentielle FAP", "Injecteur AdBlue", "Nettoyant FAP Professionnel")
    )
  )

  // Sample Workshop Repair Orders (Espace Atelier)
  val sampleRepairOrders: List<RepairOrder> = listOf(
    RepairOrder(
      id = "or_001",
      orderNumber = "OR-2026-0842",
      clientName = "Karim Benali",
      clientPhone = "0550 88 77 66",
      vehicleModel = "VW Tiguan II 2.0 TDI 150ch (2021)",
      vin = "WVWZZZ5NZMW123456",
      plateNumber = "04291-121-16",
      mileageKm = 78400,
      date = "17 Août 2026",
      status = RepairStatus.EN_COURS,
      items = listOf(
        RepairItem("Jeu plaquettes avant Brembo", "5Q0 698 151 BR", 1, 8500),
        RepairItem("Disques avant 312mm High Carbon", "1K0 615 301 AA", 1, 18500),
        RepairItem("Filtre à huile Mann-Filter", "03N 115 562 B", 1, 1900),
        RepairItem("Huile Castrol Edge 5W30 LL 5L", "CAS-5W30-5L", 1, 9500)
      ),
      laborHours = 2.5f,
      laborRatePerHourDzd = 2000,
      discountDzd = 1000,
      notes = "Contrôle géométrie train avant et purge du liquide de frein DOT 4 requis.",
      isSigned = true,
      signatureName = "K. Benali"
    ),
    RepairOrder(
      id = "or_002",
      orderNumber = "OR-2026-0839",
      clientName = "Mourad Meziane",
      clientPhone = "0770 11 22 33",
      vehicleModel = "Peugeot 308 III 1.5 BlueHDi",
      vin = "VF3FPHNSSMY001234",
      plateNumber = "18204-122-31",
      mileageKm = 94000,
      date = "15 Août 2026",
      status = RepairStatus.PIECES_COMMANDEES,
      items = listOf(
        RepairItem("Kit distribution renforcée 8mm OEM", "16 381 573 80", 1, 32000),
        RepairItem("Pompe à eau + Liquide refroidissement", "16 230 981 80", 1, 14000),
        RepairItem("Courroie d'accessoires + Galet", "6PK1190", 1, 6500)
      ),
      laborHours = 4.0f,
      laborRatePerHourDzd = 2500,
      discountDzd = 0,
      notes = "Remplacement préventif de la chaîne 7mm par la chaîne 8mm avec nouveau carter.",
      isSigned = true,
      signatureName = "M. Meziane"
    ),
    RepairOrder(
      id = "or_003",
      orderNumber = "OR-2026-0830",
      clientName = "Amine Djouadi",
      clientPhone = "0661 44 55 66",
      vehicleModel = "Renault Clio V 1.5 Blue dCi",
      vin = "VF1RJA00067890123",
      plateNumber = "09312-120-09",
      mileageKm = 62000,
      date = "12 Août 2026",
      status = RepairStatus.TERMINE,
      items = listOf(
        RepairItem("Kit vidange complète 4 filtres", "RENAULT-KIT-4F", 1, 12500),
        RepairItem("Huile Elf Full-Tech FE 5W30 5L", "ELF-5W30-RN0720", 1, 8200)
      ),
      laborHours = 1.5f,
      laborRatePerHourDzd = 2000,
      discountDzd = 700,
      notes = "Révision des 60 000 km terminée avec remise à zéro de l'indicateur de maintenance.",
      isSigned = true,
      signatureName = "A. Djouadi"
    )
  )

  // Maintenance Alerts (Tableau de bord)
  val sampleAlerts: List<MaintenanceAlert> = listOf(
    MaintenanceAlert(
      id = "alt_1",
      title = "Plaquettes de frein avant à vérifier",
      vehicle = "VW Tiguan II [DFGA]",
      severity = AlertSeverity.URGENT,
      dueInfo = "Témoin d'usure calculé : 15% restant (Env. 1,200 km)",
      recommendedAction = "Commander référence 5Q0 698 151 BR et vérifier l'épaisseur des disques."
    ),
    MaintenanceAlert(
      id = "alt_2",
      title = "Échéance courroie de distribution & chaîne",
      vehicle = "Peugeot 308 III [DV5RC]",
      severity = AlertSeverity.PREVENTIF,
      dueInfo = "94,000 km / 5 ans",
      recommendedAction = "Installation impérative du kit chaîne renforcé 8mm OEM 16 381 573 80."
    ),
    MaintenanceAlert(
      id = "alt_3",
      title = "Régénération FAP préventive conseillée",
      vehicle = "Golf 7 2.0 TDI",
      severity = AlertSeverity.CONSEILLE,
      dueInfo = "Saturation suies estimée à 38g",
      recommendedAction = "Effectuer un parcours autoroutier de 25 min à 2500 tr/min ou régénération valise."
    )
  )

  // Pricing Plans (Algérie DZD)
  val pricingPlans: List<PricingPlan> = listOf(
    PricingPlan(
      id = "plan_particulier",
      title = "Particulier / Standard",
      subtitle = "Recherche VIN & Entretien Véhicules Personnels",
      priceDzd = "13 000 DA",
      billingPeriod = "par an",
      targetUser = "Particuliers & Passionnés",
      features = listOf(
        "Limite de 15 recherches VIN complètes / jour",
        "Extension possible de +5 VIN pour 500 DA",
        "Accès au catalogue de pièces & équivalences",
        "Carnet d'entretien numérique & rappels",
        "Recherche de garages et vendeurs certifiés",
        "Rapport historique VIN téléchargeable"
      ),
      isPopular = false
    ),
    PricingPlan(
      id = "plan_pro_atelier",
      title = "Compte Atelier Pro",
      subtitle = "La solution complète pour garages et mécaniciens",
      priceDzd = "15 000 DA",
      billingPeriod = "par an",
      targetUser = "Garages & Mécaniciens",
      features = listOf(
        "Recherches VIN & Codes Moteur illimitées",
        "Schémas techniques éclatés haute précision (ETKA)",
        "Gestion des ordres de réparation & Devis pro en DZD",
        "Historique clients, véhicules et réparations",
        "Signature numérique des devis sur tablette/mobile",
        "Assistant Diagnostic MekanikAI & OBD-II illimité",
        "Gestion multi-postes (jusqu'à 3 techniciens)"
      ),
      isPopular = true,
      badgeText = "RECOMMANDÉ ATELIER"
    ),
    PricingPlan(
      id = "plan_ultra_pro",
      title = "Ultra Pro Multi-Marques",
      subtitle = "Pour grands centres auto et concessionnaires",
      priceDzd = "18 000 DA",
      billingPeriod = "par an",
      targetUser = "Centres Auto & Flottes",
      features = listOf(
        "Tous les avantages du pack Pro Atelier",
        "Accès documentation technique constructeurs OEM",
        "Gestion multi-postes et multi-utilisateurs illimitée",
        "Export comptable des devis et factures",
        "Support technique prioritaire par téléphone 6j/7",
        "Accès exclusif pièces Véhicules Électriques (EV)"
      ),
      isPopular = false
    ),
    PricingPlan(
      id = "plan_vendeur",
      title = "Espace Vendeur Pièces",
      subtitle = "Visibilité maximale auprès des garages algériens",
      priceDzd = "1 700 DA",
      billingPeriod = "par mois",
      targetUser = "Magasins & Grossistes de Pièces",
      features = listOf(
        "Publication du stock et références OEM en ligne",
        "Mise en avant dans les résultats de recherche pièces",
        "Badge Vendeur Vérifié avec Numéro de Registre (RC)",
        "Réception directe des demandes de devis garages",
        "Option Vendeur Premium : 20 000 DA/an",
        "Option Vendeur Peinture : 5 000 DA/an",
        "Option Vendeur Pneumatiques : 6 000 DA/an"
      ),
      isPopular = false
    )
  )

  // Algerian 58 Wilayas List
  val wilayasList: List<String> = listOf(
    "Toutes les Wilayas", "01 - Adrar", "02 - Chlef", "03 - Laghouat", "04 - Oum El Bouaghi",
    "05 - Batna", "06 - Béjaïa", "07 - Biskra", "08 - Béchar", "09 - Blida",
    "10 - Bouira", "11 - Tamanrasset", "12 - Tébessa", "13 - Tlemcen", "14 - Tiaret",
    "15 - Tizi Ouzou", "16 - Alger", "17 - Djelfa", "18 - Jijel", "19 - Sétif",
    "20 - Saïda", "21 - Skikda", "22 - Sidi Bel Abbès", "23 - Annaba", "24 - Guelma",
    "25 - Constantine", "26 - Médéa", "27 - Mostaganem", "28 - M'Sila", "29 - Mascara",
    "30 - Ouargla", "31 - Oran", "32 - El Bayadh", "33 - Illizi", "34 - Bordj Bou Arreridj",
    "35 - Boumerdès", "36 - El Tarf", "37 - Tindouf", "38 - Tissemsilt", "39 - El Oued",
    "40 - Khenchela", "41 - Souk Ahras", "42 - Tipaza", "43 - Mila", "44 - Aïn Defla",
    "45 - Naâma", "46 - Aïn Témouchent", "47 - Ghardaïa", "48 - Relizane",
    "49 - Timimoun", "50 - Bordj Badji Mokhtar", "51 - Ouled Djellal", "52 - Béni Abbès",
    "53 - In Salah", "54 - In Guezzam", "55 - Touggourt", "56 - Djanet", "57 - El M'Ghair", "58 - El Meniaa"
  )

  fun findVehicleByVin(vinQuery: String): Vehicle? {
    val clean = vinQuery.trim().uppercase()
    if (clean.isEmpty()) return null
    return sampleVehicles.find { it.vin.equals(clean, ignoreCase = true) }
      ?: sampleVehicles.find { clean.contains(it.brand.uppercase()) || clean.contains(it.model.uppercase()) }
      ?: sampleVehicles.firstOrNull()
  }

  fun findVehicleByEngineCode(codeQuery: String): List<Vehicle> {
    val clean = codeQuery.trim().uppercase()
    if (clean.isEmpty()) return sampleVehicles
    return sampleVehicles.filter {
      it.engineCode.contains(clean, ignoreCase = true) || it.displacement.contains(clean, ignoreCase = true)
    }
  }

  fun findObdFault(code: String): OBDFault? {
    val clean = code.trim().uppercase()
    return obdFaults.find { it.code.equals(clean, ignoreCase = true) }
  }

  // --- Workshop Appointments Data ---
  val sampleAppointments: List<WorkshopAppointment> = listOf(
    WorkshopAppointment(
      id = "apt_01",
      clientName = "Yacine Benali",
      clientPhone = "0554 18 29 40",
      vehicleModel = "Volkswagen Tiguan II 2.0 TDI (2021)",
      plateNumber = "04291-121-16",
      serviceType = ServiceType.SYSTEME_FREINAGE,
      appointmentDate = "18 Août 2026",
      timeSlot = "09:00 - 10:30",
      workshopName = "Garage Auto Performance Alger",
      status = AppointmentStatus.CONFIRME,
      estimatedDurationHours = 1.5f,
      estimatedCostDzd = 8500,
      assignedMechanic = "Mohamed K.",
      clientNotes = "Bruit métallique au freinage avant droit. Contrôler disques.",
      createdAt = "17 Août 2026, 14:10"
    ),
    WorkshopAppointment(
      id = "apt_02",
      clientName = "Amine Djelloul",
      clientPhone = "0661 45 88 12",
      vehicleModel = "Renault Clio V 1.5 dCi (2022)",
      plateNumber = "08712-122-16",
      serviceType = ServiceType.VIDANGE_FILTRES,
      appointmentDate = "18 Août 2026",
      timeSlot = "11:00 - 12:00",
      workshopName = "Garage Auto Performance Alger",
      status = AppointmentStatus.EN_ATTENTE,
      estimatedDurationHours = 1.0f,
      estimatedCostDzd = 6500,
      assignedMechanic = "Karim T.",
      clientNotes = "Vidange 10 000 km avec huile 5W30 RN0720 + filtre gasoil.",
      createdAt = "17 Août 2026, 15:30"
    ),
    WorkshopAppointment(
      id = "apt_03",
      clientName = "Redouane Khelifi",
      clientPhone = "0770 99 33 21",
      vehicleModel = "Peugeot 308 II 1.5 BlueHDi (2020)",
      plateNumber = "19234-120-09",
      serviceType = ServiceType.DIAGNOSTIC_OBD,
      appointmentDate = "19 Août 2026",
      timeSlot = "14:00 - 15:00",
      workshopName = "Garage Auto Performance Alger",
      status = AppointmentStatus.CONFIRME,
      estimatedDurationHours = 1.0f,
      estimatedCostDzd = 3500,
      assignedMechanic = "Mohamed K.",
      clientNotes = "Voyant moteur allumé après plein de carburant. Code P2002 supposé.",
      createdAt = "17 Août 2026, 11:20"
    ),
    WorkshopAppointment(
      id = "apt_04",
      clientName = "Sofiane Belkacem",
      clientPhone = "0560 31 14 78",
      vehicleModel = "Dacia Duster II 1.5 dCi 4x4 (2023)",
      plateNumber = "00543-123-35",
      serviceType = ServiceType.COURROIE_DISTRIBUTION,
      appointmentDate = "20 Août 2026",
      timeSlot = "08:30 - 12:30",
      workshopName = "Garage Auto Performance Alger",
      status = AppointmentStatus.EN_ATTENTE,
      estimatedDurationHours = 4.0f,
      estimatedCostDzd = 28000,
      assignedMechanic = "Farid B.",
      clientNotes = "Remplacement kit distribution complet + pompe à eau + purge LDR Glaceol Type D.",
      createdAt = "17 Août 2026, 16:05"
    ),
    WorkshopAppointment(
      id = "apt_05",
      clientName = "Mustapha Brahimi",
      clientPhone = "0550 72 44 91",
      vehicleModel = "Hyundai Tucson 2.0 CRDi HTRAC (2022)",
      plateNumber = "02198-122-31",
      serviceType = ServiceType.CLIMATISATION,
      appointmentDate = "17 Août 2026",
      timeSlot = "16:00 - 17:00",
      workshopName = "Garage Auto Performance Alger",
      status = AppointmentStatus.TERMINE,
      estimatedDurationHours = 1.0f,
      estimatedCostDzd = 6000,
      assignedMechanic = "Karim T.",
      clientNotes = "Recharge fluide R134a + changement filtre habitacle antibactérien.",
      createdAt = "16 Août 2026, 10:00"
    )
  )

  // --- Technical Documentation & Repair Manuals ---
  val technicalProcedures: List<RepairManualProcedure> = listOf(
    RepairManualProcedure(
      id = "proc_vag_ea288_timing",
      title = "Calage & Remplacement Kit Distribution Moteur 2.0 TDI EA288 (DFGA / CRLB)",
      systemCategory = SystemCategory.DISTRIBUTION,
      vehicleCompatibility = "VW Tiguan II, Golf VII/VIII, Passat B8, Seat Ateca, Audi A3 (2016-2025)",
      estimatedTimeHours = 3.5f,
      difficultyLevel = "Expert Atelier",
      requiredTools = listOf(
        "Pige de calage vilebrequin T10490 / T10492",
        "Pige de calage pompe haute pression & AAC T10050 / T20102",
        "Clé dynamométrique 10-100 Nm",
        "Outil de tension galet tendeur T10264"
      ),
      stepByStepInstructions = listOf(
        "1. Déposer le carter sous moteur et la roue avant droite ainsi que le pare-boue.",
        "2. Déposer la courroie d'accessoires et le support moteur droit avec calage sous carter d'huile.",
        "3. Tourner le vilebrequin dans le sens horaire jusqu'au Point Mort Haut (PMH) cylindre 1.",
        "4. Insérer la pige T10490 sur le pignon de vilebrequin et les piges T10050 sur l'AAC et la pompe HP.",
        "5. Desserrer l'écrou du galet tendeur et détendre la courroie crantée.",
        "6. Déposer la pompe à eau (3 vis torx M6 - couple 15 Nm) et nettoyer la portée du joint torique.",
        "7. Monter la nouvelle pompe à eau d'origine avec joint pré-lubrifié au liquide G12evo.",
        "8. Poser la nouvelle courroie crantée en commençant par le vilebrequin, galet enrouleur, AAC, pompe HP et galet tendeur.",
        "9. Tendre le galet jusqu'à alignement de l'index de réglage avec l'encoche et serrer l'écrou à 20 Nm + 45°.",
        "10. Retirer les piges, effectuer 2 tours complets de vilebrequin à la main et revérifier l'alignement exact des piges."
      ),
      warningNote = "Ne jamais forcer sur le galet tendeur au-delà de la butée. Remplacer impérativement les 3 vis du support moteur (serrage 40 Nm + 90°).",
      torqueSpecs = listOf(
        "Écrou galet tendeur : 20 Nm + 45°",
        "Vis galets enrouleurs : 50 Nm",
        "Vis de pompe à eau (x3) : 15 Nm",
        "Vis de poulie damper vilebrequin (x4) : 10 Nm + 90°"
      )
    ),
    RepairManualProcedure(
      id = "proc_k9k_renault_egr",
      title = "Nettoyage & Remplacement Vanne EGR Basse Pression Moteur 1.5 dCi K9K",
      systemCategory = SystemCategory.MOTEUR,
      vehicleCompatibility = "Renault Clio IV/V, Megane IV, Dacia Duster / Sandero II/III",
      estimatedTimeHours = 2.0f,
      difficultyLevel = "Intermédiaire",
      requiredTools = listOf(
        "Jeu d'embouts Torx femelles E8, E10, E12",
        "Pince pour colliers à oreilles clic-R",
        "Valise diagnostic (Can Clip / Mekanik Diagnostic) pour réinitialisation des auto-adaptatifs"
      ),
      stepByStepInstructions = listOf(
        "1. Débrancher la borne négative de la batterie 12V.",
        "2. Déposer le conduit d'admission d'air et le filtre à air.",
        "3. Déconnecter la prise électrique étanche de la vanne EGR et de la sonde de température.",
        "4. Déposer les 2 vis de fixation du tube échangeur thermique EGR.",
        "5. Desserrer les 3 vis de maintien du corps de vanne et extraire l'ensemble.",
        "6. Nettoyer le boîtier papillon et le conduit au spray nettoyant carbone professionnel.",
        "7. Remplacer les joints métalliques neufs enduits d'un léger film d'huile.",
        "8. Remonter et serrer les vis de fixation en croix à 24 Nm.",
        "9. Effectuer l'apprentissage de la butée mini/maxi de la vanne EGR à la valise diagnostic."
      ),
      warningNote = "Une vanne EGR neuve non réinitialisée provoquera le code défaut DF053 ou P0401 au premier démarrage.",
      torqueSpecs = listOf(
        "Vis de fixation boîtier EGR : 24 Nm",
        "Raccords circuit de refroidissement EGR : 12 Nm",
        "Colliers durite air : 5.5 Nm"
      )
    ),
    RepairManualProcedure(
      id = "proc_brake_pads_mqb",
      title = "Procédure Remplacement Freins Arrière avec Frein de Stationnement Électrique (EPB)",
      systemCategory = SystemCategory.FREINAGE,
      vehicleCompatibility = "Plateforme MQB (VW Tiguan, Golf VII, Audi A3, Seat Leon, Skoda Octavia)",
      estimatedTimeHours = 1.0f,
      difficultyLevel = "Intermédiaire",
      requiredTools = listOf(
        "Valise de diagnostic (Mise en position maintenance des étriers électriques)",
        "Clés plates 13mm et 15mm extra-plates",
        "Repousse-piston droit sans rotation",
        "Graisse céramique haute température"
      ),
      stepByStepInstructions = listOf(
        "1. Connecter l'outil diagnostic OBD et lancer la fonction 'Mode Maintenance / Remplacement Plaquettes Frein à Main Électrique'.",
        "2. Attendre le retrait complet des moteurs électriques de recul (bruit de vis sans fin 5 secondes).",
        "3. Déposer les deux vis de colonnettes d'étrier (clé 13mm maintenue par clé 15mm).",
        "4. Repousser le piston d'étrier droit dans son logement en surveillant le niveau de liquide de frein dans le bocal.",
        "5. Nettoyer les portées d'étrier avec une brosse métallique douce et appliquer de la graisse céramique sur les cales antibruit.",
        "6. Monter les plaquettes neuves avec témoins d'usure.",
        "7. Reposer l'étrier avec 2 vis de colonnettes neuves pré-enduites de frein filet (serrage 35 Nm).",
        "8. Pomper plusieurs fois sur la pédale de frein pour mettre les plaquettes en contact avec le disque.",
        "9. Quitter le mode maintenance à la valise pour calibrer la course du moteur électrique de frein à main."
      ),
      warningNote = "Ne jamais forcer sur le piston arrière sans avoir préalablement rétracté le servomoteur électrique par diagnostic.",
      torqueSpecs = listOf(
        "Vis de colonnette étrier arrière : 35 Nm (vis neuves)",
        "Vis de support d'étrier arrière (torx) : 90 Nm + 90°",
        "Vis de roue alliage : 140 Nm"
      )
    )
  )

  // --- Electrical Wiring Diagrams & Pinouts ---
  val wiringDiagrams: List<WiringDiagramDoc> = listOf(
    WiringDiagramDoc(
      id = "diag_vag_ecu_edc17",
      title = "Schéma de Câblage Calculateur Moteur Bosch EDC17C64 / EDC17C74 (2.0 TDI)",
      circuitName = "Gestion Moteur & Réseau CAN-Bus Propulsion",
      ecuPins = "Connecteur A (94 Voies) : Pin 1, 2, 4 (Masse puissance) | Pin 3, 5, 6 (+12V permanent après relais principal). Connecteur B (60 Voies) : Pin 37 (CAN High 2.5-3.5V), Pin 38 (CAN Low 1.5-2.5V).",
      fuseBoxLocation = "Boîte à fusibles compartiment moteur (E-Box) — Fusible SB10 (15A) & Relais Principal R1",
      relayCodes = "Relais R1 (Alimentation borne 30 / 87), Relais R4 (Pompe de gavage pré-gavage)",
      wireColorCodes = listOf(
        "Noir/Bleu : +12V après contact",
        "Marron : Masse châssis directe",
        "Orange/Noir : CAN Bus High (500 kbps)",
        "Orange/Marron : CAN Bus Low (500 kbps)",
        "Jaune/Rouge : Signal commande injecteur piézo"
      ),
      schemaDiagramRef = "VAG_SCHEMA_EDC17_CAN_2026_REV3"
    ),
    WiringDiagramDoc(
      id = "diag_renault_bsi_uch",
      title = "Brochage Unité Centrale Habitacle (UCH / BCM) Renault Clio V / Captur II",
      circuitName = "Alimentations, Verrouillage centralisé & Passerelle Gateway",
      ecuPins = "Connecteur Noir 40 Voies : Voie 12 (+BAT), Voie 24 (+APC), Voie 18/19 (Bus CAN Habitacle 250 kbps), Voie 33 (Ligne LIN Capteur Pluie/Luminosité).",
      fuseBoxLocation = "Habitacle sous planche de bord gauche — Fusibles F12 (10A UCH), F22 (20A Lève-vitres)",
      relayCodes = "Relais intégré PCB UCH pour éclairage diurne et coupure consommateurs",
      wireColorCodes = listOf(
        "Rouge : +12V permanent batterie",
        "Jaune : +12V après contact (APC)",
        "Noir : Masse de référence",
        "Blanc/Vert : Ligne diagnostic K-Line / CAN-D"
      ),
      schemaDiagramRef = "RENAULT_UCH_CLIO5_ELEC_REV4"
    )
  )

  // --- Torque Specifications (Couples de serrage constructeur) ---
  val torqueSpecs: List<TorqueSpecification> = listOf(
    TorqueSpecification("Vis de culasse Moteur 2.0 TDI EA288 (x10)", "Pass 1: 40 Nm, Pass 2: 70 Nm, Pass 3: +90°, Pass 4: +90°", "+180° total", "Vis neuves lubrifiées obligatoires"),
    TorqueSpecification("Bride de fixation injecteur Common Rail", "8 Nm + 180°", "+180°", "Vis et rondelle cuivre neuves"),
    TorqueSpecification("Poulie Damper de vilebrequin (Vis centrale)", "180 Nm + 180°", "+180°", "Vis neuve obligatoire"),
    TorqueSpecification("Chapeau de bielle (Vis à rupture élastique)", "30 Nm + 90°", "+90°", "Remplacement systématique des vis"),
    TorqueSpecification("Bouchon de vidange carter d'huile acier", "30 Nm", null, "Joint cuivre / écrasement neuf"),
    TorqueSpecification("Bouchon de vidange carter plastique VAG", "4 Nm (encliquetage cran)", null, "Bouchon plastique neuf avec joint"),
    TorqueSpecification("Étrier de frein avant (Vis de guidage colonnettes)", "35 Nm", null, "Vis neuves avec frein filet"),
    TorqueSpecification("Support d'étrier de frein avant sur pivot", "200 Nm", null, "Contrôler état des filets"),
    TorqueSpecification("Écrou de moyeu de roue avant (Vis centrale 12 pans)", "70 Nm + 90°", "+90°", "Serrer véhicule sur ses roues")
  )

  // --- Fluid Specifications & Capacities ---
  val fluidSpecs: List<FluidSpecification> = listOf(
    FluidSpecification(
      fluidType = "Huile Moteur",
      capacityLiters = "4.7 Litres (avec remplacement filtre à huile)",
      oemNorm = "VW 507.00 / 504.00 (Compatible FAP / DPF)",
      recommendedGrade = "SAE 5W-30 LongLife ou 0W-30",
      serviceInterval = "15 000 km ou 1 an (Algérie / Climat sévère)"
    ),
    FluidSpecification(
      fluidType = "Liquide de Refroidissement",
      capacityLiters = "8.2 Litres (Circuit complet avec chauffage)",
      oemNorm = "G12evo (Norme TL-774-L) ou G13",
      recommendedGrade = "Prêt à l'emploi -35°C (Rose / Violet)",
      serviceInterval = "Contrôle tous les 30 000 km / Remplacement 5 ans"
    ),
    FluidSpecification(
      fluidType = "Huile de Boîte Automatique DSG7 (DQ381)",
      capacityLiters = "6.0 Litres (Vidange ~5.5L)",
      oemNorm = "VW G 055 529 A2 / G 052 182",
      recommendedGrade = "Fluide spécial double embrayage humide (DCT/DSG)",
      serviceInterval = "60 000 km avec filtre externe"
    ),
    FluidSpecification(
      fluidType = "Liquide de Frein",
      capacityLiters = "1.0 Litre (Purge complète circuit 4 roues + bloc ABS/ESP)",
      oemNorm = "FMVSS 116 DOT 4 Class 6 (Basse Viscosité ESP)",
      recommendedGrade = "DOT 4 LV (Point d'ébullition sec > 260°C)",
      serviceInterval = "Tous les 2 ans"
    ),
    FluidSpecification(
      fluidType = "Fluide Frigorigène Climatisation",
      capacityLiters = "500g ± 15g (Fluide) + 110 ml Huile PAG 46",
      oemNorm = "R1234yf (ou R134a selon homologation usine)",
      recommendedGrade = "Gaz HFO-1234yf ou R134a",
      serviceInterval = "Contrôle pression et efficacité tous les 2 ans"
    )
  )

  // --- Industry Technical Database Sources & Standards ---
  val industryDataSources: List<IndustryDocSource> = listOf(
    IndustryDocSource(
      name = "TecRMI (TecAlliance)",
      providerType = "Standard Européen Multimarque",
      apiStandard = "TecRMI WebService REST / JSON & SOAP",
      description = "Fournit les temps barémés officiels constructeurs, manuels de réparation, guides de révision, données de géométrie et couples de serrage pour plus de 10 000 modèles.",
      coverage = "Couverture 99% du parc automobile mondial",
      integrationStatus = "Intégration Directe API / Fiches Mekanik+"
    ),
    IndustryDocSource(
      name = "HaynesPro WorkshopData",
      providerType = "Données Techniques & Schémas Électriques",
      apiStandard = "HaynesPro SmartFIX / SmartCASE APIs",
      description = "Schémas électriques interactifs en couleur, bulletins de service technique (TSB), localisation des composants, boîtes à fusibles et guides de diagnostic pas à pas.",
      coverage = "VL & VUL Européens, Asiatiques et Américains",
      integrationStatus = "Connecteur Natif Prêt"
    ),
    IndustryDocSource(
      name = "Autodata Connect",
      providerType = "Documentation Mécanique & Diagnostic",
      apiStandard = "Autodata REST API Webhooks",
      description = "Programmes d'entretien constructeur, courroies et chaînes de distribution, calculateurs, codes défauts OBD spécifiques et guide de dépannage rapide.",
      coverage = "175 fabricants et plus de 34 000 modèles",
      integrationStatus = "Compatible Passerelle Mekanik+"
    ),
    IndustryDocSource(
      name = "Portails Constructeurs OEM Directs (erWin, Service Box, Renault Infotech)",
      providerType = "Données Officielles Fabricants",
      apiStandard = "Accès Euro 5/6 PassThru J2534 & ODIS/Clip",
      description = "Accès direct aux schémas usine de premier niveau, rappels constructeurs officiels, numéros d'organes exacts par VIN et fiches de mise à jour calculateurs (Flash).",
      coverage = "VAG, Stellantis, Renault Group, Toyota, Hyundai-Kia",
      integrationStatus = "Passerelle VIN Certifiée Mekanik+"
    )
  )

  // --- Push Notifications & Maintenance Alerts ---
  val sampleNotifications: List<AppNotification> = listOf(
    AppNotification(
      id = "notif_01",
      title = "🔧 Rappel Entretien : Contrôle Niveau d'Huile & Filtres",
      message = "Votre Volkswagen Tiguan (WVWZZZ5NZMW123456) approche des 10 000 km depuis la dernière révision. Pensez à vérifier le niveau de la jauge d'huile (Norme VW 507.00).",
      category = NotificationCategory.ENTRETIEN_RAPPEL,
      timestamp = "Il y a 15 min",
      isRead = false,
      vehicleVin = "WVWZZZ5NZMW123456",
      priority = NotificationPriority.HIGH
    ),
    AppNotification(
      id = "notif_02",
      title = "📅 Rendez-vous Atelier Confirmé !",
      message = "Votre rendez-vous pour 'Freinage (Disques & Plaquettes)' le 18 Août 2026 à 09h00 au Garage Auto Performance Alger a été validé par l'équipe.",
      category = NotificationCategory.RDV_ATELIER,
      timestamp = "Il y a 1h",
      isRead = false,
      priority = NotificationPriority.HIGH
    ),
    AppNotification(
      id = "notif_03",
      title = "📦 Pièce Disponible en Stock à Alger",
      message = "Le 'Jeu de plaquettes avant Brembo (Réf 5Q0 698 151 BR)' que vous recherchiez est désormais disponible chez Sarl Auto Pièces Bab Ezzouar (Prix : 8 500 DA).",
      category = NotificationCategory.PIECE_DISPONIBLE,
      timestamp = "Il y a 3h",
      isRead = true,
      priority = NotificationPriority.NORMAL
    ),
    AppNotification(
      id = "notif_04",
      title = "⚠️ Alerte Température : Contrôle Liquide de Refroidissement (LDR)",
      message = "Avec les fortes chaleurs estivales, vérifiez le bocal d'expansion moteur à froid. Niveau prescrit entre MIN et MAX avec liquide G12evo ou Type D.",
      category = NotificationCategory.ENTRETIEN_RAPPEL,
      timestamp = "Hier à 10:20",
      isRead = true,
      priority = NotificationPriority.NORMAL
    ),
    AppNotification(
      id = "notif_05",
      title = "🧠 Nouveau Diagnostic MekanikAI Généré",
      message = "L'analyse du code défaut P0299 (Pression de suralimentation turbo) pour le moteur 2.0 TDI DFGA est disponible avec les valeurs de consigne et le schéma de dépannage.",
      category = NotificationCategory.MEKANIK_AI_ALERTE,
      timestamp = "Hier à 16:45",
      isRead = true,
      priority = NotificationPriority.NORMAL
    )
  )
}

