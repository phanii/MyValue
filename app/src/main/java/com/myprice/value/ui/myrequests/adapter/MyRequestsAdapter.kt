package com.myprice.value.ui.myrequests.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.myprice.value.R
import com.myprice.value.ui.myrequests.model.ProductBean
import com.myprice.value.utils.toCurrency
import kotlinx.android.synthetic.main.list_row.view.*
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
        Log.d("vAlues ", "adapter called2")
        holder.bindView(product)
    }

    internal fun setOnItemClickListener(clickListener: UpdateDataClickListener) {
        sClickListener = clickListener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(product: ProductBean) {
            itemView.myreqs_product_name.text = product.name
            itemView.myreqs_product_price.text = if (product.requestedPrice.equals(
                    "MarketValuePrice",
                    true
                )
            ) "MarketValuePrice" else product.requestedPrice?.toDouble()?.toCurrency
            itemView.myreqs_product_quantity.text = "Qty ${product.quantity}"
            itemView.myreqs_product_location.text = product.location

            if (product.requestStatus != null) {
                itemView.btn_accept.text = if (!product.requestStatus!!)
                    itemView.context.getString(R.string.accept)
                else
                    itemView.context.getString(R.string.reject)
            }


            itemView.ic_delete.setOnClickListener {
                itemView.swipeLayout.close(true)
                sClickListener?.onDeleteClick(adapterPosition, product.id)
            }
            itemView.ic_edit.setOnClickListener {
                itemView.swipeLayout.close(true)
                sClickListener?.onEditClick(position = adapterPosition)

            }
            itemView.btn_accept.setOnClickListener {
                sClickListener?.onAcceptOrRejectClicked(
                    position = adapterPosition, btn = it as Button,
                    id = product.id
                )
            }
            itemView.btn_chat.setOnClickListener {
                sClickListener?.goToChatScreen(product)
            }
        }
    }

    fun changeReqStatus(position: Int, flag: Boolean) {
        list[position].requestStatus = flag
        notifyItemChanged(position)
    }

    fun setData(produclist: ArrayList<ProductBean>) {
        Log.d("vAlues ", "adapter called" + list.size)
        this.list = produclist
        notifyDataSetChanged()
    }

    internal interface UpdateDataClickListener {
        fun onDeleteClick(position: Int, id: String?)
        fun onEditClick(position: Int)
        fun onAcceptOrRejectClicked(position: Int, btn: Button, id: String?)
        fun goToChatScreen(product: ProductBean)

    }
}