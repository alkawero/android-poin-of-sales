package com.simplepos.data

import androidx.room.*
import java.util.*

@Entity
data class Order(
    @PrimaryKey(autoGenerate = true) val orderId:Long,
    val orderedProductId:Long,
    var quantity:Int,
    val createdDate: Date?
    ) {
}