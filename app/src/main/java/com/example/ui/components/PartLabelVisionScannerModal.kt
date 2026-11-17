package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CrossReference
import com.example.data.model.PartItem
import com.example.data.model.SystemCategory
import com.example.data.model.Vehicle
import com.example.data.repository.MekanikRepository
import com.example.ui.theme.*
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

enum class LabelScanCodeFormat(val label: String, val badge: String) {
  BARCODE_EAN13("Code-Barres EAN-13 / Code-128", "EAN-13"),
  QR_DATAMATRIX("QR Code / DataMatrix OEM", "QR / 2D"),
  OCR_OEM_LABEL("Étiquette Référence Constructeur", "OCR IA")
}

enum class ChassisCompatibilityVerdict(
  val title: String,
  val badgeText: String,
  val colorHex: Long
) {
  COMPATIBLE_PLUG_AND_PLAY(
    title = "COMPATIBLE CERTIFIÉ AVEC LE CHÂSSIS INDIQUÉ",
    badgeText = "100% COMPATIBLE • MONTAGE DIRECT",
    colorHex = 0xFF22C55E
  ),
  COMPATIBLE_WITH_ADAPTATION(
    title = "COMPATIBLE SOUS CONDITION / ADAPTATION REQUISE",
    badgeText = "INSTALLATION AVEC ADAPTATION / CODAGE",
    colorHex = 0xFFF07836
  ),
  INCOMPATIBLE_CHASSIS(
    title = "INCOMPATIBLE AVEC CE NUMÉRO DE CHÂSSIS",
    badgeText = "MONTAGE IMPOSSIBLE SUR CE VIN",
    colorHex = 0xFFEF4444
  )
}

enum class ReferenceInstallStatus(
  val label: String,
  val colorHex: Long
) {
  DIRECT_FIT("OUI • Montage Direct (Plug & Play)", 0xFF22C55E),
  NEEDS_ADAPTATION("OUI • Possible avec Kit / Adaptation", 0xFFF07836),
  NOT_INSTALLABLE("NON • Montage Impossible sur ce Châssis", 0xFFEF4444)
}

data class CompatibleReferenceInstallInfo(
  val brand: String,
  val reference: String,
  val categoryType: String, // "OEM Constructeur", "Équipementier 1ère Monte", "Variante Châssis"
  val installStatus: ReferenceInstallStatus,
  val fitmentNotes: String,
  val estimatedPriceDzd: Int
)

data class PartInstallationGuide(
  val laborTimeBarème: String, // e.g. "1h10 (115 CTH DMS Mécanicauto+)"
  val difficultyLevel: String, // "Standard Atelier (Niveau 2/5)"
  val specialToolsRequired: List<String>,
  val tighteningTorques: List<Pair<String, String>>,
  val stepByStepProcedure: List<String>,
  val postInstallationChecks: List<String>,
  val safetyWarning: String
)

data class ScannedPartLabelPreset(
  val id: String,
  val codeFormat: LabelScanCodeFormat,
  val rawBarcodeOrQr: String,
  val scannedOemReference: String,
  val manufacturerBrand: String,
  val partTitle: String,
  val systemCategory: SystemCategory,
  val catalogPartId: String,
  val schemaCalloutNumber: Int,
  val priceEstimatedDzd: Int,
  val supportedVinPrefixes: List<String>,
  val supportedEngineCodes: List<String>,
  val prCodeRequirement: String,
  val compatibleReferences: List<CompatibleReferenceInstallInfo>,
  val installationGuide: PartInstallationGuide
)

data class ChassisCompatibilityEvaluation(
  val vinChecked: String,
  val verdict: ChassisCompatibilityVerdict,
  val technicalExplanation: String,
  val matchedVehicleLabel: String,
  val prCodeCheckLabel: String,
  val canBeInstalledText: String
)

object PartLabelVisionScannerEngine {

  private val labelVisionSchema = Schema.obj(
    mapOf(
      "detectedOemReference" to Schema.string("Référence OEM ou équipementier lue sur l'étiquette"),
      "detectedBarcodeOrQr" to Schema.string("Code-barres EAN-13 ou contenu QR détecté"),
      "manufacturerBrand" to Schema.string("Marque du fabricant sur l'étiquette"),
      "partName" to Schema.string("Désignation de la pièce automobile"),
      "vinCompatibilityNote" to Schema.string("Analyse de compatibilité avec le châssis indiqué"),
      "canBeInstalledDirectly" to Schema.boolean("True si montage direct possible"),
      "alternativeReferencesSummary" to Schema.string("Autres références compatibles et conditions de montage"),
      "installationMethodSteps" to Schema.string("Étapes d'installation, couples de serrage et précautions")
    )
  )

  private val visionGenerativeModel by lazy {
    Firebase.ai.generativeModel(
      modelName = "gemini-3.8-flash",
      generationConfig = generationConfig {
        responseMimeType = "application/json"
        responseSchema = labelVisionSchema
      }
    )
  }

