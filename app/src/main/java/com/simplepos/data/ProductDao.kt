package com.simplepos.data

import androidx.room.*

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProduct(product:Product):Long

    @Query(value="select * from Product  ORDER BY name LIMIT 20")
    suspend fun getAllProduct():List<Product>

    @Query(value="select * from Product where code = :code")
    suspend fun getProductByCode(code:String):Product?

    @Query(value = "SELECT * from Product where lower(name) like '%'||:name ||'%' limit 10")
    suspend fun getProductByName(name:String):List<Product>

    @Query(value = "SELECT * from Product where stock>=:minStock and stock<=:maxStock ")
    suspend fun getProductByStock(minStock:Int, maxStock:Int):List<Product>

    @Delete
    suspend fun deleteProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Query("UPDATE product set stock = :newStock where productId=:productId")
    suspend fun updateProductStock(productId:Long, newStock:Int)

}