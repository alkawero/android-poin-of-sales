package com.simplepos.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Sales(
    @PrimaryKey(autoGenerate = true) val salesId:Long,
    val productId:Long,
    val productName:String,
    val productPriceBuy:Int,
    val productPriceSell:Int,
    val quantity:Int,
    val subTotalBuy:Int,
    val subTotalSell:Int,
    val salesDate: Date



) {
}