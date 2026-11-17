package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun WorkshopMultiDevice2FAModal(
  securityConfig: Workshop2FASecurityConfig,
  onToggle2Fa: (Boolean) -> Unit,
  onRevokeDevice: (String) -> Unit,
  onPairNewDevice: (String, String, WorkshopDeviceType, String) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var activeTab by remember { mutableStateOf(0) } // 0 = Appareils (Max 3), 1 = Config 2FA & Google Authenticator, 2 = Codes Secours
  var showPairingDialog by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .testTag("workshop_multi_device_2fa_modal"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = GraphiteDark),
      border = BorderStroke(1.5.dp, TechCyan.copy(alpha = 0.6f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp)
      ) {
        // Modal Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(TechCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Security,
                contentDescription = null,
                tint = TechCyan,
                modifier = Modifier.size(26.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "SÉCURITÉ 2FA & MULTI-APPAREILS",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    fontSize = 16.sp
                  )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = PerformanceRed.copy(alpha = 0.2f),
                  border = BorderStroke(1.dp, PerformanceRed)
                ) {
                  Text(
                    text = "RÉSERVÉ ATELIER",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = PerformanceRedGlow,
                      fontWeight = FontWeight.Black,
                      fontSize = 8.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                  )
                }
              }
              Text(
                text = "Même compte sur plusieurs téléphones • Limite : Max 3 appareils",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumGray,
                  fontSize = 11.sp
                )
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("btn_close_2fa_modal")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Device Counter & Status Banner
        val connectedCount = securityConfig.activeDevicesCount
        val maxAllowed = securityConfig.maxAllowedDevices
        val isMaxReached = connectedCount >= maxAllowed

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isMaxReached) Color(0xFF2D1616) else MetalCard
          ),
          border = BorderStroke(
            1.dp,
            if (isMaxReached) PerformanceRed else TechCyan.copy(alpha = 0.5f)
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (isMaxReached) Icons.Default.Warning else Icons.Default.Devices,
                contentDescription = null,
                tint = if (isMaxReached) PerformanceRedGlow else TechCyan,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Quota Appareils : $connectedCount / $maxAllowed téléphones",
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                  )
                )
                Text(
                  text = if (isMaxReached) "Limite max atteinte (3 appareils actifs)" else "Possibilité d'ajouter ${maxAllowed - connectedCount} appareil supplémentaire",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isMaxReached) PerformanceRedGlow else ElectricGreen,
                    fontSize = 11.sp
                  )
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (securityConfig.is2faEnabled) ElectricGreen.copy(alpha = 0.2f) else PerformanceRed.copy(alpha = 0.2f),
              border = BorderStroke(1.dp, if (securityConfig.is2faEnabled) ElectricGreen else PerformanceRed)
            ) {
              Text(
                text = if (securityConfig.is2faEnabled) "2FA ACTIF (GOOGLE)" else "2FA INACTIF",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (securityConfig.is2faEnabled) ElectricGreen else PerformanceRedGlow,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.5.sp
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Navigation Tabs (Appareils, 2FA Google, Codes Secours)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(
            "Appareils ($connectedCount/$maxAllowed)" to Icons.Default.PhoneAndroid,
            "Google Authenticator" to Icons.Default.VpnKey,
            "Codes Secours" to Icons.Default.LockReset
          ).forEachIndexed { idx, (label, icon) ->
            val isSelected = activeTab == idx
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) TechCyan else MetalCard,
              border = BorderStroke(1.dp, if (isSelected) TechCyan else MetalBorder),
              onClick = { activeTab = idx },
              modifier = Modifier
                .weight(1f)
                .testTag("tab_2fa_$idx")
            ) {
              Row(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  icon,
                  contentDescription = null,
                  tint = if (isSelected) GraphiteDark else AluminumLight,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = label,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isSelected) GraphiteDark else AluminumLight,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                    fontSize = 10.5.sp
                  ),
                  maxLines = 1
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
          when (activeTab) {
            0 -> ConnectedDevicesTab(
              securityConfig = securityConfig,
              onOpenPairing = { showPairingDialog = true },
              onRevokeDevice = onRevokeDevice
            )
            1 -> GoogleAuthenticatorConfigTab(
              securityConfig = securityConfig,
              onToggle2Fa = onToggle2Fa,
              context = context
            )
            2 -> BackupCodesTab(
              codes = securityConfig.backupCodes,
              context = context
            )
          }
        }
      }
    }
  }

  // Pairing Modal for New Device (With 2FA Verification)
  if (showPairingDialog) {
    PairNewDevice2FAModal(
      securityConfig = securityConfig,
      onConfirmPairing = { deviceName, deviceModel, deviceType, code ->
        onPairNewDevice(deviceName, deviceModel, deviceType, code)
        showPairingDialog = false
      },
      onDismiss = { showPairingDialog = false }
    )
  }
}

