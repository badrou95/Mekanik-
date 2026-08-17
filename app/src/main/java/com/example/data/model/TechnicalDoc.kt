package com.example.data.model

data class RepairManualProcedure(
  val id: String,
  val title: String,
  val systemCategory: SystemCategory,
  val vehicleCompatibility: String,
  val estimatedTimeHours: Float,
  val difficultyLevel: String, // "Débutant", "Intermédiaire", "Expert Atelier"
  val requiredTools: List<String>,
  val stepByStepInstructions: List<String>,
  val warningNote: String? = null,
  val torqueSpecs: List<String> = emptyList()
)

data class WiringDiagramDoc(
  val id: String,
  val title: String,
  val circuitName: String,
  val ecuPins: String,
  val fuseBoxLocation: String, // e.g. "Boîte fusibles compartiment moteur BSM-01"
  val relayCodes: String,
  val wireColorCodes: List<String>,
  val schemaDiagramRef: String
)

data class TorqueSpecification(
  val componentName: String,
  val torqueNm: String,
  val angularDegrees: String? = null,
  val replacementRequirement: String = "Vis neuves obligatoires"
)

data class FluidSpecification(
  val fluidType: String, // "Huile Moteur", "Liquide de Refroidissement", "Liquide de Frein", "Huile de Boîte"
  val capacityLiters: String,
  val oemNorm: String, // e.g. "VW 507.00 / 504.00 (5W-30 LongLife III)"
  val recommendedGrade: String,
  val serviceInterval: String
)

data class IndustryDocSource(
  val name: String,
  val providerType: String, // "Fournisseur Données OEM", "Standard Indépendant", "Catalogue Pièces"
  val apiStandard: String, // "REST / SOAP WebServices", "TecDoc KType / KBA", "Autodata Connect"
  val description: String,
  val coverage: String,
  val integrationStatus: String
)
