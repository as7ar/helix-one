package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HelixRepository(
    private val helixDao: HelixDao,
    private val geminiService: GeminiService
) {
    val allTestResults: Flow<List<DbTestResult>> = helixDao.getAllTestResults()
    val allReservations: Flow<List<DbReservation>> = helixDao.getAllReservations()
    val testSchedule: Flow<DbTestSchedule?> = helixDao.getTestSchedule()
    val allNotifications: Flow<List<DbNotification>> = helixDao.getAllNotifications()

    suspend fun insertTestResult(result: DbTestResult) = withContext(Dispatchers.IO) {
        helixDao.insertTestResult(result)
    }

    suspend fun clearTestResults() = withContext(Dispatchers.IO) {
        helixDao.clearTestResults()
    }

    suspend fun insertReservation(reservation: DbReservation) = withContext(Dispatchers.IO) {
        helixDao.insertReservation(reservation)
    }

    suspend fun deleteReservation(id: Int) = withContext(Dispatchers.IO) {
        helixDao.deleteReservationById(id)
    }

    suspend fun getLatestAiSummary(): DbAiSummary? = withContext(Dispatchers.IO) {
        helixDao.getAiSummary("latest")
    }

    suspend fun insertAiSummary(summary: DbAiSummary) = withContext(Dispatchers.IO) {
        helixDao.insertAiSummary(summary)
    }

    suspend fun updateTestSchedule(schedule: DbTestSchedule) = withContext(Dispatchers.IO) {
        helixDao.insertTestSchedule(schedule)
    }

    suspend fun insertNotification(notification: DbNotification) = withContext(Dispatchers.IO) {
        helixDao.insertNotification(notification)
    }

    suspend fun markNotificationRead(id: Int) = withContext(Dispatchers.IO) {
        helixDao.markNotificationAsRead(id)
    }

    suspend fun clearAllNotifications() = withContext(Dispatchers.IO) {
        helixDao.clearAllNotifications()
    }

    // Call Gemini to generate a tailored precision analysis and store it in database
    suspend fun runAiAnalysis(
        testResultsSummary: String,
        lifestyleSummary: String
    ): String = withContext(Dispatchers.IO) {
        val analysis = geminiService.analyzeHealthData(testResultsSummary, lifestyleSummary)
        helixDao.insertAiSummary(
            DbAiSummary(
                key = "latest",
                analysisText = analysis,
                riskLevelText = "위험 (정밀 모니터링 요망)",
                recommendationsJson = "[]",
                timestamp = System.currentTimeMillis()
            )
        )
        analysis
    }
}
