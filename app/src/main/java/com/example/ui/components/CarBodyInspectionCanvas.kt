package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DamageMarker
import com.example.data.model.DamageSeverity
import com.example.data.model.InspectionViewAngle
import com.example.ui.theme.*

@Composable
fun CarBodyInspectionCanvas(
  viewAngle: InspectionViewAngle,
  markers: List<DamageMarker>,
  selectedMarkerId: String?,
  onCanvasTapped: (xRatio: Float, yRatio: Float) -> Unit,
  onMarkerSelected: (DamageMarker) -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "marker_pulse")
  val pulseRadius by infiniteTransition.animateFloat(
    initialValue = 12f,
    targetValue = 24f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulse_radius"
  )
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.8f,
    targetValue = 0.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulse_alpha"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(260.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(GraphiteBlack)
      .border(1.dp, MetalBorder, RoundedCornerShape(12.dp))
  ) {
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(viewAngle, markers) {
          detectTapGestures { tapOffset ->
            val xRatio = (tapOffset.x / size.width).coerceIn(0.05f, 0.95f)
            val yRatio = (tapOffset.y / size.height).coerceIn(0.05f, 0.95f)

            // Check if tapped near an existing marker
            val tappedMarker = markers.firstOrNull { marker ->
              val markerX = marker.xRatio * size.width
              val markerY = marker.yRatio * size.height
              val distance = kotlin.math.hypot(tapOffset.x - markerX, tapOffset.y - markerY)
              distance <= 28f
            }

            if (tappedMarker != null) {
              onMarkerSelected(tappedMarker)
            } else {
              onCanvasTapped(xRatio, yRatio)
            }
          }
        }
    ) {
      drawAutomotiveBackgroundGrid()

      when (viewAngle) {
        InspectionViewAngle.AVANT -> drawFrontViewSilhouette()
        InspectionViewAngle.ARRIERE -> drawRearViewSilhouette()
        InspectionViewAngle.PROFIL_GAUCHE -> drawProfileSilhouette(isLeft = true)
        InspectionViewAngle.PROFIL_DROIT -> drawProfileSilhouette(isLeft = false)
        InspectionViewAngle.TOIT -> drawRoofViewSilhouette()
        InspectionViewAngle.HABITACLE -> drawInteriorDashboardSilhouette()
      }

      // Draw markers for this view angle
      val currentMarkers = markers.filter { it.viewAngle == viewAngle }
      currentMarkers.forEachIndexed { index, marker ->
        val centerX = marker.xRatio * size.width
        val centerY = marker.yRatio * size.height
        val isSelected = marker.id == selectedMarkerId

        val markerColor = when (marker.severity) {
          DamageSeverity.LEGER -> ElectricGreen
          DamageSeverity.MOYEN -> MechanicalOrange
          DamageSeverity.CRITIQUE -> PerformanceRed
        }

        // Animated pulse ring for critical or selected markers
        if (isSelected || marker.severity == DamageSeverity.CRITIQUE) {
          drawCircle(
            color = markerColor.copy(alpha = pulseAlpha),
            radius = pulseRadius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 2.dp.toPx())
          )
        }

        // Base circle
        drawCircle(
          color = markerColor,
          radius = if (isSelected) 14f else 11f,
          center = Offset(centerX, centerY)
        )
        drawCircle(
          color = PureWhite,
          radius = if (isSelected) 6f else 4f,
          center = Offset(centerX, centerY)
        )
      }
    }

    // View Angle watermark badge
    Surface(
      shape = RoundedCornerShape(6.dp),
      color = MetalCard.copy(alpha = 0.85f),
      border = androidx.compose.foundation.BorderStroke(1.dp, MetalBorder),
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(8.dp)
    ) {
      Text(
        text = "VUE : ${viewAngle.label.uppercase()}",
        color = AluminumLight,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
      )
    }

    // Hint to touch
    Surface(
      shape = RoundedCornerShape(6.dp),
      color = GraphiteDark.copy(alpha = 0.85f),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(8.dp)
    ) {
      Text(
        text = "👆 Touchez la carrosserie pour pointer un défaut",
        color = AluminumGray,
        fontSize = 9.5.sp,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
      )
    }
  }
}

private fun DrawScope.drawAutomotiveBackgroundGrid() {
  val step = 20.dp.toPx()
  val gridColor = MetalBorder.copy(alpha = 0.25f)
  var x = 0f
  while (x < size.width) {
    drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 0.5f)
    x += step
  }
  var y = 0f
  while (y < size.height) {
    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 0.5f)
    y += step
  }
}