@Composable
private fun ConnectedDevicesTab(
  securityConfig: Workshop2FASecurityConfig,
  onOpenPairing: () -> Unit,
  onRevokeDevice: (String) -> Unit
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "APPAREILS AUTORISÉS EN ATELIER",
          style = MaterialTheme.typography.labelMedium.copy(
            color = AluminumLight,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        )

        Button(
          onClick = onOpenPairing,
          enabled = securityConfig.canAddMoreDevices,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (securityConfig.canAddMoreDevices) TechCyan else MetalBorder,
            disabledContainerColor = MetalBorder
          ),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
          modifier = Modifier.testTag("btn_pair_new_device")
        ) {
          Icon(
            Icons.Default.Add,
            contentDescription = null,
            tint = if (securityConfig.canAddMoreDevices) GraphiteDark else AluminumGray,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Associer un téléphone",
            color = if (securityConfig.canAddMoreDevices) GraphiteDark else AluminumGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }

    if (!securityConfig.canAddMoreDevices) {
      item {
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF291515)),
          border = BorderStroke(1.dp, PerformanceRed.copy(alpha = 0.7f))
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Le compte Atelier est plafonné à 3 appareils simultanés. Révoquez un ancien téléphone pour en enregistrer un nouveau.",
              style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 11.sp)
            )
          }
        }
      }
    }

    items(securityConfig.connectedDevices) { device ->
      ConnectedDeviceCard(
        device = device,
        onRevoke = { onRevokeDevice(device.id) }
      )
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Protection par Authentification Double Facteur",
              style = MaterialTheme.typography.bodyMedium.copy(color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            )
          }
          Text(
            text = "Chaque nouveau téléphone scannant le QR code ou se connectant avec les identifiants de l'atelier doit obligatoirement saisir le code dynamique TOTP à 6 chiffres généré par l'application Google Authenticator du chef d'atelier.",
            style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
          )
        }
      }
    }
  }
}

@Composable
private fun ConnectedDeviceCard(
  device: WorkshopConnectedDevice,
  onRevoke: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("device_card_${device.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (device.isCurrentDevice) Color(0xFF14241E) else MetalCard
    ),
    border = BorderStroke(
      1.dp,
      if (device.isCurrentDevice) ElectricGreen.copy(alpha = 0.7f) else MetalBorder
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(if (device.isCurrentDevice) ElectricGreen.copy(alpha = 0.2f) else TechCyan.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = when (device.deviceType) {
                WorkshopDeviceType.SMARTPHONE -> Icons.Default.PhoneAndroid
                WorkshopDeviceType.TABLETTE -> Icons.Default.TabletAndroid
                WorkshopDeviceType.PC_DIAG -> Icons.Default.Laptop
              },
              contentDescription = null,
              tint = if (device.isCurrentDevice) ElectricGreen else TechCyan,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = device.deviceName,
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = PureWhite
                )
              )
              if (device.isCurrentDevice) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = ElectricGreen.copy(alpha = 0.2f),
                  border = BorderStroke(1.dp, ElectricGreen)
                ) {
                  Text(
                    text = "CET APPAREIL",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = ElectricGreen,
                      fontWeight = FontWeight.Black,
                      fontSize = 8.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.5.dp)
                  )
                }
              }
            }

            Text(
              text = "${device.deviceModel} • ${device.osVersion}",
              style = MaterialTheme.typography.bodySmall.copy(
                color = AluminumLight,
                fontSize = 11.sp
              )
            )
          }
        }

        if (!device.isCurrentDevice) {
          IconButton(
            onClick = onRevoke,
            modifier = Modifier.testTag("btn_revoke_device_${device.id}")
          ) {
            Icon(
              Icons.Default.DeleteOutline,
              contentDescription = "Déconnecter",
              tint = PerformanceRedGlow,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = MetalBorder.copy(alpha = 0.5f), thickness = 0.8.dp)
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Wifi, contentDescription = null, tint = AluminumGray, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = device.ipLocation,
              style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 10.5.sp)
            )
          }

          if (device.assignedTechnicianName != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Person, contentDescription = null, tint = TechCyan, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Poste : ${device.assignedTechnicianName}",
                style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 10.5.sp)
              )
            }
          }
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "Dernière activité :",
            style = MaterialTheme.typography.labelSmall.copy(color = AluminumGray, fontSize = 9.sp)
          )
          Text(
            text = device.lastActiveTime,
            style = MaterialTheme.typography.bodySmall.copy(
              color = if (device.isCurrentDevice) ElectricGreen else AluminumLight,
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp
            )
          )
        }
      }
    }
  }
}

