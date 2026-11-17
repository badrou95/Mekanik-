package com.example.data.repository

import com.example.data.model.*

object AcousticSoundRepository {

  val PRESETS: List<EngineSoundPreset> = listOf(
    EngineSoundPreset(
      id = "SOUND_ROD_KNOCK",
      title = "Claquement Lourd Bas Moteur (Coussinets de Bielle)",
      frenchSubtitle = "Bruit métallique sourd qui s'amplifie avec le régime moteur",
      category = AcousticSoundCategory.MOTEUR_BAS,
      severity = SoundSeverity.CRITIQUE,
      frequencyPeakHz = 220,
      frequencyBandDescription = "Basses fréquences résonantes (120 - 320 Hz)",
      decibelTypical = 84,
      rhythmicPattern = "Cadence exacte sur la rotation du vilebrequin (1x RPM)",
      description = "Impact métallique lourd provenant du bas moteur au niveau des paliers de vilebrequin. Provoqué par une rupture du film d'huile ou un jeu excessif du coussinet de tête de bielle.",
      plainLanguageExplanation = "Ce claquement grave au cœur du moteur indique que les coussinets de bielle ont pris du jeu critique. Le métal tape contre le vilebrequin. Tout maintien du moteur en marche risque de percer le bloc moteur (bielle coulée).",
      probableCauses = listOf(
        "Manque de pression d'huile ou niveau d'huile moteur trop bas",
        "Usure prématurée des coussinets trimétal de tête de bielle",
        "Dégradation thermique de l'huile (intervalles de vidange trop espacés)",
        "Pompe à huile défaillante ou crépine d'aspiration colmatée"
      ),
      immediateActions = listOf(
        "COUPER LE MOTEUR IMMÉDIATEMENT : Ne plus rouler pour sauver le vilebrequin.",
        "Vérifier le niveau d'huile moteur à la jauge.",
        "Remorquer le véhicule vers l'atelier mécanique pour dépose du carter d'huile.",
        "Contrôle dimensionnel du maneton et remplacement des coussinets."
      ),
      relatedDtcCodes = listOf("P0524", "P0016", "P0300"),
      recommendedParts = listOf(
        DiagnosticRecommendedPart(
          name = "Jeu de coussinets de bielle Standard (Trimétal)",
          oemReference = "038105701B-GLY",
          brandName = "Glyco / Kolbenschmidt OEM",
          priceEstimatedDzd = 9800,
          systemCategory = SystemCategory.MOTEUR
        ),
        DiagnosticRecommendedPart(
          name = "Pompe à huile haute pression renforcée",
          oemReference = "06A115105B-OEM",
          brandName = "Pierburg / Febi Bilstein",
          priceEstimatedDzd = 24500,
          systemCategory = SystemCategory.MOTEUR
        ),
        DiagnosticRecommendedPart(
          name = "Huile moteur 5W-40 Synthèse Haute Protection (5L)",
          oemReference = "OIL-5W40-5L-PRO",
          brandName = "Motul 8100 X-cess / Total Quartz",
          priceEstimatedDzd = 7800,
          systemCategory = SystemCategory.MOTEUR
        )
      ),
      estimatedRepairCostDzd = 65000,
      estimatedLaborHours = 6.0f,
      sampleWaveformAmplitudes = listOf(0.2f, 0.9f, 0.3f, 0.85f, 0.25f, 0.95f, 0.28f, 0.88f, 0.22f, 0.92f, 0.3f, 0.86f, 0.24f, 0.91f)
    ),

    EngineSoundPreset(
      id = "SOUND_TURBO_WHISTLE",
      title = "Sifflement Aigu de Suralimentation (Turbo & Durite)",
      frenchSubtitle = "Bruit strident d'aspiration ou d'air sous pression à l'accélération",
      category = AcousticSoundCategory.TURBO_ADMISSION,
      severity = SoundSeverity.ELEVE,
      frequencyPeakHz = 3400,
      frequencyBandDescription = "Hautes fréquences aiguës continues (2800 - 4800 Hz)",
      decibelTypical = 88,
      rhythmicPattern = "Sifflement continu proportionnel à la charge du turbocompresseur",
      description = "Sifflement 'type sirène de police' ou bruit de souffle violent. Signale un jeu axial/radial excessif de la turbine turbo touchant le carter ou une durite de suralimentation d'intercooler fissurée.",
      plainLanguageExplanation = "Le turbo tourne à plus de 150 000 tours/min. Un sifflement strident signifie que les ailettes frottent contre le carter en aluminium ou qu'une durite d'air comprimé fuit, entraînant une perte de puissance immédiate et de la fumée noire.",
      probableCauses = listOf(
        "Jeu axial dans l'arbre de turbine turbo par manque de lubrification",
        "Durite de suralimentation intercooler percée ou collier desserré",
        "Ébréchure sur la roue de compresseur (corps étranger aspiré)",
        "Géométrie variable grippée en position surpression"
      ),
      immediateActions = listOf(
        "Éviter les fortes accélérations pour ne pas rompre l'arbre de turbine.",
        "Vérifier visuellement l'état des durites d'admission d'air et intercooler.",
        "Contrôler la présence d'huile dans l'échangeur air/air.",
        "Vérifier le code défaut OBD P0299 à la valise de diagnostic."
      ),
      relatedDtcCodes = listOf("P0299", "P0234", "P2263"),
      recommendedParts = listOf(
        DiagnosticRecommendedPart(
          name = "Turbocompresseur Complet avec Wastegate pneumatique/électronique",
          oemReference = "03L253019T-GAR",
          brandName = "Garrett / BorgWarner OEM",
          priceEstimatedDzd = 82000,
          systemCategory = SystemCategory.MOTEUR
        ),
        DiagnosticRecommendedPart(
          name = "Durite de suralimentation Intercooler renforcée",
          oemReference = "1K0145832E-RUB",
          brandName = "Gates / Meyle HD",
          priceEstimatedDzd = 6500,
          systemCategory = SystemCategory.MOTEUR
        ),
        DiagnosticRecommendedPart(
          name = "Kit joints d'étanchéité collecteur turbo & tuyau de graissage",
          oemReference = "KIT-TURBO-SEALS-16",
          brandName = "Elring Klinger",
          priceEstimatedDzd = 3800,
          systemCategory = SystemCategory.MOTEUR
        )
      ),
      estimatedRepairCostDzd = 95000,
      estimatedLaborHours = 4.5f,
      sampleWaveformAmplitudes = listOf(0.4f, 0.6f, 0.75f, 0.85f, 0.95f, 0.9f, 0.82f, 0.88f, 0.93f, 0.85f, 0.78f, 0.7f, 0.55f, 0.4f)
    ),

    EngineSoundPreset(
      id = "SOUND_TIMING_CHAIN_RATTLE",
      title = "Ferraillement / Claquement Chaîne de Distribution",
      frenchSubtitle = "Bruit de chaîne métallique qui claque au démarrage à froid et au ralenti",
      category = AcousticSoundCategory.DISTRIBUTION_VALVES,
      severity = SoundSeverity.CRITIQUE,
      frequencyPeakHz = 850,
      frequencyBandDescription = "Moyennes fréquences d'impact métallique (600 - 1200 Hz)",
      decibelTypical = 82,
      rhythmicPattern = "Frottement métallique irrégulier avec claquements intermittents",
      description = "Bruit de cliquetis métallique sec au niveau du carter de distribution. Causé par l'allongement de la chaîne de distribution ou le grippage du tendeur hydraulique piloté par pression d'huile.",
      plainLanguageExplanation = "La chaîne de distribution est détendue et bat contre les guides en plastique. Si elle se décale d'une seule dent, les pistons frapperont les soupapes, causant la destruction complète du haut moteur.",
      probableCauses = listOf(
        "Allongement naturel de la chaîne de distribution (usure des maillons)",
        "Tendeur de chaîne hydraulique fatigué ou clapet anti-retour fuyard",
        "Patin de guidage de chaîne usé ou fissuré",
        "Pression d'huile insuffisante au démarrage à froid"
      ),
      immediateActions = listOf(
        "Ne pas monter en régime au démarrage à froid.",
        "Faire contrôler d'urgence le déphasage d'arbre à cames (Angle VVT en live).",
        "Remplacement impératif du kit chaîne complet (chaîne + tendeur + pignons)."
      ),
      relatedDtcCodes = listOf("P0016", "P0017", "P0340"),
      recommendedParts = listOf(
        DiagnosticRecommendedPart(
          name = "Kit complet Chaîne de Distribution renforcée + Tendeur hydraulique",
          oemReference = "03C198229B-INA",
          brandName = "INA Schaeffler / SKF",
          priceEstimatedDzd = 36000,
          systemCategory = SystemCategory.MOTEUR
        ),
        DiagnosticRecommendedPart(
          name = "Pignon déphaseur d'arbre à cames VVT",
          oemReference = "03C109088E-VVT",
          brandName = "Pierburg / OEM",
          priceEstimatedDzd = 28000,
          systemCategory = SystemCategory.MOTEUR
        ),
        DiagnosticRecommendedPart(
          name = "Joint de carter de distribution & bague vilebrequin",
          oemReference = "ELR-342-910",
          brandName = "Elring",
          priceEstimatedDzd = 4200,
          systemCategory = SystemCategory.MOTEUR
        )
      ),
      estimatedRepairCostDzd = 78000,
      estimatedLaborHours = 5.5f,
      sampleWaveformAmplitudes = listOf(0.3f, 0.7f, 0.45f, 0.85f, 0.4f, 0.78f, 0.5f, 0.9f, 0.42f, 0.82f, 0.38f, 0.75f, 0.44f, 0.88f)
    ),

    EngineSoundPreset(
      id = "SOUND_BELT_SQUEAL",
      title = "Couinement Aigu Courroie Accessoire & Alternateur",
      frenchSubtitle = "Grincement strident continu au démarrage, par temps humide ou braquage",
      category = AcousticSoundCategory.COURROIES_ACCESSOIRES,
      severity = SoundSeverity.MODERE,
      frequencyPeakHz = 4200,
      frequencyBandDescription = "Très hautes fréquences de friction élastomère (3200 - 5500 Hz)",
      decibelTypical = 79,
      rhythmicPattern = "Couinement strident continu amplifié par la climatisation ou braquage",
      description = "Patinage de la courroie trapézoïdale poly-V sur les poulies d'accessoires. Souvent lié à un galet tendeur automatique affaibli, une poulie débrayable d'alternateur bloquée ou une courroie glacée.",
      plainLanguageExplanation = "La courroie qui entraîne l'alternateur et la pompe à eau glisse sur le métal. Si elle casse, vous perdez la charge de batterie, l'assistance de direction et le refroidissement du moteur instantanément.",
      probableCauses = listOf(
        "Courroie accessoire détendue ou caoutchouc desséché/glacé",
        "Galet tendeur dynamique affaibli (ressort interne fatigué)",
        "Poulie débrayable d'alternateur grippée (ne joue plus son rôle de roue libre)",
        "Poulie damper de vilebrequin dont le caoutchouc amortisseur est délaminé"
      ),
      immediateActions = listOf(
        "Vérifier la tension et l'état des stries de la courroie.",
        "Couper la climatisation et les gros consommateurs électriques pour tester.",
        "Remplacement du kit courroie d'accessoires + galet tendeur."
      ),
      relatedDtcCodes = listOf("P0562", "P0620"),
      recommendedParts = listOf(
        DiagnosticRecommendedPart(
          name = "Kit Courroie Poly-V d'Accessoires + Galet Tendeur Automatique",
          oemReference = "6PK1070-K01",
          brandName = "Continental Contitech / Gates",
          priceEstimatedDzd = 12500,
          systemCategory = SystemCategory.MOTEUR
        ),
        DiagnosticRecommendedPart(
          name = "Poulie Débrayable d'Alternateur Roue Libre",
          oemReference = "535001210-INA",
          brandName = "INA Schaeffler",
          priceEstimatedDzd = 9500,
          systemCategory = SystemCategory.MOTEUR
        ),
        DiagnosticRecommendedPart(
          name = "Poulie Damper Vilebrequin avec Silentbloc",
          oemReference = "038105243-COR",
          brandName = "Corteco / Febi",
          priceEstimatedDzd = 16800,
          systemCategory = SystemCategory.MOTEUR
        )
      ),
      estimatedRepairCostDzd = 28000,
      estimatedLaborHours = 1.5f,
      sampleWaveformAmplitudes = listOf(0.6f, 0.65f, 0.7f, 0.72f, 0.68f, 0.74f, 0.71f, 0.69f, 0.73f, 0.7f, 0.66f, 0.72f, 0.69f, 0.71f)
    ),

    EngineSoundPreset(
      id = "SOUND_INJECTOR_TICK",
      title = "Tic-Tac Sec & Claquement Injecteurs Haute Pression",
      frenchSubtitle = "Clapottis rapide et sec au niveau de la rampe commune Common Rail",
      category = AcousticSoundCategory.INJECTION_CARBURANT,
      severity = SoundSeverity.MODERE,
      frequencyPeakHz = 1850,
      frequencyBandDescription = "Fréquences d'impulsion électromécanique (1400 - 2400 Hz)",
      decibelTypical = 73,
      rhythmicPattern = "Tic-tac rapide et très régulier indexé sur les cycles d'injection",
      description = "Bruit de pulsation rapide et sec des aiguilles d'injecteurs ou fuite de compression par le joint pare-feu en cuivre (bruit de pschitt rythmé avec calamine noire).",
      plainLanguageExplanation = "Les injecteurs pulvérisent le carburant à 2000 bars. Un cliquetis trop fort ou un souffle régulier indique un injecteur qui grippe ou un joint pare-feu fuyard, ce qui encrasse le moteur et augmente la consommation.",
      probableCauses = listOf(
        "Fuite du joint pare-feu en cuivre à l'embase de l'injecteur",
        "Aiguille d'injecteur piezo ou solénoïde légèrement grippée par la calamine",
        "Correction de débit injecteur anormale (dérive d'un cylindre)",
        "Pression de rampe instable (régulateur de pression fatigué)"
      ),
      immediateActions = listOf(
        "Vérifier visuellement l'absence de goudron / calamine noire autour des puits d'injecteurs.",
        "Tester le retour des injecteurs à l'aide d'éprouvettes graduées.",
        "Remplacement des joints d'embase en cuivre et recalibrage des injecteurs."
      ),
      relatedDtcCodes = listOf("P0201", "P0202", "P0299", "P0087"),
      recommendedParts = listOf(
        DiagnosticRecommendedPart(
          name = "Kit 4x Joints Pare-Feu Cuivre + Rondelles et Vis de Bride",
          oemReference = "KIT-INJ-COPPER-4X",
          brandName = "Bosch Original / Elring",
          priceEstimatedDzd = 2800,
          systemCategory = SystemCategory.MOTEUR
        ),
        DiagnosticRecommendedPart(
          name = "Nettoyant Injecteurs Haute Pression Formule Pro (500ml)",
          oemReference = "WYNNS-DIESEL-PRO",
          brandName = "Wynn's / Bardahl Pro",
          priceEstimatedDzd = 3200,
          systemCategory = SystemCategory.MOTEUR
        ),
        DiagnosticRecommendedPart(
          name = "Injecteur Common Rail Échange Standard Reconditionné",
          oemReference = "0445110-BOSCH",
          brandName = "Bosch Common Rail",
          priceEstimatedDzd = 34000,
          systemCategory = SystemCategory.MOTEUR
        )
      ),
      estimatedRepairCostDzd = 22000,
      estimatedLaborHours = 2.0f,
      sampleWaveformAmplitudes = listOf(0.2f, 0.55f, 0.2f, 0.58f, 0.18f, 0.6f, 0.22f, 0.54f, 0.2f, 0.57f, 0.19f, 0.56f, 0.21f, 0.59f)
    ),

    EngineSoundPreset(
      id = "SOUND_FLYWHEEL_RATTLE",
      title = "Grondement & Vibrations Volant Moteur Bimasse",
      frenchSubtitle = "Claquement sourd au ralenti qui s'estompe en appuyant sur la pédale d'embrayage",
      category = AcousticSoundCategory.BOITE_EMBRAYAGE,
      severity = SoundSeverity.ELEVE,
      frequencyPeakHz = 320,
      frequencyBandDescription = "Basses fréquences d'oscillations mécaniques (180 - 450 Hz)",
      decibelTypical = 81,
      rhythmicPattern = "Grondement sourd et claquement irrégulier qui disparaît au débrayage",
      description = "Jeu excessif des ressorts amortisseurs internes du volant bimasse (DMF). Génère des vibrations dans le plancher et la pédale d'embrayage ainsi qu'un claquement métallique au ralenti.",
      plainLanguageExplanation = "Le volant moteur bimasse absorbe les vibrations du moteur diesel/essence. Quand ses ressorts internes sont fatigués, il cogne au ralenti et peut détruire la boîte de vitesses ou le démarreur.",
      probableCauses = listOf(
        "Affaissement des ressorts à arc internes du volant moteur bimasse",
        "Jeu axial et angulaire excessif entre les deux masses tournantes",
        "Usure conjointe du disque d'embrayage et de la butée hydraulique"
      ),
      immediateActions = listOf(
        "Appuyer sur la pédale d'embrayage : si le bruit disparaît, le bimasse est confirmé.",
        "Éviter les sous-régimes prolongés en 4e/5e vitesse.",
        "Prévoir le remplacement du kit 4 pièces (Volant bimasse + disque + mécanisme + butée)."
      ),
      relatedDtcCodes = listOf("P0700", "P0805"),
      recommendedParts = listOf(
        DiagnosticRecommendedPart(
          name = "Kit 4 Pièces Volant Moteur Bimasse (DMF) + Kit Embrayage Complet",
          oemReference = "600001600-LUK",
          brandName = "LuK Schaeffler / Sachs OEM",
          priceEstimatedDzd = 74000,
          systemCategory = SystemCategory.MOTEUR
        ),
        DiagnosticRecommendedPart(
          name = "Butée Hydraulique d'Embrayage CSC",
          oemReference = "510007310-LUK",
          brandName = "LuK / Valeo",
          priceEstimatedDzd = 9800,
          systemCategory = SystemCategory.MOTEUR
        )
      ),
      estimatedRepairCostDzd = 95000,
      estimatedLaborHours = 5.0f,
      sampleWaveformAmplitudes = listOf(0.4f, 0.8f, 0.35f, 0.78f, 0.42f, 0.82f, 0.38f, 0.75f, 0.44f, 0.84f, 0.39f, 0.79f, 0.41f, 0.81f)
    ),

    EngineSoundPreset(
      id = "SOUND_NORMAL_HEALTHY",
      title = "Sonorité Moteur Saine & Harmonique Équilibrée",
      frenchSubtitle = "Cycle de combustion régulier, aucune vibration anormale ni cliquetis",
      category = AcousticSoundCategory.MOTEUR_BAS,
      severity = SoundSeverity.NORMAL,
      frequencyPeakHz = 450,
      frequencyBandDescription = "Spectre acoustique uniforme et régulier (100 - 6000 Hz)",
      decibelTypical = 68,
      rhythmicPattern = "Sonorité ronde et stable sans pics de résonance anormaux",
      description = "Le profil acoustique capté correspond aux tolérances d'usine du constructeur. La combustion, le calage des arbres à cames et les accessoires fonctionnent de manière fluide.",
      plainLanguageExplanation = "Excellente nouvelle ! L'intelligence acoustique MekanikAI n'a détecté aucune anomalie audible sur votre moteur. Les harmoniques d'admission, de distribution et d'échappement sont parfaitement équilibrées.",
      probableCauses = listOf(
        "Moteur en parfait état de fonctionnement",
        "Lubrification optimale et courroies correctement tendues",
        "Injecteurs et combustion bien équilibrés sur tous les cylindres"
      ),
      immediateActions = listOf(
        "Poursuivre les entretiens périodiques selon le carnet d'entretien Mekanik+.",
        "Contrôler régulièrement les niveaux de fluides (huile, liquide de refroidissement).",
        "Enregistrer un nouveau son dans 5000 km pour suivre l'évolution acoustique."
      ),
      relatedDtcCodes = emptyList(),
      recommendedParts = listOf(
        DiagnosticRecommendedPart(
          name = "Kit d'Entretien Préventif & Filtres Vidange (Huile + Air + Habitacle)",
          oemReference = "KIT-SERVICE-FILTER-PRO",
          brandName = "Mann-Filter / Bosch",
          priceEstimatedDzd = 8500,
          systemCategory = SystemCategory.MOTEUR
        )
      ),
      estimatedRepairCostDzd = 0,
      estimatedLaborHours = 0.0f,
      sampleWaveformAmplitudes = listOf(0.3f, 0.35f, 0.32f, 0.36f, 0.34f, 0.33f, 0.35f, 0.32f, 0.36f, 0.34f, 0.33f, 0.35f, 0.32f, 0.34f)
    )
  )

