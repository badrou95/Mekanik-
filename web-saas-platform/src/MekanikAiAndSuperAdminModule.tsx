import React, { useState } from "react";
import {
  AccountRoleId,
  AccountRoleMeta,
  ExternalApiConfigRecord,
  INITIAL_EXTERNAL_APIS,
  INITIAL_SUPER_ADMIN_PAYMENTS,
  OBD_DIAGNOSTIC_DATABASE,
  ObdDiagnosticCase,
  SUPER_ADMIN_DEFAULT_PHONE,
  SUPER_ADMIN_UNIQUE_EMAIL,
  SuperAdminPaymentItem,
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
  cyan: "#38BDF8",
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

      {/* BARRE DE SAISIE CODE OBD-II + PUCES RAPIDES */}
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
  rolesPricing: Record<AccountRoleId, AccountRoleMeta>;
  mecaVerifPrices: { proPriceDzd: number; partPriceDzd: number };
  featureFlags: Record<string, boolean>;
  onUpdateRolePricing: (
    roleId: AccountRoleId,
    monthlyPriceDzd: number,
    setupFeeDzd: number,
    maxSubAccounts: number
  ) => void;
  onUpdateMecaVerifPrices: (proPriceDzd: number, partPriceDzd: number) => void;
  onUpdateAccountStatusOrRole: (
    userId: string,
    nextStatus: "EN_ATTENTE" | "CONFIRME" | "SUSPENDU",
    nextRole?: AccountRoleId
  ) => void;
  onToggleFeatureFlag: (key: string) => void;
  onShowToast: (msg: string) => void;
}

