package com.simplepos.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
data class Product(@PrimaryKey(autoGenerate = true) val productId:Long,
    var code:String,
    val name:String,
    val price_buy:Int,
                   val price_sell:Int,
    val stock:Int
)
