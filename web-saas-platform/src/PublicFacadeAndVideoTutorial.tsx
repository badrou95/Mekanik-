import React, { useEffect, useState } from "react";
import {
  AccountRoleId,
  AccountRoleMeta
} from "./typesAndData";

const Theme = {
  bg: "#101820",
  surface: "#1C2833",
  surfaceLight: "#243442",
  orange: "#F07836",
  redOem: "#E53935",
  textPrimary: "#E8EDF2",
  textSecondary: "#94A3B8",
  green: "#22C55E",
  cyan: "#38BDF8",
  border: "#2B3B4B"
};

export type BillingCycleType = "MENSUEL" | "ANNUEL";

interface PublicFacadeProps {
  rolesPricing: Record<AccountRoleId, AccountRoleMeta>;
  mecaVerifPrices: { proPriceDzd: number; partPriceDzd: number };
  onStartRegistrationWithPlan: (
    role: AccountRoleId,
    billingCycle: BillingCycleType
  ) => void;
  onOpenLoginModal: () => void;
}

interface TutorialChapter {
  id: number;
  durationLabel: string;
  title: string;
  subtitle: string;
  screenBadge: string;
  visualTitle: string;
  visualSteps: string[];
  voiceOverText: string;
  highlightColor: string;
}

const TUTORIAL_CHAPTERS: TutorialChapter[] = [
  {
    id: 1,
    durationLabel: "00:00 – 00:45",
    title: "1. Comment ouvrir un compte & choisir son abonnement",
    subtitle: "Inscription rapide en 2 minutes (Mensuel ou Annuel -20%)",
    screenBadge: "ÉTAPE 1 • OUVERTURE DE COMPTE",
    visualTitle: "Sélection de la Formule Métier & Création du Compte",
    visualSteps: [
      "1️⃣ Cliquez sur « S'inscrire / Ouvrir un Compte » ou choisissez directement votre formule dans la grille des tarifs (Mensuel ou Annuel).",
      "2️⃣ Sélectionnez votre profil : Atelier Mécanique Pro, Magasin de Pièces, Compte Mixte (1 Principal + 4 Sous-Comptes), Carrosserie, Fournisseur B2B ou Particulier.",
      "3️⃣ Renseignez votre raison sociale, nom complet, email professionnel, téléphone mobile, wilaya et définissez votre mot de passe confidentiel.",
      "4️⃣ Choisissez votre périodicité (Par Mois ou Par An avec 2 mois offerts) et votre moyen de règlement (CIB/SATIM, Edahabia, BaridiMob, Virement RIB ou Stripe)."
    ],
    voiceOverText:
      "« Bienvenue sur MEKANIK+. Pour ouvrir votre compte, choisissez d'abord si vous souhaitez un abonnement mensuel ou annuel, sélectionnez la formule correspondant à votre métier, puis remplissez vos coordonnées professionnelles. »",
    highlightColor: "#F07836"
  },
  {
    id: 2,
    durationLabel: "00:45 – 01:30",
    title: "2. Comment accéder à votre espace en toute sécurité",
    subtitle: "Connexion par Email + Mot de passe privé + Google Authenticator 2FA",
    screenBadge: "ÉTAPE 2 • CONNEXION SÉCURISÉE & SOUS-COMPTES",
    visualTitle: "Authentification Isolée par Organisation & Gestion d'Équipe",
    visualSteps: [
      "1️⃣ Cliquez sur « Connexion » en haut à droite de la façade MEKANIK+.",
      "2️⃣ Saisissez votre adresse email, votre mot de passe personnel (masqué et chiffré) ainsi que votre code Google Authenticator à 6 chiffres.",
      "3️⃣ Votre espace s'ouvre avec une isolation stricte des données : seuls les outils de votre formule apparaissent à l'écran.",
      "4️⃣ Si vous avez choisi la Formule Mixte (1+4) ou Pro, créez les sous-comptes de vos employés (Chef d'atelier, Magasinier, Technicien OBD, Vendeur) avec permissions dédiées."
    ],
    voiceOverText:
      "« Chaque organisation dispose d'un espace strictement isolé et protégé par mot de passe et double authentification Google Authenticator. Aucun mot de passe n'est visible publiquement. »",
    highlightColor: "#22C55E"
  },
  {
    id: 3,
    durationLabel: "01:30 – 02:30",
    title: "3. Comment travailler au quotidien sur la plateforme",
    subtitle: "Recherche VIN 17 caractères, Catalogue Pièces OEM, Devis Atelier & Commandes B2B",
    screenBadge: "ÉTAPE 3 • TRAVAIL QUOTIDIEN ATELIER & MAGASIN",
    visualTitle: "Flux Complet : Châssis VIN → Réf OEM → Devis OR → Commande B2B",
    visualSteps: [
      "1️⃣ Saisissez le numéro de châssis (VIN 17 caractères) ou le code moteur dans la barre centrale pour identifier instantanément le véhicule.",
      "2️⃣ Consultez l'arborescence visuelle : Véhicule ↓ Système ↓ Pièce ↓ Référence constructeur OEM (mise en évidence en rouge/orange) ↓ Équivalences.",
      "3️⃣ Dans l'Espace Atelier : créez vos Ordres de Réparation (OR), devis clients et suivez l'avancement des travaux.",
      "4️⃣ Dans l'Espace Magasin & Atelier : commandez vos pièces en 1 clic auprès des Fournisseurs B2B et suivez chaque livraison dans « Historique des Commandes »."
    ],
    voiceOverText:
      "« Au quotidien, identifiez n'importe quel véhicule par son VIN, obtenez la référence constructeur exacte et passez vos commandes B2B tout en suivant leur statut en temps réel dans l'Historique des Commandes. »",
    highlightColor: "#38BDF8"
  },
  {
    id: 4,
    durationLabel: "02:30 – 03:15",
    title: "4. Utiliser MécaVerif+™ et l'assistant MekanikAI™ OBD-II",
    subtitle: "Vérification kilométrique anti-fraude, Photos Carrosserie Avant/Après & Diagnostic IA",
    screenBadge: "ÉTAPE 4 • MÉCAVERIF+ & DIAGNOSTIC OBD-II",
    visualTitle: "Certification Historique Véhicule & Diagnostic Technique Guidé",
    visualSteps: [
      "1️⃣ Ouvrez MécaVerif+™ pour vérifier la courbe d'évolution du kilométrage réel d'un véhicule d'occasion et détecter toute baisse suspecte de compteur.",
      "2️⃣ Consultez ou téléversez les photos obligatoires Avant / Après travaux de tôlerie et carrosserie horodatées sur le VIN.",
      "3️⃣ Dans MekanikAI™ OBD-II, saisissez un code défaut (ex: P0299, P0401, P0087) pour obtenir les causes probables, les valeurs multimètre et les couples de serrage en Nm."
    ],
    voiceOverText:
      "« Enfin, sécurisez vos achats et réparations grâce au rapport MécaVerif+ et résolvez les pannes complexes avec l'assistant de diagnostic MekanikAI OBD-II. »",
    highlightColor: "#A855F7"
  }
];

