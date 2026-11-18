import React, { useEffect, useState } from "react";
import {
  ACCOUNT_ROLES,
  AccountRoleId,
  AccountRoleMeta,
  SUPER_ADMIN_UNIQUE_EMAIL,
  SubAccountMember,
  UserAccountSession
} from "./typesAndData";
import { AuthAndAccountModal } from "./AuthAndAccountModals";
import { MecaVerifAndBodyshopModule } from "./MecaVerifAndBodyshopModule";
import {
  MekanikAiDiagnosticAssistant,
  SuperAdminPortalView
} from "./MekanikAiAndSuperAdminModule";
import {
  BillingCycleType,
  PublicFacadeAndVideoTutorial
} from "./PublicFacadeAndVideoTutorial";

export const MekanikTheme = {
  bg: "#101820",
  surface: "#1C2833",
  orange: "#F07836",
  redOem: "#E53935",
  textPrimary: "#E8EDF2",
  textSecondary: "#94A3B8",
  green: "#22C55E",
  border: "#2B3B4B"
};

export type ActiveWorkspaceTab =
  | "SITE_PUBLIC"
  | "DASHBOARD_COMPTE"
  | "ATELIER_DMS"
  | "MAGASIN_OEM"
  | "HISTORIQUE_COMMANDES"
  | "CARROSSERIE_TOLERIE"
  | "MEKANIK_AI_OBD"
  | "MECAVERIF_AUDIT"
  | "FOURNISSEUR_B2B"
  | "SUPER_ADMIN_2FA";

const INITIAL_DEMO_ACCOUNTS: UserAccountSession[] = [
  {
    userId: "USR-MIXTE-01",
    organizationId: "ORG-MIXTE-ALGER-01",
    organizationName: "SARL Garage & Pièces El-Djazair Auto+ (Mixte 1+4)",
    fullName: "Badrou Meziane",
    email: "badrou@mekanikplus.dz",
    phone: "+213 550 94 82 10",
    wilaya: "Alger (16)",
    rcNumber: "16/00-4829104B22",
    role: "MIXTE_ATELIER_MAGASIN",
    twoFactorEnabled: true,
    totpSecretKey: "JBSWY3DPEHPK3PXP-MIX",
    createdAt: "15/11/2026",
    subAccounts: [
      {
        id: "SUB-1",
        fullName: "Yacine Mansouri",
        roleLabel: "Chef d'Atelier Mécanique",
        email: "atelier@mekanikplus.dz",
        phone: "+213 551 11 22 33",
        isActive: true,
        twoFactorEnabled: true,
        permissions: {
          canSearchVinAndOem: true,
          canManageWorkshopOrQuotes: true,
          canManageStoreStockAndSales: false,
          canUploadBodyshopPhotos: false,
          canUseMekanikAiObd: true,
          canViewFinancialKpis: true
        }
      },
      {
        id: "SUB-2",
        fullName: "Sofiane Khelifi",
        roleLabel: "Responsable Magasin & Stock Pièces",
        email: "magasin@mekanikplus.dz",
        phone: "+213 552 44 55 66",
        isActive: true,
        twoFactorEnabled: true,
        permissions: {
          canSearchVinAndOem: true,
          canManageWorkshopOrQuotes: false,
          canManageStoreStockAndSales: true,
          canUploadBodyshopPhotos: false,
          canUseMekanikAiObd: false,
          canViewFinancialKpis: true
        }
      },
      {
        id: "SUB-3",
        fullName: "Mourad Brahimi",
        roleLabel: "Technicien Diagnostic OBD-II",
        email: "diag@mekanikplus.dz",
        phone: "+213 553 77 88 99",
        isActive: true,
        twoFactorEnabled: true,
        permissions: {
          canSearchVinAndOem: true,
          canManageWorkshopOrQuotes: true,
          canManageStoreStockAndSales: false,
          canUploadBodyshopPhotos: false,
          canUseMekanikAiObd: true,
          canViewFinancialKpis: false
        }
      },
      {
        id: "SUB-4",
        fullName: "Amine Touati",
        roleLabel: "Vendeur Comptoir Pièces OEM",
        email: "comptoir@mekanikplus.dz",
        phone: "+213 554 00 11 22",
        isActive: true,
        twoFactorEnabled: true,
        permissions: {
          canSearchVinAndOem: true,
          canManageWorkshopOrQuotes: false,
          canManageStoreStockAndSales: true,
          canUploadBodyshopPhotos: false,
          canUseMekanikAiObd: false,
          canViewFinancialKpis: false
        }
      }
    ]
  },
  {
    userId: "USR-PART-02",
    organizationId: "ORG-PART-ALGER-02",
    organizationName: "Compte Personnel Conducteur (Karim Benali)",
    fullName: "Karim Benali",
    email: "karim.particulier@gmail.com",
    phone: "+213 661 20 30 40",
    wilaya: "Alger (16)",
    rcNumber: "PARTICULIER",
    role: "PARTICULIER",
    twoFactorEnabled: true,
    totpSecretKey: "JBSWY3DPEHPK3PXP-PAR",
    createdAt: "15/11/2026",
    subAccounts: []
  },
  {
    userId: "USR-ATELIER-03",
    organizationId: "ORG-ATELIER-ORAN-03",
    organizationName: "Atelier MécaTech Pro Oran",
    fullName: "Nabil Boudiaf",
    email: "contact@mecatech-oran.dz",
    phone: "+213 550 88 11 22",
    wilaya: "Oran (31)",
    rcNumber: "31/00-9918204B21",
    role: "ATELIER_PRO",
    twoFactorEnabled: true,
    totpSecretKey: "JBSWY3DPEHPK3PXP-ATE",
    createdAt: "15/11/2026",
    subAccounts: []
  },
  {
    userId: "USR-MAGASIN-04",
    organizationId: "ORG-MAGASIN-SETIF-04",
    organizationName: "Comptoir Pièces OEM Hauts-Plateaux",
    fullName: "Farid Ziani",
    email: "ventes@oem-setif.dz",
    phone: "+213 555 12 34 56",
    wilaya: "Sétif (19)",
    rcNumber: "19/00-3381920B20",
    role: "MAGASIN_PIECES",
    twoFactorEnabled: true,
    totpSecretKey: "JBSWY3DPEHPK3PXP-MAG",
    createdAt: "15/11/2026",
    subAccounts: []
  },
  {
    userId: "USR-CARROSSERIE-05",
    organizationId: "ORG-CARR-BLIDA-05",
    organizationName: "Atelier Spécial Carrosserie & Tôlerie Laser Blida",
    fullName: "Redha Hamidi",
    email: "tolerie@carrosserie-blida.dz",
    phone: "+213 556 70 80 90",
    wilaya: "Blida (09)",
    rcNumber: "09/00-7741920B23",
    role: "CARROSSERIE_TOLERIE",
    twoFactorEnabled: true,
    totpSecretKey: "JBSWY3DPEHPK3PXP-CAR",
    createdAt: "15/11/2026",
    subAccounts: []
  },
  {
    userId: "USR-FOURNISSEUR-06",
    organizationId: "ORG-FOURN-ALGER-06",
    organizationName: "EuroParts Grossiste & Importateur B2B Algérie",
    fullName: "Walid Cherif",
    email: "b2b@europarts-dz.com",
    phone: "+213 550 10 20 30",
    wilaya: "Alger - Oued Smar (16)",
    rcNumber: "16/00-1102938B19",
    role: "FOURNISSEUR_B2B",
    twoFactorEnabled: true,
    totpSecretKey: "JBSWY3DPEHPK3PXP-B2B",
    createdAt: "15/11/2026",
    subAccounts: []
  }
];