private fun DrawScope.drawProfileSilhouette(isLeft: Boolean) {
  val strokeColor = AluminumLight.copy(alpha = 0.85f)
  val fillColor = MetalSurface.copy(alpha = 0.6f)
  val w = size.width
  val h = size.height

  val bodyPath = Path().apply {
    moveTo(w * 0.10f, h * 0.65f) // Avant bas
    lineTo(w * 0.08f, h * 0.55f) // Pare-chocs avant
    cubicTo(w * 0.12f, h * 0.42f, w * 0.22f, h * 0.40f, w * 0.30f, h * 0.38f) // Capot
    lineTo(w * 0.42f, h * 0.22f) // Pare-brise
    lineTo(w * 0.70f, h * 0.22f) // Pavillon de toit
    lineTo(w * 0.82f, h * 0.35f) // Lunette arrière
    lineTo(w * 0.92f, h * 0.45f) // Malle / Coffre
    lineTo(w * 0.94f, h * 0.65f) // Pare-chocs arrière
    lineTo(w * 0.84f, h * 0.65f) // Passage de roue AR
    arcTo(
      rect = androidx.compose.ui.geometry.Rect(w * 0.72f, h * 0.52f, w * 0.84f, h * 0.76f),
      startAngleDegrees = 0f,
      sweepAngleDegrees = -180f,
      forceMoveTo = false
    )
    lineTo(w * 0.36f, h * 0.65f) // Bas de caisse central
    arcTo(
      rect = androidx.compose.ui.geometry.Rect(w * 0.20f, h * 0.52f, w * 0.32f, h * 0.76f),
      startAngleDegrees = 0f,
      sweepAngleDegrees = -180f,
      forceMoveTo = false
    )
    close()
  }

  drawPath(bodyPath, fillColor)
  drawPath(bodyPath, strokeColor, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))

  // Roues
  val wheelColor = MetalGray
  val rimColor = AluminumGray
  drawCircle(wheelColor, radius = h * 0.11f, center = Offset(w * 0.26f, h * 0.65f))
  drawCircle(rimColor, radius = h * 0.06f, center = Offset(w * 0.26f, h * 0.65f), style = Stroke(2.dp.toPx()))
  drawCircle(wheelColor, radius = h * 0.11f, center = Offset(w * 0.78f, h * 0.65f))
  drawCircle(rimColor, radius = h * 0.06f, center = Offset(w * 0.78f, h * 0.65f), style = Stroke(2.dp.toPx()))

  // Vitrage latéral
  val glassPath = Path().apply {
    moveTo(w * 0.43f, h * 0.25f)
    lineTo(w * 0.68f, h * 0.25f)
    lineTo(w * 0.79f, h * 0.36f)
    lineTo(w * 0.40f, h * 0.36f)
    close()
  }
  drawPath(glassPath, TechCyan.copy(alpha = 0.25f))
  drawPath(glassPath, TechCyan.copy(alpha = 0.8f), style = Stroke(1.dp.toPx()))

  // Lignes de portières & poignées
  drawLine(AluminumGray.copy(alpha = 0.6f), Offset(w * 0.55f, h * 0.25f), Offset(w * 0.54f, h * 0.64f), strokeWidth = 1.5.dp.toPx())
  drawRoundRect(AluminumLight, topLeft = Offset(w * 0.48f, h * 0.39f), size = Size(14f, 4f), cornerRadius = CornerRadius(2f))
  drawRoundRect(AluminumLight, topLeft = Offset(w * 0.64f, h * 0.39f), size = Size(14f, 4f), cornerRadius = CornerRadius(2f))
}

