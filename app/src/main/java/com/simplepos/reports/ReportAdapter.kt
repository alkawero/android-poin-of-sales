package com.simplepos.reports

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simplepos.R
import com.simplepos.data.Sales
import com.simplepos.util.toDefaultCurrencyFormat
import kotlinx.android.synthetic.main.list_item_report.view.*

class ReportAdapter (private val context: Context) : RecyclerView.Adapter<ReportAdapter.ProductViewHolder>() {
    private lateinit var clickListener: OnClickListener
    private var items = ArrayList<Sales>()
    private var selectedId = 0L

    fun setSelectedId(id:Long){
        selectedId = id
        notifyDataSetChanged()
    }

    fun setClickListener(listener: OnClickListener){
        clickListener = listener
    }

    fun setItems(orderGroupByProduct : List<Sales>){
        items = orderGroupByProduct as ArrayList<Sales>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_item_report, parent, false),
            clickListener
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {

        val sales = items[position]

        holder.textProductName.text = sales.productName
        holder.textProductQuantity.text = sales.quantity.toString()+" pcs"
        holder.textSubtotal.text = sales.subTotalSell.toDefaultCurrencyFormat()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ProductViewHolder(view : View, onClickListener: OnClickListener) : RecyclerView.ViewHolder(view){
        val textProductName: TextView = view.search_text_product_name
        val textProductQuantity: TextView = view.report_item_text_quantity
        val textSubtotal:TextView = view.report_item_sub_total
        init{
            view.setOnClickListener(){
                onClickListener.onClickItem(view,adapterPosition)
            }
        }

    }

    interface OnClickListener{
        fun onClickItem(v: View, position: Int)
    }


}