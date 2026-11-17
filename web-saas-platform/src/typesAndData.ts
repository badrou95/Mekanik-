/**
 * MEKANIK+ — MODÈLES MÉTIER & BASE DE DONNÉES SAAS (INVESTOR-READY WEB PLATFORM)
 * - Séparation stricte des 7 formules de comptes
 * - Compte Mixte Atelier + Magasin (1 Principal + 4 Sous-Comptes)
 * - Compte Spécial Carrosserie & Tôlerie (Photos Avant/Après obligatoires)
 * - MécaVerif+ (Courbe CarVertical + Photos Tôle + Paiement CIB/SATIM, Edahabia, BaridiMob, Chargily, Stripe)
 * - MekanikAI OBD-II + Documentation Technique & Schémas OEM
 * - Super-Administrateur Double 2FA (Google Authenticator + SMS OTP) & Clés API No-Code (TecDoc, ETKA, Stellantis, TikTok...)
 */

export type AccountRoleId =
  | "PARTICULIER"
  | "ATELIER_PRO"
  | "MAGASIN_PIECES"
  | "MIXTE_ATELIER_MAGASIN"
  | "CARROSSERIE_TOLERIE"
  | "FOURNISSEUR_B2B"
  | "ENTREPRISE_FLOTTE";

export interface AccountRoleMeta {
  id: AccountRoleId;
  title: string;
  shortBadge: string;
  monthlyPriceDzd: number;
  setupFeeDzd: number;
  maxSubAccounts: number;
  description: string;
  allowedModules: string[];
  accentColor: string;
}

