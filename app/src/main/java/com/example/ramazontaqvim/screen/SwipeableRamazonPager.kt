package com.example.ramazontaqvim.screen

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.ramazontaqvim.ui.theme.GoldAlpha12
import com.example.ramazontaqvim.ui.theme.GoldPrimary
import kotlinx.coroutines.launch


@SuppressLint("UnusedBoxWithConstraintsScope")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SwipeableRamazonPager(modifier: Modifier = Modifier) {
    var currentPage by remember { mutableIntStateOf(0) }
    val pageCount = 2
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val pageWidthPx = constraints.maxWidth.toFloat()
        val swipeThreshold = pageWidthPx * 0.1f

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .pointerInput(currentPage) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                val cur = offsetX.value
                                when {
                                    cur < -swipeThreshold && currentPage < pageCount - 1 -> {
                                        offsetX.animateTo(
                                            -pageWidthPx,
                                            spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)
                                        )
                                        currentPage++
                                        offsetX.snapTo(0f)
                                    }
                                    cur > swipeThreshold && currentPage > 0 -> {
                                        offsetX.animateTo(
                                            pageWidthPx,
                                            spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)
                                        )
                                        currentPage--
                                        offsetX.snapTo(0f)
                                    }
                                    else -> offsetX.animateTo(
                                        0f,
                                        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow)
                                    )
                                }
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                offsetX.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                val raw = offsetX.value + dragAmount
                                val bounded = when {
                                    raw > 0f && currentPage == 0 -> raw * 0.25f
                                    raw < 0f && currentPage == pageCount - 1 -> raw * 0.25f
                                    else -> raw
                                }
                                offsetX.snapTo(bounded)
                            }
                        }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationX = (-currentPage * pageWidthPx) + offsetX.value
                    }
            ) {
                RamazonScreen(            modifier = Modifier.align(Alignment.Center).fillMaxWidth().padding(16.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationX = ((1 - currentPage) * pageWidthPx) + offsetX.value
                    }
            ) {
                RamazonStatisticsScreen(            modifier = Modifier.align(Alignment.Center).fillMaxWidth().padding(16.dp))
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 30.dp)
                    .background(
                        GoldAlpha12 ,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pageCount) { index -> PageDot(isActive = index == currentPage, modifier = Modifier.align(Alignment.CenterVertically)) }
            }
        }
    }
}

@Composable
private fun PageDot(isActive: Boolean,modifier : Modifier) {
    val width by animateDpAsState(
        targetValue = if (isActive) 20.dp else 6.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "dot_width"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.35f,
        animationSpec = tween(300),
        label = "dot_alpha"
    )

    Box(
        modifier = modifier
            .width(width)
            .height(6.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(GoldPrimary.copy(alpha = alpha))
    )
}