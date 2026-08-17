package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.NotificationCenterModal
import com.example.ui.components.VinReportModal
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainNavigationTab
import com.example.ui.viewmodel.MekanikUiState
import com.example.ui.viewmodel.MekanikViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MekanikApp()
      }
    }
  }
}

enum class SecondarySubScreen {
  NONE,
  VENDORS_DIRECTORY,
  SUBSCRIPTION_PLANS,
  TECHNICAL_DOCS
}

@Composable
fun MekanikApp(
  mekanikViewModel: MekanikViewModel = viewModel()
) {
  val uiState by mekanikViewModel.uiState.collectAsStateWithLifecycle()
  var currentSubScreen by remember { mutableStateOf(SecondarySubScreen.NONE) }
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(uiState.toastMessage) {
    uiState.toastMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      mekanikViewModel.dismissModals()
    }
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(GraphiteDark),
    containerColor = GraphiteDark,
    contentWindowInsets = WindowInsets.systemBars,
    snackbarHost = {
      SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(bottom = 80.dp)
      ) { data ->
        Snackbar(
          containerColor = MetalCard,
          contentColor = PureWhite,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.padding(16.dp)
        ) {
          Text(
            text = data.visuals.message,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
          )
        }
      }
    },
    bottomBar = {
      if (currentSubScreen == SecondarySubScreen.NONE) {
        MekanikBottomNavigationBar(
          currentTab = uiState.currentNavTab,
          onTabSelected = { tab ->
            mekanikViewModel.setNavigationTab(tab)
          }
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      AnimatedContent(
        targetState = Pair(uiState.currentNavTab, currentSubScreen),
        transitionSpec = {
          fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
        },
        label = "screen_navigation"
      ) { (navTab, subScreen) ->
        when (subScreen) {
          SecondarySubScreen.TECHNICAL_DOCS -> {
            TechnicalDocsScreen(
              uiState = uiState,
              viewModel = mekanikViewModel,
              onBack = { currentSubScreen = SecondarySubScreen.NONE }
            )
          }
          SecondarySubScreen.VENDORS_DIRECTORY -> {
            VendorsScreen(
              uiState = uiState,
              viewModel = mekanikViewModel,
              onBack = { currentSubScreen = SecondarySubScreen.NONE }
            )
          }
          SecondarySubScreen.SUBSCRIPTION_PLANS -> {
            SubscriptionsScreen(
              uiState = uiState,
              viewModel = mekanikViewModel,
              onBack = { currentSubScreen = SecondarySubScreen.NONE }
            )
          }
          SecondarySubScreen.NONE -> {
            when (navTab) {
              MainNavigationTab.ACCUEIL -> {
                HomeScreen(
                  uiState = uiState,
                  viewModel = mekanikViewModel,
                  onNavigateToVendors = { currentSubScreen = SecondarySubScreen.VENDORS_DIRECTORY },
                  onNavigateToSubscriptions = { currentSubScreen = SecondarySubScreen.SUBSCRIPTION_PLANS },
                  onNavigateToDocs = { currentSubScreen = SecondarySubScreen.TECHNICAL_DOCS }
                )
              }
              MainNavigationTab.RECHERCHE -> {
                VinSearchScreen(
                  uiState = uiState,
                  viewModel = mekanikViewModel
                )
              }
              MainNavigationTab.PIECES -> {
                PartsCatalogScreen(
                  uiState = uiState,
                  viewModel = mekanikViewModel,
                  onNavigateToVendors = { currentSubScreen = SecondarySubScreen.VENDORS_DIRECTORY }
                )
              }
              MainNavigationTab.ATELIER -> {
                WorkshopScreen(
                  uiState = uiState,
                  viewModel = mekanikViewModel
                )
              }
              MainNavigationTab.AI_DIAGNOSTIC -> {
                MekanikAiScreen(
                  uiState = uiState,
                  viewModel = mekanikViewModel
                )
              }
            }
          }
        }
      }

      // Printable VIN certified report modal
      if (uiState.showVinReportModal && uiState.activeVinReport != null) {
        VinReportModal(
          report = uiState.activeVinReport!!,
          onDismiss = { mekanikViewModel.dismissModals() }
        )
      }

      // Push Notifications Center Modal
      if (uiState.showNotificationModal) {
        NotificationCenterModal(
          notifications = uiState.notifications,
          onDismiss = { mekanikViewModel.toggleNotificationModal(false) },
          onMarkAsRead = { id -> mekanikViewModel.markNotificationAsRead(id) },
          onMarkAllAsRead = { mekanikViewModel.markAllNotificationsAsRead() },
          onSendSimulatedNotification = { title, msg, cat ->
            mekanikViewModel.sendSimulatedNotification(title, msg, cat)
          }
        )
      }
    }
  }
}

