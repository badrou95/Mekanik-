package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.ui.theme.*
import kotlinx.coroutines.delay

enum class AutoManufacturerGroup(
    val id: String,
    val shortTitle: String,
    val fullBrandsLabel: String,
    val badgeColorHex: Long,
    val emoji: String
) {
    ALL("all", "Tous les Groupes", "Toutes marques automobiles", 0xFF38BDF8, "🌍"),
    VAG("vag", "Groupe Volkswagen (VAG)", "VW • Audi • Seat • Skoda • Cupra • Porsche", 0xFF2563EB, "🇩🇪"),
    STELLANTIS("stellantis", "Groupe Stellantis", "Peugeot • Citroën • DS • Opel • Fiat • Jeep", 0xFFFF7A00, "🦁"),
    FORD("ford", "Groupe Ford", "Ford • Ford Pro • Transit • Ranger • Focus • Fiesta", 0xFF0284C7, "🟦"),
    RENAULT_NISSAN("renault", "Groupe Renault-Dacia-Nissan", "Renault • Dacia • Nissan • Alpine", 0xFFEAB308, "🔶"),
    GERMAN_PREMIUM("german", "Mercedes-Benz & BMW", "Mercedes-Benz • BMW • Mini • Smart", 0xFF10B981, "⭐"),
    ASIAN_SINO("asian", "Asie & Sino-Auto", "Toyota • Hyundai • Kia • Chery • Geely", 0xFFE53935, "⚡")
}

data class ForumAnswerItem(
    val id: String,
    val authorName: String,
    val authorRoleBadge: String,
    val timestamp: String,
    val content: String,
    val isAcceptedSolution: Boolean = false,
    val upvotes: Int = 0
)

data class ForumTopicItem(
    val id: String,
    val group: AutoManufacturerGroup,
    val carModelTag: String,
    val engineTag: String,
    val title: String,
    val description: String,
    val authorName: String,
    val authorRoleBadge: String,
    val timestamp: String,
    val isSolved: Boolean,
    val viewsCount: Int,
    val answers: List<ForumAnswerItem>
)

val globalForumTopics = mutableStateListOf(
    ForumTopicItem(
        id = "FORUM-VAG-01",
        group = AutoManufacturerGroup.VAG,
        carModelTag = "Volkswagen Golf 7",
        engineTag = "2.0 TDI 150ch CRLB",
        title = "Voyant préchauffage clignotant + Code P0087 pression rampe trop basse à froid",
        description = "Bonjour à tous, sur une Golf 7 2.0 TDI 150 (moteur CRLB), le véhicule démarre difficilement le matin puis passe en mode dégradé avec le défaut P0087. Filtre à gasoil neuf. Quelqu'un a déjà eu le cas exact ?",
        authorName = "Atelier MécaTech Bab Ezzouar",
        authorRoleBadge = "🛠️ Atelier Pro",
        timestamp = "Il y a 25 min",
        isSolved = true,
        viewsCount = 184,
        answers = listOf(
            ForumAnswerItem(
                id = "ANS-1",
                authorName = "Karim Diag VAG Oran",
                authorRoleBadge = "🧠 Expert VAG",
                timestamp = "Il y a 18 min",
                content = "Vérifie d'abord le régulateur de pression N276 sur la rampe et surtout démonte le régulateur de débit sur la pompe HP CP4 (2 vis Torx) pour contrôler s'il n'y a pas de limaille métallique dans le tamis. Si pas de limaille, remplace la vanne de régulation Bosch 0 281 006 074.",
                isAcceptedSolution = true,
                upvotes = 19
            ),
            ForumAnswerItem(
                id = "ANS-2",
                authorName = "AutoPièces Pro Alger",
                authorRoleBadge = "🏪 Vendeur Pièces",
                timestamp = "Il y a 10 min",
                content = "Nous avons la vanne de régulation Bosch d'origine et la pompe CP4 04L130755E en stock si besoin.",
                isAcceptedSolution = false,
                upvotes = 6
            )
        )
    ),
    ForumTopicItem(
        id = "FORUM-STEL-01",
        group = AutoManufacturerGroup.STELLANTIS,
        carModelTag = "Peugeot 208 / 308",
        engineTag = "1.5 BlueHDi 100/130 DV5",
        title = "Bruit de chaîne d'arbre à cames au démarrage + Défaut P20E8 AdBlue",
        description = "Sur une Peugeot 308 1.5 BlueHDi 130ch de 2019, léger claquement côté haut moteur à froid et message anti-pollution 1100 km. Faut-il passer directement au kit chaîne 8mm ?",
        authorName = "Garage El Amel Sétif",
        authorRoleBadge = "🛠️ Atelier Pro",
        timestamp = "Il y a 1h",
        isSolved = true,
        viewsCount = 240,
        answers = listOf(
            ForumAnswerItem(
                id = "ANS-3",
                authorName = "Sofiane Spécialiste PSA",
                authorRoleBadge = "🛠️ Chef d'Atelier",
                timestamp = "Il y a 42 min",
                content = "Oui ! Si le carter supérieur a encore la chaîne 7mm (réf 9822827180), il faut impérativement monter le kit de modification 8mm complet avec les 2 arbres à cames et le carter modifié (réf 1638159880). Pour le P20E8, nettoie l'injecteur d'urée à l'eau chaude et fais le test actionneur sous DiagBox avant de remplacer le réservoir.",
                isAcceptedSolution = true,
                upvotes = 27
            )
        )
    ),
    ForumTopicItem(
        id = "FORUM-FORD-01",
        group = AutoManufacturerGroup.FORD,
        carModelTag = "Ford Focus 4 / Transit Custom",
        engineTag = "2.0 EcoBlue 130/170ch",
        title = "Courroie de distribution humide sur Ford 2.0 EcoBlue : intervalle réel et crépine ?",
        description = "Bonjour la communauté Ford, à quel kilométrage remplacez-vous la courroie humide sur les Transit Custom et Focus 2.0 EcoBlue pour éviter le colmatage de la crépine de pompe à huile ?",
        authorName = "Flotte Express Entreprise",
        authorRoleBadge = "🏢 Entreprise",
        timestamp = "Il y a 2h",
        isSolved = true,
        viewsCount = 156,
        answers = listOf(
            ForumAnswerItem(
                id = "ANS-4",
                authorName = "Atelier Ford Pro Blida",
                authorRoleBadge = "🛠️ Atelier Agréé",
                timestamp = "Il y a 1h 15",
                content = "En usage intensif ou urbain, ne dépassez jamais 120 000 km ou 5 ans. Utilisez exclusivement l'huile 0W30 norme Ford WSS-M2C950-A et démontez le carter inférieur pour nettoyer la crépine d'aspiration lors du changement du kit GK2Q-6K288-AB.",
                isAcceptedSolution = true,
                upvotes = 15
            )
        )
    ),
    ForumTopicItem(
        id = "FORUM-REN-01",
        group = AutoManufacturerGroup.RENAULT_NISSAN,
        carModelTag = "Renault Clio 5 / Dacia Duster 2",
        engineTag = "1.5 dCi K9K / 1.0 TCe",
        title = "Calibrage actuateur Wastegate électrique sur 1.0 TCe 100ch après remplacement",
        description = "Après remplacement de la tige de wastegate électrique sur une Clio 5 1.0 TCe, j'ai le code DTC1525F3. Quelle est la procédure exacte d'apprentissage des butées ?",
        authorName = "Yacine Auto Diagnostic",
        authorRoleBadge = "👤 Particulier / Passionné",
        timestamp = "Il y a 3h",
        isSolved = true,
        viewsCount = 112,
        answers = listOf(
            ForumAnswerItem(
                id = "ANS-5",
                authorName = "Mourad Tech Renault",
                authorRoleBadge = "🛠️ Technicien",
                timestamp = "Il y a 2h",
                content = "Il faut régler la tension de la tige à 3.25V contact mis (moteur arrêté) en lisant le paramètre 'Tension capteur position wastegate', serrer le contre-écrou, puis lancer la commande VP042 'Apprentissage butées turbocompresseur'.",
                isAcceptedSolution = true,
                upvotes = 21
            )
        )
    ),
    ForumTopicItem(
        id = "FORUM-VAG-02",
        group = AutoManufacturerGroup.VAG,
        carModelTag = "Seat Ibiza / Leon",
        engineTag = "1.6 TDI / 1.4 TSI",
        title = "Perte de liquide de refroidissement sans fuite visible sur Seat Leon 3 2.0 TDI",
        description = "Le bocal baisse tous les 400 km sur une Seat Leon 3. Pas de trace au sol, pompe à eau changée il y a 6 mois. Une piste ?",
        authorName = "Amine Conducteur",
        authorRoleBadge = "👤 Utilisateur",
        timestamp = "Il y a 4h",
        isSolved = true,
        viewsCount = 98,
        answers = listOf(
            ForumAnswerItem(
                id = "ANS-6",
                authorName = "Garage VAG Performance",
                authorRoleBadge = "🛠️ Atelier Pro",
                timestamp = "Il y a 3h",
                content = "Contrôle l'échangeur eau/air intégré dans le collecteur d'admission (intercooler à eau) ou le radiateur EGR. Très souvent sur le 2.0 TDI EA288, c'est l'intercooler d'admission qui suinte côté interne.",
                isAcceptedSolution = true,
                upvotes = 14
            )
        )
    )
)

