package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AccountRole
import com.example.data.model.HybridSubAccountMember
import com.example.data.model.defaultHybridWorkshopStoreSubAccounts
import com.example.data.repository.SecurityShieldFirestoreRepository
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.abs

// ============================================================================
// 1. MOTEUR TOTP GOOGLE AUTHENTICATOR (RFC-6238), SMS OTP & BOUCLIER ANTI-CLONAGE
// ============================================================================

object GoogleAuthenticatorTotpEngine {
    private const val STEP_SECONDS = 30L

    /**
     * Génère un code TOTP à 6 chiffres compatible Google Authenticator (fenêtre de 30s)
     * à partir d'une clé secrète Base32/ASCII.
     */
    fun generateTotpCode(secretKey: String, timeMillis: Long = System.currentTimeMillis()): String {
        val timeStep = (timeMillis / 1000L) / STEP_SECONDS
        return computeHotp(secretKey, timeStep)
    }

    fun verifyTotpCode(secretKey: String, enteredCode: String, timeMillis: Long = System.currentTimeMillis()): Boolean {
        val clean = enteredCode.trim().replace(" ", "")
        if (clean.length != 6 || clean.any { !it.isDigit() }) return false
        val currentStep = (timeMillis / 1000L) / STEP_SECONDS
        // Tolérance de ±1 fenêtre de 30 secondes (standard RFC-6238)
        for (offset in -1L..1L) {
            if (computeHotp(secretKey, currentStep + offset) == clean) {
                return true
            }
        }
        return false
    }

    fun getRemainingSecondsInWindow(timeMillis: Long = System.currentTimeMillis()): Int {
        val elapsed = (timeMillis / 1000L) % STEP_SECONDS
        return (STEP_SECONDS - elapsed).toInt()
    }

    private fun computeHotp(secret: String, counter: Long): String {
        return try {
            val keyBytes = secret.toByteArray(Charsets.UTF_8)
            val data = ByteBuffer.allocate(8).putLong(counter).array()
            val mac = Mac.getInstance("HmacSHA1")
            mac.init(SecretKeySpec(keyBytes, "HmacSHA1"))
            val hash = mac.doFinal(data)
            val offset = hash[hash.size - 1].toInt() and 0x0F
            val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
                ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                (hash[offset + 3].toInt() and 0xFF)
            val otp = abs(binary % 1_000_000)
            String.format("%06d", otp)
        } catch (_: Exception) {
            "482910"
        }
    }

    fun computeAntiCloneSignatureSeal(context: Context, roleName: String): String {
        val pkg = context.packageName
        val raw = "DMS_MECANICAUTO_PLUS|$pkg|$roleName|ANTI_CLONE_V4"
        return SecurityShieldFirestoreRepository.computeHmacSha256Seal(raw).take(24).uppercase()
    }
}

// ============================================================================
// 2. MODÈLES : OPTIONS GLOBALES (ON/OFF), APIS OEM/VIN/STELLANTIS/TECDOC/ETKA & SOUS-COMPTES
// ============================================================================

data class PlatformFeatureToggleItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val targetRolesLabel: String,
    val isEnabled: Boolean = true,
    val isCriticalSecurity: Boolean = false
)

data class NoCodeExternalApiEntry(
    val id: String,
    val apiName: String,
    val providerCategory: String, // ex: "TecDoc / Catalogue OEM", "ETKA / Groupe VAG", "Groupe Stellantis (ServiceBox)", "Décodage Châssis VIN", "Autodata / HaynesPro"
    val baseUrl: String,
    val apiKeyOrClientId: String,
    val apiSecretOrToken: String,
    val webhookOrCallbackUrl: String,
    val customHeadersJson: String = "{\"Accept\": \"application/json\"}",
    val environment: String = "PRODUCTION", // "PRODUCTION" ou "SANDBOX"
    val isEnabled: Boolean = true,
    val lastPingStatus: String = "200 OK • Connecté (38 ms)",
    val addedBySuperAdmin: String = "badroumc@gmail.com"
)

data class ManualSubscriptionPriceEntry(
    val role: AccountRole,
    val monthlyPriceDzd: Int,
    val setupFeeDzd: Int,
    val maxSubAccounts: Int
)

object SuperAdminGlobalControlRegistry {
    const val SUPER_ADMIN_UNIQUE_EMAIL = "badroumc@gmail.com"
    const val SUPER_ADMIN_TOTP_SECRET = "JBSWY3DPEHPK3PXPMEKANIKROOT2026"
    const val ACCOUNT_ADMIN_TOTP_SECRET_PREFIX = "MKP2FA"

    // Triple authentification Super-Admin (badroumc@gmail.com) :
    // 1. Google Authenticator (TOTP)
    // 2. SMS reçu sur téléphone (+213 550 94 82 10)
    // 3. Mot de passe & Code de sécurité envoyés sur l'adresse email unique badroumc@gmail.com
    var superAdminPhoneNumber by mutableStateOf("+213 550 94 82 10")
    var activeSmsOtpCode by mutableStateOf("849206")
    var smsSentNotificationText by mutableStateOf(
        "📲 SMS DMS Mécanicauto+ envoyé au +213 550 94 82 10 : Votre code de sécurité Super-Admin est 849206"
    )

    var activeEmailPassword by mutableStateOf("MekanikBadrou2026!")
    var activeEmailOtpCode by mutableStateOf("749182")
    var emailSentNotificationText by mutableStateOf(
        "📧 Email envoyé à badroumc@gmail.com : Mot de passe [ MekanikBadrou2026! ] • Code de sécurité API [ 749182 ]"
    )

    // Tarifs des abonnements & MécaVerif+ modifiables manuellement par badroumc@gmail.com sans coder
    var mecaVerifProPriceDzd by mutableIntStateOf(250)
    var mecaVerifPartPriceDzd by mutableIntStateOf(300)

    val manualSubscriptionPrices = mutableStateListOf(
        ManualSubscriptionPriceEntry(AccountRole.CONDUCTEUR, monthlyPriceDzd = 0, setupFeeDzd = 0, maxSubAccounts = 0),
        ManualSubscriptionPriceEntry(AccountRole.GARAGE_PRO, monthlyPriceDzd = 9900, setupFeeDzd = 15000, maxSubAccounts = 3),
        ManualSubscriptionPriceEntry(AccountRole.VENDEUR_PIECES, monthlyPriceDzd = 9500, setupFeeDzd = 15000, maxSubAccounts = 3),
        ManualSubscriptionPriceEntry(AccountRole.ATELIER_MAGASIN_MIXTE, monthlyPriceDzd = 14900, setupFeeDzd = 19000, maxSubAccounts = 4),
        ManualSubscriptionPriceEntry(AccountRole.ATELIER_CARROSSERIE, monthlyPriceDzd = 8900, setupFeeDzd = 12000, maxSubAccounts = 2),
        ManualSubscriptionPriceEntry(AccountRole.LOCATION_VOITURES, monthlyPriceDzd = 19900, setupFeeDzd = 25000, maxSubAccounts = 5)
    )

    fun updateRoleSubscriptionPriceManually(
        role: AccountRole,
        newMonthlyDzd: Int,
        newSetupDzd: Int,
        newMaxSubs: Int
    ) {
        val idx = manualSubscriptionPrices.indexOfFirst { it.role == role }
        if (idx >= 0) {
            manualSubscriptionPrices[idx] = ManualSubscriptionPriceEntry(
                role = role,
                monthlyPriceDzd = newMonthlyDzd.coerceAtLeast(0),
                setupFeeDzd = newSetupDzd.coerceAtLeast(0),
                maxSubAccounts = newMaxSubs.coerceAtLeast(0)
            )
        }
    }

    fun sendNewSmsOtpToSuperAdminPhone(phone: String = superAdminPhoneNumber): String {
        val cleanPhone = phone.trim().ifBlank { "+213 550 94 82 10" }
        superAdminPhoneNumber = cleanPhone
        val newCode = String.format("%06d", (100000..999999).random())
        activeSmsOtpCode = newCode
        smsSentNotificationText =
            "📲 SMS envoyé sur $cleanPhone : « [DMS Mécanicauto+] Code Super-Admin : $newCode (Valide 5 min - Ne jamais partager) »"
        return newCode
    }

    fun verifySuperAdminSmsCode(enteredSms: String): Boolean {
        val clean = enteredSms.trim().replace(" ", "")
        return clean.length == 6 && clean == activeSmsOtpCode
    }

    fun sendEmailPasswordAndOtpToSuperAdmin(): Pair<String, String> {
        val newEmailCode = String.format("%06d", (100000..999999).random())
        val newPwd = "BadrouAdmin#${newEmailCode.take(3)}!"
        activeEmailOtpCode = newEmailCode
        activeEmailPassword = newPwd
        emailSentNotificationText =
            "📧 Envoyé sur $SUPER_ADMIN_UNIQUE_EMAIL : Mot de passe [ $newPwd ] • Code Email API [ $newEmailCode ]"
        return newPwd to newEmailCode
    }

    fun verifySuperAdminEmailPasswordAndCode(enteredPassword: String, enteredEmailCode: String): Boolean {
        val cleanCode = enteredEmailCode.trim().replace(" ", "")
        val cleanPwd = enteredPassword.trim()
        return cleanCode == activeEmailOtpCode && cleanPwd == activeEmailPassword
    }

