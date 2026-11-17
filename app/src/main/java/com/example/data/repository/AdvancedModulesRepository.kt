package com.example.data.repository

import com.example.data.model.*

object AdvancedModulesRepository {

  val sampleInspections: List<VehicleInspection> = listOf(
    VehicleInspection(
      id = "INSP-001",
      inspectionNumber = "INSP-2026-0042",
      vin = "VF77J9HP4EJ123456",
      licensePlate = "01429-122-16",
      vehicleModel = "Peugeot 308 II (1.6 BlueHDi 120)",
      clientName = "Sofiane Benali",
      clientPhone = "0550 12 34 56",
      inspectorName = "Mourad (Chef d'Atelier)",
      inspectionType = "Entrée Réparation / Prise en charge",
      dateFormatted = "17 Oct 2026 09:30",
      mileageKm = "142 850",
      fuelPercent = 65,
      exteriorCleanliness = "Correcte",
      interiorCleanliness = "Propre",
      markers = listOf(
        DamageMarker(
          id = "DMG-01",
          viewAngle = InspectionViewAngle.PROFIL_GAUCHE,
          xRatio = 0.28f,
          yRatio = 0.48f,
          damageType = DamageType.RAYURE,
          severity = DamageSeverity.MOYEN,
          description = "Rayure superficielle sur aile avant gauche (environ 12 cm)",
          photoLabel = "Photo_Aile_AVG.jpg",
          hasPhotoAttached = true,
          timestampFormatted = "09:35"
        ),
        DamageMarker(
          id = "DMG-02",
          viewAngle = InspectionViewAngle.AVANT,
          xRatio = 0.72f,
          yRatio = 0.65f,
          damageType = DamageType.FISSURE,
          severity = DamageSeverity.CRITIQUE,
          description = "Fissure pare-chocs inférieur droit suite contact trottoir",
          photoLabel = "Photo_PC_AVD.jpg",
          hasPhotoAttached = true,
          timestampFormatted = "09:38"
        ),
        DamageMarker(
          id = "DMG-03",
          viewAngle = InspectionViewAngle.HABITACLE,
          xRatio = 0.50f,
          yRatio = 0.35f,
          damageType = DamageType.VOYANT_TABLEAU,
          severity = DamageSeverity.CRITIQUE,
          description = "Voyant moteur orange Check Engine allumé au combiné",
          photoLabel = "Photo_TDB_CheckEngine.jpg",
          hasPhotoAttached = true,
          timestampFormatted = "09:41"
        )
      ),
      clientSignatureAccepted = true,
      clientSignatureDate = "17/10/2026 09:45",
      generalNotes = "Véhicule pris en charge avec 2 clés, carte grise et carnet d'entretien. Rayures constatées conjointement avec le client avant travaux."
    ),
    VehicleInspection(
      id = "INSP-002",
      inspectionNumber = "INSP-2026-0043",
      vin = "WVWZZZ5NZMW123456",
      licensePlate = "04291-120-16",
      vehicleModel = "Volkswagen Golf 7 (2.0 TDI 150)",
      clientName = "Yacine Mansouri",
      clientPhone = "0661 88 99 00",
      inspectorName = "Samir (Réceptionnaire)",
      inspectionType = "Restitution Après Travaux",
      dateFormatted = "16 Oct 2026 17:15",
      mileageKm = "88 400",
      fuelPercent = 40,
      exteriorCleanliness = "Lavé / Nettoyé",
      interiorCleanliness = "Impeccable",
      markers = listOf(
        DamageMarker(
          id = "DMG-04",
          viewAngle = InspectionViewAngle.ARRIERE,
          xRatio = 0.52f,
          yRatio = 0.70f,
          damageType = DamageType.BOSSE,
          severity = DamageSeverity.LEGER,
          description = "Légère bosse hayon sous le logo VW (antérieure à la réparation)",
          photoLabel = "Photo_Hayon.jpg",
          hasPhotoAttached = true,
          timestampFormatted = "17:18"
        )
      ),
      clientSignatureAccepted = true,
      clientSignatureDate = "16/10/2026 17:25",
      generalNotes = "Restitution effectuée après remplacement kit d'embrayage. Essai sur route validé avec le client."
    )
  )

  val sampleNotificationTemplates: List<MessageNotificationTemplate> = listOf(
    MessageNotificationTemplate(
      id = "TPL-01",
      type = NotificationTemplateType.DEVIS_PRET,
      title = "Devis Réparation Disponible",
      templateText = "Bonjour {CLIENT}, votre devis pour votre {VEHICULE} ({MATRICULE}) est prêt chez {GARAGE}. Montant total estimé : {MONTANT} DZD. Merci de nous confirmer votre accord pour lancer les travaux. Contact atelier : {TELEPHONE}."
    ),
    MessageNotificationTemplate(
      id = "TPL-02",
      type = NotificationTemplateType.VEHICULE_REPARE,
      title = "Véhicule Prêt à Récupérer",
      templateText = "Bonjour {CLIENT}, nous vous informons que les réparations sur votre {VEHICULE} ({MATRICULE}) sont terminées avec succès chez {GARAGE} ! Votre véhicule a été contrôlé et est prêt à être récupéré. Montant : {MONTANT} DZD. Horaires : 08h00 - 18h30. Tél : {TELEPHONE}."
    ),
    MessageNotificationTemplate(
      id = "TPL-03",
      type = NotificationTemplateType.PIECES_EN_STOCK,
      title = "Pièces Détachées Reçues",
      templateText = "Bonjour {CLIENT}, les pièces d'origine commandées pour votre {VEHICULE} ({MATRICULE}) sont bien arrivées en stock au magasin {GARAGE}. Votre intervention en atelier peut débuter dès aujourd'hui. Tél : {TELEPHONE}."
    ),
    MessageNotificationTemplate(
      id = "TPL-04",
      type = NotificationTemplateType.RAPPEL_ENTRETIEN,
      title = "Rappel Entretien Périodique",
      templateText = "Bonjour {CLIENT}, {GARAGE} vous rappelle que votre {VEHICULE} ({MATRICULE}) arrive à échéance pour sa vidange périodique et le contrôle des organes de sécurité. Prenez rendez-vous en 1 clic au {TELEPHONE} pour conserver votre garantie."
    ),
    MessageNotificationTemplate(
      id = "TPL-05",
      type = NotificationTemplateType.FACTURE_DISPONIBLE,
      title = "Facture & Paiement Validé",
      templateText = "Bonjour {CLIENT}, votre facture d'atelier N° {MONTANT} pour le véhicule {MATRICULE} est validée chez {GARAGE}. Le passeport d'entretien numérique certifié a été mis à jour avec le QR code officiel. Merci pour votre confiance !"
    ),
    MessageNotificationTemplate(
      id = "TPL-06",
      type = NotificationTemplateType.DIAGNOSTIC_AI_TERMINE,
      title = "Rapport Diagnostic Moteur MekanikAI",
      templateText = "Bonjour {CLIENT}, le diagnostic électronique et l'analyse acoustique de votre {VEHICULE} ({MATRICULE}) sont terminés chez {GARAGE}. Les codes défauts ont été relevés et la solution technique est identifiée. Tél : {TELEPHONE}."
    )
  )

  val sampleBarcodeInventory: List<BarcodeInventoryItem> = listOf(
    BarcodeInventoryItem(
      id = "BC-001",
      barcode = "4047024123456",
      oemReference = "0 451 103 336",
      partName = "Filtre à Huile Haute Efficacité",
      brand = "BOSCH",
      category = "Filtration",
      currentStock = 12,
      minAlertThreshold = 4,
      unitPurchaseCostDzd = 1250,
      unitSalePriceDzd = 1850,
      shelfLocation = "Rayon A1 - Bac 04",
      supplierName = "Sarl Auto Pièces Alger",
      compatibilityVehicles = listOf("Renault Clio 4 (1.5 dCi)", "Dacia Duster", "Nissan Qashqai 1.5 dCi"),
      lastRestockDate = "14/10/2026"
    ),
    BarcodeInventoryItem(
      id = "BC-002",
      barcode = "8020584123456",
      oemReference = "P 85 072",
      partName = "Jeu de 4 Plaquettes de Frein Avant",
      brand = "BREMBO",
      category = "Freinage",
      currentStock = 2, // Rupture !
      minAlertThreshold = 5,
      unitPurchaseCostDzd = 4800,
      unitSalePriceDzd = 6900,
      shelfLocation = "Rayon F3 - Étagère 2",
      supplierName = "Établissements Freins & Disques DZ",
      compatibilityVehicles = listOf("Volkswagen Golf 7", "Seat Leon 3", "Audi A3 8V", "Skoda Octavia"),
      lastRestockDate = "02/10/2026"
    ),
    BarcodeInventoryItem(
      id = "BC-003",
      barcode = "3276428263158",
      oemReference = "826315",
      partName = "Kit d'Embrayage Complet 3 Pièces",
      brand = "VALEO",
      category = "Embrayage",
      currentStock = 1, // Rupture !
      minAlertThreshold = 3,
      unitPurchaseCostDzd = 22500,
      unitSalePriceDzd = 31000,
      shelfLocation = "Rayon L1 - Sol Palettes",
      supplierName = "Comptoir Central Pièces Oran",
      compatibilityVehicles = listOf("Peugeot 208 1.6 HDi", "Citroën C3", "Peugeot 301 / C-Elysée"),
      lastRestockDate = "28/09/2026"
    ),
    BarcodeInventoryItem(
      id = "BC-004",
      barcode = "5414465123456",
      oemReference = "5557XS",
      partName = "Kit Courroie de Distribution Renforcée",
      brand = "GATES",
      category = "Distribution",
      currentStock = 6,
      minAlertThreshold = 3,
      unitPurchaseCostDzd = 8900,
      unitSalePriceDzd = 13500,
      shelfLocation = "Rayon D2 - Bac 11",
      supplierName = "Sarl Auto Pièces Alger",
      compatibilityVehicles = listOf("Renault Megane 3 (1.5 dCi)", "Dacia Logan", "Clio 3"),
      lastRestockDate = "10/10/2026"
    ),
    BarcodeInventoryItem(
      id = "BC-005",
      barcode = "3374650234567",
      oemReference = "8100 X-CLEAN",
      partName = "Huile Moteur Synthèse 5W-30 (Bidon 5L)",
      brand = "MOTUL",
      category = "Lubrifiants",
      currentStock = 18,
      minAlertThreshold = 6,
      unitPurchaseCostDzd = 5800,
      unitSalePriceDzd = 7900,
      shelfLocation = "Allée Centrale - Fûts & Bidons",
      supplierName = "Distributeur Officiel Lubrifiants DZ",
      compatibilityVehicles = listOf("Norme ACEA C3 / API SN", "VW 504.00/507.00", "BMW LL-04", "MB 229.51"),
      lastRestockDate = "15/10/2026"
    ),
    BarcodeInventoryItem(
      id = "BC-006",
      barcode = "4011558023456",
      oemReference = "WK 820/2",
      partName = "Filtre à Carburant Diesel Haute Pression",
      brand = "MANN-FILTER",
      category = "Filtration",
      currentStock = 3, // Seuil limite !
      minAlertThreshold = 3,
      unitPurchaseCostDzd = 3200,
      unitSalePriceDzd = 4600,
      shelfLocation = "Rayon A2 - Bac 08",
      supplierName = "Sarl Auto Pièces Alger",
      compatibilityVehicles = listOf("Mercedes-Benz Classe C (W204)", "Classe E 220 CDI", "GLK 220"),
      lastRestockDate = "05/10/2026"
    ),
    BarcodeInventoryItem(
      id = "BC-007",
      barcode = "087295149967",
      oemReference = "ILKAR7B11",
      partName = "Bougie d'Allumage Laser Iridium (x4)",
      brand = "NGK",
      category = "Allumage",
      currentStock = 16,
      minAlertThreshold = 6,
      unitPurchaseCostDzd = 5200,
      unitSalePriceDzd = 7400,
      shelfLocation = "Rayon E1 - Coffret Sécurisé",
      supplierName = "Comptoir Central Pièces Oran",
      compatibilityVehicles = listOf("Toyota Yaris 1.33 VVT-i", "Corolla", "Suzuki Swift 1.2"),
      lastRestockDate = "12/10/2026"
    ),
    BarcodeInventoryItem(
      id = "BC-008",
      barcode = "4013872123456",
      oemReference = "313 033",
      partName = "Paire d'Amortisseurs Avant Gaz Super Touring",
      brand = "SACHS",
      category = "Suspension",
      currentStock = 2, // Rupture !
      minAlertThreshold = 4,
      unitPurchaseCostDzd = 16500,
      unitSalePriceDzd = 23900,
      shelfLocation = "Rayon S2 - Palettes Longues",
      supplierName = "Établissements Freins & Disques DZ",
      compatibilityVehicles = listOf("Peugeot 308 II", "Citroën C4 II", "DS4"),
      lastRestockDate = "01/10/2026",
      rotationClass = AbcRotationClass.CLASS_B
    ),
    BarcodeInventoryItem(
      id = "BC-009",
      barcode = "6901234567890",
      oemReference = "F01-3501080",
      partName = "Jeu de Plaquettes de Frein Céramique Avant (Chery / Jetour)",
      brand = "CHERY OEM",
      category = "Freinage",
      currentStock = 8,
      minAlertThreshold = 3,
      maxTargetStock = 18,
      unitPurchaseCostDzd = 5200,
      unitSalePriceDzd = 7800,
      shelfLocation = "Rayon C1 - Zone SinoAuto",
      supplierName = "SinoAuto Parts Algérie (Bab Ezzouar)",
      compatibilityVehicles = listOf("Jetour Dashing 1.6 TGDI", "Chery Tiggo 7 Pro", "Omoda 5"),
      lastRestockDate = "16/10/2026",
      rotationClass = AbcRotationClass.CLASS_A
    ),
    BarcodeInventoryItem(
      id = "BC-010",
      barcode = "4027816398124",
      oemReference = "04L 121 011 N",
      partName = "Pompe à Eau Commutable Électronique + Joint",
      brand = "INA / CONTINENTAL",
      category = "Refroidissement",
      currentStock = 0, // En rupture totale !
      minAlertThreshold = 2,
      maxTargetStock = 8,
      unitPurchaseCostDzd = 11200,
      unitSalePriceDzd = 15800,
      shelfLocation = "Rayon D1 - Bac 02",
      supplierName = "Sarl Auto Pièces Alger",
      compatibilityVehicles = listOf("VW Tiguan II 2.0 TDI", "Golf 7 GTD", "Audi A4 B9", "Skoda Karoq"),
      lastRestockDate = "24/09/2026",
      rotationClass = AbcRotationClass.CLASS_A
    )
  )

  val sampleStockMovementLogs: List<StockMovementLog> = listOf(
    StockMovementLog(
      id = "MVT-2026-01",
      itemId = "BC-001",
      partName = "Filtre à Huile Haute Efficacité",
      oemReference = "0 451 103 336",
      barcode = "4047024123456",
      type = StockMovementType.ENTREE_ACHAT,
      quantity = 10,
      previousStock = 2,
      newStock = 12,
      unitValueDzd = 1250,
      dateFormatted = "Aujourd'hui • 08:45",
      operatorName = "Kamel M. (Chef Magasinier)",
      destinationRef = "BL-FOURN-2026-412 • Sarl Auto Pièces Alger"
    ),
    StockMovementLog(
      id = "MVT-2026-02",
      itemId = "BC-002",
      partName = "Jeu de 4 Plaquettes de Frein Avant",
      oemReference = "P 85 072",
      barcode = "8020584123456",
      type = StockMovementType.SORTIE_ATELIER,
      quantity = 2,
      previousStock = 4,
      newStock = 2,
      unitValueDzd = 6900,
      dateFormatted = "Aujourd'hui • 10:15",
      operatorName = "Karim B. (Maître Mécanicien)",
      destinationRef = "OR-2026-089 • VW Tiguan 2.0 TDI (Pont N°1)"
    ),
    StockMovementLog(
      id = "MVT-2026-03",
      itemId = "BC-005",
      partName = "Huile Moteur Synthèse 5W-30 (Bidon 5L)",
      oemReference = "8100 X-CLEAN",
      barcode = "3374650234567",
      type = StockMovementType.SORTIE_VENTE_COMPTOIR,
      quantity = 3,
      previousStock = 21,
      newStock = 18,
      unitValueDzd = 7900,
      dateFormatted = "Hier • 16:30",
      operatorName = "Sofiane R. (Comptoir Pièces)",
      destinationRef = "FAC-COMPTOIR-884 • Client Particulier"
    ),
    StockMovementLog(
      id = "MVT-2026-04",
      itemId = "BC-010",
      partName = "Pompe à Eau Commutable Électronique + Joint",
      oemReference = "04L 121 011 N",
      barcode = "4027816398124",
      type = StockMovementType.SORTIE_ATELIER,
      quantity = 1,
      previousStock = 1,
      newStock = 0,
      unitValueDzd = 15800,
      dateFormatted = "Hier • 14:10",
      operatorName = "Karim B. (Maître Mécanicien)",
      destinationRef = "OR-2026-087 • Audi A4 2.0 TDI"
    )
  )

