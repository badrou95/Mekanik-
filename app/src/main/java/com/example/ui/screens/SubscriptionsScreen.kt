package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PricingPlan
import com.example.data.repository.MekanikRepository
import com.example.ui.components.MekanikPrimaryButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

@Composable
fun SubscriptionsScreen(
  uiState: MekanikUiState,
  viewModel: MekanikViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GraphiteDark)
      .testTag("subscriptions_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Bar
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
          Text(
            text = "ABONNEMENTS MEKANIK+ PRO",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              color = PureWhite,
              fontSize = 18.sp
            )
          )
          Text(
            text = "Tarifs officiels en Dinars Algériens (DZD) • Facturation Entreprise",
            style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray)
          )
        }
      }
    }

    // Plans list
    items(MekanikRepository.pricingPlans) { plan ->
      PlanCard(
        plan = plan,
        isCurrent = plan.id == "plan_pro_atelier",
        onSelectPlan = { viewModel.dismissModals() }
      )
    }
  }
}

@Composable
private fun PlanCard(
  plan: PricingPlan,
  isCurrent: Boolean,
  onSelectPlan: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("subscription_plan_${plan.id}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (plan.isPopular) Color(0xFF192026) else MetalCard
    ),
    border = BorderStroke(
      if (plan.isPopular) 1.5.dp else 1.dp,
      if (plan.isPopular) PerformanceRed else MetalBorder
    )
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = plan.title,
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              color = PureWhite
            )
          )
          Text(
            text = plan.targetUser,
            style = MaterialTheme.typography.bodySmall.copy(
              color = AluminumGray,
              fontSize = 11.5.sp
            )
          )
        }

        if (plan.isPopular) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = PerformanceRed
          ) {
            Text(
              text = plan.badgeText ?: "RECOMMANDÉ",
              style = MaterialTheme.typography.labelSmall.copy(
                color = PureWhite,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp
              ),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(verticalAlignment = Alignment.Bottom) {
        Text(
          text = plan.priceDzd,
          style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Black,
            color = PerformanceRedGlow,
            fontSize = 24.sp
          )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "/ ${plan.billingPeriod}",
          style = MaterialTheme.typography.bodySmall.copy(
            color = AluminumLight,
            fontSize = 12.sp
          ),
          modifier = Modifier.padding(bottom = 3.dp)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      HorizontalDivider(color = MetalBorder, thickness = 1.dp)

      Spacer(modifier = Modifier.height(10.dp))

      plan.features.forEach { feature ->
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(vertical = 3.dp)
        ) {
          Icon(
            Icons.Default.Check,
            contentDescription = null,
            tint = if (plan.isPopular) PerformanceRed else ElectricGreen,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = feature,
            style = MaterialTheme.typography.bodySmall.copy(
              color = OffWhite,
              fontSize = 12.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      MekanikPrimaryButton(
        text = if (isCurrent) "FORFAIT ACTIF" else "CHOISIR CE FORFAIT",
        onClick = onSelectPlan,
        isOrange = !plan.isPopular,
        modifier = Modifier.fillMaxWidth(),
        enabled = !isCurrent
      )
    }
  }
}
