package com.example.data.repository

import com.example.data.model.*

object UnifiedAutoApiRepository {

  // ============================================================================
  // 1. BASE UNIFIÉE SINOAUTO360 + DONGCHEDI AUTO-API + CAR2DB LLM
  // ============================================================================

  val sinoChineseVinRecords: List<SinoChineseVinRecord> = listOf(
    SinoChineseVinRecord(
      id = "sino-jetour-dashing",
      vin = "LVVDB21B9PD048912",
      wmiPrefix = "LVV (Chery / Jetour Automobile Co., Ltd. — Wuhu, Chine)",
      brand = "Jetour",
      parentGroup = "Chery Holding Group",
      model = "Dashing (Dasheng X-1) 1.6 TGDI",
      chineseDomesticName = "捷途大圣 (Jetour Dasheng)",
      generationCode = "X-1 (Kunpeng Platform)",
      year = 2024,
      assemblyPlant = "Usine Intelligente Chery/Jetour Wuhu #3 (Anhui, Chine)",
      engineCode = "SQRF4J16",
      engineFamilyName = "Chery ACTECO / Kunpeng 1.6L TGDI GDI Turbo",
      displacementAndPower = "1598 cm³ • 197 ch (145 kW) • 290 Nm",
      transmissionCode = "7DCT300 (Double Embrayage Humide Magna 7 rapports)",
      trimFinishDongchedi = "1.6TD DCT King Pro+ (Édition Internationale / Algérie)",
      driveType = "Traction Avant (FWD) • Châssis Indépendant Multi-Bras",
      car2DbBreadcrumb = "Asie > Chine > Chery Holding > Jetour > Dashing (X-1) [2022-2026] > 1.6 TGDI 197ch (SQRF4J16) > Châssis & Freinage > Freins à Disque Avant & EPB Arrière",
      car2DbLlmSummary = "[Car2DB LLM Digest] Le Jetour Dashing 1.6 TGDI (code moteur SQRF4J16, VIN WMI LVV) partage sa plateforme modulaire et son système de freinage avec le Chery Tiggo 8 Pro. Essieu avant équipé de disques ventilés Ø 305 mm × 25 mm et étriers flottants mono-piston. Essieu arrière équipé d'un frein de stationnement électrique (EPB) intégré nécessitant la mise en mode maintenance OBD-II (ID calculateur ESP/EPB Chery) avant repoussage des pistons.",
      dongchediFunctionCallJson = """{
  "tool_call": "mekanik_unified_api.resolve_vehicle_and_oem_tree",
  "source_apis": ["SinoAuto360_v2", "Dongchedi_AutoAPI", "Car2DB_LLM", "ChineseCar_Parts"],
  "vin": "LVVDB21B9PD048912",
  "manufacturer_group": "Chery Holding / Jetour",
  "model": "Jetour Dashing 1.6 TGDI",
  "engine_code": "SQRF4J16",
  "gearbox": "7DCT300",
  "brake_specs": {
    "front_disc_mm": "305x25 (Ventilé)",
    "rear_disc_mm": "288x12 (Plein + EPB)",
    "oem_front_pads": "T15-3501080BA",
    "oem_rear_pads": "T15-3502080BA",
    "oem_front_disc": "T15-3501075"
  }
}""",
      oemPartsCatalog = listOf(
        UnifiedSinoOemPartItem(
          partId = "jetour-pad-av",
          calloutRef = "[1]",
          partName = "Jeu de 4 Plaquettes de Frein Avant Céramique OEM (Jetour Dashing / Tiggo 8)",
          systemCategory = "Freinage",
          oemPartNumber = "T15-3501080BA",
          crossReferences = listOf("Chery T15-3501080", "Brembo P11042N", "TRW GDB2184", "Bosch 0986495412"),
          sourceProvider = UnifiedApiConnectorProvider.CHINESECAR_CATCAR_JSON,
          fitmentPosition = "Essieu Avant (Étriers flottants Chery/Mando)",
          technicalDimensions = "Longueur 141.2 mm × Hauteur 59.8 mm × Épaisseur 17.5 mm (Avec témoin acoustique)",
          torqueSpecsNm = "Vis colonnettes étrier : 34 Nm • Support étrier : 115 Nm • Roue : 120 Nm",
          estimatedPriceDzd = 9800,
          stockAvailability = "En Stock (Alger / Oran / Sétif)"
        ),
        UnifiedSinoOemPartItem(
          partId = "jetour-disc-av",
          calloutRef = "[2]",
          partName = "Disque de Frein Avant Ventilé Haute Carbone Ø 305 mm",
          systemCategory = "Freinage",
          oemPartNumber = "T15-3501075",
          crossReferences = listOf("Chery T15-3501075BA", "Brembo 09.D418.11", "TRW DF8120"),
          sourceProvider = UnifiedApiConnectorProvider.SINO_AUTO_360,
          fitmentPosition = "Essieu Avant (5 trous • Entraxe 5×108)",
          technicalDimensions = "Ø Extérieur 305 mm • Épaisseur 25 mm (Min 23 mm) • Alésage 65 mm",
          torqueSpecsNm = "Vis de maintien disque : 10 Nm • Support d'étrier : 115 Nm",
          estimatedPriceDzd = 14500,
          stockAvailability = "En Stock (24h)"
        ),
        UnifiedSinoOemPartItem(
          partId = "jetour-pad-ar",
          calloutRef = "[3]",
          partName = "Jeu de 4 Plaquettes de Frein Arrière (Compatible EPB Électrique)",
          systemCategory = "Freinage",
          oemPartNumber = "T15-3502080BA",
          crossReferences = listOf("Chery T15-3502080", "TRW GDB2185", "Textar 2589101"),
          sourceProvider = UnifiedApiConnectorProvider.CHINESECAR_CATCAR_JSON,
          fitmentPosition = "Essieu Arrière (Frein de parking motorisé EPB)",
          technicalDimensions = "Longueur 106.5 mm × Hauteur 47.2 mm × Épaisseur 15.5 mm",
          torqueSpecsNm = "Colonnettes arrière : 28 Nm • Mode maintenance EPB OBD obligatoire",
          estimatedPriceDzd = 7600,
          stockAvailability = "En Stock"
        ),
        UnifiedSinoOemPartItem(
          partId = "jetour-filter-oil",
          calloutRef = "[4]",
          partName = "Filtre à Huile Moteur Kunpeng 1.6 TGDI / 1.5 TCI",
          systemCategory = "Moteur",
          oemPartNumber = "F4J16-1012010",
          crossReferences = listOf("Chery 480-1012010", "Mann W712/95", "Mahle OC1254"),
          sourceProvider = UnifiedApiConnectorProvider.DONGCHEDI_AUTO_API,
          fitmentPosition = "Bloc Moteur SQRF4J16 (Filetage M20×1.5)",
          technicalDimensions = "Ø Ext 76 mm • Hauteur 79 mm • Clapet anti-retour 1.0 bar",
          torqueSpecsNm = "Serrage filtre : 22 Nm • Bouchon carter : 30 Nm",
          estimatedPriceDzd = 2400,
          stockAvailability = "En Stock Immédiat"
        )
      )
    ),

    SinoChineseVinRecord(
      id = "sino-geely-coolray",
      vin = "L6T7824S8ND109432",
      wmiPrefix = "L6T (Zhejiang Geely Holding Group — Ningbo, Chine)",
      brand = "Geely",
      parentGroup = "Geely Auto / Volvo BMA Architecture",
      model = "Coolray (Binyue SX11) 1.5 TD",
      chineseDomesticName = "吉利缤越 (Geely Binyue Pro)",
      generationCode = "SX11 (Plateforme modulaire BMA Geely-Volvo)",
      year = 2024,
      assemblyPlant = "Usine Geely Ningbo Hangzhou Bay (Zhejiang, Chine)",
      engineCode = "BHE15-EFZ / JLH-3G15TD",
      engineFamilyName = "Geely-Volvo NordThor 1.5L Turbo Injection Directe",
      displacementAndPower = "1499 cm³ • 177/181 ch (133 kW) • 290 Nm",
      transmissionCode = "7DCT330 (Boîte Double Embrayage Humide 7 vitesses)",
      trimFinishDongchedi = "1.5TD DCT Sport+ GF (Finition Carbone / Étriers Rouges)",
      driveType = "Traction Avant (FWD) • Architecture BMA",
      car2DbBreadcrumb = "Asie > Chine > Geely Holding > Geely > Coolray / Binyue (SX11) [2020-2026] > 1.5 TD NordThor (177/181ch) > Châssis & Freinage > Étrier Sport Avant & Plaquettes",
      car2DbLlmSummary = "[Car2DB LLM Digest] Le Geely Coolray (code interne SX11, VIN WMI L6T) repose sur l'architecture BMA co-développée avec Volvo. Le système de freinage avant utilise des plaquettes haute friction réf. OEM 4048046500 (ou 8889412784 sur phase 2 NordThor 4-cylindres BHE15) associées à des disques ventilés Ø 284 mm (ou Ø 300 mm sur pack Sport+ Étriers Rouges). Remplacement arrière soumis à la procédure OBD de rétraction EPB.",
      dongchediFunctionCallJson = """{
  "tool_call": "mekanik_unified_api.resolve_vehicle_and_oem_tree",
  "source_apis": ["SinoAuto360_v2", "Dongchedi_AutoAPI", "Car2DB_LLM", "ChineseCar_Parts"],
  "vin": "L6T7824S8ND109432",
  "manufacturer_group": "Zhejiang Geely Holding",
  "model": "Geely Coolray (Binyue SX11) 1.5 TD",
  "engine_code": "BHE15-EFZ / JLH-3G15TD",
  "gearbox": "7DCT330",
  "brake_specs": {
    "front_disc_mm": "300x26 (Ventilé Pack Sport)",
    "rear_disc_mm": "270x10 (Plein + EPB)",
    "oem_front_pads": "4048046500",
    "oem_rear_pads": "4050046200",
    "oem_front_disc": "4048011300"
  }
}""",
      oemPartsCatalog = listOf(
        UnifiedSinoOemPartItem(
          partId = "geely-pad-av",
          calloutRef = "[1]",
          partName = "Jeu de 4 Plaquettes de Frein Avant Geely Coolray SX11 (Pack Sport / Standard)",
          systemCategory = "Freinage",
          oemPartNumber = "4048046500",
          crossReferences = listOf("Geely 8889412784", "Lynk&Co 8888854120", "Brembo P24198", "Bosch 0986494921"),
          sourceProvider = UnifiedApiConnectorProvider.CHINESECAR_CATCAR_JSON,
          fitmentPosition = "Essieu Avant (Étrier flottant monopiston BMA)",
          technicalDimensions = "Longueur 132.8 mm × Hauteur 56.4 mm × Épaisseur 18.0 mm",
          torqueSpecsNm = "Vis de guidage étrier : 32 Nm • Support étrier : 105 Nm • Roue : 115 Nm",
          estimatedPriceDzd = 9200,
          stockAvailability = "En Stock (Alger / Constantine / Blida)"
        ),
        UnifiedSinoOemPartItem(
          partId = "geely-disc-av",
          calloutRef = "[2]",
          partName = "Disque de Frein Avant Ventilé Geely Coolray BMA (Ø 300 mm)",
          systemCategory = "Freinage",
          oemPartNumber = "4048011300",
          crossReferences = listOf("Geely 6608001592", "Brembo 09.C882.11", "Textar 92310403"),
          sourceProvider = UnifiedApiConnectorProvider.SINO_AUTO_360,
          fitmentPosition = "Essieu Avant (5 trous • Entraxe 5×108)",
          technicalDimensions = "Ø Extérieur 300 mm • Épaisseur 26 mm (Min 23.5 mm) • Hauteur 47 mm",
          torqueSpecsNm = "Vis torx fixation disque : 12 Nm • Support étrier : 105 Nm",
          estimatedPriceDzd = 13800,
          stockAvailability = "En Stock"
        ),
        UnifiedSinoOemPartItem(
          partId = "geely-pad-ar",
          calloutRef = "[3]",
          partName = "Jeu de 4 Plaquettes de Frein Arrière Geely Coolray (Étrier EPB)",
          systemCategory = "Freinage",
          oemPartNumber = "4050046200",
          crossReferences = listOf("Geely 8889412801", "TRW GDB2310", "Ferodo FDB5120"),
          sourceProvider = UnifiedApiConnectorProvider.CHINESECAR_CATCAR_JSON,
          fitmentPosition = "Essieu Arrière (Avec servomoteur EPB)",
          technicalDimensions = "Longueur 99.6 mm × Hauteur 43.5 mm × Épaisseur 15.0 mm",
          torqueSpecsNm = "Colonnettes arrière : 26 Nm • Désactivation EPB via valise OBD",
          estimatedPriceDzd = 7100,
          stockAvailability = "En Stock"
        ),
        UnifiedSinoOemPartItem(
          partId = "geely-spark-plug",
          calloutRef = "[4]",
          partName = "Jeu de Bougies d'Allumage Iridium Laser (Moteur 1.5 TD NordThor)",
          systemCategory = "Moteur",
          oemPartNumber = "2036011500",
          crossReferences = listOf("NGK ILKR8Q7G", "Volvo 31476962", "Bosch YR6NII330X"),
          sourceProvider = UnifiedApiConnectorProvider.DONGCHEDI_AUTO_API,
          fitmentPosition = "Culasse 1.5L Turbo GDI",
          technicalDimensions = "Filetage M12×1.25 • Clé de 14 mm • Écartement 0.7 mm",
          torqueSpecsNm = "Couple de serrage impératif : 20 Nm (Culasse aluminium)",
          estimatedPriceDzd = 8400,
          stockAvailability = "En Stock"
        )
      )
    ),

    SinoChineseVinRecord(
      id = "sino-byd-songplus",
      vin = "LC0CE4DC5P0089310",
      wmiPrefix = "LC0 (BYD Auto Industry Co., Ltd. — Shenzhen, Chine)",
      brand = "BYD",
      parentGroup = "BYD Auto (Build Your Dreams)",
      model = "Song Plus DM-i / Seal U (Hybride Rechargeable)",
      chineseDomesticName = "比亚迪宋PLUS DM-i (Champion Edition)",
      generationCode = "DM-i EHS145 (Batterie Blade LFP)",
      year = 2024,
      assemblyPlant = "Usine BYD Xi'an / Shenzhen (Chine)",
      engineCode = "BYD472QA + TZ220XYE",
      engineFamilyName = "Xiaoyun 1.5L Hybride + Moteur Synchrone Aimants Permanents",
      displacementAndPower = "1498 cm³ + EHS • Puissance combinée 218 ch (160 kW) • 325 Nm",
      transmissionCode = "E-CVT EHS145 (Système Hybride Électrique Dédié)",
      trimFinishDongchedi = "DM-i 110km Flagship Pro+",
      driveType = "Traction Avant Hybride Série-Parallèle",
      car2DbBreadcrumb = "Asie > Chine > BYD > Song Plus DM-i [2021-2026] > 1.5L EHS145 Blade LFP > Freinage Régénératif IPB Bosch & Refroidissement Haute Tension",
      car2DbLlmSummary = "[Car2DB LLM Digest] Le BYD Song Plus DM-i (VIN WMI LC0) intègre le module de freinage découplé Bosch IPB (Integrated Power Brake) combinant régénération électrique et friction hydraulique. Lors du remplacement des plaquettes avant (OEM S18D-3501080) ou de la purge du liquide DOT4 ESP, il est impératif de désactiver l'auto-test IPB pour éviter la mise sous pression soudaine des étriers.",
      dongchediFunctionCallJson = """{
  "tool_call": "mekanik_unified_api.resolve_vehicle_and_oem_tree",
  "source_apis": ["SinoAuto360_v2", "Dongchedi_AutoAPI", "Car2DB_LLM"],
  "vin": "LC0CE4DC5P0089310",
  "manufacturer_group": "BYD Auto",
  "model": "BYD Song Plus DM-i (Seal U)",
  "engine_code": "BYD472QA + EHS145",
  "brake_specs": {
    "system": "Bosch IPB Decoupled Regenerative Brake",
    "oem_front_pads": "S18D-3501080",
    "oem_coolant_lfp": "BYD-OAT-LEC-II"
  }
}""",
      oemPartsCatalog = listOf(
        UnifiedSinoOemPartItem(
          partId = "byd-pad-av",
          calloutRef = "[1]",
          partName = "Jeu de Plaquettes de Frein Avant Bas-Bruit (Spécial Hybride/EV BYD)",
          systemCategory = "Freinage",
          oemPartNumber = "S18D-3501080",
          crossReferences = listOf("BYD SC2E-3501080", "Brembo P85165E", "Bosch 0986495510"),
          sourceProvider = UnifiedApiConnectorProvider.SINO_AUTO_360,
          fitmentPosition = "Essieu Avant (Système Bosch IPB Régénératif)",
          technicalDimensions = "Longueur 148.5 mm × Hauteur 61.2 mm × Épaisseur 18.2 mm",
          torqueSpecsNm = "Colonnettes : 35 Nm • Support étrier : 120 Nm",
          estimatedPriceDzd = 11500,
          stockAvailability = "En Stock"
        )
      )
    ),

    SinoChineseVinRecord(
      id = "sino-changan-cs55",
      vin = "LS4ASE2E2PA118204",
      wmiPrefix = "LS4 (Changan Automobile Group — Chongqing, Chine)",
      brand = "Changan",
      parentGroup = "Changan Auto",
      model = "CS55 Plus (2e Génération / Uni-S) 1.5T BlueCore",
      chineseDomesticName = "长安CS55 PLUS (藍鯨 NE 1.5T)",
      generationCode = "S203 (Ark Architecture)",
      year = 2024,
      assemblyPlant = "Usine Changan Yubei (Chongqing, Chine)",
      engineCode = "JL473ZQ7",
      engineFamilyName = "BlueCore NE 1.5L Turbo GDI 350 bar",
      displacementAndPower = "1494 cm³ • 188 ch (138 kW) • 300 Nm",
      transmissionCode = "DF727 (7DCT Humide Changan UK R&D)",
      trimFinishDongchedi = "1.5T DCT Luxe Automatic",
      driveType = "Traction Avant (FWD)",
      car2DbBreadcrumb = "Asie > Chine > Changan > CS55 Plus II [2021-2026] > 1.5T BlueCore NE (JL473ZQ7) > Châssis & Freinage",
      car2DbLlmSummary = "[Car2DB LLM Digest] Le Changan CS55 Plus 2e génération (VIN WMI LS4) est motorisé par le bloc BlueCore NE JL473ZQ7 à injection directe 350 bars. Plaquettes avant OEM réf. 3501110-MK03.",
      dongchediFunctionCallJson = """{
  "tool_call": "mekanik_unified_api.resolve_vehicle_and_oem_tree",
  "source_apis": ["SinoAuto360_v2", "Dongchedi_AutoAPI", "Car2DB_LLM"],
  "vin": "LS4ASE2E2PA118204",
  "manufacturer_group": "Changan Automobile",
  "model": "Changan CS55 Plus 1.5T BlueCore",
  "engine_code": "JL473ZQ7",
  "oem_front_pads": "3501110-MK03"
}""",
      oemPartsCatalog = listOf(
        UnifiedSinoOemPartItem(
          partId = "changan-pad-av",
          calloutRef = "[1]",
          partName = "Jeu de Plaquettes de Frein Avant Changan CS55 Plus / Uni-T",
          systemCategory = "Freinage",
          oemPartNumber = "3501110-MK03",
          crossReferences = listOf("Changan 3501080-AW01", "TRW GDB2290"),
          sourceProvider = UnifiedApiConnectorProvider.SINO_AUTO_360,
          fitmentPosition = "Essieu Avant",
          technicalDimensions = "Longueur 139.0 mm × Hauteur 58.0 mm × Épaisseur 17.5 mm",
          torqueSpecsNm = "Colonnettes : 32 Nm • Support étrier : 110 Nm",
          estimatedPriceDzd = 8900,
          stockAvailability = "En Stock"
        )
      )
    ),

    SinoChineseVinRecord(
      id = "sino-faw-vag-tayron",
      vin = "LFV3A28V7P3091844",
      wmiPrefix = "LFV (FAW-Volkswagen Automotive Co., Ltd. — Changchun, Chine)",
      brand = "FAW-Volkswagen",
      parentGroup = "FAW Group & Volkswagen AG (Joint-Venture)",
      model = "Tayron / Golf 8 / ID.4 Crozz (Catalogue FAW-VAG)",
      chineseDomesticName = "一汽-大众探岳 / 高尔夫 (FAW-VW Tayron 330TSI)",
      generationCode = "MQB-A2 (Code PR Freinage 1ZA / 1ZE)",
      year = 2024,
      assemblyPlant = "Usine FAW-Volkswagen Changchun / Tianjin (Chine)",
      engineCode = "DPL / EA888 Gen3B",
      engineFamilyName = "2.0L TSI EA888 186/220 ch (Cycle Miller / Dual Injection)",
      displacementAndPower = "1984 cm³ • 186 ch (137 kW) • 320 Nm",
      transmissionCode = "DQ381 (DSG 7 rapports à bain d'huile)",
      trimFinishDongchedi = "330TSI R-Line Deluxe Edition",
      driveType = "Traction / 4Motion AWD",
      car2DbBreadcrumb = "Asie > Chine > FAW-Volkswagen > Tayron / Golf 8 [MQB] > 2.0 TSI EA888 (DPL) + DSG7 DQ381 > Freinage Code PR 1ZA & Pompe à Eau",
      car2DbLlmSummary = "[Car2DB LLM Digest] Les véhicules FAW-Volkswagen (VIN WMI LFV) utilisent la plateforme MQB de VAG avec numérotation OEM identique à ETKA/IFInterface mais variantes spécifiques Chine recensées sur Dongchedi API et CatCar.info.",
      dongchediFunctionCallJson = """{
  "tool_call": "mekanik_unified_api.resolve_vehicle_and_oem_tree",
  "source_apis": ["Dongchedi_AutoAPI", "SinoAuto360_v2", "Car2DB_LLM", "CatCar_VAG"],
  "vin": "LFV3A28V7P3091844",
  "manufacturer_group": "FAW-Volkswagen",
  "model": "FAW-VW Tayron / Golf 330TSI",
  "engine_code": "EA888-DPL",
  "oem_front_pads": "5Q0698151B",
  "oem_thermostat_pump": "06L121111M"
}""",
      oemPartsCatalog = listOf(
        UnifiedSinoOemPartItem(
          partId = "faw-vag-pad-av",
          calloutRef = "[1]",
          partName = "Jeu de Plaquettes de Frein Avant MQB (Code PR 1ZA / 1ZD Ø 312 mm)",
          systemCategory = "Freinage",
          oemPartNumber = "5Q0698151B",
          crossReferences = listOf("VAG 8V0698151C", "ATE 13.0460-2785.2", "TRW GDB1956"),
          sourceProvider = UnifiedApiConnectorProvider.DONGCHEDI_AUTO_API,
          fitmentPosition = "Essieu Avant MQB (Disques Ø 312 mm)",
          technicalDimensions = "Longueur 160.0 mm × Hauteur 64.5 mm × Épaisseur 20.4 mm (Avec capteur filaire)",
          torqueSpecsNm = "Colonnettes ATE : 30 Nm • Support d'étrier : 200 Nm",
          estimatedPriceDzd = 12400,
          stockAvailability = "En Stock"
        ),
        UnifiedSinoOemPartItem(
          partId = "faw-vag-waterpump",
          calloutRef = "[2]",
          partName = "Module Pompe à Eau & Régulateur Thermique Piloté EA888 Gen3",
          systemCategory = "Refroidissement",
          oemPartNumber = "06L121111M",
          crossReferences = listOf("VAG 06L121111H", "INA 538036010", "Pierburg 7.07152.07.0"),
          sourceProvider = UnifiedApiConnectorProvider.CHINESECAR_CATCAR_JSON,
          fitmentPosition = "Avant bloc moteur EA888 sous collecteur admission",
          technicalDimensions = "Avec courroie d'entraînement crantée 06K121605 et joint",
          torqueSpecsNm = "Vis boîtier thermostat : 9 Nm • Vis poulie pompe (Filetage à gauche !) : 10 Nm + 90°",
          estimatedPriceDzd = 34500,
          stockAvailability = "En Stock"
        )
      )
    )
  )