// ==================== RÉSEAU SOCIAL PRO B2B + CHAT PRIVÉ ÉCRIT & VOCAL ====================

data class ProSocialComment(
    val id: String,
    val authorName: String,
    val authorRole: String,
    val timestamp: String,
    val message: String
)

data class ProSocialPartnerProfile(
    val id: String,
    val name: String,
    val roleType: String, // "🏪 Vendeur de Pièces" or "🛠️ Atelier de Réparation"
    val city: String,
    val specialty: String,
    val isConnected: Boolean,
    val isOnline: Boolean = true
)

data class ProSocialPartPost(
    val id: String,
    val authorName: String,
    val authorRoleBadge: String,
    val city: String,
    val timestamp: String,
    val postTypeBadge: String,
    val partTitle: String,
    val oemReference: String,
    val brand: String,
    val compatibleCarsSummary: String,
    val priceDzd: Int,
    val stockStatus: String,
    val description: String,
    val likesCount: Int,
    val isLikedByMe: Boolean = false,
    val comments: List<ProSocialComment>
)

data class ProPrivateChatMessage(
    val id: String,
    val partnerId: String,
    val senderName: String,
    val isFromMe: Boolean,
    val timestamp: String,
    val isVoiceMessage: Boolean = false,
    val textContent: String = "",
    val voiceDurationSec: Int = 0,
    val voiceWaveformHeights: List<Float> = listOf(0.3f, 0.7f, 0.9f, 0.5f, 0.8f, 0.4f, 0.95f, 0.6f, 0.75f, 0.5f, 0.85f, 0.4f),
    val voiceTranscriptNote: String = ""
)

val globalProPartners = mutableStateListOf(
    ProSocialPartnerProfile(
        id = "PARTNER-1",
        name = "AutoPièces Pro Bab Ezzouar",
        roleType = "🏪 Vendeur de Pièces",
        city = "Alger",
        specialty = "Spécialiste VAG • Bosch • LuK",
        isConnected = true,
        isOnline = true
    ),
    ProSocialPartnerProfile(
        id = "PARTNER-2",
        name = "Atelier MécaTech Diagnostic",
        roleType = "🛠️ Atelier de Réparation",
        city = "Alger",
        specialty = "Injection, Boîtes DSG & Électronique",
        isConnected = true,
        isOnline = true
    ),
    ProSocialPartnerProfile(
        id = "PARTNER-3",
        name = "Oran Pièces Express Store",
        roleType = "🏪 Vendeur de Pièces",
        city = "Oran",
        specialty = "PSA Stellantis • Renault • Valeo",
        isConnected = true,
        isOnline = true
    ),
    ProSocialPartnerProfile(
        id = "PARTNER-4",
        name = "Sétif Euro-Moteur Atelier",
        roleType = "🛠️ Atelier de Réparation",
        city = "Sétif",
        specialty = "Réfection Moteurs & Turbos",
        isConnected = false,
        isOnline = false
    ),
    ProSocialPartnerProfile(
        id = "PARTNER-5",
        name = "Constantine Freinage & Suspension",
        roleType = "🏪 Vendeur de Pièces",
        city = "Constantine",
        specialty = "Brembo • TRW • Sachs • Bilstein",
        isConnected = true,
        isOnline = true
    )
)

