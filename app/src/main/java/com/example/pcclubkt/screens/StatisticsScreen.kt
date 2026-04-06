package com.example.pcclubkt.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pcclubkt.database.StatisticsDao

@Composable
fun StatisticsScreen(statisticsDao: StatisticsDao) {
    val backgroundColor = Color(0xFFF3F3F3)
    val scrollState = rememberScrollState()

    val genderData by statisticsDao.getGenderdistribution().collectAsState(initial = emptyList())
    val ageData by statisticsDao.getAgedistribution().collectAsState(initial = emptyList())
    val earningsData by statisticsDao.getMonthlyearnings().collectAsState(initial = emptyList())
    val expensesData by statisticsDao.getMonthlyexpenses().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Статистика", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        if (genderData.isNotEmpty()) {
            PieChartCard(
                title = "Розподіл за статтю",
                values = genderData.map { it.Visits.toFloat() },
                colors = listOf(Color(0xFF2196F3), Color(0xFFFF8484)),
                labels = genderData.map { if (it.Gender == "male") "Чол" else "Жін" }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (earningsData.isNotEmpty()) {
            BarChartCard(
                title = "Прибутки (₴)",
                data = earningsData.map { (it.Month ?: "") to it.Earnings.toFloat() },
                barColor = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (ageData.isNotEmpty()) {
            BarChartCard(
                title = "Вік відвідувачів",
                data = ageData.map { (it.Agegroup ?: "") to it.Visits.toFloat() },
                barColor = Color(0xFF9C27B0)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (expensesData.isNotEmpty()) {
            BarChartCard(
                title = "Витрати (₴)",
                data = expensesData.map { (it.Month ?: "") to it.Expenses.toFloat() },
                barColor = Color(0xFFF44336)
            )
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun PieChartCard(title: String, values: List<Float>, colors: List<Color>, labels: List<String>) {
    val total = values.sum()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.size(160.dp).padding(16.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    var startAngle = -90f
                    values.forEachIndexed { index, value ->
                        val sweepAngle = (value / total) * 360f
                        drawArc(color = colors[index], startAngle = startAngle, sweepAngle = sweepAngle, useCenter = true)
                        startAngle += sweepAngle
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                labels.forEachIndexed { index, label ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(colors[index], CircleShape))
                        Text(" $label", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun BarChartCard(title: String, data: List<Pair<String, Float>>, barColor: Color) {
    val maxVal = data.maxOfOrNull { it.second } ?: 1f
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth().height(120.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
                data.forEach { (label, value) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Box(modifier = Modifier.fillMaxWidth(0.4f).fillMaxHeight(value / maxVal).background(barColor, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)))
                        Text(label.take(3), fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}