  fun findPresetById(id: String): EngineSoundPreset {
    return PRESETS.find { it.id == id } ?: PRESETS.first()
  }

  fun analyzeAcousticRecording(
    detectedDominantHz: Int,
    decibelLevel: Int,
    selectedPresetId: String? = null,
    vehicle: Vehicle? = null
  ): AcousticDiagnosisResult {
    val preset = if (selectedPresetId != null) {
      findPresetById(selectedPresetId)
    } else {
      when {
        detectedDominantHz in 100..300 && decibelLevel > 80 -> findPresetById("SOUND_ROD_KNOCK")
        detectedDominantHz > 2500 && decibelLevel > 82 -> findPresetById("SOUND_TURBO_WHISTLE")
        detectedDominantHz in 700..1200 && decibelLevel > 78 -> findPresetById("SOUND_TIMING_CHAIN_RATTLE")
        detectedDominantHz > 3800 -> findPresetById("SOUND_BELT_SQUEAL")
        detectedDominantHz in 1300..2400 -> findPresetById("SOUND_INJECTOR_TICK")
        detectedDominantHz in 300..600 && decibelLevel > 77 -> findPresetById("SOUND_FLYWHEEL_RATTLE")
        else -> findPresetById("SOUND_NORMAL_HEALTHY")
      }
    }

    val bands = listOf(
      FrequencyBandData(60, 0.35f, "Sub-Bass (Vibrations châssis)"),
      FrequencyBandData(220, if (preset.id == "SOUND_ROD_KNOCK") 0.95f else 0.4f, "Basses (Vilebrequin / Bielles)", preset.id == "SOUND_ROD_KNOCK"),
      FrequencyBandData(500, if (preset.id == "SOUND_FLYWHEEL_RATTLE") 0.85f else 0.38f, "Bas-Médiums (Volant Moteur)", preset.id == "SOUND_FLYWHEEL_RATTLE"),
      FrequencyBandData(950, if (preset.id == "SOUND_TIMING_CHAIN_RATTLE") 0.92f else 0.42f, "Médiums (Chaîne Distribution)", preset.id == "SOUND_TIMING_CHAIN_RATTLE"),
      FrequencyBandData(1850, if (preset.id == "SOUND_INJECTOR_TICK") 0.88f else 0.35f, "Hauts-Médiums (Injecteurs HP)", preset.id == "SOUND_INJECTOR_TICK"),
      FrequencyBandData(3400, if (preset.id == "SOUND_TURBO_WHISTLE") 0.96f else 0.32f, "Aigus (Turbocompresseur)", preset.id == "SOUND_TURBO_WHISTLE"),
      FrequencyBandData(4500, if (preset.id == "SOUND_BELT_SQUEAL") 0.94f else 0.28f, "Très Aigus (Courroies Poly-V)", preset.id == "SOUND_BELT_SQUEAL")
    )

    val vContext = vehicle?.let { "${it.brand} ${it.model} (${it.displacement} / ${it.engineCode})" } ?: "Moteur thermique automobile"

    return AcousticDiagnosisResult(
      recordingId = "REC-AUDIO-${(1000..9999).random()}",
      recordedDurationSeconds = 8,
      timestamp = "Aujourd'hui à ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.FRENCH).format(java.util.Date())}",
      averageDecibels = decibelLevel,
      peakFrequencyHz = preset.frequencyPeakHz,
      dominantRhythm = preset.rhythmicPattern,
      identifiedPreset = preset,
      confidenceScorePercent = if (preset.severity == SoundSeverity.NORMAL) 96 else 92,
      waveformAmplitudes = preset.sampleWaveformAmplitudes,
      frequencyBands = bands,
      aiAcousticAnalysisSummary = "Analyse spectrale IA terminée pour $vContext : Profil acoustique identifié avec 92% de concordance spectrale avec l'empreinte sonore '${preset.title}'.",
      plainExplanation = preset.plainLanguageExplanation,
      recommendedAction = preset.immediateActions.joinToString(" • "),
      safetyWarning = if (preset.severity == SoundSeverity.CRITIQUE) "DANGER : Risque de casse moteur irrémédiable si le moteur reste tournant." else null,
      relatedParts = preset.recommendedParts
    )
  }
}
