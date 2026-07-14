package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.HelixBlue
import com.example.ui.theme.HelixDarkNavy
import com.example.ui.theme.HelixLightGray
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppTab
import com.example.viewmodel.HelixViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: HelixViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val currentTab by viewModel.currentTab.collectAsState()
                val selectedResult by viewModel.selectedResult.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Display bottom bar only if we are NOT on the sub-detailed result screen
                        if (selectedResult == null) {
                            NavigationBar(
                                containerColor = Color.White,
                                tonalElevation = 8.dp,
                                modifier = Modifier
                                    .testTag("bottom_nav_bar")
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                            ) {
                                NavigationBarItem(
                                    selected = currentTab == AppTab.HOME,
                                    onClick = { viewModel.selectTab(AppTab.HOME) },
                                    label = { Text("홈", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentTab == AppTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                            contentDescription = "홈"
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = HelixBlue,
                                        selectedTextColor = HelixBlue,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = HelixDarkNavy.copy(alpha = 0.5f),
                                        unselectedTextColor = HelixDarkNavy.copy(alpha = 0.5f)
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentTab == AppTab.ANALYSIS,
                                    onClick = { viewModel.selectTab(AppTab.ANALYSIS) },
                                    label = { Text("AI 분석", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentTab == AppTab.ANALYSIS) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                                            contentDescription = "AI 분석"
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = HelixBlue,
                                        selectedTextColor = HelixBlue,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = HelixDarkNavy.copy(alpha = 0.5f),
                                        unselectedTextColor = HelixDarkNavy.copy(alpha = 0.5f)
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentTab == AppTab.TREATMENT,
                                    onClick = { viewModel.selectTab(AppTab.TREATMENT) },
                                    label = { Text("치료 설계", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentTab == AppTab.TREATMENT) Icons.Filled.Healing else Icons.Outlined.Healing,
                                            contentDescription = "치료 설계"
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = HelixBlue,
                                        selectedTextColor = HelixBlue,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = HelixDarkNavy.copy(alpha = 0.5f),
                                        unselectedTextColor = HelixDarkNavy.copy(alpha = 0.5f)
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentTab == AppTab.HOSPITAL,
                                    onClick = { viewModel.selectTab(AppTab.HOSPITAL) },
                                    label = { Text("진료 검색", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentTab == AppTab.HOSPITAL) Icons.Filled.LocalHospital else Icons.Outlined.LocalHospital,
                                            contentDescription = "진료 검색"
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = HelixBlue,
                                        selectedTextColor = HelixBlue,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = HelixDarkNavy.copy(alpha = 0.5f),
                                        unselectedTextColor = HelixDarkNavy.copy(alpha = 0.5f)
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentTab == AppTab.MY_PAGE,
                                    onClick = { viewModel.selectTab(AppTab.MY_PAGE) },
                                    label = { Text("마이페이지", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                    icon = {
                                        Icon(
                                            imageVector = if (currentTab == AppTab.MY_PAGE) Icons.Filled.Person else Icons.Outlined.Person,
                                            contentDescription = "마이페이지"
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = HelixBlue,
                                        selectedTextColor = HelixBlue,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = HelixDarkNavy.copy(alpha = 0.5f),
                                        unselectedTextColor = HelixDarkNavy.copy(alpha = 0.5f)
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(HelixLightGray)
                            .windowInsetsPadding(WindowInsets.statusBars)
                    ) {
                        AnimatedContent(
                            targetState = selectedResult,
                            transitionSpec = {
                                slideInHorizontally(initialOffsetX = { it }) + fadeIn() togetherWith
                                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                            },
                            label = "result_detail_transition"
                        ) { targetResult ->
                            if (targetResult != null) {
                                ResultDetailScreen(
                                    viewModel = viewModel,
                                    result = targetResult,
                                    onBack = { viewModel.selectResult(null) },
                                    onNavigateToHospital = {
                                        viewModel.selectResult(null)
                                        viewModel.selectTab(AppTab.HOSPITAL)
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Crossfade(
                                    targetState = currentTab,
                                    animationSpec = tween(300),
                                    label = "tab_crossfade"
                                ) { tab ->
                                    when (tab) {
                                        AppTab.HOME -> MainDashboardScreen(
                                            viewModel = viewModel,
                                            onNavigateToResult = { viewModel.selectResult(it) },
                                            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                                        )
                                        AppTab.ANALYSIS -> AiAnalysisScreen(
                                            viewModel = viewModel,
                                            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                                        )
                                        AppTab.TREATMENT -> TreatmentScreen(
                                            viewModel = viewModel,
                                            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                                        )
                                        AppTab.HOSPITAL -> HospitalScreen(
                                            viewModel = viewModel,
                                            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                                        )
                                        AppTab.MY_PAGE -> MyPageScreen(
                                            viewModel = viewModel,
                                            onNavigateToResult = { viewModel.selectResult(it) },
                                            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