  // ============================================================================
  // 2. SCÉNARIOS DÉMO ULTRA-PRÉCIS REVERSE-ENGINEERED (ChineseCar.parts + CatCar.info + Gemini Function Calling)
  // ============================================================================

  val reverseEngineeredDemoScenarios: List<ReverseEngineeredDemoScenario> = listOf(
    ReverseEngineeredDemoScenario(
      id = "demo-jetour-dashing-brakes",
      badgeLabel = "SCÉNARIO DÉMO INVESTISSEURS #1 • JETOUR DASHING",
      title = "Remplacement Plaquettes & Disques de Frein — Jetour Dashing 1.6 TGDI",
      subtitle = "Arborescence JSON extraite de ChineseCar.parts + Décodage VIN SinoAuto360 + Specs Dongchedi + Breadcrumb Car2DB",
      brand = "Jetour",
      model = "Dashing 1.6 TGDI (Kunpeng 197 ch)",
      year = 2024,
      vinExample = "LVVDB21B9PD048912",
      engineCode = "SQRF4J16",
      sourcePlatformNames = "ChineseCar.parts • SinoAuto360 API • Auto-API (Dongchedi) • Car2DB LLM",
      car2DbBreadcrumb = "Chery Holding > Jetour > Dashing (X-1) > 1.6 TGDI SQRF4J16 > Groupe 35 : Système de Freinage > Sous-Groupe 3501 : Freins Avant & Sous-Groupe 3502 : Freins Arrière EPB",
      car2DbLlmDigest = "Résumé LLM Car2DB : Pour le Jetour Dashing (VIN LVV...), le sous-ensemble 3501 (Frein Avant) requiert la référence OEM T15-3501080BA (plaquettes) et T15-3501075 (disques ventilés 305 mm). Le sous-ensemble 3502 (Frein Arrière EPB) requiert T15-3502080BA et impose l'exécution de la routine UDS 0x3101 « EPB Lining Change Mode » avant toute intervention mécanique.",
      geminiFunctionCallName = "mekanik_unified_api.get_oem_repair_tree_by_vin",
      geminiFunctionCallArgsJson = """{
  "vin": "LVVDB21B9PD048912",
  "target_system": "BRAKE_PAD_AND_DISC_REPLACEMENT",
  "include_dongchedi_specs": true,
  "include_car2db_breadcrumb": true,
  "include_chinesecar_parts_tree": true
}""",
      rawJsonTreeContext = """{
  "catalog_source": "ChineseCar.parts + SinoAuto360 REST v2 + Dongchedi Auto-API",
  "vehicle": {
    "vin_wmi": "LVV",
    "brand": "Jetour (Chery Group)",
    "model": "Dashing (Dasheng X-1)",
    "engine": "SQRF4J16 (1.6L TGDI 197HP / 290Nm)",
    "transmission": "7DCT300"
  },
  "car2db_llm_breadcrumb": "Jetour > Dashing (X-1) > 1.6 TGDI > 35-Braking > 3501-Front & 3502-Rear EPB",
  "oem_exploded_tree": {
    "group_code": "3501-3502",
    "diagram_name": "Front Ventilated Disc Brake & Rear EPB Caliper Assembly",
    "nodes": [
      {
        "callout": "[1]",
        "oem_part_number": "T15-3501080BA",
        "part_name_fr": "Jeu de 4 plaquettes de frein avant céramique",
        "cross_refs": ["Chery T15-3501080", "Brembo P11042N", "TRW GDB2184"],
        "dimensions_mm": "141.2 x 59.8 x 17.5",
        "torque_nm": { "caliper_guide_pins": 34, "caliper_bracket": 115 }
      },
      {
        "callout": "[2]",
        "oem_part_number": "T15-3501075",
        "part_name_fr": "Disque de frein avant ventilé Ø 305x25 mm",
        "min_thickness_mm": 23.0,
        "hub_bolt_pattern": "5x108"
      },
      {
        "callout": "[3]",
        "oem_part_number": "T15-3502080BA",
        "part_name_fr": "Jeu de 4 plaquettes de frein arrière (EPB)",
        "epb_service_routine_uds": "0x2F / 0x3101 - Ouvrir servomoteur EPB via OBD"
      }
    ]
  }
}""",
      stepByStepMechanicalProcedure = listOf(
        "1. [OBD-II / EPB] Si intervention sur l'essieu arrière : brancher l'interface Mekanik+ OBD et activer « Mode Remplacement Plaquettes EPB » (calculateur ESP/EPB Chery) pour rétracter électriquement les moteurs d'étriers.",
        "2. [Dépose Avant] Déposer les roues avant (douille 19 mm). Contrôler l'épaisseur du disque ventilé Ø 305 mm (nominale : 25.0 mm • cote minimale d'usure : 23.0 mm).",
        "3. [Étrier Flottant] Dévisser les 2 vis de colonnettes d'étrier (clé 14 mm), suspendre l'étrier sans contraindre le flexible hydraulique et extraire les anciennes plaquettes réf. T15-3501080BA.",
        "4. [Nettoyage & Repoussage] Nettoyer les portées inox du support d'étrier, graisser les colonnettes à la graisse silicone haute température et repousser le piston (en surveillant le niveau du bocal DOT4).",
        "5. [Remontage & Couples OEM] Installer les plaquettes neuves T15-3501080BA (témoin d'usure côté piston intérieur). Serrer les colonnettes à 34 Nm (support d'étrier : 115 Nm si disque remplacé) et les roues à 120 Nm.",
        "6. [Calibrage Final] Pomper la pédale de frein jusqu'à contact ferme, puis exécuter la fermeture/calibrage du point de léchage EPB via l'outil diagnostic."
      ),
      specialToolOrObdResetNote = "⚠️ ATTENTION : Ne jamais repousser les pistons arrière en force sans activer le mode maintenance EPB via OBD-II sous peine de détruire la vis sans fin du servomoteur électrique Chery/Jetour.",
      partsList = sinoChineseVinRecords[0].oemPartsCatalog
    ),

    ReverseEngineeredDemoScenario(
      id = "demo-geely-coolray-brakes",
      badgeLabel = "SCÉNARIO DÉMO INVESTISSEURS #2 • GEELY COOLRAY",
      title = "Remplacement Plaquettes de Frein & Calibrage EPB — Geely Coolray (Binyue) 1.5 TD",
      subtitle = "Arborescence JSON ChineseCar.parts + Plateforme BMA Geely-Volvo + Décodage VIN L6T SinoAuto360",
      brand = "Geely",
      model = "Coolray (Binyue SX11) 1.5 TD NordThor",
      year = 2024,
      vinExample = "L6T7824S8ND109432",
      engineCode = "BHE15-EFZ / JLH-3G15TD",
      sourcePlatformNames = "ChineseCar.parts • SinoAuto360 API • Auto-API (Dongchedi) • Car2DB LLM",
      car2DbBreadcrumb = "Geely Holding > Geely > Coolray / Binyue (SX11 - BMA) > 1.5 TD NordThor > Groupe 4048 : Freins Avant & Groupe 4050 : Freins Arrière EPB",
      car2DbLlmDigest = "Résumé LLM Car2DB : Le Geely Coolray SX11 (VIN L6T...) utilise la codification OEM Geely à 10 chiffres issue de l'architecture BMA. Plaquettes avant : 4048046500 (équivalent 8889412784). Disques avant ventilés Ø 300 mm : 4048011300. Plaquettes arrière : 4050046200.",
      geminiFunctionCallName = "mekanik_unified_api.get_oem_repair_tree_by_vin",
      geminiFunctionCallArgsJson = """{
  "vin": "L6T7824S8ND109432",
  "target_system": "BRAKE_PAD_AND_DISC_REPLACEMENT",
  "include_dongchedi_specs": true,
  "include_car2db_breadcrumb": true,
  "include_chinesecar_parts_tree": true
}""",
      rawJsonTreeContext = """{
  "catalog_source": "ChineseCar.parts + SinoAuto360 REST v2 + Dongchedi Auto-API",
  "vehicle": {
    "vin_wmi": "L6T",
    "brand": "Geely",
    "model": "Coolray / Binyue (SX11 - BMA Architecture)",
    "engine": "BHE15-EFZ / JLH-3G15TD (1.5L Turbo 177-181HP / 290Nm)",
    "transmission": "7DCT330"
  },
  "car2db_llm_breadcrumb": "Geely > Coolray (SX11) > 1.5 TD > 4048-Front Brakes & 4050-Rear EPB",
  "oem_exploded_tree": {
    "group_code": "4048-4050",
    "diagram_name": "BMA Front Sport Caliper & Rear Electric Parking Brake",
    "nodes": [
      {
        "callout": "[1]",
        "oem_part_number": "4048046500",
        "superseded_by": "8889412784",
        "part_name_fr": "Jeu de 4 plaquettes de frein avant Geely Coolray",
        "cross_refs": ["Brembo P24198", "Bosch 0986494921", "Lynk&Co 8888854120"],
        "dimensions_mm": "132.8 x 56.4 x 18.0",
        "torque_nm": { "caliper_guide_pins": 32, "caliper_bracket": 105 }
      },
      {
        "callout": "[2]",
        "oem_part_number": "4048011300",
        "part_name_fr": "Disque de frein avant ventilé Ø 300x26 mm (BMA)",
        "min_thickness_mm": 23.5
      },
      {
        "callout": "[3]",
        "oem_part_number": "4050046200",
        "part_name_fr": "Jeu de 4 plaquettes de frein arrière (EPB)",
        "epb_service_routine_uds": "Activer Mode Maintenance EPB Geely BMA avant dépose"
      }
    ]
  }
}""",
      stepByStepMechanicalProcedure = listOf(
        "1. [Identification BMA] Vérifier via le VIN L6T si le Coolray est équipé des étriers Sport Rouges (disque Ø 300 mm réf. 4048011300) ou standard (Ø 284 mm).",
        "2. [Mode Atelier EPB] Pour l'essieu arrière, mettre le contact sans démarrer, désactiver l'Auto-Hold et lancer la commande OBD « Ouverture Étriers EPB Geely SX11 ».",
        "3. [Remplacement Avant] Déposer les vis de guidage d'étrier avant (13 mm), retirer les plaquettes usagées 4048046500 et remplacer les ressorts anti-bruit.",
        "4. [Serrage Dynamométrique] Serrer les vis de guidage d'étrier avant à 32 Nm (support d'étrier à 105 Nm) et les écrous de roue en alliage à 115 Nm.",
        "5. [Rodage & Sortie Mode EPB] Quitter le mode maintenance EPB via OBD, pomper la pédale de frein et effectuer un rodage progressif sur 200 km."
      ),
      specialToolOrObdResetNote = "💡 ASTUCE TECH GEELY-VOLVO : Les plaquettes avant 4048046500 / 8889412784 sont communes avec certains modèles basés sur l'architecture BMA/CMA du groupe Geely.",
      partsList = sinoChineseVinRecords[1].oemPartsCatalog
    ),

    ReverseEngineeredDemoScenario(
      id = "demo-byd-songplus-ipb",
      badgeLabel = "SCÉNARIO DÉMO INVESTISSEURS #3 • BYD SONG PLUS DM-i",
      title = "Maintenance Freinage Régénératif Bosch IPB — BYD Song Plus DM-i / Seal U",
      subtitle = "Décodage VIN LC0 SinoAuto360 + Spécifications Hybride EHS145 Dongchedi + Résumé LLM Car2DB",
      brand = "BYD",
      model = "Song Plus DM-i (Hybride Blade LFP)",
      year = 2024,
      vinExample = "LC0CE4DC5P0089310",
      engineCode = "BYD472QA + EHS145",
      sourcePlatformNames = "SinoAuto360 API • Auto-API (Dongchedi) • Car2DB LLM",
      car2DbBreadcrumb = "BYD > Song Plus DM-i > 1.5L EHS145 > Groupe 35 : Freinage Découplé Bosch IPB & Essieu Avant",
      car2DbLlmDigest = "Résumé LLM Car2DB : Sur les véhicules BYD équipés du système Bosch IPB (Integrated Power Brake), le calculateur peut actionner la pompe haute pression même contact coupé. Débrancher la batterie 12V auxiliaire ou activer le mode atelier IPB via OBD avant d'écarter les plaquettes S18D-3501080.",
      geminiFunctionCallName = "mekanik_unified_api.get_oem_repair_tree_by_vin",
      geminiFunctionCallArgsJson = """{
  "vin": "LC0CE4DC5P0089310",
  "target_system": "REGENERATIVE_BRAKE_IPB_SERVICE",
  "include_dongchedi_specs": true,
  "include_car2db_breadcrumb": true
}""",
      rawJsonTreeContext = """{
  "catalog_source": "SinoAuto360 REST v2 + Dongchedi Auto-API + Car2DB LLM",
  "vehicle": {
    "vin_wmi": "LC0",
    "brand": "BYD",
    "model": "Song Plus DM-i (Seal U)",
    "powertrain": "1.5L Xiaoyun + EHS145 + Blade LFP Battery"
  },
  "oem_exploded_tree": {
    "group_code": "3501-IPB",
    "nodes": [
      {
        "callout": "[1]",
        "oem_part_number": "S18D-3501080",
        "part_name_fr": "Jeu de plaquettes de frein avant bas-bruit EV/DM-i",
        "torque_nm": { "caliper_guide_pins": 35, "caliper_bracket": 120 }
      }
    ]
  }
}""",
      stepByStepMechanicalProcedure = listOf(
        "1. [Sécurité Bosch IPB] Activer le mode maintenance du bloc hydraulique Bosch IPB via OBD-II (interdit l'auto-pressurisation pendant l'ouverture des étriers).",
        "2. [Remplacement Plaquettes] Déposer les plaquettes avant S18D-3501080, nettoyer les glissières et appliquer une pâte céramique anti-grincement spéciale véhicules électriques/hybrides.",
        "3. [Réinitialisation Course Pédale] Exécuter le calibrage du capteur de course de pédale IPB après remontage."
      ),
      specialToolOrObdResetNote = "⚡ SÉCURITÉ HYBRIDE/EV : Ne jamais ouvrir un purgeur sur un système Bosch IPB sans la procédure guidée OBD.",
      partsList = sinoChineseVinRecords[2].oemPartsCatalog
    )
  )