  val sampleSupplierPurchaseOrders: List<SupplierPurchaseOrder> = listOf(
    SupplierPurchaseOrder(
      id = "PO-2026-01",
      orderNumber = "BC-FOURN-2026-108",
      supplierName = "Établissements Freins & Disques DZ",
      createdAt = "18/10/2026",
      expectedDeliveryDate = "20/10/2026 (24h)",
      status = SupplierOrderStatus.EN_TRANSIT,
      lines = listOf(
        SupplierOrderLine(
          itemId = "BC-002",
          oemReference = "P 85 072",
          partName = "Jeu de 4 Plaquettes de Frein Avant",
          brand = "BREMBO",
          quantityOrdered = 10,
          unitPurchaseCostDzd = 4800
        ),
        SupplierOrderLine(
          itemId = "BC-008",
          oemReference = "313 033",
          partName = "Paire d'Amortisseurs Avant Gaz Super Touring",
          brand = "SACHS",
          quantityOrdered = 6,
          unitPurchaseCostDzd = 16500
        )
      ),
      notes = "Réapprovisionnement prioritaire Freinage & Suspension"
    ),
    SupplierPurchaseOrder(
      id = "PO-2026-02",
      orderNumber = "BC-FOURN-2026-109",
      supplierName = "Sarl Auto Pièces Alger",
      createdAt = "19/10/2026",
      expectedDeliveryDate = "21/10/2026",
      status = SupplierOrderStatus.ENVOYEE,
      lines = listOf(
        SupplierOrderLine(
          itemId = "BC-010",
          oemReference = "04L 121 011 N",
          partName = "Pompe à Eau Commutable Électronique + Joint",
          brand = "INA / CONTINENTAL",
          quantityOrdered = 6,
          unitPurchaseCostDzd = 11200
        ),
        SupplierOrderLine(
          itemId = "BC-006",
          oemReference = "WK 820/2",
          partName = "Filtre à Carburant Diesel Haute Pression",
          brand = "MANN-FILTER",
          quantityOrdered = 8,
          unitPurchaseCostDzd = 3200
        )
      ),
      notes = "Urgence rupture pompe à eau 2.0 TDI DFGA"
    )
  )

  val sampleScannedPurchaseInvoices: List<ScannedPurchaseInvoice> = listOf(
    ScannedPurchaseInvoice(
      id = "INV-SCAN-01",
      invoiceNumber = "BA-2026-8841",
      supplierName = "Sarl Auto Pièces Alger (Bosch • Brembo • Mann)",
      invoiceDate = "19/10/2026 • 11:30",
      isIntegrated = false,
      lines = listOf(
        ScannedPurchaseInvoiceLine(
          id = "LINE-8841-1",
          matchedInventoryItemId = "BC-002",
          oemReference = "P 85 072",
          barcode = "8020584123456",
          partName = "Jeu de 4 Plaquettes de Frein Avant",
          brand = "BREMBO",
          category = "Freinage",
          quantityReceived = 8,
          previousStock = 2,
          previousPurchaseCostDzd = 4800,
          scannedPurchaseCostDzd = 5100, // Légère hausse fournisseur -> propose de garder ou changer le prix de vente
          previousSalePriceDzd = 6900,
          priceChoice = SalePriceValidationChoice.KEEP_SAME_PRICE,
          customSalePriceDzd = 7400,
          assignedRayon = "Rayon B1",
          assignedEtagere = "Étagère E2",
          assignedBac = "Bac B04"
        ),
        ScannedPurchaseInvoiceLine(
          id = "LINE-8841-2",
          matchedInventoryItemId = "BC-001",
          oemReference = "HU 7020 z",
          barcode = "4011558012345",
          partName = "Filtre à Huile Cartouche 2.0 TDI",
          brand = "MANN-FILTER",
          category = "Filtration",
          quantityReceived = 12,
          previousStock = 14,
          previousPurchaseCostDzd = 1250,
          scannedPurchaseCostDzd = 1250,
          previousSalePriceDzd = 1950,
          priceChoice = SalePriceValidationChoice.KEEP_SAME_PRICE,
          customSalePriceDzd = 1950,
          assignedRayon = "Rayon A1",
          assignedEtagere = "Étagère E1",
          assignedBac = "Bac B02"
        ),
        ScannedPurchaseInvoiceLine(
          id = "LINE-8841-3",
          matchedInventoryItemId = "BC-010",
          oemReference = "04L 121 011 N",
          barcode = "4027816398124",
          partName = "Pompe à Eau Commutable Électronique + Joint",
          brand = "INA / CONTINENTAL",
          category = "Refroidissement",
          quantityReceived = 4,
          previousStock = 0,
          previousPurchaseCostDzd = 11200,
          scannedPurchaseCostDzd = 11600,
          previousSalePriceDzd = 15800,
          priceChoice = SalePriceValidationChoice.CHANGE_SALE_PRICE,
          customSalePriceDzd = 16500,
          assignedRayon = "Rayon M1",
          assignedEtagere = "Étagère E3",
          assignedBac = "Bac B06"
        ),
        ScannedPurchaseInvoiceLine(
          id = "LINE-8841-4",
          matchedInventoryItemId = "BC-003",
          oemReference = "0 281 002 976",
          barcode = "4047024987654",
          partName = "Capteur Pression Différentielle FAP G450",
          brand = "BOSCH",
          category = "Électronique & Capteurs",
          quantityReceived = 5,
          previousStock = 5,
          previousPurchaseCostDzd = 5400,
          scannedPurchaseCostDzd = 5400,
          previousSalePriceDzd = 7800,
          priceChoice = SalePriceValidationChoice.KEEP_SAME_PRICE,
          customSalePriceDzd = 7800,
          assignedRayon = "Rayon E1",
          assignedEtagere = "Étagère E2",
          assignedBac = "Bac B09"
        )
      )
    ),
    ScannedPurchaseInvoice(
      id = "INV-SCAN-02",
      invoiceNumber = "BA-2026-8892",
      supplierName = "Chery & Jetour Parts Algérie (Arrivage 1.5 / 1.6 TGDI)",
      invoiceDate = "19/10/2026 • 14:10",
      isIntegrated = false,
      lines = listOf(
        ScannedPurchaseInvoiceLine(
          id = "LINE-8892-1",
          matchedInventoryItemId = "BC-009",
          oemReference = "F4J16-1012010",
          barcode = "6971234560091",
          partName = "Filtre à Huile Haute Pression Jetour / Chery 1.6 TGDI",
          brand = "CHERY ACTECO OEM",
          category = "Filtration",
          quantityReceived = 15,
          previousStock = 9,
          previousPurchaseCostDzd = 1400,
          scannedPurchaseCostDzd = 1350,
          previousSalePriceDzd = 2400,
          priceChoice = SalePriceValidationChoice.KEEP_SAME_PRICE,
          customSalePriceDzd = 2400,
          assignedRayon = "Rayon C1",
          assignedEtagere = "Étagère E2",
          assignedBac = "Bac B03"
        ),
        ScannedPurchaseInvoiceLine(
          id = "LINE-8892-2",
          matchedInventoryItemId = null, // Nouvelle pièce détectée sur le bon d'achat -> création auto dans le Rayon/Étagère/Bac
          oemReference = "SQRE4T15C-3705110",
          barcode = "6971234560884",
          partName = "Bobine d'Allumage Crayon Chery Tiggo 7 Pro 1.5 TGDI",
          brand = "CHERY ACTECO OEM",
          category = "Électronique & Capteurs",
          quantityReceived = 6,
          previousStock = 0,
          previousPurchaseCostDzd = 4600,
          scannedPurchaseCostDzd = 4600,
          previousSalePriceDzd = 6900,
          priceChoice = SalePriceValidationChoice.KEEP_SAME_PRICE,
          customSalePriceDzd = 6900,
          assignedRayon = "Rayon C1",
          assignedEtagere = "Étagère E4",
          assignedBac = "Bac B07"
        ),
        ScannedPurchaseInvoiceLine(
          id = "LINE-8892-3",
          matchedInventoryItemId = null,
          oemReference = "T15-3501080BA",
          barcode = "6971234560912",
          partName = "Jeu Plaquettes Frein Avant Céramique Jetour Dashing",
          brand = "JETOUR GENUINE",
          category = "Freinage",
          quantityReceived = 10,
          previousStock = 0,
          previousPurchaseCostDzd = 5200,
          scannedPurchaseCostDzd = 5200,
          previousSalePriceDzd = 7800,
          priceChoice = SalePriceValidationChoice.CHANGE_SALE_PRICE,
          customSalePriceDzd = 8200,
          assignedRayon = "Rayon C1",
          assignedEtagere = "Étagère E1",
          assignedBac = "Bac B01"
        )
      )
    ),
    ScannedPurchaseInvoice(
      id = "INV-SCAN-03",
      invoiceNumber = "BA-2026-8905",
      supplierName = "Établissements Freins & Disques DZ (Oran)",
      invoiceDate = "19/10/2026 • 15:00",
      isIntegrated = false,
      lines = listOf(
        ScannedPurchaseInvoiceLine(
          id = "LINE-8905-1",
          matchedInventoryItemId = "BC-011",
          oemReference = "DF 4464 S",
          barcode = "3322937891234",
          partName = "Jeu de 2 Disques de Frein Avant Ventilés Ø 312mm",
          brand = "TRW",
          category = "Freinage",
          quantityReceived = 6,
          previousStock = 5,
          previousPurchaseCostDzd = 14500,
          scannedPurchaseCostDzd = 14900,
          previousSalePriceDzd = 19800,
          priceChoice = SalePriceValidationChoice.CHANGE_SALE_PRICE,
          customSalePriceDzd = 20500,
          assignedRayon = "Rayon S2",
          assignedEtagere = "Étagère E1",
          assignedBac = "Bac B02 (Palettes)"
        ),
        ScannedPurchaseInvoiceLine(
          id = "LINE-8905-2",
          matchedInventoryItemId = "BC-004",
          oemReference = "CT 1168 K1",
          barcode = "4010858567890",
          partName = "Kit Distribution Complet + Galets Tendeurs",
          brand = "CONTINENTAL",
          category = "Distribution",
          quantityReceived = 5,
          previousStock = 2,
          previousPurchaseCostDzd = 18500,
          scannedPurchaseCostDzd = 18500,
          previousSalePriceDzd = 24900,
          priceChoice = SalePriceValidationChoice.KEEP_SAME_PRICE,
          customSalePriceDzd = 24900,
          assignedRayon = "Rayon M1",
          assignedEtagere = "Étagère E2",
          assignedBac = "Bac B05"
        )
      )
    )
  )

  val samplePrintedThermalLabels: List<PrintedThermalLabel> = listOf(
    PrintedThermalLabel(
      id = "LBL-2026-01",
      invoiceNumber = "BA-2026-8810",
      oemReference = "HU 7020 z",
      barcode = "4011558012345",
      partName = "Filtre à Huile Cartouche 2.0 TDI",
      brand = "MANN-FILTER",
      category = "Filtration",
      rayon = "Rayon A1",
      etagere = "Étagère E1",
      bac = "Bac B02",
      validatedSalePriceDzd = 1950,
      copiesToPrint = 10,
      isPrinted = true,
      printedTimestamp = "Aujourd'hui • 08:48 (Imprimante Zebra ZD421)"
    )
  )

  val sampleLaborOperations: List<LaborStandardOperation> = listOf(
    LaborStandardOperation(
      id = "MO-DIST-01",
      category = LaborCategory.DISTRIBUTION,
      oemCode = "OEM-OP-1310",
      title = "Remplacement Kit Courroie Distribution + Pompe à Eau",
      standardHours = 3.50,
      difficultyStars = 4,
      engineCompatibility = "1.5 dCi, 1.6 HDi, 2.0 TDI (Moteurs 4 cyl transversaux)",
      standardToolsRequired = listOf("Pige de calage vilebrequin", "Pige arbre à cames", "Clé dynamométrique"),
      technicalNotes = "Comprend la vidange du circuit de refroidissement, purge sous pression et contrôle des galets tendeurs."
    ),
    LaborStandardOperation(
      id = "MO-DIST-02",
      category = LaborCategory.DISTRIBUTION,
      oemCode = "OEM-OP-1315",
      title = "Remplacement Courroie d'Accessoires & Galet Tendeur",
      standardHours = 0.80,
      difficultyStars = 2,
      engineCompatibility = "Toutes motorisations Diesel / Essence",
      standardToolsRequired = listOf("Levier détendeur automatique"),
      technicalNotes = "Vérification de la poulie damper et de la roue libre d'alternateur."
    ),
    LaborStandardOperation(
      id = "MO-FREIN-01",
      category = LaborCategory.FREINAGE,
      oemCode = "OEM-OP-4210",
      title = "Remplacement Plaquettes de Frein Avant",
      standardHours = 0.70,
      difficultyStars = 2,
      engineCompatibility = "Tous véhicules de tourisme",
      standardToolsRequired = listOf("Repousse-piston pneumatique", "Nettoyant freins", "Graisse cuivrée"),
      technicalNotes = "Contrôle d'épaisseur des disques et état des soufflets de coulisseaux d'étrier."
    ),
    LaborStandardOperation(
      id = "MO-FREIN-02",
      category = LaborCategory.FREINAGE,
      oemCode = "OEM-OP-4215",
      title = "Remplacement Disques & Plaquettes de Frein Avant",
      standardHours = 1.30,
      difficultyStars = 3,
      engineCompatibility = "Tous véhicules de tourisme",
      standardToolsRequired = listOf("Clé Torx étrier", "Brosse métallique moyeu", "Comparateur de voile"),
      technicalNotes = "Brossage soigné du moyeu et contrôle du voile latéral (< 0.05 mm)."
    ),
    LaborStandardOperation(
      id = "MO-FREIN-03",
      category = LaborCategory.FREINAGE,
      oemCode = "OEM-OP-4280",
      title = "Purge Complète Circuit de Freinage & Remplacement Liquide DOT 4",
      standardHours = 0.70,
      difficultyStars = 2,
      engineCompatibility = "Tous véhicules avec centrale ABS/ESP",
      standardToolsRequired = listOf("Purgeur sous pression 2 bars", "Outil de diagnostic pour commande électrovannes ABS"),
      technicalNotes = "Purge dans l'ordre constructeur (ARD, ARG, AVD, AVG)."
    ),
    LaborStandardOperation(
      id = "MO-EMB-01",
      category = LaborCategory.EMBRAYAGE_TRANSMISSION,
      oemCode = "OEM-OP-2110",
      title = "Dépose / Repose Boîte de Vitesses & Remplacement Kit Embrayage",
      standardHours = 4.60,
      difficultyStars = 4,
      engineCompatibility = "Boîtes manuelles 5 et 6 vitesses (Traction)",
      standardToolsRequired = listOf("Vérin de fosse", "Centreur d'embrayage", "Pont élévateur"),
      technicalNotes = "Contrôle impératif du jeu axiale et radial du volant moteur bi-masse (DVF)."
    ),
    LaborStandardOperation(
      id = "MO-EMB-02",
      category = LaborCategory.EMBRAYAGE_TRANSMISSION,
      oemCode = "OEM-OP-2125",
      title = "Remplacement Volant Moteur Bi-Masse (DVF) + Butée Hydraulique",
      standardHours = 5.30,
      difficultyStars = 5,
      engineCompatibility = "Turbo Diesel HDi, TDI, dCi, CRDi",
      standardToolsRequired = listOf("Outil de blocage volant moteur", "Vis de fixation volant neuves"),
      technicalNotes = "Serrage au couple préconisé + serrage angulaire obligatoire."
    ),
    LaborStandardOperation(
      id = "MO-SUSP-01",
      category = LaborCategory.SUSPENSION_DIRECTION,
      oemCode = "OEM-OP-5110",
      title = "Remplacement Paire d'Amortisseurs Avant + Coupelles",
      standardHours = 2.20,
      difficultyStars = 3,
      engineCompatibility = "Trains avant type Pseudo-MacPherson",
      standardToolsRequired = listOf("Compresseur de ressorts hydraulique homologué", "Douille débouchée pour tige"),
      technicalNotes = "Contrôle obligatoire des biellettes de barre stabilisatrice."
    ),
    LaborStandardOperation(
      id = "MO-SUSP-02",
      category = LaborCategory.SUSPENSION_DIRECTION,
      oemCode = "OEM-OP-5190",
      title = "Contrôle & Réglage Géométrie Train Roulant 3D (Parallélisme)",
      standardHours = 0.90,
      difficultyStars = 2,
      engineCompatibility = "Tous véhicules",
      standardToolsRequired = listOf("Banc laser de géométrie 3D", "Presse-pédale de frein"),
      technicalNotes = "Équilibrage de la pression des 4 pneus et alignement centré du volant."
    ),
    LaborStandardOperation(
      id = "MO-MOT-01",
      category = LaborCategory.MOTEUR_CULASSE,
      oemCode = "OEM-OP-1140",
      title = "Dépose / Repose & Contrôle des 4 Injecteurs Common Rail",
      standardHours = 2.60,
      difficultyStars = 4,
      engineCompatibility = "Moteurs Diesel Injection Directe Haute Pression",
      standardToolsRequired = listOf("Extracteur à inertie injecteurs", "Fraise de nettoyage puits injecteurs", "Joints pare-feu neufs"),
      technicalNotes = "Reprogrammation des codes IMA / codes de calibration dans le calculateur moteur."
    ),
    LaborStandardOperation(
      id = "MO-MOT-02",
      category = LaborCategory.MOTEUR_CULASSE,
      oemCode = "OEM-OP-1180",
      title = "Remplacement Turbocompresseur & Rinçage Circuit Graissage",
      standardHours = 4.20,
      difficultyStars = 4,
      engineCompatibility = "Turbo Diesel & Turbo Essence",
      standardToolsRequired = listOf("Manomètre de pression d'huile", "Durite alimentation huile neuve", "Crépine carter"),
      technicalNotes = "Démontage impératif du carter d'huile pour nettoyage de la crépine et amorçage manuel du palier turbo."
    ),
    LaborStandardOperation(
      id = "MO-ECH-01",
      category = LaborCategory.ECHAPPEMENT_FAP,
      oemCode = "OEM-OP-1830",
      title = "Dépose, Nettoyage Professionnel Machine & Régénération FAP",
      standardHours = 3.00,
      difficultyStars = 3,
      engineCompatibility = "Diesel Euro 5 et Euro 6 (avec FAP)",
      standardToolsRequired = listOf("Station de nettoyage hydrodynamique FAP", "Valise diag réinitialisation cendres"),
      technicalNotes = "Remise à zéro des valeurs d'adaptation de pression différentielle dans l'ECU."
    ),
    LaborStandardOperation(
      id = "MO-CLIM-01",
      category = LaborCategory.CLIMATISATION,
      oemCode = "OEM-OP-6410",
      title = "Entretien Climatisation, Tirage au Vide & Recharge Gaz R134a",
      standardHours = 1.00,
      difficultyStars = 2,
      engineCompatibility = "Tous véhicules équipés A/C",
      standardToolsRequired = listOf("Station automatique de recharge de climatisation", "Traceur UV"),
      technicalNotes = "Test d'étanchéité au vide 20 minutes et injection d'huile compresseur PAG."
    ),
    LaborStandardOperation(
      id = "MO-ELEC-01",
      category = LaborCategory.ELECTRICITE_DIAGNOSTIC,
      oemCode = "OEM-OP-0110",
      title = "Diagnostic Électronique Approfondi, Scan Calculateurs & Effacement Défauts",
      standardHours = 0.80,
      difficultyStars = 3,
      engineCompatibility = "Toutes marques & protocoles OBD-II / CAN",
      standardToolsRequired = listOf("Valise constructeur ou outil multi-marques Pro", "Boîtier de maintien de charge batterie"),
      technicalNotes = "Fourniture du rapport complet avant/après effacement avec freeze frames."
    )
  )

