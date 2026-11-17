package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.ui.viewmodel.MainNavigationTab
import com.example.ui.viewmodel.MekanikViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun `read string from context and launch MainActivity across all tabs and modals`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("DMS Mécanicauto+", appName)

    composeTestRule.waitForIdle()

    // Test ViewModel initialization and all tabs + modals directly
    val vm = MekanikViewModel()
    MainNavigationTab.entries.forEach { tab ->
      vm.setNavigationTab(tab)
    }
    vm.openPartLabelVisionScanner()
    assertEquals(true, vm.isPartLabelVisionScannerOpen.value)
    vm.closePartLabelVisionScanner()

    vm.openBarcodeScannerModal()
    val firstInvoiceId = vm.uiState.value.scannedPurchaseInvoices.first().id
    vm.integrateScannedInvoiceAndGenerateLabels(firstInvoiceId)
    vm.printAllThermalLabelsForInvoice("ALL")

    // Test Local Cache Strategy (Schematics, Saved Diagnostics & Client Fault History)
    vm.preloadAllSchematicsToLocalCache()
    vm.addClientFaultHistoryRecord(
      clientName = "Test Client Atelier",
      clientPhone = "0550 00 11 22",
      vehicleModel = "Volkswagen Tiguan 2.0 TDI",
      vin = "WVWZZZ5NZMW123456",
      licensePlate = "04291-120-16",
      mileageKm = 130000,
      faultTitle = "Perte puissance turbo",
      symptoms = "Mode dégradé en montée",
      affectedSystem = "Suralimentation",
      dtcCodes = "P0299",
      diagnosticConclusion = "Durite échangeur percée",
      repairAction = "Remplacement durite",
      partsReplaced = "5N0 145 832 S",
      costDzd = 18500L,
      technicianName = "Mourad",
      status = "PANNE_RESOLUE"
    )
    assert(vm.cachedSchematics.value.isNotEmpty())
    assert(vm.clientFaultHistory.value.any { it.clientName == "Test Client Atelier" })
    assert(vm.savedDiagnostics.value.isNotEmpty())
  }
}
