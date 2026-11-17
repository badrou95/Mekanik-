package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class StoreSearchTab(val label: String, val iconEmoji: String) {
    BY_CITY("Recherche par ville", "📍"),
    IN_STORE("Recherche dans store", "🏪"),
    BY_PART("Recherche pièce", "⚙️")
}

data class AiGeneratedPartPhotoView(
    val id: String,
    val title: String,
    val angleBadge: String,
    val sourceLabel: String,
    val accentHex: Long,
    val viewType: Int // 0 = 3/4 Studio, 1 = Connectique/Profil, 2 = Gravure Laser OEM, 3 = Schéma Technique
)

data class StoreCatalogPartItem(
    val id: String,
    val partName: String,
    val category: String,
    val extractedOemRef: String,
    val brand: String,
    val storeName: String,
    val city: String,
    val priceDzd: Int,
    val stockQty: Int,
    val condition: String,
    val phoneContact: String,
    val compatibleVehicles: List<String>,
    val compatibleCrossRefs: List<String>,
    val aiGeneratedViews: List<AiGeneratedPartPhotoView>,
    val capturedBitmap: Bitmap? = null,
    val isAiVerified: Boolean = true
)

private val defaultAiViews = listOf(
    AiGeneratedPartPhotoView(
        id = "view_studio",
        title = "Vue Studio HD 3/4",
        angleBadge = "Générée par IA • Angle 45°",
        sourceLabel = "Rendu Catalogue Constructeur",
        accentHex = 0xFF38BDF8,
        viewType = 0
    ),
    AiGeneratedPartPhotoView(
        id = "view_profile",
        title = "Vue Profil & Connecteurs",
        angleBadge = "IA Multi-Angles • Côté Pins",
        sourceLabel = "Inspection Connectique & Joints",
        accentHex = 0xFF10B981,
        viewType = 1
    ),
    AiGeneratedPartPhotoView(
        id = "view_oem",
        title = "Zoom Gravure Réf. OEM",
        angleBadge = "OCR Laser • Haute Précision",
        sourceLabel = "Marquage Usine Vérifié",
        accentHex = 0xFFFF7A00,
        viewType = 2
    ),
    AiGeneratedPartPhotoView(
        id = "view_diagram",
        title = "Schéma Éclaté d'Implantation",
        angleBadge = "Documentation Technique IA",
        sourceLabel = "Couple de serrage & Emplacement",
        accentHex = 0xFFA855F7,
        viewType = 3
    )
)