  // ============================================================================
  // 3. BASE OPEN-SOURCE LOCALE : GitHub lifeofcapo/car-api (142 Marques Embarquées)
  // ============================================================================

  val lifeOfCapoEmbeddedBrands: List<LifeOfCapoBrandCatalogEntry> = listOf(
    LifeOfCapoBrandCatalogEntry(
      brandId = "capo-geely",
      brandName = "Geely",
      parentHolding = "Zhejiang Geely Holding Group",
      originCountry = "Chine 🇨🇳",
      isChineseHighlight = true,
      generationsCount = 48,
      modelsCount = 24,
      flagshipModels = listOf("Coolray (Binyue SX11)", "Emgrand (SS11)", "Starray (Boyue L)", "Monjaro (Xingyue L)", "GX3 Pro"),
      engineCodesIndexed = listOf("BHE15-EFZ (1.5 TD)", "JLH-3G15TD", "JLC-4G15B", "JLH-4G20TD (2.0 TD Volvo)"),
      localJsonSizeKb = 184
    ),
    LifeOfCapoBrandCatalogEntry(
      brandId = "capo-chery-jetour",
      brandName = "Chery & Jetour",
      parentHolding = "Chery Automobile Co., Ltd.",
      originCountry = "Chine 🇨🇳",
      isChineseHighlight = true,
      generationsCount = 62,
      modelsCount = 31,
      flagshipModels = listOf("Jetour Dashing (X-1)", "Jetour T2 (Traveller)", "Jetour X70 Plus / X90 Plus", "Tiggo 8 Pro / Pro Max", "Tiggo 7 Pro", "Tiggo 4 Pro", "Arrizo 5 / 8"),
      engineCodesIndexed = listOf("SQRF4J16 (1.6 TGDI Kunpeng)", "SQRF4J20 (2.0 TGDI)", "SQRE4T15C (1.5 TCI)", "SQRG4G15"),
      localJsonSizeKb = 246
    ),
    LifeOfCapoBrandCatalogEntry(
      brandId = "capo-byd",
      brandName = "BYD",
      parentHolding = "BYD Auto Industry",
      originCountry = "Chine 🇨🇳",
      isChineseHighlight = true,
      generationsCount = 41,
      modelsCount = 22,
      flagshipModels = listOf("Song Plus DM-i (Seal U)", "Atto 3 (Yuan Plus)", "Seal EV", "Dolphin", "Qin Plus DM-i", "Tang EV"),
      engineCodesIndexed = listOf("BYD472QA (1.5L DM-i)", "BYD476ZQC (1.5TI)", "TZ200XSU (E-Platform 3.0)"),
      localJsonSizeKb = 172
    ),
    LifeOfCapoBrandCatalogEntry(
      brandId = "capo-changan",
      brandName = "Changan",
      parentHolding = "Changan Automobile Group",
      originCountry = "Chine 🇨🇳",
      isChineseHighlight = true,
      generationsCount = 39,
      modelsCount = 19,
      flagshipModels = listOf("CS55 Plus", "CS35 Plus", "UNI-K", "UNI-T", "UNI-V", "Alsvin", "Hunter Pickup"),
      engineCodesIndexed = listOf("JL473ZQ7 (1.5T BlueCore)", "JL473ZQ3 (1.4T)", "JL486ZQ5 (2.0T BlueCore)"),
      localJsonSizeKb = 158
    ),
    LifeOfCapoBrandCatalogEntry(
      brandId = "capo-faw-vag",
      brandName = "FAW-Volkswagen / VAG",
      parentHolding = "FAW Group & Volkswagen AG",
      originCountry = "Chine / Allemagne 🇨🇳🇩🇪",
      isChineseHighlight = true,
      generationsCount = 94,
      modelsCount = 38,
      flagshipModels = listOf("Golf 8 / GTI", "Tayron", "T-Roc", "ID.4 Crozz", "ID.6 Crozz", "Tiguan L", "Magotan / Passat"),
      engineCodesIndexed = listOf("EA211 1.4 TSI (DJR)", "EA211 1.5 TSI Evo2", "EA888 2.0 TSI (DPL/DKX)", "APP310 EV"),
      localJsonSizeKb = 310
    ),
    LifeOfCapoBrandCatalogEntry(
      brandId = "capo-gwm-haval",
      brandName = "Great Wall / Haval / Tank",
      parentHolding = "Great Wall Motor (GWM)",
      originCountry = "Chine 🇨🇳",
      isChineseHighlight = true,
      generationsCount = 34,
      modelsCount = 17,
      flagshipModels = listOf("Haval H6 (3rd Gen / HEV)", "Haval Jolion", "Tank 300", "Poer Pickup"),
      engineCodesIndexed = listOf("GW4B15A (1.5 GDIT)", "GW4N20 (2.0 GDIT)"),
      localJsonSizeKb = 142
    ),
    LifeOfCapoBrandCatalogEntry(
      brandId = "capo-renault-dacia",
      brandName = "Renault & Dacia",
      parentHolding = "Renault Group",
      originCountry = "France / Roumanie 🇫🇷🇷🇴",
      isChineseHighlight = false,
      generationsCount = 112,
      modelsCount = 42,
      flagshipModels = listOf("Clio 4 / Clio 5", "Symbol DZ", "Megane 4", "Duster", "Sandero Stepway", "Master 3"),
      engineCodesIndexed = listOf("K9K 872 (1.5 dCi)", "H4Bt (0.9 TCe)", "H5Ht (1.3 TCe)", "K4M (1.6 16v)"),
      localJsonSizeKb = 340
    ),
    LifeOfCapoBrandCatalogEntry(
      brandId = "capo-stellantis",
      brandName = "Peugeot, Citroën & Fiat",
      parentHolding = "Stellantis N.V.",
      originCountry = "Europe 🇫🇷🇮🇹",
      isChineseHighlight = false,
      generationsCount = 128,
      modelsCount = 54,
      flagshipModels = listOf("208 / 308 / 3008", "Partner / Berlingo", "Fiat Tipo DZ", "Fiat 500X / Doblo"),
      engineCodesIndexed = listOf("DV6ATED4 (1.6 HDi)", "DV5RC (1.5 BlueHDi)", "EB2ADTS (1.2 PureTech)", "55280444 (1.4 Fire)"),
      localJsonSizeKb = 390
    )
  )

