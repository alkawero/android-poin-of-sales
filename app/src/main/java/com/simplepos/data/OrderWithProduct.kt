package com.simplepos.data

import androidx.room.Embedded
import androidx.room.Relation

data class OrderWithProduct(
    @Embedded val order: Order,
    @Relation(
        parentColumn = "orderedProductId",
        entityColumn = "productId"
        )
    val product: Product) {
}