private fun DrawScope.drawFrontViewSilhouette() {
  val strokeColor = AluminumLight.copy(alpha = 0.85f)
  val fillColor = MetalSurface.copy(alpha = 0.6f)
  val w = size.width
  val h = size.height

  val frontPath = Path().apply {
    moveTo(w * 0.25f, h * 0.80f) // Pneu G
    lineTo(w * 0.18f, h * 0.65f) // Aile bas G
    lineTo(w * 0.20f, h * 0.45f) // Montant Pare-brise G
    lineTo(w * 0.30f, h * 0.25f) // Toit G
    lineTo(w * 0.70f, h * 0.25f) // Toit D
    lineTo(w * 0.80f, h * 0.45f) // Montant Pare-brise D
    lineTo(w * 0.82f, h * 0.65f) // Aile bas D
    lineTo(w * 0.75f, h * 0.80f) // Pneu D
    lineTo(w * 0.70f, h * 0.80f) // Pare-chocs bas
    lineTo(w * 0.30f, h * 0.80f)
    close()
  }

  drawPath(frontPath, fillColor)
  drawPath(frontPath, strokeColor, style = Stroke(width = 2.dp.toPx()))

  // Pare-brise avant
  val windPath = Path().apply {
    moveTo(w * 0.32f, h * 0.28f)
    lineTo(w * 0.68f, h * 0.28f)
    lineTo(w * 0.76f, h * 0.45f)
    lineTo(w * 0.24f, h * 0.45f)
    close()
  }
  drawPath(windPath, TechCyan.copy(alpha = 0.25f))
  drawPath(windPath, TechCyan.copy(alpha = 0.7f), style = Stroke(1.dp.toPx()))

  // Phares Avant
  drawRoundRect(PureWhite.copy(alpha = 0.85f), topLeft = Offset(w * 0.22f, h * 0.50f), size = Size(w * 0.14f, h * 0.08f), cornerRadius = CornerRadius(6f))
  drawRoundRect(PureWhite.copy(alpha = 0.85f), topLeft = Offset(w * 0.64f, h * 0.50f), size = Size(w * 0.14f, h * 0.08f), cornerRadius = CornerRadius(6f))

  // Calandre & Logo
  drawRoundRect(MetalGray, topLeft = Offset(w * 0.38f, h * 0.50f), size = Size(w * 0.24f, h * 0.12f), cornerRadius = CornerRadius(4f), style = Stroke(1.5.dp.toPx()))
  drawCircle(PerformanceRed, radius = 6.dp.toPx(), center = Offset(w * 0.50f, h * 0.56f))

  // Plaque Immatriculation
  drawRoundRect(OffWhite, topLeft = Offset(w * 0.40f, h * 0.68f), size = Size(w * 0.20f, h * 0.06f), cornerRadius = CornerRadius(2f))
  drawRoundRect(GraphiteDark, topLeft = Offset(w * 0.40f, h * 0.68f), size = Size(w * 0.20f, h * 0.06f), cornerRadius = CornerRadius(2f), style = Stroke(1.dp.toPx()))
}

private fun DrawScope.drawRearViewSilhouette() {
  val strokeColor = AluminumLight.copy(alpha = 0.85f)
  val fillColor = MetalSurface.copy(alpha = 0.6f)
  val w = size.width
  val h = size.height

  val rearPath = Path().apply {
    moveTo(w * 0.25f, h * 0.80f)
    lineTo(w * 0.18f, h * 0.65f)
    lineTo(w * 0.22f, h * 0.45f)
    lineTo(w * 0.30f, h * 0.25f)
    lineTo(w * 0.70f, h * 0.25f)
    lineTo(w * 0.78f, h * 0.45f)
    lineTo(w * 0.82f, h * 0.65f)
    lineTo(w * 0.75f, h * 0.80f)
    close()
  }

  drawPath(rearPath, fillColor)
  drawPath(rearPath, strokeColor, style = Stroke(width = 2.dp.toPx()))

  // Lunette Arrière
  val rearGlass = Path().apply {
    moveTo(w * 0.32f, h * 0.28f)
    lineTo(w * 0.68f, h * 0.28f)
    lineTo(w * 0.74f, h * 0.43f)
    lineTo(w * 0.26f, h * 0.43f)
    close()
  }
  drawPath(rearGlass, TechCyan.copy(alpha = 0.25f))
  drawPath(rearGlass, TechCyan.copy(alpha = 0.7f), style = Stroke(1.dp.toPx()))

  // Feux Arrière Rouges
  drawRoundRect(PerformanceRed, topLeft = Offset(w * 0.20f, h * 0.48f), size = Size(w * 0.13f, h * 0.07f), cornerRadius = CornerRadius(4f))
  drawRoundRect(PerformanceRed, topLeft = Offset(w * 0.67f, h * 0.48f), size = Size(w * 0.13f, h * 0.07f), cornerRadius = CornerRadius(4f))

  // Hayon & Plaque
  drawLine(AluminumGray.copy(alpha = 0.5f), Offset(w * 0.30f, h * 0.45f), Offset(w * 0.30f, h * 0.74f), strokeWidth = 1.dp.toPx())
  drawLine(AluminumGray.copy(alpha = 0.5f), Offset(w * 0.70f, h * 0.45f), Offset(w * 0.70f, h * 0.74f), strokeWidth = 1.dp.toPx())
  drawRoundRect(OffWhite, topLeft = Offset(w * 0.40f, h * 0.56f), size = Size(w * 0.20f, h * 0.06f), cornerRadius = CornerRadius(2f))

  // Sorties d'échappement
  drawCircle(MetalGray, radius = 5.dp.toPx(), center = Offset(w * 0.28f, h * 0.78f))
  drawCircle(MetalGray, radius = 5.dp.toPx(), center = Offset(w * 0.72f, h * 0.78f))
}

