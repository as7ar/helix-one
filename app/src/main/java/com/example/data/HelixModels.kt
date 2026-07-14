package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Room Persistence Entities
@Entity(tableName = "test_results")
data class DbTestResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val type: String, // "PCR" or "SNP"
    val score: Int,
    val status: String, // "정상", "주의", "위험"
    val summary: String,
    val detailsJson: String // Detailed mutation findings as JSON
)

@Entity(tableName = "hospital_reservations")
data class DbReservation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hospitalId: String,
    val hospitalName: String,
    val date: String, // YYYY.MM.DD
    val time: String, // HH:MM
    val reminderOneDayBefore: Boolean = true,
    val status: String = "확정됨", // "확정됨", "완료됨", "취소됨"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_summaries")
data class DbAiSummary(
    @PrimaryKey val key: String = "latest",
    val analysisText: String,
    val riskLevelText: String,
    val recommendationsJson: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_schedule")
data class DbTestSchedule(
    @PrimaryKey val id: Int = 1,
    val intervalWeeks: Int, // 2, 4 (1m), 8 (2m), 12 (3m), -1 (Doctor), -2 (Custom)
    val customDays: Int = 0,
    val nextTestDate: String
)

@Entity(tableName = "helix_notifications")
data class DbNotification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val body: String,
    val type: String, // "TEST_REMINDER", "APPOINTMENT", "AI_COMPLETED", "STATUS_UPDATE"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

// UI Presentation Models
data class Hospital(
    val id: String,
    val name: String,
    val distanceKm: Double,
    val pcrTestingCapable: Boolean,
    val availableDates: List<String>,
    val etaMinutes: Int,
    val address: String,
    val phone: String
)

data class LifestyleMetric(
    val category: String, // "Sleep", "Exercise", "Diet", "Stress"
    val score: Int,
    val recommendation: String,
    val iconName: String
)

data class TreatmentRoadmapItem(
    val stage: String, // "Today", "2 weeks", "3 months", etc.
    val title: String,
    val description: String,
    val isCompleted: Boolean
)

// --- Gemini API Moshi-compatible Models ---

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null,
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    val responseMimeType: String? = null,
    val temperature: Double? = null,
    val responseSchema: Map<String, Any>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent?
)
