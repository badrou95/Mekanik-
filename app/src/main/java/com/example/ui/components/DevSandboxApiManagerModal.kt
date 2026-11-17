package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.*

private enum class DevApiTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
  MODES_SOURCES("Sources API & Sandbox", Icons.Default.Tune),
  CACHE_HTTP("Cache OkHttp & Économies", Icons.Default.Speed),
  SECURITE_KEYS("Sécurité & Clés API", Icons.Default.Key),
  RECHERCHE_PLAQUE("Plaques 🔒 (Certificat requis)", Icons.Default.Lock)
}

@Composable
fun DevSandboxApiManagerModal(
  currentMode: ApiDataSourceMode,
  metrics: ApiTelemetryMetrics,
  endpoints: List<ApiServiceEndpoint>,
  licensePlateMatches: List<LicensePlateMatch>,
  onSelectMode: (ApiDataSourceMode) -> Unit,
  onClearCache: () -> Unit,
  onTestPing: (String) -> Unit,
  onSelectPlateMatch: (LicensePlateMatch) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedTab by remember { mutableStateOf(DevApiTab.MODES_SOURCES) }
  var plateFilterQuery by remember { mutableStateOf("") }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .fillMaxHeight(0.90f)
        .testTag("dev_sandbox_api_modal"),
      shape = RoundedCornerShape(20.dp),
      color = GraphiteDark,
      border = BorderStroke(1.5.dp, TechCyan.copy(alpha = 0.8f)),
      tonalElevation = 12.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Modal Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(TechCyanDark.copy(alpha = 0.4f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.CloudSync,
                contentDescription = null,
                tint = TechCyan,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "CENTRE API & ARCHITECTURE MVVM",
                style = MaterialTheme.typography.titleMedium.copy(
                  color = PureWhite,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.5.sp
                )
              )
              Text(
                text = "Mode Zéro Frais • Sandbox TecAlliance • Cache OkHttp • Données Locales",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = AluminumLight,
                  fontSize = 11.sp
                )
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("btn_close_dev_api_modal")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = AluminumGray)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Navigation Tabs
        LazyRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(DevApiTab.entries) { tab ->
            val isSelected = selectedTab == tab
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) TechCyan else MetalCard,
              border = BorderStroke(1.dp, if (isSelected) TechCyan else MetalBorder),
              modifier = Modifier.clickable { selectedTab = tab }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  tab.icon,
                  contentDescription = null,
                  tint = if (isSelected) GraphiteDark else PureWhite,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = tab.label,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) GraphiteDark else PureWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Content
        when (selectedTab) {
          DevApiTab.MODES_SOURCES -> {
            LazyColumn(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              item {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFF131F28)),
                  border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.5f))
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                      text = "Phase 1 Développement (Zéro frais) : Basculez librement entre l'environnement Sandbox TecAlliance, le catalogue libre Car-API (100+ marques) ou la base embarquée Offline JSON.",
                      style = MaterialTheme.typography.bodySmall.copy(color = PureWhite, fontSize = 11.5.sp)
                    )
                  }
                }
              }

              items(ApiDataSourceMode.entries) { mode ->
                val isSelected = currentMode == mode
                val modeColor = Color(mode.colorHex)

                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectMode(mode) }
                    .testTag("mode_card_${mode.name}"),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) modeColor.copy(alpha = 0.15f) else MetalCard
                  ),
                  border = BorderStroke(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) modeColor else MetalBorder
                  )
                ) {
                  Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = mode.iconEmoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                          Text(
                            text = mode.label,
                            style = MaterialTheme.typography.titleSmall.copy(
                              color = PureWhite,
                              fontWeight = FontWeight.Bold,
                              fontSize = 14.sp
                            )
                          )
                          Text(
                            text = mode.subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                              color = AluminumLight,
                              fontSize = 11.sp
                            )
                          )
                        }
                      }

                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = modeColor.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, modeColor)
                      ) {
                        Text(
                          text = mode.badgeText,
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = modeColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp
                          ),
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                      }
                    }

                    if (isSelected) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ElectricGreen, modifier = Modifier.size(16.dp))
                          Spacer(modifier = Modifier.width(4.dp))
                          Text(
                            text = "SOURCE ACTUELLEMENT UTILISÉE",
                            style = MaterialTheme.typography.labelSmall.copy(
                              color = ElectricGreen,
                              fontWeight = FontWeight.Black,
                              fontSize = 10.sp
                            )
                          )
                        }

                        Text(
                          text = "Latence moyenne: ${metrics.avgResponseLatencyMs} ms",
                          style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 10.5.sp)
                        )
                      }
                    }
                  }
                }
              }

              item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "ENDPOINTS CONFIGURÉS & STATUT CONNECTIVITÉ :",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = AluminumMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                  )
                )
              }

              items(endpoints) { ep ->
                Card(
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.dp, MetalBorder)
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                          modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (ep.isConnected) ElectricGreen else PerformanceRed)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                          text = ep.name,
                          style = MaterialTheme.typography.bodySmall.copy(
                            color = PureWhite,
                            fontWeight = FontWeight.Bold
                          )
                        )
                      }
                      Text(
                        text = "${ep.endpointUrl} • ${ep.latencyMs} ms",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = AluminumLight,
                          fontFamily = FontFamily.Monospace,
                          fontSize = 9.5.sp
                        )
                      )
                      Text(
                        text = "Éléments en cache : ${ep.cachedItemsCount} fiches",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = TechCyan,
                          fontSize = 9.5.sp
                        )
                      )
                    }

                    Button(
                      onClick = { onTestPing(ep.id) },
                      shape = RoundedCornerShape(6.dp),
                      colors = ButtonDefaults.buttonColors(containerColor = GraphiteBlack),
                      border = BorderStroke(1.dp, TechCyan),
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                      modifier = Modifier.testTag("btn_ping_${ep.id}")
                    ) {
                      Text("TEST PING", color = TechCyan, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }
          }

          DevApiTab.CACHE_HTTP -> {
            LazyColumn(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              item {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = MetalCard),
                  border = BorderStroke(1.dp, TechCyan.copy(alpha = 0.6f))
                ) {
                  Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                      text = "ARCHITECTURE DE MISE EN CACHE RETROFIT 2 + OKHTTP",
                      style = MaterialTheme.typography.titleSmall.copy(
                        color = PureWhite,
                        fontWeight = FontWeight.Black
                      )
                    )
                    Text(
                      text = "La bibliothèque standard Retrofit 2 + OkHttp avec Cache Interceptor intercepte toutes les requêtes pièces et marques afin d'éviter de facturer un appel API à chaque clic utilisateur.",
                      style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.5.sp)
                    )

                    // 4 Key Stats
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      MetricMiniBox(
                        title = "TAUX DE CACHE",
                        value = "${metrics.cacheHitRatePercent}%",
                        color = ElectricGreen,
                        modifier = Modifier.weight(1f)
                      )
                      MetricMiniBox(
                        title = "APPELS ÉCONOMISÉS",
                        value = "${metrics.cachedRequests}/${metrics.totalRequests}",
                        color = TechCyan,
                        modifier = Modifier.weight(1f)
                      )
                    }

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      MetricMiniBox(
                        title = "ÉCONOMIE ESTIMÉE",
                        value = "${metrics.estimatedDinarSaved.toInt()} DA",
                        color = MechanicalOrange,
                        modifier = Modifier.weight(1f)
                      )
                      MetricMiniBox(
                        title = "LATENCE MOYENNE",
                        value = "${metrics.avgResponseLatencyMs} ms",
                        color = PureWhite,
                        modifier = Modifier.weight(1f)
                      )
                    }

                    HorizontalDivider(color = MetalBorder, thickness = 0.5.dp)

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text(
                          text = "Durée de rétention max (TTL) : 72 heures",
                          style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontSize = 10.sp)
                        )
                        Text(
                          text = "Mode hors-ligne prioritaire actif",
                          style = MaterialTheme.typography.labelSmall.copy(color = ElectricGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        )
                      }

                      Button(
                        onClick = onClearCache,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PerformanceRed),
                        modifier = Modifier.testTag("btn_clear_http_cache")
                      ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("VIDER LE CACHE", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                      }
                    }
                  }
                }
              }

              item {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(containerColor = GraphiteBlack),
                  border = BorderStroke(1.dp, MetalBorder)
                ) {
                  Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                      text = "ARCHITECTURE REPOSITORY & SÉPARATION DES COUCHES (MVVM)",
                      style = MaterialTheme.typography.labelSmall.copy(color = TechCyan, fontWeight = FontWeight.Bold)
                    )
                    Text(
                      text = "Mekanik+ applique une séparation stricte : les appels API distants passent par MekanikApiGateway et MekanikRepository. Si le fournisseur d'API change ultérieurement (TecAlliance -> Afteriize ou API interne), seuls les adaptateurs sont modifiés, sans toucher aux interfaces Jetpack Compose.",
                      style = MaterialTheme.typography.bodySmall.copy(color = AluminumGray, fontSize = 10.5.sp)
                    )
                  }
                }
              }
            }
          }

          DevApiTab.SECURITE_KEYS -> {
            LazyColumn(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              item {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1A14)),
                  border = BorderStroke(1.dp, MechanicalOrange)
                ) {
                  Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Shield, contentDescription = null, tint = MechanicalOrange, modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "SÉCURITÉ DES CLÉS API & TOKENS SÉCURISÉS",
                        style = MaterialTheme.typography.titleSmall.copy(color = PureWhite, fontWeight = FontWeight.Black)
                      )
                    }
                    Text(
                      text = "Les clés API d'accès aux catalogues ne sont jamais stockées en dur dans le code Kotlin pour éviter toute décompilation du fichier APK. Elles sont injectées dynamiquement via BuildConfig et gérées par le proxy backend sécurisé Mekanik+.",
                      style = MaterialTheme.typography.bodySmall.copy(color = AluminumLight, fontSize = 11.sp)
                    )
                  }
                }
              }

              item {
                Text(
                  text = "CLÉS API CONFIGURÉES DANS LE PROXY :",
                  style = MaterialTheme.typography.labelSmall.copy(color = AluminumMuted, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                )
              }

              item {
                KeyMaskRow(
                  name = "TecAlliance Developer Sandbox Key",
                  maskedValue = "sk-tecdoc-sandbox-9f82••••••••••••2a4e",
                  status = "PROTÉGÉ BUILDCONFIG"
                )
              }

              item {
                KeyMaskRow(
                  name = "RapidAPI Car-API Token",
                  maskedValue = "rap-car-free-3b71••••••••••••8c1d",
                  status = "PROTÉGÉ ENCRYPTION"
                )
              }

              item {
                KeyMaskRow(
                  name = "Google Gemini AI Diagnostic Token",
                  maskedValue = "AIzaSyD-7kLp••••••••••••9mXz",
                  status = "AI STUDIO SECRETS"
                )
              }

              item {
                KeyMaskRow(
                  name = "NHTSA VPIC Public API Endpoint",
                  maskedValue = "https://vpic.nhtsa.dot.gov/api (No auth needed)",
                  status = "PUBLIC FREE TIER"
                )
              }
            }
          }

          DevApiTab.RECHERCHE_PLAQUE -> {
            Column(modifier = Modifier.fillMaxSize()) {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MechanicalOrange.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, MechanicalOrange.copy(alpha = 0.55f))
              ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = MechanicalOrange,
                        modifier = Modifier.size(20.dp)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "RECHERCHE PAR IMMATRICULATION DÉSACTIVÉE",
                        style = MaterialTheme.typography.titleSmall.copy(
                          color = PureWhite,
                          fontWeight = FontWeight.Black,
                          fontSize = 12.5.sp
                        )
                      )
                    }

                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = MechanicalOrange.copy(alpha = 0.22f)
                    ) {
                      Text(
                        text = "EN ATTENTE D'AGRÉMENT",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = MechanicalOrange,
                          fontWeight = FontWeight.Black,
                          fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                      )
                    }
                  }

                  Text(
                    text = "Ce connecteur d'interrogation par plaque d'immatriculation (Fichier National / SIV) est temporairement désactivé le temps d'obtenir le certificat de sécurité et l'autorisation ministérielle officielle.",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = OffWhite,
                      fontSize = 11.5.sp,
                      lineHeight = 16.sp
                    )
                  )

                  OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().testTag("input_filter_plate"),
                    placeholder = {
                      Text(
                        "🔒 Fonctionnalité verrouillée jusqu'à réception du certificat officiel",
                        color = AluminumMuted,
                        fontSize = 11.5.sp
                      )
                    },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AluminumMuted) },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                      disabledContainerColor = GraphiteBlack.copy(alpha = 0.5f),
                      disabledBorderColor = MetalBorder.copy(alpha = 0.5f),
                      disabledTextColor = AluminumMuted,
                      disabledPlaceholderColor = AluminumMuted
                    ),
                    singleLine = true
                  )

                  Text(
                    text = "✅ Alternative active : Utilisez le décodage par numéro de châssis VIN (17 caractères), le code moteur ou le scanner optique de Carte Grise.",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = TechCyan,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
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
}

@Composable
private fun MetricMiniBox(
  title: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    color = GraphiteBlack,
    border = BorderStroke(0.5.dp, MetalBorder)
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
          color = AluminumMuted,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        )
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleSmall.copy(
          color = color,
          fontWeight = FontWeight.Black,
          fontSize = 13.sp
        )
      )
    }
  }
}

@Composable
private fun KeyMaskRow(
  name: String,
  maskedValue: String,
  status: String
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = MetalCard,
    border = BorderStroke(1.dp, MetalBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = name,
          style = MaterialTheme.typography.bodySmall.copy(
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 11.5.sp
          )
        )
        Text(
          text = maskedValue,
          style = MaterialTheme.typography.labelSmall.copy(
            color = AluminumLight,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp
          )
        )
      }

      Surface(
        shape = RoundedCornerShape(4.dp),
        color = GraphiteBlack,
        border = BorderStroke(0.5.dp, TechCyan)
      ) {
        Text(
          text = status,
          style = MaterialTheme.typography.labelSmall.copy(
            color = TechCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 8.5.sp
          ),
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }
    }
  }
}
