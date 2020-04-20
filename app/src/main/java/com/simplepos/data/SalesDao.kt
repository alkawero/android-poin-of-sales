package com.simplepos.data

import androidx.room.*
import java.util.*

@Dao
interface SalesDao {
    @Query("SELECT salesId, productId, productName, productPriceBuy, productPriceSell, sum(quantity) as quantity, sum(subTotalBuy) as subTotalBuy, sum(subTotalSell) as subTotalSell, salesDate FROM Sales s where salesDate BETWEEN :startDate AND :untilDate GROUP BY productId ORDER BY quantity DESC")
    suspend fun getSalesBetweenDates(startDate: Date, untilDate: Date):List<Sales>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveSales(sales: List<Sales>)

    @Query("DELETE FROM Sales")
    suspend fun clearSales()
}