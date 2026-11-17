package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalUriHandler
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
fun WebOemExplodedCatalogsModal(
  platforms: List<FreeWebOemCatalogPlatform>,
  explodedDiagrams: List<OemExplodedDiagramSheet>,
  onSelectCalloutForQuote: (ExplodedCalloutItem, OemExplodedDiagramSheet) -> Unit,
  onAddCalloutToSmartCart: (ExplodedCalloutItem, OemExplodedDiagramSheet) -> Unit = { _, _ -> },
  onDismiss: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Vues Éclatées & Arborescences OEM, 1 = 6 Plateformes Web Gratuites (CatCar, 7Zap, PartsSouq...)
  var selectedBrandFilter by remember { mutableStateOf(OemBrandGroupFilter.TOUS) }
  val filteredDiagrams = remember(explodedDiagrams, selectedBrandFilter) {
    if (selectedBrandFilter == OemBrandGroupFilter.TOUS) explodedDiagrams
    else explodedDiagrams.filter { it.brandGroup == selectedBrandFilter }
  }
  var activeDiagramId by remember(filteredDiagrams) {
    mutableStateOf(filteredDiagrams.firstOrNull()?.id ?: "")
  }
  val activeDiagram = filteredDiagrams.find { it.id == activeDiagramId } ?: filteredDiagrams.firstOrNull()
  var selectedCalloutNumber by remember(activeDiagram) {
    mutableIntStateOf(activeDiagram?.callouts?.firstOrNull()?.calloutNumber ?: 1)
  }
  val uriHandler = LocalUriHandler.current

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)
        .testTag("modal_web_oem_exploded_catalogs"),
      shape = RoundedCornerShape(16.dp),
      color = GraphiteBlack,
      border = BorderStroke(1.dp, TechCyan)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(MetalCard)
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(TechCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.AccountTree, contentDescription = null, tint = TechCyan, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "CATALOGUES WEB OEM GRATUITS & VUES ÉCLATÉES",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
              )
              Text(
                text = "CatCar.info • 7Zap • PartsSouq • NemigaParts • IFInterface • WebAutoCats",
                color = AluminumLight,
                fontSize = 10.sp
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite)
          }
        }

        // Tabs
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(MetalSurface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (selectedTab == 0) TechCyanDark else GraphiteBlack,
            border = BorderStroke(1.dp, if (selectedTab == 0) TechCyan else MetalBorder),
            modifier = Modifier
              .weight(1f)
              .clickable { selectedTab = 0 }
          ) {
            Row(
              modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Schema, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("VUES ÉCLATÉES & REPÈRES OEM", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
            }
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (selectedTab == 1) MechanicalOrangeDark else GraphiteBlack,
            border = BorderStroke(1.dp, if (selectedTab == 1) MechanicalOrange else MetalBorder),
            modifier = Modifier
              .weight(1f)
              .clickable { selectedTab = 1 }
          ) {
            Row(
              modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Public, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("6 PORTAILS OEM GRATUITS (${platforms.size})", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
            }
          }
        }

        // Brand Group Filter Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OemBrandGroupFilter.entries.forEach { group ->
            val isSelected = selectedBrandFilter == group
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = if (isSelected) PerformanceRed else MetalCard,
              border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
              modifier = Modifier.clickable { selectedBrandFilter = group }
            ) {
              Text(
                text = group.label,
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
          }
        }

        if (selectedTab == 0) {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
          ) {
            // Sheet selector pills
            item {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                filteredDiagrams.forEach { sheet ->
                  val isCurrent = sheet.id == activeDiagram?.id
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isCurrent) MetalCard else GraphiteBlack,
                    border = BorderStroke(1.dp, if (isCurrent) TechCyan else MetalBorder),
                    modifier = Modifier.clickable {
                      activeDiagramId = sheet.id
                      selectedCalloutNumber = sheet.callouts.firstOrNull()?.calloutNumber ?: 1
                    }
                  ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                      Text(sheet.platformSource, color = TechCyan, fontWeight = FontWeight.Black, fontSize = 9.5.sp)
                      Text(sheet.vehicleTitle, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                      Text(sheet.diagramCode, color = AluminumLight, fontSize = 9.5.sp)
                    }
                  }
                }
              }
            }

            if (activeDiagram != null) {
              item {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.7f))
                ) {
                  Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(activeDiagram.diagramCode, color = TechCyan, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        Text(activeDiagram.diagramTitle, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        Text(activeDiagram.engineAndGearboxCode, color = WarningYellow, fontWeight = FontWeight.SemiBold, fontSize = 10.5.sp)
                      }
                      OutlinedButton(
                        onClick = {
                          runCatching { uriHandler.openUri(activeDiagram.externalDirectUrl) }
                        },
                        border = BorderStroke(1.dp, TechCyan),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                      ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null, tint = TechCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ouvrir Web OEM", color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                      }
                    }

                    // Interactive Exploded View Blueprint Canvas with Callouts [1], [2], [3]...
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = Color(0xFF0B1622),
                      border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.5f)),
                      modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp)
                    ) {
                      Box(modifier = Modifier.fillMaxSize()) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                          val gridStep = 28.dp.toPx()
                          var x = 0f
                          while (x < size.width) {
                            drawLine(
                              color = Color(0xFF1B344E),
                              start = Offset(x, 0f),
                              end = Offset(x, size.height),
                              strokeWidth = 1f
                            )
                            x += gridStep
                          }
                          var y = 0f
                          while (y < size.height) {
                            drawLine(
                              color = Color(0xFF1B344E),
                              start = Offset(0f, y),
                              end = Offset(size.width, y),
                              strokeWidth = 1f
                            )
                            y += gridStep
                          }

                          // Draw technical exploded assembly axis & mechanical blocks
                          val cy = size.height * 0.52f
                          drawLine(
                            color = Color(0xFF4FC3F7),
                            start = Offset(size.width * 0.1f, cy),
                            end = Offset(size.width * 0.9f, cy),
                            strokeWidth = 3f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 8f), 0f)
                          )

                          val count = activeDiagram.callouts.size.coerceAtLeast(1)
                          activeDiagram.callouts.forEachIndexed { idx, callout ->
                            val cx = size.width * (0.18f + (idx * (0.64f / count.coerceAtLeast(1))))
                            val isSelected = callout.calloutNumber == selectedCalloutNumber
                            drawRoundRect(
                              color = if (isSelected) Color(0x33FF7A00) else Color(0x2229B6F6),
                              topLeft = Offset(cx - 32f, cy - 38f),
                              size = Size(64f, 76f),
                              style = Stroke(width = if (isSelected) 4f else 2f)
                            )
                            drawCircle(
                              color = if (isSelected) Color(0xFFFF7A00) else Color(0xFF29B6F6),
                              radius = 18f,
                              center = Offset(cx, cy)
                            )
                          }
                        }

                        // Clickable Callout Badges overlay
                        Row(
                          modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                          horizontalArrangement = Arrangement.SpaceEvenly,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          activeDiagram.callouts.forEach { callout ->
                            val isSelected = callout.calloutNumber == selectedCalloutNumber
                            Surface(
                              shape = CircleShape,
                              color = if (isSelected) MechanicalOrange else TechCyanDark,
                              border = BorderStroke(2.dp, PureWhite),
                              modifier = Modifier
                                .size(if (isSelected) 42.dp else 34.dp)
                                .clickable { selectedCalloutNumber = callout.calloutNumber }
                            ) {
                              Box(contentAlignment = Alignment.Center) {
                                Text(
                                  text = "[${callout.calloutNumber}]",
                                  color = PureWhite,
                                  fontWeight = FontWeight.Black,
                                  fontSize = if (isSelected) 13.sp else 11.sp
                                )
                              }
                            }
                          }
                        }

                        Text(
                          text = "CLIQUEZ SUR UN REPÈRE NUMÉROTÉ [1..${activeDiagram.callouts.size}] POUR ISOLER LA RÉFÉRENCE OEM",
                          color = AluminumLight,
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Bold,
                          modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 6.dp)
                        )
                      }
                    }
                  }
                }
              }

              // Callout table rows
              items(activeDiagram.callouts, key = { it.calloutNumber }) { callout ->
                val isHighlighted = callout.calloutNumber == selectedCalloutNumber
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedCalloutNumber = callout.calloutNumber },
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = if (isHighlighted) Color(0xFF261F1B) else MetalCard
                  ),
                  border = BorderStroke(
                    width = if (isHighlighted) 1.5.dp else 1.dp,
                    color = if (isHighlighted) MechanicalOrange else MetalBorder
                  )
                ) {
                  Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = if (isHighlighted) MechanicalOrange else TechCyanDark
                        ) {
                          Text(
                            text = "REPÈRE [${callout.calloutNumber}]",
                            color = PureWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                          )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                          text = callout.partName,
                          color = PureWhite,
                          fontWeight = FontWeight.Bold,
                          fontSize = 12.sp
                        )
                      }
                      Text(
                        text = "${callout.estimatedOemPriceDzd} DA",
                        color = ElectricGreen,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                      )
                    }

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text(
                          text = "Réf OEM : ${callout.oemReference}",
                          color = MechanicalOrange,
                          fontFamily = FontFamily.Monospace,
                          fontWeight = FontWeight.Black,
                          fontSize = 12.sp
                        )
                        if (callout.replacementChainRef != null) {
                          Text(
                            text = "Chaîne remplacement : ${callout.replacementChainRef}",
                            color = AluminumLight,
                            fontSize = 10.sp
                          )
                        }
                      }
                      Surface(shape = RoundedCornerShape(4.dp), color = GraphiteBlack) {
                        Text(
                          text = "Qté requise : ×${callout.quantityRequired}",
                          color = PureWhite,
                          fontWeight = FontWeight.Bold,
                          fontSize = 10.sp,
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                      }
                    }

                    Text(
                      text = "📌 Condition montage : ${callout.technicalConditionNote}",
                      color = WarningYellow,
                      fontSize = 10.5.sp
                    )

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Button(
                        onClick = { onAddCalloutToSmartCart(callout, activeDiagram) },
                        colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                        modifier = Modifier.weight(1.1f),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
                      ) {
                        Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                          text = "🛒 PANIER COMPATIBILITÉS & STOCK",
                          color = GraphiteDark,
                          fontWeight = FontWeight.Black,
                          fontSize = 10.sp
                        )
                      }

                      Button(
                        onClick = { onSelectCalloutForQuote(callout, activeDiagram) },
                        colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
                      ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                          text = "AJOUTER AU DEVIS (${callout.oemReference})",
                          fontWeight = FontWeight.Bold,
                          fontSize = 10.sp
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        } else {
          // Tab 1: The 6 Free Web OEM Catalogs (CatCar.info, 7Zap, PartsSouq, NemigaParts, IFInterface, WebAutoCats)
          val filteredPlatforms = remember(platforms, selectedBrandFilter) {
            if (selectedBrandFilter == OemBrandGroupFilter.TOUS) platforms
            else platforms.filter { it.targetBrandGroups.contains(selectedBrandFilter) }
          }
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
          ) {
            items(filteredPlatforms, key = { it.id }) { platform ->
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.7f))
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(platform.name, color = PureWhite, fontWeight = FontWeight.Black, fontSize = 14.sp)
                      Text(platform.baseUrl, color = TechCyan, fontFamily = FontFamily.Monospace, fontSize = 10.5.sp)
                    }
                    Surface(shape = RoundedCornerShape(6.dp), color = MechanicalOrangeDark) {
                      Text(
                        text = platform.specialtyBadge,
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                      )
                    }
                  }

                  Text(
                    text = "Marques couvertes : ${platform.supportedBrandsText}",
                    color = ElectricGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                  )

                  Text(
                    text = platform.architectureHighlights,
                    color = OffWhite,
                    fontSize = 11.sp
                  )

                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GraphiteBlack,
                    border = BorderStroke(1.dp, MetalBorder),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                      Text(
                        text = "🧪 SCÉNARIO DE TEST / REVERSE-ENGINEERING :",
                        color = WarningYellow,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.5.sp
                      )
                      Text(
                        text = platform.testScenarioExample,
                        color = AluminumLight,
                        fontSize = 10.5.sp
                      )
                    }
                  }

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      if (platform.supportsVinLookup) {
                        Surface(shape = RoundedCornerShape(4.dp), color = TechCyanDark) {
                          Text("RECHERCHE VIN", color = PureWhite, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                      }
                      if (platform.supportsGearboxEngineSort) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF1B5E20)) {
                          Text("TRI BOÎTE & MOTEUR", color = PureWhite, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                      }
                      if (!platform.requiresAccount) {
                        Surface(shape = RoundedCornerShape(4.dp), color = MetalSurface) {
                          Text("GRATUIT SANS COMPTE", color = ElectricGreen, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                      }
                    }

                    Button(
                      onClick = {
                        runCatching { uriHandler.openUri(platform.baseUrl) }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark),
                      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                      Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Ouvrir Portail", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
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
}
