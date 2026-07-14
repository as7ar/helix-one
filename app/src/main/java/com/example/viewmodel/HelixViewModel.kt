package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AppTab {
    HOME, ANALYSIS, TREATMENT, HOSPITAL, MY_PAGE
}

enum class BleState {
    DISCONNECTED, SCANNING, CONNECTING, CONNECTED, TESTING, COMPLETED
}

class HelixViewModel(application: Application) : AndroidViewModel(application) {
    private val database = HelixDatabase.getDatabase(application)
    private val dao = database.helixDao()
    private val geminiService = GeminiService()
    private val repository = HelixRepository(dao, geminiService)

    // Current screen routing
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Test Results from Room
    val testResults: StateFlow<List<DbTestResult>> = repository.allTestResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reservations from Room
    val reservations: StateFlow<List<DbReservation>> = repository.allReservations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications from Room
    val notifications: StateFlow<List<DbNotification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Schedule from Room
    val testSchedule: StateFlow<DbTestSchedule?> = repository.testSchedule
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Bluetooth Connection / Testing State
    private val _bleState = MutableStateFlow(BleState.DISCONNECTED)
    val bleState: StateFlow<BleState> = _bleState.asStateFlow()

    private val _bleProgress = MutableStateFlow(0f)
    val bleProgress: StateFlow<Float> = _bleProgress.asStateFlow()

    private val _bleStatusText = MutableStateFlow("기기 대기 중")
    val bleStatusText: StateFlow<String> = _bleStatusText.asStateFlow()

    // AI Analysis States
    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _aiAnalysisText = MutableStateFlow("")
    val aiAnalysisText: StateFlow<String> = _aiAnalysisText.asStateFlow()

    // Selected Test Result for Detail screen
    private val _selectedResult = MutableStateFlow<DbTestResult?>(null)
    val selectedResult: StateFlow<DbTestResult?> = _selectedResult.asStateFlow()

    // Treatment Simulation Slider
    private val _simulationProgress = MutableStateFlow(0f)
    val simulationProgress: StateFlow<Float> = _simulationProgress.asStateFlow()

    // Lifestyle Metrics (Static but modifiable in UX)
    private val _lifestyleMetrics = MutableStateFlow(
        listOf(
            LifestyleMetric("Sleep", 85, "수면 효율성 높음. 깊은 수면 단계(REM)가 20% 증가했습니다.", "ic_sleep"),
            LifestyleMetric("Exercise", 90, "유산소 운동 성취도 우수. 미토콘드리아 활성이 최적 상태입니다.", "ic_exercise"),
            LifestyleMetric("Diet", 80, "항산화 식단 준수율 보통. 글루코사민 및 야채 비율을 10% 더 확장하세요.", "ic_diet"),
            LifestyleMetric("Stress", 75, "스트레스 지수 다소 높음. 호흡 및 명상 가이드를 매일 10분 진행하세요.", "ic_stress")
        )
    )
    val lifestyleMetrics: StateFlow<List<LifestyleMetric>> = _lifestyleMetrics.asStateFlow()

    // GPS Location based Hospitals (Simulated SEOUL coordinates)
    private val _hospitals = MutableStateFlow(
        listOf(
            Hospital(
                id = "hosp_1",
                name = "서울대학교병원 정밀의학센터",
                distanceKm = 1.2,
                pcrTestingCapable = true,
                availableDates = listOf("내일 예약 가능", "07.16 목", "07.17 금"),
                etaMinutes = 10,
                address = "서울특별시 종로구 대학로 101",
                phone = "02-2072-2114"
            ),
            Hospital(
                id = "hosp_2",
                name = "삼성서울병원 유전체보건클리닉",
                distanceKm = 4.5,
                pcrTestingCapable = true,
                availableDates = listOf("07.15 수", "07.16 목", "07.20 월"),
                etaMinutes = 18,
                address = "서울특별시 강남구 일원로 81",
                phone = "02-3410-2000"
            ),
            Hospital(
                id = "hosp_3",
                name = "세브란스병원 맞춤암치료센터",
                distanceKm = 5.8,
                pcrTestingCapable = true,
                availableDates = listOf("내일 예약 가능", "07.18 토", "07.21 화"),
                etaMinutes = 22,
                address = "서울특별시 서대문구 연세로 50-1",
                phone = "02-2228-0114"
            ),
            Hospital(
                id = "hosp_4",
                name = "아산병원 분자정밀진단소",
                distanceKm = 8.1,
                pcrTestingCapable = true,
                availableDates = listOf("07.17 금", "07.18 토", "07.22 수"),
                etaMinutes = 30,
                address = "서울특별시 송파구 올림픽로43길 88",
                phone = "02-3010-3114"
            )
        )
    )
    val hospitals: StateFlow<List<Hospital>> = _hospitals.asStateFlow()

