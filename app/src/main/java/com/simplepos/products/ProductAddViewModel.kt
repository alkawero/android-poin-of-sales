package com.simplepos.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplepos.data.Product
import com.simplepos.data.ProductRepository
import kotlinx.coroutines.launch

class ProductAddViewModel (private val productRepo: ProductRepository): ViewModel() {
    private var _code = MutableLiveData<String>().apply { value="" }
    val code : LiveData<String> = _code

    fun setCode(newCode:String){
        _code.value = newCode
    }

    fun saveProduct(p: Product)=viewModelScope.launch{
        productRepo.saveProduct(p)
    }

    fun reset(){
        _code.value=""
    }


}