@Composable
private fun MekanikBottomNavigationBar(
  currentTab: MainNavigationTab,
  onTabSelected: (MainNavigationTab) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("bottom_nav_bar")
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .shadow(elevation = 20.dp, ambientColor = PerformanceRed.copy(alpha = 0.25f)),
      color = GraphiteDark,
      border = BorderStroke(1.dp, MetalBorder)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        MekanikNavItem(
          title = "Accueil",
          icon = Icons.Default.Home,
          isSelected = currentTab == MainNavigationTab.ACCUEIL,
          onClick = { onTabSelected(MainNavigationTab.ACCUEIL) },
          testTag = "nav_accueil"
        )
        MekanikNavItem(
          title = "VIN",
          icon = Icons.Default.DirectionsCar,
          isSelected = currentTab == MainNavigationTab.RECHERCHE,
          onClick = { onTabSelected(MainNavigationTab.RECHERCHE) },
          testTag = "nav_recherche"
        )

        // Center Elevated Action Button (VIN / Search trigger)
        Box(
          modifier = Modifier
            .offset(y = (-14).dp)
            .size(54.dp)
            .shadow(12.dp, CircleShape, spotColor = PerformanceRed)
            .clip(CircleShape)
            .background(
              brush = Brush.linearGradient(
                colors = listOf(PerformanceRed, MechanicalOrange)
              )
            )
            .border(3.dp, GraphiteDark, CircleShape)
            .clickable { onTabSelected(MainNavigationTab.RECHERCHE) }
            .testTag("nav_center_fab"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Action VIN",
            tint = PureWhite,
            modifier = Modifier.size(28.dp)
          )
        }

        MekanikNavItem(
          title = "Atelier",
          icon = Icons.Default.Handyman,
          isSelected = currentTab == MainNavigationTab.ATELIER,
          onClick = { onTabSelected(MainNavigationTab.ATELIER) },
          testTag = "nav_atelier"
        )
        MekanikNavItem(
          title = "MekanikAI",
          icon = Icons.Default.Psychology,
          isSelected = currentTab == MainNavigationTab.AI_DIAGNOSTIC,
          onClick = { onTabSelected(MainNavigationTab.AI_DIAGNOSTIC) },
          isAi = true,
          testTag = "nav_ai"
        )
      }
    }
  }
}

@Composable
private fun MekanikNavItem(
  title: String,
  icon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  isAi: Boolean = false,
  testTag: String = "nav_item"
) {
  val activeColor = if (isAi) TechCyan else PerformanceRedGlow
  val itemColor = if (isSelected) activeColor else AluminumMuted

  Column(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp, vertical = 4.dp)
      .testTag(testTag),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(34.dp)
        .clip(CircleShape)
        .background(
          if (isSelected) {
            if (isAi) TechCyan.copy(alpha = 0.2f) else PerformanceRedDark.copy(alpha = 0.35f)
          } else Color.Transparent
        ),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = itemColor,
        modifier = Modifier.size(20.dp)
      )
    }

    Spacer(modifier = Modifier.height(2.dp))

    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
        fontSize = 10.sp,
        color = itemColor
      )
    )
  }
}