  // ==========================================================================
  // 1. PLANNING VISUEL DES PONTS & CHARGE ATELIER
  // ==========================================================================
  val sampleWorkshopBays: List<WorkshopBayItem> = listOf(
    WorkshopBayItem(
      id = "BAY-01",
      bayNumber = 1,
      bayName = "Pont Élévateur 1 (Rapide)",
      bayType = WorkshopBayType.PONT_2_COLONNES,
      status = WorkshopBayStatus.EN_COURS,
      assignedMechanic = "Karim B. (Maître Mécanicien)",
      mechanicSpecialty = "Distribution & Moteur",
      currentVehicleModel = "Volkswagen Tiguan 2.0 TDI",
      currentLicensePlate = "01482-121-16",
      repairOrderNumber = "OR-2026-089",
      operationTitle = "Remplacement Kit Distribution + Pompe à Eau",
      elapsedMinutes = 135,
      estimatedMinutes = 210,
      isTimerRunning = true
    ),
    WorkshopBayItem(
      id = "BAY-02",
      bayNumber = 2,
      bayName = "Pont Élévateur 2 (Lourd)",
      bayType = WorkshopBayType.PONT_4_COLONNES,
      status = WorkshopBayStatus.EN_ATTENTE_PIECE,
      assignedMechanic = "Yacine M. (Technicien Boîte)",
      mechanicSpecialty = "Embrayage & Transmission",
      currentVehicleModel = "Peugeot 308 1.6 BlueHDi",
      currentLicensePlate = "09321-118-16",
      repairOrderNumber = "OR-2026-091",
      operationTitle = "Kit Embrayage + Volant Moteur Bi-Masse",
      elapsedMinutes = 160,
      estimatedMinutes = 270,
      isTimerRunning = false
    ),
    WorkshopBayItem(
      id = "BAY-03",
      bayNumber = 3,
      bayName = "Baie Géométrie 3D & Freins",
      bayType = WorkshopBayType.BAIE_GEOMETRIE_3D,
      status = WorkshopBayStatus.LIBRE,
      assignedMechanic = "Sofiane R. (Train Roulant)",
      mechanicSpecialty = "Suspension, Freins & Géométrie 3D",
      currentVehicleModel = null,
      currentLicensePlate = null,
      repairOrderNumber = null,
      operationTitle = null,
      elapsedMinutes = 0,
      estimatedMinutes = 60,
      isTimerRunning = false
    ),
    WorkshopBayItem(
      id = "BAY-04",
      bayNumber = 4,
      bayName = "Baie Diagnostic & Hybride/EV",
      bayType = WorkshopBayType.BAIE_DIAG_ELECTRIQUE,
      status = WorkshopBayStatus.CONTROLE_QUALITE,
      assignedMechanic = "Amine K. (Électronicien Diag)",
      mechanicSpecialty = "Calculateurs, FAP & MekanikAI",
      currentVehicleModel = "Renault Clio 5 1.5 Blue dCi",
      currentLicensePlate = "11204-123-16",
      repairOrderNumber = "OR-2026-092",
      operationTitle = "Régénération FAP & Calibrage Injecteurs",
      elapsedMinutes = 80,
      estimatedMinutes = 90,
      isTimerRunning = true
    )
  )

  val sampleWaitingVehicles: List<WaitingQueueVehicle> = listOf(
    WaitingQueueVehicle(
      id = "WAIT-01",
      repairOrderNumber = "OR-2026-094",
      vehicleModel = "Seat Ibiza FR 1.6 TDI",
      licensePlate = "05512-119-16",
      clientName = "Mehdi Khelifi",
      operationTitle = "Disques + Plaquettes Avant Brembo & Purge DOT4",
      estimatedMinutes = 75,
      priorityLevel = "URGENT",
      recommendedBayType = WorkshopBayType.BAIE_GEOMETRIE_3D
    ),
    WaitingQueueVehicle(
      id = "WAIT-02",
      repairOrderNumber = "OR-2026-095",
      vehicleModel = "Dacia Duster 1.5 dCi 4x4",
      licensePlate = "07841-120-31",
      clientName = "Flotte Sonatrach / Leasing",
      operationTitle = "Remplacement Paire Amortisseurs Avant + Parallélisme",
      estimatedMinutes = 120,
      priorityLevel = "FLOTTE",
      recommendedBayType = WorkshopBayType.PONT_2_COLONNES
    )
  )

  // ==========================================================================
  // 2. SUIVI DES GARANTIES PIÈCES & RETOURS SAV / RMA
  // ==========================================================================
  val samplePartWarranties: List<PartWarrantyRecord> = listOf(
    PartWarrantyRecord(
      id = "WAR-01",
      warrantyNumber = "GAR-2026-8841",
      partName = "Kit d'Embrayage + Volant Bi-Masse",
      oemReference = "835035 / VALEO",
      brand = "VALEO ORIGIN",
      supplierName = "Sarl Auto Pièces Alger",
      vehicleModel = "Peugeot 308 1.6 HDi",
      licensePlate = "01429-122-16",
      clientName = "Sofiane Benali",
      installationDate = "12/04/2026",
      expirationDate = "12/04/2027",
      durationMonths = 12,
      daysRemaining = 177,
      mileageAtInstallKm = 132000,
      maxWarrantyMileageKm = 172000,
      purchasePriceDzd = 46500,
      status = WarrantyStatus.ACTIVE
    ),
    PartWarrantyRecord(
      id = "WAR-02",
      warrantyNumber = "GAR-2025-4109",
      partName = "Pompe à Eau Haute Performance + Kit Distri",
      oemReference = "VKMC 01250",
      brand = "SKF",
      supplierName = "Comptoir Central Pièces Oran",
      vehicleModel = "Volkswagen Golf 7 2.0 TDI",
      licensePlate = "04291-120-16",
      clientName = "Yacine Mansouri",
      installationDate = "05/11/2025",
      expirationDate = "05/11/2026",
      durationMonths = 12,
      daysRemaining = 19,
      mileageAtInstallKm = 68000,
      maxWarrantyMileageKm = 118000,
      purchasePriceDzd = 18900,
      status = WarrantyStatus.EXPIRE_BIENTOT
    ),
    PartWarrantyRecord(
      id = "WAR-03",
      warrantyNumber = "GAR-2026-9012",
      partName = "Alternateur Piloté 150A Start/Stop",
      oemReference = "0 125 711 089",
      brand = "BOSCH",
      supplierName = "Distributeur Officiel Bosch DZ",
      vehicleModel = "Renault Clio 4 1.5 dCi",
      licensePlate = "12984-114-16",
      clientName = "Kamel Touati",
      installationDate = "10/08/2026",
      expirationDate = "10/08/2028",
      durationMonths = 24,
      daysRemaining = 662,
      mileageAtInstallKm = 110500,
      maxWarrantyMileageKm = 160500,
      purchasePriceDzd = 34000,
      status = WarrantyStatus.RMA_EN_COURS,
      rmaClaimReason = "Tension de charge instable (12.1V) et voyant batterie intermittent à chaud",
      rmaTrackingCode = "RMA-BOSCH-2026-409"
    )
  )

  // ==========================================================================
  // 3. PORTAIL CLIENT WEB / QR & SIGNATURE DEVIS À DISTANCE
  // ==========================================================================
  val sampleRemoteClientQuote = RemoteClientQuoteSession(
    id = "RMT-2026-01",
    quoteNumber = "DEV-2026-1048",
    shareableUrl = "https://mekanik.pro/suivi/DEV-2026-1048",
    clientName = "Sofiane Benali",
    clientPhone = "0550 12 34 56",
    vehicleModel = "Peugeot 308 II (1.6 BlueHDi 120)",
    licensePlate = "01429-122-16",
    currentStepIndex = 2, // En attente d'accord devis
    mandatoryTotalDzd = 24500,
    optionalItems = listOf(
      OptionalQuoteLineItem(
        id = "OPT-1",
        title = "Purge complète liquide de frein DOT 4 Bosch",
        recommendationReason = "Taux d'humidité mesuré à 3.2% (seuil max 3%)",
        priceDzd = 2800,
        isSelectedByClient = true
      ),
      OptionalQuoteLineItem(
        id = "OPT-2",
        title = "Traitement antibactérien & Filtre habitacle charbon actif",
        recommendationReason = "Filtre encrassé constaté lors de l'inspection 360°",
        priceDzd = 3400,
        isSelectedByClient = false
      ),
      OptionalQuoteLineItem(
        id = "OPT-3",
        title = "Balais d'essuie-glace avant Bosch Aerotwin",
        recommendationReason = "Caoutchouc fissuré côté conducteur",
        priceDzd = 4200,
        isSelectedByClient = false
      )
    ),
    isSignedRemotely = false,
    signatureTimestamp = null
  )

  // ==========================================================================
  // 4. MODE HORS-LIGNE INTELLIGENT & SYNCHRONISATION AUTO
  // ==========================================================================
  val samplePendingSyncActions: List<PendingSyncAction> = listOf(
    PendingSyncAction(
      id = "SYNC-01",
      type = SyncItemType.INSPECTION_360,
      title = "Inspection 360° Peugeot 308 (3 photos HD + Signature)",
      timestamp = "Il y a 14 min (Sous-sol Atelier)",
      payloadSizeKb = 1840,
      isSynced = false
    ),
    PendingSyncAction(
      id = "SYNC-02",
      type = SyncItemType.MOUVEMENT_STOCK,
      title = "Sortie Stock Magasin : 2x Plaquettes Brembo P85072",
      timestamp = "Il y a 9 min",
      payloadSizeKb = 42,
      isSynced = false
    ),
    PendingSyncAction(
      id = "SYNC-03",
      type = SyncItemType.DEVIS_OR,
      title = "Devis OR-2026-094 (Seat Ibiza FR - 18 900 DZD)",
      timestamp = "Il y a 4 min",
      payloadSizeKb = 128,
      isSynced = false
    )
  )

  val sampleOfflineCachedPacks: List<OfflineCachedPack> = listOf(
    OfflineCachedPack(
      id = "PACK-01",
      title = "Catalogue Pièces & Équivalences OEM (Top 50 Modèles DZ)",
      category = "Catalogue Hors-Ligne",
      itemCount = 14500,
      storageSizeMb = 48.5,
      lastUpdated = "Aujourd'hui 08:00",
      isDownloaded = true
    ),
    OfflineCachedPack(
      id = "PACK-02",
      title = "Couples de Serrage, Calages Distribution & Schémas Moteur",
      category = "Documentation Technique",
      itemCount = 620,
      storageSizeMb = 32.0,
      lastUpdated = "Hier 19:30",
      isDownloaded = true
    ),
    OfflineCachedPack(
      id = "PACK-03",
      title = "Base Codes Défauts OBD-II (DTC P0xxx / P2xxx) + Causes IA",
      category = "Diagnostic MekanikAI",
      itemCount = 4800,
      storageSizeMb = 14.2,
      lastUpdated = "15 Oct 2026",
      isDownloaded = true
    ),
    OfflineCachedPack(
      id = "PACK-04",
      title = "Barèmes des Temps Constructeurs OEM (VAG / PSA / Renault)",
      category = "Chiffrage Atelier",
      itemCount = 1250,
      storageSizeMb = 9.8,
      lastUpdated = "Non téléchargé",
      isDownloaded = false
    )
  )

  // ==========================================================================
  // 5. COMPARATEUR MULTI-FOURNISSEURS B2B & SUIVI LIVREUR
  // ==========================================================================
  val sampleSupplierComparisons: List<SupplierComparisonOffer> = listOf(
    SupplierComparisonOffer(
      id = "SUP-01",
      partName = "Kit Courroie Distribution + Pompe à Eau",
      oemReference = "04L 198 119 A / CT1168WP1",
      brand = "CONTITECH (Continental)",
      supplierName = "Sarl Auto Pièces Alger (Bab Ezzouar)",
      supplierCity = "Alger (4.2 km)",
      wholesalePriceHtDzd = 16400,
      recommendedRetailPriceDzd = 22500,
      availableStock = 8,
      deliverySlaLabel = "Coursier Moto 45 min",
      isBestPrice = false,
      isFastestDelivery = true,
      supplierRating = 4.9
    ),
    SupplierComparisonOffer(
      id = "SUP-02",
      partName = "Kit Courroie Distribution + Pompe à Eau",
      oemReference = "04L 198 119 A / KP15678XS",
      brand = "GATES POWERGRIP",
      supplierName = "EuroParts Grossiste Blida",
      supplierCity = "Blida (28 km)",
      wholesalePriceHtDzd = 14900,
      recommendedRetailPriceDzd = 21500,
      availableStock = 15,
      deliverySlaLabel = "Fourgon Navette 2h30",
      isBestPrice = true,
      isFastestDelivery = false,
      supplierRating = 4.8
    ),
    SupplierComparisonOffer(
      id = "SUP-03",
      partName = "Kit Courroie Distribution + Pompe à Eau",
      oemReference = "04L 198 119 A / VKMC 01259",
      brand = "SKF AQUAMAX",
      supplierName = "Distributeur Officiel Roulements DZ",
      supplierCity = "Alger - Oued Smar (6.8 km)",
      wholesalePriceHtDzd = 15800,
      recommendedRetailPriceDzd = 22000,
      availableStock = 5,
      deliverySlaLabel = "Express 1h15",
      isBestPrice = false,
      isFastestDelivery = false,
      supplierRating = 4.7
    ),
    SupplierComparisonOffer(
      id = "SUP-04",
      partName = "Kit Courroie Distribution + Pompe à Eau",
      oemReference = "04L 198 119 A (Origine VAG)",
      brand = "VOLKSWAGEN OEM",
      supplierName = "Comptoir Pièces d'Origine Hydra",
      supplierCity = "Alger (9.5 km)",
      wholesalePriceHtDzd = 23500,
      recommendedRetailPriceDzd = 31000,
      availableStock = 2,
      deliverySlaLabel = "Fourgon 2h00",
      isBestPrice = false,
      isFastestDelivery = false,
      supplierRating = 5.0
    )
  )

  val sampleActiveB2bOrders: List<ActiveB2bOrderTracking> = listOf(
    ActiveB2bOrderTracking(
      orderId = "CMD-B2B-704",
      supplierName = "Sarl Auto Pièces Alger",
      courierName = "Walid (Coursier Moto #14)",
      courierPhone = "0552 91 04 33",
      itemsCount = 3,
      totalHtDzd = 28400,
      etaMinutes = 18,
      status = B2bDeliveryStatus.COURSIER_EN_ROUTE
    )
  )

  // ==========================================================================
  // 6. CALCULATEUR DE COÛT DE REVIENT KILOMÉTRIQUE (TCO) & CARBURANT
  // ==========================================================================
  val sampleVehicleTcoSummaries: List<VehicleTcoSummary> = listOf(
    VehicleTcoSummary(
      vehicleId = "TCO-01",
      vehicleName = "Volkswagen Golf 7 (2.0 TDI 150)",
      licensePlate = "04291-120-16",
      annualDistanceKm = 24000,
      averageFuelLitersPer100Km = 5.8,
      totalFuelCostDzd = 68500,
      totalMaintenanceCostDzd = 74000,
      totalInsuranceCostDzd = 48000,
      totalTiresCostDzd = 36000,
      dailyRentalRateDzd = 8500,
      rentedDaysPerMonth = 21
    ),
    VehicleTcoSummary(
      vehicleId = "TCO-02",
      vehicleName = "Renault Clio 5 (1.5 Blue dCi)",
      licensePlate = "11204-123-16",
      annualDistanceKm = 32000,
      averageFuelLitersPer100Km = 4.6,
      totalFuelCostDzd = 59000,
      totalMaintenanceCostDzd = 52000,
      totalInsuranceCostDzd = 42000,
      totalTiresCostDzd = 28000,
      dailyRentalRateDzd = 6000,
      rentedDaysPerMonth = 24
    )
  )

  val sampleVehicleExpenses: List<VehicleExpenseEntry> = listOf(
    VehicleExpenseEntry(
      id = "EXP-01",
      vehicleId = "TCO-01",
      vehicleLabel = "VW Golf 7 2.0 TDI",
      category = VehicleExpenseCategory.ENTRETIEN_PIECES,
      title = "Vidange 5W30 Motul + 4 Filtres Bosch",
      date = "14 Oct 2026",
      mileageKm = 88400,
      amountDzd = 14800
    ),
    VehicleExpenseEntry(
      id = "EXP-02",
      vehicleId = "TCO-01",
      vehicleLabel = "VW Golf 7 2.0 TDI",
      category = VehicleExpenseCategory.CARBURANT,
      title = "Plein Gasoil Sans Soufre (Station Naftal Chéraga)",
      date = "16 Oct 2026",
      mileageKm = 88920,
      amountDzd = 1650,
      litersOrUnits = 52.0
    ),
    VehicleExpenseEntry(
      id = "EXP-03",
      vehicleId = "TCO-01",
      vehicleLabel = "VW Golf 7 2.0 TDI",
      category = VehicleExpenseCategory.PNEUMATIQUES,
      title = "2 Pneus Avant Michelin Primacy 4 + Équilibrage",
      date = "02 Oct 2026",
      mileageKm = 87100,
      amountDzd = 36000
    ),
    VehicleExpenseEntry(
      id = "EXP-04",
      vehicleId = "TCO-01",
      vehicleLabel = "VW Golf 7 2.0 TDI",
      category = VehicleExpenseCategory.ASSURANCE_TAXES,
      title = "Assurance Tous Risques Annuelle SAA",
      date = "15 Sep 2026",
      mileageKm = 85000,
      amountDzd = 48000
    )
  )

