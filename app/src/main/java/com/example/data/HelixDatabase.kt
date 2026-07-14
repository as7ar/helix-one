package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HelixDao {
    // Test Results
    @Query("SELECT * FROM test_results ORDER BY id DESC")
    fun getAllTestResults(): Flow<List<DbTestResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestResult(result: DbTestResult)

    @Query("DELETE FROM test_results")
    suspend fun clearTestResults()

    // Reservations
    @Query("SELECT * FROM hospital_reservations ORDER BY timestamp DESC")
    fun getAllReservations(): Flow<List<DbReservation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: DbReservation)

    @Query("DELETE FROM hospital_reservations WHERE id = :id")
    suspend fun deleteReservationById(id: Int)

    // AI Summary Cache
    @Query("SELECT * FROM ai_summaries WHERE `key` = :key")
    suspend fun getAiSummary(key: String = "latest"): DbAiSummary?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiSummary(summary: DbAiSummary)

    // Test Schedule
    @Query("SELECT * FROM user_schedule WHERE id = 1")
    fun getTestSchedule(): Flow<DbTestSchedule?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestSchedule(schedule: DbTestSchedule)

    // Notifications
    @Query("SELECT * FROM helix_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<DbNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: DbNotification)

    @Query("UPDATE helix_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Int)

    @Query("DELETE FROM helix_notifications")
    suspend fun clearAllNotifications()
}

@Database(
    entities = [
        DbTestResult::class,
        DbReservation::class,
        DbAiSummary::class,
        DbTestSchedule::class,
        DbNotification::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HelixDatabase : RoomDatabase() {
    abstract fun helixDao(): HelixDao

    companion object {
        @Volatile
        private var INSTANCE: HelixDatabase? = null

        fun getDatabase(context: Context): HelixDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HelixDatabase::class.java,
                    "helix_one_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
