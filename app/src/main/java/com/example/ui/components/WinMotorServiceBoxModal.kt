package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
fun WinMotorServiceBoxModal(
  ratesConfig: WinMotorWorkshopRatesConfig,
  operations: List<WinMotorFlatRateOperation>,
  serviceBoxBaskets: List<ServiceBoxBasketImport>,
  companionClocks: List<CompanionTimeClockEntry>,
  multiPayerOrders: List<MultiPayerSplitOrder>,
  backorderVorItems: List<BackorderVorItem>,
  onUpdateLaborRates: (t1: Int, t2: Int, t3: Int) -> Unit,
  onToggleOperationSelection: (opId: String) -> Unit,
  onImportServiceBoxBasketToOrder: (basketId: String) -> Unit,
  onCreateRepairOrderFromWinMotorSelection: () -> Unit,
  onUpdateCompanionClockStatus: (entryId: String, newStatus: CompanionClockStatus, addCentiemes: Int) -> Unit,
  onUpdateMultiPayerSplit: (orderId: String, clientPct: Int, warrantyPct: Int, internalPct: Int) -> Unit,
  onReceiveAndAllocateVorPart: (vorId: String) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var showEditRates by remember { mutableStateOf(false) }
  var rateT1Text by remember(ratesConfig.rateT1Dzd) { mutableStateOf(ratesConfig.rateT1Dzd.toString()) }
  var rateT2Text by remember(ratesConfig.rateT2Dzd) { mutableStateOf(ratesConfig.rateT2Dzd.toString()) }
  var rateT3Text by remember(ratesConfig.rateT3Dzd) { mutableStateOf(ratesConfig.rateT3Dzd.toString()) }

  val selectedOps = operations.filter { it.isSelectedForOrder }
  val totalSelectedCentiemes = selectedOps.sumOf { it.centiemesHeure }
  val totalSelectedLaborDzd = selectedOps.sumOf { it.computeLaborCostDzd(ratesConfig) }

  val totalSoldCth = companionClocks.sumOf { it.soldCentiemesBarème }
  val totalClockedCth = companionClocks.sumOf { it.clockedCentiemesReal }
  val globalWorkshopEfficiency = if (totalClockedCth > 0) {
    ((totalSoldCth.toDouble() / totalClockedCth.toDouble()) * 100).toInt()
  } else 100

  val pendingVorCount = backorderVorItems.count { !it.isReceivedAndAllocated }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
        .testTag("winmotor_servicebox_modal"),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, TechCyan)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Top Header
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF0C2233), GraphiteBlack, Color(0xFF1F162B))
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
                  .background(TechCyan.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.PrecisionManufacturing,
                  contentDescription = null,
                  tint = TechCyan,
                  modifier = Modifier.size(26.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "DMS MÉCANICAUTO+™ • GESTION AUTOMOBILE INTÉGRÉE",
                    style = MaterialTheme.typography.titleMedium.copy(
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 14.sp
                    )
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = TechCyan.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, TechCyan)
                  ) {
                    Text(
                      text = "DMS OFFICIEL",
                      color = TechCyan,
                      fontWeight = FontWeight.Black,
                      fontSize = 8.5.sp,
                      modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = "DMS Mécanicauto+ : Paniers OEM, Taux T1/T2/T3 (100 cth), Pointage Compagnon & Garantie/VOR",
                  style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 10.5.sp)
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

        // Summary KPI Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(GraphiteBlack)
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          WinMotorKpiPill(
            title = "Grille T1 / T2 / T3",
            value = "${ratesConfig.rateT1Dzd}/${ratesConfig.rateT2Dzd}/${ratesConfig.rateT3Dzd}",
            subtitle = "DA / Heure (100 cth)",
            color = TechCyan,
            modifier = Modifier.weight(1f)
          )
          WinMotorKpiPill(
            title = "Rendement Atelier",
            value = "$globalWorkshopEfficiency% Efficacité",
            subtitle = "${totalSoldCth} cth vendus / ${totalClockedCth} pointés",
            color = if (globalWorkshopEfficiency >= 100) ElectricGreen else MechanicalOrange,
            modifier = Modifier.weight(1f)
          )
          WinMotorKpiPill(
            title = "Reliquats VOR",
            value = "$pendingVorCount Pont(s) Bloqué(s)",
            subtitle = "Priorité Service Box",
            color = if (pendingVorCount > 0) PerformanceRedGlow else ElectricGreen,
            modifier = Modifier.weight(1f)
          )
        }

        // 3 Navigation Tabs
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
                "🛒 Panier Service Box & T1/T2/T3",
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
                "⏱️ Pointage Compagnon ($globalWorkshopEfficiency%)",
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
                "🛡️ Garantie/Cession & VOR ($pendingVorCount)",
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp
              )
            }
          )
        }

        when (selectedTab) {
          0 -> {
            // =================================================================
            // ONGLET 0 : PASSERELLE SERVICE BOX V14 & TAUX T1/T2/T3 (100 CTH)
            // =================================================================
            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              // T1 / T2 / T3 Rate Grid Card
              item {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.dp, TechCyan)
                ) {
                  Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = "GRILLE DE TAUX HORAIRES MULTI-TECHNICITÉ (WINMOTOR)",
                          color = TechCyan,
                          fontWeight = FontWeight.Black,
                          fontSize = 12.sp
                        )
                        Text(
                          text = "Chiffrage constructeur en centièmes d'heure (1h00 = 100 centièmes)",
                          color = AluminumLight,
                          fontSize = 10.5.sp
                        )
                      }
                      OutlinedButton(
                        onClick = { showEditRates = !showEditRates },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, TechCyan),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                      ) {
                        Text(
                          text = if (showEditRates) "Masquer" else "Modifier Taux",
                          color = TechCyan,
                          fontWeight = FontWeight.Bold,
                          fontSize = 10.5.sp
                        )
                      }
                    }

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      TierRateCard(
                        code = "T1 • Courant",
                        rateDzd = ratesConfig.rateT1Dzd,
                        desc = "Vidange, Freins, Pneus",
                        color = ElectricGreen,
                        modifier = Modifier.weight(1f)
                      )
                      TierRateCard(
                        code = "T2 • Complexe",
                        rateDzd = ratesConfig.rateT2Dzd,
                        desc = "Distribution, Embrayage",
                        color = MechanicalOrange,
                        modifier = Modifier.weight(1f)
                      )
                      TierRateCard(
                        code = "T3 • Haute Tech",
                        rateDzd = ratesConfig.rateT3Dzd,
                        desc = "Diag BSI, ADAS, 400V",
                        color = PerformanceRedGlow,
                        modifier = Modifier.weight(1f)
                      )
                    }

                    if (showEditRates) {
                      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                          value = rateT1Text,
                          onValueChange = { rateT1Text = it },
                          label = { Text("T1 (DA/h)", fontSize = 10.sp) },
                          singleLine = true,
                          modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                          value = rateT2Text,
                          onValueChange = { rateT2Text = it },
                          label = { Text("T2 (DA/h)", fontSize = 10.sp) },
                          singleLine = true,
                          modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                          value = rateT3Text,
                          onValueChange = { rateT3Text = it },
                          label = { Text("T3 (DA/h)", fontSize = 10.sp) },
                          singleLine = true,
                          modifier = Modifier.weight(1f)
                        )
                      }
                      Button(
                        onClick = {
                          onUpdateLaborRates(
                            rateT1Text.toIntOrNull() ?: 2500,
                            rateT2Text.toIntOrNull() ?: 3800,
                            rateT3Text.toIntOrNull() ?: 5500
                          )
                          showEditRates = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                      ) {
                        Text("APPLIQUER LA NOUVELLE GRILLE T1 / T2 / T3", color = GraphiteDark, fontWeight = FontWeight.Black, fontSize = 11.sp)
                      }
                    }
                  }
                }
              }

              // Section: Passerelle Panier Peugeot Service Box V14
              item {
                Text(
                  text = "PASSERELLE IMPORT PANIER PEUGEOT SERVICE BOX V14 / CRESCENDO+",
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp
                )
              }

              items(serviceBoxBaskets, key = { it.id }) { basket ->
                ServiceBoxBasketCard(
                  basket = basket,
                  ratesConfig = ratesConfig,
                  onImport = { onImportServiceBoxBasketToOrder(basket.id) }
                )
              }

              // Section: Barème des Temps Constructeur en Centièmes d'Heure
              item {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "BARÈME TEMPS CONSTRUCTEUR (CENTIÈMES D'HEURE)",
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                  )
                  Text(
                    text = "${selectedOps.size} op. (${totalSelectedCentiemes} cth = $totalSelectedLaborDzd DA)",
                    color = ElectricGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                  )
                }
              }

              items(operations, key = { it.id }) { op ->
                WinMotorOperationRow(
                  op = op,
                  ratesConfig = ratesConfig,
                  onToggle = { onToggleOperationSelection(op.id) }
                )
              }

              item {
                Button(
                  onClick = onCreateRepairOrderFromWinMotorSelection,
                  colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_winmotor_generate_or")
                ) {
                  Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = GraphiteDark)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "CRÉER ORDRE DE RÉPARATION (${totalSelectedCentiemes} CTH • $totalSelectedLaborDzd DA)",
                    color = GraphiteDark,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.5.sp
                  )
                }
              }
            }
          }

          1 -> {
            // =================================================================
            // ONGLET 1 : POINTAGE COMPAGNON & PRODUCTIVITÉ ATELIER
            // =================================================================
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
                  border = BorderStroke(1.dp, ElectricGreen)
                ) {
                  Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                      text = "POINTAGE COMPAGNON & ANALYSE TEMPS BARÈMÉ VS TEMPS POINTÉ",
                      color = ElectricGreen,
                      fontWeight = FontWeight.Black,
                      fontSize = 12.sp
                    )
                    Text(
                      text = "Comme sur WinMotor Atelier, chaque compagnon pointe ses centièmes d'heure sur l'Ordre de Réparation pour mesurer le boni/mali de temps et le rendement réel de l'équipe.",
                      color = AluminumLight,
                      fontSize = 11.sp
                    )
                  }
                }
              }

              items(companionClocks, key = { it.id }) { entry ->
                CompanionClockCard(
                  entry = entry,
                  onAddTime = {
                    onUpdateCompanionClockStatus(entry.id, CompanionClockStatus.EN_COURS, 25)
                  },
                  onPauseForPart = {
                    onUpdateCompanionClockStatus(entry.id, CompanionClockStatus.EN_PAUSE_PIECE, 0)
                  },
                  onComplete = {
                    onUpdateCompanionClockStatus(entry.id, CompanionClockStatus.TERMINE, 0)
                  }
                )
              }
            }
          }

          2 -> {
            // =================================================================
            // ONGLET 2 : VENTILATION MULTI-PAYEURS (GARANTIE/CESSION) & RELIQUATS VOR
            // =================================================================
            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              item {
                Text(
                  text = "VENTILATION MULTI-PAYEURS (CLIENT / GARANTIE CONSTRUCTEUR / CESSION)",
                  color = TechCyan,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp
                )
              }

              items(multiPayerOrders, key = { it.id }) { splitOrder ->
                MultiPayerSplitCard(
                  order = splitOrder,
                  onApplyPresetSplit = { clientPct, warrantyPct, internalPct ->
                    onUpdateMultiPayerSplit(splitOrder.id, clientPct, warrantyPct, internalPct)
                  }
                )
              }

              item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "SUIVI DES RELIQUATS MAGASIN & URGENCE « VOR » (PONT IMMOBILISÉ)",
                  color = PerformanceRedGlow,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp
                )
              }

              items(backorderVorItems, key = { it.id }) { vor ->
                BackorderVorCard(
                  item = vor,
                  onReceiveAndAllocate = { onReceiveAndAllocateVorPart(vor.id) }
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
private fun WinMotorKpiPill(
  title: String,
  value: String,
  subtitle: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    color = MetalCard,
    border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.padding(8.dp)) {
      Text(title, color = AluminumMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
      Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 11.5.sp, maxLines = 1)
      Text(subtitle, color = AluminumLight, fontSize = 8.5.sp, maxLines = 1)
    }
  }
}

