package com.simplepos.data

import java.util.*

class SalesRepository(private val salesDao: SalesDao) {

    suspend fun getSalesBetweenDates(startDate: Date, untilDate: Date):List<Sales>{
        return salesDao.getSalesBetweenDates(startDate,untilDate)
    }

    suspend fun saveSales(sales: List<Sales>){
        salesDao.saveSales((sales))
    }

    suspend fun clearSales(){
        salesDao.clearSales()
    }

}