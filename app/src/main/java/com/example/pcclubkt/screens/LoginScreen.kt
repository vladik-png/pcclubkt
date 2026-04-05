package com.example.pcclubkt.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pcclubkt.database.AppDatabase
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    db: AppDatabase,
    onLoginSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("PcClubPrefs", Context.MODE_PRIVATE)

    var loginText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val brandColor = Color(0xFFFF8484)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Вхід",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            color = brandColor
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = loginText,
            onValueChange = {
                loginText = it
                errorMessage = ""
            },
            label = { Text("Логін", color = Color.Gray) },
            trailingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Black)
            },
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                cursorColor = brandColor
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = passwordText,
            onValueChange = {
                passwordText = it
                errorMessage = ""
            },
            label = { Text("Пароль", color = Color.Gray) },
            trailingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black)
            },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                cursorColor = brandColor
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                val cleanLogin = loginText.trim()
                val cleanPassword = passwordText.trim()

                if (cleanLogin.isEmpty() || cleanPassword.isEmpty()) {
                    errorMessage = "Заповніть усі поля"
                } else {
                    scope.launch {
                        val loginData = db.loginDao().getUserByUsername(cleanLogin)

                        if (loginData != null && loginData.Password == cleanPassword) {

                            sharedPreferences.edit()
                                .putBoolean("isLoggedIn", true)
                                .putString("savedUsername", cleanLogin)
                                .apply()

                            val staffMember = db.staffDao().getStaffByUsername(cleanLogin)
                            val displayName = staffMember?.FullName ?: loginData.Username ?: "Адмін"

                            onLoginSuccess(displayName)
                        } else {
                            errorMessage = "Такого користувача не знайдено або пароль невірний"
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = brandColor)
        ) {
            Text(
                text = "Вхід",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    }
}