package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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

private enum class StockErpTab(val label: String) {
  DASHBOARD("📊 Valorisation & KPI"),
  INVOICE_OCR_LABELS("🧾 Scan Bon d'Achat & Étiquettes"),
  CATALOG("📦 Stock & Rayons"),
  MOVEMENTS("🔄 Mouvements E/S"),
  SUPPLIER_ORDERS("🚚 Commandes BC"),
  SCANNER_NEW("📷 Scanner & +Article")
}

@Composable
fun BarcodeInventoryScannerModal(
  inventory: List<BarcodeInventoryItem>,
  movementLogs: List<StockMovementLog> = emptyList(),
  supplierOrders: List<SupplierPurchaseOrder> = emptyList(),
  scannedInvoices: List<ScannedPurchaseInvoice> = emptyList(),
  printedLabels: List<PrintedThermalLabel> = emptyList(),
  onUpdateStock: (itemId: String, delta: Int) -> Unit,
  onRecordMovement: (
    itemId: String,
    type: StockMovementType,
    quantity: Int,
    destinationRef: String,
    operatorName: String
  ) -> Unit = { id, t, q, _, _ ->
    onUpdateStock(id, if (t.signMultiplier >= 0) q else -q)
  },
  onCreateNewItem: (
    partName: String,
    oemRef: String,
    barcode: String,
    brand: String,
    category: String,
    initialStock: Int,
    minThreshold: Int,
    purchaseCostDzd: Int,
    salePriceDzd: Int,
    shelfLocation: String,
    supplierName: String,
    rotationClass: AbcRotationClass
  ) -> Unit = { _, _, _, _, _, _, _, _, _, _, _, _ -> },
  onUpdateItemPricing: (
    itemId: String,
    newPurchaseCostDzd: Int,
    newSalePriceDzd: Int,
    newMinThreshold: Int,
    newShelfLocation: String
  ) -> Unit = { _, _, _, _, _ -> },
  onGenerateAutoRestockOrder: () -> Unit = {},
  onReceiveSupplierOrder: (orderId: String) -> Unit = {},
  onUpdateInvoiceLinePriceChoice: (
    invoiceId: String,
    lineId: String,
    choice: SalePriceValidationChoice,
    customSalePriceDzd: Int
  ) -> Unit = { _, _, _, _ -> },
  onUpdateAllInvoiceLinesPriceStrategy: (
    invoiceId: String,
    keepSamePriceForAll: Boolean
  ) -> Unit = { _, _ -> },
  onUpdateInvoiceLinePlacementAndQty: (
    invoiceId: String,
    lineId: String,
    rayon: String,
    etagere: String,
    bac: String,
    quantity: Int
  ) -> Unit = { _, _, _, _, _, _ -> },
  onAddLineToInvoice: (
    invoiceId: String,
    partName: String,
    oemRef: String,
    brand: String,
    category: String,
    quantity: Int,
    purchaseCostDzd: Int,
    salePriceDzd: Int,
    rayon: String,
    etagere: String,
    bac: String
  ) -> Unit = { _, _, _, _, _, _, _, _, _, _, _ -> },
  onIntegrateInvoiceAndGenerateLabels: (invoiceId: String) -> Unit = {},
  onPrintSingleLabel: (labelId: String) -> Unit = {},
  onPrintAllLabelsForInvoice: (invoiceNumber: String) -> Unit = {},
  onAddToQuote: (BarcodeInventoryItem) -> Unit,
  onDismiss: () -> Unit
) {
  var activeTab by remember { mutableStateOf(StockErpTab.DASHBOARD) }
  var scannedBarcode by remember { mutableStateOf(inventory.firstOrNull()?.barcode ?: "4047024123456") }

  // Catalog filter state
  var searchInventoryQuery by remember { mutableStateOf("") }
  var selectedCategoryFilter by remember { mutableStateOf("TOUTES") }
  var filterLowStockOnly by remember { mutableStateOf(false) }
  var editingItemId by remember { mutableStateOf<String?>(null) }

  // KPI computations
  val totalUnitsInStock = remember(inventory) { inventory.sumOf { it.currentStock } }
  val totalPurchaseValueDzd = remember(inventory) { inventory.sumOf { it.totalPurchaseValueDzd } }
  val totalSaleValueDzd = remember(inventory) { inventory.sumOf { it.totalSaleValueDzd } }
  val totalPotentialMarginDzd = remember(totalSaleValueDzd, totalPurchaseValueDzd) {
    (totalSaleValueDzd - totalPurchaseValueDzd).coerceAtLeast(0L)
  }
  val lowStockCount = remember(inventory) { inventory.count { it.isLowStock } }
  val outOfStockCount = remember(inventory) { inventory.count { it.isOutOfStock } }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = 20.dp, bottom = 12.dp, start = 8.dp, end = 8.dp)
        .testTag("modal_barcode_scanner"),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(
        1.5.dp,
        Brush.horizontalGradient(listOf(MechanicalOrange, TechCyan, PerformanceRed))
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(14.dp)
      ) {
        // Top ERP Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                  Brush.linearGradient(listOf(MechanicalOrangeDark, PerformanceRedDark))
                )
                .border(1.dp, MechanicalOrange, RoundedCornerShape(10.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Inventory,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "MEKANIK+ STOCKPRO ERP",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    fontSize = 15.sp,
                    letterSpacing = 0.6.sp
                  )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(5.dp),
                  color = TechCyanDark.copy(alpha = 0.5f),
                  border = BorderStroke(0.8.dp, TechCyan)
                ) {
                  Text(
                    text = "WMS & PIÈCES DE RECHANGE",
                    color = TechCyanGlow,
                    fontWeight = FontWeight.Black,
                    fontSize = 8.5.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
              Text(
                text = "Valorisation HT/TTC • Rayonnage • Traçabilité E/S • Bons de Commande Fournisseurs",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumLight,
                  fontSize = 10.5.sp
                )
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(MetalCard)
          ) {
            Icon(
              Icons.Default.Close,
              contentDescription = "Fermer",
              tint = PureWhite,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 5 ERP Navigation Tabs
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(StockErpTab.entries) { tab ->
            val isSelected = activeTab == tab
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) PerformanceRed else MetalCard,
              border = BorderStroke(
                1.dp,
                if (isSelected) PerformanceRedGlow else MetalBorder
              ),
              modifier = Modifier
                .clickable { activeTab = tab }
                .testTag("stock_erp_tab_${tab.name}")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = tab.label,
                  color = if (isSelected) PureWhite else AluminumLight,
                  fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                  fontSize = 11.5.sp
                )
                if (tab == StockErpTab.CATALOG && lowStockCount > 0) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    shape = CircleShape,
                    color = if (isSelected) GraphiteBlack else PerformanceRedDark
                  ) {
                    Text(
                      text = "$lowStockCount",
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.sp,
                      modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Active Tab Content
        when (activeTab) {
          StockErpTab.DASHBOARD -> {
            StockErpDashboardView(
              inventory = inventory,
              totalUnits = totalUnitsInStock,
              totalPurchaseDzd = totalPurchaseValueDzd,
              totalSaleDzd = totalSaleValueDzd,
              totalMarginDzd = totalPotentialMarginDzd,
              lowStockCount = lowStockCount,
              outOfStockCount = outOfStockCount,
              pendingInvoicesCount = scannedInvoices.count { !it.isIntegrated },
              unprintedLabelsCount = printedLabels.filter { !it.isPrinted }.sumOf { it.copiesToPrint },
              onOpenScanPurchaseInvoiceTab = {
                activeTab = StockErpTab.INVOICE_OCR_LABELS
              },
              onGenerateAutoRestockOrder = {
                onGenerateAutoRestockOrder()
                activeTab = StockErpTab.SUPPLIER_ORDERS
              },
              onOpenLowStockCatalog = {
                filterLowStockOnly = true
                activeTab = StockErpTab.CATALOG
              }
            )
          }

          StockErpTab.INVOICE_OCR_LABELS -> {
            PurchaseInvoiceOcrAndLabelsView(
              scannedInvoices = scannedInvoices,
              printedLabels = printedLabels,
              onUpdateLinePriceChoice = onUpdateInvoiceLinePriceChoice,
              onUpdateAllLinesPriceStrategy = onUpdateAllInvoiceLinesPriceStrategy,
              onUpdateLinePlacementAndQty = onUpdateInvoiceLinePlacementAndQty,
              onAddLineToInvoice = onAddLineToInvoice,
              onIntegrateInvoiceAndGenerateLabels = onIntegrateInvoiceAndGenerateLabels,
              onPrintSingleLabel = onPrintSingleLabel,
              onPrintAllLabelsForInvoice = onPrintAllLabelsForInvoice
            )
          }

          StockErpTab.CATALOG -> {
            StockErpCatalogView(
              inventory = inventory,
              searchQuery = searchInventoryQuery,
              onSearchQueryChange = { searchInventoryQuery = it },
              selectedCategory = selectedCategoryFilter,
              onSelectCategory = { selectedCategoryFilter = it },
              filterLowStockOnly = filterLowStockOnly,
              onToggleLowStockOnly = { filterLowStockOnly = !filterLowStockOnly },
              editingItemId = editingItemId,
              onSetEditingItemId = { editingItemId = it },
              onUpdateStock = onUpdateStock,
              onUpdateItemPricing = onUpdateItemPricing,
              onAddToQuote = onAddToQuote
            )
          }

          StockErpTab.MOVEMENTS -> {
            StockErpMovementsView(
              inventory = inventory,
              movementLogs = movementLogs,
              onRecordMovement = onRecordMovement
            )
          }

          StockErpTab.SUPPLIER_ORDERS -> {
            StockErpSupplierOrdersView(
              supplierOrders = supplierOrders,
              lowStockItemsCount = lowStockCount,
              onGenerateAutoRestockOrder = onGenerateAutoRestockOrder,
              onReceiveSupplierOrder = onReceiveSupplierOrder
            )
          }

          StockErpTab.SCANNER_NEW -> {
            StockErpScannerAndCreateView(
              inventory = inventory,
              scannedBarcode = scannedBarcode,
              onSelectBarcode = { scannedBarcode = it },
              onUpdateStock = onUpdateStock,
              onAddToQuote = onAddToQuote,
              onCreateNewItem = { partName, oem, bc, brand, cat, initQty, minT, buyCost, sellPrice, shelf, supp, rot ->
                onCreateNewItem(partName, oem, bc, brand, cat, initQty, minT, buyCost, sellPrice, shelf, supp, rot)
                activeTab = StockErpTab.CATALOG
              }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun StockErpDashboardView(
  inventory: List<BarcodeInventoryItem>,
  totalUnits: Int,
  totalPurchaseDzd: Long,
  totalSaleDzd: Long,
  totalMarginDzd: Long,
  lowStockCount: Int,
  outOfStockCount: Int,
  pendingInvoicesCount: Int,
  unprintedLabelsCount: Int,
  onOpenScanPurchaseInvoiceTab: () -> Unit,
  onGenerateAutoRestockOrder: () -> Unit,
  onOpenLowStockCatalog: () -> Unit
) {
  val avgMarginPercent = remember(totalSaleDzd, totalMarginDzd) {
    if (totalSaleDzd > 0) ((totalMarginDzd.toDouble() / totalSaleDzd.toDouble()) * 100).toInt() else 0
  }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // 0. Quick-Action Banner: Scan Bon d'Achat -> Rangement Rayon/Étagère/Bac -> Validation Prix -> Impression Étiquettes
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
        border = BorderStroke(
          1.3.dp,
          Brush.horizontalGradient(listOf(TechCyan, MechanicalOrange, ElectricGreen))
        ),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onOpenScanPurchaseInvoiceTab() }
          .testTag("card_open_scan_purchase_invoice")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(TechCyanDark.copy(alpha = 0.45f))
                .border(1.dp, TechCyan, RoundedCornerShape(10.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.DocumentScanner,
                contentDescription = null,
                tint = TechCyanGlow,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "🧾 SCAN BON D'ACHAT • RANGEMENT AUTO & ÉTIQUETTES",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 11.5.sp
              )
              Text(
                text = "Intègre les pièces dans leur Rayon • Étagère • Bac, demande si vous gardez ou changez le prix de vente et imprime chaque étiquette",
                color = AluminumLight,
                fontSize = 10.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = MechanicalOrangeDark.copy(alpha = 0.45f),
                  border = BorderStroke(0.8.dp, MechanicalOrange)
                ) {
                  Text(
                    text = "$pendingInvoicesCount Bon(s) d'achat prêt(s) à intégrer",
                    color = MechanicalOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                if (unprintedLabelsCount > 0) {
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = TechCyanDark.copy(alpha = 0.45f),
                    border = BorderStroke(0.8.dp, TechCyan)
                  ) {
                    Text(
                      text = "🖨️ $unprintedLabelsCount étiquettes à imprimer",
                      color = TechCyanGlow,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.sp,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }
            }
          }
          Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TechCyanGlow,
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }

    // 1. Financial Valuation Grid (4 Cards)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ErpKpiCard(
            title = "VALEUR D'ACHAT HT (PUMP)",
            value = "$totalPurchaseDzd DA",
            subTitle = "${inventory.size} réf. • $totalUnits pièces en stock",
            accentColor = TechCyan,
            modifier = Modifier.weight(1f)
          )
          ErpKpiCard(
            title = "VALEUR DE VENTE TTC",
            value = "$totalSaleDzd DA",
            subTitle = "Chiffre d'affaires stock potentiel",
            accentColor = MechanicalOrange,
            modifier = Modifier.weight(1f)
          )
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ErpKpiCard(
            title = "MARGE BRUTE LATENTE",
            value = "+$totalMarginDzd DA",
            subTitle = "Taux de marge moyen : $avgMarginPercent%",
            accentColor = ElectricGreen,
            modifier = Modifier.weight(1f)
          )
          ErpKpiCard(
            title = "ALERTES & RUPTURES",
            value = "$lowStockCount réf. ($outOfStockCount à 0)",
            subTitle = "Sous seuil critique de sécurité",
            accentColor = PerformanceRedGlow,
            modifier = Modifier
              .weight(1f)
              .clickable { onOpenLowStockCatalog() }
          )
        }
      }
    }

    // 2. Urgent Auto-Restock Action Banner
    if (lowStockCount > 0) {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF281215)),
          border = BorderStroke(1.2.dp, PerformanceRed),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.WarningAmber, contentDescription = null, tint = PerformanceRedGlow, modifier = Modifier.size(22.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "CENTRALE DE RÉAPPROVISIONNEMENT AUTOMATIQUE",
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp
                )
                Text(
                  text = "$lowStockCount références ont atteint leur seuil critique minimum. Générez un Bon de Commande (BC) calculé sur le stock cible maximal.",
                  color = AluminumLight,
                  fontSize = 10.5.sp
                )
              }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = onGenerateAutoRestockOrder,
                colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .weight(1.3f)
                  .testTag("btn_generate_auto_restock_bc")
              ) {
                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Générer BC Fournisseur Auto", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }

              OutlinedButton(
                onClick = onOpenLowStockCatalog,
                border = BorderStroke(1.dp, MechanicalOrange),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(0.9f)
              ) {
                Text("Voir les $lowStockCount réf.", color = MechanicalOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
            }
          }
        }
      }
    }

    // 3. Rotation ABC & Valorisation par Catégorie
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "ANALYSE DE ROTATION ABC & SANTÉ DU MAGASIN",
            color = PureWhite,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp
          )

          AbcRotationClass.entries.forEach { rotClass ->
            val itemsInClass = inventory.filter { it.rotationClass == rotClass }
            val valueInClass = itemsInClass.sumOf { it.totalSaleValueDzd }
            val rotColor = Color(rotClass.badgeColorHex)
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = GraphiteBlack,
              border = BorderStroke(1.dp, rotColor.copy(alpha = 0.5f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Surface(
                    shape = CircleShape,
                    color = rotColor.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, rotColor),
                    modifier = Modifier.size(28.dp)
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Text(rotClass.code, color = rotColor, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(rotClass.label, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                    Text("${itemsInClass.size} références • ${itemsInClass.sumOf { it.currentStock }} unités", color = AluminumGray, fontSize = 10.sp)
                  }
                }
                Text(
                  text = "$valueInClass DA",
                  color = rotColor,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp
                )
              }
            }
          }
        }
      }
    }

    // 4. Category Breakdown Table
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "VALORISATION PAR FAMILLE DE PIÈCES DE RECHANGE",
            color = TechCyanGlow,
            fontWeight = FontWeight.Black,
            fontSize = 11.5.sp
          )

          val categories = inventory.groupBy { it.category }
          categories.forEach { (cat, items) ->
            val catUnits = items.sumOf { it.currentStock }
            val catSaleVal = items.sumOf { it.totalSaleValueDzd }
            val catAlerts = items.count { it.isLowStock }
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MetalSurface)
                .padding(horizontal = 10.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(cat, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                  if (catAlerts > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(shape = RoundedCornerShape(4.dp), color = PerformanceRedDark) {
                      Text(
                        text = "$catAlerts alerte(s)",
                        color = PureWhite,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                      )
                    }
                  }
                }
                Text("${items.size} réf. • $catUnits u. en rayon", color = AluminumGray, fontSize = 10.sp)
              }
              Text(
                text = "$catSaleVal DA",
                color = MechanicalOrange,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ErpKpiCard(
  title: String,
  value: String,
  subTitle: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.55f))
  ) {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Text(
        text = title,
        color = AluminumMuted,
        fontWeight = FontWeight.Bold,
        fontSize = 9.5.sp
      )
      Text(
        text = value,
        color = accentColor,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Black,
        fontSize = 14.sp
      )
      Text(
        text = subTitle,
        color = AluminumLight,
        fontSize = 9.5.sp
      )
    }
  }
}

