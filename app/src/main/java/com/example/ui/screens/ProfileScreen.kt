package com.example.ui.screens

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
import com.example.data.model.AccountRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

@Composable
fun ProfileScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  onNavigateToFleet: () -> Unit,
  onNavigateToLiveTracking: () -> Unit,
  onNavigateToObd: () -> Unit,
  onNavigateToSubscriptions: () -> Unit,
  onNavigateToDocs: () -> Unit,
  onNavigateToVendors: () -> Unit,
  onNavigateToDriverSpace: () -> Unit,
  onNavigateToCarRental: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currentRole = uiState.currentAccountRole

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("profile_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 00. Compte Super-Administrateur (badroumc@gmail.com) — Approbation Inscriptions & Forfaits
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("profile_super_admin_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = adaptiveBannerSurface(0xFF261014, 0xFFFFF1F2)),
        border = BorderStroke(1.5.dp, PerformanceRedGlow)
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
                  .size(44.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(PerformanceRed.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.AdminPanelSettings,
                  contentDescription = null,
                  tint = PerformanceRedGlow,
                  modifier = Modifier.size(26.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "COMPTE SUPER-ADMINISTRATEUR",
                  style = MaterialTheme.typography.titleSmall.copy(
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.5.sp
                  )
                )
                Text(
                  text = "${uiState.superAdminSession.currentOperatorName} (${uiState.superAdminSession.currentOperatorBadge}) • ${uiState.adminStaffMembers.size} employés",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = TechCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (uiState.superAdminSession.isAuthenticated) ElectricGreen.copy(alpha = 0.2f) else MechanicalOrange.copy(alpha = 0.2f),
              border = BorderStroke(1.dp, if (uiState.superAdminSession.isAuthenticated) ElectricGreen else MechanicalOrange)
            ) {
              Text(
                text = if (uiState.superAdminSession.isAuthenticated) "CONNECTÉ" else "VERROUILLÉ",
                color = if (uiState.superAdminSession.isAuthenticated) ElectricGreen else MechanicalOrange,
                fontWeight = FontWeight.Black,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
              )
            }
          }

          Text(
            text = "Gérez les demandes d'inscription, générez les clés d'activation après les 3 jours d'essai démo, validez les forfaits DZD et créez des comptes pour vos employés administratifs.",
            style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.5.sp)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { viewModel.openSuperAdminModal() },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
              modifier = Modifier
                .weight(1.4f)
                .testTag("btn_open_super_admin_from_profile")
            ) {
              Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = PureWhite, modifier = Modifier.size(17.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "CONSOLE ADMIN & ÉQUIPE (${uiState.pendingAdminApprovalsCount})",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 10.5.sp
              )
            }

            OutlinedButton(
              onClick = { viewModel.openDemoTrialActivationModal() },
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, MechanicalOrange),
              modifier = Modifier
                .weight(1f)
                .testTag("btn_open_demo_key_from_profile")
            ) {
              Icon(
                Icons.Default.VpnKey,
                contentDescription = null,
                tint = MechanicalOrange,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "DÉMO 3J & CLÉ",
                color = MechanicalOrange,
                fontWeight = FontWeight.Black,
                fontSize = 10.5.sp
              )
            }
          }

          OutlinedButton(
            onClick = { viewModel.openSuperAdminErrorLogger() },
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.2.dp, TechCyan),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("btn_open_super_admin_error_logger")
          ) {
            Icon(
              Icons.Default.BugReport,
              contentDescription = null,
              tint = TechCyan,
              modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "🚨 JOURNAL D'ERREURS & AUTO-RÉPARATION IA (FIREBASE)",
              color = TechCyan,
              fontWeight = FontWeight.Black,
              fontSize = 10.sp
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { viewModel.openClientBugAiRepairModal() },
              colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("btn_open_ai_auto_repair_from_profile")
            ) {
              Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "🤖 RÉPARATION IA / BUG",
                color = GraphiteDark,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp
              )
            }

            Button(
              onClick = { viewModel.openSmartCartFreeAccessMapModal(3) },
              colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("btn_open_particulier_account_from_profile")
            ) {
              Icon(Icons.Default.Person, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "👤 PARTICULIER 3 500 DA",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp
              )
            }
          }
        }
      }
    }

    // 0-A. Mode d'Affichage : Automatique Téléphone / Sombre / Jour / Clair
    item {
      val context = androidx.compose.ui.platform.LocalContext.current
      val currentThemeMode = MekanikThemeController.selectedMode
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("profile_display_theme_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MetalSurface),
        border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.55f))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "🎨 APPARENCE & MODES D'AFFICHAGE",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = MechanicalOrange,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp
                )
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = if (MekanikThemeController.isLightModeActive) {
                  "☀️ Mode Clair actif : fond clair haute visibilité et écriture noire automatique sur tous les écrans."
                } else {
                  "🌙 Mode Sombre actif : fond graphite et écriture blanche automatique (synchronisable avec le téléphone)."
                },
                color = AluminumLight,
                fontSize = 11.sp,
                lineHeight = 15.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            AppDisplayThemeMode.entries.forEach { mode ->
              val isModeSelected = currentThemeMode == mode
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isModeSelected) MechanicalOrange else MetalCard,
                border = BorderStroke(
                  1.dp,
                  if (isModeSelected) MechanicalOrangeGlow else MetalBorder
                ),
                onClick = { MekanikThemeController.setDisplayThemeMode(mode, context) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("profile_theme_mode_${mode.name.lowercase()}")
              ) {
                Column(
                  modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(text = mode.emoji, fontSize = 16.sp)
                  Spacer(modifier = Modifier.height(3.dp))
                  Text(
                    text = mode.shortLabel,
                    color = if (isModeSelected) PureWhite else OffWhite,
                    fontSize = 10.sp,
                    fontWeight = if (isModeSelected) FontWeight.Black else FontWeight.SemiBold,
                    maxLines = 1
                  )
                  Text(
                    text = mode.subtitle,
                    color = if (isModeSelected) PureWhite.copy(alpha = 0.85f) else AluminumMuted,
                    fontSize = 8.sp,
                    maxLines = 1
                  )
                }
              }
            }
          }
        }
      }
    }

    // 0. Account Category / Role Selector (Atelier Pro / Conducteur / Location de Voiture)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MetalSurface),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "SÉLECTIONNER VOTRE PROFIL DE COMPTE",
            style = MaterialTheme.typography.labelSmall.copy(
              color = AluminumMuted,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          )
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            AccountRole.values().forEach { role ->
              val isSelected = currentRole == role
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) PerformanceRed else MetalCard,
                border = BorderStroke(
                  1.dp,
                  if (isSelected) PerformanceRedGlow else MetalBorder
                ),
                modifier = Modifier
                  .weight(1f)
                  .clickable { viewModel.setAccountRole(role) }
              ) {
                Column(
                  modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Icon(
                    imageVector = when (role) {
                      AccountRole.GARAGE_PRO -> Icons.Default.Handyman
                      AccountRole.VENDEUR_PIECES -> Icons.Default.Storefront
                      AccountRole.ATELIER_MAGASIN_MIXTE -> Icons.Default.PrecisionManufacturing
                      AccountRole.ATELIER_CARROSSERIE -> Icons.Default.CarRepair
                      AccountRole.CONDUCTEUR -> Icons.Default.DirectionsCar
                      AccountRole.LOCATION_VOITURES -> Icons.Default.CarRental
                    },
                    contentDescription = null,
                    tint = if (isSelected) PureWhite else AluminumLight,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = role.shortTitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = if (isSelected) PureWhite else AluminumLight,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.5.sp
                    )
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = currentRole.description,
            style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.sp)
          )
        }
      }
    }

    // 1. Dynamic User Header Card based on active Account Role
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                  when (currentRole) {
                    AccountRole.GARAGE_PRO -> PerformanceRedDark
                    AccountRole.VENDEUR_PIECES -> MechanicalOrangeDark
                    AccountRole.ATELIER_MAGASIN_MIXTE -> MechanicalOrangeDark
                    AccountRole.ATELIER_CARROSSERIE -> PerformanceRedDark
                    AccountRole.CONDUCTEUR -> EvBlue.copy(alpha = 0.4f)
                    AccountRole.LOCATION_VOITURES -> MechanicalOrangeDark
                  }
                )
                .border(2.dp, PerformanceRedGlow, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = when (currentRole) {
                  AccountRole.GARAGE_PRO -> Icons.Default.Person
                  AccountRole.VENDEUR_PIECES -> Icons.Default.Storefront
                  AccountRole.ATELIER_MAGASIN_MIXTE -> Icons.Default.PrecisionManufacturing
                  AccountRole.ATELIER_CARROSSERIE -> Icons.Default.CarRepair
                  AccountRole.CONDUCTEUR -> Icons.Default.DirectionsCar
                  AccountRole.LOCATION_VOITURES -> Icons.Default.CarRental
                },
                contentDescription = "Avatar",
                tint = PureWhite,
                modifier = Modifier.size(34.dp)
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = when (currentRole) {
                    AccountRole.GARAGE_PRO -> "Badreddine Mekanik"
                    AccountRole.VENDEUR_PIECES -> "Sarl Auto Pièces Alger"
                    AccountRole.ATELIER_MAGASIN_MIXTE -> "Auto Pièces & Montage Express"
                    AccountRole.ATELIER_CARROSSERIE -> "Carrosserie & Tôlerie Pro Alger"
                    AccountRole.CONDUCTEUR -> "Karim Mansouri"
                    AccountRole.LOCATION_VOITURES -> "Auto Rent Algiers Prestige"
                  },
                  style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                  )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                  Icons.Default.Verified,
                  contentDescription = "Vérifié",
                  tint = TechCyan,
                  modifier = Modifier.size(18.dp)
                )
              }

              Text(
                text = when (currentRole) {
                  AccountRole.GARAGE_PRO -> "Garage Auto Performance • Alger Centre (16)"
                  AccountRole.VENDEUR_PIECES -> "Grossiste & Magasin Pièces • Bab Ezzouar (16)"
                  AccountRole.ATELIER_MAGASIN_MIXTE -> "Magasin de Pièces + Atelier Montage (1 Principal + 4 Sous-Comptes)"
                  AccountRole.ATELIER_CARROSSERIE -> "Atelier Spécial Carrosserie, Tôlerie & Peinture Étuve • Alger (16)"
                  AccountRole.CONDUCTEUR -> "Propriétaire VW Tiguan II (04829-121-16) • Alger"
                  AccountRole.LOCATION_VOITURES -> "Agence Flotte 18 Véhicules • Alger & Aéroport"
                },
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.5.sp)
              )

              Spacer(modifier = Modifier.height(6.dp))

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = when (currentRole) {
                  AccountRole.GARAGE_PRO -> PerformanceRedDark.copy(alpha = 0.4f)
                  AccountRole.VENDEUR_PIECES -> MechanicalOrangeDark.copy(alpha = 0.4f)
                  AccountRole.ATELIER_MAGASIN_MIXTE -> MechanicalOrangeDark.copy(alpha = 0.4f)
                  AccountRole.ATELIER_CARROSSERIE -> PerformanceRedDark.copy(alpha = 0.4f)
                  AccountRole.CONDUCTEUR -> EvBlue.copy(alpha = 0.2f)
                  AccountRole.LOCATION_VOITURES -> MechanicalOrange.copy(alpha = 0.2f)
                },
                border = BorderStroke(
                  1.dp,
                  when (currentRole) {
                    AccountRole.GARAGE_PRO -> PerformanceRed
                    AccountRole.VENDEUR_PIECES -> MechanicalOrange
                    AccountRole.ATELIER_MAGASIN_MIXTE -> MechanicalOrange
                    AccountRole.ATELIER_CARROSSERIE -> PerformanceRed
                    AccountRole.CONDUCTEUR -> EvBlue
                    AccountRole.LOCATION_VOITURES -> MechanicalOrange
                  }
                )
              ) {
                Text(
                  text = when (currentRole) {
                    AccountRole.GARAGE_PRO -> "ABONNEMENT ATELIER EXPERT ACTIF"
                    AccountRole.VENDEUR_PIECES -> "MAGASIN DE PIÈCES AGRÉÉ MEKANIK+"
                    AccountRole.ATELIER_MAGASIN_MIXTE -> "COMPTE MIXTE ATELIER + MAGASIN (1+4 SOUS-COMPTES)"
                    AccountRole.ATELIER_CARROSSERIE -> "COMPTE SPÉCIAL CARROSSERIE & TÔLERIE CERTIFIÉ"
                    AccountRole.CONDUCTEUR -> "COMPTE CONDUCTEUR CERTIFIÉ"
                    AccountRole.LOCATION_VOITURES -> "ESPACE GESTIONNAIRE LOCATION ACTIF"
                  },
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  ),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
          Divider(color = MetalBorder)
          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            when (currentRole) {
              AccountRole.GARAGE_PRO -> {
                ProfileStatItem(title = "Véhicules", count = "8", accentColor = TechCyan)
                ProfileStatItem(title = "Diagnostics", count = "142", accentColor = ElectricGreen)
                ProfileStatItem(title = "Factures", count = "64", accentColor = MechanicalOrange)
                ProfileStatItem(title = "Note Pro", count = "4.9★", accentColor = PerformanceRedGlow)
              }
              AccountRole.VENDEUR_PIECES -> {
                ProfileStatItem(title = "Références", count = "${uiState.barcodeInventory.size}", accentColor = TechCyan)
                ProfileStatItem(title = "En Stock", count = "${uiState.barcodeInventory.sumOf { it.currentStock }}", accentColor = ElectricGreen)
                ProfileStatItem(title = "Ruptures", count = "${uiState.barcodeInventory.count { it.isLowStock }}", accentColor = PerformanceRedGlow)
                ProfileStatItem(title = "Atelier Relié", count = if (uiState.workshopStoreLinkConfig.isStoreLinked) "Oui (+5k)" else "Non", accentColor = MechanicalOrange)
              }
              AccountRole.ATELIER_MAGASIN_MIXTE -> {
                ProfileStatItem(title = "Sous-Comptes", count = "1 + 4", accentColor = TechCyan)
                ProfileStatItem(title = "Stock Pièces", count = "${uiState.barcodeInventory.sumOf { it.currentStock }}", accentColor = ElectricGreen)
                ProfileStatItem(title = "Montages OR", count = "${uiState.repairOrders.size}", accentColor = MechanicalOrange)
                ProfileStatItem(title = "2FA Google", count = "Actif", accentColor = PerformanceRedGlow)
              }
              AccountRole.ATELIER_CARROSSERIE -> {
                ProfileStatItem(title = "Tôlerie VIN", count = "Actif", accentColor = TechCyan)
                ProfileStatItem(title = "Photos Av/Ap", count = "Obligatoire", accentColor = ElectricGreen)
                ProfileStatItem(title = "Marbre/Étuve", count = "Certifié", accentColor = MechanicalOrange)
                ProfileStatItem(title = "2FA Google", count = "Actif", accentColor = PerformanceRedGlow)
              }
              AccountRole.CONDUCTEUR -> {
                ProfileStatItem(title = "Mon Véhicule", count = "1 Actif", accentColor = TechCyan)
                ProfileStatItem(title = "Réparations", count = "${uiState.driverRepairHistory.size}", accentColor = ElectricGreen)
                ProfileStatItem(title = "Factures DZD", count = "${uiState.driverInvoices.size}", accentColor = MechanicalOrange)
                ProfileStatItem(title = "Santé Auto", count = "${uiState.driverVehicle.healthScorePct}%", accentColor = PerformanceRedGlow)
              }
              AccountRole.LOCATION_VOITURES -> {
                ProfileStatItem(title = "Flotte", count = "${uiState.rentalFleetSummary.totalCars}", accentColor = TechCyan)
                ProfileStatItem(title = "En Location", count = "${uiState.rentalFleetSummary.rentedCars}", accentColor = ElectricGreen)
                ProfileStatItem(title = "Disponibles", count = "${uiState.rentalFleetSummary.availableCars}", accentColor = MechanicalOrange)
                ProfileStatItem(title = "Occupation", count = "${uiState.rentalFleetSummary.occupancyRatePct.toInt()}%", accentColor = PerformanceRedGlow)
              }
            }
          }
        }
      }
    }

    // 2. Dedicated Role Spaces High Priority Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, PerformanceRed.copy(alpha = 0.6f))
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "ACCÈS RAPIDE AUX ESPACES DÉDIÉS",
            style = MaterialTheme.typography.labelSmall.copy(
              color = PerformanceRedGlow,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            ProfileFeatureCard(
              title = "Espace Conducteur",
              subtitle = "Carnet, Factures & DIY",
              icon = Icons.Default.DirectionsCar,
              accentColor = EvBlue,
              onClick = onNavigateToDriverSpace,
              modifier = Modifier.weight(1f)
            )
            ProfileFeatureCard(
              title = "Agence Location",
              subtitle = "Flotte, Contrats & Alertes",
              icon = Icons.Default.CarRental,
              accentColor = MechanicalOrange,
              onClick = onNavigateToCarRental,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    // 3. Pro Shortcuts & Features Grid
    item {
      Text(
        text = "SERVICES & OUTILS AUTOMOBILES",
        style = MaterialTheme.typography.labelMedium.copy(
          color = AluminumMuted,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      )
    }

    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          ProfileFeatureCard(
            title = "Passeport Entretien",
            subtitle = "QR Code certifié",
            icon = Icons.Default.QrCode2,
            accentColor = TechCyan,
            onClick = { viewModel.togglePassportModal(true) },
            modifier = Modifier.weight(1f)
          )
          ProfileFeatureCard(
            title = "Facturation Fiscale",
            subtitle = "Factures & CIB DZ",
            icon = Icons.Default.ReceiptLong,
            accentColor = WarningAmber,
            onClick = { viewModel.toggleInvoiceModal(true) },
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          ProfileFeatureCard(
            title = "Gestion de Flotte",
            subtitle = "Parc multi-véhicules",
            icon = Icons.Default.LocalShipping,
            accentColor = MechanicalOrange,
            onClick = onNavigateToFleet,
            modifier = Modifier.weight(1f)
          )
          ProfileFeatureCard(
            title = "OBD & Télémétrie",
            subtitle = "Flux ECU & Sons IA",
            icon = Icons.Default.Speed,
            accentColor = ElectricGreen,
            onClick = onNavigateToObd,
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          ProfileFeatureCard(
            title = "Suivi Réparations",
            subtitle = "En direct atelier",
            icon = Icons.Default.AvTimer,
            accentColor = PerformanceRed,
            onClick = onNavigateToLiveTracking,
            modifier = Modifier.weight(1f)
          )
          ProfileFeatureCard(
            title = "Marketplace B2B",
            subtitle = "Grossistes 58 Wilayas",
            icon = Icons.Default.Storefront,
            accentColor = TechnicalBlue,
            onClick = { viewModel.toggleGroupQuoteModal(true) },
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          ProfileFeatureCard(
            title = "Équipes Ultra Pro",
            subtitle = "10 Sous-comptes & Rôles",
            icon = Icons.Default.CorporateFare,
            accentColor = PerformanceRed,
            onClick = { viewModel.openUltraProManager() },
            modifier = Modifier.weight(1f)
          )
          ProfileFeatureCard(
            title = "Abonnements Pro",
            subtitle = "Forfaits & Factures DZD",
            icon = Icons.Default.WorkspacePremium,
            accentColor = WarningAmber,
            onClick = onNavigateToSubscriptions,
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          ProfileFeatureCard(
            title = "Passerelle API & Cache",
            subtitle = "Sandbox Dev & Zéro Frais",
            icon = Icons.Default.CloudSync,
            accentColor = TechCyan,
            onClick = { viewModel.openDevApiManagerModal() },
            modifier = Modifier.weight(1f)
          )
          ProfileFeatureCard(
            title = "Hub Logiciels OEM",
            subtitle = "ETKA, ServiceBox, Dialogys",
            icon = Icons.Default.LaptopMac,
            accentColor = MechanicalOrange,
            onClick = { viewModel.openOemSoftwareLauncher() },
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          ProfileFeatureCard(
            title = "Sécurité 2FA & Multi-Postes",
            subtitle = "Max 3 téléphones • Google Auth",
            icon = Icons.Default.Security,
            accentColor = ElectricGreen,
            onClick = { viewModel.openWorkshop2FAModal() },
            modifier = Modifier.weight(1f)
          )
          ProfileFeatureCard(
            title = "Passerelle Magasin + Atelier",
            subtitle = "Compte Unique (+5 000 DA)",
            icon = Icons.Default.Storefront,
            accentColor = MechanicalOrange,
            onClick = { viewModel.openWorkshopStoreLinkModal() },
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 4. Saved Vehicles List (Mon Garage)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "MON GARAGE (VÉHICULES ENREGISTRÉS)",
          style = MaterialTheme.typography.labelMedium.copy(
            color = AluminumMuted,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        )
        Text(
          text = "3 Actifs",
          style = MaterialTheme.typography.labelSmall.copy(color = TechCyan, fontWeight = FontWeight.Bold)
        )
      }
    }

    items(uiState.searchResults) { vehicle ->
      val isCurrent = vehicle.vin == uiState.activeVehicle.vin
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = MetalCard,
        border = BorderStroke(1.dp, if (isCurrent) PerformanceRed else MetalBorder),
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            viewModel.onSearchQueryChanged(vehicle.vin)
            viewModel.performVehicleSearch()
          }
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(if (isCurrent) PerformanceRedDark else MetalSurface),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.DirectionsCar,
              contentDescription = null,
              tint = if (isCurrent) PerformanceRedGlow else AluminumLight,
              modifier = Modifier.size(22.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "${vehicle.brand} ${vehicle.model} (${vehicle.exactYear})",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = PureWhite)
            )
            Text(
              text = "Moteur : ${vehicle.engineCode} • ${vehicle.powerCh}ch • VIN : ${vehicle.vin.take(8)}...",
              style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
            )
          }

          if (isCurrent) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = ElectricGreen.copy(alpha = 0.2f),
              border = BorderStroke(1.dp, ElectricGreen)
            ) {
              Text(
                text = "Actif",
                style = MaterialTheme.typography.labelSmall.copy(color = ElectricGreen, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
              )
            }
          }
        }
      }
    }

    // 5. Quick Account Actions (Docs, Abonnements, SOS 24/7)
    item {
      Text(
        text = "PARAMÈTRES & COMPTE",
        style = MaterialTheme.typography.labelMedium.copy(
          color = AluminumMuted,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      )
    }

    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(MetalCard, RoundedCornerShape(14.dp))
          .border(1.dp, MetalBorder, RoundedCornerShape(14.dp))
      ) {
        ProfileListItem(
          icon = Icons.Default.Subscriptions,
          title = "Plans d'abonnement & Licences",
          subtitle = "Gérer les accès Pro et licences ateliers",
          onClick = onNavigateToSubscriptions
        )
        Divider(color = MetalBorder)
        ProfileListItem(
          icon = Icons.Default.MenuBook,
          title = "Documentation technique constructeur",
          subtitle = "Couples de serrage, schémas de calage, fusibles",
          onClick = onNavigateToDocs
        )
        Divider(color = MetalBorder)
        ProfileListItem(
          icon = Icons.Default.Storefront,
          title = "Annuaire des vendeurs & grossistes",
          subtitle = "Distributeurs certifiés 58 Wilayas",
          onClick = onNavigateToVendors
        )
        Divider(color = MetalBorder)
        ProfileListItem(
          icon = Icons.Default.Emergency,
          title = "Assistance SOS & Dépannage 24/7",
          subtitle = "Appel direct remorqueuses d'urgence",
          onClick = { viewModel.toggleSosModal(true) },
          accentTint = PerformanceRedGlow
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
      Text(
        text = "Mekanik+ Algérie • Version 3.4.0 Pro Build 2026\nPlateforme certifiée pour professionnels & particuliers de l'automobile",
        style = MaterialTheme.typography.bodySmall.copy(
          color = AluminumMuted,
          fontSize = 11.sp,
          lineHeight = 16.sp
        ),
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

@Composable
private fun ProfileStatItem(
  title: String,
  count: String,
  accentColor: Color
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = count,
      style = MaterialTheme.typography.titleMedium.copy(
        color = accentColor,
        fontWeight = FontWeight.Black
      )
    )
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall.copy(
        color = AluminumMuted,
        fontSize = 11.sp
      )
    )
  }
}

@Composable
private fun ProfileFeatureCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  accentColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.clickable(onClick = onClick),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(accentColor.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = PureWhite,
            fontSize = 12.5.sp
          )
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall.copy(
            color = AluminumMuted,
            fontSize = 10.5.sp
          )
        )
      }
    }
  }
}

@Composable
private fun ProfileListItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit,
  accentTint: Color = TechCyan
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(icon, contentDescription = null, tint = accentTint, modifier = Modifier.size(22.dp))
    Spacer(modifier = Modifier.width(12.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite)
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
      )
    }
    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AluminumMuted)
  }
}
