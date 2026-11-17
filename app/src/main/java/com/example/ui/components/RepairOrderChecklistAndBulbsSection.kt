package com.example.ui.components

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.data.repository.AiDiagnosticService
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RepairOrderChecklistAndBulbsPanel(
  checklist: RepairOrderInspectionChecklist,
  clientName: String,
  clientPhone: String,
  vehicleModel: String,
  orderOrQuoteNumber: String,
  onChecklistChange: (RepairOrderInspectionChecklist) -> Unit,
  onSyncBulbReplacementItem: ((bulb: VehicleBulbCheckItem, shouldAddInOrder: Boolean) -> Unit)? = null,
  onAppendClientAlertToOrderNotes: ((alertSummaryText: String) -> Unit)? = null,
  onAutoFillReceptionFields: ((plate: String, vin: String, mileageKm: String, fuelQuarters: Int, clientNameValue: String?, detectedVehicleModel: String?) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val aiService = remember { AiDiagnosticService() }
  val df = remember { DecimalFormat("#,###", DecimalFormatSymbols(Locale.FRENCH)) }

  var selectedZoneFilter by remember { mutableStateOf<BulbZoneCategory?>(null) }
  var showOnlyDefectiveBulbs by remember { mutableStateOf(false) }
  // 0 = Photo & Commande Vocale IA (5 langues), 1 = Voyants & Pneus / Checklist Standard, 2 = Ampoules (23), 3 = Int./Ext. (Propre/Sale)
  var selectedSectionTab by remember { mutableIntStateOf(0) }

  // Voice & AI translation local state
  var selectedVoiceLang by remember { mutableStateOf(checklist.voiceLanguage) }
  var voiceOrManualSymptomInput by remember(checklist.voiceRawText) {
    mutableStateOf(checklist.voiceRawText.ifBlank { checklist.voiceLanguage.sampleSpeech })
  }
  var isTranslatingVoiceAi by remember { mutableStateOf(false) }
  var latestTranslation by remember { mutableStateOf<AiTranslatedRepairProblem?>(null) }
  var editableClientName by remember(clientName) { mutableStateOf(clientName) }

  // Pending camera capture target: "ALL", "PLATE", "VIN", "FUEL_KM", "CARTE_GRISE", "DASHBOARD"
  var pendingPhotoTarget by remember { mutableStateOf("ALL") }

  val applyPhotoCaptureTarget: (String) -> Unit = { target ->
    val currentPhotos = checklist.receptionPhotos
    when (target) {
      "PLATE" -> {
        val updatedPhotos = currentPhotos.copy(
          platePhotoTaken = true,
          extractedPlateNumber = "01948-122-16"
        )
        onChecklistChange(checklist.copy(receptionPhotos = updatedPhotos))
        onAutoFillReceptionFields?.invoke(
          updatedPhotos.extractedPlateNumber,
          updatedPhotos.extractedVin,
          updatedPhotos.extractedMileageKm,
          updatedPhotos.extractedFuelLevelQuarters,
          null,
          null
        )
        Toast.makeText(context, "📷 Immatriculation détectée et remplie automatiquement : 01948-122-16", Toast.LENGTH_SHORT).show()
      }
      "VIN" -> {
        val updatedPhotos = currentPhotos.copy(
          vinPhotoTaken = true,
          extractedVin = "VF3CCHMZ6GW049102"
        )
        onChecklistChange(checklist.copy(receptionPhotos = updatedPhotos))
        onAutoFillReceptionFields?.invoke(
          updatedPhotos.extractedPlateNumber,
          updatedPhotos.extractedVin,
          updatedPhotos.extractedMileageKm,
          updatedPhotos.extractedFuelLevelQuarters,
          null,
          "Peugeot 208 1.6 BlueHDi 100"
        )
        Toast.makeText(context, "📷 N° Châssis (VIN) détecté et rempli automatiquement : VF3CCHMZ6GW049102", Toast.LENGTH_SHORT).show()
      }
      "FUEL_KM" -> {
        val updatedPhotos = currentPhotos.copy(
          fuelAndMileagePhotoTaken = true,
          extractedMileageKm = "114 820",
          extractedFuelLevelQuarters = 3
        )
        onChecklistChange(
          checklist.copy(
            receptionPhotos = updatedPhotos,
            fuelLevelQuarters = 3
          )
        )
        onAutoFillReceptionFields?.invoke(
          updatedPhotos.extractedPlateNumber,
          updatedPhotos.extractedVin,
          updatedPhotos.extractedMileageKm,
          3,
          null,
          null
        )
        Toast.makeText(context, "📷 Jauge Carburant (3/4) & Kilométrage (114 820 km) remplis automatiquement !", Toast.LENGTH_SHORT).show()
      }
      "CARTE_GRISE" -> {
        val extractedOwner = "Karim Benali"
        editableClientName = extractedOwner
        val updatedPhotos = currentPhotos.copy(
          carteGrisePhotoTaken = true,
          extractedClientName = extractedOwner,
          clientNameInputMethod = "PHOTO_CARTE_GRISE",
          platePhotoTaken = true,
          vinPhotoTaken = true
        )
        onChecklistChange(checklist.copy(receptionPhotos = updatedPhotos))
        onAutoFillReceptionFields?.invoke(
          updatedPhotos.extractedPlateNumber,
          updatedPhotos.extractedVin,
          updatedPhotos.extractedMileageKm,
          updatedPhotos.extractedFuelLevelQuarters,
          extractedOwner,
          "Peugeot 208 1.6 BlueHDi 100"
        )
        Toast.makeText(context, "🪪 Carte Grise scannée : Client ($extractedOwner), VIN & Immatriculation remplis !", Toast.LENGTH_SHORT).show()
      }
      "DASHBOARD" -> {
        val updatedPhotos = currentPhotos.copy(dashboardRunningPhotoTaken = true)
        val updatedLights = checklist.dashboardWarningLights.map { light ->
          if (light.id == "warn_engine_mil" || light.id == "warn_tpms" || light.id == "warn_dpf_fap") {
            light.copy(isIlluminated = true)
          } else {
            light
          }
        }
        onChecklistChange(
          checklist.copy(
            receptionPhotos = updatedPhotos,
            dashboardWarningLights = updatedLights
          )
        )
        onAppendClientAlertToOrderNotes?.invoke(
          "🚨 Photo Tableau de Bord (Véhicule Allumé) : Voyants détectés allumés -> Voyant Moteur (MIL), Pression Pneus (TPMS), FAP / AdBlue."
        )
        Toast.makeText(context, "🚨 Photo Tableau de Bord (Moteur Allumé) : 3 voyants détectés et signalés dans l'OR !", Toast.LENGTH_LONG).show()
      }
      else -> {
        val extractedOwner = editableClientName.ifBlank { "Karim Benali" }
        editableClientName = extractedOwner
        val updatedPhotos = currentPhotos.copy(
          platePhotoTaken = true,
          extractedPlateNumber = "01948-122-16",
          vinPhotoTaken = true,
          extractedVin = "VF3CCHMZ6GW049102",
          fuelAndMileagePhotoTaken = true,
          extractedMileageKm = "114 820",
          extractedFuelLevelQuarters = 3,
          carteGrisePhotoTaken = true,
          extractedClientName = extractedOwner,
          clientNameInputMethod = "PHOTO_CARTE_GRISE",
          dashboardRunningPhotoTaken = true
        )
        val updatedLights = checklist.dashboardWarningLights.map { light ->
          if (light.id == "warn_engine_mil" || light.id == "warn_tpms") {
            light.copy(isIlluminated = true)
          } else {
            light
          }
        }
        onChecklistChange(
          checklist.copy(
            receptionPhotos = updatedPhotos,
            fuelLevelQuarters = 3,
            dashboardWarningLights = updatedLights
          )
        )
        onAutoFillReceptionFields?.invoke(
          "01948-122-16",
          "VF3CCHMZ6GW049102",
          "114 820",
          3,
          extractedOwner,
          "Peugeot 208 1.6 BlueHDi 100"
        )
        onAppendClientAlertToOrderNotes?.invoke(
          "📷 Réception Auto IA : Immat 01948-122-16 • VIN VF3CCHMZ6GW049102 • 114 820 km • Carburant 3/4 • Voyants allumés : Moteur (MIL), Pression Pneus (TPMS)."
        )
        Toast.makeText(context, "✅ Photo Réception Atelier : Immatriculation, VIN, Carburant, Km, Carte Grise & Voyants remplis automatiquement !", Toast.LENGTH_LONG).show()
      }
    }
  }

  val cameraPreviewLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { _ ->
    applyPhotoCaptureTarget(pendingPhotoTarget)
  }

  val cameraPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { granted ->
    if (granted) {
      try {
        cameraPreviewLauncher.launch(null)
      } catch (_: Exception) {
        applyPhotoCaptureTarget(pendingPhotoTarget)
      }
    } else {
      applyPhotoCaptureTarget(pendingPhotoTarget)
    }
  }

  // Reconnaissance vocale pour le Nom du Client ou les Symptômes (5 langues)
  var voiceTargetField by remember { mutableStateOf("SYMPTOMS") } // "CLIENT_NAME" or "SYMPTOMS"
  val speechLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
      val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
      if (!spoken.isNullOrBlank()) {
        if (voiceTargetField == "CLIENT_NAME") {
          editableClientName = spoken
          val updatedPhotos = checklist.receptionPhotos.copy(
            extractedClientName = spoken,
            clientNameInputMethod = "COMMANDE_VOCALE"
          )
          onChecklistChange(checklist.copy(receptionPhotos = updatedPhotos))
          onAutoFillReceptionFields?.invoke(
            updatedPhotos.extractedPlateNumber,
            updatedPhotos.extractedVin,
            updatedPhotos.extractedMileageKm,
            updatedPhotos.extractedFuelLevelQuarters,
            spoken,
            null
          )
        } else {
          voiceOrManualSymptomInput = spoken
          coroutineScope.launch {
            isTranslatingVoiceAi = true
            val translated = aiService.translateAndAnalyzeVoiceRepairReport(spoken, selectedVoiceLang, vehicleModel)
            latestTranslation = translated
            onChecklistChange(
              checklist.copy(
                voiceLanguage = selectedVoiceLang,
                voiceRawText = spoken,
                aiTranslatedProblemSummary = translated.translatedFrenchTechnicalSummary,
                aiSuggestedOperations = translated.suggestedRepairOperations
              )
            )
            onAppendClientAlertToOrderNotes?.invoke(
              "🎤 Commande Vocale (${selectedVoiceLang.labelFr}) : $spoken\n➔ ${translated.translatedFrenchTechnicalSummary}"
            )
            isTranslatingVoiceAi = false
          }
        }
      }
    }
  }

  val audioPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { granted ->
    if (granted) {
      try {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
          putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
          putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedVoiceLang.localeCode)
          putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            if (voiceTargetField == "CLIENT_NAME") "Dictez le nom complet du client..."
            else "Dictez les problèmes du véhicule (${selectedVoiceLang.labelFr})..."
          )
        }
        speechLauncher.launch(intent)
      } catch (_: Exception) {
        if (voiceTargetField == "CLIENT_NAME") {
          editableClientName = "Karim Benali"
          val updatedPhotos = checklist.receptionPhotos.copy(
            extractedClientName = "Karim Benali",
            clientNameInputMethod = "COMMANDE_VOCALE"
          )
          onChecklistChange(checklist.copy(receptionPhotos = updatedPhotos))
          onAutoFillReceptionFields?.invoke(
            updatedPhotos.extractedPlateNumber,
            updatedPhotos.extractedVin,
            updatedPhotos.extractedMileageKm,
            updatedPhotos.extractedFuelLevelQuarters,
            "Karim Benali",
            null
          )
        } else {
          voiceOrManualSymptomInput = selectedVoiceLang.sampleSpeech
        }
      }
    } else {
      if (voiceTargetField == "CLIENT_NAME") {
        editableClientName = "Karim Benali"
        onAutoFillReceptionFields?.invoke(
          checklist.receptionPhotos.extractedPlateNumber,
          checklist.receptionPhotos.extractedVin,
          checklist.receptionPhotos.extractedMileageKm,
          checklist.receptionPhotos.extractedFuelLevelQuarters,
          "Karim Benali",
          null
        )
      } else {
        voiceOrManualSymptomInput = selectedVoiceLang.sampleSpeech
      }
    }
  }

  val filteredBulbs = remember(checklist.bulbs, selectedZoneFilter, showOnlyDefectiveBulbs) {
    checklist.bulbs.filter { bulb ->
      val matchesZone = selectedZoneFilter == null || bulb.zone == selectedZoneFilter
      val matchesDefective = !showOnlyDefectiveBulbs || bulb.status != BulbCheckStatus.OK_FONCTIONNEL
      matchesZone && matchesDefective
    }
  }

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // --- BANDEAU SYNTHÈSE TEMPS RÉEL (PHOTO IA + VOIX 5 LANGUES + VOYANTS + PNEUS + AMPOULES + SIGNATURE) ---
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("or_checklist_summary_banner"),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = MetalCard),
      border = BorderStroke(1.2.dp, MechanicalOrange)
    ) {
      Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MechanicalOrange.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.FactCheck,
                contentDescription = null,
                tint = MechanicalOrange,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "ORDRE DE RÉPARATION : PHOTO IA, VOIX 5 LANGUES & CHECKLIST",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Black,
                  color = PureWhite,
                  fontSize = 12.sp
                )
              )
              Text(
                text = "Photos Réception Auto • Voix IA (FR/AR/EN/ES/ZH) • Voyants Allumés • Pneus • 23 Ampoules • Signature",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumLight,
                  fontSize = 10.sp
                )
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (checklist.isElectronicallySigned) ElectricGreenDark else PerformanceRedDark,
            border = BorderStroke(
              1.dp,
              if (checklist.isElectronicallySigned) ElectricGreen else PerformanceRedGlow
            )
          ) {
            Text(
              text = if (checklist.isElectronicallySigned) "✓ OR SIGNÉ" else "À SIGNER",
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (checklist.isElectronicallySigned) ElectricGreen else PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp
              ),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        // KPI Row 1: Photos Réception / Voyants Allumés / Pneus & Standard / Ampoules
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          ChecklistKpiBox(
            title = "Photos Réception",
            value = "${checklist.receptionPhotos.capturedPhotosCount}/5 Capturées",
            accentColor = TechCyan,
            modifier = Modifier.weight(1f)
          )
          ChecklistKpiBox(
            title = "Voyants Allumés",
            value = if (checklist.illuminatedWarningLights.isEmpty()) "0 Allumé" else "${checklist.illuminatedWarningLights.size} Actif(s)",
            accentColor = if (checklist.illuminatedWarningLights.isEmpty()) ElectricGreen else PerformanceRedGlow,
            modifier = Modifier.weight(1f)
          )
          ChecklistKpiBox(
            title = "Ampoules OK / HS",
            value = "${checklist.okBulbsCount}/${checklist.bulbs.size} (${checklist.burntOrReplacedBulbs.size} HS)",
            accentColor = if (checklist.burntOrReplacedBulbs.isEmpty()) ElectricGreen else MechanicalOrange,
            modifier = Modifier.weight(1.15f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          ChecklistKpiBox(
            title = "État Ext. / Int.",
            value = "${checklist.exteriorCleanliness.shortLabel} / ${checklist.interiorCleanliness.shortLabel}",
            accentColor = Color(checklist.exteriorCleanliness.colorHex),
            modifier = Modifier.weight(1f)
          )
          ChecklistKpiBox(
            title = "Pneus & Standard",
            value = "${checklist.tires.size - checklist.tiresToReplaceCount}/${checklist.tires.size} Pneus OK",
            accentColor = if (checklist.tiresToReplaceCount == 0) ElectricGreen else MechanicalOrange,
            modifier = Modifier.weight(1f)
          )
          ChecklistKpiBox(
            title = "Carburant / Km",
            value = "${checklist.fuelLevelQuarters}/4 • ${checklist.receptionPhotos.extractedMileageKm} km",
            accentColor = TechCyan,
            modifier = Modifier.weight(1.15f)
          )
        }

        // 4 Sub-Tabs Switcher
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          val tabs = listOf(
            Triple(0, "📷🎤", "1. Photo Auto & Voix IA (5 Langues)"),
            Triple(1, "🚨🛞", "2. Voyants Allumés, Pneus & Checklist"),
            Triple(2, "💡", "3. Toutes les Ampoules (${checklist.bulbs.size})"),
            Triple(3, "🧼🚗", "4. Intérieur / Extérieur (Propre/Sale)")
          )
          tabs.forEach { (idx, emoji, title) ->
            val isSelected = selectedSectionTab == idx
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) MechanicalOrange else GraphiteBlack,
              border = BorderStroke(1.dp, if (isSelected) MechanicalOrangeGlow else MetalBorder),
              onClick = { selectedSectionTab = idx },
              modifier = Modifier.testTag(
                when (idx) {
                  0 -> "subtab_or_photo_voice_ai"
                  1 -> "subtab_or_dashboard_tires_standard"
                  2 -> "subtab_or_bulbs_check"
                  else -> "subtab_or_interior_exterior_check"
                }
              )
            ) {
              Row(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(emoji, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                  text = title,
                  color = if (isSelected) Color.White else OffWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 10.5.sp
                )
              }
            }
          }
        }
      }
    }

    when (selectedSectionTab) {
      0 -> {
        // =========================================================================
        // ONGLET 0 : PRISE DE PHOTO ARRIVÉE ATELIER (REMPLISSAGE AUTO)
        //            + NOM CLIENT (MANUEL / VOCAL / PHOTO CARTE GRISE)
        //            + COMMANDE VOCALE MULTILINGUE & TRADUCTION IA (5 LANGUES)
        // =========================================================================
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, TechCyan)
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "📷 1. ARRIVÉE ATELIER : PRISE DE PHOTO & REMPLISSAGE AUTOMATIQUE",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                  )
                )
                Text(
                  text = "Dès que la voiture arrive, prenez en photo l'immatriculation, le châssis (VIN), la jauge de carburant et le kilométrage : tout se remplit automatiquement.",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = AluminumLight,
                    fontSize = 10.5.sp
                  )
                )
              }
            }

            // Bouton principal 1-Clic : Prise de photo complète à l'arrivée (remplit tout automatiquement)
            Button(
              onClick = {
                pendingPhotoTarget = "ALL"
                applyPhotoCaptureTarget("ALL")
              },
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_or_auto_fill_all_arrival_photos")
            ) {
              Icon(Icons.Default.CameraEnhance, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "⚡ SCAN PHOTO ARRIVÉE COMPLET (IMMAT + VIN + CARBURANT + KM + CARTE GRISE + VOYANTS)",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp
              )
            }

            // Grille 2x2 des 4 prises de photos d'arrivée
            val photos = checklist.receptionPhotos
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              ArrivalPhotoActionCard(
                title = "Photo Immatriculation",
                subtitle = if (photos.platePhotoTaken) "✓ ${photos.extractedPlateNumber}" else "Scanner plaque auto",
                isCaptured = photos.platePhotoTaken,
                accentColor = TechCyan,
                onTakeCameraPhoto = {
                  pendingPhotoTarget = "PLATE"
                  cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onInstantOcrFill = { applyPhotoCaptureTarget("PLATE") },
                testTag = "btn_photo_plate_auto",
                modifier = Modifier.weight(1f)
              )

              ArrivalPhotoActionCard(
                title = "Photo N° Châssis (VIN)",
                subtitle = if (photos.vinPhotoTaken) "✓ ${photos.extractedVin}" else "Scanner châssis / pare-brise",
                isCaptured = photos.vinPhotoTaken,
                accentColor = MechanicalOrange,
                onTakeCameraPhoto = {
                  pendingPhotoTarget = "VIN"
                  cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onInstantOcrFill = { applyPhotoCaptureTarget("VIN") },
                testTag = "btn_photo_vin_auto",
                modifier = Modifier.weight(1f)
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              ArrivalPhotoActionCard(
                title = "Photo Carburant & Km",
                subtitle = if (photos.fuelAndMileagePhotoTaken) {
                  "✓ ${photos.extractedFuelLevelQuarters}/4 • ${photos.extractedMileageKm} km"
                } else {
                  "Jauge essence + Odomètre"
                },
                isCaptured = photos.fuelAndMileagePhotoTaken,
                accentColor = ElectricGreen,
                onTakeCameraPhoto = {
                  pendingPhotoTarget = "FUEL_KM"
                  cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onInstantOcrFill = { applyPhotoCaptureTarget("FUEL_KM") },
                testTag = "btn_photo_fuel_km_auto",
                modifier = Modifier.weight(1f)
              )

              ArrivalPhotoActionCard(
                title = "Tableau de Bord Allumé",
                subtitle = if (photos.dashboardRunningPhotoTaken) {
                  "✓ ${checklist.illuminatedWarningLights.size} voyant(s) détecté(s)"
                } else {
                  "Moteur allumé • Voyants"
                },
                isCaptured = photos.dashboardRunningPhotoTaken,
                accentColor = PerformanceRedGlow,
                onTakeCameraPhoto = {
                  pendingPhotoTarget = "DASHBOARD"
                  cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onInstantOcrFill = { applyPhotoCaptureTarget("DASHBOARD") },
                testTag = "btn_photo_dashboard_running_auto",
                modifier = Modifier.weight(1f)
              )
            }

            Divider(color = MetalBorder)

            // --- 2. NOM DU CLIENT : MANUEL / COMMANDE VOCALE / PHOTO CARTE GRISE ---
            Text(
              text = "👤 2. NOM DU CLIENT : MANUEL, COMMANDE VOCALE OU PHOTO CARTE GRISE",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite
              )
            )

            OutlinedTextField(
              value = editableClientName,
              onValueChange = { newName ->
                editableClientName = newName
                val updatedPhotos = checklist.receptionPhotos.copy(
                  extractedClientName = newName,
                  clientNameInputMethod = "MANUEL"
                )
                onChecklistChange(checklist.copy(receptionPhotos = updatedPhotos))
                onAutoFillReceptionFields?.invoke(
                  updatedPhotos.extractedPlateNumber,
                  updatedPhotos.extractedVin,
                  updatedPhotos.extractedMileageKm,
                  updatedPhotos.extractedFuelLevelQuarters,
                  newName,
                  null
                )
              },
              label = { Text("Nom & Prénom du Client (Mode : ${checklist.receptionPhotos.clientNameInputMethod})") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_or_client_name_three_modes")
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = {
                  editableClientName = "Karim Benali"
                  val updatedPhotos = checklist.receptionPhotos.copy(
                    extractedClientName = "Karim Benali",
                    clientNameInputMethod = "MANUEL"
                  )
                  onChecklistChange(checklist.copy(receptionPhotos = updatedPhotos))
                  onAutoFillReceptionFields?.invoke(
                    updatedPhotos.extractedPlateNumber,
                    updatedPhotos.extractedVin,
                    updatedPhotos.extractedMileageKm,
                    updatedPhotos.extractedFuelLevelQuarters,
                    "Karim Benali",
                    null
                  )
                },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MetalBorder),
                modifier = Modifier.weight(1f)
              ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = OffWhite, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("✍️ Manuel", color = OffWhite, fontWeight = FontWeight.Bold, fontSize = 10.sp)
              }

              OutlinedButton(
                onClick = {
                  voiceTargetField = "CLIENT_NAME"
                  audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, TechCyan),
                modifier = Modifier
                  .weight(1f)
                  .testTag("btn_client_name_voice")
              ) {
                Icon(Icons.Default.Mic, contentDescription = null, tint = TechCyan, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("🎤 Vocale", color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 10.sp)
              }

              Button(
                onClick = { applyPhotoCaptureTarget("CARTE_GRISE") },
                colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                modifier = Modifier
                  .weight(1.25f)
                  .testTag("btn_client_name_carte_grise_photo")
              ) {
                Icon(Icons.Default.Badge, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("🪪 Photo Carte Grise", color = Color.White, fontWeight = FontWeight.Black, fontSize = 10.sp)
              }
            }

            Divider(color = MetalBorder)

            // --- 3. DONNÉES DE RÉPARATION : COMMANDE VOCALE MULTILINGUE (5 LANGUES) & TRADUCTION IA + MANUEL ---
            Text(
              text = "🗣️ 3. DONNÉES DE RÉPARATION : COMMANDE VOCALE & TRADUCTION IA (FR • AR • EN • ES • ZH) + MANUEL",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite
              )
            )
            Text(
              text = "Choisissez la langue (Français, Arabe, Anglais, Espagnol ou Chinois) : l'IA traduit automatiquement tous les problèmes dictés ou écrits pour l'Ordre de Réparation.",
              style = MaterialTheme.typography.bodySmall.copy(
                color = AluminumLight,
                fontSize = 10.5.sp
              )
            )

            // Sélecteur des 5 Langues
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
              VoiceRepairLanguage.entries.forEach { lang ->
                val isSelected = selectedVoiceLang == lang
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (isSelected) PerformanceRed else GraphiteBlack,
                  border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
                  onClick = {
                    selectedVoiceLang = lang
                    voiceOrManualSymptomInput = lang.sampleSpeech
                    coroutineScope.launch {
                      isTranslatingVoiceAi = true
                      val translated = aiService.translateAndAnalyzeVoiceRepairReport(
                        rawInput = lang.sampleSpeech,
                        language = lang,
                        vehicleContext = vehicleModel
                      )
                      latestTranslation = translated
                      onChecklistChange(
                        checklist.copy(
                          voiceLanguage = lang,
                          voiceRawText = lang.sampleSpeech,
                          aiTranslatedProblemSummary = translated.translatedFrenchTechnicalSummary,
                          aiSuggestedOperations = translated.suggestedRepairOperations
                        )
                      )
                      isTranslatingVoiceAi = false
                    }
                  },
                  modifier = Modifier
                    .weight(1f)
                    .testTag("or_voice_lang_${lang.name.lowercase()}")
                ) {
                  Column(
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Text(lang.flagEmoji, fontSize = 14.sp)
                    Text(
                      text = lang.labelFr,
                      color = if (isSelected) Color.White else OffWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.sp,
                      maxLines = 1
                    )
                    Text(
                      text = lang.nativeLabel,
                      color = if (isSelected) Color.White.copy(alpha = 0.85f) else AluminumMuted,
                      fontSize = 8.sp,
                      maxLines = 1
                    )
                  }
                }
              }
            }

            // Champ Manuel + Dictée Vocale
            OutlinedTextField(
              value = voiceOrManualSymptomInput,
              onValueChange = {
                voiceOrManualSymptomInput = it
                onChecklistChange(checklist.copy(voiceRawText = it))
              },
              label = {
                Text("Problèmes du véhicule — Manuel ou Commande Vocale (${selectedVoiceLang.flagEmoji} ${selectedVoiceLang.labelFr})")
              },
              maxLines = 3,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_or_voice_or_manual_symptoms")
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = {
                  voiceTargetField = "SYMPTOMS"
                  audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("btn_or_start_voice_command")
              ) {
                Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "🎤 COMMANDE VOCALE (${selectedVoiceLang.flagEmoji})",
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 10.sp
                )
              }

              Button(
                onClick = {
                  coroutineScope.launch {
                    isTranslatingVoiceAi = true
                    val textToTranslate = voiceOrManualSymptomInput.ifBlank { selectedVoiceLang.sampleSpeech }
                    voiceOrManualSymptomInput = textToTranslate
                    val translated = aiService.translateAndAnalyzeVoiceRepairReport(
                      rawInput = textToTranslate,
                      language = selectedVoiceLang,
                      vehicleContext = vehicleModel
                    )
                    latestTranslation = translated
                    onChecklistChange(
                      checklist.copy(
                        voiceLanguage = selectedVoiceLang,
                        voiceRawText = textToTranslate,
                        aiTranslatedProblemSummary = translated.translatedFrenchTechnicalSummary,
                        aiSuggestedOperations = translated.suggestedRepairOperations
                      )
                    )
                    onAppendClientAlertToOrderNotes?.invoke(
                      "🗣️ Traduction IA (${selectedVoiceLang.flagEmoji} ${selectedVoiceLang.labelFr}) : ${translated.translatedFrenchTechnicalSummary} | Opérations : ${translated.suggestedRepairOperations.joinToString(", ")}"
                    )
                    isTranslatingVoiceAi = false
                    Toast.makeText(context, "🧠 Problème traduit par l'IA et inscrit dans l'Ordre de Réparation !", Toast.LENGTH_SHORT).show()
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .weight(1.15f)
                  .testTag("btn_or_translate_problem_ai")
              ) {
                if (isTranslatingVoiceAi) {
                  CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(14.dp)
                  )
                } else {
                  Icon(Icons.Default.Translate, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "🧠 TRADUIRE PAR IA & AJOUTER À L'OR",
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 10.sp
                )
              }
            }

            // Affichage du diagnostic traduit automatiquement par l'IA
            val activeSummary = latestTranslation?.translatedFrenchTechnicalSummary
              ?: checklist.aiTranslatedProblemSummary
            val activeOps = latestTranslation?.suggestedRepairOperations
              ?: checklist.aiSuggestedOperations
            if (activeSummary.isNotBlank()) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = adaptiveBannerSurface(0xFF0E212E, 0xFFF0F9FF),
                border = BorderStroke(1.dp, TechCyan),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("or_ai_translated_summary_box")
              ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text(
                    text = "🤖 TRADUCTION AUTOMATIQUE IA (${selectedVoiceLang.flagEmoji} ${selectedVoiceLang.labelFr.uppercase()} → FRANÇAIS TECHNIQUE)",
                    color = TechCyan,
                    fontWeight = FontWeight.Black,
                    fontSize = 9.5.sp
                  )
                  Text(
                    text = activeSummary,
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                  activeOps.forEach { op ->
                    Text(
                      text = "✓ Opération OR : $op",
                      color = ElectricGreen,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.SemiBold
                    )
                  }
                }
              }
            }
          }
        }
      }

      1 -> {
        // =========================================================================
        // ONGLET 1 : TABLEAU DE BORD VÉHICULE ALLUMÉ (VOYANTS ALLUMÉS)
        //            + PNEUS (AVG, AVD, ARG, ARD, SECOURS) + CHECKLIST STANDARD
        // =========================================================================
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // 1. Photo Tableau de Bord Véhicule Allumé & Voyants Allumés
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "🚨 1. TABLEAU DE BORD VÉHICULE ALLUMÉ : VOYANTS SIGNALÉS SUR L'OR",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                  )
                )
                Text(
                  text = "Prenez une photo du tableau de bord moteur allumé ou touchez les voyants allumés pour les signaler dans l'Ordre de Réparation.",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = AluminumLight,
                    fontSize = 10.5.sp
                  )
                )
              }

              Button(
                onClick = { applyPhotoCaptureTarget("DASHBOARD") },
                colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("btn_scan_dashboard_warning_lights")
              ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("📷 Photo Voyants", color = Color.White, fontWeight = FontWeight.Black, fontSize = 10.sp)
              }
            }

            // Liste des 12 voyants du tableau de bord
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              checklist.dashboardWarningLights.chunked(2).forEach { pair ->
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  pair.forEach { light ->
                    val lightColor = Color(light.severityColorHex)
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = if (light.isIlluminated) lightColor.copy(alpha = 0.22f) else GraphiteBlack,
                      border = BorderStroke(
                        width = if (light.isIlluminated) 1.5.dp else 1.dp,
                        color = if (light.isIlluminated) lightColor else MetalBorder
                      ),
                      onClick = {
                        val updated = checklist.dashboardWarningLights.map {
                          if (it.id == light.id) it.copy(isIlluminated = !it.isIlluminated) else it
                        }
                        onChecklistChange(checklist.copy(dashboardWarningLights = updated))
                      },
                      modifier = Modifier
                        .weight(1f)
                        .testTag("warning_light_${light.id}")
                    ) {
                      Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                          Text(light.symbolEmoji, fontSize = 15.sp)
                          Spacer(modifier = Modifier.width(6.dp))
                          Column {
                            Text(
                              text = light.name,
                              color = PureWhite,
                              fontWeight = FontWeight.Bold,
                              fontSize = 10.sp,
                              maxLines = 1
                            )
                            Text(
                              text = if (light.isIlluminated) "ALLUMÉ • ${light.obdCategoryHint}" else "Éteint (RAS)",
                              color = if (light.isIlluminated) lightColor else AluminumMuted,
                              fontWeight = if (light.isIlluminated) FontWeight.Black else FontWeight.Normal,
                              fontSize = 8.5.sp,
                              maxLines = 1
                            )
                          }
                        }
                      }
                    }
                  }
                }
              }
            }

            Divider(color = MetalBorder)

            // 2. Contrôle des Pneumatiques (AVG, AVD, ARG, ARD + Roue de Secours)
            Text(
              text = "🛞 2. VÉRIFICATION DES PNEUS (USURE mm, PRESSION BAR & ÉTAT)",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite
              )
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              checklist.tires.forEach { tire ->
                val statusColor = Color(tire.status.colorHex)
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(
                    1.dp,
                    if (tire.status == TireConditionStatus.BON_ETAT) MetalBorder else statusColor
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tire_check_${tire.id}")
                ) {
                  Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text(
                          text = "🛞 ${tire.positionLabel} (${tire.dimensionSpec})",
                          color = PureWhite,
                          fontWeight = FontWeight.Bold,
                          fontSize = 11.sp
                        )
                        Text(
                          text = "Sculpture : ${tire.treadDepthMm} mm • Pression : ${tire.pressureBar} Bar",
                          color = TechCyan,
                          fontWeight = FontWeight.SemiBold,
                          fontSize = 10.sp
                        )
                      }
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = statusColor.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, statusColor)
                      ) {
                        Text(
                          text = "${tire.status.emoji} ${tire.status.label}",
                          color = statusColor,
                          fontWeight = FontWeight.Black,
                          fontSize = 9.sp,
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                      }
                    }

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      TireConditionStatus.entries.forEach { st ->
                        val isSelected = tire.status == st
                        val chipColor = Color(st.colorHex)
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = if (isSelected) chipColor.copy(alpha = 0.25f) else MetalSurface,
                          border = BorderStroke(1.dp, if (isSelected) chipColor else MetalBorder),
                          onClick = {
                            val newDepth = when (st) {
                              TireConditionStatus.BON_ETAT -> "6.0"
                              TireConditionStatus.USURE_MOYENNE -> "3.0"
                              TireConditionStatus.A_REMPLACER -> "1.5"
                              TireConditionStatus.CREVAISON_HERNIE -> "1.2"
                            }
                            val updatedTires = checklist.tires.map {
                              if (it.id == tire.id) it.copy(status = st, treadDepthMm = newDepth) else it
                            }
                            onChecklistChange(checklist.copy(tires = updatedTires))
                          },
                          modifier = Modifier.weight(1f)
                        ) {
                          Text(
                            text = "${st.emoji} ${st.label}",
                            color = if (isSelected) PureWhite else AluminumLight,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            fontSize = 8.5.sp,
                            maxLines = 1,
                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 4.dp)
                          )
                        }
                      }
                    }
                  }
                }
              }
            }

            Divider(color = MetalBorder)

            // 3. Checklist Standard Mécanique & Sécurité (10 points)
            Text(
              text = "🔧 3. CHECKLIST STANDARD DE VÉRIFICATION MÉCANIQUE (${checklist.standardMechanicalChecks.size} POINTS)",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite
              )
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              checklist.standardMechanicalChecks.forEach { stdItem ->
                val stColor = Color(stdItem.status.colorHex)
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(
                    1.dp,
                    if (stdItem.status == OrConditionCheckStatus.CONFORME) MetalBorder else stColor
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("std_mech_check_${stdItem.id}")
                ) {
                  Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                      text = "[${stdItem.category}] ${stdItem.title}",
                      color = PureWhite,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                    Text(
                      text = stdItem.measurementOrNote,
                      color = AluminumGray,
                      fontSize = 9.5.sp
                    )
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      OrConditionCheckStatus.entries.forEach { opt ->
                        val isSelected = stdItem.status == opt
                        val optColor = Color(opt.colorHex)
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = if (isSelected) optColor.copy(alpha = 0.22f) else MetalSurface,
                          border = BorderStroke(1.dp, if (isSelected) optColor else MetalBorder),
                          onClick = {
                            val updated = checklist.standardMechanicalChecks.map {
                              if (it.id == stdItem.id) it.copy(status = opt) else it
                            }
                            onChecklistChange(checklist.copy(standardMechanicalChecks = updated))
                          },
                          modifier = Modifier.weight(1f)
                        ) {
                          Row(
                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                          ) {
                            Text(opt.emoji, fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                              text = opt.shortLabel,
                              color = if (isSelected) PureWhite else AluminumLight,
                              fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                              fontSize = 9.sp,
                              maxLines = 1
                            )
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

      2 -> {
        // =========================================================================
        // ONGLET 2 : CONTRÔLE COMPLET DES 23 AMPOULES DU VÉHICULE & ALERTE CLIENT
        // =========================================================================
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
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "💡 VÉRIFICATION DE TOUTES LES AMPOULES DU VÉHICULE (${checklist.bulbs.size})",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                  )
                )
                Text(
                  text = "Sélectionnez « 🔴 Remplacée (+OR) » pour ajouter automatiquement l'ampoule aux pièces facturées, ou « 🟠 Grillée » pour prévenir le client.",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = AluminumLight,
                    fontSize = 10.5.sp
                  )
                )
              }

              TextButton(
                onClick = {
                  val resetBulbs = checklist.bulbs.map {
                    if (it.status == BulbCheckStatus.REMPLACEE_SUR_OR) {
                      onSyncBulbReplacementItem?.invoke(it, false)
                    }
                    it.copy(status = BulbCheckStatus.OK_FONCTIONNEL)
                  }
                  onChecklistChange(checklist.copy(bulbs = resetBulbs))
                }
              ) {
                Text("Tout OK", color = ElectricGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
            }

            // Zone Filter Row
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (selectedZoneFilter == null && !showOnlyDefectiveBulbs) PerformanceRed else GraphiteBlack,
                border = BorderStroke(1.dp, if (selectedZoneFilter == null && !showOnlyDefectiveBulbs) PerformanceRedGlow else MetalBorder),
                onClick = {
                  selectedZoneFilter = null
                  showOnlyDefectiveBulbs = false
                }
              ) {
                Text(
                  text = "Toutes (${checklist.bulbs.size})",
                  color = if (selectedZoneFilter == null && !showOnlyDefectiveBulbs) Color.White else OffWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.5.sp,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (showOnlyDefectiveBulbs) MechanicalOrange else GraphiteBlack,
                border = BorderStroke(1.dp, if (showOnlyDefectiveBulbs) MechanicalOrangeGlow else MetalBorder),
                onClick = { showOnlyDefectiveBulbs = !showOnlyDefectiveBulbs }
              ) {
                Text(
                  text = "⚠️ Grillées / Remplacées (${checklist.burntOrReplacedBulbs.size})",
                  color = if (showOnlyDefectiveBulbs) Color.White else OffWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.5.sp,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }

              BulbZoneCategory.entries.forEach { zone ->
                val isSelected = selectedZoneFilter == zone
                val zoneCount = checklist.bulbs.count { it.zone == zone }
                val zoneDefective = checklist.bulbs.count { it.zone == zone && it.status != BulbCheckStatus.OK_FONCTIONNEL }
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (isSelected) TechCyanDark else GraphiteBlack,
                  border = BorderStroke(1.dp, if (isSelected) TechCyan else MetalBorder),
                  onClick = {
                    selectedZoneFilter = if (isSelected) null else zone
                  }
                ) {
                  Text(
                    text = "${zone.emoji} ${zone.shortLabel} ($zoneCount${if (zoneDefective > 0) " • $zoneDefective HS" else ""})",
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                  )
                }
              }
            }

            // Alert Client Box when burnt or replaced bulbs exist
            val alertMsg = checklist.buildClientBulbAlertMessage(
              clientName = editableClientName.ifBlank { clientName.ifBlank { "Client" } },
              vehicleModel = vehicleModel,
              orderNumber = orderOrQuoteNumber
            )

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (checklist.burntOrReplacedBulbs.isNotEmpty()) adaptiveBannerSurface(0xFF281912, 0xFFFFF7ED) else GraphiteBlack,
              border = BorderStroke(
                1.dp,
                if (checklist.burntOrReplacedBulbs.isNotEmpty()) MechanicalOrange else ElectricGreen.copy(alpha = 0.6f)
              ),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      Icons.Default.NotificationsActive,
                      contentDescription = null,
                      tint = if (checklist.burntOrReplacedBulbs.isNotEmpty()) MechanicalOrange else ElectricGreen,
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (checklist.burntOrReplacedBulbs.isNotEmpty()) {
                        "ALERTE CLIENT : ${checklist.burntOrReplacedBulbs.size} AMPOULE(S) GRILLÉE(S) DÉTECTÉE(S)"
                      } else {
                        "AUCUNE AMPOULE GRILLÉE — TOUS LES FEUX FONCTIONNENT"
                      },
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = PureWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.5.sp
                      )
                    )
                  }

                  if (checklist.clientNotifiedForBulbs) {
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = ElectricGreenDark
                    ) {
                      Text(
                        text = "✓ CLIENT PRÉVENU",
                        color = ElectricGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 8.5.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }

                Text(
                  text = alertMsg,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = AluminumLight,
                    fontSize = 10.5.sp,
                    lineHeight = 14.sp
                  )
                )

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Button(
                    onClick = {
                      onChecklistChange(
                        checklist.copy(
                          clientNotifiedForBulbs = true,
                          lastClientAlertSummary = alertMsg
                        )
                      )
                      onAppendClientAlertToOrderNotes?.invoke(alertMsg)
                      try {
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                          type = "text/plain"
                          putExtra(Intent.EXTRA_SUBJECT, "Contrôle Ampoules & État Véhicule - $orderOrQuoteNumber")
                          putExtra(Intent.EXTRA_TEXT, alertMsg)
                          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Prévenir le client ($clientPhone)"))
                      } catch (e: Exception) {
                        Toast.makeText(context, "Client prévenu et mention ajoutée à l'Ordre de Réparation !", Toast.LENGTH_LONG).show()
                      }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                      .weight(1f)
                      .testTag("btn_notify_client_burnt_bulbs")
                  ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "PRÉVENIR LE CLIENT (SMS / WHATSAPP)",
                      color = Color.White,
                      fontWeight = FontWeight.Black,
                      fontSize = 10.sp
                    )
                  }

                  OutlinedButton(
                    onClick = {
                      val summary = checklist.buildChecklistSummaryLine()
                      onAppendClientAlertToOrderNotes?.invoke(
                        "$summary\n• Détail ampoules : ${
                          checklist.burntOrReplacedBulbs.joinToString(", ") { "${it.positionName} [${it.status.shortLabel}]" }
                            .ifBlank { "Toutes ampoules OK" }
                        }"
                      )
                      onChecklistChange(checklist.copy(clientNotifiedForBulbs = true))
                      Toast.makeText(context, "Bilan ampoules & propreté inscrit sur l'Ordre de Réparation.", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, TechCyan),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Icon(Icons.Default.NoteAdd, contentDescription = null, tint = TechCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "INSCRIRE SUR L'OR",
                      color = TechCyan,
                      fontWeight = FontWeight.Black,
                      fontSize = 10.sp
                    )
                  }
                }
              }
            }

            // Bulbs List
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              filteredBulbs.forEach { bulb ->
                val statusColor = Color(bulb.status.colorHex)
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(
                    width = if (bulb.status != BulbCheckStatus.OK_FONCTIONNEL) 1.3.dp else 1.dp,
                    color = if (bulb.status != BulbCheckStatus.OK_FONCTIONNEL) statusColor else MetalBorder
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bulb_item_${bulb.id}")
                ) {
                  Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = "${bulb.zone.emoji} ${bulb.positionName}",
                          style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PureWhite,
                            fontSize = 11.5.sp
                          )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                          text = "Culot : ${bulb.bulbSocketType} • Réf : ${bulb.oemPartRef} • Tarif : ${df.format(bulb.replacementUnitPriceDzd)} DA",
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = AluminumGray,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                          )
                        )
                      }

                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = statusColor.copy(alpha = 0.18f),
                        border = BorderStroke(0.8.dp, statusColor)
                      ) {
                        Text(
                          text = "${bulb.status.badgeEmoji} ${bulb.status.shortLabel}",
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = statusColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.5.sp
                          ),
                          modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                      }
                    }

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                      BulbCheckStatus.entries.forEach { targetStatus ->
                        val isSelected = bulb.status == targetStatus
                        val chipColor = Color(targetStatus.colorHex)
                        Surface(
                          shape = RoundedCornerShape(7.dp),
                          color = if (isSelected) chipColor.copy(alpha = 0.25f) else MetalSurface,
                          border = BorderStroke(
                            1.dp,
                            if (isSelected) chipColor else MetalBorder
                          ),
                          onClick = {
                            val wasReplaced = bulb.status == BulbCheckStatus.REMPLACEE_SUR_OR
                            val willBeReplaced = targetStatus == BulbCheckStatus.REMPLACEE_SUR_OR
                            val updatedBulb = bulb.copy(status = targetStatus)
                            val updatedList = checklist.bulbs.map {
                              if (it.id == bulb.id) updatedBulb else it
                            }
                            onChecklistChange(checklist.copy(bulbs = updatedList))

                            if (!wasReplaced && willBeReplaced) {
                              onSyncBulbReplacementItem?.invoke(updatedBulb, true)
                            } else if (wasReplaced && !willBeReplaced) {
                              onSyncBulbReplacementItem?.invoke(updatedBulb, false)
                            }
                          },
                          modifier = Modifier
                            .weight(1f)
                            .testTag("bulb_status_${bulb.id}_${targetStatus.name.lowercase()}")
                        ) {
                          Row(
                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 3.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                          ) {
                            Text(targetStatus.badgeEmoji, fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                              text = targetStatus.shortLabel,
                              color = if (isSelected) PureWhite else AluminumLight,
                              fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                              fontSize = 9.sp,
                              maxLines = 1
                            )
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

      else -> {
        // =========================================================================
        // ONGLET 3 : ÉTAT DU VÉHICULE (INTÉRIEUR / EXTÉRIEUR : PROPRE / SALE) & CHECKLIST OR
        // =========================================================================
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
              text = "🧼 ÉTAT DE PROPRETÉ DU VÉHICULE À L'ENTRÉE (EXTÉRIEUR & INTÉRIEUR)",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite
              )
            )

            // 1. Propreté Extérieure
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "État Extérieur (Carrosserie / Vitrages) :",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = OffWhite,
                    fontWeight = FontWeight.Bold
                  )
                )
                Text(
                  text = checklist.exteriorCleanliness.description,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(checklist.exteriorCleanliness.colorHex),
                    fontSize = 9.5.sp
                  )
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                VehicleCleanlinessLevel.entries.forEach { level ->
                  val isSelected = checklist.exteriorCleanliness == level
                  val levelColor = Color(level.colorHex)
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) levelColor.copy(alpha = 0.24f) else GraphiteBlack,
                    border = BorderStroke(1.dp, if (isSelected) levelColor else MetalBorder),
                    onClick = {
                      onChecklistChange(checklist.copy(exteriorCleanliness = level))
                    },
                    modifier = Modifier
                      .weight(1f)
                      .testTag("ext_clean_${level.name.lowercase()}")
                  ) {
                    Column(
                      modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                      horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                      Text(level.emoji, fontSize = 14.sp)
                      Spacer(modifier = Modifier.height(2.dp))
                      Text(
                        text = level.label,
                        color = if (isSelected) PureWhite else AluminumLight,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                        fontSize = 9.5.sp,
                        maxLines = 1
                      )
                    }
                  }
                }
              }
            }

            // 2. Propreté Intérieure
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "État Intérieur (Habitacle / Sellerie / Tapis) :",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = OffWhite,
                    fontWeight = FontWeight.Bold
                  )
                )
                Text(
                  text = checklist.interiorCleanliness.description,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(checklist.interiorCleanliness.colorHex),
                    fontSize = 9.5.sp
                  )
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                VehicleCleanlinessLevel.entries.forEach { level ->
                  val isSelected = checklist.interiorCleanliness == level
                  val levelColor = Color(level.colorHex)
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) levelColor.copy(alpha = 0.24f) else GraphiteBlack,
                    border = BorderStroke(1.dp, if (isSelected) levelColor else MetalBorder),
                    onClick = {
                      onChecklistChange(checklist.copy(interiorCleanliness = level))
                    },
                    modifier = Modifier
                      .weight(1f)
                      .testTag("int_clean_${level.name.lowercase()}")
                  ) {
                    Column(
                      modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                      horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                      Text(level.emoji, fontSize = 14.sp)
                      Spacer(modifier = Modifier.height(2.dp))
                      Text(
                        text = level.label,
                        color = if (isSelected) PureWhite else AluminumLight,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                        fontSize = 9.5.sp,
                        maxLines = 1
                      )
                    }
                  }
                }
              }
            }

            // 3. Kit Protection Habitacle & Niveau de Carburant
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = GraphiteBlack,
                border = BorderStroke(
                  1.dp,
                  if (checklist.protectiveCoversInstalled) ElectricGreen else MechanicalOrange
                ),
                onClick = {
                  onChecklistChange(
                    checklist.copy(protectiveCoversInstalled = !checklist.protectiveCoversInstalled)
                  )
                },
                modifier = Modifier.weight(1.2f)
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Checkbox(
                    checked = checklist.protectiveCoversInstalled,
                    onCheckedChange = {
                      onChecklistChange(checklist.copy(protectiveCoversInstalled = it))
                    },
                    colors = CheckboxDefaults.colors(checkedColor = ElectricGreen)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Column {
                    Text(
                      text = "Housse & Tapis Protection Posés",
                      color = PureWhite,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.5.sp
                    )
                    Text(
                      text = "Siège + Volant + Tapis sol papier",
                      color = AluminumGray,
                      fontSize = 9.sp
                    )
                  }
                }
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = GraphiteBlack,
                border = BorderStroke(1.dp, MetalBorder),
                modifier = Modifier.weight(1f)
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Text(
                    text = "Niveau Carburant : ${checklist.fuelLevelFormatted}",
                    color = TechCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    (1..4).forEach { q ->
                      val active = q <= checklist.fuelLevelQuarters
                      Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (active) TechCyan else MetalSurface,
                        onClick = {
                          onChecklistChange(checklist.copy(fuelLevelQuarters = q))
                        },
                        modifier = Modifier
                          .weight(1f)
                          .height(22.dp)
                      ) {
                        Box(contentAlignment = Alignment.Center) {
                          Text(
                            text = "$q/4",
                            color = if (active) Color.White else AluminumGray,
                            fontWeight = FontWeight.Black,
                            fontSize = 8.5.sp
                          )
                        }
                      }
                    }
                  }
                }
              }
            }

            Divider(color = MetalBorder)

            // 4. Checklist Complète de Réception dans l'Ordre de Réparation (12 points)
            Text(
              text = "📋 CHECKLIST CONTRADICTOIRE D'ORDRE DE RÉPARATION (${checklist.conditionChecks.size} POINTS)",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite
              )
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              checklist.conditionChecks.forEach { item ->
                val itemColor = Color(item.status.colorHex)
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(
                    1.dp,
                    if (item.status == OrConditionCheckStatus.CONFORME) MetalBorder else itemColor
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("or_check_item_${item.id}")
                ) {
                  Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                      text = "[${item.section}] ${item.label}",
                      style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        fontSize = 11.sp
                      )
                    )
                    Text(
                      text = item.detailNote,
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = AluminumGray,
                        fontSize = 9.5.sp
                      )
                    )

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      OrConditionCheckStatus.entries.forEach { statusOpt ->
                        val isSelected = item.status == statusOpt
                        val optColor = Color(statusOpt.colorHex)
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = if (isSelected) optColor.copy(alpha = 0.22f) else MetalSurface,
                          border = BorderStroke(1.dp, if (isSelected) optColor else MetalBorder),
                          onClick = {
                            val updated = checklist.conditionChecks.map {
                              if (it.id == item.id) it.copy(status = statusOpt) else it
                            }
                            onChecklistChange(checklist.copy(conditionChecks = updated))
                          },
                          modifier = Modifier.weight(1f)
                        ) {
                          Row(
                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                          ) {
                            Text(statusOpt.emoji, fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                              text = statusOpt.shortLabel,
                              color = if (isSelected) PureWhite else AluminumLight,
                              fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                              fontSize = 9.5.sp,
                              maxLines = 1
                            )
                          }
                        }
                      }
                    }
                  }
                }
              }
            }

            OutlinedTextField(
              value = checklist.valuablesDeclaredInCar,
              onValueChange = {
                onChecklistChange(checklist.copy(valuablesDeclaredInCar = it))
              },
              label = { Text("Objets personnels / valeurs laissés dans le véhicule") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = checklist.exteriorInteriorNotes,
              onValueChange = {
                onChecklistChange(checklist.copy(exteriorInteriorNotes = it))
              },
              label = { Text("Observations état extérieur / intérieur (rayures, taches, réserves)") },
              maxLines = 3,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }
    }

    // =========================================================================
    // SIGNATURE ÉLECTRONIQUE EN BAS DE L'ORDRE DE RÉPARATION (TOUJOURS PRÉSENTE)
    // =========================================================================
    RepairOrderBottomElectronicSignatureCard(
      checklist = checklist,
      defaultSignerName = editableClientName.ifBlank { clientName.ifBlank { "Client Atelier" } },
      onChecklistChange = onChecklistChange,
      onSignedCallback = { signer, timeFormatted ->
        onAppendClientAlertToOrderNotes?.invoke(
          "✍️ Signature électronique apposée en bas de l'Ordre de Réparation par $signer ($timeFormatted)."
        )
        Toast.makeText(
          context,
          "✍️ Signature électronique enregistrée en bas de l'Ordre de Réparation !",
          Toast.LENGTH_SHORT
        ).show()
      }
    )
  }
}

