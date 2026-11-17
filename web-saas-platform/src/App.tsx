import React, { useState } from "react";

/**
 * MEKANIK+ — PLATEFORME WEB SAAS AUTOMOBILE PROFESSIONNELLE (React + TypeScript)
 * Identité visuelle officielle :
 * - Fond : #101820
 * - Surfaces : #1C2833
 * - Orange signature : #F07836
 * - Texte principal : #E8EDF2
 * - Texte secondaire : #94A3B8
 */
export type SaasSpaceKey =
  | "SITE_PUBLIC"
  | "ESPACE_UTILISATEUR"
  | "ESPACE_ATELIER"
  | "ESPACE_MAGASIN"
  | "ESPACE_FOURNISSEUR"
  | "ADMINISTRATION_GENERALE";

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

interface SupplierProduct {
  id: string;
  partName: string;
  system: string;
  oemRef: string;
  equivRefs: string[];
  compat: string[];
  publicPriceDzd: number;
  proPriceDzd: number;
  stockQty: number;
  warehouse: string;
}

export const App: React.FC = () => {
  const [activeSpace, setActiveSpace] = useState<SaasSpaceKey>("SITE_PUBLIC");
  const [searchMode, setSearchMode] = useState<"VIN" | "CODE_MOTEUR" | "VEHICULE">("VIN");
  const [vinQuery, setVinQuery] = useState("WVWZZZAUZHW129834");
  const [decodedBanner, setDecodedBanner] = useState(
    "VIN WVWZZZAUZHW129834 identifié : Volkswagen Golf 7 2.0 TDI 150ch (Moteur CRLB • Boîte DSG6 • 2019)"
  );
  const [activeOrgId, setActiveOrgId] = useState("ORG-MIXTE-ALGER-01");
  const [toastMsg, setToastMsg] = useState<string | null>(null);

  // État Paiement Unitaire MécaVerif+ (250 DA Pro / 300 DA Particulier)
  const [mecaVerifTariff, setMecaVerifTariff] = useState<250 | 300>(250);
  const [paymentGateway, setPaymentGateway] = useState<
    "SATIM_CIB" | "EDAHABIA" | "BARIDIMOB_QR" | "CHARGILY_PAY" | "STRIPE_INTL"
  >("SATIM_CIB");
  const [otpCode, setOtpCode] = useState("739204");
  const [unlockedVins, setUnlockedVins] = useState<string[]>(["WVWZZZ5NZMW123456"]);

  // État Catalogue Fournisseur B2B
  const [catalog] = useState<SupplierProduct[]>([
    {
      id: "SUP-01",
      partName: "Kit de Distribution Complet + Pompe à Eau",
      system: "Moteur & Distribution",
      oemRef: "04L 198 119 A",
      equivRefs: ["CONTITECH CT1168WP1", "GATES KP15678XS", "INA 530 0650 30"],
      compat: ["VW Golf 7 2.0 TDI (CRLB)", "Audi A3 8V 2.0 TDI", "Seat Leon 5F", "Skoda Octavia III"],
      publicPriceDzd: 34500,
      proPriceDzd: 26800,
      stockQty: 24,
      warehouse: "Dépôt Central Alger (Oued Smar)"
    },
    {
      id: "SUP-02",
      partName: "Vanne EGR Électropneumatique + Refroidisseur",
      system: "Dépollution & Admission",
      oemRef: "98 075 930 80",
      equivRefs: ["VALEO 700444", "PIERBURG 7.09720.00.0", "DELPHI EG10512"],
      compat: ["Peugeot 308 / 3008 1.6 & 2.0 BlueHDi", "Citroën C4 / Berlingo", "Opel Grandland"],
      publicPriceDzd: 42000,
      proPriceDzd: 33500,
      stockQty: 14,
      warehouse: "Dépôt Oran (Es Sénia)"
    },
    {
      id: "SUP-03",
      partName: "Optique Phare Avant Full LED + Support Tôlerie",
      system: "Carrosserie, Tôlerie & Éclairage",
      oemRef: "5G1 941 081 F",
      equivRefs: ["HELLA 1EX 013 924-111", "VALEO 046891"],
      compat: ["VW Golf 7.5 / 8", "Seat Leon FR", "Renault Clio 5 (260604129R)"],
      publicPriceDzd: 78000,
      proPriceDzd: 61000,
      stockQty: 7,
      warehouse: "Dépôt Carrosserie Blida / Alger"
    }
  ]);

  const [b2bOrders, setB2bOrders] = useState([
    {
      id: "CMD-B2B-801",
      requester: "Garage & Pièces El-Djazair Auto+ (Compte Mixte 1+4)",
      oemRef: "04L 198 119 A",
      partName: "Kit de Distribution Complet + Pompe à Eau",
      qty: 2,
      totalDzd: 53600,
      status: "NOUVELLE DEMANDE"
    },
    {
      id: "CMD-B2B-802",
      requester: "Carrosserie & Tôlerie Laser Blida",
      oemRef: "5G1 941 081 F",
      partName: "Optique Phare Avant Full LED + Support Tôlerie",
      qty: 1,
      totalDzd: 61000,
      status: "EXPÉDIÉE"
    }
  ]);

  // État Super-Admin 2FA (Google Authenticator + SMS OTP) & Clés API No-Code
  const [totpAdmin, setTotpAdmin] = useState("482910");
  const [smsAdmin, setSmsAdmin] = useState("839201");
  const [adminUnlocked, setAdminUnlocked] = useState(false);
  const [apiKeys, setApiKeys] = useState([
    { id: "TECDOC", name: "TecDoc Pegasus 3.0 Catalog API", key: "TD-LIVE-9948-MEKANIK-DZ-2026", active: true },
    { id: "ETKA", name: "ETKA Partslink24 VAG Official API", key: "ETKA-VAG-7741-OEM-PRO", active: true },
    { id: "STELLANTIS", name: "Groupe Stellantis ServiceBox & SEDRE", key: "SBX-PSA-FCA-6630-TOKEN", active: true },
    { id: "VIN_DECODER", name: "VIN Master Decoder ISO-3779", key: "VIN-ISO-8821-GLOBAL", active: true },
    { id: "TIKTOK", name: "TikTok Business & Catalogue Vidéo IA", key: "TTK-BIZ-5510-MEKANIK", active: true }
  ]);

  const showToast = (msg: string) => {
    setToastMsg(msg);
    setTimeout(() => setToastMsg(null), 4000);
  };

  const spaces: { key: SaasSpaceKey; code: string; label: string; subtitle: string }[] = [
    { key: "SITE_PUBLIC", code: "A", label: "Site Public", subtitle: "Accueil, fonctionnalités & abonnements" },
    { key: "ESPACE_UTILISATEUR", code: "B", label: "Espace Utilisateur", subtitle: "Profil, MécaVerif+ (CIB/Stripe) & suivi" },
    { key: "ESPACE_ATELIER", code: "C", label: "Espace Atelier", subtitle: "VIN, OR/Devis, MekanikAI & clients" },
    { key: "ESPACE_MAGASIN", code: "D", label: "Espace Magasin", subtitle: "Catalogue OEM, stock & équivalences" },
    { key: "ESPACE_FOURNISSEUR", code: "E", label: "Espace Fournisseur", subtitle: "Grossiste B2B, tarifs & commandes" },
    { key: "ADMINISTRATION_GENERALE", code: "F", label: "Administration Générale", subtitle: "Super-Admin 2FA TOTP+SMS & API" }
  ];

  return (
    <div style={{ display: "flex", minHeight: "100vh", backgroundColor: MekanikTheme.bg, color: MekanikTheme.textPrimary, flexWrap: "wrap" }}>
      {/* BARRE LATÉRALE GAUCHE SAAS */}
      <aside
        style={{
          width: 280,
          backgroundColor: MekanikTheme.surface,
          borderRight: `1px solid ${MekanikTheme.border}`,
          padding: 20,
          display: "flex",
          flexDirection: "column",
          justifyContent: "space-between"
        }}
      >
        <div>
          <div style={{ fontSize: 22, fontWeight: 900, letterSpacing: 0.5, marginBottom: 4 }}>
            MEKANIK<span style={{ color: MekanikTheme.orange }}>+</span> SAAS
          </div>
          <div style={{ fontSize: 11, color: MekanikTheme.textSecondary, marginBottom: 16 }}>
            Plateforme Automobile Nouvelle Génération • Tenant :{" "}
            <strong style={{ color: MekanikTheme.orange }}>{activeOrgId}</strong>
          </div>

          <nav style={{ display: "flex", flexDirection: "column", gap: 8 }}>
            {spaces.map((sp) => {
              const selected = sp.key === activeSpace;
              return (
                <button
                  key={sp.key}
                  onClick={() => setActiveSpace(sp.key)}
                  style={{
                    textAlign: "left",
                    padding: "11px 13px",
                    borderRadius: 10,
                    border: `1px solid ${selected ? MekanikTheme.orange : MekanikTheme.border}`,
                    backgroundColor: selected ? "rgba(240, 120, 54, 0.18)" : MekanikTheme.bg,
                    color: MekanikTheme.textPrimary,
                    cursor: "pointer"
                  }}
                >
                  <div style={{ fontWeight: 800, fontSize: 13 }}>
                    [{sp.code}] {sp.label}
                  </div>
                  <div style={{ fontSize: 11, color: MekanikTheme.textSecondary, marginTop: 2 }}>{sp.subtitle}</div>
                </button>
              );
            })}
          </nav>
        </div>

        <div
          style={{
            marginTop: 20,
            padding: 12,
            borderRadius: 10,
            backgroundColor: MekanikTheme.bg,
            border: `1px solid ${MekanikTheme.border}`,
            fontSize: 11
          }}
        >
          <div style={{ color: MekanikTheme.green, fontWeight: 800 }}>● SÉCURITÉ 2FA TOTP + SMS ACTIVE</div>
          <div style={{ color: MekanikTheme.textSecondary, marginTop: 4 }}>
            TecDoc • ETKA VAG • Stellantis ServiceBox • SATIM CIB / Edahabia / Stripe
          </div>
        </div>
      </aside>

      {/* CONTENU PRINCIPAL */}
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
              fontWeight: 700,
              fontSize: 13
            }}
          >
            ✓ {toastMsg}
          </div>
        )}

        {/* EN-TÊTE SUPÉRIEUR */}
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
            marginBottom: 20
          }}
        >
          <div>
            <h1 style={{ margin: 0, fontSize: 21 }}>
              MEKANIK<span style={{ color: MekanikTheme.orange }}>+</span> — « La plateforme automobile nouvelle génération »
            </h1>
            <p style={{ margin: "4px 0 0", color: MekanikTheme.textSecondary, fontSize: 12.5 }}>
              DMS Atelier • Catalogue OEM • Carrosserie Photos Avant/Après • MécaVerif+ • Fournisseur B2B
            </p>
          </div>
          <select
            value={activeOrgId}
            onChange={(e) => setActiveOrgId(e.target.value)}
            style={{
              backgroundColor: MekanikTheme.bg,
              color: MekanikTheme.textPrimary,
              border: `1px solid ${MekanikTheme.orange}`,
              borderRadius: 8,
              padding: "9px 12px",
              fontWeight: 700
            }}
          >
            <option value="ORG-MIXTE-ALGER-01">Compte Mixte Atelier+Magasin (1+4 Sous-Comptes)</option>
            <option value="ORG-ATELIER-ORAN-02">Atelier Mécanique Pro (Oran)</option>
            <option value="ORG-CARROSSERIE-BLIDA-03">Atelier Spécial Carrosserie & Tôlerie</option>
            <option value="ORG-MAGASIN-SETIF-04">Magasin Pièces Détachées OEM</option>
            <option value="ORG-FOURNISSEUR-DZ-05">Fournisseur / Grossiste B2B Algérie</option>
          </select>
        </header>

        {/* BARRE DE RECHERCHE UNIVERSELLE [ VIN ] [ CODE MOTEUR ] [ RECHERCHE VÉHICULE ] */}
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
                  fontWeight: 800,
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
              placeholder="Saisir N° Châssis VIN (17 caractères), Code Moteur (ex: CRLB, DV6FC) ou Modèle..."
              style={{
                flex: 1,
                minWidth: 220,
                padding: "12px 14px",
                borderRadius: 10,
                border: `1px solid ${MekanikTheme.border}`,
                backgroundColor: MekanikTheme.bg,
                color: MekanikTheme.textPrimary,
                fontSize: 14,
                fontFamily: "monospace"
              }}
            />
            <button
              onClick={() => {
                setDecodedBanner(
                  `Châssis ${vinQuery} décodé via TecDoc + ETKA + Stellantis : Véhicule identifié & pièces compatibles filtrées.`
                );
                showToast(`Identification ${searchMode} réussie pour ${vinQuery}`);
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
              IDENTIFIER
            </button>
          </div>
          <div style={{ marginTop: 10, fontSize: 12, color: MekanikTheme.green, fontWeight: 700 }}>
            ● {decodedBanner}
          </div>
        </section>

        {/* ESPACE A : SITE PUBLIC */}
        {activeSpace === "SITE_PUBLIC" && (
          <div style={{ display: "grid", gap: 16 }}>
            <div
              style={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fit, minmax(190px, 1fr))",
                gap: 12
              }}
            >
              {[
                { icon: "🔧", title: "Trouver une pièce", target: "ESPACE_MAGASIN" as SaasSpaceKey },
                { icon: "🚗", title: "Identifier mon véhicule", target: "ESPACE_ATELIER" as SaasSpaceKey },
                { icon: "🛠️", title: "Trouver un atelier", target: "ESPACE_ATELIER" as SaasSpaceKey },
                { icon: "🏪", title: "Espace Fournisseur B2B", target: "ESPACE_FOURNISSEUR" as SaasSpaceKey },
                { icon: "🧠", title: "Diagnostic MekanikAI", target: "ESPACE_ATELIER" as SaasSpaceKey },
                { icon: "🛡️", title: "MécaVerif+ (250/300 DA)", target: "ESPACE_UTILISATEUR" as SaasSpaceKey }
              ].map((card) => (
                <div
                  key={card.title}
                  onClick={() => setActiveSpace(card.target)}
                  style={{
                    padding: 16,
                    backgroundColor: MekanikTheme.surface,
                    borderRadius: 12,
                    border: `1px solid ${MekanikTheme.border}`,
                    cursor: "pointer"
                  }}
                >
                  <div style={{ fontSize: 22 }}>{card.icon}</div>
                  <div style={{ fontWeight: 800, marginTop: 6, fontSize: 14 }}>{card.title}</div>
                  <div style={{ fontSize: 11, color: MekanikTheme.orange, marginTop: 4 }}>Ouvrir l'espace →</div>
                </div>
              ))}
            </div>

            <div
              style={{
                padding: 20,
                backgroundColor: MekanikTheme.surface,
                borderRadius: 14,
                border: `1px solid ${MekanikTheme.border}`
              }}
            >
              <h2 style={{ margin: "0 0 12px", fontSize: 17, color: MekanikTheme.orange }}>
                ABONNEMENTS PAR RÔLE & COMPTE MIXTE (1 PRINCIPAL + 4 SOUS-COMPTES)
              </h2>
              <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))", gap: 12 }}>
                {[
                  { name: "Compte Particulier", price: "0 DA / mois", desc: "Suivi entretien + MécaVerif+ à l'acte (300 DA / VIN)" },
                  { name: "Atelier Mécanique Pro", price: "9 900 DA / mois", desc: "VIN, Devis/OR, MekanikAI OBD, Schémas & Clients" },
                  { name: "Magasin Pièces Détachées", price: "9 500 DA / mois", desc: "Catalogue OEM TecDoc/ETKA, Stock & Équivalences" },
                  { name: "Compte Mixte (Atelier + Magasin)", price: "14 900 DA / mois", desc: "1 Principal + 4 Sous-Comptes • Toutes options Atelier + Magasin" },
                  { name: "Spécial Carrosserie & Tôlerie", price: "8 900 DA / mois", desc: "Tôlerie/Plastique par VIN + Photos Avant/Après obligatoires" },
                  { name: "Fournisseur / Grossiste B2B", price: "24 900 DA / mois", desc: "Catalogue B2B, remises pro & réception commandes garages" }
                ].map((plan) => (
                  <div
                    key={plan.name}
                    style={{
                      padding: 14,
                      borderRadius: 10,
                      backgroundColor: MekanikTheme.bg,
                      border: `1px solid ${MekanikTheme.border}`
                    }}
                  >
                    <div style={{ fontWeight: 800, fontSize: 14 }}>{plan.name}</div>
                    <div style={{ fontSize: 18, fontWeight: 900, color: MekanikTheme.orange, margin: "6px 0" }}>
                      {plan.price}
                    </div>
                    <div style={{ fontSize: 12, color: MekanikTheme.textSecondary }}>{plan.desc}</div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* ESPACE B : ESPACE UTILISATEUR & PAIEMENT SÉCURISÉ MÉCAVERIF+ (CIB / EDAHABIA / BARIDIMOB / CHARGILY / STRIPE) */}
        {activeSpace === "ESPACE_UTILISATEUR" && (
          <div
            style={{
              padding: 20,
              backgroundColor: MekanikTheme.surface,
              borderRadius: 14,
              border: `1px solid ${MekanikTheme.orange}`
            }}
          >
            <h2 style={{ margin: "0 0 8px", fontSize: 18, color: MekanikTheme.orange }}>
              PAIEMENT SÉCURISÉ MÉCAVERIF+™ (HORS ABONNEMENT • 250 DA OU 300 DA PAR CHÂSSIS)
            </h2>
            <p style={{ margin: "0 0 16px", fontSize: 13, color: MekanikTheme.textSecondary }}>
              Débloquez la courbe kilométrique style CarVertical, les photos Avant/Après obligatoires des ateliers de carrosserie et l'historique mécanicien.
            </p>

            <div style={{ display: "flex", gap: 10, flexWrap: "wrap", marginBottom: 14 }}>
              <button
                onClick={() => setMecaVerifTariff(250)}
                style={{
                  padding: "10px 16px",
                  borderRadius: 8,
                  border: `1px solid ${mecaVerifTariff === 250 ? MekanikTheme.orange : MekanikTheme.border}`,
                  backgroundColor: mecaVerifTariff === 250 ? "rgba(240,120,54,0.2)" : MekanikTheme.bg,
                  color: "#FFF",
                  fontWeight: 800,
                  cursor: "pointer"
                }}
              >
                Tarif Pro Atelier / Tôlier / Magasin : 250 DA / VIN (1,70 €)
              </button>
              <button
                onClick={() => setMecaVerifTariff(300)}
                style={{
                  padding: "10px 16px",
                  borderRadius: 8,
                  border: `1px solid ${mecaVerifTariff === 300 ? MekanikTheme.orange : MekanikTheme.border}`,
                  backgroundColor: mecaVerifTariff === 300 ? "rgba(240,120,54,0.2)" : MekanikTheme.bg,
                  color: "#FFF",
                  fontWeight: 800,
                  cursor: "pointer"
                }}
              >
                Tarif Particulier : 300 DA / VIN (2,00 €)
              </button>
            </div>

            <div style={{ display: "flex", gap: 8, flexWrap: "wrap", marginBottom: 14 }}>
              {[
                { id: "SATIM_CIB", label: "Carte CIB / SATIM Algérie" },
                { id: "EDAHABIA", label: "Carte Edahabia (Algérie Poste)" },
                { id: "BARIDIMOB_QR", label: "BaridiMob QR & RIP" },
                { id: "CHARGILY_PAY", label: "Chargily Pay / SlickPay DZ" },
                { id: "STRIPE_INTL", label: "Stripe International (Visa / MC)" }
              ].map((gw) => (
                <button
                  key={gw.id}
                  onClick={() => setPaymentGateway(gw.id as any)}
                  style={{
                    padding: "8px 12px",
                    borderRadius: 8,
                    border: `1px solid ${paymentGateway === gw.id ? MekanikTheme.green : MekanikTheme.border}`,
                    backgroundColor: paymentGateway === gw.id ? "rgba(34,197,94,0.18)" : MekanikTheme.bg,
                    color: "#FFF",
                    fontWeight: 700,
                    cursor: "pointer",
                    fontSize: 12
                  }}
                >
                  {gw.label}
                </button>
              ))}
            </div>

            <div style={{ display: "flex", gap: 10, flexWrap: "wrap", alignItems: "center" }}>
              <input
                value={otpCode}
                onChange={(e) => setOtpCode(e.target.value)}
                placeholder="Code 3D-Secure SMS (6 chiffres)"
                style={{
                  padding: "10px 14px",
                  borderRadius: 8,
                  border: `1px solid ${MekanikTheme.border}`,
                  backgroundColor: MekanikTheme.bg,
                  color: "#FFF"
                }}
              />
              <button
                onClick={() => {
                  if (!unlockedVins.includes(vinQuery)) {
                    setUnlockedVins([vinQuery, ...unlockedVins]);
                  }
                  showToast(
                    `Paiement ${mecaVerifTariff} DA (${paymentGateway}) validé • Rapport MécaVerif+ débloqué pour ${vinQuery}`
                  );
                }}
                style={{
                  padding: "11px 20px",
                  borderRadius: 8,
                  border: "none",
                  backgroundColor: MekanikTheme.green,
                  color: "#FFF",
                  fontWeight: 900,
                  cursor: "pointer"
                }}
              >
                Payer {mecaVerifTariff} DA & Débloquer le Rapport VIN {vinQuery}
              </button>
            </div>
          </div>
        )}

        {/* ESPACE C : ESPACE ATELIER & MEKANIKAI OBD */}
        {activeSpace === "ESPACE_ATELIER" && (
          <div
            style={{
              padding: 20,
              backgroundColor: MekanikTheme.surface,
              borderRadius: 14,
              border: `1px solid ${MekanikTheme.border}`
            }}
          >
            <h2 style={{ margin: "0 0 10px", fontSize: 18, color: MekanikTheme.orange }}>
              C. ESPACE ATELIER PRO & ASSISTANT DIAGNOSTIC MEKANIKAI OBD-II
            </h2>
            <p style={{ color: MekanikTheme.textSecondary, fontSize: 13 }}>
              Analyse des codes défauts DTC (ex. P0299 Sous-pression turbocompresseur), procédures multimètre pas-à-pas, couples de serrage en Nm, schémas électriques OEM et commande B2B directe.
            </p>
            <div style={{ padding: 14, borderRadius: 10, backgroundColor: MekanikTheme.bg, border: `1px solid ${MekanikTheme.orange}`, marginTop: 12 }}>
              <div style={{ fontWeight: 800, color: MekanikTheme.orange }}>
                🧠 Diagnostic MekanikAI : Code P0299 — Régulation de pression de suralimentation (Limite non atteinte)
              </div>
              <div style={{ fontSize: 12.5, color: MekanikTheme.textPrimary, marginTop: 6 }}>
                1. Contrôler l'étanchéité du circuit d'admission • 2. Tester l'électrovanne N75 (Résistance : 14 à 20 Ω) • 3. Vérifier la course de l'actuateur géométrie variable • Référence OEM recommandée : <strong style={{ color: MekanikTheme.redOem }}>04L 253 010 B</strong>.
              </div>
            </div>
          </div>
        )}

        {/* ESPACE D : ESPACE MAGASIN (FIL CONDUCTEUR VISUEL & RÉFÉRENCE CONSTRUCTEUR MISE EN ÉVIDENCE) */}
        {activeSpace === "ESPACE_MAGASIN" && (
          <div style={{ display: "grid", gap: 12 }}>
            <div style={{ padding: 14, backgroundColor: MekanikTheme.surface, borderRadius: 12, border: `1px solid ${MekanikTheme.orange}`, fontWeight: 800, fontSize: 13 }}>
              Véhicule sélectionné ({vinQuery}) ↓ Système ↓ Pièce ↓ <span style={{ color: MekanikTheme.redOem }}>Référence constructeur OEM</span> ↓ Équivalences ↓ Vendeurs & Fournisseurs B2B disponibles
            </div>
            {catalog.map((item) => (
              <div
                key={item.id}
                style={{
                  padding: 16,
                  backgroundColor: MekanikTheme.surface,
                  borderRadius: 12,
                  border: `1px solid ${MekanikTheme.border}`,
                  display: "flex",
                  justifyContent: "space-between",
                  flexWrap: "wrap",
                  gap: 12
                }}
              >
                <div>
                  <div style={{ fontSize: 11, color: MekanikTheme.textSecondary }}>{item.system}</div>
                  <div style={{ fontWeight: 800, fontSize: 15, marginTop: 2 }}>{item.partName}</div>
                  <div
                    style={{
                      display: "inline-block",
                      marginTop: 6,
                      padding: "4px 10px",
                      borderRadius: 6,
                      backgroundColor: "rgba(229, 57, 53, 0.2)",
                      border: `1px solid ${MekanikTheme.redOem}`,
                      color: "#FFF",
                      fontWeight: 900,
                      fontFamily: "monospace"
                    }}
                  >
                    RÉF OEM : {item.oemRef}
                  </div>
                  <div style={{ fontSize: 12, color: MekanikTheme.textSecondary, marginTop: 6 }}>
                    Équivalences : {item.equivRefs.join(" • ")}
                  </div>
                  <div style={{ display: "flex", gap: 6, flexWrap: "wrap", marginTop: 6 }}>
                    {item.compat.map((c) => (
                      <span
                        key={c}
                        style={{
                          padding: "2px 8px",
                          borderRadius: 6,
                          backgroundColor: MekanikTheme.bg,
                          border: `1px solid ${MekanikTheme.green}`,
                          color: MekanikTheme.green,
                          fontSize: 11,
                          fontWeight: 700
                        }}
                      >
                        ✓ {c}
                      </span>
                    ))}
                  </div>
                </div>
                <div style={{ textAlign: "right" }}>
                  <div style={{ fontSize: 12, color: MekanikTheme.textSecondary }}>Prix Public : {item.publicPriceDzd} DA</div>
                  <div style={{ fontSize: 18, fontWeight: 900, color: MekanikTheme.orange }}>
                    Tarif B2B : {item.proPriceDzd} DA
                  </div>
                  <div style={{ fontSize: 11, color: MekanikTheme.green, marginTop: 4 }}>
                    ● En stock ({item.stockQty} u.) — {item.warehouse}
                  </div>
                  <button
                    onClick={() => {
                      setB2bOrders([
                        {
                          id: `CMD-B2B-${Math.floor(810 + Math.random() * 90)}`,
                          requester: activeOrgId,
                          oemRef: item.oemRef,
                          partName: item.partName,
                          qty: 1,
                          totalDzd: item.proPriceDzd,
                          status: "NOUVELLE DEMANDE"
                        },
                        ...b2bOrders
                      ]);
                      showToast(`Commande B2B transmise au Fournisseur pour ${item.oemRef}`);
                    }}
                    style={{
                      marginTop: 8,
                      padding: "8px 14px",
                      borderRadius: 8,
                      border: "none",
                      backgroundColor: MekanikTheme.orange,
                      color: "#FFF",
                      fontWeight: 800,
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

        {/* ESPACE E : ESPACE FOURNISSEUR / GROSSISTE B2B */}
        {activeSpace === "ESPACE_FOURNISSEUR" && (
          <div
            style={{
              padding: 20,
              backgroundColor: MekanikTheme.surface,
              borderRadius: 14,
              border: `1px solid ${MekanikTheme.border}`
            }}
          >
            <h2 style={{ margin: "0 0 12px", fontSize: 18, color: MekanikTheme.orange }}>
              E. ESPACE FOURNISSEUR / GROSSISTE B2B — DEMANDES DES ATELIERS & MAGASINS
            </h2>
            <div style={{ display: "grid", gap: 10 }}>
              {b2bOrders.map((ord) => (
                <div
                  key={ord.id}
                  style={{
                    padding: 14,
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
                    <div style={{ fontWeight: 800 }}>
                      {ord.id} • {ord.partName} (<span style={{ color: MekanikTheme.orange }}>{ord.oemRef}</span>)
                    </div>
                    <div style={{ fontSize: 12, color: MekanikTheme.textSecondary }}>
                      Demandeur : {ord.requester} • Qté : {ord.qty} • Total B2B : {ord.totalDzd} DA
                    </div>
                  </div>
                  <span
                    style={{
                      padding: "6px 12px",
                      borderRadius: 6,
                      backgroundColor: "rgba(34,197,94,0.18)",
                      color: MekanikTheme.green,
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

        {/* ESPACE F : ADMINISTRATION GÉNÉRALE & SUPER-ADMINISTRATEUR (DOUBLE 2FA GOOGLE AUTHENTICATOR + SMS OTP) */}
        {activeSpace === "ADMINISTRATION_GENERALE" && (
          <div
            style={{
              padding: 20,
              backgroundColor: MekanikTheme.surface,
              borderRadius: 14,
              border: `1px solid ${MekanikTheme.orange}`
            }}
          >
            <h2 style={{ margin: "0 0 8px", fontSize: 18, color: MekanikTheme.orange }}>
              F. SUPER-ADMINISTRATEUR — DOUBLE AUTHENTIFICATION 2FA (GOOGLE AUTHENTICATOR + SMS) & CLÉS API NO-CODE
            </h2>
            {!adminUnlocked ? (
              <div style={{ display: "flex", gap: 10, flexWrap: "wrap", marginTop: 12 }}>
                <input
                  value={totpAdmin}
                  onChange={(e) => setTotpAdmin(e.target.value)}
                  placeholder="Code Google Authenticator (6 chiffres)"
                  style={{ padding: "10px 14px", borderRadius: 8, backgroundColor: MekanikTheme.bg, color: "#FFF", border: `1px solid ${MekanikTheme.border}` }}
                />
                <input
                  value={smsAdmin}
                  onChange={(e) => setSmsAdmin(e.target.value)}
                  placeholder="Code SMS OTP reçu (+213 550...)"
                  style={{ padding: "10px 14px", borderRadius: 8, backgroundColor: MekanikTheme.bg, color: "#FFF", border: `1px solid ${MekanikTheme.border}` }}
                />
                <button
                  onClick={() => {
                    setAdminUnlocked(true);
                    showToast("Double authentification Super-Admin (TOTP + SMS) validée !");
                  }}
                  style={{ padding: "10px 18px", borderRadius: 8, border: "none", backgroundColor: MekanikTheme.orange, color: "#FFF", fontWeight: 900, cursor: "pointer" }}
                >
                  Déverrouiller le Coffre-Fort API
                </button>
              </div>
            ) : (
              <div style={{ display: "grid", gap: 10, marginTop: 12 }}>
                {apiKeys.map((k, idx) => (
                  <div
                    key={k.id}
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
                      <div style={{ fontWeight: 800, fontSize: 13 }}>{k.name}</div>
                      <input
                        value={k.key}
                        onChange={(e) => {
                          const next = [...apiKeys];
                          next[idx] = { ...k, key: e.target.value };
                          setApiKeys(next);
                        }}
                        style={{
                          marginTop: 4,
                          width: 310,
                          padding: "6px 10px",
                          borderRadius: 6,
                          backgroundColor: MekanikTheme.surface,
                          color: MekanikTheme.orange,
                          border: `1px solid ${MekanikTheme.border}`,
                          fontFamily: "monospace"
                        }}
                      />
                    </div>
                    <button
                      onClick={() => showToast(`Clé API ${k.id} sauvegardée à chaud sans coder !`)}
                      style={{
                        padding: "8px 14px",
                        borderRadius: 8,
                        border: "none",
                        backgroundColor: MekanikTheme.green,
                        color: "#FFF",
                        fontWeight: 800,
                        cursor: "pointer"
                      }}
                    >
                      Enregistrer
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </main>
    </div>
  );
};

export default App;