export const ACCOUNT_ROLES: Record<AccountRoleId, AccountRoleMeta> = {
  PARTICULIER: {
    id: "PARTICULIER",
    title: "Compte Particulier / Conducteur",
    shortBadge: "PARTICULIER",
    monthlyPriceDzd: 0,
    setupFeeDzd: 0,
    maxSubAccounts: 0,
    description:
      "Accès strictement personnel : suivi de vos véhicules, carnet d'entretien, recherche de pièces compatibles et achat unitaire de rapports MécaVerif+ (300 DA / châssis). Aucune option d'atelier ni de vendeur pro n'est affichée.",
    allowedModules: ["DASHBOARD_PERSO", "VIN_SEARCH", "MECAVERIF", "MY_VEHICLES", "PARTS_VIEW"],
    accentColor: "#38BDF8"
  },
  ATELIER_PRO: {
    id: "ATELIER_PRO",
    title: "Compte Atelier Mécanique Pro",
    shortBadge: "ATELIER PRO",
    monthlyPriceDzd: 9900,
    setupFeeDzd: 15000,
    maxSubAccounts: 3,
    description:
      "DMS Atelier complet : Identification VIN/Moteur, Ordres de Réparation (OR) & Devis, Gestion Clients isolée, Diagnostic MekanikAI OBD-II + Schémas OEM, Commandes Pièces B2B et MécaVerif+ à 250 DA/VIN.",
    allowedModules: ["WORKSHOP_DMS", "VIN_SEARCH", "MEKANIK_AI", "MECAVERIF", "B2B_ORDER", "SUB_ACCOUNTS"],
    accentColor: "#F07836"
  },
  MAGASIN_PIECES: {
    id: "MAGASIN_PIECES",
    title: "Compte Magasin de Pièces Détachées",
    shortBadge: "MAGASIN PIÈCES",
    monthlyPriceDzd: 9500,
    setupFeeDzd: 15000,
    maxSubAccounts: 3,
    description:
      "ERP Comptoir & Stock Pièces : Catalogue visuel OEM (TecDoc / ETKA / Stellantis), Gestion des stocks WMS, Équivalences croisées, Facturation comptoir et réapprovisionnement auprès des Fournisseurs B2B.",
    allowedModules: ["STORE_CATALOG", "VIN_SEARCH", "STOCK_WMS", "MECAVERIF", "B2B_ORDER", "SUB_ACCOUNTS"],
    accentColor: "#22C55E"
  },
  MIXTE_ATELIER_MAGASIN: {
    id: "MIXTE_ATELIER_MAGASIN",
    title: "Compte Mixte : Atelier + Magasin de Pièces (1+4 Sous-Comptes)",
    shortBadge: "MIXTE ATELIER+MAGASIN (1+4)",
    monthlyPriceDzd: 14900,
    setupFeeDzd: 19000,
    maxSubAccounts: 4,
    description:
      "Formule hybride complète pour les magasins disposant aussi d'un atelier de montage : cumule 100 % des options Atelier Pro ET Magasin de Pièces avec 1 Compte Principal + 4 Sous-Comptes dédiés.",
    allowedModules: [
      "WORKSHOP_DMS",
      "STORE_CATALOG",
      "STOCK_WMS",
      "VIN_SEARCH",
      "MEKANIK_AI",
      "MECAVERIF",
      "B2B_ORDER",
      "SUB_ACCOUNTS"
    ],
    accentColor: "#F07836"
  },
  CARROSSERIE_TOLERIE: {
    id: "CARROSSERIE_TOLERIE",
    title: "Compte Spécial Carrosserie & Tôlerie",
    shortBadge: "CARROSSERIE & TÔLERIE",
    monthlyPriceDzd: 8900,
    setupFeeDzd: 12000,
    maxSubAccounts: 2,
    description:
      "Dédié aux tôliers et carrossiers : accès par N° de châssis (VIN) aux pièces de tôlerie, plastique, boucliers, optiques et agrafes + Obligation certifiée de photographier la tôle AVANT et APRÈS réparation.",
    allowedModules: ["BODYSHOP_TOLERIE", "VIN_SEARCH", "MECAVERIF", "WORKSHOP_DMS", "B2B_ORDER"],
    accentColor: "#E53935"
  },
  FOURNISSEUR_B2B: {
    id: "FOURNISSEUR_B2B",
    title: "Compte Fournisseur / Grossiste & Importateur B2B",
    shortBadge: "FOURNISSEUR B2B",
    monthlyPriceDzd: 24900,
    setupFeeDzd: 29000,
    maxSubAccounts: 6,
    description:
      "Portail Grossiste & Distributeur : publication du catalogue OEM/Aftermarket, gestion des grilles tarifaires B2B et remises garages, disponibilité multi-dépôts et réception en temps réel des commandes ateliers/magasins.",
    allowedModules: ["SUPPLIER_PORTAL", "VIN_SEARCH", "STORE_CATALOG", "SUB_ACCOUNTS"],
    accentColor: "#A855F7"
  },
  ENTREPRISE_FLOTTE: {
    id: "ENTREPRISE_FLOTTE",
    title: "Compte Entreprise, Flotte & Location Auto",
    shortBadge: "ENTREPRISE & FLOTTE",
    monthlyPriceDzd: 19900,
    setupFeeDzd: 25000,
    maxSubAccounts: 5,
    description:
      "Gestion de parc automobile d'entreprise et agences de location : suivi kilométrique de la flotte, alertes de maintenance préventive, coûts TCO par véhicule et audit MécaVerif+.",
    allowedModules: ["FLEET_MANAGER", "VIN_SEARCH", "MECAVERIF", "WORKSHOP_DMS", "SUB_ACCOUNTS"],
    accentColor: "#F59E0B"
  }
};

export interface SubAccountMember {
  id: string;
  fullName: string;
  roleLabel: string; // ex: "Chef d'Atelier", "Mécanicien Diagnostic", "Magasinier Stock", "Vendeur Comptoir"
  email: string;
  phone: string;
  isActive: boolean;
  twoFactorEnabled: boolean;
  permissions: {
    canSearchVinAndOem: boolean;
    canManageWorkshopOrQuotes: boolean;
    canManageStoreStockAndSales: boolean;
    canUploadBodyshopPhotos: boolean;
    canUseMekanikAiObd: boolean;
    canViewFinancialKpis: boolean;
  };
}

export interface UserAccountSession {
  userId: string;
  organizationId: string;
  organizationName: string;
  fullName: string;
  email: string;
  phone: string;
  wilaya: string;
  rcNumber: string;
  role: AccountRoleId;
  twoFactorEnabled: boolean;
  totpSecretKey: string;
  createdAt: string;
  subAccounts: SubAccountMember[];
}

