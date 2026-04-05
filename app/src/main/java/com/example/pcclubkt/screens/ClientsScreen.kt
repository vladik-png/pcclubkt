package com.example.pcclubkt.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pcclubkt.database.CustomerDao
import com.example.pcclubkt.database.CustomerEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClientsScreen(customerDao: CustomerDao) {
    val customers by customerDao.getAllCustomers().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var showRegDialog by remember { mutableStateOf(false) }
    var selectedCustomer by remember { mutableStateOf<CustomerEntity?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFD1D5DB))) {
        Text(
            text = "Клієнти",
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { showRegDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD35400)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Додати клієнта", color = Color.White)
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Пошук за ПІБ", fontSize = 12.sp) },
                modifier = Modifier.width(180.dp).height(54.dp),
                leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp)) },
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }

        Spacer(Modifier.height(16.dp))

        // Таблиця
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            items(customers.filter { it.FullName.contains(searchQuery, ignoreCase = true) }) { customer ->
                HorizontalCustomerItem(customer) { selectedCustomer = customer }
            }
        }
    }

    if (showRegDialog) {
        RegistrationDialog(
            onDismiss = { showRegDialog = false },
            onSave = { newCustomer ->
                coroutineScope.launch {
                    customerDao.registerCustomer(newCustomer)
                    showRegDialog = false
                }
            }
        )
    }

    selectedCustomer?.let { customer ->
        InfoDialog(customer = customer, onDismiss = { selectedCustomer = null })
    }
}

@Composable
fun HorizontalCustomerItem(customer: CustomerEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(2f)) {
                Text(customer.FullName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(customer.PhoneNumber, color = Color.Gray, fontSize = 12.sp)
            }
            Text("${customer.Balance} ₴", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
        }
    }
}

@Composable
fun RegistrationDialog(onDismiss: () -> Unit, onSave: (CustomerEntity) -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("2005-01-01") }
    var sex by remember { mutableStateOf("male") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новий клієнт") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("ПІБ") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Телефон") })
                OutlinedTextField(value = birthday, onValueChange = { birthday = it }, label = { Text("Дата народження (РРРР-ММ-ДД)") })

                Text("Стать:", modifier = Modifier.padding(top = 8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                onSave(CustomerEntity(
                    FullName = fullName,
                    Email = email,
                    PhoneNumber = phone,
                    HappyBirthday = birthday,
                    Sex = sex,
                    Registration = now,
                    LastVisit = now
                ))
            }) { Text("Зареєструвати") }
        }
    )
}

@Composable
fun InfoDialog(customer: CustomerEntity, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFD1D5DB)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Перегляд інформації", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
                }
                Spacer(Modifier.height(20.dp))
                Text("Клієнт: ${customer.FullName}", fontSize = 18.sp)
                Text("Баланс: ${customer.Balance} ₴", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Телефон: ${customer.PhoneNumber}")
                Text("Email: ${customer.Email}")
            }
        }
    }
}