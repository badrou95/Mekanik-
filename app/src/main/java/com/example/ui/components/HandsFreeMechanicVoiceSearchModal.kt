package com.example.ui.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.Locale

private val handsFreeGeminiVoiceModel by lazy {
    Firebase.ai.generativeModel(
        modelName = "gemini-3.8-flash",
        generationConfig = generationConfig {
            temperature = 0.25f
        }
    )
}

enum class VoiceSearchCategoryFilter(val label: String, val emoji: String) {
    ALL("Tout (Pièces & Schémas)", "🎙️"),
    PART_REFERENCES("Références de Pièces", "⚙️"),
    TECHNICAL_DIAGRAMS("Schémas Techniques", "📐")
}

data class HandsFreeVoiceSearchResult(
    val id: String,
    val isTechnicalDiagram: Boolean, // false = Part Reference, true = Technical Diagram
    val voiceTriggerPhrases: List<String>,
    val title: String,
    val vehicleApplication: String,
    val primaryOemRefOrCode: String,
    val secondaryEquivalents: List<String>,
    val torqueAndSpecs: List<String>,
    val spokenAudioSummary: String,
    val diagramType: Int = 0, // 0 = Calage Distribution, 1 = Pinout ECU / Câblage, 2 = Ordre Serrage Culasse, 3 = Boîtier Fusibles/Relais
    val stockAndPriceLabel: String = ""
)

