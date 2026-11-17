package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun SuperAdminPortalModal(
  adminSession: SuperAdminAccountSession,
  staffMembers: List<AdminStaffMember>,
  registrationRequests: List<UserRegistrationRequest>,
  planSubscriptionRequests: List<PlanSubscriptionRequest>,
  auditLogs: List<AdminAuditLogEntry>,
  onLoginAdmin: (email: String, passwordOrPin: String) -> Unit,
  onLogoutAdmin: () -> Unit,
  onApproveRegistration: (requestId: String) -> Unit,
  onRejectRegistration: (requestId: String) -> Unit,
  onToggleSuspendRegistration: (requestId: String) -> Unit,
  onGenerateActivationKeyForRegistration: (requestId: String) -> Unit,
  onApproveAllPendingRegistrations: () -> Unit,
  onCreateTestRegistration: (companyName: String, managerName: String, email: String, wilaya: String, role: AccountRole, rcNumber: String, planName: String) -> Unit,
  onApprovePlanSubscription: (subscriptionId: String) -> Unit,
  onRejectPlanSubscription: (subscriptionId: String) -> Unit,
  onExtendPlanSubscription: (subscriptionId: String) -> Unit,
  onApproveAllPendingSubscriptions: () -> Unit,
  onCreateTestPlanSubscription: (accountName: String, planTitle: String, amountDzd: Int, paymentMethod: PaymentMethodAdminType, proofRef: String, role: AccountRole) -> Unit,
  onCreateAdminStaffMember: (
    fullName: String,
    email: String,
    pinCode: String,
    role: AdminStaffRole,
    wilayas: String,
    canApproveReg: Boolean,
    canGenerateKey: Boolean,
    canValidatePay: Boolean,
    canSuspend: Boolean
  ) -> Unit,
  onToggleStaffActive: (staffId: String) -> Unit,
  onSwitchAdminOperator: (staff: AdminStaffMember?) -> Unit,
  onSwitchToRolePreview: (AccountRole) -> Unit,
  onDismiss: () -> Unit,
  initialSelectedTab: Int = 0,
  errorLoggerState: ErrorLoggerUiState = ErrorLoggerUiState(),
  onSelectErrorCategoryFilter: (SystemErrorCategory?) -> Unit = {},
  onSelectErrorSeverityFilter: (SystemErrorSeverity?) -> Unit = {},
  onSelectErrorStatusFilter: (ErrorResolutionStatus?) -> Unit = {},
  onSearchErrorLogs: (String) -> Unit = {},
  onSimulateCrashLog: () -> Unit = {},
  onSimulateApiFailureLog: () -> Unit = {},
  onSimulatePersistenceFailureLog: () -> Unit = {},
  onSimulateClientBugReport: () -> Unit = {},
  onToggleAiAutoRepair: (Boolean) -> Unit = {},
  onRepairErrorWithAi: (errorId: String) -> Unit = {},
  onRepairAllOpenErrorsWithAi: () -> Unit = {},
  onUpdateErrorStatusAndNote: (errorId: String, status: ErrorResolutionStatus, note: String) -> Unit = { _, _, _ -> },
  onDeleteErrorLog: (errorId: String) -> Unit = {},
  onPurgeResolvedErrorLogs: () -> Unit = {}
) {
  var selectedTab by remember(initialSelectedTab) { mutableIntStateOf(initialSelectedTab) }
  var registrationFilter by remember { mutableStateOf<AdminApprovalStatus?>(null) }
  var subscriptionFilter by remember { mutableStateOf<AdminApprovalStatus?>(null) }
  var showAddRegistrationForm by remember { mutableStateOf(false) }
  var showAddSubscriptionForm by remember { mutableStateOf(false) }
  var showCreateStaffForm by remember { mutableStateOf(false) }

  // Login form state when locked
  var emailInput by remember { mutableStateOf(adminSession.adminEmail) }
  var passwordInput by remember { mutableStateOf("MekanikAdmin2026!") }
  var showPassword by remember { mutableStateOf(false) }

  val pendingRegistrationsCount = registrationRequests.count {
    it.status == AdminApprovalStatus.EN_ATTENTE ||
      it.status == AdminApprovalStatus.DEMO_3_JOURS ||
      it.status == AdminApprovalStatus.CLE_REQUISE
  }
  val pendingSubscriptionsCount = planSubscriptionRequests.count { it.status == AdminApprovalStatus.EN_ATTENTE }
  val approvedRevenueDzd = planSubscriptionRequests
    .filter { it.status == AdminApprovalStatus.APPROUVE }
    .sumOf { it.amountDzd }
  val pendingRevenueDzd = planSubscriptionRequests
    .filter { it.status == AdminApprovalStatus.EN_ATTENTE }
    .sumOf { it.amountDzd }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
        .testTag("super_admin_portal_modal"),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, PerformanceRed)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Top Header
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF2B0B0E), GraphiteBlack, Color(0xFF19222B))
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
                  .size(46.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(PerformanceRed.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.AdminPanelSettings,
                  contentDescription = null,
                  tint = PerformanceRedGlow,
                  modifier = Modifier.size(28.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "CONSOLE SUPER-ADMINISTRATEUR",
                    style = MaterialTheme.typography.titleMedium.copy(
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 14.sp
                    )
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (adminSession.isAuthenticated) ElectricGreen.copy(alpha = 0.2f) else MechanicalOrange.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, if (adminSession.isAuthenticated) ElectricGreen else MechanicalOrange)
                  ) {
                    Text(
                      text = if (adminSession.isAuthenticated) adminSession.currentOperatorBadge else "VERROUILLÉ",
                      color = if (adminSession.isAuthenticated) ElectricGreen else MechanicalOrange,
                      fontWeight = FontWeight.Black,
                      fontSize = 8.5.sp,
                      modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = "${adminSession.currentOperatorEmail} • Inscriptions, Clés Démo 3J & Équipe Employés",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 10.5.sp)
                )
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              if (adminSession.isAuthenticated) {
                IconButton(
                  onClick = onLogoutAdmin,
                  modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(MetalCard)
                    .testTag("btn_admin_logout")
                ) {
                  Icon(Icons.Default.Lock, contentDescription = "Verrouiller", tint = MechanicalOrange, modifier = Modifier.size(17.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
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
        }

        if (!adminSession.isAuthenticated) {
          // ====================================================================
          // ÉCRAN DE CONNEXION SUPER-ADMINISTRATEUR (GOOGLE AUTHENTICATOR + SMS) & EMPLOYÉS
          // ====================================================================
          val liveLoginTotp = remember {
            GoogleAuthenticatorTotpEngine.generateTotpCode(SuperAdminGlobalControlRegistry.SUPER_ADMIN_TOTP_SECRET)
          }
          var loginTotpInput by remember { mutableStateOf(liveLoginTotp) }
          var loginSmsInput by remember { mutableStateOf(SuperAdminGlobalControlRegistry.activeSmsOtpCode) }
          var loginPhoneInput by remember { mutableStateOf(SuperAdminGlobalControlRegistry.superAdminPhoneNumber) }
          var login2FaError by remember { mutableStateOf<String?>(null) }

          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            item {
              Spacer(modifier = Modifier.height(8.dp))
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.5.dp, PerformanceRed)
              ) {
                Column(
                  modifier = Modifier.padding(20.dp),
                  verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text(
                        text = "CONNEXION SUPER-ADMIN UNIQUE : badroumc@gmail.com (GOOGLE 2FA + SMS + EMAIL)",
                        color = PureWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.5.sp
                      )
                      Text(
                        text = "Prix des abonnements, confirmation des paiements, validation des comptes et ajout/modification d'APIs (TecDoc, ETKA, Stellantis, VIN, Autodata, SATIM, Stripe)",
                        color = AluminumGray,
                        fontSize = 11.5.sp
                      )
                    }
                  }

                  HorizontalDivider(color = MetalBorder)

                  OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Email Super-Admin ou Employé", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TechCyan) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("admin_login_email_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedBorderColor = TechCyan,
                      unfocusedBorderColor = MetalBorder,
                      focusedTextColor = PureWhite,
                      unfocusedTextColor = PureWhite
                    )
                  )

                  OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Mot de passe ou Code PIN Employé", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = MechanicalOrange) },
                    trailingIcon = {
                      IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                          if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                          contentDescription = null,
                          tint = AluminumLight
                        )
                      }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("admin_login_password_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedBorderColor = MechanicalOrange,
                      unfocusedBorderColor = MetalBorder,
                      focusedTextColor = PureWhite,
                      unfocusedTextColor = PureWhite
                    )
                  )

                  // Double Facteur : Google Authenticator + SMS Téléphone
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(
                      modifier = Modifier.padding(12.dp),
                      verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Text(
                        text = "🛡️ DOUBLE AUTHENTIFICATION : GOOGLE AUTHENTICATOR + SMS TÉLÉPHONE",
                        color = MechanicalOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.5.sp
                      )

                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        OutlinedTextField(
                          value = loginTotpInput,
                          onValueChange = {
                            loginTotpInput = it.filter { c -> c.isDigit() }.take(6)
                            login2FaError = null
                          },
                          label = { Text("Code Authenticator (6 ch.)", fontSize = 10.sp) },
                          singleLine = true,
                          modifier = Modifier.weight(1f).testTag("admin_login_totp_input"),
                          colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricGreen,
                            unfocusedBorderColor = MetalBorder,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                          )
                        )

                        OutlinedTextField(
                          value = loginSmsInput,
                          onValueChange = {
                            loginSmsInput = it.filter { c -> c.isDigit() }.take(6)
                            login2FaError = null
                          },
                          label = { Text("Code SMS reçu (6 ch.)", fontSize = 10.sp) },
                          singleLine = true,
                          modifier = Modifier.weight(1f).testTag("admin_login_sms_input"),
                          colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MechanicalOrange,
                            unfocusedBorderColor = MetalBorder,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                          )
                        )
                      }

                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        OutlinedTextField(
                          value = loginPhoneInput,
                          onValueChange = { loginPhoneInput = it },
                          label = { Text("Mon Téléphone SMS Super-Admin", fontSize = 10.sp) },
                          singleLine = true,
                          modifier = Modifier.weight(1f),
                          colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TechCyan,
                            unfocusedBorderColor = MetalBorder,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                          )
                        )

                        OutlinedButton(
                          onClick = {
                            val newSms = SuperAdminGlobalControlRegistry.sendNewSmsOtpToSuperAdminPhone(loginPhoneInput)
                            loginSmsInput = newSms
                            loginTotpInput = GoogleAuthenticatorTotpEngine.generateTotpCode(SuperAdminGlobalControlRegistry.SUPER_ADMIN_TOTP_SECRET)
                            login2FaError = null
                          },
                          shape = RoundedCornerShape(10.dp),
                          border = BorderStroke(1.dp, MechanicalOrange),
                          modifier = Modifier.height(50.dp)
                        ) {
                          Text("📲 Recevoir SMS", color = MechanicalOrange, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                        }
                      }

                      Text(
                        text = SuperAdminGlobalControlRegistry.smsSentNotificationText,
                        color = ElectricGreen,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                      )
                    }
                  }

                  login2FaError?.let { err ->
                    Text(
                      text = err,
                      color = PerformanceRedGlow,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                  }

                  // Quick Credentials Helper Box
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                      Text(
                        text = "🔑 IDENTIFIANTS SUPER-ADMIN & EMPLOYÉS PRÉ-CONFIGURÉS :",
                        color = TechCyan,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                      )
                      Text(
                        text = "• Super-Admin Root : badroumc@gmail.com (Mdp : MekanikAdmin2026!)",
                        color = OffWhite,
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace
                      )
                      staffMembers.take(2).forEach { staff ->
                        Text(
                          text = "• Employé (${staff.fullName}) : ${staff.email} (PIN : ${staff.pinCode})",
                          color = AluminumLight,
                          fontSize = 10.sp,
                          fontFamily = FontFamily.Monospace
                        )
                      }
                    }
                  }

                  Button(
                    onClick = {
                      onLoginAdmin(emailInput, passwordInput)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(48.dp)
                      .testTag("btn_submit_admin_login")
                  ) {
                    Icon(Icons.Default.Login, contentDescription = null, tint = PureWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "SE CONNECTER AU PORTAIL SUPER-ADMIN (2FA + SMS)",
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 12.5.sp
                    )
                  }

                  OutlinedButton(
                    onClick = {
                      selectedTab = 7
                      onLoginAdmin("badroumc@gmail.com", "MekanikAdmin2026!")
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ElectricGreen),
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("btn_quick_admin_login")
                  ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "CONNEXION RAPIDE & CLÉS TECDOC / ETKA / STELLANTIS / VIN",
                      color = ElectricGreen,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.5.sp
                    )
                  }
                }
              }
            }
          }
        } else {
          // ====================================================================
          // PORTAIL ADMINISTRATEUR & EMPLOYÉS AUTHENTIFIÉ
          // ====================================================================
          // Active Operator Switcher Bar (Super-Admin Root vs Employés)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF141C24))
              .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "OPÉRATEUR ACTIF : ${adminSession.currentOperatorName}",
              color = TechCyan,
              fontWeight = FontWeight.Black,
              fontSize = 10.5.sp
            )
            if (adminSession.activeStaffOperator != null) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = PerformanceRed,
                modifier = Modifier.clickable { onSwitchAdminOperator(null) }
              ) {
                Text(
                  text = "Revenir Super-Admin Root",
                  color = PureWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.5.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            } else {
              Text(
                text = "${staffMembers.count { it.isActive }} employés actifs",
                color = ElectricGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            }
          }

          // KPI Summary Strip
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(GraphiteBlack)
              .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            AdminKpiBox(
              label = "Inscriptions & Démo",
              value = "$pendingRegistrationsCount à traiter",
              sub = "Démo 3J auto à l'inscription",
              accentColor = if (pendingRegistrationsCount > 0) MechanicalOrange else ElectricGreen,
              modifier = Modifier.weight(1f)
            )
            AdminKpiBox(
              label = "Forfaits & Clés",
              value = "$pendingSubscriptionsCount en attente",
              sub = "${pendingRevenueDzd / 1000}k DA à valider",
              accentColor = if (pendingSubscriptionsCount > 0) PerformanceRedGlow else ElectricGreen,
              modifier = Modifier.weight(1f)
            )
            AdminKpiBox(
              label = "Error Logger",
              value = "${errorLoggerState.logs.size} logs (${errorLoggerState.openCriticalCount} crit.)",
              sub = "Crashs, API & Firebase",
              accentColor = if (errorLoggerState.openCriticalCount > 0) PerformanceRedGlow else ElectricGreen,
              modifier = Modifier
                .weight(1f)
                .clickable { selectedTab = 5 }
                .testTag("kpi_open_error_logger_tab")
            )
          }

          // 6 Navigation Tabs
          ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MetalSurface,
            contentColor = PureWhite,
            edgePadding = 12.dp
          ) {
            Tab(
              selected = selectedTab == 0,
              onClick = { selectedTab = 0 },
              text = {
                Text(
                  "👤 Inscriptions & Démo 3J ($pendingRegistrationsCount)",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.5.sp
                )
              }
            )
            Tab(
              selected = selectedTab == 1,
              onClick = { selectedTab = 1 },
              text = {
                Text(
                  "🔑 Forfaits & Clés ($pendingSubscriptionsCount)",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.5.sp
                )
              }
            )
            Tab(
              selected = selectedTab == 2,
              onClick = { selectedTab = 2 },
              text = {
                Text(
                  "👥 Employés Admin (${staffMembers.size})",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.5.sp
                )
              }
            )
            Tab(
              selected = selectedTab == 3,
              onClick = { selectedTab = 3 },
              text = {
                Text(
                  "🏢 Comptes Actifs (${registrationRequests.count { it.status == AdminApprovalStatus.APPROUVE }})",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.5.sp
                )
              }
            )
            Tab(
              selected = selectedTab == 4,
              onClick = { selectedTab = 4 },
              text = {
                Text(
                  "🛡️ Journal & Audit",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.5.sp
                )
              }
            )
            Tab(
              selected = selectedTab == 5,
              onClick = { selectedTab = 5 },
              modifier = Modifier.testTag("tab_super_admin_error_logger"),
              text = {
                Text(
                  "🚨 Error Logger (${errorLoggerState.logs.size})",
                  fontWeight = FontWeight.Black,
                  fontSize = 11.5.sp,
                  color = if (selectedTab == 5) PerformanceRedGlow else PureWhite
                )
              }
            )
            Tab(
              selected = selectedTab == 6,
              onClick = { selectedTab = 6 },
              modifier = Modifier.testTag("tab_super_admin_feature_toggles"),
              text = {
                Text(
                  "🎛️ Options ON/OFF (${SuperAdminGlobalControlRegistry.featureToggles.count { it.isEnabled }}/${SuperAdminGlobalControlRegistry.featureToggles.size})",
                  fontWeight = FontWeight.Black,
                  fontSize = 11.5.sp,
                  color = if (selectedTab == 6) MechanicalOrange else PureWhite
                )
              }
            )
            Tab(
              selected = selectedTab == 7,
              onClick = { selectedTab = 7 },
              modifier = Modifier.testTag("tab_super_admin_nocode_apis_2fa"),
              text = {
                Text(
                  "🔌 Prix Abonnements, Multi-APIs (TecDoc/ETKA/Stellantis/VIN) & Sous-Comptes (badroumc@gmail.com)",
                  fontWeight = FontWeight.Black,
                  fontSize = 11.5.sp,
                  color = if (selectedTab == 7) ElectricGreen else PureWhite
                )
              }
            )
          }

          // Quick Super-Admin Root Control Bar (Options ON/OFF & APIs Sans Code 2FA + SMS)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF141C24))
              .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = { selectedTab = 6 },
              shape = RoundedCornerShape(8.dp),
              border = BorderStroke(1.dp, MechanicalOrange),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.weight(1f).testTag("quick_btn_superadmin_options_on_off")
            ) {
              Icon(Icons.Default.Tune, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(5.dp))
              Text("Activer/Désactiver Options", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp, maxLines = 1)
            }
            OutlinedButton(
              onClick = { selectedTab = 7 },
              shape = RoundedCornerShape(8.dp),
              border = BorderStroke(1.dp, ElectricGreen),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.weight(1f).testTag("quick_btn_superadmin_nocode_apis_2fa")
            ) {
              Icon(Icons.Default.PhonelinkLock, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(5.dp))
              Text("Clés TecDoc/ETKA/Stellantis/VIN (2FA+SMS)", color = ElectricGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp, maxLines = 1)
            }
          }

          when (selectedTab) {
            0 -> {
              // TAB 0: APPROBATION DES INSCRIPTIONS & COMPTES DÉMO 3 JOURS
              val filteredList = registrationRequests.filter {
                registrationFilter == null || it.status == registrationFilter
              }

              LazyColumn(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                item {
                  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      item {
                        FilterChip(
                          selected = registrationFilter == null,
                          onClick = { registrationFilter = null },
                          label = { Text("Toutes (${registrationRequests.size})", fontSize = 11.sp) }
                        )
                      }
                      item {
                        FilterChip(
                          selected = registrationFilter == AdminApprovalStatus.EN_ATTENTE,
                          onClick = { registrationFilter = AdminApprovalStatus.EN_ATTENTE },
                          label = { Text("En attente", fontSize = 11.sp) }
                        )
                      }
                      item {
                        FilterChip(
                          selected = registrationFilter == AdminApprovalStatus.DEMO_3_JOURS,
                          onClick = { registrationFilter = AdminApprovalStatus.DEMO_3_JOURS },
                          label = { Text("En Démo 3J", fontSize = 11.sp) }
                        )
                      }
                      item {
                        FilterChip(
                          selected = registrationFilter == AdminApprovalStatus.CLE_REQUISE,
                          onClick = { registrationFilter = AdminApprovalStatus.CLE_REQUISE },
                          label = { Text("Clé Requise", fontSize = 11.sp) }
                        )
                      }
                      item {
                        FilterChip(
                          selected = registrationFilter == AdminApprovalStatus.APPROUVE,
                          onClick = { registrationFilter = AdminApprovalStatus.APPROUVE },
                          label = {
                            Text(
                              "Approuvées Pro (${registrationRequests.count { it.status == AdminApprovalStatus.APPROUVE }})",
                              fontSize = 11.sp
                            )
                          }
                        )
                      }
                    }

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      OutlinedButton(
                        onClick = { showAddRegistrationForm = !showAddRegistrationForm },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, TechCyan),
                        modifier = Modifier.weight(1f).testTag("btn_toggle_new_reg_form")
                      ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                          text = if (showAddRegistrationForm) "Fermer Formulaire" else "+ Inscrire (Démo 3J Auto)",
                          color = TechCyan,
                          fontWeight = FontWeight.Bold,
                          fontSize = 10.5.sp
                        )
                      }

                      if (pendingRegistrationsCount > 0) {
                        Button(
                          onClick = onApproveAllPendingRegistrations,
                          shape = RoundedCornerShape(10.dp),
                          colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
                          modifier = Modifier.weight(1f).testTag("btn_approve_all_registrations")
                        ) {
                          Icon(Icons.Default.DoneAll, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(16.dp))
                          Spacer(modifier = Modifier.width(6.dp))
                          Text(
                            text = "Tout Activer ($pendingRegistrationsCount)",
                            color = GraphiteDark,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.5.sp
                          )
                        }
                      }
                    }
                  }
                }

                if (showAddRegistrationForm) {
                  item {
                    NewRegistrationSimulatorCard(
                      onSubmit = { company, manager, email, wilaya, role, rc, plan ->
                        onCreateTestRegistration(company, manager, email, wilaya, role, rc, plan)
                        showAddRegistrationForm = false
                      }
                    )
                  }
                }

                items(filteredList, key = { it.id }) { req ->
                  RegistrationRequestCard(
                    request = req,
                    onApprove = { onApproveRegistration(req.id) },
                    onGenerateActivationKey = { onGenerateActivationKeyForRegistration(req.id) },
                    onReject = { onRejectRegistration(req.id) },
                    onToggleSuspend = { onToggleSuspendRegistration(req.id) }
                  )
                }
              }
            }

            1 -> {
              // TAB 1: APPROBATION DES FORFAITS & CLÉS D'ACTIVATION DZD
              val filteredSubs = planSubscriptionRequests.filter {
                subscriptionFilter == null || it.status == subscriptionFilter
              }

              LazyColumn(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                item {
                  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      item {
                        FilterChip(
                          selected = subscriptionFilter == null,
                          onClick = { subscriptionFilter = null },
                          label = { Text("Tous (${planSubscriptionRequests.size})", fontSize = 11.sp) }
                        )
                      }
                      item {
                        FilterChip(
                          selected = subscriptionFilter == AdminApprovalStatus.EN_ATTENTE,
                          onClick = { subscriptionFilter = AdminApprovalStatus.EN_ATTENTE },
                          label = { Text("À valider ($pendingSubscriptionsCount)", fontSize = 11.sp) }
                        )
                      }
                      item {
                        FilterChip(
                          selected = subscriptionFilter == AdminApprovalStatus.APPROUVE,
                          onClick = { subscriptionFilter = AdminApprovalStatus.APPROUVE },
                          label = {
                            Text(
                              "Actifs (${planSubscriptionRequests.count { it.status == AdminApprovalStatus.APPROUVE }})",
                              fontSize = 11.sp
                            )
                          }
                        )
                      }
                    }

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      OutlinedButton(
                        onClick = { showAddSubscriptionForm = !showAddSubscriptionForm },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MechanicalOrange),
                        modifier = Modifier.weight(1f).testTag("btn_toggle_new_sub_form")
                      ) {
                        Icon(Icons.Default.AddCard, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                          text = if (showAddSubscriptionForm) "Fermer Formulaire" else "+ Demande Forfait",
                          color = MechanicalOrange,
                          fontWeight = FontWeight.Bold,
                          fontSize = 11.sp
                        )
                      }

                      if (pendingSubscriptionsCount > 0) {
                        Button(
                          onClick = onApproveAllPendingSubscriptions,
                          shape = RoundedCornerShape(10.dp),
                          colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
                          modifier = Modifier.weight(1f).testTag("btn_approve_all_subscriptions")
                        ) {
                          Icon(Icons.Default.Verified, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(16.dp))
                          Spacer(modifier = Modifier.width(6.dp))
                          Text(
                            text = "Valider Tout ($pendingSubscriptionsCount)",
                            color = GraphiteDark,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                          )
                        }
                      }
                    }
                  }
                }

                if (showAddSubscriptionForm) {
                  item {
                    NewSubscriptionSimulatorCard(
                      onSubmit = { accountName, planTitle, amountDzd, method, proof, role ->
                        onCreateTestPlanSubscription(accountName, planTitle, amountDzd, method, proof, role)
                        showAddSubscriptionForm = false
                      }
                    )
                  }
                }

                items(filteredSubs, key = { it.id }) { sub ->
                  PlanSubscriptionApprovalCard(
                    sub = sub,
                    onApprove = { onApprovePlanSubscription(sub.id) },
                    onReject = { onRejectPlanSubscription(sub.id) },
                    onExtend = { onExtendPlanSubscription(sub.id) }
                  )
                }
              }
            }

            2 -> {
              // TAB 2: CRÉATION & GESTION DES COMPTES EMPLOYÉS DU SUPER-ADMINISTRATEUR
              LazyColumn(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                item {
                  Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MetalCard),
                    border = BorderStroke(1.2.dp, TechCyan)
                  ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Column(modifier = Modifier.weight(1f)) {
                          Text(
                            text = "ÉQUIPE EMPLOYÉS DU SUPER-ADMINISTRATEUR",
                            color = TechCyan,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.5.sp
                          )
                          Text(
                            text = "Créez des comptes pour vos employés afin qu'ils traitent les demandes, valident les paiements et génèrent les clés d'activation.",
                            color = AluminumLight,
                            fontSize = 11.sp
                          )
                        }
                      }

                      Button(
                        onClick = { showCreateStaffForm = !showCreateStaffForm },
                        colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                          .fillMaxWidth()
                          .testTag("btn_toggle_create_staff_form")
                      ) {
                        Icon(Icons.Default.PersonAddAlt1, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                          text = if (showCreateStaffForm) "FERMER LE FORMULAIRE EMPLOYÉ" else "+ CRÉER UN NOUVEAU COMPTE EMPLOYÉ",
                          color = GraphiteDark,
                          fontWeight = FontWeight.Black,
                          fontSize = 11.5.sp
                        )
                      }
                    }
                  }
                }

                if (showCreateStaffForm) {
                  item {
                    CreateAdminStaffFormCard(
                      onSubmit = { fullName, email, pinCode, role, wilayas, canReg, canKey, canPay, canSusp ->
                        onCreateAdminStaffMember(fullName, email, pinCode, role, wilayas, canReg, canKey, canPay, canSusp)
                        showCreateStaffForm = false
                      }
                    )
                  }
                }

                items(staffMembers, key = { it.id }) { staff ->
                  val isCurrentOperator = adminSession.activeStaffOperator?.id == staff.id
                  AdminStaffMemberCard(
                    staff = staff,
                    isCurrentOperator = isCurrentOperator,
                    onToggleActive = { onToggleStaffActive(staff.id) },
                    onSwitchToStaffSession = {
                      if (isCurrentOperator) {
                        onSwitchAdminOperator(null)
                      } else {
                        onSwitchAdminOperator(staff)
                      }
                    }
                  )
                }
              }
            }

            3 -> {
              // TAB 3: PARC UTILISATEURS & BASCULE RAPIDE DE PROFIL
              LazyColumn(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                item {
                  Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MetalCard),
                    border = BorderStroke(1.dp, TechCyan)
                  ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                      Text(
                        text = "INSPECTION SUPER-ADMIN DES INTERFACES PAR RÔLE",
                        color = TechCyan,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                      )
                      Text(
                        text = "En tant qu'administrateur, basculez instantanément sur n'importe quel type de compte pour vérifier son interface :",
                        color = AluminumLight,
                        fontSize = 11.sp
                      )
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                      ) {
                        AccountRole.entries.forEach { role ->
                          Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GraphiteBlack,
                            border = BorderStroke(1.dp, MetalBorder),
                            modifier = Modifier
                              .weight(1f)
                              .clickable { onSwitchToRolePreview(role) }
                          ) {
                            Column(
                              modifier = Modifier.padding(8.dp),
                              horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                              Text(role.iconEmoji, fontSize = 14.sp)
                              Spacer(modifier = Modifier.height(2.dp))
                              Text(
                                text = role.shortTitle,
                                color = PureWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp,
                                maxLines = 1
                              )
                            }
                          }
                        }
                      }
                    }
                  }
                }

                items(registrationRequests, key = { "active_${it.id}" }) { account ->
                  Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MetalCard),
                    border = BorderStroke(
                      1.dp,
                      if (account.status == AdminApprovalStatus.APPROUVE) ElectricGreen.copy(alpha = 0.5f) else MetalBorder
                    )
                  ) {
                    Row(
                      modifier = Modifier.padding(12.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = "${account.requestedRole.iconEmoji} ${account.companyOrFullName}",
                          color = PureWhite,
                          fontWeight = FontWeight.Bold,
                          fontSize = 12.5.sp
                        )
                        Text(
                          text = "${account.email} • ${account.wilaya}",
                          color = AluminumLight,
                          fontSize = 10.5.sp
                        )
                        Text(
                          text = "Clé Licence : ${account.generatedLicenseKey ?: "Essai Démo 3J (${account.demoTrialRemainingHours}h)"}",
                          color = TechCyan,
                          fontSize = 10.sp,
                          fontFamily = FontFamily.Monospace
                        )
                      }
                      OutlinedButton(
                        onClick = { onToggleSuspendRegistration(account.id) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                      ) {
                        Text(
                          text = if (account.status == AdminApprovalStatus.SUSPENDU) "Réactiver" else "Suspendre",
                          color = if (account.status == AdminApprovalStatus.SUSPENDU) ElectricGreen else MechanicalOrange,
                          fontWeight = FontWeight.Bold,
                          fontSize = 10.5.sp
                        )
                      }
                    }
                  }
                }
              }
            }

            4 -> {
              // TAB 4: JOURNAL D'AUDIT & TRAÇABILITÉ EMPLOYÉS / ROOT
              LazyColumn(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                item {
                  Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MetalCard),
                    border = BorderStroke(1.dp, PerformanceRed)
                  ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                      Text(
                        text = "FICHE SESSION OPÉRATEUR EN COURS",
                        color = PerformanceRedGlow,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                      )
                      Text("Opérateur actif : ${adminSession.currentOperatorName}", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                      Text("Email : ${adminSession.currentOperatorEmail}", color = TechCyan, fontSize = 11.5.sp, fontFamily = FontFamily.Monospace)
                      Text("Niveau : ${adminSession.currentOperatorBadge}", color = ElectricGreen, fontSize = 11.sp)
                      Text("Compte Fondateur Root : ${adminSession.adminEmail}", color = AluminumGray, fontSize = 10.5.sp)
                    }
                  }
                }

                item {
                  Text(
                    text = "JOURNAL D'AUDIT DES APPROBATIONS & CLÉS (${auditLogs.size})",
                    color = AluminumMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                }

                items(auditLogs, key = { it.id }) { log ->
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
                        Text(log.actionTitle, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(log.timestamp, color = TechCyan, fontSize = 10.sp)
                      }
                      Text(
                        text = "Par : ${log.adminEmail} (${log.operatorBadge}) • Cible : ${log.targetEntity}",
                        color = MechanicalOrange,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.5.sp
                      )
                      Text(log.impactSummary, color = AluminumLight, fontSize = 10.5.sp)
                    }
                  }
                }
              }
            }

            5 -> {
              // TAB 5: ERROR LOGGER CENTRALISÉ FIREBASE (PLANTAGES, CONNEXION API, PERSISTANCE LOCALE)
              SuperAdminErrorLoggerView(
                errorLoggerState = errorLoggerState,
                onSelectCategoryFilter = onSelectErrorCategoryFilter,
                onSelectSeverityFilter = onSelectErrorSeverityFilter,
                onSelectStatusFilter = onSelectErrorStatusFilter,
                onSearchQueryChange = onSearchErrorLogs,
                onSimulateCrashLog = onSimulateCrashLog,
                onSimulateApiFailureLog = onSimulateApiFailureLog,
                onSimulatePersistenceFailureLog = onSimulatePersistenceFailureLog,
                onSimulateClientBugReport = onSimulateClientBugReport,
                onToggleAiAutoRepair = onToggleAiAutoRepair,
                onRepairErrorWithAi = onRepairErrorWithAi,
                onRepairAllOpenErrorsWithAi = onRepairAllOpenErrorsWithAi,
                onUpdateErrorStatusAndNote = onUpdateErrorStatusAndNote,
                onDeleteErrorLog = onDeleteErrorLog,
                onPurgeResolvedLogs = onPurgeResolvedErrorLogs
              )
            }

            6 -> {
              // TAB 6: CONTRÔLE GLOBAL DES OPTIONS (ACTIVER / DÉSACTIVER TOUTES LES OPTIONS)
              SuperAdminGlobalFeatureTogglesSection(
                isSuperAdminRoot = adminSession.activeStaffOperator == null
              )
            }

            7 -> {
              // TAB 7: GESTIONNAIRE MULTI-API SANS CODE, PRIX ABONNEMENTS & SOUS-COMPTES • RÉSERVÉ À badroumc@gmail.com
              SuperAdminNoCodeApiVaultSection(
                isSuperAdminRootOperator = adminSession.activeStaffOperator == null,
                superAdminEmail = adminSession.adminEmail
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun AdminStaffMemberCard(
  staff: AdminStaffMember,
  isCurrentOperator: Boolean,
  onToggleActive: () -> Unit,
  onSwitchToStaffSession: () -> Unit
) {
  val borderColor = when {
    isCurrentOperator -> ElectricGreen
    staff.isActive -> TechCyan.copy(alpha = 0.7f)
    else -> MetalBorder
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("staff_card_${staff.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.2.dp, borderColor)
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "👤 ${staff.fullName}",
              color = PureWhite,
              fontWeight = FontWeight.Black,
              fontSize = 13.5.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = TechCyan.copy(alpha = 0.18f),
              border = BorderStroke(1.dp, TechCyan)
            ) {
              Text(
                text = staff.role.badgeLabel,
                color = TechCyan,
                fontWeight = FontWeight.Black,
                fontSize = 8.5.sp,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }
          }
          Text(
            text = "${staff.email} • PIN : ${staff.pinCode} • ${staff.lastActiveLabel}",
            color = AluminumLight,
            fontSize = 10.5.sp,
            fontFamily = FontFamily.Monospace
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (staff.isActive) ElectricGreen.copy(alpha = 0.2f) else PerformanceRed.copy(alpha = 0.2f),
          border = BorderStroke(1.dp, if (staff.isActive) ElectricGreen else PerformanceRed)
        ) {
          Text(
            text = if (isCurrentOperator) "EN SERVICE ACTIF" else if (staff.isActive) "ACTIF" else "SUSPENDU",
            color = if (staff.isActive) ElectricGreen else PerformanceRed,
            fontWeight = FontWeight.Black,
            fontSize = 8.5.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(10.dp),
        color = GraphiteBlack,
        border = BorderStroke(1.dp, MetalBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text("Périmètre : ${staff.assignedWilayas}", color = OffWhite, fontSize = 10.5.sp)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Dossiers traités : ${staff.processedRequestsCount}",
              color = ElectricGreen,
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp
            )
            Text(
              text = "Clés générées : ${staff.generatedKeysCount}",
              color = MechanicalOrange,
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp
            )
          }
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (staff.canApproveRegistrations) {
              Text("✓ Inscriptions", color = TechCyan, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
            }
            if (staff.canGenerateActivationKeys) {
              Text("✓ Clés Activation", color = ElectricGreen, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
            }
            if (staff.canValidatePaymentsDzd) {
              Text("✓ Paiements DZD", color = MechanicalOrange, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
            }
            if (staff.canSuspendAccounts) {
              Text("✓ Suspension", color = PerformanceRedGlow, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onSwitchToStaffSession,
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isCurrentOperator) PerformanceRed else TechCyan
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.weight(1.3f)
        ) {
          Icon(
            if (isCurrentOperator) Icons.Default.ExitToApp else Icons.Default.SwitchAccount,
            contentDescription = null,
            tint = if (isCurrentOperator) PureWhite else GraphiteDark,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isCurrentOperator) "QUITTER SESSION EMPLOYÉ" else "AGIR EN TANT QUE CET EMPLOYÉ",
            color = if (isCurrentOperator) PureWhite else GraphiteDark,
            fontWeight = FontWeight.Black,
            fontSize = 10.sp
          )
        }

        OutlinedButton(
          onClick = onToggleActive,
          shape = RoundedCornerShape(10.dp),
          border = BorderStroke(1.dp, if (staff.isActive) MechanicalOrange else ElectricGreen),
          modifier = Modifier.weight(0.7f)
        ) {
          Text(
            text = if (staff.isActive) "Suspendre" else "Activer",
            color = if (staff.isActive) MechanicalOrange else ElectricGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 10.5.sp
          )
        }
      }
    }
  }
}

@Composable
private fun CreateAdminStaffFormCard(
  onSubmit: (
    fullName: String,
    email: String,
    pinCode: String,
    role: AdminStaffRole,
    wilayas: String,
    canApproveReg: Boolean,
    canGenerateKey: Boolean,
    canValidatePay: Boolean,
    canSuspend: Boolean
  ) -> Unit
) {
  var fullName by remember { mutableStateOf("Lyes Hamidi") }
  var email by remember { mutableStateOf("lyes.activation@mekanik.dz") }
  var pinCode by remember { mutableStateOf("4016") }
  var wilayas by remember { mutableStateOf("Toutes Wilayas (Support & Clés Activation)") }
  var selectedRole by remember { mutableStateOf(AdminStaffRole.GESTIONNAIRE_LICENCES_DZD) }
  var canApproveReg by remember { mutableStateOf(true) }
  var canGenerateKey by remember { mutableStateOf(true) }
  var canValidatePay by remember { mutableStateOf(true) }
  var canSuspend by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
    border = BorderStroke(1.2.dp, TechCyan)
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        text = "CRÉER UN COMPTE EMPLOYÉ ADMINISTRATEUR",
        color = TechCyan,
        fontWeight = FontWeight.Black,
        fontSize = 12.sp
      )

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        AdminStaffRole.entries.forEach { role ->
          val selected = selectedRole == role
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (selected) TechCyan else MetalCard,
            modifier = Modifier
              .weight(1f)
              .clickable { selectedRole = role }
          ) {
            Text(
              text = role.badgeLabel,
              color = if (selected) GraphiteDark else PureWhite,
              fontWeight = FontWeight.Bold,
              fontSize = 8.5.sp,
              modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
            )
          }
        }
      }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = fullName,
          onValueChange = { fullName = it },
          label = { Text("Nom & Prénom Employé", fontSize = 10.sp) },
          singleLine = true,
          modifier = Modifier.weight(1.2f)
        )
        OutlinedTextField(
          value = pinCode,
          onValueChange = { pinCode = it },
          label = { Text("PIN Connexion", fontSize = 10.sp) },
          singleLine = true,
          modifier = Modifier.weight(0.8f)
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("Email Professionnel", fontSize = 10.sp) },
          singleLine = true,
          modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
          value = wilayas,
          onValueChange = { wilayas = it },
          label = { Text("Wilayas attribuées", fontSize = 10.sp) },
          singleLine = true,
          modifier = Modifier.weight(1f)
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        FilterChip(
          selected = canApproveReg,
          onClick = { canApproveReg = !canApproveReg },
          label = { Text("Inscriptions", fontSize = 10.sp) }
        )
        FilterChip(
          selected = canGenerateKey,
          onClick = { canGenerateKey = !canGenerateKey },
          label = { Text("Clés Activation", fontSize = 10.sp) }
        )
        FilterChip(
          selected = canValidatePay,
          onClick = { canValidatePay = !canValidatePay },
          label = { Text("Forfaits DZD", fontSize = 10.sp) }
        )
      }

      Button(
        onClick = {
          if (fullName.isNotBlank() && email.isNotBlank()) {
            onSubmit(
              fullName,
              email,
              pinCode.ifBlank { "2026" },
              selectedRole,
              wilayas,
              canApproveReg,
              canGenerateKey,
              canValidatePay,
              canSuspend
            )
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("btn_confirm_create_staff")
      ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "CRÉER LE COMPTE EMPLOYÉ & ACTIVER SES DROITS",
          color = GraphiteDark,
          fontWeight = FontWeight.Black,
          fontSize = 11.sp
        )
      }
    }
  }
}