  // ============================================================================
  // 4. MÉCANISME DE FALLBACK EN CASCADE & MOTEUR D'AGRÉGATION IA
  // Si une API est indisponible (503/504/429) ou ne trouve pas le VIN (404),
  // le Repository interroge automatiquement la source suivante dans la liste.
  // ============================================================================

  val defaultFallbackChainConfigs: List<FallbackSourceConfig> = listOf(
    FallbackSourceConfig(
      priorityOrder = 1,
      provider = UnifiedApiConnectorProvider.SINO_AUTO_360,
      simulatedHealthState = ApiSourceHealthState.ONLINE,
      supportedWmiPrefixes = listOf("LVV", "L6T", "LC0", "LS4", "LGW"),
      coverageDescription = "Priorité #1 (Nouvelle API Chine) : Constructeurs Nationaux Chinois (Jetour/Chery LVV, Geely L6T, BYD LC0, Changan LS4, GWM LGW)"
    ),
    FallbackSourceConfig(
      priorityOrder = 2,
      provider = UnifiedApiConnectorProvider.DONGCHEDI_AUTO_API,
      simulatedHealthState = ApiSourceHealthState.ONLINE,
      supportedWmiPrefixes = listOf("LVV", "L6T", "LC0", "LS4", "LFV", "LSV", "LBV", "LHG"),
      coverageDescription = "Priorité #2 (Fallback 1 • Nouvelle API Chine) : Auto-API Dongchedi (Marques Chinoises + Joint-Ventures FAW-VAG LFV & SAIC-VW LSV)"
    ),
    FallbackSourceConfig(
      priorityOrder = 3,
      provider = UnifiedApiConnectorProvider.CAR2DB_LLM_API,
      simulatedHealthState = ApiSourceHealthState.ONLINE,
      supportedWmiPrefixes = listOf("LVV", "L6T", "LC0", "LS4", "LFV", "WVW", "WAU", "VF1", "VF3", "VF7", "KMH", "KM8", "KNA", "WBA", "WDB"),
      coverageDescription = "Priorité #3 (Fallback 2 • Nouvelle API IA) : Car2DB LLM API (Couverture Internationale Chine + Europe + Corée + Fil d'Ariane IA)"
    ),
    FallbackSourceConfig(
      priorityOrder = 4,
      provider = UnifiedApiConnectorProvider.CHINESECAR_CATCAR_JSON,
      simulatedHealthState = ApiSourceHealthState.ONLINE,
      supportedWmiPrefixes = listOf("LVV", "L6T", "LC0", "VF1", "VF3", "VF7", "ZFA", "KMH", "KNA", "WVW", "VSS", "TMB"),
      coverageDescription = "Priorité #4 (Fallback 3 • Nouvelle API OEM Web) : ChineseCar.parts & CatCar.info (Arborescences JSON Chine, Renault, Stellantis, VAG, Corée)"
    ),
    FallbackSourceConfig(
      priorityOrder = 5,
      provider = UnifiedApiConnectorProvider.OEM_SERVICEBOX_DIALOGYS_ETKA,
      simulatedHealthState = ApiSourceHealthState.ONLINE,
      supportedWmiPrefixes = listOf("VF1", "VF3", "VF7", "VR3", "VR7", "WVW", "WAU", "VSS", "TMB", "WBA", "WDB", "ZFA", "JT", "MR0", "KMH", "KM8", "KNA"),
      coverageDescription = "Priorité #5 (Fallback 4 • API Historique Constructeurs) : Stellantis ServiceBox EPC, Renault Dialogys, VAG ETKA, BMW ETK, Mercedes WIS & Microcat Asie"
    ),
    FallbackSourceConfig(
      priorityOrder = 6,
      provider = UnifiedApiConnectorProvider.TECDOC_ALLIANCE_API,
      simulatedHealthState = ApiSourceHealthState.ONLINE,
      supportedWmiPrefixes = listOf("VF1", "VF3", "VF7", "WVW", "WAU", "WBA", "WDB", "ZFA", "VSS", "TMB", "KMH", "KM8", "KNA", "JT", "MR0", "LVV", "L6T"),
      coverageDescription = "Priorité #6 (Fallback 5 • API Historique Multimarque) : TecDoc / TecAlliance Sandbox & Pro (900+ Équipementiers IAM Bosch/Valeo/Brembo & KType)"
    ),
    FallbackSourceConfig(
      priorityOrder = 7,
      provider = UnifiedApiConnectorProvider.HAYNESPRO_AUTODATA_API,
      simulatedHealthState = ApiSourceHealthState.ONLINE,
      supportedWmiPrefixes = listOf("VF1", "VF3", "VF7", "WVW", "WAU", "WBA", "WDB", "ZFA", "VSS", "TMB", "KMH", "KM8", "KNA", "JT", "5YJ"),
      coverageDescription = "Priorité #7 (Fallback 6 • API Historique Atelier) : HaynesPro WorkshopData Ultimate & Autodata Connect (Couples OEM, Schémas VESA & SmartCASE)"
    ),
    FallbackSourceConfig(
      priorityOrder = 8,
      provider = UnifiedApiConnectorProvider.GLOBAL_OEM_7ZAP_TECDOC,
      simulatedHealthState = ApiSourceHealthState.ONLINE,
      supportedWmiPrefixes = listOf("VF1", "VF3", "WVW", "WAU", "WBA", "WDB", "JT", "MR0", "JN1", "JMZ", "KMH", "KNA"),
      coverageDescription = "Priorité #8 (Fallback 7 • API Historique EPC) : Passerelle Internationale 7Zap & PartsSouq EPC (Schémas éclatés Europe / Japon / Corée / Golfe)"
    ),
    FallbackSourceConfig(
      priorityOrder = 9,
      provider = UnifiedApiConnectorProvider.NHTSA_VPIC_PUBLIC_API,
      simulatedHealthState = ApiSourceHealthState.ONLINE,
      supportedWmiPrefixes = listOf("5YJ", "7SA", "1FA", "1FT", "1G1", "WVW", "WAU", "WBA", "WDB", "JT", "KMH", "KNA"),
      coverageDescription = "Priorité #9 (Fallback 8 • API Historique REST Live) : NHTSA vPIC Global VIN Registry (API publique US DOT vpic.nhtsa.dot.gov • Tesla / US / Monde)"
    ),
    FallbackSourceConfig(
      priorityOrder = 10,
      provider = UnifiedApiConnectorProvider.GITHUB_LIFEOFCAPO_CAR_API,
      simulatedHealthState = ApiSourceHealthState.ONLINE,
      supportedWmiPrefixes = listOf("ALL_142_BRANDS_LOCAL"),
      coverageDescription = "Priorité #10 (Fallback 9 • Open-Source 1ms) : Base Locale Embarquée GitHub lifeofcapo/car-api (142 Marques & Générations indexées)"
    ),
    FallbackSourceConfig(
      priorityOrder = 11,
      provider = UnifiedApiConnectorProvider.OFFLINE_ROOM_JSON_CACHE,
      simulatedHealthState = ApiSourceHealthState.ONLINE,
      supportedWmiPrefixes = listOf("UNIVERSAL_OFFLINE_ROOM_ISO3779"),
      coverageDescription = "Priorité #11 (Secours Ultime Historique 0ms • 100% Garanti) : Cache Local Room SQLite / Assets JSON + Décodeur Mathématique ISO-3779"
    )
  )

  var currentFallbackChainConfigs: List<FallbackSourceConfig> = defaultFallbackChainConfigs

  fun updateProviderHealthState(
    provider: UnifiedApiConnectorProvider,
    newState: ApiSourceHealthState
  ): List<FallbackSourceConfig> {
    currentFallbackChainConfigs = currentFallbackChainConfigs.map { cfg ->
      if (cfg.provider == provider) cfg.copy(simulatedHealthState = newState) else cfg
    }
    return currentFallbackChainConfigs
  }

  fun moveProviderPriorityUp(provider: UnifiedApiConnectorProvider): List<FallbackSourceConfig> {
    val sorted = currentFallbackChainConfigs.sortedBy { it.priorityOrder }.toMutableList()
    val idx = sorted.indexOfFirst { it.provider == provider }
    if (idx > 0) {
      val prev = sorted[idx - 1]
      val curr = sorted[idx]
      sorted[idx - 1] = curr.copy(priorityOrder = prev.priorityOrder)
      sorted[idx] = prev.copy(priorityOrder = curr.priorityOrder)
      currentFallbackChainConfigs = sorted.sortedBy { it.priorityOrder }
    }
    return currentFallbackChainConfigs
  }

  fun resetFallbackChainToAllOnline(): List<FallbackSourceConfig> {
    currentFallbackChainConfigs = defaultFallbackChainConfigs
    return currentFallbackChainConfigs
  }