export const App: React.FC = () => {
  // Persistance des comptes dans localStorage
  const [savedAccounts, setSavedAccounts] = useState<UserAccountSession[]>(() => {
    try {
      const raw = localStorage.getItem("mekanik_saas_accounts_v2");
      if (raw) {
        const parsed = JSON.parse(raw);
        if (Array.isArray(parsed) && parsed.length > 0) {
          const valid = parsed.filter(
            (a) => a && typeof a.userId === "string" && a.role && ACCOUNT_ROLES[a.role as AccountRoleId]
          );
          if (valid.length > 0) return valid;
        }
      }
    } catch (_e) {}
    return INITIAL_DEMO_ACCOUNTS;
  });

  const [currentAccount, setCurrentAccount] = useState<UserAccountSession>(() => {
    try {
      const activeId = localStorage.getItem("mekanik_saas_active_user_id");
      if (activeId) {
        const found = savedAccounts.find((a) => a.userId === activeId);
        if (found) return found;
      }
    } catch (_e) {}
    return savedAccounts[0] || INITIAL_DEMO_ACCOUNTS[0];
  });

  useEffect(() => {
    try {
      localStorage.setItem("mekanik_saas_accounts_v2", JSON.stringify(savedAccounts));
      localStorage.setItem("mekanik_saas_active_user_id", currentAccount.userId);
    } catch (_e) {}
  }, [savedAccounts, currentAccount]);

  // Détection de la route secrète /admin (accessible uniquement en tapant /admin ou #/admin dans l'URL)
  const checkIsAdminUrl = () => {
    if (typeof window === "undefined") return false;
    const path = window.location.pathname.toLowerCase();
    const hash = window.location.hash.toLowerCase();
    const params = new URLSearchParams(window.location.search);
    return (
      path === "/admin" ||
      path.startsWith("/admin/") ||
      hash === "#/admin" ||
      hash.startsWith("#/admin") ||
      params.get("admin") === "1"
    );
  };

  const [isHiddenAdminRoute, setIsHiddenAdminRoute] = useState<boolean>(checkIsAdminUrl);
  const [isClientLoggedIn, setIsClientLoggedIn] = useState<boolean>(false);
  const [selectedBillingCycle, setSelectedBillingCycle] = useState<BillingCycleType>("ANNUEL");
  const [preselectedRegisterRole, setPreselectedRegisterRole] =
    useState<AccountRoleId>("MIXTE_ATELIER_MAGASIN");

  useEffect(() => {
    const syncRoute = () => setIsHiddenAdminRoute(checkIsAdminUrl());
    window.addEventListener("popstate", syncRoute);
    window.addEventListener("hashchange", syncRoute);
    return () => {
      window.removeEventListener("popstate", syncRoute);
      window.removeEventListener("hashchange", syncRoute);
    };
  }, []);

  const navigateToHiddenAdminUrl = () => {
    try {
      window.history.pushState({}, "", "/admin");
    } catch (_e) {
      window.location.hash = "#/admin";
    }
    setIsHiddenAdminRoute(true);
  };

  const exitHiddenAdminUrlToPublicFacade = () => {
    try {
      window.history.pushState({}, "", "/");
      if (window.location.hash.toLowerCase().includes("admin")) {
        window.location.hash = "";
      }
    } catch (_e) {
      window.location.hash = "";
    }
    setIsHiddenAdminRoute(false);
    setActiveTab("SITE_PUBLIC");
  };

  // Par défaut, l'ouverture de la plateforme affiche la Façade Officielle (Logo animé, Présentation, Prix Mensuel/Annuel & Vidéo Tuto)
  const [activeTab, setActiveTab] = useState<ActiveWorkspaceTab>("SITE_PUBLIC");
  const [authModalOpen, setAuthModalOpen] = useState(false);
  const [authModalInitialTab, setAuthModalInitialTab] = useState<
    "SWITCH_ROLE" | "REGISTER" | "LOGIN" | "SUB_ACCOUNTS_2FA"
  >("LOGIN");

  const [searchMode, setSearchMode] = useState<"VIN" | "CODE_MOTEUR" | "VEHICULE">("VIN");
  const [vinQuery, setVinQuery] = useState("WVWZZZAUZHW129834");
  const [decodedBanner, setDecodedBanner] = useState(
    "VIN WVWZZZAUZHW129834 identifié : Volkswagen Golf 7.5 2.0 TDI 150ch (Moteur CRLB • Boîte DSG6 • 2019)"
  );
  const [toastMsg, setToastMsg] = useState<string | null>(null);

  // Tarifs des abonnements & MécaVerif+ modifiables manuellement par le Super-Admin (badroumc@gmail.com)
  const [rolesPricing, setRolesPricing] = useState<Record<AccountRoleId, AccountRoleMeta>>(() => {
    try {
      const saved = localStorage.getItem("mekanik_superadmin_roles_pricing_v1");
      if (saved) {
        const parsed = JSON.parse(saved);
        if (parsed && typeof parsed === "object") {
          return { ...ACCOUNT_ROLES, ...parsed };
        }
      }
    } catch (_e) {}
    return ACCOUNT_ROLES;
  });

  const [mecaVerifPrices, setMecaVerifPrices] = useState<{
    proPriceDzd: number;
    partPriceDzd: number;
  }>(() => {
    try {
      const saved = localStorage.getItem("mekanik_superadmin_mecaverif_prices_v1");
      if (saved) {
        const parsed = JSON.parse(saved);
        if (parsed && typeof parsed.proPriceDzd === "number" && typeof parsed.partPriceDzd === "number") {
          return parsed;
        }
      }
    } catch (_e) {}
    return { proPriceDzd: 250, partPriceDzd: 300 };
  });

  // Interrupteurs globaux pilotés par le Super-Administrateur (badroumc@gmail.com)
  const [featureFlags, setFeatureFlags] = useState<Record<string, boolean>>({
    vinDecoder: true,
    mecaVerif: true,
    mekanikAiObd: true,
    bodyshopMandatoryPhotos: true,
    b2bSupplierPortal: true,
    subAccounts2Fa: true
  });

  // Catalogue Pièces & Stock Magasin / Fournisseur
  const [partsCatalog, setPartsCatalog] = useState([
    {
      id: "PART-01",
      partName: "Kit de Distribution Complet + Pompe à Eau",
      system: "Moteur & Distribution",
      oemRef: "04L 198 119 A",
      equivRefs: ["CONTITECH CT1168WP1", "GATES KP15678XS", "INA 530 0650 30"],
      compat: ["VW Golf 7 2.0 TDI (CRLB)", "Audi A3 2.0 TDI", "Seat Leon", "Skoda Octavia"],
      publicPriceDzd: 34500,
      b2bPriceDzd: 26800,
      stockQty: 24,
      warehouse: "Dépôt Central Alger (Oued Smar)"
    },
    {
      id: "PART-02",
      partName: "Vanne EGR Électropneumatique + Refroidisseur",
      system: "Dépollution & Admission",
      oemRef: "98 075 930 80",
      equivRefs: ["VALEO 700444", "PIERBURG 7.09720.00.0", "DELPHI EG10512"],
      compat: ["Peugeot 308 / 3008 1.6 & 2.0 BlueHDi", "Citroën C4", "Opel Grandland"],
      publicPriceDzd: 42000,
      b2bPriceDzd: 33500,
      stockQty: 14,
      warehouse: "Dépôt Oran (Es Sénia)"
    },
    {
      id: "PART-03",
      partName: "Optique Phare Avant Full LED + Support Tôlerie",
      system: "Carrosserie, Tôlerie & Éclairage",
      oemRef: "5G1 941 081 F",
      equivRefs: ["HELLA 1EX 013 924-111", "VALEO 046891"],
      compat: ["VW Golf 7.5 / 8", "Seat Leon FR", "Renault Clio 5 (260604129R)"],
      publicPriceDzd: 78000,
      b2bPriceDzd: 61000,
      stockQty: 7,
      warehouse: "Dépôt Carrosserie Blida"
    },
    {
      id: "PART-04",
      partName: "Turbocompresseur Géométrie Variable + Actuateur Électronique",
      system: "Suralimentation & Moteur",
      oemRef: "04L 253 010 B",
      equivRefs: ["GARRETT 813860-5003S", "BORGWARNER 53039880205"],
      compat: ["VW Golf 7 2.0 TDI 150ch", "Audi A3 / Q3 2.0 TDI", "Skoda Superb"],
      publicPriceDzd: 118000,
      b2bPriceDzd: 92000,
      stockQty: 11,
      warehouse: "Dépôt Central Alger"
    }
  ]);

  // Ordres de réparation (OR) & Devis Atelier
  const [repairOrders, setRepairOrders] = useState([
    {
      id: "OR-2026-401",
      clientName: "Karim Boudiaf",
      phone: "+213 550 88 11 22",
      vehicle: "VW Golf 7.5 2.0 TDI (WVWZZZAUZHW129834)",
      description: "Remplacement Kit Distribution 04L198119A + Diagnostic OBD P0299",
      amountDzd: 48500,
      status: "EN COURS AU PONT N°2"
    },
    {
      id: "OR-2026-402",
      clientName: "Nabil Benkhedda",
      phone: "+213 661 45 90 12",
      vehicle: "Peugeot 3008 1.5 BlueHDi (VF3MCYHZJKS491820)",
      description: "Remplacement Vanne EGR 9807593080 + Apprentissage Valise",
      amountDzd: 52000,
      status: "DEVIS VALIDÉ CLIENT"
    }
  ]);
  const [newOrClient, setNewOrClient] = useState("");
  const [newOrDesc, setNewOrDesc] = useState("");
  const [newOrAmount, setNewOrAmount] = useState("25000");

  // Commandes B2B vers l'Espace Fournisseur (isolées par organizationId)
  const [b2bOrders, setB2bOrders] = useState([
    {
      id: "CMD-B2B-2026-801",
      organizationId: "ORG-MIXTE-ALGER-01",
      requesterOrg: "SARL Garage & Pièces El-Djazair Auto+ (Mixte 1+4)",
      oemRef: "04L 198 119 A",
      partName: "Kit de Distribution Complet + Pompe à Eau",
      qty: 3,
      unitPriceDzd: 26800,
      createdAt: "17/11/2026 • 09:40",
      status: "NOUVELLE DEMANDE"
    },
    {
      id: "CMD-B2B-2026-802",
      organizationId: "ORG-CARR-BLIDA-05",
      requesterOrg: "Atelier Spécial Carrosserie & Tôlerie Laser Blida",
      oemRef: "5G1 941 081 F",
      partName: "Optique Phare Avant Full LED + Support Tôlerie",
      qty: 1,
      unitPriceDzd: 61000,
      createdAt: "17/11/2026 • 10:15",
      status: "EN PRÉPARATION"
    },
    {
      id: "CMD-B2B-2026-803",
      organizationId: "ORG-MAGASIN-SETIF-04",
      requesterOrg: "Comptoir Pièces OEM Hauts-Plateaux (Magasin Pièces)",
      oemRef: "98 075 930 80",
      partName: "Vanne EGR Électropneumatique + Refroidisseur",
      qty: 5,
      unitPriceDzd: 33500,
      createdAt: "16/11/2026 • 16:20",
      status: "EXPÉDIÉE AU GARAGE"
    },
    {
      id: "CMD-B2B-2026-804",
      organizationId: "ORG-MAGASIN-SETIF-04",
      requesterOrg: "Comptoir Pièces OEM Hauts-Plateaux (Magasin Pièces)",
      oemRef: "04L 198 119 A",
      partName: "Kit de Distribution Complet + Pompe à Eau",
      qty: 4,
      unitPriceDzd: 26800,
      createdAt: "14/11/2026 • 11:05",
      status: "LIVRÉE ✓"
    },
    {
      id: "CMD-B2B-2026-805",
      organizationId: "ORG-ATELIER-ORAN-03",
      requesterOrg: "Atelier MécaTech Pro Oran (Atelier Pro)",
      oemRef: "04L 253 010 B",
      partName: "Turbocompresseur Géométrie Variable + Actuateur Électronique",
      qty: 1,
      unitPriceDzd: 92000,
      createdAt: "17/11/2026 • 08:50",
      status: "EN PRÉPARATION"
    },
    {
      id: "CMD-B2B-2026-806",
      organizationId: "ORG-ATELIER-ORAN-03",
      requesterOrg: "Atelier MécaTech Pro Oran (Atelier Pro)",
      oemRef: "04L 198 119 A",
      partName: "Kit de Distribution Complet + Pompe à Eau",
      qty: 2,
      unitPriceDzd: 26800,
      createdAt: "13/11/2026 • 14:30",
      status: "LIVRÉE ✓"
    }
  ]);
  const [orderHistoryStatusFilter, setOrderHistoryStatusFilter] = useState<string>("TOUS");

  // Ajout produit fournisseur
  const [newSupPartName, setNewSupPartName] = useState("");
  const [newSupOem, setNewSupOem] = useState("");
  const [newSupPrice, setNewSupPrice] = useState("18500");

  const showToast = (msg: string) => {
    setToastMsg(msg);
    setTimeout(() => setToastMsg(null), 4200);
  };

  // =========================================================================
  // SÉPARATION STRICTE DES COMPTES : NAVIGATION FILTRÉE PAR FORMULE CHOISIE
  // =========================================================================
  const roleMeta =
    rolesPricing[currentAccount.role] ||
    ACCOUNT_ROLES[currentAccount.role] ||
    ACCOUNT_ROLES.MIXTE_ATELIER_MAGASIN;

  const getFilteredNavigationForRole = (
    role: AccountRoleId
  ): { id: ActiveWorkspaceTab; icon: string; label: string; subtitle: string }[] => {
    switch (role) {
      case "PARTICULIER":
        // Compte Particulier : Aucune option d'atelier ni de vendeur ni de fournisseur !
        return [
          { id: "DASHBOARD_COMPTE", icon: "🚗", label: "Mon Espace Particulier", subtitle: "Mes véhicules, alertes & suivi" },
          { id: "MECAVERIF_AUDIT", icon: "🛡️", label: "MécaVerif+ (300 DA/VIN)", subtitle: "Courbe km & photos tôle Avant/Après" },
          { id: "SITE_PUBLIC", icon: "🌐", label: "Accueil & Tarifs", subtitle: "Recherche VIN & présentation" }
        ];
      case "ATELIER_PRO":
        // Compte Atelier Pro : Uniquement les outils de l'atelier mécanique + Historique des Commandes
        return [
          { id: "DASHBOARD_COMPTE", icon: "🛠️", label: "Tableau de Bord Atelier", subtitle: "KPIs, sous-comptes & activité" },
          { id: "ATELIER_DMS", icon: "🧾", label: "DMS Atelier (OR & Devis)", subtitle: "Clients, ordres de réparation & VIN" },
          { id: "HISTORIQUE_COMMANDES", icon: "📦", label: "Historique des Commandes", subtitle: "Commandes passées & statuts (Org isolée)" },
          { id: "MEKANIK_AI_OBD", icon: "🧠", label: "MekanikAI Diagnostic OBD", subtitle: "Codes défauts, multimètre & schémas" },
          { id: "MECAVERIF_AUDIT", icon: "🛡️", label: "MécaVerif+ Pro (250 DA)", subtitle: "Audit kilométrage & historique" },
          { id: "SITE_PUBLIC", icon: "🌐", label: "Portail Public SaaS", subtitle: "Présentation & formules" }
        ];
      case "MAGASIN_PIECES":
        // Compte Magasin de Pièces : Uniquement les outils du vendeur de pièces + Historique des Commandes
        return [
          { id: "DASHBOARD_COMPTE", icon: "🏪", label: "Tableau de Bord Magasin", subtitle: "Ventes comptoir, stock & alertes" },
          { id: "MAGASIN_OEM", icon: "⚙️", label: "Catalogue Pièces & Stock", subtitle: "Réf OEM rouge/orange & équivalences" },
          { id: "HISTORIQUE_COMMANDES", icon: "📦", label: "Historique des Commandes", subtitle: "Commandes passées & statuts (Org isolée)" },
          { id: "MECAVERIF_AUDIT", icon: "🛡️", label: "MécaVerif+ Pro (250 DA)", subtitle: "Vérification châssis client" },
          { id: "SITE_PUBLIC", icon: "🌐", label: "Portail Public SaaS", subtitle: "Présentation & formules" }
        ];
      case "MIXTE_ATELIER_MAGASIN":
        // Compte Mixte (1 Principal + 4 Sous-Comptes) : Cumule 100% des options Atelier + Magasin !
        return [
          { id: "DASHBOARD_COMPTE", icon: "⚡", label: "Dashboard Mixte (1+4)", subtitle: "Atelier + Magasin & 4 sous-comptes" },
          { id: "ATELIER_DMS", icon: "🛠️", label: "Espace Atelier (OR & Devis)", subtitle: "Clients, montage & réparations" },
          { id: "MAGASIN_OEM", icon: "🏪", label: "Espace Magasin & Stock", subtitle: "Catalogue OEM, stock & comptoir" },
          { id: "HISTORIQUE_COMMANDES", icon: "📦", label: "Historique des Commandes", subtitle: "Commandes passées & statuts (Org isolée)" },
          { id: "MEKANIK_AI_OBD", icon: "🧠", label: "MekanikAI Diagnostic OBD", subtitle: "Codes DTC, procédures & schémas" },
          { id: "MECAVERIF_AUDIT", icon: "🛡️", label: "MécaVerif+ & Carrosserie", subtitle: "Courbe km & photos Avant/Après" },
          { id: "SITE_PUBLIC", icon: "🌐", label: "Portail Public SaaS", subtitle: "Présentation & formules" }
        ];
      case "CARROSSERIE_TOLERIE":
        // Compte Spécial Carrosserie & Tôlerie
        return [
          { id: "CARROSSERIE_TOLERIE", icon: "🔨", label: "Espace Tôlerie & Photos", subtitle: "Pièces tôlerie/plastique & Avant/Après" },
          { id: "ATELIER_DMS", icon: "🧾", label: "Devis Carrosserie & Peinture", subtitle: "Ordres de réparation tôlerie" },
          { id: "MAGASIN_OEM", icon: "⚙️", label: "Catalogue Pièces par VIN", subtitle: "Optiques, boucliers, ailes & clips" },
          { id: "DASHBOARD_COMPTE", icon: "👤", label: "Mon Compte Carrosserie", subtitle: "Abonnement & paramètres 2FA" }
        ];
      case "FOURNISSEUR_B2B":
        // Compte Fournisseur / Grossiste B2B
        return [
          { id: "FOURNISSEUR_B2B", icon: "📦", label: "Portail Fournisseur B2B", subtitle: "Catalogue grossiste, prix & commandes" },
          { id: "MAGASIN_OEM", icon: "⚙️", label: "Catalogue Références OEM", subtitle: "Disponibilité dépôts & équivalences" },
          { id: "DASHBOARD_COMPTE", icon: "📊", label: "Statistiques Grossiste", subtitle: "CA B2B, garages clients & 2FA" }
        ];
      case "ENTREPRISE_FLOTTE":
        return [
          { id: "DASHBOARD_COMPTE", icon: "🚐", label: "Gestion de Parc & Flotte", subtitle: "Véhicules, entretiens & coûts TCO" },
          { id: "MECAVERIF_AUDIT", icon: "🛡️", label: "Audit MécaVerif+ Flotte", subtitle: "Suivi kilométrique & sinistres" },
          { id: "ATELIER_DMS", icon: "🛠️", label: "Ordres de Maintenance", subtitle: "Suivi des interventions parc" }
        ];
    }
  };

  const allowedNavItems = getFilteredNavigationForRole(currentAccount.role);

  // Changement ou création de compte : RÉINITIALISATION COMPLÈTE de la vue précédente
  const handleSwitchOrRegisterAccount = (nextAccount: UserAccountSession) => {
    const exists = savedAccounts.some((a) => a.userId === nextAccount.userId);
    if (exists) {
      setSavedAccounts(
        savedAccounts.map((a) => (a.userId === nextAccount.userId ? nextAccount : a))
      );
    } else {
      setSavedAccounts([nextAccount, ...savedAccounts]);
    }
    setCurrentAccount(nextAccount);
    setIsClientLoggedIn(true);
    setAuthModalOpen(false);

    // Réinitialisation stricte sur l'espace principal du compte choisi
    const defaultFirstTab = getFilteredNavigationForRole(nextAccount.role)[0].id;
    setActiveTab(defaultFirstTab);
    showToast(
      `Session authentifiée : ${nextAccount.organizationName} [${ACCOUNT_ROLES[nextAccount.role].shortBadge}] — Espace client déverrouillé.`
    );
  };

  const handleOrderPartB2b = (oemRef: string, partName: string, priceDzd: number) => {
    const nowStr = new Date().toLocaleDateString("fr-FR") + " • " + new Date().toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" });
    setB2bOrders([
      {
        id: `CMD-B2B-2026-${Math.floor(810 + Math.random() * 180)}`,
        organizationId: currentAccount.organizationId,
        requesterOrg: `${currentAccount.organizationName} (${roleMeta.shortBadge})`,
        oemRef,
        partName,
        qty: 1,
        unitPriceDzd: priceDzd,
        createdAt: nowStr,
        status: "NOUVELLE DEMANDE"
      },
      ...b2bOrders
    ]);
    showToast(
      `Commande B2B transmise au Fournisseur pour ${oemRef} (${partName}) — enregistrée dans l'Historique des Commandes de ${currentAccount.organizationId}`
    );
  };

  // =========================================================================
  // ROUTE SECRÈTE /admin : PANNEAU SUPER-ADMINISTRATEUR CACHÉ (AUCUN LIEN PUBLIC)
  // =========================================================================
  if (isHiddenAdminRoute) {
    return (
      <div
        style={{
          minHeight: "100vh",
          backgroundColor: MekanikTheme.bg,
          color: MekanikTheme.textPrimary,
          padding: 24
        }}
      >
        {toastMsg && (
          <div
            style={{
              padding: "12px 16px",
              marginBottom: 16,
              borderRadius: 10,
              backgroundColor: "rgba(34, 197, 94, 0.18)",
              border: `1px solid ${MekanikTheme.green}`,
              color: MekanikTheme.textPrimary,
              fontWeight: 800,
              fontSize: 13
            }}
          >
            ✓ {toastMsg}
          </div>
        )}

        <header
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            flexWrap: "wrap",
            gap: 12,
            padding: "16px 20px",
            backgroundColor: MekanikTheme.surface,
            borderRadius: 14,
            border: `1.5px solid ${MekanikTheme.redOem}`,
            marginBottom: 20
          }}
        >
          <div>
            <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
              <span
                style={{
                  padding: "4px 10px",
                  borderRadius: 6,
                  backgroundColor: "rgba(229,57,53,0.22)",
                  border: `1px solid ${MekanikTheme.redOem}`,
                  color: "#FFF",
                  fontWeight: 900,
                  fontSize: 11,
                  fontFamily: "monospace"
                }}
              >
                URL CACHÉE : /admin
              </span>
              <h1 style={{ margin: 0, fontSize: 18, fontWeight: 900 }}>
                PANNEAU SUPER-ADMINISTRATEUR PRIVÉ — MEKANIK+
              </h1>
            </div>
            <div style={{ fontSize: 12, color: MekanikTheme.textSecondary, marginTop: 4 }}>
              Accès strictement réservé à <strong>{SUPER_ADMIN_UNIQUE_EMAIL}</strong> • Aucun mot de passe ni code n'est affiché en clair • Invisible depuis la page d'accueil.
            </div>
          </div>

          <button
            onClick={exitHiddenAdminUrlToPublicFacade}
            style={{
              padding: "10px 16px",
              borderRadius: 9,
              border: `1px solid ${MekanikTheme.border}`,
              backgroundColor: MekanikTheme.bg,
              color: "#FFF",
              fontWeight: 800,
              fontSize: 12,
              cursor: "pointer"
            }}
          >
            ← Quitter /admin et retourner à la Façade Publique (/)
          </button>
        </header>

        <SuperAdminPortalView
          allAccounts={savedAccounts}
          rolesPricing={rolesPricing}
          mecaVerifPrices={mecaVerifPrices}
          featureFlags={featureFlags}
          onUpdateRolePricing={(roleId, monthlyPriceDzd, setupFeeDzd, maxSubAccounts) => {
            const next = {
              ...rolesPricing,
              [roleId]: {
                ...rolesPricing[roleId],
                monthlyPriceDzd,
                setupFeeDzd,
                maxSubAccounts
              }
            };
            setRolesPricing(next);
            try {
              localStorage.setItem("mekanik_superadmin_roles_pricing_v1", JSON.stringify(next));
            } catch (_e) {}
          }}
          onUpdateMecaVerifPrices={(proPriceDzd, partPriceDzd) => {
            const next = { proPriceDzd, partPriceDzd };
            setMecaVerifPrices(next);
            try {
              localStorage.setItem("mekanik_superadmin_mecaverif_prices_v1", JSON.stringify(next));
            } catch (_e) {}
          }}
          onUpdateAccountStatusOrRole={(userId, nextStatus, nextRole) => {
            const updatedList = savedAccounts.map((acc) =>
              acc.userId === userId
                ? {
                    ...acc,
                    approvalStatus: nextStatus,
                    role: nextRole || acc.role
                  }
                : acc
            );
            setSavedAccounts(updatedList);
            if (currentAccount.userId === userId) {
              const updatedCurrent = updatedList.find((a) => a.userId === userId);
              if (updatedCurrent) setCurrentAccount(updatedCurrent);
            }
          }}
          onToggleFeatureFlag={(key) => {
            setFeatureFlags({ ...featureFlags, [key]: !featureFlags[key] });
            showToast(`Module ${key} basculé avec succès !`);
          }}
          onShowToast={showToast}
        />
      </div>
    );
  }

  return (
    <div
      style={{
        display: "flex",
        minHeight: "100vh",
        backgroundColor: MekanikTheme.bg,
        color: MekanikTheme.textPrimary,
        flexWrap: "wrap"
      }}
    >
      {/* =================================================================== */}
      {/* BARRE LATÉRALE GAUCHE SAAS (FILTRÉE STRICTEMENT SELON LE COMPTE)    */}
      {/* =================================================================== */}
      <aside
        style={{
          width: 290,
          backgroundColor: MekanikTheme.surface,
          borderRight: `1px solid ${MekanikTheme.border}`,
          padding: 20,
          display: "flex",
          flexDirection: "column",
          justifyContent: "space-between"
        }}
      >
        <div>
          {/* Logo MEKANIK+ */}
          <div style={{ display: "flex", alignItems: "center", gap: 10, marginBottom: 10 }}>
            <div
              style={{
                width: 42,
                height: 42,
                borderRadius: 10,
                backgroundColor: "rgba(240,120,54,0.18)",
                border: `1.5px solid ${MekanikTheme.orange}`,
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                fontWeight: 900,
                fontSize: 18,
                color: MekanikTheme.orange
              }}
            >
              ⚙️+
            </div>
            <div>
              <div style={{ fontSize: 20, fontWeight: 900, letterSpacing: 0.5 }}>
                MEKANIK<span style={{ color: MekanikTheme.orange }}>+</span>
              </div>
              <div style={{ fontSize: 10, color: MekanikTheme.textSecondary }}>
                PLATEFORME SAAS AUTOMOBILE PRO
              </div>
            </div>
          </div>

          {/* Carte du Compte Actif (si connecté) ou Accès Visiteur Public */}
          <div
            style={{
              padding: 12,
              borderRadius: 12,
              backgroundColor: MekanikTheme.bg,
              border: `1.5px solid ${isClientLoggedIn ? roleMeta.accentColor : MekanikTheme.orange}`,
              marginBottom: 16
            }}
          >
            {isClientLoggedIn ? (
              <>
                <div
                  style={{
                    display: "inline-block",
                    padding: "3px 8px",
                    borderRadius: 5,
                    backgroundColor: roleMeta.accentColor,
                    color: "#FFF",
                    fontWeight: 900,
                    fontSize: 10,
                    marginBottom: 6
                  }}
                >
                  {roleMeta.shortBadge}
                </div>
                <div style={{ fontWeight: 800, fontSize: 13 }}>{currentAccount.organizationName}</div>
                <div style={{ fontSize: 11, color: MekanikTheme.textSecondary, marginTop: 2 }}>
                  👤 {currentAccount.fullName} • {currentAccount.wilaya}
                </div>
                <div style={{ fontSize: 10.5, color: MekanikTheme.green, fontWeight: 700, marginTop: 4 }}>
                  ● Isolé : {currentAccount.organizationId} • 2FA Actif
                </div>
              </>
            ) : (
              <>
                <div
                  style={{
                    display: "inline-block",
                    padding: "3px 8px",
                    borderRadius: 5,
                    backgroundColor: MekanikTheme.orange,
                    color: "#FFF",
                    fontWeight: 900,
                    fontSize: 10,
                    marginBottom: 6
                  }}
                >
                  PORTAIL PUBLIC MEKANIK+
                </div>
                <div style={{ fontWeight: 800, fontSize: 13 }}>Accès Client & Pro Isolé</div>
                <div style={{ fontSize: 11, color: MekanikTheme.textSecondary, marginTop: 3 }}>
                  Découvrez la plateforme, la vidéo tuto et les abonnements Mensuel / Annuel.
                </div>
              </>
            )}

            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 6, marginTop: 10 }}>
              <button
                onClick={() => {
                  setAuthModalInitialTab("LOGIN");
                  setAuthModalOpen(true);
                }}
                style={{
                  padding: "7px 8px",
                  borderRadius: 7,
                  border: `1px solid ${MekanikTheme.green}`,
                  backgroundColor: "rgba(34,197,94,0.14)",
                  color: "#FFF",
                  fontWeight: 800,
                  fontSize: 11,
                  cursor: "pointer"
                }}
              >
                🔑 Connexion
              </button>
              <button
                onClick={() => {
                  setAuthModalInitialTab("REGISTER");
                  setAuthModalOpen(true);
                }}
                style={{
                  padding: "7px 8px",
                  borderRadius: 7,
                  border: "none",
                  backgroundColor: MekanikTheme.orange,
                  color: "#FFF",
                  fontWeight: 800,
                  fontSize: 11,
                  cursor: "pointer"
                }}
              >
                ➕ S'inscrire
              </button>
            </div>
          </div>

          {/* Menu strictement filtré : sur la Façade Publique non connectée, on affiche uniquement l'accès Façade, Tarifs et Connexion */}
          <div style={{ fontSize: 10.5, fontWeight: 800, color: MekanikTheme.textSecondary, marginBottom: 8 }}>
            {isClientLoggedIn
              ? `OPTIONS EXCLUSIVES DU COMPTE (${roleMeta.shortBadge}) :`
              : "FAÇADE OFFICIELLE & ACCÈS CLIENT :"}
          </div>
          <nav style={{ display: "flex", flexDirection: "column", gap: 7 }}>
            <button
              onClick={() => setActiveTab("SITE_PUBLIC")}
              style={{
                textAlign: "left",
                padding: "10px 12px",
                borderRadius: 10,
                border: `1px solid ${activeTab === "SITE_PUBLIC" ? MekanikTheme.orange : MekanikTheme.border}`,
                backgroundColor: activeTab === "SITE_PUBLIC" ? "rgba(240, 120, 54, 0.18)" : MekanikTheme.bg,
                color: MekanikTheme.textPrimary,
                cursor: "pointer"
              }}
            >
              <div style={{ fontWeight: 800, fontSize: 13 }}>
                🌐 Façade Officielle & Vidéo Tuto
              </div>
              <div style={{ fontSize: 10.5, color: MekanikTheme.textSecondary, marginTop: 2 }}>
                Logo, présentation, tarifs Mois/An & guide
              </div>
            </button>

            {isClientLoggedIn ? (
              allowedNavItems
                .filter((item) => item.id !== "SITE_PUBLIC")
                .map((item) => {
                  const selected = activeTab === item.id;
                  return (
                    <button
                      key={item.id}
                      onClick={() => setActiveTab(item.id)}
                      style={{
                        textAlign: "left",
                        padding: "10px 12px",
                        borderRadius: 10,
                        border: `1px solid ${selected ? MekanikTheme.orange : MekanikTheme.border}`,
                        backgroundColor: selected ? "rgba(240, 120, 54, 0.18)" : MekanikTheme.bg,
                        color: MekanikTheme.textPrimary,
                        cursor: "pointer"
                      }}
                    >
                      <div style={{ fontWeight: 800, fontSize: 13 }}>
                        {item.icon} {item.label}
                      </div>
                      <div style={{ fontSize: 10.5, color: MekanikTheme.textSecondary, marginTop: 2 }}>
                        {item.subtitle}
                      </div>
                    </button>
                  );
                })
            ) : (
              <div
                style={{
                  padding: 12,
                  borderRadius: 10,
                  backgroundColor: MekanikTheme.bg,
                  border: `1px dashed ${MekanikTheme.border}`,
                  fontSize: 11.5,
                  color: MekanikTheme.textSecondary,
                  lineHeight: 1.5
                }}
              >
                🔒 <strong>Espaces Professionnels Protégés</strong>
                <div style={{ marginTop: 4 }}>
                  Connectez-vous avec vos identifiants + code 2FA ou créez votre compte (Mensuel ou Annuel -20%) pour déverrouiller votre tableau de bord métier.
                </div>
              </div>
            )}
          </nav>
        </div>

        <div
          style={{
            marginTop: 16,
            padding: 12,
            borderRadius: 10,
            backgroundColor: MekanikTheme.bg,
            border: `1px solid ${MekanikTheme.border}`,
            fontSize: 11
          }}
        >
          <div style={{ color: MekanikTheme.green, fontWeight: 800 }}>
            ● GOOGLE AUTHENTICATOR + SMS 2FA
          </div>
          <div style={{ color: MekanikTheme.textSecondary, marginTop: 4 }}>
            TecDoc • ETKA • Stellantis ServiceBox • CIB SATIM / Edahabia / Stripe
          </div>
        </div>
      </aside>

      {/* =================================================================== */}
      {/* ZONE CENTRALE PRINCIPALE                                            */}
      {/* =================================================================== */}
      <main style={{ flex: 1, minWidth: 320, padding: 24 }}>
        {toastMsg && (
          <div
            style={{
              padding: "12px 16px",
              marginBottom: 16,
              borderRadius: 10,
              backgroundColor: "rgba(34, 197, 94, 0.18)",
              border: `1px solid ${MekanikTheme.green}`,
              color: MekanikTheme.textPrimary,
              fontWeight: 800,
              fontSize: 13
            }}
          >
            ✓ {toastMsg}
          </div>
        )}

        {/* BARRE SUPÉRIEURE D'ACTIONS COMPTES & AUTHENTIFICATION */}
        <header
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            flexWrap: "wrap",
            gap: 12,
            padding: 18,
            backgroundColor: MekanikTheme.surface,
            borderRadius: 14,
            border: `1px solid ${MekanikTheme.border}`,
            marginBottom: 18
          }}
        >
          <div>
            <h1 style={{ margin: 0, fontSize: 20 }}>
              MEKANIK<span style={{ color: MekanikTheme.orange }}>+</span> — « La plateforme automobile nouvelle génération »
            </h1>
            <div style={{ fontSize: 12, color: MekanikTheme.textSecondary, marginTop: 3 }}>
              Formule active :{" "}
              <strong style={{ color: roleMeta.accentColor }}>{roleMeta.title}</strong> • Tarif :{" "}
              <strong>{roleMeta.monthlyPriceDzd.toLocaleString("fr-FR")} DA / mois</strong>
            </div>
          </div>

          <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
            <button
              onClick={() => {
                setAuthModalInitialTab("REGISTER");
                setAuthModalOpen(true);
              }}
              style={{
                padding: "9px 14px",
                borderRadius: 8,
                border: "none",
                backgroundColor: MekanikTheme.orange,
                color: "#FFF",
                fontWeight: 900,
                fontSize: 12,
                cursor: "pointer"
              }}
            >
              ➕ Créer un Compte / Inscription
            </button>
            <button
              onClick={() => {
                setAuthModalInitialTab("LOGIN");
                setAuthModalOpen(true);
              }}
              style={{
                padding: "9px 14px",
                borderRadius: 8,
                border: `1px solid ${MekanikTheme.green}`,
                backgroundColor: MekanikTheme.bg,
                color: MekanikTheme.green,
                fontWeight: 800,
                fontSize: 12,
                cursor: "pointer"
              }}
            >
              🔑 Connexion 2FA
            </button>
            <button
              onClick={() => {
                setAuthModalInitialTab("SWITCH_ROLE");
                setAuthModalOpen(true);
              }}
              style={{
                padding: "9px 14px",
                borderRadius: 8,
                border: `1px solid ${MekanikTheme.orange}`,
                backgroundColor: MekanikTheme.bg,
                color: "#FFF",
                fontWeight: 800,
                fontSize: 12,
                cursor: "pointer"
              }}
            >
              🔄 Changer de Formule ({roleMeta.shortBadge})
            </button>
            {roleMeta.maxSubAccounts > 0 && (
              <button
                onClick={() => {
                  setAuthModalInitialTab("SUB_ACCOUNTS_2FA");
                  setAuthModalOpen(true);
                }}
                style={{
                  padding: "9px 14px",
                  borderRadius: 8,
                  border: `1px solid ${MekanikTheme.border}`,
                  backgroundColor: MekanikTheme.bg,
                  color: "#FFF",
                  fontWeight: 800,
                  fontSize: 12,
                  cursor: "pointer"
                }}
              >
                👥 Sous-Comptes ({currentAccount.subAccounts.length}/{roleMeta.maxSubAccounts})
              </button>
            )}
            {isClientLoggedIn && (
              <button
                onClick={() => {
                  setIsClientLoggedIn(false);
                  setActiveTab("SITE_PUBLIC");
                  showToast("Session client verrouillée. Retour sur la Façade Officielle.");
                }}
                style={{
                  padding: "9px 14px",
                  borderRadius: 8,
                  border: `1px solid ${MekanikTheme.redOem}`,
                  backgroundColor: MekanikTheme.bg,
                  color: "#FFF",
                  fontWeight: 800,
                  fontSize: 12,
                  cursor: "pointer"
                }}
              >
                🔒 Déconnexion
              </button>
            )}
          </div>
        </header>

        {/* BARRE DE RECHERCHE CENTRALE [ VIN ] [ CODE MOTEUR ] [ RECHERCHE VÉHICULE ] */}
        {featureFlags.vinDecoder !== false && (
          <section
            style={{
              padding: 20,
              backgroundColor: MekanikTheme.surface,
              borderRadius: 14,
              border: `1px solid ${MekanikTheme.orange}`,
              marginBottom: 20
            }}
          >
            <div style={{ display: "flex", gap: 8, flexWrap: "wrap", marginBottom: 12 }}>
              {(["VIN", "CODE_MOTEUR", "VEHICULE"] as const).map((m) => (
                <button
                  key={m}
                  onClick={() => setSearchMode(m)}
                  style={{
                    padding: "8px 14px",
                    borderRadius: 8,
                    border: `1px solid ${searchMode === m ? MekanikTheme.orange : MekanikTheme.border}`,
                    backgroundColor: searchMode === m ? MekanikTheme.orange : MekanikTheme.bg,
                    color: "#FFFFFF",
                    fontWeight: 900,
                    cursor: "pointer",
                    fontSize: 12
                  }}
                >
                  [ {m === "CODE_MOTEUR" ? "CODE MOTEUR" : m === "VEHICULE" ? "RECHERCHE VÉHICULE" : "VIN"} ]
                </button>
              ))}
            </div>
            <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
              <input
                value={vinQuery}
                onChange={(e) => setVinQuery(e.target.value.toUpperCase())}
                placeholder="Saisir VIN (17 caractères), Code Moteur (CRLB, DV6FC, K9K) ou Modèle..."
                style={{
                  flex: 1,
                  minWidth: 220,
                  padding: "12px 14px",
                  borderRadius: 10,
                  border: `1px solid ${MekanikTheme.border}`,
                  backgroundColor: MekanikTheme.bg,
                  color: MekanikTheme.textPrimary,
                  fontSize: 14,
                  fontFamily: "monospace",
                  fontWeight: 800
                }}
              />
              <button
                onClick={() => {
                  if (vinQuery.trim().toLowerCase() === "/admin") {
                    navigateToHiddenAdminUrl();
                    return;
                  }
                  setDecodedBanner(
                    `Identification ${searchMode} réussie (${vinQuery}) via TecDoc + ETKA + Stellantis ServiceBox.`
                  );
                  showToast(`Véhicule ${vinQuery} identifié avec succès !`);
                }}
                style={{
                  padding: "12px 22px",
                  borderRadius: 10,
                  border: "none",
                  backgroundColor: MekanikTheme.orange,
                  color: "#FFFFFF",
                  fontWeight: 900,
                  cursor: "pointer"
                }}
              >
                🔍 IDENTIFIER
              </button>
            </div>
            <div style={{ marginTop: 10, fontSize: 12, color: MekanikTheme.green, fontWeight: 700 }}>
              ● {decodedBanner}
            </div>
          </section>
        )}

        {/* ================================================================= */}
        {/* VUE 1 : TABLEAU DE BORD PERSONNALISÉ DU COMPTE ACTIF              */}
        {/* ================================================================= */}
        {activeTab === "DASHBOARD_COMPTE" && (
          <div style={{ display: "grid", gap: 16 }}>
            <div
              style={{
                padding: 20,
                backgroundColor: MekanikTheme.surface,
                borderRadius: 16,
                border: `1.5px solid ${roleMeta.accentColor}`
              }}
            >
              <div style={{ display: "flex", justifyContent: "space-between", flexWrap: "wrap", gap: 10 }}>
                <div>
                  <div style={{ fontSize: 18, fontWeight: 900 }}>
                    TABLEAU DE BORD — {currentAccount.organizationName}
                  </div>
                  <div style={{ fontSize: 12.5, color: MekanikTheme.textSecondary, marginTop: 4 }}>
                    {roleMeta.description}
                  </div>
                </div>
                <span
                  style={{
                    padding: "6px 12px",
                    borderRadius: 8,
                    backgroundColor: roleMeta.accentColor,
                    color: "#FFF",
                    fontWeight: 900,
                    fontSize: 12
                  }}
                >
                  {roleMeta.shortBadge}
                </span>
              </div>

              {/* Cartes de raccourcis strictement limitées aux modules autorisés pour ce compte */}
              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "repeat(auto-fit, minmax(210px, 1fr))",
                  gap: 12,
                  marginTop: 16
                }}
              >
                {allowedNavItems
                  .filter((i) => i.id !== "DASHBOARD_COMPTE")
                  .map((item) => (
                    <div
                      key={item.id}
                      onClick={() => setActiveTab(item.id)}
                      style={{
                        padding: 16,
                        borderRadius: 12,
                        backgroundColor: MekanikTheme.bg,
                        border: `1px solid ${MekanikTheme.border}`,
                        cursor: "pointer"
                      }}
                    >
                      <div style={{ fontSize: 22 }}>{item.icon}</div>
                      <div style={{ fontWeight: 800, fontSize: 14, marginTop: 6 }}>{item.label}</div>
                      <div style={{ fontSize: 11.5, color: MekanikTheme.textSecondary, marginTop: 3 }}>
                        {item.subtitle}
                      </div>
                      <div style={{ fontSize: 11, color: MekanikTheme.orange, fontWeight: 800, marginTop: 8 }}>
                        Ouvrir ce module →
                      </div>
                    </div>
                  ))}
              </div>
            </div>

            {/* Si Compte Mixte (1+4) ou Compte avec Sous-Comptes : Affichage direct de l'équipe */}
            {roleMeta.maxSubAccounts > 0 && (
              <div
                style={{
                  padding: 20,
                  backgroundColor: MekanikTheme.surface,
                  borderRadius: 16,
                  border: `1px solid ${MekanikTheme.border}`
                }}
              >
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 10 }}>
                  <div>
                    <div style={{ fontWeight: 900, fontSize: 15, color: MekanikTheme.orange }}>
                      👥 COMPTE PRINCIPAL + SOUS-COMPTES ({currentAccount.subAccounts.length}/{roleMeta.maxSubAccounts} ACTIFS)
                    </div>
                    <div style={{ fontSize: 12, color: MekanikTheme.textSecondary }}>
                      Protection maximale par Google Authenticator 2FA pour ajouter ou supprimer des sous-comptes.
                    </div>
                  </div>
                  <button
                    onClick={() => {
                      setAuthModalInitialTab("SUB_ACCOUNTS_2FA");
                      setAuthModalOpen(true);
                    }}
                    style={{
                      padding: "9px 14px",
                      borderRadius: 8,
                      border: "none",
                      backgroundColor: MekanikTheme.orange,
                      color: "#FFF",
                      fontWeight: 800,
                      fontSize: 12,
                      cursor: "pointer"
                    }}
                  >
                    Gérer les Sous-Comptes (2FA)
                  </button>
                </div>

                {currentAccount.subAccounts.length > 0 && (
                  <div
                    style={{
                      display: "grid",
                      gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
                      gap: 10,
                      marginTop: 12
                    }}
                  >
                    {currentAccount.subAccounts.map((sub) => (
                      <div
                        key={sub.id}
                        style={{
                          padding: 12,
                          borderRadius: 10,
                          backgroundColor: MekanikTheme.bg,
                          border: `1px solid ${MekanikTheme.border}`
                        }}
                      >
                        <div style={{ fontWeight: 800, fontSize: 13 }}>{sub.fullName}</div>
                        <div style={{ fontSize: 11.5, color: MekanikTheme.orange, fontWeight: 700 }}>
                          {sub.roleLabel}
                        </div>
                        <div style={{ fontSize: 11, color: MekanikTheme.textSecondary, marginTop: 3 }}>
                          {sub.email} • 2FA : Actif ✓
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* Aperçu direct de l'Historique des Commandes dans le Dashboard pour MAGASIN_PIECES, ATELIER_PRO et MIXTE_ATELIER_MAGASIN */}
            {(currentAccount.role === "MAGASIN_PIECES" ||
              currentAccount.role === "ATELIER_PRO" ||
              currentAccount.role === "MIXTE_ATELIER_MAGASIN") && (
              <div
                style={{
                  padding: 20,
                  backgroundColor: MekanikTheme.surface,
                  borderRadius: 16,
                  border: `1px solid ${MekanikTheme.orange}`
                }}
              >
                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    flexWrap: "wrap",
                    gap: 10,
                    marginBottom: 12
                  }}
                >
                  <div>
                    <div style={{ fontWeight: 900, fontSize: 15.5, color: MekanikTheme.orange }}>
                      📦 HISTORIQUE DES COMMANDES DE L'ORGANISATION ({currentAccount.organizationId})
                    </div>
                    <div style={{ fontSize: 12, color: MekanikTheme.textSecondary, marginTop: 2 }}>
                      Commandes passées filtrées strictement sur l'ID de votre organisation actuelle ({currentAccount.organizationId}).
                    </div>
                  </div>
                  <button
                    onClick={() => setActiveTab("HISTORIQUE_COMMANDES")}
                    style={{
                      padding: "9px 14px",
                      borderRadius: 8,
                      border: "none",
                      backgroundColor: MekanikTheme.orange,
                      color: "#FFF",
                      fontWeight: 900,
                      fontSize: 12,
                      cursor: "pointer"
                    }}
                  >
                    Ouvrir la page Historique des Commandes →
                  </button>
                </div>

                <div style={{ display: "grid", gap: 8 }}>
                  {b2bOrders
                    .filter((ord) => ord.organizationId === currentAccount.organizationId)
                    .slice(0, 3)
                    .map((ord) => (
                      <div
                        key={ord.id}
                        style={{
                          padding: 12,
                          borderRadius: 10,
                          backgroundColor: MekanikTheme.bg,
                          border: `1px solid ${MekanikTheme.border}`,
                          display: "flex",
                          justifyContent: "space-between",
                          alignItems: "center",
                          flexWrap: "wrap",
                          gap: 10
                        }}
                      >
                        <div>
                          <div style={{ fontWeight: 800, fontSize: 13 }}>
                            {ord.id} — {ord.partName} (
                            <span style={{ color: MekanikTheme.orange, fontFamily: "monospace" }}>
                              {ord.oemRef}
                            </span>
                            )
                          </div>
                          <div style={{ fontSize: 11.5, color: MekanikTheme.textSecondary, marginTop: 2 }}>
                            Date : {ord.createdAt} • Qté : {ord.qty} • Total :{" "}
                            <strong style={{ color: MekanikTheme.green }}>
                              {(ord.unitPriceDzd * ord.qty).toLocaleString("fr-FR")} DA
                            </strong>
                          </div>
                        </div>
                        <span
                          style={{
                            padding: "5px 10px",
                            borderRadius: 6,
                            backgroundColor:
                              ord.status.includes("LIVRÉE")
                                ? "rgba(34,197,94,0.2)"
                                : "rgba(240,120,54,0.2)",
                            border: `1px solid ${
                              ord.status.includes("LIVRÉE") ? MekanikTheme.green : MekanikTheme.orange
                            }`,
                            color: ord.status.includes("LIVRÉE") ? MekanikTheme.green : MekanikTheme.orange,
                            fontWeight: 800,
                            fontSize: 11
                          }}
                        >
                          {ord.status}
                        </span>
                      </div>
                    ))}
                </div>
              </div>
            )}
          </div>
        )}

        {/* ================================================================= */}
        {/* VUE 2 : ESPACE ATELIER DMS (OR, DEVIS, CLIENTS & MONTAGE)         */}
        {/* ================================================================= */}
        {activeTab === "ATELIER_DMS" && (
          <div style={{ display: "grid", gap: 16 }}>
            <div
              style={{
                padding: 20,
                backgroundColor: MekanikTheme.surface,
                borderRadius: 16,
                border: `1px solid ${MekanikTheme.orange}`
              }}
            >
              <div style={{ fontWeight: 900, fontSize: 17, color: MekanikTheme.orange, marginBottom: 12 }}>
                🛠️ ESPACE ATELIER PRO — ORDRES DE RÉPARATION (OR), DEVIS & CLIENTS ({currentAccount.organizationName})
              </div>

              {/* Création rapide d'un Ordre de Réparation / Devis */}
              <div
                style={{
                  padding: 14,
                  borderRadius: 12,
                  backgroundColor: MekanikTheme.bg,
                  border: `1px solid ${MekanikTheme.border}`,
                  marginBottom: 16,
                  display: "grid",
                  gap: 10
                }}
              >
                <div style={{ fontWeight: 800, fontSize: 13, color: MekanikTheme.green }}>
                  + CRÉER UN NOUVEL ORDRE DE RÉPARATION (OR) / DEVIS CLIENT
                </div>
                <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))", gap: 8 }}>
                  <input
                    value={newOrClient}
                    onChange={(e) => setNewOrClient(e.target.value)}
                    placeholder="Nom du client & Téléphone"
                    style={{ padding: "9px 12px", borderRadius: 8, backgroundColor: MekanikTheme.surface, color: "#FFF", border: `1px solid ${MekanikTheme.border}` }}
                  />
                  <input
                    value={newOrDesc}
                    onChange={(e) => setNewOrDesc(e.target.value)}
                    placeholder="Travaux & Pièces à monter"
                    style={{ padding: "9px 12px", borderRadius: 8, backgroundColor: MekanikTheme.surface, color: "#FFF", border: `1px solid ${MekanikTheme.border}` }}
                  />
                  <input
                    type="number"
                    value={newOrAmount}
                    onChange={(e) => setNewOrAmount(e.target.value)}
                    placeholder="Montant Devis (DA)"
                    style={{ padding: "9px 12px", borderRadius: 8, backgroundColor: MekanikTheme.surface, color: "#FFF", border: `1px solid ${MekanikTheme.border}` }}
                  />
                  <button
                    onClick={() => {
                      if (!newOrClient.trim()) return;
                      setRepairOrders([
                        {
                          id: `OR-2026-${Math.floor(410 + Math.random() * 500)}`,
                          clientName: newOrClient.trim(),
                          phone: "+213 550 00 11 22",
                          vehicle: `Châssis ${vinQuery}`,
                          description: newOrDesc.trim() || "Entretien & Diagnostic",
                          amountDzd: Number(newOrAmount) || 25000,
                          status: "NOUVEL OR VALIDÉ"
                        },
                        ...repairOrders
                      ]);
                      setNewOrClient("");
                      setNewOrDesc("");
                      showToast("Ordre de Réparation (OR) / Devis créé avec succès !");
                    }}
                    style={{
                      padding: "9px 14px",
                      borderRadius: 8,
                      border: "none",
                      backgroundColor: MekanikTheme.orange,
                      color: "#FFF",
                      fontWeight: 900,
                      cursor: "pointer"
                    }}
                  >
                    + Enregistrer OR / Devis
                  </button>
                </div>
              </div>

              <div style={{ display: "grid", gap: 10 }}>
                {repairOrders.map((or) => (
                  <div
                    key={or.id}
                    style={{
                      padding: 14,
                      borderRadius: 12,
                      backgroundColor: MekanikTheme.bg,
                      border: `1px solid ${MekanikTheme.border}`,
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
                      flexWrap: "wrap",
                      gap: 10
                    }}
                  >
                    <div>
                      <div style={{ fontWeight: 800, fontSize: 14 }}>
                        {or.id} — {or.clientName} •{" "}
                        <span style={{ color: MekanikTheme.orange }}>{or.vehicle}</span>
                      </div>
                      <div style={{ fontSize: 12, color: MekanikTheme.textSecondary, marginTop: 3 }}>
                        {or.description}
                      </div>
                    </div>
                    <div style={{ textAlign: "right" }}>
                      <div style={{ fontWeight: 900, fontSize: 15, color: MekanikTheme.green }}>
                        {or.amountDzd.toLocaleString("fr-FR")} DA
                      </div>
                      <span
                        style={{
                          fontSize: 10.5,
                          color: MekanikTheme.orange,
                          fontWeight: 800
                        }}
                      >
                        {or.status}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* ================================================================= */}
        {/* VUE 3 : ESPACE MAGASIN & CATALOGUE PIÈCES OEM                     */}
        {/* ================================================================= */}
        {activeTab === "MAGASIN_OEM" && (
          <div style={{ display: "grid", gap: 14 }}>
            <div
              style={{
                padding: 16,
                backgroundColor: MekanikTheme.surface,
                borderRadius: 14,
                border: `1.5px solid ${MekanikTheme.orange}`,
                fontWeight: 800,
                fontSize: 13
              }}
            >
              ARBORESCENCE VISUELLE : Véhicule sélectionné ({vinQuery}) ↓ Système ↓ Pièce ↓{" "}
              <span style={{ color: MekanikTheme.redOem }}>Référence constructeur OEM</span> ↓ Équivalences ↓ Vendeurs & Fournisseurs B2B disponibles
            </div>

            {partsCatalog.map((item) => (
              <div
                key={item.id}
                style={{
                  padding: 18,
                  backgroundColor: MekanikTheme.surface,
                  borderRadius: 14,
                  border: `1px solid ${MekanikTheme.border}`,
                  display: "flex",
                  justifyContent: "space-between",
                  flexWrap: "wrap",
                  gap: 14
                }}
              >
                <div style={{ flex: 1, minWidth: 250 }}>
                  <div style={{ fontSize: 11.5, color: MekanikTheme.textSecondary }}>
                    Système : {item.system}
                  </div>
                  <div style={{ fontWeight: 900, fontSize: 16, marginTop: 2 }}>{item.partName}</div>

                  {/* Référence principale mise en évidence en rouge/orange */}
                  <div
                    style={{
                      display: "inline-block",
                      marginTop: 8,
                      padding: "5px 12px",
                      borderRadius: 7,
                      backgroundColor: "rgba(229, 57, 53, 0.22)",
                      border: `1.5px solid ${MekanikTheme.redOem}`,
                      color: "#FFF",
                      fontWeight: 900,
                      fontFamily: "monospace",
                      fontSize: 13.5
                    }}
                  >
                    RÉFÉRENCE CONSTRUCTEUR OEM : {item.oemRef}
                  </div>

                  <div style={{ fontSize: 12, color: MekanikTheme.textSecondary, marginTop: 8 }}>
                    <strong>Équivalences croisées :</strong> {item.equivRefs.join(" • ")}
                  </div>

                  {/* Compatibilités sous forme de badges */}
                  <div style={{ display: "flex", gap: 6, flexWrap: "wrap", marginTop: 8 }}>
                    {item.compat.map((c) => (
                      <span
                        key={c}
                        style={{
                          padding: "3px 9px",
                          borderRadius: 6,
                          backgroundColor: MekanikTheme.bg,
                          border: `1px solid ${MekanikTheme.green}`,
                          color: MekanikTheme.green,
                          fontSize: 11,
                          fontWeight: 800
                        }}
                      >
                        ✓ {c}
                      </span>
                    ))}
                  </div>
                </div>

                <div style={{ textAlign: "right", minWidth: 200 }}>
                  <div style={{ fontSize: 12, color: MekanikTheme.textSecondary }}>
                    Prix Public : {item.publicPriceDzd.toLocaleString("fr-FR")} DA
                  </div>
                  <div style={{ fontSize: 19, fontWeight: 900, color: MekanikTheme.orange, marginTop: 2 }}>
                    Tarif Pro B2B : {item.b2bPriceDzd.toLocaleString("fr-FR")} DA
                  </div>
                  <div style={{ fontSize: 11.5, color: MekanikTheme.green, fontWeight: 700, marginTop: 4 }}>
                    ● En stock ({item.stockQty} unités) — {item.warehouse}
                  </div>
                  <button
                    onClick={() => handleOrderPartB2b(item.oemRef, item.partName, item.b2bPriceDzd)}
                    style={{
                      marginTop: 10,
                      padding: "9px 16px",
                      borderRadius: 8,
                      border: "none",
                      backgroundColor: MekanikTheme.orange,
                      color: "#FFF",
                      fontWeight: 900,
                      fontSize: 12,
                      cursor: "pointer"
                    }}
                  >
                    Commander au Fournisseur B2B
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* ================================================================= */}
        {/* VUE 3B : PAGE HISTORIQUE DES COMMANDES (FILTRÉE PAR ORGANISATION) */}
        {/* ================================================================= */}
        {activeTab === "HISTORIQUE_COMMANDES" && (() => {
          const orgFilteredOrders = b2bOrders.filter(
            (ord) => ord.organizationId === currentAccount.organizationId
          );
          const displayedOrders =
            orderHistoryStatusFilter === "TOUS"
              ? orgFilteredOrders
              : orgFilteredOrders.filter((ord) => ord.status === orderHistoryStatusFilter);
          const totalAmountDzd = orgFilteredOrders.reduce(
            (acc, ord) => acc + ord.unitPriceDzd * ord.qty,
            0
          );
          const deliveredCount = orgFilteredOrders.filter((ord) =>
            ord.status.includes("LIVRÉE")
          ).length;
          const pendingCount = orgFilteredOrders.length - deliveredCount;

          return (
            <div style={{ display: "grid", gap: 16 }}>
              <div
                style={{
                  padding: 20,
                  backgroundColor: MekanikTheme.surface,
                  borderRadius: 16,
                  border: `1.5px solid ${MekanikTheme.orange}`
                }}
              >
                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    flexWrap: "wrap",
                    gap: 12,
                    marginBottom: 16
                  }}
                >
                  <div>
                    <div style={{ fontWeight: 900, fontSize: 18, color: MekanikTheme.orange }}>
                      📦 HISTORIQUE DES COMMANDES — {currentAccount.organizationName}
                    </div>
                    <div style={{ fontSize: 12.5, color: MekanikTheme.textSecondary, marginTop: 4 }}>
                      Liste des commandes B2B passées avec leur statut en temps réel, filtrée strictement sur l'ID de votre organisation :{" "}
                      <strong style={{ color: MekanikTheme.green, fontFamily: "monospace" }}>
                        {currentAccount.organizationId}
                      </strong>{" "}
                      ({currentAccount.role})
                    </div>
                  </div>
                  <button
                    onClick={() =>
                      handleOrderPartB2b(
                        partsCatalog[0].oemRef,
                        partsCatalog[0].partName,
                        partsCatalog[0].b2bPriceDzd
                      )
                    }
                    style={{
                      padding: "10px 16px",
                      borderRadius: 9,
                      border: "none",
                      backgroundColor: MekanikTheme.orange,
                      color: "#FFF",
                      fontWeight: 900,
                      fontSize: 12,
                      cursor: "pointer"
                    }}
                  >
                    + Passer une Nouvelle Commande B2B ({partsCatalog[0].oemRef})
                  </button>
                </div>

                {/* KPIs des commandes de l'organisation active */}
                <div
                  style={{
                    display: "grid",
                    gridTemplateColumns: "repeat(auto-fit, minmax(190px, 1fr))",
                    gap: 12,
                    marginBottom: 16
                  }}
                >
                  <div
                    style={{
                      padding: 14,
                      borderRadius: 12,
                      backgroundColor: MekanikTheme.bg,
                      border: `1px solid ${MekanikTheme.border}`
                    }}
                  >
                    <div style={{ fontSize: 11, color: MekanikTheme.textSecondary, fontWeight: 800 }}>
                      ID ORGANISATION FILTRÉ
                    </div>
                    <div
                      style={{
                        fontSize: 16,
                        fontWeight: 900,
                        color: MekanikTheme.orange,
                        fontFamily: "monospace",
                        marginTop: 4
                      }}
                    >
                      {currentAccount.organizationId}
                    </div>
                  </div>
                  <div
                    style={{
                      padding: 14,
                      borderRadius: 12,
                      backgroundColor: MekanikTheme.bg,
                      border: `1px solid ${MekanikTheme.border}`
                    }}
                  >
                    <div style={{ fontSize: 11, color: MekanikTheme.textSecondary, fontWeight: 800 }}>
                      COMMANDES PASSÉES
                    </div>
                    <div style={{ fontSize: 20, fontWeight: 900, color: "#FFF", marginTop: 4 }}>
                      {orgFilteredOrders.length} commande(s)
                    </div>
                  </div>
                  <div
                    style={{
                      padding: 14,
                      borderRadius: 12,
                      backgroundColor: MekanikTheme.bg,
                      border: `1px solid ${MekanikTheme.border}`
                    }}
                  >
                    <div style={{ fontSize: 11, color: MekanikTheme.textSecondary, fontWeight: 800 }}>
                      EN COURS / LIVRÉES
                    </div>
                    <div style={{ fontSize: 16, fontWeight: 900, color: MekanikTheme.green, marginTop: 4 }}>
                      {pendingCount} en cours • {deliveredCount} livrée(s)
                    </div>
                  </div>
                  <div
                    style={{
                      padding: 14,
                      borderRadius: 12,
                      backgroundColor: MekanikTheme.bg,
                      border: `1px solid ${MekanikTheme.border}`
                    }}
                  >
                    <div style={{ fontSize: 11, color: MekanikTheme.textSecondary, fontWeight: 800 }}>
                      TOTAL COMMANDÉ (HT)
                    </div>
                    <div style={{ fontSize: 18, fontWeight: 900, color: MekanikTheme.green, marginTop: 4 }}>
                      {totalAmountDzd.toLocaleString("fr-FR")} DA
                    </div>
                  </div>
                </div>

                {/* Filtres par statut */}
                <div style={{ display: "flex", gap: 8, flexWrap: "wrap", marginBottom: 14 }}>
                  {[
                    "TOUS",
                    "NOUVELLE DEMANDE",
                    "EN PRÉPARATION",
                    "EXPÉDIÉE AU GARAGE",
                    "LIVRÉE ✓"
                  ].map((st) => (
                    <button
                      key={st}
                      onClick={() => setOrderHistoryStatusFilter(st)}
                      style={{
                        padding: "7px 12px",
                        borderRadius: 8,
                        border: `1px solid ${
                          orderHistoryStatusFilter === st ? MekanikTheme.orange : MekanikTheme.border
                        }`,
                        backgroundColor:
                          orderHistoryStatusFilter === st ? MekanikTheme.orange : MekanikTheme.bg,
                        color: "#FFF",
                        fontWeight: 800,
                        fontSize: 11.5,
                        cursor: "pointer"
                      }}
                    >
                      {st}
                    </button>
                  ))}
                </div>

                {/* Liste des commandes filtrées pour l'organisation actuelle */}
                {displayedOrders.length === 0 ? (
                  <div
                    style={{
                      padding: 24,
                      borderRadius: 12,
                      backgroundColor: MekanikTheme.bg,
                      border: `1px solid ${MekanikTheme.border}`,
                      textAlign: "center",
                      color: MekanikTheme.textSecondary
                    }}
                  >
                    Aucune commande trouvée pour l'organisation{" "}
                    <strong>{currentAccount.organizationId}</strong> avec le filtre «{" "}
                    {orderHistoryStatusFilter} ».
                  </div>
                ) : (
                  <div style={{ display: "grid", gap: 10 }}>
                    {displayedOrders.map((ord) => {
                      const isDelivered = ord.status.includes("LIVRÉE");
                      const isShipped = ord.status.includes("EXPÉDIÉE");
                      const badgeColor = isDelivered
                        ? MekanikTheme.green
                        : isShipped
                        ? "#38BDF8"
                        : MekanikTheme.orange;

                      return (
                        <div
                          key={ord.id}
                          style={{
                            padding: 16,
                            borderRadius: 12,
                            backgroundColor: MekanikTheme.bg,
                            border: `1px solid ${MekanikTheme.border}`,
                            display: "flex",
                            justifyContent: "space-between",
                            alignItems: "center",
                            flexWrap: "wrap",
                            gap: 12
                          }}
                        >
                          <div style={{ flex: 1, minWidth: 240 }}>
                            <div style={{ display: "flex", alignItems: "center", gap: 8, flexWrap: "wrap" }}>
                              <span style={{ fontWeight: 900, fontSize: 14, color: "#FFF" }}>
                                {ord.id}
                              </span>
                              <span
                                style={{
                                  padding: "3px 8px",
                                  borderRadius: 6,
                                  backgroundColor: "rgba(229, 57, 53, 0.2)",
                                  border: `1px solid ${MekanikTheme.redOem}`,
                                  color: "#FFF",
                                  fontFamily: "monospace",
                                  fontWeight: 900,
                                  fontSize: 11.5
                                }}
                              >
                                OEM : {ord.oemRef}
                              </span>
                              <span
                                style={{
                                  padding: "3px 8px",
                                  borderRadius: 6,
                                  backgroundColor: MekanikTheme.surface,
                                  border: `1px solid ${MekanikTheme.border}`,
                                  color: MekanikTheme.textSecondary,
                                  fontFamily: "monospace",
                                  fontSize: 10.5
                                }}
                              >
                                Org : {ord.organizationId}
                              </span>
                            </div>

                            <div style={{ fontWeight: 800, fontSize: 14.5, marginTop: 6 }}>
                              {ord.partName}
                            </div>
                            <div style={{ fontSize: 12, color: MekanikTheme.textSecondary, marginTop: 3 }}>
                              Passée le : {ord.createdAt} • Demandeur : {ord.requesterOrg}
                            </div>
                          </div>

                          <div style={{ textAlign: "right", minWidth: 190 }}>
                            <div style={{ fontSize: 12, color: MekanikTheme.textSecondary }}>
                              Quantité : <strong>{ord.qty}</strong> ×{" "}
                              {ord.unitPriceDzd.toLocaleString("fr-FR")} DA
                            </div>
                            <div
                              style={{
                                fontSize: 17,
                                fontWeight: 900,
                                color: MekanikTheme.green,
                                marginTop: 2,
                                marginBottom: 6
                              }}
                            >
                              Total : {(ord.unitPriceDzd * ord.qty).toLocaleString("fr-FR")} DA
                            </div>
                            <span
                              style={{
                                display: "inline-block",
                                padding: "5px 11px",
                                borderRadius: 7,
                                backgroundColor: "rgba(240,120,54,0.14)",
                                border: `1.5px solid ${badgeColor}`,
                                color: badgeColor,
                                fontWeight: 900,
                                fontSize: 11
                              }}
                            >
                              ● {ord.status}
                            </span>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>
            </div>
          );
        })()}

        {/* ================================================================= */}
        {/* VUE 4 : MÉCAVERIF+ & ESPACE SPÉCIAL CARROSSERIE / TÔLERIE         */}
        {/* ================================================================= */}
        {(activeTab === "MECAVERIF_AUDIT" || activeTab === "CARROSSERIE_TOLERIE") && (
          <MecaVerifAndBodyshopModule
            initialVin={vinQuery}
            isProAccount={currentAccount.role !== "PARTICULIER"}
            isBodyshopAccount={currentAccount.role === "CARROSSERIE_TOLERIE"}
            onOrderPartB2b={handleOrderPartB2b}
            onShowToast={showToast}
          />
        )}

        {/* ================================================================= */}
        {/* VUE 5 : ASSISTANT DIAGNOSTIC MEKANIKAI OBD-II                     */}
        {/* ================================================================= */}
        {activeTab === "MEKANIK_AI_OBD" && (
          <MekanikAiDiagnosticAssistant
            activeVin={vinQuery}
            onOrderPartB2b={handleOrderPartB2b}
          />
        )}

        {/* ================================================================= */}
        {/* VUE 6 : ESPACE FOURNISSEUR / GROSSISTE B2B                        */}
        {/* ================================================================= */}
        {activeTab === "FOURNISSEUR_B2B" && (
          <div style={{ display: "grid", gap: 16 }}>
            <div
              style={{
                padding: 20,
                backgroundColor: MekanikTheme.surface,
                borderRadius: 16,
                border: `1.5px solid ${MekanikTheme.orange}`
              }}
            >
              <div style={{ fontWeight: 900, fontSize: 17, color: MekanikTheme.orange, marginBottom: 10 }}>
                📦 PORTAIL FOURNISSEUR & GROSSISTE B2B — GESTION DU CATALOGUE, DES PRIX & DES DEMANDES
              </div>

              {/* Publier une nouvelle référence dans le catalogue B2B */}
              <div
                style={{
                  padding: 14,
                  borderRadius: 12,
                  backgroundColor: MekanikTheme.bg,
                  border: `1px solid ${MekanikTheme.green}`,
                  marginBottom: 16,
                  display: "grid",
                  gap: 8
                }}
              >
                <div style={{ fontWeight: 800, fontSize: 12.5, color: MekanikTheme.green }}>
                  + PUBLIER UNE NOUVELLE RÉFÉRENCE AU CATALOGUE GROSSISTE B2B
                </div>
                <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(190px, 1fr))", gap: 8 }}>
                  <input
                    value={newSupPartName}
                    onChange={(e) => setNewSupPartName(e.target.value)}
                    placeholder="Désignation de la pièce"
                    style={{ padding: "9px 12px", borderRadius: 8, backgroundColor: MekanikTheme.surface, color: "#FFF", border: `1px solid ${MekanikTheme.border}` }}
                  />
                  <input
                    value={newSupOem}
                    onChange={(e) => setNewSupOem(e.target.value.toUpperCase())}
                    placeholder="Référence Constructeur OEM"
                    style={{ padding: "9px 12px", borderRadius: 8, backgroundColor: MekanikTheme.surface, color: "#FFF", border: `1px solid ${MekanikTheme.border}` }}
                  />
                  <input
                    type="number"
                    value={newSupPrice}
                    onChange={(e) => setNewSupPrice(e.target.value)}
                    placeholder="Prix B2B HT (DA)"
                    style={{ padding: "9px 12px", borderRadius: 8, backgroundColor: MekanikTheme.surface, color: "#FFF", border: `1px solid ${MekanikTheme.border}` }}
                  />
                  <button
                    onClick={() => {
                      if (!newSupPartName.trim() || !newSupOem.trim()) return;
                      const b2bP = Number(newSupPrice) || 18500;
                      setPartsCatalog([
                        {
                          id: `PART-${Date.now().toString().slice(-3)}`,
                          partName: newSupPartName.trim(),
                          system: "Catalogue Grossiste B2B",
                          oemRef: newSupOem.trim(),
                          equivRefs: ["BOSCH OEM", "VALEO PRO"],
                          compat: ["Multimarque VAG / Stellantis / Renault"],
                          publicPriceDzd: Math.round(b2bP * 1.25),
                          b2bPriceDzd: b2bP,
                          stockQty: 30,
                          warehouse: "Dépôt Central Alger"
                        },
                        ...partsCatalog
                      ]);
                      setNewSupPartName("");
                      setNewSupOem("");
                      showToast("Référence publiée dans le catalogue B2B pour tous les ateliers et magasins !");
                    }}
                    style={{
                      padding: "9px 14px",
                      borderRadius: 8,
                      border: "none",
                      backgroundColor: MekanikTheme.green,
                      color: "#FFF",
                      fontWeight: 900,
                      cursor: "pointer"
                    }}
                  >
                    + Publier au Catalogue
                  </button>
                </div>
              </div>

              <div style={{ fontWeight: 800, fontSize: 14, marginBottom: 10 }}>
                COMMANDES B2B REÇUES EN TEMPS RÉEL DES ATELIERS & MAGASINS :
              </div>
              <div style={{ display: "grid", gap: 10 }}>
                {b2bOrders.map((ord, idx) => (
                  <div
                    key={ord.id}
                    style={{
                      padding: 14,
                      borderRadius: 12,
                      backgroundColor: MekanikTheme.bg,
                      border: `1px solid ${MekanikTheme.border}`,
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
                      flexWrap: "wrap",
                      gap: 10
                    }}
                  >
                    <div>
                      <div style={{ fontWeight: 800, fontSize: 13.5 }}>
                        {ord.id} — {ord.partName} (
                        <span style={{ color: MekanikTheme.orange }}>{ord.oemRef}</span>)
                      </div>
                      <div style={{ fontSize: 12, color: MekanikTheme.textSecondary }}>
                        Client Pro : {ord.requesterOrg} • Quantité : {ord.qty} • Total :{" "}
                        {(ord.unitPriceDzd * ord.qty).toLocaleString("fr-FR")} DA
                      </div>
                    </div>
                    <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                      <span
                        style={{
                          padding: "5px 10px",
                          borderRadius: 6,
                          backgroundColor: "rgba(34,197,94,0.16)",
                          color: MekanikTheme.green,
                          fontWeight: 800,
                          fontSize: 11
                        }}
                      >
                        {ord.status}
                      </span>
                      <button
                        onClick={() => {
                          const next = [...b2bOrders];
                          const nextStatus =
                            ord.status === "NOUVELLE DEMANDE"
                              ? "EN PRÉPARATION"
                              : ord.status === "EN PRÉPARATION"
                              ? "EXPÉDIÉE AU GARAGE"
                              : "LIVRÉE ✓";
                          next[idx] = { ...ord, status: nextStatus };
                          setB2bOrders(next);
                          showToast(`Statut commande ${ord.id} mis à jour : ${nextStatus}`);
                        }}
                        style={{
                          padding: "7px 12px",
                          borderRadius: 8,
                          border: "none",
                          backgroundColor: MekanikTheme.orange,
                          color: "#FFF",
                          fontWeight: 800,
                          fontSize: 11,
                          cursor: "pointer"
                        }}
                      >
                        Avancer Statut →
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* ================================================================= */}
        {/* VUE 7 : FAÇADE OFFICIELLE MEKANIK+ (LOGO ANIMÉ, PRÉSENTATION,     */}
        {/*         ABONNEMENT MENSUEL / ANNUEL -20%, INSCRIPTION & VIDÉO)    */}
        {/* ================================================================= */}
        {activeTab === "SITE_PUBLIC" && (
          <PublicFacadeAndVideoTutorial
            rolesPricing={rolesPricing}
            mecaVerifPrices={mecaVerifPrices}
            onStartRegistrationWithPlan={(roleId, billingCycle) => {
              setPreselectedRegisterRole(roleId);
              setSelectedBillingCycle(billingCycle);
              setAuthModalInitialTab("REGISTER");
              setAuthModalOpen(true);
            }}
            onOpenLoginModal={() => {
              setAuthModalInitialTab("LOGIN");
              setAuthModalOpen(true);
            }}
          />
        )}
      </main>

      {/* MODALE COMPLÈTE D'INSCRIPTION, CONNEXION, ACCOUNT SELECTOR & SOUS-COMPTES 2FA */}
      {authModalOpen && (
        <AuthAndAccountModal
          currentAccount={currentAccount}
          savedAccounts={savedAccounts}
          initialTab={authModalInitialTab}
          initialRole={preselectedRegisterRole}
          initialBillingCycle={selectedBillingCycle}
          onSelectOrRegisterAccount={handleSwitchOrRegisterAccount}
          onUpdateSubAccounts={(updatedSubs: SubAccountMember[]) => {
            const updatedAcc: UserAccountSession = {
              ...currentAccount,
              subAccounts: updatedSubs
            };
            handleSwitchOrRegisterAccount(updatedAcc);
          }}
          onClose={() => setAuthModalOpen(false)}
        />
      )}
    </div>
  );
};

export default App;
