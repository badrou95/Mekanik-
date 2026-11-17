package com.example.data.model

data class WorkshopClientAccount(
  val id: String,
  val plateNumber: String, // Matricule (e.g. "04291-120-16")
  val passwordPin: String, // Mot de passe ou code PIN à 4-6 caractères (e.g. "1234")
  val clientName: String,
  val clientPhone: String,
  val vehicleModel: String,
  val vin: String,
  val qrAccessCode: String, // e.g. "MEKANIK-CLIENT-04291-120-16"
  val workshopName: String = "Garage Auto Performance Alger",
  val workshopPhone: String = "021 55 44 33 / 0550 12 34 56",
  val workshopAddress: String = "Zone Industrielle Oued Smar, Alger (16)",
  val createdAt: String,
  val activeRepairOrder: RepairOrder? = null,
  val repairHistory: List<RepairOrder> = emptyList(),
  val invoices: List<FiscalInvoice> = emptyList(),
  val isFreeAccount: Boolean = true
)

data class VoiceFaultPreset(
  val title: String,
  val category: String,
  val transcribedText: String,
  val suggestedUrgency: String,
  val affectedSystem: String
)

object VoiceFaultPresetsProvider {
  val sampleVoicePresets = listOf(
    VoiceFaultPreset(
      title = "Claquement Distribution / Ralenti",
      category = "Moteur",
      transcribedText = "Bruit de claquement métallique sec côté courroie de distribution au ralenti à froid. Disparition partielle à chaud. Suspicion galet tendeur usé.",
      suggestedUrgency = "Urgent",
      affectedSystem = "Distribution & Courroies"
    ),
    VoiceFaultPreset(
      title = "Fumée Noire + Perte Puissance",
      category = "Injection / FAP",
      transcribedText = "Perte de puissance nette en côte au-delà de 2500 tr/min. Fumée noire à l'échappement et voyant moteur clignotant. Code défaut probable débitmètre ou vanne EGR encrassée.",
      suggestedUrgency = "Élevée",
      affectedSystem = "Admission & Échappement"
    ),
    VoiceFaultPreset(
      title = "Vibration au Freinage / Pédale molle",
      category = "Freinage",
      transcribedText = "Forte vibration dans le volant au freinage à plus de 80 km/h. Pédale de frein spongieuse en fin de course. Disques avant voilés et liquide de frein à purger.",
      suggestedUrgency = "Critique",
      affectedSystem = "Système de Freinage"
    ),
    VoiceFaultPreset(
      title = "Sifflement Turbo sous charge",
      category = "Suralimentation",
      transcribedText = "Sifflement aigu du turbo dès l'enclenchement de la charge moteur à 1800 tr/min. Durite d'échangeur d'air possiblement fissurée ou jeu dans l'axe de turbine.",
      suggestedUrgency = "Moyenne",
      affectedSystem = "Turbocompresseur"
    ),
    VoiceFaultPreset(
      title = "Claquement Train Avant Droite",
      category = "Liaison au Sol",
      transcribedText = "Bruit sourd 'clong' sur chaussée dégradée et franchissement de dos-d'âne côté passager avant. Silentbloc de triangle ou biellette de barre stabilisatrice avec jeu prononcé.",
      suggestedUrgency = "Normal",
      affectedSystem = "Suspension & Train Roulant"
    ),
    VoiceFaultPreset(
      title = "Climatisation Inefficace & Odeur",
      category = "Confort / Habitacle",
      transcribedText = "Absence d'air froid malgré l'activation du compresseur AC. Odeur d'humidité persistante dans l'habitacle. Recharge gaz R134a et remplacement filtre habitacle requis.",
      suggestedUrgency = "Normal",
      affectedSystem = "Climatisation & Chauffage"
    )
  )
}
