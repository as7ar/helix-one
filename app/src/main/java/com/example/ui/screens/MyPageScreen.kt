package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import com.example.viewmodel.HelixViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(
    viewModel: HelixViewModel,
    onNavigateToResult: (DbTestResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val results by viewModel.testResults.collectAsState()
    val reservations by viewModel.reservations.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val testSchedule by viewModel.testSchedule.collectAsState()

    val patientName by viewModel.patientName.collectAsState()
    val patientCode by viewModel.patientCode.collectAsState()
    val patientBirth by viewModel.patientBirth.collectAsState()
    val patientGender by viewModel.patientGender.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }

    // Expandable section states
    var activeSubSection by remember { mutableStateOf(MyPageSection.NONE) }

    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(patientName) }
        var editCode by remember { mutableStateOf(patientCode) }
        var editBirth by remember { mutableStateOf(patientBirth) }
        var editGender by remember { mutableStateOf(patientGender) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("환자 프로필 수정", fontWeight = FontWeight.Bold, color = HelixDarkNavy) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("환자 성명") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HelixBlue)
                    )
                    OutlinedTextField(
                        value = editCode,
                        onValueChange = { editCode = it },
                        label = { Text("환자 식별 코드") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HelixBlue)
                    )
                    OutlinedTextField(
                        value = editBirth,
                        onValueChange = { editBirth = it },
                        label = { Text("생년월일 (YYYY.MM.DD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HelixBlue)
                    )
                    Column {
                        Text(
                            text = "성별",
                            style = MaterialTheme.typography.labelMedium,
                            color = HelixDarkNavy,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("남성", "여성", "기타").forEach { gender ->
                                val isSelected = editGender == gender
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) HelixBlue else HelixLightGray)
                                        .clickable { editGender = gender }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = gender,
                                        color = if (isSelected) Color.White else HelixDarkNavy,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editName.isNotBlank()) {
                            viewModel.savePatientProfile(
                                name = editName.trim(),
                                code = editCode.trim(),
                                birth = editBirth.trim(),
                                gender = editGender
                            )
                            showEditProfileDialog = false
                        }
                    }
                ) {
                    Text("저장", fontWeight = FontWeight.Bold, color = HelixBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("취소", color = HelixBodyText)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HelixLightGray)
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Clinical Profile Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HelixBorder, RoundedCornerShape(24.dp))
                    .clickable { showEditProfileDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(HelixBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        val initial = if (patientName.isNotEmpty()) patientName.first().toString() else "H"
                        Text(
                            text = initial,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (patientName.isNotEmpty()) "$patientName 환자" else "미등록 환자",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = HelixDarkNavy
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(HelixBlueGlow)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = patientGender,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = HelixBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                        Text(
                            text = "식별 코드: $patientCode",
                            style = MaterialTheme.typography.bodySmall,
                            color = HelixBodyText
                        )
                        if (patientBirth.isNotEmpty()) {
                            Text(
                                text = "생년월일: $patientBirth",
                                style = MaterialTheme.typography.bodySmall,
                                color = HelixBodyText
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "프로필 수정",
                        tint = HelixBlue.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Expanded view panel based on selection
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // SECTION 1: Notification Center
                MyPageAccordionHeader(
                    title = "알림 보관함 (${notifications.filter { !it.isRead }.size})",
                    icon = Icons.Filled.Notifications,
                    isSelected = activeSubSection == MyPageSection.NOTIFICATIONS,
                    onClick = {
                        activeSubSection = if (activeSubSection == MyPageSection.NOTIFICATIONS) MyPageSection.NONE else MyPageSection.NOTIFICATIONS
                    }
                )

                AnimatedVisibility(
                    visible = activeSubSection == MyPageSection.NOTIFICATIONS,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    NotificationCenterContent(
                        notifications = notifications,
                        onRead = { viewModel.readNotification(it) },
                        onClearAll = { viewModel.clearAllNotifications() }
                    )
                }

                // SECTION 2: Test Schedule Management
                MyPageAccordionHeader(
                    title = "추적 검사 일정 설정 (주기 관리)",
                    icon = Icons.Filled.CalendarMonth,
                    isSelected = activeSubSection == MyPageSection.SCHEDULE,
                    onClick = {
                        activeSubSection = if (activeSubSection == MyPageSection.SCHEDULE) MyPageSection.NONE else MyPageSection.SCHEDULE
                    }
                )

                AnimatedVisibility(
                    visible = activeSubSection == MyPageSection.SCHEDULE,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    ScheduleSettingsContent(
                        schedule = testSchedule,
                        onSelectInterval = { viewModel.updateScheduleInterval(it) }
                    )
                }

                // SECTION 3: Clinic Reservations List
                MyPageAccordionHeader(
                    title = "병원 예약 확인 내역 (${reservations.size})",
                    icon = Icons.Filled.LocalHospital,
                    isSelected = activeSubSection == MyPageSection.RESERVATIONS,
                    onClick = {
                        activeSubSection = if (activeSubSection == MyPageSection.RESERVATIONS) MyPageSection.NONE else MyPageSection.RESERVATIONS
                    }
                )

                AnimatedVisibility(
                    visible = activeSubSection == MyPageSection.RESERVATIONS,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    ReservationsContent(
                        reservations = reservations,
                        onCancel = { viewModel.cancelReservation(it) }
                    )
                }

                // SECTION 4: Diagnostic test results log
                MyPageAccordionHeader(
                    title = "검사 분석 이력 (${results.size})",
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    isSelected = activeSubSection == MyPageSection.HISTORY,
                    onClick = {
                        activeSubSection = if (activeSubSection == MyPageSection.HISTORY) MyPageSection.NONE else MyPageSection.HISTORY
                    }
                )

                AnimatedVisibility(
                    visible = activeSubSection == MyPageSection.HISTORY,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    HistoryResultsContent(
                        results = results,
                        onNavigate = onNavigateToResult
                    )
                }
            }
        }
    }
}

enum class MyPageSection {
    NONE, NOTIFICATIONS, SCHEDULE, RESERVATIONS, HISTORY
}

@Composable
fun MyPageAccordionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, HelixBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) HelixBlue else HelixDarkNavy,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = HelixDarkNavy
                )
            }

            Icon(
                imageVector = if (isSelected) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = HelixDarkNavy
            )
        }
    }
}