export const PublicFacadeAndVideoTutorial: React.FC<PublicFacadeProps> = ({
  rolesPricing,
  mecaVerifPrices,
  onStartRegistrationWithPlan,
  onOpenLoginModal
}) => {
  // Animation d'ouverture du Logo MEKANIK+ à l'arrivée sur la plateforme
  const [logoOpened, setLogoOpened] = useState(false);
  const [showIntroSplash, setShowIntroSplash] = useState(true);

  // Sélecteur Abonnement Mensuel vs Abonnement Annuel (-20% / 2 mois offerts)
  const [billingCycle, setBillingCycle] = useState<BillingCycleType>("MENSUEL");

  // Lecteur Vidéo Tutoriel interactif
  const [activeChapterIdx, setActiveChapterIdx] = useState(0);
  const [isPlayingVideo, setIsPlayingVideo] = useState(true);
  const [videoProgressPercent, setVideoProgressPercent] = useState(12);

  useEffect(() => {
    const t1 = setTimeout(() => {
      setLogoOpened(true);
    }, 350);
    const t2 = setTimeout(() => {
      setShowIntroSplash(false);
    }, 2200);
    return () => {
      clearTimeout(t1);
      clearTimeout(t2);
    };
  }, []);

  useEffect(() => {
    if (!isPlayingVideo) return;
    const interval = setInterval(() => {
      setVideoProgressPercent((prev) => {
        if (prev >= 100) {
          setActiveChapterIdx((c) => (c + 1) % TUTORIAL_CHAPTERS.length);
          return 0;
        }
        return prev + 2;
      });
    }, 300);
    return () => clearInterval(interval);
  }, [isPlayingVideo]);

  const currentChapter = TUTORIAL_CHAPTERS[activeChapterIdx];

  const computeAnnualPrice = (monthlyDzd: number) => {
    // Abonnement annuel = 10 mois payés sur 12 (2 mois offerts / ~17-20% d'économie)
    return Math.round(monthlyDzd * 12 * 0.8);
  };

  return (
    <div style={{ display: "grid", gap: 26 }}>
      {/* =================================================================== */}
      {/* 1. ANIMATION D'OUVERTURE DU LOGO MEKANIK+ & RÉVÉLATION DE LA FAÇADE */}
      {/* =================================================================== */}
      <section
        style={{
          position: "relative",
          padding: showIntroSplash ? "44px 28px" : "32px 28px",
          borderRadius: 20,
          background:
            "radial-gradient(circle at 50% 20%, rgba(240,120,54,0.22) 0%, #1C2833 52%, #101820 100%)",
          border: `1.5px solid ${Theme.orange}`,
          boxShadow: "0 16px 45px rgba(0,0,0,0.55)",
          transition: "all 300ms ease",
          overflow: "hidden"
        }}
      >
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            textAlign: "center",
            gap: 14
          }}
        >
          {/* Logo Mekanik+ Animé (Engrenage + Clé Mécanique + Silhouette Auto + Signe +) */}
          <div
            onClick={() => {
              setLogoOpened(false);
              setShowIntroSplash(true);
              setTimeout(() => setLogoOpened(true), 200);
              setTimeout(() => setShowIntroSplash(false), 1800);
            }}
            title="Cliquer pour rejouer l'animation d'ouverture du logo Mekanik+"
            style={{
              width: logoOpened ? 104 : 74,
              height: logoOpened ? 104 : 74,
              borderRadius: 24,
              background:
                "linear-gradient(135deg, #101418 0%, #252B31 55%, rgba(240,120,54,0.32) 100%)",
              border: `2.5px solid ${Theme.orange}`,
              boxShadow: logoOpened
                ? "0 0 38px rgba(240, 120, 54, 0.55)"
                : "0 0 12px rgba(240, 120, 54, 0.2)",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              cursor: "pointer",
              transform: logoOpened ? "scale(1) rotate(0deg)" : "scale(0.82) rotate(-18deg)",
              transition: "all 320ms cubic-bezier(0.22, 1, 0.36, 1)"
            }}
          >
            <div style={{ position: "relative", display: "flex", alignItems: "center" }}>
              <span style={{ fontSize: 36 }}>⚙️</span>
              <span
                style={{
                  fontSize: 34,
                  fontWeight: 900,
                  color: Theme.orange,
                  marginLeft: -4,
                  textShadow: "0 0 14px rgba(240,120,54,0.9)"
                }}
              >
                +
              </span>
            </div>
          </div>

          <div
            style={{
              opacity: logoOpened ? 1 : 0.2,
              transform: logoOpened ? "translateY(0)" : "translateY(12px)",
              transition: "all 300ms ease"
            }}
          >
            <div
              style={{
                display: "inline-block",
                padding: "4px 12px",
                borderRadius: 999,
                backgroundColor: "rgba(240,120,54,0.16)",
                border: `1px solid ${Theme.orange}`,
                color: Theme.orange,
                fontWeight: 900,
                fontSize: 11,
                letterSpacing: 0.8,
                marginBottom: 8
              }}
            >
              PLATEFORME AUTOMOBILE NOUVELLE GÉNÉRATION • WEB, TABLETTE & MOBILE
            </div>

            <h1
              style={{
                margin: 0,
                fontSize: 34,
                fontWeight: 900,
                letterSpacing: 0.6,
                color: "#FFFFFF"
              }}
            >
              MEKANIK<span style={{ color: Theme.orange }}>+</span>
            </h1>

            <p
              style={{
                maxWidth: 780,
                margin: "10px auto 0",
                fontSize: 15,
                color: Theme.textPrimary,
                lineHeight: 1.55
              }}
            >
              L'écosystème SaaS professionnel tout-en-un dédié aux{" "}
              <strong style={{ color: Theme.orange }}>Ateliers Mécaniques</strong>,{" "}
              <strong style={{ color: Theme.green }}>Magasins de Pièces Détachées OEM</strong>,{" "}
              <strong style={{ color: Theme.cyan }}>Ateliers de Carrosserie & Tôlerie</strong>,{" "}
              <strong style={{ color: "#FFF" }}>Fournisseurs B2B</strong> et{" "}
              <strong style={{ color: "#FFF" }}>Conducteurs Particuliers</strong>.
            </p>
          </div>

          {/* Boutons d'action principaux sur la façade */}
          <div
            style={{
              display: "flex",
              gap: 12,
              flexWrap: "wrap",
              justifyContent: "center",
              marginTop: 8
            }}
          >
            <button
              onClick={() => onStartRegistrationWithPlan("MIXTE_ATELIER_MAGASIN", billingCycle)}
              style={{
                padding: "13px 24px",
                borderRadius: 12,
                border: "none",
                background: "linear-gradient(135deg, #E53935 0%, #F07836 100%)",
                color: "#FFFFFF",
                fontWeight: 900,
                fontSize: 14,
                cursor: "pointer",
                boxShadow: "0 8px 22px rgba(240,120,54,0.35)"
              }}
            >
              🚀 Choisir mon Abonnement & Ouvrir un Compte
            </button>
            <button
              onClick={onOpenLoginModal}
              style={{
                padding: "13px 22px",
                borderRadius: 12,
                border: `1.5px solid ${Theme.green}`,
                backgroundColor: Theme.bg,
                color: Theme.green,
                fontWeight: 900,
                fontSize: 14,
                cursor: "pointer"
              }}
            >
              🔑 Se Connecter à mon Compte
            </button>
            <a
              href="#tutoriel-video-mekanik"
              style={{
                padding: "13px 20px",
                borderRadius: 12,
                border: `1px solid ${Theme.border}`,
                backgroundColor: Theme.surface,
                color: Theme.textPrimary,
                fontWeight: 800,
                fontSize: 13.5,
                textDecoration: "none",
                display: "inline-flex",
                alignItems: "center",
                gap: 6
              }}
            >
              🎬 Regarder le Tutoriel Vidéo
            </a>
          </div>
        </div>
      </section>

      {/* =================================================================== */}
      {/* 2. CE QUE FAIT LA PLATEFORME & CE QU'ELLE PROPOSE (PRÉSENTATION)    */}
      {/* =================================================================== */}
      <section
        style={{
          padding: 24,
          backgroundColor: Theme.surface,
          borderRadius: 18,
          border: `1px solid ${Theme.border}`
        }}
      >
        <div style={{ textAlign: "center", marginBottom: 20 }}>
          <div style={{ fontSize: 11.5, fontWeight: 900, color: Theme.orange, letterSpacing: 0.8 }}>
            FONCTIONNALITÉS & MODULES MÉTIERS INTÉGRÉS
          </div>
          <h2 style={{ margin: "6px 0 0", fontSize: 22, fontWeight: 900 }}>
            Ce que fait MEKANIK<span style={{ color: Theme.orange }}>+</span> et ce que la plateforme vous propose
          </h2>
        </div>

        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(260px, 1fr))",
            gap: 14
          }}
        >
          {[
            {
              icon: "🔍",
              badge: "IDENTIFICATION INSTANTANÉE",
              title: "Décodage VIN 17 Caractères & Code Moteur",
              desc: "Identifiez immédiatement la marque, le modèle, la motorisation, le code moteur exact et la boîte de vitesses à partir du numéro de châssis."
            },
            {
              icon: "⚙️",
              badge: "CATALOGUE CONSTRUCTEURS OEM",
              title: "Références OEM, Équivalences & Compatibilités",
              desc: "Arborescence visuelle complète : Véhicule ↓ Système ↓ Pièce ↓ Référence OEM en rouge/orange ↓ Équivalences équipementiers et disponibilité B2B."
            },
            {
              icon: "🛠️",
              badge: "ESPACE ATELIER PRO (DMS)",
              title: "Ordres de Réparation (OR), Devis & Clients",
              desc: "Gérez les réceptions véhicules, devis chiffrés en Dinars (DA), facturation, historique d'interventions et commandes de pièces en direct."
            },
            {
              icon: "🏪",
              badge: "ESPACE MAGASIN DE PIÈCES",
              title: "Gestion des Stocks, Comptoir & Commandes B2B",
              desc: "Suivez vos stocks multi-dépôts, tarifs comptoir et professionnels, et consultez votre page dédiée « Historique des Commandes »."
            },
            {
              icon: "🔨",
              badge: "SPÉCIAL CARROSSERIE & TÔLERIE",
              title: "Pièces de Tôlerie par VIN & Photos Avant/Après",
              desc: "Catalogue dédié aux éléments de carrosserie (boucliers, ailes, optiques) avec obligation de téléverser les photos Avant et Après réparation."
            },
            {
              icon: "🛡️",
              badge: `SERVICE EXTERNE (${mecaVerifPrices.proPriceDzd} / ${mecaVerifPrices.partPriceDzd} DA)`,
              title: "MécaVerif+™ — Audit Kilométrique & Historique",
              desc: "Courbe d'évolution du kilométrage réel pour détecter les compteurs trafiqués + consultation des photos Avant/Après de tôlerie par VIN."
            },
            {
              icon: "🧠",
              badge: "INTELLIGENCE DIAGNOSTIC",
              title: "MekanikAI™ OBD-II & Schémas Techniques",
              desc: "Analyse des codes défauts DTC, causes statistiques, procédures pas-à-pas au multimètre, couples de serrage en Nm et brochage calculateur."
            },
            {
              icon: "📦",
              badge: "RÉSEAU GROSSISTES ALGÉRIE",
              title: "Portail Fournisseur & Distributeur B2B",
              desc: "Connecte en temps réel les importateurs et grossistes avec tous les ateliers mécaniques et magasins de pièces sur la plateforme."
            }
          ].map((feat, idx) => (
            <div
              key={idx}
              style={{
                padding: 18,
                borderRadius: 14,
                backgroundColor: Theme.bg,
                border: `1px solid ${Theme.border}`,
                display: "flex",
                flexDirection: "column",
                gap: 8
              }}
            >
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                <span style={{ fontSize: 26 }}>{feat.icon}</span>
                <span
                  style={{
                    padding: "3px 8px",
                    borderRadius: 6,
                    backgroundColor: "rgba(240,120,54,0.15)",
                    color: Theme.orange,
                    fontWeight: 900,
                    fontSize: 10
                  }}
                >
                  {feat.badge}
                </span>
              </div>
              <div style={{ fontWeight: 900, fontSize: 15, color: "#FFF" }}>{feat.title}</div>
              <div style={{ fontSize: 12.5, color: Theme.textSecondary, lineHeight: 1.5 }}>
                {feat.desc}
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* =================================================================== */}
      {/* 3. VIDÉO TUTORIEL INTERACTIF : COMMENT OUVRIR UN COMPTE, ACCÉDER    */}
      {/*    ET TRAVAILLER AVEC LA PLATEFORME MEKANIK+                        */}
      {/* =================================================================== */}
      <section
        id="tutoriel-video-mekanik"
        style={{
          padding: 24,
          backgroundColor: Theme.surface,
          borderRadius: 18,
          border: `1.5px solid ${Theme.orange}`
        }}
      >
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            flexWrap: "wrap",
            gap: 12,
            marginBottom: 18
          }}
        >
          <div>
            <div style={{ fontSize: 11.5, fontWeight: 900, color: Theme.orange }}>
              🎬 VIDÉO TUTORIEL OFFICIEL MEKANIK+ (GUIDE COMPLET PAS-À-PAS)
            </div>
            <h2 style={{ margin: "4px 0 0", fontSize: 20, fontWeight: 900 }}>
              Comment ouvrir un compte, se connecter et travailler avec la plateforme
            </h2>
          </div>
          <div style={{ display: "flex", gap: 8 }}>
            <button
              onClick={() => setIsPlayingVideo(!isPlayingVideo)}
              style={{
                padding: "9px 16px",
                borderRadius: 8,
                border: "none",
                backgroundColor: isPlayingVideo ? Theme.orange : Theme.green,
                color: "#FFF",
                fontWeight: 900,
                fontSize: 12,
                cursor: "pointer"
              }}
            >
              {isPlayingVideo ? "⏸ Pause Vidéo" : "▶ Lecture Vidéo"}
            </button>
            <button
              onClick={() => {
                setActiveChapterIdx((activeChapterIdx + 1) % TUTORIAL_CHAPTERS.length);
                setVideoProgressPercent(0);
              }}
              style={{
                padding: "9px 14px",
                borderRadius: 8,
                border: `1px solid ${Theme.border}`,
                backgroundColor: Theme.bg,
                color: "#FFF",
                fontWeight: 800,
                fontSize: 12,
                cursor: "pointer"
              }}
            >
              ⏭ Chapitre Suivant
            </button>
          </div>
        </div>

        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(310px, 1fr))",
            gap: 16
          }}
        >
          {/* Écran Vidéo Simulé Haute Définition */}
          <div
            style={{
              borderRadius: 16,
              backgroundColor: Theme.bg,
              border: `2px solid ${currentChapter.highlightColor}`,
              padding: 20,
              display: "flex",
              flexDirection: "column",
              justifyContent: "space-between",
              minHeight: 320,
              boxShadow: "inset 0 0 40px rgba(0,0,0,0.65)"
            }}
          >
            <div>
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                  flexWrap: "wrap",
                  gap: 8,
                  marginBottom: 14
                }}
              >
                <span
                  style={{
                    padding: "4px 10px",
                    borderRadius: 6,
                    backgroundColor: currentChapter.highlightColor,
                    color: "#000",
                    fontWeight: 900,
                    fontSize: 11
                  }}
                >
                  ● EN LECTURE : {currentChapter.screenBadge}
                </span>
                <span
                  style={{
                    fontFamily: "monospace",
                    fontSize: 12,
                    color: Theme.textSecondary,
                    fontWeight: 800
                  }}
                >
                  ⏱ {currentChapter.durationLabel}
                </span>
              </div>

              <div
                style={{
                  fontSize: 18,
                  fontWeight: 900,
                  color: "#FFFFFF",
                  marginBottom: 12
                }}
              >
                {currentChapter.visualTitle}
              </div>

              <div style={{ display: "grid", gap: 8 }}>
                {currentChapter.visualSteps.map((stepText, idx) => (
                  <div
                    key={idx}
                    style={{
                      padding: "10px 12px",
                      borderRadius: 10,
                      backgroundColor: Theme.surface,
                      border: `1px solid ${Theme.border}`,
                      fontSize: 12.5,
                      color: Theme.textPrimary,
                      lineHeight: 1.45
                    }}
                  >
                    {stepText}
                  </div>
                ))}
              </div>
            </div>

            {/* Sous-titres / Voix-off du tutoriel + Barre de progression vidéo */}
            <div style={{ marginTop: 16 }}>
              <div
                style={{
                  padding: "10px 14px",
                  borderRadius: 10,
                  backgroundColor: "rgba(16, 24, 32, 0.9)",
                  border: `1px solid ${currentChapter.highlightColor}`,
                  fontSize: 12,
                  fontStyle: "italic",
                  color: "#FFF",
                  marginBottom: 10
                }}
              >
                🔊 {currentChapter.voiceOverText}
              </div>

              <div
                style={{
                  width: "100%",
                  height: 7,
                  borderRadius: 999,
                  backgroundColor: Theme.surface,
                  overflow: "hidden"
                }}
              >
                <div
                  style={{
                    width: `${videoProgressPercent}%`,
                    height: "100%",
                    backgroundColor: currentChapter.highlightColor,
                    transition: "width 250ms linear"
                  }}
                />
              </div>
            </div>
          </div>

          {/* Liste des 4 Chapitres Cliquables de la Vidéo */}
          <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
            <div style={{ fontSize: 12, fontWeight: 900, color: Theme.textSecondary }}>
              CHAPITRES DU TUTORIEL VIDÉO (CLIQUEZ POUR VOIR L'ÉTAPE) :
            </div>
            {TUTORIAL_CHAPTERS.map((chap, idx) => {
              const isSelected = idx === activeChapterIdx;
              return (
                <button
                  key={chap.id}
                  onClick={() => {
                    setActiveChapterIdx(idx);
                    setVideoProgressPercent(15);
                  }}
                  style={{
                    textAlign: "left",
                    padding: 14,
                    borderRadius: 12,
                    border: `1.5px solid ${
                      isSelected ? chap.highlightColor : Theme.border
                    }`,
                    backgroundColor: isSelected
                      ? "rgba(240, 120, 54, 0.14)"
                      : Theme.bg,
                    color: Theme.textPrimary,
                    cursor: "pointer"
                  }}
                >
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
                      gap: 8
                    }}
                  >
                    <span style={{ fontWeight: 900, fontSize: 13.5, color: "#FFF" }}>
                      {chap.title}
                    </span>
                    <span
                      style={{
                        fontSize: 11,
                        fontFamily: "monospace",
                        color: chap.highlightColor,
                        fontWeight: 800
                      }}
                    >
                      {chap.durationLabel}
                    </span>
                  </div>
                  <div
                    style={{
                      fontSize: 11.5,
                      color: Theme.textSecondary,
                      marginTop: 4
                    }}
                  >
                    {chap.subtitle}
                  </div>
                </button>
              );
            })}
          </div>
        </div>
      </section>

      {/* =================================================================== */}
      {/* 4. GRILLE DES PRIX : ABONNEMENT PAR MOIS (MENSUEL) OU PAR AN        */}
      {/*    (ANNUEL -20%) & CHOIX DE COMMENT S'INSCRIRE                      */}
      {/* =================================================================== */}
      <section
        style={{
          padding: 24,
          backgroundColor: Theme.surface,
          borderRadius: 18,
          border: `1.5px solid ${Theme.border}`
        }}
      >
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            textAlign: "center",
            marginBottom: 22
          }}
        >
          <div style={{ fontSize: 11.5, fontWeight: 900, color: Theme.orange }}>
            TARIFS TRANSPARENTS & FORMULES D'ABONNEMENT
          </div>
          <h2 style={{ margin: "6px 0 8px", fontSize: 24, fontWeight: 900 }}>
            Choisissez votre Abonnement Mensuel ou Annuel et comment vous inscrire
          </h2>
          <p style={{ maxWidth: 680, margin: 0, fontSize: 13, color: Theme.textSecondary }}>
            Basculez entre le paiement <strong>Par Mois</strong> (sans engagement) et le paiement{" "}
            <strong>Par An</strong> (avec <strong>20 % de réduction immédiate</strong>, soit plus de 2 mois offerts).
          </p>

          {/* Sélecteur Abonnement Par Mois / Abonnement Par An */}
          <div
            style={{
              display: "inline-flex",
              padding: 6,
              borderRadius: 12,
              backgroundColor: Theme.bg,
              border: `1.5px solid ${Theme.orange}`,
              gap: 8,
              marginTop: 16
            }}
          >
            <button
              onClick={() => setBillingCycle("MENSUEL")}
              style={{
                padding: "10px 20px",
                borderRadius: 9,
                border: "none",
                backgroundColor:
                  billingCycle === "MENSUEL" ? Theme.orange : "transparent",
                color: "#FFFFFF",
                fontWeight: 900,
                fontSize: 13,
                cursor: "pointer"
              }}
            >
              📅 Abonnement Par Mois (Mensuel)
            </button>
            <button
              onClick={() => setBillingCycle("ANNUEL")}
              style={{
                padding: "10px 20px",
                borderRadius: 9,
                border: "none",
                backgroundColor:
                  billingCycle === "ANNUEL" ? Theme.green : "transparent",
                color: "#FFFFFF",
                fontWeight: 900,
                fontSize: 13,
                cursor: "pointer"
              }}
            >
              🏆 Abonnement Par An (Annuel • -20% / 2 Mois Offerts)
            </button>
          </div>
        </div>

        {/* Cartes des 7 formules avec tarifs Mensuel & Annuel */}
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(275px, 1fr))",
            gap: 14
          }}
        >
          {(Object.keys(rolesPricing) as AccountRoleId[]).map((rk) => {
            const meta = rolesPricing[rk];
            const monthlyPrice = meta.monthlyPriceDzd;
            const annualPrice = computeAnnualPrice(monthlyPrice);
            const equivalentMonthlyInAnnual = Math.round(annualPrice / 12);

            return (
              <div
                key={rk}
                style={{
                  padding: 20,
                  borderRadius: 16,
                  backgroundColor: Theme.bg,
                  border: `1.5px solid ${meta.accentColor}`,
                  display: "flex",
                  flexDirection: "column",
                  justifyContent: "space-between",
                  gap: 14
                }}
              >
                <div>
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
                      gap: 8
                    }}
                  >
                    <span
                      style={{
                        padding: "4px 10px",
                        borderRadius: 6,
                        backgroundColor: meta.accentColor,
                        color: "#FFF",
                        fontWeight: 900,
                        fontSize: 10.5
                      }}
                    >
                      {meta.shortBadge}
                    </span>
                    {monthlyPrice > 0 && (
                      <span
                        style={{
                          padding: "3px 8px",
                          borderRadius: 6,
                          backgroundColor:
                            billingCycle === "ANNUEL"
                              ? "rgba(34,197,94,0.18)"
                              : "rgba(240,120,54,0.15)",
                          color:
                            billingCycle === "ANNUEL" ? Theme.green : Theme.orange,
                          fontWeight: 800,
                          fontSize: 10.5
                        }}
                      >
                        {billingCycle === "ANNUEL" ? "ÉCONOMIE -20%" : "SANS ENGAGEMENT"}
                      </span>
                    )}
                  </div>

                  <div style={{ fontWeight: 900, fontSize: 16, marginTop: 10, color: "#FFF" }}>
                    {meta.title}
                  </div>

                  {/* Affichage clair du prix Mensuel ET du prix Annuel */}
                  <div
                    style={{
                      marginTop: 10,
                      padding: 12,
                      borderRadius: 10,
                      backgroundColor: Theme.surface,
                      border: `1px solid ${Theme.border}`
                    }}
                  >
                    {monthlyPrice === 0 ? (
                      <div>
                        <div style={{ fontSize: 22, fontWeight: 900, color: Theme.green }}>
                          0 DA / mois (Gratuit)
                        </div>
                        <div style={{ fontSize: 11, color: Theme.textSecondary, marginTop: 2 }}>
                          MécaVerif+ à l'acte : {mecaVerifPrices.partPriceDzd} DA / VIN
                        </div>
                      </div>
                    ) : billingCycle === "MENSUEL" ? (
                      <div>
                        <div style={{ fontSize: 22, fontWeight: 900, color: Theme.orange }}>
                          {monthlyPrice.toLocaleString("fr-FR")} DA{" "}
                          <span style={{ fontSize: 12, fontWeight: 700, color: Theme.textSecondary }}>
                            / mois
                          </span>
                        </div>
                        <div style={{ fontSize: 11.5, color: Theme.green, fontWeight: 700, marginTop: 3 }}>
                          Ou en Annuel (-20%) : {annualPrice.toLocaleString("fr-FR")} DA / an
                        </div>
                      </div>
                    ) : (
                      <div>
                        <div style={{ fontSize: 22, fontWeight: 900, color: Theme.green }}>
                          {annualPrice.toLocaleString("fr-FR")} DA{" "}
                          <span style={{ fontSize: 12, fontWeight: 700, color: Theme.textSecondary }}>
                            / an
                          </span>
                        </div>
                        <div style={{ fontSize: 11.5, color: Theme.orange, fontWeight: 700, marginTop: 3 }}>
                          Revient à {equivalentMonthlyInAnnual.toLocaleString("fr-FR")} DA/mois (au lieu de{" "}
                          {monthlyPrice.toLocaleString("fr-FR")} DA)
                        </div>
                      </div>
                    )}
                  </div>

                  <p
                    style={{
                      fontSize: 12,
                      color: Theme.textSecondary,
                      lineHeight: 1.45,
                      margin: "10px 0"
                    }}
                  >
                    {meta.description}
                  </p>

                  <div style={{ display: "grid", gap: 5, fontSize: 11.5 }}>
                    <div style={{ color: Theme.orange, fontWeight: 800 }}>
                      👥 Équipe incluse :{" "}
                      {meta.maxSubAccounts === 0
                        ? "1 Compte Individuel"
                        : `1 Principal + ${meta.maxSubAccounts} Sous-Comptes (2FA)`}
                    </div>
                    {meta.exclusiveFeatures.slice(0, 4).map((f, i) => (
                      <div key={i} style={{ color: Theme.textPrimary }}>
                        ✓ {f}
                      </div>
                    ))}
                  </div>
                </div>

                <div style={{ display: "grid", gap: 6 }}>
                  <button
                    onClick={() => onStartRegistrationWithPlan(rk, billingCycle)}
                    style={{
                      width: "100%",
                      padding: "11px 14px",
                      borderRadius: 10,
                      border: "none",
                      backgroundColor: meta.accentColor,
                      color: "#FFF",
                      fontWeight: 900,
                      fontSize: 12.5,
                      cursor: "pointer"
                    }}
                  >
                    S'inscrire en {billingCycle === "ANNUEL" ? "Annuel (-20%)" : "Mensuel"} →
                  </button>
                </div>
              </div>
            );
          })}
        </div>

        {/* Guide comment choisir son mode d'inscription et de paiement */}
        <div
          style={{
            marginTop: 20,
            padding: 18,
            borderRadius: 14,
            backgroundColor: Theme.bg,
            border: `1px solid ${Theme.border}`,
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
            gap: 12
          }}
        >
          <div>
            <div style={{ fontWeight: 900, fontSize: 13, color: Theme.orange }}>
              1️⃣ INSCRIPTION EN LIGNE IMMÉDIATE
            </div>
            <div style={{ fontSize: 11.5, color: Theme.textSecondary, marginTop: 4 }}>
              Créez votre compte directement via le formulaire d'inscription en choisissant votre formule et votre mot de passe privé.
            </div>
          </div>
          <div>
            <div style={{ fontWeight: 900, fontSize: 13, color: Theme.green }}>
              2️⃣ PAIEMENT LOCAL ALGÉRIE (CIB / EDAHABIA / BARIDIMOB)
            </div>
            <div style={{ fontSize: 11.5, color: Theme.textSecondary, marginTop: 4 }}>
              Réglez votre abonnement mensuel ou annuel en Dinars (DA) via CIB SATIM, Carte Edahabia, BaridiMob ou Virement CCP/RIB.
            </div>
          </div>
          <div>
            <div style={{ fontWeight: 900, fontSize: 13, color: Theme.cyan }}>
              3️⃣ PAIEMENT INTERNATIONAL (STRIPE VISA / MASTERCARD)
            </div>
            <div style={{ fontSize: 11.5, color: Theme.textSecondary, marginTop: 4 }}>
              Compatible cartes Visa et Mastercard internationales avec activation automatique de votre organisation.
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};