  // ==========================================================================
  // 7. CONSTAT AMIABLE DIGITAL & DÉCLARATION SINISTRE GUIDÉE
  // ==========================================================================
  val sampleAccidentReports: List<DigitalAccidentReport> = listOf(
    DigitalAccidentReport(
      id = "SIN-01",
      reportNumber = "CONSTAT-2026-0318",
      dateTimeFormatted = "15 Oct 2026 à 17:40",
      gpsLocation = "Rocade Sud d'Alger - Échangeur Ben Aknoun",
      driverAVehicle = "Peugeot 308 (1.6 BlueHDi)",
      driverAPlate = "01429-122-16",
      driverAInsurance = "SAA Assurance Algérie",
      driverAPolicyNumber = "POL-SAA-9981204",
      driverBName = "Nabil Boudiaf",
      driverBPhone = "0662 44 18 90",
      driverBVehicle = "Hyundai Tucson CRDi",
      driverBPlate = "08812-121-16",
      driverBInsurance = "Alliance Assurances",
      driverBPolicyNumber = "ALL-2026-44109",
      selectedCircumstancesA = listOf(AccidentCircumstance.EN_STATIONNEMENT),
      selectedCircumstancesB = listOf(AccidentCircumstance.HEURTAIT_ARRIERE, AccidentCircumstance.CHANGEAIT_DE_FILE),
      impactZoneDescription = "Choc arrière gauche (Pare-chocs + Feu arrière gauche fissuré)",
      attachedPhotosCount = 4,
      status = "TRANSMIS_ASSURANCE_ET_ATELIER",
      isSignedByBothParties = true
    )
  )

  // ==========================================================================
  // 8. SCHÉMAS ÉLECTRIQUES INTERACTIFS, FUSIBLES & PINOUT ECU (HaynesPro / Autodata)
  // ==========================================================================
  val sampleFuseBoxItems: List<FuseBoxItem> = listOf(
    FuseBoxItem(
      id = "FUSE-01",
      fuseCode = "F15 (Mini- Lame)",
      amperage = "15A (Bleu)",
      zone = FuseLocationZone.COMPARTIMENT_MOTEUR,
      protectedCircuit = "Pompe de Gavage Basse Pression Carburant & Relais Principal Injection",
      symptomsIfBlown = "Le démarreur tourne mais le moteur ne démarre pas (Pression rail 0 bar - Code P0087)",
      nominalVoltageV = 12.6,
      isIntact = true
    ),
    FuseBoxItem(
      id = "FUSE-02",
      fuseCode = "F21 (Midi-Fusible)",
      amperage = "30A (Vert)",
      zone = FuseLocationZone.COMPARTIMENT_MOTEUR,
      protectedCircuit = "Calculateur ABS / ESP & Pompe Hydraulique de Freinage",
      symptomsIfBlown = "Voyants ABS + ESP allumés + Perte assistance stabilité (Code U0121)",
      nominalVoltageV = 12.5,
      isIntact = true
    ),
    FuseBoxItem(
      id = "FUSE-03",
      fuseCode = "F08 (BSI Habitacle)",
      amperage = "10A (Rouge)",
      zone = FuseLocationZone.HABITACLE_BSI,
      protectedCircuit = "Prise Diagnostic OBD-II (Broche 16 +12V permanent) & Combiné d'Instruments",
      symptomsIfBlown = "Valise diagnostic OBD ne s'allume pas / Pas de communication CAN",
      nominalVoltageV = 0.0,
      isIntact = false
    ),
    FuseBoxItem(
      id = "FUSE-04",
      fuseCode = "R02 (Relais Noir 4 Broches)",
      amperage = "Relais 40A",
      zone = FuseLocationZone.COMPARTIMENT_MOTEUR,
      protectedCircuit = "Embrayage Électromagnétique Compresseur Climatisation A/C",
      symptomsIfBlown = "Pas de froid A/C, le compresseur ne s'enclenche pas malgré pression gaz OK",
      nominalVoltageV = 12.4,
      isIntact = true
    ),
    FuseBoxItem(
      id = "FUSE-05",
      fuseCode = "MF1 (Maxi-Fusible Batterie)",
      amperage = "80A (Direction Assistée)",
      zone = FuseLocationZone.COFFRE_BATTERIE,
      protectedCircuit = "Crémaillère de Direction Assistée Électrique (EPS)",
      symptomsIfBlown = "Volant très dur + Voyant direction rouge au tableau de bord",
      nominalVoltageV = 12.6,
      isIntact = true
    )
  )

  val sampleEcuPinouts: List<EcuPinoutSignal> = listOf(
    EcuPinoutSignal(
      id = "PIN-01",
      ecuName = "Calculateur Moteur Bosch EDC17C60 / MD1CS003",
      connectorPin = "Connecteur B (58 voies) • Broche 42",
      wireColorCode = "Jaune / Liseré Vert (0.5 mm²)",
      signalName = "Capteur Haute Pression Rail Carburant (Signal FRP)",
      referenceIdleValue = "1.15 V au ralenti (~280 à 320 bars)",
      referenceLoadValue = "3.85 V sous pleine charge (~1650 bars)",
      oscilloscopePattern = "Signal continu lisse 0.5V–4.5V (Chute = Fuite injecteur ou régulateur)"
    ),
    EcuPinoutSignal(
      id = "PIN-02",
      ecuName = "Calculateur Moteur Continental SID310 / EDC17C84 (1.5 dCi)",
      connectorPin = "Connecteur A (96 voies) • Broches 14 & 15",
      wireColorCode = "Paire torsadée Orange / Blanc (CAN-H / CAN-L)",
      signalName = "Réseau Multiplexé CAN Moteur Haute Vitesse (500 kbps)",
      referenceIdleValue = "CAN-H : 2.65 V • CAN-L : 2.35 V (Résistance terminale : 60 Ω)",
      referenceLoadValue = "Trames différentielles symétriques 1.5V - 3.5V",
      oscilloscopePattern = "Créneaux carrés miroirs CAN-High / CAN-Low sans parasites"
    ),
    EcuPinoutSignal(
      id = "PIN-03",
      ecuName = "Calculateur Moteur Bosch MED17.5 / Simos 18 (VAG TSI/TDI)",
      connectorPin = "Connecteur Moteur C • Broche 27",
      wireColorCode = "Violet / Noir (0.75 mm²)",
      signalName = "Capteur Position Arbre à Cames (CMP - Effet Hall)",
      referenceIdleValue = "Signal carré 0 V ↔ 5.0 V synchronisé avec PMH vilebrequin",
      referenceLoadValue = "Fréquence proportionnelle au régime moteur (RPM)",
      oscilloscopePattern = "Fronts montants/descendants francs 0-5V (Décalage = Chaîne détendue P0016)"
    ),
    EcuPinoutSignal(
      id = "PIN-04",
      ecuName = "Module Pompe AdBlue / SCR (Réservoir Urée)",
      connectorPin = "Connecteur 8 voies • Broche 1 (+12V) & Broche 4 (LIN)",
      wireColorCode = "Rouge (2.5 mm²) / Blanc (Bus LIN)",
      signalName = "Alimentation Chauffage & Pompe Doseuse AdBlue",
      referenceIdleValue = "12.4 V alimentation + Trame LIN 0–12 V (Pression 5.0 à 6.0 bars)",
      referenceLoadValue = "Commande PWM injecteur AdBlue selon température échappement",
      oscilloscopePattern = "Signal PWM 4 Hz à l'injecteur SCR"
    )
  )

  // ==========================================================================
  // 9. CALIBRAGE ADAS & DIAGNOSTIC HAUTE TENSION EV / HYBRIDE (SoH Batterie)
  // ==========================================================================
  val sampleAdasCalibrations: List<AdasCalibrationRecord> = listOf(
    AdasCalibrationRecord(
      id = "ADAS-01",
      vehicleModel = "Volkswagen Golf 8 (2.0 TDI 150)",
      systemType = AdasSystemType.CAMERA_PARE_BRISE,
      calibrationMode = "Statique sur panneau optique CSC + Dynamique",
      targetBoardCode = "Cible VAG CSC-01 • Distance : 1500 mm • Hauteur axe : 1265 mm",
      triggerReason = "Remplacement pare-brise acoustique avec caméra Lane Assist",
      angleDeviationBeforeDeg = 2.45,
      angleDeviationAfterDeg = 0.04,
      isCalibrated = true
    ),
    AdasCalibrationRecord(
      id = "ADAS-02",
      vehicleModel = "Peugeot 3008 II (1.5 BlueHDi)",
      systemType = AdasSystemType.RADAR_FRONTAL_ACC,
      calibrationMode = "Ajustement miroir laser pare-chocs avant",
      targetBoardCode = "Miroir Radar CSC-PSA-04 • Angle azimutal ±0.15°",
      triggerReason = "Dépose/repose bouclier avant et réglage géométrie train avant",
      angleDeviationBeforeDeg = -1.85,
      angleDeviationAfterDeg = -1.85,
      isCalibrated = false
    ),
    AdasCalibrationRecord(
      id = "ADAS-03",
      vehicleModel = "Hyundai Tucson IV Hybrid",
      systemType = AdasSystemType.VISION_360_PARKING,
      calibrationMode = "Tapis de sol damier 360° autour du véhicule",
      targetBoardCode = "Kit 4 Tapis AVM-360 • Alignement laser centre roues",
      triggerReason = "Remplacement rétroviseur droit avec caméra latérale",
      angleDeviationBeforeDeg = 1.60,
      angleDeviationAfterDeg = 0.02,
      isCalibrated = true
    )
  )

  val sampleEvBatteryReports: List<EvBatteryHealthReport> = listOf(
    EvBatteryHealthReport(
      id = "EV-BAT-01",
      vehicleModel = "Volkswagen ID.4 Pro Performance (77 kWh)",
      vin = "WVWZZZE2ZNP019482",
      batteryChemistry = "Lithium-Ion NMC 400V • 12 Modules (96 Cellules)",
      stateOfHealthPercent = 94,
      stateOfChargePercent = 81,
      cellMinVoltageMv = 3942,
      cellMaxVoltageMv = 3954,
      wltpOriginalRangeKm = 520,
      estimatedRealRangeKm = 488,
      fastDcChargesCount = 38,
      acStandardChargesCount = 214,
      highVoltageLockoutActive = false
    ),
    EvBatteryHealthReport(
      id = "EV-BAT-02",
      vehicleModel = "Toyota Corolla 1.8 Hybrid (122 ch e-CVT)",
      vin = "SB1K93BE20E118304",
      batteryChemistry = "Lithium-Ion Hybride Haute Tension 207V",
      stateOfHealthPercent = 91,
      stateOfChargePercent = 64,
      cellMinVoltageMv = 3680,
      cellMaxVoltageMv = 3698,
      wltpOriginalRangeKm = 950,
      estimatedRealRangeKm = 910,
      fastDcChargesCount = 0,
      acStandardChargesCount = 0,
      highVoltageLockoutActive = true
    )
  )

  // ==========================================================================
  // 10. BULLETINS DE SERVICE TECHNIQUE (TSB), RAPPELS & PANNES CONNUES
  // ==========================================================================
  val sampleTechnicalBulletins: List<TechnicalServiceBulletin> = listOf(
    TechnicalServiceBulletin(
      id = "TSB-01",
      bulletinNumber = "TSB-PSA-B1AW014Q0",
      brandAndModels = "Peugeot 208, 308, 3008 / Citroën C3, C4, Berlingo / Opel Corsa, Grandland",
      engineCodes = "DV5RC / DV5RD (1.5 BlueHDi 100 / 130 ch)",
      severity = TsbSeverity.MALADIE_CHRONIQUE,
      title = "Claquement haut moteur & risque de casse chaîne d'arbres à cames (7 mm → 8 mm)",
      relatedDtcCodes = "P0016, P0017, P0341",
      symptomDescription = "Bruit métallique de chaîne à froid côté supérieur culasse, perte de synchronisme arbres à cames ou non-démarrage.",
      verifiedRootCause = "Allongement prématuré ou rupture de la chaîne de liaison des arbres à cames d'origine en 7 mm et usure du tendeur hydraulique.",
      officialOemFix = "Remplacement par le kit de modification constructeur avec chaîne renforcée 8 mm, nouveaux arbres à cames, carter supérieur bosselé et huile 0W30 / 5W30 FPW9.55535/03.",
      modifiedPartReference = "Kit OEM 16 381 598 80 / FAI TCK405VVT (8 mm)",
      verifiedSuccessRatePercent = 98,
      reportedCasesCount = 642
    ),
    TechnicalServiceBulletin(
      id = "TSB-02",
      bulletinNumber = "TSB-STELLANTIS-EB2-2025",
      brandAndModels = "Peugeot 208, 2008, 308 / Citroën C3, C3 Aircross / DS3",
      engineCodes = "EB2FA / EB2ADTS / EB2DTS (1.2 PureTech 82 / 110 / 130 ch)",
      severity = TsbSeverity.RAPPEL_SECURITE,
      title = "Dégradation courroie de distribution humide & colmatage crépine pompe à huile / pompe à vide",
      relatedDtcCodes = "P15A0, P15A1, P15A8, P0011",
      symptomDescription = "Voyant pression d'huile rouge + message 'Défaut pression huile moteur : Arrêtez le véhicule' et pédale de frein dure.",
      verifiedRootCause = "Désagrégation chimique de la courroie de distribution baignant dans l'huile (dilution carburant) obstruant la crépine d'aspiration d'huile et la pompe à vide de freinage.",
      officialOemFix = "Contrôle largeur courroie avec gabarit G-0109-6 : si gonflée, dépose carter inférieur, nettoyage crépine + remplacement électrovanne pompe à huile + kit courroie Dayco Bio-Oil.",
      modifiedPartReference = "Courroie OEM 16 545 146 80 + Crépine 98 147 678 80",
      verifiedSuccessRatePercent = 95,
      reportedCasesCount = 815
    ),
    TechnicalServiceBulletin(
      id = "TSB-03",
      bulletinNumber = "TSB-VAG-TPI-2049812",
      brandAndModels = "Volkswagen Golf 7, Tiguan, Passat / Audi A3, A4 / Seat Leon / Skoda Octavia",
      engineCodes = "CRLB, DFGA, CUNA, DJGA (2.0 TDI 150 / 184 / 190 ch EA288)",
      severity = TsbSeverity.NOTE_TECHNIQUE_OEM,
      title = "Surchauffe moteur aléatoire à 110-130 km/h alors que la courroie est intacte",
      relatedDtcCodes = "P261A, P0116 (Surchauffe intermittente)",
      symptomDescription = "L'aiguille de température monte brutalement de 90°C à 120°C sur autoroute puis redescend au ralenti.",
      verifiedRootCause = "Grippe du tiroir coulissant magnétique (bague de régulation) sur la pompe à eau débrayable électrique d'origine.",
      officialOemFix = "Remplacement de la pompe à eau débrayable par une version modifiée (ou kit distribution SKF/Continental sans bague bloquante) + purge assistée par valise OBD.",
      modifiedPartReference = "OEM 04L 121 011 N / Continental CT1168WP1",
      verifiedSuccessRatePercent = 97,
      reportedCasesCount = 390
    ),
    TechnicalServiceBulletin(
      id = "TSB-04",
      bulletinNumber = "TSB-RENAULT-ACTIS-49201",
      brandAndModels = "Renault Clio 4/5, Megane 4, Kadjar / Dacia Duster, Sandero, Lodgy",
      engineCodes = "K9K 608 / K9K 872 (1.5 dCi / Blue dCi)",
      severity = TsbSeverity.NOTE_TECHNIQUE_OEM,
      title = "Message 'Contrôler Injection / Antipollution' en hiver ou forte humidité",
      relatedDtcCodes = "DTC 054416, P0096, P1525",
      symptomDescription = "Perte de puissance temporaire (mode dégradé) et coupure régulateur de vitesse par temps froid.",
      verifiedRootCause = "Condensation d'eau gelée dans l'échangeur air-air (intercooler) ou encrassement du tube de prise de pression amont turbine.",
      officialOemFix = "Débouchage du conduit métallique du capteur de pression gaz d'échappement amont turbine + reprogrammation calculateur injection.",
      modifiedPartReference = "Capteur Pression Amont 22 36 509 02R + Tuyau 20 87 446 97R",
      verifiedSuccessRatePercent = 93,
      reportedCasesCount = 510
    )
  )