@Composable
private fun StockErpCatalogView(
  inventory: List<BarcodeInventoryItem>,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  selectedCategory: String,
  onSelectCategory: (String) -> Unit,
  filterLowStockOnly: Boolean,
  onToggleLowStockOnly: () -> Unit,
  editingItemId: String?,
  onSetEditingItemId: (String?) -> Unit,
  onUpdateStock: (String, Int) -> Unit,
  onUpdateItemPricing: (String, Int, Int, Int, String) -> Unit,
  onAddToQuote: (BarcodeInventoryItem) -> Unit
) {
  val categories = remember(inventory) {
    listOf("TOUTES") + inventory.map { it.category }.distinct().sorted()
  }

  val filteredItems = remember(inventory, searchQuery, selectedCategory, filterLowStockOnly) {
    inventory.filter { item ->
      val q = searchQuery.trim().lowercase()
      val matchesQuery = q.isEmpty() ||
        item.partName.lowercase().contains(q) ||
        item.oemReference.lowercase().contains(q) ||
        item.brand.lowercase().contains(q) ||
        item.barcode.lowercase().contains(q) ||
        item.shelfLocation.lowercase().contains(q) ||
        item.supplierName.lowercase().contains(q)

      val matchesCat = selectedCategory == "TOUTES" || item.category.equals(selectedCategory, ignoreCase = true)
      val matchesLow = !filterLowStockOnly || item.isLowStock
      matchesQuery && matchesCat && matchesLow
    }
  }

  Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
    // Search & Low-Stock Toggle
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = {
          Text("Rechercher Réf OEM, code-barres, rayon, marque, fournisseur...", color = AluminumMuted, fontSize = 11.5.sp)
        },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TechCyan, modifier = Modifier.size(18.dp)) },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchQueryChange("") }) {
              Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = AluminumGray, modifier = Modifier.size(16.dp))
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = GraphiteBlack,
          unfocusedContainerColor = GraphiteBlack,
          focusedBorderColor = TechCyan,
          unfocusedBorderColor = MetalBorder,
          focusedTextColor = PureWhite,
          unfocusedTextColor = OffWhite
        ),
        modifier = Modifier.weight(1f)
      )

      Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (filterLowStockOnly) PerformanceRed else MetalCard,
        border = BorderStroke(1.dp, if (filterLowStockOnly) PerformanceRedGlow else MetalBorder),
        modifier = Modifier.clickable { onToggleLowStockOnly() }
      ) {
        Text(
          text = "⚠️ Alertes",
          color = PureWhite,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 14.dp)
        )
      }
    }

    // Category Chips
    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      items(categories) { cat ->
        val isSel = selectedCategory == cat
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (isSel) TechCyanDark else MetalCard,
          border = BorderStroke(1.dp, if (isSel) TechCyan else MetalBorder),
          modifier = Modifier.clickable { onSelectCategory(cat) }
        ) {
          Text(
            text = if (cat == "TOUTES") "Toutes Familles (${inventory.size})" else cat,
            color = PureWhite,
            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
            fontSize = 10.5.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
          )
        }
      }
    }

    // Filtered Stock Cards
    LazyColumn(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(filteredItems, key = { it.id }) { item ->
        val isEditing = editingItemId == item.id
        var editBuyCost by remember(item.id, item.unitPurchaseCostDzd) { mutableStateOf(item.unitPurchaseCostDzd.toString()) }
        var editSellPrice by remember(item.id, item.unitSalePriceDzd) { mutableStateOf(item.unitSalePriceDzd.toString()) }
        var editMinThreshold by remember(item.id, item.minAlertThreshold) { mutableStateOf(item.minAlertThreshold.toString()) }
        var editShelf by remember(item.id, item.shelfLocation) { mutableStateOf(item.shelfLocation) }

        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(
            1.dp,
            when {
              item.isOutOfStock -> PerformanceRed
              item.isLowStock -> MechanicalOrange
              else -> MetalBorder
            }
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Top Row: Brand + Category + Rotation Class + Stock Badge
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = RoundedCornerShape(5.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(0.8.dp, TechCyan)
                ) {
                  Text(
                    text = "${item.brand} • ${item.category}",
                    color = TechCyanGlow,
                    fontWeight = FontWeight.Black,
                    fontSize = 9.5.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Spacer(modifier = Modifier.width(6.dp))
                val rotColor = Color(item.rotationClass.badgeColorHex)
                Surface(
                  shape = RoundedCornerShape(5.dp),
                  color = rotColor.copy(alpha = 0.18f),
                  border = BorderStroke(0.8.dp, rotColor)
                ) {
                  Text(
                    text = "Classe ${item.rotationClass.code}",
                    color = rotColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = when {
                  item.isOutOfStock -> PerformanceRedDark
                  item.isLowStock -> MechanicalOrangeDark
                  else -> ElectricGreen.copy(alpha = 0.18f)
                },
                border = BorderStroke(
                  1.dp,
                  when {
                    item.isOutOfStock -> PerformanceRed
                    item.isLowStock -> MechanicalOrange
                    else -> ElectricGreen
                  }
                )
              ) {
                Text(
                  text = when {
                    item.isOutOfStock -> "RUPTURE (0 u.)"
                    item.isLowStock -> "SEUIL BAS : ${item.currentStock} / min ${item.minAlertThreshold}"
                    else -> "EN STOCK : ${item.currentStock} u."
                  },
                  color = if (item.isLowStock) PureWhite else ElectricGreen,
                  fontWeight = FontWeight.Black,
                  fontSize = 9.5.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }

            // Part Name & OEM Reference
            Text(
              text = item.partName,
              color = PureWhite,
              fontWeight = FontWeight.Black,
              fontSize = 13.5.sp
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Réf OEM : ${item.oemReference} • EAN : ${item.barcode}",
                color = MechanicalOrange,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp
              )
              Text(
                text = "📍 ${item.shelfLocation}",
                color = TechCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp
              )
            }

            // Financial Row: Purchase HT, Sale Price, Unit Margin, Supplier
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = GraphiteBlack,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text("Achat HT : ${item.unitPurchaseCostDzd} DA", color = AluminumLight, fontSize = 10.sp)
                  Text("Fournisseur : ${item.supplierName}", color = AluminumMuted, fontSize = 9.5.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = "Vente : ${item.unitSalePriceDzd} DA",
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.5.sp
                  )
                  Text(
                    text = "Marge : +${item.unitMarginDzd} DA (${item.marginPercent}%)",
                    color = ElectricGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp
                  )
                }
              }
            }

            // Action Buttons Row: [-1 Sortie] [+1 Entrée] [Paramètres ERP] [+Devis]
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              OutlinedButton(
                onClick = { if (item.currentStock > 0) onUpdateStock(item.id, -1) },
                border = BorderStroke(1.dp, PerformanceRed),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("-1 Sortie", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
              }

              Button(
                onClick = { onUpdateStock(item.id, 1) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricGreenDark),
                border = BorderStroke(1.dp, ElectricGreen),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("+1 Entrée", color = ElectricGreen, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
              }

              OutlinedButton(
                onClick = { onSetEditingItemId(if (isEditing) null else item.id) },
                border = BorderStroke(1.dp, TechCyan),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text(if (isEditing) "Fermer" else "Prix/Seuil", color = TechCyanGlow, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
              }

              IconButton(
                onClick = { onAddToQuote(item) },
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(MechanicalOrangeDark)
              ) {
                Icon(Icons.Default.PlaylistAdd, contentDescription = "Ajouter au devis", tint = PureWhite, modifier = Modifier.size(18.dp))
              }
            }

            // Expandable Inline Editor for Price HT/TTC, Min Threshold & Shelf Location
            AnimatedVisibility(visible = isEditing) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MetalSurface,
                border = BorderStroke(1.dp, TechCyan),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 6.dp)
              ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text(
                    text = "PARAMÉTRAGE ERP DE LA FICHE ARTICLE",
                    color = TechCyanGlow,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.5.sp
                  )
                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                      value = editBuyCost,
                      onValueChange = { editBuyCost = it.filter { c -> c.isDigit() } },
                      label = { Text("Achat HT (DA)", fontSize = 10.sp) },
                      singleLine = true,
                      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                      modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                      value = editSellPrice,
                      onValueChange = { editSellPrice = it.filter { c -> c.isDigit() } },
                      label = { Text("Vente TTC (DA)", fontSize = 10.sp) },
                      singleLine = true,
                      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                      modifier = Modifier.weight(1f)
                    )
                  }
                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                      value = editMinThreshold,
                      onValueChange = { editMinThreshold = it.filter { c -> c.isDigit() } },
                      label = { Text("Seuil Alerte Min", fontSize = 10.sp) },
                      singleLine = true,
                      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                      modifier = Modifier.weight(0.8f)
                    )
                    OutlinedTextField(
                      value = editShelf,
                      onValueChange = { editShelf = it },
                      label = { Text("Emplacement Rayon/Bac", fontSize = 10.sp) },
                      singleLine = true,
                      modifier = Modifier.weight(1.2f)
                    )
                  }
                  Button(
                    onClick = {
                      onUpdateItemPricing(
                        item.id,
                        editBuyCost.toIntOrNull() ?: item.unitPurchaseCostDzd,
                        editSellPrice.toIntOrNull() ?: item.unitSalePriceDzd,
                        editMinThreshold.toIntOrNull() ?: item.minAlertThreshold,
                        editShelf
                      )
                      onSetEditingItemId(null)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Text("Enregistrer les modifications ERP", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
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
private fun StockErpMovementsView(
  inventory: List<BarcodeInventoryItem>,
  movementLogs: List<StockMovementLog>,
  onRecordMovement: (String, StockMovementType, Int, String, String) -> Unit
) {
  var selectedItemId by remember { mutableStateOf(inventory.firstOrNull()?.id ?: "") }
  var selectedMovementType by remember { mutableStateOf(StockMovementType.ENTREE_ACHAT) }
  var quantityText by remember { mutableStateOf("2") }
  var docRefText by remember { mutableStateOf("OR-2026-095 / BL-418") }
  var operatorText by remember { mutableStateOf("Kamel M. (Chef Magasinier)") }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // 1. New Stock Movement Entry Card
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, TechCyan),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "ENREGISTRER UN MOUVEMENT DE STOCK (ENTRÉE / SORTIE / INVENTAIRE)",
            color = PureWhite,
            fontWeight = FontWeight.Black,
            fontSize = 11.5.sp
          )

          // Select Part Chips
          Text("1. Sélectionner la référence :", color = AluminumMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(inventory) { item ->
              val isSel = item.id == selectedItemId
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSel) PerformanceRedDark else GraphiteBlack,
                border = BorderStroke(1.dp, if (isSel) PerformanceRed else MetalBorder),
                modifier = Modifier.clickable { selectedItemId = item.id }
              ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                  Text(item.oemReference, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                  Text("${item.brand} • Stock: ${item.currentStock}", color = TechCyan, fontSize = 9.sp)
                }
              }
            }
          }

          // Select Movement Type Chips
          Text("2. Type d'opération :", color = AluminumMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(StockMovementType.entries) { mType ->
              val isSel = selectedMovementType == mType
              val c = Color(mType.badgeColorHex)
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSel) c.copy(alpha = 0.25f) else GraphiteBlack,
                border = BorderStroke(1.dp, if (isSel) c else MetalBorder),
                modifier = Modifier.clickable { selectedMovementType = mType }
              ) {
                Text(
                  text = mType.label,
                  color = if (isSel) c else AluminumLight,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = quantityText,
              onValueChange = { quantityText = it.filter { ch -> ch.isDigit() } },
              label = { Text("Quantité", fontSize = 10.sp) },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.weight(0.6f)
            )
            OutlinedTextField(
              value = docRefText,
              onValueChange = { docRefText = it },
              label = { Text("N° OR / BL / Facture", fontSize = 10.sp) },
              singleLine = true,
              modifier = Modifier.weight(1.4f)
            )
          }

          Button(
            onClick = {
              val qty = quantityText.toIntOrNull() ?: 1
              if (selectedItemId.isNotEmpty()) {
                onRecordMovement(selectedItemId, selectedMovementType, qty, docRefText, operatorText)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("btn_submit_stock_movement")
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("VALIDER LE MOUVEMENT DANS LE JOURNAL ERP", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
          }
        }
      }
    }

    // 2. Movement History List
    item {
      Text(
        text = "HISTORIQUE DES MOUVEMENTS HORODATÉS (${movementLogs.size})",
        color = AluminumMuted,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
      )
    }

    items(movementLogs, key = { it.id }) { log ->
      val mColor = Color(log.type.badgeColorHex)
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, mColor.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(5.dp),
              color = mColor.copy(alpha = 0.18f),
              border = BorderStroke(0.8.dp, mColor)
            ) {
              Text(
                text = log.type.label,
                color = mColor,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Text(log.dateFormatted, color = AluminumMuted, fontSize = 10.sp)
          }

          Text(
            text = "${log.partName} (${log.oemReference.ifBlank { log.barcode }})",
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 12.5.sp
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Qté mouvement : ${log.quantity} u. (Stock : ${log.previousStock} ➔ ${log.newStock})",
              color = TechCyanGlow,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp
            )
            if (log.unitValueDzd > 0) {
              Text(
                text = "${log.quantity * log.unitValueDzd} DA",
                color = MechanicalOrange,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
              )
            }
          }

          Text(
            text = "Réf Doc : ${log.destinationRef} • Opérateur : ${log.operatorName}",
            color = AluminumGray,
            fontSize = 10.sp
          )
        }
      }
    }
  }
}

