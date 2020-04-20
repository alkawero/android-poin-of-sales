package com.simplepos.util

import java.text.SimpleDateFormat
import java.util.*

fun Date.toFormatString(format:String):String{
    val formatter = SimpleDateFormat(format)
    return formatter.format(this)
}