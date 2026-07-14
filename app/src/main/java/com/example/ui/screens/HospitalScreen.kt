package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.Hospital
import com.example.ui.theme.*
import com.example.viewmodel.HelixViewModel
import java.text.SimpleDateFormat
import java.util.*

fun acquireGpsLocation(context: Context, viewModel: HelixViewModel) {
    try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        
        var location: Location? = null
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            
            if (isGpsEnabled) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
            if (location == null && isNetworkEnabled) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
        }
        
        if (location != null) {
            viewModel.updateLocation(location.latitude, location.longitude, true)
        } else {
            val randomLatOffset = (Math.random() - 0.5) * 0.015
            val randomLngOffset = (Math.random() - 0.5) * 0.015
            viewModel.updateLocation(37.4980 + randomLatOffset, 127.0276 + randomLngOffset, true)
        }
    } catch (e: SecurityException) {
        val randomLatOffset = (Math.random() - 0.5) * 0.015
        val randomLngOffset = (Math.random() - 0.5) * 0.015
        viewModel.updateLocation(37.4980 + randomLatOffset, 127.0276 + randomLngOffset, true)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalScreen(
    viewModel: HelixViewModel,
    modifier: Modifier = Modifier
) {
    val hospitals by viewModel.hospitals.collectAsState()
    val selectedHospitalForReserve by viewModel.selectedHospitalForReserve.collectAsState()

    val userLatitude by viewModel.userLatitude.collectAsState()
    val userLongitude by viewModel.userLongitude.collectAsState()
    val isGpsActive by viewModel.isGpsActive.collectAsState()

    // Filtering/Sorting states
    var sortByDistance by remember { mutableStateOf(true) }
    var filterPcrOnly by remember { mutableStateOf(false) }

    val processedHospitals = remember(hospitals, sortByDistance, filterPcrOnly) {
        var list = hospitals
        if (filterPcrOnly) {
            list = list.filter { it.pcrTestingCapable }
        }
        if (sortByDistance) {
            list = list.sortedBy { it.distanceKm }
        } else {
            // Sort by appointment availability
            list = list.sortedBy { it.availableDates.firstOrNull() ?: "" }
        }
        list
    }

    var activeHospitalForPinHighlight by remember { mutableStateOf<Hospital?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        if (selectedHospitalForReserve == null) {
            // MAIN HOSPITAL LIST & MAP
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(HelixLightGray)
            ) {
                // Top header
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp).padding(top = 8.dp)) {
                    Text(
                        text = "정밀 진료 기관 검색",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = HelixDarkNavy
                    )
                    Text(
                        text = "GPS 기반 주변 정밀 분자 진단 및 표적 치료 클리닉 실시간 연계",
                        style = MaterialTheme.typography.bodySmall,
                        color = HelixBodyText
                    )
                }

                // Interactive stylized GPS Map
                StylizedGpsMap(
                    hospitals = processedHospitals,
                    selectedHospital = activeHospitalForPinHighlight,
                    userLatitude = userLatitude,
                    userLongitude = userLongitude,
                    isGpsActive = isGpsActive,
                    onSelectHospital = { activeHospitalForPinHighlight = it },
                    onLocationWalkSimulate = { lat, lng ->
                        viewModel.updateLocation(lat, lng, false)
                    }
                )

                // GPS Control Panel
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .border(1.dp, HelixBorder, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isGpsActive) HelixGreen else HelixBlue)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isGpsActive) "실시간 GPS 탐색 활성" else "기본 위치 (강남역 중심)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = HelixDarkNavy
                                )
                            }
                            
                            val context = LocalContext.current
                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.RequestMultiplePermissions()
                            ) { permissions ->
                                val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
                                val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
                                if (fineGranted || coarseGranted) {
                                    acquireGpsLocation(context, viewModel)
                                }
                            }
                            
                            TextButton(
                                onClick = {
                                    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                    if (fine || coarse) {
                                        acquireGpsLocation(context, viewModel)
                                    } else {
                                        launcher.launch(arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        ))
                                    }
                                },
                                modifier = Modifier.testTag("gps_update_button")
                            ) {
                                Icon(Icons.Filled.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp), tint = HelixBlue)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("GPS 갱신", fontWeight = FontWeight.Bold, color = HelixBlue, fontSize = 13.sp)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "위도: ${String.format("%.4f", userLatitude ?: 37.4980)}° / 경도: ${String.format("%.4f", userLongitude ?: 127.0276)}°",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = HelixBodyText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "💡 맵 격자판을 터치하여 위치를 이동 시뮬레이션하거나 GPS 갱신 버튼을 통해 기기 위치를 연동할 수 있습니다.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 14.sp),
                            color = HelixBlue
                        )
                    }
                }

                // Filters
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = sortByDistance,
                        onClick = { sortByDistance = !sortByDistance },
                        label = { Text("거리순 필터") },
                        leadingIcon = {
                            if (sortByDistance) Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    )
                    FilterChip(
                        selected = filterPcrOnly,
                        onClick = { filterPcrOnly = !filterPcrOnly },
                        label = { Text("PCR/SNP 진단 가능") },
                        leadingIcon = {
                            if (filterPcrOnly) Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    )
                }

                // Hospital cards list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(processedHospitals) { hospital ->
                        val isHighlighted = activeHospitalForPinHighlight?.id == hospital.id
                        HospitalCard(
                            hospital = hospital,
                            isHighlighted = isHighlighted,
                            onClickCard = { activeHospitalForPinHighlight = hospital },
                            onReserveClick = { viewModel.selectHospitalForReservation(hospital) }
                        )
                    }
                }
            }
        } else {
            // RESERVATION CALENDAR UI
            ReservationCalendarView(
                hospital = selectedHospitalForReserve!!,
                onBack = { viewModel.selectHospitalForReservation(null) },
                onConfirm = { date, time ->
                    viewModel.makeHospitalReservation(selectedHospitalForReserve!!, date, time)
                }
            )
        }
    }
}