val initialStoreCatalogParts = mutableStateListOf(
    StoreCatalogPartItem(
        id = "STORE-PART-01",
        partName = "Injecteur Common Rail Piezo 2.0 TDI",
        category = "Injection & Moteur",
        extractedOemRef = "04L 130 277 AC",
        brand = "BOSCH OEM",
        storeName = "AutoPièces Pro Bab Ezzouar",
        city = "Alger",
        priceDzd = 46500,
        stockQty = 8,
        condition = "Neuf d'origine",
        phoneContact = "0550 12 34 56",
        compatibleVehicles = listOf(
            "VW Golf 7 / 8 2.0 TDI (150ch / 184ch) 2013-2023",
            "Audi A3 8V / A4 B9 2.0 TDI CRLB / DEUA",
            "Seat Leon 3 / Ateca 2.0 TDI",
            "Skoda Octavia 3 / Superb 2.0 TDI"
        ),
        compatibleCrossRefs = listOf(
            "BOSCH : 0 445 110 468",
            "BOSCH : 0 445 110 469",
            "VW / AUDI : 04L130277AC / 04L130277",
            "METZGER : 0870094"
        ),
        aiGeneratedViews = defaultAiViews
    ),
    StoreCatalogPartItem(
        id = "STORE-PART-02",
        partName = "Kit Embrayage + Volant Moteur Bi-Masse",
        category = "Transmission & Boîte",
        extractedOemRef = "03L 105 266 DC",
        brand = "LuK / Schaeffler",
        storeName = "Oran Pièces Express",
        city = "Oran",
        priceDzd = 78000,
        stockQty = 4,
        condition = "Neuf Scellé",
        phoneContact = "0552 98 76 54",
        compatibleVehicles = listOf(
            "VW Passat B7 / B8 2.0 TDI",
            "VW Tiguan 5N / Caddy 4 2.0 TDI",
            "Audi Q3 8U 2.0 TDI Quattro",
            "Seat Alhambra / Tarraco 2.0 TDI"
        ),
        compatibleCrossRefs = listOf(
            "LuK : 600 0371 00",
            "SACHS : 2290 601 098",
            "VALEO : 837398",
            "VAG OEM : 03L105266DC"
        ),
        aiGeneratedViews = defaultAiViews
    ),
    StoreCatalogPartItem(
        id = "STORE-PART-03",
        partName = "Pompe à Eau + Boîtier Thermostatique",
        category = "Refroidissement",
        extractedOemRef = "16 381 573 80",
        brand = "VALEO / PSA",
        storeName = "Sétif Pièces Auto Store",
        city = "Sétif",
        priceDzd = 14500,
        stockQty = 15,
        condition = "Neuf Garantie 1 an",
        phoneContact = "0661 44 55 66",
        compatibleVehicles = listOf(
            "Peugeot 208 / 308 / 2008 1.6 BlueHDi & 1.5 BlueHDi",
            "Peugeot Partner Tepee / Rifter",
            "Citroën C3 III / C4 Cactus / Berlingo",
            "Opel Corsa F / Crossland X"
        ),
        compatibleCrossRefs = listOf(
            "PSA : 1638157380 / 9808647080",
            "VALEO : 506991",
            "SKF : VKPC 83645",
            "FEBI BILSTEIN : 106412"
        ),
        aiGeneratedViews = defaultAiViews
    ),
    StoreCatalogPartItem(
        id = "STORE-PART-04",
        partName = "Jeu de 4 Plaquettes de Frein Avant Céramique",
        category = "Freinage Haute Performance",
        extractedOemRef = "5Q0 698 151 BR",
        brand = "BREMBO / TRW",
        storeName = "Constantine Freinage & Moteur",
        city = "Constantine",
        priceDzd = 11200,
        stockQty = 22,
        condition = "Neuf Origine",
        phoneContact = "0770 33 22 11",
        compatibleVehicles = listOf(
            "VW Golf 7 GTI / GTD / R-Line",
            "Audi A3 Sportback / S3 8V",
            "Seat Leon FR / Cupra",
            "Skoda Octavia RS"
        ),
        compatibleCrossRefs = listOf(
            "BREMBO : P 85 126",
            "TRW : GDB1956",
            "ATE : 13.0460-2785.2",
            "FERODO : FDB4433"
        ),
        aiGeneratedViews = defaultAiViews
    ),
    StoreCatalogPartItem(
        id = "STORE-PART-05",
        partName = "Alternateur Piloté Smart Charge 180A",
        category = "Électricité & Charge",
        extractedOemRef = "23100-4EA0A",
        brand = "VALEO / RENAULT-NISSAN",
        storeName = "Blida Electro-Mécanique Store",
        city = "Blida",
        priceDzd = 39000,
        stockQty = 5,
        condition = "Neuf d'origine",
        phoneContact = "0558 11 22 99",
        compatibleVehicles = listOf(
            "Renault Mégane 4 / Kadjar / Clio 5 1.5 dCi",
            "Dacia Duster 2 1.5 dCi 4x4",
            "Nissan Qashqai J11 1.5 / 1.6 dCi",
            "Mercedes Classe A W176 180d (OM607)"
        ),
        compatibleCrossRefs = listOf(
            "RENAULT : 231004EA0A / 231007522R",
            "VALEO : 440615 / FG18T123",
            "BOSCH : 1 986 A00 942"
        ),
        aiGeneratedViews = defaultAiViews
    )
)

private data class AiSamplePartPreset(
    val label: String,
    val partName: String,
    val category: String,
    val extractedOemRef: String,
    val brand: String,
    val suggestedPriceDzd: Int,
    val compatibleVehicles: List<String>,
    val compatibleCrossRefs: List<String>
)

