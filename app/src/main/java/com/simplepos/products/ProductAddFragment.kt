package com.simplepos.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.simplepos.R
import com.simplepos.base.BaseFragment
import com.simplepos.data.Product
import kotlinx.android.synthetic.main.fragment_product_add.view.*
import org.koin.android.ext.android.inject


class ProductAddFragment : BaseFragment() {
    private val productAddViewModel:ProductAddViewModel by inject()
    private val scanBarcodeViewModel:ScanBarcodeViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_product_add, container, false)
        setAllObservers()
        setAllListeners()
        setInitialView()
        return rootView

    }

    override fun setAllListeners() {
        rootView.add_product_btn_scan_barcode.setOnClickListener(){btnScanBarcodeClick()}
        rootView.add_product_btn_save.setOnClickListener(){btnSaveClick()}
    }

    override fun setAllObservers() {
        productAddViewModel.code.observe(viewLifecycleOwner, Observer {
            rootView.add_product_code.text = it
        })
    }

    override fun setInitialView() {

    }



    private fun btnScanBarcodeClick(){
        scanBarcodeViewModel.setMode("add")
        findNavController().navigate(R.id.action_productAddFragment_to_scanBarcodeFragment)
    }

    private fun btnSaveClick(){
        if(
            rootView.add_product_code.text.isNotEmpty() &&
            rootView.add_product_number_price_buy.text.isNotEmpty() &&
            rootView.add_product_number_price_sell.text.isNotEmpty() &&
            rootView.add_product_number_stock.text.isNotEmpty()
        ){
            val product = Product(
                0,
                rootView.add_product_code.text.toString(),
                rootView.add_product_name.text.toString(),
                rootView.add_product_number_price_buy.text.toString().toInt(),
                rootView.add_product_number_price_sell.text.toString().toInt(),
                rootView.add_product_number_stock.text.toString().toInt()
            )
            productAddViewModel.saveProduct(product)
            productAddViewModel.reset()
            findNavController().navigate(R.id.action_productAddFragment_to_navigation_product)
        }


    }

}
