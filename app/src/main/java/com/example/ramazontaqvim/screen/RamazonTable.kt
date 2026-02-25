package com.example.ramazontaqvim.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ramazontaqvim.ui.theme.GoldAlpha12
import com.example.ramazontaqvim.ui.theme.GoldPrimary
import com.example.ramazontaqvim.ui.theme.WhiteDim
import com.example.ramazontaqvim.ui.theme.WhiteFaded
import com.example.ramazontaqvim.widget.RAMAZON_2026
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RamazonTable(today: LocalDate) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(0.03f))
            .heightIn(max = 1900.dp)
            .padding(horizontal = 12.dp)
    ) {
        itemsIndexed(RAMAZON_2026) { index, day ->

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