  // ==========================================================================
  // 11. BOURSE PIÈCES D'OCCASION CERTIFIÉES (CASSES AUTO) & CONSIGNES ÉCHANGE STANDARD
  // ==========================================================================
  val sampleCertifiedUsedParts: List<CertifiedUsedPartOffer> = listOf(
    CertifiedUsedPartOffer(
      id = "USED-01",
      partTitle = "Boîte de Vitesses Automatique DSG6 (DQ250) Complète avec Mécatronique",
      oemReference = "02E 300 062 R / Code RQM",
      donorVehicleInfo = "VW Golf 7 GTD 2.0 TDI (2019 • 58 400 km certifiés)",
      donorVinMasked = "WVWZZZAUZKW09****",
      paintColorCode = null,
      grade = UsedPartGrade.GRADE_A,
      recyclerName = "Centre VHU Agréé AutoRecycl' Rouïba",
      warrantyMonths = 12,
      priceDzd = 245000,
      newOemPriceDzd = 690000,
      isReserved = false
    ),
    CertifiedUsedPartOffer(
      id = "USED-02",
      partTitle = "Turbocompresseur Garrett Reconditionné sur Banc (avec Rapport d'Équilibrage)",
      oemReference = "98 041 193 80 / GTA1544V",
      donorVehicleInfo = "Peugeot 308 / Partner 1.6 BlueHDi 100/120 ch",
      donorVinMasked = "ÉCHANGE STANDARD USINE",
      paintColorCode = null,
      grade = UsedPartGrade.RECONDITIONNE,
      recyclerName = "TurboTech Algérie (Atelier Reconditionnement Blida)",
      warrantyMonths = 12,
      priceDzd = 44000,
      newOemPriceDzd = 112000,
      isReserved = false
    ),
    CertifiedUsedPartOffer(
      id = "USED-03",
      partTitle = "Portière Avant Gauche Complète (Sans Mastique • Peinture d'Origine)",
      oemReference = "801014021R",
      donorVehicleInfo = "Renault Clio 5 Intens (2022 • 31 000 km)",
      donorVinMasked = "VF1RJA00468******",
      paintColorCode = "KNG (Gris Cassiopée Métallisé)",
      grade = UsedPartGrade.GRADE_A,
      recyclerName = "EuroCasse Pièces Détachées Oran Es-Sénia",
      warrantyMonths = 6,
      priceDzd = 48000,
      newOemPriceDzd = 105000,
      isReserved = false
    ),
    CertifiedUsedPartOffer(
      id = "USED-04",
      partTitle = "Phare Avant Droit Full LED Matrix Intelligent",
      oemReference = "5G1 941 082 D",
      donorVehicleInfo = "Volkswagen Golf 7.5 Carat (2020 • Pattes intactes)",
      donorVinMasked = "WVWZZZAUZLW11****",
      paintColorCode = null,
      grade = UsedPartGrade.GRADE_B,
      recyclerName = "Casse Pro AutoPièces Constantine",
      warrantyMonths = 6,
      priceDzd = 62000,
      newOemPriceDzd = 165000,
      isReserved = false
    )
  )

  val sampleCoreReturnConsignes: List<CoreReturnConsigneItem> = listOf(
    CoreReturnConsigneItem(
      id = "CORE-01",
      partName = "Vieille matière : Turbo Garrett 1.6 BlueHDi (Corps non fissuré)",
      coreReference = "CONSIGNE-TURBO-4402",
      supplierName = "TurboTech Algérie Blida",
      depositAmountDzd = 15000,
      deadlineDate = "28 Oct 2026",
      status = CoreReturnStatus.A_RESTITUER_FOURNISSEUR
    ),
    CoreReturnConsigneItem(
      id = "CORE-02",
      partName = "Vieille matière : 4 Injecteurs Bosch Piezo 0445110566",
      coreReference = "CONSIGNE-INJ-8910",
      supplierName = "Diesel Electro Service Alger",
      depositAmountDzd = 24000,
      deadlineDate = "22 Oct 2026",
      status = CoreReturnStatus.EXPEDIE_EN_CONTROLE
    ),
    CoreReturnConsigneItem(
      id = "CORE-03",
      partName = "Vieille matière : Alternateur Valeo Stop&Start 180A",
      coreReference = "CONSIGNE-ALT-3109",
      supplierName = "Sarl Auto Pièces Alger",
      depositAmountDzd = 10000,
      deadlineDate = "10 Oct 2026",
      status = CoreReturnStatus.CONSIGNE_REMBOURSEE
    )
  )

  // ==========================================================================
  // 12. REGISTRE CLIMATISATION A/C (GAZ R134a / R1234yf) & TRAÇABILITÉ FLUIDES
  // ==========================================================================
  val sampleAcVehicleSpecs: List<AcVehicleSpecification> = listOf(
    AcVehicleSpecification(
      id = "AC-SPEC-01",
      vehicleModel = "Peugeot 308 II / 3008 II (1.5 & 1.6 BlueHDi)",
      productionYears = "2014 – 2023",
      gasType = RefrigerantGasType.R1234YF,
      targetGasWeightGrams = 450,
      compressorOilType = "PAG SP-A2 (Sanden / Denso)",
      totalOilCapacityMl = 110,
      lowPressureNominalBar = "1.9 – 2.4 bar",
      highPressureNominalBar = "11.5 – 14.8 bar"
    ),
    AcVehicleSpecification(
      id = "AC-SPEC-02",
      vehicleModel = "Volkswagen Golf 7 / Seat Leon 3 / Octavia 3 (TDI & TSI)",
      productionYears = "2013 – 2020",
      gasType = RefrigerantGasType.R134A,
      targetGasWeightGrams = 500,
      compressorOilType = "PAG 46 (G 052 300 A2)",
      totalOilCapacityMl = 120,
      lowPressureNominalBar = "1.8 – 2.2 bar",
      highPressureNominalBar = "12.0 – 15.5 bar"
    ),
    AcVehicleSpecification(
      id = "AC-SPEC-03",
      vehicleModel = "Renault Clio 5 / Captur 2 / Dacia Duster 2",
      productionYears = "2019 – 2026",
      gasType = RefrigerantGasType.R1234YF,
      targetGasWeightGrams = 430,
      compressorOilType = "VC200YF / PAG 46 YF",
      totalOilCapacityMl = 100,
      lowPressureNominalBar = "2.0 – 2.5 bar",
      highPressureNominalBar = "11.0 – 14.2 bar"
    ),
    AcVehicleSpecification(
      id = "AC-SPEC-04",
      vehicleModel = "Hyundai Tucson / Kia Sportage (1.6 & 2.0 CRDi)",
      productionYears = "2016 – 2024",
      gasType = RefrigerantGasType.R134A,
      targetGasWeightGrams = 510,
      compressorOilType = "FD46XG (PAG 46)",
      totalOilCapacityMl = 130,
      lowPressureNominalBar = "1.7 – 2.3 bar",
      highPressureNominalBar = "12.5 – 16.0 bar"
    )
  )

  val sampleAcServiceLogs: List<AcServiceChargeRecord> = listOf(
    AcServiceChargeRecord(
      id = "AC-LOG-01",
      dateFormatted = "17 Oct 2026 • 10:15",
      vehicleModel = "VW Golf 7 2.0 TDI",
      licensePlate = "04291-120-16",
      gasType = RefrigerantGasType.R134A,
      recoveredGrams = 290,
      injectedGrams = 500,
      oilInjectedMl = 15,
      uvTracerAdded = true,
      vacuumTestPassed = true,
      ventOutputTempCelsius = 5.2
    ),
    AcServiceChargeRecord(
      id = "AC-LOG-02",
      dateFormatted = "16 Oct 2026 • 15:40",
      vehicleModel = "Peugeot 3008 1.5 BlueHDi",
      licensePlate = "18204-122-16",
      gasType = RefrigerantGasType.R1234YF,
      recoveredGrams = 180,
      injectedGrams = 450,
      oilInjectedMl = 20,
      uvTracerAdded = true,
      vacuumTestPassed = true,
      ventOutputTempCelsius = 4.8
    )
  )

  // ==========================================================================
  // 13. ÉTAT DES LIEUX PHOTO LOCATION, CAUTION & CONTRATS COURT TERME (Turo/Getaround)
  // ==========================================================================
  val sampleShortTermRentalContracts: List<ShortTermRentalContract> = listOf(
    ShortTermRentalContract(
      id = "RENT-01",
      contractNumber = "LOC-2026-8841",
      vehicleModel = "Renault Clio 5 Intens (1.5 Blue dCi)",
      licensePlate = "11204-123-16",
      renterFullName = "Sofiane Benali",
      renterPhone = "0550 72 19 84",
      renterLicenseNumber = "PERMIS-16-2018-99402",
      checkOutDate = "14 Oct 2026 (09:00)",
      checkInDate = "17 Oct 2026 (17:00)",
      dailyRateDzd = 6500,
      rentalDays = 3,
      includedKmPerDay = 250,
      departureKm = 44200,
      returnKm = 45120, // 920 km parcourus (750 km inclus -> +170 km sup)
      departureFuelEighths = 8, // 8/8 Plein au départ
      returnFuelEighths = 6, // 6/8 au retour (-2/8èmes)
      depositAmountDzd = 50000,
      depositStatus = RentalDepositStatus.RETENUE_PARTIELLE,
      checkOutPhotosCount = 8,
      checkInPhotosCount = 8,
      newScratchesDetected = false,
      isClosedAndSigned = false
    ),
    ShortTermRentalContract(
      id = "RENT-02",
      contractNumber = "LOC-2026-8839",
      vehicleModel = "Volkswagen Golf 8 R-Line (2.0 TDI)",
      licensePlate = "00419-124-16",
      renterFullName = "Amel Khelifi",
      renterPhone = "0661 38 90 12",
      renterLicenseNumber = "PERMIS-16-2016-44108",
      checkOutDate = "10 Oct 2026 (10:00)",
      checkInDate = "14 Oct 2026 (10:00)",
      dailyRateDzd = 12000,
      rentalDays = 4,
      includedKmPerDay = 300,
      departureKm = 21100,
      returnKm = 21980, // 880 km parcourus (< 1200 km inclus)
      departureFuelEighths = 8,
      returnFuelEighths = 8,
      depositAmountDzd = 80000,
      depositStatus = RentalDepositStatus.CAUTION_LIBEREE,
      checkOutPhotosCount = 10,
      checkInPhotosCount = 10,
      newScratchesDetected = false,
      isClosedAndSigned = true
    )
  )

  // ==========================================================================
  // 14. CATALOGUES WEB OEM GRATUITS & VUES ÉCLATÉES (CatCar.info, 7Zap, PartsSouq, NemigaParts, IFInterface, WebAutoCats)
  // ==========================================================================
  val sampleFreeWebOemPlatforms: List<FreeWebOemCatalogPlatform> = listOf(
    FreeWebOemCatalogPlatform(
      id = "OEM-WEB-CATCAR",
      name = "CatCar.info",
      baseUrl = "https://www.catcar.info",
      specialtyBadge = "RÉFÉRENCE ABSOLUE • SANS COMPTE",
      targetBrandGroups = listOf(
        OemBrandGroupFilter.RENAULT_DACIA,
        OemBrandGroupFilter.STELLANTIS,
        OemBrandGroupFilter.HYUNDAI_KIA
      ),
      supportedBrandsText = "Renault (Clio, Megane, Master...), Stellantis (Peugeot, Citroën, Fiat), Hyundai & Kia",
      architectureHighlights = "Tri chirurgical par codes boîtes (TL4, PK6, JR5) et indices moteurs (K9K, H5Ht, DV5RC) + classement par marchés mondiaux sans inscription.",
      testScenarioExample = "Scénario : Renault Master III / Clio 4 → Sélectionner type mine (ex: B98) → Code moteur K9K-636 + Boîte TL4 → Planche éclatée Injection/Turbo.",
      supportsVinLookup = true,
      supportsGearboxEngineSort = true,
      requiresAccount = false
    ),
    FreeWebOemCatalogPlatform(
      id = "OEM-WEB-7ZAP",
      name = "7Zap OEM Catalog",
      baseUrl = "https://7zap.com/fr/",
      specialtyBadge = "MODÈLE VISUEL IA & INVESTISSEURS",
      targetBrandGroups = listOf(
        OemBrandGroupFilter.STELLANTIS,
        OemBrandGroupFilter.HYUNDAI_KIA,
        OemBrandGroupFilter.VAG,
        OemBrandGroupFilter.RENAULT_DACIA
      ),
      supportedBrandsText = "Intégralité Groupe Stellantis (Peugeot, Citroën, Fiat), Marques Asiatiques (Hyundai/Kia) & VAG",
      architectureHighlights = "Interface graphique ultra-propre avec zoom vectoriel sur les vues éclatées, idéale pour présenter la structuration de données manipulée par MekanikAI.",
      testScenarioExample = "Scénario : Peugeot 3008 II (P84) / Hyundai Tucson → Arborescence graphique Moteur / Freinage → Survol des repères [1..12] avec équivalences.",
      supportsVinLookup = true,
      supportsGearboxEngineSort = true,
      requiresAccount = false
    ),
    FreeWebOemCatalogPlatform(
      id = "OEM-WEB-PARTSSOUQ",
      name = "PartsSouq",
      baseUrl = "https://partssouq.com",
      specialtyBadge = " DÉCODAGE VIN INSTANTANÉ • ASIE & EUROPE",
      targetBrandGroups = listOf(
        OemBrandGroupFilter.HYUNDAI_KIA,
        OemBrandGroupFilter.RENAULT_DACIA,
        OemBrandGroupFilter.VAG
      ),
      supportedBrandsText = "Couverture massive Hyundai & Kia (Mobis officiel), Toyota, Nissan, Renault & VAG",
      architectureHighlights = "Recherche directe par VIN 17 caractères pour filtrer automatiquement l'arborescence exacte (Moteur, Châssis, Électricité) avec références OEM.",
      testScenarioExample = "Scénario : Saisir un VIN Hyundai/Kia (KMH... / U5Y...) → Filtrage automatique des options usine → Arborescence exacte Moteur / Châssis / Électricité.",
      supportsVinLookup = true,
      supportsGearboxEngineSort = true,
      requiresAccount = false
    ),
    FreeWebOemCatalogPlatform(
      id = "OEM-WEB-NEMIGAPARTS",
      name = "NemigaParts",
      baseUrl = "https://nemigaparts.com/fr/cat_spares/",
      specialtyBadge = "ARBORESCENCES COMPLÈTES VAG & HYUNDAI/KIA",
      targetBrandGroups = listOf(
        OemBrandGroupFilter.VAG,
        OemBrandGroupFilter.HYUNDAI_KIA,
        OemBrandGroupFilter.RENAULT_DACIA
      ),
      supportedBrandsText = "Volkswagen, Audi, Seat, Skoda (VAG) + Hyundai, Kia & Renault",
      architectureHighlights = "Navigation ultra-rapide sans publicité intrusive à travers les groupes constructeurs (Groupe 1 Moteur à Groupe 9 Électricité) avec schémas OEM haute résolution.",
      testScenarioExample = "Scénario : Kia Sportage QL / VW Golf 7 → Sélectionner année & code moteur → Consulter vue éclatée train avant et moyeu.",
      supportsVinLookup = false,
      supportsGearboxEngineSort = true,
      requiresAccount = false
    ),
    FreeWebOemCatalogPlatform(
      id = "OEM-WEB-IFINTERFACE",
      name = "IFInterface (CarProg2 VAG)",
      baseUrl = "https://ifinterface.com/page/page3.php",
      specialtyBadge = "SPÉCIALISTE VAG (ETKA EN LIGNE)",
      targetBrandGroups = listOf(OemBrandGroupFilter.VAG),
      supportedBrandsText = "Groupe VAG exclusivement : Volkswagen, Audi, Seat, Skoda & VW Utilitaires (Caddy, Crafter)",
      architectureHighlights = "Structure identique au logiciel officiel concessionnaire ETKA avec filtrage par codes PR (ex: 1ZE, 1ZA), années-modèles et chaînes de remplacement.",
      testScenarioExample = "Scénario : Cliquer Free Services → CarProg2 → VW Golf (AU) → Année 2019 → Groupe 6 (Freins) → Identifier réf selon code PR.",
      supportsVinLookup = false,
      supportsGearboxEngineSort = true,
      requiresAccount = false
    ),
    FreeWebOemCatalogPlatform(
      id = "OEM-WEB-WEBAUTOCATS",
      name = "WebAutoCats",
      baseUrl = "https://webautocats.com/fr/etka/",
      specialtyBadge = "VUES ÉCLATÉES VAG & CODES PR",
      targetBrandGroups = listOf(OemBrandGroupFilter.VAG),
      supportedBrandsText = "Audi, Volkswagen, Seat, Skoda (Catalogue ETKA Web francophone)",
      architectureHighlights = "Consultation directe des planches éclatées VAG en français avec correspondance précise des codes moteurs (CRLB, DFGA, CJXA) et boîtes DSG (DQ200, DQ250, DQ381).",
      testScenarioExample = "Scénario : Seat Leon 5F / Skoda Octavia 5E → Groupe 1 (Moteur 2.0 TDI) → Pompe à eau & Kit distribution avec numéros de repères.",
      supportsVinLookup = false,
      supportsGearboxEngineSort = true,
      requiresAccount = false
    ),
    FreeWebOemCatalogPlatform(
      id = "OEM-WEB-CHINESECAR-PARTS",
      name = "ChineseCar.parts & SinoAuto360",
      baseUrl = "https://chinesecar.parts",
      specialtyBadge = "SPÉCIALISTE MARQUES CHINOISES (GEELY / JETOUR / BYD / CHERY)",
      targetBrandGroups = listOf(OemBrandGroupFilter.CHINE_SINO_AUTO, OemBrandGroupFilter.VAG),
      supportedBrandsText = "Plus de 50 constructeurs chinois : Geely (Coolray/Starray), Jetour (Dashing/T2/X70), Chery (Tiggo 8 Pro), BYD (Song Plus/Atto 3), Changan & FAW-VAG",
      architectureHighlights = "Décodage VIN spécifique aux usines chinoises (WMI LVV, L6T, LC0, LS4, LFV) via SinoAuto360 + Spécifications Dongchedi Auto-API + Arborescences JSON complètes injectables dans MekanikAI / Gemini.",
      testScenarioExample = "Scénario Démo : Jetour Dashing 1.6 TGDI (VIN LVV...) ou Geely Coolray 1.5 TD (VIN L6T...) → Groupe 35 Freinage → Extraire réf OEM plaquettes (T15-3501080BA / 4048046500) + JSON Context IA.",
      supportsVinLookup = true,
      supportsGearboxEngineSort = true,
      requiresAccount = false
    ),
    FreeWebOemCatalogPlatform(
      id = "OEM-WEB-DONGCHEDI-CAR2DB",
      name = "Auto-API (Dongchedi) & Car2DB LLM",
      baseUrl = "https://www.dongchedi.com",
      specialtyBadge = "JSON FUNCTION CALLING & FIL D'ARIANE LLM",
      targetBrandGroups = listOf(OemBrandGroupFilter.CHINE_SINO_AUTO, OemBrandGroupFilter.VAG),
      supportedBrandsText = "Geely, Jetour, Chery, BYD, Changan, FAW-Volkswagen, Haval + 142 marques (lifeofcapo/car-api)",
      architectureHighlights = "Formats JSON ultra-propres mis à jour en continu pour Gemini Function Calling + Structures en fil d'Ariane (Breadcrumbs) et résumés textuels Car2DB optimisés pour les LLM.",
      testScenarioExample = "Scénario : Requête Unique MekanikOne API → Fusionne SinoAuto360 + Dongchedi + Car2DB + lifeofcapo/car-api en 48 ms.",
      supportsVinLookup = true,
      supportsGearboxEngineSort = true,
      requiresAccount = false
    )
  )

