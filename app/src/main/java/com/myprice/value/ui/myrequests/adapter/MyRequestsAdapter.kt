package com.myprice.value.ui.myrequests.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myprice.value.R
import com.myprice.value.ui.myrequests.model.ProductBean
import kotlinx.android.synthetic.main.myrequests_row.view.*

/**
Created by  on 16-Oct-19.
 */
class MyRequestsAdapter constructor(context: Context) :
    RecyclerView.Adapter<MyRequestsAdapter.ViewHolder>() {
    private var list: List<ProductBean> = emptyList()

    companion object {
        private var sClickListener: UpdateDataClickListener? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.myrequests_row,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = this.list[position]
        holder.bindView(product)
    }

    internal fun setOnItemClickListener(clickListener: UpdateDataClickListener) {
        sClickListener = clickListener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun bindView(product: ProductBean) {
            itemView.myreqs_product_name.text = product.name
            itemView.myreqs_product_price.text = product.requestedPrice
            itemView.myreqs_product_quantity.text = product.quantity
            itemView.myreqs_product_location.text = product.location
            itemView.ic_delete.setOnClickListener {
                itemView.swipeLayout.close(true)
                sClickListener?.onDeleteClick(adapterPosition, product.id)
            }
        }
    }

    fun setData(produclist: ArrayList<ProductBean>) {
        this.list = produclist
        notifyDataSetChanged()
    }

    internal interface UpdateDataClickListener {
        fun onDeleteClick(position: Int, id: String?)
    }
}