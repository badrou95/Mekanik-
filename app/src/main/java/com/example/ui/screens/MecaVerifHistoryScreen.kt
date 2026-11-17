package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class MecaVerifTrustVerdict(
  val title: String,
  val subtitle: String,
  val colorHex: Long
) {
  CERTIFIED_CLEAN(
    title = "ACHAT RECOMMANDÉ • HISTORIQUE CERTIFIÉ SAIN",
    subtitle = "Kilométrage réel cohérent • Aucun choc structurel carrosserie • Entretiens mécaniques suivis",
    colorHex = 0xFF22C55E
  ),
  MILEAGE_FRAUD_ALERT(
    title = "ALERTE CRITIQUE • COMPTEUR KILOMÉTRIQUE TRAFIQUÉ",
    subtitle = "Le kilométrage affiché au compteur ne correspond pas aux relevés certifiés DMS Mécanicauto+",
    colorHex = 0xFFEF4444
  ),
  BODYWORK_STRUCTURAL_WARNING(
    title = "VIGILANCE • DOMMAGES TÔLERIE & CARROSSERIE DÉTECTÉS",
    subtitle = "Sinistre carrosserie avec photos Avant/Après enregistré en base • Vérifier les longerons et la tôle",
    colorHex = 0xFFF07836
  )
}

enum class MecaVerifPaymentMethod(
  val label: String,
  val subtitle: String,
  val badge: String
) {
  CIB_SATIM("Carte CIB / SATIM", "Paiement interbancaire sécurisé Algérie", "CIB"),
  EDAHABIA("Carte Edahabia", "Algérie Poste • Validation instantanée", "POSTE"),
  BARIDIMOB("BaridiMob Paiement Mobile", "Paiement rapide par application", "MOBILE"),
  VISA_MASTERCARD("Visa / Mastercard", "Carte bancaire internationale", "INTL")
}

data class MileageHistoryPoint(
  val dateLabel: String,
  val year: Int,
  val recordedKm: Int,
  val sourceWorkshop: String,
  val eventType: String,
  val isAnomalyRollback: Boolean = false
)

data class BodyworkShopRecord(
  val id: String,
  val date: String,
  val mileageAtEventKm: Int,
  val bodyShopName: String,
  val impactZone: String,
  val severityLevel: String,
  val isStructuralOrAirbag: Boolean,
  val worksPerformed: List<String>,
  val replacedBodyPartsOem: List<String>,
  val repairCostDzd: Int,
  // Photos obligatoires AVANT / APRÈS réparation carrosserie & dommages sur la tôle
  val beforePhotoTitle: String = "Photo AVANT Réparation • Dommages sur la tôle constatés",
  val beforePhotoNotes: String = "Tôle froissée et déformation visible avant intervention du tôlier.",
  val beforePaintMicrons: Int = 95,
  val afterPhotoTitle: String = "Photo APRÈS Réparation • Tôlerie redressée & Peinture étuve",
  val afterPhotoNotes: String = "Alignement des jeux de carrosserie conforme et vernis étuve contrôlé.",
  val afterPaintMicrons: Int = 185,
  val mandatoryPhotosCertified: Boolean = true
)

data class MechanicShopRecord(
  val id: String,
  val date: String,
  val mileageAtVisitKm: Int,
  val mechanicShopName: String,
  val visitCategory: String,
  val operationsPerformed: List<String>,
  val installedPartsReferences: List<String>,
  val mechanicObservation: String,
  val invoiceAmountDzd: Int
)

data class BodyshopChassisPartItem(
  val id: String,
  val category: String, // "TÔLERIE ACIER / ALU", "PLASTIQUE & BOUCLIERS", "OPTIQUES & VITRAGE", "CLIPS & AGRAFES"
  val partName: String,
  val oemReference: String,
  val materialSpec: String,
  val sideOrZone: String,
  val priceDzd: Int,
  val fittingNotes: String
)

data class MecaVerifVehicleReport(
  val vin: String,
  val vehicleTitle: String,
  val engineAndGearbox: String,
  val paintColorCode: String,
  val firstRegistrationYear: Int,
  val originCountry: String,
  val previousOwnersCount: Int,
  val usageHistory: String,
  val stolenOrPledgedStatus: String,
  val defaultDashboardOdometerKm: Int,
  val summaryHighlights: List<String>,
  val mileageTimeline: List<MileageHistoryPoint>,
  val bodyworkRecords: List<BodyworkShopRecord>,
  val mechanicRecords: List<MechanicShopRecord>,
  val bodyshopChassisParts: List<BodyshopChassisPartItem>,
  val impactedZones: Set<String>
)

data class MecaVerifPricingPlan(
  val id: String,
  val title: String,
  val badge: String,
  val priceDzd: Int,
  val priceEur: String,
  val targetAudience: String,
  val description: String
)

object MecaVerifRepository {

  // Service Externe hors abonnement : 250 DA ou 300 DA par numéro de châssis (VIN) à chaque utilisation !
  val pricingPlans = listOf(
    MecaVerifPricingPlan(
      id = "PLAN_VIN_300_PARTICULIER",
      title = "Consultation par VIN • Tarif Particulier (300 DA)",
      badge = "300 DA / CHÂSSIS",
      priceDzd = 300,
      priceEur = "2,00 € / VIN",
      targetAudience = "Acheteurs Particuliers & Conducteurs",
      description = "Paiement externe à l'acte (non inclus dans l'abonnement) : débloque pour ce numéro de châssis le graphique kilométrique type CarVertical, les photos Avant/Après de la tôle et les passages mécanicien."
    ),
    MecaVerifPricingPlan(
      id = "PLAN_VIN_250_PRO",
      title = "Consultation par VIN • Tarif Pro Atelier / Tôlier / Magasin (250 DA)",
      badge = "250 DA / CHÂSSIS",
      priceDzd = 250,
      priceEur = "1,70 € / VIN",
      targetAudience = "Ateliers Mécaniques, Carrossiers Tôliers & Vendeurs Pro",
      description = "Tarif professionnel préférentiel à l'acte (externe aux abonnements DMS) : payez 250 DA à chaque numéro de châssis consulté pour obtenir l'historique complet et les photos de tôlerie."
    )
  )

  private val defaultBodyshopPartsTiguan = listOf(
    BodyshopChassisPartItem(
      id = "BP-01",
      category = "TÔLERIE ACIER / ALU",
      partName = "Capot moteur avant en acier galvanisé",
      oemReference = "5NA 823 031 D",
      materialSpec = "Tôle Acier HLE 0.8mm • Apprêt Cataphorèse Noir",
      sideOrZone = "Bloc Avant Supérieur",
      priceDzd = 64000,
      fittingNotes = "Réglage charnières couple 22 Nm + reprise joint d'étanchéité capot."
    ),
    BodyshopChassisPartItem(
      id = "BP-02",
      category = "TÔLERIE ACIER / ALU",
      partName = "Aile avant droite avec logement élargisseur R-Line",
      oemReference = "5NA 821 106 B",
      materialSpec = "Tôle Acier Embouti OEM • Prête à peindre",
      sideOrZone = "Latéral Avant Droit",
      priceDzd = 34500,
      fittingNotes = "9 vis Torx T30 + cordon mastic anti-corrosion sur bavette inférieure."
    ),
    BodyshopChassisPartItem(
      id = "BP-03",
      category = "TÔLERIE ACIER / ALU",
      partName = "Traverse de choc avant supérieure & absorbeur",
      oemReference = "5NA 807 109 C",
      materialSpec = "Acier Ultra-Haute Limite Élastique (UHSS)",
      sideOrZone = "Structure Châssis Avant",
      priceDzd = 42000,
      fittingNotes = "Serrage sur têtes de longerons à 55 Nm + contrôle capteur crash airbag."
    ),
    BodyshopChassisPartItem(
      id = "BP-04",
      category = "PLASTIQUE & BOUCLIERS",
      partName = "Pare-chocs avant complet (Bouclier PP/EPDM avec trous PDC)",
      oemReference = "5NA 807 217 R GRU",
      materialSpec = "Plastique PP+EPDM Thermoplastique Souple Apprêté",
      sideOrZone = "Bouclier Avant",
      priceDzd = 52000,
      fittingNotes = "Nécessite recalibrage radar ACC frontal après dépose/repose du bouclier."
    ),
    BodyshopChassisPartItem(
      id = "BP-05",
      category = "PLASTIQUE & BOUCLIERS",
      partName = "Calandre centrale supérieure & support emblème radar",
      oemReference = "5NA 853 651 AC",
      materialSpec = "Plastique ABS Haute Résistance + Chrome Noir",
      sideOrZone = "Face Avant",
      priceDzd = 26500,
      fittingNotes = "Clipsage direct sur support serrure + 4 vis supérieures Torx T25."
    ),
    BodyshopChassisPartItem(
      id = "BP-06",
      category = "PLASTIQUE & BOUCLIERS",
      partName = "Pare-boue / Passage de roue plastique avant droit",
      oemReference = "5NA 805 912 F",
      materialSpec = "Polypropylène (PP) Insonorisé",
      sideOrZone = "Passage de Roue AVD",
      priceDzd = 9800,
      fittingNotes = "Fixation par 12 vis auto-taraudeuses N 909 747 01."
    ),
    BodyshopChassisPartItem(
      id = "BP-07",
      category = "OPTIQUES & VITRAGE",
      partName = "Projecteur avant droit Full LED Matrix IQ.Light",
      oemReference = "5NA 941 082 F",
      materialSpec = "Polycarbonate Optique Anti-UV + Boîtier AFS",
      sideOrZone = "Optique Avant Droit",
      priceDzd = 94000,
      fittingNotes = "Transférer calculateur ballast LED ou coder module 55 au diagnostic."
    ),
    BodyshopChassisPartItem(
      id = "BP-08",
      category = "CLIPS & AGRAFES",
      partName = "Kit 30 Agrafes, Clips & Glissières de Pare-Chocs / Ailes",
      oemReference = "5NA-807-184-KIT",
      materialSpec = "Polyamide PA66 Renforcé Fibre de Verre",
      sideOrZone = "Fixations Carrosserie & Plastique",
      priceDzd = 4200,
      fittingNotes = "Remplacement systématique des glissières latérales après démontage."
    )
  )