val globalProPrivateMessages = mutableStateListOf(
    ProPrivateChatMessage(
        id = "MSG-1",
        partnerId = "PARTNER-1",
        senderName = "AutoPièces Pro Bab Ezzouar",
        isFromMe = false,
        timestamp = "10:14",
        isVoiceMessage = false,
        textContent = "Salam Chef ! Le kit chaîne 8mm 1.5 BlueHDi (16 381 598 80) vient d'entrer en stock. Je te fais le tarif remise atelier à 51 500 DA si tu en prends 2."
    ),
    ProPrivateChatMessage(
        id = "MSG-2",
        partnerId = "PARTNER-1",
        senderName = "AutoPièces Pro Bab Ezzouar",
        isFromMe = false,
        timestamp = "10:15",
        isVoiceMessage = true,
        voiceDurationSec = 14,
        voiceWaveformHeights = listOf(0.35f, 0.8f, 0.65f, 0.95f, 0.5f, 0.85f, 0.7f, 0.9f, 0.45f, 0.8f, 0.6f, 0.4f),
        voiceTranscriptNote = "🎙️ Vocal : « J'ai aussi mis de côté les 4 injecteurs Bosch 04L130277AC pour ta Golf 7, le livreur peut partir d'ici 30 minutes. »"
    ),
    ProPrivateChatMessage(
        id = "MSG-3",
        partnerId = "PARTNER-1",
        senderName = "Vous (Mon Atelier / Store)",
        isFromMe = true,
        timestamp = "10:17",
        isVoiceMessage = false,
        textContent = "Parfait ! Mets-moi 1 kit 8mm PSA et les 4 injecteurs Bosch sur le bon de livraison, je t'envoie un vocal pour la référence du joint de couvre-culasse."
    ),
    ProPrivateChatMessage(
        id = "MSG-4",
        partnerId = "PARTNER-2",
        senderName = "Atelier MécaTech Diagnostic",
        isFromMe = false,
        timestamp = "09:45",
        isVoiceMessage = true,
        voiceDurationSec = 19,
        voiceWaveformHeights = listOf(0.4f, 0.6f, 0.9f, 0.75f, 0.85f, 0.5f, 0.95f, 0.65f, 0.8f, 0.55f, 0.7f, 0.35f),
        voiceTranscriptNote = "🎙️ Vocal : « Salut confrère, aurais-tu un capteur de pression différentielle FAP d'origine pour Tiguan 2.0 TDI en dépannage aujourd'hui ? »"
    ),
    ProPrivateChatMessage(
        id = "MSG-5",
        partnerId = "PARTNER-3",
        senderName = "Oran Pièces Express Store",
        isFromMe = false,
        timestamp = "Hier",
        isVoiceMessage = false,
        textContent = "Bonjour, le bloc mécatronique DSG7 DQ200 0AM927769D est prêt pour expédition express."
    )
)

val globalProSocialPosts = mutableStateListOf(
    ProSocialPartPost(
        id = "POST-B2B-01",
        authorName = "AutoPièces Pro Bab Ezzouar",
        authorRoleBadge = "🏪 Vendeur Store Certifié",
        city = "Alger",
        timestamp = "Il y a 12 min",
        postTypeBadge = "📦 NOUVELLE PIÈCE EN STOCK",
        partTitle = "Kit Chaîne Arbre à Cames Modifié 8mm + Carter Supérieur 1.5 BlueHDi",
        oemReference = "16 381 598 80",
        brand = "PSA ORIGINE / FAI",
        compatibleCarsSummary = "Peugeot 208, 308, 3008, Partner • Citroën C3, C4, Berlingo • Opel Corsa 1.5 BlueHDi DV5",
        priceDzd = 54000,
        stockStatus = "En stock immédiat (12 kits)",
        description = "Arrivage ce matin sur notre Store ! Kit complet de fiabilisation 8mm avec les deux arbres à cames, chaîne renforcée, tendeur et carter supérieur. Tarif pro B2B spécial ateliers connectés.",
        likesCount = 24,
        comments = listOf(
            ProSocialComment(
                id = "COM-1",
                authorName = "Garage El Amel Sétif",
                authorRole = "🛠️ Atelier de Réparation",
                timestamp = "Il y a 8 min",
                message = "Super nouvelle ! Est-ce que les joints d'injecteurs et la pâte d'étanchéité sont inclus dans ce carton ?"
            ),
            ProSocialComment(
                id = "COM-2",
                authorName = "AutoPièces Pro Bab Ezzouar",
                authorRole = "🏪 Vendeur de Pièces",
                timestamp = "Il y a 5 min",
                message = "@Garage El Amel Sétif Oui tout est inclus dans le pack complet, prêt à monter ! Livraison 24h disponible."
            )
        )
    ),
    ProSocialPartPost(
        id = "POST-B2B-02",
        authorName = "Oran Pièces Express Store",
        authorRoleBadge = "🏪 Vendeur Store Certifié",
        city = "Oran",
        timestamp = "Il y a 40 min",
        postTypeBadge = "📦 ARRIVAGE STORE",
        partTitle = "Mécatronique Boîte DSG7 DQ200 Renforcé + Accumulateur Hydraulique",
        oemReference = "0AM 927 769 D",
        brand = "VAG OEM / LuK",
        compatibleCarsSummary = "VW Golf 7, Polo, Passat • Audi A1, A3 • Seat Ibiza, Leon • Skoda Octavia DSG7",
        priceDzd = 115000,
        stockStatus = "En stock (5 unités)",
        description = "Disponible sur notre page Store : bloc mécatronique DQ200 neuf avec accumulateur de pression renforcé (corrige le code P17BF / P189C). Garantie 12 mois pour les ateliers partenaires.",
        likesCount = 31,
        comments = listOf(
            ProSocialComment(
                id = "COM-3",
                authorName = "Atelier MécaTech Diagnostic",
                authorRole = "🛠️ Atelier de Réparation",
                timestamp = "Il y a 22 min",
                message = "Je vous réserve 1 unité pour une Golf 7 1.4 TSI sur notre pont n°2. Est-il pré-vierge ou à cloner au banc ?"
            )
        )
    ),
    ProSocialPartPost(
        id = "POST-B2B-03",
        authorName = "Atelier MécaTech Diagnostic",
        authorRoleBadge = "🛠️ Atelier de Réparation",
        city = "Alger",
        timestamp = "Il y a 1h 15",
        postTypeBadge = "🔧 DISPO / ÉCHANGE PRO ATELIER",
        partTitle = "Pompe Haute Pression Bosch CP4 2.0 TDI Testée au Banc",
        oemReference = "04L 130 755 E",
        brand = "BOSCH (0 445 010 538)",
        compatibleCarsSummary = "VW Golf 7, Tiguan, Caddy 4 • Audi A3, A4 • Seat Leon 2.0 TDI",
        priceDzd = 64000,
        stockStatus = "Disponible à l'atelier (2 pièces)",
        description = "Nous partageons avec les collègues ateliers et revendeurs : 2 pompes Bosch CP4 neuves commandées en lot, disponibles immédiatement avec rapport de banc.",
        likesCount = 16,
        comments = emptyList()
    )
)

/**
 * 1. FORUM AUTOMOBILE PAR GROUPE CONSTRUCTEUR & RECHERCHE PAR VOITURE
 * Accessible à tous les utilisateurs (Ateliers, Vendeurs, Entreprises, Particuliers, Location).
 */
