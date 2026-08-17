package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.MekanikPrimaryButton
import com.example.ui.components.MekanikSecondaryButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

enum class WorkshopTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
  RENDEZ_VOUS("Planning & RDV", Icons.Default.EventAvailable),
  ORDRES_DEVIS("Ordres & Devis (DZD)", Icons.Default.ReceiptLong)
}

@Composable
fun WorkshopScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf(WorkshopTab.RENDEZ_VOUS) }
  var appointmentStatusFilter by remember { mutableStateOf<AppointmentStatus?>(null) }
  var selectedOrderForDetails by remember { mutableStateOf<RepairOrder?>(null) }
  var showSignatureDialogForOrder by remember { mutableStateOf<RepairOrder?>(null) }
  var selectedAptForDetails by remember { mutableStateOf<WorkshopAppointment?>(null) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("workshop_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Handyman,
              contentDescription = null,
              tint = PerformanceRed,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "ESPACE ATELIER PRO",
              style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite,
                fontSize = 20.sp
              )
            )
          }
          Text(
            text = "Gestion des rendez-vous en ligne, planning et ordres de réparation",
            style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
          )
        }

        IconButton(
          onClick = {
            if (selectedTab == WorkshopTab.RENDEZ_VOUS) {
              viewModel.toggleBookAppointmentDialog(true)
            } else {
              viewModel.toggleNewQuoteDialog(true)
            }
          },
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(PerformanceRed)
            .testTag("btn_add_action_header")
        ) {
          Icon(
            Icons.Default.Add,
            contentDescription = "Ajouter",
            tint = PureWhite
          )
        }
      }
    }

    // Workshop Performance Banner
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Garage Auto Performance Alger",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = PureWhite
                )
              )
              Text(
                text = "3 Ponts Élévateurs • Taux M.O : 2 000 DA/h",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumLight,
                  fontSize = 11.5.sp
                )
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = GraphiteBlack,
              border = BorderStroke(0.5.dp, ElectricGreen)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(ElectricGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Atelier Ouvert",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = ElectricGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                  )
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Quick Action Switcher Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            WorkshopTab.entries.forEach { tab ->
              val isSelected = tab == selectedTab
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) PerformanceRed else GraphiteBlack,
                border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
                onClick = { selectedTab = tab },
                modifier = Modifier
                  .weight(1f)
                  .testTag("tab_workshop_${tab.name.lowercase()}")
              ) {
                Row(
                  modifier = Modifier.padding(vertical = 10.dp),
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    tab.icon,
                    contentDescription = null,
                    tint = if (isSelected) PureWhite else AluminumLight,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = tab.label,
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = if (isSelected) PureWhite else AluminumLight,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                      fontSize = 11.5.sp
                    )
                  )
                }
              }
            }
          }
        }
      }
    }

    // Dynamic Tab Views
    when (selectedTab) {
      WorkshopTab.RENDEZ_VOUS -> {
        // Appointments Section
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "PLANNING DES RÉSERVATIONS CLIENTS (${uiState.appointments.size})",
              style = MaterialTheme.typography.labelMedium.copy(
                color = AluminumMuted,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
              )
            )

            TextButton(
              onClick = { viewModel.toggleBookAppointmentDialog(true) },
              contentPadding = PaddingValues(0.dp)
            ) {
              Text(
                text = "+ Prendre RDV",
                color = MechanicalOrange,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
        }

        // Appointment Filter Chips
        item {
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            item {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (appointmentStatusFilter == null) PerformanceRed else MetalSurface,
                border = BorderStroke(1.dp, if (appointmentStatusFilter == null) PerformanceRed else MetalBorder),
                onClick = { appointmentStatusFilter = null }
              ) {
                Text(
                  text = "Tous (${uiState.appointments.size})",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  ),
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
              }
            }

            items(AppointmentStatus.entries) { status ->
              val isSelected = appointmentStatusFilter == status
              val count = uiState.appointments.count { it.status == status }
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Color(status.colorHex) else MetalSurface,
                border = BorderStroke(1.dp, if (isSelected) Color(status.colorHex) else MetalBorder),
                onClick = { appointmentStatusFilter = if (isSelected) null else status }
              ) {
                Text(
                  text = "${status.label} ($count)",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) GraphiteBlack else AluminumLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  ),
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
              }
            }
          }
        }

        val filteredApts = if (appointmentStatusFilter == null) {
          uiState.appointments
        } else {
          uiState.appointments.filter { it.status == appointmentStatusFilter }
        }

        items(filteredApts) { apt ->
          AppointmentCard(
            appointment = apt,
            onConfirm = { viewModel.updateAppointmentStatus(apt.id, AppointmentStatus.CONFIRME) },
            onConvertToOrder = { viewModel.convertAppointmentToRepairOrder(apt) },
            onViewDetails = { selectedAptForDetails = apt }
          )
        }
      }

      WorkshopTab.ORDRES_DEVIS -> {
        // Orders List Header
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "ORDRES DE RÉPARATION & DEVIS EN COURS (${uiState.repairOrders.size})",
              style = MaterialTheme.typography.labelMedium.copy(
                color = AluminumMuted,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
              )
            )

            TextButton(
              onClick = { viewModel.toggleNewQuoteDialog(true) },
              contentPadding = PaddingValues(0.dp)
            ) {
              Text(
                text = "+ Nouveau Devis",
                color = PerformanceRedGlow,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
        }

        // List of Repair Orders
        items(uiState.repairOrders) { order ->
          RepairOrderCard(
            order = order,
            onViewDetails = { selectedOrderForDetails = order },
            onSignOrder = { showSignatureDialogForOrder = order }
          )
        }
      }
    }
  }

  // Dialog to Book New Online Appointment
  if (uiState.showBookAppointmentDialog) {
    BookAppointmentDialog(
      activeVehicle = uiState.activeVehicle,
      onDismiss = { viewModel.toggleBookAppointmentDialog(false) },
      onConfirm = { clientName, clientPhone, vehicleModel, plate, serviceType, date, timeSlot, notes ->
        viewModel.bookAppointment(
          clientName,
          clientPhone,
          vehicleModel,
          plate,
          serviceType,
          date,
          timeSlot,
          notes
        )
      }
    )
  }

  // Dialog to Create New Quote (Devis)
  if (uiState.showNewQuoteDialog) {
    NewQuoteDialog(
      activeVehicleModel = "${uiState.activeVehicle.brand} ${uiState.activeVehicle.model}",
      onDismiss = { viewModel.toggleNewQuoteDialog(false) },
      onConfirm = { clientName, clientPhone, plate, laborHours, items ->
        viewModel.createRepairOrder(clientName, clientPhone, plate, laborHours, items)
      }
    )
  }

  // Dialog for Digital Signature
  if (showSignatureDialogForOrder != null) {
    val order = showSignatureDialogForOrder!!
    SignatureDialog(
      orderNumber = order.orderNumber,
      clientName = order.clientName,
      totalDzd = order.netTotalDzd,
      onDismiss = { showSignatureDialogForOrder = null },
      onSign = { signName ->
        viewModel.signRepairOrder(order.id, signName)
        showSignatureDialogForOrder = null
      }
    )
  }

  // Detail Modal for Order
  if (selectedOrderForDetails != null) {
    val order = selectedOrderForDetails!!
    OrderDetailModal(
      order = order,
      onDismiss = { selectedOrderForDetails = null },
      onSign = {
        showSignatureDialogForOrder = order
        selectedOrderForDetails = null
      }
    )
  }

  // Detail Modal for Appointment
  if (selectedAptForDetails != null) {
    val apt = selectedAptForDetails!!
    AppointmentDetailModal(
      appointment = apt,
      onDismiss = { selectedAptForDetails = null },
      onConfirm = {
        viewModel.updateAppointmentStatus(apt.id, AppointmentStatus.CONFIRME)
        selectedAptForDetails = null
      },
      onConvertToOrder = {
        viewModel.convertAppointmentToRepairOrder(apt)
        selectedAptForDetails = null
      }
    )
  }
}