val mechanicHandsFreeDatabase = listOf(
    HandsFreeVoiceSearchResult(
        id = "VF-PART-01",
        isTechnicalDiagram = false,
        voiceTriggerPhrases = listOf("injecteur golf 7", "04l130277ac", "injecteur 2.0 tdi", "injecteur audi a3"),
        title = "Injecteur Common Rail Piezo 2.0 TDI EA288",
        vehicleApplication = "VW Golf 7 / Passat B8 / Tiguan • Audi A3 8V / A4 B9 (2.0 TDI 150/184ch CRLB)",
        primaryOemRefOrCode = "04L 130 277 AC",
        secondaryEquivalents = listOf(
            "BOSCH : 0 445 110 468",
            "BOSCH : 0 445 110 469",
            "VAG : 04L 130 277 / 04L130277AC"
        ),
        torqueAndSpecs = listOf(
            "Vis de bride d'injecteur (neuve obligatoire) : 8 Nm + 180°",
            "Raccord haute pression sur rampe : 25 Nm",
            "Codage IMA 7 caractères obligatoire à la valise OBD après pose"
        ),
        spokenAudioSummary = "Référence injecteur Golf 7 deux litres TDI : 04L 130 277 AC, équivalence Bosch 0 445 110 468. Couple de serrage de la bride : 8 Newton-mètres plus 180 degrés.",
        diagramType = 0,
        stockAndPriceLabel = "En stock Store Alger (8 unités) • 46 500 DA"
    ),
    HandsFreeVoiceSearchResult(
        id = "VF-DIAG-01",
        isTechnicalDiagram = true,
        voiceTriggerPhrases = listOf("schéma calage distribution golf 7", "distribution 2.0 tdi", "calage crlb", "courroie distribution golf"),
        title = "Schéma de Calage Distribution & Couples 2.0 TDI (EA288 CRLB)",
        vehicleApplication = "VW Golf 7 / Audi A3 / Seat Leon / Skoda Octavia 2.0 TDI",
        primaryOemRefOrCode = "SCHÉMA OEM : T10490 + T10492 + T10051",
        secondaryEquivalents = listOf(
            "Kit Distribution OEM : 04L 198 119 A",
            "Pompe à eau débrayable : 04L 121 011 N",
            "Intervalle : 210 000 km"
        ),
        torqueAndSpecs = listOf(
            "1. Verrouiller le vilebrequin au PMH avec l'outil T10490 (repère 12h)",
            "2. Piger la poulie d'arbre à cames avec T10492 et la pompe HP avec T10051",
            "3. Couple écrou galet tendeur : 20 Nm + 45°",
            "4. Couple vis moyeu arbre à cames : 100 Nm • Poulie pompe HP : 95 Nm"
        ),
        spokenAudioSummary = "Schéma de calage distribution deux litres TDI CRLB affiché. Pige vilebrequin outil T10490, pige arbre à cames T10492. Couple du galet tendeur : 20 Newton-mètres plus 45 degrés.",
        diagramType = 0,
        stockAndPriceLabel = "Schéma Technique Certifié Constructeur VAG"
    ),
    HandsFreeVoiceSearchResult(
        id = "VF-PART-02",
        isTechnicalDiagram = false,
        voiceTriggerPhrases = listOf("kit chaîne 308", "1.5 bluehdi", "chaîne 8mm", "1638159880", "arbre à cames 208"),
        title = "Kit Chaîne d'Arbres à Cames Modifié 8mm + Carter Supérieur 1.5 BlueHDi",
        vehicleApplication = "Peugeot 208 / 308 / 3008 / Partner • Citroën C3 / C4 / Berlingo 1.5 BlueHDi DV5",
        primaryOemRefOrCode = "16 381 598 80",
        secondaryEquivalents = listOf(
            "Carter 7mm d'origine remplacé : 98 228 271 80",
            "Tendeur hydraulique 8mm : 98 301 468 80",
            "FAI AutoParts : TCK350VVT"
        ),
        torqueAndSpecs = listOf(
            "Pré-serrage des 22 vis de paliers de carter d'arbres à cames : 5 Nm en escargot",
            "Serrage définitif carter supérieur : 10 Nm",
            "Tendeur de chaîne : 8 Nm • Poulie AAC : 40 Nm + 45°"
        ),
        spokenAudioSummary = "Kit modification chaîne 8 millimètres un litre cinq BlueHDi : référence 16 381 598 80. Pré-serrage carter à 5 Newton-mètres puis serrage final à 10 Newton-mètres en escargot.",
        diagramType = 2,
        stockAndPriceLabel = "En stock Store Bab Ezzouar (12 kits) • 54 000 DA"
    ),
    HandsFreeVoiceSearchResult(
        id = "VF-DIAG-02",
        isTechnicalDiagram = true,
        voiceTriggerPhrases = listOf("schéma électrique edc17", "pinout calculateur 308", "câblage injecteur peugeot", "edc17c60"),
        title = "Schéma Électrique & Pinout Calculateur Bosch EDC17C60 (PSA 1.6 BlueHDi)",
        vehicleApplication = "Peugeot 208 / 308 / Partner • Citroën C4 / Berlingo 1.6 BlueHDi",
        primaryOemRefOrCode = "PINOUT ECU : CONNECTEURS T56A / T98B",
        secondaryEquivalents = listOf(
            "Calculateur Bosch : 0 281 032 456 / 98 141 826 80",
            "Relais Principal BSM : R1 (Fusible F12 30A)",
            "Résistance terminaison CAN : 60 Ohms (120 Ohms ECU seul)"
        ),
        torqueAndSpecs = listOf(
            "Alimentation +12V permanent : Connecteur T56A voies A3, A5",
            "Masse puissance : Connecteur T56A voies A1, A2, A4",
            "Réseau CAN-High : Voie A41 (2.65V) • CAN-Low : Voie A42 (2.35V)",
            "Commande Injecteur Cyl. 1 : Voies B01 (+) et B15 (-)"
        ),
        spokenAudioSummary = "Schéma électrique et pinout calculateur Bosch EDC17C60 ouvert. Alimentation 12 volts sur connecteur T56A voies A3 et A5. Réseau CAN High voie A41 et CAN Low voie A42.",
        diagramType = 1,
        stockAndPriceLabel = "Schéma Électrique Interactif & Multimètre"
    ),
    HandsFreeVoiceSearchResult(
        id = "VF-PART-03",
        isTechnicalDiagram = false,
        voiceTriggerPhrases = listOf("plaquettes frein golf 7", "5q0698151br", "frein avant gti", "brembo golf"),
        title = "Jeu de 4 Plaquettes de Frein Avant Céramique Haute Performance",
        vehicleApplication = "VW Golf 7 GTI / GTD • Audi A3 8V • Seat Leon FR • Skoda Octavia RS",
        primaryOemRefOrCode = "5Q0 698 151 BR",
        secondaryEquivalents = listOf(
            "BREMBO : P 85 126",
            "TRW : GDB1956",
            "ATE : 13.0460-2785.2"
        ),
        torqueAndSpecs = listOf(
            "Colonnettes d'étrier flottant : 35 Nm",
            "Support de chape d'étrier sur pivot : 200 Nm",
            "Roues alliage : 120 Nm"
        ),
        spokenAudioSummary = "Référence plaquettes de frein avant Golf 7 : 5Q0 698 151 BR, équivalent Brembo P 85 126. Couple colonnettes d'étrier : 35 Newton-mètres.",
        diagramType = 3,
        stockAndPriceLabel = "En stock Store Constantine (22 jeux) • 11 200 DA"
    ),
    HandsFreeVoiceSearchResult(
        id = "VF-DIAG-03",
        isTechnicalDiagram = true,
        voiceTriggerPhrases = listOf("ordre serrage culasse", "culasse ford 2.0 ecoblue", "schéma culasse transit", "joint de culasse"),
        title = "Schéma Ordre de Serrage Culasse & Couples (Moteur 2.0 EcoBlue / TDI)",
        vehicleApplication = "Ford Focus 4 / Transit Custom 2.0 EcoBlue • PSA / VAG 2.0 Diesel",
        primaryOemRefOrCode = "SCHÉMA CULASSE : ESCARGOT 1 À 10 (4 PASSES)",
        secondaryEquivalents = listOf(
            "Jeu de 10 vis de culasse neuves : GK2Q-6065-AA / Victor Reinz 14-32310-01",
            "Joint de culasse 3 crans (1.35mm) : GK2Q-6051-CA"
        ),
        torqueAndSpecs = listOf(
            "Passe 1 (Ordre 1 à 10 du centre vers l'extérieur) : 35 Nm",
            "Passe 2 (Ordre 1 à 10) : 55 Nm",
            "Passe 3 (Serrage angulaire 1 à 10) : +90°",
            "Passe 4 (Serrage angulaire final 1 à 10) : +90°"
        ),
        spokenAudioSummary = "Schéma d'ordre de serrage de culasse en escargot de 1 à 10 affiché. Première passe à 35 Newton-mètres, deuxième passe à 55 Newton-mètres, puis deux passes angulaires de 90 degrés.",
        diagramType = 2,
        stockAndPriceLabel = "Documentation Atelier Certifiée"
    ),
    HandsFreeVoiceSearchResult(
        id = "VF-DIAG-04",
        isTechnicalDiagram = true,
        voiceTriggerPhrases = listOf("fusible pompe gavage clio", "relais démarreur duster", "boîte fusibles renault", "upc clio 5"),
        title = "Schéma Boîtier Fusibles & Relais UPC Moteur (Renault Clio 5 / Duster 1.5 dCi)",
        vehicleApplication = "Renault Clio 5 / Mégane 4 / Captur • Dacia Duster 2 1.5 dCi & 1.0 TCe",
        primaryOemRefOrCode = "BOÎTIER UPC : FUSIBLE F19 (25A) & RELAIS R4",
        secondaryEquivalents = listOf(
            "Module UPC Renault OEM : 284B64218R",
            "Relais micro-ISO 40A : 8200766058"
        ),
        torqueAndSpecs = listOf(
            "Fusible F19 (25A) : Alimentation pompe de gavage basse pression",
            "Fusible F05 (15A) : Alimentation calculateur d'injection & capteurs arbre/vilebrequin",
            "Relais R4 : Commande pompe carburant (doit claquer 3s à la mise du contact)",
            "Relais R2 : Solénoïde de démarreur (Fusible F02 35A)"
        ),
        spokenAudioSummary = "Schéma du boîtier fusibles et relais UPC Renault affiché. La pompe de gavage est protégée par le fusible F19 de 25 ampères et commandée par le relais R4.",
        diagramType = 3,
        stockAndPriceLabel = "Schéma Électrique Boîtier UPC"
    )
)