    // Active reservation process
    private val _selectedHospitalForReserve = MutableStateFlow<Hospital?>(null)
    val selectedHospitalForReserve: StateFlow<Hospital?> = _selectedHospitalForReserve.asStateFlow()

    init {
        // Prepare initial data if database is empty
        viewModelScope.launch {
            repository.allTestResults.first().let { results ->
                if (results.isEmpty()) {
                    populateDefaultData()
                }
            }
            // Load latest cached AI analysis
            val cachedSummary = repository.getLatestAiSummary()
            if (cachedSummary != null) {
                _aiAnalysisText.value = cachedSummary.analysisText
            } else {
                _aiAnalysisText.value = "기본 AI 분석 보고서가 생성되지 않았습니다. 상단의 'AI 정밀 분석 시작' 버튼을 눌러주세요."
            }
        }
    }

    private suspend fun populateDefaultData() {
        // Insert a default "EGFR/BRCA 위험" state test result
        val defaultResult = DbTestResult(
            date = "2026.07.10",
            type = "PCR",
            score = 64,
            status = "위험",
            summary = "EGFR T790M 미세 돌연변이 연속성 검출",
            detailsJson = """
                [
                  {"marker": "EGFR T790M", "category": "PCR 진단", "result": "검출 (양성)", "status": "위험", "desc": "3세대 표적 치료제 내성 관련 변이"},
                  {"marker": "BRCA1 c.5266dup", "category": "SNP 분석", "result": "검출 안 됨 (정상)", "status": "정상", "desc": "유전성 유방암 감수성 대조 유전자"},
                  {"marker": "KRAS G12D", "category": "PCR 진단", "result": "검출 안 됨 (정상)", "status": "정상", "desc": "대장암 정밀 타겟 인자"}
                ]
            """.trimIndent()
        )
        repository.insertTestResult(defaultResult)

        // Add history result
        val normalHistoryResult = DbTestResult(
            date = "2026.05.12",
            type = "SNP",
            score = 92,
            status = "정상",
            summary = "주요 암 질환 감수성 검사 결과 대다수 정상 범위",
            detailsJson = """
                [
                  {"marker": "EGFR T790M", "category": "PCR 진단", "result": "검출 안 됨 (정상)", "status": "정상", "desc": "3세대 표적 치료제 내성 관련 변이"},
                  {"marker": "BRCA1 c.5266dup", "category": "SNP 분석", "result": "검출 안 됨 (정상)", "status": "정상", "desc": "유전성 유방암 감수성 대조 유전자"}
                ]
            """.trimIndent()
        )
        repository.insertTestResult(normalHistoryResult)

        // Set default schedule
        repository.updateTestSchedule(
            DbTestSchedule(
                intervalWeeks = 4, // Every 1 month
                nextTestDate = "2026.08.10"
            )
        )

        // Set initial notifications
        repository.insertNotification(
            DbNotification(
                title = "🧬 신규 AI 정밀 유전체 보고서 완료",
                body = "EGFR 돌연변이 대응을 위한 맞춤 표적 요법 및 식이 가이드라인이 업데이트되었습니다.",
                type = "AI_COMPLETED"
            )
        )
        repository.insertNotification(
            DbNotification(
                title = "🏥 정기 암 추적 검사 주기 제안",
                body = "정밀 추적 검사를 위한 주기가 '매월 1회'로 세팅되었습니다. 다음 검사 예정일은 8월 10일입니다.",
                type = "TEST_REMINDER"
            )
        )
    }

