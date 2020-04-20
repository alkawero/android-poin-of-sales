package com.simplepos.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.simplepos.R
import com.simplepos.base.BaseFragment
import com.simplepos.data.Order
import com.simplepos.data.Product
import com.simplepos.util.toDefaultCurrencyFormat
import kotlinx.android.synthetic.main.fragment_search_product.view.*
import kotlinx.android.synthetic.main.fragment_search_product.view.btn_add_cart
import kotlinx.android.synthetic.main.fragment_search_product.view.search_btn_add
import kotlinx.android.synthetic.main.fragment_search_product.view.search_btn_remove
import kotlinx.android.synthetic.main.fragment_search_product.view.text_product_count
import kotlinx.android.synthetic.main.fragment_search_product.view.search_text_product_name
import kotlinx.android.synthetic.main.fragment_search_product.view.text_product_price
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class SearchProductFragment : BaseFragment(),SearchProductAdapter.OnClickListener {

    private val searchProductViewModel:SearchProductViewModel by viewModel()
    private val orderViewModel:OrderViewModel by inject()
    private lateinit var adapter : SearchProductAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_search_product, container, false)
        setInitialData()
        setAllAdapters()
        setAllObservers()
        setAllListeners()
        setInitialView()
        return rootView
    }

    fun setInitialData(){
        searchProductViewModel.getProducts()
    }

    private fun setAllAdapters(){
        activity?.let {
            adapter = SearchProductAdapter(it)
            adapter.setClickListener(this)
            rootView.search_list_product.adapter = adapter
            rootView.search_list_product.layoutManager = LinearLayoutManager(it)
        }
    }


    override fun setAllListeners() {
        rootView.btn_add_cart.setOnClickListener(){ btnAddCartClick() }
        rootView.search_btn_add.setOnClickListener(){btnAddClick()}
        rootView.search_btn_remove.setOnClickListener(){btnRemoveClick()}
        rootView.search_btn_reset.setOnClickListener(){btnResetSelectedProduct()}
        rootView.searchView.setOnQueryTextListener(object: OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {
                text?.let {
                    searchProductViewModel.getProductsByName(text)
                }
                return true

            }

        })
    }

    override fun setAllObservers() {
        searchProductViewModel.products.observe(viewLifecycleOwner, Observer {
            adapter.setItems(it)
        })
        searchProductViewModel.productQuantity.observe(viewLifecycleOwner, Observer {
            rootView.text_product_count.text = "Beli : "+it.toString()
        })
        searchProductViewModel.selectedProduct.observe(viewLifecycleOwner, Observer {
            it?.let {
                setViewOnProductFound(it)
            } ?: setViewOnProductNull()
        })
    }


    private fun btnAddClick(){
        searchProductViewModel.addProductQuantity()
    }

    private fun btnRemoveClick(){
        searchProductViewModel.removeProductQuantity()
    }

    private fun btnAddCartClick(){
        searchProductViewModel.selectedProduct.value?.let {
            val order = Order(0,it.productId, searchProductViewModel.productQuantity.value!!, Date())
            orderViewModel.saveOrder(order)
        }
        findNavController().navigate(R.id.action_searchProductFragment_to_navigation_order)
    }

    private fun btnResetSelectedProduct(){
        searchProductViewModel.resetSelectedProduct()
    }

    override fun setInitialView(){
        setViewOnProductNull()
    }

    private fun setViewOnProductFound(product: Product){
        rootView.search_btn_reset.visibility=View.VISIBLE
        rootView.search_list_product.visibility = View.GONE
        rootView.searchView.visibility = View.GONE
        rootView.text_product_price.visibility = View.VISIBLE;
        rootView.search_text_product_name.visibility = View.VISIBLE;
        rootView.btn_add_cart.visibility = View.VISIBLE;
        rootView.search_btn_remove.visibility = View.VISIBLE
        rootView.search_btn_add.visibility = View.VISIBLE
        rootView.text_product_count.visibility = View.VISIBLE
        rootView.search_text_product_name.text = product.name
        rootView.text_product_price.text = "Rp. "+product.price_sell.toDefaultCurrencyFormat()
    }

    private fun setViewOnProductNull(){
        rootView.search_btn_reset.visibility=View.GONE
        rootView.search_list_product.visibility = View.VISIBLE
        rootView.searchView.visibility = View.VISIBLE
        rootView.text_product_price.visibility = View.GONE;
        rootView.search_text_product_name.visibility = View.GONE;
        rootView.btn_add_cart.visibility = View.GONE;
        rootView.search_btn_remove.visibility = View.GONE
        rootView.search_btn_add.visibility = View.GONE
        rootView.text_product_count.visibility = View.GONE
    }

    override fun onClickItem(v: View, position: Int) {
        searchProductViewModel.products.value?.let {
            val clickedProduct = it[position]
            searchProductViewModel.setSelectedProduct(clickedProduct)
        }

    }

}
