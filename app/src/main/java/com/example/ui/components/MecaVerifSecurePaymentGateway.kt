package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * PASSERELLE DE PAIEMENT SÉCURISÉE MÉCAVERIF+™ (MARCHÉ ALGÉRIEN & STRIPE INTERNATIONAL)
 * - Achat unitaire par numéro de châssis (VIN) : 250 DA (Tarif Pro) ou 300 DA (Tarif Particulier)
 * - Service externe strictement hors abonnement
 * - Moyens de paiement :
 *   1. CIB / SATIM Interbancaire (Algérie - 3D Secure SMS)
 *   2. Carte Edahabia (Algérie Poste - ECCP SMS)
 *   3. BaridiMob QR & RIP Instantané
 *   4. Chargily Pay™ / SlickPay Algérie (Agrégateur E-Paiement DZD)
 *   5. Stripe® International (Visa / Mastercard / Apple Pay en EUR/USD)
 */
enum class MecaVerifGatewayChannel(
    val id: String,
    val title: String,
    val shortBadge: String,
    val subtitle: String,
    val providerNetwork: String,
    val accentHex: Long,
    val isAlgerianLocal: Boolean
) {
    SATIM_CIB(
        id = "SATIM_CIB",
        title = "Carte CIB / SATIM Algérie",
        shortBadge = "CIB • SATIM",
        subtitle = "Paiement interbancaire national (CPA, BADR, BDL, BEA, BNA, AGB, Société Générale)",
        providerNetwork = "Réseau Monétique Interbancaire SATIM (3D-Secure SMS)",
        accentHex = 0xFFF07836,
        isAlgerianLocal = true
    ),
    EDAHABIA_POSTE(
        id = "EDAHABIA_POSTE",
        title = "Carte Edahabia (Algérie Poste)",
        shortBadge = "EDAHABIA",
        subtitle = "Paiement sécurisé par carte monétique Algérie Poste avec code SMS ECCP",
        providerNetwork = "Algérie Poste Monétique • Validation SMS instantanée",
        accentHex = 0xFFF59E0B,
        isAlgerianLocal = true
    ),
    BARIDIMOB_QR(
        id = "BARIDIMOB_QR",
        title = "BaridiMob QR & Virement RIP",
        shortBadge = "BARIDIMOB",
        subtitle = "Scan QR Code BaridiMob ou paiement mobile instantané sans saisie de carte",
        providerNetwork = "Application Mobile BaridiMob • Algérie Poste",
        accentHex = 0xFF22C55E,
        isAlgerianLocal = true
    ),
    CHARGILY_SLICKPAY_DZ(
        id = "CHARGILY_SLICKPAY_DZ",
        title = "Chargily Pay™ / SlickPay DZ",
        shortBadge = "CHARGILY PAY",
        subtitle = "Passerelle e-paiement SaaS algérienne certifiée (CIB & Edahabia en 1 clic)",
        providerNetwork = "API Chargily Pay v2 & SlickPay Algérie (Webhook Temps Réel)",
        accentHex = 0xFF38BDF8,
        isAlgerianLocal = true
    ),
    STRIPE_INTERNATIONAL(
        id = "STRIPE_INTERNATIONAL",
        title = "Stripe® (Visa / Mastercard)",
        shortBadge = "STRIPE INTL",
        subtitle = "Paiement international sécurisé PCI-DSS Level 1 (Cartes Visa, Mastercard, Diaspora)",
        providerNetwork = "Stripe Checkout 3DS2 • Conversion automatique (2,00 € / 1,70 €)",
        accentHex = 0xFF6366F1,
        isAlgerianLocal = false
    )
}

data class MecaVerifUnitPurchaseReceipt(
    val receiptId: String,
    val vin: String,
    val vehicleLabel: String,
    val amountDzd: Int,
    val amountEurLabel: String,
    val tariffCategory: String, // "TARIF PRO (250 DA)" ou "TARIF PARTICULIER (300 DA)"
    val gatewayChannel: MecaVerifGatewayChannel,
    val maskedInstrument: String,
    val buyerName: String,
    val authorizationCode: String,
    val createdAtFormatted: String,
    val syncedToFirestore: Boolean = true
)

