package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UltraProSubAccount
import com.example.data.model.UltraProSubAccountRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUltraProSubAccountModal(
  initialAccount: UltraProSubAccount? = null,
  currentAccounts: List<UltraProSubAccount>,
  onSaveAccount: (UltraProSubAccount) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedRole by remember {
    mutableStateOf(initialAccount?.role ?: UltraProSubAccountRole.CONSEILLER_CLIENT)
  }
  var fullName by remember { mutableStateOf(initialAccount?.fullName ?: "") }
  var email by remember { mutableStateOf(initialAccount?.email ?: "") }
  var phone by remember { mutableStateOf(initialAccount?.phone ?: "0550 ") }
  var accessPin by remember { mutableStateOf(initialAccount?.accessPin ?: "${(1000..9999).random()}") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  // Check role limits
  val currentCountOfRole = currentAccounts.count { it.role == selectedRole && it.id != initialAccount?.id }
  val isRoleLimitReached = currentCountOfRole >= selectedRole.maxLimit && initialAccount?.role != selectedRole

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.88f)
        .testTag("modal_create_sub_account"),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, Color(selectedRole.badgeColorHex))
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
            Icon(
              if (initialAccount != null) Icons.Default.Edit else Icons.Default.PersonAdd,
              contentDescription = null,
              tint = Color(selectedRole.badgeColorHex),
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = if (initialAccount != null) "MODIFIER SOUS-COMPTE" else "NOUVEAU SOUS-COMPTE ULTRA PRO",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = PureWhite
                )
              )
              Text(
                text = "Attribution des permissions et du département",
                style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumLight)
          }
        }

        Divider(color = MetalBorder, modifier = Modifier.padding(vertical = 12.dp))

        // Form Fields Scrollable
        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Role Selection Chips
          Text(
            text = "CHOISIR LE RÔLE SPÉCIALISÉ :",
            style = MaterialTheme.typography.labelSmall.copy(
              color = AluminumMuted,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          )

          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            UltraProSubAccountRole.entries.forEach { role ->
              val isSelected = selectedRole == role
              val count = currentAccounts.count { it.role == role }
              val isFull = count >= role.maxLimit && initialAccount?.role != role

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) Color(role.badgeColorHex).copy(alpha = 0.2f) else MetalCard,
                border = BorderStroke(
                  if (isSelected) 1.5.dp else 1.dp,
                  if (isSelected) Color(role.badgeColorHex) else MetalBorder
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable(enabled = !isFull || isSelected) {
                    selectedRole = role
                    errorMessage = null
                  }
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    RadioButton(
                      selected = isSelected,
                      onClick = {
                        if (!isFull || isSelected) {
                          selectedRole = role
                          errorMessage = null
                        }
                      },
                      colors = RadioButtonDefaults.colors(
                        selectedColor = Color(role.badgeColorHex)
                      )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                          text = role.label,
                          style = MaterialTheme.typography.labelMedium.copy(
                            color = if (isFull && !isSelected) AluminumMuted else PureWhite,
                            fontWeight = FontWeight.Bold
                          )
                        )
                      }
                      Text(
                        text = "${role.department} • ${role.description}",
                        style = MaterialTheme.typography.bodySmall.copy(
                          color = AluminumGray,
                          fontSize = 10.5.sp
                        ),
                        maxLines = 2
                      )
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isFull) PerformanceRedDark else GraphiteBlack,
                    border = BorderStroke(0.5.dp, if (isFull) PerformanceRed else MetalBorder)
                  ) {
                    Text(
                      text = if (isFull) "MAX ATTEINT ($count/${role.maxLimit})" else "$count/${role.maxLimit} actif(s)",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isFull) PureWhite else AluminumLight,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                      ),
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                  }
                }
              }
            }
          }

          if (isRoleLimitReached) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = PerformanceRedDark.copy(alpha = 0.3f),
              border = BorderStroke(1.dp, PerformanceRed)
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = PerformanceRed, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Limite maximale atteinte pour ce rôle (${selectedRole.maxLimit} max inclus dans le pack Ultra Pro).",
                  style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontSize = 11.sp)
                )
              }
            }
          }

          // Full Name
          OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Nom et Prénom du collaborateur") },
            placeholder = { Text("ex: Karim Mansouri") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(selectedRole.badgeColorHex)) },
            modifier = Modifier.fillMaxWidth().testTag("input_sub_account_name"),
            singleLine = true
          )

          // Email
          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Adresse Email professionnelle") },
            placeholder = { Text("collaborateur@automekanik.dz") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TechCyan) },
            modifier = Modifier.fillMaxWidth().testTag("input_sub_account_email"),
            singleLine = true
          )

          // Phone & Access PIN Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = phone,
              onValueChange = { phone = it },
              label = { Text("Téléphone mobile") },
              placeholder = { Text("0550 00 00 00") },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = ElectricGreen) },
              modifier = Modifier.weight(1.2f),
              singleLine = true
            )

            OutlinedTextField(
              value = accessPin,
              onValueChange = { accessPin = it.take(6) },
              label = { Text("Code PIN Accès") },
              placeholder = { Text("1234") },
              leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MechanicalOrange) },
              modifier = Modifier.weight(0.8f),
              singleLine = true
            )
          }

          // Permissions summary for the selected role
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MetalCard),
            border = BorderStroke(1.dp, MetalBorder)
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "DROITS & PERMISSIONS ACCORDÉS AUTOMATIQUEMENT :",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = AluminumMuted,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                )
              )

              val defaultPermissions = when (selectedRole) {
                UltraProSubAccountRole.GERANT_PRINCIPAL -> listOf(
                  "Gouvernance complète 360°",
                  "Gestion des sous-comptes & permissions",
                  "Validation clôture comptable & bilans",
                  "Contrôle total inventaire & audit"
                )
                UltraProSubAccountRole.CHEF_CONSEILLER_CLIENT -> listOf(
                  "Supervision de l'équipe de réception & conseillers",
                  "Validation des devis prioritaires et grands comptes",
                  "Attribution des véhicules aux chefs d'équipe atelier",
                  "Scan OCR Carte Grise et planning des baies de réparation"
                )
                UltraProSubAccountRole.CONSEILLER_CLIENT -> listOf(
                  "Accueil client & Prise de rendez-vous",
                  "Création et modification des Ordres de Réparation (OR)",
                  "Génération et signature de devis clients",
                  "Scan OCR Carte Grise et dictée vocale des pannes"
                )
                UltraProSubAccountRole.CHEF_ATELIER -> listOf(
                  "Supervision technique des 5 mécaniciens de l'atelier",
                  "Validation & Approbation technique des demandes de pièces",
                  "Contrôle qualité avant essai sur route & livraison",
                  "Affectation des travaux de réparation et suivi des temps"
                )
                UltraProSubAccountRole.MECANICIEN_ATELIER -> listOf(
                  "Diagnostic des pannes et démontage mécanique",
                  "Recherche de pièces détachées selon le châssis / VIN",
                  "Émission des demandes de pièces de rechange au chef d'atelier",
                  "Saisie des temps d'intervention et fiches de contrôle"
                )
                UltraProSubAccountRole.ADMIN_MAGASIN -> listOf(
                  "Gestion globale du stock et inventaire",
                  "Confirmation définitive de sortie de stock (Facture/OR auto)",
                  "Création des références et équivalences OEM",
                  "Bons de commande fournisseurs grossistes"
                )
                UltraProSubAccountRole.VENDEUR_MAGASIN -> listOf(
                  "Réception des demandes pièces approuvées par le Chef d'Atelier",
                  "Préparation des pièces en rayon selon données châssis",
                  "Vente directe au comptoir et émission des bons",
                  "Consultation stock et disponibilité en temps réel"
                )
                UltraProSubAccountRole.COMPTABLE_PRINCIPAL -> listOf(
                  "Gestion complète de la trésorerie et comptes bancaires",
                  "Déclaration et suivi de la TVA légale 19%",
                  "Rapprochements CIB/Edahabia et timbres fiscaux",
                  "Clôture officielle des factures"
                )
                UltraProSubAccountRole.ANALYSTE_FACTURES -> listOf(
                  "Analyse des factures d'achat fournisseurs",
                  "Vérification conformité devis vs factures",
                  "Suivi des impayés et rapprochement comptable"
                )
                UltraProSubAccountRole.AUDITEUR_QUALITE -> listOf(
                  "Contrôle qualité des interventions mécaniques",
                  "Audit du respect des normes constructeurs OEM",
                  "Vérification des marges et des mouvements de stock",
                  "Édition de rapports d'inspection pour le Gérant"
                )
              }

              defaultPermissions.forEach { perm ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(selectedRole.badgeColorHex), modifier = Modifier.size(13.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(text = perm, style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp))
                }
              }
            }
          }

          errorMessage?.let { err ->
            Text(text = err, color = PerformanceRed, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text("ANNULER", color = AluminumLight)
          }

          Button(
            onClick = {
              if (fullName.isBlank()) {
                errorMessage = "Veuillez saisir le nom et prénom du collaborateur."
                return@Button
              }
              if (email.isBlank() || !email.contains("@")) {
                errorMessage = "Veuillez saisir une adresse email valide."
                return@Button
              }
              if (accessPin.length < 4) {
                errorMessage = "Le code PIN doit comporter au moins 4 chiffres."
                return@Button
              }
              if (isRoleLimitReached) {
                errorMessage = "Limite maximale de sous-comptes atteinte pour ce rôle."
                return@Button
              }

              val initials = fullName.trim().split(" ")
                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                .take(2)
                .joinToString("")

              val newAcc = UltraProSubAccount(
                id = initialAccount?.id ?: "SUB-${(100..999).random()}",
                fullName = fullName.trim(),
                email = email.trim(),
                phone = phone.trim(),
                role = selectedRole,
                accessPin = accessPin.trim(),
                isActive = initialAccount?.isActive ?: true,
                permissions = when (selectedRole) {
                  UltraProSubAccountRole.GERANT_PRINCIPAL -> listOf("Accès Administrateur Total", "Gestion sous-comptes", "Clôture fiscale", "Inventaire & Audit")
                  UltraProSubAccountRole.CHEF_CONSEILLER_CLIENT -> listOf("Supervision accueil", "Validation devis", "Coordination conseillers", "Planning baies")
                  UltraProSubAccountRole.CONSEILLER_CLIENT -> listOf("Accueil client", "Devis DZD", "Ordres de réparation", "Planning RDV")
                  UltraProSubAccountRole.CHEF_ATELIER -> listOf("Supervision 5 mécaniciens", "Approbation pièces", "Contrôle qualité", "Affectation travaux")
                  UltraProSubAccountRole.MECANICIEN_ATELIER -> listOf("Diagnostic mécanique", "Recherche pièces VIN", "Demande pièces atelier", "Fiches travaux")
                  UltraProSubAccountRole.ADMIN_MAGASIN -> listOf("Gestion inventaire", "Sortie stock finale", "Commandes fournisseurs", "Réapprovisionnement")
                  UltraProSubAccountRole.VENDEUR_MAGASIN -> listOf("Réception demandes pièces", "Préparation casiers", "Vente comptoir pièces", "Bons de sortie")
                  UltraProSubAccountRole.COMPTABLE_PRINCIPAL -> listOf("Trésorerie", "TVA 19%", "Clôture factures", "Encaissements CIB")
                  UltraProSubAccountRole.ANALYSTE_FACTURES -> listOf("Analyse factures", "Suivi impayés", "Rapprochement devis/factures")
                  UltraProSubAccountRole.AUDITEUR_QUALITE -> listOf("Audit qualité", "Contrôle conformité", "Audit marges & stocks")
                },
                avatarInitials = if (initials.isNotBlank()) initials else "CO",
                lastActive = initialAccount?.lastActive ?: "Créé à l'instant",
                actionsCount = initialAccount?.actionsCount ?: 0
              )

              onSaveAccount(newAcc)
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(selectedRole.badgeColorHex),
              contentColor = PureWhite
            ),
            modifier = Modifier.weight(1.4f).testTag("btn_save_sub_account")
          ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (initialAccount != null) "ENREGISTRER MODIFICATIONS" else "VALIDER & CRÉER COMPTE",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      }
    }
  }
}
