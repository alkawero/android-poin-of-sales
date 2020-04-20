package com.simplepos.orders

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.simplepos.R
import com.simplepos.base.BaseFragment
import com.simplepos.data.Order
import com.simplepos.data.Product
import com.simplepos.util.toDefaultCurrencyFormat
import com.google.zxing.Result
import kotlinx.android.synthetic.main.fragment_scan_product.view.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class ScanProductFragment : BaseFragment(), ZXingScannerView.ResultHandler {

    private val orderViewModel: OrderViewModel by inject()
    private val scanProductViewModel : ScanProductViewModel by viewModel()
    private lateinit var scannerView: ZXingScannerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_scan_product, container, false)
        setAllObservers()
        setAllListeners()
        setInitialView()
        return rootView
    }



    override fun setAllListeners() {
        rootView.btn_add_cart.setOnClickListener { btnAddCartClick() }
        rootView.btn_reset_scanner.setOnClickListener { btnResetScannerClick() }
        rootView.search_btn_remove.setOnClickListener { btnRemoveClick() }
        rootView.search_btn_add.setOnClickListener { btnAddClick() }
    }

    override fun setAllObservers() {
        scanProductViewModel.scannedProduct.observe(viewLifecycleOwner, Observer {
            setViewOnProductFound(it)

        })
        scanProductViewModel.productQuantity.observe(viewLifecycleOwner, Observer {
            rootView.text_product_count.text = "Beli : "+it.toString()
        })
        scanProductViewModel.productNotFound.observe(viewLifecycleOwner, Observer {
            if(it)setViewOnProductNotFound()
        })
    }

    override fun setInitialView() {
        initScannerView()
        setViewOnProductNull()
    }

    private fun initScannerView(){
        scannerView = ZXingScannerView(activity)
        scannerView.setResultHandler(this)
        rootView.frame_scan.addView(scannerView)
    }

    override fun handleResult(rawResult: Result?) {
        rawResult?.let {
            scanProductViewModel.setScannedProductByCode(it.text)
        }
    }

    private fun setViewOnProductFound(product: Product){
        rootView.product_image.visibility = View.VISIBLE;
        rootView.text_product_price.visibility = View.VISIBLE;
        rootView.search_text_product_name.visibility = View.VISIBLE;
        rootView.btn_reset_scanner.visibility = View.VISIBLE;
        rootView.btn_add_cart.visibility = View.VISIBLE;
        rootView.frame_scan.visibility = View.GONE;
        rootView.search_btn_remove.visibility = View.VISIBLE
        rootView.search_btn_add.visibility = View.VISIBLE
        rootView.text_product_count.visibility = View.VISIBLE
        rootView.search_text_product_name.text = product.name
        rootView.text_product_price.text = "Rp. "+product.price_sell.toDefaultCurrencyFormat()
        rootView.text_result_status.visibility=View.GONE
    }

    override fun onResume() {
        super.onResume()
        doRequestPermission()
        scannerView.startCamera()
    }


    private fun doRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity?.let {
                    PermissionChecker.checkSelfPermission(
                        it,
                        Manifest.permission.CAMERA
                    )
                } != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            100 -> {

            }
            else -> {
                /* nothing to do in here */
            }
        }
    }



    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    private fun btnAddCartClick(){
        scanProductViewModel.scannedProduct.value?.let {
            val order = Order(0,it.productId, scanProductViewModel.productQuantity.value!!, Date())
            orderViewModel.saveOrder(order)
        }
        findNavController().navigate(R.id.action_scanProductFragment_to_navigation_order)
    }

    private fun btnResetScannerClick(){
        setViewOnProductNull()
        scannerView.resumeCameraPreview(this)
    }

    private fun btnRemoveClick(){
        scanProductViewModel.removeOrder()
    }

    private fun btnAddClick(){
        scanProductViewModel.addOrder()
    }

    private fun setViewOnProductNotFound(){
        rootView.text_result_status.text="kode produk tidak ditemukan"
        rootView.text_result_status.visibility=View.VISIBLE
        rootView.btn_reset_scanner.visibility = View.VISIBLE
    }

    private fun setViewOnProductNull(){
        rootView.search_text_product_name.text=""
        rootView.text_product_price.text=""
        rootView.frame_scan.visibility = View.VISIBLE
        rootView.btn_add_cart.visibility = View.GONE
        rootView.btn_reset_scanner.visibility = View.GONE
        rootView.product_image.visibility = View.GONE
        rootView.text_product_price.visibility = View.GONE
        rootView.search_text_product_name.visibility = View.GONE
        rootView.search_btn_remove.visibility = View.GONE
        rootView.search_btn_add.visibility = View.GONE
        rootView.text_product_count.visibility = View.GONE
        rootView.text_result_status.visibility=View.GONE
    }



}
