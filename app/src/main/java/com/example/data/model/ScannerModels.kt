package com.example.data.model

enum class ScanTargetType(val label: String, val description: String) {
  CARTE_GRISE("Carte Grise / Permis", "Numéro de châssis VIN & Immatriculation"),
  VIN_BARCODE("Code-barres / Pare-brise", "Numéro VIN 17 caractères"),
  NUM_IMMATRICULATION("Plaque d'Immatriculation", "Format Algérie (ex: 00123-116-16)"),
  PIECE_OEM("Référence Gravée Pièce", "Gravure constructeur ou code EAN"),
  ECRAN_VALISE_OBD("Écran Valise de Diagnostic", "Reconnaissance OCR de codes défauts OBD-II & Freeze Frame")
}

data class ScanDetectionResult(
  val detectedText: String,
  val targetType: ScanTargetType,
  val confidencePercent: Int,
  val extractedVin: String? = null,
  val extractedPlate: String? = null,
  val extractedOem: String? = null,
  val extractedObdCode: String? = null,
  val vehiclePreview: String? = null
)

enum class DiagnosticToolTheme {
  AUTEL_MAXISYS,
  VCDS_VAGCOM,
  LAUNCH_X431,
  BOSCH_KTS,
  DELPHI_DS150,
  DASHBOARD_CLUSTER
}

data class ObdPhysicalScreenPreset(
  val id: String,
  val toolBrand: String,
  val toolSoftware: String,
  val screenSubtitle: String,
  val primaryDtc: String,
  val dtcStandardTitle: String,
  val dtcFullDescription: String,
  val ecuModule: String,
  val statusBadge: String,
  val freezeFrameData: Map<String, String>,
  val secondaryDtcs: List<String> = emptyList(),
  val toolTheme: DiagnosticToolTheme = DiagnosticToolTheme.AUTEL_MAXISYS,
  val rawOcrText: String = ""
)