  val preloadedReports: List<MecaVerifVehicleReport> = listOf(
    // 1. Véhicule 100% Sain (Kilométrage cohérent, 1 petite retouche cosmétique avec photos Avant/Après)
    MecaVerifVehicleReport(
      vin = "WVWZZZ5NZMW123456",
      vehicleTitle = "Volkswagen Tiguan II Phase 2 (2.0 TDI 150 R-Line)",
      engineAndGearbox = "2.0 TDI (Code DFGA) • Boîte DSG7 DQ381",
      paintColorCode = "LC9X (Noir Intense Nacré)",
      firstRegistrationYear = 2021,
      originCountry = "Allemagne / Import Certifié Algérie",
      previousOwnersCount = 1,
      usageHistory = "Usage Particulier Exclusif (Aucun historique Taxi / Location)",
      stolenOrPledgedStatus = "Aucun signalement de vol • Aucun gage financier Interpol / Sûreté",
      defaultDashboardOdometerKm = 88500,
      summaryHighlights = listOf(
        "6 relevés kilométriques certifiés entre 2021 et 2026 : courbe 100% cohérente avec 88 500 km.",
        "Aucun choc sur la tôle ni passage au marbre : 1 seule retouche cosmétique sur bouclier plastique AR avec photos Avant/Après certifiées.",
        "2 dossiers d'entretien mécanique complets dans le réseau DMS Mécanicauto+ (Vidange DSG7 + Révision 2.0 TDI)."
      ),
      mileageTimeline = listOf(
        MileageHistoryPoint("Mars 2021", 2021, 15, "Concession VW Wolfsburg", "Livraison Mise en Circulation"),
        MileageHistoryPoint("Avril 2022", 2022, 24800, "Atelier Agréé VW", "1ère Révision LongLife & Filtres"),
        MileageHistoryPoint("Juin 2023", 2023, 49200, "DMS Mécanicauto+ Alger", "Entretien 50 000 km & Plaquettes Avant"),
        MileageHistoryPoint("Août 2024", 2024, 68400, "DMS Mécanicauto+ Hydra", "Vidange Boîte DSG7 (60k) & Diagnostic OBD"),
        MileageHistoryPoint("Octobre 2025", 2025, 84100, "Contrôle Technique Périodique", "Inspection Technique Zéro Défaut"),
        MileageHistoryPoint("Novembre 2026", 2026, 88500, "DMS Mécanicauto+ Bab Ezzouar", "Contrôle Pré-Vente & Lecture Calculateur")
      ),
      bodyworkRecords = listOf(
        BodyworkShopRecord(
          id = "BODY-101",
          date = "14 Janvier 2024",
          mileageAtEventKm = 58900,
          bodyShopName = "Atelier Carrosserie & Peinture El-Biar (Compte Carrosserie Certifié)",
          impactZone = "Pare-chocs Arrière (Plastique • Cosmétique uniquement)",
          severityLevel = "Léger • Éraflure vernis sur bouclier plastique sans déformation de la tôle",
          isStructuralOrAirbag = false,
          worksPerformed = listOf(
            "Ponçage et reprise rayure de stationnement sur bouclier arrière plastique",
            "Aucune tôle froissée ni longeron touché (photos Avant/Après prises par le tôlier)",
            "Épaisseur peinture contrôlée au micromètre sur toute la tôle : 115 µm (Origine Usine)"
          ),
          replacedBodyPartsOem = listOf("Catadioptre arrière droit (5NA 945 106)"),
          repairCostDzd = 18500,
          beforePhotoTitle = "Photo AVANT • Rayure vernis coin droit pare-chocs AR",
          beforePhotoNotes = "Éraflure superficielle de 14 cm sur le plastique du bouclier arrière. Tôle du hayon et aile AR 100% intactes.",
          beforePaintMicrons = 115,
          afterPhotoTitle = "Photo APRÈS • Raccord vernis LC9X étuve & lustrage",
          afterPhotoNotes = "Bouclier repeint en cabine étuve 60°C, teinte exacte LC9X et catadioptre neuf clipsé.",
          afterPaintMicrons = 135,
          mandatoryPhotosCertified = true
        )
      ),
      mechanicRecords = listOf(
        MechanicShopRecord(
          id = "MECA-201",
          date = "12 Novembre 2026",
          mileageAtVisitKm = 88500,
          mechanicShopName = "Atelier Pro DMS Mécanicauto+ Bab Ezzouar",
          visitCategory = "Contrôle Mécanique & Diagnostic Complet",
          operationsPerformed = listOf(
            "Lecture calculateurs moteur/boîte/ABS : 0 code défaut actif",
            "Contrôle pression turbo et valeurs injecteurs 1 à 4 conformes",
            "Remplacement cartouche filtre à huile et huile 0W-30 VW 507.00"
          ),
          installedPartsReferences = listOf("03N 115 562 B", "G 052 195 M4"),
          mechanicObservation = "Véhicule en parfait état mécanique. Kilométrage 88 500 km confirmé dans l'odomètre interne de la boîte DSG7 et du calculateur moteur EDC17.",
          invoiceAmountDzd = 22400
        ),
        MechanicShopRecord(
          id = "MECA-202",
          date = "19 Août 2024",
          mileageAtVisitKm = 68400,
          mechanicShopName = "Garage Réseau DMS Mécanicauto+ Hydra",
          visitCategory = "Vidange Boîte DSG7 & Freinage Avant",
          operationsPerformed = listOf(
            "Vidange huile boîte robotisée DSG7 DQ381 (6L + Filtre hydraulique)",
            "Remplacement kit plaquettes de frein avant Bosch/OEM",
            "Calibrage point de léchage embrayages K1/K2 à la valise"
          ),
          installedPartsReferences = listOf("5Q0 698 151 AP", "0GC 325 183 A", "G 055 529 A2"),
          mechanicObservation = "Entretien boîte respecté dans les délais constructeurs.",
          invoiceAmountDzd = 46800
        )
      ),
      bodyshopChassisParts = defaultBodyshopPartsTiguan,
      impactedZones = emptySet()
    ),

    // 2. Véhicule avec FRAUDE AU COMPTEUR (-82 300 km trafiqués !) + Dommages Tôlerie Avant Droit (Photos Avant/Après)
    MecaVerifVehicleReport(
      vin = "VF3MCYHZJKS491820",
      vehicleTitle = "Peugeot 3008 II GT Line (1.5 BlueHDi 130 EAT8)",
      engineAndGearbox = "1.5 BlueHDi (Code DV5RC) • Boîte Automatique EAT8",
      paintColorCode = "KWE (Blanc Nacré Tri-Couche)",
      firstRegistrationYear = 2020,
      originCountry = "France / Import Occasion",
      previousOwnersCount = 3,
      usageHistory = "Ex-Flotte VTC / Société puis revente particulier",
      stolenOrPledgedStatus = "Non volé • Attention : Compteur kilométrique rajeuni de -82 300 km !",
      defaultDashboardOdometerKm = 112000,
      summaryHighlights = listOf(
        "FRAUDE COMPTEUR DÉTECTÉE : Relevé certifié à 194 300 km en Mai 2025, mais affiché aujourd'hui à 112 000 km (-82 300 km reculés) !",
        "CHOC TÔLERIE AVANT DROIT : Aile avant droite, capot et pare-chocs remplacés en Septembre 2024 (Photos Avant/Après obligatoires disponibles).",
        "Historique mécanique signalant un remplacement de chaîne d'arbres à cames 7mm -> 8mm à 168 000 km."
      ),
      mileageTimeline = listOf(
        MileageHistoryPoint("Février 2020", 2020, 10, "Concession Peugeot Lyon", "Mise en circulation"),
        MileageHistoryPoint("Juin 2021", 2021, 54200, "Réseau Peugeot Pro", "Révision 50 000 km & Plaquettes"),
        MileageHistoryPoint("Novembre 2022", 2022, 118900, "Atelier Eurorepar", "Distribution & Pompe à eau"),
        MileageHistoryPoint("Septembre 2024", 2024, 168400, "DMS Mécanicauto+ Oran", "Kit Chaîne AAC 8mm & Choc Avant Droit"),
        MileageHistoryPoint("Mai 2025", 2025, 194300, "DMS Mécanicauto+ Alger", "Remplacement FAP & AdBlue (194 300 km réels)"),
        MileageHistoryPoint("Novembre 2026", 2026, 112000, "Compteur Actuel Déclaré", "ALERTE RECUL COMPTEUR (-82 300 KM)", isAnomalyRollback = true)
      ),
      bodyworkRecords = listOf(
        BodyworkShopRecord(
          id = "BODY-202",
          date = "18 Septembre 2024",
          mileageAtEventKm = 168400,
          bodyShopName = "Carrosserie & Tôlerie Industrielle Oran (Compte Carrosserie Pro)",
          impactZone = "Bloc Avant Droit : Aile en tôle, Capot, Traverse & Pare-chocs",
          severityLevel = "Moyen à Important • Tôle d'aile et capot froissés (Longeron intact)",
          isStructuralOrAirbag = true,
          worksPerformed = listOf(
            "Dépose de l'aile avant droite en tôle pliée et du capot moteur",
            "Redressage doublure d'aile au tire-clou et remplacement traverse avant",
            "Montage aile neuve OEM, bouclier avant et projecteur Full LED droit",
            "Peinture tri-couche Blanc Nacré KWE en cabine (épaisseur mesurée : 260 µm)"
          ),
          replacedBodyPartsOem = listOf(
            "98 142 508 80 (Aile AVD Tôle)",
            "98 115 441 80 (Capot Moteur)",
            "98 164 892 80 (Bouclier Plastique AV)",
            "98 204 119 80 (Projecteur Full LED Droit)"
          ),
          repairCostDzd = 245000,
          beforePhotoTitle = "Photo AVANT Obligatoire • Choc Avant Droit (Tôle d'aile & capot pliés)",
          beforePhotoNotes = "Enfoncement de 18 cm sur l'aile avant droite en tôle, pattes du phare LED cassées et bouclier plastique fissuré.",
          beforePaintMicrons = 110,
          afterPhotoTitle = "Photo APRÈS Obligatoire • Aile & Capot remplacés + Peinture KWE",
          afterPhotoNotes = "Éléments de tôlerie et plastique remplacés, alignement capot/aile/phare validé et peinture nacrée séchée en étuve.",
          afterPaintMicrons = 260,
          mandatoryPhotosCertified = true
        )
      ),
      mechanicRecords = listOf(
        MechanicShopRecord(
          id = "MECA-301",
          date = "09 Mai 2025",
          mileageAtVisitKm = 194300,
          mechanicShopName = "Atelier Pro DMS Mécanicauto+ Alger",
          visitCategory = "Diagnostic Réservoir AdBlue & Régénération FAP",
          operationsPerformed = listOf(
            "Lecture kilométrage calculateur moteur CMU : 194 312 km certifiés",
            "Remplacement injecteur AdBlue et nettoyage filtre à particules"
          ),
          installedPartsReferences = listOf("98 027 638 80", "16 113 295 80"),
          mechanicObservation = "Véhicule ayant réellement 194 300 km en Mai 2025. Tout affichage inférieur ultérieur prouve une manipulation du combiné d'instruments.",
          invoiceAmountDzd = 58000
        )
      ),
      bodyshopChassisParts = defaultBodyshopPartsTiguan,
      impactedZones = setOf("FRONT", "RIGHT")
    ),

    // 3. Véhicule avec Passage au Marbre Carrosserie & Photos Avant/Après Tôlerie (BMW 320d)
    MecaVerifVehicleReport(
      vin = "WBA5A51070G908112",
      vehicleTitle = "BMW Série 3 (G20) 320d xDrive Pack M Sport",
      engineAndGearbox = "2.0 TwinPower Turbo (Code B47D20) • Boîte ZF 8HP",
      paintColorCode = "C31 (Portimao Blau Metallic)",
      firstRegistrationYear = 2021,
      originCountry = "Allemagne / Import",
      previousOwnersCount = 2,
      usageHistory = "Usage Particulier",
      stolenOrPledgedStatus = "Aucun signalement de vol • Réparation carrosserie structurelle documentée avec photos Avant/Après",
      defaultDashboardOdometerKm = 94200,
      summaryHighlights = listOf(
        "Kilométrage réel cohérent (94 200 km) sans recul compteur.",
        "PASSAGE EN CARROSSERIE & MARBRE : Choc latéral gauche et demi-train en Mars 2025 (Photos Avant/Après de la tôle et rapport géométrie inclus).",
        "Suivi mécanique régulier chez spécialiste BMW (vidange boîte ZF 8HP effectuée)."
      ),
      mileageTimeline = listOf(
        MileageHistoryPoint("Mai 2021", 2021, 20, "BMW Munich", "Livraison neuve"),
        MileageHistoryPoint("Juillet 2022", 2022, 29400, "BMW Service", "Entretien CBS Huile & Microfiltre"),
        MileageHistoryPoint("Octobre 2023", 2023, 56800, "DMS Mécanicauto+ Chéraga", "Plaquettes M Sport & Liquide de frein"),
        MileageHistoryPoint("Mars 2025", 2025, 78900, "Atelier Carrosserie & Marbre Laser Alger", "Sinistre Latéral Gauche & Redressage Tôle"),
        MileageHistoryPoint("Novembre 2026", 2026, 94200, "DMS Mécanicauto+ Hydra", "Vidange Boîte ZF 8HP & Pont xDrive")
      ),
      bodyworkRecords = listOf(
        BodyworkShopRecord(
          id = "BODY-303",
          date = "22 Mars 2025",
          mileageAtEventKm = 78900,
          bodyShopName = "Atelier Carrosserie & Marbre Laser Chéraga (Compte Carrosserie Certifié)",
          impactZone = "Latéral Gauche : Porte conducteur, Bas de caisse tôle & Pied milieu B",
          severityLevel = "Structurel • Remplacement porte nue aluminium + redressage bas de caisse sur marbre",
          isStructuralOrAirbag = true,
          worksPerformed = listOf(
            "Prise des photos obligatoires AVANT travaux : porte conducteur enfoncée et bas de caisse froissé",
            "Contrôle et tirage léger au marbre laser Celette (tolérance < 0.5 mm rétablie)",
            "Remplacement porte avant gauche nue OEM et bas de caisse plastique M Sport",
            "Prise des photos obligatoires APRÈS travaux : peinture Portimao Blau C31 (290 µm sur pied milieu)"
          ),
          replacedBodyPartsOem = listOf(
            "41 51 7 492 101 (Porte Avant Gauche Nue)",
            "51 77 8 075 125 (Bas de Caisse M Sport)",
            "51 33 7 468 209 (Lève-vitre & Vitrage latéral)"
          ),
          repairCostDzd = 310000,
          beforePhotoTitle = "Photo AVANT Obligatoire • Choc Latéral Gauche (Porte & Bas de caisse enfoncés)",
          beforePhotoNotes = "Déformation prononcée du panneau de porte avant gauche et enfoncement de la tôle du bas de caisse sur 45 cm.",
          beforePaintMicrons = 105,
          afterPhotoTitle = "Photo APRÈS Obligatoire • Porte neuve posée, Marbre 0.0mm & Peinture C31",
          afterPhotoNotes = "Porte OEM neuve alignée au dixième de millimètre, bas de caisse redressé/étamé et peinture Portimao Blau C31 validée.",
          afterPaintMicrons = 285,
          mandatoryPhotosCertified = true
        )
      ),
      mechanicRecords = listOf(
        MechanicShopRecord(
          id = "MECA-401",
          date = "02 Novembre 2026",
          mileageAtVisitKm = 94200,
          mechanicShopName = "Garage Spécialiste Allemandes DMS Mécanicauto+ Hydra",
          visitCategory = "Vidange Boîte Auto ZF 8HP & Boîte de Transfert xDrive",
          operationsPerformed = listOf(
            "Remplacement carter-crépine ZF d'origine + 7L huile ZF LifeguardFluid 8",
            "Géométrie 3D 4 roues contrôlée : alignement train avant/arrière 100% dans les normes"
          ),
          installedPartsReferences = listOf("24 11 8 612 901", "83 22 2 289 720"),
          mechanicObservation = "La réparation carrosserie de 2025 a été proprement exécutée, le train roulant est parfaitement aligné.",
          invoiceAmountDzd = 68500
        )
      ),
      bodyshopChassisParts = defaultBodyshopPartsTiguan,
      impactedZones = setOf("LEFT", "CHASSIS")
    ),

    // 4. Dacia Duster
    MecaVerifVehicleReport(
      vin = "UU15SDCL561928374",
      vehicleTitle = "Dacia Duster II Phase 2 (1.5 dCi 115 4x4 Extreme)",
      engineAndGearbox = "1.5 Blue dCi (Code K9K 874) • Boîte Manuelle 6 rapports TL8",
      paintColorCode = "KQM (Gris Urbain)",
      firstRegistrationYear = 2022,
      originCountry = "Algérie / Roumanie",
      previousOwnersCount = 1,
      usageHistory = "Ancien Véhicule de Parc Entreprise (Entretien sous contrat DMS)",
      stolenOrPledgedStatus = "Aucun signalement de vol • Dossier de cession flotte complet",
      defaultDashboardOdometerKm = 79400,
      summaryHighlights = listOf(
        "Kilométrage certifié 79 400 km : 4 relevés d'atelier cohérents.",
        "1 remplacement de pare-brise et débosselage aile avant gauche avec photos Avant/Après.",
        "Kit distribution K9K et pompe à eau remplacés à 79 400 km."
      ),
      mileageTimeline = listOf(
        MileageHistoryPoint("Mars 2022", 2022, 12, "Concession Renault/Dacia Alger", "Mise en circulation"),
        MileageHistoryPoint("Mai 2023", 2023, 31500, "DMS Mécanicauto+ Blida", "Révision 30 000 km & Freins"),
        MileageHistoryPoint("Septembre 2024", 2024, 58200, "DMS Mécanicauto+ Tizi Ouzou", "Révision 60 000 km & Embrayage"),
        MileageHistoryPoint("Octobre 2026", 2026, 79400, "DMS Mécanicauto+ Alger", "Kit Distribution K9K & Vidange boîte/pont")
      ),
      bodyworkRecords = listOf(
        BodyworkShopRecord(
          id = "BODY-501",
          date = "11 Juin 2024",
          mileageAtEventKm = 52100,
          bodyShopName = "Atelier Carrosserie & Vitrage Rapide",
          impactZone = "Pare-brise & Aile Avant Gauche",
          severityLevel = "Cosmétique • Impact gravillon pare-brise & débosselage sans peinture aile",
          isStructuralOrAirbag = false,
          worksPerformed = listOf(
            "Remplacement pare-brise acoustique d'origine",
            "Débosselage sans peinture (DSP) sur aile avant gauche avec photos Avant/Après"
          ),
          replacedBodyPartsOem = listOf("72 71 268 42R (Pare-brise)"),
          repairCostDzd = 34000,
          beforePhotoTitle = "Photo AVANT • Bosse de stationnement aile AVG (5 cm) & fissure vitrage",
          beforePhotoNotes = "Léger coup de portière sur le galbe de l'aile avant gauche sans éclat de peinture.",
          beforePaintMicrons = 112,
          afterPhotoTitle = "Photo APRÈS • Débosselage sans peinture & Pare-brise neuf",
          afterPhotoNotes = "Tôle redressée par ventouse DSP en conservant la peinture d'usine (112 µm).",
          afterPaintMicrons = 112,
          mandatoryPhotosCertified = true
        )
      ),
      mechanicRecords = listOf(
        MechanicShopRecord(
          id = "MECA-501",
          date = "04 Octobre 2026",
          mileageAtVisitKm = 79400,
          mechanicShopName = "Garage Certifié DMS Mécanicauto+ Alger",
          visitCategory = "Kit Distribution + Pompe à Eau & Révision 4x4",
          operationsPerformed = listOf(
            "Remplacement kit courroie de distribution K9K + pompe à eau",
            "Vidange huile moteur 5W-30 RN17 et huile boîte/pont arrière"
          ),
          installedPartsReferences = listOf("13 0C 131 91R", "21 01 074 77R"),
          mechanicObservation = "Mécanique saine et distribution neuve, prêt à rouler sans frais immédiats.",
          invoiceAmountDzd = 49500
        )
      ),
      bodyshopChassisParts = defaultBodyshopPartsTiguan,
      impactedZones = emptySet()
    )
  )

