package com.example.pcclubkt

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.example.pcclubkt.database.AppDatabase
import com.example.pcclubkt.screens.HelloScreen
import com.example.pcclubkt.screens.LoginScreen
import com.example.pcclubkt.screens.MainScreen
import com.example.pcclubkt.ui.theme.PcclubktTheme
import kotlinx.coroutines.delay
import androidx.compose.runtime.mutableIntStateOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "my_pc_club_db_v39"
        )
            .fallbackToDestructiveMigration()
            .createFromAsset("my_pc_club.db")
            .build()

        setContent {
            PcclubktTheme {
                val context = LocalContext.current
                val sharedPreferences =
                    context.getSharedPreferences("PcClubPrefs", Context.MODE_PRIVATE)

                val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
                val savedName = sharedPreferences.getString("savedUsername", "Адмін") ?: "Адмін"
                val savedId = sharedPreferences.getInt("savedStaffId", 1)
                var currentScreen by remember { mutableStateOf("splash") }
                var loggedInAdminName by remember { mutableStateOf(savedName) }
                var loggedInStaffId by remember { mutableIntStateOf(savedId) }
                when (currentScreen) {
                    "splash" -> {
                        SplashScreen(
                            onSplashFinished = {
                                if (isLoggedIn) {
                                    currentScreen = "hello"
                                } else {
                                    currentScreen = "login"
                                }
                            }
                        )
                    }

                    "login" -> {
                        LoginScreen(
                            db = db,
                            onLoginSuccess = { adminName, staffId ->
                                loggedInAdminName = adminName
                                loggedInStaffId = staffId
                                sharedPreferences.edit().apply {
                                    putBoolean("isLoggedIn", true)
                                    putString("savedUsername", adminName)
                                    putInt("savedStaffId", staffId)
                                    apply()
                                }
                                currentScreen = "hello"
                            }
                        )
                    }

                    "hello" -> {
                        HelloScreen(
                            adminName = loggedInAdminName,
                            {
                                currentScreen = "main"
                            }
                        )
                    }

                    "main" -> {
                        MainScreen(
                            db = db,
                            currentStaffId = loggedInStaffId,
                            onLogout = {
                                sharedPreferences.edit().apply {
                                    putBoolean("isLoggedIn", false)
                                    remove("savedUsername")
                                    remove("savedStaffId")
                                    apply()
                                }
                                currentScreen = "login"
                            }
                        )
                    }
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