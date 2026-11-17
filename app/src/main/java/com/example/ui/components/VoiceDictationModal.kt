package com.example.ui.components

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AiTranslatedRepairProblem
import com.example.data.model.VoiceFaultPresetsProvider
import com.example.data.model.VoiceRepairLanguage
import com.example.data.repository.AiDiagnosticService
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun VoiceDictationModal(
  initialText: String = "",
  onApplyDictation: (String) -> Unit,
  onDismiss: () -> Unit,
  onApplyTranslatedProblem: ((AiTranslatedRepairProblem) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var isRecording by remember { mutableStateOf(true) }
  var selectedLanguage by remember { mutableStateOf(VoiceRepairLanguage.FRANCAIS) }
  var transcribedText by remember { mutableStateOf(initialText.ifBlank { VoiceRepairLanguage.FRANCAIS.sampleSpeech }) }
  var isTranslatingAi by remember { mutableStateOf(false) }
  var aiTranslationResult by remember { mutableStateOf<AiTranslatedRepairProblem?>(null) }

  val presets = remember { VoiceFaultPresetsProvider.sampleVoicePresets }
  val coroutineScope = rememberCoroutineScope()
  val aiService = remember { AiDiagnosticService() }

  // Lanceur natif Android SpeechRecognizer multilingue (FR, AR, EN, ES, ZH)
  val speechRecognizerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
      val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
      if (!spoken.isNullOrBlank()) {
        transcribedText = spoken
        coroutineScope.launch {
          isTranslatingAi = true
          aiTranslationResult = aiService.translateAndAnalyzeVoiceRepairReport(spoken, selectedLanguage)
          isTranslatingAi = false
        }
      }
    }
  }

  val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { granted ->
    isRecording = true
    if (granted) {
      try {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
          putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
          putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage.localeCode)
          putExtra(RecognizerIntent.EXTRA_PROMPT, "Dictez la panne (${selectedLanguage.labelFr} / ${selectedLanguage.nativeLabel})...")
        }
        speechRecognizerLauncher.launch(intent)
      } catch (_: Exception) {
        // Sur émulateur sans moteur Google Speech installé, utilise l'échantillon vocal de la langue choisie
        transcribedText = selectedLanguage.sampleSpeech
      }
    } else {
      transcribedText = selectedLanguage.sampleSpeech
    }
  }

  // Déclenche une première traduction IA dès l'ouverture ou le changement de langue
  LaunchedEffect(selectedLanguage) {
    if (transcribedText.isNotBlank()) {
      isTranslatingAi = true
      aiTranslationResult = aiService.translateAndAnalyzeVoiceRepairReport(transcribedText, selectedLanguage)
      isTranslatingAi = false
    }
  }

  // Audio wave pulsing animation
  val infiniteTransition = rememberInfiniteTransition(label = "audio_wave_pulse")
  val wavePulse by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.32f,
    animationSpec = infiniteRepeatable(
      animation = tween(650, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "wave_scale"
  )

  val waveAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0.85f,
    animationSpec = infiniteRepeatable(
      animation = tween(650, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "wave_alpha"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.90f)
        .clip(RoundedCornerShape(20.dp))
        .border(1.5.dp, if (isRecording) PerformanceRedGlow else MetalBorder, RoundedCornerShape(20.dp))
        .testTag("voice_dictation_dialog"),
      color = GraphiteDark,
      tonalElevation = 8.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (isRecording) PerformanceRed.copy(alpha = 0.2f) else MetalCard),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Translate,
                contentDescription = null,
                tint = if (isRecording) PerformanceRedGlow else AluminumLight,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "COMMANDE VOCALE & TRADUCTION IA (5 LANGUES)",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Black,
                  color = PureWhite,
                  letterSpacing = 0.5.sp
                )
              )
              Text(
                text = "Français • Arabe • Anglais • Espagnol • Chinois → Ordre de Réparation",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = TechCyan,
                  fontSize = 10.5.sp,
                  fontWeight = FontWeight.Bold
                )
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 1. Sélecteur des 5 Langues (Français, Arabe, Anglais, Espagnol, Chinois)
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MetalSurface,
          border = BorderStroke(1.dp, MetalBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = "🌍 1. CHOISIR LA LANGUE DE LA COMMANDE VOCALE (TRADUCTION IA AUTOMATIQUE)",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MechanicalOrange,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp
              )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
              VoiceRepairLanguage.entries.forEach { lang ->
                val isSelected = selectedLanguage == lang
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (isSelected) PerformanceRed else MetalCard,
                  border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
                  onClick = {
                    selectedLanguage = lang
                    transcribedText = lang.sampleSpeech
                  },
                  modifier = Modifier
                    .weight(1f)
                    .testTag("voice_lang_${lang.name.lowercase()}")
                ) {
                  Column(
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Text(text = lang.flagEmoji, fontSize = 15.sp)
                    Text(
                      text = lang.labelFr,
                      color = if (isSelected) Color.White else OffWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.sp,
                      maxLines = 1
                    )
                    Text(
                      text = lang.nativeLabel,
                      color = if (isSelected) Color.White.copy(alpha = 0.85f) else AluminumMuted,
                      fontSize = 8.sp,
                      maxLines = 1
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Center Microphone Animation Visualizer + Real Android Mic Launcher
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier.size(84.dp),
            contentAlignment = Alignment.Center
          ) {
            if (isRecording) {
              Box(
                modifier = Modifier
                  .size(80.dp)
                  .scale(wavePulse)
                  .clip(CircleShape)
                  .background(PerformanceRed.copy(alpha = 0.22f * waveAlpha))
              )
              Box(
                modifier = Modifier
                  .size(64.dp)
                  .scale(wavePulse * 0.9f)
                  .clip(CircleShape)
                  .background(MechanicalOrange.copy(alpha = 0.35f * waveAlpha))
              )
            }

            Box(
              modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                  brush = Brush.linearGradient(
                    colors = if (isRecording) listOf(PerformanceRed, MechanicalOrange) else listOf(MetalCard, GraphiteBlack)
                  )
                )
                .clickable {
                  recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
                .testTag("btn_toggle_recording"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                if (isRecording) Icons.Default.Mic else Icons.Default.MicOff,
                contentDescription = "Microphone",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
              )
            }
          }

          Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "🎤 Dictée vocale (${selectedLanguage.flagEmoji} ${selectedLanguage.labelFr}) ou Saisie Manuelle",
              color = PureWhite,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp
            )
            Text(
              text = "Appuyez sur le micro pour parler en ${selectedLanguage.labelFr} (${selectedLanguage.nativeLabel}). L'IA MekanikAI traduit automatiquement tous les problèmes en français technique.",
              color = AluminumLight,
              fontSize = 10.5.sp,
              lineHeight = 14.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = TechCyan.copy(alpha = 0.18f),
                border = BorderStroke(1.dp, TechCyan),
                onClick = {
                  transcribedText = selectedLanguage.sampleSpeech
                  coroutineScope.launch {
                    isTranslatingAi = true
                    aiTranslationResult = aiService.translateAndAnalyzeVoiceRepairReport(transcribedText, selectedLanguage)
                    isTranslatingAi = false
                  }
                },
                modifier = Modifier.testTag("btn_simulate_multilingual_voice")
              ) {
                Text(
                  text = "🗣️ Tester Voix ${selectedLanguage.flagEmoji} ${selectedLanguage.labelFr}",
                  color = TechCyan,
                  fontWeight = FontWeight.Black,
                  fontSize = 10.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = MechanicalOrange.copy(alpha = 0.18f),
                border = BorderStroke(1.dp, MechanicalOrange),
                onClick = {
                  coroutineScope.launch {
                    isTranslatingAi = true
                    aiTranslationResult = aiService.translateAndAnalyzeVoiceRepairReport(transcribedText, selectedLanguage)
                    isTranslatingAi = false
                  }
                },
                modifier = Modifier.testTag("btn_run_ai_voice_translation")
              ) {
                Text(
                  text = "🧠 Traduire par IA",
                  color = MechanicalOrange,
                  fontWeight = FontWeight.Black,
                  fontSize = 10.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Live Transcription / Manual Input Box
        OutlinedTextField(
          value = transcribedText,
          onValueChange = { transcribedText = it },
          label = { Text("Commande vocale ou saisie manuelle (${selectedLanguage.flagEmoji} ${selectedLanguage.labelFr})") },
          placeholder = { Text("Dictez ou écrivez les problèmes en Français, Arabe, Anglais, Espagnol ou Chinois...") },
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 85.dp, max = 125.dp)
            .testTag("input_transcription_text"),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PerformanceRedGlow,
            unfocusedBorderColor = MetalBorder,
            focusedContainerColor = GraphiteBlack,
            unfocusedContainerColor = GraphiteBlack,
            focusedTextColor = PureWhite,
            unfocusedTextColor = PureWhite
          ),
          trailingIcon = {
            if (transcribedText.isNotEmpty()) {
              IconButton(onClick = { transcribedText = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = AluminumMuted, modifier = Modifier.size(18.dp))
              }
            }
          }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 4. Résultat de la Traduction Automatique IA MekanikAI
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = adaptiveBannerSurface(0xFF0E212E, 0xFFF0F9FF),
          border = BorderStroke(1.2.dp, TechCyan),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_voice_translation_result_card")
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "TRADUCTION AUTOMATIQUE IA (${selectedLanguage.flagEmoji} ${selectedLanguage.labelFr.uppercase()} → FRANÇAIS TECHNIQUE)",
                  color = TechCyan,
                  fontWeight = FontWeight.Black,
                  fontSize = 10.sp
                )
              }
              if (isTranslatingAi) {
                CircularProgressIndicator(
                  color = TechCyan,
                  strokeWidth = 2.dp,
                  modifier = Modifier.size(14.dp)
                )
              }
            }

            val translated = aiTranslationResult
            if (translated != null) {
              Text(
                text = translated.translatedFrenchTechnicalSummary,
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                lineHeight = 16.sp
              )
              if (translated.suggestedRepairOperations.isNotEmpty()) {
                Text(
                  text = "Opérations générées pour l'Ordre de Réparation :",
                  color = MechanicalOrange,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
                translated.suggestedRepairOperations.forEach { op ->
                  Text(
                    text = "✓ $op",
                    color = ElectricGreen,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            } else {
              Text(
                text = "Dictez un problème en Français, Arabe, Anglais, Espagnol ou Chinois pour générer la traduction technique.",
                color = AluminumLight,
                fontSize = 10.5.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Preset mechanical fault chips for fast intake
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "Pannes types rapides (Atelier) :",
            style = MaterialTheme.typography.labelSmall.copy(
              color = AluminumGray,
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp
            )
          )
          Spacer(modifier = Modifier.height(6.dp))

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(presets) { preset ->
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MetalCard,
                border = BorderStroke(1.dp, MetalBorder),
                modifier = Modifier.clickable {
                  transcribedText = preset.transcribedText
                  coroutineScope.launch {
                    isTranslatingAi = true
                    aiTranslationResult = aiService.translateAndAnalyzeVoiceRepairReport(transcribedText, selectedLanguage)
                    isTranslatingAi = false
                  }
                }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.AutoFixHigh,
                    contentDescription = null,
                    tint = TechCyan,
                    modifier = Modifier.size(13.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Text(
                      text = preset.title,
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp
                      )
                    )
                    Text(
                      text = preset.category,
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = MechanicalOrange,
                        fontSize = 9.sp
                      )
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          MekanikSecondaryButton(
            text = "ANNULER",
            onClick = onDismiss,
            modifier = Modifier.weight(1f)
          )

          MekanikPrimaryButton(
            text = "INJECTER TRADUCTION IA DANS L'OR",
            onClick = {
              val translated = aiTranslationResult
              val finalToInject = if (translated != null) {
                buildString {
                  append("[${translated.sourceLanguage.flagEmoji} ${translated.sourceLanguage.labelFr}] ${translated.originalSpeechText}\n")
                  append("➔ ${translated.translatedFrenchTechnicalSummary}")
                  if (translated.suggestedRepairOperations.isNotEmpty()) {
                    append("\n• Actions OR : ${translated.suggestedRepairOperations.joinToString(" | ")}")
                  }
                }
              } else {
                transcribedText
              }
              onApplyTranslatedProblem?.invoke(
                translated ?: AiTranslatedRepairProblem(
                  sourceLanguage = selectedLanguage,
                  originalSpeechText = transcribedText,
                  translatedFrenchTechnicalSummary = transcribedText
                )
              )
              onApplyDictation(finalToInject)
              onDismiss()
            },
            icon = Icons.Default.Check,
            modifier = Modifier
              .weight(1.6f)
              .testTag("btn_confirm_dictation")
          )
        }
      }
    }
  }
}
