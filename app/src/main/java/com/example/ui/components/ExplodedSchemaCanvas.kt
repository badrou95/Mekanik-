package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExplodedHotspot
import com.example.data.model.ExplodedSchema
import com.example.data.model.SystemCategory
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ExplodedSchemaCanvas(
  schema: ExplodedSchema,
  selectedCalloutNumber: Int?,
  onHotspotClicked: (ExplodedHotspot) -> Unit,
  modifier: Modifier = Modifier
) {
  var isZoomedIn by remember { mutableStateOf(false) }

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
    border = BorderStroke(1.5.dp, TechCyan.copy(alpha = 0.65f))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Header info bar (ETKA / Partslink24 style)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.AccountTree,
              contentDescription = null,
              tint = TechCyan,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = schema.title.uppercase(),
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = OffWhite,
                fontSize = 12.5.sp
              )
            )
          }
          Text(
            text = "${schema.subTitle} • Cliquez sur un repère [1..${schema.hotspots.size}]",
            style = MaterialTheme.typography.bodySmall.copy(
              color = AluminumGray,
              fontSize = 10.5.sp
            )
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isZoomedIn) TechCyanDark else MetalSurface,
            border = BorderStroke(1.dp, if (isZoomedIn) TechCyan else MetalBorder),
            modifier = Modifier
              .clickable { isZoomedIn = !isZoomedIn }
              .testTag("btn_toggle_schema_zoom")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                if (isZoomedIn) Icons.Default.ZoomOut else Icons.Default.ZoomIn,
                contentDescription = "Zoom",
                tint = if (isZoomedIn) PureWhite else TechCyan,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isZoomedIn) "1.35x" else "1.0x",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = PureWhite,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MetalSurface,
            border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.6f))
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
                text = "ETKA / 24",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = MechanicalOrange,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black
                )
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Technical Blueprint Container
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(if (isZoomedIn) 280.dp else 235.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFF0B1117))
          .border(1.dp, Color(0xFF2A3644), RoundedCornerShape(12.dp))
      ) {
        // Blueprint Technical Grid & Mechanical Schematics Drawing
        Canvas(modifier = Modifier.fillMaxSize()) {
          val width = size.width
          val height = size.height

          // Blueprint CAD Grid Lines
          val gridSpacing = 22.dp.toPx()
          var x = 0f
          while (x < width) {
            drawLine(
              color = Color(0x1A00E5FF),
              start = Offset(x, 0f),
              end = Offset(x, height),
              strokeWidth = 1f
            )
            x += gridSpacing
          }
          var y = 0f
          while (y < height) {
            drawLine(
              color = Color(0x1A00E5FF),
              start = Offset(0f, y),
              end = Offset(width, y),
              strokeWidth = 1f
            )
            y += gridSpacing
          }

          // System-specific CAD Exploded Mechanical Vector Drawing
          when (schema.system) {
            SystemCategory.FREINAGE -> drawBrakeExplodedSchematic(width, height)
            SystemCategory.MOTEUR -> drawEngineTurboExplodedSchematic(width, height)
            SystemCategory.DISTRIBUTION -> drawTimingChainBeltSchematic(width, height)
            SystemCategory.FILTRATION -> drawFiltrationHousingSchematic(width, height)
            SystemCategory.EMBRAYAGE -> drawDualMassClutchGearboxSchematic(width, height)
            SystemCategory.SUSPENSION -> drawMacPhersonSuspensionSchematic(width, height)
            SystemCategory.REFROIDISSEMENT -> drawCoolingSystemSchematic(width, height)
            SystemCategory.ELECTRICITE -> drawElectricalAlternatorSchematic(width, height)
            SystemCategory.VEHICULE_ELECTRIQUE -> drawEvThermalLoopSchematic(width, height)
            SystemCategory.CARROSSERIE -> drawMatrixLedBodySchematic(width, height)
          }
        }

        // Overlay Interactive Hotspot Badges [1..N]
        schema.hotspots.forEach { hotspot ->
          val isSelected = selectedCalloutNumber == hotspot.calloutNumber

          Box(
            modifier = Modifier
              .align(Alignment.TopStart)
              .offset(
                x = (hotspot.xPercent * 2.85f).dp,
                y = (hotspot.yPercent * (if (isZoomedIn) 2.2f else 1.85f)).dp
              )
              .clickable { onHotspotClicked(hotspot) }
              .padding(4.dp)
              .testTag("schema_hotspot_${hotspot.calloutNumber}")
          ) {
            if (isSelected) {
              Box(
                modifier = Modifier
                  .size((30 * pulseRadius).dp)
                  .align(Alignment.Center)
                  .clip(CircleShape)
                  .background(PerformanceRed.copy(alpha = 0.38f))
              )
            }

            Surface(
              modifier = Modifier
                .size(28.dp)
                .align(Alignment.Center)
                .shadow(elevation = 6.dp, shape = CircleShape),
              shape = CircleShape,
              color = if (isSelected) PerformanceRed else MechanicalOrangeDark,
              border = BorderStroke(1.5.dp, if (isSelected) PureWhite else TechCyanGlow)
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

      Spacer(modifier = Modifier.height(10.dp))

      // Quick-Callout Strip (Repères ETKA / Partslink24 [1..N])
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        schema.hotspots.forEach { hotspot ->
          val isSelected = hotspot.calloutNumber == selectedCalloutNumber
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) PerformanceRedDark else MetalSurface,
            border = BorderStroke(1.dp, if (isSelected) PerformanceRed else MetalBorder),
            modifier = Modifier
              .clickable { onHotspotClicked(hotspot) }
              .testTag("schema_callout_chip_${hotspot.calloutNumber}")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = CircleShape,
                color = if (isSelected) PerformanceRed else GraphiteBlack,
                modifier = Modifier.size(18.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = "${hotspot.calloutNumber}",
                    color = PureWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                  )
                }
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = hotspot.label.take(26) + if (hotspot.label.length > 26) "…" else "",
                color = if (isSelected) PureWhite else AluminumLight,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 10.5.sp
              )
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
          border = BorderStroke(1.dp, PerformanceRed.copy(alpha = 0.65f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Surface(
                shape = CircleShape,
                color = PerformanceRed,
                modifier = Modifier.size(22.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = "${currentHotspot.calloutNumber}",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = PureWhite,
                      fontWeight = FontWeight.Black,
                      fontSize = 11.sp
                    )
                  )
                }
              }
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "REPÈRE SÉLECTIONNÉ SUR PLANCHE ÉCLATÉE",
                  color = MechanicalOrange,
                  fontWeight = FontWeight.Black,
                  fontSize = 9.sp
                )
                Text(
                  text = currentHotspot.label,
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PureWhite,
                    fontSize = 12.sp
                  )
                )
              }
            }
          }
        }
      }
    }
  }
}

