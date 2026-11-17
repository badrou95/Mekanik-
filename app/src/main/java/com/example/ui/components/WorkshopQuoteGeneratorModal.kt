package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.util.PdfGeneratorUtil
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

enum class QuoteGeneratorTab {
  EDITION,
  CHECKLIST_AMPOULES_ETAT,
  PREVISUALISATION_PDF
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkshopQuoteGeneratorModal(
  activeVehicle: Vehicle,
  allAvailableParts: List<PartItem>,
  preSelectedPart: PartItem? = null,
  initialClientName: String = "",
  initialClientPhone: String = "",
  initialPlateNumber: String = "",
  initialMileage: String = "",
  initialNotes: String = "",
  onOpenVoiceDictation: (() -> Unit)? = null,
  onDismiss: () -> Unit,
  onConfirmQuote: (clientName: String, clientPhone: String, plateNumber: String, laborHours: Float, items: List<RepairItem>, notes: String) -> Unit,
  onDownloadPdf: ((quoteNumber: String, clientName: String, clientPhone: String, clientAddress: String, plateNumber: String, vehicleModel: String, vehicleMileage: String, quoteNotes: String, items: List<RepairItem>, laborHours: Float, laborRatePerHour: Int, diagnosticCostDzd: Int, suppliesCostDzd: Int, applyTva19: Boolean, fiscalStampDzd: Int) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  // Current Navigation Step (Édition vs Prévisualisation PDF)
  var currentTab by remember { mutableStateOf(QuoteGeneratorTab.EDITION) }

  // Client Info State with prefilled support
  var clientName by remember(initialClientName) { mutableStateOf(initialClientName.ifBlank { "Karim Meziane" }) }
  var clientPhone by remember(initialClientPhone) { mutableStateOf(initialClientPhone.ifBlank { "0550 42 19 88" }) }
  var clientAddress by remember { mutableStateOf("Kouba, Alger (16)") }
  var plateNumber by remember(initialPlateNumber) { mutableStateOf(initialPlateNumber.ifBlank { "04291-120-16" }) }
  var vehicleMileage by remember(initialMileage) { mutableStateOf(initialMileage.ifBlank { "88500" }) }
  var quoteNotes by remember(initialNotes) { mutableStateOf(initialNotes.ifBlank { "Devis pièces d'origine certifiées et main d'œuvre garantie 6 mois / 10 000 km." }) }

  // Checklist Ordre de Réparation : 22 Ampoules Véhicule + État Intérieur/Extérieur (Propre/Sale)
  var orChecklist by remember { mutableStateOf(RepairOrderInspectionChecklist.defaultSample()) }

  // Selected Parts for Quote
  val initialParts = remember {
    val list = mutableListOf<RepairItem>()
    if (preSelectedPart != null) {
      list.add(
        RepairItem(
          name = preSelectedPart.name,
          reference = preSelectedPart.oemReference,
          quantity = 1,
          priceDzd = preSelectedPart.priceEstimatedDzd
        )
      )
    } else {
      // Default common maintenance items + default replaced bulbs from checklist
      list.add(RepairItem("Plaquettes de frein avant Brembo", "5Q0 698 151 BR", 1, 8500))
      list.add(RepairItem("Disques avant ventilés 312mm High Carbon", "1K0 615 301 AA", 1, 18500))
      RepairOrderInspectionChecklist.defaultSample().replacedBulbs.forEach { bulb ->
        list.add(bulb.toRepairItem())
      }
    }
    mutableStateListOf(*list.toTypedArray())
  }
  val quoteItems = remember { initialParts }

  // Custom Part Adding dialog state
  var showAddPartDialog by remember { mutableStateOf(false) }
  var newPartName by remember { mutableStateOf("") }
  var newPartRef by remember { mutableStateOf("") }
  var newPartQty by remember { mutableStateOf("1") }
  var newPartPriceDzd by remember { mutableStateOf("4500") }

  // Manual Labor & Workshop Options
  var laborHours by remember { mutableFloatStateOf(2.5f) }
  var laborRatePerHourDzd by remember { mutableIntStateOf(2000) }
  var laborDescription by remember { mutableStateOf("Dépose/repose train roulant, purge circuit hydraulique et contrôle dynamique.") }
  var includeDiagnosticEcu by remember { mutableStateOf(true) } // +2500 DZD
  var includeWorkshopSupplies by remember { mutableStateOf(true) } // +1000 DZD
  var discountPercent by remember { mutableIntStateOf(0) }
  var applyTva19 by remember { mutableStateOf(true) }

  // PDF Export simulated feedback
  var pdfExportSuccessMessage by remember { mutableStateOf<String?>(null) }

  // Calculations
  val partsSubtotalDzd = quoteItems.sumOf { it.priceDzd * it.quantity }
  val laborBaseTotalDzd = (laborHours * laborRatePerHourDzd).toInt()
  val diagnosticCostDzd = if (includeDiagnosticEcu) 2500 else 0
  val suppliesCostDzd = if (includeWorkshopSupplies) 1000 else 0
  val totalLaborAndServicesDzd = laborBaseTotalDzd + diagnosticCostDzd + suppliesCostDzd

  val grossTotalHtDzd = partsSubtotalDzd + totalLaborAndServicesDzd
  val discountAmountDzd = (grossTotalHtDzd * (discountPercent / 100.0)).toInt()
  val netHtDzd = (grossTotalHtDzd - discountAmountDzd).coerceAtLeast(0)
  val tvaAmountDzd = if (applyTva19) (netHtDzd * 0.19).toInt() else 0
  val fiscalStampDzd = if (netHtDzd > 0) 100 else 0
  val netTotalTtcDzd = netHtDzd + tvaAmountDzd + fiscalStampDzd

  val df = remember {
    DecimalFormat("#,###", DecimalFormatSymbols(Locale.FRENCH))
  }

  val generatedQuoteNumber = remember {
    "DEV-2026-${(1000..9999).random()}"
  }

  val triggerPdfDownload: () -> Unit = {
    if (onDownloadPdf != null) {
      onDownloadPdf(
        generatedQuoteNumber,
        clientName,
        clientPhone,
        clientAddress,
        plateNumber,
        "${activeVehicle.brand} ${activeVehicle.model} (${activeVehicle.exactYear})",
        vehicleMileage,
        quoteNotes,
        quoteItems.toList(),
        laborHours,
        laborRatePerHourDzd,
        diagnosticCostDzd,
        suppliesCostDzd,
        applyTva19,
        fiscalStampDzd
      )
    } else {
      try {
        val result = PdfGeneratorUtil.generateWorkshopQuotePdf(
          context = context,
          quoteNumber = generatedQuoteNumber,
          clientName = clientName,
          clientPhone = clientPhone,
          clientAddress = clientAddress,
          plateNumber = plateNumber,
          vehicleModel = "${activeVehicle.brand} ${activeVehicle.model} (${activeVehicle.exactYear})",
          vehicleMileage = vehicleMileage,
          quoteNotes = quoteNotes,
          items = quoteItems.toList(),
          laborHours = laborHours,
          laborRatePerHour = laborRatePerHourDzd,
          diagnosticCostDzd = diagnosticCostDzd,
          suppliesCostDzd = suppliesCostDzd,
          applyTva19 = applyTva19,
          fiscalStampDzd = fiscalStampDzd
        )
        pdfExportSuccessMessage = "Devis PDF généré : ${result.fileName} (${result.formattedSize})"
        PdfGeneratorUtil.openPdfFile(context, result.file)
      } catch (e: Exception) {
        pdfExportSuccessMessage = "Erreur PDF : ${e.message}"
      }
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Scaffold(
      modifier = modifier
        .fillMaxSize()
        .background(GraphiteDark)
        .testTag("modal_quote_generator"),
      topBar = {
        Surface(
          color = MetalCard,
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(PerformanceRedDark),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = PerformanceRedGlow,
                    modifier = Modifier.size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = "GÉNÉRATEUR DE DEVIS ATELIER",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Black,
                      color = PureWhite,
                      fontSize = 15.sp
                    )
                  )
                  Text(
                    text = "$generatedQuoteNumber • Total TTC : ${df.format(netTotalTtcDzd)} DZD",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = MechanicalOrange,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.5.sp
                    )
                  )
                }
              }

              IconButton(
                onClick = onDismiss,
                modifier = Modifier
                  .size(34.dp)
                  .clip(CircleShape)
                  .background(MetalSurface)
              ) {
                Icon(
                  Icons.Default.Close,
                  contentDescription = "Fermer",
                  tint = AluminumLight,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            // Tab Switcher (1. Édition, 2. Checklist Ampoules & État OR, 3. Aperçu PDF)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (currentTab == QuoteGeneratorTab.EDITION) PerformanceRed else GraphiteBlack,
                border = BorderStroke(1.dp, if (currentTab == QuoteGeneratorTab.EDITION) PerformanceRedGlow else MetalBorder),
                modifier = Modifier
                  .weight(1f)
                  .clickable { currentTab = QuoteGeneratorTab.EDITION }
                  .testTag("tab_quote_edit")
              ) {
                Row(
                  modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.EditNote,
                    contentDescription = null,
                    tint = if (currentTab == QuoteGeneratorTab.EDITION) PureWhite else AluminumMuted,
                    modifier = Modifier.size(15.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "1. Pièces & M.O",
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = if (currentTab == QuoteGeneratorTab.EDITION) PureWhite else AluminumGray,
                      fontSize = 10.5.sp
                    ),
                    maxLines = 1
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (currentTab == QuoteGeneratorTab.CHECKLIST_AMPOULES_ETAT) MechanicalOrange else GraphiteBlack,
                border = BorderStroke(1.dp, if (currentTab == QuoteGeneratorTab.CHECKLIST_AMPOULES_ETAT) MechanicalOrangeGlow else MetalBorder),
                modifier = Modifier
                  .weight(1.15f)
                  .clickable { currentTab = QuoteGeneratorTab.CHECKLIST_AMPOULES_ETAT }
                  .testTag("tab_quote_checklist_bulbs")
              ) {
                Row(
                  modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = if (currentTab == QuoteGeneratorTab.CHECKLIST_AMPOULES_ETAT) PureWhite else MechanicalOrange,
                    modifier = Modifier.size(15.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "2. Ampoules & État (${orChecklist.burntOrReplacedBulbs.size} HS)",
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = if (currentTab == QuoteGeneratorTab.CHECKLIST_AMPOULES_ETAT) PureWhite else AluminumLight,
                      fontSize = 10.5.sp
                    ),
                    maxLines = 1
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (currentTab == QuoteGeneratorTab.PREVISUALISATION_PDF) TechnicalBlue else GraphiteBlack,
                border = BorderStroke(1.dp, if (currentTab == QuoteGeneratorTab.PREVISUALISATION_PDF) TechCyan else MetalBorder),
                modifier = Modifier
                  .weight(0.95f)
                  .clickable { currentTab = QuoteGeneratorTab.PREVISUALISATION_PDF }
                  .testTag("tab_quote_preview_pdf")
              ) {
                Row(
                  modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = if (currentTab == QuoteGeneratorTab.PREVISUALISATION_PDF) PureWhite else AluminumMuted,
                    modifier = Modifier.size(15.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "3. Aperçu PDF",
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = if (currentTab == QuoteGeneratorTab.PREVISUALISATION_PDF) PureWhite else AluminumGray,
                      fontSize = 10.5.sp
                    ),
                    maxLines = 1
                  )
                }
              }
            }
            Spacer(modifier = Modifier.height(4.dp))
          }
        }
      },
      bottomBar = {
        Surface(
          color = MetalCard,
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (currentTab == QuoteGeneratorTab.EDITION) {
              MekanikSecondaryButton(
                text = "APERÇU",
                onClick = { currentTab = QuoteGeneratorTab.PREVISUALISATION_PDF },
                icon = Icons.Default.Visibility,
                modifier = Modifier.weight(0.85f),
                testTag = "btn_go_to_pdf_preview"
              )

              Button(
                onClick = triggerPdfDownload,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                modifier = Modifier
                  .weight(1.2f)
                  .testTag("btn_download_quote_pdf_tab1")
              ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("TÉLÉCHARGER PDF", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, color = PureWhite, fontSize = 11.sp))
              }

              MekanikPrimaryButton(
                text = "VALIDER (${df.format(netTotalTtcDzd)} DA)",
                onClick = {
                  onConfirmQuote(
                    clientName,
                    clientPhone,
                    plateNumber,
                    laborHours,
                    quoteItems.toList(),
                    quoteNotes
                  )
                },
                modifier = Modifier.weight(1.15f),
                testTag = "btn_finalize_quote"
              )
            } else {
              MekanikSecondaryButton(
                text = "MODIFIER",
                onClick = { currentTab = QuoteGeneratorTab.EDITION },
                icon = Icons.Default.Edit,
                modifier = Modifier.weight(0.85f),
                testTag = "btn_back_to_edit"
              )

              Button(
                onClick = triggerPdfDownload,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                modifier = Modifier
                  .weight(1.2f)
                  .testTag("btn_download_quote_pdf_tab2")
              ) {
                Icon(Icons.Default.Download, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("TÉLÉCHARGER PDF", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, color = PureWhite, fontSize = 11.sp))
              }

              MekanikPrimaryButton(
                text = "ENVOYER & CRÉER OR",
                onClick = {
                  onConfirmQuote(
                    clientName,
                    clientPhone,
                    plateNumber,
                    laborHours,
                    quoteItems.toList(),
                    quoteNotes
                  )
                },
                icon = Icons.Default.Send,
                modifier = Modifier.weight(1.25f),
                testTag = "btn_send_and_create_or"
              )
            }
          }
        }
      }
    ) { paddingValues ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
      ) {
        when (currentTab) {
          QuoteGeneratorTab.EDITION -> {
            // TAB 1: FORM & CALCULATOR
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(16.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              // 1. Client & Vehicle Overview Card
              item {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.dp, MetalBorder)
                ) {
                  Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = PerformanceRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                          text = "CLIENT & VÉHICULE ASSOCIÉ",
                          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                        )
                      }
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GraphiteBlack,
                        border = BorderStroke(0.5.dp, MetalBorder)
                      ) {
                        Text(
                          text = "VIN: ${activeVehicle.vin.take(10)}...",
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = TechCyan,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                          ),
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                      }
                    }

                    Text(
                      text = "${activeVehicle.brand} ${activeVehicle.model} • Moteur: ${activeVehicle.engineCode} (${activeVehicle.powerCh} ch) • Année: ${activeVehicle.exactYear}",
                      style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontWeight = FontWeight.Bold)
                    )

                    Divider(color = MetalBorder)

                    // Bandeau Réception Rapide : Prise de Photo Auto (Immat, VIN, Carburant, Km, Carte Grise, Tableau de bord allumé)
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = adaptiveBannerSurface(0xFF0E212E, 0xFFF0F9FF),
                      border = BorderStroke(1.dp, TechCyan),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                      ) {
                        Text(
                          text = "📷 ARRIVÉE VÉHICULE : REMPLISSAGE AUTOMATIQUE PAR PHOTO & VOIX IA",
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = TechCyan,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.5.sp
                          )
                        )
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                          Button(
                            onClick = {
                              plateNumber = "01948-122-16"
                              vehicleMileage = "114 820"
                              if (clientName.isBlank()) clientName = "Karim Benali"
                              val updatedPhotos = orChecklist.receptionPhotos.copy(
                                platePhotoTaken = true,
                                extractedPlateNumber = "01948-122-16",
                                vinPhotoTaken = true,
                                extractedVin = activeVehicle.vin,
                                fuelAndMileagePhotoTaken = true,
                                extractedMileageKm = "114 820",
                                extractedFuelLevelQuarters = 3,
                                carteGrisePhotoTaken = true,
                                extractedClientName = clientName,
                                clientNameInputMethod = "PHOTO_CARTE_GRISE",
                                dashboardRunningPhotoTaken = true
                              )
                              val updatedLights = orChecklist.dashboardWarningLights.map { light ->
                                if (light.id == "warn_engine_mil" || light.id == "warn_tpms") {
                                  light.copy(isIlluminated = true)
                                } else {
                                  light
                                }
                              }
                              orChecklist = orChecklist.copy(
                                receptionPhotos = updatedPhotos,
                                fuelLevelQuarters = 3,
                                dashboardWarningLights = updatedLights
                              )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier
                              .weight(1.25f)
                              .testTag("btn_quote_auto_fill_arrival_photos")
                          ) {
                            Icon(Icons.Default.CameraEnhance, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                              text = "📷 Photo Immat/VIN/Km/Jauge",
                              color = Color.White,
                              fontWeight = FontWeight.Black,
                              fontSize = 9.5.sp
                            )
                          }

                          Button(
                            onClick = {
                              clientName = "Karim Benali"
                              plateNumber = "01948-122-16"
                              orChecklist = orChecklist.copy(
                                receptionPhotos = orChecklist.receptionPhotos.copy(
                                  carteGrisePhotoTaken = true,
                                  extractedClientName = "Karim Benali",
                                  clientNameInputMethod = "PHOTO_CARTE_GRISE"
                                )
                              )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier
                              .weight(1f)
                              .testTag("btn_quote_photo_carte_grise")
                          ) {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                              text = "🪪 Carte Grise",
                              color = Color.White,
                              fontWeight = FontWeight.Black,
                              fontSize = 9.5.sp
                            )
                          }
                        }
                      }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                      OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("Nom Client (Manuel / Vocal / Carte Grise)") },
                        singleLine = true,
                        modifier = Modifier.weight(1.2f)
                      )
                      OutlinedTextField(
                        value = clientPhone,
                        onValueChange = { clientPhone = it },
                        label = { Text("Téléphone") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                      )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                      OutlinedTextField(
                        value = plateNumber,
                        onValueChange = { plateNumber = it },
                        label = { Text("Immatriculation (Photo Auto)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                      )
                      OutlinedTextField(
                        value = vehicleMileage,
                        onValueChange = { vehicleMileage = it },
                        label = { Text("Kilométrage KM & Jauge (${orChecklist.fuelLevelFormatted})") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                      )
                    }
                  }
                }
              }

              // 2. Extracted Parts & Items List
              item {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.dp, MetalBorder)
                ) {
                  Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                          text = "PIÈCES DE RECHANGE SÉLECTIONNÉES (${quoteItems.size})",
                          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                        )
                      }

                      TextButton(
                        onClick = { showAddPartDialog = true },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                      ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ajouter Pièce", color = MechanicalOrange, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                      }
                    }

                    if (quoteItems.isEmpty()) {
                      Text(
                        text = "Aucune pièce ajoutée au devis pour l'instant.",
                        style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted)
                      )
                    } else {
                      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        quoteItems.forEachIndexed { index, item ->
                          Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GraphiteBlack,
                            border = BorderStroke(1.dp, MetalBorder)
                          ) {
                            Row(
                              modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                              Column(modifier = Modifier.weight(1f)) {
                                Text(
                                  text = item.name,
                                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                                )
                                Text(
                                  text = "Réf: ${item.reference} • ${df.format(item.priceDzd)} DA / unité",
                                  style = MaterialTheme.typography.labelSmall.copy(color = AluminumGray, fontSize = 10.5.sp)
                                )
                              }

                              Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                              ) {
                                // Qty Controls
                                IconButton(
                                  onClick = {
                                    if (item.quantity > 1) {
                                      quoteItems[index] = item.copy(quantity = item.quantity - 1)
                                    } else {
                                      quoteItems.removeAt(index)
                                    }
                                  },
                                  modifier = Modifier.size(26.dp)
                                ) {
                                  Icon(Icons.Default.Remove, contentDescription = "Moins", tint = AluminumLight, modifier = Modifier.size(14.dp))
                                }

                                Text(
                                  text = "${item.quantity}",
                                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                                )

                                IconButton(
                                  onClick = {
                                    quoteItems[index] = item.copy(quantity = item.quantity + 1)
                                  },
                                  modifier = Modifier.size(26.dp)
                                ) {
                                  Icon(Icons.Default.Add, contentDescription = "Plus", tint = AluminumLight, modifier = Modifier.size(14.dp))
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                  text = "${df.format(item.priceDzd * item.quantity)} DA",
                                  style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = MechanicalOrange,
                                    fontSize = 12.sp
                                  )
                                )

                                IconButton(
                                  onClick = { quoteItems.removeAt(index) },
                                  modifier = Modifier.size(26.dp)
                                ) {
                                  Icon(Icons.Default.DeleteOutline, contentDescription = "Supprimer", tint = PerformanceRed, modifier = Modifier.size(16.dp))
                                }
                              }
                            }
                          }
                        }
                      }
                    }

                    // Fast catalogue extraction recommendation
                    if (allAvailableParts.isNotEmpty()) {
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = "Suggestions rapides depuis le catalogue compatible :",
                        style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted)
                      )
                      LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                      ) {
                        items(allAvailableParts.take(5)) { part ->
                          val isAlreadyAdded = quoteItems.any { it.reference == part.oemReference }
                          Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isAlreadyAdded) PerformanceRedDark.copy(alpha = 0.3f) else GraphiteBlack,
                            border = BorderStroke(0.5.dp, if (isAlreadyAdded) PerformanceRed else MetalBorder),
                            modifier = Modifier.clickable {
                              if (!isAlreadyAdded) {
                                quoteItems.add(
                                  RepairItem(
                                    name = part.name,
                                    reference = part.oemReference,
                                    quantity = 1,
                                    priceDzd = part.priceEstimatedDzd
                                  )
                                )
                              }
                            }
                          ) {
                            Text(
                              text = if (isAlreadyAdded) "✓ ${part.name.take(15)}..." else "+ ${part.name.take(15)}... (${df.format(part.priceEstimatedDzd)} DA)",
                              style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isAlreadyAdded) PerformanceRedGlow else AluminumLight,
                                fontSize = 10.5.sp
                              ),
                              modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                          }
                        }
                      }
                    }
                  }
                }
              }

              // 3. Manual Labor & Workshop Services Card
              item {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.dp, MetalBorder)
                ) {
                  Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Handyman, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                          text = "MAIN D'ŒUVRE & PRESTATIONS D'ATELIER",
                          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                        )
                      }
                      Text(
                        text = "${df.format(totalLaborAndServicesDzd)} DZD",
                        style = MaterialTheme.typography.labelMedium.copy(
                          color = TechCyan,
                          fontWeight = FontWeight.Black
                        )
                      )
                    }

                    // Labor hours counter and hourly rate
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(10.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GraphiteBlack,
                        border = BorderStroke(1.dp, MetalBorder),
                        modifier = Modifier.weight(1f)
                      ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                          Text(text = "Temps Main d'œuvre", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted))
                          Spacer(modifier = Modifier.height(4.dp))
                          Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                          ) {
                            IconButton(
                              onClick = { if (laborHours > 0.5f) laborHours -= 0.5f },
                              modifier = Modifier.size(28.dp)
                            ) {
                              Icon(Icons.Default.Remove, contentDescription = null, tint = AluminumLight)
                            }
                            Text(
                              text = "${laborHours} h",
                              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, color = PureWhite)
                            )
                            IconButton(
                              onClick = { laborHours += 0.5f },
                              modifier = Modifier.size(28.dp)
                            ) {
                              Icon(Icons.Default.Add, contentDescription = null, tint = AluminumLight)
                            }
                          }
                        }
                      }

                      Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GraphiteBlack,
                        border = BorderStroke(1.dp, MetalBorder),
                        modifier = Modifier.weight(1f)
                      ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                          Text(text = "Taux horaire atelier", style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted))
                          Spacer(modifier = Modifier.height(4.dp))
                          Text(
                            text = "${df.format(laborRatePerHourDzd)} DA / h",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, color = TechCyan)
                          )
                          Text(text = "Total MO: ${df.format(laborBaseTotalDzd)} DA", style = MaterialTheme.typography.labelSmall.copy(color = AluminumGray, fontSize = 10.sp))
                        }
                      }
                    }

                    OutlinedTextField(
                      value = laborDescription,
                      onValueChange = { laborDescription = it },
                      label = { Text("Désignation des travaux d'atelier") },
                      singleLine = false,
                      maxLines = 2,
                      modifier = Modifier.fillMaxWidth()
                    )

                    Divider(color = MetalBorder)

                    // Additional Workshop Checkboxes
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .clickable { includeDiagnosticEcu = !includeDiagnosticEcu },
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Checkbox(
                        checked = includeDiagnosticEcu,
                        onCheckedChange = { includeDiagnosticEcu = it },
                        colors = CheckboxDefaults.colors(checkedColor = TechCyan)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = "Diagnostic Électronique Calculateur & Scan DTC",
                          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                        )
                        Text(
                          text = "Lecture flux de données, effacement mémoire défauts",
                          style = MaterialTheme.typography.labelSmall.copy(color = AluminumGray, fontSize = 10.5.sp)
                        )
                      }
                      Text(
                        text = "+ 2 500 DA",
                        style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontWeight = FontWeight.Bold)
                      )
                    }

                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .clickable { includeWorkshopSupplies = !includeWorkshopSupplies },
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Checkbox(
                        checked = includeWorkshopSupplies,
                        onCheckedChange = { includeWorkshopSupplies = it },
                        colors = CheckboxDefaults.colors(checkedColor = TechCyan)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = "Consommables Atelier & Recyclage Déchets",
                          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                        )
                        Text(
                          text = "Nettoyant frein, chiffons d'atelier, traitement huiles usagées",
                          style = MaterialTheme.typography.labelSmall.copy(color = AluminumGray, fontSize = 10.5.sp)
                        )
                      }
                      Text(
                        text = "+ 1 000 DA",
                        style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontWeight = FontWeight.Bold)
                      )
                    }
                  }
                }
              }

              // 4. Financial Summary & Tax Breakdown Card
              item {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.dp, PerformanceRedDark)
                ) {
                  Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                      text = "RÉCAPITULATIF FINANCIER & TOTALISATION",
                      style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = AluminumMuted,
                        letterSpacing = 1.sp
                      )
                    )

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(text = "Sous-total Pièces de rechange :", style = MaterialTheme.typography.bodySmall.copy(color = OffWhite))
                      Text(text = "${df.format(partsSubtotalDzd)} DZD", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = PureWhite))
                    }

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(text = "Sous-total Main d'œuvre & Services :", style = MaterialTheme.typography.bodySmall.copy(color = OffWhite))
                      Text(text = "${df.format(totalLaborAndServicesDzd)} DZD", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = PureWhite))
                    }

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                          checked = applyTva19,
                          onCheckedChange = { applyTva19 = it },
                          colors = CheckboxDefaults.colors(checkedColor = PerformanceRed)
                        )
                        Text(text = "TVA Légale (19%) :", style = MaterialTheme.typography.bodySmall.copy(color = OffWhite))
                      }
                      Text(
                        text = if (applyTva19) "${df.format(tvaAmountDzd)} DZD" else "0 DZD (Exonéré)",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = PureWhite)
                      )
                    }

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(text = "Droit de Timbre fiscal :", style = MaterialTheme.typography.bodySmall.copy(color = OffWhite))
                      Text(text = "${df.format(fiscalStampDzd)} DZD", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = PureWhite))
                    }

                    Divider(color = MetalBorder, thickness = 1.dp)

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text(
                          text = "TOTAL NET À PAYER (TTC)",
                          style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = PureWhite
                          )
                        )
                        Text(
                          text = "Paiement à réception / CIB / Espèces",
                          style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 10.5.sp)
                        )
                      }

                      Text(
                        text = "${df.format(netTotalTtcDzd)} DZD",
                        style = MaterialTheme.typography.headlineMedium.copy(
                          fontWeight = FontWeight.Black,
                          color = PerformanceRedGlow,
                          fontSize = 20.sp
                        )
                      )
                    }
                  }
                }
              }

              // 4-B. Quick Summary Card for Bulbs & Vehicle Interior/Exterior Cleanliness in Tab 1
              item {
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { currentTab = QuoteGeneratorTab.CHECKLIST_AMPOULES_ETAT }
                    .testTag("card_open_bulbs_checklist_from_tab1"),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFF221A14)),
                  border = BorderStroke(1.2.dp, MechanicalOrange)
                ) {
                  Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                          Text(
                            text = "CHECKLIST OR : TOUTES LES AMPOULES & ÉTAT INTÉRIEUR/EXTÉRIEUR",
                            style = MaterialTheme.typography.labelSmall.copy(
                              fontWeight = FontWeight.Black,
                              color = PureWhite,
                              fontSize = 11.sp
                            )
                          )
                          Text(
                            text = orChecklist.buildChecklistSummaryLine(),
                            style = MaterialTheme.typography.bodySmall.copy(
                              color = MechanicalOrange,
                              fontSize = 10.5.sp,
                              fontWeight = FontWeight.Bold
                            )
                          )
                        }
                      }
                      Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MechanicalOrange
                      ) {
                        Text(
                          text = "VÉRIFIER",
                          color = PureWhite,
                          fontWeight = FontWeight.Black,
                          fontSize = 9.5.sp,
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                      }
                    }

                    if (orChecklist.burntOrReplacedBulbs.isNotEmpty()) {
                      Text(
                        text = "Ampoules grillées / remplacées : " +
                          orChecklist.burntOrReplacedBulbs.joinToString(" • ") {
                            "${it.positionName} (${it.status.shortLabel})"
                          },
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = AluminumLight,
                          fontSize = 10.sp
                        )
                      )
                    }
                  }
                }
              }

              item {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.dp, MetalBorder)
                ) {
                  Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notes, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                          text = "DIAGNOSTIC, SYMPTÔMES & MENTIONS",
                          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, color = AluminumMuted)
                        )
                      }

                      // Voice Dictation & 5-Language AI Translation Button
                      Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PerformanceRed.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, PerformanceRedGlow),
                        modifier = Modifier.clickable {
                          onOpenVoiceDictation?.invoke()
                        }.testTag("btn_trigger_voice_dictation")
                      ) {
                        Row(
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Icon(Icons.Default.Translate, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(14.dp))
                          Spacer(modifier = Modifier.width(4.dp))
                          Text(
                            text = "🎤 VOIX IA (FR•AR•EN•ES•ZH)",
                            style = MaterialTheme.typography.labelSmall.copy(
                              color = PureWhite,
                              fontWeight = FontWeight.Bold,
                              fontSize = 10.sp
                            )
                          )
                        }
                      }
                    }

                    OutlinedTextField(
                      value = quoteNotes,
                      onValueChange = { quoteNotes = it },
                      label = { Text("Description des pannes, réparations & conditions") },
                      modifier = Modifier.fillMaxWidth().testTag("input_quote_notes"),
                      maxLines = 4
                    )

                    // Quick Symptom Preset Chips
                    Text(
                      text = "Ajout rapide de pannes fréquentes :",
                      style = MaterialTheme.typography.labelSmall.copy(color = AluminumGray, fontSize = 10.sp)
                    )

                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      val quickSymptoms = listOf(
                        "Claquement distribution à froid",
                        "Voyant FAP / Perte puissance",
                        "Sifflement turbo en accélération",
                        "Vibration freinage à haute vitesse",
                        "Pédale embrayage dure / Patinage",
                        "Climatisation ne refroidit plus"
                      )
                      quickSymptoms.forEach { symptom ->
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = GraphiteBlack,
                          border = BorderStroke(0.5.dp, MetalBorder),
                          modifier = Modifier.clickable {
                            quoteNotes = if (quoteNotes.isBlank()) symptom else "$quoteNotes\n• $symptom"
                          }
                        ) {
                          Text(
                            text = "+ $symptom",
                            style = MaterialTheme.typography.labelSmall.copy(color = AluminumLight, fontSize = 10.sp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                          )
                        }
                      }
                    }
                  }
                }
                Spacer(modifier = Modifier.height(10.dp))
              }

              // 6. Signature Électronique en bas de l'Ordre de Réparation (onglet Édition)
              item {
                RepairOrderBottomElectronicSignatureCard(
                  checklist = orChecklist,
                  defaultSignerName = clientName.ifBlank { "Client Atelier" },
                  onChecklistChange = { orChecklist = it },
                  onSignedCallback = { signer, timeFormatted ->
                    val sigMention = "✍️ Signé électroniquement par $signer le $timeFormatted"
                    if (!quoteNotes.contains("Signé électroniquement")) {
                      quoteNotes = if (quoteNotes.isBlank()) sigMention else "$quoteNotes\n• $sigMention"
                    }
                  }
                )
                Spacer(modifier = Modifier.height(12.dp))
              }
            }
          }

          QuoteGeneratorTab.CHECKLIST_AMPOULES_ETAT -> {
            // TAB 2: PHOTO AUTO-FILL, 5-LANGUAGE VOICE AI, DASHBOARD WARNING LIGHTS, TIRES, 23 BULBS & ELECTRONIC SIGNATURE
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(16.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              item {
                RepairOrderChecklistAndBulbsPanel(
                  checklist = orChecklist,
                  clientName = clientName,
                  clientPhone = clientPhone,
                  vehicleModel = "${activeVehicle.brand} ${activeVehicle.model} (${activeVehicle.exactYear})",
                  orderOrQuoteNumber = generatedQuoteNumber,
                  onChecklistChange = { orChecklist = it },
                  onSyncBulbReplacementItem = { bulb, shouldAdd ->
                    val repairItem = bulb.toRepairItem()
                    val existingIdx = quoteItems.indexOfFirst { it.reference == repairItem.reference }
                    if (shouldAdd && existingIdx == -1) {
                      quoteItems.add(repairItem)
                    } else if (!shouldAdd && existingIdx != -1) {
                      quoteItems.removeAt(existingIdx)
                    }
                  },
                  onAppendClientAlertToOrderNotes = { alertSummary ->
                    if (!quoteNotes.contains(alertSummary.take(35))) {
                      quoteNotes = if (quoteNotes.isBlank()) alertSummary else "$quoteNotes\n• $alertSummary"
                    }
                  },
                  onAutoFillReceptionFields = { plate, _, mileage, _, newClientName, _ ->
                    if (plate.isNotBlank()) plateNumber = plate
                    if (mileage.isNotBlank()) vehicleMileage = mileage
                    if (!newClientName.isNullOrBlank()) clientName = newClientName
                  }
                )
              }
            }
          }

          QuoteGeneratorTab.PREVISUALISATION_PDF -> {
            // TAB 2: HIGH-FIDELITY PDF PREVIEW CANVAS
            Column(
              modifier = Modifier
                .fillMaxSize()
                .background(GraphiteDark)
            ) {
              // PDF Action Bar
              Surface(
                color = MetalSurface,
                border = BorderStroke(1.dp, MetalBorder)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Print, contentDescription = null, tint = PureWhite, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "Document Format A4 Normalisé",
                      style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold)
                    )
                  }

                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                      onClick = triggerPdfDownload,
                      colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                      shape = RoundedCornerShape(6.dp),
                      modifier = Modifier.testTag("btn_export_pdf_top")
                    ) {
                      Icon(Icons.Default.Download, contentDescription = null, tint = PureWhite, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Télécharger PDF", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                      onClick = {
                        try {
                          val result = PdfGeneratorUtil.generateWorkshopQuotePdf(
                            context = context,
                            quoteNumber = generatedQuoteNumber,
                            clientName = clientName,
                            clientPhone = clientPhone,
                            clientAddress = clientAddress,
                            plateNumber = plateNumber,
                            vehicleModel = "${activeVehicle.brand} ${activeVehicle.model} (${activeVehicle.exactYear})",
                            vehicleMileage = vehicleMileage,
                            quoteNotes = quoteNotes,
                            items = quoteItems.toList(),
                            laborHours = laborHours,
                            laborRatePerHour = laborRatePerHourDzd,
                            diagnosticCostDzd = diagnosticCostDzd,
                            suppliesCostDzd = suppliesCostDzd,
                            applyTva19 = applyTva19,
                            fiscalStampDzd = fiscalStampDzd
                          )
                          PdfGeneratorUtil.sharePdfFile(context, result.file, "Devis Mekanik+ $generatedQuoteNumber")
                        } catch (e: Exception) {
                          pdfExportSuccessMessage = "Erreur partage : ${e.message}"
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = GraphiteBlack),
                      border = BorderStroke(1.dp, TechCyan),
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                      shape = RoundedCornerShape(6.dp),
                      modifier = Modifier.testTag("btn_share_pdf_top")
                    ) {
                      Icon(Icons.Default.Share, contentDescription = null, tint = TechCyan, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Partager", color = TechCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }

              if (pdfExportSuccessMessage != null) {
                Surface(
                  color = ElectricGreen.copy(alpha = 0.2f),
                  border = BorderStroke(1.dp, ElectricGreen),
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = pdfExportSuccessMessage!!,
                      style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold)
                    )
                  }
                }
              }

              // Scrollable Printable PDF Sheet (Simulating real white paper with clean contrast)
              LazyColumn(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(horizontal = 12.dp, vertical = 8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
              ) {
                item {
                  Surface(
                    modifier = Modifier
                      .fillMaxWidth()
                      .shadow(8.dp, RoundedCornerShape(4.dp)),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White
                  ) {
                    Column(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                      verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                      // 1. Garage Letterhead Header
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                      ) {
                        Column {
                          Text(
                            text = "MEKANIK+ ATELIER PRO",
                            style = MaterialTheme.typography.titleMedium.copy(
                              fontWeight = FontWeight.Black,
                              color = Color(0xFF101418),
                              letterSpacing = 1.sp
                            )
                          )
                          Text(
                            text = "SARL GARAGE AUTO PERFORMANCE",
                            style = MaterialTheme.typography.labelMedium.copy(
                              fontWeight = FontWeight.Bold,
                              color = Color(0xFFE53935)
                            )
                          )
                          Text(
                            text = "12 Rue des Frères Bouadou, Bir Mourad Raïs, Alger\nTél : 023 45 67 89 • Mobile : 0550 00 11 22\nEmail : atelier@mekanikplus.dz • Web : mekanikplus.dz",
                            style = MaterialTheme.typography.bodySmall.copy(
                              color = Color(0xFF4A5568),
                              fontSize = 9.5.sp,
                              lineHeight = 13.sp
                            )
                          )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                          Surface(
                            color = Color(0xFF101418),
                            shape = RoundedCornerShape(4.dp)
                          ) {
                            Text(
                              text = "ORDRE DE RÉPARATION & DEVIS",
                              style = MaterialTheme.typography.labelMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                              ),
                              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                          }
                          Spacer(modifier = Modifier.height(4.dp))
                          Text(
                            text = generatedQuoteNumber,
                            style = MaterialTheme.typography.titleSmall.copy(
                              fontWeight = FontWeight.Bold,
                              color = Color(0xFF101418),
                              fontFamily = FontFamily.Monospace
                            )
                          )
                          Text(
                            text = "Date : 17 Août 2026",
                            style = MaterialTheme.typography.bodySmall.copy(
                              color = Color(0xFF4A5568),
                              fontSize = 10.sp
                            )
                          )
                          Text(
                            text = "Validité : 30 jours",
                            style = MaterialTheme.typography.bodySmall.copy(
                              color = Color(0xFF718096),
                              fontSize = 9.sp
                            )
                          )
                        }
                      }

                      Divider(color = Color(0xFFCBD5E0), thickness = 1.dp)

                      // 2. Client & Vehicle Metadata Boxes
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                      ) {
                        // Client Box
                        Surface(
                          color = Color(0xFFF7FAFC),
                          shape = RoundedCornerShape(4.dp),
                          border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                          modifier = Modifier.weight(1f)
                        ) {
                          Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                              text = "CLIENT",
                              style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFE53935),
                                fontSize = 9.sp
                              )
                            )
                            Text(
                              text = clientName,
                              style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A202C)
                              )
                            )
                            Text(
                              text = "Tél : $clientPhone\n$clientAddress",
                              style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF4A5568),
                                fontSize = 9.5.sp
                              )
                            )
                          }
                        }

                        // Vehicle Box
                        Surface(
                          color = Color(0xFFF7FAFC),
                          shape = RoundedCornerShape(4.dp),
                          border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                          modifier = Modifier.weight(1f)
                        ) {
                          Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                              text = "VÉHICULE",
                              style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF2B6CB0),
                                fontSize = 9.sp
                              )
                            )
                            Text(
                              text = "${activeVehicle.brand} ${activeVehicle.model}",
                              style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A202C)
                              )
                            )
                            Text(
                              text = "Immat : $plateNumber • ${vehicleMileage} KM\nVIN : ${activeVehicle.vin}\nMoteur : ${activeVehicle.engineCode} (${activeVehicle.powerCh} ch)",
                              style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF4A5568),
                                fontSize = 9.sp,
                                lineHeight = 12.sp
                              )
                            )
                          }
                        }
                      }

                      // 3. Structured Line-Item Table
                      Surface(
                        color = Color(0xFFEDF2F7),
                        shape = RoundedCornerShape(2.dp)
                      ) {
                        Row(
                          modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                          horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                          Text("DÉSIGNATION / RÉFÉRENCE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF2D3748), fontSize = 9.sp), modifier = Modifier.weight(2f))
                          Text("QTE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF2D3748), fontSize = 9.sp), modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                          Text("P.U HT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF2D3748), fontSize = 9.sp), modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                          Text("TOTAL HT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF2D3748), fontSize = 9.sp), modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                        }
                      }

                      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        quoteItems.forEach { item ->
                          Row(
                            modifier = Modifier
                              .fillMaxWidth()
                              .padding(horizontal = 6.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                          ) {
                            Column(modifier = Modifier.weight(2f)) {
                              Text(text = item.name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1A202C), fontSize = 10.sp))
                              Text(text = "Réf. OEM: ${item.reference}", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF718096), fontSize = 8.5.sp))
                            }
                            Text(text = "${item.quantity}", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2D3748), fontSize = 10.sp), modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                            Text(text = "${df.format(item.priceDzd)} DA", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2D3748), fontSize = 10.sp), modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                            Text(text = "${df.format(item.priceDzd * item.quantity)} DA", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1A202C), fontSize = 10.sp), modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                          }
                          Divider(color = Color(0xFFEDF2F7), thickness = 0.5.dp)
                        }

                        // Labor Line
                        Row(
                          modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Column(modifier = Modifier.weight(2f)) {
                            Text(text = "Main d'œuvre atelier mécanique", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1A202C), fontSize = 10.sp))
                            Text(text = laborDescription, style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF718096), fontSize = 8.5.sp))
                          }
                          Text(text = "${laborHours} h", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2D3748), fontSize = 10.sp), modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                          Text(text = "${df.format(laborRatePerHourDzd)} DA", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2D3748), fontSize = 10.sp), modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                          Text(text = "${df.format(laborBaseTotalDzd)} DA", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1A202C), fontSize = 10.sp), modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                        }

                        if (includeDiagnosticEcu) {
                          Divider(color = Color(0xFFEDF2F7), thickness = 0.5.dp)
                          Row(
                            modifier = Modifier
                              .fillMaxWidth()
                              .padding(horizontal = 6.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                          ) {
                            Text(text = "Diagnostic Électronique Valise & DTC", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF1A202C), fontSize = 10.sp), modifier = Modifier.weight(2f))
                            Text(text = "1", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2D3748), fontSize = 10.sp), modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                            Text(text = "2 500 DA", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2D3748), fontSize = 10.sp), modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                            Text(text = "2 500 DA", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1A202C), fontSize = 10.sp), modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                          }
                        }

                        if (includeWorkshopSupplies) {
                          Divider(color = Color(0xFFEDF2F7), thickness = 0.5.dp)
                          Row(
                            modifier = Modifier
                              .fillMaxWidth()
                              .padding(horizontal = 6.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                          ) {
                            Text(text = "Consommables & Traitement Déchets", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF1A202C), fontSize = 10.sp), modifier = Modifier.weight(2f))
                            Text(text = "1", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2D3748), fontSize = 10.sp), modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                            Text(text = "1 000 DA", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2D3748), fontSize = 10.sp), modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                            Text(text = "1 000 DA", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1A202C), fontSize = 10.sp), modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                          }
                        }
                      }

                      Divider(color = Color(0xFFCBD5E0), thickness = 1.dp)

                      // 3-B. Checklist d'Ordre de Réparation : État Extérieur/Intérieur & Bilan des Ampoules sur le Document A4
                      Surface(
                        color = Color(0xFFFFFAF0),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, Color(0xFFED8936)),
                        modifier = Modifier.fillMaxWidth()
                      ) {
                        Column(
                          modifier = Modifier.padding(8.dp),
                          verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                          Text(
                            text = "CHECKLIST RÉCEPTION OR : ÉTAT DU VÉHICULE (INT./EXT.) & CONTRÔLE DES ${orChecklist.bulbs.size} AMPOULES",
                            style = MaterialTheme.typography.labelSmall.copy(
                              fontWeight = FontWeight.Black,
                              color = Color(0xFFC05621),
                              fontSize = 8.5.sp
                            )
                          )
                          Text(
                            text = "• État Extérieur : ${orChecklist.exteriorCleanliness.label} | État Intérieur : ${orChecklist.interiorCleanliness.label} | Carburant : ${orChecklist.fuelLevelFormatted} | Housse & Tapis : ${if (orChecklist.protectiveCoversInstalled) "Oui" else "Non"}",
                            style = MaterialTheme.typography.bodySmall.copy(
                              color = Color(0xFF2D3748),
                              fontSize = 8.5.sp,
                              fontWeight = FontWeight.SemiBold
                            )
                          )
                          Text(
                            text = "• Ampoules contrôlées : ${orChecklist.okBulbsCount}/${orChecklist.bulbs.size} fonctionnelles | " +
                              if (orChecklist.burntOrReplacedBulbs.isEmpty()) {
                                "Aucune ampoule grillée (RAS)"
                              } else {
                                "Grillées / Remplacées : " + orChecklist.burntOrReplacedBulbs.joinToString(", ") {
                                  "${it.positionName} [${it.status.shortLabel}]"
                                }
                              },
                            style = MaterialTheme.typography.bodySmall.copy(
                              color = Color(0xFF2D3748),
                              fontSize = 8.5.sp
                            )
                          )
                          Text(
                            text = "• Voyants Tableau de Bord (Véhicule Allumé) : " +
                              if (orChecklist.illuminatedWarningLights.isEmpty()) "Aucun voyant allumé (RAS)"
                              else orChecklist.illuminatedWarningLights.joinToString(", ") { "${it.symbolEmoji} ${it.name}" },
                            style = MaterialTheme.typography.bodySmall.copy(
                              color = Color(0xFF2D3748),
                              fontSize = 8.5.sp,
                              fontWeight = FontWeight.SemiBold
                            )
                          )
                          Text(
                            text = "• Pneumatiques (4 Pneus + Secours) : " +
                              orChecklist.tires.joinToString(" | ") { "${it.positionLabel}: ${it.treadDepthMm}mm (${it.pressureBar}b)" },
                            style = MaterialTheme.typography.bodySmall.copy(
                              color = Color(0xFF2D3748),
                              fontSize = 8.sp
                            )
                          )
                          if (orChecklist.aiTranslatedProblemSummary.isNotBlank()) {
                            Text(
                              text = "• Traduction Vocale IA (${orChecklist.voiceLanguage.labelFr}) : ${orChecklist.aiTranslatedProblemSummary}",
                              style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF1E40AF),
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                              )
                            )
                          }
                        }
                      }

                      // 4. Financial Summary Right Aligned
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                      ) {
                        Column(
                          modifier = Modifier.width(220.dp),
                          verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Pièces HT :", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF718096), fontSize = 10.sp))
                            Text("${df.format(partsSubtotalDzd)} DA", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2D3748), fontSize = 10.sp))
                          }
                          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total M.O & Services HT :", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF718096), fontSize = 10.sp))
                            Text("${df.format(totalLaborAndServicesDzd)} DA", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2D3748), fontSize = 10.sp))
                          }
                          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Net HT :", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1A202C), fontSize = 10.5.sp))
                            Text("${df.format(netHtDzd)} DA", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1A202C), fontSize = 10.5.sp))
                          }
                          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("TVA (19%) :", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF718096), fontSize = 10.sp))
                            Text("${df.format(tvaAmountDzd)} DA", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2D3748), fontSize = 10.sp))
                          }
                          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Timbre fiscal :", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF718096), fontSize = 10.sp))
                            Text("${df.format(fiscalStampDzd)} DA", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF2D3748), fontSize = 10.sp))
                          }

                          Surface(
                            color = Color(0xFF101418),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.fillMaxWidth()
                          ) {
                            Row(
                              modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                              horizontalArrangement = Arrangement.SpaceBetween,
                              verticalAlignment = Alignment.CenterVertically
                            ) {
                              Text("TOTAL TTC :", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black, color = Color.White, fontSize = 11.sp))
                              Text("${df.format(netTotalTtcDzd)} DZD", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black, color = Color(0xFFFF7A00), fontSize = 13.sp))
                            }
                          }
                        }
                      }

                      // 5. Signatures & QR Code Section (with Electronic Signature at the bottom of the OR)
                      Spacer(modifier = Modifier.height(4.dp))
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                      ) {
                        // Stamp & Agreement
                        Column(modifier = Modifier.weight(1.3f)) {
                          Text(
                            text = "Signature Électronique Client & Bon pour accord OR :",
                            style = MaterialTheme.typography.labelSmall.copy(
                              fontWeight = FontWeight.Bold,
                              color = Color(0xFF2D3748),
                              fontSize = 9.sp
                            )
                          )
                          Text(
                            text = "État contradictoire (voyants, pneus, ampoules, propreté) et travaux approuvés",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF718096), fontSize = 8.sp)
                          )
                          Spacer(modifier = Modifier.height(10.dp))
                          if (orChecklist.isElectronicallySigned) {
                            Text(
                              text = "✓ SIGNÉ ÉLECTRONIQUEMENT PAR : ${orChecklist.electronicSignatureSignerName} (${orChecklist.electronicSignatureTimestamp})",
                              style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF15803D),
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp
                              )
                            )
                          } else {
                            Text(
                              text = "Signature : ________________________",
                              style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E0), fontSize = 9.sp)
                            )
                          }
                        }

                        // Garage Cachet / QR Seal
                        Column(
                          modifier = Modifier.weight(0.9f),
                          horizontalAlignment = Alignment.End
                        ) {
                          Surface(
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, Color(0xFFE53935).copy(alpha = 0.5f)),
                            color = Color(0xFFFFF5F5)
                          ) {
                            Column(
                              modifier = Modifier.padding(6.dp),
                              horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                              Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color(0xFF101418), modifier = Modifier.size(36.dp))
                              Text("DEVIS CERTIFIÉ MEKANIK+", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFE53935), fontSize = 7.5.sp, fontWeight = FontWeight.Bold))
                              Text("RC: 16/00-0984210B22", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF718096), fontSize = 7.sp))
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Sub-dialog: Add Custom Part to Quote
  if (showAddPartDialog) {
    AlertDialog(
      onDismissRequest = { showAddPartDialog = false },
      title = {
        Text("AJOUTER UNE PIÈCE AU DEVIS", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = PureWhite))
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = newPartName,
            onValueChange = { newPartName = it },
            label = { Text("Désignation de la pièce") },
            placeholder = { Text("Ex: Kit Courroie Accessoire") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = newPartRef,
            onValueChange = { newPartRef = it },
            label = { Text("Référence Constructeur / OEM") },
            placeholder = { Text("Ex: 6PK 1070") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = newPartQty,
              onValueChange = { newPartQty = it },
              label = { Text("Quantité") },
              singleLine = true,
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = newPartPriceDzd,
              onValueChange = { newPartPriceDzd = it },
              label = { Text("Prix Unit. (DZD)") },
              singleLine = true,
              modifier = Modifier.weight(1.5f)
            )
          }
        }
      },
      confirmButton = {
        MekanikPrimaryButton(
          text = "AJOUTER AU DEVIS",
          onClick = {
            if (newPartName.isNotBlank()) {
              val qty = newPartQty.toIntOrNull() ?: 1
              val price = newPartPriceDzd.toIntOrNull() ?: 0
              quoteItems.add(
                RepairItem(
                  name = newPartName,
                  reference = newPartRef.ifBlank { "GEN-OEM-${(100..999).random()}" },
                  quantity = qty,
                  priceDzd = price
                )
              )
              newPartName = ""
              newPartRef = ""
              showAddPartDialog = false
            }
          }
        )
      },
      dismissButton = {
        TextButton(onClick = { showAddPartDialog = false }) {
          Text("Annuler", color = AluminumGray)
        }
      },
      containerColor = MetalCard
    )
  }
}
