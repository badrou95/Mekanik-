package com.example.data.model

import androidx.compose.ui.graphics.vector.ImageVector

enum class WorkshopDeviceType(val label: String, val iconDescription: String) {
  SMARTPHONE("Smartphone Android / iOS", "phone_android"),
  TABLETTE("Tablette Diagnostic / Réception", "tablet_android"),
  PC_DIAG("PC Valise / Poste Diagnostic", "laptop")
}

enum class ConnectedDeviceStatus(val label: String, val badgeColorHex: Long) {
  ACTIF("En ligne • Actif", 0xFF00E676),
  EN_ATTENTE_2FA("En attente 2FA", 0xFFFF7A00),
  DECONNECTE("Déconnecté / Révoqué", 0xFFE53935)
}

data class WorkshopConnectedDevice(
  val id: String,
  val deviceName: String,
  val deviceModel: String,
  val deviceType: WorkshopDeviceType = WorkshopDeviceType.SMARTPHONE,
  val osVersion: String = "Android 14",
  val ipLocation: String = "105.101.44.12 • Alger (16)",
  val linkedDate: String,
  val lastActiveTime: String = "En ligne actuellement",
  val isCurrentDevice: Boolean = false,
  val status: ConnectedDeviceStatus = ConnectedDeviceStatus.ACTIF,
  val assignedTechnicianName: String? = null
)

data class Workshop2FASecurityConfig(
  val is2faEnabled: Boolean = true,
  val authenticatorAppName: String = "Google Authenticator / TOTP",
  val secretKey: String = "MEK2-FA99-ATEL-8842",
  val qrCodeUri: String = "otpauth://totp/MekanikPlus:AtelierAutoPerformance?secret=MEK2FA99ATEL8842&issuer=MekanikPlus",
  val maxAllowedDevices: Int = 3, // Strictly 3 devices max for Workshop
  val connectedDevices: List<WorkshopConnectedDevice> = listOf(
    WorkshopConnectedDevice(
      id = "dev_01",
      deviceName = "Samsung Galaxy S24 Ultra",
      deviceModel = "SM-S928B (Atelier Principal)",
      deviceType = WorkshopDeviceType.SMARTPHONE,
      osVersion = "Android 14 / One UI 6.1",
      ipLocation = "105.101.44.12 • Alger Centre (16)",
      linkedDate = "01 Août 2026",
      lastActiveTime = "En ligne actuellement (Cet appareil)",
      isCurrentDevice = true,
      status = ConnectedDeviceStatus.ACTIF,
      assignedTechnicianName = "Badreddine Mekanik (Gérant)"
    ),
    WorkshopConnectedDevice(
      id = "dev_02",
      deviceName = "Xiaomi Redmi Note 13 Pro+",
      deviceModel = "23090RA98G (Pont 2 Mécanique)",
      deviceType = WorkshopDeviceType.SMARTPHONE,
      osVersion = "Android 14 / HyperOS",
      ipLocation = "105.101.44.12 • Réseau Wi-Fi Atelier",
      linkedDate = "10 Août 2026",
      lastActiveTime = "Il y a 12 min",
      isCurrentDevice = false,
      status = ConnectedDeviceStatus.ACTIF,
      assignedTechnicianName = "Karim Meziane (Mécanicien)"
    )
  ),
  val backupCodes: List<String> = listOf(
    "8492-1104",
    "3901-5582",
    "7719-2041",
    "9034-6628",
    "1289-4470"
  )
) {
  val activeDevicesCount: Int get() = connectedDevices.count { it.status == ConnectedDeviceStatus.ACTIF }
  val canAddMoreDevices: Boolean get() = activeDevicesCount < maxAllowedDevices
}

enum class WorkshopActiveView(val label: String, val subtitle: String) {
  ATELIER("Vue Atelier & Réparations", "Planning, OR, Devis DZD & Diagnostics"),
  MAGASIN_PIECES("Vue Magasin de Pièces", "Stock, Comptoir Vente & Références OEM")
}

data class WorkshopStoreLinkConfig(
  val isStoreLinked: Boolean = true,
  val storeName: String = "Magasin Pièces Auto Performance Express",
  val storeRegisterNumber: String = "RC 16/00-0987144B26",
  val storeWilaya: String = "Alger (16) • Bab Ezzouar",
  val storePhone: String = "0550 12 34 56",
  val annualSupplementPriceDzd: Int = 5000,
  val activeView: WorkshopActiveView = WorkshopActiveView.ATELIER,
  val totalPartsInStock: Int = 1840,
  val lowStockAlertsCount: Int = 14,
  val dailyCounterSalesDzd: Long = 185000L,
  val autoDeductStockOnRepairOrder: Boolean = true,
  val allowCounterCashSale: Boolean = true,
  val isActivatedOnSubscription: Boolean = true
)
