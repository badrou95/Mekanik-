package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * États visuels standardisés des boutons MEKANIK+ :
 * Normal, Appuyé, Désactivé, Chargement, Succès (#22C55E) ou Erreur (#EF4444).
 */
enum class MekanikButtonVisualState {
  NORMAL,
  LOADING,
  SUCCESS,
  ERROR
}

/**
 * Bouton Principal MEKANIK+ :
 * - Orange signature #F07836
 * - Forme arrondie de 13.dp (12 à 14 px)
 * - Texte blanc, semi-gras
 * - Hauteur confortable de 50.dp (48 à 54 px)
 * - Icône discrète si nécessaire
 * - Gestion des états : Normal, Appuyé, Désactivé, Chargement, Succès (#22C55E), Erreur (#EF4444)
 */
@Composable
fun MekanikPrimaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  enabled: Boolean = true,
  isOrange: Boolean = true,
  visualState: MekanikButtonVisualState = MekanikButtonVisualState.NORMAL,
  testTag: String = "primary_button"
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed && enabled) 0.98f else 1f,
    animationSpec = spring(dampingRatio = 0.75f, stiffness = 500f),
    label = "primary_button_scale"
  )

  val containerColor = when {
    !enabled -> MetalGray.copy(alpha = 0.55f)
    visualState == MekanikButtonVisualState.SUCCESS -> MekanikValidationGreen
    visualState == MekanikButtonVisualState.ERROR -> MekanikErrorRed
    isPressed -> Color(0xFFD96526)
    else -> MekanikBrandOrange // #F07836
  }

  val contentColor = if (enabled) Color.White else AluminumGray

  Surface(
    modifier = modifier
      .scale(scale)
      .heightIn(min = 50.dp)
      .testTag(testTag),
    shape = RoundedCornerShape(13.dp),
    color = containerColor,
    shadowElevation = if (enabled && !isPressed) 2.dp else 0.dp,
    enabled = enabled && visualState != MekanikButtonVisualState.LOADING,
    onClick = onClick,
    interactionSource = interactionSource
  ) {
    Box(
      modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
      contentAlignment = Alignment.Center
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        when (visualState) {
          MekanikButtonVisualState.LOADING -> {
            CircularProgressIndicator(
              modifier = Modifier.size(18.dp),
              color = Color.White,
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(10.dp))
          }
          MekanikButtonVisualState.SUCCESS -> {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
          }
          MekanikButtonVisualState.ERROR -> {
            Icon(
              imageVector = Icons.Default.ErrorOutline,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
          }
          MekanikButtonVisualState.NORMAL -> {
            if (icon != null) {
              Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
            }
          }
        }
        Text(
          text = text,
          color = contentColor,
          fontWeight = FontWeight.SemiBold,
          fontSize = 14.sp,
          letterSpacing = 0.2.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

/**
 * Bouton Secondaire MEKANIK+ :
 * - Fond transparent ou gris ardoise foncé (#1C2833)
 * - Bordure fine (#2B3B4B)
 * - Texte clair (#E8EDF2)
 * - Hauteur confortable de 48.dp minimum
 */
@Composable
fun MekanikSecondaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  enabled: Boolean = true,
  testTag: String = "secondary_button"
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed && enabled) 0.98f else 1f,
    label = "sec_button_scale"
  )

  OutlinedButton(
    onClick = onClick,
    enabled = enabled,
    modifier = modifier
      .scale(scale)
      .heightIn(min = 48.dp)
      .testTag(testTag),
    shape = RoundedCornerShape(13.dp),
    border = BorderStroke(1.dp, if (isPressed) MekanikBrandOrange else MetalBorder),
    colors = ButtonDefaults.outlinedButtonColors(
      containerColor = MetalCard,
      contentColor = OffWhite,
      disabledContainerColor = MetalCard.copy(alpha = 0.5f),
      disabledContentColor = AluminumGray
    ),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 11.dp),
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
          tint = if (enabled) AluminumGray else AluminumGray.copy(alpha = 0.5f),
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text,
        color = if (enabled) OffWhite else AluminumGray,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.5.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

/**
 * Bouton Icône MEKANIK+ :
 * - Fond discret (#1C2833)
 * - Icône centrée
 * - Zone tactile confortable (48.dp x 48.dp)
 */
@Composable
fun MekanikIconButton(
  icon: ImageVector,
  contentDescription: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  badgeText: String? = null,
  isHighlighted: Boolean = false,
  tint: Color = OffWhite,
  testTag: String = "icon_button"
) {
  Surface(
    onClick = onClick,
    modifier = modifier
      .size(48.dp)
      .testTag(testTag),
    shape = RoundedCornerShape(12.dp),
    color = if (isHighlighted) MekanikBrandOrange.copy(alpha = 0.14f) else MetalCard,
    border = BorderStroke(
      1.dp,
      if (isHighlighted) MekanikBrandOrange.copy(alpha = 0.6f) else MetalBorder
    )
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = if (isHighlighted) MekanikBrandOrange else tint,
        modifier = Modifier.size(20.dp)
      )
      if (!badgeText.isNullOrBlank()) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MekanikBrandOrange,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = 4.dp, end = 4.dp)
        ) {
          Text(
            text = badgeText,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
          )
        }
      }
    }
  }
}

/**
 * Carte Standardisée MEKANIK+ :
 * - Surface #1C2833
 * - Coins arrondis 14.dp
 * - Bordure fine 1.dp #2B3B4B
 * - Marges régulières 16.dp
 */
@Composable
fun MekanikSurfaceCard(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  isHighlighted: Boolean = false,
  content: @Composable ColumnScope.() -> Unit
) {
  val borderStroke = BorderStroke(
    1.dp,
    if (isHighlighted) MekanikBrandOrange.copy(alpha = 0.65f) else MetalBorder
  )
  if (onClick != null) {
    Surface(
      onClick = onClick,
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      color = MetalCard,
      border = borderStroke
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        content = content
      )
    }
  } else {
    Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      color = MetalCard,
      border = borderStroke
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        content = content
      )
    }
  }
}

/**
 * Section Repliable MEKANIK+ :
 * Permet d'éviter de surcharger l'interface tout en gardant toutes les fonctionnalités accessibles en 1 clic.
 */
@Composable
fun MekanikCollapsibleSection(
  title: String,
  subtitle: String,
  icon: ImageVector,
  initiallyExpanded: Boolean = false,
  badgeText: String? = null,
  modifier: Modifier = Modifier,
  testTag: String = "collapsible_section",
  content: @Composable ColumnScope.() -> Unit
) {
  var expanded by remember { mutableStateOf(initiallyExpanded) }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag(testTag),
    shape = RoundedCornerShape(14.dp),
    color = MetalCard,
    border = BorderStroke(1.dp, MetalBorder)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { expanded = !expanded }
          .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(GraphiteDark)
              .border(1.dp, MetalBorder, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = MekanikBrandOrange,
              modifier = Modifier.size(20.dp)
            )
          }
          Column(modifier = Modifier.weight(1f)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                text = title,
                color = OffWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              if (!badgeText.isNullOrBlank()) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = MekanikBrandOrange.copy(alpha = 0.16f)
                ) {
                  Text(
                    text = badgeText,
                    color = MekanikBrandOrange,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.5.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
            Text(
              text = subtitle,
              color = AluminumGray,
              fontSize = 12.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Icon(
          imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = if (expanded) "Replier" else "Déplier",
          tint = AluminumGray,
          modifier = Modifier.size(22.dp)
        )
      }

      AnimatedVisibility(
        visible = expanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          content = content
        )
      }
    }
  }
}
