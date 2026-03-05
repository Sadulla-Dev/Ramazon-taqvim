package com.example.ramazontaqvim.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ramazontaqvim.ui.theme.GoldAlpha12
import com.example.ramazontaqvim.ui.theme.GoldAlpha30
import com.example.ramazontaqvim.ui.theme.GoldDim
import com.example.ramazontaqvim.ui.theme.GoldLight
import com.example.ramazontaqvim.ui.theme.GoldPrimary
import com.example.ramazontaqvim.ui.theme.NightDeep
import com.example.ramazontaqvim.ui.theme.NightMid
import com.example.ramazontaqvim.ui.theme.WhiteDim
import com.example.ramazontaqvim.widget.RAMAZON_2026
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RamazonStatisticsScreen(modifier: Modifier = Modifier) {
    val today = LocalDate.now()
    val now = LocalDateTime.now()

    // Total ramazon kunlari
    val totalDays = RAMAZON_2026.size
    val passedDays = RAMAZON_2026.count { it.sana.isBefore(today) }
    val todayData = RAMAZON_2026.find { it.sana == today }
    val completedDays = if (todayData != null) {
        val iftorDt = LocalDateTime.of(today, todayData.iftor)
        if (now.isAfter(iftorDt)) passedDays + 1 else passedDays
    } else passedDays

    val remainingDays = (totalDays - completedDays).coerceAtLeast(0)
    val ramazonProgress = completedDays.toFloat() / totalDays.toFloat()

    // Bugungi ro'za foizi
    val todayProgress = if (todayData != null) {
        val saharDt = LocalDateTime.of(today, todayData.sahar)
        val iftorDt = LocalDateTime.of(today, todayData.iftor)
        when {
            now.isBefore(saharDt) -> 0f
            now.isAfter(iftorDt) -> 1f
            else -> {
                val total = java.time.Duration.between(saharDt, iftorDt).toMillis().toFloat()
                val elapsed = java.time.Duration.between(saharDt, now).toMillis().toFloat()
                (elapsed / total).coerceIn(0f, 1f)
            }
        }
    } else 0f

    // O'rtacha roza davomiyligi (soatda)
    val avgFastHours = RAMAZON_2026.map { d ->
        java.time.Duration.between(
            LocalDateTime.of(d.sana, d.sahar),
            LocalDateTime.of(d.sana, d.iftor)
        ).toMinutes() / 60.0
    }.average()

    // Bugungi roza davomiyligi
    val todayFastHours = todayData?.let {
        java.time.Duration.between(
            LocalDateTime.of(it.sana, it.sahar),
            LocalDateTime.of(it.sana, it.iftor)
        ).toMinutes() / 60.0
    } ?: avgFastHours

    // Eng uzun va eng qisqa roza
    val maxFast = RAMAZON_2026.maxByOrNull { d ->
        java.time.Duration.between(
            LocalDateTime.of(d.sana, d.sahar),
            LocalDateTime.of(d.sana, d.iftor)
        ).toMinutes()
    }
    val minFast = RAMAZON_2026.minByOrNull { d ->
        java.time.Duration.between(
            LocalDateTime.of(d.sana, d.sahar),
            LocalDateTime.of(d.sana, d.iftor)
        ).toMinutes()
    }

    // Qolgan umumiy roza soatlari
    val totalFastMinutesLeft = RAMAZON_2026
        .filter { it.sana.isAfter(today) }
        .sumOf { d ->
            java.time.Duration.between(
                LocalDateTime.of(d.sana, d.sahar),
                LocalDateTime.of(d.sana, d.iftor)
            ).toMinutes()
        }
    val totalFastHoursLeft = totalFastMinutesLeft / 60

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(NightDeep, NightMid, Color(0xFF091A14))
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(GoldAlpha30, Color.Transparent, GoldAlpha12)
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .clip(RoundedCornerShape(28.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            StatHeader()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // ── Ramazon umumiy jarayoni ──
                SectionTitle("📊 Ramazon Jarayoni")

                BigProgressCard(
                    title = "Umumiy Ramazon",
                    subtitle = "$completedDays / $totalDays kun tugallandi",
                    progress = ramazonProgress,
                    valueText = "${(ramazonProgress * 100).roundToInt()}%",
                    accentColor = GoldPrimary
                )

                // ── Bugungi ro'za ──
                SectionTitle("🌙 Bugungi Ro'za")

                BigProgressCard(
                    title = "Bugungi Ro'za",
                    subtitle = todayData?.let {
                        "${it.sahar} → ${it.iftor}"
                    } ?: "Ma'lumot yo'q",
                    progress = todayProgress,
                    valueText = "${(todayProgress * 100).roundToInt()}%",
                    accentColor = Color(0xFF4FC3F7)
                )

                // ── 4 ta mini stat karta ──
                SectionTitle("⏱ Statistika")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MiniStatCard(
                        icon = "🌙",
                        label = "Qolgan",
                        value = "$remainingDays kun",
                        modifier = Modifier.weight(1f)
                    )
                    MiniStatCard(
                        icon = "✅",
                        label = "Tugallandi",
                        value = "$completedDays kun",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MiniStatCard(
                        icon = "⏰",
                        label = "Bugun",
                        value = "%.1fh".format(todayFastHours),
                        modifier = Modifier.weight(1f)
                    )
                    MiniStatCard(
                        icon = "📐",
                        label = "O'rtacha",
                        value = "%.1fh".format(avgFastHours),
                        modifier = Modifier.weight(1f)
                    )
                }

                // ── Eng uzun / Eng qisqa ──
                SectionTitle("📈 Rekordlar")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    maxFast?.let { d ->
                        val h = java.time.Duration.between(
                            LocalDateTime.of(d.sana, d.sahar),
                            LocalDateTime.of(d.sana, d.iftor)
                        ).toMinutes() / 60.0
                        RecordCard(
                            icon = "🔥",
                            label = "Eng uzun",
                            value = "%.1fh".format(h),
                            date = d.sana.toString(),
                            color = Color(0xFFFF7043),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    minFast?.let { d ->
                        val h = java.time.Duration.between(
                            LocalDateTime.of(d.sana, d.sahar),
                            LocalDateTime.of(d.sana, d.iftor)
                        ).toMinutes() / 60.0
                        RecordCard(
                            icon = "❄️",
                            label = "Eng qisqa",
                            value = "%.1fh".format(h),
                            date = d.sana.toString(),
                            color = Color(0xFF4FC3F7),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // ── Qolgan soatlar ──
                SectionTitle("⚡ Qolgan Ro'za Soatlari")

                TotalHoursCard(hours = totalFastHoursLeft)

                // ── Kunlik bar chart (oxirgi 7 kun) ──
//                SectionTitle("📅 So'nggi 7 Kunlik Ro'za")

//                WeeklyBarChart(today = today)
//
//                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

// ─── Sub-components ──────────────────────────────────────────────────────────

@Composable
private fun StatHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    listOf(Color(0x668B5A14), GoldAlpha12, Color(0x558B5A14))
                )
            )
            .drawBehind {
                drawLine(
                    color = GoldAlpha30,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                text = "إِحْصَاءٌ",
                color = GoldPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                style = LocalTextStyle.current.copy(
                    shadow = Shadow(GoldLight.copy(alpha = 0.5f), blurRadius = 20f)
                )
            )
            Text(
                text = "STATISTIKA • RAMAZON 2026",
                color = GoldDim,
                fontSize = 9.sp,
                letterSpacing = 3.sp
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(GoldAlpha12, RoundedCornerShape(12.dp))
                .border(1.dp, GoldAlpha30, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "📊",
                fontSize = 24.sp
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = WhiteDim,
        fontSize = 11.sp,
        letterSpacing = 2.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    )
}

@Composable
private fun BigProgressCard(
    title: String,
    subtitle: String,
    progress: Float,
    valueText: String,
    accentColor: Color
) {
    val animProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1200, easing = EaseOutCubic),
        label = "big_progress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(0.04f), RoundedCornerShape(18.dp))
            .border(
                1.dp,
                Brush.horizontalGradient(listOf(accentColor.copy(0.4f), Color.Transparent)),
                RoundedCornerShape(18.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(subtitle, color = WhiteDim, fontSize = 11.sp)
                }
                Text(
                    text = valueText,
                    color = accentColor,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    style = LocalTextStyle.current.copy(
                        shadow = Shadow(accentColor.copy(0.5f), blurRadius = 16f)
                    )
                )
            }

            Spacer(Modifier.height(12.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color.White.copy(0.06f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(100.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(accentColor.copy(0.6f), accentColor, accentColor.copy(0.8f))
                            )
                        )
                )
                // Glow dot at end
                if (animProgress > 0.02f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animProgress)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniStatCard(
    icon: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.White.copy(0.04f), RoundedCornerShape(14.dp))
            .border(1.dp, GoldAlpha12, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(icon, fontSize = 22.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                color = GoldPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                color = WhiteDim,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RecordCard(
    icon: String,
    label: String,
    value: String,
    date: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color.copy(0.07f), RoundedCornerShape(14.dp))
            .border(1.dp, color.copy(0.3f), RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(icon, fontSize = 22.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                color = color,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                style = LocalTextStyle.current.copy(
                    shadow = Shadow(color.copy(0.5f), blurRadius = 12f)
                )
            )
            Text(label, color = WhiteDim, fontSize = 10.sp, textAlign = TextAlign.Center)
            Text(date, color = color.copy(0.6f), fontSize = 9.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun TotalHoursCard(hours: Long) {
    val animValue by animateFloatAsState(
        targetValue = hours.toFloat(),
        animationSpec = tween(1500, easing = EaseOutCubic),
        label = "total_hours"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(Color(0x33D4AF37), Color(0x228B5A14), Color(0x33D4AF37))
                ),
                RoundedCornerShape(18.dp)
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(GoldPrimary.copy(0.6f), GoldDim.copy(0.3f))),
                RoundedCornerShape(18.dp)
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("⏳", fontSize = 32.sp)
            Column {
                Text(
                    text = "${animValue.toLong()} soat",
                    color = GoldPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    style = LocalTextStyle.current.copy(
                        shadow = Shadow(GoldLight.copy(0.5f), blurRadius = 18f)
                    )
                )
                Text(
                    text = "ro'za tutish qoldi",
                    color = WhiteDim,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeeklyBarChart(today: LocalDate) {
    val last7 = (6 downTo 0).map { today.minusDays(it.toLong()) }

    // Max fast duration for normalization
    val durations = last7.map { date ->
        val d = RAMAZON_2026.find { it.sana == date }
        if (d != null) {
            java.time.Duration.between(
                LocalDateTime.of(d.sana, d.sahar),
                LocalDateTime.of(d.sana, d.iftor)
            ).toMinutes().toFloat()
        } else 0f
    }
    val maxDur = durations.maxOrNull()?.takeIf { it > 0f } ?: 1f

    val dayLabels = listOf("Du", "Se", "Ch", "Pa", "Ju", "Sh", "Ya")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(0.03f), RoundedCornerShape(16.dp))
            .border(1.dp, GoldAlpha12, RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        last7.forEachIndexed { i, date ->
            val fraction = (durations[i] / maxDur).coerceIn(0f, 1f)
            val isToday = date == today
            val hasData = RAMAZON_2026.any { it.sana == date }

            val animFraction by animateFloatAsState(
                targetValue = fraction,
                animationSpec = tween(
                    durationMillis = 800,
                    delayMillis = i * 80,
                    easing = EaseOutCubic
                ),
                label = "bar_$i"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                // Hour label on top
                if (hasData && durations[i] > 0f) {
                    Text(
                        text = "%.0fh".format(durations[i] / 60f),
                        color = if (isToday) GoldPrimary else WhiteDim,
                        fontSize = 8.sp,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                    )
                } else {
                    Text("", fontSize = 8.sp)
                }

                Spacer(Modifier.height(4.dp))

                // Bar
                Box(
                    modifier = Modifier
                        .width(22.dp)
                        .height(80.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Background bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(0.05f))
                    )
                    // Filled bar
                    if (animFraction > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(animFraction)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    Brush.verticalGradient(
                                        if (isToday)
                                            listOf(GoldLight, GoldPrimary)
                                        else
                                            listOf(GoldDim.copy(0.8f), GoldDim.copy(0.4f))
                                    )
                                )
                        )
                    }
                    // Today indicator
                    if (isToday) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-4).dp)
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = date.dayOfWeek.value.let { dow ->
                        listOf("Du", "Se", "Ch", "Pa", "Ju", "Sh", "Ya").getOrNull(dow - 1) ?: ""
                    },
                    color = if (isToday) GoldPrimary else WhiteDim,
                    fontSize = 9.sp,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}