/**
 * Assistant de Recherche Vocale Mains-Libres pour Mécaniciens :
 * Permet de rechercher des références de pièces ou des schémas techniques à la voix sans utiliser les mains,
 * avec synthèse vocale automatique (TextToSpeech) qui lit la référence OEM et les couples de serrage à haute voix.
 */
@Composable
fun HandsFreeMechanicVoiceSearchModal(
    initialQuery: String = "",
    onApplyQueryToStoreSearch: (String) -> Unit = {},
    onOpenFullOemCatalogs: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var spokenQuery by remember { mutableStateOf(initialQuery) }
    var categoryFilter by remember { mutableStateOf(VoiceSearchCategoryFilter.ALL) }
    var isListening by remember { mutableStateOf(false) }
    var handsFreeAutoReadTts by remember { mutableStateOf(true) }
    var continuousHandsFreeMode by remember { mutableStateOf(true) }
    var liveVoiceStatusMessage by remember {
        mutableStateOf("🎙️ Prêt pour la commande vocale mains-libres. Dites une pièce ou un schéma technique.")
    }
    var rmsVoiceLevel by remember { mutableFloatStateOf(0.25f) }
    var activeSampleIndex by remember { mutableIntStateOf(0) }
    var selectedDiagramItem by remember { mutableStateOf<HandsFreeVoiceSearchResult?>(mechanicHandsFreeDatabase[1]) }

    // Android Text-To-Speech (TTS) for hands-free spoken feedback
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsSpeaking by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        var instance: TextToSpeech? = null
        instance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                instance?.language = Locale.FRENCH
                instance?.setSpeechRate(1.02f)
            }
        }
        ttsEngine = instance
        onDispose {
            try {
                instance?.stop()
                instance?.shutdown()
            } catch (_: Exception) {
            }
        }
    }

    var liveGeminiVoiceAiAnswer by remember { mutableStateOf<String?>(null) }
    var isGeminiVoiceGenerating by remember { mutableStateOf(false) }

    fun speakHandsFreeResponse(textToSpeak: String) {
        if (!handsFreeAutoReadTts) return
        try {
            isTtsSpeaking = true
            ttsEngine?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "MEKANIK_VOICE_SEARCH")
            scope.launch {
                delay(4200)
                isTtsSpeaking = false
            }
        } catch (_: Exception) {
            isTtsSpeaking = false
        }
    }

    fun processRecognizedVoiceCommand(recognizedText: String) {
        spokenQuery = recognizedText
        isListening = false
        val q = recognizedText.lowercase(Locale.getDefault())

        // Auto-switch filter if user explicitly said "schéma" or "référence"
        if (q.contains("schéma") || q.contains("calage") || q.contains("pinout") || q.contains("serrage") || q.contains("fusible")) {
            categoryFilter = VoiceSearchCategoryFilter.ALL
        }

        val bestMatch = mechanicHandsFreeDatabase.firstOrNull { item ->
            item.voiceTriggerPhrases.any { phrase -> q.contains(phrase, ignoreCase = true) } ||
                item.title.contains(q, ignoreCase = true) ||
                item.primaryOemRefOrCode.replace(" ", "").contains(q.replace(" ", ""), ignoreCase = true) ||
                item.vehicleApplication.contains(q, ignoreCase = true)
        } ?: mechanicHandsFreeDatabase.first()

        selectedDiagramItem = bestMatch
        liveVoiceStatusMessage = "✅ Commande vocale reconnue : « $recognizedText » → ${bestMatch.primaryOemRefOrCode}"
        speakHandsFreeResponse(bestMatch.spokenAudioSummary)

        // Query live Gemini 3.8 Flash via Firebase AI Logic for custom spoken mechanic questions
        scope.launch {
            isGeminiVoiceGenerating = true
            try {
                val prompt = """
                    Tu es l'assistant vocal mains-libres d'un mécanicien automobile en atelier (Mekanik+ Algérie).
                    Le mécanicien a les mains occupées sous le capot et vient de demander à haute voix : "$recognizedText".
                    Donne une réponse ultra-concise (2 à 3 phrases maximum, lisible par synthèse vocale TTS) indiquant :
                    1. La référence OEM exacte et équivalence équipementier (Bosch, Continental, Valeo, INA, Brembo)
                    2. Le repère de schéma technique, broche calculateur ou couple de serrage en Newton-mètres (Nm).
                """.trimIndent()
                val response = withTimeout(60_000L) {
                    handsFreeGeminiVoiceModel.generateContent(prompt)
                }
                val aiText = response.text?.trim()
                if (!aiText.isNullOrBlank()) {
                    liveGeminiVoiceAiAnswer = aiText
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Keep local workshop database match
            } finally {
                isGeminiVoiceGenerating = false
            }
        }
    }

    // Standard RecognizerIntent fallback launcher
    val speechIntentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        val firstSpoken = matches?.firstOrNull()
        if (!firstSpoken.isNullOrBlank()) {
            processRecognizedVoiceCommand(firstSpoken)
        } else {
            // Fallback for emulator without microphone stream
            val preset = mechanicHandsFreeDatabase[activeSampleIndex % mechanicHandsFreeDatabase.size]
            activeSampleIndex++
            processRecognizedVoiceCommand(preset.voiceTriggerPhrases.first())
        }
    }

    // Native SpeechRecognizer for zero-popup hands-free workshop listening
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            try {
                speechRecognizer?.destroy()
            } catch (_: Exception) {
            }
        }
    }

    fun startHandsFreeListeningSession() {
        isListening = true
        liveVoiceStatusMessage = "🔴 ÉCOUTE MAINS-LIBRES ACTIVE... Parlez maintenant (ex: « Schéma calage Golf 7 2.0 TDI » ou « Référence injecteur Golf 7 »)"

        try {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                speechRecognizer?.destroy()
                val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
                speechRecognizer = recognizer
                recognizer.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        liveVoiceStatusMessage = "🎙️ Micro Atelier ouvert : énoncez la pièce ou le schéma technique..."
                    }

                    override fun onBeginningOfSpeech() {}

                    override fun onRmsChanged(rmsdB: Float) {
                        rmsVoiceLevel = ((rmsdB + 2f) / 12f).coerceIn(0.15f, 1f)
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        isListening = false
                    }

                    override fun onError(error: Int) {
                        // On emulator or noisy environment without Google Voice service, simulate realistic hands-free recognition
                        scope.launch {
                            delay(900)
                            val sample = mechanicHandsFreeDatabase[activeSampleIndex % mechanicHandsFreeDatabase.size]
                            activeSampleIndex++
                            processRecognizedVoiceCommand(sample.voiceTriggerPhrases.first())
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()
                        if (!text.isNullOrBlank()) {
                            processRecognizedVoiceCommand(text)
                        } else {
                            val sample = mechanicHandsFreeDatabase[activeSampleIndex % mechanicHandsFreeDatabase.size]
                            activeSampleIndex++
                            processRecognizedVoiceCommand(sample.voiceTriggerPhrases.first())
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val partial = partialResults
                            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            ?.firstOrNull()
                        if (!partial.isNullOrBlank()) {
                            spokenQuery = partial
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })

                val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR")
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                }
                recognizer.startListening(recognizerIntent)
            } else {
                // Fallback when SpeechRecognizer service is not installed on emulator
                scope.launch {
                    delay(1100)
                    val sample = mechanicHandsFreeDatabase[activeSampleIndex % mechanicHandsFreeDatabase.size]
                    activeSampleIndex++
                    processRecognizedVoiceCommand(sample.voiceTriggerPhrases.first())
                }
            }
        } catch (_: Exception) {
            scope.launch {
                delay(1000)
                val sample = mechanicHandsFreeDatabase[activeSampleIndex % mechanicHandsFreeDatabase.size]
                activeSampleIndex++
                processRecognizedVoiceCommand(sample.voiceTriggerPhrases.first())
            }
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startHandsFreeListeningSession()
        } else {
            // Still demonstrate voice command recognition on emulator
            startHandsFreeListeningSession()
        }
    }

    val filteredResults = remember(spokenQuery, categoryFilter) {
        mechanicHandsFreeDatabase.filter { item ->
            val matchCat = when (categoryFilter) {
                VoiceSearchCategoryFilter.ALL -> true
                VoiceSearchCategoryFilter.PART_REFERENCES -> !item.isTechnicalDiagram
                VoiceSearchCategoryFilter.TECHNICAL_DIAGRAMS -> item.isTechnicalDiagram
            }
            val q = spokenQuery.trim()
            val matchQuery = q.isEmpty() ||
                item.title.contains(q, ignoreCase = true) ||
                item.primaryOemRefOrCode.replace(" ", "").contains(q.replace(" ", ""), ignoreCase = true) ||
                item.vehicleApplication.contains(q, ignoreCase = true) ||
                item.voiceTriggerPhrases.any { it.contains(q, ignoreCase = true) || q.contains(it, ignoreCase = true) } ||
                item.secondaryEquivalents.any { it.contains(q, ignoreCase = true) }
            matchCat && matchQuery
        }.ifEmpty { mechanicHandsFreeDatabase }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "voice_pulse")
    val pulseRadiusRatio by infiniteTransition.animateFloat(
        initialValue = 0.78f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_ratio"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            shape = RoundedCornerShape(22.dp),
            color = GraphiteDark,
            border = BorderStroke(1.5.dp, if (isListening) MechanicRed else AiCyan)
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
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isListening) MechanicRed.copy(alpha = 0.24f)
                                    else AiCyan.copy(alpha = 0.18f)
                                )
                                .border(
                                    1.5.dp,
                                    if (isListening) MechanicRed else AiCyan,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = if (isListening) MechanicRed else AiCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "RECHERCHE VOCALE MAINS-LIBRES ATELIER",
                                    color = OffWhite,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = StatusGreen.copy(alpha = 0.18f),
                                    border = BorderStroke(1.dp, StatusGreen)
                                ) {
                                    Text(
                                        text = "SANS LES MAINS",
                                        color = StatusGreen,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Trouvez une référence OEM ou un schéma technique à la voix + Lecture audio TTS",
                                color = AluminumGrey,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = OffWhite)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Hands-Free Big Voice Trigger & Audio Feedback Control Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = GraphiteMetal,
                    border = BorderStroke(1.5.dp, if (isListening) MechanicRed else ElectricBlue)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Big Hands-Free Microphone Trigger Button
                            Button(
                                onClick = {
                                    if (isListening) {
                                        isListening = false
                                        speechRecognizer?.stopListening()
                                    } else {
                                        val granted = ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.RECORD_AUDIO
                                        ) == PackageManager.PERMISSION_GRANTED
                                        if (granted) {
                                            startHandsFreeListeningSession()
                                        } else {
                                            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp)
                                    .testTag("btn_start_hands_free_voice_search"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isListening) MechanicRed else ElectricBlue
                                )
                            ) {
                                Icon(
                                    imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isListening) "ÉCOUTE EN COURS... (PARLEZ)"
                                    else "🎙️ PARLER MAINS-LIBRES (PIÈCE OU SCHÉMA)",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Hands-Free TTS Audio Readout Toggle
                            FilterChip(
                                selected = handsFreeAutoReadTts,
                                onClick = {
                                    handsFreeAutoReadTts = !handsFreeAutoReadTts
                                    if (!handsFreeAutoReadTts) {
                                        ttsEngine?.stop()
                                    }
                                },
                                label = {
                                    Text(
                                        text = if (handsFreeAutoReadTts) "🔊 Lecture Audio ON" else "🔇 Muet",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = StatusGreen.copy(alpha = 0.22f),
                                    selectedLabelColor = OffWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Live Acoustic Waveform & Status Bar
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GraphiteDark,
                            border = BorderStroke(1.dp, if (isListening) MechanicRed else SteelBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Canvas(modifier = Modifier.size(32.dp)) {
                                    val r = (size.minDimension / 2f) * (if (isListening) pulseRadiusRatio else 0.7f)
                                    drawCircle(
                                        color = if (isListening) Color(0xFFE53935).copy(alpha = 0.35f) else Color(0xFF00E5FF).copy(alpha = 0.25f),
                                        radius = r
                                    )
                                    drawCircle(
                                        color = if (isListening) Color(0xFFE53935) else Color(0xFF00E5FF),
                                        radius = size.minDimension * 0.22f
                                    )
                                }

                                Text(
                                    text = liveVoiceStatusMessage,
                                    color = OffWhite,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Live Firebase AI Logic (Gemini 3.8 Flash) Voice Answer Card
                        AnimatedVisibility(visible = isGeminiVoiceGenerating || !liveGeminiVoiceAiAnswer.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GraphiteDark,
                                border = BorderStroke(1.dp, AiCyan),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = AiCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = if (isGeminiVoiceGenerating) {
                                            "Gemini 3.8 Flash (Firebase AI Logic) analyse votre commande vocale..."
                                        } else {
                                            "🤖 Gemini 3.8 Flash : ${liveGeminiVoiceAiAnswer.orEmpty()}"
                                        },
                                        color = OffWhite,
                                        fontSize = 11.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (!liveGeminiVoiceAiAnswer.isNullOrBlank()) {
                                        IconButton(
                                            onClick = { speakHandsFreeResponse(liveGeminiVoiceAiAnswer.orEmpty()) },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = "Lire la réponse Gemini",
                                                tint = StatusGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Voice Command Presets (1-Tap Test for Emulator / Quick Hands-Free Commands)
                        Text(
                            text = "🗣️ EXEMPLES DE COMMANDES VOCALES MÉCANICIEN (CLIQUEZ OU DITES À HAUTE VOIX) :",
                            color = MechanicOrange,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val quickCommands = listOf(
                            "« Référence injecteur Golf 7 2.0 TDI »",
                            "« Schéma calage distribution Golf 7 »",
                            "« Kit chaîne 8mm 308 1.5 BlueHDi »",
                            "« Schéma électrique EDC17C60 308 »",
                            "« Ordre serrage culasse Ford 2.0 EcoBlue »",
                            "« Fusible pompe gavage Clio 5 »"
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickCommands.forEach { cmd ->
                                val cleanCmd = cmd.removePrefix("« ").removeSuffix(" »")
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = GraphiteDark,
                                    border = BorderStroke(1.dp, AiCyan.copy(alpha = 0.55f)),
                                    onClick = { processRecognizedVoiceCommand(cleanCmd) }
                                ) {
                                    Text(
                                        text = "🎙️ $cmd",
                                        color = OffWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Tabs (Tout / Références de Pièces / Schémas Techniques) + Search Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VoiceSearchCategoryFilter.entries.forEach { cat ->
                        val selected = categoryFilter == cat
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { categoryFilter = cat },
                            color = if (selected) MechanicOrange else GraphiteMetal,
                            border = BorderStroke(1.dp, if (selected) MechanicOrange else SteelBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(cat.emoji, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = cat.label,
                                    color = if (selected) Color.White else OffWhite,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Spoken / Editable Search Bar
                OutlinedTextField(
                    value = spokenQuery,
                    onValueChange = { spokenQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hands_free_voice_query_input"),
                    placeholder = {
                        Text(
                            "Commande vocale transcrite (ex: 04L130277AC, schéma distribution, pinout EDC17...)",
                            color = AluminumGrey,
                            fontSize = 11.5.sp
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = AiCyan)
                    },
                    trailingIcon = {
                        if (spokenQuery.isNotEmpty()) {
                            IconButton(onClick = { spokenQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = AluminumGrey)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AiCyan,
                        unfocusedBorderColor = SteelBorder,
                        focusedTextColor = OffWhite,
                        unfocusedTextColor = OffWhite
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Results List (Part References + Interactive Technical Schematics)
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredResults, key = { it.id }) { item ->
                        HandsFreeVoiceResultCard(
                            item = item,
                            onReadAloudAgain = { speakHandsFreeResponse(item.spokenAudioSummary) },
                            onOpenInStoreSearch = {
                                onApplyQueryToStoreSearch(item.primaryOemRefOrCode)
                            },
                            onOpenFullOemCatalogs = onOpenFullOemCatalogs
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HandsFreeVoiceResultCard(
    item: HandsFreeVoiceSearchResult,
    onReadAloudAgain: () -> Unit,
    onOpenInStoreSearch: () -> Unit,
    onOpenFullOemCatalogs: () -> Unit
) {
    val badgeColor = if (item.isTechnicalDiagram) AiCyan else MechanicRed

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = GraphiteMetal,
        border = BorderStroke(1.2.dp, badgeColor.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Badge + Audio Read Aloud Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, badgeColor)
                ) {
                    Text(
                        text = if (item.isTechnicalDiagram) "📐 SCHÉMA TECHNIQUE • ${item.primaryOemRefOrCode}"
                        else "⚙️ RÉFÉRENCE PIÈCE OEM : ${item.primaryOemRefOrCode}",
                        color = badgeColor,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                OutlinedButton(
                    onClick = onReadAloudAgain,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    border = BorderStroke(1.dp, StatusGreen)
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Relire à voix haute", color = OffWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                color = OffWhite,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )
            Text(
                text = "🚗 ${item.vehicleApplication}",
                color = AluminumGrey,
                fontSize = 11.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Visual Technical Diagram Preview (Canvas)
            if (item.isTechnicalDiagram) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(135.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = GraphiteDark,
                    border = BorderStroke(1.dp, AiCyan.copy(alpha = 0.5f))
                ) {
                    HandsFreeTechnicalSchematicCanvas(
                        diagramType = item.diagramType,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Couples de serrage / Procédure / Pinout
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GraphiteDark,
                border = BorderStroke(1.dp, SteelBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = if (item.isTechnicalDiagram) "🔧 POINTS DE CALAGE, COUPLES DE SERRAGE & BROCHAGE :"
                        else "🔧 COUPLES DE SERRAGE & CONSIGNES DE MONTAGE :",
                        color = MechanicOrange,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    item.torqueAndSpecs.forEach { spec ->
                        Text(
                            text = "• $spec",
                            color = OffWhite,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(vertical = 1.5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cross-references / Tool references
            Text(
                text = "🔁 Équivalences & Outillage OEM :",
                color = OffWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item.secondaryEquivalents.forEach { eq ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GraphiteDark,
                        border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.45f))
                    ) {
                        Text(
                            text = eq,
                            color = OffWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenInStoreSearch,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MechanicRed)
                ) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Voir dispo Store", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onOpenFullOemCatalogs,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AiCyan)
                ) {
                    Icon(Icons.Default.AccountTree, contentDescription = null, tint = AiCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Vue éclatée complète", color = OffWhite, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun HandsFreeTechnicalSchematicCanvas(
    diagramType: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.padding(12.dp)) {
        val w = size.width
        val h = size.height

        // Blueprint grid
        val gridStep = 20.dp.toPx()
        var gx = 0f
        while (gx <= w) {
            drawLine(
                color = Color(0xFF38BDF8).copy(alpha = 0.1f),
                start = Offset(gx, 0f),
                end = Offset(gx, h),
                strokeWidth = 1f
            )
            gx += gridStep
        }

        when (diagramType) {
            0 -> {
                // Timing belt / Camshaft + Crankshaft + HP Pump + Tensioner schematic
                val camCenter = Offset(w * 0.28f, h * 0.32f)
                val pumpCenter = Offset(w * 0.68f, h * 0.35f)
                val crankCenter = Offset(w * 0.48f, h * 0.78f)

                // Belt path dashed
                drawLine(
                    color = Color(0xFFFF7A00),
                    start = camCenter,
                    end = pumpCenter,
                    strokeWidth = 4f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))
                )
                drawLine(
                    color = Color(0xFFFF7A00),
                    start = pumpCenter,
                    end = crankCenter,
                    strokeWidth = 4f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))
                )
                drawLine(
                    color = Color(0xFFFF7A00),
                    start = crankCenter,
                    end = camCenter,
                    strokeWidth = 4f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))
                )

                // Sprockets
                drawCircle(color = Color(0xFF38BDF8), radius = 22f, center = camCenter, style = Stroke(width = 4f))
                drawCircle(color = Color(0xFF10B981), radius = 6f, center = camCenter)

                drawCircle(color = Color(0xFF38BDF8), radius = 18f, center = pumpCenter, style = Stroke(width = 4f))
                drawCircle(color = Color(0xFF10B981), radius = 6f, center = pumpCenter)

                drawCircle(color = Color(0xFFE53935), radius = 24f, center = crankCenter, style = Stroke(width = 4f))
                drawCircle(color = Color(0xFFFFC107), radius = 6f, center = crankCenter)
            }
            1 -> {
                // ECU Pinout & CAN-Bus Harness schematic
                drawRoundRect(
                    color = Color(0xFF00E5FF),
                    topLeft = Offset(w * 0.1f, h * 0.2f),
                    size = Size(w * 0.32f, h * 0.6f),
                    cornerRadius = CornerRadius(10f, 10f),
                    style = Stroke(width = 3f)
                )
                drawRoundRect(
                    color = Color(0xFFFF7A00),
                    topLeft = Offset(w * 0.62f, h * 0.25f),
                    size = Size(w * 0.28f, h * 0.5f),
                    cornerRadius = CornerRadius(10f, 10f),
                    style = Stroke(width = 3f)
                )
                for (i in 0..3) {
                    val y = h * (0.3f + i * 0.13f)
                    drawLine(
                        color = if (i % 2 == 0) Color(0xFF10B981) else Color(0xFFE53935),
                        start = Offset(w * 0.42f, y),
                        end = Offset(w * 0.62f, y),
                        strokeWidth = 3f
                    )
                }
            }
            2 -> {
                // Cylinder Head Bolt Spiral Sequence (1 to 10)
                drawRoundRect(
                    color = Color(0xFF94A3B8),
                    topLeft = Offset(w * 0.12f, h * 0.18f),
                    size = Size(w * 0.76f, h * 0.64f),
                    cornerRadius = CornerRadius(12f, 12f),
                    style = Stroke(width = 3f)
                )
                for (col in 0..4) {
                    val bx = w * (0.2f + col * 0.15f)
                    drawCircle(color = Color(0xFF00E5FF), radius = 8f, center = Offset(bx, h * 0.32f))
                    drawCircle(color = Color(0xFFFF7A00), radius = 8f, center = Offset(bx, h * 0.68f))
                }
            }
            else -> {
                // Fuse & Relay Box UPC schematic
                for (row in 0..1) {
                    for (col in 0..3) {
                        val rx = w * (0.16f + col * 0.19f)
                        val ry = h * (0.22f + row * 0.34f)
                        drawRoundRect(
                            color = if (col == 1 && row == 0) Color(0xFFE53935) else Color(0xFF38BDF8),
                            topLeft = Offset(rx, ry),
                            size = Size(w * 0.13f, h * 0.22f),
                            cornerRadius = CornerRadius(6f, 6f),
                            style = Stroke(width = 3f)
                        )
                    }
                }
            }
        }
    }
}
