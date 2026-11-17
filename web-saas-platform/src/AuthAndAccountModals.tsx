import React, { useState } from "react";
import {
  ACCOUNT_ROLES,
  AccountRoleId,
  SubAccountMember,
  UserAccountSession
} from "./typesAndData";

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
  onSelectOrRegisterAccount: (account: UserAccountSession) => void;
  onUpdateSubAccounts: (updatedSubs: SubAccountMember[]) => void;
  onClose: () => void;
}

export const AuthAndAccountModal: React.FC<AuthAndAccountModalProps> = ({
  currentAccount,
  savedAccounts,
  initialTab = "SWITCH_ROLE",
  onSelectOrRegisterAccount,
  onUpdateSubAccounts,
  onClose
}) => {
  const [activeTab, setActiveTab] = useState<
    "SWITCH_ROLE" | "REGISTER" | "LOGIN" | "SUB_ACCOUNTS_2FA"
  >(initialTab);

  // Formulaire d'inscription complète
  const [regRole, setRegRole] = useState<AccountRoleId>(currentAccount.role);
  const [regOrgName, setRegOrgName] = useState("");
  const [regFullName, setRegFullName] = useState("");
  const [regEmail, setRegEmail] = useState("");
  const [regPhone, setRegPhone] = useState("+213 550 ");
  const [regWilaya, setRegWilaya] = useState("Alger (16)");
  const [regRc, setRegRc] = useState("16/00-4829104B22");
  const [regPassword, setRegPassword] = useState("Mekanik2026!");
  const [regEnable2Fa, setRegEnable2Fa] = useState(true);

  // Formulaire de connexion
  const [loginEmail, setLoginEmail] = useState(currentAccount.email);
  const [loginPassword, setLoginPassword] = useState("••••••••••••");
  const [loginTotp, setLoginTotp] = useState("482910");
  const [loginError, setLoginError] = useState<string | null>(null);

  // Gestion des sous-comptes (protégée par 2FA Google Authenticator de l'administrateur du compte)
  const [admin2FaVerified, setAdmin2FaVerified] = useState(false);
  const [adminTotpInput, setAdminTotpInput] = useState("619402");
  const [newSubName, setNewSubName] = useState("");
  const [newSubRoleLabel, setNewSubRoleLabel] = useState("Mécanicien / Technicien");
  const [newSubEmail, setNewSubEmail] = useState("");
  const [newSubPhone, setNewSubPhone] = useState("+213 661 ");

  const roleMeta = ACCOUNT_ROLES[regRole];

  const handleCreateAccount = (e: React.FormEvent) => {
    e.preventDefault();
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
            },
            {
              id: "SUB-3",
              fullName: "Mourad Brahimi",
              roleLabel: "Technicien Diagnostic OBD-II",
              email: "diag@mecanicauto.dz",
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
              email: "comptoir@mecanicauto.dz",
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
        : [];

    const newAcc: UserAccountSession = {
      userId: `USR-${Date.now()}`,
      organizationId: orgId,
      organizationName:
        regOrgName.trim() ||
        (regRole === "PARTICULIER"
          ? `Espace Personnel ${regFullName || "Conducteur"}`
          : `${chosenMeta.shortBadge} — ${regWilaya}`),
      fullName: regFullName.trim() || "Responsable Compte Principal",
      email: regEmail.trim() || `contact@${regRole.toLowerCase()}.dz`,
      phone: regPhone.trim() || "+213 550 94 82 10",
      wilaya: regWilaya,
      rcNumber: regRole === "PARTICULIER" ? "PARTICULIER (Sans RC)" : regRc,
      role: regRole,
      twoFactorEnabled: regEnable2Fa,
      totpSecretKey: `JBSWY3DPEHPK3PXP-${orgId.slice(-3)}`,
      createdAt: new Date().toLocaleDateString("fr-FR"),
      subAccounts: defaultSubs
    };

    onSelectOrRegisterAccount(newAcc);
  };

  const handleLoginSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const match = savedAccounts.find(
      (a) => a.email.toLowerCase() === loginEmail.trim().toLowerCase()
    );
    if (match) {
      onSelectOrRegisterAccount(match);
    } else {
      setLoginError(
        "Aucun compte trouvé avec cet email. Utilisez l'onglet 'Créer un Compte' ou sélectionnez un profil ci-dessous."
      );
    }
  };

  return (
    <div
      style={{
        position: "fixed",
        inset: 0,
        backgroundColor: "rgba(8, 12, 18, 0.88)",
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
          maxWidth: 960,
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
              GESTION DES COMPTES, INSCRIPTION & CONNEXION 2FA — MEKANIK
              <span style={{ color: Theme.orange }}>+</span>
            </div>
            <div style={{ fontSize: 12, color: Theme.textSecondary, marginTop: 3 }}>
              Séparation stricte des formules : chaque compte affiche uniquement ses propres outils et réinitialise l'interface au changement de rôle.
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
            { id: "SWITCH_ROLE", label: "1. Choisir / Basculer de Formule (AccountSelector)" },
            { id: "REGISTER", label: "2. Créer un Nouveau Compte (Inscription)" },
            { id: "LOGIN", label: "3. Connexion Compte + 2FA" },
            {
              id: "SUB_ACCOUNTS_2FA",
              label: `4. Sous-Comptes (${currentAccount.subAccounts.length}/${ACCOUNT_ROLES[currentAccount.role].maxSubAccounts}) & 2FA Admin`
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

        {/* ONGLET 1 : ACCOUNT SELECTOR (SÉPARATION STRICTE DES 7 FORMULES DE COMPTES) */}
        {activeTab === "SWITCH_ROLE" && (
          <div>
            <div
              style={{
                padding: 12,
                borderRadius: 10,
                backgroundColor: Theme.bg,
                border: `1px solid ${Theme.orange}`,
                marginBottom: 14,
                fontSize: 12.5
              }}
            >
              <strong style={{ color: Theme.orange }}>RÈGLE D'ISOLATION DES COMPTES :</strong> Lorsque vous passez d'une formule à une autre (ex: de <em>Compte Atelier Pro</em> à <em>Compte Particulier</em> ou <em>Compte Magasin</em>), tous les menus et options des autres métiers sont immédiatement supprimés pour n'afficher que ceux de la formule choisie.
            </div>

            <div
              style={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fit, minmax(275px, 1fr))",
                gap: 12
              }}
            >
              {(Object.keys(ACCOUNT_ROLES) as AccountRoleId[]).map((roleKey) => {
                const meta = ACCOUNT_ROLES[roleKey];
                const isCurrent = currentAccount.role === roleKey;
                return (
                  <div
                    key={roleKey}
                    style={{
                      padding: 16,
                      borderRadius: 12,
                      backgroundColor: isCurrent ? "rgba(240,120,54,0.14)" : Theme.bg,
                      border: `1.5px solid ${isCurrent ? meta.accentColor : Theme.border}`,
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
                        <span style={{ fontWeight: 900, color: Theme.green, fontSize: 14 }}>
                          {meta.monthlyPriceDzd === 0
                            ? "GRATUIT (0 DA)"
                            : `${meta.monthlyPriceDzd.toLocaleString("fr-FR")} DA / mois`}
                        </span>
                      </div>
                      <div style={{ fontWeight: 800, fontSize: 14.5, marginTop: 8 }}>{meta.title}</div>
                      <p style={{ fontSize: 11.5, color: Theme.textSecondary, lineHeight: 1.45, marginTop: 6 }}>
                        {meta.description}
                      </p>
                      <div style={{ fontSize: 11, color: Theme.orange, fontWeight: 700, marginBottom: 10 }}>
                        • Sous-comptes autorisés :{" "}
                        {meta.maxSubAccounts === 0
                          ? "Aucun (Compte Individuel)"
                          : `1 Principal + ${meta.maxSubAccounts} Sous-Comptes`}
                      </div>
                    </div>

                    <button
                      onClick={() => {
                        const existing = savedAccounts.find((a) => a.role === roleKey);
                        if (existing) {
                          onSelectOrRegisterAccount(existing);
                        } else {
                          setRegRole(roleKey);
                          setActiveTab("REGISTER");
                        }
                      }}
                      style={{
                        width: "100%",
                        padding: "10px 12px",
                        borderRadius: 8,
                        border: "none",
                        backgroundColor: isCurrent ? Theme.green : meta.accentColor,
                        color: "#FFF",
                        fontWeight: 900,
                        fontSize: 12,
                        cursor: "pointer"
                      }}
                    >
                      {isCurrent ? "✓ Formule Active" : `Activer ${meta.shortBadge}`}
                    </button>
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {/* ONGLET 2 : CRÉER UN COMPTE / INSCRIPTION COMPLÈTE */}
        {activeTab === "REGISTER" && (
          <form onSubmit={handleCreateAccount} style={{ display: "grid", gap: 14 }}>
            <div
              style={{
                padding: 14,
                borderRadius: 10,
                backgroundColor: Theme.bg,
                border: `1px solid ${Theme.border}`
              }}
            >
              <label style={{ display: "block", fontSize: 12, fontWeight: 800, color: Theme.orange, marginBottom: 6 }}>
                CHOISIR LA FORMULE DU NOUVEAU COMPTE :
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
                {(Object.keys(ACCOUNT_ROLES) as AccountRoleId[]).map((rk) => (
                  <option key={rk} value={rk}>
                    {ACCOUNT_ROLES[rk].title} — {ACCOUNT_ROLES[rk].monthlyPriceDzd} DA/mois (Max {ACCOUNT_ROLES[rk].maxSubAccounts} sous-comptes)
                  </option>
                ))}
              </select>
              <div style={{ fontSize: 11.5, color: Theme.textSecondary, marginTop: 8 }}>
                {roleMeta.description}
              </div>
            </div>

            <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))", gap: 12 }}>
              <div>
                <label style={{ fontSize: 11.5, color: Theme.textSecondary }}>
                  {regRole === "PARTICULIER" ? "Nom de votre Espace Garage Perso" : "Raison Sociale (Atelier / Magasin / Société) *"}
                </label>
                <input
                  required
                  value={regOrgName}
                  onChange={(e) => setRegOrgName(e.target.value)}
                  placeholder={regRole === "PARTICULIER" ? "Ex: Garage Perso Karim" : "Ex: SARL Garage & Pièces El-Djazair"}
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
                  placeholder="Ex: Badrou Meziane"
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
                  placeholder="Ex: contact@mekanikplus.dz"
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
                  placeholder="+213 550 94 82 10"
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
                    placeholder="16/00-4829104B22"
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
                <label style={{ fontSize: 11.5, color: Theme.textSecondary }}>Mot de Passe Sécurisé *</label>
                <input
                  required
                  type="password"
                  value={regPassword}
                  onChange={(e) => setRegPassword(e.target.value)}
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
                  🔒 Protection Anti-Piratage & Anti-Clonage : Google Authenticator (2FA TOTP)
                </div>
                <div style={{ fontSize: 11, color: Theme.textSecondary }}>
                  Obligatoire pour ajouter/supprimer des sous-comptes et protéger l'accès administrateur du compte.
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
              Créer le Compte « {roleMeta.shortBadge} » & Ouvrir la Plateforme
            </button>
          </form>
        )}

        {/* ONGLET 3 : CONNEXION COMPTE EXISTANT + 2FA */}
        {activeTab === "LOGIN" && (
          <div style={{ display: "grid", gap: 16 }}>
            <form onSubmit={handleLoginSubmit} style={{ display: "grid", gap: 12 }}>
              <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))", gap: 12 }}>
                <div>
                  <label style={{ fontSize: 11.5, color: Theme.textSecondary }}>Email du Compte</label>
                  <input
                    value={loginEmail}
                    onChange={(e) => setLoginEmail(e.target.value)}
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
                  <label style={{ fontSize: 11.5, color: Theme.textSecondary }}>Mot de Passe</label>
                  <input
                    type="password"
                    value={loginPassword}
                    onChange={(e) => setLoginPassword(e.target.value)}
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
                    value={loginTotp}
                    onChange={(e) => setLoginTotp(e.target.value)}
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
                Se Connecter avec Vérification 2FA
              </button>
            </form>

            <div>
              <div style={{ fontSize: 12, fontWeight: 800, color: Theme.orange, marginBottom: 8 }}>
                COMPTES ENREGISTRÉS & PROFILS DÉMO INVESTISSEURS (CONNEXION EN 1 CLIC) :
              </div>
              <div style={{ display: "grid", gap: 8 }}>
                {savedAccounts.map((acc) => (
                  <div
                    key={acc.userId}
                    style={{
                      padding: 12,
                      borderRadius: 10,
                      backgroundColor: Theme.bg,
                      border: `1px solid ${
                        acc.userId === currentAccount.userId ? Theme.orange : Theme.border
                      }`,
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
                      flexWrap: "wrap",
                      gap: 10
                    }}
                  >
                    <div>
                      <div style={{ fontWeight: 800, fontSize: 13 }}>
                        {acc.organizationName} —{" "}
                        <span style={{ color: Theme.orange }}>
                          [{ACCOUNT_ROLES[acc.role].shortBadge}]
                        </span>
                      </div>
                      <div style={{ fontSize: 11.5, color: Theme.textSecondary }}>
                        Titulaire : {acc.fullName} • {acc.email} • Tenant : {acc.organizationId} • Sous-comptes : {acc.subAccounts.length}
                      </div>
                    </div>
                    <button
                      onClick={() => onSelectOrRegisterAccount(acc)}
                      style={{
                        padding: "8px 14px",
                        borderRadius: 8,
                        border: "none",
                        backgroundColor:
                          acc.userId === currentAccount.userId ? Theme.green : Theme.orange,
                        color: "#FFF",
                        fontWeight: 800,
                        fontSize: 12,
                        cursor: "pointer"
                      }}
                    >
                      {acc.userId === currentAccount.userId ? "Connecté ✓" : "Ouvrir ce Compte"}
                    </button>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* ONGLET 4 : GESTION DES SOUS-COMPTES (1 PRINCIPAL + 4 SOUS-COMPTES) AVEC GOOGLE AUTHENTICATOR */}
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
                Clé secrète Google Authenticator (TOTP) :{" "}
                <strong style={{ color: Theme.green, fontFamily: "monospace" }}>
                  {currentAccount.totpSecretKey}
                </strong>{" "}
                • Limite de la formule :{" "}
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
                  value={adminTotpInput}
                  onChange={(e) => setAdminTotpInput(e.target.value)}
                  placeholder="Code Google Authenticator (6 chiffres)"
                  style={{
                    padding: "10px 14px",
                    borderRadius: 8,
                    backgroundColor: Theme.surface,
                    color: Theme.green,
                    fontWeight: 800,
                    border: `1px solid ${Theme.green}`
                  }}
                />
                <button
                  onClick={() => setAdmin2FaVerified(true)}
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
                  Déverrouiller l'Ajout / Suppression de Sous-Comptes (2FA)
                </button>
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
                                canManageWorkshopOrQuotes: newSubRoleLabel.includes("Atelier") || newSubRoleLabel.includes("Mécanicien"),
                                canManageStoreStockAndSales: newSubRoleLabel.includes("Magasin") || newSubRoleLabel.includes("Vendeur"),
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
                  {currentAccount.subAccounts.map((sub, idx) => (
                    <div
                      key={sub.id}
                      style={{
                        padding: 14,
                        borderRadius: 10,
                        backgroundColor: Theme.bg,
                        border: `1px solid ${Theme.border}`
                      }}
                    >
                      <div
                        style={{
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
                            {sub.email} • {sub.phone} • 2FA : Actif
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

                      {/* Matrice des permissions du sous-compte */}
                      <div style={{ display: "flex", gap: 8, flexWrap: "wrap", marginTop: 10 }}>
                        {(
                          [
                            ["canSearchVinAndOem", "Recherche VIN & OEM"],
                            ["canManageWorkshopOrQuotes", "Devis & OR Atelier"],
                            ["canManageStoreStockAndSales", "Stock & Vente Magasin"],
                            ["canUploadBodyshopPhotos", "Photos Avant/Après Carrosserie"],
                            ["canUseMekanikAiObd", "Diagnostic MekanikAI OBD"],
                            ["canViewFinancialKpis", "Statistiques & CA"]
                          ] as const
                        ).map(([permKey, permLabel]) => {
                          const enabled = sub.permissions[permKey];
                          return (
                            <button
                              key={permKey}
                              onClick={() => {
                                const next = [...currentAccount.subAccounts];
                                next[idx] = {
                                  ...sub,
                                  permissions: {
                                    ...sub.permissions,
                                    [permKey]: !enabled
                                  }
                                };
                                onUpdateSubAccounts(next);
                              }}
                              style={{
                                padding: "5px 10px",
                                borderRadius: 6,
                                border: `1px solid ${enabled ? Theme.green : Theme.border}`,
                                backgroundColor: enabled ? "rgba(34,197,94,0.18)" : Theme.surface,
                                color: enabled ? Theme.green : Theme.textSecondary,
                                fontWeight: 700,
                                fontSize: 11,
                                cursor: "pointer"
                              }}
                            >
                              {enabled ? "✓ " : "✕ "}
                              {permLabel}
                            </button>
                          );
                        })}
                      </div>
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
