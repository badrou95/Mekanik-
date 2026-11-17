package com.example.data.model

enum class VoiceRepairLanguage(
  val code: String,
  val localeCode: String,
  val labelFr: String,
  val nativeLabel: String,
  val flagEmoji: String,
  val sampleSpeech: String,
  val sampleClientNameVoice: String = "Karim Benali"
) {
  FRANCAIS(
    code = "FR",
    localeCode = "fr-FR",
    labelFr = "Français",
    nativeLabel = "Français",
    flagEmoji = "🇫🇷",
    sampleSpeech = "Bruit de claquement métallique côté distribution à froid, voyant moteur et voyant pression pneus allumés au tableau de bord, et vibration au freinage avant."
  ),
  ARABE(
    code = "AR",
    localeCode = "ar-DZ",
    labelFr = "Arabe",
    nativeLabel = "العربية",
    flagEmoji = "🇩🇿",
    sampleSpeech = "عندي صوت طقطقة في المحرك الصباح كي تكون باردة، وضوء الموتور شاعل في الطابلو والفرانات القدام يصفروا مع اهتزاز في المقود."
  ),
  ANGLAIS(
    code = "EN",
    localeCode = "en-US",
    labelFr = "Anglais",
    nativeLabel = "English",
    flagEmoji = "🇬🇧",
    sampleSpeech = "Metallic knocking noise from the timing belt on cold start, check engine light and TPMS warning light are on the dashboard, and front brakes vibrate at high speed."
  ),
  ESPAGNOL(
    code = "ES",
    localeCode = "es-ES",
    labelFr = "Espagnol",
    nativeLabel = "Español",
    flagEmoji = "🇪🇸",
    sampleSpeech = "Ruido metálico en la correa de distribución en frío, testigo de motor y presión de neumáticos encendidos en el cuadro, y vibración al frenar."
  ),
  CHINOIS(
    code = "ZH",
    localeCode = "zh-CN",
    labelFr = "Chinois",
    nativeLabel = "中文",
    flagEmoji = "🇨🇳",
    sampleSpeech = "冷车启动时正时皮带区域有金属异响，仪表盘发动机故障灯和胎压灯亮起，高速刹车时前轮抖动。"
  );

  val localeTag: String get() = localeCode
  val label: String get() = labelFr
  val shortLabel: String get() = "$flagEmoji $code"
  val sampleSpokenSymptom: String get() = sampleSpeech
}

data class AiTranslatedRepairProblem(
  val sourceLanguage: VoiceRepairLanguage,
  val originalSpeechText: String = "",
  val translatedFrenchTechnicalSummary: String = "",
  val detectedSymptoms: List<String> = emptyList(),
  val suggestedRepairOperations: List<String> = emptyList(),
  val estimatedUrgency: String = "Prioritaire Atelier",
  val suggestedParts: List<RepairItem> = emptyList(),
  val aiEngineLabel: String = "MekanikAI Multilingual Translator (FR • AR • EN • ES • ZH)"
)

enum class WarningLightSeverity(val label: String, val colorHex: Long, val emoji: String) {
  CRITIQUE_ROUGE("Arrêt / Critique (Rouge)", 0xFFE53935, "🔴"),
  ALERTE_ORANGE("Alerte / Diagnostic (Orange)", 0xFFFF7A00, "🟠"),
  SERVICE_INFO("Entretien / Info (Jaune/Bleu)", 0xFF00E5FF, "🔵")
}

data class DashboardWarningLightItem(
  val id: String,
  val code: String,
  val name: String,
  val systemDescription: String,
  val severity: WarningLightSeverity,
  val isIlluminated: Boolean = false
) {
  val isLitOnDashboard: Boolean get() = isIlluminated
  val symbolEmoji: String get() = severity.emoji
  val severityColorHex: Long get() = severity.colorHex
  val obdCategoryHint: String get() = code
}

enum class TireConditionStatus(val label: String, val shortLabel: String, val emoji: String, val colorHex: Long) {
  BON_ETAT("Bon état (>= 4 mm)", "Bon", "🟢", 0xFF00E676),
  USURE_MOYENNE("Usure 50% (2.5 - 4 mm)", "Moyen", "🟠", 0xFFFF7A00),
  A_REMPLACER("Usé / Témoin (< 1.6 mm)", "À remplacer", "🔴", 0xFFE53935),
  CREVAISON_HERNIE("Crevaison / Hernie", "Hernie/Crevé", "🚨", 0xFFD50000)
}

data class TireInspectionItem(
  val id: String,
  val positionLabel: String, // AVG, AVD, ARG, ARD, Secours
  val tireDimension: String = "205/55 R16 91V",
  val treadDepthMm: String = "6.0",
  val pressureBar: String = "2.3",
  val status: TireConditionStatus = TireConditionStatus.BON_ETAT
) {
  val dimensionSpec: String get() = tireDimension
}

enum class StandardCheckResult(val label: String, val emoji: String, val colorHex: Long) {
  OK("OK / Conforme", "✅", 0xFF00E676),
  A_SURVEILLER("À surveiller", "⚠️", 0xFFFF7A00),
  A_REMPLACER("À remplacer / Urgent", "🚨", 0xFFE53935)
}

