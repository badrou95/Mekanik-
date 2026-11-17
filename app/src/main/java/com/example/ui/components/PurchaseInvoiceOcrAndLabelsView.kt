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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun PurchaseInvoiceOcrAndLabelsView(
  scannedInvoices: List<ScannedPurchaseInvoice>,
  printedLabels: List<PrintedThermalLabel>,
  onUpdateLinePriceChoice: (
    invoiceId: String,
    lineId: String,
    choice: SalePriceValidationChoice,
    customSalePriceDzd: Int
  ) -> Unit,
  onUpdateAllLinesPriceStrategy: (invoiceId: String, keepSamePriceForAll: Boolean) -> Unit,
  onUpdateLinePlacementAndQty: (
    invoiceId: String,
    lineId: String,
    rayon: String,
    etagere: String,
    bac: String,
    quantity: Int
  ) -> Unit,
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
  ) -> Unit,
  onIntegrateInvoiceAndGenerateLabels: (invoiceId: String) -> Unit,
  onPrintSingleLabel: (labelId: String) -> Unit,
  onPrintAllLabelsForInvoice: (invoiceNumber: String) -> Unit
) {
  var selectedInvoiceId by remember(scannedInvoices) {
    mutableStateOf(scannedInvoices.firstOrNull()?.id ?: "")
  }
  val activeInvoice = scannedInvoices.find { it.id == selectedInvoiceId } ?: scannedInvoices.firstOrNull()

  var showAddCustomLineForm by remember { mutableStateOf(false) }
  var editingPlacementLineId by remember { mutableStateOf<String?>(null) }

  // Custom line state
  var customPartName by remember { mutableStateOf("") }
  var customOemRef by remember { mutableStateOf("") }
  var customBrand by remember { mutableStateOf("BOSCH") }
  var customCategory by remember { mutableStateOf("Freinage") }
  var customQty by remember { mutableStateOf("6") }
  var customBuyPrice by remember { mutableStateOf("4200") }
  var customSalePrice by remember { mutableStateOf("6400") }
  var customRayon by remember { mutableStateOf("Rayon B2") }
  var customEtagere by remember { mutableStateOf("Étagère E3") }
  var customBac by remember { mutableStateOf("Bac B08") }

  // Laser animation for OCR Purchase Slip Scanner
  val infiniteTransition = rememberInfiniteTransition(label = "ocr_invoice_laser")
  val ocrLaserProgress by infiniteTransition.animateFloat(
    initialValue = 0.12f,
    targetValue = 0.88f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "ocr_laser_y"
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("purchase_invoice_ocr_labels_view"),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // 1. OCR Bon d'Achat Scanner Banner & Selector
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
        border = BorderStroke(
          1.2.dp,
          Brush.horizontalGradient(listOf(TechCyan, MechanicalOrange, ElectricGreen))
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(TechCyanDark.copy(alpha = 0.4f))
                  .border(1.dp, TechCyan, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.DocumentScanner,
                  contentDescription = null,
                  tint = TechCyanGlow,
                  modifier = Modifier.size(22.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "1. SCAN OCR BON D'ACHAT & RANGEMENT AUTO (RAYON • ÉTAGÈRE • BAC)",
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.5.sp
                )
                Text(
                  text = "Détecte les pièces et quantités, les affecte à leur Rayon/Étagère/Bac, valide vos prix de vente et imprime chaque étiquette",
                  color = AluminumLight,
                  fontSize = 10.sp
                )
              }
            }
          }

          // Animated OCR Viewfinder for the active Purchase Slip
          if (activeInvoice != null) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(82.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MetalCard)
                .border(1.dp, TechCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            ) {
              Canvas(modifier = Modifier.fillMaxSize()) {
                val y = size.height * ocrLaserProgress
                drawLine(
                  color = TechCyanGlow,
                  start = Offset(16f, y),
                  end = Offset(size.width - 16f, y),
                  strokeWidth = 2.dp.toPx()
                )
              }
              Row(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "BON D'ACHAT SCANNÉ : ${activeInvoice.invoiceNumber}",
                      color = TechCyanGlow,
                      fontWeight = FontWeight.Black,
                      fontSize = 12.sp,
                      fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = if (activeInvoice.isIntegrated) ElectricGreenDark else MechanicalOrangeDark
                    ) {
                      Text(
                        text = if (activeInvoice.isIntegrated) "✅ INTÉGRÉ AU STOCK" else "⏳ EN ATTENTE DE VALIDATION PRIX",
                        color = if (activeInvoice.isIntegrated) ElectricGreen else MechanicalOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 8.5.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                  Text(
                    text = "Fournisseur : ${activeInvoice.supplierName} • ${activeInvoice.invoiceDate}",
                    color = PureWhite,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = "${activeInvoice.lines.size} références détectées • +${activeInvoice.totalUnitsCount} pièces à ranger • Total achat : ${activeInvoice.totalInvoicePurchaseDzd} DA HT",
                    color = AluminumLight,
                    fontSize = 9.5.sp
                  )
                }
              }
            }
          }

          // Selector for Scanned Purchase Slips
          Text(
            text = "SÉLECTIONNER UN BON D'ACHAT / FACTURE FOURNISSEUR SCANNÉ :",
            color = AluminumMuted,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold
          )
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(scannedInvoices, key = { it.id }) { inv ->
              val isSel = inv.id == activeInvoice?.id
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSel) PerformanceRedDark else MetalCard,
                border = BorderStroke(
                  1.dp,
                  if (isSel) PerformanceRedGlow else if (inv.isIntegrated) ElectricGreen else MetalBorder
                ),
                modifier = Modifier
                  .clickable { selectedInvoiceId = inv.id }
                  .testTag("select_scanned_invoice_${inv.invoiceNumber}")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    if (inv.isIntegrated) Icons.Default.CheckCircle else Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = if (inv.isIntegrated) ElectricGreen else PureWhite,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Text(
                      text = "${inv.invoiceNumber} (+${inv.totalUnitsCount} pcs)",
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 10.5.sp
                    )
                    Text(
                      text = inv.supplierName.take(26),
                      color = AluminumLight,
                      fontSize = 9.sp
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // 2. Global Price Strategy Bar + Add Line Button
    if (activeInvoice != null) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.7f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "2. VALIDATION RAPIDE DES PRIX DE VENTE & AFFECTATION RAYON / ÉTAGÈRE / BAC",
                  color = MechanicalOrange,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp
                )
                Text(
                  text = "Choisissez pour chaque pièce si vous gardez le même prix de vente ou si vous le modifiez avant d'intégrer et d'imprimer les étiquettes :",
                  color = AluminumLight,
                  fontSize = 10.sp
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = { onUpdateAllLinesPriceStrategy(activeInvoice.id, true) },
                border = BorderStroke(1.dp, ElectricGreen),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("btn_keep_all_same_price")
              ) {
                Text(
                  text = "✅ Tout garder au même prix",
                  color = ElectricGreen,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
              }

              OutlinedButton(
                onClick = { onUpdateAllLinesPriceStrategy(activeInvoice.id, false) },
                border = BorderStroke(1.dp, MechanicalOrange),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("btn_adjust_all_prices")
              ) {
                Text(
                  text = "✏️ Ajuster prix (+Marge 38%)",
                  color = MechanicalOrange,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
              }

              OutlinedButton(
                onClick = { showAddCustomLineForm = !showAddCustomLineForm },
                border = BorderStroke(1.dp, TechCyan),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
              ) {
                Text(
                  text = if (showAddCustomLineForm) "Fermer" else "+ Ligne BA",
                  color = TechCyanGlow,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
              }
            }

            // Optional expandable form to add an extra line to the scanned purchase slip
            AnimatedVisibility(visible = showAddCustomLineForm) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = GraphiteBlack,
                border = BorderStroke(1.dp, TechCyan),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 6.dp)
              ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Text(
                    text = "➕ AJOUTER UNE PIÈCE AU BON D'ACHAT (${activeInvoice.invoiceNumber})",
                    color = TechCyanGlow,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.5.sp
                  )
                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                      value = customPartName,
                      onValueChange = { customPartName = it },
                      label = { Text("Désignation pièce", fontSize = 9.5.sp) },
                      singleLine = true,
                      modifier = Modifier.weight(1.4f)
                    )
                    OutlinedTextField(
                      value = customOemRef,
                      onValueChange = { customOemRef = it },
                      label = { Text("Réf OEM", fontSize = 9.5.sp) },
                      singleLine = true,
                      modifier = Modifier.weight(1f)
                    )
                  }
                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                      value = customBrand,
                      onValueChange = { customBrand = it },
                      label = { Text("Marque", fontSize = 9.5.sp) },
                      singleLine = true,
                      modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                      value = customQty,
                      onValueChange = { customQty = it.filter { c -> c.isDigit() } },
                      label = { Text("Qté reçue", fontSize = 9.5.sp) },
                      singleLine = true,
                      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                      modifier = Modifier.weight(0.7f)
                    )
                    OutlinedTextField(
                      value = customBuyPrice,
                      onValueChange = { customBuyPrice = it.filter { c -> c.isDigit() } },
                      label = { Text("Achat HT", fontSize = 9.5.sp) },
                      singleLine = true,
                      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                      modifier = Modifier.weight(0.9f)
                    )
                    OutlinedTextField(
                      value = customSalePrice,
                      onValueChange = { customSalePrice = it.filter { c -> c.isDigit() } },
                      label = { Text("Vente TTC", fontSize = 9.5.sp) },
                      singleLine = true,
                      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                      modifier = Modifier.weight(0.9f)
                    )
                  }
                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                      value = customRayon,
                      onValueChange = { customRayon = it },
                      label = { Text("Rayon", fontSize = 9.5.sp) },
                      singleLine = true,
                      modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                      value = customEtagere,
                      onValueChange = { customEtagere = it },
                      label = { Text("Étagère", fontSize = 9.5.sp) },
                      singleLine = true,
                      modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                      value = customBac,
                      onValueChange = { customBac = it },
                      label = { Text("Bac", fontSize = 9.5.sp) },
                      singleLine = true,
                      modifier = Modifier.weight(1f)
                    )
                  }
                  Button(
                    onClick = {
                      onAddLineToInvoice(
                        activeInvoice.id,
                        customPartName,
                        customOemRef,
                        customBrand,
                        customCategory,
                        customQty.toIntOrNull() ?: 4,
                        customBuyPrice.toIntOrNull() ?: 3500,
                        customSalePrice.toIntOrNull() ?: 5200,
                        customRayon,
                        customEtagere,
                        customBac
                      )
                      customPartName = ""
                      customOemRef = ""
                      showAddCustomLineForm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Text(
                      text = "➕ Ajouter cette pièce au Bon d'Achat avec son Rayon/Étagère/Bac",
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 10.5.sp
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 3. Individual Scanned Lines with Rayon / Étagère / Bac & Price Validation
      items(activeInvoice.lines, key = { it.id }) { line ->
        ScannedInvoiceLineCard(
          line = line,
          isEditingPlacement = editingPlacementLineId == line.id,
          onToggleEditPlacement = {
            editingPlacementLineId = if (editingPlacementLineId == line.id) null else line.id
          },
          onUpdatePriceChoice = { choice, newPrice ->
            onUpdateLinePriceChoice(activeInvoice.id, line.id, choice, newPrice)
          },
          onSavePlacementAndQty = { rayon, etagere, bac, qty ->
            onUpdateLinePlacementAndQty(activeInvoice.id, line.id, rayon, etagere, bac, qty)
            editingPlacementLineId = null
          }
        )
      }

      // 4. Primary Validation & Integration + Automatic Label Generation Button
      item {
        Button(
          onClick = { onIntegrateInvoiceAndGenerateLabels(activeInvoice.id) },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (activeInvoice.isIntegrated) ElectricGreenDark else PerformanceRed
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("btn_integrate_invoice_and_generate_labels")
        ) {
          Icon(
            Icons.Default.Inventory2,
            contentDescription = null,
            tint = PureWhite,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (activeInvoice.isIntegrated) {
              "🔄 RÉ-INTÉGRER LE BON (${activeInvoice.invoiceNumber}) & ACTUALISER LES ÉTIQUETTES"
            } else {
              "✅ VALIDER LES PRIX, RANGER AUX RAYONS/BACS (+${activeInvoice.totalUnitsCount} PCS) & GÉNÉRER LES ÉTIQUETTES"
            },
            color = PureWhite,
            fontWeight = FontWeight.Black,
            fontSize = 11.5.sp
          )
        }
      }
    }

    // 5. Thermal Label Printing Queue (Imprimer chaque étiquette)
    item {
      val totalCopiesToPrint = printedLabels.sumOf { it.copiesToPrint }
      val unprintedCount = printedLabels.count { !it.isPrinted }

      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
        border = BorderStroke(1.2.dp, TechCyan),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.Print,
                  contentDescription = null,
                  tint = TechCyanGlow,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "3. IMPRESSION THERMIQUE DE CHAQUE ÉTIQUETTE (ZEBRA 50x30mm)",
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.5.sp
                )
              }
              Text(
                text = "${printedLabels.size} séries de références • $totalCopiesToPrint étiquettes individuelles avec Code-Barres, Rayon/Étagère/Bac et Prix TTC",
                color = AluminumLight,
                fontSize = 10.sp
              )
            }

            if (printedLabels.isNotEmpty()) {
              Button(
                onClick = { onPrintAllLabelsForInvoice("ALL") },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (unprintedCount > 0) MechanicalOrange else ElectricGreenDark
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("btn_print_all_thermal_labels")
              ) {
                Icon(
                  Icons.Default.Print,
                  contentDescription = null,
                  tint = PureWhite,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                  text = "Tout Imprimer ($totalCopiesToPrint étiq.)",
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 10.5.sp
                )
              }
            }
          }

          if (printedLabels.isEmpty()) {
            Text(
              text = "Validez un Bon d'Achat ci-dessus pour générer automatiquement toutes les étiquettes de rayonnage et prix.",
              color = AluminumMuted,
              fontSize = 11.sp
            )
          }
        }
      }
    }

    // Individual Thermal Label Preview Cards
    items(printedLabels, key = { it.id }) { label ->
      ThermalBarcodeLabelCard(
        label = label,
        onPrintSingleLabel = { onPrintSingleLabel(label.id) }
      )
    }
  }
}

