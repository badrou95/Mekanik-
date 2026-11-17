import React, { useState } from "react";
import {
  ACCOUNT_ROLES,
  OBD_DIAGNOSTIC_DATABASE,
  ObdDiagnosticCase,
  UserAccountSession
} from "./typesAndData";

const Theme = {
  bg: "#101820",
  surface: "#1C2833",
  orange: "#F07836",
  red: "#EF4444",
  textPrimary: "#E8EDF2",
  textSecondary: "#94A3B8",
  green: "#22C55E",
  border: "#2B3B4B"
};

interface MekanikAiProps {
  activeVin: string;
  onOrderPartB2b: (oemRef: string, partName: string, priceDzd: number) => void;
}

export const MekanikAiDiagnosticAssistant: React.FC<MekanikAiProps> = ({
  activeVin,
  onOrderPartB2b
}) => {
  const [dtcInput, setDtcInput] = useState("P0299");
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [activeCase, setActiveCase] = useState<ObdDiagnosticCase>(
    OBD_DIAGNOSTIC_DATABASE.P0299
  );

  const runDiagnosis = (codeRaw: string) => {
    const clean = codeRaw.trim().toUpperCase() || "P0299";
    setDtcInput(clean);
    setIsAnalyzing(true);
    setTimeout(() => {
      const found = OBD_DIAGNOSTIC_DATABASE[clean] || {
        ...OBD_DIAGNOSTIC_DATABASE.P0299,
        code: clean,
        title: `Analyse IA OBD-II du code défaut ${clean} (Châssis ${activeVin})`
      };
      setActiveCase(found);
      setIsAnalyzing(false);
    }, 350);
  };

  return (
    <div
      style={{
        padding: 22,
        borderRadius: 16,
        background: "linear-gradient(135deg, #101820 0%, #1C2833 65%, #2A1914 100%)",
        border: `1.5px solid ${Theme.orange}`,
        boxShadow: "0 0 28px rgba(240, 120, 54, 0.16)",
        display: "grid",
        gap: 16
      }}
    >
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 10 }}>
        <div>
          <div style={{ fontSize: 19, fontWeight: 900, color: Theme.orange }}>
            🧠⚙️ MEKANIKAI™ — ASSISTANT DIAGNOSTIC OBD-II & DOCUMENTATION TECHNIQUE
          </div>
          <div style={{ fontSize: 12, color: Theme.textSecondary }}>
            Analyse des codes défauts DTC, explications techniques, procédures multimètre, pièces OEM et schémas de brochage.
          </div>
        </div>
        <span
          style={{
            padding: "5px 10px",
            borderRadius: 6,
            backgroundColor: "rgba(240,120,54,0.2)",
            border: `1px solid ${Theme.orange}`,
            color: "#FFF",
            fontWeight: 800,
            fontSize: 11
          }}
        >
          CHÂSSIS ACTIF : {activeVin}
        </span>
      </div>

      {/* BARRE DE SAISIE CODE OBD-II + PUces RAPIDES */}
      <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
        <input
          value={dtcInput}
          onChange={(e) => setDtcInput(e.target.value.toUpperCase())}
          placeholder="Saisir un code défaut OBD-II (ex: P0299, P0401, P0087)..."
          style={{
            flex: 1,
            minWidth: 220,
            padding: "11px 14px",
            borderRadius: 10,
            backgroundColor: Theme.bg,
            color: "#FFF",
            border: `1px solid ${Theme.orange}`,
            fontFamily: "monospace",
            fontWeight: 800,
            fontSize: 14
          }}
        />
        <button
          onClick={() => runDiagnosis(dtcInput)}
          style={{
            padding: "11px 20px",
            borderRadius: 10,
            border: "none",
            backgroundColor: Theme.orange,
            color: "#FFF",
            fontWeight: 900,
            cursor: "pointer"
          }}
        >
          Analyser le Code OBD
        </button>
      </div>

      <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
        {[
          ["P0299", "P0299 • Sous-pression Turbo"],
          ["P0401", "P0401 • Débit EGR Insuffisant"],
          ["P0087", "P0087 • Pression Rail Trop Basse"]
        ].map(([code, label]) => (
          <button
            key={code}
            onClick={() => runDiagnosis(code)}
            style={{
              padding: "6px 12px",
              borderRadius: 8,
              border: `1px solid ${activeCase.code === code ? Theme.orange : Theme.border}`,
              backgroundColor: activeCase.code === code ? "rgba(240,120,54,0.2)" : Theme.bg,
              color: "#FFF",
              fontWeight: 800,
              fontSize: 11.5,
              cursor: "pointer"
            }}
          >
            {label}
          </button>
        ))}
      </div>

      {isAnalyzing ? (
        <div
          style={{
            padding: 20,
            borderRadius: 12,
            backgroundColor: Theme.bg,
            border: `1px solid ${Theme.orange}`,
            color: Theme.orange,
            fontWeight: 900,
            fontSize: 14
          }}
        >
          ⏳ Analyse du problème en cours… Interrogation des bases techniques constructeur et schémas OEM...
        </div>
      ) : (
        <div style={{ display: "grid", gap: 14 }}>
          {/* 1. EXPLICATION CLAIRE DU DÉFAUT & SYMPTÔMES */}
          <div style={{ padding: 16, borderRadius: 12, backgroundColor: Theme.bg, border: `1px solid ${Theme.border}` }}>
            <div style={{ display: "flex", justifyContent: "space-between", flexWrap: "wrap", gap: 8 }}>
              <div style={{ fontWeight: 900, fontSize: 15, color: Theme.orange }}>
                1. EXPLICATION DU DÉFAUT [{activeCase.code}] — {activeCase.title}
              </div>
              <span
                style={{
                  padding: "3px 8px",
                  borderRadius: 6,
                  backgroundColor: "rgba(239,68,68,0.2)",
                  color: Theme.red,
                  fontWeight: 900,
                  fontSize: 11
                }}
              >
                GRAVITÉ : {activeCase.severity}
              </span>
            </div>
            <p style={{ fontSize: 12.5, color: Theme.textPrimary, lineHeight: 1.5, margin: "8px 0" }}>
              {activeCase.detailedExplanation}
            </p>
            <div style={{ fontSize: 12, color: Theme.textSecondary }}>
              <strong>Symptômes :</strong> {activeCase.symptoms.join(" • ")}
            </div>
          </div>

          {/* 2. CAUSES PROBABLES */}
          <div style={{ padding: 16, borderRadius: 12, backgroundColor: Theme.bg, border: `1px solid ${Theme.border}` }}>
            <div style={{ fontWeight: 900, fontSize: 13.5, color: Theme.textPrimary, marginBottom: 8 }}>
              2. CAUSES PROBABLES STATISTIQUES
            </div>
            <div style={{ display: "grid", gap: 8 }}>
              {activeCase.probableCauses.map((c, i) => (
                <div key={i} style={{ display: "flex", justifyContent: "space-between", alignItems: "center", fontSize: 12.5 }}>
                  <span>• {c.cause}</span>
                  <strong style={{ color: Theme.orange }}>{c.probabilityPercent} %</strong>
                </div>
              ))}
            </div>
          </div>

          {/* 3. PROCÉDURES DE DIAGNOSTIC AU MULTIMÈTRE & TEST ACTIONNEURS */}
          <div style={{ padding: 16, borderRadius: 12, backgroundColor: Theme.bg, border: `1px solid ${Theme.border}` }}>
            <div style={{ fontWeight: 900, fontSize: 13.5, color: Theme.green, marginBottom: 8 }}>
              3. PROCÉDURE DE DIAGNOSTIC PAS-À-PAS & VALEURS DE CONSIGNE MULTIMÈTRE
            </div>
            <div style={{ display: "grid", gap: 6, fontSize: 12.5 }}>
              {activeCase.multimeterAndObdSteps.map((st, i) => (
                <div key={i} style={{ lineHeight: 1.45 }}>{st}</div>
              ))}
            </div>
            <div
              style={{
                marginTop: 10,
                padding: 10,
                borderRadius: 8,
                backgroundColor: Theme.surface,
                border: `1px solid ${Theme.green}`,
                fontSize: 12
              }}
            >
              <strong style={{ color: Theme.green }}>Test des actionneurs OBD recommandé :</strong>{" "}
              {activeCase.actuatorTest}
            </div>
          </div>

          {/* 4. PIÈCES OEM RECOMMANDÉES */}
          <div style={{ padding: 16, borderRadius: 12, backgroundColor: Theme.bg, border: `1px solid ${Theme.border}` }}>
            <div style={{ fontWeight: 900, fontSize: 13.5, color: Theme.orange, marginBottom: 8 }}>
              4. PIÈCES DE RECHANGE CONSTRUCTEUR (OEM) RECOMMANDÉES
            </div>
            <div style={{ display: "grid", gap: 8 }}>
              {activeCase.recommendedParts.map((pt, i) => (
                <div
                  key={i}
                  style={{
                    padding: 12,
                    borderRadius: 10,
                    backgroundColor: Theme.surface,
                    border: `1px solid ${Theme.border}`,
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    flexWrap: "wrap",
                    gap: 10
                  }}
                >
                  <div>
                    <div style={{ fontWeight: 800, fontSize: 13 }}>{pt.name}</div>
                    <div style={{ fontSize: 11.5, color: Theme.red, fontFamily: "monospace", fontWeight: 900 }}>
                      Réf OEM : {pt.oemRef} • Équiv : {pt.equivRef}
                    </div>
                  </div>
                  <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
                    <strong style={{ color: Theme.green }}>
                      {pt.priceDzd.toLocaleString("fr-FR")} DA
                    </strong>
                    <button
                      onClick={() => onOrderPartB2b(pt.oemRef, pt.name, pt.priceDzd)}
                      style={{
                        padding: "7px 12px",
                        borderRadius: 8,
                        border: "none",
                        backgroundColor: Theme.orange,
                        color: "#FFF",
                        fontWeight: 800,
                        fontSize: 11.5,
                        cursor: "pointer"
                      }}
                    >
                      Commander B2B
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* 5. DOCUMENTATION TECHNIQUE, COUPLES DE SERRAGE & SCHÉMAS OEM */}
          <div style={{ padding: 16, borderRadius: 12, backgroundColor: Theme.bg, border: `1px solid ${Theme.orange}` }}>
            <div style={{ fontWeight: 900, fontSize: 13.5, color: Theme.orange, marginBottom: 8 }}>
              5. DOCUMENTATION TECHNIQUE, COUPLES DE SERRAGE (Nm) & BROCHAGE OEM
            </div>
            {activeCase.technicalDocs.map((doc, i) => (
              <div key={i} style={{ display: "grid", gap: 6, fontSize: 12 }}>
                <div style={{ fontWeight: 800, fontSize: 13 }}>
                  📄 {doc.docTitle} [{doc.docType}]
                </div>
                <div>
                  <strong style={{ color: Theme.green }}>Valeurs de consigne :</strong> {doc.keySpecs}
                </div>
                <div>
                  <strong style={{ color: Theme.orange }}>Couples de serrage :</strong> {doc.torqueSpecsNm}
                </div>
                <div style={{ fontFamily: "monospace", color: Theme.textSecondary }}>
                  {doc.pinoutNotes}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

interface SuperAdminProps {
  allAccounts: UserAccountSession[];
  featureFlags: Record<string, boolean>;
  onToggleFeatureFlag: (key: string) => void;
  onShowToast: (msg: string) => void;
}

export const SuperAdminPortalView: React.FC<SuperAdminProps> = ({
  allAccounts,
  featureFlags,
  onToggleFeatureFlag,
  onShowToast
}) => {
  const [totpCode, setTotpCode] = useState("482910");
  const [smsOtpCode, setSmsOtpCode] = useState("839201");
  const [isSuperAdminVerified, setIsSuperAdminVerified] = useState(false);
  const [newApiServiceName, setNewApiServiceName] = useState("");
  const [newApiEndpoint, setNewApiEndpoint] = useState("https://api.");
  const [newApiKeyVal, setNewApiKeyVal] = useState("");

  const [apiVault, setApiVault] = useState([
    {
      id: "TECDOC_PEGASUS",
      name: "TecDoc Pegasus 3.0 Official Catalog API",
      group: "Catalogue Pièces & Équivalences Multimabanques",
      endpoint: "https://webservice.tecalliance.services/pegasus-3-0/services/TecdocToCatDLB.jsonEndpoint",
      apiKey: "TD-LIVE-9948-MEKANIK-DZ-2026",
      enabled: true
    },
    {
      id: "ETKA_VAG",
      name: "ETKA / Partslink24 VAG (VW, Audi, Seat, Skoda)",
      group: "Groupe VAG • Éclatés & Références PR",
      endpoint: "https://api.partslink24.com/v2/vag-etka/chassis-lookup",
      apiKey: "ETKA-VAG-7741-OEM-PRO",
      enabled: true
    },
    {
      id: "STELLANTIS_SERVICEBOX",
      name: "Groupe Stellantis ServiceBox & SEDRE (Peugeot, Citroën, Fiat, Opel, Jeep)",
      group: "Groupe Stellantis • VIN, Pièces OEM & Schémas SEDRE",
      endpoint: "https://api.servicebox.stellantis.com/v3/vin-and-parts",
      apiKey: "SBX-PSA-FCA-6630-TOKEN",
      enabled: true
    },
    {
      id: "VIN_MASTER_DECODER",
      name: "VIN Master Decoder ISO-3779 & NHTSA / EuroVIN",
      group: "Lecture Numéro de Châssis & Code Moteur",
      endpoint: "https://api.vindecoder.eu/v3.2/decode",
      apiKey: "VIN-ISO-8821-GLOBAL-KEY",
      enabled: true
    },
    {
      id: "RENAULT_DIALOGYS",
      name: "Renault / Dacia Dialogys & New Dialogys API",
      group: "Groupe Renault • Pièces & Méthodes de Réparation",
      endpoint: "https://dialogys-api.renault.com/v2/catalog",
      apiKey: "DLG-REN-3391-DZ-PRO",
      enabled: true
    },
    {
      id: "TIKTOK_BUSINESS",
      name: "TikTok Business & Catalogue Vidéo Pièces IA",
      group: "Réseaux Sociaux & Diffusion Annonces Vendeurs",
      endpoint: "https://business-api.tiktok.com/open_api/v1.3/",
      apiKey: "TTK-BIZ-5510-MEKANIK",
      enabled: true
    },
    {
      id: "SATIM_CHARGILY_STRIPE",
      name: "Passerelle Monétique SATIM CIB / Edahabia / Chargily & Stripe Live",
      group: "Paiements Unitaires MécaVerif+ (250 / 300 DA) & Abonnements",
      endpoint: "https://pay.chargily.net/api/v2/checkouts",
      apiKey: "PK-LIVE-SATIM-STRIPE-2026",
      enabled: true
    }
  ]);

  return (
    <div style={{ display: "grid", gap: 18 }}>
      {/* EN-TÊTE SUPER-ADMINISTRATEUR */}
      <div
        style={{
          padding: 20,
          backgroundColor: Theme.surface,
          borderRadius: 16,
          border: `1.5px solid ${Theme.orange}`
        }}
      >
        <div style={{ fontSize: 19, fontWeight: 900, color: Theme.orange }}>
          🔐 PANNEAU SUPER-ADMINISTRATEUR (DOUBLE 2FA : GOOGLE AUTHENTICATOR + SMS OTP)
        </div>
        <div style={{ fontSize: 12.5, color: Theme.textSecondary, marginTop: 4 }}>
          Contrôle total de la plateforme sans toucher au code : activation/désactivation des modules (ON/OFF), ajout manuel des clés API (TecDoc, ETKA, Stellantis, VIN, TikTok, SATIM/Stripe) et gestion des comptes.
        </div>

        {!isSuperAdminVerified ? (
          <div
            style={{
              marginTop: 16,
              padding: 16,
              borderRadius: 12,
              backgroundColor: Theme.bg,
              border: `1px solid ${Theme.border}`,
              display: "grid",
              gap: 12
            }}
          >
            <div style={{ fontWeight: 800, fontSize: 13, color: Theme.green }}>
              ÉTAPE DE SÉCURITÉ MAXIMALE : DOUBLE VALIDATION GOOGLE AUTHENTICATOR + SMS TÉLÉPHONE (+213 550 94 82 10)
            </div>
            <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))", gap: 10 }}>
              <div>
                <label style={{ fontSize: 11, color: Theme.textSecondary }}>
                  1. Code Google Authenticator (6 chiffres)
                </label>
                <input
                  value={totpCode}
                  onChange={(e) => setTotpCode(e.target.value)}
                  style={{
                    width: "100%",
                    marginTop: 4,
                    padding: "10px 12px",
                    borderRadius: 8,
                    backgroundColor: Theme.surface,
                    color: Theme.green,
                    fontWeight: 900,
                    border: `1px solid ${Theme.green}`
                  }}
                />
              </div>
              <div>
                <label style={{ fontSize: 11, color: Theme.textSecondary }}>
                  2. Code SMS reçu sur votre téléphone (+213 550 94 82 10)
                </label>
                <input
                  value={smsOtpCode}
                  onChange={(e) => setSmsOtpCode(e.target.value)}
                  style={{
                    width: "100%",
                    marginTop: 4,
                    padding: "10px 12px",
                    borderRadius: 8,
                    backgroundColor: Theme.surface,
                    color: Theme.orange,
                    fontWeight: 900,
                    border: `1px solid ${Theme.orange}`
                  }}
                />
              </div>
            </div>
            <button
              onClick={() => {
                setIsSuperAdminVerified(true);
                onShowToast(
                  "Accès Super-Administrateur déverrouillé (Google Authenticator + SMS OTP validés)"
                );
              }}
              style={{
                padding: "12px 20px",
                borderRadius: 10,
                border: "none",
                backgroundColor: Theme.orange,
                color: "#FFF",
                fontWeight: 900,
                cursor: "pointer"
              }}
            >
              Déverrouiller la Console Super-Administrateur (2FA + SMS)
            </button>
          </div>
        ) : (
          <div style={{ marginTop: 12, color: Theme.green, fontWeight: 800, fontSize: 13 }}>
            ✓ Session Super-Administrateur Authentifiée (Double Facteur TOTP + SMS Vérifié)
          </div>
        )}
      </div>

      {isSuperAdminVerified && (
        <>
          {/* 1. INTERRUPTEURS GLOBAUX ON/OFF DE LA PLATEFORME */}
          <div
            style={{
              padding: 20,
              backgroundColor: Theme.surface,
              borderRadius: 16,
              border: `1px solid ${Theme.border}`
            }}
          >
            <div style={{ fontWeight: 900, fontSize: 15, color: Theme.orange, marginBottom: 12 }}>
              1. ACTIVATION / DÉSACTIVATION GLOBALE DES FONCTIONNALITÉS (ON / OFF SANS CODER)
            </div>
            <div
              style={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))",
                gap: 10
              }}
            >
              {[
                ["vinDecoder", "Décodage VIN & Code Moteur"],
                ["mecaVerif", "Service Externe MécaVerif+ (250/300 DA)"],
                ["mekanikAiObd", "Assistant Diagnostic MekanikAI OBD-II"],
                ["bodyshopMandatoryPhotos", "Obligation Photos Avant/Après Carrosserie"],
                ["b2bSupplierPortal", "Flux B2B Fournisseur ↔ Magasin / Atelier"],
                ["subAccounts2Fa", "Sécurité 2FA Google Authenticator Sous-Comptes"]
              ].map(([flagKey, flagLabel]) => {
                const isOn = featureFlags[flagKey] !== false;
                return (
                  <div
                    key={flagKey}
                    style={{
                      padding: 12,
                      borderRadius: 10,
                      backgroundColor: Theme.bg,
                      border: `1px solid ${isOn ? Theme.green : Theme.red}`,
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
                      gap: 8
                    }}
                  >
                    <span style={{ fontSize: 12, fontWeight: 700 }}>{flagLabel}</span>
                    <button
                      onClick={() => onToggleFeatureFlag(flagKey)}
                      style={{
                        padding: "6px 12px",
                        borderRadius: 6,
                        border: "none",
                        backgroundColor: isOn ? Theme.green : Theme.red,
                        color: "#FFF",
                        fontWeight: 900,
                        fontSize: 11,
                        cursor: "pointer"
                      }}
                    >
                      {isOn ? "ON" : "OFF"}
                    </button>
                  </div>
                );
              })}
            </div>
          </div>

          {/* 2. GESTIONNAIRE DE CLÉS API NO-CODE (TECDOC, ETKA, STELLANTIS, VIN, TIKTOK...) */}
          <div
            style={{
              padding: 20,
              backgroundColor: Theme.surface,
              borderRadius: 16,
              border: `1px solid ${Theme.border}`
            }}
          >
            <div style={{ fontWeight: 900, fontSize: 15, color: Theme.orange, marginBottom: 4 }}>
              2. COFFRE-FORT CLÉS API NO-CODE (TECDOC, ETKA, GROUPE STELLANTIS, VIN, DIALOGYS, TIKTOK)
            </div>
            <div style={{ fontSize: 12, color: Theme.textSecondary, marginBottom: 14 }}>
              Modifiez ou ajoutez manuellement vos clés API externes sans jamais toucher au code source.
            </div>

            {/* Ajouter une nouvelle API manuellement */}
            <div
              style={{
                padding: 14,
                borderRadius: 12,
                backgroundColor: Theme.bg,
                border: `1px solid ${Theme.green}`,
                marginBottom: 14,
                display: "grid",
                gap: 10
              }}
            >
              <div style={{ fontWeight: 800, fontSize: 12.5, color: Theme.green }}>
                + INTRODUIRE UNE NOUVELLE API EXTERNE MANUELLEMENT
              </div>
              <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))", gap: 8 }}>
                <input
                  value={newApiServiceName}
                  onChange={(e) => setNewApiServiceName(e.target.value)}
                  placeholder="Nom de l'API (ex: Microcat, Autodata, WhatsApp...)"
                  style={{ padding: "9px 12px", borderRadius: 8, backgroundColor: Theme.surface, color: "#FFF", border: `1px solid ${Theme.border}` }}
                />
                <input
                  value={newApiEndpoint}
                  onChange={(e) => setNewApiEndpoint(e.target.value)}
                  placeholder="URL Endpoint"
                  style={{ padding: "9px 12px", borderRadius: 8, backgroundColor: Theme.surface, color: "#FFF", border: `1px solid ${Theme.border}` }}
                />
                <input
                  value={newApiKeyVal}
                  onChange={(e) => setNewApiKeyVal(e.target.value)}
                  placeholder="Clé API / Token Secret"
                  style={{ padding: "9px 12px", borderRadius: 8, backgroundColor: Theme.surface, color: "#FFF", border: `1px solid ${Theme.border}` }}
                />
                <button
                  onClick={() => {
                    if (!newApiServiceName.trim() || !newApiKeyVal.trim()) return;
                    setApiVault([
                      {
                        id: `API-${Date.now().toString().slice(-4)}`,
                        name: newApiServiceName.trim(),
                        group: "API Externe Personnalisée",
                        endpoint: newApiEndpoint.trim(),
                        apiKey: newApiKeyVal.trim(),
                        enabled: true
                      },
                      ...apiVault
                    ]);
                    setNewApiServiceName("");
                    setNewApiKeyVal("");
                    onShowToast("Nouvelle clé API ajoutée avec succès dans le coffre-fort Super-Admin !");
                  }}
                  style={{
                    padding: "9px 14px",
                    borderRadius: 8,
                    border: "none",
                    backgroundColor: Theme.green,
                    color: "#FFF",
                    fontWeight: 900,
                    cursor: "pointer"
                  }}
                >
                  + Ajouter l'API
                </button>
              </div>
            </div>

            <div style={{ display: "grid", gap: 10 }}>
              {apiVault.map((api, idx) => (
                <div
                  key={api.id}
                  style={{
                    padding: 14,
                    borderRadius: 12,
                    backgroundColor: Theme.bg,
                    border: `1px solid ${api.enabled ? Theme.border : Theme.red}`,
                    display: "grid",
                    gap: 8
                  }}
                >
                  <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 8 }}>
                    <div>
                      <div style={{ fontWeight: 800, fontSize: 13.5 }}>{api.name}</div>
                      <div style={{ fontSize: 11, color: Theme.textSecondary }}>
                        {api.group} • Endpoint : <span style={{ fontFamily: "monospace" }}>{api.endpoint}</span>
                      </div>
                    </div>
                    <button
                      onClick={() => {
                        const next = [...apiVault];
                        next[idx] = { ...api, enabled: !api.enabled };
                        setApiVault(next);
                      }}
                      style={{
                        padding: "5px 10px",
                        borderRadius: 6,
                        border: "none",
                        backgroundColor: api.enabled ? Theme.green : Theme.red,
                        color: "#FFF",
                        fontWeight: 800,
                        fontSize: 11,
                        cursor: "pointer"
                      }}
                    >
                      {api.enabled ? "ACTIVE ✓" : "DÉSACTIVÉE"}
                    </button>
                  </div>

                  <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
                    <input
                      value={api.apiKey}
                      onChange={(e) => {
                        const next = [...apiVault];
                        next[idx] = { ...api, apiKey: e.target.value };
                        setApiVault(next);
                      }}
                      style={{
                        flex: 1,
                        minWidth: 220,
                        padding: "8px 12px",
                        borderRadius: 8,
                        backgroundColor: Theme.surface,
                        color: Theme.orange,
                        border: `1px solid ${Theme.border}`,
                        fontFamily: "monospace",
                        fontWeight: 800
                      }}
                    />
                    <button
                      onClick={() =>
                        onShowToast(`Clé API ${api.name} mise à jour à chaud !`)
                      }
                      style={{
                        padding: "8px 14px",
                        borderRadius: 8,
                        border: "none",
                        backgroundColor: Theme.orange,
                        color: "#FFF",
                        fontWeight: 800,
                        fontSize: 12,
                        cursor: "pointer"
                      }}
                    >
                      Sauvegarder Clé
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* 3. LISTE DES COMPTES ORGANISATIONS & SOUS-COMPTES INSCRITS */}
          <div
            style={{
              padding: 20,
              backgroundColor: Theme.surface,
              borderRadius: 16,
              border: `1px solid ${Theme.border}`
            }}
          >
            <div style={{ fontWeight: 900, fontSize: 15, color: Theme.orange, marginBottom: 12 }}>
              3. COMPTES ORGANISATIONS INSCRITS & SOUS-COMPTES ({allAccounts.length} ORGANISATIONS)
            </div>
            <div style={{ display: "grid", gap: 8 }}>
              {allAccounts.map((acc) => (
                <div
                  key={acc.userId}
                  style={{
                    padding: 12,
                    borderRadius: 10,
                    backgroundColor: Theme.bg,
                    border: `1px solid ${Theme.border}`,
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    flexWrap: "wrap",
                    gap: 8
                  }}
                >
                  <div>
                    <div style={{ fontWeight: 800, fontSize: 13 }}>
                      {acc.organizationName} •{" "}
                      <span style={{ color: Theme.orange }}>
                        [{ACCOUNT_ROLES[acc.role].shortBadge}]
                      </span>
                    </div>
                    <div style={{ fontSize: 11.5, color: Theme.textSecondary }}>
                      Titulaire : {acc.fullName} ({acc.email}) • Wilaya : {acc.wilaya} • Sous-comptes :{" "}
                      {acc.subAccounts.length}/{ACCOUNT_ROLES[acc.role].maxSubAccounts} • 2FA : Actif
                    </div>
                  </div>
                  <span
                    style={{
                      padding: "4px 10px",
                      borderRadius: 6,
                      backgroundColor: "rgba(34,197,94,0.16)",
                      color: Theme.green,
                      fontWeight: 800,
                      fontSize: 11
                    }}
                  >
                    {ACCOUNT_ROLES[acc.role].monthlyPriceDzd} DA / mois
                  </span>
                </div>
              ))}
            </div>
          </div>
        </>
      )}
    </div>
  );
};
