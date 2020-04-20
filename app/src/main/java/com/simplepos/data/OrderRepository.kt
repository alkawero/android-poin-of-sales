package com.simplepos.data


class OrderRepository(private val orderDao: OrderDao) {

    suspend fun saveOrder(order: Order):Long{
        return orderDao.saveOrder(order)
    }

    suspend fun getOrders():List<OrderWithProduct>{
        return orderDao.getOrders()
    }

    suspend fun deleteOrder(order: Order){
        orderDao.deleteOrder(order)
    }

    suspend fun deleteOrderByProductId(productId:Long){
        orderDao.deleteOrderByProductId(productId)
    }


    suspend fun resetOrders(){
        orderDao.resetOrders()
    }

    suspend fun updateOrder(order: Order){
        orderDao.updateOrder(order)
    }


}