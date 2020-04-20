package com.simplepos.products

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.simplepos.R
import com.simplepos.base.BaseFragment
import com.google.zxing.Result
import kotlinx.android.synthetic.main.fragment_scan_barcode.view.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.android.ext.android.inject

class ScanBarcodeFragment : BaseFragment() , ZXingScannerView.ResultHandler {

    private val scanBarcodeViewModel : ScanBarcodeViewModel by inject()
    private val productEditViewModel : ProductEditViewModel by inject()
    private val productAddViewModel : ProductAddViewModel by inject()
    private lateinit var scannerView: ZXingScannerView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_scan_barcode, container, false)
        setAllObservers()
        setAllListeners()
        setInitialView()

        return rootView
    }

    private fun initScannerView(){
        scannerView = ZXingScannerView(activity)
        scannerView.setResultHandler(this)
        rootView.frame_scan.addView(scannerView)
    }

    override fun handleResult(rawResult: Result?) {
        rawResult?.let {
            scanBarcodeViewModel.setCode(it.text)
        }
    }

    override fun setAllListeners() {
        rootView.btn_use_code.setOnClickListener(){btnUseCodeClick()}
        rootView.btn_rescan.setOnClickListener(){btnResetScannerClick()}
    }

    override fun setAllObservers() {
        scanBarcodeViewModel.code.observe(viewLifecycleOwner, Observer {
            rootView.text_code_result.text = it
        })
    }

    override fun setInitialView() {
        initScannerView()
    }

    override fun onResume() {
        super.onResume()
        doRequestPermission()
        scannerView.startCamera()
        scanBarcodeViewModel.reset()
    }


    private fun doRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity?.let { checkSelfPermission(it,Manifest.permission.CAMERA) } != PackageManager.PERMISSION_GRANTED) {
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

    private fun btnUseCodeClick(){
        scanBarcodeViewModel.code.value?.let{
            when(scanBarcodeViewModel.mode.value){
                "add"->{
                    productAddViewModel.setCode(it)
                    findNavController().navigate(R.id.action_scanBarcodeFragment_to_productAddFragment)
                }
                "edit"->{
                    productEditViewModel.setBarcode(it)
                    findNavController().navigate(R.id.action_scanBarcodeFragment_to_productEditFragment)
                }
                else->{}
            }

        }
    }

    private fun btnResetScannerClick(){
        scanBarcodeViewModel.reset()
        scannerView.resumeCameraPreview(this)
    }


}