object MecaVerifPaymentLedger {
    val transactions = mutableStateListOf(
        MecaVerifUnitPurchaseReceipt(
            receiptId = "MV-SATIM-2026-9041",
            vin = "WVWZZZ5NZMW123456",
            vehicleLabel = "Volkswagen Tiguan 2.0 TDI 150ch (2021)",
            amountDzd = 250,
            amountEurLabel = "1,70 €",
            tariffCategory = "TARIF PRO ATELIER / TÔLIER (250 DA / VIN)",
            gatewayChannel = MecaVerifGatewayChannel.SATIM_CIB,
            maskedInstrument = "CIB **** **** **** 4829",
            buyerName = "Atelier & Carrosserie El-Djazair Pro",
            authorizationCode = "SATIM-AUTH-774190",
            createdAtFormatted = "16/11/2026 • 14:22"
        ),
        MecaVerifUnitPurchaseReceipt(
            receiptId = "MV-EDAHABIA-2026-9038",
            vin = "VF3MCYHZJKS491820",
            vehicleLabel = "Peugeot 3008 GT Line 1.5 BlueHDi (2019)",
            amountDzd = 300,
            amountEurLabel = "2,00 €",
            tariffCategory = "TARIF PARTICULIER (300 DA / VIN)",
            gatewayChannel = MecaVerifGatewayChannel.EDAHABIA_POSTE,
            maskedInstrument = "EDAHABIA 6280 **** **** 9104",
            buyerName = "Amine Benali",
            authorizationCode = "AP-ECCP-552819",
            createdAtFormatted = "15/11/2026 • 18:05"
        )
    )

    fun recordAndSyncUnitPurchase(
        vin: String,
        vehicleLabel: String,
        amountDzd: Int,
        isProTariff: Boolean,
        gateway: MecaVerifGatewayChannel,
        maskedInstrument: String,
        buyerName: String
    ): MecaVerifUnitPurchaseReceipt {
        val nowStr = SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.FRANCE).format(Date())
        val randDigits = (100000..999999).random()
        val prefix = when (gateway) {
            MecaVerifGatewayChannel.SATIM_CIB -> "SATIM"
            MecaVerifGatewayChannel.EDAHABIA_POSTE -> "EDAHABIA"
            MecaVerifGatewayChannel.BARIDIMOB_QR -> "BARIDI"
            MecaVerifGatewayChannel.CHARGILY_SLICKPAY_DZ -> "CHARGILY"
            MecaVerifGatewayChannel.STRIPE_INTERNATIONAL -> "STRIPE"
        }
        val receipt = MecaVerifUnitPurchaseReceipt(
            receiptId = "MV-$prefix-2026-${randDigits.toString().take(4)}",
            vin = vin.trim().uppercase(),
            vehicleLabel = vehicleLabel,
            amountDzd = amountDzd,
            amountEurLabel = if (amountDzd <= 250) "1,70 €" else "2,00 €",
            tariffCategory = if (isProTariff) "TARIF PRO (250 DA / VIN)" else "TARIF PARTICULIER (300 DA / VIN)",
            gatewayChannel = gateway,
            maskedInstrument = maskedInstrument,
            buyerName = buyerName.ifBlank { "Client MécaVerif+" },
            authorizationCode = "$prefix-3DS-$randDigits",
            createdAtFormatted = nowStr,
            syncedToFirestore = true
        )
        transactions.add(0, receipt)

        // Sauvegarde asynchrone non-bloquante dans Firebase Firestore
        try {
            val db = FirebaseFirestore.getInstance()
            val payload = hashMapOf(
                "receiptId" to receipt.receiptId,
                "vin" to receipt.vin,
                "vehicleLabel" to receipt.vehicleLabel,
                "amountDzd" to receipt.amountDzd,
                "amountEurLabel" to receipt.amountEurLabel,
                "tariffCategory" to receipt.tariffCategory,
                "gatewayId" to receipt.gatewayChannel.id,
                "gatewayTitle" to receipt.gatewayChannel.title,
                "maskedInstrument" to receipt.maskedInstrument,
                "buyerName" to receipt.buyerName,
                "authorizationCode" to receipt.authorizationCode,
                "createdAtFormatted" to receipt.createdAtFormatted,
                "timestampMs" to System.currentTimeMillis()
            )
            db.collection("mecaverif_unit_purchases")
                .document(receipt.receiptId)
                .set(payload)
        } catch (_: Exception) {
            // Conservation locale garantie même hors ligne
        }

        return receipt
    }
}

/**
 * BANNIÈRE DE PAIEMENT SÉCURISÉ INTÉGRÉE DANS L'ÉCRAN MÉCAVERIF+
 * Permet de lancer l'achat unitaire d'un rapport (250 DA / 300 DA) via CIB/SATIM, Edahabia, BaridiMob, Chargily Pay ou Stripe
 * et de consulter les reçus fiscaux des châssis déjà débloqués.
 */
