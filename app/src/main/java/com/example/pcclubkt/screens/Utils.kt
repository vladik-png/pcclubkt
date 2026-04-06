package com.example.pcclubkt.screens

import java.text.SimpleDateFormat
import java.util.*

fun calculateAgeGroup(birthdayString: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val birthDate = sdf.parse(birthdayString) ?: return "16-20 років"
        val today = Calendar.getInstance()
        val birthCal = Calendar.getInstance().apply { time = birthDate }
        var age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        when {
            age <= 15 -> "10-15 років"
            age in 16..20 -> "16-20 років"
            age in 21..30 -> "21-25 років"
            else -> "31 рік та старші"
        }
    } catch (e: Exception) {
        "16-20 років"
    }
}

fun getCurrentMonthDbString(): String {
    val months = arrayOf("січ", "лют", "бер", "квіт", "трав", "черв", "лип", "серп", "вер", "жовт", "лист", "груд")
    return months[Calendar.getInstance().get(Calendar.MONTH)]
}