package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun SecurityShieldCenterModal(
  currentRole: FirebaseRbacRole,
  shields: List<DefenseShieldItem>,
  dmsMatrix: List<DmsProtectedOperationCheck>,
  auditLogs: List<FirestoreSecurityAuditEvent>,
  onSelectRole: (FirebaseRbacRole) -> Unit,
  onToggleShield: (Int, Boolean) -> Unit,
  onTriggerTestAuditLog: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: 6 Boucliers, 1: Rôles RBAC & DMS, 2: Journal Audit HMAC

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)
        .testTag("security_shield_center_modal"),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, ElectricGreen.copy(alpha = 0.7f))
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Modal Header
        Surface(
          color = MetalCard,
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                  modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ElectricGreen.copy(alpha = 0.2f))
                    .border(1.dp, ElectricGreen, RoundedCornerShape(12.dp)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Shield, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "6 BOUCLIERS DE DÉFENSE & RBAC FIREBASE AUTH",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Black,
                      color = PureWhite,
                      fontSize = 14.sp
                    )
                  )
                  Text(
                    text = "Rôle Actif : ${currentRole.title} • Firestore Zero-Trust",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = Color(currentRole.colorHex),
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                  )
                }
              }

              IconButton(
                onClick = onDismiss,
                modifier = Modifier
                  .size(34.dp)
                  .clip(CircleShape)
                  .background(GraphiteBlack)
              ) {
                Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite, modifier = Modifier.size(18.dp))
              }
            }

            // Tab Switcher
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              val tabs = listOf(
                "🛡 Les 6 Boucliers",
                "🔐 Rôles RBAC & DMS",
                "📜 Journal HMAC-SHA256"
              )
              tabs.forEachIndexed { idx, label ->
                val isSelected = selectedTab == idx
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (isSelected) PerformanceRed else GraphiteBlack,
                  border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
                  onClick = { selectedTab = idx },
                  modifier = Modifier.weight(1f)
                ) {
                  Text(
                    text = label,
                    color = if (isSelected) PureWhite else AluminumLight,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                    fontSize = 10.5.sp,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp)
                  )
                }
              }
            }
          }
        }

        // Content
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Role Switcher Banner always visible at top
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, Color(currentRole.colorHex))
            ) {
              Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "CONTRÔLE D'ACCÈS RBAC FIREBASE AUTH",
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                  )
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(currentRole.colorHex).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, Color(currentRole.colorHex))
                  ) {
                    Text(
                      text = currentRole.shortBadge,
                      color = Color(currentRole.colorHex),
                      fontWeight = FontWeight.Black,
                      fontSize = 9.sp,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = currentRole.description,
                  color = AluminumLight,
                  fontSize = 10.5.sp
                )
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  FirebaseRbacRole.entries.forEach { role ->
                    val isSelected = role == currentRole
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = if (isSelected) Color(role.colorHex).copy(alpha = 0.25f) else GraphiteBlack,
                      border = BorderStroke(1.dp, if (isSelected) Color(role.colorHex) else MetalBorder),
                      onClick = { onSelectRole(role) },
                      modifier = Modifier
                        .weight(1f)
                        .testTag("select_rbac_role_${role.name.lowercase()}")
                    ) {
                      Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                      ) {
                        Text(
                          text = role.id,
                          color = if (isSelected) PureWhite else AluminumLight,
                          fontWeight = FontWeight.Black,
                          fontSize = 10.sp
                        )
                        Text(
                          text = if (role == FirebaseRbacRole.SUPER_ADMIN) "Accès Total" else if (role == FirebaseRbacRole.GESTIONNAIRE) "DMS & Marges" else "Atelier & VIN",
                          color = Color(role.colorHex),
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Bold
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          if (selectedTab == 0) {
            items(shields) { shield ->
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("shield_item_${shield.shieldNumber}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, if (shield.isEnabled) ElectricGreen.copy(alpha = 0.6f) else MetalBorder)
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                      Box(
                        modifier = Modifier
                          .size(34.dp)
                          .clip(CircleShape)
                          .background(if (shield.isEnabled) ElectricGreenDark else GraphiteBlack)
                          .border(1.dp, if (shield.isEnabled) ElectricGreen else MetalBorder, CircleShape),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(
                          text = "#${shield.shieldNumber}",
                          color = if (shield.isEnabled) ElectricGreen else AluminumMuted,
                          fontWeight = FontWeight.Black,
                          fontSize = 11.sp
                        )
                      }
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text(
                          text = shield.title,
                          color = PureWhite,
                          fontWeight = FontWeight.Bold,
                          fontSize = 12.5.sp
                        )
                        Text(
                          text = shield.subtitle,
                          color = TechCyan,
                          fontSize = 10.5.sp,
                          fontWeight = FontWeight.Medium
                        )
                      }
                    }

                    Switch(
                      checked = shield.isEnabled,
                      onCheckedChange = { onToggleShield(shield.shieldNumber, it) }
                    )
                  }

                  Text(
                    text = shield.technicalSpec,
                    color = AluminumLight,
                    fontSize = 10.5.sp
                  )

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = GraphiteBlack,
                      border = BorderStroke(1.dp, MetalBorder)
                    ) {
                      Text(
                        text = shield.statusLabel,
                        color = if (shield.isEnabled) ElectricGreen else MechanicalOrange,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                      )
                    }
                    Text(
                      text = "Rôle min: ${shield.minimumRoleToConfigure.id}",
                      color = AluminumMuted,
                      fontSize = 9.5.sp
                    )
                  }
                }
              }
            }
          } else if (selectedTab == 1) {
            items(dmsMatrix) { check ->
              val isAllowed = when (check.minimumRoleRequired) {
                FirebaseRbacRole.SUPER_ADMIN -> currentRole == FirebaseRbacRole.SUPER_ADMIN
                FirebaseRbacRole.GESTIONNAIRE -> currentRole == FirebaseRbacRole.SUPER_ADMIN || currentRole == FirebaseRbacRole.GESTIONNAIRE
                FirebaseRbacRole.TECHNICIEN -> true
              }
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MetalCard,
                border = BorderStroke(1.dp, if (isAllowed) ElectricGreen.copy(alpha = 0.5f) else PerformanceRed.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = check.operationTitle,
                      color = PureWhite,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp
                    )
                    Text(
                      text = "Module : ${check.sensitiveCategory} • Requis : ${check.minimumRoleRequired.id}",
                      color = AluminumGray,
                      fontSize = 10.sp
                    )
                  }
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isAllowed) ElectricGreenDark else PerformanceRedDark,
                    border = BorderStroke(1.dp, if (isAllowed) ElectricGreen else PerformanceRed)
                  ) {
                    Text(
                      text = if (isAllowed) "AUTORISÉ" else "BLOQUÉ RBAC",
                      color = if (isAllowed) ElectricGreen else PerformanceRedGlow,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.5.sp,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }
              }
            }
          } else {
            item {
              Button(
                onClick = { onTriggerTestAuditLog("Vérification intégrité DMS & scellement HMAC-SHA256") },
                colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("GÉNÉRER ENTRÉE JOURNAL SCELLÉE HMAC-SHA256", fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
            }
            items(auditLogs) { log ->
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MetalCard,
                border = BorderStroke(1.dp, MetalBorder),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = "${log.shieldCategory} • ${log.rbacRole}",
                      color = TechCyan,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.sp
                    )
                    Text(
                      text = log.operatorName,
                      color = MechanicalOrange,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp
                    )
                  }
                  Text(
                    text = log.actionDescription,
                    color = OffWhite,
                    fontSize = 11.sp
                  )
                  Text(
                    text = "Scellé : ${log.hmacSha256Seal}",
                    color = ElectricGreen,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.5.sp
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