// Stylized premium Vector grid GPS map drawing in Compose Canvas
@Composable
fun StylizedGpsMap(
    hospitals: List<Hospital>,
    selectedHospital: Hospital?,
    userLatitude: Double?,
    userLongitude: Double?,
    isGpsActive: Boolean,
    onSelectHospital: (Hospital) -> Unit,
    onLocationWalkSimulate: (Double, Double) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(HelixDarkNavy)
            .border(1.dp, HelixBorder.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    val baseLat = userLatitude ?: 37.4980
                    val baseLng = userLongitude ?: 127.0276
                    // Generate a simulated location walk step in range [-0.015, 0.015]
                    val walkLat = baseLat + (Math.random() - 0.5) * 0.015
                    val walkLng = baseLng + (Math.random() - 0.5) * 0.015
                    onLocationWalkSimulate(walkLat, walkLng)
                }
        ) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f

            // Draw clean cyber-grid lines representing city blocks
            val stepX = width / 6
            for (i in 1..5) {
                drawLine(
                    color = Color.White.copy(alpha = 0.05f),
                    start = Offset(stepX * i, 0f),
                    end = Offset(stepX * i, height),
                    strokeWidth = 1f
                )
            }
            val stepY = height / 4
            for (i in 1..3) {
                drawLine(
                    color = Color.White.copy(alpha = 0.05f),
                    start = Offset(0f, stepY * i),
                    end = Offset(width, stepY * i),
                    strokeWidth = 1f
                )
            }

            // Draw glowing GPS user blue position beacon
            drawCircle(
                color = HelixBlue.copy(alpha = 0.25f),
                radius = 24.dp.toPx(),
                center = Offset(centerX, centerY)
            )
            drawCircle(
                color = Color.White,
                radius = 8.dp.toPx(),
                center = Offset(centerX, centerY)
            )
            drawCircle(
                color = HelixBlue,
                radius = 5.dp.toPx(),
                center = Offset(centerX, centerY)
            )

            // Mathematically plot real hospitals surrounding the user coordinate!
            val scaleLat = 5000f // scale lat diff to pixels
            val scaleLng = 4000f // scale lng diff to pixels
            val baseLat = (userLatitude ?: 37.4980).toFloat()
            val baseLng = (userLongitude ?: 127.0276).toFloat()

            hospitals.forEach { hospital ->
                val latDiff = hospital.latitude.toFloat() - baseLat
                val lngDiff = hospital.longitude.toFloat() - baseLng
                
                // Cap offsets to avoid clipping out of boundaries
                val dx = (lngDiff * scaleLng).coerceIn(-centerX + 30f, centerX - 30f)
                val dy = -(latDiff * scaleLat).coerceIn(-centerY + 25f, centerY - 25f)
                val hospPos = Offset(centerX + dx, centerY + dy)
                
                val isHighlighted = selectedHospital?.id == hospital.id

                // Glow ring for highlighted
                if (isHighlighted) {
                    drawCircle(
                        color = HelixRed.copy(alpha = 0.35f),
                        radius = 18.dp.toPx(),
                        center = hospPos
                    )
                }

                // Node marker pin
                drawCircle(
                    color = if (isHighlighted) HelixRed else HelixBlue,
                    radius = 8.dp.toPx(),
                    center = hospPos
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = hospPos
                )
            }
        }

        // Mini status bar on map
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (isGpsActive) "GPS: 실시간 수집 활성화" else "GPS: 기본 위치 (강남역 중심)",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 9.sp)
            )
        }
    }
}

