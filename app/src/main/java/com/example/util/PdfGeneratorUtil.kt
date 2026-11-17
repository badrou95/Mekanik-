package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.FiscalInvoice
import com.example.data.model.RepairItem
import com.example.data.model.RepairOrder
import com.example.data.model.DriverInvoiceItem
import com.example.data.model.RentalContract
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GeneratedPdfResult(
  val file: File,
  val fileName: String,
  val filePath: String,
  val fileSizeBytes: Long,
  val documentType: String,
  val documentNumber: String,
  val clientName: String,
  val totalAmountDzd: Double,
  val uri: Uri?,
  val timestamp: Long = System.currentTimeMillis()
) {
  val formattedSize: String get() {
    return if (fileSizeBytes < 1024) "$fileSizeBytes B"
    else "${fileSizeBytes / 1024} Ko"
  }
}

object PdfGeneratorUtil {

  private val df = DecimalFormat("#,###", DecimalFormatSymbols(Locale.FRENCH))

  private fun getOutputDir(context: Context): File {
    // Try external public downloads or standard documents directory first, then fallback to externalFilesDir
    val docsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
      ?: File(context.filesDir, "documents")
    if (!docsDir.exists()) {
      docsDir.mkdirs()
    }
    return docsDir
  }

  fun getFileUri(context: Context, file: File): Uri? {
    return try {
      FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )
    } catch (e: Exception) {
      Uri.fromFile(file)
    }
  }

  fun openPdfFile(context: Context, file: File) {
    try {
      val uri = getFileUri(context, file) ?: return
      val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      val chooser = Intent.createChooser(intent, "Ouvrir le document PDF").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(chooser)
    } catch (e: Exception) {
      Toast.makeText(context, "Document PDF sauvegardé : ${file.name}", Toast.LENGTH_LONG).show()
    }
  }

  fun sharePdfFile(context: Context, file: File, title: String = "Partager le document PDF") {
    try {
      val uri = getFileUri(context, file) ?: return
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, "Veuillez trouver ci-joint votre document automobile certifié Mekanik+ ($title).")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      val chooser = Intent.createChooser(intent, title).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(chooser)
    } catch (e: Exception) {
      Toast.makeText(context, "Impossible d'ouvrir le menu de partage : ${e.message}", Toast.LENGTH_SHORT).show()
    }
  }

  // --- 1. GÉNÉRATION DEVIS ATELIER & PIÈCES ---
  fun generateWorkshopQuotePdf(
    context: Context,
    quoteNumber: String,
    clientName: String,
    clientPhone: String,
    clientAddress: String,
    plateNumber: String,
    vehicleModel: String,
    vehicleMileage: String,
    quoteNotes: String,
    items: List<RepairItem>,
    laborHours: Float,
    laborRatePerHour: Int,
    diagnosticCostDzd: Int,
    suppliesCostDzd: Int,
    applyTva19: Boolean,
    fiscalStampDzd: Int
  ): GeneratedPdfResult {
    val document = PdfDocument()
    val pageWidth = 595
    val pageHeight = 842
    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
    val page = document.startPage(pageInfo)
    val canvas = page.canvas

    val p = Paint().apply { isAntiAlias = true }

    // Background
    p.color = Color.rgb(250, 252, 255)
    canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), p)

    // Top Brand Accent Bar (Red & Graphite)
    p.color = Color.rgb(229, 57, 53) // Performance Red
    canvas.drawRect(0f, 0f, pageWidth.toFloat(), 10f, p)
    p.color = Color.rgb(16, 20, 24) // Graphite
    canvas.drawRect(0f, 10f, pageWidth.toFloat(), 75f, p)

    // Header Title & Logo
    p.color = Color.WHITE
    p.textSize = 20f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("MEKANIK+ ATELIER PRO", 24f, 44f, p)

    p.color = Color.rgb(255, 122, 0) // Mechanical Orange
    p.textSize = 10f
    p.typeface = Typeface.DEFAULT
    canvas.drawText("PLATEFORME AUTOMOBILE NOUVELLE GÉNÉRATION • ALGER (DZ)", 24f, 60f, p)

    // Quote Badge Top Right
    p.color = Color.rgb(229, 57, 53)
    val badgeRect = RectF(pageWidth - 190f, 22f, pageWidth - 24f, 62f)
    canvas.drawRoundRect(badgeRect, 6f, 6f, p)
    p.color = Color.WHITE
    p.textSize = 12f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("DEVIS PROFORMA", pageWidth - 176f, 40f, p)
    p.textSize = 9.5f
    canvas.drawText("N° $quoteNumber", pageWidth - 176f, 54f, p)

    val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(Date())

    // Sub-header Legal Info
    var y = 96f
    p.color = Color.rgb(100, 110, 120)
    p.textSize = 8.5f
    p.typeface = Typeface.DEFAULT
    canvas.drawText("Garage Auto Performance Alger • NIF: 002016098745632 • RC: 16/00-0987654B20 • Tél: 0550 42 19 88", 24f, y, p)
    canvas.drawText("Date d'émission: $dateStr • Validité: 30 jours", pageWidth - 230f, y, p)

    // Two Info Cards: Workshop/Garage (Left) & Client/Vehicle (Right)
    y += 18f
    val cardHeight = 94f

    // Card Left: Garage Info
    p.color = Color.rgb(240, 243, 246)
    canvas.drawRoundRect(RectF(24f, y, 285f, y + cardHeight), 8f, 8f, p)
    p.color = Color.rgb(215, 222, 230)
    p.style = Paint.Style.STROKE
    p.strokeWidth = 1f
    canvas.drawRoundRect(RectF(24f, y, 285f, y + cardHeight), 8f, 8f, p)
    p.style = Paint.Style.FILL

    p.color = Color.rgb(16, 20, 24)
    p.textSize = 9.5f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("ÉMETTEUR / ATELIER :", 34f, y + 18f, p)
    p.typeface = Typeface.DEFAULT
    p.textSize = 8.5f
    p.color = Color.rgb(40, 48, 56)
    canvas.drawText("Garage : Mekanik+ Services Pro Alger Centre", 34f, y + 33f, p)
    canvas.drawText("Adresse : Zone Industrielle Oued Smar, Alger (16)", 34f, y + 47f, p)
    canvas.drawText("Chef d'atelier : Mohamed Amine / Tél: 0550 42 19 88", 34f, y + 61f, p)
    canvas.drawText("Agrément Ministère des Transports DZ-2026-A12", 34f, y + 75f, p)

    // Card Right: Client & Vehicle Info
    p.color = Color.rgb(240, 243, 246)
    canvas.drawRoundRect(RectF(305f, y, pageWidth - 24f, y + cardHeight), 8f, 8f, p)
    p.color = Color.rgb(215, 222, 230)
    p.style = Paint.Style.STROKE
    canvas.drawRoundRect(RectF(305f, y, pageWidth - 24f, y + cardHeight), 8f, 8f, p)
    p.style = Paint.Style.FILL

    p.color = Color.rgb(229, 57, 53)
    p.textSize = 9.5f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("DESTINATAIRE / VÉHICULE :", 315f, y + 18f, p)
    p.typeface = Typeface.DEFAULT
    p.textSize = 8.5f
    p.color = Color.rgb(40, 48, 56)
    canvas.drawText("Client : $clientName • Tél : $clientPhone", 315f, y + 33f, p)
    canvas.drawText("Adresse : ${clientAddress.ifBlank { "Alger (16)" }}", 315f, y + 47f, p)
    canvas.drawText("Véhicule : $vehicleModel", 315f, y + 61f, p)
    canvas.drawText("Matricule : $plateNumber • Km : $vehicleMileage km", 315f, y + 75f, p)

    // Table Header
    y += cardHeight + 22f
    p.color = Color.rgb(37, 43, 49) // Metal Card Dark
    canvas.drawRoundRect(RectF(24f, y, pageWidth - 24f, y + 24f), 4f, 4f, p)

    p.color = Color.WHITE
    p.textSize = 8.5f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("RÉFÉRENCE OEM", 32f, y + 16f, p)
    canvas.drawText("DÉSIGNATION DE LA PIÈCE / PRESTATION", 140f, y + 16f, p)
    canvas.drawText("QTÉ", 360f, y + 16f, p)
    canvas.drawText("P.U HT (DZD)", 410f, y + 16f, p)
    canvas.drawText("TOTAL HT (DZD)", 495f, y + 16f, p)

    y += 26f
    p.typeface = Typeface.DEFAULT
    var rowIdx = 0

    // Parts Rows
    var partsSubtotal = 0
    items.forEach { item ->
      val rowTotal = item.priceDzd * item.quantity
      partsSubtotal += rowTotal

      if (rowIdx % 2 == 1) {
        p.color = Color.rgb(246, 248, 251)
        canvas.drawRect(24f, y - 2f, pageWidth - 24f, y + 18f, p)
      }

      p.color = Color.rgb(229, 57, 53)
      p.textSize = 8f
      p.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
      canvas.drawText(item.reference.take(16), 32f, y + 11f, p)

      p.color = Color.rgb(30, 36, 42)
      p.textSize = 8.5f
      p.typeface = Typeface.DEFAULT
      canvas.drawText(item.name.take(38), 140f, y + 11f, p)
      canvas.drawText("${item.quantity}", 368f, y + 11f, p)
      canvas.drawText(df.format(item.priceDzd), 415f, y + 11f, p)

      p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText(df.format(rowTotal), 500f, y + 11f, p)

      y += 20f
      rowIdx++
    }

    // Labor Row
    val laborTotal = (laborHours * laborRatePerHour).toInt()
    if (rowIdx % 2 == 1) {
      p.color = Color.rgb(246, 248, 251)
      canvas.drawRect(24f, y - 2f, pageWidth - 24f, y + 18f, p)
    }
    p.color = Color.rgb(0, 168, 204)
    p.textSize = 8f
    p.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    canvas.drawText("MO-ATELIER", 32f, y + 11f, p)
    p.color = Color.rgb(30, 36, 42)
    p.textSize = 8.5f
    p.typeface = Typeface.DEFAULT
    canvas.drawText("Main d'œuvre mécanique (${laborHours}h à ${df.format(laborRatePerHour)} DA/h)", 140f, y + 11f, p)
    canvas.drawText("${laborHours}h", 365f, y + 11f, p)
    canvas.drawText(df.format(laborRatePerHour), 415f, y + 11f, p)
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText(df.format(laborTotal), 500f, y + 11f, p)
    y += 20f
    rowIdx++

    // Diagnostics if included
    if (diagnosticCostDzd > 0) {
      if (rowIdx % 2 == 1) {
        p.color = Color.rgb(246, 248, 251)
        canvas.drawRect(24f, y - 2f, pageWidth - 24f, y + 18f, p)
      }
      p.color = Color.rgb(0, 168, 204)
      p.textSize = 8f
      p.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
      canvas.drawText("DIAG-OBD", 32f, y + 11f, p)
      p.color = Color.rgb(30, 36, 42)
      p.textSize = 8.5f
      p.typeface = Typeface.DEFAULT
      canvas.drawText("Passage valise diagnostic ECU & effacement DTC", 140f, y + 11f, p)
      canvas.drawText("1", 368f, y + 11f, p)
      canvas.drawText(df.format(diagnosticCostDzd), 415f, y + 11f, p)
      p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText(df.format(diagnosticCostDzd), 500f, y + 11f, p)
      y += 20f
      rowIdx++
    }

    // Supplies if included
    if (suppliesCostDzd > 0) {
      if (rowIdx % 2 == 1) {
        p.color = Color.rgb(246, 248, 251)
        canvas.drawRect(24f, y - 2f, pageWidth - 24f, y + 18f, p)
      }
      p.color = Color.rgb(100, 110, 120)
      p.textSize = 8f
      p.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
      canvas.drawText("FOURN-ATEL", 32f, y + 11f, p)
      p.color = Color.rgb(30, 36, 42)
      p.textSize = 8.5f
      p.typeface = Typeface.DEFAULT
      canvas.drawText("Fournitures atelier & traitement des déchets", 140f, y + 11f, p)
      canvas.drawText("1", 368f, y + 11f, p)
      canvas.drawText(df.format(suppliesCostDzd), 415f, y + 11f, p)
      p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText(df.format(suppliesCostDzd), 500f, y + 11f, p)
      y += 20f
    }

    // Divider Line
    p.color = Color.rgb(200, 210, 220)
    canvas.drawLine(24f, y + 4f, pageWidth - 24f, y + 4f, p)
    y += 14f

    // Total Calculations Summary Table (Right Side)
    val totalHt = partsSubtotal + laborTotal + diagnosticCostDzd + suppliesCostDzd
    val tva = if (applyTva19) (totalHt * 0.19).toInt() else 0
    val totalTtc = totalHt + tva + fiscalStampDzd

    // Notes Box (Left Side)
    p.color = Color.rgb(245, 247, 250)
    canvas.drawRoundRect(RectF(24f, y, 320f, y + 90f), 6f, 6f, p)
    p.color = Color.rgb(215, 222, 230)
    p.style = Paint.Style.STROKE
    canvas.drawRoundRect(RectF(24f, y, 320f, y + 90f), 6f, 6f, p)
    p.style = Paint.Style.FILL

    p.color = Color.rgb(16, 20, 24)
    p.textSize = 8.5f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("CONDITIONS & GARANTIES MÉCANIQUES :", 32f, y + 16f, p)
    p.typeface = Typeface.DEFAULT
    p.textSize = 8f
    p.color = Color.rgb(60, 70, 80)
    canvas.drawText("• $quoteNotes", 32f, y + 30f, p)
    canvas.drawText("• Pièces certifiées neuves avec facture et garantie constructeur.", 32f, y + 44f, p)
    canvas.drawText("• Validité de l'offre : 30 jours à compter de la date d'émission.", 32f, y + 58f, p)
    canvas.drawText("• Modes de règlement acceptés : CIB / Edahabia / Virement / Espèces.", 32f, y + 72f, p)

    // Financial Calculation Card (Right Side)
    val calcLeft = 336f
    p.color = Color.rgb(245, 247, 250)
    canvas.drawRoundRect(RectF(calcLeft, y, pageWidth - 24f, y + 90f), 6f, 6f, p)
    p.color = Color.rgb(215, 222, 230)
    p.style = Paint.Style.STROKE
    canvas.drawRoundRect(RectF(calcLeft, y, pageWidth - 24f, y + 90f), 6f, 6f, p)
    p.style = Paint.Style.FILL

    p.color = Color.rgb(50, 60, 70)
    p.textSize = 8.5f
    p.typeface = Typeface.DEFAULT
    canvas.drawText("Total Pièces HT :", calcLeft + 10f, y + 18f, p)
    canvas.drawText("${df.format(partsSubtotal)} DZD", pageWidth - 34f - p.measureText("${df.format(partsSubtotal)} DZD"), y + 18f, p)

    canvas.drawText("Total Main d'œuvre & Serv. :", calcLeft + 10f, y + 34f, p)
    canvas.drawText("${df.format(laborTotal + diagnosticCostDzd + suppliesCostDzd)} DZD", pageWidth - 34f - p.measureText("${df.format(laborTotal + diagnosticCostDzd + suppliesCostDzd)} DZD"), y + 34f, p)

    canvas.drawText("TVA Légale (19%) :", calcLeft + 10f, y + 50f, p)
    canvas.drawText("${df.format(tva)} DZD", pageWidth - 34f - p.measureText("${df.format(tva)} DZD"), y + 50f, p)

    canvas.drawText("Droit de Timbre :", calcLeft + 10f, y + 64f, p)
    canvas.drawText("${df.format(fiscalStampDzd)} DZD", pageWidth - 34f - p.measureText("${df.format(fiscalStampDzd)} DZD"), y + 64f, p)

    // Total TTC Highlight Box
    p.color = Color.rgb(229, 57, 53)
    canvas.drawRect(calcLeft, y + 70f, pageWidth - 24f, y + 90f, p)
    p.color = Color.WHITE
    p.textSize = 10f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("TOTAL TTC (DA) :", calcLeft + 10f, y + 84f, p)
    val totalStr = "${df.format(totalTtc)} DZD"
    canvas.drawText(totalStr, pageWidth - 34f - p.measureText(totalStr), y + 84f, p)

    // Signatures and Verification Footer
    y += 105f
    p.color = Color.rgb(240, 243, 246)
    canvas.drawRoundRect(RectF(24f, y, 280f, y + 68f), 6f, 6f, p)
    canvas.drawRoundRect(RectF(300f, y, pageWidth - 24f, y + 68f), 6f, 6f, p)

    p.color = Color.rgb(100, 110, 120)
    p.textSize = 8f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("CACHE ET SIGNATURE DU GARAGE", 34f, y + 16f, p)
    canvas.drawText("SIGNATURE DU CLIENT (BON POUR ACCORD)", 310f, y + 16f, p)

    p.color = Color.rgb(160, 170, 180)
    p.typeface = Typeface.DEFAULT
    canvas.drawText("Mekanik+ Services Pro DZ • Validé", 34f, y + 54f, p)
    canvas.drawText("« Lu et approuvé, bon pour exécution »", 310f, y + 54f, p)

    // Bottom QR Code & Security Hash bar
    y += 82f
    p.color = Color.rgb(16, 20, 24)
    canvas.drawRect(0f, pageHeight - 34f, pageWidth.toFloat(), pageHeight.toFloat(), p)

    p.color = Color.rgb(255, 122, 0)
    p.textSize = 8.5f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("MEKANIK+ DZ-VERIFIED", 24f, pageHeight - 16f, p)

    p.color = Color.rgb(180, 190, 200)
    p.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
    p.textSize = 7.5f
    canvas.drawText("EMPREINTE NUMÉRIQUE SHA-256 : DZ-DEV-${quoteNumber}-MKP2026", 160f, pageHeight - 16f, p)

    document.finishPage(page)

    // Save PDF to file
    val fileName = "Devis_${quoteNumber.replace("-", "_")}.pdf"
    val outputDir = getOutputDir(context)
    val file = File(outputDir, fileName)
    val fos = FileOutputStream(file)
    document.writeTo(fos)
    fos.flush()
    fos.close()
    document.close()

    val fileUri = getFileUri(context, file)

    return GeneratedPdfResult(
      file = file,
      fileName = fileName,
      filePath = file.absolutePath,
      fileSizeBytes = file.length(),
      documentType = "DEVIS AUTOMOBILE",
      documentNumber = quoteNumber,
      clientName = clientName,
      totalAmountDzd = totalTtc.toDouble(),
      uri = fileUri
    )
  }

  // --- 2. GÉNÉRATION FACTURE FISCALE OFFICIELLE ---
  fun generateFiscalInvoicePdf(
    context: Context,
    invoice: FiscalInvoice
  ): GeneratedPdfResult {
    val document = PdfDocument()
    val pageWidth = 595
    val pageHeight = 842
    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
    val page = document.startPage(pageInfo)
    val canvas = page.canvas

    val p = Paint().apply { isAntiAlias = true }

    // Background
    p.color = Color.rgb(250, 252, 255)
    canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), p)

    // Top Brand Accent Bar (Orange & Dark Graphite)
    p.color = Color.rgb(255, 122, 0) // Mechanical Orange
    canvas.drawRect(0f, 0f, pageWidth.toFloat(), 10f, p)
    p.color = Color.rgb(16, 20, 24)
    canvas.drawRect(0f, 10f, pageWidth.toFloat(), 75f, p)

    // Header Title
    p.color = Color.WHITE
    p.textSize = 20f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("MEKANIK+ FACTURATION FISCALE", 24f, 44f, p)

    p.color = Color.rgb(255, 122, 0)
    p.textSize = 10f
    p.typeface = Typeface.DEFAULT
    canvas.drawText("RÉPUBLIQUE ALGÉRIENNE DÉMOCRATIQUE ET POPULAIRE", 24f, 60f, p)

    // Invoice Status Badge
    p.color = if (invoice.isPaid) Color.rgb(0, 200, 83) else Color.rgb(229, 57, 53)
    val badgeRect = RectF(pageWidth - 210f, 22f, pageWidth - 24f, 62f)
    canvas.drawRoundRect(badgeRect, 6f, 6f, p)
    p.color = Color.WHITE
    p.textSize = 11f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText(if (invoice.isPaid) "FACTURE ACQUITTÉE" else "FACTURE À RÉGLER", pageWidth - 198f, 40f, p)
    p.textSize = 9.5f
    canvas.drawText("N° ${invoice.invoiceNumber}", pageWidth - 198f, 54f, p)

    // Legal Sub-header
    var y = 96f
    p.color = Color.rgb(100, 110, 120)
    p.textSize = 8.5f
    p.typeface = Typeface.DEFAULT
    canvas.drawText("Émetteur : ${invoice.garageName} • NIF: ${invoice.garageNif} • NIS: ${invoice.garageNis} • RC: ${invoice.garageRc}", 24f, y, p)
    canvas.drawText("Date : ${invoice.date} • Réf : ${invoice.qrFiscalHash}", pageWidth - 240f, y, p)

    // Company and Client Cards
    y += 18f
    val cardHeight = 94f

    // Company Box
    p.color = Color.rgb(240, 243, 246)
    canvas.drawRoundRect(RectF(24f, y, 285f, y + cardHeight), 8f, 8f, p)
    p.color = Color.rgb(215, 222, 230)
    p.style = Paint.Style.STROKE
    canvas.drawRoundRect(RectF(24f, y, 285f, y + cardHeight), 8f, 8f, p)
    p.style = Paint.Style.FILL

    p.color = Color.rgb(16, 20, 24)
    p.textSize = 9.5f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("IDENTIFICATION FISCALE ÉMETTEUR :", 34f, y + 18f, p)
    p.typeface = Typeface.DEFAULT
    p.textSize = 8.5f
    p.color = Color.rgb(40, 48, 56)
    canvas.drawText("Raison Sociale : ${invoice.garageName}", 34f, y + 33f, p)
    canvas.drawText("Adresse : ${invoice.garageAddress}", 34f, y + 47f, p)
    canvas.drawText("NIF : ${invoice.garageNif} / NIS : ${invoice.garageNis}", 34f, y + 61f, p)
    canvas.drawText("Registre de Commerce : ${invoice.garageRc}", 34f, y + 75f, p)

    // Client and Vehicle Box
    p.color = Color.rgb(240, 243, 246)
    canvas.drawRoundRect(RectF(305f, y, pageWidth - 24f, y + cardHeight), 8f, 8f, p)
    p.color = Color.rgb(215, 222, 230)
    p.style = Paint.Style.STROKE
    canvas.drawRoundRect(RectF(305f, y, pageWidth - 24f, y + cardHeight), 8f, 8f, p)
    p.style = Paint.Style.FILL

    p.color = Color.rgb(255, 122, 0)
    p.textSize = 9.5f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("CLIENT / BÉNÉFICIAIRE :", 315f, y + 18f, p)
    p.typeface = Typeface.DEFAULT
    p.textSize = 8.5f
    p.color = Color.rgb(40, 48, 56)
    canvas.drawText("Client : ${invoice.clientName} (Tél: ${invoice.clientPhone})", 315f, y + 33f, p)
    canvas.drawText("Véhicule : ${invoice.vehicleModel}", 315f, y + 47f, p)
    canvas.drawText("Matricule : ${invoice.plateNumber}", 315f, y + 61f, p)
    canvas.drawText("Châssis VIN : ${invoice.vehicleVin}", 315f, y + 75f, p)

    // Table Header
    y += cardHeight + 22f
    p.color = Color.rgb(37, 43, 49)
    canvas.drawRoundRect(RectF(24f, y, pageWidth - 24f, y + 24f), 4f, 4f, p)

    p.color = Color.WHITE
    p.textSize = 8.5f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("RÉFÉRENCE", 32f, y + 16f, p)
    canvas.drawText("DESCRIPTION DE L'ARTICLE / MAIN D'ŒUVRE", 130f, y + 16f, p)
    canvas.drawText("QTÉ", 360f, y + 16f, p)
    canvas.drawText("P.U HT (DA)", 410f, y + 16f, p)
    canvas.drawText("MONTANT HT (DA)", 490f, y + 16f, p)

    y += 26f
    var rowIdx = 0

    invoice.items.forEach { item ->
      if (rowIdx % 2 == 1) {
        p.color = Color.rgb(246, 248, 251)
        canvas.drawRect(24f, y - 2f, pageWidth - 24f, y + 18f, p)
      }

      p.color = if (item.isLabor) Color.rgb(0, 168, 204) else Color.rgb(255, 122, 0)
      p.textSize = 8f
      p.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
      canvas.drawText(item.reference.take(16), 32f, y + 11f, p)

      p.color = Color.rgb(30, 36, 42)
      p.textSize = 8.5f
      p.typeface = Typeface.DEFAULT
      canvas.drawText(item.description.take(40), 130f, y + 11f, p)
      canvas.drawText("${item.quantity}", 368f, y + 11f, p)
      canvas.drawText(df.format(item.unitPriceDzd), 415f, y + 11f, p)

      p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText(df.format(item.totalLineDzd), 495f, y + 11f, p)

      y += 20f
      rowIdx++
    }

    // Divider
    p.color = Color.rgb(200, 210, 220)
    canvas.drawLine(24f, y + 4f, pageWidth - 24f, y + 4f, p)
    y += 14f

    // Payment method info box (Left)
    p.color = Color.rgb(245, 247, 250)
    canvas.drawRoundRect(RectF(24f, y, 320f, y + 90f), 6f, 6f, p)
    p.color = Color.rgb(215, 222, 230)
    p.style = Paint.Style.STROKE
    canvas.drawRoundRect(RectF(24f, y, 320f, y + 90f), 6f, 6f, p)
    p.style = Paint.Style.FILL

    p.color = Color.rgb(16, 20, 24)
    p.textSize = 8.5f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("MODALITÉS DE RÈGLEMENT ET MENTIONS :", 32f, y + 16f, p)
    p.typeface = Typeface.DEFAULT
    p.textSize = 8f
    p.color = Color.rgb(60, 70, 80)
    canvas.drawText("• Mode de paiement : ${invoice.paymentMethod?.label ?: "Carte CIB / Edahabia"}", 32f, y + 30f, p)
    canvas.drawText("• Statut : ${if (invoice.isPaid) "FACTURE PAYÉE ET CERTIFIÉE" else "En attente de paiement"}", 32f, y + 44f, p)
    canvas.drawText("• Signature fiscale numérique : ${invoice.qrFiscalHash}", 32f, y + 58f, p)
    canvas.drawText("• Document émis conformément à la législation fiscale algérienne.", 32f, y + 72f, p)

    // Financial Calculation Card (Right)
    val calcLeft = 336f
    p.color = Color.rgb(245, 247, 250)
    canvas.drawRoundRect(RectF(calcLeft, y, pageWidth - 24f, y + 90f), 6f, 6f, p)
    p.color = Color.rgb(215, 222, 230)
    p.style = Paint.Style.STROKE
    canvas.drawRoundRect(RectF(calcLeft, y, pageWidth - 24f, y + 90f), 6f, 6f, p)
    p.style = Paint.Style.FILL

    p.color = Color.rgb(50, 60, 70)
    p.textSize = 8.5f
    p.typeface = Typeface.DEFAULT
    canvas.drawText("Sous-total Hors Taxes (HT) :", calcLeft + 10f, y + 18f, p)
    canvas.drawText("${df.format(invoice.subTotalDzd)} DZD", pageWidth - 34f - p.measureText("${df.format(invoice.subTotalDzd)} DZD"), y + 18f, p)

    canvas.drawText("TVA Légale (19.0%) :", calcLeft + 10f, y + 34f, p)
    canvas.drawText("${df.format(invoice.tvaAmountDzd)} DZD", pageWidth - 34f - p.measureText("${df.format(invoice.tvaAmountDzd)} DZD"), y + 34f, p)

    canvas.drawText("Droit de Timbre Fiscal :", calcLeft + 10f, y + 50f, p)
    canvas.drawText("${df.format(invoice.stampTaxDzd)} DZD", pageWidth - 34f - p.measureText("${df.format(invoice.stampTaxDzd)} DZD"), y + 50f, p)

    // Total Net TTC Bar
    p.color = Color.rgb(16, 20, 24)
    canvas.drawRect(calcLeft, y + 66f, pageWidth - 24f, y + 90f, p)
    p.color = Color.rgb(255, 122, 0)
    p.textSize = 10f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("TOTAL NET TTC (DA) :", calcLeft + 10f, y + 82f, p)
    val totalStr = "${df.format(invoice.totalTtcDzd)} DZD"
    p.color = Color.WHITE
    canvas.drawText(totalStr, pageWidth - 34f - p.measureText(totalStr), y + 82f, p)

    // Signatures
    y += 105f
    p.color = Color.rgb(240, 243, 246)
    canvas.drawRoundRect(RectF(24f, y, 280f, y + 68f), 6f, 6f, p)
    canvas.drawRoundRect(RectF(300f, y, pageWidth - 24f, y + 68f), 6f, 6f, p)

    p.color = Color.rgb(100, 110, 120)
    p.textSize = 8f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("CACHE ET SIGNATURE DU RESPONSABLE FISCAL", 34f, y + 16f, p)
    canvas.drawText("CONFIRMATION ET RÉCEPTION CLIENT", 310f, y + 16f, p)

    p.color = Color.rgb(160, 170, 180)
    p.typeface = Typeface.DEFAULT
    canvas.drawText("Direction Financière & Comptable • Alger", 34f, y + 54f, p)
    canvas.drawText("Signature & Acquittement", 310f, y + 54f, p)

    // Footer Bar
    canvas.drawRect(0f, pageHeight - 34f, pageWidth.toFloat(), pageHeight.toFloat(), p)
    p.color = Color.rgb(16, 20, 24)
    canvas.drawRect(0f, pageHeight - 34f, pageWidth.toFloat(), pageHeight.toFloat(), p)

    p.color = Color.rgb(0, 200, 83)
    p.textSize = 8.5f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("DOCUMENT FISCAL CERTIFIÉ CONFORME", 24f, pageHeight - 16f, p)

    p.color = Color.rgb(180, 190, 200)
    p.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
    p.textSize = 7.5f
    canvas.drawText("HASH DGI-DZ: ${invoice.qrFiscalHash}", 230f, pageHeight - 16f, p)

    document.finishPage(page)

    // Save file
    val fileName = "Facture_${invoice.invoiceNumber.replace("-", "_")}.pdf"
    val outputDir = getOutputDir(context)
    val file = File(outputDir, fileName)
    val fos = FileOutputStream(file)
    document.writeTo(fos)
    fos.flush()
    fos.close()
    document.close()

    val fileUri = getFileUri(context, file)

    return GeneratedPdfResult(
      file = file,
      fileName = fileName,
      filePath = file.absolutePath,
      fileSizeBytes = file.length(),
      documentType = "FACTURE FISCALE",
      documentNumber = invoice.invoiceNumber,
      clientName = invoice.clientName,
      totalAmountDzd = invoice.totalTtcDzd,
      uri = fileUri
    )
  }

  // --- 3. GÉNÉRATION FACTURE CONDUCTEUR PARTICULIER ---
  fun generateDriverInvoicePdf(
    context: Context,
    invoice: DriverInvoiceItem
  ): GeneratedPdfResult {
    val items = invoice.itemsDetail.mapIndexed { idx, desc ->
      val isLabor = desc.lowercase().contains("main d'œuvre") || desc.lowercase().contains("calage")
      val price = if (isLabor) invoice.laborAmountDzd else invoice.partsAmountDzd / (invoice.itemsDetail.size.coerceAtLeast(1))
      com.example.data.model.InvoiceLineItem(
        reference = "ART-2026-0$idx",
        description = desc,
        quantity = 1,
        unitPriceDzd = price.toDouble(),
        isLabor = isLabor
      )
    }

    val fiscalInvoice = FiscalInvoice(
      invoiceNumber = invoice.invoiceNumber,
      invoiceType = com.example.data.model.InvoiceType.FACTURE_DEFINITIVE,
      date = invoice.date,
      garageName = invoice.garageName,
      clientName = "Conducteur Particulier",
      clientPhone = invoice.garagePhone,
      vehicleVin = "WAUZZZ8V9KA042918",
      vehicleModel = invoice.vehicleInfo,
      plateNumber = "04291-120-16",
      items = items,
      isPaid = invoice.status.lowercase() == "payée",
      paymentMethod = com.example.data.model.PaymentMethod.CIB_EDAHABIA,
      qrFiscalHash = "DZ-COND-${invoice.invoiceNumber}"
    )

    return generateFiscalInvoicePdf(context, fiscalInvoice)
  }

  // --- 4. GÉNÉRATION CONTRAT DE LOCATION ---
  fun generateRentalContractPdf(
    context: Context,
    contract: RentalContract
  ): GeneratedPdfResult {
    val document = PdfDocument()
    val pageWidth = 595
    val pageHeight = 842
    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
    val page = document.startPage(pageInfo)
    val canvas = page.canvas

    val p = Paint().apply { isAntiAlias = true }

    // Background
    p.color = Color.rgb(250, 252, 255)
    canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), p)

    // Top Accent Bar
    p.color = Color.rgb(0, 168, 204) // Tech Cyan
    canvas.drawRect(0f, 0f, pageWidth.toFloat(), 10f, p)
    p.color = Color.rgb(16, 20, 24)
    canvas.drawRect(0f, 10f, pageWidth.toFloat(), 75f, p)

    // Title
    p.color = Color.WHITE
    p.textSize = 18f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("MEKANIK+ LOCATION FLEET PRO", 24f, 44f, p)

    p.color = Color.rgb(0, 200, 230)
    p.textSize = 9.5f
    p.typeface = Typeface.DEFAULT
    canvas.drawText("CONTRAT DE LOCATION DE VÉHICULE SANS CHAUFFEUR", 24f, 60f, p)

    // Contract Badge
    p.color = Color.rgb(0, 168, 204)
    val badgeRect = RectF(pageWidth - 210f, 22f, pageWidth - 24f, 62f)
    canvas.drawRoundRect(badgeRect, 6f, 6f, p)
    p.color = Color.WHITE
    p.textSize = 11f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("CONTRAT OFFICIEL", pageWidth - 195f, 40f, p)
    p.textSize = 9.5f
    canvas.drawText("N° ${contract.contractId}", pageWidth - 195f, 54f, p)

    var y = 96f
    p.color = Color.rgb(100, 110, 120)
    p.textSize = 8.5f
    canvas.drawText("Agence : Mekanik+ Location Alger • Tél: 0555 12 34 56 • RC: 16/00-789654A20", 24f, y, p)
    canvas.drawText("Période : ${contract.startDate} au ${contract.endDate}", pageWidth - 230f, y, p)

    // Details Boxes
    y += 18f
    val cardHeight = 90f

    // Vehicle Box
    p.color = Color.rgb(240, 243, 246)
    canvas.drawRoundRect(RectF(24f, y, 285f, y + cardHeight), 8f, 8f, p)
    p.color = Color.rgb(215, 222, 230)
    p.style = Paint.Style.STROKE
    canvas.drawRoundRect(RectF(24f, y, 285f, y + cardHeight), 8f, 8f, p)
    p.style = Paint.Style.FILL

    p.color = Color.rgb(16, 20, 24)
    p.textSize = 9.5f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("VÉHICULE LOUÉ :", 34f, y + 18f, p)
    p.typeface = Typeface.DEFAULT
    p.textSize = 8.5f
    p.color = Color.rgb(40, 48, 56)
    canvas.drawText("Modèle : ${contract.vehicleName}", 34f, y + 33f, p)
    canvas.drawText("Immatriculation : ${contract.plateNumber}", 34f, y + 47f, p)
    canvas.drawText("Kilométrage Départ : ${df.format(contract.initialKm)} km", 34f, y + 61f, p)
    canvas.drawText("Caution Bloquée : ${df.format(contract.depositAmountDzd)} DZD", 34f, y + 75f, p)

    // Client Box
    p.color = Color.rgb(240, 243, 246)
    canvas.drawRoundRect(RectF(305f, y, pageWidth - 24f, y + cardHeight), 8f, 8f, p)
    p.color = Color.rgb(215, 222, 230)
    p.style = Paint.Style.STROKE
    canvas.drawRoundRect(RectF(305f, y, pageWidth - 24f, y + cardHeight), 8f, 8f, p)
    p.style = Paint.Style.FILL

    p.color = Color.rgb(0, 168, 204)
    p.textSize = 9.5f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("LOCATAIRE / CONDUCTEUR :", 315f, y + 18f, p)
    p.typeface = Typeface.DEFAULT
    p.textSize = 8.5f
    p.color = Color.rgb(40, 48, 56)
    canvas.drawText("Nom & Prénom : ${contract.clientFullName}", 315f, y + 33f, p)
    canvas.drawText("Téléphone : ${contract.clientPhone}", 315f, y + 47f, p)
    canvas.drawText("N° Pièce Identité : ${contract.clientNationalId}", 315f, y + 61f, p)
    canvas.drawText("N° Permis de Conduire : ${contract.clientDrivingLicense}", 315f, y + 75f, p)

    // Financial Calculation Card
    y += cardHeight + 20f
    p.color = Color.rgb(245, 247, 250)
    canvas.drawRoundRect(RectF(24f, y, pageWidth - 24f, y + 70f), 8f, 8f, p)
    p.color = Color.rgb(215, 222, 230)
    p.style = Paint.Style.STROKE
    canvas.drawRoundRect(RectF(24f, y, pageWidth - 24f, y + 70f), 8f, 8f, p)
    p.style = Paint.Style.FILL

    p.color = Color.rgb(16, 20, 24)
    p.textSize = 9f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("RÉCAPITULATIF FINANCIER DU CONTRAT :", 34f, y + 18f, p)
    p.typeface = Typeface.DEFAULT
    p.color = Color.rgb(50, 60, 70)
    canvas.drawText("Tarif Journalier : ${df.format(contract.dailyRateDzd)} DZD/jour • Durée : ${contract.durationDays} Jours", 34f, y + 34f, p)
    canvas.drawText("Caution de Garantie (CIB / Empreinte) : ${df.format(contract.depositAmountDzd)} DZD", 34f, y + 48f, p)

    p.color = Color.rgb(0, 168, 204)
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("MONTANT TOTAL LOCATION : ${df.format(contract.totalRentDzd)} DZD TTC", 34f, y + 62f, p)

    // Clauses and Signatures
    y += 85f
    p.color = Color.rgb(240, 243, 246)
    canvas.drawRoundRect(RectF(24f, y, 280f, y + 80f), 6f, 6f, p)
    canvas.drawRoundRect(RectF(300f, y, pageWidth - 24f, y + 80f), 6f, 6f, p)

    p.color = Color.rgb(100, 110, 120)
    p.textSize = 8f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("SIGNATURE DE L'AGENCE DE LOCATION", 34f, y + 16f, p)
    canvas.drawText("SIGNATURE DU LOCATAIRE (BON POUR ACCORD)", 310f, y + 16f, p)

    p.color = Color.rgb(160, 170, 180)
    p.typeface = Typeface.DEFAULT
    canvas.drawText("Mekanik+ Fleet Services • Alger", 34f, y + 64f, p)
    canvas.drawText("« Reconnaît avoir pris le véhicule en parfait état »", 310f, y + 64f, p)

    // Footer Bar
    canvas.drawRect(0f, pageHeight - 34f, pageWidth.toFloat(), pageHeight.toFloat(), p)
    p.color = Color.rgb(16, 20, 24)
    canvas.drawRect(0f, pageHeight - 34f, pageWidth.toFloat(), pageHeight.toFloat(), p)

    p.color = Color.rgb(0, 168, 204)
    p.textSize = 8.5f
    p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("CONTRAT CERTIFIÉ MEKANIK+ FLEET", 24f, pageHeight - 16f, p)

    document.finishPage(page)

    val fileName = "Contrat_${contract.contractId.replace("-", "_")}.pdf"
    val outputDir = getOutputDir(context)
    val file = File(outputDir, fileName)
    val fos = FileOutputStream(file)
    document.writeTo(fos)
    fos.flush()
    fos.close()
    document.close()

    val fileUri = getFileUri(context, file)

    return GeneratedPdfResult(
      file = file,
      fileName = fileName,
      filePath = file.absolutePath,
      fileSizeBytes = file.length(),
      documentType = "CONTRAT DE LOCATION",
      documentNumber = contract.contractId,
      clientName = contract.clientFullName,
      totalAmountDzd = contract.totalRentDzd.toDouble(),
      uri = fileUri
    )
  }
}
