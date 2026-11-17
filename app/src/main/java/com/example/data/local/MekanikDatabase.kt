package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    VehicleRepairLogEntity::class,
    CachedTechnicalSchematicEntity::class,
    SavedDiagnosticRecordEntity::class,
    ClientFaultHistoryEntity::class
  ],
  version = 2,
  exportSchema = false
)
abstract class MekanikDatabase : RoomDatabase() {

  abstract fun vehicleRepairLogDao(): VehicleRepairLogDao
  abstract fun workshopLocalCacheDao(): WorkshopLocalCacheDao

  companion object {
    @Volatile
    private var INSTANCE: MekanikDatabase? = null

    fun getDatabase(context: Context): MekanikDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          MekanikDatabase::class.java,
          "mekanik_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