object ObdScreenPresetsProvider {
  val presets: List<ObdPhysicalScreenPreset> = listOf(
    ObdPhysicalScreenPreset(
      id = "autel_p0299",
      toolBrand = "Autel MaxiSys MS908S Pro",
      toolSoftware = "VAG Diagnostics v8.42 - AutoScan",
      screenSubtitle = "01 - Engine Control Module (EDC17C74) • Fault Memory",
      primaryDtc = "P0299",
      dtcStandardTitle = "Turbocharger/Supercharger 'A' Underboost Condition",
      dtcFullDescription = "16683/P0299/000665 - Pression de suralimentation non atteinte (Sous-pression limite inférieure dépassée).",
      ecuModule = "Calculateur Moteur 01 (EDC17 / 2.0 TDI CR)",
      statusBadge = "CONFIRMÉ / VOYANT MIL ALLUMÉ",
      freezeFrameData = mapOf(
        "Régime Moteur" to "2 480 tr/min",
        "Pression de Suralimentation Mesurée" to "1 420 mbar",
        "Pression Consigne Calculateur" to "2 280 mbar",
        "Écart de Régulation Pression" to "-860 mbar",
        "Température Liquide Refroidissement" to "88 °C",
        "Position Actuateur Wastegate" to "92.4 %",
        "Tension Capteur MAP" to "2.14 V"
      ),
      secondaryDtcs = listOf("P0234 (Intermittent)", "P00AF (Mécanisme actuateur bloqué)"),
      toolTheme = DiagnosticToolTheme.AUTEL_MAXISYS,
      rawOcrText = "AUTEL MaxiSys | 01-Engine DTC: P0299 - Turbo Underboost Confirmed | RPM:2480 Boost:1420mbar Req:2280mbar"
    ),
    ObdPhysicalScreenPreset(
      id = "vcds_p0401",
      toolBrand = "VCDS / VAG-COM HEX-V2",
      toolSoftware = "Ross-Tech VCDS Release 24.5.0 - Groupe VAG",
      screenSubtitle = "VAG Controller 01: Engine • Fault Codes (02 Errors Found)",
      primaryDtc = "P0401",
      dtcStandardTitle = "Exhaust Gas Recirculation (EGR) Flow Insufficient Detected",
      dtcFullDescription = "001025/P0401 - Système de recyclage des gaz d'échappement (Vanne EGR) : Débit trop faible / colmatage suie.",
      ecuModule = "01-Engine - 2.0 TDI EA288 (DFGA)",
      statusBadge = "ACTIF / ENREGISTRÉ DANS L'ECU",
      freezeFrameData = mapOf(
        "Régime Moteur" to "1 850 tr/min",
        "Débit d'Air Mesuré (MAF)" to "520 mg/course",
        "Débit d'Air Consigne EGR Ouverte" to "280 mg/course",
        "Recyclage Gaz Consigne" to "45.0 %",
        "Position Réelle Vanne EGR" to "12.2 %",
        "Température Gaz Échappement" to "245 °C"
      ),
      secondaryDtcs = listOf("P046C (Capteur position EGR signal non plausible)"),
      toolTheme = DiagnosticToolTheme.VCDS_VAGCOM,
      rawOcrText = "Ross-Tech VCDS: 1 Fault Found: 001025 - EGR System: Insufficient Flow P0401 - 000 - MIL ON Freeze Frame: RPM: 1850 MAF: 520mg/str"
    ),
    ObdPhysicalScreenPreset(
      id = "launch_p0300",
      toolBrand = "Launch X-431 PRO3S+ V2.0",
      toolSoftware = "SmartBox 3.0 • Multi-Brand Diagnostic Platform",
      screenSubtitle = "Renault/PSA/VW Injection Directe Essence • Lecture Codes Défauts",
      primaryDtc = "P0300",
      dtcStandardTitle = "Random/Multiple Cylinder Misfire Detected",
      dtcFullDescription = "P030000 - Ratés d'allumage aléatoires / multiples détectés sur plusieurs cylindres (Cylindres 2 et 3).",
      ecuModule = "Calculateur Injection / Allumage Bosch MED17",
      statusBadge = "PERMANENT / RISQUE CATALYSEUR",
      freezeFrameData = mapOf(
        "Régime Ralenti" to "820 tr/min",
        "Ratés Cylindre 1" to "0",
        "Ratés Cylindre 2" to "47 ratés / min",
        "Ratés Cylindre 3" to "89 ratés / min",
        "Pression Rail Carburant" to "320 bar",
        "Tension Sonde Lambda Amont" to "0.12 V (Mélange Pauvre)"
      ),
      secondaryDtcs = listOf("P0302 (Ratés Cylindre 2)", "P0303 (Ratés Cylindre 3)"),
      toolTheme = DiagnosticToolTheme.LAUNCH_X431,
      rawOcrText = "LAUNCH X431 Pro | System: ECM / Fuel Injection | DTC P0300 Random Misfire | Cyl 2: 47 Cyl 3: 89 misfires/min"
    ),
    ObdPhysicalScreenPreset(
      id = "bosch_p2002",
      toolBrand = "Bosch KTS 590 / ESI[tronic] 2.0",
      toolSoftware = "Bosch Automotive Workshop System v2026/2",
      screenSubtitle = "Système Post-Traitement des Gaz d'Échappement • FAP / DPF",
      primaryDtc = "P2002",
      dtcStandardTitle = "Particulate Trap Efficiency Below Threshold Bank 1",
      dtcFullDescription = "P200200 - Filtre à Particules Diesel (FAP) : Efficacité en dessous du seuil critique / Colmatage en suie.",
      ecuModule = "Calculateur Moteur & DPF Diesel EDC17",
      statusBadge = "MIL ALLUMÉ / DÉGRADÉ MOTEUR",
      freezeFrameData = mapOf(
        "Masse de Suie Calculée" to "48.6 g (Seuil régénération: 24.0g)",
        "Pression Différentielle FAP" to "88 mbar au ralenti",
        "Dernière Régénération Réussie" to "Il y a 680 km",
        "Température Amont FAP" to "195 °C",
        "Vitesse Véhicule" to "0 km/h"
      ),
      secondaryDtcs = listOf("P2452 (Circuit capteur pression différentielle FAP)"),
      toolTheme = DiagnosticToolTheme.BOSCH_KTS,
      rawOcrText = "BOSCH KTS 590 | ESI[tronic] | DTC P2002 DPF Efficiency Below Threshold | Soot: 48.6g DiffPress: 88mbar"
    ),
    ObdPhysicalScreenPreset(
      id = "delphi_p0171",
      toolBrand = "Delphi DS150E Pro (CAR MAX)",
      toolSoftware = "Delphi Electronic Service Tool HD",
      screenSubtitle = "Calculateur Moteur Essence • Régulation Richesse",
      primaryDtc = "P0171",
      dtcStandardTitle = "System Too Lean (Bank 1) - Mélange Trop Pauvre",
      dtcFullDescription = "P0171 - Excès d'air ou manque de carburant banc 1. Correction de carburant à la limite positive maximale (+25%).",
      ecuModule = "Gestion Moteur Essence Multipoint",
      statusBadge = "CONFIRMÉ / CODE DTC ACTIF",
      freezeFrameData = mapOf(
        "STFT (Correction Court Terme)" to "+24.2 %",
        "LTFT (Correction Long Terme)" to "+18.8 %",
        "Débitmètre d'Air (MAF)" to "1.8 g/s (Attendu: 2.5 g/s)",
        "Dépression Collecteur (MAP)" to "34 kPa",
        "Tension Sonde O2 Banc 1 Capteur 1" to "0.08 V"
      ),
      secondaryDtcs = listOf("P0101 (Débitmètre plage/rendement non plausible)"),
      toolTheme = DiagnosticToolTheme.DELPHI_DS150,
      rawOcrText = "DELPHI DS150E | DTC: P0171 System Too Lean Bank 1 | STFT: +24.2% LTFT: +18.8% MAF: 1.8g/s"
    ),
    ObdPhysicalScreenPreset(
      id = "cluster_check_engine",
      toolBrand = "Combiné d'Instruments / Écran LCD Bord",
      toolSoftware = "Tableau de Bord Numérique • Afficheur Central Maxi-DOT",
      screenSubtitle = "Message Alerte Combiné : Anomalie Gestion Moteur",
      primaryDtc = "P0101",
      dtcStandardTitle = "Mass Air Flow (MAF) Sensor Circuit Range/Performance",
      dtcFullDescription = "P0101 - Signal débitmètre massique d'air hors plage de cohérence / encrassement capteur film chaud.",
      ecuModule = "Calculateur Moteur & Admission d'Air",
      statusBadge = "MESSAGE VOYANT MOTEUR",
      freezeFrameData = mapOf(
        "Message Combiné" to "Défaut Moteur : Faites contrôler le véhicule !",
        "Témoin MIL" to "Allumé Fixe Jaune",
        "Tension Capteur Débitmètre" to "0.65 V au lieu de 1.10 V",
        "Vitesse" to "0 km/h",
        "Kilométrage" to "88 400 km"
      ),
      secondaryDtcs = emptyList(),
      toolTheme = DiagnosticToolTheme.DASHBOARD_CLUSTER,
      rawOcrText = "DASHBOARD CLUSTER LCD | CHECK ENGINE | FAULT P0101 MAF Sensor Signal Out Of Range"
    )
  )
}

