package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppNotification
import com.example.data.model.NotificationCategory
import com.example.data.model.NotificationPriority
import com.example.ui.theme.*

@Composable
fun NotificationCenterModal(
  notifications: List<AppNotification>,
  onDismiss: () -> Unit,
  onMarkAsRead: (String) -> Unit,
  onMarkAllAsRead: () -> Unit,
  onSendSimulatedNotification: (String, String, NotificationCategory) -> Unit
) {
  var selectedCategoryFilter by remember { mutableStateOf<NotificationCategory?>(null) }
  var showSimulatorDialog by remember { mutableStateOf(false) }

  val filteredNotifications = if (selectedCategoryFilter == null) {
    notifications
  } else {
    notifications.filter { it.category == selectedCategoryFilter }
  }

  val unreadCount = notifications.count { !it.isRead }

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp)
      .testTag("notification_center_modal"),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            Icons.Default.NotificationsActive,
            contentDescription = null,
            tint = PerformanceRed,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "NOTIFICATIONS PUSH",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Black,
              color = PureWhite
            )
          )
        }

        if (unreadCount > 0) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = PerformanceRed
          ) {
            Text(
              text = "$unreadCount non lue(s)",
              style = MaterialTheme.typography.labelSmall.copy(
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              ),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 480.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Quick Actions Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(
            onClick = onMarkAllAsRead,
            contentPadding = PaddingValues(0.dp)
          ) {
            Text(
              text = "Tout marquer comme lu",
              color = TechCyan,
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
            )
          }

          TextButton(
            onClick = { showSimulatorDialog = true },
            contentPadding = PaddingValues(0.dp)
          ) {
            Icon(
              Icons.Default.Send,
              contentDescription = null,
              tint = MechanicalOrange,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Tester une alerte",
              color = MechanicalOrange,
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
            )
          }
        }

        // Filter Chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (selectedCategoryFilter == null) PerformanceRed else MetalSurface,
            border = BorderStroke(1.dp, if (selectedCategoryFilter == null) PerformanceRed else MetalBorder),
            onClick = { selectedCategoryFilter = null }
          ) {
            Text(
              text = "Toutes (${notifications.size})",
              style = MaterialTheme.typography.labelSmall.copy(
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp
              ),
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }

          NotificationCategory.entries.forEach { cat ->
            val isSelected = selectedCategoryFilter == cat
            val catCount = notifications.count { it.category == cat }
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = if (isSelected) Color(cat.colorHex) else MetalSurface,
              border = BorderStroke(1.dp, if (isSelected) Color(cat.colorHex) else MetalBorder),
              onClick = { selectedCategoryFilter = if (isSelected) null else cat }
            ) {
              Text(
                text = "${cat.label} ($catCount)",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (isSelected) GraphiteBlack else AluminumLight,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.5.sp
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        Divider(color = MetalBorder, thickness = 1.dp)

        // Notifications List
        if (filteredNotifications.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Aucune notification dans cette catégorie.",
              style = MaterialTheme.typography.bodyMedium.copy(color = AluminumGray)
            )
          }
        } else {
          LazyColumn(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(filteredNotifications) { notif ->
              NotificationItemCard(
                notification = notif,
                onMarkRead = { onMarkAsRead(notif.id) }
              )
            }
          }
        }
      }
    },
    confirmButton = {
      MekanikPrimaryButton(
        text = "FERMER",
        onClick = onDismiss
      )
    },
    containerColor = MetalCard
  )

  // Sub-dialog to trigger custom simulated push notification
  if (showSimulatorDialog) {
    PushSimulatorDialog(
      onDismiss = { showSimulatorDialog = false },
      onSendNotification = { title, msg, cat ->
        onSendSimulatedNotification(title, msg, cat)
        showSimulatorDialog = false
      }
    )
  }
}