@Composable
private fun AppointmentCard(
  appointment: WorkshopAppointment,
  onConfirm: () -> Unit,
  onConvertToOrder: () -> Unit,
  onViewDetails: () -> Unit
) {
  val statusColor = Color(appointment.status.colorHex)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onViewDetails)
      .testTag("appointment_${appointment.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            Icons.Default.CalendarMonth,
            contentDescription = null,
            tint = PerformanceRed,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${appointment.appointmentDate} • ${appointment.timeSlot}",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = PureWhite,
              fontSize = 13.5.sp
            )
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = statusColor.copy(alpha = 0.2f),
          border = BorderStroke(0.5.dp, statusColor)
        ) {
          Text(
            text = appointment.status.label,
            style = MaterialTheme.typography.labelSmall.copy(
              color = statusColor,
              fontWeight = FontWeight.Bold,
              fontSize = 9.5.sp
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = appointment.serviceType.label,
        style = MaterialTheme.typography.bodyLarge.copy(
          fontWeight = FontWeight.Black,
          color = OffWhite,
          fontSize = 14.5.sp
        )
      )

      Text(
        text = "${appointment.vehicleModel} • Immat : ${appointment.plateNumber}",
        style = MaterialTheme.typography.bodyMedium.copy(
          color = AluminumLight,
          fontSize = 12.sp
        )
      )

      Text(
        text = "Client : ${appointment.clientName} (${appointment.clientPhone}) • Tech : ${appointment.assignedMechanic}",
        style = MaterialTheme.typography.bodySmall.copy(
          color = AluminumGray,
          fontSize = 11.sp
        )
      )

      if (appointment.clientNotes.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Note : « ${appointment.clientNotes} »",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MechanicalOrange,
            fontSize = 11.sp
          )
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Divider(color = MetalBorder, thickness = 0.5.dp)

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Est. ${appointment.estimatedCostDzd} DA (${appointment.estimatedDurationHours}h)",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = TechCyan,
            fontSize = 13.5.sp
          )
        )

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          if (appointment.status == AppointmentStatus.EN_ATTENTE) {
            Button(
              onClick = onConfirm,
              colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.height(32.dp)
            ) {
              Text(
                text = "Confirmer RDV",
                color = GraphiteBlack,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
              )
            }
          }

          if (appointment.status == AppointmentStatus.CONFIRME) {
            Button(
              onClick = onConvertToOrder,
              colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.height(32.dp)
            ) {
              Text(
                text = "Créer Devis/OR",
                color = PureWhite,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun BookAppointmentDialog(
  activeVehicle: Vehicle,
  onDismiss: () -> Unit,
  onConfirm: (String, String, String, String, ServiceType, String, String, String) -> Unit
) {
  var clientName by remember { mutableStateOf("") }
  var clientPhone by remember { mutableStateOf("") }
  var plateNumber by remember { mutableStateOf("04291-121-16") }
  var selectedService by remember { mutableStateOf(ServiceType.VIDANGE_FILTRES) }
  var selectedDate by remember { mutableStateOf("19 Août 2026") }
  var selectedTimeSlot by remember { mutableStateOf("10:00 - 11:30") }
  var notes by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "RÉSERVATION DE RENDEZ-VOUS EN LIGNE",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Black,
          color = PureWhite
        )
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 440.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "Véhicule : ${activeVehicle.brand} ${activeVehicle.model} (${activeVehicle.displacement})",
          style = MaterialTheme.typography.bodySmall.copy(color = PerformanceRedGlow, fontWeight = FontWeight.Bold)
        )

        OutlinedTextField(
          value = clientName,
          onValueChange = { clientName = it },
          label = { Text("Nom et Prénom du client") },
          placeholder = { Text("Ex: Karim Meziane") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = clientPhone,
          onValueChange = { clientPhone = it },
          label = { Text("Numéro de Téléphone") },
          placeholder = { Text("Ex: 0550 12 34 56") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = plateNumber,
          onValueChange = { plateNumber = it },
          label = { Text("Matricule / Immatriculation") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Text(
          text = "PRESTATION SOUHAITÉE :",
          style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold)
        )

        ServiceType.entries.forEach { service ->
          val isSelected = service == selectedService
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) GraphiteBlack else MetalSurface,
            border = BorderStroke(1.dp, if (isSelected) PerformanceRed else MetalBorder),
            onClick = { selectedService = service },
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = service.label,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = if (isSelected) PureWhite else OffWhite,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 11.5.sp
                ),
                modifier = Modifier.weight(1f)
              )
              Text(
                text = "${service.defaultPriceDzd} DA",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = TechCyan,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.5.sp
                )
              )
            }
          }
        }

        OutlinedTextField(
          value = selectedDate,
          onValueChange = { selectedDate = it },
          label = { Text("Date du rendez-vous") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = selectedTimeSlot,
          onValueChange = { selectedTimeSlot = it },
          label = { Text("Créneau horaire") },
          placeholder = { Text("Ex: 09:00 - 10:30, 14:00 - 15:30") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Observations particulières") },
          placeholder = { Text("Ex: voyant clignote, bruit suspect...") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      MekanikPrimaryButton(
        text = "ENREGISTRER LA RÉSERVATION",
        onClick = {
          onConfirm(
            clientName,
            clientPhone,
            "${activeVehicle.brand} ${activeVehicle.model}",
            plateNumber,
            selectedService,
            selectedDate,
            selectedTimeSlot,
            notes
          )
        }
      )
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Annuler", color = AluminumGray)
      }
    },
    containerColor = MetalCard
  )
}

