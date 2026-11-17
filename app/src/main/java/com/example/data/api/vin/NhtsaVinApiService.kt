package com.example.data.api.vin

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class NhtsaVinApiResponse(
  @Json(name = "Count") val count: Int? = null,
  @Json(name = "Message") val message: String? = null,
  @Json(name = "SearchCriteria") val searchCriteria: String? = null,
  @Json(name = "Results") val results: List<NhtsaVinResultItem>? = null
)

@JsonClass(generateAdapter = true)
data class NhtsaVinResultItem(
  @Json(name = "VIN") val vin: String? = null,
  @Json(name = "Make") val make: String? = null,
  @Json(name = "Model") val model: String? = null,
  @Json(name = "ModelYear") val modelYear: String? = null,
  @Json(name = "Manufacturer") val manufacturer: String? = null,
  @Json(name = "PlantCountry") val plantCountry: String? = null,
  @Json(name = "PlantCity") val plantCity: String? = null,
  @Json(name = "PlantCompanyName") val plantCompanyName: String? = null,
  @Json(name = "VehicleType") val vehicleType: String? = null,
  @Json(name = "BodyClass") val bodyClass: String? = null,
  @Json(name = "Doors") val doors: String? = null,
  @Json(name = "DisplacementL") val displacementL: String? = null,
  @Json(name = "DisplacementCC") val displacementCc: String? = null,
  @Json(name = "EngineCylinders") val engineCylinders: String? = null,
  @Json(name = "EngineHP") val engineHp: String? = null,
  @Json(name = "EngineKW") val engineKw: String? = null,
  @Json(name = "EngineConfiguration") val engineConfiguration: String? = null,
  @Json(name = "FuelTypePrimary") val fuelTypePrimary: String? = null,
  @Json(name = "DriveType") val driveType: String? = null,
  @Json(name = "TransmissionStyle") val transmissionStyle: String? = null,
  @Json(name = "TransmissionSpeeds") val transmissionSpeeds: String? = null,
  @Json(name = "GVWR") val gvwr: String? = null,
  @Json(name = "BrakeSystemType") val brakeSystemType: String? = null,
  @Json(name = "Turbo") val turbo: String? = null,
  @Json(name = "ElectrificationLevel") val electrificationLevel: String? = null,
  @Json(name = "Series") val series: String? = null,
  @Json(name = "Trim") val trim: String? = null,
  @Json(name = "ErrorCode") val errorCode: String? = null,
  @Json(name = "ErrorText") val errorText: String? = null
)

interface NhtsaVinApiService {
  @GET("api/vehicles/decodevinvalues/{vin}")
  suspend fun decodeVin(
    @Path("vin") vin: String,
    @Query("format") format: String = "json"
  ): NhtsaVinApiResponse
}
