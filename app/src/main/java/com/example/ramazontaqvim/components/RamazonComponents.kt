@file:OptIn(ExperimentalFoundationApi::class)

package com.example.ramazontaqvim.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ramazontaqvim.data.DuoType
import com.example.ramazontaqvim.data.Phase
import com.example.ramazontaqvim.ui.theme.GoldAlpha12
import com.example.ramazontaqvim.ui.theme.GoldAlpha30
import com.example.ramazontaqvim.ui.theme.GoldDim
import com.example.ramazontaqvim.ui.theme.GoldLight
import com.example.ramazontaqvim.ui.theme.GoldPrimary
import com.example.ramazontaqvim.ui.theme.WhiteDim
import com.example.ramazontaqvim.ui.theme.WhiteFaded
import com.example.ramazontaqvim.utils.toHms


@Composable
fun CountdownDisplay(millis: Long, isElapsed: Boolean = false) {

    val (h, m, s) = millis.coerceAtLeast(0).toHms()

    val infiniteAnim = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteAnim.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1200, easing = EaseInOutSine),
            RepeatMode.Reverse
        ),
        label = "glow"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (isElapsed) {
            Text(
                text = "+",
                color = GoldPrimary.copy(alpha = 0.7f),
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(end = 2.dp)
            )
        }
        TimeUnit(value = h, glowAlpha = glowAlpha)
        TimeSep()
        TimeUnit(value = m, glowAlpha = glowAlpha)
        TimeSep()
        TimeUnit(value = s, glowAlpha = glowAlpha)
    }
}

@Composable
fun TimeUnit(value: Long, glowAlpha: Float) {
    Text(
        text = "%02d".format(value),
        color = Color.White,
        fontSize = 48.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 2.sp,
        style = LocalTextStyle.current.copy(
            shadow = Shadow(
                color = GoldLight.copy(alpha = glowAlpha * 0.4f),
                blurRadius = 30f
            )
        )
    )
}

@Composable
fun TimeSep() {
    Text(
        text = ":",
        color = GoldPrimary.copy(alpha = 0.7f),
        fontSize = 40.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier.padding(horizontal = 2.dp)
    )
}

@Composable
fun ProgressSection(progress: Float, phase: Phase) {
    val animProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "progress"
    )

    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = when (phase) {
                    Phase.SAHAR_WAIT -> "Tun"
                    Phase.ROZA -> "Ro'za"
                    Phase.AFTER_IFTOR -> "Tugadi"
                },
                color = WhiteDim,
                fontSize = 10.sp
            )
            Text(
                text = "${(animProgress * 100).toInt()}%",
                color = GoldPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(6.dp))


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(Color.White.copy(alpha = 0.06f))
                .border(1.dp, GoldAlpha12, RoundedCornerShape(100.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(100.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(GoldDim, GoldPrimary, GoldLight)
                        )
                    )
                    .drawBehind {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                listOf(Color.Transparent, GoldLight.copy(alpha = 0.6f))
                            )
                        )
                    }
            )
        }
    }
}

@Composable
fun TimeCard(
    icon: String,
    label: String,
    time: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val borderBrush = if (isActive)
        Brush.verticalGradient(listOf(GoldPrimary, GoldDim))
    else
        Brush.verticalGradient(listOf(Color.White.copy(0.1f), Color.Transparent))

    val bgColor = if (isActive) GoldAlpha12 else Color.White.copy(0.03f)

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(16.dp))
            .border(1.dp, borderBrush, RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                color = WhiteDim,
                fontSize = 9.sp,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = time,
                color = if (isActive) GoldPrimary else WhiteFaded,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                style = if (isActive) LocalTextStyle.current.copy(
                    shadow = Shadow(GoldLight.copy(0.4f), blurRadius = 20f)
                ) else LocalTextStyle.current
            )
        }
    }
}

@Composable
fun DuoCard(type: DuoType, modifier: Modifier = Modifier) {

    var isZoomed by remember { mutableStateOf(false) }

    val uzbekSize = if (isZoomed) 20.sp else 11.sp

    val arabic =
        "اَللّٰهُمَّ لَكَ صُمْتُ وَبِكَ آمَنْتُ وَعَلَيْكَ تَوَكَّلْتُ وَعَلٰى رِزْقِكَ اَفْطَرْتُ"
    val uzbek = when (type) {
        DuoType.SAHAR -> "Navvaytu an asuma sovma shahri romazona minal fajri ilal mag‘ribi, xolisan lillahi ta’ala. Allohu Akbar!"
        DuoType.IFTOR -> "Allohumma laka sumtu va bika aamantu va a’layka tavakkaltu va a’laa rizqika aftartu, fag‘firliy yaa g‘offaaru maa qoddamtu va maa axxortu"
    }
    val title = when (type) {
        DuoType.SAHAR -> "⚡ Saharlik duosi"
        DuoType.IFTOR -> "⚡ Og'iz ochish duosi"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = { isZoomed = !isZoomed }
            )
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0x33D4AF37),
                        Color(0x228B5A14),
                        Color(0x33D4AF37)
                    )
                ),
                RoundedCornerShape(18.dp)
            ).animateContentSize()
            .border(
                1.dp,
                Brush.verticalGradient(listOf(GoldPrimary.copy(0.6f), GoldDim.copy(0.3f))),
                RoundedCornerShape(18.dp)
            )
            .padding(16.dp)
    ) {
        Column {

            Text(
                text = title,
                color = GoldLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = arabic,
                color = Color.White.copy(0.9f),
                fontSize = 16.sp,
                textAlign = TextAlign.End,
                lineHeight = 26.sp,
                modifier = Modifier.fillMaxWidth(),
                style = LocalTextStyle.current.copy(
                    shadow = Shadow(GoldLight.copy(0.3f), blurRadius = 12f)
                )
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = uzbek,
                color = GoldPrimary.copy(0.75f),
                fontSize = uzbekSize,
                fontStyle = FontStyle.Italic
            )
        }
    }
}
@Composable
fun DemoNotice() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(0.03f), RoundedCornerShape(10.dp))
            .border(1.dp, Color.White.copy(0.07f), RoundedCornerShape(10.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "⚠ Demo rejim • Hozir Ramazon emas",
            color = WhiteDim,
            fontSize = 10.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun Header(kun: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    listOf(Color(0x668B5A14), GoldAlpha12, Color(0x558B5A14))
                )
            )
            .border(
                width = 0.dp,
                color = Color.Transparent
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

        Column(
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            Text(
                text = "رَمَضَانُ كَرِيمٌ",
                color = GoldPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                style = LocalTextStyle.current.copy(
                    shadow = Shadow(
                        color = GoldLight.copy(alpha = 0.5f),
                        blurRadius = 20f
                    )
                )
            )
            Text(
                text = "RAMAZON MUBORAK • 2026",
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
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$kun",
                    color = GoldPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 22.sp
                )
                Text(
                    text = "KUN",
                    color = GoldDim,
                    fontSize = 8.sp,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}
