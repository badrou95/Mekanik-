package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountRole
import com.example.data.model.NotificationCategory
import com.example.ui.theme.*

/**
 * Barre supérieure professionnelle MEKANIK+ — AUTOMOTIVE INTELLIGENCE PLATFORM.
 * Design épuré (#1C2833 sur fond #101820, accent signature #F07836).
 */
@Composable
fun MekanikTopThreeBarsHeader(
    currentRole: AccountRole,
    onOpenDrawer: () -> Unit,
    onOpenStoreSearch: (StoreSearchTab) -> Unit,
    onOpenVendorPhotoAi: () -> Unit,
    onOpenAutoForum: () -> Unit,
    onOpenProSocialNetwork: () -> Unit,
    onOpenMonthlyZipArchive: () -> Unit,
    onOpenHandsFreeVoiceSearch: () -> Unit = {},
    onOpenPartLabelVisionScanner: () -> Unit = {},
    onOpenMecaVerifHistory: () -> Unit = {},
    onOpenMekanikAiObd: (String?) -> Unit = {},
    unreadNotificationsCount: Int = 0,
    isRealtimeStreamEnabled: Boolean = true,
    onOpenNotificationsCenter: () -> Unit = {},
    onStartOnboardingGuide: () -> Unit = {},
    onOpenSaasWebPlatform: () -> Unit = {}
) {
    val context = LocalContext.current
    val isLightNormal = MekanikThemeController.isLightModeActive

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("top_three_bars_header"),
        color = MetalCard,
        border = BorderStroke(1.dp, MetalBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: 3-Bar Menu Button (48.dp touch target) + Brand Identity DMS MÉCANICAUTO+
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MekanikBrandOrange,
                    onClick = onOpenDrawer,
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("btn_open_three_bars_menu")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu Principal",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "DMS MÉCANICAUTO",
                            color = OffWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            letterSpacing = 0.4.sp
                        )
                        Text(
                            text = "+",
                            color = MekanikBrandOrange,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GraphiteDark,
                            border = BorderStroke(1.dp, MetalBorder)
                        ) {
                            Text(
                                text = currentRole.shortTitle,
                                color = AluminumGray,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "SYSTÈME DE GESTION AUTOMOBILE & ATELIER",
                        color = AluminumGray,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Right: Role-filtered Action Icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Computer Vision Part Label Scanner (ONLY for Atelier Pro & Magasin de Pièces)
                if (currentRole.isWorkshopAccount || currentRole.isPartsStoreAccount) {
                    Surface(
                        shape = RoundedCornerShape(11.dp),
                        color = GraphiteDark,
                        border = BorderStroke(1.dp, MekanikBrandOrange.copy(alpha = 0.6f)),
                        onClick = onOpenPartLabelVisionScanner,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("top_btn_part_label_vision_scanner")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scanner Étiquette Pièce (Code-Barres / QR)",
                                tint = MekanikBrandOrange,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }

                // Hands-Free Voice Search (ONLY for Atelier Pro Mechanic)
                if (currentRole.isWorkshopAccount) {
                    Surface(
                        shape = RoundedCornerShape(11.dp),
                        color = GraphiteDark,
                        border = BorderStroke(1.dp, MetalBorder),
                        onClick = onOpenHandsFreeVoiceSearch,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("top_btn_hands_free_voice_search")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Recherche Vocale Mains-Libres",
                                tint = MekanikBrandOrange,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }

                // MekanikAI OBD-II Quick Access (ONLY for Atelier Pro)
                if (currentRole.isWorkshopAccount) {
                    Surface(
                        shape = RoundedCornerShape(11.dp),
                        color = GraphiteDark,
                        border = BorderStroke(1.dp, MetalBorder),
                        onClick = { onOpenMekanikAiObd(null) },
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("top_btn_mekanik_ai_obd")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Diagnostic MekanikAI OBD-II",
                                tint = OffWhite,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }

                // Real-Time Notifications Bell with Badge
                Surface(
                    shape = RoundedCornerShape(11.dp),
                    color = GraphiteDark,
                    border = BorderStroke(1.dp, MetalBorder),
                    onClick = onOpenNotificationsCenter,
                    modifier = Modifier
                        .height(42.dp)
                        .testTag("top_btn_realtime_notifications")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notifications Temps Réel",
                            tint = if (isRealtimeStreamEnabled) MekanikValidationGreen else OffWhite,
                            modifier = Modifier.size(19.dp)
                        )
                        if (unreadNotificationsCount > 0) {
                            Surface(
                                shape = CircleShape,
                                color = MekanikBrandOrange
                            ) {
                                Text(
                                    text = "$unreadNotificationsCount",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }

                // Portail Web SaaS MEKANIK+ (PC Sidebar & 6 Espaces A-F)
                Surface(
                    shape = RoundedCornerShape(11.dp),
                    color = MekanikBrandOrange.copy(alpha = 0.16f),
                    border = BorderStroke(1.2.dp, MekanikBrandOrange),
                    onClick = onOpenSaasWebPlatform,
                    modifier = Modifier
                        .height(42.dp)
                        .testTag("top_btn_open_saas_web_platform")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DesktopWindows,
                            contentDescription = "Portail Web SaaS MEKANIK+",
                            tint = MekanikBrandOrange,
                            modifier = Modifier.size(17.dp)
                        )
                        Text(
                            text = "SaaS Web",
                            color = OffWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.5.sp
                        )
                    }
                }

                // Quick Onboarding Guide Button
                Surface(
                    shape = RoundedCornerShape(11.dp),
                    color = GraphiteDark,
                    border = BorderStroke(1.dp, MetalBorder),
                    onClick = onStartOnboardingGuide,
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("top_btn_onboarding_guide")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Guide de démarrage rapide",
                            tint = AluminumGray,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }

                // Mode Sombre / Mode Normal Toggle
                Surface(
                    shape = RoundedCornerShape(11.dp),
                    color = GraphiteDark,
                    border = BorderStroke(1.dp, MetalBorder),
                    onClick = { MekanikThemeController.toggleNormalOrDarkMode(context) },
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("top_btn_toggle_normal_dark_mode")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isLightNormal) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Changer le thème d'affichage",
                            tint = MekanikBrandOrange,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Menu Latéral Gauche (Navigation organisée par domaines métiers, sobre et sans surcharge).
 */
@Composable
fun MekanikLeftDrawerContent(
    currentRole: AccountRole,
    onSelectRole: (AccountRole) -> Unit,
    onNavigateHome: () -> Unit,
    onOpenSearchByCity: () -> Unit,
    onOpenSearchInStore: () -> Unit,
    onOpenSearchPart: () -> Unit,
    onOpenRepairOrder: () -> Unit,
    onOpenTechnicalDiagrams: () -> Unit,
    onOpenSparePartsCatalog: () -> Unit,
    onOpenPartVendors: () -> Unit,
    onOpenVendorAddPhotoAi: () -> Unit,
    onOpenAutoForum: () -> Unit,
    onOpenProSocialNetwork: () -> Unit,
    onOpenMonthlyZipArchive: () -> Unit,
    onOpenFleetAndRental: () -> Unit,
    onOpenHandsFreeVoiceSearch: () -> Unit = {},
    onOpenPartLabelVisionScanner: () -> Unit = {},
    onOpenMecaVerifHistory: () -> Unit = {},
    onOpenMekanikAiObd: (String?) -> Unit = {},
    onOpenNotificationsCenter: () -> Unit = {},
    onStartOnboardingGuide: () -> Unit = {},
    onOpenAccountSelector: () -> Unit = {},
    onOpenAccountAdmin2Fa: () -> Unit = {},
    onOpenSuperAdminPortal: () -> Unit = {},
    onCloseDrawer: () -> Unit
) {
    val context = LocalContext.current
    val isLightNormal = MekanikThemeController.isLightModeActive

    ModalDrawerSheet(
        drawerContainerColor = GraphiteDark,
        drawerContentColor = OffWhite,
        modifier = Modifier
            .width(330.dp)
            .testTag("left_three_bars_drawer")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Drawer Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "DMS MÉCANICAUTO",
                            color = OffWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "+",
                            color = MekanikBrandOrange,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                    }
                    Text(
                        text = "Système de Gestion Automobile & Atelier",
                        color = AluminumGray,
                        fontSize = 11.5.sp
                    )
                }
                IconButton(onClick = onCloseDrawer) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer le menu", tint = OffWhite)
                }
            }

            // Theme Mode Switcher Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MetalCard,
                border = BorderStroke(1.dp, MetalBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "APPARENCE DE L'INTERFACE",
                        color = AluminumGray,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppDisplayThemeMode.entries.forEach { mode ->
                            val selected = (mode == AppDisplayThemeMode.LIGHT_MODE && isLightNormal) ||
                                (mode == AppDisplayThemeMode.DARK_MODE && !isLightNormal)
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        MekanikThemeController.setDisplayThemeMode(mode, context)
                                    },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) MekanikBrandOrange else GraphiteDark,
                                border = BorderStroke(1.dp, if (selected) MekanikBrandOrange else MetalBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 9.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = mode.shortLabel,
                                        color = if (selected) Color.White else OffWhite,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.5.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            DrawerSectionHeader(title = "OPTIONS DISPONIBLES (${currentRole.badgeLabel})")

            DrawerNavButton(
                icon = Icons.Default.Dashboard,
                title = "Accueil (${currentRole.shortTitle})",
                subtitle = currentRole.formulaName,
                onClick = {
                    onNavigateHome()
                    onCloseDrawer()
                },
                testTag = "drawer_item_accueil"
            )

            // MécaVerif+™ is available for all roles (buying, checking client VIN, checking fleet)
            DrawerNavButton(
                icon = Icons.Default.VerifiedUser,
                title = "MécaVerif+™ (Historique & Kilométrage)",
                subtitle = "Service payant : Carrosserie, mécanicien & compteur réel par VIN",
                onClick = {
                    onOpenMecaVerifHistory()
                    onCloseDrawer()
                },
                testTag = "drawer_item_mecaverif_history"
            )

            // --- OPTIONS EXCLUSIVES : COMPTE ATELIER PRO (GARAGE_PRO) ---
            if (currentRole.isWorkshopAccount) {
                DrawerNavButton(
                    icon = Icons.Default.ReceiptLong,
                    title = "Ordres de réparation, Devis & DMS",
                    subtitle = "DMS Mécanicauto+ (T1/T2/T3), carte grise, checklist et signature",
                    onClick = {
                        onOpenRepairOrder()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_repair_order"
                )

                DrawerNavButton(
                    icon = Icons.Default.AccountTree,
                    title = "Schémas techniques & Vues éclatées",
                    subtitle = "Documentation constructeur, câblages et couples",
                    onClick = {
                        onOpenTechnicalDiagrams()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_technical_diagrams"
                )

                DrawerNavButton(
                    icon = Icons.Default.QrCodeScanner,
                    title = "Scanner Étiquette Pièce (Code-Barres / QR)",
                    subtitle = "Vision IA : Réf catalogue, compatibilité VIN & montage",
                    onClick = {
                        onOpenPartLabelVisionScanner()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_part_label_vision_scanner"
                )

                DrawerNavButton(
                    icon = Icons.Default.Mic,
                    title = "Recherche vocale mains-libres",
                    subtitle = "Dictée vocale de références & schémas + lecture TTS",
                    onClick = {
                        onOpenHandsFreeVoiceSearch()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_hands_free_voice_search"
                )

                DrawerNavButton(
                    icon = Icons.Default.Psychology,
                    title = "Diagnostic OBD-II & MekanikAI",
                    subtitle = "Analyse des codes défauts DTC, causes et pièces liées",
                    onClick = {
                        onOpenMekanikAiObd(null)
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_mekanik_ai_obd"
                )

                DrawerNavButton(
                    icon = Icons.Default.Build,
                    title = "Catalogue pièces & Fournisseurs",
                    subtitle = "Équivalences OEM, références PR/OPR et grossistes",
                    onClick = {
                        onOpenSparePartsCatalog()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_spare_parts"
                )

                DrawerNavButton(
                    icon = Icons.Default.Groups,
                    title = "Réseau Social Pro B2B & Forum",
                    subtitle = "Échanges techniques entre ateliers et magasins de pièces",
                    onClick = {
                        onOpenProSocialNetwork()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_pro_social"
                )

                DrawerNavButton(
                    icon = Icons.Default.FolderZip,
                    title = "Archive mensuelle Atelier (.ZIP)",
                    subtitle = "Exporter ordres de réparation et factures du mois",
                    onClick = {
                        onOpenMonthlyZipArchive()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_monthly_zip_archive"
                )
            }

            // --- OPTIONS EXCLUSIVES : COMPTE MAGASIN DE PIÈCES (VENDEUR_PIECES) ---
            if (currentRole.isPartsStoreAccount) {
                DrawerNavButton(
                    icon = Icons.Default.PhotoCamera,
                    title = "Ajouter une pièce au Store (Photo + IA)",
                    subtitle = "Extraction réf OEM, 4 photos studio et compatibilités",
                    onClick = {
                        onOpenVendorAddPhotoAi()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_vendor_add_photo_ai"
                )

                DrawerNavButton(
                    icon = Icons.Default.QrCodeScanner,
                    title = "Scanner Étiquette Pièce (Code-Barres / QR)",
                    subtitle = "Comptoir : Réf OEM, compatibilité châssis & équivalences",
                    onClick = {
                        onOpenPartLabelVisionScanner()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_part_label_vision_scanner"
                )

                DrawerNavButton(
                    icon = Icons.Default.Build,
                    title = "Catalogue pièces & Équivalences OEM",
                    subtitle = "Références constructeur, cross-références et stock",
                    onClick = {
                        onOpenSparePartsCatalog()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_spare_parts"
                )

                DrawerNavButton(
                    icon = Icons.Default.Storefront,
                    title = "Gestion Store & Recherche Pièce",
                    subtitle = "Explorer et gérer les références disponibles en magasin",
                    onClick = {
                        onOpenSearchInStore()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_search_store"
                )

                DrawerNavButton(
                    icon = Icons.Default.Groups,
                    title = "Réseau Social Pro B2B (Annonces Pièces)",
                    subtitle = "Publier vos arrivages pour les garages & messagerie B2B",
                    onClick = {
                        onOpenProSocialNetwork()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_pro_social"
                )

                DrawerNavButton(
                    icon = Icons.Default.FolderZip,
                    title = "Archive mensuelle Ventes Magasin (.ZIP)",
                    subtitle = "Exporter bons de livraison, ventes comptoir et factures",
                    onClick = {
                        onOpenMonthlyZipArchive()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_monthly_zip_archive"
                )
            }

            // --- OPTIONS EXCLUSIVES : COMPTE PARTICULIER (CONDUCTEUR) ---
            if (currentRole.isIndividualAccount) {
                DrawerNavButton(
                    icon = Icons.Default.Settings,
                    title = "Trouver une pièce pour ma voiture",
                    subtitle = "Rechercher une pièce compatible avec mon numéro de châssis",
                    onClick = {
                        onOpenSearchPart()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_search_part"
                )

                DrawerNavButton(
                    icon = Icons.Default.LocationOn,
                    title = "Trouver un Atelier ou Magasin par Ville",
                    subtitle = "Rechercher un garage ou vendeur de pièces dans ma wilaya",
                    onClick = {
                        onOpenSearchByCity()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_search_city"
                )

                DrawerNavButton(
                    icon = Icons.Default.Build,
                    title = "Catalogue Pièces Mon Véhicule",
                    subtitle = "Consulter les pièces et prix estimés pour ma voiture",
                    onClick = {
                        onOpenSparePartsCatalog()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_spare_parts"
                )

                DrawerNavButton(
                    icon = Icons.Default.Forum,
                    title = "Entraide & Forum Auto par Marque",
                    subtitle = "Poser une question sur l'entretien de mon modèle",
                    onClick = {
                        onOpenAutoForum()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_forum_auto"
                )
            }

            // --- OPTIONS EXCLUSIVES : COMPTE ENTREPRISE & FLOTTE (LOCATION_VOITURES) ---
            if (currentRole.isEnterpriseAccount) {
                DrawerNavButton(
                    icon = Icons.Default.DirectionsCar,
                    title = "Gestion de Parc Entreprise & Flotte",
                    subtitle = "Véhicules du parc, contrats, cautions et états départ/retour",
                    onClick = {
                        onOpenFleetAndRental()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_fleet_rental"
                )

                DrawerNavButton(
                    icon = Icons.Default.ReceiptLong,
                    title = "Suivi Maintenance & Ordres du Parc",
                    subtitle = "Suivi des entretiens périodiques et coûts flotte",
                    onClick = {
                        onOpenRepairOrder()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_repair_order"
                )

                DrawerNavButton(
                    icon = Icons.Default.FolderZip,
                    title = "Archive Comptable Flotte (.ZIP)",
                    subtitle = "Exporter contrats, factures et bilans mensuels du parc",
                    onClick = {
                        onOpenMonthlyZipArchive()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_monthly_zip_archive"
                )
            }

            // --- OPTIONS EXCLUSIVES : COMPTE SPÉCIAL CARROSSERIE & TÔLERIE (ATELIER_CARROSSERIE) ---
            if (currentRole.isBodyshopAccount) {
                DrawerNavButton(
                    icon = Icons.Default.DirectionsCar,
                    title = "Pièces Tôlerie, Plastique & Optiques par VIN",
                    subtitle = "Capot, ailes, boucliers, phares, vitrages & agrafes constructeur",
                    onClick = {
                        onOpenMecaVerifHistory()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_bodyshop_vin_parts"
                )

                DrawerNavButton(
                    icon = Icons.Default.PhotoCamera,
                    title = "Photos AVANT & APRÈS Réparation (Obligatoire)",
                    subtitle = "Certifier la tôle avant redressage et après peinture étuve",
                    onClick = {
                        onOpenMecaVerifHistory()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_bodyshop_mandatory_photos"
                )

                DrawerNavButton(
                    icon = Icons.Default.ReceiptLong,
                    title = "Devis Carrosserie, Marbre & Peinture",
                    subtitle = "Ordres de réparation carrosserie et chiffrage sinistre",
                    onClick = {
                        onOpenRepairOrder()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_bodyshop_repair_order"
                )

                DrawerNavButton(
                    icon = Icons.Default.Build,
                    title = "Catalogue Général & Fournisseurs Carrosserie",
                    subtitle = "Consulter les disponibilités tôlerie et peinture",
                    onClick = {
                        onOpenSparePartsCatalog()
                        onCloseDrawer()
                    },
                    testTag = "drawer_item_bodyshop_catalog"
                )
            }

            HorizontalDivider(color = MetalBorder)

            DrawerSectionHeader(title = "SÉCURITÉ 2FA, SOUS-COMPTES & SUPER-ADMIN")

            DrawerNavButton(
                icon = Icons.Default.ManageAccounts,
                title = "Changer de Formule (AccountSelector Cloud)",
                subtitle = "Particulier, Atelier, Magasin, Mixte (1+4), Carrosserie, Entreprise",
                onClick = {
                    onCloseDrawer()
                    onOpenAccountSelector()
                },
                testTag = "drawer_item_account_selector"
            )

            DrawerNavButton(
                icon = Icons.Default.PhonelinkLock,
                title = "Admin Compte & Sous-Comptes (2FA Google)",
                subtitle = "Double facteur Google Authenticator : ajouter/supprimer sous-comptes",
                onClick = {
                    onCloseDrawer()
                    onOpenAccountAdmin2Fa()
                },
                testTag = "drawer_item_account_admin_2fa"
            )

            DrawerNavButton(
                icon = Icons.Default.AdminPanelSettings,
                title = "Console Super-Administrateur (2FA)",
                subtitle = "Activer/désactiver toutes les options & ajouter APIs (TikTok/Ext) sans code",
                onClick = {
                    onCloseDrawer()
                    onOpenSuperAdminPortal()
                },
                testTag = "drawer_item_super_admin_portal"
            )

            DrawerNavButton(
                icon = Icons.Default.NotificationsNone,
                title = "Notifications (${currentRole.shortTitle})",
                subtitle = "Alertes dédiées à votre formule de compte",
                onClick = {
                    onCloseDrawer()
                    onOpenNotificationsCenter()
                },
                testTag = "drawer_item_realtime_notifications"
            )
        }
    }
}

@Composable
private fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        color = AluminumGray,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
    )
}

@Composable
private fun DrawerNavButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        color = MetalCard,
        border = BorderStroke(1.dp, MetalBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                Text(
                    text = title,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp
                )
                Text(
                    text = subtitle,
                    color = AluminumGray,
                    fontSize = 11.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = AluminumGray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Tableau de Bord Principal MEKANIK+ — AUTOMOTIVE INTELLIGENCE PLATFORM.
 * Design professionnel, minimaliste, lisible et structuré avec sections repliables.
 */
@Composable
fun SimplifiedModernHomeDashboard(
    currentRole: AccountRole,
    onSelectRole: (AccountRole) -> Unit,
    onOpenDrawerMenu: () -> Unit,
    onOpenSearchByCity: () -> Unit,
    onOpenSearchInStore: () -> Unit,
    onOpenSearchPart: () -> Unit,
    onOpenVendorAddPhotoAi: () -> Unit,
    onOpenAutoForum: () -> Unit,
    onOpenProSocialNetwork: () -> Unit,
    onOpenMonthlyZipArchive: () -> Unit,
    onOpenRepairOrder: () -> Unit,
    onOpenTechnicalDiagrams: () -> Unit,
    onOpenSparePartsCatalog: () -> Unit,
    onOpenPartVendors: () -> Unit,
    onOpenFleetAndRental: () -> Unit,
    onOpenHandsFreeVoiceSearch: () -> Unit = {},
    onOpenMekanikAiObd: (String?) -> Unit = {},
    isRealtimeStreamEnabled: Boolean = true,
    unreadNotificationsCount: Int = 0,
    onToggleRealtimeStream: (Boolean) -> Unit = {},
    onTriggerInstantRealtimeEvent: (NotificationCategory) -> Unit = {},
    onOpenNotificationsCenter: () -> Unit = {},
    onStartOnboardingGuide: () -> Unit = {},
    onOpenAccountSelector: () -> Unit = {},
    onOpenAccountAdmin2Fa: () -> Unit = {},
    onOpenSuperAdminPortal: () -> Unit = {},
    onOpenMecaVerifHistory: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Sélecteur de Profil Métier (Particulier, Atelier, Magasin, Mixte Atelier+Magasin, Carrosserie, Entreprise)
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MetalCard,
            border = BorderStroke(1.dp, MetalBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Espace de travail actif • ${currentRole.subscriptionPriceLabel}",
                            color = OffWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Séparation stricte par rôle • MécaVerif+™ externe : ${currentRole.mecaVerifExternalRateDzd} DA / VIN",
                            color = AluminumGray,
                            fontSize = 11.5.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MekanikBrandOrange.copy(alpha = 0.15f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onOpenAccountSelector() }
                            .testTag("btn_open_account_selector_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ManageAccounts,
                                contentDescription = null,
                                tint = MekanikBrandOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = currentRole.shortTitle,
                                color = MekanikBrandOrange,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AccountRole.entries.forEach { role ->
                        val selected = role == currentRole
                        val roleIcon = when (role) {
                            AccountRole.GARAGE_PRO -> Icons.Default.Handyman
                            AccountRole.VENDEUR_PIECES -> Icons.Default.Storefront
                            AccountRole.ATELIER_MAGASIN_MIXTE -> Icons.Default.PrecisionManufacturing
                            AccountRole.ATELIER_CARROSSERIE -> Icons.Default.CarRepair
                            AccountRole.CONDUCTEUR -> Icons.Default.DirectionsCar
                            AccountRole.LOCATION_VOITURES -> Icons.Default.Key
                        }
                        Surface(
                            modifier = Modifier
                                .widthIn(min = 106.dp)
                                .heightIn(min = 54.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelectRole(role) }
                                .testTag("role_chip_${role.name.lowercase()}"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) MekanikBrandOrange else GraphiteDark,
                            border = BorderStroke(1.dp, if (selected) MekanikBrandOrange else MetalBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = roleIcon,
                                    contentDescription = role.shortTitle,
                                    tint = if (selected) Color.White else AluminumGray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = role.shortTitle,
                                    color = if (selected) Color.White else OffWhite,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = role.subscriptionPriceLabel,
                                    color = if (selected) Color.White.copy(alpha = 0.9f) else AluminumGray,
                                    fontSize = 9.5.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Boutons d'accès direct : AccountSelector Firestore + Admin Compte 2FA (Sous-Comptes) + Super-Admin (Options & APIs Sans Code)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenAccountSelector,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MekanikBrandOrange),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_btn_open_account_selector")
                    ) {
                        Icon(Icons.Default.ManageAccounts, contentDescription = null, tint = MekanikBrandOrange, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("AccountSelector", color = OffWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp, maxLines = 1)
                    }

                    OutlinedButton(
                        onClick = onOpenAccountAdmin2Fa,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MekanikValidationGreen),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1.15f)
                            .testTag("home_btn_open_account_admin_2fa")
                    ) {
                        Icon(Icons.Default.PhonelinkLock, contentDescription = null, tint = MekanikValidationGreen, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Sous-Comptes (2FA)", color = MekanikValidationGreen, fontWeight = FontWeight.Bold, fontSize = 10.5.sp, maxLines = 1)
                    }

                    OutlinedButton(
                        onClick = onOpenSuperAdminPortal,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MetalBorder),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_btn_open_super_admin_portal")
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MekanikBrandOrange, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Super-Admin API", color = OffWhite, fontWeight = FontWeight.Bold, fontSize = 10.5.sp, maxLines = 1)
                    }
                }
                // Résumé des options strictement actives pour la formule sélectionnée
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GraphiteDark,
                    border = BorderStroke(1.dp, MekanikBrandOrange.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentRole.formulaName,
                                color = MekanikBrandOrange,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = MekanikValidationGreen.copy(alpha = 0.16f)
                            ) {
                                Text(
                                    text = "COMPTE ISOLÉ",
                                    color = MekanikValidationGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        currentRole.allowedFeaturesSummary.forEach { feature ->
                            Text(
                                text = "✓ $feature",
                                color = OffWhite,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = "🔒 ${currentRole.excludedFeaturesSummary}",
                            color = AluminumGray,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // =========================================================================
        // AFFICHAGE STRICTEMENT SÉPARÉ SELON LE COMPTE CHOISI
        // =========================================================================
        when (currentRole) {
            // -----------------------------------------------------------------
            // 1. COMPTE ATELIER & MÉCANICIEN PRO (GARAGE_PRO)
            // -----------------------------------------------------------------
            AccountRole.GARAGE_PRO -> {
                // Outils de recherche Atelier & Commande Vocale Mains-Libres
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MetalCard,
                    border = BorderStroke(1.dp, MetalBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Recherche Atelier : Pièces, Schémas & Commande Vocale",
                            color = OffWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        MekanikPrimaryButton(
                            text = "Recherche Vocale Mains-Libres (Pièces & Schémas)",
                            icon = Icons.Default.Mic,
                            onClick = onOpenHandsFreeVoiceSearch,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "home_hands_free_mechanic_voice_banner"
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SimplifiedQuickActionButton(
                                icon = Icons.Default.Settings,
                                label = "Par réf. pièce",
                                onClick = onOpenSearchPart,
                                modifier = Modifier.weight(1f).testTag("home_simple_search_part_btn")
                            )
                            SimplifiedQuickActionButton(
                                icon = Icons.Default.Storefront,
                                label = "Grossistes",
                                onClick = onOpenSearchInStore,
                                modifier = Modifier.weight(1f).testTag("home_simple_search_store_btn")
                            )
                            SimplifiedQuickActionButton(
                                icon = Icons.Default.Mic,
                                label = "Vocal",
                                onClick = onOpenHandsFreeVoiceSearch,
                                modifier = Modifier.weight(1f).testTag("home_simple_hands_free_voice_btn")
                            )
                        }
                    }
                }

                // Modules Atelier & Réparation (SANS l'option Vendeur Ajout Photo IA)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MetalCard,
                    border = BorderStroke(1.dp, MetalBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Modules Atelier & Réparation DMS Mécanicauto+",
                            color = OffWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RoleFeatureCard(
                                icon = Icons.Default.ReceiptLong,
                                title = "Ordres de réparation",
                                subtitle = "Devis, DMS T1/T2/T3, carte grise et signature",
                                onClick = onOpenRepairOrder,
                                modifier = Modifier.weight(1f)
                            )
                            RoleFeatureCard(
                                icon = Icons.Default.AccountTree,
                                title = "Schémas techniques",
                                subtitle = "Vues éclatées OEM, câblages et couples",
                                onClick = onOpenTechnicalDiagrams,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RoleFeatureCard(
                                icon = Icons.Default.Build,
                                title = "Catalogue de pièces",
                                subtitle = "Références constructeur et compatibilités",
                                onClick = onOpenSparePartsCatalog,
                                modifier = Modifier.weight(1f)
                            )
                            RoleFeatureCard(
                                icon = Icons.Default.Groups,
                                title = "Réseau Pro B2B",
                                subtitle = "Échanges ateliers/vendeurs & messagerie",
                                onClick = onOpenProSocialNetwork,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Diagnostic OBD-II & MekanikAI (Réservé à l'Atelier)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_mekanik_ai_obd_analyzer_card"),
                    shape = RoundedCornerShape(14.dp),
                    color = MetalCard,
                    border = BorderStroke(1.dp, MetalBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Diagnostic OBD-II & MekanikAI",
                                    color = OffWhite,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Analyse des codes défauts DTC, causes probables et pièces liées",
                                    color = AluminumGray,
                                    fontSize = 12.sp
                                )
                            }
                            MekanikPrimaryButton(
                                text = "Ouvrir",
                                onClick = { onOpenMekanikAiObd(null) },
                                modifier = Modifier.testTag("home_btn_open_mekanik_ai_obd")
                            )
                        }
                    }
                }
            }

            // -----------------------------------------------------------------
            // 2. COMPTE MAGASIN DE PIÈCES DE RECHANGE (VENDEUR_PIECES)
            // -----------------------------------------------------------------
            AccountRole.VENDEUR_PIECES -> {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MetalCard,
                    border = BorderStroke(1.dp, MetalBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Espace Magasin de Pièces de Rechange",
                            color = OffWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Outils exclusifs pour la gestion de votre store, l'ajout de pièces par IA et la vente au comptoir",
                            color = AluminumGray,
                            fontSize = 12.sp
                        )

                        // Action exclusive Vendeur : Ajouter une pièce au Store par Photo + IA
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onOpenVendorAddPhotoAi() }
                                .testTag("home_vendor_add_part_photo_ai_card"),
                            shape = RoundedCornerShape(12.dp),
                            color = GraphiteDark,
                            border = BorderStroke(1.2.dp, MekanikBrandOrange)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MekanikBrandOrange.copy(alpha = 0.18f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.PhotoCamera,
                                        contentDescription = null,
                                        tint = MekanikBrandOrange,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Ajouter une pièce au Store par Photo (IA)",
                                        color = OffWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    )
                                    Text(
                                        text = "Extraction automatique de la référence OEM, 4 vues studio et véhicules compatibles",
                                        color = AluminumGray,
                                        fontSize = 11.5.sp
                                    )
                                }
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MekanikBrandOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RoleFeatureCard(
                                icon = Icons.Default.Build,
                                title = "Catalogue & Équivalences",
                                subtitle = "Références OEM, cross-références et compatibilités",
                                onClick = onOpenSparePartsCatalog,
                                modifier = Modifier.weight(1f)
                            )
                            RoleFeatureCard(
                                icon = Icons.Default.Storefront,
                                title = "Mon Store & Stock",
                                subtitle = "Recherche dans le store et gestion des références",
                                onClick = onOpenSearchInStore,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RoleFeatureCard(
                                icon = Icons.Default.Groups,
                                title = "Réseau Pro B2B",
                                subtitle = "Publier vos arrivages pour les garages partenaires",
                                onClick = onOpenProSocialNetwork,
                                modifier = Modifier.weight(1f)
                            )
                            RoleFeatureCard(
                                icon = Icons.Default.FolderZip,
                                title = "Archives Ventes .ZIP",
                                subtitle = "Exporter vos ventes, bons et factures du mois",
                                onClick = onOpenMonthlyZipArchive,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // -----------------------------------------------------------------
            // 3. COMPTE PARTICULIER / CONDUCTEUR (CONDUCTEUR)
            // -----------------------------------------------------------------
            AccountRole.CONDUCTEUR -> {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MetalCard,
                    border = BorderStroke(1.dp, MetalBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Mon Espace Particulier & Véhicule Personnel",
                            color = OffWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Uniquement les services utiles pour votre voiture ou votre futur achat (sans les outils d'atelier ni de magasin)",
                            color = AluminumGray,
                            fontSize = 12.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RoleFeatureCard(
                                icon = Icons.Default.Settings,
                                title = "Trouver une pièce",
                                subtitle = "Identifier la pièce compatible avec ma voiture",
                                onClick = onOpenSearchPart,
                                modifier = Modifier.weight(1f)
                            )
                            RoleFeatureCard(
                                icon = Icons.Default.LocationOn,
                                title = "Ateliers & Magasins par Ville",
                                subtitle = "Trouver un garage ou vendeur dans ma wilaya",
                                onClick = onOpenSearchByCity,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RoleFeatureCard(
                                icon = Icons.Default.Build,
                                title = "Catalogue Mon Véhicule",
                                subtitle = "Voir les pièces et tarifs estimés pour mon VIN",
                                onClick = onOpenSparePartsCatalog,
                                modifier = Modifier.weight(1f)
                            )
                            RoleFeatureCard(
                                icon = Icons.Default.Forum,
                                title = "Forum Auto Entraide",
                                subtitle = "Conseils d'entretien par marque et modèle",
                                onClick = onOpenAutoForum,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // -----------------------------------------------------------------
            // 4. COMPTE ENTREPRISE & PARC AUTOMOBILE (LOCATION_VOITURES)
            // -----------------------------------------------------------------
            AccountRole.LOCATION_VOITURES -> {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MetalCard,
                    border = BorderStroke(1.dp, MetalBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Espace Entreprise, Parc Auto & Flotte",
                            color = OffWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Gestion dédiée aux entreprises, gestionnaires de parc et agences de location",
                            color = AluminumGray,
                            fontSize = 12.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RoleFeatureCard(
                                icon = Icons.Default.DirectionsCar,
                                title = "Parc & Contrats Flotte",
                                subtitle = "Véhicules, contrats départ/retour et cautions",
                                onClick = onOpenFleetAndRental,
                                modifier = Modifier.weight(1f)
                            )
                            RoleFeatureCard(
                                icon = Icons.Default.ReceiptLong,
                                title = "Maintenance du Parc",
                                subtitle = "Suivi des entretiens et ordres de la flotte",
                                onClick = onOpenRepairOrder,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RoleFeatureCard(
                                icon = Icons.Default.FolderZip,
                                title = "Archives Comptables .ZIP",
                                subtitle = "Exporter les factures et contrats mensuels",
                                onClick = onOpenMonthlyZipArchive,
                                modifier = Modifier.weight(1f)
                            )
                            RoleFeatureCard(
                                icon = Icons.Default.Store,
                                title = "Fournisseurs Partenaires",
                                subtitle = "Approvisionnement pièces pour la flotte",
                                onClick = onOpenPartVendors,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // -----------------------------------------------------------------
            // 5. COMPTE MIXTE : ATELIER MÉCANIQUE + MAGASIN DE PIÈCES (ATELIER_MAGASIN_MIXTE)
            //    (1 Compte Principal + 3 ou 4 Sous-Comptes • 100% Atelier + 100% Magasin)
            // -----------------------------------------------------------------
            AccountRole.ATELIER_MAGASIN_MIXTE -> {
                val hybridMembers = SuperAdminGlobalControlRegistry.getSubAccountsForRole(AccountRole.ATELIER_MAGASIN_MIXTE)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MetalCard,
                    border = BorderStroke(1.5.dp, MekanikBrandOrange)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Compte Mixte : Atelier Montage + Magasin de Pièces",
                                    color = OffWhite,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "1 Compte Principal + ${hybridMembers.size} Sous-Comptes • Toutes les options Atelier + Magasin réunies",
                                    color = MekanikBrandOrange,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp
                                )
                            }
                            OutlinedButton(
                                onClick = onOpenAccountAdmin2Fa,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, MekanikValidationGreen),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Gérer (2FA)", color = MekanikValidationGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Résumé des sous-comptes actifs (Atelier + Magasin)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            hybridMembers.forEach { sub ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = GraphiteDark,
                                    border = BorderStroke(1.dp, MetalBorder)
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                                        Text(
                                            text = sub.departmentBadge,
                                            color = MekanikBrandOrange,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 9.5.sp
                                        )
                                        Text(
                                            text = sub.fullName,
                                            color = OffWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = sub.roleTitle,
                                            color = AluminumGray,
                                            fontSize = 10.5.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Section A : 100% des outils Magasin de Pièces
                        Text(
                            text = "VOLET 1 : MAGASIN DE PIÈCES DE RECHANGE & COMPTOIR",
                            color = MekanikBrandOrange,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.5.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RoleFeatureCard(
                                icon = Icons.Default.PhotoCamera,
                                title = "Ajout Pièce Store IA",
                                subtitle = "4 photos studio, réf OEM & compatibilités châssis",
                                onClick = onOpenVendorAddPhotoAi,
                                modifier = Modifier.weight(1f)
                            )
                            RoleFeatureCard(
                                icon = Icons.Default.Storefront,
                                title = "Stock & Comptoir Pièces",
                                subtitle = "Vente comptoir ou transfert direct vers pont atelier",
                                onClick = onOpenSearchInStore,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Section B : 100% des outils Atelier Mécanique de Montage
                        Text(
                            text = "VOLET 2 : ATELIER MÉCANIQUE DE MONTAGE & DIAGNOSTIC",
                            color = MekanikValidationGreen,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.5.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RoleFeatureCard(
                                icon = Icons.Default.ReceiptLong,
                                title = "Ordres de Réparation & Montage",
                                subtitle = "Devis pièce + main d'œuvre T1/T2/T3 & signature",
                                onClick = onOpenRepairOrder,
                                modifier = Modifier.weight(1f)
                            )
                            RoleFeatureCard(
                                icon = Icons.Default.AccountTree,
                                title = "Schémas & Couples OEM",
                                subtitle = "Vues éclatées constructeur et méthodes de montage",
                                onClick = onOpenTechnicalDiagrams,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RoleFeatureCard(
                                icon = Icons.Default.Psychology,
                                title = "MekanikAI & OBD-II",
                                subtitle = "Diagnostic codes défauts DTC & recherche vocale",
                                onClick = { onOpenMekanikAiObd(null) },
                                modifier = Modifier.weight(1f)
                            )
                            RoleFeatureCard(
                                icon = Icons.Default.Build,
                                title = "Catalogue & Équivalences",
                                subtitle = "Croisement OEM/IAM et réapprovisionnement B2B",
                                onClick = onOpenSparePartsCatalog,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // -----------------------------------------------------------------
            // 6. COMPTE SPÉCIAL CARROSSERIE & TÔLERIE (ATELIER_CARROSSERIE)
            //    (Pièces Tôlerie/Plastique par VIN + Photos AVANT et APRÈS obligatoires)
            // -----------------------------------------------------------------
            AccountRole.ATELIER_CARROSSERIE -> {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MetalCard,
                    border = BorderStroke(1.5.dp, MekanikBrandOrange)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Espace Spécial Carrosserie, Tôlerie & Peinture (19 500 DA/an)",
                            color = OffWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Identification par N° de Châssis (VIN) des pièces de tôlerie, plastique, boucliers, optiques et agrafes + Obligation de photos AVANT et APRÈS réparation.",
                            color = AluminumGray,
                            fontSize = 12.sp
                        )

                        MekanikPrimaryButton(
                            text = "Ouvrir Catalogue Tôlerie VIN & Photos Avant/Après Obligatoires",
                            icon = Icons.Default.PhotoCamera,
                            onClick = onOpenMecaVerifHistory,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "home_btn_bodyshop_tolerie_photos"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RoleFeatureCard(
                                icon = Icons.Default.DirectionsCar,
                                title = "Tôlerie & Plastique par VIN",
                                subtitle = "Capot, ailes, pare-chocs, traverses, phares & clips OEM",
                                onClick = onOpenMecaVerifHistory,
                                modifier = Modifier.weight(1f)
                            )
                            RoleFeatureCard(
                                icon = Icons.Default.CameraEnhance,
                                title = "Photos Avant/Après (Obligatoire)",
                                subtitle = "Preuve photo tôle endommagée vs réparée pour MécaVerif+™",
                                onClick = onOpenMecaVerifHistory,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RoleFeatureCard(
                                icon = Icons.Default.ReceiptLong,
                                title = "Devis Carrosserie & Peinture",
                                subtitle = "Chiffrage redressage, marbre laser et cabine étuve",
                                onClick = onOpenRepairOrder,
                                modifier = Modifier.weight(1f)
                            )
                            RoleFeatureCard(
                                icon = Icons.Default.VerifiedUser,
                                title = "MécaVerif+™ (250 DA / VIN)",
                                subtitle = "Service externe : vérifier le châssis et le kilométrage",
                                onClick = onOpenMecaVerifHistory,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SimplifiedQuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .heightIn(min = 52.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = GraphiteDark,
        border = BorderStroke(1.dp, MetalBorder)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MekanikBrandOrange,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = OffWhite,
                fontWeight = FontWeight.Medium,
                fontSize = 11.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun RoleFeatureCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .heightIn(min = 96.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = GraphiteDark,
        border = BorderStroke(1.dp, MetalBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(MetalCard)
                        .border(1.dp, MetalBorder, RoundedCornerShape(9.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MekanikBrandOrange,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = title,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = subtitle,
                color = AluminumGray,
                fontSize = 11.5.sp,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
