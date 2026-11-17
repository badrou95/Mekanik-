package com.example.ui.components

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.data.model.AppNotification
import com.example.data.model.NotificationCategory
import com.example.data.model.NotificationPriority
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val ONBOARDING_PREFS_NAME = "mekanik_onboarding_prefs"
private const val KEY_ONBOARDING_COMPLETED = "has_completed_quickstart_v1"
private const val REALTIME_CHANNEL_ID = "mekanik_realtime_channel"

/**
 * Gère l'état de première connexion pour le Tutoriel de Démarrage Rapide (Onboarding)
 * ainsi que la diffusion des notifications système Android en temps réel.
 */
object MekanikOnboardingAndRealtimePrefs {
    fun shouldShowOnboardingOnFirstLaunch(context: Context): Boolean {
        val prefs = context.getSharedPreferences(ONBOARDING_PREFS_NAME, Context.MODE_PRIVATE)
        return !prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun markOnboardingCompleted(context: Context) {
        val prefs = context.getSharedPreferences(ONBOARDING_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
    }

    fun resetOnboardingForReplay(context: Context) {
        val prefs = context.getSharedPreferences(ONBOARDING_PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, false).apply()
    }

    fun postSystemNotificationIfPermitted(
        context: Context,
        notification: AppNotification
    ) {
        try {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    REALTIME_CHANNEL_ID,
                    "Mekanik+ Alertes Temps Réel",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications en temps réel : OBD-II, Atelier, Stock Pièces et Réseau Pro"
                }
                notificationManager.createNotificationChannel(channel)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (!granted) return
            }

            val builder = NotificationCompat.Builder(context, REALTIME_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(notification.title)
                .setContentText(notification.message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(notification.message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            notificationManager.notify(notification.id.hashCode(), builder.build())
        } catch (_: Exception) {
            // Ignore in preview/headless environments
        }
    }
}

/**
 * Modèle d'une étape du Tutoriel Interactif de Démarrage Rapide (Onboarding)
 */
data class OnboardingSpotlightStep(
    val stepNumber: Int,
    val badgeLabel: String,
    val targetZoneLabel: String,
    val spotlightAlignment: Alignment,
    val emoji: String,
    val icon: ImageVector,
    val title: String,
    val description: String,
    val keyHighlights: List<String>,
    val accentColor: Color,
    val tryNowButtonLabel: String,
    val actionKey: String
)

val mekanikQuickStartSteps: List<OnboardingSpotlightStep> = listOf(
    OnboardingSpotlightStep(
        stepNumber = 1,
        badgeLabel = "ÉTAPE 1 / 7 • PREMIÈRE CONNEXION",
        targetZoneLabel = "En-tête & Sélecteur de Profil / Mode Normal-Sombre",
        spotlightAlignment = Alignment.TopCenter,
        emoji = "🚀",
        icon = Icons.Default.DashboardCustomize,
        title = "Bienvenue sur Mekanik+ Nouvelle Génération",
        description = "Sélectionnez votre profil métier en 1 clic (Atelier de réparation, Vendeur de pièces, Conducteur/Entreprise ou Service de location) et basculez entre le Mode Normal (écriture noire) et le Mode Sombre (écriture blanche).",
        keyHighlights = listOf(
            "4 interfaces adaptées : Atelier, Vendeur, Entreprise & Location",
            "Bascule instantanée ☀️ Mode Normal (Noir) / 🌙 Mode Sombre (Blanc)",
            "Synchronisation automatique avec l'affichage de votre téléphone"
        ),
        accentColor = MechanicRed,
        tryNowButtonLabel = "Tester le changement de mode ☀️/🌙",
        actionKey = "TOGGLE_THEME"
    ),
    OnboardingSpotlightStep(
        stepNumber = 2,
        badgeLabel = "ÉTAPE 2 / 7 • NAVIGATION 3 BARRES",
        targetZoneLabel = "Bouton Menu ☰ en haut à gauche & Recherche Store",
        spotlightAlignment = Alignment.TopStart,
        emoji = "☰",
        icon = Icons.Default.MenuOpen,
        title = "Menu Simplifié à 3 Barres & Espace Store",
        description = "Ouvrez le menu ☰ en haut à gauche pour accéder directement aux 4 espaces principaux sans jamais vous perdre : Accueil, Recherche par ville, Recherche dans store et Recherche pièce.",
        keyHighlights = listOf(
            "📍 Recherche par ville (Wilaya) pour localiser ateliers & vendeurs",
            "🏪 Recherche dans store pour explorer les boutiques partenaires",
            "⚙️ Recherche pièce par référence OEM, VIN ou véhicule"
        ),
        accentColor = ElectricBlue,
        tryNowButtonLabel = "Ouvrir la Recherche Store & Ville",
        actionKey = "OPEN_STORE_SEARCH"
    ),
    OnboardingSpotlightStep(
        stepNumber = 3,
        badgeLabel = "ÉTAPE 3 / 7 • MAINS-LIBRES ATELIER",
        targetZoneLabel = "Bouton 🎙️ Vocal & Bannière Recherche Vocale",
        spotlightAlignment = Alignment.Center,
        emoji = "🎙️",
        icon = Icons.Default.Mic,
        title = "Recherche Vocale Mains-Libres (Sans toucher l'écran)",
        description = "Les mains prises sous le capot ? Dictez simplement une référence OEM ou demandez un schéma technique à la voix : Mekanik+ trouve la pièce, affiche les couples de serrage et lit la réponse à haute voix (TTS).",
        keyHighlights = listOf(
            "Reconnaissance vocale atelier (références OEM, schémas, couples Nm)",
            "Synthèse vocale TTS automatique pour écouter sans regarder l'écran",
            "Propulsé par Firebase AI Logic (Gemini 3.8 Flash)"
        ),
        accentColor = AiCyan,
        tryNowButtonLabel = "Essayer la Recherche Vocale 🎙️",
        actionKey = "OPEN_VOICE_SEARCH"
    ),
    OnboardingSpotlightStep(
        stepNumber = 4,
        badgeLabel = "ÉTAPE 4 / 7 • DIAGNOSTIC OBD-II IA",
        targetZoneLabel = "Carte 🧠 MekanikAI — Analyseur OBD-II (DTC)",
        spotlightAlignment = Alignment.Center,
        emoji = "🧠",
        icon = Icons.Default.Psychology,
        title = "MekanikAI — Analyseur de Codes Défauts OBD-II",
        description = "Saisissez n'importe quel code défaut DTC (P0299, P0401, P0300, C0035, B0001, U0100…) : MekanikAI décode la structure ISO/SAE, explique la panne clairement, liste les causes probables, les étapes de test multimètre et les pièces OEM à remplacer.",
        keyHighlights = listOf(
            "Décodeur visuel 5 caractères (P Moteur, C Châssis, B Corps, U Réseau CAN)",
            "Causes racines avec pourcentages de fréquence en atelier",
            "Checklist de contrôle interactive + Pièces OEM liées avec ajout au Devis"
        ),
        accentColor = TechCyan,
        tryNowButtonLabel = "Tester l'Analyseur OBD-II (P0299)",
        actionKey = "OPEN_OBD_AI"
    ),
    OnboardingSpotlightStep(
        stepNumber = 5,
        badgeLabel = "ÉTAPE 5 / 7 • ATELIER & VENDEUR IA",
        targetZoneLabel = "Grille Métier : Ordre de Réparation & Photo Pièce IA",
        spotlightAlignment = Alignment.Center,
        emoji = "🧾",
        icon = Icons.Default.Handyman,
        title = "Ordre de Réparation Vocal & Ajout Pièce par Photo IA",
        description = "Pour les ateliers : créez un Ordre de Réparation par photo (immatriculation, carte grise, jauge, voyants tableau de bord) et commande vocale traduite en 5 langues (FR/AR/EN/ES/ZH). Pour les vendeurs : prenez une pièce en photo, l'IA génère 4 vues HD et les voitures compatibles.",
        keyHighlights = listOf(
            "OR complet avec checklist pneus/ampoules et signature électronique",
            "Traduction vocale automatique des pannes (Français, Arabe, Anglais, Espagnol, Chinois)",
            "Studio Vendeur IA : 1 photo → Réf OEM + 4 photos HD + compatibilités"
        ),
        accentColor = MechanicOrange,
        tryNowButtonLabel = "Ouvrir le Studio Vendeur Photo IA 📸",
        actionKey = "OPEN_VENDOR_PHOTO_AI"
    ),
    OnboardingSpotlightStep(
        stepNumber = 6,
        badgeLabel = "ÉTAPE 6 / 7 • COMMUNAUTÉ & B2B",
        targetZoneLabel = "Cartes 💬 Forum Auto & 🤝 Réseau Social Pro",
        spotlightAlignment = Alignment.BottomCenter,
        emoji = "🤝",
        icon = Icons.Default.Groups,
        title = "Forum Auto par Groupe & Réseau Social Pro (Chat Écrit/Vocal)",
        description = "Posez vos questions et trouvez des solutions validées dans le Forum séparé par groupe automobile (VW, Stellantis, Ford, Renault…). Entre vendeurs et ateliers, connectez-vous sur le Réseau Pro pour voir les nouvelles pièces postées et discuter en privé par message écrit ou vocal.",
        keyHighlights = listOf(
            "Forum Auto structuré par groupe constructeur + recherche par voiture",
            "Fil d'actualité B2B des nouvelles pièces disponibles avec commentaires",
            "Messagerie privée Pro intégrée : messages écrits + notes vocales"
        ),
        accentColor = ElectricBlue,
        tryNowButtonLabel = "Explorer le Réseau Social Pro 🤝",
        actionKey = "OPEN_PRO_SOCIAL"
    ),
    OnboardingSpotlightStep(
        stepNumber = 7,
        badgeLabel = "ÉTAPE 7 / 7 • NOTIFS LIVE & ARCHIVE ZIP",
        targetZoneLabel = "Cloche 🔔 Temps Réel & Archive Mensuelle .ZIP",
        spotlightAlignment = Alignment.TopEnd,
        emoji = "🔔",
        icon = Icons.Default.NotificationsActive,
        title = "Notifications en Temps Réel & Archive Mensuelle .ZIP",
        description = "Suivez en direct toutes les alertes de votre activité (nouvelles pièces en stock, codes OBD-II critiques, validation d'OR, messages vocaux Pro) grâce au flux Temps Réel, et compressez chaque fin de mois vos ventes, bons et factures dans un fichier .ZIP.",
        keyHighlights = listOf(
            "Bannière Live Temps Réel + Centre de Notifications avec filtre et simulateur",
            "Notifications système Android natives en direct",
            "Export .ZIP mensuel avec choix des dates de début et de fin"
        ),
        accentColor = StatusGreen,
        tryNowButtonLabel = "Envoyer une Notification Temps Réel 🔔",
        actionKey = "TRIGGER_LIVE_NOTIF"
    )
)

/**
 * Superposition semi-transparente interactive (Onboarding Overlay) guidant les nouveaux utilisateurs
 * à travers les fonctionnalités principales de Mekanik+ lors de leur première connexion.
 */
@Composable
fun InteractiveQuickStartOnboardingOverlay(
    currentStepIndex: Int,
    onStepChange: (Int) -> Unit,
    onExecuteStepAction: (String) -> Unit,
    onFinishOnboarding: () -> Unit
) {
    val steps = mekanikQuickStartSteps
    val safeIndex = currentStepIndex.coerceIn(0, steps.lastIndex)
    val step = steps[safeIndex]

    val infiniteTransition = rememberInfiniteTransition(label = "onboarding_spotlight_pulse")
    val pulseRadiusMultiplier by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_radius"
    )
    val dashPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dash_phase"
    )

    // Semi-transparent full-screen overlay with animated spotlight target frame
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xD9080C12))
            .clickable(enabled = true, onClick = {}) // intercept background clicks cleanly
            .testTag("interactive_onboarding_overlay")
    ) {
        // Spotlight decorative target Canvas indicating which part of the UI is being explained
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val targetCenter = when (step.spotlightAlignment) {
                Alignment.TopStart -> Offset(w * 0.22f, h * 0.08f)
                Alignment.TopCenter -> Offset(w * 0.50f, h * 0.14f)
                Alignment.TopEnd -> Offset(w * 0.82f, h * 0.08f)
                Alignment.BottomCenter -> Offset(w * 0.50f, h * 0.82f)
                else -> Offset(w * 0.50f, h * 0.44f)
            }

            // Radial spotlight halo on the semi-transparent backdrop
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        step.accentColor.copy(alpha = 0.32f),
                        step.accentColor.copy(alpha = 0.10f),
                        Color.Transparent
                    ),
                    center = targetCenter,
                    radius = 240f * pulseRadiusMultiplier
                ),
                center = targetCenter,
                radius = 240f * pulseRadiusMultiplier
            )

            // Animated dashed spotlight frame around the highlighted zone
            val boxW = (w * 0.86f).coerceAtMost(720f)
            val boxH = 86f
            val topLeft = Offset(
                x = (targetCenter.x - boxW / 2f).coerceIn(16f, (w - boxW - 16f).coerceAtLeast(16f)),
                y = (targetCenter.y - boxH / 2f).coerceIn(16f, (h - boxH - 16f).coerceAtLeast(16f))
            )
            drawRoundRect(
                color = step.accentColor.copy(alpha = 0.85f),
                topLeft = topLeft,
                size = Size(boxW, boxH),
                cornerRadius = CornerRadius(28f, 28f),
                style = Stroke(
                    width = 3.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f), dashPhase)
                )
            )
        }

        // Top Bar: Spotlight Target Indicator + Skip Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = GraphiteMetal,
                border = BorderStroke(1.5.dp, step.accentColor)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = null,
                        tint = step.accentColor,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Cible : ${step.targetZoneLabel}",
                        color = OffWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = GraphiteDark,
                border = BorderStroke(1.dp, SteelBorder),
                onClick = onFinishOnboarding,
                modifier = Modifier.testTag("onboarding_skip_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Passer le tutoriel",
                        color = AluminumGrey,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer le tutoriel",
                        tint = OffWhite,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Central Interactive Guide Card
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .padding(horizontal = 18.dp, vertical = 56.dp)
                .shadow(24.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = GraphiteMetal,
            border = BorderStroke(2.dp, step.accentColor)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Step Badge & Step Dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = step.accentColor.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, step.accentColor)
                    ) {
                        Text(
                            text = step.badgeLabel,
                            color = step.accentColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        steps.forEachIndexed { idx, s ->
                            Box(
                                modifier = Modifier
                                    .height(7.dp)
                                    .width(if (idx == safeIndex) 22.dp else 7.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (idx == safeIndex) s.accentColor
                                        else if (idx < safeIndex) StatusGreen
                                        else SteelBorder
                                    )
                                    .clickable { onStepChange(idx) }
                            )
                        }
                    }
                }

                // Step Icon + Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(step.accentColor.copy(alpha = 0.18f))
                            .border(1.5.dp, step.accentColor, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = step.emoji, fontSize = 26.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = step.title,
                            color = OffWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            lineHeight = 21.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Guide de démarrage rapide interactif Mekanik+",
                            color = step.accentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Main explanation
                Text(
                    text = step.description,
                    color = OffWhite.copy(alpha = 0.92f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                // Key highlights box
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = GraphiteDark,
                    border = BorderStroke(1.dp, SteelBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        step.keyHighlights.forEach { bullet ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = step.accentColor,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 1.dp)
                                )
                                Text(
                                    text = bullet,
                                    color = OffWhite,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                // Interactive "Try It Now" button for the current step
                OutlinedButton(
                    onClick = { onExecuteStepAction(step.actionKey) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_try_feature_btn"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, step.accentColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = step.accentColor.copy(alpha = 0.12f),
                        contentColor = OffWhite
                    )
                ) {
                    Icon(
                        imageVector = step.icon,
                        contentDescription = null,
                        tint = step.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = step.tryNowButtonLabel,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }

                HorizontalDivider(color = SteelBorder)

                // Bottom Navigation Controls (Précédent / Suivant ou Terminer)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (safeIndex > 0) {
                        OutlinedButton(
                            onClick = { onStepChange(safeIndex - 1) },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, SteelBorder),
                            modifier = Modifier.testTag("onboarding_prev_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = OffWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Précédent",
                                color = OffWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Text(
                            text = "Astuce : relancez ce guide via 🎓 Aide",
                            color = AluminumGrey,
                            fontSize = 10.5.sp
                        )
                    }

                    Button(
                        onClick = {
                            if (safeIndex < steps.lastIndex) {
                                onStepChange(safeIndex + 1)
                            } else {
                                onFinishOnboarding()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (safeIndex < steps.lastIndex) step.accentColor else StatusGreen,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.testTag("onboarding_next_or_finish_btn")
                    ) {
                        Text(
                            text = if (safeIndex < steps.lastIndex) "Suivant (${safeIndex + 2}/${steps.size})"
                            else "Commencer sur Mekanik+ ✓",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (safeIndex < steps.lastIndex) Icons.Default.ArrowForward else Icons.Default.DoneAll,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Bannière de Notification en Temps Réel (Live Real-Time Banner) qui s'anime en haut de l'écran
 * dès qu'un nouvel événement (OBD-II, Pièce Store, Ordre de Réparation, Réseau Pro) survient.
 */
@Composable
fun LiveRealTimeNotificationBanner(
    notification: AppNotification?,
    isRealtimeStreamActive: Boolean,
    onOpenNotificationCenter: () -> Unit,
    onActionClick: (AppNotification) -> Unit,
    onDismissBanner: () -> Unit
) {
    AnimatedVisibility(
        visible = notification != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(tween(220)),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(tween(180))
    ) {
        if (notification != null) {
            val catColor = Color(notification.category.colorHex)
            val infiniteTransition = rememberInfiniteTransition(label = "live_notif_pulse")
            val dotAlpha by infiniteTransition.animateFloat(
                initialValue = 0.35f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(650),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "live_dot_alpha"
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .shadow(16.dp, RoundedCornerShape(16.dp))
                    .testTag("live_realtime_notification_banner"),
                shape = RoundedCornerShape(16.dp),
                color = GraphiteDark,
                border = BorderStroke(1.8.dp, catColor)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(StatusGreen.copy(alpha = dotAlpha))
                            )
                            Text(
                                text = "NOTIF TEMPS RÉEL • ${notification.category.label.uppercase()}",
                                color = catColor,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                                letterSpacing = 0.6.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = catColor.copy(alpha = 0.16f)
                            ) {
                                Text(
                                    text = notification.timestamp,
                                    color = OffWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismissBanner,
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fermer l'alerte temps réel",
                                tint = AluminumGrey,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = notification.title,
                        color = OffWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = notification.message,
                        color = AluminumGrey,
                        fontSize = 11.5.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                onDismissBanner()
                                onOpenNotificationCenter()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Centre de Notifs",
                                color = AluminumGrey,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                onActionClick(notification)
                                onDismissBanner()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = catColor,
                                contentColor = Color(0xFF090D16)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Ouvrir maintenant →",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Barre de contrôle du Flux de Notifications en Temps Réel intégrée dans le Centre de Notifications
 * et sur le tableau de bord d'accueil.
 */
@Composable
fun RealTimeNotificationStreamControlCard(
    isStreamEnabled: Boolean,
    unreadCount: Int,
    onToggleStream: (Boolean) -> Unit,
    onTriggerInstantRealtimeEvent: (NotificationCategory) -> Unit,
    onOpenNotificationCenter: () -> Unit,
    onStartOnboardingGuide: () -> Unit
) {
    val context = LocalContext.current
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasNotificationPermission = granted
    }

    val infiniteTransition = rememberInfiniteTransition(label = "stream_status_pulse")
    val livePulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(750),
            repeatMode = RepeatMode.Reverse
        ),
        label = "live_pulse"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_realtime_notif_and_onboarding_card"),
        shape = RoundedCornerShape(18.dp),
        color = GraphiteMetal,
        border = BorderStroke(1.5.dp, if (isStreamEnabled) StatusGreen else SteelBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isStreamEnabled) StatusGreen.copy(alpha = 0.18f)
                                else GraphiteDark
                            )
                            .border(
                                1.dp,
                                if (isStreamEnabled) StatusGreen else SteelBorder,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Notifications en Temps Réel",
                            tint = if (isStreamEnabled) StatusGreen else AluminumGrey,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isStreamEnabled) StatusGreen.copy(alpha = livePulse)
                                        else AluminumGrey
                                    )
                            )
                            Text(
                                text = "NOTIFICATIONS TEMPS RÉEL & GUIDE",
                                color = OffWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                            if (unreadCount > 0) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MechanicRed
                                ) {
                                    Text(
                                        text = "$unreadCount",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (isStreamEnabled) "Flux Live actif : alertes OBD-II, stock pièces, OR atelier & messages Pro en direct"
                            else "Flux Live en pause — activez-le pour recevoir les événements en direct",
                            color = AluminumGrey,
                            fontSize = 10.5.sp
                        )
                    }
                }

                Switch(
                    checked = isStreamEnabled,
                    onCheckedChange = onToggleStream,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = StatusGreen
                    ),
                    modifier = Modifier.testTag("switch_realtime_notifications")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Simulation Buttons for Real-Time Events + Replay Interactive Onboarding Tutorial
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Button to launch the Interactive Quick-Start Onboarding Tutorial Overlay
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MechanicRed,
                    onClick = onStartOnboardingGuide,
                    modifier = Modifier.testTag("btn_launch_interactive_onboarding")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "🎓 Tutoriel Démarrage Rapide",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.5.sp
                        )
                    }
                }

                // Instant Real-Time OBD Alert Trigger
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteDark,
                    border = BorderStroke(1.dp, TechCyan),
                    onClick = { onTriggerInstantRealtimeEvent(NotificationCategory.MEKANIK_AI_ALERTE) },
                    modifier = Modifier.testTag("btn_trigger_live_obd_notif")
                ) {
                    Text(
                        text = "⚡ Notif Live OBD-II",
                        color = OffWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                    )
                }

                // Instant Real-Time Stock Part Alert Trigger
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteDark,
                    border = BorderStroke(1.dp, ElectricBlue),
                    onClick = { onTriggerInstantRealtimeEvent(NotificationCategory.PIECE_DISPONIBLE) },
                    modifier = Modifier.testTag("btn_trigger_live_part_notif")
                ) {
                    Text(
                        text = "📦 Notif Live Pièce Store",
                        color = OffWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                    )
                }

                // Instant Real-Time Workshop OR Alert Trigger
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteDark,
                    border = BorderStroke(1.dp, StatusGreen),
                    onClick = { onTriggerInstantRealtimeEvent(NotificationCategory.RDV_ATELIER) },
                    modifier = Modifier.testTag("btn_trigger_live_atelier_notif")
                ) {
                    Text(
                        text = "🛠️ Notif Live OR Atelier",
                        color = OffWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                    )
                }

                // Open Full Notification Center
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteDark,
                    border = BorderStroke(1.dp, MechanicOrange),
                    onClick = onOpenNotificationCenter
                ) {
                    Text(
                        text = "🔔 Centre ($unreadCount)",
                        color = MechanicOrange,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.5.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                    )
                }

                if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = GraphiteDark,
                        border = BorderStroke(1.dp, StatusGreen),
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    ) {
                        Text(
                            text = "📲 Activer Notifs Android",
                            color = StatusGreen,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.5.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Génère une notification temps réel réaliste et horodatée à la seconde près
 * selon la catégorie demandée ou le cycle automatique en direct.
 */
fun buildRealtimeAppNotification(
    category: NotificationCategory,
    sequenceCounter: Int
): AppNotification {
    val timeFormatted = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    return when (category) {
        NotificationCategory.MEKANIK_AI_ALERTE -> {
            val presets = listOf(
                "🧠 Alerte OBD-II Live : Code P0299 (Sous-pression Turbo)" to
                    "Détecté en direct sur VW Tiguan 2.0 TDI (Pont 2) • Pression mesurée 1.15 bar vs consigne 1.85 bar. Cliquez pour ouvrir MekanikAI.",
                "🧠 Alerte OBD-II Live : Code P0401 (Débit EGR Insuffisant)" to
                    "Détecté sur Peugeot 308 1.6 BlueHDi • Soupape EGR encrassée. Voir les étapes de contrôle multimètre et la réf OEM 1618.NR.",
                "🧠 Alerte OBD-II Live : Code U0100 (Perte Comm. CAN ECU)" to
                    "Coupure bus CAN High/Low détectée sur Renault Clio 4 • Vérifier résistance de terminaison 60 Ohms."
            )
            val pick = presets[sequenceCounter % presets.size]
            AppNotification(
                id = "rt_obd_${System.currentTimeMillis()}",
                title = pick.first,
                message = pick.second,
                category = NotificationCategory.MEKANIK_AI_ALERTE,
                timestamp = "En direct • $timeFormatted",
                isRead = false,
                actionTag = "OPEN_OBD_AI",
                priority = NotificationPriority.URGENT
            )
        }
        NotificationCategory.PIECE_DISPONIBLE -> {
            val presets = listOf(
                "📦 Stock Temps Réel : Kit Distribution 03L198119F disponible" to
                    "SARL Auto Pièces Bab Ezzouar vient d'ajouter 6 kits Continental en stock (24 500 DA) via Photo IA.",
                "📦 Arrivage Store : Turbo BorgWarner 03L253016T" to
                    "Disponible immédiatement chez Store Pro Pièces Oran • Compatible VW Golf 7 / Tiguan / Audi A3 2.0 TDI.",
                "📦 Réseau Pro B2B : Capteur ABS Bosch 1K0927807" to
                    "Posté à l'instant sur le Réseau Social Pro par Comptoir Pièces Constantine avec 4 photos certifiées IA."
            )
            val pick = presets[sequenceCounter % presets.size]
            AppNotification(
                id = "rt_part_${System.currentTimeMillis()}",
                title = pick.first,
                message = pick.second,
                category = NotificationCategory.PIECE_DISPONIBLE,
                timestamp = "En direct • $timeFormatted",
                isRead = false,
                actionTag = "OPEN_STORE_SEARCH",
                priority = NotificationPriority.HIGH
            )
        }
        NotificationCategory.RDV_ATELIER -> {
            val presets = listOf(
                "🧾 Ordre de Réparation #OR-2026-114 signé en direct" to
                    "Le client Karim Meziane a signé électroniquement l'OR (Checklist 100% + Lecture vocale validée).",
                "🛠️ Compagnon Atelier : Pont N°1 vient de terminer la vidange" to
                    "Tâche clôturée par Yacine B. sur Dacia Duster 1.5 dCi • Véhicule prêt pour contrôle final.",
                "📅 Nouveau RDV Atelier reçu en direct (Demain 09h00)" to
                    "Diagnostic voyant moteur + révision complète sur Seat Leon 2.0 TDI (Immat: 09412-122-16)."
            )
            val pick = presets[sequenceCounter % presets.size]
            AppNotification(
                id = "rt_atelier_${System.currentTimeMillis()}",
                title = pick.first,
                message = pick.second,
                category = NotificationCategory.RDV_ATELIER,
                timestamp = "En direct • $timeFormatted",
                isRead = false,
                actionTag = "OPEN_ATELIER",
                priority = NotificationPriority.HIGH
            )
        }
        NotificationCategory.ENTRETIEN_RAPPEL -> {
            AppNotification(
                id = "rt_maint_${System.currentTimeMillis()}",
                title = "🌡️ Télémétrie Temps Réel : Alerte Température LDR (103°C)",
                message = "Flotte Entreprise #FL-08 (Peugeot Partner) : contrôler le niveau du vase d'expansion G12evo.",
                category = NotificationCategory.ENTRETIEN_RAPPEL,
                timestamp = "En direct • $timeFormatted",
                isRead = false,
                actionTag = "OPEN_ATELIER",
                priority = NotificationPriority.HIGH
            )
        }
        NotificationCategory.OFFRE_VENDEUR -> {
            AppNotification(
                id = "rt_pro_${System.currentTimeMillis()}",
                title = "💬 Réseau Social Pro : Nouveau message vocal reçu",
                message = "Atelier MécaTech Blida vous a envoyé une note vocale (0:18) concernant la disponibilité d'une vanne EGR.",
                category = NotificationCategory.OFFRE_VENDEUR,
                timestamp = "En direct • $timeFormatted",
                isRead = false,
                actionTag = "OPEN_PRO_SOCIAL",
                priority = NotificationPriority.HIGH
            )
        }
    }
}
