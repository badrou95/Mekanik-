package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Vehicle
import com.example.ui.theme.*

@Composable
fun VehicleHeroCard(
  vehicle: Vehicle,
  onOpenCatalog: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MetalCard),
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = vehicle.brand.uppercase(),
              style = MaterialTheme.typography.labelMedium.copy(
                color = if (vehicle.isEv) TechCyan else MechanicalOrange,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
              )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MetalSurface,
              border = BorderStroke(0.5.dp, MetalBorder)
            ) {
              Text(
                text = vehicle.generation,
                style = MaterialTheme.typography.labelSmall.copy(
                  color = AluminumLight,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "${vehicle.brand} ${vehicle.model}",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              color = PureWhite,
              fontSize = 18.sp
            )
          )

          Text(
            text = "${vehicle.displacement} • ${vehicle.powerCh} ch (${vehicle.powerKw} kW) • ${vehicle.gearbox}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = AluminumGray,
              fontSize = 12.sp
            )
          )
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = PerformanceRedDark.copy(alpha = 0.3f),
          border = BorderStroke(1.dp, PerformanceRed.copy(alpha = 0.6f))
        ) {
          Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "MOTEUR",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = AluminumGray
              )
            )
            Text(
              text = vehicle.engineCode,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = PureWhite,
                fontSize = 14.sp
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Technical Badges Grid (VIN, Platform, Year, Fuel)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        TechInfoBadge(
          icon = Icons.Default.QrCode,
          label = "VIN",
          value = vehicle.vin.take(10) + "...",
          modifier = Modifier.weight(1f)
        )
        TechInfoBadge(
          icon = Icons.Default.CalendarToday,
          label = "Année",
          value = "${vehicle.exactYear} (${vehicle.yearRange})",
          modifier = Modifier.weight(1.1f)
        )
        TechInfoBadge(
          icon = Icons.Default.LocalGasStation,
          label = "Énergie",
          value = vehicle.fuelType,
          modifier = Modifier.weight(1f)
        )
      }

      if (vehicle.generationNotes.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = GraphiteBlack,
          border = BorderStroke(1.dp, WarningAmber.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.Info,
              contentDescription = null,
              tint = WarningAmber,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = vehicle.generationNotes,
              style = MaterialTheme.typography.bodySmall.copy(
                color = AluminumLight,
                fontSize = 11.sp,
                lineHeight = 15.sp
              )
            )
          }
        }
      }
    }
  }
}

@Composable
private fun TechInfoBadge(
  icon: ImageVector,
  label: String,
  value: String,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    color = MetalSurface,
    border = BorderStroke(0.5.dp, MetalBorder)
  ) {
    Column(modifier = Modifier.padding(6.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = AluminumMuted,
          modifier = Modifier.size(11.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = label,
          style = MaterialTheme.typography.labelSmall.copy(
            color = AluminumMuted,
            fontSize = 9.sp
          )
        )
      }
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.bodySmall.copy(
          fontWeight = FontWeight.Bold,
          color = OffWhite,
          fontSize = 10.5.sp
        ),
        maxLines = 1
      )
    }
  }
}
