package com.simplepos.products

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.simplepos.R
import com.simplepos.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_product.view.*
import kotlinx.android.synthetic.main.my_search_view.view.*
import org.koin.android.ext.android.inject


class ProductFragment : BaseFragment(), ProductAdapter.OnClickListener {

    private val productViewModel: ProductViewModel by inject()
    private val productEditViewModel: ProductEditViewModel by inject()
    private lateinit var adapter : ProductAdapter
    private lateinit var productToolbar:androidx.appcompat.widget.Toolbar
    private lateinit var deleteAlert : AlertDialog

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_product, container, false)

        setInitialData()
        setAllAdapters()
        setAllObservers()
        setAllListeners()
        setInitialView()
        return rootView
    }

    fun setInitialData(){
        productViewModel.getProducts()
    }

    private fun setAllAdapters(){
        activity?.let {
            adapter = ProductAdapter(it)
            adapter.setClickListener(this)
            rootView.list_product.adapter = adapter
            rootView.list_product.layoutManager = LinearLayoutManager(it)
        }
    }

    private fun btnAddProductClick(){
        findNavController().navigate(R.id.action_navigation_product_to_productAddFragment)
    }


    override fun onClickItem(v: View, position: Int) {
        productViewModel.products.value?.let {
            val clickedProduct = it[position]
            productViewModel.setSelectedProduct(clickedProduct)
        }
    }

    private fun setViewOnSelectedProductNull(){
        productToolbar.menu.apply {
            findItem(R.id.product_item_search).apply {
                isVisible = true
            }
            findItem(R.id.product_item_empty_stock).apply {
                isVisible = true
            }
            findItem(R.id.product_item_few_stock).apply {
                isVisible = true
            }
            findItem(R.id.product_item_many_stock).apply {
                isVisible = true
            }
            findItem(R.id.product_item_edit).apply {
                isVisible = false
            }
            findItem(R.id.product_item_delete).apply {
                isVisible = false
            }
            findItem(R.id.product_item_cancel).apply {
                isVisible = false
            }
        }
    }

    private fun setViewOnProductSelected(){
        productToolbar.menu.apply {
            findItem(R.id.product_item_search).apply {
                isVisible = false
            }
            findItem(R.id.product_item_empty_stock).apply {
                isVisible = false
            }
            findItem(R.id.product_item_few_stock).apply {
                isVisible = false
            }
            findItem(R.id.product_item_many_stock).apply {
                isVisible = false
            }
            findItem(R.id.product_item_edit).apply {
                isVisible = true
            }
            findItem(R.id.product_item_delete).apply {
                isVisible = true
            }
            findItem(R.id.product_item_cancel).apply {
                isVisible = true
            }
        }

    }

    override fun setAllListeners() {
        activity?.let {
        val dialogBuilder= AlertDialog.Builder(it)
            dialogBuilder.setTitle("Konfirmasi")
            dialogBuilder.setMessage("Apakah anda ingin menghapus data?")

            dialogBuilder.setPositiveButton("Hapus",
                DialogInterface.OnClickListener { dialog, which ->
                    productViewModel.selectedProduct.value?.let {
                        productViewModel.deleteProduct(it)
                    }
                })

            dialogBuilder.setNegativeButton("Tidak",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })

            deleteAlert = dialogBuilder.create()
        }




        rootView.toolbar_product.apply {
            productToolbar = this
            inflateMenu(R.menu.menu_product)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.product_item_empty_stock -> {
                        productViewModel.getProductByStock(0, 0)
                        true
                    }
                    R.id.product_item_few_stock -> {
                        productViewModel.getProductByStock(1, 10)
                        true
                    }
                    R.id.product_item_many_stock -> {
                        productViewModel.getProductByStock(11, 1000)
                        true
                    }
                    R.id.product_item_cancel -> {
                        productViewModel.resetSelectedProduct()
                        true
                    }
                    R.id.product_item_delete -> {
                        deleteAlert.show()
                        true
                    }
                    R.id.product_item_edit -> {
                        productViewModel.selectedProduct.value?.let {
                            productEditViewModel.setSelectedProduct(it)
                            findNavController().navigate(R.id.action_navigation_product_to_productEditFragment)
                        }
                        true
                    }
                    R.id.product_item_search -> {
                        it.isVisible = false
                        rootView.findViewById<ConstraintLayout>(R.id.my_search_toolbar).visibility = View.VISIBLE
                        rootView.findViewById<EditText>(R.id.search_view_text).search_view_text.requestFocus()
                        activity?.let {
                            val imm =
                                it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
                        }
                            true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
            rootView.findViewById<EditText>(R.id.search_view_text).search_view_text.doOnTextChanged { text, start, count, after ->
                productViewModel.getProductsByName(text.toString())
            }
            rootView.findViewById<Button>(R.id.search_view_button_close).setOnClickListener {
                rootView.findViewById<ConstraintLayout>(R.id.my_search_toolbar).visibility = View.GONE
                rootView.findViewById<EditText>(R.id.search_view_text).search_view_text.setText("")
                productToolbar.menu.findItem(R.id.product_item_search).isVisible=true
            }
            rootView.btn_add_product.setOnClickListener { btnAddProductClick() }

        }
    }
    override fun setAllObservers() {
        productViewModel.products.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                setViewOnHaveProducts()
            }else{
                setViewOnEmptyProduct()
            }
            adapter.setItems(it)
        })
        productViewModel.selectedProduct.observe(viewLifecycleOwner, Observer {
            if(null===it){
                setViewOnSelectedProductNull()
                adapter.setSelectedId(0L)
            }else{
                setViewOnProductSelected()
                adapter.setSelectedId(it.productId)
            }

        })
    }

    override fun setInitialView() {
        setViewOnSelectedProductNull()
    }

    private fun setViewOnEmptyProduct(){
        rootView.empty_Product_animation.visibility = View.VISIBLE
    }

    private fun setViewOnHaveProducts(){
        rootView.empty_Product_animation.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        productViewModel.getProducts()
    }

}
