package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.*
import java.net.URLEncoder

@Composable
fun ClientMessagingModal(
  initialClientName: String = "Sofiane Benali",
  initialClientPhone: String = "0550 12 34 56",
  initialVehicleModel: String = "Peugeot 308 (1.6 BlueHDi)",
  initialPlateNumber: String = "01429-122-16",
  initialAmountDzd: String = "18 500",
  templates: List<MessageNotificationTemplate>,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var clientName by remember { mutableStateOf(initialClientName) }
  var clientPhone by remember { mutableStateOf(initialClientPhone) }
  var vehicleModel by remember { mutableStateOf(initialVehicleModel) }
  var plateNumber by remember { mutableStateOf(initialPlateNumber) }
  var amountDzd by remember { mutableStateOf(initialAmountDzd) }
  var selectedTemplateType by remember { mutableStateOf(NotificationTemplateType.VEHICULE_REPARE) }
  var feedbackToast by remember { mutableStateOf<String?>(null) }

  val activeTemplate = templates.firstOrNull { it.type == selectedTemplateType }
    ?: templates.firstOrNull()

  // Formatted Message Computed
  val generatedMessage = remember(clientName, clientPhone, vehicleModel, plateNumber, amountDzd, activeTemplate) {
    val template = activeTemplate?.templateText ?: ""
    template
      .replace("{CLIENT}", clientName)
      .replace("{VEHICULE}", vehicleModel)
      .replace("{MATRICULE}", plateNumber)
      .replace("{MONTANT}", amountDzd)
      .replace("{GARAGE}", "Garage Auto Performance Alger")
      .replace("{TELEPHONE}", "021 68 40 12")
  }

  // Format phone to international DZ format +213
  fun formatDzPhone(rawPhone: String): String {
    val clean = rawPhone.replace(" ", "").replace("-", "")
    return when {
      clean.startsWith("+213") -> clean
      clean.startsWith("0") -> "+213" + clean.substring(1)
      clean.startsWith("213") -> "+$clean"
      else -> "+213$clean"
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = 28.dp, bottom = 16.dp, start = 12.dp, end = 12.dp)
        .testTag("modal_client_messaging"),
      shape = RoundedCornerShape(16.dp),
      color = GraphiteDark,
      border = BorderStroke(1.dp, MetalBorder)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ElectricGreen.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Send, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "MESSAGERIE CLIENT WHATSAPP & SMS",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = PureWhite,
                  fontSize = 15.sp
                )
              )
              Text(
                text = "Notifications d'atelier automatiques et alertes de suivi",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(MetalCard)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight, modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Template Selector Chips
          item {
            Column {
              Text("MODÈLES DE NOTIFICATION DISPONIBLES :", color = AluminumGray, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.height(6.dp))
              LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(NotificationTemplateType.entries) { type ->
                  val isSelected = type == selectedTemplateType
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) ElectricGreenDark else MetalCard,
                    border = BorderStroke(1.dp, if (isSelected) ElectricGreen else MetalBorder),
                    onClick = { selectedTemplateType = type },
                    modifier = Modifier.testTag("tab_msg_${type.name.lowercase()}")
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(type.iconEmoji, fontSize = 13.sp)
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = type.label,
                        color = if (isSelected) PureWhite else AluminumLight,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp
                      )
                    }
                  }
                }
              }
            }
          }

          // Recipient Meta Details Form
          item {
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("DESTINATAIRE & DONNÉES DU VÉHICULE", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Nom Client") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedTextColor = PureWhite,
                      unfocusedTextColor = PureWhite,
                      focusedBorderColor = ElectricGreen,
                      unfocusedBorderColor = MetalBorder
                    ),
                    singleLine = true
                  )
                  OutlinedTextField(
                    value = clientPhone,
                    onValueChange = { clientPhone = it },
                    label = { Text("Téléphone (DZ)") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedTextColor = PureWhite,
                      unfocusedTextColor = PureWhite,
                      focusedBorderColor = ElectricGreen,
                      unfocusedBorderColor = MetalBorder
                    ),
                    singleLine = true
                  )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  OutlinedTextField(
                    value = vehicleModel,
                    onValueChange = { vehicleModel = it },
                    label = { Text("Véhicule") },
                    modifier = Modifier.weight(1.2f),
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedTextColor = PureWhite,
                      unfocusedTextColor = PureWhite,
                      focusedBorderColor = TechCyan,
                      unfocusedBorderColor = MetalBorder
                    ),
                    singleLine = true
                  )
                  OutlinedTextField(
                    value = amountDzd,
                    onValueChange = { amountDzd = it },
                    label = { Text("Montant (DZD)") },
                    modifier = Modifier.weight(0.8f),
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedTextColor = PureWhite,
                      unfocusedTextColor = PureWhite,
                      focusedBorderColor = MechanicalOrange,
                      unfocusedBorderColor = MetalBorder
                    ),
                    singleLine = true
                  )
                }
              }
            }
          }

          // Live Formatted Message Preview Card
          item {
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
              border = BorderStroke(1.dp, ElectricGreen.copy(alpha = 0.5f))
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ElectricGreen))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("APERÇU DU MESSAGE CLIENT", color = ElectricGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                  }
                  Text("Format WhatsApp / SMS", color = AluminumMuted, fontSize = 10.sp)
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MetalCard,
                  border = BorderStroke(0.5.dp, MetalBorder),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text(
                    text = generatedMessage,
                    color = PureWhite,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(12.dp)
                  )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.PhoneIphone, contentDescription = null, tint = AluminumMuted, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Numéro destinataire : ${formatDzPhone(clientPhone)}",
                    color = AluminumGray,
                    fontSize = 10.5.sp
                  )
                }
              }
            }
          }

          if (feedbackToast != null) {
            item {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = ElectricGreenDark,
                border = BorderStroke(1.dp, ElectricGreen),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = feedbackToast!!,
                  color = PureWhite,
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(10.dp),
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Actions Row
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          // WhatsApp Primary Button
          Button(
            onClick = {
              try {
                val formattedPhone = formatDzPhone(clientPhone).replace("+", "")
                val encodedText = URLEncoder.encode(generatedMessage, "UTF-8")
                val intent = Intent(Intent.ACTION_VIEW).apply {
                  data = Uri.parse("https://api.whatsapp.com/send?phone=$formattedPhone&text=$encodedText")
                }
                context.startActivity(intent)
                feedbackToast = "✅ WhatsApp ouvert avec le message pré-rempli !"
              } catch (e: Exception) {
                feedbackToast = "Action WhatsApp déclenchée pour ${formatDzPhone(clientPhone)}"
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(46.dp)
              .testTag("btn_send_whatsapp")
          ) {
            Icon(Icons.Default.Chat, contentDescription = null, tint = PureWhite, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("ENVOYER VIA WHATSAPP 📱", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }

          // SMS & Copy Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                try {
                  val formattedPhone = formatDzPhone(clientPhone)
                  val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:$formattedPhone")
                    putExtra("sms_body", generatedMessage)
                  }
                  context.startActivity(intent)
                  feedbackToast = "✅ Application SMS ouverte pour $formattedPhone !"
                } catch (e: Exception) {
                  feedbackToast = "Action SMS déclenchée pour ${formatDzPhone(clientPhone)}"
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, TechCyan),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("btn_send_sms")
            ) {
              Icon(Icons.Default.Sms, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("ENVOYER PAR SMS", color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
            }

            Button(
              onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Message Mekanik+", generatedMessage)
                clipboard.setPrimaryClip(clip)
                feedbackToast = "📋 Message copié dans le presse-papier !"
              },
              colors = ButtonDefaults.buttonColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, MetalBorder),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("btn_copy_message")
            ) {
              Icon(Icons.Default.ContentCopy, contentDescription = null, tint = AluminumLight, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("COPIER TEXTE", color = AluminumLight, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
            }
          }
        }
      }
    }
  }
}