@Composable
fun AutoGroupForumModal(
    onOpenProSocialNetwork: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedGroup by remember { mutableStateOf(AutoManufacturerGroup.ALL) }
    var carSearchQuery by remember { mutableStateOf("") }
    var showCreateTopicDialog by remember { mutableStateOf(false) }
    var expandedTopicId by remember { mutableStateOf<String?>(globalForumTopics.firstOrNull()?.id) }
    var replyDraftMap by remember { mutableStateOf(mapOf<String, String>()) }

    val popularCarChips = listOf(
        "Toutes les voitures",
        "Golf 7",
        "Peugeot 208",
        "Peugeot 308",
        "Ford Focus",
        "Ford Transit",
        "Clio 5",
        "Duster",
        "Seat Leon"
    )

    val filteredTopics = remember(selectedGroup, carSearchQuery, globalForumTopics.size) {
        globalForumTopics.filter { topic ->
            val groupMatch = selectedGroup == AutoManufacturerGroup.ALL || topic.group == selectedGroup
            val q = carSearchQuery.trim()
            val carMatch = q.isEmpty() || q == "Toutes les voitures" ||
                topic.carModelTag.contains(q, ignoreCase = true) ||
                topic.engineTag.contains(q, ignoreCase = true) ||
                topic.title.contains(q, ignoreCase = true) ||
                topic.description.contains(q, ignoreCase = true) ||
                topic.group.fullBrandsLabel.contains(q, ignoreCase = true)
            groupMatch && carMatch
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            shape = RoundedCornerShape(22.dp),
            color = GraphiteDark,
            border = BorderStroke(1.5.dp, ElectricBlue)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ElectricBlue.copy(alpha = 0.18f))
                                .border(1.dp, ElectricBlue, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Forum, contentDescription = null, tint = ElectricBlue)
                        }
                        Column {
                            Text(
                                text = "FORUM AUTO PAR GROUPE CONSTRUCTEUR",
                                color = OffWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Ouvert à tous • Poser des questions, trouver des solutions par voiture",
                                color = AluminumGrey,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { showCreateTopicDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MechanicRed),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("forum_new_question_btn")
                        ) {
                            Icon(Icons.Default.AddComment, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Poser une question", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = OffWhite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Automotive Groups Horizontal Selector (Volkswagen, Stellantis, Ford, Renault...)
                Text(
                    text = "1️⃣ CHOISIR UN GROUPE AUTOMOBILE (SÉPARÉ PAR CONSTRUCTEUR) :",
                    color = MechanicOrange,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AutoManufacturerGroup.entries.forEach { group ->
                        val isSelected = selectedGroup == group
                        val groupColor = Color(group.badgeColorHex)
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedGroup = group }
                                .testTag("forum_group_${group.id}"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) groupColor else GraphiteMetal,
                            border = BorderStroke(1.dp, if (isSelected) groupColor else SteelBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(group.emoji, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = group.shortTitle,
                                        color = if (isSelected) Color.White else OffWhite,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = group.fullBrandsLabel,
                                    color = if (isSelected) Color.White.copy(alpha = 0.9f) else AluminumGrey,
                                    fontSize = 9.5.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search by Car Bar
                OutlinedTextField(
                    value = carSearchQuery,
                    onValueChange = { carSearchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("forum_search_by_car_input"),
                    placeholder = {
                        Text(
                            "🚗 Rechercher par voiture, moteur ou panne (ex: Golf 7, Peugeot 208, Ford Transit, P0087...)",
                            color = AluminumGrey,
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = ElectricBlue)
                    },
                    trailingIcon = {
                        if (carSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { carSearchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = AluminumGrey)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = SteelBorder,
                        focusedTextColor = OffWhite,
                        unfocusedTextColor = OffWhite
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Car Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    popularCarChips.forEach { carChip ->
                        val active = (carChip == "Toutes les voitures" && carSearchQuery.isEmpty()) ||
                            carSearchQuery.equals(carChip, ignoreCase = true)
                        FilterChip(
                            selected = active,
                            onClick = {
                                carSearchQuery = if (carChip == "Toutes les voitures") "" else carChip
                            },
                            label = {
                                Text(
                                    text = "🚗 $carChip",
                                    fontSize = 10.5.sp,
                                    fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricBlue.copy(alpha = 0.22f),
                                selectedLabelColor = OffWhite,
                                containerColor = GraphiteMetal,
                                labelColor = AluminumGrey
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Info & Switch to B2B Social Network Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredTopics.size} sujet(s) & solution(s) dans ${selectedGroup.shortTitle}",
                        color = OffWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                    TextButton(
                        onClick = onOpenProSocialNetwork,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = MechanicOrange, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Réseau Social Pro + Chat Vocal/Écrit →",
                            color = MechanicOrange,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Forum Topics List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTopics, key = { it.id }) { topic ->
                        val isExpanded = expandedTopicId == topic.id
                        val replyDraft = replyDraftMap[topic.id] ?: ""

                        ForumTopicCard(
                            topic = topic,
                            isExpanded = isExpanded,
                            replyDraft = replyDraft,
                            onToggleExpand = {
                                expandedTopicId = if (isExpanded) null else topic.id
                            },
                            onReplyDraftChange = { newText ->
                                replyDraftMap = replyDraftMap.toMutableMap().apply { put(topic.id, newText) }
                            },
                            onSubmitReply = { markAsSolution ->
                                if (replyDraft.isNotBlank()) {
                                    val idx = globalForumTopics.indexOfFirst { it.id == topic.id }
                                    if (idx >= 0) {
                                        val newAnswer = ForumAnswerItem(
                                            id = "ANS-${System.currentTimeMillis()}",
                                            authorName = "Vous (Membre Mekanik+)",
                                            authorRoleBadge = if (markAsSolution) "✅ Solution Pro" else "💬 Réponse",
                                            timestamp = "À l'instant",
                                            content = replyDraft.trim(),
                                            isAcceptedSolution = markAsSolution,
                                            upvotes = 1
                                        )
                                        val updatedAnswers = topic.answers + newAnswer
                                        globalForumTopics[idx] = topic.copy(
                                            isSolved = topic.isSolved || markAsSolution,
                                            answers = updatedAnswers
                                        )
                                        replyDraftMap = replyDraftMap.toMutableMap().apply { put(topic.id, "") }
                                        Toast.makeText(
                                            context,
                                            if (markAsSolution) "Solution publiée et validée !" else "Réponse ajoutée au sujet !",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showCreateTopicDialog) {
        CreateForumTopicDialog(
            initialGroup = if (selectedGroup == AutoManufacturerGroup.ALL) AutoManufacturerGroup.VAG else selectedGroup,
            onDismiss = { showCreateTopicDialog = false },
            onPublishTopic = { newTopic ->
                globalForumTopics.add(0, newTopic)
                expandedTopicId = newTopic.id
                showCreateTopicDialog = false
                Toast.makeText(context, "Question publiée dans ${newTopic.group.shortTitle} !", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun ForumTopicCard(
    topic: ForumTopicItem,
    isExpanded: Boolean,
    replyDraft: String,
    onToggleExpand: () -> Unit,
    onReplyDraftChange: (String) -> Unit,
    onSubmitReply: (Boolean) -> Unit
) {
    val groupColor = Color(topic.group.badgeColorHex)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToggleExpand() },
        shape = RoundedCornerShape(16.dp),
        color = GraphiteMetal,
        border = BorderStroke(1.dp, if (topic.isSolved) StatusGreen.copy(alpha = 0.55f) else SteelBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = groupColor.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, groupColor.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "${topic.group.emoji} ${topic.group.shortTitle}",
                            color = groupColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GraphiteDark,
                        border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "🚗 ${topic.carModelTag} (${topic.engineTag})",
                            color = OffWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }

                if (topic.isSolved) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = StatusGreen.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, StatusGreen)
                    ) {
                        Text(
                            text = "✅ SOLUTION VALIDÉE",
                            color = StatusGreen,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.5.sp,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = topic.title,
                color = OffWhite,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = topic.description,
                color = AluminumGrey,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Par ${topic.authorName} (${topic.authorRoleBadge}) • ${topic.timestamp}",
                    color = AluminumGrey,
                    fontSize = 10.5.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "💬 ${topic.answers.size} réponse(s)",
                        color = ElectricBlue,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = OffWhite
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = SteelBorder)

                    topic.answers.forEach { answer ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GraphiteDark,
                            border = BorderStroke(
                                1.dp,
                                if (answer.isAcceptedSolution) StatusGreen else SteelBorder
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = answer.authorName,
                                            color = OffWhite,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.5.sp
                                        )
                                        Text(
                                            text = answer.authorRoleBadge,
                                            color = MechanicOrange,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    }
                                    if (answer.isAcceptedSolution) {
                                        Text(
                                            text = "✅ MEILLEURE SOLUTION (+${answer.upvotes})",
                                            color = StatusGreen,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 10.sp
                                        )
                                    } else {
                                        Text(
                                            text = answer.timestamp,
                                            color = AluminumGrey,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = answer.content,
                                    color = OffWhite,
                                    fontSize = 11.5.sp
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = replyDraft,
                        onValueChange = onReplyDraftChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Proposer une solution ou répondre à ce sujet (${topic.carModelTag})...",
                                color = AluminumGrey,
                                fontSize = 11.sp
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StatusGreen,
                            unfocusedBorderColor = SteelBorder,
                            focusedTextColor = OffWhite,
                            unfocusedTextColor = OffWhite
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onSubmitReply(false) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, ElectricBlue)
                        ) {
                            Text("💬 Répondre", color = OffWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Button(
                            onClick = { onSubmitReply(true) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusGreen)
                        ) {
                            Text("✅ Proposer comme Solution", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateForumTopicDialog(
    initialGroup: AutoManufacturerGroup,
    onDismiss: () -> Unit,
    onPublishTopic: (ForumTopicItem) -> Unit
) {
    var chosenGroup by remember { mutableStateOf(initialGroup) }
    var carModelInput by remember { mutableStateOf("Volkswagen Golf 7") }
    var engineInput by remember { mutableStateOf("2.0 TDI 150ch") }
    var titleInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = GraphiteDark,
            border = BorderStroke(1.5.dp, MechanicRed)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "POSER UNE QUESTION DANS LE FORUM AUTO",
                    color = OffWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )

                Text(
                    text = "Groupe automobile :",
                    color = AluminumGrey,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AutoManufacturerGroup.entries.filter { it != AutoManufacturerGroup.ALL }.forEach { grp ->
                        val selected = chosenGroup == grp
                        FilterChip(
                            selected = selected,
                            onClick = { chosenGroup = grp },
                            label = {
                                Text("${grp.emoji} ${grp.shortTitle}", fontSize = 10.5.sp)
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = carModelInput,
                        onValueChange = { carModelInput = it },
                        label = { Text("Voiture / Modèle") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = engineInput,
                        onValueChange = { engineInput = it },
                        label = { Text("Motorisation") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = titleInput,
                    onValueChange = { titleInput = it },
                    label = { Text("Titre du problème ou de la question") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = descriptionInput,
                    onValueChange = { descriptionInput = it },
                    label = { Text("Détails (symptômes, codes défauts OBD, pièces déjà testées...)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Annuler", color = OffWhite)
                    }
                    Button(
                        onClick = {
                            if (titleInput.isNotBlank()) {
                                onPublishTopic(
                                    ForumTopicItem(
                                        id = "FORUM-NEW-${System.currentTimeMillis()}",
                                        group = chosenGroup,
                                        carModelTag = carModelInput.ifBlank { "Véhicule Multi-Marques" },
                                        engineTag = engineInput.ifBlank { "Standard" },
                                        title = titleInput.trim(),
                                        description = descriptionInput.ifBlank { titleInput.trim() },
                                        authorName = "Membre Mekanik+",
                                        authorRoleBadge = "🛠️ Utilisateur Vérifié",
                                        timestamp = "À l'instant",
                                        isSolved = false,
                                        viewsCount = 1,
                                        answers = emptyList()
                                    )
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MechanicRed)
                    ) {
                        Text("Publier", color = Color.White, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

/**
 * 2. RÉSEAU SOCIAL PROFESSIONNEL B2B (DESTINÉ UNIQUEMENT AUX VENDEURS DE PIÈCES & ATELIERS DE RÉPARATION)
 * Inclut :
 * - Fil d'actualité des nouvelles pièces postées sur les Stores / Ateliers + Commentaires
 * - S'ajouter mutuellement entre Vendeurs & Ateliers
 * - Chat Privé Intime par Message Écrit ou Message Vocal (🎙️ Enregistrement & Écoute Vocale)
 */
@Composable
fun ProB2bSocialNetworkModal(
    onOpenForum: () -> Unit = {},
    onOpenVendorAddPhotoAi: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var activeSocialTab by remember { mutableIntStateOf(0) } // 0 = Fil d'actualité Pièces, 1 = Chat Privé Écrit & Vocal
    var selectedChatPartnerId by remember { mutableStateOf("PARTNER-1") }
    var searchFeedQuery by remember { mutableStateOf("") }
    var filterOnlyConnected by remember { mutableStateOf(false) }
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var commentDraftMap by remember { mutableStateOf(mapOf<String, String>()) }

    val connectedNames = remember(globalProPartners.toList()) {
        globalProPartners.filter { it.isConnected }.map { it.name }.toSet()
    }

    val filteredPosts = remember(searchFeedQuery, filterOnlyConnected, globalProSocialPosts.size, connectedNames) {
        globalProSocialPosts.filter { post ->
            val matchConnection = !filterOnlyConnected || connectedNames.contains(post.authorName) || post.authorName.contains("Votre")
            val q = searchFeedQuery.trim()
            val matchQuery = q.isEmpty() ||
                post.partTitle.contains(q, ignoreCase = true) ||
                post.oemReference.replace(" ", "").contains(q.replace(" ", ""), ignoreCase = true) ||
                post.compatibleCarsSummary.contains(q, ignoreCase = true) ||
                post.authorName.contains(q, ignoreCase = true) ||
                post.city.contains(q, ignoreCase = true)
            matchConnection && matchQuery
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            shape = RoundedCornerShape(22.dp),
            color = GraphiteDark,
            border = BorderStroke(1.5.dp, MechanicOrange)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MechanicOrange.copy(alpha = 0.18f))
                                .border(1.dp, MechanicOrange, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Groups, contentDescription = null, tint = MechanicOrange)
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "RÉSEAU SOCIAL PRO : VENDEURS & ATELIERS",
                                    color = OffWhite,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MechanicRed.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, MechanicRed)
                                ) {
                                    Text(
                                        text = "PRO B2B",
                                        color = MechanicRed,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Fil de pièces des Stores + Commentaires + Chat privé écrit & vocal entre pros",
                                color = AluminumGrey,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { showCreatePostDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MechanicOrange),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("pro_social_new_post_btn")
                        ) {
                            Icon(Icons.Default.PostAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Publier une pièce", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = OffWhite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mode Switcher: Fil d'Actualité Pièces vs Messagerie Privée Écrite & Vocale
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { activeSocialTab = 0 }
                            .testTag("pro_social_tab_feed"),
                        color = if (activeSocialTab == 0) MechanicOrange else GraphiteMetal,
                        border = BorderStroke(1.dp, if (activeSocialTab == 0) MechanicOrange else SteelBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.DynamicFeed,
                                contentDescription = null,
                                tint = if (activeSocialTab == 0) Color.White else OffWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "📦 Fil Pièces & Stores (${globalProSocialPosts.size})",
                                color = if (activeSocialTab == 0) Color.White else OffWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { activeSocialTab = 1 }
                            .testTag("pro_social_tab_private_chat"),
                        color = if (activeSocialTab == 1) ElectricBlue else GraphiteMetal,
                        border = BorderStroke(1.dp, if (activeSocialTab == 1) ElectricBlue else SteelBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Mic,
                                contentDescription = null,
                                tint = if (activeSocialTab == 1) Color.White else AiCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🔒 Chat Privé Écrit & Vocal",
                                color = if (activeSocialTab == 1) Color.White else OffWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (activeSocialTab == 0) {
                    // ==================== ONGLET 1 : FIL D'ACTUALITÉ PIÈCES & CONNEXIONS ====================
                    Text(
                        text = "🤝 VENDEURS DE PIÈCES & ATELIERS (S'AJOUTER MUTUELLEMENT OU DISCUTER EN PRIVÉ) :",
                        color = AiCyan,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        globalProPartners.forEachIndexed { idx, partner ->
                            Surface(
                                modifier = Modifier.width(235.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = GraphiteMetal,
                                border = BorderStroke(
                                    1.dp,
                                    if (partner.isConnected) StatusGreen.copy(alpha = 0.6f) else SteelBorder
                                )
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = partner.roleType,
                                            color = MechanicOrange,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = if (partner.isOnline) "🟢 En ligne" else "⚪ Hors ligne",
                                            color = if (partner.isOnline) StatusGreen else AluminumGrey,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = partner.name,
                                        color = OffWhite,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "📍 ${partner.city} • ${partner.specialty}",
                                        color = AluminumGrey,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                val toggled = !partner.isConnected
                                                globalProPartners[idx] = partner.copy(isConnected = toggled)
                                                Toast.makeText(
                                                    context,
                                                    if (toggled) "Connecté avec ${partner.name} !"
                                                    else "Connexion retirée avec ${partner.name}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (partner.isConnected) StatusGreen else ElectricBlue
                                            )
                                        ) {
                                            Text(
                                                text = if (partner.isConnected) "✓ Connecté" else "+ S'ajouter",
                                                color = Color.White,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 10.sp
                                            )
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                selectedChatPartnerId = partner.id
                                                activeSocialTab = 1
                                            },
                                            modifier = Modifier.height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, AiCyan)
                                        ) {
                                            Text(
                                                text = "🎙️/💬 Chat",
                                                color = OffWhite,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search & Filter Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchFeedQuery,
                            onValueChange = { searchFeedQuery = it },
                            modifier = Modifier.weight(1f),
                            placeholder = {
                                Text(
                                    "Chercher une pièce publiée, réf OEM, store ou ville...",
                                    color = AluminumGrey,
                                    fontSize = 11.5.sp
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = MechanicOrange)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MechanicOrange,
                                unfocusedBorderColor = SteelBorder,
                                focusedTextColor = OffWhite,
                                unfocusedTextColor = OffWhite
                            )
                        )

                        FilterChip(
                            selected = filterOnlyConnected,
                            onClick = { filterOnlyConnected = !filterOnlyConnected },
                            label = {
                                Text("Mes Connexions", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Feed Posts List
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredPosts, key = { it.id }) { post ->
                            val draftComment = commentDraftMap[post.id] ?: ""
                            ProSocialPostCard(
                                post = post,
                                commentDraft = draftComment,
                                onCommentDraftChange = { txt ->
                                    commentDraftMap = commentDraftMap.toMutableMap().apply { put(post.id, txt) }
                                },
                                onToggleLike = {
                                    val idx = globalProSocialPosts.indexOfFirst { it.id == post.id }
                                    if (idx >= 0) {
                                        val liked = !post.isLikedByMe
                                        globalProSocialPosts[idx] = post.copy(
                                            isLikedByMe = liked,
                                            likesCount = if (liked) post.likesCount + 1 else post.likesCount - 1
                                        )
                                    }
                                },
                                onOpenPrivateChat = {
                                    val matchingPartner = globalProPartners.firstOrNull {
                                        it.name.equals(post.authorName, ignoreCase = true)
                                    }
                                    if (matchingPartner != null) {
                                        selectedChatPartnerId = matchingPartner.id
                                    }
                                    activeSocialTab = 1
                                },
                                onSubmitComment = {
                                    if (draftComment.isNotBlank()) {
                                        val idx = globalProSocialPosts.indexOfFirst { it.id == post.id }
                                        if (idx >= 0) {
                                            val newComment = ProSocialComment(
                                                id = "COM-${System.currentTimeMillis()}",
                                                authorName = "Votre Compte Pro",
                                                authorRole = "🛠️/🏪 Membre Pro Connecté",
                                                timestamp = "À l'instant",
                                                message = draftComment.trim()
                                            )
                                            globalProSocialPosts[idx] = post.copy(
                                                comments = post.comments + newComment
                                            )
                                            commentDraftMap = commentDraftMap.toMutableMap().apply { put(post.id, "") }
                                            Toast.makeText(context, "Commentaire publié sur la pièce !", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )
                        }
                    }
                } else {
                    // ==================== ONGLET 2 : MESSAGERIE PRIVÉE PRO (ÉCRIT & VOCAL) ====================
                    ProPrivateTextAndVoiceChatSection(
                        selectedPartnerId = selectedChatPartnerId,
                        onSelectPartner = { selectedChatPartnerId = it },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    if (showCreatePostDialog) {
        CreateProSocialPostDialog(
            onDismiss = { showCreatePostDialog = false },
            onPublishPost = { newPost ->
                globalProSocialPosts.add(0, newPost)
                showCreatePostDialog = false
                Toast.makeText(context, "Pièce publiée sur le fil social B2B Vendeurs & Ateliers !", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun ProPrivateTextAndVoiceChatSection(
    selectedPartnerId: String,
    onSelectPartner: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var writtenMessageDraft by remember { mutableStateOf("") }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableIntStateOf(0) }
    var playingMessageId by remember { mutableStateOf<String?>(null) }

    val activePartner = globalProPartners.firstOrNull { it.id == selectedPartnerId } ?: globalProPartners.first()
    val partnerMessages = globalProPrivateMessages.filter { it.partnerId == activePartner.id }

    LaunchedEffect(isRecordingVoice) {
        if (isRecordingVoice) {
            recordingSeconds = 1
            while (isRecordingVoice) {
                delay(1000)
                recordingSeconds += 1
            }
        } else {
            recordingSeconds = 0
        }
    }

    LaunchedEffect(playingMessageId) {
        if (playingMessageId != null) {
            delay(3500)
            playingMessageId = null
        }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        isRecordingVoice = true
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Contacts Bar
        Text(
            text = "🔒 CHOISIR UN CONFRÈRE (VENDEUR OU ATELIER) POUR UN ÉCHANGE PRIVÉ EN TOUTE INTIMITÉ :",
            color = AiCyan,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 10.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            globalProPartners.forEach { partner ->
                val isSelected = partner.id == activePartner.id
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectPartner(partner.id) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) ElectricBlue else GraphiteMetal,
                    border = BorderStroke(1.dp, if (isSelected) ElectricBlue else SteelBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (partner.isOnline) StatusGreen else AluminumGrey)
                        )
                        Column {
                            Text(
                                text = partner.name,
                                color = if (isSelected) Color.White else OffWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${partner.roleType} • ${partner.city}",
                                color = if (isSelected) Color.White.copy(alpha = 0.85f) else AluminumGrey,
                                fontSize = 9.5.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Active Conversation Header Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = GraphiteMetal,
            border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.45f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Conversation privée avec ${activePartner.name}",
                        color = OffWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${activePartner.roleType} (${activePartner.city}) • Messages écrits & vocaux chiffrés entre pros",
                        color = AluminumGrey,
                        fontSize = 10.5.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StatusGreen.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, StatusGreen)
                ) {
                    Text(
                        text = "🔒 DIRECT B2B",
                        color = StatusGreen,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 9.5.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Messages Thread
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(partnerMessages, key = { it.id }) { msg ->
                val isPlaying = playingMessageId == msg.id
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isFromMe) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        modifier = Modifier.widthIn(max = 320.dp),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (msg.isFromMe) 16.dp else 4.dp,
                            bottomEnd = if (msg.isFromMe) 4.dp else 16.dp
                        ),
                        color = if (msg.isFromMe) ElectricBlue.copy(alpha = 0.24f) else GraphiteMetal,
                        border = BorderStroke(
                            1.dp,
                            if (msg.isFromMe) ElectricBlue else SteelBorder
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = msg.senderName,
                                    color = if (msg.isFromMe) AiCyan else MechanicOrange,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.5.sp
                                )
                                Text(
                                    text = msg.timestamp,
                                    color = AluminumGrey,
                                    fontSize = 9.5.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            if (msg.isVoiceMessage) {
                                // Voice Message Bubble with Waveform & Play/Pause
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = GraphiteDark,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isPlaying) StatusGreen else MechanicOrange.copy(alpha = 0.55f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            playingMessageId = if (isPlaying) null else msg.id
                                        }
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isPlaying) StatusGreen else MechanicOrange),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                    contentDescription = "Écouter le message vocal",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            // Audio Waveform Canvas
                                            Canvas(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(28.dp)
                                            ) {
                                                val bars = msg.voiceWaveformHeights
                                                val barWidth = (size.width / (bars.size * 1.8f)).coerceAtLeast(4f)
                                                val spacing = barWidth * 0.75f
                                                bars.forEachIndexed { idx, ratio ->
                                                    val barH = (size.height * ratio).coerceAtLeast(6f)
                                                    val topY = (size.height - barH) / 2f
                                                    val xPos = idx * (barWidth + spacing)
                                                    drawRoundRect(
                                                        color = if (isPlaying) Color(0xFF10B981) else Color(0xFFFF7A00),
                                                        topLeft = Offset(xPos, topY),
                                                        size = Size(barWidth, barH),
                                                        cornerRadius = CornerRadius(4f, 4f)
                                                    )
                                                }
                                            }

                                            Text(
                                                text = "0:${msg.voiceDurationSec.toString().padStart(2, '0')}",
                                                color = OffWhite,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 11.sp
                                            )
                                        }

                                        if (msg.voiceTranscriptNote.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = msg.voiceTranscriptNote,
                                                color = AluminumGrey,
                                                fontSize = 10.5.sp
                                            )
                                        }
                                    }
                                }
                            } else {
                                // Written Message
                                Text(
                                    text = msg.textContent,
                                    color = OffWhite,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Live Voice Recording Banner
        AnimatedVisibility(visible = isRecordingVoice) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(14.dp),
                color = MechanicRed.copy(alpha = 0.18f),
                border = BorderStroke(1.5.dp, MechanicRed)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.FiberManualRecord, contentDescription = null, tint = MechanicRed, modifier = Modifier.size(18.dp))
                        Text(
                            text = "🎙️ Enregistrement vocal en cours... 0:${recordingSeconds.toString().padStart(2, '0')}",
                            color = OffWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextButton(onClick = { isRecordingVoice = false }) {
                            Text("Annuler", color = AluminumGrey, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Button(
                            onClick = {
                                val finalDuration = recordingSeconds.coerceAtLeast(5)
                                isRecordingVoice = false
                                globalProPrivateMessages.add(
                                    ProPrivateChatMessage(
                                        id = "MSG-VOC-${System.currentTimeMillis()}",
                                        partnerId = activePartner.id,
                                        senderName = "Vous (Mon Atelier / Store)",
                                        isFromMe = true,
                                        timestamp = "À l'instant",
                                        isVoiceMessage = true,
                                        voiceDurationSec = finalDuration,
                                        voiceTranscriptNote = "🎙️ Message vocal envoyé à ${activePartner.name} (${finalDuration}s) • Confirmation disponibilité & tarif B2B."
                                    )
                                )
                                Toast.makeText(
                                    context,
                                    "Message vocal (${finalDuration}s) envoyé à ${activePartner.name} !",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Envoyer le vocal", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Composer Bar: Written Message Input + Send Text + Record Voice Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = writtenMessageDraft,
                onValueChange = { writtenMessageDraft = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("pro_private_chat_text_input"),
                placeholder = {
                    Text(
                        "Écrire un message privé à ${activePartner.name}...",
                        color = AluminumGrey,
                        fontSize = 11.5.sp
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = SteelBorder,
                    focusedTextColor = OffWhite,
                    unfocusedTextColor = OffWhite
                )
            )

            // Send Written Message Button
            Button(
                onClick = {
                    if (writtenMessageDraft.isNotBlank()) {
                        globalProPrivateMessages.add(
                            ProPrivateChatMessage(
                                id = "MSG-TXT-${System.currentTimeMillis()}",
                                partnerId = activePartner.id,
                                senderName = "Vous (Mon Atelier / Store)",
                                isFromMe = true,
                                timestamp = "À l'instant",
                                isVoiceMessage = false,
                                textContent = writtenMessageDraft.trim()
                            )
                        )
                        writtenMessageDraft = ""
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp),
                modifier = Modifier.testTag("pro_private_send_text_btn")
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Envoyer message écrit", tint = Color.White, modifier = Modifier.size(18.dp))
            }

            // Record Voice Message Button
            Button(
                onClick = {
                    if (!isRecordingVoice) {
                        val hasAudioPerm = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasAudioPerm) {
                            isRecordingVoice = true
                        } else {
                            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    } else {
                        val finalDuration = recordingSeconds.coerceAtLeast(4)
                        isRecordingVoice = false
                        globalProPrivateMessages.add(
                            ProPrivateChatMessage(
                                id = "MSG-VOC-${System.currentTimeMillis()}",
                                partnerId = activePartner.id,
                                senderName = "Vous (Mon Atelier / Store)",
                                isFromMe = true,
                                timestamp = "À l'instant",
                                isVoiceMessage = true,
                                voiceDurationSec = finalDuration,
                                voiceTranscriptNote = "🎙️ Message vocal privé envoyé à ${activePartner.name} (${finalDuration}s)."
                            )
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecordingVoice) StatusGreen else MechanicOrange
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp),
                modifier = Modifier.testTag("pro_private_record_voice_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Envoyer un message vocal",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isRecordingVoice) "Envoyer" else "Vocal",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun ProSocialPostCard(
    post: ProSocialPartPost,
    commentDraft: String,
    onCommentDraftChange: (String) -> Unit,
    onToggleLike: () -> Unit,
    onOpenPrivateChat: () -> Unit,
    onSubmitComment: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = GraphiteMetal,
        border = BorderStroke(1.dp, SteelBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Author Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MechanicOrange.copy(alpha = 0.2f))
                            .border(1.dp, MechanicOrange, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = MechanicOrange, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text(
                            text = post.authorName,
                            color = OffWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${post.authorRoleBadge} • 📍 ${post.city} • ${post.timestamp}",
                            color = AluminumGrey,
                            fontSize = 10.5.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StatusGreen.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, StatusGreen.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = post.postTypeBadge,
                        color = StatusGreen,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 9.5.sp,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Part Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GraphiteDark,
                border = BorderStroke(1.dp, MechanicRed.copy(alpha = 0.45f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MechanicRed.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "RÉF OEM : ${post.oemReference}",
                                color = MechanicRed,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.5.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Text(
                            text = "${post.priceDzd} DA",
                            color = StatusGreen,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = post.partTitle,
                        color = OffWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.5.sp
                    )
                    Text(
                        text = "Marque : ${post.brand} • ${post.stockStatus}",
                        color = MechanicOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🚗 Compatibilités : ${post.compatibleCarsSummary}",
                        color = AluminumGrey,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.description,
                color = OffWhite,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Like, Private Chat & Comment Counter Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onToggleLike,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        border = BorderStroke(1.dp, if (post.isLikedByMe) MechanicRed else SteelBorder)
                    ) {
                        Icon(
                            Icons.Default.ThumbUp,
                            contentDescription = null,
                            tint = if (post.isLikedByMe) MechanicRed else OffWhite,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Dispo (${post.likesCount})",
                            color = OffWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenPrivateChat,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        border = BorderStroke(1.dp, AiCyan)
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = null,
                            tint = AiCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Chat Privé Écrit / Vocal",
                            color = OffWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Text(
                    text = "💬 ${post.comments.size} commentaire(s)",
                    color = ElectricBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = SteelBorder)
            Spacer(modifier = Modifier.height(8.dp))

            // Comments List
            post.comments.forEach { comment ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteDark
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${comment.authorName} (${comment.authorRole})",
                                color = MechanicOrange,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.5.sp
                            )
                            Text(
                                text = comment.timestamp,
                                color = AluminumGrey,
                                fontSize = 9.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = comment.message,
                            color = OffWhite,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Add Comment Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentDraft,
                    onValueChange = onCommentDraftChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text("Commenter cette pièce ou demander disponibilité...", color = AluminumGrey, fontSize = 11.sp)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = SteelBorder,
                        focusedTextColor = OffWhite,
                        unfocusedTextColor = OffWhite
                    )
                )

                Button(
                    onClick = onSubmitComment,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Commenter", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun CreateProSocialPostDialog(
    onDismiss: () -> Unit,
    onPublishPost: (ProSocialPartPost) -> Unit
) {
    var authorNameInput by remember { mutableStateOf("Mon Store / Atelier Pro") }
    var cityInput by remember { mutableStateOf("Alger") }
    var partTitleInput by remember { mutableStateOf("") }
    var oemRefInput by remember { mutableStateOf("") }
    var brandInput by remember { mutableStateOf("BOSCH / VALEO OEM") }
    var compatibleCarsInput by remember { mutableStateOf("Golf 7, Audi A3, Seat Leon 2.0 TDI") }
    var priceInput by remember { mutableStateOf("35000") }
    var descriptionInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = GraphiteDark,
            border = BorderStroke(1.5.dp, MechanicOrange)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "PUBLIER UNE PIÈCE SUR LE RÉSEAU PRO (VENDEURS & ATELIERS)",
                    color = OffWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.5.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = authorNameInput,
                        onValueChange = { authorNameInput = it },
                        label = { Text("Nom du Store ou Atelier") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = cityInput,
                        onValueChange = { cityInput = it },
                        label = { Text("Ville") },
                        modifier = Modifier.weight(0.6f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = partTitleInput,
                    onValueChange = { partTitleInput = it },
                    label = { Text("Nom de la pièce disponible") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = oemRefInput,
                        onValueChange = { oemRefInput = it },
                        label = { Text("Référence OEM") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { priceInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Prix Pro (DA)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = compatibleCarsInput,
                    onValueChange = { compatibleCarsInput = it },
                    label = { Text("Véhicules compatibles") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = descriptionInput,
                    onValueChange = { descriptionInput = it },
                    label = { Text("Message pour les ateliers et vendeurs connectés") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Annuler", color = OffWhite)
                    }
                    Button(
                        onClick = {
                            if (partTitleInput.isNotBlank()) {
                                onPublishPost(
                                    ProSocialPartPost(
                                        id = "POST-B2B-${System.currentTimeMillis()}",
                                        authorName = authorNameInput.ifBlank { "Mon Store Pro" },
                                        authorRoleBadge = "🏪/🛠️ Professionnel Connecté",
                                        city = cityInput.ifBlank { "Alger" },
                                        timestamp = "À l'instant",
                                        postTypeBadge = "📦 NOUVELLE PIÈCE PUBLIÉE",
                                        partTitle = partTitleInput.trim(),
                                        oemReference = oemRefInput.ifBlank { "OEM-PRO-2026" },
                                        brand = brandInput,
                                        compatibleCarsSummary = compatibleCarsInput,
                                        priceDzd = priceInput.toIntOrNull() ?: 25000,
                                        stockStatus = "En stock immédiat",
                                        description = descriptionInput.ifBlank { "Pièce disponible immédiatement dans notre stock pour les ateliers et vendeurs partenaires." },
                                        likesCount = 1,
                                        isLikedByMe = true,
                                        comments = emptyList()
                                    )
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MechanicOrange)
                    ) {
                        Text("Publier", color = Color.White, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}
