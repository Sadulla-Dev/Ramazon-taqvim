package com.example.ramazontaqvim.widget.small

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider
import com.example.ramazontaqvim.MainActivity
import com.example.ramazontaqvim.ui.theme.GoldAlpha10
import com.example.ramazontaqvim.ui.theme.GoldAlpha20
import com.example.ramazontaqvim.ui.theme.GoldPrimary
import com.example.ramazontaqvim.ui.theme.NightDeep
import com.example.ramazontaqvim.ui.theme.WhiteDim
import com.example.ramazontaqvim.ui.theme.WhiteFaded
import com.example.ramazontaqvim.data.RamazonPhase
import com.example.ramazontaqvim.data.WidgetState
import com.example.ramazontaqvim.data.calculateWidgetState

class SmallRamazonWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SmallRamazonGlanceWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        SmallWidgetAlarmReceiver.start(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        SmallWidgetAlarmReceiver.stop(context)
    }
}

class SmallRamazonGlanceWidget(private val state: WidgetState? = null) : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(
        setOf(DpSize(110.dp, 110.dp))
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            SmallWidgetContent(state = state ?: calculateWidgetState())
        }
    }
}

@Composable
private fun SmallWidgetContent(state: WidgetState) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .clickable(actionStartActivity<MainActivity>())
            .background(NightDeep)
            .appWidgetBackground()
            .cornerRadius(20.dp)
            .padding(8.dp)
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = GlanceModifier
                        .background(GoldAlpha10)
                        .cornerRadius(8.dp)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${state.kun}-kun",
                        style = TextStyle(
                            color = ColorProvider(GoldPrimary),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(GlanceModifier.defaultWeight())

                Box(
                    modifier = GlanceModifier
                        .size(36.dp)
                        .background(GoldAlpha10)
                        .cornerRadius(18.dp)
                        .clickable(actionRunCallback<SmallReloadWidgetAction>()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "↻",
                        style = TextStyle(
                            color = ColorProvider(GoldPrimary),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(GlanceModifier.height(4.dp))

            Text(
                text = when (state.phase) {
                    RamazonPhase.SAHAR_WAIT -> "SAHARGACHA"
                    RamazonPhase.ROZA -> "IFTORGACHA"
                    RamazonPhase.IFTOR_DONE -> "IFTOR O'TDI"
                    RamazonPhase.SAHAR_DONE -> "SAHAR O'TDI"
                },
                style = TextStyle(color = ColorProvider(WhiteDim), fontSize = 10.sp)
            )

            Spacer(GlanceModifier.height(2.dp))

            Text(
                text = state.countdownText,
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(GlanceModifier.height(4.dp))

            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = state.progressPercent / 100f,
                    modifier = GlanceModifier
                        .defaultWeight()
                        .height(6.dp)
                        .cornerRadius(100.dp),
                    color = ColorProvider(GoldPrimary),
                    backgroundColor = ColorProvider(Color(0x1AFFFFFF))
                )
                Spacer(GlanceModifier.width(4.dp))
                Text(
                    text = "${state.progressPercent}%",
                    style = TextStyle(
                        color = ColorProvider(GoldPrimary),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(GlanceModifier.height(6.dp))

            Column(
                modifier = GlanceModifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = GlanceModifier.fillMaxWidth()
                        .background(
                            if (state.phase == RamazonPhase.SAHAR_WAIT) GoldAlpha20
                            else Color(0x0AFFFFFF)
                        )
                        .cornerRadius(8.dp)
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                        verticalAlignment = Alignment.Vertical.CenterVertically
                    ) {
                        Text(
                            text = state.saharTime,
                            style = TextStyle(
                                color = ColorProvider(
                                    if (state.phase == RamazonPhase.SAHAR_WAIT) GoldPrimary else WhiteFaded
                                ),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(GlanceModifier.width(4.dp))
                        Text(
                            text = "SAHARLIK",
                            style = TextStyle(color = ColorProvider(WhiteDim), fontSize = 10.sp)
                        )
                    }
                }

                Spacer(GlanceModifier.height(6.dp))

                Box(
                    modifier = GlanceModifier.fillMaxWidth()
                        .background(
                            if (state.phase == RamazonPhase.ROZA) GoldAlpha20
                            else Color(0x0AFFFFFF)
                        )
                        .cornerRadius(8.dp)
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                        verticalAlignment = Alignment.Vertical.CenterVertically
                    ) {
                        Text(
                            text = state.iftorTime,
                            style = TextStyle(
                                color = ColorProvider(
                                    if (state.phase == RamazonPhase.ROZA) GoldPrimary else WhiteFaded
                                ),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(GlanceModifier.width(4.dp))
                        Text(
                            text = "IFTORLIK",
                            style = TextStyle(color = ColorProvider(WhiteDim), fontSize = 10.sp)
                        )
                    }
                }
            }
        }
    }
}
