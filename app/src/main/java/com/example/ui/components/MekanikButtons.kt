package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun MekanikPrimaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  enabled: Boolean = true,
  isOrange: Boolean = false,
  testTag: String = "primary_button"
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.96f else 1f,
    animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
    label = "button_scale"
  )

  val gradientColors = if (isOrange) {
    listOf(MechanicalOrange, MechanicalOrangeDark)
  } else {
    listOf(PerformanceRedGlow, PerformanceRedDark)
  }

  Surface(
    modifier = modifier
      .scale(scale)
      .defaultMinSize(minHeight = 48.dp)
      .shadow(elevation = if (isPressed) 2.dp else 6.dp, shape = RoundedCornerShape(12.dp), ambientColor = if (isOrange) MechanicalOrange else PerformanceRed)
      .testTag(testTag),
    shape = RoundedCornerShape(12.dp),
    color = Color.Transparent,
    enabled = enabled,
    onClick = onClick,
    interactionSource = interactionSource
  ) {
    Box(
      modifier = Modifier
        .background(
          brush = Brush.horizontalGradient(gradientColors)
        )
        .padding(horizontal = 20.dp, vertical = 12.dp),
      contentAlignment = Alignment.Center
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        if (icon != null) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PureWhite,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
          text = text,
          style = MaterialTheme.typography.titleMedium.copy(
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
          )
        )
      }
    }
  }
}

@Composable
fun MekanikSecondaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  testTag: String = "secondary_button"
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.97f else 1f,
    label = "sec_button_scale"
  )

  OutlinedButton(
    onClick = onClick,
    modifier = modifier
      .scale(scale)
      .defaultMinSize(minHeight = 48.dp)
      .testTag(testTag),
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.dp, MetalBorder),
    colors = ButtonDefaults.outlinedButtonColors(
      containerColor = MetalCard,
      contentColor = OffWhite
    ),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    interactionSource = interactionSource
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = AluminumLight,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(
          fontWeight = FontWeight.SemiBold,
          color = OffWhite
        )
      )
    }
  }
}
