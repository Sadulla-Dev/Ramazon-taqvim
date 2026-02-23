package com.example.ramazontaqvim.screen

import androidx.compose.runtime.Composable

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.ramazontaqvim.RAMAZON_2026
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RamazonTable(today: LocalDate) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White.copy(0.03f),
            )
            .padding(horizontal = 12.dp)
    ) {

        RAMAZON_2026.forEach { day ->

            val isToday = day.sana == today

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .background(
                        if (isToday) GoldAlpha12 else Color.Transparent,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "${day.kun}-kun",
                    color = if (isToday) GoldPrimary else WhiteFaded,
                    fontSize = 11.sp,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                )

                Text(
                    text = day.sana.format(DateTimeFormatter.ofPattern("dd MMM")),
                    color = WhiteDim,
                    fontSize = 11.sp
                )

                Text(
                    text = "${day.sahar.format(DateTimeFormatter.ofPattern("HH:mm"))}  •  ${
                        day.iftor.format(DateTimeFormatter.ofPattern("HH:mm"))
                    }",
                    color = WhiteFaded,
                    fontSize = 11.sp
                )
            }
        }
    }
}