private fun DrawScope.drawRoofViewSilhouette() {
  val strokeColor = AluminumLight.copy(alpha = 0.85f)
  val fillColor = MetalSurface.copy(alpha = 0.6f)
  val w = size.width
  val h = size.height

  val roofPath = Path().apply {
    moveTo(w * 0.35f, h * 0.10f) // Nez avant
    cubicTo(w * 0.45f, h * 0.08f, w * 0.55f, h * 0.08f, w * 0.65f, h * 0.10f)
    lineTo(w * 0.72f, h * 0.30f) // Aile AVD
    lineTo(w * 0.73f, h * 0.75f) // Flanc D
    cubicTo(w * 0.68f, h * 0.92f, w * 0.32f, h * 0.92f, w * 0.27f, h * 0.75f) // Arrière
    lineTo(w * 0.28f, h * 0.30f) // Flanc G
    close()
  }

  drawPath(roofPath, fillColor)
  drawPath(roofPath, strokeColor, style = Stroke(width = 2.dp.toPx()))

  // Capot avant
  drawLine(MetalBorder, Offset(w * 0.32f, h * 0.30f), Offset(w * 0.68f, h * 0.30f), strokeWidth = 1.dp.toPx())

  // Pare-brise
  val glassRoof = Path().apply {
    moveTo(w * 0.33f, h * 0.31f)
    lineTo(w * 0.67f, h * 0.31f)
    lineTo(w * 0.64f, h * 0.44f)
    lineTo(w * 0.36f, h * 0.44f)
    close()
  }
  drawPath(glassRoof, TechCyan.copy(alpha = 0.3f))

  // Pavillon de toit panoramique
  drawRoundRect(
    color = MetalGray,
    topLeft = Offset(w * 0.37f, h * 0.46f),
    size = Size(w * 0.26f, h * 0.22f),
    cornerRadius = CornerRadius(4f),
    style = Stroke(1.5.dp.toPx())
  )

  // Lunette arrière
  val glassRear = Path().apply {
    moveTo(w * 0.37f, h * 0.70f)
    lineTo(w * 0.63f, h * 0.70f)
    lineTo(w * 0.60f, h * 0.79f)
    lineTo(w * 0.40f, h * 0.79f)
    close()
  }
  drawPath(glassRear, TechCyan.copy(alpha = 0.3f))
}

private fun DrawScope.drawInteriorDashboardSilhouette() {
  val strokeColor = AluminumLight.copy(alpha = 0.85f)
  val fillColor = MetalSurface.copy(alpha = 0.6f)
  val w = size.width
  val h = size.height

  // Contour planche de bord
  drawRoundRect(
    color = fillColor,
    topLeft = Offset(w * 0.15f, h * 0.25f),
    size = Size(w * 0.70f, h * 0.55f),
    cornerRadius = CornerRadius(16f)
  )
  drawRoundRect(
    color = strokeColor,
    topLeft = Offset(w * 0.15f, h * 0.25f),
    size = Size(w * 0.70f, h * 0.55f),
    cornerRadius = CornerRadius(16f),
    style = Stroke(2.dp.toPx())
  )

  // Volant de direction
  drawCircle(MetalGray, radius = h * 0.16f, center = Offset(w * 0.34f, h * 0.52f))
  drawCircle(PureWhite.copy(alpha = 0.8f), radius = h * 0.16f, center = Offset(w * 0.34f, h * 0.52f), style = Stroke(3.dp.toPx()))
  drawCircle(PerformanceRed, radius = h * 0.04f, center = Offset(w * 0.34f, h * 0.52f))

  // Combiné d'instruments / Compteurs
  drawRoundRect(
    color = GraphiteBlack,
    topLeft = Offset(w * 0.25f, h * 0.30f),
    size = Size(w * 0.18f, h * 0.12f),
    cornerRadius = CornerRadius(6f)
  )
  drawCircle(TechCyan, radius = 5.dp.toPx(), center = Offset(w * 0.29f, h * 0.36f))
  drawCircle(MechanicalOrange, radius = 5.dp.toPx(), center = Offset(w * 0.39f, h * 0.36f))

  // Écran tactile multimédia central
  drawRoundRect(
    color = GraphiteBlack,
    topLeft = Offset(w * 0.50f, h * 0.32f),
    size = Size(w * 0.24f, h * 0.22f),
    cornerRadius = CornerRadius(6f)
  )
  drawRoundRect(
    color = TechCyan.copy(alpha = 0.7f),
    topLeft = Offset(w * 0.50f, h * 0.32f),
    size = Size(w * 0.24f, h * 0.22f),
    cornerRadius = CornerRadius(6f),
    style = Stroke(1.5.dp.toPx())
  )

  // Console centrale & levier de vitesses
  drawRoundRect(
    color = MetalCard,
    topLeft = Offset(w * 0.48f, h * 0.58f),
    size = Size(w * 0.16f, h * 0.18f),
    cornerRadius = CornerRadius(6f)
  )
  drawCircle(AluminumLight, radius = 6.dp.toPx(), center = Offset(w * 0.56f, h * 0.65f))
}
