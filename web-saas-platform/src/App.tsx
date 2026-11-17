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

export const App: React.FC = () => {
  const [activeSpace, setActiveSpace] = useState<SaasSpaceKey>("SITE_PUBLIC");
  const [searchMode, setSearchMode] = useState<"VIN" | "CODE_MOTEUR" | "VEHICULE">("VIN");
  const [vinQuery, setVinQuery] = useState("VF3L35G9HJS109482");
  const [activeOrgId, setActiveOrgId] = useState("ORG-ATELIER-ALGER-01");

  const spaces: { key: SaasSpaceKey; code: string; label: string; subtitle: string }[] = [
    { key: "SITE_PUBLIC", code: "A", label: "Site Public", subtitle: "Accueil, tarifs & inscription" },
    { key: "ESPACE_UTILISATEUR", code: "B", label: "Espace Utilisateur", subtitle: "Profil, MécaVerif+ & historique" },
    { key: "ESPACE_ATELIER", code: "C", label: "Espace Atelier", subtitle: "VIN, OR/Devis, clients & B2B" },
    { key: "ESPACE_MAGASIN", code: "D", label: "Espace Magasin", subtitle: "Catalogue OEM, stock & commandes" },
    { key: "ESPACE_FOURNISSEUR", code: "E", label: "Espace Fournisseur", subtitle: "Grossiste B2B, prix & demandes" },
    { key: "ADMINISTRATION_GENERALE", code: "F", label: "Administration Générale", subtitle: "2FA TOTP+SMS, API TecDoc/ETKA" }
  ];

  return (
    <div style={{ display: "flex", minHeight: "100vh", backgroundColor: MekanikTheme.bg, color: MekanikTheme.textPrimary, fontFamily: "Inter, system-ui, sans-serif" }}>
      {/* Barre latérale PC SaaS */}
      <aside style={{ width: 265, backgroundColor: MekanikTheme.surface, borderRight: `1px solid ${MekanikTheme.border}`, padding: 18, display: "flex", flexDirection: "column", justifyContent: "space-between" }}>
        <div>
          <div style={{ fontSize: 20, fontWeight: 900, letterSpacing: 0.5, marginBottom: 4 }}>
            MEKANIK<span style={{ color: MekanikTheme.orange }}>+</span> SAAS
          </div>
          <div style={{ fontSize: 11, color: MekanikTheme.textSecondary, marginBottom: 16 }}>
            Tenant isolé : <strong style={{ color: MekanikTheme.orange }}>{activeOrgId}</strong>
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
                    padding: "10px 12px",
                    borderRadius: 10,
                    border: `1px solid ${selected ? MekanikTheme.orange : MekanikTheme.border}`,
                    backgroundColor: selected ? "rgba(240, 120, 54, 0.16)" : MekanikTheme.bg,
                    color: MekanikTheme.textPrimary,
                    cursor: "pointer"
                  }}
                >
                  <div style={{ fontWeight: 700, fontSize: 13 }}>
                    [{sp.code}] {sp.label}
                  </div>
                  <div style={{ fontSize: 11, color: MekanikTheme.textSecondary }}>{sp.subtitle}</div>
                </button>
              );
            })}
          </nav>
        </div>

        <div style={{ padding: 12, borderRadius: 10, backgroundColor: MekanikTheme.bg, border: `1px solid ${MekanikTheme.border}`, fontSize: 11 }}>
          <div style={{ color: MekanikTheme.green, fontWeight: 700 }}>● SÉCURITÉ 2FA TOTP + SMS</div>
          <div style={{ color: MekanikTheme.textSecondary, marginTop: 4 }}>
            API connectées : TecDoc • ETKA • Stellantis ServiceBox • VIN Master
          </div>
        </div>
      </aside>

      {/* Zone centrale */}
      <main style={{ flex: 1, padding: 28 }}>
        <header style={{ display: "flex", justifyContent: "space-between", alignItems: "center", padding: 18, backgroundColor: MekanikTheme.surface, borderRadius: 14, border: `1px solid ${MekanikTheme.border}`, marginBottom: 20 }}>
          <div>
            <h1 style={{ margin: 0, fontSize: 22 }}>
              MEKANIK<span style={{ color: MekanikTheme.orange }}>+</span> — La plateforme automobile nouvelle génération
            </h1>
            <p style={{ margin: "4px 0 0", color: MekanikTheme.textSecondary, fontSize: 13 }}>
              Architecture SaaS Web & Mobile • Isolation Multi-Tenant ({activeOrgId})
            </p>
          </div>
          <select
            value={activeOrgId}
            onChange={(e) => setActiveOrgId(e.target.value)}
            style={{ backgroundColor: MekanikTheme.bg, color: MekanikTheme.textPrimary, border: `1px solid ${MekanikTheme.orange}`, borderRadius: 8, padding: "8px 12px" }}
          >
            <option value="ORG-ATELIER-ALGER-01">Atelier MécaTech Pro (Alger)</option>
            <option value="ORG-MAGASIN-ORAN-02">Magasin AutoParts Pro (Oran)</option>
            <option value="ORG-FOURNISSEUR-DZ-03">EuroParts Grossiste B2B (Algérie)</option>
          </select>
        </header>

        {/* Recherche centrale [ VIN ] [ CODE MOTEUR ] [ RECHERCHE VÉHICULE ] */}
        <section style={{ padding: 20, backgroundColor: MekanikTheme.surface, borderRadius: 14, border: `1px solid ${MekanikTheme.orange}`, marginBottom: 20 }}>
          <div style={{ display: "flex", gap: 10, marginBottom: 12 }}>
            {(["VIN", "CODE_MOTEUR", "VEHICULE"] as const).map((m) => (
              <button
                key={m}
                onClick={() => setSearchMode(m)}
                style={{
                  padding: "8px 16px",
                  borderRadius: 8,
                  border: `1px solid ${searchMode === m ? MekanikTheme.orange : MekanikTheme.border}`,
                  backgroundColor: searchMode === m ? MekanikTheme.orange : MekanikTheme.bg,
                  color: "#FFFFFF",
                  fontWeight: 800,
                  cursor: "pointer"
                }}
              >
                [ {m === "CODE_MOTEUR" ? "CODE MOTEUR" : m === "VEHICULE" ? "RECHERCHE VÉHICULE" : "VIN"} ]
              </button>
            ))}
          </div>
          <div style={{ display: "flex", gap: 10 }}>
            <input
              value={vinQuery}
              onChange={(e) => setVinQuery(e.target.value)}
              placeholder="Saisir VIN (17 caractères), Code Moteur ou Véhicule..."
              style={{ flex: 1, padding: "12px 14px", borderRadius: 10, border: `1px solid ${MekanikTheme.border}`, backgroundColor: MekanikTheme.bg, color: MekanikTheme.textPrimary, fontSize: 14 }}
            />
            <button
              style={{ padding: "12px 22px", borderRadius: 10, border: "none", backgroundColor: MekanikTheme.orange, color: "#FFFFFF", fontWeight: 900, cursor: "pointer" }}
            >
              IDENTIFIER
            </button>
          </div>
        </section>
      </main>
    </div>
  );
};

export default App;
