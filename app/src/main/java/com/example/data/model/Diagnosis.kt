package com.example.data.model

data class DiagnosticRecommendedPart(
  val partId: String? = null,
  val name: String,
  val oemReference: String,
  val brandName: String = "Qualité Première Monte (OEM EQ)",
  val equivalentBrands: List<String> = emptyList(),
  val systemCategory: SystemCategory = SystemCategory.MOTEUR,
  val priceEstimatedDzd: Int = 8500,
  val availabilityBadge: String = "En stock chez les grossistes partenaires",
  val urgencyLevel: String = "Remplacement Recommandé"
)

enum class DiagnosticTechDocType(
  val badgeLabel: String,
  val actionButtonLabel: String
) {
  WIRING_PINOUT("SCHÉMA ÉLECTRIQUE & PINOUT ECU", "Ouvrir Pinout & Fusibles"),
  EXPLODED_VIEW_TORQUE("VUE ÉCLATÉE OEM & COUPLES (Nm)", "Voir Vue Éclatée OEM"),
  TECHNICAL_BULLETIN_TSB("BULLETIN TECHNIQUE (TSB / TPI)", "Consulter Bulletin TSB"),
  REPAIR_MANUAL_UDS("MANUEL ATELIER & ADAPTATION UDS", "Ouvrir Revue Technique")
}

data class DiagnosticTechnicalDoc(
  val id: String,
  val docCode: String,
  val title: String,
  val docType: DiagnosticTechDocType,
  val summary: String,
  val keySpecsOrTorque: String,
  val ecuPinoutOrRoutine: String? = null
)

data class OBDFault(
  val code: String, // e.g. "P0299", "P0401", "P0300", "P2002", "P0101", "P0171", "P0340", "P0500"
  val standardTitle: String,
  val frenchDescription: String,
  val plainLanguageExplanation: String, // Explication vulgarisée accessible à tous
  val probableCauses: List<String>,
  val symptoms: List<String>,
  val diagnosticSteps: List<String>,
  val concernedPartCategory: SystemCategory,
  val relatedParts: List<String>,
  val detailedParts: List<DiagnosticRecommendedPart> = emptyList(),
  val multimeterSpecs: String? = null,
  val estimatedSeverity: String = "Moyenne à Élevée",
  val estimatedRepairTimeHours: Float = 1.5f,
  val recommendedTechnicalDocs: List<DiagnosticTechnicalDoc> = emptyList()
)

data class AiDiagnosticResult(
  val query: String,
  val isObdCode: Boolean,
  val faultCode: String? = null,
  val vehicleContext: String? = null,
  val plainLanguageExplanation: String, // Explication vulgarisée
  val analysisSummary: String, // Analyse technique approfondie
  val rootCauses: List<String>,
  val stepByStepInspection: List<String>,
  val multimeterSpecs: String? = null,
  val actuatorTestRecommendation: String? = null,
  val recommendedParts: List<String> = emptyList(),
  val richRecommendedParts: List<DiagnosticRecommendedPart> = emptyList(),
  val recommendedTechnicalDocs: List<DiagnosticTechnicalDoc> = emptyList(),
  val safetyWarning: String? = null,
  val severityLevel: String = "Élevée",
  val estimatedLaborHours: Float = 1.5f
)

enum class GeminiModelOption(
  val modelId: String,
  val displayName: String,
  val badge: String
) {
  FLASH_3_5("gemini-3.8-flash", "Gemini 3.8 Flash", "Temps Réel • Rapide"),
  PRO_3_1("gemini-4-argon", "Gemini 4 Argon", "Ingénierie & Diagnostic Complexe")
}

data class GeminiChatMessage(
  val id: String,
  val isUser: Boolean,
  val text: String,
  val timestamp: String,
  val modelUsed: String = GeminiModelOption.FLASH_3_5.modelId,
  val vehicleBadge: String? = null,
  val suggestedParts: List<DiagnosticRecommendedPart> = emptyList(),
  val quickFollowUps: List<String> = emptyList(),
  val isLiveCloudApi: Boolean = false
)