@Composable
private fun TierRateCard(
  code: String,
  rateDzd: Int,
  desc: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    color = GraphiteBlack,
    border = BorderStroke(1.dp, color.copy(alpha = 0.6f))
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Text(code, color = color, fontWeight = FontWeight.Black, fontSize = 11.sp)
      Text("$rateDzd DA/h", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 13.sp)
      Text(desc, color = AluminumLight, fontSize = 9.sp, maxLines = 1)
    }
  }
}

@Composable
private fun ServiceBoxBasketCard(
  basket: ServiceBoxBasketImport,
  ratesConfig: WinMotorWorkshopRatesConfig,
  onImport: () -> Unit
) {
  val laborTotalDzd = basket.associatedOperations.sumOf { it.computeLaborCostDzd(ratesConfig) }
  val grandTotalHtDzd = basket.totalPartsHtDzd + laborTotalDzd

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.2.dp, if (basket.isImportedIntoActiveOrder) ElectricGreen else MechanicalOrange)
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "${basket.basketReference} • ${basket.vehicleModel}",
            color = PureWhite,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp
          )
          Text(
            text = "${basket.sourceCatalog} • VIN : ${basket.vin} • ${basket.oprDamCode}",
            color = TechCyan,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
          )
        }
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (basket.isImportedIntoActiveOrder) ElectricGreen.copy(alpha = 0.2f) else MechanicalOrange.copy(alpha = 0.2f),
          border = BorderStroke(1.dp, if (basket.isImportedIntoActiveOrder) ElectricGreen else MechanicalOrange)
        ) {
          Text(
            text = if (basket.isImportedIntoActiveOrder) "IMPORTÉ DANS OR" else "PRÊT À IMPORTER",
            color = if (basket.isImportedIntoActiveOrder) ElectricGreen else MechanicalOrange,
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
          Text("Pièces d'origine Service Box (${basket.partsList.size}) :", color = AluminumMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          basket.partsList.forEach { part ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("• [${part.oemRef}] ${part.designation}", color = OffWhite, fontSize = 10.5.sp, modifier = Modifier.weight(1f))
              Text("${part.quantity * part.unitPriceHtDzd} DA", color = MechanicalOrange, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
            }
          }
          HorizontalDivider(color = MetalBorder, modifier = Modifier.padding(vertical = 4.dp))
          Text("Temps barèmés associés (${basket.totalCentiemesHeure} centièmes d'heure) :", color = AluminumMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          basket.associatedOperations.forEach { op ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text(
                text = "• [${op.oemOperationCode}] ${op.label} (${op.laborTier.code})",
                color = TechCyan,
                fontSize = 10.sp,
                modifier = Modifier.weight(1f)
              )
              Text(
                text = "${op.centiemesHeure} cth (${op.computeLaborCostDzd(ratesConfig)} DA)",
                color = ElectricGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            }
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Total Panier HT (Pièces + M.O)", color = AluminumLight, fontSize = 10.sp)
          Text("$grandTotalHtDzd DA HT", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 15.sp)
        }
        Button(
          onClick = onImport,
          colors = ButtonDefaults.buttonColors(
            containerColor = if (basket.isImportedIntoActiveOrder) ElectricGreen else MechanicalOrange
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("btn_import_sbx_${basket.id}")
        ) {
          Icon(
            if (basket.isImportedIntoActiveOrder) Icons.Default.CheckCircle else Icons.Default.Download,
            contentDescription = null,
            tint = GraphiteDark,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (basket.isImportedIntoActiveOrder) "RÉ-IMPORTER DANS OR" else "IMPORTER PANIER 1-CLIC",
            color = GraphiteDark,
            fontWeight = FontWeight.Black,
            fontSize = 10.5.sp
          )
        }
      }
    }
  }
}

@Composable
private fun WinMotorOperationRow(
  op: WinMotorFlatRateOperation,
  ratesConfig: WinMotorWorkshopRatesConfig,
  onToggle: () -> Unit
) {
  val tierColor = when (op.laborTier) {
    WinMotorLaborTier.T1_COURANT -> ElectricGreen
    WinMotorLaborTier.T2_MECANIQUE_COMPLEXE -> MechanicalOrange
    WinMotorLaborTier.T3_HAUTE_TECHNICITE -> PerformanceRedGlow
  }
  val costDzd = op.computeLaborCostDzd(ratesConfig)

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = if (op.isSelectedForOrder) Color(0xFF13262E) else MetalCard,
    border = BorderStroke(1.dp, if (op.isSelectedForOrder) TechCyan else MetalBorder),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onToggle() }
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
        Checkbox(
          checked = op.isSelectedForOrder,
          onCheckedChange = { onToggle() }
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = tierColor.copy(alpha = 0.2f),
              border = BorderStroke(1.dp, tierColor)
            ) {
              Text(
                text = "${op.laborTier.code} • ${op.oemOperationCode}",
                color = tierColor,
                fontWeight = FontWeight.Black,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = op.formattedDurationReadable,
              color = TechCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp,
              fontFamily = FontFamily.Monospace
            )
          }
          Text(op.label, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          Text(op.vehicleApplication, color = AluminumLight, fontSize = 10.sp)
        }
      }
      Column(horizontalAlignment = Alignment.End) {
        Text("$costDzd DA", color = ElectricGreen, fontWeight = FontWeight.Black, fontSize = 13.5.sp)
        Text("${ratesConfig.getRateForTier(op.laborTier)} DA/h", color = AluminumMuted, fontSize = 9.5.sp)
      }
    }
  }
}

