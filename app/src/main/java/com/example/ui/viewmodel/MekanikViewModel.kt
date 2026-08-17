package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.AiDiagnosticService
import com.example.data.repository.MekanikRepository
import com.example.ui.components.SearchTabMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MainNavigationTab(val label: String, val iconTag: String) {
  ACCUEIL("Accueil", "home"),
  RECHERCHE("Recherche", "search"),
  PIECES("Pièces & Schémas", "parts"),
  ATELIER("Atelier & RDV", "workshop"),
  AI_DIAGNOSTIC("MekanikAI", "ai")
}

data class MekanikUiState(
  val currentNavTab: MainNavigationTab = MainNavigationTab.ACCUEIL,
  val searchTabMode: SearchTabMode = SearchTabMode.VIN,
  val searchQuery: String = "WVWZZZ5NZMW123456",
  val activeVehicle: Vehicle = MekanikRepository.sampleVehicles.first(),
  val searchResults: List<Vehicle> = MekanikRepository.sampleVehicles,
  val recentSearches: List<String> = listOf("WVWZZZ5NZMW123456", "DFGA", "VF3FPHNSSMY001234", "K9K 608"),
  
  // Catalog & Exploded Diagram
  val selectedSystemCategory: SystemCategory = SystemCategory.FREINAGE,
  val activeSchema: ExplodedSchema = MekanikRepository.tiguanBrakeSchema,
  val selectedHotspotNumber: Int? = 1,
  val selectedPart: PartItem? = MekanikRepository.sampleParts.first(),
  val partsForSelectedCategory: List<PartItem> = MekanikRepository.sampleParts.filter { it.system == SystemCategory.FREINAGE },

  // Workshop & Repair Orders (Devis)
  val repairOrders: List<RepairOrder> = MekanikRepository.sampleRepairOrders,
  val activeRepairOrder: RepairOrder? = null,
  val showNewQuoteDialog: Boolean = false,
  val isOrderSigned: Boolean = false,

  // Appointments Management (Système de Gestion des Rendez-Vous)
  val appointments: List<WorkshopAppointment> = MekanikRepository.sampleAppointments,
  val showBookAppointmentDialog: Boolean = false,

  // Push Notifications Center
  val notifications: List<AppNotification> = MekanikRepository.sampleNotifications,
  val showNotificationModal: Boolean = false,

  // MekanikAI State
  val aiQuery: String = "",
  val isAiLoading: Boolean = false,
  val aiDiagnosticResult: AiDiagnosticResult? = null,
  val obdHistory: List<String> = listOf("P0299", "P0401", "P0300", "P2002"),

  // Vendors & Stores
  val selectedWilayaFilter: String = "Toutes les Wilayas",
  val vendorsList: List<Vendor> = MekanikRepository.sampleVendors,
  val vendorSearchQuery: String = "",

  // User & Subscriptions
  val dailyVinCount: Int = 3,
  val dailyVinLimit: Int = 15,
  val userPlanName: String = "Compte Atelier Pro (Alger)",
  val showSubscriptionModal: Boolean = false,
  val showVinReportModal: Boolean = false,
  val activeVinReport: VinReport? = null,
  val toastMessage: String? = null
) {
  val unreadNotificationsCount: Int get() = notifications.count { !it.isRead }
}

