package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleRepairLogDao {

  @Query("SELECT * FROM vehicle_repair_logs ORDER BY createdAtTimestamp DESC")
  fun getAllRepairLogs(): Flow<List<VehicleRepairLogEntity>>

  @Query("SELECT * FROM vehicle_repair_logs WHERE vin = :vin ORDER BY createdAtTimestamp DESC")
  fun getRepairLogsByVin(vin: String): Flow<List<VehicleRepairLogEntity>>

  @Query("SELECT * FROM vehicle_repair_logs WHERE licensePlate = :plate ORDER BY createdAtTimestamp DESC")
  fun getRepairLogsByLicensePlate(plate: String): Flow<List<VehicleRepairLogEntity>>

  @Query("SELECT * FROM vehicle_repair_logs WHERE id = :id LIMIT 1")
  suspend fun getRepairLogById(id: Long): VehicleRepairLogEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRepairLog(log: VehicleRepairLogEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllRepairLogs(logs: List<VehicleRepairLogEntity>)

  @Update
  suspend fun updateRepairLog(log: VehicleRepairLogEntity)

  @Delete
  suspend fun deleteRepairLog(log: VehicleRepairLogEntity)

  @Query("DELETE FROM vehicle_repair_logs WHERE id = :id")
  suspend fun deleteRepairLogById(id: Long)

  @Query("DELETE FROM vehicle_repair_logs")
  suspend fun clearAllRepairLogs()
}