@Composable
private fun AdminKpiBox(
  label: String,
  value: String,
  sub: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    color = MetalCard,
    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.padding(8.dp)) {
      Text(label, color = AluminumMuted, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
      Text(value, color = accentColor, fontWeight = FontWeight.Black, fontSize = 12.sp, maxLines = 1)
      Text(sub, color = AluminumLight, fontSize = 9.sp, maxLines = 1)
    }
  }
}

@Composable
private fun RegistrationRequestCard(
  request: UserRegistrationRequest,
  onApprove: () -> Unit,
  onGenerateActivationKey: () -> Unit,
  onReject: () -> Unit,
  onToggleSuspend: () -> Unit
) {
  val statusColor = when (request.status) {
    AdminApprovalStatus.EN_ATTENTE -> MechanicalOrange
    AdminApprovalStatus.DEMO_3_JOURS -> TechCyan
    AdminApprovalStatus.CLE_REQUISE -> PerformanceRedGlow
    AdminApprovalStatus.APPROUVE -> ElectricGreen
    AdminApprovalStatus.REFUSE -> PerformanceRed
    AdminApprovalStatus.SUSPENDU -> WarningYellow
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("reg_card_${request.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.2.dp, statusColor.copy(alpha = 0.7f))
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "${request.requestedRole.iconEmoji} ${request.companyOrFullName}",
              color = PureWhite,
              fontWeight = FontWeight.Black,
              fontSize = 13.5.sp
            )
          }
          Text(
            text = "Gérant : ${request.managerName} • ${request.wilaya}",
            color = AluminumLight,
            fontSize = 11.sp
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = statusColor.copy(alpha = 0.18f),
          border = BorderStroke(1.dp, statusColor)
        ) {
          Text(
            text = request.status.badgeText,
            color = statusColor,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
          )
        }
      }

      // Details Box
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = GraphiteBlack,
        border = BorderStroke(1.dp, MetalBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Profil : ${request.requestedRole.label}", color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
            Text(request.submittedDate, color = AluminumMuted, fontSize = 10.sp)
          }
          Text("Contact : ${request.email} • Tél : ${request.phone}", color = OffWhite, fontSize = 10.5.sp)
          Text(
            text = "Vérification légale : ${request.rcNumber} | ${request.nifNumber}",
            color = WarningAmber,
            fontSize = 10.5.sp,
            fontFamily = FontFamily.Monospace
          )
          Text(
            text = "Essai Démo 3 Jours : Attribué automatiquement à l'inscription (${request.demoTrialRemainingHours}h / 72h)",
            color = TechCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
          Text(
            text = "Forfait demandé : ${request.requestedInitialPlanName}",
            color = MechanicalOrange,
            fontWeight = FontWeight.Bold,
            fontSize = 10.5.sp
          )
          if (request.generatedLicenseKey != null) {
            Text(
              text = "🔑 Clé d'Activation Pro : ${request.generatedLicenseKey}",
              color = ElectricGreen,
              fontWeight = FontWeight.Black,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }
          if (request.processedByOperator != null) {
            Text(
              text = "Traité par : ${request.processedByOperator}",
              color = AluminumLight,
              fontSize = 10.sp
            )
          }
        }
      }

      if (request.adminNote.isNotBlank()) {
        Text(
          text = "Note dossier : ${request.adminNote}",
          color = AluminumGray,
          fontSize = 10.5.sp
        )
      }

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (request.status != AdminApprovalStatus.APPROUVE) {
          Button(
            onClick = onApprove,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
            modifier = Modifier.weight(1.2f).testTag("btn_approve_reg_${request.id}")
          ) {
            Icon(Icons.Default.VpnKey, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = "ACTIVER & GÉNÉRER CLÉ",
              color = GraphiteDark,
              fontWeight = FontWeight.Black,
              fontSize = 10.sp
            )
          }
        } else {
          OutlinedButton(
            onClick = onGenerateActivationKey,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, TechCyan),
            modifier = Modifier.weight(1.1f)
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = TechCyan, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("NOUVELLE CLÉ", color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 10.sp)
          }
        }

        if (request.status == AdminApprovalStatus.EN_ATTENTE || request.status == AdminApprovalStatus.DEMO_3_JOURS) {
          OutlinedButton(
            onClick = onReject,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, PerformanceRed),
            modifier = Modifier.weight(0.8f).testTag("btn_reject_reg_${request.id}")
          ) {
            Icon(Icons.Default.Cancel, contentDescription = null, tint = PerformanceRed, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("REFUSER", color = PerformanceRed, fontWeight = FontWeight.Bold, fontSize = 10.sp)
          }
        } else {
          OutlinedButton(
            onClick = onToggleSuspend,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, MechanicalOrange),
            modifier = Modifier.weight(0.9f)
          ) {
            Text(
              text = if (request.status == AdminApprovalStatus.SUSPENDU) "RÉACTIVER" else "SUSPENDRE",
              color = MechanicalOrange,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun PlanSubscriptionApprovalCard(
  sub: PlanSubscriptionRequest,
  onApprove: () -> Unit,
  onReject: () -> Unit,
  onExtend: () -> Unit
) {
  val statusColor = when (sub.status) {
    AdminApprovalStatus.EN_ATTENTE -> MechanicalOrange
    AdminApprovalStatus.DEMO_3_JOURS -> TechCyan
    AdminApprovalStatus.CLE_REQUISE -> PerformanceRedGlow
    AdminApprovalStatus.APPROUVE -> ElectricGreen
    AdminApprovalStatus.REFUSE -> PerformanceRed
    AdminApprovalStatus.SUSPENDU -> WarningYellow
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("sub_card_${sub.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.2.dp, statusColor.copy(alpha = 0.7f))
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = sub.planTitle,
            color = PureWhite,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
          )
          Text(
            text = "${sub.accountRole.iconEmoji} ${sub.accountName} • ${sub.wilaya}",
            color = AluminumLight,
            fontSize = 11.sp
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = statusColor.copy(alpha = 0.18f),
          border = BorderStroke(1.dp, statusColor)
        ) {
          Text(
            text = sub.status.badgeText,
            color = statusColor,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
          )
        }
      }

      // Payment & Plan Details
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = GraphiteBlack,
        border = BorderStroke(1.dp, MetalBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${sub.amountDzd} DA",
              color = ElectricGreen,
              fontWeight = FontWeight.Black,
              fontSize = 16.sp
            )
            Surface(
              shape = RoundedCornerShape(5.dp),
              color = TechCyan.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, TechCyan)
            ) {
              Text(
                text = sub.paymentMethod.shortTag,
                color = TechCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 9.5.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Text(
            text = "Réf. Paiement : ${sub.paymentProofReference}",
            color = WarningAmber,
            fontSize = 10.5.sp,
            fontFamily = FontFamily.Monospace
          )

          if (sub.generatedActivationKey != null) {
            Text(
              text = "🔑 Clé d'Activation : ${sub.generatedActivationKey}",
              color = ElectricGreen,
              fontWeight = FontWeight.Black,
              fontSize = 10.5.sp,
              fontFamily = FontFamily.Monospace
            )
          }

          if (sub.processedByOperator != null) {
            Text(
              text = "Validé par : ${sub.processedByOperator}",
              color = TechCyan,
              fontSize = 10.sp
            )
          }

          Text(
            text = "Demande du ${sub.submittedDate} • Validité : ${sub.validUntilDate}",
            color = AluminumMuted,
            fontSize = 10.sp
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (sub.status != AdminApprovalStatus.APPROUVE) {
          Button(
            onClick = onApprove,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
            modifier = Modifier.weight(1.4f).testTag("btn_approve_sub_${sub.id}")
          ) {
            Icon(Icons.Default.Verified, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "VALIDER PAIEMENT & ENVOYER CLÉ",
              color = GraphiteDark,
              fontWeight = FontWeight.Black,
              fontSize = 10.sp
            )
          }
        }

        if (sub.status == AdminApprovalStatus.EN_ATTENTE) {
          OutlinedButton(
            onClick = onReject,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, PerformanceRed),
            modifier = Modifier.weight(0.8f).testTag("btn_reject_sub_${sub.id}")
          ) {
            Text("REJETER", color = PerformanceRed, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
          }
        } else {
          OutlinedButton(
            onClick = onExtend,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, TechCyan),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.Update, contentDescription = null, tint = TechCyan, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("PROLONGER (+1 AN)", color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
          }
        }
      }
    }
  }
}

@Composable
private fun NewRegistrationSimulatorCard(
  onSubmit: (
    companyName: String,
    managerName: String,
    email: String,
    wilaya: String,
    role: AccountRole,
    rcNumber: String,
    planName: String
  ) -> Unit
) {
  var companyName by remember { mutableStateOf("Garage Diagnostic Turbo Bejaia") }
  var managerName by remember { mutableStateOf("Nassim Meziani") }
  var email by remember { mutableStateOf("nassim.turbo06@gmail.com") }
  var wilaya by remember { mutableStateOf("06 - Béjaïa") }
  var rcNumber by remember { mutableStateOf("RC 06/00-5518293B25") }
  var selectedRole by remember { mutableStateOf(AccountRole.GARAGE_PRO) }
  var planName by remember { mutableStateOf("Forfait Atelier Pro (18 000 DA/an)") }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
    border = BorderStroke(1.dp, TechCyan)
  ) {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        text = "SIMULER UNE INSCRIPTION (AVEC COMPTE DÉMO 3 JOURS AUTOMATIQUE)",
        color = TechCyan,
        fontWeight = FontWeight.Black,
        fontSize = 11.sp
      )

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        AccountRole.entries.forEach { role ->
          val selected = selectedRole == role
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (selected) PerformanceRed else MetalCard,
            modifier = Modifier
              .weight(1f)
              .clickable { selectedRole = role }
          ) {
            Text(
              text = role.shortTitle,
              color = PureWhite,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
            )
          }
        }
      }

      OutlinedTextField(
        value = companyName,
        onValueChange = { companyName = it },
        label = { Text("Nom Atelier / Magasin / Client", fontSize = 10.sp) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
      )

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = managerName,
          onValueChange = { managerName = it },
          label = { Text("Gérant", fontSize = 10.sp) },
          singleLine = true,
          modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
          value = wilaya,
          onValueChange = { wilaya = it },
          label = { Text("Wilaya", fontSize = 10.sp) },
          singleLine = true,
          modifier = Modifier.weight(1f)
        )
      }

      Button(
        onClick = {
          if (companyName.isNotBlank()) {
            onSubmit(companyName, managerName, email, wilaya, selectedRole, rcNumber, planName)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = "INSCRIRE & ACTIVER DÉMO 3 JOURS (72H)",
          color = GraphiteDark,
          fontWeight = FontWeight.Black,
          fontSize = 11.sp
        )
      }
    }
  }
}

