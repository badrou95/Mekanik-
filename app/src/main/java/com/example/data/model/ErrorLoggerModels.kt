package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class SystemErrorCategory(
  val id: String,
  val label: String,
  val shortTabLabel: String,
  val badgeText: String,
  val colorHex: Long
) {
  CRASH_RUNTIME(
    id = "CRASH_RUNTIME",
    label = "Plantages & Exceptions Non Gérées (Crash)",
    shortTabLabel = "💥 Plantages",
    badgeText = "💥 CRASH / PLANTAGE",
    colorHex = 0xFFE53935
  ),
  API_CONNECTION_FAILURE(
    id = "API_CONNECTION_FAILURE",
    label = "Problèmes de Connexion API (11 APIs & Gemini)",
    shortTabLabel = "🌐 Connexion API",
    badgeText = "🌐 ERREUR API",
    colorHex = 0xFFFF7A00
  ),
  LOCAL_PERSISTENCE_FAILURE(
    id = "LOCAL_PERSISTENCE_FAILURE",
    label = "Échecs Persistance Locale (Room AES-256) & Firebase",
    shortTabLabel = "💾 Persistance / Firebase",
    badgeText = "💾 PERSISTANCE & SYNC",
    colorHex = 0xFF00E5FF
  ),
  CLIENT_BUG_REPORT(
    id = "CLIENT_BUG_REPORT",
    label = "Rapports de Bugs Clients & Signalements Terrain",
    shortTabLabel = "📨 Bugs Clients",
    badgeText = "📨 RAPPORT CLIENT",
    colorHex = 0xFFAB47BC
  );

  companion object {
    fun fromId(raw: String?): SystemErrorCategory {
      return entries.firstOrNull { it.id.equals(raw, ignoreCase = true) } ?: API_CONNECTION_FAILURE
    }
  }
}

enum class SystemErrorSeverity(
  val id: String,
  val label: String,
  val badgeText: String,
  val colorHex: Long
) {
  CRITICAL("CRITICAL", "Critique (Bloquant)", "🚨 CRITIQUE", 0xFFE53935),
  HIGH("HIGH", "Élevée (Dégradé)", "🟠 ÉLEVÉE", 0xFFFF7A00),
  WARNING("WARNING", "Avertissement (Fallback actif)", "🟡 AVERTISSEMENT", 0xFFFFB300),
  INFO("INFO", "Diagnostic Info", "🔵 INFO", 0xFF00E5FF);

  companion object {
    fun fromId(raw: String?): SystemErrorSeverity {
      return entries.firstOrNull { it.id.equals(raw, ignoreCase = true) } ?: HIGH
    }
  }
}

enum class ErrorResolutionStatus(
  val id: String,
  val label: String,
  val badgeText: String,
  val colorHex: Long
) {
  OPEN("OPEN", "Ouvert • À traiter", "OUVERT", 0xFFE53935),
  AI_REPAIRING("AI_REPAIRING", "Auto-Réparation MekanikAI en cours...", "🤖 RÉPARATION IA...", 0xFF00E5FF),
  AUTO_REPAIRED_BY_AI("AUTO_REPAIRED_BY_AI", "Auto-Réparé par MekanikAI Guardian", "🤖 RÉPARÉ PAR IA ✓", 0xFF00E676),
  INVESTIGATING("INVESTIGATING", "En investigation SuperAdmin", "EN ANALYSE", 0xFFFFB300),
  RESOLVED("RESOLVED", "Corrigé & Résolu", "RÉSOLU ✓", 0xFF00E676),
  IGNORED("IGNORED", "Ignoré / Faux positif", "IGNORÉ", 0xFF90A4AE);

  companion object {
    fun fromId(raw: String?): ErrorResolutionStatus {
      return entries.firstOrNull { it.id.equals(raw, ignoreCase = true) } ?: OPEN
    }
  }
}

data class AiAutoRepairReport(
  val hotfixId: String = "HOTFIX-AI-2026",
  val aiEngineUsed: String = "MekanikAI Guardian • Gemini 2.5 Flash Self-Healing",
  val rootCauseAnalysis: String = "",
  val executedActions: List<String> = emptyList(),
  val generatedPatchSnippet: String = "",
  val confidenceScorePercent: Int = 98,
  val repairDurationMs: Long = 380L,
  val repairedAtFormatted: String = "À l'instant",
  val wasTriggeredByClientReport: Boolean = false
)

