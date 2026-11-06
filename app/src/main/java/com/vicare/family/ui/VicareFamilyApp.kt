package com.vicare.family.ui

import android.net.Uri
import android.view.ViewGroup
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.math.sin
import com.vicare.family.R
import com.vicare.family.data.AlertGroup
import com.vicare.family.data.AlertSeverity
import com.vicare.family.data.FamilyAccount
import com.vicare.family.data.FamilyAlert
import com.vicare.family.data.FamilyData
import com.vicare.family.data.FamilyVitals

private val PageBg = Color(0xFFF4F8FB)
private val Ink = Color(0xFF20394A)
private val Muted = Color(0xFF718796)
private val Blue = Color(0xFF147FB5)
private val DeepBlue = Color(0xFF0E5E8F)
private val PaleBlue = Color(0xFFEAF5FB)
private val Teal = Color(0xFF168A78)
private val Amber = Color(0xFFB77A20)
private val Red = Color(0xFFC84B5A)
private val Border = Color(0xFFE3EBF0)

private enum class FamilyPage(val title: String, val navTitle: String, val glyph: String) {
    HOME("Giám sát sức khỏe", "Theo dõi", "⌂"),
    ALERTS("Trung tâm cảnh báo", "Cảnh báo", "!"),
    PROFILE("Thông tin gia đình", "Gia đình", "♡"),
    SETTINGS("Cài đặt", "Tài khoản", "⋯"),
}

@Composable
fun VicareFamilyApp() {
    var family by remember { mutableStateOf<FamilyAccount?>(null) }
    var page by remember { mutableStateOf(FamilyPage.HOME) }
    val history = remember { mutableListOf(FamilyPage.HOME) }
    val resolvedOverrides = remember { mutableStateMapOf<String, Boolean>() }
    val loggedInFamily = family
    BackHandler(enabled = loggedInFamily != null && history.size > 1) {
        if (history.size > 1) {
            history.removeAt(history.lastIndex)
            page = history.last()
        }
    }

    if (loggedInFamily == null) {
        LoginScreen { username, password ->
            val account = FamilyData.familyFor(username.trim(), password)
            if (account == null) false else {
                family = account
                page = FamilyPage.HOME
                history.clear()
                history.add(FamilyPage.HOME)
                resolvedOverrides.clear()
                true
            }
        }
        return
    }

    fun navigate(destination: FamilyPage) {
        if (page == destination) return
        history.add(destination)
        page = destination
    }

    val openCount = loggedInFamily.alerts.count { alert -> !(resolvedOverrides[alert.id] ?: alert.initialResolved) }
    Scaffold(
        containerColor = PageBg,
        topBar = {
            AppTopBar(family = loggedInFamily, openCount = openCount, onOpenAlerts = { navigate(FamilyPage.ALERTS) })
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp, windowInsets = WindowInsets.navigationBars) {
                FamilyPage.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = page == destination,
                        onClick = { navigate(destination) },
                        icon = { Text(destination.glyph, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                        label = { Text(destination.navTitle, fontSize = 10.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Blue,
                            selectedTextColor = Blue,
                            indicatorColor = PaleBlue,
                            unselectedIconColor = Muted,
                            unselectedTextColor = Muted,
                        ),
                    )
                }
            }
        },
    ) { insets ->
        when (page) {
            FamilyPage.HOME -> DashboardScreen(
                family = loggedInFamily,
                resolvedOverrides = resolvedOverrides,
                modifier = Modifier.padding(insets),
                onOpenAlerts = { navigate(FamilyPage.ALERTS) },
                onOpenProfile = { navigate(FamilyPage.PROFILE) },
            )
            FamilyPage.ALERTS -> AlertsScreen(
                family = loggedInFamily,
                resolvedOverrides = resolvedOverrides,
                modifier = Modifier.padding(insets),
                onToggle = { alert ->
                    resolvedOverrides[alert.id] = !(resolvedOverrides[alert.id] ?: alert.initialResolved)
                },
            )
            FamilyPage.PROFILE -> ProfileScreen(loggedInFamily, resolvedOverrides, Modifier.padding(insets))
            FamilyPage.SETTINGS -> SettingsScreen(
                family = loggedInFamily,
                username = loggedInFamily.username,
                modifier = Modifier.padding(insets),
                onLogout = {
                    family = null
                    page = FamilyPage.HOME
                    history.clear()
                    history.add(FamilyPage.HOME)
                    resolvedOverrides.clear()
                },
            )
        }
    }
}

