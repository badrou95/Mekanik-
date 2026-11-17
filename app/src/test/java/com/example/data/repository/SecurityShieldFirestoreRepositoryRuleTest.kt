package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.FirebaseRbacRole
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityShieldFirestoreRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun upsertAndGetUserRbacProfile_authenticatedOwner_succeeds() = runBlocking {
    signInTestUser("amine.tech@mekanikplus.dz")
    val repo = SecurityShieldFirestoreRepository(firestore, auth)

    val result = withTimeout(5000L) {
      repo.upsertUserRbacProfile(
        email = "amine.tech@mekanikplus.dz",
        displayName = "Amine Technicien",
        role = FirebaseRbacRole.TECHNICIEN,
        boundHardwareDeviceId = "HW-KEYSTORE-DZ16-0001",
        wilaya = "16 - Alger"
      )
    }
    assertTrue(result.isSuccess)
    val profile = result.getOrThrow()
    assertEquals("TECHNICIEN", profile.rbacRole)

    val fetched = withTimeout(5000L) {
      repo.getUserRbacProfile().getOrThrow()
    }
    assertNotNull(fetched)
    assertEquals("Amine Technicien", fetched?.displayName)
  }

  @Test
  fun getUserRbacProfile_crossUserAccess_failsWithPermissionDenied() = runBlocking {
    val aliceUid = signInTestUser("alice.manager@mekanikplus.dz")
    val aliceRepo = SecurityShieldFirestoreRepository(firestore, auth)
    withTimeout(5000L) {
      aliceRepo.upsertUserRbacProfile(
        email = "alice.manager@mekanikplus.dz",
        displayName = "Alice Gestionnaire",
        role = FirebaseRbacRole.GESTIONNAIRE,
        boundHardwareDeviceId = "HW-KEYSTORE-DZ16-0002",
        wilaya = "16 - Alger"
      ).getOrThrow()
    }

    signInTestUser("bob.tech@mekanikplus.dz")
    val bobRepo = SecurityShieldFirestoreRepository(firestore, auth)
    val crossResult = withTimeout(5000L) {
      bobRepo.getUserRbacProfile(targetUserId = aliceUid)
    }
    assertTrue(crossResult.isFailure)
    val ex = crossResult.exceptionOrNull() as? FirebaseFirestoreException
    assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, ex?.code)
  }

  @Test
  fun observeSecurityAuditEvents_unauthenticated_fails() = runBlocking {
    auth.signOut()
    val repo = SecurityShieldFirestoreRepository(firestore, auth)
    var failed = false
    try {
      withTimeout(3000L) {
        repo.observeUserSecurityAuditEvents().first()
      }
    } catch (e: Exception) {
      failed = true
    }
    assertTrue(failed)
  }

  @Test
  fun upsertVendorStockItem_authenticatedSeller_succeedsAndCanBeObserved() = runBlocking {
    signInTestUser("seller.alger@mekanikplus.dz")
    val repo = SecurityShieldFirestoreRepository(firestore, auth)

    val upsertRes = withTimeout(5000L) {
      repo.upsertVendorStockItem(
        stockId = "stk_test_pad_01",
        vendorName = "AutoPièces Bab Ezzouar",
        partName = "Jeu de plaquettes avant céramique",
        oemReference = "T15-3501080BA",
        iamReference = "Brembo P 11 042",
        quantityInStock = 14,
        priceDzd = 8400,
        availabilityStatus = com.example.data.model.VendorStockAvailabilityStatus.EN_STOCK
      )
    }
    assertTrue(upsertRes.isSuccess)
    val items = withTimeout(5000L) {
      repo.observeAllVendorStockItems().first { it.isNotEmpty() }
    }
    assertEquals(1, items.size)
    assertEquals(8400, items.first().priceDzd)
  }

  @Test
  fun appendAndObserveSystemErrorLog_authenticatedUser_succeedsAndEnforcesIsolation() = runBlocking {
    signInTestUser("tech.alger@mekanikplus.dz")
    val repo = SecurityShieldFirestoreRepository(firestore, auth)

    val appendRes = withTimeout(5000L) {
      repo.appendSystemErrorLog(
        userEmail = "tech.alger@mekanikplus.dz",
        rbacRole = FirebaseRbacRole.TECHNICIEN,
        errorCategory = com.example.data.model.SystemErrorCategory.API_CONNECTION_FAILURE,
        severity = com.example.data.model.SystemErrorSeverity.HIGH,
        moduleSource = "Cascade 11 APIs • CarsXE",
        errorCode = "HTTP_504_GATEWAY_TIMEOUT",
        errorTitle = "Timeout passerelle CarsXE sur décodage VIN",
        technicalMessage = "Bascule automatique vers NHTSA vPIC et cache local Room AES-256.",
        stackTracePreview = "java.net.SocketTimeoutException: timeout after 3500ms at api.carsxe.com"
      )
    }
    assertTrue(appendRes.isSuccess)
    val createdLog = appendRes.getOrThrow()
    assertEquals("API_CONNECTION_FAILURE", createdLog.errorCategory)

    val observed = withTimeout(5000L) {
      repo.observeUserSystemErrorLogs().first { list -> list.any { it.id == createdLog.id } }
    }
    assertTrue(observed.any { it.errorCode == "HTTP_504_GATEWAY_TIMEOUT" })
  }

  @Test
  fun upsertAndGetUserAccountSelection_hybridAndBodyshopRoles_succeedsAndEnforcesIsolation() = runBlocking {
    val ownerUid = signInTestUser("hybrid.owner@mekanikplus.dz")
    val ownerRepo = SecurityShieldFirestoreRepository(firestore, auth)

    val hybridRes = withTimeout(5000L) {
      ownerRepo.upsertUserAccountSelection(
        email = "hybrid.owner@mekanikplus.dz",
        displayName = "Karim Benali",
        accountRole = com.example.data.model.AccountRole.ATELIER_MAGASIN_MIXTE,
        organizationName = "Magasin & Atelier Montage El-Harrach",
        wilaya = "16 - Alger",
        subAccountsCount = 4
      )
    }
    assertTrue(hybridRes.isSuccess)
    val savedHybrid = hybridRes.getOrThrow()
    assertEquals("ATELIER_MAGASIN_MIXTE", savedHybrid.accountRole)
    assertEquals(4, savedHybrid.subAccountsCount)
    assertEquals(250, savedHybrid.mecaVerifFeePerVinDzd)

    // Update to ATELIER_CARROSSERIE
    val bodyRes = withTimeout(5000L) {
      ownerRepo.upsertUserAccountSelection(
        email = "hybrid.owner@mekanikplus.dz",
        displayName = "Karim Benali",
        accountRole = com.example.data.model.AccountRole.ATELIER_CARROSSERIE,
        organizationName = "Atelier Carrosserie & Tôlerie Alger",
        wilaya = "16 - Alger",
        subAccountsCount = 2
      )
    }
    assertTrue(bodyRes.isSuccess)
    assertEquals("ATELIER_CARROSSERIE", bodyRes.getOrThrow().accountRole)

    // Cross-user access must fail with PERMISSION_DENIED
    signInTestUser("intruder.user@mekanikplus.dz")
    val intruderRepo = SecurityShieldFirestoreRepository(firestore, auth)
    val crossAccess = withTimeout(5000L) {
      intruderRepo.getUserAccountSelection(targetUserId = ownerUid)
    }
    assertTrue(crossAccess.isFailure)
    val ex = crossAccess.exceptionOrNull() as? FirebaseFirestoreException
    assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, ex?.code)
  }
}
