package com.myprice.value

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myprice.value.ui.myrequests.model.ProductBean

class SharedViewModel : ViewModel() {

    val product = MutableLiveData<ProductBean>()

    fun setProductHere(productBean: ProductBean) {
        product.value = productBean
    }

    fun getProductToHere(): LiveData<ProductBean> {
        return product
    }
}