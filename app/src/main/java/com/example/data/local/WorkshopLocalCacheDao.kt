package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkshopLocalCacheDao {

  // ==========================================================================
  // 1. SCHÉMAS TECHNIQUES & PROCÉDURES EN CACHE LOCAL
  // ==========================================================================
  @Query("SELECT * FROM cached_technical_schematics ORDER BY isPinnedOffline DESC, lastAccessedTimestamp DESC")
  fun getAllCachedSchematics(): Flow<List<CachedTechnicalSchematicEntity>>

  @Query("SELECT * FROM cached_technical_schematics WHERE category = :category ORDER BY lastAccessedTimestamp DESC")
  fun getCachedSchematicsByCategory(category: String): Flow<List<CachedTechnicalSchematicEntity>>

  @Query("SELECT COUNT(*) FROM cached_technical_schematics")
  suspend fun getCachedSchematicsCount(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertCachedSchematic(schematic: CachedTechnicalSchematicEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAllCachedSchematics(schematics: List<CachedTechnicalSchematicEntity>)

  @Query("UPDATE cached_technical_schematics SET isPinnedOffline = :pinned, lastAccessedTimestamp = :timestamp WHERE id = :id")
  suspend fun updateSchematicPinStatus(id: String, pinned: Boolean, timestamp: Long = System.currentTimeMillis())

  @Query("DELETE FROM cached_technical_schematics WHERE id = :id")
  suspend fun deleteCachedSchematicById(id: String)

  // ==========================================================================
  // 2. DIAGNOSTICS ENREGISTRÉS (OBD-II & MEKANIKAI)
  // ==========================================================================
  @Query("SELECT * FROM saved_diagnostic_records ORDER BY createdAtTimestamp DESC")
  fun getAllSavedDiagnostics(): Flow<List<SavedDiagnosticRecordEntity>>

  @Query("SELECT * FROM saved_diagnostic_records WHERE licensePlate = :plate OR vin = :vin ORDER BY createdAtTimestamp DESC")
  fun getSavedDiagnosticsForVehicle(plate: String, vin: String): Flow<List<SavedDiagnosticRecordEntity>>

  @Query("SELECT COUNT(*) FROM saved_diagnostic_records")
  suspend fun getSavedDiagnosticsCount(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertSavedDiagnostic(record: SavedDiagnosticRecordEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAllSavedDiagnostics(records: List<SavedDiagnosticRecordEntity>)

  @Query("UPDATE saved_diagnostic_records SET isSyncedToCloud = 1 WHERE isSyncedToCloud = 0")
  suspend fun markAllDiagnosticsSynced()

  @Query("DELETE FROM saved_diagnostic_records WHERE id = :id")
  suspend fun deleteSavedDiagnosticById(id: String)

  // ==========================================================================
  // 3. HISTORIQUE CLIENT & PANNES DES CLIENTS
  // ==========================================================================
  @Query("SELECT * FROM client_fault_history ORDER BY createdAtTimestamp DESC")
  fun getAllClientFaultHistory(): Flow<List<ClientFaultHistoryEntity>>

  @Query("SELECT * FROM client_fault_history WHERE clientName LIKE '%' || :query || '%' OR licensePlate LIKE '%' || :query || '%' OR faultTitle LIKE '%' || :query || '%' ORDER BY createdAtTimestamp DESC")
  fun searchClientFaultHistory(query: String): Flow<List<ClientFaultHistoryEntity>>

  @Query("SELECT COUNT(*) FROM client_fault_history")
  suspend fun getClientFaultHistoryCount(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertClientFaultRecord(record: ClientFaultHistoryEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAllClientFaultRecords(records: List<ClientFaultHistoryEntity>)

  @Query("UPDATE client_fault_history SET status = :newStatus WHERE id = :id")
  suspend fun updateClientFaultStatus(id: String, newStatus: String)

  @Query("DELETE FROM client_fault_history WHERE id = :id")
  suspend fun deleteClientFaultRecordById(id: String)
}