@Composable
private fun CompanionClockCard(
  entry: CompanionTimeClockEntry,
  onAddTime: () -> Unit,
  onPauseForPart: () -> Unit,
  onComplete: () -> Unit
) {
  val statusColor = when (entry.status) {
    CompanionClockStatus.EN_COURS -> TechCyan
    CompanionClockStatus.EN_PAUSE_PIECE -> MechanicalOrange
    CompanionClockStatus.TERMINE -> ElectricGreen
  }
  val effColor = if (entry.productivityPercent >= 100) ElectricGreen else MechanicalOrange

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.7f))
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "👷 ${entry.companionName} (${entry.companionMatricule})",
            color = PureWhite,
            fontWeight = FontWeight.Black,
            fontSize = 13.5.sp
          )
          Text(
            text = "${entry.specialty} • ${entry.bayName}",
            color = AluminumLight,
            fontSize = 10.5.sp
          )
        }
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = statusColor.copy(alpha = 0.2f),
          border = BorderStroke(1.dp, statusColor)
        ) {
          Text(
            text = entry.status.badgeText,
            color = statusColor,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp,
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
          Text("${entry.repairOrderNumber} • ${entry.vehicleAndPlate}", color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          Text("Intervention : ${entry.operationTitle} (${entry.laborTier.code})", color = OffWhite, fontSize = 11.sp)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Barème Vendu : ${entry.soldCentiemesBarème} cth", color = AluminumLight, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace)
            Text("Pointé Réel : ${entry.clockedCentiemesReal} cth", color = MechanicalOrange, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace)
            Text("Rendement : ${entry.productivityPercent}%", color = effColor, fontWeight = FontWeight.Black, fontSize = 11.sp)
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onAddTime,
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, TechCyan),
          modifier = Modifier.weight(1f)
        ) {
          Text("+25 cth Pointés", color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
        OutlinedButton(
          onClick = onPauseForPart,
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, MechanicalOrange),
          modifier = Modifier.weight(1f)
        ) {
          Text("Pause Pièce", color = MechanicalOrange, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
        Button(
          onClick = onComplete,
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
          modifier = Modifier.weight(1f)
        ) {
          Text("Clôturer", color = GraphiteDark, fontWeight = FontWeight.Black, fontSize = 10.sp)
        }
      }
    }
  }
}