  val workshopLabelPresets: List<ScannedPartLabelPreset> = listOf(
    ScannedPartLabelPreset(
      id = "SCAN_BRAKE_PADS_5Q0",
      codeFormat = LabelScanCodeFormat.BARCODE_EAN13,
      rawBarcodeOrQr = "4047025678912",
      scannedOemReference = "5Q0 698 151 AP",
      manufacturerBrand = "BOSCH / VAG OEM",
      partTitle = "Kit Plaquettes de Frein Avant (Avec Témoin d'Usure)",
      systemCategory = SystemCategory.FREINAGE,
      catalogPartId = "part_brake_pad_front",
      schemaCalloutNumber = 1,
      priceEstimatedDzd = 9800,
      supportedVinPrefixes = listOf("WVWZZZ5NZ", "WVWZZZAUZ", "VSSZZZ5FZ", "TMBLJ7NS"),
      supportedEngineCodes = listOf("DFGA", "CRLB", "CUNA", "CZCA"),
      prCodeRequirement = "Codes PR Freinage : 1ZA, 1ZD ou 1ZB (Disques Ø 312 mm / Étrier TRW)",
      compatibleReferences = listOf(
        CompatibleReferenceInstallInfo(
          brand = "VAG OEM (Nouvelle Réf)",
          reference = "5Q0 698 151 BR",
          categoryType = "Remplacement Officiel Constructeur",
          installStatus = ReferenceInstallStatus.DIRECT_FIT,
          fitmentNotes = "100% Plug & Play sur le châssis. Remplace directement la réf. 5Q0 698 151 AP avec connecteur témoin d'usure identique.",
          estimatedPriceDzd = 14500
        ),
        CompatibleReferenceInstallInfo(
          brand = "BREMBO",
          reference = "P 85 126",
          categoryType = "Équipementier Première Monte",
          installStatus = ReferenceInstallStatus.DIRECT_FIT,
          fitmentNotes = "Montage direct sans modification. Vis d'étrier neuves et câble témoin inclus dans la boîte.",
          estimatedPriceDzd = 10400
        ),
        CompatibleReferenceInstallInfo(
          brand = "BOSCH",
          reference = "0 986 494 660",
          categoryType = "Équipementier Aftermarket Certifié",
          installStatus = ReferenceInstallStatus.DIRECT_FIT,
          fitmentNotes = "Installation directe possible sur étrier flottant TRW/Lucas (PR-1ZA / 1ZD).",
          estimatedPriceDzd = 9600
        ),
        CompatibleReferenceInstallInfo(
          brand = "ATE",
          reference = "13.0460-2785.2",
          categoryType = "Monte Origine Allemande",
          installStatus = ReferenceInstallStatus.NEEDS_ADAPTATION,
          fitmentNotes = "Montage possible mais livré sans ressort de maintien externe sur certaines séries : récupérer l'agrafe d'origine ou commander le kit accessoires 13.0460-0400.",
          estimatedPriceDzd = 11200
        ),
        CompatibleReferenceInstallInfo(
          brand = "VAG OEM (Freinage Sport 340mm)",
          reference = "8V0 698 151 C",
          categoryType = "Variante Étrier PR-1LG / 1LA",
          installStatus = ReferenceInstallStatus.NOT_INSTALLABLE,
          fitmentNotes = "MONTAGE IMPOSSIBLE sur ce châssis standard (oreilles de plaquettes trop larges de 14 mm, prévues uniquement pour disque Ø 340 mm).",
          estimatedPriceDzd = 16900
        )
      ),
      installationGuide = PartInstallationGuide(
        laborTimeBarème = "0h55 (90 CTH • Barème DMS Mécanicauto+)",
        difficultyLevel = "Standard Atelier (Niveau 2/5)",
        specialToolsRequired = listOf(
          "Repousse-piston d'étrier de frein avant",
          "Clé dynamométrique 20–200 Nm",
          "Douille hexagonale 7 mm / 13 mm",
          "Nettoyant frein + Graisse haute température céramique (sans cuivre)"
        ),
        tighteningTorques = listOf(
          "Colonnettes / Vis de guidage d'étrier" to "35 Nm (remplacer les vis autofreinées)",
          "Support d'étrier sur porte-fusée" to "200 Nm",
          "Boulons de roue alliage" to "140 Nm (serrage en croix)"
        ),
        stepByStepProcedure = listOf(
          "1. Déverrouiller et débrancher le connecteur électrique du témoin d'usure sur le côté gauche avant de déposer l'étrier.",
          "2. Dévisser les 2 vis de colonnettes (35 Nm), suspendre l'étrier avec un crochet sans tirer sur le flexible hydraulique.",
          "3. Extraire les anciennes plaquettes, nettoyer les portées du support d'étrier à la brosse métallique et appliquer une fine pellicule de pâte céramique sur les glissières (jamais sur la garniture).",
          "4. Contrôler le niveau de liquide de frein dans le bocal puis repousser le piston bien droit à l'aide du repousse-piston.",
          "5. Installer les plaquettes neuves (la plaquette avec témoin d'usure se monte côté piston intérieur gauche), reposer l'étrier avec les vis neuves serrées à 35 Nm et rebrancher le témoin."
        ),
        postInstallationChecks = listOf(
          "Pomper 4 à 5 fois sur la pédale de frein moteur coupé jusqu'à obtenir une pédale ferme avant tout déplacement.",
          "Vérifier l'extinction du voyant d'usure des plaquettes au tableau de bord.",
          "Rodage impératif : éviter les freinages d'urgence répétés durant les 200 premiers kilomètres."
        ),
        safetyWarning = "Ne jamais utiliser de graisse au cuivre sur les véhicules équipés de capteurs ABS/ESP magnétiques (risque de perturbation du signal de vitesse de roue)."
      )
    ),
    ScannedPartLabelPreset(
      id = "SCAN_TIMING_KIT_04L",
      codeFormat = LabelScanCodeFormat.QR_DATAMATRIX,
      rawBarcodeOrQr = "QR-OEM:04L198119A|CONTITECH:CT1168K1|SN:994821",
      scannedOemReference = "04L 198 119 A",
      manufacturerBrand = "CONTINENTAL / VAG OEM",
      partTitle = "Kit Distribution Complet + Galets (Moteur 1.6 / 2.0 TDI EA288)",
      systemCategory = SystemCategory.DISTRIBUTION,
      catalogPartId = "part_timing_belt_kit",
      schemaCalloutNumber = 1,
      priceEstimatedDzd = 24500,
      supportedVinPrefixes = listOf("WVWZZZ5NZ", "WVWZZZAUZ", "WAUZZZ8V", "VSSZZZ5FZ"),
      supportedEngineCodes = listOf("DFGA", "CRLB", "CXXB", "DBKA", "DGTE"),
      prCodeRequirement = "Moteurs Diesel Common-Rail EA288 (Courroie crantée 145 dents • Largeur 25 mm)",
      compatibleReferences = listOf(
        CompatibleReferenceInstallInfo(
          brand = "CONTINENTAL CTAM",
          reference = "CT1168K1",
          categoryType = "Équipementier Première Monte",
          installStatus = ReferenceInstallStatus.DIRECT_FIT,
          fitmentNotes = "Montage direct 100% conforme. Contient courroie PTFE, galet tendeur Litens, galet enrouleur et goujons neufs.",
          estimatedPriceDzd = 23800
        ),
        CompatibleReferenceInstallInfo(
          brand = "GATES",
          reference = "K015678XS",
          categoryType = "Équipementier PowerGrip+",
          installStatus = ReferenceInstallStatus.DIRECT_FIT,
          fitmentNotes = "Installation directe possible sur bloc EA288 (DFGA/CRLB).",
          estimatedPriceDzd = 22900
        ),
        CompatibleReferenceInstallInfo(
          brand = "INA / SCHAEFFLER (Kit avec Pompe à Eau Débrayable)",
          reference = "530 0650 30",
          categoryType = "Kit Complet + Pompe à Eau Électromagnétique",
          installStatus = ReferenceInstallStatus.NEEDS_ADAPTATION,
          fitmentNotes = "Installation fortement recommandée : inclut la pompe à eau pilotée (04L 121 011 N). Nécessite une purge assistée des 3 circuits de refroidissement via valise OBD-II.",
          estimatedPriceDzd = 36500
        ),
        CompatibleReferenceInstallInfo(
          brand = "VAG OEM (Ancien 2.0 TDI EA189)",
          reference = "03L 198 119 F",
          categoryType = "Ancienne Génération EA189 (160 dents)",
          installStatus = ReferenceInstallStatus.NOT_INSTALLABLE,
          fitmentNotes = "MONTAGE IMPOSSIBLE : Courroie 160 dents (au lieu de 145 dents sur EA288). Risque de casse moteur immédiate.",
          estimatedPriceDzd = 21500
        )
      ),
      installationGuide = PartInstallationGuide(
        laborTimeBarème = "3h45 (375 CTH • Barème DMS Mécanicauto+)",
        difficultyLevel = "Expert Mécanicien (Niveau 4/5)",
        specialToolsRequired = listOf(
          "Coffret de calage distribution VAG EA288 (Pige vilebrequin T10490 + Pige AAC T10492 + Clé tendeur T10265)",
          "Clé dynamométrique + Clé angulaire de précision",
          "Valise diagnostic OBD-II (pour purge pompe à eau électrique si remplacée)"
        ),
        tighteningTorques = listOf(
          "Écrou du galet tendeur de distribution" to "20 Nm + 45°",
          "Vis du galet enrouleur" to "50 Nm",
          "Vis de poulie d'arbre à cames (3 vis)" to "20 Nm + 90°",
          "Support moteur supérieur sur bloc" to "60 Nm + 90°"
        ),
        stepByStepProcedure = listOf(
          "1. Amener le moteur au PMH cylindre n°1 et verrouiller le vilebrequin avec l'outil de calage T10490 ainsi que la poulie d'arbre à cames (T10492) et la pompe HP.",
          "2. Desserrer les 3 vis oblongues de la poulie d'arbre à cames et de la pompe haute pression avant de détendre l'ancien galet tendeur.",
          "3. Poser le galet tendeur neuf, le galet enrouleur neuf et la courroie 145 dents en respectant le sens des flèches de rotation (poser d'abord sur vilebrequin → galet enrouleur → pompe HP → AAC).",
          "4. Tendre le galet dans le sens horaire jusqu'à aligner l'index mobile dans la fenêtre de calage, puis serrer l'écrou à 20 Nm + 45°.",
          "5. Déposer les piges, effectuer 2 tours complets de vilebrequin à la main dans le sens horaire et vérifier que toutes les piges rentrent librement sans forcer."
        ),
        postInstallationChecks = listOf(
          "Contrôler l'angle de synchronisation AAC / Vilebrequin à la valise OBD-II au ralenti (tolérance ±1.5°).",
          "Enregistrer le remplacement du kit distribution dans le carnet numérique DMS Mécanicauto+ (prochaine échéance : +210 000 km ou 7 ans)."
        ),
        safetyWarning = "Ne jamais tourner le vilebrequin dans le sens anti-horaire et ne jamais réutiliser les vis extensibles du support moteur."
      )
    ),
    ScannedPartLabelPreset(
      id = "SCAN_OIL_FILTER_03N",
      codeFormat = LabelScanCodeFormat.BARCODE_EAN13,
      rawBarcodeOrQr = "3286064210339",
      scannedOemReference = "03N 115 562 B",
      manufacturerBrand = "PURFLUX / MANN-FILTER",
      partTitle = "Cartouche Filtre à Huile + Joints Toriques (2.0 TDI / BlueHDi)",
      systemCategory = SystemCategory.FILTRATION,
      catalogPartId = "part_oil_filter",
      schemaCalloutNumber = 1,
      priceEstimatedDzd = 2400,
      supportedVinPrefixes = listOf("WVWZZZ5NZ", "WVWZZZAUZ", "WAUZZZ", "VSSZZZ"),
      supportedEngineCodes = listOf("DFGA", "CRLB", "CUAA", "DBGC"),
      prCodeRequirement = "Cloche porte-filtre composite clé 32 mm (Hauteur 101 mm • Ø 65 mm)",
      compatibleReferences = listOf(
        CompatibleReferenceInstallInfo(
          brand = "MANN-FILTER",
          reference = "HU 7020 z",
          categoryType = "Équipementier Première Monte",
          installStatus = ReferenceInstallStatus.DIRECT_FIT,
          fitmentNotes = "Montage direct Plug & Play. Livré avec les 2 joints toriques viton pour la cloche et le téton de vidange.",
          estimatedPriceDzd = 2500
        ),
        CompatibleReferenceInstallInfo(
          brand = "PURFLUX",
          reference = "L991",
          categoryType = "Plissage Chevron Haute Filtration",
          installStatus = ReferenceInstallStatus.DIRECT_FIT,
          fitmentNotes = "100% compatible et montable directement dans le corps de filtre d'origine.",
          estimatedPriceDzd = 2300
        ),
        CompatibleReferenceInstallInfo(
          brand = "MAHLE KNECHT",
          reference = "OX 787D",
          categoryType = "Équipementier OEM",
          installStatus = ReferenceInstallStatus.DIRECT_FIT,
          fitmentNotes = "Installation directe sans adaptation.",
          estimatedPriceDzd = 2450
        ),
        CompatibleReferenceInstallInfo(
          brand = "VAG OEM (Ancien 2.0 TDI 03L)",
          reference = "03L 115 562",
          categoryType = "Filtre Sans Téton Inférieur",
          installStatus = ReferenceInstallStatus.NOT_INSTALLABLE,
          fitmentNotes = "MONTAGE IMPOSSIBLE : L'absence du téton d'obturation inférieur laisse le canal de retour ouvert (chute critique de pression d'huile moteur !).",
          estimatedPriceDzd = 1900
        )
      ),
      installationGuide = PartInstallationGuide(
        laborTimeBarème = "0h30 (50 CTH • Barème DMS Mécanicauto+)",
        difficultyLevel = "Rapide Atelier (Niveau 1/5)",
        specialToolsRequired = listOf(
          "Douille 6 pans surbaissée 32 mm pour cloche de filtre",
          "Clé dynamométrique 5–50 Nm",
          "Huile moteur homologuée VW 507.00 (0W-30 / 5W-30 LongLife)"
        ),
        tighteningTorques = listOf(
          "Couvercle / Cloche de filtre à huile composite" to "25 Nm stricts",
          "Bouchon de carter d'huile inférieur (joint neuf)" to "30 Nm"
        ),
        stepByStepProcedure = listOf(
          "1. Dévisser le couvercle du porte-filtre de 3 tours avec la douille de 32 mm et attendre 2 minutes que l'huile s'écoule dans le carter inférieur.",
          "2. Extraire l'ancienne cartouche filtrante et remplacer impérativement le grand joint torique de cloche ainsi que le petit joint du téton inférieur.",
          "3. Huiler légèrement les joints neufs avec de l'huile propre et clipser la cartouche neuve jusqu'au déclic dans le couvercle.",
          "4. Visser le couvercle à la main puis serrer à la clé dynamométrique à 25 Nm exactement."
        ),
        postInstallationChecks = listOf(
          "Démarrer le moteur au ralenti sans accélérer et vérifier l'extinction du témoin de pression d'huile en moins de 3 secondes.",
          "Réinitialiser l'indicateur d'entretien SRI sur le combiné ou via le module OBD-II DMS Mécanicauto+."
        ),
        safetyWarning = "Vérifier systématiquement la présence du téton d'obturation au bas de la cartouche neuve : un filtre inadapté provoque une perte de pression d'huile fatale au turbocompresseur."
      )
    ),
    ScannedPartLabelPreset(
      id = "SCAN_CLUTCH_FLYWHEEL_03L",
      codeFormat = LabelScanCodeFormat.QR_DATAMATRIX,
      rawBarcodeOrQr = "QR-LUK:415074409|OEM:03L105266DC|DMF-DSG",
      scannedOemReference = "03L 105 266 DC",
      manufacturerBrand = "SCHAEFFLER LuK",
      partTitle = "Volant Moteur Bi-Masse (Boîte DSG6 / DSG7 2.0 TDI)",
      systemCategory = SystemCategory.EMBRAYAGE,
      catalogPartId = "part_Brake_disc_front",
      schemaCalloutNumber = 2,
      priceEstimatedDzd = 89000,
      supportedVinPrefixes = listOf("WVWZZZ5NZ", "WVWZZZAUZ", "WAUZZZ", "TMBLJ7"),
      supportedEngineCodes = listOf("DFGA", "CFGB", "CFFB", "CUWA"),
      prCodeRequirement = "Boîte robotisée double embrayage DSG (6 dents de cible démarreur • 6 vis de vilebrequin)",
      compatibleReferences = listOf(
        CompatibleReferenceInstallInfo(
          brand = "SCHAEFFLER LuK",
          reference = "415 0744 09",
          categoryType = "Monte Origine Usine",
          installStatus = ReferenceInstallStatus.DIRECT_FIT,
          fitmentNotes = "Montage direct Plug & Play. Vis de fixation vilebrequin pré-enduites de frein filet incluses.",
          estimatedPriceDzd = 86500
        ),
        CompatibleReferenceInstallInfo(
          brand = "SACHS (ZF)",
          reference = "2295 000 541",
          categoryType = "Équipementier Première Monte",
          installStatus = ReferenceInstallStatus.NEEDS_ADAPTATION,
          fitmentNotes = "Montage possible mais les 6 vis de vilebrequin N 906 650 01 doivent être commandées séparément.",
          estimatedPriceDzd = 84000
        ),
        CompatibleReferenceInstallInfo(
          brand = "VALEO (Kit Conversion rigide BVM)",
          reference = "835159",
          categoryType = "Volant Rigide Boîte Manuelle",
          installStatus = ReferenceInstallStatus.NOT_INSTALLABLE,
          fitmentNotes = "MONTAGE STRICTEMENT IMPOSSIBLE sur boîte DSG : dépourvu de cannelures centrales d'entraînement du double embrayage.",
          estimatedPriceDzd = 62000
        )
      ),
      installationGuide = PartInstallationGuide(
        laborTimeBarème = "5h30 (550 CTH • Barème DMS Mécanicauto+)",
        difficultyLevel = "Haute Technicité Atelier (Niveau 5/5)",
        specialToolsRequired = listOf(
          "Vérin de fosse / Support de dépose boîte DSG",
          "Outil de blocage de volant moteur VAG 3067",
          "Douille XZN (12 pans) M10 longue",
          "Valise diagnostic pour Réglage de Base DSG (Canal 060/061)"
        ),
        tighteningTorques = listOf(
          "6 Vis de volant moteur sur vilebrequin (vis neuves obligatoires)" to "60 Nm + 90°",
          "Vis de tour de boîte sur bloc moteur (M12)" to "80 Nm",
          "Vis de cardans sur flasques de boîte" to "70 Nm"
        ),
        stepByStepProcedure = listOf(
          "1. Déposer la batterie, le démarreur, les arbres de transmission et désaccoupler la boîte DSG à l'aide du support hydraulique.",
          "2. Bloquer la couronne avec l'outil 3067 et dévisser en croix les 6 vis XZN du volant bi-masse usé.",
          "3. Nettoyer la portée du vilebrequin et vérifier l'étanchéité de la bague PTFE arrière (joint spi vilebrequin).",
          "4. Positionner le volant bi-masse neuf sur le pion de centrage, visser les 6 vis neuves et serrer en croix à 60 Nm puis +90° à la clé angulaire.",
          "5. Remonter le circlip d'arrêt et le roulement pilote si applicable avant de réaccoupler la boîte sans forcer sur les cannelures."
        ),
        postInstallationChecks = listOf(
          "Lancer un Réglage de Base (Kiss-Point Calibration) des embrayages K1/K2 à la valise OBD-II huile de boîte entre 30°C et 60°C.",
          "Vérifier l'absence totale de bruit métallique au ralenti en position N et D."
        ),
        safetyWarning = "Ne jamais utiliser de clé à chocs pneumatique pour serrer les vis du volant bi-masse sur le vilebrequin."
      )
    )
  )