  fun findOrBuildReport(vinInput: String): MecaVerifVehicleReport {
    val cleanVin = vinInput.trim().uppercase()
    val exact = preloadedReports.find { it.vin.equals(cleanVin, ignoreCase = true) }
    if (exact != null) return exact

    return preloadedReports.first().copy(
      vin = cleanVin.ifBlank { "WVWZZZ5NZMW123456" },
      vehicleTitle = "Véhicule Identifié par Châssis ($cleanVin) • Base DMS Mécanicauto+"
    )
  }
}

@Composable
fun MecaVerifHistoryScreen(
  initialVin: String = "WVWZZZ5NZMW123456",
  isBodyshopAccount: Boolean = false,
  isProAccount: Boolean = true,
  onOpenScannerForVin: () -> Unit = {}
) {
  val coroutineScope = rememberCoroutineScope()

  var vinQuery by remember { mutableStateOf(initialVin) }
  var currentReport by remember {
    mutableStateOf(MecaVerifRepository.findOrBuildReport(initialVin))
  }
  var currentDashboardKmText by remember(currentReport.vin) {
    mutableStateOf(currentReport.defaultDashboardOdometerKm.toString())
  }
  // 0: Graphique Kilométrique CarVertical, 1: Carrosserie & Photos Avant/Après, 2: Passages Mécanicien, 3: Espace Tôlier (Catalogue Tôlerie/Plastique & Prise Photos Avant/Après), 4: Certificat Officiel
  var selectedReportSectionTab by remember(isBodyshopAccount) {
    mutableIntStateOf(if (isBodyshopAccount) 3 else 0)
  }

  // Chaque numéro de châssis (VIN) nécessite un paiement externe de 250 DA (Pro) ou 300 DA (Particulier) !
  var unlockedVins by remember { mutableStateOf(emptySet<String>()) }
  var paidPerVinReceipts by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
  var showPaymentUnlockModal by remember { mutableStateOf(false) }
  var showReceiptsLedgerModal by remember { mutableStateOf(false) }
  var initialGatewayChannel by remember { mutableStateOf(MecaVerifGatewayChannel.SATIM_CIB) }
  var selectedPricingPlan by remember(isProAccount) {
    mutableStateOf(
      if (isProAccount) MecaVerifRepository.pricingPlans[1] // 250 DA Pro
      else MecaVerifRepository.pricingPlans[0] // 300 DA Particulier
    )
  }
  var selectedPaymentMethod by remember { mutableStateOf(MecaVerifPaymentMethod.CIB_SATIM) }

  // Zoomable Before/After Photo Modal state
  var activePhotoInspectionRecord by remember { mutableStateOf<BodyworkShopRecord?>(null) }

  // Espace Tôlier / Carrossier : Obligation de prendre les photos AVANT et APRÈS réparation
  var bodyshopSelectedPartCategory by remember { mutableStateOf("TOUT") }
  var bodyshopImpactZoneInput by remember { mutableStateOf("Aile Avant Droite & Bouclier Plastique") }
  var bodyshopRepairNotesInput by remember {
    mutableStateOf("Remplacement aile en tôle + débosselage et peinture étuve code constructeur")
  }
  var hasCapturedMandatoryBeforePhoto by remember(currentReport.vin) { mutableStateOf(false) }
  var hasCapturedMandatoryAfterPhoto by remember(currentReport.vin) { mutableStateOf(false) }
  var bodyshopSubmissionSuccessBanner by remember { mutableStateOf<String?>(null) }

  // Animated Report Generation State
  var isGeneratingFullReport by remember { mutableStateOf(false) }
  var generationStepMessage by remember { mutableStateOf("") }
  var generatedCertificateNumber by remember { mutableStateOf("MV-2026-84920") }

  val isReportUnlocked = unlockedVins.contains(currentReport.vin)
  val perVinPriceDzd = if (isProAccount) 250 else 300

  val triggerReportGenerationAfterPayment: (Int) -> Unit = { paidAmountDzd ->
    showPaymentUnlockModal = false
    isGeneratingFullReport = true
    coroutineScope.launch {
      generationStepMessage = "1/4 • Validation du paiement externe ($paidAmountDzd DA) pour le châssis ${currentReport.vin}..."
      delay(400L)
      generationStepMessage = "2/4 • Construction du graphique kilométrique chronologique..."
      delay(400L)
      generationStepMessage = "3/4 • Chargement des photos obligatoires Avant/Après des ateliers de carrosserie..."
      delay(400L)
      generationStepMessage = "4/4 • Édition du rapport complet certifié MécaVerif+™..."
      delay(350L)
      unlockedVins = unlockedVins + currentReport.vin
      paidPerVinReceipts = paidPerVinReceipts + (currentReport.vin to paidAmountDzd)
      generatedCertificateNumber = "MV-2026-${currentReport.vin.takeLast(5)}"
      isGeneratingFullReport = false
    }
  }

  // Dynamic comparison between user-entered current odometer reading and historical records
  val enteredCurrentKm = currentDashboardKmText.filter { it.isDigit() }.toIntOrNull()
    ?: currentReport.defaultDashboardOdometerKm
  val maxHistoricalKm = currentReport.mileageTimeline
    .filter { !it.isAnomalyRollback }
    .maxOfOrNull { it.recordedKm } ?: 0
  val isMileageRollbackDetected = enteredCurrentKm < maxHistoricalKm
  val rollbackDifferenceKm = if (isMileageRollbackDetected) maxHistoricalKm - enteredCurrentKm else 0

  val overallVerdict = when {
    isMileageRollbackDetected -> MecaVerifTrustVerdict.MILEAGE_FRAUD_ALERT
    currentReport.bodyworkRecords.any { it.isStructuralOrAirbag } -> MecaVerifTrustVerdict.BODYWORK_STRUCTURAL_WARNING
    else -> MecaVerifTrustVerdict.CERTIFIED_CLEAN
  }
  val verdictColor = Color(overallVerdict.colorHex)

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("mecaverif_history_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Brand Header: MÉCAVERIF+™ (Service Externe Payant à l'Acte : 250 DA ou 300 DA / VIN)
    item {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = MetalCard,
        border = BorderStroke(1.2.dp, MekanikBrandOrange.copy(alpha = 0.75f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MekanikBrandOrange.copy(alpha = 0.18f),
                border = BorderStroke(1.dp, MekanikBrandOrange),
                modifier = Modifier.size(46.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = MekanikBrandOrange,
                    modifier = Modifier.size(26.dp)
                  )
                }
              }
              Column {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = "MÉCAVERIF+™",
                    color = OffWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                  )
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MekanikBrandOrange
                  ) {
                    Text(
                      text = "EXTERNE • 250 / 300 DA PAR VIN",
                      color = Color.White,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.sp,
                      modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = "Graphique Kilométrique, Photos Tôle Avant/Après Carrosserie & Mécanicien",
                  color = AluminumGray,
                  fontSize = 11.5.sp
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = GraphiteDark,
              border = BorderStroke(1.dp, MekanikBrandOrange),
              onClick = { showPaymentUnlockModal = true },
              modifier = Modifier.testTag("btn_open_mecaverif_pricing")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Payments,
                  contentDescription = null,
                  tint = MekanikBrandOrange,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = "$perVinPriceDzd DA / VIN",
                  color = OffWhite,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 11.sp
                )
              }
            }
          }

          // Bannière explicite : Service Externe NON inclus dans les abonnements
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = GraphiteDark,
            border = BorderStroke(1.dp, MekanikBrandOrange.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MekanikBrandOrange,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = "Service externe hors abonnement : MécaVerif+™ n'est inclus dans aucun abonnement mensuel ou annuel. Chaque vérification d'historique est facturée à l'acte : 250 DA (Compte Pro) ou 300 DA (Particulier) par numéro de châssis.",
                color = OffWhite,
                fontSize = 10.5.sp,
                lineHeight = 14.5.sp
              )
            }
          }
        }
      }
    }

    // 1.5. COMPOSANT DE PAIEMENT SÉCURISÉ MÉCAVERIF+™ (CIB SATIM, EDAHABIA, BARIDIMOB QR, CHARGILY PAY & STRIPE)
    item {
      MecaVerifSecurePaymentSummaryBanner(
        currentVin = currentReport.vin,
        isCurrentVinUnlocked = isReportUnlocked,
        defaultPriceDzd = perVinPriceDzd,
        unlockedCount = unlockedVins.size,
        onOpenSecureCheckout = { channel ->
          if (channel != null) {
            initialGatewayChannel = channel
          }
          showPaymentUnlockModal = true
        },
        onOpenReceiptsHistory = {
          showReceiptsLedgerModal = true
        }
      )
    }

    // 2. ÉTAPE 1 : SAISIE DU NUMÉRO DE CHÂSSIS (VIN) & KILOMÉTRAGE ACTUEL
    item {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = MetalCard,
        border = BorderStroke(1.dp, MetalBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "ÉTAPE 1 • SAISIR LE NUMÉRO DE CHÂSSIS (VIN)",
              color = OffWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 12.5.sp
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (vinQuery.trim().length == 17) MekanikValidationGreen.copy(alpha = 0.16f) else GraphiteDark,
              border = BorderStroke(
                1.dp,
                if (vinQuery.trim().length == 17) MekanikValidationGreen else MetalBorder
              )
            ) {
              Text(
                text = "${vinQuery.trim().length}/17 caractères ISO",
                color = if (vinQuery.trim().length == 17) MekanikValidationGreen else AluminumGray,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
              )
            }
          }

          OutlinedTextField(
            value = vinQuery,
            onValueChange = { vinQuery = it.uppercase().take(17) },
            label = { Text("Numéro de Châssis (VIN 17 caractères)", fontSize = 11.sp) },
            placeholder = { Text("Ex: VF3MCYHZJKS491820", color = AluminumGray) },
            singleLine = true,
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Tag,
                contentDescription = null,
                tint = MekanikBrandOrange
              )
            },
            trailingIcon = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                if (vinQuery.isNotBlank()) {
                  IconButton(onClick = { vinQuery = "" }) {
                    Icon(
                      imageVector = Icons.Default.Clear,
                      contentDescription = "Effacer VIN",
                      tint = AluminumGray
                    )
                  }
                }
                IconButton(onClick = onOpenScannerForVin) {
                  Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Scanner VIN Carte Grise",
                    tint = MekanikBrandOrange
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
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("mecaverif_vin_input")
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = currentDashboardKmText,
              onValueChange = { newVal ->
                currentDashboardKmText = newVal.filter { it.isDigit() }
              },
              label = { Text("Kilométrage actuel au compteur (km)", fontSize = 11.sp) },
              singleLine = true,
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Speed,
                  contentDescription = null,
                  tint = if (isMileageRollbackDetected) MekanikErrorRed else MekanikValidationGreen
                )
              },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MekanikBrandOrange,
                unfocusedBorderColor = MetalBorder,
                focusedTextColor = OffWhite,
                unfocusedTextColor = OffWhite,
                focusedContainerColor = GraphiteDark,
                unfocusedContainerColor = GraphiteDark
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1.1f)
                .testTag("mecaverif_current_km_input")
            )

            Button(
              onClick = {
                val report = MecaVerifRepository.findOrBuildReport(vinQuery)
                currentReport = report
                currentDashboardKmText = report.defaultDashboardOdometerKm.toString()
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = MekanikBrandOrange,
                contentColor = Color.White
              ),
              modifier = Modifier
                .weight(0.9f)
                .height(54.dp)
                .testTag("btn_run_mecaverif_audit")
            ) {
              Icon(
                imageVector = Icons.Default.ManageSearch,
                contentDescription = null,
                modifier = Modifier.size(19.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Analyser VIN",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
          }

          // Quick scenario selector pills
          Text(
            text = "CHÂSSIS D'EXEMPLE (SAIN, COMPTEUR TRAFIQUÉ + PHOTOS TÔLE, MARBRE CARROSSERIE) :",
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
            listOf(
              Triple("WVWZZZ5NZMW123456", "VW Tiguan • Sain (88 500 km)", 0xFF22C55E),
              Triple("VF3MCYHZJKS491820", "Peugeot 3008 • Compteur Trafiqué (-82 300 km) & Choc Tôle", 0xFFEF4444),
              Triple("WBA5A51070G908112", "BMW 320d • Marbre Carrosserie & Photos Avant/Après", 0xFFF07836),
              Triple("UU15SDCL561928374", "Dacia Duster • Ex-Flotte (79 400 km)", 0xFF22C55E)
            ).forEach { (sampleVin, label, badgeColorHex) ->
              val isSelected = currentReport.vin == sampleVin
              val chipColor = Color(badgeColorHex)
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) chipColor.copy(alpha = 0.18f) else GraphiteDark,
                border = BorderStroke(1.dp, if (isSelected) chipColor else MetalBorder),
                onClick = {
                  vinQuery = sampleVin
                  val rep = MecaVerifRepository.findOrBuildReport(sampleVin)
                  currentReport = rep
                  currentDashboardKmText = rep.defaultDashboardOdometerKm.toString()
                },
                modifier = Modifier.testTag("mecaverif_sample_$sampleVin")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(CircleShape)
                      .background(chipColor)
                  )
                  Text(
                    text = label,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                  )
                }
              }
            }
          }
        }
      }
    }

    // Animated Progress Banner while generating full report
    if (isGeneratingFullReport) {
      item {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MetalCard,
          border = BorderStroke(1.5.dp, MekanikBrandOrange),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            CircularProgressIndicator(
              color = MekanikBrandOrange,
              strokeWidth = 3.dp,
              modifier = Modifier.size(28.dp)
            )
            Column {
              Text(
                text = "GÉNÉRATION DU RAPPORT COMPLET MÉCAVERIF+™ EN COURS...",
                color = MekanikBrandOrange,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp
              )
              Text(
                text = generationStepMessage,
                color = OffWhite,
                fontSize = 11.5.sp
              )
            }
          }
        }
      }
    }

    // 3. ÉTAPE 2 : RÉSUMÉ DE L'HISTORIQUE DU VÉHICULE & BOUTON DE PAIEMENT À L'ACTE (250 OU 300 DA / VIN)
    item {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = MetalCard,
        border = BorderStroke(1.5.dp, verdictColor),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("mecaverif_vehicle_summary_card")
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Assignment,
                contentDescription = null,
                tint = MekanikBrandOrange,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = "ÉTAPE 2 • RÉSUMÉ DE L'HISTORIQUE DU VÉHICULE",
                color = MekanikBrandOrange,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.5.sp
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = verdictColor.copy(alpha = 0.16f),
              border = BorderStroke(1.dp, verdictColor)
            ) {
              Text(
                text = if (isMileageRollbackDetected) "SCORE 24/100"
                else if (currentReport.bodyworkRecords.any { it.isStructuralOrAirbag }) "SCORE 62/100"
                else "SCORE 96/100",
                color = verdictColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.5.sp,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
              )
            }
          }

          // Vehicle Identity Header
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = currentReport.vehicleTitle,
              color = OffWhite,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 15.5.sp
            )
            Text(
              text = "Châssis VIN : ${currentReport.vin} • ${currentReport.engineAndGearbox}",
              color = AluminumGray,
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp
            )
            Text(
              text = "Code Peinture : ${currentReport.paintColorCode} • Mise en circulation : ${currentReport.firstRegistrationYear} • Origine : ${currentReport.originCountry}",
              color = AluminumGray,
              fontSize = 11.sp
            )
          }

          // Verdict Banner
          Surface(
            shape = RoundedCornerShape(11.dp),
            color = verdictColor.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, verdictColor.copy(alpha = 0.55f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(
                imageVector = when (overallVerdict) {
                  MecaVerifTrustVerdict.CERTIFIED_CLEAN -> Icons.Default.CheckCircle
                  MecaVerifTrustVerdict.MILEAGE_FRAUD_ALERT -> Icons.Default.WarningAmber
                  MecaVerifTrustVerdict.BODYWORK_STRUCTURAL_WARNING -> Icons.Default.CarCrash
                },
                contentDescription = null,
                tint = verdictColor,
                modifier = Modifier.size(26.dp)
              )
              Column {
                Text(
                  text = overallVerdict.title,
                  color = verdictColor,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 12.5.sp
                )
                Text(
                  text = overallVerdict.subtitle,
                  color = OffWhite,
                  fontSize = 11.sp
                )
              }
            }
          }

          // 4 Key Pillars Grid
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val hasMajorBodywork = currentReport.bodyworkRecords.any { it.isStructuralOrAirbag }
            MecaVerifKpiMiniCard(
              title = "Photos Tôle & Carrosserie",
              value = if (hasMajorBodywork) "OUI (${currentReport.bodyworkRecords.size} Dossier Avant/Après)"
              else "Aucun Choc Tôle (${currentReport.bodyworkRecords.size} photo)",
              statusColor = if (hasMajorBodywork) MekanikErrorRed else MekanikValidationGreen,
              modifier = Modifier.weight(1f)
            )
            MecaVerifKpiMiniCard(
              title = "Passages Mécanicien",
              value = "${currentReport.mechanicRecords.size} Dossiers Atelier",
              statusColor = MekanikBrandOrange,
              modifier = Modifier.weight(1f)
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            MecaVerifKpiMiniCard(
              title = "Dernier Km Enregistré",
              value = "$maxHistoricalKm km (${currentReport.mileageTimeline.size} relevés)",
              statusColor = OffWhite,
              modifier = Modifier.weight(1f)
            )
            MecaVerifKpiMiniCard(
              title = "Cohérence Compteur Actuel",
              value = if (isMileageRollbackDetected) "NON (-$rollbackDifferenceKm km !)"
              else "OUI (100% Cohérent)",
              statusColor = if (isMileageRollbackDetected) MekanikErrorRed else MekanikValidationGreen,
              modifier = Modifier.weight(1f)
            )
          }

          // Synthèse des points clés détectés
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
              Text(
                text = "POINTS CLÉS DU RÉSUMÉ PRÉ-ACHAT :",
                color = AluminumGray,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp
              )
              currentReport.summaryHighlights.forEach { bullet ->
                Row(
                  verticalAlignment = Alignment.Top,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = "•",
                    color = MekanikBrandOrange,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp
                  )
                  Text(
                    text = bullet,
                    color = OffWhite,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                  )
                }
              }
            }
          }

          HorizontalDivider(color = MetalBorder)

          // ÉTAPE 3 : PAIEMENT EXTERNE PAR NUMÉRO DE CHÂSSIS (250 OU 300 DA / VIN)
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "ÉTAPE 3 • PAIEMENT EXTERNE PAR CHÂSSIS (HORS ABONNEMENT)",
                color = OffWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isReportUnlocked) MekanikValidationGreen.copy(alpha = 0.16f)
                else MekanikBrandOrange.copy(alpha = 0.16f)
              ) {
                Text(
                  text = if (isReportUnlocked) {
                    "PAYÉ (${paidPerVinReceipts[currentReport.vin] ?: perVinPriceDzd} DA) • #$generatedCertificateNumber"
                  } else {
                    "$perVinPriceDzd DA / CE CHÂSSIS VIN"
                  },
                  color = if (isReportUnlocked) MekanikValidationGreen else MekanikBrandOrange,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 9.5.sp,
                  modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
              }
            }

            Button(
              onClick = { showPaymentUnlockModal = true },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = MekanikBrandOrange,
                contentColor = Color.White
              ),
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_pay_generate_full_mecaverif_report")
            ) {
              Icon(
                imageVector = if (isReportUnlocked) Icons.Default.Verified else Icons.Default.Payment,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (isReportUnlocked) {
                  "Rapport Débloqué (${currentReport.vin}) • Revoir le Reçu ($perVinPriceDzd DA)"
                } else {
                  "Payer $perVinPriceDzd DA pour ce VIN & Voir Graphique + Photos Tôle"
                },
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.5.sp
              )
            }
          }
        }
      }
    }

    // 4. CONTENU DU RAPPORT COMPLET D'HISTORIQUE (Aperçu Verrouillé avant paiement OU Rapport Complet Débloqué + Espace Tôlier)
    if (!isReportUnlocked && !isBodyshopAccount) {
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MetalCard,
          border = BorderStroke(1.dp, MetalBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MekanikBrandOrange,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = "DÉBLOQUEZ CE CHÂSSIS POUR $perVinPriceDzd DA (NON INCLUS DANS L'ABONNEMENT)",
                color = OffWhite,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp
              )
            }

            listOf(
              Triple(
                "Graphique Kilométrique Interactif (Style CarVertical • ${currentReport.mileageTimeline.size} relevés)",
                "Courbe complète d'évolution kilométrique, détection visuelle de recul compteur (-$rollbackDifferenceKm km) et sources ateliers.",
                Icons.Default.Timeline
              ),
              Triple(
                "Photos des Dommages sur la Tôle (Photos AVANT & APRÈS Réparation Obligatoires)",
                "Visualisez les photos prises par l'atelier de carrosserie avant réparation (choc/tôle froissée) et après peinture étuve.",
                Icons.Default.PhotoCamera
              ),
              Triple(
                "Historique Détaillé des Passages chez le Mécanicien (${currentReport.mechanicRecords.size} interventions)",
                "Opérations mécaniques effectuées, codes défauts OBD-II lus, références de pièces montées et notes du mécanicien.",
                Icons.Default.Build
              ),
              Triple(
                "Catalogue Spécial Tôlerie / Plastique par VIN & Certificat Officiel",
                "Références OEM des pièces de tôlerie, boucliers plastique, optiques et certificat d'audit avant-achat.",
                Icons.Default.CarRepair
              )
            ).forEach { (title, desc, icon) ->
              Surface(
                shape = RoundedCornerShape(11.dp),
                color = GraphiteDark,
                border = BorderStroke(1.dp, MetalBorder),
                onClick = { showPaymentUnlockModal = true },
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MekanikBrandOrange.copy(alpha = 0.14f),
                    modifier = Modifier.size(40.dp)
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MekanikBrandOrange,
                        modifier = Modifier.size(20.dp)
                      )
                    }
                  }
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = title,
                      color = OffWhite,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp
                    )
                    Text(
                      text = desc,
                      color = AluminumGray,
                      fontSize = 10.5.sp
                    )
                  }
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MekanikBrandOrange
                  ) {
                    Text(
                      text = "$perVinPriceDzd DA",
                      color = Color.White,
                      fontWeight = FontWeight.ExtraBold,
                      fontSize = 10.sp,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }
    } else {
      // Navigation Tabs for the Unlocked Full Report & Bodyshop Workspace
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(
            Triple(0, "Graphique Kilométrage", Icons.Default.Timeline),
            Triple(1, "Photos Tôle Avant/Après (${currentReport.bodyworkRecords.size})", Icons.Default.PhotoCamera),
            Triple(2, "Passages Mécanicien (${currentReport.mechanicRecords.size})", Icons.Default.Build),
            Triple(3, "Espace Tôlier & Pièces Carrosserie", Icons.Default.CarRepair),
            Triple(4, "Certificat Officiel", Icons.Default.Verified)
          ).forEach { (idx, label, icon) ->
            val isSelected = selectedReportSectionTab == idx
            Surface(
              shape = RoundedCornerShape(11.dp),
              color = if (isSelected) MekanikBrandOrange else MetalCard,
              border = BorderStroke(
                1.dp,
                if (isSelected) MekanikBrandOrange else MetalBorder
              ),
              onClick = { selectedReportSectionTab = idx },
              modifier = Modifier.testTag("mecaverif_tab_$idx")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = icon,
                  contentDescription = null,
                  tint = if (isSelected) Color.White else AluminumGray,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = label,
                  color = if (isSelected) Color.White else OffWhite,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 11.5.sp
                )
              }
            }
          }
        }
      }

      // TAB 0: GRAPHIQUE KILOMÉTRIQUE INTERACTIF STYLE CARVERTICAL
      if (selectedReportSectionTab == 0) {
        item {
          CarVerticalStyleMileageGraphSection(
            report = currentReport,
            enteredCurrentKm = enteredCurrentKm,
            maxHistoricalKm = maxHistoricalKm,
            isMileageRollbackDetected = isMileageRollbackDetected,
            rollbackDifferenceKm = rollbackDifferenceKm
          )
        }
      }

      // TAB 1: HISTORIQUE CARROSSERIE & PHOTOS OBLIGATOIRES AVANT / APRÈS RÉPARATION DE LA TÔLE
      if (selectedReportSectionTab == 1) {
        item {
          BodyworkAndMandatoryPhotosSection(
            report = currentReport,
            onInspectBeforeAfterPhotos = { record ->
              activePhotoInspectionRecord = record
            },
            onOpenBodyshopSubmitTab = {
              selectedReportSectionTab = 3
            }
          )
        }
      }

      // TAB 2: HISTORIQUE DES PASSAGES CHEZ LE MÉCANICIEN
      if (selectedReportSectionTab == 2) {
        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = MetalCard,
            border = BorderStroke(1.dp, MetalBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Text(
                text = "RAPPORT COMPLET • PASSAGES CHEZ LE MÉCANICIEN (DMS MÉCANICAUTO+)",
                color = OffWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )

              currentReport.mechanicRecords.forEach { mecaRec ->
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = GraphiteDark,
                  border = BorderStroke(1.dp, MetalBorder),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = mecaRec.mechanicShopName,
                          color = OffWhite,
                          fontWeight = FontWeight.Bold,
                          fontSize = 12.5.sp
                        )
                        Text(
                          text = "${mecaRec.date} • Kilométrage relevé : ${mecaRec.mileageAtVisitKm} km",
                          color = MekanikBrandOrange,
                          fontWeight = FontWeight.SemiBold,
                          fontSize = 11.sp
                        )
                      }
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MekanikValidationGreen.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, MekanikValidationGreen)
                      ) {
                        Text(
                          text = "${mecaRec.invoiceAmountDzd} DA",
                          color = MekanikValidationGreen,
                          fontWeight = FontWeight.Bold,
                          fontSize = 11.sp,
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                      }
                    }

                    Text(
                      text = "Nature : ${mecaRec.visitCategory}",
                      color = OffWhite,
                      fontWeight = FontWeight.SemiBold,
                      fontSize = 11.5.sp
                    )

                    mecaRec.operationsPerformed.forEach { op ->
                      Text(
                        text = "✓ $op",
                        color = OffWhite,
                        fontSize = 11.sp
                      )
                    }

                    Text(
                      text = "Références pièces montées : ${mecaRec.installedPartsReferences.joinToString(" • ")}",
                      color = AluminumGray,
                      fontFamily = FontFamily.Monospace,
                      fontSize = 10.5.sp
                    )

                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = MetalCard,
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Text(
                        text = "Note du mécanicien : « ${mecaRec.mechanicObservation} »",
                        color = OffWhite,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(10.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      // TAB 3: ESPACE SPÉCIAL CARROSSERIE & TÔLERIE (PIÈCES TÔLERIE/PLASTIQUE PAR VIN + PHOTOS OBLIGATOIRES AVANT/APRÈS)
      if (selectedReportSectionTab == 3) {
        item {
          BodyshopTolerieWorkspaceSection(
            report = currentReport,
            selectedCategory = bodyshopSelectedPartCategory,
            onSelectCategory = { bodyshopSelectedPartCategory = it },
            impactZoneInput = bodyshopImpactZoneInput,
            onImpactZoneChange = { bodyshopImpactZoneInput = it },
            repairNotesInput = bodyshopRepairNotesInput,
            onRepairNotesChange = { bodyshopRepairNotesInput = it },
            hasCapturedBeforePhoto = hasCapturedMandatoryBeforePhoto,
            onCaptureBeforePhoto = { hasCapturedMandatoryBeforePhoto = true },
            hasCapturedAfterPhoto = hasCapturedMandatoryAfterPhoto,
            onCaptureAfterPhoto = { hasCapturedMandatoryAfterPhoto = true },
            submissionSuccessBanner = bodyshopSubmissionSuccessBanner,
            onSubmitCertifiedBodyworkRepair = {
              val newRecord = BodyworkShopRecord(
                id = "BODY-NEW-${currentReport.bodyworkRecords.size + 1}",
                date = "15 Novembre 2026 (Certifié Aujourd'hui)",
                mileageAtEventKm = enteredCurrentKm,
                bodyShopName = "Atelier Carrosserie & Tôlerie Pro (Compte Carrosserie DMS)",
                impactZone = bodyshopImpactZoneInput,
                severityLevel = "Réparation Tôlerie & Peinture avec Dossier Photos Avant/Après Obligatoires",
                isStructuralOrAirbag = false,
                worksPerformed = listOf(
                  "Photo AVANT réparation prise et horodatée sur le châssis ${currentReport.vin}",
                  bodyshopRepairNotesInput,
                  "Photo APRÈS réparation prise en sortie d'étuve (contrôle micromètre validé)"
                ),
                replacedBodyPartsOem = currentReport.bodyshopChassisParts.take(2).map { "${it.oemReference} (${it.partName})" },
                repairCostDzd = 68000,
                beforePhotoTitle = "Photo AVANT Obligatoire • $bodyshopImpactZoneInput (Tôle endommagée)",
                beforePhotoNotes = "Constat photo initial obligatoire enregistré par le tôlier avant travaux sur ${currentReport.vin}.",
                beforePaintMicrons = 110,
                afterPhotoTitle = "Photo APRÈS Obligatoire • $bodyshopImpactZoneInput (Tôlerie & Peinture terminées)",
                afterPhotoNotes = "Constat photo final obligatoire validé après redressage/remplacement et peinture code ${currentReport.paintColorCode}.",
                afterPaintMicrons = 175,
                mandatoryPhotosCertified = true
              )
              currentReport = currentReport.copy(
                bodyworkRecords = listOf(newRecord) + currentReport.bodyworkRecords
              )
              bodyshopSubmissionSuccessBanner =
                "Dossier Carrosserie avec Photos AVANT et APRÈS obligatoires enregistré sur le châssis ${currentReport.vin} !"
            }
          )
        }
      }

      // TAB 4: CERTIFICAT OFFICIEL GÉNÉRÉ
      if (selectedReportSectionTab == 4) {
        item {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = MetalCard,
            border = BorderStroke(1.2.dp, MekanikValidationGreen),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "CERTIFICAT OFFICIEL MÉCAVERIF+™ #$generatedCertificateNumber",
                  color = MekanikValidationGreen,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 13.sp
                )
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = MekanikValidationGreen.copy(alpha = 0.16f)
                ) {
                  Text(
                    text = "PAYÉ (${paidPerVinReceipts[currentReport.vin] ?: perVinPriceDzd} DA)",
                    color = MekanikValidationGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                  )
                }
              }
              Text(
                text = "Véhicule : ${currentReport.vehicleTitle}\nChâssis VIN : ${currentReport.vin}\nCode Peinture : ${currentReport.paintColorCode}\nKilométrage vérifié : $enteredCurrentKm km (Dernier relevé base : $maxHistoricalKm km)\nDossiers Photos Carrosserie Avant/Après : ${currentReport.bodyworkRecords.size}\nVerdict : ${overallVerdict.title}",
                color = OffWhite,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.5.sp,
                lineHeight = 17.sp
              )
            }
          }
        }
      }
    }
  }

  // Modal de Zoom Comparatif Photos AVANT / APRÈS Réparation Carrosserie
  activePhotoInspectionRecord?.let { rec ->
    BeforeAfterDamagePhotoDialog(
      record = rec,
      vin = currentReport.vin,
      paintCode = currentReport.paintColorCode,
      onDismiss = { activePhotoInspectionRecord = null }
    )
  }

  // Passerelle de Paiement Sécurisé Externe par Châssis (250 DA ou 300 DA / VIN • CIB SATIM, Edahabia, BaridiMob, Chargily Pay & Stripe)
  if (showPaymentUnlockModal) {
    MecaVerifSecureCheckoutModal(
      vin = currentReport.vin,
      vehicleTitle = currentReport.vehicleTitle,
      isProAccount = isProAccount,
      initialChannel = initialGatewayChannel,
      onPaymentCompleted = { receipt ->
        generatedCertificateNumber = receipt.receiptId
        triggerReportGenerationAfterPayment(receipt.amountDzd)
      },
      onDismiss = { showPaymentUnlockModal = false }
    )
  }

  // Journal des Reçus Fiscaux & Historique des Achats Unitaires MécaVerif+™
  if (showReceiptsLedgerModal) {
    MecaVerifReceiptsLedgerModal(
      onSelectUnlockedVin = { targetVin ->
        showReceiptsLedgerModal = false
        vinQuery = targetVin
        val rep = MecaVerifRepository.findOrBuildReport(targetVin)
        currentReport = rep
        currentDashboardKmText = rep.defaultDashboardOdometerKm.toString()
        unlockedVins = unlockedVins + targetVin
      },
      onDismiss = { showReceiptsLedgerModal = false }
    )
  }
}