private fun DrawScope.drawBrakeExplodedSchematic(width: Float, height: Float) {
  val discCenterX = width * 0.28f
  val discCenterY = height * 0.52f
  drawCircle(
    color = Color(0xFF42627B),
    radius = height * 0.37f,
    center = Offset(discCenterX, discCenterY),
    style = Stroke(width = 2.5.dp.toPx())
  )
  drawCircle(
    color = Color(0xFF2B4356),
    radius = height * 0.18f,
    center = Offset(discCenterX, discCenterY),
    style = Stroke(width = 1.8.dp.toPx())
  )
  for (i in 0 until 5) {
    val angle = (i * 72f) * (Math.PI / 180f)
    val boltX = discCenterX + (height * 0.12f) * cos(angle).toFloat()
    val boltY = discCenterY + (height * 0.12f) * sin(angle).toFloat()
    drawCircle(color = AluminumLight, radius = 3.dp.toPx(), center = Offset(boltX, boltY))
  }
  val padX = width * 0.48f
  val padY = height * 0.32f
  drawRoundRect(
    color = Color(0xFF56738C),
    topLeft = Offset(padX - 26.dp.toPx(), padY - 18.dp.toPx()),
    size = Size(52.dp.toPx(), 36.dp.toPx()),
    cornerRadius = CornerRadius(6.dp.toPx()),
    style = Stroke(width = 2.dp.toPx())
  )
  val caliperX = width * 0.75f
  val caliperY = height * 0.35f
  val caliperPath = Path().apply {
    moveTo(caliperX - 30.dp.toPx(), caliperY - 30.dp.toPx())
    lineTo(caliperX + 35.dp.toPx(), caliperY - 20.dp.toPx())
    lineTo(caliperX + 40.dp.toPx(), caliperY + 30.dp.toPx())
    lineTo(caliperX - 20.dp.toPx(), caliperY + 40.dp.toPx())
    close()
  }
  drawPath(path = caliperPath, color = PerformanceRedGlow, style = Stroke(width = 2.5.dp.toPx()))
  drawRoundRect(
    color = Color(0xFF3E586E),
    topLeft = Offset(width * 0.62f, height * 0.58f),
    size = Size(60.dp.toPx(), 30.dp.toPx()),
    cornerRadius = CornerRadius(4.dp.toPx()),
    style = Stroke(width = 2.dp.toPx())
  )
  drawExplodedAxisLine(Offset(discCenterX, discCenterY), Offset(caliperX, caliperY))
}

