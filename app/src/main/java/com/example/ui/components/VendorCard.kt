package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Vendor
import com.example.ui.theme.*

@Composable
fun VendorCard(
  vendor: Vendor,
  onCallVendor: (String) -> Unit,
  onOpenStore: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("vendor_card_${vendor.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(
      1.dp,
      if (vendor.isPremium) MechanicalOrange.copy(alpha = 0.6f) else MetalBorder
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = vendor.name,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PureWhite,
                fontSize = 15.sp
              )
            )
            if (vendor.isVerified) {
              Spacer(modifier = Modifier.width(6.dp))
              Icon(
                Icons.Default.Verified,
                contentDescription = "Vendeur Vérifié",
                tint = TechCyan,
                modifier = Modifier.size(16.dp)
              )
            }
          }

          Text(
            text = vendor.specialty,
            style = MaterialTheme.typography.bodySmall.copy(
              color = MechanicalOrange,
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Medium
            )
          )
        }

        if (vendor.isPremium) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MechanicalOrangeDark.copy(alpha = 0.25f),
            border = BorderStroke(0.5.dp, MechanicalOrange)
          ) {
            Text(
              text = "PREMIUM",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MechanicalOrangeGlow,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
              ),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Location & Commercial Registry (RC)
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(
          Icons.Default.LocationOn,
          contentDescription = null,
          tint = AluminumMuted,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "${vendor.wilaya} • ${vendor.commune}",
          style = MaterialTheme.typography.bodySmall.copy(
            color = AluminumLight,
            fontSize = 11.5.sp
          )
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
          text = vendor.registerCommerceNumber,
          style = MaterialTheme.typography.labelSmall.copy(
            color = AluminumMuted,
            fontSize = 9.5.sp
          )
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action Row (Phone call + Store info)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            Icons.Default.Inventory2,
            contentDescription = null,
            tint = ElectricGreen,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${vendor.inStockItemsCount} réf. en stock",
            style = MaterialTheme.typography.labelSmall.copy(
              color = ElectricGreen,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          )
        }

        Button(
          onClick = { onCallVendor(vendor.phone) },
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = PerformanceRed,
            contentColor = PureWhite
          ),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.height(34.dp)
        ) {
          Icon(
            Icons.Default.Phone,
            contentDescription = "Appeler",
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Appeler",
            style = MaterialTheme.typography.labelMedium.copy(
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold
            )
          )
        }
      }
    }
  }
}