@Composable
private fun CarVerticalStyleMileageGraphSection(
  report: MecaVerifVehicleReport,
  enteredCurrentKm: Int,
  maxHistoricalKm: Int,
  isMileageRollbackDetected: Boolean,
  rollbackDifferenceKm: Int
) {
  val points = report.mileageTimeline
  var selectedPointIndex by remember(report.vin) { mutableIntStateOf(points.lastIndex.coerceAtLeast(0)) }

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = MetalCard,
    border = BorderStroke(1.dp, MetalBorder),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("carvertical_mileage_chart_card")
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "GRAPHIQUE D'ÉVOLUTION KILOMÉTRIQUE (STYLE CARVERTICAL)",
            color = OffWhite,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp
          )
          Text(
            text = "Croisement des relevés ateliers DMS Mécanicauto+, concessions & contrôle technique",
            color = AluminumGray,
            fontSize = 10.5.sp
          )
        }
        Surface(
          shape = RoundedCornerShape(7.dp),
          color = if (isMileageRollbackDetected) MekanikErrorRed.copy(alpha = 0.18f)
          else MekanikValidationGreen.copy(alpha = 0.18f),
          border = BorderStroke(
            1.dp,
            if (isMileageRollbackDetected) MekanikErrorRed else MekanikValidationGreen
          )
        ) {
          Text(
            text = if (isMileageRollbackDetected) "FRAUDE : -$rollbackDifferenceKm KM"
            else "COURBE AUTHENTIQUE",
            color = if (isMileageRollbackDetected) MekanikErrorRed else MekanikValidationGreen,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 9.5.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      val matchColor = if (isMileageRollbackDetected) MekanikErrorRed else MekanikValidationGreen
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = matchColor.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, matchColor),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(
            imageVector = if (isMileageRollbackDetected) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
            contentDescription = null,
            tint = matchColor,
            modifier = Modifier.size(24.dp)
          )
          Text(
            text = if (isMileageRollbackDetected) {
              "ALERTE MANIPULATION ODOMÈTRE : Ce véhicule a été enregistré à $maxHistoricalKm km avant de chuter brutalement à $enteredCurrentKm km (-$rollbackDifferenceKm km supprimés) !"
            } else {
              "KILOMÉTRAGE CERTIFIÉ : Progression régulière de ${points.firstOrNull()?.recordedKm ?: 0} km (${points.firstOrNull()?.year ?: 2021}) jusqu'à $enteredCurrentKm km."
            },
            color = matchColor,
            fontWeight = FontWeight.Bold,
            fontSize = 11.5.sp,
            lineHeight = 15.5.sp
          )
        }
      }

      // Graphique Visuel style CarVertical avec axe Y (km), zone ombrée et détection de chute rouge
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = GraphiteDark,
        border = BorderStroke(1.dp, MetalBorder),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 2.dp)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          val maxKm = (points.maxOfOrNull { it.recordedKm } ?: 100000)
            .coerceAtLeast(enteredCurrentKm)
            .coerceAtLeast(50000)

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(190.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Axe Y des kilomètres
            Column(
              modifier = Modifier
                .fillMaxHeight()
                .padding(end = 8.dp, bottom = 18.dp),
              verticalArrangement = Arrangement.SpaceBetween,
              horizontalAlignment = Alignment.End
            ) {
              Text("${maxKm / 1000}k km", color = AluminumGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
              Text("${(maxKm * 2 / 3) / 1000}k km", color = AluminumGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
              Text("${(maxKm / 3) / 1000}k km", color = AluminumGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
              Text("0 km", color = AluminumGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }

            // Zone Canvas de la courbe
            Box(
              modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
            ) {
              Canvas(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(horizontal = 12.dp, vertical = 12.dp)
              ) {
                val w = size.width
                val h = size.height - 16.dp.toPx()
                val maxKmFloat = maxKm.toFloat()

                // Grille horizontale
                for (i in 0..3) {
                  val y = h * (i / 3f)
                  drawLine(
                    color = Color(0xFF253545),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1.2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                  )
                }

                if (points.size >= 2) {
                  val coords = points.mapIndexed { index, pt ->
                    val x = (index.toFloat() / (points.size - 1).toFloat()) * w
                    val kmVal = if (index == points.lastIndex) enteredCurrentKm else pt.recordedKm
                    val y = h - ((kmVal.toFloat() / maxKmFloat) * h).coerceIn(0f, h)
                    Offset(x, y)
                  }

                  // Aire sous la courbe
                  val fillPath = Path().apply {
                    moveTo(coords.first().x, h)
                    coords.forEach { pt -> lineTo(pt.x, pt.y) }
                    lineTo(coords.last().x, h)
                    close()
                  }
                  drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                      colors = if (isMileageRollbackDetected) {
                        listOf(Color(0xFFEF4444).copy(alpha = 0.28f), Color.Transparent)
                      } else {
                        listOf(Color(0xFF22C55E).copy(alpha = 0.26f), Color.Transparent)
                      },
                      startY = 0f,
                      endY = h
                    )
                  )

                  // Segments de ligne (verts si croissants, rouge pointillé si recul compteur)
                  for (i in 0 until coords.size - 1) {
                    val startPt = coords[i]
                    val endPt = coords[i + 1]
                    val isDropSegment = endPt.y > startPt.y + 2f
                    drawLine(
                      color = if (isDropSegment) Color(0xFFEF4444) else Color(0xFF22C55E),
                      start = startPt,
                      end = endPt,
                      strokeWidth = if (isDropSegment) 6f else 4.5f,
                      pathEffect = if (isDropSegment) {
                        PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                      } else null
                    )
                  }

                  // Points interactifs
                  coords.forEachIndexed { index, offset ->
                    val pt = points[index]
                    val isAnomaly = pt.isAnomalyRollback || (index == points.lastIndex && isMileageRollbackDetected)
                    val ptColor = if (isAnomaly) Color(0xFFEF4444) else Color(0xFF22C55E)
                    drawCircle(
                      color = Color(0xFF101820),
                      radius = 9f,
                      center = offset
                    )
                    drawCircle(
                      color = if (index == selectedPointIndex) Color(0xFFF07836) else ptColor,
                      radius = 6.5f,
                      center = offset
                    )
                  }
                }
              }
            }
          }

          // Axe X des années cliquables
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(start = 42.dp, top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            points.forEachIndexed { index, pt ->
              val isSelected = selectedPointIndex == index
              val isAnomaly = pt.isAnomalyRollback || (index == points.lastIndex && isMileageRollbackDetected)
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isSelected) MekanikBrandOrange else Color.Transparent,
                onClick = { selectedPointIndex = index }
              ) {
                Text(
                  text = pt.year.toString(),
                  color = when {
                    isSelected -> Color.White
                    isAnomaly -> MekanikErrorRed
                    else -> OffWhite
                  },
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }

      // Liste détaillée des relevés kilométriques
      points.forEachIndexed { index, point ->
        val displayedKm = if (index == points.lastIndex && isMileageRollbackDetected) enteredCurrentKm else point.recordedKm
        val isAnomaly = point.isAnomalyRollback || (index == points.lastIndex && isMileageRollbackDetected)
        val pointColor = if (isAnomaly) MekanikErrorRed else MekanikValidationGreen
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = if (selectedPointIndex == index) pointColor.copy(alpha = 0.12f) else GraphiteDark,
          border = BorderStroke(
            1.dp,
            if (isAnomaly) MekanikErrorRed else if (selectedPointIndex == index) MekanikBrandOrange else MetalBorder
          ),
          onClick = { selectedPointIndex = index },
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = point.dateLabel,
                  color = OffWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
                Text(
                  text = "• ${point.sourceWorkshop}",
                  color = AluminumGray,
                  fontSize = 11.sp
                )
              }
              Text(
                text = point.eventType,
                color = if (isAnomaly) MekanikErrorRed else OffWhite,
                fontWeight = if (isAnomaly) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = pointColor.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, pointColor)
            ) {
              Text(
                text = "$displayedKm km",
                color = pointColor,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun BodyworkAndMandatoryPhotosSection(
  report: MecaVerifVehicleReport,
  onInspectBeforeAfterPhotos: (BodyworkShopRecord) -> Unit,
  onOpenBodyshopSubmitTab: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = MetalCard,
    border = BorderStroke(1.dp, MetalBorder),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("bodywork_photos_section_card")
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "DOMMAGES SUR LA TÔLE & PHOTOS AVANT / APRÈS OBLIGATOIRES",
            color = OffWhite,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp
          )
          Text(
            text = "Chaque atelier de carrosserie DMS Mécanicauto+ est obligé de photographier la tôle avant et après réparation",
            color = AluminumGray,
            fontSize = 10.5.sp
          )
        }
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MekanikBrandOrange.copy(alpha = 0.18f),
          border = BorderStroke(1.dp, MekanikBrandOrange),
          onClick = onOpenBodyshopSubmitTab
        ) {
          Text(
            text = "+ Ajouter Photos Tôlier",
            color = MekanikBrandOrange,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
          )
        }
      }

      // Cartographie des 5 zones de carrosserie
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = GraphiteDark,
        border = BorderStroke(1.dp, MetalBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "CARTOGRAPHIE DES ZONES DE TÔLERIE & CHÂSSIS :",
            color = AluminumGray,
            fontWeight = FontWeight.Bold,
            fontSize = 10.5.sp
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(
              "FRONT" to "Bloc Avant",
              "LEFT" to "Latéral G.",
              "RIGHT" to "Latéral D.",
              "REAR" to "Bloc Arrière",
              "CHASSIS" to "Marbre"
            ).forEach { (zoneCode, zoneLabel) ->
              val isImpacted = report.impactedZones.contains(zoneCode)
              val zColor = if (isImpacted) MekanikErrorRed else MekanikValidationGreen
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = zColor.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, zColor),
                modifier = Modifier.weight(1f)
              ) {
                Column(
                  modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = zoneLabel,
                    color = OffWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = if (isImpacted) "TÔLE TOUCHÉE" else "INTACT",
                    color = zColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 8.5.sp
                  )
                }
              }
            }
          }
        }
      }

      // Dossiers Carrosserie avec Galerie Photos AVANT / APRÈS
      report.bodyworkRecords.forEach { bodyRec ->
        val recColor = if (bodyRec.isStructuralOrAirbag) MekanikErrorRed else MekanikBrandOrange
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = GraphiteDark,
          border = BorderStroke(1.2.dp, recColor.copy(alpha = 0.7f)),
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
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = bodyRec.bodyShopName,
                  color = OffWhite,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 12.5.sp
                )
                Text(
                  text = "${bodyRec.date} • Relevé : ${bodyRec.mileageAtEventKm} km",
                  color = AluminumGray,
                  fontSize = 11.sp
                )
              }
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MekanikValidationGreen.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, MekanikValidationGreen)
              ) {
                Text(
                  text = "PHOTOS AVANT/APRÈS CERTIFIÉES",
                  color = MekanikValidationGreen,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 8.5.sp,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
              }
            }

            Text(
              text = "Zone de tôle / carrosserie : ${bodyRec.impactZone} (${bodyRec.severityLevel})",
              color = recColor,
              fontWeight = FontWeight.Bold,
              fontSize = 11.5.sp
            )

            // DOUBLE VIGNETTE VISUELLE : PHOTO AVANT (DOMMAGES TÔLE) & PHOTO APRÈS (TÔLERIE RÉPARÉE)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Photo AVANT Réparation
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MetalCard,
                border = BorderStroke(1.dp, MekanikErrorRed.copy(alpha = 0.8f)),
                onClick = { onInspectBeforeAfterPhotos(bodyRec) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("card_photo_before_${bodyRec.id}")
              ) {
                Column(
                  modifier = Modifier.padding(8.dp),
                  verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(78.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color(0xFF1A1418))
                  ) {
                    SheetMetalPhotoIllustrationCanvas(isBeforeDamage = true)
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = MekanikErrorRed,
                      modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(5.dp)
                    ) {
                      Text(
                        text = "PHOTO AVANT (TÔLE ENDOMMAGÉE)",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 7.5.sp,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                      )
                    }
                  }
                  Text(
                    text = bodyRec.beforePhotoTitle,
                    color = OffWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = "Constat initial : ${bodyRec.beforePaintMicrons} µm",
                    color = AluminumGray,
                    fontSize = 9.sp
                  )
                }
              }

              // Photo APRÈS Réparation
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MetalCard,
                border = BorderStroke(1.dp, MekanikValidationGreen.copy(alpha = 0.8f)),
                onClick = { onInspectBeforeAfterPhotos(bodyRec) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("card_photo_after_${bodyRec.id}")
              ) {
                Column(
                  modifier = Modifier.padding(8.dp),
                  verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(78.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color(0xFF10201A))
                  ) {
                    SheetMetalPhotoIllustrationCanvas(isBeforeDamage = false)
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = MekanikValidationGreen,
                      modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(5.dp)
                    ) {
                      Text(
                        text = "PHOTO APRÈS (TÔLE & PEINTURE OK)",
                        color = Color(0xFF101820),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 7.5.sp,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                      )
                    }
                  }
                  Text(
                    text = bodyRec.afterPhotoTitle,
                    color = OffWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = "Après étuve : ${bodyRec.afterPaintMicrons} µm",
                    color = MekanikValidationGreen,
                    fontSize = 9.sp
                  )
                }
              }
            }

            OutlinedButton(
              onClick = { onInspectBeforeAfterPhotos(bodyRec) },
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, MekanikBrandOrange),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = MekanikBrandOrange),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_zoom_before_after_${bodyRec.id}")
            ) {
              Icon(Icons.Default.ZoomIn, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Agrandir les Photos AVANT / APRÈS de la Tôle & Mesures Micromètre",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }

            bodyRec.worksPerformed.forEach { work ->
              Text(
                text = "• $work",
                color = OffWhite,
                fontSize = 11.sp
              )
            }

            if (bodyRec.replacedBodyPartsOem.isNotEmpty()) {
              Text(
                text = "Pièces de tôlerie / plastique remplacées : ${bodyRec.replacedBodyPartsOem.joinToString(", ")}",
                color = AluminumGray,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.5.sp
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Espace Spécial Tôlier & Carrosserie (`AccountRole.ATELIER_CARROSSERIE`) :
 * 1. Accès par numéro de châssis (VIN) aux pièces de Tôlerie, Plastique, Boucliers, Optiques & Agrafes OEM.
 * 2. Obligation de prise de photos AVANT et APRÈS réparation carrosserie pour valider un dossier.
 */
@Composable
private fun BodyshopTolerieWorkspaceSection(
  report: MecaVerifVehicleReport,
  selectedCategory: String,
  onSelectCategory: (String) -> Unit,
  impactZoneInput: String,
  onImpactZoneChange: (String) -> Unit,
  repairNotesInput: String,
  onRepairNotesChange: (String) -> Unit,
  hasCapturedBeforePhoto: Boolean,
  onCaptureBeforePhoto: () -> Unit,
  hasCapturedAfterPhoto: Boolean,
  onCaptureAfterPhoto: () -> Unit,
  submissionSuccessBanner: String?,
  onSubmitCertifiedBodyworkRepair: () -> Unit
) {
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    // 1. Catalogue Pièces de Tôlerie, Plastique, Optiques & Agrafes par Numéro de Châssis (VIN)
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = MetalCard,
      border = BorderStroke(1.2.dp, Color(0xFF38BDF8)),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("bodyshop_tolerie_catalog_card")
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "COMPTE SPÉCIAL CARROSSERIE • PIÈCES TÔLERIE & PLASTIQUE PAR VIN",
              color = Color(0xFF38BDF8),
              fontWeight = FontWeight.ExtraBold,
              fontSize = 12.5.sp
            )
            Text(
              text = "Châssis actif : ${report.vin} • Code Peinture : ${report.paintColorCode}",
              color = OffWhite,
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp
            )
          }
          Surface(
            shape = RoundedCornerShape(7.dp),
            color = Color(0xFF38BDF8).copy(alpha = 0.18f),
            border = BorderStroke(1.dp, Color(0xFF38BDF8))
          ) {
            Text(
              text = "ESPACE TÔLIER",
              color = Color(0xFF38BDF8),
              fontWeight = FontWeight.ExtraBold,
              fontSize = 9.5.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        // Filtres par famille de carrosserie
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(
            "TOUT" to "Toutes Pièces Carrosserie",
            "TÔLERIE ACIER / ALU" to "Tôlerie (Capot, Ailes, Portes)",
            "PLASTIQUE & BOUCLIERS" to "Plastique (Pare-chocs, Calandre)",
            "OPTIQUES & VITRAGE" to "Optiques & Pare-brise",
            "CLIPS & AGRAFES" to "Clips & Agrafes OEM"
          ).forEach { (catCode, catLabel) ->
            val isSelected = selectedCategory == catCode
            Surface(
              shape = RoundedCornerShape(9.dp),
              color = if (isSelected) Color(0xFF38BDF8) else GraphiteDark,
              border = BorderStroke(
                1.dp,
                if (isSelected) Color(0xFF38BDF8) else MetalBorder
              ),
              onClick = { onSelectCategory(catCode) }
            ) {
              Text(
                text = catLabel,
                color = if (isSelected) Color(0xFF101820) else OffWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }

        val filteredParts = report.bodyshopChassisParts.filter {
          selectedCategory == "TOUT" || it.category == selectedCategory
        }

        filteredParts.forEach { part ->
          Surface(
            shape = RoundedCornerShape(11.dp),
            color = GraphiteDark,
            border = BorderStroke(1.dp, MetalBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(5.dp),
                  color = MekanikBrandOrange.copy(alpha = 0.18f)
                ) {
                  Text(
                    text = "${part.category} • ${part.sideOrZone}",
                    color = MekanikBrandOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Text(
                  text = "${part.priceDzd} DA",
                  color = MekanikValidationGreen,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 13.sp
                )
              }

              Text(
                text = part.partName,
                color = OffWhite,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.5.sp
              )
              Text(
                text = "Réf OEM Châssis : ${part.oemReference} • Matière : ${part.materialSpec}",
                color = Color(0xFF38BDF8),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp
              )
              Text(
                text = "Méthode Tôlier : ${part.fittingNotes}",
                color = AluminumGray,
                fontSize = 10.5.sp
              )
            }
          }
        }
      }
    }

    // 2. PROTOCOLE OBLIGATOIRE ATELIER DE CARROSSERIE : PRISE DE PHOTOS AVANT & APRÈS RÉPARATION
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = MetalCard,
      border = BorderStroke(1.5.dp, MekanikBrandOrange),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("bodyshop_mandatory_before_after_card")
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.CameraEnhance,
            contentDescription = null,
            tint = MekanikBrandOrange,
            modifier = Modifier.size(22.dp)
          )
          Column {
            Text(
              text = "OBLIGATION ATELIER CARROSSERIE : PHOTOS AVANT & APRÈS RÉPARATION",
              color = MekanikBrandOrange,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 12.5.sp
            )
            Text(
              text = "La validation d'un dossier carrosserie est bloquée tant que les 2 photos obligatoires (Avant et Après) ne sont pas prises.",
              color = OffWhite,
              fontSize = 10.5.sp
            )
          }
        }

        OutlinedTextField(
          value = impactZoneInput,
          onValueChange = onImpactZoneChange,
          singleLine = true,
          label = { Text("Zone de tôle / carrosserie concernée", fontSize = 11.sp) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MekanikBrandOrange,
            unfocusedBorderColor = MetalBorder,
            focusedTextColor = OffWhite,
            unfocusedTextColor = OffWhite,
            focusedContainerColor = GraphiteDark,
            unfocusedContainerColor = GraphiteDark
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = repairNotesInput,
          onValueChange = onRepairNotesChange,
          singleLine = true,
          label = { Text("Travaux de tôlerie / plastique / peinture réalisés", fontSize = 11.sp) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MekanikBrandOrange,
            unfocusedBorderColor = MetalBorder,
            focusedTextColor = OffWhite,
            unfocusedTextColor = OffWhite,
            focusedContainerColor = GraphiteDark,
            unfocusedContainerColor = GraphiteDark
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        )

        // Les 2 boutons de capture photo obligatoires (AVANT + APRÈS)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Capture Photo AVANT
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (hasCapturedBeforePhoto) MekanikValidationGreen.copy(alpha = 0.14f) else GraphiteDark,
            border = BorderStroke(
              1.2.dp,
              if (hasCapturedBeforePhoto) MekanikValidationGreen else MekanikErrorRed
            ),
            onClick = onCaptureBeforePhoto,
            modifier = Modifier
              .weight(1f)
              .testTag("btn_capture_mandatory_before_photo")
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = if (hasCapturedBeforePhoto) Icons.Default.CheckCircle else Icons.Default.AddAPhoto,
                contentDescription = null,
                tint = if (hasCapturedBeforePhoto) MekanikValidationGreen else MekanikErrorRed,
                modifier = Modifier.size(24.dp)
              )
              Text(
                text = if (hasCapturedBeforePhoto) "1. PHOTO AVANT VALIDÉE ✓" else "1. PRENDRE PHOTO AVANT *",
                color = if (hasCapturedBeforePhoto) MekanikValidationGreen else OffWhite,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 10.5.sp
              )
              Text(
                text = "Dommages sur la tôle avant travaux (Obligatoire)",
                color = AluminumGray,
                fontSize = 9.5.sp
              )
            }
          }

          // Capture Photo APRÈS
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (hasCapturedAfterPhoto) MekanikValidationGreen.copy(alpha = 0.14f) else GraphiteDark,
            border = BorderStroke(
              1.2.dp,
              if (hasCapturedAfterPhoto) MekanikValidationGreen else MekanikErrorRed
            ),
            onClick = onCaptureAfterPhoto,
            modifier = Modifier
              .weight(1f)
              .testTag("btn_capture_mandatory_after_photo")
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = if (hasCapturedAfterPhoto) Icons.Default.CheckCircle else Icons.Default.AddAPhoto,
                contentDescription = null,
                tint = if (hasCapturedAfterPhoto) MekanikValidationGreen else MekanikErrorRed,
                modifier = Modifier.size(24.dp)
              )
              Text(
                text = if (hasCapturedAfterPhoto) "2. PHOTO APRÈS VALIDÉE ✓" else "2. PRENDRE PHOTO APRÈS *",
                color = if (hasCapturedAfterPhoto) MekanikValidationGreen else OffWhite,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 10.5.sp
              )
              Text(
                text = "Tôlerie redressée & peinture finie (Obligatoire)",
                color = AluminumGray,
                fontSize = 9.5.sp
              )
            }
          }
        }

        val bothMandatoryPhotosReady = hasCapturedBeforePhoto && hasCapturedAfterPhoto
        Button(
          onClick = onSubmitCertifiedBodyworkRepair,
          enabled = bothMandatoryPhotosReady,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MekanikBrandOrange,
            contentColor = Color.White,
            disabledContainerColor = GraphiteDark,
            disabledContentColor = AluminumGray
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("btn_submit_mandatory_bodyshop_photos")
        ) {
          Icon(
            imageVector = if (bothMandatoryPhotosReady) Icons.Default.Verified else Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (bothMandatoryPhotosReady) {
              "Certifier & Publier les Photos AVANT/APRÈS sur le Châssis ${report.vin}"
            } else {
              "Verrouillé : Prenez les 2 Photos Obligatoires (Avant + Après) pour valider"
            },
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.5.sp
          )
        }

        submissionSuccessBanner?.let { msg ->
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MekanikValidationGreen.copy(alpha = 0.16f),
            border = BorderStroke(1.dp, MekanikValidationGreen),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = msg,
              color = MekanikValidationGreen,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              modifier = Modifier.padding(10.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SheetMetalPhotoIllustrationCanvas(isBeforeDamage: Boolean) {
  Canvas(modifier = Modifier.fillMaxSize()) {
    val w = size.width
    val h = size.height

    // Silhouette d'aile / portière automobile
    drawRoundRect(
      color = if (isBeforeDamage) Color(0xFF2D2226) else Color(0xFF1B332B),
      topLeft = Offset(w * 0.1f, h * 0.18f),
      size = Size(w * 0.8f, h * 0.64f),
      cornerRadius = CornerRadius(14f, 14f)
    )

    if (isBeforeDamage) {
      // Lignes d'impact / tôle froissée en rouge/orange
      val crackPath = Path().apply {
        moveTo(w * 0.28f, h * 0.30f)
        lineTo(w * 0.42f, h * 0.52f)
        lineTo(w * 0.34f, h * 0.66f)
        moveTo(w * 0.52f, h * 0.28f)
        lineTo(w * 0.42f, h * 0.52f)
        lineTo(w * 0.64f, h * 0.62f)
      }
      drawPath(
        path = crackPath,
        color = Color(0xFFEF4444),
        style = Stroke(width = 3.5f)
      )
      drawCircle(
        color = Color(0xFFF07836).copy(alpha = 0.45f),
        radius = 18f,
        center = Offset(w * 0.42f, h * 0.50f)
      )
    } else {
      // Lignes de reflets brillants d'une tôle redressée et peinte en cabine
      drawLine(
        color = Color(0xFF22C55E),
        start = Offset(w * 0.22f, h * 0.65f),
        end = Offset(w * 0.48f, h * 0.28f),
        strokeWidth = 3.5f
      )
      drawLine(
        color = Color(0xFF38BDF8).copy(alpha = 0.7f),
        start = Offset(w * 0.42f, h * 0.68f),
        end = Offset(w * 0.68f, h * 0.30f),
        strokeWidth = 2.5f
      )
    }
  }
}

@Composable
private fun BeforeAfterDamagePhotoDialog(
  record: BodyworkShopRecord,
  vin: String,
  paintCode: String,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(18.dp),
      color = MetalCard,
      border = BorderStroke(1.5.dp, MekanikBrandOrange),
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
        .testTag("before_after_photo_zoom_modal")
    ) {
      Column(
        modifier = Modifier
          .verticalScroll(rememberScrollState())
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "INSPECTION PHOTOS AVANT / APRÈS RÉPARATION TÔLERIE",
              color = OffWhite,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 14.sp
            )
            Text(
              text = "Châssis : $vin • ${record.bodyShopName}",
              color = MekanikBrandOrange,
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = OffWhite)
          }
        }

        // Photo 1 : AVANT
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = GraphiteDark,
          border = BorderStroke(1.2.dp, MekanikErrorRed),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "1. ${record.beforePhotoTitle}",
              color = MekanikErrorRed,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 12.sp
            )
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(115.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1A1418))
            ) {
              SheetMetalPhotoIllustrationCanvas(isBeforeDamage = true)
            }
            Text(
              text = record.beforePhotoNotes,
              color = OffWhite,
              fontSize = 11.sp
            )
            Text(
              text = "Mesure micromètre avant intervention : ${record.beforePaintMicrons} µm • Date : ${record.date}",
              color = AluminumGray,
              fontSize = 10.sp
            )
          }
        }

        // Photo 2 : APRÈS
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = GraphiteDark,
          border = BorderStroke(1.2.dp, MekanikValidationGreen),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "2. ${record.afterPhotoTitle}",
              color = MekanikValidationGreen,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 12.sp
            )
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(115.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF10201A))
            ) {
              SheetMetalPhotoIllustrationCanvas(isBeforeDamage = false)
            }
            Text(
              text = record.afterPhotoNotes,
              color = OffWhite,
              fontSize = 11.sp
            )
            Text(
              text = "Mesure micromètre après peinture ($paintCode) : ${record.afterPaintMicrons} µm • Certifié par l'atelier de carrosserie",
              color = MekanikValidationGreen,
              fontSize = 10.sp
            )
          }
        }

        Button(
          onClick = onDismiss,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MekanikBrandOrange),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Fermer l'inspection photo", color = Color.White, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun MecaVerifKpiMiniCard(
  title: String,
  value: String,
  statusColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = GraphiteDark,
    border = BorderStroke(1.dp, MetalBorder),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
      Text(
        text = title,
        color = AluminumGray,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium
      )
      Text(
        text = value,
        color = statusColor,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 12.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}