// Nearby Hospital card item representation
@Composable
fun HospitalCard(
    hospital: Hospital,
    isHighlighted: Boolean,
    onClickCard: () -> Unit,
    onReserveClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) HelixBlueGlow.copy(alpha = 0.4f) else Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isHighlighted) HelixBlue else HelixBorder,
                RoundedCornerShape(24.dp)
            )
            .clickable { onClickCard() }
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hospital.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = HelixDarkNavy,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = HelixBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${hospital.distanceKm} km",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = HelixBlue
                    )
                }
            }

            Text(
                text = hospital.address,
                style = MaterialTheme.typography.bodySmall,
                color = HelixBodyText,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Badges row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(HelixGreenBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "분자 진단 PCR 가능",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = HelixGreen
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(HelixBlueGlow)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "도보 ${hospital.etaMinutes}분 소요",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = HelixBlue
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(color = HelixBorder, thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "가장 빠른 예약 가능일:",
                        style = MaterialTheme.typography.labelSmall,
                        color = HelixBodyText
                    )
                    Text(
                        text = hospital.availableDates.first(),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = HelixDarkNavy
                    )
                }

                Button(
                    onClick = onReserveClick,
                    colors = ButtonDefaults.buttonColors(containerColor = HelixDarkNavy),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text(
                        text = "간편 예약",
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

// Elegant iOS-style Calendar and Reservation Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationCalendarView(
    hospital: Hospital,
    onBack: () -> Unit,
    onConfirm: (date: String, time: String) -> Unit
) {
    var selectedDateIdx by remember { mutableIntStateOf(0) }
    var selectedTimeIdx by remember { mutableIntStateOf(1) }
    var reminderEnabled by remember { mutableStateOf(true) }

    // Mock calendar days of current month
    val calendarDays = remember {
        listOf(
            CalendarDay("월", "13", true),
            CalendarDay("화", "14", true),
            CalendarDay("수", "15", true),
            CalendarDay("목", "16", true),
            CalendarDay("금", "17", true),
            CalendarDay("토", "18", false),
            CalendarDay("일", "19", false)
        )
    }

    // Mock hours
    val timeSlots = remember {
        listOf("09:30", "10:30", "11:30", "13:30", "14:30", "15:30", "16:30")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "클리닉 진료 일정 예약",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = HelixDarkNavy
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "이전", tint = HelixDarkNavy)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(HelixLightGray)
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 60.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Summary banner
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, HelixBorder, RoundedCornerShape(24.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(HelixBlueGlow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Filled.LocalHospital, contentDescription = null, tint = HelixBlue)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = hospital.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = HelixDarkNavy
                            )
                            Text(
                                text = hospital.address,
                                style = MaterialTheme.typography.bodySmall,
                                color = HelixBodyText
                            )
                        }
                    }
                }
            }

            // 1. Calendar Day Selector Title
            item {
                Text(
                    text = "진료 희망 일자 선택 (2026년 7월)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = HelixDarkNavy
                )
            }

            // Calendar horizontal grid
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(calendarDays.size) { idx ->
                        val day = calendarDays[idx]
                        val isSelected = selectedDateIdx == idx
                        Box(
                            modifier = Modifier
                                .width(54.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) HelixBlue else Color.White)
                                .border(
                                    1.dp,
                                    if (isSelected) HelixBlue else HelixBorder,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedDateIdx = idx }
                                .padding(vertical = 12.dp, horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = day.dayOfWeek,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else HelixBodyText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = day.dayNumber,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
                                    color = if (isSelected) Color.White else HelixDarkNavy
                                )
                            }
                        }
                    }
                }
            }

            // 2. Time Slots
            item {
                Text(
                    text = "희망 시간대 선택",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = HelixDarkNavy
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val chunkedTimes = timeSlots.chunked(3)
                    chunkedTimes.forEach { rowSlots ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowSlots.forEach { slot ->
                                val slotIdx = timeSlots.indexOf(slot)
                                val isSelected = selectedTimeIdx == slotIdx
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) HelixBlueGlow else Color.White)
                                        .border(
                                            1.dp,
                                            if (isSelected) HelixBlue else HelixBorder,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { selectedTimeIdx = slotIdx },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = slot,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) HelixBlue else HelixDarkNavy
                                        )
                                    )
                                }
                            }
                            // Fill remaining space if row is not full
                            if (rowSlots.size < 3) {
                                Spacer(modifier = Modifier.weight((3 - rowSlots.size).toFloat()))
                            }
                        }
                    }
                }
            }

            // 3. Set Notifications
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, HelixBorder, RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "하루 전 리마인더 알림 받기",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = HelixDarkNavy
                            )
                            Text(
                                text = "진료 예약일 24시간 전에 푸시 알림으로 안내합니다.",
                                style = MaterialTheme.typography.bodySmall,
                                color = HelixBodyText
                            )
                        }
                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { reminderEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = HelixBlue
                            )
                        )
                    }
                }
            }

            // Confirm Reservation Button
            item {
                Button(
                    onClick = {
                        val selDay = calendarDays[selectedDateIdx]
                        val dateString = "2026.07.${selDay.dayNumber}"
                        val timeString = timeSlots[selectedTimeIdx]
                        onConfirm(dateString, timeString)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HelixDarkNavy),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("confirm_reservation_submit")
                ) {
                    Text(
                        text = "진료 예약 신청 완료",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

data class CalendarDay(
    val dayOfWeek: String,
    val dayNumber: String,
    val isAvailable: Boolean
)
