package com.example.ui.components

import androidx.compose.animation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.data.repository.MekanikRepository
import com.example.ui.theme.*

private enum class UltraProTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
  WORKFLOW_PIECES("⚡ Flux Pièces (4 Étapes)", Icons.Default.AltRoute),
  OEM_LOGICIELS("Logiciels OEM (+10k DA)", Icons.Default.Dns),
  ALL("Tous les Sous-Comptes", Icons.Default.Groups),
  DIRECTION("Gérant (Direction)", Icons.Default.AdminPanelSettings),
  ATELIER("Atelier & Mécaniciens (6)", Icons.Default.PrecisionManufacturing),
  ACCUEIL("Conseillers & Team Leader (3)", Icons.Default.SupportAgent),
  MAGASIN("Magasin & Stocks (3)", Icons.Default.Inventory),
  COMPTA("Comptabilité & Factures (3)", Icons.Default.AccountBalance),
  AUDIT("Audit & Contrôle (1)", Icons.Default.FactCheck),
  JOURNAL("Journal d'Audit Traçabilité", Icons.Default.HistoryEdu)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UltraProAccountsManagerModal(
  organization: UltraProOrganizationSummary,
  subAccounts: List<UltraProSubAccount>,
  activeImpersonation: UltraProSubAccount?,
  auditLogs: List<SubAccountAuditLog>,
  partsRequisitions: List<WorkshopPartsRequisition>,
  onOpenCreatePartsRequisition: () -> Unit,
  onOpenPurchaseExtensionModal: () -> Unit,
  onOpenOemSoftwareHub: () -> Unit = {},
  onOpenPurchaseSoftwareSupplementModal: () -> Unit = {},
  onApproveByWorkshopManager: (String) -> Unit,
  onPrepareByStorekeeper: (String) -> Unit,
  onConfirmStockDispatchByHeadStorekeeper: (String) -> Unit,
  onRejectPartsRequisition: (String, String) -> Unit,
  onAddSubAccountClick: () -> Unit,
  onEditSubAccount: (UltraProSubAccount) -> Unit,
  onDeleteSubAccount: (String) -> Unit,
  onToggleActive: (String, Boolean) -> Unit,
  onSwitchAccount: (UltraProSubAccount?) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf(UltraProTab.WORKFLOW_PIECES) }
  var searchQuery by remember { mutableStateOf("") }

  val filteredAccounts = remember(subAccounts, selectedTab, searchQuery) {
    subAccounts.filter { acc ->
      val matchesTab = when (selectedTab) {
        UltraProTab.WORKFLOW_PIECES -> false
        UltraProTab.OEM_LOGICIELS -> false
        UltraProTab.ALL -> true
        UltraProTab.DIRECTION -> acc.role == UltraProSubAccountRole.GERANT_PRINCIPAL
        UltraProTab.ATELIER -> acc.role == UltraProSubAccountRole.CHEF_ATELIER || acc.role == UltraProSubAccountRole.MECANICIEN_ATELIER
        UltraProTab.ACCUEIL -> acc.role == UltraProSubAccountRole.CHEF_CONSEILLER_CLIENT || acc.role == UltraProSubAccountRole.CONSEILLER_CLIENT
        UltraProTab.MAGASIN -> acc.role == UltraProSubAccountRole.ADMIN_MAGASIN || acc.role == UltraProSubAccountRole.VENDEUR_MAGASIN
        UltraProTab.COMPTA -> acc.role == UltraProSubAccountRole.COMPTABLE_PRINCIPAL || acc.role == UltraProSubAccountRole.ANALYSTE_FACTURES
        UltraProTab.AUDIT -> acc.role == UltraProSubAccountRole.AUDITEUR_QUALITE
        UltraProTab.JOURNAL -> true
      }
      val matchesSearch = searchQuery.isBlank() ||
        acc.fullName.contains(searchQuery, ignoreCase = true) ||
        acc.email.contains(searchQuery, ignoreCase = true) ||
        acc.role.label.contains(searchQuery, ignoreCase = true)

      matchesTab && matchesSearch
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.97f)
        .fillMaxHeight(0.95f)
        .testTag("modal_ultra_pro_manager"),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, PerformanceRedGlow)
    ) {
      Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // --- TOP HEADER ---
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(PerformanceRed, MechanicalOrange))),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.CorporateFare, contentDescription = null, tint = PureWhite, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "COMPTE ENTREPRISE ULTRA PRO",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                  )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = PerformanceRedDark,
                  border = BorderStroke(0.5.dp, PerformanceRed)
                ) {
                  Text(
                    text = "38 000 DA / AN",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = PureWhite,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
              Text(
                text = "${organization.companyName} • Multi-Équipes & Workflow Pièces Intégré",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.5.sp)
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- ACTIVE ACTOR / SIMULATION BANNER ---
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (activeImpersonation != null) Color(0xFF201608) else MetalCard
          ),
          border = BorderStroke(
            1.dp,
            if (activeImpersonation != null) MechanicalOrange else MetalBorder
          )
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Icon(
                if (activeImpersonation != null) Icons.Default.SwitchAccount else Icons.Default.Shield,
                contentDescription = null,
                tint = if (activeImpersonation != null) MechanicalOrange else TechCyan,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = if (activeImpersonation != null) {
                    "SESSION ACTIVE : ${activeImpersonation.fullName} (${activeImpersonation.role.label})"
                  } else {
                    "ADMINISTRATEUR MAÎTRE : M. Reda Mansouri (Gérant & Dirigeant Général)"
                  },
                  style = MaterialTheme.typography.labelMedium.copy(
                    color = if (activeImpersonation != null) MechanicalOrange else PureWhite,
                    fontWeight = FontWeight.Bold
                  )
                )
                Text(
                  text = if (activeImpersonation != null) {
                    "Droits restreints au département : ${activeImpersonation.department}"
                  } else {
                    "Gouvernance complète des flux, sous-comptes, stocks, facturation et audits"
                  },
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 10.5.sp)
                )
              }
            }

            if (activeImpersonation != null) {
              Button(
                onClick = { onSwitchAccount(null) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = GraphiteBlack,
                  contentColor = MechanicalOrange
                ),
                border = BorderStroke(1.dp, MechanicalOrange),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("REVENIR EN GÉRANT", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- SUB-ACCOUNTS CAPACITY BAR & EXTENSION PACK ---
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "CAPACITÉ SOUS-COMPTES :",
                style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold, fontSize = 10.sp)
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "${subAccounts.size} / ${organization.maxSubAccountsAllowed} Comptes",
                  style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold)
                )
                if (organization.purchasedExtensionPacksCount > 0) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(shape = RoundedCornerShape(4.dp), color = PerformanceRedDark.copy(alpha = 0.5f)) {
                    Text(
                      text = "+${organization.purchasedExtensionPacksCount * 2} via extensions",
                      style = MaterialTheme.typography.labelSmall.copy(color = PerformanceRedGlow, fontSize = 9.sp, fontWeight = FontWeight.Bold),
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                  }
                }
              }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedButton(
                onClick = onOpenPurchaseExtensionModal,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, PerformanceRed),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("btn_buy_extension_pack")
              ) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("+2 COMPTES (2 500 DA)", fontSize = 10.5.sp, color = PerformanceRedGlow, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = onAddSubAccountClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                enabled = subAccounts.size < organization.maxSubAccountsAllowed
              ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("NOUVEAU COMPTE", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- CATEGORY TABS ---
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          contentPadding = PaddingValues(vertical = 2.dp)
        ) {
          items(UltraProTab.entries) { tab ->
            val isSelected = selectedTab == tab
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) PerformanceRed else MetalCard,
              border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
              modifier = Modifier.clickable { selectedTab = tab }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  tab.icon,
                  contentDescription = null,
                  tint = if (isSelected) PureWhite else AluminumLight,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = tab.label,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) PureWhite else AluminumLight,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                  )
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- TAB CONTENT ---
        when (selectedTab) {
          UltraProTab.WORKFLOW_PIECES -> {
            // WORKFLOW DÉTAILLÉ DE DEMANDE DE PIÈCES (4 ÉTAPES)
            Column(modifier = Modifier.weight(1f)) {
              // Workflow Stepper Header
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
                border = BorderStroke(1.dp, MetalBorder)
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "CYCLE DE VIE & TRAÇABILITÉ DES PIÈCES ATELIER",
                      style = MaterialTheme.typography.labelSmall.copy(color = PerformanceRedGlow, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    )

                    Button(
                      onClick = onOpenCreatePartsRequisition,
                      shape = RoundedCornerShape(8.dp),
                      colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                      modifier = Modifier.testTag("btn_new_parts_requisition")
                    ) {
                      Icon(Icons.Default.Add, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("+ NOUVELLE DEMANDE (MÉCANICIEN)", color = GraphiteDark, fontSize = 10.5.sp, fontWeight = FontWeight.Black)
                    }
                  }

                  // 4 Steps Visual Flow
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    StepChip(number = "1", title = "Mécanicien", sub = "Recherche Châssis/VIN", color = TechCyan)
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = AluminumMuted, modifier = Modifier.size(14.dp))
                    StepChip(number = "2", title = "Chef Atelier", sub = "Approbation Pièces", color = MechanicalOrange)
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = AluminumMuted, modifier = Modifier.size(14.dp))
                    StepChip(number = "3", title = "Magasinier", sub = "Réception & Prépa", color = Color(0xFF64B5F6))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = AluminumMuted, modifier = Modifier.size(14.dp))
                    StepChip(number = "4", title = "Chef Magasin", sub = "Sortie & Facture auto", color = ElectricGreen)
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // List of Requisitions
              LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                items(partsRequisitions) { req ->
                  PartsRequisitionCard(
                    requisition = req,
                    onApproveByWorkshopManager = { onApproveByWorkshopManager(req.id) },
                    onPrepareByStorekeeper = { onPrepareByStorekeeper(req.id) },
                    onConfirmStockDispatch = { onConfirmStockDispatchByHeadStorekeeper(req.id) },
                    onReject = { onRejectPartsRequisition(req.id, "Non conforme au devis") }
                  )
                }
              }
            }
          }

          UltraProTab.OEM_LOGICIELS -> {
            Column(modifier = Modifier.fillMaxSize()) {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16222F)),
                border = BorderStroke(1.dp, TechCyan)
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Dns, contentDescription = null, tint = TechCyan, modifier = Modifier.size(22.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "SUITES LOGICIELLES CONSTRUCTEURS OEM",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, color = PureWhite)
                      )
                    }
                    Surface(shape = RoundedCornerShape(4.dp), color = PerformanceRedDark) {
                      Text(
                        text = "PACK TOUTES MARQUES (+10 000 DA)",
                        style = MaterialTheme.typography.labelSmall.copy(color = PerformanceRedGlow, fontWeight = FontWeight.Bold, fontSize = 9.sp),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }

                  Text(
                    text = "Chaque concession ou atelier peut configurer son mode constructeur exclusif (Mode Volkswagen VAG, Stellantis, Renault, Asiatiques) ou débloquer l'accès intégral à tous les catalogues EPC, barèmes et manuels d'atelier.",
                    style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.5.sp)
                  )

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Button(
                      onClick = onOpenOemSoftwareHub,
                      shape = RoundedCornerShape(8.dp),
                      colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                      modifier = Modifier.weight(1f).testTag("btn_open_oem_hub_from_ultra_pro")
                    ) {
                      Icon(Icons.Default.Launch, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("ACCÉDER AU HUB EPC", color = GraphiteDark, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }

                    Button(
                      onClick = onOpenPurchaseSoftwareSupplementModal,
                      shape = RoundedCornerShape(8.dp),
                      colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                      modifier = Modifier.weight(1.3f).testTag("btn_unlock_oem_pack_from_ultra_pro")
                    ) {
                      Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("PACK INTÉGRAL (+10 000 DA)", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = "LOGICIELS OFFICIELS OPÉRATIONNELS :",
                style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold, fontSize = 10.sp),
                modifier = Modifier.padding(bottom = 6.dp)
              )

              LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                items(MekanikRepository.sampleOemSoftwareTools) { tool ->
                  val groupColor = Color(tool.group.themeColorHex)
                  Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MetalCard),
                    border = BorderStroke(1.dp, MetalBorder)
                  ) {
                    Row(
                      modifier = Modifier.padding(12.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(text = tool.group.iconEmoji, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                          Text(text = tool.name, style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontWeight = FontWeight.Bold))
                          Text(text = "${tool.group.label} • ${tool.officialVersion}", style = MaterialTheme.typography.labelSmall.copy(color = groupColor, fontSize = 10.sp))
                        }
                      }

                      Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(tool.toolType.badgeColorHex).copy(alpha = 0.2f),
                        border = BorderStroke(0.5.dp, Color(tool.toolType.badgeColorHex))
                      ) {
                        Text(
                          text = tool.toolType.label,
                          style = MaterialTheme.typography.labelSmall.copy(color = Color(tool.toolType.badgeColorHex), fontSize = 9.sp, fontWeight = FontWeight.Bold),
                          modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          UltraProTab.JOURNAL -> {
            // AUDIT TRAIL LOGS
            Text(
              text = "JOURNAL DE SÉCURITÉ & AUDIT DES ACTIONS EN TEMPS RÉEL :",
              style = MaterialTheme.typography.labelSmall.copy(
                color = AluminumMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              ),
              modifier = Modifier.padding(bottom = 6.dp)
            )

            LazyColumn(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              items(auditLogs) { log ->
                Card(
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.dp, MetalBorder)
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(modifier = Modifier.weight(1f)) {
                      Box(
                        modifier = Modifier
                          .size(32.dp)
                          .clip(CircleShape)
                          .background(
                            when (log.severity) {
                              "CRITIQUE" -> PerformanceRedDark
                              "IMPORTANT" -> Color(0xFF4A3500)
                              else -> GraphiteBlack
                            }
                          ),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(
                          when (log.severity) {
                            "CRITIQUE" -> Icons.Default.Warning
                            "IMPORTANT" -> Icons.Default.PriorityHigh
                            else -> Icons.Default.Check
                          },
                          contentDescription = null,
                          tint = when (log.severity) {
                            "CRITIQUE" -> PerformanceRed
                            "IMPORTANT" -> MechanicalOrange
                            else -> TechCyan
                          },
                          modifier = Modifier.size(16.dp)
                        )
                      }
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Text(
                            text = log.actionTitle,
                            style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                          )
                          Spacer(modifier = Modifier.width(6.dp))
                          Text(
                            text = "• ${log.department}",
                            style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
                          )
                        }
                        Text(
                          text = log.details,
                          style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.5.sp),
                          modifier = Modifier.padding(vertical = 2.dp)
                        )
                        Text(
                          text = "Par ${log.authorName} (${log.authorRole}) • ${log.timestamp}",
                          style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 10.sp)
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          else -> {
            // SUB-ACCOUNTS LIST
            LazyColumn(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              items(filteredAccounts) { account ->
                val isSimulated = activeImpersonation?.id == account.id
                SubAccountRowCard(
                  account = account,
                  isSimulated = isSimulated,
                  onToggleActive = { onToggleActive(account.id, it) },
                  onSwitchAccount = {
                    if (isSimulated) onSwitchAccount(null) else onSwitchAccount(account)
                  },
                  onEdit = { onEditSubAccount(account) },
                  onDelete = { onDeleteSubAccount(account.id) }
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun StepChip(number: String, title: String, sub: String, color: Color) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(18.dp)
          .clip(CircleShape)
          .background(color),
        contentAlignment = Alignment.Center
      ) {
        Text(text = number, color = GraphiteDark, fontSize = 10.sp, fontWeight = FontWeight.Black)
      }
      Spacer(modifier = Modifier.width(4.dp))
      Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp))
    }
    Text(text = sub, style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 9.sp))
  }
}

@Composable
private fun PartsRequisitionCard(
  requisition: WorkshopPartsRequisition,
  onApproveByWorkshopManager: () -> Unit,
  onPrepareByStorekeeper: () -> Unit,
  onConfirmStockDispatch: () -> Unit,
  onReject: () -> Unit
) {
  val statusColor = Color(requisition.status.colorHex)

  Card(
    modifier = Modifier.fillMaxWidth().testTag("card_requisition_${requisition.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.2.dp, statusColor.copy(alpha = 0.8f))
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      // Top Header: Number, Status Badge, Date
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(shape = RoundedCornerShape(6.dp), color = GraphiteBlack, border = BorderStroke(1.dp, MetalBorder)) {
            Text(
              text = requisition.orderNumber,
              style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontWeight = FontWeight.Black, fontSize = 11.sp),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "${requisition.vehicleModel} (${requisition.plateNumber})",
            style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = statusColor.copy(alpha = 0.2f),
          border = BorderStroke(1.dp, statusColor)
        ) {
          Text(
            text = requisition.status.label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(color = statusColor, fontSize = 9.5.sp, fontWeight = FontWeight.Black),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      // Vehicle Chassis & VIN Info
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = AluminumMuted, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Châssis / VIN : ${requisition.vehicleVin}", style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp, fontWeight = FontWeight.Medium))
        }

        Text(
          text = "Émise par : ${requisition.requestedByTechnicianName}",
          style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 10.5.sp)
        )
      }

      HorizontalDivider(color = MetalBorder, thickness = 0.5.dp)

      // Parts List
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = "PIÈCES DEMANDÉES (${requisition.parts.size}) :",
          style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        )

        requisition.parts.forEach { part ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Text(text = "• ${part.name}", style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontSize = 11.5.sp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "[${part.oemReference}]", style = MaterialTheme.typography.bodySmall.copy(color = PerformanceRedGlow, fontSize = 10.5.sp, fontWeight = FontWeight.Bold))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = "x${part.quantity}", style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 10.5.sp))
            }
            Text(
              text = "${part.unitPriceDzd * part.quantity} DA",
              style = MaterialTheme.typography.bodySmall.copy(color = ElectricGreen, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
            )
          }
        }
      }

      // Total & Notes
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = requisition.notes,
          style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 10.sp),
          modifier = Modifier.weight(1f)
        )
        Text(
          text = "TOTAL : ${requisition.totalRequisitionDzd} DA",
          style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Black, fontSize = 13.sp)
        )
      }

      // Progression Steps and Validation Actions
      HorizontalDivider(color = MetalBorder, thickness = 0.5.dp)

      when (requisition.status) {
        PartsRequisitionStatus.DEMANDE_MECANICIEN -> {
          // Action for Chef d'Atelier
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "⏳ En attente d'approbation par le Chef d'Atelier",
              style = MaterialTheme.typography.bodySmall.copy(color = MechanicalOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              OutlinedButton(
                onClick = onReject,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, PerformanceRed),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text("Refuser", color = PerformanceRed, fontSize = 10.5.sp)
              }

              Button(
                onClick = onApproveByWorkshopManager,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("btn_approve_workshop_manager_${requisition.id}")
              ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("APPROUVER (CHEF D'ATELIER)", color = GraphiteDark, fontSize = 10.5.sp, fontWeight = FontWeight.Black)
              }
            }
          }
        }

        PartsRequisitionStatus.VALIDEE_CHEF_ATELIER -> {
          // Action for Storekeeper
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "✓ Approuvée par Chef d'Atelier (${requisition.workshopManagerName ?: "Mourad G."})",
                style = MaterialTheme.typography.bodySmall.copy(color = ElectricGreen, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
              )
              Text(
                text = "Transmise au magasinier selon données Châssis/VIN",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 9.5.sp)
              )
            }

            Button(
              onClick = onPrepareByStorekeeper,
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6)),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
              modifier = Modifier.testTag("btn_prepare_storekeeper_${requisition.id}")
            ) {
              Icon(Icons.Default.Inventory2, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("PRÉPARER EN RAYON (MAGASINIER)", color = GraphiteDark, fontSize = 10.5.sp, fontWeight = FontWeight.Black)
            }
          }
        }

        PartsRequisitionStatus.EN_PREPARATION_MAGASIN -> {
          // Action for Head Storekeeper (Chef Magasin)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "📦 Préparée par le Magasinier (${requisition.storekeeperName ?: "Yacine C."})",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64B5F6), fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
              )
              Text(
                text = "En attente de confirmation définitive de sortie par le Chef Département Magasin",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 9.5.sp)
              )
            }

            Button(
              onClick = onConfirmStockDispatch,
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
              modifier = Modifier.testTag("btn_confirm_dispatch_head_store_${requisition.id}")
            ) {
              Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("CONFIRMER SORTIE (CHEF MAGASIN)", fontSize = 10.5.sp, fontWeight = FontWeight.Black)
            }
          }
        }

        PartsRequisitionStatus.CONFIRMEE_SORTIE_STOCK -> {
          // Completed State: Show direct invoice & OR injection banner
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = ElectricGreenDark.copy(alpha = 0.35f),
            border = BorderStroke(1.dp, ElectricGreen)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "SORTIE DE STOCK VALIDÉE PAR LE CHEF MAGASIN",
                    style = MaterialTheme.typography.labelSmall.copy(color = ElectricGreen, fontWeight = FontWeight.Black, fontSize = 10.5.sp)
                  )
                  Text(
                    text = "✓ Imputée directement sur l'Ordre de Réparation & la Facture Client (${requisition.plateNumber})",
                    style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontSize = 10.sp)
                  )
                }
              }

              Surface(shape = RoundedCornerShape(4.dp), color = GraphiteBlack) {
                Text(
                  text = "FACTURE CLIENT À JOUR",
                  style = MaterialTheme.typography.labelSmall.copy(color = ElectricGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
              }
            }
          }
        }

        PartsRequisitionStatus.REJETEE -> {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = PerformanceRedDark.copy(alpha = 0.3f),
            border = BorderStroke(1.dp, PerformanceRed)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Cancel, contentDescription = null, tint = PerformanceRed, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "Demande rejetée par la hiérarchie.", style = MaterialTheme.typography.bodySmall.copy(color = PerformanceRed, fontSize = 11.sp))
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SubAccountRowCard(
  account: UltraProSubAccount,
  isSimulated: Boolean,
  onToggleActive: (Boolean) -> Unit,
  onSwitchAccount: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("sub_account_card_${account.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSimulated) Color(0xFF1F241C) else MetalCard
    ),
    border = BorderStroke(
      if (isSimulated) 1.5.dp else 1.dp,
      if (isSimulated) Color(account.role.badgeColorHex) else MetalBorder
    )
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      // Top Row: Avatar, Name, Role Badge, Switch active
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(Color(account.role.badgeColorHex).copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = account.avatarInitials,
              style = MaterialTheme.typography.labelLarge.copy(
                color = Color(account.role.badgeColorHex),
                fontWeight = FontWeight.Black
              )
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = account.fullName,
                style = MaterialTheme.typography.titleSmall.copy(
                  color = PureWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
              )
              if (isSimulated) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = ElectricGreenDark
                ) {
                  Text(
                    text = "CONNECTÉ",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = ElectricGreen,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                  )
                }
              }
            }
            Text(
              text = "${account.role.label} • ${account.department}",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color(account.role.badgeColorHex),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = GraphiteBlack,
          border = BorderStroke(0.5.dp, Color(account.role.badgeColorHex))
        ) {
          Text(
            text = account.role.shortLabel.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color(account.role.badgeColorHex),
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }
      }

      // Contact & PIN Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Email, contentDescription = null, tint = AluminumMuted, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = account.email, style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Lock, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "PIN: ${account.accessPin}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = AluminumLight,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          )
        }
      }

      // Permissions Chips
      Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
          text = "Permissions & Périmètre :",
          style = MaterialTheme.typography.labelSmall.copy(
            color = AluminumMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          account.permissions.take(2).forEach { perm ->
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = GraphiteBlack
            ) {
              Text(
                text = "✓ $perm",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumLight,
                  fontSize = 10.sp
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                maxLines = 1
              )
            }
          }
          if (account.permissions.size > 2) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = GraphiteBlack
            ) {
              Text(
                text = "+${account.permissions.size - 2}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumGray,
                  fontSize = 10.sp
                ),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
              )
            }
          }
        }
      }

      HorizontalDivider(color = MetalBorder, thickness = 0.5.dp)

      // Action Buttons: Impersonate/Switch, Edit, Delete
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Switch(
            checked = account.isActive,
            onCheckedChange = onToggleActive,
            colors = SwitchDefaults.colors(
              checkedThumbColor = PureWhite,
              checkedTrackColor = ElectricGreen
            ),
            modifier = Modifier.height(24.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (account.isActive) "Actif" else "Suspendu",
            style = MaterialTheme.typography.bodySmall.copy(
              color = if (account.isActive) ElectricGreen else AluminumMuted,
              fontSize = 10.5.sp
            )
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          if (account.role != UltraProSubAccountRole.GERANT_PRINCIPAL) {
            Button(
              onClick = onSwitchAccount,
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isSimulated) PerformanceRedDark else GraphiteBlack,
                contentColor = if (isSimulated) PureWhite else Color(account.role.badgeColorHex)
              ),
              border = BorderStroke(1.dp, Color(account.role.badgeColorHex)),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(
                if (isSimulated) Icons.Default.ExitToApp else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isSimulated) "QUITTER SIMULATION" else "SE CONNECTER",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          IconButton(
            onClick = onEdit,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = AluminumLight, modifier = Modifier.size(16.dp))
          }

          if (account.role != UltraProSubAccountRole.GERANT_PRINCIPAL) {
            IconButton(
              onClick = onDelete,
              modifier = Modifier.size(32.dp)
            ) {
              Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = PerformanceRed, modifier = Modifier.size(16.dp))
            }
          }
        }
      }
    }
  }
}
