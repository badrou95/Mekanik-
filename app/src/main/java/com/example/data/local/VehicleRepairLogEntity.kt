package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicle_repair_logs")
data class VehicleRepairLogEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val vehicleId: String,
  val vin: String,
  val licensePlate: String,
  val vehicleModel: String,
  val interventionType: String,
  val description: String,
  val technicianName: String,
  val costDzd: Long,
  val mileageKm: Int,
  val status: String = "COMPLETED", // e.g. COMPLETED, IN_PROGRESS, PENDING
  val createdAtTimestamp: Long = System.currentTimeMillis()
)
