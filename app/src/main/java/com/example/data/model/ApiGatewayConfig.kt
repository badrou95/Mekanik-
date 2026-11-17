package com.example.data.model

enum class ApiDataSourceMode(
  val label: String,
  val subtitle: String,
  val badgeText: String,
  val colorHex: Long,
  val iconEmoji: String
) {
  MEKANIK_ONE_UNIFIED(
    label = "API Unique MekanikOne (IA)",
    subtitle = "Module Unifié : SinoAuto360 + Dongchedi Auto-API + Car2DB LLM + 142 Marques + ChineseCar.parts",
    badgeText = "API UNIQUE 6-EN-1",
    colorHex = 0xFF00E5FF,
    iconEmoji = "🧠"
  ),
  TECDOC_SANDBOX(
    label = "TecDoc Sandbox Dev",
    subtitle = "Portail Développeur TecAlliance (Gratuit / Test)",
    badgeText = "SANDBOX ZERO FRAIS",
    colorHex = 0xFFFF7A00,
    iconEmoji = "🧪"
  ),
  OFFLINE_JSON(
    label = "Données Locales JSON",
    subtitle = "Catalogue embarqué Android Assets / Room (100% Offline)",
    badgeText = "OFFLINE 0ms",
    colorHex = 0xFF00E676,
    iconEmoji = "⚡"
  ),
  CAR_API_OPENSOURCE(
    label = "Car-API Open-Source",
    subtitle = "Base libre GitHub (100+ marques, 1200+ pièces)",
    badgeText = "OPEN-SOURCE FREE",
    colorHex = 0xFF00E5FF,
    iconEmoji = "🌐"
  ),
  PRODUCTION_HYBRID(
    label = "Production Hybride",
    subtitle = "TecDoc Web Pro + Cache HTTP OkHttp Intelligent",
    badgeText = "PRO ULTRA-RAPIDE",
    colorHex = 0xFFE53935,
    iconEmoji = "🚀"
  )
}

data class ApiTelemetryMetrics(
  val totalRequests: Int = 142,
  val cachedRequests: Int = 128,
  val offlineLocalHits: Int = 74,
  val apiCallsSaved: Int = 128,
  val estimatedDinarSaved: Double = 3840.0, // 30 DA par requête économisée
  val avgResponseLatencyMs: Int = 18,
  val cacheHitRatePercent: Int = 90,
  val isCacheEnabled: Boolean = true,
  val maxCacheAgeHours: Int = 72
)

data class LicensePlateMatch(
  val plateNumber: String,
  val formattedDisplay: String,
  val countryOrWilaya: String,
  val vehicleBrand: String,
  val vehicleModel: String,
  val vehicleYear: Int,
  val engineCode: String,
  val displacement: String,
  val vinNumber: String,
  val fuelType: String,
  val horsepower: String,
  val registeredOwnerType: String,
  val tecDocTypeNumber: String,
  val isMatchedFromLocalDb: Boolean = true
)

data class ApiServiceEndpoint(
  val id: String,
  val name: String,
  val provider: String,
  val endpointUrl: String,
  val status: String,
  val isConnected: Boolean,
  val latencyMs: Int,
  val authType: String,
  val isFreeTier: Boolean,
  val cachedItemsCount: Int
)
