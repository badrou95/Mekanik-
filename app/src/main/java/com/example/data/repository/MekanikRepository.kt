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

  // Exploded Schemas and Parts Database (ETKA / Partslink24 Multi-System Visual Part Finder)
  val tiguanBrakeSchema = ExplodedSchema(
    system = SystemCategory.FREINAGE,
    title = "Planche 615-010 • Freinage Avant TRW/Lucas 312x25mm (PR-1ZE)",
    subTitle = "Schéma Technique Éclaté (ETKA / Partslink24 • Repères [1..6])",
    diagramType = "etka_brake_front",
    hotspots = listOf(
      ExplodedHotspot(1, "Jeu de 4 plaquettes de frein avant avec témoin", 48f, 32f, "part_pad_front"),
      ExplodedHotspot(2, "Disque de frein ventilé 312x25mm (5 trous)", 22f, 45f, "part_disc_front"),
      ExplodedHotspot(3, "Étrier de frein flottant gauche (Rouge/Alu)", 75f, 28f, "part_caliper_left"),
      ExplodedHotspot(4, "Support d'étrier de frein (Chape TRW)", 65f, 60f, "part_caliper_carrier"),
      ExplodedHotspot(5, "Flexible de frein haute pression renforcé", 82f, 15f, "part_brake_hose"),
      ExplodedHotspot(6, "Kit accessoires & ressorts antibruit", 38f, 72f, "part_hardware_kit")
    )
  )

  val explodedSchemasByCategory: Map<SystemCategory, ExplodedSchema> = mapOf(
    SystemCategory.FREINAGE to tiguanBrakeSchema,
    SystemCategory.FILTRATION to ExplodedSchema(
      system = SystemCategory.FILTRATION,
      title = "Planche 115-040 • Boîtier Filtration Huile, Air, Gasoil & Habitacle",
      subTitle = "Vue Éclatée Circuit de Filtration & Échangeur (ETKA / Partslink24)",
      diagramType = "etka_filtration",
      hotspots = listOf(
        ExplodedHotspot(1, "Cartouche filtre à huile moteur haute filtration", 26f, 42f, "part_filter_oil"),
        ExplodedHotspot(2, "Élément filtre à air moteur haute capacité", 68f, 26f, "part_filter_air"),
        ExplodedHotspot(3, "Cartouche filtre à carburant avec séparateur d'eau", 46f, 66f, "part_filter_fuel"),
        ExplodedHotspot(4, "Filtre d'habitacle charbon actif anti-allergène", 80f, 58f, "part_filter_cabin")
      )
    ),
    SystemCategory.MOTEUR to ExplodedSchema(
      system = SystemCategory.MOTEUR,
      title = "Planche 145-080 • Suralimentation Turbo VNT, Électrovanne N75 & Admission",
      subTitle = "Vue Éclatée Groupe Motopropulseur & Suralimentation (ETKA / Partslink24)",
      diagramType = "etka_engine_turbo",
      hotspots = listOf(
        ExplodedHotspot(1, "Turbocompresseur complet à géométrie variable VNT", 34f, 40f, "part_turbo_core"),
        ExplodedHotspot(2, "Électrovanne de régulation pression turbo N75", 64f, 22f, "part_turbo_n75"),
        ExplodedHotspot(3, "Durite haute pression sortie échangeur Intercooler", 76f, 58f, "part_boost_hose"),
        ExplodedHotspot(4, "Vanne EGR électrique & refroidisseur des gaz", 22f, 68f, "part_egr_valve")
      )
    ),
    SystemCategory.DISTRIBUTION to ExplodedSchema(
      system = SystemCategory.DISTRIBUTION,
      title = "Planche 109-020 • Cinématique Distribution, Chaîne 8mm & Galets Tendeurs",
      subTitle = "Vue Éclatée Synchronisation Arbres à Cames & Vilebrequin (ETKA / Partslink24)",
      diagramType = "etka_timing_belt",
      hotspots = listOf(
        ExplodedHotspot(1, "Kit chaîne d'arbres à cames renforcée 8mm + Tendeur", 42f, 28f, "part_timing_chain"),
        ExplodedHotspot(2, "Kit courroie de distribution primaire & galets INA", 56f, 62f, "part_timing_belt_kit"),
        ExplodedHotspot(3, "Poulie damper de vilebrequin filtrée", 24f, 70f, "part_crank_pulley")
      )
    ),
    SystemCategory.EMBRAYAGE to ExplodedSchema(
      system = SystemCategory.EMBRAYAGE,
      title = "Planche 141-030 • Volant Moteur Bimasse & Double Embrayage DSG / 7DCT300",
      subTitle = "Vue Éclatée Transmission & Cloche d'Embrayage (ETKA / Partslink24)",
      diagramType = "etka_clutch_gearbox",
      hotspots = listOf(
        ExplodedHotspot(1, "Volant moteur bimasse (DMF) + Kit double embrayage", 34f, 46f, "part_clutch_dmf"),
        ExplodedHotspot(2, "Butée hydraulique concentrique CSC / Fourchette", 68f, 38f, "part_clutch_csc"),
        ExplodedHotspot(3, "Kit vidange & cartouche filtre boîte DSG / 7DCT300", 74f, 68f, "part_dct_filter_kit")
      )
    ),
    SystemCategory.SUSPENSION to ExplodedSchema(
      system = SystemCategory.SUSPENSION,
      title = "Planche 407-015 • Train Avant MacPherson, Triangle & Biellette",
      subTitle = "Vue Éclatée Liaison au Sol & Géométrie Essieu Avant (ETKA / Partslink24)",
      diagramType = "etka_suspension",
      hotspots = listOf(
        ExplodedHotspot(1, "Jambe de force / Amortisseur avant gaz bitube", 46f, 26f, "part_shock_front"),
        ExplodedHotspot(2, "Triangle de suspension inférieur avec rotule & silentblocs", 32f, 68f, "part_control_arm"),
        ExplodedHotspot(3, "Biellette de barre stabilisatrice renforcée", 72f, 44f, "part_sway_link")
      )
    ),
    SystemCategory.REFROIDISSEMENT to ExplodedSchema(
      system = SystemCategory.REFROIDISSEMENT,
      title = "Planche 121-050 • Pompe à Eau Débrayable, Thermostat & Radiateur",
      subTitle = "Vue Éclatée Boucle Thermique Moteur (ETKA / Partslink24)",
      diagramType = "etka_cooling",
      hotspots = listOf(
        ExplodedHotspot(1, "Pompe à eau débrayable & boîtier thermostatique", 40f, 46f, "part_water_pump"),
        ExplodedHotspot(2, "Radiateur principal de refroidissement en aluminium", 74f, 36f, "part_radiator_main")
      )
    ),
    SystemCategory.ELECTRICITE to ExplodedSchema(
      system = SystemCategory.ELECTRICITE,
      title = "Planche 903-010 • Faisceaux Électriques ECU, Alternateur LIN-Bus & Capteurs",
      subTitle = "Vue Éclatée Câblages Moteur, Charge & Démarrage 12V/48V (ETKA / Partslink24)",
      diagramType = "etka_electrical",
      hotspots = listOf(
        ExplodedHotspot(1, "Alternateur 180A avec poulie roue libre LIN-Bus", 36f, 40f, "part_alternator_180a"),
        ExplodedHotspot(2, "Capteur de pression absolue tubulure admission (MAP)", 72f, 56f, "part_map_sensor"),
        ExplodedHotspot(3, "Faisceau de câblage électrique moteur & connecteurs injecteurs ECU", 54f, 24f, "part_wiring_harness_ecu")
      )
    ),
    SystemCategory.VEHICULE_ELECTRIQUE to ExplodedSchema(
      system = SystemCategory.VEHICULE_ELECTRIQUE,
      title = "Planche 965-090 • Boucle Thermique Batterie HT & Pompe Inverter EV",
      subTitle = "Vue Éclatée Haute Tension & Gestion Thermique EV/PHEV (ETKA / Partslink24)",
      diagramType = "etka_ev_system",
      hotspots = listOf(
        ExplodedHotspot(1, "Pompe à eau auxiliaire 12V refroidissement Inverter EV", 48f, 44f, "part_ev_inverter_pump")
      )
    ),
    SystemCategory.CARROSSERIE to ExplodedSchema(
      system = SystemCategory.CARROSSERIE,
      title = "Planche 941-020 • Optique Matrix LED IQ.Light, Bouclier & Radar",
      subTitle = "Vue Éclatée Face Avant & Éclairage Intelligent (ETKA / Partslink24)",
      diagramType = "etka_body_lighting",
      hotspots = listOf(
        ExplodedHotspot(1, "Projecteur avant Full LED Matrix avec module AFS", 44f, 42f, "part_headlamp_led")
      )
    )
  )

  fun getSchemaForCategory(category: SystemCategory): ExplodedSchema {
    return explodedSchemasByCategory[category] ?: tiguanBrakeSchema
  }

  val sampleParts: List<PartItem> = listOf(
    PartItem(
      id = "part_wiring_harness_ecu",
      system = SystemCategory.ELECTRICITE,
      name = "Faisceau de câblage électrique moteur & rampe injecteurs ECU",
      position = "Compartiment Moteur • Calculateur ECU T94 vers Rampe & Capteurs",
      oemReference = "04L 971 600 L",
      oemAlternatives = listOf("04L 972 623 C", "9802448680", "240114892R"),
      crossReferences = listOf(
        CrossReference("Leoni OEM", "L-971600-VAG", "Faisceau Première Monte Blindé"),
        CrossReference("Metzger", "2324109", "Connecteurs Étanches IP6K9K"),
        CrossReference("Herth+Buss Elparts", "51277340", "Haute Température Silicone"),
        CrossReference("Delphi", "WH-2048-TDI", "Qualité Constructeur")
      ),
      schemaCalloutNumber = 3,
      yearCompatibility = "Golf 7 / Tiguan / Octavia / Leon 2.0 TDI & équivalents PSA/Renault",
      compatibilityBadge = "Câblage Certifié Continuité CAN-Bus & ECU",
      priceEstimatedDzd = 18500,
      description = "Faisceau électrique complet pré-câblé avec connecteurs étanches pour injecteurs, capteur pression rail, sonde température LDR et prise calculateur.",
      technicalSpecs = mapOf(
        "Nombre de voies ECU" to "94 broches + 4 prises injecteurs",
        "Isolation thermique" to "-40°C à +150°C (Gaine annelée PA66)",
        "Étanchéité connecteurs" to "Norme IP6K9K joints silicone"
      )
    ),
    PartItem(
      id = "part_pad_front",
      system = SystemCategory.FREINAGE,
      name = "Jeu de plaquettes de frein avant",
      position = "Essieu Avant (Gauche & Droit)",
      oemReference = "5Q0 698 151 BR",
      oemAlternatives = listOf("5Q0 698 151 AP", "T15-3501080BA", "4048006400"),
      crossReferences = listOf(
        CrossReference("Brembo", "P 85 126", "Gamme Prime Performance"),
        CrossReference("Bosch", "0 986 494 660", "Qualité Première Monte OEM"),
        CrossReference("Ferodo", "FDB4454", "Eco-Friction Sans Cuivre"),
        CrossReference("ATE", "13.0460-7294.2", "Ceramic Faible Poussière"),
        CrossReference("TRW", "GDB1956", "Cotec Friction Coating"),
        CrossReference("Valeo", "601332", "First Range")
      ),
      schemaCalloutNumber = 1,
      yearCompatibility = "Tiguan 2016-2020 (AP) / Facelift 2021-2025 (BR) / Jetour Dashing",
      compatibilityBadge = "100% Vérifié Châssis MQB Evo & T1X",
      priceEstimatedDzd = 8500,
      description = "Plaquettes de frein avant haute endurance adaptées au système TRW avec capteur de témoin d'usure électronique intégré. Conforme norme ECE R90.",
      technicalSpecs = mapOf(
        "Épaisseur" to "20.3 mm",
        "Largeur" to "160.2 mm",
        "Hauteur" to "64.5 mm",
        "Système de freinage" to "Lucas / TRW (PR-1ZE)",
        "Témoin d'usure" to "Inclus (Longueur câble 165mm)"
      )
    ),
    PartItem(
      id = "part_disc_front",
      system = SystemCategory.FREINAGE,
      name = "Paire de disques de frein ventilés 312mm",
      position = "Essieu Avant",
      oemReference = "1K0 615 301 AA",
      oemAlternatives = listOf("5Q0 615 301 F", "3C0 615 301 C", "T15-3501075"),
      crossReferences = listOf(
        CrossReference("Brembo", "09.9772.11", "Peint anti-corrosion UV"),
        CrossReference("Bosch", "0 986 479 932", "High Carbon ventilé"),
        CrossReference("ATE", "24.0125-0158.1", "PowerDisc rainuré"),
        CrossReference("Zimmermann", "600.3233.20", "Sport Perforé Coat Z")
      ),
      schemaCalloutNumber = 2,
      yearCompatibility = "Tous modèles Tiguan II & Golf 7/8 2.0 TDI (PR-1ZE)",
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
      id = "part_caliper_carrier",
      system = SystemCategory.FREINAGE,
      name = "Support d'étrier de frein (Chape avec colonnettes)",
      position = "Essieu Avant",
      oemReference = "5Q0 615 125 B",
      oemAlternatives = listOf("3C0 615 125 A"),
      crossReferences = listOf(
        CrossReference("TRW", "BDA1182", "Avec colonnettes graissées"),
        CrossReference("ATE", "11.0230-0812.1", "Fonte nodulaire traitée")
      ),
      schemaCalloutNumber = 4,
      yearCompatibility = "2016 - 2025",
      compatibilityBadge = "100% Compatible PR-1ZE",
      priceEstimatedDzd = 11500,
      description = "Support d'étrier complet avec colonnettes de guidage et soufflets EPDM.",
      technicalSpecs = mapOf("Couple serrage porte-fusée" to "200 Nm", "Couple colonnettes" to "35 Nm")
    ),
    PartItem(
      id = "part_brake_hose",
      system = SystemCategory.FREINAGE,
      name = "Flexible de frein haute pression renforcé",
      position = "Essieu Avant (Gauche/Droit)",
      oemReference = "5Q0 611 701 E",
      oemAlternatives = listOf("5Q0 611 701 C"),
      crossReferences = listOf(
        CrossReference("ATE", "24.5134-0468.3", "Tressage interne Kevlar"),
        CrossReference("Brembo", "T 85 162", "Norme DOT FMVSS 106"),
        CrossReference("TRW", "PHD1184", "Raccord Banjo M10x1")
      ),
      schemaCalloutNumber = 5,
      yearCompatibility = "2016 - 2025",
      compatibilityBadge = "Haute Pression DOT 4 LV",
      priceEstimatedDzd = 3400,
      description = "Flexible hydraulique multi-couches résistant à la dilatation sous freinage d'urgence ABS.",
      technicalSpecs = mapOf("Longueur" to "468 mm", "Filetage" to "M10x1.0")
    ),
    PartItem(
      id = "part_hardware_kit",
      system = SystemCategory.FREINAGE,
      name = "Kit accessoires & ressorts antibruit plaquettes",
      position = "Essieu Avant",
      oemReference = "8V0 698 459",
      oemAlternatives = listOf("5Q0 698 459 A"),
      crossReferences = listOf(
        CrossReference("ATE", "13.0460-0276.2", "Acier inoxydable ressort"),
        CrossReference("TRW", "PFK1092", "Kit complet 4 roues")
      ),
      schemaCalloutNumber = 6,
      yearCompatibility = "2016 - 2025",
      compatibilityBadge = "Indispensable au montage",
      priceEstimatedDzd = 2200,
      description = "Agrafes de maintien en inox et vis autofreinées neuves pour colonnettes d'étrier.",
      technicalSpecs = mapOf("Contenu" to "4 agrafes inox + 4 vis M8x22", "Graisse" to "Sachet céramique inclus")
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
      id = "part_timing_belt_kit",
      system = SystemCategory.DISTRIBUTION,
      name = "Kit courroie de distribution primaire & galets tendeurs",
      position = "Face Distribution Moteur",
      oemReference = "04L 198 119 D",
      oemAlternatives = listOf("04L 109 119 J"),
      crossReferences = listOf(
        CrossReference("Continental", "CT1168K1", "Courroie PTFE Haute Résistance"),
        CrossReference("INA", "530 0650 10", "Galet tendeur Litens origine"),
        CrossReference("Gates", "K015678XS", "PowerGrip HSN")
      ),
      schemaCalloutNumber = 2,
      yearCompatibility = "2016 - 2025 (2.0 TDI EA288 / 1.5 BlueHDi)",
      compatibilityBadge = "Intervalle 180 000 km / 120 000 km",
      priceEstimatedDzd = 24500,
      description = "Kit complet de synchronisation vilebrequin/arbre à cames avec galet tendeur automatique et galets enrouleurs.",
      technicalSpecs = mapOf("Nombre de dents" to "145 dents", "Largeur" to "25.0 mm")
    ),
    PartItem(
      id = "part_crank_pulley",
      system = SystemCategory.DISTRIBUTION,
      name = "Poulie damper de vilebrequin filtrée",
      position = "Nez de Vilebrequin",
      oemReference = "04L 105 243 B",
      oemAlternatives = listOf("98 084 115 80"),
      crossReferences = listOf(
        CrossReference("Corteco", "80004891", "Amortisseur de vibrations EPDM"),
        CrossReference("INA", "544 0128 10", "Avec visserie neuve")
      ),
      schemaCalloutNumber = 3,
      yearCompatibility = "2016 - 2025",
      compatibilityBadge = "Équilibrage Dynamique OE",
      priceEstimatedDzd = 16800,
      description = "Poulie de vilebrequin à moyeu élastomère absorbant les acyclismes moteur et protégeant la courroie d'accessoires.",
      technicalSpecs = mapOf("Nombre de gorges" to "6PK", "Diamètre" to "153 mm")
    ),
    PartItem(
      id = "part_filter_oil",
      system = SystemCategory.FILTRATION,
      name = "Filtre à huile moteur haute filtration",
      position = "Carter Filtre Moteur",
      oemReference = "03N 115 562 B",
      oemAlternatives = listOf("03N 115 466", "F4J16-1012010", "1056025900"),
      crossReferences = listOf(
        CrossReference("Mann-Filter", "HU 7020 z", "Qualité Origine OE"),
        CrossReference("Purflux", "L980", "Plissage chevrons"),
        CrossReference("Mahle", "OX 787 D", "EcoFilter"),
        CrossReference("Bosch", "F 026 407 157", "Filtration micro-particules")
      ),
      schemaCalloutNumber = 1,
      yearCompatibility = "Moteurs 2.0 TDI EA288 / 1.6 TGDI / 1.5 TD (2016-2025)",
      compatibilityBadge = "100% Origine Constructeur",
      priceEstimatedDzd = 1900,
      description = "Cartouche filtrante avec joint torique en élastomère résistant aux huiles de synthèse 0W-20 et 5W-30.",
      technicalSpecs = mapOf("Hauteur" to "100 mm", "Diamètre extérieur" to "65 mm", "Couple cloche" to "25 Nm")
    ),
    PartItem(
      id = "part_filter_air",
      system = SystemCategory.FILTRATION,
      name = "Élément filtre à air moteur haute capacité Dust-Pro",
      position = "Boîte à Air Admission",
      oemReference = "5Q0 129 620 B",
      oemAlternatives = listOf("T15-1109111", "2032003500"),
      crossReferences = listOf(
        CrossReference("Mann-Filter", "C 30 005", "Avec pré-filtre mousse Zones Poussiéreuses"),
        CrossReference("Mahle", "LX 3502", "Média cellulose ignifugé"),
        CrossReference("Bosch", "S0324", "Étanchéité polyuréthane")
      ),
      schemaCalloutNumber = 2,
      yearCompatibility = "2016 - 2025",
      compatibilityBadge = "Spécial Climat Chaud & Poussière",
      priceEstimatedDzd = 2400,
      description = "Filtre à air moteur doté d'une nappe inférieure en mousse synthétique retenant le sable fin et protégeant le débitmètre MAF.",
      technicalSpecs = mapOf("Longueur" to "292 mm", "Largeur" to "177 mm", "Hauteur" to "70 mm")
    ),
    PartItem(
      id = "part_filter_fuel",
      system = SystemCategory.FILTRATION,
      name = "Cartouche filtre à carburant avec séparateur d'eau",
      position = "Compartiment Moteur",
      oemReference = "5Q0 127 177",
      oemAlternatives = listOf("5Q0 127 177 C", "98 097 579 80"),
      crossReferences = listOf(
        CrossReference("Mann-Filter", "PU 8028", "Séparation d'eau > 95%"),
        CrossReference("Purflux", "C866", "Protection injecteurs Common-Rail"),
        CrossReference("Delphi", "HDF682", "Filtration 3 microns")
      ),
      schemaCalloutNumber = 3,
      yearCompatibility = "2016 - 2025",
      compatibilityBadge = "Protection Haute Pression 2000 bars",
      priceEstimatedDzd = 3600,
      description = "Cartouche multicouche hydrophobe protégeant la pompe HP4/CP4 et les injecteurs piézo contre l'eau dans le gasoil.",
      technicalSpecs = mapOf("Seuil de filtration" to "3 µm", "Hauteur" to "136 mm")
    ),
    PartItem(
      id = "part_filter_cabin",
      system = SystemCategory.FILTRATION,
      name = "Filtre d'habitacle charbon actif & polyphénol",
      position = "Sous boîte à gants",
      oemReference = "5Q0 819 653",
      oemAlternatives = listOf("5Q0 819 669", "T15-8107910"),
      crossReferences = listOf(
        CrossReference("Mann-Filter", "FP 26 009", "FreciousPlus Anti-Allergène PM2.5"),
        CrossReference("Valeo", "715741", "ClimFilter Supreme"),
        CrossReference("Bosch", "0 986 628 515", "FILTER+pro")
      ),
      schemaCalloutNumber = 4,
      yearCompatibility = "2016 - 2025",
      compatibilityBadge = "Filtration PM2.5 & Gaz",
      priceEstimatedDzd = 2600,
      description = "Filtre d'habitacle 3 couches neutralisant les odeurs d'échappement, les particules fines PM2.5 et les pollens.",
      technicalSpecs = mapOf("Longueur" to "254 mm", "Largeur" to "235 mm", "Épaisseur" to "34 mm")
    ),
    PartItem(
      id = "part_turbo_core",
      system = SystemCategory.MOTEUR,
      name = "Turbocompresseur complet à géométrie variable VNT",
      position = "Collecteur d'échappement",
      oemReference = "04L 253 010 B",
      oemAlternatives = listOf("04L 253 010 T", "F4J16-1118010"),
      crossReferences = listOf(
        CrossReference("Garrett", "812971-5006S", "Première Monte Neuve"),
        CrossReference("BorgWarner", "BV40-0082", "Avec actuateur électronique calibré"),
        CrossReference("Mahle", "030 TC 11002 000", "Équilibré à 180 000 tr/min")
      ),
      schemaCalloutNumber = 1,
      yearCompatibility = "2016 - 2025",
      compatibilityBadge = "Actuateur SREA pré-calibré",
      priceEstimatedDzd = 89000,
      description = "Turbocompresseur complet assemblé avec collecteur et actuateur de géométrie variable à retour de position.",
      technicalSpecs = mapOf("Pression max" to "1.65 bar", "Huile requise" to "0W-20 / 5W-30 norme OEM")
    ),
    PartItem(
      id = "part_turbo_n75",
      system = SystemCategory.MOTEUR,
      name = "Électrovanne de régulation pression turbo N75",
      position = "Tablier Moteur",
      oemReference = "1K0 906 627 B",
      oemAlternatives = listOf("1J0 906 627 B"),
      crossReferences = listOf(
        CrossReference("Pierburg", "7.00868.02.0", "Fournisseur Officiel OE"),
        CrossReference("Bosch", "0 928 400 712", "Vanne pneumatique proportionnelle")
      ),
      schemaCalloutNumber = 2,
      yearCompatibility = "2014 - 2025",
      compatibilityBadge = "Correctif Code Défaut P0299",
      priceEstimatedDzd = 6800,
      description = "Convertisseur de pression électropneumatique pilotant la capsule de géométrie variable du turbo.",
      technicalSpecs = mapOf("Résistance bobine" to "15 - 20 Ohms", "Tension" to "12V PWM")
    ),
    PartItem(
      id = "part_boost_hose",
      system = SystemCategory.MOTEUR,
      name = "Durite haute pression sortie échangeur Intercooler",
      position = "Circuit Air Suralimenté",
      oemReference = "5Q0 145 838 C",
      oemAlternatives = listOf("5Q0 145 838 A"),
      crossReferences = listOf(
        CrossReference("Gates", "09-0842", "Silicone fluoré FKM anti-huile"),
        CrossReference("Metzger", "2400412", "Avec joints Viton et circlip")
      ),
      schemaCalloutNumber = 3,
      yearCompatibility = "2016 - 2025",
      compatibilityBadge = "Joints Viton Inclus",
      priceEstimatedDzd = 8900,
      description = "Conduite d'air de suralimentation renforcée fibres aramides empêchant les fuites de pression turbo.",
      technicalSpecs = mapOf("Diamètre intérieur" to "62 mm", "Raccord" to "Henn rapide avec agrafe")
    ),
    PartItem(
      id = "part_egr_valve",
      system = SystemCategory.MOTEUR,
      name = "Vanne EGR électrique & module refroidisseur",
      position = "Arrière Bloc Moteur",
      oemReference = "04L 131 512 BH",
      oemAlternatives = listOf("04L 131 512 BQ"),
      crossReferences = listOf(
        CrossReference("Wahler / BorgWarner", "710339D", "Première Monte OE"),
        CrossReference("Valeo", "700468", "Joints métalliques inclus")
      ),
      schemaCalloutNumber = 4,
      yearCompatibility = "2016 - 2025",
      compatibilityBadge = "Apprentissage de base OBD requis",
      priceEstimatedDzd = 42000,
      description = "Module complet de recirculation des gaz d'échappement avec clapet by-pass et échangeur inox.",
      technicalSpecs = mapOf("Pilotage" to "Servomoteur 5 broches", "Refroidissement" to "Liquide G12evo")
    ),
    PartItem(
      id = "part_clutch_dmf",
      system = SystemCategory.EMBRAYAGE,
      name = "Volant moteur bimasse & Kit double embrayage DSG/7DCT",
      position = "Cloche Boîte de Vitesses",
      oemReference = "03L 105 266 DC",
      oemAlternatives = listOf("025CHA-1601100", "30205-7DCT300"),
      crossReferences = listOf(
        CrossReference("LuK", "600 0370 00", "RepSet DMF Complet"),
        CrossReference("Sachs", "2295 000 541", "Première Monte ZF/Magna")
      ),
      schemaCalloutNumber = 1,
      yearCompatibility = "2016 - 2025 (DSG7 DQ381 / Magna 7DCT300)",
      compatibilityBadge = "Calibrage Kiss-Point Requis",
      priceEstimatedDzd = 74500,
      description = "Volant moteur à double masse amortie éliminant les claquements au ralenti et protégeant les arbres primaires K1/K2.",
      technicalSpecs = mapOf("Nombre de dents" to "129", "Jeu libre max" to "3 dents")
    ),
    PartItem(
      id = "part_clutch_csc",
      system = SystemCategory.EMBRAYAGE,
      name = "Butée hydraulique concentrique CSC / Actionneur",
      position = "Intérieur Cloche Boîte",
      oemReference = "0A5 141 671 N",
      oemAlternatives = listOf("02E 325 025 AD"),
      crossReferences = listOf(
        CrossReference("LuK", "510 0177 10", "Corps Aluminium Haute Pression"),
        CrossReference("Valeo", "810044", "FTE Automotive OE")
      ),
      schemaCalloutNumber = 2,
      yearCompatibility = "2016 - 2025",
      compatibilityBadge = "Purge DOT 4 Obligatoire",
      priceEstimatedDzd = 14200,
      description = "Récepteur concentrique hydraulique assurant un débrayage progressif et sans fuite.",
      technicalSpecs = mapOf("Fluide" to "DOT 4 LV", "Raccord" to "Clip rapide avec joint torique")
    ),
    PartItem(
      id = "part_dct_filter_kit",
      system = SystemCategory.EMBRAYAGE,
      name = "Kit vidange 6L + Cartouche filtre boîte DSG / 7DCT300",
      position = "Boîte Automatique Double Embrayage",
      oemReference = "0GC 325 183 A",
      oemAlternatives = listOf("025CHA-1504100", "G 055 529 A2"),
      crossReferences = listOf(
        CrossReference("Meyle", "100 135 0114", "Kit complet Huile + Filtre + Bouchon"),
        CrossReference("Mann-Filter", "H 6013 z", "Cartouche haute pression mécatronique")
      ),
      schemaCalloutNumber = 3,
      yearCompatibility = "2018 - 2025 (DSG7 DQ381 / Magna 7DCT300)",
      compatibilityBadge = "Échéance stricte 60 000 km",
      priceEstimatedDzd = 26500,
      description = "Kit complet de maintenance préventive pour boîte à double embrayage humide (huile synthétique + filtre haute pression).",
      technicalSpecs = mapOf("Capacité" to "5.2L à 6.0L", "Température niveau" to "35°C - 45°C à la valise")
    ),
    PartItem(
      id = "part_shock_front",
      system = SystemCategory.SUSPENSION,
      name = "Amortisseur avant gaz bitube (Jambe de force)",
      position = "Essieu Avant (Gauche/Droit)",
      oemReference = "5QF 413 031 AS",
      oemAlternatives = listOf("T15-2905010"),
      crossReferences = listOf(
        CrossReference("Sachs", "317 577", "Première Monte VAG"),
        CrossReference("Bilstein", "22-266804", "Gamme B4 OE Replacement"),
        CrossReference("Monroe", "G8441", "Oespectrum")
      ),
      schemaCalloutNumber = 1,
      yearCompatibility = "2016 - 2025",
      compatibilityBadge = "Diamètre jambe 55mm",
      priceEstimatedDzd = 14800,
      description = "Amortisseur pressurisé au gaz azote garantissant une tenue de route optimale sur chaussée dégradée.",
      technicalSpecs = mapOf("Diamètre tube" to "55 mm", "Type" to "Pression de gaz bitube")
    ),
    PartItem(
      id = "part_control_arm",
      system = SystemCategory.SUSPENSION,
      name = "Triangle de suspension inférieur complet avec rotule",
      position = "Essieu Avant Inférieur",
      oemReference = "5Q0 407 151 R",
      oemAlternatives = listOf("5Q0 407 152 R"),
      crossReferences = listOf(
        CrossReference("Lemförder", "38391 01", "Première Monte ZF"),
        CrossReference("TRW", "JTC2290", "Avec silentbloc arrière hydraulique")
      ),
      schemaCalloutNumber = 2,
      yearCompatibility = "2016 - 2025",
      compatibilityBadge = "Silentbloc Renforcé Inclus",
      priceEstimatedDzd = 16900,
      description = "Bras de liaison transversal complet prêt à poser avec silentbloc arrière renforcé.",
      technicalSpecs = mapOf("Matière" to "Acier embouti haute limite élastique", "Cône rotule" to "18.5 mm")
    ),
    PartItem(
      id = "part_sway_link",
      system = SystemCategory.SUSPENSION,
      name = "Biellette de barre stabilisatrice renforcée HD",
      position = "Essieu Avant",
      oemReference = "5Q0 411 315 B",
      oemAlternatives = listOf("1K0 411 315 R"),
      crossReferences = listOf(
        CrossReference("Meyle HD", "116 060 0000/HD", "Rotules majorées 22mm Garantie 4 ans"),
        CrossReference("Lemförder", "26774 01", "Qualité Origine")
      ),
      schemaCalloutNumber = 3,
      yearCompatibility = "2014 - 2025",
      compatibilityBadge = "Anti-Claquement Train Avant",
      priceEstimatedDzd = 3800,
      description = "Biellette d'accouplement de barre anti-roulis supprimant les bruits de claquement sur ralentisseurs.",
      technicalSpecs = mapOf("Longueur" to "335 mm", "Filetage" to "M12x1.5")
    ),
    PartItem(
      id = "part_water_pump",
      system = SystemCategory.REFROIDISSEMENT,
      name = "Pompe à eau débrayable & boîtier thermostatique",
      position = "Bloc Moteur",
      oemReference = "04L 121 011 N",
      oemAlternatives = listOf("04L 121 011 P", "06L 121 111 M"),
      crossReferences = listOf(
        CrossReference("Pierburg", "7.07152.12.0", "Version modifiée sans grippage tiroir"),
        CrossReference("Valeo", "506995", "Turbine métallique"),
        CrossReference("SKF", "VKPC 81339", "Joint EPDM haute température")
      ),
      schemaCalloutNumber = 1,
      yearCompatibility = "2016 - 2025",
      compatibilityBadge = "Turbine Modifiée Anti-Surchauffe",
      priceEstimatedDzd = 19200,
      description = "Pompe de liquide de refroidissement à régulation électrique pour montée en température rapide.",
      technicalSpecs = mapOf("Liquide recommandé" to "G12evo (-35°C)", "Purge" to "Sous vide ou cycle OBD")
    ),
    PartItem(
      id = "part_radiator_main",
      system = SystemCategory.REFROIDISSEMENT,
      name = "Radiateur principal de refroidissement en aluminium",
      position = "Face Avant",
      oemReference = "5Q0 121 251 ER",
      oemAlternatives = listOf("5Q0 121 251 GN"),
      crossReferences = listOf(
        CrossReference("Nissens", "65308", "Faisceau haute densité Brasé"),
        CrossReference("Mahle Behr", "CR 1142 000P", "Première Monte OE")
      ),
      schemaCalloutNumber = 2,
      yearCompatibility = "2016 - 2025",
      compatibilityBadge = "Spécial Climats Chauds (Algérie)",
      priceEstimatedDzd = 27400,
      description = "Radiateur moteur à faisceau aluminium haute dissipation thermique pour conduites par fortes chaleurs.",
      technicalSpecs = mapOf("Dimensions faisceau" to "650 x 415 x 26 mm")
    ),
    PartItem(
      id = "part_alternator_180a",
      system = SystemCategory.ELECTRICITE,
      name = "Alternateur 180A avec poulie roue libre LIN-Bus",
      position = "Support Accessoires",
      oemReference = "04L 903 023 L",
      oemAlternatives = listOf("04L 903 024 K"),
      crossReferences = listOf(
        CrossReference("Valeo", "440618", "100% Neuf Origine"),
        CrossReference("Bosch", "0 125 811 082", "Régulateur intelligent Start&Stop")
      ),
      schemaCalloutNumber = 1,
      yearCompatibility = "2016 - 2025",
      compatibilityBadge = "Compatible Start & Stop AGM/EFB",
      priceEstimatedDzd = 38500,
      description = "Alternateur haute puissance piloté par bus LIN pour véhicules équipés de gestion d'énergie et Start&Stop.",
      technicalSpecs = mapOf("Courant de charge" to "180 A", "Tension" to "14.4V régulée LIN")
    ),
    PartItem(
      id = "part_map_sensor",
      system = SystemCategory.ELECTRICITE,
      name = "Capteur de pression absolue tubulure d'admission (MAP)",
      position = "Collecteur d'Admission",
      oemReference = "03K 906 051",
      oemAlternatives = listOf("04L 906 051 G"),
      crossReferences = listOf(
        CrossReference("Bosch", "0 281 006 059", "Capteur Pression + Température NTC"),
        CrossReference("Hella", "6PP 009 400-861", "4 broches")
      ),
      schemaCalloutNumber = 2,
      yearCompatibility = "2015 - 2025",
      compatibilityBadge = "Plage 0.5 - 4.0 Bars",
      priceEstimatedDzd = 5400,
      description = "Capteur combiné mesurant la pression de suralimentation réelle et la température d'air admis.",
      technicalSpecs = mapOf("Broches" to "4 pins", "Plage de mesure" to "50 à 400 kPa")
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
      yearCompatibility = "Véhicules Hybrides & Électriques VAG / MEB / BYD DM-i (2020-2025)",
      compatibilityBadge = "Véhicule Électrique / Hybride",
      priceEstimatedDzd = 28500,
      description = "Pompe de circulation électrique sans balais pour le refroidissement du pack batterie haute tension et de l'électronique de puissance.",
      technicalSpecs = mapOf("Tension" to "12V", "Consommation" to "1.5A", "Débit" to "1200 L/h")
    ),
    PartItem(
      id = "part_headlamp_led",
      system = SystemCategory.CARROSSERIE,
      name = "Projecteur avant Full LED Matrix IQ.Light avec module AFS",
      position = "Avant Gauche",
      oemReference = "5NA 941 081 F",
      oemAlternatives = listOf("T18-3711010BA"),
      crossReferences = listOf(
        CrossReference("Hella", "1EX 015 112-211", "Optique Origine OE"),
        CrossReference("Valeo", "047412", "Full LED Directionnel")
      ),
      schemaCalloutNumber = 1,
      yearCompatibility = "2020 - 2025",
      compatibilityBadge = "Codage ODIS / UDS Requis",
      priceEstimatedDzd = 94000,
      description = "Bloc optique matriciel haute définition avec feux de jour signature LED et clignotant défilant.",
      technicalSpecs = mapOf("Technologie" to "Matrix LED 24 segments", "Module ballast" to "Inclus sous l'optique")
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
      plainLanguageExplanation = "Le moteur manque d'air sous pression : le turbo ne souffle pas assez fort. Vous ressentez un gros manque de reprise ou une voiture molle (mode sécurité), souvent causé par une durite d'air fendue qui siffle ou une petite vanne de régulation bloquée.",
      probableCauses = listOf(
        "Fuite d'air sur le circuit de suralimentation (durite percée ou échangeur intercooler fissuré)",
        "Électrovanne de régulation de pression N75 défaillante",
        "Géométrie variable du turbo grippée par la calamine ou capsule Wastegate percée",
        "Capteur de pression absolue (MAP sensor) encrassé ou défectueux"
      ),
      symptoms = listOf(
        "Perte de puissance flagrante à l'accélération (mode dégradé / limp mode)",
        "Sifflement d'air anormal sous le capot à l'accélération",
        "Voyant moteur 'Check Engine' allumé au tableau de bord"
      ),
      diagnosticSteps = listOf(
        "1. Effectuer un test d'étanchéité sous pression du circuit d'air (fumigène ou 1.5 bar d'air comprimé).",
        "2. Contrôler la dépression à l'entrée de la capsule turbo (minimum -600 mbar au ralenti).",
        "3. Mesurer la résistance de l'électrovanne N75 au multimètre (valeur nominale : 15 à 20 Ohms).",
        "4. Enregistrer les paramètres en temps réel avec la valise : Pression de consigne vs Pression mesurée."
      ),
      concernedPartCategory = SystemCategory.MOTEUR,
      relatedParts = listOf("Électrovanne N75", "Durite de suralimentation", "Capteur MAP", "Actuateur Turbo Wastegate"),
      detailedParts = listOf(
        DiagnosticRecommendedPart(
          partId = "part_turbo_n75",
          name = "Électrovanne de régulation turbo N75",
          oemReference = "1K0 906 627 B",
          brandName = "Pierburg OEM Original",
          equivalentBrands = listOf("Pierburg", "Bosch", "Febi"),
          systemCategory = SystemCategory.MOTEUR,
          priceEstimatedDzd = 6800,
          availabilityBadge = "En stock chez 12 vendeurs partenaires",
          urgencyLevel = "Remplacement Fortement Recommandé"
        ),
        DiagnosticRecommendedPart(
          partId = "part_boost_hose",
          name = "Durite de suralimentation échangeur renforcée",
          oemReference = "5Q0 145 838 C",
          brandName = "Gates / Bugiad Turbo Hose",
          equivalentBrands = listOf("Gates", "Metzger", "Sasic"),
          systemCategory = SystemCategory.MOTEUR,
          priceEstimatedDzd = 8900,
          availabilityBadge = "En stock immédiat",
          urgencyLevel = "Contrôle étanchéité prioritaire"
        ),
        DiagnosticRecommendedPart(
          partId = "part_map_sensor",
          name = "Capteur de pression absolue tubulure d'admission (MAP)",
          oemReference = "03K 906 051",
          brandName = "Bosch Direct Injection",
          equivalentBrands = listOf("Bosch", "Hella", "Facet"),
          systemCategory = SystemCategory.ELECTRICITE,
          priceEstimatedDzd = 5400,
          availabilityBadge = "En stock (Alger, Oran, Sétif)",
          urgencyLevel = "Nettoyage ou Remplacement"
        )
      ),
      multimeterSpecs = "Électrovanne N75 : 15.5 Ω à 20°C | Capteur MAP : Alimentation 5.0V, Signal 1.8V au ralenti",
      estimatedSeverity = "Moyenne à Élevée (Mode dégradé)",
      estimatedRepairTimeHours = 1.5f
    ),
    OBDFault(
      code = "P0401",
      standardTitle = "Système EGR - Débit de recirculation des gaz insuffisant",
      frenchDescription = "Le flux de gaz d'échappement recyclés réinjecté dans l'admission est insuffisant par rapport à la cartographie moteur.",
      plainLanguageExplanation = "La vanne de recyclage des gaz (EGR) est bouchée par la suie et la calamine noire. Le moteur n'arrive plus à réinjecter une partie des gaz d'échappement pour dépolluer, ce qui provoque des à-coups entre 1500 et 2000 tr/min et de la fumée noire.",
      probableCauses = listOf(
        "Vanne EGR colmatée par la calamine",
        "Refroidisseur EGR (EGR Cooler) bouché",
        "Conduits d'admission d'air fortement encrassés",
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
      relatedParts = listOf("Vanne EGR Électrique", "Joints d'étanchéité EGR", "Débitmètre d'air MAF"),
      detailedParts = listOf(
        DiagnosticRecommendedPart(
          partId = "part_egr_valve",
          name = "Vanne EGR électrique avec refroidisseur intégré",
          oemReference = "04L 131 512 D",
          brandName = "Valeo / BorgWarner OEM",
          equivalentBrands = listOf("Valeo", "Pierburg", "Continental"),
          systemCategory = SystemCategory.MOTEUR,
          priceEstimatedDzd = 38000,
          availabilityBadge = "En stock chez les grossistes VAG",
          urgencyLevel = "Remplacement ou Décalaminage"
        ),
        DiagnosticRecommendedPart(
          partId = "part_egr_gaskets",
          name = "Kit joints d'étanchéité métalliques EGR",
          oemReference = "04L 131 547 S",
          brandName = "Elring Klinger",
          equivalentBrands = listOf("Elring", "Reinz"),
          systemCategory = SystemCategory.MOTEUR,
          priceEstimatedDzd = 2200,
          availabilityBadge = "En stock immédiat",
          urgencyLevel = "Obligatoire au remontage"
        )
      ),
      multimeterSpecs = "Position EGR fermée : 0.75V | Pleine ouverture : 4.25V | Moteur papillon : 3.8 Ω",
      estimatedSeverity = "Moyenne (Risque encrassement FAP)",
      estimatedRepairTimeHours = 2.0f
    ),
    OBDFault(
      code = "P0300",
      standardTitle = "Ratés d'allumage détectés sur cylindres multiples / aléatoires",
      frenchDescription = "Le capteur PMH vilebrequin a mesuré des variations d'accélération angulaire correspondant à des combustions incomplètes.",
      plainLanguageExplanation = "Un ou plusieurs cylindres ne brûlent pas correctement le carburant. Le moteur boîte, broute ou tourne 'sur 3 pattes' en tremblant au ralenti. Si le voyant moteur clignote, coupez le moteur pour ne pas faire fondre le catalyseur.",
      probableCauses = listOf(
        "Bougies d'allumage ou de préchauffage usées ou charbonnées",
        "Bobines d'allumage défaillantes (court-circuit interne)",
        "Injecteur encrassé, grippé ou débit de retour irrégulier",
        "Prise d'air sur le collecteur d'admission",
        "Pression de carburant trop basse dans la rampe"
      ),
      symptoms = listOf(
        "Moteur qui tremble et tourne sur 3 cylindres au ralenti",
        "Odeur d'essence ou de gasoil imbrûlé à l'échappement",
        "Voyant moteur clignotant (signe d'alerte pour le pot catalytique)"
      ),
      diagnosticSteps = listOf(
        "1. Vérifier les valeurs de correction de débit des injecteurs (doivent être comprises entre -1.5 et +1.5 mg/coup).",
        "2. Tester les bobines d'allumage par inversion de cylindre pour observer si le code d'erreur suit la bobine.",
        "3. Contrôler les compressions des cylindres à chaud."
      ),
      concernedPartCategory = SystemCategory.MOTEUR,
      relatedParts = listOf("Bougies NGK / Bosch", "Bobines d'allumage", "Injecteur de carburant", "Filtre à carburant"),
      detailedParts = listOf(
        DiagnosticRecommendedPart(
          partId = "part_spark_plugs",
          name = "Jeu de 4 bougies d'allumage Laser Iridium",
          oemReference = "04E 905 601 B",
          brandName = "NGK Japan / Bosch Double Platinum",
          equivalentBrands = listOf("NGK", "Bosch", "Denso", "Beru"),
          systemCategory = SystemCategory.ELECTRICITE,
          priceEstimatedDzd = 9600,
          availabilityBadge = "En stock immédiat",
          urgencyLevel = "Remplacement Prioritaire"
        ),
        DiagnosticRecommendedPart(
          partId = "part_ignition_coil",
          name = "Bobine d'allumage crayon haute tension",
          oemReference = "04C 905 110 L",
          brandName = "Eldor / Beru OEM",
          equivalentBrands = listOf("Eldor", "Beru", "Bosch", "Delphi"),
          systemCategory = SystemCategory.ELECTRICITE,
          priceEstimatedDzd = 7500,
          availabilityBadge = "En stock chez 18 vendeurs",
          urgencyLevel = "Remplacement Recommandé"
        )
      ),
      multimeterSpecs = "Résistance primaire bobine : 0.8 Ω | Résistance secondaire : 7.2 kΩ",
      estimatedSeverity = "Haute (Risque destruction catalyseur)",
      estimatedRepairTimeHours = 1.0f
    ),
    OBDFault(
      code = "P2002",
      standardTitle = "Filtre à particules diesel (FAP) - Efficacité sous le seuil (Banque 1)",
      frenchDescription = "Le capteur de pression différentielle mesure un delta de pression anormal indiquant une saturation ou une dégradation du monolithe FAP.",
      plainLanguageExplanation = "Le filtre à particules (FAP) est colmaté par les suies d'échappement, très souvent suite à des petits trajets répétés en ville sans que le moteur n'ait chauffé. Le système se met en sécurité pour éviter d'étouffer le moteur.",
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
      relatedParts = listOf("Capteur de pression différentielle FAP", "Injecteur AdBlue", "Nettoyant FAP Professionnel"),
      detailedParts = listOf(
        DiagnosticRecommendedPart(
          partId = "part_dpf_diff_sensor",
          name = "Capteur de pression différentielle FAP (G450)",
          oemReference = "04L 906 051 B",
          brandName = "Bosch Piezo Micro Sensor",
          equivalentBrands = listOf("Bosch", "Hella", "Sensata"),
          systemCategory = SystemCategory.ELECTRICITE,
          priceEstimatedDzd = 8200,
          availabilityBadge = "En stock chez 15 vendeurs",
          urgencyLevel = "Contrôle & Remplacement"
        ),
        DiagnosticRecommendedPart(
          partId = "part_dpf_cleaner",
          name = "Additif nettoyant FAP régénération express (1L)",
          oemReference = "G 052 143 A2",
          brandName = "Liqui Moly / Bardahl Pro",
          equivalentBrands = listOf("Liqui Moly", "Bardahl", "Wynn's"),
          systemCategory = SystemCategory.FILTRATION,
          priceEstimatedDzd = 4500,
          availabilityBadge = "En stock immédiat",
          urgencyLevel = "Traitement Curatif"
        )
      ),
      multimeterSpecs = "Tension signal G450 au ralenti : 0.45V - 0.70V (Delta pression < 15 mbar)",
      estimatedSeverity = "Haute (Colmatage irréversible si négligé)",
      estimatedRepairTimeHours = 1.5f
    ),
    OBDFault(
      code = "P0101",
      standardTitle = "Débitmètre d'air massique (MAF) - Plage / Rendement du circuit",
      frenchDescription = "Le signal envoyé par le capteur de débitmètre d'air (fil chaud ou film chaud) est incohérent par rapport à la vitesse moteur et à la position du papillon.",
      plainLanguageExplanation = "Le capteur qui pèse l'air entrant dans le moteur est sale ou défectueux. Le calculateur moteur ne sait plus exactement combien de carburant injecter, ce qui fait surconsommer ou donne des trous à l'accélération.",
      probableCauses = listOf(
        "Film chaud du débitmètre encrassé par les poussières ou vapeurs d'huile",
        "Filtre à air moteur complètement saturé ou mal positionné",
        "Prise d'air ou durite d'admission déboîtée entre le débitmètre et le turbo",
        "Faux contact dans le connecteur électrique du débitmètre"
      ),
      symptoms = listOf(
        "Trous à l'accélération à bas régime",
        "Surconsommation de carburant (+15% à +25%)",
        "Démarrage difficile à froid"
      ),
      diagnosticSteps = listOf(
        "1. Inspecter l'état du filtre à air moteur et l'absence de corps étrangers.",
        "2. Mesurer la valeur de débit d'air au ralenti (environ 4.0 à 7.0 g/s sur 2.0 TDI).",
        "3. Contrôler l'alimentation 12V et la masse de référence du connecteur 4 broches."
      ),
      concernedPartCategory = SystemCategory.MOTEUR,
      relatedParts = listOf("Débitmètre d'air MAF", "Filtre à air moteur", "Nettoyant spécial capteur MAF"),
      detailedParts = listOf(
        DiagnosticRecommendedPart(
          partId = "part_maf_sensor",
          name = "Débitmètre d'air massique à film chaud",
          oemReference = "04L 906 461 B",
          brandName = "Hitachi / Bosch OEM",
          equivalentBrands = listOf("Hitachi", "Bosch", "Pierburg"),
          systemCategory = SystemCategory.ELECTRICITE,
          priceEstimatedDzd = 14500,
          availabilityBadge = "En stock chez 9 grossistes",
          urgencyLevel = "Remplacement Recommandé"
        ),
        DiagnosticRecommendedPart(
          partId = "part_air_filter",
          name = "Filtre à air moteur haute capacité",
          oemReference = "5Q0 129 620 B",
          brandName = "Mann-Filter C 30 005",
          equivalentBrands = listOf("Mann-Filter", "Purflux", "Mahle", "Bosch"),
          systemCategory = SystemCategory.FILTRATION,
          priceEstimatedDzd = 2800,
          availabilityBadge = "En stock immédiat",
          urgencyLevel = "Entretien Périodique"
        )
      ),
      multimeterSpecs = "Alimentation broche 2 : 12.0V | Masse broche 3 : 0.0V | Signal broche 4 : 1.0V au contact, 1.8V au ralenti",
      estimatedSeverity = "Moyenne",
      estimatedRepairTimeHours = 0.5f
    ),
    OBDFault(
      code = "P0171",
      standardTitle = "Mélange trop pauvre (Banque 1) - Manque de carburant ou excès d'air",
      frenchDescription = "La sonde lambda amont indique un excès permanent d'oxygène dans les gaz d'échappement, obligeant le calculateur à corriger le temps d'injection au maximum (+25%).",
      plainLanguageExplanation = "Il y a trop d'air ou pas assez d'essence/diesel qui arrive dans les cylindres. C'est souvent causé par une petite durite d'air craquelée (prise d'air pirate) ou un filtre à carburant colmaté.",
      probableCauses = listOf(
        "Prise d'air pirate sur le collecteur d'admission ou le reniflard d'huile",
        "Filtre à carburant colmaté ou pompe de gavage à débit insuffisant",
        "Injecteurs de carburant partiellement bouchés",
        "Sonde lambda amont vieillie ou encrassée"
      ),
      symptoms = listOf(
        "Ralenti instable et hésitations à l'accélération",
        "Légères détonations dans l'échappement à la décélération",
        "Voyant moteur orange fixe"
      ),
      diagnosticSteps = listOf(
        "1. Pulvériser du nettoyant frein sur les durites d'admission pour détecter une prise d'air (hausse de régime).",
        "2. Contrôler les valeurs LTFT (Long Term Fuel Trim) à la valise : supérieure à +20%.",
        "3. Mesurer la pression d'essence / gasoil au manomètre en amont de la pompe."
      ),
      concernedPartCategory = SystemCategory.MOTEUR,
      relatedParts = listOf("Sonde lambda amont", "Filtre à carburant", "Joints collecteur admission", "Valve de reniflard PCV"),
      detailedParts = listOf(
        DiagnosticRecommendedPart(
          partId = "part_lambda_sensor",
          name = "Sonde lambda amont à large bande 5 fils",
          oemReference = "04L 906 262 A",
          brandName = "Bosch LSU 4.9",
          equivalentBrands = listOf("Bosch", "NTK", "Denso"),
          systemCategory = SystemCategory.ELECTRICITE,
          priceEstimatedDzd = 16800,
          availabilityBadge = "En stock chez 11 revendeurs",
          urgencyLevel = "Contrôle & Remplacement"
        ),
        DiagnosticRecommendedPart(
          partId = "part_fuel_filter",
          name = "Filtre à carburant haute pression",
          oemReference = "5Q0 127 177 B",
          brandName = "Mann-Filter PU 8021",
          equivalentBrands = listOf("Mann-Filter", "Purflux", "Bosch", "Mahle"),
          systemCategory = SystemCategory.FILTRATION,
          priceEstimatedDzd = 3600,
          availabilityBadge = "En stock immédiat",
          urgencyLevel = "Entretien Immédiat"
        )
      ),
      multimeterSpecs = "Chauffage sonde lambda : 2.5 à 4.0 Ω | Tension de signal : oscille 0.1V à 0.9V",
      estimatedSeverity = "Moyenne",
      estimatedRepairTimeHours = 1.0f
    ),
    OBDFault(
      code = "P0340",
      standardTitle = "Capteur de position d'arbre à cames (AAC) - Panne du circuit",
      frenchDescription = "Le calculateur ne reçoit aucun signal synchronisé du capteur Hall d'arbre à cames lors de la rotation du moteur.",
      plainLanguageExplanation = "Le capteur qui surveille la position exacte des soupapes et de la distribution ne transmet plus son signal. Le moteur peut avoir du mal à démarrer ou refuser de démarrer, ou se décaler si la courroie/chaîne a sauté une dent.",
      probableCauses = listOf(
        "Capteur d'arbre à cames à effet Hall grillé ou défaillant",
        "Faisceau électrique coupé ou broche oxydée sur le connecteur AAC",
        "Décalage de la courroie ou de la chaîne de distribution (dents sautées)",
        "Cible magnétique d'arbre à cames abîmée ou desserrée"
      ),
      symptoms = listOf(
        "Temps de démarreur anormalement long avant démarrage",
        "Coupures moteur intermittentes en roulant",
        "Perte de puissance et allumage du voyant moteur"
      ),
      diagnosticSteps = listOf(
        "1. Mesurer la tension d'alimentation du capteur (5.0V ou 12V selon constructeur) et la masse.",
        "2. Contrôler le signal carré à l'oscilloscope ou la fréquence en Hz lors de la rotation démarreur.",
        "3. Contrôler le calage mécanique de la distribution avec les piges d'atelier."
      ),
      concernedPartCategory = SystemCategory.DISTRIBUTION,
      relatedParts = listOf("Capteur AAC Arbre à cames", "Kit de distribution", "Pige de calage moteur"),
      detailedParts = listOf(
        DiagnosticRecommendedPart(
          partId = "part_camshaft_sensor",
          name = "Capteur de position d'arbre à cames Hall",
          oemReference = "04L 907 601 A",
          brandName = "Bosch Cam Sensor OEM",
          equivalentBrands = listOf("Bosch", "Hella", "Valeo", "Febi"),
          systemCategory = SystemCategory.ELECTRICITE,
          priceEstimatedDzd = 6200,
          availabilityBadge = "En stock immédiat",
          urgencyLevel = "Remplacement Prioritaire"
        ),
        DiagnosticRecommendedPart(
          partId = "part_timing_chain",
          name = "Kit chaîne de distribution renforcée 8mm",
          oemReference = "16 381 573 80",
          brandName = "Gates / OEM France",
          equivalentBrands = listOf("Gates", "INA", "Dayco", "Continental"),
          systemCategory = SystemCategory.DISTRIBUTION,
          priceEstimatedDzd = 32000,
          availabilityBadge = "En stock chez les grossistes certifiés",
          urgencyLevel = "Vérification Calage Impérative"
        )
      ),
      multimeterSpecs = "Alimentation : 5.0V | Signal carré : 0V / 5.0V | Fréquence au démarreur : 10 à 25 Hz",
      estimatedSeverity = "Haute (Risque décalage distribution)",
      estimatedRepairTimeHours = 1.0f
    ),
    OBDFault(
      code = "P0420",
      standardTitle = "Circuit de catalyseur (Ligne 1) - Rendement inférieur au seuil",
      frenchDescription = "La sonde lambda aval (après catalyseur) présente une oscillation trop proche de la sonde amont, indiquant que le convertisseur catalytique ne stocke plus l'oxygène.",
      plainLanguageExplanation = "Le pot catalytique ne filtre plus suffisamment les gaz d'échappement, ou la sonde à oxygène située après le catalyseur envoie une mauvaise mesure. Le véhicule pollue davantage et peut être refusé au contrôle technique.",
      probableCauses = listOf(
        "Monolithe du convertisseur catalytique usé, saturé ou fondu suite à des ratés d'allumage",
        "Sonde lambda aval (capteur O2 banque 1 capteur 2) lente ou défectueuse",
        "Fuite sur la ligne d'échappement avant ou au niveau de la tresse d'échappement",
        "Utilisation prolongée de carburant de mauvaise qualité ou additifs non conformes"
      ),
      symptoms = listOf(
        "Voyant moteur Check Engine allumé en permanence",
        "Odeur d'œuf pourri (soufre) à l'échappement à chaud",
        "Échec au test antipollution du contrôle technique"
      ),
      diagnosticSteps = listOf(
        "1. Comparer à la valise les courbes de tension de la sonde amont (0.1V-0.9V) et de la sonde aval (doit rester stable autour de 0.6V-0.7V à chaud).",
        "2. Mesurer l'écart de température entre l'entrée et la sortie du catalyseur au thermomètre infrarouge (la sortie doit être plus chaude de 30°C à 60°C).",
        "3. Vérifier l'absence de fuite d'échappement sur le collecteur et le joint de bride."
      ),
      concernedPartCategory = SystemCategory.MOTEUR,
      relatedParts = listOf("Catalyseur d'échappement OEM", "Sonde Lambda Aval 4 fils", "Joint de bride d'échappement"),
      detailedParts = listOf(
        DiagnosticRecommendedPart(
          partId = "part_o2_downstream",
          name = "Sonde Lambda Aval (Chauffée 4 fils)",
          oemReference = "06A 906 262 BR",
          brandName = "Bosch / NTK Japan",
          equivalentBrands = listOf("Bosch", "NTK", "Denso", "Delphi"),
          systemCategory = SystemCategory.ELECTRICITE,
          priceEstimatedDzd = 13500,
          availabilityBadge = "En stock immédiat",
          urgencyLevel = "Contrôle & Remplacement"
        ),
        DiagnosticRecommendedPart(
          partId = "part_catalytic_conv",
          name = "Convertisseur Catalytique Homologué Euro 6",
          oemReference = "5Q0 254 500 X",
          brandName = "Walker / Bosal OEM",
          equivalentBrands = listOf("Walker", "Bosal", "BM Catalysts"),
          systemCategory = SystemCategory.MOTEUR,
          priceEstimatedDzd = 64000,
          availabilityBadge = "Disponible sur commande 24h",
          urgencyLevel = "Remplacement si monolithe HS"
        )
      ),
      multimeterSpecs = "Résistance chauffage sonde aval : 4.0 à 9.0 Ω | Signal sonde aval stabilisé : 0.65V ± 0.08V",
      estimatedSeverity = "Moyenne (Antipollution)",
      estimatedRepairTimeHours = 1.5f
    ),
    OBDFault(
      code = "P0087",
      standardTitle = "Rampe de distribution / Pression système carburant - Trop basse",
      frenchDescription = "La pression réelle mesurée dans la rampe commune (Common Rail) est inférieure de plus de 250 bars à la consigne du calculateur d'injection.",
      plainLanguageExplanation = "Le carburant n'arrive pas avec assez de pression jusqu'aux injecteurs. Le moteur peut caler brutalement en pleine accélération ou refuser de démarrer, souvent à cause d'un filtre à gasoil bouché ou du régulateur de pompe haute pression.",
      probableCauses = listOf(
        "Filtre à carburant colmaté ou présence d'eau/limaille dans le circuit gasoil",
        "Régulateur de pression de carburant (DRV) ou électrovanne de dosage (IMV/ZME) grippé",
        "Injecteur avec débit de retour excessif (fuite interne vers le retour)",
        "Pompe haute pression CP4 / CP1 ou pompe de gavage basse pression défaillante"
      ),
      symptoms = listOf(
        "Coupure moteur franche lors d'une forte accélération ou en montée",
        "Voyant préchauffage clignotant + message 'Défaut Moteur Atelier'",
        "Démarrage très difficile à chaud comme à froid"
      ),
      diagnosticSteps = listOf(
        "1. Ouvrir le bocal du filtre à carburant pour vérifier l'absence de limaille métallique (test à l'aimant).",
        "2. Réaliser le test des fioles de retour d'injecteurs au ralenti pendant 2 minutes.",
        "3. Contrôler la pression de gavage basse pression (doit être > 4.5 bars) et la consigne Rail au démarreur (> 250 bars)."
      ),
      concernedPartCategory = SystemCategory.MOTEUR,
      relatedParts = listOf("Régulateur pression rampe Bosch", "Filtre à gasoil haute filtration", "Capteur pression Common Rail"),
      detailedParts = listOf(
        DiagnosticRecommendedPart(
          partId = "part_rail_regulator",
          name = "Régulateur de pression carburant Common Rail (DRV)",
          oemReference = "0 281 006 074",
          brandName = "Bosch Diesel Systems",
          equivalentBrands = listOf("Bosch", "Delphi", "Continental"),
          systemCategory = SystemCategory.MOTEUR,
          priceEstimatedDzd = 21500,
          availabilityBadge = "En stock chez 14 diésélistes",
          urgencyLevel = "Remplacement Prioritaire"
        ),
        DiagnosticRecommendedPart(
          partId = "part_fuel_filter_hp",
          name = "Cartouche filtre à gasoil haute séparation d'eau",
          oemReference = "5Q0 127 177 B",
          brandName = "Purflux C813 / Mann PU8021",
          equivalentBrands = listOf("Purflux", "Mann-Filter", "Bosch"),
          systemCategory = SystemCategory.FILTRATION,
          priceEstimatedDzd = 3600,
          availabilityBadge = "En stock immédiat",
          urgencyLevel = "Remplacement Immédiat"
        )
      ),
      multimeterSpecs = "Capteur Rail : Alim 5.0V, Signal 0.5V (0 bar) à 4.5V (1800 bars) | Électrovanne ZME : 2.6 à 3.2 Ω",
      estimatedSeverity = "Critique (Risque coupure moteur)",
      estimatedRepairTimeHours = 2.0f
    ),
    OBDFault(
      code = "C0035",
      standardTitle = "Capteur de vitesse de roue Avant Gauche - Défaut de circuit / Signal",
      frenchDescription = "Le bloc hydraulique ABS/ESP ne reçoit plus d'impulsions cohérentes du capteur magnéto-résistif de la roue avant gauche.",
      plainLanguageExplanation = "Le capteur qui mesure la vitesse de rotation de la roue avant gauche est coupé ou encrassé. Par sécurité, la voiture désactive l'ABS et l'antipatinage ESP (voyants orange allumés), tout en conservant le freinage classique.",
      probableCauses = listOf(
        "Capteur ABS avant gauche encrassé par la poussière métallique de frein ou coupé",
        "Câble du capteur ABS frotté contre la jante ou arraché près de la jambe d'amortisseur",
        "Cible magnétique intégrée au roulement de moyeu avant gauche endommagée",
        "Oxydation dans le connecteur 2 broches du passage de roue"
      ),
      symptoms = listOf(
        "Voyants ABS + ESP/ASR + Frein de parking électrique allumés au tableau de bord",
        "Désactivation de l'aide au démarrage en côte et du régulateur de vitesse",
        "Direction assistée légèrement plus ferme sur certains modèles"
      ),
      diagnosticSteps = listOf(
        "1. Lire les vitesses des 4 roues en temps réel à la valise en faisant tourner la roue AV G sur le pont.",
        "2. Inspecter visuellement le faisceau du capteur ABS dans le passage de roue avant gauche.",
        "3. Nettoyer l'entrefer du capteur et contrôler l'état de la bague magnétique du roulement."
      ),
      concernedPartCategory = SystemCategory.FREINAGE,
      relatedParts = listOf("Capteur ABS Avant Gauche", "Roulement de moyeu avec bague ABS"),
      detailedParts = listOf(
        DiagnosticRecommendedPart(
          partId = "part_abs_sensor_fl",
          name = "Capteur de vitesse de roue ABS actif Avant Gauche",
          oemReference = "WHT 003 857 A",
          brandName = "ATE / Bosch Chassis Systems",
          equivalentBrands = listOf("ATE", "Bosch", "TRW", "Delphi"),
          systemCategory = SystemCategory.FREINAGE,
          priceEstimatedDzd = 5800,
          availabilityBadge = "En stock immédiat",
          urgencyLevel = "Remplacement Recommandé"
        ),
        DiagnosticRecommendedPart(
          partId = "part_wheel_bearing_fl",
          name = "Kit roulement de roue avant avec cible magnétique ABS",
          oemReference = "5Q0 407 621 C",
          brandName = "SKF VKBA 7011 / SNR",
          equivalentBrands = listOf("SKF", "SNR", "FAG"),
          systemCategory = SystemCategory.SUSPENSION,
          priceEstimatedDzd = 16500,
          availabilityBadge = "En stock chez 10 grossistes",
          urgencyLevel = "Si cible magnétique abîmée"
        )
      ),
      multimeterSpecs = "Capteur actif Hall : Alimentation 12V | Courant de signal carré : 7 mA (état bas) / 14 mA (état haut)",
      estimatedSeverity = "Moyenne à Élevée (Sécurité ABS/ESP)",
      estimatedRepairTimeHours = 0.8f
    ),
    OBDFault(
      code = "C0040",
      standardTitle = "Capteur de vitesse de roue Avant Droit - Plage / Performance du circuit",
      frenchDescription = "Écart de vitesse anormal ou absence de signal sur l'entrée roue avant droite du calculateur ABS/ESP.",
      plainLanguageExplanation = "Le capteur ABS de la roue avant droite envoie une vitesse erronée ou nulle. Les systèmes ABS et ESP se mettent hors service jusqu'au remplacement ou nettoyage du capteur.",
      probableCauses = listOf(
        "Capteur ABS avant droit HS ou fil coupé",
        "Jeu excessif dans le roulement de roue avant droit",
        "Bague cible ABS encrassée par de la graisse ou de la rouille"
      ),
      symptoms = listOf(
        "Témoins ABS et ESP allumés dès 20 km/h",
        "Déclenchement intempestif de l'ABS à basse vitesse"
      ),
      diagnosticSteps = listOf(
        "1. Comparer la vitesse roue AV D avec les 3 autres roues en essai routier valise.",
        "2. Contrôler le 12V sur le connecteur côté faisceau châssis.",
        "3. Remplacer le capteur ABS AV D et effacer la mémoire défaut."
      ),
      concernedPartCategory = SystemCategory.FREINAGE,
      relatedParts = listOf("Capteur ABS Avant Droit", "Faisceau réparation connecteur ABS"),
      detailedParts = listOf(
        DiagnosticRecommendedPart(
          partId = "part_abs_sensor_fr",
          name = "Capteur de vitesse de roue ABS Avant Droit",
          oemReference = "WHT 003 856 A",
          brandName = "ATE / Bosch OEM",
          equivalentBrands = listOf("ATE", "Bosch", "Febi", "TRW"),
          systemCategory = SystemCategory.FREINAGE,
          priceEstimatedDzd = 5800,
          availabilityBadge = "En stock immédiat",
          urgencyLevel = "Remplacement Recommandé"
        )
      ),
      multimeterSpecs = "Tension d'alimentation connecteur : 12.0V | Signal courant : 7 mA / 14 mA",
      estimatedSeverity = "Moyenne à Élevée",
      estimatedRepairTimeHours = 0.8f
    ),
    OBDFault(
      code = "C1288",
      standardTitle = "Capteur de pression principale du maître-cylindre de frein - Défaut circuit",
      frenchDescription = "Le transducteur de pression hydraulique interne ou externe au bloc ABS/ESP renvoie une valeur hors plage.",
      plainLanguageExplanation = "Le capteur qui mesure la force avec laquelle vous appuyez sur la pédale de frein est en défaut. Le contrôle de trajectoire ESP est désactivé mais le freinage hydraulique reste actif.",
      probableCauses = listOf(
        "Capteur de pression G201 sur le bloc hydraulique ABS défectueux",
        "Liquide de frein DOT4 très dégradé ou chargé d'humidité",
        "Contacteur de pédale de frein désynchronisé avec la pression hydraulique"
      ),
      symptoms = listOf(
        "Voyant ESP / Contrôle de stabilité allumé fixe",
        "Feux stop restant parfois allumés ou aide au freinage d'urgence inactive"
      ),
      diagnosticSteps = listOf(
        "1. Lire la pression de freinage au repos à la valise (doit être 0.0 bar ± 1.5 bar pédale relâchée).",
        "2. Appuyer franchement sur la pédale et vérifier que la pression monte jusqu'à 80-120 bars.",
        "3. Effectuer une purge complète du bloc ABS avec l'outil de diagnostic."
      ),
      concernedPartCategory = SystemCategory.FREINAGE,
      relatedParts = listOf("Capteur pression maître-cylindre", "Liquide de frein DOT4 ESP", "Contacteur pédale de frein"),
      detailedParts = listOf(
        DiagnosticRecommendedPart(
          partId = "part_brake_pressure_sensor",
          name = "Capteur de pression hydraulique maître-cylindre ABS",
          oemReference = "1K0 907 597 B",
          brandName = "ATE Original",
          equivalentBrands = listOf("ATE", "Bosch", "Vemo"),
          systemCategory = SystemCategory.FREINAGE,
          priceEstimatedDzd = 14800,
          availabilityBadge = "En stock chez 6 spécialistes freinage",
          urgencyLevel = "Remplacement Recommandé"
        )
      ),
      multimeterSpecs = "Alim : 5.0V | Signal repos : 0.5V (0 bar) | Signal freinage fort : 3.8V (100 bars)",
      estimatedSeverity = "Élevée (Sécurité ESP)",
      estimatedRepairTimeHours = 1.5f
    ),
    OBDFault(
      code = "B0001",
      standardTitle = "Circuit d'amorçage Airbag frontal conducteur (Étape 1) - Résistance élevée",
      frenchDescription = "Le calculateur SRS Airbag mesure une impédance supérieure à 4.5 Ohms sur la ligne de l'allumeur du coussin gonflable conducteur.",
      plainLanguageExplanation = "Le contacteur tournant (spirale sous le volant) ou le connecteur sous le siège a un faux contact. Le voyant Airbag reste allumé et l'airbag du volant risque de ne pas se déclencher en cas de choc.",
      probableCauses = listOf(
        "Nappe du contacteur tournant (ressort hélicoïdal / Clockspring) coupée sous le volant",
        "Connecteur jaune sous le siège conducteur desserré après déplacement du siège",
        "Verrouillage du connecteur d'allumeur d'airbag mal enclenché"
      ),
      symptoms = listOf(
        "Voyant Airbag / SRS rouge allumé au tableau de bord",
        "Boutons du volant multifonction ou klaxon inopérants par intermittence"
      ),
      diagnosticSteps = listOf(
        "1. SÉCURITÉ : Débrancher la batterie 12V et attendre 10 minutes avant toute intervention sur le circuit SRS.",
        "2. NE JAMAIS mesurer un airbag directement à l'ohmmètre (utiliser une résistance de simulation SRS de 2.2 Ohms).",
        "3. Insérer le leurre 2.2 Ω avant le contacteur tournant pour confirmer que la nappe du volant est coupée."
      ),
      concernedPartCategory = SystemCategory.ELECTRICITE,
      relatedParts = listOf("Contacteur tournant Airbag (Clockspring)", "Kit réparation connecteur sous siège"),
      detailedParts = listOf(
        DiagnosticRecommendedPart(
          partId = "part_clockspring_srs",
          name = "Contacteur tournant spirale volant Airbag & Commandes",
          oemReference = "5K0 953 569 AL",
          brandName = "Valeo / Kostal OEM",
          equivalentBrands = listOf("Valeo", "Kostal", "Febi Bilstein", "Meat & Doria"),
          systemCategory = SystemCategory.ELECTRICITE,
          priceEstimatedDzd = 19500,
          availabilityBadge = "En stock immédiat",
          urgencyLevel = "Sécurité Passive Prioritaire"
        )
      ),
      multimeterSpecs = "Impédance nominale ligne SRS (lue à la valise) : 1.8 à 2.8 Ω (Défaut si > 4.2 Ω)",
      estimatedSeverity = "Haute (Sécurité Airbag SRS)",
      estimatedRepairTimeHours = 1.0f
    ),
    OBDFault(
      code = "B1000",
      standardTitle = "Calculateur central habitacle / Unité de commande SRS - Défaut interne ou codage",
      frenchDescription = "Anomalie de somme de contrôle mémoire EEPROM ou défaut de configuration dans le module de carrosserie / airbag.",
      plainLanguageExplanation = "Le boîtier électronique qui gère les équipements de sécurité ou de confort de l'habitacle a subi une chute de tension batterie ou nécessite une réinitialisation à la valise.",
      probableCauses = listOf(
        "Chute de tension sévère de la batterie 12V lors d'un démarrage difficile",
        "Humidité ou oxydation sur les broches du boîtier BSI / BCM",
        "Données de crash mémorisées dans l'EEPROM du module SRS"
      ),
      symptoms = listOf(
        "Témoin Airbag ou défaut électronique général affiché sur l'écran",
        "Fonctionnements erratiques des essuie-glaces ou de la centralisation"
      ),
      diagnosticSteps = listOf(
        "1. Tester l'état de santé de la batterie 12V (SOH) et la tension de charge alternateur (14.2V).",
        "2. Réaliser un 'Hard Reset' du BSI et effacer les défauts fugitifs sous maintien de charge stabilisé."
      ),
      concernedPartCategory = SystemCategory.ELECTRICITE,
      relatedParts = listOf("Batterie AGM Start-Stop 70Ah", "Module interface BSI/BCM"),
      detailedParts = listOf(
        DiagnosticRecommendedPart(
          partId = "part_battery_agm_70",
          name = "Batterie 12V 70Ah 760A Technologie AGM Start-Stop",
          oemReference = "000 915 105 CC",
          brandName = "Varta Silver Dynamic AGM",
          equivalentBrands = listOf("Varta", "Bosch", "Exide", "Fulmen"),
          systemCategory = SystemCategory.ELECTRICITE,
          priceEstimatedDzd = 29500,
          availabilityBadge = "En stock immédiat",
          urgencyLevel = "Contrôle & Remplacement"
        )
      ),
      multimeterSpecs = "Tension repos batterie : > 12.6V | Tension sous démarreur : > 10.2V",
      estimatedSeverity = "Moyenne",
      estimatedRepairTimeHours = 0.8f
    ),
    OBDFault(
      code = "B1325",
      standardTitle = "Tension d'alimentation du module carrosserie (BCM/BSI) - Inférieure au seuil",
      frenchDescription = "Le boîtier de servitude intelligent a enregistré une tension réseau de bord inférieure à 9.5V.",
      plainLanguageExplanation = "La batterie ou l'alternateur ne fournit plus assez d'électricité aux boîtiers de la voiture. Plusieurs voyants peuvent s'allumer simultanément au tableau de bord.",
      probableCauses = listOf(
        "Batterie 12V en fin de vie ou sulfatée",
        "Régulateur d'alternateur ou poulie roue-libre d'alternateur défectueuse",
        "Cosse de masse châssis/boîte de vitesses desserrée ou oxydée"
      ),
      symptoms = listOf(
        "Démarrage lent et extinction momentanée de l'écran central",
        "Message 'Défaut charge batterie' ou désactivation du Start & Stop"
      ),
      diagnosticSteps = listOf(
        "1. Mesurer la chute de tension entre la borne (-) batterie et le bloc moteur au démarrage (< 0.2V).",
        "2. Contrôler la tension de régulation de l'alternateur en charge (phares + dégivrage allumés : 13.9V à 14.5V)."
      ),
      concernedPartCategory = SystemCategory.ELECTRICITE,
      relatedParts = listOf("Régulateur d'alternateur", "Batterie 12V", "Poulie débrayable alternateur"),
      detailedParts = listOf(
        DiagnosticRecommendedPart(
          partId = "part_alt_pulley",
          name = "Poulie roue-libre d'alternateur (OAP)",
          oemReference = "03L 903 119 G",
          brandName = "INA Schaeffler OEM",
          equivalentBrands = listOf("INA", "Valeo", "Bosch", "SKF"),
          systemCategory = SystemCategory.ELECTRICITE,
          priceEstimatedDzd = 7800,
          availabilityBadge = "En stock immédiat",
          urgencyLevel = "Remplacement Recommandé"
        )
      ),
      multimeterSpecs = "Charge alternateur : 14.2V | Chute de tension ligne masse : < 0.15V",
      estimatedSeverity = "Moyenne",
      estimatedRepairTimeHours = 1.0f
    ),
    OBDFault(
      code = "U0100",
      standardTitle = "Perte de communication CAN-Bus avec le calculateur moteur (ECM/PCM)",
      frenchDescription = "Les autres calculateurs du réseau multiplexé (ABS, boîte auto, combiné) ne reçoivent plus les trames CAN High/Low du calculateur d'injection.",
      plainLanguageExplanation = "Le cerveau du moteur (calculateur) ne répond plus aux autres boîtiers de la voiture. Souvent causé par le relais principal d'alimentation moteur grillé, un fusible fondu ou un fil coupé dans le faisceau.",
      probableCauses = listOf(
        "Relais principal d'alimentation calculateur moteur (Relais 109 / R4 UPC) HS",
        "Paire torsadée CAN-High / CAN-Low coupée ou en court-circuit",
        "Oxydation dans la prise multibroches du calculateur moteur (infiltration d'eau baie de pare-brise)",
        "Fusible d'alimentation +15 après contact grillé"
      ),
      symptoms = listOf(
        "Le moteur lance au démarreur mais ne démarre pas (voyant préchauffage/moteur absent au contact)",
        "Ventilateur de refroidissement qui tourne à pleine vitesse dès la mise du contact",
        "Messages d'erreur multiples (ESP, Boîte, Frein de parking) au combiné"
      ),
      diagnosticSteps = listOf(
        "1. Débrancher la batterie et mesurer la résistance terminale du bus CAN sur la prise OBD entre les broches 6 (CAN-H) et 14 (CAN-L) : doit être exactement 60 Ohms.",
        "2. Vérifier le claquement et la sortie 12V du relais principal d'alimentation ECM.",
        "3. Contrôler les tensions CAN-High (~2.6V à 3.5V) et CAN-Low (~1.5V à 2.4V) contact mis."
      ),
      concernedPartCategory = SystemCategory.ELECTRICITE,
      relatedParts = listOf("Relais principal gestion moteur", "Kit réparation faisceau CAN-Bus", "Boîtier fusibles moteur"),
      detailedParts = listOf(
        DiagnosticRecommendedPart(
          partId = "part_ecu_main_relay",
          name = "Relais principal d'alimentation calculateur moteur 40A",
          oemReference = "4H0 951 253 A",
          brandName = "Hella / VAG Original Relay",
          equivalentBrands = listOf("Hella", "Bosch", "Febi", "Meyle"),
          systemCategory = SystemCategory.ELECTRICITE,
          priceEstimatedDzd = 2900,
          availabilityBadge = "En stock immédiat",
          urgencyLevel = "Remplacement Immédiat"
        )
      ),
      multimeterSpecs = "Résistance CAN (Broches 6 & 14 OBD hors tension) : 60 Ω (2 résistances de 120 Ω en parallèle) | CAN-H + CAN-L = 5.0V",
      estimatedSeverity = "Critique (Immobilisation véhicule)",
      estimatedRepairTimeHours = 1.5f
    ),
    OBDFault(
      code = "U0121",
      standardTitle = "Perte de communication CAN avec le module de commande ABS / ESP",
      frenchDescription = "Trames de vitesse véhicule et d'état freinage absentes sur le bus CAN Propulsion.",
      plainLanguageExplanation = "Le calculateur moteur et le tableau de bord n'arrivent plus à communiquer avec le bloc ABS. Le compteur de vitesse peut tomber à zéro et les témoins de freinage s'allument.",
      probableCauses = listOf(
        "Maxi-fusible d'alimentation 40A/50A du bloc ABS grillé ou fissuré sur la platine batterie",
        "Connecteur 38/47 voies du bloc ABS mal verrouillé ou oxydé",
        "Fil CAN-High ou CAN-Low coupé sur la branche menant au bloc hydraulique"
      ),
      symptoms = listOf(
        "Voyants ABS, ESP et Direction assistée allumés",
        "Compteur kilométrique et indicateur de vitesse inopérants"
      ),
      diagnosticSteps = listOf(
        "1. Contrôler les deux alimentations permanentes +30 (fusibles 40A et 25A) et l'après-contact +15 sur la prise du bloc ABS.",
        "2. Vérifier la continuité des deux fils CAN torsadés entre le bloc ABS et la passerelle Gateway."
      ),
      concernedPartCategory = SystemCategory.ELECTRICITE,
      relatedParts = listOf("Platine porte-fusibles batterie", "Connecteur étanche ABS"),
      detailedParts = listOf(
        DiagnosticRecommendedPart(
          partId = "part_battery_fuse_box",
          name = "Platine porte-fusibles haute intensité sur batterie",
          oemReference = "1K0 937 629 A",
          brandName = "MTA / VAG OEM",
          equivalentBrands = listOf("MTA", "Febi", "Topran"),
          systemCategory = SystemCategory.ELECTRICITE,
          priceEstimatedDzd = 9200,
          availabilityBadge = "En stock chez 8 vendeurs",
          urgencyLevel = "Contrôle & Remplacement"
        )
      ),
      multimeterSpecs = "Résistance ligne CAN au connecteur ABS : 60 Ω | Masse broches 13 & 38 : 0.0 Ω",
      estimatedSeverity = "Haute",
      estimatedRepairTimeHours = 1.2f
    ),
    OBDFault(
      code = "U0001",
      standardTitle = "Bus de communication CAN Haute Vitesse (High Speed CAN) - Défaut de ligne",
      frenchDescription = "Mise en court-circuit au +12V, à la masse ou coupure sur le bus CAN Propulsion 500 kbps.",
      plainLanguageExplanation = "Le câble réseau principal qui relie les calculateurs de la voiture subit un court-circuit ou une coupure. L'ensemble du tableau de bord affiche des alertes simultanées.",
      probableCauses = listOf(
        "Faisceau CAN blessé contre une arête métallique sous le bac à batterie",
        "Accessoire seconde monte (alarme, traceur GPS, autoradio) branché par erreur sur les fils CAN",
        "Passerelle centrale (CAN Gateway) défaillante"
      ),
      symptoms = listOf(
        "Allumage simultané de multiples voyants (Sapin de Noël au combiné)",
        "Impossibilité de dialoguer avec plusieurs calculateurs à la valise OBD"
      ),
      diagnosticSteps = listOf(
        "1. Débrancher tout boîtier additionnel sur la prise OBD ou derrière l'autoradio.",
        "2. Mesurer l'impédance entre broches 6 et 14 de la prise OBD : si 120 Ω = une terminaison coupée ; si 0 Ω = court-circuit franc.",
        "3. Isoler les calculateurs un par un sur le peigne CAN jusqu'au retour de la valeur 60 Ω."
      ),
      concernedPartCategory = SystemCategory.ELECTRICITE,
      relatedParts = listOf("Module Passerelle CAN Gateway", "Faisceau torsadé réparation CAN"),
      detailedParts = listOf(
        DiagnosticRecommendedPart(
          partId = "part_can_gateway",
          name = "Module Passerelle Centrale Diagnostic (CAN Gateway)",
          oemReference = "5Q0 907 530 AM",
          brandName = "Continental / VAG OEM",
          equivalentBrands = listOf("Continental", "Bosch", "Hella"),
          systemCategory = SystemCategory.ELECTRICITE,
          priceEstimatedDzd = 24000,
          availabilityBadge = "En stock immédiat",
          urgencyLevel = "Diagnostic Multiplexage"
        )
      ),
      multimeterSpecs = "Signal CAN-High : 2.5V → 3.5V | Signal CAN-Low : 2.5V → 1.5V | Impédance boucle : 60.0 Ω",
      estimatedSeverity = "Critique (Réseau Multiplexé)",
      estimatedRepairTimeHours = 2.0f
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

  // Comptes Clients Ateliers (Comptes gratuits dédiés aux ateliers pour consultation & impression)
  val sampleWorkshopClientAccounts: List<WorkshopClientAccount> = listOf(
    WorkshopClientAccount(
      id = "WCA-001",
      plateNumber = "04291-121-16",
      passwordPin = "1234",
      clientName = "Karim Benali",
      clientPhone = "0550 88 77 66",
      vehicleModel = "VW Tiguan II 2.0 TDI 150ch (2021)",
      vin = "WVWZZZ5NZMW123456",
      qrAccessCode = "MEKANIK-CLIENT-04291-121-16",
      createdAt = "10 Janvier 2026",
      activeRepairOrder = sampleRepairOrders[0],
      repairHistory = listOf(sampleRepairOrders[0]),
      invoices = listOf(
        FiscalInvoice(
          invoiceNumber = "FCT-2026-0842",
          invoiceType = InvoiceType.FACTURE_DEFINITIVE,
          date = "17/08/2026",
          clientName = "Karim Benali",
          clientPhone = "0550 88 77 66",
          vehicleVin = "WVWZZZ5NZMW123456",
          vehicleModel = "VW Tiguan II 2.0 TDI 150ch (2021)",
          plateNumber = "04291-121-16",
          items = listOf(
            InvoiceLineItem("5Q0 615 301 F", "Jeu de disques avant Brembo Ø312mm", 1, 18500.0),
            InvoiceLineItem("5Q0 698 151 BR", "Jeu plaquettes avant Brembo", 1, 8500.0),
            InvoiceLineItem("MO-FREIN-01", "Pose disques & plaquettes + purge DOT4", 2, 2500.0, isLabor = true)
          ),
          tvaPercent = 19.0,
          stampTaxDzd = 100.0,
          isPaid = true,
          paymentMethod = PaymentMethod.CIB_EDAHABIA,
          qrFiscalHash = "DZ-MEKANIK-2026-FCT-0842"
        )
      ),
      isFreeAccount = true
    ),
    WorkshopClientAccount(
      id = "WCA-002",
      plateNumber = "18204-122-31",
      passwordPin = "2026",
      clientName = "Mourad Meziane",
      clientPhone = "0770 11 22 33",
      vehicleModel = "Peugeot 308 III 1.5 BlueHDi (2022)",
      vin = "VF3FPHNSSMY001234",
      qrAccessCode = "MEKANIK-CLIENT-18204-122-31",
      createdAt = "15 Février 2026",
      activeRepairOrder = sampleRepairOrders[1],
      repairHistory = listOf(sampleRepairOrders[1]),
      invoices = listOf(
        FiscalInvoice(
          invoiceNumber = "PRF-2026-0839",
          invoiceType = InvoiceType.FACTURE_PROFORMA,
          date = "15/08/2026",
          clientName = "Mourad Meziane",
          clientPhone = "0770 11 22 33",
          vehicleVin = "VF3FPHNSSMY001234",
          vehicleModel = "Peugeot 308 III 1.5 BlueHDi",
          plateNumber = "18204-122-31",
          items = listOf(
            InvoiceLineItem("16 381 573 80", "Kit chaîne arbre à cames renforcée 8mm OEM PSA", 1, 32000.0),
            InvoiceLineItem("16 230 981 80", "Pompe à eau + Liquide refroidissement G13", 1, 14000.0),
            InvoiceLineItem("MO-DISTRI-01", "Calage distribution & chaîne d'arbres à cames", 4, 2500.0, isLabor = true)
          ),
          tvaPercent = 19.0,
          stampTaxDzd = 100.0,
          isPaid = false,
          paymentMethod = null,
          qrFiscalHash = "DZ-MEKANIK-2026-PRF-0839"
        )
      ),
      isFreeAccount = true
    ),
    WorkshopClientAccount(
      id = "WCA-003",
      plateNumber = "09312-120-09",
      passwordPin = "5678",
      clientName = "Amine Djouadi",
      clientPhone = "0661 44 55 66",
      vehicleModel = "Renault Clio V 1.5 Blue dCi (2023)",
      vin = "VF1RJA00067890123",
      qrAccessCode = "MEKANIK-CLIENT-09312-120-09",
      createdAt = "02 Mars 2026",
      activeRepairOrder = sampleRepairOrders[2],
      repairHistory = listOf(sampleRepairOrders[2]),
      invoices = listOf(
        FiscalInvoice(
          invoiceNumber = "FCT-2026-0830",
          invoiceType = InvoiceType.FACTURE_DEFINITIVE,
          date = "12/08/2026",
          clientName = "Amine Djouadi",
          clientPhone = "0661 44 55 66",
          vehicleVin = "VF1RJA00067890123",
          vehicleModel = "Renault Clio V 1.5 Blue dCi",
          plateNumber = "09312-120-09",
          items = listOf(
            InvoiceLineItem("RENAULT-KIT-4F", "Kit révision complète 4 filtres Purflux", 1, 12500.0),
            InvoiceLineItem("ELF-5W30-RN0720", "Huile Elf Full-Tech FE 5W30 5L", 1, 8200.0),
            InvoiceLineItem("MO-VID-01", "Vidange, remplacement 4 filtres & RAZ entretien", 1, 3000.0, isLabor = true)
          ),
          tvaPercent = 19.0,
          stampTaxDzd = 100.0,
          isPaid = true,
          paymentMethod = PaymentMethod.ESPECES,
          qrFiscalHash = "DZ-MEKANIK-2026-FCT-0830"
        )
      ),
      isFreeAccount = true
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

  // Tâches de Maintenance du Jour (Atelier Dashboard)
  val sampleDailyMaintenanceTasks: List<DailyMaintenanceTask> = listOf(
    DailyMaintenanceTask(
      id = "TASK-01",
      taskTitle = "Vidange Complète 10 000 km & 4 Filtres Purflux",
      category = "Vidange & Filtres",
      vehicleModel = "Volkswagen Golf 7 (2.0 TDI 150)",
      licensePlate = "04291-120-16",
      clientName = "Yacine Mansouri",
      clientPhone = "0661 88 99 00",
      scheduledTime = "09:00",
      estimatedMinutes = 45,
      priority = MaintenanceTaskPriority.PROGRAMME,
      status = MaintenanceTaskStatus.EN_COURS,
      assignedMechanic = "Karim B.",
      bayNumber = 1,
      requiredPartsSummary = "Filtres Huile/Air/Gazole/Habitacle + Huile Motul 5W30 LL",
      partsAvailable = true,
      notes = "Client souhaite aussi vérifier la pression de la roue de secours"
    ),
    DailyMaintenanceTask(
      id = "TASK-02",
      taskTitle = "Remplacement Disques & Plaquettes Avant Brembo",
      category = "Freinage & Sécurité",
      vehicleModel = "Seat Ibiza FR (1.6 TDI 105)",
      licensePlate = "05512-119-16",
      clientName = "Mehdi Khelifi",
      clientPhone = "0550 44 33 22",
      scheduledTime = "10:15",
      estimatedMinutes = 75,
      priority = MaintenanceTaskPriority.URGENT,
      status = MaintenanceTaskStatus.EN_ATTENTE,
      assignedMechanic = "Sofiane R.",
      bayNumber = 3,
      requiredPartsSummary = "Jeu disques Ø288mm + Plaquettes Brembo P 85 072",
      partsAvailable = true,
      notes = "Témoin d'usure allumé et bruit métallique au freinage"
    ),
    DailyMaintenanceTask(
      id = "TASK-03",
      taskTitle = "Diagnostic Électronique & Régénération FAP Forcée",
      category = "Diagnostic & ADAS",
      vehicleModel = "Renault Clio 5 (1.5 Blue dCi 115)",
      licensePlate = "11204-123-16",
      clientName = "Amine Djouadi",
      clientPhone = "0661 44 55 66",
      scheduledTime = "11:30",
      estimatedMinutes = 60,
      priority = MaintenanceTaskPriority.NORMAL,
      status = MaintenanceTaskStatus.EN_COURS,
      assignedMechanic = "Amine K.",
      bayNumber = 4,
      requiredPartsSummary = "Sonde T° échappement amont + Nettoyant FAP DPF Cleaner",
      partsAvailable = true,
      notes = "Code défaut OBD P2463 suies élevées, régénération statique"
    ),
    DailyMaintenanceTask(
      id = "TASK-04",
      taskTitle = "Recharge Climatisation R1234yf & Tirage au Vide",
      category = "Climatisation",
      vehicleModel = "Peugeot 308 III (1.5 BlueHDi)",
      licensePlate = "09321-118-16",
      clientName = "Mourad Meziane",
      clientPhone = "0770 11 22 33",
      scheduledTime = "14:00",
      estimatedMinutes = 50,
      priority = MaintenanceTaskPriority.PROGRAMME,
      status = MaintenanceTaskStatus.EN_ATTENTE,
      assignedMechanic = "Yacine M.",
      bayNumber = 2,
      requiredPartsSummary = "Fluide R1234yf (450g) + Traceur UV étanchéité + Huile PAG",
      partsAvailable = true,
      notes = "Faible débit d'air froid et odeur humidité signalée"
    ),
    DailyMaintenanceTask(
      id = "TASK-05",
      taskTitle = "Contrôle Pré-Visite Technique & Réglage Optiques",
      category = "Pré-Contrôle",
      vehicleModel = "Dacia Duster 4x4 (1.5 dCi)",
      licensePlate = "07841-120-31",
      clientName = "Flotte Entreprise BTP",
      clientPhone = "0560 12 99 88",
      scheduledTime = "15:30",
      estimatedMinutes = 40,
      priority = MaintenanceTaskPriority.FLOTTE,
      status = MaintenanceTaskStatus.EN_ATTENTE,
      assignedMechanic = "Sofiane R.",
      bayNumber = 3,
      requiredPartsSummary = "Ampoules H7 Osram + Balais essuie-glace Valeo",
      partsAvailable = true,
      notes = "Visite technique périodique obligatoire demain matin"
    ),
    DailyMaintenanceTask(
      id = "TASK-06",
      taskTitle = "Calibrage Caméra Frontale ADAS & Radar ACC",
      category = "Diagnostic & ADAS",
      vehicleModel = "Volkswagen Tiguan II (2.0 TDI)",
      licensePlate = "01482-121-16",
      clientName = "Karim Benali",
      clientPhone = "0550 88 77 66",
      scheduledTime = "16:30",
      estimatedMinutes = 90,
      priority = MaintenanceTaskPriority.NORMAL,
      status = MaintenanceTaskStatus.EN_ATTENTE,
      assignedMechanic = "Amine K.",
      bayNumber = 4,
      requiredPartsSummary = "Cible étalonnage VAG + Prise OBD diagnostic UDS",
      partsAvailable = true,
      notes = "Suite au remplacement du pare-brise par Carglass"
    ),
    DailyMaintenanceTask(
      id = "TASK-07",
      taskTitle = "Purge Complète Liquide de Frein DOT4 sous Pression",
      category = "Freinage & Sécurité",
      vehicleModel = "Peugeot 208 II (1.2 PureTech)",
      licensePlate = "19402-121-16",
      clientName = "Sarah Haddad",
      clientPhone = "0555 77 88 99",
      scheduledTime = "08:15",
      estimatedMinutes = 35,
      priority = MaintenanceTaskPriority.NORMAL,
      status = MaintenanceTaskStatus.TERMINE,
      assignedMechanic = "Sofiane R.",
      bayNumber = 3,
      requiredPartsSummary = "Bidon 1L Liquide de frein haute température DOT4",
      partsAvailable = true,
      notes = "Purge sous pression 2 bars terminée et essai concluant"
    )
  )

  // Pricing Plans (Algérie DZD)
  val pricingPlans: List<PricingPlan> = listOf(
    PricingPlan(
      id = "plan_particulier",
      title = "Compte Particulier Basique (DIY)",
      subtitle = "Pour particuliers qui réparent & entretiennent leur véhicule eux-mêmes",
      priceDzd = "3 500 DA",
      billingPeriod = "par an (3 000 - 4 000 DA)",
      targetUser = "Clients Particuliers & Auto-Entretien",
      features = listOf(
        "🚗 1 seul numéro de châssis (VIN) inclus dans l'abonnement de base",
        "➕ Ajout d'un autre numéro de châssis : +1 200 DA (1 000 à 1 500 DA / VIN)",
        "⚙️ Accès illimité aux pièces de rechange & câblages via votre châssis",
        "🛒 Panier intelligent Schéma Éclaté : références compatibles & stock vendeurs/ateliers",
        "🛠️ Accès guidé aux schémas de réparation, ordres de réparation & méthodes DIY",
        "🗺️ Carte Google Maps exacte des vendeurs, ateliers (prix montage) & showrooms"
      ),
      isPopular = true,
      badgeText = "NOUVEAU • COMPTE PARTICULIER 3 500 DA"
    ),
    PricingPlan(
      id = "plan_particulier_libre_10000",
      title = "Compte Particulier • Accès Libre Intégral",
      subtitle = "Accès total sans restriction aux schémas électriques, manuels & multi-véhicules",
      priceDzd = "10 000 DA",
      billingPeriod = "par an",
      targetUser = "Particuliers Experts & Passionnés",
      features = listOf(
        "🔓 Accès totalement libre à tous les schémas de réparation et câblages complets",
        "📋 Ordres de réparation complets, couples de serrage & procédures constructeur",
        "🚗 Jusqu'à 5 numéros de châssis (VIN) inclus + recherche multi-modèles libre",
        "🛒 Panier Schéma Éclaté illimité avec compatibilités OEM/IAM & stock temps réel",
        "🤖 Assistance Diagnostic MekanikAI & Auto-Réparation prioritaire"
      ),
      isPopular = false,
      badgeText = "ACCÈS LIBRE 10 000 DA"
    ),
    PricingPlan(
      id = "plan_pro_atelier",
      title = "Compte Atelier Pro",
      subtitle = "La solution complète pour garages et mécaniciens",
      priceDzd = "18 000 DA",
      billingPeriod = "par an",
      targetUser = "Garages & Mécaniciens",
      features = listOf(
        "📱 Multi-Appareils Atelier : Connexion sur 3 téléphones/tablettes max",
        "🔒 Double Facteur (2FA Google Authenticator / TOTP sécurisé)",
        "🏪 Option Passerelle Magasin + Atelier unifié (+5 000 DA/an)",
        "⚡ Recherches VIN, Plaques & Codes Moteur illimitées",
        "📐 Schémas techniques éclatés & couples de serrage (ETKA/HaynesPro)",
        "🧾 Gestion des Ordres de Réparation (OR) & Devis pro en DZD",
        "✍️ Signature numérique des devis sur tablette/smartphone",
        "🧠 Assistant Diagnostic MekanikAI & Valise OBD-II illimité",
        "📊 Statistiques Recharts & Suivi du chiffre d'affaires"
      ),
      isPopular = true,
      badgeText = "RECOMMANDÉ ATELIER"
    ),
    PricingPlan(
      id = "plan_ultra_pro",
      title = "Compte Entreprise Ultra Pro",
      subtitle = "Solution complète pour grands garages, concessions & distributeurs pièces",
      priceDzd = "38 000 DA",
      billingPeriod = "par an",
      targetUser = "Grands Garages, Concessions & Réseaux Multi-Équipes",
      features = listOf(
        "Gestion Hiérarchique Multi-Comptes (10 sous-comptes inclus)",
        "👑 1 Compte Gérant / Administrateur Général (Gouvernance & Droits)",
        "👤 2 Comptes Conseillers Clients (Accueil, Devis, Planning RDV)",
        "📦 1 Compte Administrateur Magasin (Gestion Stock & Inventaire Global)",
        "🛒 2 Sous-Comptes Vendeurs Magasin (Vente Pièces Comptoir uniquement)",
        "📊 1 Compte Comptable Principal (Trésorerie, TVA 19% & Bilans)",
        "🧾 2 Comptes Analystes Factures (Vérification factures & Impayés)",
        "🔍 1 Compte Auditeur Qualité & Contrôle Interne des Opérations",
        "Traçabilité intégrale & Journal d'Audit (Audit Trail) en temps réel",
        "Switch de profil instantané avec code PIN d'accès sécurisé",
        "Recherches VIN, schémas OEM et diagnostics MekanikAI illimités",
        "Assistance prioritaire 7j/7 et formations d'équipe incluses"
      ),
      isPopular = true,
      badgeText = "OFFRE ENTREPRISE ULTRA PRO"
    ),
    PricingPlan(
      id = "plan_mixte_atelier_magasin",
      title = "Compte Mixte : Atelier + Magasin de Pièces",
      subtitle = "Pour petites entreprises & magasins disposant aussi d'un atelier de montage mécanique",
      priceDzd = "23 000 DA",
      billingPeriod = "par an (ou 2 200 DA/mois • 1 Principal + 4 Sous-Comptes)",
      targetUser = "Magasins de Pièces avec Atelier de Montage",
      features = listOf(
        "👑 1 Compte Principal Gérant + 3 ou 4 Sous-Comptes inclus (Chef Atelier, Mécanicien Monteur, Vendeur Comptoir, Magasinier)",
        "🔐 Double Facteur (2FA Google Authenticator) pour ajouter/supprimer des sous-comptes & anti-clonage",
        "🛠️ 100% des options ATELIER : Ordres de réparation (OR), Devis Montage, DMS T1/T2/T3, Cache Pannes, OBD-II & Vocale",
        "🏪 100% des options MAGASIN : Catalogue OEM, Ajout Pièce Store Photo IA, Scanner Étiquette QR/EAN & Gestion Stock",
        "🔄 Imputation directe d'une pièce du magasin vers le pont de montage atelier",
        "🛡️ MécaVerif+™ (VerifyCar) externe hors abonnement : 250 DA par numéro de châssis"
      ),
      isPopular = true,
      badgeText = "NOUVEAU • COMPTE MIXTE (ATELIER + MAGASIN)"
    ),
    PricingPlan(
      id = "plan_carrosserie_tolerie",
      title = "Compte Spécial Carrosserie & Tôlerie",
      subtitle = "Dédié aux tôliers, ateliers de carrosserie et cabines de peinture",
      priceDzd = "19 500 DA",
      billingPeriod = "par an (ou 1 800 DA/mois)",
      targetUser = "Tôliers, Carrossiers & Peintres Automobiles",
      features = listOf(
        "🚘 Accès par N° de Châssis (VIN) aux pièces de tôlerie, plastique, pare-chocs, ailes, capots, phares, vitrages & agrafes",
        "📸 Obligation certifiée de prendre les photos AVANT et APRÈS les réparations de carrosserie",
        "🔗 Synchronisation directe du dossier photos Avant/Après avec l'historique national MécaVerif+™",
        "🔐 Double Facteur (2FA Google Authenticator) pour l'administration du compte et des sous-comptes tôliers",
        "🧾 Devis Carrosserie, Marbre Laser, Micromètre (µm) & Peinture Étuve",
        "🛡️ MécaVerif+™ (VerifyCar) externe hors abonnement : 250 DA par numéro de châssis"
      ),
      isPopular = true,
      badgeText = "NOUVEAU • SPÉCIAL CARROSSERIE & TÔLERIE"
    ),
    PricingPlan(
      id = "plan_vendeur",
      title = "Espace Vendeur Pièces",
      subtitle = "Visibilité maximale auprès des garages algériens",
      priceDzd = "20 000 DA",
      billingPeriod = "par an (ou 1 700 DA/mois)",
      targetUser = "Magasins & Grossistes de Pièces",
      features = listOf(
        "Publication du stock et références OEM en ligne",
        "Ajouter une pièce au Store par Photo + IA (4 vues studio)",
        "Scanner Étiquette Pièce (Code-Barres / QR & Compatibilité VIN)",
        "Double Facteur (2FA Google Authenticator) pour gérer les sous-comptes vendeurs",
        "Badge Vendeur Vérifié avec Numéro de Registre (RC)",
        "MécaVerif+™ (VerifyCar) externe hors abonnement : 250 DA par numéro de châssis"
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

  /**
   * Recherche un véhicule par VIN avec mécanisme de 'fallback' automatique en cascade :
   * Si la 1ère API (ex: SinoAuto360) est indisponible (503/504/429) ou ne trouve pas le VIN (404),
   * le Repository interroge automatiquement la source suivante dans la liste (Dongchedi -> Car2DB -> ChineseCar.parts/CatCar -> lifeofcapo 142 marques -> TecDoc/ISO).
   */
  fun findVehicleByVinWithFallback(vinQuery: String): FallbackCascadeResolutionResult {
    return UnifiedAutoApiRepository.resolveVinWithFallbackCascade(vinQuery)
  }

  fun findVehicleByVin(vinQuery: String): Vehicle? {
    val clean = vinQuery.trim().uppercase()
    if (clean.isEmpty()) return null
    val fallbackResult = findVehicleByVinWithFallback(clean)
    return fallbackResult.resolvedVehicle
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
    if (clean.isEmpty()) return null
    obdFaults.find { it.code.equals(clean, ignoreCase = true) }?.let { return it }
    val extractedCode = Regex("\\b([PCBU]\\d{4})\\b").find(clean)?.groupValues?.getOrNull(1)
    if (extractedCode != null) {
      obdFaults.find { it.code.equals(extractedCode, ignoreCase = true) }?.let { return it }
    }
    return obdFaults.find {
      clean.contains(it.code, ignoreCase = true) ||
        it.standardTitle.contains(clean, ignoreCase = true)
    }
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

  // Live Repair Tracking Session (Atelier en direct)
  val sampleLiveRepairSession = LiveRepairTrackingSession(
    trackingId = "TRK-2026-ALG-8891",
    vehicleVin = "WVWZZZ5NZMW123456",
    vehicleName = "VW Tiguan II 2.0 TDI (2021)",
    plateNumber = "08745-121-16",
    clientName = "Mourad Belkacemi",
    clientPhone = "+213 550 12 34 56",
    workshopName = "Garage Mekanik+ Centre d'Excellence Alger",
    currentStageName = "Sur le Pont Élévateur (Remplacement Disques & Plaquettes)",
    progressPercent = 65,
    estimatedReadyTime = "Aujourd'hui à 17:30",
    totalEstimatedPriceDzd = 28500.0,
    steps = listOf(
      RepairTimelineStep(
        stepNumber = 1,
        title = "Réception & Inspection Entrante",
        description = "Check-in du véhicule, relevé kilométrique (94 200 km), état carrosserie et scan électronique initial.",
        timestamp = "08:30",
        status = RepairStepStatus.COMPLETED,
        technicianName = "Karim B. (Chef d'atelier)"
      ),
      RepairTimelineStep(
        stepNumber = 2,
        title = "Diagnostic OBD-II & Dépistage",
        description = "Vérification des calculateurs ABS/ESP et mesure de l'épaisseur résiduelle des disques (3.2 mm sous cote mini).",
        timestamp = "09:45",
        status = RepairStepStatus.COMPLETED,
        technicianName = "Sofiane T. (Électronicien)"
      ),
      RepairTimelineStep(
        stepNumber = 3,
        title = "Réception des Pièces d'Origine OEM",
        description = "Disques ventilés Brembo Ø312mm et plaquettes céramiques livrés depuis le magasin de Bab Ezzouar.",
        timestamp = "11:15",
        status = RepairStepStatus.COMPLETED,
        technicianName = "Magasinier Atelier"
      ),
      RepairTimelineStep(
        stepNumber = 4,
        title = "Intervention Mécanique sur le Pont",
        description = "Démontage des étriers, nettoyage des moyeux, serrage au couple dynamométrique 110 Nm et purge du liquide de frein DOT 4.",
        timestamp = "En cours (14:15)",
        status = RepairStepStatus.IN_PROGRESS,
        technicianName = "Amine M. (Mécanicien Senior)"
      ),
      RepairTimelineStep(
        stepNumber = 5,
        title = "Contrôle Qualité & Essai Routier",
        description = "Vérification de l'alignement, calibrage du capteur de pression pédale et rodage initial.",
        timestamp = "Prévu à 16:45",
        status = RepairStepStatus.PENDING
      ),
      RepairTimelineStep(
        stepNumber = 6,
        title = "Véhicule Prêt pour Restitution",
        description = "Nettoyage compartiment moteur, remise des anciennes pièces remplacées et délivrance de la facture certifiée.",
        timestamp = "Prévu à 17:30",
        status = RepairStepStatus.PENDING
      )
    )
  )

  // Certified Digital Service Passport (Carnet d'entretien numérique infalsifiable)
  val sampleCertifiedPassport = CertifiedServicePassport(
    vehicleVin = "WVWZZZ5NZMW123456",
    vehicleModel = "Volkswagen Tiguan II Facelift 2.0 TDI 150ch",
    firstRegistrationYear = 2021,
    currentMileageKm = 94200,
    ownerName = "Mourad Belkacemi",
    qrValidationCode = "MK-CERT-2026-WVW5NZ-OK",
    records = listOf(
      DigitalServiceRecord(
        recordId = "REC-94K",
        date = "17 Août 2026",
        mileageKm = 94200,
        operationTitle = "Remplacement Freinage Complet Avant & Purge",
        performedTasks = listOf("Remplacement disques Brembo Ø312mm", "Pose plaquettes céramique OEM", "Purge sous pression DOT 4 LV"),
        partsReplaced = listOf("5Q0 615 301 F (Disques)", "5Q0 698 151 BR (Plaquettes)"),
        totalCostDzd = 28500.0,
        garageName = "Mekanik+ Alger Centre",
        certHash = "SHA256:7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069"
      ),
      DigitalServiceRecord(
        recordId = "REC-80K",
        date = "15 Mars 2026",
        mileageKm = 80150,
        operationTitle = "Vidange Moteur LongLife & Pack 4 Filtres",
        performedTasks = listOf("Vidange huile Motul Specific 504.00/507.00 5W-30", "Filtre à huile", "Filtre à air", "Filtre à gasoil", "Filtre habitacle charbon actif"),
        partsReplaced = listOf("03N 115 562 B", "5Q0 129 620 B", "5Q0 127 177 D", "5Q0 819 653"),
        totalCostDzd = 24800.0,
        garageName = "Mekanik+ Alger Centre",
        certHash = "SHA256:4b227777d4dd1fc61c6f884f48641d02b4d121d3fd328cb08b5531fcacdabf8a"
      ),
      DigitalServiceRecord(
        recordId = "REC-60K",
        date = "10 Octobre 2025",
        mileageKm = 60500,
        operationTitle = "Vidange Boîte de Vitesses Automatique DSG7 (DQ381)",
        performedTasks = listOf("Vidange fluide DSG d'origine VW G 055 529", "Remplacement crépine et filtre boîte hydraulique", "Réapprentissage des embrayages au banc"),
        partsReplaced = listOf("G 055 529 A2 (6L)", "0GC 325 183 A (Filtre)"),
        totalCostDzd = 38000.0,
        garageName = "Atelier Spécialiste VAG Cheraga",
        certHash = "SHA256:ef2d127de37b942baad06145e54b0c619a1f22327b2ebbcfbec78f5564afe39d"
      )
    )
  )

  // Fleet Management (Flotte d'Entreprise)
  val sampleFleetVehicles: List<FleetVehicle> = listOf(
    FleetVehicle(
      id = "FLT-01",
      plateNumber = "08745-121-16",
      brandModel = "VW Tiguan II 2.0 TDI",
      vin = "WVWZZZ5NZMW123456",
      assignedDriver = "Redha Slimani",
      driverPhone = "+213 555 10 20 30",
      currentMileageKm = 94200,
      nextServiceKm = 100000,
      insuranceExpiryDate = "15/12/2026 (Alliance Assurances)",
      technicalControlDate = "20/11/2026",
      vignetteTaxYear = 2026,
      fuelConsumptionAvg = 6.4,
      status = FleetVehicleStatus.EN_SERVICE,
      monthlyMaintenanceCostDzd = 14200.0
    ),
    FleetVehicle(
      id = "FLT-02",
      plateNumber = "12450-120-16",
      brandModel = "Peugeot 308 III 1.5 BlueHDi",
      vin = "VF3FPHNSSMY001234",
      assignedDriver = "Hichem Mansouri",
      driverPhone = "+213 661 44 55 66",
      currentMileageKm = 68400,
      nextServiceKm = 70000,
      insuranceExpiryDate = "05/09/2026 (CAAT)",
      technicalControlDate = "10/10/2026",
      vignetteTaxYear = 2026,
      fuelConsumptionAvg = 5.1,
      status = FleetVehicleStatus.CONTROLE_REQUIS,
      monthlyMaintenanceCostDzd = 8900.0
    ),
    FleetVehicle(
      id = "FLT-03",
      plateNumber = "00456-119-31",
      brandModel = "Renault Clio V 1.5 dCi",
      vin = "VF1RJA00067890123",
      assignedDriver = "Khaled Zerrouki",
      driverPhone = "+213 770 99 88 77",
      currentMileageKm = 125000,
      nextServiceKm = 130000,
      insuranceExpiryDate = "18/01/2027 (SAA)",
      technicalControlDate = "14/02/2027",
      vignetteTaxYear = 2026,
      fuelConsumptionAvg = 4.8,
      status = FleetVehicleStatus.EN_SERVICE,
      monthlyMaintenanceCostDzd = 6500.0
    ),
    FleetVehicle(
      id = "FLT-04",
      plateNumber = "09812-122-25",
      brandModel = "Toyota Hilux 2.4 D-4D 4x4",
      vin = "AHTBA3CD200987654",
      assignedDriver = "Youcef Benali",
      driverPhone = "+213 560 33 22 11",
      currentMileageKm = 142000,
      nextServiceKm = 145000,
      insuranceExpiryDate = "30/08/2026 (Cash Assurances)",
      technicalControlDate = "15/09/2026",
      vignetteTaxYear = 2026,
      fuelConsumptionAvg = 8.6,
      status = FleetVehicleStatus.EN_MAINTENANCE,
      monthlyMaintenanceCostDzd = 32000.0
    )
  )

  val sampleFleetSummary = FleetOverviewSummary(
    totalVehicles = 18,
    activeInService = 15,
    inMaintenance = 2,
    pendingAlertsCount = 3,
    totalMonthlyBudgetDzd = 284500.0,
    fleetCategory = "Parc VTC & Transport Express (Wilayas Centre-Ouest)"
  )

  // SOS Breakdown & Tow Trucks Database (Alger, Blida, Boumerdes, Oran, Constantine)
  val sampleTowTrucks: List<TowTruckService> = listOf(
    TowTruckService(
      id = "TOW-01",
      companyName = "SOS Dépannage 24/7 Alger Express",
      driverName = "Mourad Remorquage",
      phone = "+213 550 99 00 11",
      distanceKm = 3.2,
      estimatedArrivalMinutes = 15,
      baseRateDzd = 3500.0,
      wilayaLocation = "Alger (Autoroute de l'Est / Rocade Sud)",
      rating = 4.9f,
      isAvailable24_7 = true,
      truckType = "Plateau Basculant avec Treuil"
    ),
    TowTruckService(
      id = "TOW-02",
      companyName = "Assistance Rapide Mitidja Blida",
      driverName = "Tarek Depan",
      phone = "+213 661 88 77 66",
      distanceKm = 7.8,
      estimatedArrivalMinutes = 25,
      baseRateDzd = 4000.0,
      wilayaLocation = "Blida & Autoroute Chiffa",
      rating = 4.8f,
      isAvailable24_7 = true,
      truckType = "Dépanneuse 4x4 Tout Terrain"
    ),
    TowTruckService(
      id = "TOW-03",
      companyName = "Trans-Auto Dépannage Oran Ouest",
      driverName = "Farid El Bahia",
      phone = "+213 770 12 34 56",
      distanceKm = 12.0,
      estimatedArrivalMinutes = 35,
      baseRateDzd = 4500.0,
      wilayaLocation = "Oran & Zone Industrielle Arzew",
      rating = 4.7f,
      isAvailable24_7 = true,
      truckType = "Plateau Double Essieu"
    )
  )

  // Fiscal Invoices & Proformas
  val sampleInvoices: List<FiscalInvoice> = listOf(
    FiscalInvoice(
      invoiceNumber = "FCT-2026-0842",
      invoiceType = InvoiceType.FACTURE_DEFINITIVE,
      date = "17/08/2026",
      clientName = "Mourad Belkacemi",
      clientPhone = "+213 550 12 34 56",
      vehicleVin = "WVWZZZ5NZMW123456",
      vehicleModel = "VW Tiguan II 2.0 TDI (2021)",
      plateNumber = "08745-121-16",
      items = listOf(
        InvoiceLineItem("5Q0 615 301 F", "Jeu de disques de frein ventilés avant Brembo Ø312mm", 1, 16800.0),
        InvoiceLineItem("5Q0 698 151 BR", "Jeu de 4 plaquettes de frein avant OEM avec témoin", 1, 8500.0),
        InvoiceLineItem("MO-FREIN-01", "Main d'œuvre : Pose disques & plaquettes + purge circuit", 2, 2500.0, isLabor = true),
        InvoiceLineItem("LDR-DOT4", "Liquide de frein haute performance DOT 4 (1L)", 1, 1200.0)
      ),
      tvaPercent = 19.0,
      stampTaxDzd = 100.0,
      isPaid = true,
      paymentMethod = PaymentMethod.CIB_EDAHABIA
    ),
    FiscalInvoice(
      invoiceNumber = "PRF-2026-0914",
      invoiceType = InvoiceType.FACTURE_PROFORMA,
      date = "17/08/2026",
      clientName = "Entreprise Sarl Maghreb Logistique",
      clientPhone = "+213 23 45 67 89",
      vehicleVin = "VF3FPHNSSMY001234",
      vehicleModel = "Peugeot 308 III 1.5 BlueHDi",
      plateNumber = "12450-120-16",
      items = listOf(
        InvoiceLineItem("16 381 573 80", "Kit de chaîne d'arbre à cames 8mm renforcé OEM PSA", 1, 42000.0),
        InvoiceLineItem("16 289 261 80", "Kit courroie de distribution + Pompe à eau", 1, 28500.0),
        InvoiceLineItem("MO-DISTRI-02", "Main d'œuvre : Calage distribution & remplacement chaîne 8mm", 5, 3000.0, isLabor = true)
      ),
      tvaPercent = 19.0,
      stampTaxDzd = 100.0,
      isPaid = false,
      paymentMethod = null
    )
  )

  // B2B Marketplace Offers Comparison
  val sampleMarketplaceOffers: List<VendorPartOffer> = listOf(
    VendorPartOffer(
      vendorId = "VND-ALG-01",
      vendorName = "Sarl Auto Pièces Bab Ezzouar",
      wilaya = "Alger (16)",
      brandName = "Brembo (Italie)",
      priceDzd = 16800.0,
      inStockQuantity = 14,
      deliveryCarrier = "Yalidine Express (Livraison 24h)",
      deliveryCostDzd = 600.0,
      vendorRating = 4.9f,
      isCertifiedOem = true
    ),
    VendorPartOffer(
      vendorId = "VND-ALG-02",
      vendorName = "Grossiste Pièces Kouba VAG",
      wilaya = "Alger (16)",
      brandName = "Bosch Original",
      priceDzd = 15500.0,
      inStockQuantity = 8,
      deliveryCarrier = "ZR Express (Livraison 24h/48h)",
      deliveryCostDzd = 550.0,
      vendorRating = 4.8f,
      isCertifiedOem = true
    ),
    VendorPartOffer(
      vendorId = "VND-ORN-01",
      vendorName = "Comptoir Ouest Pièces Auto Oran",
      wilaya = "Oran (31)",
      brandName = "Febi Bilstein (Allemagne)",
      priceDzd = 14900.0,
      inStockQuantity = 22,
      deliveryCarrier = "Kazi Tour (Inter-Wilayas 48h)",
      deliveryCostDzd = 700.0,
      vendorRating = 4.7f,
      isCertifiedOem = false
    )
  )

  // Audio Acoustic Noise Analysis Samples (IA MekanikAI)
  val sampleAudioAnalyses: List<AudioNoiseAnalysis> = listOf(
    AudioNoiseAnalysis(
      audioId = "AUDIO-01",
      title = "Cliquetis Métallique Haute Fréquence au Ralenti",
      probableSource = "Poussoir hydraulique de soupape ou Injecteur Piézo 2.0 TDI",
      confidenceRate = 92,
      severity = "Moyenne (Surveillance recommandée)",
      acousticDescription = "Battement régulier à fréquence 14 Hz synchronisé avec le cycle de distribution demi-vitesse vilebrequin. Bruit disparaissant à 2000 tr/min.",
      recommendedAction = "Contrôler le débit de retour des injecteurs au banc et vérifier la pression d'huile au manomètre à chaud.",
      waveformSample = listOf(0.2f, 0.5f, 0.9f, 0.3f, 0.1f, 0.8f, 0.4f, 0.1f, 0.95f, 0.3f, 0.2f, 0.7f, 0.4f)
    ),
    AudioNoiseAnalysis(
      audioId = "AUDIO-02",
      title = "Sifflement Aigu à l'Accélération (Charge Turbo)",
      probableSource = "Fuite durite d'air de suralimentation ou jeu axial turbine turbo",
      confidenceRate = 96,
      severity = "Haute (Risque casse turbo si ignoré)",
      acousticDescription = "Sifflement aérodynamique débutant à 1600 tr/min atteignant 85 dB lors de la montée en pression du turbocompresseur (P0299 potentiel).",
      recommendedAction = "Inspecter la durite échangeur d'air (intercooler) et contrôler le jeu axial/radial de l'axe de turbine côté compresseur.",
      waveformSample = listOf(0.1f, 0.3f, 0.6f, 0.85f, 0.9f, 0.98f, 0.95f, 0.88f, 0.6f, 0.4f, 0.2f, 0.1f)
    )
  )

  // --- Monthly Workshop Repair Statistics Dataset (Recharts Engine) ---
  val monthlyRepairStats: List<MonthlyRepairStats> = listOf(
    MonthlyRepairStats(
      monthKey = "2026-01",
      monthShort = "Jan",
      monthFull = "Janvier 2026",
      totalRepairs = 42,
      freinageRepairs = 12,
      moteurTurboRepairs = 9,
      distributionRepairs = 6,
      climatisationRepairs = 2,
      electriciteRepairs = 5,
      vidangeRevisionRepairs = 8,
      revenueDzd = 1420000L,
      partsRevenueDzd = 890000L,
      laborRevenueDzd = 530000L,
      laborHoursWorked = 212.0f,
      avgTicketDzd = 33810L,
      vehiclesHandled = 39,
      customerSatisfactionPct = 98.2f,
      diagnosticAiAssistedCount = 28
    ),
    MonthlyRepairStats(
      monthKey = "2026-02",
      monthShort = "Fév",
      monthFull = "Février 2026",
      totalRepairs = 48,
      freinageRepairs = 14,
      moteurTurboRepairs = 11,
      distributionRepairs = 7,
      climatisationRepairs = 3,
      electriciteRepairs = 5,
      vidangeRevisionRepairs = 8,
      revenueDzd = 1680000L,
      partsRevenueDzd = 1040000L,
      laborRevenueDzd = 640000L,
      laborHoursWorked = 246.0f,
      avgTicketDzd = 35000L,
      vehiclesHandled = 45,
      customerSatisfactionPct = 97.8f,
      diagnosticAiAssistedCount = 33
    ),
    MonthlyRepairStats(
      monthKey = "2026-03",
      monthShort = "Mar",
      monthFull = "Mars 2026",
      totalRepairs = 56,
      freinageRepairs = 16,
      moteurTurboRepairs = 13,
      distributionRepairs = 9,
      climatisationRepairs = 4,
      electriciteRepairs = 6,
      vidangeRevisionRepairs = 8,
      revenueDzd = 1940000L,
      partsRevenueDzd = 1210000L,
      laborRevenueDzd = 730000L,
      laborHoursWorked = 285.5f,
      avgTicketDzd = 34642L,
      vehiclesHandled = 52,
      customerSatisfactionPct = 98.5f,
      diagnosticAiAssistedCount = 41
    ),
    MonthlyRepairStats(
      monthKey = "2026-04",
      monthShort = "Avr",
      monthFull = "Avril 2026",
      totalRepairs = 62,
      freinageRepairs = 18,
      moteurTurboRepairs = 14,
      distributionRepairs = 10,
      climatisationRepairs = 6,
      electriciteRepairs = 6,
      vidangeRevisionRepairs = 8,
      revenueDzd = 2190000L,
      partsRevenueDzd = 1370000L,
      laborRevenueDzd = 820000L,
      laborHoursWorked = 318.0f,
      avgTicketDzd = 35322L,
      vehiclesHandled = 58,
      customerSatisfactionPct = 98.9f,
      diagnosticAiAssistedCount = 49
    ),
    MonthlyRepairStats(
      monthKey = "2026-05",
      monthShort = "Mai",
      monthFull = "Mai 2026",
      totalRepairs = 71,
      freinageRepairs = 19,
      moteurTurboRepairs = 15,
      distributionRepairs = 11,
      climatisationRepairs = 11,
      electriciteRepairs = 7,
      vidangeRevisionRepairs = 8,
      revenueDzd = 2580000L,
      partsRevenueDzd = 1620000L,
      laborRevenueDzd = 960000L,
      laborHoursWorked = 368.0f,
      avgTicketDzd = 36338L,
      vehiclesHandled = 66,
      customerSatisfactionPct = 99.1f,
      diagnosticAiAssistedCount = 56
    ),
    MonthlyRepairStats(
      monthKey = "2026-06",
      monthShort = "Juin",
      monthFull = "Juin 2026",
      totalRepairs = 84,
      freinageRepairs = 23,
      moteurTurboRepairs = 18,
      distributionRepairs = 13,
      climatisationRepairs = 15,
      electriciteRepairs = 7,
      vidangeRevisionRepairs = 8,
      revenueDzd = 3120000L,
      partsRevenueDzd = 1960000L,
      laborRevenueDzd = 1160000L,
      laborHoursWorked = 430.0f,
      avgTicketDzd = 37142L,
      vehiclesHandled = 79,
      customerSatisfactionPct = 98.7f,
      diagnosticAiAssistedCount = 68
    ),
    MonthlyRepairStats(
      monthKey = "2026-07",
      monthShort = "Juil",
      monthFull = "Juillet 2026",
      totalRepairs = 92,
      freinageRepairs = 25,
      moteurTurboRepairs = 20,
      distributionRepairs = 14,
      climatisationRepairs = 18,
      electriciteRepairs = 7,
      vidangeRevisionRepairs = 8,
      revenueDzd = 3450000L,
      partsRevenueDzd = 2170000L,
      laborRevenueDzd = 1280000L,
      laborHoursWorked = 475.0f,
      avgTicketDzd = 37500L,
      vehiclesHandled = 87,
      customerSatisfactionPct = 99.2f,
      diagnosticAiAssistedCount = 74
    ),
    MonthlyRepairStats(
      monthKey = "2026-08",
      monthShort = "Août",
      monthFull = "Août 2026 (En cours)",
      totalRepairs = 88,
      freinageRepairs = 24,
      moteurTurboRepairs = 19,
      distributionRepairs = 14,
      climatisationRepairs = 16,
      electriciteRepairs = 7,
      vidangeRevisionRepairs = 8,
      revenueDzd = 3280000L,
      partsRevenueDzd = 2060000L,
      laborRevenueDzd = 1220000L,
      laborHoursWorked = 452.0f,
      avgTicketDzd = 37272L,
      vehiclesHandled = 83,
      customerSatisfactionPct = 99.0f,
      diagnosticAiAssistedCount = 71
    ),
    MonthlyRepairStats(
      monthKey = "2026-09",
      monthShort = "Sep",
      monthFull = "Septembre 2026 (Prévision)",
      totalRepairs = 78,
      freinageRepairs = 22,
      moteurTurboRepairs = 17,
      distributionRepairs = 12,
      climatisationRepairs = 11,
      electriciteRepairs = 7,
      vidangeRevisionRepairs = 9,
      revenueDzd = 2890000L,
      partsRevenueDzd = 1810000L,
      laborRevenueDzd = 1080000L,
      laborHoursWorked = 405.0f,
      avgTicketDzd = 37051L,
      vehiclesHandled = 74,
      customerSatisfactionPct = 98.8f,
      diagnosticAiAssistedCount = 62
    ),
    MonthlyRepairStats(
      monthKey = "2026-10",
      monthShort = "Oct",
      monthFull = "Octobre 2026 (Prévision)",
      totalRepairs = 74,
      freinageRepairs = 21,
      moteurTurboRepairs = 16,
      distributionRepairs = 12,
      climatisationRepairs = 8,
      electriciteRepairs = 8,
      vidangeRevisionRepairs = 9,
      revenueDzd = 2740000L,
      partsRevenueDzd = 1720000L,
      laborRevenueDzd = 1020000L,
      laborHoursWorked = 388.0f,
      avgTicketDzd = 37027L,
      vehiclesHandled = 70,
      customerSatisfactionPct = 98.9f,
      diagnosticAiAssistedCount = 59
    ),
    MonthlyRepairStats(
      monthKey = "2026-11",
      monthShort = "Nov",
      monthFull = "Novembre 2026 (Prévision)",
      totalRepairs = 69,
      freinageRepairs = 20,
      moteurTurboRepairs = 15,
      distributionRepairs = 11,
      climatisationRepairs = 5,
      electriciteRepairs = 9,
      vidangeRevisionRepairs = 9,
      revenueDzd = 2560000L,
      partsRevenueDzd = 1600000L,
      laborRevenueDzd = 960000L,
      laborHoursWorked = 362.0f,
      avgTicketDzd = 37101L,
      vehiclesHandled = 65,
      customerSatisfactionPct = 98.6f,
      diagnosticAiAssistedCount = 54
    ),
    MonthlyRepairStats(
      monthKey = "2026-12",
      monthShort = "Déc",
      monthFull = "Décembre 2026 (Prévision)",
      totalRepairs = 76,
      freinageRepairs = 22,
      moteurTurboRepairs = 17,
      distributionRepairs = 12,
      climatisationRepairs = 4,
      electriciteRepairs = 11,
      vidangeRevisionRepairs = 10,
      revenueDzd = 2860000L,
      partsRevenueDzd = 1790000L,
      laborRevenueDzd = 1070000L,
      laborHoursWorked = 398.0f,
      avgTicketDzd = 37631L,
      vehiclesHandled = 72,
      customerSatisfactionPct = 99.1f,
      diagnosticAiAssistedCount = 61
    )
  )

  fun getWorkshopKpiSummary(): WorkshopKpiSummary {
    val totalRepairs = monthlyRepairStats.sumOf { it.totalRepairs }
    val totalRev = monthlyRepairStats.sumOf { it.revenueDzd }
    val totalHours = monthlyRepairStats.sumOf { it.laborHoursWorked.toDouble() }.toFloat()
    val avgTicket = if (totalRepairs > 0) totalRev / totalRepairs else 0L

    return WorkshopKpiSummary(
      year = 2026,
      totalRepairsYear = totalRepairs,
      totalRevenueYearDzd = totalRev,
      totalLaborHoursYear = totalHours,
      averageTicketYearDzd = avgTicket,
      topSystemRepaired = "Système de Freinage (Disques & Plaquettes)",
      topSystemSharePct = 28,
      averageInterventionTimeHours = 2.4f,
      satisfactionRatePct = 98.8f,
      activeTechniciansCount = 4,
      monthlyGrowthPct = 14.6f
    )
  }

  fun getSystemBreakdownList(): List<SystemRepairBreakdown> {
    val totalRepairs = monthlyRepairStats.sumOf { it.totalRepairs }
    val freinage = monthlyRepairStats.sumOf { it.freinageRepairs }
    val moteur = monthlyRepairStats.sumOf { it.moteurTurboRepairs }
    val distribution = monthlyRepairStats.sumOf { it.distributionRepairs }
    val clim = monthlyRepairStats.sumOf { it.climatisationRepairs }
    val elec = monthlyRepairStats.sumOf { it.electriciteRepairs }
    val vidange = monthlyRepairStats.sumOf { it.vidangeRevisionRepairs }

    return listOf(
      SystemRepairBreakdown(
        systemName = "Freinage & Sécurité",
        categoryKey = "freinage",
        repairCount = freinage,
        percentage = (freinage * 100) / totalRepairs,
        totalRevenueDzd = 8650000L,
        colorHex = "#E53935", // Performance Red
        iconTag = "disc_full"
      ),
      SystemRepairBreakdown(
        systemName = "Moteur & Suralimentation (Turbo/Injecteurs)",
        categoryKey = "moteur",
        repairCount = moteur,
        percentage = (moteur * 100) / totalRepairs,
        totalRevenueDzd = 9840000L,
        colorHex = "#FF7A00", // Mechanical Orange
        iconTag = "engine"
      ),
      SystemRepairBreakdown(
        systemName = "Kit Distribution & Pompes à Eau",
        categoryKey = "distribution",
        repairCount = distribution,
        percentage = (distribution * 100) / totalRepairs,
        totalRevenueDzd = 6200000L,
        colorHex = "#00E5FF", // Tech Cyan
        iconTag = "sync"
      ),
      SystemRepairBreakdown(
        systemName = "Climatisation & Thermique Moteur",
        categoryKey = "clim",
        repairCount = clim,
        percentage = (clim * 100) / totalRepairs,
        totalRevenueDzd = 3100000L,
        colorHex = "#2979FF", // Ev Blue
        iconTag = "ac_unit"
      ),
      SystemRepairBreakdown(
        systemName = "Électricité & Capteurs OBD/FAP",
        categoryKey = "electricite",
        repairCount = elec,
        percentage = (elec * 100) / totalRepairs,
        totalRevenueDzd = 1850000L,
        colorHex = "#7C4DFF", // Purple Tech
        iconTag = "bolt"
      ),
      SystemRepairBreakdown(
        systemName = "Révisions Complètes & Vidanges 4 Filtres",
        categoryKey = "vidange",
        repairCount = vidange,
        percentage = (vidange * 100) / totalRepairs,
        totalRevenueDzd = 1030000L,
        colorHex = "#00E676", // Electric Green
        iconTag = "oil_barrel"
      )
    )
  }

  // Répartition du temps de travail hebdomadaire par compagnon (Atelier Dashboard Recharts/D3)
  val sampleCompanionWorkTimeAllocations: List<CompanionWorkTimeAllocation> = listOf(
    CompanionWorkTimeAllocation(
      id = "COMP-ALLOC-01",
      companionName = "Karim Benali",
      shortName = "Karim B.",
      matricule = "COMP-DZ-01",
      specialty = "Maître Mécanicien • Distribution & Moteur",
      bayName = "Pont Élévateur 1 (Rapide)",
      productiveRepairHours = 32.5f,
      diagnosticAndTestHours = 4.5f,
      waitingPartsHours = 2.0f,
      internalMaintenanceHours = 1.0f,
      soldBaremeHours = 46.0f
    ),
    CompanionWorkTimeAllocation(
      id = "COMP-ALLOC-02",
      companionName = "Yacine Mansouri",
      shortName = "Yacine M.",
      matricule = "COMP-DZ-02",
      specialty = "Technicien Boîte • Embrayage & Transmission",
      bayName = "Pont Élévateur 2 (Lourd)",
      productiveRepairHours = 28.0f,
      diagnosticAndTestHours = 3.5f,
      waitingPartsHours = 5.5f,
      internalMaintenanceHours = 2.0f,
      soldBaremeHours = 38.5f
    ),
    CompanionWorkTimeAllocation(
      id = "COMP-ALLOC-03",
      companionName = "Sofiane Rahmani",
      shortName = "Sofiane R.",
      matricule = "COMP-DZ-03",
      specialty = "Train Roulant • Suspension, Freins & Géométrie 3D",
      bayName = "Baie Géométrie 3D & Freins",
      productiveRepairHours = 34.0f,
      diagnosticAndTestHours = 3.0f,
      waitingPartsHours = 1.5f,
      internalMaintenanceHours = 1.5f,
      soldBaremeHours = 44.8f
    ),
    CompanionWorkTimeAllocation(
      id = "COMP-ALLOC-04",
      companionName = "Amine Khelif",
      shortName = "Amine K.",
      matricule = "COMP-DZ-04",
      specialty = "Électronicien Diag • Calculateurs, FAP & ADAS",
      bayName = "Baie Diagnostic & Hybride/EV",
      productiveRepairHours = 24.5f,
      diagnosticAndTestHours = 12.5f,
      waitingPartsHours = 1.5f,
      internalMaintenanceHours = 1.5f,
      soldBaremeHours = 43.2f
    )
  )

  // Taux de productivité hebdomadaire sur 7 jours (Atelier Dashboard Recharts/D3)
  val sampleWeeklyProductivityPoints: List<WeeklyProductivityDayPoint> = listOf(
    WeeklyProductivityDayPoint(
      dayCode = "Sam",
      dayFullLabel = "Samedi",
      soldBaremeHours = 31.5f,
      clockedRealHours = 28.0f,
      productivityPct = 113,
      targetPct = 100,
      vehiclesCompleted = 11,
      karimPct = 118,
      yacinePct = 104,
      sofianePct = 115,
      aminePct = 114
    ),
    WeeklyProductivityDayPoint(
      dayCode = "Dim",
      dayFullLabel = "Dimanche",
      soldBaremeHours = 29.0f,
      clockedRealHours = 27.5f,
      productivityPct = 105,
      targetPct = 100,
      vehiclesCompleted = 9,
      karimPct = 112,
      yacinePct = 96,
      sofianePct = 108,
      aminePct = 106
    ),
    WeeklyProductivityDayPoint(
      dayCode = "Lun",
      dayFullLabel = "Lundi",
      soldBaremeHours = 33.2f,
      clockedRealHours = 30.0f,
      productivityPct = 111,
      targetPct = 100,
      vehiclesCompleted = 12,
      karimPct = 116,
      yacinePct = 101,
      sofianePct = 114,
      aminePct = 112
    ),
    WeeklyProductivityDayPoint(
      dayCode = "Mar",
      dayFullLabel = "Mardi",
      soldBaremeHours = 27.8f,
      clockedRealHours = 29.0f,
      productivityPct = 96,
      targetPct = 100,
      vehiclesCompleted = 8,
      karimPct = 105,
      yacinePct = 86,
      sofianePct = 98,
      aminePct = 95
    ),
    WeeklyProductivityDayPoint(
      dayCode = "Mer",
      dayFullLabel = "Mercredi",
      soldBaremeHours = 34.5f,
      clockedRealHours = 30.5f,
      productivityPct = 113,
      targetPct = 100,
      vehiclesCompleted = 13,
      karimPct = 120,
      yacinePct = 103,
      sofianePct = 116,
      aminePct = 113
    ),
    WeeklyProductivityDayPoint(
      dayCode = "Jeu",
      dayFullLabel = "Jeudi",
      soldBaremeHours = 36.0f,
      clockedRealHours = 31.0f,
      productivityPct = 116,
      targetPct = 100,
      vehiclesCompleted = 14,
      karimPct = 122,
      yacinePct = 108,
      sofianePct = 119,
      aminePct = 115
    ),
    WeeklyProductivityDayPoint(
      dayCode = "Ven",
      dayFullLabel = "Vendredi (Permanence)",
      soldBaremeHours = 14.5f,
      clockedRealHours = 13.0f,
      productivityPct = 112,
      targetPct = 100,
      vehiclesCompleted = 5,
      karimPct = 115,
      yacinePct = 105,
      sofianePct = 114,
      aminePct = 112
    )
  )

  // ==========================================
  // --- 1. COMPTE CONDUCTEUR : DONNÉES & GUIDES ---
  // ==========================================

  val sampleDriverVehicleSummary = DriverVehicleSummary(
    plateNumber = "04829-121-16",
    brandModel = "Volkswagen Tiguan II 2.0 TDI (150 ch)",
    exactYear = 2021,
    engine = "2.0 TDI (Code: DFGA)",
    vin = "WVWZZZ5NZMW123456",
    currentMileageKm = 68450,
    lastServiceDate = "12 Mai 2026",
    nextServiceDueKm = 75000,
    technicalInspectionDue = "15 Janvier 2027",
    insuranceExpiry = "28 Octobre 2026",
    healthScorePct = 96
  )

  val sampleDriverRepairRecords: List<DriverRepairRecord> = listOf(
    DriverRepairRecord(
      id = "REP-DZ-2026-089",
      date = "12 Mai 2026",
      vehiclePlate = "04829-121-16",
      vehicleModel = "VW Tiguan 2.0 TDI 150ch",
      garageName = "Garage Auto Performance Alger",
      garageWilaya = "Alger (16)",
      interventionType = "Freinage & Révision Périodique",
      description = "Remplacement disques & plaquettes AV/AR Brembo + Vidange Synthétique 5W30 507.00 + Remplacement 4 filtres (Huile, Air, Gazole, Habitacle charbon).",
      replacedParts = listOf(
        "Plaquettes de frein avant Brembo P85126",
        "Disques de frein ventilés avant 312mm Brembo",
        "Plaquettes de frein arrière Brembo P85124",
        "Filtre à huile Mann HU7020z",
        "Filtre à carburant Mann PU8021",
        "Huile Motul 8100 X-clean+ 5W30 (5L)"
      ),
      laborHours = 3.5f,
      partsCostDzd = 48500L,
      laborCostDzd = 9500L,
      totalAmountDzd = 58000L,
      invoiceNumber = "FAC-2026-0512-04",
      isCertified = true,
      odometerKm = 64200
    ),
    DriverRepairRecord(
      id = "REP-DZ-2026-034",
      date = "18 Janvier 2026",
      vehiclePlate = "04829-121-16",
      vehicleModel = "VW Tiguan 2.0 TDI 150ch",
      garageName = "Service Rapide Bab Ezzouar",
      garageWilaya = "Alger (16)",
      interventionType = "Climatisation & Recharge Gaz",
      description = "Recharge fluide frigorigène R134a + Remplacement filtre déshydrateur + Traitement antibactérien des conduits de ventilation.",
      replacedParts = listOf(
        "Fluide frigorigène R134a (550g)",
        "Filtre d'habitacle anti-allergène Purflux AHC392",
        "Huile compresseur PAG46"
      ),
      laborHours = 1.5f,
      partsCostDzd = 12000L,
      laborCostDzd = 4500L,
      totalAmountDzd = 16500L,
      invoiceNumber = "FAC-2026-0118-12",
      isCertified = true,
      odometerKm = 58100
    ),
    DriverRepairRecord(
      id = "REP-DZ-2025-112",
      date = "04 Novembre 2025",
      vehiclePlate = "04829-121-16",
      vehicleModel = "VW Tiguan 2.0 TDI 150ch",
      garageName = "Atelier Élite VAG Blida",
      garageWilaya = "Blida (09)",
      interventionType = "Kit Distribution & Pompe à Eau",
      description = "Remplacement préventif courroie de distribution crantée, galets tendeurs, pompe à eau avec joint et purge complète liquide de refroidissement G13.",
      replacedParts = listOf(
        "Kit de distribution Continental Contitech CT1168K1",
        "Pompe à eau renforcée Dolz A245",
        "Liquide de refroidissement Febi G13 (7L)",
        "Courroie accessoires 6PK1070"
      ),
      laborHours = 5.0f,
      partsCostDzd = 62000L,
      laborCostDzd = 16000L,
      totalAmountDzd = 78000L,
      invoiceNumber = "FAC-2025-1104-09",
      isCertified = true,
      odometerKm = 51400
    ),
    DriverRepairRecord(
      id = "REP-DZ-2025-067",
      date = "22 Juin 2025",
      vehiclePlate = "04829-121-16",
      vehicleModel = "VW Tiguan 2.0 TDI 150ch",
      garageName = "Garage Auto Performance Alger",
      garageWilaya = "Alger (16)",
      interventionType = "Batterie AGM & Alternateur",
      description = "Remplacement batterie 12V AGM Start-Stop 70Ah 760A + Réapprentissage et codage BMS via valise ODIS.",
      replacedParts = listOf(
        "Batterie Varta Silver Dynamic AGM A7 (70Ah 760A)",
        "Nettoyage bornes et traitement anti-sulfate"
      ),
      laborHours = 1.0f,
      partsCostDzd = 28500L,
      laborCostDzd = 3000L,
      totalAmountDzd = 31500L,
      invoiceNumber = "FAC-2025-0622-02",
      isCertified = true,
      odometerKm = 42800
    )
  )

  val sampleDriverInvoices: List<DriverInvoiceItem> = listOf(
    DriverInvoiceItem(
      id = "INV-001",
      invoiceNumber = "FAC-2026-0512-04",
      date = "12/05/2026",
      garageName = "Garage Auto Performance Alger",
      garagePhone = "0550 12 34 56",
      vehicleInfo = "VW Tiguan 2.0 TDI (04829-121-16)",
      mileageKm = 64200,
      partsAmountDzd = 48500L,
      laborAmountDzd = 9500L,
      totalAmountDzd = 58000L,
      paymentMethod = "Carte CIB / Edahabia",
      status = "Payée • Facture Validée",
      itemsDetail = listOf(
        "Jeu plaquettes AV Brembo P85126 (14 500 DA)",
        "Jeu disques AV 312mm Brembo (19 000 DA)",
        "Jeu plaquettes AR Brembo P85124 (8 000 DA)",
        "Huile Motul 5W30 5L + Filtres (7 000 DA)",
        "Main d'œuvre mécanicien qualifié 3.5h (9 500 DA)"
      )
    ),
    DriverInvoiceItem(
      id = "INV-002",
      invoiceNumber = "FAC-2026-0118-12",
      date = "18/01/2026",
      garageName = "Service Rapide Bab Ezzouar",
      garagePhone = "0560 98 76 54",
      vehicleInfo = "VW Tiguan 2.0 TDI (04829-121-16)",
      mileageKm = 58100,
      partsAmountDzd = 12000L,
      laborAmountDzd = 4500L,
      totalAmountDzd = 16500L,
      paymentMethod = "Espèces avec Reçu Fiscal",
      status = "Payée • Archivée",
      itemsDetail = listOf(
        "Recharge Gaz R134a 550g (8 500 DA)",
        "Filtre habitacle Purflux (3 500 DA)",
        "Main d'œuvre tirage au vide & injection (4 500 DA)"
      )
    ),
    DriverInvoiceItem(
      id = "INV-003",
      invoiceNumber = "FAC-2025-1104-09",
      date = "04/11/2025",
      garageName = "Atelier Élite VAG Blida",
      garagePhone = "025 40 11 22",
      vehicleInfo = "VW Tiguan 2.0 TDI (04829-121-16)",
      mileageKm = 51400,
      partsAmountDzd = 62000L,
      laborAmountDzd = 16000L,
      totalAmountDzd = 78000L,
      paymentMethod = "Virement Bancaire BNA",
      status = "Payée • Garantie 1 An",
      itemsDetail = listOf(
        "Kit de distribution Contitech (38 000 DA)",
        "Pompe à eau renforcée Dolz (16 500 DA)",
        "Liquide refroidissement G13 Febi (7 500 DA)",
        "Main d'œuvre calage & montage 5.0h (16 000 DA)"
      )
    )
  )

  val sampleDriverMaintenanceMethods: List<DriverMaintenanceMethod> = listOf(
    DriverMaintenanceMethod(
      id = "METHOD-01",
      title = "Contrôle & Appoint des Niveaux Moteur (Huile, Liquide Refroidissement, Freins)",
      category = DriverGuideCategory.NIVEAUX_FLUIDES,
      difficulty = "Facile (Débutant)",
      durationMinutes = 10,
      estimatedSavingsDzd = 2500L,
      toolsNeeded = listOf("Chiffon propre non pelucheux", "Entonnoir propre", "Gants de protection"),
      safetyTips = listOf(
        "Opération à effectuer TOUJOURS sur sol plat et moteur FROID (au moins 15 min d'arrêt).",
        "Ne JAMAIS ouvrir le bouchon du vase d'expansion si le moteur est chaud (risque de brûlures par vapeur sous pression)."
      ),
      steps = listOf(
        MaintenanceStep(
          stepNumber = 1,
          title = "Niveau d'huile moteur",
          instruction = "Retirez la jauge orange/jaune, essuyez-la complètement avec un chiffon, réinsérez-la à fond pendant 3 secondes, puis retirez-la à nouveau. Le niveau doit se situer entre le repère MIN et MAX.",
          cautionTip = "Si le niveau est sous le MIN, ajoutez 0.5L d'huile homologuée constructeur (ex: 5W30 VW 507.00) puis revérifiez. Ne jamais dépasser le MAX."
        ),
        MaintenanceStep(
          stepNumber = 2,
          title = "Niveau liquide de refroidissement",
          instruction = "Vérifiez visuellement à travers le bocal transparent. Le niveau doit être entre le MIN et le MAX à froid.",
          cautionTip = "Utilisez uniquement du liquide homologué (rose/violet G12+/G13). Ne pas mélanger avec de l'eau du robinet."
        ),
        MaintenanceStep(
          stepNumber = 3,
          title = "Liquide de frein (DOT4 / DOT5.1)",
          instruction = "Contrôlez par transparence le réservoir au-dessus du maître-cylindre sans retirer le bouchon inutilement pour éviter l'humidité.",
          cautionTip = "Une baisse lente signale souvent l'usure naturelle des plaquettes. Une baisse brutale indique une fuite urgente."
        ),
        MaintenanceStep(
          stepNumber = 4,
          title = "Liquide lave-glace",
          instruction = "Remplissez le bocal (bouchon bleu) avec un liquide lave-glace démoustiquant ou antigel."
        )
      ),
      relatedPartKeyword = "Huile moteur 5W30",
      isRecommendedNow = true,
      intervalKmOrMonths = "Tous les 1 000 km ou avant long trajet"
    ),
    DriverMaintenanceMethod(
      id = "METHOD-02",
      title = "Vérification de la Pression & Usure des Pneus (Témoins & Bar/PSI)",
      category = DriverGuideCategory.FREINAGE_PNEUS,
      difficulty = "Facile (Débutant)",
      durationMinutes = 8,
      estimatedSavingsDzd = 4000L,
      toolsNeeded = listOf("Manomètre de pression", "Gonfleur / Station service", "Jauge de profondeur de sculpture"),
      safetyTips = listOf(
        "Prendre la pression à FROID (véhicule ayant roulé moins de 3 km à vitesse réduite).",
        "Ne jamais dégonfler un pneu chaud."
      ),
      steps = listOf(
        MaintenanceStep(
          stepNumber = 1,
          title = "Repérer les pressions prescrites",
          instruction = "Consultez l'étiquette collée sur le montant de portière conducteur ou à l'intérieur de la trappe à carburant (ex: 2.3 bar AV, 2.3 bar AR à vide / 2.6 bar en charge)."
        ),
        MaintenanceStep(
          stepNumber = 2,
          title = "Mesure et ajustement de pression",
          instruction = "Dévissez le bouchon de valve, enclenchez l'embout du manomètre bien droit, ajustez à la pression cible. N'oubliez pas la roue de secours (+0.3 bar)."
        ),
        MaintenanceStep(
          stepNumber = 3,
          title = "Contrôle des témoins d'usure (TWI)",
          instruction = "Inspectez les bossages de 1.6 mm au fond des rainures principales. Si la gomme arrive à fleur du témoin, le pneu doit être changé impérativement.",
          cautionTip = "Vérifiez aussi l'absence de hernie, coupure sur les flancs ou usure asymétrique (indiquant un défaut de parallélisme)."
        ),
        MaintenanceStep(
          stepNumber = 4,
          title = "Réinitialisation TPMS (Voyant Pression)",
          instruction = "Après gonflage, allez dans le menu Tableau de bord / Infodivertissement > Pneus > Enregistrer la pression."
        )
      ),
      relatedPartKeyword = "Plaquettes de frein",
      isRecommendedNow = true,
      intervalKmOrMonths = "Tous les mois & avant chaque départ autoroute"
    ),
    DriverMaintenanceMethod(
      id = "METHOD-03",
      title = "Remplacement du Filtre d'Habitacle (Anti-Pollen / Charbon Actif)",
      category = DriverGuideCategory.FILTRES_MOTEUR,
      difficulty = "Facile (10 min)",
      durationMinutes = 10,
      estimatedSavingsDzd = 3000L,
      toolsNeeded = listOf("Tournevis Torx T20 (selon modèle) ou à la main"),
      safetyTips = listOf(
        "Couper le contact avant d'accéder au boîtier.",
        "Respecter impérativement le sens des flèches 'AIR FLOW' imprimées sur le filtre neuf."
      ),
      steps = listOf(
        MaintenanceStep(
          stepNumber = 1,
          title = "Accéder à l'emplacement du filtre",
          instruction = "Le filtre d'habitacle se trouve soit derrière la boîte à gants (démontage de 2 clips latéraux), soit sous la planche de bord côté passager."
        ),
        MaintenanceStep(
          stepNumber = 2,
          title = "Retirer la trappe d'accès",
          instruction = "Déclipsez le couvercle rectangulaire en tirant doucement sur les ergots de fixation."
        ),
        MaintenanceStep(
          stepNumber = 3,
          title = "Extraire l'ancien filtre et nettoyer",
          instruction = "Sortez l'ancien filtre encrassé. Aspirez ou essuyez les feuilles mortes et poussières accumulées dans le logement."
        ),
        MaintenanceStep(
          stepNumber = 4,
          title = "Insérer le filtre neuf et refermer",
          instruction = "Glissez le filtre neuf en veillant à orienter la flèche 'Air Flow' vers le bas (ou vers l'habitacle). Replacez la trappe et vérifiez le flux d'air en allumant la ventilation.",
          cautionTip = "Un filtre propre élimine les mauvaises odeurs de clim et préserve le moteur de soufflerie."
        )
      ),
      relatedPartKeyword = "Filtre d'habitacle",
      isRecommendedNow = false,
      intervalKmOrMonths = "Tous les 15 000 km ou 1 an"
    ),
    DriverMaintenanceMethod(
      id = "METHOD-04",
      title = "Changement des Balais d'Essuie-Glace Avant & Arrière",
      category = DriverGuideCategory.VISIBILITE_ECLAIRAGE,
      difficulty = "Très Facile (5 min)",
      durationMinutes = 5,
      estimatedSavingsDzd = 2000L,
      toolsNeeded = listOf("Chiffon doux à placer sur le pare-brise"),
      safetyTips = listOf(
        "TRÈS IMPORTANT : Posez TOUJOURS un chiffon épais ou un carton sur le pare-brise sous le bras métallique. Si le bras nu se rabat brutalement, il brise le pare-brise."
      ),
      steps = listOf(
        MaintenanceStep(
          stepNumber = 1,
          title = "Position de maintenance",
          instruction = "Sur les véhicules récents (VAG, Renault, Peugeot), mettez le contact, coupez-le, puis actionnez le commodo d'essuie-glace vers le bas pour que les balais se positionnent verticalement."
        ),
        MaintenanceStep(
          stepNumber = 2,
          title = "Déclipser le balai usagé",
          instruction = "Soulevez le bras, appuyez sur le bouton carré de déverrouillage central de l'adaptateur et faites glisser le balai hors du bras."
        ),
        MaintenanceStep(
          stepNumber = 3,
          title = "Installer le balai neuf",
          instruction = "Enclenchez le nouveau balai dans le rail jusqu'à entendre un 'CLIC' franc de verrouillage."
        ),
        MaintenanceStep(
          stepNumber = 4,
          title = "Test sur pare-brise mouillé",
          instruction = "Reposez délicatement le bras. Activez le lave-glace pour vérifier l'absence de traces, grincements ou zones sautillantes."
        )
      ),
      relatedPartKeyword = "Balais d'essuie-glace",
      isRecommendedNow = false,
      intervalKmOrMonths = "Chaque année avant la saison des pluies"
    ),
    DriverMaintenanceMethod(
      id = "METHOD-05",
      title = "Entretien de la Batterie 12V & Démarrage de Secours avec Câbles",
      category = DriverGuideCategory.ELECTRICITE_BATTERIE,
      difficulty = "Facile à Moyen",
      durationMinutes = 12,
      estimatedSavingsDzd = 3500L,
      toolsNeeded = listOf("Câbles de démarrage avec pinces cuivrées", "Brosse métallique", "Graisse pour cosses"),
      safetyTips = listOf(
        "Ne jamais approcher de flamme ou fumer près d'une batterie (dégagement d'hydrogène).",
        "Ne jamais inverser les polarités (+) et (-) sous peine de détruire le calculateur moteur et l'alternateur."
      ),
      steps = listOf(
        MaintenanceStep(
          stepNumber = 1,
          title = "Nettoyage des bornes (sulfate blanc)",
          instruction = "Si les bornes présentent une poudre blanche/bleue, nettoyez-les avec un chiffon humide et de l'eau tiède savonneuse, séchez et appliquez une fine pellicule de graisse technique."
        ),
        MaintenanceStep(
          stepNumber = 2,
          title = "Ordre de branchement des câbles (Règle d'or)",
          instruction = "1. Pince ROUGE sur le (+) de la batterie déchargée.\n2. Pince ROUGE sur le (+) de la batterie du véhicule dépanneur.\n3. Pince NOIRE sur le (-) de la batterie du dépanneur.\n4. Pince NOIRE sur une masse métallique non peinte du moteur du véhicule en panne (pas sur la borne -).",
          cautionTip = "Ne touchez pas les pinces rouges et noires entre elles lors de la manipulation."
        ),
        MaintenanceStep(
          stepNumber = 3,
          title = "Démarrage et temps de stabilisation",
          instruction = "Démarrez le véhicule donneur, laissez tourner à 2000 tr/min pendant 3 minutes, puis tentez de démarrer le véhicule en panne. Laissez tourner les deux moteurs 3 minutes."
        ),
        MaintenanceStep(
          stepNumber = 4,
          title = "Débranchement dans l'ordre inverse exact",
          instruction = "Débranchez dans l'ordre inverse : Masse noire dépanné > Pince noire dépanneur > Pince rouge dépanneur > Pince rouge dépanné. Roulez au moins 30 minutes pour recharger."
        )
      ),
      relatedPartKeyword = "Batterie 12V",
      isRecommendedNow = false,
      intervalKmOrMonths = "Contrôle tous les 6 mois & avant l'hiver"
    ),
    DriverMaintenanceMethod(
      id = "METHOD-06",
      title = "Comprendre & Réagir aux Voyants du Tableau de Bord (Couleurs Rouge / Orange)",
      category = DriverGuideCategory.VOYANTS_DIAGNOSTIC,
      difficulty = "Guide d'Analyse Essentiel",
      durationMinutes = 5,
      estimatedSavingsDzd = 15000L,
      toolsNeeded = listOf("Scanner OBD Mekanik+ ou Caméra IA"),
      safetyTips = listOf(
        "VOYANT ROUGE = ARRÊT IMMÉDIAT EN TOUTE SÉCURITÉ. Ne continuez pas à rouler au risque de casser le moteur.",
        "VOYANT ORANGE = ANOMALIE NON CRITIQUE À CONTRÔLER RAPIDEMENT."
      ),
      steps = listOf(
        MaintenanceStep(
          stepNumber = 1,
          title = "Voyant Pression d'Huile Rouge (Burette)",
          instruction = "Arrêt d'urgence moteur immédiat. Coupez le contact. Vérifiez la jauge. Si le niveau est bon, la pompe à huile ou le capteur a un défaut. Ne redémarrez pas, appelez une dépanneuse.",
          cautionTip = "Rouler sans pression d'huile détruit le moteur en moins de 60 secondes."
        ),
        MaintenanceStep(
          stepNumber = 2,
          title = "Voyant Température / Liquide de Refroidissement Rouge",
          instruction = "Le moteur surchauffe (> 110°C). Arrêtez-vous, allumez le chauffage habitacle à fond pour évacuer des calories, coupez le moteur et laissez refroidir 30 minutes avant d'ouvrir le capot."
        ),
        MaintenanceStep(
          stepNumber = 3,
          title = "Voyant Moteur Orange 'Check Engine' (Défaut OBD)",
          instruction = "Le calculateur a détecté une anomalie (sondes lambda, FAP, vanne EGR, ratés d'allumage). Vous pouvez rouler à allure modérée. Lancez un scan dans l'onglet MekanikAI pour lire le code P0xxx exact."
        ),
        MaintenanceStep(
          stepNumber = 4,
          title = "Voyant Préchauffage / Spirale Clignotante (Diesel TDI)",
          instruction = "Mode dégradé activé (perte de puissance). Indique un souci de capteur de suralimentation, capteur pédale ou vanne EGR. À faire diagnostiquer en atelier."
        )
      ),
      relatedPartKeyword = "Capteur OBD",
      isRecommendedNow = true,
      intervalKmOrMonths = "Guide permanent de sécurité"
    )
  )

  // ==========================================
  // --- 2. COMPTE LOCATION DE VOITURES : FLOTTE & CONTRATS ---
  // ==========================================

  val sampleRentalFleetSummary = RentalFleetSummary(
    agencyName = "Auto Rent Algiers Prestige",
    wilaya = "Alger (16) - Aéroport Houari Boumediene & Hydra",
    totalCars = 18,
    rentedCars = 15,
    availableCars = 2,
    inWorkshopCars = 1,
    occupancyRatePct = 83.3f,
    monthlyRentalRevenueDzd = 3850000L,
    monthlyMaintenanceExpenseDzd = 245000L,
    overdueMaintenanceAlertsCount = 2
  )

  val sampleRentalVehicles: List<RentalVehicle> = listOf(
    RentalVehicle(
      id = "RENT-CAR-01",
      plateNumber = "08234-122-16",
      brandModel = "Hyundai Accent 1.4 MPI GLS (100 ch)",
      category = RentalCategory.CITADINE,
      year = 2023,
      vin = "KMHCT41CPNU887123",
      fuelType = "Essence",
      transmission = "Boîte Manuelle 6",
      currentMileageKm = 46200,
      fuelLevelPct = 85,
      dailyRateDzd = 6500L,
      rentalStatus = RentalStatus.EN_LOCATION,
      currentRenterName = "Karim Benali (SARL Maghreb Tech)",
      currentContractId = "CTR-2026-0814",
      nextScheduledServiceKm = 50000,
      technicalControlExpiry = "14 Mars 2027",
      insuranceExpiry = "20 Décembre 2026",
      totalMaintenanceSpentDzd = 98000L,
      maintenanceHistory = listOf(
        RentalMaintenanceEntry(
          id = "MNT-HYU-01",
          date = "10 Juin 2026",
          mileageAtServiceKm = 40000,
          interventionType = "Vidange Huile 5W30 + 3 Filtres",
          garageName = "Service Pro Rapide Alger",
          partsReplaced = listOf("Huile Shell Helix 5W30 4L", "Filtre huile", "Filtre air"),
          costDzd = 14500L,
          invoiceRef = "FAC-HYU-40K",
          nextDueKm = 50000
        )
      )
    ),
    RentalVehicle(
      id = "RENT-CAR-02",
      plateNumber = "01945-121-16",
      brandModel = "Seat Ibiza 1.6 MPI Style (110 ch)",
      category = RentalCategory.CITADINE,
      year = 2022,
      vin = "VSSZZZ6JZNR091456",
      fuelType = "Essence",
      transmission = "Boîte Manuelle 5",
      currentMileageKm = 58900,
      fuelLevelPct = 100,
      dailyRateDzd = 7000L,
      rentalStatus = RentalStatus.DISPONIBLE,
      currentRenterName = null,
      currentContractId = null,
      nextScheduledServiceKm = 60000,
      technicalControlExpiry = "05 Mai 2027",
      insuranceExpiry = "12 Novembre 2026",
      totalMaintenanceSpentDzd = 135000L,
      maintenanceHistory = listOf(
        RentalMaintenanceEntry(
          id = "MNT-SEAT-01",
          date = "22 Juillet 2026",
          mileageAtServiceKm = 55000,
          interventionType = "Plaquettes de frein AV + Purge liquide",
          garageName = "Garage Auto Performance Alger",
          partsReplaced = listOf("Plaquettes Brembo P85072", "Liquide DOT4"),
          costDzd = 18000L,
          invoiceRef = "FAC-SEAT-55K",
          nextDueKm = 75000
        )
      )
    ),
    RentalVehicle(
      id = "RENT-CAR-03",
      plateNumber = "03312-123-16",
      brandModel = "Volkswagen Golf 8 2.0 TDI R-Line (150 ch)",
      category = RentalCategory.BERLINE_PREMIUM,
      year = 2024,
      vin = "WVWZZZCDZPW043219",
      fuelType = "Diesel",
      transmission = "Automatique DSG7",
      currentMileageKm = 31400,
      fuelLevelPct = 70,
      dailyRateDzd = 16000L,
      rentalStatus = RentalStatus.EN_LOCATION,
      currentRenterName = "Yassine Mansouri (Cabinet Audit)",
      currentContractId = "CTR-2026-0818",
      nextScheduledServiceKm = 35000,
      technicalControlExpiry = "18 Janvier 2028",
      insuranceExpiry = "18 Janvier 2027",
      totalMaintenanceSpentDzd = 64000L,
      maintenanceHistory = listOf(
        RentalMaintenanceEntry(
          id = "MNT-GOLF-01",
          date = "15 Avril 2026",
          mileageAtServiceKm = 20000,
          interventionType = "Service LongLife VAG 507.00",
          garageName = "Atelier Élite VAG Blida",
          partsReplaced = listOf("Huile Castrol Edge 5W30", "Filtre huile", "Filtre habitacle"),
          costDzd = 28000L,
          invoiceRef = "FAC-GOLF-20K",
          nextDueKm = 35000
        )
      )
    ),
    RentalVehicle(
      id = "RENT-CAR-04",
      plateNumber = "09412-122-16",
      brandModel = "Dacia Duster 1.5 dCi 4x4 Prestige (110 ch)",
      category = RentalCategory.SUV_4X4,
      year = 2023,
      vin = "UU1HSDJ9G58291044",
      fuelType = "Diesel",
      transmission = "Manuelle 6 avec réducteur",
      currentMileageKm = 72100,
      fuelLevelPct = 40,
      dailyRateDzd = 9500L,
      rentalStatus = RentalStatus.EN_REVISION_ATELIER,
      currentRenterName = null,
      currentContractId = null,
      nextScheduledServiceKm = 70000, // Dépassé de 2100 km -> Alerte !
      technicalControlExpiry = "12 Septembre 2026",
      insuranceExpiry = "30 Septembre 2026",
      totalMaintenanceSpentDzd = 182000L,
      maintenanceHistory = listOf(
        RentalMaintenanceEntry(
          id = "MNT-DUST-01",
          date = "18 Août 2026",
          mileageAtServiceKm = 72100,
          interventionType = "Kit Distribution + Pompe à eau + 4 Pneus All-Terrain",
          garageName = "Garage Auto Performance Alger",
          partsReplaced = listOf("Kit courroie Gates", "Pompe à eau", "4 Pneus Michelin Lattitude Cross"),
          costDzd = 86000L,
          invoiceRef = "FAC-DUST-72K",
          nextDueKm = 130000
        )
      )
    ),
    RentalVehicle(
      id = "RENT-CAR-05",
      plateNumber = "06718-121-16",
      brandModel = "Renault Symbol 1.6 16v Exception (105 ch)",
      category = RentalCategory.CITADINE,
      year = 2022,
      vin = "VF1LNB21469018241",
      fuelType = "Essence / GPL Gaz",
      transmission = "Manuelle 5",
      currentMileageKm = 88400,
      fuelLevelPct = 90,
      dailyRateDzd = 5500L,
      rentalStatus = RentalStatus.EN_LOCATION,
      currentRenterName = "Mohamed Cherif (Livraison Express)",
      currentContractId = "CTR-2026-0810",
      nextScheduledServiceKm = 90000,
      technicalControlExpiry = "01 Novembre 2026",
      insuranceExpiry = "15 Janvier 2027",
      totalMaintenanceSpentDzd = 142000L,
      maintenanceHistory = listOf()
    ),
    RentalVehicle(
      id = "RENT-CAR-06",
      plateNumber = "02199-123-16",
      brandModel = "Toyota Hilux Double Cabine 2.4 D-4D 4x4 (150 ch)",
      category = RentalCategory.UTILITAIRE_FOURGON,
      year = 2023,
      vin = "AHTFR22G609182390",
      fuelType = "Diesel",
      transmission = "Manuelle 6",
      currentMileageKm = 64300,
      fuelLevelPct = 80,
      dailyRateDzd = 18000L,
      rentalStatus = RentalStatus.EN_LOCATION,
      currentRenterName = "Entreprise Travaux Publics Sonel",
      currentContractId = "CTR-2026-0728",
      nextScheduledServiceKm = 70000,
      technicalControlExpiry = "20 Février 2027",
      insuranceExpiry = "10 Avril 2027",
      totalMaintenanceSpentDzd = 210000L,
      maintenanceHistory = listOf()
    ),
    RentalVehicle(
      id = "RENT-CAR-07",
      plateNumber = "05114-124-16",
      brandModel = "Peugeot 208 II 1.2 PureTech GT (100 ch)",
      category = RentalCategory.CITADINE,
      year = 2024,
      vin = "VR3UPHNSSNY819201",
      fuelType = "Essence",
      transmission = "Automatique EAT8",
      currentMileageKm = 19500,
      fuelLevelPct = 100,
      dailyRateDzd = 8500L,
      rentalStatus = RentalStatus.DISPONIBLE,
      currentRenterName = null,
      currentContractId = null,
      nextScheduledServiceKm = 25000,
      technicalControlExpiry = "14 Juillet 2028",
      insuranceExpiry = "14 Juillet 2027",
      totalMaintenanceSpentDzd = 32000L,
      maintenanceHistory = listOf()
    ),
    RentalVehicle(
      id = "RENT-CAR-08",
      plateNumber = "08801-124-16",
      brandModel = "Audi A3 Sportback 35 TFSI S-Line (150 ch)",
      category = RentalCategory.LUXE_SPORT,
      year = 2024,
      vin = "WAUZZZGY5PA029118",
      fuelType = "Mild-Hybrid Essence",
      transmission = "S-Tronic 7",
      currentMileageKm = 24100,
      fuelLevelPct = 95,
      dailyRateDzd = 24000L,
      rentalStatus = RentalStatus.EN_LOCATION,
      currentRenterName = "Dr. Amina Touati",
      currentContractId = "CTR-2026-0816",
      nextScheduledServiceKm = 30000,
      technicalControlExpiry = "22 Février 2028",
      insuranceExpiry = "22 Février 2027",
      totalMaintenanceSpentDzd = 55000L,
      maintenanceHistory = listOf()
    )
  )

  val sampleRentalContracts: List<RentalContract> = listOf(
    RentalContract(
      contractId = "CTR-2026-0814",
      vehicleId = "RENT-CAR-01",
      vehicleName = "Hyundai Accent 1.4 GLS",
      plateNumber = "08234-122-16",
      clientFullName = "Karim Benali",
      clientPhone = "0555 33 22 11",
      clientNationalId = "109823471892",
      clientDrivingLicense = "P-16/094821",
      startDate = "14 Août 2026",
      endDate = "24 Août 2026",
      durationDays = 10,
      initialKm = 45100,
      returnKm = null,
      depositAmountDzd = 40000L,
      dailyRateDzd = 6500L,
      totalRentDzd = 65000L,
      status = ContractStatus.EN_COURS,
      isDepositReturned = false,
      notes = "Caution bloquée par empreinte CIB. Véhicule propre, niveau carburant départ 100%."
    ),
    RentalContract(
      contractId = "CTR-2026-0818",
      vehicleId = "RENT-CAR-03",
      vehicleName = "Volkswagen Golf 8 2.0 TDI R-Line",
      plateNumber = "03312-123-16",
      clientFullName = "Yassine Mansouri (Cabinet Audit)",
      clientPhone = "0661 45 78 90",
      clientNationalId = "112938475610",
      clientDrivingLicense = "P-16/120934",
      startDate = "18 Août 2026",
      endDate = "22 Août 2026",
      durationDays = 4,
      initialKm = 30900,
      returnKm = null,
      depositAmountDzd = 100000L,
      dailyRateDzd = 16000L,
      totalRentDzd = 64000L,
      status = ContractStatus.EN_COURS,
      isDepositReturned = false,
      notes = "Départ VIP Aéroport d'Alger. Sièges cuir impeccables."
    ),
    RentalContract(
      contractId = "CTR-2026-0816",
      vehicleId = "RENT-CAR-08",
      vehicleName = "Audi A3 Sportback 35 TFSI S-Line",
      plateNumber = "08801-124-16",
      clientFullName = "Dr. Amina Touati",
      clientPhone = "0770 99 88 77",
      clientNationalId = "104928174563",
      clientDrivingLicense = "P-16/083419",
      startDate = "16 Août 2026",
      endDate = "23 Août 2026",
      durationDays = 7,
      initialKm = 23200,
      returnKm = null,
      depositAmountDzd = 150000L,
      dailyRateDzd = 24000L,
      totalRentDzd = 168000L,
      status = ContractStatus.EN_COURS,
      isDepositReturned = false,
      notes = "Contrat Prestige. Restitution prévue à l'agence d'Hydra."
    ),
    RentalContract(
      contractId = "CTR-2026-0810",
      vehicleId = "RENT-CAR-05",
      vehicleName = "Renault Symbol 1.6 16v GPL",
      plateNumber = "06718-121-16",
      clientFullName = "Mohamed Cherif",
      clientPhone = "0540 12 34 56",
      clientNationalId = "102938471625",
      clientDrivingLicense = "P-16/071928",
      startDate = "10 Août 2026",
      endDate = "17 Août 2026 (En Retard)",
      durationDays = 7,
      initialKm = 86800,
      returnKm = null,
      depositAmountDzd = 30000L,
      dailyRateDzd = 5500L,
      totalRentDzd = 38500L,
      status = ContractStatus.RETARD_RESTITUTION,
      isDepositReturned = false,
      notes = "Client prévenu par SMS. Retard de 24h signalé."
    )
  )

  // --------------------------------------------------------------------------
  // UNIVERSAL VEHICLE QR CODE PASSPORT REGISTRY (Multi-Rôles Mekanik+)
  // --------------------------------------------------------------------------
  val sampleVehicleQrPassports: List<VehiclePassportQrData> = listOf(
    VehiclePassportQrData(
      qrToken = "MK-QR-2026-VAG-04829",
      registeredByRole = AccountRole.CONDUCTEUR,
      registeredByName = "Karim Mansouri (Propriétaire Particulier)",
      registrationDate = "14 Janvier 2026",
      vehicleVin = "WVWZZZ5NZMW123456",
      vehiclePlate = "04829-121-16",
      brand = "Volkswagen",
      model = "Tiguan II Facelift 2.0 TDI SCR",
      exactYear = 2021,
      engineCode = "DFGA (EA288 evo)",
      displacement = "2.0 TDI SCR 4Motion",
      powerCh = 150,
      fuelType = "Diesel (AdBlue Euro 6d)",
      gearbox = "DSG7 (DQ381)",
      currentMileageKm = 118400,
      healthScorePct = 94,
      lastServiceDate = "10 Juillet 2026",
      nextServiceKm = 120000,
      qrVerificationHash = "SHA256:7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069",
      orderedPartsForRetailer = listOf(
        QrOrderedPart(
          partId = "part-101",
          name = "Plaquettes de frein avant OEM Brembo",
          oemReference = "5Q0 698 151 BR",
          brandRequested = "Brembo Prime",
          quantity = 1,
          unitPriceDzd = 12500,
          status = QrPartOrderStatus.PRETE_COMPTOIR,
          systemCategory = "Freinage"
        ),
        QrOrderedPart(
          partId = "part-102",
          name = "Kit Courroie de Distribution + Pompe à Eau Gates",
          oemReference = "KP25649XS",
          brandRequested = "Gates PowerGrip",
          quantity = 1,
          unitPriceDzd = 28500,
          status = QrPartOrderStatus.RESERVEE_MAGASIN,
          systemCategory = "Distribution"
        ),
        QrOrderedPart(
          partId = "part-103",
          name = "Filtre à Huile Purflux Haute Pression",
          oemReference = "03N 115 562 B",
          brandRequested = "Purflux / Sogefi",
          quantity = 1,
          unitPriceDzd = 1800,
          status = QrPartOrderStatus.DEMANDEE_CLIENT,
          systemCategory = "Filtration"
        )
      ),
      activeRepairOrdersForWorkshop = listOf(
        QrRepairOrderInfo(
          orderNumber = "OR-2026-0842",
          dateCreated = "16 Août 2026",
          interventionType = "Grosse Révision 120 000 km & Remplacement Distribution",
          reportedSymptoms = "Léger sifflement courroie à froid, voyant maintenance périodique.",
          diagnosticSummary = "Tension galet tendeur affaiblie. Usure plaquettes avant à 75%. Système injection OK.",
          assignedTechnician = "Mourad K. (Chef d'Atelier)",
          estimatedLaborHours = 4.5f,
          estimatedCostDzd = 48500,
          status = RepairStatus.EN_COURS
        )
      ),
      rentalContractInfo = null,
      recentCertifiedRecords = listOf(
        DigitalServiceRecord(
          recordId = "REC-01",
          date = "10 Juillet 2026",
          mileageKm = 110200,
          operationTitle = "Vidange LongLife 5W30 & Filtre à Carburant",
          performedTasks = listOf("Vidange Synthèse Castrol Edge", "Remplacement filtre gasoil Mahle", "Diagnostic valise ODIS"),
          partsReplaced = listOf("Huile 5W30 5L", "Filtre Gasoil 5Q0 127 177 B"),
          totalCostDzd = 18500.0,
          garageName = "Garage Auto Performance (Alger)",
          certHash = "CERT-9941-DZ"
        )
      )
    ),

    VehiclePassportQrData(
      qrToken = "MK-QR-2026-RENT-06718",
      registeredByRole = AccountRole.LOCATION_VOITURES,
      registeredByName = "Auto Rent Algiers Prestige (Agence Hydra)",
      registrationDate = "05 Février 2026",
      vehicleVin = "VF1RJA00067890123",
      vehiclePlate = "06718-121-16",
      brand = "Renault",
      model = "Symbol II 1.6 16v GPL",
      exactYear = 2021,
      engineCode = "K4M 690",
      displacement = "1.6 MPI 16v + Kit GPL Landi Renzo",
      powerCh = 105,
      fuelType = "Essence / GPL",
      gearbox = "BVM5 (JH3)",
      currentMileageKm = 87200,
      healthScorePct = 88,
      lastServiceDate = "28 Juin 2026",
      nextServiceKm = 90000,
      qrVerificationHash = "SHA256:4a8c9e0112d8a57fe782410a8d29b01c34ef7791a82bc194627192305da9319e",
      orderedPartsForRetailer = listOf(
        QrOrderedPart(
          partId = "part-201",
          name = "Kit Embrayage Complet 3 Pièces (Disque + Mécanisme + Butée)",
          oemReference = "826229 / 77 01 479 123",
          brandRequested = "Valeo Origine",
          quantity = 1,
          unitPriceDzd = 18500,
          status = QrPartOrderStatus.DEMANDEE_CLIENT,
          systemCategory = "Embrayage"
        ),
        QrOrderedPart(
          partId = "part-202",
          name = "Jeu de Plaquettes de Frein Avant",
          oemReference = "41 06 055 33R",
          brandRequested = "Ferodo Premier",
          quantity = 1,
          unitPriceDzd = 4200,
          status = QrPartOrderStatus.RESERVEE_MAGASIN,
          systemCategory = "Freinage"
        )
      ),
      activeRepairOrdersForWorkshop = listOf(
        QrRepairOrderInfo(
          orderNumber = "OR-2026-0899",
          dateCreated = "17 Août 2026",
          interventionType = "Remplacement Kit Embrayage & Contrôle Butée Hydraulique",
          reportedSymptoms = "Pédale d'embrayage dure en fin de course, patinage en 3ème sous forte charge.",
          diagnosticSummary = "Garniture disque usée jusqu'aux rivets. Butée bruyante.",
          assignedTechnician = "Karim B. (Mécanicien Boîtes)",
          estimatedLaborHours = 3.5f,
          estimatedCostDzd = 26000,
          status = RepairStatus.PIECES_COMMANDEES
        )
      ),
      rentalContractInfo = QrRentalContractInfo(
        contractNumber = "CTR-2026-0810",
        agencyName = "Auto Rent Algiers Prestige",
        agencyPhone = "0550 88 99 00",
        renterFullName = "Mohamed Cherif",
        renterPhone = "0540 12 34 56",
        driverLicenseNumber = "P-16/071928",
        startDate = "10 Août 2026",
        expectedEndDate = "17 Août 2026 (En cours de restitution)",
        initialMileageKm = 86800,
        fuelLevelPct = 85,
        dailyRateDzd = 5500,
        depositAmountDzd = 30000,
        returnNotes = "Vérifier voyant GPL et niveau réservoir avant clôture du contrat."
      ),
      recentCertifiedRecords = listOf(
        DigitalServiceRecord(
          recordId = "REC-02",
          date = "28 Juin 2026",
          mileageKm = 80000,
          operationTitle = "Révision GPL & Remplacement Bougies NGK",
          performedTasks = listOf("Filtre phase gazeuse GPL", "4 Bougies Iridium", "Contrôle étanchéité"),
          partsReplaced = listOf("Filtre GPL Landi", "4x Bougies NGK"),
          totalCostDzd = 9800.0,
          garageName = "Atelier GPL Spécialiste Rouiba",
          certHash = "CERT-3382-DZ"
        )
      )
    ),

    VehiclePassportQrData(
      qrToken = "MK-QR-2026-GAR-09331",
      registeredByRole = AccountRole.GARAGE_PRO,
      registeredByName = "Garage Auto Performance (Badreddine Mekanik)",
      registrationDate = "22 Mars 2026",
      vehicleVin = "VF3FPHNSSMY001234",
      vehiclePlate = "09331-120-16",
      brand = "Peugeot",
      model = "208 II 1.2 PureTech 100 GT Line",
      exactYear = 2022,
      engineCode = "EB2ADTD (PureTech Turbo)",
      displacement = "1.2 PureTech Turbo 100",
      powerCh = 100,
      fuelType = "Essence Sans Plomb",
      gearbox = "EAT8 (Automatique)",
      currentMileageKm = 62400,
      healthScorePct = 76,
      lastServiceDate = "15 Août 2026",
      nextServiceKm = 65000,
      qrVerificationHash = "SHA256:91bf302eac4311029e78294a0d921b7782ef382901a8820c7891230489aa5421",
      orderedPartsForRetailer = listOf(
        QrOrderedPart(
          partId = "part-301",
          name = "Kit Courroie Distribution Humide Renforcée OEM (Dernière Génération)",
          oemReference = "16 545 160 80",
          brandRequested = "PSA Stellantis OEM",
          quantity = 1,
          unitPriceDzd = 34000,
          status = QrPartOrderStatus.PRETE_COMPTOIR,
          systemCategory = "Distribution"
        ),
        QrOrderedPart(
          partId = "part-302",
          name = "Pompe à Huile Moteur + Joint Crépine Haute Pression",
          oemReference = "98 088 592 80",
          brandRequested = "PSA Origine",
          quantity = 1,
          unitPriceDzd = 22000,
          status = QrPartOrderStatus.DEMANDEE_CLIENT,
          systemCategory = "Moteur"
        ),
        QrOrderedPart(
          partId = "part-303",
          name = "Jeu de 3 Bougies d'Allumage Iridium Haute Température",
          oemReference = "98 072 268 80 / ILKAR7J7G",
          brandRequested = "NGK Japan",
          quantity = 3,
          unitPriceDzd = 2500,
          status = QrPartOrderStatus.RESERVEE_MAGASIN,
          systemCategory = "Allumage"
        )
      ),
      activeRepairOrdersForWorkshop = listOf(
        QrRepairOrderInfo(
          orderNumber = "OR-2026-0877",
          dateCreated = "15 Août 2026",
          interventionType = "Mesure Calibre Courroie Humide & Dépose Carter / Nettoyage Crépine",
          reportedSymptoms = "Voyant Pression Huile clignotant lors des décélérations brutales. Calibre jauge > 16.5mm.",
          diagnosticSummary = "Effilochage partiel courroie dans le bain d'huile. Dépôt dans crépine détecté par caméra endoscopique.",
          assignedTechnician = "Badreddine (Spécialiste PureTech)",
          estimatedLaborHours = 6.0f,
          estimatedCostDzd = 68000,
          status = RepairStatus.EN_COURS
        )
      ),
      rentalContractInfo = null,
      recentCertifiedRecords = listOf(
        DigitalServiceRecord(
          recordId = "REC-03",
          date = "12 Février 2026",
          mileageKm = 48000,
          operationTitle = "Remplacement Plaquettes & Liquide de Frein DOT4",
          performedTasks = listOf("Plaquettes avant Ferodo", "Purge sous pression 2 bars", "Essai freinage"),
          partsReplaced = listOf("Plaquettes avant", "Liquide DOT4 Plus"),
          totalCostDzd = 11200.0,
          garageName = "Garage Auto Performance (Alger)",
          certHash = "CERT-8812-DZ"
        )
      )
    ),

    VehiclePassportQrData(
      qrToken = "MK-QR-2026-FLT-08122",
      registeredByRole = AccountRole.GARAGE_PRO,
      registeredByName = "Gestionnaire Flotte Transport & Maintenance",
      registrationDate = "10 Janvier 2026",
      vehicleVin = "UU1HSD0E560123456",
      vehiclePlate = "08122-119-16",
      brand = "Dacia",
      model = "Duster II 1.5 dCi 110 4x4",
      exactYear = 2020,
      engineCode = "K9K 858",
      displacement = "1.5 dCi 110 FAP 4WD",
      powerCh = 110,
      fuelType = "Diesel",
      gearbox = "BVM6 avec 1ère courte",
      currentMileageKm = 142100,
      healthScorePct = 82,
      lastServiceDate = "01 Août 2026",
      nextServiceKm = 140000,
      qrVerificationHash = "SHA256:09cfa39281bc89a710293847162534ef00192837465019283746501928374650",
      orderedPartsForRetailer = listOf(
        QrOrderedPart(
          partId = "part-401",
          name = "Amortisseurs Avant Renforcés Monroe OESpectrum (Paire)",
          oemReference = "54 30 200 48R",
          brandRequested = "Monroe OESpectrum",
          quantity = 2,
          unitPriceDzd = 9500,
          status = QrPartOrderStatus.PRETE_COMPTOIR,
          systemCategory = "Suspension"
        ),
        QrOrderedPart(
          partId = "part-402",
          name = "Biellettes de Barre Stabilisatrice Renforcées Heavy Duty",
          oemReference = "54 61 800 01R",
          brandRequested = "Lemförder / ZF",
          quantity = 2,
          unitPriceDzd = 3200,
          status = QrPartOrderStatus.DEMANDEE_CLIENT,
          systemCategory = "Suspension"
        )
      ),
      activeRepairOrdersForWorkshop = listOf(
        QrRepairOrderInfo(
          orderNumber = "OR-2026-0850",
          dateCreated = "14 Août 2026",
          interventionType = "Remplacement Train Roulant Avant & Contrôle Géométrie 3D",
          reportedSymptoms = "Claquement train avant sur pistes caillouteuses. Flottement à 100 km/h.",
          diagnosticSummary = "Jeu sur rotules et coupelles d'amortisseurs affaissées.",
          assignedTechnician = "Farid T. (Technicien Châssis)",
          estimatedLaborHours = 3.0f,
          estimatedCostDzd = 32000,
          status = RepairStatus.DIAGNOSTIC
        )
      ),
      rentalContractInfo = null,
      recentCertifiedRecords = emptyList()
    )
  )

  fun findQrPassportByToken(token: String): VehiclePassportQrData? {
    val clean = token.trim().uppercase()
    return sampleVehicleQrPassports.firstOrNull {
      it.qrToken.uppercase() == clean ||
      it.vehicleVin.uppercase() == clean ||
      it.vehiclePlate.replace("-", "").replace(" ", "").uppercase() == clean.replace("-", "").replace(" ", "")
    } ?: sampleVehicleQrPassports.firstOrNull {
      it.vehicleVin.contains(clean, ignoreCase = true) ||
      it.vehiclePlate.contains(clean, ignoreCase = true) ||
      clean.contains(it.qrToken, ignoreCase = true)
    }
  }

  // --- COMPTE ENTREPRISE ULTRA PRO (MULTI-ÉQUIPES & SOUS-COMPTES HIÉRARCHIQUES) ---
  val sampleUltraProSubAccounts: List<UltraProSubAccount> = listOf(
    // 1. Compte Maître : Administrateur Général / Gérant
    UltraProSubAccount(
      id = "SUB-001",
      fullName = "M. Reda Mansouri",
      email = "reda.mansouri@automekanik-alger.dz",
      phone = "0550 11 22 33",
      role = UltraProSubAccountRole.GERANT_PRINCIPAL,
      accessPin = "9900",
      isActive = true,
      permissions = listOf(
        "Accès Administrateur Total",
        "Création & Suppression des sous-comptes",
        "Gestion des rôles & permissions",
        "Validation clôture comptable",
        "Validation inventaire & stocks",
        "Journal d'audit & Traçabilité complète"
      ),
      avatarInitials = "RM",
      lastActive = "En ligne maintenant",
      actionsCount = 342
    ),

    // 2. Chef Conseiller Client / Team Leader Réception
    UltraProSubAccount(
      id = "SUB-002",
      fullName = "Hicham Bellahcene",
      email = "hicham.teamleader@automekanik-alger.dz",
      phone = "0551 00 11 22",
      role = UltraProSubAccountRole.CHEF_CONSEILLER_CLIENT,
      accessPin = "1000",
      isActive = true,
      permissions = listOf(
        "Supervision générale de l'accueil & des réceptions",
        "Coordination de l'équipe des conseillers clients",
        "Attribution et régulation des créneaux de RDV atelier",
        "Validation des devis complexes et remises commerciales"
      ),
      avatarInitials = "HB",
      lastActive = "Il y a 3 min",
      actionsCount = 115
    ),

    // 3. Conseillers Clients (2 comptes)
    UltraProSubAccount(
      id = "SUB-003",
      fullName = "Amine Benali",
      email = "amine.reception@automekanik-alger.dz",
      phone = "0551 22 33 44",
      role = UltraProSubAccountRole.CONSEILLER_CLIENT,
      accessPin = "1101",
      isActive = true,
      permissions = listOf(
        "Accueil clients & Planning des RDV",
        "Création Ordres de Réparation (OR)",
        "Génération Devis clients DZD",
        "Scan Carte Grise & Dictée vocale des pannes",
        "Suivi avancement atelier"
      ),
      avatarInitials = "AB",
      lastActive = "Il y a 5 min",
      actionsCount = 88
    ),
    UltraProSubAccount(
      id = "SUB-004",
      fullName = "Sara Khelifi",
      email = "sara.conseil@automekanik-alger.dz",
      phone = "0552 33 44 55",
      role = UltraProSubAccountRole.CONSEILLER_CLIENT,
      accessPin = "1102",
      isActive = true,
      permissions = listOf(
        "Accueil clients & Planning des RDV",
        "Création Ordres de Réparation (OR)",
        "Génération Devis clients DZD",
        "Envoi notifications SMS/WhatsApp clients"
      ),
      avatarInitials = "SK",
      lastActive = "Il y a 18 min",
      actionsCount = 64
    ),

    // 4. Atelier Mécanique : Chef d'Atelier (Responsable Technique)
    UltraProSubAccount(
      id = "SUB-005",
      fullName = "Mourad Guendouz",
      email = "mourad.chefatelier@automekanik-alger.dz",
      phone = "0770 12 34 56",
      role = UltraProSubAccountRole.CHEF_ATELIER,
      accessPin = "5500",
      isActive = true,
      permissions = listOf(
        "Supervision des 5 mécaniciens de l'atelier",
        "Validation et approbation technique des demandes de pièces",
        "Affectation des ordres de réparation aux ponts élévateurs",
        "Contrôle qualité avant restitution véhicule"
      ),
      avatarInitials = "MG",
      lastActive = "En intervention Pont 2",
      actionsCount = 192
    ),

    // 5. Atelier Mécanique : 5 Mécaniciens / Techniciens
    UltraProSubAccount(
      id = "SUB-006",
      fullName = "Karim Meziane",
      email = "karim.meca1@automekanik-alger.dz",
      phone = "0661 23 45 67",
      role = UltraProSubAccountRole.MECANICIEN_ATELIER,
      accessPin = "5501",
      isActive = true,
      permissions = listOf(
        "Diagnostic mécanique & lecture codes défauts OBD",
        "Recherche et sélection des pièces par VIN / Châssis",
        "Émission des demandes de pièces au Chef d'Atelier",
        "Pointage des temps d'intervention"
      ),
      avatarInitials = "KM",
      lastActive = "Sur Tiguan [DFGA]",
      actionsCount = 145
    ),
    UltraProSubAccount(
      id = "SUB-007",
      fullName = "Sofiane Brahimi",
      email = "sofiane.meca2@automekanik-alger.dz",
      phone = "0662 34 56 78",
      role = UltraProSubAccountRole.MECANICIEN_ATELIER,
      accessPin = "5502",
      isActive = true,
      permissions = listOf(
        "Diagnostic mécanique & électricité auto",
        "Recherche de pièces détachées selon le châssis",
        "Création de demandes de pièces",
        "Pose & Remplacement des composants"
      ),
      avatarInitials = "SB",
      lastActive = "Sur Peugeot 308 [DV5RC]",
      actionsCount = 120
    ),
    UltraProSubAccount(
      id = "SUB-008",
      fullName = "Walid Hamidi",
      email = "walid.meca3@automekanik-alger.dz",
      phone = "0663 45 67 89",
      role = UltraProSubAccountRole.MECANICIEN_ATELIER,
      accessPin = "5503",
      isActive = true,
      permissions = listOf(
        "Travaux de freinage, trains roulants et suspensions",
        "Demandes de pièces selon références OEM constructeur",
        "Purges et contrôles hydrauliques"
      ),
      avatarInitials = "WH",
      lastActive = "Il y a 25 min",
      actionsCount = 89
    ),
    UltraProSubAccount(
      id = "SUB-009",
      fullName = "Bilal Ouali",
      email = "bilal.meca4@automekanik-alger.dz",
      phone = "0664 56 78 90",
      role = UltraProSubAccountRole.MECANICIEN_ATELIER,
      accessPin = "5504",
      isActive = true,
      permissions = listOf(
        "Diagnostic moteurs diesel Common Rail & Injection",
        "Sélection et demande de pièces de distribution et pompes",
        "Calage et contrôles dynamiques"
      ),
      avatarInitials = "BO",
      lastActive = "Il y a 10 min",
      actionsCount = 76
    ),
    UltraProSubAccount(
      id = "SUB-010",
      fullName = "Khaled Saci",
      email = "khaled.meca5@automekanik-alger.dz",
      phone = "0665 67 89 01",
      role = UltraProSubAccountRole.MECANICIEN_ATELIER,
      accessPin = "5505",
      isActive = true,
      permissions = listOf(
        "Entretien périodique, vidanges & climatisation",
        "Demande de filtres, courroies et fluides",
        "Essais routiers post-réparation"
      ),
      avatarInitials = "KS",
      lastActive = "Il y a 40 min",
      actionsCount = 63
    ),

    // 6. Département Magasin (1 Administrateur Magasin + 2 Vendeurs/Magasiniers)
    UltraProSubAccount(
      id = "SUB-011",
      fullName = "Kamel Boudiaf",
      email = "kamel.magasin@automekanik-alger.dz",
      phone = "0770 44 55 66",
      role = UltraProSubAccountRole.ADMIN_MAGASIN,
      accessPin = "2201",
      isActive = true,
      permissions = listOf(
        "Confirmation définitive des sorties de stock pièces",
        "Injection automatique des pièces sur Facture & OR client",
        "Gestion globale de l'inventaire et réapprovisionnements",
        "Commandes grossistes et supervision des magasins"
      ),
      avatarInitials = "KB",
      lastActive = "Il y a 2 min",
      actionsCount = 215
    ),
    UltraProSubAccount(
      id = "SUB-012",
      fullName = "Yacine Cherif",
      email = "yacine.vente@automekanik-alger.dz",
      phone = "0771 55 66 77",
      role = UltraProSubAccountRole.VENDEUR_MAGASIN,
      accessPin = "2202",
      isActive = true,
      permissions = listOf(
        "Réception des demandes de pièces validées par le Chef d'Atelier",
        "Préparation physique des pièces en casiers selon Châssis/VIN",
        "Vente au comptoir & vérification des disponibilités",
        "Interdiction de modification globale d'inventaire"
      ),
      avatarInitials = "YC",
      lastActive = "Il y a 10 min",
      actionsCount = 142
    ),
    UltraProSubAccount(
      id = "SUB-013",
      fullName = "Tarek Medjani",
      email = "tarek.comptoir@automekanik-alger.dz",
      phone = "0772 66 77 88",
      role = UltraProSubAccountRole.VENDEUR_MAGASIN,
      accessPin = "2203",
      isActive = true,
      permissions = listOf(
        "Réception des demandes et préparation des paniers atelier",
        "Vente au comptoir & encaissement direct",
        "Consultation stock et équivalences"
      ),
      avatarInitials = "TM",
      lastActive = "Il y a 45 min",
      actionsCount = 98
    ),

    // 7. Département Comptabilité & Facturation (1 Comptable + 2 Analystes Factures)
    UltraProSubAccount(
      id = "SUB-014",
      fullName = "Nadia Zerrouki",
      email = "nadia.compta@automekanik-alger.dz",
      phone = "0560 77 88 99",
      role = UltraProSubAccountRole.COMPTABLE_PRINCIPAL,
      accessPin = "3301",
      isActive = true,
      permissions = listOf(
        "Gestion complète de la trésorerie",
        "Déclaration & Suivi de la TVA (19%)",
        "Rapprochements bancaires & CIB/Edahabia",
        "Clôture des factures & Édition des bilans",
        "Validation des décaissements fournisseurs"
      ),
      avatarInitials = "NZ",
      lastActive = "Il y a 30 min",
      actionsCount = 178
    ),
    UltraProSubAccount(
      id = "SUB-015",
      fullName = "Bilel Hadjadj",
      email = "bilel.factures@automekanik-alger.dz",
      phone = "0561 88 99 00",
      role = UltraProSubAccountRole.ANALYSTE_FACTURES,
      accessPin = "3302",
      isActive = true,
      permissions = listOf(
        "Analyse & Rapprochement des factures d'achats pièces",
        "Contrôle de conformité des devis vs factures",
        "Suivi des impayés et relances clients",
        "Exportation des rapports d'audit financier"
      ),
      avatarInitials = "BH",
      lastActive = "Il y a 1 heure",
      actionsCount = 74
    ),
    UltraProSubAccount(
      id = "SUB-016",
      fullName = "Ines Brahimi",
      email = "ines.auditfactures@automekanik-alger.dz",
      phone = "0562 99 00 11",
      role = UltraProSubAccountRole.ANALYSTE_FACTURES,
      accessPin = "3303",
      isActive = true,
      permissions = listOf(
        "Analyse & Rapprochement des factures d'achats pièces",
        "Contrôle de conformité des devis vs factures",
        "Vérification des droits de timbre fiscal"
      ),
      avatarInitials = "IB",
      lastActive = "Hier à 17:30",
      actionsCount = 52
    ),

    // 8. Département Audit & Contrôle Interne (1 Auditeur Qualité)
    UltraProSubAccount(
      id = "SUB-017",
      fullName = "Farid Ould-Ali",
      email = "farid.audit@automekanik-alger.dz",
      phone = "0661 12 34 56",
      role = UltraProSubAccountRole.AUDITEUR_QUALITE,
      accessPin = "4401",
      isActive = true,
      permissions = listOf(
        "Audit technique des interventions mécaniques",
        "Contrôle de conformité des couples de serrage et normes OEM",
        "Audit des marges brutes sur pièces vendues",
        "Contrôle des écarts de stock physique vs théorique",
        "Génération de rapports d'inspection pour la Direction"
      ),
      avatarInitials = "FO",
      lastActive = "Il y a 20 min",
      actionsCount = 91
    )
  )

  // --- WORKFLOW DÉTAILLÉ DE DEMANDES DE PIÈCES (4 ÉTAPES) ---
  val samplePartsRequisitions: List<WorkshopPartsRequisition> = listOf(
    WorkshopPartsRequisition(
      id = "REQ-2026-001",
      orderNumber = "DMD-0842",
      linkedRepairOrderId = "or_001",
      vehicleModel = "VW Tiguan II 2.0 TDI (2021)",
      vehicleVin = "WVWZZZ5NZMW123456",
      plateNumber = "04291-121-16",
      requestedByTechnicianId = "SUB-006",
      requestedByTechnicianName = "Karim Meziane (Mécanicien)",
      parts = listOf(
        RequisitionPartItem("p_01", "5Q0 615 301 F", "Jeu de disques avant Brembo Ø312mm", "Brembo", 1, 18500, 4),
        RequisitionPartItem("p_02", "5Q0 698 151 BR", "Jeu de plaquettes avant Brembo", "Brembo", 1, 8500, 8),
        RequisitionPartItem("p_03", "DOT4-BREM-1L", "Liquide de frein DOT4 Premium 1L", "Brembo", 1, 1500, 15)
      ),
      status = PartsRequisitionStatus.CONFIRMEE_SORTIE_STOCK,
      createdAt = "Aujourd'hui à 08:30",
      approvedByWorkshopManagerAt = "Aujourd'hui à 08:45",
      workshopManagerName = "Mourad Guendouz (Chef d'Atelier)",
      receivedByStorekeeperAt = "Aujourd'hui à 09:00",
      storekeeperName = "Yacine Cherif (Magasinier)",
      confirmedByHeadStorekeeperAt = "Aujourd'hui à 09:15",
      headStorekeeperName = "Kamel Boudiaf (Chef Magasin)",
      notes = "Pièces préparées au casier A04. Sortie de stock validée et imputée immédiatement sur l'OR-2026-0842 et la facture client."
    ),
    WorkshopPartsRequisition(
      id = "REQ-2026-002",
      orderNumber = "DMD-0839",
      linkedRepairOrderId = "or_002",
      vehicleModel = "Peugeot 308 III 1.5 BlueHDi",
      vehicleVin = "VF3FPHNSSMY001234",
      plateNumber = "18204-122-31",
      requestedByTechnicianId = "SUB-007",
      requestedByTechnicianName = "Sofiane Brahimi (Mécanicien)",
      parts = listOf(
        RequisitionPartItem("p_04", "16 381 573 80", "Kit chaîne distribution 8mm OEM PSA", "PSA OEM", 1, 32000, 3),
        RequisitionPartItem("p_05", "16 230 981 80", "Pompe à eau + Joint", "Valeo", 1, 14000, 2),
        RequisitionPartItem("p_06", "6PK1190", "Courroie accessoires Gates", "Gates", 1, 6500, 6)
      ),
      status = PartsRequisitionStatus.EN_PREPARATION_MAGASIN,
      createdAt = "Aujourd'hui à 09:30",
      approvedByWorkshopManagerAt = "Aujourd'hui à 09:50",
      workshopManagerName = "Mourad Guendouz (Chef d'Atelier)",
      receivedByStorekeeperAt = "Aujourd'hui à 10:10",
      storekeeperName = "Yacine Cherif (Magasinier)",
      notes = "En cours de préparation physique en rayon par le magasinier. En attente de validation de sortie par le Chef Magasin."
    ),
    WorkshopPartsRequisition(
      id = "REQ-2026-003",
      orderNumber = "DMD-0855",
      linkedRepairOrderId = "or_003",
      vehicleModel = "Renault Clio V 1.5 Blue dCi",
      vehicleVin = "VF1RJA00067890123",
      plateNumber = "09312-120-09",
      requestedByTechnicianId = "SUB-008",
      requestedByTechnicianName = "Walid Hamidi (Mécanicien)",
      parts = listOf(
        RequisitionPartItem("p_07", "RENAULT-KIT-4F", "Kit 4 Filtres révision complète", "Purflux", 1, 12500, 9),
        RequisitionPartItem("p_08", "ELF-5W30-RN0720", "Huile Elf Full-Tech FE 5W30 5L", "Elf", 1, 8200, 12)
      ),
      status = PartsRequisitionStatus.VALIDEE_CHEF_ATELIER,
      createdAt = "Aujourd'hui à 10:40",
      approvedByWorkshopManagerAt = "Aujourd'hui à 11:05",
      workshopManagerName = "Mourad Guendouz (Chef d'Atelier)",
      notes = "Demande validée techniquement par le Chef d'Atelier. Transmise au magasinier pour préparation."
    ),
    WorkshopPartsRequisition(
      id = "REQ-2026-004",
      orderNumber = "DMD-0860",
      linkedRepairOrderId = "or_004",
      vehicleModel = "Golf 7 2.0 TDI EA288",
      vehicleVin = "WVWZZZAUZHP998877",
      plateNumber = "05612-118-16",
      requestedByTechnicianId = "SUB-009",
      requestedByTechnicianName = "Bilal Ouali (Mécanicien)",
      parts = listOf(
        RequisitionPartItem("p_09", "04L 131 512 BH", "Vanne EGR + Échangeur Thermique", "Wahler OEM", 1, 48000, 1),
        RequisitionPartItem("p_10", "G12EVO-5L", "Liquide de refroidissement G12evo 5L", "VAG OEM", 1, 4500, 7)
      ),
      status = PartsRequisitionStatus.DEMANDE_MECANICIEN,
      createdAt = "Aujourd'hui à 11:15",
      notes = "Nouvelle demande de pièces émise par le mécanicien après diagnostic OBD (Code P0401). En attente d'approbation par le Chef d'Atelier."
    )
  )

  val sampleSubAccountAuditLogs: List<SubAccountAuditLog> = listOf(
    SubAccountAuditLog(
      id = "LOG-100",
      authorName = "Kamel Boudiaf",
      authorRole = "Chef Magasinier",
      actionTitle = "Sortie de stock confirmée & Facturation auto",
      details = "Validation sortie DMD-0842 (Disques & plaquettes Brembo). Imputation directe sur OR-2026-0842 et Facture FCT-2026-0842.",
      timestamp = "Aujourd'hui à 09:15",
      department = "Magasin & Pièces",
      severity = "IMPORTANT"
    ),
    SubAccountAuditLog(
      id = "LOG-101",
      authorName = "Mourad Guendouz",
      authorRole = "Chef d'Atelier",
      actionTitle = "Approbation demande pièces DMD-0839",
      details = "Vérification conformité kit chaîne distribution 8mm pour Peugeot 308 (VF3FPHNSSMY001234). Demande transmise au magasinier.",
      timestamp = "Aujourd'hui à 09:50",
      department = "Atelier Mécanique",
      severity = "NORMAL"
    ),
    SubAccountAuditLog(
      id = "LOG-102",
      authorName = "Karim Meziane",
      authorRole = "Mécanicien",
      actionTitle = "Création demande pièces DMD-0842",
      details = "Sélection pièces après démontage train avant Tiguan (Disques + Plaquettes Brembo).",
      timestamp = "Aujourd'hui à 08:30",
      department = "Atelier Mécanique",
      severity = "NORMAL"
    ),
    SubAccountAuditLog(
      id = "LOG-103",
      authorName = "Hicham Bellahcene",
      authorRole = "Team Leader Réception",
      actionTitle = "Supervision planning atelier",
      details = "Réaffectation créneau d'urgence Pont 1 pour remplacement distribution Peugeot 308.",
      timestamp = "Aujourd'hui à 08:15",
      department = "Accueil & Réception",
      severity = "NORMAL"
    ),
    SubAccountAuditLog(
      id = "LOG-104",
      authorName = "Nadia Zerrouki",
      authorRole = "Comptable Principal",
      actionTitle = "Clôture Facture & Encaissement CIB",
      details = "Validation règlement facture FCT-2026-0842 (29 500 DA) par carte CIB. Génération reçu fiscal.",
      timestamp = "Aujourd'hui à 10:12",
      department = "Finance & Comptabilité",
      severity = "IMPORTANT"
    ),
    SubAccountAuditLog(
      id = "LOG-105",
      authorName = "Farid Ould-Ali",
      authorRole = "Auditeur Qualité",
      actionTitle = "Inspection & Contrôle Conformité",
      details = "Vérification calage distribution sur OR-2026-0839. Respect strict de la méthode constructeur PSA.",
      timestamp = "Aujourd'hui à 09:30",
      department = "Audit & Conformité",
      severity = "NORMAL"
    ),
    SubAccountAuditLog(
      id = "LOG-106",
      authorName = "M. Reda Mansouri",
      authorRole = "Gérant Principal",
      actionTitle = "Achat Pack Extension +2 Sous-Comptes",
      details = "Ajout de 2 sous-comptes supplémentaires pour l'équipe atelier (+2 500 DA). Capacité portée à 16 comptes.",
      timestamp = "Aujourd'hui à 08:00",
      department = "Direction Générale",
      severity = "IMPORTANT"
    )
  )

  val sampleUltraProOrganization: UltraProOrganizationSummary = UltraProOrganizationSummary()

  val sampleFullSoftwareSupplement: FullSoftwarePackageSupplement = FullSoftwarePackageSupplement()

  val sampleOemSoftwareTools: List<OemSoftwareTool> = listOf(
    // VAG Group
    OemSoftwareTool(
      id = "tool_etka_vag",
      name = "ETKA 8.3 — Catalogue Pièces Officiel VAG",
      group = OemManufacturerGroup.VAG_GROUP,
      toolType = SoftwareToolType.CATALOGUE_EPC,
      officialVersion = "v8.3 (Mise à jour 2026/08)",
      description = "Catalogue électronique officiel pièces de rechange pour Volkswagen, Audi, Seat, Skoda et Porsche. Éclatés complets avec références OEM et prix barémés.",
      targetAudience = "Concessions VAG, Magasiniers & Ateliers Spécialisés",
      keyFeatures = listOf(
        "Décodage VIN complet avec PR-Numbers (Codes options usine)",
        "Éclatés 3D de tous les sous-ensembles (Moteur, Châssis, Boîte DSG, Électricité)",
        "Références croisées et kits de remplacement préconisés par le constructeur",
        "Liaison directe avec le panier de commande Mekanik+ Pro"
      ),
      directLaunchUrl = "https://vag.etka.mekanikplus.dz"
    ),
    OemSoftwareTool(
      id = "tool_elsawin_vag",
      name = "ELSAWin Pro — Manuels d'Atelier & Schémas VAG",
      group = OemManufacturerGroup.VAG_GROUP,
      toolType = SoftwareToolType.MANUEL_REPARATION,
      officialVersion = "Édition Service 2026",
      description = "Système officiel d'assistance technique pour les ateliers du groupe Volkswagen : manuels de réparation étape par étape, couples de serrage et schémas électriques interactifs.",
      targetAudience = "Mécaniciens d'Atelier & Chefs d'Équipe VAG",
      keyFeatures = listOf(
        "Procédures officielles de dépose/repose moteur, boîte et trains roulants",
        "Couples de serrage avec serrage angulaire (Nm + °)",
        "Schémas électriques de principe et localisation des calculateurs",
        "Bulletins de service technique (TSB) et rappels constructeurs"
      ),
      directLaunchUrl = "https://elsawin.mekanikplus.dz"
    ),
    OemSoftwareTool(
      id = "tool_odis_vag",
      name = "ODIS Service 23 — Diagnostic & Programmation VAG",
      group = OemManufacturerGroup.VAG_GROUP,
      toolType = SoftwareToolType.DIAGNOSTIC_ODIS,
      officialVersion = "ODIS-S v23.0.4",
      description = "Système de diagnostic intégré pour les calculateurs et bus CAN du groupe Volkswagen. Recherche guidée des pannes et codage en ligne.",
      targetAudience = "Techniciens Diagnostic & Électriciens Auto",
      keyFeatures = listOf(
        "Recherche assistée des pannes avec plan de contrôle pas-à-pas",
        "Adaptations de base (calibrage papillon, régénération FAP, apprentissage DSG)",
        "Pass-Thru J2534 et communication DoIP / CAN FD"
      )
    ),

    // Stellantis Group
    OemSoftwareTool(
      id = "tool_servicebox_stellantis",
      name = "Stellantis ServiceBox EPC — Catalogue Pièces",
      group = OemManufacturerGroup.STELLANTIS_GROUP,
      toolType = SoftwareToolType.CATALOGUE_EPC,
      officialVersion = "Portail TechHub 2026",
      description = "Catalogue électronique de pièces de rechange d'origine pour Peugeot, Citroën, DS, Opel, Fiat, Alfa Romeo et Jeep.",
      targetAudience = "Concessions Stellantis, Magasins de Pièces & Garages Agréés",
      keyFeatures = listOf(
        "Filtrage automatique par numéro VIN/Châssis (17 caractères)",
        "Visualisation de la nomenclature complète et sous-ensembles",
        "Vérification de la disponibilité et substitution de références",
        "Export direct vers l'Ordre de Réparation Mekanik+"
      ),
      directLaunchUrl = "https://servicebox.stellantis.mekanikplus.dz"
    ),
    OemSoftwareTool(
      id = "tool_docbackup_stellantis",
      name = "DocBackup & SEDRE — Méthodes & Câblage Stellantis",
      group = OemManufacturerGroup.STELLANTIS_GROUP,
      toolType = SoftwareToolType.SCHEMA_ELECTRIQUE,
      officialVersion = "v2026.2",
      description = "Documentation technique d'atelier, gammes de révision, schémas électriques couleur SEDRE et localisation des composants pour les marques Stellantis.",
      targetAudience = "Mécaniciens, Diagnosticiens & Électriciens",
      keyFeatures = listOf(
        "Schémas électriques détaillés avec repérage des connecteurs et BSI/BSM",
        "Méthodes officielles de calage de distribution (PureTech / BlueHDi / MultiAir)",
        "Barèmes de temps de main-d'œuvre constructeur",
        "Consignes de sécurité pour véhicules hybrides et électriques (e-CMP / STLA)"
      )
    ),

    // Renault Group
    OemSoftwareTool(
      id = "tool_dialogys_renault",
      name = "Dialogys 4.90 — Catalogue Pièces & Temps Renault",
      group = OemManufacturerGroup.RENAULT_GROUP,
      toolType = SoftwareToolType.CATALOGUE_EPC,
      officialVersion = "Release 4.90 Pro",
      description = "Documentation électronique officielle Renault et Dacia. Contient l'intégralité des catalogues de pièces détachées (PR), manuels de réparation (MR) et barèmes de temps (TM).",
      targetAudience = "Agents Renault, Réparateurs Indépendants & Vendeurs Grossistes",
      keyFeatures = listOf(
        "Identification par plaque ovale ou numéro VIN",
        "Recherche de pièces éclatées avec prix HT/TTC et coefficients",
        "Méthodes de carrosserie et mécanique détaillées",
        "Synchronisation automatique avec le magasin de stock"
      ),
      directLaunchUrl = "https://dialogys.renault.mekanikplus.dz"
    ),
    OemSoftwareTool(
      id = "tool_clip_renault",
      name = "Renault CLIP Diagnostic & Repman",
      group = OemManufacturerGroup.RENAULT_GROUP,
      toolType = SoftwareToolType.DIAGNOSTIC_ODIS,
      officialVersion = "v232",
      description = "Outil de diagnostic officiel pour les véhicules de la marque au losange, Dacia et Alpine.",
      targetAudience = "Ateliers de Réparation & Concessionnaires",
      keyFeatures = listOf(
        "Diagnostic tous calculateurs (Injection dCi/TCe, ABS/ESP, UCH)",
        "Configuration des injecteurs et réinitialisation des services d'entretien",
        "Guides de test de conformité"
      )
    ),

    // German Premium (BMW & Mercedes-Benz)
    OemSoftwareTool(
      id = "tool_bmw_ista_etk",
      name = "BMW ISTA-D & ETK — Atelier & Pièces BMW / Mini",
      group = OemManufacturerGroup.GERMAN_PREMIUM,
      toolType = SoftwareToolType.CATALOGUE_EPC,
      officialVersion = "ISTA 4.45 / ETK 2026",
      description = "Suite complète BMW Group : catalogue électronique ETK et logiciel d'atelier ISTA pour manuels de réparation, schémas de câblage interactifs et données de couple.",
      targetAudience = "Spécialistes BMW & Mini, Garages Haut de Gamme",
      keyFeatures = listOf(
        "Recherche par les 7 derniers caractères du VIN",
        "Schémas électriques interactifs dynamiques (WDS)",
        "Instructions pas-à-pas avec illustrations haute définition",
        "Plans de maintenance conditionnelle (CBS)"
      ),
      directLaunchUrl = "https://bmw.etk.mekanikplus.dz"
    ),
    OemSoftwareTool(
      id = "tool_mercedes_wis_epc",
      name = "Mercedes-Benz WIS / ASRA / EPC — Atelier Étoile",
      group = OemManufacturerGroup.GERMAN_PREMIUM,
      toolType = SoftwareToolType.MANUEL_REPARATION,
      officialVersion = "Édition Entreprise 2026",
      description = "Système d'information d'atelier (WIS), barèmes de temps (ASRA) et catalogue électronique de pièces (EPC) pour l'ensemble de la gamme Mercedes-Benz et utilitaires.",
      targetAudience = "Concessions et Réparateurs Spécialisés Mercedes-Benz",
      keyFeatures = listOf(
        "Documentation complète pour véhicules particuliers, utilitaires (Sprinter/Vito) et poids lourds",
        "Nomenclatures et éclatés des pièces avec numéros OEM A000...",
        "Valeurs de réglage, géométrie des trains et tolérances d'usure",
        "Barèmes forfaitaires de temps pour chiffrage précis des devis"
      ),
      directLaunchUrl = "https://mercedes.wis.mekanikplus.dz"
    ),

    // Asian Brands
    OemSoftwareTool(
      id = "tool_microcat_asian",
      name = "Microcat EPC — Constructeurs Asiatiques (Toyota / Hyundai / Kia)",
      group = OemManufacturerGroup.ASIAN_BRANDS,
      toolType = SoftwareToolType.CATALOGUE_EPC,
      officialVersion = "v2026 Multi-Asian",
      description = "Catalogue de pièces pour les marques leaders asiatiques : Toyota, Hyundai, Kia, Nissan, Suzuki, Chery et Geely.",
      targetAudience = "Concessionnaires Asiatiques & Ateliers Toutes Marques",
      keyFeatures = listOf(
        "Prise en charge approfondie des modèles assemblés et commercialisés en Algérie",
        "Recherche rapide par référence croisée ou éclatés moteurs/boîtes",
        "Mises à jour des tarifs pièces d'origine et adaptables certifiées"
      ),
      directLaunchUrl = "https://asian.microcat.mekanikplus.dz"
    ),

    // Universal Multi-brand Suites
    OemSoftwareTool(
      id = "tool_tecdoc_pro",
      name = "TecDoc Catalogue Professional — Référentiel Mondial",
      group = OemManufacturerGroup.MULTI_BRAND_UNIVERSAL,
      toolType = SoftwareToolType.CATALOGUE_EPC,
      officialVersion = "TecAlliance Q3/2026",
      description = "Le catalogue universel de référence mondial pour l'après-vente automobile. Plus de 900 marques d'équipementiers (Bosch, Valeo, Sachs, Brembo, Mann-Filter) et 110 000 modèles de véhicules.",
      targetAudience = "Vendeurs Grossistes, Magasiniers & Ateliers Multimarques",
      keyFeatures = listOf(
        "Équivalences exactes entre références constructeurs (OEM) et équipementiers (IAM)",
        "Recherche par code moteur, type mine, KBA ou immatriculation",
        "Données de compatibilité et dimensions techniques certifiées",
        "Intégration directe avec les fiches de stock et fournisseurs Mekanik+"
      ),
      directLaunchUrl = "https://tecdoc.mekanikplus.dz"
    ),
    OemSoftwareTool(
      id = "tool_haynespro_workshop",
      name = "HaynesPro WorkshopData Ultimate — Données Techniques & Diagnostic",
      group = OemManufacturerGroup.MULTI_BRAND_UNIVERSAL,
      toolType = SoftwareToolType.MANUEL_REPARATION,
      officialVersion = "WorkshopData Touch 2026",
      description = "Base de données technique multimarque d'atelier numéro 1 en Europe : manuels de réparation, données de maintenance, schémas de câblage couleur, modules VESA (diagnostic guidé) et SmartCASE (pannes connues résolues).",
      targetAudience = "Chefs d'Atelier, Mécaniciens & Diagnosticiens Multimarques",
      keyFeatures = listOf(
        "Module SmartCASE : plus de 15 000 cas de pannes réelles résolues avec bulletins techniques",
        "Schémas électriques couleurs avec gestion de l'électronique embarquée (VESA)",
        "Plans d'entretien officiels constructeurs préservant la garantie constructeur",
        "Calculateur automatique de devis et barèmes de temps de main-d'œuvre"
      ),
      directLaunchUrl = "https://haynespro.mekanikplus.dz"
    )
  )

  // API Gateway Services & Developer Sandbox Endpoints (11 APIs synchronisées dans la Cascade de Fallback Automatique)
  val sampleApiEndpoints: List<ApiServiceEndpoint> = listOf(
    ApiServiceEndpoint(
      id = "ep_mekanik_one_unified",
      name = "MekanikOne Unified Auto-API (Routeur & Cascade 11-en-1)",
      provider = "Mekanik+ AI Gateway & Auto-Fallback Engine",
      endpointUrl = "https://api.mekanik-plus.ai/v1/unified-auto-graph/resolve",
      status = "ACTIF • CASCADE FALLBACK 11 SOURCES (NOUVELLES + HISTORIQUES)",
      isConnected = true,
      latencyMs = 48,
      authType = "Unified AI Router (5 APIs Chine/IA + 6 APIs Historiques)",
      isFreeTier = true,
      cachedItemsCount = 24800
    ),
    ApiServiceEndpoint(
      id = "ep_sinoauto360",
      name = "[Fallback #1] SinoAuto360 API (50+ Marques Chinoises & VIN Usines)",
      provider = "SinoAuto360 Developer Platform",
      endpointUrl = "https://api.sinoauto360.com/v2/vin-oem/decode",
      status = "PRIORITÉ #1 • RESTful JSON OEM",
      isConnected = true,
      latencyMs = 42,
      authType = "Developer Evaluation Key (Geely / Jetour / BYD / Changan)",
      isFreeTier = true,
      cachedItemsCount = 5200
    ),
    ApiServiceEndpoint(
      id = "ep_dongchedi_auto_api",
      name = "[Fallback #2] Auto-API (Dongchedi • Specs, Finitions & Moteurs)",
      provider = "Dongchedi Third-Party Auto-API",
      endpointUrl = "https://auto-api.dongchedi-partner.cn/v1/specs/extract",
      status = "PRIORITÉ #2 (FALLBACK 1) • GEMINI FUNCTION CALLING",
      isConnected = true,
      latencyMs = 36,
      authType = "Clean JSON Schema (Geely / Jetour / FAW-VAG)",
      isFreeTier = true,
      cachedItemsCount = 4100
    ),
    ApiServiceEndpoint(
      id = "ep_car2db_llm",
      name = "[Fallback #3] Car2DB API (Version Évaluation • Optimisée LLM)",
      provider = "Car2DB Automotive Database",
      endpointUrl = "https://api.car2db.com/v3/llm-breadcrumb/vehicle",
      status = "PRIORITÉ #3 (FALLBACK 2) • FIL D'ARIANE & RÉSUMÉS LLM",
      isConnected = true,
      latencyMs = 21,
      authType = "LLM Evaluation Token (Breadcrumbs + Text Digest)",
      isFreeTier = true,
      cachedItemsCount = 3600
    ),
    ApiServiceEndpoint(
      id = "ep_chinesecar_catcar",
      name = "[Fallback #4] ChineseCar.parts & CatCar.info (Arborescences OEM)",
      provider = "Open Web OEM Catalogs",
      endpointUrl = "https://chinesecar.parts/api/tree-export/oem-schema",
      status = "PRIORITÉ #4 (FALLBACK 3) • ARBORESCENCE JSON",
      isConnected = true,
      latencyMs = 14,
      authType = "Reverse-Engineered JSON Context Tree",
      isFreeTier = true,
      cachedItemsCount = 2950
    ),
    ApiServiceEndpoint(
      id = "ep_oem_servicebox_dialogys_etka",
      name = "[Fallback #5 • API Historique] Portails Constructeurs (ServiceBox / Dialogys / ETKA)",
      provider = "Stellantis / Renault / VAG / BMW / Mercedes / Microcat",
      endpointUrl = "https://epc-gateway.mekanikplus.dz/v2/oem-portals/vin-filter",
      status = "PRIORITÉ #5 (FALLBACK 4) • PORTAILS OFFICIELS OEM",
      isConnected = true,
      latencyMs = 34,
      authType = "Passerelle EPC Constructeurs (ServiceBox, Dialogys 4.90, ETKA 8.5)",
      isFreeTier = true,
      cachedItemsCount = 6400
    ),
    ApiServiceEndpoint(
      id = "ep_tecdoc_sandbox",
      name = "[Fallback #6 • API Historique] TecDoc / TecAlliance API (Sandbox & Pro)",
      provider = "TecAlliance GmbH",
      endpointUrl = "https://web.tecalliance.net/api/v1/sandbox/articles",
      status = "PRIORITÉ #6 (FALLBACK 5) • 900+ ÉQUIPEMENTIERS IAM",
      isConnected = true,
      latencyMs = 42,
      authType = "Bearer Token (TecAlliance KType & Cross-Ref)",
      isFreeTier = true,
      cachedItemsCount = 4450
    ),
    ApiServiceEndpoint(
      id = "ep_haynespro_autodata",
      name = "[Fallback #7 • API Historique] HaynesPro WorkshopData & Autodata API",
      provider = "HaynesPro / Autodata Connect",
      endpointUrl = "https://haynespro.mekanikplus.dz/api/v1/workshopdata/vin",
      status = "PRIORITÉ #7 (FALLBACK 6) • SCHÉMAS VESA, COUPLES & SMARTCASE",
      isConnected = true,
      latencyMs = 29,
      authType = "WorkshopData Ultimate Token",
      isFreeTier = true,
      cachedItemsCount = 3120
    ),
    ApiServiceEndpoint(
      id = "ep_global_7zap_partssouq",
      name = "[Fallback #8 • API Historique] Passerelle 7Zap & PartsSouq EPC",
      provider = "7Zap / PartsSouq Global EPC Bridge",
      endpointUrl = "https://gateway.mekanik-plus.ai/v1/7zap-partssouq-epc",
      status = "PRIORITÉ #8 (FALLBACK 7) • ÉCLATÉS EUROPE / ASIE / GOLFE",
      isConnected = true,
      latencyMs = 28,
      authType = "EPC Bridge Key",
      isFreeTier = true,
      cachedItemsCount = 2890
    ),
    ApiServiceEndpoint(
      id = "ep_nhtsa_vin_free",
      name = "[Fallback #9 • API Historique] NHTSA vPIC Global VIN Registry",
      provider = "US Department of Transportation (Retrofit 2 Live)",
      endpointUrl = "https://vpic.nhtsa.dot.gov/api/vehicles/decodevinvaluesextended",
      status = "PRIORITÉ #9 (FALLBACK 8) • LIVE REST API",
      isConnected = true,
      latencyMs = 85,
      authType = "Public Rest API (Gratuit • vpic.nhtsa.dot.gov)",
      isFreeTier = true,
      cachedItemsCount = 1490
    ),
    ApiServiceEndpoint(
      id = "ep_car_api_github",
      name = "[Fallback #10] Car-API Open-Source (GitHub lifeofcapo • 142 Marques)",
      provider = "Open Source Car Database",
      endpointUrl = "local://assets/lifeofcapo-car-api-142brands.json",
      status = "PRIORITÉ #10 (FALLBACK 9) • 142 MARQUES EMBARQUÉES (1ms)",
      isConnected = true,
      latencyMs = 1,
      authType = "Base Open-Source Locale TypeScript/JSON",
      isFreeTier = true,
      cachedItemsCount = 3200
    ),
    ApiServiceEndpoint(
      id = "ep_offline_json_cache",
      name = "[Fallback #11 • API Historique] Cache Local Room SQLite / Assets JSON + ISO-3779",
      provider = "Mekanik+ Local Cache Engine & Décodeur ISO-3779",
      endpointUrl = "local://assets/catalog_oem_parts.json",
      status = "PRIORITÉ #11 (SECOURS ULTIME 100%) • 0ms HORS-LIGNE",
      isConnected = true,
      latencyMs = 1,
      authType = "Zéro coût / 100% Hors-ligne garanti",
      isFreeTier = true,
      cachedItemsCount = 5840
    )
  )

  // License Plate Database (Algérie Immatriculations & Conversion Automatique en VIN + Type Mine)
  val sampleLicensePlateMatches: List<LicensePlateMatch> = listOf(
    LicensePlateMatch(
      plateNumber = "1234512116",
      formattedDisplay = "12345 - 121 - 16",
      countryOrWilaya = "Alger (16) • Particulier 2021",
      vehicleBrand = "Volkswagen",
      vehicleModel = "Tiguan II Facelift",
      vehicleYear = 2021,
      engineCode = "DFGA",
      displacement = "2.0 TDI 150ch SCR",
      vinNumber = "WVWZZZ5NZMW123456",
      fuelType = "Diesel",
      horsepower = "8 CV Fiscaux / 150 ch DIN",
      registeredOwnerType = "Particulier (Premier Propriétaire)",
      tecDocTypeNumber = "TYP-VAG-TIG-2021-150",
      isMatchedFromLocalDb = true
    ),
    LicensePlateMatch(
      plateNumber = "0987611731",
      formattedDisplay = "09876 - 117 - 31",
      countryOrWilaya = "Oran (31) • Particulier 2017",
      vehicleBrand = "Volkswagen",
      vehicleModel = "Tiguan II Phase 1",
      vehicleYear = 2017,
      engineCode = "DFGA",
      displacement = "2.0 TDI 150ch",
      vinNumber = "WVWZZZ5NZHW098765",
      fuelType = "Diesel",
      horsepower = "8 CV Fiscaux / 150 ch DIN",
      registeredOwnerType = "Particulier",
      tecDocTypeNumber = "TYP-VAG-TIG-2017-150",
      isMatchedFromLocalDb = true
    ),
    LicensePlateMatch(
      plateNumber = "0012312225",
      formattedDisplay = "00123 - 122 - 25",
      countryOrWilaya = "Constantine (25) • Société 2022",
      vehicleBrand = "Peugeot",
      vehicleModel = "308 III (P51)",
      vehicleYear = 2022,
      engineCode = "DV5RC",
      displacement = "1.5 BlueHDi 130ch",
      vinNumber = "VF3FPHNSSMY001234",
      fuelType = "Diesel",
      horsepower = "6 CV Fiscaux / 130 ch DIN",
      registeredOwnerType = "Flotte Entreprise SARL",
      tecDocTypeNumber = "TYP-PSA-308-2022-130",
      isMatchedFromLocalDb = true
    ),
    LicensePlateMatch(
      plateNumber = "0678911919",
      formattedDisplay = "06789 - 119 - 19",
      countryOrWilaya = "Sétif (19) • Particulier 2019",
      vehicleBrand = "Renault",
      vehicleModel = "Clio V (BJA)",
      vehicleYear = 2023,
      engineCode = "H4Dt (1.0 TCe)",
      displacement = "1.0 TCe 90ch",
      vinNumber = "VF1RJA00067890123",
      fuelType = "Essence",
      horsepower = "5 CV Fiscaux / 90 ch DIN",
      registeredOwnerType = "Particulier",
      tecDocTypeNumber = "TYP-REN-CLIO-2023-90",
      isMatchedFromLocalDb = true
    ),
    LicensePlateMatch(
      plateNumber = "5432112009",
      formattedDisplay = "54321 - 120 - 09",
      countryOrWilaya = "Blida (09) • Particulier 2020",
      vehicleBrand = "Hyundai",
      vehicleModel = "Tucson IV (NX4)",
      vehicleYear = 2022,
      engineCode = "G4FP (Smartstream)",
      displacement = "1.6 T-GDi 150ch",
      vinNumber = "KMHJT81DBNU456789",
      fuelType = "Essence",
      horsepower = "9 CV Fiscaux / 150 ch DIN",
      registeredOwnerType = "Particulier",
      tecDocTypeNumber = "TYP-HYU-TUC-2022-150",
      isMatchedFromLocalDb = true
    )
  )

  val sampleWorkshop2FAConfig: Workshop2FASecurityConfig = Workshop2FASecurityConfig()

  val sampleWorkshopStoreLinkConfig: WorkshopStoreLinkConfig = WorkshopStoreLinkConfig()
}