data class StandardMechanicalCheckItem(
  val id: String,
  val category: String,
  val label: String,
  val detail: String,
  val result: StandardCheckResult = StandardCheckResult.OK,
  val status: OrConditionCheckStatus = when (result) {
    StandardCheckResult.OK -> OrConditionCheckStatus.CONFORME
    StandardCheckResult.A_SURVEILLER -> OrConditionCheckStatus.SALE_OU_RESERVE
    StandardCheckResult.A_REMPLACER -> OrConditionCheckStatus.ANOMALIE_A_SIGNALER
  }
) {
  val title: String get() = label
  val measurementOrNote: String get() = detail
}

data class ReceptionPhotoCaptureState(
  val platePhotoTaken: Boolean = false,
  val extractedPlateNumber: String = "01948-122-16",
  val vinPhotoTaken: Boolean = false,
  val extractedVin: String = "VF3CCHMZ6GW049102",
  val fuelAndMileagePhotoTaken: Boolean = false,
  val extractedMileageKm: String = "114 820",
  val extractedFuelLevelQuarters: Int = 3,
  val carteGrisePhotoTaken: Boolean = false,
  val extractedClientName: String = "Karim Benali",
  val clientNameInputMethod: String = "MANUEL",
  val dashboardRunningPhotoTaken: Boolean = false
) {
  val capturedPhotosCount: Int
    get() = listOf(
      platePhotoTaken,
      vinPhotoTaken,
      fuelAndMileagePhotoTaken,
      carteGrisePhotoTaken,
      dashboardRunningPhotoTaken
    ).count { it }
}

enum class BulbZoneCategory(val label: String, val shortLabel: String, val emoji: String) {
  AVANT("Éclairage Avant (Phares, Codes, Veilleuses, Antibrouillards)", "Avant", "🚘"),
  ARRIERE("Éclairage Arrière (Stops, Lanternes, Recul, Plaque)", "Arrière", "🚨"),
  LATERAL_SIGNALISATION("Signalisation Latérale & Rétroviseurs", "Latéral", "↔️"),
  HABITACLE_COFFRE("Éclairage Habitacle, Plafonnier & Coffre", "Habitacle", "💡")
}

enum class BulbCheckStatus(
  val shortLabel: String,
  val fullLabel: String,
  val badgeEmoji: String,
  val colorHex: Long
) {
  OK_FONCTIONNEL(
    shortLabel = "OK",
    fullLabel = "Fonctionnelle (RAS)",
    badgeEmoji = "🟢",
    colorHex = 0xFF00E676
  ),
  GRILLEE_A_SIGNALER(
    shortLabel = "Grillée (Prévenir)",
    fullLabel = "Ampoule grillée — Client à prévenir",
    badgeEmoji = "🟠",
    colorHex = 0xFFFF7A00
  ),
  REMPLACEE_SUR_OR(
    shortLabel = "Remplacée (+OR)",
    fullLabel = "Ampoule grillée & remplacée sur l'OR",
    badgeEmoji = "🔴",
    colorHex = 0xFFE53935
  ),
  FAUX_CONTACT(
    shortLabel = "Faible / Contact",
    fullLabel = "Intensité faible ou faux contact douille",
    badgeEmoji = "🟡",
    colorHex = 0xFFFFD600
  )
}

data class VehicleBulbCheckItem(
  val id: String,
  val positionName: String,
  val zone: BulbZoneCategory,
  val bulbSocketType: String,
  val oemPartRef: String,
  val replacementUnitPriceDzd: Int,
  val status: BulbCheckStatus = BulbCheckStatus.OK_FONCTIONNEL
) {
  fun toRepairItem(): RepairItem {
    return RepairItem(
      name = "Ampoule $positionName ($bulbSocketType)",
      reference = oemPartRef,
      quantity = 1,
      priceDzd = replacementUnitPriceDzd
    )
  }
}

enum class VehicleCleanlinessLevel(
  val label: String,
  val shortLabel: String,
  val description: String,
  val emoji: String,
  val colorHex: Long
) {
  TRES_PROPRE(
    label = "Très Propre",
    shortLabel = "✨ Très Propre",
    description = "Lavage récent, inspection visuelle 100% dégagée",
    emoji = "✨",
    colorHex = 0xFF00E676
  ),
  PROPRE(
    label = "Propre",
    shortLabel = "🟢 Propre",
    description = "État normal et propre à la réception",
    emoji = "🟢",
    colorHex = 0xFF00E5FF
  ),
  SALE(
    label = "Sale / Poussiéreux",
    shortLabel = "🟠 Sale",
    description = "Présence de poussière/saleté (réserves sur micro-rayures)",
    emoji = "🟠",
    colorHex = 0xFFFF7A00
  ),
  TRES_SALE(
    label = "Très Sale / Boueux",
    shortLabel = "🔴 Très Sale",
    description = "Saleté importante / boue — contrôle carrosserie sous réserve",
    emoji = "🔴",
    colorHex = 0xFFE53935
  )
}

