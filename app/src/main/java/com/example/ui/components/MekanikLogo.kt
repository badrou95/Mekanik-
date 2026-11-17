package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MekanikOfficialLogoBadge(
  modifier: Modifier = Modifier,
  size: androidx.compose.ui.unit.Dp = 44.dp
) {
  Box(
    modifier = modifier
      .size(size)
      .clip(RoundedCornerShape(12.dp))
      .border(1.dp, MetalBorder, RoundedCornerShape(12.dp))
      .background(MetalCard),
    contentAlignment = Alignment.Center
  ) {
    Image(
      painter = painterResource(id = R.drawable.ic_mekanik_logo),
      contentDescription = "Logo Officiel Mekanik+",
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.Crop
    )
  }
}

@Composable
fun MekanikLogo(
  modifier: Modifier = Modifier,
  showTagline: Boolean = false,
  isSmall: Boolean = false,
  animateOnEntry: Boolean = true
) {
  val emblemAlpha = remember { Animatable(if (animateOnEntry) 0f else 1f) }
  val emblemScale = remember { Animatable(if (animateOnEntry) 0.9f else 1f) }
  val textAlpha = remember { Animatable(if (animateOnEntry) 0f else 1f) }

  LaunchedEffect(animateOnEntry) {
    if (animateOnEntry) {
      launch {
        emblemAlpha.animateTo(1f, animationSpec = tween(250, easing = FastOutSlowInEasing))
      }
      launch {
        emblemScale.animateTo(1f, animationSpec = tween(250, easing = FastOutSlowInEasing))
      }
      launch {
        delay(60)
        textAlpha.animateTo(1f, animationSpec = tween(250, easing = FastOutSlowInEasing))
      }
    }
  }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
  ) {
    Box(
      modifier = Modifier
        .size(if (isSmall) 40.dp else 48.dp)
        .graphicsLayer {
          alpha = emblemAlpha.value
          scaleX = emblemScale.value
          scaleY = emblemScale.value
        }
        .clip(RoundedCornerShape(12.dp))
        .border(1.dp, MetalBorder, RoundedCornerShape(12.dp))
        .background(MetalCard),
      contentAlignment = Alignment.Center
    ) {
      Image(
        painter = painterResource(id = R.drawable.ic_mekanik_logo),
        contentDescription = "Logo Officiel Mekanik+",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column(
      modifier = Modifier.graphicsLayer {
        alpha = textAlpha.value
      }
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "MEKANIK",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = if (isSmall) 17.sp else 20.sp,
            letterSpacing = 0.8.sp,
            color = OffWhite
          )
        )
        Text(
          text = "+",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.ExtraBold,
            fontSize = if (isSmall) 19.sp else 22.sp,
            color = MekanikBrandOrange
          )
        )
      }

      if (showTagline) {
        Text(
          text = "AUTOMOTIVE INTELLIGENCE PLATFORM",
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp,
            color = AluminumGray
          )
        )
      }
    }
  }
}
