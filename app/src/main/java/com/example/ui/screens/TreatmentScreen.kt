package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.HelixViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TreatmentScreen(
    viewModel: HelixViewModel,
    modifier: Modifier = Modifier
) {
    val simProgress by viewModel.simulationProgress.collectAsState()

    // Mock Roadmap items
    val roadmapItems = remember {
        listOf(
            RoadmapItem("오늘 (Today)", "PCR 분석 완료", "EGFR T790M 및 주요 분자 진단 정보 수집 완료", true),
            RoadmapItem("2주 후", "맞춤형 표적 요법 개시", "3세대 표적 약제 복용 및 1차 면역 체계 정렬", false),
            RoadmapItem("3개월 후", "정밀 MRI 추적 검사", "종양 부위의 체적 변화 및 감쇠 추이 관찰", false),
            RoadmapItem("6개월 후", "임상 치료 효과 종합 평가", "유전체 변이 잔존율 분석 및 표적 설계 재평가", false),
            RoadmapItem("1년 후", "건강 목표 최종 달성", "세포 완전 관해 및 일상 완전성 유지 관리", false)
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HelixLightGray)
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Title Header
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = "정밀 표적 치료 설계",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = HelixDarkNavy
                )
                Text(
                    text = "유전자 타겟 매칭 치료 처방전 및 진척도 시뮬레이션",
                    style = MaterialTheme.typography.bodySmall,
                    color = HelixBodyText
                )
            }
        }

        // 1. Personalized Treatment Recommendation Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HelixBorder, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Healing,
                                contentDescription = null,
                                tint = HelixBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "추천 최적 표적 항암 요법",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = HelixDarkNavy
                            )
                        }

                        // Confidence badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(HelixBlueGlow)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "적합도 98%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HelixBlue
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "3세대 EGFR 표적 티로신 키나아제 억제제 요법",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = HelixDarkNavy
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "환자의 유전체 검사에서 검출된 EGFR T790M 돌연변이에 직접 결합하여 선택적으로 종양 수용체 활성화를 차단하는 3세대 표적 요법입니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HelixBodyText
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(color = HelixBorder, thickness = 1.dp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        RecommendationReasonRow(
                            label = "유전자 돌연변이 매치",
                            value = "EGFR T790M 시퀀스 정합 판별 완료"
                        )
                        RecommendationReasonRow(
                            label = "환자 기초 대사 수준",
                            value = "간 기능 및 신장 여과 기능 적합 (Grade 1)"
                        )
                        RecommendationReasonRow(
                            label = "최신 글로벌 임상 매핑",
                            value = "FDA 표적 가이드라인과 98% 조화성 확보"
                        )
                    }
                }
            }
        }

        // 2. Interactive Treatment Simulation Slider
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HelixBorder, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "표적 예후 인터랙티브 시뮬레이터",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = HelixDarkNavy,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "아래 슬라이더를 밀어 치료 전과 치료 후의 종양 바이오마커 완화 시각화 대조를 실행할 수 있습니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = HelixBodyText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated 3D-feeling Medical Graphics Canvas
                    SimulationCanvas(progress = simProgress)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Slider
                    Slider(
                        value = simProgress,
                        onValueChange = { viewModel.updateSimulationProgress(it) },
                        colors = SliderDefaults.colors(
                            thumbColor = HelixBlue,
                            activeTrackColor = HelixBlue,
                            inactiveTrackColor = HelixBlueGlow
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "치료 시작 전 (Before)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = HelixRed
                        )
                        Text(
                            text = "표적 치료 완료 (After)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = HelixGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Simulated Metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SimulationMetricCard(
                            label = "암세포 사멸 감쇠율",
                            value = "${(simProgress * 85).toInt()}%",
                            color = HelixBlue,
                            modifier = Modifier.weight(1f)
                        )
                        SimulationMetricCard(
                            label = "전반 예후 회복 점수",
                            value = "${(70 + (simProgress * 24)).toInt()}점",
                            color = HelixGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 3. Treatment Roadmap (Vertical Timeline UI)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HelixBorder, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "12달 정밀 메디컬 맵",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = HelixDarkNavy
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // Vertical Timeline Loop
                    roadmapItems.forEachIndexed { index, item ->
                        RoadmapTimelineRow(
                            item = item,
                            isLast = index == roadmapItems.size - 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationReasonRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = HelixBodyText
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = HelixDarkNavy
        )
    }
}

@Composable
fun SimulationCanvas(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(HelixDarkNavy)
            .border(1.dp, HelixBorder.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "sim_glow")
        val pulseFactor by infiniteTransition.animateFloat(
            initialValue = 0.85f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f

            // Draw radial clinical scanning grid
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = 70.dp.toPx(),
                style = Stroke(width = 1f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.03f),
                radius = 45.dp.toPx(),
                style = Stroke(width = 1f)
            )

            // Draw before cells: Red hot nodes, fading as progress increases
            val beforeAlpha = (1f - progress).coerceIn(0.1f, 1f)
            val beforeRadius = (35.dp.toPx() * (1f - progress * 0.85f) * pulseFactor).coerceAtLeast(8.dp.toPx())
            
            if (progress < 0.95f) {
                // Main tumor representation
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(HelixRed.copy(alpha = 0.5f * beforeAlpha), Color.Transparent),
                        center = Offset(centerX, centerY),
                        radius = beforeRadius * 1.5f
                    ),
                    radius = beforeRadius * 1.5f
                )
                drawCircle(
                    color = HelixRed.copy(alpha = 0.8f * beforeAlpha),
                    radius = beforeRadius,
                    center = Offset(centerX, centerY)
                )

                // Surrounding active disease markers
                val satellites = 5
                for (i in 0 until satellites) {
                    val angle = Math.toRadians((i * (360 / satellites) + (progress * 45f)).toDouble()).toFloat()
                    val dist = beforeRadius * 1.2f
                    val satX = centerX + dist * cos(angle)
                    val satY = centerY + dist * sin(angle)
                    drawCircle(
                        color = HelixRed.copy(alpha = 0.9f * beforeAlpha),
                        radius = 4.dp.toPx(),
                        center = Offset(satX, satY)
                    )
                }
            }

            // Draw after cells: Healthy green regenerative cells, expanding as progress increases
            val afterAlpha = progress.coerceIn(0.1f, 1f)
            val afterRadius = (25.dp.toPx() * progress * pulseFactor)

            if (progress > 0.05f) {
                // Healthy mitochondria nodes
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(HelixGreen.copy(alpha = 0.4f * afterAlpha), Color.Transparent),
                        center = Offset(centerX, centerY),
                        radius = afterRadius * 1.8f
                    ),
                    radius = afterRadius * 1.8f
                )

                // Draw tiny healthy cells spreading
                val healthyCount = 8
                for (i in 0 until healthyCount) {
                    val angle = Math.toRadians((i * (360 / healthyCount) - (progress * 30f)).toDouble()).toFloat()
                    val dist = 50.dp.toPx() * progress
                    val hX = centerX + dist * cos(angle)
                    val hY = centerY + dist * sin(angle)
                    
                    drawCircle(
                        color = HelixGreen.copy(alpha = 0.7f * afterAlpha),
                        radius = 3.dp.toPx(),
                        center = Offset(hX, hY)
                    )
                }
            }
        }

        // Overlay text labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "종양 바이오마커 시퀀스 매핑",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.5f))
            )
            Text(
                text = if (progress < 0.5f) "병변 활동 구역 검출" else "유전자 억제 활성 상태",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (progress < 0.5f) HelixRed else HelixGreen,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun SimulationMetricCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(HelixLightGray)
            .border(1.dp, HelixBorder, RoundedCornerShape(16.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = HelixBodyText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = color
                )
            )
        }
    }
}

// Vertical Roadmap Timeline Row
@Composable
fun RoadmapTimelineRow(item: RoadmapItem, isLast: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Vertical timeline indicators
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (item.isCompleted) HelixBlue else HelixBorder),
                contentAlignment = Alignment.Center
            ) {
                if (item.isCompleted) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color.White, CircleShape)
                    )
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(55.dp)
                        .background(if (item.isCompleted) HelixBlue else HelixBorder)
                )
            }
        }

        // Timeline description content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.stage,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (item.isCompleted) HelixBlue else HelixBodyText
            )
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = HelixDarkNavy
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = HelixBodyText,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )
        }
    }
}

data class RoadmapItem(
    val stage: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean
)