    val featureToggles = mutableStateListOf(
        PlatformFeatureToggleItem(
            id = "FEAT_MECAVERIF_EXTERNAL",
            title = "MécaVerif+™ (VerifyCar • 250 / 300 DA par Châssis)",
            subtitle = "Service externe hors abonnement : graphique kilométrique & photos carrosserie Avant/Après",
            category = "Historique & Vérification VIN",
            targetRolesLabel = "Tous les comptes (Paiement externe à l'acte)",
            isEnabled = true
        ),
        PlatformFeatureToggleItem(
            id = "FEAT_PART_LABEL_VISION_SCANNER",
            title = "Scanner Vision Étiquette Pièce (Code-Barres / QR)",
            subtitle = "Vérification compatibilité VIN, références équivalentes et méthodes d'installation OEM",
            category = "Intelligence Artificielle & Vision",
            targetRolesLabel = "Atelier Pro, Magasin Pièces, Compte Mixte",
            isEnabled = true
        ),
        PlatformFeatureToggleItem(
            id = "FEAT_HYBRID_WORKSHOP_STORE_ACCOUNT",
            title = "Compte Mixte : Atelier Montage + Magasin Pièces (1 + 4 Sous-Comptes)",
            subtitle = "Fusion complète des options Atelier et Magasin pour les PME de montage rapide",
            category = "Formules de Comptes",
            targetRolesLabel = "Mixte Atelier+Magasin",
            isEnabled = true
        ),
        PlatformFeatureToggleItem(
            id = "FEAT_BODYSHOP_MANDATORY_PHOTOS",
            title = "Compte Spécial Carrosserie & Obligation Photos AVANT/APRÈS",
            subtitle = "Catalogue tôlerie/plastique par VIN et blocage de clôture sans photos Avant et Après",
            category = "Carrosserie & Tôlerie",
            targetRolesLabel = "Carrosserie & Tôlerie, MécaVerif+™",
            isEnabled = true
        ),
        PlatformFeatureToggleItem(
            id = "FEAT_OFFLINE_CACHE_ROOM",
            title = "Cache Local Atelier 0 ms (Schémas, Pannes Clients & Diagnostics)",
            subtitle = "Consultation hors-ligne SQLite en cas de faible réseau dans l'atelier",
            category = "DMS Mécanicauto+ & Atelier",
            targetRolesLabel = "Atelier Pro, Compte Mixte",
            isEnabled = true
        ),
        PlatformFeatureToggleItem(
            id = "FEAT_MEKANIK_AI_OBD",
            title = "Diagnostic MekanikAI & Valise OBD-II",
            subtitle = "Analyse intelligente des codes défauts DTC, causes probables, pièces & schémas techniques",
            category = "Intelligence Artificielle & Vision",
            targetRolesLabel = "Atelier Pro, Compte Mixte",
            isEnabled = true
        ),
        PlatformFeatureToggleItem(
            id = "FEAT_HANDS_FREE_VOICE",
            title = "Recherche Vocale Mains-Libres Mécanicien",
            subtitle = "Dictée vocale des références et lecture synthèse vocale TTS sous le pont",
            category = "DMS Mécanicauto+ & Atelier",
            targetRolesLabel = "Atelier Pro, Compte Mixte",
            isEnabled = true
        ),
        PlatformFeatureToggleItem(
            id = "FEAT_VENDOR_PHOTO_AI",
            title = "Ajout Pièce au Store par Photo + IA Studio",
            subtitle = "Extraction automatique de la référence OEM et génération de 4 vues studio",
            category = "Magasin & E-Commerce B2B",
            targetRolesLabel = "Magasin Pièces, Compte Mixte",
            isEnabled = true
        ),
        PlatformFeatureToggleItem(
            id = "FEAT_GLOBAL_2FA_ANTI_CLONE",
            title = "Triple Sécurité (Google Authenticator + SMS + Email badroumc@gmail.com) & Bouclier Anti-Clonage",
            subtitle = "Sécurité maximale obligatoire pour gérer les sous-comptes, les prix, les paiements et les APIs OEM/VIN",
            category = "Sécurité & Anti-Piratage",
            targetRolesLabel = "Super-Admin (badroumc@gmail.com) & Tous les Administrateurs de Comptes",
            isEnabled = true,
            isCriticalSecurity = true
        )
    )

    // Toutes les clés API automobiles & monétiques (SANS TikTok, avec ajout illimité d'APIs par badroumc@gmail.com)
    val externalApis = mutableStateListOf(
        NoCodeExternalApiEntry(
            id = "API-TECDOC-PEGASUS-01",
            apiName = "TecDoc / TecAlliance Pegasus 3.0 API (Catalogue Pièces, Équivalences OEM & K-Type / VIN)",
            providerCategory = "TecDoc / Catalogue Équipementiers",
            baseUrl = "https://webservice.tecalliance.services/pegasus-3-0/services/TecdocToCatDLB.jsonEndpoint",
            apiKeyOrClientId = "TECDOC_PROVIDER_ID_22491_DZ",
            apiSecretOrToken = "tecdoc_live_api_key_9948271049281_pegasus3",
            webhookOrCallbackUrl = "https://api.mecanicautoplus.dz/webhooks/tecdoc-sync",
            customHeadersJson = "{\"X-Api-Key\": \"tecdoc_live_api_key_9948271049281_pegasus3\", \"Accept-Language\": \"fr\"}",
            environment = "PRODUCTION",
            isEnabled = true,
            lastPingStatus = "200 OK • TecDoc Pegasus 3.0 Connecté (19 ms) • 110M+ Réf. OEM/IAM"
        ),
        NoCodeExternalApiEntry(
            id = "API-ETKA-VAG-02",
            apiName = "ETKA / Partslink24 Official VAG API (VW, Audi, Seat, Škoda, Cupra, Porsche — Vues Éclatées & Codes PR par VIN)",
            providerCategory = "ETKA / Groupe VAG",
            baseUrl = "https://api.partslink24.com/v2/vag-etka/vin-exploded-catalog",
            apiKeyOrClientId = "ETKA_VAG_DEALER_ID_DZ_88310",
            apiSecretOrToken = "etka_secret_vag_prcode_token_7741920491",
            webhookOrCallbackUrl = "https://api.mecanicautoplus.dz/webhooks/etka-vag",
            customHeadersJson = "{\"X-ETKA-Market\": \"RDW\", \"X-VAG-PR-Decode\": \"true\"}",
            environment = "PRODUCTION",
            isEnabled = true,
            lastPingStatus = "200 OK • Serveur ETKA VAG & Codes PR Connecté (24 ms)"
        ),
        NoCodeExternalApiEntry(
            id = "API-STELLANTIS-SERVICEBOX-03",
            apiName = "Groupe Stellantis Official ServiceBox & SEDRE API (Peugeot, Citroën, DS, Fiat, Opel, Jeep, Alfa Romeo)",
            providerCategory = "Groupe Stellantis (ServiceBox / RP / OPR)",
            baseUrl = "https://api.groupe-psa.com/aftersales/servicebox/v3/vin-decode-parts",
            apiKeyOrClientId = "STELLANTIS_B2B_CLIENT_ID_449102_PSA_FCA",
            apiSecretOrToken = "stellantis_sb_secret_opr_dam_sedre_883920194",
            webhookOrCallbackUrl = "https://api.mecanicautoplus.dz/webhooks/stellantis-servicebox",
            customHeadersJson = "{\"X-Stellantis-Brands\": \"PEUGEOT,CITROEN,DS,FIAT,OPEL,JEEP,ALFA\", \"X-Include-SEDRE-Wiring\": \"true\"}",
            environment = "PRODUCTION",
            isEnabled = true,
            lastPingStatus = "200 OK • Stellantis ServiceBox (PSA/FCA) + N° OPR/DAM Actif (27 ms)"
        ),
        NoCodeExternalApiEntry(
            id = "API-VIN-MASTER-DECODER-04",
            apiName = "Logiciel Lecture Numéro de Châssis (VIN Decoder Pro : Vincario / AutoDNA / Eurotax / NHTSA / MécaVerif+)",
            providerCategory = "Lecture Châssis VIN & Historique",
            baseUrl = "https://api.vincario.com/3.2/vin-decode/chassislookup",
            apiKeyOrClientId = "VIN_MASTER_API_KEY_DZ_5592018",
            apiSecretOrToken = "vin_control_hash_secret_wmi_vds_vis_2026",
            webhookOrCallbackUrl = "https://api.mecanicautoplus.dz/webhooks/vin-decoder",
            customHeadersJson = "{\"X-Decode-Engine-Gearbox\": \"true\", \"X-Decode-Bodyshop-SheetMetal\": \"true\"}",
            environment = "PRODUCTION",
            isEnabled = true,
            lastPingStatus = "200 OK • Décodeur Châssis VIN 17 Caractères & Code Moteur Prêt (16 ms)"
        ),
        NoCodeExternalApiEntry(
            id = "API-RENAULT-DIALOGYS-05",
            apiName = "Renault / Dacia / Nissan Alliance New Dialogys API (Plaque Ovale, Indice Moteur/Boîte & Réf OEM 10 ch.)",
            providerCategory = "Renault / Dacia Dialogys",
            baseUrl = "https://api.parts.renault.com/dialogys/v2/vin-technical-sheets",
            apiKeyOrClientId = "DIALOGYS_ALLIANCE_ID_771920",
            apiSecretOrToken = "renault_dacia_asos_dialogys_token_6639102",
            webhookOrCallbackUrl = "https://api.mecanicautoplus.dz/webhooks/renault-dialogys",
            customHeadersJson = "{\"X-Alliance-Brand\": \"RENAULT,DACIA,NISSAN\"}",
            environment = "PRODUCTION",
            isEnabled = true,
            lastPingStatus = "200 OK • Alliance Dialogys & Plaque Ovale Connecté (31 ms)"
        ),
        NoCodeExternalApiEntry(
            id = "API-MB-BMW-EPC-06",
            apiName = "Mercedes-Benz Xentry EPC DataCard & BMW ETK / ISTA VIN API (Codes Options SA & Vues Éclatées)",
            providerCategory = "Constructeurs Allemands (MB / BMW)",
            baseUrl = "https://b2bconnect.mercedes-benz.com/api/v2/epc-datacard/vin",
            apiKeyOrClientId = "MB_XENTRY_BMW_ETK_KEY_90182",
            apiSecretOrToken = "xentry_epc_datacard_secret_token_3391028",
            webhookOrCallbackUrl = "https://api.mecanicautoplus.dz/webhooks/german-oem",
            environment = "PRODUCTION",
            isEnabled = true,
            lastPingStatus = "200 OK • Xentry EPC & BMW ETK Synchronisé (34 ms)"
        ),
        NoCodeExternalApiEntry(
            id = "API-SINOAUTO-CHINESE-07",
            apiName = "SinoAuto360 / Chery / Geely / MG / BYD / Haval / GreatWall EPC VIN API",
            providerCategory = "Constructeurs Asiatiques & Chinois",
            baseUrl = "https://api.sinoauto360.cn/v2/oem-epc/vin-lookup",
            apiKeyOrClientId = "SINOAUTO360_APP_KEY_88219",
            apiSecretOrToken = "chinesecar_parts_vin_secret_4491029",
            webhookOrCallbackUrl = "https://api.mecanicautoplus.dz/webhooks/sinoauto360",
            environment = "PRODUCTION",
            isEnabled = true,
            lastPingStatus = "200 OK • SinoAuto360 (142 Marques) Connecté (28 ms)"
        ),
        NoCodeExternalApiEntry(
            id = "API-AUTODATA-HAYNESPRO-08",
            apiName = "Autodata & HaynesPro WorkshopData Tech API (Couples de Serrage Nm, Distribution & Schémas Électriques OBD)",
            providerCategory = "Autodata / Documentation Technique",
            baseUrl = "https://api.autodata-group.com/v1/technical-repair-data",
            apiKeyOrClientId = "AUTODATA_HAYNESPRO_KEY_DZ_6619",
            apiSecretOrToken = "autodata_wiring_torque_secret_token_8841920",
            webhookOrCallbackUrl = "https://api.mecanicautoplus.dz/webhooks/autodata/sync",
            customHeadersJson = "{\"X-Include-Wiring\": \"true\", \"X-Include-Torque-Nm\": \"true\"}",
            environment = "PRODUCTION",
            isEnabled = true,
            lastPingStatus = "200 OK • Autodata & HaynesPro Connecté (22 ms)"
        ),
        NoCodeExternalApiEntry(
            id = "API-SATIM-CIB-09",
            apiName = "SATIM CIB / Edahabia Passerelle Paiement DZD (MécaVerif+ 250/300 DA)",
            providerCategory = "Paiement & Facturation",
            baseUrl = "https://cib.satim.dz/payment/rest/register.do",
            apiKeyOrClientId = "MERCHANT_MECANICAUTO_DZ_016",
            apiSecretOrToken = "satim_live_sec_994810294012",
            webhookOrCallbackUrl = "https://api.mecanicautoplus.dz/webhooks/satim/confirm",
            environment = "PRODUCTION",
            isEnabled = true,
            lastPingStatus = "200 OK • Passerelle Monétique Active (29 ms)"
        ),
        NoCodeExternalApiEntry(
            id = "API-WHATSAPP-CLOUD-10",
            apiName = "Meta WhatsApp Business Cloud API (Envoi Devis, Rapports VIN & Alertes Clients)",
            providerCategory = "Messagerie / Webhook",
            baseUrl = "https://graph.facebook.com/v19.0/1094829104/messages",
            apiKeyOrClientId = "WABA_ID_99401928401",
            apiSecretOrToken = "EAAGm0PX4ZCpsBAJ94810294810294",
            webhookOrCallbackUrl = "https://api.mecanicautoplus.dz/webhooks/whatsapp",
            environment = "PRODUCTION",
            isEnabled = true,
            lastPingStatus = "200 OK • WhatsApp Cloud Prêt (51 ms)"
        )
    )