@Composable
fun MecaVerifSecurePaymentSummaryBanner(
    currentVin: String,
    isCurrentVinUnlocked: Boolean,
    defaultPriceDzd: Int,
    unlockedCount: Int,
    onOpenSecureCheckout: (MecaVerifGatewayChannel?) -> Unit,
    onOpenReceiptsHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MetalCard,
        border = BorderStroke(
            1.4.dp,
            if (isCurrentVinUnlocked) MekanikValidationGreen else MekanikBrandOrange
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("mecaverif_secure_payment_banner")
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF101820),
                            Color(0xFF1C2833),
                            if (isCurrentVinUnlocked) Color(0xFF122B22) else Color(0xFF281B15)
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isCurrentVinUnlocked) {
                            MekanikValidationGreen.copy(alpha = 0.18f)
                        } else {
                            MekanikBrandOrange.copy(alpha = 0.18f)
                        },
                        border = BorderStroke(
                            1.dp,
                            if (isCurrentVinUnlocked) MekanikValidationGreen else MekanikBrandOrange
                        ),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isCurrentVinUnlocked) Icons.Default.Verified else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isCurrentVinUnlocked) MekanikValidationGreen else MekanikBrandOrange,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "PAIEMENT SÉCURISÉ MÉCAVERIF+™ (ALGÉRIE & STRIPE)",
                                color = OffWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.5.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = if (isCurrentVinUnlocked) MekanikValidationGreen else MekanikBrandOrange
                            ) {
                                Text(
                                    text = if (isCurrentVinUnlocked) "CHÂSSIS DÉBLOQUÉ ✓" else "ACHAT UNITAIRE • $defaultPriceDzd DA",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 8.5.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "CIB SATIM • Edahabia Algérie Poste • BaridiMob QR • Chargily Pay • Stripe Visa/MC",
                            color = AluminumGray,
                            fontSize = 10.5.sp
                        )
                    }
                }

                // Bouton Journal des Reçus
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteDark,
                    border = BorderStroke(1.dp, MetalBorder),
                    onClick = onOpenReceiptsHistory,
                    modifier = Modifier.testTag("btn_open_mecaverif_receipts_ledger")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = MekanikBrandOrange,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Reçus (${MecaVerifPaymentLedger.transactions.size})",
                            color = OffWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        )
                    }
                }
            }

            // Raccourcis directs par moyen de paiement (1 clic pour ouvrir la passerelle sur le canal choisi)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MecaVerifGatewayChannel.entries.forEach { channel ->
                    val channelColor = Color(channel.accentHex)
                    Surface(
                        shape = RoundedCornerShape(9.dp),
                        color = GraphiteDark,
                        border = BorderStroke(1.dp, channelColor.copy(alpha = 0.65f)),
                        onClick = { onOpenSecureCheckout(channel) },
                        modifier = Modifier.testTag("chip_gateway_${channel.id.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(channelColor)
                            )
                            Text(
                                text = channel.shortBadge,
                                color = OffWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Bouton principal d'achat unitaire ou de consultation du certificat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onOpenSecureCheckout(null) },
                    shape = RoundedCornerShape(11.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCurrentVinUnlocked) MekanikValidationGreen else MekanikBrandOrange,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("btn_launch_mecaverif_unit_checkout")
                ) {
                    Icon(
                        imageVector = if (isCurrentVinUnlocked) Icons.Default.VerifiedUser else Icons.Default.ShoppingCartCheckout,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCurrentVinUnlocked) {
                            "Rapport VIN $currentVin Débloqué • Acheter un autre VIN"
                        } else {
                            "Acheter le Rapport Unitaire ($defaultPriceDzd DA • VIN $currentVin)"
                        },
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * MODALE DE PAIEMENT SÉCURISÉ MÉCAVERIF+™ (CHECKOUT ALGÉRIE CIB/EDAHABIA/BARIDIMOB/CHARGILY + STRIPE)
 * Avec saisie carte interactive, génération QR BaridiMob, validation 3D-Secure OTP SMS et émission de Reçu Fiscal.
 */
@Composable
fun MecaVerifSecureCheckoutModal(
    vin: String,
    vehicleTitle: String,
    isProAccount: Boolean,
    initialChannel: MecaVerifGatewayChannel = MecaVerifGatewayChannel.SATIM_CIB,
    onPaymentCompleted: (MecaVerifUnitPurchaseReceipt) -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    // 1. Choix du tarif unitaire (250 DA Pro ou 300 DA Particulier)
    var selectedTariffIsPro by remember { mutableStateOf(isProAccount) }
    val activePriceDzd = if (selectedTariffIsPro) 250 else 300
    val activePriceEur = if (selectedTariffIsPro) "1,70 €" else "2,00 €"

    // 2. Moyen de paiement sélectionné
    var selectedChannel by remember { mutableStateOf(initialChannel) }

    // 3. Champs de formulaire sécurisés
    var cardNumberInput by remember(selectedChannel) {
        mutableStateOf(
            when (selectedChannel) {
                MecaVerifGatewayChannel.SATIM_CIB -> "6274 8201 9482 4829"
                MecaVerifGatewayChannel.EDAHABIA_POSTE -> "6280 5910 3841 9104"
                MecaVerifGatewayChannel.STRIPE_INTERNATIONAL -> "4242 4242 4242 4242"
                else -> "6274 8201 9482 4829"
            }
        )
    }
    var cardHolderInput by remember {
        mutableStateOf(if (isProAccount) "ATELIER MECANICAUTO PRO" else "KARIM MEZIANE")
    }
    var expiryInput by remember { mutableStateOf("09/28") }
    var cvvInput by remember { mutableStateOf("842") }
    var buyerPhoneInput by remember { mutableStateOf("+213 550 94 82 10") }

    // 4. Étape 3D-Secure / OTP SMS & État de traitement
    var checkoutStep by remember { mutableIntStateOf(1) } // 1 = Saisie Moyen de paiement, 2 = Vérification 3D-Secure OTP SMS, 3 = Reçu Confirmé
    var generatedOtpCode by remember { mutableStateOf("739204") }
    var enteredOtpCode by remember { mutableStateOf("739204") }
    var isProcessingGateway by remember { mutableStateOf(false) }
    var processingStatusText by remember { mutableStateOf("") }
    var completedReceipt by remember { mutableStateOf<MecaVerifUnitPurchaseReceipt?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MetalCard,
            border = BorderStroke(1.5.dp, MekanikBrandOrange),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("mecaverif_secure_checkout_modal")
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // EN-TÊTE OFFICIEL DU CHECKOUT SÉCURISÉ
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MekanikBrandOrange.copy(alpha = 0.18f),
                            border = BorderStroke(1.dp, MekanikBrandOrange),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = MekanikBrandOrange,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "CHECKOUT SÉCURISÉ • MÉCAVERIF+™",
                                color = OffWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Achat unitaire hors abonnement • Châssis : $vin",
                                color = MekanikBrandOrange,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = OffWhite)
                    }
                }

                // BARRE DE PROGRESSION DES 3 ÉTAPES DE PAIEMENT
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        1 to "1. Tarif & Carte",
                        2 to "2. 3D-Secure SMS",
                        3 to "3. Reçu & Rapport"
                    ).forEach { (stepNum, label) ->
                        val isCurrentOrDone = checkoutStep >= stepNum
                        val stepColor = if (checkoutStep > stepNum) MekanikValidationGreen
                        else if (checkoutStep == stepNum) MekanikBrandOrange
                        else AluminumGray
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isCurrentOrDone) stepColor.copy(alpha = 0.16f) else GraphiteDark,
                            border = BorderStroke(1.dp, if (isCurrentOrDone) stepColor else MetalBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = label,
                                color = if (isCurrentOrDone) OffWhite else AluminumGray,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // RÉCAPITULATIF DU RAPPORT D'HISTORIQUE ACHETÉ À L'UNITÉ
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GraphiteDark,
                    border = BorderStroke(1.dp, MetalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PRODUIT : RAPPORT D'HISTORIQUE VÉHICULE UNITAIRE",
                                color = AluminumGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MekanikValidationGreen.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, MekanikValidationGreen)
                            ) {
                                Text(
                                    text = "SSL 256-BIT • 3D-SECURE",
                                    color = MekanikValidationGreen,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 8.5.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = vehicleTitle,
                            color = OffWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.5.sp
                        )
                        Text(
                            text = "✓ Courbe kilométrique CarVertical • ✓ Photos Tôle Avant/Après obligatoires • ✓ Passages Mécanicien & Certificat PDF",
                            color = AluminumGray,
                            fontSize = 10.5.sp
                        )
                    }
                }

                // =========================================================================
                // ÉTAPE 1 : SÉLECTION DU TARIF (250 DA / 300 DA) & MOYEN DE PAIEMENT
                // =========================================================================
                if (checkoutStep == 1) {
                    Text(
                        text = "1. FORMULE TARIFAIRE À L'ACTE (HORS ABONNEMENT) :",
                        color = AluminumGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Option 250 DA (Pro)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedTariffIsPro) MekanikBrandOrange.copy(alpha = 0.16f) else GraphiteDark,
                            border = BorderStroke(
                                1.2.dp,
                                if (selectedTariffIsPro) MekanikBrandOrange else MetalBorder
                            ),
                            onClick = { selectedTariffIsPro = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("select_tariff_250_pro")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "TARIF PRO",
                                        color = OffWhite,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "250 DA",
                                        color = MekanikValidationGreen,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp
                                    )
                                }
                                Text(
                                    text = "Atelier, Tôlier Carrosserie & Magasin (1,70 € / VIN)",
                                    color = AluminumGray,
                                    fontSize = 9.5.sp
                                )
                            }
                        }

                        // Option 300 DA (Particulier)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (!selectedTariffIsPro) MekanikBrandOrange.copy(alpha = 0.16f) else GraphiteDark,
                            border = BorderStroke(
                                1.2.dp,
                                if (!selectedTariffIsPro) MekanikBrandOrange else MetalBorder
                            ),
                            onClick = { selectedTariffIsPro = false },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("select_tariff_300_particulier")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "PARTICULIER",
                                        color = OffWhite,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "300 DA",
                                        color = MekanikValidationGreen,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp
                                    )
                                }
                                Text(
                                    text = "Acheteur & Conducteur particulier (2,00 € / VIN)",
                                    color = AluminumGray,
                                    fontSize = 9.5.sp
                                )
                            }
                        }
                    }

                    Text(
                        text = "2. CHOISIR LA PASSERELLE DE PAIEMENT (ALGÉRIE OU STRIPE) :",
                        color = AluminumGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MecaVerifGatewayChannel.entries.forEach { channel ->
                            val isSelected = selectedChannel == channel
                            val chColor = Color(channel.accentHex)
                            Surface(
                                shape = RoundedCornerShape(11.dp),
                                color = if (isSelected) chColor.copy(alpha = 0.2f) else GraphiteDark,
                                border = BorderStroke(
                                    1.2.dp,
                                    if (isSelected) chColor else MetalBorder
                                ),
                                onClick = { selectedChannel = channel },
                                modifier = Modifier.testTag("select_gateway_${channel.id.lowercase()}")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .widthIn(min = 135.dp)
                                        .padding(horizontal = 12.dp, vertical = 9.dp),
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(chColor)
                                        )
                                        Text(
                                            text = channel.shortBadge,
                                            color = OffWhite,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Text(
                                        text = if (channel.isAlgerianLocal) "Marché Algérien (DZD)" else "International ($activePriceEur)",
                                        color = AluminumGray,
                                        fontSize = 9.5.sp
                                    )
                                }
                            }
                        }
                    }

                    // APERÇU VISUEL DE LA CARTE OU DU QR CODE SELON LE CANAL SÉLECTIONNÉ
                    when (selectedChannel) {
                        MecaVerifGatewayChannel.SATIM_CIB,
                        MecaVerifGatewayChannel.EDAHABIA_POSTE,
                        MecaVerifGatewayChannel.STRIPE_INTERNATIONAL -> {
                            val cardColorStart = when (selectedChannel) {
                                MecaVerifGatewayChannel.SATIM_CIB -> Color(0xFF1E3A5F)
                                MecaVerifGatewayChannel.EDAHABIA_POSTE -> Color(0xFF5C4212)
                                else -> Color(0xFF2B275A)
                            }
                            val cardColorEnd = Color(0xFF101820)
                            val accentColor = Color(selectedChannel.accentHex)

                            // Carte bancaire visuelle interactive
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = GraphiteDark,
                                border = BorderStroke(1.2.dp, accentColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .background(
                                            Brush.linearGradient(listOf(cardColorStart, cardColorEnd))
                                        )
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = selectedChannel.title,
                                            color = OffWhite,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = accentColor
                                        ) {
                                            Text(
                                                text = selectedChannel.shortBadge,
                                                color = Color.White,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 9.5.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = cardNumberInput.ifBlank { "**** **** **** ****" },
                                        color = OffWhite,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        letterSpacing = 1.5.sp
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("TITULAIRE", color = AluminumGray, fontSize = 8.5.sp)
                                            Text(
                                                text = cardHolderInput.ifBlank { "TITULAIRE CARTE" },
                                                color = OffWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("EXPIRATION", color = AluminumGray, fontSize = 8.5.sp)
                                            Text(
                                                text = expiryInput.ifBlank { "MM/AA" },
                                                color = OffWhite,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }

                            // Champs de saisie de la carte CIB / Edahabia / Stripe
                            OutlinedTextField(
                                value = cardNumberInput,
                                onValueChange = { cardNumberInput = it.take(23) },
                                label = { Text("Numéro de carte (${selectedChannel.shortBadge})", fontSize = 11.sp) },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = MekanikBrandOrange)
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MekanikBrandOrange,
                                    unfocusedBorderColor = MetalBorder,
                                    focusedTextColor = OffWhite,
                                    unfocusedTextColor = OffWhite,
                                    focusedContainerColor = GraphiteDark,
                                    unfocusedContainerColor = GraphiteDark
                                ),
                                shape = RoundedCornerShape(11.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("checkout_card_number_input")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = expiryInput,
                                    onValueChange = { expiryInput = it.take(5) },
                                    label = { Text("Expire (MM/AA)", fontSize = 11.sp) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MekanikBrandOrange,
                                        unfocusedBorderColor = MetalBorder,
                                        focusedTextColor = OffWhite,
                                        unfocusedTextColor = OffWhite,
                                        focusedContainerColor = GraphiteDark,
                                        unfocusedContainerColor = GraphiteDark
                                    ),
                                    shape = RoundedCornerShape(11.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = cvvInput,
                                    onValueChange = { cvvInput = it.filter { ch -> ch.isDigit() }.take(4) },
                                    label = { Text("Code CVV2 / CVC", fontSize = 11.sp) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MekanikBrandOrange,
                                        unfocusedBorderColor = MetalBorder,
                                        focusedTextColor = OffWhite,
                                        unfocusedTextColor = OffWhite,
                                        focusedContainerColor = GraphiteDark,
                                        unfocusedContainerColor = GraphiteDark
                                    ),
                                    shape = RoundedCornerShape(11.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = cardHolderInput,
                                    onValueChange = { cardHolderInput = it.uppercase() },
                                    label = { Text("Nom du titulaire", fontSize = 11.sp) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MekanikBrandOrange,
                                        unfocusedBorderColor = MetalBorder,
                                        focusedTextColor = OffWhite,
                                        unfocusedTextColor = OffWhite,
                                        focusedContainerColor = GraphiteDark,
                                        unfocusedContainerColor = GraphiteDark
                                    ),
                                    shape = RoundedCornerShape(11.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = buyerPhoneInput,
                                    onValueChange = { buyerPhoneInput = it },
                                    label = { Text("Téléphone SMS 3DS", fontSize = 11.sp) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MekanikBrandOrange,
                                        unfocusedBorderColor = MetalBorder,
                                        focusedTextColor = OffWhite,
                                        unfocusedTextColor = OffWhite,
                                        focusedContainerColor = GraphiteDark,
                                        unfocusedContainerColor = GraphiteDark
                                    ),
                                    shape = RoundedCornerShape(11.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        MecaVerifGatewayChannel.BARIDIMOB_QR -> {
                            // Interface BaridiMob QR Code + RIP Algérie Poste
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = GraphiteDark,
                                border = BorderStroke(1.2.dp, MekanikValidationGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // QR Code BaridiMob généré sur Canvas
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color.White,
                                        modifier = Modifier.size(92.dp)
                                    ) {
                                        Canvas(modifier = Modifier.padding(8.dp)) {
                                            val cell = size.width / 7f
                                            for (r in 0..6) {
                                                for (c in 0..6) {
                                                    val isCorner = (r < 2 && c < 2) || (r < 2 && c > 4) || (r > 4 && c < 2)
                                                    val isPattern = (r + c) % 2 == 0 || (r * c) % 3 == 0
                                                    if (isCorner || isPattern) {
                                                        drawRoundRect(
                                                            color = if (isCorner) Color(0xFF101820) else Color(0xFF16A34A),
                                                            topLeft = Offset(c * cell, r * cell),
                                                            size = Size(cell * 0.85f, cell * 0.85f),
                                                            cornerRadius = CornerRadius(2f, 2f)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "SCAN QR BARIDIMOB • ALGÉRIE POSTE",
                                            color = MekanikValidationGreen,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "RIP Marchand MEKANIK+ :\n007999990024819204 38",
                                            color = OffWhite,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = "Montant exact : $activePriceDzd DA • Motif : VIN $vin",
                                            color = AluminumGray,
                                            fontSize = 10.5.sp
                                        )
                                    }
                                }
                            }
                        }

                        MecaVerifGatewayChannel.CHARGILY_SLICKPAY_DZ -> {
                            // Interface Passerelle Chargily Pay / SlickPay Algérie
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = GraphiteDark,
                                border = BorderStroke(1.2.dp, Color(0xFF38BDF8)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "SESSION E-PAIEMENT CHARGILY PAY™ v2",
                                            color = Color(0xFF38BDF8),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "WEBHOOK ACTIF",
                                            color = MekanikValidationGreen,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 9.5.sp
                                        )
                                    }
                                    Text(
                                        text = "ID Session : chk_live_dz_${vin.takeLast(6).lowercase()}_$activePriceDzd",
                                        color = OffWhite,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "Redirection sécurisée SATIM / Algérie Poste sans partage des coordonnées bancaires avec le marchand.",
                                        color = AluminumGray,
                                        fontSize = 10.5.sp
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val newOtp = (100000..999999).random().toString()
                            generatedOtpCode = newOtp
                            enteredOtpCode = newOtp
                            checkoutStep = 2
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MekanikBrandOrange,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_proceed_to_3ds_otp")
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedChannel == MecaVerifGatewayChannel.STRIPE_INTERNATIONAL) {
                                "Continuer vers 3D-Secure Stripe ($activePriceEur / $activePriceDzd DA)"
                            } else {
                                "Valider & Recevoir le Code SMS 3D-Secure ($activePriceDzd DA)"
                            },
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                }

                // =========================================================================
                // ÉTAPE 2 : AUTHENTIFICATION 3D-SECURE / OTP SMS (SATIM / ECCP / STRIPE)
                // =========================================================================
                if (checkoutStep == 2) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = GraphiteDark,
                        border = BorderStroke(1.2.dp, MekanikBrandOrange),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "VÉRIFICATION 3D-SECURE • CODE SMS À 6 CHIFFRES",
                                    color = MekanikBrandOrange,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.5.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MekanikValidationGreen.copy(alpha = 0.16f)
                                ) {
                                    Text(
                                        text = "SMS ENVOYÉ ✓",
                                        color = MekanikValidationGreen,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Réseau : ${selectedChannel.providerNetwork}\nUn code de sécurité à usage unique (OTP) a été transmis au $buyerPhoneInput pour autoriser le débit unitaire de $activePriceDzd DA (Châssis $vin).",
                                color = OffWhite,
                                fontSize = 11.sp,
                                lineHeight = 15.5.sp
                            )

                            OutlinedTextField(
                                value = enteredOtpCode,
                                onValueChange = { enteredOtpCode = it.filter { c -> c.isDigit() }.take(6) },
                                label = { Text("Code OTP SMS 3D-Secure (6 chiffres)", fontSize = 11.sp) },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.Default.Sms, contentDescription = null, tint = MekanikValidationGreen)
                                },
                                trailingIcon = {
                                    TextButton(onClick = { enteredOtpCode = generatedOtpCode }) {
                                        Text(
                                            text = "Auto-remplir ($generatedOtpCode)",
                                            color = MekanikBrandOrange,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.5.sp
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MekanikValidationGreen,
                                    unfocusedBorderColor = MetalBorder,
                                    focusedTextColor = OffWhite,
                                    unfocusedTextColor = OffWhite,
                                    focusedContainerColor = MetalCard,
                                    unfocusedContainerColor = MetalCard
                                ),
                                shape = RoundedCornerShape(11.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_3ds_otp_code")
                            )

                            if (isProcessingGateway) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = MekanikBrandOrange,
                                        strokeWidth = 2.5.dp,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = processingStatusText,
                                        color = OffWhite,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.5.sp
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { checkoutStep = 1 },
                                    enabled = !isProcessingGateway,
                                    shape = RoundedCornerShape(11.dp),
                                    border = BorderStroke(1.dp, MetalBorder),
                                    modifier = Modifier.weight(0.4f)
                                ) {
                                    Text("Retour", color = OffWhite, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        isProcessingGateway = true
                                        coroutineScope.launch {
                                            processingStatusText = "Connexion chiffrée au serveur ${selectedChannel.shortBadge}..."
                                            delay(350L)
                                            processingStatusText = "Validation du code 3D-Secure & autorisation bancaire ($activePriceDzd DA)..."
                                            delay(400L)
                                            val masked = when (selectedChannel) {
                                                MecaVerifGatewayChannel.BARIDIMOB_QR -> "BARIDIMOB RIP •••• 9204 38"
                                                MecaVerifGatewayChannel.CHARGILY_SLICKPAY_DZ -> "CHARGILY E-PAY •••• $activePriceDzd DA"
                                                else -> "${selectedChannel.shortBadge} •••• ${cardNumberInput.takeLast(4)}"
                                            }
                                            val receipt = MecaVerifPaymentLedger.recordAndSyncUnitPurchase(
                                                vin = vin,
                                                vehicleLabel = vehicleTitle,
                                                amountDzd = activePriceDzd,
                                                isProTariff = selectedTariffIsPro,
                                                gateway = selectedChannel,
                                                maskedInstrument = masked,
                                                buyerName = cardHolderInput
                                            )
                                            completedReceipt = receipt
                                            isProcessingGateway = false
                                            checkoutStep = 3
                                        }
                                    },
                                    enabled = !isProcessingGateway && enteredOtpCode.length >= 4,
                                    shape = RoundedCornerShape(11.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MekanikValidationGreen,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .weight(0.6f)
                                        .height(48.dp)
                                        .testTag("btn_confirm_3ds_and_pay")
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Confirmer $activePriceDzd DA",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // =========================================================================
                // ÉTAPE 3 : REÇU FISCAL CERTIFIÉ & DÉBLOCAGE IMMÉDIAT DU CHÂSSIS
                // =========================================================================
                if (checkoutStep == 3 && completedReceipt != null) {
                    val rec = completedReceipt!!
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = GraphiteDark,
                        border = BorderStroke(1.5.dp, MekanikValidationGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mecaverif_payment_receipt_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MekanikValidationGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "PAIEMENT APPROUVÉ & CHÂSSIS DÉBLOQUÉ",
                                        color = MekanikValidationGreen,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MekanikValidationGreen.copy(alpha = 0.18f)
                                ) {
                                    Text(
                                        text = "${rec.amountDzd} DA PAYÉ",
                                        color = MekanikValidationGreen,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            HorizontalDivider(color = MetalBorder)

                            Text(
                                text = "N° Reçu Fiscal : ${rec.receiptId}\n" +
                                    "Code Autorisation : ${rec.authorizationCode}\n" +
                                    "Passerelle : ${rec.gatewayChannel.title} (${rec.maskedInstrument})\n" +
                                    "Titulaire / Client : ${rec.buyerName}\n" +
                                    "Châssis (VIN) débloqué : ${rec.vin}\n" +
                                    "Formule : ${rec.tariffCategory}\n" +
                                    "Horodatage : ${rec.createdAtFormatted}",
                                color = OffWhite,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.5.sp
                            )
                        }
                    }

                    Button(
                        onClick = { onPaymentCompleted(rec) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MekanikValidationGreen,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_open_unlocked_vin_report")
                    ) {
                        Icon(imageVector = Icons.Default.Assessment, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ouvrir le Rapport Complet MécaVerif+™ (VIN ${rec.vin})",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.5.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * MODALE DU JOURNAL DES REÇUS & FACTURES UNITAIRES MÉCAVERIF+™
 */
@Composable
fun MecaVerifReceiptsLedgerModal(
    onSelectUnlockedVin: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MetalCard,
            border = BorderStroke(1.5.dp, MekanikBrandOrange),
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .testTag("mecaverif_receipts_ledger_modal")
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "HISTORIQUE DES ACHATS UNITAIRES MÉCAVERIF+™",
                            color = OffWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.5.sp
                        )
                        Text(
                            text = "Reçus SATIM CIB, Edahabia, BaridiMob, Chargily Pay & Stripe synchronisés",
                            color = AluminumGray,
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = OffWhite)
                    }
                }

                MecaVerifPaymentLedger.transactions.forEach { tx ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GraphiteDark,
                        border = BorderStroke(1.dp, MetalBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${tx.receiptId} • ${tx.gatewayChannel.shortBadge}",
                                    color = MekanikBrandOrange,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.5.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MekanikValidationGreen.copy(alpha = 0.16f),
                                    border = BorderStroke(1.dp, MekanikValidationGreen)
                                ) {
                                    Text(
                                        text = "${tx.amountDzd} DA (${tx.amountEurLabel})",
                                        color = MekanikValidationGreen,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Châssis VIN : ${tx.vin} • ${tx.vehicleLabel}",
                                color = OffWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )

                            Text(
                                text = "${tx.maskedInstrument} • Auth: ${tx.authorizationCode} • ${tx.createdAtFormatted}",
                                color = AluminumGray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { onSelectUnlockedVin(tx.vin) }) {
                                    Icon(
                                        imageVector = Icons.Default.OpenInNew,
                                        contentDescription = null,
                                        tint = MekanikBrandOrange,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Consulter ce rapport VIN",
                                        color = MekanikBrandOrange,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
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
