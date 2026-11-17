package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.*

private enum class UnifiedHubTab(val title: String, val icon: ImageVector) {
  UNIFIED_ENGINE("API Unique IA (11-en-1)", Icons.Default.Hub),
  FALLBACK_CASCADE("Cascade Fallback (11 API)", Icons.Default.SwapVert),
  ESSENTIAL_SIX_OPTIONS("6 Options Pro (OCR • IAM • OR • DCT • QR)", Icons.Default.Verified),
  SINO_DONGCHEDI("SinoAuto360 & Dongchedi", Icons.Default.DirectionsCar),
  REVERSE_SCENARIOS("Scénarios JSON (Jetour/Geely)", Icons.Default.AccountTree),
  CAR2DB_LIFEOFCAPO("Car2DB LLM & 142 Marques", Icons.Default.Storage)
}

@Composable
fun UnifiedAutoApiHubModal(
  connectors: List<UnifiedApiConnectorProvider>,
  fallbackChainConfigs: List<FallbackSourceConfig> = emptyList(),
  sinoRecords: List<SinoChineseVinRecord>,
  demoScenarios: List<ReverseEngineeredDemoScenario>,
  lifeOfCapoBrands: List<LifeOfCapoBrandCatalogEntry>,
  activeTrace: UnifiedSingleApiExecutionTrace,
  initialTabIndex: Int = 0,
  ocrPresets: List<OcrChassisCascadePreset> = emptyList(),
  crossRefBridges: List<SinoEuroCrossReferenceBridge> = emptyList(),
  autoLearnedCacheEntries: List<AutoLearnedVinCacheEntry> = emptyList(),
  oneClickQuote: OneClickAutoQuoteBundle? = null,
  selectedQuoteTierIndex: Int = 1,
  fluidSpecs: List<FluidAndServiceResetSpec> = emptyList(),
  qrPassportSync: QrPassportCascadeSyncRecord? = null,
  onExecuteUnifiedQuery: (String, String?) -> Unit,
  onUpdateProviderHealth: (UnifiedApiConnectorProvider, ApiSourceHealthState) -> Unit = { _, _ -> },
  onMoveProviderPriorityUp: (UnifiedApiConnectorProvider) -> Unit = {},
  onSimulateOutageAndFallback: () -> Unit = {},
  onSimulateLegacyApisFallback: () -> Unit = {},
  onResetFallbackChainOnline: () -> Unit = {},
  onRunOcrToCascade: (String) -> Unit = {},
  onSelectCrossRefAndAddToQuote: (SinoEuroCrossReferenceBridge, CrossRefIamSupplierMatch) -> Unit = { _, _ -> },
  onTriggerAutoLearnCache: () -> Unit = {},
  onRecallFromOfflineRoomZeroMs: (String) -> Unit = {},
  onSelectQuoteTier: (Int) -> Unit = {},
  onCreateWorkshopOrderFromOneClickQuote: () -> Unit = {},
  onExportOneClickQuotePdf: () -> Unit = {},
  onExecuteObdResetRoutine: (FluidAndServiceResetSpec) -> Unit = {},
  onSyncAndOpenUniversalQrPassport: () -> Unit = {},
  onApplyVehicleToApp: (SinoChineseVinRecord) -> Unit,
  onLaunchScenarioInAiAndWorkshop: (ReverseEngineeredDemoScenario) -> Unit,
  onDismiss: () -> Unit
) {
  val initialTab = remember(initialTabIndex) {
    UnifiedHubTab.entries.getOrElse(initialTabIndex) { UnifiedHubTab.UNIFIED_ENGINE }
  }
  var selectedTab by remember(initialTabIndex) { mutableStateOf(initialTab) }
  var queryText by remember(activeTrace.queryInput) { mutableStateOf(activeTrace.queryInput) }
  var selectedScenario by remember { mutableStateOf(demoScenarios.first()) }
  var showChineseOnlyBrands by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .fillMaxHeight(0.92f)
        .testTag("unified_auto_api_hub_modal"),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(
        1.5.dp,
        Brush.horizontalGradient(listOf(TechCyan, MechanicalOrange, PerformanceRed))
      ),
      tonalElevation = 14.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                  Brush.linearGradient(
                    listOf(TechCyanDark, PerformanceRedDark)
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Hub,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "MEKANIK-ONE • API UNIQUE & FALLBACK AUTO",
                  style = MaterialTheme.typography.titleMedium.copy(
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.5.sp
                  )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = TechCyan.copy(alpha = 0.2f),
                  border = BorderStroke(0.8.dp, TechCyan)
                ) {
                  Text(
                    text = "11 API + 6 OPTIONS PRO",
                    color = TechCyan,
                    fontWeight = FontWeight.Black,
                    fontSize = 8.5.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
              Text(
                text = "SinoAuto360 → Dongchedi → Car2DB → ChineseCar.parts → ServiceBox/Dialogys/ETKA → TecDoc → HaynesPro → 7Zap → NHTSA vPIC → lifeofcapo → Cache Room/JSON",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumLight,
                  fontSize = 9.5.sp
                )
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("btn_close_unified_auto_api_modal")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumGray)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Navigation Tabs
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(UnifiedHubTab.entries) { tab ->
            val isSelected = selectedTab == tab
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) TechCyan.copy(alpha = 0.18f) else MetalSurface,
              border = BorderStroke(
                1.dp,
                if (isSelected) TechCyan else MetalBorder
              ),
              modifier = Modifier
                .clickable { selectedTab = tab }
                .testTag("tab_unified_${tab.name.lowercase()}")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  tab.icon,
                  contentDescription = null,
                  tint = if (isSelected) TechCyan else AluminumLight,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = tab.title,
                  color = if (isSelected) PureWhite else AluminumLight,
                  fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                  fontSize = 11.5.sp
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
          when (selectedTab) {
            UnifiedHubTab.UNIFIED_ENGINE -> UnifiedEngineAggregatorTab(
              connectors = connectors,
              sinoRecords = sinoRecords,
              activeTrace = activeTrace,
              queryText = queryText,
              onQueryTextChange = { queryText = it },
              onExecuteQuery = { q -> onExecuteUnifiedQuery(q, null) },
              onSimulateOutageAndFallback = onSimulateOutageAndFallback,
              onResetFallbackChainOnline = onResetFallbackChainOnline,
              onOpenFallbackTab = { selectedTab = UnifiedHubTab.FALLBACK_CASCADE },
              onApplyVehicle = onApplyVehicleToApp,
              onOpenScenarioTab = {
                selectedTab = UnifiedHubTab.REVERSE_SCENARIOS
              }
            )
            UnifiedHubTab.FALLBACK_CASCADE -> FallbackCascadeInteractiveTab(
              fallbackChainConfigs = fallbackChainConfigs,
              activeTrace = activeTrace,
              onExecuteQuery = { q ->
                queryText = q
                onExecuteUnifiedQuery(q, null)
              },
              onUpdateProviderHealth = onUpdateProviderHealth,
              onMoveProviderPriorityUp = onMoveProviderPriorityUp,
              onSimulateOutageAndFallback = onSimulateOutageAndFallback,
              onSimulateLegacyApisFallback = onSimulateLegacyApisFallback,
              onResetFallbackChainOnline = onResetFallbackChainOnline
            )
            UnifiedHubTab.ESSENTIAL_SIX_OPTIONS -> {
              if (oneClickQuote != null && qrPassportSync != null) {
                EssentialSixOptionsInteractiveTab(
                  activeTrace = activeTrace,
                  ocrPresets = ocrPresets,
                  crossRefBridges = crossRefBridges,
                  autoLearnedCacheEntries = autoLearnedCacheEntries,
                  oneClickQuote = oneClickQuote,
                  selectedTierIndex = selectedQuoteTierIndex,
                  fluidSpecs = fluidSpecs,
                  qrPassportSync = qrPassportSync,
                  onRunOcrToCascade = { rawOcr ->
                    onRunOcrToCascade(rawOcr)
                  },
                  onSelectCrossRefAndAddToQuote = onSelectCrossRefAndAddToQuote,
                  onTriggerAutoLearnCache = onTriggerAutoLearnCache,
                  onRecallFromOfflineRoomZeroMs = onRecallFromOfflineRoomZeroMs,
                  onSelectQuoteTier = onSelectQuoteTier,
                  onCreateWorkshopOrderFromOneClickQuote = onCreateWorkshopOrderFromOneClickQuote,
                  onExportOneClickQuotePdf = onExportOneClickQuotePdf,
                  onExecuteObdResetRoutine = onExecuteObdResetRoutine,
                  onSyncAndOpenUniversalQrPassport = onSyncAndOpenUniversalQrPassport
                )
              }
            }
            UnifiedHubTab.SINO_DONGCHEDI -> SinoAuto360DongchediTab(
              sinoRecords = sinoRecords,
              onSelectAndDecode = { rec ->
                onExecuteUnifiedQuery(rec.vin, null)
              },
              onApplyVehicleToApp = onApplyVehicleToApp
            )
            UnifiedHubTab.REVERSE_SCENARIOS -> ReverseEngineeredScenariosTab(
              scenarios = demoScenarios,
              selectedScenario = selectedScenario,
              onSelectScenario = { sc ->
                selectedScenario = sc
                onExecuteUnifiedQuery(sc.vinExample, sc.id)
              },
              onLaunchInAiAndWorkshop = onLaunchScenarioInAiAndWorkshop
            )
            UnifiedHubTab.CAR2DB_LIFEOFCAPO -> Car2DbAndLifeOfCapoTab(
              brands = lifeOfCapoBrands,
              sinoRecords = sinoRecords,
              showChineseOnly = showChineseOnlyBrands,
              onToggleChineseOnly = { showChineseOnlyBrands = it },
              onTestBrandInUnifiedApi = { brandName ->
                queryText = brandName
                onExecuteUnifiedQuery(brandName, null)
                selectedTab = UnifiedHubTab.UNIFIED_ENGINE
              }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun UnifiedEngineAggregatorTab(
  connectors: List<UnifiedApiConnectorProvider>,
  sinoRecords: List<SinoChineseVinRecord>,
  activeTrace: UnifiedSingleApiExecutionTrace,
  queryText: String,
  onQueryTextChange: (String) -> Unit,
  onExecuteQuery: (String) -> Unit,
  onSimulateOutageAndFallback: () -> Unit,
  onResetFallbackChainOnline: () -> Unit,
  onOpenFallbackTab: () -> Unit,
  onApplyVehicle: (SinoChineseVinRecord) -> Unit,
  onOpenScenarioTab: () -> Unit
) {
  val cascade = activeTrace.fallbackCascade

  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    // Unified Endpoint Banner
    item {
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = GraphiteBlack,
        border = BorderStroke(1.2.dp, TechCyan.copy(alpha = 0.7f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = TechCyan,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "POINT D'ENTRÉE UNIQUE : UNIFIED AUTO-GRAPH API",
                color = TechCyan,
                fontWeight = FontWeight.Black,
                fontSize = 11.5.sp
              )
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (cascade.fallbackHopsCount > 0) MechanicalOrange.copy(alpha = 0.2f) else ElectricGreen.copy(alpha = 0.18f),
              border = BorderStroke(0.8.dp, if (cascade.fallbackHopsCount > 0) MechanicalOrange else ElectricGreen)
            ) {
              Text(
                text = if (cascade.fallbackHopsCount > 0) {
                  "FALLBACK #${cascade.winningStepOrder} • ${cascade.totalCascadeLatencyMs} ms"
                } else {
                  "DIRECT #1 • ${cascade.totalCascadeLatencyMs} ms"
                },
                color = if (cascade.fallbackHopsCount > 0) MechanicalOrange else ElectricGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 9.5.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = activeTrace.unifiedEndpoint,
            color = MechanicalOrange,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Mécanisme de Fallback Automatique actif : si la 1ère API (ex: SinoAuto360) est indisponible (503/504) ou ne trouve pas le VIN (404), le Repository interroge automatiquement la source suivante dans la liste (Dongchedi → Car2DB LLM → ChineseCar.parts/CatCar → lifeofcapo 142 marques → 7Zap/TecDoc).",
            color = AluminumLight,
            fontSize = 11.sp,
            lineHeight = 15.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Search / VIN Input Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = queryText,
              onValueChange = onQueryTextChange,
              modifier = Modifier
                .weight(1f)
                .testTag("input_unified_api_query"),
              singleLine = true,
              label = {
                Text(
                  "VIN Chinois / Européen, Modèle ou Code Moteur (ex: LVVDB21B9PD048912)",
                  fontSize = 10.5.sp
                )
              },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TechCyan,
                unfocusedBorderColor = MetalBorder,
                focusedTextColor = PureWhite,
                unfocusedTextColor = PureWhite
              )
            )

            Button(
              onClick = { onExecuteQuery(queryText) },
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .height(52.dp)
                .testTag("btn_execute_unified_api")
            ) {
              Icon(Icons.Default.Bolt, contentDescription = null, tint = PureWhite, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("EXÉCUTER", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 11.sp)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Quick VIN Preset Chips
          Text(
            text = "Tester un décodage VIN & la chaîne de Fallback automatique :",
            color = AluminumGray,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(6.dp))
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(sinoRecords) { rec ->
              val isCurrent = activeTrace.matchedVinRecord.id == rec.id
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isCurrent) MechanicalOrange.copy(alpha = 0.22f) else MetalCard,
                border = BorderStroke(
                  1.dp,
                  if (isCurrent) MechanicalOrange else MetalBorder
                ),
                modifier = Modifier.clickable {
                  onQueryTextChange(rec.vin)
                  onExecuteQuery(rec.vin)
                }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "${rec.brand} ${rec.model.substringBefore(" ")} (${rec.vin.take(3)}...)",
                    color = if (isCurrent) MechanicalOrange else PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                  )
                }
              }
            }
            item {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = TechCyan.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, TechCyan),
                modifier = Modifier.clickable {
                  onQueryTextChange("VF3FPHNSSMY001234")
                  onExecuteQuery("VF3FPHNSSMY001234")
                }
              ) {
                Text(
                  text = "🇫🇷 Peugeot 308 (VF3... → Fallback #3 Car2DB)",
                  color = TechCyan,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.5.sp,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }
      }
    }

    // Live Fallback Cascade Trace Card
    item {
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = MetalCard,
        border = BorderStroke(
          1.2.dp,
          if (cascade.fallbackHopsCount > 0) MechanicalOrange else ElectricGreen.copy(alpha = 0.7f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Icon(
                Icons.Default.SwapVert,
                contentDescription = null,
                tint = if (cascade.fallbackHopsCount > 0) MechanicalOrange else ElectricGreen,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "TRACE D'EXÉCUTION DE LA CHAÎNE DE FALLBACK REPOSITORY",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 11.5.sp
              )
            }
            TextButton(onClick = onOpenFallbackTab) {
              Text(
                text = "Configurer Fallback →",
                color = TechCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp
              )
            }
          }

          Text(
            text = cascade.cascadeSummaryBanner,
            color = if (cascade.fallbackHopsCount > 0) MechanicalOrange else ElectricGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Quick Chaos Outage Simulation Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = onSimulateOutageAndFallback,
              colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange.copy(alpha = 0.25f)),
              border = BorderStroke(1.dp, MechanicalOrange),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("btn_simulate_fallback_outage")
            ) {
              Icon(Icons.Default.WarningAmber, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = "Simuler Panne #1 & #2 (Test Fallback)",
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            }

            OutlinedButton(
              onClick = onResetFallbackChainOnline,
              border = BorderStroke(1.dp, ElectricGreen),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("btn_reset_fallback_online")
            ) {
              Icon(Icons.Default.Refresh, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = "Rétablir 6 API Online (200 OK)",
                color = ElectricGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Step-by-step cascade rows
          cascade.attemptSteps.forEach { step ->
            val stepColor = Color(step.status.colorHex)
            Surface(
              shape = RoundedCornerShape(9.dp),
              color = GraphiteBlack,
              border = BorderStroke(
                width = if (step.status.isResolvedSuccess) 1.4.dp else 0.8.dp,
                color = stepColor.copy(alpha = if (step.status == FallbackAttemptStatus.STANDBY_NOT_SOLICITED) 0.35f else 0.85f)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                      shape = CircleShape,
                      color = stepColor.copy(alpha = 0.22f),
                      border = BorderStroke(1.dp, stepColor)
                    ) {
                      Text(
                        text = "#${step.stepOrder}",
                        color = stepColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                      )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = step.provider.displayName,
                      color = if (step.status == FallbackAttemptStatus.STANDBY_NOT_SOLICITED) AluminumGray else PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.5.sp
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(5.dp),
                    color = stepColor.copy(alpha = 0.18f),
                    border = BorderStroke(0.8.dp, stepColor)
                  ) {
                    Text(
                      text = if (step.httpStatusCode > 0) "${step.status.badgeLabel} • ${step.latencyMs}ms" else step.status.badgeLabel,
                      color = stepColor,
                      fontWeight = FontWeight.Bold,
                      fontSize = 8.5.sp,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = step.diagnosticReason,
                  color = AluminumLight,
                  fontSize = 10.sp,
                  lineHeight = 13.5.sp
                )
                if (step.nextFallbackTargetName != null) {
                  Spacer(modifier = Modifier.height(3.dp))
                  Text(
                    text = "↳ Bascule automatique immédiate vers : ${step.nextFallbackTargetName}",
                    color = MechanicalOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  )
                }
              }
            }
          }
        }
      }
    }

    // Live Unified Result Card for Matched Vehicle
    item {
      val rec = activeTrace.matchedVinRecord
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = GraphiteBlack,
        border = BorderStroke(1.5.dp, MechanicalOrange),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "RÉSULTAT UNIFIÉ : ${rec.brand.uppercase()} ${rec.model.uppercase()}",
                color = MechanicalOrange,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
              )
              Text(
                text = "VIN : ${rec.vin} • ${rec.chineseDomesticName} • Moteur : ${rec.engineCode}",
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = PerformanceRed.copy(alpha = 0.2f),
              border = BorderStroke(1.dp, PerformanceRed)
            ) {
              Text(
                text = "${rec.oemPartsCatalog.size} PIÈCES OEM",
                color = PerformanceRedGlow,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Car2DB Breadcrumb Box
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MetalSurface,
            border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "🧭 FIL D'ARIANE CAR2DB (OPTIMISÉ LLM / GEMINI) :",
                color = TechCyan,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = rec.car2DbBreadcrumb,
                color = PureWhite,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = rec.car2DbLlmSummary,
                color = AluminumLight,
                fontSize = 10.5.sp,
                lineHeight = 14.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // JSON Payload Preview
          Text(
            text = "PAYLOAD JSON UNIFIÉ (SinoAuto360 + Dongchedi + Car2DB + ChineseCar.parts) :",
            color = AluminumLight,
            fontWeight = FontWeight.Bold,
            fontSize = 10.5.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF0A0D10),
            border = BorderStroke(1.dp, MetalBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = activeTrace.unifiedResponseJson,
              color = ElectricGreen,
              fontFamily = FontFamily.Monospace,
              fontSize = 9.5.sp,
              lineHeight = 13.sp,
              modifier = Modifier.padding(10.dp)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { onApplyVehicle(rec) },
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("btn_apply_unified_vehicle")
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "ACTIVER CE VÉHICULE & SES PIÈCES OEM",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 10.5.sp
              )
            }

            OutlinedButton(
              onClick = onOpenScenarioTab,
              border = BorderStroke(1.dp, TechCyan),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.Psychology, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "VOIR SCÉNARIOS DÉMO IA",
                color = TechCyan,
                fontWeight = FontWeight.Black,
                fontSize = 10.5.sp
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SinoAuto360DongchediTab(
  sinoRecords: List<SinoChineseVinRecord>,
  onSelectAndDecode: (SinoChineseVinRecord) -> Unit,
  onApplyVehicleToApp: (SinoChineseVinRecord) -> Unit
) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MetalCard,
        border = BorderStroke(1.dp, PerformanceRed),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = "SINOAUTO360 API + AUTO-API (DONGCHEDI) — 50+ MARQUES CHINOISES",
            color = PureWhite,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Décodage VIN spécifique aux usines chinoises (WMI LVV Chery/Jetour, L6T Geely, LC0 BYD, LS4 Changan, LFV FAW-VAG) couplé à l'extraction JSON propre de Dongchedi pour Gemini Function Calling.",
            color = AluminumLight,
            fontSize = 11.sp
          )
        }
      }
    }

    items(sinoRecords) { record ->
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = GraphiteBlack,
        border = BorderStroke(1.dp, MetalBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = RoundedCornerShape(5.dp),
                  color = PerformanceRed.copy(alpha = 0.2f),
                  border = BorderStroke(0.8.dp, PerformanceRed)
                ) {
                  Text(
                    text = record.brand.uppercase(),
                    color = PerformanceRedGlow,
                    fontWeight = FontWeight.Black,
                    fontSize = 9.5.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = record.model,
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 13.sp
                )
              }
              Spacer(modifier = Modifier.height(3.dp))
              Text(
                text = "VIN : ${record.vin} • ${record.wmiPrefix}",
                color = TechCyan,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "• Moteur : ${record.engineCode} (${record.engineFamilyName}) — ${record.displacementAndPower}",
            color = PureWhite,
            fontSize = 11.sp
          )
          Text(
            text = "• Transmission : ${record.transmissionCode} • Finition Dongchedi : ${record.trimFinishDongchedi}",
            color = AluminumLight,
            fontSize = 10.5.sp
          )
          Text(
            text = "• Usine d'assemblage : ${record.assemblyPlant}",
            color = AluminumGray,
            fontSize = 10.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "PIÈCES DE RECHANGE OEM EXTRAITES (RESTful JSON) :",
            color = MechanicalOrange,
            fontWeight = FontWeight.Black,
            fontSize = 10.5.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          record.oemPartsCatalog.forEach { part ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MetalSurface,
              border = BorderStroke(0.8.dp, MetalBorder),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "${part.calloutRef} ${part.partName}",
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.weight(1f)
                  )
                  Surface(
                    shape = RoundedCornerShape(5.dp),
                    color = PerformanceRed.copy(alpha = 0.2f),
                    border = BorderStroke(0.8.dp, PerformanceRed)
                  ) {
                    Text(
                      text = "OEM ${part.oemPartNumber}",
                      color = PerformanceRedGlow,
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black,
                      fontSize = 10.sp,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                  text = "Équivalences : ${part.crossReferences.joinToString(" • ")}",
                  color = TechCyan,
                  fontSize = 10.sp
                )
                Text(
                  text = "${part.technicalDimensions} • ${part.torqueSpecsNm}",
                  color = AluminumLight,
                  fontSize = 9.5.sp
                )
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = part.stockAvailability,
                    color = ElectricGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp
                  )
                  Text(
                    text = "${part.estimatedPriceDzd} DZD",
                    color = MechanicalOrange,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.5.sp
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { onApplyVehicleToApp(record) },
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text(
                text = "CHARGER DANS LE CATALOGUE PIÈCES",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp
              )
            }
            OutlinedButton(
              onClick = { onSelectAndDecode(record) },
              border = BorderStroke(1.dp, TechCyan),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "VOIR JSON FN-CALL",
                color = TechCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ReverseEngineeredScenariosTab(
  scenarios: List<ReverseEngineeredDemoScenario>,
  selectedScenario: ReverseEngineeredDemoScenario,
  onSelectScenario: (ReverseEngineeredDemoScenario) -> Unit,
  onLaunchInAiAndWorkshop: (ReverseEngineeredDemoScenario) -> Unit
) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Text(
        text = "SCÉNARIOS ULTRA-PRÉCIS POUR DÉMO INVESTISSEURS (ChineseCar.parts + CatCar.info + Gemini)",
        color = PureWhite,
        fontWeight = FontWeight.Black,
        fontSize = 12.sp
      )
      Spacer(modifier = Modifier.height(6.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(scenarios) { sc ->
          val isSelected = sc.id == selectedScenario.id
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) PerformanceRed.copy(alpha = 0.22f) else MetalSurface,
            border = BorderStroke(1.dp, if (isSelected) PerformanceRed else MetalBorder),
            modifier = Modifier
              .clickable { onSelectScenario(sc) }
              .testTag("scenario_chip_${sc.id}")
          ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
              Text(
                text = "${sc.brand} ${sc.model.substringBefore(" ")}",
                color = if (isSelected) PureWhite else AluminumLight,
                fontWeight = FontWeight.Black,
                fontSize = 11.5.sp
              )
              Text(
                text = sc.vinExample,
                color = if (isSelected) MechanicalOrange else AluminumGray,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.5.sp
              )
            }
          }
        }
      }
    }

    item {
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = GraphiteBlack,
        border = BorderStroke(1.5.dp, TechCyan),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Surface(
            shape = RoundedCornerShape(5.dp),
            color = MechanicalOrange.copy(alpha = 0.2f),
            border = BorderStroke(0.8.dp, MechanicalOrange)
          ) {
            Text(
              text = selectedScenario.badgeLabel,
              color = MechanicalOrange,
              fontWeight = FontWeight.Black,
              fontSize = 9.5.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = selectedScenario.title,
            color = PureWhite,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp
          )
          Text(
            text = selectedScenario.subtitle,
            color = AluminumLight,
            fontSize = 11.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Car2DB Breadcrumb & Digest
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MetalSurface,
            border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "FIL D'ARIANE CAR2DB : ${selectedScenario.car2DbBreadcrumb}",
                color = TechCyan,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = selectedScenario.car2DbLlmDigest,
                color = PureWhite,
                fontSize = 10.5.sp,
                lineHeight = 14.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // OBD / EPB Warning
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = PerformanceRedDark.copy(alpha = 0.28f),
            border = BorderStroke(1.dp, PerformanceRed),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = selectedScenario.specialToolOrObdResetNote,
              color = PureWhite,
              fontWeight = FontWeight.SemiBold,
              fontSize = 10.5.sp,
              modifier = Modifier.padding(10.dp)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Step-by-Step Procedure
          Text(
            text = "PROCÉDURE D'ATELIER GÉNÉRÉE PAR L'IA & COUPLES DE SERRAGE OEM :",
            color = MechanicalOrange,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          selectedScenario.stepByStepMechanicalProcedure.forEach { step ->
            Text(
              text = step,
              color = OffWhite,
              fontSize = 10.5.sp,
              lineHeight = 14.5.sp,
              modifier = Modifier.padding(vertical = 2.dp)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Raw JSON Tree Context
          Text(
            text = "ARBORESCENCE JSON CONTEXTUELLE (ChineseCar.parts / SinoAuto360 / Dongchedi) :",
            color = TechCyan,
            fontWeight = FontWeight.Black,
            fontSize = 10.5.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF0A0D10),
            border = BorderStroke(1.dp, MetalBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = selectedScenario.rawJsonTreeContext,
              color = ElectricGreen,
              fontFamily = FontFamily.Monospace,
              fontSize = 9.5.sp,
              lineHeight = 13.sp,
              modifier = Modifier.padding(10.dp)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = { onLaunchInAiAndWorkshop(selectedScenario) },
            colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("btn_launch_demo_scenario_ai")
          ) {
            Icon(Icons.Default.Psychology, contentDescription = null, tint = PureWhite, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "INJECTER CE SCÉNARIO JSON DANS MEKANIK-AI & CHARGER LES PIÈCES",
              color = PureWhite,
              fontWeight = FontWeight.Black,
              fontSize = 11.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun Car2DbAndLifeOfCapoTab(
  brands: List<LifeOfCapoBrandCatalogEntry>,
  sinoRecords: List<SinoChineseVinRecord>,
  showChineseOnly: Boolean,
  onToggleChineseOnly: (Boolean) -> Unit,
  onTestBrandInUnifiedApi: (String) -> Unit
) {
  val filteredBrands = remember(brands, showChineseOnly) {
    if (showChineseOnly) brands.filter { it.isChineseHighlight } else brands
  }

  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MetalCard,
        border = BorderStroke(1.dp, ElectricGreen),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "GITHUB lifeofcapo/car-api (142 MARQUES LOCALES) + CAR2DB LLM",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp
              )
              Text(
                text = "Structure JSON/TypeScript enregistrée localement (Geely, Chery/Jetour, BYD, Changan, FAW-VAG + marques mondiales) couplée aux fils d'Ariane Car2DB.",
                color = AluminumLight,
                fontSize = 10.5.sp
              )
            }
            FilterChip(
              selected = showChineseOnly,
              onClick = { onToggleChineseOnly(!showChineseOnly) },
              label = { Text("Focus Chine 🇨🇳", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
            )
          }
        }
      }
    }

    items(filteredBrands) { brand ->
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = GraphiteBlack,
        border = BorderStroke(
          1.dp,
          if (brand.isChineseHighlight) MechanicalOrange.copy(alpha = 0.7f) else MetalBorder
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "${brand.brandName} • ${brand.originCountry}",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
              )
              Text(
                text = "Groupe : ${brand.parentHolding} • ${brand.modelsCount} modèles • ${brand.generationsCount} générations indexées",
                color = AluminumLight,
                fontSize = 10.5.sp
              )
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = ElectricGreen.copy(alpha = 0.15f),
              border = BorderStroke(0.8.dp, ElectricGreen)
            ) {
              Text(
                text = "JSON LOCAL (${brand.localJsonSizeKb} KB)",
                color = ElectricGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Modèles phares : ${brand.flagshipModels.joinToString(", ")}",
            color = OffWhite,
            fontSize = 10.5.sp
          )
          Text(
            text = "Moteurs indexés : ${brand.engineCodesIndexed.joinToString(" • ")}",
            color = TechCyan,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedButton(
            onClick = { onTestBrandInUnifiedApi(brand.brandName) },
            border = BorderStroke(1.dp, MechanicalOrange),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "INTERROGER ${brand.brandName.uppercase()} VIA L'API UNIQUE",
              color = MechanicalOrange,
              fontWeight = FontWeight.Black,
              fontSize = 10.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun FallbackCascadeInteractiveTab(
  fallbackChainConfigs: List<FallbackSourceConfig>,
  activeTrace: UnifiedSingleApiExecutionTrace,
  onExecuteQuery: (String) -> Unit,
  onUpdateProviderHealth: (UnifiedApiConnectorProvider, ApiSourceHealthState) -> Unit,
  onMoveProviderPriorityUp: (UnifiedApiConnectorProvider) -> Unit,
  onSimulateOutageAndFallback: () -> Unit,
  onSimulateLegacyApisFallback: () -> Unit,
  onResetFallbackChainOnline: () -> Unit
) {
  val cascade = activeTrace.fallbackCascade
  val orderedConfigs = remember(fallbackChainConfigs) {
    fallbackChainConfigs.sortedBy { it.priorityOrder }
  }

  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    // Header & Simulation Presets
    item {
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = GraphiteBlack,
        border = BorderStroke(1.2.dp, MechanicalOrange),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "CHAÎNE DE FALLBACK AUTOMATIQUE GLOBALE (11 SOURCES API)",
                color = MechanicalOrange,
                fontWeight = FontWeight.Black,
                fontSize = 12.5.sp
              )
              Text(
                text = "Inclut les 5 nouvelles APIs Chine/IA ET les 6 APIs historiques d'avant (ServiceBox/Dialogys/ETKA, TecDoc Alliance, HaynesPro/Autodata, 7Zap/PartsSouq, NHTSA vPIC, Cache Local Room/JSON).",
                color = AluminumLight,
                fontSize = 10.5.sp,
                lineHeight = 14.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Current Resolution Banner
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (cascade.fallbackHopsCount > 0) MechanicalOrange.copy(alpha = 0.16f) else ElectricGreen.copy(alpha = 0.16f),
            border = BorderStroke(1.dp, if (cascade.fallbackHopsCount > 0) MechanicalOrange else ElectricGreen),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "REQUÊTE ACTIVE : ${cascade.queryVinOrInput}",
                color = PureWhite,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = cascade.cascadeSummaryBanner,
                color = if (cascade.fallbackHopsCount > 0) MechanicalOrange else ElectricGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp
              )
              Text(
                text = "Véhicule résolu : ${cascade.resolvedVehicle.brand} ${cascade.resolvedVehicle.model} • Moteur ${cascade.resolvedVehicle.engineCode}",
                color = OffWhite,
                fontSize = 10.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "SCÉNARIOS DE TEST FALLBACK EN 1 CLIC :",
            color = TechCyan,
            fontWeight = FontWeight.Black,
            fontSize = 10.5.sp
          )
          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = onSimulateOutageAndFallback,
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text(
                text = "🚨 Simuler Panne #1 (503) & #2 (Timeout)",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp
              )
            }

            OutlinedButton(
              onClick = onResetFallbackChainOnline,
              border = BorderStroke(1.dp, ElectricGreen),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text(
                text = "✅ Rétablir les 11 API en ligne (200)",
                color = ElectricGreen,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Button(
            onClick = onSimulateLegacyApisFallback,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D5AFE)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.History, contentDescription = null, tint = PureWhite, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "🔄 Tester Bascule vers APIs d'Avant (#5 ServiceBox/Dialogys/ETKA, #6 TecDoc, #7 HaynesPro, #9 NHTSA)",
              color = PureWhite,
              fontWeight = FontWeight.Black,
              fontSize = 9.5.sp
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            OutlinedButton(
              onClick = { onExecuteQuery("LVVDB21B9PD048912") },
              border = BorderStroke(1.dp, TechCyan),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f),
              contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
            ) {
              Text("🇨🇳 Jetour (LVV... → #1)", color = TechCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
              onClick = { onExecuteQuery("VF3FPHNSSMY001234") },
              border = BorderStroke(1.dp, MechanicalOrange),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f),
              contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
            ) {
              Text("🇫🇷 Peugeot (VF3... → ServiceBox/Car2DB)", color = MechanicalOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
              onClick = { onExecuteQuery("5YJ3E1EB7NF998877") },
              border = BorderStroke(1.dp, ElectricGreen),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f),
              contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
            ) {
              Text("🇺🇸 Tesla (5YJ... → HaynesPro/NHTSA)", color = ElectricGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Interactive Priority & Health Configuration for Each API Source
    item {
      Text(
        text = "ORDRE DE PRIORITÉ & ÉTAT SIMULÉ DES 11 SOURCES DU REPOSITORY (NOUVELLES + HISTORIQUES) :",
        color = PureWhite,
        fontWeight = FontWeight.Black,
        fontSize = 11.5.sp
      )
    }

    items(orderedConfigs) { cfg ->
      val stepResult = cascade.attemptSteps.find { it.provider == cfg.provider }
      val healthColor = Color(cfg.simulatedHealthState.colorHex)
      val genBadgeColor = if (cfg.provider.isHistoricalMekanikApi) Color(0xFF3D5AFE) else TechCyan

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MetalCard,
        border = BorderStroke(
          width = if (stepResult?.status?.isResolvedSuccess == true) 1.5.dp else 1.dp,
          color = if (stepResult?.status?.isResolvedSuccess == true) ElectricGreen else healthColor.copy(alpha = 0.7f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = TechCyan.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, TechCyan)
              ) {
                Text(
                  text = "PRIORITÉ #${cfg.priorityOrder}",
                  color = TechCyan,
                  fontWeight = FontWeight.Black,
                  fontSize = 9.5.sp,
                  modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = genBadgeColor.copy(alpha = 0.22f),
                border = BorderStroke(0.8.dp, genBadgeColor)
              ) {
                Text(
                  text = cfg.provider.generationLabel,
                  color = PureWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 8.sp,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = cfg.provider.displayName,
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp
                )
                Text(
                  text = cfg.provider.endpointPattern,
                  color = AluminumGray,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.sp
                )
              }
            }

            if (cfg.priorityOrder > 1) {
              OutlinedButton(
                onClick = { onMoveProviderPriorityUp(cfg.provider) },
                border = BorderStroke(0.8.dp, TechCyan),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(30.dp)
              ) {
                Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = TechCyan, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text("Priorité +", color = TechCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = cfg.coverageDescription,
            color = AluminumLight,
            fontSize = 10.sp
          )

          if (stepResult != null) {
            Spacer(modifier = Modifier.height(6.dp))
            val stepColor = Color(stepResult.status.colorHex)
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = stepColor.copy(alpha = 0.14f),
              border = BorderStroke(0.8.dp, stepColor),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Text(
                  text = "Dernier essai : ${stepResult.status.badgeLabel} (${stepResult.latencyMs} ms)",
                  color = stepColor,
                  fontWeight = FontWeight.Black,
                  fontSize = 9.5.sp
                )
                Text(
                  text = stepResult.diagnosticReason,
                  color = PureWhite,
                  fontSize = 9.5.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Simuler l'état de cette source API :",
            color = AluminumGray,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(4.dp))

          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(ApiSourceHealthState.entries) { stateOption ->
              val isSelected = cfg.simulatedHealthState == stateOption
              val optionColor = Color(stateOption.colorHex)
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isSelected) optionColor.copy(alpha = 0.24f) else GraphiteBlack,
                border = BorderStroke(1.dp, if (isSelected) optionColor else MetalBorder),
                modifier = Modifier.clickable {
                  onUpdateProviderHealth(cfg.provider, stateOption)
                }
              ) {
                Text(
                  text = stateOption.shortBadge,
                  color = if (isSelected) optionColor else AluminumLight,
                  fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                  fontSize = 9.5.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}

