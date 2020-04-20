package com.simplepos.orders


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplepos.data.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList


class OrderViewModel(private val orderRepository: OrderRepository, private val salesRepository: SalesRepository, private val productRepository: ProductRepository) : ViewModel() {

    private val _orders = MutableLiveData<List<OrderWithProduct>>()
    private val _selectedOrder = MutableLiveData<OrderWithProduct>()
    private val _totalPrice = MutableLiveData<Int>().apply { value = 0 }
    private val _payAmount = MutableLiveData<Int>().apply { value = 0 }
    private val _changeAmount = MutableLiveData<Int>().apply { value = 0 }

    val totalPrice : LiveData<Int> = _totalPrice
    val payAmount : LiveData<Int> = _payAmount
    val changeAmount :LiveData<Int> = _changeAmount
    val orders: LiveData<List<OrderWithProduct>> = _orders
    val selectedOrder: LiveData<OrderWithProduct> = _selectedOrder

    fun addPayAmount(amount:Int){
        var payValue = _payAmount.value!!.plus(amount)
        _payAmount.postValue(payValue)
        _changeAmount.postValue(payValue.minus(_totalPrice.value!!))
    }

    fun erasePayAmount(){
        _payAmount.postValue(0)
        _changeAmount.postValue(-_totalPrice.value!!)
    }

    fun setPayAmount(payValue:Int){
        _payAmount.postValue(payValue)
        _changeAmount.postValue(payValue.minus(_totalPrice.value!!))
    }

    fun setSelectedOrder(orderWithProduct: OrderWithProduct){
        if(null === _selectedOrder.value){
            _selectedOrder.postValue(orderWithProduct)
        }else{
            if(orderWithProduct.order == _selectedOrder.value!!.order)
                _selectedOrder.postValue(null)
            else
                _selectedOrder.postValue(orderWithProduct)
        }

    }

    fun resetSelectedOrder(){
        _selectedOrder.postValue(null)
    }

    fun getOrders() = viewModelScope.launch {
        orderRepository.getOrders().let {newOrders->
            _orders.postValue(newOrders)
            if(newOrders.isNotEmpty()){
                val newTotalPrice = newOrders.map { it.product.price_sell * it.order.quantity}.sum()
                _totalPrice.postValue(newTotalPrice)
                _changeAmount.postValue(_payAmount.value?.minus(newTotalPrice))
            }else{
                _totalPrice.postValue(0)
                _payAmount.postValue(0)
                _changeAmount.postValue(0)
            }
        }
    }

    fun saveOrder(order: Order) = viewModelScope.launch{
        runBlocking {
            orderRepository.deleteOrderByProductId(order.orderedProductId)
            orderRepository.saveOrder(order)
            getOrders()
        }
    }

    fun saveSales() = viewModelScope.launch{
        val newSales = _orders.value?.map {
            Sales(0,
                it.product.productId,
                it.product.name,
                it.product.price_buy,
                it.product.price_sell,
                it.order.quantity,
                it.product.price_buy*it.order.quantity,
                it.product.price_sell*it.order.quantity,
                Date()
            ) }
        newSales?.let { sales->
            salesRepository.saveSales(sales)
            _orders.value?.forEach {
                val productId = it.product.productId
                val newStock = if(it.product.stock - it.order.quantity < 0) 0 else it.product.stock - it.order.quantity
                productRepository.updateProductStock(productId,newStock)
            }
            resetOrders()
            _orders.postValue(ArrayList<OrderWithProduct>())
        }
    }


    fun deleteOrder(order: Order) = viewModelScope.launch {
        runBlocking {
            orderRepository.deleteOrder(order)
            getOrders()
            _selectedOrder.postValue(null)
        }
    }

    fun resetOrders() = viewModelScope.launch {
        runBlocking {
            orderRepository.resetOrders()
            getOrders()
            _payAmount.postValue(0)
            _changeAmount.postValue(0)
            _selectedOrder.postValue(null)
        }
    }

    fun addQuantity() = viewModelScope.launch {
        runBlocking {
            _selectedOrder.value?.apply {
                order.quantity = order.quantity+1
                orderRepository.updateOrder(order)
            }

            getOrders()
        }
    }



    fun removeQuantity()= viewModelScope.launch {
        runBlocking {
            _selectedOrder.value?.apply {
                if(order.quantity>1) {
                    order.quantity = order.quantity-1
                    orderRepository.updateOrder(order)
                    getOrders()
                }
            }


        }
    }


}