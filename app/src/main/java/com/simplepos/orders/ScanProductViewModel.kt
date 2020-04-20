package com.simplepos.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplepos.data.ProductRepository
import com.simplepos.data.Product
import kotlinx.coroutines.launch

class ScanProductViewModel(private val productRepo: ProductRepository) : ViewModel() {
    private var _scannedProduct : MutableLiveData<Product> = MutableLiveData<Product>()
    private var _productQuantity = MutableLiveData<Int>().apply { value=1 }
    private var _productNotFound = MutableLiveData<Boolean>().apply { value=false }

    val scannedProduct: LiveData<Product> = _scannedProduct
    val productQuantity: LiveData<Int> = _productQuantity
    val productNotFound:LiveData<Boolean> = _productNotFound

    fun addOrder(){
        _productQuantity.value = _productQuantity.value?.plus(1)
    }

    fun removeOrder(){
        if(productQuantity.value!! > 1){
            _productQuantity.value = _productQuantity.value?.minus(1)
        }
    }

    fun setScannedProductByCode(code:String) = viewModelScope.launch {
        productRepo.getProductByCode(code)?.let {
            _scannedProduct.postValue(it)
            _productNotFound.postValue(false)
        } ?: _productNotFound.postValue(true)
    }


}