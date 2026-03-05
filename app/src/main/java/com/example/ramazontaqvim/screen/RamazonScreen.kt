@file:OptIn(ExperimentalFoundationApi::class)

package com.example.ramazontaqvim.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ramazontaqvim.components.CountdownDisplay
import com.example.ramazontaqvim.components.DemoNotice
import com.example.ramazontaqvim.components.DuoCard
import com.example.ramazontaqvim.components.Header
import com.example.ramazontaqvim.components.ProgressSection
import com.example.ramazontaqvim.components.RamadanFinishedScreen
import com.example.ramazontaqvim.components.TimeCard
import com.example.ramazontaqvim.data.DuoType
import com.example.ramazontaqvim.data.Phase
import com.example.ramazontaqvim.data.RAMAZON_2026
import com.example.ramazontaqvim.ui.theme.GoldAlpha12
import com.example.ramazontaqvim.ui.theme.GoldAlpha30
import com.example.ramazontaqvim.ui.theme.GoldPrimary
import com.example.ramazontaqvim.ui.theme.NightDeep
import com.example.ramazontaqvim.ui.theme.NightMid
import com.example.ramazontaqvim.ui.theme.WhiteDim
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BoxScope.RamazonScreen(modifier: Modifier = Modifier) {

    var now by remember { mutableStateOf(LocalDateTime.now()) }
    var isTableExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            now = LocalDateTime.now()
        }
    }
    val today = now.toLocalDate()

    val data = RAMAZON_2026.find { it.sana == today } ?: RAMAZON_2026.first()
    val isDemo = RAMAZON_2026.none { it.sana == today }

    val saharDt = LocalDateTime.of(data.sana, data.sahar)
    val iftorDt = LocalDateTime.of(data.sana, data.iftor)

    val msSahar = java.time.Duration.between(now, saharDt).toMillis()
    val msIftor = java.time.Duration.between(now, iftorDt).toMillis()

    val saharDtPlus2h = saharDt.plusHours(2)
    val iftorDtPlus2h = iftorDt.plusHours(2)

    val duoType = when {
        now.isBefore(saharDtPlus2h) -> DuoType.SAHAR
        now.isBefore(iftorDt) -> DuoType.IFTOR
        now.isBefore(iftorDtPlus2h) -> DuoType.IFTOR
        else -> DuoType.SAHAR
    }

    val tomorrowData = RAMAZON_2026.find { it.sana == today.plusDays(1) }
    val nextSaharDt = tomorrowData?.let {
        LocalDateTime.of(it.sana, it.sahar)
    } ?: saharDt.plusDays(1)

    val phase = when {
        now.isBefore(saharDt) -> Phase.SAHAR_WAIT
        now.isBefore(iftorDt) -> Phase.ROZA
        else -> Phase.AFTER_IFTOR
    }


    val isWithinSaharWindow = phase == Phase.ROZA && now.isBefore(saharDtPlus2h)
    val isWithinIftorWindow = phase == Phase.AFTER_IFTOR && now.isBefore(iftorDtPlus2h)

    val countdownLabel = when {
        phase == Phase.SAHAR_WAIT -> "SAHARLIKGACHA"
        isWithinSaharWindow -> "SAHARLIKDAN O'TDI"
        phase == Phase.ROZA -> "IFTORGACHA"
        isWithinIftorWindow -> "IFTORLIKDAN O'TDI"
        else -> "SAHARLIKGACHA"
    }


    val countdown = when {
        phase == Phase.SAHAR_WAIT -> msSahar
        isWithinSaharWindow -> java.time.Duration.between(saharDt, now).toMillis()
        phase == Phase.ROZA -> msIftor
        isWithinIftorWindow -> java.time.Duration.between(iftorDt, now).toMillis()
        else -> java.time.Duration.between(now, nextSaharDt).toMillis()
    }

    val progress = when (phase) {
        Phase.SAHAR_WAIT -> {
            val total = java.time.Duration.between(
                LocalDateTime.of(data.sana, LocalTime.MIDNIGHT), saharDt
            ).toMillis().toFloat()
            val elapsed = java.time.Duration.between(
                LocalDateTime.of(data.sana, LocalTime.MIDNIGHT), now
            ).toMillis().toFloat()
            (elapsed / total).coerceIn(0f, 1f)
        }

        Phase.ROZA -> {
            val total = java.time.Duration.between(saharDt, iftorDt).toMillis().toFloat()
            val elapsed = java.time.Duration.between(saharDt, now).toMillis().toFloat()
            (elapsed / total).coerceIn(0f, 1f)
        }

        Phase.AFTER_IFTOR -> 1f
    }
    val lastDay = RAMAZON_2026.last().sana
    val isRamadanFinished = today.isAfter(lastDay)
    if (isRamadanFinished) {
        RamadanFinishedScreen(Modifier.align(Alignment.Center))
        return
    }
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
            .padding(0.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            item { Header(kun = data.kun) }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    AnimatedContent(
                        targetState = duoType,
                        transitionSpec = {
                            fadeIn(tween(400)) togetherWith fadeOut(tween(400))
                        },
                        label = "duoSwitch"
                    ) { type ->
                        DuoCard(
                            type = type,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Text(
                        text = countdownLabel,
                        color = WhiteDim,
                        fontSize = 11.sp,
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(8.dp))

                    CountdownDisplay(
                        millis = countdown,
                        isElapsed = isWithinSaharWindow || isWithinIftorWindow
                    )

                    Spacer(Modifier.height(16.dp))

                    ProgressSection(progress = progress, phase = phase)

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {


                        TimeCard(
                            icon = "☀️",
                            label = "SAHARLIK",
                            time = data.sahar.format(DateTimeFormatter.ofPattern("HH:mm")),
                            isActive = now.isBefore(saharDtPlus2h),
                            modifier = Modifier.weight(1f)
                        )

                        TimeCard(
                            icon = "\uD83C\uDF19",
                            label = "IFTOR",
                            time = data.iftor.format(DateTimeFormatter.ofPattern("HH:mm")),
                            isActive = now.isAfter(saharDtPlus2h) && now.isBefore(iftorDtPlus2h),
                            modifier = Modifier.weight(1f)
                        )
                    }



                    if (isDemo) {
                        Spacer(Modifier.height(12.dp))
                        DemoNotice()
                    }

                    Spacer(Modifier.height(8.dp))
                }
                Spacer(Modifier.height(16.dp))
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topEnd = 14.dp, topStart = 14.dp))
                        .background(Color.White.copy(0.03f))
                        .clickable { isTableExpanded = !isTableExpanded }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isTableExpanded) "▲ Ramazon jadvalini yopish"
                        else "▼ Barcha Ramazon kunlari",
                        color = GoldPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = isTableExpanded,
                    enter = expandVertically(
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = LinearOutSlowInEasing
                        )
                    ),
                    exit = shrinkVertically(
                        animationSpec = tween(
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeOut(
                        animationSpec = tween(
                            durationMillis = 400,
                            easing = LinearOutSlowInEasing
                        )
                    )
                ) {
                    RamazonTable(today = today)
                }
            }
        }
    }
}