// 1. Notification list content
@Composable
fun NotificationCenterContent(
    notifications: List<DbNotification>,
    onRead: (Int) -> Unit,
    onClearAll: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, HelixBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "알림 수신 히스토리",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = HelixDarkNavy
                )
                if (notifications.isNotEmpty()) {
                    Text(
                        text = "전체 삭제",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = HelixRed
                        ),
                        modifier = Modifier.clickable { onClearAll() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (notifications.isEmpty()) {
                Text(
                    text = "새로운 알림이 없습니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = HelixBodyText,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    notifications.take(5).forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (item.isRead) Color.Transparent else HelixBlueGlow.copy(alpha = 0.3f))
                                .clickable { onRead(item.id) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(if (item.isRead) Color.Transparent else HelixBlue, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = HelixDarkNavy
                                )
                                Text(
                                    text = item.body,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HelixBodyText
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 2. Schedule configuration picker
@Composable
fun ScheduleSettingsContent(
    schedule: DbTestSchedule?,
    onSelectInterval: (Int) -> Unit
) {
    val intervalOptions = listOf(
        Pair(2, "2주 마다"),
        Pair(4, "매월 1회"),
        Pair(8, "2개월 마다"),
        Pair(12, "3개월 마다")
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, HelixBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "바이오마커 정기 추적 관찰 주기",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = HelixDarkNavy
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "종양 바이오마커 변이 증폭 여부를 일정 주기로 추적 진단하여 초기에 위험도를 예측 및 제어합니다.",
                style = MaterialTheme.typography.bodySmall,
                color = HelixBodyText
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Options buttons list
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                intervalOptions.forEach { opt ->
                    val isSelected = schedule?.intervalWeeks == opt.first
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) HelixBlueGlow else HelixLightGray)
                            .clickable { onSelectInterval(opt.first) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = opt.second,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) HelixBlue else HelixDarkNavy
                            )
                        )
                        if (isSelected) {
                            Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = HelixBlue)
                        }
                    }
                }
            }

            schedule?.let { sched ->
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = HelixBorder)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "차기 정기 검사 예정일:",
                        style = MaterialTheme.typography.bodySmall,
                        color = HelixBodyText
                    )
                    Text(
                        text = sched.nextTestDate,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = HelixBlue
                        )
                    )
                }
            }
        }
    }
}

// 3. Reservations display panel
@Composable
fun ReservationsContent(
    reservations: List<DbReservation>,
    onCancel: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, HelixBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "현재 확정된 외래 일정",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = HelixDarkNavy
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (reservations.isEmpty()) {
                Text(
                    text = "등록된 진료 예약 내역이 없습니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = HelixBodyText,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    reservations.forEach { res ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = HelixLightGray),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = res.hospitalName,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = HelixDarkNavy
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(HelixBlueGlow)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = res.status,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = HelixBlue,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "일정: ${res.date} (오전 ${res.time})",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = HelixBodyText
                                        )
                                        Text(
                                            text = "24시간 전 사전 푸시 리마인더 활성화",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = HelixGreen
                                        )
                                    }

                                    IconButton(
                                        onClick = { onCancel(res.id) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Cancel,
                                            contentDescription = "예약 취소",
                                            tint = HelixRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 4. Past historical tests
@Composable
fun HistoryResultsContent(
    results: List<DbTestResult>,
    onNavigate: (DbTestResult) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, HelixBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "종전 분자/유전 진단 분석 보관함",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = HelixDarkNavy
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (results.isEmpty()) {
                Text(
                    text = "저장된 과거 이력이 없습니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = HelixBodyText
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    results.forEach { res ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(HelixLightGray)
                                .clickable { onNavigate(res) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${res.date} - ${res.type} 정밀 분석",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = HelixDarkNavy
                                )
                                Text(
                                    text = res.summary,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HelixBodyText,
                                    maxLines = 1
                                )
                            }

                            val scoreColor = if (res.score < 70) HelixRed else HelixGreen
                            Text(
                                text = "${res.score}점",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black),
                                color = scoreColor
                            )
                        }
                    }
                }
            }
        }
    }
}
