package com.example.pcclubkt.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pcclubkt.database.StatisticsDao
import java.util.Calendar

@Composable
fun StatisticsScreen(statisticsDao: StatisticsDao) {
    val backgroundColor = Color(0xFFF3F3F3)
    val scrollState = rememberScrollState()

    val genderData by statisticsDao.getGenderdistribution().collectAsState(initial = emptyList())
    val ageData by statisticsDao.getAgedistribution().collectAsState(initial = emptyList())
    val earningsData by statisticsDao.getMonthlyearnings().collectAsState(initial = emptyList())
    val expensesData by statisticsDao.getMonthlyexpenses().collectAsState(initial = emptyList())

    val allMonths = listOf("січ", "лют", "бер", "квіт", "трав", "черв", "лип", "серп", "вер", "жовт", "лист", "груд")
    val currentMonthIdx = Calendar.getInstance().get(Calendar.MONTH)
    val activeMonths = allMonths.take(currentMonthIdx + 1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 32.dp), contentAlignment = Alignment.Center) {
            Text(text = "Статистика", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
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

        // Графік прибутків
        if (earningsData.isNotEmpty()) {
            BarChartCard(
                title = "Прибутки (₴)",
                data = earningsData.filter { it.Month in activeMonths }.map { (it.Month ?: "") to it.Earnings.toFloat() },
                barColor = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (expensesData.isNotEmpty()) {
            BarChartCard(
                title = "Витрати (₴)",
                data = expensesData.filter { it.Month in activeMonths }.map { (it.Month ?: "") to it.Expenses.toFloat() },
                barColor = Color(0xFFF44336)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (ageData.isNotEmpty()) {
            BarChartCard(
                title = "Вік відвідувачів",
                data = ageData.map { (it.Agegroup ?: "") to it.Visits.toFloat() },
                barColor = Color(0xFF9C27B0)
            )
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun PieChartCard(title: String, values: List<Float>, colors: List<Color>, labels: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    val total = values.sum()

    var animationPlayed by remember { mutableStateOf(false) }
    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 360f else 0f,
        animationSpec = tween(durationMillis = 1000), label = ""
    )
    LaunchedEffect(true) { animationPlayed = true }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Box(modifier = Modifier.size(160.dp).padding(16.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    var startAngle = -90f
                    values.forEachIndexed { index, value ->
                        val sweepAngle = (value / total) * animateRotation
                        drawArc(
                            color = colors[index],
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 40f, cap = StrokeCap.Round)
                        )
                        startAngle += sweepAngle
                    }
                }
                Text(text = total.toInt().toString(), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                labels.forEachIndexed { index, label ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).background(colors[index], CircleShape))
                        Text(" $label", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(12.dp))
                    labels.forEachIndexed { index, label ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, color = Color.DarkGray)
                            Text("${values[index].toInt()} візитів", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BarChartCard(title: String, data: List<Pair<String, Float>>, barColor: Color) {
    var expanded by remember { mutableStateOf(false) }
    val maxVal = data.maxOfOrNull { it.second } ?: 1f

    var animationPlayed by remember { mutableStateOf(false) }
    val heightFraction by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000), label = ""
    )
    LaunchedEffect(true) { animationPlayed = true }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth().height(if (expanded) 160.dp else 120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEach { (label, value) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        if (expanded) {
                            Text(
                                text = value.toInt().toString(),
                                fontSize = 10.sp,
                                color = barColor,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Box(modifier = Modifier
                            .fillMaxWidth(if (expanded) 0.6f else 0.4f)
                            .fillMaxHeight((value / maxVal) * heightFraction)
                            .background(barColor, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        )
                        Text(label.take(3), fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp))
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(12.dp))
                    data.forEach { (label, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, color = Color.DarkGray)
                            Text(value.toInt().toString(), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}