@Composable
private fun LoginScreen(onLogin: (String, String) -> Boolean) {
    var username by rememberSaveable { mutableStateOf("admin1") }
    var password by rememberSaveable { mutableStateOf("123") }
    var error by rememberSaveable { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize().background(PageBg).statusBarsPadding().navigationBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 22.dp, vertical = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(26.dp))
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White, shadowElevation = 3.dp) {
            Image(painterResource(R.drawable.y_nhan_mark), contentDescription = "Y Nhãn", modifier = Modifier.width(112.dp).height(82.dp).padding(10.dp), contentScale = ContentScale.Fit)
        }
        Text("Y NHÃN", modifier = Modifier.padding(top = 15.dp), color = DeepBlue, fontSize = 21.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        Text("by VICARE", modifier = Modifier.padding(top = 3.dp), color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)
        Spacer(Modifier.height(34.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(22.dp), elevation = CardDefaults.cardElevation(1.dp)) {
            Column(Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Chào mừng trở lại", color = Ink, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                Text("Đăng nhập để xem tình hình sức khỏe gia đình.", color = Muted, fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.padding(bottom = 15.dp))
                Text("Tên đăng nhập", color = Ink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; error = false },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None, imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(12.dp),
                )
                Text("Mật khẩu", color = Ink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; error = false },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    shape = RoundedCornerShape(12.dp),
                )
                if (error) Text("Tên đăng nhập hoặc mật khẩu chưa đúng.", color = Red, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
                Button(
                    onClick = { error = !onLogin(username, password) },
                    modifier = Modifier.fillMaxWidth().height(52.dp).padding(top = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue),
                ) { Text("Đăng nhập", fontSize = 14.sp, fontWeight = FontWeight.Bold) }
            }
        }
        Text("Chăm sóc từ điều gần gũi nhất", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(top = 22.dp))
    }
}

