package com.example.pcclubkt.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pcclubkt.database.CustomerDao
import com.example.pcclubkt.database.CustomerEntity
import com.example.pcclubkt.database.VisitLogDao
import com.example.pcclubkt.database.VisitLogEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.example.pcclubkt.database.StatisticsDao

@Composable
fun ClientsScreen(customerDao: CustomerDao, visitLogDao: VisitLogDao, statisticsDao: StatisticsDao) {
    val customers by customerDao.getAllCustomers().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var showRegDialog by remember { mutableStateOf(false) }
    var selectedCustomer by remember { mutableStateOf<CustomerEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F3F3))
            .padding(horizontal = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Клієнти", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Пошук за ПІБ", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.LightGray
            ),
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { showRegDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD35400)),
            shape = RoundedCornerShape(50),
            modifier = Modifier.height(40.dp).align(Alignment.End)
        ) {
            Text("Додати клієнта", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(customers.filter { it.FullName.contains(searchQuery, ignoreCase = true) }) { customer ->
                CustomerMiniboxItem(customer) { selectedCustomer = customer }
            }
        }
    }

    if (showRegDialog) {
        RegistrationDialog(
            onDismiss = { showRegDialog = false },
            onSave = { newCustomer ->
                coroutineScope.launch {
                    customerDao.registerCustomer(newCustomer)

                    newCustomer.Sex?.let { statisticsDao.incrementGenderVisits(it) }

                    val ageGroup = calculateAgeGroup(newCustomer.HappyBirthday)
                    statisticsDao.incrementAgeGroupVisits(ageGroup)

                    showRegDialog = false
                }
            }
        )
    }

    selectedCustomer?.let { customer ->
        val liveCustomer = customers.find { it.CustomerID == customer.CustomerID } ?: customer

        InfoDialog(
            customer = liveCustomer,
            visitLogDao = visitLogDao,
            onDismiss = { selectedCustomer = null },
            onAddBalance = { amount ->
                coroutineScope.launch {
                    liveCustomer.CustomerID?.let { id ->
                        customerDao.addBalance(id, amount)
                        val currentMonth = getCurrentMonthDbString()
                        statisticsDao.addMonthlyEarnings(currentMonth, amount)
                    }
                }
            },
            onUpdateCustomer = { updated ->
                coroutineScope.launch {
                    customerDao.updateCustomer(updated)
                }
            }
        )
    }
}

@Composable
fun CustomerMiniboxItem(customer: CustomerEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = customer.FullName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Телефон", color = Color.Gray, fontSize = 14.sp)
                Text(customer.PhoneNumber, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Поточний баланс", color = Color.Gray, fontSize = 14.sp)
                Text(
                    text = "${customer.Balance} ₴",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF388E3C)
                )
            }
        }
    }
}

@Composable
fun InfoDialog(
    customer: CustomerEntity,
    visitLogDao: VisitLogDao,
    onDismiss: () -> Unit,
    onAddBalance: (Int) -> Unit,
    onUpdateCustomer: (CustomerEntity) -> Unit
) {
    var showTopUp by remember { mutableStateOf(false) }
    var showEdit by remember { mutableStateOf(false) }
    val logs by visitLogDao.getLogsForCustomer(customer.CustomerID ?: 0)
        .collectAsState(initial = emptyList())

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF3F4F6)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Профіль клієнта", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { showEdit = true }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Редагувати",
                                tint = Color(0xFF1976D2)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Поточний баланс", color = Color.Gray, fontSize = 14.sp)
                            Text(
                                "${customer.Balance} ₴",
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF2E7D32)
                            )

                            Button(
                                onClick = { showTopUp = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .padding(top = 16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFFF8484
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "Поповнити рахунок",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            "Особисті дані",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        ProfileField("ПІБ", customer.FullName)
                        ProfileField("Телефон", customer.PhoneNumber)
                        ProfileField("Email", customer.Email)
                        ProfileField("Останній візит", customer.LastVisit)
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        "Історія сесій",
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(8.dp))

                    if (logs.isEmpty()) {
                        Text(
                            "Історія порожня",
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    } else {
                        logs.forEach { log ->
                            VisitLogItem(log)
                        }
                    }
                }
            }
        }
    }

    if (showTopUp) {
        var amountText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showTopUp = false },
            title = { Text("Введіть суму") },
            text = {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { if (it.all { c -> c.isDigit() }) amountText = it },
                    placeholder = { Text("Наприклад: 100") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    amountText.toIntOrNull()?.let { onAddBalance(it) }
                    showTopUp = false
                }) { Text("Оплатити") }
            }
        )
    }

    if (showEdit) {
        EditCustomerDialog(
            customer = customer,
            onDismiss = { showEdit = false },
            onSave = { updatedCustomer ->
                onUpdateCustomer(updatedCustomer)
                showEdit = false
            }
        )
    }
}

@Composable
fun VisitLogItem(log: VisitLogEntity) {
    val pricePerMin = 10
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    var costText = "—"
    var minutesPlayed = 0

    try {
        val start = sdf.parse(log.StartTime)
        val end = sdf.parse(log.EndTime)
        if (start != null && end != null) {
            minutesPlayed = ((end.time - start.time) / (1000 * 60)).toInt().coerceAtLeast(0)
            costText = "-${minutesPlayed * pricePerMin} ₴"
        }
    } catch (e: Exception) {
        costText = "В процесі..."
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF3F4F6), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("${log.ComputerID}", fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("ПК №${log.ComputerID}", fontWeight = FontWeight.Bold)
                Text("${log.StartTime} — ${log.EndTime}", fontSize = 11.sp, color = Color.Gray)
                if (minutesPlayed > 0) {
                    Text("Тривалість: $minutesPlayed хв", fontSize = 11.sp, color = Color.DarkGray)
                }
            }

            Text(
                text = costText,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Column(
        modifier = Modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth()
    ) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Text(if (value.isBlank()) "—" else value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun RegistrationDialog(onDismiss: () -> Unit, onSave: (CustomerEntity) -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("2000-01-01") }
    var sex by remember { mutableStateOf("male") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новий клієнт") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("ПІБ") })
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Телефон") })
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") })
                OutlinedTextField(
                    value = birthday,
                    onValueChange = { birthday = it },
                    label = { Text("Дата народження") })

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    RadioButton(selected = sex == "male", onClick = { sex = "male" })
                    Text("Чол")
                    Spacer(Modifier.width(10.dp))
                    RadioButton(selected = sex == "female", onClick = { sex = "female" })
                    Text("Жін")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                onSave(
                    CustomerEntity(
                        FullName = fullName,
                        Email = email,
                        PhoneNumber = phone,
                        HappyBirthday = birthday,
                        MembershipStatus = 1,
                        Balance = 0,
                        Sex = sex,
                        Registration = now,
                        LastVisit = now
                    )
                )
            }) { Text("Зареєструвати") }
        }
    )
}

@Composable
fun EditCustomerDialog(
    customer: CustomerEntity,
    onDismiss: () -> Unit,
    onSave: (CustomerEntity) -> Unit
) {
    var fullName by remember { mutableStateOf(customer.FullName) }
    var email by remember { mutableStateOf(customer.Email) }
    var phone by remember { mutableStateOf(customer.PhoneNumber) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редагувати клієнта") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("ПІБ") })
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Телефон") })
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    customer.copy(
                        FullName = fullName,
                        PhoneNumber = phone,
                        Email = email
                    )
                )
            }) { Text("Зберегти") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Скасувати") }
        }
    )
}