@Composable
private fun AppointmentDetailModal(
  appointment: WorkshopAppointment,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
  onConvertToOrder: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "DÉTAILS DU RENDEZ-VOUS CLIENT",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Black,
          color = PureWhite
        )
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = "Client : ${appointment.clientName} (${appointment.clientPhone})",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = PureWhite)
        )
        Text(
          text = "Véhicule : ${appointment.vehicleModel} • Matricule : ${appointment.plateNumber}",
          style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight)
        )
        Text(
          text = "Date & Heure : ${appointment.appointmentDate} à ${appointment.timeSlot}",
          style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Prestation : ${appointment.serviceType.label}",
          style = MaterialTheme.typography.bodySmall.copy(color = MechanicalOrange, fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Tarif estimé : ${appointment.estimatedCostDzd} DA (Durée : ${appointment.estimatedDurationHours}h)",
          style = MaterialTheme.typography.bodySmall.copy(color = PureWhite)
        )
        if (appointment.clientNotes.isNotBlank()) {
          Text(
            text = "Remarques : « ${appointment.clientNotes} »",
            style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
          )
        }
      }
    },
    confirmButton = {
      if (appointment.status == AppointmentStatus.EN_ATTENTE) {
        MekanikPrimaryButton(
          text = "CONFIRMER CE RENDEZ-VOUS",
          onClick = onConfirm
        )
      } else {
        MekanikPrimaryButton(
          text = "CONVERTIR EN ORDRE DE RÉPARATION",
          onClick = onConvertToOrder
        )
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Fermer", color = AluminumGray)
      }
    },
    containerColor = MetalCard
  )
}