@Composable
private fun NotificationItemCard(
  notification: AppNotification,
  onMarkRead: () -> Unit
) {
  val catColor = Color(notification.category.colorHex)
  val catIcon: ImageVector = when (notification.category) {
    NotificationCategory.ENTRETIEN_RAPPEL -> Icons.Default.Build
    NotificationCategory.RDV_ATELIER -> Icons.Default.EventAvailable
    NotificationCategory.PIECE_DISPONIBLE -> Icons.Default.Inventory2
    NotificationCategory.MEKANIK_AI_ALERTE -> Icons.Default.Psychology
    NotificationCategory.OFFRE_VENDEUR -> Icons.Default.LocalOffer
  }

  Surface(
    shape = RoundedCornerShape(10.dp),
    color = if (!notification.isRead) GraphiteBlack else MetalSurface,
    border = BorderStroke(
      if (!notification.isRead) 1.dp else 0.5.dp,
      if (!notification.isRead) catColor.copy(alpha = 0.6f) else MetalBorder
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onMarkRead)
  ) {
    Row(
      modifier = Modifier.padding(10.dp),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(catColor.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          catIcon,
          contentDescription = null,
          tint = catColor,
          modifier = Modifier.size(16.dp)
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = notification.title,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Medium,
              color = if (!notification.isRead) PureWhite else OffWhite,
              fontSize = 12.5.sp
            ),
            modifier = Modifier.weight(1f)
          )
          Text(
            text = notification.timestamp,
            style = MaterialTheme.typography.labelSmall.copy(
              color = AluminumMuted,
              fontSize = 10.sp
            )
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = notification.message,
          style = MaterialTheme.typography.bodySmall.copy(
            color = AluminumLight,
            fontSize = 11.5.sp,
            lineHeight = 15.sp
          )
        )
      }
    }
  }
}

@Composable
private fun PushSimulatorDialog(
  onDismiss: () -> Unit,
  onSendNotification: (String, String, NotificationCategory) -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "SIMULATEUR DE NOTIFICATIONS PUSH",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Black,
          color = PureWhite
        )
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = "Choisissez un type d'alerte pour tester le comportement en direct :",
          style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
        )

        PresetPushOption(
          title = "🛢️ Contrôle Niveau d'Huile (10 000 km)",
          subtitle = "Rappel préventif pour le propriétaire avec norme d'huile",
          onClick = {
            onSendNotification(
              "🛢️ Rappel : Vérifier le niveau d'huile moteur",
              "Votre véhicule a parcouru 10 000 km. Contrôlez la jauge à froid (Norme préconisée : 5W-30 Synthèse).",
              NotificationCategory.ENTRETIEN_RAPPEL
            )
          }
        )

        PresetPushOption(
          title = "🌡️ Alerte Liquide de Refroidissement (LDR)",
          subtitle = "Vérification bocal d'expansion pour éviter la surchauffe",
          onClick = {
            onSendNotification(
              "🌡️ Alerte LDR : Contrôle niveau liquide de refroidissement",
              "Niveau du vase d'expansion à vérifier moteur froid. Utiliser exclusivement du liquide G12evo ou Type D.",
              NotificationCategory.ENTRETIEN_RAPPEL
            )
          }
        )

        PresetPushOption(
          title = "📅 Confirmation RDV Atelier Pro",
          subtitle = "Notification envoyée au client après validation atelier",
          onClick = {
            onSendNotification(
              "📅 RDV Atelier Validé : Diagnostic & Vidange",
              "Votre rendez-vous pour demain à 09h30 au Garage Auto Performance Alger a été confirmé par l'équipe.",
              NotificationCategory.RDV_ATELIER
            )
          }
        )

        PresetPushOption(
          title = "📦 Pièce OEM Arrivée en Stock",
          subtitle = "Alerte de disponibilité magasin pièces de rechange",
          onClick = {
            onSendNotification(
              "📦 Pièce Disponible : Disques ventilés 312mm",
              "Les disques de frein Brembo que vous aviez en alerte sont maintenant en stock chez Sarl Auto Pièces Alger.",
              NotificationCategory.PIECE_DISPONIBLE
            )
          }
        )
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text("Fermer", color = AluminumGray)
      }
    },
    containerColor = MetalCard
  )
}

@Composable
private fun PresetPushOption(
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = MetalSurface,
    border = BorderStroke(0.5.dp, MetalBorder),
    onClick = onClick,
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontWeight = FontWeight.Bold,
          color = PureWhite,
          fontSize = 12.5.sp
        )
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(
          color = AluminumGray,
          fontSize = 10.5.sp
        )
      )
    }
  }
}