  val sampleOemExplodedDiagrams: List<OemExplodedDiagramSheet> = listOf(
    OemExplodedDiagramSheet(
      id = "DIAG-CN-JETOUR-01",
      platformSource = "ChineseCar.parts + SinoAuto360 + Dongchedi Auto-API",
      brandGroup = OemBrandGroupFilter.CHINE_SINO_AUTO,
      vehicleTitle = "Jetour Dashing (X-1) / Chery Tiggo 8 Pro",
      engineAndGearboxCode = "Moteur 1.6 TGDI Kunpeng 197 ch (SQRF4J16) • Boîte 7DCT300 (VIN LVV...)",
      category = OemTreeBranchCategory.CHASSIS_FREINAGE,
      diagramCode = "Planche 3501-3502 — Freins Avant Ventilés Ø 305mm & Étrier Arrière EPB",
      diagramTitle = "Scénario Démo IA : Remplacement Plaquettes & Disques de Frein Jetour Dashing",
      externalDirectUrl = "https://chinesecar.parts",
      callouts = listOf(
        ExplodedCalloutItem(
          calloutNumber = 1,
          partName = "Jeu de 4 Plaquettes de Frein Avant Céramique OEM (Jetour Dashing / Tiggo 8)",
          oemReference = "T15-3501080BA",
          replacementChainRef = "Équiv. Chery T15-3501080 / Brembo P11042N / TRW GDB2184",
          technicalConditionNote = "141.2 × 59.8 × 17.5 mm • Couple colonnettes : 34 Nm • Support : 115 Nm",
          quantityRequired = 1,
          estimatedOemPriceDzd = 9800
        ),
        ExplodedCalloutItem(
          calloutNumber = 2,
          partName = "Disque de Frein Avant Ventilé Haute Carbone Ø 305 × 25 mm",
          oemReference = "T15-3501075",
          replacementChainRef = "Équiv. T15-3501075BA / Brembo 09.D418.11",
          technicalConditionNote = "Entraxe 5×108 • Épaisseur minimale d'usure : 23.0 mm",
          quantityRequired = 2,
          estimatedOemPriceDzd = 14500
        ),
        ExplodedCalloutItem(
          calloutNumber = 3,
          partName = "Jeu de 4 Plaquettes de Frein Arrière (Compatible Frein Électrique EPB)",
          oemReference = "T15-3502080BA",
          replacementChainRef = "Équiv. TRW GDB2185",
          technicalConditionNote = "⚠️ Activer Mode Maintenance EPB via OBD (UDS 0x3101) avant repoussage piston",
          quantityRequired = 1,
          estimatedOemPriceDzd = 7600
        )
      )
    ),
    OemExplodedDiagramSheet(
      id = "DIAG-CN-GEELY-02",
      platformSource = "ChineseCar.parts + SinoAuto360 + Car2DB LLM",
      brandGroup = OemBrandGroupFilter.CHINE_SINO_AUTO,
      vehicleTitle = "Geely Coolray (Binyue SX11) / Starray",
      engineAndGearboxCode = "Moteur 1.5 TD NordThor 177/181 ch (BHE15-EFZ / JLH-3G15TD) • Boîte 7DCT330 (VIN L6T...)",
      category = OemTreeBranchCategory.CHASSIS_FREINAGE,
      diagramCode = "Planche 4048-4050 — Étrier Sport Avant BMA & Frein Arrière Motorisé EPB",
      diagramTitle = "Scénario Démo IA : Remplacement Plaquettes de Frein Geely Coolray (Architecture BMA)",
      externalDirectUrl = "https://chinesecar.parts",
      callouts = listOf(
        ExplodedCalloutItem(
          calloutNumber = 1,
          partName = "Jeu de 4 Plaquettes de Frein Avant Geely Coolray SX11 (Pack Sport / Standard)",
          oemReference = "4048046500",
          replacementChainRef = "Remplacé par 8889412784 / Brembo P24198 / Bosch 0986494921",
          technicalConditionNote = "132.8 × 56.4 × 18.0 mm • Couple colonnettes : 32 Nm • Support : 105 Nm",
          quantityRequired = 1,
          estimatedOemPriceDzd = 9200
        ),
        ExplodedCalloutItem(
          calloutNumber = 2,
          partName = "Disque de Frein Avant Ventilé Geely Coolray BMA Ø 300 × 26 mm",
          oemReference = "4048011300",
          replacementChainRef = "Équiv. Geely 6608001592 / Brembo 09.C882.11",
          technicalConditionNote = "Pack Sport Étriers Rouges Ø 300 mm (Vérifier si Ø 284 mm sur finition standard)",
          quantityRequired = 2,
          estimatedOemPriceDzd = 13800
        ),
        ExplodedCalloutItem(
          calloutNumber = 3,
          partName = "Jeu de 4 Plaquettes de Frein Arrière Geely Coolray (Servomoteur EPB)",
          oemReference = "4050046200",
          replacementChainRef = "Équiv. 8889412801 / TRW GDB2310",
          technicalConditionNote = "Ouverture électrique EPB obligatoire via valise OBD avant dépose",
          quantityRequired = 1,
          estimatedOemPriceDzd = 7100
        )
      )
    ),
    OemExplodedDiagramSheet(
      id = "DIAG-REN-01",
      platformSource = "CatCar.info (Renault Dialogys OEM)",
      brandGroup = OemBrandGroupFilter.RENAULT_DACIA,
      vehicleTitle = "Renault Clio 4 / Megane 4 / Master 3",
      engineAndGearboxCode = "Moteur 1.5 dCi (K9K-636 / K9K-872) • Boîte 6 rapports TL4-045",
      category = OemTreeBranchCategory.MOTEUR,
      diagramCode = "Planche 19.02 — Distribution, Pompe HP & Injection",
      diagramTitle = "Ensemble Courroie Distribution, Galet Tendeur & Pompe Haute Pression Continental",
      externalDirectUrl = "https://www.catcar.info/renault/?lang=fr",
      callouts = listOf(
        ExplodedCalloutItem(
          calloutNumber = 1,
          partName = "Kit Courroie de Distribution + Galet Tendeur (119 dents / 27mm)",
          oemReference = "130C13191R",
          replacementChainRef = "Anc. 130C17529R",
          technicalConditionNote = "Moteur K9K indice 608/636/872 • Boîte TL4 ou EDC DC4",
          quantityRequired = 1,
          estimatedOemPriceDzd = 21500
        ),
        ExplodedCalloutItem(
          calloutNumber = 2,
          partName = "Pompe à Eau Haute Performance + Joint Métallique",
          oemReference = "210105296R",
          replacementChainRef = "Anc. 2101000Q2G",
          technicalConditionNote = "Montage obligatoire avec kit 130C13191R",
          quantityRequired = 1,
          estimatedOemPriceDzd = 11200
        ),
        ExplodedCalloutItem(
          calloutNumber = 3,
          partName = "Poulie Damper de Vilebrequin Filtrante (6PK)",
          oemReference = "123033245R",
          replacementChainRef = null,
          technicalConditionNote = "Vis centrale M12x1.25 à remplacer impérativement",
          quantityRequired = 1,
          estimatedOemPriceDzd = 16800
        ),
        ExplodedCalloutItem(
          calloutNumber = 4,
          partName = "Injecteur Piézoélectrique Continental / VDO",
          oemReference = "166006212R",
          replacementChainRef = "Anc. 166006526R",
          technicalConditionNote = "Codage IMA 6 caractères requis à la valise après pose",
          quantityRequired = 4,
          estimatedOemPriceDzd = 54000
        )
      )
    ),
    OemExplodedDiagramSheet(
      id = "DIAG-PSA-02",
      platformSource = "7Zap / CatCar.info (Stellantis ServiceBox)",
      brandGroup = OemBrandGroupFilter.STELLANTIS,
      vehicleTitle = "Peugeot 308 II / 3008 II / Citroën C4 / Fiat Scudo",
      engineAndGearboxCode = "Moteur 1.5 BlueHDi 130 (DV5RC / YHZ) • Boîte EAT8 / ML6C",
      category = OemTreeBranchCategory.MOTEUR,
      diagramCode = "Planche 0801.B4 — Carter Arbres à Cames & Chaîne 8mm",
      diagramTitle = "Kit Modification Chaîne d'Arbres à Cames 8 mm & Demi-Carters Supérieurs",
      externalDirectUrl = "https://7zap.com/fr/",
      callouts = listOf(
        ExplodedCalloutItem(
          calloutNumber = 1,
          partName = "Kit Complet Carter Arbres à Cames + Chaîne Renforcée 8 mm",
          oemReference = "16 381 598 80",
          replacementChainRef = "Remplace chaîne 7mm 98 126 472 80",
          technicalConditionNote = "Jusqu'à OPR 16885 (Modification TSB B1BW0133Q0)",
          quantityRequired = 1,
          estimatedOemPriceDzd = 94000
        ),
        ExplodedCalloutItem(
          calloutNumber = 2,
          partName = "Tendeur Hydraulique de Chaîne AAC (Course Modifiée 8mm)",
          oemReference = "98 301 472 80",
          replacementChainRef = "Anc. 98 239 548 80",
          technicalConditionNote = "Couple de serrage 10 Nm + pâte étanchéité silicone",
          quantityRequired = 1,
          estimatedOemPriceDzd = 8900
        ),
        ExplodedCalloutItem(
          calloutNumber = 3,
          partName = "Injecteur AdBlue / Urée SCR 3 Trous avec Joint Thermique",
          oemReference = "98 139 301 80",
          replacementChainRef = null,
          technicalConditionNote = "Tous types DV5RC / DV6FC • Collier inox 9809421180",
          quantityRequired = 1,
          estimatedOemPriceDzd = 31500
        )
      )
    ),
    OemExplodedDiagramSheet(
      id = "DIAG-HK-03",
      platformSource = "PartsSouq / NemigaParts (Hyundai & Kia Mobis)",
      brandGroup = OemBrandGroupFilter.HYUNDAI_KIA,
      vehicleTitle = "Hyundai Tucson (TL) / Kia Sportage (QL) / i30",
      engineAndGearboxCode = "Moteur 1.7 & 2.0 CRDi (D4FD / D4HA) • Boîte 7-DCT (D7UF1) ou BVM6",
      category = OemTreeBranchCategory.BOITE_TRANSMISSION,
      diagramCode = "Planche 41-411 — Double Embrayage 7-DCT & Actionneur",
      diagramTitle = "Ensemble Double Embrayage à Sec, Butée Concentrique & Volant Bi-Masse",
      externalDirectUrl = "https://partssouq.com",
      callouts = listOf(
        ExplodedCalloutItem(
          calloutNumber = 1,
          partName = "Kit Double Embrayage Complet 7-DCT (2ème Génération Renforcée)",
          oemReference = "41200-2D220",
          replacementChainRef = "Anc. 41200-2D101 / 41200-2D200",
          technicalConditionNote = "Marché Europe / Moyen-Orient / Algérie • Calibration TCU requise",
          quantityRequired = 1,
          estimatedOemPriceDzd = 138000
        ),
        ExplodedCalloutItem(
          calloutNumber = 2,
          partName = "Volant Moteur Bi-Masse (DMF) 7-DCT",
          oemReference = "23200-2A405",
          replacementChainRef = null,
          technicalConditionNote = "Moteur D4FD 1.7 CRDi 141 ch • 8 vis spécifiques",
          quantityRequired = 1,
          estimatedOemPriceDzd = 86000
        ),
        ExplodedCalloutItem(
          calloutNumber = 3,
          partName = "Actionneur Électromécanique de Fourchettes d'Embrayage",
          oemReference = "41470-2D011",
          replacementChainRef = null,
          technicalConditionNote = "Contrôler tiges de poussée avant montage",
          quantityRequired = 1,
          estimatedOemPriceDzd = 69000
        )
      )
    ),
    OemExplodedDiagramSheet(
      id = "DIAG-VAG-04",
      platformSource = "IFInterface / WebAutoCats / 7Zap (ETKA VAG)",
      brandGroup = OemBrandGroupFilter.VAG,
      vehicleTitle = "Volkswagen Golf 7/8 / Audi A3 / Seat Leon / Skoda Octavia",
      engineAndGearboxCode = "Moteur 2.0 TDI 150 (CRLB / DFGA) • Boîte DSG6 DQ250 / DSG7 DQ381",
      category = OemTreeBranchCategory.CHASSIS_FREINAGE,
      diagramCode = "Planche 615-020 — Freins à Disque Ventilés Avant (Codes PR)",
      diagramTitle = "Disques Ventilés, Étriers Flottants & Plaquettes selon Code PR Constructeur",
      externalDirectUrl = "https://webautocats.com/fr/etka/",
      callouts = listOf(
        ExplodedCalloutItem(
          calloutNumber = 1,
          partName = "Disque de Frein Avant Ventilé Ø 288 × 25 mm (5×112)",
          oemReference = "5Q0 615 301 H",
          replacementChainRef = "Anc. 1K0 615 301 AA",
          technicalConditionNote = "Uniquement pour Code PR : 1ZE / 1ZP (Vérifier étiquette coffre)",
          quantityRequired = 2,
          estimatedOemPriceDzd = 14500
        ),
        ExplodedCalloutItem(
          calloutNumber = 2,
          partName = "Disque de Frein Avant Ventilé Ø 312 × 25 mm (Châssis Sport / GTD)",
          oemReference = "5Q0 615 301 F",
          replacementChainRef = null,
          technicalConditionNote = "Uniquement pour Code PR : 1ZA / 1ZB / 1ZD",
          quantityRequired = 2,
          estimatedOemPriceDzd = 18900
        ),
        ExplodedCalloutItem(
          calloutNumber = 3,
          partName = "Jeu de 4 Plaquettes de Frein Avant avec Témoin d'Usure",
          oemReference = "8V0 698 151 B",
          replacementChainRef = "Anc. 5Q0 698 151 B",
          technicalConditionNote = "Système de freinage ATE • Codes PR 1ZE, 1ZA, 1ZB",
          quantityRequired = 1,
          estimatedOemPriceDzd = 13200
        )
      )
    )
  )

  // ==========================================================================
  // 15. VÉRIFICATEUR ANTI-ERREUR MONTAGE (Codes PR VAG, N° OPR PSA, Indices Renault)
  // ==========================================================================
  val sampleFitmentVerificationRules: List<FitmentVerificationRule> = listOf(
    FitmentVerificationRule(
      id = "FIT-VAG-1ZE",
      systemType = FitmentCodeSystemType.PR_CODE_VAG,
      vehicleModel = "VW Golf 7 / Seat Leon 3 / Audi A3 8V (2.0 TDI)",
      codeEntered = "1ZE",
      stickerLocationHint = "Étiquette d'options dans le carnet d'entretien ou plancher de coffre (roue de secours)",
      partCategory = "Freinage Avant (Disques & Plaquettes)",
      exactMountedSpec = "Disques Avant Ø 288 mm × 25 mm • Étrier ATE • Plaquettes avec témoin filaire",
      incompatibleTrapSpec = "⚠️ PIÈGE FRÉQUENT : Ne pas monter le Ø 280 mm (PR 1ZF - 1.6 TDI) ni le Ø 312 mm (PR 1ZA - GTD/R-Line)",
      verifiedOemRef = "5Q0 615 301 H + 8V0 698 151 B"
    ),
    FitmentVerificationRule(
      id = "FIT-PSA-OPR",
      systemType = FitmentCodeSystemType.OPR_DAM_PSA,
      vehicleModel = "Peugeot 208 II / 308 II / Citroën C3 III / Berlingo B9",
      codeEntered = "OPR 15482",
      stickerLocationHint = "Étiquette pression pneus sur le montant de portière conducteur (5 premiers chiffres du N° DAM)",
      partCategory = "Kit Distribution & Carter Arbres à Cames (1.5 BlueHDi DV5)",
      exactMountedSpec = "Montage 1ère génération Chaîne AAC 7 mm (Nécessite kit conversion 8 mm si remplacement complet)",
      incompatibleTrapSpec = "⚠️ PIÈGE FRÉQUENT : À partir de OPR 16886, chaîne 8 mm d'origine et tendeur différent (Réf 9830147280)",
      verifiedOemRef = "16 381 598 80 (Kit Conversion 8mm)"
    ),
    FitmentVerificationRule(
      id = "FIT-REN-TL4",
      systemType = FitmentCodeSystemType.INDICE_RENAULT,
      vehicleModel = "Renault Clio 4 / Megane 3 & 4 / Duster 1.5 dCi",
      codeEntered = "K9K 636 • Boîte TL4-045",
      stickerLocationHint = "Plaque ovale pied milieu droit + gravage carter boîte de vitesses",
      partCategory = "Kit Embrayage + Butée Hydraulique Concentrique (CSC)",
      exactMountedSpec = "Volant Bi-Masse Ø 230 mm • 21 cannelures • Butée hydraulique raccord clip rapide métallique",
      incompatibleTrapSpec = "⚠️ PIÈGE FRÉQUENT : Boîte JR5 (5 vitesses) utilise un volant rigide Ø 215 mm et une butée différente",
      verifiedOemRef = "302052307R + Butée 306206822R"
    ),
    FitmentVerificationRule(
      id = "FIT-MOBIS-D4FD",
      systemType = FitmentCodeSystemType.MARCHE_MOBIS,
      vehicleModel = "Hyundai Tucson TL / Kia Sportage QL (1.7 & 2.0 CRDi)",
      codeEntered = "D4FD • EUR/GEN",
      stickerLocationHint = "Décoder les positions 8 et 10 du VIN sur PartsSouq / CatCar.info",
      partCategory = "Filtre à Particules / Capteur Pression Différentielle & Pompe à Eau",
      exactMountedSpec = "Norme Euro 6 (Avec sonde Lambda amont + capteur Delta-P 3 broches)",
      incompatibleTrapSpec = "⚠️ PIÈGE FRÉQUENT : Version Euro 4/5 Marché Moyen-Orient (GEN) sans FAP d'origine possède un collecteur différent",
      verifiedOemRef = "39210-2A800 / 25100-2A201"
    )
  )