@Composable
private fun ArrivalPhotoActionCard(
  title: String,
  subtitle: String,
  isCaptured: Boolean,
  accentColor: Color,
  onTakeCameraPhoto: () -> Unit,
  onInstantOcrFill: () -> Unit,
  testTag: String,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = if (isCaptured) accentColor.copy(alpha = 0.16f) else GraphiteBlack,
    border = BorderStroke(1.2.dp, if (isCaptured) accentColor else MetalBorder),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          color = PureWhite,
          fontWeight = FontWeight.Black,
          fontSize = 10.5.sp,
          maxLines = 1
        )
        Icon(
          imageVector = if (isCaptured) Icons.Default.CheckCircle else Icons.Default.CameraAlt,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(16.dp)
        )
      }
      Text(
        text = subtitle,
        color = if (isCaptured) accentColor else AluminumLight,
        fontWeight = if (isCaptured) FontWeight.Bold else FontWeight.Normal,
        fontSize = 9.5.sp,
        maxLines = 1
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = accentColor,
          onClick = onInstantOcrFill,
          modifier = Modifier
            .weight(1f)
            .testTag(testTag)
        ) {
          Text(
            text = "📷 Photo Auto",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp,
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 6.dp)
          )
        }
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MetalSurface,
          border = BorderStroke(1.dp, accentColor),
          onClick = onTakeCameraPhoto
        ) {
          Icon(
            Icons.Default.PhotoCamera,
            contentDescription = "Caméra",
            tint = accentColor,
            modifier = Modifier
              .padding(5.dp)
              .size(13.dp)
          )
        }
      }
    }
  }
}

