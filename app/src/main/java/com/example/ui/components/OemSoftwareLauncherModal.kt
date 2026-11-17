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
import com.example.ui.theme.*

@Composable
fun OemSoftwareLauncherModal(
  tools: List<OemSoftwareTool>,
  config: WorkshopSoftwareConfiguration,
  supplement: FullSoftwarePackageSupplement,
  selectedTool: OemSoftwareTool?,
  selectedGroupFilter: OemManufacturerGroup?,
  onSelectGroupMode: (OemManufacturerGroup) -> Unit,
  onLaunchTool: (OemSoftwareTool) -> Unit,
  onOpenPurchaseSupplementModal: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var currentSelectedTool by remember(selectedTool, tools) {
    mutableStateOf(selectedTool ?: tools.firstOrNull())
  }
  var filterGroup by remember(selectedGroupFilter) { mutableStateOf(selectedGroupFilter) }

  val filteredTools = remember(tools, filterGroup, searchQuery) {
    tools.filter { tool ->
      val matchesGroup = filterGroup == null || tool.group == filterGroup
      val matchesSearch = searchQuery.isBlank() ||
        tool.name.contains(searchQuery, ignoreCase = true) ||
        tool.description.contains(searchQuery, ignoreCase = true) ||
        tool.group.coveredBrands.any { it.contains(searchQuery, ignoreCase = true) }

      matchesGroup && matchesSearch
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
        .testTag("modal_oem_software_launcher"),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, TechCyan)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
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
                .background(Brush.linearGradient(listOf(TechCyan, MechanicalOrange))),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Dns, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "HUB LOGICIELS CONSTRUCTEURS OEM",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                  )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = TechCyanDark,
                  border = BorderStroke(0.5.dp, TechCyan)
                ) {
                  Text(
                    text = "EPC & MANUELS",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = TechCyan,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
              Text(
                text = "Accès direct aux logiciels officiels de recherche de pièces et manuels d'atelier",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.5.sp)
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- FULL ALL-IN-ONE SOFTWARE PACK (+10,000 DA) BANNER ---
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = if (config.hasFullSoftwarePackageUnlocked) Color(0xFF14241B) else MetalCard),
          border = BorderStroke(1.dp, if (config.hasFullSoftwarePackageUnlocked) ElectricGreen else PerformanceRedGlow)
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Icon(
                if (config.hasFullSoftwarePackageUnlocked) Icons.Default.CheckCircle else Icons.Default.WorkspacePremium,
                contentDescription = null,
                tint = if (config.hasFullSoftwarePackageUnlocked) ElectricGreen else PerformanceRedGlow,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = if (config.hasFullSoftwarePackageUnlocked) {
                      "PACK INTÉGRAL TOUS LOGICIELS OEM ACTIF"
                    } else {
                      "OPTION : PACK INTÉGRAL TOUS LOGICIELS (+10 000 DA)"
                    },
                    style = MaterialTheme.typography.labelMedium.copy(
                      color = if (config.hasFullSoftwarePackageUnlocked) ElectricGreen else PureWhite,
                      fontWeight = FontWeight.Bold
                    )
                  )
                  if (config.hasFullSoftwarePackageUnlocked) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(shape = RoundedCornerShape(4.dp), color = ElectricGreenDark) {
                      Text(
                        text = "ILLIMITÉ",
                        style = MaterialTheme.typography.labelSmall.copy(color = ElectricGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                      )
                    }
                  }
                }
                Text(
                  text = if (config.hasFullSoftwarePackageUnlocked) {
                    "Accès complet débloqué : ETKA, ServiceBox, Dialogys, Microcat, ISTA, WIS/EPC & TecDoc Pro."
                  } else {
                    "Débloquez l'accès universel à 100% des catalogues et manuels constructeurs pour 10 000 DA / an."
                  },
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 10.5.sp)
                )
              }
            }

            if (!config.hasFullSoftwarePackageUnlocked) {
              Button(
                onClick = onOpenPurchaseSupplementModal,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("btn_open_supplement_10k")
              ) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("DÉBLOQUER (+10 000 DA)", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
              }
            } else {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = GraphiteBlack,
                border = BorderStroke(0.5.dp, ElectricGreen)
              ) {
                Text(
                  text = "100% DISPONIBLE",
                  style = MaterialTheme.typography.labelSmall.copy(color = ElectricGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- WORKSHOP MODE SELECTOR (VOLKSWAGEN / STELLANTIS / RENAULT / ASIAN / UNIVERSAL) ---
        Text(
          text = "CHOISIR LE MODE CONSTRUCTEUR DE VOTRE ATELIER / CONCESSION :",
          style = MaterialTheme.typography.labelSmall.copy(
            color = AluminumMuted,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        )

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(vertical = 2.dp)
        ) {
          item {
            val isAll = filterGroup == null
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isAll) TechCyan else MetalCard,
              border = BorderStroke(1.dp, if (isAll) TechCyan else MetalBorder),
              modifier = Modifier.clickable { filterGroup = null }
            ) {
              Text(
                text = "🌐 Tous les Programmes",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (isAll) GraphiteDark else PureWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                ),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
              )
            }
          }

          items(OemManufacturerGroup.entries) { group ->
            val isSelected = filterGroup == group
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) Color(group.themeColorHex) else MetalCard,
              border = BorderStroke(1.dp, if (isSelected) Color(group.themeColorHex) else MetalBorder),
              modifier = Modifier.clickable {
                filterGroup = group
                onSelectGroupMode(group)
              }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(text = group.iconEmoji, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Mode ${group.shortCode}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) GraphiteDark else PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Field
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Rechercher un logiciel (ex: ETKA, ServiceBox, Dialogys, HaynesPro, VIN...)", fontSize = 11.5.sp, color = AluminumMuted) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AluminumMuted, modifier = Modifier.size(16.dp)) },
          shape = RoundedCornerShape(10.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MetalCard,
            unfocusedContainerColor = MetalCard,
            focusedBorderColor = TechCyan,
            unfocusedBorderColor = MetalBorder,
            focusedTextColor = PureWhite,
            unfocusedTextColor = PureWhite
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // --- SOFTWARE TOOLS LIST & SELECTED TOOL PREVIEW ---
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredTools) { tool ->
            val isCurrent = currentSelectedTool?.id == tool.id
            val groupColor = Color(tool.group.themeColorHex)

            Card(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("card_oem_tool_${tool.id}")
                .clickable { currentSelectedTool = tool },
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isCurrent) Color(0xFF1E262E) else MetalCard
              ),
              border = BorderStroke(
                if (isCurrent) 1.5.dp else 1.dp,
                if (isCurrent) groupColor else MetalBorder
              )
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Header: Name, Group, Version
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(text = tool.group.iconEmoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                      Text(
                        text = tool.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                          color = PureWhite,
                          fontWeight = FontWeight.Bold,
                          fontSize = 13.5.sp
                        )
                      )
                      Text(
                        text = "${tool.group.label} • ${tool.officialVersion}",
                        style = MaterialTheme.typography.bodySmall.copy(
                          color = groupColor,
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Medium
                        )
                      )
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(tool.toolType.badgeColorHex).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, Color(tool.toolType.badgeColorHex))
                  ) {
                    Text(
                      text = tool.toolType.label.uppercase(),
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(tool.toolType.badgeColorHex),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                      ),
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                // Description
                Text(
                  text = tool.description,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = AluminumLight,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp
                  )
                )

                // Covered Brands Chips
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  tool.group.coveredBrands.take(4).forEach { brand ->
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = GraphiteBlack
                    ) {
                      Text(
                        text = brand,
                        style = MaterialTheme.typography.bodySmall.copy(
                          color = AluminumGray,
                          fontSize = 9.5.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                  if (tool.group.coveredBrands.size > 4) {
                    Surface(shape = RoundedCornerShape(4.dp), color = GraphiteBlack) {
                      Text(
                        text = "+${tool.group.coveredBrands.size - 4}",
                        style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 9.5.sp),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                      )
                    }
                  }
                }

                // Key Features Checklist
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                  tool.keyFeatures.take(2).forEach { feature ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Check, contentDescription = null, tint = groupColor, modifier = Modifier.size(13.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(text = feature, style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 10.5.sp))
                    }
                  }
                }

                HorizontalDivider(color = MetalBorder, thickness = 0.5.dp)

                // Actions: Launch, Search Parts
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LockOpen, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = tool.accessStatus,
                      style = MaterialTheme.typography.bodySmall.copy(color = ElectricGreen, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    )
                  }

                  Button(
                    onClick = { onLaunchTool(tool) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = groupColor),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_launch_${tool.id}")
                  ) {
                    Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("OUVRIR LE LOGICIEL", color = GraphiteDark, fontSize = 10.5.sp, fontWeight = FontWeight.Black)
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