@Composable
private fun StockErpSupplierOrdersView(
  supplierOrders: List<SupplierPurchaseOrder>,
  lowStockItemsCount: Int,
  onGenerateAutoRestockOrder: () -> Unit,
  onReceiveSupplierOrder: (String) -> Unit
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
        border = BorderStroke(1.dp, MechanicalOrange),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "BONS DE COMMANDE FOURNISSEURS (BC)",
              color = PureWhite,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp
            )
            Text(
              text = "$lowStockItemsCount référence(s) actuellement sous le seuil d'alerte minimum.",
              color = AluminumLight,
              fontSize = 10.5.sp
            )
          }
          Button(
            onClick = onGenerateAutoRestockOrder,
            colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Créer BC Auto", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }
    }

    items(supplierOrders, key = { it.id }) { order ->
      val statusColor = Color(order.status.colorHex)
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.65f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "${order.orderNumber} • ${order.supplierName}",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
              )
              Text(
                text = "Créé : ${order.createdAt} • Livraison prévue : ${order.expectedDeliveryDate}",
                color = AluminumGray,
                fontSize = 10.sp
              )
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = statusColor.copy(alpha = 0.2f),
              border = BorderStroke(1.dp, statusColor)
            ) {
              Text(
                text = order.status.label,
                color = statusColor,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          // Order Lines
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(GraphiteBlack)
              .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            order.lines.forEach { line ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "• ${line.partName} [${line.oemReference}] (${line.brand})",
                  color = OffWhite,
                  fontSize = 10.5.sp,
                  modifier = Modifier.weight(1f)
                )
                Text(
                  text = "${line.quantityOrdered} x ${line.unitPurchaseCostDzd} = ${line.lineTotalDzd} DA",
                  color = TechCyanGlow,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.5.sp
                )
              }
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "TOTAL HT : ${order.totalOrderCostDzd} DA (${order.totalUnitsCount} pièces)",
              color = MechanicalOrange,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp
            )

            if (order.status != SupplierOrderStatus.RECEPTIONNEE) {
              Button(
                onClick = { onReceiveSupplierOrder(order.id) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricGreenDark),
                border = BorderStroke(1.dp, ElectricGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_receive_po_${order.id}")
              ) {
                Icon(Icons.Default.DownloadDone, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Réceptionner en Stock (+${order.totalUnitsCount})", color = ElectricGreen, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun StockErpScannerAndCreateView(
  inventory: List<BarcodeInventoryItem>,
  scannedBarcode: String,
  onSelectBarcode: (String) -> Unit,
  onUpdateStock: (String, Int) -> Unit,
  onAddToQuote: (BarcodeInventoryItem) -> Unit,
  onCreateNewItem: (
    String, String, String, String, String, Int, Int, Int, Int, String, String, AbcRotationClass
  ) -> Unit
) {
  var newPartName by remember { mutableStateOf("") }
  var newOemRef by remember { mutableStateOf("") }
  var newBarcode by remember { mutableStateOf("6130009876543") }
  var newBrand by remember { mutableStateOf("BOSCH") }
  var newCategory by remember { mutableStateOf("Freinage") }
  var newInitStock by remember { mutableStateOf("10") }
  var newMinThreshold by remember { mutableStateOf("3") }
  var newBuyCost by remember { mutableStateOf("4500") }
  var newSellPrice by remember { mutableStateOf("6800") }
  var newShelf by remember { mutableStateOf("Rayon B1 - Casier 04") }
  var newSupplier by remember { mutableStateOf("Sarl Auto Pièces Alger") }
  var newRotationClass by remember { mutableStateOf(AbcRotationClass.CLASS_A) }

  val currentMatchedItem = inventory.firstOrNull {
    it.barcode == scannedBarcode || it.oemReference.equals(scannedBarcode, ignoreCase = true)
  } ?: inventory.firstOrNull()

  val infiniteTransition = rememberInfiniteTransition(label = "laser_anim")
  val laserPositionRatio by infiniteTransition.animateFloat(
    initialValue = 0.15f,
    targetValue = 0.85f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "laser_y"
  )

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // 1. Laser Barcode Viewfinder
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(150.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(GraphiteBlack)
          .border(1.dp, TechCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height
          val cornerLength = 22.dp.toPx()
          val cornerStroke = 3.dp.toPx()
          drawLine(TechCyan, Offset(w * 0.15f, h * 0.15f), Offset(w * 0.15f + cornerLength, h * 0.15f), strokeWidth = cornerStroke, cap = StrokeCap.Round)
          drawLine(TechCyan, Offset(w * 0.15f, h * 0.15f), Offset(w * 0.15f, h * 0.15f + cornerLength), strokeWidth = cornerStroke, cap = StrokeCap.Round)
          drawLine(TechCyan, Offset(w * 0.85f, h * 0.15f), Offset(w * 0.85f - cornerLength, h * 0.15f), strokeWidth = cornerStroke, cap = StrokeCap.Round)
          drawLine(TechCyan, Offset(w * 0.85f, h * 0.15f), Offset(w * 0.85f, h * 0.15f + cornerLength), strokeWidth = cornerStroke, cap = StrokeCap.Round)
          val laserY = h * laserPositionRatio
          drawLine(PerformanceRedGlow, Offset(w * 0.15f, laserY), Offset(w * 0.85f, laserY), strokeWidth = 2.5.dp.toPx())
        }
        Column(
          modifier = Modifier.align(Alignment.Center),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text("DOUCHETTE LASER EAN-13 / QR CODE MAGASIN", color = TechCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Text("Code-barres actif : $scannedBarcode", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 12.sp)
        }
      }
    }

    // Preset Barcode Selector
    item {
      LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(inventory) { item ->
          val isSel = item.barcode == scannedBarcode
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isSel) PerformanceRedDark else MetalCard,
            border = BorderStroke(1.dp, if (isSel) PerformanceRed else MetalBorder),
            modifier = Modifier.clickable { onSelectBarcode(item.barcode) }
          ) {
            Text(
              text = "${item.brand} • ${item.oemReference}",
              color = PureWhite,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
            )
          }
        }
      }
    }

    // Scanned Item Quick Actions
    if (currentMatchedItem != null) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, TechCyan)
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("${currentMatchedItem.partName} [${currentMatchedItem.oemReference}]", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 13.sp)
            Text("Stock actuel : ${currentMatchedItem.currentStock} u. • Emplacement : ${currentMatchedItem.shelfLocation}", color = TechCyanGlow, fontSize = 11.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = { onUpdateStock(currentMatchedItem.id, 1) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricGreenDark),
                modifier = Modifier.weight(1f)
              ) {
                Text("+1 Entrée", color = ElectricGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
              Button(
                onClick = { if (currentMatchedItem.currentStock > 0) onUpdateStock(currentMatchedItem.id, -1) },
                colors = ButtonDefaults.buttonColors(containerColor = PerformanceRedDark),
                modifier = Modifier.weight(1f)
              ) {
                Text("-1 Sortie", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
              OutlinedButton(
                onClick = { onAddToQuote(currentMatchedItem) },
                border = BorderStroke(1.dp, MechanicalOrange),
                modifier = Modifier.weight(1f)
              ) {
                Text("+ Devis", color = MechanicalOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
            }
          }
        }
      }
    }

    // 2. Complete New Part Creation Form
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.2.dp, MechanicalOrange),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "➕ CRÉER UNE NOUVELLE FICHE PIÈCE DE RECHANGE DANS LE STOCK",
            color = PureWhite,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp
          )

          OutlinedTextField(
            value = newPartName,
            onValueChange = { newPartName = it },
            label = { Text("Désignation complète de la pièce", fontSize = 10.5.sp) },
            placeholder = { Text("Ex: Kit Distribution + Pompe à Eau 2.0 TDI", fontSize = 11.sp) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("erp_new_part_name_input")
          )

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = newOemRef,
              onValueChange = { newOemRef = it },
              label = { Text("Référence OEM", fontSize = 10.sp) },
              singleLine = true,
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = newBrand,
              onValueChange = { newBrand = it },
              label = { Text("Équipementier", fontSize = 10.sp) },
              singleLine = true,
              modifier = Modifier.weight(1f)
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = newCategory,
              onValueChange = { newCategory = it },
              label = { Text("Famille (Freinage, Moteur...)", fontSize = 10.sp) },
              singleLine = true,
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = newBarcode,
              onValueChange = { newBarcode = it },
              label = { Text("Code-barres EAN-13", fontSize = 10.sp) },
              singleLine = true,
              modifier = Modifier.weight(1f)
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = newInitStock,
              onValueChange = { newInitStock = it.filter { c -> c.isDigit() } },
              label = { Text("Stock Initial", fontSize = 10.sp) },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = newMinThreshold,
              onValueChange = { newMinThreshold = it.filter { c -> c.isDigit() } },
              label = { Text("Seuil Alerte Min", fontSize = 10.sp) },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.weight(1f)
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = newBuyCost,
              onValueChange = { newBuyCost = it.filter { c -> c.isDigit() } },
              label = { Text("Prix Achat HT (DA)", fontSize = 10.sp) },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = newSellPrice,
              onValueChange = { newSellPrice = it.filter { c -> c.isDigit() } },
              label = { Text("Prix Vente TTC (DA)", fontSize = 10.sp) },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.weight(1f)
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = newShelf,
              onValueChange = { newShelf = it },
              label = { Text("Rayon / Casier", fontSize = 10.sp) },
              singleLine = true,
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = newSupplier,
              onValueChange = { newSupplier = it },
              label = { Text("Fournisseur", fontSize = 10.sp) },
              singleLine = true,
              modifier = Modifier.weight(1f)
            )
          }

          Button(
            onClick = {
              onCreateNewItem(
                newPartName,
                newOemRef,
                newBarcode,
                newBrand,
                newCategory,
                newInitStock.toIntOrNull() ?: 5,
                newMinThreshold.toIntOrNull() ?: 2,
                newBuyCost.toIntOrNull() ?: 4000,
                newSellPrice.toIntOrNull() ?: 6000,
                newShelf,
                newSupplier,
                newRotationClass
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("btn_create_new_erp_part")
          ) {
            Icon(Icons.Default.AddBox, contentDescription = null, tint = PureWhite, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("AJOUTER LA PIÈCE DE RECHANGE AU STOCK ERP", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 11.5.sp)
          }
        }
      }
    }
  }
}
