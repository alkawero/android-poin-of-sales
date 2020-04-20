package com.simplepos.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplepos.data.Product
import com.simplepos.data.ProductRepository
import kotlinx.coroutines.launch

class ProductEditViewModel(private val productRepo: ProductRepository) : ViewModel() {

    private var _selectedProduct : MutableLiveData<Product> = MutableLiveData<Product>()


    val selectedProduct: LiveData<Product> = _selectedProduct


    fun setSelectedProduct(product: Product){
        _selectedProduct.postValue(product)
    }

    fun updateProduct(p:Product)=viewModelScope.launch{
        productRepo.updateProduct(p)
    }

    fun setBarcode(newCode:String){
        _selectedProduct.value?.apply {
                code = newCode
            _selectedProduct.postValue(this)
        }
    }


}