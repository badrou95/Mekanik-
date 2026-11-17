package com.example.ui.components

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.RemoteClientQuoteSession
import com.example.ui.theme.*

@Composable
fun ClientRemoteQuotePortalModal(
  session: RemoteClientQuoteSession,
  onToggleOptionalItem: (String) -> Unit,
  onApproveAndSignRemotely: () -> Unit,
  onAdvanceLiveStep: () -> Unit,
  onSendWhatsAppLink: () -> Unit,
  onDismiss: () -> Unit
) {
  val steps = listOf(
    "Réception 360°",
    "Diagnostic IA",
    "Accord Devis",
    "Sur le Pont",
    "Prêt à récupérer"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)
        .testTag("client_remote_quote_portal_modal"),
      shape = RoundedCornerShape(18.dp),
      color = GraphiteDark,
      border = BorderStroke(1.dp, TechCyan)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(MetalCard)
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(TechCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Draw, contentDescription = null, tint = TechCyan)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "PORTAIL CLIENT & SIGNATURE À DISTANCE",
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
              )
              Text(
                text = "Lien sécurisé : ${session.shareableUrl}",
                color = TechCyan,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = PureWhite)
          }
        }

        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 14.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          contentPadding = PaddingValues(vertical = 14.dp)
        ) {
          // Vehicle & Live Progress Stepper
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(
                      text = "🚗 ${session.vehicleModel} (${session.licensePlate})",
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 13.5.sp
                    )
                    Text(
                      text = "Client : ${session.clientName} • Devis N° ${session.quoteNumber}",
                      color = AluminumLight,
                      fontSize = 11.sp
                    )
                  }
                  OutlinedButton(
                    onClick = onAdvanceLiveStep,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    border = BorderStroke(1.dp, TechCyan)
                  ) {
                    Text("Étape +1", color = TechCyan, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                  }
                }

                // 5-Step Progress Indicator
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  steps.forEachIndexed { idx, label ->
                    val isDone = idx <= session.currentStepIndex
                    Column(
                      horizontalAlignment = Alignment.CenterHorizontally,
                      modifier = Modifier.weight(1f)
                    ) {
                      Box(
                        modifier = Modifier
                          .size(26.dp)
                          .clip(CircleShape)
                          .background(if (isDone) ElectricGreen else GraphiteBlack),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(
                          text = "${idx + 1}",
                          color = if (isDone) GraphiteBlack else AluminumGray,
                          fontWeight = FontWeight.Black,
                          fontSize = 11.sp
                        )
                      }
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = label,
                        color = if (isDone) PureWhite else AluminumMuted,
                        fontSize = 9.sp,
                        fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                      )
                    }
                  }
                }
              }
            }
          }

          // Video Check-up Banner from Mechanic
          item {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = GraphiteBlack,
              border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.7f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.PlayCircleFilled, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(30.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text("PREUVE VIDÉO ATELIER JOINTE AU DEVIS", color = MechanicalOrange, fontWeight = FontWeight.Black, fontSize = 11.sp)
                  Text(session.mechanicVideoNoteTitle, color = PureWhite, fontSize = 11.sp)
                }
              }
            }
          }

          // Mandatory & Interactive Optional Repairs Selection
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MetalCard),
              border = BorderStroke(1.dp, MetalBorder)
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("1. INTERVENTION PRINCIPALE (OBLIGATOIRE)", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 12.sp)
                  Text("${session.mandatoryTotalDzd} DZD", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
                Text(
                  text = "Kit Disques + Plaquettes Avant d'origine + Main d'œuvre barème constructeur garantie 12 mois.",
                  color = AluminumLight,
                  fontSize = 11.sp
                )

                HorizontalDivider(color = MetalBorder)

                Text(
                  text = "2. TRAVAUX RECOMMANDÉS (AU CHOIX DU CLIENT À DISTANCE) :",
                  color = TechCyan,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.5.sp
                )

                session.optionalItems.forEach { opt ->
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (opt.isSelectedByClient) TechCyan.copy(alpha = 0.12f) else GraphiteBlack,
                    border = BorderStroke(1.dp, if (opt.isSelectedByClient) TechCyan else MetalBorder),
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable { onToggleOptionalItem(opt.id) }
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Checkbox(
                          checked = opt.isSelectedByClient,
                          onCheckedChange = { onToggleOptionalItem(opt.id) },
                          colors = CheckboxDefaults.colors(checkedColor = TechCyan)
                        )
                        Column {
                          Text(opt.title, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                          Text(opt.recommendationReason, color = AluminumLight, fontSize = 10.sp)
                        }
                      }
                      Text(
                        text = "+${opt.priceDzd} DA",
                        color = if (opt.isSelectedByClient) TechCyan else AluminumLight,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                      )
                    }
                  }
                }

                HorizontalDivider(color = MetalBorder)

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("TOTAL DEVIS VALIDÉ TTC :", color = PureWhite, fontWeight = FontWeight.Black, fontSize = 13.sp)
                  Text(
                    text = "${session.finalApprovedTotalDzd} DZD",
                    color = ElectricGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                  )
                }
              }
            }
          }

          // Remote Signature Box
          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
              border = BorderStroke(1.dp, if (session.isSignedRemotely) ElectricGreen else PerformanceRedGlow)
            ) {
              Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                  text = if (session.isSignedRemotely) "✅ BON POUR ACCORD SIGNÉ NUMÉRIQUEMENT" else "✍️ SIGNATURE ÉLECTRONIQUE À DISTANCE DU CLIENT",
                  color = if (session.isSignedRemotely) ElectricGreen else PureWhite,
                  fontWeight = FontWeight.Black,
                  fontSize = 12.sp
                )
                if (session.isSignedRemotely) {
                  Text(
                    text = "Signé par ${session.clientName} • Horodatage certifié : ${session.signatureTimestamp}",
                    color = AluminumLight,
                    fontSize = 11.sp
                  )
                } else {
                  Text(
                    text = "En validant ce devis à distance, le client autorise l'atelier à commander les pièces sélectionnées et lancer les travaux.",
                    color = AluminumLight,
                    fontSize = 11.sp
                  )
                  Button(
                    onClick = onApproveAndSignRemotely,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricGreen, contentColor = GraphiteBlack)
                  ) {
                    Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Signer & Approuver le Devis (${session.finalApprovedTotalDzd} DZD)", fontWeight = FontWeight.Black)
                  }
                }

                OutlinedButton(
                  onClick = onSendWhatsAppLink,
                  modifier = Modifier.fillMaxWidth(),
                  border = BorderStroke(1.dp, TechCyan)
                ) {
                  Icon(Icons.Default.Share, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Envoyer le Lien de Signature au Client via WhatsApp / SMS", color = TechCyan, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                }
              }
            }
          }
        }
      }
    }
  }
}
