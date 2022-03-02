package com.example.libraryapplication.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateFormatter {
    @RequiresApi(Build.VERSION_CODES.O)
    fun dateFormatter(date: LocalDate):String{
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return formatter.format(date).toString()
    }

    fun dayToYear(date: String):String{
        val year=date.substringAfterLast('/')
        val month=date.substringBeforeLast('/').substringAfterLast('/')
        val day=date.substringBeforeLast('/').substringBeforeLast('/')
        return "$year-$month-$day"
    }
}