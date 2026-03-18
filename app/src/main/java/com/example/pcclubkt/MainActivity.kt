package com.example.pcclubkt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.example.pcclubkt.ui.theme.PcclubktTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PcclubktTheme {
                var showSplash by remember { mutableStateOf(true) }
                var isLoggedIn by remember { mutableStateOf(false) }
                var isShiftStarted by remember { mutableStateOf(false) }
                if (showSplash) {
                    SplashScreen(
                        onSplashFinished = { showSplash = false }
                    )
                } else if (!isLoggedIn) {
                    LoginScreen(
                        onLoginClick = { isLoggedIn = true }
                    )
                } else if (!isShiftStarted) {
                    HelloScreen(
                        onStartShiftClick = { isShiftStarted = true }
                    )
                } else {
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    LaunchedEffect(key1 = true) {
        delay(2000L)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Red)) {
                    append("MY")
                }
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append("CLUB")
                }
            },
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}