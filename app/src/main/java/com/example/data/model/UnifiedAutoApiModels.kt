package com.example.data.model

// ============================================================================
// MODULE D'AGRÉGATION IA : « MEKANIK-ONE UNIFIED AUTO-API »
// Regroupe en une seule API unifiée :
// 1. SinoAuto360 API (50+ marques chinoises, décodage VIN usines Chine, OEM RESTful JSON)
// 2. Auto-API / Dongchedi API (Spécifications techniques, finitions, moteurs Geely/Jetour/FAW-VAG pour Function Calling)
// 3. Car2DB API (Structures en fil d'Ariane / Breadcrumbs & résumés textuels optimisés LLM)
// 4. GitHub lifeofcapo/car-api (Base locale embarquée 142 marques dont Geely & Chery/Jetour)
// 5. ChineseCar.parts & CatCar.info (Arborescences JSON reverse-engineered pour scénarios démo IA)
// ============================================================================

enum class UnifiedApiConnectorProvider(
  val id: String,
  val displayName: String,
  val shortBadge: String,
  val specialization: String,
  val endpointPattern: String,
  val accentColorHex: Long,
  val averageLatencyMs: Int,
  val isLlmFunctionCallingReady: Boolean = true,
  val isHistoricalMekanikApi: Boolean = false,
  val generationLabel: String = "API CHINE & IA"
) {
  SINO_AUTO_360(
    id = "sinoauto360",
    displayName = "SinoAuto360 API",
    shortBadge = "VIN CHINE & OEM REST",
    specialization = "50+ constructeurs chinois (Geely, Jetour/Chery, BYD, Changan) • Décodage VIN usines chinoises & catalogue pièces OEM JSON",
    endpointPattern = "https://api.sinoauto360.com/v2/vin-oem/decode",
    accentColorHex = 0xFFE53935,
    averageLatencyMs = 42,
    isHistoricalMekanikApi = false,
    generationLabel = "NOUVELLE API CHINE / IA"
  ),
  DONGCHEDI_AUTO_API(
    id = "dongchedi_auto_api",
    displayName = "Auto-API (Dongchedi)",
    shortBadge = "SPECS & GEMINI FN-CALL",
    specialization = "Spécifications techniques, finitions & moteurs (Geely, Jetour, FAW-VAG) • JSON ultra-propre mis à jour en continu pour Function Calling",
    endpointPattern = "https://auto-api.dongchedi-partner.cn/v1/specs/extract",
    accentColorHex = 0xFFFF7A00,
    averageLatencyMs = 36,
    isHistoricalMekanikApi = false,
    generationLabel = "NOUVELLE API CHINE / IA"
  ),
  CAR2DB_LLM_API(
    id = "car2db_llm",
    displayName = "Car2DB API (LLM Edition)",
    shortBadge = "FIL D'ARIANE & LLM DIGEST",
    specialization = "Base automobile internationale & chinoise avec requêtes optimisées LLM (Fil d'Ariane hiérarchique + résumés textuels intégrés)",
    endpointPattern = "https://api.car2db.com/v3/llm-breadcrumb/vehicle",
    accentColorHex = 0xFF00E5FF,
    averageLatencyMs = 21,
    isHistoricalMekanikApi = false,
    generationLabel = "NOUVELLE API CHINE / IA"
  ),
  CHINESECAR_CATCAR_JSON(
    id = "chinesecar_catcar",
    displayName = "ChineseCar.parts & CatCar.info",
    shortBadge = "ARBORESCENCE OEM JSON",
    specialization = "Catalogues Web ouverts & arborescences JSON OEM (Plaquettes Jetour Dashing, Geely Coolray, Chery Tiggo 8, Europe) injectables dans Gemini",
    endpointPattern = "https://chinesecar.parts/api/tree-export/oem-schema",
    accentColorHex = 0xFFAB47BC,
    averageLatencyMs = 14,
    isHistoricalMekanikApi = false,
    generationLabel = "NOUVELLE API CHINE / IA"
  ),
  OEM_SERVICEBOX_DIALOGYS_ETKA(
    id = "oem_servicebox_dialogys_etka",
    displayName = "Portails Constructeurs (ServiceBox / Dialogys / ETKA)",
    shortBadge = "STELLANTIS • RENAULT • VAG • BMW",
    specialization = "Passerelle historique vers les portails officiels : Stellantis ServiceBox EPC, Renault Dialogys, VAG ETKA, BMW ETK, Mercedes WIS & Microcat Asie",
    endpointPattern = "https://epc-gateway.mekanikplus.dz/v2/oem-portals/vin-filter",
    accentColorHex = 0xFF3D5AFE,
    averageLatencyMs = 34,
    isHistoricalMekanikApi = true,
    generationLabel = "API HISTORIQUE INTÉGRÉE"
  ),
  TECDOC_ALLIANCE_API(
    id = "tecdoc_alliance_api",
    displayName = "TecDoc / TecAlliance API (Sandbox & Pro)",
    shortBadge = "900+ ÉQUIPEMENTIERS IAM • KTYPE",
    specialization = "Référentiel mondial après-vente historique : 900+ marques IAM (Bosch, Valeo, Brembo, Sachs, Mann-Filter), KType & équivalences croisées OEM",
    endpointPattern = "https://web.tecalliance.net/api/v1/sandbox/articles",
    accentColorHex = 0xFFFF9100,
    averageLatencyMs = 42,
    isHistoricalMekanikApi = true,
    generationLabel = "API HISTORIQUE INTÉGRÉE"
  ),
  HAYNESPRO_AUTODATA_API(
    id = "haynespro_autodata_api",
    displayName = "HaynesPro WorkshopData & Autodata API",
    shortBadge = "SCHÉMAS VESA • COUPLES • SMARTCASE",
    specialization = "Base technique d'atelier historique : couples de serrage OEM, schémas électriques interactifs VESA, bulletins SmartCASE & barèmes de temps",
    endpointPattern = "https://haynespro.mekanikplus.dz/api/v1/workshopdata/vin",
    accentColorHex = 0xFFFFD600,
    averageLatencyMs = 29,
    isHistoricalMekanikApi = true,
    generationLabel = "API HISTORIQUE INTÉGRÉE"
  ),
  GLOBAL_OEM_7ZAP_TECDOC(
    id = "global_oem_bridge",
    displayName = "Passerelle 7Zap & PartsSouq EPC",
    shortBadge = "ÉCLATÉS 7ZAP • PARTSSOUQ",
    specialization = "Pont historique international vers les schémas éclatés 7Zap & PartsSouq pour véhicules européens, japonais, coréens et Moyen-Orient/Afrique",
    endpointPattern = "https://gateway.mekanik-plus.ai/v1/7zap-partssouq-epc",
    accentColorHex = 0xFF29B6F6,
    averageLatencyMs = 28,
    isHistoricalMekanikApi = true,
    generationLabel = "API HISTORIQUE INTÉGRÉE"
  ),
  NHTSA_VPIC_PUBLIC_API(
    id = "nhtsa_vpic_public",
    displayName = "NHTSA vPIC Global VIN API (US DOT)",
    shortBadge = "REST PUBLIC LIVE • vpic.nhtsa.dot.gov",
    specialization = "API publique REST officielle du Département des Transports US (Retrofit 2 + OkHttp) pour le décodage VIN mondial, Tesla, Ford, GM & imports",
    endpointPattern = "https://vpic.nhtsa.dot.gov/api/vehicles/decodevinvaluesextended",
    accentColorHex = 0xFF26A69A,
    averageLatencyMs = 85,
    isHistoricalMekanikApi = true,
    generationLabel = "API HISTORIQUE INTÉGRÉE"
  ),
  GITHUB_LIFEOFCAPO_CAR_API(
    id = "lifeofcapo_car_api",
    displayName = "GitHub lifeofcapo/car-api (142 Marques)",
    shortBadge = "142 MARQUES OPEN-SOURCE",
    specialization = "Base open-source TypeScript/JSON embarquée localement : 142 marques & générations (Geely, Chery/Jetour, BYD, Europe & Asie) à 1 ms",
    endpointPattern = "local://assets/lifeofcapo-car-api-142brands.json",
    accentColorHex = 0xFF00E676,
    averageLatencyMs = 1,
    isHistoricalMekanikApi = false,
    generationLabel = "OPEN-SOURCE EMBARQUÉ"
  ),
  OFFLINE_ROOM_JSON_CACHE(
    id = "offline_room_json_cache",
    displayName = "Cache Local Room SQLite / Assets JSON + ISO-3779",
    shortBadge = "OFFLINE 0ms • ROOM + ISO-3779",
    specialization = "Secours ultime 100% hors-ligne historique (0 ms) : Catalogue embarqué Android Assets / Room SQLite + Décodeur mathématique universel ISO-3779",
    endpointPattern = "local://assets/catalog_oem_parts.json",
    accentColorHex = 0xFF69F0AE,
    averageLatencyMs = 1,
    isHistoricalMekanikApi = true,
    generationLabel = "SECOURS LOCAL HISTORIQUE 0ms"
  )
}