  // ==========================================================================
  // 16. RENTABILITÉ PAR OR, PRODUCTIVITÉ MÉCANICIENS & FÛTS D'HUILE VRAC 208L
  // ==========================================================================
  val sampleRepairOrderProfitability: List<RepairOrderProfitabilityItem> = listOf(
    RepairOrderProfitabilityItem(
      id = "PROF-OR-01",
      repairOrderNumber = "OR-2026-1094",
      vehicleAndClient = "Peugeot 3008 1.5 BlueHDi • M. Karim Mansouri",
      mechanicAssigned = "Yacine B. (Chef d'Atelier)",
      billedHoursBarème = 5.4,
      clockedHoursReal = 4.5, // 120% d'efficacité !
      partsBuyCostHtDzd = 72000,
      partsSellPriceHtDzd = 98500,
      laborSellPriceHtDzd = 24300
    ),
    RepairOrderProfitabilityItem(
      id = "PROF-OR-02",
      repairOrderNumber = "OR-2026-1095",
      vehicleAndClient = "VW Golf 7 2.0 TDI • SARL TransLogistique",
      mechanicAssigned = "Mehdi K. (Électromécanicien)",
      billedHoursBarème = 3.8,
      clockedHoursReal = 3.6, // 105% d'efficacité
      partsBuyCostHtDzd = 31000,
      partsSellPriceHtDzd = 44500,
      laborSellPriceHtDzd = 17100
    ),
    RepairOrderProfitabilityItem(
      id = "PROF-OR-03",
      repairOrderNumber = "OR-2026-1096",
      vehicleAndClient = "Renault Clio 5 1.5 dCi • Mme Nadia Touati",
      mechanicAssigned = "Riad S. (Technicien Rapide)",
      billedHoursBarème = 1.5,
      clockedHoursReal = 1.9, // 78% (Boulon grippé)
      partsBuyCostHtDzd = 11200,
      partsSellPriceHtDzd = 16800,
      laborSellPriceHtDzd = 6750
    )
  )

  val sampleBulkOilDrums: List<BulkOilDrumTank> = listOf(
    BulkOilDrumTank(
      id = "DRUM-01",
      fluidName = "Fût 208L — Huile Moteur 5W-30 Low SAPS (C3)",
      viscosityAndNorm = "Homologuée VW 504.00/507.00 • PSA B71 2290 • RN0720",
      drumCapacityLiters = 208.0,
      currentRemainingLiters = 124.5,
      costPerLiterDzd = 1150,
      sellPricePerLiterDzd = 1850,
      supplierName = "TotalEnergies / Motul Algérie"
    ),
    BulkOilDrumTank(
      id = "DRUM-02",
      fluidName = "Fût 208L — Huile Moteur 5W-40 100% Synthèse (A3/B4)",
      viscosityAndNorm = "Homologuée RN0710 • VW 502.00/505.00 • MB 229.5",
      drumCapacityLiters = 208.0,
      currentRemainingLiters = 29.0, // 13% -> ALERTE SEUIL CRITIQUE !
      costPerLiterDzd = 980,
      sellPricePerLiterDzd = 1600,
      supplierName = "Naftal / Castrol Pro"
    ),
    BulkOilDrumTank(
      id = "DRUM-03",
      fluidName = "Cuve Atelier 200L — AdBlue® ISO 22241 Haute Pureté",
      viscosityAndNorm = "Systèmes SCR BlueHDi / TDI / dCi (Sans cristallisation)",
      drumCapacityLiters = 200.0,
      currentRemainingLiters = 88.0,
      costPerLiterDzd = 190,
      sellPricePerLiterDzd = 380,
      supplierName = "Chimie Auto Pro Blida"
    ),
    BulkOilDrumTank(
      id = "DRUM-04",
      fluidName = "Bidon Atelier 60L — Liquide de Frein DOT 4 LV (ESP/ABS)",
      viscosityAndNorm = "Basse viscosité spécial blocs ABS/ESP modernes",
      drumCapacityLiters = 60.0,
      currentRemainingLiters = 9.5, // 15% -> ALERTE SEUIL !
      costPerLiterDzd = 1400,
      sellPricePerLiterDzd = 2400,
      supplierName = "Brembo / Bosch Fluides"
    )
  )

  // ==========================================================================
  // 17. SCANNER PRÉ-CONTRÔLE TECHNIQUE (ZÉRO CONTRE-VISITE) & VÉHICULES DE COURTOISIE
  // ==========================================================================
  val samplePreCtInspectionPoints: List<PreCtInspectionPoint> = listOf(
    PreCtInspectionPoint(
      id = "CT-PT-01",
      categoryTitle = "Freinage & Équilibre Banc",
      checkPointName = "Déséquilibre Freinage Train Avant / Train Arrière",
      measuredValueText = "Avant : 7% • Arrière : 11% • Efficacité globale : 68%",
      officialThresholdText = "Contre-visite si déséquilibre ≥ 30% ou efficacité < 50%",
      status = PreCtCheckStatus.CONFORME,
      correctiveActionDzd = 0
    ),
    PreCtInspectionPoint(
      id = "CT-PT-02",
      categoryTitle = "Pollution & Opacité des Fumées",
      checkPointName = "Opacité Fumées Diesel (Coefficient d'absorption m⁻¹) & Voyant OBD",
      measuredValueText = "Opacité mesurée : 2.15 m⁻¹ (Fumée noire à l'accélération)",
      officialThresholdText = "Seuil max Euro 5/6 : 0.70 à 1.50 m⁻¹ + Aucun code défaut MIL actif",
      status = PreCtCheckStatus.CONTRE_VISITE_MAJEURE,
      correctiveActionDzd = 14500 // Décalaminage Hydrogène + Nettoyant Injecteurs/FAP
    ),
    PreCtInspectionPoint(
      id = "CT-PT-03",
      categoryTitle = "Direction, Rotules & Trains Roulants",
      checkPointName = "Jeu Rotules Axiales, Triangles & Soufflets de Cardan",
      measuredValueText = "Léger craquement silentbloc triangle Av. Droit (sans jeu excessif)",
      officialThresholdText = "Contre-visite si jeu franc rotule ou soufflet déchiré",
      status = PreCtCheckStatus.DEFAUT_MINEUR,
      correctiveActionDzd = 6500
    ),
    PreCtInspectionPoint(
      id = "CT-PT-04",
      categoryTitle = "Éclairage, Régloscope Phares & Visibilité",
      checkPointName = "Hauteur Faisceau Feux de Croisement (Rabattement %)",
      measuredValueText = "Phare Gauche : -1.2% • Phare Droit : +0.4% (Éblouissant !)",
      officialThresholdText = "Rabattement obligatoire compris entre -0.5% et -2.5%",
      status = PreCtCheckStatus.CONTRE_VISITE_MAJEURE,
      correctiveActionDzd = 1500 // Réglage optique régloscope laser
    ),
    PreCtInspectionPoint(
      id = "CT-PT-05",
      categoryTitle = "Pneumatiques & Sécurité Habitacle",
      checkPointName = "Profondeur Sculptures (> 1.6 mm), Hernies & Ceintures",
      measuredValueText = "Avant : 4.8 mm • Arrière : 5.2 mm • Aucune hernie",
      officialThresholdText = "Témoin 1.6 mm mini + Indice charge/vitesse identique par essieu",
      status = PreCtCheckStatus.CONFORME,
      correctiveActionDzd = 0
    )
  )

  val sampleCourtesyLoanerVehicles: List<CourtesyLoanerVehicle> = listOf(
    CourtesyLoanerVehicle(
      id = "LOAN-01",
      vehicleModel = "Dacia Sandero Stepway 3 (1.0 TCe GPL)",
      licensePlate = "08412-123-16",
      fuelLevelEighths = 7,
      dailyPenaltyIfLateDzd = 4500,
      isCurrentlyLoaned = true,
      assignedClientName = "M. Karim Mansouri (3008 sur Pont 1)",
      linkedRepairOrderNumber = "OR-2026-1094",
      expectedReturnDate = "18 Oct 2026 (17:00)"
    ),
    CourtesyLoanerVehicle(
      id = "LOAN-02",
      vehicleModel = "Renault Symbol / Logan (1.5 dCi — Courtoisie Atelier)",
      licensePlate = "14920-120-16",
      fuelLevelEighths = 8,
      dailyPenaltyIfLateDzd = 3500,
      isCurrentlyLoaned = false,
      assignedClientName = null,
      linkedRepairOrderNumber = null,
      expectedReturnDate = null
    ),
    CourtesyLoanerVehicle(
      id = "LOAN-03",
      vehicleModel = "Peugeot 208 Like (1.2 PureTech — Prêt Flotte)",
      licensePlate = "02914-122-16",
      fuelLevelEighths = 6,
      dailyPenaltyIfLateDzd = 4500,
      isCurrentlyLoaned = false,
      assignedClientName = null,
      linkedRepairOrderNumber = null,
      expectedReturnDate = null
    )
  )

  // ============================================================================
  // 26. SUPER-ADMINISTRATEUR : APPROBATION DES INSCRIPTIONS & FORFAITS (DZD)
  // ============================================================================
  val sampleRegistrationRequests = listOf(
    UserRegistrationRequest(
      id = "REG-2026-801",
      companyOrFullName = "SARL Garage MechaTech & Diagnostic Pro",
      managerName = "Sofiane Benali",
      email = "contact@mechatech-dz.com",
      phone = "0550 84 19 22",
      wilaya = "16 - Alger (Bab Ezzouar)",
      requestedRole = AccountRole.GARAGE_PRO,
      rcNumber = "RC 16/00-4829104B22",
      nifNumber = "NIF 002216482910455",
      requestedInitialPlanName = "Forfait Atelier Expert + Passerelle Magasin (23 000 DA/an)",
      submittedDate = "Aujourd'hui • 08:42",
      status = AdminApprovalStatus.EN_ATTENTE,
      adminNote = "Dossier complet : Extrait RC + Agrément Artisan Mécanicien vérifiés."
    ),
    UserRegistrationRequest(
      id = "REG-2026-802",
      companyOrFullName = "EURL Auto Pièces Import & Distribution Oran",
      managerName = "Kamel Ouali",
      email = "k.ouali@autopieces-oran.dz",
      phone = "0770 31 64 90",
      wilaya = "31 - Oran (Es Senia)",
      requestedRole = AccountRole.VENDEUR_PIECES,
      rcNumber = "RC 31/00-9182734A21",
      nifNumber = "NIF 002131918273488",
      requestedInitialPlanName = "Forfait Vendeur & Grossiste Pièces Pro (18 000 DA/an)",
      submittedDate = "Aujourd'hui • 08:15",
      status = AdminApprovalStatus.EN_ATTENTE,
      adminNote = "Grossiste pièces VAG & Stellantis — Demande accès catalogue OEM + B2B."
    ),
    UserRegistrationRequest(
      id = "REG-2026-803",
      companyOrFullName = "Constantine Prestige Car Rental & Fleet",
      managerName = "Yacine Bouzid",
      email = "direction@prestige-rent25.dz",
      phone = "0661 92 45 11",
      wilaya = "25 - Constantine (El Khroub)",
      requestedRole = AccountRole.LOCATION_VOITURES,
      rcNumber = "RC 25/00-3391048B23",
      nifNumber = "NIF 002325339104812",
      requestedInitialPlanName = "Forfait Ultra Pro Entreprise 10 Sous-Comptes (30 000 DA/an)",
      submittedDate = "Hier • 19:30",
      status = AdminApprovalStatus.EN_ATTENTE,
      adminNote = "Flotte de 28 véhicules locatifs. Souhaite activer les états des lieux 360°."
    ),
    UserRegistrationRequest(
      id = "REG-2026-804",
      companyOrFullName = "Amine Khelif (Propriétaire Seat Leon FR)",
      managerName = "Amine Khelif",
      email = "amine.khelif09@gmail.com",
      phone = "0541 29 88 04",
      wilaya = "09 - Blida",
      requestedRole = AccountRole.CONDUCTEUR,
      rcNumber = "CIN / Permis N° 09-481920",
      nifNumber = "Particulier Certifié",
      requestedInitialPlanName = "Forfait Conducteur & Carnet Digital (2 000 DA/an)",
      submittedDate = "Hier • 17:10",
      status = AdminApprovalStatus.EN_ATTENTE,
      adminNote = "Demande d'accès au carnet d'entretien QR et diagnostic IA."
    ),
    UserRegistrationRequest(
      id = "REG-2026-798",
      companyOrFullName = "Garage Auto Performance • Alger Centre",
      managerName = "Badreddine Mekanik",
      email = "atelier.performance@mekanik.dz",
      phone = "0552 10 20 30",
      wilaya = "16 - Alger Centre",
      requestedRole = AccountRole.GARAGE_PRO,
      rcNumber = "RC 16/00-1102938B20",
      nifNumber = "NIF 002016110293801",
      requestedInitialPlanName = "Pack Complet Clé en Main (35 000 DA/an)",
      submittedDate = "12 Oct 2026",
      status = AdminApprovalStatus.APPROUVE,
      generatedLicenseKey = "MK-DZ-2026-PRO-9941",
      adminNote = "Compte principal certifié et vérifié."
    ),
    UserRegistrationRequest(
      id = "REG-2026-795",
      companyOrFullName = "SARL Auto Pièces Alger (Bab Ezzouar)",
      managerName = "Mourad Haddad",
      email = "commercial@autopieces-alger.dz",
      phone = "0560 44 12 89",
      wilaya = "16 - Alger",
      requestedRole = AccountRole.VENDEUR_PIECES,
      rcNumber = "RC 16/00-7748291B19",
      nifNumber = "NIF 001916774829140",
      requestedInitialPlanName = "Forfait Vendeur & Magasin Pièces (18 000 DA/an)",
      submittedDate = "10 Oct 2026",
      status = AdminApprovalStatus.APPROUVE,
      generatedLicenseKey = "MK-DZ-2026-MAG-4410",
      adminNote = "Distributeur officiel agréé sur la Marketplace."
    )
  )

  val samplePlanSubscriptionRequests = listOf(
    PlanSubscriptionRequest(
      id = "SUB-2026-901",
      accountName = "SARL Garage MechaTech & Diagnostic Pro",
      userEmail = "contact@mechatech-dz.com",
      accountRole = AccountRole.GARAGE_PRO,
      wilaya = "16 - Alger",
      planId = "plan_garage_store_bundle",
      planTitle = "Forfait Atelier Pro (18 000 DA) + Passerelle Magasin (+5 000 DA)",
      billingPeriod = "Annuel (12 mois)",
      amountDzd = 23000,
      paymentMethod = PaymentMethodAdminType.BARIDIMOB_CCP,
      paymentProofReference = "Reçu BaridiMob RIP #BM-20261017-88412",
      submittedDate = "Aujourd'hui • 08:45",
      status = AdminApprovalStatus.EN_ATTENTE,
      includesStoreLinkAddon = true,
      extraSubAccountsCount = 0
    ),
    PlanSubscriptionRequest(
      id = "SUB-2026-902",
      accountName = "Constantine Prestige Car Rental & Fleet",
      userEmail = "direction@prestige-rent25.dz",
      accountRole = AccountRole.LOCATION_VOITURES,
      wilaya = "25 - Constantine",
      planId = "plan_ultra_pro",
      planTitle = "Forfait Ultra Pro Entreprise (10 Sous-Comptes + Multi-Postes)",
      billingPeriod = "Annuel (12 mois)",
      amountDzd = 30000,
      paymentMethod = PaymentMethodAdminType.VIREMENT_BANCAIRE,
      paymentProofReference = "Ordre Virement BNA #VIR-25-994012",
      submittedDate = "Aujourd'hui • 08:05",
      status = AdminApprovalStatus.EN_ATTENTE,
      includesStoreLinkAddon = false,
      extraSubAccountsCount = 10
    ),
    PlanSubscriptionRequest(
      id = "SUB-2026-903",
      accountName = "EURL Auto Pièces Import & Distribution Oran",
      userEmail = "k.ouali@autopieces-oran.dz",
      accountRole = AccountRole.VENDEUR_PIECES,
      wilaya = "31 - Oran",
      planId = "plan_full_package_35k",
      planTitle = "Pack Complet Clé en Main (Atelier + Magasin + Tous Logiciels OEM)",
      billingPeriod = "Annuel (12 mois)",
      amountDzd = 35000,
      paymentMethod = PaymentMethodAdminType.CIB_EDAHABIA,
      paymentProofReference = "SATIM CIB Autorisation #AUTH-774190",
      submittedDate = "Hier • 21:15",
      status = AdminApprovalStatus.EN_ATTENTE,
      includesStoreLinkAddon = true,
      extraSubAccountsCount = 4
    ),
    PlanSubscriptionRequest(
      id = "SUB-2026-904",
      accountName = "Atelier Électro-Diesel Sétif Pro",
      userEmail = "electrodiesel.setif19@gmail.com",
      accountRole = AccountRole.GARAGE_PRO,
      wilaya = "19 - Sétif",
      planId = "pack_oem_software_10k",
      planTitle = "Supplément Pack Intégral Logiciels OEM + Pack Extension (+2 Comptes)",
      billingPeriod = "Annuel (12 mois)",
      amountDzd = 12500,
      paymentMethod = PaymentMethodAdminType.BARIDIMOB_CCP,
      paymentProofReference = "Virement CCP #CCP-19-552019",
      submittedDate = "Hier • 16:40",
      status = AdminApprovalStatus.EN_ATTENTE,
      includesStoreLinkAddon = false,
      extraSubAccountsCount = 2
    ),
    PlanSubscriptionRequest(
      id = "SUB-2026-890",
      accountName = "Garage Auto Performance • Alger Centre",
      userEmail = "atelier.performance@mekanik.dz",
      accountRole = AccountRole.GARAGE_PRO,
      wilaya = "16 - Alger",
      planId = "plan_full_package_35k",
      planTitle = "Pack Complet Clé en Main (Ultra Pro + Magasin + Logiciels OEM)",
      billingPeriod = "Annuel (12 mois)",
      amountDzd = 35000,
      paymentMethod = PaymentMethodAdminType.CIB_EDAHABIA,
      paymentProofReference = "SATIM CIB #AUTH-510294",
      submittedDate = "12 Oct 2026",
      status = AdminApprovalStatus.APPROUVE,
      includesStoreLinkAddon = true,
      extraSubAccountsCount = 10
    )
  )

