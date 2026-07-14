package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.HelixViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingSetupScreen(
    viewModel: HelixViewModel,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var birth by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("남성") }
    
    var nameError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HelixLightGray)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Elegant brand header
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(HelixBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "HELIX ",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        ),
                        color = HelixDarkNavy
                    )
                    Text(
                        text = "ONE",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        ),
                        color = HelixBlue
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "환자 정밀 프로필 초기 등록",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = HelixDarkNavy
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "정밀 유전체 감수성 예측과 표적 치료 설계를 위한 기초 바이오 데이터를 설정합니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = HelixBodyText,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    lineHeight = 16.sp
                )
            }

            // Input Form Card
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HelixBorder, RoundedCornerShape(28.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Patient Name
                    Column {
                        Text(
                            text = "환자 성명 *",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = HelixDarkNavy
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { 
                                name = it
                                if (it.isNotEmpty()) nameError = false
                            },
                            placeholder = { Text("환자 성명을 입력하세요") },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = HelixBlue) },
                            singleLine = true,
                            isError = nameError,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HelixBlue,
                                unfocusedBorderColor = HelixBorder,
                                errorBorderColor = HelixRed
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            shape = RoundedCornerShape(12.dp)
                        )
                        if (nameError) {
                            Text(
                                text = "성명은 필수 입력 항목입니다.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = HelixRed,
                                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                            )
                        }
                    }

                    // Patient ID / Code
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "환자 식별 코드 (선택)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = HelixDarkNavy
                            )
                            Text(
                                text = "자동 생성",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = HelixBlue,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .clickable {
                                        code = "HX-2026-${(1000..9999).random()}"
                                    }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
                            placeholder = { Text("예: HX-2026-9081 (자동 발급 가능)") },
                            leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null, tint = HelixBlue) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("code_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HelixBlue,
                                unfocusedBorderColor = HelixBorder
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Birthdate
                    Column {
                        Text(
                            text = "생년월일 (선택)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = HelixDarkNavy
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = birth,
                            onValueChange = { birth = it },
                            placeholder = { Text("예: 1994.04.12") },
                            leadingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = HelixBlue) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("birth_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HelixBlue,
                                unfocusedBorderColor = HelixBorder
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Gender Selection
                    Column {
                        Text(
                            text = "성별",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = HelixDarkNavy
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf("남성", "여성", "기타").forEach { gender ->
                                val isSelected = selectedGender == gender
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) HelixBlue else HelixLightGray)
                                        .border(
                                            1.dp,
                                            if (isSelected) HelixBlue else HelixBorder,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { selectedGender = gender }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = gender,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else HelixDarkNavy
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Register button
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                    } else {
                        viewModel.savePatientProfile(
                            name = name.trim(),
                            code = code.trim(),
                            birth = birth.trim(),
                            gender = selectedGender
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = HelixDarkNavy),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("register_button")
            ) {
                Text(
                    text = "정밀 프로필 등록 및 분석 시작",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