@Composable
private fun NewSubscriptionSimulatorCard(
  onSubmit: (
    accountName: String,
    planTitle: String,
    amountDzd: Int,
    paymentMethod: PaymentMethodAdminType,
    proofRef: String,
    role: AccountRole
  ) -> Unit
) {
  var accountName by remember { mutableStateOf("Garage Diagnostic Turbo Bejaia") }
  var planTitle by remember { mutableStateOf("Forfait Atelier Pro + Passerelle Magasin (23 000 DA)") }
  var amountText by remember { mutableStateOf("23000") }
  var proofRef by remember { mutableStateOf("Reçu BaridiMob #BM-2026-99281") }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
    border = BorderStroke(1.dp, MechanicalOrange)
  ) {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        text = "CRÉER UNE DEMANDE D'APPROBATION DE FORFAIT",
        color = MechanicalOrange,
        fontWeight = FontWeight.Black,
        fontSize = 11.5.sp
      )

      OutlinedTextField(
        value = accountName,
        onValueChange = { accountName = it },
        label = { Text("Compte demandeur", fontSize = 10.sp) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
      )

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = planTitle,
          onValueChange = { planTitle = it },
          label = { Text("Forfait choisi", fontSize = 10.sp) },
          singleLine = true,
          modifier = Modifier.weight(1.4f)
        )
        OutlinedTextField(
          value = amountText,
          onValueChange = { amountText = it },
          label = { Text("Montant DA", fontSize = 10.sp) },
          singleLine = true,
          modifier = Modifier.weight(0.8f)
        )
      }

      Button(
        onClick = {
          val amt = amountText.toIntOrNull() ?: 18000
          onSubmit(
            accountName,
            planTitle,
            amt,
            PaymentMethodAdminType.BARIDIMOB_CCP,
            proofRef,
            AccountRole.GARAGE_PRO
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = "SOUMETTRE POUR VALIDATION ADMIN",
          color = PureWhite,
          fontWeight = FontWeight.Black,
          fontSize = 11.sp
        )
      }
    }
  }
}