  val sampleAdminAuditLogs = listOf(
    AdminAuditLogEntry(
      id = "LOG-01",
      timestamp = "Aujourd'hui • 09:14",
      adminEmail = "badroumc@gmail.com",
      actionTitle = "Connexion Super-Administrateur",
      targetEntity = "Console Centrale Mekanik+ Algérie",
      impactSummary = "Accès complet activé (Validation Inscriptions & Licences DZD)"
    ),
    AdminAuditLogEntry(
      id = "LOG-02",
      timestamp = "12 Oct 2026 • 14:20",
      adminEmail = "badroumc@gmail.com",
      actionTitle = "Approbation Forfait Pack Complet 35 000 DA",
      targetEntity = "Garage Auto Performance (Alger)",
      impactSummary = "Licence MK-DZ-2026-PRO-9941 activée + Facture fiscale émise"
    ),
    AdminAuditLogEntry(
      id = "LOG-03",
      timestamp = "10 Oct 2026 • 11:05",
      adminEmail = "badroumc@gmail.com",
      actionTitle = "Validation Inscription Grossiste Pièces",
      targetEntity = "SARL Auto Pièces Alger (Bab Ezzouar)",
      impactSummary = "Vérification RC/NIF validée • Accès Marketplace B2B ouvert",
      operatorBadge = "SUPER-ADMIN ROOT"
    )
  )

  // ============================================================================
  // 27. ÉQUIPE EMPLOYÉS DU SUPER-ADMINISTRATEUR (badroumc@gmail.com)
  // ============================================================================
  val sampleAdminStaffMembers = listOf(
    AdminStaffMember(
      id = "STAFF-01",
      fullName = "Rania Merabet",
      email = "rania.admin@mekanik.dz",
      pinCode = "1016",
      role = AdminStaffRole.AGENT_INSCRIPTIONS,
      assignedWilayas = "Centre (16-Alger, 09-Blida, 35-Boumerdès, 15-Tizi Ouzou)",
      canApproveRegistrations = true,
      canGenerateActivationKeys = true,
      canValidatePaymentsDzd = false,
      canSuspendAccounts = false,
      processedRequestsCount = 42,
      generatedKeysCount = 29,
      isActive = true,
      lastActiveLabel = "En service • Aujourd'hui 09:30"
    ),
    AdminStaffMember(
      id = "STAFF-02",
      fullName = "Walid Bensalem",
      email = "walid.licences@mekanik.dz",
      pinCode = "2031",
      role = AdminStaffRole.GESTIONNAIRE_LICENCES_DZD,
      assignedWilayas = "National (Toutes les 58 Wilayas — Reçus CCP / CIB / Banque)",
      canApproveRegistrations = true,
      canGenerateActivationKeys = true,
      canValidatePaymentsDzd = true,
      canSuspendAccounts = true,
      processedRequestsCount = 67,
      generatedKeysCount = 61,
      isActive = true,
      lastActiveLabel = "En service • Aujourd'hui 09:18"
    ),
    AdminStaffMember(
      id = "STAFF-03",
      fullName = "Samy Khelifa",
      email = "samy.support@mekanik.dz",
      pinCode = "3025",
      role = AdminStaffRole.SUPPORT_TECHNIQUE,
      assignedWilayas = "Est & Ouest (25-Constantine, 19-Sétif, 31-Oran, 06-Béjaïa)",
      canApproveRegistrations = true,
      canGenerateActivationKeys = true,
      canValidatePaymentsDzd = false,
      canSuspendAccounts = false,
      processedRequestsCount = 24,
      generatedKeysCount = 18,
      isActive = true,
      lastActiveLabel = "Hier à 18:45"
    )
  )

  // ============================================================================
  // 28. SUITE DMS « WINMOTOR NEXT & PEUGEOT SERVICE BOX V14 »
  // ============================================================================
  val sampleWinMotorOperations = listOf(
    WinMotorFlatRateOperation(
      id = "WM-OP-01",
      oemOperationCode = "0129-B4",
      label = "Remplacement Kit Distribution + Pompe à Eau + Purge sous vide",
      vehicleApplication = "Peugeot 308 / 3008 / Citroën C4 (1.5 BlueHDi 130 DV5RC)",
      centiemesHeure = 340, // 3h24
      laborTier = WinMotorLaborTier.T2_MECANIQUE_COMPLEXE,
      includesDiagnosticPass = true,
      isSelectedForOrder = true
    ),
    WinMotorFlatRateOperation(
      id = "WM-OP-02",
      oemOperationCode = "0801-E2",
      label = "Modification Demi-Carter AAC + Chaîne 8 mm (TSB B1BW0133Q0)",
      vehicleApplication = "Stellantis 1.5 BlueHDi DV5 (OPR <= 16885)",
      centiemesHeure = 520, // 5h12
      laborTier = WinMotorLaborTier.T2_MECANIQUE_COMPLEXE,
      includesDiagnosticPass = true,
      isSelectedForOrder = false
    ),
    WinMotorFlatRateOperation(
      id = "WM-OP-03",
      oemOperationCode = "1645-C9",
      label = "Télécodage BSI / Apprentissage Injecteurs & Régénération FAP DiagBox",
      vehicleApplication = "Tous Modèles Peugeot / Citroën / DS / Opel (Architecture Multiplexée)",
      centiemesHeure = 120, // 1h12
      laborTier = WinMotorLaborTier.T3_HAUTE_TECHNICITE,
      includesDiagnosticPass = true,
      isSelectedForOrder = true
    ),
    WinMotorFlatRateOperation(
      id = "WM-OP-04",
      oemOperationCode = "4210-A1",
      label = "Remplacement Disques & Plaquettes Avant + Contrôle Etrier",
      vehicleApplication = "Peugeot 208 / 308 / Partner / VW Golf 7 & 8",
      centiemesHeure = 130, // 1h18
      laborTier = WinMotorLaborTier.T1_COURANT,
      includesDiagnosticPass = false,
      isSelectedForOrder = false
    ),
    WinMotorFlatRateOperation(
      id = "WM-OP-05",
      oemOperationCode = "0001-R1",
      label = "Entretien Périodique Constructeur (Huile 0W30 B71 2312 + 4 Filtres)",
      vehicleApplication = "Gamme Peugeot / Citroën / Renault / VAG",
      centiemesHeure = 90, // 0h54
      laborTier = WinMotorLaborTier.T1_COURANT,
      includesDiagnosticPass = false,
      isSelectedForOrder = false
    ),
    WinMotorFlatRateOperation(
      id = "WM-OP-06",
      oemOperationCode = "6190-H8",
      label = "Diagnostic Consignation 400V & Contrôle Isolement Pack Batterie PHEV/e-208",
      vehicleApplication = "Peugeot e-208 / 3008 Hybrid4 / Renault Megane E-Tech",
      centiemesHeure = 180, // 1h48
      laborTier = WinMotorLaborTier.T3_HAUTE_TECHNICITE,
      includesDiagnosticPass = true,
      isSelectedForOrder = false
    )
  )

  val sampleServiceBoxBaskets = listOf(
    ServiceBoxBasketImport(
      id = "SBX-01",
      basketReference = "SBX-V14-2026-884",
      sourceCatalog = "Peugeot Service Box V14 GAR • Crescendo+",
      vin = "VF3MCYHZJMS109482",
      oprDamCode = "OPR 15940 (Usine Sochaux)",
      vehicleModel = "Peugeot 3008 II (P84) 1.5 BlueHDi 130 EAT8",
      partsList = listOf(
        ServiceBoxPartLine("16 381 598 80", "Kit Complet Carter Arbres à Cames + Chaîne 8 mm", 1, 94000),
        ServiceBoxPartLine("16 231 840 80", "Kit Distribution + Pompe à Eau Origine PSA", 1, 28500),
        ServiceBoxPartLine("98 094 211 80", "Joint & Pâte Silicone Étanchéité Haute Température", 1, 4200)
      ),
      associatedOperations = listOf(
        WinMotorFlatRateOperation(
          id = "SBX-OP-1",
          oemOperationCode = "0801-E2",
          label = "Dépose/Pose Carter AAC + Modification Chaîne 8mm + Distribution",
          vehicleApplication = "Peugeot 3008 1.5 BlueHDi DV5RC",
          centiemesHeure = 520,
          laborTier = WinMotorLaborTier.T2_MECANIQUE_COMPLEXE,
          includesDiagnosticPass = true
        ),
        WinMotorFlatRateOperation(
          id = "SBX-OP-2",
          oemOperationCode = "1645-C9",
          label = "Lecture/Effacement Défauts & Réinitialisation Auto-Adaptatifs ECU",
          vehicleApplication = "Peugeot 3008 1.5 BlueHDi DV5RC",
          centiemesHeure = 80,
          laborTier = WinMotorLaborTier.T3_HAUTE_TECHNICITE,
          includesDiagnosticPass = true
        )
      ),
      isImportedIntoActiveOrder = false
    ),
    ServiceBoxBasketImport(
      id = "SBX-02",
      basketReference = "SBX-V14-2026-891",
      sourceCatalog = "Peugeot Service Box V14 / 7Zap Stellantis",
      vin = "VF3LCBHZHGS224109",
      oprDamCode = "OPR 15112 (Usine Mulhouse)",
      vehicleModel = "Peugeot 308 II (T9) 1.6 BlueHDi 120 BVM6",
      partsList = listOf(
        ServiceBoxPartLine("98 139 301 80", "Injecteur Urée / AdBlue SCR 3 Trous Origine", 1, 31500),
        ServiceBoxPartLine("16 118 920 80", "Collier Inox Échappement SCR + Joint", 1, 3400)
      ),
      associatedOperations = listOf(
        WinMotorFlatRateOperation(
          id = "SBX-OP-3",
          oemOperationCode = "1740-D1",
          label = "Remplacement Injecteur Urée SCR + Test Dosage DiagBox",
          vehicleApplication = "Peugeot 308 1.6 BlueHDi DV6FC",
          centiemesHeure = 140,
          laborTier = WinMotorLaborTier.T3_HAUTE_TECHNICITE,
          includesDiagnosticPass = true
        )
      ),
      isImportedIntoActiveOrder = false
    )
  )

  val sampleCompanionClockEntries = listOf(
    CompanionTimeClockEntry(
      id = "CLK-01",
      companionName = "Yacine Benali",
      companionMatricule = "COMP-DZ-01",
      specialty = "Chef d'Atelier & Référent Technique Peugeot/VAG",
      repairOrderNumber = "OR-2026-1094",
      vehicleAndPlate = "Peugeot 3008 1.5 BlueHDi • 18204-122-16",
      operationTitle = "Kit Chaîne AAC 8mm + Distribution DV5RC (0801-E2)",
      laborTier = WinMotorLaborTier.T2_MECANIQUE_COMPLEXE,
      soldCentiemesBarème = 520, // 5h12 vendues au barème
      clockedCentiemesReal = 440, // 4h24 pointées -> 118% productivité
      status = CompanionClockStatus.EN_COURS,
      bayName = "Pont N°1 (2 Colonnes 4T)"
    ),
    CompanionTimeClockEntry(
      id = "CLK-02",
      companionName = "Mehdi Khelifi",
      companionMatricule = "COMP-DZ-02",
      specialty = "Électromécanicien DiagBox / ODIS / Télécodage BSI",
      repairOrderNumber = "OR-2026-1095",
      vehicleAndPlate = "Peugeot 308 1.6 BlueHDi • 01429-122-16",
      operationTitle = "Télécodage Calculateur SCR + Injecteur AdBlue (1740-D1)",
      laborTier = WinMotorLaborTier.T3_HAUTE_TECHNICITE,
      soldCentiemesBarème = 140, // 1h24 vendues
      clockedCentiemesReal = 115, // 1h09 pointées -> 121% productivité
      status = CompanionClockStatus.TERMINE,
      bayName = "Baie Diagnostic Électronique"
    ),
    CompanionTimeClockEntry(
      id = "CLK-03",
      companionName = "Riad Saidi",
      companionMatricule = "COMP-DZ-03",
      specialty = "Technicien Service Rapide & Freinage (T1)",
      repairOrderNumber = "OR-2026-1096",
      vehicleAndPlate = "Renault Clio 5 1.5 dCi • 11204-123-16",
      operationTitle = "Disques & Plaquettes Avant + Purge DOT4 LV (4210-A1)",
      laborTier = WinMotorLaborTier.T1_COURANT,
      soldCentiemesBarème = 130, // 1h18 vendues
      clockedCentiemesReal = 145, // 1h27 pointées -> 89% (étrier grippé)
      status = CompanionClockStatus.EN_PAUSE_PIECE,
      bayName = "Pont Rapide N°3"
    )
  )

  val sampleMultiPayerSplitOrders = listOf(
    MultiPayerSplitOrder(
      id = "SPLIT-01",
      repairOrderNumber = "OR-2026-1094",
      vehicleModel = "Peugeot 3008 II 1.5 BlueHDi 130 (DV5RC)",
      vin = "VF3MCYHZJMS109482",
      clientName = "M. Karim Mansouri",
      totalOrderAmountHtDzd = 146500,
      clientSharePercent = 35, // 35% à la charge du client
      manufacturerWarrantyPercent = 55, // 55% Prise en charge Campagne Spéciale Constructeur Peugeot (Chaîne 8mm)
      internalCessionPercent = 10, // 10% Remise commerciale / Cession Interne Atelier
      insuranceSharePercent = 0,
      warrantyClaimReference = "GAR-PSA-DV5-2026-912",
      isValidatedForInvoicing = true
    ),
    MultiPayerSplitOrder(
      id = "SPLIT-02",
      repairOrderNumber = "OR-2026-1095",
      vehicleModel = "Peugeot 308 II 1.6 BlueHDi 120",
      vin = "VF3LCBHZHGS224109",
      clientName = "SARL TransLogistique Algérie",
      totalOrderAmountHtDzd = 42600,
      clientSharePercent = 50,
      manufacturerWarrantyPercent = 50,
      internalCessionPercent = 0,
      insuranceSharePercent = 0,
      warrantyClaimReference = "GAR-SCR-UREA-2026-408",
      isValidatedForInvoicing = false
    )
  )

  val sampleBackorderVorItems = listOf(
    BackorderVorItem(
      id = "VOR-01",
      oemReference = "16 381 598 80",
      partDesignation = "Kit Carter Arbres à Cames + Chaîne Renforcée 8 mm (DV5)",
      linkedRepairOrderNumber = "OR-2026-1094",
      clientName = "M. Karim Mansouri",
      vehicleModel = "Peugeot 3008 II 1.5 BlueHDi",
      blockedBayLabel = "Pont N°1 Immobilisé (VOR)",
      supplierName = "Magasin Central Pièces Origine Stellantis Alger",
      priority = BackorderVorPriority.VOR_PONT_BLOQUE,
      expectedArrivalTime = "Aujourd'hui à 14h30 (Coursier Express)",
      priceDzd = 94000,
      isReceivedAndAllocated = false
    ),
    BackorderVorItem(
      id = "VOR-02",
      oemReference = "98 139 301 80",
      partDesignation = "Injecteur AdBlue / Urée SCR 3 Trous + Collier Inox",
      linkedRepairOrderNumber = "OR-2026-1095",
      clientName = "SARL TransLogistique Algérie",
      vehicleModel = "Peugeot 308 II 1.6 BlueHDi",
      blockedBayLabel = "Baie Diagnostic Immobilisée",
      supplierName = "SARL Auto Pièces Alger (Bab Ezzouar)",
      priority = BackorderVorPriority.VOR_PONT_BLOQUE,
      expectedArrivalTime = "Livré au comptoir magasin",
      priceDzd = 31500,
      isReceivedAndAllocated = true
    ),
    BackorderVorItem(
      id = "VOR-03",
      oemReference = "41200-2D220",
      partDesignation = "Kit Double Embrayage 7-DCT Renforcé Hyundai/Kia",
      linkedRepairOrderNumber = "OR-2026-1099",
      clientName = "M. Sofiane Touati",
      vehicleModel = "Hyundai Tucson 1.7 CRDi 7-DCT",
      blockedBayLabel = null,
      supplierName = "Importateur Mobis Pièces Oran",
      priority = BackorderVorPriority.COMMANDE_FERME_CLIENT,
      expectedArrivalTime = "Demain matin 09h00",
      priceDzd = 138000,
      isReceivedAndAllocated = false
    )
  )
}