private val aiSamplePresets = listOf(
    AiSamplePartPreset(
        label = "Turbocompresseur Géométrie Variable 1.5 / 2.0 dCi-TDI",
        partName = "Turbocompresseur Garrett VNT + Actuateur Électronique",
        category = "Suralimentation & Moteur",
        extractedOemRef = "04L 253 010 T",
        brand = "GARRETT / BorgWarner OEM",
        suggestedPriceDzd = 94000,
        compatibleVehicles = listOf(
            "VW Golf 7 / Tiguan / Passat 2.0 TDI 150ch (CRLB, DFGA)",
            "Audi A3 8V / A4 B9 / Q3 2.0 TDI 2014-2022",
            "Seat Leon 3 / Ateca 2.0 TDI",
            "Skoda Octavia 3 / Karoq 2.0 TDI"
        ),
        compatibleCrossRefs = listOf(
            "GARRETT : 817047-5009S / 817047-0005",
            "BORGWARNER : 5303 988 0512",
            "VAG OEM : 04L253010T / 04L253010B",
            "MAHLE : 030 TC 11004 000"
        )
    ),
    AiSamplePartPreset(
        label = "Pompe Haute Pression CP4 + Régulateur",
        partName = "Pompe Haute Pression Carburant Bosch CP4",
        category = "Injection Diesel",
        extractedOemRef = "04L 130 755 E",
        brand = "BOSCH Origine",
        suggestedPriceDzd = 68500,
        compatibleVehicles = listOf(
            "VW Golf 7 / Caddy 4 / Transporter T6 2.0 TDI",
            "Audi A3 / A4 / A6 2.0 TDI Ultra",
            "Seat Leon / Alhambra 2.0 TDI",
            "Skoda Yeti / Superb 2.0 TDI"
        ),
        compatibleCrossRefs = listOf(
            "BOSCH : 0 445 010 537 / 0 445 010 538",
            "BOSCH Échange : 0 986 437 440",
            "VAG : 04L130755E / 04L130755D"
        )
    ),
    AiSamplePartPreset(
        label = "Optique Phare Avant Full LED Matrix",
        partName = "Bloc Optique Avant Droit Full LED Adaptatif",
        category = "Éclairage & Carrosserie",
        extractedOemRef = "98 291 452 80",
        brand = "VALEO / PSA",
        suggestedPriceDzd = 82000,
        compatibleVehicles = listOf(
            "Peugeot 208 II (P21) GT / GT-Line 2019-2024",
            "Peugeot 2008 II (P24) Full LED",
            "Peugeot e-208 / e-2008 Électrique"
        ),
        compatibleCrossRefs = listOf(
            "PSA OEM : 9829145280 / 1648291080",
            "VALEO : 047419",
            "HELLA : 1EX 015 112-021"
        )
    )
)

val algerianCities = listOf(
    "Toutes les villes",
    "Alger",
    "Oran",
    "Constantine",
    "Sétif",
    "Blida",
    "Annaba",
    "Tizi Ouzou",
    "Béjaïa",
    "Tlemcen",
    "Batna"
)

/**
 * Espace Store & Recherche Simplifiée (Par Ville / Dans Store / Pièce) + Bouton Ajouter Pièce Photo IA
 */
