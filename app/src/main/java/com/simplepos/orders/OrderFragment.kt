package com.simplepos.orders


import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.simplepos.R
import com.simplepos.base.BaseFragment
import com.simplepos.util.toDefaultCurrencyFormat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_pay_option.view.*
import kotlinx.android.synthetic.main.fragment_order.view.*
import org.koin.android.ext.android.inject
import java.lang.NumberFormatException
import kotlin.math.abs

class OrderFragment : BaseFragment(), OrderAdapter.OnClickListener {

    private val orderViewModel: OrderViewModel by inject()
    private lateinit var adapter : OrderAdapter
    private lateinit var orderToolbar:androidx.appcompat.widget.Toolbar
    private lateinit var bottomSheet: ConstraintLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_order, container, false)
        setAllObservers()
        setInitialData()
        setAllAdapters()
        setAllListeners()
        setInitialView()
        return rootView
    }

    fun setInitialData(){
        orderViewModel.getOrders()
    }

    private fun setAllAdapters(){
        activity?.let {fragmentActivity->
            adapter = OrderAdapter(fragmentActivity)
            adapter.setClickListener(this)
            rootView.orderList.adapter = adapter
            rootView.orderList.layoutManager = LinearLayoutManager(fragmentActivity)
        }
    }


    override fun setAllObservers(){
        orderViewModel.orders.observe(viewLifecycleOwner, Observer {
            adapter.setItems(it)
            if(it.isNotEmpty()) {
                setViewOnHaveOrders()
            }else{
                setViewOnOrdersEmpty()
            }

        })
        orderViewModel.selectedOrder.observe(viewLifecycleOwner, Observer {
            if(null===it){
                setViewOnSelectedOrderNull()
                adapter.setSelectedId(0L)
            }else{
                setViewOnOrderSelected()
                adapter.setSelectedId(it.order.orderId)
            }

        })
        orderViewModel.totalPrice.observe(viewLifecycleOwner, Observer {
            rootView.pay_text_total.text = it.toDefaultCurrencyFormat()
            rootView.text_total.text = if(it>0)"Harga Total : "+it.toDefaultCurrencyFormat()else ""

        })
        orderViewModel.payAmount.observe(viewLifecycleOwner, Observer {
            rootView.pay_text_pay.text = it.toDefaultCurrencyFormat()
            if(it<1){
                rootView.pay_number_free.text.clear()
            }
        })
        orderViewModel.changeAmount.observe(viewLifecycleOwner, Observer {
            rootView.pay_text_change.text =abs(it).toDefaultCurrencyFormat()
            if(it>=0){
                rootView.pay_text_change.setTextColor(Color.parseColor("#028E09"))
                rootView.pay_text_change_x.setTextColor(Color.parseColor("#028E09"))
                rootView.pay_text_change_x.text = "Kembalian"
                setViewSudahBayar()

            }else{
                rootView.pay_text_change.setTextColor(Color.RED)
                rootView.pay_text_change_x.setTextColor(Color.RED)
                rootView.pay_text_change_x.text = "Kurang bayar"
                setViewBelumBayar()
            }
        })

    }

    override fun setInitialView() {

        setViewOnSelectedOrderNull()

    }

    override fun setAllListeners(){
        rootView.btn_scan_product.setOnClickListener { btnScanClick() }
        rootView.btn_search_product.setOnClickListener { btnSearchClick() }
        rootView.order_btn_pay_option.setOnClickListener { toggleBottomSheet() }
        rootView.pay_image_colapse.setOnClickListener { toggleBottomSheet() }
        rootView.pay_btn_100.setOnClickListener { orderViewModel.addPayAmount(100000) }
        rootView.pay_btn_50.setOnClickListener { orderViewModel.addPayAmount(50000) }
        rootView.pay_btn_20.setOnClickListener { orderViewModel.addPayAmount(20000) }
        rootView.pay_btn_10.setOnClickListener { orderViewModel.addPayAmount(10000) }
        rootView.pay_btn_5.setOnClickListener { orderViewModel.addPayAmount(5000) }
        rootView.pay_btn_2.setOnClickListener { orderViewModel.addPayAmount(2000) }
        rootView.pay_btn_1.setOnClickListener { orderViewModel.addPayAmount(1000) }
        rootView.pay_btn_500.setOnClickListener { orderViewModel.addPayAmount(500) }
        rootView.pay_btn_erase.setOnClickListener { orderViewModel.erasePayAmount() }
        rootView.pay_btn_save.setOnClickListener {
            toggleBottomSheet()
            orderViewModel.saveSales()
        }



        rootView.pay_number_free.doOnTextChanged { text, start, count, after ->

            if(!text.isNullOrEmpty() && text.isNotBlank()){
                try{
                    orderViewModel.setPayAmount(Integer.parseInt(text.toString()))
                }catch (e:NumberFormatException){

                }
            }else{
                orderViewModel.setPayAmount(0)
            }



        }


        //orderViewModel.setPayAmount(Integer.parseInt(it))

        bottomSheet = rootView.findViewById(R.id.layout_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if(newState==BottomSheetBehavior.STATE_COLLAPSED) {
                    setViewOnBottomSheetOpen(false)
                }

            }

        })

        rootView.report_toolbar.apply {
            orderToolbar = this
            inflateMenu(R.menu.menu_order)
            setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {

                    R.id.order_item_save->{
                        orderViewModel.saveSales()
                        true
                    }
                    R.id.order_item_reset -> {
                        onMenuResetClick()
                        true
                    }
                    R.id.order_item_add->{
                        orderViewModel.addQuantity()
                        true
                    }
                    R.id.order_item_remove->{
                        orderViewModel.removeQuantity()
                        true
                    }
                    R.id.order_item_cancel->{
                        orderViewModel.resetSelectedOrder()
                        true
                    }
                    R.id.order_item_delete->{
                        orderViewModel.selectedOrder.value?.let {
                            orderViewModel.deleteOrder(it.order)
                        }
                        true
                    }
                    else -> super.onOptionsItemSelected(menuItem)
                }
            }
        }
    }

    private fun setViewBelumBayar(){
        rootView.pay_btn_save.visibility=View.GONE
    }

    private fun setViewSudahBayar(){
        rootView.pay_btn_save.visibility=View.VISIBLE
    }

    private fun btnScanClick(){
        findNavController().navigate(R.id.action_navigation_order_to_scanProductFragment)
    }

    private fun btnSearchClick(){
        findNavController().navigate(R.id.action_navigation_order_to_searchProductFragment)
    }

    private fun toggleBottomSheet(){
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            setViewOnBottomSheetOpen(true)
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            setViewOnBottomSheetOpen(false)
        }


    }

    override fun onResume() {
        super.onResume()
        orderViewModel.getOrders()
    }

    override fun onClickItem(v: View, position: Int) {
        orderViewModel.orders.value?.get(position)?.let { orderViewModel.setSelectedOrder(it) }
    }

    private fun onMenuResetClick(){
        orderViewModel.resetOrders()
    }

    private fun setViewOnSelectedOrderNull(){
        orderToolbar.menu.apply {
            findItem(R.id.order_item_reset).apply {
                isVisible = true
            }
            findItem(R.id.order_item_remove).apply {
                isVisible = false
            }
            findItem(R.id.order_item_add).apply {
                isVisible = false
            }
            findItem(R.id.order_item_delete).apply {
                isVisible = false
            }
            findItem(R.id.order_item_cancel).apply {
                isVisible = false
            }
        }


    }

    private fun setViewOnOrderSelected(){
        orderToolbar.menu.apply {
            findItem(R.id.order_item_reset).apply {
                isVisible = false
            }
            findItem(R.id.order_item_remove).apply {
                isVisible = true
            }
            findItem(R.id.order_item_add).apply {
                isVisible = true
            }
            findItem(R.id.order_item_delete).apply {
                isVisible = true
            }
            findItem(R.id.order_item_cancel).apply {
                isVisible = true
            }
        }

    }

    private fun setViewOnOrdersEmpty(){
        orderToolbar.menu.apply {
            findItem(R.id.order_item_reset).apply {
                isVisible = false
            }
            findItem(R.id.order_item_save).apply {
                isVisible = false
            }
        }
        rootView.order_btn_pay_option.visibility = View.GONE
        rootView.order_text_empty_cart.visibility = View.VISIBLE
        rootView.text_total.visibility = View.GONE
        rootView.empty_cart_animation.visibility = View.VISIBLE
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        setViewOnBottomSheetOpen(false)

    }

    private fun setViewOnHaveOrders(){
        orderToolbar.menu.apply {
            findItem(R.id.order_item_reset).apply {
                isVisible = true
            }
            findItem(R.id.order_item_save).apply {
                isVisible = true
            }
        }
        rootView.order_btn_pay_option.visibility = View.VISIBLE
        rootView.empty_cart_animation.visibility = View.GONE
        rootView.order_text_empty_cart.visibility = View.GONE
        rootView.text_total.visibility = View.VISIBLE
    }

    private fun setViewOnBottomSheetOpen(isOpen:Boolean){
        rootView.btn_scan_product.visibility = if(isOpen===true)View.GONE else View.VISIBLE
        rootView.btn_search_product.visibility = if(isOpen===true)View.GONE else View.VISIBLE
        rootView.text_total.visibility = if(isOpen===true)View.GONE else View.VISIBLE
        rootView.orderList.visibility = if(isOpen===true)View.GONE else View.VISIBLE
    }




}