export interface MileagePoint {
  year: number;
  dateLabel: string;
  km: number;
  source: string;
  note: string;
  isFraudRollback?: boolean;
}

export interface BodyshopDamagePhotoRecord {
  id: string;
  date: string;
  km: number;
  shopName: string;
  impactZone: string;
  severity: string;
  isStructural: boolean;
  beforePhotoLabel: string;
  beforeMicrons: number;
  afterPhotoLabel: string;
  afterMicrons: number;
  paintCode: string;
  replacedPartsOem: string[];
  costDzd: number;
}

export interface MecaVerifVinReport {
  vin: string;
  vehicleTitle: string;
  engineCode: string;
  gearbox: string;
  paintCode: string;
  year: number;
  origin: string;
  ownersCount: number;
  defaultCurrentKm: number;
  stolenStatus: string;
  mileageHistory: MileagePoint[];
  bodyshopRecords: BodyshopDamagePhotoRecord[];
  mechanicVisits: {
    date: string;
    km: number;
    workshop: string;
    operations: string[];
    partsOem: string[];
    amountDzd: number;
  }[];
  bodyshopPartsCatalog: {
    id: string;
    category: "TÔLERIE ACIER / ALU" | "PLASTIQUE & BOUCLIERS" | "OPTIQUES & VITRAGE" | "CLIPS & AGRAFES";
    partName: string;
    oemRef: string;
    zone: string;
    priceDzd: number;
  }[];
}

