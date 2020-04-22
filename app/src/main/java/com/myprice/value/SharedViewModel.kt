package com.myprice.value

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myprice.value.ui.myrequests.model.ProductBean

class SharedViewModel : ViewModel() {
    lateinit var lat: MutableLiveData<Double>
    lateinit var lng: MutableLiveData<Double>

    val product = MutableLiveData<ProductBean>()

    fun setProductHere(productBean: ProductBean) {
        product.value = productBean
    }

    fun getProductToHere(): LiveData<ProductBean> {
        return product
    }

    fun setLastLocationHere(lati: Double, lngi: Double) {
        lat = MutableLiveData()
        lng = MutableLiveData()
        lat.value = lati
        lng.value = lngi
    }

    fun getLastLocationHere_lat(): LiveData<Double> {
        return this.lat
    }

    fun getLastLocationHere_lng(): LiveData<Double> {
        return this.lng
    }
}