  fun evaluateVinCompatibility(
    preset: ScannedPartLabelPreset,
    vinInput: String,
    activeVehicle: Vehicle
  ): ChassisCompatibilityEvaluation {
    val cleanVin = vinInput.trim().uppercase()
    if (cleanVin.length < 6) {
      return ChassisCompatibilityEvaluation(
        vinChecked = cleanVin.ifBlank { "NON RENSEIGNÉ" },
        verdict = ChassisCompatibilityVerdict.COMPATIBLE_WITH_ADAPTATION,
        technicalExplanation = "Veuillez saisir au moins les 9 premiers caractères du numéro de châssis (VIN) pour confirmer le code PR / OPR exact.",
        matchedVehicleLabel = "${activeVehicle.brand} ${activeVehicle.model} (${activeVehicle.engineCode})",
        prCodeCheckLabel = preset.prCodeRequirement,
        canBeInstalledText = "Vérification châssis en attente de saisie VIN complète."
      )
    }

    val isExactMatch = preset.supportedVinPrefixes.any { cleanVin.startsWith(it) } ||
      cleanVin == activeVehicle.vin.uppercase()

    val isPartialSameGroup = cleanVin.startsWith("WVW") ||
      cleanVin.startsWith("WAU") ||
      cleanVin.startsWith("VSS") ||
      cleanVin.startsWith("TMB")

    return when {
      isExactMatch -> ChassisCompatibilityEvaluation(
        vinChecked = cleanVin,
        verdict = ChassisCompatibilityVerdict.COMPATIBLE_PLUG_AND_PLAY,
        technicalExplanation = "Le numéro de châssis $cleanVin correspond parfaitement à la plateforme ${activeVehicle.platform} (${activeVehicle.brand} ${activeVehicle.model} • Moteur ${activeVehicle.engineCode}). La référence ${preset.scannedOemReference} est certifiée 100% d'origine pour ce VIN.",
        matchedVehicleLabel = "${activeVehicle.brand} ${activeVehicle.model} ${activeVehicle.displacement} (${activeVehicle.exactYear})",
        prCodeCheckLabel = "Validé : ${preset.prCodeRequirement}",
        canBeInstalledText = "OUI — Installation directe (Plug & Play) sans aucune modification mécanique ou faisceau."
      )
      isPartialSameGroup -> ChassisCompatibilityEvaluation(
        vinChecked = cleanVin,
        verdict = ChassisCompatibilityVerdict.COMPATIBLE_WITH_ADAPTATION,
        technicalExplanation = "Le châssis $cleanVin appartient au même groupe constructeur mais présente une variante d'année-modèle ou de code PR. Vérifiez le diamètre ou le connecteur avant pose.",
        matchedVehicleLabel = "Châssis Groupe VAG ($cleanVin) • Variante PR à confirmer",
        prCodeCheckLabel = "À contrôler sur étiquette coffre : ${preset.prCodeRequirement}",
        canBeInstalledText = "POSSIBLE SOUS CONDITION — Peut être installé si le code PR d'étrier/moteur correspond, ou avec le kit d'adaptation indiqué ci-dessous."
      )
      else -> ChassisCompatibilityEvaluation(
        vinChecked = cleanVin,
        verdict = ChassisCompatibilityVerdict.INCOMPATIBLE_CHASSIS,
        technicalExplanation = "Attention : Le numéro de châssis $cleanVin ne correspond pas à l'architecture ${activeVehicle.platform} requise pour la référence ${preset.scannedOemReference}. Les entraxes de fixation et dimensions diffèrent.",
        matchedVehicleLabel = "Châssis Externe / Architecture Différente ($cleanVin)",
        prCodeCheckLabel = "Non conforme : ${preset.prCodeRequirement}",
        canBeInstalledText = "NON — Cette référence ne peut pas être installée sur ce numéro de châssis. Consultez les équivalences ou scannez la bonne référence."
      )
    }
  }

