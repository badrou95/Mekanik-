package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entité Room pour la mise en cache locale des schémas techniques,
 * brochages calculateurs (ECU Pinout), procédures de réparation et couples de serrage.
 * Permet une consultation instantanée (0 ms) même en sous-sol ou faible réseau atelier.
 */
@Entity(tableName = "cached_technical_schematics")
data class CachedTechnicalSchematicEntity(
  @PrimaryKey
  val id: String,
  val title: String,
  val category: String, // "SCHEMA_ELECTRIQUE", "PROCEDURE_MANUEL", "VUE_ECLATEE_OEM", "COUPLES_FLUIDES"
  val vehicleBrand: String,
  val vehicleModel: String,
  val engineCode: String,
  val systemName: String,
  val schemaReferenceCode: String,
  val ecuPinoutOrSpecs: String,
  val fuseBoxAndRelays: String,
  val wireColorsOrSteps: String, // Séparé par '\n'
  val torqueSpecsSummary: String,
  val requiredToolsSummary: String,
  val warningNotes: String = "",
  val payloadSizeKb: Int = 145,
  val isPinnedOffline: Boolean = true,
  val cachedAtFormatted: String = "En cache local SQLite",
  val cachedAtTimestamp: Long = System.currentTimeMillis(),
  val lastAccessedTimestamp: Long = System.currentTimeMillis()
)

/**
 * Entité Room pour les diagnostics enregistrés (OBD-II DTC + analyses MekanikAI).
 * Garantit l'accès hors-ligne aux causes probables, étapes de contrôle multimètre et pièces OEM.
 */
@Entity(tableName = "saved_diagnostic_records")
data class SavedDiagnosticRecordEntity(
  @PrimaryKey
  val id: String,
  val dtcCode: String,
  val diagnosticTitle: String,
  val queryOrSymptom: String,
  val vehicleModel: String,
  val vin: String,
  val licensePlate: String,
  val clientName: String,
  val severityLevel: String, // "CRITIQUE", "ÉLEVÉE", "MOYENNE", "RÉSOLU"
  val plainExplanation: String,
  val technicalSummary: String,
  val rootCausesJoined: String, // Séparé par '\n'
  val inspectionStepsJoined: String, // Séparé par '\n'
  val multimeterSpecs: String,
  val recommendedPartsJoined: String, // Séparé par '\n'
  val estimatedLaborHours: Float = 1.5f,
  val isSyncedToCloud: Boolean = true,
  val formattedDate: String,
  val createdAtTimestamp: Long = System.currentTimeMillis()
)

/**
 * Entité Room pour l'historique client et le registre des pannes récurrentes ou signalées par véhicule/client.
 */
@Entity(tableName = "client_fault_history")
data class ClientFaultHistoryEntity(
  @PrimaryKey
  val id: String,
  val clientId: String,
  val clientName: String,
  val clientPhone: String,
  val vehicleModel: String,
  val vin: String,
  val licensePlate: String,
  val mileageKm: Int,
  val faultTitle: String, // La panne du client
  val customerReportedSymptoms: String, // Symptômes décrits par le client
  val affectedSystem: String, // ex: "Suralimentation & EGR", "Injection Common Rail", "Freinage ABS"
  val linkedDtcCodes: String, // ex: "P0299, P0401"
  val diagnosticConclusion: String,
  val repairActionTaken: String,
  val partsReplaced: String,
  val totalCostDzd: Long,
  val technicianName: String,
  val status: String, // "PANNE_RESOLUE", "EN_DIAGNOSTIC", "PIECE_COMMANDEE", "A_SURVEILLER"
  val recordedDate: String,
  val isAvailableOffline: Boolean = true,
  val createdAtTimestamp: Long = System.currentTimeMillis()
)
