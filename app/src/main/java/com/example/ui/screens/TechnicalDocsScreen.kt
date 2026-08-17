package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.repository.MekanikRepository
import com.example.ui.components.MekanikPrimaryButton
import com.example.ui.components.MekanikSecondaryButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

enum class TechnicalDocSection(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
  PROCEDURES("Manuels & Procédures", Icons.Default.MenuBook),
  SCHEMAS_ELEC("Schémas Électriques", Icons.Default.ElectricBolt),
  COUPLES_SERRAGE("Couples de Serrage (Nm)", Icons.Default.Handyman),
  FLUIDES_CAPACITES("Fluides & Capacités", Icons.Default.WaterDrop),
  SOURCES_INDUSTRIE("Sources & Intégration API", Icons.Default.Hub)
}

@Composable
fun TechnicalDocsScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedSection by remember { mutableStateOf(TechnicalDocSection.PROCEDURES) }
  var docSearchQuery by remember { mutableStateOf("") }
  var selectedProcedureDetail by remember { mutableStateOf<RepairManualProcedure?>(null) }
  var selectedWiringDetail by remember { mutableStateOf<WiringDiagramDoc?>(null) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("technical_docs_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Bar with Back Button
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onBack,
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(MetalCard)
              .testTag("btn_back_docs")
          ) {
            Icon(
              Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Retour",
              tint = PureWhite
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "DOCUMENTATION TECHNIQUE",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite,
                fontSize = 18.sp
              )
            )
            Text(
              text = "Données constructeur, schémas, couples de serrage & fluides",
              style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.5.sp)
            )
          }
        }
      }
    }

    // Active Vehicle Context Banner
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MetalCard),
        border = BorderStroke(1.dp, MetalBorder)
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.DirectionsCar,
              contentDescription = null,
              tint = PerformanceRed,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "${uiState.activeVehicle.brand} ${uiState.activeVehicle.model}",
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = PureWhite
                )
              )
              Text(
                text = "Moteur : ${uiState.activeVehicle.engineCode} (${uiState.activeVehicle.displacement}) • ${uiState.activeVehicle.gearbox}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumLight,
                  fontSize = 11.sp
                )
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = GraphiteBlack,
            border = BorderStroke(0.5.dp, MetalBorder)
          ) {
            Text(
              text = "Plateforme ${uiState.activeVehicle.platform}",
              style = MaterialTheme.typography.labelSmall.copy(
                color = TechnicalBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              ),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }
        }
      }
    }

    // Search Field
    item {
      OutlinedTextField(
        value = docSearchQuery,
        onValueChange = { docSearchQuery = it },
        placeholder = { Text("Rechercher une procédure, couple, fluide, fusible...", color = AluminumMuted, fontSize = 13.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TechnicalBlue) },
        trailingIcon = {
          if (docSearchQuery.isNotEmpty()) {
            IconButton(onClick = { docSearchQuery = "" }) {
              Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = AluminumGray)
            }
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("doc_search_input"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = MetalSurface,
          unfocusedContainerColor = MetalSurface,
          focusedBorderColor = TechnicalBlue,
          unfocusedBorderColor = MetalBorder,
          focusedTextColor = PureWhite,
          unfocusedTextColor = OffWhite
        ),
        shape = RoundedCornerShape(10.dp),
        singleLine = true
      )
    }

    // Horizontal Section Tabs
    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(TechnicalDocSection.entries) { section ->
          val isSelected = section == selectedSection
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) PerformanceRed else MetalCard,
            border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
            onClick = { selectedSection = section },
            modifier = Modifier.testTag("tab_doc_${section.name.lowercase()}")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                section.icon,
                contentDescription = null,
                tint = if (isSelected) PureWhite else AluminumLight,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = section.label,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = if (isSelected) PureWhite else AluminumLight,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 12.sp
                )
              )
            }
          }
        }
      }
    }

    // Dynamic Section Content
    when (selectedSection) {
      TechnicalDocSection.PROCEDURES -> {
        val filteredProcedures = MekanikRepository.technicalProcedures.filter {
          docSearchQuery.isBlank() || it.title.contains(docSearchQuery, ignoreCase = true) || it.vehicleCompatibility.contains(docSearchQuery, ignoreCase = true)
        }

        item {
          Text(
            text = "MANUELS DE RÉPARATION & GUIDES PAS À PAS (${filteredProcedures.size})",
            style = MaterialTheme.typography.labelMedium.copy(
              color = AluminumMuted,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          )
        }

        items(filteredProcedures) { proc ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { selectedProcedureDetail = proc }
              .testTag("procedure_card_${proc.id}"),
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
                  color = TechnicalBlue.copy(alpha = 0.2f),
                  border = BorderStroke(0.5.dp, TechnicalBlue)
                ) {
                  Text(
                    text = proc.systemCategory.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = TechnicalBlue,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }

                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = GraphiteBlack
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      Icons.Default.Timer,
                      contentDescription = null,
                      tint = MechanicalOrange,
                      modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "${proc.estimatedTimeHours}h • ${proc.difficultyLevel}",
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = AluminumLight,
                        fontSize = 10.5.sp
                      )
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = proc.title,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = PureWhite
                )
              )

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = "Compatibilité : ${proc.vehicleCompatibility}",
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
                Text(
                  text = "${proc.stepByStepInstructions.size} étapes détaillées",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = ElectricGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                  )
                )

                Text(
                  text = "Consulter le manuel →",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = PerformanceRedGlow,
                    fontWeight = FontWeight.Bold
                  )
                )
              }
            }
          }
        }
      }

      TechnicalDocSection.SCHEMAS_ELEC -> {
        val filteredWiring = MekanikRepository.wiringDiagrams.filter {
          docSearchQuery.isBlank() || it.title.contains(docSearchQuery, ignoreCase = true) || it.circuitName.contains(docSearchQuery, ignoreCase = true)
        }

        item {
          Text(
            text = "SCHÉMAS ÉLECTRIQUES & BROCHAGE CALCULATEURS (${filteredWiring.size})",
            style = MaterialTheme.typography.labelMedium.copy(
              color = AluminumMuted,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          )
        }

        items(filteredWiring) { diag ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { selectedWiringDetail = diag }
              .testTag("wiring_card_${diag.id}"),
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
                Text(
                  text = diag.circuitName.uppercase(),
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = TechCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  )
                )
                Text(
                  text = diag.schemaDiagramRef,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = AluminumMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                  )
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = diag.title,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = PureWhite
                )
              )

              Spacer(modifier = Modifier.height(8.dp))

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = GraphiteBlack,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = diag.ecuPins,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = AluminumLight,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.5.sp,
                    lineHeight = 15.sp
                  ),
                  modifier = Modifier.padding(8.dp)
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = "📍 ${diag.fuseBoxLocation}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumGray,
                  fontSize = 11.sp
                )
              )
            }
          }
        }
      }

      TechnicalDocSection.COUPLES_SERRAGE -> {
        val filteredTorques = MekanikRepository.torqueSpecs.filter {
          docSearchQuery.isBlank() || it.componentName.contains(docSearchQuery, ignoreCase = true)
        }

        item {
          Text(
            text = "COUPLES DE SERRAGE CONSTRUCTEUR (Nm & ANGULAIRE)",
            style = MaterialTheme.typography.labelMedium.copy(
              color = AluminumMuted,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          )
        }

        items(filteredTorques) { spec ->
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MetalCard,
            border = BorderStroke(1.dp, MetalBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = spec.componentName,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                  )
                )
                Text(
                  text = "Condition : ${spec.replacementRequirement}",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = AluminumMuted,
                    fontSize = 11.sp
                  )
                )
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = GraphiteBlack,
                border = BorderStroke(1.dp, PerformanceRed.copy(alpha = 0.5f))
              ) {
                Text(
                  text = spec.torqueNm,
                  style = MaterialTheme.typography.bodyMedium.copy(
                    color = PerformanceRedGlow,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.5.sp
                  ),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }

      TechnicalDocSection.FLUIDES_CAPACITES -> {
        val filteredFluids = MekanikRepository.fluidSpecs.filter {
          docSearchQuery.isBlank() || it.fluidType.contains(docSearchQuery, ignoreCase = true) || it.oemNorm.contains(docSearchQuery, ignoreCase = true)
        }

        item {
          Text(
            text = "SPÉCIFICATIONS FLUIDES & CAPACITÉS DES RÉSERVOIRS",
            style = MaterialTheme.typography.labelMedium.copy(
              color = AluminumMuted,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          )
        }

        items(filteredFluids) { fluid ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MetalCard),
            border = BorderStroke(1.dp, MetalBorder)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = fluid.fluidType,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                  )
                )
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = GraphiteBlack,
                  border = BorderStroke(0.5.dp, TechCyan)
                ) {
                  Text(
                    text = fluid.capacityLiters,
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = TechCyan,
                      fontWeight = FontWeight.Bold,
                      fontFamily = FontFamily.Monospace,
                      fontSize = 11.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = "Norme OEM : ${fluid.oemNorm}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MechanicalOrange,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 12.sp
                )
              )

              Text(
                text = "Grade prescrit : ${fluid.recommendedGrade}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = OffWhite,
                  fontSize = 11.5.sp
                )
              )

              Text(
                text = "Intervalle de service : ${fluid.serviceInterval}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumGray,
                  fontSize = 11.sp
                )
              )
            }
          }
        }
      }

      TechnicalDocSection.SOURCES_INDUSTRIE -> {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
            border = BorderStroke(1.dp, TechnicalBlue)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.IntegrationInstructions,
                  contentDescription = null,
                  tint = TechnicalBlue,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "ARCHITECTURE & SOURCES DE DONNÉES TECHNIQUES",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite
                  )
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Mekanik+ intègre et normalise les catalogues et bases de données professionnelles internationales via connecteurs REST API, protocoles OEM PassThru et webhooks temps réel.",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumLight,
                  fontSize = 11.5.sp,
                  lineHeight = 16.sp
                )
              )
            }
          }
        }

        items(MekanikRepository.industryDataSources) { source ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MetalCard),
            border = BorderStroke(1.dp, MetalBorder)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = source.name,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                  )
                )
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = ElectricGreen.copy(alpha = 0.2f),
                  border = BorderStroke(0.5.dp, ElectricGreen)
                ) {
                  Text(
                    text = source.integrationStatus,
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = ElectricGreen,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "${source.providerType} • ${source.apiStandard}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumGray,
                  fontSize = 11.sp
                )
              )

              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = source.description,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = OffWhite,
                  fontSize = 11.5.sp,
                  lineHeight = 16.sp
                )
              )

              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Couverture : ${source.coverage}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MechanicalOrange,
                  fontWeight = FontWeight.Medium,
                  fontSize = 11.sp
                )
              )
            }
          }
        }
      }
    }
  }

  // Procedure Detailed Modal
  if (selectedProcedureDetail != null) {
    val proc = selectedProcedureDetail!!
    AlertDialog(
      onDismissRequest = { selectedProcedureDetail = null },
      title = {
        Text(
          text = proc.title,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Black,
            color = PureWhite
          )
        )
      },
      text = {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 420.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          item {
            Text(
              text = "OUTILLAGE SPÉCIAL REQUIS :",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MechanicalOrange,
                fontWeight = FontWeight.Bold
              )
            )
            proc.requiredTools.forEach { tool ->
              Text(
                text = "• $tool",
                style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 11.5.sp)
              )
            }
          }

          item {
            Divider(color = MetalBorder, thickness = 1.dp)
            Text(
              text = "PROCÉDURE CHRONOLOGIQUE :",
              style = MaterialTheme.typography.labelSmall.copy(
                color = TechnicalBlue,
                fontWeight = FontWeight.Bold
              )
            )
            Spacer(modifier = Modifier.height(4.dp))
            proc.stepByStepInstructions.forEach { step ->
              Text(
                text = step,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumLight,
                  fontSize = 11.5.sp,
                  lineHeight = 16.sp
                ),
                modifier = Modifier.padding(bottom = 6.dp)
              )
            }
          }

          if (proc.warningNote != null) {
            item {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF2C1518),
                border = BorderStroke(1.dp, PerformanceRed),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = "⚠️ ${proc.warningNote}",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = PerformanceRedGlow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  ),
                  modifier = Modifier.padding(8.dp)
                )
              }
            }
          }

          if (proc.torqueSpecs.isNotEmpty()) {
            item {
              Text(
                text = "COUPLES DE SERRAGE ASSOCIÉS :",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = TechCyan,
                  fontWeight = FontWeight.Bold
                )
              )
              proc.torqueSpecs.forEach { t ->
                Text(
                  text = "• $t",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = PureWhite,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                  )
                )
              }
            }
          }
        }
      },
      confirmButton = {
        MekanikPrimaryButton(
          text = "FERMER",
          onClick = { selectedProcedureDetail = null }
        )
      },
      containerColor = MetalCard
    )
  }

  // Wiring Detailed Modal
  if (selectedWiringDetail != null) {
    val diag = selectedWiringDetail!!
    AlertDialog(
      onDismissRequest = { selectedWiringDetail = null },
      title = {
        Text(
          text = diag.title,
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
            text = "Circuit : ${diag.circuitName}",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = TechCyan,
              fontWeight = FontWeight.Bold
            )
          )
          Text(
            text = "Brochage : ${diag.ecuPins}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = AluminumLight,
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp
            )
          )
          Text(
            text = "Boîte à fusibles : ${diag.fuseBoxLocation}",
            style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 11.sp)
          )
          Text(
            text = "Relais : ${diag.relayCodes}",
            style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 11.sp)
          )
          Divider(color = MetalBorder)
          Text(
            text = "Codes couleurs faisceau :",
            style = MaterialTheme.typography.labelSmall.copy(color = MechanicalOrange, fontWeight = FontWeight.Bold)
          )
          diag.wireColorCodes.forEach { c ->
            Text(
              text = "• $c",
              style = MaterialTheme.typography.bodySmall.copy(color = OffWhite, fontSize = 11.sp)
            )
          }
        }
      },
      confirmButton = {
        MekanikPrimaryButton(
          text = "FERMER",
          onClick = { selectedWiringDetail = null }
        )
      },
      containerColor = MetalCard
    )
  }
}