@Composable
private fun AppTopBar(family: FamilyAccount, openCount: Int, onOpenAlerts: () -> Unit) {
    Surface(color = Color.White, shadowElevation = 1.dp) {
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 18.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(painterResource(R.drawable.y_nhan_mark), contentDescription = "Y Nhãn", modifier = Modifier.width(48.dp).height(38.dp), contentScale = ContentScale.Fit)
            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                Text("Y NHÃN", color = DeepBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = .8.sp)
                Text(family.name, color = Muted, fontSize = 10.sp)
            }
            Box(contentAlignment = Alignment.TopEnd) {
                Surface(onClick = onOpenAlerts, shape = CircleShape, color = PaleBlue) {
                    Text("!", modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = Blue, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                }
                if (openCount > 0) Surface(modifier = Modifier.size(17.dp), color = Red, shape = CircleShape) {
                    Box(contentAlignment = Alignment.Center) { Text(openCount.toString(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable
private fun DashboardScreen(
    family: FamilyAccount,
    resolvedOverrides: Map<String, Boolean>,
    modifier: Modifier = Modifier,
    onOpenAlerts: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    var tick by remember(family.id) { mutableIntStateOf(0) }
    var updatedAt by remember(family.id) { mutableStateOf(currentTime()) }
    LaunchedEffect(family.id) {
        while (true) {
            delay(2200)
            tick += 1
            updatedAt = currentTime()
        }
    }
    val liveVitals = liveVitals(family, tick)
    val status = overallStatus(family, liveVitals, resolvedOverrides)
    val openCount = family.alerts.count { !(resolvedOverrides[it.id] ?: it.initialResolved) }
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        WelcomeCard(family, status, openCount, updatedAt, onOpenAlerts)
        MemberCard(family, status, onOpenProfile)
        SectionHeading("CHỈ SỐ SỨC KHỎE", "Chỉ số sinh tồn", "Cập nhật $updatedAt")
        VitalsGrid(liveVitals)
        CameraSection(family)
        if (openCount > 0) {
            AlertsSummary(family, openCount, onOpenAlerts)
        } else {
            Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFEFF8F4)) {
                Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("✓", color = Teal, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Column(Modifier.padding(start = 11.dp)) {
                        Text("Chưa có cảnh báo mới", color = Teal, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Các chỉ số của gia đình đang được theo dõi.", color = Muted, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeCard(family: FamilyAccount, status: HealthStatus, openCount: Int, updatedAt: String, onOpenAlerts: () -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Column(
            Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Blue, DeepBlue))).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("HÔM NAY CÙNG GIA ĐÌNH", color = Color(0xFFBDEBFA), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
            Text("Sức khỏe người thân,\ntrong tầm mắt.", color = Color.White, fontSize = 23.sp, lineHeight = 29.sp, fontWeight = FontWeight.Bold)
            Text(if (openCount > 0) "Có $openCount thông tin cần gia đình lưu ý hôm nay." else "Chưa có cảnh báo mới. Các chỉ số gần nhất đang ổn định.", color = Color(0xFFE1F2F8), fontSize = 12.sp, lineHeight = 18.sp)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(status)
                Text("Cập nhật lúc $updatedAt", color = Color(0xFFD4EAF3), fontSize = 10.sp)
            }
            if (openCount > 0) TextButton(onClick = onOpenAlerts, contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)) {
                Text("Xem cảnh báo   →", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MemberCard(family: FamilyAccount, status: HealthStatus, onOpenProfile: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenProfile),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border),
    ) {
        Column(Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                InitialAvatar(family.member.name, size = 50.dp)
                Column(Modifier.weight(1f).padding(start = 11.dp)) {
                    Text("THÀNH VIÊN ĐANG THEO DÕI", color = Muted, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = .65.sp)
                    Text(family.member.name, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${family.member.gender} · ${family.member.age} tuổi · ${family.member.bloodType}", color = Muted, fontSize = 10.sp)
                }
                StatusDot(status)
            }
            HorizontalDivider(Modifier.padding(vertical = 13.dp), color = Border)
            Row(horizontalArrangement = Arrangement.spacedBy(0.dp), modifier = Modifier.fillMaxWidth()) {
                MemberStat("Chiều cao", "${family.member.height} cm", Modifier.weight(1f))
                MemberStat("Cân nặng", "${family.member.weight} kg", Modifier.weight(1f))
                MemberStat("Tiền sử", family.member.conditions.firstOrNull() ?: "Chưa ghi nhận", Modifier.weight(1.25f))
            }
        }
    }
}

@Composable
private fun MemberStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier.padding(end = 7.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, color = Muted, fontSize = 9.sp)
        Text(value, color = Ink, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun VitalsGrid(vitals: FamilyVitals) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Huyết áp", "${vitals.systolic}/${vitals.diastolic}", "mmHg", metricStatus("bp", vitals.systolic.toDouble()), "◉", Modifier.weight(1f))
            MetricCard("Nhịp tim", vitals.heartRate.toString(), "nhịp/phút", metricStatus("heart", vitals.heartRate.toDouble()), "♥", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("SpO₂", vitals.spo2.toString(), "%", metricStatus("spo2", vitals.spo2.toDouble()), "◌", Modifier.weight(1f))
            MetricCard("Nhiệt độ", String.format(Locale("vi", "VN"), "%.1f", vitals.temperature), "°C", metricStatus("temperature", vitals.temperature), "♨", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Nhịp thở", vitals.respiratoryRate.toString(), "lần/phút", metricStatus("respiration", vitals.respiratoryRate.toDouble()), "≋", Modifier.weight(1f))
            Surface(modifier = Modifier.weight(1f).height(112.dp), color = Color.Transparent) {}
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, unit: String, status: HealthStatus, glyph: String, modifier: Modifier = Modifier) {
    val tone = when (status) { HealthStatus.NORMAL -> Teal; HealthStatus.WARNING -> Amber; HealthStatus.CRITICAL -> Red }
    Card(modifier = modifier.height(112.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, if (status == HealthStatus.NORMAL) Border else tone.copy(alpha = .25f))) {
        Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Surface(shape = RoundedCornerShape(10.dp), color = tone.copy(alpha = .11f)) {
                    Text(glyph, modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp), color = tone, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Text(when (status) { HealthStatus.NORMAL -> "Ổn định"; HealthStatus.WARNING -> "Theo dõi"; HealthStatus.CRITICAL -> "Khẩn cấp" }, color = tone, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
            }
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(label, color = Muted, fontSize = 10.sp)
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(value, color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(unit, modifier = Modifier.padding(bottom = 3.dp), color = Muted, fontSize = 8.sp, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun CameraSection(family: FamilyAccount) {
    var thermal by rememberSaveable(family.id) { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeading("KHÔNG GIAN SỐNG", "Camera giám sát", "")
        Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Border)) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(if (thermal) "Ảnh nhiệt" else "Camera hồng ngoại", color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(if (thermal) "Ảnh false-color từ IMVIA-NIR" else "IMVIA-NIR · Hồng ngoại gần", color = Muted, fontSize = 10.sp)
                    }
                    Surface(shape = RoundedCornerShape(50), color = Color(0xFFEFF8F4)) {
                        Row(Modifier.padding(horizontal = 9.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            Box(Modifier.size(6.dp).background(Teal, CircleShape))
                            Text("Đang phát", color = Teal, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                VideoPlayer(cameraVideoResource(family.cameraSubject, thermal), Modifier.fillMaxWidth().aspectRatio(16f / 10f))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CameraModeChip("Hồng ngoại", selected = !thermal) { thermal = false }
                    CameraModeChip("Ảnh nhiệt", selected = thermal) { thermal = true }
                }
            }
        }
    }
}

@Composable
private fun CameraModeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = RoundedCornerShape(9.dp), color = if (selected) PaleBlue else Color(0xFFF7F9FA), border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) Color(0xFFB8DDEB) else Border)) {
        Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = if (selected) Blue else Muted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun AlertsSummary(family: FamilyAccount, openCount: Int, onOpenAlerts: () -> Unit) {
    Surface(onClick = onOpenAlerts, shape = RoundedCornerShape(16.dp), color = Color(0xFFFFFAF0), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0E2C9))) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(10.dp), color = Color.White) { Text("!", modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp), color = Amber, fontSize = 19.sp, fontWeight = FontWeight.Bold) }
            Column(Modifier.weight(1f).padding(start = 11.dp)) {
                Text("Trung tâm cảnh báo", color = Ink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("$openCount cảnh báo cần gia đình lưu ý", color = Muted, fontSize = 10.sp)
            }
            Text("›", color = Amber, fontSize = 24.sp)
        }
    }
}

@Composable
private fun AlertsScreen(family: FamilyAccount, resolvedOverrides: Map<String, Boolean>, modifier: Modifier = Modifier, onToggle: (FamilyAlert) -> Unit) {
    val openCount = family.alerts.count { !(resolvedOverrides[it.id] ?: it.initialResolved) }
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 17.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        Text("THEO DÕI AN TOÀN", color = Blue, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
        Text("Trung tâm cảnh báo", color = Ink, fontSize = 23.sp, fontWeight = FontWeight.Bold)
        Text("Những thay đổi sức khỏe và hoạt động cần gia đình lưu ý.", color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
        Surface(shape = RoundedCornerShape(15.dp), color = if (openCount > 0) Color(0xFFFFF8EA) else Color(0xFFEFF8F4)) {
            Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(if (openCount > 0) "!" else "✓", color = if (openCount > 0) Amber else Teal, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Column(Modifier.padding(start = 10.dp)) {
                    Text(if (openCount > 0) "$openCount cảnh báo đang chờ gia đình" else "Gia đình đang an toàn", color = Ink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(if (openCount > 0) "Xem từng cảnh báo và cập nhật khi đã kiểm tra." else "Hiện không có cảnh báo cần xử lý.", color = Muted, fontSize = 10.sp)
                }
            }
        }
        if (family.alerts.isEmpty()) {
            EmptyAlerts()
        } else {
            family.alerts.forEach { alert ->
                val resolved = resolvedOverrides[alert.id] ?: alert.initialResolved
                AlertCard(alert, resolved, onToggle = { onToggle(alert) })
            }
        }
    }
}

@Composable
private fun AlertCard(alert: FamilyAlert, resolved: Boolean, onToggle: () -> Unit) {
    val tone = when (alert.severity) { AlertSeverity.CRITICAL -> Red; AlertSeverity.WARNING -> Amber; AlertSeverity.INFO -> Teal }
    Card(shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, if (resolved) Border else tone.copy(alpha = .25f))) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(9.dp), color = tone.copy(alpha = .1f)) {
                    Text(if (alert.group == AlertGroup.ACTIVITY) "⌁" else "♥", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), color = tone, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Text(if (alert.group == AlertGroup.ACTIVITY) "HOẠT ĐỘNG" else "CHỈ SỐ SINH TỒN", modifier = Modifier.weight(1f).padding(start = 9.dp), color = Muted, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = .7.sp)
                Text(if (resolved) "Đã xử lý" else when (alert.severity) { AlertSeverity.CRITICAL -> "Khẩn cấp"; AlertSeverity.WARNING -> "Cần chú ý"; AlertSeverity.INFO -> "Đã ghi nhận" }, color = if (resolved) Teal else tone, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Text(alert.title, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(alert.value, color = tone, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(alert.condition, color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
            alert.camera?.let { Text("◉  $it", color = Muted, fontSize = 10.sp) }
            if (alert.hasVideo) {
                Text("Video sự kiện", color = Ink, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                VideoPlayer(R.raw.fall_recording, Modifier.fillMaxWidth().aspectRatio(16f / 10f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("◷  ${alert.occurredAt}", modifier = Modifier.weight(1f), color = Muted, fontSize = 10.sp)
                TextButton(onClick = onToggle) { Text(if (resolved) "Đã xử lý ✓" else "Đánh dấu đã xử lý", color = if (resolved) Teal else Blue, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun EmptyAlerts() {
    Card(shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 27.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("✓", color = Teal, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Chưa có cảnh báo mới", color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("Nếu có thay đổi về sức khỏe hoặc hoạt động, thông tin sẽ xuất hiện ở đây.", color = Muted, fontSize = 11.sp, lineHeight = 17.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ProfileScreen(family: FamilyAccount, resolvedOverrides: Map<String, Boolean>, modifier: Modifier = Modifier) {
    val status = overallStatus(family, family.vitals, resolvedOverrides)
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 17.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        Text("THÔNG TIN SỨC KHỎE CƠ BẢN", color = Blue, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp)
        Text("Thông tin gia đình", color = Ink, fontSize = 23.sp, fontWeight = FontWeight.Bold)
        Text("Hồ sơ người thân đang được theo dõi trong ${family.name}.", color = Muted, fontSize = 12.sp)
        Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Border)) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                InitialAvatar(family.member.name, size = 58.dp)
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    Text("THÀNH VIÊN CHÍNH", color = Muted, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = .7.sp)
                    Text(family.member.name, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("${family.member.gender} · ${family.member.age} tuổi", color = Muted, fontSize = 11.sp)
                }
                StatusDot(status)
            }
        }
        InfoCard("Thông tin cá nhân") {
            ProfileRow("Ngày sinh", family.member.birthDate)
            ProfileRow("Tuổi", "${family.member.age} tuổi")
            ProfileRow("Giới tính", family.member.gender)
            ProfileRow("Nhóm máu", family.member.bloodType)
            ProfileRow("Chiều cao", "${family.member.height} cm")
            ProfileRow("Cân nặng", "${family.member.weight} kg")
        }
        InfoCard("Tiền sử & chăm sóc") {
            Text("Tiền sử sức khỏe", color = Muted, fontSize = 10.sp)
            family.member.conditions.forEach { BulletRow(it) }
            Spacer(Modifier.height(3.dp))
            Text("Thuốc đang dùng", color = Muted, fontSize = 10.sp)
            family.member.medications.forEach { BulletRow(it) }
            Spacer(Modifier.height(3.dp))
            Text("Ghi chú gia đình", color = Muted, fontSize = 10.sp)
            Text(family.member.note, color = Ink, fontSize = 11.sp, lineHeight = 17.sp)
        }
        InfoCard("Người liên hệ khẩn cấp") {
            Text(family.member.emergencyContact, color = Ink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun InfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Border)) {
        Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Muted, fontSize = 11.sp)
        Text(value, color = Ink, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun BulletRow(value: String) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.padding(top = 5.dp).size(5.dp).background(Blue, CircleShape))
        Text(value, color = Ink, fontSize = 11.sp, lineHeight = 16.sp)
    }
}

@Composable
private fun SettingsScreen(family: FamilyAccount, username: String, modifier: Modifier = Modifier, onLogout: () -> Unit) {
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 17.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        Text("TÀI KHOẢN & QUYỀN RIÊNG TƯ", color = Blue, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp)
        Text("Cài đặt", color = Ink, fontSize = 23.sp, fontWeight = FontWeight.Bold)
        Text("Quản lý thông tin tài khoản gia đình.", color = Muted, fontSize = 12.sp)
        InfoCard("Tài khoản hiện tại") {
            ProfileRow("Tên đăng nhập", username)
            ProfileRow("Phạm vi dữ liệu", family.name)
            ProfileRow("Thành viên theo dõi", family.member.name)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Phiên đăng nhập", color = Muted, fontSize = 11.sp)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(Modifier.size(7.dp).background(Teal, CircleShape))
                    Text("Đang hoạt động", color = Teal, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF1F2))) {
            Text("Đăng xuất khỏi tài khoản", color = Red, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun SectionHeading(eyebrow: String, title: String, aside: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(eyebrow, color = Blue, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            Text(title, modifier = Modifier.weight(1f), color = Ink, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            if (aside.isNotBlank()) Text(aside, color = Muted, fontSize = 9.sp)
        }
    }
}

private enum class HealthStatus { NORMAL, WARNING, CRITICAL }

@Composable
private fun StatusChip(status: HealthStatus) {
    val tone = statusTone(status)
    Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = .16f)) {
        Row(Modifier.padding(horizontal = 9.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Box(Modifier.size(6.dp).background(tone, CircleShape))
            Text(statusLabel(status), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun StatusDot(status: HealthStatus) {
    val tone = statusTone(status)
    Surface(shape = RoundedCornerShape(50), color = tone.copy(alpha = .1f)) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Box(Modifier.size(6.dp).background(tone, CircleShape))
            Text(statusLabel(status), color = tone, fontSize = 8.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}

@Composable
private fun InitialAvatar(name: String, size: Dp) {
    val initial = name.split(" ").takeLast(2).mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
    Surface(modifier = Modifier.size(size), shape = CircleShape, color = PaleBlue) {
        Box(contentAlignment = Alignment.Center) { Text(initial, color = Blue, fontSize = if (size > 52.dp) 17.sp else 15.sp, fontWeight = FontWeight.Bold) }
    }
}

private fun statusTone(status: HealthStatus) = when (status) { HealthStatus.NORMAL -> Teal; HealthStatus.WARNING -> Amber; HealthStatus.CRITICAL -> Red }
private fun statusLabel(status: HealthStatus) = when (status) { HealthStatus.NORMAL -> "Đang ổn định"; HealthStatus.WARNING -> "Cần chú ý"; HealthStatus.CRITICAL -> "Cần hỗ trợ ngay" }

private fun metricStatus(key: String, value: Double): HealthStatus = when (key) {
    "bp" -> if (value >= 180) HealthStatus.CRITICAL else if (value >= 140) HealthStatus.WARNING else HealthStatus.NORMAL
    "spo2" -> if (value < 90) HealthStatus.CRITICAL else if (value < 92) HealthStatus.WARNING else HealthStatus.NORMAL
    "heart" -> if (value > 120 || value < 40) HealthStatus.CRITICAL else if (value > 100 || value < 50) HealthStatus.WARNING else HealthStatus.NORMAL
    "temperature" -> if (value >= 39 || value < 35) HealthStatus.CRITICAL else if (value >= 37.5 || value < 36) HealthStatus.WARNING else HealthStatus.NORMAL
    "respiration" -> if (value > 30 || value < 8) HealthStatus.CRITICAL else if (value > 22 || value < 12) HealthStatus.WARNING else HealthStatus.NORMAL
    else -> HealthStatus.NORMAL
}

private fun overallStatus(family: FamilyAccount, vitals: FamilyVitals, resolvedOverrides: Map<String, Boolean>): HealthStatus {
    if (vitals.spo2 < 92 || vitals.systolic >= 180 || vitals.diastolic >= 120) return HealthStatus.CRITICAL
    if (vitals.systolic >= 140 || vitals.diastolic >= 90 || vitals.heartRate > 100 || vitals.heartRate < 50 || family.alerts.any { it.group == AlertGroup.ACTIVITY && !(resolvedOverrides[it.id] ?: it.initialResolved) }) return HealthStatus.WARNING
    return HealthStatus.NORMAL
}

private fun liveVitals(family: FamilyAccount, tick: Int): FamilyVitals {
    val phase = family.id.takeLast(2).toInt() * .53
    fun drift(sensorPhase: Double): Double = (sin(tick * .78 + phase + sensorPhase) - sin(phase + sensorPhase)) / 2.0
    fun vary(value: Int, range: Int, sensorPhase: Double) = (value + drift(sensorPhase) * range).roundToInt()
    return family.vitals.copy(
        systolic = vary(family.vitals.systolic, 2, .1),
        diastolic = vary(family.vitals.diastolic, 1, .4),
        heartRate = vary(family.vitals.heartRate, 2, .8),
        spo2 = vary(family.vitals.spo2, 1, 1.3),
        temperature = kotlin.math.round((family.vitals.temperature + drift(1.8) * .1) * 10.0) / 10.0,
        respiratoryRate = vary(family.vitals.respiratoryRate, 1, 2.3),
    )
}

private fun currentTime(): String = SimpleDateFormat("HH:mm:ss", Locale("vi", "VN")).format(Date())

private fun cameraVideoResource(subject: String, thermal: Boolean): Int = when (subject to thermal) {
    "01" to false -> R.raw.imvia_ir_01
    "02" to false -> R.raw.imvia_ir_02
    "03" to false -> R.raw.imvia_ir_03
    "04" to false -> R.raw.imvia_ir_04
    "05" to false -> R.raw.imvia_ir_05
    "01" to true -> R.raw.imvia_thermal_01
    "02" to true -> R.raw.imvia_thermal_02
    "03" to true -> R.raw.imvia_thermal_03
    "04" to true -> R.raw.imvia_thermal_04
    else -> R.raw.imvia_thermal_05
}

@Composable
private fun VideoPlayer(resourceId: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var playing by rememberSaveable(resourceId) { mutableStateOf(true) }
    var playbackError by rememberSaveable(resourceId) { mutableStateOf(false) }
    val videoUri = remember(resourceId, context.packageName) { Uri.parse("android.resource://${context.packageName}/$resourceId") }
    val videoView = remember(resourceId) {
        VideoView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setOnPreparedListener { player ->
                playbackError = false
                player.isLooping = true
                player.setVolume(0f, 0f)
                if (playing) start()
            }
            setOnErrorListener { _, _, _ -> playbackError = true; playing = false; true }
            setVideoURI(videoUri)
        }
    }
    DisposableEffect(videoView) { onDispose { videoView.stopPlayback() } }
    Box(modifier.clip(RoundedCornerShape(14.dp)).background(Color(0xFF07131C))) {
        key(resourceId) {
            AndroidView(factory = { videoView }, modifier = Modifier.fillMaxSize(), update = { view ->
                if (!playbackError && playing && !view.isPlaying) view.start()
                if (!playing && view.isPlaying) view.pause()
            })
        }
        if (playbackError) {
            Surface(modifier = Modifier.align(Alignment.Center), color = Color(0xCC07131C), shape = RoundedCornerShape(10.dp), onClick = {
                playbackError = false
                playing = true
                videoView.stopPlayback()
                videoView.setVideoURI(videoUri)
            }) { Text("Chạm để phát lại", modifier = Modifier.padding(horizontal = 13.dp, vertical = 9.dp), color = Color.White, fontSize = 11.sp) }
        }
        Surface(modifier = Modifier.align(Alignment.TopStart).padding(8.dp), color = Color(0xAA081722), shape = RoundedCornerShape(6.dp)) {
            Text("IMVIA-NIR", modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp), color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = .4.sp)
        }
        Surface(modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp).size(36.dp), color = Color(0xAA07131C), shape = CircleShape, onClick = {
            playing = !playing
            if (playing) videoView.start() else videoView.pause()
        }) { Box(contentAlignment = Alignment.Center) { Text(if (playing) "Ⅱ" else "▶", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold) } }
    }
}