data class UnifiedSinoOemPartItem(
  val partId: String,
  val calloutRef: String, // ex: "[1]", "[2]"
  val partName: String,
  val systemCategory: String,
  val oemPartNumber: String,
  val crossReferences: List<String>,
  val sourceProvider: UnifiedApiConnectorProvider,
  val fitmentPosition: String,
  val technicalDimensions: String,
  val torqueSpecsNm: String,
  val estimatedPriceDzd: Int,
  val stockAvailability: String
)

data class SinoChineseVinRecord(
  val id: String,
  val vin: String,
  val wmiPrefix: String,
  val brand: String,
  val parentGroup: String,
  val model: String,
  val chineseDomesticName: String,
  val generationCode: String,
  val year: Int,
  val assemblyPlant: String,
  val engineCode: String,
  val engineFamilyName: String,
  val displacementAndPower: String,
  val transmissionCode: String,
  val trimFinishDongchedi: String,
  val driveType: String,
  val car2DbBreadcrumb: String,
  val car2DbLlmSummary: String,
  val dongchediFunctionCallJson: String,
  val oemPartsCatalog: List<UnifiedSinoOemPartItem>
)

data class ReverseEngineeredDemoScenario(
  val id: String,
  val badgeLabel: String,
  val title: String,
  val subtitle: String,
  val brand: String,
  val model: String,
  val year: Int,
  val vinExample: String,
  val engineCode: String,
  val sourcePlatformNames: String,
  val car2DbBreadcrumb: String,
  val car2DbLlmDigest: String,
  val geminiFunctionCallName: String,
  val geminiFunctionCallArgsJson: String,
  val rawJsonTreeContext: String,
  val stepByStepMechanicalProcedure: List<String>,
  val specialToolOrObdResetNote: String,
  val partsList: List<UnifiedSinoOemPartItem>
)