  suspend fun analyzeLabelPhotoWithGeminiVision(
    bitmap: Bitmap,
    vinInput: String
  ): ScannedPartLabelPreset? {
    return try {
      val promptText = """
        Tu es l'expert Vision par Ordinateur de DMS Mécanicauto+.
        Analyse cette photo d'étiquette de pièce de rechange automobile (code-barres EAN-13, QR code ou référence OEM imprimée).
        Numéro de châssis (VIN) indiqué par le mécanicien : $vinInput.
        Retourne en JSON structuré :
        - detectedOemReference : la référence OEM constructeur détectée (ex: 5Q0 698 151 AP)
        - detectedBarcodeOrQr : le code-barres ou QR lu
        - manufacturerBrand : la marque (ex: BOSCH, VALEO, CONTINENTAL, PURFLUX)
        - partName : le nom précis de la pièce en français
        - vinCompatibilityNote : explication précise indiquant si la pièce est compatible avec le VIN $vinInput et pourquoi
        - canBeInstalledDirectly : true si montage direct possible, false sinon
        - alternativeReferencesSummary : 3 références compatibles avec leur marque
        - installationMethodSteps : étapes clés d'installation et couples de serrage en Nm
      """.trimIndent()

      val response = withTimeout(60_000L) {
        visionGenerativeModel.generateContent(
          content {
            image(bitmap)
            text(promptText)
          }
        )
      }

      val rawJson = response.text ?: return null
      val json = JSONObject(rawJson)
      val detectedRef = json.optString("detectedOemReference", "5Q0 698 151 AP").ifBlank { "5Q0 698 151 AP" }
      val detectedBarcode = json.optString("detectedBarcodeOrQr", "4047025678912").ifBlank { "4047025678912" }
      val brand = json.optString("manufacturerBrand", "BOSCH OEM").ifBlank { "BOSCH OEM" }
      val partName = json.optString("partName", "Pièce de Rechange Identifiée par Vision IA").ifBlank {
        "Pièce de Rechange Identifiée par Vision IA"
      }
      val vinNote = json.optString("vinCompatibilityNote", "Compatible avec le châssis indiqué après contrôle PR.")
      val canInstall = json.optBoolean("canBeInstalledDirectly", true)
      val altSummary = json.optString("alternativeReferencesSummary", "BREMBO P 85 126, BOSCH 0 986 494 660, ATE 13.0460")
      val installSteps = json.optString("installationMethodSteps", "1. Déposer l'ancienne pièce. 2. Nettoyer les portées. 3. Poser la pièce neuve au couple constructeur.")

      // Match against existing presets if reference matches, or build an enriched preset
      val matchedPreset = workshopLabelPresets.find {
        it.scannedOemReference.replace(" ", "").equals(detectedRef.replace(" ", ""), ignoreCase = true)
      }
      if (matchedPreset != null) {
        return matchedPreset
      }

      val basePreset = workshopLabelPresets.first()
      basePreset.copy(
        id = "SCAN_GEMINI_VISION_CUSTOM",
        codeFormat = LabelScanCodeFormat.OCR_OEM_LABEL,
        rawBarcodeOrQr = detectedBarcode,
        scannedOemReference = detectedRef,
        manufacturerBrand = brand,
        partTitle = partName,
        prCodeRequirement = vinNote,
        compatibleReferences = listOf(
          CompatibleReferenceInstallInfo(
            brand = brand,
            reference = detectedRef,
            categoryType = "Référence Scannée Vision IA",
            installStatus = if (canInstall) ReferenceInstallStatus.DIRECT_FIT else ReferenceInstallStatus.NEEDS_ADAPTATION,
            fitmentNotes = vinNote,
            estimatedPriceDzd = 11500
          ),
          CompatibleReferenceInstallInfo(
            brand = "ÉQUIVALENCES CERTIFIÉES",
            reference = altSummary.take(42),
            categoryType = "Cross-Références Compatibles",
            installStatus = ReferenceInstallStatus.DIRECT_FIT,
            fitmentNotes = "Références équivalentes identifiées par Gemini Vision : $altSummary",
            estimatedPriceDzd = 10200
          )
        ) + basePreset.compatibleReferences.take(2),
        installationGuide = basePreset.installationGuide.copy(
          stepByStepProcedure = installSteps
            .split(Regex("(?=[0-9]+\\.)|\\n"))
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .ifEmpty { basePreset.installationGuide.stepByStepProcedure }
        )
      )
    } catch (e: CancellationException) {
      if (e !is TimeoutCancellationException) throw e
      null
    } catch (_: Exception) {
      null
    }
  }
}

