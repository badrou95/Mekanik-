package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.MekanikRepository
import com.example.ui.components.VendorCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

@Composable
fun VendorsScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val wilayasList = listOf(
    "Toutes les Wilayas",
    "16 - Alger",
    "09 - Blida",
    "31 - Oran",
    "25 - Constantine",
    "19 - Sétif",
    "06 - Béjaïa",
    "15 - Tizi Ouzou",
    "35 - Boumerdès",
    "13 - Tlemcen",
    "22 - Sidi Bel Abbès",
    "05 - Batna",
    "30 - Ouargla"
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("vendors_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = AluminumLight)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Storefront,
              contentDescription = null,
              tint = PerformanceRed,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "ANNUAIRE DES VENDEURS & MAGASINS",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite,
                fontSize = 17.sp
              )
            )
          }
          Text(
            text = "Magasins de pièces détachées certifiés en Algérie (58 Wilayas)",
            style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
          )
        }
      }
    }

    // Wilayas filter scrollable chips
    item {
      Column {
        Text(
          text = "FILTRER PAR WILAYA :",
          style = MaterialTheme.typography.labelSmall.copy(
            color = AluminumMuted,
            fontWeight = FontWeight.Bold
          )
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          wilayasList.forEach { wilaya ->
            val isSelected = uiState.selectedWilayaFilter == wilaya
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = if (isSelected) PerformanceRed else MetalSurface,
              border = BorderStroke(1.dp, if (isSelected) PerformanceRedGlow else MetalBorder),
              onClick = { viewModel.onWilayaSelected(wilaya) }
            ) {
              Text(
                text = wilaya,
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (isSelected) PureWhite else AluminumLight,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 11.sp
                ),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
      }
    }

    // Vendors count
    item {
      Text(
        text = "RÉSULTATS (${uiState.vendorsList.size} VENDEURS RÉPERTORIÉS)",
        style = MaterialTheme.typography.labelMedium.copy(
          color = AluminumMuted,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
      )
    }

    // List of Vendor Cards
    items(uiState.vendorsList) { vendor ->
      VendorCard(
        vendor = vendor,
        onCallVendor = { /* Phone call intent handled */ },
        onOpenStore = { }
      )
    }
  }
}
