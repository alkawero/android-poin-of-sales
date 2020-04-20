package com.simplepos.orders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simplepos.R
import com.simplepos.data.OrderWithProduct
import kotlinx.android.synthetic.main.list_item_product_in_cart.view.*

class OrderAdapter(private val context:Context) : RecyclerView.Adapter<OrderAdapter.ProductViewHolder>() {
    private lateinit var clickListener: OnClickListener
    private var items = ArrayList<OrderWithProduct>()
    private var selectedId = 0L

    fun setSelectedId(id:Long){
        selectedId = id
        notifyDataSetChanged()
    }

    fun setClickListener(listener: OnClickListener){
        clickListener = listener
    }

    fun setItems(orderWithProducts : List<OrderWithProduct>){
        items = orderWithProducts as ArrayList<OrderWithProduct>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_item_product_in_cart, parent, false),
            clickListener
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val orderWithProduct = items[position]
        if(orderWithProduct.order.orderId===selectedId){
            holder.itemView.setBackgroundResource(R.drawable.selected_product_shape)
        }else{
            holder.itemView.setBackgroundResource(0)
        }

        holder.textProductName.text = orderWithProduct.product.name
        holder.textProductQuantity.text = orderWithProduct.order.quantity.toString()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ProductViewHolder(view : View,onClickListener: OnClickListener) : RecyclerView.ViewHolder(view){
        val textProductName: TextView = view.text_item_product_name
        val textProductQuantity: TextView = view.report_item_text_quantity
        init{
            view.setOnClickListener(){
                onClickListener.onClickItem(view,adapterPosition)
            }
        }

    }

    interface OnClickListener{
        fun onClickItem(v:View, position: Int)
    }


}