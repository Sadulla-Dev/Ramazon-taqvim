package com.example.ramazontaqvim.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ramazontaqvim.ui.theme.GoldAlpha12
import com.example.ramazontaqvim.widget.RAMAZON_2026
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RamazonTable(today: LocalDate) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(0.03f))
            .heightIn(max = 1900.dp)
            .padding(horizontal = 12.dp)
    ) {
        RAMAZON_2026.forEachIndexed { index, day ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .background(
                        if (day.sana == today) GoldAlpha12 else Color.Transparent,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    modifier = Modifier.padding(end = 10.dp),
                    text = day.kun.toString(),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = day.haftaKuni.replaceFirstChar { it.uppercase() },
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = day.sana.format(DateTimeFormatter.ofPattern("d-MMMM")),
                    color = Color(0xFFB0BEC5),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Start
                )

                Spacer(Modifier.weight(1f))

                Text(
                    text = day.sahar.format(DateTimeFormatter.ofPattern("HH:mm")),
                    color = Color(0xFF8BC34A),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = day.iftor.format(DateTimeFormatter.ofPattern("HH:mm")),
                    color = Color(0xFFFFEB3B),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start
                )
            }

            if (index < RAMAZON_2026.lastIndex) {
                HorizontalDivider(
                    thickness = DividerDefaults.Thickness,
                    color = Color.White.copy(0.08f)
                )
            }
        }
    }
}