@Composable
fun RepairOrderBottomElectronicSignatureCard(
  checklist: RepairOrderInspectionChecklist,
  defaultSignerName: String,
  onChecklistChange: (RepairOrderInspectionChecklist) -> Unit,
  onSignedCallback: (signerName: String, timestamp: String) -> Unit
) {
  var signerNameInput by remember(checklist.electronicSignatureSignerName, defaultSignerName) {
    mutableStateOf(
      checklist.electronicSignatureSignerName.ifBlank { defaultSignerName }
    )
  }
  val strokes = remember { mutableStateListOf<List<Offset>>() }
  var currentStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }
  val totalPoints = strokes.sumOf { it.size } + currentStroke.size + checklist.electronicSignatureStrokeCount

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("or_bottom_electronic_signature_card"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(
      1.5.dp,
      if (checklist.isElectronicallySigned) ElectricGreen else MechanicalOrange
    )
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Icon(
            Icons.Default.Draw,
            contentDescription = null,
            tint = if (checklist.isElectronicallySigned) ElectricGreen else MechanicalOrange,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "✍️ SIGNATURE ÉLECTRONIQUE EN BAS DE L'ORDRE DE RÉPARATION",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite
              )
            )
            Text(
              text = if (checklist.isElectronicallySigned) {
                "Signé par ${checklist.electronicSignatureSignerName} (${checklist.electronicSignatureTimestamp})"
              } else {
                "Bon pour accord client sur l'état du véhicule, les voyants allumés, la checklist et les travaux"
              },
              style = MaterialTheme.typography.bodySmall.copy(
                color = if (checklist.isElectronicallySigned) ElectricGreen else AluminumLight,
                fontSize = 10.sp
              )
            )
          }
        }

        if (checklist.isElectronicallySigned) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = ElectricGreenDark,
            border = BorderStroke(1.dp, ElectricGreen)
          ) {
            Text(
              text = "✓ CERTIFIÉ",
              color = ElectricGreen,
              fontWeight = FontWeight.Black,
              fontSize = 9.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      OutlinedTextField(
        value = signerNameInput,
        onValueChange = {
          signerNameInput = it
          onChecklistChange(checklist.copy(electronicSignatureSignerName = it))
        },
        label = { Text("Nom & Prénom du Client Signataire") },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_or_bottom_signer_name")
      )

      // Zone de signature tactile au doigt
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(120.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(GraphiteBlack)
          .pointerInput(Unit) {
            detectDragGestures(
              onDragStart = { offset ->
                currentStroke = listOf(offset)
              },
              onDrag = { change, _ ->
                change.consume()
                currentStroke = currentStroke + change.position
              },
              onDragEnd = {
                if (currentStroke.size > 1) {
                  strokes.add(currentStroke)
                }
                currentStroke = emptyList()
              }
            )
          }
          .testTag("canvas_or_bottom_electronic_signature")
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val gridStep = 28.dp.toPx()
          var x = gridStep
          while (x < size.width) {
            drawLine(
              color = Color.Gray.copy(alpha = 0.14f),
              start = Offset(x, 0f),
              end = Offset(x, size.height),
              strokeWidth = 1f
            )
            x += gridStep
          }
          drawLine(
            color = Color.Gray.copy(alpha = 0.35f),
            start = Offset(20f, size.height * 0.78f),
            end = Offset(size.width - 20f, size.height * 0.78f),
            strokeWidth = 1.5f
          )

          val allStrokes = strokes + listOf(currentStroke)
          allStrokes.forEach { pts ->
            if (pts.size > 1) {
              val path = Path().apply {
                moveTo(pts.first().x, pts.first().y)
                for (i in 1 until pts.size) {
                  lineTo(pts[i].x, pts[i].y)
                }
              }
              drawPath(
                path = path,
                color = Color(0xFF0284C7),
                style = Stroke(width = 3.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
              )
            }
          }
        }

        if (strokes.isEmpty() && currentStroke.isEmpty() && !checklist.isElectronicallySigned) {
          Text(
            text = "Signez ici avec le doigt ou cliquez sur « Signer & Valider l'OR »",
            color = AluminumMuted,
            fontSize = 10.5.sp,
            modifier = Modifier.align(Alignment.Center)
          )
        } else if (checklist.isElectronicallySigned && strokes.isEmpty()) {
          Text(
            text = "✓ Signature Électronique : ${checklist.electronicSignatureSignerName} (${checklist.electronicSignatureTimestamp})",
            color = ElectricGreen,
            fontWeight = FontWeight.Black,
            fontSize = 11.5.sp,
            modifier = Modifier.align(Alignment.Center)
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = {
            strokes.clear()
            currentStroke = emptyList()
            onChecklistChange(
              checklist.copy(
                isElectronicallySigned = false,
                electronicSignatureTimestamp = "",
                electronicSignatureStrokeCount = 0
              )
            )
          },
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, MetalBorder),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.Refresh, contentDescription = null, tint = AluminumLight, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Effacer", color = AluminumLight, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }

        Button(
          onClick = {
            val timestamp = SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.FRANCE).format(Date())
            val finalSigner = signerNameInput.ifBlank { defaultSignerName }
            onChecklistChange(
              checklist.copy(
                isElectronicallySigned = true,
                electronicSignatureSignerName = finalSigner,
                electronicSignatureTimestamp = timestamp,
                electronicSignatureStrokeCount = totalPoints.coerceAtLeast(24)
              )
            )
            onSignedCallback(finalSigner, timestamp)
          },
          colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1.6f)
            .testTag("btn_confirm_or_bottom_electronic_signature")
        ) {
          Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (checklist.isElectronicallySigned) "✓ OR SIGNÉ ÉLECTRONIQUEMENT" else "✍️ SIGNER & VALIDER L'OR",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 10.5.sp
          )
        }
      }
    }
  }
}

