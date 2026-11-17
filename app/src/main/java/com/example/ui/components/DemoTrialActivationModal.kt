package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import com.example.ui.theme.*

@Composable
fun DemoTrialStatusBanner(
  demoStatus: UserDemoTrialStatus,
  onOpenActivationPortal: () -> Unit,
  onSimulatePhaseChange: (DemoTrialPhase) -> Unit,
  modifier: Modifier = Modifier
) {
  val accentColor = when (demoStatus.phase) {
    DemoTrialPhase.DEMO_ACTIVE_3_DAYS -> TechCyan
    DemoTrialPhase.DEMO_LAST_DAY_WARNING -> MechanicalOrange
    DemoTrialPhase.DEMO_EXPIRED_KEY_REQUIRED -> PerformanceRedGlow
    DemoTrialPhase.PRO_ACTIVATED_UNLOCKED -> ElectricGreen
  }

  val bgColor = when (demoStatus.phase) {
    DemoTrialPhase.DEMO_ACTIVE_3_DAYS -> Color(0xFF10212B)
    DemoTrialPhase.DEMO_LAST_DAY_WARNING -> Color(0xFF2B1C10)
    DemoTrialPhase.DEMO_EXPIRED_KEY_REQUIRED -> Color(0xFF2B0E12)
    DemoTrialPhase.PRO_ACTIVATED_UNLOCKED -> Color(0xFF0E261B)
  }

  Surface(
    shape = RoundedCornerShape(14.dp),
    color = bgColor,
    border = BorderStroke(1.2.dp, accentColor),
    modifier = modifier
      .fillMaxWidth()
      .clickable { onOpenActivationPortal() }
      .testTag("demo_trial_status_banner")
  ) {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(9.dp))
              .background(accentColor.copy(alpha = 0.22f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = when (demoStatus.phase) {
                DemoTrialPhase.DEMO_EXPIRED_KEY_REQUIRED -> Icons.Default.LockClock
                DemoTrialPhase.PRO_ACTIVATED_UNLOCKED -> Icons.Default.Verified
                else -> Icons.Default.HourglassTop
              },
              contentDescription = null,
              tint = accentColor,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = when (demoStatus.phase) {
                  DemoTrialPhase.DEMO_ACTIVE_3_DAYS -> "COMPTE DÉMO 3 JOURS ACTIF (${demoStatus.remainingHours}H / 72H)"
                  DemoTrialPhase.DEMO_LAST_DAY_WARNING -> "FIN D'ESSAI DÉMO (${demoStatus.remainingHours}H RESTANTES)"
                  DemoTrialPhase.DEMO_EXPIRED_KEY_REQUIRED -> "ESSAI 3 JOURS EXPIRÉ • CLÉ D'ACTIVATION REQUISE"
                  DemoTrialPhase.PRO_ACTIVATED_UNLOCKED -> "LICENCE PRO ACTIVÉE PAR CLÉ OFFICIELLE"
                },
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
              )
            }
            Text(
              text = when (demoStatus.phase) {
                DemoTrialPhase.DEMO_EXPIRED_KEY_REQUIRED ->
                  "Accès limité suspendu après 72h — Saisissez votre Clé d'Activation pour continuer à travailler."
                DemoTrialPhase.PRO_ACTIVATED_UNLOCKED ->
                  "Clé ${demoStatus.enteredActivationKey ?: demoStatus.expectedActivationKey} validée (${demoStatus.activatedByAdminOrStaff ?: "Admin"}) • Quotas illimités."
                else ->
                  "Accès limité 3j auto à l'inscription : VIN (${demoStatus.vinUsedToday}/${demoStatus.vinDailyDemoLimit}) • IA (${demoStatus.aiDiagUsedToday}/${demoStatus.aiDiagDailyDemoLimit}) • OEM (${demoStatus.oemCatalogViewsToday}/${demoStatus.oemCatalogDailyDemoLimit})"
              },
              color = AluminumLight,
              fontSize = 10.sp
            )
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = accentColor
        ) {
          Text(
            text = if (demoStatus.isProUnlocked) "PRO ACTIF" else "CLÉ / DÉMO",
            color = GraphiteDark,
            fontWeight = FontWeight.Black,
            fontSize = 9.5.sp,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
          )
        }
      }

      // Quick Phase Switcher Strip so user can test the 3-day demo & lock behavior immediately
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Simuler :",
          color = AluminumMuted,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        )
        listOf(
          DemoTrialPhase.DEMO_ACTIVE_3_DAYS to "J1 (68h)",
          DemoTrialPhase.DEMO_LAST_DAY_WARNING to "J3 (5h)",
          DemoTrialPhase.DEMO_EXPIRED_KEY_REQUIRED to "Expiré (Clé)",
          DemoTrialPhase.PRO_ACTIVATED_UNLOCKED to "Activé Pro"
        ).forEach { (phaseItem, shortLabel) ->
          val selected = demoStatus.phase == phaseItem
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (selected) accentColor.copy(alpha = 0.28f) else GraphiteBlack,
            border = BorderStroke(0.8.dp, if (selected) accentColor else MetalBorder),
            modifier = Modifier
              .weight(1f)
              .clickable { onSimulatePhaseChange(phaseItem) }
              .testTag("sim_demo_phase_${phaseItem.name.lowercase()}")
          ) {
            Text(
              text = shortLabel,
              color = if (selected) PureWhite else AluminumLight,
              fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
              fontSize = 8.8.sp,
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 3.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun DemoTrialActivationModal(
  demoStatus: UserDemoTrialStatus,
  onSimulatePhaseChange: (DemoTrialPhase) -> Unit,
  onActivateWithKey: (enteredKey: String) -> Unit,
  onRequestActivationKeyFromAdmin: (paymentMethod: PaymentMethodAdminType, reference: String) -> Unit,
  onSimulateNewSignupWithAutoDemo: (companyName: String, email: String, role: AccountRole) -> Unit,
  onConsumeDemoQuotaSample: () -> Unit,
  onOpenSuperAdminConsole: () -> Unit,
  onDismiss: () -> Unit
) {
  var keyInput by remember(demoStatus.expectedActivationKey) {
    mutableStateOf(demoStatus.enteredActivationKey ?: "")
  }
  var paymentReferenceInput by remember { mutableStateOf("Reçu BaridiMob #BM-2026-77419") }
  var selectedPaymentMethod by remember { mutableStateOf(PaymentMethodAdminType.BARIDIMOB_CCP) }

  // New Signup Auto-Demo test state
  var showSignupSimulator by remember { mutableStateOf(false) }
  var testSignupCompany by remember { mutableStateOf("Atelier Mécanique & Diagnostic El Djazair") }
  var testSignupEmail by remember { mutableStateOf("contact@atelier-eldjazair.dz") }
  var testSignupRole by remember { mutableStateOf(AccountRole.GARAGE_PRO) }

  val statusColor = when (demoStatus.phase) {
    DemoTrialPhase.DEMO_ACTIVE_3_DAYS -> TechCyan
    DemoTrialPhase.DEMO_LAST_DAY_WARNING -> MechanicalOrange
    DemoTrialPhase.DEMO_EXPIRED_KEY_REQUIRED -> PerformanceRedGlow
    DemoTrialPhase.PRO_ACTIVATED_UNLOCKED -> ElectricGreen
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
        .testTag("demo_trial_activation_modal"),
        shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, statusColor)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Top Header
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF101E28), GraphiteBlack, Color(0xFF281215))
              )
            )
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(statusColor.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (demoStatus.isProUnlocked) Icons.Default.Verified else Icons.Default.VpnKey,
                  contentDescription = null,
                  tint = statusColor,
                  modifier = Modifier.size(26.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "COMPTE DÉMO 3 JOURS & CLÉ D'ACTIVATION",
                  style = MaterialTheme.typography.titleMedium.copy(
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                  )
                )
                Text(
                  text = "Essai automatique 72h à l'inscription • Déblocage par Clé Pro",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp)
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
              Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite, modifier = Modifier.size(18.dp))
            }
          }
        }

        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // 1. Simulateur Cycle de Vie 3 Jours
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, statusColor.copy(alpha = 0.7f))
            ) {
              Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "CYCLE DE VIE DU COMPTE DÉMO (3 JOURS = 72H)",
                    color = statusColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.5.sp
                  )
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, statusColor)
                  ) {
                    Text(
                      text = demoStatus.phase.badgeText,
                      color = statusColor,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.sp,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                Text(
                  text = "Chaque nouvelle inscription reçoit automatiquement 3 jours d'essai avec accès limité. Une fois les 72h écoulées, une Clé d'Activation est demandée pour continuer à travailler.",
                  color = AluminumLight,
                  fontSize = 11.sp
                )

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  DemoTrialPhase.entries.forEach { phase ->
                    val isSelected = demoStatus.phase == phase
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = if (isSelected) statusColor else GraphiteBlack,
                      border = BorderStroke(1.dp, if (isSelected) PureWhite else MetalBorder),
                      modifier = Modifier
                        .weight(1f)
                        .clickable { onSimulatePhaseChange(phase) }
                    ) {
                      Column(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                      ) {
                        Text(
                          text = when (phase) {
                            DemoTrialPhase.DEMO_ACTIVE_3_DAYS -> "Jour 1/3"
                            DemoTrialPhase.DEMO_LAST_DAY_WARNING -> "Jour 3 (5h)"
                            DemoTrialPhase.DEMO_EXPIRED_KEY_REQUIRED -> "Expiré (0h)"
                            DemoTrialPhase.PRO_ACTIVATED_UNLOCKED -> "Activé Pro"
                          },
                          color = if (isSelected) GraphiteDark else PureWhite,
                          fontWeight = FontWeight.Black,
                          fontSize = 10.sp
                        )
                        Text(
                          text = when (phase) {
                            DemoTrialPhase.DEMO_ACTIVE_3_DAYS -> "68h restantes"
                            DemoTrialPhase.DEMO_LAST_DAY_WARNING -> "Urgence clé"
                            DemoTrialPhase.DEMO_EXPIRED_KEY_REQUIRED -> "Clé requise"
                            DemoTrialPhase.PRO_ACTIVATED_UNLOCKED -> "Illimité"
                          },
                          color = if (isSelected) GraphiteDark else AluminumLight,
                          fontSize = 8.5.sp
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          // 2. État des Quotas Limités du Compte Démo 3 Jours
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "QUOTAS D'ACCÈS LIMITÉ PENDANT LES 3 JOURS DÉMO",
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                  )
                  Text(
                    text = if (demoStatus.isProUnlocked) "DÉBLOQUÉ ILLIMITÉ" else "${demoStatus.remainingHours}h / 72h",
                    color = statusColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.5.sp
                  )
                }

                // Progress bar of 72h trial
                val progressFraction = if (demoStatus.isProUnlocked) {
                  1f
                } else {
                  (demoStatus.remainingHours.toFloat() / 72f).coerceIn(0f, 1f)
                }
                LinearProgressIndicator(
                  progress = { progressFraction },
                  color = statusColor,
                  trackColor = GraphiteBlack,
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                )

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  QuotaMetricCard(
                    title = "Décodage VIN",
                    used = if (demoStatus.isProUnlocked) "Illimité" else "${demoStatus.vinUsedToday} / ${demoStatus.vinDailyDemoLimit}",
                    subtitle = "Par jour en Démo",
                    color = TechCyan,
                    modifier = Modifier.weight(1f)
                  )
                  QuotaMetricCard(
                    title = "MekanikAI Diag",
                    used = if (demoStatus.isProUnlocked) "Illimité" else "${demoStatus.aiDiagUsedToday} / ${demoStatus.aiDiagDailyDemoLimit}",
                    subtitle = "Analyses IA / jour",
                    color = MechanicalOrange,
                    modifier = Modifier.weight(1f)
                  )
                  QuotaMetricCard(
                    title = "Vues OEM 7Zap",
                    used = if (demoStatus.isProUnlocked) "Illimité" else "${demoStatus.oemCatalogViewsToday} / ${demoStatus.oemCatalogDailyDemoLimit}",
                    subtitle = "Planches / jour",
                    color = ElectricGreen,
                    modifier = Modifier.weight(1f)
                  )
                }

                if (!demoStatus.isProUnlocked) {
                  OutlinedButton(
                    onClick = onConsumeDemoQuotaSample,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, TechCyan),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "SIMULER L'UTILISATION D'UN QUOTA DÉMO (+1 action)",
                      color = TechCyan,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }
          }

          // 3. Portail de Saisie de la Clé d'Activation (Obligatoire après 3 jours)
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (demoStatus.isLockedByExpiration) Color(0xFF281014) else MetalCard
              ),
              border = BorderStroke(
                1.5.dp,
                if (demoStatus.isLockedByExpiration) PerformanceRedGlow else ElectricGreen
              )
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = if (demoStatus.isLockedByExpiration) Icons.Default.Lock else Icons.Default.Key,
                    contentDescription = null,
                    tint = if (demoStatus.isLockedByExpiration) PerformanceRedGlow else ElectricGreen,
                    modifier = Modifier.size(24.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = if (demoStatus.isLockedByExpiration) {
                        "COMPTE VERROUILLÉ APRÈS 3 JOURS — CLÉ D'ACTIVATION REQUISE"
                      } else {
                        "ACTIVER VOTRE COMPTE PRO AVEC UNE CLÉ D'ACTIVATION"
                      },
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 13.sp
                    )
                    Text(
                      text = "Saisissez la clé fournie par le Super-Administrateur (badroumc@gmail.com) ou son équipe d'employés.",
                      color = AluminumLight,
                      fontSize = 11.sp
                    )
                  }
                }

                OutlinedTextField(
                  value = keyInput,
                  onValueChange = { keyInput = it.uppercase() },
                  label = { Text("Clé d'Activation Officielle (ex: MKP-ACT-2026-9941)", fontSize = 11.sp) },
                  leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = ElectricGreen) },
                  singleLine = true,
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_activation_key"),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricGreen,
                    unfocusedBorderColor = MetalBorder,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite
                  )
                )

                // Helper pre-configured valid key box
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.5f)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "CLÉ D'ACTIVATION ATTRIBUÉE À VOTRE DOSSIER :",
                        color = TechCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp
                      )
                      Text(
                        text = demoStatus.expectedActivationKey,
                        color = PureWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                      )
                    }
                    OutlinedButton(
                      onClick = { keyInput = demoStatus.expectedActivationKey },
                      shape = RoundedCornerShape(8.dp),
                      border = BorderStroke(1.dp, TechCyan),
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                      Text("Remplir Clé", color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                    }
                  }
                }

                Button(
                  onClick = {
                    val keyToUse = keyInput.ifBlank { demoStatus.expectedActivationKey }
                    onActivateWithKey(keyToUse)
                  },
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_unlock_with_activation_key")
                ) {
                  Icon(Icons.Default.LockOpen, contentDescription = null, tint = GraphiteDark)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "VALIDER LA CLÉ & CONTINUER À TRAVAILLER EN ILLIMITÉ",
                    color = GraphiteDark,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                  )
                }
              }
            }
          }

          // 4. Demander une Clé d'Activation à l'équipe Admin (Après paiement DZD)
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, MechanicalOrange)
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                  text = "VOUS N'AVEZ PAS ENCORE VOTRE CLÉ D'ACTIVATION ?",
                  color = MechanicalOrange,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp
                )
                Text(
                  text = "Envoyez votre référence de paiement DZD au Super-Administrateur ou aux employés habilités pour recevoir votre clé immédiatement :",
                  color = AluminumLight,
                  fontSize = 11.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  PaymentMethodAdminType.entries.forEach { method ->
                    val selected = selectedPaymentMethod == method
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = if (selected) MechanicalOrange else GraphiteBlack,
                      border = BorderStroke(1.dp, if (selected) PureWhite else MetalBorder),
                      modifier = Modifier
                        .weight(1f)
                        .clickable { selectedPaymentMethod = method }
                    ) {
                      Text(
                        text = method.shortTag,
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                      )
                    }
                  }
                }

                OutlinedTextField(
                  value = paymentReferenceInput,
                  onValueChange = { paymentReferenceInput = it },
                  label = { Text("Justificatif de règlement (BaridiMob / CIB / Virement)", fontSize = 10.5.sp) },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  Button(
                    onClick = {
                      onRequestActivationKeyFromAdmin(selectedPaymentMethod, paymentReferenceInput)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.2f)
                  ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("DEMANDER MA CLÉ", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 11.sp)
                  }

                  OutlinedButton(
                    onClick = onOpenSuperAdminConsole,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, PerformanceRedGlow),
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Console Admin", color = PerformanceRedGlow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                  }
                }
              }
            }
          }

          // 5. Simulateur d'Inscription avec Attribution Automatique du Compte Démo 3 Jours
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
              border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.6f))
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "TESTER UNE NOUVELLE INSCRIPTION (DÉMO 3J AUTO)",
                      color = TechCyan,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.5.sp
                    )
                    Text(
                      text = "Vérifiez que chaque nouvel inscrit reçoit automatiquement 3 jours d'essai Démo",
                      color = AluminumLight,
                      fontSize = 10.sp
                    )
                  }
                  OutlinedButton(
                    onClick = { showSignupSimulator = !showSignupSimulator },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, TechCyan)
                  ) {
                    Text(
                      text = if (showSignupSimulator) "Masquer" else "Simuler Inscription",
                      color = TechCyan,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.5.sp
                    )
                  }
                }

                if (showSignupSimulator) {
                  OutlinedTextField(
                    value = testSignupCompany,
                    onValueChange = { testSignupCompany = it },
                    label = { Text("Nom du Garage / Magasin / Client", fontSize = 10.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                  )
                  OutlinedTextField(
                    value = testSignupEmail,
                    onValueChange = { testSignupEmail = it },
                    label = { Text("Email d'inscription", fontSize = 10.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                  )
                  Button(
                    onClick = {
                      onSimulateNewSignupWithAutoDemo(testSignupCompany, testSignupEmail, testSignupRole)
                      showSignupSimulator = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Text(
                      text = "INSCRIRE & ATTRIBUER COMPTE DÉMO 3 JOURS (72H)",
                      color = GraphiteDark,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.sp
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

@Composable
private fun QuotaMetricCard(
  title: String,
  used: String,
  subtitle: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    color = GraphiteBlack,
    border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Text(title, color = AluminumLight, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
      Text(used, color = color, fontWeight = FontWeight.Black, fontSize = 14.sp)
      Text(subtitle, color = AluminumMuted, fontSize = 9.sp)
    }
  }
}
