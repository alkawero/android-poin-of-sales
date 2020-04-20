package com.simplepos.reports

import androidx.room.Embedded
import androidx.room.Relation
import com.simplepos.data.Order
import com.simplepos.data.Product

class OrderGroupByProduct(
    @Embedded val order: Order,
    @Relation(
        parentColumn = "orderedProductId",
        entityColumn = "productId"
    )
    val product: Product,
    val productQuantity:Int,
    var sumPrice: Int
) {
}