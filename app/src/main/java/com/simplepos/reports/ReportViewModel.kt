package com.simplepos.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simplepos.data.Sales
import com.simplepos.data.SalesRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList

class ReportViewModel(val salesRepository: SalesRepository) : ViewModel() {

    private val _successOrders = MutableLiveData<List<Sales>>()
    private val _totalSell = MutableLiveData<Int>().apply { value=0 }
    private val _totalMargin = MutableLiveData<Int>().apply { value=0 }
    private val _periodeType = MutableLiveData<Int>().apply { value=0 }
    private val _orderDate = MutableLiveData<Date>().apply { value=Date() }
    private val _startOrderDate = MutableLiveData<Date>().apply { value=Date() }
    private val _untilOrderDate = MutableLiveData<Date>().apply { value=Date() }

    val dateFormats = arrayListOf<String>("d MMMM yyyy","MMMM yyyy","yyyy")
    val successOrders : LiveData<List<Sales>> = _successOrders
    val totalSell:LiveData<Int> = _totalSell
    val totalMargin:LiveData<Int> = _totalMargin
    val orderDate:LiveData<Date> = _orderDate
    val startOrderDate:LiveData<Date> = _startOrderDate
    val untilOrderDate:LiveData<Date> = _untilOrderDate
    val periodeType:LiveData<Int> = _periodeType

    fun setPeriodeType(index:Int){
        _periodeType.postValue(index)
        val c = Calendar.getInstance()
        when(index){
            0->{
                _orderDate.postValue(Date())
                _startOrderDate.postValue(Date())
                _untilOrderDate.postValue(Date())
            }
            1->{
                c.set(Calendar.DAY_OF_MONTH,1)
                _orderDate.postValue(c.time)
                _startOrderDate.postValue(c.time)
                c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
                _untilOrderDate.postValue(c.time)
            }
            2->{
                c.time = Date()
                c.set(Calendar.DAY_OF_YEAR, 1);
                _orderDate.postValue(c.time)
                _startOrderDate.postValue(c.time)
                c.set(Calendar.MONTH, 11);
                c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
                _untilOrderDate.postValue(c.time)
            }
        }
    }

    fun getSuccessOrders() = viewModelScope.launch {
        val startD = _startOrderDate.value
        val untilD = _untilOrderDate.value

        if(startD!==null  && untilD !== null){
            val cStart = Calendar.getInstance().apply {
                time = startD
                set(Calendar.HOUR_OF_DAY, 0);
            }
           val cUntil = Calendar.getInstance().apply {
               time = untilD
               set(Calendar.HOUR_OF_DAY, 23);
           }
            val newSuccessOrder = salesRepository.getSalesBetweenDates(cStart.time, cUntil.time)

            newSuccessOrder?.let{ sales ->
                _successOrders.postValue(sales)

                if(sales.isNotEmpty()){
                    val totalSell = sales.map{ it.subTotalSell }.reduce { acc, next -> acc + next}
                    val totalBuy = sales.map{ it.subTotalBuy }.reduce { acc, next -> acc + next}

                    _totalSell.postValue(
                        totalSell
                    )
                    _totalMargin.postValue(
                        totalSell - totalBuy
                    )
                }else{
                    _totalSell.postValue(0)
                    _totalMargin.postValue(0)
                }
            }

        }


        
        
    }

    fun clearSales() = viewModelScope.launch {
        runBlocking {
            salesRepository.clearSales()
            _successOrders.postValue(ArrayList<Sales>())
            _totalSell.postValue(0)
            _totalMargin.postValue(0)
        }
    }

    fun setToNextDate(){
        _orderDate.value.let {orderDate->
        val c = Calendar.getInstance().apply {time = orderDate}
        _periodeType.value.let {
            when(it){
                0 -> {
                    c.apply {
                        add(Calendar.DATE,1)
                        _orderDate.postValue(time)
                        _startOrderDate.postValue(time)
                        _untilOrderDate.postValue(time)
                    }
                }
                1 -> {
                    c.apply {
                        add(Calendar.MONTH,1)
                        set(Calendar.DAY_OF_MONTH,1)
                        _orderDate.postValue(time)
                        _startOrderDate.postValue(time)
                        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH));
                        _untilOrderDate.postValue(time)
                    }
                }
                2 -> {
                    c.apply {
                        add(Calendar.YEAR,1)
                        set(Calendar.DAY_OF_YEAR, 1);
                        _orderDate.postValue(time)
                        _startOrderDate.postValue(time)
                        set(Calendar.MONTH, 11);
                        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH));
                        _untilOrderDate.postValue(time)
                    }
                }
                else->{}
            }
        }

        }

    }


    fun setToPrevDate(){
        _orderDate.value.let {orderDate->
            val c = Calendar.getInstance().apply {time = orderDate}
            _periodeType.value.let {
                when(it){
                    0 -> {
                        c.apply {
                            add(Calendar.DATE,-1)
                            _orderDate.postValue(time)
                            _startOrderDate.postValue(time)
                            _untilOrderDate.postValue(time)
                        }
                    }
                    1 -> {
                        c.apply {
                            add(Calendar.MONTH,-1)
                            set(Calendar.DAY_OF_MONTH,1)
                            _orderDate.postValue(time)
                            _startOrderDate.postValue(time)
                            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH));
                            _untilOrderDate.postValue(time)
                        }
                    }
                    2 -> {
                        c.apply {
                            add(Calendar.YEAR,-1)
                            set(Calendar.DAY_OF_YEAR, 1);
                            _orderDate.postValue(time)
                            _startOrderDate.postValue(time)
                            set(Calendar.MONTH, 11);
                            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH));
                            _untilOrderDate.postValue(time)
                        }
                    }
                    else->{}
                }
            }

        }
    }

    fun setStartOrderDate(date : Date){
        _startOrderDate.postValue(date)
    }
    fun setUntilOrderDate(date : Date){
        _untilOrderDate.postValue(date)
    }





}