private fun DrawScope.drawEngineTurboExplodedSchematic(width: Float, height: Float) {
  val turboCenter = Offset(width * 0.36f, height * 0.46f)
  drawCircle(color = MechanicalOrange, radius = height * 0.25f, center = turboCenter, style = Stroke(width = 2.5.dp.toPx()))
  drawCircle(color = TechCyan, radius = height * 0.12f, center = turboCenter, style = Stroke(width = 2.dp.toPx()))
  drawRoundRect(
    color = Color(0xFF56738C),
    topLeft = Offset(width * 0.60f, height * 0.20f),
    size = Size(54.dp.toPx(), 34.dp.toPx()),
    cornerRadius = CornerRadius(6.dp.toPx()),
    style = Stroke(width = 2.dp.toPx())
  )
  val hosePath = Path().apply {
    moveTo(turboCenter.x + 28.dp.toPx(), turboCenter.y)
    cubicTo(width * 0.55f, height * 0.48f, width * 0.66f, height * 0.64f, width * 0.84f, height * 0.60f)
  }
  drawPath(path = hosePath, color = TechCyanGlow, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
  drawExplodedAxisLine(turboCenter, Offset(width * 0.65f, height * 0.28f))
}

private fun DrawScope.drawTimingChainBeltSchematic(width: Float, height: Float) {
  val camLeft = Offset(width * 0.38f, height * 0.30f)
  val camRight = Offset(width * 0.56f, height * 0.30f)
  val crank = Offset(width * 0.47f, height * 0.74f)
  drawCircle(color = MechanicalOrange, radius = 22.dp.toPx(), center = camLeft, style = Stroke(width = 2.5.dp.toPx()))
  drawCircle(color = MechanicalOrange, radius = 22.dp.toPx(), center = camRight, style = Stroke(width = 2.5.dp.toPx()))
  drawCircle(color = TechCyan, radius = 28.dp.toPx(), center = crank, style = Stroke(width = 2.5.dp.toPx()))
  val beltPath = Path().apply {
    moveTo(camLeft.x, camLeft.y - 22.dp.toPx())
    lineTo(camRight.x, camRight.y - 22.dp.toPx())
    lineTo(crank.x + 28.dp.toPx(), crank.y)
    lineTo(crank.x - 28.dp.toPx(), crank.y)
    close()
  }
  drawPath(
    path = beltPath,
    color = PerformanceRedGlow,
    style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f)))
  )
}

private fun DrawScope.drawFiltrationHousingSchematic(width: Float, height: Float) {
  drawRoundRect(
    color = TechCyan,
    topLeft = Offset(width * 0.20f, height * 0.28f),
    size = Size(52.dp.toPx(), 82.dp.toPx()),
    cornerRadius = CornerRadius(8.dp.toPx()),
    style = Stroke(width = 2.2.dp.toPx())
  )
  drawRoundRect(
    color = MechanicalOrange,
    topLeft = Offset(width * 0.58f, height * 0.20f),
    size = Size(86.dp.toPx(), 48.dp.toPx()),
    cornerRadius = CornerRadius(6.dp.toPx()),
    style = Stroke(width = 2.2.dp.toPx())
  )
  drawRoundRect(
    color = Color(0xFF6B8499),
    topLeft = Offset(width * 0.44f, height * 0.58f),
    size = Size(58.dp.toPx(), 54.dp.toPx()),
    cornerRadius = CornerRadius(8.dp.toPx()),
    style = Stroke(width = 2.dp.toPx())
  )
  drawExplodedAxisLine(Offset(width * 0.28f, height * 0.45f), Offset(width * 0.65f, height * 0.35f))
}