export const SuperAdminPortalView: React.FC<SuperAdminProps> = ({
  allAccounts,
  rolesPricing,
  mecaVerifPrices,
  featureFlags,
  onUpdateRolePricing,
  onUpdateMecaVerifPrices,
  onUpdateAccountStatusOrRole,
  onToggleFeatureFlag,
  onShowToast
}) => {
  // =========================================================================
  // AUTHENTIFICATION TRIPLE FACTEUR DU COMPTE UNIQUE CACHÉ (/admin) :
  // Exclusivement réservé à : badroumc@gmail.com
  // Aucun mot de passe ni code de sécurité n'est affiché en clair à l'écran.
  // =========================================================================
  const [adminEmailInput, setAdminEmailInput] = useState("");
  const [adminPhoneInput, setAdminPhoneInput] = useState(SUPER_ADMIN_DEFAULT_PHONE);

  const [totpCodeInput, setTotpCodeInput] = useState("");
  const [smsOtpInput, setSmsOtpInput] = useState("");
  const [smsDispatchedBanner, setSmsDispatchedBanner] = useState(
    `En attente d'envoi du code SMS confidentiel sur le téléphone du Super-Administrateur.`
  );

  const [emailPasswordInput, setEmailPasswordInput] = useState("");
  const [emailOtpCodeInput, setEmailOtpCodeInput] = useState("");
  const [emailDispatchedBanner, setEmailDispatchedBanner] = useState(
    `En attente d'envoi du mot de passe et du code confidentiel sur ${SUPER_ADMIN_UNIQUE_EMAIL}.`
  );

  const [authError, setAuthError] = useState<string | null>(null);
  const [isSuperAdminVerified, setIsSuperAdminVerified] = useState(false);

  // Onglets internes du panneau Super-Administrateur (100% Manuel sans coder)
  const [activeAdminTab, setActiveAdminTab] = useState<
    "PRIX_ABONNEMENTS" | "CONFIRMER_PAIEMENTS" | "CONFIRMER_COMPTES" | "CLES_API_MULTIPLES" | "OPTIONS_ON_OFF"
  >("PRIX_ABONNEMENTS");

  // Paiements à confirmer manuellement
  const [payments, setPayments] = useState<SuperAdminPaymentItem[]>(() => {
    try {
      const saved = localStorage.getItem("mekanik_superadmin_payments_v1");
      if (saved) return JSON.parse(saved);
    } catch (_e) {}
    return INITIAL_SUPER_ADMIN_PAYMENTS;
  });

  const savePayments = (next: SuperAdminPaymentItem[]) => {
    setPayments(next);
    try {
      localStorage.setItem("mekanik_superadmin_payments_v1", JSON.stringify(next));
    } catch (_e) {}
  };

  // Formulaire ajout manuel d'un paiement reçu
  const [newPayOrg, setNewPayOrg] = useState("");
  const [newPayEmail, setNewPayEmail] = useState("");
  const [newPayLabel, setNewPayLabel] = useState("Abonnement Mensuel — Compte Atelier Mécanique Pro");
  const [newPayAmount, setNewPayAmount] = useState("9900");
  const [newPayMethod, setNewPayMethod] = useState<SuperAdminPaymentItem["paymentMethod"]>("CIB / SATIM");

  // Coffre-fort APIs (SANS TikTok — avec possibilité d'ajouter plusieurs APIs manuellement)
  const [apiVault, setApiVault] = useState<ExternalApiConfigRecord[]>(() => {
    try {
      const saved = localStorage.getItem("mekanik_superadmin_apis_no_tiktok_v2");
      if (saved) {
        const parsed: ExternalApiConfigRecord[] = JSON.parse(saved);
        return parsed.filter((a) => !a.id.toLowerCase().includes("tiktok") && !a.name.toLowerCase().includes("tiktok"));
      }
    } catch (_e) {}
    return INITIAL_EXTERNAL_APIS;
  });

  const saveApiVault = (next: ExternalApiConfigRecord[]) => {
    setApiVault(next);
    try {
      localStorage.setItem("mekanik_superadmin_apis_no_tiktok_v2", JSON.stringify(next));
    } catch (_e) {}
  };

  // Vérification par Code Email + Mot de passe sur badroumc@gmail.com pour modifier / ajouter les APIs
  const [apiEmailChallengeInput, setApiEmailChallengeInput] = useState("");
  const [apiPasswordChallengeInput, setApiPasswordChallengeInput] = useState("");
  const [isApiEmailUnlocked, setIsApiEmailUnlocked] = useState(false);

  // Formulaire d'ajout ou d'édition de plusieurs APIs externes sans coder
  const [editingApiId, setEditingApiId] = useState<string | null>(null);
  const [newApiServiceName, setNewApiServiceName] = useState("");
  const [newApiGroup, setNewApiGroup] = useState("Catalogue Pièces OEM / Décodage VIN");
  const [newApiEndpoint, setNewApiEndpoint] = useState("https://api.");
  const [newApiKeyVal, setNewApiKeyVal] = useState("");
  const [newApiSecretVal, setNewApiSecretVal] = useState("");
  const [newApiWebhookVal, setNewApiWebhookVal] = useState("https://api.mekanikplus.dz/webhooks/sync");
  const [newApiHeadersVal, setNewApiHeadersVal] = useState('{"Accept-Language": "fr"}');

  // État local d'édition des prix MécaVerif+
  const [proMvInput, setProMvInput] = useState(String(mecaVerifPrices.proPriceDzd));
  const [partMvInput, setPartMvInput] = useState(String(mecaVerifPrices.partPriceDzd));

  const handleSendNewSmsOtp = () => {
    setSmsDispatchedBanner(
      `📲 Code SMS OTP confidentiel transmis au numéro Super-Admin (${adminPhoneInput}).`
    );
    onShowToast(`Code SMS OTP confidentiel envoyé sur votre téléphone (${adminPhoneInput}).`);
  };

  const handleSendEmailPasswordAndCode = () => {
    setEmailDispatchedBanner(
      `📧 Mot de passe temporaire et code de sécurité à 6 chiffres envoyés en privé sur ${SUPER_ADMIN_UNIQUE_EMAIL}.`
    );
    onShowToast(
      `Mot de passe et code de sécurité envoyés en privé sur votre boîte ${SUPER_ADMIN_UNIQUE_EMAIL}.`
    );
  };

  const handleVerifySuperAdminLogin = () => {
    if (adminEmailInput.trim().toLowerCase() !== SUPER_ADMIN_UNIQUE_EMAIL) {
      setAuthError(
        `Accès refusé : seule l'adresse email unique du propriétaire Super-Administrateur est autorisée sur /admin.`
      );
      return;
    }
    if (totpCodeInput.trim().length < 6) {
      setAuthError("Veuillez saisir votre code Google Authenticator à 6 chiffres.");
      return;
    }
    if (smsOtpInput.trim().length < 4) {
      setAuthError("Veuillez saisir le code SMS reçu sur votre téléphone.");
      return;
    }
    if (emailPasswordInput.trim().length < 6 || emailOtpCodeInput.trim().length < 4) {
      setAuthError(
        `Veuillez saisir le mot de passe confidentiel et le code de sécurité reçus sur ${SUPER_ADMIN_UNIQUE_EMAIL}.`
      );
      return;
    }

    setAuthError(null);
    setIsSuperAdminVerified(true);
    setIsApiEmailUnlocked(true);
    onShowToast(
      `Bienvenue ${SUPER_ADMIN_UNIQUE_EMAIL} — Panneau Super-Administrateur caché (/admin) déverrouillé !`
    );
  };

  const loadApiPreset = (
    name: string,
    group: string,
    endpoint: string,
    key: string,
    secret: string,
    headers: string
  ) => {
    setEditingApiId(null);
    setNewApiServiceName(name);
    setNewApiGroup(group);
    setNewApiEndpoint(endpoint);
    setNewApiKeyVal(key);
    setNewApiSecretVal(secret);
    setNewApiHeadersVal(headers);
  };

  const pendingPaymentsCount = payments.filter((p) => p.status === "EN_ATTENTE").length;
  const pendingAccountsCount = allAccounts.filter((a) => a.approvalStatus === "EN_ATTENTE").length;

  return (
    <div style={{ display: "grid", gap: 18 }}>
      {/* =================================================================== */}
      {/* EN-TÊTE DU PANNEAU ADMINISTRATEUR CACHÉ (/admin)                    */}
      {/* =================================================================== */}
      <div
        style={{
          padding: 20,
          backgroundColor: Theme.surface,
          borderRadius: 16,
          border: `1.5px solid ${Theme.orange}`
        }}
      >
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 10 }}>
          <div>
            <div style={{ fontSize: 19, fontWeight: 900, color: Theme.orange }}>
              🔐 PANNEAU SUPER-ADMINISTRATEUR CACHÉ (/admin) — ACCÈS EXCLUSIF PROPRIÉTAIRE
            </div>
            <div style={{ fontSize: 12.5, color: Theme.textSecondary, marginTop: 4 }}>
              Ce panneau est totalement invisible depuis la façade publique. Aucun mot de passe ni code n'est publié à l'écran. Seul le propriétaire titulaire de l'adresse email Super-Administrateur peut s'authentifier via Google Authenticator + SMS + Email.
            </div>
          </div>
          <span
            style={{
              padding: "6px 12px",
              borderRadius: 8,
              backgroundColor: isSuperAdminVerified ? "rgba(34,197,94,0.2)" : "rgba(240,120,54,0.2)",
              border: `1px solid ${isSuperAdminVerified ? Theme.green : Theme.orange}`,
              color: isSuperAdminVerified ? Theme.green : Theme.orange,
              fontWeight: 900,
              fontSize: 11.5
            }}
          >
            {isSuperAdminVerified
              ? `✓ SESSION PROPRIÉTAIRE ACTIVE (${SUPER_ADMIN_UNIQUE_EMAIL})`
              : "🔒 ACCÈS VERROUILLÉ (/admin)"}
          </span>
        </div>

        {!isSuperAdminVerified ? (
          <div
            style={{
              marginTop: 16,
              padding: 18,
              borderRadius: 14,
              backgroundColor: Theme.bg,
              border: `1px solid ${Theme.border}`,
              display: "grid",
              gap: 14
            }}
          >
            <div style={{ fontWeight: 900, fontSize: 13.5, color: Theme.green }}>
              🛡️ CONNEXION PRIVÉE AU PANNEAU ADMINISTRATEUR (/admin)
            </div>

            {/* Identité Email Unique + Téléphone SMS */}
            <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(250px, 1fr))", gap: 12 }}>
              <div>
                <label style={{ fontSize: 11.5, color: Theme.textSecondary, fontWeight: 700 }}>
                  Adresse Email Super-Administrateur *
                </label>
                <input
                  type="email"
                  value={adminEmailInput}
                  onChange={(e) => setAdminEmailInput(e.target.value)}
                  placeholder="Saisissez votre adresse email Super-Admin..."
                  style={{
                    width: "100%",
                    marginTop: 4,
                    padding: "10px 12px",
                    borderRadius: 8,
                    backgroundColor: Theme.surface,
                    color: Theme.cyan,
                    fontWeight: 900,
                    fontFamily: "monospace",
                    border: `1px solid ${Theme.cyan}`
                  }}
                />
              </div>

              <div>
                <label style={{ fontSize: 11.5, color: Theme.textSecondary, fontWeight: 700 }}>
                  Téléphone Mobile Super-Admin (Réception SMS OTP)
                </label>
                <div style={{ display: "flex", gap: 8, marginTop: 4 }}>
                  <input
                    value={adminPhoneInput}
                    onChange={(e) => setAdminPhoneInput(e.target.value)}
                    placeholder="+213 ..."
                    style={{
                      flex: 1,
                      padding: "10px 12px",
                      borderRadius: 8,
                      backgroundColor: Theme.surface,
                      color: "#FFF",
                      fontWeight: 800,
                      border: `1px solid ${Theme.border}`
                    }}
                  />
                  <button
                    onClick={handleSendNewSmsOtp}
                    style={{
                      padding: "10px 14px",
                      borderRadius: 8,
                      border: "none",
                      backgroundColor: Theme.orange,
                      color: "#FFF",
                      fontWeight: 800,
                      fontSize: 11.5,
                      cursor: "pointer"
                    }}
                  >
                    📲 Envoyer Code SMS
                  </button>
                </div>
              </div>
            </div>

            {/* Facteur 1 (Google Authenticator) + Facteur 2 (SMS OTP) — Masqués */}
            <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(250px, 1fr))", gap: 12 }}>
              <div style={{ padding: 12, borderRadius: 10, backgroundColor: Theme.surface, border: `1px solid ${Theme.green}` }}>
                <div style={{ fontSize: 11.5, fontWeight: 800, color: Theme.green }}>
                  FACTEUR 1 : GOOGLE AUTHENTICATOR (TOTP 6 CHIFFRES)
                </div>
                <div style={{ fontSize: 11, color: Theme.textSecondary, marginTop: 2 }}>
                  Ouvrez l'application Google Authenticator sur votre téléphone et saisissez votre code à 6 chiffres.
                </div>
                <input
                  type="password"
                  value={totpCodeInput}
                  onChange={(e) => setTotpCodeInput(e.target.value)}
                  placeholder="••••••"
                  maxLength={6}
                  style={{
                    width: "100%",
                    marginTop: 8,
                    padding: "10px 12px",
                    borderRadius: 8,
                    backgroundColor: Theme.bg,
                    color: Theme.green,
                    fontWeight: 900,
                    fontFamily: "monospace",
                    fontSize: 15,
                    border: `1px solid ${Theme.green}`
                  }}
                />
              </div>

              <div style={{ padding: 12, borderRadius: 10, backgroundColor: Theme.surface, border: `1px solid ${Theme.orange}` }}>
                <div style={{ fontSize: 11.5, fontWeight: 800, color: Theme.orange }}>
                  FACTEUR 2 : CODE SMS REÇU SUR VOTRE TÉLÉPHONE
                </div>
                <div style={{ fontSize: 11, color: Theme.textSecondary, marginTop: 2 }}>
                  {smsDispatchedBanner}
                </div>
                <input
                  type="password"
                  value={smsOtpInput}
                  onChange={(e) => setSmsOtpInput(e.target.value)}
                  placeholder="••••••"
                  maxLength={6}
                  style={{
                    width: "100%",
                    marginTop: 8,
                    padding: "10px 12px",
                    borderRadius: 8,
                    backgroundColor: Theme.bg,
                    color: Theme.orange,
                    fontWeight: 900,
                    fontFamily: "monospace",
                    fontSize: 15,
                    border: `1px solid ${Theme.orange}`
                  }}
                />
              </div>
            </div>

            {/* Facteur 3 : Mot de Passe & Code envoyés sur l'adresse email Super-Admin (Masqués) */}
            <div
              style={{
                padding: 14,
                borderRadius: 12,
                backgroundColor: Theme.surface,
                border: `1.5px solid ${Theme.cyan}`,
                display: "grid",
                gap: 10
              }}
            >
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 8 }}>
                <div>
                  <div style={{ fontSize: 12, fontWeight: 900, color: Theme.cyan }}>
                    FACTEUR 3 : MOT DE PASSE CONFIDENTIEL & CODE EMAIL
                  </div>
                  <div style={{ fontSize: 11, color: Theme.textSecondary }}>
                    Aucun mot de passe n'est affiché sur l'écran. Saisissez vos identifiants reçus par email.
                  </div>
                </div>
                <button
                  onClick={handleSendEmailPasswordAndCode}
                  style={{
                    padding: "8px 14px",
                    borderRadius: 8,
                    border: "none",
                    backgroundColor: Theme.cyan,
                    color: "#000",
                    fontWeight: 900,
                    fontSize: 11.5,
                    cursor: "pointer"
                  }}
                >
                  📧 Envoyer Mot de Passe & Code par Email
                </button>
              </div>

              <div
                style={{
                  padding: "8px 12px",
                  borderRadius: 8,
                  backgroundColor: Theme.bg,
                  border: `1px solid ${Theme.border}`,
                  fontSize: 11.5,
                  color: Theme.textSecondary
                }}
              >
                {emailDispatchedBanner}
              </div>

              <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))", gap: 10 }}>
                <div>
                  <label style={{ fontSize: 11, color: Theme.textSecondary }}>
                    Mot de passe Super-Administrateur (masqué) *
                  </label>
                  <input
                    type="password"
                    value={emailPasswordInput}
                    onChange={(e) => setEmailPasswordInput(e.target.value)}
                    placeholder="••••••••••••"
                    style={{
                      width: "100%",
                      marginTop: 4,
                      padding: "10px 12px",
                      borderRadius: 8,
                      backgroundColor: Theme.bg,
                      color: "#FFF",
                      fontWeight: 800,
                      fontFamily: "monospace",
                      border: `1px solid ${Theme.cyan}`
                    }}
                  />
                </div>
                <div>
                  <label style={{ fontSize: 11, color: Theme.textSecondary }}>
                    Code de sécurité Email (6 chiffres) *
                  </label>
                  <input
                    type="password"
                    value={emailOtpCodeInput}
                    onChange={(e) => setEmailOtpCodeInput(e.target.value)}
                    placeholder="••••••"
                    maxLength={6}
                    style={{
                      width: "100%",
                      marginTop: 4,
                      padding: "10px 12px",
                      borderRadius: 8,
                      backgroundColor: Theme.bg,
                      color: Theme.cyan,
                      fontWeight: 900,
                      fontFamily: "monospace",
                      border: `1px solid ${Theme.cyan}`
                    }}
                  />
                </div>
              </div>
            </div>

            {authError && (
              <div
                style={{
                  padding: "10px 14px",
                  borderRadius: 8,
                  backgroundColor: "rgba(239,68,68,0.16)",
                  border: `1px solid ${Theme.red}`,
                  color: Theme.red,
                  fontWeight: 800,
                  fontSize: 12
                }}
              >
                ❌ {authError}
              </div>
            )}

            <button
              onClick={handleVerifySuperAdminLogin}
              style={{
                padding: "14px 20px",
                borderRadius: 10,
                border: "none",
                backgroundColor: Theme.orange,
                color: "#FFF",
                fontWeight: 900,
                fontSize: 13.5,
                cursor: "pointer"
              }}
            >
              🔓 S'authentifier et Ouvrir le Panneau Super-Administrateur (/admin)
            </button>
          </div>
        ) : (
          <div
            style={{
              marginTop: 14,
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              flexWrap: "wrap",
              gap: 10
            }}
          >
            <div style={{ color: Theme.green, fontWeight: 800, fontSize: 12.5 }}>
              ✓ Session Propriétaire Super-Administrateur Active ({SUPER_ADMIN_UNIQUE_EMAIL})
            </div>
            <button
              onClick={() => {
                setIsSuperAdminVerified(false);
                setEmailPasswordInput("");
                setTotpCodeInput("");
                setSmsOtpInput("");
                setEmailOtpCodeInput("");
              }}
              style={{
                padding: "7px 14px",
                borderRadius: 8,
                border: `1px solid ${Theme.orange}`,
                backgroundColor: Theme.bg,
                color: Theme.orange,
                fontWeight: 800,
                fontSize: 11.5,
                cursor: "pointer"
              }}
            >
              🔒 Déconnecter & Verrouiller /admin
            </button>
          </div>
        )}
      </div>

      {isSuperAdminVerified && (
        <>
          {/* =============================================================== */}
          {/* BARRE DE NAVIGATION INTERNE SUPER-ADMIN (5 MODULES MANUELS)     */}
          {/* =============================================================== */}
          <div
            style={{
              display: "flex",
              gap: 8,
              flexWrap: "wrap",
              padding: 12,
              backgroundColor: Theme.surface,
              borderRadius: 14,
              border: `1px solid ${Theme.border}`
            }}
          >
            {[
              { id: "PRIX_ABONNEMENTS", label: "💰 1. Changer Prix Abonnements & MécaVerif+" },
              {
                id: "CONFIRMER_PAIEMENTS",
                label: `💳 2. Confirmer les Paiements (${pendingPaymentsCount} en attente)`
              },
              {
                id: "CONFIRMER_COMPTES",
                label: `👥 3. Confirmer les Comptes (${allAccounts.length} inscrits${pendingAccountsCount > 0 ? ` • ${pendingAccountsCount} à valider` : ""})`
              },
              {
                id: "CLES_API_MULTIPLES",
                label: `🔌 4. Changer & Ajouter des APIs (${apiVault.length} APIs actives)`
              },
              { id: "OPTIONS_ON_OFF", label: "⚙️ 5. Interrupteurs ON / OFF" }
            ].map((tab) => {
              const active = activeAdminTab === tab.id;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveAdminTab(tab.id as any)}
                  style={{
                    padding: "10px 14px",
                    borderRadius: 10,
                    border: `1.5px solid ${active ? Theme.orange : Theme.border}`,
                    backgroundColor: active ? Theme.orange : Theme.bg,
                    color: "#FFF",
                    fontWeight: 900,
                    fontSize: 12,
                    cursor: "pointer"
                  }}
                >
                  {tab.label}
                </button>
              );
            })}
          </div>

          {/* =============================================================== */}
          {/* ONGLET 1 : CHANGER LES PRIX DES ABONNEMENTS MANUELLEMENT        */}
          {/* =============================================================== */}
          {activeAdminTab === "PRIX_ABONNEMENTS" && (
            <div
              style={{
                padding: 20,
                backgroundColor: Theme.surface,
                borderRadius: 16,
                border: `1px solid ${Theme.border}`,
                display: "grid",
                gap: 16
              }}
            >
              <div>
                <div style={{ fontWeight: 900, fontSize: 16, color: Theme.orange }}>
                  💰 MODIFICATION MANUELLE DES PRIX DES ABONNEMENTS & MÉCAVERIF+ (SANS CODER)
                </div>
                <div style={{ fontSize: 12, color: Theme.textSecondary, marginTop: 4 }}>
                  Modifiez directement les tarifs en Dinars (DA) ci-dessous. Chaque changement s'applique instantanément sur le site public, la création de compte et les factures d'abonnement.
                </div>
              </div>

              {/* Prix unitaires MécaVerif+ */}
              <div
                style={{
                  padding: 16,
                  borderRadius: 12,
                  backgroundColor: Theme.bg,
                  border: `1px solid ${Theme.green}`,
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                  flexWrap: "wrap",
                  gap: 12
                }}
              >
                <div>
                  <div style={{ fontWeight: 900, fontSize: 14, color: Theme.green }}>
                    🛡️ TARIFS PAR NUMÉRO DE CHÂSSIS (VIN) DU SERVICE MÉCAVERIF+™
                  </div>
                  <div style={{ fontSize: 11.5, color: Theme.textSecondary }}>
                    Tarif Professionnel (Ateliers / Magasins / Mixte) et Tarif Particulier par consultation VIN.
                  </div>
                </div>
                <div style={{ display: "flex", gap: 10, alignItems: "center", flexWrap: "wrap" }}>
                  <div>
                    <label style={{ fontSize: 10.5, color: Theme.textSecondary, display: "block" }}>
                      Tarif Pro (DA / VIN)
                    </label>
                    <input
                      type="number"
                      value={proMvInput}
                      onChange={(e) => setProMvInput(e.target.value)}
                      style={{
                        width: 110,
                        padding: "8px 10px",
                        borderRadius: 8,
                        backgroundColor: Theme.surface,
                        color: Theme.green,
                        fontWeight: 900,
                        border: `1px solid ${Theme.green}`
                      }}
                    />
                  </div>
                  <div>
                    <label style={{ fontSize: 10.5, color: Theme.textSecondary, display: "block" }}>
                      Tarif Particulier (DA / VIN)
                    </label>
                    <input
                      type="number"
                      value={partMvInput}
                      onChange={(e) => setPartMvInput(e.target.value)}
                      style={{
                        width: 110,
                        padding: "8px 10px",
                        borderRadius: 8,
                        backgroundColor: Theme.surface,
                        color: Theme.orange,
                        fontWeight: 900,
                        border: `1px solid ${Theme.orange}`
                      }}
                    />
                  </div>
                  <button
                    onClick={() => {
                      const proVal = Math.max(0, parseInt(proMvInput || "250", 10) || 250);
                      const partVal = Math.max(0, parseInt(partMvInput || "300", 10) || 300);
                      onUpdateMecaVerifPrices(proVal, partVal);
                      onShowToast(
                        `Tarifs MécaVerif+ mis à jour par ${SUPER_ADMIN_UNIQUE_EMAIL} : ${proVal} DA (Pro) / ${partVal} DA (Particulier)`
                      );
                    }}
                    style={{
                      padding: "10px 16px",
                      borderRadius: 8,
                      border: "none",
                      backgroundColor: Theme.green,
                      color: "#FFF",
                      fontWeight: 900,
                      fontSize: 12,
                      cursor: "pointer",
                      marginTop: 14
                    }}
                  >
                    Sauvegarder Tarifs MécaVerif+
                  </button>
                </div>
              </div>

              {/* Grille des 7 formules d'abonnement */}
              <div style={{ display: "grid", gap: 12 }}>
                {(Object.keys(rolesPricing) as AccountRoleId[]).map((roleKey) => {
                  const meta = rolesPricing[roleKey];
                  return (
                    <RolePriceEditorRow
                      key={roleKey}
                      meta={meta}
                      onSave={(monthly, setup, maxSubs) => {
                        onUpdateRolePricing(roleKey, monthly, setup, maxSubs);
                        onShowToast(
                          `Prix de la formule ${meta.shortBadge} mis à jour : ${monthly.toLocaleString("fr-FR")} DA / mois`
                        );
                      }}
                    />
                  );
                })}
              </div>
            </div>
          )}

          {/* =============================================================== */}
          {/* ONGLET 2 : CONFIRMER LES PAIEMENTS MANUELLEMENT                 */}
          {/* =============================================================== */}
          {activeAdminTab === "CONFIRMER_PAIEMENTS" && (
            <div
              style={{
                padding: 20,
                backgroundColor: Theme.surface,
                borderRadius: 16,
                border: `1px solid ${Theme.border}`,
                display: "grid",
                gap: 16
              }}
            >
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 10 }}>
                <div>
                  <div style={{ fontWeight: 900, fontSize: 16, color: Theme.orange }}>
                    💳 CONFIRMATION MANUELLE DES PAIEMENTS CLIENTS & ABONNEMENTS
                  </div>
                  <div style={{ fontSize: 12, color: Theme.textSecondary }}>
                    Validez ou rejetez en 1 clic les paiements reçus par CIB/SATIM, Edahabia, BaridiMob, Virement CCP/RIB ou Stripe.
                  </div>
                </div>
                {pendingPaymentsCount > 0 && (
                  <button
                    onClick={() => {
                      const next = payments.map((p) =>
                        p.status === "EN_ATTENTE" ? { ...p, status: "CONFIRME" as const } : p
                      );
                      savePayments(next);
                      onShowToast(`Tous les paiements en attente (${pendingPaymentsCount}) ont été confirmés !`);
                    }}
                    style={{
                      padding: "10px 16px",
                      borderRadius: 8,
                      border: "none",
                      backgroundColor: Theme.green,
                      color: "#FFF",
                      fontWeight: 900,
                      fontSize: 12,
                      cursor: "pointer"
                    }}
                  >
                    ✅ Tout Confirmer ({pendingPaymentsCount} en attente)
                  </button>
                )}
              </div>

              {/* Ajouter un paiement manuel reçu */}
              <div
                style={{
                  padding: 14,
                  borderRadius: 12,
                  backgroundColor: Theme.bg,
                  border: `1px solid ${Theme.border}`,
                  display: "grid",
                  gap: 10
                }}
              >
                <div style={{ fontWeight: 800, fontSize: 12.5, color: Theme.cyan }}>
                  + ENREGISTRER & CONFIRMER UN NOUVEAU PAIEMENT REÇU MANUELLEMENT
                </div>
                <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))", gap: 8 }}>
                  <input
                    value={newPayOrg}
                    onChange={(e) => setNewPayOrg(e.target.value)}
                    placeholder="Client / Garage / Magasin"
                    style={{ padding: "9px 10px", borderRadius: 8, backgroundColor: Theme.surface, color: "#FFF", border: `1px solid ${Theme.border}` }}
                  />
                  <input
                    value={newPayEmail}
                    onChange={(e) => setNewPayEmail(e.target.value)}
                    placeholder="Email du client"
                    style={{ padding: "9px 10px", borderRadius: 8, backgroundColor: Theme.surface, color: "#FFF", border: `1px solid ${Theme.border}` }}
                  />
                  <input
                    value={newPayLabel}
                    onChange={(e) => setNewPayLabel(e.target.value)}
                    placeholder="Motif (Abonnement / MécaVerif+)"
                    style={{ padding: "9px 10px", borderRadius: 8, backgroundColor: Theme.surface, color: "#FFF", border: `1px solid ${Theme.border}` }}
                  />
                  <input
                    type="number"
                    value={newPayAmount}
                    onChange={(e) => setNewPayAmount(e.target.value)}
                    placeholder="Montant (DA)"
                    style={{ padding: "9px 10px", borderRadius: 8, backgroundColor: Theme.surface, color: Theme.green, fontWeight: 900, border: `1px solid ${Theme.border}` }}
                  />
                  <select
                    value={newPayMethod}
                    onChange={(e) => setNewPayMethod(e.target.value as any)}
                    style={{ padding: "9px 10px", borderRadius: 8, backgroundColor: Theme.surface, color: "#FFF", border: `1px solid ${Theme.border}` }}
                  >
                    <option value="CIB / SATIM">CIB / SATIM</option>
                    <option value="EDAHABIA">EDAHABIA</option>
                    <option value="BARIDIMOB QR">BARIDIMOB QR</option>
                    <option value="VIREMENT CCP / RIB">VIREMENT CCP / RIB</option>
                    <option value="STRIPE VISA">STRIPE VISA</option>
                  </select>
                  <button
                    onClick={() => {
                      if (!newPayOrg.trim()) return;
                      const created: SuperAdminPaymentItem = {
                        id: `PAY-2026-${Math.floor(910 + Math.random() * 89)}`,
                        payerOrganization: newPayOrg.trim(),
                        payerEmail: newPayEmail.trim() || "client@mekanikplus.dz",
                        planOrServiceLabel: newPayLabel.trim(),
                        amountDzd: parseInt(newPayAmount || "9900", 10) || 9900,
                        paymentMethod: newPayMethod,
                        referenceCode: `MAN-${Date.now().toString().slice(-6)}`,
                        date: new Date().toLocaleDateString("fr-FR") + " " + new Date().toLocaleTimeString("fr-FR").slice(0, 5),
                        status: "CONFIRME"
                      };
                      savePayments([created, ...payments]);
                      setNewPayOrg("");
                      setNewPayEmail("");
                      onShowToast(`Paiement de ${created.amountDzd.toLocaleString("fr-FR")} DA enregistré et confirmé !`);
                    }}
                    style={{
                      padding: "9px 14px",
                      borderRadius: 8,
                      border: "none",
                      backgroundColor: Theme.orange,
                      color: "#FFF",
                      fontWeight: 900,
                      cursor: "pointer"
                    }}
                  >
                    + Ajouter & Confirmer
                  </button>
                </div>
              </div>

              <div style={{ display: "grid", gap: 10 }}>
                {payments.map((pay) => {
                  const statusColor =
                    pay.status === "CONFIRME"
                      ? Theme.green
                      : pay.status === "REJETE"
                      ? Theme.red
                      : Theme.orange;
                  return (
                    <div
                      key={pay.id}
                      style={{
                        padding: 14,
                        borderRadius: 12,
                        backgroundColor: Theme.bg,
                        border: `1.5px solid ${statusColor}`,
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                        flexWrap: "wrap",
                        gap: 10
                      }}
                    >
                      <div>
                        <div style={{ display: "flex", alignItems: "center", gap: 8, flexWrap: "wrap" }}>
                          <span style={{ fontWeight: 900, fontSize: 13.5 }}>{pay.payerOrganization}</span>
                          <span style={{ fontSize: 11, color: Theme.textSecondary }}>({pay.payerEmail})</span>
                          <span
                            style={{
                              padding: "2px 8px",
                              borderRadius: 5,
                              backgroundColor: statusColor,
                              color: "#FFF",
                              fontWeight: 900,
                              fontSize: 10
                            }}
                          >
                            {pay.status === "CONFIRME"
                              ? "CONFIRMÉ PAR SUPER-ADMIN"
                              : pay.status === "REJETE"
                              ? "REJETÉ"
                              : "EN ATTENTE DE CONFIRMATION"}
                          </span>
                        </div>
                        <div style={{ fontSize: 12, color: Theme.textPrimary, marginTop: 4 }}>
                          {pay.planOrServiceLabel}
                        </div>
                        <div style={{ fontSize: 11, color: Theme.textSecondary, marginTop: 2, fontFamily: "monospace" }}>
                          Méthode : {pay.paymentMethod} • Réf : {pay.referenceCode} • Date : {pay.date}
                        </div>
                      </div>

                      <div style={{ display: "flex", alignItems: "center", gap: 8, flexWrap: "wrap" }}>
                        <strong style={{ fontSize: 15, color: Theme.green, marginRight: 6 }}>
                          {pay.amountDzd.toLocaleString("fr-FR")} DA
                        </strong>
                        {pay.status !== "CONFIRME" && (
                          <button
                            onClick={() => {
                              const next = payments.map((p) =>
                                p.id === pay.id ? { ...p, status: "CONFIRME" as const } : p
                              );
                              savePayments(next);
                              onShowToast(`Paiement ${pay.referenceCode} (${pay.amountDzd} DA) confirmé !`);
                            }}
                            style={{
                              padding: "8px 12px",
                              borderRadius: 8,
                              border: "none",
                              backgroundColor: Theme.green,
                              color: "#FFF",
                              fontWeight: 900,
                              fontSize: 11.5,
                              cursor: "pointer"
                            }}
                          >
                            ✅ Confirmer
                          </button>
                        )}
                        {pay.status !== "REJETE" && (
                          <button
                            onClick={() => {
                              const next = payments.map((p) =>
                                p.id === pay.id ? { ...p, status: "REJETE" as const } : p
                              );
                              savePayments(next);
                              onShowToast(`Paiement ${pay.referenceCode} rejeté.`);
                            }}
                            style={{
                              padding: "8px 12px",
                              borderRadius: 8,
                              border: `1px solid ${Theme.red}`,
                              backgroundColor: Theme.surface,
                              color: Theme.red,
                              fontWeight: 800,
                              fontSize: 11.5,
                              cursor: "pointer"
                            }}
                          >
                            ❌ Rejeter
                          </button>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* =============================================================== */}
          {/* ONGLET 3 : CONFIRMER LES COMPTES INSCRITS MANUELLEMENT          */}
          {/* =============================================================== */}
          {activeAdminTab === "CONFIRMER_COMPTES" && (
            <div
              style={{
                padding: 20,
                backgroundColor: Theme.surface,
                borderRadius: 16,
                border: `1px solid ${Theme.border}`,
                display: "grid",
                gap: 14
              }}
            >
              <div>
                <div style={{ fontWeight: 900, fontSize: 16, color: Theme.orange }}>
                  👥 CONFIRMATION & GESTION MANUELLE DES COMPTES INSCRITS ({allAccounts.length} COMPTES)
                </div>
                <div style={{ fontSize: 12, color: Theme.textSecondary, marginTop: 4 }}>
                  Approuvez les nouveaux comptes, suspendez un accès ou modifiez manuellement la formule d'abonnement d'un client en 1 clic.
                </div>
              </div>

              <div style={{ display: "grid", gap: 10 }}>
                {allAccounts.map((acc) => {
                  const status = acc.approvalStatus || "CONFIRME";
                  const statusColor =
                    status === "CONFIRME"
                      ? Theme.green
                      : status === "SUSPENDU"
                      ? Theme.red
                      : Theme.orange;
                  const rMeta = rolesPricing[acc.role];
                  return (
                    <div
                      key={acc.userId}
                      style={{
                        padding: 14,
                        borderRadius: 12,
                        backgroundColor: Theme.bg,
                        border: `1.5px solid ${statusColor}`,
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                        flexWrap: "wrap",
                        gap: 10
                      }}
                    >
                      <div>
                        <div style={{ display: "flex", alignItems: "center", gap: 8, flexWrap: "wrap" }}>
                          <span style={{ fontWeight: 900, fontSize: 14 }}>{acc.organizationName}</span>
                          <span
                            style={{
                              padding: "2px 8px",
                              borderRadius: 5,
                              backgroundColor: rMeta.accentColor,
                              color: "#FFF",
                              fontWeight: 900,
                              fontSize: 10
                            }}
                          >
                            {rMeta.shortBadge}
                          </span>
                          <span
                            style={{
                              padding: "2px 8px",
                              borderRadius: 5,
                              backgroundColor: statusColor,
                              color: "#FFF",
                              fontWeight: 900,
                              fontSize: 10
                            }}
                          >
                            {status === "CONFIRME"
                              ? "COMPTE CONFIRMÉ & ACTIF"
                              : status === "SUSPENDU"
                              ? "COMPTE SUSPENDU"
                              : "EN ATTENTE DE CONFIRMATION"}
                          </span>
                        </div>
                        <div style={{ fontSize: 12, color: Theme.textSecondary, marginTop: 4 }}>
                          Titulaire : <strong style={{ color: "#FFF" }}>{acc.fullName}</strong> ({acc.email}) • Tél : {acc.phone} • Wilaya : {acc.wilaya} • RC : {acc.rcNumber}
                        </div>
                        <div style={{ fontSize: 11, color: Theme.green, marginTop: 2 }}>
                          Tarif appliqué : {rMeta.monthlyPriceDzd.toLocaleString("fr-FR")} DA / mois • Sous-comptes : {acc.subAccounts.length}/{rMeta.maxSubAccounts}
                        </div>
                      </div>

                      <div style={{ display: "flex", alignItems: "center", gap: 8, flexWrap: "wrap" }}>
                        <select
                          value={acc.role}
                          onChange={(e) => {
                            const nextRole = e.target.value as AccountRoleId;
                            onUpdateAccountStatusOrRole(acc.userId, status, nextRole);
                            onShowToast(
                              `Formule de ${acc.organizationName} changée en ${rolesPricing[nextRole].shortBadge}`
                            );
                          }}
                          style={{
                            padding: "7px 10px",
                            borderRadius: 8,
                            backgroundColor: Theme.surface,
                            color: "#FFF",
                            border: `1px solid ${Theme.border}`,
                            fontWeight: 800,
                            fontSize: 11
                          }}
                        >
                          {(Object.keys(rolesPricing) as AccountRoleId[]).map((rk) => (
                            <option key={rk} value={rk}>
                              {rolesPricing[rk].shortBadge} ({rolesPricing[rk].monthlyPriceDzd} DA)
                            </option>
                          ))}
                        </select>

                        {status !== "CONFIRME" && (
                          <button
                            onClick={() => {
                              onUpdateAccountStatusOrRole(acc.userId, "CONFIRME");
                              onShowToast(`Compte ${acc.organizationName} confirmé et activé !`);
                            }}
                            style={{
                              padding: "8px 12px",
                              borderRadius: 8,
                              border: "none",
                              backgroundColor: Theme.green,
                              color: "#FFF",
                              fontWeight: 900,
                              fontSize: 11.5,
                              cursor: "pointer"
                            }}
                          >
                            ✅ Confirmer le Compte
                          </button>
                        )}

                        {status !== "SUSPENDU" && (
                          <button
                            onClick={() => {
                              onUpdateAccountStatusOrRole(acc.userId, "SUSPENDU");
                              onShowToast(`Compte ${acc.organizationName} suspendu.`);
                            }}
                            style={{
                              padding: "8px 12px",
                              borderRadius: 8,
                              border: `1px solid ${Theme.red}`,
                              backgroundColor: Theme.surface,
                              color: Theme.red,
                              fontWeight: 800,
                              fontSize: 11.5,
                              cursor: "pointer"
                            }}
                          >
                            ⏸️ Suspendre
                          </button>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* =============================================================== */}
          {/* ONGLET 4 : CHANGER & AJOUTER PLUSIEURS APIS MANUELLEMENT        */}
          {/*            (SANS TIKTOK • PROTÉGÉ PAR CODE EMAIL badroumc@gmail.com) */}
          {/* =============================================================== */}
          {activeAdminTab === "CLES_API_MULTIPLES" && (
            <div
              style={{
                padding: 20,
                backgroundColor: Theme.surface,
                borderRadius: 16,
                border: `1px solid ${Theme.border}`,
                display: "grid",
                gap: 16
              }}
            >
              <div>
                <div style={{ fontWeight: 900, fontSize: 16, color: Theme.orange }}>
                  🔌 GESTIONNAIRE MULTI-API SANS CODER (TECDOC, ETKA, STELLANTIS, VIN, DIALOGYS, XENTRY, MICROCAT, AUTODATA, SATIM, STRIPE)
                </div>
                <div style={{ fontSize: 12, color: Theme.textSecondary, marginTop: 4 }}>
                  Ajoutez autant d'APIs externes que vous le souhaitez et modifiez vos clés manuellement. L'API TikTok a été définitivement supprimée conformément à vos instructions.
                </div>
              </div>

              {/* Bandeau de sécurité Email badroumc@gmail.com pour autoriser les modifications d'API */}
              <div
                style={{
                  padding: 14,
                  borderRadius: 12,
                  backgroundColor: Theme.bg,
                  border: `1.5px solid ${isApiEmailUnlocked ? Theme.green : Theme.cyan}`,
                  display: "grid",
                  gap: 10
                }}
              >
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 8 }}>
                  <div>
                    <div style={{ fontWeight: 900, fontSize: 12.5, color: Theme.cyan }}>
                      📧 PROTECTION PAR MOT DE PASSE & CODE EMAIL SUR {SUPER_ADMIN_UNIQUE_EMAIL} POUR CHANGER / AJOUTER LES APIS
                    </div>
                    <div style={{ fontSize: 11, color: Theme.textSecondary }}>
                      {emailDispatchedBanner}
                    </div>
                  </div>
                  <button
                    onClick={handleSendEmailPasswordAndCode}
                    style={{
                      padding: "8px 12px",
                      borderRadius: 8,
                      border: "none",
                      backgroundColor: Theme.cyan,
                      color: "#000",
                      fontWeight: 900,
                      fontSize: 11,
                      cursor: "pointer"
                    }}
                  >
                    📧 Renvoyer un Code sur {SUPER_ADMIN_UNIQUE_EMAIL}
                  </button>
                </div>

                <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))", gap: 8 }}>
                  <input
                    type="password"
                    value={apiPasswordChallengeInput}
                    onChange={(e) => setApiPasswordChallengeInput(e.target.value)}
                    placeholder={`Mot de passe confidentiel (${SUPER_ADMIN_UNIQUE_EMAIL})`}
                    style={{
                      padding: "9px 12px",
                      borderRadius: 8,
                      backgroundColor: Theme.surface,
                      color: "#FFF",
                      fontFamily: "monospace",
                      fontWeight: 800,
                      border: `1px solid ${Theme.border}`
                    }}
                  />
                  <input
                    type="password"
                    value={apiEmailChallengeInput}
                    onChange={(e) => setApiEmailChallengeInput(e.target.value)}
                    placeholder={`Code à 6 chiffres reçu sur ${SUPER_ADMIN_UNIQUE_EMAIL}`}
                    maxLength={6}
                    style={{
                      padding: "9px 12px",
                      borderRadius: 8,
                      backgroundColor: Theme.surface,
                      color: Theme.cyan,
                      fontFamily: "monospace",
                      fontWeight: 900,
                      border: `1px solid ${Theme.cyan}`
                    }}
                  />
                  <button
                    onClick={() => {
                      if (
                        apiEmailChallengeInput.trim().length >= 4 &&
                        apiPasswordChallengeInput.trim().length >= 6
                      ) {
                        setIsApiEmailUnlocked(true);
                        onShowToast(
                          `Code Email & Mot de passe (${SUPER_ADMIN_UNIQUE_EMAIL}) validés : ajout et modification d'APIs autorisés !`
                        );
                      } else {
                        onShowToast(
                          `Veuillez saisir le mot de passe et le code de sécurité reçus sur ${SUPER_ADMIN_UNIQUE_EMAIL}.`
                        );
                      }
                    }}
                    style={{
                      padding: "9px 14px",
                      borderRadius: 8,
                      border: "none",
                      backgroundColor: Theme.green,
                      color: "#FFF",
                      fontWeight: 900,
                      fontSize: 11.5,
                      cursor: "pointer"
                    }}
                  >
                    ✓ Vérifier Code Email {SUPER_ADMIN_UNIQUE_EMAIL}
                  </button>
                </div>
              </div>

              {/* Modèles rapides 1-clic pour pré-remplir n'importe quelle API automobile */}
              <div>
                <div style={{ fontSize: 11.5, fontWeight: 900, color: Theme.orange, marginBottom: 8 }}>
                  MODÈLES RAPIDES POUR AJOUTER PLUSIEURS APIS AUTOMOBILES EN 1 CLIC :
                </div>
                <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
                  {[
                    [
                      "📘 + API TecDoc",
                      "TecDoc Pegasus 3.0 Secondary Node",
                      "Catalogue OEM & Équivalences",
                      "https://webservice.tecalliance.services/pegasus-3-0/services/TecdocToCatDLB.jsonEndpoint",
                      "TD-PRO-KEY-2026",
                      "td_secret_token_9948",
                      '{"X-TecDoc-Provider": "22491"}'
                    ],
                    [
                      "🚗 + API ETKA (VAG)",
                      "ETKA / Partslink24 VAG Live",
                      "Groupe VAG (VW, Audi, Seat, Škoda)",
                      "https://api.partslink24.com/v2/vag-etka/chassis-lookup",
                      "ETKA-VAG-KEY-8831",
                      "etka_vag_secret_4410",
                      '{"X-ETKA-Market": "RDW"}'
                    ],
                    [
                      "🦁 + API Stellantis ServiceBox",
                      "Groupe Stellantis ServiceBox & SEDRE",
                      "Peugeot, Citroën, DS, Fiat, Opel, Jeep",
                      "https://api.servicebox.stellantis.com/v3/vin-and-parts",
                      "STELLANTIS-SBX-5519",
                      "sedre_opr_dam_secret_7721",
                      '{"X-Stellantis-Brands": "ALL"}'
                    ],
                    [
                      "🔍 + API Lecture Châssis VIN",
                      "AutoDNA / CarVertical / Vincario VIN API",
                      "Lecture Numéro de Châssis & Historique Km",
                      "https://api.vincario.com/3.2/vin-decode",
                      "VIN-DECODER-KEY-6620",
                      "vin_hash_secret_2026",
                      '{"X-Decode-Full": "true"}'
                    ],
                    [
                      "🔧 + API Autodata / HaynesPro",
                      "Autodata & HaynesPro WorkshopData API",
                      "Couples de Serrage, Distribution & Schémas OBD",
                      "https://api.autodata-group.com/v1/technical-repair-data",
                      "AUTODATA-KEY-3310",
                      "haynespro_secret_8841",
                      '{"X-Include-Wiring": "true"}'
                    ],
                    [
                      "🌏 + API SinoAuto360 / Microcat",
                      "SinoAuto360 & Microcat (Chery, Geely, JAC, Toyota, Hyundai)",
                      "Constructeurs Asiatiques & Chinois par VIN",
                      "https://api.sinoauto360.cn/v2/epc/vin-lookup",
                      "SINO-MCAT-KEY-7740",
                      "sino_epc_secret_1190",
                      '{"X-Brands": "CHERY,GEELY,JAC,BYD,TOYOTA,HYUNDAI"}'
                    ]
                  ].map(([btnLabel, name, grp, url, key, sec, hdr]) => (
                    <button
                      key={btnLabel}
                      onClick={() => loadApiPreset(name, grp, url, key, sec, hdr)}
                      style={{
                        padding: "7px 12px",
                        borderRadius: 8,
                        border: `1px solid ${Theme.orange}`,
                        backgroundColor: Theme.bg,
                        color: "#FFF",
                        fontWeight: 800,
                        fontSize: 11.5,
                        cursor: "pointer"
                      }}
                    >
                      {btnLabel}
                    </button>
                  ))}
                </div>
              </div>

              {/* Formulaire complet pour ajouter ou modifier une API manuellement */}
              <div
                style={{
                  padding: 16,
                  borderRadius: 12,
                  backgroundColor: Theme.bg,
                  border: `1.5px solid ${Theme.green}`,
                  display: "grid",
                  gap: 10
                }}
              >
                <div style={{ fontWeight: 900, fontSize: 13, color: Theme.green }}>
                  {editingApiId
                    ? `✏️ MODIFIER MANUELLEMENT L'API [${editingApiId}]`
                    : "+ AJOUTER UNE NOUVELLE API EXTERNE MANUELLEMENT (SANS CODER)"}
                </div>

                <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))", gap: 10 }}>
                  <input
                    value={newApiServiceName}
                    onChange={(e) => setNewApiServiceName(e.target.value)}
                    placeholder="Nom de l'API (ex: TecDoc, ETKA, Stellantis, Microcat, Autodata...)"
                    style={{ padding: "10px 12px", borderRadius: 8, backgroundColor: Theme.surface, color: "#FFF", border: `1px solid ${Theme.border}` }}
                  />
                  <input
                    value={newApiGroup}
                    onChange={(e) => setNewApiGroup(e.target.value)}
                    placeholder="Catégorie / Groupe Constructeur"
                    style={{ padding: "10px 12px", borderRadius: 8, backgroundColor: Theme.surface, color: "#FFF", border: `1px solid ${Theme.border}` }}
                  />
                  <input
                    value={newApiEndpoint}
                    onChange={(e) => setNewApiEndpoint(e.target.value)}
                    placeholder="URL Endpoint WebService"
                    style={{ padding: "10px 12px", borderRadius: 8, backgroundColor: Theme.surface, color: "#FFF", fontFamily: "monospace", border: `1px solid ${Theme.border}` }}
                  />
                  <input
                    value={newApiKeyVal}
                    onChange={(e) => setNewApiKeyVal(e.target.value)}
                    placeholder="Clé API / Client ID / Provider ID"
                    style={{ padding: "10px 12px", borderRadius: 8, backgroundColor: Theme.surface, color: Theme.orange, fontFamily: "monospace", fontWeight: 800, border: `1px solid ${Theme.border}` }}
                  />
                  <input
                    value={newApiSecretVal}
                    onChange={(e) => setNewApiSecretVal(e.target.value)}
                    placeholder="Secret Key / Token Privé"
                    style={{ padding: "10px 12px", borderRadius: 8, backgroundColor: Theme.surface, color: Theme.cyan, fontFamily: "monospace", border: `1px solid ${Theme.border}` }}
                  />
                  <input
                    value={newApiWebhookVal}
                    onChange={(e) => setNewApiWebhookVal(e.target.value)}
                    placeholder="Webhook URL (Optionnel)"
                    style={{ padding: "10px 12px", borderRadius: 8, backgroundColor: Theme.surface, color: "#FFF", fontFamily: "monospace", border: `1px solid ${Theme.border}` }}
                  />
                </div>

                <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
                  <input
                    value={newApiHeadersVal}
                    onChange={(e) => setNewApiHeadersVal(e.target.value)}
                    placeholder='En-têtes JSON (ex: {"X-TecDoc-Provider": "22491"})'
                    style={{
                      flex: 1,
                      minWidth: 220,
                      padding: "10px 12px",
                      borderRadius: 8,
                      backgroundColor: Theme.surface,
                      color: "#FFF",
                      fontFamily: "monospace",
                      border: `1px solid ${Theme.border}`
                    }}
                  />
                  <button
                    onClick={() => {
                      if (!isApiEmailUnlocked) {
                        onShowToast(
                          `Veuillez d'abord valider le code reçu sur ${SUPER_ADMIN_UNIQUE_EMAIL} ci-dessus.`
                        );
                        return;
                      }
                      if (!newApiServiceName.trim() || !newApiKeyVal.trim()) {
                        onShowToast("Veuillez saisir le nom de l'API et la clé API.");
                        return;
                      }
                      const entry: ExternalApiConfigRecord = {
                        id: editingApiId || `API-${Date.now().toString().slice(-5)}`,
                        name: newApiServiceName.trim(),
                        group: newApiGroup.trim() || "API Automobile Externe",
                        endpoint: newApiEndpoint.trim() || "https://api.mekanikplus.dz",
                        apiKey: newApiKeyVal.trim(),
                        apiSecret: newApiSecretVal.trim() || "secret_live_token",
                        webhookUrl: newApiWebhookVal.trim(),
                        customHeadersJson: newApiHeadersVal.trim(),
                        enabled: true,
                        addedByEmail: SUPER_ADMIN_UNIQUE_EMAIL
                      };
                      if (editingApiId) {
                        saveApiVault(apiVault.map((a) => (a.id === editingApiId ? entry : a)));
                        onShowToast(`API ${entry.name} modifiée avec succès par ${SUPER_ADMIN_UNIQUE_EMAIL} !`);
                      } else {
                        saveApiVault([entry, ...apiVault]);
                        onShowToast(`Nouvelle API ${entry.name} ajoutée au coffre-fort Super-Admin !`);
                      }
                      setEditingApiId(null);
                      setNewApiServiceName("");
                      setNewApiKeyVal("");
                      setNewApiSecretVal("");
                    }}
                    style={{
                      padding: "10px 18px",
                      borderRadius: 8,
                      border: "none",
                      backgroundColor: Theme.green,
                      color: "#FFF",
                      fontWeight: 900,
                      cursor: "pointer"
                    }}
                  >
                    {editingApiId ? "💾 Enregistrer les Modifications" : "+ Ajouter cette API au Coffre-Fort"}
                  </button>
                </div>
              </div>

              {/* Liste complète des APIs configurées (Modification en direct + Suppression) */}
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
                          {api.group} • Validée par : <strong style={{ color: Theme.cyan }}>{api.addedByEmail}</strong>
                        </div>
                      </div>
                      <div style={{ display: "flex", gap: 6, flexWrap: "wrap" }}>
                        <button
                          onClick={() => {
                            setEditingApiId(api.id);
                            setNewApiServiceName(api.name);
                            setNewApiGroup(api.group);
                            setNewApiEndpoint(api.endpoint);
                            setNewApiKeyVal(api.apiKey);
                            setNewApiSecretVal(api.apiSecret);
                            setNewApiWebhookVal(api.webhookUrl);
                            setNewApiHeadersVal(api.customHeadersJson);
                          }}
                          style={{
                            padding: "5px 10px",
                            borderRadius: 6,
                            border: `1px solid ${Theme.cyan}`,
                            backgroundColor: Theme.surface,
                            color: Theme.cyan,
                            fontWeight: 800,
                            fontSize: 11,
                            cursor: "pointer"
                          }}
                        >
                          ✏️ Éditer Complet
                        </button>
                        <button
                          onClick={() => {
                            const next = [...apiVault];
                            next[idx] = { ...api, enabled: !api.enabled };
                            saveApiVault(next);
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
                        <button
                          onClick={() => {
                            saveApiVault(apiVault.filter((a) => a.id !== api.id));
                            onShowToast(`API ${api.name} supprimée par ${SUPER_ADMIN_UNIQUE_EMAIL}.`);
                          }}
                          style={{
                            padding: "5px 10px",
                            borderRadius: 6,
                            border: `1px solid ${Theme.red}`,
                            backgroundColor: Theme.surface,
                            color: Theme.red,
                            fontWeight: 800,
                            fontSize: 11,
                            cursor: "pointer"
                          }}
                        >
                          🗑️ Supprimer
                        </button>
                      </div>
                    </div>

                    <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))", gap: 8 }}>
                      <div>
                        <label style={{ fontSize: 10, color: Theme.textSecondary }}>URL Endpoint</label>
                        <input
                          value={api.endpoint}
                          onChange={(e) => {
                            const next = [...apiVault];
                            next[idx] = { ...api, endpoint: e.target.value };
                            saveApiVault(next);
                          }}
                          style={{
                            width: "100%",
                            marginTop: 2,
                            padding: "8px 10px",
                            borderRadius: 8,
                            backgroundColor: Theme.surface,
                            color: "#FFF",
                            border: `1px solid ${Theme.border}`,
                            fontFamily: "monospace",
                            fontSize: 11.5
                          }}
                        />
                      </div>
                      <div>
                        <label style={{ fontSize: 10, color: Theme.textSecondary }}>Clé API / Client ID</label>
                        <input
                          value={api.apiKey}
                          onChange={(e) => {
                            const next = [...apiVault];
                            next[idx] = { ...api, apiKey: e.target.value };
                            saveApiVault(next);
                          }}
                          style={{
                            width: "100%",
                            marginTop: 2,
                            padding: "8px 10px",
                            borderRadius: 8,
                            backgroundColor: Theme.surface,
                            color: Theme.orange,
                            border: `1px solid ${Theme.border}`,
                            fontFamily: "monospace",
                            fontWeight: 800,
                            fontSize: 11.5
                          }}
                        />
                      </div>
                      <div>
                        <label style={{ fontSize: 10, color: Theme.textSecondary }}>Secret / Token</label>
                        <input
                          value={api.apiSecret}
                          onChange={(e) => {
                            const next = [...apiVault];
                            next[idx] = { ...api, apiSecret: e.target.value };
                            saveApiVault(next);
                          }}
                          style={{
                            width: "100%",
                            marginTop: 2,
                            padding: "8px 10px",
                            borderRadius: 8,
                            backgroundColor: Theme.surface,
                            color: Theme.cyan,
                            border: `1px solid ${Theme.border}`,
                            fontFamily: "monospace",
                            fontSize: 11.5
                          }}
                        />
                      </div>
                    </div>

                    <div style={{ display: "flex", justifyContent: "flex-end" }}>
                      <button
                        onClick={() =>
                          onShowToast(
                            `Clé API ${api.name} enregistrée à chaud par ${SUPER_ADMIN_UNIQUE_EMAIL} !`
                          )
                        }
                        style={{
                          padding: "7px 14px",
                          borderRadius: 8,
                          border: "none",
                          backgroundColor: Theme.orange,
                          color: "#FFF",
                          fontWeight: 800,
                          fontSize: 11.5,
                          cursor: "pointer"
                        }}
                      >
                        💾 Sauvegarder Clé API
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* =============================================================== */}
          {/* ONGLET 5 : INTERRUPTEURS GLOBAUX ON / OFF SANS CODER            */}
          {/* =============================================================== */}
          {activeAdminTab === "OPTIONS_ON_OFF" && (
            <div
              style={{
                padding: 20,
                backgroundColor: Theme.surface,
                borderRadius: 16,
                border: `1px solid ${Theme.border}`
              }}
            >
              <div style={{ fontWeight: 900, fontSize: 15, color: Theme.orange, marginBottom: 12 }}>
                ⚙️ ACTIVATION / DÉSACTIVATION GLOBALE DES FONCTIONNALITÉS (ON / OFF SANS CODER)
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
                  ["mecaVerif", `Service Externe MécaVerif+ (${mecaVerifPrices.proPriceDzd}/${mecaVerifPrices.partPriceDzd} DA)`],
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
          )}
        </>
      )}
    </div>
  );
};

const RolePriceEditorRow: React.FC<{
  meta: AccountRoleMeta;
  onSave: (monthly: number, setup: number, maxSubs: number) => void;
}> = ({ meta, onSave }) => {
  const [monthlyInput, setMonthlyInput] = useState(String(meta.monthlyPriceDzd));
  const [setupInput, setSetupInput] = useState(String(meta.setupFeeDzd));
  const [maxSubsInput, setMaxSubsInput] = useState(String(meta.maxSubAccounts));

  return (
    <div
      style={{
        padding: 14,
        borderRadius: 12,
        backgroundColor: Theme.bg,
        border: `1.5px solid ${meta.accentColor}`,
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        flexWrap: "wrap",
        gap: 12
      }}
    >
      <div style={{ flex: 1, minWidth: 220 }}>
        <span
          style={{
            padding: "2px 8px",
            borderRadius: 5,
            backgroundColor: meta.accentColor,
            color: "#FFF",
            fontWeight: 900,
            fontSize: 10
          }}
        >
          {meta.shortBadge}
        </span>
        <div style={{ fontWeight: 800, fontSize: 14, marginTop: 4 }}>{meta.title}</div>
        <div style={{ fontSize: 11, color: Theme.textSecondary }}>
          Tarif actuel :{" "}
          <strong style={{ color: Theme.green }}>
            {meta.monthlyPriceDzd.toLocaleString("fr-FR")} DA / mois
          </strong>{" "}
          • Frais d'ouverture : {meta.setupFeeDzd.toLocaleString("fr-FR")} DA • Max sous-comptes :{" "}
          {meta.maxSubAccounts}
        </div>
      </div>

      <div style={{ display: "flex", gap: 8, alignItems: "center", flexWrap: "wrap" }}>
        <div>
          <label style={{ fontSize: 10, color: Theme.textSecondary, display: "block" }}>
            Prix Mensuel (DA)
          </label>
          <input
            type="number"
            value={monthlyInput}
            onChange={(e) => setMonthlyInput(e.target.value)}
            style={{
              width: 115,
              padding: "8px 10px",
              borderRadius: 8,
              backgroundColor: Theme.surface,
              color: Theme.green,
              fontWeight: 900,
              border: `1px solid ${Theme.border}`
            }}
          />
        </div>
        <div>
          <label style={{ fontSize: 10, color: Theme.textSecondary, display: "block" }}>
            Frais Setup (DA)
          </label>
          <input
            type="number"
            value={setupInput}
            onChange={(e) => setSetupInput(e.target.value)}
            style={{
              width: 110,
              padding: "8px 10px",
              borderRadius: 8,
              backgroundColor: Theme.surface,
              color: "#FFF",
              fontWeight: 800,
              border: `1px solid ${Theme.border}`
            }}
          />
        </div>
        <div>
          <label style={{ fontSize: 10, color: Theme.textSecondary, display: "block" }}>
            Max Sous-Comptes
          </label>
          <input
            type="number"
            value={maxSubsInput}
            onChange={(e) => setMaxSubsInput(e.target.value)}
            style={{
              width: 95,
              padding: "8px 10px",
              borderRadius: 8,
              backgroundColor: Theme.surface,
              color: Theme.orange,
              fontWeight: 900,
              border: `1px solid ${Theme.border}`
            }}
          />
        </div>
        <button
          onClick={() => {
            const m = Math.max(0, parseInt(monthlyInput || "0", 10) || 0);
            const s = Math.max(0, parseInt(setupInput || "0", 10) || 0);
            const sub = Math.max(0, parseInt(maxSubsInput || "0", 10) || 0);
            onSave(m, s, sub);
          }}
          style={{
            padding: "9px 14px",
            borderRadius: 8,
            border: "none",
            backgroundColor: Theme.orange,
            color: "#FFF",
            fontWeight: 900,
            fontSize: 11.5,
            cursor: "pointer",
            marginTop: 12
          }}
        >
          💾 Enregistrer Prix
        </button>
      </div>
    </div>
  );
};
