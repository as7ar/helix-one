package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DbTestResult
import com.example.ui.theme.*
import com.example.viewmodel.HelixViewModel
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultDetailScreen(
    viewModel: HelixViewModel,
    result: DbTestResult,
    onBack: () -> Unit,
    onNavigateToHospital: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Parse the detailsJson safely
    val markersList = remember(result.detailsJson) {
        val list = mutableListOf<MarkerDetail>()
        try {
            val jsonArray = JSONArray(result.detailsJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    MarkerDetail(
                        marker = obj.optString("marker", "미상"),
                        category = obj.optString("category", "일반"),
                        result = obj.optString("result", "검출 안 됨"),
                        status = obj.optString("status", "정상"),
                        desc = obj.optString("desc", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback default
            list.add(MarkerDetail("EGFR T790M", "PCR 진단", "검출", "위험", "3세대 표적 치료제 관련"))
        }
        list
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "진단 보고서 상세",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = HelixDarkNavy
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = HelixDarkNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(HelixLightGray)
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // General info card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, HelixBorder, RoundedCornerShape(24.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(HelixBlueGlow)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${result.type} 유전자 분석",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HelixBlue
                                    )
                                )
                            }
                            Text(
                                text = "진단일: ${result.date}",
                                style = MaterialTheme.typography.bodySmall,
                                color = HelixBodyText
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = result.summary,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = HelixDarkNavy
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        HorizontalDivider(color = HelixBorder, thickness = 1.dp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val scoreColor = if (result.score < 70) HelixRed else HelixGreen
                            Text(
                                text = "종합 지수:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = HelixBodyText
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${result.score} / 100 점",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = scoreColor
                                )
                            )
                        }
                    }
                }
            }

            // CRITICAL DANGER ALERT & Reservation Trigger
            if (result.status == "위험") {
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = HelixRedBg),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, HelixRed.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = "위험",
                                    tint = HelixRed,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "가까운 시일 내 병원 진료 권장",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HelixRed
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "지속적인 표적 유전 변이 검출이 관측되었습니다. 전문 임상의를 통해 정밀 진단 및 표적 치료 로드맵 구성을 권유드립니다.",
                                style = MaterialTheme.typography.bodySmall,
                                color = HelixRed.copy(alpha = 0.9f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = onNavigateToHospital,
                                colors = ButtonDefaults.buttonColors(containerColor = HelixRed),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("hospital_reservation_button")
                            ) {
                                Text(
                                    text = "가까운 정밀 진료 병원 예약하기",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                // If normal, show next schedule
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = HelixGreenBg),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, HelixGreen.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "안정",
                                tint = HelixGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "정상 지수 감지됨 (검출 안 됨)",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HelixGreen
                                    )
                                )
                                Text(
                                    text = "지정된 바이오마커 변이 증폭이 없습니다. 정기 추적 관찰을 권장합니다.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = HelixGreen.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // Biomarkers List section title
            item {
                Text(
                    text = "세부 바이오마커 리스트",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = HelixDarkNavy,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )
            }

            // Iterate over markers
            items(markersList) { marker ->
                MarkerRowItem(marker)
            }
        }
    }
}

@Composable
fun MarkerRowItem(marker: MarkerDetail) {
    val statusColor = when (marker.status) {
        "위험" -> HelixRed
        "주의" -> HelixYellow
        else -> HelixGreen
    }
    
    val statusBg = when (marker.status) {
        "위험" -> HelixRedBg
        "주의" -> HelixYellowBg
        else -> HelixGreenBg
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, HelixBorder, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = marker.marker,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = HelixDarkNavy
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(HelixLightGray)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = marker.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = HelixBodyText
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = marker.desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = HelixBodyText
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = marker.result,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    )
                }
            }
        }
    }
}

data class MarkerDetail(
    val marker: String,
    val category: String,
    val result: String,
    val status: String,
    val desc: String
)