private fun DrawScope.drawDualMassClutchGearboxSchematic(width: Float, height: Float) {
  val dmfCenter = Offset(width * 0.34f, height * 0.50f)
  drawCircle(color = MechanicalOrange, radius = height * 0.34f, center = dmfCenter, style = Stroke(width = 3.dp.toPx()))
  drawCircle(color = TechCyan, radius = height * 0.22f, center = dmfCenter, style = Stroke(width = 2.dp.toPx()))
  val cscCenter = Offset(width * 0.68f, height * 0.44f)
  drawCircle(color = PerformanceRedGlow, radius = height * 0.16f, center = cscCenter, style = Stroke(width = 2.5.dp.toPx()))
  drawExplodedAxisLine(dmfCenter, cscCenter)
}

private fun DrawScope.drawMacPhersonSuspensionSchematic(width: Float, height: Float) {
  val strutTop = Offset(width * 0.48f, height * 0.16f)
  val strutBottom = Offset(width * 0.42f, height * 0.72f)
  drawLine(color = TechCyan, start = strutTop, end = strutBottom, strokeWidth = 5.dp.toPx())
  val armLeft = Offset(width * 0.24f, height * 0.74f)
  val armRight = Offset(width * 0.56f, height * 0.76f)
  drawLine(color = MechanicalOrange, start = armLeft, end = armRight, strokeWidth = 4.dp.toPx())
  drawLine(
    color = PerformanceRedGlow,
    start = Offset(width * 0.72f, height * 0.24f),
    end = Offset(width * 0.72f, height * 0.68f),
    strokeWidth = 3.dp.toPx()
  )
}

private fun DrawScope.drawCoolingSystemSchematic(width: Float, height: Float) {
  val pumpCenter = Offset(width * 0.36f, height * 0.50f)
  drawCircle(color = TechCyan, radius = height * 0.24f, center = pumpCenter, style = Stroke(width = 2.5.dp.toPx()))
  drawRoundRect(
    color = MechanicalOrange,
    topLeft = Offset(width * 0.62f, height * 0.22f),
    size = Size(88.dp.toPx(), 96.dp.toPx()),
    cornerRadius = CornerRadius(6.dp.toPx()),
    style = Stroke(width = 2.5.dp.toPx())
  )
  drawExplodedAxisLine(pumpCenter, Offset(width * 0.68f, height * 0.50f))
}

private fun DrawScope.drawElectricalAlternatorSchematic(width: Float, height: Float) {
  val altCenter = Offset(width * 0.36f, height * 0.46f)
  drawCircle(color = ElectricGreen, radius = height * 0.26f, center = altCenter, style = Stroke(width = 2.5.dp.toPx()))
  drawRoundRect(
    color = TechCyan,
    topLeft = Offset(width * 0.66f, height * 0.48f),
    size = Size(54.dp.toPx(), 38.dp.toPx()),
    cornerRadius = CornerRadius(6.dp.toPx()),
    style = Stroke(width = 2.dp.toPx())
  )
  drawExplodedAxisLine(altCenter, Offset(width * 0.70f, height * 0.54f))
}

private fun DrawScope.drawEvThermalLoopSchematic(width: Float, height: Float) {
  drawRoundRect(
    color = TechCyanGlow,
    topLeft = Offset(width * 0.22f, height * 0.26f),
    size = Size(width * 0.56f, height * 0.48f),
    cornerRadius = CornerRadius(10.dp.toPx()),
    style = Stroke(width = 2.5.dp.toPx())
  )
  drawCircle(
    color = ElectricGreen,
    radius = 24.dp.toPx(),
    center = Offset(width * 0.50f, height * 0.50f),
    style = Stroke(width = 2.5.dp.toPx())
  )
}

private fun DrawScope.drawMatrixLedBodySchematic(width: Float, height: Float) {
  val headlampPath = Path().apply {
    moveTo(width * 0.22f, height * 0.42f)
    lineTo(width * 0.68f, height * 0.28f)
    lineTo(width * 0.76f, height * 0.56f)
    lineTo(width * 0.28f, height * 0.64f)
    close()
  }
  drawPath(path = headlampPath, color = TechCyanGlow, style = Stroke(width = 2.5.dp.toPx()))
}

private fun DrawScope.drawExplodedAxisLine(start: Offset, end: Offset) {
  drawLine(
    color = Color(0x99FF7A00),
    start = start,
    end = end,
    strokeWidth = 1.6.dp.toPx(),
    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
  )
}