data class CarteGriseScanPreset(
  val id: String,
  val title: String,
  val countryDoc: String, // e.g. "Carte Grise Algérie (DZ)"
  val vin: String,
  val plateNumber: String,
  val brandModel: String,
  val engineCode: String,
  val displacement: String,
  val firstRegistration: String,
  val sectionELabel: String = "(E) Numéro dans la série du type (VIN)",
  val rawDocumentText: String
)

object CarteGrisePresetsProvider {
  val presets: List<CarteGriseScanPreset> = listOf(
    CarteGriseScanPreset(
      id = "cg_vw_tiguan",
      title = "Volkswagen Tiguan II 2.0 TDI",
      countryDoc = "Carte Grise République Algérienne (Wilaya 16 - Alger)",
      vin = "WVWZZZ5NZMW123456",
      plateNumber = "08745-121-16",
      brandModel = "VOLKSWAGEN TIGUAN 2.0 TDI (DFGA)",
      engineCode = "DFGA (150ch)",
      displacement = "1968 cc",
      firstRegistration = "14/03/2021",
      rawDocumentText = "RÉPUBLIQUE ALGÉRIENNE DÉMOCRATIQUE ET POPULAIRE\nMINISTÈRE DE L'INTÉRIEUR • CARTE GRISE AUTOMOBILE\n(A) 08745-121-16 | (B) 14/03/2021\n(D.1) VOLKSWAGEN (D.3) TIGUAN II ELEGANCE\n(E) [VIN] : WVWZZZ5NZMW123456\n(P.1) 1968 CC (P.3) GASOIL (P.5) DFGA\n(F.1) 2140 KG"
    ),
    CarteGriseScanPreset(
      id = "cg_peugeot_308",
      title = "Peugeot 308 III 1.5 BlueHDi",
      countryDoc = "Carte Grise République Algérienne (Wilaya 31 - Oran)",
      vin = "VF3FPHNSSMY001234",
      plateNumber = "04291-120-31",
      brandModel = "PEUGEOT 308 III ALLURE PACK",
      engineCode = "DV5RC (130ch)",
      displacement = "1499 cc",
      firstRegistration = "08/11/2022",
      rawDocumentText = "RÉPUBLIQUE ALGÉRIENNE DÉMOCRATIQUE ET POPULAIRE\nWILAYA D'ORAN • CERTIFICAT D'IMMATRICULATION\n(A) 04291-120-31 | (B) 08/11/2022\n(D.1) PEUGEOT (D.3) 308 III\n(E) [NUMÉRO DE CHÂSSIS] : VF3FPHNSSMY001234\n(P.1) 1499 CC (P.3) GO (P.5) DV5RC"
    ),
    CarteGriseScanPreset(
      id = "cg_renault_clio5",
      title = "Renault Clio V 1.5 dCi",
      countryDoc = "Carte Grise République Algérienne (Wilaya 06 - Béjaïa)",
      vin = "VF1RJA00067890123",
      plateNumber = "01129-119-06",
      brandModel = "RENAULT CLIO V INTENS",
      engineCode = "K9K 608 (115ch)",
      displacement = "1461 cc",
      firstRegistration = "19/06/2020",
      rawDocumentText = "CERTIFICAT D'IMMATRICULATION AUTOMOBILE DZ\nWILAYA DE BÉJAÏA\n(A) 01129-119-06 | (B) 19/06/2020\n(D.1) RENAULT (D.3) CLIO V\n(E) NUMÉRO D'IDENTIFICATION DU VÉHICULE : VF1RJA00067890123\n(P.1) 1461 CM3 (P.5) K9K 608"
    ),
    CarteGriseScanPreset(
      id = "cg_toyota_hilux",
      title = "Toyota Hilux 2.8 D-4D 4x4",
      countryDoc = "Carte Grise République Algérienne (Wilaya 30 - Ouargla)",
      vin = "MR0FA3CD000445566",
      plateNumber = "00341-122-30",
      brandModel = "TOYOTA HILUX INVINCIBLE 4WD",
      engineCode = "1GD-FTV (204ch)",
      displacement = "2755 cc",
      firstRegistration = "25/01/2022",
      rawDocumentText = "RÉPUBLIQUE ALGÉRIENNE • WILAYA D'OUARGLA\n(A) 00341-122-30 | (B) 25/01/2022\n(D.1) TOYOTA (D.3) HILUX 4X4 PICKUP\n(E) CHÂSSIS / VIN : MR0FA3CD000445566\n(P.1) 2755 CC (P.5) 1GD-FTV"
    ),
    CarteGriseScanPreset(
      id = "cg_hyundai_tucson",
      title = "Hyundai Tucson 1.6 CRDi HTRAC",
      countryDoc = "Carte Grise République Algérienne (Wilaya 25 - Constantine)",
      vin = "KMHN3812LNU112233",
      plateNumber = "06712-121-25",
      brandModel = "HYUNDAI TUCSON NX4 N-LINE",
      engineCode = "Smartstream D1.6",
      displacement = "1598 cc",
      firstRegistration = "12/09/2021",
      rawDocumentText = "CERTIFICAT IMMATRICULATION DZ • WILAYA CONSTANTINE\n(A) 06712-121-25\n(D.1) HYUNDAI (D.3) TUCSON NX4\n(E) [IDENTIFIANT SÉRIE VIN] : KMHN3812LNU112233\n(P.1) 1598 CC (P.3) DIESEL"
    ),
    CarteGriseScanPreset(
      id = "cg_mercedes_cclass",
      title = "Mercedes-Benz C 220 d (W206)",
      countryDoc = "Carte Grise Européenne / Algérie Douane",
      vin = "W1K2060041F098765",
      plateNumber = "09981-123-16",
      brandModel = "MERCEDES-BENZ CLASSE C 220 D",
      engineCode = "OM654M Mild-Hybrid",
      displacement = "1993 cc",
      firstRegistration = "05/04/2023",
      rawDocumentText = "RÉPUBLIQUE ALGÉRIENNE / UNION EUROPÉENNE\n(A) 09981-123-16\n(D.1) MERCEDES-BENZ (D.3) C 220 D\n(E) VIN : W1K2060041F098765\n(P.1) 1993 CM3 (P.5) OM 654 M"
    )
  )
}
