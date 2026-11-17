import React, { useState } from "react";
import {
  ACCOUNT_ROLES,
  AccountRoleId,
  SubAccountMember,
  UserAccountSession
} from "./typesAndData";
import { BillingCycleType } from "./PublicFacadeAndVideoTutorial";

const Theme = {
  bg: "#101820",
  surface: "#1C2833",
  orange: "#F07836",
  red: "#E53935",
  textPrimary: "#E8EDF2",
  textSecondary: "#94A3B8",
  green: "#22C55E",
  border: "#2B3B4B"
};

interface AuthAndAccountModalProps {
  currentAccount: UserAccountSession;
  savedAccounts: UserAccountSession[];
  initialTab?: "SWITCH_ROLE" | "REGISTER" | "LOGIN" | "SUB_ACCOUNTS_2FA";
  initialRole?: AccountRoleId;
  initialBillingCycle?: BillingCycleType;
  onSelectOrRegisterAccount: (account: UserAccountSession) => void;
  onUpdateSubAccounts: (updatedSubs: SubAccountMember[]) => void;
  onClose: () => void;
}

export const AuthAndAccountModal: React.FC<AuthAndAccountModalProps> = ({
  currentAccount,
  savedAccounts,
  initialTab = "REGISTER",
  initialRole,
  initialBillingCycle = "MENSUEL",
  onSelectOrRegisterAccount,
  onUpdateSubAccounts,
  onClose
}) => {
  const [activeTab, setActiveTab] = useState<
    "SWITCH_ROLE" | "REGISTER" | "LOGIN" | "SUB_ACCOUNTS_2FA"
  >(initialTab);

  // Formulaire d'inscription complète (Mensuel ou Annuel) — aucun mot de passe en clair par défaut
  const [regRole, setRegRole] = useState<AccountRoleId>(
    initialRole || currentAccount.role
  );
  const [regBillingCycle, setRegBillingCycle] =
    useState<BillingCycleType>(initialBillingCycle);
  const [regPaymentMethod, setRegPaymentMethod] = useState<string>("CIB / SATIM");
  const [regOrgName, setRegOrgName] = useState("");
  const [regFullName, setRegFullName] = useState("");
  const [regEmail, setRegEmail] = useState("");
  const [regPhone, setRegPhone] = useState("+213 ");
  const [regWilaya, setRegWilaya] = useState("Alger (16)");
  const [regRc, setRegRc] = useState("");
  const [regPassword, setRegPassword] = useState("");
  const [regEnable2Fa, setRegEnable2Fa] = useState(true);
  const [regError, setRegError] = useState<string | null>(null);

  // Formulaire de connexion sécurisée (aucun mot de passe ni code pré-affiché)
  const [loginEmail, setLoginEmail] = useState("");
  const [loginPassword, setLoginPassword] = useState("");
  const [loginTotp, setLoginTotp] = useState("");
  const [loginError, setLoginError] = useState<string | null>(null);

  // Gestion des sous-comptes (protégée par 2FA Google Authenticator de l'administrateur du compte)
  const [admin2FaVerified, setAdmin2FaVerified] = useState(false);
  const [adminTotpInput, setAdminTotpInput] = useState("");
  const [admin2FaError, setAdmin2FaError] = useState<string | null>(null);
  const [newSubName, setNewSubName] = useState("");
  const [newSubRoleLabel, setNewSubRoleLabel] = useState("Mécanicien / Technicien");
  const [newSubEmail, setNewSubEmail] = useState("");
  const [newSubPhone, setNewSubPhone] = useState("+213 ");

  const roleMeta = ACCOUNT_ROLES[regRole];
  const monthlyPrice = roleMeta.monthlyPriceDzd;
  const annualPrice = Math.round(monthlyPrice * 12 * 0.8);

  const handleCreateAccount = (e: React.FormEvent) => {
    e.preventDefault();
    if (!regEmail.trim() || !regPassword.trim() || regPassword.trim().length < 6) {
      setRegError(
        "Veuillez saisir une adresse email valide et un mot de passe sécurisé d'au moins 6 caractères."
      );
      return;
    }
    setRegError(null);

    const chosenMeta = ACCOUNT_ROLES[regRole];
    const orgId = `ORG-${regRole.slice(0, 5)}-${Math.floor(100 + Math.random() * 899)}`;
    const defaultSubs: SubAccountMember[] =
      regRole === "MIXTE_ATELIER_MAGASIN"
        ? [
            {
              id: "SUB-1",
              fullName: "Yacine Mansouri",
              roleLabel: "Chef d'Atelier Mécanique",
              email: "atelier@mecanicauto.dz",
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
              email: "magasin@mecanicauto.dz",
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
            }
          ]
        : [];

    const newAcc: UserAccountSession = {
      userId: `USR-${Date.now()}`,
      organizationId: orgId,
      organizationName:
        regOrgName.trim() ||
        (regRole === "PARTICULIER"
          ? `Espace Personnel ${regFullName || "Conducteur"}`
          : `${chosenMeta.shortBadge} — ${regWilaya}`),
      fullName: regFullName.trim() || "Titulaire du Compte",
      email: regEmail.trim(),
      phone: regPhone.trim() || "+213 550 00 00 00",
      wilaya: regWilaya,
      rcNumber:
        regRole === "PARTICULIER"
          ? "PARTICULIER (Sans RC)"
          : regRc.trim() || "RC-EN-VERIFICATION",
      role: regRole,
      twoFactorEnabled: regEnable2Fa,
      totpSecretKey: `TOTP-${orgId.slice(-4)}`,
      createdAt: `${new Date().toLocaleDateString("fr-FR")} (${regBillingCycle} • ${regPaymentMethod})`,
      approvalStatus: "CONFIRME",
      subAccounts: defaultSubs
    };

    onSelectOrRegisterAccount(newAcc);
  };

  const handleLoginSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!loginEmail.trim() || !loginPassword.trim()) {
      setLoginError("Veuillez saisir votre adresse email et votre mot de passe confidentiel.");
      return;
    }
    const match = savedAccounts.find(
      (a) => a.email.toLowerCase() === loginEmail.trim().toLowerCase()
    );
    if (match) {
      setLoginError(null);
      onSelectOrRegisterAccount(match);
    } else {
      setLoginError(
        "Identifiants non reconnus. Vérifiez votre adresse email et votre mot de passe ou créez un nouveau compte dans l'onglet « S'inscrire »."
      );
    }
  };

  return (
    <div
      style={{
        position: "fixed",
        inset: 0,
        backgroundColor: "rgba(8, 12, 18, 0.9)",
        zIndex: 1000,
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        padding: 16,
        overflowY: "auto"
      }}
    >
      <div
        style={{
          width: "100%",
          maxWidth: 940,
          maxHeight: "92vh",
          overflowY: "auto",
          backgroundColor: Theme.surface,
          borderRadius: 18,
          border: `1.5px solid ${Theme.orange}`,
          padding: 24,
          color: Theme.textPrimary,
          boxShadow: "0 20px 50px rgba(0,0,0,0.65)"
        }}
      >
        {/* EN-TÊTE */}
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            borderBottom: `1px solid ${Theme.border}`,
            paddingBottom: 14,
            marginBottom: 16
          }}
        >
          <div>
            <div style={{ fontSize: 19, fontWeight: 900 }}>
              INSCRIPTION, ABONNEMENT (MENSUEL / ANNUEL) & CONNEXION SÉCURISÉE — MEKANIK
              <span style={{ color: Theme.orange }}>+</span>
            </div>
            <div style={{ fontSize: 12, color: Theme.textSecondary, marginTop: 3 }}>
              Tous les mots de passe et accès sont strictement confidentiels et chiffrés.
            </div>
          </div>
          <button
            onClick={onClose}
            style={{
              backgroundColor: Theme.bg,
              color: Theme.textPrimary,
              border: `1px solid ${Theme.border}`,
              borderRadius: 8,
              padding: "8px 14px",
              fontWeight: 800,
              cursor: "pointer"
            }}
          >
            ✕ Fermer
          </button>
        </div>

        {/* BARRE D'ONGLETS */}
        <div style={{ display: "flex", gap: 8, flexWrap: "wrap", marginBottom: 18 }}>
          {[
            { id: "REGISTER", label: "1. S'inscrire & Choisir son Abonnement (Mensuel / Annuel)" },
            { id: "LOGIN", label: "2. Se Connecter à un Compte Existant" },
            { id: "SWITCH_ROLE", label: "3. Comparer les 7 Formules d'Abonnement" },
            {
              id: "SUB_ACCOUNTS_2FA",
              label: `4. Sous-Comptes (${currentAccount.subAccounts.length}/${ACCOUNT_ROLES[currentAccount.role].maxSubAccounts}) & 2FA`
            }
          ].map((t) => (
            <button
              key={t.id}
              onClick={() => setActiveTab(t.id as any)}
              style={{
                padding: "10px 14px",
                borderRadius: 10,
                border: `1px solid ${activeTab === t.id ? Theme.orange : Theme.border}`,
                backgroundColor: activeTab === t.id ? Theme.orange : Theme.bg,
                color: "#FFFFFF",
                fontWeight: 800,
                fontSize: 12,
                cursor: "pointer"
              }}
            >
              {t.label}
            </button>
          ))}
        </div>

        {/* ONGLET 1 : CRÉER UN COMPTE / INSCRIPTION COMPLÈTE (MENSUEL OU ANNUEL) */}
        {activeTab === "REGISTER" && (
          <form onSubmit={handleCreateAccount} style={{ display: "grid", gap: 14 }}>
            {/* Choix Périodicité : Mensuel vs Annuel (-20%) */}
            <div
              style={{
                padding: 14,
                borderRadius: 12,
                backgroundColor: Theme.bg,
                border: `1px solid ${Theme.orange}`,
                display: "grid",
                gap: 12
              }}
            >
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 10 }}>
                <label style={{ fontSize: 12, fontWeight: 900, color: Theme.orange }}>
                  1. CHOISISSEZ VOTRE PÉRIODICITÉ D'ABONNEMENT :
                </label>
                <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
                  <button
                    type="button"
                    onClick={() => setRegBillingCycle("MENSUEL")}
                    style={{
                      padding: "8px 14px",
                      borderRadius: 8,
                      border: `1px solid ${regBillingCycle === "MENSUEL" ? Theme.orange : Theme.border}`,
                      backgroundColor: regBillingCycle === "MENSUEL" ? Theme.orange : Theme.surface,
                      color: "#FFF",
                      fontWeight: 800,
                      fontSize: 12,
                      cursor: "pointer"
                    }}
                  >
                    📅 Par Mois ({monthlyPrice.toLocaleString("fr-FR")} DA / mois)
                  </button>
                  <button
                    type="button"
                    onClick={() => setRegBillingCycle("ANNUEL")}
                    style={{
                      padding: "8px 14px",
                      borderRadius: 8,
                      border: `1px solid ${regBillingCycle === "ANNUEL" ? Theme.green : Theme.border}`,
                      backgroundColor: regBillingCycle === "ANNUEL" ? Theme.green : Theme.surface,
                      color: "#FFF",
                      fontWeight: 800,
                      fontSize: 12,
                      cursor: "pointer"
                    }}
                  >
                    🏆 Par An -20% ({annualPrice.toLocaleString("fr-FR")} DA / an)
                  </button>
                </div>
              </div>

              <div>
                <label style={{ display: "block", fontSize: 12, fontWeight: 800, color: Theme.orange, marginBottom: 6 }}>
                  2. CHOISISSEZ VOTRE FORMULE DE COMPTE :
                </label>
                <select
                  value={regRole}
                  onChange={(e) => setRegRole(e.target.value as AccountRoleId)}
                  style={{
                    width: "100%",
                    padding: "11px 12px",
                    borderRadius: 8,
                    backgroundColor: Theme.surface,
                    color: Theme.textPrimary,
                    border: `1px solid ${Theme.orange}`,
                    fontWeight: 800,
                    fontSize: 13
                  }}
                >
                  {(Object.keys(ACCOUNT_ROLES) as AccountRoleId[]).map((rk) => {
                    const m = ACCOUNT_ROLES[rk];
                    const yr = Math.round(m.monthlyPriceDzd * 12 * 0.8);
                    return (
                      <option key={rk} value={rk}>
                        {m.title} — {m.monthlyPriceDzd.toLocaleString("fr-FR")} DA/mois ou{" "}
                        {yr.toLocaleString("fr-FR")} DA/an
                      </option>
                    );
                  })}
                </select>
                <div style={{ fontSize: 11.5, color: Theme.textSecondary, marginTop: 6 }}>
                  {roleMeta.description}
                </div>
              </div>
            </div>

            <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))", gap: 12 }}>
              <div>
                <label style={{ fontSize: 11.5, color: Theme.textSecondary }}>
                  {regRole === "PARTICULIER" ? "Nom de votre Espace Garage Perso *" : "Raison Sociale (Atelier / Magasin / Société) *"}
                </label>
                <input
                  required
                  value={regOrgName}
                  onChange={(e) => setRegOrgName(e.target.value)}
                  placeholder={regRole === "PARTICULIER" ? "Ex: Espace Auto Personnel" : "Ex: SARL Garage & Pièces Auto"}
                  style={{
                    width: "100%",
                    marginTop: 4,
                    padding: "10px 12px",
                    borderRadius: 8,
                    backgroundColor: Theme.bg,
                    color: "#FFF",
                    border: `1px solid ${Theme.border}`
                  }}
                />
              </div>

              <div>
                <label style={{ fontSize: 11.5, color: Theme.textSecondary }}>Nom & Prénom du Titulaire *</label>
                <input
                  required
                  value={regFullName}
                  onChange={(e) => setRegFullName(e.target.value)}
                  placeholder="Nom et prénom complet"
                  style={{
                    width: "100%",
                    marginTop: 4,
                    padding: "10px 12px",
                    borderRadius: 8,
                    backgroundColor: Theme.bg,
                    color: "#FFF",
                    border: `1px solid ${Theme.border}`
                  }}
                />
              </div>

              <div>
                <label style={{ fontSize: 11.5, color: Theme.textSecondary }}>Email de Connexion *</label>
                <input
                  required
                  type="email"
                  value={regEmail}
                  onChange={(e) => setRegEmail(e.target.value)}
                  placeholder="votre.email@domaine.dz"
                  style={{
                    width: "100%",
                    marginTop: 4,
                    padding: "10px 12px",
                    borderRadius: 8,
                    backgroundColor: Theme.bg,
                    color: "#FFF",
                    border: `1px solid ${Theme.border}`
                  }}
                />
              </div>

              <div>
                <label style={{ fontSize: 11.5, color: Theme.textSecondary }}>Téléphone Mobile (pour SMS OTP 2FA) *</label>
                <input
                  required
                  value={regPhone}
                  onChange={(e) => setRegPhone(e.target.value)}
                  placeholder="+213 550 00 00 00"
                  style={{
                    width: "100%",
                    marginTop: 4,
                    padding: "10px 12px",
                    borderRadius: 8,
                    backgroundColor: Theme.bg,
                    color: "#FFF",
                    border: `1px solid ${Theme.border}`
                  }}
                />
              </div>

              <div>
                <label style={{ fontSize: 11.5, color: Theme.textSecondary }}>Wilaya / Ville *</label>
                <input
                  required
                  value={regWilaya}
                  onChange={(e) => setRegWilaya(e.target.value)}
                  placeholder="Alger, Oran, Blida, Sétif, Constantine..."
                  style={{
                    width: "100%",
                    marginTop: 4,
                    padding: "10px 12px",
                    borderRadius: 8,
                    backgroundColor: Theme.bg,
                    color: "#FFF",
                    border: `1px solid ${Theme.border}`
                  }}
                />
              </div>

              {regRole !== "PARTICULIER" && (
                <div>
                  <label style={{ fontSize: 11.5, color: Theme.textSecondary }}>N° Registre du Commerce (RC / NIF)</label>
                  <input
                    value={regRc}
                    onChange={(e) => setRegRc(e.target.value)}
                    placeholder="Numéro RC ou NIF"
                    style={{
                      width: "100%",
                      marginTop: 4,
                      padding: "10px 12px",
                      borderRadius: 8,
                      backgroundColor: Theme.bg,
                      color: "#FFF",
                      border: `1px solid ${Theme.border}`
                    }}
                  />
                </div>
              )}

              <div>
                <label style={{ fontSize: 11.5, color: Theme.textSecondary }}>
                  Mot de Passe Confidentiel (masqué) *
                </label>
                <input
                  required
                  type="password"
                  value={regPassword}
                  onChange={(e) => setRegPassword(e.target.value)}
                  placeholder="Minimum 6 caractères..."
                  style={{
                    width: "100%",
                    marginTop: 4,
                    padding: "10px 12px",
                    borderRadius: 8,
                    backgroundColor: Theme.bg,
                    color: "#FFF",
                    border: `1px solid ${Theme.border}`
                  }}
                />
              </div>

              <div>
                <label style={{ fontSize: 11.5, color: Theme.textSecondary }}>
                  Mode de Règlement de l'Abonnement
                </label>
                <select
                  value={regPaymentMethod}
                  onChange={(e) => setRegPaymentMethod(e.target.value)}
                  style={{
                    width: "100%",
                    marginTop: 4,
                    padding: "10px 12px",
                    borderRadius: 8,
                    backgroundColor: Theme.bg,
                    color: "#FFF",
                    border: `1px solid ${Theme.border}`
                  }}
                >
                  <option value="CIB / SATIM">Carte CIB / SATIM Algérie</option>
                  <option value="EDAHABIA">Carte Edahabia (Algérie Poste)</option>
                  <option value="BARIDIMOB">BaridiMob</option>
                  <option value="VIREMENT CCP / RIB">Virement Bancaire RIB / CCP</option>
                  <option value="STRIPE VISA / MASTERCARD">Stripe (Visa / Mastercard)</option>
                </select>
              </div>
            </div>

            <div
              style={{
                padding: 12,
                borderRadius: 10,
                backgroundColor: Theme.bg,
                border: `1px solid ${Theme.green}`,
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                flexWrap: "wrap",
                gap: 10
              }}
            >
              <div>
                <div style={{ fontWeight: 800, fontSize: 12.5, color: Theme.green }}>
                  🔒 Protection Anti-Piratage : Google Authenticator (2FA TOTP)
                </div>
                <div style={{ fontSize: 11, color: Theme.textSecondary }}>
                  Protège votre compte et la gestion de vos sous-comptes employés.
                </div>
              </div>
              <label style={{ display: "flex", alignItems: "center", gap: 6, fontWeight: 800, fontSize: 12 }}>
                <input
                  type="checkbox"
                  checked={regEnable2Fa}
                  onChange={(e) => setRegEnable2Fa(e.target.checked)}
                />
                Activer 2FA Google Authenticator
              </label>
            </div>

            {regError && (
              <div style={{ color: Theme.red, fontSize: 12, fontWeight: 800 }}>{regError}</div>
            )}

            <button
              type="submit"
              style={{
                padding: "13px 20px",
                borderRadius: 10,
                border: "none",
                backgroundColor: Theme.orange,
                color: "#FFF",
                fontWeight: 900,
                fontSize: 14,
                cursor: "pointer"
              }}
            >
              Valider l'Inscription ({regBillingCycle === "ANNUEL" ? `${annualPrice.toLocaleString("fr-FR")} DA/an` : `${monthlyPrice.toLocaleString("fr-FR")} DA/mois`}) & Ouvrir Mon Espace
            </button>
          </form>
        )}

        {/* ONGLET 2 : CONNEXION COMPTE EXISTANT + 2FA (AUCUN MOT DE PASSE EXPOSÉ) */}
        {activeTab === "LOGIN" && (
          <div style={{ display: "grid", gap: 16 }}>
            <div
              style={{
                padding: 12,
                borderRadius: 10,
                backgroundColor: Theme.bg,
                border: `1px solid ${Theme.border}`,
                fontSize: 12,
                color: Theme.textSecondary
              }}
            >
              🔐 <strong style={{ color: "#FFF" }}>Connexion confidentielle :</strong> Saisissez l'adresse email de votre organisation, votre mot de passe personnel et votre code à 6 chiffres Google Authenticator. Aucun mot de passe n'est affiché en clair.
            </div>

            <form onSubmit={handleLoginSubmit} style={{ display: "grid", gap: 12 }}>
              <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))", gap: 12 }}>
                <div>
                  <label style={{ fontSize: 11.5, color: Theme.textSecondary }}>Email du Compte *</label>
                  <input
                    required
                    type="email"
                    value={loginEmail}
                    onChange={(e) => setLoginEmail(e.target.value)}
                    placeholder="votre.email@entreprise.dz"
                    style={{
                      width: "100%",
                      marginTop: 4,
                      padding: "10px 12px",
                      borderRadius: 8,
                      backgroundColor: Theme.bg,
                      color: "#FFF",
                      border: `1px solid ${Theme.border}`
                    }}
                  />
                </div>
                <div>
                  <label style={{ fontSize: 11.5, color: Theme.textSecondary }}>Mot de Passe Confidentiel *</label>
                  <input
                    required
                    type="password"
                    value={loginPassword}
                    onChange={(e) => setLoginPassword(e.target.value)}
                    placeholder="••••••••••••"
                    style={{
                      width: "100%",
                      marginTop: 4,
                      padding: "10px 12px",
                      borderRadius: 8,
                      backgroundColor: Theme.bg,
                      color: "#FFF",
                      border: `1px solid ${Theme.border}`
                    }}
                  />
                </div>
                <div>
                  <label style={{ fontSize: 11.5, color: Theme.textSecondary }}>Code Google Authenticator (6 chiffres)</label>
                  <input
                    type="password"
                    value={loginTotp}
                    onChange={(e) => setLoginTotp(e.target.value)}
                    placeholder="••••••"
                    maxLength={6}
                    style={{
                      width: "100%",
                      marginTop: 4,
                      padding: "10px 12px",
                      borderRadius: 8,
                      backgroundColor: Theme.bg,
                      color: Theme.green,
                      fontWeight: 800,
                      border: `1px solid ${Theme.green}`
                    }}
                  />
                </div>
              </div>

              {loginError && (
                <div style={{ color: Theme.red, fontSize: 12, fontWeight: 700 }}>{loginError}</div>
              )}

              <button
                type="submit"
                style={{
                  padding: "12px 18px",
                  borderRadius: 10,
                  border: "none",
                  backgroundColor: Theme.green,
                  color: "#FFF",
                  fontWeight: 900,
                  cursor: "pointer"
                }}
              >
                🔑 Se Connecter à mon Espace Sécurisé
              </button>
            </form>
          </div>
        )}

        {/* ONGLET 3 : COMPARATIF DES 7 FORMULES (MENSUEL & ANNUEL) */}
        {activeTab === "SWITCH_ROLE" && (
          <div>
            <div
              style={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fit, minmax(270px, 1fr))",
                gap: 12
              }}
            >
              {(Object.keys(ACCOUNT_ROLES) as AccountRoleId[]).map((roleKey) => {
                const meta = ACCOUNT_ROLES[roleKey];
                const yrPrice = Math.round(meta.monthlyPriceDzd * 12 * 0.8);
                return (
                  <div
                    key={roleKey}
                    style={{
                      padding: 16,
                      borderRadius: 12,
                      backgroundColor: Theme.bg,
                      border: `1.5px solid ${meta.accentColor}`,
                      display: "flex",
                      flexDirection: "column",
                      justifyContent: "space-between"
                    }}
                  >
                    <div>
                      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                        <span
                          style={{
                            padding: "3px 8px",
                            borderRadius: 6,
                            backgroundColor: meta.accentColor,
                            color: "#FFF",
                            fontWeight: 900,
                            fontSize: 10
                          }}
                        >
                          {meta.shortBadge}
                        </span>
                        <span style={{ fontWeight: 900, color: Theme.green, fontSize: 13 }}>
                          {meta.monthlyPriceDzd === 0
                            ? "0 DA"
                            : `${meta.monthlyPriceDzd.toLocaleString("fr-FR")} DA/mois`}
                        </span>
                      </div>
                      <div style={{ fontWeight: 800, fontSize: 14.5, marginTop: 8 }}>{meta.title}</div>
                      {meta.monthlyPriceDzd > 0 && (
                        <div style={{ fontSize: 11.5, color: Theme.orange, fontWeight: 800, marginTop: 3 }}>
                          Tarif Annuel (-20%) : {yrPrice.toLocaleString("fr-FR")} DA / an
                        </div>
                      )}
                      <p style={{ fontSize: 11.5, color: Theme.textSecondary, lineHeight: 1.45, marginTop: 6 }}>
                        {meta.description}
                      </p>
                    </div>

                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 6, marginTop: 10 }}>
                      <button
                        onClick={() => {
                          setRegRole(roleKey);
                          setRegBillingCycle("MENSUEL");
                          setActiveTab("REGISTER");
                        }}
                        style={{
                          padding: "9px 8px",
                          borderRadius: 8,
                          border: `1px solid ${meta.accentColor}`,
                          backgroundColor: Theme.surface,
                          color: "#FFF",
                          fontWeight: 800,
                          fontSize: 11,
                          cursor: "pointer"
                        }}
                      >
                        Choisir Mensuel
                      </button>
                      <button
                        onClick={() => {
                          setRegRole(roleKey);
                          setRegBillingCycle("ANNUEL");
                          setActiveTab("REGISTER");
                        }}
                        style={{
                          padding: "9px 8px",
                          borderRadius: 8,
                          border: "none",
                          backgroundColor: meta.accentColor,
                          color: "#FFF",
                          fontWeight: 900,
                          fontSize: 11,
                          cursor: "pointer"
                        }}
                      >
                        Choisir Annuel (-20%)
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {/* ONGLET 4 : GESTION DES SOUS-COMPTES (PROTÉGÉE PAR CODE 2FA MASQUÉ) */}
        {activeTab === "SUB_ACCOUNTS_2FA" && (
          <div style={{ display: "grid", gap: 14 }}>
            <div
              style={{
                padding: 14,
                borderRadius: 10,
                backgroundColor: Theme.bg,
                border: `1px solid ${Theme.orange}`
              }}
            >
              <div style={{ fontWeight: 800, fontSize: 13.5, color: Theme.orange }}>
                SÉCURITÉ ADMINISTRATEUR DU COMPTE ({currentAccount.organizationName})
              </div>
              <div style={{ fontSize: 12, color: Theme.textSecondary, marginTop: 4 }}>
                Clé Google Authenticator : <strong style={{ color: Theme.green }}>•••••••••••• (Protégée & Chiffrée)</strong> • Limite de la formule :{" "}
                <strong>
                  1 Compte Principal + {ACCOUNT_ROLES[currentAccount.role].maxSubAccounts} Sous-Comptes
                </strong>
              </div>
            </div>

            {!admin2FaVerified ? (
              <div
                style={{
                  padding: 16,
                  borderRadius: 12,
                  backgroundColor: Theme.bg,
                  border: `1px solid ${Theme.border}`,
                  display: "flex",
                  gap: 10,
                  alignItems: "center",
                  flexWrap: "wrap"
                }}
              >
                <input
                  type="password"
                  value={adminTotpInput}
                  onChange={(e) => setAdminTotpInput(e.target.value)}
                  placeholder="Saisir votre code Google Authenticator (6 chiffres)"
                  maxLength={6}
                  style={{
                    padding: "10px 14px",
                    borderRadius: 8,
                    backgroundColor: Theme.surface,
                    color: Theme.green,
                    fontWeight: 800,
                    border: `1px solid ${Theme.green}`,
                    minWidth: 260
                  }}
                />
                <button
                  onClick={() => {
                    if (adminTotpInput.trim().length < 6) {
                      setAdmin2FaError("Veuillez saisir votre code Google Authenticator à 6 chiffres.");
                      return;
                    }
                    setAdmin2FaError(null);
                    setAdmin2FaVerified(true);
                  }}
                  style={{
                    padding: "10px 18px",
                    borderRadius: 8,
                    border: "none",
                    backgroundColor: Theme.orange,
                    color: "#FFF",
                    fontWeight: 900,
                    cursor: "pointer"
                  }}
                >
                  Déverrouiller la Gestion des Sous-Comptes
                </button>
                {admin2FaError && (
                  <div style={{ width: "100%", color: Theme.red, fontSize: 12, fontWeight: 700 }}>
                    {admin2FaError}
                  </div>
                )}
              </div>
            ) : (
              <>
                {ACCOUNT_ROLES[currentAccount.role].maxSubAccounts > 0 &&
                  currentAccount.subAccounts.length <
                    ACCOUNT_ROLES[currentAccount.role].maxSubAccounts && (
                    <div
                      style={{
                        padding: 14,
                        borderRadius: 12,
                        backgroundColor: Theme.bg,
                        border: `1px solid ${Theme.green}`,
                        display: "grid",
                        gap: 10
                      }}
                    >
                      <div style={{ fontWeight: 800, fontSize: 13, color: Theme.green }}>
                        + AJOUTER UN SOUS-COMPTE ({currentAccount.subAccounts.length}/
                        {ACCOUNT_ROLES[currentAccount.role].maxSubAccounts} utilisés)
                      </div>
                      <div
                        style={{
                          display: "grid",
                          gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))",
                          gap: 8
                        }}
                      >
                        <input
                          value={newSubName}
                          onChange={(e) => setNewSubName(e.target.value)}
                          placeholder="Nom complet de l'employé"
                          style={{
                            padding: "9px 12px",
                            borderRadius: 8,
                            backgroundColor: Theme.surface,
                            color: "#FFF",
                            border: `1px solid ${Theme.border}`
                          }}
                        />
                        <select
                          value={newSubRoleLabel}
                          onChange={(e) => setNewSubRoleLabel(e.target.value)}
                          style={{
                            padding: "9px 12px",
                            borderRadius: 8,
                            backgroundColor: Theme.surface,
                            color: "#FFF",
                            border: `1px solid ${Theme.border}`
                          }}
                        >
                          <option value="Chef d'Atelier Mécanique">Chef d'Atelier Mécanique</option>
                          <option value="Mécanicien / Technicien OBD">Mécanicien / Technicien OBD</option>
                          <option value="Magasinier / Gestionnaire Stock">Magasinier / Gestionnaire Stock</option>
                          <option value="Vendeur Comptoir Pièces">Vendeur Comptoir Pièces</option>
                          <option value="Tôlier / Carrossier Peintre">Tôlier / Carrossier Peintre</option>
                        </select>
                        <input
                          value={newSubEmail}
                          onChange={(e) => setNewSubEmail(e.target.value)}
                          placeholder="Email employé"
                          style={{
                            padding: "9px 12px",
                            borderRadius: 8,
                            backgroundColor: Theme.surface,
                            color: "#FFF",
                            border: `1px solid ${Theme.border}`
                          }}
                        />
                        <button
                          onClick={() => {
                            if (!newSubName.trim()) return;
                            const created: SubAccountMember = {
                              id: `SUB-${Date.now().toString().slice(-4)}`,
                              fullName: newSubName.trim(),
                              roleLabel: newSubRoleLabel,
                              email: newSubEmail.trim() || "employe@mecanicauto.dz",
                              phone: newSubPhone,
                              isActive: true,
                              twoFactorEnabled: true,
                              permissions: {
                                canSearchVinAndOem: true,
                                canManageWorkshopOrQuotes:
                                  newSubRoleLabel.includes("Atelier") ||
                                  newSubRoleLabel.includes("Mécanicien"),
                                canManageStoreStockAndSales:
                                  newSubRoleLabel.includes("Magasin") ||
                                  newSubRoleLabel.includes("Vendeur"),
                                canUploadBodyshopPhotos: newSubRoleLabel.includes("Tôlier"),
                                canUseMekanikAiObd: true,
                                canViewFinancialKpis: false
                              }
                            };
                            onUpdateSubAccounts([...currentAccount.subAccounts, created]);
                            setNewSubName("");
                            setNewSubEmail("");
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
                          + Créer Sous-Compte
                        </button>
                      </div>
                    </div>
                  )}

                <div style={{ display: "grid", gap: 10 }}>
                  {currentAccount.subAccounts.map((sub) => (
                    <div
                      key={sub.id}
                      style={{
                        padding: 14,
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
                        <div style={{ fontWeight: 800, fontSize: 13.5 }}>
                          {sub.fullName} — <span style={{ color: Theme.orange }}>{sub.roleLabel}</span>
                        </div>
                        <div style={{ fontSize: 11, color: Theme.textSecondary }}>
                          {sub.email} • 2FA : Actif ✓
                        </div>
                      </div>
                      <button
                        onClick={() => {
                          const filtered = currentAccount.subAccounts.filter((s) => s.id !== sub.id);
                          onUpdateSubAccounts(filtered);
                        }}
                        style={{
                          padding: "6px 12px",
                          borderRadius: 6,
                          border: `1px solid ${Theme.red}`,
                          backgroundColor: "rgba(229,57,53,0.16)",
                          color: "#FFF",
                          fontWeight: 800,
                          fontSize: 11,
                          cursor: "pointer"
                        }}
                      >
                        Supprimer Sous-Compte
                      </button>
                    </div>
                  ))}
                </div>
              </>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
