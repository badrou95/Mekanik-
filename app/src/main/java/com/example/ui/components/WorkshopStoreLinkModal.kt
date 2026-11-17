package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun WorkshopStoreLinkModal(
  storeConfig: WorkshopStoreLinkConfig,
  onToggleStoreLinked: (Boolean) -> Unit,
  onSwitchActiveView: (WorkshopActiveView) -> Unit,
  onUpdateStoreDetails: (String, String, String, String) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isEditingDetails by remember { mutableStateOf(false) }
  var storeName by remember { mutableStateOf(storeConfig.storeName) }
  var storeRc by remember { mutableStateOf(storeConfig.storeRegisterNumber) }
  var storeWilaya by remember { mutableStateOf(storeConfig.storeWilaya) }
  var storePhone by remember { mutableStateOf(storeConfig.storePhone) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .testTag("workshop_store_link_modal"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = GraphiteDark),
      border = BorderStroke(1.5.dp, MechanicalOrange.copy(alpha = 0.8f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MechanicalOrange.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Storefront,
                contentDescription = null,
                tint = MechanicalOrange,
                modifier = Modifier.size(26.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "PASSERELLE MAGASIN + ATELIER",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    fontSize = 16.sp
                  )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = MechanicalOrange.copy(alpha = 0.2f),
                  border = BorderStroke(1.dp, MechanicalOrange)
                ) {
                  Text(
                    text = "UN SEUL COMPTE",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = MechanicalOrange,
                      fontWeight = FontWeight.Black,
                      fontSize = 8.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                  )
                }
              }
              Text(
                text = "Option Formule Atelier Pro • Supplément +5 000 DA/an",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumGray,
                  fontSize = 11.sp
                )
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("btn_close_store_link_modal")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Status & Pricing Summary Card
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (storeConfig.isStoreLinked) Color(0xFF1E2620) else MetalCard
              ),
              border = BorderStroke(
                1.5.dp,
                if (storeConfig.isStoreLinked) ElectricGreen else MechanicalOrange
              )
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = if (storeConfig.isStoreLinked) Icons.Default.Link else Icons.Default.LinkOff,
                      contentDescription = null,
                      tint = if (storeConfig.isStoreLinked) ElectricGreen else MechanicalOrange,
                      modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                      Text(
                        text = if (storeConfig.isStoreLinked) "COMPTE UNIFIÉ ACTIF & SYNCHRONISÉ" else "OPTION DISPONIBLE (+5 000 DA)",
                        style = MaterialTheme.typography.titleMedium.copy(
                          color = PureWhite,
                          fontWeight = FontWeight.Black,
                          fontSize = 14.sp
                        )
                      )
                      Text(
                        text = "Atelier de Réparation (18 000 DA) + Magasin Pièces (+5 000 DA)",
                        style = MaterialTheme.typography.bodySmall.copy(
                          color = AluminumGray,
                          fontSize = 11.sp
                        )
                      )
                    }
                  }

                  Switch(
                    checked = storeConfig.isStoreLinked,
                    onCheckedChange = { onToggleStoreLinked(it) },
                    colors = SwitchDefaults.colors(
                      checkedThumbColor = PureWhite,
                      checkedTrackColor = ElectricGreen,
                      uncheckedTrackColor = MetalBorder
                    ),
                    modifier = Modifier.testTag("switch_store_link")
                  )
                }

                Row(verticalAlignment = Alignment.Bottom) {
                  Text(
                    text = "23 000 DA",
                    style = MaterialTheme.typography.headlineMedium.copy(
                      fontWeight = FontWeight.Black,
                      color = PerformanceRedGlow,
                      fontSize = 22.sp
                    )
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "/ an (Pack complet Atelier 18 000 DA + Magasin 5 000 DA)",
                    style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.5.sp),
                    modifier = Modifier.padding(bottom = 2.dp)
                  )
                }

                Text(
                  text = "Cette option permet aux propriétaires de garages possédant également une boutique de pièces détachées de tout gérer depuis une interface unique sans payer deux abonnements séparés.",
                  style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 11.5.sp)
                )
              }
            }
          }

          // Fast View Switcher (Atelier vs Magasin de pièces)
          if (storeConfig.isStoreLinked) {
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, MetalBorder)
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  Text(
                    text = "BASCULE RAPIDE DE VUE (SANS DÉCONNEXION)",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = AluminumGray,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp
                    )
                  )

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                  ) {
                    WorkshopActiveView.entries.forEach { viewMode ->
                      val isSelected = storeConfig.activeView == viewMode
                      Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) (if (viewMode == WorkshopActiveView.ATELIER) PerformanceRed else MechanicalOrange) else GraphiteBlack,
                        border = BorderStroke(
                          1.dp,
                          if (isSelected) PureWhite.copy(alpha = 0.8f) else MetalBorder
                        ),
                        onClick = { onSwitchActiveView(viewMode) },
                        modifier = Modifier
                          .weight(1f)
                          .testTag("view_mode_${viewMode.name.lowercase()}")
                      ) {
                        Column(
                          modifier = Modifier.padding(12.dp),
                          verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                          Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                              imageVector = if (viewMode == WorkshopActiveView.ATELIER) Icons.Default.Handyman else Icons.Default.Storefront,
                              contentDescription = null,
                              tint = if (isSelected) PureWhite else AluminumLight,
                              modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                              text = if (viewMode == WorkshopActiveView.ATELIER) "ATELIER" else "MAGASIN",
                              style = MaterialTheme.typography.titleSmall.copy(
                                color = if (isSelected) PureWhite else AluminumLight,
                                fontWeight = FontWeight.Black
                              )
                            )
                          }
                          Text(
                            text = viewMode.subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                              color = if (isSelected) OffWhite else AluminumGray,
                              fontSize = 10.sp
                            ),
                            maxLines = 2
                          )
                        }
                      }
                    }
                  }
                }
              }
            }

            // Linked Store Overview & Quick Telemetry
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MetalCard),
                border = BorderStroke(1.dp, MetalBorder)
              ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Store, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "Informations du Magasin de Pièces",
                        style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold)
                      )
                    }

                    TextButton(
                      onClick = { isEditingDetails = !isEditingDetails },
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                      Icon(
                        if (isEditingDetails) Icons.Default.Check else Icons.Default.Edit,
                        contentDescription = null,
                        tint = TechCyan,
                        modifier = Modifier.size(14.dp)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = if (isEditingDetails) "Valider" else "Modifier",
                        color = TechCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }

                  if (isEditingDetails) {
                    OutlinedTextField(
                      value = storeName,
                      onValueChange = { storeName = it },
                      label = { Text("Nom du Magasin de Pièces") },
                      modifier = Modifier.fillMaxWidth(),
                      colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MechanicalOrange,
                        unfocusedBorderColor = MetalBorder,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                      ),
                      shape = RoundedCornerShape(8.dp),
                      singleLine = true
                    )

                    OutlinedTextField(
                      value = storeRc,
                      onValueChange = { storeRc = it },
                      label = { Text("Numéro Registre de Commerce (RC)") },
                      modifier = Modifier.fillMaxWidth(),
                      colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MechanicalOrange,
                        unfocusedBorderColor = MetalBorder,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                      ),
                      shape = RoundedCornerShape(8.dp),
                      singleLine = true
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                      OutlinedTextField(
                        value = storeWilaya,
                        onValueChange = { storeWilaya = it },
                        label = { Text("Wilaya / Commune") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                          focusedBorderColor = MechanicalOrange,
                          unfocusedBorderColor = MetalBorder,
                          focusedTextColor = PureWhite,
                          unfocusedTextColor = PureWhite
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                      )

                      OutlinedTextField(
                        value = storePhone,
                        onValueChange = { storePhone = it },
                        label = { Text("Téléphone Contact") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                          focusedBorderColor = MechanicalOrange,
                          unfocusedBorderColor = MetalBorder,
                          focusedTextColor = PureWhite,
                          unfocusedTextColor = PureWhite
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                      )
                    }

                    Button(
                      onClick = {
                        onUpdateStoreDetails(storeName, storeRc, storeWilaya, storePhone)
                        isEditingDetails = false
                      },
                      shape = RoundedCornerShape(8.dp),
                      colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Text("ENREGISTRER LES MODIFICATIONS", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                    }
                  } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                      Column {
                        Text(text = storeConfig.storeName, style = MaterialTheme.typography.bodyMedium.copy(color = PureWhite, fontWeight = FontWeight.Bold))
                        Text(text = storeConfig.storeRegisterNumber, style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp))
                      }
                      Column(horizontalAlignment = Alignment.End) {
                        Text(text = storeConfig.storeWilaya, style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp))
                        Text(text = storeConfig.storePhone, style = MaterialTheme.typography.bodySmall.copy(color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp))
                      }
                    }
                  }

                  HorizontalDivider(color = MetalBorder.copy(alpha = 0.5f), thickness = 0.8.dp)

                  // Stock & Sales telemetry
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                  ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(text = "${storeConfig.totalPartsInStock}", style = MaterialTheme.typography.titleMedium.copy(color = TechCyan, fontWeight = FontWeight.Black))
                      Text(text = "Pièces en stock", style = MaterialTheme.typography.labelSmall.copy(color = AluminumGray, fontSize = 10.sp))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(text = "${storeConfig.lowStockAlertsCount}", style = MaterialTheme.typography.titleMedium.copy(color = PerformanceRedGlow, fontWeight = FontWeight.Black))
                      Text(text = "Alertes réassort", style = MaterialTheme.typography.labelSmall.copy(color = AluminumGray, fontSize = 10.sp))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(text = "${storeConfig.dailyCounterSalesDzd} DA", style = MaterialTheme.typography.titleMedium.copy(color = ElectricGreen, fontWeight = FontWeight.Black))
                      Text(text = "Ventes comptoir/j", style = MaterialTheme.typography.labelSmall.copy(color = AluminumGray, fontSize = 10.sp))
                    }
                  }
                }
              }
            }
          }

          // Key advantages list
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                  text = "AVANTAGES DU COMPTE UNIQUE ATELIER + MAGASIN",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = AluminumGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  )
                )

                listOf(
                  "🔑 Un seul identifiant de connexion pour les 2 activités (sans double mot de passe)",
                  "📦 Déduction automatique du stock magasin lors de la facturation d'un devis atelier",
                  "🛒 Vente de pièces au comptoir directement depuis l'application avec reçus DZD",
                  "💰 Économie substantielle : 23 000 DA/an au lieu de 33 000 DA en abonnements distincts",
                  "📊 Comptabilité & bilans TVA consolidés pour le garage et la boutique"
                ).forEach { adv ->
                  Row(verticalAlignment = Alignment.Top) {
                    Icon(
                      Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = ElectricGreen,
                      modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = adv,
                      style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 11.5.sp)
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom Action Button
        Button(
          onClick = onDismiss,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MechanicalOrange),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("btn_close_store_link")
        ) {
          Icon(Icons.Default.Check, contentDescription = null, tint = PureWhite, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (storeConfig.isStoreLinked) "ENREGISTRER & FERMER" else "COMPRIS",
            color = PureWhite,
            fontWeight = FontWeight.Black
          )
        }
      }
    }
  }
}