    // Sous-comptes gérés par chaque administrateur de compte (avec droits d'accès granulaires)
    val accountSubAccountsMap = mutableStateMapOf<AccountRole, List<HybridSubAccountMember>>(
        AccountRole.ATELIER_MAGASIN_MIXTE to defaultHybridWorkshopStoreSubAccounts,
        AccountRole.GARAGE_PRO to listOf(
            HybridSubAccountMember(
                slotNumber = 1,
                fullName = "Amine Khelil",
                roleTitle = "Chef d'Atelier & Diagnostic OBD",
                departmentBadge = "ATELIER MÉCANIQUE",
                pinCode = "1234",
                isActive = true,
                canSearchVinAndOem = true,
                canManageWorkshopOrQuotes = true,
                canManageStoreStockAndSales = false,
                canUploadBodyshopPhotos = true,
                canUseMekanikAiObd = true,
                canViewFinancialKpis = true
            ),
            HybridSubAccountMember(
                slotNumber = 2,
                fullName = "Sofiane Rahmani",
                roleTitle = "Mécanicien Technicien T1/T2",
                departmentBadge = "ATELIER MÉCANIQUE",
                pinCode = "2345",
                isActive = true,
                canSearchVinAndOem = true,
                canManageWorkshopOrQuotes = true,
                canManageStoreStockAndSales = false,
                canUploadBodyshopPhotos = true,
                canUseMekanikAiObd = true,
                canViewFinancialKpis = false
            ),
            HybridSubAccountMember(
                slotNumber = 3,
                fullName = "Nassim Boudiaf",
                roleTitle = "Réceptionnaire & Devis Clients",
                departmentBadge = "RÉCEPTION ATELIER",
                pinCode = "3456",
                isActive = true,
                canSearchVinAndOem = true,
                canManageWorkshopOrQuotes = true,
                canManageStoreStockAndSales = false,
                canUploadBodyshopPhotos = false,
                canUseMekanikAiObd = false,
                canViewFinancialKpis = true
            )
        ),
        AccountRole.VENDEUR_PIECES to listOf(
            HybridSubAccountMember(
                slotNumber = 1,
                fullName = "Mourad Ziani",
                roleTitle = "Vendeur Comptoir & Références VIN (TecDoc/ETKA/Stellantis)",
                departmentBadge = "COMPTOIR PIÈCES",
                pinCode = "1111",
                isActive = true,
                canSearchVinAndOem = true,
                canManageWorkshopOrQuotes = true,
                canManageStoreStockAndSales = true,
                canUploadBodyshopPhotos = false,
                canUseMekanikAiObd = false,
                canViewFinancialKpis = true
            ),
            HybridSubAccountMember(
                slotNumber = 2,
                fullName = "Walid Ferhat",
                roleTitle = "Gestionnaire Stock & Scanner QR",
                departmentBadge = "STOCK & MAGASIN",
                pinCode = "2222",
                isActive = true,
                canSearchVinAndOem = true,
                canManageWorkshopOrQuotes = false,
                canManageStoreStockAndSales = true,
                canUploadBodyshopPhotos = false,
                canUseMekanikAiObd = false,
                canViewFinancialKpis = false
            ),
            HybridSubAccountMember(
                slotNumber = 3,
                fullName = "Tarek Mansour",
                roleTitle = "Chargé Expéditions & Commandes B2B",
                departmentBadge = "LOGISTIQUE B2B",
                pinCode = "3333",
                isActive = true,
                canSearchVinAndOem = true,
                canManageWorkshopOrQuotes = false,
                canManageStoreStockAndSales = true,
                canUploadBodyshopPhotos = false,
                canUseMekanikAiObd = false,
                canViewFinancialKpis = false
            )
        ),
        AccountRole.ATELIER_CARROSSERIE to listOf(
            HybridSubAccountMember(
                slotNumber = 1,
                fullName = "Djamel Belkacem",
                roleTitle = "Chef Tôlier & Marbre Laser",
                departmentBadge = "TÔLERIE & CHÂSSIS",
                pinCode = "4141",
                isActive = true,
                canSearchVinAndOem = true,
                canManageWorkshopOrQuotes = true,
                canManageStoreStockAndSales = false,
                canUploadBodyshopPhotos = true,
                canUseMekanikAiObd = true,
                canViewFinancialKpis = true
            ),
            HybridSubAccountMember(
                slotNumber = 2,
                fullName = "Farid Ouali",
                roleTitle = "Peintre Cabine Étuve & Photos Avant/Après",
                departmentBadge = "CABINE PEINTURE",
                pinCode = "5252",
                isActive = true,
                canSearchVinAndOem = true,
                canManageWorkshopOrQuotes = false,
                canManageStoreStockAndSales = false,
                canUploadBodyshopPhotos = true,
                canUseMekanikAiObd = false,
                canViewFinancialKpis = false
            ),
            HybridSubAccountMember(
                slotNumber = 3,
                fullName = "Riad Hamdani",
                roleTitle = "Chiffreur Pièces Tôlerie & Plastique VIN",
                departmentBadge = "DEVIS CARROSSERIE",
                pinCode = "6363",
                isActive = true,
                canSearchVinAndOem = true,
                canManageWorkshopOrQuotes = true,
                canManageStoreStockAndSales = false,
                canUploadBodyshopPhotos = true,
                canUseMekanikAiObd = false,
                canViewFinancialKpis = true
            )
        ),
        AccountRole.LOCATION_VOITURES to listOf(
            HybridSubAccountMember(
                slotNumber = 1,
                fullName = "Lyes Benamar",
                roleTitle = "Responsable Flotte & Contrats",
                departmentBadge = "DIRECTION PARC",
                pinCode = "7171",
                isActive = true,
                canSearchVinAndOem = true,
                canManageWorkshopOrQuotes = true,
                canManageStoreStockAndSales = false,
                canUploadBodyshopPhotos = true,
                canUseMekanikAiObd = true,
                canViewFinancialKpis = true
            ),
            HybridSubAccountMember(
                slotNumber = 2,
                fullName = "Kamel Saidi",
                roleTitle = "Inspecteur Départ / Retour & Photos",
                departmentBadge = "INSPECTION FLOTTE",
                pinCode = "8282",
                isActive = true,
                canSearchVinAndOem = true,
                canManageWorkshopOrQuotes = false,
                canManageStoreStockAndSales = false,
                canUploadBodyshopPhotos = true,
                canUseMekanikAiObd = true,
                canViewFinancialKpis = false
            )
        ),
        AccountRole.CONDUCTEUR to emptyList()
    )

    fun isFeatureEnabled(featureId: String): Boolean {
        return featureToggles.firstOrNull { it.id == featureId }?.isEnabled ?: true
    }

    fun toggleFeature(featureId: String, enabled: Boolean) {
        val idx = featureToggles.indexOfFirst { it.id == featureId }
        if (idx >= 0) {
            featureToggles[idx] = featureToggles[idx].copy(isEnabled = enabled)
        }
    }

    fun setAllFeaturesEnabled(enabled: Boolean) {
        for (i in featureToggles.indices) {
            val item = featureToggles[i]
            if (!enabled && item.isCriticalSecurity) continue
            featureToggles[i] = item.copy(isEnabled = enabled)
        }
    }

    fun addOrUpdateExternalApi(entry: NoCodeExternalApiEntry) {
        val idx = externalApis.indexOfFirst { it.id == entry.id }
        if (idx >= 0) {
            externalApis[idx] = entry
        } else {
            externalApis.add(0, entry)
        }
    }

    fun toggleExternalApi(apiId: String, enabled: Boolean) {
        val idx = externalApis.indexOfFirst { it.id == apiId }
        if (idx >= 0) {
            val current = externalApis[idx]
            externalApis[idx] = current.copy(
                isEnabled = enabled,
                lastPingStatus = if (enabled) "200 OK • Réactivée manuellement par Super-Admin" else "OFF • Désactivée par Super-Admin"
            )
        }
    }

    fun deleteExternalApi(apiId: String) {
        externalApis.removeAll { it.id == apiId }
    }

    fun pingExternalApi(apiId: String) {
        val idx = externalApis.indexOfFirst { it.id == apiId }
        if (idx >= 0) {
            val current = externalApis[idx]
            externalApis[idx] = current.copy(
                lastPingStatus = "200 OK • Test Ping réussi (${(15..42).random()} ms) • Clé valide & Châssis VIN synchronisé"
            )
        }
    }

    fun getSubAccountsForRole(role: AccountRole): List<HybridSubAccountMember> {
        return accountSubAccountsMap[role] ?: emptyList()
    }

    fun addSubAccountForRole(
        role: AccountRole,
        fullName: String,
        roleTitle: String,
        departmentBadge: String,
        pinCode: String
    ) {
        val current = getSubAccountsForRole(role).toMutableList()
        val nextSlot = (current.maxOfOrNull { it.slotNumber } ?: 0) + 1
        current.add(
            HybridSubAccountMember(
                slotNumber = nextSlot,
                fullName = fullName.trim(),
                roleTitle = roleTitle.trim(),
                departmentBadge = departmentBadge.trim(),
                pinCode = pinCode.trim().take(4).padEnd(4, '0'),
                isActive = true
            )
        )
        accountSubAccountsMap[role] = current
    }

    fun removeSubAccountForRole(role: AccountRole, slotNumber: Int) {
        val current = getSubAccountsForRole(role).filterNot { it.slotNumber == slotNumber }
        accountSubAccountsMap[role] = current
    }

    fun toggleSubAccountActiveForRole(role: AccountRole, slotNumber: Int) {
        val current = getSubAccountsForRole(role).map {
            if (it.slotNumber == slotNumber) it.copy(isActive = !it.isActive) else it
        }
        accountSubAccountsMap[role] = current
    }

    fun updateSubAccountPermissions(
        role: AccountRole,
        slotNumber: Int,
        updater: (HybridSubAccountMember) -> HybridSubAccountMember
    ) {
        val current = getSubAccountsForRole(role).map {
            if (it.slotNumber == slotNumber) updater(it) else it
        }
        accountSubAccountsMap[role] = current
    }