@Composable
private fun MultiPayerSplitCard(
  order: MultiPayerSplitOrder,
  onApplyPresetSplit: (clientPct: Int, warrantyPct: Int, internalPct: Int) -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, TechCyan)
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text("${order.repairOrderNumber} • ${order.vehicleModel}", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 13.sp)
          Text("Client : ${order.clientName} • Dossier Garantie : ${order.warrantyClaimReference}", color = AluminumLight, fontSize = 10.5.sp)
        }
        Text("${order.totalOrderAmountHtDzd} DA HT", color = ElectricGreen, fontWeight = FontWeight.Black, fontSize = 14.sp)
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        WinMotorKpiPill(
          title = "Part Client (${order.clientSharePercent}%)",
          value = "${order.clientAmountDzd} DA",
          subtitle = "Facture Client",
          color = TechCyan,
          modifier = Modifier.weight(1f)
        )
        WinMotorKpiPill(
          title = "Garantie PSA (${order.manufacturerWarrantyPercent}%)",
          value = "${order.warrantyAmountDzd} DA",
          subtitle = "Rappel Constructeur",
          color = ElectricGreen,
          modifier = Modifier.weight(1f)
        )
        WinMotorKpiPill(
          title = "Cession (${order.internalCessionPercent}%)",
          value = "${order.internalCessionAmountDzd} DA",
          subtitle = "Interne Atelier",
          color = MechanicalOrange,
          modifier = Modifier.weight(1f)
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedButton(
          onClick = { onApplyPresetSplit(100, 0, 0) },
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text("100% Client", color = PureWhite, fontSize = 9.5.sp)
        }
        OutlinedButton(
          onClick = { onApplyPresetSplit(35, 55, 10) },
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text("Rappel 35/55/10%", color = TechCyan, fontSize = 9.5.sp)
        }
        OutlinedButton(
          onClick = { onApplyPresetSplit(0, 100, 0) },
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text("100% Garantie", color = ElectricGreen, fontSize = 9.5.sp)
        }
      }
    }
  }
}

