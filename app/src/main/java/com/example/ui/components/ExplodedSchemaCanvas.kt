package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExplodedHotspot
import com.example.data.model.ExplodedSchema
import com.example.ui.theme.*

@Composable
fun ExplodedSchemaCanvas(
  schema: ExplodedSchema,
  selectedCalloutNumber: Int?,
  onHotspotClicked: (ExplodedHotspot) -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "hotspot_pulse")
  val pulseRadius by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.35f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "radius"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("exploded_schema_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
    border = BorderStroke(1.5.dp, MetalBorder)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Header info bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(PerformanceRed)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = schema.title.uppercase(),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = OffWhite,
                fontSize = 13.sp
              )
            )
          }
          Text(
            text = "Catalogue Éclaté Technique • Cliquez sur un repère [1..6]",
            style = MaterialTheme.typography.bodySmall.copy(
              color = AluminumGray,
              fontSize = 11.sp
            )
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MetalSurface,
          border = BorderStroke(1.dp, MetalBorder)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.TouchApp,
              contentDescription = null,
              tint = MechanicalOrange,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Interactif",
              style = MaterialTheme.typography.labelSmall.copy(
                color = AluminumLight,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Technical Blueprint Container
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(230.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFF0F151B))
          .border(1.dp, Color(0xFF2A3440), RoundedCornerShape(12.dp))
      ) {
        // Blueprint Technical Grid & Mechanical Schematics Drawing
        Canvas(modifier = Modifier.fillMaxSize()) {
          val width = size.width
          val height = size.height

          // Grid Lines
          val gridSpacing = 24.dp.toPx()
          var x = 0f
          while (x < width) {
            drawLine(
              color = Color(0x184A6572),
              start = Offset(x, 0f),
              end = Offset(x, height),
              strokeWidth = 1f
            )
            x += gridSpacing
          }
          var y = 0f
          while (y < height) {
            drawLine(
              color = Color(0x184A6572),
              start = Offset(0f, y),
              end = Offset(width, y),
              strokeWidth = 1f
            )
            y += gridSpacing
          }

          // Mechanical Brake Assembly Outlines (ETKA style vector exploded illustration)
          // 1. Disc Outline (#2)
          val discCenterX = width * 0.28f
          val discCenterY = height * 0.52f
          drawCircle(
            color = Color(0xFF3E4F5E),
            radius = height * 0.38f,
            center = Offset(discCenterX, discCenterY),
            style = Stroke(width = 2.dp.toPx())
          )
          drawCircle(
            color = Color(0xFF2A3440),
            radius = height * 0.18f,
            center = Offset(discCenterX, discCenterY),
            style = Stroke(width = 1.5.dp.toPx())
          )
          // Hub Bolt holes
          for (i in 0 until 5) {
            val angle = (i * 72f) * (Math.PI / 180f)
            val boltX = discCenterX + (height * 0.12f) * kotlin.math.cos(angle).toFloat()
            val boltY = discCenterY + (height * 0.12f) * kotlin.math.sin(angle).toFloat()
            drawCircle(
              color = AluminumGray,
              radius = 3.dp.toPx(),
              center = Offset(boltX, boltY)
            )
          }

          // 2. Brake Pads Outline (#1)
          val padX = width * 0.48f
          val padY = height * 0.30f
          drawRoundRect(
            color = Color(0xFF4A5A6A),
            topLeft = Offset(padX - 25.dp.toPx(), padY - 18.dp.toPx()),
            size = Size(50.dp.toPx(), 36.dp.toPx()),
            cornerRadius = CornerRadius(6.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
          )
          // Friction lining layer
          drawRoundRect(
            color = Color(0xFF6C7D8E),
            topLeft = Offset(padX - 21.dp.toPx(), padY - 14.dp.toPx()),
            size = Size(42.dp.toPx(), 28.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx())
          )

          // 3. Caliper Outline (#3)
          val caliperX = width * 0.75f
          val caliperY = height * 0.35f
          val caliperPath = Path().apply {
            moveTo(caliperX - 30.dp.toPx(), caliperY - 30.dp.toPx())
            lineTo(caliperX + 35.dp.toPx(), caliperY - 20.dp.toPx())
            lineTo(caliperX + 40.dp.toPx(), caliperY + 30.dp.toPx())
            lineTo(caliperX - 20.dp.toPx(), caliperY + 40.dp.toPx())
            close()
          }
          drawPath(
            path = caliperPath,
            color = Color(0xFF5A2A2A),
            style = Stroke(width = 2.5.dp.toPx())
          )

          // 4. Caliper Carrier (#4)
          drawRoundRect(
            color = Color(0xFF33414E),
            topLeft = Offset(width * 0.62f, height * 0.58f),
            size = Size(60.dp.toPx(), 30.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
          )

          // 5. High Pressure Brake Hose (#5)
          val hosePath = Path().apply {
            moveTo(width * 0.82f, height * 0.16f)
            cubicTo(
              width * 0.86f, height * 0.22f,
              width * 0.78f, height * 0.28f,
              width * 0.76f, height * 0.32f
            )
          }
          drawPath(
            path = hosePath,
            color = PerformanceRedGlow,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
          )

          // Exploded Guideline Dotted Vectors connecting components
          val guidelinePath = Path().apply {
            moveTo(discCenterX + height * 0.2f, discCenterY - height * 0.1f)
            lineTo(padX - 25.dp.toPx(), padY)
            moveTo(padX + 25.dp.toPx(), padY)
            lineTo(caliperX - 25.dp.toPx(), caliperY)
          }
          drawPath(
            path = guidelinePath,
            color = Color(0x88FF7A00),
            style = Stroke(
              width = 1.5.dp.toPx(),
              pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
            )
          )
        }

        // Overlay Interactive Hotspot Badges
        schema.hotspots.forEach { hotspot ->
          val isSelected = selectedCalloutNumber == hotspot.calloutNumber

          Box(
            modifier = Modifier
              .align(Alignment.TopStart)
              .offset(
                x = (hotspot.xPercent * 2.8f).dp, // responsive positioning
                y = (hotspot.yPercent * 1.8f).dp
              )
              .clickable { onHotspotClicked(hotspot) }
              .padding(4.dp)
          ) {
            if (isSelected) {
              // Outer Glowing Ring on Selected Callout
              Box(
                modifier = Modifier
                  .size((28 * pulseRadius).dp)
                  .align(Alignment.Center)
                  .clip(CircleShape)
                  .background(PerformanceRed.copy(alpha = 0.35f))
              )
            }

            Surface(
              modifier = Modifier
                .size(26.dp)
                .align(Alignment.Center)
                .shadow(elevation = 6.dp, shape = CircleShape),
              shape = CircleShape,
              color = if (isSelected) PerformanceRed else MechanicalOrangeDark,
              border = BorderStroke(1.5.dp, PureWhite)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = "${hotspot.calloutNumber}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = PureWhite,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                  )
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Bottom Legend showing active selected hotspot label
      val currentHotspot = schema.hotspots.find { it.calloutNumber == selectedCalloutNumber }
      if (currentHotspot != null) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MetalSurface,
          border = BorderStroke(1.dp, PerformanceRed.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = CircleShape,
              color = PerformanceRed,
              modifier = Modifier.size(20.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = "${currentHotspot.calloutNumber}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                )
              }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = currentHotspot.label,
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = OffWhite,
                fontSize = 12.sp
              )
            )
          }
        }
      }
    }
  }
}
