package com.example.data.model

data class MonthlyRepairStats(
  val monthKey: String, // e.g., "2026-01"
  val monthShort: String, // "Jan", "Fév", "Mar", etc.
  val monthFull: String, // "Janvier 2026", "Février 2026", etc.
  val totalRepairs: Int,
  val freinageRepairs: Int,
  val moteurTurboRepairs: Int,
  val distributionRepairs: Int,
  val climatisationRepairs: Int,
  val electriciteRepairs: Int,
  val vidangeRevisionRepairs: Int,
  val revenueDzd: Long,
  val partsRevenueDzd: Long,
  val laborRevenueDzd: Long,
  val laborHoursWorked: Float,
  val avgTicketDzd: Long,
  val vehiclesHandled: Int,
  val customerSatisfactionPct: Float,
  val diagnosticAiAssistedCount: Int
)

data class WorkshopKpiSummary(
  val year: Int,
  val totalRepairsYear: Int,
  val totalRevenueYearDzd: Long,
  val totalLaborHoursYear: Float,
  val averageTicketYearDzd: Long,
  val topSystemRepaired: String,
  val topSystemSharePct: Int,
  val averageInterventionTimeHours: Float,
  val satisfactionRatePct: Float,
  val activeTechniciansCount: Int,
  val monthlyGrowthPct: Float
)

data class SystemRepairBreakdown(
  val systemName: String,
  val categoryKey: String,
  val repairCount: Int,
  val percentage: Int,
  val totalRevenueDzd: Long,
  val colorHex: String,
  val iconTag: String
)

enum class ChartMetricType(val label: String, val unit: String) {
  REPAIRS_COUNT("Volume Réparations", "réparations"),
  REVENUE_DZD("Chiffre d'Affaires", "DA"),
  LABOR_HOURS("Heures Atelier", "heures"),
  AVG_TICKET("Panier Moyen", "DA")
}

enum class ChartViewPeriod(val label: String) {
  FULL_YEAR("Année 2026 (12 Mois)"),
  LAST_6_MONTHS("6 Derniers Mois"),
  CURRENT_QUARTER("Trimestre Actuel (T3)")
}

data class CompanionWorkTimeAllocation(
  val id: String,
  val companionName: String,
  val shortName: String,
  val matricule: String,
  val specialty: String,
  val bayName: String,
  val productiveRepairHours: Float,
  val diagnosticAndTestHours: Float,
  val waitingPartsHours: Float,
  val internalMaintenanceHours: Float,
  val soldBaremeHours: Float
) {
  val totalClockedHours: Float
    get() = productiveRepairHours + diagnosticAndTestHours + waitingPartsHours + internalMaintenanceHours

  val productiveBillableHours: Float
    get() = productiveRepairHours + diagnosticAndTestHours

  val productivityRatePct: Int
    get() = if (totalClockedHours <= 0f) 100 else ((soldBaremeHours / totalClockedHours) * 100f).toInt()

  val productiveSharePct: Int
    get() = if (totalClockedHours <= 0f) 0 else ((productiveRepairHours / totalClockedHours) * 100f).toInt()

  val diagnosticSharePct: Int
    get() = if (totalClockedHours <= 0f) 0 else ((diagnosticAndTestHours / totalClockedHours) * 100f).toInt()

  val waitingPartsSharePct: Int
    get() = if (totalClockedHours <= 0f) 0 else ((waitingPartsHours / totalClockedHours) * 100f).toInt()

  val internalSharePct: Int
    get() = (100 - productiveSharePct - diagnosticSharePct - waitingPartsSharePct).coerceAtLeast(0)
}

data class WeeklyProductivityDayPoint(
  val dayCode: String, // "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"
  val dayFullLabel: String,
  val soldBaremeHours: Float,
  val clockedRealHours: Float,
  val productivityPct: Int,
  val targetPct: Int = 100,
  val vehiclesCompleted: Int,
  val karimPct: Int,
  val yacinePct: Int,
  val sofianePct: Int,
  val aminePct: Int
)
