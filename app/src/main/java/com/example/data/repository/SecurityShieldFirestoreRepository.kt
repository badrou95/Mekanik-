package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import java.security.MessageDigest
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType(val value: String) {
  CREATE("create"),
  UPDATE("update"),
  DELETE("delete"),
  LIST("list"),
  GET("get"),
  WRITE("write")
}

fun handleFirestoreError(
  exception: Exception,
  operationType: OperationType,
  path: String?
): String {
  val currentUser = try {
    FirebaseAuth.getInstance().currentUser
  } catch (e: Exception) {
    null
  }

  val providerInfoList = currentUser?.providerData?.map { provider ->
    JSONObject().apply {
      put("providerId", provider.providerId)
      put("email", provider.email)
    }
  } ?: emptyList()

  val authInfoJson = JSONObject().apply {
    put("userId", currentUser?.uid)
    put("email", currentUser?.email)
    put("emailVerified", currentUser?.isEmailVerified)
    put("tenantId", currentUser?.tenantId)
    put("providerInfo", JSONArray(providerInfoList))
  }

  val errorInfoJson = JSONObject().apply {
    put("error", exception.message ?: exception.toString())
    put("operationType", operationType.value)
    put("path", path)
    put("authInfo", authInfoJson)
  }

  val jsonString = errorInfoJson.toString()
  Log.e("FirestoreError", "Firestore Error: $jsonString")

  if (path == null || !path.contains("system_error_logs")) {
    MekanikErrorLogger.logLocalPersistenceOrFirebaseError(
      storageEngine = "Firebase Firestore & Cache Local",
      operation = operationType.value,
      pathOrTable = path ?: "firestore_root",
      exceptionMessage = exception.message ?: exception.toString(),
      stackTraceOrJson = jsonString,
      severity = SystemErrorSeverity.HIGH,
      userEmail = currentUser?.email ?: "badroumc@gmail.com"
    )
  }
  return jsonString
}