enum class OrConditionCheckStatus(
  val label: String,
  val shortLabel: String,
  val emoji: String,
  val colorHex: Long
) {
  CONFORME(
    label = "Conforme / Propre",
    shortLabel = "Conforme",
    emoji = "✅",
    colorHex = 0xFF00E676
  ),
  SALE_OU_RESERVE(
    label = "Sale / Réserve notée",
    shortLabel = "Sale / Réserve",
    emoji = "⚠️",
    colorHex = 0xFFFF7A00
  ),
  ANOMALIE_A_SIGNALER(
    label = "Anomalie / Endommagé",
    shortLabel = "Anomalie",
    emoji = "🚨",
    colorHex = 0xFFE53935
  )
}

data class OrVehicleConditionCheckItem(
  val id: String,
  val section: String,
  val label: String,
  val detailNote: String,
  val status: OrConditionCheckStatus = OrConditionCheckStatus.CONFORME
)

data class RepairOrderInspectionChecklist(
  val exteriorCleanliness: VehicleCleanlinessLevel = VehicleCleanlinessLevel.PROPRE,
  val interiorCleanliness: VehicleCleanlinessLevel = VehicleCleanlinessLevel.PROPRE,
  val protectiveCoversInstalled: Boolean = true,
  val fuelLevelQuarters: Int = 3, // 1 = 1/4, 2 = 1/2, 3 = 3/4, 4 = 4/4 Plein
  val valuablesDeclaredInCar: String = "Aucun objet de valeur déclaré",
  val exteriorInteriorNotes: String = "Housse siège, tapis sol papier et couvre-volant installés à la réception.",
  val bulbs: List<VehicleBulbCheckItem> = defaultVehicleBulbs(),
  val conditionChecks: List<OrVehicleConditionCheckItem> = defaultConditionChecks(),
  val tires: List<TireInspectionItem> = defaultTiresInspection(),
  val standardMechanicalChecks: List<StandardMechanicalCheckItem> = defaultStandardMechanicalChecks(),
  val dashboardWarningLights: List<DashboardWarningLightItem> = defaultDashboardWarningLights(),
  val receptionPhotos: ReceptionPhotoCaptureState = ReceptionPhotoCaptureState(),
  val voiceLanguage: VoiceRepairLanguage = VoiceRepairLanguage.FRANCAIS,
  val voiceRawText: String = "",
  val aiTranslatedProblemSummary: String = "",
  val aiSuggestedOperations: List<String> = emptyList(),
  val isElectronicallySigned: Boolean = false,
  val electronicSignatureSignerName: String = "",
  val electronicSignatureTimestamp: String = "",
  val electronicSignatureStrokeCount: Int = 0,
  val clientNotifiedForBulbs: Boolean = false,
  val lastClientAlertSummary: String = ""
) {
  val burntOrReplacedBulbs: List<VehicleBulbCheckItem>
    get() = bulbs.filter {
      it.status == BulbCheckStatus.GRILLEE_A_SIGNALER ||
        it.status == BulbCheckStatus.REMPLACEE_SUR_OR ||
        it.status == BulbCheckStatus.FAUX_CONTACT
    }

  val replacedBulbs: List<VehicleBulbCheckItem>
    get() = bulbs.filter { it.status == BulbCheckStatus.REMPLACEE_SUR_OR }

  val burntNotReplacedBulbs: List<VehicleBulbCheckItem>
    get() = bulbs.filter {
      it.status == BulbCheckStatus.GRILLEE_A_SIGNALER ||
        it.status == BulbCheckStatus.FAUX_CONTACT
    }

  val okBulbsCount: Int
    get() = bulbs.count { it.status == BulbCheckStatus.OK_FONCTIONNEL }

  val replacedBulbsTotalDzd: Int
    get() = replacedBulbs.sumOf { it.replacementUnitPriceDzd }

  val litWarningLights: List<DashboardWarningLightItem>
    get() = dashboardWarningLights.filter { it.isLitOnDashboard }

  val illuminatedWarningLights: List<DashboardWarningLightItem>
    get() = litWarningLights

  val tiresToWatchOrReplace: List<TireInspectionItem>
    get() = tires.filter { it.status != TireConditionStatus.BON_ETAT }

  val tiresToReplaceCount: Int
    get() = tiresToWatchOrReplace.size

  val fuelLevelFormatted: String
    get() = when (fuelLevelQuarters) {
      1 -> "1/4 (25% - Réserve proche)"
      2 -> "1/2 (50% - Moitié)"
      3 -> "3/4 (75%)"
      else -> "4/4 (100% - Plein)"
    }

  fun buildClientBulbAlertMessage(
    clientName: String,
    vehicleModel: String,
    orderNumber: String
  ): String {
    val defective = burntOrReplacedBulbs
    val litLights = litWarningLights
    val lightsInfo = if (litLights.isNotEmpty()) {
      "\n🚨 Voyants allumés constatés au tableau de bord : " + litLights.joinToString(", ") { it.name }
    } else ""

    if (defective.isEmpty()) {
      return "Bonjour $clientName, suite au contrôle complet des ${bulbs.size} ampoules sur votre $vehicleModel (OR $orderNumber), toutes vos ampoules sont 100% fonctionnelles (RAS)." + lightsInfo
    }
    val replacedText = if (replacedBulbs.isNotEmpty()) {
      "\n🔴 Ampoules grillées remplacées sur l'OR : " +
        replacedBulbs.joinToString(", ") { "${it.positionName} (${it.bulbSocketType} - ${it.replacementUnitPriceDzd} DA)" }
    } else ""

    val toWarnText = if (burntNotReplacedBulbs.isNotEmpty()) {
      "\n🟠 Ampoules grillées / faibles constatées (en attente de votre accord) : " +
        burntNotReplacedBulbs.joinToString(", ") { "${it.positionName} (${it.bulbSocketType} - ${it.replacementUnitPriceDzd} DA)" }
    } else ""

    return "Bonjour $clientName, lors de la checklist de réception (OR $orderNumber) sur votre $vehicleModel, notre atelier a contrôlé les ${bulbs.size} ampoules du véhicule :" +
      replacedText +
      toWarnText +
      lightsInfo +
      "\nÉtat véhicule : Extérieur ${exteriorCleanliness.label} • Intérieur ${interiorCleanliness.label}."
  }

  fun buildChecklistSummaryLine(): String {
    val replacedCount = replacedBulbs.size
    val warnCount = burntNotReplacedBulbs.size
    val lightsCount = litWarningLights.size
    val tiresAlerts = tiresToWatchOrReplace.size
    return "Ext: ${exteriorCleanliness.label} • Int: ${interiorCleanliness.label} • Carburant: ${fuelLevelQuarters}/4 • Ampoules: ${okBulbsCount}/${bulbs.size} OK" +
      (if (replacedCount > 0) " ($replacedCount rempl.)" else "") +
      (if (warnCount > 0) " ($warnCount grillée(s))" else "") +
      (if (lightsCount > 0) " • $lightsCount voyant(s) TDB" else "") +
      (if (tiresAlerts > 0) " • $tiresAlerts pneu(s) à voir" else "")
  }

  companion object {
    fun defaultSample(): RepairOrderInspectionChecklist = RepairOrderInspectionChecklist()

    fun defaultDashboardWarningLights(): List<DashboardWarningLightItem> = listOf(
      DashboardWarningLightItem(
        id = "warn_engine_mil",
        code = "MIL / OBD",
        name = "Voyant Moteur (Check Engine / Antipollution)",
        systemDescription = "Défaut calculateur injection, allumage ou catalyseur/sonde lambda",
        severity = WarningLightSeverity.ALERTE_ORANGE,
        isIlluminated = true
      ),
      DashboardWarningLightItem(
        id = "warn_tpms",
        code = "TPMS",
        name = "Voyant Pression des Pneus (TPMS)",
        systemDescription = "Sous-gonflage détecté ou capteur valve à réinitialiser",
        severity = WarningLightSeverity.ALERTE_ORANGE,
        isIlluminated = true
      ),
      DashboardWarningLightItem(
        id = "warn_oil",
        code = "OIL",
        name = "Voyant Pression d'Huile Moteur",
        systemDescription = "Pression d'huile insuffisante ou niveau critique",
        severity = WarningLightSeverity.CRITIQUE_ROUGE,
        isIlluminated = false
      ),
      DashboardWarningLightItem(
        id = "warn_battery",
        code = "BAT 12V",
        name = "Voyant Batterie / Circuit de Charge Alternateur",
        systemDescription = "Défaut alternateur, régulateur ou courroie accessoire",
        severity = WarningLightSeverity.CRITIQUE_ROUGE,
        isIlluminated = false
      ),
      DashboardWarningLightItem(
        id = "warn_abs_esp",
        code = "ABS / ESP",
        name = "Voyant ABS & Contrôle Dynamique (ESP)",
        systemDescription = "Capteur vitesse de roue ou bloc hydraulique ABS",
        severity = WarningLightSeverity.ALERTE_ORANGE,
        isIlluminated = false
      ),
      DashboardWarningLightItem(
        id = "warn_dpf_fap",
        code = "FAP / DPF",
        name = "Voyant Filtre à Particules (FAP) / AdBlue",
        systemDescription = "Colmatage FAP ou système SCR AdBlue à régénérer",
        severity = WarningLightSeverity.ALERTE_ORANGE,
        isIlluminated = false
      ),
      DashboardWarningLightItem(
        id = "warn_brake_wear",
        code = "FREINS",
        name = "Voyant Usure Plaquettes / Niveau Liquide Frein",
        systemDescription = "Témoin d'usure plaquettes avant ou niveau DOT4 bas",
        severity = WarningLightSeverity.CRITIQUE_ROUGE,
        isIlluminated = false
      ),
      DashboardWarningLightItem(
        id = "warn_airbag_srs",
        code = "SRS",
        name = "Voyant Airbag & Prétensionneurs de Ceinture",
        systemDescription = "Connecteur sous siège, contacteur tournant ou calculateur SRS",
        severity = WarningLightSeverity.CRITIQUE_ROUGE,
        isIlluminated = false
      ),
      DashboardWarningLightItem(
        id = "warn_coolant_temp",
        code = "TEMP °C",
        name = "Voyant Température Liquide de Refroidissement",
        systemDescription = "Surchauffe moteur, thermostat, pompe à eau ou motoventilateur",
        severity = WarningLightSeverity.CRITIQUE_ROUGE,
        isIlluminated = false
      ),
      DashboardWarningLightItem(
        id = "warn_glow_plug",
        code = "PRÉCHAUFF.",
        name = "Voyant Préchauffage / Gestion Moteur Diesel",
        systemDescription = "Bougies de préchauffage ou régulation haute pression gazole",
        severity = WarningLightSeverity.ALERTE_ORANGE,
        isIlluminated = false
      ),
      DashboardWarningLightItem(
        id = "warn_steering_eps",
        code = "EPS",
        name = "Voyant Direction Assistée Électrique / Hydraulique",
        systemDescription = "Capteur d'angle de braquage ou moteur d'assistance",
        severity = WarningLightSeverity.ALERTE_ORANGE,
        isIlluminated = false
      ),
      DashboardWarningLightItem(
        id = "warn_service_wrench",
        code = "SERVICE 🔧",
        name = "Voyant Clé d'Entretien / Échéance Révision",
        systemDescription = "Indicateur de maintenance périodique dépassé",
        severity = WarningLightSeverity.SERVICE_INFO,
        isIlluminated = true
      )
    )

    fun defaultTiresInspection(): List<TireInspectionItem> = listOf(
      TireInspectionItem(
        id = "tire_avg",
        positionLabel = "Pneu Avant Gauche (AVG)",
        tireDimension = "205/55 R16 91V",
        treadDepthMm = "3.2 mm",
        pressureBar = "2.1 Bar (Sous-gonflé)",
        status = TireConditionStatus.USURE_MOYENNE
      ),
      TireInspectionItem(
        id = "tire_avd",
        positionLabel = "Pneu Avant Droit (AVD)",
        tireDimension = "205/55 R16 91V",
        treadDepthMm = "3.4 mm",
        pressureBar = "2.3 Bar",
        status = TireConditionStatus.USURE_MOYENNE
      ),
      TireInspectionItem(
        id = "tire_arg",
        positionLabel = "Pneu Arrière Gauche (ARG)",
        tireDimension = "205/55 R16 91V",
        treadDepthMm = "6.2 mm",
        pressureBar = "2.2 Bar",
        status = TireConditionStatus.BON_ETAT
      ),
      TireInspectionItem(
        id = "tire_ard",
        positionLabel = "Pneu Arrière Droit (ARD)",
        tireDimension = "205/55 R16 91V",
        treadDepthMm = "6.1 mm",
        pressureBar = "2.2 Bar",
        status = TireConditionStatus.BON_ETAT
      ),
      TireInspectionItem(
        id = "tire_secours",
        positionLabel = "Roue de Secours + Cric & Clé Antivol",
        tireDimension = "Galette / 205/55 R16",
        treadDepthMm = "7.5 mm (Neuf)",
        pressureBar = "2.5 Bar",
        status = TireConditionStatus.BON_ETAT
      )
    )

    fun defaultStandardMechanicalChecks(): List<StandardMechanicalCheckItem> = listOf(
      StandardMechanicalCheckItem(
        id = "std_freins_av_ar",
        category = "FREINAGE",
        label = "Plaquettes & Disques de frein Avant / Arrière",
        detail = "Contrôle épaisseur garnitures et voile disques",
        result = StandardCheckResult.A_REMPLACER
      ),
      StandardMechanicalCheckItem(
        id = "std_niveau_huile",
        category = "NIVEAUX MOTEUR",
        label = "Niveau & Qualité Huile Moteur",
        detail = "Vérification jauge manuelle et absence de fuite carter",
        result = StandardCheckResult.OK
      ),
      StandardMechanicalCheckItem(
        id = "std_liquide_refroidissement",
        category = "NIVEAUX MOTEUR",
        label = "Liquide de Refroidissement (Vase d'expansion & Ph)",
        detail = "Niveau entre MINI/MAXI et protection antigel/anticorrosion",
        result = StandardCheckResult.OK
      ),
      StandardMechanicalCheckItem(
        id = "std_liquide_frein",
        category = "FREINAGE",
        label = "Liquide de Frein & Embrayage (DOT4 / Hygroscopie)",
        detail = "Taux d'humidité < 2% et niveau bocal maître-cylindre",
        result = StandardCheckResult.OK
      ),
      StandardMechanicalCheckItem(
        id = "std_batterie_12v",
        category = "ÉLECTRICITÉ",
        label = "Batterie 12V (Tension repos 12.6V / Charge 14.2V)",
        detail = "Test état de santé SOH et serrage des cosses",
        result = StandardCheckResult.OK
      ),
      StandardMechanicalCheckItem(
        id = "std_courroies",
        category = "MOTEUR",
        label = "Courroie d'Accessoires & Kit Distribution",
        detail = "Contrôle craquelures, galet tendeur et bruit à froid",
        result = StandardCheckResult.A_SURVEILLER
      ),
      StandardMechanicalCheckItem(
        id = "std_suspension_direction",
        category = "TRAIN ROULANT",
        label = "Amortisseurs, Rotules, Biellettes & Soufflets Cardan",
        detail = "Absence de jeu ou fuite hydraulique sur pont",
        result = StandardCheckResult.OK
      ),
      StandardMechanicalCheckItem(
        id = "std_essuie_glaces_lave_glace",
        category = "VISIBILITÉ",
        label = "Balais d'Essuie-Glaces Av/Ar & Niveau Lave-Glace",
        detail = "État raclettes caoutchouc et fonctionnement pompe",
        result = StandardCheckResult.OK
      ),
      StandardMechanicalCheckItem(
        id = "std_clim_habitacle",
        category = "CLIMATISATION",
        label = "Climatisation (Froid) & Filtre Habitacle Pollen",
        detail = "Température soufflage aérateurs et débit d'air",
        result = StandardCheckResult.OK
      ),
      StandardMechanicalCheckItem(
        id = "std_echappement",
        category = "ÉCHAPPEMENT",
        label = "Ligne d'Échappement, Silencieux & Fixations",
        detail = "Contrôle étanchéité et silentblocs de maintien",
        result = StandardCheckResult.OK
      )
    )

    fun defaultVehicleBulbs(): List<VehicleBulbCheckItem> = listOf(
      // --- ÉCLAIRAGE AVANT (11 ampoules) ---
      VehicleBulbCheckItem(
        id = "bulb_code_avg",
        positionName = "Feu de croisement Avant Gauche (Code AVG)",
        zone = BulbZoneCategory.AVANT,
        bulbSocketType = "H7 12V 55W / LED",
        oemPartRef = "AMP-OSR-H7-AVG",
        replacementUnitPriceDzd = 1200,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),
      VehicleBulbCheckItem(
        id = "bulb_code_avd",
        positionName = "Feu de croisement Avant Droit (Code AVD)",
        zone = BulbZoneCategory.AVANT,
        bulbSocketType = "H7 12V 55W / LED",
        oemPartRef = "AMP-OSR-H7-AVD",
        replacementUnitPriceDzd = 1200,
        status = BulbCheckStatus.REMPLACEE_SUR_OR
      ),
      VehicleBulbCheckItem(
        id = "bulb_phare_avg",
        positionName = "Feu de route Avant Gauche (Phare AVG)",
        zone = BulbZoneCategory.AVANT,
        bulbSocketType = "H1 / HB3 12V 55W",
        oemPartRef = "AMP-OSR-H1-AVG",
        replacementUnitPriceDzd = 1100,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),
      VehicleBulbCheckItem(
        id = "bulb_phare_avd",
        positionName = "Feu de route Avant Droit (Phare AVD)",
        zone = BulbZoneCategory.AVANT,
        bulbSocketType = "H1 / HB3 12V 55W",
        oemPartRef = "AMP-OSR-H1-AVD",
        replacementUnitPriceDzd = 1100,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),
      VehicleBulbCheckItem(
        id = "bulb_veilleuse_avg",
        positionName = "Veilleuse / Feu de position Avant Gauche (AVG)",
        zone = BulbZoneCategory.AVANT,
        bulbSocketType = "W5W T10 12V 5W",
        oemPartRef = "AMP-PHI-W5W-AVG",
        replacementUnitPriceDzd = 350,
        status = BulbCheckStatus.GRILLEE_A_SIGNALER
      ),
      VehicleBulbCheckItem(
        id = "bulb_veilleuse_avd",
        positionName = "Veilleuse / Feu de position Avant Droit (AVD)",
        zone = BulbZoneCategory.AVANT,
        bulbSocketType = "W5W T10 12V 5W",
        oemPartRef = "AMP-PHI-W5W-AVD",
        replacementUnitPriceDzd = 350,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),
      VehicleBulbCheckItem(
        id = "bulb_drl_av",
        positionName = "Feux de jour (DRL) Avant Gauche & Droit",
        zone = BulbZoneCategory.AVANT,
        bulbSocketType = "PW24W / P21W / Rampe LED",
        oemPartRef = "AMP-DRL-AV-24W",
        replacementUnitPriceDzd = 1500,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),
      VehicleBulbCheckItem(
        id = "bulb_antibrouillard_avg",
        positionName = "Antibrouillard Avant Gauche (AVG)",
        zone = BulbZoneCategory.AVANT,
        bulbSocketType = "H11 / H8 12V 55W",
        oemPartRef = "AMP-OSR-H11-AVG",
        replacementUnitPriceDzd = 1600,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),
      VehicleBulbCheckItem(
        id = "bulb_antibrouillard_avd",
        positionName = "Antibrouillard Avant Droit (AVD)",
        zone = BulbZoneCategory.AVANT,
        bulbSocketType = "H11 / H8 12V 55W",
        oemPartRef = "AMP-OSR-H11-AVD",
        replacementUnitPriceDzd = 1600,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),
      VehicleBulbCheckItem(
        id = "bulb_cligno_avg",
        positionName = "Clignotant Avant Gauche (AVG)",
        zone = BulbZoneCategory.AVANT,
        bulbSocketType = "PY21W BAU15s Ambre",
        oemPartRef = "AMP-PY21W-AVG",
        replacementUnitPriceDzd = 450,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),
      VehicleBulbCheckItem(
        id = "bulb_cligno_avd",
        positionName = "Clignotant Avant Droit (AVD)",
        zone = BulbZoneCategory.AVANT,
        bulbSocketType = "PY21W BAU15s Ambre",
        oemPartRef = "AMP-PY21W-AVD",
        replacementUnitPriceDzd = 450,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),

      // --- SIGNALISATION LATÉRALE (2 ampoules) ---
      VehicleBulbCheckItem(
        id = "bulb_repetiteur_lat_g",
        positionName = "Répétiteur Latéral / Rétroviseur Gauche",
        zone = BulbZoneCategory.LATERAL_SIGNALISATION,
        bulbSocketType = "WY5W T10 Ambre / LED",
        oemPartRef = "AMP-WY5W-LATG",
        replacementUnitPriceDzd = 450,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),
      VehicleBulbCheckItem(
        id = "bulb_repetiteur_lat_d",
        positionName = "Répétiteur Latéral / Rétroviseur Droit",
        zone = BulbZoneCategory.LATERAL_SIGNALISATION,
        bulbSocketType = "WY5W T10 Ambre / LED",
        oemPartRef = "AMP-WY5W-LATD",
        replacementUnitPriceDzd = 450,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),

      // --- ÉCLAIRAGE ARRIÈRE (10 ampoules) ---
      VehicleBulbCheckItem(
        id = "bulb_stop_arg",
        positionName = "Feu Stop Arrière Gauche (ARG)",
        zone = BulbZoneCategory.ARRIERE,
        bulbSocketType = "P21W / P21/5W BAY15d",
        oemPartRef = "AMP-P215W-ARG",
        replacementUnitPriceDzd = 450,
        status = BulbCheckStatus.GRILLEE_A_SIGNALER
      ),
      VehicleBulbCheckItem(
        id = "bulb_stop_ard",
        positionName = "Feu Stop Arrière Droit (ARD)",
        zone = BulbZoneCategory.ARRIERE,
        bulbSocketType = "P21W / P21/5W BAY15d",
        oemPartRef = "AMP-P215W-ARD",
        replacementUnitPriceDzd = 450,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),
      VehicleBulbCheckItem(
        id = "bulb_stop_central",
        positionName = "3ème Feu Stop Central (Hayon / Lunette)",
        zone = BulbZoneCategory.ARRIERE,
        bulbSocketType = "W16W / Rampe LED",
        oemPartRef = "AMP-W16W-STOP3",
        replacementUnitPriceDzd = 850,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),
      VehicleBulbCheckItem(
        id = "bulb_lanterne_arg",
        positionName = "Feu de position / Lanterne Arrière Gauche (ARG)",
        zone = BulbZoneCategory.ARRIERE,
        bulbSocketType = "R5W / P21/5W 12V",
        oemPartRef = "AMP-R5W-ARG",
        replacementUnitPriceDzd = 400,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),
      VehicleBulbCheckItem(
        id = "bulb_lanterne_ard",
        positionName = "Feu de position / Lanterne Arrière Droit (ARD)",
        zone = BulbZoneCategory.ARRIERE,
        bulbSocketType = "R5W / P21/5W 12V",
        oemPartRef = "AMP-R5W-ARD",
        replacementUnitPriceDzd = 400,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),
      VehicleBulbCheckItem(
        id = "bulb_cligno_arg",
        positionName = "Clignotant Arrière Gauche (ARG)",
        zone = BulbZoneCategory.ARRIERE,
        bulbSocketType = "PY21W Ambre",
        oemPartRef = "AMP-PY21W-ARG",
        replacementUnitPriceDzd = 450,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),
      VehicleBulbCheckItem(
        id = "bulb_cligno_ard",
        positionName = "Clignotant Arrière Droit (ARD)",
        zone = BulbZoneCategory.ARRIERE,
        bulbSocketType = "PY21W Ambre",
        oemPartRef = "AMP-PY21W-ARD",
        replacementUnitPriceDzd = 450,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),
      VehicleBulbCheckItem(
        id = "bulb_recul_ar",
        positionName = "Feu de recul (Marche Arrière)",
        zone = BulbZoneCategory.ARRIERE,
        bulbSocketType = "P21W BA15s / W16W",
        oemPartRef = "AMP-P21W-RECUL",
        replacementUnitPriceDzd = 500,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),
      VehicleBulbCheckItem(
        id = "bulb_brouillard_ar",
        positionName = "Feu Antibrouillard Arrière",
        zone = BulbZoneCategory.ARRIERE,
        bulbSocketType = "P21W BA15s 12V 21W",
        oemPartRef = "AMP-P21W-ABAR",
        replacementUnitPriceDzd = 450,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),
      VehicleBulbCheckItem(
        id = "bulb_plaque_immat",
        positionName = "Éclairage Plaque d'Immatriculation (Gauche & Droit)",
        zone = BulbZoneCategory.ARRIERE,
        bulbSocketType = "C5W Navette 36mm / W5W",
        oemPartRef = "AMP-C5W-PLAQUE",
        replacementUnitPriceDzd = 350,
        status = BulbCheckStatus.REMPLACEE_SUR_OR
      ),

      // --- HABITACLE & COFFRE (2 ampoules) ---
      VehicleBulbCheckItem(
        id = "bulb_plafonnier_hab",
        positionName = "Plafonnier Habitacle Avant / Arrière & Lecteur Carte",
        zone = BulbZoneCategory.HABITACLE_COFFRE,
        bulbSocketType = "C5W Navette / W5W LED",
        oemPartRef = "AMP-C5W-PLAF",
        replacementUnitPriceDzd = 400,
        status = BulbCheckStatus.OK_FONCTIONNEL
      ),
      VehicleBulbCheckItem(
        id = "bulb_coffre_boite_gants",
        positionName = "Éclairage Coffre & Boîte à Gants",
        zone = BulbZoneCategory.HABITACLE_COFFRE,
        bulbSocketType = "W5W T10 / C5W",
        oemPartRef = "AMP-W5W-COFFRE",
        replacementUnitPriceDzd = 350,
        status = BulbCheckStatus.OK_FONCTIONNEL
      )
    )

    fun defaultConditionChecks(): List<OrVehicleConditionCheckItem> = listOf(
      OrVehicleConditionCheckItem(
        id = "chk_ext_carrosserie_av_ar",
        section = "EXTÉRIEUR",
        label = "Carrosserie Pare-chocs Avant, Capot & Arrière",
        detailNote = "État peinture, absence d'enfoncement ou rayure non déclarée",
        status = OrConditionCheckStatus.CONFORME
      ),
      OrVehicleConditionCheckItem(
        id = "chk_ext_portes_ailes",
        section = "EXTÉRIEUR",
        label = "Portières, Ailes & Bas de caisse (Gauche / Droit)",
        detailNote = "Contrôle visuel latéral (propre / sale / micro-rayures)",
        status = OrConditionCheckStatus.CONFORME
      ),
      OrVehicleConditionCheckItem(
        id = "chk_ext_vitrages_optiques",
        section = "EXTÉRIEUR",
        label = "Pare-brise, Lunette, Vitres & Optiques de phares",
        detailNote = "Absence d'impact, fissure ou ternissement optique",
        status = OrConditionCheckStatus.CONFORME
      ),
      OrVehicleConditionCheckItem(
        id = "chk_ext_retroviseurs_essuie",
        section = "EXTÉRIEUR",
        label = "Rétroviseurs G/D, Balais d'essuie-glaces & Lave-glace",
        detailNote = "Coques rétroviseurs intactes et balais avant/arrière opérationnels",
        status = OrConditionCheckStatus.CONFORME
      ),
      OrVehicleConditionCheckItem(
        id = "chk_ext_jantes_pneus",
        section = "EXTÉRIEUR",
        label = "État des 4 Jantes, Pneumatiques & Écrou antivol",
        detailNote = "Pression, usure bande de roulement et présence douille antivol",
        status = OrConditionCheckStatus.CONFORME
      ),
      OrVehicleConditionCheckItem(
        id = "chk_int_sieges_sellerie",
        section = "INTÉRIEUR & HABITACLE",
        label = "Sièges Avant, Banquette Arrière & Sellerie",
        detailNote = "État de propreté (propre / taché / déchirure) + pose housse protection",
        status = OrConditionCheckStatus.CONFORME
      ),
      OrVehicleConditionCheckItem(
        id = "chk_int_tapis_moquette",
        section = "INTÉRIEUR & HABITACLE",
        label = "Tapis de sol, Moquette, Panneaux de portes & Ciel de toit",
        detailNote = "Propreté intérieure (propre / poussiéreux / boueux) + tapis papier",
        status = OrConditionCheckStatus.CONFORME
      ),
      OrVehicleConditionCheckItem(
        id = "chk_int_tdb_commandes",
        section = "INTÉRIEUR & HABITACLE",
        label = "Tableau de bord, Volant, Climatisation & Écran Multimédia",
        detailNote = "Fonctionnement commandes et absence de voyant non signalé",
        status = OrConditionCheckStatus.CONFORME
      ),
      OrVehicleConditionCheckItem(
        id = "chk_int_odeur_proprete",
        section = "INTÉRIEUR & HABITACLE",
        label = "Hygiène générale Habitacle & Coffre arrière",
        detailNote = "État général intérieur constaté contradictoirement avec le client",
        status = OrConditionCheckStatus.CONFORME
      ),
      OrVehicleConditionCheckItem(
        id = "chk_sec_roue_secours_cric",
        section = "ÉQUIPEMENTS & SÉCURITÉ",
        label = "Roue de secours / Kit gonflage, Cric, Clé & Triangle",
        detailNote = "Vérification présence dans le coffre à la prise en charge",
        status = OrConditionCheckStatus.CONFORME
      ),
      OrVehicleConditionCheckItem(
        id = "chk_sec_documents_bord",
        section = "ÉQUIPEMENTS & SÉCURITÉ",
        label = "Carte Grise, Assurance, Carnet d'entretien & Double clé",
        detailNote = "Documents de bord remis au conseiller réceptionnaire",
        status = OrConditionCheckStatus.CONFORME
      ),
      OrVehicleConditionCheckItem(
        id = "chk_sec_compartiment_moteur",
        section = "ÉQUIPEMENTS & SÉCURITÉ",
        label = "Propreté Compartiment Moteur, Cache sous-moteur & Niveaux",
        detailNote = "Absence de fuite apparente, bouchons présents et fixation carter",
        status = OrConditionCheckStatus.CONFORME
      )
    )
  }
}