@Composable
private fun ScannedInvoiceLineCard(
  line: ScannedPurchaseInvoiceLine,
  isEditingPlacement: Boolean,
  onToggleEditPlacement: () -> Unit,
  onUpdatePriceChoice: (SalePriceValidationChoice, Int) -> Unit,
  onSavePlacementAndQty: (rayon: String, etagere: String, bac: String, qty: Int) -> Unit
) {
  var customPriceInput by remember(line.id, line.customSalePriceDzd) {
    mutableStateOf(line.customSalePriceDzd.toString())
  }
  var editRayon by remember(line.id, line.assignedRayon) { mutableStateOf(line.assignedRayon) }
  var editEtagere by remember(line.id, line.assignedEtagere) { mutableStateOf(line.assignedEtagere) }
  var editBac by remember(line.id, line.assignedBac) { mutableStateOf(line.assignedBac) }
  var editQty by remember(line.id, line.quantityReceived) { mutableStateOf(line.quantityReceived.toString()) }

  val isPriceChanged = line.priceChoice == SalePriceValidationChoice.CHANGE_SALE_PRICE
  val hasSupplierCostIncrease = line.purchasePriceDeltaDzd > 0

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(
      1.dp,
      if (isPriceChanged) MechanicalOrange else TechCyan.copy(alpha = 0.6f)
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      // Header: Brand, OEM Ref, New/Existing status, Quantity badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = GraphiteBlack,
              border = BorderStroke(0.8.dp, TechCyan)
            ) {
              Text(
                text = line.brand,
                color = TechCyanGlow,
                fontWeight = FontWeight.Black,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Text(
              text = "Réf OEM : ${line.oemReference}",
              color = MechanicalOrange,
              fontWeight = FontWeight.Black,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = if (line.matchedInventoryItemId != null) ElectricGreenDark.copy(alpha = 0.5f) else PerformanceRedDark.copy(alpha = 0.6f)
            ) {
              Text(
                text = if (line.matchedInventoryItemId != null) "EN STOCK (${line.previousStock} u.)" else "NOUVELLE RÉFÉRENCE",
                color = if (line.matchedInventoryItemId != null) ElectricGreen else PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 8.5.sp,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = line.partName,
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 12.5.sp
          )
        }

        // Received Quantity Pill
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = ElectricGreenDark.copy(alpha = 0.45f),
          border = BorderStroke(1.dp, ElectricGreen)
        ) {
          Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "+${line.quantityReceived} PCS",
              color = ElectricGreen,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp
            )
            Text(
              text = "Stock: ${line.previousStock} ➔ ${line.previousStock + line.quantityReceived}",
              color = PureWhite,
              fontSize = 8.5.sp
            )
          }
        }
      }

      // Automatic Assignment Banner: RAYON • ÉTAGÈRE • BAC
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = GraphiteBlack,
        border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 7.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(
              Icons.Default.Place,
              contentDescription = null,
              tint = TechCyanGlow,
              modifier = Modifier.size(16.dp)
            )
            Surface(
              shape = RoundedCornerShape(5.dp),
              color = TechCyanDark.copy(alpha = 0.4f),
              border = BorderStroke(0.8.dp, TechCyan)
            ) {
              Text(
                text = line.assignedRayon,
                color = TechCyanGlow,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Text("•", color = AluminumMuted, fontWeight = FontWeight.Black)
            Surface(
              shape = RoundedCornerShape(5.dp),
              color = MechanicalOrangeDark.copy(alpha = 0.4f),
              border = BorderStroke(0.8.dp, MechanicalOrange)
            ) {
              Text(
                text = line.assignedEtagere,
                color = MechanicalOrange,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Text("•", color = AluminumMuted, fontWeight = FontWeight.Black)
            Surface(
              shape = RoundedCornerShape(5.dp),
              color = ElectricGreenDark.copy(alpha = 0.4f),
              border = BorderStroke(0.8.dp, ElectricGreen)
            ) {
              Text(
                text = line.assignedBac,
                color = ElectricGreen,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          TextButton(
            onClick = onToggleEditPlacement,
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = if (isEditingPlacement) "Fermer" else "📍 Modifier Bac",
              color = TechCyanGlow,
              fontWeight = FontWeight.Bold,
              fontSize = 9.5.sp
            )
          }
        }
      }

      // Optional Inline Editor for Rayon / Étagère / Bac & Quantity
      AnimatedVisibility(visible = isEditingPlacement) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = GraphiteBlack,
          border = BorderStroke(1.dp, TechCyan),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              OutlinedTextField(
                value = editRayon,
                onValueChange = { editRayon = it },
                label = { Text("Rayon", fontSize = 9.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f)
              )
              OutlinedTextField(
                value = editEtagere,
                onValueChange = { editEtagere = it },
                label = { Text("Étagère", fontSize = 9.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f)
              )
              OutlinedTextField(
                value = editBac,
                onValueChange = { editBac = it },
                label = { Text("Bac", fontSize = 9.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f)
              )
              OutlinedTextField(
                value = editQty,
                onValueChange = { editQty = it.filter { c -> c.isDigit() } },
                label = { Text("Qté", fontSize = 9.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.7f)
              )
            }
            Button(
              onClick = {
                onSavePlacementAndQty(
                  editRayon,
                  editEtagere,
                  editBac,
                  editQty.toIntOrNull() ?: line.quantityReceived
                )
              },
              colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "Enregistrer l'emplacement Rayon / Étagère / Bac",
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              )
            }
          }
        }
      }

      // Purchase Price vs Sale Price Validation Prompt
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = GraphiteBlack,
        border = BorderStroke(
          1.dp,
          if (isPriceChanged) MechanicalOrange else ElectricGreen.copy(alpha = 0.6f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Achat HT Bon : ${line.scannedPurchaseCostDzd} DA" +
                if (hasSupplierCostIncrease) " (⚠️ +${line.purchasePriceDeltaDzd} DA vs ancien achat)" else " (Stable)",
              color = if (hasSupplierCostIncrease) MechanicalOrange else AluminumLight,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
            Text(
              text = "Marge validée : +${line.newMarginDzd} DA (${line.newMarginPercent}%)",
              color = ElectricGreen,
              fontWeight = FontWeight.Black,
              fontSize = 10.sp
            )
          }

          Text(
            text = "💬 Voulez-vous garder le même prix de vente (${line.previousSalePriceDzd} DA) ou le changer pour valider ?",
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 10.5.sp
          )

          // Two Validation Choice Buttons: Garder le même prix vs Changer le prix
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val isKeepSelected = line.priceChoice == SalePriceValidationChoice.KEEP_SAME_PRICE
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isKeepSelected) ElectricGreenDark else MetalCard,
              border = BorderStroke(
                1.2.dp,
                if (isKeepSelected) ElectricGreen else MetalBorder
              ),
              modifier = Modifier
                .weight(1f)
                .clickable {
                  onUpdatePriceChoice(
                    SalePriceValidationChoice.KEEP_SAME_PRICE,
                    line.previousSalePriceDzd
                  )
                }
                .testTag("choice_keep_price_${line.oemReference}")
            ) {
              Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "✅ Garder même prix",
                  color = if (isKeepSelected) ElectricGreen else AluminumLight,
                  fontWeight = FontWeight.Black,
                  fontSize = 10.5.sp
                )
                Text(
                  text = "${line.previousSalePriceDzd} DA TTC",
                  color = PureWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
              }
            }

            val isChangeSelected = line.priceChoice == SalePriceValidationChoice.CHANGE_SALE_PRICE
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isChangeSelected) MechanicalOrangeDark else MetalCard,
              border = BorderStroke(
                1.2.dp,
                if (isChangeSelected) MechanicalOrange else MetalBorder
              ),
              modifier = Modifier
                .weight(1f)
                .clickable {
                  val suggested = if (line.customSalePriceDzd == line.previousSalePriceDzd) {
                    ((line.scannedPurchaseCostDzd * 1.38) / 50.0).toInt() * 50
                  } else line.customSalePriceDzd
                  customPriceInput = suggested.toString()
                  onUpdatePriceChoice(
                    SalePriceValidationChoice.CHANGE_SALE_PRICE,
                    suggested
                  )
                }
                .testTag("choice_change_price_${line.oemReference}")
            ) {
              Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "✏️ Changer le prix",
                  color = if (isChangeSelected) MechanicalOrange else AluminumLight,
                  fontWeight = FontWeight.Black,
                  fontSize = 10.5.sp
                )
                Text(
                  text = "${line.customSalePriceDzd} DA TTC",
                  color = PureWhite,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
              }
            }
          }

          // Custom Sale Price Input when CHANGE_SALE_PRICE is active
          AnimatedVisibility(visible = isPriceChanged) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                OutlinedTextField(
                  value = customPriceInput,
                  onValueChange = { raw ->
                    val clean = raw.filter { it.isDigit() }
                    customPriceInput = clean
                    val parsed = clean.toIntOrNull()
                    if (parsed != null && parsed >= 100) {
                      onUpdatePriceChoice(SalePriceValidationChoice.CHANGE_SALE_PRICE, parsed)
                    }
                  },
                  label = { Text("Nouveau Prix de Vente TTC (DA)", fontSize = 9.5.sp) },
                  singleLine = true,
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  modifier = Modifier
                    .weight(1f)
                    .testTag("input_new_sale_price_${line.oemReference}")
                )

                // Quick Margin Preset Chips
                listOf("+5%" to 1.05, "+10%" to 1.10, "Marge 40%" to 1.40).forEach { (label, mult) ->
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MetalCard,
                    border = BorderStroke(1.dp, MechanicalOrange),
                    modifier = Modifier.clickable {
                      val base = if (label.startsWith("Marge")) line.scannedPurchaseCostDzd else line.previousSalePriceDzd
                      val computed = ((base * mult) / 50.0).toInt() * 50
                      customPriceInput = computed.toString()
                      onUpdatePriceChoice(SalePriceValidationChoice.CHANGE_SALE_PRICE, computed)
                    }
                  ) {
                    Text(
                      text = label,
                      color = MechanicalOrange,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.5.sp,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
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
private fun ThermalBarcodeLabelCard(
  label: PrintedThermalLabel,
  onPrintSingleLabel: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(
      1.dp,
      if (label.isPrinted) ElectricGreen else MechanicalOrange
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      // Realistic White Thermal Sticker Preview (50x30mm Zebra style)
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFFDFDFD),
        border = BorderStroke(1.5.dp, Color(0xFF1A1E24)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // Sticker Top Header: MEKANIK+ STOCKPRO + Brand + Invoice
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "MEKANIK+ OEM • ${label.brand}",
              color = Color(0xFF101418),
              fontWeight = FontWeight.Black,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = "Bon: ${label.invoiceNumber}",
              color = Color(0xFF37474F),
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace
            )
          }

          // Part Name & OEM Reference
          Text(
            text = label.partName,
            color = Color(0xFF101418),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "RÉF : ${label.oemReference}",
              color = Color(0xFFB71C1C),
              fontWeight = FontWeight.Black,
              fontSize = 12.sp,
              fontFamily = FontFamily.Monospace
            )

            // Validated Sale Price Box on Sticker
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = Color(0xFF101418)
            ) {
              Text(
                text = "PRIX TTC : ${label.validatedSalePriceDzd} DA",
                color = Color(0xFFFFFFFF),
                fontWeight = FontWeight.Black,
                fontSize = 11.5.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          // High-Contrast Location Block on Sticker: RAYON • ÉTAGÈRE • BAC
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = Color(0xFFECEFF1),
            border = BorderStroke(1.dp, Color(0xFF263238)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "📍 ${label.rayon.uppercase()}  |  ${label.etagere.uppercase()}  |  ${label.bac.uppercase()}",
                color = Color(0xFF101418),
                fontWeight = FontWeight.Black,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = "x${label.copiesToPrint} ÉTIQ.",
                color = Color(0xFFD84315),
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp
              )
            }
          }

          // Realistic EAN-13 Vector Barcode Bars drawn via Canvas
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Canvas(
              modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(28.dp)
            ) {
              val barCount = 46
              val step = size.width / barCount
              for (i in 0 until barCount) {
                val seed = (label.barcode.hashCode() + i * 31)
                val isThick = (seed % 3 == 0)
                val isDrawn = (seed % 5 != 0) || i < 3 || i > barCount - 4
                if (isDrawn) {
                  drawRect(
                    color = Color(0xFF101418),
                    topLeft = Offset(i * step, 0f),
                    size = androidx.compose.ui.geometry.Size(
                      width = if (isThick) step * 0.78f else step * 0.42f,
                      height = size.height
                    )
                  )
                }
              }
            }
            Text(
              text = "* ${label.barcode} *",
              color = Color(0xFF101418),
              fontWeight = FontWeight.Bold,
              fontSize = 9.5.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      // Print Status & Individual Print Trigger
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = if (label.isPrinted) "✅ ${label.printedTimestamp}" else "⏳ Prêt à imprimer (${label.copiesToPrint} étiquettes pour ${label.fullShelfCode})",
            color = if (label.isPrinted) ElectricGreen else MechanicalOrange,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        }

        Button(
          onClick = onPrintSingleLabel,
          colors = ButtonDefaults.buttonColors(
            containerColor = if (label.isPrinted) MetalSurface else TechCyanDark
          ),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
          modifier = Modifier.testTag("btn_print_label_${label.oemReference}")
        ) {
          Icon(
            Icons.Default.Print,
            contentDescription = null,
            tint = PureWhite,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = if (label.isPrinted) {
              "Réimprimer (${label.copiesToPrint})"
            } else {
              "🖨️ Imprimer (${label.copiesToPrint} étiq.)"
            },
            color = PureWhite,
            fontWeight = FontWeight.Black,
            fontSize = 10.sp
          )
        }
      }
    }
  }
}
