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
import com.example.pcclubkt.database.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventsScreen(
    eventDao: EventDao,
    gameDao: GameDao,
    customerDao: CustomerDao,
    statisticsDao: StatisticsDao
) {
    val events by eventDao.getAllEvents().collectAsState(initial = emptyList())
    val games by gameDao.getAllGames().collectAsState(initial = emptyList())
    val customers by customerDao.getAllCustomers().collectAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color(0xFFF3F3F3),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFFFF8484)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Турніри Клубу", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(events) { event ->
                    val gameName = games.find { it.GameID == event.GameID }?.GameName ?: "Гра #${event.GameID}"
                    val creatorName = customers.find { it.CustomerID == event.CustomerID }?.FullName ?: "Адмін"

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
                                Text(gameName, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                                Text(event.EventDate ?: "", color = Color.Gray, fontSize = 14.sp)
                            }

                            Text("Організатор: $creatorName", fontSize = 13.sp, color = Color.DarkGray)

                            Spacer(Modifier.height(8.dp))

                            Text(
                                "Призовий фонд: ${event.Prize} ₴",
                                color = Color(0xFF388E3C),
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Учасників: ${event.CountPeople}")

                                if (event.Status == 1) {
                                    Text("Завершено", color = Color.Gray, fontWeight = FontWeight.Bold)
                                } else {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                eventDao.completeEvent(event.EventID)
                                                statisticsDao.addMonthlyExpenses(
                                                    getCurrentMonthDbString(),
                                                    event.Prize ?: 0
                                                )
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8484)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text("Завершити", fontSize = 12.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddEventDialog(
            games = games,
            customers = customers,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(
    games: List<GamesEntity>,
    customers: List<CustomerEntity>,
    onDismiss: () -> Unit,
    onSave: (EventsEntity) -> Unit
) {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    var selectedGame by remember { mutableStateOf<GamesEntity?>(null) }
    var selectedCustomer by remember { mutableStateOf<CustomerEntity?>(null) }
    var prize by remember { mutableStateOf("") }
    var people by remember { mutableStateOf("10") }
    var eventDate by remember { mutableStateOf(today) }

    var gamesExpanded by remember { mutableStateOf(false) }
    var customersExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новий івент", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                ExposedDropdownMenuBox(
                    expanded = gamesExpanded,
                    onExpandedChange = { gamesExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedGame?.GameName ?: "Оберіть гру",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Гра") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(gamesExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = gamesExpanded,
                        onDismissRequest = { gamesExpanded = false }
                    ) {
                        games.forEach { game ->
                            DropdownMenuItem(
                                text = { Text(game.GameName) },
                                onClick = {
                                    selectedGame = game
                                    gamesExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = customersExpanded,
                    onExpandedChange = { customersExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCustomer?.FullName ?: "Оберіть організатора",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Організатор") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(customersExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = customersExpanded,
                        onDismissRequest = { customersExpanded = false }
                    ) {
                        customers.forEach { person ->
                            DropdownMenuItem(
                                text = { Text(person.FullName) },
                                onClick = {
                                    selectedCustomer = person
                                    customersExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = eventDate,
                    onValueChange = { eventDate = it },
                    label = { Text("Дата початку (РРРР-ММ-ДД)") },
                    placeholder = { Text("2024-05-20") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = prize,
                    onValueChange = { prize = it },
                    label = { Text("Призовий фонд (₴)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = people,
                    onValueChange = { people = it },
                    label = { Text("Кількість людей") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                enabled = selectedGame != null && selectedCustomer != null && eventDate.isNotBlank(),
                onClick = {
                    onSave(EventsEntity(
                        GameID = selectedGame?.GameID,
                        EventDate = eventDate,
                        Prize = prize.toIntOrNull() ?: 0,
                        CountPeople = people.toIntOrNull() ?: 0,
                        CustomerID = selectedCustomer?.CustomerID,
                        Status = 0
                    ))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Створити")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Скасувати") }
        }
    )
}