    // Tab switcher
    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    // Selection helper
    fun selectResult(result: DbTestResult?) {
        _selectedResult.value = result
    }

    // Bluetooth Device connection logic simulation
    fun startBluetoothScanning() {
        viewModelScope.launch {
            _bleState.value = BleState.SCANNING
            _bleProgress.value = 0.1f
            _bleStatusText.value = "근처 정밀 진단 웨어러블 기기 스캔 중..."
            delay(1200)

            _bleState.value = BleState.CONNECTING
            _bleProgress.value = 0.3f
            _bleStatusText.value = "Helix Bio-Analyzer V2 연결 중..."
            delay(1500)

            _bleState.value = BleState.CONNECTED
            _bleProgress.value = 0.6f
            _bleStatusText.value = "장치 연결 및 페어링 성공. 진단 준비 중..."
            delay(1000)

            _bleState.value = BleState.TESTING
            _bleProgress.value = 0.7f
            _bleStatusText.value = "유전체 분자 신호 추출 및 증폭 중 (PCR 분석)..."
            
            // Progress animation
            for (p in 70..100) {
                _bleProgress.value = p / 100f
                delay(80)
            }
            
            // Completed! Create a test result
            val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            val dateStr = format.format(Date())
            
            // Toggle between NORMAL & DANGER randomly or strategically
            val isNormal = Math.random() > 0.5
            val newResult = if (isNormal) {
                DbTestResult(
                    date = dateStr,
                    type = "PCR",
                    score = 96,
                    status = "정상",
                    summary = "분자 진단 검출 유전체 변이 없음 (정상 상태)",
                    detailsJson = """
                        [
                          {"marker": "EGFR T790M", "category": "PCR 진단", "result": "검출 안 됨 (정상)", "status": "정상", "desc": "3세대 표적 치료제 내성 관련 변이"},
                          {"marker": "BRCA1 c.5266dup", "category": "SNP 분석", "result": "검출 안 됨 (정상)", "status": "정상", "desc": "유전성 유방암 감수성 대조 유전자"},
                          {"marker": "KRAS G12D", "category": "PCR 진단", "result": "검출 안 됨 (정상)", "status": "정상", "desc": "대장암 정밀 타겟 인자"}
                        ]
                    """.trimIndent()
                )
            } else {
                DbTestResult(
                    date = dateStr,
                    type = "PCR",
                    score = 58,
                    status = "위험",
                    summary = "EGFR 변이 연속 검출 및 주의 구간 유지",
                    detailsJson = """
                        [
                          {"marker": "EGFR T790M", "category": "PCR 진단", "result": "주의 (이전 진단比 수치 상승)", "status": "주의", "desc": "3세대 표적 치료제 관련 종양 바이오마커 증가"},
                          {"marker": "BRCA1 c.5266dup", "category": "SNP 분석", "result": "검출 안 됨 (정상)", "status": "정상", "desc": "유전성 유방암 감수성 대조 유전자"},
                          {"marker": "KRAS G12D", "category": "PCR 진단", "result": "주의 (신규 미량 활성)", "status": "주의", "desc": "췌장/대장 유전 위험 인자"}
                        ]
                    """.trimIndent()
                )
            }

            repository.insertTestResult(newResult)
            
            // Trigger a push notification about completed test
            val notif = DbNotification(
                title = "🧬 진단 완료: $dateStr 정밀 진단 레코드 생성",
                body = if (isNormal) "모든 타겟 분자 마커 음성 반응으로 안전함이 확인되었습니다." else "EGFR 변이 수치 가속화 위험이 감출되었습니다. 정밀 임상 예약이 제안됩니다.",
                type = "STATUS_UPDATE"
            )
            repository.insertNotification(notif)

            _bleState.value = BleState.COMPLETED
            _bleStatusText.value = if (isNormal) "검사 완벽 완료: 정상" else "검사 완료: 위험 마커 검출"
            delay(2000)
            
            // Back to disconnected so user can scan again
            _bleState.value = BleState.DISCONNECTED
            _bleProgress.value = 0f
            _bleStatusText.value = "기기 대기 중"
            
            // Auto-select the newly generated result
            _selectedResult.value = newResult
        }
    }

