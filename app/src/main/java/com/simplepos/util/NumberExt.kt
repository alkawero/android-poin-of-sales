package com.simplepos.util

import java.text.NumberFormat
import java.util.*

fun Int.toDefaultCurrencyFormat():String{
    val formatter = NumberFormat.getInstance(Locale.US)
    return formatter.format(this)
}