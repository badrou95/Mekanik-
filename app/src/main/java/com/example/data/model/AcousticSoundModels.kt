package com.example.data.model

enum class AcousticSoundCategory(val label: String, val iconName: String) {
  MOTEUR_BAS("Bas Moteur & Vilebrequin", "Build"),
  TURBO_ADMISSION("Suralimentation & Turbo", "Air"),
  DISTRIBUTION_VALVES("Distribution & Soupapes", "Settings"),
  INJECTION_CARBURANT("Système d'Injection HP", "LocalGasStation"),
  COURROIES_ACCESSOIRES("Courroies & Galets Tendeurs", "Loop"),
  ECHAPPEMENT_FAP("Échappement & FAP/Catalyseur", "Cloud"),
  BOITE_EMBRAYAGE("Transmission & Volant Moteur", "Speed")
}

enum class SoundSeverity(val label: String, val colorHex: Long) {
  NORMAL("Fonctionnement Normal", 0xFF00C853), // ElectricGreen
  MINEUR("Usure Légère / À Surveiller", 0xFF00A8CC), // TechCyan
  MODERE("Intervention Préventive Requise", 0xFFFFB300), // Amber
  ELEVE("Réparation Urgente Conseillée", 0xFFFF7A00), // MechanicalOrange
  CRITIQUE("ARRÊT IMMÉDIAT DU MOTEUR", 0xFFE53935) // PerformanceRed
}

data class EngineSoundPreset(
  val id: String,
  val title: String,
  val frenchSubtitle: String,
  val category: AcousticSoundCategory,
  val severity: SoundSeverity,
  val frequencyPeakHz: Int,
  val frequencyBandDescription: String,
  val decibelTypical: Int,
  val rhythmicPattern: String, // e.g. "Cadence liée au régime vilebrequin (1x RPM)", "Cadence arbre à cames (0.5x RPM)", "Sifflement continu haute fréquence"
  val description: String,
  val plainLanguageExplanation: String,
  val probableCauses: List<String>,
  val immediateActions: List<String>,
  val relatedDtcCodes: List<String>,
  val recommendedParts: List<DiagnosticRecommendedPart>,
  val estimatedRepairCostDzd: Int,
  val estimatedLaborHours: Float,
  val sampleWaveformAmplitudes: List<Float>
)

data class FrequencyBandData(
  val frequencyHz: Int,
  val amplitude: Float,
  val bandName: String,
  val isAbnormalPeak: Boolean = false
)

data class AcousticDiagnosisResult(
  val recordingId: String,
  val recordedDurationSeconds: Int,
  val timestamp: String,
  val averageDecibels: Int,
  val peakFrequencyHz: Int,
  val dominantRhythm: String,
  val identifiedPreset: EngineSoundPreset,
  val confidenceScorePercent: Int,
  val waveformAmplitudes: List<Float>,
  val frequencyBands: List<FrequencyBandData>,
  val aiAcousticAnalysisSummary: String,
  val plainExplanation: String,
  val recommendedAction: String,
  val safetyWarning: String?,
  val relatedParts: List<DiagnosticRecommendedPart> = emptyList()
)

data class SoundRecordingHistoryItem(
  val id: String,
  val title: String,
  val date: String,
  val durationSeconds: Int,
  val vehicleModel: String,
  val plateNumber: String,
  val diagnosedProblem: String,
  val severity: SoundSeverity,
  val confidenceScorePercent: Int,
  val isSharedWithWorkshop: Boolean = false
)
