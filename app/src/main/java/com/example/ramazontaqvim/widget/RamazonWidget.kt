package com.example.ramazontaqvim.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider
import com.example.ramazontaqvim.MainActivity


// ════════════════════════════════════════════════════════════════════
//  COLOR PALETTE
// ════════════════════════════════════════════════════════════════════
private val NightDeep = Color(0xFF080B14)
private val GoldPrimary = Color(0xFFD4AF37)
private val GoldLight = Color(0xFFF5D76E)
private val GoldDim = Color(0xFF8B6914)
private val GoldAlpha20 = Color(0x33D4AF37)
private val GoldAlpha10 = Color(0x1AD4AF37)
private val WhiteFaded = Color(0xCCFFFFFF)
private val WhiteDim = Color(0x66FFFFFF)

class RamazonWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = RamazonGlanceWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        RamazonWidgetUpdateReceiver.start(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        RamazonWidgetUpdateReceiver.stop(context)
    }
}

class RamazonGlanceWidget(private val state: WidgetState? = null) : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(360.dp, 220.dp),
        )
    )
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { Content(state = state ?: calculateWidgetState()) }
    }


    @Composable
    private fun Content(state: WidgetState) {
        LargeWidget(state)
    }
}


@Composable
private fun LargeWidget(state: WidgetState) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()  .clickable(
                actionStartActivity<MainActivity>()
            )
            .background(NightDeep)
            .appWidgetBackground()
            .cornerRadius(24.dp)
            .padding(8.dp)
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {

            // Header
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(GlanceModifier.width(8.dp))
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = "رَمَضَانُ كَرِيمٌ",
                        style = TextStyle(
                            color = ColorProvider(GoldPrimary),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "RAMAZON MUBORAK • 2026",
                        style = TextStyle(color = ColorProvider(GoldDim), fontSize = 9.sp)
                    )
                }

                Box(
                    modifier = GlanceModifier
                        .size(40.dp)
                        .background(GoldAlpha10)
                        .cornerRadius(100.dp)
                        .clickable(actionRunCallback<ReloadWidgetAction>()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "↻",
                        style = TextStyle(
                            color = ColorProvider(GoldDim),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(GlanceModifier.width(8.dp))
                Box(
                    modifier = GlanceModifier
                        .background(GoldAlpha10)
                        .cornerRadius(12.dp)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${state.kun}",
                            style = TextStyle(
                                color = ColorProvider(GoldPrimary),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "KUN",
                            style = TextStyle(color = ColorProvider(GoldDim), fontSize = 8.sp)
                        )
                    }
                }

            }

            Spacer(GlanceModifier.height(10.dp))

            Column(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when (state.phase) {
                        RamazonPhase.SAHAR_WAIT -> "SAHARLIKGACHA"
                        RamazonPhase.ROZA -> "IFTORGACHA"
                        RamazonPhase.IFTOR_DONE -> "IFTOR O'TDI"
                        RamazonPhase.SAHAR_DONE -> "SAHAR O'TDI"
                    },
                    style = TextStyle(color = ColorProvider(WhiteDim), fontSize = 10.sp)
                )
                Spacer(GlanceModifier.height(4.dp))
                Text(
                    text = state.countdownText,
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(GlanceModifier.height(10.dp))

            // Progress bar
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = state.progressPercent / 100f,
                    modifier = GlanceModifier
                        .defaultWeight()
                        .height(5.dp)
                        .cornerRadius(100.dp),
                    color = ColorProvider(GoldPrimary),
                    backgroundColor = ColorProvider(Color(0x1AFFFFFF))
                )
                Spacer(GlanceModifier.width(8.dp))
                Text(
                    text = "${state.progressPercent}%",
                    style = TextStyle(
                        color = ColorProvider(GoldPrimary),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(GlanceModifier.height(8.dp))

            // Vaqt kartalari
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .background(
                            if (state.phase == RamazonPhase.SAHAR_WAIT) GoldAlpha20 else Color(
                                0x0AFFFFFF
                            )
                        )
                        .cornerRadius(14.dp)
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.saharTime,
                            style = TextStyle(
                                color = ColorProvider(if (state.phase == RamazonPhase.SAHAR_WAIT) GoldPrimary else WhiteFaded),
                                fontSize = 22.sp, fontWeight = FontWeight.Bold
                            )
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = GlanceModifier.padding(start = 5.dp)
                        ) {

                            Text(text = "☀\uFE0F", style = TextStyle(fontSize = 16.sp))
                            Spacer(GlanceModifier.height(2.dp))
                            Text(
                                text = "SAHARLIK",
                                style = TextStyle(color = ColorProvider(WhiteDim), fontSize = 8.sp)
                            )
                            Spacer(GlanceModifier.height(2.dp))
                        }

                    }
                }

                Spacer(GlanceModifier.width(10.dp))

                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .background(
                            if (state.phase == RamazonPhase.ROZA && state.showDuo) GoldAlpha20 else Color(
                                0x0AFFFFFF
                            )
                        )
                        .cornerRadius(14.dp)
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.iftorTime,
                            style = TextStyle(
                                color = ColorProvider(if (state.phase == RamazonPhase.ROZA && state.showDuo) GoldPrimary else WhiteFaded),
                                fontSize = 22.sp, fontWeight = FontWeight.Bold
                            )
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = GlanceModifier.padding(start = 5.dp)
                        ) {
                            Text(text = "🌙", style = TextStyle(fontSize = 16.sp))
                            Spacer(GlanceModifier.height(2.dp))
                            Text(
                                text = "IFTOR",
                                style = TextStyle(color = ColorProvider(WhiteDim), fontSize = 8.sp)
                            )
                            Spacer(GlanceModifier.height(2.dp))
                        }
                    }
                }
            }
        }
    }
}