@Composable
private fun RepairOrderCard(
  order: RepairOrder,
  onViewDetails: () -> Unit,
  onSignOrder: () -> Unit
) {
  val statusColor = Color(order.status.colorHex)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onViewDetails)
      .testTag("repair_order_${order.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = order.orderNumber,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Black,
              color = PureWhite,
              fontFamily = FontFamily.Monospace
            )
          )
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = statusColor.copy(alpha = 0.2f),
            border = BorderStroke(0.5.dp, statusColor)
          ) {
            Text(
              text = order.status.label,
              style = MaterialTheme.typography.labelSmall.copy(
                color = statusColor,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              ),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Text(
          text = "${order.netTotalDzd} DA",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Black,
            color = PerformanceRedGlow,
            fontSize = 16.sp
          )
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "${order.vehicleModel} • Immat : ${order.plateNumber}",
        style = MaterialTheme.typography.bodyMedium.copy(
          fontWeight = FontWeight.SemiBold,
          color = OffWhite
        )
      )

      Text(
        text = "Client : ${order.clientName} (${order.clientPhone}) • Date : ${order.date}",
        style = MaterialTheme.typography.bodySmall.copy(
          color = AluminumGray,
          fontSize = 11.5.sp
        )
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            if (order.isSigned) Icons.Default.CheckCircle else Icons.Default.Draw,
            contentDescription = null,
            tint = if (order.isSigned) ElectricGreen else AluminumMuted,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (order.isSigned) "Signé (${order.signatureName})" else "En attente signature client",
            style = MaterialTheme.typography.bodySmall.copy(
              color = if (order.isSigned) ElectricGreen else AluminumMuted,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          )
        }

        TextButton(
          onClick = onViewDetails,
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Text(
            text = "Détails & Impression",
            color = MechanicalOrange,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    }
  }
}