export const PRELOADED_VIN_REPORTS: Record<string, MecaVerifVinReport> = {
  WVWZZZAUZHW129834: {
    vin: "WVWZZZAUZHW129834",
    vehicleTitle: "Volkswagen Golf 7.5 2.0 TDI 150ch Carat DSG6",
    engineCode: "CRLB / DFGA (1968 cm³)",
    gearbox: "DSG6 DQ250 (0D9)",
    paintCode: "LA7W (Reflet d'Argent Métallisé)",
    year: 2019,
    origin: "Concession Officielle Sovac Algérie",
    ownersCount: 2,
    defaultCurrentKm: 142500,
    stolenStatus: "AUCUN SIGNALEMENT VOL NI GAGE",
    mileageHistory: [
      { year: 2019, dateLabel: "04/2019", km: 15, source: "Livraison Neuf Concession", note: "Mise en circulation initiale" },
      { year: 2020, dateLabel: "06/2020", km: 28400, source: "Atelier Agréé Alger", note: "Révision 30 000 km + Filtres OEM" },
      { year: 2022, dateLabel: "02/2022", km: 64900, source: "DMS Mécanicauto+ Blida", note: "Vidange boîte DSG6 + Plaquettes" },
      { year: 2024, dateLabel: "05/2024", km: 112300, source: "Atelier MécaTech Pro", note: "Kit distribution + Pompe à eau 04L198119A" },
      { year: 2026, dateLabel: "11/2026", km: 142500, source: "Contrôle Actuel", note: "Kilométrage cohérent et certifié" }
    ],
    bodyshopRecords: [
      {
        id: "BODY-VW-01",
        date: "14/09/2023",
        km: 94200,
        shopName: "Carrosserie & Tôlerie Laser Alger",
        impactZone: "Aile Avant Droite & Bouclier Avant Plastique",
        severity: "Accrochage léger de stationnement (Longerons 100% intacts)",
        isStructural: false,
        beforePhotoLabel: "Photo AVANT Obligatoire : Aile Av. Droite enfoncée sur 12 cm + rayure bouclier",
        beforeMicrons: 105,
        afterPhotoLabel: "Photo APRÈS Obligatoire : Aile neuve OEM peinte en étuve (Code LA7W)",
        afterMicrons: 175,
        paintCode: "LA7W",
        replacedPartsOem: ["5G0 821 106 A (Aile Av. D)", "5G0 807 217 GRU (Bouclier Av.)"],
        costDzd: 64000
      }
    ],
    mechanicVisits: [
      {
        date: "18/05/2024",
        km: 112300,
        workshop: "Atelier MécaTech Pro (Alger)",
        operations: ["Remplacement Kit Distribution + Pompe à eau", "Purge circuit refroidissement G13", "Diagnostic OBD-II 0 défaut"],
        partsOem: ["04L 198 119 A", "G 013 A8J M1"],
        amountDzd: 48500
      }
    ],
    bodyshopPartsCatalog: [
      { id: "BP-1", category: "TÔLERIE ACIER / ALU", partName: "Aile Avant Droite en Acier Galvanisé", oemRef: "5G0 821 106 A", zone: "Latéral Avant Droit", priceDzd: 28500 },
      { id: "BP-2", category: "TÔLERIE ACIER / ALU", partName: "Capot Moteur Aluminium / Acier", oemRef: "5G0 823 031 J", zone: "Bloc Avant", priceDzd: 54000 },
      { id: "BP-3", category: "PLASTIQUE & BOUCLIERS", partName: "Pare-chocs / Bouclier Avant Apprêté PDC", oemRef: "5G0 807 217 GRU", zone: "Face Avant", priceDzd: 36000 },
      { id: "BP-4", category: "OPTIQUES & VITRAGE", partName: "Optique Phare Avant Full LED Directionnel", oemRef: "5G1 941 081 F", zone: "Avant Gauche", priceDzd: 61000 },
      { id: "BP-5", category: "CLIPS & AGRAFES", partName: "Kit 24 Agrafes Fixation Pare-chocs & Pare-boue", oemRef: "N 909 747 01", zone: "Fixations Carrosserie", priceDzd: 2400 }
    ]
  },
  VF3MCYHZJKS491820: {
    vin: "VF3MCYHZJKS491820",
    vehicleTitle: "Peugeot 3008 II GT Line 1.5 BlueHDi 130ch EAT8",
    engineCode: "DV5RC (YHZ)",
    gearbox: "Automatique EAT8 (AMN8)",
    paintCode: "KWE (Blanc Nacré Tri-Couche)",
    year: 2020,
    origin: "Importation Europe",
    ownersCount: 3,
    defaultCurrentKm: 96200,
    stolenStatus: "AUCUN SIGNALEMENT VOL • ALERTE COMPTEUR TRAFIQUÉ (-82 300 KM)",
    mileageHistory: [
      { year: 2020, dateLabel: "03/2020", km: 10, source: "Usine Sochaux", note: "Sortie d'usine" },
      { year: 2021, dateLabel: "09/2021", km: 58400, source: "Réseau Stellantis", note: "Entretien périodique + AdBlue" },
      { year: 2023, dateLabel: "07/2023", km: 134900, source: "Garage Agréé", note: "Remplacement chaîne arbres à cames 8mm" },
      { year: 2024, dateLabel: "10/2024", km: 178500, source: "Atelier DMS Oran", note: "Nettoyage FAP & Vanne EGR 9807593080" },
      {
        year: 2026,
        dateLabel: "11/2026",
        km: 96200,
        source: "Relevé Compteur Actuel",
        note: "FRAUDE ODOMÈTRE DÉTECTÉE : Chute anormale de 178 500 km à 96 200 km (-82 300 km supprimés) !",
        isFraudRollback: true
      }
    ],
    bodyshopRecords: [
      {
        id: "BODY-PSA-01",
        date: "22/11/2024",
        km: 178500,
        shopName: "Atelier Tôlerie & Marbre Pro Oran",
        impactZone: "Portière Avant Gauche, Pied Milieu & Bas de Caisse",
        severity: "Choc latéral important • Redressage au banc de mesure + Photos Avant/Après certifiées",
        isStructural: true,
        beforePhotoLabel: "Photo AVANT Obligatoire : Enfoncement sévère portière Av. G + pliure tôle bas de caisse",
        beforeMicrons: 110,
        afterPhotoLabel: "Photo APRÈS Obligatoire : Portière remplacée + masticage & peinture Blanc Nacré KWE",
        afterMicrons: 310,
        paintCode: "KWE",
        replacedPartsOem: ["98 142 509 80 (Porte Av. G)", "98 109 822 XT (Enjoliveur Bas de Caisse)"],
        costDzd: 145000
      }
    ],
    mechanicVisits: [
      {
        date: "12/10/2024",
        km: 178500,
        workshop: "Atelier DMS Oran",
        operations: ["Remplacement kit chaîne d'arbres à cames 8mm", "Remplacement Vanne EGR"],
        partsOem: ["98 305 804 80", "98 075 930 80"],
        amountDzd: 89000
      }
    ],
    bodyshopPartsCatalog: [
      { id: "BP-PSA-1", category: "TÔLERIE ACIER / ALU", partName: "Panneau de Portière Avant Gauche Nu", oemRef: "98 142 509 80", zone: "Latéral Gauche", priceDzd: 68000 },
      { id: "BP-PSA-2", category: "PLASTIQUE & BOUCLIERS", partName: "Élargisseur d'Aile & Bas de Caisse ABS", oemRef: "98 109 822 XT", zone: "Latéral Inférieur", priceDzd: 19500 },
      { id: "BP-PSA-3", category: "OPTIQUES & VITRAGE", partName: "Projecteur Full LED Peugeot Technology", oemRef: "98 142 990 80", zone: "Avant Gauche", priceDzd: 74000 }
    ]
  }
};