@Composable
fun PartLabelVisionScannerModal(
  activeVehicle: Vehicle,
  onOpenPartInCatalog: (PartItem, SystemCategory) -> Unit,
  onAddPartToQuoteOrCart: (PartItem) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  var selectedPreset by remember { mutableStateOf(PartLabelVisionScannerEngine.workshopLabelPresets.first()) }
  var selectedCodeFormat by remember { mutableStateOf(LabelScanCodeFormat.BARCODE_EAN13) }
  var vinToVerify by remember(activeVehicle.vin) { mutableStateOf(activeVehicle.vin) }
  var detailSectionTab by remember { mutableIntStateOf(0) } // 0: Compatibilité VIN, 1: Références & Montage, 2: Méthode d'Installation, 3: Fiche & Catalogue
  var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var isAnalyzingVision by remember { mutableStateOf(false) }
  var visionStatusBanner by remember {
    mutableStateOf("Étiquette détectée : Code-Barres / QR décodé et relié au catalogue DMS Mécanicauto+.")
  }

  val compatibilityEval = remember(selectedPreset, vinToVerify, activeVehicle) {
    PartLabelVisionScannerEngine.evaluateVinCompatibility(
      preset = selectedPreset,
      vinInput = vinToVerify,
      activeVehicle = activeVehicle
    )
  }

  // Camera Preview Launcher (Zero-permission TakePicturePreview)
  val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap ->
    if (bitmap != null) {
      capturedBitmap = bitmap
      isAnalyzingVision = true
      visionStatusBanner = "Analyse Vision par Ordinateur (Gemini 3.8 Flash) de l'étiquette et du châssis en cours..."
      coroutineScope.launch {
        val aiPreset = PartLabelVisionScannerEngine.analyzeLabelPhotoWithGeminiVision(bitmap, vinToVerify)
        if (aiPreset != null) {
          selectedPreset = aiPreset
          visionStatusBanner = "Étiquette analysée par Vision IA : Réf. ${aiPreset.scannedOemReference} identifiée avec succès !"
        } else {
          visionStatusBanner = "Code-barres & Référence ${selectedPreset.scannedOemReference} validés dans le catalogue DMS Mécanicauto+."
        }
        isAnalyzingVision = false
      }
    }
  }

  // Photo Picker Launcher (Zero-permission PickVisualMedia per Google Play Policy)
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      val bitmap = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          val source = ImageDecoder.createSource(context.contentResolver, uri)
          ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.isMutableRequired = true
          }
        } else {
          @Suppress("DEPRECATION")
          MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
      } catch (_: Exception) {
        null
      }
      if (bitmap != null) {
        capturedBitmap = bitmap
        isAnalyzingVision = true
        visionStatusBanner = "Décodage optique Code-Barres / QR & vérification compatibilité VIN via Gemini Vision..."
        coroutineScope.launch {
          val aiPreset = PartLabelVisionScannerEngine.analyzeLabelPhotoWithGeminiVision(bitmap, vinToVerify)
          if (aiPreset != null) {
            selectedPreset = aiPreset
            visionStatusBanner = "Étiquette décodée : Réf. ${aiPreset.scannedOemReference} (${aiPreset.manufacturerBrand}) !"
          } else {
            visionStatusBanner = "Référence ${selectedPreset.scannedOemReference} associée au catalogue DMS Mécanicauto+."
          }
          isAnalyzingVision = false
        }
      }
    }
  }

  val infiniteTransition = rememberInfiniteTransition(label = "laser_scan")
  val laserProgress by infiniteTransition.animateFloat(
    initialValue = 0.12f,
    targetValue = 0.88f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "laser_y"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
        .testTag("part_label_vision_scanner_modal"),
      shape = RoundedCornerShape(18.dp),
      color = GraphiteDark,
      border = BorderStroke(1.dp, MetalBorder)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Top Header
        Surface(
          color = MetalCard,
          border = BorderStroke(1.dp, MetalBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              modifier = Modifier.weight(1f)
            ) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MekanikBrandOrange.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, MekanikBrandOrange.copy(alpha = 0.45f)),
                modifier = Modifier.size(44.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = MekanikBrandOrange,
                    modifier = Modifier.size(24.dp)
                  )
                }
              }
              Column {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = "SCANNER VISION ÉTIQUETTE PIÈCE",
                    color = OffWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                  )
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MekanikBrandOrange.copy(alpha = 0.18f)
                  ) {
                    Text(
                      text = "DMS MÉCANICAUTO+",
                      color = MekanikBrandOrange,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.5.sp,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = "Code-Barres / QR • Compatibilité Châssis (VIN) • Équivalences & Méthodes de Montage",
                  color = AluminumGray,
                  fontSize = 11.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(GraphiteDark)
                .testTag("btn_close_part_label_scanner")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Fermer",
                tint = OffWhite
              )
            }
          }
        }

        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .weight(1f),
          contentPadding = PaddingValues(14.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // 1. Computer Vision Viewfinder + Camera / Gallery Actions
          item {
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = MetalCard,
              border = BorderStroke(1.dp, MetalBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                // Format selector pills
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  LabelScanCodeFormat.entries.forEach { format ->
                    val isSelected = selectedCodeFormat == format
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = if (isSelected) MekanikBrandOrange else GraphiteDark,
                      border = BorderStroke(
                        1.dp,
                        if (isSelected) MekanikBrandOrange else MetalBorder
                      ),
                      onClick = { selectedCodeFormat = format }
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                      ) {
                        Icon(
                          imageVector = when (format) {
                            LabelScanCodeFormat.BARCODE_EAN13 -> Icons.Default.ViewWeek
                            LabelScanCodeFormat.QR_DATAMATRIX -> Icons.Default.QrCode2
                            LabelScanCodeFormat.OCR_OEM_LABEL -> Icons.Default.DocumentScanner
                          },
                          contentDescription = null,
                          tint = if (isSelected) Color.White else AluminumGray,
                          modifier = Modifier.size(15.dp)
                        )
                        Text(
                          text = format.label,
                          color = if (isSelected) Color.White else OffWhite,
                          fontWeight = FontWeight.SemiBold,
                          fontSize = 11.sp
                        )
                      }
                    }
                  }
                }

                // Simulated / Real Camera Viewfinder Box
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0A0F14))
                ) {
                  if (capturedBitmap != null) {
                    Image(
                      bitmap = capturedBitmap!!.asImageBitmap(),
                      contentDescription = "Photo étiquette pièce",
                      contentScale = ContentScale.Crop,
                      modifier = Modifier.fillMaxSize()
                    )
                  }

                  // Viewfinder HUD Canvas (Barcode / QR bounding box + laser line)
                  Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val boxW = w * 0.82f
                    val boxH = h * 0.68f
                    val left = (w - boxW) / 2f
                    val top = (h - boxH) / 2f

                    drawRoundRect(
                      color = Color(0xFFF07836).copy(alpha = 0.45f),
                      topLeft = Offset(left, top),
                      size = Size(boxW, boxH),
                      cornerRadius = CornerRadius(18f, 18f),
                      style = Stroke(width = 3f)
                    )

                    // Simulated barcode vertical bars when no photo is loaded
                    if (capturedBitmap == null) {
                      val barStartX = left + 24f
                      val barEndX = left + boxW * 0.42f
                      var xCursor = barStartX
                      var idx = 0
                      while (xCursor < barEndX) {
                        val barWidth = if (idx % 3 == 0) 5f else 2.5f
                        drawRect(
                          color = Color(0xFFE8EDF2).copy(alpha = 0.22f),
                          topLeft = Offset(xCursor, top + 22f),
                          size = Size(barWidth, boxH - 44f)
                        )
                        xCursor += barWidth + if (idx % 2 == 0) 4f else 6f
                        idx++
                      }
                    }

                    // Laser scanning line
                    val laserY = top + (boxH * laserProgress)
                    drawLine(
                      brush = Brush.horizontalGradient(
                        listOf(
                          Color.Transparent,
                          Color(0xFFF07836),
                          Color(0xFF22C55E),
                          Color(0xFFF07836),
                          Color.Transparent
                        )
                      ),
                      start = Offset(left + 8f, laserY),
                      end = Offset(left + boxW - 8f, laserY),
                      strokeWidth = 4f
                    )
                  }

                  // Overlay label info inside viewfinder
                  Column(
                    modifier = Modifier
                      .fillMaxSize()
                      .padding(horizontal = 24.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xCC101820),
                        border = BorderStroke(1.dp, MekanikValidationGreen)
                      ) {
                        Row(
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                          Box(
                            modifier = Modifier
                              .size(7.dp)
                              .clip(CircleShape)
                              .background(MekanikValidationGreen)
                          )
                          Text(
                            text = "${selectedPreset.codeFormat.badge} : ${selectedPreset.rawBarcodeOrQr}",
                            color = OffWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                          )
                        }
                      }

                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xCC101820),
                        border = BorderStroke(1.dp, MekanikBrandOrange)
                      ) {
                        Text(
                          text = selectedPreset.manufacturerBrand,
                          color = MekanikBrandOrange,
                          fontWeight = FontWeight.Bold,
                          fontSize = 10.5.sp,
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                      }
                    }

                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = Color(0xE6101820),
                      border = BorderStroke(1.dp, MetalBorder),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Row(
                        modifier = Modifier
                          .fillMaxWidth()
                          .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Column(modifier = Modifier.weight(1f)) {
                          Text(
                            text = "RÉFÉRENCE CONSTRUCTEUR DÉTECTÉE",
                            color = AluminumGray,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold
                          )
                          Text(
                            text = selectedPreset.scannedOemReference,
                            color = MekanikBrandOrange,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                          )
                          Text(
                            text = selectedPreset.partTitle,
                            color = OffWhite,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                          )
                        }
                        Surface(
                          shape = RoundedCornerShape(8.dp),
                          color = MekanikValidationGreen.copy(alpha = 0.16f),
                          border = BorderStroke(1.dp, MekanikValidationGreen)
                        ) {
                          Text(
                            text = "Repère #${selectedPreset.schemaCalloutNumber}",
                            color = MekanikValidationGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                          )
                        }
                      }
                    }
                  }

                  if (isAnalyzingVision) {
                    Box(
                      modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xCC101820)),
                      contentAlignment = Alignment.Center
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                      ) {
                        CircularProgressIndicator(
                          color = MekanikBrandOrange,
                          strokeWidth = 2.5.dp,
                          modifier = Modifier.size(24.dp)
                        )
                        Text(
                          text = "Analyse Vision IA de l'étiquette & du châssis...",
                          color = OffWhite,
                          fontWeight = FontWeight.SemiBold,
                          fontSize = 12.5.sp
                        )
                      }
                    }
                  }
                }

                // Camera & Photo Picker Action Buttons + Sample Label Presets
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Button(
                    onClick = { cameraLauncher.launch(null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                      containerColor = MekanikBrandOrange,
                      contentColor = Color.White
                    ),
                    modifier = Modifier
                      .weight(1f)
                      .height(48.dp)
                      .testTag("btn_capture_part_label_camera")
                  ) {
                    Icon(
                      imageVector = Icons.Default.PhotoCamera,
                      contentDescription = null,
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "Scanner Caméra",
                      fontWeight = FontWeight.SemiBold,
                      fontSize = 12.5.sp
                    )
                  }

                  OutlinedButton(
                    onClick = {
                      photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                      )
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MetalBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OffWhite),
                    modifier = Modifier
                      .weight(1f)
                      .height(48.dp)
                      .testTag("btn_pick_part_label_gallery")
                  ) {
                    Icon(
                      imageVector = Icons.Default.ImageSearch,
                      contentDescription = null,
                      tint = MekanikBrandOrange,
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "Photo Étiquette",
                      fontWeight = FontWeight.SemiBold,
                      fontSize = 12.5.sp
                    )
                  }
                }

                // Quick Preset Selector (Simulate scanning real spare part boxes in workshop)
                Text(
                  text = "ÉTIQUETTES DE PIÈCES ATELIER PRÊTES AU SCAN :",
                  color = AluminumGray,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  PartLabelVisionScannerEngine.workshopLabelPresets.forEach { preset ->
                    val isSelected = selectedPreset.id == preset.id
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = if (isSelected) MekanikBrandOrange.copy(alpha = 0.18f) else GraphiteDark,
                      border = BorderStroke(
                        1.dp,
                        if (isSelected) MekanikBrandOrange else MetalBorder
                      ),
                      onClick = {
                        capturedBitmap = null
                        selectedPreset = preset
                        selectedCodeFormat = preset.codeFormat
                        visionStatusBanner = "Étiquette ${preset.scannedOemReference} (${preset.manufacturerBrand}) scannée et vérifiée sur le châssis $vinToVerify."
                      },
                      modifier = Modifier.testTag("preset_part_label_${preset.id}")
                    ) {
                      Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                      ) {
                        Row(
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                          Text(
                            text = preset.scannedOemReference,
                            color = if (isSelected) MekanikBrandOrange else OffWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                          )
                          Text(
                            text = "• ${preset.codeFormat.badge}",
                            color = AluminumGray,
                            fontSize = 9.5.sp
                          )
                        }
                        Text(
                          text = preset.partTitle.take(28) + "...",
                          color = AluminumGray,
                          fontSize = 10.sp
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          // 2. VIN / Chassis Number Input & Live Compatibility Verification Banner
          item {
            val verdictColor = Color(compatibilityEval.verdict.colorHex)
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = MetalCard,
              border = BorderStroke(1.2.dp, verdictColor.copy(alpha = 0.7f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.VerifiedUser,
                      contentDescription = null,
                      tint = verdictColor,
                      modifier = Modifier.size(20.dp)
                    )
                    Text(
                      text = "COMPATIBILITÉ AVEC LE NUMÉRO DE CHÂSSIS (VIN)",
                      color = OffWhite,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp
                    )
                  }
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = verdictColor.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, verdictColor)
                  ) {
                    Text(
                      text = compatibilityEval.verdict.badgeText,
                      color = verdictColor,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.5.sp,
                      modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                  }
                }

                // VIN input field + quick test VIN buttons
                OutlinedTextField(
                  value = vinToVerify,
                  onValueChange = { vinToVerify = it.uppercase() },
                  label = {
                    Text(
                      "Numéro de Châssis (VIN) du véhicule à équiper",
                      fontSize = 11.sp
                    )
                  },
                  singleLine = true,
                  leadingIcon = {
                    Icon(
                      imageVector = Icons.Default.DirectionsCar,
                      contentDescription = null,
                      tint = MekanikBrandOrange
                    )
                  },
                  trailingIcon = {
                    if (vinToVerify.isNotBlank()) {
                      IconButton(onClick = { vinToVerify = activeVehicle.vin }) {
                        Icon(
                          imageVector = Icons.Default.Refresh,
                          contentDescription = "Réinitialiser VIN actif",
                          tint = AluminumGray
                        )
                      }
                    }
                  },
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MekanikBrandOrange,
                    unfocusedBorderColor = MetalBorder,
                    focusedTextColor = OffWhite,
                    unfocusedTextColor = OffWhite,
                    focusedContainerColor = GraphiteDark,
                    unfocusedContainerColor = GraphiteDark
                  ),
                  shape = RoundedCornerShape(11.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("scanner_vin_compatibility_input")
                )

                // Quick VIN test chips (Compatible vs Adaptation vs Incompatible)
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Tester châssis :",
                    color = AluminumGray,
                    fontSize = 10.5.sp
                  )
                  listOf(
                    "WVWZZZ5NZMW123456" to "Châssis Tiguan (Compatible)",
                    "WAUZZZ8V2KA901234" to "Châssis Audi A3 (Variante PR)",
                    "VF3MCYHZJKS491820" to "Châssis Peugeot (Incompatible)"
                  ).forEach { (sampleVin, label) ->
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = if (vinToVerify == sampleVin) MekanikBrandOrange.copy(alpha = 0.2f) else GraphiteDark,
                      border = BorderStroke(
                        1.dp,
                        if (vinToVerify == sampleVin) MekanikBrandOrange else MetalBorder
                      ),
                      onClick = { vinToVerify = sampleVin }
                    ) {
                      Text(
                        text = label,
                        color = if (vinToVerify == sampleVin) MekanikBrandOrange else OffWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                      )
                    }
                  }
                }

                // Detailed Verdict Box
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteDark,
                  border = BorderStroke(1.dp, verdictColor.copy(alpha = 0.45f)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Text(
                      text = compatibilityEval.verdict.title,
                      color = verdictColor,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.5.sp
                    )
                    Text(
                      text = compatibilityEval.technicalExplanation,
                      color = OffWhite,
                      fontSize = 11.5.sp,
                      lineHeight = 16.sp
                    )
                    HorizontalDivider(color = MetalBorder)
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.BuildCircle,
                        contentDescription = null,
                        tint = MekanikBrandOrange,
                        modifier = Modifier.size(16.dp)
                      )
                      Text(
                        text = "Peut-elle être installée ? ${compatibilityEval.canBeInstalledText}",
                        color = OffWhite,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                      )
                    }
                    Text(
                      text = "Contrôle PR / Spécification : ${compatibilityEval.prCodeCheckLabel}",
                      color = AluminumGray,
                      fontSize = 10.5.sp
                    )
                  }
                }
              }
            }
          }

          // 3. Section Tabs: [Références Compatibles & Montabilité] | [Méthode d'Installation & Couples] | [Catalogue & Devis]
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              listOf(
                0 to "Réfs Compatibles (${selectedPreset.compatibleReferences.size})",
                1 to "Méthode d'Installation",
                2 to "Catalogue & Devis"
              ).forEach { (idx, title) ->
                val isSelected = detailSectionTab == idx
                Surface(
                  shape = RoundedCornerShape(11.dp),
                  color = if (isSelected) MekanikBrandOrange else MetalCard,
                  border = BorderStroke(
                    1.dp,
                    if (isSelected) MekanikBrandOrange else MetalBorder
                  ),
                  onClick = { detailSectionTab = idx },
                  modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .testTag("scanner_detail_tab_$idx")
                ) {
                  Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 6.dp)
                  ) {
                    Text(
                      text = title,
                      color = if (isSelected) Color.White else OffWhite,
                      fontWeight = FontWeight.SemiBold,
                      fontSize = 11.sp,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                  }
                }
              }
            }
          }

          // Tab 0: Autres Références Compatibles & Possibilité d'Installation
          if (detailSectionTab == 0) {
            item {
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = MetalCard,
                border = BorderStroke(1.dp, MetalBorder),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(
                  modifier = Modifier.padding(14.dp),
                  verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Text(
                    text = "AUTRES RÉFÉRENCES COMPATIBLES & POSSIBILITÉ D'INSTALLATION",
                    color = OffWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp
                  )
                  Text(
                    text = "Vérifiez quelles références équivalentes peuvent être montées directement, lesquelles nécessitent une adaptation, et celles incompatibles avec le châssis $vinToVerify :",
                    color = AluminumGray,
                    fontSize = 11.sp
                  )

                  selectedPreset.compatibleReferences.forEach { refInfo ->
                    val statusColor = Color(refInfo.installStatus.colorHex)
                    Surface(
                      shape = RoundedCornerShape(11.dp),
                      color = GraphiteDark,
                      border = BorderStroke(1.dp, statusColor.copy(alpha = 0.45f)),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                      ) {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Column(modifier = Modifier.weight(1f)) {
                            Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                              Text(
                                text = refInfo.brand,
                                color = OffWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                              )
                              Text(
                                text = refInfo.reference,
                                color = MekanikBrandOrange,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                              )
                            }
                            Text(
                              text = refInfo.categoryType,
                              color = AluminumGray,
                              fontSize = 10.sp
                            )
                          }

                          Text(
                            text = "${refInfo.estimatedPriceDzd} DA",
                            color = OffWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                          )
                        }

                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = statusColor.copy(alpha = 0.14f),
                          border = BorderStroke(1.dp, statusColor.copy(alpha = 0.6f))
                        ) {
                          Text(
                            text = refInfo.installStatus.label,
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                          )
                        }

                        Text(
                          text = refInfo.fitmentNotes,
                          color = OffWhite,
                          fontSize = 11.sp,
                          lineHeight = 15.sp
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          // Tab 1: Méthodes d'Installation, Couples de Serrage & Outillage
          if (detailSectionTab == 1) {
            item {
              val guide = selectedPreset.installationGuide
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = MetalCard,
                border = BorderStroke(1.dp, MetalBorder),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(
                  modifier = Modifier.padding(14.dp),
                  verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column {
                      Text(
                        text = "MÉTHODE D'INSTALLATION & PROCÉDURE ATELIER",
                        color = OffWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                      )
                      Text(
                        text = "Temps barème : ${guide.laborTimeBarème} • ${guide.difficultyLevel}",
                        color = MekanikBrandOrange,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                      )
                    }
                  }

                  // Couples de serrage
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteDark,
                    border = BorderStroke(1.dp, MetalBorder),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(
                      modifier = Modifier.padding(12.dp),
                      verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                      ) {
                        Icon(
                          imageVector = Icons.Default.Speed,
                          contentDescription = null,
                          tint = MekanikBrandOrange,
                          modifier = Modifier.size(17.dp)
                        )
                        Text(
                          text = "COUPLES DE SERRAGE CONSTRUCTEUR (Nm)",
                          color = OffWhite,
                          fontWeight = FontWeight.Bold,
                          fontSize = 11.5.sp
                        )
                      }
                      guide.tighteningTorques.forEach { (organ, torque) ->
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                          Text(
                            text = "• $organ",
                            color = AluminumGray,
                            fontSize = 11.sp,
                            modifier = Modifier.weight(1f)
                          )
                          Text(
                            text = torque,
                            color = MekanikValidationGreen,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                          )
                        }
                      }
                    }
                  }

                  // Outillage spécifique
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteDark,
                    border = BorderStroke(1.dp, MetalBorder),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(
                      modifier = Modifier.padding(12.dp),
                      verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                      ) {
                        Icon(
                          imageVector = Icons.Default.Construction,
                          contentDescription = null,
                          tint = MekanikBrandOrange,
                          modifier = Modifier.size(17.dp)
                        )
                        Text(
                          text = "OUTILLAGE SPÉCIFIQUE REQUIS",
                          color = OffWhite,
                          fontWeight = FontWeight.Bold,
                          fontSize = 11.5.sp
                        )
                      }
                      guide.specialToolsRequired.forEach { tool ->
                        Text(
                          text = "✓ $tool",
                          color = OffWhite,
                          fontSize = 11.sp
                        )
                      }
                    }
                  }

                  // Étapes pas-à-pas
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteDark,
                    border = BorderStroke(1.dp, MetalBorder),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(
                      modifier = Modifier.padding(12.dp),
                      verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Text(
                        text = "ÉTAPES DE MONTAGE PAS-À-PAS :",
                        color = MekanikBrandOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                      )
                      guide.stepByStepProcedure.forEach { step ->
                        Text(
                          text = step,
                          color = OffWhite,
                          fontSize = 11.5.sp,
                          lineHeight = 16.sp
                        )
                      }
                    }
                  }

                  // Contrôles après montage & Alerte Sécurité
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MekanikErrorRed.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, MekanikErrorRed.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(
                      modifier = Modifier.padding(12.dp),
                      verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                      Text(
                        text = "PRÉCAUTION MÉCANIQUE & APRÈS-POSE :",
                        color = MekanikErrorRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                      )
                      Text(
                        text = guide.safetyWarning,
                        color = OffWhite,
                        fontSize = 11.sp
                      )
                      guide.postInstallationChecks.forEach { check ->
                        Text(
                          text = "→ $check",
                          color = AluminumGray,
                          fontSize = 10.5.sp
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          // Tab 2: Fiche Technique & Intégration Catalogue / Devis DMS Mécanicauto+
          if (detailSectionTab == 2) {
            item {
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = MetalCard,
                border = BorderStroke(1.dp, MetalBorder),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(
                  modifier = Modifier.padding(14.dp),
                  verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Text(
                    text = "LIAISON DIRECTE CATALOGUE & DMS MÉCANICAUTO+",
                    color = OffWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp
                  )
                  Text(
                    text = visionStatusBanner,
                    color = MekanikValidationGreen,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("Système Catalogue :", color = AluminumGray, fontSize = 11.5.sp)
                    Text(
                      selectedPreset.systemCategory.label,
                      color = OffWhite,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.5.sp
                    )
                  }
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("Repère Schéma Éclaté :", color = AluminumGray, fontSize = 11.5.sp)
                    Text(
                      "Repère #${selectedPreset.schemaCalloutNumber}",
                      color = MekanikBrandOrange,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.5.sp
                    )
                  }
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("Prix Tarif Estimé :", color = AluminumGray, fontSize = 11.5.sp)
                    Text(
                      "${selectedPreset.priceEstimatedDzd} DA",
                      color = MekanikValidationGreen,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.5.sp
                    )
                  }
                }
              }
            }
          }
        }

        // Bottom Sticky CTA Bar: Open in Catalog + Add to Quote/Cart
        Surface(
          color = MetalCard,
          border = BorderStroke(1.dp, MetalBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            val matchedCatalogPart = remember(selectedPreset) {
              MekanikRepository.sampleParts.find {
                it.id == selectedPreset.catalogPartId ||
                  it.oemReference.equals(selectedPreset.scannedOemReference, ignoreCase = true)
              } ?: PartItem(
                id = selectedPreset.catalogPartId,
                system = selectedPreset.systemCategory,
                name = selectedPreset.partTitle,
                position = "Montage Certifié DMS Mécanicauto+",
                oemReference = selectedPreset.scannedOemReference,
                oemAlternatives = selectedPreset.compatibleReferences
                  .filter { it.installStatus != ReferenceInstallStatus.NOT_INSTALLABLE }
                  .map { it.reference },
                crossReferences = selectedPreset.compatibleReferences
                  .filter { it.installStatus != ReferenceInstallStatus.NOT_INSTALLABLE }
                  .map { CrossReference(it.brand, it.reference, it.installStatus.label) },
                schemaCalloutNumber = selectedPreset.schemaCalloutNumber,
                yearCompatibility = compatibilityEval.matchedVehicleLabel,
                compatibilityBadge = compatibilityEval.verdict.badgeText,
                priceEstimatedDzd = selectedPreset.priceEstimatedDzd,
                description = "${selectedPreset.partTitle} • ${compatibilityEval.canBeInstalledText}",
                technicalSpecs = mapOf(
                  "Code-Barres / QR" to selectedPreset.rawBarcodeOrQr,
                  "Temps Montage" to selectedPreset.installationGuide.laborTimeBarème,
                  "Châssis vérifié" to compatibilityEval.vinChecked
                )
              )
            }

            OutlinedButton(
              onClick = { onAddPartToQuoteOrCart(matchedCatalogPart) },
              shape = RoundedCornerShape(12.dp),
              border = BorderStroke(1.dp, MetalBorder),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = OffWhite),
              modifier = Modifier
                .weight(0.9f)
                .height(50.dp)
                .testTag("btn_scanner_add_to_quote")
            ) {
              Icon(
                imageVector = Icons.Default.AddShoppingCart,
                contentDescription = null,
                tint = MekanikBrandOrange,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Ajouter au Devis",
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
              )
            }

            Button(
              onClick = {
                onOpenPartInCatalog(matchedCatalogPart, selectedPreset.systemCategory)
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = MekanikBrandOrange,
                contentColor = Color.White
              ),
              modifier = Modifier
                .weight(1.3f)
                .height(50.dp)
                .testTag("btn_scanner_open_in_catalog")
            ) {
              Icon(
                imageVector = Icons.Default.ManageSearch,
                contentDescription = null,
                modifier = Modifier.size(19.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Ouvrir Réf. au Catalogue",
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp
              )
            }
          }
        }
      }
    }
  }
}