data class LifeOfCapoBrandCatalogEntry(
  val brandId: String,
  val brandName: String,
  val parentHolding: String,
  val originCountry: String,
  val isChineseHighlight: Boolean,
  val generationsCount: Int,
  val modelsCount: Int,
  val flagshipModels: List<String>,
  val engineCodesIndexed: List<String>,
  val localJsonSizeKb: Int
)

// ============================================================================
// MÉCANISME DE FALLBACK EN CASCADE (REPOSITORY MULTI-SOURCES)
// Si une API est indisponible (503/504/429) ou ne trouve pas le VIN (404),
// le Repository interroge automatiquement la source suivante dans la liste.
// ============================================================================

enum class ApiSourceHealthState(
  val label: String,
  val shortBadge: String,
  val httpStatusCode: Int,
  val colorHex: Long
) {
  ONLINE(
    label = "Opérationnelle (200 OK)",
    shortBadge = "200 ONLINE",
    httpStatusCode = 200,
    colorHex = 0xFF00E676
  ),
  VIN_NOT_IN_CATALOG(
    label = "VIN absent de cette source (404 Not Found)",
    shortBadge = "404 VIN ABSENT",
    httpStatusCode = 404,
    colorHex = 0xFFFFB300
  ),
  UNAVAILABLE_503(
    label = "Serveur Indisponible (503 Service Unavailable)",
    shortBadge = "503 PANNE",
    httpStatusCode = 503,
    colorHex = 0xFFE53935
  ),
  TIMEOUT_504(
    label = "Délai dépassé / Circuit-Breaker (504 Timeout)",
    shortBadge = "504 TIMEOUT",
    httpStatusCode = 504,
    colorHex = 0xFFFF7A00
  ),
  RATE_LIMITED_429(
    label = "Quota Dépassé (429 Too Many Requests)",
    shortBadge = "429 QUOTA",
    httpStatusCode = 429,
    colorHex = 0xFFAB47BC
  )
}

