package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun VehicleInspectionModal(
  inspections: List<VehicleInspection>,
  onSaveInspection: (VehicleInspection) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedTab by remember { mutableStateOf(0) } // 0 = Nouvelle Inspection 360, 1 = Historique
  var currentAngle by remember { mutableStateOf(InspectionViewAngle.AVANT) }
  var vehicleModel by remember { mutableStateOf("Peugeot 308 (1.6 BlueHDi)") }
  var licensePlate by remember { mutableStateOf("01429-122-16") }
  var vin by remember { mutableStateOf("VF77J9HP4EJ123456") }
  var clientName by remember { mutableStateOf("Karim Meziane") }
  var clientPhone by remember { mutableStateOf("0550 42 19 88") }
  var mileageKm by remember { mutableStateOf("114 200") }
  var fuelPercent by remember { mutableFloatStateOf(60f) }
  var generalNotes by remember { mutableStateOf("") }

  // Markers placed
  var markers by remember { mutableStateOf<List<DamageMarker>>(emptyList()) }
  var selectedMarker by remember { mutableStateOf<DamageMarker?>(null) }

  // Marker creation sheet state
  var pendingDamageType by remember { mutableStateOf(DamageType.RAYURE) }
  var pendingSeverity by remember { mutableStateOf(DamageSeverity.MOYEN) }
  var pendingDescription by remember { mutableStateOf("") }
  var showAddMarkerSheet by remember { mutableStateOf(false) }
  var pendingXRatio by remember { mutableFloatStateOf(0.5f) }
  var pendingYRatio by remember { mutableFloatStateOf(0.5f) }

  // Digital Signature
  val signaturePoints = remember { mutableStateListOf<Offset>() }
  var isSignatureCaptured by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = 28.dp, bottom = 12.dp, start = 10.dp, end = 10.dp)
        .testTag("modal_vehicle_inspection"),
      shape = RoundedCornerShape(16.dp),
      color = GraphiteDark,
      border = BorderStroke(1.dp, MetalBorder)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PerformanceRed.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = PerformanceRed, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "INSPECTION & ÉTAT DES LIEUX 360°",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = PureWhite,
                  fontSize = 15.sp
                )
              )
              Text(
                text = "Schéma carrosserie tactile, photos et signature client",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(MetalCard)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight, modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs : Nouvelle Inspection vs Historique
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (selectedTab == 0) PerformanceRed else MetalCard,
            border = BorderStroke(1.dp, if (selectedTab == 0) PerformanceRedGlow else MetalBorder),
            onClick = { selectedTab = 0 },
            modifier = Modifier.weight(1f)
          ) {
            Row(
              modifier = Modifier.padding(vertical = 8.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.CameraAlt, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Nouvelle Inspection", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (selectedTab == 1) PerformanceRed else MetalCard,
            border = BorderStroke(1.dp, if (selectedTab == 1) PerformanceRedGlow else MetalBorder),
            onClick = { selectedTab = 1 },
            modifier = Modifier.weight(1f)
          ) {
            Row(
              modifier = Modifier.padding(vertical = 8.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.History, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Historique (${inspections.size})", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
          // New Inspection View
          LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            // Vehicle & Client Meta Card
            item {
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, MetalBorder)
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Véhicule : $vehicleModel", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Surface(shape = RoundedCornerShape(4.dp), color = GraphiteBlack, border = BorderStroke(0.5.dp, TechCyan)) {
                      Text(licensePlate, color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                  }
                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Client : $clientName ($clientPhone)", color = AluminumGray, fontSize = 11.sp)
                    Text("VIN : $vin", color = AluminumMuted, fontSize = 10.sp)
                  }
                }
              }
            }

            // View Angle Selector
            item {
              Column {
                Text("SÉLECTIONNER L'ANGLE DU VÉHICULE :", color = AluminumGray, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  items(InspectionViewAngle.entries) { angle ->
                    val isSelected = angle == currentAngle
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = if (isSelected) PerformanceRed else GraphiteBlack,
                      border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
                      onClick = { currentAngle = angle },
                      modifier = Modifier.testTag("tab_angle_${angle.name.lowercase()}")
                    ) {
                      Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                          text = angle.label,
                          color = if (isSelected) PureWhite else AluminumLight,
                          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                          fontSize = 11.sp
                        )
                        val angleCount = markers.count { it.viewAngle == angle }
                        if (angleCount > 0) {
                          Spacer(modifier = Modifier.width(4.dp))
                          Box(
                            modifier = Modifier
                              .size(16.dp)
                              .clip(CircleShape)
                              .background(if (isSelected) PureWhite else PerformanceRed),
                            contentAlignment = Alignment.Center
                          ) {
                            Text(angleCount.toString(), color = if (isSelected) PerformanceRed else PureWhite, fontSize = 9.sp, fontWeight = FontWeight.Black)
                          }
                        }
                      }
                    }
                  }
                }
              }
            }

            // Interactive Car Body Canvas
            item {
              CarBodyInspectionCanvas(
                viewAngle = currentAngle,
                markers = markers,
                selectedMarkerId = selectedMarker?.id,
                onCanvasTapped = { x, y ->
                  pendingXRatio = x
                  pendingYRatio = y
                  pendingDescription = ""
                  showAddMarkerSheet = true
                },
                onMarkerSelected = { marker ->
                  selectedMarker = marker
                }
              )
            }

            // Add Marker Sub-Form
            if (showAddMarkerSheet) {
              item {
                Card(
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalGray),
                  border = BorderStroke(1.dp, PerformanceRedGlow)
                ) {
                  Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                      Text("NOUVEAU DÉFAUT RELEVÉ", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                      IconButton(onClick = { showAddMarkerSheet = false }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = AluminumLight, modifier = Modifier.size(16.dp))
                      }
                    }

                    // Damage Type
                    Text("Type de dommage :", color = AluminumGray, fontSize = 11.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      items(DamageType.entries) { type ->
                        val isSel = type == pendingDamageType
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = if (isSel) PerformanceRedDark else GraphiteBlack,
                          border = BorderStroke(1.dp, if (isSel) PerformanceRed else MetalBorder),
                          onClick = { pendingDamageType = type }
                        ) {
                          Text("${type.iconEmoji} ${type.label}", color = PureWhite, fontSize = 10.5.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                      }
                    }

                    // Severity
                    Text("Niveau de gravité :", color = AluminumGray, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                      DamageSeverity.entries.forEach { sev ->
                        val isSel = sev == pendingSeverity
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = if (isSel) Color(sev.colorHex).copy(alpha = 0.3f) else GraphiteBlack,
                          border = BorderStroke(1.dp, if (isSel) Color(sev.colorHex) else MetalBorder),
                          onClick = { pendingSeverity = sev },
                          modifier = Modifier.weight(1f)
                        ) {
                          Text(sev.label, color = if (isSel) Color(sev.colorHex) else AluminumLight, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 6.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                      }
                    }

                    // Description
                    OutlinedTextField(
                      value = pendingDescription,
                      onValueChange = { pendingDescription = it },
                      label = { Text("Précisions (ex: rayure 10cm, choc trottoir...)") },
                      modifier = Modifier.fillMaxWidth(),
                      colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        focusedBorderColor = PerformanceRed,
                        unfocusedBorderColor = MetalBorder
                      ),
                      singleLine = true
                    )

                    // Add Button
                    Button(
                      onClick = {
                        val newMarker = DamageMarker(
                          viewAngle = currentAngle,
                          xRatio = pendingXRatio,
                          yRatio = pendingYRatio,
                          damageType = pendingDamageType,
                          severity = pendingSeverity,
                          description = pendingDescription.ifBlank { pendingDamageType.label },
                          photoLabel = "Photo_${currentAngle.name.take(4)}_${markers.size + 1}.jpg",
                          hasPhotoAttached = true
                        )
                        markers = markers + newMarker
                        showAddMarkerSheet = false
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Icon(Icons.Default.Check, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Enregistrer le Défaut (+ Photo)", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }

            // List of Placed Markers
            if (markers.isNotEmpty()) {
              item {
                Card(
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.dp, MetalBorder)
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Text("DÉFAUTS RELEVÉS (${markers.size}) :", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    markers.forEachIndexed { idx, m ->
                      Row(
                        modifier = Modifier
                          .fillMaxWidth()
                          .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                          Box(
                            modifier = Modifier
                              .size(10.dp)
                              .clip(CircleShape)
                              .background(Color(m.severity.colorHex))
                          )
                          Spacer(modifier = Modifier.width(8.dp))
                          Column {
                            Text("${m.viewAngle.label} • ${m.damageType.label}", color = PureWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text(m.description, color = AluminumGray, fontSize = 10.sp)
                          }
                        }
                        IconButton(
                          onClick = { markers = markers.filter { it.id != m.id } },
                          modifier = Modifier.size(24.dp)
                        ) {
                          Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = PerformanceRedGlow, modifier = Modifier.size(16.dp))
                        }
                      }
                      if (idx < markers.size - 1) {
                        HorizontalDivider(color = MetalBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
                      }
                    }
                  }
                }
              }
            }

            // Fuel & Mileage Gauges
            item {
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, MetalBorder)
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  Text("CONTRÔLE KILOMÉTRAGE & CARBURANT", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                      value = mileageKm,
                      onValueChange = { mileageKm = it },
                      label = { Text("Kilométrage (km)") },
                      modifier = Modifier.weight(1f),
                      colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        focusedBorderColor = TechCyan,
                        unfocusedBorderColor = MetalBorder
                      )
                    )
                  }

                  Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                      Text("Niveau de carburant :", color = AluminumGray, fontSize = 11.5.sp)
                      Text("${fuelPercent.toInt()}%", color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Slider(
                      value = fuelPercent,
                      onValueChange = { fuelPercent = it },
                      valueRange = 0f..100f,
                      colors = SliderDefaults.colors(
                        thumbColor = PerformanceRed,
                        activeTrackColor = TechCyan,
                        inactiveTrackColor = GraphiteBlack
                      )
                    )
                  }
                }
              }
            }

            // Digital Signature Pad for Client
            item {
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, MetalBorder)
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("SIGNATURE NUMÉRIQUE CLIENT", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    if (signaturePoints.isNotEmpty()) {
                      TextButton(onClick = { signaturePoints.clear(); isSignatureCaptured = false }) {
                        Text("Effacer", color = PerformanceRedGlow, fontSize = 11.sp)
                      }
                    }
                  }

                  Text("Signez avec le doigt dans le cadre ci-dessous pour valider l'état des lieux :", color = AluminumMuted, fontSize = 10.sp)

                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(110.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(GraphiteBlack)
                      .border(1.dp, if (signaturePoints.isNotEmpty()) ElectricGreen else MetalBorder, RoundedCornerShape(8.dp))
                      .pointerInput(Unit) {
                        detectDragGestures(
                          onDragStart = { offset ->
                            signaturePoints.add(offset)
                            isSignatureCaptured = true
                          },
                          onDrag = { change, _ ->
                            change.consume()
                            signaturePoints.add(change.position)
                            isSignatureCaptured = true
                          }
                        )
                      }
                  ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                      if (signaturePoints.size > 1) {
                        val path = Path().apply {
                          moveTo(signaturePoints.first().x, signaturePoints.first().y)
                          for (i in 1 until signaturePoints.size) {
                            lineTo(signaturePoints[i].x, signaturePoints[i].y)
                          }
                        }
                        drawPath(path, color = TechCyan, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                      }
                    }

                    if (signaturePoints.isEmpty()) {
                      Text(
                        text = "✍️ Signature Client ici",
                        color = AluminumMuted.copy(alpha = 0.5f),
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.Center)
                      )
                    }
                  }
                }
              }
            }
          }

          // Bottom Action Bar
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = {
              val inspection = VehicleInspection(
                vin = vin,
                licensePlate = licensePlate,
                vehicleModel = vehicleModel,
                clientName = clientName,
                clientPhone = clientPhone,
                mileageKm = mileageKm,
                fuelPercent = fuelPercent.toInt(),
                markers = markers,
                clientSignatureAccepted = isSignatureCaptured,
                generalNotes = generalNotes
              )
              onSaveInspection(inspection)
            },
            colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("btn_save_inspection")
          ) {
            Icon(Icons.Default.Verified, contentDescription = null, tint = PureWhite)
            Spacer(modifier = Modifier.width(8.dp))
            Text("VALIDER & ENREGISTRER L'ÉTAT DES LIEUX", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        } else {
          // Inspection History Tab
          LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(inspections) { item ->
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, MetalBorder)
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(item.inspectionNumber, color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(item.dateFormatted, color = AluminumMuted, fontSize = 11.sp)
                  }
                  Text(item.vehicleModel, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Matricule : ${item.licensePlate}", color = AluminumLight, fontSize = 11.sp)
                    Text("Km : ${item.mileageKm}", color = AluminumLight, fontSize = 11.sp)
                  }
                  Text("Client : ${item.clientName} (${item.clientPhone})", color = AluminumGray, fontSize = 11.sp)
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (item.markers.isEmpty()) ElectricGreen else PerformanceRed))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${item.markers.size} anomalie(s) carrosserie signalée(s)", color = AluminumLight, fontSize = 10.5.sp)
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
