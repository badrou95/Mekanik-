package com.example.data.model

data class DecodedOptionalPackage(
  val optionCode: String, // e.g. "PR-WIE / R-Line", "PACK-ADAS-L2+", "PR-1ZE"
  val packageName: String,
  val includedEquipment: String,
  val mechanicalImpactNote: String // e.g. "Impose disques avant 340mm (au lieu de 312mm) + amortisseurs pilotés DCC"
)

data class DecodedManufacturerRecall(
  val campaignCode: String, // e.g. "24H8 / NHTSA-24V112", "STELLANTIS-KGH", "CHERY-TSB-2024-09"
  val title: String,
  val severityBadge: String, // "CRITIQUE SÉCURITÉ", "PRIORITAIRE CONSTRUCTEUR", "MISE À JOUR ECU"
  val statusBadge: String, // "CAMPAGNE OUVERTE (À VÉRIFIER)", "CORRECTIF DISPONIBLE EN ATELIER"
  val riskDescription: String,
  val correctiveAction: String,
  val concernedOemPartNumber: String
)

data class DecodedVinTsbItem(
  val tsbNumber: String, // e.g. "TPI 2064819/4", "TSB-B15-8821", "DCT300-NOTE-07"
  val systemCategory: String, // "Moteur & Distribution", "Boîte Double Embrayage", "Freinage EPB"
  val symptom: String,
  val rootCause: String,
  val workshopSolution: String,
  val recommendedOemPartNumber: String
)

data class DecodedVehicleSpecs(
  val vin: String,
  val isValidVin: Boolean,
  val validationMessage: String,

  // ISO-3779 17-Character Breakdown
  val wmi: String, // Chars 1-3
  val wmiOriginCountry: String,
  val wmiManufacturerName: String,

  val vds: String, // Chars 4-8
  val vdsDescription: String,

  val checkDigit: String, // Char 9
  val isCheckDigitValid: Boolean,

  val modelYearChar: String, // Char 10
  val modelYear: Int,

  val plantCode: String, // Char 11
  val plantLocation: String,

  val sequentialNumber: String, // Chars 12-17

  // Primary Technical Specifications
  val make: String, // Brand
  val model: String,
  val seriesOrGeneration: String,
  val trimLevel: String,
  val vehicleType: String,
  val bodyClass: String,
  val numberOfDoors: String,

  // Powertrain & Engine
  val displacementL: String,
  val displacementCc: String,
  val engineCylinders: String,
  val engineConfiguration: String, // e.g. "En ligne (I4)", "V6", "Électrique Triphasé"
  val enginePowerHp: String,
  val enginePowerKw: String,
  val engineCode: String,
  val fuelTypePrimary: String,
  val isTurboCharged: Boolean,
  val isElectrified: Boolean,
  val electrificationLevel: String, // e.g. "BEV (100% Électrique)", "MHEV 48V", "Thermique pur"

  // Drivetrain & Chassis
  val driveType: String, // "Traction Avant (FWD)", "Transmission Intégrale (AWD / 4Motion / Quattro)", "Propulsion (RWD)"
  val transmissionStyle: String, // "Automatique DSG 7 rapports", "Manuelle 6 vitesses"
  val brakeSystemType: String, // "Disques ventilés avant / Disques pleins arrière avec ABS/ESP"
  val steeringType: String,
  val grossVehicleWeightRating: String, // e.g. "Classe 1C : 1814 - 2268 kg"

  // Production Plant
  val plantCity: String,
  val plantCountry: String,
  val plantCompanyName: String,

  // Decoding Metadata
  val dataSource: String, // "API Publique NHTSA vPIC (Direct)" or "Décodeur Haute Précision ISO-3779"
  val decodeTimestamp: String,
  val isPublicApiSuccess: Boolean,
  val rawApiErrorCode: String = "0",

  // Expanded VIN Decoding: Trim PR/OPR Codes, Optional Packages, Recalls & TSBs
  val trimSubModelCode: String = "FINITION PRO CERTIFIÉE",
  val prOprEquipmentCodes: List<String> = emptyList(),
  val optionalPackages: List<DecodedOptionalPackage> = emptyList(),
  val manufacturerRecalls: List<DecodedManufacturerRecall> = emptyList(),
  val technicalServiceBulletins: List<DecodedVinTsbItem> = emptyList()
) {
  val fullVehicleTitle: String
    get() = "$make $model $trimLevel ($modelYear)"
}

data class VinSpecItem(
  val label: String,
  val value: String,
  val isHighlighted: Boolean = false,
  val isMono: Boolean = false
)

data class VinSpecCategoryGroup(
  val title: String,
  val iconTag: String, // "engine", "gearbox", "chassis", "factory", "security"
  val specs: List<VinSpecItem>
)

data class PresetTestVin(
  val vin: String,
  val label: String,
  val brand: String,
  val year: Int,
  val fuelType: String,
  val flagEmoji: String
)
