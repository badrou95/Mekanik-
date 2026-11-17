package com.example.ui.components

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
import com.example.data.model.*
import com.example.ui.theme.*

private enum class EssentialOptionSubTab(
  val number: Int,
  val shortTitle: String,
  val icon: ImageVector,
  val accentColor: Color
) {
  OCR_CHASSIS_BRIDGE(1, "1. OCR → Cascade 11 API", Icons.Default.DocumentScanner, TechCyan),
  SINO_EURO_CROSS_REF(2, "2. Pont Chine ↔ Europe (IAM)", Icons.Default.CompareArrows, MechanicalOrange),
  ROOM_AUTO_LEARNING(3, "3. Cache Room Auto-Appris (0ms)", Icons.Default.Memory, ElectricGreen),
  ONE_CLICK_OR_QUOTE(4, "4. Devis / OR Auto 1-Clic (DZD)", Icons.Default.ReceiptLong, PerformanceRed),
  FLUIDS_DCT_RESET(5, "5. Huiles DCT & Reset Service", Icons.Default.OilBarrel, WarningAmber),
  QR_PASSPORT_SYNC(6, "6. Pont Passeport QR Universel", Icons.Default.QrCode2, Color(0xFFAB47BC))
}

@Composable
fun EssentialSixOptionsInteractiveTab(
  activeTrace: UnifiedSingleApiExecutionTrace,
  ocrPresets: List<OcrChassisCascadePreset>,
  crossRefBridges: List<SinoEuroCrossReferenceBridge>,
  autoLearnedCacheEntries: List<AutoLearnedVinCacheEntry>,
  oneClickQuote: OneClickAutoQuoteBundle,
  selectedTierIndex: Int,
  fluidSpecs: List<FluidAndServiceResetSpec>,
  qrPassportSync: QrPassportCascadeSyncRecord,
  onRunOcrToCascade: (String) -> Unit,
  onSelectCrossRefAndAddToQuote: (SinoEuroCrossReferenceBridge, CrossRefIamSupplierMatch) -> Unit,
  onTriggerAutoLearnCache: () -> Unit,
  onRecallFromOfflineRoomZeroMs: (String) -> Unit,
  onSelectQuoteTier: (Int) -> Unit,
  onCreateWorkshopOrderFromOneClickQuote: () -> Unit,
  onExportOneClickQuotePdf: () -> Unit,
  onExecuteObdResetRoutine: (FluidAndServiceResetSpec) -> Unit,
  onSyncAndOpenUniversalQrPassport: () -> Unit
) {
  var selectedSubTab by remember { mutableStateOf(EssentialOptionSubTab.OCR_CHASSIS_BRIDGE) }
  var selectedOcrPreset by remember(ocrPresets) { mutableStateOf(ocrPresets.first()) }
  var customNoisyOcrInput by remember(selectedOcrPreset) { mutableStateOf(selectedOcrPreset.rawNoisyOcrText) }
  var selectedCrossRef by remember(crossRefBridges) { mutableStateOf(crossRefBridges.first()) }
  var selectedFluidSpec by remember(fluidSpecs) { mutableStateOf(fluidSpecs.first()) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("tab_essential_six_options_content"),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Banner Header
    item {
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = MetalCard,
        border = BorderStroke(
          1.2.dp,
          Brush.horizontalGradient(listOf(TechCyan, ElectricGreen, MechanicalOrange, PerformanceRed))
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Verified,
                contentDescription = null,
                tint = ElectricGreen,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "SUITE INDUSTRIELLE : LES 6 OPTIONS INDISPENSABLES",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 12.5.sp
              )
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = ElectricGreen.copy(alpha = 0.18f),
              border = BorderStroke(0.8.dp, ElectricGreen)
            ) {
              Text(
                text = "6/6 ACTIVES",
                color = ElectricGreen,
                fontWeight = FontWeight.Black,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Text(
            text = "Connectées en temps réel à la Cascade 11 API (${activeTrace.matchedVinRecord.brand} ${activeTrace.matchedVinRecord.model} • VIN ${activeTrace.matchedVinRecord.vin}). Sélectionnez l'une des 6 briques ci-dessous :",
            color = AluminumLight,
            fontSize = 11.sp
          )

          // Sub-Tabs Selector for the 6 options
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(EssentialOptionSubTab.entries) { subTab ->
              val isSelected = selectedSubTab == subTab
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) subTab.accentColor.copy(alpha = 0.22f) else GraphiteBlack,
                border = BorderStroke(1.dp, if (isSelected) subTab.accentColor else MetalBorder),
                modifier = Modifier
                  .clickable { selectedSubTab = subTab }
                  .testTag("subtab_option_${subTab.number}")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    subTab.icon,
                    contentDescription = null,
                    tint = if (isSelected) subTab.accentColor else AluminumLight,
                    modifier = Modifier.size(15.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = subTab.shortTitle,
                    color = if (isSelected) PureWhite else AluminumLight,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                    fontSize = 11.sp
                  )
                }
              }
            }
          }
        }
      }
    }

    // Dynamic Content per Option (1 to 6)
    item {
      when (selectedSubTab) {
        // ====================================================================
        // OPTION 1 : PONT PHOTO PLAQUE CHÂSSIS / CARTE GRISE (OCR) → CASCADE 11 API
        // ====================================================================
        EssentialOptionSubTab.OCR_CHASSIS_BRIDGE -> {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MetalCard,
            border = BorderStroke(1.dp, TechCyan),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = TechCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "1. PONT PHOTO PARE-BRISE / CARTE GRISE DZ (OCR HORS-LIGNE) → CASCADE 11 API",
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                  )
                  Text(
                    text = "Corrige automatiquement les erreurs de lecture optique (O→0, I→1, Q→0, reflets vitre) avant d'interroger les 11 APIs.",
                    color = AluminumLight,
                    fontSize = 10.5.sp
                  )
                }
              }

              // Presets Row
              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ocrPresets) { preset ->
                  val isSel = preset.id == selectedOcrPreset.id
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSel) TechCyan.copy(alpha = 0.2f) else GraphiteBlack,
                    border = BorderStroke(1.dp, if (isSel) TechCyan else MetalBorder),
                    modifier = Modifier.clickable {
                      selectedOcrPreset = preset
                      customNoisyOcrInput = preset.rawNoisyOcrText
                    }
                  ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                      Text(preset.sourceTypeBadge, color = TechCyan, fontWeight = FontWeight.Black, fontSize = 8.5.sp)
                      Text(preset.title, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                    }
                  }
                }
              }

              // Editable Raw OCR Input
              OutlinedTextField(
                value = customNoisyOcrInput,
                onValueChange = { customNoisyOcrInput = it },
                label = { Text("Texte brut extrait par OCR (Photo WhatsApp / Pare-brise / Carte Grise)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = TechCyan,
                  unfocusedBorderColor = MetalBorder,
                  focusedTextColor = PureWhite,
                  unfocusedTextColor = PureWhite
                )
              )

              // Correction Preview Card
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = GraphiteBlack,
                border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "VIN ISO-3779 NETTOYÉ & PRÊT POUR CASCADE :",
                      color = AluminumLight,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp
                    )
                    Text(
                      text = "Confiance OCR : ${selectedOcrPreset.confidencePercent}% (${selectedOcrPreset.extractionTimeMs} ms)",
                      color = ElectricGreen,
                      fontWeight = FontWeight.Black,
                      fontSize = 10.sp
                    )
                  }

                  Text(
                    text = selectedOcrPreset.autoCleanedIsoVin,
                    color = ElectricGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                  )

                  selectedOcrPreset.ocrCorrectionsApplied.forEach { corr ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TechCyan, modifier = Modifier.size(13.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(corr, color = OffWhite, fontSize = 10.5.sp)
                    }
                  }

                  Text(
                    text = "Véhicule détecté : ${selectedOcrPreset.detectedBrandModelPreview} • Immat : ${selectedOcrPreset.wilayaPlateDetected}",
                    color = MechanicalOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                  )
                }
              }

              Button(
                onClick = { onRunOcrToCascade(customNoisyOcrInput) },
                colors = ButtonDefaults.buttonColors(containerColor = TechCyan, contentColor = GraphiteBlack),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("btn_execute_ocr_to_cascade")
              ) {
                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "NETTOYER LE VIN OCR & LANCER LA CASCADE 11 API",
                  fontWeight = FontWeight.Black,
                  fontSize = 11.5.sp
                )
              }
            }
          }
        }

        // ====================================================================
        // OPTION 2 : PONT ÉQUIVALENCE CROISÉE CHINOISE ↔ EUROPÉENNE (OEM → TECDOC IAM)
        // ====================================================================
        EssentialOptionSubTab.SINO_EURO_CROSS_REF -> {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MetalCard,
            border = BorderStroke(1.dp, MechanicalOrange),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CompareArrows, contentDescription = null, tint = MechanicalOrange)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "2. PONT ÉQUIVALENCE CROISÉE CHINOISE ↔ EUROPÉENNE (OEM → TECDOC IAM)",
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                  )
                  Text(
                    text = "Traduit instantanément une réf OEM chinoise (SinoAuto360/ChineseCar.parts) en réf européenne Bosch, Brembo, TRW ou Mann-Filter (#6 TecDoc) disponible en Algérie.",
                    color = AluminumLight,
                    fontSize = 10.5.sp
                  )
                }
              }

              // Vehicle selector
              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(crossRefBridges) { bridge ->
                  val isSel = bridge.id == selectedCrossRef.id
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSel) MechanicalOrange.copy(alpha = 0.22f) else GraphiteBlack,
                    border = BorderStroke(1.dp, if (isSel) MechanicalOrange else MetalBorder),
                    modifier = Modifier.clickable { selectedCrossRef = bridge }
                  ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                      Text(bridge.partCategory, color = MechanicalOrange, fontWeight = FontWeight.Black, fontSize = 8.5.sp)
                      Text(bridge.chineseVehicleLabel, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                    }
                  }
                }
              }

              // Chinese OEM Origin Box
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = GraphiteBlack,
                border = BorderStroke(1.dp, PerformanceRed),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = "RÉFÉRENCE CONSTRUCTEUR CHINE (OEM)",
                      color = PerformanceRedGlow,
                      fontWeight = FontWeight.Black,
                      fontSize = 10.sp
                    )
                    Text(
                      text = "VIN : ${selectedCrossRef.vinExample}",
                      color = AluminumLight,
                      fontFamily = FontFamily.Monospace,
                      fontSize = 10.sp
                    )
                  }
                  Text(
                    text = "${selectedCrossRef.partName} — Réf OEM : ${selectedCrossRef.chineseOemPartNumber}",
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                  )
                  Text(
                    text = "Spécifications : ${selectedCrossRef.technicalDimensionsSpec}",
                    color = TechCyan,
                    fontSize = 10.5.sp
                  )
                  Text(
                    text = "Plateforme Européenne Commune : ${selectedCrossRef.europeanPlatformSibling}",
                    color = ElectricGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                  )
                }
              }

              // IAM European Equivalents List
              selectedCrossRef.iamEquivalents.forEach { iam ->
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteDark,
                  border = BorderStroke(1.dp, MetalBorder),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(11.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Text(
                            text = "${iam.iamBrand} • ${iam.iamPartNumber}",
                            color = PureWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.5.sp
                          )
                          Spacer(modifier = Modifier.width(6.dp))
                          Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ElectricGreen.copy(alpha = 0.2f)
                          ) {
                            Text(
                              text = "${iam.compatibilityCertaintyPercent}% COMPATIBLE",
                              color = ElectricGreen,
                              fontWeight = FontWeight.Black,
                              fontSize = 8.5.sp,
                              modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                          }
                        }
                        Text(
                          text = "${iam.tecDocKType} • ${iam.sharedPlatformOrigin}",
                          color = AluminumLight,
                          fontSize = 10.sp
                        )
                      }
                      Column(horizontalAlignment = Alignment.End) {
                        Text(
                          text = "${iam.priceDzd} DZD",
                          color = ElectricGreen,
                          fontWeight = FontWeight.Black,
                          fontSize = 13.sp
                        )
                        Text(
                          text = "-${iam.savingsPercent}% vs OEM (${iam.oemDealerPriceDzd} DA)",
                          color = MechanicalOrange,
                          fontWeight = FontWeight.Bold,
                          fontSize = 9.5.sp
                        )
                      }
                    }

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = "📍 ${iam.localVendorNameWilaya} • ${iam.stockStatus}",
                        color = TechCyan,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp
                      )
                      Button(
                        onClick = { onSelectCrossRefAndAddToQuote(selectedCrossRef, iam) },
                        colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange, contentColor = PureWhite),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(30.dp)
                      ) {
                        Text("Choisir cette Équivalence", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                      }
                    }
                  }
                }
              }
            }
          }
        }

        // ====================================================================
        // OPTION 3 : SYNCHRONISATION INTELLIGENTE DU CACHE LOCAL (ROOM SQLITE 0ms)
        // ====================================================================
        EssentialOptionSubTab.ROOM_AUTO_LEARNING -> {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MetalCard,
            border = BorderStroke(1.dp, ElectricGreen),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Memory, contentDescription = null, tint = ElectricGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "3. AUTO-APPRENTISSAGE DU CACHE LOCAL ROOM SQLITE (0 ms HORS-LIGNE)",
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                  )
                  Text(
                    text = "Chaque VIN résolu par les APIs #1 à #10 est automatiquement sauvegardé dans la source #11 (Room SQLite). Si Internet coupe au sous-sol de l'atelier, le véhicule répond en 0 ms.",
                    color = AluminumLight,
                    fontSize = 10.5.sp
                  )
                }
              }

              Button(
                onClick = onTriggerAutoLearnCache,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen, contentColor = GraphiteBlack),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("btn_auto_learn_room_cache")
              ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "INDEXER LE VÉHICULE ACTIF (${activeTrace.matchedVinRecord.vin}) DANS ROOM SQLITE (0 ms)",
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp
                )
              }

              autoLearnedCacheEntries.forEach { entry ->
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, MetalBorder),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(11.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text(
                          text = entry.vehicleLabel,
                          color = PureWhite,
                          fontWeight = FontWeight.Black,
                          fontSize = 12.sp
                        )
                        Text(
                          text = "VIN : ${entry.vin} • ${entry.engineAndTrans}",
                          color = TechCyan,
                          fontFamily = FontFamily.Monospace,
                          fontSize = 10.sp
                        )
                      }
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ElectricGreen.copy(alpha = 0.2f),
                        border = BorderStroke(0.8.dp, ElectricGreen)
                      ) {
                        Text(
                          text = "LATENCE OFFLINE : ${entry.cachedOfflineLatencyMs} ms",
                          color = ElectricGreen,
                          fontWeight = FontWeight.Black,
                          fontSize = 9.sp,
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                      }
                    }

                    Text(
                      text = "Appris depuis : ${entry.initialSourceProvider.displayName} (${entry.initialNetworkLatencyMs} ms) • ${entry.oemPartsCountIndexed} pièces OEM + ${entry.crossRefIamCountIndexed} équiv. IAM (${entry.localRoomSizeKb} Ko)",
                      color = AluminumLight,
                      fontSize = 10.sp
                    )

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = "${entry.learnedTimestamp} • Consulté ${entry.hitCountInWorkshop}x",
                        color = MechanicalOrange,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                      )
                      OutlinedButton(
                        onClick = { onRecallFromOfflineRoomZeroMs(entry.vin) },
                        border = BorderStroke(1.dp, ElectricGreen),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
                        modifier = Modifier.height(28.dp)
                      ) {
                        Text("Charger en 0 ms (Hors-Ligne)", color = ElectricGreen, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                      }
                    }
                  }
                }
              }
            }
          }
        }

        // ====================================================================
        // OPTION 4 : DEVIS / ORDRE DE RÉPARATION (OR) EN 1 CLIC (DZD + BARÈMES T1/T2/T3)
        // ====================================================================
        EssentialOptionSubTab.ONE_CLICK_OR_QUOTE -> {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MetalCard,
            border = BorderStroke(1.dp, PerformanceRed),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = PerformanceRedGlow)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "4. DEVIS / ORDRE DE RÉPARATION (OR) AUTOMATIQUE EN 1 CLIC (DZD)",
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                  )
                  Text(
                    text = "Assemble automatiquement le VIN décodé, les pièces OEM/IAM de la Cascade 11 API et le barème horaire T1/T2/T3 (WinMotor) en Dinars Algériens.",
                    color = AluminumLight,
                    fontSize = 10.5.sp
                  )
                }
              }

              // Tier Selector (T1 / T2 / T3)
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                listOf(
                  0 to "T1 • 2 500 DA/h",
                  1 to "T2 • 3 500 DA/h",
                  2 to "T3 • 4 500 DA/h"
                ).forEach { (idx, label) ->
                  val isSel = selectedTierIndex == idx
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSel) PerformanceRed else GraphiteBlack,
                    border = BorderStroke(1.dp, if (isSel) PerformanceRedGlow else MetalBorder),
                    modifier = Modifier
                      .weight(1f)
                      .clickable { onSelectQuoteTier(idx) }
                  ) {
                    Text(
                      text = label,
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 10.sp,
                      modifier = Modifier.padding(vertical = 7.dp, horizontal = 6.dp)
                    )
                  }
                }
              }

              // Quote Summary Card
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = GraphiteBlack,
                border = BorderStroke(1.dp, MetalBorder),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = "${oneClickQuote.orderReferenceNumber} • ${oneClickQuote.vehicleTitle}",
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 12.sp
                    )
                    Text(
                      text = oneClickQuote.generatedFromCascadeSource,
                      color = TechCyan,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.5.sp
                    )
                  }

                  Text(
                    text = "VIN : ${oneClickQuote.vin} • ${oneClickQuote.tariffTierLabel} (${oneClickQuote.flatRateHours}h)",
                    color = AluminumLight,
                    fontSize = 10.sp
                  )

                  HorizontalDivider(color = MetalBorder)

                  oneClickQuote.partsLines.forEach { line ->
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = "${line.description} [${line.refCode}]",
                          color = OffWhite,
                          fontWeight = FontWeight.SemiBold,
                          fontSize = 10.5.sp
                        )
                        Text(
                          text = line.sourceApiBadge,
                          color = AluminumMuted,
                          fontSize = 9.sp
                        )
                      }
                      Text(
                        text = "${line.unitPriceDzd} DZD",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                      )
                    }
                  }

                  HorizontalDivider(color = MetalBorder)

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = "Pièces HT : ${oneClickQuote.totalPartsHtDzd} DA | M.O : ${oneClickQuote.totalLaborHtDzd} DA | Consom. : ${oneClickQuote.consumablesDzd} DA",
                      color = AluminumLight,
                      fontSize = 10.sp
                    )
                  }

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "TOTAL TTC (TVA ${oneClickQuote.tvaPercent}% incluse) :",
                      color = MechanicalOrange,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.5.sp
                    )
                    Text(
                      text = "${oneClickQuote.totalTtcDzd} DZD TTC",
                      color = ElectricGreen,
                      fontWeight = FontWeight.Black,
                      fontSize = 15.sp
                    )
                  }

                  Text(
                    text = "🛡️ ${oneClickQuote.warrantyNotice}",
                    color = TechCyan,
                    fontSize = 9.5.sp
                  )
                }
              }

              Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                  onClick = onCreateWorkshopOrderFromOneClickQuote,
                  colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed, contentColor = PureWhite),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier
                    .weight(1f)
                    .testTag("btn_create_or_one_click")
                ) {
                  Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Créer l'OR Atelier", fontWeight = FontWeight.Black, fontSize = 11.sp)
                }

                OutlinedButton(
                  onClick = onExportOneClickQuotePdf,
                  border = BorderStroke(1.dp, TechCyan),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("PDF & WhatsApp Client", color = TechCyan, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
              }
            }
          }
        }

        // ====================================================================
        // OPTION 5 : FLUIDES, LUBRIFIANTS & PROCÉDURES DE RESET SERVICE (DCT CHINE)
        // ====================================================================
        EssentialOptionSubTab.FLUIDS_DCT_RESET -> {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MetalCard,
            border = BorderStroke(1.dp, WarningAmber),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.OilBarrel, contentDescription = null, tint = WarningAmber)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "5. FLUIDES, BOÎTES DCT HUMIDES & PROCÉDURES DE RESET SERVICE",
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                  )
                  Text(
                    text = "Prévient les casses de boîtes 7DCT300 (Jetour, Chery, Geely) et moteurs Turbo GDI grâce aux normes OEM exactes et procédures de remise à zéro.",
                    color = AluminumLight,
                    fontSize = 10.5.sp
                  )
                }
              }

              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(fluidSpecs) { spec ->
                  val isSel = spec.id == selectedFluidSpec.id
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSel) WarningAmber.copy(alpha = 0.22f) else GraphiteBlack,
                    border = BorderStroke(1.dp, if (isSel) WarningAmber else MetalBorder),
                    modifier = Modifier.clickable { selectedFluidSpec = spec }
                  ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                      Text(spec.engineCode, color = WarningAmber, fontWeight = FontWeight.Black, fontSize = 8.5.sp)
                      Text(spec.vehicleTitle, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                    }
                  }
                }
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = GraphiteBlack,
                border = BorderStroke(1.dp, MetalBorder),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text(
                    text = "🛢️ HUILE MOTEUR : ${selectedFluidSpec.engineOilViscosity}",
                    color = ElectricGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                  )
                  Text(
                    text = "• Norme exigée : ${selectedFluidSpec.engineOilNormOem}\n• Capacité : ${selectedFluidSpec.engineOilCapacityLiters}",
                    color = OffWhite,
                    fontSize = 10.5.sp
                  )

                  HorizontalDivider(color = MetalBorder)

                  Text(
                    text = "⚙️ TRANSMISSION DCT / EHS : ${selectedFluidSpec.transmissionCode}",
                    color = MechanicalOrange,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                  )
                  Text(
                    text = "• Fluide obligatoire : ${selectedFluidSpec.dctGearboxFluidType}\n• Capacité : ${selectedFluidSpec.dctGearboxCapacityLiters}\n• Échéance : ${selectedFluidSpec.dctChangeIntervalKm}",
                    color = OffWhite,
                    fontSize = 10.5.sp
                  )

                  HorizontalDivider(color = MetalBorder)

                  Text(
                    text = "❄️ Refroidissement : ${selectedFluidSpec.coolantSpecAndCapacity}\n🛑 Freins / EPB : ${selectedFluidSpec.brakeFluidSpec}",
                    color = TechCyan,
                    fontSize = 10.5.sp
                  )

                  HorizontalDivider(color = MetalBorder)

                  Text(
                    text = "🔄 PROCÉDURE DE RESET COMPTEUR ENTRETIEN (AU TABLEAU DE BORD) :",
                    color = WarningAmber,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                  )
                  selectedFluidSpec.manualClusterResetSteps.forEach { step ->
                    Text(step, color = PureWhite, fontSize = 10.5.sp)
                  }
                  Text(
                    text = "💻 Commande OBD-II UDS : ${selectedFluidSpec.obdUdsResetRoutine}",
                    color = TechCyan,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.5.sp
                  )
                }
              }

              Button(
                onClick = { onExecuteObdResetRoutine(selectedFluidSpec) },
                colors = ButtonDefaults.buttonColors(containerColor = WarningAmber, contentColor = GraphiteBlack),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "ENVOYER LA COMMANDE OBD-II UDS DE RESET ENTRETIEN",
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp
                )
              }
            }
          }
        }

        // ====================================================================
        // OPTION 6 : PONT DIRECT AVEC LE « PASSEPORT QR UNIVERSEL DU VÉHICULE »
        // ====================================================================
        EssentialOptionSubTab.QR_PASSPORT_SYNC -> {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MetalCard,
            border = BorderStroke(1.dp, Color(0xFFAB47BC)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color(0xFFAB47BC))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "6. PONT DIRECT AVEC LE « PASSEPORT QR UNIVERSEL DU VÉHICULE »",
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                  )
                  Text(
                    text = "Synchronise le résultat de la Cascade 11 API, les fluides certifiés et le dernier OR sur le sticker QR collé sur le montant de porte (Atelier + Vendeur + Client).",
                    color = AluminumLight,
                    fontSize = 10.5.sp
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = GraphiteBlack,
                border = BorderStroke(1.dp, Color(0xFFAB47BC).copy(alpha = 0.7f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "PASSEPORT ID : ${qrPassportSync.passportQrId}",
                      color = Color(0xFFCE93D8),
                      fontWeight = FontWeight.Black,
                      fontSize = 11.sp
                    )
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = ElectricGreen.copy(alpha = 0.2f)
                    ) {
                      Text(
                        text = "STICKER PORTE PRÊT",
                        color = ElectricGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 8.5.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }

                  Text(
                    text = "${qrPassportSync.vehicleTitle} • VIN : ${qrPassportSync.vin}",
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                  )
                  Text(
                    text = "Source API Certifiée : ${qrPassportSync.winningApiProviderName}",
                    color = TechCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                  )
                  Text(
                    text = "Huile Moteur enregistrée : ${qrPassportSync.certifiedEngineOilGrade}",
                    color = OffWhite,
                    fontSize = 10.5.sp
                  )
                  Text(
                    text = "Échéance Boîte DCT : ${qrPassportSync.nextDctGearboxServiceKm}",
                    color = WarningAmber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                  )
                  Text(
                    text = "Dernier Devis / OR lié : ${qrPassportSync.lastSyncedWorkshopOrderRef}",
                    color = ElectricGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                  )
                }
              }

              Button(
                onClick = onSyncAndOpenUniversalQrPassport,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAB47BC), contentColor = PureWhite),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("btn_sync_open_qr_passport")
              ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "SYNCHRONISER & OUVRIR LE PASSEPORT QR UNIVERSEL",
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp
                )
              }
            }
          }
        }
      }
    }
  }
}
