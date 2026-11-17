import React, { useState } from "react";
import {
  BodyshopDamagePhotoRecord,
  MecaVerifVinReport,
  PRELOADED_VIN_REPORTS
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

interface MecaVerifAndBodyshopProps {
  initialVin: string;
  isProAccount: boolean;
  isBodyshopAccount: boolean;
  onOrderPartB2b: (oemRef: string, partName: string, priceDzd: number) => void;
  onShowToast: (msg: string) => void;
}

export const MecaVerifAndBodyshopModule: React.FC<MecaVerifAndBodyshopProps> = ({
  initialVin,
  isProAccount,
  isBodyshopAccount,
  onOrderPartB2b,
  onShowToast
}) => {
  const [vinInput, setVinInput] = useState(initialVin || "WVWZZZAUZHW129834");
  const [reportsMap, setReportsMap] = useState<Record<string, MecaVerifVinReport>>(
    PRELOADED_VIN_REPORTS
  );

  const activeReport: MecaVerifVinReport =
    reportsMap[vinInput.trim().toUpperCase()] || {
      ...PRELOADED_VIN_REPORTS.WVWZZZAUZHW129834,
      vin: vinInput.trim().toUpperCase() || "WVWZZZAUZHW129834",
      vehicleTitle: `Véhicule Identifié par Châssis (${vinInput.trim().toUpperCase()})`
    };

  const [currentKmInput, setCurrentKmInput] = useState<number>(activeReport.defaultCurrentKm);
  const [unlockedVins, setUnlockedVins] = useState<string[]>([]);
  const [showCheckoutModal, setShowCheckoutModal] = useState(false);
  const [checkoutStep, setCheckoutStep] = useState<1 | 2 | 3>(1);
  const [selectedTariff, setSelectedTariff] = useState<250 | 300>(isProAccount ? 250 : 300);
  const [selectedGateway, setSelectedGateway] = useState<
    "SATIM_CIB" | "EDAHABIA" | "BARIDIMOB_QR" | "CHARGILY_PAY" | "STRIPE_INTL"
  >("SATIM_CIB");
  const [cardNumber, setCardNumber] = useState("6274 8201 9482 4829");
  const [cardHolder, setCardHolder] = useState("ATELIER MECANICAUTO PRO");
  const [cardExpiry, setCardExpiry] = useState("09/28");
  const [cardCvv, setCardCvv] = useState("842");
  const [otpInput, setOtpInput] = useState("739204");
  const [receiptsHistory, setReceiptsHistory] = useState<
    {
      receiptId: string;
      vin: string;
      amountDzd: number;
      gateway: string;
      date: string;
    }[]
  >([
    {
      receiptId: "MV-SATIM-2026-9041",
      vin: "WVWZZZ5NZMW123456",
      amountDzd: 250,
      gateway: "SATIM_CIB",
      date: "16/11/2026 • 14:22"
    }
  ]);

  // Espace Tôlier / Carrosserie : Obligation de prendre les photos AVANT et APRÈS
  const [impactZoneInput, setImpactZoneInput] = useState(
    "Aile Avant Droite & Pare-chocs Plastique"
  );
  const [repairNotesInput, setRepairNotesInput] = useState(
    "Débosselage tôle, remplacement agrafes OEM et peinture étuve code constructeur"
  );
  const [beforePhotoCaptured, setBeforePhotoCaptured] = useState(false);
  const [afterPhotoCaptured, setAfterPhotoCaptured] = useState(false);
  const [partCategoryFilter, setPartCategoryFilter] = useState<string>("TOUT");

  const isUnlocked = unlockedVins.includes(activeReport.vin);
  const maxHistoricalKm = Math.max(
    ...activeReport.mileageHistory.filter((p) => !p.isFraudRollback).map((p) => p.km),
    0
  );
  const isRollbackFraud = currentKmInput < maxHistoricalKm;
  const rollbackDiff = isRollbackFraud ? maxHistoricalKm - currentKmInput : 0;

  const handleSelectSampleVin = (sampleVin: string) => {
    setVinInput(sampleVin);
    const rep = reportsMap[sampleVin];
    if (rep) {
      setCurrentKmInput(rep.defaultCurrentKm);
    }
  };

  const handleCertifyBodyshopPhotos = () => {
    if (!beforePhotoCaptured || !afterPhotoCaptured) return;
    const newRec: BodyshopDamagePhotoRecord = {
      id: `BODY-${Date.now().toString().slice(-4)}`,
      date: new Date().toLocaleDateString("fr-FR"),
      km: currentKmInput,
      shopName: "Atelier Carrosserie & Tôlerie Certifié MEKANIK+",
      impactZone: impactZoneInput,
      severity: "Réparation Tôlerie & Peinture avec Dossier Photos Avant/Après Obligatoires",
      isStructural: false,
      beforePhotoLabel: `Photo AVANT Obligatoire : ${impactZoneInput} (Dommages tôle constatés)`,
      beforeMicrons: 105,
      afterPhotoLabel: `Photo APRÈS Obligatoire : ${impactZoneInput} (Tôlerie redressée & peinture étuve ${activeReport.paintCode})`,
      afterMicrons: 180,
      paintCode: activeReport.paintCode,
      replacedPartsOem: activeReport.bodyshopPartsCatalog.slice(0, 2).map((p) => `${p.oemRef} (${p.partName})`),
      costDzd: 58000
    };

    setReportsMap({
      ...reportsMap,
      [activeReport.vin]: {
        ...activeReport,
        bodyshopRecords: [newRec, ...activeReport.bodyshopRecords]
      }
    });
    setBeforePhotoCaptured(false);
    setAfterPhotoCaptured(false);
    onShowToast(
      `Photos AVANT et APRÈS obligatoires certifiées et publiées sur le châssis ${activeReport.vin} !`
    );
  };

  return (
    <div style={{ display: "grid", gap: 18 }}>
      {/* BANNIÈRE MÉCAVERIF+ (SERVICE EXTERNE HORS ABONNEMENT : 250 DA OU 300 DA / VIN) */}
      <div
        style={{
          padding: 20,
          backgroundColor: Theme.surface,
          borderRadius: 16,
          border: `1.5px solid ${isUnlocked ? Theme.green : Theme.orange}`
        }}
      >
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            flexWrap: "wrap",
            gap: 12
          }}
        >
          <div>
            <div style={{ display: "flex", alignItems: "center", gap: 8, flexWrap: "wrap" }}>
              <span style={{ fontSize: 20, fontWeight: 900 }}>🛡️ MÉCAVERIF+™ (VerifyCar)</span>
              <span
                style={{
                  padding: "4px 10px",
                  borderRadius: 6,
                  backgroundColor: Theme.orange,
                  color: "#FFF",
                  fontWeight: 900,
                  fontSize: 11
                }}
              >
                SERVICE EXTERNE HORS ABONNEMENT • 250 DA (PRO) / 300 DA (PARTICULIER) PAR VIN
              </span>
            </div>
            <p style={{ margin: "6px 0 0", fontSize: 12.5, color: Theme.textSecondary }}>
              Graphique kilométrique style CarVertical, détection de compteur trafiqué, photos AVANT/APRÈS obligatoires des ateliers de carrosserie et passages mécanicien.
            </p>
          </div>

          <button
            onClick={() => {
              setCheckoutStep(1);
              setShowCheckoutModal(true);
            }}
            style={{
              padding: "12px 20px",
              borderRadius: 10,
              border: "none",
              backgroundColor: isUnlocked ? Theme.green : Theme.orange,
              color: "#FFF",
              fontWeight: 900,
              fontSize: 13,
              cursor: "pointer"
            }}
          >
            {isUnlocked
              ? `✓ Rapport VIN ${activeReport.vin} Débloqué (Racheter / Reçus)`
              : `🔓 Débloquer ce Châssis (${isProAccount ? 250 : 300} DA • CIB / Edahabia / Stripe)`}
          </button>
        </div>

        {/* SÉLECTEUR DE CHÂSSIS VIN & KILOMÉTRAGE COMPTEUR */}
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
            gap: 10,
            marginTop: 14
          }}
        >
          <div>
            <label style={{ fontSize: 11, color: Theme.textSecondary, fontWeight: 700 }}>
              NUMÉRO DE CHÂSSIS (VIN 17 CARACTÈRES)
            </label>
            <input
              value={vinInput}
              onChange={(e) => setVinInput(e.target.value.toUpperCase().slice(0, 17))}
              style={{
                width: "100%",
                marginTop: 4,
                padding: "10px 12px",
                borderRadius: 8,
                backgroundColor: Theme.bg,
                color: "#FFF",
                border: `1px solid ${Theme.border}`,
                fontFamily: "monospace",
                fontWeight: 800
              }}
            />
          </div>
          <div>
            <label style={{ fontSize: 11, color: Theme.textSecondary, fontWeight: 700 }}>
              KILOMÉTRAGE AFFICHÉ AU COMPTEUR (KM) — TESTEZ UN RECUL !
            </label>
            <input
              type="number"
              value={currentKmInput}
              onChange={(e) => setCurrentKmInput(Number(e.target.value) || 0)}
              style={{
                width: "100%",
                marginTop: 4,
                padding: "10px 12px",
                borderRadius: 8,
                backgroundColor: Theme.bg,
                color: isRollbackFraud ? Theme.red : Theme.green,
                border: `1px solid ${isRollbackFraud ? Theme.red : Theme.green}`,
                fontWeight: 900
              }}
            />
          </div>
        </div>

        {/* BOUTONS SCÉNARIOS DE DÉMONSTRATION INVESTISSEURS */}
        <div style={{ display: "flex", gap: 8, flexWrap: "wrap", marginTop: 12 }}>
          <button
            onClick={() => handleSelectSampleVin("WVWZZZAUZHW129834")}
            style={{
              padding: "7px 12px",
              borderRadius: 8,
              border: `1px solid ${Theme.green}`,
              backgroundColor:
                activeReport.vin === "WVWZZZAUZHW129834" ? "rgba(34,197,94,0.2)" : Theme.bg,
              color: "#FFF",
              fontWeight: 700,
              fontSize: 11.5,
              cursor: "pointer"
            }}
          >
            🟢 Scénario 1 : VW Golf 7.5 TDI • Kilométrage Sain (142 500 km) & Photo Aile
          </button>
          <button
            onClick={() => handleSelectSampleVin("VF3MCYHZJKS491820")}
            style={{
              padding: "7px 12px",
              borderRadius: 8,
              border: `1px solid ${Theme.red}`,
              backgroundColor:
                activeReport.vin === "VF3MCYHZJKS491820" ? "rgba(239,68,68,0.22)" : Theme.bg,
              color: "#FFF",
              fontWeight: 700,
              fontSize: 11.5,
              cursor: "pointer"
            }}
          >
            🔴 Scénario 2 : Peugeot 3008 • COMPTEUR TRAFIQUÉ (-82 300 km) & Choc Tôle Latéral
          </button>
        </div>
      </div>

      {/* 1. GRAPHIQUE KILOMÉTRIQUE INTERACTIF STYLE CARVERTICAL */}
      <div
        style={{
          padding: 20,
          backgroundColor: Theme.surface,
          borderRadius: 16,
          border: `1px solid ${isRollbackFraud ? Theme.red : Theme.border}`
        }}
      >
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            flexWrap: "wrap",
            gap: 8,
            marginBottom: 12
          }}
        >
          <div>
            <div style={{ fontWeight: 900, fontSize: 15 }}>
              📈 GRAPHIQUE D'ÉVOLUTION DU KILOMÉTRAGE (STYLE CARVERTICAL)
            </div>
            <div style={{ fontSize: 12, color: Theme.textSecondary }}>
              {activeReport.vehicleTitle} • Moteur : {activeReport.engineCode} • Peinture : {activeReport.paintCode}
            </div>
          </div>
          <span
            style={{
              padding: "5px 12px",
              borderRadius: 8,
              backgroundColor: isRollbackFraud
                ? "rgba(239,68,68,0.2)"
                : "rgba(34,197,94,0.2)",
              border: `1px solid ${isRollbackFraud ? Theme.red : Theme.green}`,
              color: isRollbackFraud ? Theme.red : Theme.green,
              fontWeight: 900,
              fontSize: 12
            }}
          >
            {isRollbackFraud
              ? `⚠️ ALERTE COMPTEUR TRAFIQUÉ : -${rollbackDiff.toLocaleString("fr-FR")} KM SUPPRIMÉS`
              : "✓ KILOMÉTRAGE CERTIFIÉ AUTHENTIQUE"}
          </span>
        </div>

        {/* COURBE SVG CARVERTICAL */}
        <div
          style={{
            backgroundColor: Theme.bg,
            borderRadius: 12,
            border: `1px solid ${Theme.border}`,
            padding: 16
          }}
        >
          {(() => {
            const pts = activeReport.mileageHistory.map((p, idx, arr) =>
              idx === arr.length - 1 ? { ...p, km: currentKmInput } : p
            );
            const maxKm = Math.max(...pts.map((p) => p.km), 50000);
            const svgW = 680;
            const svgH = 190;
            const padX = 50;
            const padY = 24;
            const plotW = svgW - padX * 2;
            const plotH = svgH - padY * 2;

            const coords = pts.map((p, i) => {
              const x = padX + (i / Math.max(pts.length - 1, 1)) * plotW;
              const y = padY + plotH - (p.km / maxKm) * plotH;
              return { x, y, ...p };
            });

            return (
              <svg
                viewBox={`0 0 ${svgW} ${svgH}`}
                style={{ width: "100%", height: "auto", maxHeight: 220 }}
              >
                {/* Lignes de grille horizontales */}
                {[0, 0.33, 0.66, 1].map((ratio, idx) => {
                  const y = padY + plotH * ratio;
                  const kmLabel = Math.round(((1 - ratio) * maxKm) / 1000);
                  return (
                    <g key={idx}>
                      <line
                        x1={padX}
                        y1={y}
                        x2={svgW - padX}
                        y2={y}
                        stroke="#253545"
                        strokeDasharray="6 4"
                      />
                      <text x={8} y={y + 4} fill="#94A3B8" fontSize="10" fontFamily="monospace">
                        {kmLabel}k
                      </text>
                    </g>
                  );
                })}

                {/* Segments entre les points */}
                {coords.slice(0, -1).map((c, i) => {
                  const next = coords[i + 1];
                  const isDrop = next.y > c.y + 1;
                  return (
                    <line
                      key={i}
                      x1={c.x}
                      y1={c.y}
                      x2={next.x}
                      y2={next.y}
                      stroke={isDrop ? "#EF4444" : "#22C55E"}
                      strokeWidth={isDrop ? 4 : 3.2}
                      strokeDasharray={isDrop ? "8 5" : undefined}
                    />
                  );
                })}

                {/* Points et étiquettes */}
                {coords.map((c, i) => {
                  const isDropPoint = i === coords.length - 1 && isRollbackFraud;
                  return (
                    <g key={i}>
                      <circle
                        cx={c.x}
                        cy={c.y}
                        r={6}
                        fill={isDropPoint ? "#EF4444" : "#22C55E"}
                        stroke="#101820"
                        strokeWidth={2}
                      />
                      <text
                        x={c.x}
                        y={c.y - 10}
                        textAnchor="middle"
                        fill={isDropPoint ? "#EF4444" : "#E8EDF2"}
                        fontSize="10"
                        fontWeight="bold"
                      >
                        {c.km.toLocaleString("fr-FR")} km
                      </text>
                      <text
                        x={c.x}
                        y={svgH - 4}
                        textAnchor="middle"
                        fill="#94A3B8"
                        fontSize="10"
                      >
                        {c.dateLabel}
                      </text>
                    </g>
                  );
                })}
              </svg>
            );
          })()}
        </div>
      </div>

      {/* 2. PHOTOS AVANT / APRÈS OBLIGATOIRES DES DOMMAGES SUR LA TÔLE */}
      <div
        style={{
          padding: 20,
          backgroundColor: Theme.surface,
          borderRadius: 16,
          border: `1px solid ${Theme.border}`
        }}
      >
        <div style={{ fontWeight: 900, fontSize: 15, marginBottom: 4 }}>
          📸 DOMMAGES SUR LA TÔLE & PHOTOS AVANT / APRÈS OBLIGATOIRES DES ATELIERS DE CARROSSERIE
        </div>
        <div style={{ fontSize: 12, color: Theme.textSecondary, marginBottom: 14 }}>
          Chaque atelier de carrosserie MEKANIK+ est dans l'obligation de photographier la tôle avant et après réparation.
        </div>

        <div style={{ display: "grid", gap: 14 }}>
          {activeReport.bodyshopRecords.map((rec) => (
            <div
              key={rec.id}
              style={{
                padding: 16,
                borderRadius: 12,
                backgroundColor: Theme.bg,
                border: `1px solid ${rec.isStructural ? Theme.red : Theme.orange}`
              }}
            >
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  flexWrap: "wrap",
                  gap: 8,
                  marginBottom: 8
                }}
              >
                <div>
                  <div style={{ fontWeight: 800, fontSize: 14 }}>
                    {rec.shopName} • Zone :{" "}
                    <span style={{ color: Theme.orange }}>{rec.impactZone}</span>
                  </div>
                  <div style={{ fontSize: 11.5, color: Theme.textSecondary }}>
                    Date : {rec.date} • Relevé : {rec.km.toLocaleString("fr-FR")} km • {rec.severity}
                  </div>
                </div>
                <span
                  style={{
                    padding: "4px 10px",
                    borderRadius: 6,
                    backgroundColor: "rgba(34,197,94,0.16)",
                    color: Theme.green,
                    fontWeight: 800,
                    fontSize: 10.5
                  }}
                >
                  ✓ PHOTOS AVANT & APRÈS CERTIFIÉES
                </span>
              </div>

              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))",
                  gap: 12,
                  marginTop: 10
                }}
              >
                {/* Photo AVANT */}
                <div
                  style={{
                    padding: 12,
                    borderRadius: 10,
                    backgroundColor: "#1A1317",
                    border: `1px solid ${Theme.red}`
                  }}
                >
                  <div style={{ fontWeight: 800, fontSize: 12, color: Theme.red }}>
                    1. {rec.beforePhotoLabel}
                  </div>
                  <svg viewBox="0 0 260 90" style={{ width: "100%", height: 80, marginTop: 6 }}>
                    <rect x="20" y="15" width="220" height="60" rx="10" fill="#2A1B21" stroke="#EF4444" />
                    <path
                      d="M 75 22 L 110 48 L 92 70 M 135 20 L 110 48 L 155 66"
                      stroke="#EF4444"
                      strokeWidth="3"
                      fill="none"
                    />
                    <circle cx="110" cy="48" r="14" fill="rgba(240,120,54,0.45)" />
                  </svg>
                  <div style={{ fontSize: 11, color: Theme.textSecondary, marginTop: 4 }}>
                    Épaisseur micromètre avant : {rec.beforeMicrons} µm • Tôle déformée
                  </div>
                </div>

                {/* Photo APRÈS */}
                <div
                  style={{
                    padding: 12,
                    borderRadius: 10,
                    backgroundColor: "#11221C",
                    border: `1px solid ${Theme.green}`
                  }}
                >
                  <div style={{ fontWeight: 800, fontSize: 12, color: Theme.green }}>
                    2. {rec.afterPhotoLabel}
                  </div>
                  <svg viewBox="0 0 260 90" style={{ width: "100%", height: 80, marginTop: 6 }}>
                    <rect x="20" y="15" width="220" height="60" rx="10" fill="#163026" stroke="#22C55E" />
                    <line x1="65" y1="65" x2="120" y2="22" stroke="#22C55E" strokeWidth="3" />
                    <line x1="115" y1="68" x2="175" y2="24" stroke="#38BDF8" strokeWidth="2.5" />
                  </svg>
                  <div style={{ fontSize: 11, color: Theme.green, marginTop: 4 }}>
                    Épaisseur micromètre après étuve ({rec.paintCode}) : {rec.afterMicrons} µm • Alignement validé
                  </div>
                </div>
              </div>

              <div style={{ fontSize: 11.5, color: Theme.textSecondary, marginTop: 8 }}>
                Pièces de tôlerie/plastique remplacées :{" "}
                <strong style={{ color: Theme.textPrimary }}>
                  {rec.replacedPartsOem.join(" • ")}
                </strong>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* 3. ESPACE SPÉCIAL CARROSSERIE & TÔLERIE (PIÈCES PAR CHÂSSIS + PRISE DE PHOTOS AVANT/APRÈS OBLIGATOIRES) */}
      <div
        style={{
          padding: 20,
          backgroundColor: Theme.surface,
          borderRadius: 16,
          border: `1.5px solid ${isBodyshopAccount ? Theme.red : Theme.orange}`
        }}
      >
        <div style={{ fontWeight: 900, fontSize: 16, color: Theme.orange }}>
          🔨 ESPACE SPÉCIAL CARROSSERIE & TÔLERIE (ACCÈS CHÂSSIS {activeReport.vin})
        </div>
        <div style={{ fontSize: 12, color: Theme.textSecondary, marginTop: 2, marginBottom: 14 }}>
          Catalogue de pièces de tôlerie, plastique, boucliers, optiques et agrafes filtré par numéro de châssis + Dépôt obligatoire des photos AVANT et APRÈS réparation.
        </div>

        {/* Filtre de catégories tôlerie/plastique */}
        <div style={{ display: "flex", gap: 8, flexWrap: "wrap", marginBottom: 12 }}>
          {[
            "TOUT",
            "TÔLERIE ACIER / ALU",
            "PLASTIQUE & BOUCLIERS",
            "OPTIQUES & VITRAGE",
            "CLIPS & AGRAFES"
          ].map((cat) => (
            <button
              key={cat}
              onClick={() => setPartCategoryFilter(cat)}
              style={{
                padding: "7px 12px",
                borderRadius: 8,
                border: `1px solid ${partCategoryFilter === cat ? Theme.orange : Theme.border}`,
                backgroundColor: partCategoryFilter === cat ? Theme.orange : Theme.bg,
                color: "#FFF",
                fontWeight: 800,
                fontSize: 11,
                cursor: "pointer"
              }}
            >
              {cat}
            </button>
          ))}
        </div>

        <div style={{ display: "grid", gap: 8, marginBottom: 16 }}>
          {activeReport.bodyshopPartsCatalog
            .filter(
              (p) => partCategoryFilter === "TOUT" || p.category === partCategoryFilter
            )
            .map((part) => (
              <div
                key={part.id}
                style={{
                  padding: 12,
                  borderRadius: 10,
                  backgroundColor: Theme.bg,
                  border: `1px solid ${Theme.border}`,
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                  flexWrap: "wrap",
                  gap: 10
                }}
              >
                <div>
                  <span
                    style={{
                      fontSize: 10,
                      color: Theme.orange,
                      fontWeight: 800
                    }}
                  >
                    [{part.category}] • {part.zone}
                  </span>
                  <div style={{ fontWeight: 800, fontSize: 13.5 }}>{part.partName}</div>
                  <div style={{ fontSize: 12, color: Theme.red, fontFamily: "monospace", fontWeight: 900 }}>
                    Réf Constructeur OEM : {part.oemRef}
                  </div>
                </div>
                <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
                  <span style={{ fontWeight: 900, fontSize: 14, color: Theme.green }}>
                    {part.priceDzd.toLocaleString("fr-FR")} DA
                  </span>
                  <button
                    onClick={() => onOrderPartB2b(part.oemRef, part.partName, part.priceDzd)}
                    style={{
                      padding: "8px 12px",
                      borderRadius: 8,
                      border: "none",
                      backgroundColor: Theme.orange,
                      color: "#FFF",
                      fontWeight: 800,
                      fontSize: 11.5,
                      cursor: "pointer"
                    }}
                  >
                    Commander Pièce Tôlerie
                  </button>
                </div>
              </div>
            ))}
        </div>

        {/* Formulaire d'obligation de prise de photos AVANT et APRÈS */}
        <div
          style={{
            padding: 16,
            borderRadius: 12,
            backgroundColor: Theme.bg,
            border: `1px solid ${Theme.border}`,
            display: "grid",
            gap: 10
          }}
        >
          <div style={{ fontWeight: 900, fontSize: 13, color: Theme.textPrimary }}>
            OBLIGATION TÔLIER : ENREGISTRER LES PHOTOS AVANT ET APRÈS RÉPARATION SUR LE CHÂSSIS
          </div>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))", gap: 10 }}>
            <input
              value={impactZoneInput}
              onChange={(e) => setImpactZoneInput(e.target.value)}
              placeholder="Zone de tôle réparée (ex: Aile Av. D)"
              style={{
                padding: "9px 12px",
                borderRadius: 8,
                backgroundColor: Theme.surface,
                color: "#FFF",
                border: `1px solid ${Theme.border}`
              }}
            />
            <input
              value={repairNotesInput}
              onChange={(e) => setRepairNotesInput(e.target.value)}
              placeholder="Travaux effectués & code peinture"
              style={{
                padding: "9px 12px",
                borderRadius: 8,
                backgroundColor: Theme.surface,
                color: "#FFF",
                border: `1px solid ${Theme.border}`
              }}
            />
          </div>

          <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
            <button
              onClick={() => setBeforePhotoCaptured(true)}
              style={{
                flex: 1,
                padding: "11px 14px",
                borderRadius: 10,
                border: `1.5px solid ${beforePhotoCaptured ? Theme.green : Theme.red}`,
                backgroundColor: beforePhotoCaptured ? "rgba(34,197,94,0.18)" : Theme.surface,
                color: "#FFF",
                fontWeight: 800,
                cursor: "pointer"
              }}
            >
              {beforePhotoCaptured
                ? "✓ 1. Photo AVANT Réparation Validée"
                : "📷 1. Prendre Photo AVANT Réparation (Obligatoire *)"}
            </button>
            <button
              onClick={() => setAfterPhotoCaptured(true)}
              style={{
                flex: 1,
                padding: "11px 14px",
                borderRadius: 10,
                border: `1.5px solid ${afterPhotoCaptured ? Theme.green : Theme.red}`,
                backgroundColor: afterPhotoCaptured ? "rgba(34,197,94,0.18)" : Theme.surface,
                color: "#FFF",
                fontWeight: 800,
                cursor: "pointer"
              }}
            >
              {afterPhotoCaptured
                ? "✓ 2. Photo APRÈS Réparation Validée"
                : "📷 2. Prendre Photo APRÈS Réparation (Obligatoire *)"}
            </button>
          </div>

          <button
            disabled={!beforePhotoCaptured || !afterPhotoCaptured}
            onClick={handleCertifyBodyshopPhotos}
            style={{
              padding: "12px 18px",
              borderRadius: 10,
              border: "none",
              backgroundColor:
                beforePhotoCaptured && afterPhotoCaptured ? Theme.orange : "#2B3B4B",
              color: "#FFF",
              fontWeight: 900,
              cursor: beforePhotoCaptured && afterPhotoCaptured ? "pointer" : "not-allowed"
            }}
          >
            {beforePhotoCaptured && afterPhotoCaptured
              ? `Certifier & Publier les Photos AVANT/APRÈS sur le Châssis ${activeReport.vin}`
              : "🔒 Verrouillé : Prenez d'abord les 2 Photos Obligatoires (Avant + Après)"}
          </button>
        </div>
      </div>

      {/* MODALE DE PAIEMENT SÉCURISÉ MÉCAVERIF+ (CIB SATIM, EDAHABIA, BARIDIMOB QR, CHARGILY PAY, STRIPE) */}
      {showCheckoutModal && (
        <div
          style={{
            position: "fixed",
            inset: 0,
            backgroundColor: "rgba(8,12,18,0.88)",
            zIndex: 1100,
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            padding: 16
          }}
        >
          <div
            style={{
              width: "100%",
              maxWidth: 640,
              backgroundColor: Theme.surface,
              borderRadius: 18,
              border: `1.5px solid ${Theme.orange}`,
              padding: 22,
              color: Theme.textPrimary
            }}
          >
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 14 }}>
              <div>
                <div style={{ fontWeight: 900, fontSize: 17 }}>
                  💳 PASSERELLE DE PAIEMENT SÉCURISÉ MÉCAVERIF+™
                </div>
                <div style={{ fontSize: 11.5, color: Theme.orange, fontFamily: "monospace" }}>
                  Achat Unitaire Hors Abonnement • Châssis : {activeReport.vin}
                </div>
              </div>
              <button
                onClick={() => setShowCheckoutModal(false)}
                style={{
                  backgroundColor: Theme.bg,
                  color: "#FFF",
                  border: `1px solid ${Theme.border}`,
                  borderRadius: 8,
                  padding: "6px 12px",
                  cursor: "pointer"
                }}
              >
                ✕
              </button>
            </div>

            {checkoutStep === 1 && (
              <div style={{ display: "grid", gap: 12 }}>
                <div style={{ display: "flex", gap: 10 }}>
                  <button
                    onClick={() => setSelectedTariff(250)}
                    style={{
                      flex: 1,
                      padding: 12,
                      borderRadius: 10,
                      border: `1.5px solid ${selectedTariff === 250 ? Theme.orange : Theme.border}`,
                      backgroundColor: selectedTariff === 250 ? "rgba(240,120,54,0.18)" : Theme.bg,
                      color: "#FFF",
                      fontWeight: 800,
                      cursor: "pointer"
                    }}
                  >
                    Tarif Pro : 250 DA / VIN (1,70 €)
                  </button>
                  <button
                    onClick={() => setSelectedTariff(300)}
                    style={{
                      flex: 1,
                      padding: 12,
                      borderRadius: 10,
                      border: `1.5px solid ${selectedTariff === 300 ? Theme.orange : Theme.border}`,
                      backgroundColor: selectedTariff === 300 ? "rgba(240,120,54,0.18)" : Theme.bg,
                      color: "#FFF",
                      fontWeight: 800,
                      cursor: "pointer"
                    }}
                  >
                    Tarif Particulier : 300 DA / VIN (2,00 €)
                  </button>
                </div>

                <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
                  {[
                    ["SATIM_CIB", "Carte CIB / SATIM"],
                    ["EDAHABIA", "Carte Edahabia"],
                    ["BARIDIMOB_QR", "BaridiMob QR"],
                    ["CHARGILY_PAY", "Chargily Pay DZ"],
                    ["STRIPE_INTL", "Stripe (Visa / MC)"]
                  ].map(([id, label]) => (
                    <button
                      key={id}
                      onClick={() => setSelectedGateway(id as any)}
                      style={{
                        padding: "8px 12px",
                        borderRadius: 8,
                        border: `1px solid ${selectedGateway === id ? Theme.green : Theme.border}`,
                        backgroundColor: selectedGateway === id ? "rgba(34,197,94,0.18)" : Theme.bg,
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

                <div
                  style={{
                    padding: 14,
                    borderRadius: 12,
                    background: "linear-gradient(135deg, #1E3A5F, #101820)",
                    border: `1px solid ${Theme.orange}`
                  }}
                >
                  <div style={{ fontSize: 11, color: Theme.textSecondary }}>
                    Passerelle : {selectedGateway} • Chiffrement SSL 256-Bit
                  </div>
                  <div style={{ fontFamily: "monospace", fontSize: 16, fontWeight: 900, margin: "8px 0" }}>
                    {cardNumber}
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between", fontSize: 11 }}>
                    <span>{cardHolder}</span>
                    <span>EXP {cardExpiry}</span>
                  </div>
                </div>

                <div style={{ display: "grid", gridTemplateColumns: "2fr 1fr 1fr", gap: 8 }}>
                  <input
                    value={cardNumber}
                    onChange={(e) => setCardNumber(e.target.value)}
                    placeholder="Numéro de carte"
                    style={{ padding: "9px 12px", borderRadius: 8, backgroundColor: Theme.bg, color: "#FFF", border: `1px solid ${Theme.border}` }}
                  />
                  <input
                    value={cardExpiry}
                    onChange={(e) => setCardExpiry(e.target.value)}
                    placeholder="MM/AA"
                    style={{ padding: "9px 12px", borderRadius: 8, backgroundColor: Theme.bg, color: "#FFF", border: `1px solid ${Theme.border}` }}
                  />
                  <input
                    value={cardCvv}
                    onChange={(e) => setCardCvv(e.target.value)}
                    placeholder="CVV"
                    style={{ padding: "9px 12px", borderRadius: 8, backgroundColor: Theme.bg, color: "#FFF", border: `1px solid ${Theme.border}` }}
                  />
                </div>

                <button
                  onClick={() => setCheckoutStep(2)}
                  style={{
                    padding: "12px 18px",
                    borderRadius: 10,
                    border: "none",
                    backgroundColor: Theme.orange,
                    color: "#FFF",
                    fontWeight: 900,
                    cursor: "pointer"
                  }}
                >
                  Continuer vers Vérification 3D-Secure SMS ({selectedTariff} DA)
                </button>
              </div>
            )}

            {checkoutStep === 2 && (
              <div style={{ display: "grid", gap: 12 }}>
                <div style={{ fontSize: 13 }}>
                  Un code SMS 3D-Secure à 6 chiffres a été envoyé pour confirmer le paiement unitaire de{" "}
                  <strong>{selectedTariff} DA</strong> sur le châssis <strong>{activeReport.vin}</strong>.
                </div>
                <input
                  value={otpInput}
                  onChange={(e) => setOtpInput(e.target.value)}
                  placeholder="Code OTP SMS (6 chiffres)"
                  style={{
                    padding: "10px 14px",
                    borderRadius: 8,
                    backgroundColor: Theme.bg,
                    color: Theme.green,
                    fontWeight: 900,
                    border: `1px solid ${Theme.green}`
                  }}
                />
                <button
                  onClick={() => {
                    const recId = `MV-${selectedGateway}-2026-${Math.floor(1000 + Math.random() * 8999)}`;
                    setReceiptsHistory([
                      {
                        receiptId: recId,
                        vin: activeReport.vin,
                        amountDzd: selectedTariff,
                        gateway: selectedGateway,
                        date: new Date().toLocaleString("fr-FR")
                      },
                      ...receiptsHistory
                    ]);
                    setUnlockedVins([activeReport.vin, ...unlockedVins]);
                    setCheckoutStep(3);
                  }}
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
                  Valider le Code 3D-Secure & Débloquer le Châssis
                </button>
              </div>
            )}

            {checkoutStep === 3 && (
              <div style={{ display: "grid", gap: 12 }}>
                <div
                  style={{
                    padding: 14,
                    borderRadius: 10,
                    backgroundColor: Theme.bg,
                    border: `1px solid ${Theme.green}`,
                    fontFamily: "monospace",
                    fontSize: 12
                  }}
                >
                  <div style={{ color: Theme.green, fontWeight: 900, marginBottom: 6 }}>
                    ✓ PAIEMENT APPROUVÉ & RAPPORT MÉCAVERIF+ DÉBLOQUÉ
                  </div>
                  <div>Reçu : {receiptsHistory[0]?.receiptId}</div>
                  <div>Châssis VIN : {activeReport.vin}</div>
                  <div>Montant réglé : {selectedTariff} DA ({selectedGateway})</div>
                </div>
                <button
                  onClick={() => {
                    setShowCheckoutModal(false);
                    onShowToast(`Rapport MécaVerif+ débloqué pour ${activeReport.vin}`);
                  }}
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
                  Consulter le Rapport Complet
                </button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
