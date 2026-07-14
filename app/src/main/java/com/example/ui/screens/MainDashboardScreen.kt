package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DbTestResult
import com.example.ui.theme.*
import com.example.viewmodel.BleState
import com.example.viewmodel.HelixViewModel
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    viewModel: HelixViewModel,
    onNavigateToResult: (DbTestResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val results by viewModel.testResults.collectAsState()
    val bleState by viewModel.bleState.collectAsState()
    val bleProgress by viewModel.bleProgress.collectAsState()
    val bleStatusText by viewModel.bleStatusText.collectAsState()
    val latestResult = results.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HelixLightGray)
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Top luxury Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(HelixBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "HELIX ",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                ),
                                color = HelixDarkNavy
                            )
                            Text(
                                text = "ONE",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                ),
                                color = HelixBlue
                            )
                        }
                        Text(
                            text = "정밀 예측 및 바이오마커 분석기",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = HelixBodyText
                        )
                    }
                }

                // Profile and notifications
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButton(
                        onClick = { viewModel.selectTab(com.example.viewmodel.AppTab.MY_PAGE) },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, CircleShape)
                            .border(1.dp, HelixBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "알림",
                            tint = HelixDarkNavy,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(HelixBlue)
                            .clickable { viewModel.selectTab(com.example.viewmodel.AppTab.MY_PAGE) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "H1",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Biological DNA Double Helix Visualizer
        item {
            DnaIllustrationCard()
        }

        // Health Score Hero (Tesla/Apple Style)
        item {
            Card(
                shape = RoundedCornerShape(40.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HelixBorder, RoundedCornerShape(40.dp))
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "종합 정밀 건강 상태",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = HelixBodyText
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Box(contentAlignment = Alignment.Center) {
                        val score = latestResult?.score ?: 92
                        val color = if (score < 70) HelixRed else HelixBlue
                        
                        CircularProgressIndicator(
                            progress = { score / 100f },
                            modifier = Modifier.size(130.dp),
                            color = color,
                            strokeWidth = 9.dp,
                            trackColor = HelixBorder,
                            strokeCap = StrokeCap.Round
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$score",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 40.sp,
                                    letterSpacing = (-1).sp
                                ),
                                color = HelixDarkNavy
                            )
                            Text(
                                text = "INDEX",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = HelixBodyText
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Text(
                        text = if ((latestResult?.score ?: 92) >= 90) "건강 스코어: 최상 (Normal)" else "건강 스코어: 관리 요망",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = HelixDarkNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "유전체 예측 분석 결과 생체 연령 2.4세 젊음",
                        style = MaterialTheme.typography.bodySmall,
                        color = HelixBodyText
                    )
                }
            }
        }

        // Latest Test Result Card
        latestResult?.let { res ->
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, HelixBorder, RoundedCornerShape(24.dp))
                        .clickable { onNavigateToResult(res) }
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "최근 검사 리포트",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = HelixBodyText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${res.type} 정밀 분석",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = HelixDarkNavy
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val isDanger = res.status == "위험"
                        val isWarning = res.status == "주의"
                        val (bgColor, textColor, label) = when {
                            isDanger -> Triple(HelixRedBg, HelixRed, "위험 (정밀 처방)")
                            isWarning -> Triple(HelixYellowBg, HelixYellow, "주의 요망")
                            else -> Triple(HelixGreenBg, HelixGreen, "정상 (Normal)")
                        }
                        
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgColor)
                                .padding(vertical = 6.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(textColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Clean utility grid items
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(HelixLightGray)
                                    .border(1.dp, HelixBorder, RoundedCornerShape(16.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "변이 검출 여부",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = HelixBodyText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isDanger) "검출 됨" else "검출 안 됨",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDanger) HelixRed else HelixGreen
                                    )
                                )
                            }
                            
                            Column(
                                modifier = Modifier
                                    .weight(1.2f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(HelixLightGray)
                                    .border(1.dp, HelixBorder, RoundedCornerShape(16.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "진단일",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = HelixBodyText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = res.date,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HelixDarkNavy
                                    )
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = res.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = HelixDarkNavy,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { onNavigateToResult(res) },
                                modifier = Modifier
                                    .background(HelixBlueGlow, CircleShape)
                                    .size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "상세 보기",
                                    tint = HelixBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bluetooth device test triggers and results
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
                        text = "웨어러블 정밀 기기 검사",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = HelixDarkNavy
                    )
                    Text(
                        text = "Helix Bio-Analyzer 기기와 블루투스 동기화를 통해 현장 유전체/PCR 진단을 실행합니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = HelixBodyText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (bleState == BleState.DISCONNECTED) {
                        Button(
                            onClick = { viewModel.startBluetoothScanning() },
                            colors = ButtonDefaults.buttonColors(containerColor = HelixDarkNavy),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("start_test_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Bluetooth,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "검사 시작하기",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    } else {
                        // Testing Active view with progress indicator
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(
                                progress = { bleProgress },
                                modifier = Modifier.size(60.dp),
                                color = HelixBlue,
                                trackColor = HelixBlueGlow,
                                strokeWidth = 5.dp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = bleStatusText,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = HelixDarkNavy,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

// Custom animated 3D-looking DNA illustration on white canvas
@Composable
fun DnaIllustrationCard() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = HelixBorder)
            .border(1.dp, HelixBorder, RoundedCornerShape(24.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val infiniteTransition = rememberInfiniteTransition(label = "dna_rotation")
            val waveOffset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 2f * Math.PI.toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "offset"
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val midY = height / 2f
                val pointsCount = 18
                val spacing = width / (pointsCount + 1)
                val amplitude = 35.dp.toPx()

                // Draw background glowing grid
                val gridPaint = Stroke(width = 0.5.dp.toPx())
                for (i in 1..4) {
                    val y = height * (i / 5f)
                    drawLine(
                        color = HelixBorder.copy(alpha = 0.3f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                }

                // Draw double helix
                for (i in 0 until pointsCount) {
                    val x = spacing * (i + 1)
                    val factor = (i.toFloat() / pointsCount) * 2f * Math.PI.toFloat()
                    
                    // Wave 1
                    val y1 = midY + amplitude * sin(factor + waveOffset)
                    // Wave 2
                    val y2 = midY - amplitude * sin(factor + waveOffset)

                    // Draw connecting strands
                    drawLine(
                        color = HelixBlue.copy(alpha = 0.25f),
                        start = Offset(x, y1),
                        end = Offset(x, y2),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Nodes on Wave 1 (Blue)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(HelixBlue, HelixBlueGlow),
                            center = Offset(x, y1)
                        ),
                        radius = 6.dp.toPx(),
                        center = Offset(x, y1)
                    )

                    // Nodes on Wave 2 (Teal-Green/Biotech glow)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(HelixGreen, HelixGreenBg),
                            center = Offset(x, y2)
                        ),
                        radius = 4.dp.toPx(),
                        center = Offset(x, y2)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(HelixBlueGlow)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "BIOTECH AI ENGINE ACTIVE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = HelixBlue,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Text(
                    text = "환자의 미세 유전체 발현도와 분자 진단 추이를 분석하여 실시간 예측 모델을 수립하고 있습니다.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = HelixDarkNavy.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.width(260.dp)
                )
            }
        }
    }
}
