package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MekanikLogo(
  modifier: Modifier = Modifier,
  showTagline: Boolean = false,
  isSmall: Boolean = false
) {
  val infiniteTransition = rememberInfiniteTransition(label = "logo_anim")
  val pulseGlow by infiniteTransition.animateFloat(
    initialValue = 0.8f,
    targetValue = 1.2f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glow"
  )

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
  ) {
    // Custom Vector Emblem combining Gear + Wrench + Plus + Car Silhouette
    Box(
      modifier = Modifier
        .size(if (isSmall) 34.dp else 46.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(
          brush = Brush.linearGradient(
            colors = listOf(MetalGray, GraphiteBlack)
          )
        ),
      contentAlignment = Alignment.Center
    ) {
      Canvas(
        modifier = Modifier
          .fillMaxSize()
          .padding(4.dp)
      ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2.6f

        // Mechanical Gear Teeth Ring
        val teethCount = 8
        for (i in 0 until teethCount) {
          val angle = (i * 360f / teethCount) * (Math.PI / 180f)
          val toothX = center.x + (radius * 1.05f) * cos(angle).toFloat()
          val toothY = center.y + (radius * 1.05f) * sin(angle).toFloat()
          drawCircle(
            color = AluminumGray,
            radius = size.minDimension * 0.08f,
            center = Offset(toothX, toothY)
          )
        }

        // Inner Gear Rim
        drawCircle(
          color = MetalBorder,
          radius = radius,
          center = center,
          style = Stroke(width = 2.dp.toPx())
        )

        // Automotive Aerodynamic Silhouette Line
        val path = Path().apply {
          moveTo(center.x - radius * 0.9f, center.y + radius * 0.3f)
          cubicTo(
            center.x - radius * 0.4f, center.y - radius * 0.5f,
            center.x + radius * 0.2f, center.y - radius * 0.5f,
            center.x + radius * 0.9f, center.y + radius * 0.3f
          )
        }
        drawPath(
          path = path,
          color = AluminumLight,
          style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        // Mechanical Vibrant PLUS '+' Icon
        val plusSize = radius * 0.55f * pulseGlow
        // Horizontal bar
        drawLine(
          color = PerformanceRed,
          start = Offset(center.x - plusSize, center.y + radius * 0.1f),
          end = Offset(center.x + plusSize, center.y + radius * 0.1f),
          strokeWidth = 3.5.dp.toPx(),
          cap = StrokeCap.Round
        )
        // Vertical bar
        drawLine(
          color = MechanicalOrange,
          start = Offset(center.x, center.y + radius * 0.1f - plusSize),
          end = Offset(center.x, center.y + radius * 0.1f + plusSize),
          strokeWidth = 3.5.dp.toPx(),
          cap = StrokeCap.Round
        )
      }
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "MEKANIK",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Black,
            fontSize = if (isSmall) 17.sp else 22.sp,
            letterSpacing = 1.2.sp,
            color = OffWhite
          )
        )
        Text(
          text = "+",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Black,
            fontSize = if (isSmall) 20.sp else 26.sp,
            color = PerformanceRedGlow
          )
        )
      }

      if (showTagline) {
        Text(
          text = "PLATEFORME AUTOMOBILE ALGÉRIE",
          style = MaterialTheme.typography.labelMedium.copy(
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = AluminumMuted
          )
        )
      }
    }
  }
}
