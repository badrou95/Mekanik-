package com.example.data.model

enum class ObdConnectionStatus {
  DISCONNECTED,
  SCANNING,
  CONNECTING,
  CONNECTED
}

data class ObdSensorGauge(
  val label: String,
  val value: Float,
  val unit: String,
  val min: Float,
  val max: Float,
  val optimalMin: Float,
  val optimalMax: Float,
  val isAlert: Boolean = false
)

data class ObdLiveTelemetry(
  val rpm: Int = 850,
  val speedKmh: Int = 0,
  val coolantTempC: Int = 90,
  val turboBoostBar: Float = 1.35f,
  val railPressureBar: Int = 1620,
  val egtTempC: Int = 410,
  val mafFlowGs: Float = 14.8f,
  val dpfSootLoadPercent: Int = 38,
  val batteryVolts: Float = 14.2f,
  val activeDtcCodes: List<String> = listOf("P0299", "P0401")
)

data class AudioNoiseAnalysis(
  val audioId: String,
  val title: String,
  val probableSource: String,
  val confidenceRate: Int,
  val severity: String,
  val acousticDescription: String,
  val recommendedAction: String,
  val waveformSample: List<Float>
)