enum class FallbackAttemptStatus(
  val badgeLabel: String,
  val isResolvedSuccess: Boolean,
  val isFailedAndCascaded: Boolean,
  val colorHex: Long
) {
  RESOLVED_SUCCESS(
    badgeLabel = "RÉSOLU 200 OK (SOURCE RETENUE)",
    isResolvedSuccess = true,
    isFailedAndCascaded = false,
    colorHex = 0xFF00E676
  ),
  FALLBACK_API_UNAVAILABLE(
    badgeLabel = "ÉCHEC 503 → FALLBACK SOURCE SUIVANTE",
    isResolvedSuccess = false,
    isFailedAndCascaded = true,
    colorHex = 0xFFE53935
  ),
  FALLBACK_TIMEOUT(
    badgeLabel = "TIMEOUT 504 → FALLBACK SOURCE SUIVANTE",
    isResolvedSuccess = false,
    isFailedAndCascaded = true,
    colorHex = 0xFFFF7A00
  ),
  FALLBACK_RATE_LIMITED(
    badgeLabel = "QUOTA 429 → FALLBACK SOURCE SUIVANTE",
    isResolvedSuccess = false,
    isFailedAndCascaded = true,
    colorHex = 0xFFAB47BC
  ),
  FALLBACK_VIN_NOT_FOUND(
    badgeLabel = "VIN INTROUVABLE (404) → FALLBACK SOURCE SUIVANTE",
    isResolvedSuccess = false,
    isFailedAndCascaded = true,
    colorHex = 0xFFFFB300
  ),
  STANDBY_NOT_SOLICITED(
    badgeLabel = "EN VEILLE (NON SOLLICITÉE • RÉSOLU EN AMONT)",
    isResolvedSuccess = false,
    isFailedAndCascaded = false,
    colorHex = 0xFF78909C
  )
}

data class FallbackSourceConfig(
  val priorityOrder: Int,
  val provider: UnifiedApiConnectorProvider,
  val simulatedHealthState: ApiSourceHealthState = ApiSourceHealthState.ONLINE,
  val supportedWmiPrefixes: List<String>,
  val coverageDescription: String
)

data class FallbackAttemptStep(
  val stepOrder: Int,
  val provider: UnifiedApiConnectorProvider,
  val status: FallbackAttemptStatus,
  val httpStatusCode: Int,
  val latencyMs: Int,
  val diagnosticReason: String,
  val nextFallbackTargetName: String? = null
)

data class FallbackCascadeResolutionResult(
  val queryVinOrInput: String,
  val resolvedSuccessfully: Boolean,
  val winningProvider: UnifiedApiConnectorProvider,
  val winningStepOrder: Int,
  val fallbackHopsCount: Int,
  val totalCascadeLatencyMs: Int,
  val attemptSteps: List<FallbackAttemptStep>,
  val resolvedVehicle: Vehicle,
  val resolvedSinoRecord: SinoChineseVinRecord?,
  val cascadeSummaryBanner: String
)

data class UnifiedSingleApiExecutionTrace(
  val requestId: String,
  val timestamp: String,
  val unifiedEndpoint: String = "POST https://api.mekanik-plus.ai/v1/unified-auto-graph/resolve",
  val queryInput: String,
  val queryType: String, // "VIN_CHINE_DECODE", "GEMINI_FUNCTION_CALL", "SCENARIO_JSON_TREE"
  val totalMergedLatencyMs: Int,
  val providersQueried: List<UnifiedApiConnectorProvider>,
  val matchedVinRecord: SinoChineseVinRecord,
  val activeScenario: ReverseEngineeredDemoScenario?,
  val unifiedResponseJson: String,
  val fallbackCascade: FallbackCascadeResolutionResult
)