  fun applyChaosOutageSimulationPreset(): List<FallbackSourceConfig> {
    // Simule une panne 503 sur #1 SinoAuto360 et un Timeout 504 sur #2 Dongchedi
    // pour démontrer la bascule automatique vers #3 Car2DB LLM API
    currentFallbackChainConfigs = defaultFallbackChainConfigs.map { cfg ->
      when (cfg.provider) {
        UnifiedApiConnectorProvider.SINO_AUTO_360 ->
          cfg.copy(simulatedHealthState = ApiSourceHealthState.UNAVAILABLE_503)
        UnifiedApiConnectorProvider.DONGCHEDI_AUTO_API ->
          cfg.copy(simulatedHealthState = ApiSourceHealthState.TIMEOUT_504)
        else ->
          cfg.copy(simulatedHealthState = ApiSourceHealthState.ONLINE)
      }
    }
    return currentFallbackChainConfigs
  }

  fun applyLegacyApisFallbackSimulationPreset(): List<FallbackSourceConfig> {
    // Simule l'indisponibilité des 4 nouvelles APIs Chine/IA (#1 à #4)
    // pour prouver que le Fallback bascule automatiquement vers les APIs historiques d'avant :
    // #5 ServiceBox/Dialogys/ETKA -> #6 TecDoc Alliance -> #7 HaynesPro/Autodata -> #8 7Zap/PartsSouq -> #9 NHTSA vPIC -> #10 lifeofcapo -> #11 Room/JSON Offline
    currentFallbackChainConfigs = defaultFallbackChainConfigs.map { cfg ->
      when (cfg.provider) {
        UnifiedApiConnectorProvider.SINO_AUTO_360 ->
          cfg.copy(simulatedHealthState = ApiSourceHealthState.UNAVAILABLE_503)
        UnifiedApiConnectorProvider.DONGCHEDI_AUTO_API ->
          cfg.copy(simulatedHealthState = ApiSourceHealthState.TIMEOUT_504)
        UnifiedApiConnectorProvider.CAR2DB_LLM_API ->
          cfg.copy(simulatedHealthState = ApiSourceHealthState.RATE_LIMITED_429)
        UnifiedApiConnectorProvider.CHINESECAR_CATCAR_JSON ->
          cfg.copy(simulatedHealthState = ApiSourceHealthState.UNAVAILABLE_503)
        else ->
          cfg.copy(simulatedHealthState = ApiSourceHealthState.ONLINE)
      }
    }
    return currentFallbackChainConfigs
  }

  fun findExactSinoRecordOrNull(query: String): SinoChineseVinRecord? {
    val clean = query.trim().uppercase()
    if (clean.isEmpty()) return null
    return sinoChineseVinRecords.firstOrNull { record ->
      record.vin.uppercase() == clean ||
        (clean.length >= 3 && record.vin.uppercase().startsWith(clean.take(3)) && clean.first() == 'L') ||
        record.brand.uppercase().contains(clean) ||
        record.model.uppercase().contains(clean) ||
        record.engineCode.uppercase().contains(clean) ||
        (clean.contains("JETOUR") && record.brand.equals("Jetour", ignoreCase = true)) ||
        (clean.contains("DASHING") && record.model.contains("Dashing", ignoreCase = true)) ||
        (clean.contains("GEELY") && record.brand.equals("Geely", ignoreCase = true)) ||
        (clean.contains("COOLRAY") && record.model.contains("Coolray", ignoreCase = true)) ||
        (clean.contains("BINYUE") && record.model.contains("Binyue", ignoreCase = true)) ||
        (clean.contains("BYD") && record.brand.equals("BYD", ignoreCase = true)) ||
        (clean.contains("CHANGAN") && record.brand.equals("Changan", ignoreCase = true)) ||
        (clean.contains("FAW") && record.brand.contains("FAW", ignoreCase = true))
    }
  }

  fun findSinoRecordByQuery(query: String): SinoChineseVinRecord {
    return findExactSinoRecordOrNull(query) ?: sinoChineseVinRecords.first()
  }

  /**
   * Vérifie si une source API donnée (nouvelle OU historique d'avant) contient le VIN ou le modèle demandé.
   * Si elle retourne (false, raison), le Repository déclenche automatiquement le fallback vers la source suivante.
   */
  private fun canProviderResolveQuery(
    provider: UnifiedApiConnectorProvider,
    cleanQuery: String
  ): Pair<Boolean, String> {
    val wmi = cleanQuery.take(3)
    val isPureChineseDomestic = wmi in listOf("LVV", "L6T", "LC0", "LS4", "LGW") ||
      cleanQuery.contains("JETOUR") ||
      cleanQuery.contains("DASHING") ||
      cleanQuery.contains("GEELY") ||
      cleanQuery.contains("COOLRAY") ||
      cleanQuery.contains("BYD") ||
      cleanQuery.contains("CHANGAN") ||
      cleanQuery.contains("CHERY")
    val isSinoJointVenture = wmi in listOf("LFV", "LSV", "LBV", "LHG") ||
      cleanQuery.contains("FAW") ||
      cleanQuery.contains("TAYRON")
    val isEuroOrAsianOem = wmi in listOf("VF1", "VF3", "VF7", "VR3", "VR7", "WVW", "WAU", "WBA", "WDB", "ZFA", "KMH", "KM8", "KNA", "VSS", "TMB", "JT", "MR0", "JN1", "JMZ", "YV1") ||
      cleanQuery.contains("RENAULT") ||
      cleanQuery.contains("PEUGEOT") ||
      cleanQuery.contains("CITROEN") ||
      cleanQuery.contains("VOLKSWAGEN") ||
      cleanQuery.contains("GOLF") ||
      cleanQuery.contains("CLIO") ||
      cleanQuery.contains("HYUNDAI") ||
      cleanQuery.contains("KIA") ||
      cleanQuery.contains("TOYOTA") ||
      cleanQuery.contains("BMW") ||
      cleanQuery.contains("MERCEDES") ||
      cleanQuery.contains("AUDI")
    val isUsOrGlobalNhtsa = wmi in listOf("5YJ", "7SA", "1FA", "1FT", "1FM", "1G1", "1GC", "WVW", "WAU", "WBA", "WDB", "JT", "KMH", "KM8", "KNA") ||
      cleanQuery.contains("TESLA") ||
      cleanQuery.contains("FORD")

    return when (provider) {
      UnifiedApiConnectorProvider.SINO_AUTO_360 -> {
        if (isPureChineseDomestic) {
          Pair(true, "VIN constructeur chinois ($wmi) identifié dans la base usines SinoAuto360 v2.")
        } else if (isSinoJointVenture) {
          Pair(false, "VIN Joint-Venture ($wmi) non couvert par le endpoint constructeurs nationaux SinoAuto360 (404 Not Found).")
        } else {
          Pair(false, "VIN hors Chine ($wmi) absent du référentiel SinoAuto360 (404 VIN Not Found).")
        }
      }
      UnifiedApiConnectorProvider.DONGCHEDI_AUTO_API -> {
        if (isPureChineseDomestic || isSinoJointVenture) {
          Pair(true, "Spécifications techniques, finitions & moteur extraites via Auto-API Dongchedi (JSON Function Calling).")
        } else {
          Pair(false, "Véhicule européen/international ($wmi) non répertorié sur le marché domestique Dongchedi CN (404 Not Found).")
        }
      }
      UnifiedApiConnectorProvider.CAR2DB_LLM_API -> {
        if (isPureChineseDomestic || isSinoJointVenture || isEuroOrAsianOem) {
          Pair(true, "Véhicule résolu via Car2DB API avec structure en Fil d'Ariane (Breadcrumb) et résumé LLM.")
        } else {
          Pair(false, "WMI nord-américain/spécial ($wmi) non indexé dans l'édition d'évaluation Car2DB LLM (404 Not Found).")
        }
      }
      UnifiedApiConnectorProvider.CHINESECAR_CATCAR_JSON -> {
        if (isPureChineseDomestic || isEuroOrAsianOem) {
          Pair(true, "Arborescence de pièces OEM résolue via ChineseCar.parts / CatCar.info.")
        } else {
          Pair(false, "Arborescence éclatée non disponible pour ce code ($wmi) sur ChineseCar.parts / CatCar.info (404).")
        }
      }
      UnifiedApiConnectorProvider.OEM_SERVICEBOX_DIALOGYS_ETKA -> {
        if (isEuroOrAsianOem || isSinoJointVenture) {
          Pair(true, "Véhicule résolu par les Portails Constructeurs Historiques (Stellantis ServiceBox / Renault Dialogys / VAG ETKA / Microcat).")
        } else {
          Pair(false, "Marque hors portails européens/asiatiques ServiceBox / Dialogys / ETKA ($wmi) (404 Not Found).")
        }
      }
      UnifiedApiConnectorProvider.TECDOC_ALLIANCE_API -> {
        if (isEuroOrAsianOem || isPureChineseDomestic || isSinoJointVenture) {
          Pair(true, "Véhicule & équivalences équipementiers IAM (Bosch/Valeo/Brembo) résolus via TecDoc / TecAlliance API.")
        } else {
          Pair(false, "KType TecDoc non trouvé pour ce châssis spécifique ($wmi) (404 Not Found).")
        }
      }
      UnifiedApiConnectorProvider.HAYNESPRO_AUTODATA_API -> {
        if (isEuroOrAsianOem || isUsOrGlobalNhtsa || isPureChineseDomestic) {
          Pair(true, "Données d'atelier, couples de serrage OEM & schémas VESA résolus via HaynesPro WorkshopData & Autodata API.")
        } else {
          Pair(false, "Dossier technique WorkshopData non disponible pour ce WMI ($wmi) (404 Not Found).")
        }
      }
      UnifiedApiConnectorProvider.GLOBAL_OEM_7ZAP_TECDOC -> {
        if (isEuroOrAsianOem || isSinoJointVenture) {
          Pair(true, "Schémas éclatés OEM résolus via la passerelle historique 7Zap & PartsSouq EPC.")
        } else {
          Pair(false, "Châssis ($wmi) absent de la passerelle 7Zap / PartsSouq (404 Not Found).")
        }
      }
      UnifiedApiConnectorProvider.NHTSA_VPIC_PUBLIC_API -> {
        if (isUsOrGlobalNhtsa || isEuroOrAsianOem) {
          Pair(true, "Décodage officiel réussi via l'API publique REST NHTSA vPIC (vpic.nhtsa.dot.gov).")
        } else {
          Pair(false, "Constructeur domestique non homologué US DOT NHTSA vPIC ($wmi) (404 Not Found).")
        }
      }
      UnifiedApiConnectorProvider.GITHUB_LIFEOFCAPO_CAR_API -> {
        Pair(true, "Véhicule identifié localement à 1 ms dans la base open-source embarquée GitHub lifeofcapo/car-api (142 marques).")
      }
      UnifiedApiConnectorProvider.OFFLINE_ROOM_JSON_CACHE -> {
        Pair(true, "Véhicule résolu à 0 ms via le Cache Local Historique (Room SQLite / Assets JSON + Décodeur Mathématique ISO-3779).")
      }
    }
  }

