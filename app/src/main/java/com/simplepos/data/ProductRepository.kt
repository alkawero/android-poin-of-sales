package com.simplepos.data

class ProductRepository(private val productDao: ProductDao) {


    suspend fun getAllProducts():List<Product>{
        return productDao.getAllProduct()
    }

    suspend fun saveProduct(product: Product){
        productDao.saveProduct(product)
    }

    suspend fun getProductByCode(code : String):Product?{
        return productDao.getProductByCode(code)
    }

    suspend fun getProductByName(name:String):List<Product>?{
        return productDao.getProductByName(name)
    }

    suspend fun getProductByStock(minStock:Int, maxStock:Int):List<Product>?{
        return productDao.getProductByStock(minStock,maxStock)
    }

    suspend fun deleteProduct(product: Product){
        productDao.deleteProduct(product)
    }

    suspend fun updateProduct(product: Product){
        productDao.updateProduct(product)
    }

    suspend fun updateProductStock(productId:Long, newStock:Int){
        productDao.updateProductStock(productId,newStock)
    }

}