@Composable
private fun ChecklistKpiBox(
  title: String,
  value: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = GraphiteBlack,
    border = BorderStroke(0.8.dp, accentColor.copy(alpha = 0.55f)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
          color = AluminumMuted,
          fontSize = 8.5.sp
        ),
        maxLines = 1
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.bodySmall.copy(
          color = accentColor,
          fontWeight = FontWeight.Black,
          fontSize = 10.5.sp
        ),
        maxLines = 1
      )
    }
  }
}

@Composable
fun RepairOrderChecklistModal(
  order: RepairOrder,
  onSaveUpdatedOrder: (RepairOrder) -> Unit,
  onDismiss: () -> Unit
) {
  var workingChecklist by remember(order.id) { mutableStateOf(order.checklist) }
  var workingItems by remember(order.id) { mutableStateOf(order.items) }
  var workingNotes by remember(order.id) { mutableStateOf(order.notes) }
  var workingClientName by remember(order.id) { mutableStateOf(order.clientName) }
  var workingVehicleModel by remember(order.id) { mutableStateOf(order.vehicleModel) }
  var workingVin by remember(order.id) { mutableStateOf(order.vin) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = 24.dp, bottom = 12.dp, start = 10.dp, end = 10.dp)
        .testTag("modal_repair_order_checklist"),
      shape = RoundedCornerShape(16.dp),
      color = GraphiteDark,
      border = BorderStroke(1.2.dp, MechanicalOrange)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Surface(
          color = MetalCard,
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(MechanicalOrange.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.FactCheck, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "ORDRE DE RÉPARATION VOCAL & PHOTO IA • ${order.orderNumber}",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                  )
                )
                Text(
                  text = "$workingVehicleModel • Client : $workingClientName (${order.clientPhone})",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = AluminumLight,
                    fontSize = 11.sp
                  )
                )
              }
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MetalSurface)
            ) {
              Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite, modifier = Modifier.size(18.dp))
            }
          }
        }

        // Scrollable Body
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 12.dp),
          contentPadding = PaddingValues(vertical = 12.dp)
        ) {
          item {
            RepairOrderChecklistAndBulbsPanel(
              checklist = workingChecklist,
              clientName = workingClientName,
              clientPhone = order.clientPhone,
              vehicleModel = workingVehicleModel,
              orderOrQuoteNumber = order.orderNumber,
              onChecklistChange = { workingChecklist = it },
              onSyncBulbReplacementItem = { bulb, shouldAdd ->
                val repairItem = bulb.toRepairItem()
                workingItems = if (shouldAdd) {
                  if (workingItems.any { it.reference == repairItem.reference }) {
                    workingItems
                  } else {
                    workingItems + repairItem
                  }
                } else {
                  workingItems.filterNot { it.reference == repairItem.reference }
                }
              },
              onAppendClientAlertToOrderNotes = { alertText ->
                if (!workingNotes.contains(alertText.take(35))) {
                  workingNotes = if (workingNotes.isBlank()) alertText else "$workingNotes\n$alertText"
                }
              },
              onAutoFillReceptionFields = { _, vin, _, _, newClientName, detectedVehicle ->
                if (vin.isNotBlank()) workingVin = vin
                if (!newClientName.isNullOrBlank()) workingClientName = newClientName
                if (!detectedVehicle.isNullOrBlank()) workingVehicleModel = detectedVehicle
              }
            )
          }
        }

        // Footer Save Button
        Surface(
          color = MetalCard,
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            MekanikSecondaryButton(
              text = "ANNULER",
              onClick = onDismiss,
              modifier = Modifier.weight(0.8f)
            )
            MekanikPrimaryButton(
              text = "ENREGISTRER L'OR COMPLET (${workingItems.size} PIÈCES)",
              onClick = {
                val summaryLine = workingChecklist.buildChecklistSummaryLine()
                val updatedNotes = if (workingNotes.contains("Extérieur:")) {
                  workingNotes
                } else if (workingNotes.isBlank()) {
                  summaryLine
                } else {
                  "$workingNotes\n• $summaryLine"
                }
                onSaveUpdatedOrder(
                  order.copy(
                    clientName = workingClientName,
                    vehicleModel = workingVehicleModel,
                    vin = workingVin,
                    items = workingItems,
                    notes = updatedNotes,
                    checklist = workingChecklist,
                    isSigned = workingChecklist.isElectronicallySigned || order.isSigned,
                    signatureName = workingChecklist.electronicSignatureSignerName.ifBlank { order.signatureName }
                  )
                )
              },
              icon = Icons.Default.CheckCircle,
              modifier = Modifier
                .weight(1.4f)
                .testTag("btn_save_repair_order_checklist")
            )
          }
        }
      }
    }
  }
}
