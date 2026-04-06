package com.example.pcclubkt.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pcclubkt.database.EventDao
import com.example.pcclubkt.database.EventsEntity
import kotlinx.coroutines.launch

@Composable
fun EventsScreen(eventDao: EventDao) {
    val events by eventDao.getAllEvents().collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color(0xFFF3F3F3),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFFFF8484),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Додати івент")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Турніри Клубу", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            if (events.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Івентів поки немає", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(events) { event ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Івент №${event.EventID}", fontWeight = FontWeight.Bold)
                                    Text(event.EventDate ?: "Без дати", color = Color.Gray, fontSize = 14.sp)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text("Призовий фонд: ${event.Prize ?: 0} ₴", color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
                                Text("Кількість учасників: ${event.CountPeople ?: 0}", fontSize = 14.sp)
                                Text(
                                    text = if (event.Status == 1) "Завершено" else "Активний",
                                    color = if (event.Status == 1) Color.Gray else Color(0xFFFF8484),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddEventDialog(
            onDismiss = { showDialog = false },
            onSave = { newEvent ->
                scope.launch {
                    eventDao.insertEvent(newEvent)
                    showDialog = false
                }
            }
        )
    }
}

@Composable
fun AddEventDialog(onDismiss: () -> Unit, onSave: (EventsEntity) -> Unit) {
    var date by remember { mutableStateOf("") }
    var prize by remember { mutableStateOf("") }
    var people by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новий івент") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Дата (напр. 15.04)") })
                OutlinedTextField(value = prize, onValueChange = { prize = it }, label = { Text("Призовий фонд (₴)") })
                OutlinedTextField(value = people, onValueChange = { people = it }, label = { Text("Кількість людей") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(EventsEntity(
                    GameID = 1,
                    EventDate = date,
                    Prize = prize.toIntOrNull() ?: 0,
                    CountPeople = people.toIntOrNull() ?: 0,
                    CustomerID = null,
                    Status = 0
                ))
            }) { Text("Створити") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Скасувати") }
        }
    )
}