@Composable
fun SimplifiedStoreAndCitySearchModal(
    initialTab: StoreSearchTab = StoreSearchTab.IN_STORE,
    onOpenVendorAddPartAi: () -> Unit,
    onOpenHandsFreeVoiceSearch: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(initialTab) }
    var selectedCity by remember { mutableStateOf("Toutes les villes") }
    var selectedStoreFilter by remember { mutableStateOf("Tous les stores") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedPartForDetail by remember { mutableStateOf<StoreCatalogPartItem?>(null) }

    val availableStores = remember(initialStoreCatalogParts.size) {
        listOf("Tous les stores") + initialStoreCatalogParts.map { it.storeName }.distinct()
    }

    val filteredParts = remember(selectedTab, selectedCity, selectedStoreFilter, searchQuery, initialStoreCatalogParts.size) {
        initialStoreCatalogParts.filter { item ->
            val matchesCity = selectedCity == "Toutes les villes" || item.city.equals(selectedCity, ignoreCase = true)
            val matchesStore = selectedStoreFilter == "Tous les stores" || item.storeName.equals(selectedStoreFilter, ignoreCase = true)
            val q = searchQuery.trim()
            val matchesQuery = q.isEmpty() ||
                item.partName.contains(q, ignoreCase = true) ||
                item.extractedOemRef.replace(" ", "").contains(q.replace(" ", ""), ignoreCase = true) ||
                item.brand.contains(q, ignoreCase = true) ||
                item.city.contains(q, ignoreCase = true) ||
                item.storeName.contains(q, ignoreCase = true) ||
                item.compatibleVehicles.any { it.contains(q, ignoreCase = true) } ||
                item.compatibleCrossRefs.any { it.replace(" ", "").contains(q.replace(" ", ""), ignoreCase = true) }
            matchesCity && matchesStore && matchesQuery
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            shape = RoundedCornerShape(22.dp),
            color = GraphiteDark,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SteelBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ElectricBlue.copy(alpha = 0.16f))
                                .border(1.dp, ElectricBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(selectedTab.iconEmoji, fontSize = 20.sp)
                        }
                        Column {
                            Text(
                                text = "ESPACE STORE & PIÈCES DE RECHANGE",
                                color = OffWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Recherche par ville, par store ou par référence avec IA",
                                color = AluminumGrey,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = onOpenHandsFreeVoiceSearch,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("store_open_hands_free_voice_btn")
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Vocal", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                        }
                        Button(
                            onClick = onOpenVendorAddPartAi,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MechanicOrange),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("open_add_part_photo_ai_btn")
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Vendeur : + Photo IA", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = OffWhite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3 Mode Tabs (Recherche par ville / Recherche dans store / Recherche pièce)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StoreSearchTab.entries.forEach { tab ->
                        val selected = selectedTab == tab
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedTab = tab },
                            color = if (selected) MechanicRed else GraphiteMetal,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selected) MechanicRed else SteelBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(tab.iconEmoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tab.label,
                                    color = if (selected) Color.White else OffWhite,
                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("store_part_search_input"),
                    placeholder = {
                        Text(
                            when (selectedTab) {
                                StoreSearchTab.BY_CITY -> "Chercher une pièce ou un store dans une ville (ex: Alger, Oran, Pompe à eau...)"
                                StoreSearchTab.IN_STORE -> "Chercher dans les stores partenaires (ex: Injecteur, Embrayage, Bosch...)"
                                StoreSearchTab.BY_PART -> "Entrer une référence OEM, mot-clé ou véhicule (ex: 04L130277AC, Golf 7...)"
                            },
                            color = AluminumGrey,
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = ElectricBlue)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Effacer", tint = AluminumGrey)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = SteelBorder,
                        focusedTextColor = OffWhite,
                        unfocusedTextColor = OffWhite
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Pills by City or Store
                if (selectedTab == StoreSearchTab.BY_CITY || selectedTab == StoreSearchTab.BY_PART) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        algerianCities.forEach { city ->
                            val isSelected = selectedCity == city
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCity = city },
                                label = {
                                    Text(
                                        text = if (city == "Toutes les villes") "📍 Toutes les villes" else "📍 $city",
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricBlue.copy(alpha = 0.22f),
                                    selectedLabelColor = OffWhite,
                                    containerColor = GraphiteMetal,
                                    labelColor = AluminumGrey
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (selectedTab == StoreSearchTab.IN_STORE) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableStores.forEach { store ->
                            val isSelected = selectedStoreFilter == store
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedStoreFilter = store },
                                label = {
                                    Text(
                                        text = "🏪 $store",
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MechanicOrange.copy(alpha = 0.22f),
                                    selectedLabelColor = OffWhite,
                                    containerColor = GraphiteMetal,
                                    labelColor = AluminumGrey
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Results Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredParts.size} pièce(s) disponible(s) en stock immédiat",
                        color = OffWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Références & Compatibilités compilées par IA",
                        color = AiCyan,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Catalog List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredParts, key = { it.id }) { part ->
                        StoreCatalogPartCard(
                            part = part,
                            onInspectPart = { selectedPartForDetail = part },
                            onOrderPart = {
                                Toast.makeText(
                                    context,
                                    "Demande envoyée à ${part.storeName} (${part.city}) • Réf ${part.extractedOemRef}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }
                }
            }
        }
    }

    selectedPartForDetail?.let { part ->
        StorePartAiCompilationDetailDialog(
            part = part,
            onDismiss = { selectedPartForDetail = null }
        )
    }
}

@Composable
private fun StoreCatalogPartCard(
    part: StoreCatalogPartItem,
    onInspectPart: () -> Unit,
    onOrderPart: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onInspectPart() },
        shape = RoundedCornerShape(16.dp),
        color = GraphiteMetal,
        border = androidx.compose.foundation.BorderStroke(1.dp, SteelBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Miniature AI Part Visual
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GraphiteDark)
                            .border(1.dp, ElectricBlue.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (part.capturedBitmap != null) {
                            Image(
                                bitmap = part.capturedBitmap.asImageBitmap(),
                                contentDescription = part.partName,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            AiPartTechnicalCanvasPreview(viewType = 0, modifier = Modifier.fillMaxSize())
                        }
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MechanicRed.copy(alpha = 0.18f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MechanicRed.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "RÉF : ${part.extractedOemRef}",
                                    color = MechanicRed,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                            if (part.isAiVerified) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = AiPurple.copy(alpha = 0.16f)
                                ) {
                                    Text(
                                        text = "🧠 IA +4 Photos",
                                        color = AiCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = part.partName,
                            color = OffWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${part.brand} • 🏪 ${part.storeName} (${part.city})",
                            color = AluminumGrey,
                            fontSize = 11.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${part.priceDzd} DA",
                        color = StatusGreen,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "En stock (${part.stockQty})",
                        color = OffWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Compatible vehicles badges
            Text(
                text = "🚗 Véhicules compatibles :",
                color = OffWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                part.compatibleVehicles.forEach { vehicle ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GraphiteDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SteelBorder)
                    ) {
                        Text(
                            text = vehicle,
                            color = OffWhite,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Compatible cross-references
            Text(
                text = "🔁 Références équivalentes :",
                color = OffWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                part.compatibleCrossRefs.forEach { ref ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MechanicOrange.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MechanicOrange.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = ref,
                            color = MechanicOrange,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onInspectPart,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Voir 4 Photos IA & Détails", color = OffWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = onOrderPart,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusGreen)
                ) {
                    Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Commander (${part.city})", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun StorePartAiCompilationDetailDialog(
    part: StoreCatalogPartItem,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = GraphiteDark,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ElectricBlue)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = part.partName,
                            color = OffWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Réf OEM : ${part.extractedOemRef} • ${part.storeName} (${part.city})",
                            color = MechanicOrange,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = OffWhite)
                    }
                }

                Text(
                    text = "📸 Photos complémentaires générées et recherchées par MekanikAI :",
                    color = AiCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    part.aiGeneratedViews.forEach { view ->
                        Surface(
                            modifier = Modifier.width(155.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = GraphiteMetal,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(view.accentHex).copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(82.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GraphiteDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AiPartTechnicalCanvasPreview(
                                        viewType = view.viewType,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = view.title,
                                    color = OffWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = view.angleBadge,
                                    color = Color(view.accentHex),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "🚗 Voitures compatibles compilées par l'IA :",
                    color = OffWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                part.compatibleVehicles.forEach { v ->
                    Text("• $v", color = AluminumGrey, fontSize = 11.sp)
                }

                Text(
                    text = "🔁 Références équivalentes OEM / Équipementiers :",
                    color = OffWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                part.compatibleCrossRefs.forEach { r ->
                    Text("• $r", color = MechanicOrange, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Text("Fermer la fiche technique IA", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Espace Vendeur : Prendre une photo de la pièce ->
 * L'IA détecte la référence automatiquement, recherche/génère d'autres photos de la pièce,
 * et compile toutes les voitures compatibles et références compatibles.
 */
@Composable
fun VendorAddPartPhotoAiModal(
    onPartPublishedToStore: (StoreCatalogPartItem) -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedPresetIndex by remember { mutableIntStateOf(0) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isAiAnalyzingPhoto by remember { mutableStateOf(false) }
    var aiScanCompleted by remember { mutableStateOf(true) }
    var aiProgressStepText by remember { mutableStateOf("Référence OEM, 4 photos sous tous les angles et compatibilités compilées par IA.") }

    val preset = aiSamplePresets[selectedPresetIndex]
    var extractedRef by remember(selectedPresetIndex) { mutableStateOf(preset.extractedOemRef) }
    var partTitle by remember(selectedPresetIndex) { mutableStateOf(preset.partName) }
    var partBrand by remember(selectedPresetIndex) { mutableStateOf(preset.brand) }
    var partCategory by remember(selectedPresetIndex) { mutableStateOf(preset.category) }
    var priceDzdInput by remember(selectedPresetIndex) { mutableStateOf(preset.suggestedPriceDzd.toString()) }
    var stockQtyInput by remember { mutableStateOf("6") }
    var storeNameInput by remember { mutableStateOf("Mekanik+ Store Pro") }
    var selectedCity by remember { mutableStateOf("Alger") }
    var compatibleVehiclesList by remember(selectedPresetIndex) { mutableStateOf(preset.compatibleVehicles) }
    var compatibleCrossRefsList by remember(selectedPresetIndex) { mutableStateOf(preset.compatibleCrossRefs) }

    fun triggerAiPhotoPipeline(bitmap: Bitmap?, presetIdx: Int) {
        scope.launch {
            capturedBitmap = bitmap
            isAiAnalyzingPhoto = true
            aiScanCompleted = false
            aiProgressStepText = "📸 Lecture OCR de la gravure et de l'étiquette sur la pièce..."
            delay(350)
            val targetPreset = aiSamplePresets[presetIdx]
            extractedRef = targetPreset.extractedOemRef
            aiProgressStepText = "🔍 Référence ${targetPreset.extractedOemRef} détectée ! Recherche et génération IA d'autres photos de la pièce..."
            delay(450)
            aiProgressStepText = "🚗 Compilation IA des voitures compatibles et références croisées OEM/IAM..."
            delay(350)
            partTitle = targetPreset.partName
            partBrand = targetPreset.brand
            partCategory = targetPreset.category
            priceDzdInput = targetPreset.suggestedPriceDzd.toString()
            compatibleVehiclesList = targetPreset.compatibleVehicles
            compatibleCrossRefsList = targetPreset.compatibleCrossRefs
            isAiAnalyzingPhoto = false
            aiScanCompleted = true
            aiProgressStepText = "✅ Compilation IA terminée : Réf ${targetPreset.extractedOemRef} + 4 photos générées + ${targetPreset.compatibleVehicles.size} motorisations compatibles."
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            triggerAiPhotoPipeline(bitmap, selectedPresetIndex)
        } else {
            // Si l'utilisateur annule ou sur émulateur sans flux caméra, simuler la capture avec le preset actif
            triggerAiPhotoPipeline(null, selectedPresetIndex)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            triggerAiPhotoPipeline(null, selectedPresetIndex)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            shape = RoundedCornerShape(22.dp),
            color = GraphiteDark,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MechanicOrange)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MechanicOrange.copy(alpha = 0.18f))
                                .border(1.dp, MechanicOrange, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraEnhance, contentDescription = null, tint = MechanicOrange)
                        }
                        Column {
                            Text(
                                text = "ESPACE VENDEUR : AJOUTER SA PIÈCE PAR PHOTO IA",
                                color = OffWhite,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Prenez une photo → Réf OEM + Autres photos IA + Voitures & Réfs compatibles",
                                color = AluminumGrey,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = OffWhite)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Step 1: Take Photo Button + Sample Presets
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = GraphiteMetal,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SteelBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "1️⃣ PRENDRE UNE PHOTO DE LA PIÈCE",
                                    color = MechanicOrange,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Dès que vous prenez la photo, MekanikAI extrait la référence gravée, génère automatiquement 4 photos studio/techniques de la pièce et compile toutes les voitures et références compatibles.",
                                    color = AluminumGrey,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            val granted = ContextCompat.checkSelfPermission(
                                                context,
                                                Manifest.permission.CAMERA
                                            ) == PackageManager.PERMISSION_GRANTED
                                            if (granted) {
                                                cameraLauncher.launch(null)
                                            } else {
                                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("vendor_take_part_photo_btn"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MechanicRed)
                                    ) {
                                        Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "📸 Prendre la pièce en photo",
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            val nextIdx = (selectedPresetIndex + 1) % aiSamplePresets.size
                                            selectedPresetIndex = nextIdx
                                            triggerAiPhotoPipeline(capturedBitmap, nextIdx)
                                        },
                                        modifier = Modifier.height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AiCyan)
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AiCyan, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Tester autre pièce IA", color = OffWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Status Banner
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isAiAnalyzingPhoto) AiPurple.copy(alpha = 0.16f) else StatusGreen.copy(alpha = 0.14f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isAiAnalyzingPhoto) AiCyan else StatusGreen.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (isAiAnalyzingPhoto) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                strokeWidth = 2.dp,
                                                color = AiCyan
                                            )
                                        } else {
                                            Icon(Icons.Default.Verified, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(18.dp))
                                        }
                                        Text(
                                            text = aiProgressStepText,
                                            color = OffWhite,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Step 2: AI Generated & Searched Photos of the Part
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = GraphiteMetal,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AiCyan.copy(alpha = 0.45f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "2️⃣ PHOTOS RECHERCHÉES & GÉNÉRÉES PAR L'IA",
                                        color = AiCyan,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = AiPurple.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "4 VUES HD AUTO-GÉNÉRÉES",
                                            color = AiCyan,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "À partir de votre photo et de la référence $extractedRef, l'IA a généré et compilé les vues complémentaires de la pièce :",
                                    color = AluminumGrey,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    defaultAiViews.forEach { view ->
                                        Surface(
                                            modifier = Modifier.width(168.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            color = GraphiteDark,
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                Color(view.accentHex).copy(alpha = 0.6f)
                                            )
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(92.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(GraphiteMetal),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (view.viewType == 0 && capturedBitmap != null) {
                                                        Image(
                                                            bitmap = capturedBitmap!!.asImageBitmap(),
                                                            contentDescription = view.title,
                                                            modifier = Modifier.fillMaxSize()
                                                        )
                                                    } else {
                                                        AiPartTechnicalCanvasPreview(
                                                            viewType = view.viewType,
                                                            modifier = Modifier.fillMaxSize()
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = view.title,
                                                    color = OffWhite,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = view.angleBadge,
                                                    color = Color(view.accentHex),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = view.sourceLabel,
                                                    color = AluminumGrey,
                                                    fontSize = 9.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Step 3: Extracted Reference, Compatible Cars & Compatible Cross-References
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = GraphiteMetal,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SteelBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "3️⃣ COMPILATION IA : RÉFÉRENCE, VOITURES & ÉQUIVALENCES",
                                    color = OffWhite,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = extractedRef,
                                        onValueChange = { extractedRef = it },
                                        label = { Text("Référence OEM détectée") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MechanicRed,
                                            unfocusedBorderColor = SteelBorder,
                                            focusedTextColor = OffWhite,
                                            unfocusedTextColor = OffWhite
                                        )
                                    )
                                    OutlinedTextField(
                                        value = partBrand,
                                        onValueChange = { partBrand = it },
                                        label = { Text("Marque / Équipementier") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = ElectricBlue,
                                            unfocusedBorderColor = SteelBorder,
                                            focusedTextColor = OffWhite,
                                            unfocusedTextColor = OffWhite
                                        )
                                    )
                                }

                                OutlinedTextField(
                                    value = partTitle,
                                    onValueChange = { partTitle = it },
                                    label = { Text("Désignation de la pièce") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ElectricBlue,
                                        unfocusedBorderColor = SteelBorder,
                                        focusedTextColor = OffWhite,
                                        unfocusedTextColor = OffWhite
                                    )
                                )

                                // Compatible Cars Box
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = GraphiteDark,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusGreen.copy(alpha = 0.4f))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "🚗 Voitures compatibles compilées automatiquement par l'IA :",
                                            color = StatusGreen,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        compatibleVehiclesList.forEach { car ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.padding(vertical = 2.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = StatusGreen,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(car, color = OffWhite, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }

                                // Compatible Cross References Box
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = GraphiteDark,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MechanicOrange.copy(alpha = 0.45f))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "🔁 Références compatibles (Équivalences Constructeurs & IAM) :",
                                            color = MechanicOrange,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        compatibleCrossRefsList.forEach { crossRef ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.padding(vertical = 2.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.SwapHoriz,
                                                    contentDescription = null,
                                                    tint = MechanicOrange,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    text = crossRef,
                                                    color = OffWhite,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                // Vendor Store Pricing & City
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = priceDzdInput,
                                        onValueChange = { priceDzdInput = it.filter { ch -> ch.isDigit() } },
                                        label = { Text("Prix Store (DA)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        value = stockQtyInput,
                                        onValueChange = { stockQtyInput = it.filter { ch -> ch.isDigit() } },
                                        label = { Text("Quantité Stock") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = storeNameInput,
                                        onValueChange = { storeNameInput = it },
                                        label = { Text("Nom du Store Vendeur") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        value = selectedCity,
                                        onValueChange = { selectedCity = it },
                                        label = { Text("Ville (Wilaya)") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Publish Button
                Button(
                    onClick = {
                        val newItem = StoreCatalogPartItem(
                            id = "STORE-AI-${System.currentTimeMillis()}",
                            partName = partTitle.ifBlank { "Pièce Auto Certifiée IA" },
                            category = partCategory,
                            extractedOemRef = extractedRef.ifBlank { "OEM-AI-2025" },
                            brand = partBrand.ifBlank { "Origine Constructeur" },
                            storeName = storeNameInput.ifBlank { "Mekanik+ Store Pro" },
                            city = selectedCity.ifBlank { "Alger" },
                            priceDzd = priceDzdInput.toIntOrNull() ?: 25000,
                            stockQty = stockQtyInput.toIntOrNull() ?: 5,
                            condition = "Neuf Vérifié par IA",
                            phoneContact = "0550 88 99 00",
                            compatibleVehicles = compatibleVehiclesList,
                            compatibleCrossRefs = compatibleCrossRefsList,
                            aiGeneratedViews = defaultAiViews,
                            capturedBitmap = capturedBitmap,
                            isAiVerified = true
                        )
                        initialStoreCatalogParts.add(0, newItem)
                        onPartPublishedToStore(newItem)
                        Toast.makeText(
                            context,
                            "Pièce publiée dans le Store avec ses 4 photos IA et compatibilités !",
                            Toast.LENGTH_LONG
                        ).show()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("publish_ai_part_to_store_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusGreen)
                ) {
                    Icon(Icons.Default.Publish, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PUBLIER LA PIÈCE + PHOTOS IA DANS LE STORE",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AiPartTechnicalCanvasPreview(
    viewType: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.padding(8.dp)) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2f, h / 2f)

        // Subtle technical grid
        val step = 16.dp.toPx()
        var x = 0f
        while (x <= w) {
            drawLine(
                color = Color(0xFF38BDF8).copy(alpha = 0.08f),
                start = Offset(x, 0f),
                end = Offset(x, h),
                strokeWidth = 1f
            )
            x += step
        }

        when (viewType) {
            0 -> {
                // 3/4 Studio mechanical part silhouette
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.35f), Color.Transparent)
                    ),
                    radius = minOf(w, h) * 0.45f,
                    center = center
                )
                drawCircle(
                    color = Color(0xFF94A3B8),
                    radius = minOf(w, h) * 0.32f,
                    center = center,
                    style = Stroke(width = 6f)
                )
                drawCircle(
                    color = Color(0xFF38BDF8),
                    radius = minOf(w, h) * 0.16f,
                    center = center,
                    style = Stroke(width = 4f)
                )
            }
            1 -> {
                // Connector & Profile schematic
                drawRoundRect(
                    color = Color(0xFF10B981).copy(alpha = 0.25f),
                    topLeft = Offset(w * 0.18f, h * 0.25f),
                    size = Size(w * 0.64f, h * 0.5f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                drawRoundRect(
                    color = Color(0xFF10B981),
                    topLeft = Offset(w * 0.18f, h * 0.25f),
                    size = Size(w * 0.64f, h * 0.5f),
                    cornerRadius = CornerRadius(10f, 10f),
                    style = Stroke(width = 3f)
                )
                for (i in 0..3) {
                    val px = w * (0.28f + i * 0.14f)
                    drawCircle(
                        color = Color(0xFFF59E0B),
                        radius = 4f,
                        center = Offset(px, h * 0.5f)
                    )
                }
            }
            2 -> {
                // OEM Laser Engraving Zoom frame
                drawRoundRect(
                    color = Color(0xFFFF7A00).copy(alpha = 0.2f),
                    topLeft = Offset(w * 0.12f, h * 0.28f),
                    size = Size(w * 0.76f, h * 0.44f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                drawRoundRect(
                    color = Color(0xFFFF7A00),
                    topLeft = Offset(w * 0.12f, h * 0.28f),
                    size = Size(w * 0.76f, h * 0.44f),
                    cornerRadius = CornerRadius(8f, 8f),
                    style = Stroke(width = 2.5f)
                )
                drawLine(
                    color = Color(0xFFFF7A00),
                    start = Offset(w * 0.22f, h * 0.5f),
                    end = Offset(w * 0.78f, h * 0.5f),
                    strokeWidth = 3f
                )
            }
            else -> {
                // Exploded mounting diagram
                drawCircle(
                    color = Color(0xFFA855F7),
                    radius = minOf(w, h) * 0.18f,
                    center = Offset(w * 0.32f, h * 0.5f),
                    style = Stroke(width = 3f)
                )
                drawCircle(
                    color = Color(0xFF38BDF8),
                    radius = minOf(w, h) * 0.22f,
                    center = Offset(w * 0.68f, h * 0.5f),
                    style = Stroke(width = 3f)
                )
                drawLine(
                    color = Color(0xFFE2E8F0),
                    start = Offset(w * 0.32f, h * 0.5f),
                    end = Offset(w * 0.68f, h * 0.5f),
                    strokeWidth = 2f
                )
            }
        }
    }
}