  /**
   * Algorithme principal de résolution avec Fallback automatique en cascade.
   * Parcourt les sources par ordre de priorité :
   * - Si une API est indisponible (503 / Timeout 504 / Quota 429) -> passe automatiquement à la source suivante.
   * - Si une API répond mais ne trouve pas le VIN (404) -> passe automatiquement à la source suivante.
   * - Dès qu'une source résout le VIN, l'exécution s'arrête et retourne le véhicule ainsi que la trace complète.
   */
  fun resolveVinWithFallbackCascade(
    queryInput: String,
    sourceConfigs: List<FallbackSourceConfig> = currentFallbackChainConfigs
  ): FallbackCascadeResolutionResult {
    val clean = queryInput.trim().uppercase().ifEmpty { "LVVDB21B9PD048912" }
    val orderedConfigs = sourceConfigs.sortedBy { it.priorityOrder }
    val steps = mutableListOf<FallbackAttemptStep>()

    var winningProvider: UnifiedApiConnectorProvider? = null
    var winningStepOrder = 1
    var cumulativeLatencyMs = 0
    var fallbackHops = 0

    for ((index, config) in orderedConfigs.withIndex()) {
      val stepNum = index + 1
      val nextProviderName = orderedConfigs.getOrNull(index + 1)?.provider?.displayName

      if (winningProvider != null) {
        steps.add(
          FallbackAttemptStep(
            stepOrder = stepNum,
            provider = config.provider,
            status = FallbackAttemptStatus.STANDBY_NOT_SOLICITED,
            httpStatusCode = 0,
            latencyMs = 0,
            diagnosticReason = "Non sollicitée — VIN déjà résolu en amont par ${winningProvider.displayName} (Appel économisé).",
            nextFallbackTargetName = null
          )
        )
        continue
      }

      when (config.simulatedHealthState) {
        ApiSourceHealthState.UNAVAILABLE_503 -> {
          val hopLatency = 16
          cumulativeLatencyMs += hopLatency
          fallbackHops++
          steps.add(
            FallbackAttemptStep(
              stepOrder = stepNum,
              provider = config.provider,
              status = FallbackAttemptStatus.FALLBACK_API_UNAVAILABLE,
              httpStatusCode = 503,
              latencyMs = hopLatency,
              diagnosticReason = "API ${config.provider.displayName} indisponible (HTTP 503 Service Unavailable).",
              nextFallbackTargetName = nextProviderName
            )
          )
        }
        ApiSourceHealthState.TIMEOUT_504 -> {
          val hopLatency = 280
          cumulativeLatencyMs += hopLatency
          fallbackHops++
          steps.add(
            FallbackAttemptStep(
              stepOrder = stepNum,
              provider = config.provider,
              status = FallbackAttemptStatus.FALLBACK_TIMEOUT,
              httpStatusCode = 504,
              latencyMs = hopLatency,
              diagnosticReason = "Circuit-Breaker déclenché sur ${config.provider.displayName} (Timeout > 250ms).",
              nextFallbackTargetName = nextProviderName
            )
          )
        }
        ApiSourceHealthState.RATE_LIMITED_429 -> {
          val hopLatency = 12
          cumulativeLatencyMs += hopLatency
          fallbackHops++
          steps.add(
            FallbackAttemptStep(
              stepOrder = stepNum,
              provider = config.provider,
              status = FallbackAttemptStatus.FALLBACK_RATE_LIMITED,
              httpStatusCode = 429,
              latencyMs = hopLatency,
              diagnosticReason = "Quota de requêtes dépassé sur ${config.provider.displayName} (HTTP 429 Rate Limit).",
              nextFallbackTargetName = nextProviderName
            )
          )
        }
        ApiSourceHealthState.VIN_NOT_IN_CATALOG -> {
          val hopLatency = config.provider.averageLatencyMs
          cumulativeLatencyMs += hopLatency
          fallbackHops++
          steps.add(
            FallbackAttemptStep(
              stepOrder = stepNum,
              provider = config.provider,
              status = FallbackAttemptStatus.FALLBACK_VIN_NOT_FOUND,
              httpStatusCode = 404,
              latencyMs = hopLatency,
              diagnosticReason = "VIN '$clean' introuvable dans ${config.provider.displayName} (HTTP 404 Not Found).",
              nextFallbackTargetName = nextProviderName
            )
          )
        }
        ApiSourceHealthState.ONLINE -> {
          val (canResolve, reason) = canProviderResolveQuery(config.provider, clean)
          val hopLatency = config.provider.averageLatencyMs
          cumulativeLatencyMs += hopLatency

          if (canResolve) {
            winningProvider = config.provider
            winningStepOrder = stepNum
            steps.add(
              FallbackAttemptStep(
                stepOrder = stepNum,
                provider = config.provider,
                status = FallbackAttemptStatus.RESOLVED_SUCCESS,
                httpStatusCode = 200,
                latencyMs = hopLatency,
                diagnosticReason = reason,
                nextFallbackTargetName = null
              )
            )
          } else {
            fallbackHops++
            steps.add(
              FallbackAttemptStep(
                stepOrder = stepNum,
                provider = config.provider,
                status = FallbackAttemptStatus.FALLBACK_VIN_NOT_FOUND,
                httpStatusCode = 404,
                latencyMs = hopLatency,
                diagnosticReason = reason,
                nextFallbackTargetName = nextProviderName
              )
            )
          }
        }
      }
    }

    // Si toutes les sources ont été forcées en panne par l'utilisateur, le secours local ultime #6 / #5 prend le relais
    val finalWinner = winningProvider ?: UnifiedApiConnectorProvider.GITHUB_LIFEOFCAPO_CAR_API
    val exactSino = findExactSinoRecordOrNull(clean)
    val resolvedVehicle: Vehicle = if (exactSino != null) {
      toMekanikVehicle(exactSino).copy(
        generationNotes = "[Résolu par ${finalWinner.displayName} après $fallbackHops bascule(s)] ${exactSino.car2DbLlmSummary}"
      )
    } else {
      val euroMatch = MekanikRepository.sampleVehicles.find {
        it.vin.equals(clean, ignoreCase = true) ||
          clean.contains(it.brand.uppercase()) ||
          clean.contains(it.model.uppercase()) ||
          (clean.length >= 3 && it.vin.uppercase().startsWith(clean.take(3)))
      } ?: MekanikRepository.sampleVehicles.first()
      euroMatch.copy(
        generationNotes = "[Résolu par ${finalWinner.displayName} après $fallbackHops bascule(s)] ${euroMatch.generationNotes}"
      )
    }

    val summaryBanner = if (fallbackHops == 0) {
      "✅ Résolution directe au 1er essai via ${finalWinner.displayName} ($cumulativeLatencyMs ms • 0 fallback)"
    } else {
      "🔄 Fallback Automatique Réussi : $fallbackHops source(s) indisponible(s)/404 → Résolu par #$winningStepOrder ${finalWinner.displayName} ($cumulativeLatencyMs ms total)"
    }

    return FallbackCascadeResolutionResult(
      queryVinOrInput = clean,
      resolvedSuccessfully = winningProvider != null,
      winningProvider = finalWinner,
      winningStepOrder = winningStepOrder,
      fallbackHopsCount = fallbackHops,
      totalCascadeLatencyMs = cumulativeLatencyMs,
      attemptSteps = steps,
      resolvedVehicle = resolvedVehicle,
      resolvedSinoRecord = exactSino,
      cascadeSummaryBanner = summaryBanner
    )
  }

  fun executeUnifiedApiQuery(
    queryInput: String,
    scenarioId: String? = null,
    sourceConfigs: List<FallbackSourceConfig> = currentFallbackChainConfigs
  ): UnifiedSingleApiExecutionTrace {
    val scenario = scenarioId?.let { id ->
      reverseEngineeredDemoScenarios.firstOrNull { it.id == id }
    } ?: reverseEngineeredDemoScenarios.firstOrNull { sc ->
      queryInput.contains(sc.brand, ignoreCase = true) ||
        queryInput.contains(sc.model.take(7), ignoreCase = true) ||
        queryInput.contains(sc.vinExample.take(6), ignoreCase = true)
    }

    val effectiveQuery = scenario?.vinExample ?: queryInput.ifBlank { "LVVDB21B9PD048912" }
    val cascadeResult = resolveVinWithFallbackCascade(effectiveQuery, sourceConfigs)
    val matchedRecord = cascadeResult.resolvedSinoRecord ?: findSinoRecordByQuery(effectiveQuery)

    val fallbackStepsJson = cascadeResult.attemptSteps.joinToString(",\n") { step ->
      """      {
        "priority_order": ${step.stepOrder},
        "source_api": "${step.provider.displayName}",
        "http_status": ${step.httpStatusCode},
        "status": "${step.status.name}",
        "latency_ms": ${step.latencyMs},
        "reason": "${step.diagnosticReason.replace("\"", "'")}"${if (step.nextFallbackTargetName != null) ",\n        \"auto_fallback_to\": \"${step.nextFallbackTargetName}\"" else ""}
      }"""
    }

    val unifiedJson = """{
  "api_gateway": "MekanikOne Unified Auto-API v1.0 (AI Aggregator + Auto-Fallback Engine)",
  "endpoint": "POST https://api.mekanik-plus.ai/v1/unified-auto-graph/resolve",
  "status": "200 OK (WINNING_SOURCE: ${cascadeResult.winningProvider.displayName})",
  "fallback_hops_triggered": ${cascadeResult.fallbackHopsCount},
  "total_cascade_latency_ms": ${cascadeResult.totalCascadeLatencyMs},
  "fallback_cascade_trace": [
$fallbackStepsJson
  ],
  "aggregated_pipelines": {
    "1_sinoauto360_vin_oem": {
      "status": "DECODED",
      "vin": "${matchedRecord.vin}",
      "wmi_factory": "${matchedRecord.wmiPrefix}",
      "assembly_plant": "${matchedRecord.assemblyPlant}",
      "oem_parts_matched": ${matchedRecord.oemPartsCatalog.size}
    },
    "2_dongchedi_auto_api": {
      "status": "EXTRACTED_FOR_GEMINI_FN_CALL",
      "brand": "${matchedRecord.brand}",
      "domestic_cn_name": "${matchedRecord.chineseDomesticName}",
      "trim_finish": "${matchedRecord.trimFinishDongchedi}",
      "engine_code": "${matchedRecord.engineCode}",
      "powertrain_specs": "${matchedRecord.displacementAndPower}",
      "transmission": "${matchedRecord.transmissionCode}"
    },
    "3_car2db_llm_optimized": {
      "breadcrumb": "${matchedRecord.car2DbBreadcrumb}",
      "llm_summary": "${matchedRecord.car2DbLlmSummary}"
    },
    "4_github_lifeofcapo_car_api": {
      "local_cache": "HIT (0ms - 142 Brands Embedded)",
      "generation_code": "${matchedRecord.generationCode}"
    },
    "5_chinesecar_parts_catcar_tree": {
      "exploded_nodes": [
${
      matchedRecord.oemPartsCatalog.joinToString(",\n") { part ->
        """        {
          "callout": "${part.calloutRef}",
          "oem_ref": "${part.oemPartNumber}",
          "name": "${part.partName}",
          "cross_refs": [${part.crossReferences.joinToString(", ") { "\"$it\"" }}],
          "torque_nm": "${part.torqueSpecsNm}",
          "price_dzd": ${part.estimatedPriceDzd}
        }"""
      }
    }
      ]
    }
  }
}"""

    return UnifiedSingleApiExecutionTrace(
      requestId = "MK1-UNIFIED-${(100000..999999).random()}",
      timestamp = "Temps réel • ${cascadeResult.totalCascadeLatencyMs} ms (${cascadeResult.fallbackHopsCount} bascule(s) Fallback)",
      queryInput = effectiveQuery,
      queryType = if (scenario != null) "SCENARIO_JSON_TREE + GEMINI_FN_CALL" else "UNIFIED_VIN_WITH_AUTO_FALLBACK",
      totalMergedLatencyMs = cascadeResult.totalCascadeLatencyMs,
      providersQueried = UnifiedApiConnectorProvider.entries,
      matchedVinRecord = matchedRecord,
      activeScenario = scenario,
      unifiedResponseJson = unifiedJson,
      fallbackCascade = cascadeResult
    )
  }

  fun toMekanikVehicle(record: SinoChineseVinRecord): Vehicle {
    val isHybridOrEv = record.model.contains("DM-i", ignoreCase = true) || record.model.contains("EV", ignoreCase = true)
    return Vehicle(
      id = "veh-${record.id}",
      vin = record.vin,
      brand = record.brand,
      model = record.model,
      generation = "${record.generationCode} (${record.chineseDomesticName})",
      yearRange = "2022 - 2026",
      exactYear = record.year,
      engineCode = record.engineCode,
      displacement = record.displacementAndPower.substringBefore(" •"),
      powerCh = 197,
      powerKw = 145,
      fuelType = if (isHybridOrEv) "Hybride Rechargeable (DM-i)" else "Essence Turbo GDI",
      gearbox = record.transmissionCode,
      platform = record.generationCode,
      assemblyPlant = record.assemblyPlant,
      isEv = isHybridOrEv,
      generationNotes = record.car2DbLlmSummary
    )
  }

  fun toMekanikPartItems(record: SinoChineseVinRecord): List<PartItem> {
    return record.oemPartsCatalog.mapIndexed { idx, item ->
      PartItem(
        id = item.partId,
        system = if (item.systemCategory.contains("Frein", ignoreCase = true)) SystemCategory.FREINAGE else SystemCategory.MOTEUR,
        name = item.partName,
        position = item.fitmentPosition,
        oemReference = item.oemPartNumber,
        oemAlternatives = item.crossReferences,
        crossReferences = item.crossReferences.map { ref ->
          val parts = ref.split(" ", limit = 2)
          CrossReference(
            brand = parts.firstOrNull() ?: "OEM",
            reference = parts.getOrNull(1) ?: ref,
            qualityGrade = "Équivalence Certifiée (${item.sourceProvider.displayName})"
          )
        },
        schemaCalloutNumber = idx + 1,
        yearCompatibility = "2021 - 2026 (${record.generationCode})",
        compatibilityBadge = "100% Compatible VIN ${record.vin.take(3)}",
        priceEstimatedDzd = item.estimatedPriceDzd,
        description = "${item.technicalDimensions} • ${item.torqueSpecsNm}",
        technicalSpecs = mapOf(
          "Source API" to item.sourceProvider.displayName,
          "Dimensions" to item.technicalDimensions,
          "Couples de serrage" to item.torqueSpecsNm,
          "Disponibilité" to item.stockAvailability
        )
      )
    }
  }

  // ============================================================================
  // LES 6 OPTIONS INDISPENSABLES « MEKANIK+ INDUSTRIAL PRO SUITE »
  // ============================================================================

