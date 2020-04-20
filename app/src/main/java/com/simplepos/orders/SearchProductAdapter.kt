package com.simplepos.orders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simplepos.R
import com.simplepos.data.Product
import com.simplepos.util.toDefaultCurrencyFormat
import kotlinx.android.synthetic.main.list_item_product.view.*

class SearchProductAdapter(private val context: Context):
    RecyclerView.Adapter<SearchProductAdapter.ProductViewHolder>() {

    private lateinit var clickListener: OnClickListener
    private var items = ArrayList<Product>()


    fun setClickListener(listener: OnClickListener) {
        clickListener = listener
    }

    fun setItems(newItems: List<Product>) {
        items = newItems as ArrayList<Product>
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_item_product,
                parent,
                false
            ), clickListener
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = items[position]
        holder.textProductName.text = product.name
        holder.textPrice.text = "Rp. " + product.price_sell.toDefaultCurrencyFormat()
        holder.textStock.text = product.stock.toString() + " pcs"
    }

    class ProductViewHolder(view: View, onClickListener: OnClickListener) :
        RecyclerView.ViewHolder(view) {
        val textProductName: TextView = view.item_text_product_name
        val textPrice: TextView = view.item_text_price
        val textStock: TextView = view.item_text_stock

        init {
            view.setOnClickListener() {
                onClickListener.onClickItem(view, adapterPosition)
            }
        }
    }

    interface OnClickListener {
        fun onClickItem(v: View, position: Int)
    }
}
