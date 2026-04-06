package com.example.pcclubkt.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pcclubkt.database.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PcGridScreen(computerDao: ComputerDao, customerDao: CustomerDao, visitLogDao: VisitLogDao) {
    val computerList by computerDao.getComputersWithDetails().collectAsState(initial = emptyList())
    val allCustomers by customerDao.getAllCustomers().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(computerList) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        while (true) {
            val now = Date()
            computerList.forEach { details ->
                val pc = details.computer
                if (pc.Status == "occupied") {
                    val activeLog = visitLogDao.getActiveLogForComputer(pc.ComputerID ?: 0)
                    activeLog?.let { log ->
                        try {
                            val endTime = sdf.parse(log.EndTime)
                            if (endTime != null && now.after(endTime)) {
                                computerDao.freePc(pc.ComputerID ?: 0)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            kotlinx.coroutines.delay(30000)
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var filterStatus by remember { mutableStateOf("all") }

    val occupiedClientIds = computerList
        .filter { it.computer.Status == "occupied" }
        .mapNotNull { it.computer.CurrentClientID }
        .toSet()

    val availableCustomers = allCustomers.filter { it.CustomerID !in occupiedClientIds }

    val filteredList = computerList.filter { item ->
        val pc = item.computer
        val matchesSearch = pc.ComputerID?.toString()?.contains(searchQuery) ?: true
        val matchesFilter = when (filterStatus) {
            "available" -> pc.Status == "available"
            "occupied" -> pc.Status == "occupied"
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F3F3))
            .padding(horizontal = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Керування ПК",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Пошук ПК за номером") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterButton("Всі", filterStatus == "all") { filterStatus = "all" }
            FilterButton("Вільні", filterStatus == "available") { filterStatus = "available" }
            FilterButton("Зайняті", filterStatus == "occupied") { filterStatus = "occupied" }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredList) { item ->
                ComputerCard(
                    details = item,
                    availableCustomers = availableCustomers,
                    onFreePc = { pcId ->
                        coroutineScope.launch {
                            val pricePerMin = 10
                            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            val now = Date()

                            val activeLog = visitLogDao.getActiveLogForComputer(pcId)

                            activeLog?.let { log ->
                                try {
                                    val startTime = sdf.parse(log.StartTime) ?: now
                                    val plannedEndTime = sdf.parse(log.EndTime) ?: now

                                    val totalPlannedMinutes =
                                        ((plannedEndTime.time - startTime.time) / (1000 * 60)).toInt()
                                    val actualMinutesUsed =
                                        ((now.time - startTime.time) / (1000 * 60)).toInt()
                                            .coerceAtLeast(0)

                                    if (actualMinutesUsed < totalPlannedMinutes) {
                                        val minutesToRefund =
                                            totalPlannedMinutes - actualMinutesUsed
                                        val refundAmount = minutesToRefund * pricePerMin
                                        customerDao.addBalance(log.CustomerID, refundAmount)
                                    }

                                    visitLogDao.updateLog(log.copy(EndTime = sdf.format(now)))

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            computerDao.freePc(pcId)
                        }
                    },
                    onAssignPcWithTime = { pcId, clientId, minutes ->
                        coroutineScope.launch {
                            val pricePerMin = 10
                            val totalCost = minutes * pricePerMin

                            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            val calendar = Calendar.getInstance()

                            val startTime = sdf.format(calendar.time)
                            calendar.add(Calendar.MINUTE, minutes)
                            val endTime = sdf.format(calendar.time)

                            customerDao.subtractBalance(clientId, totalCost)
                            computerDao.assignClientToPc(pcId, clientId)

                            visitLogDao.insertLog(
                                VisitLogEntity(
                                    CustomerID = clientId,
                                    ComputerID = pcId,
                                    StartTime = startTime,
                                    EndTime = endTime
                                )
                            )
                            customerDao.updateLastVisit(clientId, startTime)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFFFF8484) else Color(0xFFE0E0E0),
            contentColor = Color.Black
        ),
        modifier = Modifier.height(36.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        shape = RoundedCornerShape(50)
    ) {
        Text(text, fontSize = 12.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComputerCard(
    details: ComputerWithDetails,
    availableCustomers: List<CustomerEntity>,
    onFreePc: (Int) -> Unit,
    onAssignPcWithTime: (Int, Int, Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedClient by remember { mutableStateOf<CustomerEntity?>(null) }
    var minutesInput by remember { mutableStateOf("") }

    val pricePerMin = 10

    val pc = details.computer
    val specs = details.specs
    val currentClient = details.client

    val isOccupied = pc.Status == "occupied"
    val statusColor = if (isOccupied) Color(0xFFFF4B4B) else Color(0xFF4CAF50)

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("ПК №${pc.ComputerID}", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Характеристики:", fontWeight = FontWeight.Bold)
                    Text("CPU: ${specs.Cpu ?: "N/A"} | GPU: ${specs.Gpu ?: "N/A"}")
                    Text("RAM: ${specs.RAM ?: "N/A"}")

                    Spacer(Modifier.height(16.dp))

                    if (isOccupied && currentClient != null) {
                        Text("Користувач:", fontWeight = FontWeight.Bold)
                        Text("Грає: ${currentClient.FullName}")
                        Text("Баланс: ${currentClient.Balance} ₴", color = Color(0xFF388E3C))
                    } else {
                        Text("Вибір клієнта:", fontWeight = FontWeight.Bold)

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedClient?.FullName ?: "Оберіть клієнта",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                modifier = Modifier
                                    .menuAnchor(
                                        MenuAnchorType.PrimaryNotEditable,
                                        true
                                    )
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                availableCustomers.forEach { customer ->
                                    DropdownMenuItem(
                                        text = { Text("${customer.FullName} (${customer.Balance} ₴)") },
                                        onClick = {
                                            selectedClient = customer
                                            val maxPossible = customer.Balance / pricePerMin
                                            minutesInput =
                                                if (maxPossible >= 60) "60" else maxPossible.toString()
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        selectedClient?.let { client ->
                            val maxPossibleMinutes = (client.Balance / pricePerMin)
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Час гри (Макс: $maxPossibleMinutes хв)",
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = minutesInput,
                                onValueChange = { newValue ->
                                    if (newValue.all { it.isDigit() }) {
                                        val enteredNum = newValue.toIntOrNull() ?: 0
                                        if (enteredNum <= maxPossibleMinutes) {
                                            minutesInput = newValue
                                        }
                                    }
                                },
                                label = { Text("Хвилини") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            val cost = (minutesInput.toIntOrNull() ?: 0) * pricePerMin
                            Text(
                                "Буде знято: $cost ₴",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = isOccupied || (selectedClient != null && (minutesInput.toIntOrNull()
                        ?: 0) > 0),
                    onClick = {
                        pc.ComputerID?.let { id ->
                            if (isOccupied) {
                                onFreePc(id)
                            } else {
                                val mins = minutesInput.toIntOrNull() ?: 0
                                selectedClient?.CustomerID?.let { clientId ->
                                    onAssignPcWithTime(id, clientId, mins)
                                }
                            }
                        }
                        showDialog = false
                        selectedClient = null
                        minutesInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isOccupied) Color.Red else Color.Black)
                ) {
                    Text(if (isOccupied) "Звільнити ПК" else "Посадити за ПК")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Закрити") }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(statusColor)
            )
            Spacer(Modifier.height(12.dp))
            Icon(Icons.Default.Computer, null, modifier = Modifier.size(32.dp))
            Text("ПК №${pc.ComputerID}", fontWeight = FontWeight.Bold)
            Text(
                text = if (isOccupied) (currentClient?.FullName?.split(" ")?.firstOrNull()
                    ?: "Зайнято") else "Вільно",
                fontSize = 12.sp,
                color = statusColor
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}