  // 1. Pont « Photo Plaque Châssis / Carte Grise (OCR Hors-Ligne) → Cascade 11 API »
  val ocrChassisCascadePresets: List<OcrChassisCascadePreset> = listOf(
    OcrChassisCascadePreset(
      id = "ocr-whatsapp-jetour",
      sourceTypeBadge = "PHOTO WHATSAPP PARE-BRISE",
      title = "Photo Pare-Brise Floue (Jetour Dashing 1.6T)",
      rawNoisyOcrText = "CHASSIS NO: * L V V D B 2 I B 9 P D O 4 8 9 I 2 * (Reflet vitre)",
      autoCleanedIsoVin = "LVVDB21B9PD048912",
      ocrCorrectionsApplied = listOf(
        "Suppression espaces & astérisques parasites (*)",
        "Correction ISO-3779 : 'I' → '1' (pos 7 & 16)",
        "Correction ISO-3779 : 'O' → '0' (pos 12)"
      ),
      confidencePercent = 99,
      extractionTimeMs = 18,
      detectedBrandModelPreview = "Jetour Dashing 1.6 TGDI 197 ch (Usine Chery Wuhu)",
      wilayaPlateDetected = "09412-124-16 (Alger)"
    ),
    OcrChassisCascadePreset(
      id = "ocr-carte-grise-geely",
      sourceTypeBadge = "CARTE GRISE ALGÉRIENNE (NIV)",
      title = "Carte Grise Algérienne — Geely Coolray (SX11)",
      rawNoisyOcrText = "N.I.V : L6T7824S7ND0I9384 - IMMAT: 18492-123-31",
      autoCleanedIsoVin = "L6T7824S7ND019384",
      ocrCorrectionsApplied = listOf(
        "Extraction champ N.I.V officiel Carte Grise DZ",
        "Correction ISO-3779 : 'I' → '1' (pos 13)",
        "Validation clé de contrôle (Check-Digit pos 9 = '7')"
      ),
      confidencePercent = 99,
      extractionTimeMs = 15,
      detectedBrandModelPreview = "Geely Coolray (Binyue) 1.5TD 177 ch (Plateforme BMA)",
      wilayaPlateDetected = "18492-123-31 (Oran)"
    ),
    OcrChassisCascadePreset(
      id = "ocr-plaque-rivetee-chery",
      sourceTypeBadge = "PLAQUE RIVETÉE MONTANT B",
      title = "Plaque Constructeur Métal — Chery Tiggo 8 Pro",
      rawNoisyOcrText = "CHERY AUTOMOBILE CO. [LVTDB21B7ND08842O] 145kW",
      autoCleanedIsoVin = "LVTDB21B7ND088420",
      ocrCorrectionsApplied = listOf(
        "Filtrage texte constructeur & puissance (145kW)",
        "Correction ISO-3779 : 'O' → '0' (pos 17)"
      ),
      confidencePercent = 98,
      extractionTimeMs = 21,
      detectedBrandModelPreview = "Chery Tiggo 8 Pro Max 1.6 TGDI (Kunpeng Power)",
      wilayaPlateDetected = "04519-124-19 (Sétif)"
    ),
    OcrChassisCascadePreset(
      id = "ocr-etiquette-peugeot",
      sourceTypeBadge = "CARTE GRISE / ÉTIQUETTE DAM",
      title = "Étiquette Porte & Carte Grise — Peugeot 308 III",
      rawNoisyOcrText = "VF3 FPH NSSM YOOI234 / DAM 16482",
      autoCleanedIsoVin = "VF3FPHNSSMY001234",
      ocrCorrectionsApplied = listOf(
        "Fusion segments VIN & extraction N° APV/PR (DAM 16482)",
        "Correction ISO-3779 : 'O','O','I' → '0','0','1' (pos 12-14)"
      ),
      confidencePercent = 100,
      extractionTimeMs = 14,
      detectedBrandModelPreview = "Peugeot 308 III 1.5 BlueHDi 130 (DV5RC)",
      wilayaPlateDetected = "29104-122-16 (Alger)"
    )
  )

  fun cleanNoisyOcrVinString(rawInput: String): Pair<String, List<String>> {
    val corrections = mutableListOf<String>()
    val upper = rawInput.uppercase()
    // Extract candidate alphanumeric token or clean full string
    val stripped = upper
      .replace("CHASSIS NO:", "")
      .replace("N.I.V :", "")
      .replace("NIV:", "")
      .replace(Regex("[^A-Z0-9]"), "")
    val sb = StringBuilder()
    for ((idx, ch) in stripped.take(17).withIndex()) {
      when (ch) {
        'I' -> {
          sb.append('1')
          corrections.add("Pos ${idx + 1}: 'I' corrigé en '1' (Norme ISO-3779)")
        }
        'O', 'Q' -> {
          sb.append('0')
          corrections.add("Pos ${idx + 1}: '$ch' corrigé en '0' (Norme ISO-3779)")
        }
        else -> sb.append(ch)
      }
    }
    if (corrections.isEmpty()) {
      corrections.add("Nettoyage caractères spéciaux & validation Check-Digit ISO-3779")
    }
    val finalVin = sb.toString().ifBlank { "LVVDB21B9PD048912" }
    return finalVin to corrections
  }

  // 2. Pont « Équivalence Croisée Chinoise ↔ Européenne (OEM → TecDoc IAM) »
  val sinoEuroCrossRefBridges: List<SinoEuroCrossReferenceBridge> = listOf(
    SinoEuroCrossReferenceBridge(
      id = "xref-jetour-dashing-pads",
      chineseVehicleLabel = "Jetour Dashing 1.6 TGDI (SQRF4J16)",
      vinExample = "LVVDB21B9PD048912",
      partCategory = "Freinage Avant",
      partName = "Jeu de 4 Plaquettes de Frein Avant Céramique",
      chineseOemPartNumber = "J60-3501080 / T15-3501080BA",
      chineseOemAvailabilityAlert = "Pièce d'origine maison mère parfois sous 15j → Équivalents Européens En Stock Immédiat",
      technicalDimensionsSpec = "155.2 x 61.5 x 17.5 mm • Étrier Continental FNc",
      europeanPlatformSibling = "Architecture Étrier Continental / ATE partagée (Gamme SUV Europe & Chery T1X)",
      iamEquivalents = listOf(
        CrossRefIamSupplierMatch(
          iamBrand = "Brembo (Gamme Prime)",
          iamPartNumber = "P 85 126",
          tecDocKType = "KType #144892",
          compatibilityCertaintyPercent = 100,
          sharedPlatformOrigin = "Même empreinte d'étrier Continental 155.2mm",
          localVendorNameWilaya = "SARL AutoParts El-Harrach (Alger - 16)",
          priceDzd = 9800,
          oemDealerPriceDzd = 15200,
          savingsPercent = 35,
          stockStatus = "EN STOCK (14 jeux)"
        ),
        CrossRefIamSupplierMatch(
          iamBrand = "Bosch Automotive",
          iamPartNumber = "0 986 494 660 (BP1652)",
          tecDocKType = "KType #144892",
          compatibilityCertaintyPercent = 100,
          sharedPlatformOrigin = "Homologation ECE-R90 • Zéro modification",
          localVendorNameWilaya = "Comptoir Pièces Oran Est (Oran - 31)",
          priceDzd = 8900,
          oemDealerPriceDzd = 15200,
          savingsPercent = 41,
          stockStatus = "EN STOCK (22 jeux)"
        ),
        CrossRefIamSupplierMatch(
          iamBrand = "TRW Lucas (ZF Aftermarket)",
          iamPartNumber = "GDB1984",
          tecDocKType = "KType #144892",
          compatibilityCertaintyPercent = 99,
          sharedPlatformOrigin = "Monte équipementier étriers flottants",
          localVendorNameWilaya = "Maghreb Freinage Sétif (Sétif - 19)",
          priceDzd = 9200,
          oemDealerPriceDzd = 15200,
          savingsPercent = 39,
          stockStatus = "EN STOCK (9 jeux)"
        )
      )
    ),
    SinoEuroCrossReferenceBridge(
      id = "xref-geely-coolray-volvo-cma",
      chineseVehicleLabel = "Geely Coolray (SX11) 1.5TD (JLH-3G15TD)",
      vinExample = "L6T7824S7ND019384",
      partCategory = "Freinage & Filtration (Moteur Co-Développé Volvo)",
      partName = "Plaquettes de Frein Avant & Filtre à Huile Moteur 1.5TD",
      chineseOemPartNumber = "4048001500 (Frein) / 1056006100 (Filtre)",
      chineseOemAvailabilityAlert = "100% Identique à la plateforme Volvo XC40 T3 (B3154T) en stock chez tous les grossistes Europe",
      technicalDimensionsSpec = "133.0 x 50.6 x 16.0 mm • Moteur JLH-3G15TD = Volvo B3154T",
      europeanPlatformSibling = "Volvo XC40 1.5 T3 / Lynk & Co 01 (Plateforme Commune Geely-Volvo CMA/BMA)",
      iamEquivalents = listOf(
        CrossRefIamSupplierMatch(
          iamBrand = "TRW (Monte Volvo XC40)",
          iamPartNumber = "GDB2155",
          tecDocKType = "KType #132910 (Volvo XC40)",
          compatibilityCertaintyPercent = 100,
          sharedPlatformOrigin = "Correspondance directe OEM Volvo 31445796",
          localVendorNameWilaya = "EuroParts Bab Ezzouar (Alger - 16)",
          priceDzd = 9400,
          oemDealerPriceDzd = 14500,
          savingsPercent = 35,
          stockStatus = "EN STOCK (18 jeux)"
        ),
        CrossRefIamSupplierMatch(
          iamBrand = "Brembo",
          iamPartNumber = "P 24 215",
          tecDocKType = "KType #132910",
          compatibilityCertaintyPercent = 100,
          sharedPlatformOrigin = "Dimensions exactes 133x50.6x16mm",
          localVendorNameWilaya = "Auto Pièces Constantine (25)",
          priceDzd = 10200,
          oemDealerPriceDzd = 14500,
          savingsPercent = 30,
          stockStatus = "EN STOCK (11 jeux)"
        ),
        CrossRefIamSupplierMatch(
          iamBrand = "Mann-Filter (Filtre Huile Volvo/Geely)",
          iamPartNumber = "HU 7029 z",
          tecDocKType = "KType #132910",
          compatibilityCertaintyPercent = 100,
          sharedPlatformOrigin = "Cartouche filtrante bloc 1.5TD Geely/Volvo",
          localVendorNameWilaya = "SARL AutoParts El-Harrach (Alger - 16)",
          priceDzd = 2400,
          oemDealerPriceDzd = 4200,
          savingsPercent = 43,
          stockStatus = "EN STOCK (45 unités)"
        )
      )
    ),
    SinoEuroCrossReferenceBridge(
      id = "xref-chery-tiggo8-ignition-filter",
      chineseVehicleLabel = "Chery Tiggo 8 Pro 1.6 TGDI (Acteco SQRF4J16)",
      vinExample = "LVTDB21B7ND088420",
      partCategory = "Allumage GDI & Filtration Moteur",
      partName = "Bougies Iridium GDI & Filtre à Huile Vissé",
      chineseOemPartNumber = "F4J16-3707010 (Bougies) / F4J16-1012010 (Filtre)",
      chineseOemAvailabilityAlert = "Gestion moteur Bosch MED17.8.10 → Composants Bosch / NGK / Mann immédiatement disponibles",
      technicalDimensionsSpec = "Filetage M12x1.25 • Portée 26.5 mm • Clé 14 mm (Iridium Laser)",
      europeanPlatformSibling = "Injection Directe Haute Pression 200 bars Bosch MED17 & Filetage Filtre Standard M20x1.5",
      iamEquivalents = listOf(
        CrossRefIamSupplierMatch(
          iamBrand = "NGK Laser Iridium",
          iamPartNumber = "SILZKR8H8G (97516)",
          tecDocKType = "KType #141055",
          compatibilityCertaintyPercent = 100,
          sharedPlatformOrigin = "Monte d'origine usine Chery Acteco GDI",
          localVendorNameWilaya = "Pro-Injection Blida (09)",
          priceDzd = 11500,
          oemDealerPriceDzd = 17800,
          savingsPercent = 35,
          stockStatus = "EN STOCK (12 coffrets x4)"
        ),
        CrossRefIamSupplierMatch(
          iamBrand = "Mann-Filter",
          iamPartNumber = "W 610/3",
          tecDocKType = "KType #141055",
          compatibilityCertaintyPercent = 100,
          sharedPlatformOrigin = "Clapet anti-retour 1.0 bar certifié SQRF4J16",
          localVendorNameWilaya = "SARL AutoParts El-Harrach (Alger - 16)",
          priceDzd = 1850,
          oemDealerPriceDzd = 3400,
          savingsPercent = 45,
          stockStatus = "EN STOCK (60 unités)"
        ),
        CrossRefIamSupplierMatch(
          iamBrand = "Bosch Double Iridium",
          iamPartNumber = "0 242 140 538 (YR6NII332)",
          tecDocKType = "KType #141055",
          compatibilityCertaintyPercent = 99,
          sharedPlatformOrigin = "Calibré pour calculateur Bosch MED17.8.10",
          localVendorNameWilaya = "Comptoir Pièces Oran Est (Oran - 31)",
          priceDzd = 9900,
          oemDealerPriceDzd = 17800,
          savingsPercent = 44,
          stockStatus = "EN STOCK (16 coffrets x4)"
        )
      )
    )
  )

  // 3. Synchronisation Intelligente du Cache Local (« Auto-Apprentissage Room SQLite »)
  private val _autoLearnedCacheEntries = mutableListOf(
    AutoLearnedVinCacheEntry(
      vin = "LVVDB21B9PD048912",
      vehicleLabel = "Jetour Dashing 1.6 TGDI 197 ch (2024)",
      engineAndTrans = "SQRF4J16 • 7DCT300 Humide",
      initialSourceProvider = UnifiedApiConnectorProvider.SINO_AUTO_360,
      initialNetworkLatencyMs = 42,
      cachedOfflineLatencyMs = 0,
      learnedTimestamp = "Auto-appris aujourd'hui (Cache Room #11)",
      oemPartsCountIndexed = 18,
      crossRefIamCountIndexed = 9,
      fluidsAndTorqueIndexed = true,
      localRoomSizeKb = 48,
      hitCountInWorkshop = 14
    ),
    AutoLearnedVinCacheEntry(
      vin = "L6T7824S7ND019384",
      vehicleLabel = "Geely Coolray (SX11) 1.5TD 177 ch (2024)",
      engineAndTrans = "JLH-3G15TD (Volvo Co-Dev) • 7DCT300",
      initialSourceProvider = UnifiedApiConnectorProvider.DONGCHEDI_AUTO_API,
      initialNetworkLatencyMs = 36,
      cachedOfflineLatencyMs = 0,
      learnedTimestamp = "Auto-appris hier (Cache Room #11)",
      oemPartsCountIndexed = 22,
      crossRefIamCountIndexed = 12,
      fluidsAndTorqueIndexed = true,
      localRoomSizeKb = 54,
      hitCountInWorkshop = 19
    ),
    AutoLearnedVinCacheEntry(
      vin = "VF3FPHNSSMY001234",
      vehicleLabel = "Peugeot 308 III 1.5 BlueHDi 130 (2023)",
      engineAndTrans = "DV5RC • EAT8 Aisin",
      initialSourceProvider = UnifiedApiConnectorProvider.OEM_SERVICEBOX_DIALOGYS_ETKA,
      initialNetworkLatencyMs = 34,
      cachedOfflineLatencyMs = 0,
      learnedTimestamp = "Auto-appris Atelier (Cache Room #11)",
      oemPartsCountIndexed = 31,
      crossRefIamCountIndexed = 16,
      fluidsAndTorqueIndexed = true,
      localRoomSizeKb = 62,
      hitCountInWorkshop = 27
    )
  )

