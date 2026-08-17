package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MekanikPrimaryButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainNavigationTab
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

@Composable
fun MekanikAiScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  modifier: Modifier = Modifier
) {
  val focusManager = LocalFocusManager.current
  val infiniteTransition = rememberInfiniteTransition(label = "ai_halo")
  val haloAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0.8f,
    animationSpec = infiniteRepeatable(
      animation = tween(1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "alpha"
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("mekanik_ai_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Glowing AI Hero Header: Brain + Gear + Glowing Halo
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
        border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.5f))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // AI Halo Emblem
          Box(
            modifier = Modifier
              .size(72.dp)
              .clip(CircleShape)
              .background(
                brush = Brush.radialGradient(
                  colors = listOf(
                    TechCyan.copy(alpha = haloAlpha),
                    MechanicalOrangeDark.copy(alpha = 0.1f),
                    Color.Transparent
                  )
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Surface(
              modifier = Modifier.size(54.dp),
              shape = CircleShape,
              color = MetalCard,
              border = BorderStroke(1.5.dp, TechCyan)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  Icons.Default.Psychology,
                  contentDescription = null,
                  tint = PureWhite,
                  modifier = Modifier.size(32.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "MEKANIK AI DIAGNOSTIC",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp,
              color = PureWhite
            )
          )

          Text(
            text = "Assistant mécanicien intelligent • Analyse OBD-II & Symptômes",
            style = MaterialTheme.typography.bodySmall.copy(
              color = AluminumGray,
              fontSize = 12.sp
            )
          )
        }
      }
    }

    // Input Query Card (OBD Code or Symptom Description)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "CODE DÉFAUT OBD OU SYMPTÔME MÉCANIQUE",
            style = MaterialTheme.typography.labelMedium.copy(
              color = AluminumMuted,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          )
          Spacer(modifier = Modifier.height(6.dp))

          OutlinedTextField(
            value = uiState.aiQuery,
            onValueChange = { viewModel.onAiQueryChanged(it) },
            placeholder = {
              Text(
                "Ex: P0299, P0401, Sifflement turbo au ralenti...",
                color = AluminumMuted
              )
            },
            leadingIcon = {
              Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = TechCyan
              )
            },
            trailingIcon = {
              if (uiState.aiQuery.isNotEmpty()) {
                IconButton(onClick = { viewModel.onAiQueryChanged("") }) {
                  Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = AluminumGray)
                }
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("ai_query_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MetalSurface,
              unfocusedContainerColor = MetalSurface,
              focusedBorderColor = TechCyan,
              unfocusedBorderColor = MetalBorder,
              focusedTextColor = PureWhite,
              unfocusedTextColor = OffWhite
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
              onSend = {
                focusManager.clearFocus()
                viewModel.askMekanikAi()
              }
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Quick OBD Code Chips
          Text(
            text = "CODES FRÉQUENTS :",
            style = MaterialTheme.typography.labelSmall.copy(
              color = AluminumMuted,
              fontSize = 10.sp
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            ObdChip("P0299 (Pression Turbo Basse)", "P0299") { viewModel.askMekanikAi(it) }
            ObdChip("P0401 (Débit EGR Insuffisant)", "P0401") { viewModel.askMekanikAi(it) }
            ObdChip("P0300 (Ratés Allumage)", "P0300") { viewModel.askMekanikAi(it) }
            ObdChip("Bruit freinage", "Bruit au freinage") { viewModel.askMekanikAi(it) }
            ObdChip("Fumée noire turbo", "Fumée noire à l'accélération") { viewModel.askMekanikAi(it) }
          }

          Spacer(modifier = Modifier.height(14.dp))

          MekanikPrimaryButton(
            text = if (uiState.isAiLoading) "ANALYSE EN COURS..." else "LANCER LE DIAGNOSTIC IA",
            onClick = {
              focusManager.clearFocus()
              viewModel.askMekanikAi()
            },
            icon = Icons.Default.AutoFixHigh,
            enabled = !uiState.isAiLoading,
            modifier = Modifier.fillMaxWidth(),
            testTag = "btn_submit_ai"
          )
        }
      }
    }

    // Loading State with modern automotive scan animation
    if (uiState.isAiLoading) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, TechCyan)
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            CircularProgressIndicator(
              color = TechCyan,
              modifier = Modifier.size(24.dp),
              strokeWidth = 2.5.dp
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
              Text(
                text = "Analyse du problème en cours...",
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = PureWhite
                )
              )
              Text(
                text = "Interrogation des bases de données constructeur & retours atelier",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumGray,
                  fontSize = 11.sp
                )
              )
            }
          }
        }
      }
    }

    // Diagnostic Results View
    if (!uiState.isAiLoading && uiState.aiDiagnosticResult != null) {
      val result = uiState.aiDiagnosticResult!!

      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_result_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.5.dp, TechCyan.copy(alpha = 0.7f))
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            // Header Result
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = TechCyan.copy(alpha = 0.2f),
                  border = BorderStroke(1.dp, TechCyan)
                ) {
                  Text(
                    text = if (result.isObdCode) "DÉFAUT OBD" else "SYMPTÔME ANALYSÉ",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = TechCyan,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = result.query.uppercase(),
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Diagnostic Summary
            Text(
              text = result.analysisSummary,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = OffWhite,
                lineHeight = 20.sp
              )
            )

            if (result.safetyWarning != null) {
              Spacer(modifier = Modifier.height(10.dp))
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF2C1518),
                border = BorderStroke(1.dp, PerformanceRed),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = PerformanceRed,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = result.safetyWarning,
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = PerformanceRedGlow,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Causes Probables (Root Causes)
            Text(
              text = "CAUSES PROBABLES",
              style = MaterialTheme.typography.labelMedium.copy(
                color = MechanicalOrange,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MetalSurface)
                .padding(10.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              result.rootCauses.forEach { cause ->
                Row(verticalAlignment = Alignment.Top) {
                  Text(
                    text = "•",
                    style = MaterialTheme.typography.bodyMedium.copy(
                      color = MechanicalOrange,
                      fontWeight = FontWeight.Black
                    )
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = cause,
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = AluminumLight,
                      fontSize = 12.sp
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Procédure d'inspection pas-à-pas
            Text(
              text = "CONTRÔLES À EFFECTUER À L'ATELIER",
              style = MaterialTheme.typography.labelMedium.copy(
                color = TechCyan,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(GraphiteBlack)
                .padding(10.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              result.stepByStepInspection.forEach { step ->
                Text(
                  text = step,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = AluminumLight,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp
                  )
                )
              }
            }

            // 3. Multimètre & Valeurs de consigne
            if (result.multimeterSpecs != null) {
              Spacer(modifier = Modifier.height(12.dp))
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = MetalSurface,
                border = BorderStroke(0.5.dp, MetalBorder),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.ElectricBolt,
                    contentDescription = null,
                    tint = WarningAmber,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = result.multimeterSpecs,
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = OffWhite,
                      fontFamily = FontFamily.Monospace,
                      fontSize = 11.sp
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Pièces recommandées associées
            Text(
              text = "PIÈCES DE RECHANGE RECOMMANDÉES",
              style = MaterialTheme.typography.labelMedium.copy(
                color = ElectricGreen,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            )
            Spacer(modifier = Modifier.height(6.dp))

            result.recommendedParts.forEach { partName ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = MetalSurface,
                border = BorderStroke(0.5.dp, MetalBorder),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 6.dp)
                  .clickable {
                    viewModel.setNavigationTab(MainNavigationTab.PIECES)
                  }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = partName,
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = PureWhite,
                      fontSize = 11.5.sp
                    )
                  )
                  Text(
                    text = "Voir référence →",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = MechanicalOrange,
                      fontWeight = FontWeight.Bold
                    )
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

@Composable
private fun ObdChip(
  label: String,
  queryValue: String,
  onClick: (String) -> Unit
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = MetalSurface,
    border = BorderStroke(1.dp, MetalBorder),
    onClick = { onClick(queryValue) },
    modifier = Modifier.height(28.dp)
  ) {
    Box(
      modifier = Modifier.padding(horizontal = 8.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          color = AluminumLight,
          fontSize = 10.5.sp,
          fontWeight = FontWeight.Medium
        )
      )
    }
  }
}