@Composable
private fun NewQuoteDialog(
  activeVehicleModel: String,
  onDismiss: () -> Unit,
  onConfirm: (String, String, String, Float, List<RepairItem>) -> Unit
) {
  var clientName by remember { mutableStateOf("") }
  var clientPhone by remember { mutableStateOf("") }
  var plateNumber by remember { mutableStateOf("") }
  var laborHoursText by remember { mutableStateOf("2.0") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "CRÉATION DE DEVIS ATELIER (DZD)",
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Black,
          color = PureWhite
        )
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "Véhicule sélectionné : $activeVehicleModel",
          style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight)
        )

        OutlinedTextField(
          value = clientName,
          onValueChange = { clientName = it },
          label = { Text("Nom du client") },
          placeholder = { Text("Ex: Karim Meziane") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = clientPhone,
          onValueChange = { clientPhone = it },
          label = { Text("Téléphone client") },
          placeholder = { Text("Ex: 0550 12 34 56") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = plateNumber,
          onValueChange = { plateNumber = it },
          label = { Text("Immatriculation / Matricule") },
          placeholder = { Text("Ex: 04291-121-16") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = laborHoursText,
          onValueChange = { laborHoursText = it },
          label = { Text("Temps Main d'œuvre (Heures)") },
          placeholder = { Text("Ex: 2.5") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      MekanikPrimaryButton(
        text = "GÉNÉRER LE DEVIS",
        onClick = {
          val hours = laborHoursText.toFloatOrNull() ?: 2.0f
          onConfirm(
            clientName,
            clientPhone,
            plateNumber,
            hours,
            listOf(
              RepairItem("Plaquettes avant Brembo", "5Q0 698 151 BR", 1, 8500),
              RepairItem("Disques avant ventilés 312mm", "1K0 615 301 AA", 1, 18500)
            )
          )
        },
        testTag = "btn_confirm_quote"
      )
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Annuler", color = AluminumGray)
      }
    },
    containerColor = MetalCard
  )
}

@Composable
private fun SignatureDialog(
  orderNumber: String,
  clientName: String,
  totalDzd: Int,
  onDismiss: () -> Unit,
  onSign: (String) -> Unit
) {
  var signerName by remember { mutableStateOf(clientName) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "SIGNATURE NUMÉRIQUE DU CLIENT",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Black,
          color = PureWhite
        )
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "Devis $orderNumber — Montant Net TTC : $totalDzd DZD",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = PerformanceRedGlow,
            fontWeight = FontWeight.Bold
          )
        )
        Text(
          text = "Le client certifie accepter l'ordre de réparation et les tarifs pièces/main d'œuvre spécifiés.",
          style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
        )

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = GraphiteBlack,
          border = BorderStroke(1.dp, MetalBorder),
          modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = "Zone de signature tactile / tablette",
              style = MaterialTheme.typography.bodySmall.copy(
                color = AluminumMuted,
                fontFamily = FontFamily.Cursive,
                fontSize = 16.sp
              )
            )
          }
        }

        OutlinedTextField(
          value = signerName,
          onValueChange = { signerName = it },
          label = { Text("Nom du signataire") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      MekanikPrimaryButton(
        text = "VALIDER LA SIGNATURE",
        onClick = { onSign(signerName) }
      )
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Annuler", color = AluminumGray)
      }
    },
    containerColor = MetalCard
  )
}

@Composable
private fun OrderDetailModal(
  order: RepairOrder,
  onDismiss: () -> Unit,
  onSign: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "FICHE DEVIS / FACTURE PRO",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Black,
            color = PureWhite
          )
        )
        Text(
          text = order.orderNumber,
          style = MaterialTheme.typography.bodySmall.copy(
            color = PerformanceRed,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
          )
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = "Client : ${order.clientName} (${order.clientPhone})",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = OffWhite)
        )
        Text(
          text = "Véhicule : ${order.vehicleModel} • VIN : ${order.vin}",
          style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
        )

        Divider(color = MetalBorder, thickness = 1.dp)

        Text(
          text = "DÉTAIL DES PIÈCES & MAIN D'ŒUVRE",
          style = MaterialTheme.typography.labelSmall.copy(
            color = AluminumMuted,
            fontWeight = FontWeight.Bold
          )
        )

        order.items.forEach { item ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "${item.quantity}x ${item.name} (${item.reference})",
              style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 11.sp),
              modifier = Modifier.weight(1f)
            )
            Text(
              text = "${item.priceDzd * item.quantity} DA",
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = AluminumLight,
                fontSize = 11.sp
              )
            )
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Main d'œuvre atelier (${order.laborHours}h à ${order.laborRatePerHourDzd} DA/h)",
            style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 11.sp)
          )
          Text(
            text = "${order.laborTotalDzd} DA",
            style = MaterialTheme.typography.bodySmall.copy(
              fontWeight = FontWeight.Bold,
              color = AluminumLight,
              fontSize = 11.sp
            )
          )
        }

        Divider(color = MetalBorder, thickness = 1.dp)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "TOTAL NET TTC :",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Black,
              color = PureWhite
            )
          )
          Text(
            text = "${order.netTotalDzd} DA",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              color = PerformanceRedGlow
            )
          )
        }
      }
    },
    confirmButton = {
      if (!order.isSigned) {
        MekanikPrimaryButton(
          text = "SIGNER CE DEVIS",
          onClick = onSign
        )
      } else {
        MekanikSecondaryButton(
          text = "FERMER",
          onClick = onDismiss
        )
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Fermer", color = AluminumGray)
      }
    },
    containerColor = MetalCard
  )
}