export interface ObdDiagnosticCase {
  code: string;
  title: string;
  severity: "CRITIQUE" | "ÉLEVÉE" | "MODÉRÉE";
  system: string;
  affectedVehicles: string;
  symptoms: string[];
  detailedExplanation: string;
  probableCauses: { cause: string; probabilityPercent: number }[];
  multimeterAndObdSteps: string[];
  actuatorTest: string;
  recommendedParts: { name: string; oemRef: string; equivRef: string; priceDzd: number }[];
  technicalDocs: {
    docTitle: string;
    docType: string;
    keySpecs: string;
    torqueSpecsNm: string;
    pinoutNotes: string;
  }[];
}

export const OBD_DIAGNOSTIC_DATABASE: Record<string, ObdDiagnosticCase> = {
  P0299: {
    code: "P0299",
    title: "Régulation de la pression de suralimentation — Limite de régulation non atteinte (Sous-pression Turbo)",
    severity: "ÉLEVÉE",
    system: "Suralimentation, Turbocompresseur & Circuit d'Air (ECM)",
    affectedVehicles: "2.0 TDI (CRLB/CUNA), 1.6/2.0 BlueHDi (DV6FC/DW10), 1.5 dCi (K9K)",
    symptoms: [
      "Perte franche de puissance à l'accélération (mode dégradé limité à 3 000 tr/min)",
      "Voyant moteur MIL et/ou préchauffage clignotant au tableau de bord",
      "Sifflement d'air sous charge (fuite durite échangeur) ou géométrie variable grippée"
    ],
    detailedExplanation:
      "Le calculateur moteur (ECM) constate que la pression de suralimentation réelle mesurée par le capteur MAP (G31) reste inférieure d'au moins 250 à 400 mbar à la consigne cartographique pendant plus de 6 secondes sous pleine charge.",
    probableCauses: [
      { cause: "Fuite sur une durite de suralimentation ou l'échangeur air/air (Intercooler)", probabilityPercent: 42 },
      { cause: "Électrovanne de régulation N75 défectueuse ou circuit de dépression percé", probabilityPercent: 31 },
      { cause: "Grippage des ailettes de la géométrie variable (VNT) du turbocompresseur ou actuateur G581", probabilityPercent: 27 }
    ],
    multimeterAndObdSteps: [
      "1. Lecture de données en temps réel (Live Data OBD) : comparer 'Pression suralimentation consigne' (ex. 2 350 mbar absolus) et 'Pression mesurée' en 3e vitesse à 2 500 tr/min.",
      "2. Test d'étanchéité sous pression (1,5 bar) du circuit d'admission entre la sortie turbo, l'intercooler et le boîtier papillon.",
      "3. Contrôle au multimètre de l'électrovanne N75 : résistance entre les bornes 1 et 2 = 14,0 à 20,0 Ω à 20°C ; alimentation +12V sur borne 1 contact mis.",
      "4. Contrôle à la pompe à vide manuelle de la capsule/actuateur de géométrie variable : doit tenir -0,75 bar sans chute et déplacer la tige de 10 à 12 mm sans point dur."
    ],
    actuatorTest:
      "Lancer le test des actionneurs OBD 'Activation Électrovanne de Suralimentation N75 / Actuateur G581' (0 % ↔ 95 %) et vérifier visuellement le déplacement fluide de la tige de géométrie variable.",
    recommendedParts: [
      { name: "Électrovanne Convertisseur de Pression Turbo (N75)", oemRef: "1K0 906 627 B / 98 095 280 80", equivRef: "PIERBURG 7.00868.02.0", priceDzd: 11200 },
      { name: "Capteur de Pression de Suralimentation MAP (3 Bars)", oemRef: "03G 906 051 E", equivRef: "BOSCH 0 281 002 845", priceDzd: 7800 },
      { name: "Turbocompresseur Géométrie Variable Complet + Actuateur", oemRef: "04L 253 010 B", equivRef: "GARRETT 813860-5003S", priceDzd: 92000 }
    ],
    technicalDocs: [
      {
        docTitle: "Schéma Électrique OEM — Gestion Suralimentation (Actuateur G581 & Capteur MAP G31)",
        docType: "SCHÉMA ÉLECTRIQUE & BROCHAGE",
        keySpecs: "Signal capteur position recopie turbo : 0,75 V (repos) à 3,85 V (pleine charge) • Alimentation capteur : 5,0 V stabilisé.",
        torqueSpecsNm: "Écrous collecteur/turbo : 25 Nm • Conduite d'arrivée d'huile : 22 Nm • Collier V-Band échappement : 15 Nm.",
        pinoutNotes: "Connecteur 6 voies Actuateur Turbo : Pin 1 = +5V Réf | Pin 2 = Signal Position | Pin 3 = Masse Capteur | Pin 5 & 6 = Commande PWM Moteur."
      }
    ]
  },
  P0401: {
    code: "P0401",
    title: "Système de Recirculation des Gaz d'Échappement (EGR) — Débit insuffisant détecté",
    severity: "MODÉRÉE",
    system: "Dépollution EGR & Admission",
    affectedVehicles: "1.6 / 2.0 BlueHDi, 2.0 TDI, 1.6 TDI, 1.5 dCi",
    symptoms: [
      "Voyant moteur allumé avec message 'Antipollution à contrôler'",
      "À-coups moteur à régime stabilisé entre 1 800 et 2 200 tr/min",
      "Surconsommation de carburant et encrassement accéléré du FAP"
    ],
    detailedExplanation:
      "Lorsque le calculateur commande l'ouverture de la vanne EGR, le débitmètre d'air massique (MAF) ne détecte pas la baisse attendue du débit d'air frais aspiré, indiquant un conduit EGR obstrué par la calamine ou une soupape EGR bloquée fermée.",
    probableCauses: [
      { cause: "Encrassement par la suie/calamine de la vanne EGR et du refroidisseur EGR", probabilityPercent: 58 },
      { cause: "Défaillance du moteur électrique ou du potentiomètre de recopie de la vanne EGR", probabilityPercent: 28 },
      { cause: "Signal erroné du débitmètre massique d'air (MAF)", probabilityPercent: 14 }
    ],
    multimeterAndObdSteps: [
      "1. Lecture paramètre OBD : comparer 'Consigne ouverture EGR (%)' et 'Position réelle EGR (%)' au ralenti.",
      "2. Contrôle multimètre sur connecteur 5 voies Vanne EGR : alimentation +5V sur Pin 2 (potentiomètre), masse sur Pin 4, tension recopie Pin 3 variant de 0,7V (fermée) à 4,1V (ouverte).",
      "3. Dépose et inspection endoscopique du refroidisseur EGR et de la pipe d'admission."
    ],
    actuatorTest:
      "Exécuter l'apprentissage/adaptation des butées de vanne EGR après remplacement ou décalaminage.",
    recommendedParts: [
      { name: "Module Vanne EGR Électrique + Refroidisseur", oemRef: "98 075 930 80 / 04L 131 512 BH", equivRef: "VALEO 700444", priceDzd: 33500 },
      { name: "Joint Métallique Bride EGR", oemRef: "03L 131 547 B", equivRef: "ELRING 737.070", priceDzd: 1400 }
    ],
    technicalDocs: [
      {
        docTitle: "Note Technique Constructeur — Remplacement & Apprentissage Vanne EGR",
        docType: "PROCÉDURE & COUPLES DE SERRAGE",
        keySpecs: "Résistance bobinage moteur DC EGR : 2,8 à 5,2 Ω à 20°C.",
        torqueSpecsNm: "Vis de fixation vanne EGR sur culasse/bloc : 20 Nm (approche en croix) • Raccord tube inox EGR : 22 Nm.",
        pinoutNotes: "Pin 1 & 5 : Commande PWM Moteur EGR (12V) | Pin 2 : +5V Réf | Pin 3 : Signal Recopie Position | Pin 4 : Masse."
      }
    ]
  },
  P0087: {
    code: "P0087",
    title: "Pression de la rampe d'injection Common Rail trop basse",
    severity: "CRITIQUE",
    system: "Injection Haute Pression Diesel (Common Rail)",
    affectedVehicles: "Tous moteurs Diesel Common Rail Bosch / Continental / Delphi",
    symptoms: [
      "Démarrage très difficile ou impossible à chaud comme à froid",
      "Coupure nette du moteur lors d'une forte demande de couple (dépassement / côte)",
      "Claquement métallique d'injection ou mode dégradé immédiat"
    ],
    detailedExplanation:
      "La pression réelle dans la rampe haute pression est inférieure de plus de 150 à 200 bars à la consigne calculateur (ex: < 180 bars au démarreur alors que 250 bars minimum sont requis pour autoriser l'ouverture des injecteurs).",
    probableCauses: [
      { cause: "Filtre à gasoil colmaté ou défaillance de la pompe de gavage basse pression", probabilityPercent: 35 },
      { cause: "Fuite interne excessive (retour trop élevé) sur un ou plusieurs injecteurs", probabilityPercent: 38 },
      { cause: "Régulateur de débit (IMV/DRV) ou pompe haute pression HP4/CP4 limaille", probabilityPercent: 27 }
    ],
    multimeterAndObdSteps: [
      "1. Contrôler sous démarreur la 'Pression Rail Mesurée' : doit atteindre > 250 bars en moins de 2 secondes.",
      "2. Effectuer le test des fioles de retour d'injecteurs pendant 2 minutes au ralenti : tout injecteur dépassant 35 ml ou le double des autres est fuyard.",
      "3. Déposer le régulateur de débit sur la pompe HP et vérifier l'absence de limaille métallique au fond du logement."
    ],
    actuatorTest: "Test de compression électronique et équilibrage des débits d'injecteurs cylindre par cylindre.",
    recommendedParts: [
      { name: "Injecteur Common Rail Piézo / Solénoïde", oemRef: "04L 130 277 AC", equivRef: "BOSCH 0 445 110 469", priceDzd: 39500 },
      { name: "Régulateur de Pression / Débit Rampe (DRV)", oemRef: "057 130 764 AB", equivRef: "BOSCH 0 281 006 074", priceDzd: 16500 }
    ],
    technicalDocs: [
      {
        docTitle: "Revue Technique — Diagnostic Haute Pression & Codage IMA Injecteurs",
        docType: "REVUE TECHNIQUE & SERRAGE",
        keySpecs: "Pression au ralenti : 250 à 320 bars • Pression pleine charge : 1 600 à 2 000 bars.",
        torqueSpecsNm: "Bride d'injecteur (vis neuve obligatoire) : 8 Nm + serrage angulaire 180° • Raccord tube HP : 28 Nm.",
        pinoutNotes: "Capteur Pression Rail (3 fils) : Pin 1 = Masse | Pin 2 = Signal (0,5V à 0 bar ; ~1,2V au ralenti ; 4,5V à 2000 bars) | Pin 3 = +5V."
      }
    ]
  }
};
