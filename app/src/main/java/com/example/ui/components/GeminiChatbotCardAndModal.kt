package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DiagnosticRecommendedPart
import com.example.data.model.GeminiChatMessage
import com.example.data.model.GeminiModelOption
import com.example.data.model.Vehicle
import com.example.ui.theme.*

@Composable
fun GeminiChatbotSection(
  messages: List<GeminiChatMessage>,
  chatInput: String,
  isTyping: Boolean,
  selectedModel: GeminiModelOption,
  activeVehicle: Vehicle,
  onInputChanged: (String) -> Unit,
  onSendMessage: (String?) -> Unit,
  onSelectModel: (GeminiModelOption) -> Unit,
  onClearHistory: () -> Unit,
  onOpenPartInCatalog: (DiagnosticRecommendedPart) -> Unit,
  modifier: Modifier = Modifier
) {
  val focusManager = LocalFocusManager.current
  val hasLiveKey = true

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("gemini_chatbot_card"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
    border = BorderStroke(
      1.5.dp,
      Brush.horizontalGradient(listOf(TechCyan, MechanicalOrange, PerformanceRed))
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Top Header: Gemini Branding + Reset Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(listOf(TechCyanDark, MechanicalOrangeDark))
              )
              .border(1.5.dp, TechCyan, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.AutoAwesome,
              contentDescription = "Gemini Chatbot",
              tint = PureWhite,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "GEMINI CHATBOT",
                style = MaterialTheme.typography.titleMedium.copy(
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.8.sp
                )
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = TechCyan.copy(alpha = 0.18f),
                border = BorderStroke(0.8.dp, TechCyan)
              ) {
                Text(
                  text = selectedModel.displayName.uppercase(),
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = TechCyanGlow,
                    fontWeight = FontWeight.Black,
                    fontSize = 8.5.sp
                  ),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = "Assistant conversationnel multi-tours • Véhicule : ${activeVehicle.brand} ${activeVehicle.model} (${activeVehicle.engineCode})",
              style = MaterialTheme.typography.bodySmall.copy(
                color = AluminumLight,
                fontSize = 10.5.sp
              )
            )
          }
        }

        IconButton(
          onClick = onClearHistory,
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MetalSurface)
            .testTag("gemini_chatbot_reset_btn")
        ) {
          Icon(
            Icons.Default.Refresh,
            contentDescription = "Réinitialiser la conversation",
            tint = AluminumLight,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      // Model Switcher Row (gemini-3.8-flash vs gemini-4-argon)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        GeminiModelOption.entries.forEach { option ->
          val isSelected = selectedModel == option
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) TechCyanDark.copy(alpha = 0.45f) else MetalSurface,
            border = BorderStroke(1.dp, if (isSelected) TechCyan else MetalBorder),
            modifier = Modifier
              .weight(1f)
              .clickable { onSelectModel(option) }
              .testTag("gemini_model_chip_${option.name}")
          ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  if (option == GeminiModelOption.FLASH_3_5) Icons.Default.Bolt else Icons.Default.Psychology,
                  contentDescription = null,
                  tint = if (isSelected) TechCyanGlow else AluminumGray,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = option.displayName,
                  color = if (isSelected) PureWhite else AluminumLight,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp
                )
              }
              Text(
                text = option.badge,
                color = if (isSelected) TechCyanGlow else AluminumMuted,
                fontSize = 9.sp
              )
            }
          }
        }
      }

      // Firebase AI Logic & App Check Status Notice
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = MetalCard,
        border = BorderStroke(0.8.dp, if (hasLiveKey) ElectricGreen.copy(alpha = 0.5f) else MechanicalOrange.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            Icons.Default.VerifiedUser,
            contentDescription = null,
            tint = ElectricGreen,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Firebase AI Logic (Firebase.ai) & App Check actifs • Modèle Cloud : ${selectedModel.modelId}",
            style = MaterialTheme.typography.labelSmall.copy(
              color = AluminumLight,
              fontSize = 9.5.sp
            )
          )
        }
      }

      // Chat Messages Stream
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(MetalCard)
          .border(1.dp, MetalBorder, RoundedCornerShape(14.dp))
          .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        messages.takeLast(6).forEach { message ->
          GeminiChatBubble(
            message = message,
            onFollowUpClick = { prompt ->
              focusManager.clearFocus()
              onSendMessage(prompt)
            },
            onOpenPartInCatalog = onOpenPartInCatalog
          )
        }

        AnimatedVisibility(visible = isTyping) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(GraphiteBlack)
              .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            CircularProgressIndicator(
              color = TechCyan,
              strokeWidth = 2.dp,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "${selectedModel.displayName} analyse le véhicule ${activeVehicle.brand} ${activeVehicle.model}...",
              style = MaterialTheme.typography.bodySmall.copy(
                color = TechCyanGlow,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            )
          }
        }
      }

      // Chat Input Field + Send Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = chatInput,
          onValueChange = onInputChanged,
          placeholder = {
            Text(
              text = "Posez votre question à Gemini (ex: Couple culasse, P0299, réf plaquettes)...",
              color = AluminumMuted,
              fontSize = 12.sp
            )
          },
          leadingIcon = {
            Icon(
              Icons.Default.Chat,
              contentDescription = null,
              tint = TechCyan,
              modifier = Modifier.size(18.dp)
            )
          },
          trailingIcon = {
            if (chatInput.isNotEmpty()) {
              IconButton(onClick = { onInputChanged("") }) {
                Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = AluminumGray)
              }
            }
          },
          modifier = Modifier
            .weight(1f)
            .testTag("gemini_chatbot_input"),
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
              onSendMessage(null)
            }
          )
        )

        Button(
          onClick = {
            focusManager.clearFocus()
            onSendMessage(null)
          },
          enabled = !isTyping && chatInput.isNotBlank(),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = PerformanceRed,
            disabledContainerColor = MetalSurface
          ),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
          modifier = Modifier
            .height(52.dp)
            .testTag("gemini_chatbot_send_btn")
        ) {
          Icon(
            Icons.AutoMirrored.Filled.Send,
            contentDescription = "Envoyer à Gemini",
            tint = PureWhite,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun GeminiChatBubble(
  message: GeminiChatMessage,
  onFollowUpClick: (String) -> Unit,
  onOpenPartInCatalog: (DiagnosticRecommendedPart) -> Unit
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
  ) {
    Surface(
      shape = RoundedCornerShape(
        topStart = 14.dp,
        topEnd = 14.dp,
        bottomStart = if (message.isUser) 14.dp else 4.dp,
        bottomEnd = if (message.isUser) 4.dp else 14.dp
      ),
      color = if (message.isUser) PerformanceRedDark else GraphiteBlack,
      border = BorderStroke(
        1.dp,
        if (message.isUser) PerformanceRed else TechCyan.copy(alpha = 0.5f)
      ),
      modifier = Modifier.widthIn(max = 520.dp)
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        // Sender & Model Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              if (message.isUser) Icons.Default.Person else Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = if (message.isUser) PureWhite else TechCyanGlow,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (message.isUser) "VOUS (ATELIER)" else "GEMINI AI (${message.modelUsed})",
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (message.isUser) PureWhite else TechCyanGlow,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp
              )
            )
          }

          Text(
            text = message.timestamp,
            style = MaterialTheme.typography.labelSmall.copy(
              color = AluminumMuted,
              fontSize = 9.sp
            )
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = message.text.replace("**", ""),
          style = MaterialTheme.typography.bodySmall.copy(
            color = OffWhite,
            fontSize = 12.sp,
            lineHeight = 17.5.sp
          )
        )

        // Suggested OEM Parts inside Gemini Reply
        if (!message.isUser && message.suggestedParts.isNotEmpty()) {
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "PIÈCES OEM IDENTIFIÉES PAR GEMINI :",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MechanicalOrange,
              fontWeight = FontWeight.Black,
              fontSize = 9.5.sp
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          message.suggestedParts.forEach { part ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MetalSurface,
              border = BorderStroke(0.8.dp, MechanicalOrange.copy(alpha = 0.6f)),
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
                .clickable { onOpenPartInCatalog(part) }
            ) {
              Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = part.name,
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                  Text(
                    text = "Réf OEM : ${part.oemReference} • ${part.brandName}",
                    color = TechCyan,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.5.sp
                  )
                }
                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = "${part.priceEstimatedDzd} DA",
                    color = MechanicalOrange,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.5.sp
                  )
                  Text(
                    text = "Voir Schéma →",
                    color = ElectricGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                  )
                }
              }
            }
          }
        }

        // Quick Follow-Up Chips
        if (!message.isUser && message.quickFollowUps.isNotEmpty()) {
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            message.quickFollowUps.forEach { followUp ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = MetalCard,
                border = BorderStroke(0.8.dp, TechCyan.copy(alpha = 0.6f)),
                modifier = Modifier.clickable { onFollowUpClick(followUp) }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.SubdirectoryArrowRight,
                    contentDescription = null,
                    tint = TechCyan,
                    modifier = Modifier.size(12.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = followUp,
                    color = PureWhite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
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
fun GeminiChatbotDialogModal(
  messages: List<GeminiChatMessage>,
  chatInput: String,
  isTyping: Boolean,
  selectedModel: GeminiModelOption,
  activeVehicle: Vehicle,
  onInputChanged: (String) -> Unit,
  onSendMessage: (String?) -> Unit,
  onSelectModel: (GeminiModelOption) -> Unit,
  onClearHistory: () -> Unit,
  onOpenPartInCatalog: (DiagnosticRecommendedPart) -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)
        .testTag("gemini_chatbot_modal"),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, TechCyan)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Modal Top Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(GraphiteBlack)
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = TechCyanGlow,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "MEKANIK+ GEMINI CHATBOT",
              color = PureWhite,
              fontWeight = FontWeight.Black,
              fontSize = 14.sp
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite)
          }
        }

        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            GeminiChatbotSection(
              messages = messages,
              chatInput = chatInput,
              isTyping = isTyping,
              selectedModel = selectedModel,
              activeVehicle = activeVehicle,
              onInputChanged = onInputChanged,
              onSendMessage = onSendMessage,
              onSelectModel = onSelectModel,
              onClearHistory = onClearHistory,
              onOpenPartInCatalog = { part ->
                onDismiss()
                onOpenPartInCatalog(part)
              }
            )
          }
        }
      }
    }
  }
}