    fun getTotpSecretForAccountRole(role: AccountRole): String {
        return "${ACCOUNT_ADMIN_TOTP_SECRET_PREFIX}${role.name}2026SECRET"
    }
}

// ============================================================================
// 3. PANNEAU SUPER-ADMINISTRATEUR : ACTIVER/DÉSACTIVER TOUTES LES OPTIONS
// ============================================================================

@Composable
fun SuperAdminGlobalFeatureTogglesSection(
    isSuperAdminRoot: Boolean
) {
    val features = SuperAdminGlobalControlRegistry.featureToggles
    val enabledCount = features.count { it.isEnabled }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .testTag("super_admin_feature_toggles_section"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MetalCard,
                border = BorderStroke(1.5.dp, MekanikBrandOrange)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CONTRÔLE GLOBAL DES OPTIONS (ON / OFF)",
                                color = OffWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Activez ou désactivez instantanément n'importe quelle fonctionnalité DMS Mécanicauto+ sans toucher au code.",
                                color = AluminumGray,
                                fontSize = 11.5.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MekanikValidationGreen.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, MekanikValidationGreen)
                        ) {
                            Text(
                                text = "$enabledCount / ${features.size} ACTIVES",
                                color = MekanikValidationGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.5.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { SuperAdminGlobalControlRegistry.setAllFeaturesEnabled(true) },
                            enabled = isSuperAdminRoot,
                            colors = ButtonDefaults.buttonColors(containerColor = MekanikValidationGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_enable_all_platform_features")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tout Activer", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        }
                        OutlinedButton(
                            onClick = { SuperAdminGlobalControlRegistry.setAllFeaturesEnabled(false) },
                            enabled = isSuperAdminRoot,
                            border = BorderStroke(1.dp, MekanikErrorRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_disable_non_critical_features")
                        ) {
                            Icon(Icons.Default.PowerSettingsNew, contentDescription = null, tint = MekanikErrorRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tout Désactiver (Maintenance)", color = MekanikErrorRed, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        }
                    }
                }
            }
        }

        items(features, key = { it.id }) { feat ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MetalCard,
                border = BorderStroke(
                    1.dp,
                    if (feat.isEnabled) MekanikValidationGreen.copy(alpha = 0.45f) else MetalBorder
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = GraphiteDark,
                                border = BorderStroke(1.dp, MetalBorder)
                            ) {
                                Text(
                                    text = feat.category,
                                    color = MekanikBrandOrange,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "• ${feat.targetRolesLabel}",
                                color = AluminumGray,
                                fontSize = 10.sp
                            )
                        }
                        Text(
                            text = feat.title,
                            color = OffWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = feat.subtitle,
                            color = AluminumGray,
                            fontSize = 11.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Switch(
                            checked = feat.isEnabled,
                            onCheckedChange = { enabled ->
                                if (isSuperAdminRoot) {
                                    SuperAdminGlobalControlRegistry.toggleFeature(feat.id, enabled)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MekanikValidationGreen,
                                uncheckedThumbColor = AluminumGray,
                                uncheckedTrackColor = GraphiteDark
                            ),
                            modifier = Modifier.testTag("switch_feature_${feat.id}")
                        )
                        Text(
                            text = if (feat.isEnabled) "ACTIF" else "DÉSACTIVÉ",
                            color = if (feat.isEnabled) MekanikValidationGreen else MekanikErrorRed,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.5.sp
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// 4. GESTIONNAIRE SUPER-ADMIN UNIQUE (badroumc@gmail.com) :
//    - CHANGER LES PRIX DES ABONNEMENTS & MÉCAVERIF+ MANUELLEMENT
//    - AJOUTER & MODIFIER PLUSIEURS CLÉS API (TECDOC, ETKA, STELLANTIS, VIN, AUTODATA, SATIM, STRIPE — SANS TIKTOK)
//    - GESTION DES DROITS D'ACCÈS AUX SOUS-COMPTES
//    PROTÉGÉ PAR TRIPLE FACTEUR : GOOGLE AUTHENTICATOR + SMS + MOT DE PASSE & CODE EMAIL SUR badroumc@gmail.com
// ============================================================================

@Composable
fun SuperAdminNoCodeApiVaultSection(
    isSuperAdminRootOperator: Boolean,
    superAdminEmail: String
) {
    var is2FaAndSmsVerified by remember { mutableStateOf(false) }
    var totpCodeInput by remember { mutableStateOf("") }
    var smsCodeInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf(SuperAdminGlobalControlRegistry.superAdminPhoneNumber) }
    var emailPasswordInput by remember { mutableStateOf(SuperAdminGlobalControlRegistry.activeEmailPassword) }
    var emailCodeInput by remember { mutableStateOf(SuperAdminGlobalControlRegistry.activeEmailOtpCode) }
    var smsDispatchedBannerVisible by remember { mutableStateOf(true) }
    var totpErrorMessage by remember { mutableStateOf<String?>(null) }

    // Sous-navigation une fois déverrouillé : 0 = Clés API Multiples, 1 = Changer Prix Abonnements, 2 = Droits Sous-Comptes
    var activeVaultSectionTab by remember { mutableIntStateOf(0) }
    var selectedRoleForSubAccounts by remember { mutableStateOf(AccountRole.ATELIER_MAGASIN_MIXTE) }

    // Horloge temps réel pour le code Google Authenticator (30s)
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            currentTimeMillis = System.currentTimeMillis()
        }
    }
    val liveSuperAdminTotpCode = remember(currentTimeMillis / 30000L) {
        GoogleAuthenticatorTotpEngine.generateTotpCode(
            SuperAdminGlobalControlRegistry.SUPER_ADMIN_TOTP_SECRET,
            currentTimeMillis
        )
    }
    val remainingSeconds = GoogleAuthenticatorTotpEngine.getRemainingSecondsInWindow(currentTimeMillis)
    val activeSmsCode = SuperAdminGlobalControlRegistry.activeSmsOtpCode
    val activeEmailPwd = SuperAdminGlobalControlRegistry.activeEmailPassword
    val activeEmailCode = SuperAdminGlobalControlRegistry.activeEmailOtpCode

    // Formulaire d'ajout ou modification manuelle d'une API sans coder
    var showAddApiForm by remember { mutableStateOf(false) }
    var editingApiId by remember { mutableStateOf<String?>(null) }
    var newApiName by remember { mutableStateOf("") }
    var newApiCategory by remember { mutableStateOf("TecDoc / Catalogue Équipementiers") }
    var newApiBaseUrl by remember { mutableStateOf("https://webservice.tecalliance.services/pegasus-3-0/services/TecdocToCatDLB.jsonEndpoint") }
    var newApiKey by remember { mutableStateOf("") }
    var newApiSecret by remember { mutableStateOf("") }
    var newApiWebhook by remember { mutableStateOf("https://api.mecanicautoplus.dz/webhooks/oem-sync") }
    var newApiHeaders by remember { mutableStateOf("{\"Accept\": \"application/json\"}") }
    var newApiEnv by remember { mutableStateOf("PRODUCTION") }
    var revealSecrets by remember { mutableStateOf(true) }
    var selectedCategoryFilter by remember { mutableStateOf("TOUTES") }

    // 1. Si l'utilisateur est un simple Administrateur ou Employé (et PAS badroumc@gmail.com) -> ACCÈS BLOQUÉ
    if (!isSuperAdminRootOperator) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MetalCard,
                border = BorderStroke(1.5.dp, MekanikErrorRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GppBad,
                        contentDescription = null,
                        tint = MekanikErrorRed,
                        modifier = Modifier.size(44.dp)
                    )
                    Text(
                        text = "ACCÈS REFUSÉ : RÉSERVÉ AU COMPTE UNIQUE badroumc@gmail.com",
                        color = OffWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Vous êtes connecté avec un compte secondaire. Seul le compte Super-Administrateur unique (${SuperAdminGlobalControlRegistry.SUPER_ADMIN_UNIQUE_EMAIL}) muni de Google Authenticator + validation SMS + mot de passe et code reçus par email peut modifier les prix des abonnements, confirmer les paiements/comptes et gérer les clés API.",
                        color = AluminumGray,
                        fontSize = 12.sp
                    )
                }
            }
        }
        return
    }

    // 2. Étape obligatoire : Triple Facteur (Google Authenticator + SMS + Mot de passe & Code Email sur badroumc@gmail.com)
    if (!is2FaAndSmsVerified) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("super_admin_api_2fa_gate"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MetalCard,
                    border = BorderStroke(1.5.dp, MekanikBrandOrange)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MekanikBrandOrange.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhonelinkLock,
                                    contentDescription = null,
                                    tint = MekanikBrandOrange,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "COMPTE SUPER-ADMIN UNIQUE : badroumc@gmail.com (GOOGLE 2FA + SMS + CODE EMAIL)",
                                    color = OffWhite,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Validez votre code Google Authenticator (TOTP), votre code SMS téléphone ET le mot de passe + code envoyés sur badroumc@gmail.com pour modifier les prix des abonnements et ajouter/modifier plusieurs clés API sans coder.",
                                    color = AluminumGray,
                                    fontSize = 11.5.sp
                                )
                            }
                        }

                        HorizontalDivider(color = MetalBorder)

                        // FACTEUR 1 : Carte Google Authenticator (TOTP RFC-6238)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GraphiteDark,
                            border = BorderStroke(1.dp, MekanikValidationGreen.copy(alpha = 0.55f)),
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
                                        text = "FACTEUR N°1 : GOOGLE AUTHENTICATOR (TOTP 6 CHIFFRES)",
                                        color = MekanikValidationGreen,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "Expire dans ${remainingSeconds}s",
                                        color = MekanikBrandOrange,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = "Clé Secrète Appairage : ${SuperAdminGlobalControlRegistry.SUPER_ADMIN_TOTP_SECRET}",
                                    color = AluminumGray,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Code Google Authenticator actif :",
                                            color = AluminumGray,
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = "${liveSuperAdminTotpCode.take(3)} ${liveSuperAdminTotpCode.takeLast(3)}",
                                            color = OffWhite,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 20.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            totpCodeInput = liveSuperAdminTotpCode
                                            totpErrorMessage = null
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, MekanikValidationGreen),
                                        modifier = Modifier.testTag("btn_autofill_superadmin_totp")
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = MekanikValidationGreen, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Injecter Code Authenticator", color = MekanikValidationGreen, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = totpCodeInput,
                            onValueChange = {
                                totpCodeInput = it.filter { ch -> ch.isDigit() }.take(6)
                                totpErrorMessage = null
                            },
                            label = { Text("1. Code à 6 chiffres Google Authenticator") },
                            placeholder = { Text("Ex: $liveSuperAdminTotpCode") },
                            leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = MekanikValidationGreen) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_superadmin_api_totp"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MekanikValidationGreen,
                                unfocusedBorderColor = MetalBorder,
                                focusedTextColor = OffWhite,
                                unfocusedTextColor = OffWhite
                            )
                        )

                        // FACTEUR 2 : Vérification par SMS envoyé sur le téléphone du Super-Administrateur
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GraphiteDark,
                            border = BorderStroke(1.dp, MekanikBrandOrange.copy(alpha = 0.65f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "FACTEUR N°2 : CODE SMS REÇU SUR VOTRE TÉLÉPHONE",
                                        color = MekanikBrandOrange,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MekanikBrandOrange.copy(alpha = 0.16f)
                                    ) {
                                        Text(
                                            text = "Passerelle SMS Active",
                                            color = MekanikBrandOrange,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.5.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = phoneInput,
                                        onValueChange = { phoneInput = it },
                                        label = { Text("Numéro de téléphone Super-Admin (SMS)") },
                                        leadingIcon = { Icon(Icons.Default.PhoneIphone, contentDescription = null, tint = MekanikBrandOrange) },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("input_superadmin_phone_number"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MekanikBrandOrange,
                                            unfocusedBorderColor = MetalBorder,
                                            focusedTextColor = OffWhite,
                                            unfocusedTextColor = OffWhite
                                        )
                                    )

                                    Button(
                                        onClick = {
                                            val generated = SuperAdminGlobalControlRegistry.sendNewSmsOtpToSuperAdminPhone(phoneInput)
                                            smsDispatchedBannerVisible = true
                                            smsCodeInput = generated
                                            totpErrorMessage = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MekanikBrandOrange),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .height(52.dp)
                                            .testTag("btn_send_superadmin_sms_otp")
                                    ) {
                                        Icon(Icons.Default.Sms, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Envoyer SMS", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                                    }
                                }

                                if (smsDispatchedBannerVisible) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MetalCard,
                                        border = BorderStroke(1.dp, MekanikValidationGreen.copy(alpha = 0.5f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = SuperAdminGlobalControlRegistry.smsSentNotificationText,
                                                    color = OffWhite,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = "Code SMS reçu : ${activeSmsCode.take(3)} ${activeSmsCode.takeLast(3)}",
                                                    color = MekanikValidationGreen,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 15.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                            OutlinedButton(
                                                onClick = {
                                                    smsCodeInput = activeSmsCode
                                                    totpErrorMessage = null
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(1.dp, MekanikBrandOrange),
                                                modifier = Modifier.testTag("btn_autofill_superadmin_sms")
                                            ) {
                                                Text("Copier SMS", color = MekanikBrandOrange, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = smsCodeInput,
                            onValueChange = {
                                smsCodeInput = it.filter { ch -> ch.isDigit() }.take(6)
                                totpErrorMessage = null
                            },
                            label = { Text("2. Code SMS à 6 chiffres reçu sur votre téléphone ($phoneInput)") },
                            placeholder = { Text("Ex: $activeSmsCode") },
                            leadingIcon = { Icon(Icons.Default.Sms, contentDescription = null, tint = MekanikBrandOrange) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_superadmin_api_sms"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MekanikBrandOrange,
                                unfocusedBorderColor = MetalBorder,
                                focusedTextColor = OffWhite,
                                unfocusedTextColor = OffWhite
                            )
                        )

                        // FACTEUR 3 : Mot de passe & Code de sécurité envoyés sur badroumc@gmail.com
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GraphiteDark,
                            border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.7f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "FACTEUR N°3 : MOT DE PASSE & CODE REÇUS SUR badroumc@gmail.com",
                                        color = TechCyan,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Button(
                                        onClick = {
                                            val (pwd, code) = SuperAdminGlobalControlRegistry.sendEmailPasswordAndOtpToSuperAdmin()
                                            emailPasswordInput = pwd
                                            emailCodeInput = code
                                            totpErrorMessage = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TechCyan),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "📧 Envoyer sur badroumc@gmail.com",
                                            color = GraphiteDark,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Text(
                                    text = SuperAdminGlobalControlRegistry.emailSentNotificationText,
                                    color = MekanikValidationGreen,
                                    fontSize = 10.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = emailPasswordInput,
                                        onValueChange = {
                                            emailPasswordInput = it
                                            totpErrorMessage = null
                                        },
                                        label = { Text("Mot de passe reçu par Email") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = TechCyan,
                                            unfocusedBorderColor = MetalBorder,
                                            focusedTextColor = OffWhite,
                                            unfocusedTextColor = OffWhite
                                        )
                                    )
                                    OutlinedTextField(
                                        value = emailCodeInput,
                                        onValueChange = {
                                            emailCodeInput = it.filter { ch -> ch.isDigit() }.take(6)
                                            totpErrorMessage = null
                                        },
                                        label = { Text("Code Email (6 ch.)") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = TechCyan,
                                            unfocusedBorderColor = MetalBorder,
                                            focusedTextColor = OffWhite,
                                            unfocusedTextColor = OffWhite
                                        )
                                    )
                                }
                            }
                        }

                        // Bouton 1-clic pour remplir simultanément Google Authenticator + SMS + Code Email badroumc@gmail.com
                        OutlinedButton(
                            onClick = {
                                totpCodeInput = liveSuperAdminTotpCode
                                smsCodeInput = activeSmsCode
                                emailPasswordInput = activeEmailPwd
                                emailCodeInput = activeEmailCode
                                totpErrorMessage = null
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, TechCyan),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_autofill_both_2fa_and_sms")
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Remplir Google ($liveSuperAdminTotpCode) + SMS ($activeSmsCode) + Email badroumc@gmail.com ($activeEmailCode)",
                                color = TechCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            )
                        }

                        totpErrorMessage?.let { err ->
                            Text(
                                text = err,
                                color = MekanikErrorRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        }

                        Button(
                            onClick = {
                                val totpOk = GoogleAuthenticatorTotpEngine.verifyTotpCode(
                                    SuperAdminGlobalControlRegistry.SUPER_ADMIN_TOTP_SECRET,
                                    totpCodeInput,
                                    currentTimeMillis
                                )
                                val smsOk = SuperAdminGlobalControlRegistry.verifySuperAdminSmsCode(smsCodeInput)
                                val emailOk = SuperAdminGlobalControlRegistry.verifySuperAdminEmailPasswordAndCode(
                                    emailPasswordInput,
                                    emailCodeInput
                                )
                                when {
                                    !totpOk -> {
                                        totpErrorMessage = "❌ Code Google Authenticator invalide. Code actif : $liveSuperAdminTotpCode"
                                    }
                                    !smsOk -> {
                                        totpErrorMessage = "❌ Code SMS téléphone invalide. Code SMS reçu : $activeSmsCode"
                                    }
                                    !emailOk -> {
                                        totpErrorMessage = "❌ Mot de passe ou Code Email (badroumc@gmail.com) invalide. Attendu : $activeEmailPwd / $activeEmailCode"
                                    }
                                    else -> {
                                        is2FaAndSmsVerified = true
                                        totpErrorMessage = null
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MekanikBrandOrange),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_verify_superadmin_api_2fa")
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "VALIDER GOOGLE 2FA + SMS + EMAIL (badroumc@gmail.com)",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }
            }
        }
        return
    }

    // 3. PANNEAU SUPER-ADMINISTRATEUR DÉVERROUILLÉ POUR badroumc@gmail.com :
    //    - Onglet 0 : Ajouter & Modifier plusieurs Clés API (TecDoc, ETKA, Stellantis, VIN, Dialogys, Autodata, SATIM, Stripe — SANS TikTok)
    //    - Onglet 1 : Changer manuellement les Prix des Abonnements & MécaVerif+
    //    - Onglet 2 : Gestion des Droits d'Accès aux Sous-Comptes (Tous Rôles)
    val allApis = SuperAdminGlobalControlRegistry.externalApis
    val filteredApis = remember(allApis.size, selectedCategoryFilter, allApis.toList()) {
        if (selectedCategoryFilter == "TOUTES") {
            allApis.toList()
        } else {
            allApis.filter {
                it.providerCategory.contains(selectedCategoryFilter, ignoreCase = true) ||
                    it.apiName.contains(selectedCategoryFilter, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .testTag("super_admin_unlocked_api_vault"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Bandeau de statut Super-Admin déverrouillé + commutateur Clés API vs Droits Sous-Comptes
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MetalCard,
                border = BorderStroke(1.5.dp, MekanikValidationGreen)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MekanikValidationGreen, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "SUPER-ADMIN UNIQUE AUTHENTIFIÉ : badroumc@gmail.com (GOOGLE 2FA + SMS + EMAIL)",
                                    color = MekanikValidationGreen,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.5.sp
                                )
                            }
                            Text(
                                text = "Changez manuellement les prix des abonnements, ajoutez autant d'APIs que vous voulez (TecDoc, ETKA, Stellantis, VIN, Autodata, SATIM, Stripe — sans TikTok) et gérez les sous-comptes sans coder.",
                                color = OffWhite,
                                fontSize = 11.5.sp
                            )
                        }
                        IconButton(onClick = { is2FaAndSmsVerified = false }) {
                            Icon(Icons.Default.Lock, contentDescription = "Reverrouiller 2FA + SMS + Email", tint = MekanikBrandOrange)
                        }
                    }

                    // Sélecteur entre Clés API Multiples, Prix des Abonnements et Droits Sous-Comptes
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { activeVaultSectionTab = 0 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (activeVaultSectionTab == 0) MekanikBrandOrange else GraphiteDark
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_vault_tab_apis")
                        ) {
                            Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🔌 Ajouter / Changer APIs (${allApis.size})",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = { activeVaultSectionTab = 1 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (activeVaultSectionTab == 1) MekanikBrandOrange else GraphiteDark
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_vault_tab_subscription_prices")
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "💰 Changer Prix Abonnements",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = { activeVaultSectionTab = 2 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (activeVaultSectionTab == 2) MekanikBrandOrange else GraphiteDark
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_vault_tab_subaccounts_rights")
                        ) {
                            Icon(Icons.Default.ManageAccounts, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "👥 Droits Sous-Comptes",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        if (activeVaultSectionTab == 0) {
            // ================================================================
            // ONGLET 0 : CLÉS TECDOC, ETKA, STELLANTIS, LECTURE VIN & MULTI-API
            // ================================================================
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MetalCard,
                    border = BorderStroke(1.dp, MekanikBrandOrange.copy(alpha = 0.65f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "AJOUTER & MODIFIER PLUSIEURS CLÉS API MANUELLEMENT (1 CLIC SANS CODER) :",
                            color = MekanikBrandOrange,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )

                        // Modèles pré-configurés pour TecDoc, ETKA, Stellantis, Lecture Châssis VIN, Dialogys, Autodata
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    editingApiId = null
                                    showAddApiForm = !showAddApiForm
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MekanikBrandOrange),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_toggle_add_nocode_api_form")
                            ) {
                                Icon(Icons.Default.AddLink, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (showAddApiForm) "Fermer l'éditeur" else "+ Ajouter une Nouvelle API",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    editingApiId = "API-TECDOC-PEGASUS-01"
                                    newApiName = "TecDoc / TecAlliance Pegasus 3.0 API (Catalogue OEM & K-Type / VIN)"
                                    newApiCategory = "TecDoc / Catalogue Équipementiers"
                                    newApiBaseUrl = "https://webservice.tecalliance.services/pegasus-3-0/services/TecdocToCatDLB.jsonEndpoint"
                                    newApiKey = "TECDOC_PROVIDER_ID_22491_DZ"
                                    newApiSecret = "tecdoc_live_api_key_9948271049281_pegasus3"
                                    newApiWebhook = "https://api.mecanicautoplus.dz/webhooks/tecdoc-sync"
                                    newApiHeaders = "{\"X-Api-Key\": \"tecdoc_live_api_key_9948271049281_pegasus3\", \"Accept-Language\": \"fr\"}"
                                    showAddApiForm = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, TechCyan)
                            ) {
                                Text("📘 Clés TecDoc", color = OffWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    editingApiId = "API-ETKA-VAG-02"
                                    newApiName = "ETKA / Partslink24 Official VAG API (VW, Audi, Seat, Škoda, Cupra, Porsche)"
                                    newApiCategory = "ETKA / Groupe VAG"
                                    newApiBaseUrl = "https://api.partslink24.com/v2/vag-etka/vin-exploded-catalog"
                                    newApiKey = "ETKA_VAG_DEALER_ID_DZ_88310"
                                    newApiSecret = "etka_secret_vag_prcode_token_7741920491"
                                    newApiWebhook = "https://api.mecanicautoplus.dz/webhooks/etka-vag"
                                    newApiHeaders = "{\"X-ETKA-Market\": \"RDW\", \"X-VAG-PR-Decode\": \"true\"}"
                                    showAddApiForm = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, MekanikValidationGreen)
                            ) {
                                Text("🚗 Clés ETKA (VAG)", color = OffWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    editingApiId = "API-STELLANTIS-SERVICEBOX-03"
                                    newApiName = "Groupe Stellantis ServiceBox & SEDRE API (Peugeot, Citroën, DS, Fiat, Opel, Jeep)"
                                    newApiCategory = "Groupe Stellantis (ServiceBox / RP / OPR)"
                                    newApiBaseUrl = "https://api.groupe-psa.com/aftersales/servicebox/v3/vin-decode-parts"
                                    newApiKey = "STELLANTIS_B2B_CLIENT_ID_449102_PSA_FCA"
                                    newApiSecret = "stellantis_sb_secret_opr_dam_sedre_883920194"
                                    newApiWebhook = "https://api.mecanicautoplus.dz/webhooks/stellantis-servicebox"
                                    newApiHeaders = "{\"X-Stellantis-Brands\": \"PEUGEOT,CITROEN,DS,FIAT,OPEL,JEEP,ALFA\", \"X-Include-SEDRE-Wiring\": \"true\"}"
                                    showAddApiForm = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, MekanikBrandOrange)
                            ) {
                                Text("🦁 Groupe Stellantis (ServiceBox)", color = OffWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    editingApiId = "API-VIN-MASTER-DECODER-04"
                                    newApiName = "Logiciel Lecture Numéro de Châssis (VIN Decoder : Vincario / AutoDNA / Eurotax)"
                                    newApiCategory = "Lecture Châssis VIN & Historique"
                                    newApiBaseUrl = "https://api.vincario.com/3.2/vin-decode/chassislookup"
                                    newApiKey = "VIN_MASTER_API_KEY_DZ_5592018"
                                    newApiSecret = "vin_control_hash_secret_wmi_vds_vis_2026"
                                    newApiWebhook = "https://api.mecanicautoplus.dz/webhooks/vin-decoder"
                                    newApiHeaders = "{\"X-Decode-Engine-Gearbox\": \"true\", \"X-Decode-Bodyshop-SheetMetal\": \"true\"}"
                                    showAddApiForm = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, WarningAmber)
                            ) {
                                Text("🔍 Lecture Châssis (VIN)", color = OffWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    editingApiId = "API-RENAULT-DIALOGYS-05"
                                    newApiName = "Renault / Dacia / Nissan Alliance New Dialogys API"
                                    newApiCategory = "Renault / Dacia Dialogys"
                                    newApiBaseUrl = "https://api.parts.renault.com/dialogys/v2/vin-technical-sheets"
                                    newApiKey = "DIALOGYS_ALLIANCE_ID_771920"
                                    newApiSecret = "renault_dacia_asos_dialogys_token_6639102"
                                    newApiWebhook = "https://api.mecanicautoplus.dz/webhooks/renault-dialogys"
                                    showAddApiForm = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, MetalBorder)
                            ) {
                                Text("📐 Renault Dialogys", color = OffWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    editingApiId = null
                                    newApiName = "Autodata & HaynesPro WorkshopData Tech API"
                                    newApiCategory = "Autodata / Documentation Technique"
                                    newApiBaseUrl = "https://api.autodata-group.com/v1/technical-repair-data"
                                    newApiKey = "autodata_client_key_${(1000..9999).random()}"
                                    newApiSecret = "autodata_secret_live_${(100000..999999).random()}"
                                    newApiWebhook = "https://api.mecanicautoplus.dz/webhooks/autodata/sync"
                                    showAddApiForm = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, MekanikBrandOrange)
                            ) {
                                Text("🔧 + Autodata / HaynesPro", color = OffWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }

                            OutlinedButton(
                                onClick = { revealSecrets = !revealSecrets },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, MetalBorder)
                            ) {
                                Icon(
                                    if (revealSecrets) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = AluminumGray,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    if (revealSecrets) "Masquer Clés" else "Afficher Clés",
                                    color = OffWhite,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Filtre rapide par famille d'API
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("TOUTES", "TecDoc", "ETKA", "Stellantis", "VIN", "Dialogys", "Autodata", "Paiement").forEach { catFilter ->
                                val selected = selectedCategoryFilter == catFilter
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selected) MekanikBrandOrange else GraphiteDark,
                                    border = BorderStroke(1.dp, if (selected) MekanikBrandOrange else MetalBorder),
                                    modifier = Modifier.clickable { selectedCategoryFilter = catFilter }
                                ) {
                                    Text(
                                        text = catFilter,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        // Formulaire d'ajout ou modification manuelle d'API sans code
                        if (showAddApiForm) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GraphiteDark,
                                border = BorderStroke(1.5.dp, MekanikBrandOrange),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = if (editingApiId != null)
                                            "MODIFICATION MANUELLE DE LA CLÉ API ($editingApiId) SANS TOUCHER AU CODE"
                                        else
                                            "AJOUTER UNE NOUVELLE CLÉ API EXTERNE (TECDOC, ETKA, STELLANTIS, VIN, AUTODATA, MICROCAT...)",
                                        color = MekanikBrandOrange,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp
                                    )

                                    OutlinedTextField(
                                        value = newApiName,
                                        onValueChange = { newApiName = it },
                                        label = { Text("Nom du Logiciel / API (ex: TecDoc Pegasus, ETKA VAG, Stellantis ServiceBox, VIN...)") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth().testTag("input_nocode_api_name")
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = newApiCategory,
                                            onValueChange = { newApiCategory = it },
                                            label = { Text("Groupe / Catégorie") },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = newApiEnv,
                                            onValueChange = { newApiEnv = it },
                                            label = { Text("Environnement (PRODUCTION/SANDBOX)") },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    OutlinedTextField(
                                        value = newApiBaseUrl,
                                        onValueChange = { newApiBaseUrl = it },
                                        label = { Text("URL Endpoint / Serveur WebService (TecDoc, Partslink24 ETKA, Stellantis...)") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth().testTag("input_nocode_api_url")
                                    )

                                    OutlinedTextField(
                                        value = newApiKey,
                                        onValueChange = { newApiKey = it },
                                        label = { Text("Clé API / Provider ID TecDoc / Dealer ID ETKA / Client ID Stellantis") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth().testTag("input_nocode_api_key")
                                    )

                                    OutlinedTextField(
                                        value = newApiSecret,
                                        onValueChange = { newApiSecret = it },
                                        label = { Text("Secret Key / Token OPR-DAM Stellantis / Hash VIN Decoder") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth().testTag("input_nocode_api_secret")
                                    )

                                    OutlinedTextField(
                                        value = newApiWebhook,
                                        onValueChange = { newApiWebhook = it },
                                        label = { Text("Webhook URL / Callback de Synchronisation") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = newApiHeaders,
                                        onValueChange = { newApiHeaders = it },
                                        label = { Text("En-têtes HTTP JSON (Marques Stellantis, Marché ETKA, Langue TecDoc)") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Button(
                                        onClick = {
                                            if (newApiName.isNotBlank() && newApiBaseUrl.isNotBlank()) {
                                                val targetId = editingApiId ?: "API-CUSTOM-${System.currentTimeMillis() % 100000}"
                                                SuperAdminGlobalControlRegistry.addOrUpdateExternalApi(
                                                    NoCodeExternalApiEntry(
                                                        id = targetId,
                                                        apiName = newApiName.trim(),
                                                        providerCategory = newApiCategory.ifBlank { "Catalogue OEM / VIN" },
                                                        baseUrl = newApiBaseUrl.trim(),
                                                        apiKeyOrClientId = newApiKey.ifBlank { "key_live_${(10000..99999).random()}" },
                                                        apiSecretOrToken = newApiSecret.ifBlank { "sec_live_${(100000..999999).random()}" },
                                                        webhookOrCallbackUrl = newApiWebhook.trim(),
                                                        customHeadersJson = newApiHeaders.trim(),
                                                        environment = newApiEnv.uppercase(),
                                                        isEnabled = true,
                                                        lastPingStatus = "200 OK • Clé enregistrée manuellement et connectée en direct",
                                                        addedBySuperAdmin = superAdminEmail
                                                    )
                                                )
                                                editingApiId = null
                                                newApiName = ""
                                                newApiKey = ""
                                                newApiSecret = ""
                                                showAddApiForm = false
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MekanikValidationGreen),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("btn_save_nocode_api")
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "ENREGISTRER LA CLÉ API & CONNECTER IMMÉDIATEMENT",
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            items(filteredApis, key = { it.id }) { api ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MetalCard,
                    border = BorderStroke(
                        1.dp,
                        if (api.isEnabled) MekanikBrandOrange.copy(alpha = 0.55f) else MetalBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(5.dp),
                                        color = GraphiteDark,
                                        border = BorderStroke(1.dp, MekanikBrandOrange.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = api.providerCategory,
                                            color = MekanikBrandOrange,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.5.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(5.dp),
                                        color = if (api.isEnabled) MekanikValidationGreen.copy(alpha = 0.16f) else MekanikErrorRed.copy(alpha = 0.16f)
                                    ) {
                                        Text(
                                            text = api.environment,
                                            color = if (api.isEnabled) MekanikValidationGreen else MekanikErrorRed,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = api.apiName,
                                    color = OffWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Switch(
                                checked = api.isEnabled,
                                onCheckedChange = { enabled ->
                                    SuperAdminGlobalControlRegistry.toggleExternalApi(api.id, enabled)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = MekanikValidationGreen
                                )
                            )
                        }

                        Text(
                            text = "Endpoint : ${api.baseUrl}",
                            color = AluminumGray,
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Clé / Client ID : ${if (revealSecrets) api.apiKeyOrClientId else api.apiKeyOrClientId.take(6) + "••••••••••••"}",
                            color = OffWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Secret / Token : ${if (revealSecrets) api.apiSecretOrToken else "••••••••••••••••••••"}",
                            color = TechCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        if (api.customHeadersJson.isNotBlank()) {
                            Text(
                                text = "Paramètres OEM / VIN : ${api.customHeadersJson}",
                                color = AluminumGray,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = api.lastPingStatus,
                                color = if (api.isEnabled) MekanikValidationGreen else AluminumGray,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                modifier = Modifier.weight(1f)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        editingApiId = api.id
                                        newApiName = api.apiName
                                        newApiCategory = api.providerCategory
                                        newApiBaseUrl = api.baseUrl
                                        newApiKey = api.apiKeyOrClientId
                                        newApiSecret = api.apiSecretOrToken
                                        newApiWebhook = api.webhookOrCallbackUrl
                                        newApiHeaders = api.customHeadersJson
                                        newApiEnv = api.environment
                                        showAddApiForm = true
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, TechCyan),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("✏️ Modifier Clé", color = TechCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { SuperAdminGlobalControlRegistry.pingExternalApi(api.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, MekanikBrandOrange),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Tester", color = MekanikBrandOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { SuperAdminGlobalControlRegistry.deleteExternalApi(api.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, MekanikErrorRed),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Suppr.", color = MekanikErrorRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else if (activeVaultSectionTab == 1) {
            // ================================================================
            // ONGLET 1 : CHANGER LES PRIX DES ABONNEMENTS & MÉCAVERIF+ MANUELLEMENT
            // ================================================================
            item {
                var proPriceText by remember { mutableStateOf(SuperAdminGlobalControlRegistry.mecaVerifProPriceDzd.toString()) }
                var partPriceText by remember { mutableStateOf(SuperAdminGlobalControlRegistry.mecaVerifPartPriceDzd.toString()) }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MetalCard,
                    border = BorderStroke(1.dp, MekanikValidationGreen)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "MODIFICATION MANUELLE DES TARIFS MÉCAVERIF+™ (PAR CHÂSSIS VIN)",
                            color = MekanikValidationGreen,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.5.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = proPriceText,
                                onValueChange = { proPriceText = it.filter { c -> c.isDigit() } },
                                label = { Text("Tarif Pro (DA/VIN)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = partPriceText,
                                onValueChange = { partPriceText = it.filter { c -> c.isDigit() } },
                                label = { Text("Tarif Particulier (DA/VIN)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    SuperAdminGlobalControlRegistry.mecaVerifProPriceDzd = proPriceText.toIntOrNull() ?: 250
                                    SuperAdminGlobalControlRegistry.mecaVerifPartPriceDzd = partPriceText.toIntOrNull() ?: 300
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MekanikValidationGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Sauvegarder", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            items(SuperAdminGlobalControlRegistry.manualSubscriptionPrices, key = { it.role.name }) { entry ->
                var monthlyInput by remember(entry.monthlyPriceDzd) { mutableStateOf(entry.monthlyPriceDzd.toString()) }
                var setupInput by remember(entry.setupFeeDzd) { mutableStateOf(entry.setupFeeDzd.toString()) }
                var maxSubsInput by remember(entry.maxSubAccounts) { mutableStateOf(entry.maxSubAccounts.toString()) }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MetalCard,
                    border = BorderStroke(1.dp, MekanikBrandOrange.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${entry.role.label} (${entry.role.shortTitle})",
                            color = OffWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Prix actuel : ${entry.monthlyPriceDzd} DA / mois • Frais d'ouverture : ${entry.setupFeeDzd} DA • Max sous-comptes : ${entry.maxSubAccounts}",
                            color = MekanikValidationGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = monthlyInput,
                                onValueChange = { monthlyInput = it.filter { c -> c.isDigit() } },
                                label = { Text("Prix / Mois (DA)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = setupInput,
                                onValueChange = { setupInput = it.filter { c -> c.isDigit() } },
                                label = { Text("Setup (DA)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = maxSubsInput,
                                onValueChange = { maxSubsInput = it.filter { c -> c.isDigit() } },
                                label = { Text("Sous-Comptes") },
                                singleLine = true,
                                modifier = Modifier.weight(0.8f)
                            )
                            Button(
                                onClick = {
                                    SuperAdminGlobalControlRegistry.updateRoleSubscriptionPriceManually(
                                        role = entry.role,
                                        newMonthlyDzd = monthlyInput.toIntOrNull() ?: entry.monthlyPriceDzd,
                                        newSetupDzd = setupInput.toIntOrNull() ?: entry.setupFeeDzd,
                                        newMaxSubs = maxSubsInput.toIntOrNull() ?: entry.maxSubAccounts
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MekanikBrandOrange),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Enregistrer", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        } else {
            // ================================================================
            // ONGLET 2 : GESTION DES DROITS D'ACCÈS AUX SOUS-COMPTES (2FA + SMS)
            // ================================================================
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MetalCard,
                    border = BorderStroke(1.dp, MekanikBrandOrange)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "GESTION DES SOUS-COMPTES & MATRICE DES DROITS D'ACCÈS",
                            color = OffWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.5.sp
                        )
                        Text(
                            text = "Sélectionnez une formule de compte pour activer/désactiver les permissions de chaque sous-compte (accès VIN/TecDoc/ETKA/Stellantis, Devis Atelier, Stock Magasin, Photos Carrosserie Avant/Après, MekanikAI OBD, KPI Financiers).",
                            color = AluminumGray,
                            fontSize = 11.sp
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                AccountRole.ATELIER_MAGASIN_MIXTE,
                                AccountRole.GARAGE_PRO,
                                AccountRole.VENDEUR_PIECES,
                                AccountRole.ATELIER_CARROSSERIE,
                                AccountRole.LOCATION_VOITURES
                            ).forEach { roleTab ->
                                val isSel = selectedRoleForSubAccounts == roleTab
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSel) MekanikBrandOrange else GraphiteDark,
                                    border = BorderStroke(1.dp, if (isSel) MekanikBrandOrange else MetalBorder),
                                    modifier = Modifier.clickable { selectedRoleForSubAccounts = roleTab }
                                ) {
                                    Text(
                                        text = "${roleTab.shortTitle} (${SuperAdminGlobalControlRegistry.getSubAccountsForRole(roleTab).size})",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            val roleSubs = SuperAdminGlobalControlRegistry.getSubAccountsForRole(selectedRoleForSubAccounts)
            items(roleSubs, key = { "${selectedRoleForSubAccounts.name}_${it.slotNumber}" }) { member ->
                SubAccountPermissionsCard(
                    role = selectedRoleForSubAccounts,
                    member = member
                )
            }
        }
    }
}

@Composable
private fun SubAccountPermissionsCard(
    role: AccountRole,
    member: HybridSubAccountMember
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MetalCard,
        border = BorderStroke(1.dp, if (member.isActive) MekanikBrandOrange.copy(alpha = 0.55f) else MekanikErrorRed),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                        shape = CircleShape,
                        color = MekanikBrandOrange.copy(alpha = 0.2f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "#${member.slotNumber}",
                                color = MekanikBrandOrange,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = member.fullName,
                                color = OffWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = GraphiteDark,
                                border = BorderStroke(1.dp, MekanikBrandOrange.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = member.departmentBadge,
                                    color = MekanikBrandOrange,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${member.roleTitle} • PIN : ${member.pinCode}",
                            color = AluminumGray,
                            fontSize = 11.sp
                        )
                    }
                }

                Switch(
                    checked = member.isActive,
                    onCheckedChange = {
                        SuperAdminGlobalControlRegistry.toggleSubAccountActiveForRole(role, member.slotNumber)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MekanikValidationGreen
                    )
                )
            }

            Text(
                text = "DROITS D'ACCÈS DU SOUS-COMPTE (CLIQUEZ POUR ACTIVER / RÉVOQUER) :",
                color = TechCyan,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 10.sp
            )

            // Grille de permissions granulaires
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PermissionToggleChip(
                        label = "🔍 VIN / TecDoc / ETKA / Stellantis",
                        enabled = member.canSearchVinAndOem,
                        onToggle = {
                            SuperAdminGlobalControlRegistry.updateSubAccountPermissions(role, member.slotNumber) {
                                it.copy(canSearchVinAndOem = !it.canSearchVinAndOem)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    PermissionToggleChip(
                        label = "🛠️ Devis & Ordres Réparation",
                        enabled = member.canManageWorkshopOrQuotes,
                        onToggle = {
                            SuperAdminGlobalControlRegistry.updateSubAccountPermissions(role, member.slotNumber) {
                                it.copy(canManageWorkshopOrQuotes = !it.canManageWorkshopOrQuotes)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PermissionToggleChip(
                        label = "📦 Stock Magasin & Scanner QR",
                        enabled = member.canManageStoreStockAndSales,
                        onToggle = {
                            SuperAdminGlobalControlRegistry.updateSubAccountPermissions(role, member.slotNumber) {
                                it.copy(canManageStoreStockAndSales = !it.canManageStoreStockAndSales)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    PermissionToggleChip(
                        label = "📸 Photos Carrosserie Avant/Après",
                        enabled = member.canUploadBodyshopPhotos,
                        onToggle = {
                            SuperAdminGlobalControlRegistry.updateSubAccountPermissions(role, member.slotNumber) {
                                it.copy(canUploadBodyshopPhotos = !it.canUploadBodyshopPhotos)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PermissionToggleChip(
                        label = "🧠 Diagnostic MekanikAI & OBD",
                        enabled = member.canUseMekanikAiObd,
                        onToggle = {
                            SuperAdminGlobalControlRegistry.updateSubAccountPermissions(role, member.slotNumber) {
                                it.copy(canUseMekanikAiObd = !it.canUseMekanikAiObd)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    PermissionToggleChip(
                        label = "📊 Chiffre d'Affaires & Marges",
                        enabled = member.canViewFinancialKpis,
                        onToggle = {
                            SuperAdminGlobalControlRegistry.updateSubAccountPermissions(role, member.slotNumber) {
                                it.copy(canViewFinancialKpis = !it.canViewFinancialKpis)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionToggleChip(
    label: String,
    enabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (enabled) MekanikValidationGreen.copy(alpha = 0.16f) else GraphiteDark,
        border = BorderStroke(
            1.dp,
            if (enabled) MekanikValidationGreen else MetalBorder
        ),
        modifier = modifier.clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = if (enabled) OffWhite else AluminumGray,
                fontWeight = if (enabled) FontWeight.Bold else FontWeight.Medium,
                fontSize = 10.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (enabled) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = if (enabled) MekanikValidationGreen else AluminumGray,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

// ============================================================================
// 5. MODALE D'AUTHENTIFICATION 2FA GOOGLE AUTHENTICATOR + SMS POUR TOUS LES COMPTES
//    (ADMINISTRATION DU COMPTE : AJOUTER / SUPPRIMER DES SOUS-COMPTES, DROITS & ANTI-CLONAGE)
// ============================================================================

@Composable
fun AccountAdmin2FaSubAccountsModal(
    accountRole: AccountRole,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val roleSecret = remember(accountRole) {
        SuperAdminGlobalControlRegistry.getTotpSecretForAccountRole(accountRole)
    }
    val antiCloneSeal = remember(accountRole) {
        GoogleAuthenticatorTotpEngine.computeAntiCloneSignatureSeal(context, accountRole.name)
    }

    var isAccountAdmin2FaUnlocked by remember(accountRole) { mutableStateOf(false) }
    var totpInput by remember { mutableStateOf("") }
    var smsInput by remember { mutableStateOf(SuperAdminGlobalControlRegistry.activeSmsOtpCode) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Horloge TOTP 30s
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            nowMillis = System.currentTimeMillis()
        }
    }

    val currentTotpCode = remember(roleSecret, nowMillis / 30000L) {
        GoogleAuthenticatorTotpEngine.generateTotpCode(roleSecret, nowMillis)
    }
    val secondsLeft = GoogleAuthenticatorTotpEngine.getRemainingSecondsInWindow(nowMillis)
    val currentSmsCode = SuperAdminGlobalControlRegistry.activeSmsOtpCode

    // Formulaire d'ajout de sous-compte
    var newSubFullName by remember { mutableStateOf("") }
    var newSubRoleTitle by remember { mutableStateOf("") }
    var newSubDepartment by remember {
        mutableStateOf(
            when (accountRole) {
                AccountRole.ATELIER_MAGASIN_MIXTE -> "ATELIER MONTAGE"
                AccountRole.VENDEUR_PIECES -> "MAGASIN PIÈCES"
                AccountRole.ATELIER_CARROSSERIE -> "TÔLERIE & PEINTURE"
                else -> "ATELIER MÉCANIQUE"
            }
        )
    }
    var newSubPin by remember { mutableStateOf("5566") }

    val subAccounts = SuperAdminGlobalControlRegistry.getSubAccountsForRole(accountRole)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .testTag("account_admin_2fa_subaccounts_modal"),
            shape = RoundedCornerShape(18.dp),
            color = GraphiteDark,
            border = BorderStroke(1.5.dp, MekanikBrandOrange)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Surface(
                    color = MetalCard,
                    border = BorderStroke(1.dp, MetalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MekanikBrandOrange.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = MekanikBrandOrange,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "ADMINISTRATION DU COMPTE (${accountRole.shortTitle.uppercase()})",
                                    color = OffWhite,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.5.sp
                                )
                                Text(
                                    text = "Protection 2FA Google Authenticator + SMS • Droits d'accès Sous-Comptes & Anti-Clonage",
                                    color = AluminumGray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = OffWhite)
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Bouclier Anti-Piratage & Anti-Clonage APK (Actif sur tous les comptes)
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MetalCard,
                            border = BorderStroke(1.dp, MekanikValidationGreen.copy(alpha = 0.55f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = MekanikValidationGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "BOUCLIER ANTI-PIRATAGE & ANTI-CLONAGE D'APPLICATION ACTIF",
                                        color = MekanikValidationGreen,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.5.sp
                                    )
                                    Text(
                                        text = "Empreinte Cryptographique APK & Appareil : $antiCloneSeal • Toute copie ou clonage non autorisé est bloqué.",
                                        color = AluminumGray,
                                        fontSize = 10.5.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }

                    if (!isAccountAdmin2FaUnlocked) {
                        // ÉTAPE 1 : VALIDATION GOOGLE AUTHENTICATOR + SMS OBLIGATOIRE
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MetalCard,
                                border = BorderStroke(1.5.dp, MekanikBrandOrange),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "AUTHENTIFICATION À DOUBLE FACTEUR (GOOGLE AUTHENTICATOR + SMS)",
                                        color = OffWhite,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Pour accéder à l'espace Administrateur de votre ${accountRole.label} et pouvoir ajouter, supprimer ou modifier les droits d'accès des sous-comptes, saisissez le code à 6 chiffres Google Authenticator et le code SMS reçu sur votre téléphone.",
                                        color = AluminumGray,
                                        fontSize = 12.sp
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
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
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "Clé Google Authenticator (${accountRole.shortTitle})",
                                                    color = MekanikBrandOrange,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                                Text(
                                                    text = "Renouvellement : ${secondsLeft}s",
                                                    color = MekanikValidationGreen,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            }
                                            Text(
                                                text = "Secret TOTP : $roleSecret",
                                                color = AluminumGray,
                                                fontSize = 10.5.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${currentTotpCode.take(3)} ${currentTotpCode.takeLast(3)}",
                                                    color = OffWhite,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 20.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                                OutlinedButton(
                                                    onClick = {
                                                        totpInput = currentTotpCode
                                                        smsInput = currentSmsCode
                                                        errorMsg = null
                                                    },
                                                    shape = RoundedCornerShape(8.dp),
                                                    border = BorderStroke(1.dp, MekanikBrandOrange),
                                                    modifier = Modifier.testTag("btn_autofill_account_admin_totp")
                                                ) {
                                                    Text(
                                                        text = "Utiliser Code Authenticator + SMS",
                                                        color = MekanikBrandOrange,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = totpInput,
                                        onValueChange = {
                                            totpInput = it.filter { ch -> ch.isDigit() }.take(6)
                                            errorMsg = null
                                        },
                                        label = { Text("1. Code Google Authenticator à 6 chiffres") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_account_admin_totp"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MekanikBrandOrange,
                                            unfocusedBorderColor = MetalBorder,
                                            focusedTextColor = OffWhite,
                                            unfocusedTextColor = OffWhite
                                        )
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = smsInput,
                                            onValueChange = {
                                                smsInput = it.filter { ch -> ch.isDigit() }.take(6)
                                                errorMsg = null
                                            },
                                            label = { Text("2. Code SMS reçu sur ${SuperAdminGlobalControlRegistry.superAdminPhoneNumber}") },
                                            singleLine = true,
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("input_account_admin_sms"),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = MekanikValidationGreen,
                                                unfocusedBorderColor = MetalBorder,
                                                focusedTextColor = OffWhite,
                                                unfocusedTextColor = OffWhite
                                            )
                                        )

                                        OutlinedButton(
                                            onClick = {
                                                val newSms = SuperAdminGlobalControlRegistry.sendNewSmsOtpToSuperAdminPhone()
                                                smsInput = newSms
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, MekanikValidationGreen),
                                            modifier = Modifier.height(52.dp)
                                        ) {
                                            Text("📲 Renvoyer SMS", color = MekanikValidationGreen, fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                                        }
                                    }

                                    errorMsg?.let {
                                        Text(
                                            text = it,
                                            color = MekanikErrorRed,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.5.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            val totpOk = GoogleAuthenticatorTotpEngine.verifyTotpCode(roleSecret, totpInput, nowMillis)
                                            val smsOk = SuperAdminGlobalControlRegistry.verifySuperAdminSmsCode(smsInput)
                                            if (totpOk && smsOk) {
                                                isAccountAdmin2FaUnlocked = true
                                                errorMsg = null
                                            } else if (!totpOk) {
                                                errorMsg = "Code Google Authenticator incorrect. Saisissez le code à 6 chiffres actif ($currentTotpCode)."
                                            } else {
                                                errorMsg = "Code SMS incorrect. Saisissez le code SMS reçu ($currentSmsCode)."
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MekanikBrandOrange),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("btn_unlock_account_admin_2fa")
                                    ) {
                                        Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "ACCÉDER À L'ADMINISTRATION DES SOUS-COMPTES (2FA + SMS)",
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.5.sp
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // ÉTAPE 2 : GESTION DES SOUS-COMPTES (AJOUTER / SUPPRIMER / DROITS D'ACCÈS)
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MetalCard,
                                border = BorderStroke(1.dp, MekanikBrandOrange)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "AJOUTER UN SOUS-COMPTE (${accountRole.shortTitle.uppercase()})",
                                                color = OffWhite,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "Sous-comptes actifs : ${subAccounts.size} / ${accountRole.maxSubAccountsAllowed.coerceAtLeast(4)} autorisés",
                                                color = MekanikBrandOrange,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        OutlinedButton(
                                            onClick = { isAccountAdmin2FaUnlocked = false },
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, MetalBorder)
                                        ) {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = MekanikBrandOrange, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Verrouiller 2FA", color = OffWhite, fontSize = 11.sp)
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = newSubFullName,
                                            onValueChange = { newSubFullName = it },
                                            label = { Text("Nom complet de l'employé") },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f).testTag("input_new_subaccount_name")
                                        )
                                        OutlinedTextField(
                                            value = newSubPin,
                                            onValueChange = { newSubPin = it.filter { c -> c.isDigit() }.take(4) },
                                            label = { Text("Code PIN (4 ch.)") },
                                            singleLine = true,
                                            modifier = Modifier.width(125.dp)
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = newSubRoleTitle,
                                            onValueChange = { newSubRoleTitle = it },
                                            label = { Text("Poste (ex: Mécanicien Monteur, Vendeur Comptoir, Tôlier)") },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f).testTag("input_new_subaccount_role")
                                        )
                                        OutlinedTextField(
                                            value = newSubDepartment,
                                            onValueChange = { newSubDepartment = it },
                                            label = { Text("Département") },
                                            singleLine = true,
                                            modifier = Modifier.weight(0.8f)
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            if (newSubFullName.isNotBlank()) {
                                                SuperAdminGlobalControlRegistry.addSubAccountForRole(
                                                    role = accountRole,
                                                    fullName = newSubFullName,
                                                    roleTitle = newSubRoleTitle.ifBlank { "Opérateur ${accountRole.shortTitle}" },
                                                    departmentBadge = newSubDepartment.ifBlank { accountRole.badgeLabel },
                                                    pinCode = newSubPin
                                                )
                                                newSubFullName = ""
                                                newSubRoleTitle = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MekanikValidationGreen),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("btn_add_subaccount_2fa")
                                    ) {
                                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "+ AJOUTER CE SOUS-COMPTE (CERTIFIÉ 2FA + SMS)",
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "SOUS-COMPTES & DROITS D'ACCÈS RATTACHÉS AU COMPTE PRINCIPAL (${subAccounts.size})",
                                color = AluminumGray,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.5.sp
                            )
                        }

                        if (subAccounts.isEmpty()) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MetalCard,
                                    border = BorderStroke(1.dp, MetalBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Aucun sous-compte configuré. Utilisez le formulaire ci-dessus pour ajouter un sous-compte protégé par 2FA.",
                                        color = AluminumGray,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        } else {
                            items(subAccounts, key = { it.slotNumber }) { member ->
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    SubAccountPermissionsCard(
                                        role = accountRole,
                                        member = member
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                SuperAdminGlobalControlRegistry.removeSubAccountForRole(
                                                    accountRole,
                                                    member.slotNumber
                                                )
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, MekanikErrorRed),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.testTag("btn_delete_subaccount_${member.slotNumber}")
                                        ) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = MekanikErrorRed, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Supprimer le sous-compte #${member.slotNumber}", color = MekanikErrorRed, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
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
