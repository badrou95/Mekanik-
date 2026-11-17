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
        if (Array.isArray(parsed) && parsed.length > 0) return parsed;
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
    return savedAccounts[0];
  });

  useEffect(() => {
    try {
      localStorage.setItem("mekanik_saas_accounts_v2", JSON.stringify(savedAccounts));
      localStorage.setItem("mekanik_saas_active_user_id", currentAccount.userId);
    } catch (_e) {}
  }, [savedAccounts, currentAccount]);

  const [activeTab, setActiveTab] = useState<ActiveWorkspaceTab>("DASHBOARD_COMPTE");
  const [authModalOpen, setAuthModalOpen] = useState(false);
  const [authModalInitialTab, setAuthModalInitialTab] = useState<
    "SWITCH_ROLE" | "REGISTER" | "LOGIN" | "SUB_ACCOUNTS_2FA"
  >("SWITCH_ROLE");

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
      if (saved) return JSON.parse(saved);
    } catch (_e) {}
    return ACCOUNT_ROLES;
  });

  const [mecaVerifPrices, setMecaVerifPrices] = useState<{
    proPriceDzd: number;
    partPriceDzd: number;
  }>(() => {
    try {
      const saved = localStorage.getItem("mekanik_superadmin_mecaverif_prices_v1");
      if (saved) return JSON.parse(saved);
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

  // Commandes B2B vers l'Espace Fournisseur
  const [b2bOrders, setB2bOrders] = useState([
    {
      id: "CMD-B2B-2026-801",
      requesterOrg: "SARL Garage & Pièces El-Djazair Auto+ (Mixte 1+4)",
      oemRef: "04L 198 119 A",
      partName: "Kit de Distribution Complet + Pompe à Eau",
      qty: 3,
      unitPriceDzd: 26800,
      status: "NOUVELLE DEMANDE"
    },
    {
      id: "CMD-B2B-2026-802",
      requesterOrg: "Atelier Spécial Carrosserie & Tôlerie Laser Blida",
      oemRef: "5G1 941 081 F",
      partName: "Optique Phare Avant Full LED + Support Tôlerie",
      qty: 1,
      unitPriceDzd: 61000,
      status: "EN PRÉPARATION"
    }
  ]);

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
  const roleMeta = rolesPricing[currentAccount.role] || ACCOUNT_ROLES[currentAccount.role];

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
        // Compte Atelier Pro : Uniquement les outils de l'atelier mécanique
        return [
          { id: "DASHBOARD_COMPTE", icon: "🛠️", label: "Tableau de Bord Atelier", subtitle: "KPIs, sous-comptes & activité" },
          { id: "ATELIER_DMS", icon: "🧾", label: "DMS Atelier (OR & Devis)", subtitle: "Clients, ordres de réparation & VIN" },
          { id: "MEKANIK_AI_OBD", icon: "🧠", label: "MekanikAI Diagnostic OBD", subtitle: "Codes défauts, multimètre & schémas" },
          { id: "MECAVERIF_AUDIT", icon: "🛡️", label: "MécaVerif+ Pro (250 DA)", subtitle: "Audit kilométrage & historique" },
          { id: "SITE_PUBLIC", icon: "🌐", label: "Portail Public SaaS", subtitle: "Présentation & formules" }
        ];
      case "MAGASIN_PIECES":
        // Compte Magasin de Pièces : Uniquement les outils du vendeur de pièces
        return [
          { id: "DASHBOARD_COMPTE", icon: "🏪", label: "Tableau de Bord Magasin", subtitle: "Ventes comptoir, stock & alertes" },
          { id: "MAGASIN_OEM", icon: "⚙️", label: "Catalogue Pièces & Stock", subtitle: "Réf OEM rouge/orange & équivalences" },
          { id: "MECAVERIF_AUDIT", icon: "🛡️", label: "MécaVerif+ Pro (250 DA)", subtitle: "Vérification châssis client" },
          { id: "SITE_PUBLIC", icon: "🌐", label: "Portail Public SaaS", subtitle: "Présentation & formules" }
        ];
      case "MIXTE_ATELIER_MAGASIN":
        // Compte Mixte (1 Principal + 4 Sous-Comptes) : Cumule 100% des options Atelier + Magasin !
        return [
          { id: "DASHBOARD_COMPTE", icon: "⚡", label: "Dashboard Mixte (1+4)", subtitle: "Atelier + Magasin & 4 sous-comptes" },
          { id: "ATELIER_DMS", icon: "🛠️", label: "Espace Atelier (OR & Devis)", subtitle: "Clients, montage & réparations" },
          { id: "MAGASIN_OEM", icon: "🏪", label: "Espace Magasin & Stock", subtitle: "Catalogue OEM, stock & comptoir" },
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
    setAuthModalOpen(false);

    // Réinitialisation stricte sur l'espace principal du compte choisi
    const defaultFirstTab = getFilteredNavigationForRole(nextAccount.role)[0].id;
    setActiveTab(defaultFirstTab);
    showToast(
      `Compte actif : ${nextAccount.organizationName} [${ACCOUNT_ROLES[nextAccount.role].shortBadge}] — Interface réinitialisée et filtrée.`
    );
  };

  const handleOrderPartB2b = (oemRef: string, partName: string, priceDzd: number) => {
    setB2bOrders([
      {
        id: `CMD-B2B-2026-${Math.floor(810 + Math.random() * 180)}`,
        requesterOrg: `${currentAccount.organizationName} (${roleMeta.shortBadge})`,
        oemRef,
        partName,
        qty: 1,
        unitPriceDzd: priceDzd,
        status: "NOUVELLE DEMANDE"
      },
      ...b2bOrders
    ]);
    showToast(`Commande B2B transmise au Fournisseur pour ${oemRef} (${partName})`);
  };

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

          {/* Carte du Compte Actif & Boutons Inscription / Changement de Formule */}
          <div
            style={{
              padding: 12,
              borderRadius: 12,
              backgroundColor: MekanikTheme.bg,
              border: `1.5px solid ${roleMeta.accentColor}`,
              marginBottom: 16
            }}
          >
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

            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 6, marginTop: 10 }}>
              <button
                onClick={() => {
                  setAuthModalInitialTab("SWITCH_ROLE");
                  setAuthModalOpen(true);
                }}
                style={{
                  padding: "7px 8px",
                  borderRadius: 7,
                  border: `1px solid ${MekanikTheme.orange}`,
                  backgroundColor: "rgba(240,120,54,0.18)",
                  color: "#FFF",
                  fontWeight: 800,
                  fontSize: 11,
                  cursor: "pointer"
                }}
              >
                🔄 Changer Compte
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

          {/* Menu strictement filtré pour la formule active */}
          <div style={{ fontSize: 10.5, fontWeight: 800, color: MekanikTheme.textSecondary, marginBottom: 8 }}>
            OPTIONS EXCLUSIVES DU COMPTE ({roleMeta.shortBadge}) :
          </div>
          <nav style={{ display: "flex", flexDirection: "column", gap: 7 }}>
            {allowedNavItems.map((item) => {
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
            })}
          </nav>

          {/* Accès Super-Administrateur Unique : badroumc@gmail.com (Google 2FA + SMS + Email) */}
          <div style={{ marginTop: 16, paddingTop: 14, borderTop: `1px solid ${MekanikTheme.border}` }}>
            <button
              onClick={() => setActiveTab("SUPER_ADMIN_2FA")}
              style={{
                width: "100%",
                textAlign: "left",
                padding: "10px 12px",
                borderRadius: 10,
                border: `1.5px solid ${
                  activeTab === "SUPER_ADMIN_2FA" ? MekanikTheme.orange : MekanikTheme.border
                }`,
                backgroundColor:
                  activeTab === "SUPER_ADMIN_2FA" ? "rgba(240,120,54,0.22)" : MekanikTheme.bg,
                color: "#FFF",
                cursor: "pointer"
              }}
            >
              <div style={{ fontWeight: 900, fontSize: 12, color: MekanikTheme.orange }}>
                🔐 Super-Admin ({SUPER_ADMIN_UNIQUE_EMAIL})
              </div>
              <div style={{ fontSize: 10, color: MekanikTheme.textSecondary }}>
                Prix Abonnements • Paiements • Comptes • Multi-APIs (2FA+SMS+Email)
              </div>
            </button>
          </div>
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
          </div>
        </header>

        {/* BARRE DE BASCULEMENT RAPIDE EN 1 CLIC ENTRE LES 7 FORMULES DE COMPTES (DÉMO INVESTISSEURS) */}
        <div
          style={{
            padding: "10px 14px",
            backgroundColor: MekanikTheme.surface,
            borderRadius: 12,
            border: `1px solid ${MekanikTheme.border}`,
            marginBottom: 18,
            display: "flex",
            alignItems: "center",
            gap: 8,
            flexWrap: "wrap"
          }}
        >
          <span style={{ fontSize: 11, fontWeight: 900, color: MekanikTheme.orange }}>
            DÉMO SÉPARATION DES COMPTES (1 CLIC) :
          </span>
          {(Object.keys(ACCOUNT_ROLES) as AccountRoleId[]).map((rk) => {
            const m = ACCOUNT_ROLES[rk];
            const active = currentAccount.role === rk;
            return (
              <button
                key={rk}
                onClick={() => {
                  const found = savedAccounts.find((a) => a.role === rk);
                  if (found) {
                    handleSwitchOrRegisterAccount(found);
                  } else {
                    const autoAcc: UserAccountSession = {
                      userId: `USR-${rk}`,
                      organizationId: `ORG-${rk.slice(0, 5)}-01`,
                      organizationName: `${m.shortBadge} Démo`,
                      fullName: "Responsable Pro",
                      email: `demo@${rk.toLowerCase()}.dz`,
                      phone: "+213 550 94 82 10",
                      wilaya: "Alger (16)",
                      rcNumber: "16/00-4829104B22",
                      role: rk,
                      twoFactorEnabled: true,
                      totpSecretKey: "JBSWY3DPEHPK3PXP",
                      createdAt: "17/11/2026",
                      subAccounts: []
                    };
                    handleSwitchOrRegisterAccount(autoAcc);
                  }
                }}
                style={{
                  padding: "6px 10px",
                  borderRadius: 7,
                  border: `1px solid ${active ? m.accentColor : MekanikTheme.border}`,
                  backgroundColor: active ? m.accentColor : MekanikTheme.bg,
                  color: "#FFF",
                  fontWeight: 800,
                  fontSize: 11,
                  cursor: "pointer"
                }}
              >
                {m.shortBadge}
              </button>
            );
          })}
        </div>

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
        {/* VUE 7 : SITE PUBLIC & PRÉSENTATION DES FORMULES                   */}
        {/* ================================================================= */}
        {activeTab === "SITE_PUBLIC" && (
          <div style={{ display: "grid", gap: 16 }}>
            <div
              style={{
                padding: 20,
                backgroundColor: MekanikTheme.surface,
                borderRadius: 16,
                border: `1px solid ${MekanikTheme.border}`
              }}
            >
              <div style={{ fontWeight: 900, fontSize: 17, color: MekanikTheme.orange, marginBottom: 12 }}>
                LES 7 FORMULES DE COMPTES SÉPARÉES — MEKANIK+ SAAS
              </div>
              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "repeat(auto-fit, minmax(250px, 1fr))",
                  gap: 12
                }}
              >
                {(Object.keys(rolesPricing) as AccountRoleId[]).map((rk) => {
                  const meta = rolesPricing[rk];
                  return (
                    <div
                      key={rk}
                      style={{
                        padding: 16,
                        borderRadius: 12,
                        backgroundColor: MekanikTheme.bg,
                        border: `1px solid ${meta.accentColor}`,
                        display: "flex",
                        flexDirection: "column",
                        justifyContent: "space-between"
                      }}
                    >
                      <div>
                        <div style={{ fontWeight: 900, fontSize: 14, color: meta.accentColor }}>
                          {meta.title}
                        </div>
                        <div style={{ fontWeight: 900, fontSize: 18, margin: "6px 0" }}>
                          {meta.monthlyPriceDzd === 0
                            ? "0 DA / mois"
                            : `${meta.monthlyPriceDzd.toLocaleString("fr-FR")} DA / mois`}
                        </div>
                        <p style={{ fontSize: 11.5, color: MekanikTheme.textSecondary, lineHeight: 1.45 }}>
                          {meta.description}
                        </p>
                      </div>
                      <button
                        onClick={() => {
                          setAuthModalInitialTab("REGISTER");
                          setAuthModalOpen(true);
                        }}
                        style={{
                          marginTop: 10,
                          padding: "9px 12px",
                          borderRadius: 8,
                          border: "none",
                          backgroundColor: meta.accentColor,
                          color: "#FFF",
                          fontWeight: 800,
                          fontSize: 12,
                          cursor: "pointer"
                        }}
                      >
                        Créer ce Compte →
                      </button>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        )}

        {/* ================================================================= */}
        {/* VUE 8 : SUPER-ADMINISTRATEUR UNIQUE (badroumc@gmail.com)          */}
        {/* ================================================================= */}
        {activeTab === "SUPER_ADMIN_2FA" && (
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
                localStorage.setItem(
                  "mekanik_superadmin_mecaverif_prices_v1",
                  JSON.stringify(next)
                );
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
        )}
      </main>

      {/* MODALE COMPLÈTE D'INSCRIPTION, CONNEXION, ACCOUNT SELECTOR & SOUS-COMPTES 2FA */}
      {authModalOpen && (
        <AuthAndAccountModal
          currentAccount={currentAccount}
          savedAccounts={savedAccounts}
          initialTab={authModalInitialTab}
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