  val autoLearnedCacheEntries: List<AutoLearnedVinCacheEntry>
    get() = _autoLearnedCacheEntries.toList()

  fun autoLearnActiveTraceToRoomCache(trace: UnifiedSingleApiExecutionTrace): List<AutoLearnedVinCacheEntry> {
    val rec = trace.matchedVinRecord
    val existingIdx = _autoLearnedCacheEntries.indexOfFirst { it.vin.equals(rec.vin, ignoreCase = true) }
    val updatedEntry = AutoLearnedVinCacheEntry(
      vin = rec.vin,
      vehicleLabel = "${rec.brand} ${rec.model} (${rec.year})",
      engineAndTrans = "${rec.engineCode} • ${rec.transmissionCode}",
      initialSourceProvider = trace.fallbackCascade.winningProvider,
      initialNetworkLatencyMs = trace.totalMergedLatencyMs.coerceAtLeast(14),
      cachedOfflineLatencyMs = 0,
      learnedTimestamp = "Synchronisé à l'instant dans Room SQLite (0 ms hors-ligne)",
      oemPartsCountIndexed = rec.oemPartsCatalog.size + 14,
      crossRefIamCountIndexed = rec.oemPartsCatalog.sumOf { it.crossReferences.size } + 6,
      fluidsAndTorqueIndexed = true,
      localRoomSizeKb = 52,
      hitCountInWorkshop = if (existingIdx >= 0) _autoLearnedCacheEntries[existingIdx].hitCountInWorkshop + 1 else 1
    )
    if (existingIdx >= 0) {
      _autoLearnedCacheEntries[existingIdx] = updatedEntry
    } else {
      _autoLearnedCacheEntries.add(0, updatedEntry)
    }
    return _autoLearnedCacheEntries.toList()
  }

  // 4. Génération Automatique du « Devis / Ordre de Réparation (OR) en 1 Clic » (DZD)
  fun buildOneClickQuoteForTrace(
    trace: UnifiedSingleApiExecutionTrace,
    tierIndex: Int = 1 // 0 = T1 (2500 DA/h), 1 = T2 (3500 DA/h), 2 = T3 (4500 DA/h)
  ): OneClickAutoQuoteBundle {
    val rec = trace.matchedVinRecord
    val (tierLabel, hourlyRate) = when (tierIndex) {
      0 -> "Barème T1 • Entretien & Freinage Courant (2 500 DA/h)" to 2500
      2 -> "Barème T3 • Haute Technicité DCT / GDI / Hybride (4 500 DA/h)" to 4500
      else -> "Barème T2 • Mécanique & Diagnostic OBD-II / EPB (3 500 DA/h)" to 3500
    }
    val partsLines = rec.oemPartsCatalog.map { part ->
      OneClickAutoQuoteLineItem(
        refCode = part.oemPartNumber,
        description = "${part.partName} (${part.fitmentPosition})",
        sourceApiBadge = "${part.sourceProvider.displayName} • Équiv: ${part.crossReferences.firstOrNull() ?: "OEM"}",
        quantity = 1,
        unitPriceDzd = part.estimatedPriceDzd,
        isLaborLine = false
      )
    }
    return OneClickAutoQuoteBundle(
      id = "OR-AUTO-${rec.vin.takeLast(6)}",
      orderReferenceNumber = "OR-2026-${rec.vin.takeLast(4)}",
      vehicleTitle = "${rec.brand} ${rec.model} (${rec.engineCode})",
      vin = rec.vin,
      clientName = "Client Atelier Pro (${rec.brand} Algérie)",
      clientPhone = "+213 550 84 19 20",
      wilaya = "16 - Alger (Bab Ezzouar / El-Harrach)",
      tariffTierLabel = tierLabel,
      hourlyRateDzd = hourlyRate,
      flatRateHours = 1.5f,
      partsLines = partsLines,
      consumablesDzd = 1200, // Nettoyant frein + graisse céramique colonnettes
      tvaPercent = 19,
      warrantyNotice = "Garantie Pièces & Main d'Œuvre : 6 Mois ou 10 000 km • Couples serrés à la clé dynamométrique (${rec.oemPartsCatalog.firstOrNull()?.torqueSpecsNm ?: "35 Nm / 110 Nm"})",
      generatedFromCascadeSource = "#${trace.fallbackCascade.winningStepOrder} ${trace.fallbackCascade.winningProvider.displayName}"
    )
  }

  // 5. Module « Fluides, Lubrifiants & Procédures de Reset Service » (Spécial Boîtes DCT Chinoises & Europe)
  val fluidAndServiceResetSpecs: List<FluidAndServiceResetSpec> = listOf(
    FluidAndServiceResetSpec(
      id = "fluid-jetour-dashing-16tgdi",
      vehicleTitle = "Jetour Dashing / X70 Plus 1.6 TGDI (SQRF4J16)",
      vinSample = "LVVDB21B9PD048912",
      engineCode = "SQRF4J16 (Acteco 1.6 TGDI 197 ch)",
      transmissionCode = "Getrag 7DCT300 (Double Embrayage Humide 7 rapports)",
      engineOilViscosity = "5W-30 ou 0W-20 Synthèse Totale (Low SAPS)",
      engineOilNormOem = "API SP / ILSAC GF-6A (Anti-LSPI obligatoire pour Turbo GDI)",
      engineOilCapacityLiters = "4.3 L (avec remplacement cartouche filtre F4J16-1012010) • Couple bouchon : 35 Nm",
      dctGearboxFluidType = "Huile DCT Humide Spécifique 7DCT300 (Sinopec DCTF-GS / Shell Spirax S5 DCT 11 / Motul Multi DCTF)",
      dctGearboxCapacityLiters = "4.0 L (Vidange maintenance) / 4.6 L (Boîte sèche) • Ne JAMAIS mettre d'ATF classique !",
      dctChangeIntervalKm = "Tous les 60 000 km (Recommandé 45 000 km en usage urbain dense / fortes chaleurs Algérie)",
      coolantSpecAndCapacity = "Antigel Organique OAT LEC-II Rose (-35°C) • Capacité : 7.5 L",
      brakeFluidSpec = "DOT 4 LV (Basse Viscosité ESP Bosch 9.3) • 0.85 L • Purge tous les 2 ans",
      manualClusterResetSteps = listOf(
        "1. Mettre le contact en position ON (Moteur éteint, pied hors de la pédale de frein).",
        "2. Sur l'écran central ou via le bouton droit du volant, aller dans « Réglages Véhicule » → « Maintenance / Entretien ».",
        "3. Maintenir appuyé le bouton « OK / Reset Kilométrage Entretien » pendant 5 secondes jusqu'au bip de confirmation (Remise à 10 000 km)."
      ),
      obdUdsResetRoutine = "UDS Service $2E F1 98 (Write Data By Identifier : Reset Oil Counter) • EPB Mode Maintenance : $31 01 F0 01"
    ),
    FluidAndServiceResetSpec(
      id = "fluid-geely-coolray-15td",
      vehicleTitle = "Geely Coolray (SX11) 1.5TD (Co-Développé Volvo)",
      vinSample = "L6T7824S7ND019384",
      engineCode = "JLH-3G15TD / BHE15-EFZ (177 / 181 ch)",
      transmissionCode = "7DCT300 (Embrayage Humide Co-Développé Volvo-Geely)",
      engineOilViscosity = "0W-20 Synthèse Totale Exclusivement",
      engineOilNormOem = "Norme Volvo VCC RBS0-2AE / ACEA C5 / API SP (Attention : la 5W-40 dégrade le déphaseur VVT et le turbo !)",
      engineOilCapacityLiters = "5.6 L sur le 3-cylindres 1.5TD JLH-3G15TD (carter grande capacité) / 4.2 L sur le 4-cylindres BHE15",
      dctGearboxFluidType = "Huile DCT Humide 7DCT300 (Shell Spirax S5 DCT 11 / Castrol Transmax Dual / Norme Volvo BOT 351 C4)",
      dctGearboxCapacityLiters = "4.2 L (Vidange atelier avec mise à niveau à 40°C via OBD)",
      dctChangeIntervalKm = "Tous les 60 000 km ou 3 ans (Calibration points de léchage embrayage K1/K2 recommandée après vidange)",
      coolantSpecAndCapacity = "Liquide de refroidissement Volvo/Geely VCS Jaune ou Rose OAT • 6.8 L",
      brakeFluidSpec = "DOT 4 Class 6 (ISO 4925) • 0.80 L",
      manualClusterResetSteps = listOf(
        "1. Fermer toutes les portes et mettre le contact sans démarrer (Appui court sur START sans toucher au frein).",
        "2. Appuyer simultanément à fond sur la pédale d'accélérateur et la pédale de frein pendant 10 secondes.",
        "3. Relâcher lorsque le message « Entretien Réinitialisé / Service Reset Complete » s'affiche au combiné numérique."
      ),
      obdUdsResetRoutine = "UDS ICM/CEM Module : Routine $31 01 D0 01 (Reset SRI) • Calibration Embrayage 7DCT : $31 01 02 05"
    ),
    FluidAndServiceResetSpec(
      id = "fluid-byd-song-plus-dmi",
      vehicleTitle = "BYD Song Plus DM-i (Hybride Rechargeable EHS)",
      vinSample = "LGXC76C44P0019283",
      engineCode = "BYD472QA (1.5L Xiaoyun Ciclo Atkinson) + Moteur EHS",
      transmissionCode = "E-CVT EHS (Electric Hybrid System dédié)",
      engineOilViscosity = "0W-20 Synthèse Spéciale Hybride DM-i",
      engineOilNormOem = "API SP / ILSAC GF-6A (Protection démarrages fréquents thermique/électrique)",
      engineOilCapacityLiters = "3.8 L (avec filtre BYD472QA-1012010)",
      dctGearboxFluidType = "Fluide Transmission EHS Spécifique BYD (BYD EHSF-1 Ultra-Basse Viscosité Diélectrique)",
      dctGearboxCapacityLiters = "3.5 L (Compartiment EHS refroidi avec stator électrique)",
      dctChangeIntervalKm = "Première vidange EHS à 40 000 km puis tous les 48 000 km",
      coolantSpecAndCapacity = "Double circuit : Circuit Moteur OAT (5.8 L) + Circuit Batterie Blade / Inverter Basse Conductivité (4.2 L)",
      brakeFluidSpec = "DOT 4 LV Régénératif (Système IPB Bosch Integrated Power Brake) • 0.90 L",
      manualClusterResetSteps = listOf(
        "1. Sur la tablette rotative DiLink, ouvrir « Véhicule » → « État du Véhicule / Santé & Maintenance ».",
        "2. Sélectionner « Compteur Entretien HEV (Thermique) » ou « Entretien EV ».",
        "3. Appuyer sur « Réinitialiser après vidange » (Code atelier par défaut : 0000 ou confirmation directe)."
      ),
      obdUdsResetRoutine = "DiLink Gateway UDS : $31 01 FF 00 (Reset HEV Oil Life & EHS Fluid Counter)"
    )
  )

  // 6. Pont Direct avec le « Passeport QR Universel du Véhicule »
  fun buildQrPassportSyncRecord(
    trace: UnifiedSingleApiExecutionTrace,
    fluidSpec: FluidAndServiceResetSpec,
    quoteBundle: OneClickAutoQuoteBundle
  ): QrPassportCascadeSyncRecord {
    val rec = trace.matchedVinRecord
    return QrPassportCascadeSyncRecord(
      passportQrId = "QR-MK1-${rec.vin.takeLast(6)}",
      vin = rec.vin,
      vehicleTitle = "${rec.brand} ${rec.model} (${rec.year})",
      licensePlateDz = "09412-124-16",
      winningApiProviderName = "#${trace.fallbackCascade.winningStepOrder} ${trace.fallbackCascade.winningProvider.displayName}",
      syncedOemPartsSummary = rec.oemPartsCatalog.map { "${it.partName} [OEM: ${it.oemPartNumber}]" },
      certifiedEngineOilGrade = "${fluidSpec.engineOilViscosity} (${fluidSpec.engineOilNormOem})",
      nextDctGearboxServiceKm = fluidSpec.dctChangeIntervalKm,
      lastSyncedWorkshopOrderRef = "${quoteBundle.orderReferenceNumber} (${quoteBundle.totalTtcDzd} DZD TTC)",
      lastSyncedTimestamp = "Synchronisé à l'instant • Prêt au scan (Atelier / Vendeur / Client)"
    )
  }
}

