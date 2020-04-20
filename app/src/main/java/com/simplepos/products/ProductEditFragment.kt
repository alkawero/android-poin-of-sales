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
import kotlinx.android.synthetic.main.fragment_product_edit.view.*
import org.koin.android.ext.android.inject


class ProductEditFragment : BaseFragment() {

    private val productViewModel: ProductViewModel by inject()
    private val productEditViewModel: ProductEditViewModel by inject()
    private val scanBarcodeViewModel: ScanBarcodeViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView =  inflater.inflate(R.layout.fragment_product_edit, container, false)
        setAllListeners()
        setAllObservers()
        setInitialView()

        return rootView
    }

    override fun setAllListeners() {
        rootView.edit_product_btn_scan_barcode.setOnClickListener {
            scanBarcodeViewModel.setMode("edit")
            findNavController().navigate(R.id.action_productEditFragment_to_scanBarcodeFragment)
        }
        rootView.edit_product_btn_save.setOnClickListener(){
            btnSaveClick()
        }
    }

    override fun setAllObservers(){
        productEditViewModel.selectedProduct.observe(viewLifecycleOwner, Observer {
            it?.let {
                rootView.edit_product_code.text = it.code
                rootView.edit_product_name.setText(it.name)
                rootView.edit_product_number_price_buy.setText(it.price_buy.toString())
                rootView.edit_product_number_price_sell.setText(it.price_sell.toString())
                rootView.edit_product_number_stock.setText(it.stock.toString())
            }
        })
    }

    override fun setInitialView() {

    }

    private fun btnSaveClick(){
        productViewModel.resetSelectedProduct()

        productEditViewModel.selectedProduct.value?.let{
            val newProduct = Product(
                it.productId,
                rootView.edit_product_code.text.toString(),
                rootView.edit_product_name.text.toString(),
                rootView.edit_product_number_price_buy.text.toString().toInt(),
                rootView.edit_product_number_price_sell.text.toString().toInt(),
                rootView.edit_product_number_stock.text.toString().toInt())
            productEditViewModel.updateProduct(newProduct)
        }

        findNavController().navigate(R.id.action_productEditFragment_to_navigation_product)
    }
}
