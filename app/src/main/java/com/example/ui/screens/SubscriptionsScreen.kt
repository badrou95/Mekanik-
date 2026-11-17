package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PricingPlan
import com.example.data.repository.MekanikRepository
import com.example.ui.components.MekanikPrimaryButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

@Composable
fun SubscriptionsScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("subscriptions_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Bar
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = AluminumLight)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Column {
          Text(
            text = "ABONNEMENTS MEKANIK+ PRO",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              color = PureWhite,
              fontSize = 18.sp
            )
          )
          Text(
            text = "Tarifs officiels en Dinars Algériens (DZD) • Facturation Entreprise",
            style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
          )
        }
      }
    }

    // Super-Admin Quick Approval Banner for Subscriptions
    item {
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = adaptiveBannerSurface(0xFF281014, 0xFFFFF1F2),
        border = BorderStroke(1.2.dp, PerformanceRedGlow),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("subs_super_admin_banner")
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
              Icons.Default.AdminPanelSettings,
              contentDescription = null,
              tint = PerformanceRedGlow,
              modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "CONSOLE ADMIN : APPROBATION FORFAITS & INSCRIPTIONS",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 11.5.sp
              )
              Text(
                text = "${uiState.pendingAdminApprovalsCount} demandes en attente (${uiState.superAdminSession.adminEmail})",
                color = AluminumLight,
                fontSize = 10.5.sp
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = { viewModel.openSuperAdminModal() },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("btn_open_admin_from_subs")
          ) {
            Text(
              text = "GÉRER & APPROUVER",
              color = PureWhite,
              fontWeight = FontWeight.Black,
              fontSize = 10.5.sp
            )
          }
        }
      }
    }

    // 3-Day Automatic Demo Trial & Activation Key Unlock Banner
    item {
      com.example.ui.components.DemoTrialStatusBanner(
        demoStatus = uiState.demoTrialStatus,
        onOpenActivationPortal = { viewModel.openDemoTrialActivationModal() },
        onSimulatePhaseChange = { viewModel.simulateDemoTrialPhase(it) },
        modifier = Modifier.fillMaxWidth()
      )
    }

    // Plans list
    items(MekanikRepository.pricingPlans) { plan ->
      PlanCard(
        plan = plan,
        isCurrent = plan.id == "plan_ultra_pro",
        onSelectPlan = {
          if (plan.id == "plan_ultra_pro") {
            viewModel.openUltraProManager()
          } else {
            viewModel.submitUserPlanSelectionForAdminApproval(plan)
          }
        }
      )
    }

    // Extension Pack Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("subscription_extension_pack_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.5.dp, TechCyan)
      ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.GroupAdd, contentDescription = null, tint = TechCyan, modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "PACK EXTENSION SOUS-COMPTES",
                  style = MaterialTheme.typography.titleMedium.copy(color = PureWhite, fontWeight = FontWeight.Black)
                )
                Text(
                  text = "Option pour Entreprises & Grands Ateliers",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.5.sp)
                )
              }
            }

            Surface(shape = RoundedCornerShape(6.dp), color = TechCyan.copy(alpha = 0.2f), border = BorderStroke(1.dp, TechCyan)) {
              Text(
                text = "SUPPLÉMENT MODULAIRE",
                style = MaterialTheme.typography.labelSmall.copy(color = TechCyan, fontWeight = FontWeight.Black, fontSize = 9.sp),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }

          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "2 500 DA",
              style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                color = TechCyan,
                fontSize = 24.sp
              )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "/ par tranche de 2 comptes (2 000 - 3 000 DA)",
              style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.5.sp),
              modifier = Modifier.padding(bottom = 3.dp)
            )
          }

          Text(
            text = "Permet d'ajouter des comptes mécaniciens supplémentaires pour l'atelier, des magasiniers ou des conseillers clients selon la taille de votre structure.",
            style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 12.sp)
          )

          Button(
            onClick = { viewModel.openPurchaseExtensionDialog() },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
            modifier = Modifier.fillMaxWidth().testTag("btn_buy_extension_from_subs")
          ) {
            Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "ACHETER UN PACK EXTENSION (+2 COMPTES)",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, color = GraphiteDark)
            )
          }
        }
      }
    }

    // 2. Passerelle Magasin de Pièces & Atelier (Compte Unique : +5 000 DA)
    item {
      val isStoreLinked = uiState.workshopStoreLinkConfig.isStoreLinked

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("subscription_store_link_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isStoreLinked) adaptiveBannerSurface(0xFF261D15, 0xFFFFF7ED) else MetalCard
        ),
        border = BorderStroke(1.5.dp, if (isStoreLinked) ElectricGreen else MechanicalOrange)
      ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Storefront,
                contentDescription = null,
                tint = if (isStoreLinked) ElectricGreen else MechanicalOrange,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "PASSERELLE MAGASIN + ATELIER",
                  style = MaterialTheme.typography.titleMedium.copy(color = PureWhite, fontWeight = FontWeight.Black)
                )
                Text(
                  text = "Compte unique & synchronisation stock / devis",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.5.sp)
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (isStoreLinked) ElectricGreen.copy(alpha = 0.2f) else MechanicalOrange.copy(alpha = 0.2f),
              border = BorderStroke(1.dp, if (isStoreLinked) ElectricGreen else MechanicalOrange)
            ) {
              Text(
                text = if (isStoreLinked) "ACTIF SUR ATELIER" else "SUPPLÉMENT +5 000 DA",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (isStoreLinked) ElectricGreen else MechanicalOrange,
                  fontWeight = FontWeight.Black,
                  fontSize = 9.sp
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }

          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "+5 000 DA",
              style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                color = MechanicalOrange,
                fontSize = 24.sp
              )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "/ an (en option sur Formule Atelier 18 000 DA)",
              style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.5.sp),
              modifier = Modifier.padding(bottom = 3.dp)
            )
          }

          Text(
            text = "Permet de relier votre magasin de pièces détachées avec votre atelier pour avoir un seul et même compte pour les deux (23 000 DA au total au lieu de payer deux abonnements). Déduction de stock et factures consolidées.",
            style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 12.sp)
          )

          Button(
            onClick = { viewModel.openWorkshopStoreLinkModal() },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
            modifier = Modifier.fillMaxWidth().testTag("btn_open_store_link_from_subs")
          ) {
            Icon(Icons.Default.Link, contentDescription = null, tint = PureWhite, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (isStoreLinked) "GÉRER LA PASSERELLE MAGASIN (ACTIF)" else "ACTIVER LA PASSERELLE MAGASIN (+5 000 DA)",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, color = PureWhite)
            )
          }
        }
      }
    }

    // 3. Multi-Appareils Atelier (Max 3) & Sécurité 2FA Google Authenticator
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("subscription_multi_device_2fa_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.5.dp, TechCyan.copy(alpha = 0.8f))
      ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Security, contentDescription = null, tint = TechCyan, modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "MULTI-APPAREILS & 2FA GOOGLE",
                  style = MaterialTheme.typography.titleMedium.copy(color = PureWhite, fontWeight = FontWeight.Black)
                )
                Text(
                  text = "Max 3 appareils simultanés (Exclusif Atelier Pro)",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.5.sp)
                )
              }
            }

            Surface(shape = RoundedCornerShape(6.dp), color = TechCyan.copy(alpha = 0.2f), border = BorderStroke(1.dp, TechCyan)) {
              Text(
                text = "${uiState.workshop2FAConfig.activeDevicesCount} / 3 CONNECTÉS",
                style = MaterialTheme.typography.labelSmall.copy(color = TechCyan, fontWeight = FontWeight.Black, fontSize = 9.sp),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }

          Text(
            text = "Ouvrez le même compte Atelier sur plusieurs téléphones et tablettes en toute sécurité avec double facteur Google Authenticator (jusqu'à 3 appareils maximum pour l'équipe atelier).",
            style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 12.sp)
          )

          Button(
            onClick = { viewModel.openWorkshop2FAModal() },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GraphiteBlack),
            border = BorderStroke(1.dp, TechCyan),
            modifier = Modifier.fillMaxWidth().testTag("btn_manage_2fa_from_subs")
          ) {
            Icon(Icons.Default.Devices, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "GÉRER LES APPAREILS & 2FA GOOGLE",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, color = TechCyan)
            )
          }
        }
      }
    }

    // 2. Full All-in-One OEM Technical Software Pack Card (+10,000 DA)
    item {
      val isUnlocked = uiState.workshopSoftwareConfig.hasFullSoftwarePackageUnlocked

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("subscription_full_oem_software_pack_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isUnlocked) adaptiveBannerSurface(0xFF14241B, 0xFFF0FDF4) else MetalCard
        ),
        border = BorderStroke(1.5.dp, if (isUnlocked) ElectricGreen else PerformanceRedGlow)
      ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Apps,
                contentDescription = null,
                tint = if (isUnlocked) ElectricGreen else PerformanceRedGlow,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "PACK INTÉGRAL LOGICIELS OEM",
                  style = MaterialTheme.typography.titleMedium.copy(color = PureWhite, fontWeight = FontWeight.Black)
                )
                Text(
                  text = "Tous les catalogues EPC & manuels constructeurs",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.5.sp)
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (isUnlocked) ElectricGreen.copy(alpha = 0.2f) else PerformanceRed.copy(alpha = 0.2f),
              border = BorderStroke(1.dp, if (isUnlocked) ElectricGreen else PerformanceRed)
            ) {
              Text(
                text = if (isUnlocked) "ACTIF & DÉBLOQUÉ" else "OPTION COMPLÈTE",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (isUnlocked) ElectricGreen else PerformanceRedGlow,
                  fontWeight = FontWeight.Black,
                  fontSize = 9.sp
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }

          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "10 000 DA",
              style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                color = if (isUnlocked) ElectricGreen else PerformanceRedGlow,
                fontSize = 24.sp
              )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "/ an (Pack universel toutes marques)",
              style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.5.sp),
              modifier = Modifier.padding(bottom = 3.dp)
            )
          }

          Text(
            text = "Accès sans restriction à 100% des catalogues électroniques de pièces de rechange (ETKA VAG, ServiceBox Stellantis, Dialogys Renault, Microcat Asie, BMW ETK/ISTA, Mercedes WIS/EPC, TecDoc Pro & HaynesPro) et modes concessionnaires personnalisables.",
            style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 12.sp)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = { viewModel.openOemSoftwareLauncher() },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MetalBorder),
              modifier = Modifier.weight(1f).testTag("btn_open_hub_from_subs")
            ) {
              Icon(Icons.Default.Dns, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("OUVRIR LE HUB", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            if (!isUnlocked) {
              Button(
                onClick = { viewModel.openPurchaseFullSoftwareSupplementModal() },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                modifier = Modifier.weight(1.4f).testTag("btn_buy_full_software_from_subs")
              ) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("DÉBLOQUER (+10 000 DA)", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.Black)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun PlanCard(
  plan: PricingPlan,
  isCurrent: Boolean,
  onSelectPlan: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("subscription_plan_${plan.id}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (plan.isPopular) adaptiveBannerSurface(0xFF192026, 0xFFFFF1F2) else MetalCard
    ),
    border = BorderStroke(
      if (plan.isPopular) 1.5.dp else 1.dp,
      if (plan.isPopular) PerformanceRed else MetalBorder
    )
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = plan.title,
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              color = PureWhite
            )
          )
          Text(
            text = plan.targetUser,
            style = MaterialTheme.typography.bodySmall.copy(
              color = AluminumGray,
              fontSize = 11.5.sp
            )
          )
        }

        if (plan.isPopular) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = PerformanceRed
          ) {
            Text(
              text = plan.badgeText ?: "RECOMMANDÉ",
              style = MaterialTheme.typography.labelSmall.copy(
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp
              ),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(verticalAlignment = Alignment.Bottom) {
        Text(
          text = plan.priceDzd,
          style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Black,
            color = PerformanceRedGlow,
            fontSize = 24.sp
          )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "/ ${plan.billingPeriod}",
          style = MaterialTheme.typography.bodySmall.copy(
            color = AluminumLight,
            fontSize = 12.sp
          ),
          modifier = Modifier.padding(bottom = 3.dp)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      HorizontalDivider(color = MetalBorder, thickness = 1.dp)

      Spacer(modifier = Modifier.height(10.dp))

      plan.features.forEach { feature ->
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(vertical = 3.dp)
        ) {
          Icon(
            Icons.Default.Check,
            contentDescription = null,
            tint = if (plan.isPopular) PerformanceRed else ElectricGreen,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = feature,
            style = MaterialTheme.typography.bodySmall.copy(
              color = OffWhite,
              fontSize = 12.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      if (plan.id == "plan_ultra_pro") {
        Button(
          onClick = onSelectPlan,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = PerformanceRed,
            contentColor = PureWhite
          ),
          modifier = Modifier.fillMaxWidth().testTag("btn_manage_ultra_pro_plan")
        ) {
          Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "GÉRER LES 10 SOUS-COMPTES (38 000 DA)",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black)
          )
        }
      } else {
        MekanikPrimaryButton(
          text = if (isCurrent) "FORFAIT ACTIF" else "CHOISIR CE FORFAIT",
          onClick = onSelectPlan,
          isOrange = !plan.isPopular,
          modifier = Modifier.fillMaxWidth(),
          enabled = !isCurrent
        )
      }
    }
  }
}
