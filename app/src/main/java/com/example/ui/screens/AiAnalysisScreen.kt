package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.HelixViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AiAnalysisScreen(
    viewModel: HelixViewModel,
    modifier: Modifier = Modifier
) {
    val aiLoading by viewModel.aiLoading.collectAsState()
    val aiText by viewModel.aiAnalysisText.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HelixLightGray)
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Top Brand Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AI 정밀 유전 분석",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = HelixDarkNavy
                    )
                    Text(
                        text = "Google Gemini 기반 인공지능 표적 인자 및 건강 예측",
                        style = MaterialTheme.typography.bodySmall,
                        color = HelixBodyText
                    )
                }

                IconButton(
                    onClick = { viewModel.triggerGeminiAnalysis() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, HelixBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "새로고침",
                        tint = HelixDarkNavy
                    )
                }
            }
        }

        // Futuristic AI network glowing animation
        item {
            AiNetworkVisualizer(isLoading = aiLoading)
        }

        // Trigger action card
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
                        text = "실시간 인공지능 오믹스(Omics) 분석기",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = HelixDarkNavy
                    )
                    Text(
                        text = "환자의 현장 SNP 유전형질, PCR DNA 증폭 검사 결과 및 수면/운동 생활 데이터를 유기적으로 매핑하여 질환 진행률과 위험 바이오마커를 예측합니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = HelixBodyText,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.triggerGeminiAnalysis() },
                        enabled = !aiLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = HelixDarkNavy),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        if (aiLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "실시간 AI 정밀 진단 시작",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Report display card
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = HelixBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "H-ONE 오믹스 정밀 분석 리포트",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = HelixDarkNavy
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (aiLoading) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 40.dp),
                                color = HelixBlue,
                                trackColor = HelixBlueGlow
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Gemini 모델이 정밀 유전체와 라이프스타일 결합 요인을 정렬 중입니다...",
                                style = MaterialTheme.typography.bodySmall,
                                color = HelixBodyText
                            )
                        }
                    } else {
                        // Display generated text with formatting
                        Text(
                            text = aiText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 24.sp
                            ),
                            color = HelixDarkNavy
                        )
                    }
                }
            }
        }
    }
}

// Custom glowing AI & DNA network visualizer
@Composable
fun AiNetworkVisualizer(isLoading: Boolean) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = HelixDarkNavy),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp), ambientColor = HelixDarkNavy)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val infiniteTransition = rememberInfiniteTransition(label = "ai_glow")
            
            // Nodes offset animations
            val angle1 by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(8000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "angle1"
            )

            val speedMultiplier = if (isLoading) 3.5f else 1f
            val pulseGlow by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween((1500 / speedMultiplier).toInt(), easing = EaseInOut),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse"
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val centerX = width / 2f
                val centerY = height / 2f

                // Draw background glowing aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(HelixBlue.copy(alpha = 0.3f * pulseGlow), Color.Transparent),
                        center = Offset(centerX, centerY),
                        radius = 120.dp.toPx()
                    ),
                    radius = 120.dp.toPx()
                )

                // Define 6 nodes coordinates in a circular network
                val r = 60.dp.toPx()
                val nodesCount = 6
                val points = mutableListOf<Offset>()

                for (i in 0 until nodesCount) {
                    val angleRad = Math.toRadians((angle1 + (i * (360 / nodesCount))).toDouble()).toFloat()
                    val offsetRadius = r + (sin(angleRad * 3f) * 10.dp.toPx() * pulseGlow)
                    val x = centerX + offsetRadius * cos(angleRad)
                    val y = centerY + offsetRadius * sin(angleRad)
                    points.add(Offset(x, y))
                }

                // Connect nodes with biotech style lines
                for (i in 0 until nodesCount) {
                    val p1 = points[i]
                    val p2 = points[(i + 1) % nodesCount]
                    val p3 = points[(i + 3) % nodesCount] // cross connection

                    drawLine(
                        color = HelixBlue.copy(alpha = 0.4f),
                        start = p1,
                        end = p2,
                        strokeWidth = 1.5.dp.toPx()
                    )

                    drawLine(
                        color = HelixBlue.copy(alpha = 0.25f * pulseGlow),
                        start = p1,
                        end = p3,
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Draw central nucleus (Gemini core)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, HelixBlue.copy(alpha = 0.6f)),
                        center = Offset(centerX, centerY)
                    ),
                    radius = (20 + (5 * pulseGlow)).dp.toPx(),
                    center = Offset(centerX, centerY)
                )

                // Draw outer node glowing balls
                points.forEachIndexed { idx, p ->
                    val color = if (idx % 2 == 0) HelixBlue else HelixGreen
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White, color),
                            center = p
                        ),
                        radius = 7.dp.toPx(),
                        center = p
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isLoading) "ANALYZING GENOME..." else "STANDBY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }

                Text(
                    text = if (isLoading) "환자의 유전자 발현 시퀀스를 정렬하고 이상 돌연변이를 인공지능 가속 대조 중입니다..." 
                           else "Helix Precision AI: 최적의 표적 예후 적합 판독 완료",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
