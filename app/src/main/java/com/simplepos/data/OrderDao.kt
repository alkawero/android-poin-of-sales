package com.simplepos.data

import androidx.room.*

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOrder(order:Order):Long

    @Query("SELECT * FROM `Order` ")
    suspend fun getOrders():List<OrderWithProduct>

    @Delete
    suspend fun deleteOrder(order:Order)

    @Query("DELETE FROM `Order` where orderedProductId = :productId")
    suspend fun deleteOrderByProductId(productId:Long)

    @Query("DELETE FROM `Order`")
    suspend fun resetOrders()

    @Update
    suspend fun updateOrder(order: Order)
}