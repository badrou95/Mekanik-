package com.example.data.model

data class OBDFault(
  val code: String, // e.g. "P0299", "P0401", "P0300"
  val standardTitle: String,
  val frenchDescription: String,
  val probableCauses: List<String>,
  val symptoms: List<String>,
  val diagnosticSteps: List<String>,
  val concernedPartCategory: SystemCategory,
  val relatedParts: List<String>,
  val estimatedSeverity: String = "Moyenne à Élevée"
)

data class AiDiagnosticResult(
  val query: String,
  val isObdCode: Boolean,
  val faultCode: String? = null,
  val vehicleContext: String? = null,
  val analysisSummary: String,
  val rootCauses: List<String>,
  val stepByStepInspection: List<String>,
  val multimeterSpecs: String? = null,
  val recommendedParts: List<String>,
  val safetyWarning: String? = null
)
