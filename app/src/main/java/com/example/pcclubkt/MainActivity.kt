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
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.pcclubkt.ui.theme.PcclubktTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "pc_club_database"
        ).fallbackToDestructiveMigration().build()

        lifecycleScope.launch {
            val pcDao = db.pcDao()
            val adminDao = db.adminDao()
            val initialPcs = List(18) { index ->
                PcEntity(id = index + 1, name = "${index + 1} пк", isOccupied = false)
            }
            pcDao.insertComputers(initialPcs)
            adminDao.insertAdmin(AdminEntity(login = "admin", password = "admin", name = "admin"))
        }

        setContent {
            PcclubktTheme {
                var currentScreen by remember { mutableStateOf("splash") }
                var loggedInAdminName by remember { mutableStateOf("") }

                when (currentScreen) {
                    "splash" -> {
                        SplashScreen(
                            onSplashFinished = { currentScreen = "login" }
                        )
                    }
                    "login" -> {
                        LoginScreen(
                            db = db,
                            onLoginSuccess = { adminName -> loggedInAdminName = adminName
                                currentScreen = "hello" }
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
                        MainScreen(db = db)
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