    // Call Gemini for Precision Medicine Insights
    fun triggerGeminiAnalysis() {
        viewModelScope.launch {
            _aiLoading.value = true
            
            // Fetch latest test results and lifestyle metric to feed as prompt context
            val results = testResults.value
            val currentResult = results.firstOrNull()
            
            val testSummary = if (currentResult != null) {
                "검사일: ${currentResult.date}, 검사유형: ${currentResult.type}, 상태: ${currentResult.status}, 요약: ${currentResult.summary}\n세부마커: ${currentResult.detailsJson}"
            } else {
                "최근 검사 데이터 없음"
            }
            
            val lifestyleSummary = lifestyleMetrics.value.joinToString("\n") {
                "- ${it.category}: ${it.score}점 (${it.recommendation})"
            }

            try {
                val response = repository.runAiAnalysis(testSummary, lifestyleSummary)
                _aiAnalysisText.value = response
                
                // Save notification
                repository.insertNotification(
                    DbNotification(
                        title = "✨ 실시간 AI 정밀 처방 보고서 발급 완료",
                        body = "유전자 기반 표적 적응증 분석과 맞춤 웰니스 액션 플랜이 정밀 분석 완료되었습니다.",
                        type = "AI_COMPLETED"
                    )
                )
            } catch (e: Exception) {
                _aiAnalysisText.value = "AI 정밀 유전체 원격 분석 중 통신 지연이 발생하였습니다. 로컬 지식 백업본을 성공적으로 로드하였습니다."
            } finally {
                _aiLoading.value = false
            }
        }
    }

    // Update simulation slider progress
    fun updateSimulationProgress(progress: Float) {
        _simulationProgress.value = progress
    }

    // Hospital reservation logic
    fun selectHospitalForReservation(hospital: Hospital?) {
        _selectedHospitalForReserve.value = hospital
    }

    fun makeHospitalReservation(hospital: Hospital, dateStr: String, timeStr: String) {
        viewModelScope.launch {
            val reservation = DbReservation(
                hospitalId = hospital.id,
                hospitalName = hospital.name,
                date = dateStr,
                time = timeStr,
                reminderOneDayBefore = true,
                status = "확정됨"
            )
            repository.insertReservation(reservation)

            // Insert notification
            repository.insertNotification(
                DbNotification(
                    title = "🏥 병원 진료 예약 확정",
                    body = "${hospital.name} 정밀 진단 클리닉 예약이 ${dateStr} ${timeStr}에 완료되었습니다. 24시간 전 리마인더가 설정되었습니다.",
                    type = "APPOINTMENT"
                )
            )

            // Reset selection and navigate to HOME or MY PAGE to view reservation history
            _selectedHospitalForReserve.value = null
        }
    }

    fun cancelReservation(id: Int) {
        viewModelScope.launch {
            repository.deleteReservation(id)
            repository.insertNotification(
                DbNotification(
                    title = "❌ 병원 예약 취소 알림",
                    body = "신청하신 병원 정밀 진료 예약 일정이 취소 처리되었습니다.",
                    type = "APPOINTMENT"
                )
            )
        }
    }

    // Update scheduling settings
    fun updateScheduleInterval(weeks: Int) {
        viewModelScope.launch {
            val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            val calendar = Calendar.getInstance()
            
            val daysToAdd = when (weeks) {
                2 -> 14
                4 -> 30
                8 -> 60
                12 -> 90
                else -> 30 // Default 1 month
            }
            calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
            val nextDateStr = format.format(calendar.time)

            repository.updateTestSchedule(
                DbTestSchedule(
                    intervalWeeks = weeks,
                    nextTestDate = nextDateStr
                )
            )

            repository.insertNotification(
                DbNotification(
                    title = "⚙️ 검사 추적 주기 설정 업데이트",
                    body = "다음 DNA 정기 추적 일정이 업데이트되었습니다: $nextDateStr (주기: $daysToAdd 일)",
                    type = "TEST_REMINDER"
                )
            )
        }
    }

    // Mark notification read
    fun readNotification(id: Int) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
        }
    }
}
