package com.example.pcclubkt.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pcclubkt.database.ComputerDao
import com.example.pcclubkt.database.ComputerEntity
import kotlinx.coroutines.launch

@Composable
fun PcGridScreen(computerDao: ComputerDao) {
    val backgroundColor = Color.White
    val computerList by computerDao.getAllComputersFlow().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Text(
            text = "Керування ПК",
            fontSize = 24.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(computerList) { computer ->
                ComputerCard(
                    computer = computer,
                    onStatusChange = { newStatus ->
                        coroutineScope.launch {
                            computerDao.updateComputer(computer.copy(Status = newStatus))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ComputerCard(computer: ComputerEntity, onStatusChange: (String) -> Unit) {
    val statusColor = if (computer.Status == "occupied") Color(0xFFFF0000) else Color(0xFF00FF00)
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(statusColor)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${computer.ComputerID} ПК",
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFDD4B4B))
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF424242))
        ) {
            DropdownMenuItem(
                text = { Text("Увімкнути (Зайняти)", color = Color(0xFFEF5350), fontSize = 12.sp) },
                onClick = {
                    expanded = false
                    onStatusChange("occupied")
                }
            )
            DropdownMenuItem(
                text = { Text("Вимкнути (Звільнити)", color = Color(0xFF66BB6A), fontSize = 12.sp) },
                onClick = {
                    expanded = false
                    onStatusChange("available")
                }
            )
        }
    }
}