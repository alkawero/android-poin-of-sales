package com.simplepos.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplepos.data.Product
import com.simplepos.data.ProductRepository
import kotlinx.coroutines.launch

class SearchProductViewModel (private val productRepo: ProductRepository) : ViewModel() {

    private var _selectedProduct : MutableLiveData<Product> = MutableLiveData<Product>()
    private var _productQuantity = MutableLiveData<Int>().apply { value=1 }
    private var _products = MutableLiveData<List<Product>>()

    val products:LiveData<List<Product>> = _products
    val selectedProduct: LiveData<Product> = _selectedProduct
    val productQuantity: LiveData<Int> = _productQuantity

    fun getProducts() = viewModelScope.launch {
        _products.postValue(productRepo.getAllProducts())
    }

    fun getProductsByName(name:String) = viewModelScope.launch {
        _products.postValue(productRepo.getProductByName(name.toLowerCase()))
    }

    fun addProductQuantity(){
        _productQuantity.value = _productQuantity.value?.plus(1)
    }

    fun removeProductQuantity(){
        if(productQuantity.value!! > 1){
            _productQuantity.value = _productQuantity.value?.minus(1)
        }
    }

    fun setSelectedProduct(product: Product){
        _selectedProduct.postValue(product)
    }

    fun resetSelectedProduct(){
        _selectedProduct.postValue(null)
    }


}