@IgnoreExtraProperties
data class FirestoreSystemErrorLog(
  @DocumentId val id: String = "",
  val userId: String = "",
  val userEmail: String = "badroumc@gmail.com",
  val rbacRole: String = FirebaseRbacRole.SUPER_ADMIN.id,
  val errorCategory: String = SystemErrorCategory.API_CONNECTION_FAILURE.id,
  val severity: String = SystemErrorSeverity.HIGH.id,
  val moduleSource: String = "Mekanik+ Core",
  val errorCode: String = "ERR_UNKNOWN",
  val errorTitle: String = "",
  val technicalMessage: String = "",
  val stackTracePreview: String = "",
  val deviceHardwareInfo: String = "Android 16 (API 36) • HW-KEYSTORE-DZ16-A94F21 • 16 - Alger",
  val resolutionStatus: String = ErrorResolutionStatus.OPEN.id,
  val superAdminNote: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
  @get:Exclude val aiAutoRepairReport: AiAutoRepairReport? = null
) {
  @get:Exclude
  val parsedCategory: SystemErrorCategory
    get() = SystemErrorCategory.fromId(errorCategory)

  @get:Exclude
  val parsedSeverity: SystemErrorSeverity
    get() = SystemErrorSeverity.fromId(severity)

  @get:Exclude
  val parsedStatus: ErrorResolutionStatus
    get() = ErrorResolutionStatus.fromId(resolutionStatus)

  @get:Exclude
  val isRepairedByAi: Boolean
    get() = parsedStatus == ErrorResolutionStatus.AUTO_REPAIRED_BY_AI ||
      aiAutoRepairReport != null ||
      superAdminNote.contains("[AUTO-REPAIR IA")

  @get:Exclude
  val formattedDate: String
    get() {
      val date = createdAt?.toDate() ?: updatedAt?.toDate() ?: Date()
      return try {
        SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRANCE).format(date)
      } catch (e: Exception) {
        "Aujourd'hui"
      }
    }
}

data class ClientBugReportPreset(
  val id: String,
  val title: String,
  val moduleSource: String,
  val defaultDescription: String,
  val suggestedSeverity: SystemErrorSeverity
)

data class ErrorLoggerUiState(
  val logs: List<FirestoreSystemErrorLog> = emptyList(),
  val selectedCategoryFilter: SystemErrorCategory? = null,
  val selectedSeverityFilter: SystemErrorSeverity? = null,
  val selectedStatusFilter: ErrorResolutionStatus? = null,
  val searchQuery: String = "",
  val isUncaughtHandlerInstalled: Boolean = true,
  val isFirebaseSyncActive: Boolean = true,
  val isAiAutoRepairEnabled: Boolean = true,
  val isAiRepairInProgress: Boolean = false,
  val activeAiRepairStepText: String = "MekanikAI Guardian 24/7 en veille active • Prêt à réparer tout crash ou bug client",
  val latestAiRepairReport: AiAutoRepairReport? = null,
  val lastSyncedTimestamp: String = "Synchronisé en temps réel (Firestore /system_error_logs)"
) {
  val filteredLogs: List<FirestoreSystemErrorLog>
    get() {
      val q = searchQuery.trim().lowercase()
      return logs.filter { log ->
        val matchesCat = selectedCategoryFilter == null || log.parsedCategory == selectedCategoryFilter
        val matchesSev = selectedSeverityFilter == null || log.parsedSeverity == selectedSeverityFilter
        val matchesStatus = selectedStatusFilter == null || log.parsedStatus == selectedStatusFilter
        val matchesQuery = q.isEmpty() ||
          log.errorCode.lowercase().contains(q) ||
          log.errorTitle.lowercase().contains(q) ||
          log.moduleSource.lowercase().contains(q) ||
          log.technicalMessage.lowercase().contains(q) ||
          log.stackTracePreview.lowercase().contains(q) ||
          log.userEmail.lowercase().contains(q) ||
          log.superAdminNote.lowercase().contains(q) ||
          log.deviceHardwareInfo.lowercase().contains(q)
        matchesCat && matchesSev && matchesStatus && matchesQuery
      }
    }

  val crashCount: Int
    get() = logs.count { it.parsedCategory == SystemErrorCategory.CRASH_RUNTIME }

  val apiFailureCount: Int
    get() = logs.count { it.parsedCategory == SystemErrorCategory.API_CONNECTION_FAILURE }

  val localPersistenceFailureCount: Int
    get() = logs.count { it.parsedCategory == SystemErrorCategory.LOCAL_PERSISTENCE_FAILURE }

  val clientBugReportsCount: Int
    get() = logs.count { it.parsedCategory == SystemErrorCategory.CLIENT_BUG_REPORT }

  val totalAiAutoRepairsCount: Int
    get() = logs.count { it.isRepairedByAi }

  val openCriticalCount: Int
    get() = logs.count {
      (it.parsedStatus == ErrorResolutionStatus.OPEN || it.parsedStatus == ErrorResolutionStatus.INVESTIGATING) &&
        (it.parsedSeverity == SystemErrorSeverity.CRITICAL || it.parsedSeverity == SystemErrorSeverity.HIGH)
    }

  val unresolvedCount: Int
    get() = logs.count {
      it.parsedStatus == ErrorResolutionStatus.OPEN || it.parsedStatus == ErrorResolutionStatus.INVESTIGATING
    }
}
