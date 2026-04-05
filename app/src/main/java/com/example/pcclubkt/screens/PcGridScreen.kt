package com.example.pcclubkt.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pcclubkt.database.ComputerDao
import com.example.pcclubkt.database.ComputerWithDetails
import com.example.pcclubkt.database.CustomerDao
import com.example.pcclubkt.database.CustomerEntity
import kotlinx.coroutines.launch

@Composable
fun PcGridScreen(computerDao: ComputerDao, customerDao: CustomerDao) {
    val computerList by computerDao.getComputersWithDetails().collectAsState(initial = emptyList())
    val allCustomers by customerDao.getAllCustomers().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

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

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Пошук ПК за номером") },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(50)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterButton("Всі", filterStatus == "all") { filterStatus = "all" }
            FilterButton("Вільні", filterStatus == "available") { filterStatus = "available" }
            FilterButton("Зайняті", filterStatus == "occupied") { filterStatus = "occupied" }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize().padding(top = 16.dp)
        ) {
            items(filteredList) { item ->
                ComputerCard(
                    details = item,
                    availableCustomers = availableCustomers,
                    onFreePc = { pcId ->
                        coroutineScope.launch { computerDao.freePc(pcId) }
                    },
                    onAssignPc = { pcId, clientId ->
                        coroutineScope.launch { computerDao.assignClientToPc(pcId, clientId) }
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
    onAssignPc: (Int, Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedClient by remember { mutableStateOf<CustomerEntity?>(null) }

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
                    Text("⚙️ Характеристики:", fontWeight = FontWeight.Bold)
                    Text("CPU: ${specs.Cpu}")
                    Text("GPU: ${specs.Gpu}")
                    Text("RAM: ${specs.RAM}")

                    Spacer(Modifier.height(16.dp))

                    Text("👤 Користувач:", fontWeight = FontWeight.Bold)
                    if (isOccupied && currentClient != null) {
                        Text("Грає: ${currentClient.FullName}")
                        Text("Баланс: ${currentClient.Balance} грн", color = Color(0xFF388E3C))
                    } else {
                        if (availableCustomers.isEmpty()) {
                            Text("Усі клієнти вже зайняті або не зареєстровані.", color = Color.Red, fontSize = 14.sp)
                        } else {
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedClient?.FullName ?: "Оберіть клієнта",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    modifier = Modifier.menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    availableCustomers.forEach { customer ->
                                        DropdownMenuItem(
                                            text = { Text(customer.FullName) },
                                            onClick = {
                                                selectedClient = customer
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = isOccupied || selectedClient != null,
                    onClick = {
                        pc.ComputerID?.let { id ->
                            if (isOccupied) {
                                onFreePc(id)
                            } else {
                                selectedClient?.CustomerID?.let { clientId ->
                                    onAssignPc(id, clientId)
                                }
                            }
                        }
                        showDialog = false
                        selectedClient = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOccupied) Color.Red else Color.Black,
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    Text(if (isOccupied) "Звільнити ПК" else "Посадити за ПК", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Закрити") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { showDialog = true },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(statusColor))
            Spacer(Modifier.height(12.dp))
            Icon(Icons.Default.Computer, contentDescription = null, modifier = Modifier.size(32.dp))
            Text("ПК №${pc.ComputerID}", fontWeight = FontWeight.Bold)

            Text(
                text = if (isOccupied) (currentClient?.FullName?.split(" ")?.firstOrNull() ?: "Зайнято") else "Вільно",
                fontSize = 12.sp,
                color = statusColor
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}