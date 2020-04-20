package com.simplepos.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters

@Database(entities = [Product::class,Order::class,Sales::class],version=10)
@TypeConverters(Converter::class)
abstract class PosDatabase : RoomDatabase() {
    abstract val productDao:ProductDao
    abstract val orderDao:OrderDao
    abstract val salesDao:SalesDao
}
