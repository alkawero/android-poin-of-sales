package com.simplepos.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScanBarcodeViewModel: ViewModel() {
    private var _code = MutableLiveData<String>().apply { value="" }
    private var _mode = MutableLiveData<String>().apply { value="add" }



    val code : LiveData<String> = _code
    val mode : LiveData<String> = _mode

    fun setCode(code:String){
        _code.value=code
    }
    fun setMode(mode:String){
        _mode.value=mode
    }

    fun reset(){
        _code.value=""
    }
}