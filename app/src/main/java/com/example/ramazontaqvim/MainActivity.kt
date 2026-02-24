package com.example.ramazontaqvim

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.ramazontaqvim.screen.NightDeep
import com.example.ramazontaqvim.screen.RamazonScreen
import com.example.ramazontaqvim.ui.theme.RamazonTaqvimTheme
import com.example.ramazontaqvim.widget.big.RamazonWidgetWorker

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            RamazonTaqvimTheme {
                RamazonApp()
                RamazonWidgetWorker.schedule(this)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RamazonApp() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NightDeep)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            RamazonScreen(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
