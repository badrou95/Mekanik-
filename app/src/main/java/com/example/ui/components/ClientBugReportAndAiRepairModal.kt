package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.data.repository.MekanikErrorLogger
import com.example.ui.theme.*

@Composable
fun ClientBugReportAndAiRepairModal(
  errorLoggerState: ErrorLoggerUiState,
  currentUserEmail: String,
  onSubmitBugAndAutoRepair: (
    moduleSource: String,
    bugTitle: String,
    bugDescription: String,
    severity: SystemErrorSeverity
  ) -> Unit,
  onRepairAllOpenIncidents: () -> Unit,
  onOpenSuperAdminErrorLogger: () -> Unit,
  onDismiss: () -> Unit
) {
  var selectedModule by remember { mutableStateOf("Stock & Inventaire • Scanner OCR Bon d'Achat") }
  var bugTitle by remember { mutableStateOf("") }
  var bugDescription by remember { mutableStateOf("") }
  var selectedSeverity by remember { mutableStateOf(SystemErrorSeverity.HIGH) }
  var hasJustSubmitted by remember { mutableStateOf(false) }

  val modules = remember {
    listOf(
      "Stock & Inventaire • Scanner OCR Bon d'Achat",
      "Recherche VIN • Cascade 11 APIs",
      "MekanikAI Chatbot & Diagnostic",
      "Catalogue Pièces & Équivalences OEM",
      "Atelier & Devis Facturation DZD",
      "Espace Vendeur & Synchronisation Firebase"
    )
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.90f)
        .testTag("client_bug_ai_repair_modal"),
      shape = RoundedCornerShape(22.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, TechCyan)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF0A2432), GraphiteBlack, Color(0xFF1F1028))
              )
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(TechCyan.copy(alpha = 0.2f))
                .border(1.dp, TechCyan, RoundedCornerShape(12.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.AutoFixHigh,
                contentDescription = null,
                tint = TechCyan,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "🤖 MEKANIK-AI GUARDIAN • AUTO-RÉPARATION 24/7",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 13.5.sp
              )
              Text(
                text = "Réparation automatique des plantages, bugs & rapports clients en direct",
                color = TechCyan,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("btn_close_ai_repair_modal")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = OffWhite)
          }
        }

        HorizontalDivider(color = MetalBorder)

        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // 1. Statut du Gardien IA en direct
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(
                1.dp,
                if (errorLoggerState.isAiRepairInProgress) MechanicalOrange else ElectricGreen
              )
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      if (errorLoggerState.isAiRepairInProgress) Icons.Default.Sync else Icons.Default.Verified,
                      contentDescription = null,
                      tint = if (errorLoggerState.isAiRepairInProgress) MechanicalOrange else ElectricGreen,
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (errorLoggerState.isAiRepairInProgress) {
                        "AUTO-RÉPARATION IA EN COURS..."
                      } else {
                        "BOUCLIER D'AUTO-GUÉRISON IA ACTIF (24H/24)"
                      },
                      color = if (errorLoggerState.isAiRepairInProgress) MechanicalOrange else ElectricGreen,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.5.sp
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ElectricGreen.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, ElectricGreen)
                  ) {
                    Text(
                      text = "${errorLoggerState.totalAiAutoRepairsCount} RÉPARÉS PAR IA",
                      color = ElectricGreen,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.5.sp,
                      modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                  }
                }

                Text(
                  text = errorLoggerState.activeAiRepairStepText,
                  color = PureWhite,
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace
                )

                if (errorLoggerState.isAiRepairInProgress) {
                  LinearProgressIndicator(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(5.dp)
                      .clip(RoundedCornerShape(4.dp)),
                    color = TechCyan,
                    trackColor = GraphiteBlack
                  )
                }

                if (errorLoggerState.unresolvedCount > 0) {
                  Button(
                    onClick = onRepairAllOpenIncidents,
                    colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("btn_modal_repair_all_open")
                  ) {
                    Icon(Icons.Default.BuildCircle, contentDescription = null, tint = PureWhite, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "⚡ LANCER L'AUTO-RÉPARATION IA SUR LES ${errorLoggerState.unresolvedCount} INCIDENT(S) OUVERTS",
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 10.5.sp
                    )
                  }
                }
              }
            }
          }

          // 2. Dernier rapport de réparation IA exécuté (si disponible)
          errorLoggerState.latestAiRepairReport?.let { report ->
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1D1A)),
                border = BorderStroke(1.2.dp, ElectricGreen.copy(alpha = 0.8f))
              ) {
                Column(
                  modifier = Modifier.padding(14.dp),
                  verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "✅ DERNIER HOTFIX IA APPLIQUÉ : ${report.hotfixId}",
                      color = ElectricGreen,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.5.sp
                    )
                    Text(
                      text = "Confiance ${report.confidenceScorePercent}% • ${report.repairedAtFormatted}",
                      color = TechCyan,
                      fontSize = 10.sp,
                      fontFamily = FontFamily.Monospace
                    )
                  }

                  Text(
                    text = "Diagnostic IA : ${report.rootCauseAnalysis}",
                    color = OffWhite,
                    fontSize = 11.sp
                  )

                  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    report.executedActions.forEach { action ->
                      Text(
                        text = "✔ $action",
                        color = ElectricGreen,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold
                      )
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF070B0E),
                    border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Text(
                      text = report.generatedPatchSnippet,
                      color = TechCyan,
                      fontSize = 10.sp,
                      fontFamily = FontFamily.Monospace,
                      modifier = Modifier.padding(8.dp)
                    )
                  }
                }
              }
            }
          }

          // 3. Scénarios rapides de rapport client en 1 clic
          item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                text = "⚡ EXEMPLES DE RAPPORTS CLIENTS (CLIQUEZ POUR PRÉ-REMPLIR & TESTER L'IA)",
                color = MechanicalOrange,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
              )
              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(MekanikErrorLogger.clientBugPresets, key = { it.id }) { preset ->
                  Card(
                    modifier = Modifier
                      .width(245.dp)
                      .clickable {
                        selectedModule = preset.moduleSource
                        bugTitle = preset.title
                        bugDescription = preset.defaultDescription
                        selectedSeverity = preset.suggestedSeverity
                        hasJustSubmitted = false
                      }
                      .testTag("preset_client_bug_${preset.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MetalCard),
                    border = BorderStroke(1.dp, MetalBorder)
                  ) {
                    Column(
                      modifier = Modifier.padding(10.dp),
                      verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Text(
                        text = preset.moduleSource,
                        color = TechCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        maxLines = 1
                      )
                      Text(
                        text = preset.title,
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        maxLines = 2
                      )
                      Text(
                        text = "Cliquer pour tester la réparation IA →",
                        color = MechanicalOrange,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.SemiBold
                      )
                    }
                  }
                }
              }
            }
          }

          // 4. Formulaire d'envoi de rapport de bug client ou d'anomalie
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Text(
                  text = "📨 SIGNALER UN BUG OU UN PLANTAGE À L'IA (PRISE EN CHARGE IMMÉDIATE)",
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp
                )

                Text(
                  text = "Module concerné :",
                  color = AluminumLight,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  items(modules) { mod ->
                    FilterChip(
                      selected = selectedModule == mod,
                      onClick = { selectedModule = mod },
                      label = { Text(mod, fontSize = 10.sp) }
                    )
                  }
                }

                OutlinedTextField(
                  value = bugTitle,
                  onValueChange = { bugTitle = it },
                  label = { Text("Titre du bug ou plantage constaté", fontSize = 11.sp) },
                  placeholder = { Text("Ex: Blocage lors de l'impression étiquette stock...", fontSize = 11.sp) },
                  singleLine = true,
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_client_bug_title"),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechCyan,
                    unfocusedBorderColor = MetalBorder,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                  )
                )

                OutlinedTextField(
                  value = bugDescription,
                  onValueChange = { bugDescription = it },
                  label = { Text("Description détaillée du problème rencontré par le client", fontSize = 11.sp) },
                  placeholder = {
                    Text(
                      "Décrivez l'action effectuée. MekanikAI Guardian va analyser le module et appliquer un correctif automatique...",
                      fontSize = 11.sp
                    )
                  },
                  minLines = 3,
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_client_bug_description"),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechCyan,
                    unfocusedBorderColor = MetalBorder,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                  )
                )

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Urgence :",
                    color = AluminumLight,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold
                  )
                  SystemErrorSeverity.entries.forEach { sev ->
                    FilterChip(
                      selected = selectedSeverity == sev,
                      onClick = { selectedSeverity = sev },
                      label = { Text(sev.badgeText, fontSize = 9.5.sp) }
                    )
                  }
                }

                Button(
                  onClick = {
                    val finalTitle = bugTitle.ifBlank { "Anomalie signalée sur $selectedModule" }
                    val finalDesc = bugDescription.ifBlank {
                      "Signalement client ($currentUserEmail) demandant une vérification et réparation automatique par l'IA sur $selectedModule."
                    }
                    onSubmitBugAndAutoRepair(
                      selectedModule,
                      finalTitle,
                      finalDesc,
                      selectedSeverity
                    )
                    hasJustSubmitted = true
                    bugTitle = ""
                    bugDescription = ""
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_submit_client_bug_ai_repair")
                ) {
                  Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = PureWhite)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "🤖 ENVOYER & RÉPARER AUTOMATIQUEMENT PAR L'IA",
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.5.sp
                  )
                }

                AnimatedVisibility(visible = hasJustSubmitted) {
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ElectricGreen.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, ElectricGreen),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(
                        text = "✅ Rapport transmis ! MekanikAI Guardian a isolé le bug et appliqué le Hot-Patch.",
                        color = ElectricGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        modifier = Modifier.weight(1f)
                      )
                      TextButton(onClick = onOpenSuperAdminErrorLogger) {
                        Text(
                          text = "Voir Logs →",
                          color = TechCyan,
                          fontWeight = FontWeight.Black,
                          fontSize = 10.5.sp
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