@Composable
private fun GoogleAuthenticatorConfigTab(
  securityConfig: Workshop2FASecurityConfig,
  onToggle2Fa: (Boolean) -> Unit,
  context: Context
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.6f))
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Key, contentDescription = null, tint = TechCyan, modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Application Google Authenticator",
                  style = MaterialTheme.typography.titleMedium.copy(color = PureWhite, fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "Standard TOTP RFC 6238 • Codes à 6 chiffres",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
                )
              }
            }

            Switch(
              checked = securityConfig.is2faEnabled,
              onCheckedChange = { onToggle2Fa(it) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = PureWhite,
                checkedTrackColor = ElectricGreen,
                uncheckedTrackColor = MetalBorder
              )
            )
          }

          Text(
            text = "L'activation du 2FA est requise pour synchroniser le compte atelier sur plusieurs téléphones mobiles sans risque d'accès non autorisé.",
            style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 12.sp)
          )
        }
      }
    }

    // Secret Key & QR Code Info Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "CLÉ SECRÈTE DE CONFIGURATION (MANUELLE OU QR)",
            style = MaterialTheme.typography.labelSmall.copy(
              color = AluminumGray,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          )

          // Simulated QR Code Frame
          Box(
            modifier = Modifier
              .size(140.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(PureWhite)
              .padding(8.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Icon(
                Icons.Default.QrCode2,
                contentDescription = "QR Code 2FA",
                tint = Color(0xFF101418),
                modifier = Modifier.size(110.dp)
              )
            }
          }

          Text(
            text = "Scannez ce QR Code dans Google Authenticator, Microsoft Authenticator ou FreeOTP",
            style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp)
          )

          // Secret Key Box with Copy Action
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MetalCard,
            border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.5f)),
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Clé Secrète Mekanik 2FA", securityConfig.secretKey)
                clipboard.setPrimaryClip(clip)
              }
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Clé secrète de secours :",
                  style = MaterialTheme.typography.labelSmall.copy(color = AluminumGray, fontSize = 9.sp)
                )
                Text(
                  text = securityConfig.secretKey,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    color = TechCyan,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                  )
                )
              }

              Icon(
                Icons.Default.ContentCopy,
                contentDescription = "Copier",
                tint = TechCyan,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun BackupCodesTab(
  codes: List<String>,
  context: Context
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Shield, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Codes de Récupération à Usage Unique",
              style = MaterialTheme.typography.titleMedium.copy(color = PureWhite, fontWeight = FontWeight.Bold)
            )
          }
          Text(
            text = "Conservez ces codes en lieu sûr. Ils permettent d'ouvrir une session d'urgence sur un téléphone d'atelier si vous perdez l'accès à Google Authenticator.",
            style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.5.sp)
          )
        }
      }
    }

    items(codes) { code ->
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = GraphiteBlack,
        border = BorderStroke(1.dp, MetalBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Key, contentDescription = null, tint = AluminumGray, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = code,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = PureWhite,
                fontSize = 14.sp
              )
            )
          }

          Text(
            text = "VALIDE",
            style = MaterialTheme.typography.labelSmall.copy(
              color = ElectricGreen,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
          )
        }
      }
    }

    item {
      Button(
        onClick = {
          val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
          val clip = ClipData.newPlainText("Codes Secours Mekanik+", codes.joinToString("\n"))
          clipboard.setPrimaryClip(clip)
        },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MetalBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("COPIER TOUS LES CODES DE SECOURS", color = PureWhite, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
private fun PairNewDevice2FAModal(
  securityConfig: Workshop2FASecurityConfig,
  onConfirmPairing: (String, String, WorkshopDeviceType, String) -> Unit,
  onDismiss: () -> Unit
) {
  var phoneName by remember { mutableStateOf("") }
  var technicianName by remember { mutableStateOf("") }
  var selectedType by remember { mutableStateOf(WorkshopDeviceType.SMARTPHONE) }
  var totpCode by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("pair_new_device_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = GraphiteDark),
      border = BorderStroke(1.5.dp, TechCyan)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AddModerator, contentDescription = null, tint = TechCyan, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Associer un téléphone (2FA)",
              style = MaterialTheme.typography.titleMedium.copy(color = PureWhite, fontWeight = FontWeight.Black)
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight)
          }
        }

        Text(
          text = "Étape d'enregistrement : saisissez le nom de l'appareil et validez avec le code 2FA de Google Authenticator.",
          style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.5.sp)
        )

        OutlinedTextField(
          value = phoneName,
          onValueChange = { phoneName = it },
          label = { Text("Nom de l'appareil (ex: Téléphone Pont 3)") },
          placeholder = { Text("Samsung Galaxy A54...") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_device_name"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TechCyan,
            unfocusedBorderColor = MetalBorder,
            focusedTextColor = PureWhite,
            unfocusedTextColor = PureWhite
          ),
          shape = RoundedCornerShape(10.dp),
          singleLine = true
        )

        OutlinedTextField(
          value = technicianName,
          onValueChange = { technicianName = it },
          label = { Text("Nom du Mécanicien / Utilisateur") },
          placeholder = { Text("Walid (Électricien)...") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_technician_name"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TechCyan,
            unfocusedBorderColor = MetalBorder,
            focusedTextColor = PureWhite,
            unfocusedTextColor = PureWhite
          ),
          shape = RoundedCornerShape(10.dp),
          singleLine = true
        )

        // TOTP 6-digit Code verification
        OutlinedTextField(
          value = totpCode,
          onValueChange = { if (it.length <= 6) totpCode = it },
          label = { Text("Code Google Authenticator (6 chiffres)") },
          placeholder = { Text("123456") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_totp_code"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ElectricGreen,
            unfocusedBorderColor = MetalBorder,
            focusedTextColor = PureWhite,
            unfocusedTextColor = PureWhite
          ),
          shape = RoundedCornerShape(10.dp),
          singleLine = true
        )

        if (errorMessage != null) {
          Text(
            text = errorMessage!!,
            style = MaterialTheme.typography.bodySmall.copy(color = PerformanceRedGlow, fontSize = 11.sp)
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AluminumLight),
            modifier = Modifier.weight(1f)
          ) {
            Text("Annuler")
          }

          Button(
            onClick = {
              if (phoneName.isBlank()) {
                errorMessage = "Veuillez indiquer un nom d'appareil."
              } else if (totpCode.length < 6) {
                errorMessage = "Veuillez saisir les 6 chiffres de Google Authenticator."
              } else {
                onConfirmPairing(phoneName.trim(), phoneName.trim(), selectedType, totpCode.trim())
              }
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
            modifier = Modifier
              .weight(1.3f)
              .testTag("btn_confirm_device_pairing")
          ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("VALIDER 2FA", color = GraphiteDark, fontWeight = FontWeight.Black)
          }
        }
      }
    }
  }
}
