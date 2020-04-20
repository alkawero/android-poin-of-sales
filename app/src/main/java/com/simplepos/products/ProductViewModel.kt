package com.simplepos.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplepos.data.Product
import com.simplepos.data.ProductRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ProductViewModel(private val productRepo: ProductRepository) : ViewModel() {

    private var _selectedProduct : MutableLiveData<Product> = MutableLiveData<Product>()
    private var _products = MutableLiveData<List<Product>>()

    val products:LiveData<List<Product>> = _products
    val selectedProduct: LiveData<Product> = _selectedProduct

    fun getProducts() = viewModelScope.launch {
        _products.postValue(productRepo.getAllProducts())
    }

    fun getProductsByName(name:String) = viewModelScope.launch {
        _products.postValue(productRepo.getProductByName(name.toLowerCase()))
    }

    fun setSelectedProduct(product: Product){
        if(product == _selectedProduct.value){
            _selectedProduct.postValue(null)
        }else{
            _selectedProduct.postValue(product)
        }

    }

    fun getProductByStock(minStock:Int, maxStock:Int) = viewModelScope.launch {
        _products.postValue(productRepo.getProductByStock(minStock,maxStock))
    }

    fun deleteProduct(product: Product) = viewModelScope.launch {
        runBlocking {
            productRepo.deleteProduct(product)
            _products.postValue(productRepo.getAllProducts())
            _selectedProduct.postValue(null)
        }
    }

    fun resetSelectedProduct(){
        _selectedProduct.postValue(null)
    }

}