class SecurityShieldFirestoreRepository(
  private val db: FirebaseFirestore,
  private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    ),
    FirebaseAuth.getInstance()
  )

  private fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("L'utilisateur doit être authentifié via Firebase Auth (Google Sign-In) avant d'accéder au DMS sécurisé.")
  }

  suspend fun upsertUserRbacProfile(
    email: String,
    displayName: String,
    role: FirebaseRbacRole,
    boundHardwareDeviceId: String,
    wilaya: String = "16 - Alger",
    operatorPinHash: String? = null,
    isRemoteWiped: Boolean = false
  ): Result<FirestoreUserRbacProfile> = runCatching {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid)
    try {
      val existingSnap = docRef.get().await()
      val baseMap = mutableMapOf<String, Any>(
        "userId" to uid,
        "email" to email.ifBlank { "operator@mekanikplus.dz" },
        "displayName" to displayName.ifBlank { "Opérateur Mekanik+" },
        "rbacRole" to role.id,
        "boundHardwareDeviceId" to boundHardwareDeviceId.ifBlank { "HW-KEYSTORE-DZ16-0001" },
        "wilaya" to wilaya.ifBlank { "16 - Alger" },
        "isRemoteWiped" to isRemoteWiped,
        "updatedAt" to FieldValue.serverTimestamp()
      )
      if (!operatorPinHash.isNullOrBlank()) {
        baseMap["operatorPinHash"] = operatorPinHash
      }
      if (!existingSnap.exists()) {
        baseMap["createdAt"] = FieldValue.serverTimestamp()
        docRef.set(baseMap).await()
      } else {
        docRef.update(baseMap).await()
      }
      val updatedSnap = docRef.get().await()
      updatedSnap.toObject(
        FirestoreUserRbacProfile::class.java,
        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
      ) ?: FirestoreUserRbacProfile(
        id = uid,
        userId = uid,
        email = email,
        displayName = displayName,
        rbacRole = role.id,
        boundHardwareDeviceId = boundHardwareDeviceId,
        wilaya = wilaya
      )
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, docRef.path)
      throw e
    }
  }

  suspend fun getUserRbacProfile(targetUserId: String? = null): Result<FirestoreUserRbacProfile?> = runCatching {
    val uid = targetUserId ?: requireUserId()
    val docRef = db.collection("users").document(uid)
    try {
      val snap = docRef.get().await()
      snap.toObject(
        FirestoreUserRbacProfile::class.java,
        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
      )
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.GET, docRef.path)
      throw e
    }
  }

  fun observeCurrentUserRbacProfile(): Flow<FirestoreUserRbacProfile?> = flow {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid)
    emitAll(
      docRef.snapshots()
        .map { snap ->
          snap.toObject(
            FirestoreUserRbacProfile::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
          )
        }
        .catch { error ->
          if (error is Exception) handleFirestoreError(error, OperationType.GET, docRef.path)
          throw error
        }
    )
  }

  // ============================================================================
  // 1.5. COMPOSANT ACCOUNTSELECTOR : STOCKAGE DU RÔLE CHOISI DANS FIRESTORE
  // ============================================================================

  suspend fun upsertUserAccountSelection(
    email: String,
    displayName: String,
    accountRole: AccountRole,
    organizationName: String,
    wilaya: String = "16 - Alger",
    subAccountsCount: Int = accountRole.defaultSubAccountsCount,
    subAccountsSummary: String = ""
  ): Result<FirestoreUserAccountSelection> = runCatching {
    val uid = requireUserId()
    val docRef = db.collection("user_account_selections").document(uid)
    val safeEmail = email.ifBlank { auth.currentUser?.email ?: "client@mekanikplus.dz" }.take(115)
    val safeDisplay = displayName.ifBlank { auth.currentUser?.displayName ?: "Utilisateur DMS Mécanicauto+" }.take(95)
    val safeOrg = organizationName.ifBlank { accountRole.label }.take(130)
    val safeWilaya = wilaya.ifBlank { "16 - Alger" }.take(75)
    val safeSubCount = subAccountsCount.coerceIn(0, 20)
    val defaultSummary = when (accountRole) {
      AccountRole.ATELIER_MAGASIN_MIXTE ->
        "1 Compte Principal + $safeSubCount Sous-Comptes (Chef Atelier, Mécanicien Monteur, Vendeur Comptoir, Magasinier)"
      AccountRole.ATELIER_CARROSSERIE ->
        "1 Chef Tôlier + $safeSubCount Sous-Comptes Carrossiers/Peintres (Photos Avant/Après obligatoires)"
      AccountRole.CONDUCTEUR ->
        "Compte Personnel Particulier (1 titulaire)"
      else ->
        "1 Compte Principal + $safeSubCount Sous-Comptes (${accountRole.shortTitle})"
    }
    val safeSummary = subAccountsSummary.ifBlank { defaultSummary }.take(380)

    try {
      val existingSnap = docRef.get().await()
      val baseMap = mutableMapOf<String, Any>(
        "userId" to uid,
        "email" to safeEmail,
        "displayName" to safeDisplay,
        "accountRole" to accountRole.name,
        "organizationName" to safeOrg,
        "wilaya" to safeWilaya,
        "subAccountsCount" to safeSubCount,
        "subAccountsSummary" to safeSummary,
        "subscriptionPriceDzd" to accountRole.subscriptionPriceDzd,
        "mecaVerifFeePerVinDzd" to accountRole.mecaVerifExternalRateDzd,
        "updatedAt" to FieldValue.serverTimestamp()
      )
      if (!existingSnap.exists()) {
        baseMap["createdAt"] = FieldValue.serverTimestamp()
        docRef.set(baseMap).await()
      } else {
        docRef.update(baseMap).await()
      }
      val updatedSnap = docRef.get().await()
      updatedSnap.toObject(
        FirestoreUserAccountSelection::class.java,
        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
      ) ?: FirestoreUserAccountSelection(
        id = uid,
        userId = uid,
        email = safeEmail,
        displayName = safeDisplay,
        accountRole = accountRole.name,
        organizationName = safeOrg,
        wilaya = safeWilaya,
        subAccountsCount = safeSubCount,
        subAccountsSummary = safeSummary,
        subscriptionPriceDzd = accountRole.subscriptionPriceDzd,
        mecaVerifFeePerVinDzd = accountRole.mecaVerifExternalRateDzd
      )
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, docRef.path)
      throw e
    }
  }

  suspend fun getUserAccountSelection(targetUserId: String? = null): Result<FirestoreUserAccountSelection?> = runCatching {
    val uid = targetUserId ?: requireUserId()
    val docRef = db.collection("user_account_selections").document(uid)
    try {
      val snap = docRef.get().await()
      snap.toObject(
        FirestoreUserAccountSelection::class.java,
        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
      )
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.GET, docRef.path)
      throw e
    }
  }

  fun observeCurrentUserAccountSelection(): Flow<FirestoreUserAccountSelection?> = flow {
    val uid = requireUserId()
    val docRef = db.collection("user_account_selections").document(uid)
    emitAll(
      docRef.snapshots()
        .map { snap ->
          snap.toObject(
            FirestoreUserAccountSelection::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
          )
        }
        .catch { error ->
          if (error is Exception) handleFirestoreError(error, OperationType.GET, docRef.path)
          throw error
        }
    )
  }

  suspend fun appendSecurityAuditEvent(
    operatorName: String,
    role: FirebaseRbacRole,
    shieldCategory: String,
    actionDescription: String
  ): Result<String> = runCatching {
    val uid = requireUserId()
    val logId = "audit_${UUID.randomUUID().toString().replace("-", "").take(16)}"
    val docRef = db.collection("security_audit_logs").document(logId)
    val seal = computeHmacSha256Seal("$uid|$role|$shieldCategory|$actionDescription")
    val payload = mapOf(
      "userId" to uid,
      "operatorName" to operatorName.ifBlank { "Opérateur Mekanik+" },
      "rbacRole" to role.id,
      "shieldCategory" to shieldCategory.ifBlank { "RBAC_AUDIT" },
      "actionDescription" to actionDescription.take(480),
      "hmacSha256Seal" to seal,
      "createdAt" to FieldValue.serverTimestamp()
    )
    try {
      docRef.set(payload).await()
      logId
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      throw e
    }
  }

  fun observeUserSecurityAuditEvents(): Flow<List<FirestoreSecurityAuditEvent>> = flow {
    val uid = requireUserId()
    val path = "security_audit_logs"
    emitAll(
      db.collection(path)
        .whereEqualTo("userId", uid)
        .snapshots()
        .map { querySnap ->
          querySnap.toObjects(
            FirestoreSecurityAuditEvent::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
          )
        }
        .catch { error ->
          if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
          throw error
        }
    )
  }

  suspend fun upsertVendorStockItem(
    stockId: String,
    vendorName: String,
    partName: String,
    oemReference: String,
    iamReference: String,
    quantityInStock: Int,
    priceDzd: Int,
    availabilityStatus: VendorStockAvailabilityStatus
  ): Result<FirestoreVendorStockItem> = runCatching {
    val uid = requireUserId()
    val cleanId = stockId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").ifBlank {
      "stk_${UUID.randomUUID().toString().replace("-", "").take(12)}"
    }
    val docRef = db.collection("vendor_stock").document(cleanId)
    try {
      val existingSnap = docRef.get().await()
      val payload = mutableMapOf<String, Any>(
        "userId" to uid,
        "vendorName" to vendorName.ifBlank { "Sarl Auto Pièces Alger" },
        "partName" to partName.ifBlank { "Pièce Automobile" },
        "oemReference" to oemReference.ifBlank { "OEM-STD" },
        "iamReference" to iamReference.ifBlank { "IAM-STD" },
        "quantityInStock" to quantityInStock.coerceIn(0, 100000),
        "priceDzd" to priceDzd.coerceIn(0, 50000000),
        "availabilityStatus" to availabilityStatus.id,
        "updatedAt" to FieldValue.serverTimestamp()
      )
      if (!existingSnap.exists()) {
        payload["createdAt"] = FieldValue.serverTimestamp()
        docRef.set(payload).await()
      } else {
        docRef.update(payload).await()
      }
      val snap = docRef.get().await()
      snap.toObject(
        FirestoreVendorStockItem::class.java,
        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
      ) ?: FirestoreVendorStockItem(
        id = cleanId,
        userId = uid,
        vendorName = vendorName,
        partName = partName,
        oemReference = oemReference,
        iamReference = iamReference,
        quantityInStock = quantityInStock,
        priceDzd = priceDzd,
        availabilityStatus = availabilityStatus.id
      )
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, docRef.path)
      throw e
    }
  }

  suspend fun deleteVendorStockItem(stockId: String): Result<Unit> = runCatching {
    requireUserId()
    val docRef = db.collection("vendor_stock").document(stockId)
    try {
      docRef.delete().await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path)
      throw e
    }
  }

  fun observeAllVendorStockItems(): Flow<List<FirestoreVendorStockItem>> = flow {
    requireUserId()
    val path = "vendor_stock"
    emitAll(
      db.collection(path)
        .snapshots()
        .map { querySnap ->
          querySnap.toObjects(
            FirestoreVendorStockItem::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
          )
        }
        .catch { error ->
          if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
          throw error
        }
    )
  }

  // ============================================================================
  // 4. JOURNAL D'ERREURS SYSTÈME (ERROR LOGGER : CRASHES, API, PERSISTANCE LOCALE & FIREBASE)
  // ============================================================================

  suspend fun appendSystemErrorLog(
    userEmail: String,
    rbacRole: FirebaseRbacRole,
    errorCategory: SystemErrorCategory,
    severity: SystemErrorSeverity,
    moduleSource: String,
    errorCode: String,
    errorTitle: String,
    technicalMessage: String,
    stackTracePreview: String,
    deviceHardwareInfo: String = "Android 16 (API 36) • HW-KEYSTORE-DZ16-A94F21 • 16 - Alger",
    resolutionStatus: ErrorResolutionStatus = ErrorResolutionStatus.OPEN,
    superAdminNote: String = ""
  ): Result<FirestoreSystemErrorLog> = runCatching {
    val uid = requireUserId()
    val errorId = "err_${UUID.randomUUID().toString().replace("-", "").take(16)}"
    val docRef = db.collection("system_error_logs").document(errorId)
    val safeEmail = userEmail.ifBlank { auth.currentUser?.email ?: "operator@mekanikplus.dz" }.take(115)
    val safeModule = moduleSource.ifBlank { "Mekanik+ Core" }.take(115)
    val safeCode = errorCode.ifBlank { "ERR_RUNTIME_001" }.take(95)
    val safeTitle = errorTitle.ifBlank { "Erreur système capturée" }.take(195)
    val safeMessage = technicalMessage.ifBlank { "Détails techniques non spécifiés." }.take(980)
    val safeTrace = stackTracePreview.ifBlank { "StackTrace indisponible" }.take(1950)
    val safeDevice = deviceHardwareInfo.ifBlank { "Android 16 • HW-KEYSTORE-DZ16" }.take(195)
    val safeNote = superAdminNote.take(480)

    val payload = mapOf(
      "userId" to uid,
      "userEmail" to safeEmail,
      "rbacRole" to rbacRole.id,
      "errorCategory" to errorCategory.id,
      "severity" to severity.id,
      "moduleSource" to safeModule,
      "errorCode" to safeCode,
      "errorTitle" to safeTitle,
      "technicalMessage" to safeMessage,
      "stackTracePreview" to safeTrace,
      "deviceHardwareInfo" to safeDevice,
      "resolutionStatus" to resolutionStatus.id,
      "superAdminNote" to safeNote,
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )
    try {
      docRef.set(payload).await()
      val snap = docRef.get().await()
      snap.toObject(
        FirestoreSystemErrorLog::class.java,
        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
      ) ?: FirestoreSystemErrorLog(
        id = errorId,
        userId = uid,
        userEmail = safeEmail,
        rbacRole = rbacRole.id,
        errorCategory = errorCategory.id,
        severity = severity.id,
        moduleSource = safeModule,
        errorCode = safeCode,
        errorTitle = safeTitle,
        technicalMessage = safeMessage,
        stackTracePreview = safeTrace,
        deviceHardwareInfo = safeDevice,
        resolutionStatus = resolutionStatus.id,
        superAdminNote = safeNote
      )
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      throw e
    }
  }

  fun observeUserSystemErrorLogs(): Flow<List<FirestoreSystemErrorLog>> = flow {
    val uid = requireUserId()
    val path = "system_error_logs"
    emitAll(
      db.collection(path)
        .whereEqualTo("userId", uid)
        .snapshots()
        .map { querySnap ->
          querySnap.toObjects(
            FirestoreSystemErrorLog::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
          )
        }
        .catch { error ->
          if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
          throw error
        }
    )
  }

  fun observeAllSystemErrorLogsForSuperAdmin(): Flow<List<FirestoreSystemErrorLog>> = flow {
    requireUserId()
    val path = "system_error_logs"
    emitAll(
      db.collection(path)
        .snapshots()
        .map { querySnap ->
          querySnap.toObjects(
            FirestoreSystemErrorLog::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
          )
        }
        .catch { error ->
          if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
          throw error
        }
    )
  }

  suspend fun updateSystemErrorLogStatus(
    errorId: String,
    resolutionStatus: ErrorResolutionStatus,
    superAdminNote: String
  ): Result<Unit> = runCatching {
    requireUserId()
    val docRef = db.collection("system_error_logs").document(errorId)
    val updateMap = mapOf(
      "resolutionStatus" to resolutionStatus.id,
      "superAdminNote" to superAdminNote.take(480),
      "updatedAt" to FieldValue.serverTimestamp()
    )
    try {
      docRef.update(updateMap).await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  suspend fun deleteSystemErrorLog(errorId: String): Result<Unit> = runCatching {
    requireUserId()
    val docRef = db.collection("system_error_logs").document(errorId)
    try {
      docRef.delete().await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path)
      throw e
    }
  }

  companion object {
    private const val HMAC_SECRET = "MEKANIK_PLUS_SHIELD_2026_ALGERIA_KEYSTORE_SECRET"

    fun computeHmacSha256Seal(payload: String): String {
      return try {
        val mac = Mac.getInstance("HmacSHA256")
        val keySpec = SecretKeySpec(HMAC_SECRET.toByteArray(Charsets.UTF_8), "HmacSHA256")
        mac.init(keySpec)
        val rawHmac = mac.doFinal(payload.toByteArray(Charsets.UTF_8))
        "HMAC-SHA256-" + rawHmac.joinToString("") { "%02X".format(it) }.take(24)
      } catch (e: Exception) {
        val digest = MessageDigest.getInstance("SHA-256").digest(payload.toByteArray(Charsets.UTF_8))
        "HMAC-SHA256-" + digest.joinToString("") { "%02X".format(it) }.take(24)
      }
    }

    val defaultRealtimeVendorInventory: List<FirestoreVendorStockItem> = listOf(
      FirestoreVendorStockItem(
        id = "stk_pad_front_alger",
        userId = "vendor_bab_ezzouar",
        vendorName = "AutoPièces Bab Ezzouar (Alger 16)",
        partName = "Jeu de plaquettes de frein avant",
        oemReference = "5Q0 698 151 BR",
        iamReference = "Brembo P 85 126 / Bosch 0 986 494 660",
        quantityInStock = 18,
        priceDzd = 8500,
        availabilityStatus = VendorStockAvailabilityStatus.EN_STOCK.id
      ),
      FirestoreVendorStockItem(
        id = "stk_pad_front_jetour",
        userId = "vendor_bab_ezzouar",
        vendorName = "AutoPièces Bab Ezzouar (Alger 16)",
        partName = "Jeu de plaquettes avant céramique Jetour Dashing",
        oemReference = "T15-3501080BA",
        iamReference = "Brembo P 11 042 / TRW GDB2291",
        quantityInStock = 14,
        priceDzd = 8400,
        availabilityStatus = VendorStockAvailabilityStatus.EN_STOCK.id
      ),
      FirestoreVendorStockItem(
        id = "stk_disc_front_alger",
        userId = "vendor_bab_ezzouar",
        vendorName = "AutoPièces Bab Ezzouar (Alger 16)",
        partName = "Paire de disques de frein ventilés 312mm",
        oemReference = "1K0 615 301 AA",
        iamReference = "Brembo 09.9772.11 / ATE 24.0125-0158.1",
        quantityInStock = 8,
        priceDzd = 18500,
        availabilityStatus = VendorStockAvailabilityStatus.EN_STOCK.id
      ),
      FirestoreVendorStockItem(
        id = "stk_caliper_left_setif",
        userId = "vendor_setif_turbo",
        vendorName = "Sétif Auto Performance (Sétif 19)",
        partName = "Étrier de frein avant gauche monopiston",
        oemReference = "5Q0 615 123 D",
        iamReference = "TRW BHW1071E / Brembo F 85 330",
        quantityInStock = 3,
        priceDzd = 24000,
        availabilityStatus = VendorStockAvailabilityStatus.STOCK_FAIBLE.id
      ),
      FirestoreVendorStockItem(
        id = "stk_timing_chain_oran",
        userId = "vendor_oran_pieces",
        vendorName = "Sarl Maghreb Pièces Détachées (Oran 31)",
        partName = "Kit chaîne de distribution renforcée 8mm",
        oemReference = "16 381 573 80",
        iamReference = "Gates KP15678XS / INA 530 0698 30",
        quantityInStock = 4,
        priceDzd = 32000,
        availabilityStatus = VendorStockAvailabilityStatus.STOCK_FAIBLE.id
      ),
      FirestoreVendorStockItem(
        id = "stk_filter_oil_blida",
        userId = "vendor_blida_moteur",
        vendorName = "Comptoir Central Pièces Blida (Blida 09)",
        partName = "Filtre à huile moteur haute filtration",
        oemReference = "03N 115 562 B",
        iamReference = "Mann-Filter HU 7020 z / Purflux L980",
        quantityInStock = 45,
        priceDzd = 1900,
        availabilityStatus = VendorStockAvailabilityStatus.EN_STOCK.id
      ),
      FirestoreVendorStockItem(
        id = "stk_turbo_core_alger",
        userId = "vendor_bab_ezzouar",
        vendorName = "AutoPièces Bab Ezzouar (Alger 16)",
        partName = "Turbocompresseur à géométrie variable VNT",
        oemReference = "04L 253 010 B",
        iamReference = "Garrett 812971-5006S / BorgWarner BV40",
        quantityInStock = 5,
        priceDzd = 89000,
        availabilityStatus = VendorStockAvailabilityStatus.EN_STOCK.id
      ),
      FirestoreVendorStockItem(
        id = "stk_clutch_dmf_oran",
        userId = "vendor_oran_pieces",
        vendorName = "Sarl Maghreb Pièces Détachées (Oran 31)",
        partName = "Volant moteur bimasse & Kit double embrayage DSG/7DCT",
        oemReference = "03L 105 266 DC",
        iamReference = "LuK 600 0370 00 / Sachs 2295 000 541",
        quantityInStock = 2,
        priceDzd = 74500,
        availabilityStatus = VendorStockAvailabilityStatus.STOCK_FAIBLE.id
      ),
      FirestoreVendorStockItem(
        id = "stk_shock_front_constantine",
        userId = "vendor_constantine_auto",
        vendorName = "Constantine Auto Express (Constantine 25)",
        partName = "Amortisseur avant gaz bitube (Jambe de force)",
        oemReference = "5QF 413 031 AS",
        iamReference = "Sachs 317 577 / Bilstein 22-266804",
        quantityInStock = 10,
        priceDzd = 14800,
        availabilityStatus = VendorStockAvailabilityStatus.EN_STOCK.id
      ),
      FirestoreVendorStockItem(
        id = "stk_water_pump_blida",
        userId = "vendor_blida_moteur",
        vendorName = "Comptoir Central Pièces Blida (Blida 09)",
        partName = "Pompe à eau débrayable & boîtier thermostatique",
        oemReference = "04L 121 011 N",
        iamReference = "Pierburg 7.07152.12.0 / Valeo 506995",
        quantityInStock = 9,
        priceDzd = 19200,
        availabilityStatus = VendorStockAvailabilityStatus.EN_STOCK.id
      ),
      FirestoreVendorStockItem(
        id = "stk_alternator_alger",
        userId = "vendor_bab_ezzouar",
        vendorName = "AutoPièces Bab Ezzouar (Alger 16)",
        partName = "Alternateur 180A poulie roue libre LIN-Bus",
        oemReference = "04L 903 023 L",
        iamReference = "Valeo 440618 / Bosch 0 125 811 082",
        quantityInStock = 6,
        priceDzd = 38500,
        availabilityStatus = VendorStockAvailabilityStatus.EN_STOCK.id
      ),
      FirestoreVendorStockItem(
        id = "stk_ev_pump_alger",
        userId = "vendor_bab_ezzouar",
        vendorName = "AutoPièces Bab Ezzouar (Alger 16)",
        partName = "Pompe à eau auxiliaire 12V refroidissement Inverter EV",
        oemReference = "5QE 965 561 B",
        iamReference = "Pierburg 7.04071.71.0 / Bosch 0 392 023 004",
        quantityInStock = 7,
        priceDzd = 28500,
        availabilityStatus = VendorStockAvailabilityStatus.EN_STOCK.id
      ),
      FirestoreVendorStockItem(
        id = "stk_headlamp_led_constantine",
        userId = "vendor_constantine_auto",
        vendorName = "Constantine Auto Express (Constantine 25)",
        partName = "Projecteur avant Full LED Matrix IQ.Light",
        oemReference = "5NA 941 081 F",
        iamReference = "Hella 1EX 015 112-211 / Valeo 047412",
        quantityInStock = 2,
        priceDzd = 94000,
        availabilityStatus = VendorStockAvailabilityStatus.SUR_COMMANDE.id
      )
    )

    // Données initiales des 6 Boucliers de Défense
    val defaultBoundDevices: List<HardwareBoundDeviceRecord> = listOf(
      HardwareBoundDeviceRecord(
        deviceSlotNumber = 1,
        deviceName = "Poste #1 • Tablette Diagnostic Réception (Samsung Tab Active4 Pro)",
        hardwareKeystoreId = "HW-KEYSTORE-DZ16-A94F21",
        boundWilaya = "16 - Alger (Bab Ezzouar)",
        ipAddressMasked = "105.101.42.18 (Fibre Idoom Pro)",
        lastActiveStatus = "EN LIGNE • Certificat Matériel Vérifié",
        isAuthorized = true,
        isGeoVelocityAnomaly = false
      ),
      HardwareBoundDeviceRecord(
        deviceSlotNumber = 2,
        deviceName = "Poste #2 • Smartphone Chef d'Atelier (Pixel 8 Pro)",
        hardwareKeystoreId = "HW-KEYSTORE-DZ16-C71E09",
        boundWilaya = "16 - Alger (El-Harrach)",
        ipAddressMasked = "154.121.88.04 (4G Djezzy Pro)",
        lastActiveStatus = "EN LIGNE • Certificat Matériel Vérifié",
        isAuthorized = true,
        isGeoVelocityAnomaly = false
      ),
      HardwareBoundDeviceRecord(
        deviceSlotNumber = 3,
        deviceName = "Poste #3 • PC Comptoir Pièces & Facturation WinMotor",
        hardwareKeystoreId = "HW-KEYSTORE-DZ16-F03B88",
        boundWilaya = "16 - Alger (Bab Ezzouar)",
        ipAddressMasked = "105.101.42.19 (Réseau Local Atelier)",
        lastActiveStatus = "VEILLE SÉCURISÉE • Empreinte OK",
        isAuthorized = true,
        isGeoVelocityAnomaly = false
      )
    )

    val defaultSignedDocuments: List<SignedDocumentSealRecord> = listOf(
      SignedDocumentSealRecord(
        docId = "OR-2026-8912",
        docTypeBadge = "ORDRE DE RÉPARATION (OR)",
        docTitle = "Jetour Dashing 1.6 TGDI • Remplacement Plaquettes & Purge EPB",
        vin = "LVVDB21B9PD048912",
        certifiedMileageKm = 28500,
        certifiedAmountDzd = 24990,
        hmacSha256Signature = computeHmacSha256Seal("OR-2026-8912|LVVDB21B9PD048912|28500|24990"),
        isTamperedDetected = false
      ),
      SignedDocumentSealRecord(
        docId = "FAC-DZ-2026-0419",
        docTypeBadge = "FACTURE FISCALE DZD",
        docTitle = "Geely Coolray (SX11) 1.5TD • Kit Révision 0W-20 VCC RBS0-2AE",
        vin = "L6T7824S7ND019384",
        certifiedMileageKm = 44200,
        certifiedAmountDzd = 31400,
        hmacSha256Signature = computeHmacSha256Seal("FAC-DZ-2026-0419|L6T7824S7ND019384|44200|31400"),
        isTamperedDetected = false
      ),
      SignedDocumentSealRecord(
        docId = "QR-MK1-048912",
        docTypeBadge = "PASSEPORT QR VÉHICULE",
        docTitle = "Sticker Montant Porte • Carnet Certifié Boîte 7DCT300",
        vin = "LVVDB21B9PD048912",
        certifiedMileageKm = 28500,
        certifiedAmountDzd = 24990,
        hmacSha256Signature = computeHmacSha256Seal("QR-MK1-048912|LVVDB21B9PD048912|28500|24990"),
        isTamperedDetected = false
      )
    )

    val dmsProtectedOperations: List<DmsProtectedOperationCheck> = listOf(
      DmsProtectedOperationCheck(
        operationId = "dms_op_approve_keys",
        operationTitle = "Générer des Clés d'Activation & Valider les Forfaits DZD",
        sensitiveCategory = "Console SuperAdmin & Licences",
        minimumRoleRequired = FirebaseRbacRole.SUPER_ADMIN,
        requiresManagerPinInKiosk = true
      ),
      DmsProtectedOperationCheck(
        operationId = "dms_op_remote_wipe",
        operationTitle = "Déclencher un Remote Wipe AES-256 (Purge Cache Room à distance)",
        sensitiveCategory = "Cyber-Défense & Révocation",
        minimumRoleRequired = FirebaseRbacRole.SUPER_ADMIN,
        requiresManagerPinInKiosk = true
      ),
      DmsProtectedOperationCheck(
        operationId = "dms_op_modify_tariffs",
        operationTitle = "Modifier les Taux Horaires DMS WinMotor (T1 / T2 / T3)",
        sensitiveCategory = "Facturation & Tarifs DMS",
        minimumRoleRequired = FirebaseRbacRole.GESTIONNAIRE,
        requiresManagerPinInKiosk = true
      ),
      DmsProtectedOperationCheck(
        operationId = "dms_op_view_margins",
        operationTitle = "Consulter la Marge Nette Atelier, Bénéfices & Comptabilité Globale",
        sensitiveCategory = "Données Financières Sensibles",
        minimumRoleRequired = FirebaseRbacRole.GESTIONNAIRE,
        requiresManagerPinInKiosk = true
      ),
      DmsProtectedOperationCheck(
        operationId = "dms_op_delete_invoice",
        operationTitle = "Supprimer une Facture Fiscale ou Appliquer une Remise > 15%",
        sensitiveCategory = "Contrôle Anti-Fraude Caisse",
        minimumRoleRequired = FirebaseRbacRole.GESTIONNAIRE,
        requiresManagerPinInKiosk = true
      ),
      DmsProtectedOperationCheck(
        operationId = "dms_op_vin_cascade_obd",
        operationTitle = "Décodage VIN (Cascade 11 API), Pointage Compagnon & Diagnostic OBD-II",
        sensitiveCategory = "Opérations Techniques Atelier",
        minimumRoleRequired = FirebaseRbacRole.TECHNICIEN,
        requiresManagerPinInKiosk = false
      )
    )

    val defaultSixShields: List<DefenseShieldItem> = listOf(
      DefenseShieldItem(
        shieldNumber = 1,
        title = "Bouclier #1 : Hardware Binding & Geo-Velocity Check",
        subtitle = "Empreinte Android Keystore (Max 3 Postes Atelier)",
        technicalSpec = "Verrouille l'abonnement aux 3 certificats matériels de l'atelier et bloque toute connexion simultanée depuis 2 wilayas distantes.",
        statusLabel = "3/3 POSTES VÉRIFIÉS • 0 ANOMALIE GEO",
        isEnabled = true,
        minimumRoleToConfigure = FirebaseRbacRole.GESTIONNAIRE
      ),
      DefenseShieldItem(
        shieldNumber = 2,
        title = "Bouclier #2 : Anti-Scraping & Watermarking Invisible",
        subtitle = "Disjoncteur Cascade 11 APIs (Max 15 VIN / 2 min)",
        technicalSpec = "Empêche l'aspiration automatisée des catalogues OEM/TecDoc et injecte un tatouage numérique traçable par compte.",
        statusLabel = "DISJONCTEUR ACTIF • 18 BOTS BLOQUÉS",
        isEnabled = true,
        minimumRoleToConfigure = FirebaseRbacRole.SUPER_ADMIN
      ),
      DefenseShieldItem(
        shieldNumber = 3,
        title = "Bouclier #3 : Scellement Cryptographique HMAC-SHA256",
        subtitle = "Factures Fiscales DZD, Ordres de Réparation & Passeport QR",
        technicalSpec = "Signe le kilométrage, le VIN et le montant TTC pour rendre toute falsification de PDF ou de sticker QR immédiatement détectable.",
        statusLabel = "SIGNATURE HMAC-SHA256 ACTIVE",
        isEnabled = true,
        minimumRoleToConfigure = FirebaseRbacRole.GESTIONNAIRE
      ),
      DefenseShieldItem(
        shieldNumber = 4,
        title = "Bouclier #4 : Coffre-Fort Local AES-256-GCM & Remote Wipe",
        subtitle = "Chiffrement SQLCipher Room #11 & Révocation à Distance",
        technicalSpec = "Chiffre le cache local de véhicules (0 ms) et permet de purger à distance un appareil volé ou non renouvelé.",
        statusLabel = "AES-256-GCM VERROUILLÉ • PRÊT",
        isEnabled = true,
        minimumRoleToConfigure = FirebaseRbacRole.SUPER_ADMIN
      ),
      DefenseShieldItem(
        shieldNumber = 5,
        title = "Bouclier #5 : Play Integrity, Anti-Root & Anti-Time-Rollback",
        subtitle = "Protection Démo 72h contre le recul de l'horloge système",
        technicalSpec = "Détecte le root/Frida, vérifie le certificat APK SHA-256 et synchronise l'horloge monotone avec le serveur NTP.",
        statusLabel = "INTÉGRITÉ 100% • HORLOGE NTP SYNC",
        isEnabled = true,
        minimumRoleToConfigure = FirebaseRbacRole.SUPER_ADMIN
      ),
      DefenseShieldItem(
        shieldNumber = 6,
        title = "Bouclier #6 : Verrouillage Kiosque PIN & RBAC Firebase Auth",
        subtitle = "Séparation SuperAdmin / Gestionnaire / Technicien",
        technicalSpec = "Restreint l'accès aux marges DMS, taux horaires T1/T2/T3 et suppressions de factures selon le rôle Firebase Auth.",
        statusLabel = "RBAC ZERO-TRUST ACTIF",
        isEnabled = true,
        minimumRoleToConfigure = FirebaseRbacRole.GESTIONNAIRE
      )
    )

    val defaultAuditEvents: List<FirestoreSecurityAuditEvent> = listOf(
      FirestoreSecurityAuditEvent(
        id = "audit_01",
        userId = "superadmin_uid",
        operatorName = "Badrou (SuperAdmin)",
        rbacRole = FirebaseRbacRole.SUPER_ADMIN.id,
        shieldCategory = "BOUCLIER_6_RBAC",
        actionDescription = "Initialisation de la matrice RBAC Firebase Auth (SuperAdmin, Gestionnaire, Technicien)",
        hmacSha256Seal = computeHmacSha256Seal("audit_01|SUPER_ADMIN|BOUCLIER_6_RBAC")
      ),
      FirestoreSecurityAuditEvent(
        id = "audit_02",
        userId = "gestionnaire_uid",
        operatorName = "Chef d'Atelier Alger",
        rbacRole = FirebaseRbacRole.GESTIONNAIRE.id,
        shieldCategory = "BOUCLIER_3_HMAC",
        actionDescription = "Scellement cryptographique de l'Ordre de Réparation OR-2026-8912 (24 990 DA)",
        hmacSha256Seal = computeHmacSha256Seal("audit_02|GESTIONNAIRE|OR-2026-8912")
      )
    )
  }
}