class MekanikViewModel(
  private val aiDiagnosticService: AiDiagnosticService = AiDiagnosticService()
) : ViewModel() {

  private val _uiState = MutableStateFlow(MekanikUiState())
  val uiState: StateFlow<MekanikUiState> = _uiState.asStateFlow()

  init {
    updateCategory(SystemCategory.FREINAGE)
  }

  fun setNavigationTab(tab: MainNavigationTab) {
    _uiState.update { it.copy(currentNavTab = tab) }
  }

  fun setSearchTabMode(mode: SearchTabMode) {
    _uiState.update { it.copy(searchTabMode = mode) }
  }

  fun onSearchQueryChanged(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun performVehicleSearch() {
    val query = _uiState.value.searchQuery.trim()
    val mode = _uiState.value.searchTabMode

    when (mode) {
      SearchTabMode.VIN -> {
        val found = MekanikRepository.findVehicleByVin(query)
        if (found != null) {
          _uiState.update {
            it.copy(
              activeVehicle = found,
              searchResults = listOf(found),
              dailyVinCount = (it.dailyVinCount + 1).coerceAtMost(it.dailyVinLimit),
              currentNavTab = MainNavigationTab.RECHERCHE
            )
          }
        }
      }
      SearchTabMode.CODE_MOTEUR -> {
        val results = MekanikRepository.findVehicleByEngineCode(query)
        if (results.isNotEmpty()) {
          _uiState.update {
            it.copy(
              activeVehicle = results.first(),
              searchResults = results,
              currentNavTab = MainNavigationTab.RECHERCHE
            )
          }
        }
      }
      SearchTabMode.MANUELLE -> {
        val results = MekanikRepository.sampleVehicles.filter {
          it.brand.contains(query, ignoreCase = true) || it.model.contains(query, ignoreCase = true)
        }.ifEmpty { MekanikRepository.sampleVehicles }
        _uiState.update {
          it.copy(
            activeVehicle = results.first(),
            searchResults = results,
            currentNavTab = MainNavigationTab.RECHERCHE
          )
        }
      }
    }
  }

  fun selectVehicle(vehicle: Vehicle) {
    _uiState.update {
      it.copy(
        activeVehicle = vehicle,
        searchQuery = vehicle.vin,
        currentNavTab = MainNavigationTab.PIECES
      )
    }
    updateCategory(SystemCategory.FREINAGE)
  }

  fun updateCategory(category: SystemCategory) {
    val filtered = MekanikRepository.sampleParts.filter { it.system == category }
    _uiState.update {
      it.copy(
        selectedSystemCategory = category,
        partsForSelectedCategory = filtered,
        selectedPart = filtered.firstOrNull(),
        selectedHotspotNumber = filtered.firstOrNull()?.schemaCalloutNumber ?: 1
      )
    }
  }

  fun selectHotspot(hotspot: ExplodedHotspot) {
    val matchingPart = MekanikRepository.sampleParts.find {
      it.id == hotspot.partId || it.schemaCalloutNumber == hotspot.calloutNumber
    }
    _uiState.update {
      it.copy(
        selectedHotspotNumber = hotspot.calloutNumber,
        selectedPart = matchingPart ?: it.selectedPart
      )
    }
  }

  fun selectPartItem(part: PartItem) {
    _uiState.update {
      it.copy(
        selectedPart = part,
        selectedHotspotNumber = part.schemaCalloutNumber
      )
    }
  }

  // --- MekanikAI Methods ---
  fun onAiQueryChanged(query: String) {
    _uiState.update { it.copy(aiQuery = query) }
  }

  fun askMekanikAi(predefinedQuery: String? = null) {
    val query = predefinedQuery ?: _uiState.value.aiQuery
    if (query.isBlank()) return

    _uiState.update {
      it.copy(
        isAiLoading = true,
        aiQuery = query
      )
    }

    viewModelScope.launch {
      val result = aiDiagnosticService.diagnoseProblem(query, _uiState.value.activeVehicle)
      _uiState.update {
        it.copy(
          isAiLoading = false,
          aiDiagnosticResult = result
        )
      }
    }
  }

  // --- Appointments System Actions ---
  fun bookAppointment(
    clientName: String,
    clientPhone: String,
    vehicleModel: String,
    plateNumber: String,
    serviceType: ServiceType,
    date: String,
    timeSlot: String,
    notes: String
  ) {
    val newApt = WorkshopAppointment(
      id = "apt_${System.currentTimeMillis()}",
      clientName = clientName.ifBlank { "Client Particulier" },
      clientPhone = clientPhone.ifBlank { "0550 00 00 00" },
      vehicleModel = vehicleModel.ifBlank { "${_uiState.value.activeVehicle.brand} ${_uiState.value.activeVehicle.model}" },
      plateNumber = plateNumber.ifBlank { "01234-121-16" },
      serviceType = serviceType,
      appointmentDate = date,
      timeSlot = timeSlot,
      workshopName = "Garage Auto Performance Alger",
      status = AppointmentStatus.EN_ATTENTE,
      estimatedDurationHours = serviceType.durationHours,
      estimatedCostDzd = serviceType.defaultPriceDzd,
      assignedMechanic = "Équipe Atelier",
      clientNotes = notes,
      createdAt = "Aujourd'hui"
    )

    val confirmationNotification = AppNotification(
      id = "notif_${System.currentTimeMillis()}",
      title = "📅 Demande de Rendez-vous Enregistrée",
      message = "Votre demande pour '${serviceType.label}' le $date ($timeSlot) a été envoyée à l'atelier. Vous recevrez une confirmation sous peu.",
      category = NotificationCategory.RDV_ATELIER,
      timestamp = "À l'instant",
      isRead = false,
      priority = NotificationPriority.HIGH
    )

    _uiState.update {
      it.copy(
        appointments = listOf(newApt) + it.appointments,
        notifications = listOf(confirmationNotification) + it.notifications,
        showBookAppointmentDialog = false,
        toastMessage = "Rendez-vous réservé avec succès pour le $date à $timeSlot !"
      )
    }
  }

  fun updateAppointmentStatus(appointmentId: String, newStatus: AppointmentStatus) {
    _uiState.update { state ->
      val updated = state.appointments.map { apt ->
        if (apt.id == appointmentId) {
          apt.copy(status = newStatus)
        } else apt
      }

      val aptItem = state.appointments.find { it.id == appointmentId }
      val statusNotif = if (newStatus == AppointmentStatus.CONFIRME && aptItem != null) {
        AppNotification(
          id = "notif_${System.currentTimeMillis()}",
          title = "✅ Rendez-vous Atelier Confirmé !",
          message = "Le Garage Auto Performance a confirmé votre prestation '${aptItem.serviceType.label}' pour le ${aptItem.appointmentDate} à ${aptItem.timeSlot}.",
          category = NotificationCategory.RDV_ATELIER,
          timestamp = "À l'instant",
          isRead = false,
          priority = NotificationPriority.HIGH
        )
      } else null

      state.copy(
        appointments = updated,
        notifications = if (statusNotif != null) listOf(statusNotif) + state.notifications else state.notifications,
        toastMessage = "Statut du rendez-vous mis à jour : ${newStatus.label}"
      )
    }
  }

  fun convertAppointmentToRepairOrder(apt: WorkshopAppointment) {
    val newOrder = RepairOrder(
      id = "or_${System.currentTimeMillis()}",
      orderNumber = "OR-2026-${(1000..9999).random()}",
      clientName = apt.clientName,
      clientPhone = apt.clientPhone,
      vehicleModel = apt.vehicleModel,
      vin = _uiState.value.activeVehicle.vin,
      plateNumber = apt.plateNumber,
      mileageKm = 88000,
      date = apt.appointmentDate,
      status = RepairStatus.EN_COURS,
      items = listOf(
        RepairItem(apt.serviceType.label, "PRESTATION-ATELIER", 1, apt.estimatedCostDzd)
      ),
      laborHours = apt.estimatedDurationHours,
      laborRatePerHourDzd = 2000,
      notes = "Généré automatiquement depuis le rendez-vous en ligne. Notes client : ${apt.clientNotes}"
    )

    updateAppointmentStatus(apt.id, AppointmentStatus.EN_COURS)

    _uiState.update {
      it.copy(
        repairOrders = listOf(newOrder) + it.repairOrders,
        toastMessage = "Rendez-vous converti en Ordre de Réparation ${newOrder.orderNumber} !"
      )
    }
  }

  // --- Push Notifications Center Actions ---
  fun markNotificationAsRead(notificationId: String) {
    _uiState.update { state ->
      val updated = state.notifications.map {
        if (it.id == notificationId) it.copy(isRead = true) else it
      }
      state.copy(notifications = updated)
    }
  }

  fun markAllNotificationsAsRead() {
    _uiState.update { state ->
      val updated = state.notifications.map { it.copy(isRead = true) }
      state.copy(notifications = updated, toastMessage = "Toutes les notifications sont marquées comme lues.")
    }
  }

  fun sendSimulatedNotification(title: String, message: String, category: NotificationCategory) {
    val newNotif = AppNotification(
      id = "notif_${System.currentTimeMillis()}",
      title = title,
      message = message,
      category = category,
      timestamp = "À l'instant",
      isRead = false,
      priority = NotificationPriority.HIGH
    )
    _uiState.update {
      it.copy(
        notifications = listOf(newNotif) + it.notifications,
        toastMessage = "Alerte push envoyée : $title"
      )
    }
  }

  // --- Workshop & Repair Order Actions ---
  fun createRepairOrder(
    clientName: String,
    clientPhone: String,
    plate: String,
    laborHours: Float,
    items: List<RepairItem>
  ) {
    val vehicle = _uiState.value.activeVehicle
    val newOrder = RepairOrder(
      id = "or_${System.currentTimeMillis()}",
      orderNumber = "OR-2026-${(1000..9999).random()}",
      clientName = clientName.ifBlank { "Client Particulier" },
      clientPhone = clientPhone.ifBlank { "0550 00 00 00" },
      vehicleModel = "${vehicle.brand} ${vehicle.model} (${vehicle.exactYear})",
      vin = vehicle.vin,
      plateNumber = plate.ifBlank { "01234-121-16" },
      mileageKm = 85000,
      date = "17 Août 2026",
      status = RepairStatus.EN_COURS,
      items = items.ifEmpty {
        listOf(
          RepairItem("Plaquettes de frein avant Brembo", "5Q0 698 151 BR", 1, 8500),
          RepairItem("Disques avant 312mm High Carbon", "1K0 615 301 AA", 1, 18500)
        )
      },
      laborHours = laborHours,
      laborRatePerHourDzd = 2000,
      notes = "Contrôle technique et essai routier effectués."
    )

    _uiState.update {
      it.copy(
        repairOrders = listOf(newOrder) + it.repairOrders,
        showNewQuoteDialog = false,
        toastMessage = "Devis ${newOrder.orderNumber} créé avec succès !"
      )
    }
  }

  fun signRepairOrder(orderId: String, signatureName: String) {
    _uiState.update { state ->
      val updated = state.repairOrders.map { order ->
        if (order.id == orderId) {
          order.copy(isSigned = true, signatureName = signatureName)
        } else order
      }
      state.copy(
        repairOrders = updated,
        toastMessage = "Ordre de réparation validé et signé numériquement."
      )
    }
  }

  // --- Vendors Filter ---
  fun onWilayaSelected(wilaya: String) {
    val filtered = if (wilaya == "Toutes les Wilayas") {
      MekanikRepository.sampleVendors
    } else {
      MekanikRepository.sampleVendors.filter { it.wilaya.contains(wilaya.take(2)) }
    }
    _uiState.update {
      it.copy(
        selectedWilayaFilter = wilaya,
        vendorsList = filtered.ifEmpty { MekanikRepository.sampleVendors }
      )
    }
  }

  fun generateVinReport(vin: String) {
    val vehicle = MekanikRepository.findVehicleByVin(vin) ?: _uiState.value.activeVehicle
    val report = VinReport(
      vin = vehicle.vin,
      brandModel = "${vehicle.brand} ${vehicle.model} ${vehicle.displacement}",
      engineNumber = "${vehicle.engineCode}*194821",
      gearboxCode = vehicle.gearbox,
      firstRegistrationDate = "14/03/${vehicle.exactYear}",
      technicalControlStatus = "Conforme (Validé jusqu'en 03/2027)",
      mileageHistory = listOf(
        MileageRecord("12/03/2022", 15400, "Concessionnaire Agréé"),
        MileageRecord("20/07/2024", 52300, "Contrôle Technique Wilaya 16"),
        MileageRecord("10/08/2026", 78400, "Atelier Partenaire Mekanik+")
      ),
      recallCampaigns = listOf(
        "Rappel Constructeur 23Z4 (Mise à jour logicielle calculateur moteur - Effectué)",
        "Contrôle étanchéité raccord haute pression (Conforme)"
      ),
      reportToken = "MEK-REP-${(10000..99999).random()}-DZ"
    )
    _uiState.update {
      it.copy(
        activeVinReport = report,
        showVinReportModal = true
      )
    }
  }

  fun dismissModals() {
    _uiState.update {
      it.copy(
        showSubscriptionModal = false,
        showVinReportModal = false,
        showNewQuoteDialog = false,
        showBookAppointmentDialog = false,
        showNotificationModal = false,
        toastMessage = null
      )
    }
  }

  fun toggleSubscriptionModal(show: Boolean) {
    _uiState.update { it.copy(showSubscriptionModal = show) }
  }

  fun toggleNewQuoteDialog(show: Boolean) {
    _uiState.update { it.copy(showNewQuoteDialog = show) }
  }

  fun toggleBookAppointmentDialog(show: Boolean) {
    _uiState.update { it.copy(showBookAppointmentDialog = show) }
  }

  fun toggleNotificationModal(show: Boolean) {
    _uiState.update { it.copy(showNotificationModal = show) }
  }
}