@Composable
private fun BackorderVorCard(
  item: BackorderVorItem,
  onReceiveAndAllocate: () -> Unit
) {
  val borderColor = if (item.isReceivedAndAllocated) ElectricGreen else PerformanceRedGlow

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
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
          Text(
            text = "[${item.oemReference}] ${item.partDesignation}",
            color = PureWhite,
            fontWeight = FontWeight.Black,
            fontSize = 12.5.sp
          )
          Text(
            text = "${item.linkedRepairOrderNumber} • ${item.vehicleModel} (${item.clientName})",
            color = AluminumLight,
            fontSize = 10.5.sp
          )
        }
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = borderColor.copy(alpha = 0.2f),
          border = BorderStroke(1.dp, borderColor)
        ) {
          Text(
            text = if (item.isReceivedAndAllocated) "AFFECTÉE À L'OR" else item.priority.badgeText,
            color = borderColor,
            fontWeight = FontWeight.Black,
            fontSize = 8.5.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Text(
        text = "Fournisseur : ${item.supplierName} • ETA : ${item.expectedArrivalTime} • ${item.blockedBayLabel ?: "Véhicule roulant"}",
        color = MechanicalOrange,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold
      )

      if (!item.isReceivedAndAllocated) {
        Button(
          onClick = onReceiveAndAllocate,
          colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Inventory, contentDescription = null, tint = GraphiteDark, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "RÉCEPTIONNER LA PIÈCE & DÉBLOQUER ${item.linkedRepairOrderNumber}",
            color = GraphiteDark,
            fontWeight = FontWeight.Black,
            fontSize = 10.5.sp
          )
        }
      }
    }
  }
}
