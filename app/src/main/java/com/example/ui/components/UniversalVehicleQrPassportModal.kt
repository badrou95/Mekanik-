package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikViewModel

@Composable
fun UniversalVehicleQrPassportModal(
  passport: VehiclePassportQrData,
  selectedTab: Int,
  onTabSelected: (Int) -> Unit,
  viewModel: MekanikViewModel,
  onDismiss: () -> Unit
) {
  var showQrZoom by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .background(GraphiteDark)
        .testTag("universal_qr_passport_modal"),
      color = GraphiteDark
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
                .size(42.dp)
                .clip(CircleShape)
                .background(PerformanceRedDark)
                .border(1.5.dp, PerformanceRedGlow, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.QrCode2,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "PASSEPORT QR UNIVERSEL",
                  style = MaterialTheme.typography.titleMedium.copy(
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                  )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = TechCyan.copy(alpha = 0.2f),
                  border = BorderStroke(1.dp, TechCyan)
                ) {
                  Text(
                    text = "CERTIFIÉ",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = TechCyan,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                  )
                }
              }
              Text(
                text = "${passport.brand} ${passport.model} • ${passport.vehiclePlate}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumLight,
                  fontSize = 11.5.sp
                )
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(MetalCard)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Vehicle QR & Quick Info Banner
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MetalCard),
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Interactive QR Matrix
            Box(
              modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PureWhite)
                .clickable { showQrZoom = !showQrZoom }
                .padding(6.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Box(modifier = Modifier.size(14.dp).background(GraphiteBlack))
                  Box(modifier = Modifier.size(6.dp).background(PerformanceRed))
                  Box(modifier = Modifier.size(14.dp).background(GraphiteBlack))
                }
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.Center
                ) {
                  Icon(
                    Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = GraphiteDark,
                    modifier = Modifier.size(24.dp)
                  )
                }
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Box(modifier = Modifier.size(14.dp).background(GraphiteBlack))
                  Box(modifier = Modifier.size(8.dp).background(GraphiteBlack))
                  Box(modifier = Modifier.size(14.dp).background(GraphiteBlack))
                }
              }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = passport.qrToken,
                style = MaterialTheme.typography.labelSmall.copy(
                  color = PerformanceRedGlow,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.5.sp
                )
              )
              Text(
                text = "Inscrit par : ${passport.registeredByName}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumLight,
                  fontWeight = FontWeight.Medium,
                  fontSize = 11.sp
                )
              )
              Text(
                text = "VIN : ${passport.vehicleVin}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumMuted,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.5.sp
                )
              )
              Spacer(modifier = Modifier.height(4.dp))
              Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = ElectricGreen.copy(alpha = 0.2f),
                  border = BorderStroke(1.dp, ElectricGreen)
                ) {
                  Text(
                    text = "${passport.currentMileageKm} km",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = ElectricGreen,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = MechanicalOrange.copy(alpha = 0.2f),
                  border = BorderStroke(1.dp, MechanicalOrange)
                ) {
                  Text(
                    text = "Santé ${passport.healthScorePct}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = MechanicalOrange,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4 Role Navigation Tabs
        ScrollableTabRow(
          selectedTabIndex = selectedTab,
          containerColor = MetalSurface,
          contentColor = PureWhite,
          edgePadding = 0.dp,
          indicator = {},
          divider = {}
        ) {
          val tabs = listOf(
            Triple(0, "Fiche Technique", Icons.Default.DirectionsCar),
            Triple(1, "Atelier : OR & Devis", Icons.Default.Handyman),
            Triple(2, "Vendeur : Pièces (${passport.orderedPartsForRetailer.size})", Icons.Default.Storefront),
            Triple(3, "Location de Voitures", Icons.Default.CarRental)
          )

          tabs.forEach { (index, title, icon) ->
            val isSelected = selectedTab == index
            Tab(
              selected = isSelected,
              onClick = { onTabSelected(index) },
              modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 2.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) PerformanceRed else MetalCard)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  icon,
                  contentDescription = null,
                  tint = if (isSelected) PureWhite else AluminumMuted,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = title,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) PureWhite else AluminumLight,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                  )
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Content
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          when (selectedTab) {
            0 -> {
              // --- TAB 0 : FICHE TECHNIQUE & PASSEPORT ---
              item {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.dp, MetalBorder)
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                      text = "DONNÉES TECHNIQUES CERTIFIÉES",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = AluminumMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                      )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    TechSpecRow("Marque & Modèle", "${passport.brand} ${passport.model} (${passport.exactYear})")
                    TechSpecRow("Code Moteur", passport.engineCode)
                    TechSpecRow("Cylindrée & Puissance", "${passport.displacement} • ${passport.powerCh} ch")
                    TechSpecRow("Carburant / Énergie", passport.fuelType)
                    TechSpecRow("Boîte de Vitesses", passport.gearbox)
                    TechSpecRow("Plaque d'Immatriculation", passport.vehiclePlate)
                    TechSpecRow("Dernier Entretien Réalisé", passport.lastServiceDate)
                    TechSpecRow("Prochaine Révision Prévue", "${passport.nextServiceKm} km")
                    TechSpecRow("Empreinte Cryptographique", passport.qrVerificationHash.take(28) + "...")
                  }
                }
              }

              if (passport.recentCertifiedRecords.isNotEmpty()) {
                item {
                  Text(
                    text = "HISTORIQUE D'ENTRETIEN CERTIFIÉ",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = AluminumMuted,
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 1.sp
                    )
                  )
                }

                items(passport.recentCertifiedRecords) { record ->
                  Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MetalCard),
                    border = BorderStroke(1.dp, MetalBorder)
                  ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Text(
                          text = record.operationTitle,
                          style = MaterialTheme.typography.titleSmall.copy(
                            color = PureWhite,
                            fontWeight = FontWeight.Bold
                          )
                        )
                        Text(
                          text = "${record.totalCostDzd.toInt()} DA",
                          style = MaterialTheme.typography.titleSmall.copy(
                            color = TechCyan,
                            fontWeight = FontWeight.Black
                          )
                        )
                      }
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = "${record.date} à ${record.mileageKm} km • ${record.garageName}",
                        style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 11.sp)
                      )
                    }
                  }
                }
              }
            }

            1 -> {
              // --- TAB 1 : ATELIER : ORDRES DE RÉPARATION & DEVIS ---
              item {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "ORDRES DE RÉPARATION (OR) EN ATELIER",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = AluminumMuted,
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 1.sp
                    )
                  )
                  Button(
                    onClick = {
                      viewModel.toggleNewQuoteDialog(true)
                      onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nouvel OR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }

              if (passport.activeRepairOrdersForWorkshop.isEmpty()) {
                item {
                  Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MetalCard),
                    border = BorderStroke(1.dp, MetalBorder)
                  ) {
                    Column(
                      modifier = Modifier.padding(20.dp),
                      horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                      Icon(Icons.Default.TaskAlt, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(36.dp))
                      Spacer(modifier = Modifier.height(8.dp))
                      Text(
                        text = "Aucun ordre de réparation en attente",
                        style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold)
                      )
                      Text(
                        text = "Le véhicule est à jour ou prêt pour une nouvelle intervention.",
                        style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 11.sp)
                      )
                    }
                  }
                }
              } else {
                items(passport.activeRepairOrdersForWorkshop) { or ->
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
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = PerformanceRedDark,
                          border = BorderStroke(1.dp, PerformanceRedGlow)
                        ) {
                          Text(
                            text = or.orderNumber,
                            style = MaterialTheme.typography.labelSmall.copy(
                              color = PureWhite,
                              fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                          )
                        }

                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = Color(or.status.colorHex).copy(alpha = 0.2f),
                          border = BorderStroke(1.dp, Color(or.status.colorHex))
                        ) {
                          Text(
                            text = or.status.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                              color = Color(or.status.colorHex),
                              fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                          )
                        }
                      }

                      Spacer(modifier = Modifier.height(10.dp))
                      Text(
                        text = or.interventionType,
                        style = MaterialTheme.typography.titleSmall.copy(
                          fontWeight = FontWeight.Bold,
                          color = PureWhite
                        )
                      )
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = "Symptômes : ${or.reportedSymptoms}",
                        style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp)
                      )
                      Text(
                        text = "Diagnostic : ${or.diagnosticSummary}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontSize = 11.sp)
                      )

                      Spacer(modifier = Modifier.height(10.dp))
                      Divider(color = MetalBorder)
                      Spacer(modifier = Modifier.height(8.dp))

                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Column {
                          Text(
                            text = "Technicien : ${or.assignedTechnician} • ${or.estimatedLaborHours}h MO",
                            style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 10.5.sp)
                          )
                          Text(
                            text = "Devis Estimé : ${or.estimatedCostDzd} DA",
                            style = MaterialTheme.typography.titleSmall.copy(
                              color = MechanicalOrange,
                              fontWeight = FontWeight.Black
                            )
                          )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                          Button(
                            onClick = {
                              val nextStatus = when (or.status) {
                                RepairStatus.DIAGNOSTIC -> RepairStatus.PIECES_COMMANDEES
                                RepairStatus.PIECES_COMMANDEES -> RepairStatus.EN_COURS
                                RepairStatus.EN_COURS -> RepairStatus.TERMINE
                                RepairStatus.TERMINE -> RepairStatus.FACTURE
                                RepairStatus.FACTURE -> RepairStatus.EN_COURS
                              }
                              viewModel.updateQrRepairOrderStatus(or.orderNumber, nextStatus)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MetalSurface),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MetalBorder),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                          ) {
                            Text("Avancer Statut", fontSize = 10.5.sp, color = PureWhite)
                          }
                        }
                      }
                    }
                  }
                }
              }
            }

            2 -> {
              // --- TAB 2 : VENDEUR / DÉTAILLANT : PIÈCES DEMANDÉES & DEVIS ---
              item {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "PIÈCES DEMANDÉES & DEVIS COMPTOIR",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = AluminumMuted,
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 1.sp
                    )
                  )
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = TechCyan.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, TechCyan)
                  ) {
                    Text(
                      text = "Total : ${passport.partsTotalValueDzd} DA",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = TechCyan,
                        fontWeight = FontWeight.Bold
                      ),
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                  }
                }
              }

              if (passport.orderedPartsForRetailer.isEmpty()) {
                item {
                  Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MetalCard),
                    border = BorderStroke(1.dp, MetalBorder)
                  ) {
                    Column(
                      modifier = Modifier.padding(20.dp),
                      horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                      Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = AluminumMuted, modifier = Modifier.size(36.dp))
                      Spacer(modifier = Modifier.height(8.dp))
                      Text(
                        text = "Aucune pièce commandée sur ce QR",
                        style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold)
                      )
                      Text(
                        text = "Le client peut ajouter des pièces directement depuis le catalogue Mekanik+.",
                        style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 11.sp)
                      )
                    }
                  }
                }
              } else {
                items(passport.orderedPartsForRetailer) { part ->
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
                        verticalAlignment = Alignment.Top
                      ) {
                        Column(modifier = Modifier.weight(1f)) {
                          Text(
                            text = part.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                              fontWeight = FontWeight.Bold,
                              color = PureWhite
                            )
                          )
                          Spacer(modifier = Modifier.height(3.dp))
                          Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                              text = "Réf OEM : ",
                              style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 11.sp)
                            )
                            Text(
                              text = part.oemReference,
                              style = MaterialTheme.typography.bodySmall.copy(
                                color = PerformanceRedGlow,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.5.sp
                              )
                            )
                          }
                          Text(
                            text = "Marque demandée : ${part.brandRequested} • Qté : ${part.quantity}",
                            style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp)
                          )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                          Text(
                            text = "${part.totalPriceDzd} DA",
                            style = MaterialTheme.typography.titleMedium.copy(
                              color = TechCyan,
                              fontWeight = FontWeight.Black
                            )
                          )
                          Spacer(modifier = Modifier.height(4.dp))
                          Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (part.status) {
                              QrPartOrderStatus.PRETE_COMPTOIR -> ElectricGreen.copy(alpha = 0.2f)
                              QrPartOrderStatus.RESERVEE_MAGASIN -> WarningAmber.copy(alpha = 0.2f)
                              QrPartOrderStatus.DEMANDEE_CLIENT -> TechnicalBlue.copy(alpha = 0.2f)
                              QrPartOrderStatus.LIVREE -> AluminumMuted.copy(alpha = 0.2f)
                            },
                            border = BorderStroke(
                              1.dp,
                              when (part.status) {
                                QrPartOrderStatus.PRETE_COMPTOIR -> ElectricGreen
                                QrPartOrderStatus.RESERVEE_MAGASIN -> WarningAmber
                                QrPartOrderStatus.DEMANDEE_CLIENT -> TechnicalBlue
                                QrPartOrderStatus.LIVREE -> AluminumMuted
                              }
                            )
                          ) {
                            Text(
                              text = part.status.label,
                              style = MaterialTheme.typography.labelSmall.copy(
                                color = when (part.status) {
                                  QrPartOrderStatus.PRETE_COMPTOIR -> ElectricGreen
                                  QrPartOrderStatus.RESERVEE_MAGASIN -> WarningAmber
                                  QrPartOrderStatus.DEMANDEE_CLIENT -> TechnicalBlue
                                  QrPartOrderStatus.LIVREE -> AluminumLight
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                              ),
                              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                          }
                        }
                      }

                      Spacer(modifier = Modifier.height(10.dp))
                      Divider(color = MetalBorder)
                      Spacer(modifier = Modifier.height(8.dp))

                      // Action buttons for retailer
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        Button(
                          onClick = {
                            val nextStatus = when (part.status) {
                              QrPartOrderStatus.DEMANDEE_CLIENT -> QrPartOrderStatus.RESERVEE_MAGASIN
                              QrPartOrderStatus.RESERVEE_MAGASIN -> QrPartOrderStatus.PRETE_COMPTOIR
                              QrPartOrderStatus.PRETE_COMPTOIR -> QrPartOrderStatus.LIVREE
                              QrPartOrderStatus.LIVREE -> QrPartOrderStatus.PRETE_COMPTOIR
                            }
                            viewModel.updateQrPartStatus(part.partId, nextStatus)
                          },
                          colors = ButtonDefaults.buttonColors(containerColor = MetalSurface),
                          shape = RoundedCornerShape(8.dp),
                          border = BorderStroke(1.dp, MetalBorder),
                          modifier = Modifier.weight(1f),
                          contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                          Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(14.dp))
                          Spacer(modifier = Modifier.width(4.dp))
                          Text("Changer Statut", fontSize = 11.sp, color = PureWhite)
                        }

                        Button(
                          onClick = {
                            viewModel.toggleInvoiceModal(true)
                            onDismiss()
                          },
                          colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                          shape = RoundedCornerShape(8.dp),
                          modifier = Modifier.weight(1f),
                          contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                          Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(14.dp))
                          Spacer(modifier = Modifier.width(4.dp))
                          Text("Facturer Comptoir", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                      }
                    }
                  }
                }
              }
            }

            3 -> {
              // --- TAB 3 : LOCATION DE VOITURES (CONTRAT & RESTITUTION) ---
              item {
                Text(
                  text = "CONTRAT & GESTION AGENCE LOCATION",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = AluminumMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                  )
                )
              }

              val rental = passport.rentalContractInfo
              if (rental == null) {
                item {
                  Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MetalCard),
                    border = BorderStroke(1.dp, MetalBorder)
                  ) {
                    Column(
                      modifier = Modifier.padding(20.dp),
                      horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                      Icon(Icons.Default.CarRental, contentDescription = null, tint = AluminumMuted, modifier = Modifier.size(36.dp))
                      Spacer(modifier = Modifier.height(8.dp))
                      Text(
                        text = "Véhicule non rattaché à une agence de location",
                        style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold)
                      )
                      Text(
                        text = "Ce véhicule est enregistré en compte Particulier ou Atelier Garage Pro.",
                        style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 11.sp)
                      )
                    }
                  }
                }
              } else {
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
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = MechanicalOrangeDark,
                          border = BorderStroke(1.dp, MechanicalOrange)
                        ) {
                          Text(
                            text = rental.contractNumber,
                            style = MaterialTheme.typography.labelSmall.copy(
                              color = PureWhite,
                              fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                          )
                        }
                        Text(
                          text = "${rental.dailyRateDzd} DA / jour",
                          style = MaterialTheme.typography.titleSmall.copy(
                            color = TechCyan,
                            fontWeight = FontWeight.Black
                          )
                        )
                      }

                      Spacer(modifier = Modifier.height(10.dp))
                      TechSpecRow("Agence Loueuse", "${rental.agencyName} (${rental.agencyPhone})")
                      TechSpecRow("Locataire Actuel", "${rental.renterFullName} (${rental.renterPhone})")
                      TechSpecRow("N° Permis de Conduire", rental.driverLicenseNumber)
                      TechSpecRow("Dates Contrat", "${rental.startDate} ➔ ${rental.expectedEndDate}")
                      TechSpecRow("Km Départ", "${rental.initialMileageKm} km")
                      TechSpecRow("Niveau Carburant", "${rental.fuelLevelPct} %")
                      TechSpecRow("Caution CIB Bloquée", "${rental.depositAmountDzd} DA")

                      if (rental.returnNotes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                          text = "Notes restitution : ${rental.returnNotes}",
                          style = MaterialTheme.typography.bodySmall.copy(color = WarningAmber, fontSize = 11.sp)
                        )
                      }

                      Spacer(modifier = Modifier.height(12.dp))
                      Button(
                        onClick = {
                          viewModel.returnRentalCar(rental.contractNumber, passport.currentMileageKm + 450)
                          onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                      ) {
                        Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Clôturer Restitution & Libérer Caution", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                      }
                    }
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom Actions Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = { showQrZoom = true },
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, MetalBorder),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Agrandir QR", fontSize = 12.sp)
          }

          Button(
            onClick = {
              viewModel.toggleUniversalQrScanner(true)
              onDismiss()
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Scanner Autre QR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun TechSpecRow(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall.copy(color = AluminumMuted, fontSize = 11.5.sp)
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall.copy(
        color = PureWhite,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.5.sp
      )
    )
  }
}
