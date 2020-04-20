package com.simplepos.data

import androidx.room.TypeConverter
import java.util.*

class Converter {
    @TypeConverter
    fun timeStampToDate(value:Long?):Date?{
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimeStamp(value:Date?):Long?{
        return value?.time?.toLong()
    }
}